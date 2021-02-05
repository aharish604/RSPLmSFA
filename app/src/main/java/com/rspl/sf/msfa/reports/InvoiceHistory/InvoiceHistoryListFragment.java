//package com.arteriatech.sf.reports.InvoiceHistory;
//
//
//import android.app.SearchManager;
//import android.app.SearchableInfo;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.SearchView;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.arteriatech.mutils.adapter.AdapterInterface;
//import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
//import com.arteriatech.mutils.common.UtilConstants;
//import Constants;
//import com.arteriatech.sf.common.ConstantsUtils;
//import com.arteriatech.sf.filter.DateFilterFragment;
//import R;
//import com.arteriatech.sf.reports.InvoiceDetailsActivity;
//import com.arteriatech.sf.reports.InvoiceHistory.filter.SOFilterActivity;
//import com.arteriatech.sf.reports.invoicelist.InvoiceListBean;
//import com.arteriatech.sf.so.SOUtils;
//
//import java.util.ArrayList;
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class InvoiceHistoryListFragment extends Fragment implements
//        SwipeRefreshLayout.OnRefreshListener,AdapterInterface<InvoiceListBean>,InvoiceListView.SalesOrderResponse,InvoiceListView {
//    SimpleRecyclerViewAdapter<InvoiceListBean> salesOrderRecyclerViewAdapter;
//    ArrayList<InvoiceListBean>salesOrderBeenArraylist;
//    ArrayList<InvoiceListBean>salesOrderBeenFilterArraylist =new ArrayList<>();
//    SwipeRefreshLayout swipeRefresh;
//    RecyclerView recyclerView;
//    String customerNumber,customerName,CPGUID,CPUID;
//    TextView textViewNoDataFound;
//    InvoiceHeaderListPresenter presenter;
//    private String mStrBundleRetUID="";
//
//    public InvoiceHistoryListFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Bundle bundleExt = getArguments();
//
//        if (bundleExt != null) {
//            customerNumber = bundleExt.getString(Constants.CPNo);
//            customerName = bundleExt.getString(Constants.RetailerName);
//            mStrBundleRetUID = bundleExt.getString(Constants.CPUID);
//            CPGUID = bundleExt.getString(Constants.CPGUID);
//        }
//
//        setHasOptionsMenu(true);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_sales_order_header_list, container, false);
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        initializeUI(view);
//    }
//
//    void initializeUI(View view){
//        salesOrderBeenArraylist=new ArrayList<>();
//        textViewNoDataFound=(TextView)view.findViewById(R.id.no_record_found);
//        swipeRefresh=(SwipeRefreshLayout)view.findViewById(R.id.swipeRefresh);
//        ConstantsUtils.setProgressColor(getContext(), swipeRefresh);
//        recyclerView=(RecyclerView)view.findViewById(R.id.recyclerView);
//        swipeRefresh.setOnRefreshListener(this);
//        presenter = new InvoiceHeaderListPresenter(getActivity(),customerNumber,this,CPGUID);
//        initializeRecyclerView();
//        presenter.connectToOfflineDB(this);
//
//    }
//    void initializeRecyclerView(){
//        recyclerView.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());
//        recyclerView.setLayoutManager(linearLayoutManager);
//        int itemResource = R.layout.recycler_view_invoice_history_list;
//        salesOrderRecyclerViewAdapter = new SimpleRecyclerViewAdapter<>(getActivity(),itemResource,this,recyclerView,textViewNoDataFound);
//        recyclerView.setAdapter(salesOrderRecyclerViewAdapter);
//    }
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        menu.clear();
//        inflater.inflate(R.menu.menu_search, menu);
//        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
//        SearchableInfo searchInfo = searchManager.getSearchableInfo(getActivity().getComponentName());
//        SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
//        MenuItem dateFilter = menu.findItem(R.id.filter);
//        if (TextUtils.isEmpty("")) {
//            dateFilter.setVisible(true);
//        } else {
//
//            dateFilter.setVisible(false);
//        }
//        mSearchView.setSearchableInfo(searchInfo);
//        mSearchView.setQueryHint(getString(R.string.lbl_so_doc_num_search));
//        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                presenter.onSearch(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                presenter.onSearch(newText);
//                return false;
//            }
//        });
//        presenter.onSearch("");
////        presenter.getRefreshTime();
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.filter:
//                presenter.onFilter();
//                return true;
//            case R.id.home:
//                getActivity().finish();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//
//    @Override
//    public void onRefresh() {
//        presenter.onRefresh();
//    }
//
//    @Override
//    public void success(ArrayList success) {
//        this.salesOrderBeenArraylist =success;
//        salesOrderRecyclerViewAdapter.refreshAdapter(success);
//    }
//
//    @Override
//    public void error(String message) {
//
//    }
//
//    @Override
//    public void showMessage(String message) {
//
//    }
//
//    @Override
//    public void dialogMessage(String message, String msgType) {
//
//    }
//
//    @Override
//    public void showProgressDialog() {
//        swipeRefresh.setRefreshing(true);
//    }
//
//    @Override
//    public void hideProgressDialog() {
//        swipeRefresh.setRefreshing(false);
//    }
//
//    @Override
//    public void searchResult(ArrayList<InvoiceListBean> salesOrderBeen) {
//        salesOrderRecyclerViewAdapter.refreshAdapter(salesOrderBeen);
//    }
//
//    @Override
//    public void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus) {
//        Intent intent = new Intent(getContext(), SOFilterActivity.class);
//
//        intent.putExtra(SOFilterActivity.EXTRA_DELV_STATUS, delvStatus);
//        startActivityForResult(intent, ConstantsUtils.ACTIVITY_RESULT_FILTER);
//    }
//
//    @Override
//    public void setFilterDate(String filterType) {
//
//    }
//
//    @Override
//    public void displayRefreshTime(String refreshTime) {
//
//    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == ConstantsUtils.ACTIVITY_RESULT_FILTER) {
//            String filterType = data.getStringExtra(DateFilterFragment.EXTRA_DEFAULT);
//            String startDate = data.getStringExtra(DateFilterFragment.EXTRA_START_DATE);
//            String endDate = data.getStringExtra(DateFilterFragment.EXTRA_END_DATE);
//            String soStatus = data.getStringExtra(SOFilterActivity.EXTRA_SO_STATUS);
//            String statusName = data.getStringExtra(SOFilterActivity.EXTRA_SO_STATUS_NAME);
//            String delvStatus = data.getStringExtra(SOFilterActivity.EXTRA_DELV_STATUS);
//            String delvStatusName = data.getStringExtra(SOFilterActivity.EXTRA_DELV_STATUS_NAME);
//            salesOrderBeenFilterArraylist.clear();
//            if (!delvStatusName.equalsIgnoreCase(Constants.ALL)) {
//                for (int i = 0; i <salesOrderBeenArraylist.size(); i++) {
//                    if (salesOrderBeenArraylist.get(i).getDeviceStatus().equalsIgnoreCase(delvStatus)){
//                        InvoiceListBean salesOrderBean =salesOrderBeenArraylist.get(i);
//                        salesOrderBeenFilterArraylist.add(salesOrderBean);
//                    }
//                }
//                salesOrderRecyclerViewAdapter.refreshAdapter(salesOrderBeenFilterArraylist);
//            } else {
//                salesOrderRecyclerViewAdapter.refreshAdapter(salesOrderBeenArraylist);
//            }
//            presenter.startFilter(startDate, endDate, filterType, soStatus, delvStatus, statusName, delvStatusName);
//
//        }
//    }
//    /**
//     * RecyclcerView Click listener
//     */
//    @Override
//    public void onItemClick(InvoiceListBean salesOrderBean, View view, int i) {
//
//        Intent toInvoiceHisdetails = new Intent(getActivity(), InvoiceDetailsActivity.class);
//        startActivity(toInvoiceHisdetails);
//
//    }
//
//    /**
//     * recyclerView Resource
//     */
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
//        return new InvoiceHeaderListViewHolder(view);
//    }
//
//    /**
//     * recyclerView OnBindViewHolder
//     * @param salesOrderBean
//     */
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, InvoiceListBean salesOrderBean) {
//        ((InvoiceHeaderListViewHolder) viewHolder).textViewOrderID.setText(salesOrderBean.getInvoiceNo());
//        ((InvoiceHeaderListViewHolder) viewHolder).textViewOrderDate.setText(salesOrderBean.getInvoiceDate());
//        ((InvoiceHeaderListViewHolder) viewHolder).textViewSalesOrderValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(salesOrderBean.getTotAmount()) + " " + salesOrderBean.getCurrency());
//        ((InvoiceHeaderListViewHolder) viewHolder).textViewQty.setText(salesOrderBean.getInvQty());
//
//        Drawable delvStatusImg = SOUtils.displayStatusImage(salesOrderBean.getDeviceStatus(), getContext());
//        if (delvStatusImg != null) {
//            ((InvoiceHeaderListViewHolder) viewHolder).imageViewDeliveryStatus.setImageDrawable(delvStatusImg);
//        }
//
//    }
//}
