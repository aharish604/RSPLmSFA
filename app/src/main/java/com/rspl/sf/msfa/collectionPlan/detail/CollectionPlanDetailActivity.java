package com.rspl.sf.msfa.collectionPlan.detail;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.collectionPlan.WeekDetailsList;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by e10860 on 2/19/2018.
 */

public class CollectionPlanDetailActivity extends AppCompatActivity implements AdapterInterface<WeekDetailsList> {

    SimpleRecyclerViewAdapter<WeekDetailsList> simpleRecyclerViewAdapter;
    ArrayList<WeekDetailsList> weekDetailsListal = new ArrayList<>();
    private RecyclerView rvCollectionPlan;
    private Activity activity;
    private ArrayList<WeekDetailsList> weekDetailsLists = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collectionplan_detail);
        if (getIntent() != null) {
            weekDetailsLists = (ArrayList<WeekDetailsList>) getIntent().getSerializableExtra(Constants.EXTRA_COLLECTION_DETAIL);
        }
        init();
    }

    private void init() {
        activity = CollectionPlanDetailActivity.this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.collection_detail), 0);
        rvCollectionPlan = (RecyclerView) findViewById(R.id.rvCollectionPlan);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        rvCollectionPlan.setHasFixedSize(true);
        rvCollectionPlan.setLayoutManager(linearLayoutManager);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<>(activity, R.layout.item_collection_plan_detail, this, rvCollectionPlan, null);
        rvCollectionPlan.setAdapter(simpleRecyclerViewAdapter);
        weekDetailsListal = weekDetailsLists();
        if (weekDetailsListal != null && !weekDetailsListal.isEmpty())
            simpleRecyclerViewAdapter.refreshAdapter(weekDetailsListal);
    }

    private ArrayList<WeekDetailsList> weekDetailsLists() {
        ArrayList<WeekDetailsList> alWeakList = new ArrayList<>();
        try {
            if (weekDetailsLists != null && !weekDetailsLists.isEmpty()) {
                Collections.sort(weekDetailsLists, new Comparator<WeekDetailsList>() {
                    public int compare(WeekDetailsList one, WeekDetailsList other) {
                        return one.getcPNo().compareTo(other.getcPNo());
                    }
                });
                ArrayList<String> stringsCustomer = new ArrayList<>();
                ArrayList<String> stringsSaleArea = new ArrayList<>();
                boolean isAdd=false;
                for (int i = 0; i < weekDetailsLists.size(); i++) {
                    WeekDetailsList mergeSameCpno = new WeekDetailsList();
                    isAdd = false;
                    for (int j = 0; j < weekDetailsLists.size(); j++) {
                        if (weekDetailsLists.get(i).getcPNo().equalsIgnoreCase(weekDetailsLists.get(j).getcPNo())) {
                            if (!stringsCustomer.contains(weekDetailsLists.get(i).getcPNo())) {
                                isAdd = true;
                                if (weekDetailsLists.get(j).getCrdtCtrlArea().equalsIgnoreCase("1010")) {
                                    if(TextUtils.isEmpty(mergeSameCpno.getcPNo())) {
                                        mergeSameCpno = weekDetailsLists.get(j);
                                        mergeSameCpno.setCrdtCtrlArea1(weekDetailsLists.get(j).getCrdtCtrlArea());
                                        mergeSameCpno.setAchievedValue(weekDetailsLists.get(j).getAchievedValue());
                                    }else{
                                        mergeSameCpno.setCrdtCtrlArea1(weekDetailsLists.get(j).getCrdtCtrlArea());
                                        mergeSameCpno.setCurrency(weekDetailsLists.get(j).getCurrency());
                                        mergeSameCpno.setCPName(weekDetailsLists.get(j).getCPName());
                                        mergeSameCpno.setRemarks(weekDetailsLists.get(j).getRemarks());
                                        mergeSameCpno.setPlannedValue(weekDetailsLists.get(j).getPlannedValue());
                                        mergeSameCpno.setAchievedValue(weekDetailsLists.get(j).getAchievedValue());
                                    }
                                    //  alWeakList.add(weekDetailsLists.get(i));
                                } else if (weekDetailsLists.get(j).getCrdtCtrlArea().equalsIgnoreCase("1030")) {
                                   // mergeSameCpno = weekDetailsLists.get(j);
                                    if(TextUtils.isEmpty(mergeSameCpno.getcPNo())) {
                                        mergeSameCpno = weekDetailsLists.get(j);
                                        mergeSameCpno.setCrdtCtrlArea2(weekDetailsLists.get(j).getCrdtCtrlArea());
                                        mergeSameCpno.setAchievedValue1(weekDetailsLists.get(j).getAchievedValue());
                                    }else{
                                        mergeSameCpno.setCrdtCtrlArea2(weekDetailsLists.get(j).getCrdtCtrlArea());
                                        mergeSameCpno.setCurrency(weekDetailsLists.get(j).getCurrency());
                                        mergeSameCpno.setCPName(weekDetailsLists.get(j).getCPName());
                                        mergeSameCpno.setRemarks2(weekDetailsLists.get(j).getRemarks2());
                                        mergeSameCpno.setPlannedValue2(weekDetailsLists.get(j).getPlannedValue2());
                                        mergeSameCpno.setAchievedValue1(weekDetailsLists.get(j).getAchievedValue());
                                    }

                                }else{
                                    mergeSameCpno = weekDetailsLists.get(j);
                                    mergeSameCpno.setCrdtCtrlArea1(weekDetailsLists.get(j).getCrdtCtrlArea());
                                }
                            }
                        }

                    }
                     if (isAdd) {
                        alWeakList.add(mergeSameCpno);
                        stringsCustomer.add(weekDetailsLists.get(i).getcPNo());
                    }
                }

                   /* value = value+i;
                    WeekDetailsList weekDetailsBean = weekDetailsLists.get(i);
                    String saleArea = weekDetailsBean.getCrdtCtrlArea();
                    if(saleArea.equalsIgnoreCase("1010")){
                        weekDetailsBean.setCrdtCtrlArea1(saleArea);
                    }else if(saleArea.equalsIgnoreCase("1030")){
                        weekDetailsBean.setCrdtCtrlArea2(saleArea);
                    }
                    if(!stringsCustomer.contains(weekDetailsBean.getcPNo())){

                        alWeakList.add(weekDetailsBean);
                        stringsCustomer.add(weekDetailsBean.getcPNo());
                    }else {
                        WeekDetailsList weekDetailsBean1 = alWeakList.get(value-1);
                        weekDetailsBean1.setRemarks2(weekDetailsBean.getRemarks2());
                        if(weekDetailsBean1.getCrdtCtrlArea1().equalsIgnoreCase("")){
                            weekDetailsBean1.setCrdtCtrlArea1(saleArea);
                        }
                        if(weekDetailsBean1.getCrdtCtrlArea2().equalsIgnoreCase("")){
                            weekDetailsBean1.setCrdtCtrlArea2(saleArea);
                        }
                        if(weekDetailsBean1.getPlannedValue2().equalsIgnoreCase("0")) {
                            weekDetailsBean1.setPlannedValue2(weekDetailsBean.getPlannedValue2());
                        }
                        if(weekDetailsBean1.getPlannedValue().equalsIgnoreCase("0")) {
                            weekDetailsBean1.setPlannedValue(weekDetailsBean.getPlannedValue());
                        }
                        alWeakList.set(value-1,weekDetailsBean1);
                    }*/

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alWeakList;
    }

    @Override
    public void onItemClick(WeekDetailsList weekHeaderList, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new CollectionPlanVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, WeekDetailsList weekHeaderList) {
//        ((CollectionPlanVH) viewHolder).tvPlanActivityDesc.setText(ConstantsUtils.commaSeparator(weekHeaderList.getPlannedValue(),weekHeaderList.getCurrency()!=null?weekHeaderList.getCurrency():"")+" "+weekHeaderList.getCurrency()!=null?weekHeaderList.getCurrency():"");
//        ((CollectionPlanVH) viewHolder).tvPlanActivityDesc.setText(ConstantsUtils.commaSeparator(weekHeaderList.getPlannedValue(),weekHeaderList.getCurrency())+" "+weekHeaderList.getCurrency());
        String creditID = weekHeaderList.getCrdtCtrlArea();
        /*try {
            if(!TextUtils.isEmpty(creditID)) {
                if (creditID.equalsIgnoreCase("1010")) {
                    ((CollectionPlanVH) viewHolder).tvPlanActivityDesc.setText(ConstantsUtils.commaSeparator(weekHeaderList.getPlannedValue(),weekHeaderList.getCurrency())+" "+weekHeaderList.getCurrency());
                    ((CollectionPlanVH) viewHolder).tvActivity.setText("Activity(RSPL S & D Credit)");
                } else if (creditID.equalsIgnoreCase("1030")) {
                    ((CollectionPlanVH) viewHolder).tvPlanActivityDesc.setText(ConstantsUtils.commaSeparator(weekHeaderList.getPlannedValue2(),weekHeaderList.getCurrency())+" "+weekHeaderList.getCurrency());
                    ((CollectionPlanVH) viewHolder).tvActivity.setText("Activity(RSPL Focus Brands Credit)");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        if (!TextUtils.isEmpty(weekHeaderList.getCrdtCtrlArea1())) {
            ((CollectionPlanVH) viewHolder).tvPlanActivityDesc.setText(ConstantsUtils.commaSeparator(weekHeaderList.getPlannedValue(), weekHeaderList.getCurrency()) + " " + weekHeaderList.getCurrency());
           // ((CollectionPlanVH) viewHolder).tvActivity.setText("Amount (RSPL S & D Credit)");
            ((CollectionPlanVH) viewHolder).tvActivity.setText("Amount");
            ((CollectionPlanVH) viewHolder).llActivity.setVisibility(View.VISIBLE);
            ((CollectionPlanVH) viewHolder).llRemarks.setVisibility(View.VISIBLE);
        } else {
            ((CollectionPlanVH) viewHolder).llActivity.setVisibility(View.GONE);
            ((CollectionPlanVH) viewHolder).llRemarks.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(weekHeaderList.getCrdtCtrlArea2())) {
            ((CollectionPlanVH) viewHolder).tvPlanActivityDesc1.setText(ConstantsUtils.commaSeparator(weekHeaderList.getPlannedValue2(), weekHeaderList.getCurrency()) + " " + weekHeaderList.getCurrency());
           // ((CollectionPlanVH) viewHolder).tvActivity1.setText("Amount (RSPL Focus Brands Credit)");
            ((CollectionPlanVH) viewHolder).tvActivity1.setText("Amount");
            ((CollectionPlanVH) viewHolder).llActivity1.setVisibility(View.VISIBLE);
            ((CollectionPlanVH) viewHolder).llRemarks1.setVisibility(View.VISIBLE);
        } else {
            ((CollectionPlanVH) viewHolder).llActivity1.setVisibility(View.GONE);
            ((CollectionPlanVH) viewHolder).llRemarks1.setVisibility(View.GONE);
        }
        try {
            ((CollectionPlanVH) viewHolder).tvActualActivityDesc.setText(ConstantsUtils.commaSeparator(weekHeaderList.getAchievedValue(), weekHeaderList.getCurrency()) + " " + weekHeaderList.getCurrency());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ((CollectionPlanVH) viewHolder).tvActualActivityDesc1.setText(ConstantsUtils.commaSeparator(weekHeaderList.getAchievedValue1(), weekHeaderList.getCurrency()) + " " + weekHeaderList.getCurrency());
        } catch (Exception e) {
            e.printStackTrace();
        }

        double mDouAchValue = 0.0;
        try {
            mDouAchValue = Double.parseDouble(weekHeaderList.getAchievedValue());
        } catch (NumberFormatException e) {
            mDouAchValue = 0.00;
            e.printStackTrace();
        }

        double mDouAchValue1 = 0.0;
        try {
            mDouAchValue1 = Double.parseDouble(weekHeaderList.getAchievedValue1());
        } catch (NumberFormatException e) {
            mDouAchValue1 = 0.00;
            e.printStackTrace();
        }


        if (mDouAchValue > 0) {
            ((CollectionPlanVH) viewHolder).llActivityActual.setVisibility(View.VISIBLE);
        } else {
            ((CollectionPlanVH) viewHolder).llActivityActual.setVisibility(View.GONE);
        }
        if (mDouAchValue1 > 0) {
            ((CollectionPlanVH) viewHolder).llActivityActual1.setVisibility(View.VISIBLE);
        } else {
            ((CollectionPlanVH) viewHolder).llActivityActual1.setVisibility(View.GONE);
        }

        ((CollectionPlanVH) viewHolder).tvPlanNameDesc.setText(weekHeaderList.getcPName());
        ((CollectionPlanVH) viewHolder).tvPlanRemarksDesc.setText(weekHeaderList.getRemarks());
        ((CollectionPlanVH) viewHolder).tvPlanRemarksDesc1.setText(weekHeaderList.getRemarks2());
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
