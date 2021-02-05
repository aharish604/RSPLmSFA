package com.rspl.sf.msfa.reports.targets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.rspl.sf.msfa.reports.materialgrouptargets.MaterialGroupTargetsActivity;
import com.rspl.sf.msfa.ui.FlowLayout;
import com.rspl.sf.msfa.ui.TextProgressBar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

//import com.arteriatech.sf.reports.behaviourlist.filter.BehaviourFilterActivity;


/**
 * Created by e10526 on 03-02-2017.
 *
 */
public class TargetsActivity extends AppCompatActivity implements TargetViewPresenter,TargetViewPresenter.TargetResponse,SwipeRefreshLayout.OnRefreshListener,AdapterInterface<MyTargetsBean> {


    // android components
    SwipeRefreshLayout swipeRefresh;
    RecyclerView recyclerView;
    TextView no_record_found, tvremaingdays,tv_days_percentage;
    Toolbar toolbar;
    SimpleRecyclerViewAdapter<MyTargetsBean> recyclerViewAdapter=null;
    LinearLayout llFlowLayout;
    private FlowLayout flowLayout;
    // variables
    ArrayList<MyTargetsBean> mapTargetVal =null;
    TargetsPresenterImpl presenter;
    ArrayList<MyTargetsBean> customerBeanBeenFilterArrayList;
    private TextProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_targets);
        initializeUI(this);
    }


    private void getNoOfdays(){
//                Date mDateFirstDay = getFirstDayOfQuarter(null);
//        Date mDateLastDay = getLastDayOfQuarter(null);
//        int day = getNoOfDays(new Date(),mDateLastDay);
//        int numDays = getNoOfDays(mDateFirstDay,mDateLastDay);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int numDays = calendar.getActualMaximum(Calendar.DATE);
        int remaingDays = numDays - day;

        double mDoubPer = 0;
        try {
            mDoubPer = Double.parseDouble(day+"")/Double.parseDouble(numDays+"")*100;
        } catch (Exception e) {
            mDoubPer=0.0;
            e.printStackTrace();
        }
        String percentageValue = UtilConstants.removeDecimalPoints(mDoubPer+"");
        tvremaingdays.setText(remaingDays+" "+getString(R.string.lbl_days));
        tv_days_percentage.setText(percentageValue+" %");
        progressBar.setProgress(Integer.parseInt(percentageValue));
        progressBar.setText(percentageValue+" %");


    }
    private int getNoOfDays(Date today,Date date2){
        long diff =  date2.getTime() - today.getTime();
        int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));

        return  numOfDays;
    }
    private static Date getFirstDayOfQuarter(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)/3 * 3);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    private static Date getLastDayOfQuarter(Date date) {
        Calendar cal = Calendar.getInstance();
//        cal.setTime(date);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)/3 * 3 + 2);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }
    @Override
    public void initializeUI(Context context) {
        llFlowLayout = (LinearLayout)findViewById(R.id.llFilterLayout);
        swipeRefresh =(SwipeRefreshLayout)findViewById(R.id.swipeRefresh_targets);
        tvremaingdays =(TextView)findViewById(R.id.tv_remaing_days);
        tv_days_percentage =(TextView)findViewById(R.id.tv_days_percentage);
        recyclerView =(RecyclerView) findViewById(R.id.recycler_view_targets);
        no_record_found =(TextView) findViewById(R.id.no_record_found);
        flowLayout = (FlowLayout) findViewById(R.id.llFlowLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBar =(TextProgressBar)findViewById(R.id.progressBar);
        ConstantsUtils.setProgressColor(this, swipeRefresh);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_mytargets),0);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        displayRefreshTime(ConstantsUtils.getLastSeenDateFormat(getApplicationContext(), ConstantsUtils.getMilliSeconds(
                ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.Targets, Constants.TimeStamp, getApplicationContext()))));
        getNoOfdays();
        initializeClickListeners();
        initializeRecyclerViewItems(new LinearLayoutManager(this));
        initializeObjects(this);
    }


    @Override
    public void initializeClickListeners() {
        swipeRefresh.setOnRefreshListener(this);
    }


    @Override
    public void initializeObjects(Context context) {
        mapTargetVal = new ArrayList<>();
        customerBeanBeenFilterArrayList = new ArrayList<>();
        presenter= new TargetsPresenterImpl(this,this,this);
        presenter.onStart();
//        mapTargetVal = presenter.getTargetsFromOfflineDB();
    }

    @Override
    public void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new SimpleRecyclerViewAdapter<>(this, R.layout.recycler_targets_list_item,this,recyclerView,no_record_found);
        recyclerView.setAdapter(recyclerViewAdapter);
        if(mapTargetVal!=null && mapTargetVal.size()>0) {
            recyclerViewAdapter.refreshAdapter(mapTargetVal);
        }
    }


    @Override
    public void showMessage(String message) {
        ConstantsUtils.displayLongToast(TargetsActivity.this,message);
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
        try {
            presenter.onRefresh();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(MyTargetsBean myTargetsBean, View view, int i) {
        Intent intentCrsSkuGrp = new Intent(TargetsActivity.this, MaterialGroupTargetsActivity.class);
        intentCrsSkuGrp.putExtra(Constants.KPICode, myTargetsBean.getKPICode());
        intentCrsSkuGrp.putExtra(Constants.KPIName, myTargetsBean.getKPIName());
        intentCrsSkuGrp.putExtra(Constants.KPIGUID, myTargetsBean.getKpiGuid());
        intentCrsSkuGrp.putExtra(Constants.CalculationBase, myTargetsBean.getCalculationBase());
        intentCrsSkuGrp.putExtra(Constants.KPIFor, myTargetsBean.getKPIFor());
        intentCrsSkuGrp.putExtra(Constants.RollUpTo, myTargetsBean.getRollUpTo());
        intentCrsSkuGrp.putExtra(Constants.KPICategory, myTargetsBean.getKPICategory());
        intentCrsSkuGrp.putExtra(Constants.CalculationSource, myTargetsBean.getCalculationSource());
        intentCrsSkuGrp.putExtra(Constants.PartnerMgrGUID, Constants.getSPGUID(Constants.SPGUID));
        intentCrsSkuGrp.putExtra(Constants.Periodicity, myTargetsBean.getPeriodicity());
        intentCrsSkuGrp.putExtra(Constants.KPISet, myTargetsBean);
        startActivity(intentCrsSkuGrp);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new TargetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, MyTargetsBean customerBean) {
        if(customerBean.getCalculationBase().equalsIgnoreCase(Constants.str_01)){
            ((TargetViewHolder)viewHolder).tv_kpi_name.setText(customerBean.getPeriodicityDesc()+" Target ("+customerBean.getKPIName()+")");
        }else{
            ((TargetViewHolder)viewHolder).tv_kpi_name.setText(customerBean.getKPIName());
        }

        if(customerBean.getCalculationBase().equalsIgnoreCase(Constants.str_01)){
            ((TargetViewHolder)viewHolder).tv_target_val.setText(customerBean.getMonthTarget());
            ((TargetViewHolder)viewHolder).tv_achieved_val.setText(customerBean.getMTDA());
            ((TargetViewHolder)viewHolder).tv_bal_val.setText(customerBean.getBTD());
            ((TargetViewHolder)viewHolder).tv_uom.setVisibility(View.VISIBLE);
            if(customerBean.getUOM().equalsIgnoreCase("TO")){
                ((TargetViewHolder)viewHolder).tv_uom.setText("TON");
            }else{
                ((TargetViewHolder)viewHolder).tv_uom.setText(customerBean.getUOM());
            }

        }else{
            ((TargetViewHolder)viewHolder).tv_target_val.setText(UtilConstants.removeLeadingZerowithTwoDecimal(customerBean.getMonthTarget()));
            ((TargetViewHolder)viewHolder).tv_achieved_val.setText(UtilConstants.removeLeadingZerowithTwoDecimal(customerBean.getMTDA()));
            ((TargetViewHolder)viewHolder).tv_bal_val.setText(UtilConstants.removeLeadingZerowithTwoDecimal(customerBean.getBTD()));
        }


        displayPieChart(customerBean.getAchivedPercentage(),((TargetViewHolder)viewHolder).pieChart_target);
    }

    private void displayPieChart(String targetPer, PieChart pieChart) {
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        //spacing between graph and margin
//        mChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setExtraOffsets(-5,-5,-5,-5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);
//        pieChart.setCenterText(Constants.generateCenterSpannableText(ConstantsUtils.decimalZeroBasedOnValue(targetPer) + "%"));
        if(targetPer==null){
            targetPer = "0.00";
        }
        Double mDouTarVal = 0.00;
        try {
            mDouTarVal = Double.parseDouble(targetPer);
        } catch (NumberFormatException e) {
            mDouTarVal = 0.00;
            e.printStackTrace();
        }
        targetPer =  String.valueOf(ConstantsUtils.decimalRoundOff(new BigDecimal(targetPer), 2));//alternativeUOMQty+"";
        pieChart.setCenterText(Constants.generateCenterSpannableText(targetPer + "%"));

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(75f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setDrawCenterText(true);

        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(false);
        if(mDouTarVal>100){
            Constants.setPieChartData("100", pieChart,TargetsActivity.this);
        }else{
            Constants.setPieChartData(targetPer, pieChart,TargetsActivity.this);
        }

        pieChart.animateXY(1500, 1500);
//        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // entry label styling
        pieChart.getLegend().setEnabled(false);
        pieChart.setDrawEntryLabels(false);

//        pieChart.getTransformer().prepareMatrixValuePx(chart);
//        pieChart.mat().prepareMatrixOffset(chart);

//        pieChart.getContentRect().set(0, 0, pieChart.getWidth(), pieChart.getHeight());
        pieChart.invalidate();
    }

    @Override
    public void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus) {
//        Intent intent = new Intent(MaterialGroupTargetsActivity.this, BehaviourFilterActivity.class);
//        intent.putExtra(DateFilterFragment.EXTRA_DEFAULT, filterType);
//        intent.putExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_STATUS, status);
//        intent.putExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_STATUS_NAME, delvStatus);
//        startActivityForResult(intent, ConstantsUtils.ACTIVITY_RESULT_FILTER);
    }

    @Override
    public void TargetSync() {
//        mapTargetVal =presenter.getTargetsFromOfflineDB();
        presenter.onStart();
//        displayRefreshTime(ConstantsUtils.getLastSeenDateFormat(getApplicationContext(), ConstantsUtils.getMilliSeconds(
//                ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.SPChannelEvaluationList, Constants.TimeStamp, getApplicationContext()))));
    }
    @Override
    public void displayList(ArrayList<MyTargetsBean> alTargets) {
        try {
            displayRefreshTime(ConstantsUtils.getLastSeenDateFormat(getApplicationContext(), ConstantsUtils.getMilliSeconds(
                    ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.Targets, Constants.TimeStamp, getApplicationContext()))));
        } catch (Exception e) {
            e.printStackTrace();
        }

        refreshAdapter(alTargets);
    }

    @Override
    public void displayMessage(String msg) {
        ConstantsUtils.displayLongToast(TargetsActivity.this,msg);
    }


    private void refreshAdapter(ArrayList<MyTargetsBean> alTargets) {
        if(alTargets!=null && alTargets.size()>0) {
            recyclerViewAdapter.refreshAdapter(alTargets);
        }
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

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}
