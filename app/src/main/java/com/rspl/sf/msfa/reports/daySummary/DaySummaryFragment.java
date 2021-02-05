package com.rspl.sf.msfa.reports.daySummary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.adapter.AdapterViewInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewTypeAdapter;
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.github.mikephil.charting.charts.PieChart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.ViewPagerTabAdapter;
import com.rspl.sf.msfa.asyncTask.RefreshAsyncTask;
import com.rspl.sf.msfa.attendance.CreateAttendanceActivity;
import com.rspl.sf.msfa.attendance.DayEndRemarksActivity;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.CustomDialogCallBack;
import com.rspl.sf.msfa.main.MainMenu;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.mbo.MyTargetsBean;
import com.rspl.sf.msfa.notification.NotificationSetClass;
import com.rspl.sf.msfa.registration.Configuration;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.ui.FlowLayout;
import com.rspl.sf.msfa.ui.TextProgressBar;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.impl.ODataDurationDefaultImpl;
import com.sap.smp.client.odata.impl.ODataGuidDefaultImpl;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by e10526 on 22-02-2018.*/

public class DaySummaryFragment extends Fragment implements DaySummaryViewPresenter, DaySummaryViewPresenter.DaySummResponse,
        SwipeRefreshLayout.OnRefreshListener, AdapterViewInterface<MyTargetsBean>, View.OnClickListener, UIListener {
    public static volatile String mStrSPGUID = "";
    // android components
    SwipeRefreshLayout swipeRefresh;
    RecyclerView recyclerView;
    RecyclerView recycler_view_portal_dashboard;
    TextView no_record_found, tv_no_of_outlets, tv_days_percentage, tv_order_val,
            tv_tar_sal_val, tv_ach_sal_val, tv_tar_tlsd_val, tv_ach_tlsd_val,
            tv_tar_bill_val, tv_ach_bill_val, tv_tar_eco_val, tv_ach_eco_val, tv_day_start_text, dashboard_month;
    Toolbar toolbar;
    SimpleRecyclerViewTypeAdapter<MyTargetsBean> recyclerViewAdapter = null;
    SimpleRecyclerViewTypeAdapter<DashBoardBean> recyclerViewAdapterDashBoard = null;
    LinearLayout llFlowLayout;
    // variables
    ArrayList<MyTargetsBean> mapTargetVal = new ArrayList<>();
    ArrayList<DashBoardBean> mapDasgBoardVal = new ArrayList<>();
    DaySummaryPresenterImpl presenter;
    ArrayList<MyTargetsBean> customerBeanBeenFilterArrayList;
    PieChart pieChart_sales_val, pieChart_tlsd,/*pieChart_outlets,*/
            pieChart_billcut, pieChart_eco;
    String mStrPreviousDate = "", mStrAttendanceId = "";
    String mStrOtherRetailerGuid = "";
    boolean wantToCloseDialog = false;
    String[][] delList = null;
    ODataGuid mStrVisitId = null;
    String mStrVisitEndRemarks = "";
    String mStrCalBase = "";
    private View layoutView;
    private boolean checkUnauthorized = false;
    private ImageView iv_day_start_action;
    private CardView cv_approval_view, cv_tlsd_view, cv_sales_view, cv_bill_cut_view, cv_eco_view, cv_visit;
    private TextView tv_sales_value_lbl;
    private TextView lastSync;
    private FlowLayout flowLayout;
    private ProgressDialog pdLoadDialog = null;
    private ODataPropMap oDataProperties;
    private ODataProperty oDataProperty;
    private boolean mBooleanDayStartDialog = false, mBooleanDayEndDialog = false, mBooleanDayResetDialog = false;
    private String mStrPopUpText = "";
    private LinearLayout llDaySummaryMain;
    private CardView cvMtpApprovalView;
    private TextView tvMtpApprovalCount;
    private CardView cvAttendance;
    private TextView tvSOApprovalCount;
    private boolean mBooleanEndFlag = false;
    private boolean isFirstRegistration = false;
    private boolean mBooleanStartFalg = false;
    private boolean mBooleanCompleteFlag = false;
    private ProgressBar pbMTPAplCount;
    private ProgressBar pbSOAplCount;
    private TextProgressBar pbSalesPer, pbBillCut, pbTLSDPer, pbECOPer;
    private ViewPager viewpagerDashboard;
    private TabLayout tabLayoutDashboard;
    private ViewPagerTabAdapter viewPagerAdapter;
    private DaySummaryListFragment daySummaryListFragment = null;
    private MonthSummaryListFragment monthSummaryListFragment = null;

    private ProgressBar pbAttendance = null;
    SharedPreferences sharedPreferences;
    public DaySummaryFragment() {
        /*
         * empty constructor needed
         */
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_daysummary, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        ((MainMenu) getActivity()).setActionBarTitle(getString(R.string.overview), false, false);
        layoutView = view;

        initUI(layoutView);
    }

    public void initUI(View view) {
        Log.d("DaySummary Fragment", " Entered");
        viewpagerDashboard = (ViewPager) view.findViewById(R.id.viewpagerDashboard);
        tabLayoutDashboard = (TabLayout) view.findViewById(R.id.tabLayoutDashboard);
        pbAttendance = (ProgressBar) view.findViewById(R.id.pbAttendance);
        llFlowLayout = (LinearLayout) view.findViewById(R.id.llFilterLayout);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh_targets);
        swipeRefresh.setDistanceToTriggerSync(ConstantsUtils.SWIPE_REFRESH_DISTANCE);
        tv_no_of_outlets = (TextView) view.findViewById(R.id.tv_no_of_outlets);
        dashboard_month = (TextView) view.findViewById(R.id.dashboard_month);
        tv_order_val = (TextView) view.findViewById(R.id.tv_order_val);
        tvSOApprovalCount = (TextView) view.findViewById(R.id.tv_so_approval_count);
        tvMtpApprovalCount = (TextView) view.findViewById(R.id.tv_mtp_approval_count);
        pbMTPAplCount = (ProgressBar) view.findViewById(R.id.pbMTPAplCount);
        pbSOAplCount = (ProgressBar) view.findViewById(R.id.pbSOAplCount);
        cv_visit = (CardView) view.findViewById(R.id.cv_visit);
        cv_sales_view = (CardView) view.findViewById(R.id.cv_sales_view);
        cv_tlsd_view = (CardView) view.findViewById(R.id.cv_tlsd_view);
        cv_bill_cut_view = (CardView) view.findViewById(R.id.cv_bill_cut_view);
        cv_eco_view = (CardView) view.findViewById(R.id.cv_eco_view);
        tv_sales_value_lbl = (TextView) view.findViewById(R.id.tv_sales_value_lbl);
        lastSync = (TextView) view.findViewById(R.id.lastSync);

        pbSalesPer = (TextProgressBar) view.findViewById(R.id.pbSalesPer);
        pbBillCut = (TextProgressBar) view.findViewById(R.id.pbBillCut);
        pbTLSDPer = (TextProgressBar) view.findViewById(R.id.pbTLSDPer);
        pbECOPer = (TextProgressBar) view.findViewById(R.id.pbECOPer);

        cv_approval_view = (CardView) view.findViewById(R.id.cv_approval_view);
        cv_approval_view.setOnClickListener(this);
         sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        try {
            if (sharedPreferences.getString(Constants.isSOApprovalKey, "").equalsIgnoreCase(Constants.isSOApprovalTcode)) {
                cv_approval_view.setVisibility(View.VISIBLE);
                String totalSOCount = sharedPreferences.getString(Constants.TotalSOCount, "");
                tvSOApprovalCount.setVisibility(View.VISIBLE);
                tvSOApprovalCount.setText(totalSOCount);
            } else {
                cv_approval_view.setVisibility(View.GONE);
            }

            if (sharedPreferences.getString(Constants.isMTPApprovalKey, "").equalsIgnoreCase(Constants.isMTPApprovalTcode)) {
                String totalMTPCount = sharedPreferences.getString(Constants.TotalMTPCount, "");
                tvMtpApprovalCount.setVisibility(View.VISIBLE);
                tvMtpApprovalCount.setText(totalMTPCount);
            }
            if (sharedPreferences.getBoolean("writeDBGLog", false)) {
                Constants.writeDebug = sharedPreferences.getBoolean("writeDBGLog", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        cv_visit.setOnClickListener(this);

        tv_day_start_text = (TextView) view.findViewById(R.id.tv_day_start_text);
        iv_day_start_action = (ImageView) view.findViewById(R.id.iv_day_start_action);

        tv_tar_sal_val = (TextView) view.findViewById(R.id.tv_tar_sal_val);
        tv_ach_sal_val = (TextView) view.findViewById(R.id.tv_ach_sal_val);

        tv_tar_tlsd_val = (TextView) view.findViewById(R.id.tv_tar_tlsd_val);
        tv_ach_tlsd_val = (TextView) view.findViewById(R.id.tv_ach_tlsd_val);

        tv_tar_bill_val = (TextView) view.findViewById(R.id.tv_tar_bill_val);
        tv_ach_bill_val = (TextView) view.findViewById(R.id.tv_ach_bill_val);

        tv_tar_eco_val = (TextView) view.findViewById(R.id.tv_tar_eco_val);
        tv_ach_eco_val = (TextView) view.findViewById(R.id.tv_ach_eco_val);

        tv_days_percentage = (TextView) view.findViewById(R.id.tv_days_percentage);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recycler_view_portal_dashboard = (RecyclerView) view.findViewById(R.id.recycler_view_portal_dashboard);
        no_record_found = (TextView) view.findViewById(R.id.no_record_found);
        flowLayout = (FlowLayout) view.findViewById(R.id.llFlowLayout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        pieChart_outlets = (PieChart) view.findViewById(R.id.pieChart_outlets);
        pieChart_sales_val = (PieChart) view.findViewById(R.id.pieChart_sales_val);
        pieChart_tlsd = (PieChart) view.findViewById(R.id.pieChart_tlsd);
        pieChart_billcut = (PieChart) view.findViewById(R.id.pieChart_bill_cut);
        pieChart_eco = (PieChart) view.findViewById(R.id.pieChart_eco);
        ConstantsUtils.setProgressColor(getActivity(), swipeRefresh);
        cvMtpApprovalView = (CardView) view.findViewById(R.id.cv_mtp_approval_view);
        cvAttendance = (CardView) view.findViewById(R.id.cv_attendance);
        cvMtpApprovalView.setOnClickListener(this);

        llDaySummaryMain = (LinearLayout) view.findViewById(R.id.llDaySummaryMain);
//        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.menu_day_summary),0);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mStrSPGUID = Constants.getSPGUID(Constants.SPGUID);
        displayRefreshTime(ConstantsUtils.getLastSeenDateFormat(getActivity(), ConstantsUtils.getMilliSeconds(
                ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.Targets, Constants.TimeStamp, getActivity()))));

        initializeObj();
        initializeClickListeners();
//        initializeTabLayout();
        cvAttendance.setOnClickListener(this);
        //setAttendenceUI();
        initializeRecyclerViewItems(new LinearLayoutManager(getActivity()));
        initializeRecyclerViewDashBoardItems(new LinearLayoutManager(getActivity()));

//        initializeRecyclerViewItems(new LinearLayoutManager(this));
        initializeTabLayout();
    }

    private void initializeTabLayout() {
        setupViewPager(viewpagerDashboard);
        tabLayoutDashboard.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        tabLayoutDashboard.setupWithViewPager(viewpagerDashboard);
        viewpagerDashboard.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                enableDisableSwipeRefresh(state == ViewPager.SCROLL_STATE_IDLE);
            }
        });
    }

    private void enableDisableSwipeRefresh(boolean enable) {
        if (swipeRefresh != null) {
            swipeRefresh.setEnabled(enable);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerTabAdapter(getChildFragmentManager());
        daySummaryListFragment = new DaySummaryListFragment();
        monthSummaryListFragment = new MonthSummaryListFragment();
        /*Bundle bundle = new Bundle();
        Bundle bundle1 = new Bundle();
        try {
            bundle.putSerializable(Constants.DayDashBoardList,mapTargetVal);
            bundle1.putSerializable(Constants.MonthDashBoardList,mapDasgBoardVal);
//        Bundle bundle1 = getIntent().getExtras();
            daySummaryListFragment.setArguments(bundle);
            monthSummaryListFragment.setArguments(bundle1);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
//        salesOrderHeaderListFragmentPendingSync.setArguments(bundle1);
        viewPagerAdapter.addFrag(daySummaryListFragment, getString(R.string.dashboard_day));
        viewPagerAdapter.addFrag(monthSummaryListFragment, getString(R.string.dashboard_month));
        viewpagerDashboard.setAdapter(viewPagerAdapter);
    }

    private void setAttendenceUI() {
        Constants.MapEntityVal.clear();
        mStrPreviousDate = "";
        mStrAttendanceId = "";
        mBooleanStartFalg = false;
        mBooleanEndFlag = false;
        mBooleanCompleteFlag = false;
        if (TextUtils.isEmpty(mStrSPGUID) || mStrSPGUID.equalsIgnoreCase("0")) {
            mStrSPGUID = Constants.getSPGUID(Constants.SPGUID);
        }
        String prvDayQry = Constants.Attendances + "?$filter=EndDate eq null and StartDate ne datetime'" + UtilConstants.getNewDate() + "' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
        try {
            mStrAttendanceId = OfflineManager.getAttendance(prvDayQry);
            if (!mStrAttendanceId.equalsIgnoreCase("")) {
                mStrPreviousDate = UtilConstants.getConvertCalToStirngFormat((Calendar) Constants.MapEntityVal.get(Constants.StartDate));
            } else {
                mStrPreviousDate = "";
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
//            ConstantsUtils.printErrorLog(e.getMessage());
        }

        String dayEndqry = Constants.Attendances + "?$filter=EndDate eq null and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
        try {
            mStrAttendanceId = OfflineManager.getAttendance(dayEndqry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog(e.getMessage());
        }

        String startDateStr;
        String endDateStr;
        if (Constants.MapEntityVal.isEmpty()) {

            String dayEndClosedqry = Constants.Attendances + "?$filter=EndDate eq datetime'" + UtilConstants.getNewDate() + "' and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
            try {
                mStrAttendanceId = OfflineManager.getAttendance(dayEndClosedqry);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
//                ConstantsUtils.printErrorLog(e.getMessage());
            }

            startDateStr = UtilConstants.getConvertCalToStirngFormat((Calendar) Constants.MapEntityVal.get(Constants.StartDate));
            endDateStr = UtilConstants.getConvertCalToStirngFormat((Calendar) Constants.MapEntityVal.get(Constants.EndDate));

            if (startDateStr.equalsIgnoreCase(UtilConstants.getNewDate()) && endDateStr.equalsIgnoreCase(UtilConstants.getNewDate())) {
                iv_day_start_action.setImageResource(R.drawable.stop);
                tv_day_start_text.setText(getActivity().getString(R.string.tv_complete));
                mBooleanCompleteFlag = true;
                mBooleanEndFlag = false;
                mBooleanStartFalg = true;
            } else {
                iv_day_start_action.setImageResource(R.drawable.start);
                tv_day_start_text.setText(getActivity().getString(R.string.menu_start));
            }

        } else {
            if (Constants.MapEntityVal.get(Constants.EndDate) == null) {
                iv_day_start_action.setImageResource(R.drawable.stop);
                tv_day_start_text.setText(getActivity().getString(R.string.tv_end));
                mBooleanEndFlag = true;
            } else {
                iv_day_start_action.setImageResource(R.drawable.start);
                tv_day_start_text.setText(getActivity().getString(R.string.menu_start));
            }
        }
    }

    @Override
    public void initializeClickListeners() {
        swipeRefresh.setOnRefreshListener(this);
    }

    public void initializeObj() {
        customerBeanBeenFilterArrayList = new ArrayList<>();
        presenter = new DaySummaryPresenterImpl(getActivity(), this, getActivity());
        presenter.onStart();
      //  presenter.getAttendance();
//        mapTargetVal = presenter.getTargetsFromOfflineDB();
//        setUI();
    }

    private void setUI() {
        Log.d("DaySummary Fragment", " setUI");
        // setAttendenceUI();

        if (mapTargetVal != null && mapTargetVal.size() > 0) {
            recyclerViewAdapter.refreshAdapter(mapTargetVal);
        }

        /*if (mapDasgBoardVal != null && mapDasgBoardVal.size() > 0) {
            dashboard_month.setVisibility(View.VISIBLE);
            recyclerViewAdapterDashBoard.refreshAdapter(mapDasgBoardVal);
        }else {
            dashboard_month.setVisibility(View.GONE);
        }*/
       /* if (mapTargetVal != null && mapTargetVal.size() > 0) {
            HashSet<String> mKpiNames = null;
            try {
                mKpiNames = mapTargetVal.get(mapTargetVal.size() - 1).getKpiNames();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mKpiNames != null && mKpiNames.size() > 0) {

                mStrCalBase = Constants.getName(Constants.ConfigTypsetTypeValues, Constants.TypeValue, Constants.Types,Constants.CLBASE);

                String salesKPI = Constants.KPISet + "?$filter = " + Constants.ValidTo + " ge datetime'" + UtilConstants.getNewDate() + "' and " + Constants.Periodicity + " eq '02' and " + Constants.KPICategory + " eq '02' and " + Constants.CalculationBase + " eq '"+mStrCalBase+"' ";

                try {
                    if (OfflineManager.getVisitActivityStatusForVisit(salesKPI)) {
                        cv_sales_view.setVisibility(View.VISIBLE);
                    } else {
                        cv_sales_view.setVisibility(View.GONE);
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                String tlsdKPI = Constants.KPISet + "?$filter = " + Constants.ValidTo + " ge datetime'" + UtilConstants.getNewDate() + "' and " + Constants.Periodicity + " eq '02' and " + Constants.KPICategory + " eq '07' and " + Constants.CalculationBase + " eq '04'";
                try {
                    if (OfflineManager.getVisitActivityStatusForVisit(tlsdKPI)) {
                        cv_tlsd_view.setVisibility(View.GONE);
                    } else {
                        cv_tlsd_view.setVisibility(View.GONE);
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }

                String billCutKPI = Constants.KPISet + "?$filter = " + Constants.ValidTo + " ge datetime'" + UtilConstants.getNewDate() + "' and " + Constants.Periodicity + " eq '02' and " + Constants.KPICategory + " eq '07' and " + Constants.CalculationBase + " eq '05'";
                try {
                    if (OfflineManager.getVisitActivityStatusForVisit(billCutKPI)) {
                        cv_bill_cut_view.setVisibility(View.GONE);
                    } else {
                        cv_bill_cut_view.setVisibility(View.GONE);
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }

                String ecoKPI = Constants.KPISet + "?$filter = " + Constants.ValidTo + " ge datetime'" + UtilConstants.getNewDate() + "' and " + Constants.Periodicity + " eq '02' and " + Constants.KPICategory + " eq '04' and " + Constants.CalculationBase + " eq '05'";
                try {
                    if (OfflineManager.getVisitActivityStatusForVisit(ecoKPI)) {
                        cv_eco_view.setVisibility(View.VISIBLE);
                    } else {
                        cv_eco_view.setVisibility(View.GONE);
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            } else {
                cv_sales_view.setVisibility(View.GONE);
                cv_tlsd_view.setVisibility(View.GONE);
                cv_bill_cut_view.setVisibility(View.GONE);
                cv_eco_view.setVisibility(View.GONE);
            }
            for (MyTargetsBean myTargetsBean : mapTargetVal) {
                if (myTargetsBean.getKPIName().equalsIgnoreCase("Visits")) {
                    tv_no_of_outlets.setText(myTargetsBean.getMTDA() + "/" + myTargetsBean.getMonthTarget());
//                    Constants.displayPieChart(myTargetsBean.getAchivedPercentage(),
//                            pieChart_outlets,getActivity(),6,myTargetsBean.getMTDA()+"/"+myTargetsBean.getMonthTarget());
                    tv_order_val.setText(ConstantsUtils.commaSeparator(myTargetsBean.getBTD(), myTargetsBean.getCurrency()));
                } else if (myTargetsBean.getKPIName().contains("Sales")) {
                    if(mStrCalBase.equalsIgnoreCase(Constants.str_02)){
                        tv_tar_sal_val.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myTargetsBean.getMonthTarget()));
                        tv_ach_sal_val.setText(ConstantsUtils.commaSeparator(myTargetsBean.getMTDA(), myTargetsBean.getCurrency()));
                    }else{
                        tv_tar_sal_val.setText(UtilConstants.removeLeadingZeroQuantity(myTargetsBean.getMonthTarget()));
                        tv_ach_sal_val.setText(UtilConstants.removeLeadingZeroQuantity(myTargetsBean.getMTDA()));
                    }

                    pbSalesPer.setProgress(Integer.parseInt(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage())));
                    pbSalesPer.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage()) + "%");
                    tv_sales_value_lbl.setText(myTargetsBean.getKPIName());
//                    Constants.displayPieChart(myTargetsBean.getAchivedPercentage(), pieChart_sales_val, getActivity(), 8,
//                            UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage()) + "%");
                } else if (myTargetsBean.getKPIName().contains("ECO")) {
                    tv_tar_eco_val.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getMonthTarget()));
                    tv_ach_eco_val.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getMTDA()));
                    pbECOPer.setProgress(Integer.parseInt(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage())));
                    pbECOPer.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage()) + "%");
//                    Constants.displayPieChart(myTargetsBean.getAchivedPercentage(), pieChart_eco, getActivity(), 8,
//                            UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage()) + "%");
                } else if (myTargetsBean.getKPIName().contains("Bill")) {
                    tv_tar_bill_val.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getMonthTarget()));
                    tv_ach_bill_val.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getMTDA()));
                    pbBillCut.setProgress(Integer.parseInt(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage())));
                    pbBillCut.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage()) + "%");
//                    Constants.displayPieChart(myTargetsBean.getAchivedPercentage(), pieChart_billcut, getActivity(), 8,
//                            UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage()) + "%");
                } else if (myTargetsBean.getKPIName().contains("TLSD")) {
                    tv_tar_tlsd_val.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getMonthTarget()));
                    tv_ach_tlsd_val.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getMTDA()));
                    pbTLSDPer.setProgress(Integer.parseInt(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage())));
                    pbTLSDPer.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage()) + "%");
//                    Constants.displayPieChart(myTargetsBean.getAchivedPercentage(), pieChart_tlsd, getActivity(), 8,
//                            UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage()) + "%");
                }
            }
        }*/

    }


    @Override
    public void initializeUI(Context context) {

    }


    @Override
    public void initializeObjects(Context context) {

    }

    @Override
    public void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager) {
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager _sGridLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(_sGridLayoutManager);
        recyclerViewAdapter = new SimpleRecyclerViewTypeAdapter<>(getActivity(), R.layout.recycler_targets_list_item, this, recyclerView, no_record_found);
        recyclerView.setAdapter(recyclerViewAdapter);
//        recyclerViewAdapter.refreshAdapter(mapTargetVal);
    }

    @Override
    public void initializeRecyclerViewDashBoardItems(LinearLayoutManager linearLayoutManager) {
        recycler_view_portal_dashboard.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recycler_view_portal_dashboard.setNestedScrollingEnabled(false);
        recycler_view_portal_dashboard.setLayoutManager(staggeredGridLayoutManager);
        recyclerViewAdapterDashBoard = new SimpleRecyclerViewTypeAdapter<>(getActivity(), R.layout.dash_broad_list, new AdapterViewInterface<DashBoardBean>() {


            @Override
            public int getItemViewType(int i, ArrayList arrayList) {
                return 0;
            }

            @Override
            public void onItemClick(DashBoardBean dashBoardBean, View view, int i) {

            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {

                return new DashBoardHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, DashBoardBean dashBoardBean, ArrayList<DashBoardBean> arrayList) {
                ((DashBoardHolder) viewHolder).tv_ach_dashboard_val_tittle.setText(dashBoardBean.getApplication());
//                ((DashBoardHolder)viewHolder).tv_tar_dashbord_val.setText(dashBoardBean.getTotal());

                if (dashBoardBean.getApplication().equalsIgnoreCase("RTGS Value")) {
                    ((DashBoardHolder) viewHolder).tv_dashboard_val.setText("Planned");
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
                } else {
                    ((DashBoardHolder) viewHolder).tv_dashboard_val.setText("Total");
                    ((DashBoardHolder) viewHolder).tv_actual_dashbord_val.setText(dashBoardBean.getActive());
                    ((DashBoardHolder) viewHolder).tv_tar_dashbord_val.setText(dashBoardBean.getTotal());
                }

                try {
                    double percentageActual = Double.parseDouble(dashBoardBean.getActive()) / Double.parseDouble(dashBoardBean.getTotal()) * 100;
                    ((DashBoardHolder) viewHolder).pbSalesPerActucal.setProgress(Integer.parseInt(UtilConstants.trimQtyDecimalPlace(String.valueOf(percentageActual))));
                    ((DashBoardHolder) viewHolder).pbSalesPerActucal.setText(UtilConstants.trimQtyDecimalPlace(String.valueOf(percentageActual) + "%"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }


        }, recyclerView, no_record_found);
        recycler_view_portal_dashboard.setAdapter(recyclerViewAdapterDashBoard);
    }


    @Override
    public void showMessage(String message) {
        if (message.contains("invalid authentication")) {
            if (sharedPreferences.getString(Constants.isSOApprovalKey, "").equalsIgnoreCase(Constants.isSOApprovalTcode)) {
                cv_approval_view.setVisibility(View.VISIBLE);
                String totalSOCount = sharedPreferences.getString(Constants.TotalSOCount, "");
                tvSOApprovalCount.setVisibility(View.VISIBLE);
                tvSOApprovalCount.setText(totalSOCount);
            } else {
                cv_approval_view.setVisibility(View.GONE);
            }
            if(!checkUnauthorized) {
                checkUnauthorized = true;
                try {
                    showProgressDialog();
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME,0);
                    String loginUser=sharedPreferences.getString("username","");
                    String login_pwd=sharedPreferences.getString("password","");
                    UtilConstants.getPasswordStatus(Configuration.IDPURL, loginUser, login_pwd, Configuration.APP_ID, new UtilConstants.PasswordStatusCallback() {
                        @Override
                        public void passwordStatus(final JSONObject jsonObject) {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideProgressDialog();
                                    Constants.passwordStatusErrorMessage(getActivity(), jsonObject,loginUser);
                                }
                            });

                        }
                    });
                } catch (Throwable e) {
                    checkUnauthorized = false;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressDialog();
                        }
                    });
                    e.printStackTrace();
                }



//                Constants.customAlertDialogWithScroll(getContext(), message);
            }
//            Constants.customAlertDialogWithScroll(getContext(), message);
        } else if(message.contains("HTTP Status 401 ? Unauthorized")){

            if (sharedPreferences.getString(Constants.isSOApprovalKey, "").equalsIgnoreCase(Constants.isSOApprovalTcode)) {
                cv_approval_view.setVisibility(View.VISIBLE);
                String totalSOCount = sharedPreferences.getString(Constants.TotalSOCount, "");
                tvSOApprovalCount.setVisibility(View.VISIBLE);
                tvSOApprovalCount.setText(totalSOCount);
            } else {
                cv_approval_view.setVisibility(View.GONE);
            }
            if(!checkUnauthorized) {
                checkUnauthorized = true;
                try {
                    showProgressDialog();
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME,0);
                    String loginUser=sharedPreferences.getString("username","");
                    String login_pwd=sharedPreferences.getString("password","");
                    UtilConstants.getPasswordStatus(Configuration.IDPURL, loginUser, login_pwd, Configuration.APP_ID, new UtilConstants.PasswordStatusCallback() {
                        @Override
                        public void passwordStatus(final JSONObject jsonObject) {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideProgressDialog();
                                    Constants.passwordStatusErrorMessage(getActivity(), jsonObject,loginUser);
                                }
                            });

                        }
                    });
                } catch (Throwable e) {
                    checkUnauthorized = false;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressDialog();
                        }
                    });
                    e.printStackTrace();
                }



//                Constants.customAlertDialogWithScroll(getContext(), message);
            }
        }

        else {
            ConstantsUtils.displayLongToast(getContext(), message);
        }
    }

    @Override
    public void dialogMessage(String message, String msgType) {

    }

    /**
     * Displaying Last Refresh time and setting to Toolbar
     */
    @Override
    public void displayRefreshTime(String refreshTime) {
        try {
            String lastRefresh = "";
            if (!TextUtils.isEmpty(refreshTime)) {
                lastRefresh = getString(R.string.po_last_refreshed) + " " + refreshTime;
            }


//            if (lastRefresh!=null)
//                getSupportActionBar().setSubtitle(lastRefresh);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    public void lastSyncRefreshTime(String refreshTime) {
        try {
            String lastRefresh = "";
            if (!TextUtils.isEmpty(refreshTime)) {
                LogManager.writeLogDebug("Dashboard refresh sync time :" + refreshTime);
                lastRefresh = getString(R.string.po_last_refreshed) + " " + refreshTime;
            }

            lastSync.setText(lastRefresh);
//            if (lastRefresh!=null)
//                getSupportActionBar().setSubtitle(lastRefresh);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void success(ArrayList success) {
        this.mapTargetVal = success;
        recyclerViewAdapter.refreshAdapter(success);
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void error(String message) {
        swipeRefresh.setRefreshing(false);

    }


    @Override
    public void showProgressDialog() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideProgressDialog() {
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void searchResult(ArrayList<MyTargetsBean> customerBeanArrayList) {
        recyclerViewAdapter.refreshAdapter(customerBeanArrayList);
    }

    @Override
    public void onRefresh() {
        try {
            checkUnauthorized = false;
            Log.d(getActivity().getClass().getSimpleName(), "onRefresh");
            presenter.onRefresh();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
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

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkUnauthorized = false;
    }

    @Override
    public void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus) {
//        Intent intent = new Intent(getActivity(), BehaviourFilterActivity.class);
//        intent.putExtra(DateFilterFragment.EXTRA_DEFAULT, filterType);
//        intent.putExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_STATUS, status);
//        intent.putExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_STATUS_NAME, delvStatus);
//        startActivityForResult(intent, ConstantsUtils.ACTIVITY_RESULT_FILTER);
    }

    @Override
    public void TargetSync() {
        presenter.onStart();
//        mapTargetVal =presenter.getTargetsFromOfflineDB();
//        displayRefreshTime(ConstantsUtils.getLastSeenDateFormat(getActivity(), ConstantsUtils.getMilliSeconds(
//                ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.Targets, Constants.TimeStamp, getActivity()))));
    }

    @Override
    public void displayList(ArrayList<MyTargetsBean> alTargets) {
        Log.d("DaySummary Fragment", " displayList");
        llDaySummaryMain.setVisibility(View.VISIBLE);
        mapTargetVal.clear();
//        mapDasgBoardVal.clear();
//        mapDasgBoardVal.clear();
        mapTargetVal.addAll(alTargets);
//        mapDasgBoardVal.addAll(alDashBoard);
        /*try {
            mapDasgBoardVal.addAll(boardBeans);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        if (sharedPreferences.getString(Constants.isSOApprovalKey, "").equalsIgnoreCase(Constants.isSOApprovalTcode)) {
            cv_approval_view.setVisibility(View.VISIBLE);
        } else {
            cv_approval_view.setVisibility(View.GONE);
        }

        if (sharedPreferences.getString(Constants.isMTPApprovalKey, "").equalsIgnoreCase(Constants.isMTPApprovalTcode)) {
            cvMtpApprovalView.setVisibility(View.VISIBLE);
           /* String totalMTPCount = sharedPreferences.getString(Constants.TotalMTPCount,"");
            tvMtpApprovalCount.setVisibility(View.VISIBLE);
            tvMtpApprovalCount.setText(totalMTPCount);*/
        } else {
            cvMtpApprovalView.setVisibility(View.GONE);
        }

        if (sharedPreferences.getString(Constants.isStartCloseEnabled, "").equalsIgnoreCase(Constants.isStartCloseTcode)) {
            cvAttendance.setVisibility(View.VISIBLE);
        } else {
            cvAttendance.setVisibility(View.GONE);
        }

        lastSyncRefreshTime(ConstantsUtils.getLastSeenDateFormat(getActivity(), ConstantsUtils.getMilliSeconds(
                ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.Targets, Constants.TimeStamp, getActivity()))));
//        lastSyncRefreshTime(ConstantsUtils.getLastSeenDateFormat(getContext(), ConstantsUtils.getCurrentTimeLong()));
        setUI();
        refreshViewPager();


    }

    private void refreshViewPager() {
//        viewpagerDashboard.setAdapter(viewPagerAdapter);
        if (daySummaryListFragment != null) {
            daySummaryListFragment.onRefresh(mapTargetVal);
        }

    }

    @Override
    public void displayDashBoardList(ArrayList<DashBoardBean> boardBeans) {
        mapDasgBoardVal.clear();
//        mapDasgBoardVal.addAll(boardBeans);
        ArrayList<DashBoardBean> duplicateList = new ArrayList<>();
        try {
            Gson gson = new Gson();
            SharedPreferences sharedPreferences1 = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
            String response = sharedPreferences1.getString(Constants.SharMonthDashBoardList, "");
            duplicateList = gson.fromJson(response, new TypeToken<List<DashBoardBean>>() {
            }.getType());
//            mapDasgBoardVal.addAll(duplicateList);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        mapDasgBoardVal.addAll(duplicateList);
       /* if (mapDasgBoardVal != null && mapDasgBoardVal.size() > 0) {
            dashboard_month.setVisibility(View.VISIBLE);
            recyclerViewAdapterDashBoard.refreshAdapter(mapDasgBoardVal);
        }else {
            dashboard_month.setVisibility(View.GONE);
        }*/
        if (monthSummaryListFragment != null) {
            if (duplicateList != null)
                monthSummaryListFragment.onRefresh(duplicateList);
//            monthSummaryListFragment.onRefresh(mapDasgBoardVal);
        }
    }

    @Override
    public void showDashMonthProgress() {
        if (daySummaryListFragment != null) {
            daySummaryListFragment.showProgress();
        }
    }

    @Override
    public void showDashDayProgress() {
        if (monthSummaryListFragment != null) {
            monthSummaryListFragment.showProgress();
        }
    }

    @Override
    public void displayDashBoardError(String error) {
        mapDasgBoardVal.clear();
        showMessage(error);
        LogManager.writeLogDebug("Dashboard refresh error: " + error);

        if (monthSummaryListFragment != null) {
//            monthSummaryListFragment.onRefresh(mapDasgBoardVal);
        }
    }

    @Override
    public void displayAttendanceview() {
        Log.d("DaySummary Fragment", " displayAttendanceview true");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setAttendenceUI();
                hideAttendancePB();
                // Stuff that updates the UI
            }
        });


    }

    @Override
    public void showMTPProgress() {
        tvMtpApprovalCount.setVisibility(View.GONE);
        pbMTPAplCount.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSOProgress() {
        tvSOApprovalCount.setVisibility(View.GONE);
        pbSOAplCount.setVisibility(View.VISIBLE);
    }

    @Override
    public void showAttendancePB() {
        iv_day_start_action.setVisibility(View.GONE);
        pbAttendance.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    public void hideMTPProgress() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvMtpApprovalCount.setVisibility(View.VISIBLE);
                pbMTPAplCount.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void hideSOProgress() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvSOApprovalCount.setVisibility(View.VISIBLE);
                pbSOAplCount.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void hideAttendancePB() {
        pbAttendance.setVisibility(ProgressBar.GONE);
        iv_day_start_action.setVisibility(View.VISIBLE);
    }

    @Override
    public void disPlayMTPCount(String count) {
        tvMtpApprovalCount.setText(count);
    }

    @Override
    public void disPlaySOCount(String count) {
        tvSOApprovalCount.setText(count);
    }

    @Override
    public void refreshTotalOrderVale() {
        recyclerViewAdapter.refreshAdapter(mapTargetVal);
    }


    private void refreshAdapter(ArrayList<MyTargetsBean> alTargets) {
        recyclerViewAdapter.refreshAdapter(alTargets);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* if (resultCode == ConstantsUtils.ACTIVITY_RESULT_FILTER) {
            String filterType = data.getStringExtra(DateFilterFragment.EXTRA_DEFAULT);
            String soStatus = data.getStringExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_STATUS);
            String statusName = data.getStringExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_STATUS_NAME);
            String delvStatus = data.getStringExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_DELV_STATUS);
            String delvStatusName = data.getStringExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_DELV_STATUS_NAME);
            customerBeanBeenFilterArrayList.clear();
            if (!statusName.equalsIgnoreCase(Constants.ALL)) {
                for (int i = 0; i < mapTargetVal.size(); i++) {
                    if (mapTargetVal.get(i).getKPIName().equalsIgnoreCase(soStatus)){
                        MyTargetsBean customerBean = mapTargetVal.get(i);
                        customerBeanBeenFilterArrayList.add(customerBean);
                    }
                }
                llFlowLayout.setVisibility(View.VISIBLE);
                recyclerViewAdapter.refreshAdapter(customerBeanBeenFilterArrayList);
            } else {
                llFlowLayout.setVisibility(View.GONE);
                recyclerViewAdapter.refreshAdapter(mapTargetVal);
            }
            presenter.startFilter(requestCode, resultCode, data);
        }else */
        if (requestCode == LocationUtils.REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                onDayStartOrEnd();
            }
        } else if (requestCode == ConstantsUtils.SO_RESULT_CODE) {
            presenter = new DaySummaryPresenterImpl(getActivity(), this, getActivity());
            presenter.onStart();
        } else if (requestCode == MainMenu.ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            presenter = new DaySummaryPresenterImpl(getActivity(), this, getActivity());
            presenter.onStart();
        }
    }

    @Override
    public void setFilterDate(String filterType) {
        try {
            String[] filterTypeArr = filterType.split(", ");
            ConstantsUtils.displayFilter(filterTypeArr, flowLayout, getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cv_attendance:
                onDaystart();
                break;
            case R.id.cv_mtp_approval_view:
                ConstantsUtils.onMTPApprovals(getActivity());
                break;
            case R.id.cv_visit:
                onNavigateMTP();
                break;
            case R.id.cv_approval_view:
                onSOApproval();
                break;
        }
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

    private void onSOApproval() {
        ConstantsUtils.onSoApproval(getActivity());
    }

    private void onMTP() {
        ConstantsUtils.onMTPActivity(getActivity());
    }

    private void onDaystart() {
        Log.d("DaySummary Fragment", " onDaystart");
        setAttendenceUI();
        pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.checking_pemission));

        LocationUtils.checkLocationPermission(getActivity(), new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                closeProgressDialog();
                if (status) {
                    if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
                        onDayStartOrEnd();
                    } else {
                        ConstantsUtils.showAutoDateSetDialog(getActivity());
                    }
                }
            }
        });
    }

    private void closeProgressDialog() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void onDayStartOrEnd() {
        if (mBooleanEndFlag) {
            if (mStrPreviousDate.equalsIgnoreCase("")) {
                getNonVisitedDealers(UtilConstants.getNewDate());
            } else {
                getNonVisitedDealers(mStrPreviousDate);
            }
        } else {
            attendanceFunctionality(iv_day_start_action, tv_day_start_text);
        }
    }

    private void getNonVisitedDealers(String strDate) {
        try {
            new GetNonVistedRetailers().execute(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[][] checkNonVisitedRetailers(String selctedDate) {
        String retList[][] = null;
        try {

            String routeQry = Constants.RoutePlans + "?$filter=" + Constants.VisitDate + " eq datetime'" + selctedDate + "'";

            String mGetRouteQry = OfflineManager.getRouteQry(routeQry);

            if (!mGetRouteQry.equalsIgnoreCase("")) {
                mGetRouteQry = Constants.RouteSchedulePlans + "?$filter=" + mGetRouteQry;
                retList = OfflineManager.getNotVisitedRetailerList(mGetRouteQry, UtilConstants.getNewDate());
            } else {
                retList = null;
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog(e.getMessage());
        }
        return retList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case UtilConstants.Location_PERMISSION_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationUtils.checkLocationPermission(getActivity(), new LocationInterface() {
                        @Override
                        public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                            if (status) {
                                onDayStartOrEnd();
                            }
                        }
                    });
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }


        }
        // other 'case' lines to check for other
        // permissions this app might request
    }

    private void attendanceFunctionality(final ImageView ivIcon, final TextView tvIconName) {
        if (mBooleanEndFlag) {
            String message;
            if (mStrPreviousDate.equalsIgnoreCase("")) {
                //For Today
                mStrOtherRetailerGuid = "";
                String otherRetVisitQuery = Constants.Visits + "?$filter=EndDate eq null " +
                        "and StartDate eq datetime'" + UtilConstants.getNewDate() + "'and " + Constants.StatusID + " eq '01' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";

                String[] otherRetDetails = new String[2];
                try {
                    otherRetDetails = OfflineManager.checkVisitForOtherRetailer(otherRetVisitQuery);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
//                    ConstantsUtils.printErrorLog(e.getMessage());
                }
                final String[] finalOtherRetDetails = otherRetDetails;

                mStrOtherRetailerGuid = finalOtherRetDetails[1];
                if (mStrOtherRetailerGuid != null && !mStrOtherRetailerGuid.equalsIgnoreCase("")) {
                                         /*
                                         ToDo display alert dialog for visit started but not ended retailer
                                          */
                    AlertDialog.Builder alertDialogVisitEnd = new AlertDialog.Builder(
                            getActivity(), R.style.MyTheme);

                    alertDialogVisitEnd.setMessage(getString(R.string.visit_end_not_marked_for_specific_retailer, otherRetDetails[0]))
                            .setCancelable(false)
                            .setPositiveButton(
                                    R.string.yes,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int id) {
                                            dialog.cancel();

                                            pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.gps_progress));
                                            Constants.getLocation(getActivity(), new LocationInterface() {
                                                @Override
                                                public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                    closeProgressDialog();
                                                    if (status) {
                                                        if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
                                                            boolean isVisitActivities = false;
                                                            try {
                                                                isVisitActivities = OfflineManager.checkVisitActivitiesForRetailer(Constants.VisitActivities + "?$filter=" + Constants.VISITKEY + " eq guid'" + mStrOtherRetailerGuid + "'");
                                                            } catch (OfflineODataStoreException e) {
                                                                e.printStackTrace();
                                                            }
                                                            mStrVisitId = ODataGuidDefaultImpl.initWithString36(mStrOtherRetailerGuid);
                                                            if (isVisitActivities) {
                                                                onSaveVisitClose();
                                                            } else {
                                                                wantToCloseDialog = false;
                                                                onAlertDialogForVisitDayEndRemarks();
                                                            }
                                                        } else {
                                                            ConstantsUtils.showAutoDateSetDialog(getActivity());
                                                        }
                                                    }
                                                }
                                            });


                                        }
                                    });
                    alertDialogVisitEnd.setNegativeButton(R.string.no,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }

                            });

                    alertDialogVisitEnd.show();
                } else {
//                    delList = checkNonVisitedRetailers(UtilConstants.getNewDate());

                    String alrtConfMsg = "", alrtNegtiveMsg = "";

                    if (delList == null) {


                        message = getString(R.string.msg_confirm_day_end);
                        alrtConfMsg = getString(R.string.yes);
                        alrtNegtiveMsg = getString(R.string.no);

                    } else {
                        message = getString(R.string.msg_confirm_day_end);
                        alrtConfMsg = getString(R.string.ok);
                        alrtNegtiveMsg = getString(R.string.cancel);
                    }

                                         /*
                                           ToDo display alert dialog for Day end or non visited retailers
                                         */

                    AlertDialog.Builder alertDialogDayEnd = new AlertDialog.Builder(
                            getActivity(), R.style.MyTheme);
                    alertDialogDayEnd.setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton(
                                    alrtConfMsg,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int id) {
                                            dialog.cancel();
                                            if (delList == null) {
                                                pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.gps_progress));
                                                Constants.getLocation(getActivity(), new LocationInterface() {
                                                    @Override
                                                    public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                        closeProgressDialog();
                                                        if (status) {
                                                            if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
                                                                mBooleanEndFlag = false;
                                                                tvIconName
                                                                        .setText(R.string.tv_complete);
                                                                mBooleanStartFalg = true;
                                                                mBooleanCompleteFlag = true;
                                                                mStrPopUpText = getString(R.string.msg_update_end);
                                                                mBooleanDayStartDialog = false;
                                                                mBooleanDayEndDialog = true;
                                                                mBooleanDayResetDialog = false;
                                                                onSaveClose();
                                                            } else {
                                                                ConstantsUtils.showAutoDateSetDialog(getActivity());
                                                            }
                                                        }
                                                    }
                                                });
                                            } else {
                                                if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
                                                    Intent intentNavEndRemarksScreen = new Intent(getActivity(), DayEndRemarksActivity.class);
                                                    intentNavEndRemarksScreen.putExtra(Constants.ClosingeDayType, Constants.Today);
                                                    intentNavEndRemarksScreen.putExtra(Constants.ClosingeDay, UtilConstants.getNewDate());
                                                    startActivity(intentNavEndRemarksScreen);
                                                } else {
                                                    ConstantsUtils.showAutoDateSetDialog(getActivity());
                                                }
                                            }
                                        }

                                    })
                            .setNegativeButton(alrtNegtiveMsg,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int id) {
                                            dialog.cancel();
                                        }

                                    });
                    alertDialogDayEnd.show();
                }

            } else {
                message = getString(R.string.msg_previous_day_end);

                /*
                 *ToDo display alert dialog for previous day is not ended.
                 */
                AlertDialog.Builder alertDialogPreviousDay = new AlertDialog.Builder(getActivity(), R.style.MyTheme);
                alertDialogPreviousDay.setMessage(
                        message)
                        .setCancelable(false)
                        .setPositiveButton(
                                getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();

                                        mStrOtherRetailerGuid = "";

                                        String otherRetVisitQuery = Constants.Visits + "?$filter=EndDate eq null " +
                                                "and StartDate eq datetime'" + mStrPreviousDate + "'and " + Constants.StatusID + " eq '01' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";

                                        String[] otherRetDetails = new String[2];
                                        try {
                                            otherRetDetails = OfflineManager.checkVisitForOtherRetailer(otherRetVisitQuery);
                                        } catch (OfflineODataStoreException e) {
                                            e.printStackTrace();
//                                            ConstantsUtils.printErrorLog(e.getMessage());
                                        }
                                        final String[] finalOtherRetDetails = otherRetDetails;

                                        mStrOtherRetailerGuid = finalOtherRetDetails[1];
                                        if (mStrOtherRetailerGuid != null && !mStrOtherRetailerGuid.equalsIgnoreCase("")) {
                                            /*
                                             *ToDo display alert dialog for visit started but not ended retailer
                                             */
                                            AlertDialog.Builder alertDialogVisitEnd = new AlertDialog.Builder(
                                                    getActivity(), R.style.MyTheme);

                                            alertDialogVisitEnd.setMessage(getString(R.string.visit_end_not_marked_for_specific_retailer, otherRetDetails[0]))
                                                    .setCancelable(false)
                                                    .setPositiveButton(
                                                            R.string.yes,
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(
                                                                        DialogInterface dialog,
                                                                        int id) {
                                                                    dialog.cancel();
                                                                    pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.gps_progress));
                                                                    Constants.getLocation(getActivity(), new LocationInterface() {
                                                                        @Override
                                                                        public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                                            closeProgressDialog();
                                                                            if (status) {
                                                                                if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
                                                                                    boolean isVisitActivities = false;
                                                                                    try {
                                                                                        isVisitActivities = OfflineManager.checkVisitActivitiesForRetailer(Constants.VisitActivities + "?$filter=" + Constants.VISITKEY + " eq guid'" + mStrOtherRetailerGuid + "'");
                                                                                    } catch (OfflineODataStoreException e) {
                                                                                        e.printStackTrace();
                                                                                    }
                                                                                    mStrVisitId = ODataGuidDefaultImpl.initWithString36(mStrOtherRetailerGuid);
                                                                                    if (isVisitActivities) {
                                                                                        onSaveVisitClose();
                                                                                    } else {
                                                                                        wantToCloseDialog = false;
                                                                                        onAlertDialogForVisitDayEndRemarks();
                                                                                    }
                                                                                } else {
                                                                                    ConstantsUtils.showAutoDateSetDialog(getActivity());
                                                                                }
                                                                            }
                                                                        }
                                                                    });


                                                                }
                                                            });
                                            alertDialogVisitEnd.setNegativeButton(R.string.no,
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog,
                                                                            int id) {
                                                            dialog.cancel();
                                                        }

                                                    });

                                            alertDialogVisitEnd.show();
                                        } else {
                                            String msg = "";
//                                            delList = checkNonVisitedRetailers(mStrPreviousDate);

                                            String alrtConfMsg = "", alrtNegtiveMsg = "";

                                            if (delList == null) {
                                                msg = getString(R.string.msg_confirm_day_end);
                                                alrtConfMsg = getString(R.string.yes);
                                                alrtNegtiveMsg = getString(R.string.no);
                                            } else {
                                                msg = getString(R.string.msg_remarks_pending_visit);
                                                alrtConfMsg = getString(R.string.ok);
                                                alrtNegtiveMsg = getString(R.string.cancel);
                                            }

                                                                     /*
                                                                     ToDo display alert dialog for Day end  and non visited retailers
                                                                       */
                                            AlertDialog.Builder alertDialogDayEnd = new AlertDialog.Builder(
                                                    getActivity(), R.style.MyTheme);
                                            alertDialogDayEnd.setMessage(msg)
                                                    .setCancelable(false)
                                                    .setPositiveButton(
                                                            alrtConfMsg,
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(
                                                                        DialogInterface dialog,
                                                                        int id) {
                                                                    dialog.cancel();
                                                                    if (delList == null) {
                                                                        pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.gps_progress));
                                                                        Constants.getLocation(getActivity(), new LocationInterface() {
                                                                            @Override
                                                                            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                                                closeProgressDialog();
                                                                                if (status) {
                                                                                    if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
                                                                                        mStrPopUpText = getString(R.string.msg_update_previous_day_end);
                                                                                        mBooleanDayStartDialog = false;
                                                                                        mBooleanDayEndDialog = true;
                                                                                        mBooleanDayResetDialog = false;

                                                                                        onSaveClose();
                                                                                        mBooleanEndFlag = false;
                                                                                        tvIconName
                                                                                                .setText(R.string.tv_start);
                                                                                        mBooleanStartFalg = false;
                                                                                        mBooleanCompleteFlag = false;
                                                                                        ivIcon.setImageResource(R.drawable.stop);
                                                                                    } else {
                                                                                        ConstantsUtils.showAutoDateSetDialog(getActivity());
                                                                                    }
                                                                                }
                                                                            }
                                                                        });

                                                                    } else {
                                                                        if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
                                                                            Intent intentNavEndRemarksScreen = new Intent(getActivity(), DayEndRemarksActivity.class);
                                                                            intentNavEndRemarksScreen.putExtra(Constants.ClosingeDayType, Constants.PreviousDay);
                                                                            intentNavEndRemarksScreen.putExtra(Constants.ClosingeDay, mStrPreviousDate);
                                                                            startActivity(intentNavEndRemarksScreen);
                                                                        } else {
                                                                            ConstantsUtils.showAutoDateSetDialog(getActivity());
                                                                        }
                                                                    }
                                                                }

                                                            })
                                                    .setNegativeButton(alrtNegtiveMsg,
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(
                                                                        DialogInterface dialog,
                                                                        int id) {
                                                                    dialog.cancel();
                                                                }

                                                            });
                                            alertDialogDayEnd.show();
                                        }
                                    }

                                })
                        .setNegativeButton(
                                getString(R.string.no),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();
                                    }

                                });
                alertDialogPreviousDay.show();
            }


        } else {

            if (!mBooleanStartFalg) {

                Intent intentNavPrevScreen = new Intent(getActivity(), CreateAttendanceActivity.class);
                startActivity(intentNavPrevScreen);

            }

            if (mBooleanCompleteFlag) {

                                    /*
                                    ToDo display alert dialog for Day end reset
                                     */
                AlertDialog.Builder alertDialogDayEndReset = new AlertDialog.Builder(getActivity(), R.style.MyTheme);
                alertDialogDayEndReset.setMessage(
                        getString(R.string.msg_reset_day_end))
                        .setCancelable(false)
                        .setPositiveButton(
                                //commenting
                                getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();
                                        pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.gps_progress));
                                        Constants.getLocation(getActivity(), new LocationInterface() {
                                            @Override
                                            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                closeProgressDialog();
                                                if (status) {
                                                    if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
                                                        ivIcon.setImageResource(R.drawable.stop);
                                                        tvIconName
                                                                .setText(R.string.tv_end);
                                                        mBooleanEndFlag = true;
                                                        mBooleanCompleteFlag = false;
                                                        mBooleanStartFalg = true;


                                                        mBooleanDayStartDialog = false;
                                                        mBooleanDayEndDialog = false;
                                                        mBooleanDayResetDialog = true;
                                                        onCloseUpdate();
                                                    } else {
                                                        ConstantsUtils.showAutoDateSetDialog(getActivity());
                                                    }
                                                }
                                            }
                                        });
                                    }
                                })
                        .setNegativeButton(
                                getString(R.string.no),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();
                                    }

                                });
                alertDialogDayEndReset.show();

            }

        }
    }

    /*Ends day*/
    private void onSaveClose() {
        try {
            new ClosingDate().execute();
        } catch (Exception e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog(e.getMessage());
        }

    }

    /*resets day*/
    private void onCloseUpdate() {
        mStrPopUpText = getString(R.string.msg_resetting_day_end);
        try {
            new ResettingDate().execute();
        } catch (Exception e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog(e.getMessage());
        }

    }

    private void onSaveVisitClose() {
        mStrPopUpText = getString(R.string.marking_visit_end_plz_wait);
        try {
            new ClosingVisit().execute();
        } catch (Exception e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog(e.getMessage());
        }
    }

    private void onAlertDialogForVisitDayEndRemarks() {

        ConstantsUtils.showVisitRemarksDialog(getActivity(), new CustomDialogCallBack() {
            @Override
            public void cancelDialogCallBack(boolean userClicked, String ids, String description) {
                mStrVisitEndRemarks = description;
                if (userClicked) {
                    if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
                        wantToCloseDialog = false;
                        onSaveVisitClose();
                    } else {
                        ConstantsUtils.showAutoDateSetDialog(getActivity());
                    }
                } else {

                }
            }
        }, getString(R.string.alert_plz_enter_remarks));

        /*










        AlertDialog.Builder alertDialogVisitEndRemarks = new AlertDialog.Builder(getActivity(), R.style.MyTheme);
        alertDialogVisitEndRemarks.setMessage(R.string.alert_plz_enter_remarks);
        alertDialogVisitEndRemarks.setCancelable(false);
        int MAX_LENGTH = 255;

        final EditText etVisitEndRemarks = new EditText(getActivity());

        if (wantToCloseDialog) {
            etVisitEndRemarks.setBackgroundResource(R.drawable.edittext_border);

        } else {
            etVisitEndRemarks.setBackgroundResource(R.drawable.edittext);
        }

        etVisitEndRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (wantToCloseDialog) {
                    etVisitEndRemarks.setBackgroundResource(R.drawable.edittext_border);
                    wantToCloseDialog = false;
                } else {
                    etVisitEndRemarks.setBackgroundResource(R.drawable.edittext);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(MAX_LENGTH);
        etVisitEndRemarks.setFilters(FilterArray);

        etVisitEndRemarks.setText(mStrVisitEndRemarks.equalsIgnoreCase("") ? mStrVisitEndRemarks : "");
        etVisitEndRemarks.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        etVisitEndRemarks.setLayoutParams(lp);
        alertDialogVisitEndRemarks.setView(etVisitEndRemarks);
        alertDialogVisitEndRemarks.setPositiveButton(R.string.save,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mStrVisitEndRemarks = etVisitEndRemarks.getText().toString();
                        if (mStrVisitEndRemarks.equalsIgnoreCase("")) {
                            wantToCloseDialog = true;
                            onAlertDialogForVisitDayEndRemarks();
                        } else {
                            wantToCloseDialog = false;
                            onSaveVisitClose();
                        }
                    }
                });

        alertDialogVisitEndRemarks.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        mStrVisitEndRemarks = etVisitEndRemarks.getText().toString();
                    }
                });
        AlertDialog alertDialog = alertDialogVisitEndRemarks.create();
        alertDialog.show();*/

    }

    @Override
    public void onRequestError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e, getContext());
        if (errorBean.hasNoError()) {
            Toast.makeText(getActivity(), getString(R.string.err_odata_unexpected, e.getMessage()),
                    Toast.LENGTH_LONG).show();

            if (mBooleanDayStartDialog)
                mStrPopUpText = getString(R.string.msg_start_upd_sync_error);
            else if (mBooleanDayEndDialog)
                mStrPopUpText = getString(R.string.msg_end_upd_sync_error);
            else if (mBooleanDayResetDialog) {
                mStrPopUpText = getString(R.string.msg_reset_upd_sync_error);
            }
            if (mStrPopUpText.equalsIgnoreCase("")) {
                try {
                    mStrPopUpText = errorBean.getErrorMsg();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            if (operation == Operation.Create.getValue()) {
                closeProgressDialog();
                displayPopUpMsg();
            } else if (operation == Operation.Update.getValue()) {
                try {
                    pdLoadDialog.dismiss();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                displayPopUpMsg();
            } else if (operation == Operation.OfflineFlush.getValue()) {
                closeProgressDialog();

                displayPopUpMsg();
            } else if (operation == Operation.OfflineRefresh.getValue()) {
                closeProgressDialog();

                displayPopUpMsg();
            } else if (operation == Operation.GetStoreOpen.getValue()) {
                try {

                    closeProgressDialog();
                    UtilConstants.showAlert(getString(R.string.msg_offline_store_failure), getActivity());
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        } else {
            closeProgressDialog();
            Constants.displayMsgReqError(errorBean.getErrorCode(), getActivity());
        }
    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.Create.getValue()) {
            if (Constants.getSyncType(getActivity(), Constants.Attendances, Constants.CreateOperation).equalsIgnoreCase("4")) {
                closeProgressDialog();

                if (mBooleanDayStartDialog)
                    mStrPopUpText = getString(R.string.dialog_day_started);
                else if (mBooleanDayEndDialog)
                    mStrPopUpText = getString(R.string.dialog_day_ended);
                else if (mBooleanDayResetDialog)
                    mStrPopUpText = getString(R.string.dialog_day_reset);
                else if (!mStrOtherRetailerGuid.equalsIgnoreCase(""))
                    mStrPopUpText = getString(R.string.visit_ended);

                displayPopUpMsg();
            } else {
                if (!UtilConstants.isNetworkAvailable(getActivity())) {
                    closeProgressDialog();
                    UtilConstants.onNoNetwork(getActivity());
                } else {
                    OfflineManager.flushQueuedRequests(DaySummaryFragment.this);
                }
            }
        } else if (operation == Operation.Update.getValue()) {
            if (Constants.getSyncType(getActivity(), Constants.Attendances, Constants.UpdateOperation).equalsIgnoreCase("4")) {

                closeProgressDialog();
                if (mBooleanDayStartDialog)
                    mStrPopUpText = getString(R.string.dialog_day_started);
                else if (mBooleanDayEndDialog)
                    mStrPopUpText = getString(R.string.dialog_day_ended);
                else if (mBooleanDayResetDialog)
                    mStrPopUpText = getString(R.string.dialog_day_reset);
                try {
                    if (mStrOtherRetailerGuid != null && !TextUtils.isEmpty(mStrOtherRetailerGuid))
                        mStrPopUpText = getString(R.string.visit_ended);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                displayPopUpMsg();
            } else {
                if (!UtilConstants.isNetworkAvailable(getActivity())) {
                    closeProgressDialog();
                    UtilConstants.onNoNetwork(getActivity());
                } else {
                    OfflineManager.flushQueuedRequests(DaySummaryFragment.this);
                }
            }

        } else if (operation == Operation.OfflineFlush.getValue()) {

            if (Constants.getSyncType(getActivity(), Constants.Attendances, Constants.ReadOperation).equalsIgnoreCase("4")) {
                closeProgressDialog();

                if (mBooleanDayStartDialog)
                    mStrPopUpText = getString(R.string.dialog_day_started);
                else if (mBooleanDayEndDialog)
                    mStrPopUpText = getString(R.string.dialog_day_ended);
                else if (mBooleanDayResetDialog)
                    mStrPopUpText = getString(R.string.dialog_day_reset);
                else if (!mStrOtherRetailerGuid.equalsIgnoreCase(""))
                    mStrPopUpText = getString(R.string.visit_ended);

                displayPopUpMsg();
            } else {
                if (!UtilConstants.isNetworkAvailable(getActivity())) {
                    closeProgressDialog();
                    UtilConstants.onNoNetwork(getActivity());
                } else {

                    String allCollection = "";
//                    if (mBooleanDayStartDialog) {
//                        allCollection = Constants.Attendances + "," + Constants.SPStockItems + "," + Constants.SPStockItemDetails + "," + Constants.SPStockItemSNos + "," + Constants.SFINVOICES + "," + Constants.SSInvoiceItemDetails
//                                + "," + Constants.SSInvoiceItemSerials + "," + Constants.FinancialPostings
//                                + "," + Constants.FinancialPostingItemDetails
//                                + "," + Constants.CPStockItems + "," + Constants.CPStockItemDetails + "," + Constants.CPStockItemSnos + "," + Constants.Schemes + "," + Constants.Tariffs + "," + Constants.SegmentedMaterials;
//                    } else {
                    allCollection = Constants.Attendances;
//                    }

                    new RefreshAsyncTask(getActivity(), allCollection, this).execute();
//                    OfflineManager.refreshRequests(getActivity(), allCollection, DaySummaryFragment.this);
                }
            }


        } else if (operation == Operation.OfflineRefresh.getValue()) {
            closeProgressDialog();

            if (mBooleanDayStartDialog)
                mStrPopUpText = getString(R.string.dialog_day_started);
            else if (mBooleanDayEndDialog)
                mStrPopUpText = getString(R.string.dialog_day_ended);
            else if (mBooleanDayResetDialog)
                mStrPopUpText = getString(R.string.dialog_day_reset);

            displayPopUpMsg();
        } else if (operation == Operation.GetStoreOpen.getValue()) {
            new NotificationSetClass(getContext());
            try {
                OfflineManager.getAuthorizations(getActivity());
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFS_NAME,
                    0);
            if (settings.getBoolean(Constants.isFirstTimeReg, false)) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(Constants.isFirstTimeReg, false);
                editor.commit();
                try {
                    OfflineManager.getAuthorizations(getActivity());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                try {
                    /*String syncTime = Constants.getSyncHistoryddmmyyyyTime();
                    String[] DEFINGREQARRAY = Constants.getDefinigReq(getActivity());


                    for (int incReq = 0; incReq < DEFINGREQARRAY.length; incReq++) {
                        String colName = DEFINGREQARRAY[incReq];
                        if (colName.contains("?$")) {
                            String splitCollName[] = colName.split("\\?");
                            colName = splitCollName[0];
                        }

                        Constants.events.updateStatus(Constants.SYNC_TABLE,
                                colName, Constants.TimeStamp, syncTime
                        );
                    }*/
                    List<String> DEFINGREQARRAY = Arrays.asList(Constants.getDefinigReq(getActivity()));
               //     Constants.updateSyncTime(DEFINGREQARRAY, getActivity(), Constants.Sync_All);
                } catch (Exception exce) {
                    exce.printStackTrace();
                    ConstantsUtils.printErrorLog(exce.getMessage());
                }
            }

            try {

                closeProgressDialog();
                TargetSync();
                //setAppointmentNotification();
            } catch (Exception e) {
                e.printStackTrace();
                ConstantsUtils.printErrorLog(e.getMessage());
            }

        }

    }

    private void displayPopUpMsg() {
        UtilConstants.showAlert(mStrPopUpText, getActivity());
        setAttendenceUI();
    }

    @Override
    public void onDestroyView() {
        presenter.onDestroy();
        super.onDestroyView();
    }

    private class GetNonVistedRetailers extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            String mStrDate = params[0];
            try {
                Thread.sleep(1000);
                delList = checkNonVisitedRetailers(mStrDate);

            } catch (InterruptedException e) {
                e.printStackTrace();
                ConstantsUtils.printErrorLog(e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            closeProgressDialog();
            attendanceFunctionality(iv_day_start_action, tv_day_start_text);
        }
    }

    /*AsyncTask to Close Attendance for day*/
    private class ClosingDate extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(mStrPopUpText);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);

                Constants.MapEntityVal.clear();

                String qry = Constants.Attendances + "?$filter=EndDate eq null and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
                try {
                    mStrAttendanceId = OfflineManager.getAttendance(qry);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
//                    ConstantsUtils.printErrorLog(e.getMessage());
                }

                Hashtable hashTableAttendanceValues;

                hashTableAttendanceValues = new Hashtable();
                if (Constants.MapEntityVal.size() > 0) {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);

                    String loginIdVal = sharedPreferences.getString(Constants.username, "");
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.LOGINID, loginIdVal);
                    //noinspection unchecked
                    if (Constants.MapEntityVal.get(Constants.AttendanceGUID) != null) {
                        hashTableAttendanceValues.put(Constants.AttendanceGUID, Constants.MapEntityVal.get(Constants.AttendanceGUID));
                    } else {
                        hashTableAttendanceValues.put(Constants.AttendanceGUID, "");
                    }
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.StartDate, Constants.MapEntityVal.get(Constants.StartDate));
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.StartTime, Constants.MapEntityVal.get(Constants.StartTime));
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.StartLat, Constants.MapEntityVal.get(Constants.StartLat));
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.StartLong, Constants.MapEntityVal.get(Constants.StartLong));
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.EndLat, BigDecimal.valueOf(UtilConstants.round(UtilConstants.latitude, 12)));
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.EndLong, BigDecimal.valueOf(UtilConstants.round(UtilConstants.longitude, 12)));
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.EndDate, UtilConstants.getNewDateTimeFormat());

                    hashTableAttendanceValues.put(Constants.SPGUID, mStrSPGUID);

                    hashTableAttendanceValues.put(Constants.SetResourcePath, Constants.MapEntityVal.get(Constants.SetResourcePath));

                    if (Constants.MapEntityVal.get(Constants.Etag) != null) {
                        hashTableAttendanceValues.put(Constants.Etag, Constants.MapEntityVal.get(Constants.Etag));
                    } else {
                        hashTableAttendanceValues.put(Constants.Etag, "");
                    }

                    hashTableAttendanceValues.put(Constants.Remarks, Constants.MapEntityVal.get(Constants.Remarks));
                    hashTableAttendanceValues.put(Constants.AttendanceTypeH1, Constants.MapEntityVal.get(Constants.AttendanceTypeH1));
                    hashTableAttendanceValues.put(Constants.AttendanceTypeH2, Constants.MapEntityVal.get(Constants.AttendanceTypeH2));

                    final Calendar calCurrentTime = Calendar.getInstance();
                    int hourOfDay = calCurrentTime.get(Calendar.HOUR_OF_DAY); // 24 hour clock
                    int minute = calCurrentTime.get(Calendar.MINUTE);
                    int second = calCurrentTime.get(Calendar.SECOND);
                    ODataDuration oDataDuration = null;
                    try {
                        oDataDuration = new ODataDurationDefaultImpl();
                        oDataDuration.setHours(hourOfDay);
                        oDataDuration.setMinutes(minute);
                        oDataDuration.setSeconds(BigDecimal.valueOf(second));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.EndTime, oDataDuration);

                    //noinspection unchecked

                    SharedPreferences sharedPreferencesVal = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
                    SharedPreferences.Editor editor = sharedPreferencesVal.edit();
                    editor.putInt("VisitSeqId", 0);
                    editor.commit();

                    try {
                        //noinspection unchecked
                        OfflineManager.updateAttendance(hashTableAttendanceValues, DaySummaryFragment.this);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                        ConstantsUtils.printErrorLog(e.getMessage());
                    }
                }else {
                    if(pdLoadDialog!=null && pdLoadDialog.isShowing()){
                        pdLoadDialog.dismiss();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                ConstantsUtils.printErrorLog(e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    /*AsyncTask to reset attendance for day*/
    private class ResettingDate extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(mStrPopUpText);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);

                Constants.MapEntityVal.clear();

                String dayEndClosedqry = Constants.Attendances + "?$filter=EndDate eq datetime'" + UtilConstants.getNewDate() + "' and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
                try {
                    mStrAttendanceId = OfflineManager.getAttendance(dayEndClosedqry);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                    ConstantsUtils.printErrorLog(e.getMessage());
                }

                Hashtable hashTableAttendanceValues;


                if(Constants.MapEntityVal.size()>0) {
                    hashTableAttendanceValues = new Hashtable();
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);

                    String loginIdVal = sharedPreferences.getString(Constants.username, "");
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.LOGINID, loginIdVal);
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.AttendanceGUID, Constants.MapEntityVal.get(Constants.AttendanceGUID));
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.StartDate, Constants.MapEntityVal.get(Constants.StartDate));
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.StartTime, Constants.MapEntityVal.get(Constants.StartTime));
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.StartLat, Constants.MapEntityVal.get(Constants.StartLat));
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.StartLong, Constants.MapEntityVal.get(Constants.StartLong));
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.EndLat, "");
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.EndLong, "");
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.EndDate, "");

                    hashTableAttendanceValues.put(Constants.Remarks, Constants.MapEntityVal.get(Constants.Remarks));
                    hashTableAttendanceValues.put(Constants.AttendanceTypeH1, Constants.MapEntityVal.get(Constants.AttendanceTypeH1));
                    hashTableAttendanceValues.put(Constants.AttendanceTypeH2, Constants.MapEntityVal.get(Constants.AttendanceTypeH2));

                    hashTableAttendanceValues.put(Constants.SPGUID, mStrSPGUID);

                    hashTableAttendanceValues.put(Constants.SetResourcePath, Constants.MapEntityVal.get(Constants.SetResourcePath));

                    if (Constants.MapEntityVal.get(Constants.Etag) != null) {
                        hashTableAttendanceValues.put(Constants.Etag, Constants.MapEntityVal.get(Constants.Etag));
                    } else {
                        hashTableAttendanceValues.put(Constants.Etag, "");
                    }

                    final Calendar calCurrentTime = Calendar.getInstance();
                    int hourOfDay = calCurrentTime.get(Calendar.HOUR_OF_DAY); // 24 hour clock
                    int minute = calCurrentTime.get(Calendar.MINUTE);
                    int second = calCurrentTime.get(Calendar.SECOND);
                    ODataDuration oDataDuration = null;
                    try {
                        oDataDuration = new ODataDurationDefaultImpl();
                        oDataDuration.setHours(hourOfDay);
                        oDataDuration.setMinutes(minute);
                        oDataDuration.setSeconds(BigDecimal.valueOf(second));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.EndTime, "");

                    try {
                        //noinspection unchecked
                        OfflineManager.resetAttendanceEntity(hashTableAttendanceValues, DaySummaryFragment.this);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                        ConstantsUtils.printErrorLog(e.getMessage());
                    }
                }else {
                    if(pdLoadDialog!=null && pdLoadDialog.isShowing()){
                        pdLoadDialog.dismiss();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                ConstantsUtils.printErrorLog(e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    /*
     Async task for Closing Visit End
    */
    private class ClosingVisit extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(mStrPopUpText);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(100);

                Hashtable table = new Hashtable();

                try {

                    if (!mStrOtherRetailerGuid.equalsIgnoreCase("")) {
                        mStrVisitId = ODataGuidDefaultImpl.initWithString36(mStrOtherRetailerGuid);
                    }
                    ODataEntity visitEntity;
                    visitEntity = OfflineManager.getVisitDetailsByKey(mStrVisitId);

                    if (visitEntity != null) {
                        oDataProperties = visitEntity.getProperties();
                        oDataProperty = oDataProperties.get(Constants.StartLat);
                        //noinspection unchecked
                        table.put(Constants.StartLat, oDataProperty.getValue());
                        oDataProperty = oDataProperties.get(Constants.StartLong);
                        //noinspection unchecked
                        table.put(Constants.StartLong, oDataProperty.getValue());
                        oDataProperty = oDataProperties.get(Constants.STARTDATE);
                        //noinspection unchecked
                        table.put(Constants.STARTDATE, oDataProperty.getValue());

                        oDataProperty = oDataProperties.get(Constants.STARTTIME);
                        //noinspection unchecked
                        table.put(Constants.STARTTIME, oDataProperty.getValue());

                        //noinspection unchecked
                        table.put(Constants.EndLat, BigDecimal.valueOf(UtilConstants.round(UtilConstants.latitude, 12)));
                        //noinspection unchecked
                        table.put(Constants.EndLong, BigDecimal.valueOf(UtilConstants.round(UtilConstants.longitude, 12)));
                        //noinspection unchecked
                        table.put(Constants.ENDDATE, UtilConstants.getNewDateTimeFormat());

                        //noinspection unchecked
                        oDataProperty = oDataProperties.get(Constants.CPNo);
                        table.put(Constants.CPNo, UtilConstants.removeLeadingZeros((String) (oDataProperty.getValue())));
                        //noinspection unchecked
                        table.put(Constants.VISITKEY, mStrVisitId.guidAsString36().toUpperCase());
                        //noinspection unchecked
                        table.put(Constants.Remarks, mStrVisitEndRemarks);

                        table.put(Constants.SPGUID, mStrSPGUID);

                        oDataProperty = oDataProperties.get(Constants.ROUTEPLANKEY);

                        //noinspection unchecked
                        if (oDataProperty.getValue() == null) {
                            table.put(Constants.ROUTEPLANKEY, "");
                        } else {
                            ODataGuid mRouteGuid = (ODataGuid) oDataProperty.getValue();

                            table.put(Constants.ROUTEPLANKEY, mRouteGuid.guidAsString36().toUpperCase());
                        }


                        oDataProperty = oDataProperties.get(Constants.StatusID);
                        table.put(Constants.StatusID, oDataProperty.getValue());

                        oDataProperty = oDataProperties.get(Constants.CPTypeID);
                        table.put(Constants.CPTypeID, oDataProperty.getValue());

                        oDataProperty = oDataProperties.get(Constants.VisitCatID);
                        table.put(Constants.VisitCatID, oDataProperty.getValue());

                        // table.put(Constants.VisitDate, UtilConstants.getNewDateTimeFormat());
                        try {
                            oDataProperty = oDataProperties.get(Constants.VisitDate);
                            table.put(Constants.VisitDate, oDataProperty != null ? oDataProperty.getValue() : null);
                        } catch (Exception e) {
                            oDataProperty = null;
                            table.put(Constants.VisitDate, "");
                        }

                        oDataProperty = oDataProperties.get(Constants.VisitSeq);
                        table.put(Constants.VisitSeq, oDataProperty.getValue());

                        oDataProperty = oDataProperties.get(Constants.CPGUID);
                        table.put(Constants.CPGUID, oDataProperty.getValue());


                        final Calendar calCurrentTime = Calendar.getInstance();
                        int hourOfDay = calCurrentTime.get(Calendar.HOUR_OF_DAY); // 24 hour clock
                        int minute = calCurrentTime.get(Calendar.MINUTE);
                        int second = calCurrentTime.get(Calendar.SECOND);
                        ODataDuration oDataDuration = null;
                        try {
                            oDataDuration = new ODataDurationDefaultImpl();
                            oDataDuration.setHours(hourOfDay);
                            oDataDuration.setMinutes(minute);
                            oDataDuration.setSeconds(BigDecimal.valueOf(second));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        table.put(Constants.ENDTIME, oDataDuration);

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
                        String loginIdVal = sharedPreferences.getString("username", "");
                        //noinspection unchecked
                        table.put(Constants.LOGINID, loginIdVal);

                        table.put(Constants.SetResourcePath, Constants.Visits + "(guid'" + mStrVisitId.guidAsString36().toUpperCase() + "')");

                        if (visitEntity.getEtag() != null) {
                            table.put(Constants.Etag, visitEntity.getEtag());
                        } else {
                        }

                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                    ConstantsUtils.printErrorLog(e.getMessage());
                }
                try {
                    //noinspection unchecked
                    OfflineManager.updateVisit(table, DaySummaryFragment.this);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                    ConstantsUtils.printErrorLog(e.getMessage());
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
                ConstantsUtils.printErrorLog(e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }
}
