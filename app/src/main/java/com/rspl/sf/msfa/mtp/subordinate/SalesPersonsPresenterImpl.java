package com.rspl.sf.msfa.mtp.subordinate;

import android.app.Activity;
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
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.mbo.SalesPersonBean;
import com.rspl.sf.msfa.socreate.ConfigTypeValues;
import com.rspl.sf.msfa.store.GetOnlineODataInterface;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.store.OnlineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by e10769 on 10-Apr-18.
 */

public class SalesPersonsPresenterImpl implements SalesPersonsPresenter, UIListener, GetOnlineODataInterface {

    private Activity mContext;
    private SalesPersonsView salesPersonsView = null;
    private ArrayList<SalesPersonBean> salesPersonBeanList = new ArrayList<>();
    private ArrayList<SalesPersonBean> searchBeanArrayList = new ArrayList<>();
    private ArrayList<String> alAssignColl = new ArrayList<>();
    private String searchQry = "";
    private String filterQRy = "";
    private String comingFrom = "";
    private GUID refguid =null;

    public SalesPersonsPresenterImpl(Activity mContext, SalesPersonsView salesPersonsView,String comingFrom) {
        this.mContext = mContext;
        this.salesPersonsView = salesPersonsView;
        this.comingFrom = comingFrom;
    }

    @Override
    public void onStart() {
        if (salesPersonsView != null) {
            salesPersonsView.showProgress();
        }
        salesPersonBeanList.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String mStrSPGUID = Constants.getSPGUID(Constants.SPGUID);
                String qry = "";
                if (comingFrom.equalsIgnoreCase(ConstantsUtils.RTGS_SUBORDINATE)) {
                    qry = Constants.SalesPersons + "?$filter=" + Constants.SPGUID + " ne guid'" + mStrSPGUID + "' " +
                            "and "+Constants.SPCategoryID+" ne '01'";
                }else{
                    qry = Constants.SalesPersons + "?$filter=" + Constants.SPGUID + " ne guid'" + mStrSPGUID + "'";
                }

                try {
                    salesPersonBeanList = OfflineManager.getSalesPersons(qry);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (salesPersonsView != null) {
                            salesPersonsView.hideProgress();
                            salesPersonsView.displayLastRefreshedTime(UtilConstants.getLastRefreshedTime(mContext, ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.SalesPersons, Constants.TimeStamp, mContext)));
                            onSearchQuery(searchQry);
                        }
                    }
                });
            }
        }).start();

    }

    @Override
    public void onDestroy() {
        salesPersonsView = null;
    }

    @Override
    public void onRefresh() {

        onRefreshData();
    }

    @Override
    public void onSearch(String searchTxt) {
        this.searchQry = searchTxt;
        onSearchQuery(searchTxt);
    }

    @Override
    public void onSearch(String searchTxt, ArrayList<SalesPersonBean> salesPersonBeans) {

    }

    @Override
    public void startFilter(String data) {
        this.searchQry = data;
        this.filterQRy = data;
        onSearchQuery(data);
        displayFilterType(data);
    }

    private void displayFilterType(String data) {
        try {
            String statusDesc = "";
            if (!TextUtils.isEmpty(data)) {
                statusDesc = ", " + data;
            }
            /*if (!TextUtils.isEmpty(delvStatusId)) {
                statusDesc = statusDesc + ", " + delvStatusName;
            }*/
            if (salesPersonsView != null) {
                salesPersonsView.setFilterDate(statusDesc);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void getSalesPersonsFromOnline() {
        if(salesPersonsView!=null){
            salesPersonsView.showProgress();
        }
        if(UtilConstants.isNetworkAvailable(mContext)){
            new AsyncTaskSalesPersonsByRole().execute();
        }else {
            if(salesPersonsView!=null) {
                salesPersonsView.hideProgress();
                salesPersonsView.displayList(salesPersonBeanList);
                salesPersonsView.displayMsg(mContext.getString(R.string.err_no_network));
            }
        }
    }
    Bundle requestbundle = null;
    String salesPersonQry = "";

    ArrayList<String> filterList = new ArrayList<>();
    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> entities, int operation, int requestCode, String resourcePath, Bundle bundle) {
       /* attendanceSummaryDone.clear();
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
        });*/
        salesPersonBeanList.clear();
        filterList.clear();
        SalesPersonBean salesPersonBean = null;
        ODataPropMap properties;
        ArrayList<ConfigTypeValues> configTypeValuesArrayList = new ArrayList<>();
        ArrayList<String> destinationID = new ArrayList<>();
        for (ODataEntity entity : entities) {
            salesPersonBean = new SalesPersonBean();
            properties = entity.getProperties();
            salesPersonBean = OfflineManager.getSalesPersonData(salesPersonBean, properties);
            /*if(!destinationID.contains(salesPersonBean.getDesignationID())){
                destinationID.add(salesPersonBean.getDesignationID());
            }*/
            salesPersonBeanList.add(salesPersonBean);
        }
        try {
            configTypeValuesArrayList = OfflineManager.getConfigTypeValues("ConfigTypsetTypeValues?$filter=Typeset eq 'MTPSTY'");
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        String rollType = sharedPreferences.getString(Constants.USERROLE, "");
        String strRoleID = "";
        if(!TextUtils.isEmpty(rollType) &&  rollType.equalsIgnoreCase("TO")) {
            strRoleID ="TSO";
        }else if(!TextUtils.isEmpty(rollType) &&  rollType.equalsIgnoreCase("Z1")) {
            strRoleID ="ASM";
        } else if(!TextUtils.isEmpty(rollType) &&  rollType.equalsIgnoreCase("Z3")) {
            strRoleID ="SH";
        }

        boolean isRoleCheck = false;
        Collections.sort(configTypeValuesArrayList, new Comparator<ConfigTypeValues>() {
            @Override
            public int compare(ConfigTypeValues o1, ConfigTypeValues o2) {
                return o1.getType().compareTo(o2.getType());
            }
        });
        for(int i=0;i<configTypeValuesArrayList.size();i++){
            if(strRoleID.equalsIgnoreCase(configTypeValuesArrayList.get(i).getTypeValue())){
                isRoleCheck = true;
            }
            if(isRoleCheck){
                configTypeValuesArrayList.remove(i);
            }else {
                filterList.add(configTypeValuesArrayList.get(i).getTypeValue());
            }
        }

        if(filterList.size()>1){
            filterList.add(0,"All");
        }

        Collections.sort(salesPersonBeanList, new Comparator<SalesPersonBean>() {
            @Override
            public int compare(SalesPersonBean o1, SalesPersonBean o2) {
                return o1.getFirstName().compareTo(o2.getFirstName());
            }
        });

        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (salesPersonsView!=null){
                    salesPersonsView.hideProgress();
                    salesPersonsView.displayFilter(filterList);
                    // salesPersonsView.displayList(attFinalList);
                    salesPersonsView.displayLastRefreshedTime(UtilConstants.getLastRefreshedTime(mContext, ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.SalesPersons, Constants.TimeStamp, mContext)));
                    onSearchQuery(searchQry);
                }
            }
        });
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, int operation, int requestCode, String resourcePath, final String errorMsg, Bundle bundle) {
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (salesPersonsView!=null){
                    salesPersonsView.hideProgress();
                    salesPersonsView.displayMsg(errorMsg);
                    onSearchQuery(searchQry);
                    // salesPersonsView.displayList(attFinalList);
                }
            }
        });
    }

    private class AsyncTaskSalesPersonsByRole extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //get SalesPersons list from online call
            try {
                Log.d("SalesPerson","Opening Store");
                SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
                final String rollType = sharedPreferences.getString(Constants.USERROLE, "");
                // SalesPersons?$filter=DesignationID eq 'TSI' or DesignationID eq 'TSO' or DesignationID eq 'ASM'
                salesPersonQry = Constants.SalesPersons;
                String filterType = "?$filter=",makeQuery="";
                if(!TextUtils.isEmpty(rollType) &&  rollType.equalsIgnoreCase("TO")) {
//                    salesPersonQry = salesPersonQry+filterType+Constants.DesignationID+" eq 'TSI' or "+Constants.DesignationID+" eq 'TSO'";
                    salesPersonQry = salesPersonQry + filterType + Constants.DesignationID + "%20eq%20'TSI'";
                }else if(!TextUtils.isEmpty(rollType) &&  rollType.equalsIgnoreCase("Z1")) {
                    /*salesPersonQry = salesPersonQry+filterType+Constants.DesignationID+" eq 'TSI' or "+Constants.DesignationID+" eq 'TSO' or "
                            +Constants.DesignationID+" eq 'ASM'";*/
                    salesPersonQry = salesPersonQry + filterType + Constants.DesignationID + "%20eq%20'TSI'%20or%20" + Constants.DesignationID + "%20eq%20'TSO'";
                } else if(!TextUtils.isEmpty(rollType) &&  rollType.equalsIgnoreCase("Z3")) {
                    /*salesPersonQry = salesPersonQry+filterType+Constants.DesignationID+" eq 'TSI' or "+Constants.DesignationID+" eq 'TSO' or "
                            +Constants.DesignationID+" eq 'ASM' or "+Constants.DesignationID+" eq 'SH'";*/
                    salesPersonQry = salesPersonQry + filterType + Constants.DesignationID + "%20eq%20'TSI'%20or%20" + Constants.DesignationID + "%20eq%20'TSO'%20or%20"
                            + Constants.DesignationID + "%20eq%20'ASM'";
                }

                try {

                    OnlineManager.doOnlineHeaderSPMTPRequest(salesPersonQry, mContext, iReceiveEvent -> {
                        if (iReceiveEvent.getResponseStatusCode()==200){
                            JSONObject jsonObject = OnlineManager.getJSONBody(iReceiveEvent);
                            JSONArray jsonArray = OnlineManager.getJSONArrayBody(jsonObject);
                            salesPersonBeanList.clear();
                            filterList.clear();
                            SalesPersonBean salesPersonBean = null;
                            ODataPropMap properties;
                            ArrayList<ConfigTypeValues> configTypeValuesArrayList = new ArrayList<>();
                            ArrayList<String> destinationID = new ArrayList<>();
                            for (int i=0;i< jsonArray.length();i++) {
                                try {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    salesPersonBean = new SalesPersonBean();
//                                    properties = entity.getProperties();
                                    salesPersonBean = OfflineManager.getSalesPersonData(salesPersonBean, jsonObject1);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
            /*if(!destinationID.contains(salesPersonBean.getDesignationID())){
                destinationID.add(salesPersonBean.getDesignationID());
            }*/
                                salesPersonBeanList.add(salesPersonBean);
                            }
                            try {
                                configTypeValuesArrayList = OfflineManager.getConfigTypeValues("ConfigTypsetTypeValues?$filter=Typeset eq 'MTPSTY'");
                            } catch (OfflineODataStoreException e) {
                                e.printStackTrace();
                            }
                            String strRoleID = "";
                            if(!TextUtils.isEmpty(rollType) &&  rollType.equalsIgnoreCase("TO")) {
                                strRoleID ="TSO";
                            }else if(!TextUtils.isEmpty(rollType) &&  rollType.equalsIgnoreCase("Z1")) {
                                strRoleID ="ASM";
                            } else if(!TextUtils.isEmpty(rollType) &&  rollType.equalsIgnoreCase("Z3")) {
                                strRoleID ="SH";
                            }

                            boolean isRoleCheck = false;
                            Collections.sort(configTypeValuesArrayList, new Comparator<ConfigTypeValues>() {
                                @Override
                                public int compare(ConfigTypeValues o1, ConfigTypeValues o2) {
                                    return o1.getType().compareTo(o2.getType());
                                }
                            });
                            for(int i=0;i<configTypeValuesArrayList.size();i++){
                                if(strRoleID.equalsIgnoreCase(configTypeValuesArrayList.get(i).getTypeValue())){
                                    isRoleCheck = true;
                                }
                                if(isRoleCheck){
                                    configTypeValuesArrayList.remove(i);
                                }else {
                                    filterList.add(configTypeValuesArrayList.get(i).getTypeValue());
                                }
                            }

                            if(filterList.size()>1){
                                filterList.add(0,"All");
                            }

                            Collections.sort(salesPersonBeanList, new Comparator<SalesPersonBean>() {
                                @Override
                                public int compare(SalesPersonBean o1, SalesPersonBean o2) {
                                    return o1.getFirstName().compareTo(o2.getFirstName());
                                }
                            });

                            ((Activity)mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (salesPersonsView!=null){
                                        salesPersonsView.hideProgress();
                                        salesPersonsView.displayFilter(filterList);
                                        // salesPersonsView.displayList(attFinalList);
                                        salesPersonsView.displayLastRefreshedTime(UtilConstants.getLastRefreshedTime(mContext, ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.SalesPersons, Constants.TimeStamp, mContext)));
                                        onSearchQuery(searchQry);
                                    }
                                }
                            });
                        }else {
                            String errorMsg="";
                            try {
                                errorMsg = Constants.getErrorMessage(iReceiveEvent,mContext);
                                String finalErrorMsg = errorMsg;
                                ((Activity)mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (salesPersonsView!=null){
                                            salesPersonsView.hideProgress();
                                            salesPersonsView.displayMsg(finalErrorMsg);
                                        }
                                    }
                                });
                                LogManager.writeLogError(errorMsg);
                            } catch (Throwable e) {
                                e.printStackTrace();
                                ((Activity)mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (salesPersonsView!=null){
                                            salesPersonsView.hideProgress();
                                            salesPersonsView.displayMsg(e.toString());
                                        }
                                    }
                                });
                                LogManager.writeLogError(e.getMessage());
                            }
                        }
                    }, e -> {
                        e.printStackTrace();
                        String errormessage = "";
                        errormessage = ConstantsUtils.geterrormessageForInternetlost(e.getMessage(),mContext);
                        if(TextUtils.isEmpty(errormessage)){
                            errormessage = e.getMessage();
                        }
                        String finalErrormessage = errormessage;
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (salesPersonsView!=null){
                                    salesPersonsView.hideProgress();
                                    salesPersonsView.displayMsg(finalErrormessage);
                                }
                            }
                        });

                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    ConstantsUtils.printErrorLog(e.getMessage());
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (salesPersonsView!=null){
                                salesPersonsView.hideProgress();
                                salesPersonsView.displayMsg(e.toString());
                            }
                        }
                    });

                }

            } catch (Exception e) {
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (salesPersonsView!=null){
                            salesPersonsView.hideProgress();
                            salesPersonsView.displayMsg(e.toString());
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




    private void onSearchQuery(String searchText) {
        searchBeanArrayList.clear();
        boolean isFName = false;
        boolean isLName = false;
        boolean isMobile = false;
        boolean isDesidnationID = false;
        boolean isRSCode = false;
        if(!TextUtils.isEmpty(filterQRy)){
            ArrayList<SalesPersonBean> tempList = new ArrayList<>();
            for (SalesPersonBean item : salesPersonBeanList) {
                if (!TextUtils.isEmpty(filterQRy)) {
                    isDesidnationID = item.getDesignationID().toLowerCase().contains(filterQRy.toLowerCase());
                } else {
                    isDesidnationID = true;
                }
                if (isDesidnationID)
                    tempList.add(item);
            }
            if (TextUtils.isEmpty(searchText)) {
                searchBeanArrayList.addAll(tempList);
            } else {
                for (SalesPersonBean item : tempList) {
                    if (!TextUtils.isEmpty(searchText)) {
                        isFName = item.getFirstName().toLowerCase().contains(searchText.toLowerCase());
                        isLName = item.getLastName().toLowerCase().contains(searchText.toLowerCase());
                        isMobile = item.getMobileNo().toLowerCase().contains(searchText.toLowerCase());
                        isDesidnationID = item.getDesignationID().toLowerCase().contains(searchText.toLowerCase());
                        isRSCode = item.getExternalRefID().toLowerCase().contains(searchText.toLowerCase());
                    } else {
                        isFName = true;
                        isLName = true;
                        isMobile = true;
                        isDesidnationID = true;
                        isRSCode = true;
                    }
                    if (isFName || isLName || isMobile || isDesidnationID || isRSCode)
                        searchBeanArrayList.add(item);
                }
            }
        }else {
            if (salesPersonBeanList != null) {
                if (TextUtils.isEmpty(searchText)) {
                    searchBeanArrayList.addAll(salesPersonBeanList);
                } else {
                    for (SalesPersonBean item : salesPersonBeanList) {
                        if (!TextUtils.isEmpty(searchText)) {
                            isFName = item.getFirstName().toLowerCase().contains(searchText.toLowerCase());
                            isLName = item.getLastName().toLowerCase().contains(searchText.toLowerCase());
                            isMobile = item.getMobileNo().toLowerCase().contains(searchText.toLowerCase());
                            isDesidnationID = item.getDesignationID().toLowerCase().contains(searchText.toLowerCase());
                            isRSCode = item.getExternalRefID().toLowerCase().contains(searchText.toLowerCase());
                        } else {
                            isFName = true;
                            isLName = true;
                            isMobile = true;
                            isDesidnationID = true;
                            isRSCode = true;
                        }
                        if (isFName || isLName || isMobile || isDesidnationID || isRSCode)
                            searchBeanArrayList.add(item);
                    }
                }
            }
        }

        if (salesPersonsView != null) {
            salesPersonsView.displayList(searchBeanArrayList);
        }
    }

    private void onRefreshData() {
        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(mContext)) {
            alAssignColl.clear();
            concatCollectionStr = "";
            alAssignColl.add(Constants.SalesPersons);
            alAssignColl.add(Constants.UserSalesPersons);
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);

            if (Constants.iSAutoSync) {
                if (salesPersonsView != null) {
                    salesPersonsView.hideProgress();
                    salesPersonsView.displayMsg(mContext.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    refguid = GUID.newRandom();
                    Constants.updateStartSyncTime(mContext,Constants.DownLoad,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(mContext, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (salesPersonsView != null) {
                salesPersonsView.hideProgress();
                salesPersonsView.displayMsg(mContext.getString(R.string.no_network_conn));
            }
        }
    }

    @Override
    public void onRequestError(int i, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(i, e, mContext);
        if (errorBean.hasNoError()) {
            if (!Constants.isStoreClosed) {
                if (i == Operation.OfflineRefresh.getValue()) {
                    try {
                       /* String syncTime = Constants.getSyncHistoryddmmyyyyTime();
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
                        Constants.updateSyncTime(alAssignColl,mContext,Constants.DownLoad,refguid.toString().toUpperCase());
                    } catch (Exception exce) {
                        LogManager.writeLogError(Constants.SyncTableHistory + exce.getMessage());
                    }
                    Constants.isSync = false;
                    if (!Constants.isStoreClosed) {
                        if (salesPersonsView != null) {
                            salesPersonsView.hideProgress();
                            salesPersonsView.displayMsg(mContext.getString(R.string.msg_error_occured_during_sync));
                        }
                    } else {
                        if (salesPersonsView != null) {
                            salesPersonsView.hideProgress();
                            salesPersonsView.displayMsg(mContext.getString(R.string.msg_sync_terminated));
                        }
                    }
                }
            }
        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(mContext)) {
                Constants.isSync = true;
                if (salesPersonsView != null) {
                    salesPersonsView.showProgress();
                }
                new RefreshAsyncTask(mContext, "", this).execute();
            } else {
                Constants.isSync = false;
                if (salesPersonsView != null) {
                    salesPersonsView.hideProgress();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), mContext);
                }
            }
        } else {
            Constants.isSync = false;
            if (salesPersonsView != null) {
                salesPersonsView.hideProgress();
                Constants.displayMsgReqError(errorBean.getErrorCode(), mContext);
            }
        }
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        if (!Constants.isStoreClosed) {
            if (i == Operation.OfflineRefresh.getValue()) {
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
                    Constants.updateSyncTime(alAssignColl,mContext,Constants.DownLoad,refguid.toString().toUpperCase());
                } catch (Exception exce) {
                    LogManager.writeLogError(Constants.SyncTableHistory + exce.getMessage());
                }

                Constants.isSync = false;
                if (!Constants.isStoreClosed) {
                    if (salesPersonsView != null) {
                        salesPersonsView.hideProgress();
                        AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, mContext, BuildConfig.APPLICATION_ID, false);
                    }
                    onStart();
                } else {
                    if (salesPersonsView != null) {
                        salesPersonsView.hideProgress();
                        salesPersonsView.displayMsg(mContext.getString(R.string.msg_sync_terminated));
                    }
                }
            } else if (i == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
                Constants.isSync = false;
                try {
                    OfflineManager.getAuthorizations(mContext);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                Constants.setSyncTime(mContext,refguid.toString().toUpperCase());

                if (salesPersonsView != null) {
                    salesPersonsView.hideProgress();
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, mContext, BuildConfig.APPLICATION_ID, false);
                }
                onStart();
            }
        }
    }


}
