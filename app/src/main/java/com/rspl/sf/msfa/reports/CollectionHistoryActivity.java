package com.rspl.sf.msfa.reports;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.TraceLog;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.rspl.sf.msfa.BuildConfig;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.ViewPagerTabAdapter;
import com.rspl.sf.msfa.asyncTask.RefreshAsyncTask;
import com.rspl.sf.msfa.asyncTask.SyncFromDataValtAsyncTaskAll;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.UpdateListener;
import com.rspl.sf.msfa.interfaces.MessageWithBooleanCallBack;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;

/**
 * Created by e10526 on 27-04-2016.
 */
public class CollectionHistoryActivity extends AppCompatActivity implements View.OnClickListener,
        UIListener, UpdateListener, MessageWithBooleanCallBack {
    //new
    public static UpdateListener updateListener = null;
    public TextView tv_last_sync_time_value;
    ListView lv_coll_his_list = null;
    TextView tvEmptyLay = null;
    String concatCollectionStr = "";
    ArrayList<String> alAssignColl = new ArrayList<>();
    ProgressDialog syncProgDialog = null;
    boolean dialogCancelled = false;
    MenuItem menu_refresh, menu_sync;
    Menu menu;
    CollectionHistoryDeviceFragment deviceFragment;
    CollectionHistoryFragment historyFragment;
    ViewPagerTabAdapter viewPagerAdapter;
    private ArrayList<CollectionHistoryBean> alCollectionBean;
    private String mStrBundleRetID = "";
    private String mStrBundleRetUID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    private TabLayout tabLayout;
    //This is viewPager for collection sections device/normal
    private ViewPager viewPager;
    private GUID refguid =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        setContentView(R.layout.activity_collection_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_collections), 0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
        }
        initUI();
    }

    /*Initializes UI*/
    void initUI() {
        tv_last_sync_time_value = (TextView) findViewById(R.id.tv_last_sync_time_value);
        tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.FinancialPostings, Constants.TimeStamp, this));

        lv_coll_his_list = (ListView) findViewById(R.id.lv_coll_list);
        Spinner spnr_so_status = (Spinner) findViewById(R.id.spnr_so_status);
        spnr_so_status.setVisibility(View.GONE);

        //new
        tvEmptyLay = (TextView) findViewById(R.id.tv_empty_lay);

        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);

        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetUID);

        if (menu_refresh != null && menu_sync != null) {
            menu_sync.setVisible(false);
            menu_refresh.setVisible(true);

        }

        tabInitialize();
    }

    /*Initialize tab for collections*/
    private void tabInitialize() {

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        tabLayout.setupWithViewPager(viewPager);
    }

    /*Setting up ViewPager for Tabs*/
    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerTabAdapter(getSupportFragmentManager());

        historyFragment = new CollectionHistoryFragment();
        deviceFragment = new CollectionHistoryDeviceFragment();

        Bundle bundleVisit = new Bundle();
        bundleVisit.putString(Constants.CPGUID, mStrBundleCPGUID);
        bundleVisit.putString(Constants.CPNo, mStrBundleRetID);
        bundleVisit.putString(Constants.RetailerName, mStrBundleRetName);
        bundleVisit.putString(Constants.CPUID, mStrBundleRetUID);
        historyFragment.setArguments(bundleVisit);
        deviceFragment.setArguments(bundleVisit);

        viewPagerAdapter.addFrag(historyFragment, Constants.History);
        viewPagerAdapter.addFrag(deviceFragment, Constants.PendingSync);
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (menu_refresh != null && menu_sync != null) {
                    if (position == 0) {
                        menu_sync.setVisible(false);
                        menu_refresh.setVisible(true);
                    } else if (position == 1) {
                        if (checkDeviceCollectionsAvailable()) {
                            menu_sync.setVisible(true);
                            menu_refresh.setVisible(false);
                        } else {
                            menu_sync.setVisible(false);
                            menu_refresh.setVisible(false);
                        }

                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //Check Device Collection is Available Or Null
    public boolean checkDeviceCollectionsAvailable() {
        try {
            alCollectionBean = OfflineManager.getDevCollHisList(CollectionHistoryActivity.this, mStrBundleCPGUID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        if (alCollectionBean != null && alCollectionBean.size() > 0) {

            return true;
        } else {

            return false;

        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_coll_his_list, menu);

        menu_refresh = menu.findItem(R.id.menu_refresh_coll);
        menu_sync = menu.findItem(R.id.menu_sync_coll);

        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_coll_his_list, menu);
        menu_refresh = menu.findItem(R.id.menu_refresh_coll);
        menu_sync = menu.findItem(R.id.menu_sync_coll);
        menu_refresh.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_sync_coll:
                onSync();
                break;
            case R.id.menu_refresh_coll:
                onRefresh();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }

    /*onSync of post device collection and refresh collections*/
    private void onSync() {
        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
            if (Constants.iSAutoSync) {
                UtilConstants.showAlert(getString(R.string.alert_auto_sync_is_progress), CollectionHistoryActivity.this);
            } else {

                onSyncCollection();
            }
        } else {
            UtilConstants.showAlert(getString(R.string.no_network_conn), CollectionHistoryActivity.this);
        }
    }

    public String[] tempCollDevList = null;
    public int isFromWhere = 0;
    public int pendingCollVal = 0;
    public int penCollReqCount = 0;
    public int mError = 0;

    private void onSyncCollection() {

        try {
            mError = 0;
            isFromWhere = 3;
            alCollectionBean.clear();

            Constants.Entity_Set.clear();
            Constants.AL_ERROR_MSG.clear();

            alCollectionBean = OfflineManager.getDevCollHisList(CollectionHistoryActivity.this, mStrBundleCPGUID);
            if (!alCollectionBean.isEmpty()) {

                if (UtilConstants.isNetworkAvailable(getApplicationContext())) {

                    alAssignColl.clear();
//                    if (!alAssignColl.contains(Constants.INVOICES)) {
//                        alAssignColl.add(Constants.InvoiceItemDetails);
//                        alAssignColl.add(Constants.INVOICES);
//                    }
//                    if (!alAssignColl.contains(Constants.Collections)) {
//                        alAssignColl.add(Constants.Collections);
//                        alAssignColl.add(Constants.CollectionItemDetails);
//                    }

                    if (!alAssignColl.contains(Constants.OutstandingInvoices)) {
                        alAssignColl.add(Constants.OutstandingInvoiceItemDetails);
                        alAssignColl.add(Constants.OutstandingInvoices);
                    }

                    pendingCollVal = 0;
                    if (tempCollDevList != null) {
                        tempCollDevList = null;
                        penCollReqCount = 0;
                    }

                    Constants.mBoolIsReqResAval = true;
                    Constants.mBoolIsNetWorkNotAval = false;

                    if (alCollectionBean != null && alCollectionBean.size() > 0) {
                        tempCollDevList = new String[alCollectionBean.size()];

                        for (CollectionHistoryBean returnOrderBean : alCollectionBean) {
                            tempCollDevList[pendingCollVal] = returnOrderBean.getDeviceNo();
                            pendingCollVal++;
                        }
                        syncProgDialog = Constants.showProgressDialog(CollectionHistoryActivity.this, "", getString(R.string.msg_sync_progress_msg_plz_wait));
                        refguid = GUID.newRandom();
                        Constants.updateStartSyncTime(CollectionHistoryActivity.this,Constants.UpLoad,Constants.StartSync,refguid.toString().toUpperCase());
                        new SyncFromDataValtAsyncTaskAll(CollectionHistoryActivity.this, tempCollDevList, this, this).execute();
                    }
                } else {
                    UtilConstants.showAlert(getString(R.string.no_network_conn), this);
                }


            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                onBackPressed();
                break;
        }
    }

    /*refresh collections*/
    void onRefresh() {

        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
            isFromWhere = 0;
            mError = 0;
            alAssignColl.clear();
            concatCollectionStr = "";
            Constants.Entity_Set.clear();
            Constants.AL_ERROR_MSG.clear();
            alAssignColl.add(Constants.CollectionItemDetails);
            alAssignColl.add(Constants.Collections);
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
            if (Constants.iSAutoSync) {
                UtilConstants.showAlert(getString(R.string.alert_auto_sync_is_progress), CollectionHistoryActivity.this);
            } else {
                try {
                    Constants.isSync = true;
                    dialogCancelled = false;
                    new LoadingData().execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            UtilConstants.showAlert(getString(R.string.no_network_conn), CollectionHistoryActivity.this);
        }

    }

    private void closingPrgDialog() {
        try {
            syncProgDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestError(int operation, Exception exception) {
        ErrorBean errorBean = Constants.getErrorCode(operation, exception, CollectionHistoryActivity.this);
        if (errorBean.hasNoError()) {
            penCollReqCount++;
            mError++;
            Constants.mBoolIsReqResAval = true;
            if ((operation == Operation.Create.getValue()) && (penCollReqCount == pendingCollVal)) {
                Constants.isSync = false;
                displayErrorDialog(errorBean.getErrorMsg());
                getRefreshFragment(1);

            } else if (operation == Operation.OfflineRefresh.getValue()) {

                Constants.isSync = false;
                closingPrgDialog();
                displayErrorDialog(errorBean.getErrorMsg());
                if (isFromWhere == 3) {
                    getRefreshFragment(1);
                } else {
                    getRefreshFragment(0);
                }
            }
        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(this)) {
                Constants.isSync = true;
                syncProgDialog = new ProgressDialog(CollectionHistoryActivity.this, R.style.ProgressDialogTheme);
                syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
                syncProgDialog.setCancelable(true);
                syncProgDialog.setCanceledOnTouchOutside(false);
                syncProgDialog.show();
                new RefreshAsyncTask(this, "", this).execute();
            } else {
                Constants.isSync = false;
                closingPrgDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), this);
            }
        } else {
            Constants.isSync = false;
            Constants.mBoolIsReqResAval = true;
            Constants.mBoolIsNetWorkNotAval = true;
            closingPrgDialog();
            Constants.displayMsgReqError(errorBean.getErrorCode(), CollectionHistoryActivity.this);

            if (isFromWhere == 3) {
                getRefreshFragment(1);
            } else {
                getRefreshFragment(0);
            }
        }
    }

    private void displayErrorDialog(String errMsg) {
        String mErrorMsg = "";
        if (Constants.AL_ERROR_MSG.size() > 0) {
            mErrorMsg = Constants.convertALBussinessMsgToString(Constants.AL_ERROR_MSG);
        }
        if (mErrorMsg.equalsIgnoreCase("")) {
            syncProgDialog.dismiss();
            UtilConstants.showAlert(errMsg.equalsIgnoreCase("") ? getString(R.string.msg_error_occured_during_sync) : errMsg, CollectionHistoryActivity.this);
        } else {
            Constants.customAlertDialogWithScroll(CollectionHistoryActivity.this, mErrorMsg);
        }
    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {

        if ((operation == Operation.Create.getValue()) && pendingCollVal > 0) {
            Constants.mBoolIsReqResAval = true;
            Constants.removeDeviceDocNoFromSharedPref(CollectionHistoryActivity.this, Constants.CollList, tempCollDevList[penCollReqCount]);
            ConstantsUtils.storeInDataVault(tempCollDevList[penCollReqCount], "",this);
            penCollReqCount++;
        }
        if ((operation == Operation.Create.getValue()) && (penCollReqCount == pendingCollVal)) {
            if (UtilConstants.isNetworkAvailable(CollectionHistoryActivity.this)) {
                try {
                    concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                    OfflineManager.refreshRequests(CollectionHistoryActivity.this, concatCollectionStr, this);
                } catch (OfflineODataStoreException e) {
                    TraceLog.e(Constants.SyncOnRequestSuccess, e);
                }
            } else {
                Constants.isSync = false;
                closingPrgDialog();
                UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), CollectionHistoryActivity.this);
                getRefreshFragment(1);
            }
        } else if (operation == Operation.OfflineRefresh.getValue()) {
            Constants.updateLastSyncTimeToTable(alAssignColl,this,Constants.UpLoad,refguid.toString().toUpperCase());
            tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.FinancialPostings, Constants.TimeStamp, this));
            closingPrgDialog();
            Constants.isSync = false;

            if (mError == 0) {
                UtilConstants.showAlert(getString(R.string.msg_sync_successfully_completed), CollectionHistoryActivity.this);
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, CollectionHistoryActivity.this, BuildConfig.APPLICATION_ID, false);
            } else {
                displayErrorDialog(getString(R.string.error_occured_during_post));
            }

            if (isFromWhere == 3) {
                getRefreshFragment(1);
            } else {
                getRefreshFragment(0);
            }
        }
    }

    /*refresh pager and fragments*/
    public void getRefreshFragment(int position) {

        viewPagerAdapter = new ViewPagerTabAdapter(getSupportFragmentManager());
        historyFragment = new CollectionHistoryFragment();
        deviceFragment = new CollectionHistoryDeviceFragment();

        tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.FinancialPostings, Constants.TimeStamp, this));

        Bundle bundleVisit = new Bundle();
        bundleVisit.putString(Constants.CPGUID, mStrBundleCPGUID);
        bundleVisit.putString(Constants.CPNo, mStrBundleRetID);
        bundleVisit.putString(Constants.RetailerName, mStrBundleRetName);
        bundleVisit.putString(Constants.CPUID, mStrBundleRetUID);
        historyFragment.setArguments(bundleVisit);
        deviceFragment.setArguments(bundleVisit);

        viewPagerAdapter.addFrag(historyFragment, Constants.Collections);
        viewPagerAdapter.addFrag(deviceFragment, Constants.DeviceCollectionsText);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(position);

    }

    @Override
    public void onUpdate() {
        getRefreshFragment(1);
    }

    /*AsyncTask for refresh collections*/
    public class LoadingData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(CollectionHistoryActivity.this, R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
            syncProgDialog.setCancelable(true);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();

            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    CollectionHistoryActivity.this, R.style.MyTheme);
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
                                            });
                            builder.show();
                        }
                    });
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                try {

                    OfflineManager.refreshStoreSync(getApplicationContext(), CollectionHistoryActivity.this, Constants.Fresh, concatCollectionStr);
                } catch (OfflineODataStoreException e) {
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

            if (syncProgDialog != null) {

                syncProgDialog.dismiss();
            }
        }
    }

    @Override
    public void clickedStatus(boolean clickedStatus, String err_msg,ErrorBean errorBean) {
        if (!clickedStatus) {
            closingPrgDialog();
            UtilConstants.showAlert(err_msg, CollectionHistoryActivity.this);
        }
    }
}
