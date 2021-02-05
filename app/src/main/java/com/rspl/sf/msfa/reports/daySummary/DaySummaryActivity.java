package com.rspl.sf.msfa.reports.daySummary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.github.mikephil.charting.charts.PieChart;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.MyTargetsBean;
import com.rspl.sf.msfa.reports.targets.TargetViewHolder;
import com.rspl.sf.msfa.ui.FlowLayout;

import java.util.ArrayList;


/**
 * Created by e10526 on 03-02-2017.
 *
 */
public class DaySummaryActivity extends AppCompatActivity implements DaySummaryViewPresenter,DaySummaryViewPresenter.DaySummResponse,SwipeRefreshLayout.OnRefreshListener,AdapterInterface<MyTargetsBean> {


    // android components
    SwipeRefreshLayout swipeRefresh;
    RecyclerView recyclerView;
    TextView no_record_found, tvremaingdays,tv_days_percentage,tv_order_val,
            tv_tar_sal_val,tv_ach_sal_val,tv_tar_tlsd_val,tv_ach_tlsd_val,
            tv_tar_bill_val,tv_ach_bill_val,tv_tar_eco_val,tv_ach_eco_val;
    Toolbar toolbar;
    SimpleRecyclerViewAdapter<MyTargetsBean> recyclerViewAdapter=null;
    LinearLayout llFlowLayout;
    private FlowLayout flowLayout;
    // variables
    ArrayList<MyTargetsBean> mapTargetVal =null;
    DaySummaryPresenterImpl presenter;
    ArrayList<MyTargetsBean> customerBeanBeenFilterArrayList;
    PieChart pieChart_sales_val,pieChart_tlsd,pieChart_outlets,pieChart_billcut,pieChart_eco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daysummary);
        initializeUI(this);
    }


    @Override
    public void initializeUI(Context context) {
        llFlowLayout = (LinearLayout)findViewById(R.id.llFilterLayout);
//        swipeRefresh =(SwipeRefreshLayout)findViewById(R.id.swipeRefresh_targets);
        tvremaingdays =(TextView)findViewById(R.id.tv_remaing_days);
        tv_order_val =(TextView)findViewById(R.id.tv_order_val);

        tv_tar_sal_val =(TextView)findViewById(R.id.tv_tar_sal_val);
        tv_ach_sal_val =(TextView)findViewById(R.id.tv_ach_sal_val);

        tv_tar_tlsd_val =(TextView)findViewById(R.id.tv_tar_tlsd_val);
        tv_ach_tlsd_val =(TextView)findViewById(R.id.tv_ach_tlsd_val);

        tv_tar_bill_val =(TextView)findViewById(R.id.tv_tar_bill_val);
        tv_ach_bill_val =(TextView)findViewById(R.id.tv_ach_bill_val);

        tv_tar_eco_val =(TextView)findViewById(R.id.tv_tar_eco_val);
        tv_ach_eco_val =(TextView)findViewById(R.id.tv_ach_eco_val);

        tv_days_percentage =(TextView)findViewById(R.id.tv_days_percentage);
        recyclerView =(RecyclerView) findViewById(R.id.recycler_view_targets);
        no_record_found =(TextView) findViewById(R.id.no_record_found);
        flowLayout = (FlowLayout) findViewById(R.id.llFlowLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        pieChart_outlets = (PieChart) findViewById(R.id.pieChart_outlets);
        pieChart_sales_val = (PieChart) findViewById(R.id.pieChart_sales_val);
        pieChart_tlsd = (PieChart) findViewById(R.id.pieChart_tlsd);
        pieChart_billcut = (PieChart) findViewById(R.id.pieChart_bill_cut);
        pieChart_eco = (PieChart) findViewById(R.id.pieChart_eco);
//        ConstantsUtils.setProgressColor(this, swipeRefresh);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.menu_day_summary),0);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        displayRefreshTime(ConstantsUtils.getLastSeenDateFormat(getApplicationContext(), ConstantsUtils.getMilliSeconds(
                ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.Targets, Constants.TimeStamp, getApplicationContext()))));
        initializeClickListeners();
        initializeObjects(this);
//        initializeRecyclerViewItems(new LinearLayoutManager(this));

    }



    @Override
    public void initializeClickListeners() {
//        swipeRefresh.setOnRefreshListener(this);
    }


    @Override
    public void initializeObjects(Context context) {
        mapTargetVal = new ArrayList<>();
        customerBeanBeenFilterArrayList = new ArrayList<>();
        presenter= new DaySummaryPresenterImpl(this,this,this);
//        mapTargetVal = presenter.getTargetsFromOfflineDB();
        setUI();
    }
    private void setUI(){
        if(mapTargetVal!=null && mapTargetVal.size()>0){
            for(MyTargetsBean myTargetsBean:mapTargetVal){
                if(myTargetsBean.getKPIName().equalsIgnoreCase("Visits")){
                    Constants.displayPieChart(myTargetsBean.getAchivedPercentage(),
                            pieChart_outlets,DaySummaryActivity.this,6,myTargetsBean.getMTDA()+"/"+myTargetsBean.getMonthTarget());
                    tv_order_val.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myTargetsBean.getBTD()));
                }else if(myTargetsBean.getKPIName().contains("Sales")){
                    tv_tar_sal_val.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myTargetsBean.getMonthTarget()));
                    tv_ach_sal_val.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myTargetsBean.getMTDA()));
                    Constants.displayPieChart(myTargetsBean.getAchivedPercentage(),pieChart_sales_val,DaySummaryActivity.this,8,
                            UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage())+"%");
                }else if(myTargetsBean.getKPIName().contains("ECO")){
                    tv_tar_eco_val.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getMonthTarget()));
                    tv_ach_eco_val.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getMTDA()));
                    Constants.displayPieChart(myTargetsBean.getAchivedPercentage(),pieChart_eco,DaySummaryActivity.this,8,
                            UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage())+"%");
                }else if(myTargetsBean.getKPIName().contains("Bill")){
                    tv_tar_bill_val.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getMonthTarget()));
                    tv_ach_bill_val.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getMTDA()));
                    Constants.displayPieChart(myTargetsBean.getAchivedPercentage(),pieChart_billcut,DaySummaryActivity.this,8,
                            UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage())+"%");
                }else if(myTargetsBean.getKPIName().contains("TLSD")){
                    tv_tar_tlsd_val.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getMonthTarget()));
                    tv_ach_tlsd_val.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getMTDA()));
                    Constants.displayPieChart(myTargetsBean.getAchivedPercentage(),pieChart_tlsd,DaySummaryActivity.this,8,
                            UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage())+"%");
                }
            }
        }
    }


    @Override
    public void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new SimpleRecyclerViewAdapter<>(this, R.layout.recycler_targets_list_item,this,recyclerView,no_record_found);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.refreshAdapter(mapTargetVal);
    }

    @Override
    public void initializeRecyclerViewDashBoardItems(LinearLayoutManager linearLayoutManager) {

    }


    @Override
    public void showMessage(String message) {

    }

    @Override
    public void dialogMessage(String message, String msgType) {

    }
    /**
     *  Displaying Last Refresh time and setting to Toolbar
     */
    @Override
    public void displayRefreshTime(String refreshTime) {
        try {
            String lastRefresh = "";
            if (!TextUtils.isEmpty(refreshTime)) {
                lastRefresh = getString(R.string.po_last_refreshed) + " " + refreshTime;
            }
            if (lastRefresh!=null)
                getSupportActionBar().setSubtitle(lastRefresh);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    @Override
    public void success(ArrayList success) {
        this.mapTargetVal =success;
        recyclerViewAdapter.refreshAdapter(success);
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void error(String message) {
        swipeRefresh.setRefreshing(false);

    }


    @Override
    public void showProgressDialog() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideProgressDialog() {
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void searchResult(ArrayList<MyTargetsBean> customerBeanArrayList) {
        recyclerViewAdapter.refreshAdapter(customerBeanArrayList);
    }

    @Override
    public void onRefresh() {
        presenter.onRefresh();
    }

    @Override
    public void onItemClick(MyTargetsBean customerBean, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new TargetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, MyTargetsBean customerBean) {
        ((TargetViewHolder)viewHolder).tv_kpi_name.setText(customerBean.getKPIName());
        ((TargetViewHolder)viewHolder).tv_target_val.setText(UtilConstants.removeLeadingZerowithTwoDecimal(customerBean.getMonthTarget()));
        ((TargetViewHolder)viewHolder).tv_achieved_val.setText(UtilConstants.removeLeadingZerowithTwoDecimal(customerBean.getMTDA()));
        ((TargetViewHolder)viewHolder).tv_bal_val.setText(UtilConstants.removeLeadingZerowithTwoDecimal(customerBean.getBTD()));
    }


    @Override
    public void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus) {
//        Intent intent = new Intent(DaySummaryActivity.this, BehaviourFilterActivity.class);
//        intent.putExtra(DateFilterFragment.EXTRA_DEFAULT, filterType);
//        intent.putExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_STATUS, status);
//        intent.putExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_STATUS_NAME, delvStatus);
//        startActivityForResult(intent, ConstantsUtils.ACTIVITY_RESULT_FILTER);
    }

    @Override
    public void TargetSync() {
//        mapTargetVal =presenter.getTargetsFromOfflineDB();
        displayRefreshTime(ConstantsUtils.getLastSeenDateFormat(getApplicationContext(), ConstantsUtils.getMilliSeconds(
                ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.SPChannelEvaluationList, Constants.TimeStamp, getApplicationContext()))));
    }
    @Override
    public void displayList(ArrayList<MyTargetsBean> alTargets) {
        refreshAdapter(alTargets);
    }

    @Override
    public void displayDashBoardList(ArrayList<DashBoardBean> boardBeans) {

    }

    @Override
    public void displayDashBoardError(String error) {

    }

    @Override
    public void displayAttendanceview() {

    }

    @Override
    public void showMTPProgress() {

    }

    @Override
    public void showDashMonthProgress() {

    }

    @Override
    public void showDashDayProgress() {

    }

    @Override
    public void showSOProgress() {

    }

    @Override
    public void showAttendancePB() {

    }

    @Override
    public void hideMTPProgress() {

    }

    @Override
    public void hideSOProgress() {

    }

    @Override
    public void hideAttendancePB() {

    }

    @Override
    public void disPlayMTPCount(String count) {

    }

    @Override
    public void disPlaySOCount(String count) {

    }

    @Override
    public void refreshTotalOrderVale() {

    }

    private void refreshAdapter(ArrayList<MyTargetsBean> alTargets) {
        recyclerViewAdapter.refreshAdapter(alTargets);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == ConstantsUtils.ACTIVITY_RESULT_FILTER) {
//            String filterType = data.getStringExtra(DateFilterFragment.EXTRA_DEFAULT);
//            String soStatus = data.getStringExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_STATUS);
//            String statusName = data.getStringExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_STATUS_NAME);
//            String delvStatus = data.getStringExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_DELV_STATUS);
//            String delvStatusName = data.getStringExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_DELV_STATUS_NAME);
//            customerBeanBeenFilterArrayList.clear();
//            if (!statusName.equalsIgnoreCase(Constants.ALL)) {
//                for (int i = 0; i < mapTargetVal.size(); i++) {
//                    if (mapTargetVal.get(i).getKPIName().equalsIgnoreCase(soStatus)){
//                        MyTargetsBean customerBean = mapTargetVal.get(i);
//                        customerBeanBeenFilterArrayList.add(customerBean);
//                    }
//                }
//                llFlowLayout.setVisibility(View.VISIBLE);
//                recyclerViewAdapter.refreshAdapter(customerBeanBeenFilterArrayList);
//            } else {
//                llFlowLayout.setVisibility(View.GONE);
//                recyclerViewAdapter.refreshAdapter(mapTargetVal);
//            }
//            presenter.startFilter(requestCode, resultCode, data);
//        }
    }
    @Override
    public void setFilterDate(String filterType) {
        try {
            String[] filterTypeArr = filterType.split(", ");
            ConstantsUtils.displayFilter(filterTypeArr, flowLayout, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
