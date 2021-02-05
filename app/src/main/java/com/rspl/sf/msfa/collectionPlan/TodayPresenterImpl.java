package com.rspl.sf.msfa.collectionPlan;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by e10769 on 19-02-2018.
 */

public class TodayPresenterImpl implements TodayPresenter, UIListener {
    private Context mContext;
    private TodayView views = null;
    private List<String> alAssignColl = new ArrayList<>();
    private Activity activity;
    private String spGuid="";
    private GUID refguid =null;

    public TodayPresenterImpl(Context mContext, TodayView views,Activity activity, String spGuid) {
        this.mContext = mContext;
        this.views = views;
        this.activity = activity;
        this.spGuid=spGuid;
    }

    @Override
    public void onStart() {
        if (views != null) {
            views.onProgress();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String month="";
                String year="";
                try {
                    Date date = new Date();
                    DateFormat monthFormat = new SimpleDateFormat("MM");
                    month = monthFormat.format(date);
                    DateFormat yearFormat = new SimpleDateFormat("yyyy");
                    year = yearFormat.format(date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String qryCltPlan = Constants.CollectionPlan + "?$filter=" + Constants.Period + " eq '" + month + "' and "+Constants.Fiscalyear+ " eq '" + year + "' and SPGUID eq guid'" + spGuid + "'";
                String routeGuid="";
                try {
                    routeGuid  = OfflineManager.getCollectionPlanGuidID(qryCltPlan);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                ArrayList<WeekHeaderList> displayList = new ArrayList<>();
                try {
                    if(!TextUtils.isEmpty(routeGuid)) {
                        String startDate = SOUtils.getStartDate(mContext, mContext.getString(R.string.so_filter_today));
                        String qry = Constants.CollectionPlanItem + "?$filter=" + Constants.COllectionPlanDate + " eq datetime'" + startDate + "' and CollectionPlanGUID eq guid'" + routeGuid + "'";
                        displayList = OfflineManager.getTodayPlane(qry);
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                final ArrayList<WeekHeaderList> finalDisplayList = displayList;
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (views != null) {
                            views.onHideProgress();
                            views.displayData(finalDisplayList);
                            views.displayLastRefreshedTime(UtilConstants.getLastRefreshedTime(mContext, ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.CollectionPlan, Constants.TimeStamp, mContext)));
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
                alAssignColl.add(Constants.CollectionPlan);
                alAssignColl.add(Constants.CollectionPlanItem);
                alAssignColl.add(Constants.CollectionPlanItemDetails);
                alAssignColl.add(Constants.ConfigTypsetTypeValues);
                String syncColl = TextUtils.join(", ", alAssignColl);
                Constants.isSync = true;
                refguid = GUID.newRandom();
                Constants.updateStartSyncTime(mContext,Constants.RTGS_sync,Constants.StartSync,refguid.toString().toUpperCase());
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
                        Constants.updateSyncTime(alAssignColl,mContext,Constants.RTGS_sync,refguid.toString().toUpperCase());
                    } catch (Exception exce) {
                        LogManager.writeLogError(Constants.SyncTableHistory + exce.getMessage());
                    }
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
        }  else {
            Constants.isSync = false;
            if (views != null) {
                views.onHideProgress();
                Constants.displayMsgReqError(errorBean.getErrorCode(), mContext);
            }
        }
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
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
                    Constants.updateSyncTime(alAssignColl,mContext,Constants.RTGS_sync,refguid.toString().toUpperCase());
                } catch (Exception exce) {
                    LogManager.writeLogError(Constants.SyncTableHistory + exce.getMessage());
                }
                ConstantsUtils.startAutoSync(mContext,false);
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
                        views.showMsg(mContext.getString(R.string.msg_sync_terminated));
                    }
                }
            }else if (i == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
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
}
