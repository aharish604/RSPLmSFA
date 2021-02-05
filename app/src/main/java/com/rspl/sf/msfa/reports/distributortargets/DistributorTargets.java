package com.rspl.sf.msfa.reports.distributortargets;

import android.content.Context;
import android.graphics.Color;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.github.mikephil.charting.charts.PieChart;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.MyTargetsBean;
import com.rspl.sf.msfa.ui.TextProgressBar;

import java.math.BigDecimal;
import java.util.ArrayList;

public class DistributorTargets extends AppCompatActivity implements DistributorTargetViewPresenter,SwipeRefreshLayout.OnRefreshListener, AdapterInterface<MyTargetsBean> {

    Bundle bundleExtras = null;
    MyTargetsBean myTargetsBean=null;
    DistributorTargetImpl presenter;
    ArrayList<MyTargetsBean> mapTargetVal =null;
    RecyclerView recyclerView;
    TextView no_record_found, tvremaingdays,tv_days_percentage,tv_remaing_days_text,tv_percentage_txt;
    Toolbar toolbar;
    SimpleRecyclerViewAdapter<MyTargetsBean> recyclerViewAdapter=null;
    private TextProgressBar progressBar;
    SwipeRefreshLayout swipeRefresh;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor_targets);
        bundleExtras = getIntent().getExtras();
        if(bundleExtras!=null){
            myTargetsBean = (MyTargetsBean) getIntent().getSerializableExtra(Constants.KPISet);
        }
        if(myTargetsBean==null){
            myTargetsBean =new MyTargetsBean();
        }
        initializeUI(this);
    }

    @Override
    public void initializeUI(Context context) {
        tvremaingdays =(TextView)findViewById(R.id.tv_remaing_days);
        tv_remaing_days_text =(TextView)findViewById(R.id.tv_remaing_days_text);
        tv_percentage_txt =(TextView)findViewById(R.id.tv_percentage_txt);
        tv_days_percentage =(TextView)findViewById(R.id.tv_days_percentage);
        recyclerView =(RecyclerView) findViewById(R.id.recycler_view_targets);
        no_record_found =(TextView) findViewById(R.id.no_record_found);
        progressBar =(TextProgressBar)findViewById(R.id.progressBar);
        swipeRefresh =(SwipeRefreshLayout)findViewById(R.id.swipeRefresh_targets);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_custwise_targets),0);
        ConstantsUtils.setProgressColor(this, swipeRefresh);

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
        tv_percentage_txt.setText("Achieved");


    }
    private void getNoOfdays(){
       /* Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int numDays = calendar.getActualMaximum(Calendar.DATE);
        int remaingDays = numDays - day;

        double mDoubPer = 0;
        try {
            mDoubPer = Double.parseDouble(day+"")/Double.parseDouble(numDays+"")*100;
        } catch (Exception e) {
            mDoubPer=0.0;
            e.printStackTrace();
        }*/
//        String percentageValue = UtilConstants.removeDecimalPoints(mDoubPer+"");
        String percentageValue = UtilConstants.removeDecimalPoints(UtilConstants.removeLeadingZero(myTargetsBean.getAchivedPercentage()));
        tv_remaing_days_text.setVisibility(View.INVISIBLE);
//        tvremaingdays.setText(remaingDays+" "+getString(R.string.lbl_days));
        if(myTargetsBean.getCalculationBase().equalsIgnoreCase(Constants.str_01)){
            tvremaingdays.setText(myTargetsBean.getPeriodicityDesc()+" Target ("+myTargetsBean.getKPIName()+")");
        }else{
            tvremaingdays.setText(myTargetsBean.getKPIName());
        }

        tv_days_percentage.setText(percentageValue+" %");
        progressBar.setProgress(Integer.parseInt(percentageValue));
        progressBar.setText(percentageValue+" %");
        tv_percentage_txt.setText("Achieved");

    }

    @Override
    public void initializeClickListeners() {
        swipeRefresh.setOnRefreshListener(this);


    }

    @Override
    public void initializeObjects(Context context) {
        mapTargetVal = new ArrayList<>();
        presenter= new DistributorTargetImpl(this,this,this,bundleExtras);
        presenter.onStart();
    }

    @Override
    public void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new SimpleRecyclerViewAdapter<>(this, R.layout.custwise_brand_target_item,this,recyclerView,no_record_found);
        recyclerView.setAdapter(recyclerViewAdapter);
        if(mapTargetVal!=null && mapTargetVal.size()>0) {
            recyclerViewAdapter.refreshAdapter(mapTargetVal);
        }

    }

    @Override
    public void showMessage(String message) {
        ConstantsUtils.displayLongToast(DistributorTargets.this,message);

    }

    @Override
    public void dialogMessage(String message, String msgType) {

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
    public void displayList(ArrayList<MyTargetsBean> alTargets) {
        if(alTargets.size()>0) {
            tvremaingdays.setText(alTargets.get(0).getMaterialGrpDesc());
        }else {
            tvremaingdays.setText("");
        }
        refreshAdapter(alTargets);
    }

    @Override
    public void displayMessage(String msg) {
        ConstantsUtils.displayLongToast(DistributorTargets.this,msg);

    }


    @Override
    public void onItemClick(MyTargetsBean myTargetsBean, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new DistibutorTargetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, MyTargetsBean customerBean) {
        ((DistibutorTargetViewHolder) viewHolder).tv_kpi_name.setText(customerBean.getPartnerName() + "(" + customerBean.getPartnerNo() + ")");
        if (customerBean.getCalculationBase().equalsIgnoreCase(Constants.str_01)) {
            ((DistibutorTargetViewHolder) viewHolder).tv_target_val.setText(customerBean.getMonthTarget());
            ((DistibutorTargetViewHolder) viewHolder).tv_achieved_val.setText(customerBean.getMTDA());
            ((DistibutorTargetViewHolder) viewHolder).tv_bal_val.setText(customerBean.getBTD());
            ((DistibutorTargetViewHolder) viewHolder).tv_uom.setVisibility(View.VISIBLE);
            if (customerBean.getUOM().equalsIgnoreCase("TO")) {
                ((DistibutorTargetViewHolder) viewHolder).tv_uom.setText("TON");
            } else {
                ((DistibutorTargetViewHolder) viewHolder).tv_uom.setText(customerBean.getUOM());
            }
        } else {
            ((DistibutorTargetViewHolder) viewHolder).tv_target_val.setText(UtilConstants.removeLeadingZerowithTwoDecimal(customerBean.getMonthTarget()));
            ((DistibutorTargetViewHolder) viewHolder).tv_achieved_val.setText(UtilConstants.removeLeadingZerowithTwoDecimal(customerBean.getMTDA()));
            ((DistibutorTargetViewHolder) viewHolder).tv_bal_val.setText(UtilConstants.removeLeadingZerowithTwoDecimal(customerBean.getBTD()));
        }
        ((DistibutorTargetViewHolder) viewHolder).tv_percentage_val.setText(UtilConstants.removeLeadingZerowithTwoDecimal(customerBean.getAchivedPercentage()));

        Double mDouTarVal = 0.00;
        if (!TextUtils.isEmpty(customerBean.getAchivedPercentage())) {

            try {
                mDouTarVal = Double.parseDouble(customerBean.getAchivedPercentage());
            } catch (NumberFormatException e) {
                mDouTarVal = 0.00;
                e.printStackTrace();
            }
        }
        if(mDouTarVal==0)
            ((DistibutorTargetViewHolder) viewHolder).cl_achev_status.setBackgroundColor(getResources().getColor(R.color.RED));
        else if(mDouTarVal>0 && mDouTarVal<=99)
            ((DistibutorTargetViewHolder) viewHolder).cl_achev_status.setBackgroundColor(getResources().getColor(R.color.YELLOW));
        else
            ((DistibutorTargetViewHolder) viewHolder).cl_achev_status.setBackgroundColor(getResources().getColor(R.color.GREEN));



      //  displayPieChart(customerBean.getAchivedPercentage(),((DistibutorTargetViewHolder)viewHolder).pieChart_target);

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
            Constants.setPieChartData("100", pieChart,DistributorTargets.this);
        }else{
            Constants.setPieChartData(targetPer, pieChart,DistributorTargets.this);
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


    private void refreshAdapter(ArrayList<MyTargetsBean> alTargets) {
        if(alTargets!=null && alTargets.size()>0) {
            recyclerViewAdapter.refreshAdapter(alTargets);
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
    public void onRefresh() {
        swipeRefresh.setRefreshing(false);
    }
}
