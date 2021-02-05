package com.rspl.sf.msfa.attendance.attendancesummary;

import android.app.Activity;
import android.content.Context;
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
import com.rspl.sf.msfa.soapproval.SOApproveActivity;
import com.rspl.sf.msfa.solist.SOListBean;
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

public class AttendanceSummaryPresentImpl implements AttendanceSummaryPresenterView, GetOnlineODataInterface {
    private Context context;
    private AttendanceSummaryView summaryView;
    private ArrayList<AttendanceSummaryBean> attendanceSummaryDone = new ArrayList<>();
    private ArrayList<AttendanceSummaryBean> attendanceSummaryNotDone = new ArrayList<>();
    private ArrayList<AttendanceSummaryBean> attendanceSummarySearchList = new ArrayList<>();
    private ArrayList<AttendanceSummaryBean> attFinalList = new ArrayList<>();
    private String searchText="";

    public AttendanceSummaryPresentImpl(Context context, AttendanceSummaryView summaryView) {
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
            new AsyncTaskAttendanceSummary().execute();
        }else {
            if(summaryView!=null) {
                summaryView.hideProgressDialog();
                summaryView.displayList(attFinalList);
                summaryView.showMessage(context.getString(R.string.err_no_network));
            }
        }

    }

    public void onSearch(String searchText) {
        this.searchText=searchText;
        attendanceSummarySearchList.clear();
        if (attFinalList != null) {
            if (TextUtils.isEmpty(searchText)) {
                attendanceSummarySearchList.addAll(attFinalList);
            } else {
                for (AttendanceSummaryBean item : attFinalList) {
                    if (item.getCreatedBy().toLowerCase().contains(searchText.toLowerCase()) || item.getSPName().toLowerCase().contains(searchText.toLowerCase())) {
                        attendanceSummarySearchList.add(item);
                    }
                }
            }
        }
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (summaryView != null) {
                    summaryView.displayList(attendanceSummarySearchList);
                }
            }
        });

    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> entities, int operation, int requestCode, String resourcePath, Bundle bundle) {
        attendanceSummaryDone.clear();
        attFinalList.clear();
        attendanceSummaryDone.addAll(OnlineManager.getAttendanceSummaryList(entities));
        ArrayList<String> strRSCodeList = new ArrayList<>();
        for(AttendanceSummaryBean summaryBean : attendanceSummaryDone){
            strRSCodeList.add(summaryBean.getCreatedBy());
        }
        for(AttendanceSummaryBean summaryBean : attendanceSummaryNotDone){
            if(!strRSCodeList.contains(summaryBean.getCreatedBy())){
                attFinalList.add(summaryBean);
            }
        }
        attFinalList.addAll(attendanceSummaryDone);
        Collections.sort(attFinalList, new Comparator<AttendanceSummaryBean>() {
            @Override
            public int compare(AttendanceSummaryBean o1, AttendanceSummaryBean o2) {
                return o1.getSPName().compareTo(o2.getSPName());
            }
        });
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (summaryView!=null){
                    summaryView.hideProgressDialog();
                    summaryView.displayList(attFinalList);
                }
            }
        });
    }

    private void refreshErrorUI(String errorMsg){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (summaryView!=null){
                    summaryView.hideProgressDialog();
                    summaryView.showMessage(errorMsg);
                    summaryView.displayList(attFinalList);
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
                    summaryView.displayList(attFinalList);
                }
            }
        });
    }
    Bundle requestbundle = null;
    // get Attendance list report for current date
    private class AsyncTaskAttendanceSummary extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //get all rs code from CustomerPartnerFunctions
            String loginId = "";
            loginId = OfflineManager.getLoginID("UserProfileAuthSet?$filter=Application%20eq%20%27PD%27");
            String qryPartnerFunctions = Constants.CustomerPartnerFunctions +"?$select=PartnerVendorNo,PartnerVendorName"+ " &$filter=PartnerVendorNo ne 'DEF0001' and PartnerVendorNo ne '"+loginId+"' and ("+Constants.PartnerFunctionID + " eq 'TS' or "+ Constants.PartnerFunctionID + " eq 'TO')";

            try {
                attendanceSummaryNotDone.addAll(OfflineManager.getCustomerPartnerFunctionList(qryPartnerFunctions));
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            //get Attendance list from online call
            try {
                Log.d("ApprovalList","Opening Store");
                final String qry = Constants.Attendances +"?$select=SPName,CreatedBy,StartDate,EndDate,EndTime,StartTime" +"&$filter=StartDate%20eq%20datetime'" + UtilConstants.getNewDate() + "'%20and%20"+ Constants.SPGUID +"%20ne%20guid'"+Constants.getSPGUID(Constants.SPGUID).toUpperCase()+"'";

                try {

                    OnlineManager.doOnlineHeaderAttRequest(qry, context, iReceiveEvent -> {
                        if (iReceiveEvent.getResponseStatusCode()==200){
                            JSONObject jsonObject = OnlineManager.getJSONBody(iReceiveEvent);
                            JSONArray jsonArray = OnlineManager.getJSONArrayBody(jsonObject);
                            attendanceSummaryDone.clear();
                            attFinalList.clear();
                            attendanceSummaryDone.addAll(OnlineManager.getAttendanceSummaryList(jsonArray));
                            ArrayList<String> strRSCodeList = new ArrayList<>();
                            for(AttendanceSummaryBean summaryBean : attendanceSummaryDone){
                                strRSCodeList.add(summaryBean.getCreatedBy());
                            }
                            for(AttendanceSummaryBean summaryBean : attendanceSummaryNotDone){
                                if(!strRSCodeList.contains(summaryBean.getCreatedBy())){
                                    attFinalList.add(summaryBean);
                                }
                            }
                            attFinalList.addAll(attendanceSummaryDone);
                            Collections.sort(attFinalList, new Comparator<AttendanceSummaryBean>() {
                                @Override
                                public int compare(AttendanceSummaryBean o1, AttendanceSummaryBean o2) {
                                    return o1.getSPName().compareTo(o2.getSPName());
                                }
                            });
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (summaryView!=null){
                                        summaryView.hideProgressDialog();
                                        summaryView.displayList(attFinalList);
                                    }
                                }
                            });
                        }else {
                            String errorMsg="";
                            try {
                                errorMsg = Constants.getErrorMessage(iReceiveEvent,context);
                                refreshErrorUI(errorMsg);
                                LogManager.writeLogError(errorMsg);
                            } catch (Throwable e) {
                                e.printStackTrace();
                                refreshErrorUI(e.getMessage());
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
                                    summaryView.displayList(attFinalList);
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
                                summaryView.displayList(attFinalList);
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
                            summaryView.showMessage(e.getMessage());
                            summaryView.displayList(attFinalList);
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
