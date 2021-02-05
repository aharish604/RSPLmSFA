package com.rspl.sf.msfa.grreport;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.rspl.sf.msfa.BuildConfig;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.asyncTask.RefreshAsyncTask;
import com.rspl.sf.msfa.attendance.attendancesummary.AttendanceSummaryBean;
import com.rspl.sf.msfa.attendance.attendancesummary.AttendanceSummaryPresentImpl;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.filter.DateFilterFragment;
import com.rspl.sf.msfa.interfaces.AsyncTaskCallBack;
import com.rspl.sf.msfa.interfaces.MessageWithBooleanCallBack;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.soapproval.OpenOnlineManagerStore;
import com.rspl.sf.msfa.store.GetOnlineODataInterface;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.store.OnlineManager;
import com.rspl.sf.msfa.store.OnlineStoreListener;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.httpc.events.IReceiveEvent;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GRReportListPresenter implements IGRReportPresenterImpt, UIListener, MessageWithBooleanCallBack {
    private Activity activity;
    private  IGRReportView IGRReportView;
    private Context context;
    private GUID refguid =null;
    private String cpguid;
    private String customerNumber;
    private String mStrBundleCPGUID = "";
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";
    private String comingFrom = "";
    private String concatFlushCollStr = "";
    private String syncType = "";
    private String HeaderName = "";
    private boolean isErrorFromBackend = false;
    private ArrayList<String> alAssignColl = new ArrayList<>();
    private ArrayList<String> alFlushColl = new ArrayList<>();
    private int pendingROVal = 0;
    private String[] tempRODevList = null;
    private int penROReqCount = 0;
    private ArrayList<GRReportBean> alSalesPromotion = new ArrayList<>();

    public GRReportListPresenter(Activity activity, Context context, IGRReportView IGRReportView, Bundle bundleExtras) {
        this.activity = activity;
        this.IGRReportView= IGRReportView;
        this.context= context;
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
//            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
//            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
//            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
//            comingFrom = bundleExtras.getString(Constants.comingFrom);
//            syncType = bundleExtras.getString(Constants.SyncType);
//            HeaderName = bundleExtras.getString(Constants.HeaderName);
        }

    }


    @Override
    public void connectToOfflineDB() {
        if (UtilConstants.isNetworkAvailable(context.getApplicationContext())) {
            new GRDataListAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    IGRReportView.dialogMessage(context.getString(R.string.err_no_network));
//                            UtilConstants.showAlert(context.getString(R.string.data_conn_lost_during_sync), context);
                }
            });
        }
    }

    @Override
    public void onStart() {
        connectToOfflineDB();
    }

    @Override
    public void onUploadData() {

    }

    private String endDate = "";
    private String companyCodeId = "";
    private String companyCodeName = "";
    private String startDate = "";
    Bundle requestbundle = null;
    private String filterType = "Last One Month";

    @Override
    public void onDestroy() {
        IGRReportView = null;
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onFilter() {
        if (IGRReportView != null) {
            IGRReportView.openFilter(startDate, endDate, filterType, companyCodeId,"");
        }
    }

    @Override
    public void onSearch(String searchText) {
        boolean soSearchStatus=false;
        boolean soSearchinvoice=false;
        ArrayList<GRReportBean> alSalesSearch = new ArrayList<>();
        if (alSalesPromotion != null) {
            if (TextUtils.isEmpty(searchText)) {
                alSalesSearch.addAll(alSalesPromotion);
            } else {
                for (GRReportBean item : alSalesPromotion) {


                    if (!TextUtils.isEmpty(searchText)) {
                        soSearchStatus = item.getZZGRNo().toLowerCase().contains(searchText.toLowerCase());
                        soSearchinvoice = item.getInvoiceNo().toLowerCase().contains(searchText.toLowerCase());
                    } else {
                        soSearchStatus = true;
                        soSearchinvoice = true;
                    }
                    if ( soSearchStatus || soSearchinvoice)
                        alSalesSearch.add(item);
                }
            }
        }
        if (IGRReportView != null) {
            IGRReportView.displayData(alSalesSearch);
        }
    }

    @Override
    public void onRefresh() {
        /*if (IGRReportView != null) {
            IGRReportView.showProgressDialog();
        }
        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(context)) {
            alAssignColl.clear();
            alAssignColl.addAll(SyncUtils.getMaterialDocs());
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);

            if (Constants.iSAutoSync) {
                if (IGRReportView != null) {
                    IGRReportView.hideProgressDialog();
                    IGRReportView.dialogMessage(context.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    SyncUtils.updatingSyncStartTime(context,Constants.DownLoad,Constants.StartSync,null);
                    new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (IGRReportView != null) {
                IGRReportView.hideProgressDialog();
                IGRReportView.dialogMessage(context.getString(R.string.no_network_conn));
            }
        }*/
        onStart();
    }

    @Override
    public void startFilter(int requestCode, int resultCode, Intent data) {
        filterType = data.getStringExtra(DateFilterFragment.EXTRA_DEFAULT);
        startDate = data.getStringExtra(DateFilterFragment.EXTRA_START_DATE);
        endDate = data.getStringExtra(DateFilterFragment.EXTRA_END_DATE);
        companyCodeId = data.getStringExtra(GRFilterActivity.EXTRA_AS_COMPNY_CODE);
        companyCodeName = data.getStringExtra(GRFilterActivity.EXTRA_AS_COMPNY_CODE_NAME);
        displayFilterType();
        onStart();
    }

    @Override
    public void getRefreshTime() {

    }

    @Override
    public void getDetails(String no) {

    }

    @Override
    public void clickedStatus(boolean clickedStatus, String errorMsg, ErrorBean errorBean) {
        if (!clickedStatus) {
            if (IGRReportView != null) {
                IGRReportView.hideProgressDialog();
                IGRReportView.dialogMessage(errorMsg);
            }
        }
    }

    private void displayFilterType() {
        String statusDesc = "";
        String displayFilterType = filterType;
        if (filterType.equalsIgnoreCase(context.getString(R.string.so_filter_manual_selection))) {
            displayFilterType = ConstantsUtils.convertDateIntoDeviceFormat(startDate) + " - " + ConstantsUtils.convertDateIntoDeviceFormat(endDate);
        }
        if (IGRReportView != null) {
            IGRReportView.setFilterDate(displayFilterType);
        }
    }

    private class GRDataListAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (IGRReportView != null) {
                IGRReportView.showProgressDialog();
            }
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                alSalesPromotion.clear();
                if (UtilConstants.isNetworkAvailable(context.getApplicationContext())) {
                    int cusNo=0;
                    if(!TextUtils.isEmpty(mStrBundleRetID)){
                        cusNo = Integer.parseInt(mStrBundleRetID);
                    }
                    String qry="";
                    if(!TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(endDate)){
                        qry = Constants.INVOICES + "?$filter=CustomerNo%20eq%20'" + cusNo + "'%20and%20(InvoiceDate%20ge%20datetime'" + startDate+"'%20and%20InvoiceDate%20le%20datetime'" + endDate+"')%20&$orderby=%20InvoiceDate%20desc,InvoiceNo%20desc";
                    }else {
                        qry = Constants.INVOICES + "?$filter=CustomerNo%20eq%20'" + cusNo + "'%20and%20InvoiceDate%20ge%20datetime'" + Constants.getLastDateToTillDate() + "'%20&$orderby=%20InvoiceDate%20desc,InvoiceNo%20desc";
                    }

                    OnlineManager.doOnlineGetRequest(qry, context, event -> {
                        if (event.getResponseStatusCode() == 200) {
                            String responseBody = IReceiveEvent.Util.getResponseBody(event.getReader());
                            Log.d("OnlineManager", "getUserRollInfo: " + responseBody + " " + event.getResponseStatusCode());
                            JSONObject jsonObj = null;
                            try {
                                jsonObj = new JSONObject(responseBody);
                                JSONObject dObject = jsonObj.getJSONObject("d");
                                JSONArray resultArray = dObject.getJSONArray("results");
                                alSalesPromotion.addAll(OnlineManager.getGRReportList(resultArray));

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (IGRReportView != null) {
                                            if (IGRReportView != null) {
                                                IGRReportView.hideProgressDialog();
                                                IGRReportView.displayData(alSalesPromotion);
                                            }}
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                                refreshUI(responseBody);
                            }
                        } else {
                            String responseBody = IReceiveEvent.Util.getResponseBody(event.getReader());
                            refreshUI(responseBody);
                        }
                    }, iError -> {
                        iError.printStackTrace();
                        String errormessage = "";
                        errormessage = ConstantsUtils.geterrormessageForInternetlost(iError.getMessage(),context);
                        if(TextUtils.isEmpty(errormessage)){
                            errormessage = iError.getMessage();
                        }
                        refreshUI(errormessage);
                    });
                }else {
                    if (IGRReportView != null) {
                        IGRReportView.hideProgressDialog();
                    }
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            IGRReportView.dialogMessage(context.getString(R.string.data_conn_lost_during_sync));
//                            UtilConstants.showAlert(context.getString(R.string.data_conn_lost_during_sync), context);
                        }
                    });
                }
            } catch(Throwable e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            /*if (IGRReportView!=null){
                IGRReportView.hideProgressDialog();
                IGRReportView.displayData(alSalesPromotion);
                IGRReportView.displayRefreshTime(SyncUtils.getCollectionSyncTime(context, Constants.MaterialDocs));
            }*/
        }
    }


    private void refreshUI(final String errorMsg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (IGRReportView != null) {
                    IGRReportView.hideProgressDialog();
                    if (!TextUtils.isEmpty(errorMsg)) {
                        IGRReportView.dialogMessage(errorMsg);
                        IGRReportView.displayData(alSalesPromotion);
//                        IGRReportView.displayRefreshTime(Syn.getCollectionSyncTime(context, Constants.MaterialDocs));
                    }
                }
            }
        });
    }
    @Override
    public void onRequestError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e, context);
        isErrorFromBackend = true;
        if (errorBean.hasNoError()) {
            penROReqCount++;
            Constants.mBoolIsReqResAval = true;
            if ((operation == Operation.Create.getValue()) && (penROReqCount == pendingROVal)) {
                LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
                String concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                new RefreshAsyncTask(context, concatCollectionStr, this).execute();
            }

            if (operation == Operation.OfflineFlush.getValue()) {
                new RefreshAsyncTask(context, Constants.Visits, this).execute();
            } else if (operation == Operation.OfflineRefresh.getValue()) {
                LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
                if (IGRReportView != null) {
                    IGRReportView.hideProgressDialog();
                    IGRReportView.onRefreshView();
                    IGRReportView.dialogMessage(context.getString(R.string.msg_error_occured_during_sync));
                }
            } else if (operation == Operation.GetStoreOpen.getValue()) {
                Constants.isSync = false;
                if (IGRReportView != null) {
                    IGRReportView.hideProgressDialog();
                    IGRReportView.dialogMessage(context.getString(R.string.msg_error_occured_during_sync));
                }
            }
        } else if (errorBean.isStoreFailed()) {
            Constants.mBoolIsReqResAval = true;
            Constants.mBoolIsNetWorkNotAval = true;
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (IGRReportView != null) {
                    IGRReportView.showProgressDialog();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (IGRReportView != null) {
                    IGRReportView.showProgressDialog();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        } else {
            Constants.mBoolIsReqResAval = true;
            Constants.isSync = false;
            if (IGRReportView != null) {
                IGRReportView.showProgressDialog();
                IGRReportView.onRefreshView();
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }
    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {
/* if (operation == Operation.OfflineRefresh.getValue() && isFromWhere == 2) {
            Constants.updateLastSyncTimeToTable(mContext, pendingCollectionList);
            Constants.isSync = false;
            if (feedBackListView != null) {
                feedBackListView.hideProgress();
                feedBackListView.showMessage(mContext.getString(R.string.msg_sync_successfully_completed));
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, mActivity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        }*/
//        if (isFromWhere == 4) {
        if (operation == Operation.Create.getValue() && pendingROVal > 0) {
            Constants.mBoolIsReqResAval = true;

           /* alFlushColl = Constants.getPendingList();
            concatFlushCollStr = Constants.getConcatinatinFlushCollectios(alFlushColl);
            try {
                if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                    try {
                        OfflineManager.flushQueuedRequests(TSFSalesPromotionListPresenter.this, concatFlushCollStr);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                }
            } catch (ODataException e) {
                e.printStackTrace();
            }*/
           /* Set<String> set = new HashSet<>();
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
            set = sharedPreferences.getStringSet(Constants.SecondarySOCreate, null);

            HashSet<String> setTemp = new HashSet<>();
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
                while (itr.hasNext()) {
                    setTemp.add(itr.next().toString());
                }
            }

            setTemp.remove(tempRODevList[penROReqCount]);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(Constants.SecondarySOCreate, setTemp);
            editor.commit();*/


            penROReqCount++;
        }

        String concatCollectionStr = "";
        if ((operation == Operation.Create.getValue()) && (penROReqCount == pendingROVal)) {
            try {
                concatFlushCollStr = Constants.getConcatinatinFlushCollectios(alFlushColl);
                if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                    if (UtilConstants.isNetworkAvailable(context.getApplicationContext())) {
                        try {
                            OfflineManager.flushQueuedRequests(GRReportListPresenter.this, concatFlushCollStr);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        IGRReportView.hideProgressDialog();
                        UtilConstants.showAlert(context.getString(R.string.data_conn_lost_during_sync), context);
                    }
                } else {
                    if (UtilConstants.isNetworkAvailable(context.getApplicationContext())) {
                        concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                        new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                    } else {
                        IGRReportView.hideProgressDialog();
                        UtilConstants.showAlert(context.getString(R.string.err_no_network), context);
                    }
                }

            } catch (ODataException e) {
                e.printStackTrace();
            }
        } else if (operation == Operation.OfflineFlush.getValue()) {
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
            new RefreshAsyncTask(context, concatCollectionStr, this).execute();
        } else if (operation == Operation.OfflineRefresh.getValue()) {
            ConstantsUtils.startAutoSync(context, false);
            if (IGRReportView != null) {
                IGRReportView.hideProgressDialog();

                IGRReportView.onRefreshView();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
            Constants.isSync = false;
            try {
                OfflineManager.getAuthorizations(context);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
//            Constants.setSyncTime(context);
            ConstantsUtils.startAutoSync(context, false);
            if (IGRReportView != null) {
                IGRReportView.onRefreshView();
                IGRReportView.hideProgressDialog();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        }
    }

}
