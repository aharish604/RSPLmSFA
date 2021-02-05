package com.rspl.sf.msfa.mtp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterViewInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewTypeAdapter;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.collectionPlan.HeaderVH;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;

import java.util.ArrayList;

public class MTPDetailsActivity extends AppCompatActivity implements AdapterViewInterface<MTPRoutePlanBean> {

    private ArrayList<MTPRoutePlanBean> mtpRoutePlanBeanArrayList = null;
    private RecyclerView recyclerView;
    private SimpleRecyclerViewTypeAdapter<MTPRoutePlanBean> simpleAdapter;
    private String comingFrom = "";
    private TextView tvActivityDesc;
    private TextView tvRemarks;
    private ConstraintLayout clHeader;
    private TextView tvDate;
    private TextView tvDay;
    private boolean isAsmLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mtp_details);
        Intent intent = getIntent();
        if (intent != null) {
            mtpRoutePlanBeanArrayList = (ArrayList<MTPRoutePlanBean>) intent.getSerializableExtra(Constants.EXTRA_BEAN);
            comingFrom = intent.getStringExtra(ConstantsUtils.EXTRA_COMING_FROM);
            isAsmLogin = intent.getBooleanExtra(ConstantsUtils.EXTRA_ISASM_LOGIN, false);
        }
        if (mtpRoutePlanBeanArrayList == null) {
            mtpRoutePlanBeanArrayList = new ArrayList<>();
        }
        intiUI();
    }

    private void intiUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        tvActivityDesc = (TextView) findViewById(R.id.tvActivityDesc);
        tvRemarks = (TextView) findViewById(R.id.tvRemarks);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvDay = (TextView) findViewById(R.id.tvDay);
        clHeader = (ConstraintLayout) findViewById(R.id.clHeader);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        simpleAdapter = new SimpleRecyclerViewTypeAdapter<MTPRoutePlanBean>(MTPDetailsActivity.this, R.layout.mtp_today_plan_item, this, null, null);
        recyclerView.setAdapter(simpleAdapter);
        simpleAdapter.refreshAdapter(mtpRoutePlanBeanArrayList);

        String date = "";
        if (!mtpRoutePlanBeanArrayList.isEmpty()) {
            MTPRoutePlanBean mtpRoutePlanBean = mtpRoutePlanBeanArrayList.get(0);
            date = mtpRoutePlanBean.getVisitDate();
            tvRemarks.setText(mtpRoutePlanBean.getRemarks());
            tvActivityDesc.setText(mtpRoutePlanBean.getActivityDec());
            tvDate.setText(mtpRoutePlanBean.getDate());
            tvDay.setText(mtpRoutePlanBean.getDay());
            clHeader.setVisibility(View.VISIBLE);
        } else {
            clHeader.setVisibility(View.GONE);
        }
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.mtp_details_title), 0);
    }

    @Override
    public void onItemClick(MTPRoutePlanBean mtpRoutePlanBean, View view, int i) {

    }

    @Override
    public int getItemViewType(int i, ArrayList<MTPRoutePlanBean> arrayList) {
//        if (isAsmLogin) {
        if (!TextUtils.isEmpty(arrayList.get(i).getSalesDistrict()) || !TextUtils.isEmpty(arrayList.get(i).getCustomerNo())) {
            return 0;
        } else {
            return 1;
        }
//        } else {
           /* if (!TextUtils.isEmpty(arrayList.get(i).getCustomerNo()))
                return 0;
            else
                return 1;*/
//        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        if (i == 0)
            return new MTPTodayVH(view);
        else {
            View viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.snippet_route_plan_item, viewGroup, false);
            return new HeaderVH(viewItem);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, final MTPRoutePlanBean mtpRoutePlanBean, ArrayList<MTPRoutePlanBean> arrayList) {
        if (viewHolder instanceof MTPTodayVH) {
            String custName = mtpRoutePlanBean.getCustomerName();
            if (TextUtils.isEmpty(custName)) {
                custName = mtpRoutePlanBean.getSalesDistrictDesc();
            }

            ((MTPTodayVH) viewHolder).tvName.setText(custName);
            try {
                String fChar = "";
                if (!TextUtils.isEmpty(custName)) {
                    fChar = String.valueOf(custName.charAt(0)).toUpperCase();
                }
                ((MTPTodayVH) viewHolder).tvHName.setText(fChar);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(mtpRoutePlanBean.getMobile1())) {
                ((MTPTodayVH) viewHolder).ivMobile.setVisibility(View.VISIBLE);
                ((MTPTodayVH) viewHolder).ivMobile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(mtpRoutePlanBean.getMobile1())) {
                            Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.tel_txt + (mtpRoutePlanBean.getMobile1())));
                            startActivity(dialIntent);
                        }
                    }
                });
            } else {
                ((MTPTodayVH) viewHolder).ivMobile.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(mtpRoutePlanBean.getCustomerNo())) {
                ((MTPTodayVH) viewHolder).tvDesc.setText(mtpRoutePlanBean.getCustomerNo() + " " + mtpRoutePlanBean.getCity());
                ((MTPTodayVH) viewHolder).tvDesc.setVisibility(View.VISIBLE);
            } else {
                ((MTPTodayVH) viewHolder).tvDesc.setVisibility(View.GONE);
            }
        } else if (viewHolder instanceof HeaderVH) {
            ((HeaderVH) viewHolder).tvWeekHeader.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_edit:
//                Intent intent = new Intent(MTPDetailsActivity.this, MTPCreateActivity.class);
//                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
