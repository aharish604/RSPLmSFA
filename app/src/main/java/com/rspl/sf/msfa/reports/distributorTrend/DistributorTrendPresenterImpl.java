package com.rspl.sf.msfa.reports.distributorTrend;

import android.app.Activity;

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
import com.rspl.sf.msfa.mbo.MyPerformanceBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;

/**
 * Created by e10769 on 23-Apr-18.
 */

public class DistributorTrendPresenterImpl implements DistributorTrendPresenter, UIListener {

    private Activity mContext;
    private DistributorTrendView rtView = null;
    private String cpGuid = "";
    private ArrayList<MyPerformanceBean> alRetTrends = new ArrayList<>();
    private ArrayList<String> alAssignColl = new ArrayList<>();
    private GUID refguid =null;

    public DistributorTrendPresenterImpl(Activity mContext, DistributorTrendView rtView, String cpGuid) {
        this.mContext = mContext;
        this.rtView = rtView;
        this.cpGuid = cpGuid;
    }

    @Override
    public void onStart() {
        if (rtView != null) {
            rtView.showProgress();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String mStrRetTrendQry = Constants.Performances + "?$filter=" + Constants.CPGUID + " eq '" + Constants.appendPrecedingZeros(cpGuid.toUpperCase(),10) + "' " + "and "+ Constants.PerformanceTypeID+" eq '000006' ";
                    alRetTrends = OfflineManager.getRetTrendsList(mStrRetTrendQry, Constants.appendPrecedingZeros(cpGuid.toUpperCase(),10));
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (rtView != null) {
                            rtView.hideProgress();
                            rtView.displayLstSyncTime(ConstantsUtils.getLastSeenDateFormat(mContext, ConstantsUtils.getMilliSeconds(
                                    ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.Performances, Constants.TimeStamp, mContext))));
                            rtView.displayList(alRetTrends);
                        }
                    }
                });
            }
        }).start();

    }

    @Override
    public void onDestroy() {
        rtView = null;
    }

    @Override
    public void onRefresh() {
        alAssignColl.clear();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(mContext)) {
            alAssignColl.clear();
            concatCollectionStr = "";
            alAssignColl.add(Constants.Performances);
            alAssignColl.add(Constants.TargetItems);
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            for (int incVal = 0; incVal < alAssignColl.size(); incVal++) {
                if (incVal == 0 && incVal == alAssignColl.size() - 1) {
                    concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
                } else if (incVal == 0) {
                    concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
                } else if (incVal == alAssignColl.size() - 1) {
                    concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
                } else {
                    concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
                }
            }

            if (Constants.iSAutoSync) {
                if (rtView != null) {
                    rtView.hideProgress();
                    rtView.displayMsg(mContext.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    // progressDialog = Constants.showProgressDialog(context, "", context.getString(R.string.msg_sync_progress_msg_plz_wait));
                    refguid = GUID.newRandom();
                    Constants.updateStartSyncTime(mContext,Constants.DownLoad,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(mContext, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (rtView != null) {
                rtView.hideProgress();
                rtView.displayMsg(mContext.getString(R.string.no_network_conn));
            }
        }
    }

    @Override
    public void onRequestError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e, mContext);
        if (errorBean.hasNoError()) {
            if (!Constants.isStoreClosed) {
                if (rtView != null) {
                    rtView.hideProgress();
                }
                if (operation == Operation.OfflineRefresh.getValue()) {
                    Constants.isSync = false;
                    if (!Constants.isStoreClosed) {
                        if (rtView != null) {
                            rtView.hideProgress();
                            rtView.displayMsg(mContext.getString(R.string.msg_error_occured_during_sync));
                        }
                    } else {
                        if (rtView != null) {
                            rtView.hideProgress();
                            rtView.displayMsg(mContext.getString(R.string.msg_sync_terminated));
                        }
                    }
                }else if (operation == Operation.GetStoreOpen.getValue()){
                    if (rtView != null) {
                        rtView.hideProgress();
                        rtView.displayMsg(mContext.getString(R.string.msg_error_occured_during_sync));
                    }
                }
            }

        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(mContext)) {
                Constants.isSync = true;
                if (rtView != null) {
                    rtView.hideProgress();
                }
                new RefreshAsyncTask(mContext, "", this).execute();
            } else {
                Constants.isSync = false;
                if (rtView != null) {
                    rtView.hideProgress();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), mContext);
                }
            }
        } else {
            Constants.isSync = false;
            if (rtView != null) {
                rtView.hideProgress();
                Constants.displayMsgReqError(errorBean.getErrorCode(), mContext);
            }
        }
    }


    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.OfflineRefresh.getValue()) {
            Constants.updateLastSyncTimeToTable(alAssignColl,mContext,Constants.DownLoad,refguid.toString().toUpperCase());
//            ConstantsUtils.serviceReSchedule(mContext, true);
            Constants.isSync = false;
            Constants.setSyncTime(mContext,refguid.toString().toUpperCase());
            if (rtView != null) {
                rtView.hideProgress();
                onStart();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, mContext, BuildConfig.APPLICATION_ID, false);
            }

        } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
            Constants.isSync = false;
            try {
                OfflineManager.getAuthorizations(mContext);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setSyncTime(mContext,refguid.toString().toUpperCase());
//            ConstantsUtils.serviceReSchedule(mContext, true);
            if (rtView != null) {
                rtView.hideProgress();
                onStart();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, mContext, BuildConfig.APPLICATION_ID, false);
            }
        }
    }
}
