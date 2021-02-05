package com.rspl.sf.msfa.collectionPlan;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterViewInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewTypeAdapter;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.ConstantsUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by e10860 on 2/17/2018.
 */

public class RTGSTodayFragment extends Fragment implements TodayView, AdapterViewInterface<WeekDetailsList>, SwipeRefreshLayout.OnRefreshListener {

    private TodayPresenterImpl presenter;
    private TextView tvDate;
    private RecyclerView rvToday;
    private TextView tvDay;
    private TextView tvAmount, tvAmountCurr;
    private ConstraintLayout clHeader;
    private SimpleRecyclerViewTypeAdapter<WeekDetailsList> simpleRecyclerViewAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private String lastRefreshTime = "";
    private String spGUID="";

    public RTGSTodayFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            spGUID = bundle.getString(ConstantsUtils.EXTRA_SPGUID);
        }
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.route_plan_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvToday = (RecyclerView) view.findViewById(R.id.rvRoutList);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        clHeader = (ConstraintLayout) view.findViewById(R.id.clHeader);
        clHeader.setVisibility(View.VISIBLE);
        tvDay = (TextView) view.findViewById(R.id.tvDay);
        tvAmount = (TextView) view.findViewById(R.id.tvAmount);
        tvAmountCurr = (TextView) view.findViewById(R.id.tvAmountCurr);
        Calendar currentCal = Calendar.getInstance();
        rvToday.setHasFixedSize(true);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewTypeAdapter<>(getContext(), R.layout.snippet_today_plan_item, this, rvToday, null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvToday.setLayoutManager(linearLayoutManager);
        rvToday.setAdapter(simpleRecyclerViewAdapter);
        presenter = new TodayPresenterImpl(getContext(), this,getActivity(),spGUID);
        presenter.onStart();
    }

    @Override
    public void onProgress() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void onHideProgress() {
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void displayData(ArrayList<WeekHeaderList> displayList) {
        if (!displayList.isEmpty()) {
            tvDate.setText(displayList.get(0).getDate());
            tvDay.setText(displayList.get(0).getDay());
            tvAmountCurr.setText(displayList.get(0).getCurrency());
            tvAmount.setText(ConstantsUtils.commaSeparator(displayList.get(0).getTotalAmount(), ""));
            simpleRecyclerViewAdapter.refreshAdapter(displayList.get(0).getWeekDetailsLists());
        }else {
            Calendar currentCal = Calendar.getInstance();
            String dayShortName = currentCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
            tvDate.setText(ConstantsUtils.getDayDate());
            tvDay.setText(dayShortName);
        }
    }

    @Override
    public void displayLastRefreshedTime(String displayTime) {
        try {
            if(getActivity()!=null && isAdded()) {
                lastRefreshTime = displayTime;
                String lastRefresh = "";
                if (!TextUtils.isEmpty(displayTime)) {
                    lastRefresh = getString(R.string.po_last_refreshed) + " " + displayTime;
                }
                ((RTGSActivity) getActivity()).displaySubTitles(lastRefresh);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(WeekDetailsList weekHeaderList, View view, int i) {

    }

    @Override
    public int getItemViewType(int i, ArrayList<WeekDetailsList> arrayList) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new TodayVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, WeekDetailsList weekHeaderList, ArrayList<WeekDetailsList> arrayList) {
        ((TodayVH) viewHolder).tvName.setText(weekHeaderList.getCPName());
//         ((TodayVH) viewHolder).tvDate.setText(weekHeaderList.getCurrentDate());
 //       ((TodayVH) viewHolder).tvRemarks.setText(weekHeaderList.getCrdtCtrlAreaDs());
        ((TodayVH) viewHolder).tvRemarks.setText("Amount");
        ((TodayVH) viewHolder).tvDesc.setText(ConstantsUtils.commaSeparator(weekHeaderList.getPlannedValue(), weekHeaderList.getCurrency()) + " " + weekHeaderList.getCurrency());
//        ((TodayVH) viewHolder).tvDay.setText(weekHeaderList.getDay());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Calendar currentCal = Calendar.getInstance();
        ((RTGSActivity) getActivity()).displaySubTitle(""/*ConstantsUtils.convertCalenderToDisplayDateFormat(currentCal, "dd-MMM-yyyy")*/);
    }

    @Override
    public void onRefresh() {
        presenter.onRefresh();
    }

    @Override
    public void showMsg(String msg) {
        ConstantsUtils.displayLongToast(getContext(), msg);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantsUtils.DATE_SETTINGS_REQUEST_CODE) {
            presenter.onStart();
        }
    }
}
