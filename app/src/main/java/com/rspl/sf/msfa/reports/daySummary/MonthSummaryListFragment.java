package com.rspl.sf.msfa.reports.daySummary;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterViewInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewTypeAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MonthSummaryListFragment extends Fragment implements AdapterViewInterface<DashBoardBean> {


    private RecyclerView recyclerViewMonth;
    private ArrayList<DashBoardBean> mapDasgBoardVal = new ArrayList<>();
    private SimpleRecyclerViewTypeAdapter<DashBoardBean> recyclerViewAdapter;
    private TextView no_record_found;

    public MonthSummaryListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_month_summary_list, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        recyclerViewMonth = view.findViewById(R.id.recycler_view_month);
        no_record_found = (TextView) view.findViewById(R.id.no_record_found);
        recyclerViewMonth.setHasFixedSize(true);
        StaggeredGridLayoutManager _sGridLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerViewMonth.setLayoutManager(_sGridLayoutManager);
        recyclerViewAdapter = new SimpleRecyclerViewTypeAdapter<DashBoardBean>(getActivity(), R.layout.dash_broad_list, this, recyclerViewMonth, no_record_found);
        recyclerViewMonth.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.refreshAdapter(mapDasgBoardVal);
    }

    @Override
    public void onItemClick(DashBoardBean dashBoardBean, View view, int i) {

    }

    @Override
    public int getItemViewType(int i, ArrayList<DashBoardBean> arrayList) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new DashBoardHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, DashBoardBean dashBoardBean, ArrayList<DashBoardBean> arrayList) {
        try {
            ((DashBoardHolder) viewHolder).tv_ach_dashboard_val_tittle.setText(dashBoardBean.getApplication());
//                ((DashBoardHolder)viewHolder).tv_tar_dashbord_val.setText(dashBoardBean.getTotal());

          //  if (dashBoardBean.getApplication().equalsIgnoreCase("RTGS Value ( S & D )")) {
                if (dashBoardBean.getApplication().equalsIgnoreCase("RTGS Value ( Total Balance )")) {
                    ((DashBoardHolder) viewHolder).tv_dashboard_val.setText("Planned");
                ((DashBoardHolder) viewHolder).pbSalesPerActucal.setVisibility(View.VISIBLE);

                try {
                   // ((DashBoardHolder) viewHolder).tv_actual_dashbord_val.setText(Constants.convertCurrencyInWords(Double.parseDouble(dashBoardBean.getActive())));
                    ((DashBoardHolder) viewHolder).tv_actual_dashbord_val.setText(Constants.convertCurrencyInWords(Double.parseDouble(dashBoardBean.getActive())));

                    ((DashBoardHolder) viewHolder).tv_tar_dashbord_val.setText(Constants.convertCurrencyInWords(Double.parseDouble(dashBoardBean.getTotal())));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else if (dashBoardBean.getApplication().equalsIgnoreCase("RTGS Value ( Focus Brand )")) {
                ((DashBoardHolder) viewHolder).tv_dashboard_val.setText("Planned");
                ((DashBoardHolder) viewHolder).pbSalesPerActucal.setVisibility(View.VISIBLE);
                try {
                    ((DashBoardHolder) viewHolder).tv_actual_dashbord_val.setText(Constants.convertCurrencyInWords(Double.parseDouble(dashBoardBean.getActive())));
                    ((DashBoardHolder) viewHolder).tv_tar_dashbord_val.setText(Constants.convertCurrencyInWords(Double.parseDouble(dashBoardBean.getTotal())));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else if (dashBoardBean.getApplication().equalsIgnoreCase("Team Count")) {
                ((DashBoardHolder) viewHolder).tv_dashboard_val.setText("Sales Team");
                ((DashBoardHolder) viewHolder).tv_actual_dashbord_val.setText(dashBoardBean.getActive());
                ((DashBoardHolder) viewHolder).tv_tar_dashbord_val.setText(dashBoardBean.getTotal());
                ((DashBoardHolder) viewHolder).pbSalesPerActucal.setVisibility(View.VISIBLE);

            } else if (dashBoardBean.getApplication().equalsIgnoreCase("Retailer Outstanding")) {
                ((DashBoardHolder) viewHolder).tv_dashboard_val.setText("Pending");
                ((DashBoardHolder) viewHolder).tv_actual_dashbord_val.setText(dashBoardBean.getActive());
                ((DashBoardHolder) viewHolder).tv_tar_dashbord_val.setText(dashBoardBean.getTotal());
                ((DashBoardHolder) viewHolder).pbSalesPerActucal.setVisibility(View.GONE);
            } else if (dashBoardBean.getApplication().equalsIgnoreCase("Retailers Stock Value")) {
                ((DashBoardHolder) viewHolder).tv_dashboard_val.setText("Stock Value");
                ((DashBoardHolder) viewHolder).tv_actual_dashbord_val.setText(dashBoardBean.getActive());
                ((DashBoardHolder) viewHolder).tv_tar_dashbord_val.setText(dashBoardBean.getTotal());
                ((DashBoardHolder) viewHolder).pbSalesPerActucal.setVisibility(View.GONE);
            } else {
                ((DashBoardHolder) viewHolder).tv_dashboard_val.setText("Total");
                ((DashBoardHolder) viewHolder).tv_actual_dashbord_val.setText(dashBoardBean.getActive());
                ((DashBoardHolder) viewHolder).tv_tar_dashbord_val.setText(dashBoardBean.getTotal());
                ((DashBoardHolder) viewHolder).pbSalesPerActucal.setVisibility(View.VISIBLE);

            }
            if (dashBoardBean.isShowProgress()) {
                ((DashBoardHolder) viewHolder).pbCount.setVisibility(View.VISIBLE);
                ((DashBoardHolder) viewHolder).tv_actual_dashbord_val.setVisibility(View.GONE);
            } else {
                ((DashBoardHolder) viewHolder).pbCount.setVisibility(View.GONE);
                ((DashBoardHolder) viewHolder).tv_actual_dashbord_val.setVisibility(View.VISIBLE);
            }
            try {
                if (!TextUtils.isEmpty(dashBoardBean.getActive()) && !TextUtils.isEmpty(dashBoardBean.getTotal())) {
                    if (!Double.isNaN(Double.parseDouble(dashBoardBean.getActive())) && !Double.isNaN(Double.parseDouble(dashBoardBean.getTotal()))) {
                        double percentageActual = Double.parseDouble(dashBoardBean.getActive()) / Double.parseDouble(dashBoardBean.getTotal()) * 100;
                        try {
                            if (!Double.isNaN(percentageActual)) {
                                ((DashBoardHolder) viewHolder).pbSalesPerActucal.setProgress(Integer.parseInt(UtilConstants.trimQtyDecimalPlace(String.valueOf(percentageActual))));
                                ((DashBoardHolder) viewHolder).pbSalesPerActucal.setText(UtilConstants.trimQtyDecimalPlace(String.valueOf(percentageActual) + "%"));
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    ((DashBoardHolder) viewHolder).pbSalesPerActucal.setProgress(0);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.writeLogError("Month Summary List onBindViewHolder" + e.getMessage());
        }

    }

    public void showProgress() {
        for (DashBoardBean showProgress : mapDasgBoardVal) {
            showProgress.setShowProgress(true);
        }
        if (recyclerViewAdapter != null)
            recyclerViewAdapter.refreshAdapter(mapDasgBoardVal);
    }

    public void onRefresh(ArrayList<DashBoardBean> mapDasgBoardList) {
        if (mapDasgBoardList != null) {
            mapDasgBoardVal.clear();
            mapDasgBoardVal.addAll(mapDasgBoardList);
        }
        if (recyclerViewAdapter != null)
            recyclerViewAdapter.refreshAdapter(mapDasgBoardVal);
    }
}
