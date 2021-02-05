package com.rspl.sf.msfa.dealerstock;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.filterlist.SearchFilterInterface;
import com.rspl.sf.msfa.CustomerDetailsActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.socreate.filter.BrandFilterActivity;
import com.rspl.sf.msfa.solist.SOListBean;

import java.util.ArrayList;

/**
 * Created by e10526 on 09-04-2018.
 */

public class DealerStockReviewActivity extends AppCompatActivity implements StockCrtStpTwoView, AdapterInterface<DealerStockBean>, SearchFilterInterface, View.OnClickListener, StockCreateQtyTextWatcherInterface {

    private static String TAG = DealerStockReviewActivity.class.getSimpleName();
    LinearLayout linearLayoutFlowLayout;
    private StockCrtStpTwoPresenterImpl presenter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private TextView noRecordFound;
    private SimpleRecyclerViewAdapter<DealerStockBean> simpleRecyclerViewAdapter;
    private ArrayList<DealerStockBean> dealerStockBeanArrayList = new ArrayList<>();
    private String searchStr[] = {"Desc", "Code"};
    private SOListBean soListBeanHeader = null;
    private boolean isSessionRequired = false;
    private SOListBean soDefaultBean = null;
    private Toolbar toolbar;
    private boolean checkAddItem;
    private SearchView mSearchView = null;
    private boolean isKeyBoardOpen = false;
    private String mstrCustomerNo = "", mStrCPGUID32 = "", mStrComingFrom = "", mStrUID = "", mStrCustomerName = "";
    private DealerStockHeaderBean dealerStockHeaderBean = null;
    private ArrayList<DealerStockBean> alDealerStockList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_stock);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            dealerStockHeaderBean = (DealerStockHeaderBean) bundle.getSerializable(Constants.EXTRA_SO_HEADER);
            if (dealerStockHeaderBean != null) {
                mstrCustomerNo = dealerStockHeaderBean.getCustomerNumber();
                mStrCustomerName = dealerStockHeaderBean.getCustomerName();
            }
            mStrUID = bundle.getString(Constants.CPUID);
            mStrComingFrom = bundle.getString(Constants.comingFrom);
            mStrCPGUID32 = bundle.getString(Constants.CPGUID32);
        }
        if (dealerStockHeaderBean == null) {
            dealerStockHeaderBean = new DealerStockHeaderBean();
        }
        if (dealerStockHeaderBean.getAlStockList() != null && dealerStockHeaderBean.getAlStockList().size() > 0) {
            alDealerStockList.addAll(dealerStockHeaderBean.getAlStockList());
        }

        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.dealer_stock_title), 0);
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(getString(R.string.dealer_stock_sub_review));
        linearLayoutFlowLayout = (LinearLayout) findViewById(R.id.llFilterLayout);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        noRecordFound = (TextView) findViewById(R.id.no_record_found);


        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<DealerStockBean>(DealerStockReviewActivity.this, R.layout.dealer_stock_review_line_item, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(simpleRecyclerViewAdapter);
        final View headerView = findViewById(R.id.llListView);
        headerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = headerView.getRootView().getHeight() - headerView.getHeight();
                if (heightDiff > ConstantsUtils.dpToPx(200, DealerStockReviewActivity.this)) {
                    isKeyBoardOpen = true;
                } else {
                    isKeyBoardOpen = false;
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    hideKeyboard();
                }
            }
        });


        presenter = new StockCrtStpTwoPresenterImpl(DealerStockReviewActivity.this, this, mStrComingFrom, isSessionRequired, alDealerStockList, mstrCustomerNo, true, mStrCustomerName,mStrCPGUID32);
        presenter.onStart();

    }

    private void hideKeyboard() {
        if (isKeyBoardOpen) {
            UtilConstants.hideKeyboardFrom(DealerStockReviewActivity.this);
        }
    }


    @Override
    public void displayList(ArrayList<DealerStockBean> reviewStockList) {
        refreshAdapter(reviewStockList);
    }

    @Override
    public void displaySearchList(ArrayList<DealerStockBean> soItemList) {
        refreshAdapter(soItemList);
    }

    private void refreshAdapter(ArrayList<DealerStockBean> soItemList) {
        dealerStockBeanArrayList = soItemList;
        simpleRecyclerViewAdapter.refreshAdapter(soItemList);
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = ConstantsUtils.showProgressDialog(DealerStockReviewActivity.this, message);
    }

    @Override
    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void displayMessage(String message) {
        ConstantsUtils.displayShortToast(DealerStockReviewActivity.this, message);
    }

    @Override
    public void displayTotalSelectedMat(int finalSelectedCount) {
        if (finalSelectedCount == 0) {
//            tvCartCount.setText("0");
//            llCartCount.setVisibility(View.GONE);
        } else {
//            llCartCount.setVisibility(View.VISIBLE);
//            tvCartCount.setText(String.valueOf(finalSelectedCount));
        }
    }


    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus) {
        Intent intent = new Intent(DealerStockReviewActivity.this, BrandFilterActivity.class);
        intent.putExtra(BrandFilterActivity.EXTRA_SO_STATUS, status);
        startActivityForResult(intent, ConstantsUtils.ACTIVITY_RESULT_FILTER);
    }


    @Override
    public void onItemClick(DealerStockBean item, View view, int position) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, View viewItem) {
        return new StockMaterialReviewVH(viewItem, new StockCreateQtyTextWatcher());
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position, final DealerStockBean soItemBean) {

        ((StockMaterialReviewVH) holder).tvMatDesc.setText(soItemBean.getMatNoAndDesc());
        ((StockMaterialReviewVH) holder).tvLandingPrice.setText(soItemBean.getEnterdQty() + " " + soItemBean.getUOM());

    }

    @Override
    public void onTextChange(String charSequence, DealerStockBean soItemBean, RecyclerView.ViewHolder holder) {

//        presenter.getCheckedCount();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dealer_stock, menu);
        menu.removeItem(R.id.menu_review);
        menu.removeItem(R.id.add);
//        menu.removeItem(R.id.filter);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getComponentName());
        mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.so_mat_search_hint));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                simpleRecyclerViewAdapter.searchFilter(dealerStockBeanArrayList, StockCreateStpTwoActivity.this);
                presenter.onSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                simpleRecyclerViewAdapter.searchFilter(dealerStockBeanArrayList, StockCreateStpTwoActivity.this);
                presenter.onSearch(newText);
                return false;
            }
        });

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_save:
                //next step
                presenter.saveItem(recyclerView, alDealerStockList);
                return true;
            case R.id.filter:
                presenter.onFilter();
                return true;
            default:
                return super.onOptionsItemSelected(item);


        }

    }

    @Override
    public boolean applyConditionToAdd(Object o) {
        if (mSearchView != null)
            return presenter.onSearch(mSearchView.getQuery().toString(), o);
        else
            return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flDisplaySelectedItem:
                presenter.validateItem(ConstantsUtils.SO_VIEW_SELECTED_MATERIAL, recyclerView);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (resultCode == ConstantsUtils.ACTIVITY_RESULT_FILTER) {
            presenter.startFilter(requestCode, resultCode, data);
        } else {
            presenter.onActivityResult(requestCode, resultCode, data);
        }*/
    }

    @Override
    public void setFilterDate(String filterType) {
        try {
            if (filterType != null && !filterType.equalsIgnoreCase("")) {
                linearLayoutFlowLayout.setVisibility(View.VISIBLE);
            } else {
                linearLayoutFlowLayout.setVisibility(View.GONE);
            }
            String[] filterTypeArr = filterType.split(", ");
//            ConstantsUtils.displayFilter(filterTypeArr, flowLayout, StockCreateStpTwoActivity.this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateUpdateSuccess() {
        Intent intent = new Intent(DealerStockReviewActivity.this, CustomerDetailsActivity.class);
        intent.putExtra(Constants.CPNo, mstrCustomerNo);
        intent.putExtra(Constants.CPUID, mStrUID);
        intent.putExtra(Constants.RetailerName, mStrCustomerName);
        intent.putExtra(Constants.CPGUID32, mStrCPGUID32);
        intent.putExtra(Constants.comingFrom, mStrComingFrom);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
