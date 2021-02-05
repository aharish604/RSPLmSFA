package com.rspl.sf.msfa.socreate.stepTwo;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
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
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.filterlist.SearchFilterInterface;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.DialogCallBack;
import com.rspl.sf.msfa.mbo.ConfigTypesetTypesBean;
import com.rspl.sf.msfa.so.SOUtils;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.socreate.filter.BrandFilterActivity;
import com.rspl.sf.msfa.solist.SOListBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.ui.FlowLayout;
import com.rspl.sf.msfa.ui.MovableFrameLayout;

import java.util.ArrayList;

public class SOCreateStpTwoActivity extends AppCompatActivity implements SOCrtStpTwoView, AdapterInterface<SOItemBean>, SearchFilterInterface, View.OnClickListener, SOCreateQtyTextWatcherInterface {

    private static String TAG = SOCreateStpTwoActivity.class.getSimpleName();
    LinearLayout linearLayoutFlowLayout;
    private int comingFrom = 0;
    private SOCrtStpTwoPresenterImpl presenter;
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
    private FlowLayout flowLayout;
    private boolean isKeyBoardOpen = false;
     ArrayList<ConfigTypesetTypesBean> configTypesetTypesBeanArrayList = new ArrayList<>();
    String mStrConfigQry = Constants.ConfigTypesetTypes + "?$filter=" + Constants.Typeset + " eq '" +
            Constants.RSFRJN + "' &$orderby=" + Constants.Types + " asc";
    //    private TextView tvCartCount = null;
//    private FrameLayout flCartCount = null;
//    private ArrayList<SOItemBean> checkedArrayList = new ArrayList();
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            // super.onSelectedChanged(viewHolder, actionState);
            if (viewHolder != null) {
                final View foregroundView = ((SOMultiMaterialVH) viewHolder).viewForeground;
                try {
                    if (foregroundView != null) {
                        if (!searchSOItemBean.isEmpty()) {
                            int position = viewHolder.getAdapterPosition();
                            SOItemBean soItemBean = searchSOItemBean.get(position);
                            if ((!TextUtils.isEmpty(soItemBean.getHighLevellItemNo()) && soItemBean.getHighLevellItemNo().equalsIgnoreCase("000000"))
                            && (!soItemBean.getStatusID().equalsIgnoreCase("D"))) {
                                getDefaultUIUtil().onSelected(foregroundView);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            int position = viewHolder.getAdapterPosition();
            if(position>=0) {
            SOItemBean soItemBean = searchSOItemBean.get(position);
            if (soItemBean.isChecked()) {
                final View foregroundView = (((SOMultiMaterialVH) viewHolder).viewForeground);
                try {
                    if (foregroundView != null) {
                        if ((!TextUtils.isEmpty(soItemBean.getHighLevellItemNo()) && soItemBean.getHighLevellItemNo().equalsIgnoreCase("000000"))
                                && ( !soItemBean.getStatusID().equalsIgnoreCase("D"))) {
                            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            }
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            final View foregroundView = ((SOMultiMaterialVH) viewHolder).viewForeground;
            try {
                if (foregroundView != null) {
                    getDefaultUIUtil().clearView(foregroundView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            int position = viewHolder.getAdapterPosition();
            if(position>=0) {
                SOItemBean soItemBean = searchSOItemBean.get(position);
                if (soItemBean.isChecked()) {
                    View foregroundView = ((SOMultiMaterialVH) viewHolder).viewForeground;
                    try {
                        if (foregroundView != null) {
                            if ((!TextUtils.isEmpty(soItemBean.getHighLevellItemNo()) && soItemBean.getHighLevellItemNo().equalsIgnoreCase("000000"))
                                    && ( !soItemBean.getStatusID().equalsIgnoreCase("D"))) {
                                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                                        actionState, isCurrentlyActive);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
            return super.convertToAbsoluteDirection(flags, layoutDirection);
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
            if (viewHolder instanceof SOMultiMaterialVH) {
                final int position = viewHolder.getAdapterPosition(); //swiped position
                if(position>=0){
                if (!searchSOItemBean.isEmpty()) {
                    SOItemBean soItemBean = searchSOItemBean.get(position);
                    if ((!TextUtils.isEmpty(soItemBean.getHighLevellItemNo()) && soItemBean.getHighLevellItemNo().equalsIgnoreCase("000000"))
                            && ( !soItemBean.getStatusID().equalsIgnoreCase("D"))) {
                        if (direction == ItemTouchHelper.RIGHT) {
                            ((SOMultiMaterialVH) viewHolder).ivRight.setVisibility(View.GONE);
                            ((SOMultiMaterialVH) viewHolder).ivLeft.setVisibility(View.VISIBLE);
                            resetMatView(soItemBean, viewHolder);
                        } else if (direction == ItemTouchHelper.LEFT) {
                            ((SOMultiMaterialVH) viewHolder).ivRight.setVisibility(View.VISIBLE);
                            ((SOMultiMaterialVH) viewHolder).ivLeft.setVisibility(View.GONE);
                            resetMatView(soItemBean, viewHolder);
                        }
                    }
                }
                }
            }
        }
    };
    private String mstrCustomerNo = "", mStrCPGUID32 = "", mStrUID = "", mStrCustomerName = "";
    private TextView tvOrderType;
    private Paint p = new Paint();
    private TextView tvNewOrder;
    private String maxStrLength="";
    private int maxLength=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socreate_stp_two);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(getString(R.string.so_stp2_create_sub_title));
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
            mStrCPGUID32 = bundle.getString(Constants.CPGUID32);
        }
        if (soListBeanHeader == null) {
            soListBeanHeader = new SOListBean();
        }
        String orderType=getString(R.string.so_new_order);
        if (comingFrom == ConstantsUtils.SO_APPROVAL_EDIT_ACTIVITY) {
            ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.menu_sos_edit), 0);
            orderType=soListBeanHeader.getSONo();
        }else
            ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.menu_sos_create), 0);

        linearLayoutFlowLayout = (LinearLayout) findViewById(R.id.llFilterLayout);

        flowLayout = (FlowLayout) findViewById(R.id.llFlowLayout);
        tvNewOrder = (TextView) findViewById(R.id.tvNewOrder);
        tvNewOrder.setText(orderType);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        noRecordFound = (TextView) findViewById(R.id.no_record_found);
        llCartCount = (LinearLayout) findViewById(R.id.llCartCount);
        llCartCount.setVisibility(View.GONE);
        tvCartCount = (TextView) findViewById(R.id.tvCartCount);
        flDisplaySelectedItem = (FloatingActionButton) findViewById(R.id.flDisplaySelectedItem);
        flMovableView = (MovableFrameLayout) findViewById(R.id.flMovableView);
        tvOrderType = (TextView) findViewById(R.id.tvOrderType);
        if (!TextUtils.isEmpty(soListBeanHeader.getOrderType()))
            tvOrderType.setText(getString(R.string.po_details_display_value, soListBeanHeader.getOrderTypeDesc(), soListBeanHeader.getOrderType()));

        flMovableView.setVisibility(View.GONE);
        flDisplaySelectedItem.setOnClickListener(this);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<SOItemBean>(SOCreateStpTwoActivity.this, R.layout.so_multi_material_item, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(simpleRecyclerViewAdapter);
        final View headerView = findViewById(R.id.llListView);
        headerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = headerView.getRootView().getHeight() - headerView.getHeight();
                if (heightDiff > ConstantsUtils.dpToPx(200, SOCreateStpTwoActivity.this)) {
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

        try {
            maxLength = Constants.quantityLength();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            configTypesetTypesBeanArrayList.addAll(OfflineManager.getConfigTypesetTypes(mStrConfigQry));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        presenter = new SOCrtStpTwoPresenterImpl(checkAddItem, SOCreateStpTwoActivity.this, this, comingFrom, isSessionRequired, soDefaultBean, soListBeanHeader, selectedItemList, mstrCustomerNo);
        presenter.onStart();


        /*try {
            maxStrLength = OfflineManager.getValueByColumnName("ConfigTypsetTypeValues?$filter=Typeset eq 'SF' and Types eq 'ALWLENQTY'",Constants.TypeValue);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            maxStrLength="";
        }
        if(!TextUtils.isEmpty(maxStrLength)){
            maxLength= Integer.parseInt(maxStrLength);
        }else {
            maxLength=9;
        }*/
    }

    private void hideKeyboard() {
        if (isKeyBoardOpen) {
            UtilConstants.hideKeyboardFrom(SOCreateStpTwoActivity.this);
        }
    }


    @Override
    public void displayList(ArrayList<SOItemBean> soItemList) {
        if (comingFrom == ConstantsUtils.SO_APPROVAL_EDIT_ACTIVITY) {
            for(SOItemBean soItems : soItemList) {
                if (!TextUtils.isEmpty(soItems.getSoQty()) && !soItems.getSoQty().equalsIgnoreCase("0")) {
                    soItems.setNotnew(true);
                    soItems.setEditAndApproveQty(soItems.getSoQty());
                }
                if (!TextUtils.isEmpty(soItems.getStatusID()) && soItems.getStatusID().equalsIgnoreCase("D")){
                    soItems.setRejectionId(configTypesetTypesBeanArrayList.get(1).getTypes());
                    soItems.setRejectionStatusDesc(configTypesetTypesBeanArrayList.get(1).getTypesName());
                }
            }
        }
        flMovableView.setVisibility(View.VISIBLE);
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
        progressDialog = ConstantsUtils.showProgressDialog(SOCreateStpTwoActivity.this, message);
    }

    @Override
    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void displayMessage(String message) {
        ConstantsUtils.displayLongToast(SOCreateStpTwoActivity.this, message);
    }

    @Override
    public void displayTotalSelectedMat(int finalSelectedCount) {
        if (finalSelectedCount == 0) {
            tvCartCount.setText("0");
            llCartCount.setVisibility(View.GONE);
        } else {
            llCartCount.setVisibility(View.VISIBLE);
            tvCartCount.setText(String.valueOf(finalSelectedCount));
        }
    }


    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus) {
        Intent intent = new Intent(SOCreateStpTwoActivity.this, BrandFilterActivity.class);
        intent.putExtra(BrandFilterActivity.EXTRA_DELV_STATUS, delvStatus);
        if(comingFrom == 1 || comingFrom==36){
            try {
                intent.putExtra(Constants.CreateEditSO, soListBeanHeader.getDivison());
                intent.putExtra(Constants.EXTRA_COME_FROM, comingFrom);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        startActivityForResult(intent, ConstantsUtils.ACTIVITY_RESULT_FILTER);
    }


    @Override
    public void onItemClick(SOItemBean item, View view, int position) {


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, View viewItem) {
        return new SOMultiMaterialVH(viewItem, new SOCreateQtyTextWatcher());
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position, final SOItemBean soItemBean) {

        ((SOMultiMaterialVH) holder).tvMatDesc.setText(soItemBean.getMatNoAndDesc());
        ((SOMultiMaterialVH) holder).tvUom.setText(soItemBean.getUom());
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((SOMultiMaterialVH) holder).etQty.setFilters(new InputFilter[]{
                        new InputFilter() {
                            @Override
                            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                               if(source.length()>1){
                                   return "";
                               }
                               return null;
                            }
                        }
                });
                ((SOMultiMaterialVH) holder).etQty.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        menu.clear();
                    }
                });
                ((SOMultiMaterialVH) holder).etQty.setCustomInsertionActionModeCallback(new ActionMode.Callback2() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {

                    }
                });
            }

            ((SOMultiMaterialVH) holder).etQty.setLongClickable(false);
            ((SOMultiMaterialVH) holder).etQty.setTextIsSelectable(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (soItemBean.isDecimalCheck()) {
            ((SOMultiMaterialVH) holder).etQty.setInputType(InputType.TYPE_CLASS_NUMBER);
//            UtilConstants.editTextDecimalFormat(((SOMultiMaterialVH) holder).etQty, 13, 0);
            ((SOMultiMaterialVH) holder).etQty.setImeOptions(EditorInfo.IME_ACTION_DONE);
            UtilConstants.editTextDecimalFormat(((SOMultiMaterialVH) holder).etQty, maxLength, 0);
        } else {
            ((SOMultiMaterialVH) holder).etQty.setInputType(InputType.TYPE_CLASS_NUMBER /*| InputType.TYPE_NUMBER_FLAG_DECIMAL*/);
//            UtilConstants.editTextDecimalFormat(((SOMultiMaterialVH) holder).etQty, 13, 3);
            ((SOMultiMaterialVH) holder).etQty.setImeOptions(EditorInfo.IME_ACTION_DONE);
            UtilConstants.editTextDecimalFormat(((SOMultiMaterialVH) holder).etQty, maxLength, 0);
        }
        ((SOMultiMaterialVH) holder).tvLandingPrice.setText(ConstantsUtils.commaSeparator(soItemBean.getLandingPrice(), soItemBean.getCurrency()) + " " + soItemBean.getCurrency());
        ((SOMultiMaterialVH) holder).soCreateQtyTextWatcher.updateTextWatcher(soItemBean, holder, this);
        if (soItemBean.isChecked() && !soItemBean.isRemoved()) {
            ((SOMultiMaterialVH) holder).etQty.setText(soItemBean.getSoQty());
            ((SOMultiMaterialVH) holder).clView.setVisibility(View.VISIBLE);
            ((SOMultiMaterialVH) holder).tvUom.setVisibility(View.VISIBLE);
            ((SOMultiMaterialVH) holder).btAdd.setVisibility(View.GONE);

        } else {
            ((SOMultiMaterialVH) holder).clView.setVisibility(View.GONE);
            ((SOMultiMaterialVH) holder).tvUom.setVisibility(View.GONE);
            ((SOMultiMaterialVH) holder).btAdd.setVisibility(View.VISIBLE);
        }


//        if(comingFrom == ConstantsUtils.SO_APPROVAL_EDIT_ACTIVITY && !TextUtils.isEmpty(soItemBean.getItemFlag()) && soItemBean.getItemFlag().equalsIgnoreCase("E")){
        if(comingFrom == ConstantsUtils.SO_APPROVAL_EDIT_ACTIVITY && !TextUtils.isEmpty(soItemBean.getHighLevellItemNo()) && !soItemBean.getHighLevellItemNo().equalsIgnoreCase("000000")){
            ((SOMultiMaterialVH) holder).tvMatDesc.setText(soItemBean.getMatDesc()+"("+soItemBean.getMatCode()+")");
            ((SOMultiMaterialVH) holder).etQty.setEnabled(false);
            ((SOMultiMaterialVH) holder).etQty.setFocusable(false);
            ((SOMultiMaterialVH) holder).etQty.setClickable(false);
            ((SOMultiMaterialVH) holder).etQty.setFocusableInTouchMode(false);
            ((SOMultiMaterialVH) holder).etQty.setLongClickable(false);
            ((SOMultiMaterialVH) holder).btAdd.setVisibility(View.GONE);
            ((SOMultiMaterialVH) holder).clView.setVisibility(View.VISIBLE);
            ((SOMultiMaterialVH) holder).tvUom.setVisibility(View.VISIBLE);
            ((SOMultiMaterialVH) holder).etQty.setText(soItemBean.getSoQty());
        }else if (!TextUtils.isEmpty(soItemBean.getStatusID()) && soItemBean.getStatusID().equalsIgnoreCase("D")){
            ((SOMultiMaterialVH) holder).etQty.setEnabled(false);
            ((SOMultiMaterialVH) holder).etQty.setFocusable(false);
            ((SOMultiMaterialVH) holder).etQty.setClickable(false);
            ((SOMultiMaterialVH) holder).etQty.setFocusableInTouchMode(false);
            ((SOMultiMaterialVH) holder).etQty.setLongClickable(false);
            ((SOMultiMaterialVH) holder).btAdd.setVisibility(View.GONE);
            ((SOMultiMaterialVH) holder).clView.setVisibility(View.VISIBLE);
            ((SOMultiMaterialVH) holder).tvUom.setVisibility(View.VISIBLE);
        }



        ((SOMultiMaterialVH) holder).btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(soItemBean.isNotnew() && soItemBean.isChecked()){
                  soItemBean.setChecked(false);
              }
                if (soItemBean.isChecked()) {
                    soItemBean.setChecked(false);
                } else {
                    soItemBean.setChecked(true);
                }

                if (soItemBean.isRemoved()) {
                    soItemBean.setRemoved(false);
                    soItemBean.setRejectionId("");
                    soItemBean.setRejectionStatusDesc("");
                }

                if(searchSOItemBean!=null && searchSOItemBean.size()>0){
                    for(SOItemBean dataItems : searchSOItemBean){
                        if(soItemBean.getItemNo().equalsIgnoreCase(dataItems.getHighLevellItemNo()) && !dataItems.getHighLevellItemNo().equalsIgnoreCase("000000")){
                            if (dataItems.isRemoved()) {
                                dataItems.setRemoved(false);
                                dataItems.setRejectionId("");
                                dataItems.setRejectionStatusDesc("");
                            }
//                            break;
                        }
                    }
                }

                if (comingFrom == ConstantsUtils.SO_APPROVAL_EDIT_ACTIVITY && !soItemBean.isNotnew()) {
                    soItemBean.setNewAdded(true);
                    soItemBean.setItemFlag("M");
                }else if(comingFrom == ConstantsUtils.SO_APPROVAL_EDIT_ACTIVITY && soItemBean.isNotnew()) {
                    soItemBean.setNewAdded(false);
                }else{
                    soItemBean.setNewAdded(true);
                    soItemBean.setItemFlag("M");
                }



                    if (soItemBean.isChecked()) {
                    ((SOMultiMaterialVH) holder).clView.setVisibility(View.VISIBLE);
                    ((SOMultiMaterialVH) holder).tvUom.setVisibility(View.VISIBLE);
                    ((SOMultiMaterialVH) holder).btAdd.setVisibility(View.GONE);
                    ((SOMultiMaterialVH) holder).etQty.setFocusable(true);
                    ((SOMultiMaterialVH) holder).etQty.setFocusableInTouchMode(true);
                    ((SOMultiMaterialVH) holder).etQty.requestFocus();
                    ((SOMultiMaterialVH) holder).etQty.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    ((SOMultiMaterialVH) holder).etQty.setText("");
                    ConstantsUtils.showKeyboard(SOCreateStpTwoActivity.this, ((SOMultiMaterialVH) holder).etQty);

                } else {
                    ((SOMultiMaterialVH) holder).clView.setVisibility(View.GONE);
                    ((SOMultiMaterialVH) holder).tvUom.setVisibility(View.GONE);
                    ((SOMultiMaterialVH) holder).btAdd.setVisibility(View.VISIBLE);
                }
                /*if(comingFrom == ConstantsUtils.SO_APPROVAL_EDIT_ACTIVITY && !TextUtils.isEmpty(soItemBean.getItemFlag()) && soItemBean.getItemFlag().equalsIgnoreCase("E")) {
                    ((SOMultiMaterialVH) holder).clView.setVisibility(View.VISIBLE);
                    ((SOMultiMaterialVH) holder).tvUom.setVisibility(View.VISIBLE);
                    ((SOMultiMaterialVH) holder).btAdd.setVisibility(View.GONE);
                }*/


                presenter.getCheckedCount();
            }
        });

    }

    @Override
    public void onTextChange(String charSequence, SOItemBean soItemBean, RecyclerView.ViewHolder holder) {

        presenter.getCheckedCount();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_material_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getComponentName());
        mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.so_mat_search_hint));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                simpleRecyclerViewAdapter.searchFilter(searchSOItemBean, SOCreateStpTwoActivity.this);
                presenter.onSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                simpleRecyclerViewAdapter.searchFilter(searchSOItemBean, SOCreateStpTwoActivity.this);
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
                if (ConstantsUtils.isAutomaticTimeZone(this)) {
                    if(Constants.getRollID(SOCreateStpTwoActivity.this)){
                        if(Constants.isNetworkAvailable(SOCreateStpTwoActivity.this)){
                            if(ConstantsUtils.isPinging()){
                                presenter.validateItem(comingFrom, recyclerView);
                            }else {
                                try {
                                    ConstantsUtils.toastAMessage("Internet is ON, But it is not Active",SOCreateStpTwoActivity.this);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
//                            Toast.makeText(SOCreateStpTwoActivity.this, "Internet is ON, But it is not Active", Toast.LENGTH_LONG).show();
                            }
                        }else {
                            try {
                                ConstantsUtils.toastAMessage(this.getString(R.string.no_network_conn),SOCreateStpTwoActivity.this);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//                        Toast.makeText(SOCreateStpTwoActivity.this, this.getString(R.string.no_network_conn), Toast.LENGTH_LONG).show();
                        }
                    }else {
                        presenter.validateItem(comingFrom, recyclerView);
                    }
                }else {
                    ConstantsUtils.showAutoDateSetDialog(this);
                }

                return true;
            case R.id.menu_add:
                //next step
                if (ConstantsUtils.isAutomaticTimeZone(this)) {
                    presenter.validateItem(comingFrom, recyclerView);
                }else {
                    ConstantsUtils.showAutoDateSetDialog(this);
                }
                return true;
            case R.id.menu_so_cancel:
                SOUtils.redirectMainActivity(SOCreateStpTwoActivity.this, comingFrom);
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
                if (ConstantsUtils.isAutomaticTimeZone(this)) {
                    presenter.validateItem(ConstantsUtils.SO_VIEW_SELECTED_MATERIAL, recyclerView);
                }else {
                    ConstantsUtils.showAutoDateSetDialog(this);
                }

                break;
        }
    }


    private void resetMatView(final SOItemBean resoItemBean, final RecyclerView.ViewHolder holder) {
        if (comingFrom == ConstantsUtils.SO_APPROVAL_EDIT_ACTIVITY && resoItemBean.isNotnew()) {
            ConstantsUtils.dialogBoxWithButton(SOCreateStpTwoActivity.this, "", getString(R.string.delete_exisiting_item),
                    getString(R.string.yes), getString(R.string.no), new DialogCallBack() {
                        @Override
                        public void clickedStatus(boolean clickedStatus) {
                            if (clickedStatus) {
                                if (resoItemBean.isChecked() && !resoItemBean.isHide()) {
                                    resoItemBean.setRemoved(true);
                                    if(!resoItemBean.isNotnew()) {
                                        resoItemBean.setChecked(false);
                                        resoItemBean.setSoQty("0");
                                    }else{
                                        resoItemBean.setChecked(true);
                                        resoItemBean.setSoQty(resoItemBean.getEditAndApproveQty());
                                    }

                                    if(searchSOItemBean!=null && searchSOItemBean.size()>0){
                                        for(SOItemBean dataItems : searchSOItemBean){
                                            if(resoItemBean.getItemNo().equalsIgnoreCase(dataItems.getHighLevellItemNo()) && !dataItems.getHighLevellItemNo().equalsIgnoreCase("000000")){
                                                dataItems.setRemoved(true);
                                                if(!dataItems.isNotnew()) {
                                                    dataItems.setChecked(false);
                                                    dataItems.setSoQty("0");
                                                }else{
                                                    dataItems.setChecked(true);
                                                    dataItems.setSoQty(dataItems.getSoQty());
                                                }
                                                dataItems.setRejectionId(configTypesetTypesBeanArrayList.get(1).getTypes());
                                                dataItems.setRejectionStatusDesc(configTypesetTypesBeanArrayList.get(1).getTypesName());
//                                                break;
                                            }
                                        }
                                    }
                                    resoItemBean.setRejectionId(configTypesetTypesBeanArrayList.get(1).getTypes());
                                    resoItemBean.setRejectionStatusDesc(configTypesetTypesBeanArrayList.get(1).getTypesName());
                                    ((SOMultiMaterialVH) holder).clView.setVisibility(View.GONE);
                                    ((SOMultiMaterialVH) holder).tvUom.setVisibility(View.GONE);
                                    ((SOMultiMaterialVH) holder).btAdd.setVisibility(View.VISIBLE);
                                    presenter.getCheckedCount();
                                    ((SOMultiMaterialVH) holder).etQty.clearFocus();
                                    ((SOMultiMaterialVH) holder).etQty.setFocusable(false);
                                }
                                hideKeyboard();
                                simpleRecyclerViewAdapter.notifyDataSetChanged();

                            } else {
                                hideKeyboard();
                                simpleRecyclerViewAdapter.notifyDataSetChanged();
                            }

                        }
                    });
        }else{
            if (resoItemBean.isChecked() && !resoItemBean.isHide()) {
                resoItemBean.setChecked(false);
                resoItemBean.setSoQty("0");
            //    resoItemBean.setRejectionId(configTypesetTypesBeanArrayList.get(1).getTypes());
            //    resoItemBean.setRejectionStatusDesc(configTypesetTypesBeanArrayList.get(1).getTypesName());
                ((SOMultiMaterialVH) holder).clView.setVisibility(View.GONE);
                ((SOMultiMaterialVH)     holder).tvUom.setVisibility(View.GONE);
                ((SOMultiMaterialVH) holder).btAdd.setVisibility(View.VISIBLE);
                presenter.getCheckedCount();
                ((SOMultiMaterialVH) holder).etQty.clearFocus();
                ((SOMultiMaterialVH) holder).etQty.setFocusable(false);
            }
            hideKeyboard();
            simpleRecyclerViewAdapter.notifyDataSetChanged();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ConstantsUtils.ACTIVITY_RESULT_FILTER) {
            presenter.startFilter(requestCode, resultCode, data);
        } else if (requestCode==ConstantsUtils.ACTIVITY_RESULT_MATERIAL){
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
            ConstantsUtils.displayFilter(filterTypeArr, flowLayout, SOCreateStpTwoActivity.this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
