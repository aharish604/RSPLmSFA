package com.rspl.sf.msfa.reports;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.MyTargetsBean;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by e10526 on 1/18/2017.
 *
 */
public class MyTargetsActivity extends AppCompatActivity {
    private ArrayList<MyTargetsBean> alMyTargets = null;
    private boolean mBooleanRemoveScrollViews = true;
    private String mStrSPGuid = "";
    private String[][] mArrayDistributors;

    private ArrayList<MyTargetsBean> alKpiList = null;

    Map<String,Double> mapMonthTarget=new HashMap<>();
    Map<String,Double> mapMonthAchived=new HashMap<>();
    private Map<String,MyTargetsBean> mapMyTargetVal=new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_targets);
        //Initialize action bar with back button(true)
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_my_targets), 0);
        initUI();
    }

    /*Initializes UI*/
    void initUI(){
        getDistributors();
        getSystemKPI(Constants.getCurrentMonth(), Constants.getCurrentYear());
        getMyTargetsList();
    }



    /*Get targets for sales person  based on query*/
    private void getMyTargetsList() {
        try {
            if (alKpiList !=null && alKpiList.size()>0) {
                final Calendar c = Calendar.getInstance();
                int fiscalYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);

                String mon = "";
                String day = "";
                int mnt = 0;
                mnt = mMonth + 1;
                if (mnt < 10)
                    mon = "0" + mnt;
                else
                    mon = "" + mnt;
                String mStrMonthYear =  mon + fiscalYear;

                alMyTargets = OfflineManager.getMyTargets(alKpiList, mStrSPGuid,mStrMonthYear);
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
                if(mapMonthTarget.containsKey(bean.getKPICode())) {
                    double mDoubMonthTarget = Double.parseDouble(bean.getMonthTarget()) +  mapMonthTarget.get(bean.getKPICode());
                    double mDoubMonthAchived = Double.parseDouble(bean.getMTDA()) +  mapMonthAchived.get(bean.getKPICode());

                    mapMonthTarget.put(bean.getKPICode(), mDoubMonthTarget);
                    mapMonthAchived.put(bean.getKPICode(), mDoubMonthAchived);
                    mapMyTargetBean.put(bean.getKPICode(),bean);
                }else {
                    double mDoubMonthTarget = Double.parseDouble(bean.getMonthTarget()) ;
                    double mDoubMonthAchived = Double.parseDouble(bean.getMTDA()) ;
                    double mDoubAchivedPer = Double.parseDouble(bean.getAchivedPercentage());
                    double mDoubBTD = Double.parseDouble(bean.getBTD());

                    mapMonthTarget.put(bean.getKPICode(), mDoubMonthTarget);
                    mapMonthAchived.put(bean.getKPICode(), mDoubMonthAchived);
                    mapMyTargetBean.put(bean.getKPICode(),bean);
                }
        }


        return mapMyTargetBean;
    }


    /*Displays Target values*/
    @SuppressLint("InflateParams")
    private void displayMyTargetsValues() {

        ScrollView scroll_my_stock_list = (ScrollView) findViewById(R.id.scroll_my_targets_list);
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
                            .inflate(R.layout.my_targets_list_item,
                                    null, false);

                   final MyTargetsBean myTargetsBean = mapMyTargetVal.get(key);

                    ((TextView) llMyTargets.findViewById(R.id.tv_kpi_value))
                            .setText(myTargetsBean.getKPIName());

                    ((TextView) llMyTargets
                            .findViewById(R.id.tv_month_target_value))
                            .setText(UtilConstants.removeLeadingZeroVal(mapMonthTarget.get(key).toString()));

                    ((TextView) llMyTargets
                            .findViewById(R.id.tv_mtda_value))
                            .setText(UtilConstants.removeLeadingZeroVal(mapMonthAchived.get(key).toString()));



                    double achivedPer = OfflineManager.getAchivedPer(mapMonthTarget.get(key).toString(),mapMonthAchived.get(key).toString());

                     double BTDPer = OfflineManager.getBTD(mapMonthTarget.get(key).toString(),mapMonthAchived.get(key).toString());

                    ((TextView) llMyTargets
                            .findViewById(R.id.tv_achived_per_value))
                            .setText(UtilConstants.removeLeadingZeroVal(achivedPer+""));

                    ((TextView) llMyTargets
                            .findViewById(R.id.tv_btd_val))
                            .setText(UtilConstants.removeLeadingZeroVal(BTDPer+""));

                    ImageView iv_arraow_sel = (ImageView) llMyTargets
                            .findViewById(R.id.iv_arraow_value);

                LinearLayout ll_navigate_layout = (LinearLayout) llMyTargets
                        .findViewById(R.id.ll_navigate_layout);

                    ll_navigate_layout.setVisibility(View.VISIBLE);

                if (!isFindMoreThanOneTargetItemsRecord(myTargetsBean)){
                    iv_arraow_sel.setImageDrawable(null);
                }


                    iv_arraow_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                                Intent intentCrsSkuGrp = new Intent(MyTargetsActivity.this, CRSSKUGroupWiseTargetsActivity.class);
                                intentCrsSkuGrp.putExtra(Constants.KPICode, myTargetsBean.getKPICode());
                                intentCrsSkuGrp.putExtra(Constants.KPIName, myTargetsBean.getKPIName());
                                intentCrsSkuGrp.putExtra(Constants.KPIGUID, myTargetsBean.getKpiGuid());
                                intentCrsSkuGrp.putExtra(Constants.CalculationBase, myTargetsBean.getCalculationBase());
                                intentCrsSkuGrp.putExtra(Constants.KPIFor, myTargetsBean.getKPIFor());
                                intentCrsSkuGrp.putExtra(Constants.RollUpTo, myTargetsBean.getRollUpTo());
                                intentCrsSkuGrp.putExtra(Constants.CalculationSource, myTargetsBean.getCalculationSource());
                                intentCrsSkuGrp.putExtra(Constants.PartnerMgrGUID, mStrSPGuid);
                                startActivity(intentCrsSkuGrp);
                            }
                    });
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


    private boolean isFindMoreThanOneTargetItemsRecord(MyTargetsBean myTargetsBean){
        boolean isTargetItemRecAvalible = false;
        String qryTargets = Constants.Targets+ "?$filter=" +Constants.KPIGUID+ " eq guid'"
                + myTargetsBean.getKpiGuid().toUpperCase()+"'" ;
        try {
            ArrayList<MyTargetsBean> alMyTargets = OfflineManager.getMyTargetsList(qryTargets, mStrSPGuid,
                    myTargetsBean.getKPIName(), myTargetsBean.getKPICode(), myTargetsBean.getKpiGuid().toUpperCase(),
                    myTargetsBean.getCalculationBase(), myTargetsBean.getKPIFor(),
                    myTargetsBean.getCalculationSource(), myTargetsBean.getRollUpTo(),"","");
            if(alMyTargets!=null && alMyTargets.size()>0){
                if(alMyTargets.size()>1){
                    isTargetItemRecAvalible = true;
                }else{
                    isTargetItemRecAvalible = false;
                }
            }else{
                isTargetItemRecAvalible =false;
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        return  isTargetItemRecAvalible;
    }


    /*Gets kpiList for selected month and year*/
    private void getSystemKPI(String month, String mStrCurrentYear) {
        try {
            String mStrMyStockQry;
                mStrMyStockQry = Constants.KPISet + "?$filter = " + Constants.Month + " eq '" + month + "' " +
                        "and " + Constants.Year + " eq '" + mStrCurrentYear + "'  ";

            alKpiList = OfflineManager.getKpiSetGuidList(mStrMyStockQry);

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
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


    /*Get sales person details*/
    private void getDistributors() {

        String qryStr = Constants.SalesPersons + "?$filter=(" + Constants.CPGUID + " ne '' and " + Constants.CPGUID + " ne null) &$apply=groupby((" + Constants.CPGUID + "))";
        try {
            mArrayDistributors = OfflineManager.getDistributorList(qryStr);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if (mArrayDistributors == null) {
            mArrayDistributors = new String[7][1];
            mArrayDistributors[0][0] = "";
            mArrayDistributors[1][0] = "";
            mArrayDistributors[2][0] = "";
            mArrayDistributors[3][0] = "";
            mArrayDistributors[4][0] = "";
            mArrayDistributors[5][0] = "";
            mArrayDistributors[6][0] = "";
        } else {
            if (mArrayDistributors[0].length > 0) {
                mStrSPGuid = mArrayDistributors[8][0];
            }
        }
    }
}
