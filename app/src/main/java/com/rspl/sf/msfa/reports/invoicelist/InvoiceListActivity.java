package com.rspl.sf.msfa.reports.invoicelist;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.filter.DateFilterFragment;
import com.rspl.sf.msfa.reports.InvoiceHistory.invocieFilter.InvoiceFilterActivity;
import com.rspl.sf.msfa.reports.invoicelist.invoiceDetails.InvoiceDetailsActivity;
import com.rspl.sf.msfa.so.SOUtils;
import com.rspl.sf.msfa.ui.FlowLayout;

import java.util.ArrayList;

/**
 * Created by e10604 on 25/1/2018.
 */

public class InvoiceListActivity extends AppCompatActivity implements IInvoiceListViewPresenter, SwipeRefreshLayout.OnRefreshListener, AdapterInterface<InvoiceListBean> {

    // android components
    SwipeRefreshLayout swipeRefresh;
    RecyclerView recyclerView;
    TextView no_record_found;
    Toolbar toolbar;
    SimpleRecyclerViewAdapter<InvoiceListBean> recyclerViewAdapter = null;
    LinearLayout linearLayoutFlowLayout;
    // variables
    InvoiceListPresenter presenter;
    InvoiceListBean invoiceListBean;
    View viewLayout = null;
    private FlowLayout flowLayout;
    private Bundle bundleExtras;
    private String mStrBundleRetID = "", mStrBundleCPGUID = "";
    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";
    private boolean isInvoiceItemsEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_list_mvp);

        bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            isInvoiceItemsEnabled = bundleExtras.getBoolean(Constants.isInvoiceItemsEnabled);
        }

        initializeUI(this);
    }

    @Override
    public void initializeUI(Context context) {
        viewLayout = findViewById(android.R.id.content);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setDistanceToTriggerSync(ConstantsUtils.SWIPE_REFRESH_DISTANCE);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        no_record_found = (TextView) findViewById(R.id.no_record_found);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        linearLayoutFlowLayout = (LinearLayout) findViewById(R.id.llFilterLayout);
        flowLayout = (FlowLayout) findViewById(R.id.llFlowLayout);
        ConstantsUtils.setProgressColor(this, swipeRefresh);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_invoice_History), 0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initializeClickListeners();
        initializeObjects(this);
        initializeRecyclerViewItems(new LinearLayoutManager(this));

    }

    @Override
    public void initializeClickListeners() {
        swipeRefresh.setOnRefreshListener(this);
    }

    @Override
    public void initializeObjects(Context context) {
        presenter = new InvoiceListPresenter(this, this, this, mStrBundleRetID, mStrBundleCPGUID);
        if (isInvoiceItemsEnabled) {
            presenter.getInvoiceItemsList();
        } else {
            presenter.getInvoiceList();
        }
        displayRefreshTime(ConstantsUtils.getLastSeenDateFormat(getApplicationContext(), ConstantsUtils.getMilliSeconds(
                ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.InvoiceItems, Constants.TimeStamp, getApplicationContext()))));
    }

    @Override
    public void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new SimpleRecyclerViewAdapter<>(this, R.layout.recycler_view_invoice_list, this, recyclerView, no_record_found);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getComponentName());
        MenuItem dateFilter = menu.findItem(R.id.filter);
        if (TextUtils.isEmpty("")) {
            dateFilter.setVisible(true);
        } else {
            dateFilter.setVisible(false);
        }
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.lbl_inv_no_search));
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
                presenter.onFilter();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showMessage(String message) {
        ConstantsUtils.displayLongToast(InvoiceListActivity.this, message);
    }

    @Override
    public void dialogMessage(String message, String msgType) {

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
    public void openFilter(String startDate, String endDate, String filterType, String status, String grStatus) {
        Intent intent = new Intent(this, InvoiceFilterActivity.class);
        intent.putExtra(DateFilterFragment.EXTRA_DEFAULT, filterType);
        intent.putExtra(DateFilterFragment.EXTRA_START_DATE, startDate);
        intent.putExtra(DateFilterFragment.EXTRA_END_DATE, endDate);
        intent.putExtra(InvoiceFilterActivity.EXTRA_INVOICE_STATUS, status);
        intent.putExtra(InvoiceFilterActivity.EXTRA_INVOICE_GR_STATUS, grStatus);
        startActivityForResult(intent, ConstantsUtils.ACTIVITY_RESULT_FILTER);
    }

    @Override
    public void searchResult(ArrayList<InvoiceListBean> reqBeanArrayList) {
        recyclerViewAdapter.refreshAdapter(reqBeanArrayList);
    }

    @Override
    public void displayResult(ArrayList<InvoiceListBean> feedbackBeanArrayList) {
        recyclerViewAdapter.refreshAdapter(feedbackBeanArrayList);
    }

    @Override
    public void setFilterDate(String filterType) {
        try {
            if (filterType != null && !filterType.equalsIgnoreCase("")) {
                linearLayoutFlowLayout.setVisibility(View.VISIBLE);
            } else {
                linearLayoutFlowLayout.setVisibility(View.GONE);
            }
            String[] filterTypeArr = filterType.split(", ");
            ConstantsUtils.displayFilter(filterTypeArr, flowLayout, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void invoiceDetails(InvoiceListBean invoiceListBean) {
        Intent toInvoiceHisdetails = new Intent(this, InvoiceDetailsActivity.class);
        toInvoiceHisdetails.putExtra(Constants.CPNo, mStrBundleRetID);
        toInvoiceHisdetails.putExtra(Constants.RetailerName, mStrBundleRetName);
        toInvoiceHisdetails.putExtra(Constants.CPUID, mStrBundleRetUID);
        toInvoiceHisdetails.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        toInvoiceHisdetails.putExtra(Constants.INVOICE_ITEM, invoiceListBean);
        startActivity(toInvoiceHisdetails);
    }

    @Override
    public void invoiceListFresh() {
        try {
            if (isInvoiceItemsEnabled) {
                presenter.getInvoiceItemsList();
            } else {
                presenter.getInvoiceList();
            }
            displayRefreshTime(ConstantsUtils.getLastSeenDateFormat(getApplicationContext(), ConstantsUtils.getMilliSeconds(
                    ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.InvoiceItems, Constants.TimeStamp, getApplicationContext()))));

        } catch (Throwable e) {

            e.printStackTrace();
        }
    }

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
    public void onRefresh() {
        try {
            presenter.onRefresh();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(InvoiceListBean reqBean, View view, int i) {
        this.invoiceListBean = reqBean;
        presenter.getInvoiceDetails(reqBean.getInvoiceNo());

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new InvoiceListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i, InvoiceListBean invoiceBean) {
        ((InvoiceListViewHolder) viewHolder).textViewInvoiceDate.setText(invoiceBean.getInvoiceDate());
        ((InvoiceListViewHolder) viewHolder).textViewInvoiceNumber.setText(invoiceBean.getInvoiceNo());
        ((InvoiceListViewHolder) viewHolder).textViewInvoiceAmount.setText((ConstantsUtils.commaSeparator(invoiceBean.getNetAmount(), invoiceBean.getCurrency()) + " " + invoiceBean.getCurrency()).trim());

        if (isInvoiceItemsEnabled) {
            ((InvoiceListViewHolder) viewHolder).textViewMaterialName.setVisibility(View.VISIBLE);
            ((InvoiceListViewHolder) viewHolder).textViewQuantity.setVisibility(View.VISIBLE);
            ((InvoiceListViewHolder) viewHolder).textViewMaterialName.setText(invoiceBean.getMaterialDesc());
            ((InvoiceListViewHolder) viewHolder).textViewQuantity.setText(invoiceBean.getQuantity() + " " + invoiceBean.getUOM());
        } else {
            ((InvoiceListViewHolder) viewHolder).textViewMaterialName.setVisibility(View.GONE);
            ((InvoiceListViewHolder) viewHolder).textViewQuantity.setVisibility(View.GONE);
        }
        Drawable delvStatusImg = SOUtils.displayInvoiceStatusImage(invoiceBean.getInvoiceStatus(), invoiceBean.getDueDateStatus(), this);
        if (delvStatusImg != null) {
            ((InvoiceListViewHolder) viewHolder).imageViewInvoiceStatus.setImageDrawable(delvStatusImg);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ConstantsUtils.ACTIVITY_RESULT_FILTER) {
            String soStatus = data.getStringExtra(InvoiceFilterActivity.EXTRA_INVOICE_STATUS);
            String delvStatus = data.getStringExtra(InvoiceFilterActivity.EXTRA_INVOICE_GR_STATUS);
            if (TextUtils.isEmpty(soStatus) && TextUtils.isEmpty(delvStatus)) {
                linearLayoutFlowLayout.setVisibility(View.GONE);
            } else {
                linearLayoutFlowLayout.setVisibility(View.VISIBLE);
            }
            presenter.startFilter(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}
