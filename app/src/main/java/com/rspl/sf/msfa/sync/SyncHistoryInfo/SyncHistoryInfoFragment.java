package com.rspl.sf.msfa.sync.SyncHistoryInfo;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.mutils.sync.SyncHistoryModel;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.rspl.sf.msfa.BuildConfig;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.SPGeo.database.DatabaseHelperGeo;
import com.rspl.sf.msfa.asyncTask.RefreshAsyncTask;
import com.rspl.sf.msfa.asyncTask.RefreshGeoAsyncTask;
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
import com.rspl.sf.msfa.sync.AllSyncAsyncTask;
import com.rspl.sf.msfa.sync.SyncHist;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A simple {@link Fragment} subclass.
 */
public class SyncHistoryInfoFragment extends Fragment implements UIListener, View.OnClickListener, CollectionSyncInterface,OnlineODataInterface {

    ArrayList<String> tempCPList = new ArrayList<>();
    int updateCancelSOCount = 0;
    int cancelSoPos = 0;
    int mIntPendingCollVal = 0;
    Hashtable dbHeadTable;
    ArrayList<HashMap<String, String>> arrtable;
    String[][] invKeyValues;
    ArrayList<String> alAssignColl = new ArrayList<>();
    ArrayList<String> alFlushColl = new ArrayList<>();
    String concatCollectionStr = "";
    String concatFlushCollStr = "";
    String endPointURL = "";
    String appConnID = "";
    String syncType = "";
    boolean onlineStoreOpen = false;
    PendingCountAdapter pendingCountAdapter;
    private RecyclerView recycler_view_His, rvSyncTime;
    private int pendingCount = 0;
    private boolean mBoolIsNetWorkNotAval = false;
    private boolean mBoolIsReqResAval = false;
    private boolean isBatchReqs = false;
    private boolean tokenFlag = false;
    private int penReqCount = 0;
    private ProgressDialog syncProgDialog = null;
    private boolean dialogCancelled = false;
    private int mError = 0;
    private List<PendingCountBean> pendingCountBeanList = new ArrayList<>();
    private ArrayList<SyncHistoryModel> syncHistoryModelList = new ArrayList<>();
    private ImageView ivUploadDownload, ivSyncAll;
    private TextView tvPendingCount, tvPendingStatus;
    private NestedScrollView nestedScroll;
    private LinearLayout cvUpdatePending;
    private SimpleRecyclerViewAdapter<SyncHistoryModel> simpleUpdateHistoryAdapter;
    int[] cancelSOCount = new int[0];
    private boolean isClickable = false;
    private DatabaseHelperGeo databaseHelper = null;
    private ReentrantLock reentrantLock = null;
    private int responseCount = 0,sqlDbCount = 0;
    private GUID refguid =null;
    private TrafficSpeedMeasurer mTrafficSpeedMeasurer;
    private static final boolean SHOW_SPEED_IN_BITS = false;
    private int networkErrorCount=0,networkError=0;
    Thread networkThread;
    private boolean isMonitoringStopped = false;


    public SyncHistoryInfoFragment() {
        // Required empty public constructor
    }

    private static int getPendingListSize(Context mContext) {
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

    public ArrayList<Object> getPendingCollList(Context mContext, boolean isFromAutoSync) {
        ArrayList<Object> objectsArrayList = new ArrayList<>();
        int mIntPendingCollVal = 0;
        String[][] invKeyValues = null;
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        invKeyValues = new String[getPendingListSize(mContext)][2];
        if (!isFromAutoSync) {
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
        if (!isFromAutoSync) {
            set = sharedPreferences.getStringSet(Constants.MTPDataValt, null);
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
                while (itr.hasNext()) {
                    invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                    invKeyValues[mIntPendingCollVal][1] = Constants.MTPDataValt;
                    mIntPendingCollVal++;
                }
            }
        }
        if (!isFromAutoSync) {
            set = sharedPreferences.getStringSet(Constants.RTGSDataValt, null);
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
                while (itr.hasNext()) {
                    invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                    invKeyValues[mIntPendingCollVal][1] = Constants.RTGSDataValt;
                    mIntPendingCollVal++;
                }
            }
        }
        int cancelSOSize = 0;
        int i = 0;
        int[] cancelSOCount = new int[0];
        if (!isFromAutoSync) {
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
                        store = ConstantsUtils.getFromDataVault(invKeyValues[mIntPendingCollVal][0].toString(),getActivity());
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
        if (!isFromAutoSync) {
            set = sharedPreferences.getStringSet(Constants.SOUpdate, null);
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
//            cancelSOCount = new int[set.size()];
                while (itr.hasNext()) {
                    invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                    invKeyValues[mIntPendingCollVal][1] = Constants.SOUpdate;

                    String store = null;
                    try {
                        store = ConstantsUtils.getFromDataVault(invKeyValues[mIntPendingCollVal][0].toString(),getActivity());
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
        if (!isFromAutoSync) {
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
            Arrays.sort(invKeyValues, new ArrayComarator());
            objectsArrayList.add(mIntPendingCollVal);
            objectsArrayList.add(invKeyValues);
            objectsArrayList.add(cancelSOCount);
        }

        return objectsArrayList;

    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        String type = bundle != null ? bundle.getString(Constants.BUNDLE_RESOURCE_PATH) : "";
        Log.d("responseSuccess", "responseSuccess: " + type);
        if (!isBatchReqs) {
            switch (type) {
                case Constants.RouteSchedules:
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onRequestSuccess(Operation.Update.getValue(), "");
                        }
                    });
                    break;
                case Constants.CollectionPlan:
                    getActivity().runOnUiThread(new Runnable() {
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
    public void responseFailed(final ODataRequestExecution request, String s, Bundle bundle) {
        Log.d("SyncError", "responseFailed: " + s);
        if (!isBatchReqs) {
            getActivity().runOnUiThread(new Runnable() {
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
                            onRequestError(Operation.Update.getValue(), new com.rspl.sf.msfa.store.OnlineODataStoreException(oError.getMessage()));
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
            try {
                i1 = new BigInteger(s1[0]);
            } catch (NumberFormatException e) {
            }

            try {
                i2 = new BigInteger(s2[0]);
            } catch (NumberFormatException e) {
            }

            if (i1 != null && i2 != null) {
                return i1.compareTo(i2);
            } else {
                return s1[0].compareTo(s2[0]);
            }
        }

    }

    public static ArrayList<String> getRefreshList(Context context) {
        ArrayList<String> alAssignColl = new ArrayList<>();
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
            if (sharedPreferences.getInt(Constants.CURRENT_VERSION_CODE, 0) >= 21) {
                if (OfflineManager.getVisitStatusForCustomer(Constants.SPGeos + Constants.isLocalFilterQry)) {
                    alAssignColl.add(Constants.SPGeos);
                }
            }

            if (sharedPreferences.getInt(Constants.CURRENT_VERSION_CODE, 0) >= 21 && Arrays.asList(Constants.getDefinigReq(context)).contains(Constants.SyncHistorys)) {
                if (OfflineManager.getVisitStatusForCustomer(Constants.SyncHistorys + Constants.isLocalFilterQry)) {
                    alAssignColl.add(Constants.SyncHistorys);
                }
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog(e.getMessage());
        }
        return alAssignColl;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sync_history_info, container, false);
        recycler_view_His = view.findViewById(R.id.recycler_view_His);
        rvSyncTime = view.findViewById(R.id.rvSyncTime);
        ivUploadDownload = view.findViewById(R.id.ivUploadDownload);
        tvPendingStatus = view.findViewById(R.id.tvPendingStatus);
        cvUpdatePending = view.findViewById(R.id.cvUpdatePending);
        tvPendingCount = view.findViewById(R.id.tvPendingCount);
        nestedScroll = view.findViewById(R.id.nestedScroll);
        ivSyncAll = view.findViewById(R.id.ivSyncAll);
        ivUploadDownload.setOnClickListener(SyncHistoryInfoFragment.this);
        ivSyncAll.setOnClickListener(SyncHistoryInfoFragment.this);
        recycler_view_His.setHasFixedSize(false);
        rvSyncTime.setHasFixedSize(false);
        databaseHelper = DatabaseHelperGeo.getInstance(this.getActivity());
        sqlDbCount = databaseHelper.getSqlLocationDataCount();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        ViewCompat.setNestedScrollingEnabled(recycler_view_His, false);
        recycler_view_His.setLayoutManager(linearLayoutManager);
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        ViewCompat.setNestedScrollingEnabled(rvSyncTime, false);
        rvSyncTime.setLayoutManager(linearLayoutManager);
        pendingCountAdapter = new PendingCountAdapter(pendingCountBeanList, getActivity(), this);
        recycler_view_His.setAdapter(pendingCountAdapter);
        setSyncTimeAdapter();
//        pendingCountBeanList = getRecordInfo();
        initRecyclerView();
        ConstantsUtils.focusOnView(nestedScroll);
        mTrafficSpeedMeasurer = new TrafficSpeedMeasurer(TrafficSpeedMeasurer.TrafficType.ALL);

        return view;
    }

    private void setSyncTimeAdapter() {
        simpleUpdateHistoryAdapter = new SimpleRecyclerViewAdapter<SyncHistoryModel>(getActivity(), R.layout.item_history_time, new AdapterInterface<SyncHistoryModel>() {
            @Override
            public void onItemClick(SyncHistoryModel o, View view, int i) {

            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
                return new HistoryTimeVH(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, SyncHistoryModel o) {
                ((HistoryTimeVH) viewHolder).tvEntityName.setText(o.getCollections());
                ((HistoryTimeVH) viewHolder).tvSyncTime.setText(o.getTimeStamp());
            }
        }, null, null);
        rvSyncTime.setAdapter(simpleUpdateHistoryAdapter);
    }

    private void initRecyclerView() {
        pendingCountBeanList.clear();
        pendingCountBeanList.addAll(getRecordInfo(getActivity()));
        pendingCountAdapter.notifyDataSetChanged();
        simpleUpdateHistoryAdapter.refreshAdapter(syncHistoryModelList);
        tvPendingCount.setText(String.valueOf(pendingCount));
        if (pendingCount > 0) {
            cvUpdatePending.setVisibility(View.VISIBLE);
            tvPendingStatus.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.RejectedColor));
        } else {
            cvUpdatePending.setVisibility(View.GONE);
            tvPendingStatus.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.ApprovedColor));
        }
        if(sqlDbCount>0)
            moveDataSqlToOfflineDB();
    }

    private void moveDataSqlToOfflineDB() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (reentrantLock==null){
                        reentrantLock = new ReentrantLock();
                    }
                    try {
                        Log.e("Main Menu REENTRANT:","LOCKED");
                        reentrantLock.lock();
                        Constants.getDataFromSqliteDB(getActivity(), new UIListener() {
                            @Override
                            public void onRequestError(int i, Exception e) {
                                responseCount++;
                                if(responseCount>=sqlDbCount)
                                    checkAndRefresh();
                            }

                            @Override
                            public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                                responseCount++;
                                if(responseCount>=sqlDbCount)
                                    checkAndRefresh();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Main Menu EXCEPTION","ANR EXCEPTION OCCURRED");
                    }finally {
                        if (reentrantLock!=null&&reentrantLock.isHeldByCurrentThread())reentrantLock.unlock();
                        Log.e("Main Menu REENTRANT:","UNLOCKED FINALLY");
                    }
                }
            }).start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void checkAndRefresh() {
        if(pendingCountBeanList!=null && pendingCountBeanList.size()>0)
            for(PendingCountBean countBeanData : pendingCountBeanList)
                countBeanData.setShowProgress(false);

        pendingCountAdapter.notifyDataSetChanged();

    }

    private List<PendingCountBean> getRecordInfo(Context mContext) {
        pendingCount = 0;
        syncHistoryModelList.clear();
        syncHistoryModelList.addAll(getAllRecords());
        try {
            Collections.sort(syncHistoryModelList, new Comparator<SyncHistoryModel>() {
                public int compare(SyncHistoryModel one, SyncHistoryModel other) {
                    return one.getCollections().compareTo(other.getCollections());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        PendingCountBean countBean = null;
        int count = 0;
        List<PendingCountBean> pendingCountBeans = new ArrayList();
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        ArrayList<String> alTempList = new ArrayList<>();
        List<PendingCountBean> temppendingList = new ArrayList();
        List<PendingCountBean> tempNonpendingList = new ArrayList();
        ArrayList<String> alCollectionList = null;
        for (int k = 0; k < syncHistoryModelList.size(); k++) {
            alCollectionList = new ArrayList<>();
            SyncHistoryModel historyModel = syncHistoryModelList.get(k);
            try {
                if ((historyModel.getCollections().equalsIgnoreCase(Constants.Attendances)) && !alTempList.contains(Constants.Attendances)) {
                    if (sharedPreferences.getString(Constants.isStartCloseEnabled, "").equalsIgnoreCase(Constants.isStartCloseTcode)) {
                        count = 0;
                        alTempList.add("Attendances");
                        alCollectionList.add(Constants.Attendances);
                        countBean = new PendingCountBean();
                        count = OfflineManager.getPendingCount(Constants.Attendances + "?$filter= sap.islocal() ");
                        if (count > 0) {
                            pendingCount = pendingCount + count;
                            countBean.setCollection(Constants.Attendances);
                            countBean.setCount(count);
                            countBean.setSyncTime(historyModel.getTimeStamp());
                            countBean.setAlCollectionList(alCollectionList);
                            temppendingList.add(countBean);
                        } else {
                            countBean.setCollection(Constants.Attendances);
                            countBean.setCount(count);
                            countBean.setSyncTime(historyModel.getTimeStamp());
                            countBean.setAlCollectionList(alCollectionList);
                            tempNonpendingList.add(countBean);
                        }
                    }


                }else if ((historyModel.getCollections().equalsIgnoreCase("CollectionPlan") || historyModel.getCollections().equalsIgnoreCase("CollectionPlanItem")|| historyModel.getCollections().equalsIgnoreCase("CollectionPlanItemDetails")) && !alTempList.contains("RTGS")) {
                    if (sharedPreferences.getString(Constants.isRTGSEnabled, "").equalsIgnoreCase(Constants.isRTGSTcode) || sharedPreferences.getString(Constants.isRTGSSubOrdinateEnabled, "").equalsIgnoreCase(Constants.isRTGSSubOrdinateTcode)) {
                        count = 0;

                        alTempList.add("RTGS");
                        alCollectionList.addAll(ConstantsUtils.getRGTCollection());
                        countBean = new PendingCountBean();
                        set = sharedPreferences.getStringSet(Constants.RTGSDataValt, null);
                        if (set != null && !set.isEmpty()) {
                            count = set.size();
                            pendingCount = pendingCount + count;
                            countBean.setCollection("RTGS");
                            countBean.setCount(count);
                            countBean.setSyncTime(historyModel.getTimeStamp());
                            countBean.setAlCollectionList(alCollectionList);
                            temppendingList.add(countBean);
                        } else {
                            countBean.setCollection("RTGS");
                            countBean.setCount(count);
                            countBean.setSyncTime(historyModel.getTimeStamp());
                            countBean.setAlCollectionList(alCollectionList);
                            tempNonpendingList.add(countBean);
                        }

                    }

                }else if ((historyModel.getCollections().equalsIgnoreCase(Constants.CustomerCreditLimits)) && !alTempList.contains("Credit Limits")) {
                    count = 0;

                    alTempList.add("Credit Limits");
                    alCollectionList.add(Constants.CustomerCreditLimits);
                    countBean = new PendingCountBean();
                    countBean.setCollection("Credit Limits");
                    countBean.setCount(count);
                    countBean.setSyncTime(historyModel.getTimeStamp());
                    countBean.setAlCollectionList(alCollectionList);
                    tempNonpendingList.add(countBean);
                } else if ((historyModel.getCollections().equalsIgnoreCase(Constants.UserProfileAuthSet)) && !alTempList.contains("Authorization")) {
                    count = 0;

                    alTempList.add("Authorization");
                    alCollectionList.add(Constants.UserProfileAuthSet);

                    countBean = new PendingCountBean();
                    countBean.setCollection("Authorization");
                    countBean.setCount(count);
                    countBean.setSyncTime(historyModel.getTimeStamp());
                    countBean.setAlCollectionList(alCollectionList);
                    tempNonpendingList.add(countBean);
                } else if (( historyModel.getCollections().equalsIgnoreCase(Constants.RouteSchedulePlans)|| historyModel.getCollections().equalsIgnoreCase(Constants.RouteSchedules)) && !alTempList.contains("MTP")) {
                    if (sharedPreferences.getString(Constants.isRouteEnabled, "").equalsIgnoreCase(Constants.isRoutePlaneTcode) || sharedPreferences.getString(Constants.isMTPEnabled, "").equalsIgnoreCase(Constants.isMTPTcode)) {
                        count = 0;

                        alTempList.add("MTP");
                        alCollectionList.addAll(ConstantsUtils.getMTPCollection());
                        countBean = new PendingCountBean();
                        set = sharedPreferences.getStringSet(Constants.MTPDataValt, null);
                        if (set != null && !set.isEmpty()) {
                            count = set.size();
                            pendingCount = pendingCount + count;
                        }

                        if (count > 0) {
                            countBean.setCollection("MTP");
                            countBean.setCount(count);
                            countBean.setSyncTime(historyModel.getTimeStamp());
                            countBean.setAlCollectionList(alCollectionList);
                            temppendingList.add(countBean);
                        } else {
                            countBean.setCollection("MTP");
                            countBean.setCount(count);
                            countBean.setSyncTime(historyModel.getTimeStamp());
                            countBean.setAlCollectionList(alCollectionList);
                            tempNonpendingList.add(countBean);
                        }

                    }

                } else if ((historyModel.getCollections().equalsIgnoreCase(Constants.SOs) || historyModel.getCollections().equalsIgnoreCase(Constants.SOItemDetails)|| historyModel.getCollections().equalsIgnoreCase(Constants.SOTexts)|| historyModel.getCollections().equalsIgnoreCase(Constants.SOItems)|| historyModel.getCollections().equalsIgnoreCase(Constants.SOConditions)) && !alTempList.contains("Sales Order")) {
                    if (sharedPreferences.getString(Constants.isSOListEnabled, "").equalsIgnoreCase(Constants.isSOListTcode)) {
                        count = 0;

                        alTempList.add("Sales Order");
                        alCollectionList.addAll(ConstantsUtils.getSaleOrderCollection());

                        set = sharedPreferences.getStringSet(Constants.SalesOrderDataValt, null);
                        if (set != null && !set.isEmpty()) {
                            count = set.size();
                            pendingCount = pendingCount + count;
                        }
                        countBean = new PendingCountBean();
                        if (count > 0) {
                            countBean.setCollection("Sales Order");
                            countBean.setCount(count);
                            countBean.setSyncTime(historyModel.getTimeStamp());
                            countBean.setAlCollectionList(alCollectionList);
                            temppendingList.add(countBean);
                        } else {
                            countBean.setCollection("Sales Order");
                            countBean.setCount(count);
                            countBean.setSyncTime(historyModel.getTimeStamp());
                            countBean.setAlCollectionList(alCollectionList);
                            tempNonpendingList.add(countBean);
                        }
                    }


                } else if ((historyModel.getCollections().equalsIgnoreCase("Customers") || historyModel.getCollections().equalsIgnoreCase("CustomerPartnerFunctions")|| historyModel.getCollections().equalsIgnoreCase("CustomerSalesAreas")|| historyModel.getCollections().equalsIgnoreCase("UserCustomers")) && !alTempList.contains("Customers")) {
                    if (sharedPreferences.getString(Constants.isCustomerListEnabled, "").equalsIgnoreCase(Constants.isCustomerListTcode)) {
                        count = 0;

                        alTempList.add("Customers");
                        alCollectionList.addAll(ConstantsUtils.getCustomerCollection());

                        countBean = new PendingCountBean();
                        countBean.setCollection("Customers");
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        tempNonpendingList.add(countBean);
                    }
                } else if ((historyModel.getCollections().equalsIgnoreCase("Visits") || historyModel.getCollections().equalsIgnoreCase("VisitActivities")) && !alTempList.contains("Visits")) {
                    count = 0;

                    alTempList.add("Visits");
                    alCollectionList.add(Constants.Visits);
                    alCollectionList.add(Constants.VisitActivities);


                    count = OfflineManager.getPendingCount(Constants.Visits + "?$filter= sap.islocal() ") + OfflineManager.getPendingCount(Constants.VisitActivities + "?$filter= sap.islocal() ");
                    countBean = new PendingCountBean();
                    if (count > 0) {
                        pendingCount = pendingCount + count;
                        countBean.setCollection(Constants.Visits);
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        temppendingList.add(countBean);
                    }else {
                        countBean.setCollection(Constants.Visits);
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        tempNonpendingList.add(countBean);
                    }
                } else if ((historyModel.getCollections().equalsIgnoreCase(Constants.InvoiceItemDetails) || (historyModel.getCollections().equalsIgnoreCase(Constants.INVOICES))|| (historyModel.getCollections().equalsIgnoreCase(Constants.InvoiceConditions))|| (historyModel.getCollections().equalsIgnoreCase(Constants.InvoicePartnerFunctions))) && (!alTempList.contains("Invoice"))) {
                    if (sharedPreferences.getString(Constants.isInvHistoryEnabled, "").equalsIgnoreCase(Constants.isInvoiceHistoryTcode)) {
                        count = 0;

                        alTempList.add("Invoice");
                        alCollectionList.addAll(ConstantsUtils.getInvoiceeCollection());
                        countBean = new PendingCountBean();
                        countBean.setCollection("Invoice");
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        tempNonpendingList.add(countBean);
                    }
                } else if (historyModel.getCollections().equalsIgnoreCase(Constants.UserProfileAuthSet) && !alTempList.contains(Constants.UserProfileAuthSet)) {
                    count = 0;

                    alTempList.add("Authorization");
                    alCollectionList.add(Constants.UserProfileAuthSet);
                    countBean = new PendingCountBean();
                    countBean.setCollection("Authorization");
                    countBean.setCount(count);
                    countBean.setSyncTime(historyModel.getTimeStamp());
                    countBean.setAlCollectionList(alCollectionList);
                    tempNonpendingList.add(countBean);
                } else if ((historyModel.getCollections().equalsIgnoreCase(Constants.ValueHelps)) && !alTempList.contains("Value Helps")) {
                    count = 0;

                    alTempList.add("Value Helps");
                    alCollectionList.addAll(ConstantsUtils.getValueHelps());
                    countBean = new PendingCountBean();
                    countBean.setCollection("Value Helps");
                    countBean.setCount(count);
                    countBean.setSyncTime(historyModel.getTimeStamp());
                    countBean.setAlCollectionList(alCollectionList);
                    tempNonpendingList.add(countBean);
                }else if ((historyModel.getCollections().equalsIgnoreCase(Constants.SPChannelEvaluationList) && !alTempList.contains("Dealer Behaviour"))) {
                    if (sharedPreferences.getString(Constants.isDealerBehaviourEnabled, "").equalsIgnoreCase(Constants.isDealerBehaviourTcode)) {
                        count = 0;

                        alTempList.add("Dealer Behaviour");
                        alCollectionList.add(Constants.SPChannelEvaluationList);
                        countBean = new PendingCountBean();
                        countBean.setCollection("Dealer Behaviour");
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        tempNonpendingList.add(countBean);
                    }
                }else if ((historyModel.getCollections().equalsIgnoreCase(Constants.Targets)) && !alTempList.contains("Targets")) {
                    count = 0;

                    alTempList.add("Targets");
                    alCollectionList.addAll(ConstantsUtils.getTargets());
                    countBean = new PendingCountBean();
                    countBean.setCollection("Targets");
                    countBean.setCount(count);
                    countBean.setSyncTime(historyModel.getTimeStamp());
                    countBean.setAlCollectionList(alCollectionList);
                    tempNonpendingList.add(countBean);
                }else if((historyModel.getCollections().equalsIgnoreCase(Constants.SalesPersons)|| historyModel.getCollections().equalsIgnoreCase(Constants.UserSalesPersons)) && !alTempList.contains("Sale Person")){
                    count = 0;

                    alTempList.add("Sale Person");
                    alCollectionList.addAll(ConstantsUtils.getSalePerson());
                    countBean = new PendingCountBean();
                    countBean.setCollection("Sale Person");
                    countBean.setCount(count);
                    countBean.setSyncTime(historyModel.getTimeStamp());
                    countBean.setAlCollectionList(alCollectionList);
                    tempNonpendingList.add(countBean);
                }else if((historyModel.getCollections().equalsIgnoreCase(Constants.MaterialSaleAreas)|| historyModel.getCollections().equalsIgnoreCase(Constants.OrderMaterialGroups)) && !alTempList.contains("Masters")){
                    count = 0;

                    alTempList.add("Masters");
                    alCollectionList.addAll(ConstantsUtils.getMaster());
                    countBean = new PendingCountBean();
                    countBean.setCollection("Masters");
                    countBean.setCount(count);
                    countBean.setSyncTime(historyModel.getTimeStamp());
                    countBean.setAlCollectionList(alCollectionList);
                    tempNonpendingList.add(countBean);
                }else if((historyModel.getCollections().equalsIgnoreCase(Constants.PlantStocks)) && !alTempList.contains("Plant Stocks")){
                    count = 0;

                    alTempList.add("Plant Stocks");
                    alCollectionList.add(Constants.PlantStocks);
                    countBean = new PendingCountBean();
                    countBean.setCollection("Plant Stocks");
                    countBean.setCount(count);
                    countBean.setSyncTime(historyModel.getTimeStamp());
                    countBean.setAlCollectionList(alCollectionList);
                    tempNonpendingList.add(countBean);
                }else if((historyModel.getCollections().equalsIgnoreCase(Constants.Stocks)) && !alTempList.contains("Stocks")){
                    count = 0;

                    alTempList.add("Stocks");
                    alCollectionList.add(Constants.Stocks);
                    countBean = new PendingCountBean();
                    count = OfflineManager.getPendingCount(Constants.Stocks + "?$filter= sap.islocal() ") + OfflineManager.getPendingCount(Constants.VisitActivities + "?$filter= sap.islocal() ");
                    countBean = new PendingCountBean();
                    if (count > 0) {
                        pendingCount = pendingCount + count;
                        countBean.setCollection(Constants.Stocks);
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        temppendingList.add(countBean);
                    }else {
                        countBean.setCollection(Constants.Stocks);
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        tempNonpendingList.add(countBean);
                    }
                }else if ((historyModel.getCollections().equalsIgnoreCase(Constants.SyncHistorys)) && !alTempList.contains(Constants.SyncHistorys)) {
                    count = 0;
                    alTempList.add("SyncHistorys");
                    countBean = new PendingCountBean();
                    count = OfflineManager.getPendingCount(Constants.SyncHistorys + "?$filter= sap.islocal() ");
                    alCollectionList.add(Constants.SyncHistorys);
                    if (count > 0) {
                        pendingCount = pendingCount + count;
                        countBean.setCollection("Sync Historys");
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        temppendingList.add(countBean);
                    } else {
                        countBean.setCollection("Sync Historys");
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        tempNonpendingList.add(countBean);
                    }
                }else if ((historyModel.getCollections().equalsIgnoreCase(Constants.SPGeos)) && !alTempList.contains(Constants.SPGeos)) {
                    count = 0;

                    if (sharedPreferences.getBoolean(getString(R.string.enable_geo), false)){
                        alTempList.add("SPGeos");
                        alCollectionList.add("SPGeos");
                        countBean = new PendingCountBean();
                        if(sqlDbCount>0) countBean.setShowProgress(true);
                        else countBean.setShowProgress(false);
                        count = OfflineManager.getPendingCountGeo(Constants.SPGeos + "?$filter= sap.islocal() ");
                        count += databaseHelper.getSqlLocationDataCount();
                        if (count > 0) {
                            pendingCount = pendingCount + count;
                            countBean.setCollection("Geo");
                            countBean.setCount(count);
                            countBean.setSyncTime(historyModel.getTimeStamp());
                            countBean.setAlCollectionList(alCollectionList);
                            temppendingList.add(countBean);
                        } else {
                            countBean.setCollection("Geo");
                            countBean.setCount(count);
                            countBean.setSyncTime(historyModel.getTimeStamp());
                            countBean.setAlCollectionList(alCollectionList);
                            tempNonpendingList.add(countBean);
                        }
                    }
                    else{
                        Log.d("Geo Enable : "," false");
                    }
                }


            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(tempNonpendingList, new Comparator<PendingCountBean>() {
            public int compare(PendingCountBean one, PendingCountBean other) {
                return one.getCollection().compareTo(other.getCollection());
            }
        });

        Collections.sort(temppendingList, new Comparator<PendingCountBean>() {
            public int compare(PendingCountBean one, PendingCountBean other) {
                return one.getCollection().compareTo(other.getCollection());
            }
        });
        pendingCountBeans.addAll(temppendingList);
        pendingCountBeans.addAll(tempNonpendingList);
        return pendingCountBeans;
    }
    private void startNetworkMonitoring(){
        mTrafficSpeedMeasurer.startMeasuring();
        isMonitoringStopped = true;
        checkNetwork(getContext(), new OnNetworkInfoListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onNetworkFailureListener(boolean isFailed) {
                if (isFailed) {
                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //  Toast.makeText(SyncSelectionActivity.this, "Network dropped / unable to connect.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        },false,0);

    }
    @Override
    public  void onDestroy() {
        super.onDestroy();
        mTrafficSpeedMeasurer.stopMeasuring();
    }

    @Override
    public  void onResume() {
        super.onResume();
        isClickable=false;
        mTrafficSpeedMeasurer.registerListener(mStreamSpeedListener);

    }

    @Override
    public  void onPause() {
        super.onPause();
        mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
    }


    private ITrafficSpeedListener mStreamSpeedListener = new ITrafficSpeedListener() {

        @Override
        public void onTrafficSpeedMeasured(final double upStream, final double downStream) {
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String upStreamSpeed = Utils.parseSpeed(upStream, SHOW_SPEED_IN_BITS);
                    String downStreamSpeed = Utils.parseSpeed(downStream, SHOW_SPEED_IN_BITS);
                    if(upStream<=0 || downStream<=0)
                        networkErrorCount++;
                    else
                        networkErrorCount=0;

                    if((upStream!=0 && upStream<1)|| (downStream!=0 && downStream<1))
                        networkError++;
                    else
                        networkError=0;

                    if(networkErrorCount>=3){
                        networkErrorCount=0;
                        isMonitoringStopped = false;
                        mTrafficSpeedMeasurer.stopMeasuring();
                        mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
                        UtilConstants.dialogBoxWithCallBack(getContext(), "", "Sync can't perform due to network unavailability", getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
                            @Override
                            public void clickedStatus(boolean b) {
                                if (b) {
                                    onBackPressed();
                                }
                            }
                        });
                    }else if(networkError>=3){
                        networkError=0;
                        isMonitoringStopped = false;
                        mTrafficSpeedMeasurer.stopMeasuring();
                        mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
                        UtilConstants.dialogBoxWithCallBack(getContext(), "", "Sync can't perform due to low network bandwidth", getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
                            @Override
                            public void clickedStatus(boolean b) {
                                if (b) {
                                    onBackPressed();
                                }
                            }
                        });
                    }

                    Log.d("Network_Bandwidth","Values"+upStreamSpeed+"--"+downStreamSpeed);
                }
            });
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
                    networkError=0;
                    isMonitoringStopped = false;
                    mTrafficSpeedMeasurer.stopMeasuring();
                    mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
                    UtilConstants.dialogBoxWithCallBack(getContext(), "", "Sync can't perform due to network unavailability", getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
                        @Override
                        public void clickedStatus(boolean b) {
                            if (b) {
                                onBackPressed();
                            }
                        }
                    });
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
    private void onUpdateSync() {

        Constants.Entity_Set.clear();
        Constants.AL_ERROR_MSG.clear();
        mBoolIsNetWorkNotAval = false;
        isBatchReqs = false;
        mBoolIsReqResAval = true;
        updateCancelSOCount = 0;
        cancelSoPos = 0;
        try{
            penReqCount = 0;
            mIntPendingCollVal = 0;
            invKeyValues = null;
            cancelSOCount = new int[0];
            ArrayList<Object> objectArrayList = getPendingCollList(getActivity(), false);
            if (!objectArrayList.isEmpty()) {
                mIntPendingCollVal = (int) objectArrayList.get(0);
                invKeyValues = (String[][]) objectArrayList.get(1);
                cancelSOCount = (int[]) objectArrayList.get(2);
            }

            try {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME,0);
                if (OfflineManager.offlineGeo!=null &&!OfflineManager.offlineGeo.getRequestQueueIsEmpty() && sharedPreferences.getBoolean(getString(R.string.enable_geo), false)) {
                    if (UtilConstants.isNetworkAvailable(getActivity())) {
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
                                        if (UtilConstants.isNetworkAvailable(getActivity())) {
                                            try {
                                                //                        OfflineManager.refreshRequests(getApplicationContext(), concatCollectionStr, SyncSelectionActivity.this);
                                                new RefreshGeoAsyncTask(getActivity(), Constants.SPGeos, new UIListener() {
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
           /* if (OfflineManager.offlineStore.getRequestQueueIsEmpty() && mIntPendingCollVal == 0) {
                initRecyclerView();
                UtilConstants.showAlert(getString(R.string.no_req_to_update_sap), getActivity());
            } else {*/
            alAssignColl.clear();
            alFlushColl.clear();
            ArrayList<String> allAssignColl = getRefreshList(getActivity());
            if (!allAssignColl.isEmpty()) {
                alAssignColl.addAll(allAssignColl);
                alFlushColl.addAll(allAssignColl);
            }
            if (Constants.iSAutoSync || Constants.isBackGroundSync) {
                if (Constants.iSAutoSync) {
                    showAlert(getString(R.string.alert_auto_sync_is_progress));
                } else if (Constants.isBackGroundSync) {
                    showAlert(getString(R.string.alert_backgrounf_sync_is_progress));
                }
            } else {
                if (mIntPendingCollVal > 0) {

                    if (!alAssignColl.contains(Constants.ConfigTypsetTypeValues))
                        alAssignColl.add(Constants.ConfigTypsetTypeValues);
                    if (UtilConstants.isNetworkAvailable(getActivity())) {
                        refguid = GUID.newRandom();
                        Constants.updateStartSyncTime(getActivity(), Constants.UpLoad, Constants.StartSync,refguid.toString().toUpperCase());
                        Constants.isSync = true;
                        onPostOnlineData();
                    } else {
                        showAlert(getString(R.string.no_network_conn));
                    }
                } else if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                    refguid = GUID.newRandom();
                    if (UtilConstants.isNetworkAvailable(getActivity())) {
                        Constants.updateStartSyncTime(getActivity(), Constants.UpLoad, Constants.StartSync,refguid.toString().toUpperCase());
                        onPostOfflineData();
                    } else {
                        showAlert(getString(R.string.no_network_conn));
                    }
                }

//                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*private void checkPendingReqIsAval() {
        try {
            mIntPendingCollVal = 0;
            invKeyValues = null;
            ArrayList<Object> objectArrayList = getPendingCollList(getActivity(), false);
            if (!objectArrayList.isEmpty()) {
                mIntPendingCollVal = (int) objectArrayList.get(0);
                invKeyValues = (String[][]) objectArrayList.get(1);
            }

            penReqCount = 0;


            alAssignColl.clear();
            alFlushColl.clear();
            concatCollectionStr = "";
            concatFlushCollStr = "";
            ArrayList<String> allAssignColl = getRefreshList(getActivity());
            if (!allAssignColl.isEmpty()) {
                alAssignColl.addAll(allAssignColl);
                alFlushColl.addAll(allAssignColl);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/


    private void onPostOnlineData() {
        try {
            new AsyncPostDataValutData().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestError(int operation, Exception ex) {
        isClickable =false;
        if (ex != null)
            ex.printStackTrace();
        final ErrorBean errorBean = Constants.getErrorCode(operation, ex, getActivity());
        try {
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
                                if (UtilConstants.isNetworkAvailable(getActivity())) {
                                    try {
                                        new AsyncPostOfflineData().execute();
                                    } catch (Exception e2) {
                                        e2.printStackTrace();
                                    }
                                } else {
                                    Constants.isSync = false;
                                    showAlert(getString(R.string.data_conn_lost_during_sync));                                }
                            } else {
                                if (UtilConstants.isNetworkAvailable(getActivity())) {
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
                                        new RefreshAsyncTask(getActivity(), concatCollectionStr, this).execute();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        TraceLog.e(Constants.SyncOnRequestSuccess, e);
                                    }
                                } else {
                                    Constants.isSync = false;
                                    closingProgressDialog();
                                    showAlert(getString(R.string.data_conn_lost_during_sync));                                }
                            }

                        } catch (ODataException e3) {
                            e3.printStackTrace();
                        }
                    }

                }

                if (operation == Operation.OfflineFlush.getValue()) {
                    if (UtilConstants.isNetworkAvailable(getActivity())) {
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
                            new RefreshAsyncTask(getActivity(), concatCollectionStr, this).execute();
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
                    final String finalMErrorMsg = mErrorMsg;
                    Constants.updateSyncTime(alAssignColl, getActivity(), Constants.UpLoad,refguid.toString().toUpperCase(), new SyncHistoryCallBack() {
                        @Override
                        public void displaySuccessMessage() {
                            try {
                                if(!getActivity().isFinishing()) {
                                    if (finalMErrorMsg.equalsIgnoreCase("")) {
                                        if (errorBean.getErrorMsg().contains("invalid authentication")) {

                                            try {
                                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME,0);
                                                String loginUser=sharedPreferences.getString("username","");
                                                String login_pwd=sharedPreferences.getString("password","");
                                                UtilConstants.getPasswordStatus(Configuration.IDPURL, loginUser, login_pwd, Configuration.APP_ID, new UtilConstants.PasswordStatusCallback() {
                                                    @Override
                                                    public void passwordStatus(final JSONObject jsonObject) {

                                                        if(!getActivity().isFinishing()) {
                                                            getActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    closingProgressDialog();
                                                                    Constants.passwordStatusErrorMessage(getActivity(), jsonObject, loginUser);
                                                                }
                                                            });
                                                        }

                                                    }
                                                });
                                            } catch (Throwable e) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        closingProgressDialog();
                                                    }
                                                });
                                                e.printStackTrace();
                                            }


//                                            Constants.customAlertDialogWithScroll(getActivity(), errorBean.getErrorMsg());
                                        } else {
                                            closingProgressDialog();
                                            showAlert(errorBean.getErrorMsg());
                                        }

                                    } else {
                                        if (errorBean.getErrorMsg().contains("invalid authentication")) {

                                            try {
                                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME,0);
                                                String loginUser=sharedPreferences.getString("username","");
                                                String login_pwd=sharedPreferences.getString("password","");
                                                UtilConstants.getPasswordStatus(Configuration.IDPURL, loginUser, login_pwd, Configuration.APP_ID, new UtilConstants.PasswordStatusCallback() {
                                                    @Override
                                                    public void passwordStatus(final JSONObject jsonObject) {

                                                        if(!getActivity().isFinishing()) {
                                                            getActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    closingProgressDialog();
                                                                    Constants.passwordStatusErrorMessage(getActivity(), jsonObject, loginUser);
                                                                }
                                                            });
                                                        }

                                                    }
                                                });
                                            } catch (Throwable e) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        closingProgressDialog();
                                                    }
                                                });
                                                e.printStackTrace();
                                            }


//                                            Constants.customAlertDialogWithScroll(getActivity(), errorBean.getErrorMsg());
                                        }else {
                                            closingProgressDialog();
                                            Constants.customAlertDialogWithScroll(getActivity(), finalMErrorMsg);
                                        }
                                    }
                                }else {
                                    try {
                                        LogManager.writeLogError("SyncHistoryInfoFragment Finished");
                                    } catch (Throwable e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } else if (operation == Operation.GetStoreOpen.getValue()) {
                    mBoolIsReqResAval = true;
                    mBoolIsNetWorkNotAval = true;
                    closingProgressDialog();
                    Constants.isSync = false;
                    showAlert(errorBean.getErrorMsg());
                }
            } else if (errorBean.isStoreFailed()) {
                if (UtilConstants.isNetworkAvailable(getActivity())) {
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
                    Constants.displayMsgReqError(errorBean.getErrorCode(), getActivity());
                }
            } else {
                mBoolIsReqResAval = true;
                mBoolIsNetWorkNotAval = true;
                Constants.isSync = false;
                closingProgressDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), getActivity());
            }
        } catch (Exception e) {
            mBoolIsReqResAval = true;
            mBoolIsNetWorkNotAval = true;
            Constants.isSync = false;
            closingProgressDialog();
            Constants.displayMsgReqError(errorBean.getErrorCode(), getActivity());
        }
    }

    @Override
    public void onRequestSuccess(int operation, String key)  {
        isClickable =false;
        if (dialogCancelled == false && !Constants.isStoreClosed) {
            if (operation == Operation.Create.getValue() && mIntPendingCollVal > 0) {
                mBoolIsReqResAval = true;
                if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.CollList)) {
                    Constants.removeDeviceDocNoFromSharedPref(getActivity(), Constants.CollList, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SalesOrderDataValt)) {
                    Constants.removeDeviceDocNoFromSharedPref(getActivity(), Constants.SalesOrderDataValt, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SOUpdate)) {
                    Constants.removeDeviceDocNoFromSharedPref(getActivity(), Constants.SOUpdate, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SOCancel)) {
                    Constants.removeDeviceDocNoFromSharedPref(getActivity(), Constants.SOCancel, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.Expenses)) {
                    Constants.removeDeviceDocNoFromSharedPref(getActivity(), Constants.Expenses, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.MTPDataValt)) {
                    Constants.removeDeviceDocNoFromSharedPref(getActivity(), Constants.MTPDataValt, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.RTGSDataValt)) {
                    Constants.removeDeviceDocNoFromSharedPref(getActivity(), Constants.RTGSDataValt, invKeyValues[penReqCount][0]);
                }

                ConstantsUtils.storeInDataVault(invKeyValues[penReqCount][0], "",getActivity());

                penReqCount++;
            }
            if (operation == Operation.Update.getValue() && mIntPendingCollVal > 0) {
                mBoolIsReqResAval = true;
                updateCancelSOCount++;
                if (cancelSOCount.length == 0 || updateCancelSOCount == cancelSOCount[cancelSoPos]) {
                    updateCancelSOCount = 0;
                    cancelSoPos++;

                    if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.CollList)) {
                        Constants.removeDeviceDocNoFromSharedPref(getActivity(), Constants.CollList, invKeyValues[penReqCount][0]);
                    } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SalesOrderDataValt)) {
                        Constants.removeDeviceDocNoFromSharedPref(getActivity(), Constants.SalesOrderDataValt, invKeyValues[penReqCount][0]);
                    } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SOUpdate)) {
                        Constants.removeDeviceDocNoFromSharedPref(getActivity(), Constants.SOUpdate, invKeyValues[penReqCount][0]);
                    } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SOCancel)) {
                        Constants.removeDeviceDocNoFromSharedPref(getActivity(), Constants.SOCancel, invKeyValues[penReqCount][0]);
                    } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.Expenses)) {
                        Constants.removeDeviceDocNoFromSharedPref(getActivity(), Constants.Expenses, invKeyValues[penReqCount][0]);
                    } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.MTPDataValt)) {
                        Constants.removeDeviceDocNoFromSharedPref(getActivity(), Constants.MTPDataValt, invKeyValues[penReqCount][0]);
                    } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.RTGSDataValt)) {
                        Constants.removeDeviceDocNoFromSharedPref(getActivity(), Constants.RTGSDataValt, invKeyValues[penReqCount][0]);
                    }

                    ConstantsUtils.storeInDataVault(invKeyValues[penReqCount][0], "",getActivity());
                    String popUpText = "";
                    if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SOCancel))
                        popUpText = "Sales order # " + invKeyValues[penReqCount][0] + " cancelled successfully.";
                    else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SOUpdate))
                        popUpText = "Sales order # " + invKeyValues[penReqCount][0] + " changed successfully.";
                    LogManager.writeLogInfo(popUpText);

                    penReqCount++;
                }
            }
            if ((operation == Operation.Create.getValue() || operation == Operation.Update.getValue()) && (penReqCount == mIntPendingCollVal)) {
                try {
                    if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                        closingProgressDialog();
                        if (UtilConstants.isNetworkAvailable(getActivity())) {
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
                        if (UtilConstants.isNetworkAvailable(getActivity())) {
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
                                new RefreshAsyncTask(getActivity(), concatCollectionStr, this).execute();
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
                }

            } else if (operation == Operation.OfflineFlush.getValue()) {
                if (UtilConstants.isNetworkAvailable(getActivity())) {
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
//                        OfflineManager.refreshRequests(getApplicationContext(), concatCollectionStr, SyncSelectionActivity.this);
                        new RefreshAsyncTask(getActivity(), concatCollectionStr, this).execute();
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
                /*try {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME,0);
                    if (OfflineManager.offlineGeo!=null&&!OfflineManager.offlineGeo.getRequestQueueIsEmpty()  && sharedPreferences.getBoolean(getString(R.string.enable_geo), false)) {
                        try {
                            OfflineManager.flushQueuedRequestsForGeo(new UIListener() {
                                @Override
                                public void onRequestError(int i, Exception e) {
                                    refreshData();
                                }

                                @Override
                                public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
                                    if (operation == Operation.OfflineFlush.getValue()) {
                                        if (UtilConstants.isNetworkAvailable(getActivity())) {
                                            try {
//                        OfflineManager.refreshRequests(getApplicationContext(), concatCollectionStr, SyncSelectionActivity.this);
                                                new RefreshGeoAsyncTask(getActivity(), Constants.SPGeos, this).execute();
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
                new NotificationSetClass(getActivity());
                ConstantsUtils.startAutoSync(getActivity(), false);
                try {
                    OfflineManager.getAuthorizations(getActivity());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                Constants.setSyncTime(getActivity(),refguid.toString().toUpperCase());
                closingProgressDialog();
                showAlert(getString(R.string.msg_sync_successfully_completed));
            }
        }
    }

    private void refreshData() {
        try {
            OfflineManager.getAuthorizations(getActivity());
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        Constants.deleteDeviceMerchansisingFromDataVault(getActivity());
        Constants.setBirthdayListToDataValut(getActivity());
        ConstantsUtils.startAutoSync(getActivity(), false);
        setAppointmentNotification();
        updatingSyncTime();
        Constants.isSync = false;
        String mErrorMsg = "";
        if (Constants.AL_ERROR_MSG.size() > 0) {
            mErrorMsg = Constants.convertALBussinessMsgToString(Constants.AL_ERROR_MSG);
        }

        final String finalMErrorMsg = mErrorMsg;
        Constants.updateSyncTime(alAssignColl, getActivity(), Constants.UpLoad,refguid.toString().toUpperCase(), new SyncHistoryCallBack() {
            @Override
            public void displaySuccessMessage() {
                closingProgressDialog();
                if (mError == 0) {
//                    ConstantsUtils.serviceReSchedule(getActivity(), true);
                    UtilConstants.dialogBoxWithCallBack(getActivity(), "", getString(R.string.msg_sync_successfully_completed), getString(R.string.ok), "", false, new DialogCallBack() {
                        @Override
                        public void clickedStatus(boolean b) {
                            AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, getActivity(), BuildConfig.APPLICATION_ID, false);
                        }
                    });
                    isMonitoringStopped=false;
                    mTrafficSpeedMeasurer.stopMeasuring();
                    mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
                } else {
                    try {
                        if(!getActivity().isFinishing()) {
                            if (finalMErrorMsg.equalsIgnoreCase("")) {
                                showAlert(getString(R.string.error_occured_during_post));
                            } else {
                                Constants.customAlertDialogWithScroll(getActivity(), finalMErrorMsg);
                            }
                        }else {
                            try {
                                LogManager.writeLogError("SyncHistoryInfoFragment Finished");
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
    }

    /*private void setStoreOpenUI() {
        closingProgressDialog();
        UtilConstants.showAlert(getString(R.string.msg_offline_store_success),
                getActivity());
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivUploadDownload:
                if (UtilConstants.isNetworkAvailable(getActivity())) {
                    if (!Constants.isPullDownSync&&!Constants.isBackGroundSync&&!Constants.iSAutoSync) {
                        if (!isClickable) {
                            isClickable = true;
                            syncType = Constants.UpLoad;
                            onUpdateSync();
                        }
                    }else{
                        if (Constants.isPullDownSync||Constants.iSAutoSync||Constants.isBackGroundSync) {
                            if (Constants.iSAutoSync){
                                showAlert(getString(R.string.alert_auto_sync_is_progress));
                            }else{
                                showAlert(getString(R.string.alert_backgrounf_sync_is_progress));
                            }
                        }
                    }
                }else{
                    showAlert(getString(R.string.no_network_conn));
                }
                break;
            case R.id.ivSyncAll:
//                onAllSync();
                break;
        }
    }

    private void onBackPressed() {
        getActivity().finish();
    }

    private void closingProgressDialog() {
        try {
            syncProgDialog.dismiss();
            syncProgDialog = null;
            isClickable = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        concatCollectionStr = "";
        initRecyclerView();
    }


    private void onPostOfflineData() {
        Constants.isSync = true;
        try {
            new AsyncPostOfflineData().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void onUploadDownload(boolean isUpload, final PendingCountBean countBean, String syncType) {
        this.syncType = "";
        alAssignColl.clear();
        Constants.AL_ERROR_MSG.clear();
        Constants.Entity_Set.clear();
        Constants.isSync = true;
        dialogCancelled = false;
        this.syncType = syncType;
        Constants.isStoreClosed = false;
        mError = 0;
        refguid = GUID.newRandom();
        if (UtilConstants.isNetworkAvailable(getActivity())) {
            if (Constants.isPullDownSync||Constants.iSAutoSync||Constants.isBackGroundSync) {
                if (Constants.iSAutoSync){
                    showAlert(getString(R.string.alert_auto_sync_is_progress));
                }else{
                    showAlert(getString(R.string.alert_backgrounf_sync_is_progress));
                }
            }else{
                if(!isClickable) {
                    isClickable = true;
                    alAssignColl.addAll(countBean.getAlCollectionList());
                    if (!alAssignColl.contains(Constants.ConfigTypsetTypeValues))
                        alAssignColl.add(Constants.ConfigTypsetTypeValues);
                    syncProgDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
                    syncProgDialog.setMessage(getString(R.string.app_loading));
                    syncProgDialog.setCancelable(false);
                    syncProgDialog.show();
                    if(alAssignColl.contains(Constants.SPGeos)){
                        try {
//                        OfflineManager.refreshRequests(getApplicationContext(), concatCollectionStr, SyncSelectionActivity.this);
                            new RefreshGeoAsyncTask(getActivity(), Constants.SPGeos, new UIListener() {
                                @Override
                                public void onRequestError(int i, Exception e) {

                                }

                                @Override
                                public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                                    if (i == Operation.OfflineRefresh.getValue()) {
                                        alAssignColl.remove(Constants.SPGeos);
                                        concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                                        new RefreshAsyncTask(getActivity(), concatCollectionStr, SyncHistoryInfoFragment.this).execute();
                                    }



                                }
                            }).execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                            TraceLog.e(Constants.SyncOnRequestSuccess, e);
                        }
                    }else{
                        concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                        new RefreshAsyncTask(getActivity(), concatCollectionStr, this).execute();
                    }


                }
            }
        } else {
            ConstantsUtils.showAlert(getString(R.string.data_conn_lost_during_sync), getActivity(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isClickable = false;
                    dialog.cancel();
                }
            });
        }
    }

   /* private void assignCollToArrayList() {
        alAssignColl.clear();
        concatCollectionStr = "";
        alAssignColl.addAll(SyncUtils.getAllSyncValue(getActivity()));
        concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
    }*/

   /* private void onAllSync() {
        if (UtilConstants.isNetworkAvailable(getActivity())) {
            onSyncAll();
        } else {
            UtilConstants.showAlert(getActivity().getString(R.string.no_network_conn), getActivity());
        }
    }*/

    private void onSyncAll() {
        try {
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
        syncProgDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
        syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
        syncProgDialog.setCancelable(true);
        syncProgDialog.setCanceledOnTouchOutside(false);
        syncProgDialog.show();

        syncProgDialog
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface Dialog) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                getActivity(), R.style.MyTheme);
                        builder.setMessage(R.string.do_want_cancel_sync)
                                .setCancelable(false)
                                .setPositiveButton(
                                        R.string.yes,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface Dialog,
                                                    int id) {
                                                dialogCancelled = true;
                                                isClickable =false;
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
        new AllSyncAsyncTask(getActivity(), this, new ArrayList<String>(),refguid.toString().toUpperCase()).execute();
    }

    private void assignCollToArrayList() {
        alAssignColl.clear();
        concatCollectionStr = "";
        alAssignColl.addAll(Constants.getDefinigReqList(getActivity()));
    }

    /*public class LoadingData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
            syncProgDialog.setCancelable(true);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();

            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    getActivity(), R.style.MyTheme);
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
//            Constants.printLogInfo("check store is opened or not");
            if (!OfflineManager.isOfflineStoreOpen()) {
//                Constants.printLogInfo("check store is failed");
                try {
                    OfflineManager.openOfflineStore(getActivity(), SyncHistoryInfoFragment.this);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
            } else {
                Constants.isStoreClosed = false;
                assignCollToArrayList();
//                Constants.printLogInfo("check store is opened");
                try {
                    OfflineManager.refreshStoreSync(getActivity().getApplicationContext(), SyncHistoryInfoFragment.this, Constants.ALL, "");
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





    public class AsyncPostOfflineData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.updating_data_plz_wait));
            syncProgDialog.setCancelable(false);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();
            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    getActivity(), R.style.MyTheme);
                            builder.setMessage(R.string.do_want_cancel_sync)
                                    .setCancelable(false)
                                    .setPositiveButton(
                                            R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {
                                                    dialogCancelled = true;
                                                    isClickable =false;
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
                            OfflineManager.flushQueuedRequests(SyncHistoryInfoFragment.this, concatFlushCollStr);
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

    /*private List<SyncHistoryModel> getRecordInfo() {
        List<SyncHistoryModel> syncHistoryModelList = (new SyncHistoryDB(this.getActivity())).getAllRecord();
        List<SyncHistoryModel> duplicateSyncHistoryModelList = new ArrayList();
        ArrayList<String> alEntity = new ArrayList<>();
        for (int k=0; k<syncHistoryModelList.size(); k++){
            SyncHistoryModel historyModel = syncHistoryModelList.get(k);

            if((historyModel.getCollections().equalsIgnoreCase("RoutePlans") || historyModel.getCollections().equalsIgnoreCase("RouteSchedulePlans") || historyModel.getCollections().equalsIgnoreCase("RouteSchedules")) && !alEntity.contains("Beat")){
                SyncHistoryModel model = new SyncHistoryModel();
                model.setCollections("Beat");
                model.setTimeStamp(historyModel.getTimeStamp());
                duplicateSyncHistoryModelList.add(model);
                alEntity.add("Beat");
            }else if((historyModel.getCollections().equalsIgnoreCase("ChannelPartners") || historyModel.getCollections().equalsIgnoreCase("CPDMSDivisons") ) && !alEntity.contains("Retailers")){
                SyncHistoryModel model = new SyncHistoryModel();
                model.setCollections("Retailers");
                model.setTimeStamp(historyModel.getTimeStamp());
                duplicateSyncHistoryModelList.add(model);
                alEntity.add("Retailers");

            }else if((historyModel.getCollections().equalsIgnoreCase("SSSOs")  || historyModel.getCollections().equalsIgnoreCase("SSSOItemDetails")) && !alEntity.contains("Sales Order")){
                SyncHistoryModel model = new SyncHistoryModel();
                model.setCollections("Sales Order");
                model.setTimeStamp(historyModel.getTimeStamp());
                duplicateSyncHistoryModelList.add(model);
                alEntity.add("Sales Order");
            }else if((historyModel.getCollections().equalsIgnoreCase("FinancialPostings") || historyModel.getCollections().equalsIgnoreCase("FinancialPostingItemDetails")) && !alEntity.contains("Collections")){
                SyncHistoryModel model = new SyncHistoryModel();
                model.setCollections("Collections");
                model.setTimeStamp(historyModel.getTimeStamp());
                duplicateSyncHistoryModelList.add(model);
                alEntity.add("Collections");
            }else if((historyModel.getCollections().equalsIgnoreCase("Visits") || historyModel.getCollections().equalsIgnoreCase("VisitActivities")) && !alEntity.contains("Visits")){
                SyncHistoryModel model = new SyncHistoryModel();
                model.setCollections("Visits");
                model.setTimeStamp(historyModel.getTimeStamp());
                duplicateSyncHistoryModelList.add(model);
                alEntity.add("Visits");
            }else if((historyModel.getCollections().equalsIgnoreCase("Attendances")) && !alEntity.contains("Attendances")){
                SyncHistoryModel model = new SyncHistoryModel();
                model.setCollections("Attendances");
                model.setTimeStamp(historyModel.getTimeStamp());
                duplicateSyncHistoryModelList.add(model);
                alEntity.add("Attendances");
            }
        }

        Collections.sort(duplicateSyncHistoryModelList, new Comparator<SyncHistoryModel>() {
            @Override
            public int compare(SyncHistoryModel historyModel, SyncHistoryModel historyMode2) {
                return historyModel.getCollections().compareTo(historyMode2.getCollections());
            }
        } );
        return duplicateSyncHistoryModelList;
    }*/
    /*private class OpenOfflineStore extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.app_loading));
            syncProgDialog.setCancelable(false);
            syncProgDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                try {
                    if (!OfflineManager.isOfflineStoreOpen()) {
                        try {
                            OfflineManager.openOfflineStore(getActivity(), SyncHistoryInfoFragment.this);
                        } catch (OfflineODataStoreException e) {
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } catch (InterruptedException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }*/

    public class AsyncPostDataValutData extends AsyncTask<Void, Boolean, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.updating_data_plz_wait));
            syncProgDialog.setCancelable(false);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();
            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    getActivity(), R.style.MyTheme);
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
        protected Boolean doInBackground(Void... params) {
            boolean isStoreOpened = false;
            try {
                isStoreOpened = OnlineManager.openOnlineStore(getActivity(), true);
            } catch (com.rspl.sf.msfa.store.OnlineODataStoreException e) {
                e.printStackTrace();
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
                            store = ConstantsUtils.getFromDataVault(invKeyValues[k][0].toString(),getActivity());
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }


                        //Fetch object from data vault
                        try {

                            JSONObject fetchJsonHeaderObject = new JSONObject(store);
                            dbHeadTable = new Hashtable();
                            arrtable = new ArrayList<>();

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
                                    OnlineManager.createCollectionEntry(dbHeadTable, arrtable, SyncHistoryInfoFragment.this);

                                } catch (com.rspl.sf.msfa.store.OnlineODataStoreException e) {
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
                                OnlineManager.createEntity( Constants.REPEATABLE_REQUEST_ID,Constants.REPEATABLE_DATE,dbHeadTable.toString(), Constants.SOs, SyncHistoryInfoFragment.this, getActivity());
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

                                OnlineManager.createEntity(Constants.REPEATABLE_REQUEST_ID,Constants.REPEATABLE_DATE,dbHeadTable.toString(), Constants.SOs, SyncHistoryInfoFragment.this, getActivity());
                            }else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SOUpdate)) {
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
                                    OnlineManager.cancelSO(dbHeadTable, arrtable, SyncHistoryInfoFragment.this);
                                } catch (com.rspl.sf.msfa.store.OnlineODataStoreException e) {
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
                                    OnlineManager.createDailyExpense(dbHeadTable, arrtable, SyncHistoryInfoFragment.this);
                                } catch (com.rspl.sf.msfa.store.OnlineODataStoreException e) {
                                    e.printStackTrace();
                                }
                            } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.RouteSchedules)) {
                                // preparing entity pending
                                if (!alAssignColl.contains(Constants.RouteSchedulePlans)) {
                                    alAssignColl.add(Constants.RouteSchedules);
                                    alAssignColl.add(Constants.RouteSchedulePlans);
                                }
                                Constants.REPEATABLE_REQUEST_ID="";
                                Constants.REPEATABLE_DATE="";
                                isBatchReqs = false;

                                JSONObject dbHeadTable = Constants.getMTPHeaderValuesFrmJsonObject(fetchJsonHeaderObject);
                                if (TextUtils.isEmpty(String.valueOf(fetchJsonHeaderObject.get(Constants.IS_UPDATE)))) {
                                    OnlineManager.createEntity(Constants.REPEATABLE_REQUEST_ID,Constants.REPEATABLE_DATE, dbHeadTable.toString(), Constants.RouteSchedules, SyncHistoryInfoFragment.this,getActivity());
                                }else {
                                    try {
                                        OnlineManager.batchUpdateMTP(fetchJsonHeaderObject,getActivity(),SyncHistoryInfoFragment.this);
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
                                    OnlineManager.createEntity(Constants.REPEATABLE_REQUEST_ID,Constants.REPEATABLE_DATE, dbHeadTable.toString(), Constants.CollectionPlan, SyncHistoryInfoFragment.this, getActivity());
                                }else {
                                    try {
                                        OnlineManager.batchUpdateRTGS(fetchJsonHeaderObject,getActivity(),SyncHistoryInfoFragment.this);
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
                    ConstantsUtils.showAlert(getString(R.string.data_conn_lost_during_sync_error_code, Constants.ErrorNo + ""), getActivity(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isClickable = false;
                            dialog.cancel();
                        }
                    });

                } else if (Constants.ErrorNo == Constants.UnAuthorized_Error_Code && Constants.ErrorName.equalsIgnoreCase(Constants.NetworkError_Name)) {
                    if(Constants.ErrorNo == Constants.UnAuthorized_Error_Code){
                        String errorMessage = Constants.PasswordExpiredMsg;
                        ConstantsUtils.showAlert(errorMessage, getActivity(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isClickable = false;
                                dialog.cancel();
                            }
                        });
                    }else{
                        ConstantsUtils.showAlert(getString(R.string.auth_fail_plz_contact_admin, Constants.ErrorNo + ""), getActivity(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isClickable = false;
                                dialog.cancel();
                            }
                        });
                    }

//                    UtilConstants.showAlert(getString(R.string.auth_fail_plz_contact_admin, Constants.ErrorNo + ""), SyncSelectionActivity.this);
                } else if (Constants.ErrorNo == Constants.Comm_Error_Code) {
                    ConstantsUtils.showAlert(getString(R.string.data_conn_lost_during_sync_error_code, Constants.ErrorNo + ""), getActivity(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isClickable = false;
                            dialog.cancel();
                        }
                    });
                } else {
                    ConstantsUtils.showAlert(getString(R.string.data_conn_lost_during_sync_error_code, Constants.ErrorNo + ""), getActivity(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isClickable = false;
                            dialog.cancel();
                        }
                    });
                }
            }
        }
    }

    /*
   ToDo Update Last Sync time into DB table
    */
    private void updatingSyncTime() {
        if (!Constants.syncHistoryTableExist()) {
            try {
                Constants.createSyncDatabase(getActivity());  // create sync history table
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
            //  Constants.updateSyncTime(alAssignColl,getActivity(),Constants.DownLoad);
        } catch (Exception exce) {
            exce.printStackTrace();
            LogManager.writeLogError(Constants.sync_table_history_txt + exce.getMessage());
        }
    }

    private void setAppointmentNotification() {
        new NotificationSetClass(getActivity());

    }

    private ArrayList<SyncHistoryModel> getAllRecords(){
        ArrayList<SyncHistoryModel> syncHistoryModels = new ArrayList<>();
        Cursor syncHistCursor = SyncHist.getInstance().findAllSyncHist();

        if (syncHistCursor!=null && syncHistCursor.getCount() > 0) {
            while (syncHistCursor.moveToNext()) {
                SyncHistoryModel syncHistoryModel = new SyncHistoryModel();
                syncHistoryModel.setCollections(syncHistCursor.getString(syncHistCursor
                        .getColumnIndex(Constants.Collections)));
                syncHistoryModel.setTimeStamp(syncHistCursor
                        .getString(syncHistCursor
                                .getColumnIndex(Constants.TimeStamp)));
                syncHistoryModels.add(syncHistoryModel);
            }
        }
        return syncHistoryModels;
    }

    private void showAlert(String message){
        ConstantsUtils.showAlert(message, getActivity(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isClickable=false;
                dialog.cancel();
            }
        });
    }
}
