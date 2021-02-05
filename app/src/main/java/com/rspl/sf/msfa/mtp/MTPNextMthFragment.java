package com.rspl.sf.msfa.mtp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterViewInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewTypeAdapter;
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.collectionPlan.HeaderVH;
import com.rspl.sf.msfa.collectionPlan.RouteDetailVH;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.AsyncTaskCallBack;
import com.rspl.sf.msfa.interfaces.CustomDialogCallBack;
import com.rspl.sf.msfa.mtp.approval.MTPApprovalActivity;
import com.rspl.sf.msfa.mtp.create.MTPCreateActivity;
import com.rspl.sf.msfa.mtp.subordinate.MTPSubOrdActivity;
import com.rspl.sf.msfa.reports.daySummary.DaySummaryPresenterImpl;
import com.rspl.sf.msfa.so.SOUtils;
import com.rspl.sf.msfa.store.OnlineManager;
import com.rspl.sf.msfa.ui.fabTnsfmgToolBar.FABToolbarLayout;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MTPNextMthFragment extends Fragment implements MTPCurrentView, View.OnClickListener, UIListener, AsyncTaskCallBack, AdapterViewInterface<MTPHeaderBean>, SwipeRefreshLayout.OnRefreshListener, OnlineODataInterface {
    public String entityKey = "";
    public String initiator = "";
    private RecyclerView rvRoutList;
    private SwipeRefreshLayout swipeRefresh;
    private SimpleRecyclerViewTypeAdapter<MTPHeaderBean> simpleRecyclerViewAdapter;
    private MTPCurrentPrsenterImpl presenter;
    private TextView tvDate;
    private MenuItem menuEdit = null;
    private MenuItem menuSave = null;
    private boolean isSaveVisible = false;
    private boolean isEditDisplay = false;
    private TextView tvHeaderDate;
    private ImageView ivStatus;
    private TextView tvStatusDesc;
    private MenuItem menuCancel = null;
    private String lastRefreshTime = "";
    private String spGUID = "";
    private TextView noRecordFound;
    private boolean isAsmLogin = false;
    private String comingFrom = "";
    private ArrayList<MTPHeaderBean> displayList = new ArrayList<>();
    private ArrayList<MTPHeaderBean> tempDisplayList = new ArrayList<>();
    private FloatingActionButton fabToolbar;
    private FABToolbarLayout fabToolbarContainer;
    private View tvReject;
    private View tvApprove;
    private View tvEditApprove;
    private View viewLayout;
    private String mStrInstanceId = "";
    private ProgressDialog progressDialog;
    private UIListener uiListener;
    private AsyncTaskCallBack asyncTaskCallBack;
    private Hashtable<String, String> masterHeaderTable = new Hashtable<>();
    private RelativeLayout fabtoolbar_container;
    private String sDesisionKey = "";
    private boolean isRejected = false;
    private boolean isClickable = false;
    private boolean isEditAndApprove = false;
    private String monthForApproval="";

    public MTPNextMthFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.getString(ConstantsUtils.EXTRA_COMING_FROM, "").equals(ConstantsUtils.MTP_APPROVAL)) {
                comingFrom = ConstantsUtils.MTP_APPROVAL;
                displayList = (ArrayList<MTPHeaderBean>) bundle.getSerializable(Constants.EXTRA_BEAN);
                tempDisplayList = (ArrayList<MTPHeaderBean>) bundle.getSerializable(Constants.EXTRA_BEAN);
                mStrInstanceId = bundle.getString(ConstantsUtils.ROUTE_INSTANCE_ID, "");
                entityKey = bundle.getString(ConstantsUtils.ROUTE_ENTITY_KEY, "");
                initiator = bundle.getString(Constants.Initiator, "");
              /*  if (!displayList.isEmpty())
                    entityKey = displayList.get(0).getRouteSchGUID();*/
            } else {
                comingFrom = bundle.getString(ConstantsUtils.EXTRA_COMING_FROM);
                isAsmLogin = bundle.getBoolean(ConstantsUtils.EXTRA_ISASM_LOGIN, false);
                spGUID = bundle.getString(ConstantsUtils.EXTRA_SPGUID);
            }
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mtp_next, container, false);
    }


    @Override
    public void onProgress() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void ShowDialgueProgress() {
        progressDialog = ConstantsUtils.showProgressDialog(getActivity(), "Update data please wait...");
    }

    @Override
    public void HideDialgueProgress() {
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @Override
    public void editAndApprove() {
        if (!TextUtils.isEmpty(mStrInstanceId)) {
            /*SOUtils.showCommentsEditApproveDialog(getActivity(), new CustomDialogCallBack() {
                @Override
                public void cancelDialogCallBack(boolean userClicked, String ids, String description) {
                    if (userClicked) {
                        approveOrder(mStrInstanceId, "01", entityKey, description + "");
                    }else{
                        isClickable = false;
                    }
                }
            }, getString(R.string.approve_title_comments));*/
            approveOrder(mStrInstanceId, "01", entityKey, "" + "");
        }else{
            isClickable = false;
        }
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
                tvStatusDesc.setText("Draft");
                if (!comingFrom.equalsIgnoreCase(ConstantsUtils.MTP_APPROVAL) && !comingFrom.equalsIgnoreCase(ConstantsUtils.MTP_SUBORDINATE_NEXT)) {
                    isEditDisplay = true;
                    displayMenuIcon(isEditDisplay);
                }
            } else {
                if(TextUtils.isEmpty(displayList.get(1).getApprovalStatusDs())){
                    tvStatusDesc.setText("Saved");
                }else {
                    tvStatusDesc.setText(displayList.get(1).getApprovalStatusDs());
                }
//                tvStatusDesc.setText(displayList.get(1).getApprovalStatusDs());
                if( !comingFrom.equalsIgnoreCase(ConstantsUtils.MTP_SUBORDINATE_NEXT) && displayList.get(1).getApprovalStatus().equalsIgnoreCase(Constants.RejectedStatusID)) {
                    isEditDisplay = true;
                    isRejected = false;
                }
                else if(isEditAndApprove) {
                    isEditDisplay = true;
                    isRejected = false;
                }else{
                    isEditDisplay = false;
                    isRejected = false;
                }
                displayMenuIcon(isEditDisplay);
            }
            Calendar calendar = ConstantsUtils.convertCalenderToDisplayDateFormat(displayList.get(1).getFullDate(), "dd-MMM-yyyy");
            tvHeaderDate.setText(ConstantsUtils.convertCalenderToDisplayDateFormat(calendar, "MMM-yyyy"));
        } else {
            tvStatusDesc.setText("");
        }
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
        UtilConstants.dialogBoxWithCallBack(getContext(), "", message, getString(R.string.ok), "", true, new DialogCallBack() {
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
        noRecordFound = (TextView) view.findViewById(R.id.no_record_found);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvHeaderDate = (TextView) view.findViewById(R.id.tvHeaderDate);
        ivStatus = (ImageView) view.findViewById(R.id.ivStatus);
        viewLayout = view.findViewById(android.R.id.content);
        tvStatusDesc = (TextView) view.findViewById(R.id.tvStatusDesc);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        fabToolbarContainer = (FABToolbarLayout) view.findViewById(R.id.fabtoolbarContainer);
        fabtoolbar_container = (RelativeLayout) view.findViewById(R.id.fabtoolbar_container);
        fabToolbar = (FloatingActionButton) view.findViewById(R.id.fabtoolbar);
        fabToolbar.setOnClickListener(this);
//        Calendar currentCal = Calendar.getInstance();
//        currentCal.add(Calendar.MONTH, 1);
//        tvHeaderDate.setText(ConstantsUtils.convertCalenderToDisplayDateFormat(currentCal, "MMM-yyyy"));
//        tvStatusDesc.setText("Draft");
        rvRoutList.setHasFixedSize(true);
        tvReject = view.findViewById(R.id.tvReject);
        tvReject.setOnClickListener(this);
        tvEditApprove = view.findViewById(R.id.tvEditApprove);
        tvEditApprove.setOnClickListener(this);
        tvApprove = view.findViewById(R.id.tvApprove);
        tvApprove.setOnClickListener(this);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewTypeAdapter<>(getContext(), R.layout.snippet_route_plan_item, this, rvRoutList, noRecordFound);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvRoutList.setLayoutManager(linearLayoutManager);
        rvRoutList.setAdapter(simpleRecyclerViewAdapter);
        uiListener = this;
        asyncTaskCallBack = this;
        if (comingFrom.equals(ConstantsUtils.MTP_APPROVAL)) {
            calendarCurrent = Calendar.getInstance();
            if (calendarCurrent.get(Calendar.DAY_OF_MONTH) <= Constants.getMTPDaysAllowEdit())
                isListChangable = true;
            else
                isListChangable = false;
            isListChangable = true;
            if(isListChangable){
                tvEditApprove.setVisibility(View.VISIBLE);
            }else {
                tvEditApprove.setVisibility(View.GONE);
            }
//            tvEditApprove.setVisibility(View.GONE);
            displayData(displayList);
            if(displayList!=null && displayList.size()>0){
                monthForApproval = displayList.get(1).getMonthForApproval();
            }
            presenter = new MTPCurrentPrsenterImpl(getActivity(), this, "", spGUID, isAsmLogin,displayList);
            fabtoolbar_container.setVisibility(View.VISIBLE);
        } else {
            fabtoolbar_container.setVisibility(View.GONE);
            presenter = new MTPCurrentPrsenterImpl(getActivity(), this, ConstantsUtils.MONTH_NEXT, spGUID, isAsmLogin);
            if(!TextUtils.isEmpty(comingFrom) && comingFrom.equalsIgnoreCase( ConstantsUtils.MTP_SUBORDINATE_NEXT))
                presenter.getMTPOnlineBySPGuid();
            else
                presenter.onStart();
        }
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rvRoutList.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    if (!fabToolbarContainer.isFab())
                        fabToolbarContainer.hide();
                }
            });
        }*/
        rvRoutList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!fabToolbarContainer.isFab())
                    fabToolbarContainer.hide();
            }
        });
//        tvEditApprove.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(MTPHeaderBean weekHeaderList, View view, int i) {
        if (!weekHeaderList.isTitle()) {
            if (isSaveVisible) {
                if(comingFrom.equalsIgnoreCase(ConstantsUtils.MTP_APPROVAL)){
                    int month = 0;
                    try {
                        if(!TextUtils.isEmpty(monthForApproval)){
                            month=Integer.parseInt(monthForApproval);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if((calendarCurrent.get(Calendar.MONTH)+1)==month) {
                        if (calendarCurrent.get(Calendar.DAY_OF_MONTH) <= Integer.parseInt(weekHeaderList.getDate())) {
                            Intent intent = new Intent(getContext(), MTPCreateActivity.class);
                            intent.putExtra(ConstantsUtils.EXTRA_POS, i);
                            intent.putExtra(ConstantsUtils.EXTRA_DATE, weekHeaderList.getFullDate());
                            intent.putExtra(Constants.EXTRA_BEAN, weekHeaderList);
                            intent.putExtra(ConstantsUtils.EXTRA_ISASM_LOGIN, isAsmLogin);
                            intent.putExtra(Constants.Initiator, initiator);
                            intent.putExtra(Constants.comingFrom, comingFrom);
                            startActivityForResult(intent, MTPCreateActivity.REQUEST_CODE_CREATE_NEXT_MONTH);
                        } else {
                            showMsg(getString(R.string.mtp_msg_past_date_validation));
                        }
                    }else {
                        Intent intent = new Intent(getContext(), MTPCreateActivity.class);
                        intent.putExtra(ConstantsUtils.EXTRA_POS, i);
                        intent.putExtra(ConstantsUtils.EXTRA_DATE, weekHeaderList.getFullDate());
                        intent.putExtra(Constants.EXTRA_BEAN, weekHeaderList);
                        intent.putExtra(ConstantsUtils.EXTRA_ISASM_LOGIN, isAsmLogin);
                        intent.putExtra(Constants.Initiator, initiator);
                        intent.putExtra(Constants.comingFrom, comingFrom);
                        startActivityForResult(intent, MTPCreateActivity.REQUEST_CODE_CREATE_NEXT_MONTH);
                    }
                }else {
                    Intent intent = new Intent(getContext(), MTPCreateActivity.class);
                    intent.putExtra(ConstantsUtils.EXTRA_POS, i);
                    intent.putExtra(ConstantsUtils.EXTRA_DATE, weekHeaderList.getFullDate());
                    intent.putExtra(Constants.EXTRA_BEAN, weekHeaderList);
                    intent.putExtra(ConstantsUtils.EXTRA_ISASM_LOGIN, isAsmLogin);
                    intent.putExtra(Constants.Initiator, initiator);
                    intent.putExtra(Constants.comingFrom, comingFrom);
                    startActivityForResult(intent, MTPCreateActivity.REQUEST_CODE_CREATE_NEXT_MONTH);
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
        if (getActivity() instanceof MTPActivity)
            presenter.onDestroy();
        else if (getActivity() instanceof MTPSubOrdActivity)
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
    private boolean isListChangable = false;
    private Calendar calendarCurrent;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_mtp_next, menu);
        if (getActivity() instanceof MTPActivity)
            ((MTPActivity) getActivity()).displayTitle("");
        else if (getActivity() instanceof MTPSubOrdActivity)
            ((MTPSubOrdActivity) getActivity()).displayTitle("");
        displayLastRefreshedTime(lastRefreshTime);
        menuEdit = menu.findItem(R.id.menu_edit);
        menuSave = menu.findItem(R.id.menu_save);
        menuCancel = menu.findItem(R.id.menu_cancel);

        displayMenuIcon(isEditDisplay);
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
                    if (comingFrom.equalsIgnoreCase(ConstantsUtils.MTP_APPROVAL)) {
                        UtilConstants.dialogBoxWithCallBack(getContext(), "", getString(R.string.mtp_save_conformation), getString(R.string.yes), getString(R.string.no), true, new DialogCallBack() {
                            @Override
                            public void clickedStatus(boolean b) {
                                if (b) {
                                    String period = ConstantsUtils.getMonth();
                                    if(!TextUtils.isEmpty(monthForApproval) && period.equalsIgnoreCase(monthForApproval))
                                        presenter.onSaveData(true);
                                    else
                                        presenter.onSaveData(false);
                                } else {
                                    //presenter.onSaveData(getString(R.string.mtp_draft));
                                    getActivity().finish();
                                }
                                if (menuSave != null) {
                                    isSaveVisible = false;
                                    menuEdit.setVisible(!isSaveVisible);
                                    menuSave.setVisible(isSaveVisible);
                                    menuCancel.setVisible(isSaveVisible);
                                }
                            }
                        });
                    }else {
                        if (isRejected) {
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
                        } else {
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
//                    isSaveVisible = false;
//                    menuEdit.setVisible(!isSaveVisible);
//                    menuSave.setVisible(isSaveVisible);
//                    menuCancel.setVisible(isSaveVisible);
                    fabtoolbar_container.setVisibility(View.GONE);
                    if (comingFrom.equalsIgnoreCase(ConstantsUtils.MTP_APPROVAL)){
                        UtilConstants.dialogBoxWithCallBack(getContext(), "", "Do you want to cancel the changes?", getString(R.string.yes), getString(R.string.no), true, new DialogCallBack() {
                            @Override
                            public void clickedStatus(boolean b) {
                                if (b) {
                                    getActivity().finish();
                                } else {

                                }
                            }
                        });
                        displayData(displayList);
                    }else {
//                        fabtoolbar_container.setVisibility(View.VISIBLE);
                        isSaveVisible = false;
                        menuEdit.setVisible(!isSaveVisible);
                        menuSave.setVisible(isSaveVisible);
                        menuCancel.setVisible(isSaveVisible);
                        presenter.onStart();
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onSaveEditApproveData(ArrayList<MTPHeaderBean> displayList,String mStrInstanceId, String desisionKey, String soNo) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        String rollType = sharedPreferences.getString(Constants.USERROLE, "");


        Hashtable<String, String> masterHeaderTable = new Hashtable<>();
        GUID guid = GUID.newRandom();
        String headerGuid = guid.toString36();
        String doc_no = (System.currentTimeMillis() + "").substring(3, 10);
        masterHeaderTable.put(Constants.SalesPersonID, Constants.getSPGUID(Constants.SPGUID));
        masterHeaderTable.put(Constants.IS_UPDATE, "");
        masterHeaderTable.put(Constants.InstanceID, mStrInstanceId);
//        masterHeaderTable.put(Constants.EntityType, "ROUTE");
        masterHeaderTable.put(Constants.DecisionKey, desisionKey);
//        masterHeaderTable.put(Constants.LoginID, loginIdVal);
        masterHeaderTable.put(Constants.EntityKey, soNo);
        String period = ConstantsUtils.getMonth();
        String periodYear = SOUtils.getYearFromCalender(getActivity(), getActivity().getString(R.string.so_filter_current_mont));

        Calendar currentCal = Calendar.getInstance();
        currentCal.set(Calendar.DAY_OF_MONTH, 1);
        currentCal.set(Calendar.MILLISECOND, 0);
        currentCal.set(Calendar.SECOND, 0);
        currentCal.set(Calendar.MINUTE, 0);
        currentCal.set(Calendar.HOUR, 0);
        masterHeaderTable.put(Constants.ValidFrom, ConstantsUtils.convertCalenderToDisplayDateFormat(currentCal, "yyyy-MM-dd'T'HH:mm:ss"));
        currentCal = Calendar.getInstance();
        currentCal.set(Calendar.DAY_OF_MONTH, currentCal.getActualMaximum(Calendar.DAY_OF_MONTH));
        currentCal.set(Calendar.MILLISECOND, 0);
        currentCal.set(Calendar.SECOND, 0);
        currentCal.set(Calendar.MINUTE, 0);
        currentCal.set(Calendar.HOUR, 0);
        masterHeaderTable.put(Constants.ValidTo, ConstantsUtils.convertCalenderToDisplayDateFormat(currentCal, "yyyy-MM-dd'T'HH:mm:ss"));

        masterHeaderTable.put(Constants.EntityType, Constants.RouteSchedules);

        ArrayList<HashMap<String, String>> itemTable = new ArrayList<>();
        for (MTPHeaderBean mtpHeaderBean : displayList) {
            if (!TextUtils.isEmpty(mtpHeaderBean.getActivityID())) {
                if (!TextUtils.isEmpty(mtpHeaderBean.getRouteSchGUID())) {
                    headerGuid = mtpHeaderBean.getRouteSchGUID();
                    masterHeaderTable.put(Constants.IS_UPDATE, "X");
                    masterHeaderTable.put(Constants.RoutId, mtpHeaderBean.getRoutId());
                    masterHeaderTable.put(Constants.CreatedBy, mtpHeaderBean.getCreatedBy());
                    Calendar createdOnCal = ConstantsUtils.convertCalenderToDisplayDateFormat(mtpHeaderBean.getCreatedOn(), "dd-MMM-yyyy");
                    createdOnCal.set(Calendar.MILLISECOND, 0);
                    createdOnCal.set(Calendar.SECOND, 0);
                    createdOnCal.set(Calendar.MINUTE, 0);
                    createdOnCal.set(Calendar.HOUR, 0);
                    masterHeaderTable.put(Constants.CreatedOn, ConstantsUtils.convertCalenderToDisplayDateFormat(createdOnCal, "yyyy-MM-dd'T'HH:mm:ss"));
                } else {
                    masterHeaderTable.put(Constants.IS_UPDATE, "");
                }
                if (!TextUtils.isEmpty(mtpHeaderBean.getDeviceNo())) {
                    doc_no = mtpHeaderBean.getDeviceNo();
                }
                if(!TextUtils.isEmpty(mtpHeaderBean.getApprovalStatus()) && mtpHeaderBean.getApprovalStatus().equalsIgnoreCase(Constants.RejectedStatusID)) {
                    masterHeaderTable.put(Constants.ApprovalStatus, "");
                    masterHeaderTable.put(Constants.ApprovalStatusDs, "");
                }else{
                    masterHeaderTable.put(Constants.ApprovalStatus, mtpHeaderBean.getApprovalStatus());
                    masterHeaderTable.put(Constants.ApprovalStatusDs, mtpHeaderBean.getApprovalStatusDs());
                }
                for (MTPRoutePlanBean routePlanBean : mtpHeaderBean.getMTPRoutePlanBeanArrayList()) {
                    HashMap<String, String> dbItemTable = new HashMap<>();

                    if (!TextUtils.isEmpty(routePlanBean.getRouteSchPlanGUID())) {
                        dbItemTable.put(Constants.RouteSchPlanGUID, routePlanBean.getRouteSchPlanGUID());
                    } else {
                        guid = GUID.newRandom();
                        dbItemTable.put(Constants.RouteSchPlanGUID, guid.toString36());
                    }
                    dbItemTable.put(Constants.RouteSchGUID, headerGuid);
                    Calendar calendar = ConstantsUtils.convertCalenderToDisplayDateFormat(mtpHeaderBean.getFullDate(), "dd-MMM-yyyy");
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.HOUR, 0);
                    dbItemTable.put(Constants.VisitDate, ConstantsUtils.convertCalenderToDisplayDateFormat(calendar, "yyyy-MM-dd'T'HH:mm:ss"));
                    dbItemTable.put(Constants.ActivityID, routePlanBean.getActivityId());
                    dbItemTable.put(Constants.ActivityDesc, routePlanBean.getActivityDec());

                    if (isAsmLogin  || rollType.equalsIgnoreCase("Z3")) {
                        dbItemTable.put(Constants.SalesDistrict, routePlanBean.getSalesDistrict());
                        dbItemTable.put(Constants.SalesDistrictDesc, routePlanBean.getSalesDistrictDesc());
                    } else {
                        dbItemTable.put(Constants.VisitCPGUID, routePlanBean.getCustomerNo());
                        dbItemTable.put(Constants.VisitCPName, routePlanBean.getCustomerName());
                    }
                    dbItemTable.put(Constants.Remarks, routePlanBean.getRemarks());
                    itemTable.add(dbItemTable);
                }
            }
        }
        if (itemTable.size() > 0) {
            masterHeaderTable.put(Constants.Month, period);
            masterHeaderTable.put(Constants.Year, periodYear);
            masterHeaderTable.put(Constants.RouteSchGUID, headerGuid);
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 6);
            //    new MTPPostToServerAsyncTask(getActivity(), this, masterHeaderTable, itemTable, 3, bundle).execute();
        } else {
            if(progressDialog!=null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            showMsg("Please enter tour plan details");

        }
    }

    @Override
    public void onRefresh() {
        if (!comingFrom.equals(ConstantsUtils.MTP_APPROVAL)) {
            if(!TextUtils.isEmpty(comingFrom) && comingFrom.equalsIgnoreCase( ConstantsUtils.MTP_SUBORDINATE_NEXT))
                presenter.getMTPOnlineBySPGuid();
            else
                presenter.onRefresh();
        } else {
            swipeRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==ConstantsUtils.DATE_SETTINGS_REQUEST_CODE){
            presenter.onStart();
        }else {
            presenter.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabtoolbar:
                fabToolbarContainer.show();
                break;
            case R.id.tvApprove:
                if(!isClickable) {
                    isClickable = true;
                    approveCredit();
                    fabToolbarContainer.hide();
                }
                isEditAndApprove = false;
                break;
            case R.id.tvReject:
                if(!isClickable) {
                    isClickable = true;
                    rejectCredit();
                    fabToolbarContainer.hide();
                }
                isEditAndApprove = false;

                break;
            case R.id.tvEditApprove:
//                if (!isClickable) {
//                    isClickable = true;
//                    rejectCredit();

                fabToolbarContainer.hide();
                fabtoolbar_container.setVisibility(View.GONE);
//                }
                isEditAndApprove = true;
                isEditDisplay = true;
                isRejected = false;

                displayMenuIcon(isEditDisplay);
                break;
        }
    }

    private void rejectCredit() {
        if (!TextUtils.isEmpty(mStrInstanceId)) {
            SOUtils.showCommentsDialog(getActivity(), new CustomDialogCallBack() {
                @Override
                public void cancelDialogCallBack(boolean userClicked, String ids, String description) {
                    if (userClicked) {
                        approveOrder(mStrInstanceId, "02", entityKey, description + "");
                    }else{
                        isClickable = false;
                    }
                }
            }, getString(R.string.approve_title_comments));
        }else{
            isClickable = false;
        }
    }

    private void approveCredit() {
        if (!TextUtils.isEmpty(mStrInstanceId)) {
            SOUtils.showCommentsDialog(getActivity(), new CustomDialogCallBack() {
                @Override
                public void cancelDialogCallBack(boolean userClicked, String ids, String description) {
                    if (userClicked) {
                        approveOrder(mStrInstanceId, "01", entityKey, description + "");
                    }else{
                        isClickable = false;
                    }
                }
            }, getString(R.string.approve_title_comments));
        }else{
            isClickable = false;
        }
    }

    private void editApprove(){
        if (UtilConstants.isNetworkAvailable(getActivity())) {
            progressDialog = ConstantsUtils.showProgressDialog(getActivity(), "Update data please wait...");
            sDesisionKey="01";
            onSaveEditApproveData(displayList,mStrInstanceId, "01", entityKey);
        } else {
            ConstantsUtils.displayLongToast(getContext(),getContext().getString(R.string.no_network_conn));
        }
    }

    /*approve order*/
    private void approveOrder(String mStrInstanceId, String desisionKey, String soNo, String comments) {
        if (UtilConstants.isNetworkAvailable(getActivity())) {
            sDesisionKey = desisionKey;
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
            String loginIdVal = sharedPreferences.getString(Constants.username, "");
            masterHeaderTable.clear();
            masterHeaderTable.put(Constants.InstanceID, mStrInstanceId);
            masterHeaderTable.put(Constants.EntityType, "ROUTE");
            masterHeaderTable.put(Constants.DecisionKey, desisionKey);
            masterHeaderTable.put(Constants.LoginID, loginIdVal);
            masterHeaderTable.put(Constants.EntityKey, soNo);
            masterHeaderTable.put(Constants.Comments, comments);

            JSONObject headerObject = new JSONObject();
            try {
                headerObject.putOpt(Constants.InstanceID, masterHeaderTable.get(Constants.InstanceID));
                headerObject.putOpt(Constants.EntityType, masterHeaderTable.get(Constants.EntityType));
                headerObject.putOpt(Constants.DecisionKey, masterHeaderTable.get(Constants.DecisionKey));
                headerObject.putOpt(Constants.LoginID, masterHeaderTable.get(Constants.LoginID));
                headerObject.putOpt(Constants.EntityKey, masterHeaderTable.get(Constants.EntityKey));
                headerObject.putOpt(Constants.Comments, masterHeaderTable.get(Constants.Comments));
            } catch (Throwable e) {
                e.printStackTrace();
            }
            String qry =Constants.Tasks + "(InstanceID='" + masterHeaderTable.get(Constants.InstanceID) + "',EntityType='" + masterHeaderTable.get(Constants.EntityType) + "')";

            progressDialog = ConstantsUtils.showProgressDialog(getActivity(), "Update data please wait...");

            OnlineManager.updateEntity("",headerObject.toString(),qry, MTPNextMthFragment.this,getActivity());
            /*new DirecySyncAsyncTask(getActivity(), asyncTaskCallBack, uiListener, masterHeaderTable, null, 2).execute();*/
            DaySummaryPresenterImpl.isReloadMTPApproval = true;
        } else {
            ConstantsUtils.displayLongToast(getContext(),getContext().getString(R.string.no_network_conn));
        }
    }

    @Override
    public void onRequestError(int i, Exception e) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        String status = getString(R.string.mtp_rejected);
        if (sDesisionKey.equalsIgnoreCase("01")) {
            status = getString(R.string.mtp_approved);
        }
        isClickable = false;
        UtilConstants.dialogBoxWithCallBack(getActivity(), "", getString(R.string.mtp_approval_fail, status), getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                onListScreen();
            }
        });
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        String status = getString(R.string.mtp_rejected);
        ;
        if (sDesisionKey.equalsIgnoreCase("01")) {
            status = getString(R.string.mtp_approved);
        }
        UtilConstants.dialogBoxWithCallBack(getActivity(), "", getString(R.string.mtp_approval_success, status), getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                onListScreen();
            }
        });
    }


    public void onListScreen() {
        isClickable = false;
        Intent intent = new Intent(getActivity(), MTPApprovalActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onStatus(boolean status, String values) {

    }
    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        String status = "";
        if (sDesisionKey.equalsIgnoreCase("01")) {
            status = getString(R.string.mtp_approved);
        }
        final String finalStatus = status;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UtilConstants.dialogBoxWithCallBack(getActivity(), "", getString(R.string.mtp_approval_success, finalStatus), getString(R.string.ok), "", false, new DialogCallBack() {
                    @Override
                    public void clickedStatus(boolean b) {
                        onListScreen();
                    }
                });
            }
        });

    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, String s, Bundle bundle) {
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        String status = "";
        if (sDesisionKey.equalsIgnoreCase("01")) {
            status = getString(R.string.mtp_approved);
        }
        isClickable = false;
        final String finalStatus = status;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UtilConstants.dialogBoxWithCallBack(getActivity(), "", getString(R.string.mtp_approval_success, finalStatus), getString(R.string.ok), "", false, new DialogCallBack() {
                    @Override
                    public void clickedStatus(boolean b) {
                        onListScreen();
                    }
                });
            }
        });
    }
}
