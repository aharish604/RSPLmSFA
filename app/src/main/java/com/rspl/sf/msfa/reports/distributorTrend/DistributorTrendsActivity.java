package com.rspl.sf.msfa.reports.distributorTrend;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.MyPerformanceBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class DistributorTrendsActivity extends AppCompatActivity implements DistributorTrendView, AdapterInterface<MyPerformanceBean>, SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private TextView no_record_found;
    private Toolbar toolbar;
    private DistributorTrendPresenterImpl presenter;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";
    private String mStrBundleCPGUID = "";
    private SimpleRecyclerViewAdapter<MyPerformanceBean> recyclerViewAdapter;
    private String m1Header = "";
    private String m2Header = "";
    private String m3Header = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor_trends);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.EXTRA_CUSTOMER_NO);
            mStrBundleRetName = bundleExtras.getString(Constants.EXTRA_CUSTOMER_NAME);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
        }
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        no_record_found = (TextView) findViewById(R.id.no_record_found);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.setProgressColor(this, swipeRefresh);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_retailer_trends), 0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.add(Calendar.MONTH, -1);
        m1Header = mCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        mCalendar.add(Calendar.MONTH, -1);
        m2Header = mCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        mCalendar.add(Calendar.MONTH, -1);
        m3Header = mCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new SimpleRecyclerViewAdapter<MyPerformanceBean>(this, R.layout.trends_item, this, recyclerView, no_record_found);
        recyclerView.setAdapter(recyclerViewAdapter);
        presenter = new DistributorTrendPresenterImpl(DistributorTrendsActivity.this, this, mStrBundleCPGUID);
        presenter.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        initScroll();
    }
/*
    void initScroll(){
        svHeader = (HorizontalScrollView) findViewById(R.id.sv_header);
        svItem = (HorizontalScrollView) findViewById(R.id.sv_item);

        svHeader.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    svHeader.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                        @Override
                        public void onScrollChange(View view, int scrollX, int scrollY, int i2, int i3) {
                            svItem.scrollTo(scrollX,scrollY);
                        }
                    });
                }
                return false;
            }
        });

        svItem.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e("Tag1",String.valueOf(getWindow().getCurrentFocus()));
                svItem.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        Log.e("Tag2",String.valueOf(getWindow().getCurrentFocus()));
                        int scrollY = svItem.getScrollY(); // For ScrollView
                        int scrollX = svItem.getScrollX(); // For HorizontalScrollView
                        // DO SOMETHING WITH THE SCROLL COORDINATES
                        svHeader.scrollTo(scrollX,scrollY);
                        Log.e("Tag3",String.valueOf(getWindow().getCurrentFocus()));
                    }
                });
                return false;
            }
        });
    }*/

    @Override
    public void showProgress() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void displayMsg(String msg) {
        ConstantsUtils.displayLongToast(DistributorTrendsActivity.this, msg);
    }

    @Override
    public void displayList(ArrayList<MyPerformanceBean> alRetTrends) {
        recyclerViewAdapter.refreshAdapter(alRetTrends);
//        displayRetTrendsValues(alRetTrends);
    }

    @Override
    public void displayLstSyncTime(String lastSeenDateFormat) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(getString(R.string.po_last_refreshed) + " " + lastSeenDateFormat);
        }
    }
    /*private void displayRetTrendsValues(ArrayList<MyPerformanceBean> filteredArraylist) {

        TableLayout tlCRSList = (TableLayout) findViewById(R.id.crs_sku);
        TableLayout tlReportList = (TableLayout) findViewById(R.id.report_table);

        tlCRSList.removeAllViews();
        tlReportList.removeAllViews();
        LinearLayout llLineItemVal;
        LinearLayout llCRSKUGroup;

        if (filteredArraylist != null && filteredArraylist.size() > 0) {
            for (final MyPerformanceBean performanceBean : filteredArraylist) {
                llLineItemVal = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.ll_trends_scroll_line_item, null, false);
                llCRSKUGroup = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.ll_trends_crs_sku_line_item, null, false);

                TextView tvSKUGroupName = (TextView) llCRSKUGroup.findViewById(R.id.tv_item_trends_sku_grp);

                tvSKUGroupName.setText(performanceBean.getMaterialDesc());

                TextView tv_m3_value = (TextView) llLineItemVal.findViewById(R.id.tv_m3_value);
                TextView tv_m2_value = (TextView) llLineItemVal.findViewById(R.id.tv_m2_value);
                TextView tv_m1_value = (TextView) llLineItemVal.findViewById(R.id.tv_m1_value);
                TextView tv_avg_lst_mnt = (TextView) llLineItemVal.findViewById(R.id.tv_avg_lst_mnt);
                TextView tv_ly_sm_ach = (TextView) llLineItemVal.findViewById(R.id.tv_ly_sm_ach);
                TextView tv_cm_tgt = (TextView) llLineItemVal.findViewById(R.id.tv_cm_tgt);

                TextView tv_cy_mtd = (TextView) llLineItemVal.findViewById(R.id.tv_cy_mtd);
                TextView tv_bal_to_do = (TextView) llLineItemVal.findViewById(R.id.tv_bal_to_do);
                TextView tv_ach_per = (TextView) llLineItemVal.findViewById(R.id.tv_ach_per);
                TextView tv_ly_growth_per = (TextView) llLineItemVal.findViewById(R.id.tv_ly_growth_per);

                tv_m3_value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(performanceBean.getAmtMonth3PrevPerf()));
                tv_m2_value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(performanceBean.getAmtMonth2PrevPerf()));
                tv_m1_value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(performanceBean.getAmtMonth1PrevPerf()));
                tv_avg_lst_mnt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(performanceBean.getAvgLstThreeMonth()));
                tv_ly_sm_ach.setText(UtilConstants.removeLeadingZerowithTwoDecimal(performanceBean.getAmtLMTD()));
                tv_cm_tgt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(performanceBean.getCMTarget()));
                tv_cy_mtd.setText(UtilConstants.removeLeadingZerowithTwoDecimal(performanceBean.getAmtMTD()));
                tv_bal_to_do.setText(UtilConstants.removeLeadingZerowithTwoDecimal(performanceBean.getBalToDo()));
                tv_ach_per.setText(UtilConstants.removeLeadingZerowithTwoDecimal(performanceBean.getAchivedPer()));
                tv_ly_growth_per.setText(UtilConstants.removeLeadingZerowithTwoDecimal(performanceBean.getGrPer()));

                tlReportList.addView(llLineItemVal);
                tlCRSList.addView(llCRSKUGroup);
            }
        }else{
            tlReportList = (TableLayout) findViewById(R.id.report_table);

            LinearLayout llEmptyLayout = (LinearLayout) LayoutInflater.from(DistributorTrendsActivity.this)
                    .inflate(R.layout.ll_so_create_empty_layout, null);

            tlReportList.addView(llEmptyLayout);
        }

    }*/

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onItemClick(MyPerformanceBean myPerformanceBean, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new DistributorTrendVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, MyPerformanceBean myPerformanceBean) {
        ((DistributorTrendVH) viewHolder).tvM1Header.setText(m3Header);
        ((DistributorTrendVH) viewHolder).tvM2Header.setText(m2Header);
        ((DistributorTrendVH) viewHolder).tvM3Header.setText(m1Header);
        if (myPerformanceBean.getReportType().equalsIgnoreCase("02")) {
            ((DistributorTrendVH) viewHolder).tvM1Value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getAmtMonth1PrevPerf()));
            ((DistributorTrendVH) viewHolder).tvM2Value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getAmtMonth2PrevPerf()));
            ((DistributorTrendVH) viewHolder).tvM3Value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getAmtMonth3PrevPerf()));
            ((DistributorTrendVH) viewHolder).tvLsttreMValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getAvgLstThreeMonth()));
            ((DistributorTrendVH) viewHolder).tvLysmaValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getAmtLMTD()));
            ((DistributorTrendVH) viewHolder).tvCMTargValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getCMTarget()));
            ((DistributorTrendVH) viewHolder).tvCMTDValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getAmtMTD()));
            ((DistributorTrendVH) viewHolder).tvBTDValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getBalToDo()));
        }else{
            HashMap<String,String> mapUOM = myPerformanceBean.getMapUOM();
            if(mapUOM.containsKey(myPerformanceBean.getUOM())){
//                ((DistributorTrendVH) viewHolder).tvM1Value.setText(UtilConstants.trimQtyDecimalPlace(myPerformanceBean.getAmtMonth1PrevPerf()));
//                ((DistributorTrendVH) viewHolder).tvM2Value.setText(UtilConstants.trimQtyDecimalPlace(myPerformanceBean.getAmtMonth2PrevPerf()));
//                ((DistributorTrendVH) viewHolder).tvM3Value.setText(UtilConstants.trimQtyDecimalPlace(myPerformanceBean.getAmtMonth3PrevPerf()));
//                ((DistributorTrendVH) viewHolder).tvLsttreMValue.setText(UtilConstants.trimQtyDecimalPlace(myPerformanceBean.getAvgLstThreeMonth()));
//                ((DistributorTrendVH) viewHolder).tvLysmaValue.setText(UtilConstants.trimQtyDecimalPlace(myPerformanceBean.getAmtLMTD()));
//                ((DistributorTrendVH) viewHolder).tvCMTargValue.setText(UtilConstants.trimQtyDecimalPlace(myPerformanceBean.getCMTarget()));
//                ((DistributorTrendVH) viewHolder).tvCMTDValue.setText(UtilConstants.trimQtyDecimalPlace(myPerformanceBean.getAmtMTD()));
//                ((DistributorTrendVH) viewHolder).tvBTDValue.setText(UtilConstants.trimQtyDecimalPlace(myPerformanceBean.getBalToDo()));

                ((DistributorTrendVH) viewHolder).tvM1Value.setText(myPerformanceBean.getAmtMonth1PrevPerf());
                ((DistributorTrendVH) viewHolder).tvM2Value.setText(myPerformanceBean.getAmtMonth2PrevPerf());
                ((DistributorTrendVH) viewHolder).tvM3Value.setText(myPerformanceBean.getAmtMonth3PrevPerf());
                ((DistributorTrendVH) viewHolder).tvLsttreMValue.setText(myPerformanceBean.getAvgLstThreeMonth());
                ((DistributorTrendVH) viewHolder).tvLysmaValue.setText(myPerformanceBean.getAmtLMTD());
                ((DistributorTrendVH) viewHolder).tvCMTargValue.setText(myPerformanceBean.getCMTarget());
                ((DistributorTrendVH) viewHolder).tvCMTDValue.setText(myPerformanceBean.getAmtMTD());
                ((DistributorTrendVH) viewHolder).tvBTDValue.setText(myPerformanceBean.getBalToDo());
            }else{
                ((DistributorTrendVH) viewHolder).tvM1Value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getAmtMonth1PrevPerf()));
                ((DistributorTrendVH) viewHolder).tvM2Value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getAmtMonth2PrevPerf()));
                ((DistributorTrendVH) viewHolder).tvM3Value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getAmtMonth3PrevPerf()));
                ((DistributorTrendVH) viewHolder).tvLsttreMValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getAvgLstThreeMonth()));
                ((DistributorTrendVH) viewHolder).tvLysmaValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getAmtLMTD()));
                ((DistributorTrendVH) viewHolder).tvCMTargValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getCMTarget()));
                ((DistributorTrendVH) viewHolder).tvCMTDValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getAmtMTD()));
                ((DistributorTrendVH) viewHolder).tvBTDValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getBalToDo()));

            }

        }
        ((DistributorTrendVH) viewHolder).tvACHDValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getAchivedPer()));
        ((DistributorTrendVH) viewHolder).tvLYGrthValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getGrPer()));
        ((DistributorTrendVH) viewHolder).tvTrendsSKUGrp.setText(getString(R.string.po_details_display_value, myPerformanceBean.getMaterialDesc(), myPerformanceBean.getUOM()));
//        ((DistributorTrendVH) viewHolder).tvTrendsSKUGrp.setText(myPerformanceBean.getMaterialDesc());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_distributor_trend, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_info:
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                DistTrendInfoFragment dbFilterDialogFragment = new DistTrendInfoFragment();
                dbFilterDialogFragment.show(ft, "dialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        presenter.onRefresh();
    }
}
