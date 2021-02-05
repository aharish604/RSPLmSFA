package com.rspl.sf.msfa.soDetails;/*
package com.arteriatech.sf.mdc.soDetails;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.sf.mdc.R;
import com.arteriatech.sf.mdc.constant.Constants;
import com.arteriatech.sf.mdc.soCreate.SOConditionItemDetaiBean;
import com.arteriatech.sf.mdc.soCreate.SOItemBean;
import com.arteriatech.sf.mdc.soCreate.SOSubItemBean;

import java.util.ArrayList;

*/
/**
 * A simple {@link Fragment} subclass.
 *//*

public class SOItemDetailsFragment extends Fragment implements AdapterInterface<SOItemBean> {

    private ArrayList<SOItemBean> itemList=null;
    private RecyclerView recyclerView;
    private SimpleRecyclerViewAdapter<SOItemBean> soItemAdapter;
    private TextView tvNoRecordFound;

    public SOItemDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle!=null){
            itemList = (ArrayList<SOItemBean>)bundle.getSerializable(Constants.EXTRA_SO_ITEM_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_soitem_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        tvNoRecordFound = (TextView) view.findViewById(R.id.no_record_found);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        soItemAdapter = new SimpleRecyclerViewAdapter<SOItemBean>(getContext(), R.layout.so_item_header, this, recyclerView, tvNoRecordFound);
        recyclerView.setAdapter(soItemAdapter);
        if (itemList!=null){
            soItemAdapter.refreshAdapter(itemList);
        }
    }

    @Override
    public void onItemClick(SOItemBean soItemBean, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new SOItemDetailsVH(view, getContext());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i, SOItemBean soItemBean) {
 */
/*so item start *//*

        ((SOItemDetailsVH) holder).tvItemNo.setText("Item #" + soItemBean.getItemNo());
        ((SOItemDetailsVH) holder).tvMaterial.setText(soItemBean.getMatCode());
        ((SOItemDetailsVH) holder).tvDescription.setText(soItemBean.getMatDesc());
        ((SOItemDetailsVH) holder).tvQuantity.setText(soItemBean.getSoQty() + " " + soItemBean.getUom());
        ((SOItemDetailsVH) holder).tvUnitPrice.setText(UtilConstants.commaSeparator(UtilConstants.removeLeadingZero(soItemBean.getUnitPrice())) + " " + soItemBean.getCurrency());
        */
/*so item end*//*

        */
/*condition start*//*

        int conditionTotalSize = soItemBean.getConditionItemDetaiBeanArrayList().size();
        if (conditionTotalSize > 0) {
            try {
                ((SOItemDetailsVH) holder).llSOCondition.removeAllViews();
            } catch (Exception e) {
                e.printStackTrace();
            }
            TextView[] tvDescription = new TextView[conditionTotalSize];
            TextView[] tvPercentage = new TextView[conditionTotalSize];
            TextView[] tvAmount = new TextView[conditionTotalSize];
            TextView[] tvTotalAmount = new TextView[conditionTotalSize];
            ((SOItemDetailsVH) holder).llSOCondition.setVisibility(View.VISIBLE);
            TableLayout tableScheduleHeading = (TableLayout) LayoutInflater.from(getContext()).inflate(R.layout.table_view, null);
            for (int j = 0; j < conditionTotalSize; j++) {
                SOConditionItemDetaiBean soScheduleBean = soItemBean.getConditionItemDetaiBeanArrayList().get(j);
                if (!soScheduleBean.getViewType().equalsIgnoreCase("T")) {
                    LinearLayout rowScheduleItem = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.so_condition_item, null);
                    tvDescription[j] = (TextView) rowScheduleItem.findViewById(R.id.tv_description);
                    tvPercentage[j] = (TextView) rowScheduleItem.findViewById(R.id.tv_percentage);
                    tvAmount[j] = (TextView) rowScheduleItem.findViewById(R.id.tv_amount);
                    tvTotalAmount[j] = (TextView) rowScheduleItem.findViewById(R.id.tv_total_anount);
                    tvDescription[j].setText(soScheduleBean.getName());
                    tvAmount[j].setText(UtilConstants.commaSeparator(soScheduleBean.getAmount()));
                    if (j == 0) {
                        ((SOItemDetailsVH) holder).tvCurrencyType.setText("(" + soItemBean.getCurrency() + ")");
                        ((SOItemDetailsVH) holder).tvTotalCurrencyType.setText("(" + soItemBean.getCurrency() + ")");
                    }
                    tvTotalAmount[j].setText(UtilConstants.commaSeparator(soScheduleBean.getConditionValue()));
                    tableScheduleHeading.addView(rowScheduleItem);
                } else {
                    LinearLayout rowScheduleItem = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.so_condition_total_item, null);
                    tvDescription[j] = (TextView) rowScheduleItem.findViewById(R.id.tv_description);
                    tvAmount[j] = (TextView) rowScheduleItem.findViewById(R.id.tv_amount);
                    tvTotalAmount[j] = (TextView) rowScheduleItem.findViewById(R.id.tv_total_anount);
                    tvDescription[j].setText(soScheduleBean.getName());
                    if (soItemBean.getCurrency().equalsIgnoreCase("INR")) {
                        tvAmount[j].setText(UtilConstants.commaSeparator(soScheduleBean.getAmount()));
                    } else {
                        tvAmount[j].setText(UtilConstants.commaSeparator(soScheduleBean.getAmount()));
                    }
                    tvTotalAmount[j].setText(UtilConstants.commaSeparator(soScheduleBean.getConditionValue()));
                    if (j == 0) {
                        ((SOItemDetailsVH) holder).tvCurrencyType.setText("(" + soItemBean.getCurrency() + ")");
                        ((SOItemDetailsVH) holder).tvTotalCurrencyType.setText("(" + soItemBean.getCurrency() + ")");
                    }
                    tableScheduleHeading.addView(rowScheduleItem);
                }
            }
            ((SOItemDetailsVH) holder).llSOCondition.addView(tableScheduleHeading);
        } else {
            ((SOItemDetailsVH) holder).llSOCondition.setVisibility(View.GONE);
        }
            */
/*finished condition *//*


            */
/*schedule display started*//*

        int scheduleTotalSize = 0;
        try {
            scheduleTotalSize = soItemBean.getSoSubItemBeen().size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (scheduleTotalSize > 0) {
            try {
                ((SOItemDetailsVH) holder).llSOSchedule.removeAllViews();
            } catch (Exception e) {
                e.printStackTrace();
            }
            */
/*if (activityFrom == 1 || activityFrom == 4) {
                Collections.sort(soItemBean.getSoSubItemBeen(), new Comparator<SOSubItemBean>() {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    @Override
                    public int compare(SOSubItemBean r1, SOSubItemBean r2) {

                        try {
                            return dateFormat.parse(r1.getDateForStore()).compareTo(dateFormat.parse(r2.getDateForStore()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });
            }*//*


            TextView[] tvItemDate = new TextView[scheduleTotalSize];
            TextView[] tvItemQty = new TextView[scheduleTotalSize];
            ((SOItemDetailsVH) holder).llSOSchedule.setVisibility(View.VISIBLE);
            TableLayout tableScheduleHeading = (TableLayout) LayoutInflater.from(getContext()).inflate(R.layout.table_view, null);
            for (int j = 0; j < scheduleTotalSize; j++) {
                SOSubItemBean soScheduleBean = soItemBean.getSoSubItemBeen().get(j);
                LinearLayout rowScheduleItem = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.so_schedule_item, null);
                tvItemDate[j] = (TextView) rowScheduleItem.findViewById(R.id.tv_date);
                tvItemQty[j] = (TextView) rowScheduleItem.findViewById(R.id.tv_qty);
                tvItemDate[j].setText(soScheduleBean.getDate());
                tvItemQty[j].setText(soScheduleBean.getSubQty() + " " + soItemBean.getUom());
                tableScheduleHeading.addView(rowScheduleItem);
            }
            ((SOItemDetailsVH) holder).llSOSchedule.addView(tableScheduleHeading);
        } else {
            ((SOItemDetailsVH) holder).llSOSchedule.setVisibility(View.GONE);
        }
        //finished schedule display
    }
}
*/
