package com.rspl.sf.msfa.soapproval.history;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.so.SOUtils;
import com.rspl.sf.msfa.soapproval.TaskHistoryVH;
import com.rspl.sf.msfa.solist.SOListBean;
import com.rspl.sf.msfa.solist.SOTaskHistoryBean;

import java.util.ArrayList;

public class SOApproveHistoryActivity extends AppCompatActivity implements SOApprovalHistoryView, SwipeRefreshLayout.OnRefreshListener {

    private Toolbar toolbar;
    private CardView soApprovalHist;
    private RecyclerView rvApprovalHistory;
    private TextView noRFApprovalHistory;
    private SimpleRecyclerViewAdapter<SOTaskHistoryBean> simpleApprovalHistoryAdapter;
    private SOApprovalHistoryPresImpl presenter;
    private SwipeRefreshLayout swipeRefresh;
    private SOListBean soListBean=null;
    private TextView tvSONo;
    private TextView tvOrderType;
    private ImageView ivDeliveryStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soapprove_history);
        Intent intent = getIntent();
        if (intent!=null){
            soListBean = (SOListBean)intent.getSerializableExtra(Constants.EXTRA_SO_DETAIL);
        }
        if (soListBean==null){
            soListBean=new SOListBean();
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.approval_history_title), 0);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        ConstantsUtils.setProgressColor(SOApproveHistoryActivity.this, swipeRefresh);
        soApprovalHist = (CardView) findViewById(R.id.soApprovalHist);
        rvApprovalHistory = (RecyclerView) findViewById(R.id.rvApprovalHistory);
        noRFApprovalHistory = (TextView) findViewById(R.id.no_record_found);
        tvSONo = (TextView) findViewById(R.id.tvSONo);
        tvOrderType = (TextView) findViewById(R.id.tvOrderType);
        ivDeliveryStatus = (ImageView) findViewById(R.id.ivDeliveryStatus);
        soApprovalHist.setVisibility(View.GONE);

        Drawable delvStatusImg = SOUtils.displayStatusImage(soListBean.getStatus(), soListBean.getDelvStatus(), SOApproveHistoryActivity.this);
        if (delvStatusImg!=null)
        ivDeliveryStatus.setImageDrawable(delvStatusImg);
        tvSONo.setText(soListBean.getSONo());
        tvOrderType.setText(getString(R.string.po_details_display_value, soListBean.getOrderTypeDesc(), soListBean.getOrderType()));
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        rvApprovalHistory.setLayoutManager(linearLayoutManager1);
        rvApprovalHistory.setHasFixedSize(true);
        displayApprovalHistory();
        presenter = new SOApprovalHistoryPresImpl(SOApproveHistoryActivity.this, this, soListBean);
        presenter.onStart();

    }

    private void displayApprovalHistory() {
        simpleApprovalHistoryAdapter = new SimpleRecyclerViewAdapter<SOTaskHistoryBean>(SOApproveHistoryActivity.this, R.layout.approval_item, new AdapterInterface<SOTaskHistoryBean>() {
            @Override
            public void onItemClick(SOTaskHistoryBean o, View view, int i) {
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
                return new TaskHistoryVH(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, SOTaskHistoryBean o) {
                int resImg = R.drawable.ic_hourglass_empty_black_24dp;
                int resColor = R.color.InvStatusOrange;
                if (o.getActionName().equalsIgnoreCase(getString(R.string.details_approved))) {
                    resImg = R.drawable.ic_done_black_24dp;
                    resColor = R.color.InvStatusGreen;
                } else if (o.getActionName().equalsIgnoreCase(getString(R.string.details_rejected))) {
                    resImg = R.drawable.ic_close_black_24dp;
                    resColor = R.color.InvStatusRed;
                }
                Drawable drawable = ContextCompat.getDrawable(SOApproveHistoryActivity.this, resImg);
                drawable.setColorFilter(ContextCompat.getColor(SOApproveHistoryActivity.this, resColor), PorterDuff.Mode.SRC_IN);
                ((TaskHistoryVH) viewHolder).ivStatus.setImageDrawable(drawable);
                ((TaskHistoryVH) viewHolder).tvActionName.setText(o.getPerformedByName());
                if(!o.getTimestamp().equalsIgnoreCase("null")) {
                    ((TaskHistoryVH) viewHolder).tvCustomerName.setText(o.getTimestamp());
                }else {
                    ((TaskHistoryVH) viewHolder).tvCustomerName.setText("");
                }
                ((TaskHistoryVH) viewHolder).tvRemarks.setText(o.getComments());
            }
        }, rvApprovalHistory, noRFApprovalHistory);
        rvApprovalHistory.setAdapter(simpleApprovalHistoryAdapter);
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return true;
        }
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
    public void displayResult(ArrayList<SOTaskHistoryBean> soTaskHistoryBeanArrayList) {
        soApprovalHist.setVisibility(View.VISIBLE);
        simpleApprovalHistoryAdapter.refreshAdapter(soTaskHistoryBeanArrayList);
    }

    @Override
    public void showMessage(String msg) {
        ConstantsUtils.displayLongToast(SOApproveHistoryActivity.this, msg);
    }

    @Override
    public void onRefresh() {
        presenter.onStart();
    }
}
