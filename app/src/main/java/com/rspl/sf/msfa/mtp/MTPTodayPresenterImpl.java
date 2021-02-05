package com.rspl.sf.msfa.mtp;

import android.app.Activity;
import android.content.Context;
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
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.so.SOUtils;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10769 on 19-02-2018.
 */

public class MTPTodayPresenterImpl implements MTPTodayPresenter, UIListener {
    private Context mContext;
    private MTPTodayView views = null;
    private List<String> alAssignColl = new ArrayList<>();
    private String spGUID = "";
    private boolean isAsmLogin = false;
    private Activity activity;
    private GUID refguid =null;

    public MTPTodayPresenterImpl(Activity activity,Context mContext, MTPTodayView views, String spGUID, boolean isAsmLogin) {
        this.mContext = mContext;
        this.views = views;
        this.spGUID = spGUID;
        this.isAsmLogin = isAsmLogin;
        this.activity = activity;
    }

    @Override
    public void onStart() {
        if (views != null) {
            views.onProgress();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<MTPRoutePlanBean> displayList = new ArrayList<>();
                try {
                    String startDate = SOUtils.getStartDate(mContext, mContext.getString(R.string.so_filter_today));
                    String endDate = SOUtils.getEndDate(mContext, mContext.getString(R.string.so_filter_today));
//            String qry = Constants.RoutePlans+"?$select=CustomerNo,CustomerName,VisitDate &$filter=VisitDate ge datetime'" + startDate + "' and VisitDate le datetime'" + endDate + "'";
                    String qry = Constants.RouteSchedules + "?$filter=ValidTo ge datetime'" + startDate + "' and ValidFrom le datetime'" + endDate + "' and SalesPersonID eq guid'" + spGUID + "' and ApprovalStatus eq '03'";
                    displayList = OfflineManager.getMTPTodayPlane(qry, isAsmLogin);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                final ArrayList<MTPRoutePlanBean> finalDisplayList = displayList;
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (views != null) {
                            views.onHideProgress();
                            views.displayData(finalDisplayList);
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
                Constants.updateStartSyncTime(mContext,Constants.MTP_sync,Constants.StartSync,refguid.toString().toUpperCase());
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
                }else if (i == Operation.GetStoreOpen.getValue()){
                    Constants.isSync = false;
                    if (views != null) {
                        views.onHideProgress();
                        views.showMsg(mContext.getString(R.string.msg_error_occured_during_sync));
                    }
                }
        }  else if (errorBean.isStoreFailed()) {
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
        }else {
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
                Constants.updateSyncTime(alAssignColl,mContext,Constants.MTP_sync,refguid.toString().toUpperCase());
            } catch (Exception exce) {
                LogManager.writeLogError(Constants.SyncTableHistory + exce.getMessage());
            }
            ConstantsUtils.startAutoSync(mContext,false);
            Constants.isSync = false;
            if (views != null) {
                views.onHideProgress();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false);
            }
            onStart();
        } else if (i == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
            Constants.isSync = false;
            try {
                OfflineManager.getAuthorizations(mContext);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setSyncTime(mContext,refguid.toString().toUpperCase());
            ConstantsUtils.startAutoSync(mContext,false);
            if (views != null) {
                views.onHideProgress();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false);
            }
            onStart();
        }
    }
}
