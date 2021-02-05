package com.rspl.sf.msfa.claimreports;

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
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.ConstantsUtils;

import java.util.ArrayList;

public class ClaimReportActivity extends AppCompatActivity implements ClaimReportView, AdapterInterface<ClaimReportBean> , SwipeRefreshLayout.OnRefreshListener{

    Toolbar toolbar;
    ClaimReportPresentImpl summaryPresent = null;
    private RecyclerView recyclerView;
    private TextView noRecordFound,totalMaxClaim,totalClaim;
    SwipeRefreshLayout swipeRefresh;
    private SimpleRecyclerViewAdapter<ClaimReportBean> simpleRecyclerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_report);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_Claim_Summary), 0);
        init();
    }

    private void init() {
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        noRecordFound = (TextView) findViewById(R.id.no_record_found);
        totalClaim = (TextView) findViewById(R.id.totalClaim);
        totalMaxClaim = (TextView) findViewById(R.id.totalMaxClaim);
//        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<ClaimReportBean>(ClaimReportActivity.this, R.layout.claim_sum_item_list, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(simpleRecyclerViewAdapter);
        ConstantsUtils.setProgressColor(this, swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
//        displayRefreshTime(ConstantsUtils.getLastSeenDateFormat(getApplicationContext(), ConstantsUtils.getCurrentTimeLong()));
        summaryPresent = new ClaimReportPresentImpl(this,this);
        summaryPresent.onStart();
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
            displayRefreshTime(ConstantsUtils.getLastSeenDateFormat(getApplicationContext(), ConstantsUtils.getCurrentTimeLong()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        totalClaim.setText(UtilConstants.removeLeadingZerowithTwoDecimal(totalClaimAmt));
        totalMaxClaim.setText(UtilConstants.removeLeadingZerowithTwoDecimal(totalMaxClaimAmt));
        if(list!=null){
            simpleRecyclerViewAdapter.refreshAdapter(list);
        }
    }

    @Override
    public void onItemClick(ClaimReportBean ClaimReportBean, View view, int i) {
        summaryPresent.onItemClick(ClaimReportBean);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new ClaimReportVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, ClaimReportBean ClaimReportBean) {
        ((ClaimReportVH) viewHolder).tvSPName.setText(ClaimReportBean.getParentName());
        ((ClaimReportVH) viewHolder).tvRSCode.setText(ClaimReportBean.getParentNo());
        ((ClaimReportVH) viewHolder).tvclaimAmount.setText(UtilConstants.removeLeadingZerowithTwoDecimal(ClaimReportBean.getTotalClaimAmount()));
        ((ClaimReportVH) viewHolder).maxClaimAmount.setText(UtilConstants.removeLeadingZerowithTwoDecimal(ClaimReportBean.getTotalMaxClaimAmt()));
    }

    @Override
    public void onRefresh() {
        try {
            summaryPresent.onStart();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
