package com.rspl.sf.msfa.returnOrder.returnDetail;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.returnOrder.ReturnOrderBean;
import com.rspl.sf.msfa.so.SOUtils;

/**
 * Created by e10860 on 12/29/2017.
 */

public class ReturnOrderDetailActivity extends AppCompatActivity implements RODetailsView, View.OnClickListener, AdapterInterface<ReturnOrderItemBean> {

    private TextView tvReturnNo;
    private Context context;
    private ReturnOrderBean roListBean = null;
    private RODetailsPresenterImpl presenter;

    private TextView tvOrderDetails;
    private TextView tvRefDoc;
    private ImageView ivOrderDetails;
    private LinearLayout llHeaderSale;
    private AppCompatTextView tvNoRecordFoundItemDetails;
    private SimpleRecyclerViewAdapter<ReturnOrderItemBean> roItemAdapter;


    private ImageView ivExpandIcon;
    private LinearLayout llHeader;
    private RecyclerView recyclerView;
    private TextView tvNoRecordFound;
    private TextView tvCustomerName;
    private TextView tvCustomerId;
    private ProgressDialog progressDialog = null;
    private View notesItemView;
    private TextView tvNotesHeader;
    private TextView tvNotesText;
    private EditText etNotes;
    private ImageView btPostNotes;
    private boolean isSessionRequired;
    private View notesHeaderView;
    private Toolbar toolbar;
    private LinearLayout ly_so_header;
    private Context mContext;
    private String soNumber = "";
    private TextView tvSONo;
    private TextView tvAmount;
    private TextView tvDate;
    private ImageView ivDeliveryStatus;
    private TextView tvAddress;
    private LinearLayout llSOCondition;
    private ImageView ivPricingDetails;
    private ImageView ivItemDetails;
    private CardView cvOrderDetails;
    private CardView cvPricingDetails;
    private CardView cvItem;
    private LinearLayout llItemList;
    private NestedScrollView nestedScroll;
    private TextView tvOrderType, tvTerms;
    private ConstraintLayout cl_payment_terms;
    private TextView tvShipToPartyName;
    private TextView tvSalesAreaDesc;
    private TextView tvSalesGroupDesc;
    private TextView tvSalesOfficeDesc;
    private TextView tvPlantDesc;
    private TextView tvCustomerPODesc;
    private TextView tvCustomerPODateDesc;
    private TextView tvShippingTypeDesc;
    private TextView tvMeansOfTranstDesc;
    private TextView tvIncoterm1Desc;
    private TextView tvIncoterm2Desc;
    private TextView tvPaytermDesc;
    private TextView tvUnloadingPointDesc;
    private TextView tvReceivingPointDesc;
    private TextView tvRefDocDesc;
    private TextView tvSalesDistDesc;
    private FloatingActionButton fabEdit;
    private LinearLayout llSalesArea, ll_total_amt;
    private LinearLayout llPlant;
    private LinearLayout llRefDoc, llCustomerPo, llCustomerPoDate, llShipToAdd, llShippingType, llIncoTerm1, llIncoTerm2, llUnloadingPoint, llReceivingPoint, llMeansOfTranst, llPaymentTerm;
    private LinearLayout llBillTo;
    private TextView tvBillToDesc;
    private TextView tvBillTo;
    private TextView tvCustomerPO;
    private TextView tvOrderDateDesc;
    private LinearLayout llOrderDate;
    private TextView tvReferenceHeader;
    private boolean fromSoCreate = false;
    private LinearLayout llSalesOffice;
    private LinearLayout llSalesGroup, llTaxOneAmt, llTaxTwoAmt, llTaxThreeAmt;
    private TextView tvTaxOneAmtVal, tvTaxTwoAmtVal, tvTaxThreeAmtVal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.return_detail_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Bundle bundle = getIntent().getExtras();
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.return_detail), 0);
        if (bundle != null) {
            roListBean = (ReturnOrderBean) bundle.getSerializable(Constants.EXTRA_SO_HEADER);
            isSessionRequired = bundle.getBoolean(Constants.EXTRA_SESSION_REQUIRED, false);
        }

        if (roListBean == null) {
            roListBean = new ReturnOrderBean();
        }
        initUI();
    }

    private void initUI() {
        mContext = ReturnOrderDetailActivity.this;
        nestedScroll = (NestedScrollView) findViewById(R.id.nestedScroll);
        tvReturnNo = (TextView) findViewById(R.id.tvReturnNo);
        ivDeliveryStatus = (ImageView) findViewById(R.id.ivDeliveryStatus);
        tvOrderType = (TextView) findViewById(R.id.tvOrderType);
        tvTerms = (TextView) findViewById(R.id.tvTerms);
        cl_payment_terms = (ConstraintLayout) findViewById(R.id.cl_payment_terms);

        tvShipToPartyName = (TextView) findViewById(R.id.tvShipToPartyName);
        tvSalesAreaDesc = (TextView) findViewById(R.id.tvSalesAreaDesc);
        tvSalesGroupDesc = (TextView) findViewById(R.id.tvSalesGroupDesc);
        tvSalesOfficeDesc = (TextView) findViewById(R.id.tvSalesOfficeDesc);
        tvPlantDesc = (TextView) findViewById(R.id.tvPlantDesc);
        tvCustomerPODesc = (TextView) findViewById(R.id.tvCustomerPODesc);
        tvCustomerPODateDesc = (TextView) findViewById(R.id.tvCustomerPODateDesc);
        tvShippingTypeDesc = (TextView) findViewById(R.id.tvShippingTypeDesc);
        tvMeansOfTranstDesc = (TextView) findViewById(R.id.tvMeansOfTranstDesc);
        tvIncoterm1Desc = (TextView) findViewById(R.id.tvIncoterm1Desc);
        tvIncoterm2Desc = (TextView) findViewById(R.id.tvIncoterm2Desc);
        tvPaytermDesc = (TextView) findViewById(R.id.tvPaytermDesc);
        tvUnloadingPointDesc = (TextView) findViewById(R.id.tvUnloadingPointDesc);
        tvReceivingPointDesc = (TextView) findViewById(R.id.tvReceivingPointDesc);
        tvRefDocDesc = (TextView) findViewById(R.id.tvRefDocDesc);
        tvSalesDistDesc = (TextView) findViewById(R.id.tvSalesDistDesc);
        tvBillToDesc = (TextView) findViewById(R.id.tvBillToDesc);
        tvBillTo = (TextView) findViewById(R.id.tvBillTo);
        tvOrderDateDesc = (TextView) findViewById(R.id.tvOrderDateDesc);
        tvReferenceHeader = (TextView) findViewById(R.id.tvReferenceHeader);
        tvCustomerPO = (TextView) findViewById(R.id.tvCustomerPO);
        tvTaxOneAmtVal = (TextView) findViewById(R.id.tvTaxOneAmtVal);
        tvTaxTwoAmtVal = (TextView) findViewById(R.id.tvTaxTwoAmtVal);
        tvTaxThreeAmtVal = (TextView) findViewById(R.id.tvTaxThreeAmtVal);
        llTaxOneAmt = (LinearLayout) findViewById(R.id.llTaxOneAmt);
        llTaxTwoAmt = (LinearLayout) findViewById(R.id.llTaxTwoAmt);
        llTaxThreeAmt = (LinearLayout) findViewById(R.id.llTaxThreeAmt);

        llSalesArea = (LinearLayout) findViewById(R.id.llSalesArea);
        ll_total_amt = (LinearLayout) findViewById(R.id.ll_total_amt);
        llPlant = (LinearLayout) findViewById(R.id.llPlant);
        llSalesOffice = (LinearLayout) findViewById(R.id.llSalesOffice);
        llSalesGroup = (LinearLayout) findViewById(R.id.llSalesGroup);
        llRefDoc = (LinearLayout) findViewById(R.id.llRefDoc);
        llCustomerPo = (LinearLayout) findViewById(R.id.llCustomerPo);
        llCustomerPoDate = (LinearLayout) findViewById(R.id.llCustomerPoDate);
        llShipToAdd = (LinearLayout) findViewById(R.id.llShipToAdd);
        llShippingType = (LinearLayout) findViewById(R.id.llShippingType);
        llIncoTerm1 = (LinearLayout) findViewById(R.id.llIncoTerm1);
        llIncoTerm2 = (LinearLayout) findViewById(R.id.llIncoTerm2);
        llUnloadingPoint = (LinearLayout) findViewById(R.id.llUnloadingPoint);
        llReceivingPoint = (LinearLayout) findViewById(R.id.llReceivingPoint);
        llMeansOfTranst = (LinearLayout) findViewById(R.id.llMeansOfTranst);
        llPaymentTerm = (LinearLayout) findViewById(R.id.llPaymentTerm);
        llBillTo = (LinearLayout) findViewById(R.id.llBillTo);
        llOrderDate = (LinearLayout) findViewById(R.id.llOrderDate);
//        ll_total_amt.setVisibility(View.GONE);
        llPaymentTerm.setVisibility(View.GONE);

        tvDate = (TextView) findViewById(R.id.tvDate);
        ivDeliveryStatus = (ImageView) findViewById(R.id.ivDeliveryStatus);
        cvOrderDetails = (CardView) findViewById(R.id.cvOrderDetails);
        cvPricingDetails = (CardView) findViewById(R.id.cvPricingDetails);
        cvItem = (CardView) findViewById(R.id.cvItem);

        ivExpandIcon = (ImageView) findViewById(R.id.ivOrderDetails);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvAmount = (TextView) findViewById(R.id.tvAmount);
        llHeader = (LinearLayout) findViewById(R.id.headerItem);
        llSOCondition = (LinearLayout) findViewById(R.id.llSOCondition);
        ivPricingDetails = (ImageView) findViewById(R.id.ivPricingDetails);
        ivExpandIcon.setOnClickListener(this);
        ivPricingDetails.setOnClickListener(this);

        /*item start*/
        View soItemTitelView = findViewById(R.id.soItemTitelView);
        TextView tvItemTitle = (TextView) soItemTitelView.findViewById(R.id.tv_heading);
        tvItemTitle.setText(getString(R.string.item_details_title));
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        llItemList = (LinearLayout) findViewById(R.id.llItemList);
        tvNoRecordFound = (TextView) findViewById(R.id.no_record_found);
        ivItemDetails = (ImageView) findViewById(R.id.ivItemDetails);
        ivItemDetails.setOnClickListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        roItemAdapter = new SimpleRecyclerViewAdapter<ReturnOrderItemBean>(ReturnOrderDetailActivity.this, R.layout.ro_item_material, this, recyclerView, tvNoRecordFoundItemDetails);
        recyclerView.setAdapter(roItemAdapter);
        context = ReturnOrderDetailActivity.this;
        presenter = new RODetailsPresenterImpl(ReturnOrderDetailActivity.this, this, 1, roListBean, isSessionRequired);

        displayHeaderList(roListBean);
    }

    private void setUI() {
        tvReturnNo.setText(roListBean.getRetOrdNo());

        tvOrderType.setText(roListBean.getCustomerName() + " (" + roListBean.getCustomerNo() + ")");

        tvOrderDateDesc.setText(roListBean.getOrderDate());
        tvDate.setText(roListBean.getOrderDate());

        Drawable img = SOUtils.displayReturnOrderStatusImage(roListBean.getStatusID(), roListBean.getGRStatusID(), this);
        ivDeliveryStatus.setImageDrawable(img);

        roItemAdapter.refreshAdapter(roListBean.getRoItemList());
        ConstantsUtils.focusOnView(nestedScroll);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_print, menu);
        MenuItem item = menu.findItem(R.id.menu_print);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

        }
        return true;
    }

    @Override
    public void displayHeaderList(ReturnOrderBean roListBean) {

        if (!TextUtils.isEmpty(roListBean.getShipToPartyName())) {
            tvShipToPartyName.setText(getString(R.string.po_details_display_value, roListBean.getShipToPartyName(), roListBean.getShipToParty()));
            tvAddress.setText(roListBean.getAddress());
            llShipToAdd.setVisibility(View.VISIBLE);
        } else {
            llShipToAdd.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(roListBean.getSalesAreaDesc())) {
            tvSalesAreaDesc.setText(getString(R.string.po_details_display_value, roListBean.getSalesAreaDesc(), roListBean.getSalesArea()));
            llSalesArea.setVisibility(View.VISIBLE);
        } else {
            llSalesArea.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(roListBean.getPlantDesc())) {
            tvPlantDesc.setText(getString(R.string.po_details_display_value, roListBean.getPlantDesc(), roListBean.getPlant()));
            llPlant.setVisibility(View.VISIBLE);
        } else {
            llPlant.setVisibility(View.GONE);
        }
        llSalesGroup.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(roListBean.getSalesOffDesc())) {
            tvSalesOfficeDesc.setText(getString(R.string.po_details_display_value, roListBean.getSalesOffDesc(), roListBean.getSalesOff()));
            llSalesOffice.setVisibility(View.VISIBLE);
        } else {
            llSalesOffice.setVisibility(View.GONE);
        }
        llCustomerPoDate.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(roListBean.getOrderReasonDesc())) {
            tvCustomerPODesc.setText(getString(R.string.po_details_display_value, roListBean.getOrderReasonDesc(), roListBean.getOrderReasonID()));
            llCustomerPo.setVisibility(View.VISIBLE);
            tvReferenceHeader.setVisibility(View.VISIBLE);
            tvReferenceHeader.setText("INFO");
            tvCustomerPO.setText("Reason");
        } else {
            llCustomerPo.setVisibility(View.GONE);
            tvReferenceHeader.setVisibility(View.GONE);
        }


//        if (llCustomerPoDate.getVisibility() == View.GONE && llCustomerPo.getVisibility() == View.GONE) {
//            tvReferenceHeader.setVisibility(View.GONE);
//        }
        llShippingType.setVisibility(View.GONE);
        llMeansOfTranst.setVisibility(View.GONE);
        llIncoTerm1.setVisibility(View.GONE);
        llIncoTerm2.setVisibility(View.GONE);
        llUnloadingPoint.setVisibility(View.GONE);
        llReceivingPoint.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(roListBean.getOrderDate())) {
            tvOrderDateDesc.setText(ConstantsUtils.convertDateIntoDisplayFormat(roListBean.getOrderDate()));
            llOrderDate.setVisibility(View.VISIBLE);
        } else {
            llOrderDate.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(roListBean.getTax1Amt())) {
//            tvTaxOneAmtVal.setText(UtilConstants.commaSeparator(UtilConstants.removeLeadingZero(roListBean.getTax1Amt())) + " " + roListBean.getCurrency());
            tvTaxOneAmtVal.setText(ConstantsUtils.commaSeparator(UtilConstants.removeLeadingZero(roListBean.getTax1Amt()), roListBean.getCurrency()) + " " + roListBean.getCurrency());
            llTaxOneAmt.setVisibility(View.GONE);
        } else {
            llTaxOneAmt.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(roListBean.getTax2Amt())) {
//            tvTaxTwoAmtVal.setText(UtilConstants.commaSeparator(UtilConstants.removeLeadingZero(roListBean.getTax2Amt())) + " " + roListBean.getCurrency());
            tvTaxTwoAmtVal.setText(ConstantsUtils.commaSeparator(UtilConstants.removeLeadingZero(roListBean.getTax2Amt()), roListBean.getCurrency()) + " " + roListBean.getCurrency());
            llTaxTwoAmt.setVisibility(View.GONE);
        } else {
            llTaxTwoAmt.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(roListBean.getTax3Amt())) {
//            tvTaxThreeAmtVal.setText(UtilConstants.commaSeparator(UtilConstants.removeLeadingZero(roListBean.getTax3Amt())) + " " + roListBean.getCurrency());
            tvTaxThreeAmtVal.setText(ConstantsUtils.commaSeparator(UtilConstants.removeLeadingZero(roListBean.getTax3Amt()), roListBean.getCurrency()) + " " + roListBean.getCurrency());
            llTaxThreeAmt.setVisibility(View.GONE);
        } else {
            llTaxThreeAmt.setVisibility(View.GONE);
        }

        tvTerms.setVisibility(View.GONE);
        cl_payment_terms.setVisibility(View.GONE);
        llPaymentTerm.setVisibility(View.GONE);
        llRefDoc.setVisibility(View.GONE);
        llBillTo.setVisibility(View.GONE);
        setUI();
        displayConditionItemDetails(roListBean);
    }

    private void displayConditionItemDetails(ReturnOrderBean roListBean) {
        try {
            llSOCondition.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvAmount.setText(ConstantsUtils.commaSeparator(UtilConstants.removeLeadingZero(roListBean.getNetAmount()), roListBean.getCurrency()) + " " + roListBean.getCurrency());
        TextView[] tvDescription = new TextView[6];
        TextView[] tvTotalAmount = new TextView[6];
        TableLayout tableScheduleHeading = (TableLayout) LayoutInflater.from(this).inflate(R.layout.table_view, null);
        if (!TextUtils.isEmpty(roListBean.getUnitPrice())) {
            LinearLayout rowScheduleItem = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.so_condition_item, null);
            tvDescription[0] = (TextView) rowScheduleItem.findViewById(R.id.tvDescription);
            tvTotalAmount[0] = (TextView) rowScheduleItem.findViewById(R.id.tvTotalAmount);
            String strAmount = ConstantsUtils.commaSeparator(UtilConstants.removeLeadingZero(roListBean.getUnitPrice()), roListBean.getCurrency()) + " " + roListBean.getCurrency();
            tvDescription[0].setText(getString(R.string.str_unit_price));
            tvTotalAmount[0].setText(strAmount);
            tableScheduleHeading.addView(rowScheduleItem);
        }

        if (!TextUtils.isEmpty(roListBean.getPriDiscAmt())) {
            LinearLayout rowScheduleItem = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.so_condition_item, null);
            tvDescription[1] = (TextView) rowScheduleItem.findViewById(R.id.tvDescription);
            tvTotalAmount[1] = (TextView) rowScheduleItem.findViewById(R.id.tvTotalAmount);
            String strAmount = ConstantsUtils.commaSeparator(UtilConstants.removeLeadingZero(roListBean.getPriDiscAmt()), roListBean.getCurrency()) + " " + roListBean.getCurrency();
            tvDescription[1].setText(getString(R.string.str_dis_amt));
            tvTotalAmount[1].setText(strAmount);
            tableScheduleHeading.addView(rowScheduleItem);
        }

        if (!TextUtils.isEmpty(roListBean.getTax1Amt())) {
            LinearLayout rowScheduleItem = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.so_condition_item, null);
            tvDescription[2] = (TextView) rowScheduleItem.findViewById(R.id.tvDescription);
            tvTotalAmount[2] = (TextView) rowScheduleItem.findViewById(R.id.tvTotalAmount);
            String strAmount = ConstantsUtils.commaSeparator(UtilConstants.removeLeadingZero(roListBean.getTax1Amt()), roListBean.getCurrency()) + " " + roListBean.getCurrency();
            tvDescription[2].setText(getString(R.string.str_tax_one_amt));
            tvTotalAmount[2].setText(strAmount);
            tableScheduleHeading.addView(rowScheduleItem);
        }

        if (!TextUtils.isEmpty(roListBean.getTax2Amt())) {
            LinearLayout rowScheduleItem = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.so_condition_item, null);
            tvDescription[3] = (TextView) rowScheduleItem.findViewById(R.id.tvDescription);
            tvTotalAmount[3] = (TextView) rowScheduleItem.findViewById(R.id.tvTotalAmount);
            String strAmount = ConstantsUtils.commaSeparator(UtilConstants.removeLeadingZero(roListBean.getTax2Amt()), roListBean.getCurrency()) + " " + roListBean.getCurrency();
            tvDescription[3].setText(getString(R.string.str_tax_two_amt));
            tvTotalAmount[3].setText(strAmount);
            tableScheduleHeading.addView(rowScheduleItem);
        }

        if (!TextUtils.isEmpty(roListBean.getTax3Amt())) {
            LinearLayout rowScheduleItem = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.so_condition_item, null);
            tvDescription[4] = (TextView) rowScheduleItem.findViewById(R.id.tvDescription);
            tvTotalAmount[4] = (TextView) rowScheduleItem.findViewById(R.id.tvTotalAmount);
            String strAmount = ConstantsUtils.commaSeparator(UtilConstants.removeLeadingZero(roListBean.getTax3Amt()), roListBean.getCurrency()) + " " + roListBean.getCurrency();
            tvDescription[4].setText(getString(R.string.str_tax_three_amt));
            tvTotalAmount[4].setText(strAmount);
            tableScheduleHeading.addView(rowScheduleItem);
        }
        if (!TextUtils.isEmpty(roListBean.getNetAmount())) {
            LinearLayout rowScheduleItem = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.so_condition_item, null);
            tvDescription[5] = (TextView) rowScheduleItem.findViewById(R.id.tvDescription);
            tvTotalAmount[5] = (TextView) rowScheduleItem.findViewById(R.id.tvTotalAmount);
            String strAmount = ConstantsUtils.commaSeparator(UtilConstants.removeLeadingZero(roListBean.getNetAmount()), roListBean.getCurrency()) + " " + roListBean.getCurrency();
            tvDescription[5].setText(getString(R.string.str_net_amt));
            tvTotalAmount[5].setText(strAmount);
            tableScheduleHeading.addView(rowScheduleItem);
        }
        try {
            llSOCondition.addView(tableScheduleHeading);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showProgressDialog(String s) {

    }

    @Override
    public void hideProgressDialog() {

    }

    @Override
    public void showMessage(String message, boolean isSimpleDialog) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
           /* case R.id.ivOrderDetails:
                if (llHeader.getVisibility() == View.VISIBLE) {
                    ivExpandIcon.setImageResource(R.drawable.ic_arrow_down_black_24dp);
                    llHeader.setVisibility(View.GONE);
                    tvDate.setVisibility(View.VISIBLE);
//                    ViewGroup.MarginLayoutParams layoutParams = ConstantsUtils.getLayoutParams(cvOrderDetails);
//                    int marginB = ConstantsUtils.dpToPx(8, mContext);
                    *//*if (llSOCondition.getVisibility() == View.VISIBLE) {
                        if (ConstantsUtils.getLayoutParams(cvPricingDetails).topMargin != 0) {
                            marginB = 0;
                        }
                    } else {
                        marginB = 0;
                        ViewGroup.MarginLayoutParams layoutParamss = ConstantsUtils.getLayoutParams(cvPricingDetails);
                        layoutParamss.setMargins(ConstantsUtils.dpToPx(8, mContext), 0, ConstantsUtils.dpToPx(8, mContext), layoutParamss.bottomMargin);
                        cvPricingDetails.requestLayout();
                    }*//*
//                    layoutParams.setMargins(ConstantsUtils.dpToPx(8, mContext), ConstantsUtils.dpToPx(8, mContext), ConstantsUtils.dpToPx(8, mContext), marginB);
//                    cvOrderDetails.requestLayout();
                } else {
                    ivExpandIcon.setImageResource(R.drawable.ic_arrow_up_black_24dp);
                    llHeader.setVisibility(View.VISIBLE);
                    tvDate.setVisibility(View.GONE);
//                    int marginB = ConstantsUtils.dpToPx(8, mContext);
//                    if (llSOCondition.getVisibility() == View.VISIBLE) {
//                        if (ConstantsUtils.getLayoutParams(cvPricingDetails).topMargin != 0) {
//                            marginB = 0;
//                        }
//                    }
//                    ViewGroup.MarginLayoutParams layoutParams = ConstantsUtils.getLayoutParams(cvOrderDetails);
//                    layoutParams.setMargins(ConstantsUtils.dpToPx(8, mContext), ConstantsUtils.dpToPx(8, mContext), ConstantsUtils.dpToPx(8, mContext), marginB);

//                    cvOrderDetails.requestLayout();

                }
                break;*/
            case R.id.ivOrderDetails:
                if (llHeader.getVisibility() == View.VISIBLE) {
                    ivExpandIcon.setImageResource(R.drawable.ic_arrow_down_black_24dp);
                    llHeader.setVisibility(View.GONE);
                    tvDate.setVisibility(View.VISIBLE);
                    ViewGroup.MarginLayoutParams layoutParams = ConstantsUtils.getLayoutParams(cvOrderDetails);
                    int marginB = ConstantsUtils.dpToPx(8, mContext);
                    if (llSOCondition.getVisibility() == View.VISIBLE) {
                        if (ConstantsUtils.getLayoutParams(cvPricingDetails).topMargin != 0) {
                            marginB = 0;
                        }
                    } else {
                        marginB = 0;
                        ViewGroup.MarginLayoutParams layoutParamss = ConstantsUtils.getLayoutParams(cvPricingDetails);
                        layoutParamss.setMargins(ConstantsUtils.dpToPx(8, mContext), 0, ConstantsUtils.dpToPx(8, mContext), layoutParamss.bottomMargin);
                        cvPricingDetails.requestLayout();
                    }
                    layoutParams.setMargins(ConstantsUtils.dpToPx(8, mContext), ConstantsUtils.dpToPx(8, mContext), ConstantsUtils.dpToPx(8, mContext), marginB);
                    cvOrderDetails.requestLayout();
                } else {
                    ivExpandIcon.setImageResource(R.drawable.ic_arrow_up_black_24dp);
                    llHeader.setVisibility(View.VISIBLE);
                    tvDate.setVisibility(View.GONE);
                    int marginB = ConstantsUtils.dpToPx(8, mContext);
                    if (llSOCondition.getVisibility() == View.VISIBLE) {
                        if (ConstantsUtils.getLayoutParams(cvPricingDetails).topMargin != 0) {
                            marginB = 0;
                        }
                    }
                    ViewGroup.MarginLayoutParams layoutParams = ConstantsUtils.getLayoutParams(cvOrderDetails);
                    layoutParams.setMargins(ConstantsUtils.dpToPx(8, mContext), ConstantsUtils.dpToPx(8, mContext), ConstantsUtils.dpToPx(8, mContext), marginB);

                    cvOrderDetails.requestLayout();

                }
                break;
            case R.id.ivPricingDetails:
                if (llSOCondition.getVisibility() == View.VISIBLE) {
                    ivPricingDetails.setImageResource(R.drawable.ic_arrow_down_black_24dp);
                    llSOCondition.setVisibility(View.GONE);
                    tvAmount.setVisibility(View.VISIBLE);
                    int marginB = ConstantsUtils.dpToPx(8, mContext);
                    int marginT = ConstantsUtils.dpToPx(8, mContext);
                    if (llItemList.getVisibility() == View.VISIBLE) {
                        if (ConstantsUtils.getLayoutParams(cvItem).topMargin != 0) {
                            marginB = 0;
                        }
                    } else {
                        marginB = 0;
                        ViewGroup.MarginLayoutParams layoutParamss = ConstantsUtils.getLayoutParams(cvItem);
                        layoutParamss.setMargins(ConstantsUtils.dpToPx(8, mContext), 0, ConstantsUtils.dpToPx(8, mContext), 0);
                        cvItem.requestLayout();
                    }
                    if (llHeader.getVisibility() == View.VISIBLE) {
                        if (ConstantsUtils.getLayoutParams(cvOrderDetails).bottomMargin != 0) {
                            marginT = 0;
                        }
                    } else {
                        marginT = 0;
                        ViewGroup.MarginLayoutParams layoutParamss = ConstantsUtils.getLayoutParams(cvOrderDetails);
                        layoutParamss.setMargins(ConstantsUtils.dpToPx(8, mContext), ConstantsUtils.dpToPx(8, mContext), ConstantsUtils.dpToPx(8, mContext), 0);
                        cvOrderDetails.requestLayout();
                    }

                    ViewGroup.MarginLayoutParams layoutParams = ConstantsUtils.getLayoutParams(cvPricingDetails);
                    layoutParams.setMargins(ConstantsUtils.dpToPx(8, mContext), marginT, ConstantsUtils.dpToPx(8, mContext), marginB);
                    cvPricingDetails.requestLayout();

                } else {
                    ivPricingDetails.setImageResource(R.drawable.ic_arrow_up_black_24dp);
                    llSOCondition.setVisibility(View.VISIBLE);
                    tvAmount.setVisibility(View.GONE);
                    int marginB = 0;//ConstantsUtils.dpToPx(8,mContext);
                    int marginT = ConstantsUtils.dpToPx(8, mContext);
                    if (llItemList.getVisibility() == View.VISIBLE) {
                        if (ConstantsUtils.getLayoutParams(cvItem).topMargin != 0) {
                            marginB = 0;
                        }
                    }
                    if (llHeader.getVisibility() == View.VISIBLE) {
                        if (ConstantsUtils.getLayoutParams(cvOrderDetails).bottomMargin != 0) {
                            marginT = 0;
                        }
                    }
                    ViewGroup.MarginLayoutParams layoutParams =
                            (ViewGroup.MarginLayoutParams) cvPricingDetails.getLayoutParams();
                    layoutParams.setMargins(ConstantsUtils.dpToPx(8, mContext), marginT, ConstantsUtils.dpToPx(8, mContext), marginB);
                    cvPricingDetails.requestLayout();
                }
                break;

        }
    }


    @Override
    public void onItemClick(ReturnOrderItemBean returnOrderItemBean, View view, int i) {
//        presenter.onItemClick(returnOrderItemBean);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View viewItem) {
        return new ReturnOrderItemVH(viewItem, ReturnOrderDetailActivity.this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, ReturnOrderItemBean returnOrderItemBean) {
        Drawable img = SOUtils.displayReturnOrderStatusImage(returnOrderItemBean.getStatusID(), returnOrderItemBean.getGRStatusID(), this);
        ((ReturnOrderItemVH) viewHolder).ivDelvStatus.setImageDrawable(img);
        ((ReturnOrderItemVH) viewHolder).tvMaterialDesc.setText(returnOrderItemBean.getROMaterialDescAndNo());
        ((ReturnOrderItemVH) viewHolder).tvQty.setText(returnOrderItemBean.getQuantity() + " " + returnOrderItemBean.getUOM());
//        ((ReturnOrderItemVH) viewHolder).tvAmount.setText(UtilConstants.commaSeparator(UtilConstants.removeLeadingZero(returnOrderItemBean.getUnitPrice())) + " " + returnOrderItemBean.getCurrency());
        ((ReturnOrderItemVH) viewHolder).tvAmount.setText(ConstantsUtils.commaSeparator(UtilConstants.removeLeadingZero(returnOrderItemBean.getUnitPrice()), returnOrderItemBean.getCurrency()) + " " + returnOrderItemBean.getCurrency());
    }

}
