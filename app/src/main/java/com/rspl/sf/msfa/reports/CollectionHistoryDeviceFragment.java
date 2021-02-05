package com.rspl.sf.msfa.reports;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.CollectionHisDeviceListAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.store.OnlineManager;
import com.rspl.sf.msfa.store.OnlineODataStoreException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionHistoryDeviceFragment extends Fragment implements  UIListener {
    private ArrayList<CollectionHistoryBean> alCollectionBean;
    private String mStrBundleRetID = "";
    private String mStrBundleRetUID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    TextView tvEmptyLayDevice = null;
    ListView lv_coll_his_list = null;
    public String[] tempCollDevList = null;
    private CollectionHisDeviceListAdapter collectionHisListAdapter = null;
    int pendingCollVal = 0, penReqCount = 0;
    ArrayList<String> alAssignColl = new ArrayList<>();
    String concatCollectionStr = "";
    private ProgressDialog syncProgDialog;
    String mStrPopUpText = "";

    Hashtable dbHeadTable;
    ArrayList<HashMap<String, String>> arrtable;

    View myInflatedView = null;
    private Bundle bundle;

    public CollectionHistoryDeviceFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bundle =savedInstanceState;
        // Inflate the layout for this fragment
        mStrBundleRetID = getArguments().getString(Constants.CPNo);
        mStrBundleCPGUID = getArguments().getString(Constants.CPGUID);
        mStrBundleRetUID = getArguments().getString(Constants.CPUID);
        mStrBundleRetName = getArguments().getString(Constants.RetailerName);
        myInflatedView = inflater.inflate(R.layout.fragment_collection_history_device, container, false);

        initUI();
        // Inflate the layout for this fragment
        return myInflatedView;
    }

    void initUI() {
        lv_coll_his_list = (ListView) myInflatedView.findViewById(R.id.lv_coll_list);
        tvEmptyLayDevice = (TextView) myInflatedView.findViewById(R.id.tv_empty_layone);

        getCollectionList();

        EditText edNameSearch = (EditText) myInflatedView.findViewById(R.id.ed_collection_search);
        edNameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                collectionHisListAdapter.getFilter().filter(cs); //Filter from my adapter
                collectionHisListAdapter.notifyDataSetChanged(); //Update my view
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
            }
        });
    }

    /*Get CollectionList from device(DataVault)*/
    private void getCollectionList() {

        try {
            alCollectionBean = OfflineManager.getDevCollHisList(getActivity(), mStrBundleRetID);
            pendingCollVal = 0;
            if (tempCollDevList != null) {
                tempCollDevList = null;
                penReqCount = 0;
            }

            if (alCollectionBean != null && alCollectionBean.size() > 0) {
                tempCollDevList = new String[alCollectionBean.size()];
                for (int k = 0; k < alCollectionBean.size(); k++) {
                    tempCollDevList[k] = alCollectionBean.get(k).getDeviceNo();
                    pendingCollVal++;
                }
            }else{

                tvEmptyLayDevice.setVisibility(View.VISIBLE);

            }

            this.collectionHisListAdapter = new CollectionHisDeviceListAdapter(getActivity(), alCollectionBean,mStrBundleCPGUID,mStrBundleRetID,mStrBundleRetName, tvEmptyLayDevice);
            lv_coll_his_list.setEmptyView(getActivity().findViewById(R.id.tv_empty_lay) );
            lv_coll_his_list.setAdapter(this.collectionHisListAdapter);
            this.collectionHisListAdapter.notifyDataSetChanged();

            if(alCollectionBean.size()>0){
                tvEmptyLayDevice.setVisibility(View.GONE);
            } else
                tvEmptyLayDevice.setVisibility(View.VISIBLE);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }




    @Override
    public void onRequestError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e,getActivity());
        if (errorBean.hasNoError()) {
        penReqCount++;
        if ((operation == Operation.Create.getValue()) && (penReqCount == pendingCollVal)) {
            LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
        }

                if (operation == Operation.OfflineFlush.getValue()) {
                    try {
                        OfflineManager.refreshRequests(getActivity(), Constants.Visits, CollectionHistoryDeviceFragment.this);
                    } catch (OfflineODataStoreException e1) {
                e1.printStackTrace();
            }
        } else if (operation == Operation.OfflineRefresh.getValue()) {
            LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
            try {
               /* String syncTime = Constants.getSyncHistoryddmmyyyyTime();
                String[] DEFINGREQARRAY = Constants.getDefinigReq(getActivity());
                for (int incReq = 0; incReq < DEFINGREQARRAY.length; incReq++) {

                    String colName = DEFINGREQARRAY[incReq];
                    if (colName.contains("?$")) {
                        String splitCollName[] = colName.split("\\?");
                        colName = splitCollName[0];
                    }

                    Constants.events.updateStatus(Constants.SYNC_TABLE,
                            colName, Constants.TimeStamp, syncTime
                    );
                }*/
             //   Constants.updateSyncTime(alAssignColl,getActivity(),Constants.DownLoad,refguid.toString().toUpperCase());
            } catch (Exception exce) {
                LogManager.writeLogError(Constants.SyncTableHistory + exce.getMessage());
            }
            Constants.isSync = false;
            syncProgDialog.dismiss();
            UtilConstants.showAlert(getString(R.string.msg_error_occured_during_sync), getActivity());

        }
        }else{
            Constants.isSync = false;
            syncProgDialog.dismiss();
            Constants.displayMsgReqError(errorBean.getErrorCode(),getActivity());
        }
    }

    @Override
    public void onRequestSuccess(int operation, String key) {
        if (operation == Operation.Create.getValue() && pendingCollVal > 0) {

            Set<String> set = new HashSet<>();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
            set = sharedPreferences.getStringSet(Constants.CollList, null);

            HashSet<String> setTemp = new HashSet<>();
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
                while (itr.hasNext()) {
                    setTemp.add(itr.next().toString());
                }
            }

            setTemp.remove(tempCollDevList[penReqCount]);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(Constants.CollList, setTemp);
            editor.commit();

            try {
                ConstantsUtils.storeInDataVault(tempCollDevList[penReqCount], "",getActivity());
            } catch (Throwable e) {
                e.printStackTrace();
            }

            penReqCount++;
        }
        if ((operation == Operation.Create.getValue()) && (penReqCount == pendingCollVal)) {

            try {
                for (int incVal = 0; incVal < alAssignColl.size(); incVal++) {
                    if (incVal == 0 && incVal == alAssignColl.size() - 1) {
                        concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
                    } else if (incVal == 0) {
                        concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
                    } else if (incVal == alAssignColl.size() - 1) {
                        concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
                    } else {
                        concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
                    }
                }
                OfflineManager.refreshRequests(getActivity(), concatCollectionStr, CollectionHistoryDeviceFragment.this);
            } catch (OfflineODataStoreException e) {
                TraceLog.e(Constants.SyncOnRequestSuccess, e);
            }


        } else if (operation == Operation.OfflineFlush.getValue()) {

            try {
                for (int incVal = 0; incVal < alAssignColl.size(); incVal++) {
                    if (incVal == 0 && incVal == alAssignColl.size() - 1) {
                        concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
                    } else if (incVal == 0) {
                        concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
                    } else if (incVal == alAssignColl.size() - 1) {
                        concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
                    } else {
                        concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
                    }
                }
                OfflineManager.refreshRequests(getActivity(), concatCollectionStr, CollectionHistoryDeviceFragment.this);

            } catch (OfflineODataStoreException e) {
                TraceLog.e(Constants.SyncOnRequestSuccess, e);
            }
        } else if (operation == Operation.OfflineRefresh.getValue()) {
            try {
                /*String syncTime = Constants.getSyncHistoryddmmyyyyTime();
                for (int incReq = 0; incReq < alAssignColl.size(); incReq++) {
                    String colName = alAssignColl.get(incReq);
                    if (colName.contains("?$")) {
                        String splitCollName[] = colName.split("\\?");
                        colName = splitCollName[0];
                    }
                    Constants.events.updateStatus(Constants.SYNC_TABLE,
                            colName, Constants.TimeStamp, syncTime
                    );
                }*/
              //  Constants.updateSyncTime(alAssignColl,getActivity(),Constants.DownLoad);
            } catch (Exception exce) {
                LogManager.writeLogError(Constants.SyncTableHistory + exce.getMessage());
            }


//                popUpText = "Collection created";
            try {
                syncProgDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }



            AlertDialog.Builder builder = new AlertDialog.Builder(
                    getActivity(), R.style.MyTheme);
            builder.setMessage(getString(R.string.msg_sync_successfully_completed))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    CollectionHistoryActivity.updateListener.onUpdate();
                                }
                            });

            builder.show();

        } else {
            try {
                syncProgDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*post device collections
    public void postDeviceCollections() {
        mStrPopUpText = Constants.SubmittingDeviceCollectionsPleaseWait;
        try {
            new PostingDataVaultData().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*AsyncTask to post device collections*/
    public class PostingDataVaultData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(mStrPopUpText);
            syncProgDialog.setCancelable(false);
            syncProgDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(2000);

                OnlineManager.openOnlineStore(getActivity(),true);

                if (pendingCollVal > 0) {
                    for (int k = 0; k < tempCollDevList.length; k++) {
                        String store = null;
                        try {
                            store = ConstantsUtils.getFromDataVault(tempCollDevList[k].toString(),getActivity());
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                        //Fetch object from data vault
                        try {

                            JSONObject fetchJsonHeaderObject = new JSONObject(store);
                            dbHeadTable = new Hashtable();
                            arrtable = new ArrayList<>();
                            if (fetchJsonHeaderObject.getString(Constants.EntityType).equalsIgnoreCase(Constants.Collection)) {
                                if(!alAssignColl.contains(Constants.SFINVOICES)){
                                    alAssignColl.add(Constants.SSInvoiceItemDetails);
                                    alAssignColl.add(Constants.SFINVOICES);
                                }
                                if (!alAssignColl.contains(Constants.FinancialPostings)) {
                                    alAssignColl.add(Constants.FinancialPostings);
                                    alAssignColl.add(Constants.FinancialPostingItemDetails);
                                }

                                if(!alAssignColl.contains(Constants.OutstandingInvoices)){
                                    alAssignColl.add(Constants.OutstandingInvoiceItemDetails);
                                    alAssignColl.add(Constants.OutstandingInvoices);
                                }
                                dbHeadTable = Constants.getCollHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                String itemsString = fetchJsonHeaderObject.getString(Constants.ItemsText);

                                arrtable = UtilConstants.convertToArrayListMap(itemsString);

                                try {
                                    OnlineManager.createCollectionEntry(dbHeadTable, arrtable, CollectionHistoryDeviceFragment.this);

                                } catch (OnlineODataStoreException e) {
                                    e.printStackTrace();
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            syncProgDialog.dismiss();
        }
    }
}
