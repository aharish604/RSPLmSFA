package com.rspl.sf.msfa.attendance.attendancesummary;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;

import java.util.ArrayList;

public class AttendanceSummaryActivity extends AppCompatActivity implements AttendanceSummaryView, AdapterInterface<AttendanceSummaryBean> , SwipeRefreshLayout.OnRefreshListener{

    Toolbar toolbar;
    AttendanceSummaryPresentImpl summaryPresent = null;
    private RecyclerView recyclerView;
    private TextView noRecordFound;
    SwipeRefreshLayout swipeRefresh;
    private SimpleRecyclerViewAdapter<AttendanceSummaryBean> simpleRecyclerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_summary);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_attendance_Sum), 0);
        init();
    }

    private void init() {
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        noRecordFound = (TextView) findViewById(R.id.no_record_found);
//        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<AttendanceSummaryBean>(AttendanceSummaryActivity.this, R.layout.att_sum_item_list, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(simpleRecyclerViewAdapter);
        ConstantsUtils.setProgressColor(this, swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
//        displayRefreshTime(ConstantsUtils.getLastSeenDateFormat(getApplicationContext(), ConstantsUtils.getCurrentTimeLong()));
        try {
            displayRefreshTime(ConstantsUtils.getLastSeenDateFormat(getApplicationContext(), ConstantsUtils.getMilliSeconds(
                    ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.Attendances, Constants.TimeStamp, getApplicationContext()))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        summaryPresent = new AttendanceSummaryPresentImpl(this,this);
        summaryPresent.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
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
        });
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
    public void displayList(ArrayList<AttendanceSummaryBean> list) {
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
    public void onItemClick(AttendanceSummaryBean attendanceSummaryBean, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new AttendanceSummaryVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, AttendanceSummaryBean attendanceSummaryBean) {
        ((AttendanceSummaryVH)viewHolder).tvSPName.setText(attendanceSummaryBean.getSPName());
        ((AttendanceSummaryVH)viewHolder).tvRSCode.setText(attendanceSummaryBean.getCreatedBy());
        if(!TextUtils.isEmpty(attendanceSummaryBean.getStartTime())) {
            ((AttendanceSummaryVH) viewHolder).tvStartTime.setText(attendanceSummaryBean.getStartTime());
        }else {
            ((AttendanceSummaryVH) viewHolder).tvStartTime.setText("-");
        }
        ((AttendanceSummaryVH) viewHolder).tvTimeDiff.setText(attendanceSummaryBean.getTimeDiff());
        if(!TextUtils.isEmpty(attendanceSummaryBean.getEndTime())) {
            ((AttendanceSummaryVH) viewHolder).tvEndTime.setText(attendanceSummaryBean.getEndTime());
        }else {
            ((AttendanceSummaryVH) viewHolder).tvEndTime.setText("-");
        }
        int time = 0;

        try {
            if(!TextUtils.isEmpty(attendanceSummaryBean.getTotalWorkingHour())){
                time = Integer.parseInt(attendanceSummaryBean.getTotalWorkingHour());
            }else {
                time =0;
            }
        } catch (NumberFormatException e) {
            time = 0;
            e.printStackTrace();
        }

        if(!TextUtils.isEmpty(attendanceSummaryBean.getStartTime()) && TextUtils.isEmpty(attendanceSummaryBean.getEndTime())){
            ((AttendanceSummaryVH) viewHolder).tvTimeDiff.setTextColor(getResources().getColor(R.color.OpenColor));
        }else if(time>=8){
            ((AttendanceSummaryVH) viewHolder).tvTimeDiff.setTextColor(getResources().getColor(R.color.InvStatusGreen));
        }else {
            ((AttendanceSummaryVH) viewHolder).tvTimeDiff.setTextColor(getResources().getColor(R.color.RED));
        }
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
