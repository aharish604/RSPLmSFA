package com.rspl.sf.msfa.claimreports;

import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;

import java.util.ArrayList;

public class ClaimReportDetailsActivity extends AppCompatActivity implements ClaimReportView, AdapterInterface<ClaimReportBean> , SwipeRefreshLayout.OnRefreshListener{

    Toolbar toolbar;
    ClaimReportPresentImpl summaryPresent = null;
    private RecyclerView recyclerView;
    private TextView noRecordFound,tvRSCode,tvSPName,tvclaimAmount,maxClaimAmount;
    SwipeRefreshLayout swipeRefresh;
    private SimpleRecyclerViewAdapter<ClaimReportBean> simpleRecyclerViewAdapter;
    private ArrayList<ClaimReportBean> claimReportBeans = new ArrayList<>();
    private ClaimReportBean claimReportBean = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_details_report);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_Claim_details), 0);
        init();
    }

    private void init() {
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        noRecordFound = (TextView) findViewById(R.id.no_record_found);
        tvSPName = (TextView) findViewById(R.id.tvSPName);
        tvRSCode = (TextView) findViewById(R.id.tvRSCode);
        tvclaimAmount = (TextView) findViewById(R.id.tvclaimAmount);
        maxClaimAmount = (TextView) findViewById(R.id.maxClaimAmount);
//        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<ClaimReportBean>(ClaimReportDetailsActivity.this, R.layout.claim_sum_item_details_list, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(simpleRecyclerViewAdapter);
        ConstantsUtils.setProgressColor(this, swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        /*try {
            displayRefreshTime(ConstantsUtils.getLastSeenDateFormat(getApplicationContext(), ConstantsUtils.getMilliSeconds(
                    ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.Attendances, Constants.TimeStamp, getApplicationContext()))));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        if(getIntent()!=null){
            Bundle bundle = getIntent().getExtras();
            claimReportBean = (ClaimReportBean) bundle.getSerializable(ConstantsUtils.CLAIM_SUMMARY);
        }
        if(claimReportBean!=null){
            claimReportBeans = claimReportBean.getClaimReportBeans();
            tvclaimAmount.setText(UtilConstants.removeLeadingZerowithTwoDecimal(claimReportBean.getTotalClaimAmount()));
            maxClaimAmount.setText(UtilConstants.removeLeadingZerowithTwoDecimal(claimReportBean.getTotalMaxClaimAmt()));
            tvSPName.setText(claimReportBean.getParentName());
            tvRSCode.setText(claimReportBean.getParentNo());
        }
        simpleRecyclerViewAdapter.refreshAdapter(claimReportBeans);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_search, menu);
        /*SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getComponentName());
        MenuItem dateFilter = menu.findItem(R.id.filter);
        dateFilter.setVisible(false);
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.so_ap_search_hint));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                summaryPresent.onSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                summaryPresent.onSearch(newText);
                return false;
            }
        });*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }

    @Override
    public void showMessage(String message) {
        ConstantsUtils.dialogBoxWithButton(this, "", message, getString(R.string.ok), "", null);
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void dialogMessage(String message) {

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
    public void displayRefreshTime(String refreshTime) {
        String lastRefresh = "";
        if (!TextUtils.isEmpty(refreshTime)) {
            lastRefresh = getString(R.string.po_last_refreshed) + " " + refreshTime;
        }
        if (lastRefresh != null)
            getSupportActionBar().setSubtitle(lastRefresh);
    }

    @Override
    public void displayList(ArrayList<ClaimReportBean> list, String totalClaimAmt, String totalMaxClaimAmt) {
        try {
            Constants.events.updateStatus(Constants.SYNC_TABLE,
                    Constants.Attendances, Constants.TimeStamp, Constants.getSyncHistoryddmmyyyyTime()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            displayRefreshTime(ConstantsUtils.getLastSeenDateFormat(getApplicationContext(), ConstantsUtils.getMilliSeconds(
                    ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.Attendances, Constants.TimeStamp, getApplicationContext()))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(list!=null){
            simpleRecyclerViewAdapter.refreshAdapter(list);
        }
    }

    @Override
    public void onItemClick(ClaimReportBean ClaimReportBean, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new ClaimDetailsReportVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, ClaimReportBean ClaimReportBean) {
        ((ClaimDetailsReportVH) viewHolder).tvSchemeDesc.setText(ClaimReportBean.getZSchemeTypeDesc() + " ("+claimReportBean.getZSchemeValidFrm() + " - "+claimReportBean.getZSchemeValidTo()+")");
        ((ClaimDetailsReportVH) viewHolder).totalMaxClaim.setText(UtilConstants.removeLeadingZerowithTwoDecimal(ClaimReportBean.getZMaxClaimAmt()));
        ((ClaimDetailsReportVH) viewHolder).totalClaim.setText(UtilConstants.removeLeadingZerowithTwoDecimal(ClaimReportBean.getClaimAmount()));
    }

    @Override
    public void onRefresh() {
       swipeRefresh.setRefreshing(false);
    }
}
