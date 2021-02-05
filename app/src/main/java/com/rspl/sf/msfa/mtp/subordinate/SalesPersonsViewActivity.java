package com.rspl.sf.msfa.mtp.subordinate;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.SalesPersonBean;
import com.rspl.sf.msfa.mtp.subordinate.mtpfilter.MTPSubOrdinateFilterActivity;
import com.rspl.sf.msfa.ui.FlowLayout;

import java.util.ArrayList;

public class SalesPersonsViewActivity extends AppCompatActivity implements SalesPersonsView, SwipeRefreshLayout.OnRefreshListener, AdapterInterface<SalesPersonBean> {

    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout llFlowLayout;
    private FlowLayout flowLayout;
    private RecyclerView recyclerView;
    private SalesPersonsPresenterImpl presenter;
    private SimpleRecyclerViewAdapter<SalesPersonBean> simpleRVAdapter;
    private ArrayList<String> filterList = new ArrayList<>();
    private TextView noRecordFound;
    private SearchView mSearchView = null;
    private String comingFrom = "";
    private String status = "";
    LinearLayout linearLayoutFlowLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_person_view);
        Intent intent = getIntent();
        if (intent != null) {
            comingFrom = intent.getStringExtra(ConstantsUtils.EXTRA_COMING_FROM);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (comingFrom.equalsIgnoreCase(ConstantsUtils.RTGS_SUBORDINATE)) {
            ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.rtgs_sub_ord_title), 0);
        } else {
            ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.mtp_sub_ord_title), 0);
        }
        initUI();
    }

    private void initUI() {
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        llFlowLayout = (LinearLayout) findViewById(R.id.llFilterLayout);
        flowLayout = (FlowLayout) findViewById(R.id.llFlowLayout);
        ConstantsUtils.setProgressColor(SalesPersonsViewActivity.this, swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        noRecordFound = (TextView) findViewById(R.id.no_record_found);
        swipeRefresh.setOnRefreshListener(this);
        linearLayoutFlowLayout = (LinearLayout) findViewById(R.id.llFilterLayout);
        flowLayout = (FlowLayout) findViewById(R.id.llFlowLayout);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SalesPersonsViewActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        simpleRVAdapter = new SimpleRecyclerViewAdapter<SalesPersonBean>(SalesPersonsViewActivity.this, R.layout.sales_person_item, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(simpleRVAdapter);
        presenter = new SalesPersonsPresenterImpl(SalesPersonsViewActivity.this, this,comingFrom);
        if (comingFrom.equalsIgnoreCase(ConstantsUtils.RTGS_SUBORDINATE)) {
            presenter.onStart();
        }
        else{
            presenter.getSalesPersonsFromOnline();
        }
    }

    @Override
    public void displayMsg(String msg) {
        ConstantsUtils.displayLongToast(SalesPersonsViewActivity.this, msg);
    }

    @Override
    public void showProgress() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void setFilterDate(String data) {
        try {
            if (data != null && !data.equalsIgnoreCase("")) {
                linearLayoutFlowLayout.setVisibility(View.VISIBLE);
            } else {
                linearLayoutFlowLayout.setVisibility(View.GONE);
            }
            String[] filterTypeArr = data.split(", ");
            ConstantsUtils.displayFilter(filterTypeArr, flowLayout, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void displayLastRefreshedTime(String refreshTime) {
        String lastRefresh = "";
        if (!TextUtils.isEmpty(refreshTime)) {
            lastRefresh = getString(R.string.po_last_refreshed) + " " + refreshTime;
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(lastRefresh);
        }
    }

    ArrayList<SalesPersonBean> tempSalesPersonBeanArrayLis = new ArrayList<>();
    @Override
    public void displayList(ArrayList<SalesPersonBean> salesPersonBeanArrayList) {
        this.tempSalesPersonBeanArrayLis=salesPersonBeanArrayList;
        simpleRVAdapter.refreshAdapter(salesPersonBeanArrayList);
    }

    @Override
    public void displayFilter(ArrayList<String> filterList) {
        this.filterList = filterList;
//        dateFilter.setVisible(true);
        if(filterList.size()>1){
            if(dateFilter!=null){
                dateFilter.setVisible(true);
            }
        }else {
            if(dateFilter!=null){
                dateFilter.setVisible(false);
            }
        }
    }

    @Override
    public void onRefresh() {
        if (comingFrom.equalsIgnoreCase(ConstantsUtils.RTGS_SUBORDINATE)) {
            presenter.onRefresh();
        } else {
            presenter.getSalesPersonsFromOnline();
        }

    }

    @Override
    public void onItemClick(SalesPersonBean salesPersonBean, View view, int i) {
        if (ConstantsUtils.isAutomaticTimeZone(SalesPersonsViewActivity.this)) {
            if (comingFrom.equalsIgnoreCase(ConstantsUtils.MTP_SUBORDINATE)) {
                Intent intent = new Intent(SalesPersonsViewActivity.this, MTPSubOrdActivity.class);
                intent.putExtra(ConstantsUtils.EXTRA_SPGUID, salesPersonBean.getSPGUID());
                startActivity(intent);
            } else {
                Intent intent = new Intent(SalesPersonsViewActivity.this, RTGSSubOrdActivity.class);
                intent.putExtra(ConstantsUtils.EXTRA_SPGUID, salesPersonBean.getSPGUID());
                intent.putExtra(ConstantsUtils.EXTRA_ExternalRefID, salesPersonBean.getExternalRefID());
                startActivity(intent);
            }
        } else {
            ConstantsUtils.showAutoDateSetDialog(SalesPersonsViewActivity.this);
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new SalesPersonVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, SalesPersonBean salesPersonBean) {
        ((SalesPersonVH) viewHolder).tvSPName.setText(salesPersonBean.getFirstName() + " " + salesPersonBean.getLastName() + " ("+salesPersonBean.getExternalRefID()+")");
        ((SalesPersonVH) viewHolder).tvSPPhone.setText(salesPersonBean.getMobileNo());
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    MenuItem dateFilter = null;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        View view = mSearchView.findViewById(androidx.appcompat.R.id.search_plate);
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.transperant));
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getComponentName());
        dateFilter = menu.findItem(R.id.filter);
        /*if(comingFrom.equalsIgnoreCase(ConstantsUtils.RTGS_SUBORDINATE)){
            dateFilter.setVisible(false);
        }else {
            dateFilter.setVisible(true);
        }*/
        dateFilter.setVisible(false);
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mSearchView != null) {
                    if (!mSearchView.isIconified()) {
                        mSearchView.setIconified(true);
                    } else {
                        finish();
                    }
                }
                return true;
            case R.id.filter:
                Intent intent = new Intent(this, MTPSubOrdinateFilterActivity.class);
                intent.putStringArrayListExtra(Constants.FilterList,(ArrayList<String>)filterList);
                intent.putExtra(Constants.Status_ID,status);
                startActivityForResult(intent, ConstantsUtils.ACTIVITY_RESULT_FILTER);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ConstantsUtils.ACTIVITY_RESULT_FILTER) {
            status = data.getStringExtra(Constants.Status_ID);
            if(status.equalsIgnoreCase("All")){
                status = "";
            }
            if (TextUtils.isEmpty(status)) {
                linearLayoutFlowLayout.setVisibility(View.GONE);
            } else {
                linearLayoutFlowLayout.setVisibility(View.VISIBLE);
            }
            presenter.startFilter(status);
        }
    }
}
