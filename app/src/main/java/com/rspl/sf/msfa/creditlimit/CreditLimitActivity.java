package com.rspl.sf.msfa.creditlimit;

import android.graphics.Color;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterViewInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewTypeAdapter;
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.rspl.sf.msfa.BuildConfig;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.asyncTask.RefreshAsyncTask;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.finance.CollectionBean;
import com.rspl.sf.msfa.mbo.ConfigTypesetTypesBean;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.so.SOUtils;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10742 on 5/26/2017.
 */

public class CreditLimitActivity extends AppCompatActivity implements UIListener, SwipeRefreshLayout.OnRefreshListener, AdapterViewInterface<CollectionBean> {

    public static String[][] creditControlValues;
    TextView tvCustName = null, tvCustNum = null;
    TextView tvCreditLimit = null;
    TextView tvCreditExposure = null;
    TextView tvCreditLimitUsed = null;
    TextView tvCurrentOutstanding = null;
    TextView tvSecurityDeposit = null;
    Spinner spCreditControlStatus;
    String concatCollectionStr = "";
    ArrayList<String> alAssignColl = new ArrayList<>();
    boolean dialogCancelled = false;
    TextView tv_last_sync_time_value;
    String[] creditLimitValues = new String[]{"0", "0", "0", "0", "0", "", "0", ""};
    String selectedStatus;
    private String mStrBundleRetID = "", mStrBundleCPGUID = "";
    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";
    private Bundle bundleExtras = null;
    private ConstraintLayout clCreditController;
    private String[][] arrayInvStatusVal;
    private PieChart pieChart;
    private int pos = 0;
    private TextView tvBalanceAmount;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout llCreditExposure;
    private LinearLayout llCreditLimit;
    private SimpleRecyclerViewTypeAdapter<CollectionBean> simpleRecyclerViewAdapterOpen, simpleRecyclerViewAdapterClosed;
    private RecyclerView rvCreditDocOpen, rvCreditDocClosed;
    private TextView no_record_found, no_record_found_close;
    private NestedScrollView nestedScroll;
    private GUID refguid =null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)

        setContentView(R.layout.activity_credit_limit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_credit_limit), 0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
        }

        initUI();
    }

    private void initUI() {
        tvCustName = (TextView) findViewById(R.id.tv_reatiler_name);
        tvCustNum = (TextView) findViewById(R.id.tv_reatiler_id);
        tvCustName.setText(mStrBundleRetName);
        tvCustNum.setText(mStrBundleRetUID);

        // tv_last_sync_time_value = (TextView)findViewById(R.id.tv_last_sync_time_value);
        nestedScroll = (NestedScrollView) findViewById(R.id.nestedScroll);
        spCreditControlStatus = (Spinner) findViewById(R.id.spin_credit_control_his_status_id);
        tvCreditLimit = (TextView) findViewById(R.id.tv_credit_limit);
        tvCreditExposure = (TextView) findViewById(R.id.tv_credit_exposure);
        tvCreditLimitUsed = (TextView) findViewById(R.id.tv_credit_limit_used);
        tvCurrentOutstanding = (TextView) findViewById(R.id.tv_current_outstanding);
        tvSecurityDeposit = (TextView) findViewById(R.id.tv_security_deposit);
        clCreditController = (ConstraintLayout) findViewById(R.id.clCreditController);
        llCreditExposure = (LinearLayout) findViewById(R.id.llCreditExposure);
        llCreditLimit = (LinearLayout) findViewById(R.id.llCreditLimit);
        pieChart = (PieChart) findViewById(R.id.pieChart);
        tvBalanceAmount = (TextView) findViewById(R.id.tvBalanceAmount);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        ConstantsUtils.setProgressColor(CreditLimitActivity.this, swipeRefresh);
        pieChart.setNoDataText(getString(R.string.no_data));

        rvCreditDocOpen = (RecyclerView) findViewById(R.id.rvCreditDocOpen);
        rvCreditDocClosed = (RecyclerView) findViewById(R.id.rvCreditDocClosed);
        View llCloseView = findViewById(R.id.llCloseView);
        View llOpenView = findViewById(R.id.llOpenView);
        no_record_found = (TextView) llOpenView;
        no_record_found_close = (TextView) llCloseView;

        simpleRecyclerViewAdapterOpen = new SimpleRecyclerViewTypeAdapter<>(CreditLimitActivity.this, R.layout.credit_doc_item_level, this, rvCreditDocOpen, no_record_found);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CreditLimitActivity.this);
        rvCreditDocOpen.setLayoutManager(linearLayoutManager);
        rvCreditDocOpen.setNestedScrollingEnabled(false);
        rvCreditDocOpen.setAdapter(simpleRecyclerViewAdapterOpen);

        simpleRecyclerViewAdapterClosed = new SimpleRecyclerViewTypeAdapter<>(CreditLimitActivity.this, R.layout.credit_doc_item_level, this, rvCreditDocClosed, no_record_found_close);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(CreditLimitActivity.this);
        rvCreditDocClosed.setLayoutManager(linearLayoutManager1);
        rvCreditDocClosed.setNestedScrollingEnabled(false);
        rvCreditDocClosed.setAdapter(simpleRecyclerViewAdapterClosed);
        //getStatus();
        getCustomerArea();

    }

    private final void focusOnView() {
        nestedScroll.post(new Runnable() {
            @Override
            public void run() {
                nestedScroll.scrollTo(0, 0);
            }
        });
    }

    private void displayPieChart() {

        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setCenterText(ConstantsUtils.generateCenterSpannableText(creditLimitValues[6] + " %"));

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setDrawCenterText(true);

        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(false);

        String creditPer = creditLimitValues[6];
        if (Double.parseDouble(creditPer) < 0) {
            creditPer = creditPer.replace("-", "");
        }
        setData(creditPer);

        pieChart.animateY(1000, Easing.EasingOption.EaseInOutQuad);
        // entry label styling
        pieChart.getLegend().setEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.invalidate();
    }

    private void setData(String totalPercent) {

        String remainingPercent = "0";
        try {
            remainingPercent = String.valueOf(100 - Integer.parseInt(totalPercent.split("\\.")[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        float flTotalPercent = Float.parseFloat(totalPercent);
        float flRemainingPer = Float.parseFloat(remainingPercent);

        List<PieEntry> entries = new ArrayList<>();
        if (flTotalPercent != 0f)
            entries.add(new PieEntry(flTotalPercent, ""));
        if (flRemainingPer != 0f)
            entries.add(new PieEntry(Float.parseFloat(remainingPercent), ""));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(0f);
        //pie chart text color start
//        ArrayList<Integer> colorText = new ArrayList<Integer>();
////        if (flTotalPercent != 0f)
//            colorText.add(ColorTemplate.rgb(String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(getContext(), android.R.color.transparent)))));
//        colorText.add(Color.argb(5,100, 100, 100));
//            colorText.add(ColorTemplate.rgb(String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(getContext(), android.R.color.transparent)))));
//        if (flRemainingPer != 0f)
//            colorText.add(Color.rgba(255, 255, 255, .4));//white
        //pie chart text color end

        //pie chart background color start
        ArrayList<Integer> colors = new ArrayList<Integer>();
        if (flTotalPercent != 0f)
            colors.add(ColorTemplate.rgb(String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(this, R.color.secondaryColor)))));
        if (flRemainingPer != 0f)
            colors.add(Color.rgb(238, 238, 238));//gray


        dataSet.setColors(colors);
        //pie chart background color end
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setDrawValues(false);
        pieChart.setData(data);
        pieChart.highlightValue(0, 0, false);
    }

    private void getCustomerArea() {

        String mStrConfigQry = Constants.ConfigTypesetTypes + "?$filter=" + Constants.Typeset + " eq '" + Constants.CRDCTL + "' &$orderby = Types asc";
        try {
            final ArrayList<ConfigTypesetTypesBean> configTypesetTypeList = new ArrayList<>();
            configTypesetTypeList.addAll(OfflineManager.getCredLimtSize(mStrConfigQry));
            try {
                creditControlValues = OfflineManager.getCreditControlValues(Constants.CustomerCreditLimits + "?$filter=" + Constants.Customer + " eq '" + mStrBundleRetID + "'");
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            if (configTypesetTypeList!=null && configTypesetTypeList.size() >= 1) {
                if (creditControlValues!=null && creditControlValues.length > 1) {
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, creditControlValues[1]) {
                        @Override
                        public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                            final View v = super.getDropDownView(position, convertView, parent);
                            v.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
//                                ((TextView) v.findViewById(android.R.id.text1)).setSingleLine(false);
                                        if (position == spCreditControlStatus.getSelectedItemPosition()/* - 1*/)
                                            ((TextView) v.findViewById(android.R.id.text1)).setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            return v;
                        }
                    };
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spCreditControlStatus.setAdapter(arrayAdapter);
                    spCreditControlStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                            pos = position;
                            if(pos>=0){
                                displayData(pos);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                        }
                    });

                    clCreditController.setVisibility(View.VISIBLE);
                }
            } else {
                if (creditControlValues!=null && creditControlValues.length > 0) {
                    pos = 0;
                    displayData(pos);
                    clCreditController.setVisibility(View.GONE);
                }
            }
            if (creditControlValues!=null && creditControlValues.length > 0) {
                displayData(pos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayData(int position) {
        try {
            selectedStatus = creditControlValues[0][position];
        } catch (Exception e) {
            e.printStackTrace();
        }
        getCreditLimitData();
        displayCreditLimitValues();
        setPieChart();
        String startDate = SOUtils.getStartDate(CreditLimitActivity.this, getString(R.string.so_filter_current_mont));
        String endDate = SOUtils.getEndDate(CreditLimitActivity.this, getString(R.string.so_filter_current_mont));
        String dateFil = " and DocumentDate ge datetime'" + startDate + "' and DocumentDate le datetime'" + endDate + "' and CustomerNo eq '"+mStrBundleRetID+"' &$orderby=" + Constants.DocumentNo + " desc";
        try {
            ArrayList<CollectionBean> collectionBeanArrayList = OfflineManager.getCollection(CreditLimitActivity.this, Constants.Collections + "?$filter=(StatusID eq '01' or StatusID eq '03')" + dateFil);
            displayData(collectionBeanArrayList);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        try {
            ArrayList<CollectionBean> collectionBeanArrayList = OfflineManager.getCollection(CreditLimitActivity.this, Constants.Collections + "?$filter=StatusID eq '02'" + dateFil);
            displayClosedData(collectionBeanArrayList);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void setPieChart() {
        if (!creditLimitValues[0].equals("0.00"))
            displayPieChart();
    }


    private void getCreditLimitData() {
        try {
            creditLimitValues = OfflineManager.getCrediLimitValues(Constants.CustomerCreditLimits + "?$filter=" +
                    Constants.Customer + " eq '" + mStrBundleRetID + "' and " + Constants.CreditControlAreaID + " eq '" + selectedStatus + "'");
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        if (creditLimitValues == null) {
            creditLimitValues = new String[]{"0", "0", "0", "0", "0", "", "0", ""};
        }
    }

    private void displayCreditLimitValues() {
        if (Double.parseDouble(creditLimitValues[0]) <= 0) {
            pieChart.setVisibility(View.GONE);
            llCreditExposure.setVisibility(View.GONE);
            llCreditLimit.setVisibility(View.GONE);
        } else {
            pieChart.setVisibility(View.VISIBLE);
            llCreditExposure.setVisibility(View.VISIBLE);
            llCreditLimit.setVisibility(View.VISIBLE);
        }
        tvCreditLimit.setText(ConstantsUtils.commaSeparator(creditLimitValues[0], creditLimitValues[5]) + " " + creditLimitValues[5]);
        tvCreditExposure.setText(ConstantsUtils.commaSeparator(creditLimitValues[1], creditLimitValues[5]) + " " + creditLimitValues[5]);
        tvCreditLimitUsed.setText(ConstantsUtils.commaSeparator(creditLimitValues[2], creditLimitValues[5]) + " " + creditLimitValues[5]);
        tvCurrentOutstanding.setText(ConstantsUtils.commaSeparator(creditLimitValues[3], creditLimitValues[5]) + " " + creditLimitValues[5]);
        tvSecurityDeposit.setText(ConstantsUtils.commaSeparator(creditLimitValues[4], creditLimitValues[5]) + " " + creditLimitValues[5]);
        tvBalanceAmount.setText(ConstantsUtils.commaSeparator(creditLimitValues[2], creditLimitValues[5]) + " " + creditLimitValues[5]);
        displayRefreshTime(UtilConstants.getLastRefreshedTime(getApplicationContext(), ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.CustomerCreditLimits, Constants.TimeStamp, this)));
//        if(Double.parseDouble(creditLimitValues[2])>90){
//            tvCreditLimitUsed.setBackgroundColor(getResources().getColor(R.color.text_red));
//        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_invoice_his_list, menu);
        MenuItem refresh = menu.findItem(R.id.menu_refresh_inv);
        refresh.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_refresh_inv:
                onSwipeRefresh();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }


    /*Refresh Invoice list from backEnd*/
    void onSwipeRefresh() {
        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
            alAssignColl.clear();
            concatCollectionStr = "";
            alAssignColl.add(Constants.CustomerCreditLimits);
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            alAssignColl.add(Constants.Collections);
//            alAssignColl.add(Constants.CollectionItemDetails);
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
                hideProgressDialog();
                displayMessage(getString(R.string.alert_auto_sync_is_progress));
            } else {
                try {
                    Constants.isSync = true;
                    dialogCancelled = false;
                    refguid = GUID.newRandom();
                    Constants.updateStartSyncTime(this,Constants.CrdStatus_sync,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(CreditLimitActivity.this, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            hideProgressDialog();
            displayMessage(getString(R.string.no_network_conn));
        }
    }

    @Override
    public void onRefresh() {
        onSwipeRefresh();
    }

    @Override
    public void onRequestError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e, CreditLimitActivity.this);
        if (errorBean.hasNoError()) {
            if (dialogCancelled == false && !Constants.isStoreClosed) {
                if (operation == Operation.OfflineRefresh.getValue()) {
                    hideProgressDialog();
                    Constants.isSync = false;
                    if (!Constants.isStoreClosed) {
                        UtilConstants.showAlert(getString(R.string.msg_error_occured_during_sync), CreditLimitActivity.this);


                    } else {
                        UtilConstants.showAlert(getString(R.string.msg_sync_terminated), CreditLimitActivity.this);
                    }
                }else if (operation == Operation.GetStoreOpen.getValue()){
                    hideProgressDialog();
                    Constants.isSync = false;
                    UtilConstants.showAlert(getString(R.string.msg_error_occured_during_sync), CreditLimitActivity.this);
                }
            }
        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(CreditLimitActivity.this)) {
                Constants.isSync = true;
                showProgressDialog();
                new RefreshAsyncTask(CreditLimitActivity.this, "", this).execute();
            } else {
                Constants.isSync = false;
                hideProgressDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), CreditLimitActivity.this);
            }
        } else {
            Constants.isSync = false;
            hideProgressDialog();
            Constants.displayMsgReqError(errorBean.getErrorCode(), CreditLimitActivity.this);
        }
    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {
        if (dialogCancelled == false && !Constants.isStoreClosed) {
            if (operation == Operation.OfflineRefresh.getValue()) {
                try {
                    OfflineManager.getAuthorizations(getApplicationContext());
                    ConstantsUtils.startAutoSync(CreditLimitActivity.this, false);
//                    ConstantsUtils.serviceReSchedule(CreditLimitActivity.this, true);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
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
                                colName, Constants.TimeStamp, syncTime
                        );
                    }*/
                    Constants.updateSyncTime(alAssignColl,this,Constants.CrdStatus_sync,refguid.toString().toUpperCase());
                } catch (Exception exce) {
                    LogManager.writeLogError(Constants.SyncTableHistory + exce.getMessage());
                }

                //tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.SFINVOICES, Constants.TimeStamp,this));
                hideProgressDialog();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, CreditLimitActivity.this, BuildConfig.APPLICATION_ID, false);
                Constants.isSync = false;
                if (!Constants.isStoreClosed) {
                    getCustomerArea();
                    displayRefreshTime(UtilConstants.getLastRefreshedTime(getApplicationContext(), ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.CustomerCreditLimits, Constants.TimeStamp, CreditLimitActivity.this)));
                } else {
                    UtilConstants.showAlert(getString(R.string.msg_sync_terminated), CreditLimitActivity.this);
                }
            } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
                Constants.isSync = false;
                try {
                    OfflineManager.getAuthorizations(CreditLimitActivity.this);
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, CreditLimitActivity.this, BuildConfig.APPLICATION_ID, false);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                Constants.setSyncTime(CreditLimitActivity.this,refguid.toString().toUpperCase());
                ConstantsUtils.startAutoSync(CreditLimitActivity.this, false);
                getCustomerArea();
                displayRefreshTime(UtilConstants.getLastRefreshedTime(getApplicationContext(), ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.CustomerCreditLimits, Constants.TimeStamp, CreditLimitActivity.this)));
            }
        }
    }

    public void displayRefreshTime(String refreshTime) {
        String lastRefresh = "";
        if (!TextUtils.isEmpty(refreshTime)) {
            lastRefresh = getString(R.string.po_last_refreshed) + " " + refreshTime;
        }
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(lastRefresh);
    }

    private void showProgressDialog() {
        swipeRefresh.setRefreshing(true);
    }

    private void hideProgressDialog() {
        swipeRefresh.setRefreshing(false);
    }

    private void displayMessage(String msg) {
        ConstantsUtils.displayLongToast(CreditLimitActivity.this, msg);
    }

    public void displayData(ArrayList<CollectionBean> collectionBeen) {
        simpleRecyclerViewAdapterOpen.refreshAdapter(collectionBeen);
    }

    private void displayClosedData(ArrayList<CollectionBean> collectionBeanArrayList) {
        simpleRecyclerViewAdapterClosed.refreshAdapter(collectionBeanArrayList);
        focusOnView();
    }

    @Override
    public void onItemClick(CollectionBean collectionBean, View view, int i) {

    }

    @Override
    public int getItemViewType(int i, ArrayList<CollectionBean> arrayList) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new CreditItemVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, CollectionBean collectionBean, ArrayList<CollectionBean> arrayList) {
        ((CreditItemVH) viewHolder).tvDocNo.setText(collectionBean.getDocNo());
        ((CreditItemVH) viewHolder).tvDocAmt.setText(collectionBean.getDocAmount() + " " + collectionBean.getCurrency());
        ((CreditItemVH) viewHolder).tvDate.setText(collectionBean.getDocDate());
    }
}
