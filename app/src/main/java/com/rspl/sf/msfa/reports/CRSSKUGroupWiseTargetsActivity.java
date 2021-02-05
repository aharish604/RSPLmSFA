package com.rspl.sf.msfa.reports;

import android.annotation.SuppressLint;
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
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.MyTargetsBean;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by e10526 on 1/18/2017.
 *
 */

public class CRSSKUGroupWiseTargetsActivity extends AppCompatActivity {
    private boolean mBooleanRemoveScrollViews = true;
    private String mStrBundleKpiCode ="",mStrBundleKpiName="",mStrBundleKpiGUID="",mStrBundleRollup="",
            mStrBundleKpiFor="",mStrBundleCalBased="",mStrBundleCalSource="",mStrParnerGuid="";

    private Map<String,MyTargetsBean> mapMyTargetVal=new HashMap<>();
    Map<String,Double> mapMonthTarget=new HashMap<>();
    Map<String,Double> mapMonthAchived=new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
//        ActionBarView.initActionBarView(this, true,getString(R.string.title_sku_grp_targets));
        setContentView(R.layout.activity_crssku_group_wise_targets);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_sku_grp_targets), 0);


        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleKpiCode = bundleExtras.getString(Constants.KPICode);
            mStrBundleKpiName = bundleExtras.getString(Constants.KPIName);
            mStrBundleKpiGUID = bundleExtras.getString(Constants.KPIGUID);
            mStrBundleCalBased = bundleExtras.getString(Constants.CalculationBase);
            mStrBundleKpiFor = bundleExtras.getString(Constants.KPIFor);
            mStrBundleRollup = bundleExtras.getString(Constants.RollUpTo);
            mStrBundleCalSource= bundleExtras.getString(Constants.CalculationSource);
            mStrParnerGuid = bundleExtras.getString(Constants.PartnerMgrGUID);
        }
        initUI();
    }

    /*Initializes UI*/
    void initUI(){
        getMyTargetsList();
    }


    /*Get targets for sales person and kpi code based on query*/
    private void getMyTargetsList() {
        try {

            String qryTargets = Constants.Targets+ "?$filter=" +Constants.KPIGUID+ " eq guid'"
                    + mStrBundleKpiGUID+"'" ;
            ArrayList<MyTargetsBean> alMyTargets = OfflineManager.getMyTargetsList(qryTargets, mStrParnerGuid,
                    mStrBundleKpiName, mStrBundleKpiCode, mStrBundleKpiGUID,
                    mStrBundleCalBased, mStrBundleKpiFor,
                    mStrBundleCalSource, mStrBundleRollup,"","");

            mapMyTargetVal = getALMyTargetList(alMyTargets);
            displayMyTargetsValues();
        } catch (OfflineODataStoreException e) {
            displayMyTargetsValues();
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
    }

    //ToDo sum of actual and target quantity/Value based on crs sku group and assign to map table
    private Map<String,MyTargetsBean> getALMyTargetList(ArrayList<MyTargetsBean> alMyTargets){
        Map<String,MyTargetsBean> mapMyTargetBean=new HashMap<>();
        if(alMyTargets!=null && alMyTargets.size()>0){

            for(MyTargetsBean bean:alMyTargets)
                if(mapMonthTarget.containsKey(bean.getOrderMaterialGroupID())) {
                    double mDoubMonthTarget = Double.parseDouble(bean.getMonthTarget()) +  mapMonthTarget.get(bean.getOrderMaterialGroupID());
                    double mDoubMonthAchived = Double.parseDouble(bean.getMTDA()) +  mapMonthAchived.get(bean.getOrderMaterialGroupID());

                    mapMonthTarget.put(bean.getOrderMaterialGroupID(), mDoubMonthTarget);
                    mapMonthAchived.put(bean.getOrderMaterialGroupID(), mDoubMonthAchived);
                    mapMyTargetBean.put(bean.getOrderMaterialGroupID(),bean);
                }else {
                    double mDoubMonthTarget = Double.parseDouble(bean.getMonthTarget()) ;
                    double mDoubMonthAchived = Double.parseDouble(bean.getMTDA()) ;
                    double mDoubAchivedPer = Double.parseDouble(bean.getAchivedPercentage());
                    double mDoubBTD = Double.parseDouble(bean.getBTD());

                    mapMonthTarget.put(bean.getOrderMaterialGroupID(), mDoubMonthTarget);
                    mapMonthAchived.put(bean.getOrderMaterialGroupID(), mDoubMonthAchived);
                    mapMyTargetBean.put(bean.getOrderMaterialGroupID(),bean);
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

            Iterator iter = mapMyTargetVal.keySet().iterator();
            while(iter.hasNext()){
                llMyTargets = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.my_targets_list_item,
                                null, false);

                String key = iter.next().toString();
                MyTargetsBean myTargetsBean = mapMyTargetVal.get(key);

                ((TextView) llMyTargets.findViewById(R.id.tv_kpi_value))
                        .setText(myTargetsBean.getOrderMaterialGroupDesc());

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


}

