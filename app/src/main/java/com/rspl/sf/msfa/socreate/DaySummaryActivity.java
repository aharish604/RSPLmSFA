package com.rspl.sf.msfa.socreate;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.asyncTask.AsyncTaskItemInterface;
import com.rspl.sf.msfa.asyncTask.GetDataFromOnlineDB;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.CustomerBean;
import com.rspl.sf.msfa.mbo.MyTargetsBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.smp.client.odata.ODataEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by e10526 on 1/24/2017.
 *
 */

public class DaySummaryActivity  extends AppCompatActivity implements AsyncTaskItemInterface {
    private boolean mBooleanRemoveScrollViews = true;
    private ArrayList<MyTargetsBean> alMyTargets = null;
    private String mStrSpGuid = "";
    private ArrayList<MyTargetsBean> alKpiList = null;
    Map<String,Double> mapBTD=new HashMap<>();
    private Map<String,MyTargetsBean> mapMyTargetVal=new HashMap<>();
    private TextView tv_traget_out_visit,tv_actual_out_visited,tv_total_order_value;
    private ArrayList<CustomerBean> alTodaysRetailers;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_summary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_day_summary),0);
        initUI();
    }

    /*Initializes UI*/
    void initUI(){
        tv_traget_out_visit = (TextView)findViewById(R.id.tv_traget_out_visit);
        tv_actual_out_visited = (TextView)findViewById(R.id.tv_actual_out_visited);
        tv_total_order_value = (TextView)findViewById(R.id.tv_total_order_value);
        alTodaysRetailers = Constants.getTodaysBeatRetailers();
        setValuesToUI();
       /* String[][] mArrayDistributors =
        try{
            mStrSpGuid = mArrayDistributors[8][0];
        }catch (Exception e){
            e.printStackTrace();
        }*/
       mStrSpGuid = Constants.getSPGUID(Constants.SPGUID);


        getDataFromOnline();
        getSystemKPI(Constants.getCurrentMonth(), Constants.getCurrentYear());
        getMyTargetsList();
    }
    private ProgressDialog progressDialog = null;
    private void getDataFromOnline(){
        progressDialog = ConstantsUtils.showProgressDialog(DaySummaryActivity.this, getString(R.string.app_loading));
        String query= Constants.KPISet + "?$filter = " + Constants.Month + " eq '" + Constants.getCurrentMonth() + "' " + "and " + Constants.Year + " eq '" + Constants.getCurrentYear() + "'  ";
        new GetDataFromOnlineDB(DaySummaryActivity.this, Constants.str_01, this,query,false).execute();
    }

    private void getBalanceVisit(ArrayList<CustomerBean> alTodaysRetailers){
               String mStrRetQry = "";
        if(alTodaysRetailers!=null && alTodaysRetailers.size()>0){
            for(int i=0;i<alTodaysRetailers.size();i++){
                if (i == 0 && i == alTodaysRetailers.size() - 1) {
                    mStrRetQry = mStrRetQry
                            + "("+ Constants.VisitCPGUID+"%20eq%20'"
                            + alTodaysRetailers.get(i).getCpGuidStringFormat() + "')";

                } else if (i == 0) {
                    mStrRetQry = mStrRetQry
                            + "("+ Constants.VisitCPGUID+"%20eq%20'"
                            + alTodaysRetailers.get(i).getCpGuidStringFormat() + "'";

                } else if (i == alTodaysRetailers.size() - 1) {
                    mStrRetQry = mStrRetQry
                            + "%20or%20"+ Constants.VisitCPGUID+"%20eq%20'"
                            + alTodaysRetailers.get(i).getCpGuidStringFormat() + "')";
                } else {
                    mStrRetQry = mStrRetQry
                            + "%20or%20"+ Constants.VisitCPGUID+"%20eq%20'"
                            + alTodaysRetailers.get(i).getCpGuidStringFormat() + "'";
                }
            }




        }
    }

    private void setValuesToUI(){
        tv_traget_out_visit.setText(Constants.getVisitTargetForToday(this));
        tv_actual_out_visited.setText(Constants.getVisitedRetailerCount());
//        tv_total_order_value.setText(Constants.getTotalOrderValue(DaySummaryActivity.this, UtilConstants.getNewDate(),alTodaysRetailers));
    }

    /*Gets kpiList for selected month and year*/
    private void getSystemKPI(String month, String mStrCurrentYear) {
        try {
            alKpiList = OfflineManager.getKpiSetGuidList(Constants.KPISet + "?$filter = " + Constants.Month + " eq '" + month + "' " + "and " + Constants.Year + " eq '" + mStrCurrentYear + "'  ","");

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }

    }

    /*Get targets for sales person  based on query*/
    private void getMyTargetsList() {
        try {
            if (alKpiList !=null && alKpiList.size()>0) {
                alMyTargets = OfflineManager.getMyTargets(alKpiList, mStrSpGuid,"");
            }
            mapMyTargetVal = getALMyTargetList(alMyTargets);
            displayMyTargetsValues();
        } catch (OfflineODataStoreException e) {
            displayMyTargetsValues();
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
    }

    //ToDo sum of actual and target quantity/Value based on kpi code and assign to map table
    private Map<String,MyTargetsBean> getALMyTargetList(ArrayList<MyTargetsBean> alMyTargets){
        Map<String,MyTargetsBean> mapMyTargetBean=new HashMap<>();
        if(alMyTargets!=null && alMyTargets.size()>0){
            for(MyTargetsBean bean:alMyTargets)
                if(mapBTD.containsKey(bean.getKPICode())) {
                    double mDoubBTD = Double.parseDouble(bean.getBTD())+  mapBTD.get(bean.getKPICode());
                    mapBTD.put(bean.getKPICode(), mDoubBTD);
                    mapMyTargetBean.put(bean.getKPICode(),bean);
                }else {
                    double mDoubBTD = Double.parseDouble(bean.getBTD()) ;
                    mapBTD.put(bean.getKPICode(), mDoubBTD);
                    mapMyTargetBean.put(bean.getKPICode(),bean);
                }
        }
        return mapMyTargetBean;
    }

    /*Displays Target values*/
    @SuppressLint("InflateParams")
    private void displayMyTargetsValues() {

        ScrollView scroll_my_stock_list = (ScrollView) findViewById(R.id.scroll_day_summary_list);
        if (!mBooleanRemoveScrollViews) {
            scroll_my_stock_list.removeAllViews();
        }

        mBooleanRemoveScrollViews = false;

        @SuppressLint("InflateParams")
        TableLayout tlMyTargets = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_table, null, false);


        LinearLayout llMyTargets;

        if(!mapMyTargetVal.isEmpty()){

            Iterator iterator = mapMyTargetVal.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next().toString();

                llMyTargets = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.day_summary_list_item,
                                null, false);

                final MyTargetsBean myTargetsBean = mapMyTargetVal.get(key);

                ((TextView) llMyTargets.findViewById(R.id.tv_kpi_value))
                        .setText(myTargetsBean.getKPIName());

                ((TextView) llMyTargets
                        .findViewById(R.id.tv_day_target_value))
                        .setText(UtilConstants.removeLeadingZeroVal(mapBTD.get(key).toString()));

                ((TextView) llMyTargets
                        .findViewById(R.id.tv_day_achieved))
                        .setText(UtilConstants.removeLeadingZeroVal(mapBTD.get(key).toString()));

                tlMyTargets.addView(llMyTargets);

            }

        } else {

            LinearLayout llEmptyLayout = (LinearLayout) LayoutInflater.from(this)
                    .inflate(R.layout.empty_layout, null);

            tlMyTargets.addView(llEmptyLayout);
        }


        scroll_my_stock_list.addView(tlMyTargets);
        scroll_my_stock_list.requestLayout();
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
    public void soItemLoaded(List<ODataEntity> entity, Boolean isStoreOpened, String mReqId) {
        closeProgDialog();
        if (!isStoreOpened) {

            ConstantsUtils.dialogBoxWithButton(DaySummaryActivity.this, "", Constants.makeMsgReqError(Constants.ErrorNo, DaySummaryActivity.this, false), getString(R.string.ok), "", null);
        } else {
            if (entity!=null) {
                if(Constants.str_02.equalsIgnoreCase(mReqId)){
                    Constants.oDataEntity =  entity;
                  /*  Intent intent=new Intent(DaySummaryActivity.this,RetailerListActivity.class);
                    startActivity(intent);*/
                }
            }
        }
    }

    private void closeProgDialog(){
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
