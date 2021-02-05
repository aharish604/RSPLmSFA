package com.rspl.sf.msfa.grreport;


import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.filter.DateFilterFragment;
import com.rspl.sf.msfa.interfaces.DialogCallBack;
import com.rspl.sf.msfa.registration.Configuration;
import com.rspl.sf.msfa.so.SOUtils;
import com.rspl.sf.msfa.soDetails.SOItemDetailsVH1;
import com.rspl.sf.msfa.ui.FlowLayout;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class GRReportListFragment extends Fragment implements  SwipeRefreshLayout.OnRefreshListener,IGRReportView, AdapterInterface<GRReportBean> {
    boolean isMaterialEnabled = false;
    private SimpleRecyclerViewAdapter<GRReportBean> salesOrderRecyclerViewAdapter;
    private ArrayList<GRReportBean> salesOrderBeenArrayList = new ArrayList<>();
    private ArrayList<GRReportBean> salesOrderBeenFilterArrayList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private String customerNumber, customerName, CPGUID, CPUID;
    private TextView textViewNoDataFound;
    private GRReportListPresenter presenter;
    private FlowLayout flowLayout;
    private LinearLayout linearLayoutFlowLayout;
    private String tabStatus = "";
    private int itemResource = 0;
    private String lastRefresh = "";
    private Bundle bundleExt = null;
    private String comingFrom = "";
    boolean isCheck = false;

    public GRReportListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundleExt = getArguments();
        if (bundleExt != null) {
            customerNumber = bundleExt.getString(Constants.CPNo, "");
            customerName = bundleExt.getString(Constants.CustomerName, "");
            CPGUID = bundleExt.getString(Constants.CPGUID, "");
            comingFrom = bundleExt.getString(Constants.comingFrom);
            isMaterialEnabled = bundleExt.getBoolean(Constants.isMaterialEnabled, false);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grreport_list, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeUI(view);
    }
    void initializeUI(View view) {
        salesOrderBeenArrayList = new ArrayList<>();
        textViewNoDataFound = (TextView) view.findViewById(R.id.no_record_found);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        linearLayoutFlowLayout = (LinearLayout) view.findViewById(R.id.llFilterLayout);
        flowLayout = (FlowLayout) view.findViewById(R.id.llFlowLayout);
        ConstantsUtils.setProgressColor(getContext(), swipeRefresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        swipeRefresh.setOnRefreshListener(this);
        setFilterDate("Last One Month");
        presenter = new GRReportListPresenter(getActivity(),getContext(),this,bundleExt);
        initializeRecyclerView();
        presenter.connectToOfflineDB();

    }
    void initializeRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        salesOrderRecyclerViewAdapter = new SimpleRecyclerViewAdapter<>(getActivity(),R.layout.rg_gr_report_list_items, this, recyclerView, textViewNoDataFound);
        recyclerView.setAdapter(salesOrderRecyclerViewAdapter);
    }

    @Override
    public void onRefresh() {
            presenter.onRefresh();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ConstantsUtils.ACTIVITY_RESULT_FILTER) {
            presenter.startFilter(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRefreshView() {
        presenter.connectToOfflineDB();
    }

    private boolean clickCheck = false;
    @Override
    public void onItemClick(GRReportBean item, View view, int position) {
    }
    private void refreshUI(final String errorMsg) {
        clickCheck = false;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    hideProgressDialog();
                    if (!TextUtils.isEmpty(errorMsg)) {
                        if(errorMsg.contains("HTTP Status 401 ? Unauthorized")){
                            dialogMessage(errorMsg);
                        }else {
                            Constants.dialogBoxWithButton(getActivity(), "", errorMsg, getString(R.string.ok), "",  new DialogCallBack() {
                                @Override
                                public void clickedStatus(boolean b) {
                                }
                            });
                        }
                    }
            }
        });
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, View viewItem) {
        return new GRReportListViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, GRReportBean item) {
//        ((GRReportListViewHolder)holder).tv_gr_date.setText(item.getMaterialDocDate());
//        ((GRReportListViewHolder)holder).tv_gr_id.setText(item.getMaterialDocNo());
//        ((GRReportListViewHolder)holder).tv_bill_no.setText(item.getZZEWayBillNo());
        ((GRReportListViewHolder)holder).tv_invoice_no.setText(item.getInvoiceNo());
        ((GRReportListViewHolder)holder).tv_invoice_date.setText(item.getInvoiceDate());
        if(!TextUtils.isEmpty(item.getZZTranspDate()) && !TextUtils.isEmpty(item.getZZTranspTime())){
            if(!TextUtils.equals(item.getZZTranspTime(),"00:00")) {
                ((GRReportListViewHolder) holder).tv_gr_date.setText(item.getZZTranspDate() + " " + item.getZZTranspTime());
            }else {
                ((GRReportListViewHolder)holder).tv_gr_date.setText("-");
            }
        }else {
            ((GRReportListViewHolder)holder).tv_gr_date.setText("-");
        }

        if(!TextUtils.isEmpty(item.getZZGRDate()) && !TextUtils.isEmpty(item.getZZGRTime())){
            if(!TextUtils.equals(item.getZZGRTime(),"00:00")) {
                ((GRReportListViewHolder) holder).tv_trans_date.setText(item.getZZGRDate() + " " + item.getZZGRTime());
            }else {
                ((GRReportListViewHolder)holder).tv_trans_date.setText("-");
            }
        }else {
            ((GRReportListViewHolder)holder).tv_trans_date.setText("-");
        }

        if(!TextUtils.isEmpty(item.getZZTotalTime())){
            if(!TextUtils.equals(item.getZZTotalTime(),"00:00")) {
                ((GRReportListViewHolder) holder).tv_gr_time.setText(item.getZZTotalTime() + " Hrs for GR");
            }else {
                ((GRReportListViewHolder)holder).tv_gr_time.setText("-");
            }
        }else {
            ((GRReportListViewHolder)holder).tv_gr_time.setText("-");
        }

        if(!TextUtils.isEmpty(item.getZZGRNo())){
            ((GRReportListViewHolder)holder).tv_gr_no.setText(item.getZZGRNo());
            if(!TextUtils.equals(item.getZZShortDmgSts(),"NO")){
                ((GRReportListViewHolder)holder).tv_gr_no.setTextColor(getResources().getColor(R.color.RED));
            }else {
                ((GRReportListViewHolder)holder).tv_gr_no.setTextColor(getResources().getColor(R.color.InvStatusGreen));
            }
        }else {
            ((GRReportListViewHolder)holder).tv_gr_no.setText("-");
        }

        Drawable delvStatusImg = SOUtils.displayStatusImage("C", "C", getContext());
        if (delvStatusImg != null) {
            ((GRReportListViewHolder) holder).imageViewDeliveryStatus.setImageDrawable(delvStatusImg);
        }
        if(TextUtils.isEmpty(item.getZZGRNo())) {
            Drawable img = ContextCompat.getDrawable(getActivity(), R.drawable.ic_local_shipping_black_24dp).mutate();
            if (img != null)
                img.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorGrey), PorterDuff.Mode.SRC_IN);

            ((GRReportListViewHolder) holder).imageViewDeliveryStatus.setImageDrawable(img);
        }
    }

    @Override
    public void dialogMessage(String message) {
        isCheck = false;
//        ConstantsUtils.displayLongToast(getContext(), message);
        if(message.contains("HTTP Status 401 ? Unauthorized")){
            try {
                Constants.customAlertDialogWithScroll(getContext(), message);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }else {
            Constants.dialogBoxWithButton(getActivity(), "", message, getString(R.string.ok), "",  new DialogCallBack() {
                @Override
                public void clickedStatus(boolean b) {
                }
            });
        }
    }

    @Override
    public void showProgressDialog() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideProgressDialog() {
        isCheck = false;
        clickCheck = false;
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void searchResult(ArrayList<GRReportBean> salesOrderBeen) {

    }

    @Override
    public void displayData(ArrayList<GRReportBean> salesOrderBeen) {
        try {
            Constants.events.updateStatus(Constants.SYNC_TABLE,
                    Constants.INVOICES, Constants.TimeStamp, Constants.getSyncHistoryddmmyyyyTime()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            displayRefreshTime(ConstantsUtils.getLastSeenDateFormat(getActivity(), ConstantsUtils.getMilliSeconds(
                    ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.INVOICES, Constants.TimeStamp, getActivity()))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        salesOrderRecyclerViewAdapter.refreshAdapter(salesOrderBeen);
    }

    @Override
    public void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus) {
        Intent intent = new Intent(getContext(), GRFilterActivity.class);
        intent.putExtra(DateFilterFragment.EXTRA_DEFAULT, filterType);
        intent.putExtra(DateFilterFragment.EXTRA_START_DATE, startDate);
        intent.putExtra(DateFilterFragment.EXTRA_END_DATE, endDate);
        intent.putExtra(Constants.comingFrom, "GRReport");
        intent.putExtra(GRFilterActivity.EXTRA_AS_COMPNY_CODE, "");
        startActivityForResult(intent, ConstantsUtils.ACTIVITY_RESULT_FILTER);
    }


    @Override
    public void setFilterDate(String filterType) {
        try {
            String[] filterTypeArr = filterType.split(", ");
            if(!TextUtils.isEmpty(filterType)) {
                linearLayoutFlowLayout.setVisibility(View.VISIBLE);
            }else {
                linearLayoutFlowLayout.setVisibility(View.GONE);
            }
            ConstantsUtils.displayFilter(filterTypeArr, linearLayoutFlowLayout, getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void displayRefreshTime(String refreshTime) {
        if (!TextUtils.isEmpty(refreshTime)) {
            lastRefresh = getString(R.string.po_last_refreshed) + " " + refreshTime;
        }
        ((GRReportListActivity)getActivity() ).setActionBarSubTitle(lastRefresh);
    }
    @Override
    public void onResume() {
        super.onResume();
        clickCheck = false;
//        displayRefreshTime(SyncUtils.getCollectionSyncTime(getContext(), Constants.MaterialDocs));
    }
    @Override
    public void onDestroyView() {
        presenter.onDestroy();
        super.onDestroyView();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getActivity().getComponentName());
        SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        MenuItem dateFilter = menu.findItem(R.id.filter);
        if (TextUtils.isEmpty("")) {
            dateFilter.setVisible(true);
        } else {
            dateFilter.setVisible(false);


        }
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.lbl_gr_doc_num_search));

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.onSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                presenter.onSearch(newText);
                return false;
            }
        });
//        presenter.onSearch("");
        presenter.getRefreshTime();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                presenter.onFilter();
                return true;
            case R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
