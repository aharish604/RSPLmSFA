package com.rspl.sf.msfa.claimreports;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.AsyncTaskCallBack;
import com.rspl.sf.msfa.soapproval.OpenOnlineManagerStore;
import com.rspl.sf.msfa.store.GetOnlineODataInterface;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.store.OnlineManager;
import com.rspl.sf.msfa.store.OnlineStoreListener;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClaimReportPresentImpl implements ClaimReportPresenterView, GetOnlineODataInterface {
    private Context context;
    private ClaimReportView summaryView;
    private ArrayList<ClaimReportBean> attendanceSummaryDone = new ArrayList<>();
    private ArrayList<ClaimReportBean> attendanceSummaryNotDone = new ArrayList<>();
    private ArrayList<ClaimReportBean> attendanceSummarySearchList = new ArrayList<>();
    private ArrayList<ClaimReportBean> attFinalList = new ArrayList<>();
    private String searchText="";

    public ClaimReportPresentImpl(Context context, ClaimReportView summaryView) {
        this.context = context;
        this.summaryView = summaryView;
    }

    @Override
    public void onStart() {
        if(summaryView!=null){
            summaryView.showProgressDialog();
        }
        attendanceSummaryDone.clear();
        attendanceSummaryNotDone.clear();
        attFinalList.clear();
        if(UtilConstants.isNetworkAvailable(context)){
            new AsyncTaskClaimReport().execute();
        }else {
            if(summaryView!=null) {
                summaryView.hideProgressDialog();
                summaryView.displayList(attFinalList,totalClaimAmount,totalMaxClaimAmount);
                summaryView.showMessage(context.getString(R.string.err_no_network));
            }
        }

    }

    @Override
    public void onItemClick(ClaimReportBean claimReportBean) {
        try {
            Intent intent = new Intent(context,ClaimReportDetailsActivity.class);
            intent.putExtra(ConstantsUtils.CLAIM_SUMMARY,claimReportBean);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSearch(String searchText) {
        this.searchText=searchText;
        /*attendanceSummarySearchList.clear();
        if (attFinalList != null) {
            if (TextUtils.isEmpty(searchText)) {
                attendanceSummarySearchList.addAll(attFinalList);
            } else {
                for (ClaimReportBean item : attFinalList) {
                    if (item.getCreatedBy().toLowerCase().contains(searchText.toLowerCase()) || item.getSPName().toLowerCase().contains(searchText.toLowerCase())) {
                        attendanceSummarySearchList.add(item);
                    }
                }
            }
        }*/
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (summaryView != null) {
                    summaryView.displayList(attendanceSummarySearchList,totalClaimAmount,totalMaxClaimAmount);
                }
            }
        });

    }

    private String totalClaimAmount = "0.00";
    private String totalMaxClaimAmount = "0.00";
    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> entities, int operation, int requestCode, String resourcePath, Bundle bundle) {
        attFinalList.clear();
        attFinalList.addAll(OnlineManager.getClaimSumaryList(entities));
        Collections.sort(attFinalList, new Comparator<ClaimReportBean>() {
            @Override
            public int compare(ClaimReportBean o1, ClaimReportBean o2) {
                return o1.getParentName().compareTo(o2.getParentName());
            }
        });
        double totalClaim = 0.0;
        double totalMaxClaim = 0.0;
        for (ClaimReportBean claimReportBean : attFinalList){
            if(!TextUtils.isEmpty(claimReportBean.getTotalClaimAmount())){
                totalClaim = totalClaim +Double.parseDouble(claimReportBean.getTotalClaimAmount());
            }else {
                totalClaim = totalClaim +0.0;
            }

            if(!TextUtils.isEmpty(claimReportBean.getTotalMaxClaimAmt())){
                totalMaxClaim = totalMaxClaim +Double.parseDouble(claimReportBean.getTotalMaxClaimAmt());
            }else {
                totalMaxClaim = totalMaxClaim +0.0;
            }
        }
        totalClaimAmount = String.valueOf(totalClaim);
        totalMaxClaimAmount = String.valueOf(totalMaxClaim);
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (summaryView!=null){
                    summaryView.hideProgressDialog();
                    summaryView.displayList(attFinalList,totalClaimAmount,totalMaxClaimAmount);
                }
            }
        });
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, int operation, int requestCode, String resourcePath, final String errorMsg, Bundle bundle) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (summaryView!=null){
                    summaryView.hideProgressDialog();
                    summaryView.showMessage(errorMsg);
                    summaryView.displayList(attFinalList,totalClaimAmount,totalMaxClaimAmount);
                }
            }
        });
    }
    Bundle requestbundle = null;
    // get Attendance list report for current date
    private class AsyncTaskClaimReport extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
//                OnlineStoreListener openListener = OnlineStoreListener.getInstance();
                /* OnlineODataStore store = openListener.getStore();*/
                Log.d("ApprovalList","Opening Store");
                final String qry = Constants.Claims +"?$filter=ClaimTypeID%20eq%20'07'%20and%20(ApprovalStatusID%20eq%20'01'%20or%20ApprovalStatusID%20eq%20'02')%20&$select=ClaimAmount,ZMaxClaimAmt,ZSchemeTypeDesc,ZSchemeType,ZSchemeValidTo,ZSchemeValidFrm,ParentNo,ParentName";

                try {

                    OnlineManager.doOnlineHeaderAttRequest(qry, context, iReceiveEvent -> {
                        if (iReceiveEvent.getResponseStatusCode()==200){
                            JSONObject jsonObject = OnlineManager.getJSONBody(iReceiveEvent);
                            JSONArray jsonArray = OnlineManager.getJSONArrayBody(jsonObject);
                            attFinalList.clear();
                            attFinalList.addAll(OnlineManager.getClaimSumaryList(jsonArray));
                            Collections.sort(attFinalList, new Comparator<ClaimReportBean>() {
                                @Override
                                public int compare(ClaimReportBean o1, ClaimReportBean o2) {
                                    return o1.getParentName().compareTo(o2.getParentName());
                                }
                            });
                            double totalClaim = 0.0;
                            double totalMaxClaim = 0.0;
                            for (ClaimReportBean claimReportBean : attFinalList){
                                if(!TextUtils.isEmpty(claimReportBean.getTotalClaimAmount())){
                                    totalClaim = totalClaim +Double.parseDouble(claimReportBean.getTotalClaimAmount());
                                }else {
                                    totalClaim = totalClaim +0.0;
                                }

                                if(!TextUtils.isEmpty(claimReportBean.getTotalMaxClaimAmt())){
                                    totalMaxClaim = totalMaxClaim +Double.parseDouble(claimReportBean.getTotalMaxClaimAmt());
                                }else {
                                    totalMaxClaim = totalMaxClaim +0.0;
                                }
                            }
                            totalClaimAmount = String.valueOf(totalClaim);
                            totalMaxClaimAmount = String.valueOf(totalMaxClaim);
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (summaryView!=null){
                                        summaryView.hideProgressDialog();
                                        summaryView.displayList(attFinalList,totalClaimAmount,totalMaxClaimAmount);
                                    }
                                }
                            });
//
                        }else {
                            String errorMsg="";
                            try {
                                errorMsg = Constants.getErrorMessage(iReceiveEvent,context);
                                String finalErrorMsg = errorMsg;
                                ((Activity)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (summaryView!=null){
                                            summaryView.hideProgressDialog();
                                            summaryView.showMessage(finalErrorMsg);
                                            summaryView.displayList(attFinalList,totalClaimAmount,totalMaxClaimAmount);
                                        }
                                    }
                                });
                                LogManager.writeLogError(errorMsg);
                            } catch (Throwable e) {
                                e.printStackTrace();
                                ((Activity)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (summaryView!=null){
                                            summaryView.hideProgressDialog();
                                            summaryView.showMessage(e.getMessage());
                                            summaryView.displayList(attFinalList,totalClaimAmount,totalMaxClaimAmount);
                                        }
                                    }
                                });
                                LogManager.writeLogError(e.getMessage());
                            }
                        }
                    }, e -> {
                        e.printStackTrace();
                        String errormessage = "";
                        errormessage = ConstantsUtils.geterrormessageForInternetlost(e.getMessage(),context);
                        if(TextUtils.isEmpty(errormessage)){
                            errormessage = e.getMessage();
                        }
                        String finalErrormessage = errormessage;
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (summaryView!=null){
                                    summaryView.hideProgressDialog();
                                    summaryView.showMessage(finalErrormessage);
                                    summaryView.displayList(attFinalList,totalClaimAmount,totalMaxClaimAmount);
                                }
                            }
                        });

                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    ConstantsUtils.printErrorLog(e.getMessage());
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (summaryView!=null){
                                summaryView.hideProgressDialog();
                                summaryView.showMessage(e.getMessage());
                                summaryView.displayList(attFinalList,totalClaimAmount,totalMaxClaimAmount);
                            }
                        }
                    });

                }
            } catch (Exception e) {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (summaryView!=null){
                            summaryView.hideProgressDialog();
                            summaryView.showMessage(e.toString());
                        }
                    }
                });

                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
