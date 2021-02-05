package com.rspl.sf.msfa.returnOrder;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterViewInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewTypeAdapter;
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.returnOrder.retrunFilter.FilterReturnActivity;
import com.rspl.sf.msfa.so.SOUtils;
import com.rspl.sf.msfa.ui.FlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10860 on 12/28/2017.
 */

public class ReturnOrderFragment extends Fragment implements ReturnOrderView, SwipeRefreshLayout.OnRefreshListener, AdapterViewInterface<ReturnOrderBean> {

    private RecyclerView rvReturnOrder;
    private TextView tvNoDataFound;
    private SimpleRecyclerViewTypeAdapter<ReturnOrderBean> returnOrderAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private ReturnOrderPresenterImpl returnOrderPresenter;
    private int comingFrom = 0;
    private String customerNo = "";
    private boolean isSessionRequired = false;
    private FlowLayout llFlowLayout;
    private LinearLayout llFilterType, llFilterLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            comingFrom = bundle.getInt(Constants.EXTRA_COME_FROM, 1);
            customerNo = bundle.getString(Constants.EXTRA_CUSTOMER_NO, "");
            isSessionRequired = bundle.getBoolean(Constants.EXTRA_SESSION_REQUIRED, false);
        }
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.return_order_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        rvReturnOrder = (RecyclerView) view.findViewById(R.id.rvReturnOrder);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        tvNoDataFound = (TextView) view.findViewById(R.id.no_record_found);
        llFlowLayout = (FlowLayout) view.findViewById(R.id.llFlowLayout);
        llFilterType = (LinearLayout) view.findViewById(R.id.llFilterType);
        llFilterLayout = (LinearLayout) view.findViewById(R.id.llFilterLayout);
        ConstantsUtils.setProgressColor(getContext(), swipeRefresh);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvReturnOrder.setLayoutManager(linearLayoutManager);
        rvReturnOrder.setHasFixedSize(true);
        returnOrderPresenter = new ReturnOrderPresenterImpl(getActivity(), comingFrom, this, isSessionRequired, customerNo);
        int itemResource;
        if (comingFrom == 1) {
            itemResource = R.layout.snippet_return_order;//item
        } else {
            itemResource = R.layout.snippet_return_order_item;//header
        }
        returnOrderAdapter = new SimpleRecyclerViewTypeAdapter<>(getActivity(), itemResource, this, rvReturnOrder, tvNoDataFound);
        rvReturnOrder.setAdapter(returnOrderAdapter);
        returnOrderPresenter.onStart();
    }

    @Override
    public void onItemClick(ReturnOrderBean o, View view, int i) {
        returnOrderPresenter.onItemClick(o);
    }

    @Override
    public int getItemViewType(int i, ArrayList arrayList) {
        return arrayList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new ReturnOrderVH(view);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, ReturnOrderBean returnOrderBean, ArrayList<ReturnOrderBean> arrayList) {
        ((ReturnOrderVH) viewHolder).tvOrderId.setText(returnOrderBean.getRetOrdNo());
        ((ReturnOrderVH) viewHolder).tvOrderDate.setText(returnOrderBean.getOrderDate());
        ((ReturnOrderVH) viewHolder).tvSOValue.setText(ConstantsUtils.commaSeparator(returnOrderBean.getNetAmount(), returnOrderBean.getCurrency())+" "+returnOrderBean.getCurrency());
        ((ReturnOrderVH) viewHolder).tvMaterialName.setText(returnOrderBean.getMaterialDesc());
        try {
            ((ReturnOrderVH) viewHolder).tvSOQTY.setText(ConstantsUtils.checkNoUOMZero( returnOrderBean.getUOM(),returnOrderBean.getQuantity()) + " " + returnOrderBean.getUOM());
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        Drawable delvStatusImg = SOUtils.displayReturnOrderStatusImage(returnOrderBean.getStatusID(), returnOrderBean.getGRStatusID(), getContext());
        if (delvStatusImg != null) {
            ((ReturnOrderVH) viewHolder).ivDelvStatus.setImageDrawable(delvStatusImg);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_ro_search, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getActivity().getComponentName());
        SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        MenuItem dateFilter = menu.findItem(R.id.filter);
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.lbl_ro_doc_num_search));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                returnOrderPresenter.onSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                returnOrderPresenter.onSearch(newText);
                return false;
            }
        });
        returnOrderPresenter.onSearch("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                returnOrderPresenter.onFilter();
                return true;
            /*default:
                return super.onOptionsItemSelected(item);*/
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showMessage(String message) {
        ConstantsUtils.displayShortToast(getContext(), message);
    }

    @Override
    public void dialogMessage(String message, final String msgType) {

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
    public void searchResult(ArrayList<ReturnOrderBean> returnListBeen) {
        refreshAdapter(returnListBeen);
    }

    @Override
    public void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus) {
        Intent intent = new Intent(getContext(), FilterReturnActivity.class);
//        intent.putExtra(DateFilterFragment.EXTRA_DEFAULT, filterType);
//        intent.putExtra(DateFilterFragment.EXTRA_START_DATE, startDate);
//        intent.putExtra(DateFilterFragment.EXTRA_END_DATE, endDate);
        intent.putExtra(FilterReturnActivity.EXTRA_RETURN_STATUS, status);
        intent.putExtra(FilterReturnActivity.EXTRA_GR_STATUS, delvStatus);
        startActivityForResult(intent, ConstantsUtils.ACTIVITY_RESULT_FILTER);
    }

    @Override
    public void setFilterDate(String filterType) {
        try {
            String[] filterTypeArr = filterType.split(", ");
            ConstantsUtils.displayFilter(filterTypeArr, llFlowLayout, getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(filterType)) {
            llFilterLayout.setVisibility(View.VISIBLE);
        } else {
            llFilterLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void displayRefreshTime(String refreshTime) {
        if (getActivity() instanceof ReturnOrderActivity) {
            ((ReturnOrderActivity) getActivity()).setActionBarSubTitle(refreshTime);
        }
    }

    @Override
    public void displayList(List<ReturnOrderBean> list) {
        refreshAdapter((ArrayList<ReturnOrderBean>) list);
    }

    @Override
    public void onRefresh() {
        returnOrderPresenter.onRefresh();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ConstantsUtils.ACTIVITY_RESULT_FILTER) {
            refreshAdapter(new ArrayList<ReturnOrderBean>());
            returnOrderPresenter.startFilter(requestCode, resultCode, data);
        }
    }

    private void refreshAdapter(ArrayList<ReturnOrderBean> list) {
        returnOrderAdapter.refreshAdapter(list);
    }
}
