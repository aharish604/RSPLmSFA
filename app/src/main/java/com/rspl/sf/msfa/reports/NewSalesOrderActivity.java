package com.rspl.sf.msfa.reports;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.ViewPagerTabAdapter;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NewSalesOrderActivity extends AppCompatActivity implements UIListener, DialogCallBack {

    public String[] tempRODevList = null;
    ArrayList<String> alAssignColl = new ArrayList<>();
    List<SalesOrderBean> SalesOrderBeanList = new ArrayList<>();
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";
    private String mStrBundleCPGUID = "";
    private String mStrComingFrom = "";
    private TextView retId;
    private TextView retName;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private static NewSalesOrderListFragment SalesOrderListFragmentLeft;
    private NewSalesOrderListFragment SalesOrderListFragmentRight;
    private ViewPagerTabAdapter viewPagerAdapter;
    private int comingFrom = 0;
    private String actionBarTitle = "";
    private String tabLeftTitle = "";
    private String tabRightTitle = "";
    private MenuItem menu_refresh = null;
    private MenuItem menu_sync = null;
    private Menu menu;
    private String concatCollectionStr = "";
    private boolean dialogCancelled = false;
    private ProgressDialog progressDialog = null;
    private Context mContext;
    private int penROReqCount = 0;
    private int pendingROVal = 0;
    private int isFromWhere = 0;

    Spinner spnrSOStatus = null;
    private String[][] arrayInvStatusVal;
    boolean isRefreshed = false;
    ProgressDialog pdLoadDialog = null;
    String selectedStatus = Constants.str_00;
    ArrayList<SalesOrderBean> salesOrderBeanArrayList = new ArrayList<>();
    ArrayList<SalesOrderBean> salesItemsBeanArrayList = new ArrayList<>();

    int defaultTabPos = 0;
    boolean isErrorFromBackend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sales_order_activty);
       // isCancelledOrChanged = false;
      //  ActionBarView.initActionBarView(this, true, getString(R.string.title_SalesOrderList));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_SalesOrderList), 0);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mContext = NewSalesOrderActivity.this;
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPUID);
//            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
            defaultTabPos = bundleExtras.getInt(Constants.EXTRA_TAB_POS,0);
        }
        tabLeftTitle = Constants.titleHistory;
        tabRightTitle = Constants.titlePending;


        retName = (TextView) findViewById(R.id.tv_reatiler_name);
        retId = (TextView) findViewById(R.id.tv_reatiler_id);
        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetUID);
        tabInitialize();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        if(SalesOrderListDetailsActivity.isCancelledOrChanged){
            new GetSOList().execute();
            SalesOrderListDetailsActivity.isCancelledOrChanged = false;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(SalesOrderListDetailsActivity.isCancelledOrChanged){
            getSOStatus();
            displaySOStatus();
            SalesOrderListDetailsActivity.isCancelledOrChanged = false;
        }
    }

    /*Initialize tab*/
    private void tabInitialize() {
        if (menu_refresh != null && menu_sync != null) {
            menu_sync.setVisible(false);
            menu_refresh.setVisible(true);
        }
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tabLayout.setupWithViewPager(viewPager);
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
                        spnrSOStatus.setVisibility(View.VISIBLE);
                    } else if (position == 1) {
                        if (checkSSSOrderAvailable()) {
                            menu_sync.setVisible(true);
                            menu_refresh.setVisible(false);
                        } else {
                            menu_sync.setVisible(false);
                            menu_refresh.setVisible(false);
                        }

                        spnrSOStatus.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        spnrSOStatus = (Spinner) findViewById(R.id.spnr_so_status);
        getSOStatus();
        displaySOStatus();
        viewPager.setCurrentItem(defaultTabPos, true);
    }

    /*get different status for invoices*/
    private void getSOStatus() {
        try {
            String mStrConfigQry = Constants.ConfigTypesetTypes + "?$filter=" + Constants.Typeset + " eq '" +
                    Constants.DELVST + "' &$orderby=" + Constants.Types + " asc";

            arrayInvStatusVal = OfflineManager.getConfigTysetTypesValues(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
        }
    }

    /*gets status for invoices*/
    private void displaySOStatus() {
        if (arrayInvStatusVal == null) {
            arrayInvStatusVal = new String[2][1];
            arrayInvStatusVal[0][0] = "";
            arrayInvStatusVal[1][0] = "";
        }

        String[][] tempStatusArray = new String[2][arrayInvStatusVal[0].length + 1];
        tempStatusArray[0][0] = Constants.str_00;
        tempStatusArray[1][0] = Constants.All;
        for (int i = 1; i < arrayInvStatusVal[0].length + 1; i++) {
            tempStatusArray[0][i] = arrayInvStatusVal[0][i - 1];
            tempStatusArray[1][i] = arrayInvStatusVal[1][i - 1];
        }
        arrayInvStatusVal = tempStatusArray;

        ArrayAdapter<String> productCategoryAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_textview,R.id.tvItemValue, arrayInvStatusVal[1]);
        productCategoryAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spnrSOStatus.setAdapter(productCategoryAdapter);


        spnrSOStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {

                selectedStatus = arrayInvStatusVal[0][position];

                new GetSOList().execute();
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    /*AsyncTask to get Retailers List*/
    private class GetSOList extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(NewSalesOrderActivity.this,R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {

            getSSSODataFromOfflineDb(selectedStatus);

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                pdLoadDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            SalesOrderListFragmentLeft.updateListForSelectedStatus(salesOrderBeanArrayList);
        }
    }



    /*get ssso data from offline db*/
    private void getSSSODataFromOfflineDb(String status) {
        salesOrderBeanArrayList.clear();
        String query = "";
        String soQuery = "";
        if (status.equalsIgnoreCase(Constants.str_00)) {
//            query = Constants.SOs + "?$filter=" + Constants.CustomerNo + " eq '" + mStrBundleRetID +
//                    "' &$orderby=" + Constants.SONo + " desc";

            soQuery = Constants.SOs + "?$filter=" + Constants.CustomerNo + " eq '" + mStrBundleRetID +
                    "' &$orderby=" + Constants.OrderDate + " desc";
            query = Constants.SOItems + "?$filter=" + Constants.CustomerNo + " eq '" +"000000"+mStrBundleRetID +
                    "' &$orderby=" + Constants.SONo + " desc";
        }
        else {
//            query = Constants.SOs + "?$filter=" + Constants.CustomerNo + " eq '" + mStrBundleRetID +
//                    "' and " + Constants.DelvStatus + " eq '" + status + "' &$orderby=" + Constants.SONo + " desc";
           /* query = Constants.SOItems + "?$filter=" + Constants.CustomerNo + " eq '" + mStrBundleRetID +
                    "' and " + Constants.DelvStatus + " eq '" + status + "' &$orderby=" + Constants.OrderDate + " desc"; */

         /*   query = Constants.SOItemDetails + "?$filter=" + Constants.CustomerNo + " eq '" + mStrBundleRetID +
                    "' and " + Constants.DelvStatus + " eq '" + status + "' &$orderby=" + Constants.OrderDate + " desc";*/
            query = Constants.SOItems + "?$filter=" + Constants.CustomerNo + " eq '" +"000000"+mStrBundleRetID +
                    "' and " + Constants.DelvStatusId + " eq '" + status +
                    "' &$orderby=" + Constants.SONo + " desc";

        }
        try {
            salesOrderBeanArrayList.clear();
            salesOrderBeanArrayList.addAll(OfflineManager.getNewSecondarySalesOrderList(
                    NewSalesOrderActivity.this, query, mStrBundleRetID, status));
            salesItemsBeanArrayList.addAll(OfflineManager.getSecondarySalesOrderList(
                    NewSalesOrderActivity.this, soQuery, mStrBundleRetID, status));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
//        if(!isRefreshed) {
//        }
//        else
//            isRefreshed = false;
    }

    private boolean checkSSSOrderAvailable() {
        boolean countNotZero = false;
        try {
            countNotZero = OfflineManager.getSSSoListAvailabilityFromDataValt(NewSalesOrderActivity.this, mStrBundleCPGUID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return countNotZero;
    }

    /*set up view page fragment*/
    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerTabAdapter(getSupportFragmentManager());
        SalesOrderListFragmentLeft = new NewSalesOrderListFragment();
        SalesOrderListFragmentRight = new NewSalesOrderListFragment();
        Bundle bundleLeft = new Bundle();
        bundleLeft.putString(Constants.CPGUID, mStrBundleCPGUID);
        bundleLeft.putString(Constants.CPNo, mStrBundleRetID);
        bundleLeft.putString(Constants.RetailerName, mStrBundleRetName);
        bundleLeft.putString(Constants.CPUID, mStrBundleRetUID);
        bundleLeft.putInt(Constants.comingFrom, comingFrom);
        bundleLeft.putInt(Constants.EXTRA_TAB_POS, Constants.TAB_POS_1);
        Bundle bundleRight = new Bundle();
        bundleRight.putString(Constants.CPGUID, mStrBundleCPGUID);
        bundleRight.putString(Constants.CPNo, mStrBundleRetID);
        bundleRight.putString(Constants.RetailerName, mStrBundleRetName);
        bundleRight.putString(Constants.CPUID, mStrBundleRetUID);
        bundleRight.putInt(Constants.EXTRA_TAB_POS, Constants.TAB_POS_2);
        SalesOrderListFragmentLeft.setArguments(bundleLeft);
        SalesOrderListFragmentRight.setArguments(bundleRight);
        viewPagerAdapter.addFrag(SalesOrderListFragmentLeft, tabLeftTitle);
        viewPagerAdapter.addFrag(SalesOrderListFragmentRight, tabRightTitle);
        viewPager.setAdapter(viewPagerAdapter);
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
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewSalesOrderActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_competition_information).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        NewSalesOrderActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_sync_coll:
                onSyncSOrder();
                break;
            case R.id.menu_refresh_coll:
                onRefreshSOrder();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }


    private void onSyncSOrder() {
        isErrorFromBackend = false;
        isFromWhere = 4;
        try {
            SalesOrderBeanList.clear();
            SalesOrderBeanList = OfflineManager.getSoListFromDataValt(NewSalesOrderActivity.this, mStrBundleCPGUID,false);
            if (!SalesOrderBeanList.isEmpty()) {

                if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                    alAssignColl.clear();
                    alAssignColl.add(Constants.SOs);
                    alAssignColl.add(Constants.SOItemDetails);
                    alAssignColl.add(Constants.SOItems);
                    alAssignColl.add(Constants.SOTexts);
                    alAssignColl.add(Constants.SOConditions);
                    pendingROVal = 0;
                    if (tempRODevList != null) {
                        tempRODevList = null;
                        penROReqCount = 0;
                    }

                    if (SalesOrderBeanList != null && SalesOrderBeanList.size() > 0) {
                        tempRODevList = new String[SalesOrderBeanList.size()];

                        for (SalesOrderBean SalesOrderBean : SalesOrderBeanList) {
                            tempRODevList[pendingROVal] = SalesOrderBean.getDeviceNo();
                            pendingROVal++;
                        }
                        progressDialog = Constants.showProgressDialog(NewSalesOrderActivity.this, "", getString(R.string.msg_sync_progress_msg_plz_wait));
                        new SyncFromDataValtAsyncTask(NewSalesOrderActivity.this, tempRODevList, this, this).execute();
                    }
                } else {
                    UtilConstants.showAlert(getString(R.string.no_network_conn), this);
                }


            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void onRefreshSOrder() {
        isErrorFromBackend = false;
        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
            isFromWhere = 2;
            alAssignColl.clear();
            concatCollectionStr = "";
            alAssignColl.add(Constants.SOs);
            alAssignColl.add(Constants.SOItemDetails);
            alAssignColl.add(Constants.SOItems);
            alAssignColl.add(Constants.SOTexts);
            alAssignColl.add(Constants.SOConditions);
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
                UtilConstants.showAlert(getString(R.string.alert_auto_sync_is_progress), NewSalesOrderActivity.this);
            } else {
                try {
                    Constants.isSync = true;
                    dialogCancelled = false;
                    progressDialog = Constants.showProgressDialog(NewSalesOrderActivity.this, "", getString(R.string.msg_sync_progress_msg_plz_wait));
                    new RefreshAsyncTask(NewSalesOrderActivity.this, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            UtilConstants.showAlert(getString(R.string.no_network_conn), NewSalesOrderActivity.this);
        }
    }

    @Override
    public void onRequestError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e,NewSalesOrderActivity.this);
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
            if (isFromWhere == 1 || isFromWhere == 2) {
                if (!dialogCancelled && !Constants.isStoreClosed) {
                    if (operation == Operation.OfflineRefresh.getValue()) {
                        try {
                            /*String syncTime = UtilConstants.getSyncHistoryddmmyyyyTime();
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
                          //  Constants.updateSyncTime(alAssignColl,this,Constants.DownLoad);
                        } catch (Exception exce) {
                            LogManager.writeLogError(Constants.SyncTableHistory + exce.getMessage());
                        }

                        try {
                            if (progressDialog != null) {
                                Constants.hideProgressDialog(progressDialog);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        Constants.isSync = false;
                        if (!Constants.isStoreClosed) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    NewSalesOrderActivity.this, R.style.MyTheme);
                            builder.setMessage(getString(R.string.msg_error_occured_during_sync))
                                    .setCancelable(false)
                                    .setPositiveButton(getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,
                                                                    int id) {

                                                    dialog.cancel();
                                                }
                                            });

                            builder.show();


                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    NewSalesOrderActivity.this, R.style.MyTheme);
                            builder.setMessage(getString(R.string.msg_sync_terminated))
                                    .setCancelable(false)
                                    .setPositiveButton(getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,
                                                                    int id) {

                                                    dialog.cancel();

                                                }
                                            });

                            builder.show();
                        }
                    }


                }
            } else {
                penROReqCount++;
                if ((operation == Operation.Create.getValue()) && (penROReqCount == pendingROVal)) {
                    LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
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
                        OfflineManager.refreshRequests(NewSalesOrderActivity.this, concatCollectionStr, this);
                    }
                    catch (OfflineODataStoreException e1){
                        e1.printStackTrace();
                    }
                }

                if (operation == Operation.OfflineFlush.getValue()) {
                    try {
                        OfflineManager.refreshRequests(NewSalesOrderActivity.this, Constants.Visits, this);
                    } catch (OfflineODataStoreException e1) {
                        e1.printStackTrace();
                    }
                } else if (operation == Operation.OfflineRefresh.getValue()) {
                    LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
                    try {
                       /* String syncTime = UtilConstants.getSyncHistoryddmmyyyyTime();
                        String[] DEFINGREQARRAY = Constants.getDefinigReq(NewSalesOrderActivity.this);
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
                //        Constants.updateSyncTime(alAssignColl,this,Constants.DownLoad);
                    } catch (Exception exce) {
                        LogManager.writeLogError(Constants.SyncTableHistory + exce.getMessage());
                    }
                    Constants.isSync = false;
                    try {
                        if (progressDialog != null) {
                            Constants.hideProgressDialog(progressDialog);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    UtilConstants.showAlert(getString(R.string.msg_error_occured_during_sync), NewSalesOrderActivity.this);
                }
            }
        }else{
            Constants.isSync = false;
            if (progressDialog != null) {
                Constants.hideProgressDialog(progressDialog);
            }
            Constants.displayMsgReqError(errorBean.getErrorCode(),NewSalesOrderActivity.this);
        }
    }

    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        if (!dialogCancelled && !Constants.isStoreClosed) {
            if (operation == Operation.OfflineRefresh.getValue() && isFromWhere == 2) {
          //      Constants.updateLastSyncTimeToTable(alAssignColl,this,Constants.DownLoad);
                try {
                    if (progressDialog != null) {
                        Constants.hideProgressDialog(progressDialog);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Constants.isSync = false;
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
                        NewSalesOrderActivity.this, R.style.MyTheme);
                builder.setMessage(getString(R.string.msg_sync_successfully_completed))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        getRefreshFragment(0);
                                    }
                                });

                builder.show();
            }
            if (isFromWhere == 4) {
                if (operation == Operation.Create.getValue() && pendingROVal > 0) {

                    Set<String> set = new HashSet<>();
                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
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
                        ConstantsUtils.storeInDataVault(tempRODevList[penROReqCount], "",this);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    penROReqCount++;


                }
                if ((operation == Operation.Create.getValue()) && (penROReqCount == pendingROVal)) {

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
                        OfflineManager.refreshRequests(NewSalesOrderActivity.this, concatCollectionStr, this);
                    } catch (OfflineODataStoreException e) {
                        TraceLog.e(Constants.SyncOnRequestSuccess, e);
                    }


                } else if (operation == Operation.OfflineFlush.getValue()) {

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
                        OfflineManager.refreshRequests(NewSalesOrderActivity.this, concatCollectionStr, this);

                    } catch (OfflineODataStoreException e) {
                        TraceLog.e(Constants.SyncOnRequestSuccess, e);
                    }
                } else if (operation == Operation.OfflineRefresh.getValue()) {
                //    Constants.updateLastSyncTimeToTable(alAssignColl,this,Constants.DownLoad);
                    try {
                        if (progressDialog != null) {
//                            Constants.hideProgressDialog(progressDialog);
                            progressDialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String msg = "";
                    if(!isErrorFromBackend)
                        msg = getString(R.string.msg_sync_successfully_completed);
                    else{
                        msg = getString(R.string.msg_error_occured_during_sync);
                    }
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
                            NewSalesOrderActivity.this, R.style.MyTheme);
                    builder.setMessage(msg)
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            getRefreshFragment(1);
                                        }
                                    });

                    builder.show();

                } else {
                    try {
                        if (progressDialog != null) {
                            Constants.hideProgressDialog(progressDialog);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void clickedStatus(boolean clickedStatus) {
        if (!clickedStatus) {
            try {
                if (progressDialog != null) {
                    Constants.hideProgressDialog(progressDialog);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /*refresh pager and fragments*/
    public void getRefreshFragment(int position) {
//        isRefreshed = true;
//        viewPagerAdapter = new ViewPagerTabAdapter(getSupportFragmentManager());
//        SalesOrderListFragmentLeft = new SalesOrderListFragment();
//        SalesOrderListFragmentRight = new SalesOrderListFragment();
//        Bundle bundleLeft = new Bundle();
//        bundleLeft.putString(Constants.CPGUID, mStrBundleCPGUID);
//        bundleLeft.putString(Constants.CPNo, mStrBundleRetID);
//        bundleLeft.putString(Constants.RetailerName, mStrBundleRetName);
//        bundleLeft.putString(Constants.CPUID, mStrBundleRetUID);
//        bundleLeft.putInt(Constants.comingFrom, comingFrom);
//        bundleLeft.putInt(Constants.EXTRA_TAB_POS, Constants.TAB_POS_1);
//        Bundle bundleRight = new Bundle();
//        bundleRight.putString(Constants.CPGUID, mStrBundleCPGUID);
//        bundleRight.putString(Constants.CPNo, mStrBundleRetID);
//        bundleRight.putString(Constants.RetailerName, mStrBundleRetName);
//        bundleRight.putString(Constants.CPUID, mStrBundleRetUID);
//        bundleRight.putInt(Constants.EXTRA_TAB_POS, Constants.TAB_POS_2);
//        bundleRight.putInt(Constants.comingFrom, comingFrom);
//        SalesOrderListFragmentLeft.setArguments(bundleLeft);
//        SalesOrderListFragmentRight.setArguments(bundleRight);
//        viewPagerAdapter.addFrag(SalesOrderListFragmentLeft, tabLeftTitle);
//        viewPagerAdapter.addFrag(SalesOrderListFragmentRight, tabRightTitle);
//        viewPager.setAdapter(viewPagerAdapter);
//        viewPager.setCurrentItem(position);
////        spnrSOStatus.setSelection(0);
//        getSOStatus();
//        displaySOStatus();
        Intent refershIntent = new Intent(NewSalesOrderActivity.this, NewSalesOrderActivity.class);
        refershIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        refershIntent.putExtra(Constants.CPNo, mStrBundleRetID);
        refershIntent.putExtra(Constants.RetailerName, mStrBundleRetName);
        refershIntent.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        refershIntent.putExtra(Constants.CPUID, mStrBundleRetUID);
        refershIntent.putExtra(Constants.comingFrom, Constants.RetDetails);
        refershIntent.putExtra(Constants.EXTRA_TAB_POS, position);
        startActivity(refershIntent);

    }

}