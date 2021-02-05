package com.rspl.sf.msfa.reports.behaviourlist;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.CustomerBean;

import java.util.ArrayList;

/**
 * Created by e10860 on 4/17/2018.
 */

public class UnbilledFragment extends Fragment implements IBehaviourListView, AdapterInterface<CustomerBean>, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView rvBehaviourList;
    private SimpleRecyclerViewAdapter<CustomerBean> simpleRecyclerViewAdapter;
    private TextView no_record_found;
    private String statusID = "";
    SwipeRefreshLayout swipeRefresh;
    private BehaviourPresenter behaviourPresenter;
    private boolean hasLoadedOnce = false;
    private int pos = 0;

    public UnbilledFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            statusID = bundle.getString("behaviourStatusID");
            pos = bundle.getInt("");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getActivity().getComponentName());
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        View view = mSearchView.findViewById(androidx.appcompat.R.id.search_plate);
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.transperant));
        MenuItem dateFilter = menu.findItem(R.id.filter);
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.lbl_search_by_retailer_name));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                behaviourPresenter.onSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                behaviourPresenter.onSearch(newText);
                return false;
            }
        });
        behaviourPresenter.onSearch("");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            // we check that the fragment is becoming visible
            if (isVisibleToUser && !hasLoadedOnce && pos == 3) {
                hasLoadedOnce = true;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_unbilled, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvBehaviourList = (RecyclerView) view.findViewById(R.id.rvBehaviourList);
        no_record_found = (TextView) view.findViewById(R.id.no_record_found);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvBehaviourList.setLayoutManager(linearLayoutManager);
        rvBehaviourList.setNestedScrollingEnabled(false);
        ConstantsUtils.setProgressColor(getActivity(), swipeRefresh);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<>(getContext(), R.layout.unbilled_item_row, this, rvBehaviourList, no_record_found);
        rvBehaviourList.setAdapter(simpleRecyclerViewAdapter);
        displayRefreshTime(ConstantsUtils.getLastSeenDateFormat(getContext(), ConstantsUtils.getMilliSeconds(
                ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.SPChannelEvaluationList, Constants.TimeStamp, getContext()))));
        callBehaviourList(statusID);
    }

    public void callBehaviourList(String statusID) {
        behaviourPresenter = new BehaviourPresenter(getContext(), this, getActivity());
        behaviourPresenter.loadAsyncTask(statusID);
    }

    @Override
    public void onItemClick(CustomerBean customerBean, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new UnbilledVH(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i, final CustomerBean customerBean) {
        ((UnbilledVH) viewHolder).tvRetailerName.setText(customerBean.getRetailerName());
        ((UnbilledVH) viewHolder).tv_retailer_mob_no.setText(customerBean.getCustomerId() + " " + customerBean.getCity());
        ((UnbilledVH) viewHolder).ivMobileNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!customerBean.getMobileNumber().equalsIgnoreCase("")) {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.tel_txt + (customerBean.getMobileNumber())));
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
            ((UnbilledVH) viewHolder).tv_address2.setText(this.getString(R.string.str_concat_two_texts_with_coma, addressVa, "\n" + state + "\n" + cityVal + "\n" + disticVal));
        } else {
            ((UnbilledVH) viewHolder).tv_address2.setText(this.getString(R.string.str_concat_two_texts_with_coma, addressVa, "\n" + disticVal));
        }

        if (customerBean.getRetailerName().length() > 0 && !"".equals(String.valueOf(customerBean.getRetailerName().charAt(0))))
            ((UnbilledVH) viewHolder).tvName.setText(String.valueOf(customerBean.getRetailerName().charAt(0)).toUpperCase());
        ((UnbilledVH) viewHolder).mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((UnbilledVH) viewHolder).detailsLayout.getVisibility() == View.VISIBLE)
                    ((UnbilledVH) viewHolder).detailsLayout.setVisibility(View.GONE);
                else
                    ((UnbilledVH) viewHolder).detailsLayout.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public void showMessage(String message) {
        ConstantsUtils.displayShortToast(getContext(), message);
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
    public void searchResult(ArrayList<CustomerBean> CustomerBeanArrayList) {
        simpleRecyclerViewAdapter.refreshAdapter(CustomerBeanArrayList);
    }

    @Override
    public void displayRefreshTime(String refreshTime) {
        try {
            String lastRefresh = "";
            if (!TextUtils.isEmpty(refreshTime)) {
                lastRefresh = getString(R.string.po_last_refreshed) + " " + refreshTime;
            }

            if (getActivity() instanceof BehaviourListActivity) {
                BehaviourListActivity activity = (BehaviourListActivity) getActivity();
                activity.setActionTitle(lastRefresh);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        behaviourPresenter.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                behaviourPresenter.onFilter();
                return true;
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        behaviourPresenter.onRefresh(statusID);
    }

}
