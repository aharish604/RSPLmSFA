package com.rspl.sf.msfa.reports.daySummary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.google.gson.Gson;
import com.rspl.sf.msfa.BuildConfig;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.asyncTask.RefreshAsyncTask;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.AsyncTaskCallBack;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.mbo.MyTargetsBean;
import com.rspl.sf.msfa.mtp.approval.MTPApprovalPresenter;
import com.rspl.sf.msfa.so.SOUtils;
import com.rspl.sf.msfa.soapproval.OpenOnlineManagerStore;
import com.rspl.sf.msfa.soapproval.SOApproveActivity;
import com.rspl.sf.msfa.store.GetOnlineODataInterface;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.store.OnlineManager;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by e10893 on 25-01-2018.
 */

public class DaySummaryPresenterImpl implements DaySummaryPresenter, UIListener, GetOnlineODataInterface,OnlineODataInterface {

    public static boolean isReloadMTPApproval = false;
    public static boolean isReloadSOApproval = false;
    private final SharedPreferences sharedPreferences;
    ArrayList<String> alAssignColl = null;
    Map<String, Double> mapMonthAchived = new HashMap<>();
    Map<String, Double> mapMonthTarget = new HashMap<>();
    HashSet<String> kpiNames = new HashSet<>();
    private Context context;
    private DaySummaryViewPresenter iDaySummViewPresenter = null;
    private Activity activity;
    private ArrayList<MyTargetsBean> alTargets;
    private ArrayList<MyTargetsBean> alKpiList = null;
    private ArrayList<MyTargetsBean> alMtlyKpiList = new ArrayList<>();
    private ArrayList<MyTargetsBean> alMyTargets = null;
    private Map<String, MyTargetsBean> mapMyTargetVal = new HashMap<>();
    private int totalRequest = 0;
    private int totalRequestDashBoard = 0;
    private int currentRequest = 0;
    private int currentRequestDasgboard = 0;
    private String totalMTPCount = "";
    private String totalSOCount = "";
    String mStrMonthYear = "";
    boolean readFromCache = false;
    ArrayList<DashBoardBean> alDashBoardBeansTran = new ArrayList<>();
    ArrayList<DashBoardBean> alDashBoardBeansMast = new ArrayList<>();
    private ArrayList<DashBoardBean> dashBoardBeans = null;
    private GUID refguid =null;


    public DaySummaryPresenterImpl(Context context, DaySummaryViewPresenter iDaySummViewPresenter, Activity activity) {
        this.context = context;
        this.iDaySummViewPresenter = iDaySummViewPresenter;
        this.alTargets = new ArrayList<>();
        this.dashBoardBeans = new ArrayList<>();
        this.activity = activity;
        sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
    }

    @Override
    public void onStart() {
        totalRequest = 0;
        currentRequest = 0;
        totalRequestDashBoard = 2;
        currentRequestDasgboard = 0;

        getAttendance();
        getDataFromOffline();
    }

    @Override
    public void onDestroy() {
        iDaySummViewPresenter = null;
    }

    @Override
    public void totalOrderValue() {
        String qry =Constants.SOs + "?$filter=" + Constants.OrderDate + " eq datetime'" + UtilConstants.getNewDate() /*"2019-01-23T00:00:00"*/+ "'&$select=TotalAmount";

        ConstantsUtils.onlineRequest(activity, qry , false, 3, ConstantsUtils.SESSION_QRY, this, false, true);
    }

    /*@Override
    public void reloadSOCount() {
        totalRequest = 0;
        currentRequest = 0;
        totalSOCount = "";
        requestSO();
    }*/

    private void requestSO(boolean readFromCache) {
        if (sharedPreferences.getString(Constants.isSOApprovalKey, "").equalsIgnoreCase(Constants.isSOApprovalTcode)) {
            Log.d("Testing", "requestSO");
            LogManager.writeLogInfo("Requesting for SO Approval ");
            totalRequest++;
            if (iDaySummViewPresenter != null)
                iDaySummViewPresenter.showSOProgress();

            try {
                String qry = Constants.Tasks + "/?$select=InstanceID&$filter=" + Constants.EntityType + "+eq+'SO'";
                OnlineManager.doOnlineGetRequest(qry, context, iReceiveEvent -> {
                    if (iReceiveEvent.getResponseStatusCode() == 200) {
                        JSONObject jsonObject = OnlineManager.getJSONBody(iReceiveEvent);
                        JSONArray jsonArray = OnlineManager.getJSONArrayBody(jsonObject);

                        totalSOCount = String.valueOf(jsonArray.length());
                        try {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constants.TotalSOCount, totalSOCount);
                            editor.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        SOApproveActivity.SOTotalCount = totalSOCount;
//                        }

                        activity.runOnUiThread(() -> {
                            if (iDaySummViewPresenter != null) {
                                iDaySummViewPresenter.hideSOProgress();
                                iDaySummViewPresenter.disPlaySOCount(totalSOCount);
                            }
                        });
                    }else {
                        String errorMsg="";
                        try {
                            errorMsg = Constants.getErrorMessage(iReceiveEvent,context);
                            refreshErrorUI(errorMsg);
                            iDaySummViewPresenter.hideSOProgress();
                            LogManager.writeLogError(errorMsg);
                        } catch (Throwable e) {
                            e.printStackTrace();
                            refreshErrorUI(e.getMessage());
                            iDaySummViewPresenter.hideSOProgress();
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
                    refreshErrorUI(errormessage);
                    iDaySummViewPresenter.hideSOProgress();
                    LogManager.writeLogError(e.toString());
                });
            } catch (Exception e) {
                totalRequest--;
                e.printStackTrace();
                refreshErrorUI(e.getMessage());
                iDaySummViewPresenter.hideSOProgress();
                ConstantsUtils.printErrorLog(e.getMessage());
            }
        }
    }

   /* @Override
    public void reloadMTPCount() {
        totalRequest = 0;
        currentRequest = 0;
        totalMTPCount = "";
       requestMTP();
    }*/

    private void requestMTP(boolean readFromCache) {
        if (sharedPreferences.getString(Constants.isMTPApprovalKey, "").equalsIgnoreCase(Constants.isMTPApprovalTcode)) {
            Log.d("Testing", "requestMTP");
            totalRequest++;
            if (iDaySummViewPresenter != null)
                iDaySummViewPresenter.showMTPProgress();
            try {
                String qry = Constants.Tasks + "/?$select=InstanceID,EntityAttribute4&$filter=" + Constants.EntityType + "+eq+'ROUTE'";
                OnlineManager.doOnlineGetRequest(qry, context, iReceiveEvent -> {
                    if (iReceiveEvent.getResponseStatusCode() == 200) {
                        JSONObject jsonObject = OnlineManager.getJSONBody(iReceiveEvent);
                        JSONArray jsonArray = OnlineManager.getJSONArrayBody(jsonObject);

                        try {
                            totalMTPCount = OfflineManager.getRouteCount(jsonArray);;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constants.TotalMTPCount, totalMTPCount);
                            editor.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        MTPApprovalPresenter.mtpTotalCount = totalMTPCount;
//                        }

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (iDaySummViewPresenter != null) {
                                    iDaySummViewPresenter.hideMTPProgress();
                                    iDaySummViewPresenter.disPlayMTPCount(totalMTPCount);
                                }
                            }
                        });
                    }else {
                        String errorMsg="";
                        try {
                            errorMsg = Constants.getErrorMessage(iReceiveEvent,context);
                            refreshErrorUI(errorMsg);
                            iDaySummViewPresenter.hideMTPProgress();
                            LogManager.writeLogError(errorMsg);
                        } catch (Throwable e) {
                            e.printStackTrace();
                            refreshErrorUI(e.getMessage());
                            iDaySummViewPresenter.hideMTPProgress();
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
                    refreshErrorUI(errormessage);
                    iDaySummViewPresenter.hideMTPProgress();
                    LogManager.writeLogError(e.toString());
                });
            } catch (Exception e) {
                totalRequest--;
                e.printStackTrace();
                refreshErrorUI(e.getMessage());
                iDaySummViewPresenter.hideMTPProgress();
                ConstantsUtils.printErrorLog(e.getMessage());
            }
        }
    }

    private void requestMasterCountDBs(boolean readFromCache) {
        Log.d("Testing", "requestMasterCountDBs");
        totalRequest++;

        try {
            String qry = Constants.MasterCountDBs + "?$filter=DistributorCount%20eq%20'X'%20and%20DSRCount%20eq%20'X'%20and%20BeatCount%20eq%20'X'%20and%20RetailerCount%20eq%20'X'%20and%20SPCount%20eq%20'X'";
            OnlineManager.doOnlineGetRequestDashBoard(qry, context, iReceiveEvent -> {
                if (iReceiveEvent.getResponseStatusCode() == 200) {
                    JSONObject jsonObject = OnlineManager.getJSONBody(iReceiveEvent);
                    JSONArray jsonArray = OnlineManager.getJSONArrayBody(jsonObject);
                    try {
                        alDashBoardBeansMast.clear();
                        alDashBoardBeansMast = OnlineManager.getMasterCountDB(jsonArray);
                        displayUIMonth();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    String errorMsg="";
                    try {
                        errorMsg = Constants.getErrorMessage(iReceiveEvent,context);
//                        if(iReceiveEvent.getResponseStatusCode()!=401) {
                        refreshErrorUI(errorMsg);
//                        }
                        LogManager.writeLogError(errorMsg);
                    } catch (Throwable e) {
                        e.printStackTrace();
//                        if(iReceiveEvent.getResponseStatusCode()!=401) {
                        refreshErrorUI(e.getMessage());
//                        }
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
                refreshErrorUI(errormessage);
                LogManager.writeLogError(e.toString());
            });
        } catch (Exception e) {
            totalRequest--;
            e.printStackTrace();
            refreshErrorUI(e.getMessage());
            ConstantsUtils.printErrorLog(e.getMessage());
        }
    }

    private void displayUIMonth() {
        try {
            dashBoardBeans.clear();
            if (alDashBoardBeansMast != null && !alDashBoardBeansMast.isEmpty()) {
                dashBoardBeans.addAll(alDashBoardBeansMast);
            }
            if (alDashBoardBeansTran != null && !alDashBoardBeansTran.isEmpty()) {
                dashBoardBeans.addAll(alDashBoardBeansTran);
            }
            Gson gson = new Gson();
            String json = gson.toJson(dashBoardBeans);

//                HashSet boardBeanSet = new HashSet(dashBoardBeans);
            SharedPreferences sharedPreferences1 = context.getSharedPreferences(Constants.PREFS_NAME, 0);
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            editor.putString(Constants.SharMonthDashBoardList, json);
            editor.commit();


        } catch (Exception e) {
            e.printStackTrace();
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (iDaySummViewPresenter != null) {
                    if (dashBoardBeans != null && !dashBoardBeans.isEmpty()) {
                        iDaySummViewPresenter.displayDashBoardList(dashBoardBeans);
                    }
                }
            }
        });
    }

    private void requestTransactionCountDBs(boolean readFromCache) {
        Log.d("Testing", "requestTransactionCountDBs");
        totalRequest++;

        try {
            String qry = Constants.TransactionCountDBs + "?$filter=SecOrdCount%20eq%20'X'%20and%20SecInvCount%20eq%20'X'%20and%20RTGSCount%20eq%20'X'";
            OnlineManager.doOnlineGetRequestDashBoard(qry, context, iReceiveEvent -> {
                if (iReceiveEvent.getResponseStatusCode() == 200) {
                    JSONObject jsonObject = OnlineManager.getJSONBody(iReceiveEvent);
                    JSONArray jsonArray = OnlineManager.getJSONArrayBody(jsonObject);
                    try {
                        alDashBoardBeansTran.clear();
                        alDashBoardBeansTran = OnlineManager.getTransactionCountDBs(jsonArray);
                        displayUIMonth();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    String errorMsg="";
                    try {
                        errorMsg = Constants.getErrorMessage(iReceiveEvent,context);
//                        if(iReceiveEvent.getResponseStatusCode()!=401) {
                        refreshErrorUI(errorMsg);
//                        }
                        LogManager.writeLogError(errorMsg);
                    } catch (Throwable e) {
                        e.printStackTrace();
//                        if(iReceiveEvent.getResponseStatusCode()!=401) {
                        refreshErrorUI(e.getMessage());
//                        }

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
                refreshErrorUI(errormessage);
                LogManager.writeLogError(e.toString());
            });
        } catch (Exception e) {
            totalRequest--;
            e.printStackTrace();
            refreshErrorUI(e.getMessage());
            ConstantsUtils.printErrorLog(e.getMessage());
        }
    }



    private void getDataFromOnline() {
        getMTPApprovalCount(Constants.getRollID(context));
        if (UtilConstants.isNetworkAvailable(activity)) {
            requestMasterCountDBs(true);
            requestTransactionCountDBs(true);
        }

//                displayUIMonth();
        displayUIList(true);
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME,0);
        String rollType = sharedPreferences.getString(Constants.USERROLE, "");
        if (rollType.equalsIgnoreCase("Z5")) {
            String totalOrderValueKey = sharedPreferences.getString(Constants.Total_Order_Value_KEY, "");
            if (TextUtils.isEmpty(totalOrderValueKey)) {
                totalOrderValue();
            }
        }

    }

    private void openOnlineStore() {
        //optional store open
        if (UtilConstants.isNetworkAvailable(activity)) {
            new OpenOnlineManagerStore(activity, new AsyncTaskCallBack() {
                @Override
                public void onStatus(boolean status, String values) {
                    if (status) {
                        getMTPApprovalCount(Constants.getRollID(context));
                    } else {
                        if (iDaySummViewPresenter != null)
                            iDaySummViewPresenter.showMessage(values);
                    }
                }
            }).execute();
        }
    }

    /*private void openOnlineStoreDashBroad() {
        //optional store open
        if (UtilConstants.isNetworkAvailable(activity)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        boolean dashbroadStore=false;
                        OnlineStoreListenerDashBroad openListener = OnlineStoreListenerDashBroad.getInstance();
                        OnlineODataStore store = openListener.getStore();
                        if (store == null) {
                            dashbroadStore = OnlineManager.openOnlineStoreDashBroad(context,false);
                        }else {
                            dashbroadStore=true;
                        }
                        if(dashbroadStore) {
                            requestMasterCountDBs(true);
                            requestTransactionCountDBs(true);
                            Log.d("DashBroadStore", "Successfull");
                        }
                    } catch (OnlineODataStoreException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }*/

    private void getMTPApprovalCount(boolean readFromCache) {
        Log.d("Testing","getMTPApprovalCount");
        if (UtilConstants.isNetworkAvailable(activity)) {
            getCountRequest(readFromCache);
        } /*else {
            if (iDaySummViewPresenter != null)
                iDaySummViewPresenter.showMessage(context.getString(R.string.err_no_network));
        }*/
    }

    @Override
    public void onFilter() {
    }

    @Override
    public void onSearch(String searchText) {

    }

    @Override
    public void onRefresh() {

        totalRequest = 0;
        currentRequest = 0;
        totalRequestDashBoard = 2;
        currentRequestDasgboard = 0;
        if (iDaySummViewPresenter != null) {
            iDaySummViewPresenter.showSOProgress();
            iDaySummViewPresenter.showMTPProgress();
//            iDaySummViewPresenter.showDashMonthProgress();
//            iDaySummViewPresenter.showDashDayProgress();
        }
        if (UtilConstants.isNetworkAvailable(context)) {
            try {
                if (ConstantsUtils.isPinging()) {
                    if (Constants.writeDebug) {
                        LogManager.writeLogDebug("Dashboard refresh sync started");
                    }
                    if (iDaySummViewPresenter != null) {
                        iDaySummViewPresenter.showDashMonthProgress();
                        iDaySummViewPresenter.showDashDayProgress();
                    }
                    alAssignColl = new ArrayList<>();
                    alAssignColl.add(Constants.Targets);
                    alAssignColl.add(Constants.TargetItems);
                    alAssignColl.add(Constants.KPISet);
                    alAssignColl.add(Constants.KPIItems);
                    alAssignColl.add(Constants.ConfigTypsetTypeValues);
                    SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME,0);
                    String rollType = sharedPreferences.getString(Constants.USERROLE, "");
                    if (rollType.equalsIgnoreCase("Z5")) {
                        if (Constants.writeDebug)
                            LogManager.writeLogDebug("Dashboard refresh sync: Total order value Started");
                        totalOrderValue();
                    }
                    getTargets(alAssignColl);
                } else {
                    if (iDaySummViewPresenter != null) {
                        if (Constants.writeDebug)
                            LogManager.writeLogDebug("Dashboard refresh sync: Internet is ON, But it is not Active");
                        iDaySummViewPresenter.hideProgressDialog();
                        iDaySummViewPresenter.hideSOProgress();
                        iDaySummViewPresenter.hideMTPProgress();
                        iDaySummViewPresenter.displayDashBoardList(dashBoardBeans);
                        iDaySummViewPresenter.showMessage("Internet is ON, But it is not Active");
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            if (iDaySummViewPresenter != null) {
                iDaySummViewPresenter.hideProgressDialog();
                iDaySummViewPresenter.hideSOProgress();
                iDaySummViewPresenter.hideMTPProgress();
                iDaySummViewPresenter.displayDashBoardList(dashBoardBeans);
                if (Constants.writeDebug)
                    LogManager.writeLogDebug("Dashboard refresh sync: " + context.getString(R.string.no_network_conn));
                iDaySummViewPresenter.showMessage(context.getString(R.string.no_network_conn));
            }
        }
//        if (Constants.onlineStoreDashBroad==null){
//            openOnlineStoreDashBroad();
//        }
    }

    @Override
    public void startFilter(int requestCode, int resultCode, Intent data) {
    }


    public void getDataFromOffline() {
        Log.d("DaySummary Fragment"," EnteredPresnt");

        if (iDaySummViewPresenter != null) {
            iDaySummViewPresenter.showProgressDialog();
            //  iDaySummViewPresenter.showAttendancePB();
        }
        getDataFromOnline();
    }
    HashMap<String, String> mapUOM =null;
    private void getDataFromDB() {
        if(mapMyTargetVal!=null && mapMyTargetVal.size()>0){
            mapMyTargetVal.clear();
        }
        Constants.mapMatGrpBasedOnUOM.clear();
        Constants.mapMatGrpBasedOnUOMTemp.clear();
        Constants.isComingFromDashBoard = true;
        getSystemKPI();
        getMyTargetsList();
        alTargets = new ArrayList<>();
        Constants.alTodayBeatCustomers.clear();
        String currency = Constants.getCurrency();;
       /* String[][] currencyArr = Constants.getDistributors();
        try {
            if (currencyArr[10][0] != null) {
                currency = currencyArr[10][0];
            }
        }catch (Exception e){
            e.printStackTrace();
        }*/
        String actualVistedCustomers = Constants.getVisitedRetailerCount();
        boolean hideVisit = SOUtils.isHideVisit(context);
        ArrayList<String> setVisitedCustomers = new ArrayList<>();
        if (!hideVisit)
            setVisitedCustomers = Constants.getTodayVisitedCustomers();

        String targetCustomers = Constants.getVisitTargetForToday(context);
        String mStrCalBase = Constants.getName(Constants.ConfigTypsetTypeValues, Constants.TypeValue, Constants.Types,Constants.CLBASE);
        String totalOrderValue = Constants.getTotalOrderValue(context, UtilConstants.getNewDate(), setVisitedCustomers, hideVisit,"02");
//        String totalOrderValue = Constants.getTotalOrderValue(context, UtilConstants.getNewDate(), setVisitedCustomers, hideVisit,mStrCalBase);
        String mStrDeviceTLSDOffLine = Constants.getDeviceTLSD("");
        String mStrDeviceTLSD = Constants.getDeviceTLSDDataVault(Constants.alCustomers, context);

        Double mCalTLSDVal = 0.0;
        try {
            mCalTLSDVal = Double.parseDouble(mStrDeviceTLSDOffLine) + Double.parseDouble(mStrDeviceTLSD);
        } catch (NumberFormatException e) {
            mCalTLSDVal = 0.0;
            e.printStackTrace();
        }
        String qry = Constants.ConfigTypesetTypes + "?$filter=" + Constants.Typeset + " eq '" + Constants.UOMNO0 + "' ";

        try {
            mapUOM =new HashMap<>();
            mapUOM = OfflineManager.getUOMMapVal(qry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        HashMap<String,MyTargetsBean> mapQtyBasedonUOM = new HashMap<>();
        try {
            if(mStrCalBase.equalsIgnoreCase(Constants.str_01)){
//                mapQtyBasedonUOM =Constants.getUOMBasedMaterialGrp(context,UtilConstants.getNewDate());
                mapQtyBasedonUOM =Constants.getKPICodeBasedMaterialGrp(context,UtilConstants.getNewDate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        kpiNames = new HashSet<>();
        alTargets.clear();
//        alTargets = getValuesFromMap(mapMyTargetVal, totalOrderValue, mCalTLSDVal + "", currency,mapQtyBasedonUOM);
        alTargets = getValuesFromMapTemp(mapMyTargetVal, totalOrderValue, mCalTLSDVal + "", currency,mapQtyBasedonUOM);

        MyTargetsBean myTargetsBean = null;
        double mDouAchivedPercentage = 0;
        try {
            myTargetsBean = new MyTargetsBean();
            myTargetsBean.setMonthTarget(targetCustomers);
            myTargetsBean.setMTDA(actualVistedCustomers);
            mDouAchivedPercentage = OfflineManager.getAchivedPer(targetCustomers, actualVistedCustomers);
            myTargetsBean.setAchivedPercentage(mDouAchivedPercentage + "");
            myTargetsBean.setBTD(totalOrderValue);
            myTargetsBean.setKPIName("Visits");
            myTargetsBean.setCurrency(currency);
            myTargetsBean.setKpiNames(kpiNames);
            alTargets.add(0,myTargetsBean);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            myTargetsBean = new MyTargetsBean();
            myTargetsBean.setMonthTarget(targetCustomers);
            myTargetsBean.setMTDA(actualVistedCustomers);
            mDouAchivedPercentage = OfflineManager.getAchivedPer(targetCustomers, actualVistedCustomers);
            myTargetsBean.setAchivedPercentage(mDouAchivedPercentage + "");
            myTargetsBean.setBTD(totalOrderValue);
            myTargetsBean.setKPIName("Total Order Value");
            myTargetsBean.setCurrency(currency);
            myTargetsBean.setKpiNames(kpiNames);
            alTargets.add(1,myTargetsBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public ArrayList<MyTargetsBean> getTargetsFromOfflineDB() {
        getSystemKPI();
        getMyTargetsList();
        String currency = Constants.getCurrency();
       *//* String[][] currencyArr = Constants.getDistributors();
        try {
            if (currencyArr[10][0] != null) {
                currency = currencyArr[10][0];
            }
        }catch (Exception e){
            e.printStackTrace();
        }*//*
        alTargets = new ArrayList<>();
        Constants.alTodayBeatCustomers.clear();
        String actualVistedCustomers = Constants.getVisitedRetailerCount();
        String targetCustomers = Constants.getVisitTargetForToday(context);
        boolean hideVisit = SOUtils.isHideVisit(context);
        ArrayList<String> setVisitedCustomers = new ArrayList<>();
        if (!hideVisit)
            setVisitedCustomers = Constants.getTodayVisitedCustomers();
        String mStrCalBase = Constants.getName(Constants.ConfigTypsetTypeValues, Constants.TypeValue, Constants.Types,Constants.CLBASE);
//        String totalOrderValue = Constants.getTotalOrderValue(context, UtilConstants.getNewDate(), setVisitedCustomers, hideVisit,mStrCalBase);
        String totalOrderValue = Constants.getTotalOrderValue(context, UtilConstants.getNewDate(), setVisitedCustomers, hideVisit,"02");
        String mStrDeviceTLSDOffLine = Constants.getDeviceTLSD("");
        String mStrDeviceTLSD = Constants.getDeviceTLSDDataVault(Constants.alCustomers, context);

        Double mCalTLSDVal = 0.0;
        try {
            mCalTLSDVal = Double.parseDouble(mStrDeviceTLSDOffLine) + Double.parseDouble(mStrDeviceTLSD);
        } catch (NumberFormatException e) {
            mCalTLSDVal = 0.0;
            e.printStackTrace();
        }
        kpiNames = new HashSet<>();
        alTargets = getValuesFromMap(mapMyTargetVal, totalOrderValue, mCalTLSDVal + "", currency);

        MyTargetsBean myTargetsBean = new MyTargetsBean();
//        myTargetsBean.setMonthTarget("1200.00");
//        myTargetsBean.setMTDA("1050.00");
//        myTargetsBean.setBTD("150.00");
//        myTargetsBean.setKPIName("Sales Value");
//        alTargets.add(myTargetsBean);

//        myTargetsBean =new MyTargetsBean();
//        myTargetsBean.setMonthTarget("100.00");
//        myTargetsBean.setMTDA("50.00");
//        myTargetsBean.setBTD("50.00");
//        myTargetsBean.setKPIName("TLSD");
//        alTargets.add(myTargetsBean);

        myTargetsBean = new MyTargetsBean();
        myTargetsBean.setMonthTarget(targetCustomers);
        myTargetsBean.setMTDA(actualVistedCustomers);
        double mDouAchivedPercentage = OfflineManager.getAchivedPer(targetCustomers, actualVistedCustomers);
        myTargetsBean.setAchivedPercentage(mDouAchivedPercentage + "");
        myTargetsBean.setBTD(totalOrderValue);
        myTargetsBean.setKPIName("Visits");
        myTargetsBean.setCurrency(currency);
        myTargetsBean.setKpiNames(kpiNames);
        alTargets.add(0,myTargetsBean);

        myTargetsBean = new MyTargetsBean();
        myTargetsBean.setMonthTarget(targetCustomers);
        myTargetsBean.setMTDA(actualVistedCustomers);
        mDouAchivedPercentage = OfflineManager.getAchivedPer(targetCustomers, actualVistedCustomers);
        myTargetsBean.setAchivedPercentage(mDouAchivedPercentage + "");
        myTargetsBean.setBTD(totalOrderValue);
        myTargetsBean.setKPIName("Total Order Value");
        myTargetsBean.setCurrency(currency);
        myTargetsBean.setKpiNames(kpiNames);
        alTargets.add(1,myTargetsBean);

//        iTargetViewPresenter.displayList(soItemBeanArrayList);
        return alTargets;

    }*/

    private ArrayList<MyTargetsBean> getValuesFromMap(Map<String, MyTargetsBean> mapMyTargetVal, String orderVal,
                                                      String mStrTlSD, String currency,HashMap<String,MyTargetsBean> mapQtyBasedonUOM) {
        ArrayList<MyTargetsBean> alTargets = null;
        try {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int numDays = calendar.getActualMaximum(Calendar.DATE);
            int remaingDays = numDays - day;

            alTargets = new ArrayList<>();
            if (!mapMyTargetVal.isEmpty()) {
                Iterator iterator = mapMyTargetVal.keySet().iterator();
                while (iterator.hasNext()) {
                    try {
                        String key = iterator.next().toString();
                        MyTargetsBean myTargetsBean = mapMyTargetVal.get(key);
                        myTargetsBean.setMonthTarget(mapMonthTarget.get(key).toString());
                        kpiNames.add(myTargetsBean.getKPIName());  // adding kpi names
                        myTargetsBean.setKpiNames(kpiNames);
                        if (myTargetsBean.getKPIName().contains("Sales")) {
//                    myTargetsBean.setMTDA(mapMonthAchived.get(key).toString());
                            myTargetsBean.setMTDA(orderVal);
                        } else if (myTargetsBean.getKPIName().contains("TLSD")) {
                            myTargetsBean.setMTDA(mStrTlSD);
                        } else {
                            myTargetsBean.setMTDA(mapMonthAchived.get(key).toString());
                        }

                        if(myTargetsBean.getCalculationBase().equalsIgnoreCase(Constants.str_01)){
                            if(!mapQtyBasedonUOM.isEmpty()){
                                Double achivedTarget = 0.00;
                                try {
                                    if(myTargetsBean.getUOM().equalsIgnoreCase("TO")){
                                        try {
                                            achivedTarget = Double.parseDouble(mapQtyBasedonUOM.get(myTargetsBean.getUOM()).getMTDA())/1000;
                                        } catch (NumberFormatException e) {
                                            achivedTarget = 0.00;
                                            e.printStackTrace();
                                        }
                                    }else{
                                        try {
                                            if (mapQtyBasedonUOM.get(myTargetsBean.getUOM())!=null) {
                                                achivedTarget = Double.parseDouble(mapQtyBasedonUOM.get(myTargetsBean.getUOM()).getMTDA());
                                            }else {
                                                achivedTarget = 0.00;
                                            }
                                        } catch (NumberFormatException e) {
                                            achivedTarget = 0.00;
                                            e.printStackTrace();
                                        }
                                    }

                                } catch (Exception e) {
                                    achivedTarget = 0.00;
                                    e.printStackTrace();
                                }
//                        myTargetsBean.setMTDA(achivedTarget+"");
                                try {
                                    if (mapUOM.containsKey(myTargetsBean.getUOM()))
                                        myTargetsBean.setMTDA(OnlineManager.trimQtyDecimalPlace(achivedTarget+""));
                                    else
                                        myTargetsBean.setMTDA(UtilConstants.removeLeadingZeroQuantity(achivedTarget + ""));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        // 25-08-2018 start
                        //                double BTDPer = OfflineManager.getBTD(mapMonthTarget.get(key).toString(), myTargetsBean.getMTDA());
                        //                double achivedPer = OfflineManager.getAchivedPer(mapMonthTarget.get(key).toString(), myTargetsBean.getMTDA());

                        double BTDPer = OfflineManager.getBTD(mapMonthTarget.get(key).toString(), myTargetsBean.getMTDA());
                        //                double BTDPer = OfflineManager.getBTD(mapMonthTarget.get(key).toString(), "0.00");
                        double achivedPer = OfflineManager.getAchivedPer(mapMonthTarget.get(key).toString(), myTargetsBean.getMTDA());
                        //                double achivedPer = OfflineManager.getAchivedPer(mapMonthTarget.get(key).toString(), "0.00");
                        double dayTarget = OfflineManager.getDayTarget(BTDPer + "", remaingDays + "");

                        if (dayTarget < 0) {
                            dayTarget = 0.0;
                        }
                        if(myTargetsBean.getCalculationBase().equalsIgnoreCase(Constants.str_01)){

                            try {
                                if (mapUOM.containsKey(myTargetsBean.getUOM()))
                                    myTargetsBean.setMonthTarget(OnlineManager.trimQtyDecimalPlace(dayTarget+""));
                                else
                                    myTargetsBean.setMonthTarget(UtilConstants.removeLeadingZeroQuantity(dayTarget + ""));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }else{
                            myTargetsBean.setMonthTarget(dayTarget + "");
                        }

                        //                myTargetsBean.setMTDA("0.00");
                        //  25-08-2018  End

                        myTargetsBean.setBTD(BTDPer + "");
                        myTargetsBean.setAchivedPercentage(achivedPer + "");
                        myTargetsBean.setCurrency(currency);
                        alTargets.add(mapMyTargetVal.get(key));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alTargets;
    }

    private ArrayList<MyTargetsBean> getValuesFromMapTemp(Map<String, MyTargetsBean> mapMyTargetVal, String orderVal,
                                                          String mStrTlSD, String currency,HashMap<String,MyTargetsBean> mapQtyBasedonUOM) {
        ArrayList<MyTargetsBean> alTargets = null;
        try {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int numDays = calendar.getActualMaximum(Calendar.DATE);
            int remaingDays = (numDays - day)+1;

            alTargets = new ArrayList<>();
            if (!mapMyTargetVal.isEmpty()) {
                Iterator iterator = mapMyTargetVal.keySet().iterator();
                while (iterator.hasNext()) {
                    try {
                        String key = iterator.next().toString();
                        MyTargetsBean myTargetsBean = mapMyTargetVal.get(key);
                        myTargetsBean.setMonthTarget(mapMonthTarget.get(key).toString());
                        kpiNames.add(myTargetsBean.getKPIName());  // adding kpi names
                        myTargetsBean.setKpiNames(kpiNames);
                        if (myTargetsBean.getKPIName().contains("Sales")) {
//                    myTargetsBean.setMTDA(mapMonthAchived.get(key).toString());
                            myTargetsBean.setMTDA(orderVal);
                        } else if (myTargetsBean.getKPIName().contains("TLSD")) {
                            myTargetsBean.setMTDA(mStrTlSD);
                        } else {
                            myTargetsBean.setMTDA(mapMonthAchived.get(key).toString());
                        }

                        if(myTargetsBean.getCalculationBase().equalsIgnoreCase(Constants.str_01)){
                            if(!mapQtyBasedonUOM.isEmpty()){
                                Double achivedTarget = 0.00;
                                try {
                                    if(myTargetsBean.getUOM().equalsIgnoreCase("TO")){
                                        try {
                                            achivedTarget = Double.parseDouble(mapQtyBasedonUOM.get(myTargetsBean.getKpiGuid().replaceAll("-","")).getMTDA())/1000;
                                        } catch (NumberFormatException e) {
                                            achivedTarget = 0.00;
                                            e.printStackTrace();
                                        }
                                    }else{
                                        try {
                                            if (mapQtyBasedonUOM.get(myTargetsBean.getKpiGuid().replaceAll("-",""))!=null) {
                                                achivedTarget = Double.parseDouble(mapQtyBasedonUOM.get(myTargetsBean.getKpiGuid().replaceAll("-","")).getMTDA());
                                            }else {
                                                achivedTarget = 0.00;
                                            }
                                        } catch (NumberFormatException e) {
                                            achivedTarget = 0.00;
                                            e.printStackTrace();
                                        }
                                    }

                                } catch (Exception e) {
                                    achivedTarget = 0.00;
                                    e.printStackTrace();
                                }
//                        myTargetsBean.setMTDA(achivedTarget+"");
                                try {
                                    if (mapUOM.containsKey(myTargetsBean.getUOM()))
                                        myTargetsBean.setMTDA(OnlineManager.trimQtyDecimalPlace(achivedTarget+""));
                                    else
                                        myTargetsBean.setMTDA(UtilConstants.removeLeadingZeroQuantity(achivedTarget + ""));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        // 25-08-2018 start
                        //                double BTDPer = OfflineManager.getBTD(mapMonthTarget.get(key).toString(), myTargetsBean.getMTDA());
                        //                double achivedPer = OfflineManager.getAchivedPer(mapMonthTarget.get(key).toString(), myTargetsBean.getMTDA());

//                        double BTDPer = OfflineManager.getBTD(mapMonthTarget.get(key).toString(), myTargetsBean.getMTDA());
                        double BTDPer = OfflineManager.getBTD(mapMonthTarget.get(key).toString(), mapMonthAchived.get(key).toString());
                        //                double BTDPer = OfflineManager.getBTD(mapMonthTarget.get(key).toString(), "0.00");
                        double achivedPer = OfflineManager.getAchivedPer(mapMonthTarget.get(key).toString(), myTargetsBean.getMTDA());
//                double achivedPer = OfflineManager.getAchivedPer(mapMonthTarget.get(key).toString(), "0.00");
                        double dayTarget = OfflineManager.getDayTarget(BTDPer + "", remaingDays + "");

                        if (dayTarget < 0) {
                            dayTarget = 0.0;
                        }
                        if(myTargetsBean.getCalculationBase().equalsIgnoreCase(Constants.str_01)){

                            try {
                                if (mapUOM.containsKey(myTargetsBean.getUOM()))
                                    myTargetsBean.setMonthTarget(OnlineManager.trimQtyDecimalPlace(dayTarget+""));
                                else
//                                    myTargetsBean.setMonthTarget(UtilConstants.removeLeadingZeroQuantity(dayTarget + ""));
                                    myTargetsBean.setMonthTarget(OnlineManager.trimQtyDecimalPlace(dayTarget + ""));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }else{
                            myTargetsBean.setMonthTarget(dayTarget + "");
                        }

                        //                myTargetsBean.setMTDA("0.00");
                        //  25-08-2018  End

                        myTargetsBean.setBTD(BTDPer + "");
                        myTargetsBean.setAchivedPercentage(achivedPer + "");
                        myTargetsBean.setCurrency(currency);
                        alTargets.add(mapMyTargetVal.get(key));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alTargets;
    }

    /*Gets kpiList for selected month and year*/
    private void getSystemKPI() {
        try {
            String mStrMyStockQry;
            mStrMyStockQry = Constants.KPISet + "?$filter = " + Constants.ValidTo + " ge datetime'" + UtilConstants.getNewDate() + "' and "+Constants.Periodicity+" eq '02' ";

            alKpiList = OfflineManager.getKpiSetGuidList(mStrMyStockQry, "");

            if(alKpiList!=null && alKpiList.size()>0){
                alMtlyKpiList = new ArrayList<>();
                for(MyTargetsBean targetsBean:alKpiList){
                    if(targetsBean.getCalculationBase().equalsIgnoreCase(Constants.str_01)
                            && targetsBean.getKPICategory().equalsIgnoreCase(Constants.str_02)){
                        alMtlyKpiList.add(targetsBean);
                    }

                }
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog(e.getMessage());
        }

    }

    /*Get targets for sales person  based on query*/
    private void getMyTargetsList() {
        try {
            if (alKpiList != null && alKpiList.size() > 0) {
                if (alMyTargets != null && alMyTargets.size() > 0) {
                    alMyTargets = new ArrayList<>();
                }
                final Calendar c = Calendar.getInstance();
                int fiscalYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);

                String mon = "";
                int mnt = 0;
                mnt = mMonth + 1;
                if (mnt < 10)
                    mon = "0" + mnt;
                else
                    mon = "" + mnt;
                mStrMonthYear =  mon + fiscalYear;

                alMyTargets = OfflineManager.getMyTargets(alKpiList, Constants.getSPGUID(Constants.SPGUID),mStrMonthYear);
            }
            mapMonthTarget.clear();
            mapMonthAchived.clear();
            mapMyTargetVal = getALMyTargetList(alMyTargets);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog(e.getMessage());
        }
    }

    //ToDo sum of actual and target quantity/Value based on kpi code and assign to map table
    private Map<String, MyTargetsBean> getALMyTargetList(ArrayList<MyTargetsBean> alMyTargets) {
        Map<String, MyTargetsBean> mapMyTargetBean = new HashMap<>();
        if (alMyTargets != null && alMyTargets.size() > 0) {
            for (MyTargetsBean bean : alMyTargets)
                if (mapMonthTarget.containsKey(bean.getKPICode())) {
                    double mDoubMonthTarget = 0;
                    try {
                        if(!TextUtils.isEmpty(bean.getMonthTarget())) {
                            mDoubMonthTarget = Double.parseDouble(bean.getMonthTarget()) + mapMonthTarget.get(bean.getKPICode());
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    double mDoubMonthAchived = 0;
                    try {
                        if(!TextUtils.isEmpty(bean.getMTDA())) {
                            mDoubMonthAchived = Double.parseDouble(bean.getMTDA()) + mapMonthAchived.get(bean.getKPICode());
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    mapMonthTarget.put(bean.getKPICode(), mDoubMonthTarget);
                    mapMonthAchived.put(bean.getKPICode(), mDoubMonthAchived);
                    mapMyTargetBean.put(bean.getKPICode(), bean);
                } else {
                    double mDoubMonthTarget = 0;
                    try {
                        if(!TextUtils.isEmpty(bean.getMonthTarget())) {
                            mDoubMonthTarget = Double.parseDouble(bean.getMonthTarget());
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    double mDoubMonthAchived = 0;
                    try {
                        if(!TextUtils.isEmpty(bean.getMTDA())) {
                            mDoubMonthAchived = Double.parseDouble(bean.getMTDA());
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    try {
                        double mDoubAchivedPer = Double.parseDouble(bean.getAchivedPercentage());
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    try {
                        double mDoubBTD = Double.parseDouble(bean.getBTD());
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    mapMonthTarget.put(bean.getKPICode(), mDoubMonthTarget);
                    mapMonthAchived.put(bean.getKPICode(), mDoubMonthAchived);
                    mapMyTargetBean.put(bean.getKPICode(), bean);
                }
        }


        return mapMyTargetBean;
    }

    @Override
    public void onRequestError(int i, Exception e) {
        if (iDaySummViewPresenter != null)
            iDaySummViewPresenter.hideProgressDialog();
        ErrorBean errorBean = Constants.getErrorCode(i, e, context);
        if (errorBean.hasNoError()) {
            if (!Constants.isStoreClosed) {
                if (i == Operation.OfflineRefresh.getValue()) {
                    try {
                        /*String syncTime = UtilConstants.getSyncHistoryddmmyyyyTime();
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
                        //    Constants.updateSyncTime(alAssignColl,context,Constants.DownLoad);
                    } catch (Exception exce) {
                        e.printStackTrace();
                        ConstantsUtils.printErrorLog(e.getMessage());
                    }

                    Constants.isSync = false;
                    if (!Constants.isStoreClosed) {
                        if (iDaySummViewPresenter != null) {
                            iDaySummViewPresenter.hideProgressDialog();
                            if (Constants.writeDebug)
                                LogManager.writeLogDebug("Dashboard refresh failed:" + e.getLocalizedMessage());

                            if(e.getMessage().contains("invalid authentication")){
                                iDaySummViewPresenter.hideSOProgress();
                                iDaySummViewPresenter.hideMTPProgress();
                                iDaySummViewPresenter.displayDashBoardList(dashBoardBeans);
                                displayUIList(true);
                               /* iDaySummViewPresenter.hideProgressDialog();
                                iDaySummViewPresenter.hideSOProgress();
                                iDaySummViewPresenter.hideMTPProgress();
                                iDaySummViewPresenter.displayDashBoardList(dashBoardBeans);*/
                                iDaySummViewPresenter.showMessage(e.getMessage());

                            }/*else if(e.getMessage().contains("HTTP Status 401 ? Unauthorized")){
                             *//*  iDaySummViewPresenter.hideProgressDialog();
                             //   iDaySummViewPresenter.hideSOProgress();
                               // iDaySummViewPresenter.hideMTPProgress();
                                iDaySummViewPresenter.displayDashBoardList(dashBoardBeans);*//*
                                iDaySummViewPresenter.showMessage(e.getMessage());
                            }*/else {
                                iDaySummViewPresenter.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                            }
                        }
//                        responseFailed(null, 0, 0, "", context.getString(R.string.msg_error_occured_during_sync), null);
                       /* android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                                context, R.style.MyTheme);
                        builder.setMessage(context.getString(R.string.msg_error_occured_during_sync))
                                .setCancelable(false)
                                .setPositiveButton(context.getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                dialog.cancel();
                                            }
                                        });

                        builder.show();*/


                    } else {
                        if (iDaySummViewPresenter != null) {
                            iDaySummViewPresenter.hideProgressDialog();
                            iDaySummViewPresenter.showMessage(context.getString(R.string.msg_sync_terminated));
                        }
//                        responseFailed(null, 0, 0, "", context.getString(R.string.msg_sync_terminated), null);
                       /* android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                                context, R.style.MyTheme);
                        builder.setMessage(context.getString(R.string.msg_sync_terminated))
                                .setCancelable(false)
                                .setPositiveButton(context.getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                dialog.cancel();
                                            }
                                        });

                        builder.show();*/
                    }
                }else if (i == Operation.GetStoreOpen.getValue()){
                    Constants.isSync = false;
                    if (iDaySummViewPresenter != null) {
                        iDaySummViewPresenter.hideProgressDialog();

                        if (Constants.writeDebug)
                            LogManager.writeLogDebug("Dashboard refresh failed:" + e.getLocalizedMessage());
                        if(e.getMessage().contains("invalid authentication")){
                            iDaySummViewPresenter.showMessage(context.getString(R.string.invalidPassword));
                        }else if(e.getMessage().contains("HTTP Status 401 ? Unauthorized")){
                            iDaySummViewPresenter.showMessage(context.getString(R.string.invalidPassword));
                        }else {
                            iDaySummViewPresenter.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                        }
                    }
                }
            }

        }else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (iDaySummViewPresenter != null) {
                    iDaySummViewPresenter.showProgressDialog();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (iDaySummViewPresenter != null) {
                    iDaySummViewPresenter.hideProgressDialog();
                    if (Constants.writeDebug)
                        LogManager.writeLogDebug("Dashboard refresh failed:" + errorBean.getErrorCode() + " : " + e.getLocalizedMessage());
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        }  else {
            Constants.isSync = false;
            if (iDaySummViewPresenter != null) {
                iDaySummViewPresenter.hideProgressDialog();
                displayUIList(true);
                if (Constants.writeDebug)
                    LogManager.writeLogDebug("Dashboard refresh failed:" + errorBean.getErrorCode() + " : " + e.getLocalizedMessage());
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        if (!Constants.isStoreClosed) {
            if (i == Operation.OfflineRefresh.getValue()) {
                Log.d("Testing","onRequestSuccess : OfflineRefresh");
                Constants.updateLastSyncTimeToTable(alAssignColl,context,Constants.DB_pull_sync,refguid.toString().toUpperCase());
                ConstantsUtils.startAutoSync(context,false);
//                ConstantsUtils.serviceReSchedule(context, true);
                if (iDaySummViewPresenter != null) {
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false);
                }
                Constants.isSync = false;
                totalMTPCount = "";
                totalSOCount = "";
                displayUIList(true);
                getMTPApprovalCount(false);
                requestMasterCountDBs(false);
                requestTransactionCountDBs(false);
//                displayUIMonth();
            } else if (i == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
                Constants.isSync = false;
                try {
                    OfflineManager.getAuthorizations(context);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
//                ConstantsUtils.serviceReSchedule(context, true);
                if (iDaySummViewPresenter != null) {
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false);
                }
                // Constants.setSyncTime(context);
                ConstantsUtils.startAutoSync(context,false);
                totalMTPCount = "";
                totalSOCount = "";
                displayUIList(true);
                getMTPApprovalCount(Constants.getRollID(context));
                requestMasterCountDBs(false);
                requestTransactionCountDBs(false);
//                displayUIMonth();
            }
        }


    }

    /**
     * sync Dealer Behaviour online
     *
     * @param collectionName
     */
    private void getTargets(@NonNull ArrayList<String> collectionName) {
        Log.d("Testing","getTargets");
//        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
//        if (UtilConstants.isNetworkAvailable(context)) {
//        alAssignColl.clear();
        concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(collectionName);
        if (Constants.iSAutoSync) {
            UtilConstants.showAlert(context.getString(R.string.alert_auto_sync_is_progress), context);
        } else {
            try {
                Constants.isSync = true;
                refguid = GUID.newRandom();
                Constants.updateStartSyncTime(context, Constants.DB_pull_sync, Constants.StartSync,refguid.toString().toUpperCase());

                //+ Arrays.asList(collectionName)+" Started");
                for (int i = 0; i < collectionName.size(); i++) {
                    if (Constants.writeDebug)
                        LogManager.writeLogDebug("Dashboard refresh sync : " + collectionName.get(i) + " started");
                }
                new RefreshAsyncTask(context, concatCollectionStr, this).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
       /* } else {
            UtilConstants.showAlert(context.getString(R.string.no_network_conn), context);
        }*/
    }

    public void getCountRequest(boolean readFromCache) {
//        totalRequest = 0;
//        currentRequest = 0;
//        totalMTPCount = "";
//        totalSOCount = "";

        if (!TextUtils.isEmpty(totalMTPCount) || !TextUtils.isEmpty(totalSOCount)) {
            if (!isReloadSOApproval && !isReloadMTPApproval) {
                if (!TextUtils.isEmpty(SOApproveActivity.SOTotalCount))
                    totalSOCount = SOApproveActivity.SOTotalCount;
                if (!TextUtils.isEmpty(MTPApprovalPresenter.mtpTotalCount))
                    totalMTPCount = MTPApprovalPresenter.mtpTotalCount;
                refreshUI("");
            } else {
                if (isReloadSOApproval) {
                    readFromCache = false;
                    totalSOCount = "";
                    requestSO(readFromCache);
                    isReloadSOApproval = false;
                }
                if (isReloadMTPApproval) {
                    readFromCache = false;
                    totalMTPCount = "";
                    requestMTP(readFromCache);
                    isReloadMTPApproval = false;
                }
            }

        } else {
            requestMTP(readFromCache);
            requestSO(readFromCache);
        }

    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> entities, int operation, int requestCode, String resourcePath, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        switch (type) {
            case 1:
                if (entities == null && bundle.getBoolean(UtilConstants.BUNDLE_READ_FROM_TECHNICAL_CACHE)) {
                    bundle.putBoolean(Constants.BUNDLE_READ_FROM_TECHNICAL_CACHE, false);
                    try {
                        OnlineManager.requestOnline(this, bundle, context);
                    } catch (Exception e) {
                        totalRequest--;
                        e.printStackTrace();
                        ConstantsUtils.printErrorLog(e.getMessage());
                    }
                } else {
                    Log.d("Testing", "responseSuccessMTPCount");
                    if (Constants.writeDebug)
                        LogManager.writeLogDebug("Dashboard refresh : Success for MTP");
                    if (entities != null) {
//                    totalMTPCount = String.valueOf(entities.size());
                        try {
                            totalMTPCount = OfflineManager.getRouteCount(entities);
                            if (Constants.writeDebug)
                                LogManager.writeLogDebug("Dashboard refresh : MTPCount " + totalMTPCount);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                        try {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constants.TotalMTPCount, totalMTPCount);
                            editor.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        MTPApprovalPresenter.mtpTotalCount = totalMTPCount;
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (iDaySummViewPresenter != null) {
                                iDaySummViewPresenter.hideMTPProgress();
                                iDaySummViewPresenter.disPlayMTPCount(totalMTPCount);
                            }
                        }
                    });
                    currentRequest++;
                }
                break;
            case 2:
                if (entities == null && bundle.getBoolean(UtilConstants.BUNDLE_READ_FROM_TECHNICAL_CACHE)) {
                    bundle.putBoolean(Constants.BUNDLE_READ_FROM_TECHNICAL_CACHE, false);
                    try {
                        OnlineManager.requestOnline(this, bundle, context);
                    } catch (Exception e) {
                        totalRequest--;
                        e.printStackTrace();
                        ConstantsUtils.printErrorLog(e.getMessage());
                    }
                } else {
                    Log.d("Testing", "responseSuccess : TotalSOCount");
                    if (Constants.writeDebug)
                        LogManager.writeLogDebug("Dashboard refresh : Success SO Count");

                    if (entities != null) {
                        totalSOCount = String.valueOf(entities.size());
                        if (Constants.writeDebug)
                            LogManager.writeLogDebug("Dashboard refresh : SO Count " + totalSOCount);
                        try {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constants.TotalSOCount, totalSOCount);
                            editor.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        SOApproveActivity.SOTotalCount = totalSOCount;
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (iDaySummViewPresenter != null) {
                                iDaySummViewPresenter.hideSOProgress();
                                iDaySummViewPresenter.disPlaySOCount(totalSOCount);
                            }
                        }
                    });
                    currentRequest++;
                }
                break;

            case 4:
                if (entities == null && bundle.getBoolean(UtilConstants.BUNDLE_READ_FROM_TECHNICAL_CACHE)) {
                    bundle.putBoolean(Constants.BUNDLE_READ_FROM_TECHNICAL_CACHE, false);
                    try {
                        OnlineManager.requestOnlineDashBoard(this, bundle, context);
                    } catch (Exception e) {
                        currentRequestDasgboard--;
                        e.printStackTrace();
                        ConstantsUtils.printErrorLog(e.getMessage());
                    }
                } else {
                    Log.d("Testing", "responseSuccess : getMasterCountDB");
                    if (Constants.writeDebug)
                        LogManager.writeLogDebug("Dashboard refresh: Success Dashboard Master Count");
                    if (entities != null) {
                        try {
                            alDashBoardBeansMast.clear();
                            alDashBoardBeansMast = OnlineManager.getMasterCountDB(entities);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        currentRequestDasgboard++;
                    }
                }
                break;
            case 5:
                if (entities == null && bundle.getBoolean(UtilConstants.BUNDLE_READ_FROM_TECHNICAL_CACHE)) {
                    bundle.putBoolean(Constants.BUNDLE_READ_FROM_TECHNICAL_CACHE, false);
                    try {
                        OnlineManager.requestOnlineDashBoard(this, bundle, context);
                    } catch (Exception e) {
                        currentRequestDasgboard--;
                        e.printStackTrace();
                        ConstantsUtils.printErrorLog(e.getMessage());
                    }
                } else {
                    Log.d("Testing","responseSuccess : getTransactionCountDBs");
                    if (entities != null) {

                        try {
                            alDashBoardBeansTran.clear();
                            alDashBoardBeansTran = OnlineManager.getTransactionCountDBs(entities);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        currentRequestDasgboard++;
                    }
                }

                break;
        }
        if (currentRequestDasgboard == totalRequestDashBoard) {
            try {
                dashBoardBeans.clear();
                if (alDashBoardBeansMast!=null&&!alDashBoardBeansMast.isEmpty()) {
                    dashBoardBeans.addAll(alDashBoardBeansMast);
                }
                if (alDashBoardBeansTran!=null&&!alDashBoardBeansTran.isEmpty()) {
                    dashBoardBeans.addAll(alDashBoardBeansTran);
                }
                Gson gson = new Gson();
                String json = gson.toJson(dashBoardBeans);

//                HashSet boardBeanSet = new HashSet(dashBoardBeans);
                SharedPreferences sharedPreferences1 = context.getSharedPreferences(Constants.PREFS_NAME, 0);
                SharedPreferences.Editor editor = sharedPreferences1.edit();
                editor.putString(Constants.SharMonthDashBoardList, json);
                editor.commit();


            } catch (Exception e) {
                e.printStackTrace();
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (iDaySummViewPresenter != null) {
                        if (dashBoardBeans!=null&&!dashBoardBeans.isEmpty()) {
                            iDaySummViewPresenter.displayDashBoardList(dashBoardBeans);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, int operation, int requestCode, String resourcePath, final String errorMsg, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        if(type==4 || type==5){
            currentRequestDasgboard++;
            if (currentRequestDasgboard == totalRequestDashBoard) {
                currentRequestDasgboard = 0;
//                requestTransactionCountDBs(true);
//                requestMasterCountDBs(true);
                /*((Activity)activity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(iDaySummViewPresenter != null){
                            iDaySummViewPresenter.showDashDayProgress();
                        }
                    }
                });*/
                ((Activity) activity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iDaySummViewPresenter != null) {
                            iDaySummViewPresenter.hideProgressDialog();
                            iDaySummViewPresenter.displayList(alTargets);
                            iDaySummViewPresenter.displayDashBoardList(dashBoardBeans);
                        }
                    }
                });
                if (Constants.writeDebug)
                    LogManager.writeLogDebug("Dashboard refresh failed:" + errorMsg);
                refreshErrorUI(errorMsg);
            }
        }else {
            currentRequest++;
            if (currentRequest == totalRequest) {
                if (Constants.writeDebug)
                    LogManager.writeLogDebug("Dashboard refresh failed:" + errorMsg);
                refreshUI(errorMsg);
            }else{
                if(errorMsg.contains("HTTP Status 401 ? Unauthorized")){
                    if (Constants.writeDebug) {
                        LogManager.writeLogDebug("Dashboard refresh failed:" + errorMsg);
                    }
                    refreshUI(errorMsg);
                }
            }
        }
    }

    private void displayUIList(final boolean displayattendance) {
        if (iDaySummViewPresenter != null) {
            iDaySummViewPresenter.showProgressDialog();
        }
        Log.d("Testing","displayUIList");
        new Thread(new Runnable() {
            @Override
            public void run() {
                getDataFromDB();
                ((Activity) activity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iDaySummViewPresenter != null) {
                            iDaySummViewPresenter.hideProgressDialog();

                            iDaySummViewPresenter.displayList(alTargets);
                           /* if(displayattendance) {
                                Log.d("DaySummary Fragment"," displayattendance true");

                                iDaySummViewPresenter.displayAttendanceview();
                                iDaySummViewPresenter.hideAttendancePB();
                            }else{
                                Log.d("DaySummary Fragment"," displayattendance false");

                            }*/
                            iDaySummViewPresenter.displayDashBoardList(dashBoardBeans);
                        }
                    }
                });
            }
        }).start();
    }

    private void refreshUI(final String errorMsg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (iDaySummViewPresenter != null) {
                    iDaySummViewPresenter.hideMTPProgress();
                    iDaySummViewPresenter.hideSOProgress();
                    iDaySummViewPresenter.disPlayMTPCount(totalMTPCount);
                    iDaySummViewPresenter.disPlaySOCount(totalSOCount);
                    if (!TextUtils.isEmpty(errorMsg)) {
                        iDaySummViewPresenter.showMessage(errorMsg);
                    }
                }
            }
        });
    }
    private void refreshErrorUI(final String errorMsg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (iDaySummViewPresenter != null) {
                    iDaySummViewPresenter.displayDashBoardError(errorMsg);
                }
            }
        });
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME,0);
        switch (type) {
            case 3:
                Log.d("Testing","TotalOrderVale");
                try {
                    String mStrTotalOrderVale = OfflineManager.getTotalOrderValue(list);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constants.Total_Order_Value_KEY,mStrTotalOrderVale);
                    editor.commit();

                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iDaySummViewPresenter != null) {
//                            iDaySummViewPresenter.hideMTPProgress();
                            iDaySummViewPresenter.refreshTotalOrderVale();
                        }
                    }
                });
        }
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, String errorMsg, Bundle bundle) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (iDaySummViewPresenter != null) {
                    iDaySummViewPresenter.refreshTotalOrderVale();
                }
            }
        });
    }

    public void getAttendance() {
        if (sharedPreferences.getString(Constants.isStartCloseEnabled, "").equalsIgnoreCase(Constants.isStartCloseTcode)) {
            if (iDaySummViewPresenter != null) {
                iDaySummViewPresenter.showAttendancePB();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ((Activity) activity).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (iDaySummViewPresenter != null) {
                                iDaySummViewPresenter.displayAttendanceview();
                                iDaySummViewPresenter.hideAttendancePB();
                            }
                        }
                    });
                }
            }).start();

        }
    }
}
