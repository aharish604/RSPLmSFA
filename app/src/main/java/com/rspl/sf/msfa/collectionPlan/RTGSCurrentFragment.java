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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterViewInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewTypeAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.collectionPlan.collectionCreate.CollectionPlanCreateActivity;
import com.rspl.sf.msfa.collectionPlan.detail.CollectionPlanDetailActivity;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mtp.subordinate.RTGSSubOrdActivity;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by e10860 on 2/16/2018.
 */

public class
RTGSCurrentFragment extends Fragment implements RTGSView, AdapterViewInterface<WeekHeaderList>, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView rvRoutList;
    private SwipeRefreshLayout swipeRefresh;
    //    private TextView no_record_found;
    private SimpleRecyclerViewTypeAdapter<WeekHeaderList> simpleRecyclerViewAdapter;
    private RTGSCurrentPrsenterImpl presenter;
    private String comingFrom = "", spGUID = "",ExternalRefID="";
    private TextView tvDate;
    private ConstraintLayout clHeader;

    private MenuItem menuEdit = null;
    private MenuItem menuSave = null;
    private boolean isSaveVisible = false;
    private MenuItem menuCancel = null;
    private String lastRefreshTime = "";
    private Calendar calendarCurrent = null;
    private boolean isListChangable = false;

    public RTGSCurrentFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            comingFrom = bundle.getString(ConstantsUtils.EXTRA_COMING_FROM);
            spGUID = bundle.getString(ConstantsUtils.EXTRA_SPGUID);
            ExternalRefID = bundle.getString(ConstantsUtils.EXTRA_ExternalRefID);
        }
        setHasOptionsMenu(true);
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
        simpleRecyclerViewAdapter.refreshAdapter(displayList);
    }

    @Override
    public void displayLastRefreshedTime() {
    }

    @Override
    public void displayViewPost(int i) {
        scrollRvToCurrentWeek(i);
    }

    @Override
    public void showMsg(String message) {
        ConstantsUtils.displayLongToast(getContext(), message);
    }

    @Override
    public void showSuccessMsg(String message) {
        UtilConstants.dialogBoxWithCallBack(getContext(), "", message, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                scrollRvToCurrentWeek(0);
            }
        });
    }

    private void scrollRvToCurrentWeek(int pos) {
        rvRoutList.getLayoutManager().scrollToPosition(pos);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.route_plan_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvRoutList = (RecyclerView) view.findViewById(R.id.rvRoutList);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        clHeader = (ConstraintLayout) view.findViewById(R.id.clHeader);
        clHeader.setVisibility(View.GONE);
        calendarCurrent = Calendar.getInstance();
        if (comingFrom.equalsIgnoreCase(ConstantsUtils.MONTH_CURRENT) || comingFrom.equalsIgnoreCase(ConstantsUtils.RTGS_SUBORDINATE_CURRENT)) {
            tvDate.setText(ConstantsUtils.convertCalenderToDisplayDateFormat(calendarCurrent, "MMM-yyyy"));
            if (calendarCurrent.get(Calendar.DAY_OF_MONTH) <= Constants.getRTGSDaysAllowEdit() /*&& !comingFrom.equalsIgnoreCase(ConstantsUtils.RTGS_SUBORDINATE_CURRENT)*/) {
                isListChangable = true;
            } else {
                isListChangable = false;
            }
        } else {
            calendarCurrent.add(Calendar.MONTH, 1);
            tvDate.setText(ConstantsUtils.convertCalenderToDisplayDateFormat(calendarCurrent, "MMM-yyyy"));
           /* if (!comingFrom.equalsIgnoreCase(ConstantsUtils.RTGS_SUBORDINATE_NEXT))*/
                isListChangable = true;
        }
        rvRoutList.setHasFixedSize(true);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewTypeAdapter<>(getContext(), R.layout.snippet_route_plan_item, this, rvRoutList, null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvRoutList.setLayoutManager(linearLayoutManager);
        rvRoutList.setAdapter(simpleRecyclerViewAdapter);
        presenter = new RTGSCurrentPrsenterImpl(getActivity(), this, comingFrom, spGUID, getContext());
        presenter.onStart();
        displayLastRefreshedTime();
    }

    @Override
    public void onItemClick(WeekHeaderList weekHeaderList, View view, int i) {

        if (!weekHeaderList.isTitle()) {
            if (!weekHeaderList.isSunday() && !weekHeaderList.isSecondSat()) {
                if (isSaveVisible) {
                    if (calendarCurrent.get(Calendar.DAY_OF_MONTH) <= Integer.parseInt(weekHeaderList.getDate())
                            || comingFrom.equalsIgnoreCase(ConstantsUtils.MONTH_NEXT)
                            || comingFrom.equalsIgnoreCase(ConstantsUtils.RTGS_SUBORDINATE_NEXT)) {
                        Intent intent = new Intent(getContext(), CollectionPlanCreateActivity.class);
                        intent.putExtra(ConstantsUtils.EXTRA_POS, i);
                        intent.putExtra(ConstantsUtils.EXTRA_DATE, weekHeaderList.getFullDate());
                        intent.putExtra(Constants.EXTRA_BEAN, weekHeaderList);
                        intent.putExtra(Constants.comingFrom, comingFrom);
                        intent.putExtra(ConstantsUtils.EXTRA_ExternalRefID, ExternalRefID);
                        startActivityForResult(intent, CollectionPlanCreateActivity.CUSTOMER_CATEGROIZE_REQUEST);
                    } else {
                        showMsg(getString(R.string.mtp_msg_past_date_validation));
                    }
                } else if (!TextUtils.isEmpty(weekHeaderList.getName())) {
                    Intent intent = new Intent(getActivity(), CollectionPlanDetailActivity.class);
                    intent.putExtra(Constants.EXTRA_COLLECTION_DETAIL, weekHeaderList.getWeekDetailsLists());
                    startActivity(intent);
                }
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantsUtils.DATE_SETTINGS_REQUEST_CODE) {
            presenter.onStart();
        } else {
            presenter.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public int getItemViewType(int i, ArrayList<WeekHeaderList> arrayList) {
        WeekHeaderList weekHeaderList = arrayList.get(i);
        if (weekHeaderList.isTitle()) {
            return 1;//title
        } else {
            return 2;
        }
    }

    @Override
    public void onDestroyView() {
        presenter.onDestroy();
        super.onDestroyView();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        if (i == 1) {
            //title
            return new HeaderVH(view);
        } else {
            View viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.snippet_week_detail, viewGroup, false);
            return new RouteDetailVH(viewItem);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, WeekHeaderList weekHeaderList, ArrayList<WeekHeaderList> arrayList) {
        if (viewHolder instanceof HeaderVH) {
            ((HeaderVH) viewHolder).tvWeekHeader.setText(weekHeaderList.getWeekTitle());
        } else if (viewHolder instanceof RouteDetailVH) {
            if (weekHeaderList.isSunday() || weekHeaderList.isSecondSat()) {
                ((RouteDetailVH) viewHolder).cvItem.setBackgroundColor(getResources().getColor(R.color.GREY));
            } else {
                ((RouteDetailVH) viewHolder).cvItem.setBackgroundColor(getResources().getColor(R.color.white));
            }
            ((RouteDetailVH) viewHolder).tvName.setText(weekHeaderList.getName());
            ((RouteDetailVH) viewHolder).tvDate.setText(weekHeaderList.getDate());
            ((RouteDetailVH) viewHolder).tvRemarks.setText(weekHeaderList.getRemarks());
            if (!"".equals(weekHeaderList.getTotalAmount()) && Double.parseDouble(weekHeaderList.getTotalAmount()) > 0) {
                ((RouteDetailVH) viewHolder).tvDesc.setText(ConstantsUtils.commaSeparator(weekHeaderList.getTotalAmount(), weekHeaderList.getCurrency()) + " " + weekHeaderList.getCurrency());
            } else {
                ((RouteDetailVH) viewHolder).tvDesc.setText("");
            }

            if (!"".equals(weekHeaderList.getTotalAchivedAmount()) && Double.parseDouble(weekHeaderList.getTotalAchivedAmount()) > 0) {
                ((RouteDetailVH) viewHolder).tvRemarks.setVisibility(View.VISIBLE);
                ((RouteDetailVH) viewHolder).tvRemarks.setText(ConstantsUtils.commaSeparator(weekHeaderList.getTotalAchivedAmount(), weekHeaderList.getCurrency()) + " " + weekHeaderList.getCurrency());
            } else {
                ((RouteDetailVH) viewHolder).tvRemarks.setVisibility(View.GONE);
                ((RouteDetailVH) viewHolder).tvRemarks.setText("");
            }
            ((RouteDetailVH) viewHolder).tvDay.setText(weekHeaderList.getDay());

        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        Calendar currentCal = Calendar.getInstance();
        if (comingFrom.equalsIgnoreCase(ConstantsUtils.MONTH_CURRENT) || comingFrom.equalsIgnoreCase(ConstantsUtils.RTGS_SUBORDINATE_CURRENT)) {
            if (getActivity() instanceof RTGSActivity)
                ((RTGSActivity) getActivity()).displaySubTitle(" : " + ConstantsUtils.convertCalenderToDisplayDateFormat(currentCal, "MMM-yyyy"));
            else if (getActivity() instanceof RTGSSubOrdActivity)
                ((RTGSSubOrdActivity) getActivity()).displayTitle(" : " + ConstantsUtils.convertCalenderToDisplayDateFormat(currentCal, "MMM-yyyy"));
        } else {
            currentCal.add(Calendar.MONTH, 1);
            if (getActivity() instanceof RTGSActivity)
                ((RTGSActivity) getActivity()).displaySubTitle(" : " + ConstantsUtils.convertCalenderToDisplayDateFormat(currentCal, "MMM-yyyy"));
            else if (getActivity() instanceof RTGSSubOrdActivity)
                ((RTGSSubOrdActivity) getActivity()).displayTitle(" : " + ConstantsUtils.convertCalenderToDisplayDateFormat(currentCal, "MMM-yyyy"));
        }
        if (isListChangable) {
            inflater.inflate(R.menu.menu_mtp_next, menu);
//        ((MTPActivity) getActivity()).displayTitle("");
//        displayLastRefreshedTime(lastRefreshTime);
            menuEdit = menu.findItem(R.id.menu_edit);
            menuSave = menu.findItem(R.id.menu_save);
            menuCancel = menu.findItem(R.id.menu_cancel);
            menuEdit.setVisible(!isSaveVisible);
            menuSave.setVisible(isSaveVisible);
            menuCancel.setVisible(isSaveVisible);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
                    if (menuSave != null) {
                        isSaveVisible = true;
                        menuEdit.setVisible(!isSaveVisible);
                        menuSave.setVisible(isSaveVisible);
                        menuCancel.setVisible(isSaveVisible);
                    }
                } else {
                    UtilConstants.dialogBoxWithCallBack(getContext(), "", getContext().getString(R.string.autodate_change_msg), getContext().getString(R.string.autodate_change_btn), "", false, new com.arteriatech.mutils.interfaces.DialogCallBack() {
                        @Override
                        public void clickedStatus(boolean b) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), ConstantsUtils.DATE_SETTINGS_REQUEST_CODE);
                        }
                    });
                }
                return true;
            case R.id.menu_save:
                if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
                    UtilConstants.dialogBoxWithCallBack(getContext(), "", getString(R.string.mtp_save_conformation), getString(R.string.mtp_save), getString(R.string.cancel), true, new DialogCallBack() {
                        @Override
                        public void clickedStatus(boolean b) {
                            if (b) {
                                presenter.onSaveData(getString(R.string.mtp_save), comingFrom);
                            }
                            if (menuSave != null) {
                                isSaveVisible = false;
                                menuEdit.setVisible(!isSaveVisible);
                                menuSave.setVisible(isSaveVisible);
                                menuCancel.setVisible(isSaveVisible);
                            }
                        }
                    });
                    return true;
                } else {
                    UtilConstants.dialogBoxWithCallBack(getContext(), "", getContext().getString(R.string.autodate_change_msg), getContext().getString(R.string.autodate_change_btn), "", false, new com.arteriatech.mutils.interfaces.DialogCallBack() {
                        @Override
                        public void clickedStatus(boolean b) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), ConstantsUtils.DATE_SETTINGS_REQUEST_CODE);
                        }
                    });
                }
            case R.id.menu_cancel:
                if (menuSave != null) {
                    isSaveVisible = false;
                    menuEdit.setVisible(!isSaveVisible);
                    menuSave.setVisible(isSaveVisible);
                    menuCancel.setVisible(isSaveVisible);
                    presenter.onStart();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        presenter.onRefresh();
    }

    @Override
    public void displayLastRefreshedTime(String displayTime) {
        lastRefreshTime = displayTime;
        String lastRefresh = "";
        if (!TextUtils.isEmpty(displayTime)) {
            lastRefresh = getString(R.string.po_last_refreshed) + " " + displayTime;
        }
        if (getActivity() instanceof RTGSActivity)
            ((RTGSActivity) getActivity()).displaySubTitles(lastRefresh);
        else if (getActivity() instanceof RTGSSubOrdActivity)
            ((RTGSSubOrdActivity) getActivity()).displaySubTitles(lastRefresh);
    }
}
