package com.rspl.sf.msfa.reports.behaviourlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.ViewPagerTabAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.ConfigTypesetTypesBean;
import com.rspl.sf.msfa.mbo.CustomerBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.ui.FlowLayout;

import java.util.ArrayList;


/**
 * Created by e10526 on 03-02-2017.
 */
public class BehaviourListActivity extends AppCompatActivity implements AdapterInterface<CustomerBean> {


    // android components
    RecyclerView recyclerView;
    TextView no_record_found;
    Toolbar toolbar;
    SimpleRecyclerViewAdapter<CustomerBean> recyclerViewAdapter = null;
    LinearLayout llFlowLayout;
    // variables
    BehaviourPresenter presenter;
    ArrayList<CustomerBean> customerBeanBeenFilterArrayList;
    private FlowLayout flowLayout;
    private ViewPager viewpagerBehaviour;
    private TabLayout tabLayoutBehaviour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behaviour_list);
        initializeUI(this);
        setTabs();
    }


    public void initializeUI(Context context) {
        llFlowLayout = (LinearLayout) findViewById(R.id.llFilterLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        no_record_found = (TextView) findViewById(R.id.no_record_found);
        flowLayout = (FlowLayout) findViewById(R.id.llFlowLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewpagerBehaviour = (ViewPager) findViewById(R.id.viewpagerBehaviour);
        tabLayoutBehaviour = (TabLayout) findViewById(R.id.tabLayoutBehaviour);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_behaviour_list), 0);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //initializeObjects(this);
        //initializeRecyclerViewItems(new LinearLayoutManager(this));

    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
    }




    @Override
    public void onItemClick(CustomerBean customerBean, View view, int i) {

        /* Intent intentReqDetails = new Intent(FeedbackListActivity.this, FeedBackDetailsActivity.class);
        intentReqDetails.putExtra(Constants.FeedBackGUID, feedbackBean.getFeedbackGUID());
        intentReqDetails.putExtra(Constants.FeedbackNo, feedbackBean.getFeedbackNo());
        intentReqDetails.putExtra(Constants.FeedbackTypeDesc, feedbackBean.getFeedbackDescription());
        intentReqDetails.putExtra("from", "FeedBack");
        startActivity(intentReqDetails);*/

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new BehaviourListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, CustomerBean customerBean) {
        ((BehaviourListViewHolder) viewHolder).textViewRetailerName.setText(customerBean.getRetailerName());
        ((BehaviourListViewHolder) viewHolder).textViewCustomerID.setText(customerBean.getCustomerId());
        ((BehaviourListViewHolder) viewHolder).textViewMtdValue.setText(customerBean.getPurchaseQty() + " " + customerBean.getUOM());
    }


    private void setTabs() {
        customerBeanBeenFilterArrayList = new ArrayList<>();
        String mStrConfigQry = Constants.ConfigTypesetTypes + "?$filter=" + Constants.Typeset + " eq '" + Constants.EVLTYP + "'"+ " &$orderby = Types asc";
        final ViewPagerTabAdapter adapter = new ViewPagerTabAdapter(getSupportFragmentManager());
        try {
            final ArrayList<ConfigTypesetTypesBean> configTypesetTypesBeanArrayList = (ArrayList<ConfigTypesetTypesBean>) OfflineManager.getStatusConfig(mStrConfigQry, Constants.ALL);
            for (int i = 0; i < configTypesetTypesBeanArrayList.size(); i++) {
                if (!configTypesetTypesBeanArrayList.get(i).getTypesName().equalsIgnoreCase(Constants.All) && !configTypesetTypesBeanArrayList.get(i).getTypes().equals("")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("behaviourStatusID", configTypesetTypesBeanArrayList.get(i).getTypes());
                    bundle.putInt("behaviourPosition", i);
                    if("000004".equals(configTypesetTypesBeanArrayList.get(i).getTypes())) {
                        UnbilledFragment behaviourFragment = new UnbilledFragment();
                        behaviourFragment.setArguments(bundle);
                        adapter.addFrag(behaviourFragment, configTypesetTypesBeanArrayList.get(i).getTypesName());
                    }else {
                        BehaviourFragment behaviourFragment = new BehaviourFragment();
                        behaviourFragment.setArguments(bundle);
                        adapter.addFrag(behaviourFragment, configTypesetTypesBeanArrayList.get(i).getTypesName());
                    }
                }
            }
            viewpagerBehaviour.setAdapter(adapter);
            viewpagerBehaviour.setOffscreenPageLimit(adapter.getCount()-1);
            tabLayoutBehaviour.setupWithViewPager(viewpagerBehaviour);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    public void setActionTitle(String lastRefresh) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(lastRefresh);
    }
}
