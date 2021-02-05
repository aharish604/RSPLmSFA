package com.rspl.sf.msfa.reports.targets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import java.math.BigInteger;
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

public class TargetsPresenterImpl implements TargetPresenter, UIListener {

    ArrayList<String> alAssignColl = null;
    Map<String, Double> mapMonthAchived = new HashMap<>();
    Map<String, Double> mapMonthTarget = new HashMap<>();
    private Context context;
    private TargetViewPresenter iTargetViewPresenter = null;
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
    HashMap<String, String> mapUOM =null;
    private GUID refguid =null;

    public TargetsPresenterImpl(Context context, TargetViewPresenter iTargetViewPresenter, Activity activity) {
        this.context = context;
        this.iTargetViewPresenter = iTargetViewPresenter;
        this.alTargets = new ArrayList<>();
        this.searchTargetAL = new ArrayList<>();
        this.headerTable = new Hashtable<>();
        this.activity = activity;
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
        Constants.isComingFromDashBoard = false;
        getSystemKPI();
        getMyTargetsList();
        String qry = Constants.ConfigTypesetTypes + "?$filter=" + Constants.Typeset + " eq '" + Constants.UOMNO0 + "' ";

        try {
            mapUOM =new HashMap<>();
            mapUOM = OfflineManager.getUOMMapVal(qry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
//        alTargets = getValuesFromMap(mapMyTargetVal, mStrTotalOrderVal);
        alTargets = getValuesFromMap(mapMyTargetVal, "0.00");
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
        alAssignColl.clear();
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
                    if (btdVal < 0) {
                        btdVal = 0.0;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                myTargetsBean.setBTD(btdVal + "");
                myTargetsBean.setAchivedPercentage(achivedPer + "");
                myTargetsBean.setUOM(myTargetsBean.getUOM());
                if(myTargetsBean.getCalculationBase().equalsIgnoreCase(Constants.str_01)){
                    try {
                        if (mapUOM.containsKey(myTargetsBean.getUOM())) {
                            myTargetsBean.setMTDA(OnlineManager.trimQtyDecimalPlace(myTargetsBean.getMTDA() + ""));
                            myTargetsBean.setMonthTarget(OnlineManager.trimQtyDecimalPlace(myTargetsBean.getMonthTarget() + ""));
                            myTargetsBean.setBTD(OnlineManager.trimQtyDecimalPlace(myTargetsBean.getBTD() + ""));
                        }else {
                            myTargetsBean.setMTDA(UtilConstants.removeLeadingZeroQuantity(myTargetsBean.getMTDA() + ""));
                            myTargetsBean.setMonthTarget(UtilConstants.removeLeadingZeroQuantity(myTargetsBean.getMonthTarget() + ""));
                            myTargetsBean.setBTD(UtilConstants.removeLeadingZeroQuantity(myTargetsBean.getBTD() + ""));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                alTargets.add(mapMyTargetVal.get(key));
            }
        }
        Collections.sort(alTargets, new Comparator<MyTargetsBean>() {
            public int compare(MyTargetsBean one, MyTargetsBean other) {
                BigInteger i1 = null;
                BigInteger i2 = null;
                try {
                    i1 = new BigInteger(one.getKPICode());
                } catch (NumberFormatException e) {
                }

                try {
                    i2 = new BigInteger(other.getKPICode());
                } catch (NumberFormatException e) {
                }
                if (i1 != null && i2 != null) {
                    return i1.compareTo(i2);
                } else {
                    return one.getKPICode().compareTo(other.getKPICode());
                }
            }
        });
        return alTargets;
    }

    /*Gets kpiList for selected month and year*/
    private void getSystemKPI() {
        try {
            String mStrMyStockQry;
//            mStrMyStockQry = Constants.KPISet + "?$filter = " + Constants.ValidTo + " ge datetime'" + UtilConstants.getNewDate() + "'  and "+Constants.Periodicity+" eq '04' ";
            mStrMyStockQry = Constants.KPISet + "?$filter = " + Constants.ValidTo + " ge datetime'" + UtilConstants.getNewDate() + "'";

            alKpiList = OfflineManager.getKpiSetGuidList(mStrMyStockQry, "");

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }

    }

    /*Get targets for sales person  based on query*/
    private void getMyTargetsList() {
        try {
            if (alKpiList != null && alKpiList.size() > 0) {
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
                alMyTargets = OfflineManager.getMyTargets(alKpiList, Constants.getSPGUID(Constants.SPGUID),mStrMonthYear);
            }
            mapMonthTarget.clear();
            mapMonthAchived.clear();
            mapMyTargetVal = getALMyTargetList(alMyTargets);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
    }

    //ToDo sum of actual and target quantity/Value based on kpi code and assign to map table
    private Map<String, MyTargetsBean> getALMyTargetList(ArrayList<MyTargetsBean> alMyTargets) {
        Map<String, MyTargetsBean> mapMyTargetBean = new HashMap<>();
        if (alMyTargets != null && alMyTargets.size() > 0) {
            for (MyTargetsBean bean : alMyTargets)
                if (mapMonthTarget.containsKey(bean.getKPICode())) {
                    double mDoubMonthTarget = Double.parseDouble(bean.getMonthTarget()) + mapMonthTarget.get(bean.getKPICode());
                    double mDoubMonthAchived = Double.parseDouble(bean.getMTDA()) + mapMonthAchived.get(bean.getKPICode());

                    mapMonthTarget.put(bean.getKPICode(), mDoubMonthTarget);
                    mapMonthAchived.put(bean.getKPICode(), mDoubMonthAchived);
                    mapMyTargetBean.put(bean.getKPICode(), bean);
                } else {
                    double mDoubMonthTarget = Double.parseDouble(bean.getMonthTarget());
                    double mDoubMonthAchived = Double.parseDouble(bean.getMTDA());
                    double mDoubAchivedPer = Double.parseDouble(bean.getAchivedPercentage());
                    double mDoubBTD = Double.parseDouble(bean.getBTD());

                    mapMonthTarget.put(bean.getKPICode(), mDoubMonthTarget);
                    mapMonthAchived.put(bean.getKPICode(), mDoubMonthAchived);
                    mapMyTargetBean.put(bean.getKPICode(), bean);
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
                Constants.updateLastSyncTimeToTable(alAssignColl,context,Constants.Target_sync,refguid.toString().toUpperCase());
                Constants.isSync = false;
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
                Constants.setSyncTime(context,refguid.toString().toUpperCase());
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

        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(context)) {
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
                    Constants.updateStartSyncTime(context,Constants.Target_sync,Constants.StartSync,refguid.toString().toUpperCase());
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
