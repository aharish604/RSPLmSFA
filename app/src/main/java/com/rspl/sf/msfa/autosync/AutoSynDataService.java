package com.rspl.sf.msfa.autosync;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.asyncTask.RefreshAsyncTask;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.MSFAApplication;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.store.OnlineManager;
import com.rspl.sf.msfa.store.OnlineODataStoreException;
import com.rspl.sf.msfa.sync.FlushDataAsyncTask;
import com.rspl.sf.msfa.sync.SyncSelectionActivity;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by E10953 on 02-08-2019.
 */

public class AutoSynDataService extends JobIntentService implements UIListener {
    public static final int JOB_ID = 100;
    long timestamp;
    private Handler mHandlerDifferentTrd = new Handler();
    public static String TAG = "AutoSynDataService";
    public static Context sContext ;
    private int penReqCount = 0;
    private int mIntPendingCollVal = 0;
    private String[][] invKeyValues = null;
    private ArrayList<String> alAssignColl = new ArrayList<>();
    private ArrayList<String> alFlushColl = new ArrayList<>();
    private int mError = 0;
    private boolean tokenFlag = false, onlineStoreOpen = false;
    private GUID refguid =null;


    public static void enqueueWork(Context context, Intent work) {
        sContext = context;
        enqueueWork(context, AutoSynDataService.class, JOB_ID, work);
    }

    // This describes what will happen when service is triggered
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d("DEBUG", "AutoSynDataService triggered");
        sContext = AutoSynDataService.this;

        timestamp =  System.currentTimeMillis();
        // Extract additional values from the bundle
        String val = intent.getStringExtra("foo");

        mHandlerDifferentTrd.post(new Runnable() {
            public void run() {
                try {
                    Log.d(TAG, "auto sync run: started");
                    Constants.mErrorCount = 0;
//                    if (!Constants.isDayStartSyncEnbled)
//                        LogManager.writeLogInfo(mContext.getString(R.string.auto_sync_trigger));
                    if (!Constants.isSync && !Constants.isLocationSync && !Constants.isBackGroundSync) {
                        Constants.isSync = false;
                        Constants.iSAutoSync = true;
                        if (UtilConstants.isNetworkAvailable(sContext)) {
                            if (!Constants.isDayStartSyncEnbled)
                                LogManager.writeLogInfo(sContext.getString(R.string.auto_sync_started));
                            Constants.mApplication = (MSFAApplication) sContext.getApplicationContext();
                            refguid = GUID.newRandom();
                            Constants.updateStartSyncTime(AutoSynDataService.this,Constants.Auto_Sync,Constants.StartSync,refguid.toString().toUpperCase());
                            onUpdateSync(sContext, AutoSynDataService.this);
                        } else {
                            LogManager.writeLogInfo(sContext.getString(R.string.auto_sync_not_perfrom_due_to_no_network));
                            Constants.iSAutoSync = false;
                            Constants.mErrorCount++;
                            setCallBackToUI(true, sContext.getString(R.string.no_network_conn),null);
                        }
                    } else {
                        Log.d(TAG, "run: stoped started");
                        if (Constants.isLocationSync) {
                            LogManager.writeLogInfo(sContext.getString(R.string.location_sync_prog_auto_sync_not_perfrom));
                        }else if (Constants.isBackGroundSync) {
                            LogManager.writeLogInfo(sContext.getString(R.string.alert_backgrounf_sync_is_progress));
                        }else {
                            if (!Constants.isDayStartSyncEnbled) {
                                LogManager.writeLogInfo(sContext.getString(R.string.sync_prog_auto_sync_not_perfrom));
                            }
                        }
                        Constants.mErrorCount++;
                        setCallBackToUI(true, sContext.getString(R.string.alert_auto_sync_is_progress),null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogManager.writeLogInfo("Autto Sync error" + e.getMessage());
                    Constants.mErrorCount++;
                    setCallBackToUI(true, e.getMessage(),null);
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
        try{
            Constants.deletePostedSOData(AutoSynDataService.this);
        }catch (Exception e){
            e.printStackTrace();
        }
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
            invKeyValues = null;
            ArrayList<Object> objectArrayList = getPendingCollList(mContext,true);
            if (!objectArrayList.isEmpty()) {
                mIntPendingCollVal = (int) objectArrayList.get(0);
                invKeyValues = (String[][]) objectArrayList.get(1);
//                    cancelSOCount=(int[]) objectArrayList.get(2);
            }

//            }
            penReqCount = 0;

            if (!OfflineManager.isOfflineStoreOpen()) {
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

            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        postData(uiListener);
                    }
                }).start();
            }


        } catch (Exception e) {
            e.printStackTrace();
            Constants.iSAutoSync = false;
            LogManager.writeLogInfo("Autto Sync " + e.getMessage());
            setCallBackToUI(true, e.getMessage(), null);
        }
    }
    public ArrayList<Object> getPendingCollList(Context mContext, boolean isAutoSync) {
        ArrayList<Object> objectsArrayList = new ArrayList<>();
        int mIntPendingCollVal = 0;
        String[][] invKeyValues = null;
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        invKeyValues = new String[SyncSelectionActivity.getPendingListSize(mContext)][2];

        if (!isAutoSync) {
            set = sharedPreferences.getStringSet(Constants.CollList, null);
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
                while (itr.hasNext()) {
                    invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                    invKeyValues[mIntPendingCollVal][1] = Constants.CollList;
                    mIntPendingCollVal++;
                }
            }
        }

        set = sharedPreferences.getStringSet(Constants.SalesOrderDataValt, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.SalesOrderDataValt;
                mIntPendingCollVal++;
            }
        }
        set = sharedPreferences.getStringSet(Constants.MTPDataValt, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.MTPDataValt;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.RTGSDataValt, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.RTGSDataValt;
                mIntPendingCollVal++;
            }
        }
        int cancelSOSize = 0;
        int i = 0;
        int[] cancelSOCount = new int[0];
        if (!isAutoSync) {
            set = sharedPreferences.getStringSet(Constants.SOCancel, null);


            cancelSOSize = (set != null && !set.isEmpty()) ? set.size() : 0;

            set = sharedPreferences.getStringSet(Constants.SOUpdate, null);
            int changeSOSize = 0;
            changeSOSize = (set != null && !set.isEmpty()) ? set.size() : 0;

            cancelSOCount = new int[cancelSOSize + changeSOSize];

            set = sharedPreferences.getStringSet(Constants.SOCancel, null);
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
//            cancelSOCount = new int[set.size()];
                while (itr.hasNext()) {
                    invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                    invKeyValues[mIntPendingCollVal][1] = Constants.SOCancel;

                    String store = null;
                    try {
                        store = ConstantsUtils.getFromDataVault(invKeyValues[mIntPendingCollVal][0].toString(),this);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }


                    //Fetch object from data vault
                    ArrayList<HashMap<String, String>> arrtable = null;
                    try {

                        JSONObject fetchJsonHeaderObject = new JSONObject(store);
                        String itemsString = fetchJsonHeaderObject.getString(Constants.SalesOrderItems);
                        arrtable = UtilConstants.convertToArrayListMap(itemsString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (arrtable != null)
                        cancelSOCount[i] = arrtable.size() + 1;
                    i++;
                    mIntPendingCollVal++;
                }
            }
        }
        if (!isAutoSync) {
            set = sharedPreferences.getStringSet(Constants.SOUpdate, null);
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
//            cancelSOCount = new int[set.size()];
                while (itr.hasNext()) {
                    invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                    invKeyValues[mIntPendingCollVal][1] = Constants.SOUpdate;

                    String store = null;
                    try {
                        store = ConstantsUtils.getFromDataVault(invKeyValues[mIntPendingCollVal][0].toString(),this);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }


                    ArrayList<HashMap<String, String>> arrtable = null;
                    try {

                        JSONObject fetchJsonHeaderObject = new JSONObject(store);
                        String itemsString = fetchJsonHeaderObject.getString(Constants.SalesOrderItems);
                        arrtable = UtilConstants.convertToArrayListMap(itemsString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (arrtable != null)
                        cancelSOCount[i] = arrtable.size() + 1;
                    i++;
                    mIntPendingCollVal++;
                }
            }
        }
        if (!isAutoSync) {
            set = sharedPreferences.getStringSet(Constants.Expenses, null);
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
                while (itr.hasNext()) {
                    invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                    invKeyValues[mIntPendingCollVal][1] = Constants.Expenses;
                    mIntPendingCollVal++;
                }
            }
        }

        if (mIntPendingCollVal > 0) {
            int count = 0;
            for (int j = 0; j < invKeyValues.length; j++) {
                if (invKeyValues[j][0] != null && invKeyValues[j][1] != null) {
                    count++;
                }
            }
            String removeNullValues[][] = new String[count][2];
            for (int k = 0; k < invKeyValues.length; k++) {
                if (invKeyValues[k][0] != null && invKeyValues[k][1] != null) {
                    removeNullValues[k] = invKeyValues[k];
                }
            }
            Arrays.sort(removeNullValues, new SyncSelectionActivity.ArrayComarator());
            objectsArrayList.add(mIntPendingCollVal);
            objectsArrayList.add(removeNullValues);
            objectsArrayList.add(cancelSOCount);
        }

        return objectsArrayList;

    }
    private void postData(UIListener uiListener) {
        try {
//            if (OfflineManager.offlineStore.getRequestQueueIsEmpty() && mIntPendingCollVal == 0) {
            if (mIntPendingCollVal == 0) {
                LogManager.writeLogInfo(sContext.getString(R.string.no_req_to_update_sap));
                setCallBackToUI(true, sContext.getString(R.string.no_req_to_update_sap), null);
                Constants.iSAutoSync = false;
                /*if (UtilConstants.isNetworkAvailable(mContext)) {
                    alAssignColl.addAll(Constants.getDefinigReqList(mContext));
                    onAllSync(mContext);
                } else {
                    Constants.iSAutoSync = false;
                    Constants.mErrorCount++;
                    setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync), null);
                }*/
                if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                    if (UtilConstants.isNetworkAvailable(sContext)) {
                        try {
                            ArrayList<String> allAssignColl = SyncSelectionActivity.getRefreshList(sContext);
                            if (!allAssignColl.isEmpty()) {
                                alAssignColl.addAll(allAssignColl);
                                alFlushColl.addAll(allAssignColl);
                            }
                            new FlushDataAsyncTask(this, alFlushColl).execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Constants.iSAutoSync = false;
                        Constants.mErrorCount++;
                        setCallBackToUI(true, sContext.getString(R.string.data_conn_lost_during_sync), null);
                        LogManager.writeLogInfo(sContext.getString(R.string.data_conn_lost_during_sync));
                    }
                } else {
                    if (!UtilConstants.isNetworkAvailable(sContext)) {
                        Constants.iSAutoSync = false;
                        Constants.mErrorCount++;
                        setCallBackToUI(true, sContext.getString(R.string.data_conn_lost_during_sync), null);
                        LogManager.writeLogInfo(sContext.getString(R.string.data_conn_lost_during_sync));
                    } else {
                        onAllSync(sContext);
                    }
                }
            } else {
                alAssignColl.clear();
                alFlushColl.clear();
                ArrayList<String> allAssignColl = SyncSelectionActivity.getRefreshList(sContext);
                if (!allAssignColl.isEmpty()) {
                    alAssignColl.addAll(allAssignColl);
                    alFlushColl.addAll(allAssignColl);
                }
                if (mIntPendingCollVal > 0) {
                    if (UtilConstants.isNetworkAvailable(sContext)) {
                        onlineStoreOpen = false;
                        Constants.mBoolIsReqResAval = true;
                        Constants.mBoolIsNetWorkNotAval = false;

                        tokenFlag = false;
                        Constants.x_csrf_token = "";
                        Constants.ErrorCode = 0;
                        Constants.ErrorNo = 0;
                        Constants.ErrorName = "";
                        Constants.ErrorNo_Get_Token = 0;
                        Constants.IsOnlineStoreFailed = false;
                        try {
                            onlineStoreOpen = OnlineManager.openOnlineStore(sContext, true);
                        } catch (OnlineODataStoreException e) {
                            e.printStackTrace();
                            Constants.iSAutoSync = false;
                        }
                        if (onlineStoreOpen) {
                            /*SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
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
                            } else {*/
                            try {
                                new SyncPostDataFromDataValt(sContext, uiListener, invKeyValues).execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//                            }
                        } else {
                            Constants.iSAutoSync = false;
                            Constants.mErrorCount++;
                            setCallBackToUI(true, Constants.makeMsgReqError(Constants.ErrorNo, sContext, false), null);
                        }


                    } else {
                        Constants.iSAutoSync = false;
                        Constants.mErrorCount++;
                        setCallBackToUI(true, sContext.getString(R.string.no_network_conn), null);
                        LogManager.writeLogInfo(sContext.getString(R.string.no_network_conn));
                    }
                } else{
                    Constants.iSAutoSync = false;
                }

                /*else if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
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
            }
        } catch (Exception e) {
            e.printStackTrace();
            Constants.mErrorCount++;
            setCallBackToUI(true, sContext.getString(R.string.data_conn_lost_during_sync), null);
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
                    setErrorUI(mErrorMsg,errorBean);
                    //old code
                    /*try {
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
//                                alAssignColl.clear();
//                                alAssignColl.addAll(Constants.getDefinigReqList(mContext));
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
                    if (UtilConstants.isNetworkAvailable(sContext)) {
//                        alAssignColl.clear();
//                        alAssignColl.addAll(Constants.getDefinigReqList(mContext));
                        onAllSync(sContext);
                    } else {
                        Constants.iSAutoSync = false;
                        Constants.mErrorCount++;
                        setCallBackToUI(true, sContext.getString(R.string.data_conn_lost_during_sync),null);
                    }
                } else if (operation == Operation.OfflineRefresh.getValue()) {
                    Constants.iSAutoSync = false;
                    Constants.mErrorCount++;
                    final String mErrorMsg = "";
                    setErrorUI(mErrorMsg,errorBean);

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
                Constants.mErrorCount++;

                if(errorBean.isStoreFailed()){
                    OfflineManager.offlineStore = null;
                    OfflineManager.options = null;
                    openStore(errorBean);
                }else{
                    setCallBackToUI(true, Constants.makeMsgReqError(errorBean.getErrorCode(), sContext, false),errorBean);
                }

            }
        } catch (Exception e) {
            Constants.mBoolIsNetWorkNotAval = true;
            Constants.mBoolIsReqResAval = true;
            if (Constants.iSAutoSync) {
                Constants.iSAutoSync = false;
            }
            Constants.mErrorCount++;
            setCallBackToUI(true, Constants.makeMsgReqError(errorBean.getErrorCode(), sContext, false),null);
        }
    }




    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        Log.d(TAG, "onRequestSuccess: ");
        if (operation == Operation.Create.getValue() && mIntPendingCollVal > 0) {
            Constants.mBoolIsReqResAval = true;
            try {
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
            } catch (Exception e){
                e.printStackTrace();
            }

            ConstantsUtils.storeInDataVault(invKeyValues[penReqCount][0], "",sContext);
            penReqCount++;
        }
        if ((operation == Operation.Create.getValue()) && (penReqCount == mIntPendingCollVal)) {
//                setUI();
            //old code
            try {
                if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                    if (UtilConstants.isNetworkAvailable(sContext)) {
                        try {
                            new FlushDataAsyncTask(this, alFlushColl).execute();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    } else {
                        Constants.iSAutoSync = false;
                        setCallBackToUI(true, sContext.getString(R.string.data_conn_lost_during_sync),null);
                    }
                } else {
                    if (UtilConstants.isNetworkAvailable(sContext)) {
//                            alAssignColl.clear();
//                            alAssignColl.addAll(Constants.getDefinigReqList(mContext));
                        onAllSync(sContext);
                    } else {
                        Constants.iSAutoSync = false;
                        setCallBackToUI(true, sContext.getString(R.string.data_conn_lost_during_sync),null);
                    }
                }

            } catch (ODataException e) {
                e.printStackTrace();
            }

        } else if (operation == Operation.OfflineFlush.getValue()) {
            if (UtilConstants.isNetworkAvailable(sContext)) {
//                    alAssignColl.clear();
//                    alAssignColl.addAll(Constants.getDefinigReqList(mContext));
                onAllSync(sContext);
            } else {
                Constants.iSAutoSync = false;
                setCallBackToUI(true, sContext.getString(R.string.data_conn_lost_during_sync),null);
            }
        } else if (operation == Operation.OfflineRefresh.getValue()) {

            try {
                OfflineManager.getAuthorizations(sContext);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setBirthdayListToDataValut(sContext);
            Constants.updateLastSyncTimeToTable(alAssignColl,sContext,Constants.Auto_Sync,refguid.toString().toUpperCase());
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
    }

    private void setErrorUI(String mErrorMsg,ErrorBean errorBean){
        if (Constants.AL_ERROR_MSG.size() > 0) {
            mErrorMsg = Constants.convertALBussinessMsgToString(Constants.AL_ERROR_MSG);
        }
        if (mErrorMsg.equalsIgnoreCase("")) {
            setCallBackToUI(true, errorBean.getErrorMsg(),null);
        } else {
            setCallBackToUI(true, mErrorMsg,null);
        }
    }
    private void openStore(final ErrorBean errorBean){
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
                                Constants.mErrorCount++;
                                setCallBackToUI(true, Constants.makeMsgReqError(errorBean.getErrorCode(), sContext, false), null);
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
                                    setCallBackToUI(true, "", null);
                                }
                            }
                        });
                    } catch (OfflineODataStoreException e) {
                        setCallBackToUI(true, Constants.makeMsgReqError(errorBean.getErrorCode(), sContext, false), errorBean);
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }
                }
            }).start();

        }else{
            setCallBackToUI(true, Constants.makeMsgReqError(errorBean.getErrorCode(), sContext, false), errorBean);
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
    private void setUI(){
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
    }

    private void setCallBackToUI(final boolean status, final String error_Msg, final ErrorBean errorBean) {
        LogManager.writeLogError("Call-Back: " +error_Msg);

        /*if (sContext!=null && sContext instanceof Activity) {
            ((Activity) sContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (dialogCallBack != null) {
                        dialogCallBack.clickedStatus(status, error_Msg, errorBean);
                    } else {
                        if (!Constants.isDayStartSyncEnbled)
                            LogManager.writeLogInfo(sContext.getString(R.string.auto_sync_end));
                    }
                }
            });
        }*/

    }

    public class SyncPostDataFromDataValt extends AsyncTask<Void, Void, Void> {
        private Context mContext;
        private UIListener uiListener;
        private String[][] invKeyValues;
        private Hashtable dbHeadTable;
        private ArrayList<HashMap<String, String>> arrtable;
        private String TAG = "UpdatePendingRequest";

        public SyncPostDataFromDataValt(Context mContext, UIListener uiListener, String[][] invKeyValues) {
            this.mContext = mContext;
            this.uiListener = uiListener;
            this.invKeyValues = invKeyValues;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);

                for (int k = 0; k < invKeyValues.length; k++) {

                    /*while (!Constants.mBoolIsReqResAval) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if(Constants.mBoolIsNetWorkNotAval){
                        break;
                    }

                    Constants.mBoolIsReqResAval= false;*/
                    String store = null;
                    try {
                        store = ConstantsUtils.getFromDataVault(invKeyValues[k][0].toString(),sContext);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    //Fetch object from data vault
                    try {

                        if(store!=null){
                        JSONObject fetchJsonHeaderObject = new JSONObject(store);
                        dbHeadTable = new Hashtable();
                        arrtable = new ArrayList<>();

                        if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.Collection)) {
                            dbHeadTable = Constants.getCollHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                            String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);

                            arrtable = UtilConstants.convertToArrayListMap(itemsString);

                            try {
                                OnlineManager.createCollectionEntry(dbHeadTable, arrtable, uiListener);

                            } catch (OnlineODataStoreException e) {
                                e.printStackTrace();
                            }

                        } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SecondarySOCreate)) {
                            Constants.REPEATABLE_REQUEST_ID="";
                            Constants.REPEATABLE_DATE="";
                            JSONObject dbHeadTable = Constants.getSOHeaderValuesFrmJsonObject(fetchJsonHeaderObject);
                            OnlineManager.createEntity( Constants.REPEATABLE_REQUEST_ID,Constants.REPEATABLE_DATE,dbHeadTable.toString(), Constants.SOs, uiListener, mContext);
                        }else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SalesOrderDataValt)) {
                            Constants.REPEATABLE_REQUEST_ID="";
                            Constants.REPEATABLE_DATE="";
                            if (!alAssignColl.contains(Constants.SOs)) {
                                alAssignColl.add(Constants.SOs);
                                alAssignColl.add(Constants.SOItemDetails);
                                alAssignColl.add(Constants.SOItems);
                                alAssignColl.add(Constants.SOTexts);
                                alAssignColl.add(Constants.SOConditions);
                            }
                            JSONObject dbHeadTable = Constants.getSOsHeaderValueFrmJsonObject1(fetchJsonHeaderObject);

                            OnlineManager.createEntity(Constants.REPEATABLE_REQUEST_ID,Constants.REPEATABLE_DATE,dbHeadTable.toString(), Constants.SOs, uiListener, mContext);
                        } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SOUpdate)) {
                            dbHeadTable = Constants.getSOCancelHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                            String itemsString = fetchJsonHeaderObject.getString(Constants.SalesOrderItems);
                            arrtable = UtilConstants.convertToArrayListMap(itemsString);
                            try {
                                OnlineManager.cancelSO(dbHeadTable, arrtable, uiListener);
                            } catch (OnlineODataStoreException e) {
                                e.printStackTrace();
                            }
                        } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.Expenses)) {
                            dbHeadTable = Constants.getExpenseHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                            String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                            arrtable = UtilConstants.convertToArrayListMap(itemsString);
                            try {
                                OnlineManager.createDailyExpense(dbHeadTable, arrtable, uiListener);
                            } catch (OnlineODataStoreException e) {
                                e.printStackTrace();
                            }
                        }/* else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.RouteSchedules)) {
                            isBatchReqs = false;
                            dbHeadTable = Constants.getMTPHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                            String itemsString = fetchJsonHeaderObject.getString(Constants.RouteSchedulePlans);
                            arrtable = UtilConstants.convertToArrayListMap(itemsString);
                            try {
                                OnlineManager.createMTP(dbHeadTable, arrtable, SyncSelectionActivity.this, SyncSelectionActivity.this);
                            } catch (OnlineODataStoreException e) {
                                e.printStackTrace();
                            }
                        } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.CollectionPlan)) {
                            isBatchReqs = false;
                            dbHeadTable = Constants.getRTGSHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                            String itemsString = fetchJsonHeaderObject.getString(Constants.CollectionPlanItem);
                            arrtable = UtilConstants.convertToArrayListMap(itemsString);
                            try {
                                OnlineManager.createRTGS(dbHeadTable, arrtable, SyncSelectionActivity.this, SyncSelectionActivity.this);
                            } catch (OnlineODataStoreException e) {
                                e.printStackTrace();
                            }
                        }*/
                    }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Constants.iSAutoSync = false;
                    }

                }
                Constants.iSAutoSync = false;


            } catch (InterruptedException e) {
                e.printStackTrace();
                Constants.iSAutoSync = false;
            }
            return null;
        }
    }
}
