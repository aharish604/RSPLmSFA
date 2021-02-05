package com.rspl.sf.msfa.autosync;

/**
 * Created by E10953 on 05-08-2019.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.asyncTask.RefreshAsyncTask;
import com.rspl.sf.msfa.asyncTask.RefreshGeoAsyncTask;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.MSFAApplication;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.sync.SyncSelectionActivity;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by E10953 on 02-08-2019.
 */

public class AutoSyncLocationDataService extends JobIntentService implements UIListener {
    public static final int JOB_ID = 300;
    long timestamp;
    private Handler mHandlerDifferentTrd = new Handler();
    public static String TAG = "AutoSyncLocationDataService";
    public static Context sContext;
    private int penReqCount = 0;
    private int mIntPendingCollVal = 0;
    private String[][] invKeyValues = null;
    private ArrayList<String> alAssignColl = new ArrayList<>();
    private ArrayList<String> alFlushColl = new ArrayList<>();
    private int mError = 0;
    ReentrantLock reentrantLock;
    private GUID refguid =null;



    public static void enqueueWork(Context context, Intent work) {
        sContext = context;
        enqueueWork(context, AutoSyncLocationDataService.class, JOB_ID, work);
    }

    // This describes what will happen when service is triggered
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d("DEBUG", "AutoSyncLocationDataService triggered");
        sContext = AutoSyncLocationDataService.this;
        timestamp = System.currentTimeMillis();
        // Extract additional values from the bundle
        String val = intent.getStringExtra("foo");




        mHandlerDifferentTrd.post(new Runnable() {
            public void run() {
                try {
                    try {
                        if (!ConstantsUtils.isMyServiceRunning(AutoSynDataService.class, AutoSyncLocationDataService.this))
                            Constants.iSAutoSync = false;
                    }catch (ExceptionInInitializerError e){
                        e.printStackTrace();
                    }catch (NoClassDefFoundError e){
                        e.printStackTrace();
                    }
                    Log.d(TAG, "auto sync location run: started");
                  //  LogManager.writeLogInfo(sContext.getString(R.string.auto_sync_location_trigger));
                    Constants.mErrorCount = 0;
//                    if (!Constants.isDayStartSyncEnbled)
//                        LogManager.writeLogInfo(mContext.getString(R.string.auto_sync_trigger));
                    if (!Constants.isSync && !Constants.iSAutoSync) {
                        Constants.isSync = false;
                        Constants.isLocationSync = true;
                        Constants.iSAutoSync = true;
                        if (UtilConstants.isNetworkAvailable(sContext)) {
                            if (!Constants.isDayStartSyncEnbled)
                                LogManager.writeLogInfo(sContext.getString(R.string.auto_sync_location_started));
                            Constants.mApplication = (MSFAApplication) sContext.getApplicationContext();

                            onUpdateSync(sContext, AutoSyncLocationDataService.this);
                        } else {
                            LogManager.writeLogInfo(sContext.getString(R.string.auto_sync_not_perfrom_due_to_no_network));
                            Constants.iSAutoSync = false;
                            Constants.isLocationSync = false;
                            Constants.mErrorCount++;
                            // setCallBackToUI(true, sContext.getString(R.string.no_network_conn),null);
                        }
                    } else {
                        Log.d(TAG, "run: stoped started");
                        if (Constants.iSAutoSync) {
                            LogManager.writeLogInfo(sContext.getString(R.string.location_sync_auto_sync_not_perfrom));
                        } else {
                            if (!Constants.isDayStartSyncEnbled)
                                LogManager.writeLogInfo(sContext.getString(R.string.sync_prog_auto_sync_not_perfrom_location));
                        }

                        Constants.mErrorCount++;
                        //    setCallBackToUI(true, mContext.getString(R.string.alert_auto_sync_is_progress),null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogManager.writeLogInfo("Autto Sync location error" + e.getMessage());
                    Constants.mErrorCount++;
                    // setCallBackToUI(true, e.getMessage(),null);
                }
                catch (ExceptionInInitializerError e){
                    e.printStackTrace();
                }catch (NoClassDefFoundError e){
                    e.printStackTrace();
                }
            }
        });


        // Extract the receiver passed into the service
        // ResultReceiver rec = intent.getParcelableExtra("receiver");
        // Send result to activity
        //  sendResultValue(rec, val);
        // Let's also create notification
        //  createNotification(val);
    }

    private void onUpdateSync(final Context mContext, final UIListener uiListener) {
        try {
            penReqCount = 0;
            mIntPendingCollVal = 0;
          /*  ArrayList<Object> objectArrayLists = SyncSelectionActivity.getPendingInvList(mContext);
            if (!objectArrayLists.isEmpty()) {
                mIntPendingCollVal = (int) objectArrayLists.get(0);
                invKeyValues = (String[][]) objectArrayLists.get(1);
            }

            if (mIntPendingCollVal > 0) {

            } else {*/
//                mIntPendingCollVal = 0;
                /*invKeyValues = null;
                ArrayList<Object> objectArrayList = SyncSelectionActivity.getPendingCollList(mContext,true);
                if (!objectArrayList.isEmpty()) {
                    mIntPendingCollVal = (int) objectArrayList.get(0);
                    invKeyValues = (String[][]) objectArrayList.get(1);
//                    cancelSOCount=(int[]) objectArrayList.get(2);
                }*/

//            }
            penReqCount = 0;

           /* if (!OfflineManager.isOfflineStoreOpen()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OfflineManager.openOfflineStore(mContext, new UIListener() {
                                @Override
                                public void onRequestError(int operation, Exception exception) {
                                    ErrorBean errorBean = Constants.getErrorCode(operation, exception, mContext);

                                    Constants.iSAutoSync = false;
                                    Constants.mErrorCount++;
                                    setCallBackToUI(true, Constants.makeMsgReqError(errorBean.getErrorCode(), mContext, false), null);
                                }


                                @Override
                                public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                                    if (OfflineManager.isOfflineStoreOpen()) {
                                        try {
                                            OfflineManager.getAuthorizations(mContext);
                                        } catch (OfflineODataStoreException e) {
                                            e.printStackTrace();
                                        }
                                        Constants.mErrorCount = 0;
                                        Constants.iSAutoSync = false;
                                        setCallBackToUI(true, "", null);
                                    }
                                }
                            });
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                            Constants.iSAutoSync = false;
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                        }
                    }
                }).start();

            } else {*/
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (OfflineManager.isOfflineStoreOpenGeo()) {
                        if (reentrantLock == null) {
                            reentrantLock = new ReentrantLock();
                        }
                        try {
                            Log.e("TrackService REENTRANT:", "LOCKED");
                            reentrantLock.lock();
                            Constants.getDataFromSqliteDB(getApplicationContext(),null);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("TrackService EXCEPTION", "ANR EXCEPTION OCCURRED");
                        } finally {
                            if (reentrantLock != null && reentrantLock.isHeldByCurrentThread()) {
                                reentrantLock.unlock();
                            }
                            Log.e("TrackService REENTRANT:", "UNLOCKED FINALLY");
                        }
                        postData(uiListener);
                    }else{
                        LogManager.writeLogInfo("Autto Sync location Service Offline Store not opened");

                    }
                }
            }).start();
//            }


        } catch (Exception e) {
            e.printStackTrace();
            Constants.iSAutoSync = false;
            Constants.isLocationSync = false;
            LogManager.writeLogInfo("Autto Sync location" + e.getMessage());
            //     setCallBackToUI(true, e.getMessage(), null);
        }catch (ExceptionInInitializerError e){
            e.printStackTrace();
        }
    }

    private void postData(UIListener uiListener) {
        try {
//            if (OfflineManager.offlineStore.getRequestQueueIsEmpty() && mIntPendingCollVal == 0) {
           /* if (mIntPendingCollVal == 0) {
                LogManager.writeLogInfo(mContext.getString(R.string.no_req_to_update_sap));
                Constants.iSAutoSync = false;
                setCallBackToUI(true, mContext.getString(R.string.no_req_to_update_sap), null);
                *//*if (UtilConstants.isNetworkAvailable(mContext)) {
                    alAssignColl.addAll(Constants.getDefinigReqList(mContext));
                    onAllSync(mContext);
                } else {
                    Constants.iSAutoSync = false;
                    Constants.mErrorCount++;
                    setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync), null);
                }*//*
            } else {*/
            alAssignColl.clear();
            alFlushColl.clear();
            ArrayList<String> allAssignColl = SyncSelectionActivity.getRefreshListAuto(sContext);
            if (!allAssignColl.isEmpty()) {
                alAssignColl.addAll(allAssignColl);
                alFlushColl.addAll(allAssignColl);
            }
            if (!OfflineManager.offlineGeo.getRequestQueueIsEmpty()) {
                if (UtilConstants.isNetworkAvailable(sContext)) {
                    try {
                        if (OfflineManager.getVisitStatusForCustomerGeo(Constants.SPGeos + Constants.isLocalFilterQry)) {
                            refguid = GUID.newRandom();
                            Constants.updateStartSyncTime(AutoSyncLocationDataService.this,Constants.Auto_Sync,Constants.StartSync,refguid.toString().toUpperCase());
                            try {
                                OfflineManager.flushQueuedRequestsForGeo(new UIListener() {
                                    @Override
                                    public void onRequestError(int i, Exception e) {

                                    }

                                    @Override
                                    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
                                        if (operation == Operation.OfflineFlush.getValue()) {
                                            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                                                try {
//                        OfflineManager.refreshRequests(getApplicationContext(), concatCollectionStr, SyncSelectionActivity.this);
                                                    new RefreshGeoAsyncTask(getApplicationContext(), Constants.SPGeos, this).execute();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    TraceLog.e(Constants.SyncOnRequestSuccess, e);
                                                }
                                            } else {
                                                Constants.isSync = false;

                                            }
                                        } else if (operation == Operation.OfflineRefresh.getValue()) {
                                            try {
                                                OfflineManager.getAuthorizations(sContext);
                                            } catch (OfflineODataStoreException e) {
                                                e.printStackTrace();
                                            }
                                            Constants.setBirthdayListToDataValut(sContext);
                                            Constants.updateLastSyncTimeToTable(alAssignColl, sContext, Constants.Auto_Sync,refguid.toString().toUpperCase());
                                            Constants.deleteDeviceMerchansisingFromDataVault(sContext);
                                            setUI();
                                        }
                                        //  refreshData();
                                    }
                                }, Constants.SPGeos);
                            } catch (OfflineODataStoreException e) {
                                e.printStackTrace();
                            }
                           // OfflineManager.flushQueuedRequestsForGeo(uiListener, Constants.SPGeos);
                        } else {

                            Constants.mErrorCount++;
                            //  setCallBackToUI(true, mContext.getString(R.string.no__loc_req_to_update_sap), null);
                            LogManager.writeLogInfo(sContext.getString(R.string.no__loc_req_to_update_sap));
                            LogManager.writeLogInfo(sContext.getString(R.string.auto_location_sync_end));
                            Constants.iSAutoSync = false;
                            Constants.isLocationSync = false;
                        }
//                        new FlushDataAsyncTask(this, alFlushColl).execute();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Constants.iSAutoSync = false;
                        Constants.isLocationSync = false;
                        Constants.mErrorCount++;
                        // setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync)+e.getMessage(), null);
                        LogManager.writeLogInfo(sContext.getString(R.string.data_conn_lost_during_sync) + e.getMessage());
                    }
                } else {
                    Constants.iSAutoSync = false;
                    Constants.isLocationSync = false;
                    Constants.mErrorCount++;
                    // setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync), null);
                    LogManager.writeLogInfo(sContext.getString(R.string.data_conn_lost_during_sync));
                }
            } else {
                Constants.iSAutoSync = false;
                Constants.isLocationSync = false;
                Constants.mErrorCount++;
                // setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync), null);
                LogManager.writeLogInfo(sContext.getString(R.string.data_conn_lost_during_sync));
            }
                /*if (mIntPendingCollVal > 0) {
                    if (UtilConstants.isNetworkAvailable(mContext)) {
                        onlineStoreOpen = false;
                        Constants.mBoolIsReqResAval = true;
                        Constants.mBoolIsNetWorkNotAval = false;
                        Constants.onlineStore = null;

                        tokenFlag = false;
                        Constants.x_csrf_token = "";
                        Constants.ErrorCode = 0;
                        Constants.ErrorNo = 0;
                        Constants.ErrorName = "";
                        Constants.ErrorNo_Get_Token = 0;
                        Constants.IsOnlineStoreFailed = false;
                        OnlineStoreListener.instance = null;
                        try {
                            onlineStoreOpen = OnlineManager.openOnlineStore(mContext);
                        } catch (OnlineODataStoreException e) {
                            e.printStackTrace();
                        }
                        if (onlineStoreOpen) {
                            *//*SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
                            if (sharedPreferences.getString(Constants.isInvoiceCreateKey, "").equalsIgnoreCase(Constants.isInvoiceTcode)) {
                                onLoadToken(mContext);
                                if (tokenFlag) {
                                    if (Constants.x_csrf_token != null && !Constants.x_csrf_token.equalsIgnoreCase("")) {
                                        try {
                                            new PostDataFromDataValt(mContext, uiListener, invKeyValues, iNetListener).execute();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Constants.iSAutoSync = false;
                                        Constants.mErrorCount++;
                                        setCallBackToUI(true, Constants.makeMsgReqError(-2, mContext, true), null);
                                    }
                                } else {
                                    Constants.iSAutoSync = false;
                                    Constants.mErrorCount++;
                                    setCallBackToUI(true, Constants.makeMsgReqError(Constants.ErrorNo_Get_Token, mContext, true), null);
                                }
                            } else {*//*
                                try {
                                    new PostDataFromDataValt(mContext, uiListener, invKeyValues, iNetListener).execute();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
//                            }
                        } else {
                            Constants.iSAutoSync = false;
                            Constants.mErrorCount++;
                            setCallBackToUI(true, Constants.makeMsgReqError(Constants.ErrorNo, mContext, false), null);
                        }


                    } else {
                        Constants.iSAutoSync = false;
                        Constants.mErrorCount++;
                        setCallBackToUI(true, mContext.getString(R.string.no_network_conn), null);
                        LogManager.writeLogInfo(mContext.getString(R.string.no_network_conn));
                    }
                }*/ /*else if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                    if (UtilConstants.isNetworkAvailable(mContext)) {
                        try {
                            new FlushDataAsyncTask(this, alFlushColl).execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Constants.iSAutoSync = false;
                        Constants.mErrorCount++;
                        setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync), null);
                        LogManager.writeLogInfo(mContext.getString(R.string.data_conn_lost_during_sync));
                    }
                } else {
                    if (!UtilConstants.isNetworkAvailable(mContext)) {
                        Constants.iSAutoSync = false;
                        Constants.mErrorCount++;
                        setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync), null);
                        LogManager.writeLogInfo(mContext.getString(R.string.data_conn_lost_during_sync));
                    } else {
                        onAllSync(mContext);
                    }
                }*/
        } catch (Exception e) {
            e.printStackTrace();
            Constants.mErrorCount++;
            //  setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync), null);
        }catch (ExceptionInInitializerError e){
            e.printStackTrace();
        }
    }


    // Send result to activity using ResultReceiver
    private void sendResultValue(ResultReceiver rec, String val) {
        // To send a message to the Activity, create a pass a Bundle
        Bundle bundle = new Bundle();
        bundle.putString("resultValue", "My Result Value. You Passed in: " + val + " with timestamp: " + timestamp);
        // Here we call send passing a resultCode and the bundle of extras
        rec.send(Activity.RESULT_OK, bundle);
    }

    // Construct compatible notification
    private void createNotification(String val) {

       /* // Construct pending intent to serve as action for notification item
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("message", "Launched via notification with message: " + val + " and timestamp " + timestamp);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        // Create notification
        String longText = "Intent service has a new message with: " + val + " and a timestamp of: " + timestamp;
        Notification noti =
                new NotificationCompat.Builder(this, DemoApplication.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("New Result!")
                        .setContentText("Simple Intent service has a new message")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(longText))
                        .setContentIntent(pIntent)
                        .build();

        // Hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIF_ID, noti);*/
    }

    @Override
    public void onRequestError(int operation, Exception exception) {
        ErrorBean errorBean = Constants.getErrorCode(operation, exception, sContext);
        try {
            if (errorBean.hasNoError()) {
                mError++;
                penReqCount++;
                Constants.mBoolIsReqResAval = true;
                Constants.mErrorCount++;

                if ((operation == Operation.Create.getValue()) && (penReqCount == mIntPendingCollVal)) {
                    final String mErrorMsg = "";
                    Constants.iSAutoSync = false;
                    Constants.isLocationSync = false;
                    setErrorUI(mErrorMsg, errorBean);
                    //old code
                   /* try {
                        if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                            if (UtilConstants.isNetworkAvailable(mContext)) {
                                try {
                                    new FlushDataAsyncTask(this, alFlushColl).execute();
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                }
                            } else {
                                Constants.iSAutoSync = false;
                                Constants.mErrorCount++;
                                setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync),null);
                            }
                        } else {
                            if (UtilConstants.isNetworkAvailable(mContext)) {
                                alAssignColl.clear();
                                alAssignColl.addAll(Constants.getDefinigReqList(mContext));
                                onAllSync(mContext);
                            } else {
                                Constants.iSAutoSync = false;
                                Constants.mErrorCount++;
                                setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync),null);
                            }
                        }
                    } catch (ODataException e3) {
                        e3.printStackTrace();
                    }*/

                }

                if (operation == Operation.OfflineFlush.getValue()) {
//                    if (UtilConstants.isNetworkAvailable(mContext)) {
//                        alAssignColl.clear();
//                        alAssignColl.addAll(Constants.getDefinigReqList(mContext));
//                        onAllSync(mContext);
//                    } else {
                    Constants.iSAutoSync = false;
                    Constants.isLocationSync = false;
                    Constants.mErrorCount++;
                    //  setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync),null);
//                    }
                } else if (operation == Operation.OfflineRefresh.getValue()) {
                    Constants.iSAutoSync = false;
                    Constants.isLocationSync = false;
                    Constants.mErrorCount++;
                    final String mErrorMsg = "";
                    setErrorUI(mErrorMsg, errorBean);

                   /* try {
                        new SyncGeoAsyncTask(mContext, new MessageWithBooleanCallBack() {
                            @Override
                            public void clickedStatus(boolean clickedStatus, String errorMsg, ErrorBean errorBean) {
                                Log.d("clickedStatus Req", clickedStatus+"");

                            }
                        }, Constants.All).execute();
                    } catch (Exception e) {
                        setErrorUI(mErrorMsg,errorBean);
                        e.printStackTrace();
                    }*/


                }
            } else {
                Constants.mBoolIsNetWorkNotAval = true;
                Constants.mBoolIsReqResAval = true;
                if (Constants.iSAutoSync) {
                    Constants.iSAutoSync = false;
                }
                Constants.isLocationSync = false;
                Constants.mErrorCount++;

                if (errorBean.isStoreFailed()) {
                    OfflineManager.offlineStore = null;
                    OfflineManager.options = null;
                    openStore(errorBean);
                } else {
                    //  setCallBackToUI(true, Constants.makeMsgReqError(errorBean.getErrorCode(), mContext, false),errorBean);
                }

            }
        } catch (Exception e) {
            Constants.mBoolIsNetWorkNotAval = true;
            Constants.mBoolIsReqResAval = true;
            if (Constants.iSAutoSync) {
                Constants.iSAutoSync = false;
            }
            Constants.isLocationSync = false;
            Constants.mErrorCount++;
            //    setCallBackToUI(true, Constants.makeMsgReqError(errorBean.getErrorCode(), mContext, false),null);
        }catch (ExceptionInInitializerError e){
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        try {
            Log.d(TAG, "onRequestSuccess: ");
            if (operation == Operation.Create.getValue() && mIntPendingCollVal > 0) {
                Constants.mBoolIsReqResAval = true;
                if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.CollList)) {
                    Constants.removeDeviceDocNoFromSharedPref(sContext, Constants.CollList, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SalesOrderDataValt)) {
                    Constants.removeDeviceDocNoFromSharedPref(sContext, Constants.SalesOrderDataValt, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SOUpdate)) {
                    Constants.removeDeviceDocNoFromSharedPref(sContext, Constants.SOUpdate, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SOCancel)) {
                    Constants.removeDeviceDocNoFromSharedPref(sContext, Constants.SOCancel, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.Expenses)) {
                    Constants.removeDeviceDocNoFromSharedPref(sContext, Constants.Expenses, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.MTPDataValt)) {
                    Constants.removeDeviceDocNoFromSharedPref(sContext, Constants.MTPDataValt, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.RTGSDataValt)) {
                    Constants.removeDeviceDocNoFromSharedPref(sContext, Constants.RTGSDataValt, invKeyValues[penReqCount][0]);
                }

                ConstantsUtils.storeInDataVault(invKeyValues[penReqCount][0], "",sContext);
                penReqCount++;
            }
            if ((operation == Operation.Create.getValue()) && (penReqCount == mIntPendingCollVal)) {
                setUI();
                //old code
               /* try {
                    if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                        if (UtilConstants.isNetworkAvailable(mContext)) {
                            try {
                                new FlushDataAsyncTask(this, alFlushColl).execute();
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        } else {
                            Constants.iSAutoSync = false;
                            setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync),null);
                        }
                    } else {
                        if (UtilConstants.isNetworkAvailable(mContext)) {
                            alAssignColl.clear();
                            alAssignColl.addAll(Constants.getDefinigReqList(mContext));
                            onAllSync(mContext);
                        } else {
                            Constants.iSAutoSync = false;
                            setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync),null);
                        }
                    }

                } catch (ODataException e) {
                    e.printStackTrace();
                }*/

            } else if (operation == Operation.OfflineFlush.getValue()) {
                if (UtilConstants.isNetworkAvailable(sContext)) {
//                    alAssignColl.clear();
//                    alAssignColl.addAll(Constants.getDefinigReqList(mContext));
                    onAllSync(sContext);
                } else
                    LogManager.writeLogInfo(sContext.getString(R.string.auto_location_sync_end));
                Constants.iSAutoSync = false;
                Constants.isLocationSync = false;
                // setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync),null);
//                }
            } else if (operation == Operation.OfflineRefresh.getValue()) {

                try {
                    OfflineManager.getAuthorizations(sContext);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                Constants.setBirthdayListToDataValut(sContext);
                Constants.updateLastSyncTimeToTable(alAssignColl, sContext, Constants.Auto_Sync,refguid.toString().toUpperCase());
                Constants.deleteDeviceMerchansisingFromDataVault(sContext);
                setUI();
//                Constants.setAppointmentNotification(mContext);
//                if(alAssignColl.contains(Constants.RoutePlans) || alAssignColl.contains(Constants.ChannelPartners) || alAssignColl.contains(Constants.Visits)) {
//                    Constants.alTodayBeatRet.clear();
//                    Constants.TodayTargetRetailersCount = Constants.getVisitTargetForToday();
//                    Constants.TodayActualVisitRetailersCount = Constants.getVisitedRetailerCount(Constants.alTodayBeatRet);
//                }
//                if(alAssignColl.contains(Constants.SSSOs) || alAssignColl.contains(Constants.Targets)) {
//                    Constants.loadingTodayAchived(mContext,Constants.alTodayBeatRet);
//                }

               /* try {
                    new SyncGeoAsyncTask(mContext, new MessageWithBooleanCallBack() {
                        @Override
                        public void clickedStatus(boolean clickedStatus, String errorMsg, ErrorBean errorBean) {
                            Log.d("clickedStatus Req", clickedStatus+"");
                            setUI();
                        }
                    }, Constants.All).execute();
                } catch (Exception e) {
                    setUI();
                    e.printStackTrace();
                }*/
                /*Constants.iSAutoSync = false;

                String mErrorMsg = "";
                if (Constants.AL_ERROR_MSG.size() > 0) {
                    mErrorMsg = Constants.convertALBussinessMsgToString(Constants.AL_ERROR_MSG);
                }

                if (mErrorMsg.equalsIgnoreCase("")) {
                    setCallBackToUI(true, mContext.getString(R.string.error_occured_during_post),null);
                } else {
                    setCallBackToUI(true, mErrorMsg,null);
                }*/

            }
        }catch (ExceptionInInitializerError e){
            e.printStackTrace();
        }

    }

    private void setErrorUI(String mErrorMsg, ErrorBean errorBean) {
        if (Constants.AL_ERROR_MSG.size() > 0) {
            mErrorMsg = Constants.convertALBussinessMsgToString(Constants.AL_ERROR_MSG);
        }
        if (mErrorMsg.equalsIgnoreCase("")) {
            // setCallBackToUI(true, errorBean.getErrorMsg(),null);
        } else {
            // setCallBackToUI(true, mErrorMsg,null);
        }
    }

    private void openStore(final ErrorBean errorBean) {
        if (!OfflineManager.isOfflineStoreOpen()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OfflineManager.openOfflineStore(sContext, new UIListener() {
                            @Override
                            public void onRequestError(int operation, Exception exception) {
                                ErrorBean errorBean = Constants.getErrorCode(operation, exception, sContext);

                                Constants.iSAutoSync = false;
                                Constants.isLocationSync = false;
                                Constants.mErrorCount++;
                                // setCallBackToUI(true, Constants.makeMsgReqError(errorBean.getErrorCode(), mContext, false), null);
                            }


                            @Override
                            public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                                if (OfflineManager.isOfflineStoreOpen()) {
                                    try {
                                        OfflineManager.getAuthorizations(sContext);
                                    } catch (OfflineODataStoreException e) {
                                        e.printStackTrace();
                                    }
                                    Constants.mErrorCount = 0;
                                    // setCallBackToUI(true, "", null);
                                }
                            }
                        });
                    } catch (OfflineODataStoreException e) {
                        //  setCallBackToUI(true, Constants.makeMsgReqError(errorBean.getErrorCode(), mContext, false), errorBean);
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }catch (ExceptionInInitializerError e){
                        e.printStackTrace();
                    }
                }
            }).start();

        } else {
            // setCallBackToUI(true, Constants.makeMsgReqError(errorBean.getErrorCode(), mContext, false), errorBean);
        }
    }

    private void onAllSync(Context mContext) {
//        new AllSyncAsyncTask(mContext, this, new ArrayList<String>()).execute();
        String syncCollection = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
        if (!TextUtils.isEmpty(syncCollection)) {
            new RefreshAsyncTask(mContext, syncCollection, this).execute();
        } else {
            //  setCallBackToUI(true, "No offline data to post", null);
        }
    }

    private void setUI() {
        try{
        Constants.iSAutoSync = false;
        Constants.isLocationSync = false;

        String mErrorMsg = "";
        if (Constants.AL_ERROR_MSG.size() > 0) {
            mErrorMsg = Constants.convertALBussinessMsgToString(Constants.AL_ERROR_MSG);
        }

        if (mErrorMsg.equalsIgnoreCase("")) {
            //  setCallBackToUI(true, mContext.getString(R.string.error_occured_during_post),null);
        } else {
            //setCallBackToUI(true, mErrorMsg,null);
        }
    }catch (ExceptionInInitializerError e){
            e.printStackTrace();
        }
    }
}

