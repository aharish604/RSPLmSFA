package com.rspl.sf.msfa.mtp;


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
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterViewInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewTypeAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.collectionPlan.HeaderVH;
import com.rspl.sf.msfa.collectionPlan.RouteDetailVH;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mtp.create.MTPCreateActivity;
import com.rspl.sf.msfa.mtp.subordinate.MTPSubOrdActivity;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class MTPCurrentFragment extends Fragment implements MTPCurrentView, AdapterViewInterface<MTPHeaderBean>, SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView rvRoutList;
    private SwipeRefreshLayout swipeRefresh;
    private SimpleRecyclerViewTypeAdapter<MTPHeaderBean> simpleRecyclerViewAdapter;
    private MTPCurrentPrsenterImpl presenter;
    private String comingFrom = "";
    private TextView tvDate;
    private String lastRefreshTime = "";
    private String spGUID = "";
    private TextView noRecordFound;
    private MenuItem menuEdit = null;
    private MenuItem menuSave = null;
    private MenuItem menuCancel = null;
    private boolean isSaveVisible = false;
    private boolean isListChangable = false;
    private Calendar calendarCurrent;
    private boolean isAsmLogin = false;
    private TextView tvActivityDesc;
    private TextView tvRemarks;
    private ImageView ivStatus;
    private boolean isEditDisplay = false;
    private boolean isRejected = false;
    private ConstraintLayout clHeader;

    public MTPCurrentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            comingFrom = bundle.getString(ConstantsUtils.EXTRA_COMING_FROM);
            spGUID = bundle.getString(ConstantsUtils.EXTRA_SPGUID);
            isAsmLogin = bundle.getBoolean(ConstantsUtils.EXTRA_ISASM_LOGIN, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mtp_current, container, false);
    }


    @Override
    public void onProgress() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void ShowDialgueProgress() {

    }

    @Override
    public void HideDialgueProgress() {

    }

    @Override
    public void editAndApprove() {

    }

    @Override
    public void onHideProgress() {
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void displayData(ArrayList<MTPHeaderBean> displayList) {
        simpleRecyclerViewAdapter.refreshAdapter(displayList);
        if (!displayList.isEmpty() && displayList.size() > 1) {
            if (!TextUtils.isEmpty(displayList.get(1).getTestRun())) {
                tvRemarks.setText("Draft");
                if (!comingFrom.equalsIgnoreCase(ConstantsUtils.MTP_APPROVAL) && !comingFrom.equalsIgnoreCase(ConstantsUtils.MTP_SUBORDINATE_CURRENT)) {
                    isEditDisplay = true;
                    displayMenuIcon(isEditDisplay);
                }
            } else {
                if(TextUtils.isEmpty(displayList.get(1).getApprovalStatusDs())){
                    tvRemarks.setText("Saved");
                }else {
                    tvRemarks.setText(displayList.get(1).getApprovalStatusDs());
                }
                if(displayList.get(1).getApprovalStatus().equalsIgnoreCase(Constants.RejectedStatusID)) {
                    isEditDisplay = true;
                    isRejected = false;
                }
                else {
                    isEditDisplay = false;
                    isRejected = false;
                }
                displayMenuIcon(isEditDisplay);
            }
            Calendar calendar = ConstantsUtils.convertCalenderToDisplayDateFormat(displayList.get(1).getFullDate(), "dd-MMM-yyyy");
            tvActivityDesc.setText(ConstantsUtils.convertCalenderToDisplayDateFormat(calendar, "MMM-yyyy"));
        } else {
            tvRemarks.setText("");
        }
        clHeader.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayLastRefreshedTime(String refreshTime) {
        lastRefreshTime = refreshTime;
        String lastRefresh = "";
        if (!TextUtils.isEmpty(refreshTime)) {
            lastRefresh = getString(R.string.po_last_refreshed) + " " + refreshTime;
        }
        if (getActivity() instanceof MTPActivity)
            ((MTPActivity) getActivity()).displaySubTitles(lastRefresh);
        else if (getActivity() instanceof MTPSubOrdActivity)
            ((MTPSubOrdActivity) getActivity()).displaySubTitles(lastRefresh);

    }

    @Override
    public void showMsg(String msg) {
        ConstantsUtils.displayLongToast(getContext(), msg);
    }

    @Override
    public void showSuccessMsg(String message) {
        UtilConstants.dialogBoxWithCallBack(getContext(), "", message, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                presenter.onStart();
            }
        });
    }

    @Override
    public void displayViewPost(int pos) {
        scrollRvToCurrentWeek(pos);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvRoutList = (RecyclerView) view.findViewById(R.id.rvRoutList);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        noRecordFound = (TextView) view.findViewById(R.id.no_record_found);
        tvActivityDesc = (TextView) view.findViewById(R.id.tvActivityDesc);
        tvRemarks = (TextView) view.findViewById(R.id.tvRemarks);
        ivStatus = (ImageView) view.findViewById(R.id.ivStatus);
        clHeader = (ConstraintLayout) view.findViewById(R.id.clHeader);
        ivStatus.setVisibility(View.VISIBLE);
        calendarCurrent = Calendar.getInstance();
        if (calendarCurrent.get(Calendar.DAY_OF_MONTH) <= Constants.getMTPDaysAllowEdit() && !comingFrom.equalsIgnoreCase(ConstantsUtils.MTP_SUBORDINATE_CURRENT))
            isListChangable = true;
        else
            isListChangable = false;
        rvRoutList.setHasFixedSize(true);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewTypeAdapter<>(getContext(), R.layout.snippet_route_plan_item, this, rvRoutList, noRecordFound);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvRoutList.setLayoutManager(linearLayoutManager);
        rvRoutList.setAdapter(simpleRecyclerViewAdapter);
        presenter = new MTPCurrentPrsenterImpl(getActivity(), this, comingFrom, spGUID, isAsmLogin);
        if(!TextUtils.isEmpty(comingFrom) && comingFrom.equalsIgnoreCase( ConstantsUtils.MTP_SUBORDINATE_CURRENT))
            presenter.getMTPOnlineBySPGuid();
        else
            presenter.onStart();
    }

    @Override
    public void onItemClick(MTPHeaderBean weekHeaderList, View view, int i) {
        if (!weekHeaderList.isTitle()) {
            if (isSaveVisible) {
                if (calendarCurrent.get(Calendar.DAY_OF_MONTH) <= Integer.parseInt(weekHeaderList.getDate())) {
                    Intent intent = new Intent(getContext(), MTPCreateActivity.class);
                    intent.putExtra(ConstantsUtils.EXTRA_POS, i);
                    intent.putExtra(ConstantsUtils.EXTRA_ISASM_LOGIN, isAsmLogin);
                    intent.putExtra(ConstantsUtils.EXTRA_DATE, weekHeaderList.getFullDate());
                    intent.putExtra(Constants.EXTRA_BEAN, weekHeaderList);
                    startActivityForResult(intent, MTPCreateActivity.REQUEST_CODE_CREATE_NEXT_MONTH);
                } else {
                    showMsg(getString(R.string.mtp_msg_past_date_validation));
                }
            } else if (!TextUtils.isEmpty(weekHeaderList.getActivityID())) {
                Intent intent = new Intent(getContext(), MTPDetailsActivity.class);
                intent.putExtra(Constants.EXTRA_BEAN, weekHeaderList.getMTPRoutePlanBeanArrayList());
                intent.putExtra(ConstantsUtils.EXTRA_ISASM_LOGIN, isAsmLogin);
                intent.putExtra(ConstantsUtils.EXTRA_COMING_FROM, "");
                startActivity(intent);
            }
        }
    }

    @Override
    public int getItemViewType(int i, ArrayList<MTPHeaderBean> arrayList) {
        MTPHeaderBean weekHeaderList = arrayList.get(i);
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
            View viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mtp_week_item, viewGroup, false);
            return new RouteDetailVH(viewItem);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, MTPHeaderBean weekHeaderList, ArrayList<MTPHeaderBean> arrayList) {
        if (viewHolder instanceof HeaderVH) {
            ((HeaderVH) viewHolder).tvWeekHeader.setText(weekHeaderList.getWeekTitle());
        } else if (viewHolder instanceof RouteDetailVH) {
            String displayName = weekHeaderList.getCustomerName();
            if (TextUtils.isEmpty(displayName)) {
                displayName = weekHeaderList.getSalesDistrictDisc();
            }
            ((RouteDetailVH) viewHolder).tvName.setText(displayName);
            ((RouteDetailVH) viewHolder).tvDate.setText(weekHeaderList.getDate());
            ((RouteDetailVH) viewHolder).tvRemarks.setText(weekHeaderList.getRemarks());
            ((RouteDetailVH) viewHolder).tvDesc.setText(weekHeaderList.getActivityDec());
            ((RouteDetailVH) viewHolder).tvDay.setText(weekHeaderList.getDay());
        }
    }

    private void scrollRvToCurrentWeek(int pos) {
        rvRoutList.getLayoutManager().scrollToPosition(pos);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        displayLastRefreshedTime(lastRefreshTime);
        if (isListChangable) {
            inflater.inflate(R.menu.menu_mtp_next, menu);
            menuEdit = menu.findItem(R.id.menu_edit);
            menuSave = menu.findItem(R.id.menu_save);
            menuCancel = menu.findItem(R.id.menu_cancel);
        }
        if (getActivity() instanceof MTPActivity)
            ((MTPActivity) getActivity()).displayTitle("");
        else if (getActivity() instanceof MTPSubOrdActivity)
            ((MTPSubOrdActivity) getActivity()).displayTitle("");
        displayMenuIcon(isEditDisplay);
    }

    private void displayMenuIcon(boolean displayIcon) {
        if (displayIcon) {
            if (menuEdit != null) {
                menuEdit.setVisible(!isSaveVisible);
                menuSave.setVisible(isSaveVisible);
                menuCancel.setVisible(isSaveVisible);
            }
        } else {
            if (menuEdit != null) {
                menuEdit.setVisible(false);
                menuSave.setVisible(false);
                menuCancel.setVisible(false);
            }
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
                    if(isRejected){
                        UtilConstants.dialogBoxWithCallBack(getContext(), "", getString(R.string.mtp_save_conformation), getString(R.string.mtp_save), getString(R.string.cancel), true, new DialogCallBack() {
                            @Override
                            public void clickedStatus(boolean b) {
                                if (b) {
                                    presenter.onSaveData(getString(R.string.mtp_save));
                                } else {
                                    //presenter.onSaveData(getString(R.string.mtp_draft));
                                }
                                if (menuSave != null) {
                                    isSaveVisible = false;
                                    menuEdit.setVisible(!isSaveVisible);
                                    menuSave.setVisible(isSaveVisible);
                                    menuCancel.setVisible(isSaveVisible);
                                }
                            }
                        });
                    }else{
                        UtilConstants.dialogBoxWithCallBack(getContext(), "", getString(R.string.mtp_save_conformation), getString(R.string.mtp_save), getString(R.string.mtp_draft), true, new DialogCallBack() {
                            @Override
                            public void clickedStatus(boolean b) {
                                if (b) {
                                    presenter.onSaveData(getString(R.string.mtp_save));
                                } else {
                                    presenter.onSaveData(getString(R.string.mtp_draft));
                                }
                                if (menuSave != null) {
                                    isSaveVisible = false;
                                    menuEdit.setVisible(!isSaveVisible);
                                    menuSave.setVisible(isSaveVisible);
                                    menuCancel.setVisible(isSaveVisible);
                                }
                            }
                        });
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
        if(!TextUtils.isEmpty(comingFrom) && comingFrom.equalsIgnoreCase( ConstantsUtils.MTP_SUBORDINATE_CURRENT))
            presenter.getMTPOnlineBySPGuid();
        else
            presenter.onRefresh();
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
}
