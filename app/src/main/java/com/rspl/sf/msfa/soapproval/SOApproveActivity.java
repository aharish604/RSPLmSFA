package com.rspl.sf.msfa.soapproval;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.OnlineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.collectionPlan.collectionCreate.SaleAreaBean;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.AsyncTaskCallBack;
import com.rspl.sf.msfa.interfaces.AsyncTaskSOItemInterface;
import com.rspl.sf.msfa.interfaces.DialogCallBack;
import com.rspl.sf.msfa.mbo.SalesOrderBean;
import com.rspl.sf.msfa.socreate.CreditLimitBean;
import com.rspl.sf.msfa.solist.SOListBean;
import com.rspl.sf.msfa.store.GetOnlineODataInterface;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.store.OnlineManager;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class SOApproveActivity extends AppCompatActivity implements OnClickInterface, UIListener, GetOnlineODataInterface, AsyncTaskSOItemInterface, SwipeRefreshLayout.OnRefreshListener, SOApprovalView, AdapterInterface<SOListBean> {

    private static final String TAG = "SOApproveActivity";
    public static String SOTotalCount = "";
    public static boolean isRefresh = false;
    ArrayList<SOListBean> soBeanList = new ArrayList<>();
    ArrayList<SOListBean> soBeanSearchList = new ArrayList<>();
    SOListBean selectedSOItem = null;
    SalesApprovalBean appItemBean = new SalesApprovalBean();
    private RecyclerView recyclerView;
    private TextView noDataFound;
    private int comingFrom = 0;
    private SOApprovalAdapter soApprovalAdapter;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefresh;
    private long refreshTime = 0;
    private List<String> alAssignColl = new ArrayList<>();
    private AsyncTaskSOItemInterface asyncTaskCallBack;
    private ArrayList<CreditLimitBean> creditLimitBean = new ArrayList<>();
    private int currentRequest = 0;
    private int totalRequest = 0;
    private String errorDetailsMsg = "";
    private int requestCode = 0;
    private SimpleRecyclerViewAdapter<SOListBean> simpleRecyclerViewAdapter;
    private SOApprovalPresenter soApprovalPresenter;
    private volatile boolean isR2Executed, isR3Executed, isR4Executed, isClicked;
    Bundle requestbundle = new Bundle();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_so_approve);
        Intent intent = getIntent();
        if (intent != null) {
            comingFrom = intent.getIntExtra(Constants.comingFrom, 0);
        }
        try {
            SharedPreferences sharedPreferences = SOApproveActivity.this.getSharedPreferences(Constants.PREFS_NAME, 0);
            if (sharedPreferences.getBoolean("writeDBGLog", false)) {
                Constants.writeDebug = sharedPreferences.getBoolean("writeDBGLog", false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (Constants.writeDebug) {
            LogManager.writeLogDebug("So Approval Activity opened");
        }
        initScreen();
    }

    private void initScreen() {
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setDistanceToTriggerSync(ConstantsUtils.SWIPE_REFRESH_DISTANCE);
        ConstantsUtils.setProgressColor(SOApproveActivity.this, swipeRefresh);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.menu_sales_order_approval), 0);
        asyncTaskCallBack = this;
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        noDataFound = (TextView) findViewById(R.id.no_record_found);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SOApproveActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<SOListBean>(SOApproveActivity.this, R.layout.ll_so_approval_item, SOApproveActivity.this, recyclerView, noDataFound);
        recyclerView.setAdapter(simpleRecyclerViewAdapter);
//        soApprovalAdapter.onItemClick(this);
        soApprovalPresenter = new SOApprovalPresenter(this, this, comingFrom);

        isRefresh = true;
    }

    private void showProgressDialog() {
        swipeRefresh.setRefreshing(true);
    }

    private void hideProgressDialog() {
        try {
            swipeRefresh.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openStore() {
        showProgressDialog();

        if (UtilConstants.isNetworkAvailable(SOApproveActivity.this)) {
            try {
                if (Constants.writeDebug)
                    LogManager.writeLogDebug("So Approval (online store Started)");
                Log.d(TAG, "openStore: start");
                new OpenOnlineManagerStore(SOApproveActivity.this, new AsyncTaskCallBack() {
                    @Override
                    public void onStatus(boolean status, String values) {
                        Log.d(TAG, "openStore: store opened");
                        hideProgressDialog();
                        if (status) {
                            if (Constants.writeDebug)
                                LogManager.writeLogDebug("So Approval (online store opened)");
                            requestSoApprovalList();
                        } else {
                            if (Constants.writeDebug)
                                LogManager.writeLogDebug("So Approval (online store Failed) : " + values);
                            ConstantsUtils.displayLongToast(SOApproveActivity.this, values);
                        }
                    }
                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } catch (Exception e) {
                if (Constants.writeDebug)
                    LogManager.writeLogDebug("So Approval (online store Failed) : " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        } else {
            hideProgressDialog();
            ConstantsUtils.dialogBoxWithButton(SOApproveActivity.this, "", getString(R.string.no_network_conn), getString(R.string.ok), "", new DialogCallBack() {
                @Override
                public void clickedStatus(boolean clickedStatus) {
                    setResult(ConstantsUtils.SO_RESULT_CODE);
                    finish();
                }
            });
        }
    }

    @Override
    public void displayRefreshTime(long refreshTime) {
        this.refreshTime = refreshTime;
        if (refreshTime > 0) {
            String stRefreshTime = ConstantsUtils.getLastSeenDateFormat(SOApproveActivity.this, refreshTime);
            String lastRefresh = "";
            if (!TextUtils.isEmpty(stRefreshTime)) {
                lastRefresh = getString(R.string.po_last_refreshed) + " " + stRefreshTime;
            }
            if (getSupportActionBar() != null) {
                getSupportActionBar().setSubtitle(lastRefresh);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        /*if (isRefresh) {
            Log.d(TAG, "onStart: true");
            soBeanList.clear();
            soBeanSearchList.clear();
            soApprovalAdapter.notifyDataSetChanged();
            openStore();
            isRefresh = false;
        }else {
            Log.d(TAG, "onStart: false");
        }*/
    }

    private void requestSoApprovalList() {
        if (UtilConstants.isNetworkAvailable(SOApproveActivity.this)) {
            getDataFromDB(SOApproveActivity.this);
        } else {
            ConstantsUtils.dialogBoxWithButton(SOApproveActivity.this, "", getString(R.string.no_network_conn), getString(R.string.ok), "", null);
        }
    }

    /*get SO data from offline db*/
    private void getDataFromDB(Context mContext) {
        showProgressDialog();
        if (Constants.writeDebug)
            LogManager.writeLogDebug("Requesting for SO Approval ");

        totalRequest = 0;
        currentRequest = 0;
        if (soApprovalPresenter != null) {
            soApprovalPresenter.onstart();
        }
        Log.d(TAG, "server request started");
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> entities, int operation, int requestCode, String resourcePath, Bundle bundle) {
        this.requestCode = requestCode;
        switch (requestCode) {
            case 1:
                try {
                    LogManager.writeLogInfo("Request Success SO Approval ");
                    SOTotalCount = String.valueOf(entities.size());
                    Log.d(TAG, "response came xml parse started");
                    soBeanList.clear();
//                    soBeanList = OnlineManager.getSOList(soBeanList, entities);
                    try {
                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constants.TotalSOCount, String.valueOf(soBeanList.size()));
                        editor.commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "SO List Loaded  ");
                    ((Activity) SOApproveActivity.this).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshTime = ConstantsUtils.getCurrentTimeLong();
                            displayRefreshTime(refreshTime);
//                            displayListView();
                            displaySearchList(soBeanList);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    LogManager.writeLogError(Constants.error_txt + " : " + e.getMessage());
                } finally {
                    ((Activity) SOApproveActivity.this).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgDailog();
                        }
                    });

                }
                currentRequest++;
                break;
            case 2:
                try {
                    Log.e(TAG, "Request Success SO Items Details");
                    LogManager.writeLogInfo("Request Success SO Items Details");
                    appItemBean = OnlineManager.getSOHeaderList(appItemBean, oDataRequestExecution, SOApproveActivity.this);
                } catch (OnlineODataStoreException e) {
                    e.printStackTrace();
                }
                refreshCreditLimit();
                currentRequest++;
                isR2Executed = true;
                Log.d(TAG, "getCreditLimitValue response 1 ");

                Log.d("REQUEST", "R2 EXECUTED");
                break;
            case 3:
                Log.e(TAG, "Request Success Tasks");
                LogManager.writeLogInfo("Request Success Tasks");
                appItemBean = OnlineManager.getTaskHistoryList(appItemBean, oDataRequestExecution, SOApproveActivity.this);
                currentRequest++;
                isR3Executed = true;
                Log.d("REQUEST", "R3 EXECUTED");
                Log.d(TAG, "getCreditLimitValue response 2 ");

                break;

            case 4:
                List<CreditLimitBean> limitBeanList = null;
                try {
                    Log.e(TAG, "Response Success for Customer Credit Limit");
                    LogManager.writeLogInfo("Response Success for Customer Credit Limit");
                    limitBeanList = OfflineManager.getCreditLimitOnline(entities);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!limitBeanList.isEmpty()) {
                    creditLimitBean.clear();
                    for (CreditLimitBean data :limitBeanList)
                        creditLimitBean.add(data);
//                    getCreditLimitValue();
//                    getCreditLimit();
                }
                currentRequest++;
                isR4Executed = true;
                Log.d("REQUEST", "R4 EXECUTED");
                break;
        }
        Log.d(TAG, "getCreditLimitValue" + isR2Executed + isR3Executed + isR4Executed);

        if (isR2Executed && isR3Executed && isR4Executed) {
            getCreditLimit();
        }
        /*if (currentRequest == totalRequest && (requestCode == 2 || requestCode == 3)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.isEmpty(errorDetailsMsg)) {
                        soItemLoaded(appItemBean, true);
                    } else {
                        hideProgressDialog();
                        ConstantsUtils.displayErrorDialog(SOApproveActivity.this, errorDetailsMsg);
                    }
                }
            });

        }*/

    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, int operation, int requestCode, String resourcePath, final String errorMsg, Bundle bundle) {
        Log.d(TAG, "response came response failed" + errorMsg);
        currentRequest++;
        errorDetailsMsg = errorMsg;
        if (currentRequest == totalRequest) {
            try {
                closeProgDailog();
                ((Activity) SOApproveActivity.this).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ConstantsUtils.displayErrorDialog(SOApproveActivity.this, errorMsg);
                    }
                });

            } catch (Exception e) {
                LogManager.writeLogError(Constants.error_txt + " : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void closeProgDailog() {
        try {
            hideProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void soItemLoaded(SalesApprovalBean appBean, Boolean isStoreOpened) {
        hideProgressDialog();
        LogManager.writeLogInfo("Checking Store Open : " + isStoreOpened);
        if (!isStoreOpened) {
            if (Constants.writeDebug) {
                LogManager.writeLogInfo("So Aprrove  Item Detail failed: Store opened failed");
            }
            ConstantsUtils.dialogBoxWithButton(SOApproveActivity.this, "", Constants.makeMsgReqError(Constants.ErrorNo, SOApproveActivity.this, false), getString(R.string.ok), "", null);
        } else {

            Intent i = new Intent(SOApproveActivity.this, ApprovalListDetails.class);
            i.putExtra("Header", appBean.getSoHeaderBeanArrayList());
            i.putExtra("Items", appBean.getSoItemBeanArrayList());
            i.putExtra("ApprovalHistory", appBean.getTaskHistorysArrayList());
            i.putExtra(Constants.EXTRA_SO_CREDIT_LIMIT, creditLimitBean);
            i.putExtra(Constants.RetailerName, selectedSOItem.getCustomerName());
            i.putExtra(Constants.CPUID, selectedSOItem.getCustomerNo());
            i.putExtra(Constants.EXTRA_SO_INSTANCE_ID, selectedSOItem.getInstanceID());
            startActivity(i);
            //finish();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        isR2Executed = false;
        isR3Executed = false;
        isR4Executed = false;
        isClicked = false;
        displayRefreshTime(refreshTime);
        if (isRefresh) {
            Log.d(TAG, "onStart: true");
            soBeanList.clear();
            soBeanSearchList.clear();
//            soApprovalAdapter.notifyDataSetChanged();
            simpleRecyclerViewAdapter.refreshAdapter(soBeanList);
            noDataFound.setVisibility(View.GONE);
            openStore();
            isRefresh = false;
        } else {
            Log.d(TAG, "onStart: false");
        }
    }

    @Override
    public void onRequestError(int i, Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgressDialog();
            }
        });
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
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
                    //     Constants.updateSyncTime(alAssignColl, this, Constants.DownLoad);
                } catch (Exception exce) {
                    LogManager.writeLogError(Constants.SyncTableHistory + exce.getMessage());
                }
//                getCreditLimitValue();
                getCreditLimit();

            }
        }
    }


    @Override
    public void onItemClicks(View view, int item) {
        selectedSOItem = soBeanSearchList.get(item);
        Log.d(TAG, "Before getting SO Details ");
        showProgressDialog();

        if (UtilConstants.isNetworkAvailable(SOApproveActivity.this)) {
            Log.d(TAG, "onItemClicks");
            if (Constants.writeDebug)
                LogManager.writeLogInfo("SOApproval : onItemClick");
//            refreshCreditLimit();
            getCreditLimitValue();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getComponentName());
        MenuItem dateFilter = menu.findItem(R.id.filter);
        dateFilter.setVisible(false);
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.so_ap_search_hint));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                soApprovalAdapter.filter(query);
                soApprovalPresenter.onSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                soApprovalAdapter.filter(newText);
                soApprovalPresenter.onSearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onRefresh() {
        openStore();
    }

    private void getCreditLimitValue() {
        Log.d(TAG, "getCreditLimitValue ");
        errorDetailsMsg = "";
        /*if(!Constants.getRollID(this)) {
            String qry = Constants.CustomerCreditLimits + "?$select=BalanceAmount,Currency&$filter=" + Constants.Customer + " eq '" + selectedSOItem.getCustomerNo() + "'";
            final List<CreditLimitBean> limitBeanList = OfflineManager.getCreditLimit(qry);
            if (!limitBeanList.isEmpty()) {
                creditLimitBean = limitBeanList.get(0);
            }
        }*/
        if (UtilConstants.isNetworkAvailable(SOApproveActivity.this)) {

            boolean isStoreOpened = true;
            //    if (isStoreOpened){
            String soQry = Constants.SOs + "('" + selectedSOItem.getSONo() + "')?$expand=SOItemDetails,SOConditions";

            try {

                OnlineManager.doOnlineGetRequest(soQry, this, iReceiveEvent -> {
                    if (iReceiveEvent.getResponseStatusCode()==200){
                        JSONObject jsonObject = OnlineManager.getJSONBody(iReceiveEvent);
                        JSONArray jsonArray = OnlineManager.getJSONArrayBody(jsonObject);
                        try {
                            Log.e(TAG, "Request Success SO Items Details");
                            LogManager.writeLogInfo("Request Success SO Items Details");
                            appItemBean = OnlineManager.getSOHeaderList(appItemBean, jsonObject, SOApproveActivity.this);
                        } catch (OnlineODataStoreException e) {
                            e.printStackTrace();
                        }
                        currentRequest++;
                        isR2Executed = true;
                        Log.d(TAG, "getCreditLimitValue response 1 ");

                        Log.d("REQUEST", "R2 EXECUTED");

                        String taskQry=Constants.Tasks + "(InstanceID='" + selectedSOItem.getInstanceID() + "',EntityType='SO')?$expand=TaskDecisions,TaskHistorys";

                        try {

                            OnlineManager.doOnlineGetRequest(taskQry, this, iReceiveEvent1 -> {
                                if (iReceiveEvent1.getResponseStatusCode()==200){
                                    JSONObject jsonObject1 = OnlineManager.getJSONBody(iReceiveEvent1);
                                    Log.e(TAG, "Request Success Tasks");
                                    LogManager.writeLogInfo("Request Success Tasks");
                                    appItemBean = OnlineManager.getTaskHistoryList(appItemBean, jsonObject1, SOApproveActivity.this);
                                    currentRequest++;
                                    isR3Executed = true;
                                    Log.d("REQUEST", "R3 EXECUTED");
                                    Log.d(TAG, "getCreditLimitValue response 2 ");
                                    refreshCreditLimit();
                                }else {
                                    String errorMsg="";
                                    try {
                                        errorMsg = Constants.getErrorMessage(iReceiveEvent,this);
                                        String finalErrorMsg = errorMsg;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                hideProgressDialog();
                                                Toast.makeText(SOApproveActivity.this, finalErrorMsg, Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        LogManager.writeLogError(errorMsg);
                                    } catch (Throwable e) {
                                        e.printStackTrace();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                hideProgressDialog();
                                                Toast.makeText(SOApproveActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        LogManager.writeLogError(e.getMessage());
                                    }
                                }
                            }, e -> {
                                isR3Executed = false;
                                e.printStackTrace();
                                String errormessage = "";
                                errormessage = ConstantsUtils.geterrormessageForInternetlost(e.getMessage(),this);
                                if(TextUtils.isEmpty(errormessage)){
                                    errormessage = e.getMessage();
                                }
                                String finalErrormessage = errormessage;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideProgressDialog();
                                        Toast.makeText(SOApproveActivity.this, finalErrormessage, Toast.LENGTH_LONG).show();
                                    }
                                });
                            });
                        } catch (Exception e) {
                            isR3Executed = false;
                            e.printStackTrace();
                            ConstantsUtils.printErrorLog(e.getMessage());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideProgressDialog();
                                    Toast.makeText(SOApproveActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }else {
                        String errorMsg="";
                        try {
                            errorMsg = Constants.getErrorMessage(iReceiveEvent,this);
                            String finalErrorMsg = errorMsg;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideProgressDialog();
                                    Toast.makeText(SOApproveActivity.this, finalErrorMsg, Toast.LENGTH_LONG).show();
                                }
                            });
                            LogManager.writeLogError(errorMsg);
                        } catch (Throwable e) {
                            e.printStackTrace();
                            String error=e.getMessage();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideProgressDialog();
                                    Toast.makeText(SOApproveActivity.this,error , Toast.LENGTH_LONG).show();
                                }
                            });
                            LogManager.writeLogError(e.getMessage());
                        }
                    }
                }, e -> {
                    isR2Executed=false;
                    e.printStackTrace();
                    String errormessage = "";
                    errormessage = ConstantsUtils.geterrormessageForInternetlost(e.getMessage(),this);
                    if(TextUtils.isEmpty(errormessage)){
                        errormessage = e.getMessage();
                    }
                    String finalErrormessage = errormessage;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressDialog();
                            Toast.makeText(SOApproveActivity.this, finalErrormessage, Toast.LENGTH_LONG).show();
                        }
                    });

                });
            } catch (Exception e) {
                isR2Executed=false;
                e.printStackTrace();
                ConstantsUtils.printErrorLog(e.getMessage());
                String error=e.getMessage();
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG);runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                        Toast.makeText(SOApproveActivity.this,error , Toast.LENGTH_LONG).show();
                    }
                });
            }

            /*totalRequest = 0;
            currentRequest = 0;
            requestbundle =  new Bundle();
            requestbundle.putString(Constants.BUNDLE_RESOURCE_PATH, Constants.SOs + "('" + selectedSOItem.getSONo() + "')?$expand=SOItemDetails,SOConditions");
            requestbundle.putInt(Constants.BUNDLE_REQUEST_CODE, 2);
            requestbundle.putInt(Constants.BUNDLE_OPERATION, Operation.GetRequest.getValue());
            requestbundle.putBoolean(Constants.BUNDLE_SESSION_REQUIRED, true);
            requestbundle.putBoolean(Constants.BUNDLE_SESSION_URL_REQUIRED, true);
            try {
                totalRequest++;
                Log.d(TAG, "Requesting for SOItemDetails");
                if (Constants.writeDebug) {
                    LogManager.writeLogInfo("Requesting for SOItemDetails : URL " + Constants.SOs + "('" + selectedSOItem.getSONo() + "')?$expand=SOItemDetails,SOConditions");
                }

                try {
                    *//*OnlineStoreListener openListener = OnlineStoreListener.getInstance();
                    OnlineODataStore store = openListener.getStore();
                    Log.d("ApprovalList","Opening Store");
                    if(store==null) {
                        new OpenOnlineManagerStore(SOApproveActivity.this, new AsyncTaskCallBack() {
                            @Override
                            public void onStatus(boolean status, String values) {
                                Log.d(TAG, "onStatus: OnlineStore" + status);
                                try {
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                    LogManager.writeLogError(Constants.error_txt + e1.getMessage());
                                }
                                if (status) {
                                    LogManager.writeLogInfo("Online Store opened");
                                    try {
                                        OnlineManager.requestOnline(SOApproveActivity.this, requestbundle, SOApproveActivity.this);
                                        requestbundle = new Bundle();
                                        requestbundle.putString(Constants.BUNDLE_RESOURCE_PATH, Constants.Tasks + "(InstanceID='" + selectedSOItem.getInstanceID() + "',EntityType='SO')?$expand=TaskDecisions,TaskHistorys");
                                        requestbundle.putInt(Constants.BUNDLE_REQUEST_CODE, 3);
                                        requestbundle.putInt(Constants.BUNDLE_OPERATION, Operation.GetRequest.getValue());
                                        requestbundle.putBoolean(Constants.BUNDLE_SESSION_REQUIRED, true);
                                        requestbundle.putBoolean(Constants.BUNDLE_SESSION_URL_REQUIRED, true);
                                        try {
                                            totalRequest++;
                                            Log.d(TAG, "Requesting for Tasks");
                                            if (Constants.writeDebug) {
                                                LogManager.writeLogInfo("SOApproval : Requesting for  Tasks : URL :" + Constants.Tasks + "(InstanceID='" + selectedSOItem.getInstanceID() + "',EntityType='SO')?$expand=TaskDecisions,TaskHistorys");
                                            }


                                            OnlineManager.requestOnline(SOApproveActivity.this, requestbundle, SOApproveActivity.this);
                                            Log.d(TAG, "getCreditLimitValue request 2 " + "--" + totalRequest);

                                        } catch (Exception e) {
                                            totalRequest--;
                                            LogManager.writeLogError(Constants.error_txt1 + " : " + e.getMessage());
                                            e.printStackTrace();
                                            closeProgDailog();
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                }
                            }
                        }).execute();
                    }else{*//*
                        OnlineManager.requestOnline(SOApproveActivity.this, requestbundle, SOApproveActivity.this);
                        requestbundle = new Bundle();
                        requestbundle.putString(Constants.BUNDLE_RESOURCE_PATH, Constants.Tasks + "(InstanceID='" + selectedSOItem.getInstanceID() + "',EntityType='SO')?$expand=TaskDecisions,TaskHistorys");
                        requestbundle.putInt(Constants.BUNDLE_REQUEST_CODE, 3);
                        requestbundle.putInt(Constants.BUNDLE_OPERATION, Operation.GetRequest.getValue());
                        requestbundle.putBoolean(Constants.BUNDLE_SESSION_REQUIRED, true);
                        requestbundle.putBoolean(Constants.BUNDLE_SESSION_URL_REQUIRED, true);
                        try {
                            totalRequest++;
                            Log.d(TAG, "Requesting for Tasks");
                            if (Constants.writeDebug) {
                                LogManager.writeLogInfo("SOApproval : Requesting for  Tasks : URL :" + Constants.Tasks + "(InstanceID='" + selectedSOItem.getInstanceID() + "',EntityType='SO')?$expand=TaskDecisions,TaskHistorys");
                            }


                            OnlineManager.requestOnline(SOApproveActivity.this, requestbundle, SOApproveActivity.this);
                            Log.d(TAG, "getCreditLimitValue request 2 " + "--" + totalRequest);

                        } catch (Exception e) {
                            totalRequest--;
                            LogManager.writeLogError(Constants.error_txt1 + " : " + e.getMessage());
                            e.printStackTrace();
                            closeProgDailog();
                        }

                  *//*  }*//*



                } catch (Exception e) {
                    e.printStackTrace();
                }

              *//*  OnlineStoreListener openListener = OnlineStoreListener.getInstance();
                OnlineODataStore store = openListener.getStore();
                if (store == null) {*//*
             *//*  try {
 //                       isStoreOpened = OnlineManager.openOnlineStore(SOApproveActivity.this, true);
                        new OpenOnlineManagerStore(SOApproveActivity.this, new AsyncTaskCallBack() {
                            @Override
                            public void onStatus(boolean status, String values) {
                                Log.d(TAG, "onStatus: OnlineStore" + status);

                                if (status) {
                                    LogManager.writeLogInfo("Online Store opened");
                                    try {
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                //Do something after 100ms

                                            }
                                        }, 4000);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    closeProgDailog();
                                    UtilConstants.showAlert("Online Store not opened ,Please check your internet connection and try again", SOApproveActivity.this);
                                }
                            }
                        }).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*//*

             *//*if (isStoreOpened)
                    OnlineManager.requestOnline(SOApproveActivity.this, requestbundle, SOApproveActivity.this);
                else {
                    closeProgDailog();
                    UtilConstants.showAlert("Online Store not opened ,Please check your internet connection and try again", SOApproveActivity.this);
                }*//*
                Log.d(TAG, "getCreditLimitValue request 1 ");

            } catch (Exception e) {
                totalRequest--;
                LogManager.writeLogError(Constants.error_txt1 + " : " + e.getMessage());
                e.printStackTrace();
                closeProgDailog();
            }*/


                   /* }else{
                        closeProgDailog();
                        UtilConstants.showAlert("Online Store not opened ,Please check your internet connection and try again",SOApproveActivity.this);
                    }*/


//            new GetDataFromOnlineDB(SOApproveActivity.this, selectedSOItem, asyncTaskCallBack).execute();
        }
    }

    private void refreshCreditLimit() {
        String creditControID = "";
        try {
            SalesOrderBean salesOrderBean = appItemBean.getSoHeaderBeanArrayList().get(0);
            String divisionID = salesOrderBean.getSalesArea();
            String qry = Constants.CustomerSalesAreas + "?$filter=" + Constants.CustomerNo + " eq '" + selectedSOItem.getCustomerNo() + "' and SalesArea eq '" + divisionID + "'";
            creditControID = OfflineManager.getCreditControlArea(qry);
        } catch (Exception e) {
            e.printStackTrace();
        }

       // ArrayList<String> salesArea = null;
        /*try {
            salesArea = OfflineManager.getSaleAreaFromUsrAth("UserProfileAuthSet?$filter=Application%20eq%20%27PD%27" + " &$orderby=AuthOrgTypeID asc");
        } catch (Exception e) {
            e.printStackTrace();
        }*/


        ArrayList<String> salesArea = new ArrayList<>();
        try {
            salesArea = OfflineManager.getSalesArea("UserProfileAuthSet?$filter=Application%20eq%20%27PD%27" + " &$orderby=AuthOrgTypeID asc");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<String> distributorChannel = new ArrayList<>();
        try {
            distributorChannel = OfflineManager.getDistibuterChannelIds("UserProfileAuthSet?$filter=Application%20eq%20%27PD%27" + " and AuthOrgTypeID eq '000008'");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<String> salesArearlist = new ArrayList<>();
        salesArearlist.clear();

        for (int i = 0; i < distributorChannel.size(); i++) {
            for (int j = 0; j < salesArea.size(); j++) {
                String saleArea[] = salesArea.get(j).split("/");

                salesArearlist.add(saleArea[0] + "/" + distributorChannel.get(i) + "/" + saleArea[1]);

                System.out.println("Sales Area List : " + saleArea[0] + "/" + distributorChannel.get(i) + "/" + saleArea[1]);

            }

        }


        String stringSaleArea = "";
        if (salesArearlist != null && !salesArearlist.isEmpty()) {
            for (int z = 0; z < salesArearlist.size(); z++) {
                if (z == salesArearlist.size() - 1) {
                    stringSaleArea = stringSaleArea + "SalesArea%20eq%20'" + salesArearlist.get(z) + "'";
                } else {
                    stringSaleArea = stringSaleArea + "SalesArea%20eq%20'" + salesArearlist.get(z) + "'%20or%20";
                }
            }
        }
        String creditControlAreas = "";
        if (!TextUtils.isEmpty(stringSaleArea)) {
            String qry = Constants.CustomerSalesAreas + "?$filter=" + Constants.CustomerNo + "%20eq%20'" + selectedSOItem.getCustomerNo() + "'%20and%20(" + stringSaleArea + ")";
            System.out.println("CustomerSalesAreas 5"+qry);

            ArrayList<SaleAreaBean> sortList =OfflineManager.getSaleAreaFromCustomerCreditLmt(qry);
            Collections.sort(sortList, new Comparator<SaleAreaBean>() {
                public int compare(SaleAreaBean one, SaleAreaBean other) {
                    return one.getCreditControlAreaID().compareTo(other.getCreditControlAreaID());
                }
            });
            //  customerBean.setSaleAreaBeanAl(sortList);

            if (sortList != null && !sortList.isEmpty()) {
                for (int z = 0; z < sortList.size(); z++) {
                    if (z == sortList.size() - 1) {
                        creditControlAreas = creditControlAreas + "CreditControlAreaID%20eq%20'" + sortList.get(z).getCreditControlAreaID() + "'";
                    } else {
                        creditControlAreas = creditControlAreas + "CreditControlAreaID%20eq%20'" + sortList.get(z).getCreditControlAreaID() + "'%20or%20";
                    }
                }
            }
        }


        String qry = "";
        if(!TextUtils.isEmpty(creditControlAreas)){
            qry= Constants.CustomerCreditLimits + "?$select=BalanceAmount,Currency,CreditControlAreaID&$filter=" + Constants.Customer + "%20eq%20'" + selectedSOItem.getCustomerNo() + "'%20and%20" + creditControlAreas + ")";
        }else {
            qry = Constants.CustomerCreditLimits + "?$select=BalanceAmount,Currency,CreditControlAreaID&$filter=" + Constants.Customer + "%20eq%20'" + selectedSOItem.getCustomerNo() + "'";
        }

//        if(Constants.getRollID(this)) {
        try {
//                String qry = Constants.CustomerCreditLimits + "?$select=BalanceAmount,Currency&$filter=" + Constants.Customer + " eq '" + selectedSOItem.getCustomerNo() + "'";
            if (UtilConstants.isNetworkAvailable(SOApproveActivity.this)) {
                try {

                    OnlineManager.doOnlineGetRequest(qry, this, iReceiveEvent1 -> {
                        if (iReceiveEvent1.getResponseStatusCode()==200){
                            JSONObject jsonObject1 = OnlineManager.getJSONBody(iReceiveEvent1);
                            JSONArray jsonArray = OnlineManager.getJSONArrayBody(jsonObject1);
                            List<CreditLimitBean> limitBeanList = null;
                            try {
                                Log.e(TAG, "Response Success for Customer Credit Limit");
                                LogManager.writeLogInfo("Response Success for Customer Credit Limit");
                                limitBeanList = OfflineManager.getCreditLimitOnline(jsonArray);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (!limitBeanList.isEmpty()) {
                                creditLimitBean.clear();
                                for (CreditLimitBean data :limitBeanList)
                                    creditLimitBean.add(data);
//                    getCreditLimitValue();
//                    getCreditLimit();
                            }
                            currentRequest++;
                            isR4Executed = true;
                            Log.d("REQUEST", "R4 EXECUTED");
                            if (isR2Executed && isR3Executed && isR4Executed) {
                                getCreditLimit();
                            }
                        }else {
                            String errorMsg="";
                            try {
                                errorMsg = Constants.getErrorMessage(iReceiveEvent1,this);
                                String finalErrorMsg = errorMsg;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideProgressDialog();
                                        Toast.makeText(SOApproveActivity.this, finalErrorMsg, Toast.LENGTH_LONG).show();
                                    }
                                });
                                LogManager.writeLogError(errorMsg);
                            } catch (Throwable e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideProgressDialog();
                                        Toast.makeText(SOApproveActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });
                                LogManager.writeLogError(e.getMessage());
                            }
                        }
                    }, e -> {
                        isR4Executed = false;
                        e.printStackTrace();
                        String errormessage = "";
                        errormessage = ConstantsUtils.geterrormessageForInternetlost(e.getMessage(),this);
                        if(TextUtils.isEmpty(errormessage)){
                            errormessage = e.getMessage();
                        }
                        String finalErrormessage = errormessage;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideProgressDialog();
                                Toast.makeText(SOApproveActivity.this, finalErrormessage, Toast.LENGTH_LONG).show();
                            }
                        });

                    });
                } catch (Exception e) {
                    isR4Executed = false;
                    e.printStackTrace();
                    ConstantsUtils.printErrorLog(e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressDialog();
                            Toast.makeText(SOApproveActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }else {
//            try {
//                alAssignColl.add(Constants.CustomerCreditLimits);
//                String syncColl = TextUtils.join(", ", alAssignColl);
//                OfflineManager.refreshStoreSync(this, this, Constants.Fresh, syncColl);
//            } catch (final OfflineODataStoreException e) {
//                e.printStackTrace();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        hideProgressDialog();
//                    }
//                });
//            }
//        }
    }

    private void getCreditLimit() {
        if (isR2Executed && isR3Executed && isR4Executed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /*try {
                        String qryBlck = Constants.ZZInactiveCustBlks+"?$filter="+Constants.CustomerNo+" eq '"+selectedSOItem.getCustomerNo()+"'";
                        //+" and SalesArea eq '"+soListBean.getSalesArea()+"' and Application eq 'PO_CRT'

                        HashMap<String, String> blockCustomer = new HashMap<>();
                        String jSONStr ="";
                        try {
                            jSONStr= OfflineManager.checkBlockedCustomer(SOApproveActivity.this,qryBlck);
                        } catch (OfflineODataStoreException e) {
                            LogManager.writeLogError("isBlock Customer Error "+e.getMessage());
                            e.printStackTrace();
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(jSONStr);
                            JSONArray jsonItem = jsonObject.getJSONArray("configdata");
                            blockCustomer = Constants.getBlockCustomerKeyValues(jsonItem);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }*/
                    if ((appItemBean.getSoHeaderBeanArrayList() != null && appItemBean.getSoHeaderBeanArrayList().size() > 0)
                            && (appItemBean.getSoItemBeanArrayList() != null && appItemBean.getSoItemBeanArrayList().size() > 0)) {

                        if (TextUtils.isEmpty(errorDetailsMsg)) {
                            isR2Executed = false;
                            isR3Executed = false;
                            isR4Executed = false;
                            Log.d("REQUEST", "R2,R3,R4 DEFAULT");
                            soItemLoaded(appItemBean, true);
                        } else {
                            hideProgressDialog();
                            ConstantsUtils.displayErrorDialog(SOApproveActivity.this, errorDetailsMsg);
                        }

                    }else{

                        hideProgressDialog();
                        // ConstantsUtils.displayErrorDialog(SOApproveActivity.this, "Unable to fetch sales order details. Please try again");
                        AlertDialog.Builder builder = new AlertDialog.Builder(SOApproveActivity.this);
                        builder.setMessage("Unable to fetch sales order details. Please try again")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        dialog.dismiss();
                                        isR2Executed = false;
                                        isR3Executed = false;
                                        isR4Executed = false;
                                        Intent intent = new Intent(SOApproveActivity.this, SOApproveActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        startActivity(intent);
                                    }
                                });
                        builder.create().show();
                    }
                }
            });

        }
    }

    @Override
    public void displaySearchList(ArrayList<SOListBean> soItemList) {
        if (soItemList != null) {
            simpleRecyclerViewAdapter.refreshAdapter(soItemList);
        }
    }

    @Override
    public void showProgress() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void onItemClick(SOListBean soListBean, View view, int item) {
        //  if (!isClicked) {
        //   isClicked = true;
        selectedSOItem = soListBean;
        Log.d(TAG, "Before getting SO Details ");
        showProgressDialog();

        if (UtilConstants.isNetworkAvailable(SOApproveActivity.this)) {
            Log.d(TAG, "onItemClicks");
            LogManager.writeLogInfo("SOApproval : onItemClicks");
//            refreshCreditLimit();
            getCreditLimitValue();
        }
        //  }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new SOApprovalVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, SOListBean soListBean) {
//        SOListBean soBean = soBeanSearchList.get(position);
        try {
            ((SOApprovalVH) holder).tvOrderDate.setText(ConstantsUtils.convertDateIntoDDMMYYYY(soListBean.getOrderDate()));
            ((SOApprovalVH) holder).tvOrderId.setText(soListBean.getSONo());
            ((SOApprovalVH) holder).tvSOCCName.setText(soListBean.getCustomerName());
            ((SOApprovalVH) holder).tvSOValue.setText(ConstantsUtils.commaSeparator(soListBean.getEntityValue1(), soListBean.getEntityCurrency()) + " " + soListBean.getEntityCurrency());

            System.out.println("tvSOValue:"+ConstantsUtils.commaSeparator(soListBean.getEntityValue1(), soListBean.getEntityCurrency()) + " " + soListBean.getEntityCurrency());

            Drawable img = displayImage(soListBean.getEntityType(), this);
            if (img != null) {
                ((SOApprovalVH) holder).ivStatus.setImageDrawable(img);
            }
//        holder.tvSOCC.setText(soBean.getQuantity());
      /*  if (soBean.getDelvStatus().equals("1")) {
            holder.tvPriority.setBackgroundResource(R.color.RED);
        } else if (soBean.getDelvStatus().equals("2")) {
            holder.tvPriority.setBackgroundResource(R.color.YELLOW);
        } else if (soBean.getDelvStatus().equals("3")) {
            holder.tvPriority.setBackgroundResource(R.color.GREEN);
        } else if (soBean.getDelvStatus().equals("4")) {
            holder.tvPriority.setBackgroundResource(R.color.ORANGE);
        } else if (soBean.getDelvStatus().equals("5")) {
            holder.tvPriority.setBackgroundResource(R.color.ORANGE);
        } else {
            holder.tvPriority.setBackgroundResource(android.R.color.transparent);
        }*/
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private Drawable displayImage(String entityType, Context mContext) {
        Drawable img = null;
        if (entityType.equalsIgnoreCase("SO")) {
            img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
            if (img != null)
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.PendingApprovalColor), PorterDuff.Mode.SRC_IN);
        } else {
            img = ContextCompat.getDrawable(mContext, R.drawable.ic_assignment_black_24dp).mutate();
            if (img != null)
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.PendingApprovalColor), PorterDuff.Mode.SRC_IN);
        }
        return img;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
