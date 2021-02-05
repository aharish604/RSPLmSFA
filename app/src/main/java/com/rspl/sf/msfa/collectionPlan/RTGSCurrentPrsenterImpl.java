package com.rspl.sf.msfa.collectionPlan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.google.gson.Gson;
import com.rspl.sf.msfa.BuildConfig;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.asyncTask.RefreshAsyncTask;
import com.rspl.sf.msfa.asyncTask.SyncFromDataValtAsyncTask;
import com.rspl.sf.msfa.collectionPlan.collectionCreate.CollectionPlanCreateActivity;
import com.rspl.sf.msfa.collectionPlan.collectionCreate.SaleAreaBean;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.DialogCallBack;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.so.SOUtils;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.store.OnlineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by e10860 on 2/16/2018.
 */

public class RTGSCurrentPrsenterImpl implements RTGSCurrentPresenter, UIListener {

    private Activity activity;
    private RTGSView views = null;
    private String comingFrom = "", spGUID = "";
    private ArrayList<WeekHeaderList> finalDisplayList = new ArrayList<>();
    private List<String> alAssignColl = new ArrayList<>();
    private Context mcontext;
    private GUID refguid =null;

    RTGSCurrentPrsenterImpl(Activity activity, RTGSView views, String comingFrom, String spGUID, Context mcontext) {
        this.activity = activity;
        this.views = views;
        this.comingFrom = comingFrom;
        this.spGUID = spGUID;
        this.mcontext = mcontext;
    }

    @Override
    public void onStart() {

        if (views != null) {
            views.onProgress();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                int displayPos = 0;
                Calendar currentCal = Calendar.getInstance();
                int week = currentCal.get(Calendar.WEEK_OF_MONTH);
                try {
                    String period = ConstantsUtils.getMonth();
                    String periodYear = SOUtils.getYearFromCalender(activity, activity.getString(R.string.so_filter_current_mont));
                    if (comingFrom.equalsIgnoreCase(ConstantsUtils.MONTH_NEXT) || comingFrom.equalsIgnoreCase(ConstantsUtils.RTGS_SUBORDINATE_NEXT)) {
                        period = ConstantsUtils.getNextMonth();
//                        periodYear = ConstantsUtils.getYear();
                        periodYear = SOUtils.getYearFromCalender(activity, activity.getString(R.string.so_filter_next_mont));
                    }

                    String startDate = "";
                    String endDate = "";

                    if (comingFrom.equalsIgnoreCase(ConstantsUtils.MONTH_CURRENT) || comingFrom.equalsIgnoreCase(ConstantsUtils.RTGS_SUBORDINATE_CURRENT)) {
                        startDate = SOUtils.getStartDate(activity, activity.getString(R.string.so_filter_current_mont));
                        endDate = SOUtils.getEndDate(activity, activity.getString(R.string.so_filter_current_mont));
                    } else {
                        startDate = SOUtils.getStartDate(activity, activity.getString(R.string.so_filter_next_mont));
                        endDate = SOUtils.getEndDate(activity, activity.getString(R.string.so_filter_next_mont));
                    }
                    String qry = Constants.CollectionPlan + "?$filter=" + Constants.Period + " eq '" + period + "' and " + Constants.Fiscalyear + " eq '" + periodYear + "' and SPGUID eq guid'" + spGUID + "'";
//            displayList = OfflineManager.getCollectionCurrent(qry, comingFrom);
                    finalDisplayList = OnlineManager.getCollectionNextmonth(qry, comingFrom, activity, startDate, endDate, period,spGUID);
                    if (comingFrom.equalsIgnoreCase(ConstantsUtils.MONTH_CURRENT)) {
                        for (WeekHeaderList mtpHeaderBean : finalDisplayList) {
                            if (mtpHeaderBean.isTitle() && mtpHeaderBean.getWeek() == week) {
                                break;
                            }
                            displayPos++;
                        }
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
//                if (views != null) {
//                    views.displayData(finalDisplayList);
//                    if (displayPos > 0)
//                        views.displayViewPost(displayPos);
//                }
                final int dispPOS = displayPos;
                ((Activity) activity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (views != null) {
                            views.onHideProgress();
                            views.displayData(finalDisplayList);
                            if (dispPOS > 0)
                                views.displayViewPost(dispPOS);
                            views.displayLastRefreshedTime(UtilConstants.getLastRefreshedTime(mcontext, ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.CollectionPlan, Constants.TimeStamp, mcontext)));
                        }
                    }
                });
            }
        }).start();


    }

    @Override
    public void onDestroy() {
        views = null;
    }

    @Override
    public void onSaveData(String saveType, String comingFrom) {
        boolean isValidData = false;
        String period = ConstantsUtils.getMonth();
        String periodYear = SOUtils.getYearFromCalender(activity, activity.getString(R.string.so_filter_current_mont));
        if (comingFrom.equalsIgnoreCase(ConstantsUtils.MONTH_NEXT) || comingFrom.equalsIgnoreCase(ConstantsUtils.RTGS_SUBORDINATE_NEXT)) {
            period = ConstantsUtils.getNextMonth();
//            periodYear = ConstantsUtils.getYear();
            periodYear = SOUtils.getYearFromCalender(activity, activity.getString(R.string.so_filter_next_mont));
        }

        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.PREFS_NAME, 0);
        Hashtable<String, String> masterHeaderTable = new Hashtable<>();
        GUID guid = GUID.newRandom();
        String headerGuid = guid.toString36(),createdOn="",createdBy="";
        String doc_no = (System.currentTimeMillis() + "").substring(3, 10);
        masterHeaderTable.put(Constants.SPGUID, spGUID);
        masterHeaderTable.put(Constants.IS_UPDATE, "");
        if (comingFrom.equalsIgnoreCase(ConstantsUtils.MONTH_CURRENT) || comingFrom.equalsIgnoreCase(ConstantsUtils.RTGS_SUBORDINATE_CURRENT)) {
            Calendar currentCal = Calendar.getInstance();
            currentCal.set(Calendar.DAY_OF_MONTH, 1);
            currentCal.set(Calendar.MILLISECOND, 0);
            currentCal.set(Calendar.SECOND, 0);
            currentCal.set(Calendar.MINUTE, 0);
            currentCal.set(Calendar.HOUR, 0);
            masterHeaderTable.put(Constants.ValidFrom, ConstantsUtils.convertCalenderToDisplayDateFormat(currentCal, "yyyy-MM-dd'T'HH:mm:ss"));
            currentCal = Calendar.getInstance();
            currentCal.set(Calendar.DAY_OF_MONTH, currentCal.getActualMaximum(Calendar.DAY_OF_MONTH));
            currentCal.set(Calendar.MILLISECOND, 0);
            currentCal.set(Calendar.SECOND, 0);
            currentCal.set(Calendar.MINUTE, 0);
            currentCal.set(Calendar.HOUR, 0);
            masterHeaderTable.put(Constants.ValidTo, ConstantsUtils.convertCalenderToDisplayDateFormat(currentCal, "yyyy-MM-dd'T'HH:mm:ss"));
        } else {
            Calendar currentCal = Calendar.getInstance();
            currentCal.add(Calendar.MONTH, 1);
            currentCal.set(Calendar.DAY_OF_MONTH, 1);
            currentCal.set(Calendar.MILLISECOND, 0);
            currentCal.set(Calendar.SECOND, 0);
            currentCal.set(Calendar.MINUTE, 0);
            currentCal.set(Calendar.HOUR, 0);
            masterHeaderTable.put(Constants.ValidFrom, ConstantsUtils.convertCalenderToDisplayDateFormat(currentCal, "yyyy-MM-dd'T'HH:mm:ss"));
            currentCal = Calendar.getInstance();
            currentCal.add(Calendar.MONTH, 1);
            currentCal.set(Calendar.DAY_OF_MONTH, currentCal.getActualMaximum(Calendar.DAY_OF_MONTH));
            currentCal.set(Calendar.MILLISECOND, 0);
            currentCal.set(Calendar.SECOND, 0);
            currentCal.set(Calendar.MINUTE, 0);
            currentCal.set(Calendar.HOUR, 0);
            masterHeaderTable.put(Constants.ValidTo, ConstantsUtils.convertCalenderToDisplayDateFormat(currentCal, "yyyy-MM-dd'T'HH:mm:ss"));
        }

//        String salesDist = "";
//        try {
//            ArrayList<Config> configs = OfflineManager.getSalesDistrict();
//            if (!configs.isEmpty()) {
//                salesDist = configs.get(0).getFeature();
//            }
//        } catch (OfflineODataStoreException e) {
//            e.printStackTrace();
//        }
        masterHeaderTable.put(Constants.EntityType, Constants.CollectionPlan);
        String mStrCurrncy = Constants.getNameByCPGUID(Constants.SalesPersons,Constants.Currency,Constants.SPGUID,spGUID);
        ArrayList<HashMap<String, String>> itemTable = new ArrayList<>();
        for (WeekHeaderList mtpHeaderBean : finalDisplayList) {
            if (!TextUtils.isEmpty(mtpHeaderBean.getCollectionPlanGUID()) && !TextUtils.isEmpty(mtpHeaderBean.getIsUpdate())) {
                headerGuid = mtpHeaderBean.getCollectionPlanGUID();
                createdOn = mtpHeaderBean.getCreatedOn();
                createdBy = mtpHeaderBean.getCreatedBy();
                masterHeaderTable.put(Constants.IS_UPDATE, "X");
            } else {
                masterHeaderTable.put(Constants.IS_UPDATE, "");
            }
            if (!TextUtils.isEmpty(mtpHeaderBean.getDeviceNo())) {
                doc_no = mtpHeaderBean.getDeviceNo();
            }
            for (WeekDetailsList routePlanBean : mtpHeaderBean.getWeekDetailsLists()) {

                if (!isValidData) {
                    isValidData = true;
                }
                HashMap<String, String> dbItemTable = new HashMap<>();

                if (!TextUtils.isEmpty(routePlanBean.getCollectionPlanItemGUID())) {
                    dbItemTable.put(Constants.CollectionPlanItemGUID, routePlanBean.getCollectionPlanItemGUID());
                } else {
                    guid = GUID.newRandom();
                    dbItemTable.put(Constants.CollectionPlanItemGUID, guid.toString36());
                }
                dbItemTable.put(Constants.CollectionPlanGUID, headerGuid);
                Calendar calendar = ConstantsUtils.convertCalenderToDisplayDateFormat(mtpHeaderBean.getFullDate(), "dd-MMM-yyyy");
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.HOUR, 0);
                dbItemTable.put(Constants.CollectionPlanDate, ConstantsUtils.convertCalenderToDisplayDateFormat(calendar, "yyyy-MM-dd'T'HH:mm:ss"));
                dbItemTable.put(Constants.CPNo, routePlanBean.getcPNo());
                dbItemTable.put(Constants.CPName, routePlanBean.getcPName());
                dbItemTable.put(Constants.Remarks, routePlanBean.getRemarks());
                dbItemTable.put(Constants.CPType, routePlanBean.getcPType());
                dbItemTable.put(Constants.PlannedValue, routePlanBean.getPlannedValue());
                try {
                 //   dbItemTable.put(Constants.AchievedValue, routePlanBean.getAchievedValue());
                    dbItemTable.put(Constants.AchievedValue, "0.00");
                } catch (Exception e) {
                    dbItemTable.put(Constants.AchievedValue, "0");
                    e.printStackTrace();
                }
                dbItemTable.put(Constants.Currency, mStrCurrncy);
                dbItemTable.put(Constants.CreatedOn, routePlanBean.getCreatedOn());
                dbItemTable.put(Constants.CreatedBy, routePlanBean.getCreatedBy());
                String creaditCrotrlID = "";
                String creaditCrotrlDesc = "";
                try {
                    if(routePlanBean.getSaleAreaDetailsBean().size()>0){
                        SaleAreaBean saleAreaBean = routePlanBean.getSaleAreaDetailsBean().get(0);
                        if(saleAreaBean!=null){
                            creaditCrotrlID = saleAreaBean.getCreditControlAreaID();
                            creaditCrotrlDesc = saleAreaBean.getCreditControlAreaDesc();
                        }
                    }else{
                        creaditCrotrlID = routePlanBean.getCrdtCtrlArea();
                        creaditCrotrlDesc = routePlanBean.getCrdtCtrlAreaDs();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dbItemTable.put(Constants.CrdtCtrlArea, creaditCrotrlID);
                dbItemTable.put(Constants.CrdtCtrlAreaDs, creaditCrotrlDesc);
                int value1=0;
                if (!TextUtils.isEmpty(routePlanBean.getPlannedValue())){
                    try {
                        value1 = (int)Double.parseDouble(routePlanBean.getPlannedValue());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                if ((!TextUtils.isEmpty(routePlanBean.getPlannedValue())&&value1!=0)/*||!TextUtils.isEmpty(routePlanBean.getRemarks())*/) {
                    itemTable.add(dbItemTable);
                }
                int value=0;
                if (!TextUtils.isEmpty(routePlanBean.getPlannedValue2())){
                    try {
                         value = (int)Double.parseDouble(routePlanBean.getPlannedValue2());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                // extra value
                if ((!TextUtils.isEmpty(routePlanBean.getPlannedValue2())&&value!=0)/*||!TextUtils.isEmpty(routePlanBean.getRemarks2())*/) {

                    Calendar calendar1 = ConstantsUtils.convertCalenderToDisplayDateFormat(mtpHeaderBean.getFullDate(), "dd-MMM-yyyy");
                    calendar1.set(Calendar.MILLISECOND, 0);
                    calendar1.set(Calendar.SECOND, 0);
                    calendar1.set(Calendar.MINUTE, 0);
                    calendar1.set(Calendar.HOUR, 0);
                    dbItemTable = new HashMap<>();
                    dbItemTable.put(Constants.CollectionPlanGUID, headerGuid);
                    dbItemTable.put(Constants.CollectionPlanDate, ConstantsUtils.convertCalenderToDisplayDateFormat(calendar1, "yyyy-MM-dd'T'HH:mm:ss"));
                    dbItemTable.put(Constants.CPNo, routePlanBean.getcPNo());
                    dbItemTable.put(Constants.CPName, routePlanBean.getcPName());
                    dbItemTable.put(Constants.Remarks, routePlanBean.getRemarks());
                    dbItemTable.put(Constants.CPType, routePlanBean.getcPType());
                    dbItemTable.put(Constants.PlannedValue, routePlanBean.getPlannedValue());
                    try {
                        dbItemTable.put(Constants.AchievedValue, routePlanBean.getAchievedValue());
                    } catch (Exception e) {
                        dbItemTable.put(Constants.AchievedValue, "0");
                        e.printStackTrace();
                    }
                    dbItemTable.put(Constants.Currency, mStrCurrncy);
                    dbItemTable.put(Constants.CreatedOn, routePlanBean.getCreatedOn());
                    dbItemTable.put(Constants.CreatedBy, routePlanBean.getCreatedBy());
                    if (!TextUtils.isEmpty(routePlanBean.getCollectionPlanItemGUID1())) {
                        dbItemTable.put(Constants.CollectionPlanItemGUID, routePlanBean.getCollectionPlanItemGUID1());
                    } else {
                        guid = GUID.newRandom();
                        dbItemTable.put(Constants.CollectionPlanItemGUID, guid.toString36());
                    }
                    dbItemTable.put(Constants.PlannedValue, routePlanBean.getPlannedValue2());
                    dbItemTable.put(Constants.Remarks, routePlanBean.getRemarks2());
                    creaditCrotrlID = "";
                    creaditCrotrlDesc = "";
                    try {
                        if(routePlanBean.getSaleAreaDetailsBean().size()>1){
                            SaleAreaBean saleAreaBean = routePlanBean.getSaleAreaDetailsBean().get(1);
                            if(saleAreaBean!=null){
                                creaditCrotrlID = saleAreaBean.getCreditControlAreaID();
                                creaditCrotrlDesc = saleAreaBean.getCreditControlAreaDesc();
                            }
                        }else{
                            if(routePlanBean.getSaleAreaDetailsBean()!=null && routePlanBean.getSaleAreaDetailsBean().size()>0){
                                SaleAreaBean saleAreaBean = routePlanBean.getSaleAreaDetailsBean().get(0);
                                if(saleAreaBean!=null){
                                    creaditCrotrlID = saleAreaBean.getCreditControlAreaID();
                                    creaditCrotrlDesc = saleAreaBean.getCreditControlAreaDesc();
                                }
                            }else {
                                creaditCrotrlID = routePlanBean.getCrdtCtrlArea();
                                creaditCrotrlDesc = routePlanBean.getCrdtCtrlAreaDs();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dbItemTable.put(Constants.CrdtCtrlArea, creaditCrotrlID);
                    dbItemTable.put(Constants.CrdtCtrlAreaDs, creaditCrotrlDesc);
                    itemTable.add(dbItemTable);
                }
            }
        }
        masterHeaderTable.put(Constants.CollectionPlanGUID, headerGuid);

        masterHeaderTable.put(Constants.Period, period);
        masterHeaderTable.put(Constants.Fiscalyear, periodYear);
        masterHeaderTable.put(Constants.CreatedOn, createdOn);
        masterHeaderTable.put(Constants.CreatedBy, createdBy);
        Gson gson1 = new Gson();
        String jsonFromMap = "";
        try {
            jsonFromMap = gson1.toJson(itemTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        masterHeaderTable.put(Constants.CollectionPlanItem, jsonFromMap);
        if (isValidData) {
            Set<String> set = sharedPreferences.getStringSet(Constants.RTGSDataValt, null);
            HashSet<String> setTemp = new HashSet<>();
            if (set != null && !set.isEmpty()) {
                Iterator<String> itr = set.iterator();
                while (itr.hasNext()) {
                    setTemp.add(itr.next().toString());
                }
            }
            setTemp.add(doc_no);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(Constants.RTGSDataValt, setTemp);
            editor.apply();
            JSONObject jsonHeaderObject = new JSONObject(masterHeaderTable);
            ConstantsUtils.storeInDataVault(doc_no, jsonHeaderObject.toString(),mcontext);
            onStart();
            if (views != null) {
                if (TextUtils.isEmpty(masterHeaderTable.get(Constants.IS_UPDATE))){
                    try {
//                        runOnBackground(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("MTP_CREATE","FAILED WITH EXCEPTION");
                    }
                    views.showSuccessMsg("RTGS Created Successfully");
                } else {
                    try {
//                        runOnBackground(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("MTP_CREATE","FAILED WITH EXCEPTION");
                    }
                    views.showSuccessMsg("RTGS Created Successfully");
                }
            }
        } else {
            if (views != null) {
                views.showSuccessMsg("Please select atleast one collection plan");
            }
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CollectionPlanCreateActivity.CUSTOMER_CATEGROIZE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                int indexPos = data.getIntExtra(ConstantsUtils.EXTRA_POS, -1);
                WeekHeaderList mtpResultHeaderBean = (WeekHeaderList) data.getSerializableExtra(Constants.EXTRA_BEAN);
                if (finalDisplayList != null && finalDisplayList.size() > indexPos && indexPos != -1 && mtpResultHeaderBean != null) {
                    finalDisplayList.remove(indexPos);
                    finalDisplayList.add(indexPos, mtpResultHeaderBean);
                    if (views != null)
                        views.displayData(finalDisplayList);
                }
            }
        }
    }

    @Override
    public void onRefresh() {
        if (UtilConstants.isNetworkAvailable(activity)) {
            if (Constants.iSAutoSync) {
                if (views != null) {
                    views.onHideProgress();
                    views.showMsg(activity.getString(R.string.alert_auto_sync_is_progress));
                }
                return;
            }
            if (views != null) {
                views.onProgress();
            }
            try {
                alAssignColl.add(Constants.CollectionPlan);
                alAssignColl.add(Constants.CollectionPlanItem);
                alAssignColl.add(Constants.CollectionPlanItemDetails);
                alAssignColl.add(Constants.ConfigTypsetTypeValues);
                String syncColl = TextUtils.join(", ", alAssignColl);
                Constants.isSync = true;
                refguid = GUID.newRandom();
                Constants.updateStartSyncTime(mcontext,Constants.RTGS_sync,Constants.StartSync,refguid.toString().toUpperCase());
                new RefreshAsyncTask(mcontext, syncColl, this).execute();
            } catch (final Exception e) {
                e.printStackTrace();
                if (views != null) {
                    views.onHideProgress();
                    views.showMsg(e.getMessage());
                }
            }
        } else {
            if (views != null) {
                views.onHideProgress();
                views.showMsg(activity.getString(R.string.no_network_conn));
            }
        }
    }

    @Override
    public void onRequestError(int i, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(i, e, activity);
        if (errorBean.hasNoError()) {
            if (!Constants.isStoreClosed) {
                if (i == Operation.OfflineRefresh.getValue()) {
                    Constants.isSync = false;
                    if (!Constants.isStoreClosed) {
                        if (views != null) {
                            views.onHideProgress();
                            views.showMsg(activity.getString(R.string.msg_error_occured_during_sync));
                        }
                    } else {
                        if (views != null) {
                            views.onHideProgress();
                            views.showMsg(activity.getString(R.string.msg_sync_terminated));
                        }
                    }
                }else if (i == Operation.GetStoreOpen.getValue()){
                    Constants.isSync = false;
                    if (views != null) {
                        views.onHideProgress();
                        views.showMsg(activity.getString(R.string.msg_error_occured_during_sync));
                    }
                }
            }
        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(mcontext)) {
                Constants.isSync = true;
                if (views != null) {
                    views.onProgress();
                }
                new RefreshAsyncTask(mcontext, "", this).execute();
            } else {
                Constants.isSync = false;
                if (views != null) {
                    views.onHideProgress();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), mcontext);
                }
            }
        } else {
            Constants.isSync = false;
            if (views != null) {
                views.onHideProgress();
                Constants.displayMsgReqError(errorBean.getErrorCode(), activity);
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
                    Constants.updateSyncTime(alAssignColl,mcontext,Constants.RTGS_sync,refguid.toString().toUpperCase());
                } catch (Exception exce) {
                    LogManager.writeLogError(Constants.SyncTableHistory + exce.getMessage());
                }
                ConstantsUtils.startAutoSync(mcontext,false);
                Constants.isSync = false;
                if (!Constants.isStoreClosed) {
                    if (views != null) {
                        views.onHideProgress();
                        AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false);
                    }
                    onStart();
                } else {
                    if (views != null) {
                        views.onHideProgress();
                        views.showMsg(activity.getString(R.string.msg_sync_terminated));
                    }
                }
            } else if (i == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
                Constants.isSync = false;
                try {
                    OfflineManager.getAuthorizations(mcontext);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                Constants.setSyncTime(mcontext,refguid.toString().toUpperCase());
                ConstantsUtils.startAutoSync(mcontext,false);
                if (views != null) {
                    views.onHideProgress();
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false);
                }
                onStart();
            }
        }
    }
    private String[] tempRODevList = null;
    private int pendingCount=0;
    private int removeCount=0;
    private ArrayList<String> pendingCollectionList = new ArrayList<>();

    /**
     * This method will post MTP Data to
     * backend on immediate after saving the data in vault if networdk available
     */
    private void runOnBackground(boolean isUpdate) {
        if(UtilConstants.isNetworkAvailable(mcontext)){
            if (Constants.iSAutoSync || Constants.isLocationSync) {
                if(Constants.iSAutoSync) {
                    LogManager.writeLogError(mcontext.getString(R.string.alert_auto_sync_is_progress));
                }else  if(Constants.isLocationSync){
                    LogManager.writeLogError(mcontext.getString(R.string.alert_auto_sync_location_is_progress));
                }
            }else {
                Constants.isSync = true;
                Constants.isBackGroundSync = true;
                SharedPreferences sharedPreferences = mcontext.getSharedPreferences(Constants.PREFS_NAME, 0);
                Set<String> set = sharedPreferences.getStringSet(Constants.RTGSDataValt, null);
                if (set != null && !set.isEmpty()) {
                    tempRODevList = new String[set.size()];
                    for (String s : set) {
                        tempRODevList[pendingCount] = s;
                        pendingCount++;
                    }
                }
                if (tempRODevList != null) {
                    final int finalPendingCount = pendingCount;
                    if (!isUpdate) {
                        new SyncFromDataValtAsyncTask(mcontext, tempRODevList, new UIListener() {
                            @Override
                            public void onRequestError(int i, Exception e) {

                            }

                            @Override
                            public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                                if (i == Operation.Create.getValue() && finalPendingCount > 0) {
                                    removePendingList();
                                }
                                if ((i == Operation.Create.getValue()) && (removeCount == pendingCount)) {
                                    refreshList();
                                } else if (i == Operation.OfflineRefresh.getValue()) {
                                    Constants.isBackGroundSync = false;
                                    try {
                                        Constants.updateLastSyncTimeToTable(pendingCollectionList, mcontext, Constants.UpLoad,refguid.toString().toUpperCase());
                                        ConstantsUtils.startAutoSync(mcontext, false);
//                                        ConstantsUtils.serviceReSchedule(mcontext, true);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, new DialogCallBack() {
                            @Override
                            public void clickedStatus(boolean clickedStatus) {

                            }
                        }).execute();
                    }else{
                        new SyncFromDataValtAsyncTask(mcontext, tempRODevList, null,new OnlineODataInterface(){
                            @Override
                            public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
                                try {
                                    removePendingList();
                                    refreshList();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            @Override
                            public void responseFailed(ODataRequestExecution oDataRequestExecution, String s, Bundle bundle) {
                                LogManager.writeLogError("Background sync failed");
                            }
                        }, new DialogCallBack() {
                            @Override
                            public void clickedStatus(boolean clickedStatus) {

                            }
                        }).execute();
                    }
                }
            }
        }
    }
    private void removePendingList(){
        try {
            if(tempRODevList!=null && tempRODevList.length>0) {
                Constants.removeDeviceDocNoFromSharedPref(mcontext, Constants.RTGSDataValt, tempRODevList[removeCount]);
                removeCount++;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
    private void refreshList() {
        if (UtilConstants.isNetworkAvailable(mcontext)) {
            try {
                pendingCollectionList.add(Constants.CollectionPlan);
                pendingCollectionList.add(Constants.CollectionPlanItem);
                pendingCollectionList.add(Constants.CollectionPlanItemDetails);
                String concatCollectionStr="";
                for (int incVal = 0; incVal < pendingCollectionList.size(); incVal++) {
                    if (incVal == 0 && incVal == pendingCollectionList.size() - 1) {
                        concatCollectionStr = concatCollectionStr + pendingCollectionList.get(incVal);
                    } else if (incVal == 0) {
                        concatCollectionStr = concatCollectionStr + pendingCollectionList.get(incVal) + ", ";
                    } else if (incVal == pendingCollectionList.size() - 1) {
                        concatCollectionStr = concatCollectionStr + pendingCollectionList.get(incVal);
                    } else {
                        concatCollectionStr = concatCollectionStr + pendingCollectionList.get(incVal) + ", ";
                    }
                }
                new RefreshAsyncTask(mcontext, concatCollectionStr, new UIListener() {
                    @Override
                    public void onRequestError(int i, Exception e) {
                        LogManager.writeLogError("RTGS Error post : "+e.toString());
                    }

                    @Override
                    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                        Constants.updateLastSyncTimeToTable(pendingCollectionList, mcontext, Constants.UpLoad,refguid.toString().toUpperCase());
                        ConstantsUtils.startAutoSync(mcontext, false);
//                        ConstantsUtils.serviceReSchedule(mcontext, true);
                        Constants.isBackGroundSync=false;
                    }
                }).execute();
            } catch (Exception e) {
                TraceLog.e(Constants.SyncOnRequestSuccess, e);
            }
        } else {
            Constants.iSAutoSync = false;
            LogManager.writeLogError(mcontext.getString(R.string.data_conn_lost_during_sync));
        }
    }
}
