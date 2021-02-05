package com.rspl.sf.msfa.reports.materialgrouptargets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.TextUtils;

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
import com.rspl.sf.msfa.filter.DateFilterFragment;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.mbo.MyTargetsBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.store.OnlineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

//import com.arteriatech.sf.reports.behaviourlist.filter.BehaviourFilterActivity;

/**
 * Created by e10893 on 25-01-2018.
 */

public class MatGroupTargetPresenterImpl implements MaterialGroupPresenter, UIListener {

    ArrayList<String> alAssignColl = null;
    Map<String, Double> mapMonthAchived = new HashMap<>();
    Map<String, Double> mapMonthTarget = new HashMap<>();
    private Context context;
    private MatGroupTargetViewPresenter iTargetViewPresenter = null;
    private Activity activity;
    private ArrayList<MyTargetsBean> alTargets;
    private ArrayList<MyTargetsBean> searchTargetAL;
    private Hashtable<String, String> headerTable;
    private String SPGUID = "";
    private String searchText = "";
    private String CPGUID = "", CPUID = "", cpNo = "", cpName = "";
    private String startDate = "";
    private String endDate = "";
    private String filterType = "";
    private String delvStatusId = "";
    private String statusId = "";
    private String statusName = "";
    private String delvStatusName = "";
    private boolean isErrorFromBackend = false;
    private ArrayList<MyTargetsBean> alKpiList = null;
    private ArrayList<MyTargetsBean> alMyTargets = null;
    private Map<String, MyTargetsBean> mapMyTargetVal = new HashMap<>();
    MyTargetsBean salesKpi=null;
    Bundle bundleExtras=null;
    HashMap<String, String> mapUOM =null;

    private GUID refguid =null;

    private String mStrBundleKpiCode ="",mStrBundleKpiName="",mStrBundleKpiGUID="",mStrBundleRollup="",
            mStrBundleKpiFor="",mStrBundleCalBased="",mStrBundleCalSource="",mStrParnerGuid="",mStrBundleKPICat="",mStrPeriodicity="",mStrPeriodicityDesc="";
    public MatGroupTargetPresenterImpl(Context context, MatGroupTargetViewPresenter iTargetViewPresenter, Activity activity, Bundle bundleExtras) {
        this.context = context;
        this.iTargetViewPresenter = iTargetViewPresenter;
        this.alTargets = new ArrayList<>();
        this.searchTargetAL = new ArrayList<>();
        this.headerTable = new Hashtable<>();
        this.activity = activity;
        this.bundleExtras = bundleExtras;
    }


    @Override
    public void onFilter() {

        if (iTargetViewPresenter != null) {
            iTargetViewPresenter.openFilter(startDate, endDate, filterType, statusId, delvStatusId);
        }

    }

    @Override
    public void onSearch(String searchText) {
        if (!this.searchText.equalsIgnoreCase(searchText)) {
            this.searchText = searchText;
            onSearchQuery(searchText);
        }
    }

    @Override
    public void onStart() {
        getDataFromOffline();
    }

    @Override
    public void onDestroy() {
        iTargetViewPresenter = null;
    }

    public void getDataFromOffline() {
        if (iTargetViewPresenter != null) {
            iTargetViewPresenter.showProgressDialog();

        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                getDataFromDB();

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iTargetViewPresenter != null) {
                            iTargetViewPresenter.hideProgressDialog();

                            iTargetViewPresenter.displayList(alTargets);
                        }
                    }
                });
            }
        }).start();


    }

    private void getDataFromDB() {
        alTargets = new ArrayList<>();
//        String mStrTotalOrderVal = Constants.getTotalOrderValueByCurrentMonth(Constants.getFirstDateOfCurrentMonth(), "", "");
        bundleExtras = activity.getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleKpiCode = bundleExtras.getString(Constants.KPICode);
            mStrBundleKpiName = bundleExtras.getString(Constants.KPIName);
            mStrBundleKpiGUID = bundleExtras.getString(Constants.KPIGUID);
            mStrBundleCalBased = bundleExtras.getString(Constants.CalculationBase);
            mStrBundleKpiFor = bundleExtras.getString(Constants.KPIFor);
            mStrBundleRollup = bundleExtras.getString(Constants.RollUpTo);
            mStrBundleCalSource= bundleExtras.getString(Constants.CalculationSource);
            mStrBundleKPICat= bundleExtras.getString(Constants.KPICategory);
            mStrParnerGuid = bundleExtras.getString(Constants.PartnerMgrGUID);
            mStrPeriodicity = bundleExtras.getString(Constants.Periodicity);
            mStrPeriodicityDesc = bundleExtras.getString(Constants.PeriodicityDesc);
        }
        getSystemKPI();
        getMyTargetsList();
//        alTargets = getValuesFromMap(mapMyTargetVal, mStrTotalOrderVal);
//        alTargets = getValuesFromMap(mapMyTargetVal, "0.00");
    }

    private void onSearchQuery(String searchText) {
        this.searchText = searchText;
        searchTargetAL.clear();
        boolean soTypeStatus = false;
        boolean soDelStatus = false;
        boolean soSearchStatus = false;
        if (alTargets != null) {
            if (TextUtils.isEmpty(searchText)) {
                searchTargetAL.addAll(alTargets);
            } else {
                for (MyTargetsBean item : alTargets) {
                    soTypeStatus = false;
                    soDelStatus = false;
                    soSearchStatus = false;

                    if (!TextUtils.isEmpty(searchText)) {
                        soSearchStatus = item.getKPIName().toLowerCase().contains(searchText.toLowerCase());
                    } else {
                        soSearchStatus = true;
                    }
                    if (soSearchStatus)
                        searchTargetAL.add(item);
                }
            }
        }
        if (iTargetViewPresenter != null) {
            iTargetViewPresenter.searchResult(searchTargetAL);
        }
    }

    @Override
    public void onRefresh() {
        alAssignColl = new ArrayList<>();
        alAssignColl.add(Constants.Targets);
        alAssignColl.add(Constants.TargetItems);
        alAssignColl.add(Constants.KPISet);
        alAssignColl.add(Constants.KPIItems);
        alAssignColl.add(Constants.ConfigTypsetTypeValues);
        getTargets(alAssignColl);
    }

    @Override
    public void startFilter(int requestCode, int resultCode, Intent data) {
        filterType = data.getStringExtra(DateFilterFragment.EXTRA_DEFAULT);
//        statusId = data.getStringExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_STATUS);
//        statusName = data.getStringExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_STATUS_NAME);
//        delvStatusId = data.getStringExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_DELV_STATUS);
//        delvStatusName = data.getStringExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_DELV_STATUS_NAME);
        displayFilterType();
    }



    private ArrayList<MyTargetsBean> getValuesFromMap(Map<String, MyTargetsBean> mapMyTargetVal, String totalOrderVal) {
        ArrayList<MyTargetsBean> alTargets = new ArrayList<>();
        if (!mapMyTargetVal.isEmpty()) {
            Iterator iterator = mapMyTargetVal.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                MyTargetsBean myTargetsBean = mapMyTargetVal.get(key);
                myTargetsBean.setMonthTarget(mapMonthTarget.get(key).toString());

                Double morderVal = 0.0;
                double btdVal = 0;
                double achivedPer = 0;
                try {
                    if (myTargetsBean.getKPIName().contains("Sales")) {
                        morderVal = Double.parseDouble(totalOrderVal);
                    }
                    myTargetsBean.setMTDA((mapMonthAchived.get(key) + morderVal) + "");
                    btdVal = OfflineManager.getBTD(mapMonthTarget.get(key).toString(), (mapMonthAchived.get(key) + morderVal) + "");
                    achivedPer = OfflineManager.getAchivedPer(mapMonthTarget.get(key).toString(), (mapMonthAchived.get(key) + morderVal) + "");
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                myTargetsBean.setBTD(btdVal + "");
                myTargetsBean.setAchivedPercentage(achivedPer + "");

                alTargets.add(mapMyTargetVal.get(key));
            }
        }
        return alTargets;
    }

    /*Gets kpiList for selected month and year*/
    private void getSystemKPI() {
        try {
//            String mStrMyStockQry;
//            mStrMyStockQry = Constants.KPISet + "?$filter = " + Constants.ValidTo + " ge datetime'" + UtilConstants.getNewDate() + "' and "+Constants.Periodicity+" eq '04' ";

            try {
//                salesKpi = OfflineManager.getSpecificKpi(Constants.KPISet + "?$filter ="+Constants.ValidTo+" ge datetime'" + UtilConstants.getNewDate() + "'" +
//                        " and "+Constants.Periodicity+" eq '"+04+"' and "+Constants.KPICategory+" eq '02' and "+Constants.CalculationBase+" eq '01' ");
                salesKpi = OfflineManager.getSpecificKpi(Constants.KPISet + "?$filter ="+Constants.ValidTo+" ge datetime'" + UtilConstants.getNewDate() + "'" +
                        " and "+Constants.KPIGUID+" eq guid'"+mStrBundleKpiGUID+"'");
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            if(salesKpi!=null){
                if (alKpiList!=null) {
                    alKpiList.add(salesKpi);
                }
            }

//            alKpiList = OfflineManager.getKpiSetGuidList(mStrMyStockQry, "");

        } catch (Exception e) {
            e.printStackTrace();
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }

    }

    /*Get targets for sales person  based on query*/
    private void getMyTargetsList() {
       /* try {
            if (alKpiList != null && alKpiList.size() > 0) {
                alMyTargets = OfflineManager.getMyTargets(alKpiList, Constants.getSPGUID(Constants.SPGUID),Constants.str_04);
            }
            mapMyTargetVal = getALMyTargetList(alMyTargets);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }*/
        String qry = Constants.ConfigTypesetTypes + "?$filter=" + Constants.Typeset + " eq '" + Constants.UOMNO0 + "' ";
        try {
            mapUOM =new HashMap<>();
            mapUOM = OfflineManager.getUOMMapVal(qry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        try {
            String qryTargets = "";
            if(mStrPeriodicity.equalsIgnoreCase(Constants.str_04)){
                 qryTargets = Constants.Targets+ "?$filter=" +Constants.KPIGUID+ " eq guid'"
                        + mStrBundleKpiGUID+"' and "+Constants.Periodicity+" eq '"+mStrPeriodicity+"' " ;
            }else{
                final Calendar c = Calendar.getInstance();
                int fiscalYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);

                String mon = "";
                String day = "";
                int mnt = 0;
                mnt = mMonth + 1;
                if (mnt < 10)
                    mon = "0" + mnt;
                else
                    mon = "" + mnt;
                String mStrMonthYear =  mon + fiscalYear;
                 qryTargets = Constants.Targets+ "?$filter=" +Constants.KPIGUID+ " eq guid'"
                        + mStrBundleKpiGUID+"' and "+Constants.Periodicity+" eq '"+mStrPeriodicity+"'  and "+Constants.Period+" eq '"+mStrMonthYear+"' ";
            }

            ArrayList<MyTargetsBean> alMyTargets = OfflineManager.getMyTargetsList(qryTargets, mStrParnerGuid,
                    mStrBundleKpiName, mStrBundleKpiCode, mStrBundleKpiGUID,
                    mStrBundleCalBased, mStrBundleKpiFor,
                    mStrBundleCalSource, mStrBundleRollup,mStrBundleKPICat,true,mStrPeriodicity,mStrPeriodicityDesc);

//            ArrayList<MyTargetsBean> alOrderValByOrderMatGrp = OfflineManager.getActualTargetByOrderMatGrp(CRSSKUGroupWiseTargetsActivity.this,mStrCPDMSDIV);
//            mapMyTargetValByCRSSKU = getALOrderVal(alOrderValByOrderMatGrp);
            mapMonthAchived = new HashMap<>();
            mapMonthTarget = new HashMap<>();
            mapMyTargetVal = getALMyTargetList(alMyTargets);
            sortingValues();
        } catch (OfflineODataStoreException e) {
            sortingValues();
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
    }
    private void sortingValues(){
        MyTargetsBean myTarTemp;
        if(/*!mapOrderValAch.isEmpty() ||*/ !mapMonthTarget.isEmpty()){
//            if(!mapOrderValAch.isEmpty()) {
//                Iterator iter = mapOrderValAch.keySet().iterator();
//                while (iter.hasNext()) {
//                    String key = iter.next().toString();
//                    myTarTemp = new MyTargetsBean();
//
//                    MyTargetsBean myTargetsBean = mapMyTargetValByCRSSKU.get(key);
//
//                    String orderMatDesc = "";
//                    if (myTargetsBean.getOrderMaterialGroupDesc().equalsIgnoreCase("")) {
//                        try {
//                            orderMatDesc = OfflineManager.getValueByColumnName(Constants.OrderMaterialGroups + "?$select=" + Constants.OrderMaterialGroupDesc + " &$filter = "
//                                    + Constants.OrderMaterialGroupID + " eq '" + key + "'", Constants.OrderMaterialGroupDesc);
//                        } catch (OfflineODataStoreException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        orderMatDesc = myTargetsBean.getOrderMaterialGroupDesc();
//
//                    }
//
//                    myTarTemp.setOrderMaterialGroupDesc(orderMatDesc);
//
//                    String mDouMonthTarget = "0";
//                    try {
//                        mDouMonthTarget = mapMonthTarget.get(key).toString();
//                    } catch (Exception e) {
//                        mDouMonthTarget = "0";
//                    }
//
//                    Double mDouOrderVal = 0.0;
//                    try {
//                        if (mapOrderValAch.containsKey(key)) {
//                            mDouOrderVal = mapOrderValAch.get(key);
//                        } else {
//                            mDouOrderVal = 0.0;
//                        }
//                    } catch (NumberFormatException e) {
//                        mDouOrderVal = 0.0;
//                    }
//
//                    if (mDouOrderVal.isInfinite() || mDouOrderVal.isNaN()) {
//                        mDouOrderVal = 0.0;
//                    }
//                    if (mDouOrderVal == null) {
//                        mDouOrderVal = 0.0;
//                    }
//
//                    Double achivedVal = mDouOrderVal.doubleValue();
//
//                    myTarTemp.setMonthTarget(mDouMonthTarget + "");
//                    myTarTemp.setMTDA(achivedVal + "");
//
//                    double achivedPer = OfflineManager.getAchivedPer(mDouMonthTarget, achivedVal.toString());
//
//                    double BTDPer = OfflineManager.getBTD(mDouMonthTarget, achivedVal.toString());
//
//                    myTarTemp.setBTD(BTDPer + "");
//                    myTarTemp.setAchivedPercentage(achivedPer + "");
//                    myTarTemp.setOrderMaterialGroupID(key);
//                    alTarget.add(myTarTemp);
//                }
//            }
            if(!mapMonthTarget.isEmpty()) {
                Iterator iterMapTarget = mapMonthTarget.keySet().iterator();
                while (iterMapTarget.hasNext()) {
                    myTarTemp =new MyTargetsBean();
                    String key = iterMapTarget.next().toString();
                    MyTargetsBean myTargetsBean = mapMyTargetVal.get(key);
//                    if(!mapMonthAchived.containsKey(key)){
                       /* String orderMatDesc = "";
                        if (myTargetsBean.getMaterialGrpDesc().equalsIgnoreCase("")) {
                            try {
                                orderMatDesc = OfflineManager.getValueByColumnName(Constants.OrderMaterialGroups + "?$select=" + Constants.OrderMaterialGroupDesc + " &$filter = "
                                        + Constants.OrderMaterialGroupID + " eq '" + key + "'", Constants.OrderMaterialGroupDesc);
                            } catch (OfflineODataStoreException e) {
                                e.printStackTrace();
                            }
                        } else {
                            orderMatDesc = myTargetsBean.getMaterialGrpDesc();
                        }*/

                        myTarTemp.setMaterialGroup(myTargetsBean.getMaterialGroup());
                        myTarTemp.setMaterialGrpDesc(myTargetsBean.getMaterialGrpDesc());
                        myTarTemp.setCalculationBase(myTargetsBean.getCalculationBase());
                        myTarTemp.setTargetItemGUID(myTargetsBean.getTargetItemGUID());
                        myTarTemp.setTargetGUID(myTargetsBean.getTargetGUID());
                        myTarTemp.setUOM(myTargetsBean.getUOM());

                        String mDouMonthTarget = "0";
                        try {
                            mDouMonthTarget = mapMonthTarget.get(key).toString();
                        } catch (Exception e) {
                            mDouMonthTarget = "0";
                        }
                    Double mDoubleMAPAch = null;
                    try {
                        mDoubleMAPAch = mapMonthAchived.get(key);
                    } catch (Exception e) {
                        mDoubleMAPAch = 0.0;
                        e.printStackTrace();
                    }

                    if(mDoubleMAPAch.isInfinite() || mDoubleMAPAch.isNaN()){
                        mDoubleMAPAch = 0.0;
                    }

                    myTarTemp.setMonthTarget(mDouMonthTarget+"");
                        myTarTemp.setMTDA(mDoubleMAPAch+"");

                        double achivedPer = OfflineManager.getAchivedPer(mDouMonthTarget,mDoubleMAPAch.toString());

                        double BTDPer = OfflineManager.getBTD(mDouMonthTarget,mDoubleMAPAch.toString());
                    if (BTDPer < 0) {
                        BTDPer = 0.0;
                    }

                        myTarTemp.setBTD(BTDPer+"");
                        myTarTemp.setAchivedPercentage(achivedPer+"");
                        myTarTemp.setOrderMaterialGroupID(key);

                    if(myTargetsBean.getCalculationBase().equalsIgnoreCase(Constants.str_01)){
                        try {
                            if (mapUOM.containsKey(myTargetsBean.getUOM())) {
                                myTarTemp.setMTDA(OnlineManager.trimQtyDecimalPlace(myTarTemp.getMTDA() + ""));
                                myTarTemp.setMonthTarget(OnlineManager.trimQtyDecimalPlace(myTarTemp.getMonthTarget() + ""));
                                myTarTemp.setBTD(OnlineManager.trimQtyDecimalPlace(myTarTemp.getBTD() + ""));
                            }else {
                                myTarTemp.setMTDA(UtilConstants.removeLeadingZeroQuantity(myTarTemp.getMTDA() + ""));
                                myTarTemp.setMonthTarget(UtilConstants.removeLeadingZeroQuantity(myTarTemp.getMonthTarget() + ""));
                                myTarTemp.setBTD(UtilConstants.removeLeadingZeroQuantity(myTarTemp.getBTD() + ""));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                        alTargets.add(myTarTemp);
//                    }
                }
            }
        }

        if(alTargets!=null && alTargets.size()>0){
            Collections.sort(alTargets, new Comparator<MyTargetsBean>() {
                public int compare(MyTargetsBean one, MyTargetsBean other) {
                    return one.getMaterialGrpDesc().compareTo(other.getMaterialGrpDesc());
                }
            });
        }

    }
    //ToDo sum of actual and target quantity/Value based on kpi code and assign to map table
    private Map<String, MyTargetsBean> getALMyTargetList(ArrayList<MyTargetsBean> alMyTargets) {
        Map<String, MyTargetsBean> mapMyTargetBean = new HashMap<>();
        if (alMyTargets != null && alMyTargets.size() > 0) {
            for (MyTargetsBean bean : alMyTargets)
                if (mapMonthTarget.containsKey(bean.getMaterialGroup())) {
                    double mDoubMonthTarget = Double.parseDouble(bean.getMonthTarget()) + mapMonthTarget.get(bean.getMaterialGroup());
                    double mDoubMonthAchived = Double.parseDouble(bean.getMTDA()) + mapMonthAchived.get(bean.getMaterialGroup());

                    mapMonthTarget.put(bean.getMaterialGroup(), mDoubMonthTarget);
                    mapMonthAchived.put(bean.getMaterialGroup(), mDoubMonthAchived);
                    mapMyTargetBean.put(bean.getMaterialGroup(), bean);
                } else {
                    double mDoubMonthTarget = Double.parseDouble(bean.getMonthTarget());
                    double mDoubMonthAchived = Double.parseDouble(bean.getMTDA());
                    double mDoubAchivedPer = Double.parseDouble(bean.getAchivedPercentage());
                    double mDoubBTD = Double.parseDouble(bean.getBTD());

                    mapMonthTarget.put(bean.getMaterialGroup(), mDoubMonthTarget);
                    mapMonthAchived.put(bean.getMaterialGroup(), mDoubMonthAchived);
                    mapMyTargetBean.put(bean.getMaterialGroup(), bean);
                }
        }


        return mapMyTargetBean;
    }

    private void displayFilterType() {
        try {
            String statusDesc = "";
            if (!TextUtils.isEmpty(statusId)) {
                statusDesc = ", " + statusName;
            }
            if (!TextUtils.isEmpty(delvStatusId)) {
                statusDesc = statusDesc + ", " + delvStatusName;
            }
            if (iTargetViewPresenter != null) {
                iTargetViewPresenter.setFilterDate(statusDesc);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onRequestError(int i, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(i, e, context);
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
            if (!Constants.isStoreClosed) {
                if (i == Operation.OfflineRefresh.getValue()) {
                    Constants.isSync = false;
                    if (!Constants.isStoreClosed) {
                        if (iTargetViewPresenter != null) {
                            iTargetViewPresenter.hideProgressDialog();
                            iTargetViewPresenter.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                        }

                    } else {
                        if (iTargetViewPresenter != null) {
                            iTargetViewPresenter.hideProgressDialog();
                            iTargetViewPresenter.showMessage(context.getString(R.string.msg_sync_terminated));
                        }
                    }
                }else if (i == Operation.GetStoreOpen.getValue()){
                    Constants.isSync = false;
                    if (iTargetViewPresenter != null) {
                        iTargetViewPresenter.hideProgressDialog();
                        iTargetViewPresenter.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                    }
                }
            }

        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (iTargetViewPresenter != null) {
                    iTargetViewPresenter.showProgressDialog();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (iTargetViewPresenter != null) {
                    iTargetViewPresenter.hideProgressDialog();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        }else {
            Constants.isSync = false;
            if (iTargetViewPresenter != null) {
                iTargetViewPresenter.hideProgressDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        if (!Constants.isStoreClosed) {
            if (i == Operation.OfflineRefresh.getValue()) {
           //     Constants.updateLastSyncTimeToTable(alAssignColl,context,Constants.DownLoad);
                Constants.isSync = false;
                Constants.updateLastSyncTimeToTable(alAssignColl,context,Constants.MatGrpTrg_sync,refguid.toString().toUpperCase());
                ConstantsUtils.startAutoSync(context,false);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iTargetViewPresenter != null) {
                            getDataFromOffline();
                            AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false);
                        }
                    }
                });
            } else if (i == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
                Constants.isSync = false;
                try {
                    OfflineManager.getAuthorizations(context);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                ConstantsUtils.startAutoSync(context,false);
             //   Constants.setSyncTime(context);
                if (iTargetViewPresenter != null) {
                    getDataFromOffline();
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false);
                }
            }

        }
    }

    /**
     * sync Dealer Behaviour online
     *
     * @param collectionName
     */
    private void getTargets(@NonNull ArrayList<String> collectionName) {
       // alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(context)) {
         //   alAssignColl.clear();
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(collectionName);
            if (Constants.iSAutoSync) {
                if (iTargetViewPresenter != null) {
                    iTargetViewPresenter.hideProgressDialog();
                    iTargetViewPresenter.displayMessage(context.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    refguid = GUID.newRandom();
                    Constants.updateStartSyncTime(context,Constants.MatGrpTrg_sync,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (iTargetViewPresenter != null) {
                iTargetViewPresenter.hideProgressDialog();
                iTargetViewPresenter.displayMessage(context.getString(R.string.no_network_conn));
            }
        }
    }
}
