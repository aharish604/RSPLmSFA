package com.rspl.sf.msfa.mtp.approval;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mtp.MTPHeaderBean;

import java.util.ArrayList;

public class MTPApprovalActivity extends AppCompatActivity implements IMTPApprovalViewPresenter, AdapterInterface<MTPApprovalBean>, SwipeRefreshLayout.OnRefreshListener {
    // android components
    RecyclerView recyclerView;
    TextView textViewNoRecordFound;
    SimpleRecyclerViewAdapter<MTPApprovalBean> recyclerViewAdapter;
    ProgressDialog progressDialog = null;
    Toolbar toolbar;
    // variables
    ArrayList<MTPApprovalBean> mtpApprovalBeanArrayList;
    MTPApprovalPresenter presenter;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mtpapproval);
        initializeUI(this);
    }

    @Override
    public void initializeUI(Context context) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.menu_mtp_approval), 0);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        textViewNoRecordFound = (TextView) findViewById(R.id.no_record_found);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        ConstantsUtils.setProgressColor(this, swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        initializeClickListeners();
        initializeObjects(this);
        initializeRecyclerViewItems(new LinearLayoutManager(this));
    }

    @Override
    public void initializeClickListeners() {

    }

    @Override
    public void initializeObjects(Context context) {
        mtpApprovalBeanArrayList = new ArrayList<>();
        presenter = new MTPApprovalPresenter(this, this, this);
        presenter.loadAsyncTask();
    }

    @Override
    public void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new SimpleRecyclerViewAdapter<>(this, R.layout.recycler_view_mtp_approval_list, this, recyclerView, textViewNoRecordFound);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void showProgressDialog() {
        // progressDialog = ConstantsUtils.showProgressDialog(this, getString(R.string.app_loading));
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideProgressDialog() {
        //  progressDialog.dismiss();
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void searchResult(ArrayList retailerSearchList) {

    }


    @Override
    public void refreshList() {
        mtpApprovalBeanArrayList = presenter.approvalBeanArrayList;
        // if (mtpApprovalBeanArrayList.size() > 0) {
        recyclerViewAdapter.refreshAdapter(mtpApprovalBeanArrayList);
        // }
        displayRefreshTime(ConstantsUtils.getLastSeenDateFormat(this, ConstantsUtils.getCurrentTimeLong()));
    }

    public void displayRefreshTime(String refreshTime) {
        String lastRefresh = "";
        if (!TextUtils.isEmpty(refreshTime)) {
            lastRefresh = getString(R.string.po_last_refreshed) + " " + refreshTime;
        }
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(lastRefresh);
    }

    @Override
    public void showMessage(Object o) {
        Toast.makeText(this, o.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideMessage() {

    }

    @Override
    public void openDetailScreen(ArrayList<MTPHeaderBean> mtpRoutePlanBeanArrayList, MTPApprovalBean mtpApprovalBean) {
        Intent intent = new Intent(this, MTPApprovalDetailActivity.class);
        intent.putExtra(Constants.EXTRA_BEAN, mtpRoutePlanBeanArrayList);
        intent.putExtra(ConstantsUtils.EXTRA_COMING_FROM, ConstantsUtils.MTP_APPROVAL);
        intent.putExtra(ConstantsUtils.ROUTE_INSTANCE_ID, mtpApprovalBean.getInstanceID());
        intent.putExtra(ConstantsUtils.ROUTE_ENTITY_KEY, mtpApprovalBean.getEntityKey());
        intent.putExtra(Constants.Initiator, mtpApprovalBean.getInitiator());
        startActivity(intent);
    }

    @Override
    public void onItemClick(MTPApprovalBean mtpApprovalBean, View view, int i) {
        /* open mtp detail screen*/
        presenter.mtpDetails(mtpApprovalBean);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new MTPApprovalListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, MTPApprovalBean mtpApprovalBean) {
        ((MTPApprovalListViewHolder) viewHolder).textViewOrderID.setText(mtpApprovalBean.getEntityAttribute5());
        if(!mtpApprovalBean.getEntityAttribute6().equalsIgnoreCase("")) {
            ((MTPApprovalListViewHolder) viewHolder).textViewOrderDate.setText(mtpApprovalBean.getEntityAttribute6() + "-" + mtpApprovalBean.getEntityAttribute7());
        }
        ((MTPApprovalListViewHolder) viewHolder).textViewMTPApprovalName.setText(mtpApprovalBean.getEntityAttribute1());
        ((MTPApprovalListViewHolder) viewHolder).imageViewStatus.setImageResource(R.drawable.ic_date_range_black_24dp);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        presenter.loadAsyncTask();
    }
}
