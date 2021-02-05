package com.rspl.sf.msfa.dealerstock;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.filterlist.SearchFilterInterface;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.dealerstock.stockmaterial.DealerStockAddMaterialActivity;
import com.rspl.sf.msfa.socreate.filter.BrandFilterActivity;
import com.rspl.sf.msfa.solist.SOListBean;

import java.util.ArrayList;

public class StockCreateStpTwoActivity extends AppCompatActivity implements StockCrtStpTwoView, AdapterInterface<DealerStockBean>, SearchFilterInterface, View.OnClickListener, StockCreateQtyTextWatcherInterface {
    public static final int INTENT_RESULT_STOCK_CREATE = 111;
    private static String TAG = StockCreateStpTwoActivity.class.getSimpleName();
    //    private LinearLayout llCartCount;
//    private TextView tvCartCount;
//    private FloatingActionButton flDisplaySelectedItem;
//    private MovableFrameLayout flMovableView;
    LinearLayout linearLayoutFlowLayout;
    private StockCrtStpTwoPresenterImpl presenter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private TextView noRecordFound;
    private SimpleRecyclerViewAdapter<DealerStockBean> simpleRecyclerViewAdapter;
    //    private Spinner spSearch;
//    private EditText etSearch;
    private ArrayList<DealerStockBean> searchSOItemBean = new ArrayList<>();
    private String searchStr[] = {"Desc", "Code"};
    private SOListBean soListBeanHeader = null;
    private boolean isSessionRequired = false;
    private SOListBean soDefaultBean = null;
    private ArrayList<DealerStockBean> selectedItemList = null;
    private Toolbar toolbar;
    private boolean checkAddItem;
    private SearchView mSearchView = null;
    private boolean isKeyBoardOpen = false;
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            // super.onSelectedChanged(viewHolder, actionState);
            if (viewHolder != null) {
                final View foregroundView = ((StockMultiMaterialVH) viewHolder).viewForeground;
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
            DealerStockBean soItemBean = searchSOItemBean.get(position);
//            if (soItemBean.isChecked()) {
                final View foregroundView = (((StockMultiMaterialVH) viewHolder).viewForeground);
                getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
//            }
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            try {
                final View foregroundView = ((StockMultiMaterialVH) viewHolder).viewForeground;
                getDefaultUIUtil().clearView(foregroundView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            int position = viewHolder.getAdapterPosition();
            DealerStockBean soItemBean = searchSOItemBean.get(position);
//            if (soItemBean.isChecked()) {
                View foregroundView = ((StockMultiMaterialVH) viewHolder).viewForeground;

                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                        actionState, isCurrentlyActive);
//            }
        }

        @Override
        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
            return super.convertToAbsoluteDirection(flags, layoutDirection);
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
            if (viewHolder instanceof StockMultiMaterialVH) {
                final int position = viewHolder.getAdapterPosition(); //swiped position
                if (!searchSOItemBean.isEmpty()) {
                    DealerStockBean soItemBean = searchSOItemBean.get(position);
                    if (direction == ItemTouchHelper.RIGHT) {
                        ((StockMultiMaterialVH) viewHolder).ivRight.setVisibility(View.GONE);
                        ((StockMultiMaterialVH) viewHolder).ivLeft.setVisibility(View.VISIBLE);
                        resetMatView(soItemBean, viewHolder);
                    } else if (direction == ItemTouchHelper.LEFT) {
                        ((StockMultiMaterialVH) viewHolder).ivRight.setVisibility(View.VISIBLE);
                        ((StockMultiMaterialVH) viewHolder).ivLeft.setVisibility(View.GONE);
                        resetMatView(soItemBean, viewHolder);
                    }
                }
            }
        }
    };
    private String mstrCustomerNo = "", mStrCPGUID32 = "", mStrComingFrom = "", mStrUID = "", mStrCustomerName = "";
    //    private TextView tvOrderType;
    private Paint p = new Paint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_stock);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mstrCustomerNo = bundle.getString(Constants.CPNo);
            mStrCustomerName = bundle.getString(Constants.RetailerName);
            mStrUID = bundle.getString(Constants.CPUID);
            mStrComingFrom = bundle.getString(Constants.comingFrom);
            mStrCPGUID32 = bundle.getString(Constants.CPGUID32);
        }
        if (soListBeanHeader == null) {
            soListBeanHeader = new SOListBean();
        }
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.dealer_stock_title), 0);
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(getString(R.string.dealer_stock_entrys));
        linearLayoutFlowLayout = (LinearLayout) findViewById(R.id.llFilterLayout);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        noRecordFound = (TextView) findViewById(R.id.no_record_found);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<DealerStockBean>(StockCreateStpTwoActivity.this, R.layout.dealer_stock_line_item, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(simpleRecyclerViewAdapter);
        final View headerView = findViewById(R.id.llListView);
        headerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = headerView.getRootView().getHeight() - headerView.getHeight();
                if (heightDiff > ConstantsUtils.dpToPx(200, StockCreateStpTwoActivity.this)) {
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


        presenter = new StockCrtStpTwoPresenterImpl(checkAddItem, StockCreateStpTwoActivity.this, this,
                mStrComingFrom, isSessionRequired, soDefaultBean, soListBeanHeader, selectedItemList, mstrCustomerNo, false, mStrCustomerName,mStrCPGUID32,mStrUID);
        presenter.onStart();

    }

    private void hideKeyboard() {
        if (isKeyBoardOpen) {
            UtilConstants.hideKeyboardFrom(StockCreateStpTwoActivity.this);
        }
    }


    @Override
    public void displayList(ArrayList<DealerStockBean> soItemList) {
        refreshAdapter(soItemList);
    }

    @Override
    public void displaySearchList(ArrayList<DealerStockBean> soItemList) {
        refreshAdapter(soItemList);
    }

    private void refreshAdapter(ArrayList<DealerStockBean> soItemList) {
        searchSOItemBean = soItemList;
        Log.d("SOMaterial", "getSOMaterialList: adding");
        simpleRecyclerViewAdapter.refreshAdapter(searchSOItemBean);
        Log.d("SOMaterial", "getSOMaterialList: display");
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = ConstantsUtils.showProgressDialog(StockCreateStpTwoActivity.this, message);
    }

    @Override
    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void displayMessage(String message) {
        ConstantsUtils.displayShortToast(StockCreateStpTwoActivity.this, message);
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
        Intent intent = new Intent(StockCreateStpTwoActivity.this, BrandFilterActivity.class);
        intent.putExtra(BrandFilterActivity.EXTRA_SO_STATUS, status);
        startActivityForResult(intent, ConstantsUtils.ACTIVITY_RESULT_FILTER);
    }


    @Override
    public void onItemClick(DealerStockBean item, View view, int position) {


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, View viewItem) {
        return new StockMultiMaterialVH(viewItem, new StockCreateQtyTextWatcher());
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position, final DealerStockBean soItemBean) {

        ((StockMultiMaterialVH) holder).tvMatDesc.setText(soItemBean.getMatNoAndDesc());
        ((StockMultiMaterialVH) holder).tvUom.setText(soItemBean.getUOM());
        ((StockMultiMaterialVH) holder).tvLandingPrice.setText(soItemBean.getUnrestrictedQty() + " " + soItemBean.getUOM());
        if (!soItemBean.isDecimalCheck()) {
            ((StockMultiMaterialVH) holder).etQty.setInputType(InputType.TYPE_CLASS_NUMBER);
            UtilConstants.editTextDecimalFormat(((StockMultiMaterialVH) holder).etQty, 13, 0);
        } else {
            ((StockMultiMaterialVH) holder).etQty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            UtilConstants.editTextDecimalFormat(((StockMultiMaterialVH) holder).etQty, 13, 3);
        }
        ((StockMultiMaterialVH) holder).soCreateQtyTextWatcher.updateTextWatcher(soItemBean, holder, this);
        if (soItemBean.isChecked()) {
            ((StockMultiMaterialVH) holder).etQty.setText(soItemBean.getEnterdQty());
            ((StockMultiMaterialVH) holder).clView.setVisibility(View.VISIBLE);
            ((StockMultiMaterialVH) holder).tvUom.setVisibility(View.VISIBLE);
            ((StockMultiMaterialVH) holder).btAdd.setVisibility(View.GONE);

        } else {
            ((StockMultiMaterialVH) holder).clView.setVisibility(View.GONE);
            ((StockMultiMaterialVH) holder).tvUom.setVisibility(View.GONE);
            ((StockMultiMaterialVH) holder).btAdd.setVisibility(View.VISIBLE);
        }
        ((StockMultiMaterialVH) holder).btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soItemBean.isChecked()) {
                    soItemBean.setChecked(false);
                } else {
                    soItemBean.setChecked(true);
                }
                if (soItemBean.isChecked()) {
                    ((StockMultiMaterialVH) holder).clView.setVisibility(View.VISIBLE);
                    ((StockMultiMaterialVH) holder).tvUom.setVisibility(View.VISIBLE);
                    ((StockMultiMaterialVH) holder).btAdd.setVisibility(View.GONE);
                    ((StockMultiMaterialVH) holder).etQty.setFocusable(true);
                    ((StockMultiMaterialVH) holder).etQty.setFocusableInTouchMode(true);
                    ((StockMultiMaterialVH) holder).etQty.requestFocus();
                    ((StockMultiMaterialVH) holder).etQty.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    ConstantsUtils.showKeyboard(StockCreateStpTwoActivity.this, ((StockMultiMaterialVH) holder).etQty);

                } else {
                    ((StockMultiMaterialVH) holder).clView.setVisibility(View.GONE);
                    ((StockMultiMaterialVH) holder).tvUom.setVisibility(View.GONE);
                    ((StockMultiMaterialVH) holder).btAdd.setVisibility(View.VISIBLE);
                }
//                presenter.getCheckedCount();
            }
        });

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
        menu.removeItem(R.id.menu_save);
//        menu.removeItem(R.id.filter);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getComponentName());
        mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.so_mat_search_hint));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                simpleRecyclerViewAdapter.searchFilter(searchSOItemBean, StockCreateStpTwoActivity.this);
                presenter.onSearchMaterial(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                simpleRecyclerViewAdapter.searchFilter(searchSOItemBean, StockCreateStpTwoActivity.this);
                presenter.onSearchMaterial(newText);
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
            case R.id.menu_review:
                //next step
                presenter.validateItem(0, recyclerView);

                return true;
            case R.id.filter:
                presenter.onFilter();
                return true;
            case R.id.add:
                Intent intent = new Intent(this, DealerStockAddMaterialActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_DEALER_STOCK_BEAN, searchSOItemBean);
                startActivityForResult(intent, INTENT_RESULT_STOCK_CREATE);
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


    private void resetMatView(DealerStockBean soItemBean, final RecyclerView.ViewHolder holder) {
        if (soItemBean.isChecked() && !soItemBean.isHide()) {
            soItemBean.setChecked(false);
            soItemBean.setEnterdQty("0");
            ((StockMultiMaterialVH) holder).clView.setVisibility(View.GONE);
            ((StockMultiMaterialVH) holder).tvUom.setVisibility(View.GONE);
            ((StockMultiMaterialVH) holder).btAdd.setVisibility(View.VISIBLE);
//            presenter.getCheckedCount();
            ((StockMultiMaterialVH) holder).etQty.clearFocus();
            ((StockMultiMaterialVH) holder).etQty.setFocusable(false);
        }
        searchSOItemBean.remove(soItemBean);
        presenter.removeItem(soItemBean);
        hideKeyboard();
        simpleRecyclerViewAdapter.refreshAdapter(searchSOItemBean);
    }

    @SuppressWarnings("all")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ConstantsUtils.ACTIVITY_RESULT_FILTER) {
            presenter.startFilter(requestCode, resultCode, data);
        } else {
            presenter.onActivityResult(requestCode, resultCode, data);
        }
       /* if (resultCode == INTENT_RESULT_STOCK_CREATE) {
            materialArrayList = new ArrayList<>();
            materialArrayList.addAll((ArrayList<StocksInfoBean>) data.getSerializableExtra(Constants.INTENT_EXTRA_MATERIAL_LIST));
            for (StocksInfoBean stocksInfoBean : materialArrayList) {
                DealerStockBean dealerStockBean = new DealerStockBean();
                dealerStockBean.setMaterialNo(stocksInfoBean.getMaterialNo());
                dealerStockBean.setMaterialDesc(stocksInfoBean.getMaterialDesc());
                dealerStockBean.setUOM(stocksInfoBean.getUOM());
                dealerStockBean.setUnrestrictedQty("0");
                dealerStockBean.setMatNoAndDesc(getString(R.string.po_details_display_value, stocksInfoBean.getMaterialDesc(), stocksInfoBean.getMaterialNo()));
                searchSOItemBean.add(dealerStockBean);
            }*/
//            simpleRecyclerViewAdapter.refreshAdapter(searchSOItemBean);
//            recyclerView.smoothScrollToPosition(searchSOItemBean.size());
//        }
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
        Intent intent = new Intent(this, StockCreateStpTwoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.CPNo, mstrCustomerNo);
        intent.putExtra(Constants.CPUID, mStrUID);
        intent.putExtra(Constants.RetailerName, mStrCustomerName);
        intent.putExtra(Constants.CPGUID32, mStrCPGUID32);
        intent.putExtra(Constants.comingFrom, mStrComingFrom);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        UtilConstants.dialogBoxWithCallBack(this, "", getString(R.string.on_back_press_dealer_stock_msg), getString(R.string.yes), getString(R.string.no), false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean clickedStatus) {
                if (clickedStatus) {
                    finish();
                }
            }
        });

    }
}
