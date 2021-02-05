package com.rspl.sf.msfa.reports.daySummary;


import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterViewInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewTypeAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.MyTargetsBean;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DaySummaryListFragment extends Fragment implements AdapterViewInterface<MyTargetsBean> {

    private SimpleRecyclerViewTypeAdapter<MyTargetsBean> recyclerViewAdapter;
    private RecyclerView recyclerViewDay;
    private ArrayList<MyTargetsBean> mapTargetVal = new ArrayList<>();
    private TextView no_record_found;

    public DaySummaryListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_day_summary_list, container, false);
       /* if(getArguments() != null){
            try {
                mapTargetVal = (ArrayList<MyTargetsBean>) getArguments().getSerializable(Constants.DayDashBoardList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        init(view);
        return view;
    }

    private void init(View view) {
        recyclerViewDay = view.findViewById(R.id.recycler_view_day);
        recyclerViewDay.setHasFixedSize(true);
        StaggeredGridLayoutManager _sGridLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerViewDay.setLayoutManager(_sGridLayoutManager);
        recyclerViewAdapter = new SimpleRecyclerViewTypeAdapter<MyTargetsBean>(getActivity(), R.layout.recycler_targets_list_item, this, recyclerViewDay, no_record_found);
        recyclerViewDay.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.refreshAdapter(mapTargetVal);
       /* if(mapTargetVal != null && mapTargetVal.size()>0){

        }*/

    }

    public void showProgress() {
        for (MyTargetsBean showProgress : mapTargetVal) {
            showProgress.setShowProgress(true);
        }
        if (recyclerViewAdapter != null)
            recyclerViewAdapter.refreshAdapter(mapTargetVal);
    }
    public void onRefresh(ArrayList<MyTargetsBean> mapTargetList) {
        if (mapTargetList != null) {
            mapTargetVal.clear();
            mapTargetVal.addAll(mapTargetList);
        }
        if (recyclerViewAdapter != null)
            recyclerViewAdapter.refreshAdapter(mapTargetVal);
    }

    private void onNavigateMTP() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        if (sharedPreferences.getString(Constants.isMTPEnabled, "").equalsIgnoreCase(Constants.isMTPTcode)) {
            if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
                onMTP();
            } else {
                ConstantsUtils.showAutoDateSetDialog(getActivity());
            }
        }
    }

    private void onMTP() {
        ConstantsUtils.onMTPActivity(getActivity());
    }

    @Override
    public void onItemClick(MyTargetsBean myTargetsBean, View view, int i) {
        if (myTargetsBean.getKPIName().contains("Visits")) {
            onNavigateMTP();
        }
    }

    @Override
    public int getItemViewType(int i, ArrayList<MyTargetsBean> arrayList) {
        try {
            if (arrayList.get(i).getKPIName().contains("Visits") || arrayList.get(i).getKPIName().contains("Order Value"))
                return 0;
            else
                return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        View viewItem = null;
        if (i == 0) {
            viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dashboard_visit_view_line_item, viewGroup, false);
            return new VisitTargetHolder(viewItem);
        } else if (i == 1) {
            viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dashboard_sales_view_line_item, viewGroup, false);
            return new SalesTargetViewHolder(viewItem);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, MyTargetsBean myTargetsBean, ArrayList<MyTargetsBean> arrayList) {
        if (viewHolder instanceof VisitTargetHolder) {
            if (myTargetsBean.getKPIName().contains("Visits")) {
                if (Constants.getRollID(getActivity())) {
                    ((VisitTargetHolder) viewHolder).cv_visit.setVisibility(View.GONE);
                } else {
                    ((VisitTargetHolder) viewHolder).cv_visit.setVisibility(View.VISIBLE);
                    ((VisitTargetHolder) viewHolder).tv_no_of_outlets.setText(myTargetsBean.getMTDA() + "/" + myTargetsBean.getMonthTarget());
                }
//                ((VisitTargetHolder) viewHolder).tv_no_of_outlets.setText(myTargetsBean.getMTDA() + "/" + myTargetsBean.getMonthTarget());
//                ((VisitTargetHolder)viewHolder).cv_visit.setVisibility(View.GONE);
            } else {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
                String rollType = sharedPreferences.getString(Constants.USERROLE, "");
                if (rollType.equalsIgnoreCase("Z5")) {
                    String totalOrderValue = sharedPreferences.getString(Constants.Total_Order_Value_KEY, "");
                    ((VisitTargetHolder) viewHolder).tv_no_of_outlets.setText(UtilConstants.removeLeadingZerowithTwoDecimal(totalOrderValue));
                } else {
                    ((VisitTargetHolder) viewHolder).tv_no_of_outlets.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myTargetsBean.getBTD()));
                }
//                ((VisitTargetHolder) viewHolder).tv_no_of_outlets.setText("99,999,999.00");
                ((VisitTargetHolder) viewHolder).tv_order_val.setText(getActivity().getText(R.string.lbl_today_order_val));

            }
            if (myTargetsBean.isShowProgress()){
                ((VisitTargetHolder) viewHolder).pbCount.setVisibility(View.VISIBLE);
                ((VisitTargetHolder) viewHolder).tv_no_of_outlets.setVisibility(View.GONE);
            }else {
                ((VisitTargetHolder) viewHolder).pbCount.setVisibility(View.GONE);
                ((VisitTargetHolder) viewHolder).tv_no_of_outlets.setVisibility(View.VISIBLE);
            }

        } else if (viewHolder instanceof SalesTargetViewHolder) {
            if (myTargetsBean.getCalculationBase().equalsIgnoreCase(Constants.str_02)) {
                ((SalesTargetViewHolder) viewHolder).tv_sales_value_lbl.setText(myTargetsBean.getKPIName());
                ((SalesTargetViewHolder) viewHolder).tv_target_val.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myTargetsBean.getMonthTarget()));
                ((SalesTargetViewHolder) viewHolder).tv_ach_sal_val.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myTargetsBean.getMTDA()));
            } else {
                String mStrKPIName = "";
                if (myTargetsBean.getUOM().equalsIgnoreCase("TO")) {
                    mStrKPIName = myTargetsBean.getKPIName() + " (TON)";
                } else {
                    if (myTargetsBean.getUOM().equalsIgnoreCase("")) {
                        mStrKPIName = myTargetsBean.getKPIName() + " " + myTargetsBean.getUOM() + "";
                    } else {
                        mStrKPIName = myTargetsBean.getKPIName() + " (" + myTargetsBean.getUOM() + ")";
                    }

                }

//                String mStrKPIName = myTargetsBean.getKPIName()+" "+myTargetsBean.getUOM()+"";
//                if(mStrKPIName.contains("Monthly Target")){
//                    ((SalesTargetViewHolder) viewHolder).tv_sales_value_lbl.setText(mStrKPIName.replace("Monthly Target",""));
//                }else{
                ((SalesTargetViewHolder) viewHolder).tv_sales_value_lbl.setText(mStrKPIName);
//                }

                ((SalesTargetViewHolder) viewHolder).tv_target_val.setText(myTargetsBean.getMonthTarget());
                ((SalesTargetViewHolder) viewHolder).tv_ach_sal_val.setText(myTargetsBean.getMTDA());
            }

            ((SalesTargetViewHolder) viewHolder).pbSalesPer.setProgress(Integer.parseInt(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage())));
            ((SalesTargetViewHolder) viewHolder).pbSalesPer.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage()) + "%");
            if (myTargetsBean.isShowProgress()){
                ((SalesTargetViewHolder) viewHolder).pbCount.setVisibility(View.VISIBLE);
                ((SalesTargetViewHolder) viewHolder).tv_ach_sal_val.setVisibility(View.GONE);
            }else {
                ((SalesTargetViewHolder) viewHolder).pbCount.setVisibility(View.GONE);
                ((SalesTargetViewHolder) viewHolder).tv_ach_sal_val.setVisibility(View.VISIBLE);
            }
        }
    }
}
