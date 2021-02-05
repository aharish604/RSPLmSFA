package com.rspl.sf.msfa.sync;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.rspl.sf.msfa.BuildConfig;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.asyncTask.RefreshAsyncTask;
import com.rspl.sf.msfa.asyncTask.RefreshGeoAsyncTask;
import com.rspl.sf.msfa.autosync.AutoSynDataService;
import com.rspl.sf.msfa.autosync.AutoSyncDataAlarmReceiver;
import com.rspl.sf.msfa.autosync.AutoSyncDataLocationAlarmReceiver;
import com.rspl.sf.msfa.autosync.AutoSyncLocationDataService;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.SyncHistoryCallBack;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.networkmonitor.ITrafficSpeedListener;
import com.rspl.sf.msfa.networkmonitor.TrafficSpeedMeasurer;
import com.rspl.sf.msfa.networkmonitor.Utils;
import com.rspl.sf.msfa.notification.NotificationSetClass;
import com.rspl.sf.msfa.registration.Configuration;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.store.OnlineManager;
import com.rspl.sf.msfa.store.OnlineODataStoreException;
import com.rspl.sf.msfa.sync.SyncHistoryInfo.SyncHistoryInfoActivity;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataError;
import com.sap.smp.client.odata.ODataPayload;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;
import com.sap.smp.client.odata.store.ODataRequestParamSingle;
import com.sap.smp.client.odata.store.ODataResponseSingle;
import com.sap.smp.client.odata.store.impl.ODataRequestParamSingleDefaultImpl;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This class shows selection type of sync icons in grid manner.
 */

@SuppressLint("NewApi")
public class SyncSelectionActivity extends AppCompatActivity implements UIListener, OnlineODataInterface {
    GridView grid_main;
    String iconName[] = Constants.syncMenu;
    int OriginalStatus[] = {1, 1, 1, 1};
    int TempStatus[] = {1, 1, 1, 1};
    String[][] invKeyValues;
    int mIntPendingCollVal = 0;
    String syncHistoryType = "";
    ProgressDialog syncProgDialog;
    Hashtable dbHeadTable;
    ArrayList<HashMap<String, String>> arrtable;
    int mError = 0;
    ArrayList<String> alAssignColl = new ArrayList<>();
    ArrayList<String> alFlushColl = new ArrayList<>();
    String concatCollectionStr = "";
    String concatFlushCollStr = "";
    int[] cancelSOCount = new int[0];
    int updateCancelSOCount = 0;
    int cancelSoPos = 0;
    private boolean dialogCancelled = false;
    private int penReqCount = 0;
    private boolean mBoolIsNetWorkNotAval = false;
    private boolean mBoolIsReqResAval = false;
    private boolean isBatchReqs = false;
    private boolean isClickable = false;
    private boolean startServices = false;
    private boolean isAllSync = false;
    private SharedPreferences mSharedPrefs = null;
    private GUID refguid =null;
    private TrafficSpeedMeasurer mTrafficSpeedMeasurer;
    private static final boolean SHOW_SPEED_IN_BITS = false;
    private int networkErrorCount=0,networkError=0;
    Thread networkThread;
    private boolean isMonitoringStopped = false;
    private boolean isNetwrokErrAlert = false;
    private boolean isRefreshDB = false;




    public static int getPendingListSize(Context mContext) {
        int size = 0;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);

        Set<String> set = new HashSet<>();

        set = sharedPreferences.getStringSet(Constants.CollList, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }

//		set = sharedPreferences.getStringSet(Constants.SOList, null);
        set = sharedPreferences.getStringSet(Constants.SalesOrderDataValt, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }

        set = sharedPreferences.getStringSet(Constants.SOUpdate, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }

        set = sharedPreferences.getStringSet(Constants.SOCancel, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }

        set = sharedPreferences.getStringSet(Constants.Expenses, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.MTPDataValt, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.RTGSDataValt, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }

        return size;
    }

    public static ArrayList<String> getRefreshList(final Context context) {
        final ArrayList<String> alAssignColl = new ArrayList<>();
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (OfflineManager.getVisitStatusForCustomer(Constants.Attendances + Constants.isLocalFilterQry)) {
                        alAssignColl.add(Constants.Attendances);
                    }
                    if (OfflineManager.getVisitStatusForCustomer(Constants.Visits + Constants.isLocalFilterQry)) {
                        alAssignColl.add(Constants.Visits);
                    }

                    if (OfflineManager.getVisitStatusForCustomer(Constants.VisitActivities + Constants.isLocalFilterQry)) {
                        alAssignColl.add(Constants.VisitActivities);
                    }

                    if (OfflineManager.getVisitStatusForCustomer(Constants.MerchReviews + Constants.isLocalFilterQry)) {
                        alAssignColl.add(Constants.MerchReviews);
                        alAssignColl.add(Constants.MerchReviewImages);
                    }

                    if (OfflineManager.getVisitStatusForCustomer(Constants.CompetitorInfos + Constants.isLocalFilterQry)) {
                        alAssignColl.add(Constants.CompetitorInfos);
                    }
                    if (OfflineManager.getVisitStatusForCustomer(Constants.Stocks + Constants.isLocalFilterQry)) {
                        alAssignColl.add(Constants.Stocks);
                    }
                    SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);

//            TODo For location capturing
                   /* if (sharedPreferences.getInt(Constants.CURRENT_VERSION_CODE, 0) >= 21 && Arrays.asList(Constants.getDefinigReq(context)).contains(Constants.SPGeos)) {
                        if (OfflineManager.getVisitStatusForCustomer(Constants.SPGeos + Constants.isLocalFilterQry)) {
                            alAssignColl.add(Constants.SPGeos);
                        }
                    }*/

                   /* if (sharedPreferences.getInt(Constants.CURRENT_VERSION_CODE, 0) >= 21) {
                        if (OfflineManager.getVisitStatusForCustomerGeo(Constants.SPGeos + Constants.isLocalFilterQry)) {
                            alAssignColl.add(Constants.SPGeos);
                        }
                    }*/

                    if (sharedPreferences.getInt(Constants.CURRENT_VERSION_CODE, 0) >= 21 && Arrays.asList(Constants.getDefinigReq(context)).contains(Constants.SyncHistorys)) {
                        if (OfflineManager.getVisitStatusForCustomer(Constants.SyncHistorys + Constants.isLocalFilterQry)) {
                            alAssignColl.add(Constants.SyncHistorys);
                        }
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return alAssignColl;
    }

    public static ArrayList<String> getRefreshListAuto(final Context context) {
        final ArrayList<String> alAssignColl = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (OfflineManager.getVisitStatusForCustomer(Constants.Attendances + Constants.isLocalFilterQry)) {
                        alAssignColl.add(Constants.Attendances);
                    }
                    if (OfflineManager.getVisitStatusForCustomer(Constants.Visits + Constants.isLocalFilterQry)) {
                        alAssignColl.add(Constants.Visits);
                    }

                    if (OfflineManager.getVisitStatusForCustomer(Constants.VisitActivities + Constants.isLocalFilterQry)) {
                        alAssignColl.add(Constants.VisitActivities);
                    }

                    if (OfflineManager.getVisitStatusForCustomer(Constants.MerchReviews + Constants.isLocalFilterQry)) {
                        alAssignColl.add(Constants.MerchReviews);
                        alAssignColl.add(Constants.MerchReviewImages);
                    }

                    if (OfflineManager.getVisitStatusForCustomer(Constants.CompetitorInfos + Constants.isLocalFilterQry)) {
                        alAssignColl.add(Constants.CompetitorInfos);
                    }
                    if (OfflineManager.getVisitStatusForCustomer(Constants.Stocks + Constants.isLocalFilterQry)) {
                        alAssignColl.add(Constants.Stocks);
                    }
                    SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);

//            TODo For location capturing
                   /* if (sharedPreferences.getInt(Constants.CURRENT_VERSION_CODE, 0) >= 21 && Arrays.asList(Constants.getDefinigReq(context)).contains(Constants.SPGeos)) {
                        if (OfflineManager.getVisitStatusForCustomer(Constants.SPGeos + Constants.isLocalFilterQry)) {
                            alAssignColl.add(Constants.SPGeos);
                        }
                    }*/

                  /*  if (sharedPreferences.getInt(Constants.CURRENT_VERSION_CODE, 0) >= 21) {
                        if (OfflineManager.getVisitStatusForCustomerGeo(Constants.SPGeos + Constants.isLocalFilterQry)) {
                            alAssignColl.add(Constants.SPGeos);
                        }
                    }*/

                    if (sharedPreferences.getInt(Constants.CURRENT_VERSION_CODE, 0) >= 21 && Arrays.asList(Constants.getDefinigReq(context)).contains(Constants.SyncHistorys)) {
                        if (OfflineManager.getVisitStatusForCustomer(Constants.SyncHistorys + Constants.isLocalFilterQry)) {
                            alAssignColl.add(Constants.SyncHistorys);
                        }
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return alAssignColl;
    }

    public ArrayList<Object> getPendingCollList(Context mContext, boolean isAutoSync) {
        ArrayList<Object> objectsArrayList = new ArrayList<>();
        int mIntPendingCollVal = 0;
        String[][] invKeyValues = null;
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        invKeyValues = new String[getPendingListSize(mContext)][2];

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
            Arrays.sort(removeNullValues, new ArrayComarator());
            objectsArrayList.add(mIntPendingCollVal);
            objectsArrayList.add(removeNullValues);
            objectsArrayList.add(cancelSOCount);
        }

        return objectsArrayList;

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
        // ActionBarView.initActionBarView(this, true, getString(R.string.syncmenu));
        setContentView(R.layout.activity_sync_selction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mSharedPrefs = getSharedPreferences(Constants.PREFS_NAME, 0);
        if (mSharedPrefs.getBoolean("writeDBGLog", false)) {
            Constants.writeDebug = mSharedPrefs.getBoolean("writeDBGLog", false);
        }
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.syncmenu), 0);
        deleteDataFromSODV();
        onInitUI();
        setIconVisiblity();
        setValuesToUI();
        mTrafficSpeedMeasurer = new TrafficSpeedMeasurer(TrafficSpeedMeasurer.TrafficType.ALL);

    }

    private void deleteDataFromSODV() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Constants.deletePostedSOData(SyncSelectionActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /*
               * TODO This method initialize UI
               */
    private void onInitUI() {
        grid_main = (GridView) findViewById(R.id.GridView01);

    }

    /*
  TODO This method set values to UI
  */
    private void setValuesToUI() {
        grid_main.setAdapter(new SyncAdapter(this));
    }

    private void setIconVisiblity() {
        OriginalStatus[0] = 1;
        OriginalStatus[1] = 1;
        OriginalStatus[2] = 1;
        OriginalStatus[3] = 1;
        int countStatus = 0;
        int len = OriginalStatus.length;
        for (int countOriginalStaus = 0; countOriginalStaus < len; countOriginalStaus++) {
            if (OriginalStatus[countOriginalStaus] == 1) {
                TempStatus[countStatus] = countOriginalStaus;
                countStatus++;
            }
        }
    }

   /* private void getPendingCollList() {
        penReqCount = 0;
        mIntPendingCollVal = 0;
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        set = sharedPreferences.getStringSet(Constants.CollList, null);
        invKeyValues = new String[getPendingListSize(SyncSelectionActivity.this)][2];
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.CollList;
                mIntPendingCollVal++;
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
        set = sharedPreferences.getStringSet(Constants.SOCancel, null);

        int cancelSOSize = 0;
        cancelSOSize = (set != null && !set.isEmpty()) ? set.size() : 0;

        set = sharedPreferences.getStringSet(Constants.SOUpdate, null);
        int changeSOSize = 0;
        changeSOSize = (set != null && !set.isEmpty()) ? set.size() : 0;

        cancelSOCount = new int[cancelSOSize + changeSOSize];
        int i = 0;
        set = sharedPreferences.getStringSet(Constants.SOCancel, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
//            cancelSOCount = new int[set.size()];
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.SOCancel;

                String store = null;
                try {
                    store = LogonCore.getInstance().getObjectFromStore(invKeyValues[mIntPendingCollVal][0].toString());
                } catch (LogonCoreException e) {
                    e.printStackTrace();
                }


                //Fetch object from data vault
                try {

                    JSONObject fetchJsonHeaderObject = new JSONObject(store);
                    String itemsString = fetchJsonHeaderObject.getString(Constants.SalesOrderItems);
                    arrtable = UtilConstants.convertToArrayListMap(itemsString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cancelSOCount[i] = arrtable.size() + 1;
                i++;
                mIntPendingCollVal++;
            }
        }
        set = sharedPreferences.getStringSet(Constants.SOUpdate, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
//            cancelSOCount = new int[set.size()];
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.SOUpdate;

                String store = null;
                try {
                    store = LogonCore.getInstance().getObjectFromStore(invKeyValues[mIntPendingCollVal][0].toString());
                } catch (LogonCoreException e) {
                    e.printStackTrace();
                }


                //Fetch object from data vault
                try {

                    JSONObject fetchJsonHeaderObject = new JSONObject(store);
                    String itemsString = fetchJsonHeaderObject.getString(Constants.SalesOrderItems);
                    arrtable = UtilConstants.convertToArrayListMap(itemsString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cancelSOCount[i] = arrtable.size() + 1;
                i++;
                mIntPendingCollVal++;
            }
        }


        set = sharedPreferences.getStringSet(Constants.Expenses, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.Expenses;
                mIntPendingCollVal++;
            }
        }

        if (mIntPendingCollVal > 0) {
            Arrays.sort(invKeyValues, new ArrayComarator());
        }

    }*/
   private void startNetworkMonitoring(){
       mTrafficSpeedMeasurer.startMeasuring();
       isMonitoringStopped = true;
       checkNetwork(this, new OnNetworkInfoListener() {
           @RequiresApi(api = Build.VERSION_CODES.M)
           @Override
           public void onNetworkFailureListener(boolean isFailed) {
               if (isFailed) {
                    /*runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //  Toast.makeText(SyncSelectionActivity.this, "Network dropped / unable to connect.", Toast.LENGTH_SHORT).show();
                        }
                    });*/
               }
           }
       },false,0);

   }
    /**
     * This method update pending requests.
     */
    private void onUpdateSync() {
        try {
            Constants.Entity_Set.clear();
            Constants.AL_ERROR_MSG.clear();
            mBoolIsNetWorkNotAval = false;
            isBatchReqs = false;
            mBoolIsReqResAval = true;
            updateCancelSOCount = 0;
            cancelSoPos = 0;
            try {
                penReqCount = 0;
                mIntPendingCollVal = 0;
                invKeyValues = null;
                cancelSOCount = new int[0];
                ArrayList<Object> objectArrayList = getPendingCollList(SyncSelectionActivity.this, false);
                if (objectArrayList != null && !objectArrayList.isEmpty()) {
                    mIntPendingCollVal = (int) objectArrayList.get(0);
                    invKeyValues = (String[][]) objectArrayList.get(1);
                    cancelSOCount = (int[]) objectArrayList.get(2);
                }


                SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,0);
                /*if(OfflineManager.offlineGeo !=null && !OfflineManager.offlineGeo.getRequestQueueIsEmpty()) {
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
                                            new RefreshGeoAsyncTask(getApplicationContext(), Constants.SPGeos, new UIListener() {
                                                @Override
                                                public void onRequestError(int i, Exception e) {
                                                    Log.d("Geo_onRequestError","Refresh");

                                                }

                                                @Override
                                                public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                                                    Log.d("Geo_onRequestSuccess","Refresh");
                                                }
                                            }).execute();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            TraceLog.e(Constants.SyncOnRequestSuccess, e);
                                        }
                                    } else {
                                        Constants.isSync = false;
                                        closingProgressDialog();
                                        showAlert(getString(R.string.data_conn_lost_during_sync));
                                    }
                                } else if (operation == Operation.OfflineRefresh.getValue()) {
                                    //refreshData();
                                }
                                //  refreshData();
                            }
                        }, Constants.SPGeos);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
//                    UtilConstants.showAlert(getString(R.string.no_req_to_update_sap), SyncSelectionActivity.this);
                }else {

                }*/

                try {
                    if (OfflineManager.offlineGeo!=null &&!OfflineManager.offlineGeo.getRequestQueueIsEmpty() && sharedPreferences.getBoolean(getString(R.string.enable_geo), false)) {
                       if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                           try {
                               OfflineManager.flushQueuedRequestsForGeo(new UIListener() {
                                   @Override
                                   public void onRequestError(int i, Exception e) {
                                      // refreshData();
                                       LogManager.writeLogError("Geo_onRequestError  : Flush");
                                   }

                                   @Override
                                   public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
                                       if (operation == Operation.OfflineFlush.getValue()) {
                                           if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                                               try {
   //                        OfflineManager.refreshRequests(getApplicationContext(), concatCollectionStr, SyncSelectionActivity.this);
                                                   new RefreshGeoAsyncTask(getApplicationContext(), Constants.SPGeos, new UIListener() {
                                                       @Override
                                                       public void onRequestError(int i, Exception e) {
                                                           LogManager.writeLogError("Geo_onRequestError  : Refresh");
                                                       }

                                                       @Override
                                                       public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                                                           LogManager.writeLogError("Geo_onRequestSuccess  : Refresh");
                                                       }
                                                   }).execute();
                                               } catch (Exception e) {
                                                   e.printStackTrace();
                                                   TraceLog.e(Constants.SyncOnRequestSuccess, e);
                                               }
                                           } else {
                                               Constants.isSync = false;
                                               closingProgressDialog();
                                               showAlert(getString(R.string.data_conn_lost_during_sync));
                                           }
                                       } else if (operation == Operation.OfflineRefresh.getValue()) {
                                          // refreshData();
                                           LogManager.writeLogError("Geo_onRequestSuccess : Refresh");
                                       }
                                       //  refreshData();
                                   }
                               }, Constants.SPGeos);
                           } catch (OfflineODataStoreException e) {
                               e.printStackTrace();
                           }
                       } else {
                           if (Constants.writeDebug)
                               LogManager.writeLogDebug("Upload Sync Geo: " + getString(R.string.no_network_conn));
//                           showAlert(getString(R.string.no_network_conn));
                       }
                   }
                } catch (ODataException e) {
                    e.printStackTrace();
                }


                if (OfflineManager.offlineStore.getRequestQueueIsEmpty()  && mIntPendingCollVal == 0) {
                    showAlert(getString(R.string.no_req_to_update_sap));
                } else {
                    alAssignColl.clear();
                    alFlushColl.clear();
                    ArrayList<String> allAssignColl = getRefreshList(SyncSelectionActivity.this);
                    if (allAssignColl != null && !allAssignColl.isEmpty()) {
                        alAssignColl.addAll(allAssignColl);
                        alFlushColl.addAll(allAssignColl);
                    }
                    if (Constants.iSAutoSync || Constants.isBackGroundSync || Constants.isPullDownSync) {
                        if (Constants.iSAutoSync) {
                            showAlert(getString(R.string.alert_auto_sync_is_progress));
                        } else if (Constants.isBackGroundSync) {
                            showAlert(getString(R.string.alert_backgrounf_sync_is_progress));
                        } else if (Constants.isPullDownSync) {
                            showAlert(getString(R.string.alert_backgrounf_sync_is_progress));
                        }
                    } else {
                        if (mIntPendingCollVal > 0) {

                            if (!alAssignColl.contains(Constants.ConfigTypsetTypeValues))
                                alAssignColl.add(Constants.ConfigTypsetTypeValues);

                            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                                startNetworkMonitoring();
                                refguid = GUID.newRandom();
                                Constants.updateStartSyncTime(this, Constants.UpLoad, Constants.StartSync,refguid.toString().toUpperCase());
                                Constants.isSync = true;
                                if (Constants.writeDebug)
                                    LogManager.writeLogDebug("Upload Sync : Started");
                                onPostOnlineData();
                            } else {
                                if (Constants.writeDebug)
                                    LogManager.writeLogDebug("Upload Sync : " + getString(R.string.no_network_conn));
                                showAlert(getString(R.string.no_network_conn));
                            }
                        } else if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                            startNetworkMonitoring();
                            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                                refguid = GUID.newRandom();
                                Constants.updateStartSyncTime(this, Constants.UpLoad, Constants.StartSync,refguid.toString().toUpperCase());
                                onPostOfflineData();
                            } else {
                                if (Constants.writeDebug)
                                    LogManager.writeLogDebug("Upload Sync : " + getString(R.string.no_network_conn));
                                showAlert(getString(R.string.no_network_conn));
                            }
                        }
                    }
                }
            } catch (ODataException e) {
                e.printStackTrace();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void onPostOfflineData() {
        Constants.isSync = true;
        try {
            new AsyncPostOfflineData().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onPostOnlineData() {
        try {
            if (Constants.writeDebug) {
                LogManager.writeLogDebug("Sync : Upload in progress");
            }
            new AsyncPostDataValutData().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSyncAll() {
        try {
            startNetworkMonitoring();
            isAllSync = true;
            Constants.AL_ERROR_MSG.clear();
            Constants.Entity_Set.clear();
            Constants.isSync = true;
            dialogCancelled = false;
            Constants.isStoreClosed = false;

            statAsyncTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void statAsyncTask() {
        syncProgDialog = new ProgressDialog(SyncSelectionActivity.this, R.style.ProgressDialogTheme);
        syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
        syncProgDialog.setCancelable(true);
        syncProgDialog.setCanceledOnTouchOutside(false);
        syncProgDialog.show();

        syncProgDialog
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface Dialog) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                SyncSelectionActivity.this, R.style.MyTheme);
                        builder.setMessage(R.string.do_want_cancel_sync)
                                .setCancelable(false)
                                .setPositiveButton(
                                        R.string.yes,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface Dialog,
                                                    int id) {
                                                dialogCancelled = true;
                                                if(refguid==null){
                                                    refguid = GUID.newRandom();
                                                }
                                                Constants.updateStartSyncTime(SyncSelectionActivity.this,Constants.download_all_cancel_sync,Constants.EndSync,refguid.toString().toUpperCase());
                                                onBackPressed();
                                            }
                                        })
                                .setNegativeButton(
                                        R.string.no,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface Dialog,
                                                    int id) {

                                                displayProgressDialog();

                                            }
                                        });
                        builder.show();
                    }
                });
        assignCollToArrayList();
         refguid = GUID.newRandom();
        new AllSyncAsyncTask(SyncSelectionActivity.this, this, new ArrayList<String>(),refguid.toString().toUpperCase()).execute();
    }

    /**
     * This method calls sync all collections for the selected "All" icon
     */
    private void onAllSync() {
        startServices = true;
        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
            try {
                if (!OfflineManager.getSyncStatus(Constants.Sync_All)) {
                    syncAll();
                } else {
                    syncAlreadyDoneorNotMsg();
                }
            } catch (OfflineODataStoreException e) {
                syncAll();
                e.printStackTrace();
            }
        } else {
            showAlert(getString(R.string.no_network_conn));
        }
    }

    private void syncAll() {
        if (Constants.iSAutoSync || Constants.isBackGroundSync || Constants.isPullDownSync) {
            if (Constants.iSAutoSync) {
                showAlert(getString(R.string.alert_auto_sync_is_progress));
            } else {
                showAlert(getString(R.string.alert_backgrounf_sync_is_progress));
            }
        } else {
            onSyncAll();
        }
    }

    /**
     * This method calls fresh sync for the selected "Fresh" icon
     */
    private void onFreshSync() {
        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
            try {
                if(!OfflineManager.getSyncStatus(Constants.DownLoad)){
                    Intent intent = new Intent(this, SyncSelectViewActivity.class);
                    startActivity(intent);
                    isClickable=false;
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
                    builder.setMessage(R.string.download_sync_confirmation)
                            .setCancelable(false)
                            .setPositiveButton(R.string.yes,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Intent intent = new Intent(SyncSelectionActivity.this, SyncSelectViewActivity.class);
                                            startActivity(intent);
                                            isClickable=false;
                                        }
                                    })
                            .setNegativeButton(R.string.no,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            isClickable = false;
                                        }
                                    });


                    builder.show();
                }
            } catch (OfflineODataStoreException e) {
                Intent intent = new Intent(this, SyncSelectViewActivity.class);
                startActivity(intent);
                isClickable=false;
                e.printStackTrace();
            }
        } else {
            showAlert(getString(R.string.no_network_conn));
        }
    }

    private void syncAlreadyDoneorNotMsg(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
        builder.setMessage(R.string.all_sync_confirmation)
                .setCancelable(false)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                syncAll();
                            }
                        })
                .setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                isClickable = false;
                            }
                        });


        builder.show();
    }
    private void assignCollToArrayList() {
        alAssignColl.clear();
        concatCollectionStr = "";
        for (int i = 0; i < alAssignColl.size(); i++) {

            if(Constants.writeDebug)
            LogManager.writeLogDebug("All Sync Starts:" + alAssignColl.get(i));
        }
        alAssignColl.addAll(Constants.getDefinigReqList(SyncSelectionActivity.this));
        /*alAssignColl.add(Constants.Attendances);
        alAssignColl.add(Constants.Customers);
        alAssignColl.add(Constants.Visits);
        alAssignColl.add(Constants.RoutePlans);
        alAssignColl.add(Constants.RouteSchedulePlans);
        alAssignColl.add(Constants.RouteSchedules);
//		alAssignColl.add(Constants.FinancialPostingItemDetails);
//		alAssignColl.add(Constants.FinancialPostings);
        alAssignColl.add(Constants.INVOICES);
        alAssignColl.add(Constants.InvoiceItemDetails);
        alAssignColl.add(Constants.OutstandingInvoices);
        alAssignColl.add(Constants.OutstandingInvoiceItemDetails);
        alAssignColl.add(Constants.VisitActivities);

        alAssignColl.add(Constants.SOs);
        alAssignColl.add(Constants.SOConditions);
        alAssignColl.add(Constants.SOItems);
        alAssignColl.add(Constants.SOItemDetails);
        alAssignColl.add(Constants.SOTexts);
        alAssignColl.add(Constants.CustomerPartnerFunctions);
        alAssignColl.add(Constants.CustomerSalesAreas);
        alAssignColl.add(Constants.MaterialSaleAreas);

        alAssignColl.add(Constants.ValueHelps);
        alAssignColl.add(Constants.ExpenseConfigs);
        alAssignColl.add(Constants.ConfigTypesetTypes);
        alAssignColl.add(Constants.ConfigTypsetTypeValues);
        alAssignColl.add(Constants.SalesPersons);
        alAssignColl.add(Constants.UserProfileAuthSet);
        alAssignColl.add(Constants.SegmentedMaterials);
        alAssignColl.add(Constants.ChannelPartners);*/

//		alAssignColl.add(Constants.SegmentedMaterials);
//		alAssignColl.add(Constants.ConfigTypsetTypeValues);
//		alAssignColl.add(Constants.CPStockItemSnos);
//
//		alAssignColl.add(Constants.KPISet);
//		alAssignColl.add(Constants.Targets);
//		alAssignColl.add(Constants.TargetItems);
//		alAssignColl.add(Constants.KPIItems);
//
//		alAssignColl.add(Constants.CompetitorInfos);
//		alAssignColl.add(Constants.CompetitorMasters);

        // Todo check current day cpstock items synced or not
//		if(!Constants.isSpecificCollTodaySyncOrNot(Constants.getLastSyncDate(Constants.SYNC_TABLE, Constants.Collections,
//				Constants.CPStockItems, Constants.TimeStamp,SyncSelectionActivity.this))){
//			alAssignColl.add(Constants.CPStockItems);
//		}
//
//		alAssignColl.add(Constants.MerchReviews);
//		alAssignColl.add(Constants.MerchReviewImages);

       /* for (int incVal = 0; incVal < alAssignColl.size(); incVal++) {
            if (incVal == 0 && incVal == alAssignColl.size() - 1) {
                concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
            } else if (incVal == 0) {
                concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
            } else if (incVal == alAssignColl.size() - 1) {
                concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
            } else {
                concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
            }
        }*/
    }

    private void displayProgressDialog() {
        try {
            syncProgDialog
                    .show();
            syncProgDialog
                    .setCancelable(true);
            syncProgDialog
                    .setCanceledOnTouchOutside(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialogCancelled = false;
    }

    @Override
    public void onRequestError(int operation, Exception ex) {
        isClickable=false;
        if (ex != null)
            ex.printStackTrace();
        final ErrorBean errorBean = Constants.getErrorCode(operation, ex, SyncSelectionActivity.this);
        String customeNEO = Constants.checkUnknownNetworkerror(errorBean.getErrorMsg(), SyncSelectionActivity.this);


        LogManager.writeLogDebug("Sync Failed : " + operation + ":" + ex.getLocalizedMessage());
        try {
            if (!TextUtils.isEmpty(errorBean.getErrorMsg()) && errorBean.getErrorMsg().contains("10348") && !isRefreshDB) {
                isRefreshDB = true;
                if (mSharedPrefs.getBoolean("flagTointializeDB", false)) {
                    closingProgressDialog();
                    syncProgDialog = new ProgressDialog(SyncSelectionActivity.this, R.style.ProgressDialogTheme);
                    syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
                    syncProgDialog.setCancelable(true);
                    syncProgDialog.setCanceledOnTouchOutside(false);
                    syncProgDialog.show();

                    if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                        Constants.isSync = true;
                        Constants.closeStore(SyncSelectionActivity.this);
                        new RefreshAsyncTask(SyncSelectionActivity.this, "", this).execute();
                    }


                } else {
                    SharedPreferences.Editor editor = mSharedPrefs.edit();
                    try {
                        editor.putBoolean("flagTointializeDB", true);
                        editor.apply();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    UtilConstants.dialogBoxWithCallBack(SyncSelectionActivity.this, "", getString(R.string.error_10348) + "Kindly Authenticate again", "OK", "", false, new DialogCallBack() {
                        @Override
                        public void clickedStatus(boolean b) {
                            Constants.iSAutoSync = false;
                            UpdatePendingRequest.instance = null;
                            if (OfflineManager.offlineStore != null) {
                                try {
                                    OfflineManager.offlineStore.closeStore();
                                } catch (ODataException e) {
                                    e.printStackTrace();
                                }
                            }
                            OfflineManager.offlineStore = null;
                            OfflineManager.options = null;
                            OfflineManager.optionsGeo = null;
                            OfflineManager.offlineGeo = null;
                            android.os.Process.killProcess(android.os.Process.myPid());
//                                finishAffinity();
                            System.exit(1);

                        }
                    });

                }
            } else {
                if (customeNEO.equalsIgnoreCase("")){
                    if (errorBean.hasNoError()) {
                        mBoolIsReqResAval = true;
//                ConstantsUtils.printErrorLog(ex.getMessage());
                        if (dialogCancelled == false && !Constants.isStoreClosed) {

                            if (operation == Operation.Update.getValue() && mIntPendingCollVal > 0) {
                                updateCancelSOCount++;
                                updateCancelSOCount = 0;
                                cancelSoPos++;
                                String popUpText = "";
                                if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SOCancel))
                                    popUpText = "Sales order # " + invKeyValues[penReqCount][0] + " cancellation failed.";
                                else
                                    popUpText = "Sales order # " + invKeyValues[penReqCount][0] + " changed failed.";
                                LogManager.writeLogInfo(popUpText);
                            }
                            penReqCount++;

                            if ((operation == Operation.Create.getValue() || operation == Operation.Update.getValue()) && (penReqCount == mIntPendingCollVal)) {

                                try {
                                    if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                                        closingProgressDialog();
                                        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                                            try {
                                                new AsyncPostOfflineData().execute();
                                            } catch (Exception e2) {
                                                e2.printStackTrace();
                                            }
                                        } else {
                                            Constants.isSync = false;
                                            showAlert(getString(R.string.data_conn_lost_during_sync));
                                        }
                                    } else {
                                        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                                            try {
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
                                                new RefreshAsyncTask(getApplicationContext(), concatCollectionStr, this).execute();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                TraceLog.e(Constants.SyncOnRequestSuccess, e);
                                            }
                                        } else {
                                            Constants.isSync = false;
                                            closingProgressDialog();
                                            showAlert(getString(R.string.data_conn_lost_during_sync));

                                        }
                                    }

                                } catch (ODataException e3) {
                                    e3.printStackTrace();
                                }
                            }

                        }

                        if (operation == Operation.OfflineFlush.getValue()) {
                            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                                try {
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
                                    new RefreshAsyncTask(getApplicationContext(), concatCollectionStr, this).execute();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    TraceLog.e(Constants.SyncOnRequestSuccess, e);
                                }
                            } else {
                                Constants.isSync = false;
                                closingProgressDialog();
                                showAlert(getString(R.string.data_conn_lost_during_sync));
                            }
                        } else if (operation == Operation.OfflineRefresh.getValue()) {
                            updatingSyncTime();
                            Constants.isSync = false;
                            String mErrorMsg = "";
                            if (Constants.AL_ERROR_MSG.size() > 0) {
                                mErrorMsg = Constants.convertALBussinessMsgToString(Constants.AL_ERROR_MSG);
                            }
                            try {
                                final String finalMErrorMsg = mErrorMsg;
                                Constants.updateSyncTime(alAssignColl, this, refguid.toString().toUpperCase(), syncHistoryType, new SyncHistoryCallBack() {
                                    @Override
                                    public void displaySuccessMessage() {

                                        try {
                                            if(!isFinishing()) {
                                                if (finalMErrorMsg.equalsIgnoreCase("")) {
                                                    closingProgressDialog();
                                                    if (errorBean.getErrorMsg().contains("invalid authentication")) {
                                                        try {
                                                            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,0);
                                                            String loginUser=sharedPreferences.getString("username","");
                                                            String login_pwd=sharedPreferences.getString("password","");
                                                            UtilConstants.getPasswordStatus(Configuration.IDPURL, loginUser, login_pwd, Configuration.APP_ID, new UtilConstants.PasswordStatusCallback() {
                                                                @Override
                                                                public void passwordStatus(final JSONObject jsonObject) {

                                                                    if(!isFinishing()) {
                                                                       runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                Constants.passwordStatusErrorMessage(SyncSelectionActivity.this, jsonObject, loginUser);
                                                                            }
                                                                        });
                                                                    }

                                                                }
                                                            });
                                                        } catch (Throwable e) {
                                                            e.printStackTrace();
                                                        }
                                                    }else {
                                                        showAlert(errorBean.getErrorMsg());
                                                    }
                                                } else {

                                                    if (errorBean.getErrorMsg().contains("invalid authentication")) {
                                                        try {
                                                            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
                                                            String loginUser = sharedPreferences.getString("username", "");
                                                            String login_pwd = sharedPreferences.getString("password", "");
                                                            UtilConstants.getPasswordStatus(Configuration.IDPURL, loginUser, login_pwd, Configuration.APP_ID, new UtilConstants.PasswordStatusCallback() {
                                                                @Override
                                                                public void passwordStatus(final JSONObject jsonObject) {

                                                                    if (!isFinishing()) {
                                                                        runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                closingProgressDialog();
                                                                                Constants.passwordStatusErrorMessage(SyncSelectionActivity.this, jsonObject, loginUser);
                                                                            }
                                                                        });
                                                                    }

                                                                }
                                                            });
                                                        } catch (Throwable e) {
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    closingProgressDialog();
                                                                }
                                                            });

                                                            e.printStackTrace();
                                                        }
                                                    }else {
                                                        closingProgressDialog();
                                                        Constants.customAlertDialogWithScroll(SyncSelectionActivity.this, finalMErrorMsg);
                                                    }
                                                }
                                            }else {
                                                try {
                                                    LogManager.writeLogError("SyncSelectionActivity finished");
                                                } catch (Throwable e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        } catch (Throwable e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (Exception exce) {
                                exce.printStackTrace();
                                LogManager.writeLogError(Constants.sync_table_history_txt + exce.getMessage());
                            }
                        } else if (operation == Operation.GetStoreOpen.getValue()) {
                            mBoolIsReqResAval = true;
                            mBoolIsNetWorkNotAval = true;
                            closingProgressDialog();
                            Constants.isSync = false;
                            showAlert(errorBean.getErrorMsg());
                        }
                    } else if (errorBean.isStoreFailed()) {
                        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                            mBoolIsReqResAval = true;
                            mBoolIsNetWorkNotAval = true;
                            Constants.isSync = true;
                            closingProgressDialog();
                            onSyncAll();
                        } else {
                            mBoolIsReqResAval = true;
                            mBoolIsNetWorkNotAval = true;
                            Constants.isSync = false;
                            closingProgressDialog();
                            Constants.displayMsgReqError(errorBean.getErrorCode(), SyncSelectionActivity.this);
                        }
                    } else {
                        mBoolIsReqResAval = true;
                        mBoolIsNetWorkNotAval = true;
                        Constants.isSync = false;
                        closingProgressDialog();
                        Constants.displayMsgReqError(errorBean.getErrorCode(), SyncSelectionActivity.this);
                    }
            }else{
                    closingProgressDialog();
                    UtilConstants.showAlert(customeNEO, SyncSelectionActivity.this);

                }
        }
        } catch (Exception e) {
            mBoolIsReqResAval = true;
            mBoolIsNetWorkNotAval = true;
            Constants.isSync = false;
            closingProgressDialog();
            Constants.displayMsgReqError(errorBean.getErrorCode(), SyncSelectionActivity.this);
        }
    }

    private void setAppointmentNotification() {
        new NotificationSetClass(this);

    }

    @Override
    public void onRequestSuccess(int operation, String key) {
        isClickable=false;
        if (dialogCancelled == false && !Constants.isStoreClosed) {
            if (operation == Operation.Create.getValue() && mIntPendingCollVal > 0) {
                mBoolIsReqResAval = true;
                if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.CollList)) {
                    Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.CollList, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SalesOrderDataValt)) {
                    Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.SalesOrderDataValt, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SOUpdate)) {
                    Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.SOUpdate, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SOCancel)) {
                    Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.SOCancel, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.Expenses)) {
                    Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.Expenses, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.MTPDataValt)) {
                    Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.MTPDataValt, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.RTGSDataValt)) {
                    Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.RTGSDataValt, invKeyValues[penReqCount][0]);
                }

                if(Constants.writeDebug)
                LogManager.writeLogDebug("Upload Sync Create Success");

                ConstantsUtils.storeInDataVault(invKeyValues[penReqCount][0], "",this);

                penReqCount++;
            }
            if (operation == Operation.Update.getValue() && mIntPendingCollVal > 0) {
                mBoolIsReqResAval = true;
                updateCancelSOCount++;
                if (cancelSOCount.length == 0 || updateCancelSOCount == cancelSOCount[cancelSoPos]) {
                    updateCancelSOCount = 0;
                    cancelSoPos++;

                    if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.CollList)) {
                        Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.CollList, invKeyValues[penReqCount][0]);
                    } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SalesOrderDataValt)) {
                        Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.SalesOrderDataValt, invKeyValues[penReqCount][0]);
                    } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SOUpdate)) {
                        Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.SOUpdate, invKeyValues[penReqCount][0]);
                    } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SOCancel)) {
                        Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.SOCancel, invKeyValues[penReqCount][0]);
                    } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.Expenses)) {
                        Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.Expenses, invKeyValues[penReqCount][0]);
                    } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.MTPDataValt)) {
                        Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.MTPDataValt, invKeyValues[penReqCount][0]);
                    } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.RTGSDataValt)) {
                        Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.RTGSDataValt, invKeyValues[penReqCount][0]);
                    }

                    ConstantsUtils.storeInDataVault(invKeyValues[penReqCount][0], "",this);
                    String popUpText = "";
                    if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SOCancel))
                        popUpText = "Sales order # " + invKeyValues[penReqCount][0] + " cancelled successfully.";
                    else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SOUpdate))
                        popUpText = "Sales order # " + invKeyValues[penReqCount][0] + " changed successfully.";
                    LogManager.writeLogInfo(popUpText);

                    penReqCount++;

                    if(Constants.writeDebug)
                    LogManager.writeLogDebug("Upload Sync Update success");
                }
            }
            if ((operation == Operation.Create.getValue() || operation == Operation.Update.getValue()) && (penReqCount == mIntPendingCollVal)) {
                try {
                    if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                        closingProgressDialog();
                        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                            try {
                                new AsyncPostOfflineData().execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Constants.isSync = false;
                            showAlert(getString(R.string.data_conn_lost_during_sync));
                        }
                    } else {
                        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                            try {
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
                                new RefreshAsyncTask(getApplicationContext(), concatCollectionStr, this).execute();
//                                OfflineManager.refreshRequests(getApplicationContext(), concatCollectionStr, SyncSelectionActivity.this);
                            } catch (Exception e) {
                                e.printStackTrace();
                                TraceLog.e(Constants.SyncOnRequestSuccess, e);
                            }
                        } else {
                            Constants.isSync = false;
                            closingProgressDialog();
                            showAlert(getString(R.string.data_conn_lost_during_sync));
                        }
                    }

                } catch (ODataException e) {
                    e.printStackTrace();
                    LogManager.writeLogDebug("Upload Sync Exception: " + e.getLocalizedMessage());
                }

            } else if (operation == Operation.OfflineFlush.getValue()) {
                if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                    try {
                        for (int incVal = 0; incVal < alAssignColl.size(); incVal++) {
                            if (incVal == 0 && incVal == alAssignColl.size() - 1) {
                                if (Constants.writeDebug)
                                LogManager.writeLogDebug("Upload Sync : " + concatCollectionStr + " Starts");
                                concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
                            } else if (incVal == 0) {
                                if (Constants.writeDebug)
                                LogManager.writeLogDebug("Upload Sync : " + concatCollectionStr + " Starts");
                                concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
                            } else if (incVal == alAssignColl.size() - 1) {
                                if (Constants.writeDebug)
                                LogManager.writeLogDebug("Upload Sync : " + concatCollectionStr + " Starts");
                                concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
                            } else {
                                if (Constants.writeDebug)
                                LogManager.writeLogDebug("Upload Sync : " + concatCollectionStr + " Starts");
                                concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
                            }
                        }
//                        OfflineManager.refreshRequests(getApplicationContext(), concatCollectionStr, SyncSelectionActivity.this);
                        new RefreshAsyncTask(getApplicationContext(), concatCollectionStr, this).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                        TraceLog.e(Constants.SyncOnRequestSuccess, e);
                    }
                } else {
                    Constants.isSync = false;
                    closingProgressDialog();
                    showAlert(getString(R.string.data_conn_lost_during_sync));
                }
            } else if (operation == Operation.OfflineRefresh.getValue()) {
                refreshData();

               /* try {
                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,0);
                    if (OfflineManager.offlineGeo!=null &&!OfflineManager.offlineGeo.getRequestQueueIsEmpty() && sharedPreferences.getBoolean(getString(R.string.enable_geo), false)) {
                        try {
                            OfflineManager.flushQueuedRequestsForGeo(new UIListener() {
                                @Override
                                public void onRequestError(int i, Exception e) {
                                    refreshData();
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
                                            closingProgressDialog();
                                            showAlert(getString(R.string.data_conn_lost_during_sync));
                                        }
                                    } else if (operation == Operation.OfflineRefresh.getValue()) {
                                        refreshData();
                                    }
                                }
                            }, Constants.SPGeos);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                    } else {
                        refreshData();

                    }
                } catch (ODataException e) {
                    e.printStackTrace();
                }*/
            } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
                Constants.isSync = false;
                new NotificationSetClass(getApplicationContext());
                ConstantsUtils.startAutoSync(SyncSelectionActivity.this, false);
                try {
                    OfflineManager.getAuthorizations(getApplicationContext());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                Constants.setSyncTime(SyncSelectionActivity.this,refguid.toString().toUpperCase());
                closingProgressDialog();
                if (Constants.writeDebug)
                    LogManager.writeLogDebug("Upload Sync : Completed");
                showAlert(getString(R.string.msg_sync_successfully_completed));
            }
        }
    }

    private void closingProgressDialog() {
        try {
            syncProgDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        concatCollectionStr = "";
    }

    private void refreshData() {
        try {
            OfflineManager.getAuthorizations(getApplicationContext());
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        Constants.deleteDeviceMerchansisingFromDataVault(SyncSelectionActivity.this);
        Constants.setBirthdayListToDataValut(SyncSelectionActivity.this);
        ConstantsUtils.startAutoSync(SyncSelectionActivity.this, false);
        setAppointmentNotification();
        updatingSyncTime();
        Constants.isSync = false;
        String mErrorMsg = "";
        if (Constants.AL_ERROR_MSG.size() > 0) {
            mErrorMsg = Constants.convertALBussinessMsgToString(Constants.AL_ERROR_MSG);
        }
        try {
            final String finalMErrorMsg = mErrorMsg;
            Constants.updateSyncTime(alAssignColl, this, syncHistoryType,refguid.toString().toUpperCase(), new SyncHistoryCallBack() {
                @Override
                public void displaySuccessMessage() {
                    closingProgressDialog();
                    if (mError == 0) {
                        // ConstantsUtils.serviceReSchedule(SyncSelectionActivity.this, true);
                        if (startServices) {
//                            ConstantsUtils.serviceReSchedule(SyncSelectionActivity.this, true);

                            ConstantsUtils.stopAlarmManagerByID(SyncSelectionActivity.this, AutoSyncDataAlarmReceiver.class, AutoSyncDataAlarmReceiver.REQUEST_CODE);
                            if (ConstantsUtils.isMyServiceRunning(AutoSynDataService.class, SyncSelectionActivity.this))
                                stopService(new Intent(SyncSelectionActivity.this, AutoSynDataService.class));
                            ConstantsUtils.stopAlarmManagerByID(SyncSelectionActivity.this, AutoSyncDataLocationAlarmReceiver.class, AutoSyncDataLocationAlarmReceiver.REQUEST_CODE);
                            if (ConstantsUtils.isMyServiceRunning(AutoSyncLocationDataService.class, SyncSelectionActivity.this))
                                stopService(new Intent(SyncSelectionActivity.this, AutoSyncLocationDataService.class));
                            /*if (mSharedPrefs.getBoolean(getString(R.string.enable_geo), false))
                                ConstantsUtils.startAutoSyncLocation(SyncSelectionActivity.this, true);
                            else
                                stopService(new Intent(SyncSelectionActivity.this, AutoSyncLocationDataService.class));*/
                            ConstantsUtils.startAutoSync(SyncSelectionActivity.this, true);
                            startServices = false;
                        }
                        if (Constants.writeDebug)
                            LogManager.writeLogDebug("Upload Sync : Completed");
                        UtilConstants.dialogBoxWithCallBack(SyncSelectionActivity.this, "", getString(R.string.msg_sync_successfully_completed), getString(R.string.ok), "", false, new DialogCallBack() {
                            @Override
                            public void clickedStatus(boolean b) {
                                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, SyncSelectionActivity.this, BuildConfig.APPLICATION_ID, false);
                            }
                        });
                        isMonitoringStopped=false;
                        mTrafficSpeedMeasurer.stopMeasuring();
                        mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
                    } else {
                        try {
                            if(!isFinishing()) {
                                if (finalMErrorMsg.equalsIgnoreCase("")) {
                                    showAlert(getString(R.string.error_occured_during_post));
                                } else {
                                    Constants.customAlertDialogWithScroll(SyncSelectionActivity.this, finalMErrorMsg);
                                }
                            }else {
                                try {
                                    LogManager.writeLogError("SyncSelectionActivity finished");
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception exce) {
            exce.printStackTrace();
            LogManager.writeLogError(Constants.sync_table_history_txt + exce.getMessage());
        }
    }

    /*
    ToDo Update Last Sync time into DB table
     */
    private void updatingSyncTime() {
        if (!Constants.syncHistoryTableExist()) {
            try {
                Constants.createSyncDatabase(SyncSelectionActivity.this);  // create sync history table
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            /*String syncTime = Constants.getSyncHistoryddmmyyyyTime();
            for (int incReq = 0; incReq < alAssignColl.size(); incReq++) {
                String colName = alAssignColl.get(incReq);
                if (colName.contains("?$")) {
                    String splitCollName[] = colName.split("\\?");
                    colName = splitCollName[0];
                }
                Constants.events.updateStatus(Constants.SYNC_TABLE,
                        colName, Constants.timeStamp, syncTime
                );
            }*/
           // Constants.updateSyncTime(alAssignColl,this,syncHistoryType,refguid.toString().toUpperCase());
        } catch (Exception exce) {
            exce.printStackTrace();
            LogManager.writeLogError(Constants.sync_table_history_txt + exce.getMessage());
        }
    }

    private void onSyncHist() {
        Intent intent = new Intent(this, SyncHistoryActivity.class);
        startActivity(intent);
    }

    private void onSyncHistInfo() {
        Intent intent = new Intent(this, SyncHistoryInfoActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_back:
                onBackPressed();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        isClickable=false;
        String type = bundle != null ? bundle.getString(Constants.BUNDLE_RESOURCE_PATH) : "";
        Log.d("responseSuccess", "responseSuccess: " + type);
        if (!isBatchReqs) {
            switch (type) {
                case Constants.RouteSchedules:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onRequestSuccess(Operation.Update.getValue(), "");
                        }
                    });
                    break;
                case Constants.CollectionPlan:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onRequestSuccess(Operation.Update.getValue(), "");
                        }
                    });
                    break;
            }
            isBatchReqs = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isClickable = false;
        mTrafficSpeedMeasurer.registerListener(mStreamSpeedListener);

    }

    @Override
    public void responseFailed(final ODataRequestExecution request, String s, Bundle bundle) {
        isClickable=false;
        Log.d("SyncError", "responseFailed: " + s);
        if (!isBatchReqs) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TraceLog.scoped(this).d(Constants.RequestFailed);
                    if (request != null && request.getResponse() != null) {
                        ODataPayload payload = ((ODataResponseSingle) request.getResponse()).getPayload();
                        if (payload != null && payload instanceof ODataError) {
                            ODataError oError = (ODataError) payload;
                            TraceLog.d(Constants.RequestFailed_status_message + oError.getMessage());
                            try {
                                ODataRequestParamSingle oDataResponseSingle = (ODataRequestParamSingleDefaultImpl) request.getRequest();
                                ODataEntity oDataEntity = (ODataEntity) oDataResponseSingle.getPayload();
                                Constants.Entity_Set.add(oDataEntity.getResourcePath());
                            } catch (Exception e3) {
                                e3.printStackTrace();
                            }
                            LogManager.writeLogError(Constants.Error + " :" + oError.getMessage());
                            Constants.AL_ERROR_MSG.add(oError.getMessage());
                            onRequestError(Operation.Update.getValue(), new OnlineODataStoreException(oError.getMessage()));
                            return;
                        }
                    }
                    onRequestError(Operation.Update.getValue(), null);
                }
            });
            isBatchReqs = true;
        }
    }

    public static class ArrayComarator implements Comparator<String[]> {

        @Override
        public int compare(String s1[], String s2[]) {
            BigInteger i1 = null;
            BigInteger i2 = null;
            if (s1!=null) {
                try {
                    i1 = new BigInteger(s1[0]);
                } catch (NumberFormatException e) {
                }
            }

            if (s2!=null) {
                try {
                    i2 = new BigInteger(s2[0]);
                } catch (NumberFormatException e) {
                }
            }

            if (i1 != null && i2 != null) {
                return i1.compareTo(i2);
            } else {
                if (s1 != null&&s2!=null) {
                    return s1[0].compareTo(s2[0]);
                }
            }
            return 0;
        }

    }

   /* public class LoadingData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(SyncSelectionActivity.this, R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
            syncProgDialog.setCancelable(true);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();

            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    SyncSelectionActivity.this, R.style.MyTheme);
                            builder.setMessage(R.string.do_want_cancel_sync)
                                    .setCancelable(false)
                                    .setPositiveButton(
                                            R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {
                                                    dialogCancelled = true;
                                                    onBackPressed();
                                                }
                                            })
                                    .setNegativeButton(
                                            R.string.no,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {

                                                    displayProgressDialog();

                                                }
                                            });
                            builder.show();
                        }
                    });
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (!OfflineManager.isOfflineStoreOpen()) {
                try {
                    OfflineManager.openOfflineStore(SyncSelectionActivity.this, SyncSelectionActivity.this);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
            } else {
                Constants.isStoreClosed = false;
                assignCollToArrayList();

                try {
                    OfflineManager.refreshStoreSync(getApplicationContext(), SyncSelectionActivity.this, Constants.All, concatCollectionStr);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }*/

    /**
     * This adapter show arrange icons and text in grid view manner.
     */
    public class SyncAdapter extends BaseAdapter {
        Context mContext;

        SyncAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            int counttemp = 0;
            for (int OriginalStatu : OriginalStatus) {
                if (OriginalStatu == 1) {
                    counttemp++;
                }
            }
            return counttemp;
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int iconposition = TempStatus[position];
            View view;
            if (convertView == null) {
                LayoutInflater li = getLayoutInflater();
                view = li.inflate(R.layout.retailer_menu_inside, null);
                view.requestFocus();
                TextView tvIconName = (TextView) view.findViewById(R.id.icon_text);
                tvIconName.setTextColor(getResources().getColor(R.color.icon_text_blue));
                tvIconName.setText(iconName[iconposition]);
                ImageView ivIcon = (ImageView) view.findViewById(R.id.ib_must_sell);
                if (iconposition == 0) {
                    ivIcon.setImageResource(R.drawable.ic_sync_black_24dp);
                    ivIcon.setColorFilter(ContextCompat.getColor(SyncSelectionActivity.this, R.color.secondaryColor), android.graphics.PorterDuff.Mode.SRC_IN);
                    ivIcon.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            syncHistoryType = Constants.Sync_All;
                            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                                if (Constants.isPullDownSync||Constants.iSAutoSync||Constants.isBackGroundSync) {
                                    if (Constants.iSAutoSync){
                                        showAlert(getString(R.string.alert_auto_sync_is_progress));
                                    }else{
                                        showAlert(getString(R.string.alert_backgrounf_sync_is_progress));
                                    }
                                }else{
                                    if(!isClickable) {
                                        isClickable = true;
                                        onAllSync();
                                    }
                                }
                            }else{
                                showAlert(getString(R.string.no_network_conn));
                            }

                        }
                    });
                } else if (iconposition == 1) {
                    ivIcon.setImageResource(R.drawable.ic_sync_black_24dp);
                    ivIcon.setColorFilter(ContextCompat.getColor(SyncSelectionActivity.this, R.color.secondaryColor), android.graphics.PorterDuff.Mode.SRC_IN);
                    ivIcon.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            syncHistoryType = Constants.DownLoad;
                            if (!isClickable) {
                                isClickable = true;
                                onFreshSync();
                            }
                        }
                    });
                } else if (iconposition == 2) {
                    ivIcon.setImageResource(R.drawable.ic_sync_black_24dp);
                    ivIcon.setColorFilter(ContextCompat.getColor(SyncSelectionActivity.this, R.color.secondaryColor), android.graphics.PorterDuff.Mode.SRC_IN);
                    ivIcon.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            syncHistoryType = Constants.UpLoad;
                            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                                if (!isClickable) {
                                    isClickable = true;
                                    onUpdateSync();
                                }
                            } else {
                                showAlert(getString(R.string.no_network_conn));
                            }
                        }
                    });
                } else if (iconposition == 3) {
                    ivIcon.setImageResource(R.drawable.ic_history_black_24dp);
                    ivIcon.setColorFilter(ContextCompat.getColor(SyncSelectionActivity.this, R.color.secondaryColor), android.graphics.PorterDuff.Mode.SRC_IN);
                    ivIcon.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
//                            onSyncHist();
                            if (!isClickable) {
                                isClickable = true;
                                onSyncHistInfo();
                            }
                        }
                    });
                }
                view.setId(position);
            } else {
                view = convertView;
            }
            return view;
        }

    }

   

    public class AsyncPostOfflineData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(SyncSelectionActivity.this, R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.updating_data_plz_wait));
            syncProgDialog.setCancelable(false);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();
            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    SyncSelectionActivity.this, R.style.MyTheme);
                            builder.setMessage(R.string.do_want_cancel_sync)
                                    .setCancelable(false)
                                    .setPositiveButton(
                                            R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {
                                                    dialogCancelled = true;
                                                    if(refguid==null){
                                                        refguid = GUID.newRandom();
                                                    }
                                                    Constants.updateStartSyncTime(SyncSelectionActivity.this,Constants.upload_cancel_sync,Constants.EndSync,refguid.toString().toUpperCase());
                                                    onBackPressed();
                                                }
                                            })
                                    .setNegativeButton(
                                            R.string.no,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {

                                                    displayProgressDialog();

                                                }
                                            });
                            builder.show();
                        }
                    });


        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                concatFlushCollStr="";
                for (int incVal = 0; incVal < alFlushColl.size(); incVal++) {
                    if (incVal == 0 && incVal == alFlushColl.size() - 1) {
                        concatFlushCollStr = concatFlushCollStr + alFlushColl.get(incVal);
                    } else if (incVal == 0) {
                        concatFlushCollStr = concatFlushCollStr + alFlushColl.get(incVal) + ", ";
                    } else if (incVal == alFlushColl.size() - 1) {
                        concatFlushCollStr = concatFlushCollStr + alFlushColl.get(incVal);
                    } else {
                        concatFlushCollStr = concatFlushCollStr + alFlushColl.get(incVal) + ", ";
                    }
                }

                try {
                    if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                        try {
                            dialogCancelled = false;
                            OfflineManager.flushQueuedRequests(SyncSelectionActivity.this, concatFlushCollStr);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (ODataException e) {
                    e.printStackTrace();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(syncProgDialog!=null && syncProgDialog.isShowing()){
            syncProgDialog.dismiss();
        }
        mTrafficSpeedMeasurer.stopMeasuring();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
    }

    private ITrafficSpeedListener mStreamSpeedListener = new ITrafficSpeedListener() {

        @Override
        public void onTrafficSpeedMeasured(final double upStream, final double downStream) {
            if (SyncSelectionActivity.this != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String upStreamSpeed = Utils.parseSpeed(upStream, SHOW_SPEED_IN_BITS);
                        String downStreamSpeed = Utils.parseSpeed(downStream, SHOW_SPEED_IN_BITS);
                        if (upStream <= 0 || downStream <= 0)
                            networkErrorCount++;
                        else
                            networkErrorCount = 0;

                        if ((upStream != 0 && upStream < 1) || (downStream != 0 && downStream < 1))
                            networkError++;
                        else
                            networkError = 0;

                        if (networkErrorCount >= 3) {
                            networkErrorCount = 0;
                            isNetwrokErrAlert = true;
                            isMonitoringStopped = false;
                            mTrafficSpeedMeasurer.stopMeasuring();
                            mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
                            closingProgressDialog();

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    UtilConstants.dialogBoxWithCallBack(SyncSelectionActivity.this, "", "Sync can't perform due to network drop", getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
                                        @Override
                                        public void clickedStatus(boolean b) {
                                            if (b) {
                                                isNetwrokErrAlert = false;
                                                if(refguid==null){
                                                    refguid = GUID.newRandom();
                                                }
                                                if(syncHistoryType.equalsIgnoreCase(Constants.UpLoad)){
                                                    Constants.updateStartSyncTime(SyncSelectionActivity.this,Constants.upload_net_sync,Constants.EndSync,refguid.toString().toUpperCase());
                                                }else if(syncHistoryType.equalsIgnoreCase(Constants.Sync_All)){
                                                    Constants.updateStartSyncTime(SyncSelectionActivity.this,Constants.download_all_net_sync,Constants.EndSync,refguid.toString().toUpperCase());
                                                }
                                                onBackPressed();
                                            } else {
                                                isNetwrokErrAlert = false;
                                            }
                                        }
                                    });
                                }
                            });


                        } else if (networkError >= 3) {
                            networkError = 0;
                            isNetwrokErrAlert = true;
                            isMonitoringStopped = false;
                            mTrafficSpeedMeasurer.stopMeasuring();
                            mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
                            closingProgressDialog();

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    UtilConstants.dialogBoxWithCallBack(SyncSelectionActivity.this, "", "Sync can't perform due to network drop", getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
                                        @Override
                                        public void clickedStatus(boolean b) {
                                            if (b) {
                                                isNetwrokErrAlert = false;
                                                if(refguid==null){
                                                    refguid = GUID.newRandom();
                                                }
                                                if(syncHistoryType.equalsIgnoreCase(Constants.UpLoad)){
                                                    Constants.updateStartSyncTime(SyncSelectionActivity.this,Constants.upload_net_sync,Constants.EndSync,refguid.toString().toUpperCase());
                                                }else if(syncHistoryType.equalsIgnoreCase(Constants.Sync_All)){
                                                    Constants.updateStartSyncTime(SyncSelectionActivity.this,Constants.download_all_net_sync,Constants.EndSync,refguid.toString().toUpperCase());
                                                }
                                                onBackPressed();
                                            } else {
                                                isNetwrokErrAlert = false;
                                            }
                                        }
                                    });
                                }
                            });


                        }

                        Log.d("Network_Bandwidth", "Values" + upStreamSpeed + "--" + downStreamSpeed);
                    }
                });
            }
        }
    };

    private static boolean isActiveNetwork(Context context){
        return isConnected(context);
    }
    private static boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isWiFi = false;
        boolean isMobile = false;
        boolean isConnected = false;
        if (activeNetwork != null) {
            isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
            isMobile = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
            isConnected = activeNetwork.isConnectedOrConnecting();
        }
        if (isConnected) {
            if (isWiFi) {
                return isConnectedToThisServer();
            }
            if (isMobile) {
                return isConnectedToThisServer();
            }
        } else {
            return false;
        }
        return false;
    }
    private static boolean isConnectedToThisServer() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
    public interface OnNetworkInfoListener{
        void onNetworkFailureListener(boolean isFailed);
    }
    private static boolean isNetworkStopped;
    public  void checkNetwork(final Context context, final OnNetworkInfoListener networkInfoListener, boolean isInterupted, final int delayInSec){
        if (!isInterupted) {
            isNetworkStopped=false;
            networkThread  = new Thread(new Runnable() {
                @Override
                public void run() {
                    check(context, networkInfoListener,delayInSec);
                }
            });
            networkThread.start();
        }else{
            isNetworkStopped =true;
        }
    }
    private  void check(Context context,OnNetworkInfoListener networkInfoListener, int delayInSec){
        if (!isNetworkStopped) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean isError = isActiveNetwork(context);
            if (!isError) {
                networkErrorCount++;
                if(networkErrorCount>=3){
                    networkErrorCount=0;
                    isNetwrokErrAlert=true;
                    isMonitoringStopped = false;
                    mTrafficSpeedMeasurer.stopMeasuring();
                    mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
                    closingProgressDialog();
                    if (SyncSelectionActivity.this != null) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                UtilConstants.dialogBoxWithCallBack(SyncSelectionActivity.this, "", "Sync can't perform due to network drop", getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
                                    @Override
                                    public void clickedStatus(boolean b) {
                                        if (b) {
                                            isNetwrokErrAlert = false;
                                            if(refguid==null){
                                                refguid = GUID.newRandom();
                                            }
                                            if(syncHistoryType.equalsIgnoreCase(Constants.UpLoad)){
                                                Constants.updateStartSyncTime(SyncSelectionActivity.this,Constants.upload_net_sync,Constants.EndSync,refguid.toString().toUpperCase());
                                            }else if(syncHistoryType.equalsIgnoreCase(Constants.Sync_All)){
                                                Constants.updateStartSyncTime(SyncSelectionActivity.this,Constants.download_all_net_sync,Constants.EndSync,refguid.toString().toUpperCase());
                                            }
                                            onBackPressed();
                                        } else {
                                            isNetwrokErrAlert = false;
                                        }
                                    }
                                });

                            }
                        });
                    }

                }

                Log.e("CHECKING NETWORK", "NETWORK ERROR");

                if (networkInfoListener != null && isMonitoringStopped) {
                    networkInfoListener.onNetworkFailureListener(true);
                    check(context, networkInfoListener,delayInSec);
                }
            } else {
                if (networkInfoListener != null && isMonitoringStopped) {
                    Log.e("CHECKING NETWORK", "NETWORK ACTIVE");
                    networkInfoListener.onNetworkFailureListener(false);
                    check(context, networkInfoListener, delayInSec);
                }
            }
        }
    }



    public class AsyncPostDataValutData extends AsyncTask<Void, Boolean, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(SyncSelectionActivity.this, R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.updating_data_plz_wait));
            syncProgDialog.setCancelable(false);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();
            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    SyncSelectionActivity.this, R.style.MyTheme);
                            builder.setMessage(R.string.do_want_cancel_sync)
                                    .setCancelable(false)
                                    .setPositiveButton(
                                            R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {
                                                    dialogCancelled = true;
                                                    if(refguid==null){
                                                        refguid = GUID.newRandom();
                                                    }
                                                    Constants.updateStartSyncTime(SyncSelectionActivity.this,Constants.upload_cancel_sync,Constants.EndSync,refguid.toString().toUpperCase());
                                                    onBackPressed();
                                                }
                                            })
                                    .setNegativeButton(
                                            R.string.no,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {

                                                    displayProgressDialog();

                                                }
                                            });
                            builder.show();
                        }
                    });


        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean isStoreOpened = false;
            try {
                isStoreOpened = OnlineManager.openOnlineStore(SyncSelectionActivity.this, true);
            } catch (OnlineODataStoreException e) {
                e.printStackTrace();
                LogManager.writeLogDebug("Upload Sync Store Error :" + e.getLocalizedMessage());
            }
            mBoolIsReqResAval = true;
            if (isStoreOpened) {

                if (mIntPendingCollVal > 0) {
                    for (int k = 0; k < invKeyValues.length; k++) {

                        while (!mBoolIsReqResAval) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        if (mBoolIsNetWorkNotAval) {
                            break;
                        }
                        mBoolIsReqResAval = false;
                        String store = null;
                        try {
                            store = ConstantsUtils.getFromDataVault(invKeyValues[k][0].toString(),SyncSelectionActivity.this);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }


                        //Fetch object from data vault
                        try {

                            JSONObject fetchJsonHeaderObject = new JSONObject(store);
                            dbHeadTable = new Hashtable();
                            arrtable = new ArrayList<>();
                            if (Constants.writeDebug) {
                                LogManager.writeLogDebug("Upload Sync Collection Name:" + fetchJsonHeaderObject.getString(Constants.entityType));
                            }
                            if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.Collection)) {

//                                if (!alAssignColl.contains(Constants.SFINVOICES)) {
//                                    alAssignColl.add(Constants.SSInvoiceItemDetails);
//                                    alAssignColl.add(Constants.SFINVOICES);
//                                }
//                                if (!alAssignColl.contains(Constants.FinancialPostings)) {
//                                    alAssignColl.add(Constants.FinancialPostings);
//                                    alAssignColl.add(Constants.FinancialPostingItemDetails);
//                                }
                                if (!alAssignColl.contains(Constants.OutstandingInvoices)) {
                                    alAssignColl.add(Constants.OutstandingInvoiceItemDetails);
                                    alAssignColl.add(Constants.OutstandingInvoices);
                                }
                                dbHeadTable = Constants.getCollHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);

                                arrtable = UtilConstants.convertToArrayListMap(itemsString);

                                try {
                                    OnlineManager.createCollectionEntry(dbHeadTable, arrtable, SyncSelectionActivity.this);

                                } catch (OnlineODataStoreException e) {
                                    e.printStackTrace();
                                }

                            } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SecondarySOCreate)) {
                                if (!alAssignColl.contains(Constants.SFINVOICES)) {
                                    alAssignColl.add(Constants.SSInvoiceItemDetails);
                                    alAssignColl.add(Constants.SFINVOICES);
                                }
                                Constants.REPEATABLE_REQUEST_ID="";
                                Constants.REPEATABLE_DATE="";
                                JSONObject dbHeadTable = Constants.getSOHeaderValuesFrmJsonObject(fetchJsonHeaderObject);
                                OnlineManager.createEntity( Constants.REPEATABLE_REQUEST_ID,Constants.REPEATABLE_DATE,dbHeadTable.toString(), Constants.SOs, SyncSelectionActivity.this, SyncSelectionActivity.this);
                            } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SalesOrderDataValt)) {
                                if (!alAssignColl.contains(Constants.SOs)) {
                                    alAssignColl.add(Constants.SOs);
                                    alAssignColl.add(Constants.SOItemDetails);
                                    alAssignColl.add(Constants.SOItems);
                                    alAssignColl.add(Constants.SOTexts);
                                    alAssignColl.add(Constants.SOConditions);
                                }
                                Constants.REPEATABLE_REQUEST_ID="";
                                Constants.REPEATABLE_DATE="";
                                JSONObject dbHeadTable = Constants.getSOsHeaderValueFrmJsonObject1(fetchJsonHeaderObject);
                                OnlineManager.createEntity(Constants.REPEATABLE_REQUEST_ID,Constants.REPEATABLE_DATE,dbHeadTable.toString(), Constants.SOs, SyncSelectionActivity.this, SyncSelectionActivity.this);
                            }  else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SOUpdate)) {
                                if (!alAssignColl.contains(Constants.SOs)) {
                                    alAssignColl.add(Constants.SOs);
                                    alAssignColl.add(Constants.SOItemDetails);
                                    alAssignColl.add(Constants.SOTexts);
                                    alAssignColl.add(Constants.SOItems);
                                    alAssignColl.add(Constants.SOConditions);
                                }
                                dbHeadTable = Constants.getSOCancelHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                String itemsString = fetchJsonHeaderObject.getString(Constants.SalesOrderItems);
                                arrtable = UtilConstants.convertToArrayListMap(itemsString);
                                try {
                                    OnlineManager.cancelSO(dbHeadTable, arrtable, SyncSelectionActivity.this);
                                } catch (OnlineODataStoreException e) {
                                    e.printStackTrace();
                                }
                            } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.Expenses)) {
                                if (!alAssignColl.contains(Constants.Expenses)) {
                                    alAssignColl.add(Constants.ExpenseItemDetails);
                                    alAssignColl.add(Constants.Expenses);
                                    alAssignColl.add(Constants.ExpenseDocuments);
                                }
                                dbHeadTable = Constants.getExpenseHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                                arrtable = UtilConstants.convertToArrayListMap(itemsString);
                                try {
                                    OnlineManager.createDailyExpense(dbHeadTable, arrtable, SyncSelectionActivity.this);
                                } catch (OnlineODataStoreException e) {
                                    e.printStackTrace();
                                }
                            } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.RouteSchedules)) {
                                // preparing entity pending
                                if (!alAssignColl.contains(Constants.RouteSchedulePlans)) {
                                    alAssignColl.add(Constants.RouteSchedules);
                                    alAssignColl.add(Constants.RouteSchedulePlans);
                                }
                                isBatchReqs = false;
                                Constants.REPEATABLE_REQUEST_ID="";
                                Constants.REPEATABLE_DATE="";
                                if (TextUtils.isEmpty(String.valueOf(fetchJsonHeaderObject.get(Constants.IS_UPDATE)))) {
                                    JSONObject dbHeadTable = Constants.getMTPHeaderValuesFrmJsonObject(fetchJsonHeaderObject);
                                    OnlineManager.createEntity(Constants.REPEATABLE_REQUEST_ID,Constants.REPEATABLE_DATE, dbHeadTable.toString(), Constants.RouteSchedules, SyncSelectionActivity.this, SyncSelectionActivity.this);
                                }else {
                                    try {
                                        OnlineManager.batchUpdateMTP(fetchJsonHeaderObject,SyncSelectionActivity.this,SyncSelectionActivity.this);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }  else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.CollectionPlan)) {
                                // preparing entity pending
                                if (!alAssignColl.contains(Constants.CollectionPlan)) {
                                    alAssignColl.add(Constants.CollectionPlan);
                                    alAssignColl.add(Constants.CollectionPlanItem);
                                    alAssignColl.add(Constants.CollectionPlanItemDetails);
                                }
                                isBatchReqs = false;
                                Constants.REPEATABLE_REQUEST_ID="";
                                Constants.REPEATABLE_DATE="";
                                if (TextUtils.isEmpty(fetchJsonHeaderObject.getString(Constants.IS_UPDATE))) {
                                    JSONObject dbHeadTable = Constants.getRTGSHeaderValuesFrmJsonObject(fetchJsonHeaderObject);
                                    OnlineManager.createEntity(Constants.REPEATABLE_REQUEST_ID,Constants.REPEATABLE_DATE, dbHeadTable.toString(), Constants.CollectionPlan, SyncSelectionActivity.this, SyncSelectionActivity.this);
                                }else {
                                    try {
                                        OnlineManager.batchUpdateRTGS(fetchJsonHeaderObject,SyncSelectionActivity.this,SyncSelectionActivity.this);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return isStoreOpened;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (!result) {
                closingProgressDialog();
                if (Constants.ErrorNo == Constants.Network_Error_Code && Constants.ErrorName.equalsIgnoreCase(Constants.NetworkError_Name)) {
                    showAlert(getString(R.string.data_conn_lost_during_sync_error_code, Constants.ErrorNo + ""));
                } else if (Constants.ErrorNo == Constants.UnAuthorized_Error_Code && Constants.ErrorName.equalsIgnoreCase(Constants.NetworkError_Name)) {
                    if(Constants.ErrorNo == Constants.UnAuthorized_Error_Code){
                        String errorMessage = Constants.PasswordExpiredMsg;
                        showAlert(errorMessage);

                    }else{
                        showAlert(getString(R.string.auth_fail_plz_contact_admin, Constants.ErrorNo + ""));
                    }

//                    UtilConstants.showAlert(getString(R.string.auth_fail_plz_contact_admin, Constants.ErrorNo + ""), SyncSelectionActivity.this);
                } else if (Constants.ErrorNo == Constants.Comm_Error_Code) {
                    showAlert(getString(R.string.data_conn_lost_during_sync_error_code, Constants.ErrorNo + ""));
                } else {
                    showAlert(getString(R.string.data_conn_lost_during_sync_error_code, Constants.ErrorNo + ""));
                }
            }
        }
    }

    public class CustomComparator implements Comparator<String> {

        @Override
        public int compare(String s1, String s2) {
            BigInteger i1 = null;
            BigInteger i2 = null;
            try {
                i1 = new BigInteger(s1);
            } catch (NumberFormatException e) {
            }

            try {
                i2 = new BigInteger(s2);
            } catch (NumberFormatException e) {
            }

            if (i1 != null && i2 != null) {
                return i1.compareTo(i2);
            } else {
                return s1.compareTo(s2);
            }
        }

    }
    private void showAlert(String message){
        ConstantsUtils.showAlert(message, SyncSelectionActivity.this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isClickable=false;
                dialog.cancel();
            }
        });
    }

}
