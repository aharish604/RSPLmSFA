package com.rspl.sf.msfa.mtp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.rspl.sf.msfa.CustomerDetailsActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.collectionPlan.HeaderVH;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.routeplan.customerslist.CustomerListActivity;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class MTPTodayFragment extends Fragment implements MTPTodayView, AdapterViewInterface<MTPRoutePlanBean>, SwipeRefreshLayout.OnRefreshListener {

    private MTPTodayPresenterImpl presenter;
    private TextView tvActivityDesc;
    private TextView tvRemarks;
    private RecyclerView rvToday;
    private SimpleRecyclerViewTypeAdapter<MTPRoutePlanBean> simpleRecyclerViewAdapter;
    private ConstraintLayout clHeader;
    private SwipeRefreshLayout swipeRefresh;
    private String lastRefreshTime = "";
    private String spGUID = "";
    private TextView noRecordFound;
    private boolean isAsmLogin = false;

    public MTPTodayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvToday = (RecyclerView) view.findViewById(R.id.rvRoutList);
        noRecordFound = (TextView) view.findViewById(R.id.no_record_found);
        tvActivityDesc = (TextView) view.findViewById(R.id.tvActivityDesc);
        tvRemarks = (TextView) view.findViewById(R.id.tvRemarks);
        clHeader = (ConstraintLayout) view.findViewById(R.id.clHeader);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);

        rvToday.setHasFixedSize(true);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewTypeAdapter<MTPRoutePlanBean>(getContext(), R.layout.mtp_today_plan_item, this, rvToday, noRecordFound);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvToday.setLayoutManager(linearLayoutManager);
        rvToday.setAdapter(simpleRecyclerViewAdapter);

        presenter = new MTPTodayPresenterImpl(getActivity(),getContext(), this, spGUID, isAsmLogin);
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
    public void displayData(ArrayList<MTPRoutePlanBean> displayList) {
        if (getActivity()!=null||isAdded()) {
            simpleRecyclerViewAdapter.refreshAdapter(displayList);
            if (!displayList.isEmpty()) {
                MTPRoutePlanBean mtpRoutePlanBean = displayList.get(0);
                tvRemarks.setText(mtpRoutePlanBean.getRemarks());
                tvActivityDesc.setText(mtpRoutePlanBean.getActivityDec());
                clHeader.setVisibility(View.VISIBLE);
            } else {
                clHeader.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void displayLastRefreshedTime(String refreshTime) {
        try {
            if (getActivity()!=null||isAdded()) {
                lastRefreshTime = refreshTime;
                String lastRefresh = "";
                if (!TextUtils.isEmpty(refreshTime)) {
                    lastRefresh = getString(R.string.po_last_refreshed) + " " + refreshTime;
                }
                ((MTPActivity) getActivity()).displaySubTitles(lastRefresh);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showMsg(String msg) {
        ConstantsUtils.displayLongToast(getContext(), msg);
    }

    @Override
    public void onItemClick(MTPRoutePlanBean weekHeaderList, View view, int i) {
        if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
            String rollType = sharedPreferences.getString(Constants.USERROLE, "");
//            if (isAsmLogin) {
            if (rollType.equalsIgnoreCase("Z1") || rollType.equalsIgnoreCase("Z3")) {
                Intent intentRetailerDetails = new Intent(getContext(), CustomerListActivity.class);
                intentRetailerDetails.putExtra(Constants.comingFrom, Constants.MTPList);
                intentRetailerDetails.putExtra(Constants.EXTRA_TITLE, weekHeaderList.getSalesDistrictDesc());
                intentRetailerDetails.putExtra(Constants.SalesDistrictID, weekHeaderList.getSalesDistrict());
                startActivity(intentRetailerDetails);
            } else {
                checkCustomerVisited(weekHeaderList);
            }
        } else {
            UtilConstants.dialogBoxWithCallBack(getContext(), "", getContext().getString(R.string.autodate_change_msg), getContext().getString(R.string.autodate_change_btn), "", false, new com.arteriatech.mutils.interfaces.DialogCallBack() {
                @Override
                public void clickedStatus(boolean b) {
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), ConstantsUtils.DATE_SETTINGS_REQUEST_CODE);
                }
            });
        }
    }

    private void onNavigateToCustomerDetails(MTPRoutePlanBean weekHeaderList) {
        Intent intentRetailerDetails = new Intent(getContext(), CustomerDetailsActivity.class);
        intentRetailerDetails.putExtra(Constants.RetailerName, weekHeaderList.getCustomerName());
        intentRetailerDetails.putExtra(Constants.PostalCode, weekHeaderList.getPostalCode());
        intentRetailerDetails.putExtra(Constants.CPNo, weekHeaderList.getCustomerNo());
        intentRetailerDetails.putExtra(Constants.CPUID, weekHeaderList.getCustomerNo());
        intentRetailerDetails.putExtra(Constants.CPGUID32, weekHeaderList.getCustomerNo());
        intentRetailerDetails.putExtra(Constants.Address, weekHeaderList.getAddress());
        intentRetailerDetails.putExtra("MobileNo", weekHeaderList.getMobile1());
        intentRetailerDetails.putExtra(Constants.VisitCatID, Constants.BeatVisitCatID);
        intentRetailerDetails.putExtra(Constants.comingFrom, Constants.MTPList);
        intentRetailerDetails.putExtra(Constants.NAVFROM, Constants.Retailer);
        Constants.VisitNavigationFrom = "";
        startActivity(intentRetailerDetails);
    }

    private void checkCustomerVisited(final MTPRoutePlanBean weekHeaderList) {
        boolean isCustVisited = false;
        String mStrSPGUID = Constants.getSPGUID(Constants.SPGUID);
        String mStrVisitQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() +
                "' and CPGUID eq '" + weekHeaderList.getCustomerNo() + "' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
        try {
            if (OfflineManager.getVisitActivityStatusForVisit(mStrVisitQry)) {
                isCustVisited = true;
            } else {
                isCustVisited = false;
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        if (isCustVisited) {
            UtilConstants.dialogBoxWithCallBack(getContext(), "", getString(R.string.alert_customer_already_visited), getString(R.string.yes), getString(R.string.no), true, new DialogCallBack() {
                @Override
                public void clickedStatus(boolean b) {
                    if (b) {
                        onNavigateToCustomerDetails(weekHeaderList);
                    }
                }
            });
        } else {
            onNavigateToCustomerDetails(weekHeaderList);
        }

    }

    @Override
    public int getItemViewType(int i, ArrayList<MTPRoutePlanBean> arrayList) {
        if (!TextUtils.isEmpty(arrayList.get(i).getSalesDistrict()) || !TextUtils.isEmpty(arrayList.get(i).getCustomerNo()))
            return 0;
        else
            return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        if (i == 0)
            return new MTPTodayVH(view);
        else {
            View viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.snippet_route_plan_item, viewGroup, false);
            return new HeaderVH(viewItem);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i, final MTPRoutePlanBean weekHeaderList, ArrayList<MTPRoutePlanBean> arrayList) {
        if (viewHolder instanceof MTPTodayVH) {
            String displayName = weekHeaderList.getCustomerName();
            if (TextUtils.isEmpty(displayName)) {
                displayName = weekHeaderList.getSalesDistrictDesc();
            }
            ((MTPTodayVH) viewHolder).tvName.setText(displayName);
            try {
                String fChar = "";
                if (!TextUtils.isEmpty(displayName)) {
                    fChar = String.valueOf(displayName.charAt(0)).toUpperCase();
                }
                ((MTPTodayVH) viewHolder).tvHName.setText(fChar);
            } catch (Exception e) {
                e.printStackTrace();
            }
//        ((MTPTodayVH) viewHolder).tvRemarks.setText(weekHeaderList.getRemarks());
            if (!TextUtils.isEmpty(weekHeaderList.getCustomerNo())) {
                ((MTPTodayVH) viewHolder).tvDesc.setText(weekHeaderList.getCustomerNo() + " " + weekHeaderList.getCity());
                ((MTPTodayVH) viewHolder).tvDesc.setVisibility(View.VISIBLE);
            } else {
                ((MTPTodayVH) viewHolder).tvDesc.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(weekHeaderList.getMobile1())) {
                ((MTPTodayVH) viewHolder).ivMobile.setVisibility(View.VISIBLE);
                ((MTPTodayVH) viewHolder).ivMobile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(weekHeaderList.getMobile1())) {
                            Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.tel_txt + (weekHeaderList.getMobile1())));
                            startActivity(dialIntent);
                        }
                    }
                });
            } else {
                ((MTPTodayVH) viewHolder).ivMobile.setVisibility(View.GONE);
            }
        } else if (viewHolder instanceof HeaderVH) {
            ((HeaderVH) viewHolder).tvWeekHeader.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Calendar currentCal = Calendar.getInstance();
//        tvDate.setText(ConstantsUtils.convertCalenderToDisplayDateFormat(currentCal, "dd-MMM-yyyy"));
        ((MTPActivity) getActivity()).displayTitle(" : " + ConstantsUtils.convertCalenderToDisplayDateFormat(currentCal, "dd-MMM-yyyy"));
        displayLastRefreshedTime(lastRefreshTime);
    }

    @Override
    public void onRefresh() {
        presenter.onRefresh();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantsUtils.DATE_SETTINGS_REQUEST_CODE) {
            presenter.onStart();
        }
    }
}
