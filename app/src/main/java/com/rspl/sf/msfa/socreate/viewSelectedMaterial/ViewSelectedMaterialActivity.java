package com.rspl.sf.msfa.socreate.viewSelectedMaterial;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.InputType;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.so.SOUtils;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.socreate.shipToDetails.ShipToDetailsActivity;
import com.rspl.sf.msfa.socreate.stepTwo.SOCreateQtyTextWatcher;
import com.rspl.sf.msfa.socreate.stepTwo.SOCreateQtyTextWatcherInterface;
import com.rspl.sf.msfa.solist.SOListBean;

import java.math.BigDecimal;
import java.util.ArrayList;

public class ViewSelectedMaterialActivity extends AppCompatActivity implements AdapterInterface<SOItemBean>, SOCreateQtyTextWatcherInterface {

    SOListBean soListBeanHeader;
    int comingFrom;
    private int maxLength=0;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView noRecordFound;
    private SimpleRecyclerViewAdapter<SOItemBean> simpleRecyclerViewAdapter;
    private ArrayList<SOItemBean> selectedItemList = null;
    private CoordinatorLayout coordinatorLayout;
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            // super.onSelectedChanged(viewHolder, actionState);
            if (viewHolder != null) {
                int position = viewHolder.getAdapterPosition();
                if(selectedItemList!=null && selectedItemList.size()>0 && selectedItemList.get(position).isNewAdded()) {
                    final View foregroundView = ((SelectedMatVH) viewHolder).viewForeground;
                    getDefaultUIUtil().onSelected(foregroundView);
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
            if(selectedItemList!=null && selectedItemList.size()>0 && selectedItemList.get(position).isNewAdded()) {
                final View foregroundView = (((SelectedMatVH) viewHolder).viewForeground);
                getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            final View foregroundView = ((SelectedMatVH) viewHolder).viewForeground;
                getDefaultUIUtil().clearView(foregroundView);

        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            final View foregroundView = ((SelectedMatVH) viewHolder).viewForeground;
            int position = viewHolder.getAdapterPosition();
            if(selectedItemList!=null && selectedItemList.size()>0 && selectedItemList.get(position).isNewAdded()) {
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
            if (viewHolder instanceof SelectedMatVH) {
                final int position = viewHolder.getAdapterPosition(); //swiped position
                if(selectedItemList!=null && selectedItemList.size()>0 && selectedItemList.get(position).isNewAdded()) {
                if (!selectedItemList.isEmpty()) {
                    if (direction == ItemTouchHelper.RIGHT) {
                        ((SelectedMatVH) viewHolder).ivRight.setVisibility(View.GONE);
                        ((SelectedMatVH) viewHolder).ivLeft.setVisibility(View.VISIBLE);
                    } else if (direction == ItemTouchHelper.LEFT) {
                        ((SelectedMatVH) viewHolder).ivRight.setVisibility(View.VISIBLE);
                        ((SelectedMatVH) viewHolder).ivLeft.setVisibility(View.GONE);
                    }
                    if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT) {//swipe right
                        showSnackBar(position);
                    }
                }
            }
            }
        }
    };
    private TextView tvOrderType;
    private boolean isSessionRequired;
    private boolean isKeyBoardOpen = false;
    private SOListBean soDefaultBean = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_view_selected_material);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(getString(R.string.so_create_cart_sub_title));
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.menu_sos_create), 0);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectedItemList = (ArrayList<SOItemBean>) bundle.getSerializable(Constants.EXTRA_SO_ITEM_LIST);
            soListBeanHeader = (SOListBean) bundle.getSerializable(Constants.EXTRA_HEADER_BEAN);
            isSessionRequired = bundle.getBoolean(Constants.EXTRA_SESSION_REQUIRED, false);
            comingFrom = bundle.getInt(Constants.EXTRA_COME_FROM, 0);
            soDefaultBean = (SOListBean) bundle.getSerializable(Constants.EXTRA_SO_HEADER);
        }
        if (selectedItemList == null) {
            selectedItemList = new ArrayList<>();
        }

        String orderType = getString(R.string.so_new_order);
        if (comingFrom == ConstantsUtils.SO_APPROVAL_EDIT_ACTIVITY) {
            ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.menu_sos_edit), 0);
            orderType = soListBeanHeader.getSONo();
        } else
            ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.menu_sos_create), 0);

        try {
            maxLength = Constants.quantityLength();
        } catch (Exception e) {
            e.printStackTrace();
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        noRecordFound = (TextView) findViewById(R.id.no_record_found);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        tvOrderType = (TextView) findViewById(R.id.tvOrderType);
        tvOrderType.setText(getString(R.string.po_details_display_value, soListBeanHeader.getOrderTypeDesc(), soListBeanHeader.getOrderType()));
        TextView tvNewOrder = (TextView) findViewById(R.id.tvNewOrder);
        tvNewOrder.setText(orderType);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<>(ViewSelectedMaterialActivity.this, R.layout.view_slt_mat_item, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(simpleRecyclerViewAdapter);
        simpleRecyclerViewAdapter.refreshAdapter(selectedItemList);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        coordinatorLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = coordinatorLayout.getRootView().getHeight() - coordinatorLayout.getHeight();
                if (heightDiff > ConstantsUtils.dpToPx(200, ViewSelectedMaterialActivity.this)) {
                    isKeyBoardOpen = true;
                } else {
                    isKeyBoardOpen = false;
                }
            }
        });
    }
/*
    @Override
    public void onBackPressed() {
        callAddMenu();
    }*/

    @Override
    public void onItemClick(SOItemBean soItemBean, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new SelectedMatVH(view, new SOCreateQtyTextWatcher());
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i, final SOItemBean soItemBean) {
        ((SelectedMatVH) viewHolder).tvMatDesc.setText(soItemBean.getMatNoAndDesc());
        ((SelectedMatVH) viewHolder).tvMatDesc.setText(soItemBean.getMatNoAndDesc());
        if(!TextUtils.isEmpty(soItemBean.getHighLevellItemNo()) && !soItemBean.getHighLevellItemNo().equalsIgnoreCase("000000")){
            ((SelectedMatVH) viewHolder).tvMatDesc.setText(soItemBean.getMatDesc()+"("+soItemBean.getMatCode()+")");

        }
        try {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((SelectedMatVH) viewHolder).etQty.setCustomInsertionActionModeCallback(new ActionMode.Callback2() {
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
            ((SelectedMatVH) viewHolder).etQty.setLongClickable(false);
            ((SelectedMatVH) viewHolder).etQty.setTextIsSelectable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (soItemBean.isDecimalCheck()) {
            ((SelectedMatVH) viewHolder).etQty.setInputType(InputType.TYPE_CLASS_NUMBER);
//            UtilConstants.editTextDecimalFormat(((SelectedMatVH) viewHolder).etQty, 13, 0);
            UtilConstants.editTextDecimalFormat(((SelectedMatVH) viewHolder).etQty, maxLength, 0);
        } else {
            ((SelectedMatVH) viewHolder).etQty.setInputType(InputType.TYPE_CLASS_NUMBER /*| InputType.TYPE_NUMBER_FLAG_DECIMAL*/);
//            UtilConstants.editTextDecimalFormat(((SelectedMatVH) viewHolder).etQty, 13, 3);
            UtilConstants.editTextDecimalFormat(((SelectedMatVH) viewHolder).etQty, maxLength, 0);
        }
        ((SelectedMatVH) viewHolder).soCreateQtyTextWatcher.updateTextWatcher(soItemBean, viewHolder, this);
//        ((SelectedMatVH) viewHolder).tvSelQty.setText(OfflineManager.checkNoUOMZero(soItemBean.getUom(), soItemBean.getSoQty()) + " " + soItemBean.getUom());
        ((SelectedMatVH) viewHolder).etQty.setText(soItemBean.getSoQty());
        ((SelectedMatVH) viewHolder).tvUom.setText(soItemBean.getUom());
        if (soItemBean.isChecked()) {
            ((SelectedMatVH) viewHolder).ivSelectedImg.setImageDrawable(ContextCompat.getDrawable(ViewSelectedMaterialActivity.this, R.drawable.ic_remove_shopping_cart_black_24dp));
        } else {
            ((SelectedMatVH) viewHolder).ivSelectedImg.setImageDrawable(ContextCompat.getDrawable(ViewSelectedMaterialActivity.this, R.drawable.ic_shopping_cart_black_24dp));
        }

        ((SelectedMatVH) viewHolder).etQty.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((SelectedMatVH) viewHolder).etQty.setCursorVisible(true);
               /* switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (event.getX() - (((SelectedMatVH) viewHolder).etQty.getMeasuredWidth() - ((SelectedMatVH) viewHolder).etQty.getPaddingRight()) >= 0) {
                            if (soItemBean.isChecked() && !soItemBean.isHide()) {
//                                ((SelectedMatVH) viewHolder).etQty.setText("");
                                hideKeyboard();
                                showSnackBar(viewHolder.getAdapterPosition());
                            }
                        } else {
                            ((SelectedMatVH) viewHolder).etQty.setCursorVisible(true);
                        }
                        break;
                }*/
                return false;
            }
        });
        ((SelectedMatVH) viewHolder).ivRight.setVisibility(View.VISIBLE);
        ((SelectedMatVH) viewHolder).ivLeft.setVisibility(View.VISIBLE);
        if(soItemBean.isRemoved() || soItemBean.getStatusID().equalsIgnoreCase("D")) {
            ((SelectedMatVH) viewHolder).iv_deleted.setVisibility(View.VISIBLE);
            ((SelectedMatVH) viewHolder).etQty.setEnabled(false);
            ((SelectedMatVH) viewHolder).etQty.setFocusable(false);
            ((SelectedMatVH) viewHolder).etQty.setClickable(false);
            ((SelectedMatVH) viewHolder).etQty.setFocusableInTouchMode(false);
            ((SelectedMatVH) viewHolder).etQty.setLongClickable(false);

        }
        else {
            ((SelectedMatVH) viewHolder).iv_deleted.setVisibility(View.GONE);
            ((SelectedMatVH) viewHolder).etQty.setEnabled(true);
            ((SelectedMatVH) viewHolder).etQty.setFocusable(true);
            ((SelectedMatVH) viewHolder).etQty.setFocusableInTouchMode(true);
            ((SelectedMatVH) viewHolder).etQty.setClickable(true);



        }
        if(!TextUtils.isEmpty(soItemBean.getHighLevellItemNo())&& !soItemBean.getHighLevellItemNo().equalsIgnoreCase("000000")){
            ((SelectedMatVH) viewHolder).etQty.setEnabled(false);
            ((SelectedMatVH) viewHolder).etQty.setFocusable(false);
            ((SelectedMatVH) viewHolder).etQty.setClickable(false);
            ((SelectedMatVH) viewHolder).etQty.setFocusableInTouchMode(false);
            ((SelectedMatVH) viewHolder).etQty.setLongClickable(false);
        }

    }

    private void hideKeyboard() {
        if (isKeyBoardOpen) {
            UtilConstants.hideKeyboardFrom(ViewSelectedMaterialActivity.this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_select_menu, menu);
        /*if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }*/

        return true;
    }

    private void showSnackBar(final int position) {
        // backup of removed item for undo purpose
        final SOItemBean deletedItem = selectedItemList.get(position);
        selectedItemList.remove(selectedItemList.get(position));
        simpleRecyclerViewAdapter.refreshAdapter(selectedItemList);
        // showing snack bar with Undo option
        Snackbar snackbar = Snackbar.make(coordinatorLayout, deletedItem.getMatDesc() + " is removed", Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.so_create_undo, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // undo is selected, restore the deleted item
                selectedItemList.add(position, deletedItem);
                simpleRecyclerViewAdapter.refreshAdapter(selectedItemList);
            }
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.secondaryDarkColor));
        snackbar.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_next:
                if (ConstantsUtils.isAutomaticTimeZone(this)) {
                    callNextMenu();
                }else {
                    ConstantsUtils.showAutoDateSetDialog(this);
                }

                break;
            case R.id.menu_add:
                onBackPressed();
//                callAddMenu();
                break;
            case R.id.menu_so_cancel:
                SOUtils.redirectMainActivity(ViewSelectedMaterialActivity.this, comingFrom);
                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_SO_ITEM_LIST, selectedItemList);
        setResult(ConstantsUtils.ACTIVITY_RESULT_MATERIAL, intent);
        finish();
    }

    private void callNextMenu() {
        if (validateMaterial()) {
            soListBeanHeader.setSoItemBeanArrayList(selectedItemList);
            Intent intent = new Intent(this, ShipToDetailsActivity.class);
            intent.putExtra(Constants.EXTRA_HEADER_BEAN, soListBeanHeader);
            if (soDefaultBean != null) {
                intent.putExtra(Constants.EXTRA_SO_HEADER, soDefaultBean);
            }
            intent.putExtra(Constants.EXTRA_COME_FROM, comingFrom);
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            startActivity(intent);
            finish();
        }
    }

    private void displayMsg(String msg) {
        ConstantsUtils.displayLongToast(ViewSelectedMaterialActivity.this, msg);
    }

    private boolean validateMaterial() {
        if (!selectedItemList.isEmpty()) {
            BigDecimal totalAmount = new BigDecimal("0");
            BigDecimal totalNetWeight = new BigDecimal("0");
            BigDecimal totalQty = new BigDecimal("0");
            String netWeightUOM = "";
            String strCurrency="";
            try {
                for (SOItemBean soItemBean : selectedItemList) {
                    if (soItemBean.isChecked() && !soItemBean.isHide()) {
                        if (TextUtils.isEmpty(soItemBean.getSoQty()) || Double.parseDouble(soItemBean.getSoQty()) <= 0) {
                            displayMsg(getString(R.string.so_error_enter_valid_qty));
                            return false;
                        } else {
                            if(!TextUtils.isEmpty(soItemBean.getItemFlag()) && soItemBean.getItemFlag().equalsIgnoreCase("M")) {
                                totalAmount = totalAmount.add(new BigDecimal(soItemBean.getLandingPrice()).multiply(new BigDecimal(soItemBean.getSoQty())));
                                totalNetWeight = totalNetWeight.add(new BigDecimal(soItemBean.getNetWeight()));
                                totalQty = totalQty.add(new BigDecimal(soItemBean.getSoQty()));
                                netWeightUOM = soItemBean.getNetWeightUOM();
                                strCurrency = soItemBean.getCurrency();
                            }else if(TextUtils.isEmpty(soItemBean.getItemFlag())){
                                totalAmount = totalAmount.add(new BigDecimal(soItemBean.getLandingPrice()).multiply(new BigDecimal(soItemBean.getSoQty())));
                                totalNetWeight = totalNetWeight.add(new BigDecimal(soItemBean.getNetWeight()));
                                totalQty = totalQty.add(new BigDecimal(soItemBean.getSoQty()));
                                netWeightUOM = soItemBean.getNetWeightUOM();
                                strCurrency = soItemBean.getCurrency();
                            }
                        }
                    }
                }
                soListBeanHeader.setTotalAmt(String.valueOf(totalAmount));
                soListBeanHeader.setmStrTotalWeight(String.valueOf(totalNetWeight));
                soListBeanHeader.setQuantity(String.valueOf(totalQty));
                soListBeanHeader.setmStrWeightUOM(netWeightUOM);
                soListBeanHeader.setCurrency(strCurrency);
            } catch (Exception e) {
                e.printStackTrace();
                displayMsg(getString(R.string.so_error_enter_valid_qty));
                return false;
            }
        } else {
            displayMsg(getString(R.string.lbl_no_items_selected));
            return false;
        }
        return true;
    }

    @Override
    public void onTextChange(String charSequence, SOItemBean soItemBean, RecyclerView.ViewHolder holder) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantsUtils.ACTIVITY_RESULT_MATERIAL) {
            if (resultCode == ConstantsUtils.ACTIVITY_RESULT_MATERIAL) {
                Bundle bundle = data.getExtras();
                if (bundle != null)
                    if (bundle.getBoolean(Constants.CHECK_ADD_MATERIAL_ITEM, false)) {
                        selectedItemList.clear();
                        for (SOItemBean soItemBean : (ArrayList<SOItemBean>) bundle.getSerializable(Constants.EXTRA_SO_ITEM_LIST)) {
                            selectedItemList.add(soItemBean);
                        }
                        simpleRecyclerViewAdapter.refreshAdapter(selectedItemList);
                    } else {
                        for (SOItemBean soItemBean : (ArrayList<SOItemBean>) bundle.getSerializable(Constants.EXTRA_SO_ITEM_LIST)) {
                            selectedItemList.add(soItemBean);
                        }
                        simpleRecyclerViewAdapter.refreshAdapter(selectedItemList);
                    }

            } else {
                finish();
            }
        }

    }
}
