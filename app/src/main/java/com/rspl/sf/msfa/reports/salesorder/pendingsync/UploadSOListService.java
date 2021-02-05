package com.rspl.sf.msfa.reports.salesorder.pendingsync;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.TraceLog;
import com.rspl.sf.msfa.asyncTask.RefreshAsyncTask;
import com.rspl.sf.msfa.asyncTask.SyncFromDataValtAsyncTask;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.DialogCallBack;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.mbo.SalesOrderBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class UploadSOListService extends Service implements UIListener {
    private ArrayList<String> pendingCollectionList = new ArrayList<>();
    private ArrayList<SalesOrderBean> salesOrderHeaderArrayList = new ArrayList<>();
    private boolean isErrorFromBackend = false;
    private int penROReqCount = 0;
    private int pendingROVal = 0;
    private String[] tempRODevList = null;
    private static String TAG = UploadSOListService.class.getSimpleName();

    public UploadSOListService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: service started");
        new Thread(new Runnable() {
            @Override
            public void run() {
                onSyncSOrder(getApplicationContext());
            }
        }).start();

    }

    private void onSyncSOrder(Context mContext) {
        isErrorFromBackend = false;
        try {
            salesOrderHeaderArrayList.clear();
            salesOrderHeaderArrayList = (ArrayList<SalesOrderBean>) OfflineManager.getSoListFromDataValt(mContext, "", true);
            if (!salesOrderHeaderArrayList.isEmpty()) {

                if (UtilConstants.isNetworkAvailable(mContext)) {
                    pendingCollectionList.clear();
                    pendingCollectionList.add(Constants.SOs);
                    pendingCollectionList.add(Constants.SOItemDetails);
                    pendingCollectionList.add(Constants.SOTexts);
                    pendingCollectionList.add(Constants.SOItems);
                    pendingCollectionList.add(Constants.SOConditions);
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
                        new SyncFromDataValtAsyncTask(mContext, tempRODevList, this, new DialogCallBack() {
                            @Override
                            public void clickedStatus(boolean clickedStatus) {

                            }
                        }).execute();
                    } else {
                        stopService();
                    }
                } else {
                    stopService();
                }
            } else {
                stopService();
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog(e.getMessage());
            stopService();
        }
    }

    @Override
    public void onRequestError(int i, Exception e) {
        onError(i, e, getApplicationContext());
    }

    public void onError(int operation, Exception e, Context mContext) {
        e.printStackTrace();
        ErrorBean errorBean = Constants.getErrorCode(operation, e, mContext);
        String concatCollectionStr = "";
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
            penROReqCount++;
            if ((operation == Operation.Create.getValue()) && (penROReqCount == pendingROVal)) {
                e.printStackTrace();
                ConstantsUtils.printErrorLog(e.getMessage());
                try {
                    concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(pendingCollectionList);
                    new RefreshAsyncTask(mContext, concatCollectionStr, this).execute();
//                        OfflineManager.refreshRequests(context, concatCollectionStr, this);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            if (operation == Operation.OfflineFlush.getValue()) {
                try {
                    new RefreshAsyncTask(mContext, concatCollectionStr, this).execute();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else if (operation == Operation.OfflineRefresh.getValue()) {
                e.printStackTrace();
                ConstantsUtils.printErrorLog(e.getMessage());
                try {
                    /*String syncTime = UtilConstants.getSyncHistoryddmmyyyyTime();
                    String[] DEFINGREQARRAY = Constants.getDefinigReq(mContext);

                    for (int incReq = 0; incReq < DEFINGREQARRAY.length; incReq++) {

                        String colName = DEFINGREQARRAY[incReq];
                        if (colName.contains("?$")) {
                            String splitCollName[] = colName.split("\\?");
                            colName = splitCollName[0];
                        }

                        Constants.events.updateStatus(Constants.SYNC_TABLE,
                                colName, Constants.TimeStamp, syncTime
                        );
                    }*/
                    List<String > DEFINGREQARRAY = Arrays.asList(Constants.getDefinigReq(getApplicationContext()));
                 //   Constants.updateSyncTime(DEFINGREQARRAY,this,Constants.DownLoad);
                } catch (Exception exce) {
                    e.printStackTrace();
                    ConstantsUtils.printErrorLog(e.getMessage());
                }
                stopService();
            } else if (operation == Operation.GetStoreOpen.getValue()) {
                Constants.isSync = false;
                stopService();
            }
        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(mContext)) {
                Constants.isSync = true;
                new RefreshAsyncTask(mContext, concatCollectionStr, this).execute();
            } else {
                Constants.isSync = false;
                stopService();
            }
        } else {
            Constants.isSync = false;
            stopService();
        }
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        onSuccess(i, s, getApplicationContext());
    }

    public void onSuccess(int operation, String s, Context mContext) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.Create.getValue() && pendingROVal > 0) {

            Set<String> set = new HashSet<>();
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
            set = sharedPreferences.getStringSet(Constants.SalesOrderDataValt, null);

            HashSet<String> setTemp = new HashSet<>();
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
                while (itr.hasNext()) {
                    setTemp.add(itr.next().toString());
                }
            }

            setTemp.remove(tempRODevList[penROReqCount]);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(Constants.SalesOrderDataValt, setTemp);
            editor.commit();

            try {
                ConstantsUtils.storeInDataVault(tempRODevList[penROReqCount], "",mContext);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            penROReqCount++;


        }
        String concatCollectionStr = "";
        if ((operation == Operation.Create.getValue()) && (penROReqCount == pendingROVal)) {

            try {
                concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(pendingCollectionList);
                new RefreshAsyncTask(mContext, concatCollectionStr, this).execute();
//                        OfflineManager.refreshRequests(context, concatCollectionStr, this);
            } catch (Exception e) {
                e.printStackTrace();
                TraceLog.e(Constants.SyncOnRequestSuccess, e);
            }


        } else if (operation == Operation.OfflineFlush.getValue()) {

            try {
                concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(pendingCollectionList);
                new RefreshAsyncTask(mContext, concatCollectionStr, this).execute();
//                        OfflineManager.refreshRequests(context, concatCollectionStr, this);

            } catch (Exception e) {
                TraceLog.e(Constants.SyncOnRequestSuccess, e);
            }
        } else if (operation == Operation.OfflineRefresh.getValue()) {
            stopService();

        } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
            Constants.isSync = false;
            try {
                OfflineManager.getAuthorizations(mContext);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        //    Constants.setSyncTime(mContext);
            stopService();
        }


    }

    private void stopService() {
        stopSelf();
    }
}
