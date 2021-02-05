package com.rspl.sf.msfa.reports.plantstock;

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
import com.rspl.sf.msfa.reports.plantstock.filter.StockFilterActivity;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.socreate.stepTwo.SOCreateQtyTextWatcher;
import com.rspl.sf.msfa.socreate.stepTwo.SOCreateQtyTextWatcherInterface;
import com.rspl.sf.msfa.solist.SOListBean;
import com.rspl.sf.msfa.ui.FlowLayout;
import com.rspl.sf.msfa.ui.MovableFrameLayout;

import java.util.ArrayList;

public class PlantStockActivity extends AppCompatActivity implements IPlantListViewPresenter, PlantStockView, SwipeRefreshLayout.OnRefreshListener, AdapterInterface<SOItemBean>, SearchFilterInterface, SOCreateQtyTextWatcherInterface {
    private int comingFrom = 0;
    SwipeRefreshLayout swipeRefresh;
    private PlantStockPersenter presenter;
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
    private String mstrCustomerNo = "", mStrCPGUID32 = "", mStrComingFrom = "", mStrUID = "", mStrCustomerName = "";
    //    private TextView tvCartCount = null;
//    private FrameLayout flCartCount = null;
//    private ArrayList<SOItemBean> checkedArrayList = new ArrayList();
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            // super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            try {
                int position = viewHolder.getAdapterPosition();
                SOItemBean soItemBean = searchSOItemBean.get(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            int position = viewHolder.getAdapterPosition();
        }

        @Override
        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
            return super.convertToAbsoluteDirection(flags, layoutDirection);
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
            try {
                if (viewHolder instanceof PlantStockMatlVH) {
                    final int position = viewHolder.getAdapterPosition(); //swiped position
                    if (!searchSOItemBean.isEmpty()) {
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private TextView tvOrderType;
    private Paint p = new Paint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_stock);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_plant_stock), 0);
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
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        ConstantsUtils.setProgressColor(this, swipeRefresh);
        initializeClickListeners();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<SOItemBean>(PlantStockActivity.this, R.layout.plant_stock_line_item, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(simpleRecyclerViewAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    hideKeyboard();
                }
            }
        });


        presenter = new PlantStockPersenter(checkAddItem, PlantStockActivity.this, this, comingFrom, isSessionRequired, soDefaultBean, soListBeanHeader, selectedItemList, this, this);
        presenter.onStart();

    }

    private void hideKeyboard() {
        if (isKeyBoardOpen) {
            UtilConstants.hideKeyboardFrom(PlantStockActivity.this);
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
        progressDialog = ConstantsUtils.showProgressDialog(PlantStockActivity.this, message);
    }

    @Override
    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void displayMessage(String message) {
        ConstantsUtils.displayLongToast(PlantStockActivity.this, message);
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
        return new PlantStockMatlVH(viewItem, new SOCreateQtyTextWatcher());
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position, final SOItemBean soItemBean) {
        ((PlantStockMatlVH) holder).tvMatDesc.setText(soItemBean.getMatNoAndDesc());
        ((PlantStockMatlVH) holder).tvPrice.setText(soItemBean.getQuantity() + " " + soItemBean.getUom());
        ((PlantStockMatlVH) holder).tvPlant.setText(getString(R.string.po_details_display_value, soItemBean.getPlantDesc(), soItemBean.getPlantId()));
    }

    @Override
    public void onTextChange(String charSequence, SOItemBean soItemBean, RecyclerView.ViewHolder holder) {

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
        if (lastRefresh != null)
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ConstantsUtils.ACTIVITY_RESULT_FILTER) {
            presenter.startFilter(requestCode, resultCode, data);
        } else {
            presenter.onActivityResult(requestCode, resultCode, data);
        }
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
            ConstantsUtils.displayFilter(filterTypeArr, flowLayout, PlantStockActivity.this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus) {
        Intent intent = new Intent(PlantStockActivity.this, StockFilterActivity.class);
        intent.putExtra(StockFilterActivity.EXTRA_BRAND_ID, status);
        startActivityForResult(intent, ConstantsUtils.ACTIVITY_RESULT_FILTER);
    }
}
