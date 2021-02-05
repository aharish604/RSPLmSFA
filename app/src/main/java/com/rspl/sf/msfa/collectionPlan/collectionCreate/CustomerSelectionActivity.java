package com.rspl.sf.msfa.collectionPlan.collectionCreate;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.collectionPlan.WeekDetailsList;
import com.rspl.sf.msfa.collectionPlan.WeekHeaderList;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.filter.DateFilterFragment;
import com.rspl.sf.msfa.mbo.CustomerBean;
import com.rspl.sf.msfa.routeplan.customerslist.CustomerPresenter;
import com.rspl.sf.msfa.routeplan.customerslist.ICustomerViewPresenter;
import com.rspl.sf.msfa.routeplan.customerslist.filter.CustomersFilterActivity;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.ui.FlowLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by e10526 on 24-02-2018.
 */

public class CustomerSelectionActivity extends AppCompatActivity implements ICustomerViewPresenter<CustomerBean>, SwipeRefreshLayout.OnRefreshListener, AdapterInterface<CustomerBean> {
    // android components
    SwipeRefreshLayout swipeRefresh;
    RecyclerView recyclerView;
    TextView no_record_found;
    Toolbar toolbar;
    SimpleRecyclerViewAdapter<CustomerBean> recyclerViewAdapter = null;
    private FlowLayout flowLayout;
    LinearLayout linearLayoutFlowLayout;
    SearchView mSearchView;

    // variables
    CustomerPresenter presenter;
    ArrayList<CustomerBean> retailerArrayList = null;
    String customerNumber = "", visitType = "", beatType = "", ExternalRefID = "";
    private String From = Constants.RetailerList;

    private ArrayList<CustomerBean> customerSelectedList = new ArrayList<>();
    public static ArrayList<CustomerBean> customerSelectedListTemp = new ArrayList<>();
    private HashMap<String, CustomerBean> mapCustData = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_customer_list);
        initializeUI(this);
    }

    /**
     * Initialing UI
     */
    @Override
    public void initializeUI(Context context) {
//      ConstantsUtils.setProgressColor(this, swipeRefresh);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        no_record_found = (TextView) findViewById(R.id.no_record_found);
        linearLayoutFlowLayout = (LinearLayout) findViewById(R.id.llFilterLayout);
        flowLayout = (FlowLayout) findViewById(R.id.llFlowLayout);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_retailer_list), 0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        loadData();
        initializeClickListeners();
        initializeRecyclerViewItems(new LinearLayoutManager(this));
        initializeObjects(this);
    }

    /**
     * Initialing ClickListeners
     */
    @Override
    public void initializeClickListeners() {
//        swipeRefresh.setVisibility(View.GONE);
        ConstantsUtils.setProgressColor(this, swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
    }

    /**
     * Initialing Objects
     */
    @Override
    public void initializeObjects(Context context) {
        retailerArrayList = new ArrayList<>();
        presenter = new CustomerPresenter(this, this, this, visitType, customerNumber, "");
        presenter.loadRTGSCustomerList(mtpHeaderBean.getWeekDetailsLists(), ExternalRefID);

//        presenter.loadAsyncTask();
        displayRefreshTime(UtilConstants.getLastRefreshedTime(getApplicationContext(), ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.Customers, Constants.TimeStamp, this)));

    }

    /**
     * Initialing RecyclerView
     */
    @Override
    public void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new SimpleRecyclerViewAdapter<CustomerBean>(this, R.layout.mtp_customer_select_item, this, recyclerView, no_record_found);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void showProgressDialog() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideProgressDialog() {
        swipeRefresh.setRefreshing(false);
    }

    /**
     * getting data from Offline DB and populating to RecyclerView
     */
    @Override
    public void onRefreshData() {
        retailerArrayList = presenter.retailerArrayList;
        recyclerViewAdapter.refreshAdapter(retailerArrayList);
    }

    /**
     * Sync Customers Online to get Latest data
     */
    @Override
    public void customersListSync() {
        try {
//            presenter.loadAsyncTask();
            presenter.loadRTGSCustomerList(mtpHeaderBean.getWeekDetailsLists(), ExternalRefID);
            displayRefreshTime(UtilConstants.getLastRefreshedTime(getApplicationContext(), ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.Customers, Constants.TimeStamp, this)));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Opening Filter Activity
     */
    @Override
    public void openFilter(String filterType, String status, String grStatus) {
        Intent intent = new Intent(this, CustomersFilterActivity.class);
        intent.putExtra(DateFilterFragment.EXTRA_DEFAULT, filterType);
        intent.putExtra(CustomersFilterActivity.EXTRA_INVOICE_STATUS, status);
        intent.putExtra(CustomersFilterActivity.EXTRA_INVOICE_GR_STATUS, grStatus);
        startActivityForResult(intent, ConstantsUtils.ACTIVITY_RESULT_FILTER);
    }

    /**
     * Setting FlowLayout Filter Data
     */
    @Override
    public void setFilterDate(String filterType) {
        try {
            String[] filterTypeArr = filterType.split(", ");
            ConstantsUtils.displayFilter(filterTypeArr, flowLayout, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displaying Last Refresh time and setting to Toolbar
     */
    @Override
    public void displayRefreshTime(String refreshTime) {
        String lastRefresh = "";
        if (!TextUtils.isEmpty(refreshTime)) {
            lastRefresh = getString(R.string.po_last_refreshed) + " " + refreshTime;
        }
        if (lastRefresh != null)
            getSupportActionBar().setSubtitle(lastRefresh);

    }

    @Override
    public void displayMsg(String msg) {
        ConstantsUtils.displayLongToast(CustomerSelectionActivity.this, msg);
    }

    @Override
    public void sendSelectedItem(Intent intent) {

    }

    @Override
    public void errorMsgEditText(String error) {

    }

    @Override
    public void errorMsgEditText1(String error) {

    }

    /**
     * Getting Search Data
     */
    @Override
    public void searchResult(ArrayList<CustomerBean> retailerSearchList) {
        try {
            for (CustomerBean customerBean : retailerSearchList) {
                if (customerBean.isChecked()) {
                    mapCustData.put(customerBean.getCustomerId(), customerBean);
                   /* ArrayList<String> salesArea = null;
                    try {
                        salesArea = OfflineManager.getSaleAreaFromUsrAth("UserProfileAuthSet?$filter=Application%20eq%20%27PD%27"+" &$orderby=AuthOrgTypeID asc");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/

                    ArrayList<String> salesArea = new ArrayList<>();
                    try {
                        salesArea = OfflineManager.getSalesArea("UserProfileAuthSet?$filter=Application%20eq%20%27PD%27" + " &$orderby=AuthOrgTypeID asc");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ArrayList<String> distributorChannel = new ArrayList<>();
                    try {
                        distributorChannel = OfflineManager.getDistibuterChannelIds("UserProfileAuthSet?$filter=Application%20eq%20%27PD%27" + " and AuthOrgTypeID eq '000008'");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ArrayList<String> salesArearlist = new ArrayList<>();
                    salesArearlist.clear();

                    for (int i = 0; i < distributorChannel.size(); i++) {
                        for (int j = 0; j < salesArea.size(); j++) {
                            String saleArea[] = salesArea.get(j).split("/");

                            salesArearlist.add(saleArea[0] + "/" + distributorChannel.get(i) + "/" + saleArea[1]);

                            System.out.println("Sales Area List : " + saleArea[0] + "/" + distributorChannel.get(i) + "/" + saleArea[1]);

                        }

                    }
                    String stringSaleArea = "";
                    if (salesArearlist != null && !salesArearlist.isEmpty()) {
                        for (int i = 0; i < salesArearlist.size(); i++) {
                            if (i == salesArearlist.size() - 1) {
                                stringSaleArea = stringSaleArea + "SalesArea eq '" + salesArearlist.get(i) + "'";
                            } else {
                                stringSaleArea = stringSaleArea + "SalesArea eq '" + salesArearlist.get(i) + "' or ";
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(stringSaleArea)) {
                        String qry = Constants.CustomerSalesAreas + "?$filter=" + Constants.CustomerNo + " eq '" + customerBean.getCustomerId() + "' and (" + stringSaleArea + ")";
                        System.out.println("CustomerSalesAreas 1"+qry);
                        ArrayList<SaleAreaBean> sortList = OfflineManager.getSaleAreaFromCustomerCreditLmt(qry);
                        Collections.sort(sortList, new Comparator<SaleAreaBean>() {
                            public int compare(SaleAreaBean one, SaleAreaBean other) {
                                return one.getCreditControlAreaID().compareTo(other.getCreditControlAreaID());
                            }
                        });
                        customerBean.setSaleAreaBeanAl(sortList);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            recyclerViewAdapter.refreshAdapter(retailerSearchList);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Loading all UI components and Intent Data
     */
    private WeekHeaderList mtpHeaderBean;
    private WeekHeaderList mtpResultHeaderBean;
    private ArrayList<WeekDetailsList> weekDetailsList;

    private void loadData() {
        customerNumber = getIntent().getStringExtra(Constants.CPNo);
        visitType = getIntent().getStringExtra(Constants.VisitType);
        beatType = getIntent().getStringExtra(Constants.BeatType);
        ExternalRefID = getIntent().getStringExtra(ConstantsUtils.EXTRA_ExternalRefID);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mtpHeaderBean = (WeekHeaderList) bundle.getSerializable(Constants.EXTRA_BEAN);
//            listPos = bundle.getInt(ConstantsUtils.EXTRA_POS, 0);
        }
        if (mtpHeaderBean == null) {
            mtpHeaderBean = new WeekHeaderList();
        }
        weekDetailsList = mtpHeaderBean.getWeekDetailsLists();
        mtpResultHeaderBean = mtpHeaderBean;

    }

    /**
     * On Swipe Refresh
     */
    @Override
    public void onRefresh() {
        presenter.onRefresh();
    }

    @Override
    public void onItemClick(CustomerBean customerBean, View view, int i) {
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new MTPCreateVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, final CustomerBean customerBean) {
        ((MTPCreateVH) viewHolder).cbName.setText(customerBean.getCustomerName());
        ((MTPCreateVH) viewHolder).ivMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(customerBean.getMobile1())) {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.tel_txt + (customerBean.getMobile1())));
                    startActivity(dialIntent);
                }
            }
        });
        ((MTPCreateVH) viewHolder).cbName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                customerBean.setChecked(isChecked);
                 /* ArrayList<String> salesArea = null;
                try {
                    salesArea = OfflineManager.getSaleAreaFromUsrAth("UserProfileAuthSet?$filter=Application%20eq%20%27PD%27"+" &$orderby=AuthOrgTypeID asc");
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                ArrayList<String> salesArea = new ArrayList<>();
                try {
                    salesArea = OfflineManager.getSalesArea("UserProfileAuthSet?$filter=Application%20eq%20%27PD%27" + " &$orderby=AuthOrgTypeID asc");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ArrayList<String> distributorChannel = new ArrayList<>();
                try {
                    distributorChannel = OfflineManager.getDistibuterChannelIds("UserProfileAuthSet?$filter=Application%20eq%20%27PD%27" + " and AuthOrgTypeID eq '000008'");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ArrayList<String> salesArearlist = new ArrayList<>();
                salesArearlist.clear();

                for (int i = 0; i < distributorChannel.size(); i++) {
                    for (int j = 0; j < salesArea.size(); j++) {
                        String saleArea[] = salesArea.get(j).split("/");

                        salesArearlist.add(saleArea[0] + "/" + distributorChannel.get(i) + "/" + saleArea[1]);

                        System.out.println("Sales Area List : " + saleArea[0] + "/" + distributorChannel.get(i) + "/" + saleArea[1]);

                    }

                }

                String stringSaleArea = "";
                if (salesArearlist != null && !salesArearlist.isEmpty()) {
                    for (int i = 0; i < salesArearlist.size(); i++) {
                        if (i == salesArearlist.size() - 1) {
                            stringSaleArea = stringSaleArea + "SalesArea eq '" + salesArearlist.get(i) + "'";
                        } else {
                            stringSaleArea = stringSaleArea + "SalesArea eq '" + salesArearlist.get(i) + "' or ";
                        }
                    }
                }
                if (!TextUtils.isEmpty(stringSaleArea)) {
                    String qry = Constants.CustomerSalesAreas + "?$filter=" + Constants.CustomerNo + " eq '" + customerBean.getCustomerId() + "' and (" + stringSaleArea + ")";

                    System.out.println("CustomerSalesAreas 2"+qry);

                    ArrayList<SaleAreaBean> sortList = OfflineManager.getSaleAreaFromCustomerCreditLmt(qry);
                    Collections.sort(sortList, new Comparator<SaleAreaBean>() {
                        public int compare(SaleAreaBean one, SaleAreaBean other) {
                            return one.getCreditControlAreaID().compareTo(other.getCreditControlAreaID());
                        }
                    });
                    customerBean.setSaleAreaBeanAl(sortList);
                }
                if (isChecked) {
                    mapCustData.put(customerBean.getCustomerId(), customerBean);
                } else {
                    mapCustData.remove(customerBean.getCustomerId());
                }
               /* if (customerSelectedList.contains(customerBean)) {
                    customerSelectedList.remove(customerBean);
                } else {
                    customerSelectedList.add(customerBean);
                }*/
               /* if(isChecked){
                    customerSelectedList.add(customerBean);
                }*/
            }
        });
        ((MTPCreateVH) viewHolder).cbName.setChecked(customerBean.isChecked());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_apply, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.apply:
                Intent intent = new Intent();
                customerSelectedListTemp.clear();
                if (mapCustData != null && !mapCustData.isEmpty()) {
                    Iterator iterator = mapCustData.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = iterator.next().toString();
                        final CustomerBean customerBean = mapCustData.get(key);
                        customerSelectedListTemp.add(customerBean);
                    }
                }
//                customerSelectedListTemp.addAll(customerSelectedList);
                intent.putExtra(Constants.EXTRA_SO_ITEM_LIST, customerSelectedList);
                setResult(CollectionPlanCreateActivity.CUSTOMER_CATEGROIZE_REQUEST, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
