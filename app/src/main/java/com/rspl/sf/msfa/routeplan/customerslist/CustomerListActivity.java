package com.rspl.sf.msfa.routeplan.customerslist;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.CustomerDetailsActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.filter.DateFilterFragment;
import com.rspl.sf.msfa.mbo.CustomerBean;
import com.rspl.sf.msfa.routeplan.customerslist.filter.CustomersFilterActivity;
import com.rspl.sf.msfa.ui.FlowLayout;

import java.util.ArrayList;


public class CustomerListActivity extends AppCompatActivity implements ICustomerViewPresenter, SwipeRefreshLayout.OnRefreshListener, AdapterInterface<CustomerBean> {
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
    String customerNumber = "", visitType = "", beatType = "";
    private String comingFrom = Constants.RetailerList;
    private String salesDistrictId;

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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        no_record_found = (TextView) findViewById(R.id.no_record_found);
        linearLayoutFlowLayout = (LinearLayout) findViewById(R.id.llFilterLayout);
        flowLayout = (FlowLayout) findViewById(R.id.llFlowLayout);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        loadData();
        initializeClickListeners();
        initializeObjects(this);
        initializeRecyclerViewItems(new LinearLayoutManager(this));
    }

    /**
     * Initialing ClickListeners
     */
    @Override
    public void initializeClickListeners() {
        swipeRefresh.setOnRefreshListener(this);
    }

    /**
     * Initialing Objects
     */
    @Override
    public void initializeObjects(Context context) {
        retailerArrayList = new ArrayList<>();
        presenter = new CustomerPresenter(this, this, this, visitType, customerNumber, salesDistrictId);
        presenter.loadAsyncTask();

        displayRefreshTime(UtilConstants.getLastRefreshedTime(getApplicationContext(), ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.Customers, Constants.TimeStamp, this)));

    }

    /**
     * Initialing RecyclerView
     */
    @Override
    public void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new SimpleRecyclerViewAdapter<>(this, R.layout.snippet_customer, this, recyclerView, no_record_found);
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
            presenter.loadAsyncTask();
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
        if (!comingFrom.equalsIgnoreCase(Constants.MTPList)) {
            if (lastRefresh != null)
                getSupportActionBar().setSubtitle(lastRefresh);
        }
    }

    @Override
    public void displayMsg(String msg) {
        ConstantsUtils.displayLongToast(CustomerListActivity.this,msg);
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
    public void searchResult(ArrayList retailerSearchList) {
        try {
            recyclerViewAdapter.refreshAdapter(retailerSearchList);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Loading all UI components and Intent Data
     */
    private void loadData() {
        customerNumber = getIntent().getStringExtra(Constants.CPNo);
        visitType = getIntent().getStringExtra(Constants.VisitType);
        beatType = getIntent().getStringExtra(Constants.BeatType);
        comingFrom = getIntent().getStringExtra(Constants.comingFrom);
        salesDistrictId = getIntent().getStringExtra(Constants.SalesDistrictID);
        String salesDistrictTitle = getIntent().getStringExtra(Constants.EXTRA_TITLE);
        String title = getString(R.string.lbl_retailer_list);
        if (TextUtils.isEmpty(comingFrom)) {
            comingFrom = Constants.RetailerList;
        } else if (comingFrom.equalsIgnoreCase(Constants.AdhocList)) {
            title = getString(R.string.lbl_adhoc_list);
        } else {
            if (!TextUtils.isEmpty(salesDistrictTitle))
                title = salesDistrictTitle;
        }
        ConstantsUtils.initActionBarView(this, toolbar, true, title, 0);
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
        onItemClickIntent(customerBean);
    }

    private void onItemClickIntent(CustomerBean customerBean) {
        if (ConstantsUtils.isAutomaticTimeZone(CustomerListActivity.this)) {
            Intent intentRetailerDetails = new Intent(this, CustomerDetailsActivity.class);
            intentRetailerDetails.putExtra(Constants.RetailerName, customerBean.getCustomerName());
            intentRetailerDetails.putExtra(Constants.PostalCode, customerBean.getPostalCode());
            intentRetailerDetails.putExtra(Constants.CPNo, customerBean.getCustomerId());
            intentRetailerDetails.putExtra(Constants.CPGUID32, customerBean.getCustomerId());

            intentRetailerDetails.putExtra(Constants.Address, customerBean.getAddress1());
            intentRetailerDetails.putExtra("MobileNo", customerBean.getMobile1());

            if (comingFrom.equalsIgnoreCase(Constants.ProspectiveCustomerList)) {
                intentRetailerDetails.putExtra(Constants.comingFrom, Constants.ProspectiveCustomerList);
            } else if (comingFrom.equalsIgnoreCase(Constants.MTPList)) {
                intentRetailerDetails.putExtra(Constants.comingFrom, comingFrom);
                intentRetailerDetails.putExtra(Constants.VisitCatID, Constants.BeatVisitCatID);
            } else if (comingFrom.equalsIgnoreCase(Constants.AdhocList)) {
                intentRetailerDetails.putExtra(Constants.comingFrom, comingFrom);
                intentRetailerDetails.putExtra(Constants.VisitCatID, Constants.AdhocVisitCatID);
                if (customerBean.getCurrency() != null) {
                    intentRetailerDetails.putExtra(Constants.Currency, customerBean.getCurrency());
                } else {
                    intentRetailerDetails.putExtra(Constants.Currency, "");
                }
            } else {
                intentRetailerDetails.putExtra(Constants.comingFrom, Constants.RetailerList);
            }
            intentRetailerDetails.putExtra(Constants.NAVFROM, Constants.Retailer);
            //        intentRetailerDetails.putExtra(Constants.CPGUID, customerBean.getCPGUID());
            Constants.VisitNavigationFrom = "";
            startActivity(intentRetailerDetails);
        } else {
            ConstantsUtils.showAutoDateSetDialog(CustomerListActivity.this);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new CustomerListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i, final CustomerBean customerBean) {

        ((CustomerListViewHolder) viewHolder).tvRetailerName.setText(customerBean.getCustomerName());
        ((CustomerListViewHolder) viewHolder).tv_retailer_mob_no.setText(customerBean.getCustomerId() + " " + customerBean.getCity());
        ((CustomerListViewHolder) viewHolder).ivMobileNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!customerBean.getMobile1().equalsIgnoreCase("")) {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.tel_txt + (customerBean.getMobile1())));
                    startActivity(dialIntent);
                }
            }
        });
        String cityVal, state = "";

        cityVal = customerBean.getCity();
        state = customerBean.getState();

        String disticVal;

        if (!customerBean.getDistrict().equalsIgnoreCase("") && !customerBean.getPostalCode().equalsIgnoreCase("")) {
            disticVal = customerBean.getDistrict() + " " + customerBean.getPostalCode();
        } else if (!customerBean.getDistrict().equalsIgnoreCase("") && customerBean.getPostalCode().equalsIgnoreCase("")) {
            disticVal = customerBean.getDistrict();
        } else if (customerBean.getDistrict().equalsIgnoreCase("") && !customerBean.getPostalCode().equalsIgnoreCase("")) {
            disticVal = customerBean.getPostalCode();
        } else {
            disticVal = "";
        }

        String addressVa = "";
        if (!customerBean.getAddress1().equalsIgnoreCase("")) {
            addressVa = customerBean.getAddress1();
        }

        if (!customerBean.getAddress2().equalsIgnoreCase("")) {
            addressVa = addressVa + "," + customerBean.getAddress2();
        }

        if (!customerBean.getAddress3().equalsIgnoreCase("")) {
            addressVa = addressVa + "," + customerBean.getAddress3();
        }
        if (!customerBean.getAddress4().equalsIgnoreCase("")) {
            addressVa = addressVa + "," + customerBean.getAddress3();
        }
        if (!cityVal.equalsIgnoreCase("") && !state.equalsIgnoreCase("")) {
            ((CustomerListViewHolder) viewHolder).tv_address2.setText(this.getString(R.string.str_concat_two_texts_with_coma, addressVa, "\n" + state + "\n" + cityVal + "\n" + disticVal));
        } else {
            ((CustomerListViewHolder) viewHolder).tv_address2.setText(this.getString(R.string.str_concat_two_texts_with_coma, addressVa, "\n" + disticVal));
        }

        if (customerBean.getCustomerName().length() > 0)
            ((CustomerListViewHolder) viewHolder).tvName.setText(String.valueOf(customerBean.getCustomerName().trim().charAt(0)).toUpperCase());
        ((CustomerListViewHolder) viewHolder).mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CustomerListViewHolder) viewHolder).detailsLayout.getVisibility() == View.VISIBLE)
                    ((CustomerListViewHolder) viewHolder).detailsLayout.setVisibility(View.GONE);
                else
                    ((CustomerListViewHolder) viewHolder).detailsLayout.setVisibility(View.VISIBLE);
            }
        });

        ((CustomerListViewHolder) viewHolder).tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Intent intentRetailerDetails = new Intent(CustomerListActivity.this, CustomerDetailsActivity.class);
                intentRetailerDetails.putExtra(Constants.RetailerName, customerBean.getCustomerName());
                intentRetailerDetails.putExtra(Constants.PostalCode, customerBean.getPostalCode());
                intentRetailerDetails.putExtra(Constants.CPNo, customerBean.getCustomerId());

                intentRetailerDetails.putExtra(Constants.Address, customerBean.getAddress1());
                intentRetailerDetails.putExtra("MobileNo", customerBean.getMobile1());
                // intentRetailerDetails.putExtra(Constants.VisitCatID,);

                if (comingFrom.equalsIgnoreCase(Constants.ProspectiveCustomerList)) {
                    intentRetailerDetails.putExtra(Constants.comingFrom, Constants.ProspectiveCustomerList);
                }else if (comingFrom.equalsIgnoreCase(Constants.MTPList)) {
                    intentRetailerDetails.putExtra(Constants.comingFrom, comingFrom);
                } else {
                    intentRetailerDetails.putExtra(Constants.comingFrom, Constants.RetailerList);
                }

                intentRetailerDetails.putExtra(Constants.NAVFROM, Constants.Retailer);
                //        intentRetailerDetails.putExtra(Constants.CPGUID, customerBean.getCPGUID());
                Constants.VisitNavigationFrom = "";
                startActivity(intentRetailerDetails);*/
                onItemClickIntent(customerBean);
            }
        });

       /* ((CustomerListViewHolder) viewHolder).tvRetailerName.setText(customerBean.getCustomerName());

        ((CustomerListViewHolder) viewHolder).tvRetailerCatTypeDesc.setText("");
        if (!customerBean.getCustomerId().equalsIgnoreCase("") && !customerBean.getCity().equalsIgnoreCase("")) {
            ((CustomerListViewHolder) viewHolder).tv_retailer_mob_no.setText(customerBean.getCustomerId() + " , " + customerBean.getCity());
        } else {
            ((CustomerListViewHolder) viewHolder).tv_retailer_mob_no.setText(customerBean.getCustomerId() + " " + customerBean.getCity());
        }

        final RecyclerView.ViewHolder testView = viewHolder; */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        View view = mSearchView.findViewById(androidx.appcompat.R.id.search_plate);
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.transperant));
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getComponentName());
        MenuItem dateFilter = menu.findItem(R.id.filter);
     /*   if (TextUtils.isEmpty("")) {
            dateFilter.setVisible(true);
        } else {*/
        dateFilter.setVisible(false);
        //}
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.lbl_cust_name_search));
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
        presenter.onSearch("");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                //   presenter.onFilter();
                return true;
            case android.R.id.home:
                if (mSearchView != null) {
                    if (!mSearchView.isIconified()) {
                        mSearchView.setIconified(true);
                    } else {
                        finish();
                    }
                }
                return true;
            case R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


