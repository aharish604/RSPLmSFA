package com.rspl.sf.msfa.reports.salesorder.pendingsync;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.TraceLog;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.rspl.sf.msfa.BuildConfig;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.asyncTask.RefreshAsyncTask;
import com.rspl.sf.msfa.asyncTask.SyncFromDataValtAsyncTask;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.DialogCallBack;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.mbo.SalesOrderBean;
import com.rspl.sf.msfa.reports.salesorder.header.SalesOrderHeaderListActivity;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by e10847 on 07-12-2017.
 */

public class SalesOrderHeaderPendingSyncPresenter implements ISalesOrderHeaderPendingSyncPresenter, UIListener {
    private Activity context = null;
    private String CPGUID = "";
    private ArrayList<SalesOrderBean> salesOrderHeaderArrayList = new ArrayList<>();
    private ISalesOrderPendingSyncView salesOrderView = null;
    private ArrayList<String> pendingCollectionList = null;
    private View view;
    private boolean isErrorFromBackend = false;
    private int penROReqCount = 0;
    private int pendingROVal = 0;
    private int isFromWhere = 0;
    private String[] tempRODevList = null;
    private boolean dialogCancelled = false;
    private String concatCollectionStr = "";
    private GUID refguid =null;

    public SalesOrderHeaderPendingSyncPresenter(Activity context, String CPGUID, ISalesOrderPendingSyncView salesOrderPendingSyncView, View view) {
        this.context = context;
        this.CPGUID = CPGUID;
        this.salesOrderView = salesOrderPendingSyncView;
        this.salesOrderHeaderArrayList = new ArrayList<>();
        this.pendingCollectionList = new ArrayList<>();
        this.view = view;
    }

    @Override
    public void connectToOfflineDB(final ISalesOrderPendingSyncView.SalesOrderResponse<SalesOrderBean> salesOrderResponse) {
        if (salesOrderView != null)
            salesOrderView.showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                salesOrderHeaderArrayList.clear();
                getSalesOrderList();
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (salesOrderView != null) {
                            salesOrderResponse.success(salesOrderHeaderArrayList);
                            salesOrderView.hideProgressDialog();
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onSync() {
        onSyncSOrder();
    }

    @Override
    public void onDestroy() {
        salesOrderView = null;
    }

    @Override
    public void onRequestError(int i, Exception e) {
        onError(i, e);
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        onSuccess(i, s);
    }

    /**
     * get SOs Pending Sync List from DataVault
     */
    private void getSalesOrderList() {
        try {
            salesOrderHeaderArrayList.addAll(OfflineManager.getSoListFromDataValt(context, CPGUID, false));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void onSyncSOrder() {
      //  deletePostedSOData();
        Constants.isPullDownSync=true;
        isErrorFromBackend = false;
        isFromWhere = 4;
        try {

                salesOrderHeaderArrayList.clear();
                salesOrderHeaderArrayList = (ArrayList<SalesOrderBean>) OfflineManager.getSoListFromDataValt(context, CPGUID, false);
                if (!salesOrderHeaderArrayList.isEmpty()) {

                    if (UtilConstants.isNetworkAvailable(context)) {
                        pendingCollectionList.clear();
                        pendingCollectionList.add(Constants.SOs);
                        pendingCollectionList.add(Constants.SOItemDetails);
                        pendingCollectionList.add(Constants.SOTexts);
                        pendingCollectionList.add(Constants.SOItems);
                        pendingCollectionList.add(Constants.SOConditions);
                        pendingCollectionList.add(Constants.ConfigTypsetTypeValues);
                        pendingROVal = 0;
                        if (tempRODevList != null) {
                            tempRODevList = null;
                            penROReqCount = 0;
                        }

                        if (salesOrderHeaderArrayList != null && salesOrderHeaderArrayList.size() > 0) {
                            tempRODevList = new String[salesOrderHeaderArrayList.size()];

                            for (com.rspl.sf.msfa.mbo.SalesOrderBean SalesOrderBean : salesOrderHeaderArrayList) {
                                tempRODevList[pendingROVal] = SalesOrderBean.getDeviceNo();
                                pendingROVal++;
                            }
                            if (Constants.iSAutoSync || Constants.isBackGroundSync) {
                                if (salesOrderView != null) {
                                    salesOrderView.hideProgressDialog();
                                    if(Constants.iSAutoSync) {
                                        salesOrderView.showMessage(context.getString(R.string.alert_auto_sync_is_progress));
                                    }else if(Constants.isBackGroundSync){
                                        salesOrderView.showMessage(context.getString(R.string.alert_backgrounf_sync_is_progress));
                                    }
                                    Constants.isPullDownSync=false;
                                }
                            }else {
                                if (salesOrderView != null) {
                                    Constants.isSync = true;
                                    salesOrderView.showProgressDialog();
                                    refguid = GUID.newRandom();
                                    Constants.updateStartSyncTime(context,Constants.SOPostPD_sync,Constants.StartSync,refguid.toString().toUpperCase());
                                    new SyncFromDataValtAsyncTask(context, tempRODevList, this, new DialogCallBack() {
                                        @Override
                                        public void clickedStatus(boolean clickedStatus) {

                                        }
                                    }).execute();
                                }
                            }
                        }
                    } else {
                        if (salesOrderView != null) {
                            salesOrderView.hideProgressDialog();
                            salesOrderView.showMessage(context.getString(R.string.no_network_conn));
                        }
                        Constants.isPullDownSync=false;
                    }
                } else {
                    if (salesOrderView != null) {
                        salesOrderView.hideProgressDialog();
                        salesOrderView.onReloadData();
                        salesOrderView.showMessage(context.getString(R.string.no_req_to_update_sap));
                    }
                }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            Constants.isPullDownSync=false;
            ConstantsUtils.printErrorLog(e.getMessage());
            if (salesOrderView != null) {
                salesOrderView.hideProgressDialog();
            }
        }
    }

    public void onError(int operation, Exception e) {
        Constants.isPullDownSync=false;
        e.printStackTrace();
        ErrorBean errorBean = Constants.getErrorCode(operation, e, context);
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
            if (isFromWhere == 1 || isFromWhere == 2) {
                if (!dialogCancelled && !Constants.isStoreClosed) {
                    if (operation == Operation.OfflineRefresh.getValue()) {
                        if (salesOrderView != null) {
                            salesOrderView.hideProgressDialog();
                            Constants.isSync = false;
                            if (!Constants.isStoreClosed) {
                                salesOrderView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                            } else {
                                salesOrderView.showMessage(context.getString(R.string.msg_sync_terminated));
                            }
                        }
                    } else if (operation == Operation.GetStoreOpen.getValue()) {
                        Constants.isSync = false;
                        if (salesOrderView != null) {
                            salesOrderView.hideProgressDialog();
                            salesOrderView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                        }
                    }


                }
            } else {
                penROReqCount++;
                if ((operation == Operation.Create.getValue()) && (penROReqCount == pendingROVal)) {
                    e.printStackTrace();
                    try {
                        concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(pendingCollectionList);
                        new RefreshAsyncTask(context, concatCollectionStr, this).execute();
//                        OfflineManager.refreshRequests(context, concatCollectionStr, this);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

                if (operation == Operation.OfflineFlush.getValue()) {
                    try {
                        new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else if (operation == Operation.OfflineRefresh.getValue()) {
                   /* e.printStackTrace();
                    try {
                        String syncTime = UtilConstants.getSyncHistoryddmmyyyyTime();
                        String[] DEFINGREQARRAY = Constants.getDefinigReq(context);
                        for (int incReq = 0; incReq < DEFINGREQARRAY.length; incReq++) {

                            String colName = DEFINGREQARRAY[incReq];
                            if (colName.contains("?$")) {
                                String splitCollName[] = colName.split("\\?");
                                colName = splitCollName[0];
                            }

                            Constants.events.updateStatus(Constants.SYNC_TABLE,
                                    colName, Constants.TimeStamp, syncTime
                            );
                        }
                    } catch (Exception exce) {
                        e.printStackTrace();
                        ConstantsUtils.printErrorLog(e.getMessage());
                    }*/
                    if (salesOrderView != null) {
                        salesOrderView.hideProgressDialog();
                        salesOrderView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                    }
                } else if (operation == Operation.GetStoreOpen.getValue()) {
                    Constants.isSync = false;
                    if (salesOrderView != null) {
                        salesOrderView.hideProgressDialog();
                        salesOrderView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                    }
                }
            }
        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                dialogCancelled = false;
                if (salesOrderView != null) {
                    salesOrderView.showProgressDialog();
                }
                new RefreshAsyncTask(context, concatCollectionStr, this).execute();
            } else {
                Constants.isSync = false;
                if (salesOrderView != null) {
                    salesOrderView.hideProgressDialog();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        } else {
            Constants.isSync = false;
            if (salesOrderView != null) {
                salesOrderView.hideProgressDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }
    }

    public void onSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        Constants.isPullDownSync=false;
        if (!dialogCancelled && !Constants.isStoreClosed) {
            if (operation == Operation.OfflineRefresh.getValue() && isFromWhere == 2) {
                Constants.isSync = false;
                if (salesOrderView != null) {
                    Constants.updateLastSyncTimeToTable(pendingCollectionList,context,Constants.SOPostPD_sync,refguid.toString().toUpperCase());
                    ConstantsUtils.startAutoSync(context,false);
//                    ConstantsUtils.serviceReSchedule(context, true);
                    salesOrderView.hideProgressDialog();
                    salesOrderView.showMessage(context.getString(R.string.msg_sync_successfully_completed));
                }
            }
            if (isFromWhere == 4) {
                if (operation == Operation.Create.getValue() && pendingROVal > 0) {
                    Constants.updateLastSyncTimeToTable(pendingCollectionList,context,Constants.SOPostPD_sync,refguid.toString().toUpperCase());

                    Set<String> set = new HashSet<>();
                    SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
                    set = sharedPreferences.getStringSet(Constants.SalesOrderDataValt, null);

                    HashSet<String> setTemp = new HashSet<>();
                    if (set != null && !set.isEmpty()) {
                        Iterator itr = set.iterator();
                        while (itr.hasNext()) {
                            setTemp.add(itr.next().toString());
                        }
                    }

                    try {
                        if(tempRODevList!=null && tempRODevList.length>0) {
                            setTemp.remove(tempRODevList[penROReqCount]);

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putStringSet(Constants.SalesOrderDataValt, setTemp);
                            editor.commit();

                            try {
                                ConstantsUtils.storeInDataVault(tempRODevList[penROReqCount], "",context);
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    penROReqCount++;


                }
                if ((operation == Operation.Create.getValue()) && (penROReqCount == pendingROVal)) {

                    try {
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
                        new RefreshAsyncTask(context, concatCollectionStr, this).execute();
//                        OfflineManager.refreshRequests(context, concatCollectionStr, this);
                    } catch (Exception e) {
                        e.printStackTrace();
                        TraceLog.e(Constants.SyncOnRequestSuccess, e);
                    }


                } else if (operation == Operation.OfflineFlush.getValue()) {

                    try {
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
                        new RefreshAsyncTask(context, concatCollectionStr, this).execute();
//                        OfflineManager.refreshRequests(context, concatCollectionStr, this);

                    } catch (Exception e) {
                        TraceLog.e(Constants.SyncOnRequestSuccess, e);
                    }
                } else if (operation == Operation.OfflineRefresh.getValue()) {
                    if (salesOrderView != null) {
                     //   Constants.updateLastSyncTimeToTable(pendingCollectionList,context,Constants.UpLoad,refguid.toString().toUpperCase());
                        ConstantsUtils.startAutoSync(context,false);
//                        ConstantsUtils.serviceReSchedule(context, true);
                        salesOrderView.hideProgressDialog();
                        String msg = "";
                        if (salesOrderView != null) {
                            if (!isErrorFromBackend) {
                                SalesOrderHeaderListActivity.mBoolRefreshDone = true;
                                salesOrderView.onReloadData();
                                salesOrderView.showMessage(context.getString(R.string.msg_sync_successfully_completed));
                            } else {
                                salesOrderView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, context, BuildConfig.APPLICATION_ID, false);
                            }
                        }

                    } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
                        Constants.isSync = false;
                        try {
                            OfflineManager.getAuthorizations(context);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                        Constants.setSyncTime(context,refguid.toString().toUpperCase());
                        ConstantsUtils.startAutoSync(context,false);
                        if (salesOrderView != null) {
                            salesOrderView.onReloadData();
                            salesOrderView.showMessage(context.getString(R.string.msg_sync_successfully_completed));
                            AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, context, BuildConfig.APPLICATION_ID, false);
                        }
                    }
                }
            }

        }
    }
}
