package com.rspl.sf.msfa.mtp;

import android.app.Activity;
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
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.AsyncTaskCallBack;
import com.rspl.sf.msfa.interfaces.DialogCallBack;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.mtp.approval.MTPApprovalBean;
import com.rspl.sf.msfa.mtp.create.MTPCreateActivity;
import com.rspl.sf.msfa.so.SOUtils;
import com.rspl.sf.msfa.soapproval.OpenOnlineManagerStore;
import com.rspl.sf.msfa.store.GetOnlineODataInterface;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.store.OnlineManager;
import com.rspl.sf.msfa.store.OnlineStoreListener;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.json.JSONArray;
import org.json.JSONObject;

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

public class MTPCurrentPrsenterImpl implements MTPCurrentPresenter, UIListener, GetOnlineODataInterface {

    ArrayList<MTPHeaderBean> mtpRoutePlanBeanArrayList;
    private Activity mContext;
    private MTPCurrentView views = null;
    private String comingFrom = "";
    private List<String> alAssignColl = new ArrayList<>();
    private String spGUID = "";
    private ArrayList<MTPHeaderBean> finalDisplayList = new ArrayList<>();
    private boolean isAsmLogin = false;
    private GUID refguid = null;


    public MTPCurrentPrsenterImpl(Activity activity, MTPCurrentView views, String comingFrom, String spGUID, boolean isAsmLogin) {
        this.mContext = activity;
        this.views = views;
        this.comingFrom = comingFrom;
        this.spGUID = spGUID;
        this.isAsmLogin = isAsmLogin;
    }

    public MTPCurrentPrsenterImpl(Activity activity, MTPCurrentView views, String comingFrom, String spGUID, boolean isAsmLogin, ArrayList<MTPHeaderBean> finalDisplayList) {
        this.mContext = activity;
        this.views = views;
        this.comingFrom = comingFrom;
        this.spGUID = spGUID;
        this.isAsmLogin = isAsmLogin;
        this.finalDisplayList = finalDisplayList;
    }

    @Override
    public void onStart() {
        if (views != null) {
            views.onProgress();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Calendar currentCal = Calendar.getInstance();
                int week = currentCal.get(Calendar.WEEK_OF_MONTH);
                int displayPos = 0;
                try {
         /*   String startDate = SOUtils.getStartDate(activity, activity.getString(R.string.so_filter_today));
            String endDate = SOUtils.getEndDate(activity, activity.getString(R.string.so_filter_today));*/
                    String startDate = "";
                    String endDate = "";
                    String qry = "";
                    if (comingFrom.equalsIgnoreCase(ConstantsUtils.MONTH_CURRENT) || comingFrom.equalsIgnoreCase(ConstantsUtils.MTP_SUBORDINATE_CURRENT)) {
                        startDate = SOUtils.getStartDate(mContext, mContext.getString(R.string.so_filter_current_mont));
                        endDate = SOUtils.getEndDate(mContext, mContext.getString(R.string.so_filter_current_mont));
                        qry = Constants.RouteSchedules + "?$filter=ValidTo ge datetime'" + startDate + "' and ValidFrom le datetime'" + endDate + "' and SalesPersonID eq guid'" + spGUID + "'";
                    } else {
                        startDate = SOUtils.getStartDate(mContext, mContext.getString(R.string.so_filter_next_mont));
                        endDate = SOUtils.getEndDate(mContext, mContext.getString(R.string.so_filter_next_mont));
                        qry = Constants.RouteSchedules + "?$filter=ValidTo ge datetime'" + startDate + "' and ValidFrom le datetime'" + endDate + "' and SalesPersonID eq guid'" + spGUID + "'";
                    }
//            String qry = Constants.RoutePlans + "?$select=CustomerNo,CustomerName,VisitDate &$filter=VisitDate ge datetime'" + startDate + "' and VisitDate le datetime'" + endDate + "'";

                    finalDisplayList = OfflineManager.getMTPCurrentPlane(mContext, qry, comingFrom, startDate, endDate, isAsmLogin);
                    if (comingFrom.equalsIgnoreCase(ConstantsUtils.MONTH_CURRENT)) {
                        for (MTPHeaderBean mtpHeaderBean : finalDisplayList) {
                            if (mtpHeaderBean.isTitle() && mtpHeaderBean.getWeek() == week) {
                                break;
                            }
                            displayPos++;
                        }
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                final int finalDisplayPos = displayPos;
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (views != null) {
                            views.displayData(finalDisplayList);
                            views.onHideProgress();
                            if (finalDisplayPos > 0)
                                views.displayViewPost(finalDisplayPos);
                            views.displayLastRefreshedTime(UtilConstants.getLastRefreshedTime(mContext, ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.RouteSchedules, Constants.TimeStamp, mContext)));
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
    public void onRefresh() {
        if (UtilConstants.isNetworkAvailable(mContext)) {
            if (Constants.iSAutoSync) {
                if (views != null) {
                    views.onHideProgress();
                    views.showMsg(mContext.getString(R.string.alert_auto_sync_is_progress));
                }
                return;
            }
            if (views != null) {
                views.onProgress();
            }
            try {
                alAssignColl.add(Constants.RouteSchedules);
                alAssignColl.add(Constants.RouteSchedulePlans);
                alAssignColl.add(Constants.ConfigTypsetTypeValues);
                String syncColl = TextUtils.join(", ", alAssignColl);
                Constants.isSync = true;
                refguid = GUID.newRandom();
                Constants.updateStartSyncTime(mContext, Constants.MTP_sync, Constants.StartSync, refguid.toString().toUpperCase());
                new RefreshAsyncTask(mContext, syncColl, this).execute();
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
                views.showMsg(mContext.getString(R.string.no_network_conn));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MTPCreateActivity.REQUEST_CODE_CREATE_NEXT_MONTH) {
            if (resultCode == Activity.RESULT_OK) {
                int indexPos = data.getIntExtra(ConstantsUtils.EXTRA_POS, -1);
                MTPHeaderBean mtpResultHeaderBean = (MTPHeaderBean) data.getSerializableExtra(Constants.EXTRA_BEAN);
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
    public void onSaveData(String saveType) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        String rollType = sharedPreferences.getString(Constants.USERROLE, "");


        Hashtable<String, String> masterHeaderTable = new Hashtable<>();
        GUID guid = GUID.newRandom();
        String headerGuid = guid.toString36();
        String doc_no = (System.currentTimeMillis() + "").substring(3, 10);
        masterHeaderTable.put(Constants.SalesPersonID, spGUID);
        masterHeaderTable.put(Constants.IS_UPDATE, "");

        String period = ConstantsUtils.getMonth();
        String periodYear = SOUtils.getYearFromCalender(mContext, mContext.getString(R.string.so_filter_current_mont));
        if (comingFrom.equalsIgnoreCase(ConstantsUtils.MONTH_NEXT) || comingFrom.equalsIgnoreCase(ConstantsUtils.MTP_SUBORDINATE_NEXT)) {
            period = ConstantsUtils.getNextMonth();
//                        periodYear = ConstantsUtils.getYear();
            periodYear = SOUtils.getYearFromCalender(mContext, mContext.getString(R.string.so_filter_next_mont));
        }

        if (saveType.equalsIgnoreCase(mContext.getString(R.string.mtp_draft)))
            masterHeaderTable.put(Constants.Testrun, "D");
        else
            masterHeaderTable.put(Constants.Testrun, "");
        if (comingFrom.equalsIgnoreCase(ConstantsUtils.MONTH_CURRENT) || comingFrom.equalsIgnoreCase(ConstantsUtils.MTP_SUBORDINATE_CURRENT)) {
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
        masterHeaderTable.put(Constants.EntityType, Constants.RouteSchedules);

        ArrayList<HashMap<String, String>> itemTable = new ArrayList<>();
        for (MTPHeaderBean mtpHeaderBean : finalDisplayList) {
            if (!TextUtils.isEmpty(mtpHeaderBean.getActivityID())) {
                if (!TextUtils.isEmpty(mtpHeaderBean.getRouteSchGUID()) && !TextUtils.isEmpty(mtpHeaderBean.getIsUpdate())) {
                    headerGuid = mtpHeaderBean.getRouteSchGUID();
                    masterHeaderTable.put(Constants.IS_UPDATE, "X");
                    masterHeaderTable.put(Constants.RoutId, mtpHeaderBean.getRoutId());
                    masterHeaderTable.put(Constants.CreatedBy, mtpHeaderBean.getCreatedBy());
                    Calendar createdOnCal = ConstantsUtils.convertCalenderToDisplayDateFormat(mtpHeaderBean.getCreatedOn(), "dd-MMM-yyyy");
                    createdOnCal.set(Calendar.MILLISECOND, 0);
                    createdOnCal.set(Calendar.SECOND, 0);
                    createdOnCal.set(Calendar.MINUTE, 0);
                    createdOnCal.set(Calendar.HOUR, 0);
                    masterHeaderTable.put(Constants.CreatedOn, ConstantsUtils.convertCalenderToDisplayDateFormat(createdOnCal, "yyyy-MM-dd'T'HH:mm:ss"));
                } else {
                    masterHeaderTable.put(Constants.IS_UPDATE, "");
                }
                if (!TextUtils.isEmpty(mtpHeaderBean.getDeviceNo())) {
                    doc_no = mtpHeaderBean.getDeviceNo();
                }
                if (!TextUtils.isEmpty(mtpHeaderBean.getApprovalStatus()) && mtpHeaderBean.getApprovalStatus().equalsIgnoreCase(Constants.RejectedStatusID)) {
                    masterHeaderTable.put(Constants.ApprovalStatus, "");
                    masterHeaderTable.put(Constants.ApprovalStatusDs, "");
                } else {
                    masterHeaderTable.put(Constants.ApprovalStatus, mtpHeaderBean.getApprovalStatus());
                    masterHeaderTable.put(Constants.ApprovalStatusDs, mtpHeaderBean.getApprovalStatusDs());
                }
                for (MTPRoutePlanBean routePlanBean : mtpHeaderBean.getMTPRoutePlanBeanArrayList()) {
                    HashMap<String, String> dbItemTable = new HashMap<>();

                    if (!TextUtils.isEmpty(routePlanBean.getRouteSchPlanGUID())) {
                        dbItemTable.put(Constants.RouteSchPlanGUID, routePlanBean.getRouteSchPlanGUID());
                    } else {
                        guid = GUID.newRandom();
                        dbItemTable.put(Constants.RouteSchPlanGUID, guid.toString36());
                    }
                    dbItemTable.put(Constants.RouteSchGUID, headerGuid);
                    Calendar calendar = ConstantsUtils.convertCalenderToDisplayDateFormat(mtpHeaderBean.getFullDate(), "dd-MMM-yyyy");
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.HOUR, 0);
                    dbItemTable.put(Constants.VisitDate, ConstantsUtils.convertCalenderToDisplayDateFormat(calendar, "yyyy-MM-dd'T'HH:mm:ss"));
                    dbItemTable.put(Constants.ActivityID, routePlanBean.getActivityId());
                    dbItemTable.put(Constants.ActivityDesc, routePlanBean.getActivityDec());

                    if (isAsmLogin || rollType.equalsIgnoreCase("Z3")) {
                        dbItemTable.put(Constants.SalesDistrict, routePlanBean.getSalesDistrict());
                        dbItemTable.put(Constants.SalesDistrictDesc, routePlanBean.getSalesDistrictDesc());
                    } else {
                        dbItemTable.put(Constants.VisitCPGUID, routePlanBean.getCustomerNo());
                        dbItemTable.put(Constants.VisitCPName, routePlanBean.getCustomerName());
                    }
                    dbItemTable.put(Constants.Remarks, routePlanBean.getRemarks());
                    itemTable.add(dbItemTable);
                }
            }
        }
        if (itemTable.size() > 0) {
            masterHeaderTable.put(Constants.Month, period);
            masterHeaderTable.put(Constants.Year, periodYear);
            masterHeaderTable.put(Constants.RouteSchGUID, headerGuid);
            Gson gson1 = new Gson();
            String jsonFromMap = "";
            try {
                jsonFromMap = gson1.toJson(itemTable);
            } catch (Exception e) {
                e.printStackTrace();
            }
            masterHeaderTable.put(Constants.RouteSchedulePlans, jsonFromMap);
            Set<String> set = sharedPreferences.getStringSet(Constants.MTPDataValt, null);
            HashSet<String> setTemp = new HashSet<>();
            if (set != null && !set.isEmpty()) {
                Iterator<String> itr = set.iterator();
                while (itr.hasNext()) {
                    setTemp.add(itr.next().toString());
                }
            }
            setTemp.add(doc_no);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(Constants.MTPDataValt, setTemp);
            editor.apply();
            JSONObject jsonHeaderObject = new JSONObject(masterHeaderTable);
            ConstantsUtils.storeInDataVault(doc_no, jsonHeaderObject.toString(),mContext);

            if (views != null) {
                if (TextUtils.isEmpty(masterHeaderTable.get(Constants.IS_UPDATE))) {
                    try {
//                        runOnBackground(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("MTP_CREATE", "FAILED WITH EXCEPTION");
                    }
                    views.showSuccessMsg("MTP Created Successfully");
                } else {
                    try {
//                        runOnBackground(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("MTP_CREATE", "FAILED WITH EXCEPTION");
                    }
                    if (saveType.equalsIgnoreCase(mContext.getString(R.string.mtp_draft)) || saveType.equalsIgnoreCase(mContext.getString(R.string.mtp_save))){
                        views.showSuccessMsg("MTP Created Successfully");
                    }else {
                        views.showSuccessMsg("MTP Updated Successfully");
                    }
                }
            }

        } else {
            if (views != null) {
                views.showMsg("Please enter tour plan details");
            }
        }
    }

    public void onSaveData(boolean currentMonth) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        String rollType = sharedPreferences.getString(Constants.USERROLE, "");


        Hashtable<String, String> masterHeaderTable = new Hashtable<>();
        GUID guid = GUID.newRandom();
        String headerGuid = guid.toString36();
        String doc_no = (System.currentTimeMillis() + "").substring(3, 10);
        masterHeaderTable.put(Constants.SalesPersonID, Constants.getSPGUID(Constants.SPGUID));
        masterHeaderTable.put(Constants.IS_UPDATE, "");

        String period = ConstantsUtils.getMonth();
        String periodYear = SOUtils.getYearFromCalender(mContext, mContext.getString(R.string.so_filter_current_mont));
        if (!currentMonth || comingFrom.equalsIgnoreCase(ConstantsUtils.MONTH_NEXT) || comingFrom.equalsIgnoreCase(ConstantsUtils.MTP_SUBORDINATE_NEXT)) {
            period = ConstantsUtils.getNextMonth();
//                        periodYear = ConstantsUtils.getYear();
            periodYear = SOUtils.getYearFromCalender(mContext, mContext.getString(R.string.so_filter_next_mont));
        }

//        masterHeaderTable.put(Constants.Testrun, "");
        masterHeaderTable.put(Constants.Testrun, "E");
        if (currentMonth || comingFrom.equalsIgnoreCase(ConstantsUtils.MONTH_CURRENT) || comingFrom.equalsIgnoreCase(ConstantsUtils.MTP_SUBORDINATE_CURRENT)) {
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
        masterHeaderTable.put(Constants.EntityType, Constants.RouteSchedules);

        ArrayList<HashMap<String, String>> itemTable = new ArrayList<>();
        for (MTPHeaderBean mtpHeaderBean : finalDisplayList) {
            if (!TextUtils.isEmpty(mtpHeaderBean.getActivityID())) {
                if (!TextUtils.isEmpty(mtpHeaderBean.getRouteSchGUID()) && !TextUtils.isEmpty(mtpHeaderBean.getIsUpdate())) {
                    headerGuid = mtpHeaderBean.getRouteSchGUID();
                    masterHeaderTable.put(Constants.SalesPersonID, finalDisplayList.get(1).getSalesPersonGuid());
                    masterHeaderTable.put(Constants.IS_UPDATE, "X");
                    masterHeaderTable.put(Constants.RoutId, mtpHeaderBean.getRoutId());
                    masterHeaderTable.put(Constants.CreatedBy, mtpHeaderBean.getCreatedBy());
                    Calendar createdOnCal = ConstantsUtils.convertCalenderToDisplayDateFormat(mtpHeaderBean.getCreatedOn(), "dd-MMM-yyyy");
                    createdOnCal.set(Calendar.MILLISECOND, 0);
                    createdOnCal.set(Calendar.SECOND, 0);
                    createdOnCal.set(Calendar.MINUTE, 0);
                    createdOnCal.set(Calendar.HOUR, 0);
                    masterHeaderTable.put(Constants.CreatedOn, ConstantsUtils.convertCalenderToDisplayDateFormat(createdOnCal, "yyyy-MM-dd'T'HH:mm:ss"));
                } else {
                    masterHeaderTable.put(Constants.IS_UPDATE, "");
                }
                if (!TextUtils.isEmpty(mtpHeaderBean.getDeviceNo())) {
                    doc_no = mtpHeaderBean.getDeviceNo();
                }
                if (!TextUtils.isEmpty(mtpHeaderBean.getApprovalStatus()) && mtpHeaderBean.getApprovalStatus().equalsIgnoreCase(Constants.RejectedStatusID)) {
                    masterHeaderTable.put(Constants.ApprovalStatus, "");
                    masterHeaderTable.put(Constants.ApprovalStatusDs, "");
                } else {
                    masterHeaderTable.put(Constants.ApprovalStatus, mtpHeaderBean.getApprovalStatus());
                    masterHeaderTable.put(Constants.ApprovalStatusDs, mtpHeaderBean.getApprovalStatusDs());
                }
                for (MTPRoutePlanBean routePlanBean : mtpHeaderBean.getMTPRoutePlanBeanArrayList()) {
                    HashMap<String, String> dbItemTable = new HashMap<>();

                    if (!TextUtils.isEmpty(routePlanBean.getRouteSchPlanGUID())) {
                        dbItemTable.put(Constants.RouteSchPlanGUID, routePlanBean.getRouteSchPlanGUID());
                    } else {
                        guid = GUID.newRandom();
                        dbItemTable.put(Constants.RouteSchPlanGUID, guid.toString36());
                    }
                    dbItemTable.put(Constants.RouteSchGUID, headerGuid);
                    Calendar calendar = ConstantsUtils.convertCalenderToDisplayDateFormat(mtpHeaderBean.getFullDate(), "dd-MMM-yyyy");
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.HOUR, 0);
                    dbItemTable.put(Constants.VisitDate, ConstantsUtils.convertCalenderToDisplayDateFormat(calendar, "yyyy-MM-dd'T'HH:mm:ss"));
                    dbItemTable.put(Constants.ActivityID, routePlanBean.getActivityId());
                    dbItemTable.put(Constants.ActivityDesc, routePlanBean.getActivityDec());

                    if (isAsmLogin || rollType.equalsIgnoreCase("Z3")) {
                        dbItemTable.put(Constants.SalesDistrict, routePlanBean.getSalesDistrict());
                        dbItemTable.put(Constants.SalesDistrictDesc, routePlanBean.getSalesDistrictDesc());
                    } else {
                        dbItemTable.put(Constants.VisitCPGUID, routePlanBean.getCustomerNo());
                        dbItemTable.put(Constants.VisitCPName, routePlanBean.getCustomerName());
                    }
                    dbItemTable.put(Constants.Remarks, routePlanBean.getRemarks());
                    itemTable.add(dbItemTable);
                }
            }
        }
        if (itemTable.size() > 0) {
            masterHeaderTable.put(Constants.Month, period);
            masterHeaderTable.put(Constants.Year, periodYear);
            masterHeaderTable.put(Constants.RouteSchGUID, headerGuid);
            Gson gson1 = new Gson();
            String jsonFromMap = "";
            try {
                jsonFromMap = gson1.toJson(itemTable);
            } catch (Exception e) {
                e.printStackTrace();
            }
            masterHeaderTable.put(Constants.RouteSchedulePlans, jsonFromMap);
            Set<String> set = sharedPreferences.getStringSet(Constants.MTPDataValt, null);
            HashSet<String> setTemp = new HashSet<>();
            if (set != null && !set.isEmpty()) {
                Iterator<String> itr = set.iterator();
                while (itr.hasNext()) {
                    setTemp.add(itr.next().toString());
                }
            }
            setTemp.add(doc_no);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(Constants.MTPDataValt, setTemp);
            editor.apply();
            JSONObject jsonHeaderObject = new JSONObject(masterHeaderTable);
            ConstantsUtils.storeInDataVault(doc_no, jsonHeaderObject.toString(),mContext);
            if (views != null) {
                views.ShowDialgueProgress();
            }
            runOnBackground(true);
        } else {
            if (views != null) {
                views.showMsg("Please enter tour plan details");
            }
        }
    }

    @Override
    public void onRequestError(int i, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(i, e, mContext);
        if (errorBean.hasNoError()) {
            if (i == Operation.OfflineRefresh.getValue()) {
                Constants.isSync = false;
                if (!Constants.isStoreClosed) {
                    if (views != null) {
                        views.onHideProgress();
                        views.showMsg(mContext.getString(R.string.msg_error_occured_during_sync));
                    }
                } else {
                    if (views != null) {
                        views.onHideProgress();
                        views.showMsg(mContext.getString(R.string.msg_sync_terminated));
                    }
                }
            } else if (i == Operation.GetStoreOpen.getValue()) {
                Constants.isSync = false;
                if (views != null) {
                    views.onHideProgress();
                    views.showMsg(mContext.getString(R.string.msg_error_occured_during_sync));
                }
            }
        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(mContext)) {
                Constants.isSync = true;
                if (views != null) {
                    views.onProgress();
                }
                new RefreshAsyncTask(mContext, "", this).execute();
            } else {
                Constants.isSync = false;
                if (views != null) {
                    views.onHideProgress();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), mContext);
                }
            }
        } else {
            Constants.isSync = false;
            if (views != null) {
                views.onHideProgress();
                Constants.displayMsgReqError(errorBean.getErrorCode(), mContext);
            }
        }
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
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
                Constants.updateSyncTime(alAssignColl, mContext, Constants.MTP_sync, refguid.toString().toUpperCase());
            } catch (Exception exce) {
                LogManager.writeLogError(Constants.SyncTableHistory + exce.getMessage());
            }
            ConstantsUtils.startAutoSync(mContext, false);
            Constants.isSync = false;
            if (!Constants.isStoreClosed) {
                if (views != null) {
                    views.onHideProgress();
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, mContext, BuildConfig.APPLICATION_ID, false);
                }
                onStart();
            } else {
                if (views != null) {
                    views.onHideProgress();
                    views.showMsg(mContext.getString(R.string.msg_sync_terminated));
                }
            }
        } else if (i == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
            Constants.isSync = false;
            try {
                OfflineManager.getAuthorizations(mContext);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setSyncTime(mContext, refguid.toString().toUpperCase());
            ConstantsUtils.startAutoSync(mContext, false);
            if (views != null) {
                views.onHideProgress();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, mContext, BuildConfig.APPLICATION_ID, false);
            }
            onStart();
        }
    }

    @Override
    public void mtpDetails(MTPApprovalBean mtpApprovalBean) {
        String qry = Constants.RouteSchedules + "(guid'" + mtpApprovalBean.getEntityKey() + "')?$expand=RouteSchedulePlans";
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_RESOURCE_PATH, qry);
        bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 1);
        bundle.putInt(Constants.BUNDLE_OPERATION, Operation.GetRequest.getValue());
        bundle.putBoolean(Constants.BUNDLE_SESSION_REQUIRED, false);
        bundle.putBoolean(Constants.BUNDLE_SESSION_URL_REQUIRED, false);
        try {
            if (views != null)
                views.onProgress();
            OnlineManager.requestOnline(this, bundle, mContext);
        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt1 + " : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void responseSuccess(final ODataRequestExecution oDataRequestExecution, List<ODataEntity> entities, int operation, int requestCode, String resourcePath, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        switch (type) {
            case 1:
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (views != null) {
                            views.onHideProgress();
                            mtpRoutePlanBeanArrayList = OnlineManager.getMTPApprovalListDetail(oDataRequestExecution, isAsmLogin);
                            if (!mtpRoutePlanBeanArrayList.isEmpty()) {
                                //  views.openDetailScreen(mtpRoutePlanBeanArrayList.get(0).getMTPRoutePlanBeanArrayList());
                            }
                        }
                    }
                });
                break;

            case 2:
                Calendar currentCal = Calendar.getInstance();
                int week = currentCal.get(Calendar.WEEK_OF_MONTH);
                int displayPos = 0;
                try {
                    String startDate = "";
                    String endDate = "";
                    String qry = "";
                    if (comingFrom.equalsIgnoreCase(ConstantsUtils.MONTH_CURRENT) || comingFrom.equalsIgnoreCase(ConstantsUtils.MTP_SUBORDINATE_CURRENT)) {
                        startDate = SOUtils.getStartDate(mContext, mContext.getString(R.string.so_filter_current_mont));
                        endDate = SOUtils.getEndDate(mContext, mContext.getString(R.string.so_filter_current_mont));
                        qry = Constants.RouteSchedules + "?$filter=ValidTo ge datetime'" + startDate + "' and ValidFrom le datetime'" + endDate + "' and SalesPersonID eq guid'" + spGUID + "'";

                    } else {
                        startDate = SOUtils.getStartDate(mContext, mContext.getString(R.string.so_filter_next_mont));
                        endDate = SOUtils.getEndDate(mContext, mContext.getString(R.string.so_filter_next_mont));
                        qry = Constants.RouteSchedules + "?$filter=ValidTo ge datetime'" + startDate + "' and ValidFrom le datetime'" + endDate + "' and SalesPersonID eq guid'" + spGUID + "'";
                    }
                    String rschGuid  = OfflineManager.getValidFromAndToFromEntity(startDate,endDate,entities);

                    if(!TextUtils.isEmpty(rschGuid)) {

                        String planqry = Constants.RouteSchedules + "(guid'" + rschGuid + "')?$expand=RouteSchedulePlans";
                        Bundle bundlersch = new Bundle();
                        bundlersch.putString(Constants.BUNDLE_RESOURCE_PATH, planqry);
                        bundlersch.putInt(Constants.BUNDLE_REQUEST_CODE, 3);
                        bundlersch.putInt(Constants.BUNDLE_OPERATION, Operation.GetRequest.getValue());
                        bundlersch.putBoolean(Constants.BUNDLE_SESSION_REQUIRED, false);
                        bundlersch.putBoolean(Constants.BUNDLE_SESSION_URL_REQUIRED, false);
                        try {
                            OnlineManager.requestOnline(this, bundlersch, mContext);
                        } catch (Exception e) {
                            LogManager.writeLogError(Constants.error_txt1 + " : " + e.getMessage());
                            e.printStackTrace();
                        }
                    }else{
                        int displayPositem = 0;
                        /*Calendar currentCalItems = Calendar.getInstance();
                        int weekitems = currentCalItems.get(Calendar.WEEK_OF_MONTH);
                        finalDisplayList = OfflineManager.getMTPCurrentPlane(mContext, qry, comingFrom, startDate, endDate, isAsmLogin);
                   //     finalDisplayList = OnlineManager.getMTPApprovalListDetail(oDataRequestExecution, OfflineManager.isASMUser());;
                        if (comingFrom.equalsIgnoreCase(ConstantsUtils.MONTH_CURRENT)) {
                            for (MTPHeaderBean mtpHeaderBean : finalDisplayList) {
                                if (mtpHeaderBean.isTitle() && mtpHeaderBean.getWeek() == weekitems) {
                                    break;
                                }
                                displayPositem++;
                            }
                        }*/
                        finalDisplayList = OfflineManager.getMTPCurrentPlane(mContext, comingFrom, startDate, endDate, isAsmLogin);

                        final int finalDisplayPos = displayPositem;
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (views != null) {
                                    views.displayData(finalDisplayList);
                                    views.onHideProgress();
                                    if (finalDisplayPos > 0)
                                        views.displayViewPost(finalDisplayPos);
                                    views.displayLastRefreshedTime(UtilConstants.getLastRefreshedTime(mContext, ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.RouteSchedules, Constants.TimeStamp, mContext)));
                                }
                            }
                        });
                    }

                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }

                break;
            case 3:
                int displayPositem = 0;
                Calendar currentCalItems = Calendar.getInstance();
                int weekitems = currentCalItems.get(Calendar.WEEK_OF_MONTH);
                finalDisplayList = OnlineManager.getMTPApprovalListDetail(oDataRequestExecution, OfflineManager.isASMUser());;
                if (comingFrom.equalsIgnoreCase(ConstantsUtils.MONTH_CURRENT)) {
                    for (MTPHeaderBean mtpHeaderBean : finalDisplayList) {
                        if (mtpHeaderBean.isTitle() && mtpHeaderBean.getWeek() == weekitems) {
                            break;
                        }
                        displayPositem++;
                    }
                }
                final int finalDisplayPos = displayPositem;
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (views != null) {
                            views.displayData(finalDisplayList);
                            views.onHideProgress();
                            if (finalDisplayPos > 0)
                                views.displayViewPost(finalDisplayPos);
                            views.displayLastRefreshedTime(UtilConstants.getLastRefreshedTime(mContext, ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.RouteSchedules, Constants.TimeStamp, mContext)));
                        }
                    }
                });
                break;

             /*   activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (imtpApprovalViewPresenter != null) {
                            imtpApprovalViewPresenter.hideProgressDialog();
                            mtpRoutePlanBeanArrayList = OnlineManager.getMTPApprovalListDetail(oDataRequestExecution, OfflineManager.isASMUser());
                            if (!mtpRoutePlanBeanArrayList.isEmpty()) {
                                imtpApprovalViewPresenter.openDetailScreen(mtpRoutePlanBeanArrayList, mtpApprovalBean);
                            }
                        }
                    }
                });
                break;*/
        }
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, int operation, int requestCode, String resourcePath, final String errorMsg, Bundle bundle) {
        try {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (views != null) {
                        views.onHideProgress();
                        views.showMsg(errorMsg);
                    }
                }
            });
        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + " : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String[] tempRODevList = null;
    private int pendingCount = 0;
    private int removeCount = 0;
    private ArrayList<String> pendingCollectionList = new ArrayList<>();

    /**
     * This method will post MTP Data to
     * backend on immediate after saving the data in vault if networdk available
     */
    private void runOnBackground(boolean isUpdate) {
        pendingCount = 0;
        refguid = GUID.newRandom();
        Constants.updateStartSyncTime(mContext, Constants.MTP_sync, Constants.StartSync, refguid.toString().toUpperCase());
        if (UtilConstants.isNetworkAvailable(mContext)) {
            if (Constants.iSAutoSync || Constants.isLocationSync) {
                if (Constants.iSAutoSync) {
                    LogManager.writeLogError(mContext.getString(R.string.alert_auto_sync_is_progress));
                } else if (Constants.isLocationSync) {
                    LogManager.writeLogError(mContext.getString(R.string.alert_auto_sync_location_is_progress));
                }
            } else {
                Constants.isSync = true;
                Constants.isBackGroundSync = true;
                SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
                Set<String> set = sharedPreferences.getStringSet(Constants.MTPDataValt, null);
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
                        new SyncFromDataValtAsyncTask(mContext, tempRODevList, new UIListener() {
                            @Override
                            public void onRequestError(int i, Exception e) {
                                Constants.isBackGroundSync = false;
                                if (views != null) {
                                    views.HideDialgueProgress();
                                    views.showMsg(e.toString());
                                }
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
                                        Constants.updateLastSyncTimeToTable(pendingCollectionList, mContext, Constants.UpLoad, refguid.toString().toUpperCase());
                                        ConstantsUtils.startAutoSync(mContext, false);
//                                        ConstantsUtils.serviceReSchedule(mContext, true);
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
                    } else {
                        new SyncFromDataValtAsyncTask(mContext, tempRODevList,  new UIListener() {
                            @Override
                            public void onRequestSuccess(int i, String s){
                                if (i == Operation.Create.getValue() && finalPendingCount > 0) {
                                removePendingList();
                            }
                                if ((i == Operation.Create.getValue()) && (removeCount == pendingCount)) {
                                refreshList();
                            } else if (i == Operation.OfflineRefresh.getValue()) {
                                Constants.isBackGroundSync = false;
                                try {
                                    Constants.updateLastSyncTimeToTable(pendingCollectionList, mContext, Constants.UpLoad, refguid.toString().toUpperCase());
                                    ConstantsUtils.startAutoSync(mContext, false);
//                                        ConstantsUtils.serviceReSchedule(mContext, true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            }

                            public void onRequestError(int i, Exception e) {
                                Constants.isBackGroundSync = false;
                                if (views != null) {
                                    views.HideDialgueProgress();
                                    views.showMsg(e.toString());
                                }
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

    private void removePendingList() {
        try {
            if (tempRODevList != null && tempRODevList.length > 0) {
                Constants.removeDeviceDocNoFromSharedPref(mContext, Constants.MTPDataValt, tempRODevList[removeCount]);
                removeCount++;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private void refreshList() {
        if (UtilConstants.isNetworkAvailable(mContext)) {
            try {
                pendingCollectionList.add(Constants.RouteSchedules);
                pendingCollectionList.add(Constants.RouteSchedulePlans);
                String concatCollectionStr = "";
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
                new RefreshAsyncTask(mContext, concatCollectionStr, new UIListener() {
                    @Override
                    public void onRequestError(int i, Exception e) {
                        Constants.isBackGroundSync = false;
                        if (views != null) {
                            views.HideDialgueProgress();
                            views.showMsg(e.toString());
                        }
                    }

                    @Override
                    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                        if (i == Operation.OfflineRefresh.getValue()) {
                            Constants.isBackGroundSync = false;
                            try {
                                Constants.updateLastSyncTimeToTable(pendingCollectionList, mContext, Constants.MTP_sync, refguid.toString().toUpperCase());
                                ConstantsUtils.startAutoSync(mContext, false);
//                                ConstantsUtils.serviceReSchedule(mContext, true);
                                if (views != null) {
                                    views.HideDialgueProgress();
                                    views.editAndApprove();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).execute();
            } catch (Exception e) {
                TraceLog.e(Constants.SyncOnRequestSuccess, e);
            }
        } else {
            Constants.iSAutoSync = false;
            LogManager.writeLogError(mContext.getString(R.string.data_conn_lost_during_sync));
        }
    }

    public void getMTPOnlineBySPGuid() {

        if (views != null) {
            views.onProgress();
        }
        if (UtilConstants.isNetworkAvailable(mContext)) {
            new AsyncTaskSalesPersonsMTP().execute();
        } else {
            if (views != null) {
                views.onHideProgress();
                views.displayData(finalDisplayList);
                views.showMsg(mContext.getString(R.string.err_no_network));
            }
        }

    }

    private String mtpQuery = "";
    Bundle requestbundle = null;

    private class AsyncTaskSalesPersonsMTP extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //get SalesPersons list from online call
            try {
//                OnlineStoreListener openListener = OnlineStoreListener.getInstance();
                /* OnlineODataStore store = openListener.getStore();*/
                Log.d("SalesPerson", "Opening Store");
                SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
                Calendar currentCal = Calendar.getInstance();
                int week = currentCal.get(Calendar.WEEK_OF_MONTH);
                int displayPos = 0;
         /*   String startDate = SOUtils.getStartDate(activity, activity.getString(R.string.so_filter_today));
            String endDate = SOUtils.getEndDate(activity, activity.getString(R.string.so_filter_today));*/
                String startDate = "";
                String endDate = "";
                if (comingFrom.equalsIgnoreCase(ConstantsUtils.MONTH_CURRENT) || comingFrom.equalsIgnoreCase(ConstantsUtils.MTP_SUBORDINATE_CURRENT)) {
                    startDate = SOUtils.getStartDate(mContext, mContext.getString(R.string.so_filter_current_mont));
                    endDate = SOUtils.getEndDate(mContext, mContext.getString(R.string.so_filter_current_mont));
                    mtpQuery = Constants.RouteSchedules + "?$filter=SalesPersonID%20eq%20guid'" + spGUID + "'%20&%20ValidFrom%20le%20datetime'" + endDate + "'%20&%20ValidTo%20ge%20datetime'" + startDate + "'";
                } else {
                    startDate = SOUtils.getStartDate(mContext, mContext.getString(R.string.so_filter_next_mont));
                    endDate = SOUtils.getEndDate(mContext, mContext.getString(R.string.so_filter_next_mont));
                    mtpQuery = Constants.RouteSchedules + "?$filter=SalesPersonID%20eq%20guid'" + spGUID + "'%20&%20ValidFrom%20le%20datetime'" + endDate + "'%20&%20ValidTo%20ge%20datetime'" + startDate + "'";
                }


                try {

                    OnlineManager.doOnlineGetRequest(mtpQuery, mContext, iReceiveEvent -> {
                        if (iReceiveEvent.getResponseStatusCode()==200){
                            JSONObject jsonObject = OnlineManager.getJSONBody(iReceiveEvent);
                            JSONArray jsonArray = OnlineManager.getJSONArrayBody(jsonObject);
                            Calendar currentCal1 = Calendar.getInstance();
                            int week1 = currentCal1.get(Calendar.WEEK_OF_MONTH);
                            int displayPos1 = 0;
                            try {
                                String startDate1 = "";
                                String endDate1 = "";
                                String qry = "";
                                if (comingFrom.equalsIgnoreCase(ConstantsUtils.MONTH_CURRENT) || comingFrom.equalsIgnoreCase(ConstantsUtils.MTP_SUBORDINATE_CURRENT)) {
                                    startDate1 = SOUtils.getStartDate1(mContext, mContext.getString(R.string.so_filter_current_mont));
                                    endDate1 = SOUtils.getEndDate1(mContext, mContext.getString(R.string.so_filter_current_mont));
                                    qry = Constants.RouteSchedules + "?$filter=ValidTo%20ge%20datetime'" + startDate1 + "'%20and%20ValidFrom%20le%20datetime'" + endDate1 + "'%20and%20SalesPersonID%20eq%20guid'" + spGUID + "'";

                                } else {
                                    startDate1 = SOUtils.getStartDate1(mContext, mContext.getString(R.string.so_filter_next_mont));
                                    endDate1 = SOUtils.getEndDate1(mContext, mContext.getString(R.string.so_filter_next_mont));
                                    qry = Constants.RouteSchedules + "?$filter=ValidTo%20ge%20datetime'" + startDate1 + "'%20and%20ValidFrom%20le%20datetime'" + endDate1 + "'%20and%20SalesPersonID%20eq%20guid'" + spGUID + "'";
                                }
                                String rschGuid  = OfflineManager.getValidFromAndToFromEntity(startDate1,endDate1,jsonArray);

                                if(!TextUtils.isEmpty(rschGuid)) {
                                    String planqry = Constants.RouteSchedules + "(guid'" + rschGuid + "')?$expand=RouteSchedulePlans";

                                    try {

                                        OnlineManager.doOnlineGetRequest(planqry, mContext, iReceiveEvent1 -> {
                                            if (iReceiveEvent1.getResponseStatusCode()==200){
                                                JSONObject jsonObject1 = OnlineManager.getJSONBody(iReceiveEvent1);
                                                JSONArray jsonArray1 = OnlineManager.getJSONArrayBody(jsonObject1);
                                                int displayPositem = 0;
                                                Calendar currentCalItems = Calendar.getInstance();
                                                int weekitems = currentCalItems.get(Calendar.WEEK_OF_MONTH);
                                                finalDisplayList = OnlineManager.getMTPApprovalListDetail(jsonObject1, OfflineManager.isASMUser());;
                                                if (comingFrom.equalsIgnoreCase(ConstantsUtils.MONTH_CURRENT)) {
                                                    for (MTPHeaderBean mtpHeaderBean : finalDisplayList) {
                                                        if (mtpHeaderBean.isTitle() && mtpHeaderBean.getWeek() == weekitems) {
                                                            break;
                                                        }
                                                        displayPositem++;
                                                    }
                                                }
                                                final int finalDisplayPos = displayPositem;
                                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (views != null) {
                                                            views.displayData(finalDisplayList);
                                                            views.onHideProgress();
                                                            if (finalDisplayPos > 0)
                                                                views.displayViewPost(finalDisplayPos);
                                                            views.displayLastRefreshedTime(UtilConstants.getLastRefreshedTime(mContext, ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.RouteSchedules, Constants.TimeStamp, mContext)));
                                                        }
                                                    }
                                                });
//
                                            }else {
                                                String errorMsg="";
                                                try {
                                                    errorMsg = Constants.getErrorMessage(iReceiveEvent,mContext);
                                                    String finalErrorMsg = errorMsg;
                                                    ((Activity) mContext).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (views != null) {
                                                                views.displayData(finalDisplayList);
                                                                views.onHideProgress();
                                                                views.showMsg(finalErrorMsg);
                                                            }
                                                        }
                                                    });
                                                    LogManager.writeLogError(errorMsg);
                                                } catch (Throwable e) {
                                                    e.printStackTrace();
                                                    ((Activity) mContext).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (views != null) {
                                                                views.displayData(finalDisplayList);
                                                                views.onHideProgress();
                                                                views.showMsg(e.toString());
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
                                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (views != null) {
                                                        views.displayData(finalDisplayList);
                                                        views.onHideProgress();
                                                        views.showMsg(finalErrormessage);
                                                    }
                                                }
                                            });
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        ConstantsUtils.printErrorLog(e.getMessage());
                                        ((Activity) mContext).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (views != null) {
                                                    views.displayData(finalDisplayList);
                                                    views.onHideProgress();
                                                    views.showMsg(e.toString());
                                                }
                                            }
                                        });
                                    }
                                }else{
                                    int displayPositem = 0;
                                    finalDisplayList = OfflineManager.getMTPCurrentPlane(mContext, comingFrom, startDate1, endDate1, isAsmLogin);

                                    final int finalDisplayPos = displayPositem;
                                    ((Activity) mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (views != null) {
                                                views.displayData(finalDisplayList);
                                                views.onHideProgress();
                                                if (finalDisplayPos > 0)
                                                    views.displayViewPost(finalDisplayPos);
                                                views.displayLastRefreshedTime(UtilConstants.getLastRefreshedTime(mContext, ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.RouteSchedules, Constants.TimeStamp, mContext)));
                                            }
                                        }
                                    });
                                }

                            } catch (OfflineODataStoreException e) {
                                e.printStackTrace();
                            }
//
                        }else {
                            String errorMsg="";
                            try {
                                errorMsg = Constants.getErrorMessage(iReceiveEvent,mContext);
                                String finalErrorMsg = errorMsg;
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (views != null) {
                                            views.displayData(finalDisplayList);
                                            views.onHideProgress();
                                            views.showMsg(finalErrorMsg);
                                        }
                                    }
                                });
                                LogManager.writeLogError(errorMsg);
                            } catch (Throwable e) {
                                e.printStackTrace();
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (views != null) {
                                            views.displayData(finalDisplayList);
                                            views.onHideProgress();
                                            views.showMsg(e.toString());
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
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (views != null) {
                                    views.displayData(finalDisplayList);
                                    views.onHideProgress();
                                    views.showMsg(finalErrormessage);
                                }
                            }
                        });
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    ConstantsUtils.printErrorLog(e.getMessage());
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (views != null) {
                                views.displayData(finalDisplayList);
                                views.onHideProgress();
                                views.showMsg(e.toString());
                            }
                        }
                    });
                }

            } catch (Exception e) {
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (views != null) {
                            views.onHideProgress();
                            views.showMsg(e.toString());
                        }
                    }
                });

                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute (Void aVoid){
            super.onPostExecute(aVoid);
        }
    }
}
