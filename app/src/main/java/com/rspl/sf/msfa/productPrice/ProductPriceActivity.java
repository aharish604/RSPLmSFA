package com.rspl.sf.msfa.productPrice;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.filterlist.SearchFilterInterface;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.socreate.filter.BrandFilterActivity;
import com.rspl.sf.msfa.socreate.stepTwo.SOCreateQtyTextWatcher;
import com.rspl.sf.msfa.socreate.stepTwo.SOCreateQtyTextWatcherInterface;
import com.rspl.sf.msfa.socreate.stepTwo.SOCrtStpTwoView;
import com.rspl.sf.msfa.solist.SOListBean;
import com.rspl.sf.msfa.ui.FlowLayout;
import com.rspl.sf.msfa.ui.MovableFrameLayout;

import java.util.ArrayList;

public class ProductPriceActivity extends AppCompatActivity implements IProductListViewPresenter,SOCrtStpTwoView, SwipeRefreshLayout.OnRefreshListener,AdapterInterface<SOItemBean>, SearchFilterInterface, View.OnClickListener, SOCreateQtyTextWatcherInterface {
    private int comingFrom = 0;
    SwipeRefreshLayout swipeRefresh;
    private ProductPricePersenter presenter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private TextView noRecordFound;
    private SimpleRecyclerViewAdapter<SOItemBean> simpleRecyclerViewAdapter;
    //    private Spinner spSearch;
//    private EditText etSearch;
    private ArrayList<SOItemBean> searchSOItemBean = new ArrayList<>();
    private String searchStr[] = {"Desc", "Code"};
    private SOListBean soListBeanHeader = null;
    private boolean isSessionRequired = false;
    private SOListBean soDefaultBean = null;
    private ArrayList<SOItemBean> selectedItemList = null;
    private Toolbar toolbar;
    private boolean checkAddItem;
    private SearchView mSearchView = null;
    private LinearLayout llCartCount;
    private TextView tvCartCount;
    private FloatingActionButton flDisplaySelectedItem;
    private MovableFrameLayout flMovableView;
    private boolean isKeyBoardOpen = false;
    private FlowLayout flowLayout;
    LinearLayout linearLayoutFlowLayout;
    private String mstrCustomerNo = "",mStrCPGUID32="",mStrComingFrom="",mStrUID="",mStrCustomerName="";
    //    private TextView tvCartCount = null;
//    private FrameLayout flCartCount = null;
//    private ArrayList<SOItemBean> checkedArrayList = new ArrayList();
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            // super.onSelectedChanged(viewHolder, actionState);
            if (viewHolder != null) {
                final View foregroundView = ((PPMultiMaterialVH) viewHolder).viewForeground;
                getDefaultUIUtil().onSelected(foregroundView);
            }
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            int position = viewHolder.getAdapterPosition();
            SOItemBean soItemBean = searchSOItemBean.get(position);
            if (soItemBean.isChecked()) {
                final View foregroundView = (((PPMultiMaterialVH) viewHolder).viewForeground);
                getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//            final View foregroundView = ((PPMultiMaterialVH) viewHolder).viewForeground;
//            getDefaultUIUtil().clearView(foregroundView);
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            int position = viewHolder.getAdapterPosition();
            SOItemBean soItemBean = searchSOItemBean.get(position);
            if (soItemBean.isChecked()) {
                View foregroundView = ((PPMultiMaterialVH) viewHolder).viewForeground;

                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                        actionState, isCurrentlyActive);
            }
        }

        @Override
        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
            return super.convertToAbsoluteDirection(flags, layoutDirection);
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
            if (viewHolder instanceof PPMultiMaterialVH) {
                final int position = viewHolder.getAdapterPosition(); //swiped position
                if (!searchSOItemBean.isEmpty()) {
                    SOItemBean soItemBean = searchSOItemBean.get(position);
                    if (direction == ItemTouchHelper.RIGHT) {
                        ((PPMultiMaterialVH) viewHolder).ivRight.setVisibility(View.GONE);
                        ((PPMultiMaterialVH) viewHolder).ivLeft.setVisibility(View.VISIBLE);
                        resetMatView(soItemBean, viewHolder);
                    } else if (direction == ItemTouchHelper.LEFT) {
                        ((PPMultiMaterialVH) viewHolder).ivRight.setVisibility(View.VISIBLE);
                        ((PPMultiMaterialVH) viewHolder).ivLeft.setVisibility(View.GONE);
                        resetMatView(soItemBean, viewHolder);
                    }
                }
            }
        }
    };
    private TextView tvOrderType;
    private Paint p = new Paint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_price);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_product_pricing), 0);
        //setSupportActionBar(toolbar);
     /*   if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(getString(R.string.so_stp2_create_sub_title));*/
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            soListBeanHeader = (SOListBean) bundle.getSerializable(Constants.EXTRA_HEADER_BEAN);
            isSessionRequired = bundle.getBoolean(Constants.EXTRA_SESSION_REQUIRED, false);
            comingFrom = bundle.getInt(Constants.EXTRA_COME_FROM, 0);
            soDefaultBean = (SOListBean) bundle.getSerializable(Constants.EXTRA_SO_HEADER);
            selectedItemList = (ArrayList<SOItemBean>) bundle.getSerializable(Constants.EXTRA_SO_ITEM_LIST);
            checkAddItem = bundle.getBoolean(Constants.CHECK_ADD_MATERIAL_ITEM, false);

            mstrCustomerNo = bundle.getString(Constants.CPNo);
            mStrCustomerName = bundle.getString(Constants.RetailerName);
            mStrUID = bundle.getString(Constants.CPUID);
            mStrComingFrom = bundle.getString(Constants.comingFrom);
            mStrCPGUID32 = bundle.getString(Constants.CPGUID32);
        }
        if (soListBeanHeader == null) {
            soListBeanHeader = new SOListBean();
        }
        linearLayoutFlowLayout = (LinearLayout) findViewById(R.id.llFilterLayout);

        flowLayout = (FlowLayout) findViewById(R.id.llFlowLayout);

       // ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.menu_sos_create), 0);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        noRecordFound = (TextView) findViewById(R.id.no_record_found);
        swipeRefresh =(SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        ConstantsUtils.setProgressColor(this, swipeRefresh);
        initializeClickListeners();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        /*ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);*/
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<SOItemBean>(ProductPriceActivity.this, R.layout.pp_multi_material_item, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(simpleRecyclerViewAdapter);
        final View headerView = findViewById(R.id.llListView);
//        headerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                int heightDiff = headerView.getRootView().getHeight() - headerView.getHeight();
//                if (heightDiff > ConstantsUtils.dpToPx(200, ProductPriceActivity.this)) {
//                    isKeyBoardOpen = true;
//                } else {
//                    isKeyBoardOpen = false;
//                }
//            }
//        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    hideKeyboard();
                }
            }
        });


        presenter = new ProductPricePersenter(checkAddItem, ProductPriceActivity.this, this, comingFrom, isSessionRequired, soDefaultBean, soListBeanHeader, selectedItemList,this,this);
        presenter.onStart();

    }

    private void hideKeyboard() {
        if (isKeyBoardOpen) {
            UtilConstants.hideKeyboardFrom(ProductPriceActivity.this);
        }
    }


    @Override
    public void displayList(ArrayList<SOItemBean> soItemList) {
        refreshAdapter(soItemList);
    }

    @Override
    public void displaySearchList(ArrayList<SOItemBean> soItemList) {
        refreshAdapter(soItemList);
    }

    private void refreshAdapter(ArrayList<SOItemBean> soItemList) {
        searchSOItemBean = soItemList;
        Log.d("SOMaterial", "getSOMaterialList: adding");
        simpleRecyclerViewAdapter.refreshAdapter(soItemList);
        Log.d("SOMaterial", "getSOMaterialList: display");
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = ConstantsUtils.showProgressDialog(ProductPriceActivity.this, message);
    }

    @Override
    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void displayMessage(String message) {
        ConstantsUtils.displayLongToast(ProductPriceActivity.this, message);
    }

    @Override
    public void displayTotalSelectedMat(int finalSelectedCount) {
        if (finalSelectedCount == 0) {
        } else {
        }
    }


    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onItemClick(SOItemBean item, View view, int position) {


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, View viewItem) {
        return new PPMultiMaterialVH(viewItem, new SOCreateQtyTextWatcher());
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position, final SOItemBean soItemBean) {

        ((PPMultiMaterialVH) holder).tvMatDesc.setText(soItemBean.getMatNoAndDesc());
        //((PlantStockMatlVH) holder).tvPrice.setText(soItemBean.getUnitPrice());
        ((PPMultiMaterialVH) holder).tvPrice.setText(ConstantsUtils.commaSeparator(soItemBean.getLandingPrice(), soItemBean.getCurrency()) + " " + soItemBean.getCurrency());
        //((PlantStockMatlVH) holder).tvCurreny.setText(soItemBean.getCurrency());
        ((PPMultiMaterialVH) holder).tvUom.setText(soItemBean.getUom());
        if (soItemBean.getUom().equalsIgnoreCase("PC") || soItemBean.getUom().equals("EA")) {
            ((PPMultiMaterialVH) holder).etQty.setInputType(InputType.TYPE_CLASS_NUMBER);
            UtilConstants.editTextDecimalFormat(((PPMultiMaterialVH) holder).etQty, 13, 0);
        } else {
            ((PPMultiMaterialVH) holder).etQty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            UtilConstants.editTextDecimalFormat(((PPMultiMaterialVH) holder).etQty, 13, 3);
        }

        ((PPMultiMaterialVH) holder).soCreateQtyTextWatcher.updateTextWatcher(soItemBean, holder, this);
        if (soItemBean.isChecked()) {
            ((PPMultiMaterialVH) holder).etQty.setText(soItemBean.getSoQty());
            ((PPMultiMaterialVH) holder).clView.setVisibility(View.VISIBLE);
            ((PPMultiMaterialVH) holder).tvUom.setVisibility(View.VISIBLE);

        } else {
            ((PPMultiMaterialVH) holder).clView.setVisibility(View.GONE);
            ((PPMultiMaterialVH) holder).tvUom.setVisibility(View.GONE);
        }
        ((PPMultiMaterialVH) holder).tvUom.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTextChange(String charSequence, SOItemBean soItemBean, RecyclerView.ViewHolder holder) {

        presenter.getCheckedCount();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_product_price, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getComponentName());
        mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.so_mat_search_hint));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.onSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
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
            case R.id.menu_next:
                //next step
                presenter.validateItem(comingFrom, recyclerView);
                return true;
            case R.id.menu_add:
                //next step
                presenter.validateItem(comingFrom, recyclerView);
                return true;
            /*case R.id.menu_so_cancel:
                SOUtils.redirectMainActivity(ProductPriceActivity.this, false);
                return true;*/
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



    private void resetMatView(SOItemBean soItemBean, final RecyclerView.ViewHolder holder) {
        if (soItemBean.isChecked() && !soItemBean.isHide()) {
            soItemBean.setChecked(false);
            soItemBean.setSoQty("0");
            ((PPMultiMaterialVH) holder).clView.setVisibility(View.GONE);
            ((PPMultiMaterialVH) holder).tvUom.setVisibility(View.GONE);
            //((PlantStockMatlVH) holder).btAdd.setVisibility(View.VISIBLE);
            presenter.getCheckedCount();
            ((PPMultiMaterialVH) holder).etQty.clearFocus();
            ((PPMultiMaterialVH) holder).etQty.setFocusable(false);
        }
        hideKeyboard();
        simpleRecyclerViewAdapter.notifyDataSetChanged();
    }


    @Override
    public void onRefresh() {
        try {
            presenter.onRefresh();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }


    @Override
    public void ProductListFresh() {

        try {
            simpleRecyclerViewAdapter.notifyDataSetChanged();
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,0);
            String rollType = sharedPreferences.getString(Constants.USERROLE, "");
            if (rollType.equalsIgnoreCase("Z5")) {
                displayRefreshTime(ConstantsUtils.getLastSeenDateFormat(getApplicationContext(), ConstantsUtils.getCurrentTimeLong()));
            }else {
            displayRefreshTime(ConstantsUtils.getLastSeenDateFormat(getApplicationContext(), ConstantsUtils.getMilliSeconds(
                    ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.MaterialByCustomers, Constants.TimeStamp, getApplicationContext()))));
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void displayRefreshTime(String refreshTime) {
        String lastRefresh = "";
        if (!TextUtils.isEmpty(refreshTime)) {
            lastRefresh = getString(R.string.po_last_refreshed) + " " + refreshTime;
        }
        if (lastRefresh!=null)
            getSupportActionBar().setSubtitle(lastRefresh);
    }

    @Override
    public void initializeClickListeners() {
        swipeRefresh.setOnRefreshListener(this);

    }

    @Override
    public void showProgressDialog() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideProgressDialog1() {
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void displayMsg(String msg) {
        ConstantsUtils.displayLongToast(ProductPriceActivity.this,msg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ConstantsUtils.ACTIVITY_RESULT_FILTER) {
            presenter.startFilter(requestCode, resultCode, data);
        }else{
            presenter.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void setFilterDate(String filterType) {
        try {
            if(filterType!=null && !filterType.equalsIgnoreCase("")){
                linearLayoutFlowLayout.setVisibility(View.VISIBLE);
            }else{
                linearLayoutFlowLayout.setVisibility(View.GONE);
            }
            String[] filterTypeArr = filterType.split(", ");
            ConstantsUtils.displayFilter(filterTypeArr, flowLayout, ProductPriceActivity.this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus) {
        Intent intent = new Intent(ProductPriceActivity.this, BrandFilterActivity.class);
        intent.putExtra(BrandFilterActivity.EXTRA_DELV_STATUS, status);
        startActivityForResult(intent, ConstantsUtils.ACTIVITY_RESULT_FILTER);
    }
}
