package com.rspl.sf.msfa.visit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.MyUtils;
import com.rspl.sf.msfa.mbo.MerchandisingBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.store.OnlineManager;
import com.rspl.sf.msfa.store.OnlineODataStoreException;

import java.util.ArrayList;

/**
 * Created by e10763 on 12/19/2016.
 *
 */

public class MerchandisingListFragment extends Fragment {

    private String mStrBundleRetID = "";
    private String mStrBundleRetUID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";

    boolean flag = true;
    ScrollView scrollView;
    TableLayout tableLayout;
    View myInflatedView = null;
    private ArrayList<MerchandisingBean> alMercBean;
    private ProgressDialog pdLoadDialog;

    private String mStrMerReviewGuid = "", mStrMerItemGuid = "", mStrMerDocStore = "", mStrPopUpText = "";
    MerchandisingBean mtempBean;

    public MerchandisingListFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mStrBundleRetID = getArguments().getString(Constants.CPNo);
        mStrBundleCPGUID = getArguments().getString(Constants.CPGUID);
        mStrBundleRetUID = getArguments().getString(Constants.CPUID);
        mStrBundleRetName = getArguments().getString(Constants.RetailerName);
        // Inflate the layout for this fragment
        myInflatedView = inflater.inflate(R.layout.fragment_merch_list, container, false);

        initUI();
        return myInflatedView;
    }

    /*InitializesUI*/
    void initUI() {
        getMerchndisingList();
    }


    // TODO get merchandising list from data base

    private void getMerchndisingList() {
        try {


            alMercBean = OfflineManager.getMerchandisingList(Constants.MerchReviews + "?$filter= " + Constants.CPGUID + " eq '"
                    + mStrBundleRetID + "' and not sap.islocal() &$orderby=" + Constants.MerchReviewDate + "%20desc",Constants.NonDeviceMechindising,getActivity());

            displyMerivews();


        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    //TODO Display Merchandising values
    private void displyMerivews() {
        scrollView = (ScrollView) myInflatedView.findViewById(R.id.scroll_snap_shot_list);
        if (!flag) {
            scrollView.removeAllViews();
        }

        flag = false;

        tableLayout = (TableLayout) LayoutInflater.from(getActivity())
                .inflate(R.layout.item_table, null);
        if (!alMercBean.isEmpty()
                && alMercBean.size() > 0) {
            LinearLayout llTargetTable = null;

            for (int i = 0; i < alMercBean.size(); i++) {

                final MerchandisingBean merchandisingBean = alMercBean.get(i);
                llTargetTable = (LinearLayout) LayoutInflater.from(getActivity())
                        .inflate(R.layout.merchandising_list_item, null);

                ((TextView) llTargetTable.findViewById(R.id.tvDateValue))
                        .setText(UtilConstants.convertDateIntoDeviceFormat(getContext(), alMercBean.get(i).getMerchReviewDate()));

                ((TextView) llTargetTable.findViewById(R.id.tvSnapTypeValue))
                        .setText(alMercBean.get(i).getMerchReviewTypeDesc());

                ImageView imv = (ImageView) llTargetTable
                        .findViewById(R.id.imgImageValue);
                imv.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {


                        if (merchandisingBean.getEtag().equalsIgnoreCase("")) {
                            if (UtilConstants.isNetworkAvailable(getActivity())) {
                                mStrMerReviewGuid = merchandisingBean.getMerchReviewGUID();
                                mStrMerItemGuid = merchandisingBean.getMerchReviewImgGUID();
                                mStrMerDocStore = merchandisingBean.getDocumentStore();
                                mtempBean = merchandisingBean;
                                onLoadDialog();
                            } else {
                                UtilConstants.onNoNetwork(getActivity());
                            }

                        } else {
                            Intent toSnapdetails = new Intent(getActivity(), MerchandisingDetailsActivity.class);
                            toSnapdetails.putExtra(Constants.CPNo, mStrBundleRetID);
                            toSnapdetails.putExtra(Constants.CPUID, mStrBundleRetUID);
                            toSnapdetails.putExtra(Constants.RetailerName, mStrBundleRetName);
                            toSnapdetails.putExtra(Constants.MerchReviewGUID, merchandisingBean.getMerchReviewGUID());
                            toSnapdetails.putExtra(Constants.MerchReviewTypeDesc, merchandisingBean.getMerchReviewTypeDesc());
                            toSnapdetails.putExtra(Constants.Remarks, merchandisingBean.getRemarks());
                            toSnapdetails.putExtra(Constants.Etag, merchandisingBean.getEtag());
                            toSnapdetails.putExtra(Constants.CPGUID, mStrBundleCPGUID);
                            toSnapdetails.putExtra(Constants.SetResourcePath, merchandisingBean.getResourcePath());
                            toSnapdetails.putExtra(Constants.Image, "");
                            startActivity(toSnapdetails);
                        }


                    }

                    ;
                });

                LinearLayout llLine = new LinearLayout(getActivity());
                llLine.setBackgroundColor(Color.parseColor("#000000"));
                llLine.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 1));
                tableLayout.addView(llLine);

                tableLayout.addView(llTargetTable);
            }

            LinearLayout llLine = new LinearLayout(getActivity());
            llLine.setBackgroundColor(Color.parseColor("#000000"));
            llLine.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 1));
            tableLayout.addView(llLine);

        } else {

            LinearLayout llEmptyLayout = (LinearLayout) LayoutInflater.from(getActivity())
                    .inflate(R.layout.empty_layout, null);

            tableLayout.addView(llEmptyLayout);

        }
        scrollView.addView(tableLayout);
        scrollView.requestLayout();
    }

    String mStrImage = "";
    byte imageByteArray[] = null;

    private void onLoadDialog() {
        mStrPopUpText = getString(R.string.prg_dialog_txt_retrive_merch_img);
        try {
            new GetMerReviewImgAsyncTask().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class GetMerReviewImgAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(mStrPopUpText);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                try {

                    OnlineManager.openOnlineStore(getActivity(), false);

                    /*LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();
                    String endPointURL = null;
                    try {
                        endPointURL = lgCtx.getAppEndPointUrl();
                    } catch (LogonCoreException e) {
                        e.printStackTrace();
                    }*/
                    String endPointURL = MyUtils.getDefaultEndPointURL();
                    imageByteArray = OnlineManager.getMerchindisingImage(endPointURL + "/" + Constants.MerchReviewImages + "(" + Constants.MerchImageGUID + "='" + mStrMerItemGuid + "',"
                            + Constants.MerchReviewGUID + "=guid'" + mStrMerReviewGuid.toUpperCase() + "'," + Constants.DocumentStore + "='" + mStrMerDocStore + "')/$value");
                } catch (OnlineODataStoreException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            pdLoadDialog.dismiss();
            if (imageByteArray != null) {
                Intent toSnapdetails = new Intent(getActivity(), MerchandisingDetailsActivity.class);
                toSnapdetails.putExtra(Constants.CPNo, mStrBundleRetID);
                toSnapdetails.putExtra(Constants.CPUID, mStrBundleRetUID);
                toSnapdetails.putExtra(Constants.RetailerName, mStrBundleRetName);
                toSnapdetails.putExtra(Constants.MerchReviewGUID, mtempBean.getMerchReviewGUID());
                toSnapdetails.putExtra(Constants.MerchReviewTypeDesc, mtempBean.getMerchReviewTypeDesc());
                toSnapdetails.putExtra(Constants.Remarks, mtempBean.getRemarks());
                toSnapdetails.putExtra(Constants.Etag, mtempBean.getEtag());
                toSnapdetails.putExtra(Constants.CPGUID, mStrBundleCPGUID);
                toSnapdetails.putExtra(Constants.SetResourcePath, mtempBean.getResourcePath());
                toSnapdetails.putExtra(Constants.Image, mStrImage);
                toSnapdetails.putExtra(Constants.ImageByteArray,imageByteArray);
                startActivity(toSnapdetails);
            } else {
                UtilConstants.showAlert(getString(R.string.error_occured_during_get_image), getActivity());
            }

        }
    }
}
