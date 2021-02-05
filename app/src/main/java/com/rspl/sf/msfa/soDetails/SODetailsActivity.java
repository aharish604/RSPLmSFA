package com.rspl.sf.msfa.soDetails;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.CustomerDetailsActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.so.SOUtils;
import com.rspl.sf.msfa.soapproval.SOApproveActivity;
import com.rspl.sf.msfa.socreate.CreditLimitBean;
import com.rspl.sf.msfa.socreate.CustomerPartnerFunctionBean;
import com.rspl.sf.msfa.socreate.SOConditionItemDetaiBean;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.socreate.shipToDetails.ShipToDetailsActivity;
import com.rspl.sf.msfa.socreate.stepOne.SOCreateActivity;
import com.rspl.sf.msfa.solist.SOListBean;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SODetailsActivity extends AppCompatActivity implements SODetailsView, CustomDialogCallBack, View.OnClickListener, AdapterInterface<SOItemBean> {

    private int comingFrom = 0;
    private SOListBean soListBean = null;
    private SODetailsPresenterImpl presenter;
    private ImageView ivExpandIcon;
    private LinearLayout llHeader;
    private RecyclerView recyclerView;
    private TextView tvNoRecordFound;
    private SimpleRecyclerViewAdapter<SOItemBean> soItemAdapter;
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
    private SODetailsActivity mContext;
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
    private TextView tvOrderType;
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
    private LinearLayout llSalesArea;
    private LinearLayout llPlant;
    private LinearLayout llRefDoc, llCustomerPo, llCustomerPoDate, llShipToAdd, llShippingType, llIncoTerm1, llIncoTerm2, llUnloadingPoint, llReceivingPoint, llMeansOfTranst, llPaymentTerm;
    private LinearLayout llBillTo;
    private TextView tvBillToDesc;
    private TextView tvBillTo;
    private TextView tvOrderDateDesc;
    private LinearLayout llOrderDate;
    private TextView tvReferenceHeader;
    private boolean fromSoCreate = false;
    private LinearLayout llSalesOffice;
    private LinearLayout llSalesGroup;
    private TextView tvCreditAmount,tvCreditAmountFBD;
    private List<CreditLimitBean> creditLimitBean;
    private TextView tvTotlWeightAmt;
    private MenuItem menuSave = null;
    ArrayList<SOItemBean> soitemlist=new ArrayList<>();

    String customerNo="";
    private boolean isClickable = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_so_details_new);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            comingFrom = bundle.getInt(Constants.EXTRA_COME_FROM, 0);
            soListBean = (SOListBean) bundle.getSerializable(Constants.EXTRA_HEADER_BEAN);
            isSessionRequired = bundle.getBoolean(Constants.EXTRA_SESSION_REQUIRED, false);
            soNumber = bundle.getString(Constants.EXTRA_POS, "");
            fromSoCreate = bundle.getBoolean(Constants.EXTRA_SO_CREATE_TITLE);
            creditLimitBean = soListBean.getCreditControlAreas();
        }
        if (soListBean == null) {
            soListBean = new SOListBean();
        }
         customerNo = soListBean.getSoldTo();

        if (comingFrom == ConstantsUtils.SO_APPROVAL_EDIT_ACTIVITY) {
            ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.menu_sos_edit), 0);
            if (getSupportActionBar() != null)
                getSupportActionBar().setSubtitle(getString(R.string.so_review_sub_title));
        } else if (comingFrom == ConstantsUtils.SO_CREATE_ACTIVITY) {
            ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.menu_sos_create), 0);
            if (getSupportActionBar() != null)
                getSupportActionBar().setSubtitle(getString(R.string.so_review_sub_title));
        } else
            ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.po_details_title), 0);
//        else
//            ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.menu_so_item_details), 0);


        try{
            SharedPreferences sharedPreferences = SODetailsActivity.this.getSharedPreferences(Constants.PREFS_NAME, 0);
            if (sharedPreferences.getBoolean("writeDBGLog", false)) {
                Constants.writeDebug = sharedPreferences.getBoolean("writeDBGLog", false);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        mContext = SODetailsActivity.this;
        initUI();
        presenter = new SODetailsPresenterImpl(SODetailsActivity.this,customerNo, this, new SODetailsModelImpl(), comingFrom, soListBean, isSessionRequired);
        presenter.onStart();
    }

    private void initUI() {
        ly_so_header = (LinearLayout) findViewById(R.id.ly_so_header);
        ly_so_header.setVisibility(View.GONE);
        tvCustomerName = (TextView) findViewById(R.id.tv_header_title);
        tvCustomerId = (TextView) findViewById(R.id.tv_header_id);
        nestedScroll = (NestedScrollView) findViewById(R.id.nestedScroll);
        fabEdit = (FloatingActionButton) findViewById(R.id.fabEdit);
        tvTotlWeightAmt = (TextView) findViewById(R.id.tvTotlWeightAmt);
        fabEdit.setOnClickListener(this);

        /*init UI*/
        ivExpandIcon = (ImageView) findViewById(R.id.ivOrderDetails);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        llHeader = (LinearLayout) findViewById(R.id.headerItem);
        ivExpandIcon.setOnClickListener(this);

        /*item start*/
        View soItemTitelView = findViewById(R.id.soItemTitelView);
        TextView tvItemTitle = (TextView) soItemTitelView.findViewById(R.id.tv_heading);
        tvItemTitle.setText(getString(R.string.item_details_title));
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        llItemList = (LinearLayout) findViewById(R.id.llItemList);
        tvNoRecordFound = (TextView) findViewById(R.id.no_record_found);
        ivItemDetails = (ImageView) findViewById(R.id.ivItemDetails);
        ivItemDetails.setOnClickListener(this);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        soItemAdapter = new SimpleRecyclerViewAdapter<SOItemBean>(SODetailsActivity.this, R.layout.so_item_material_review, this, recyclerView, tvNoRecordFound);
        recyclerView.setAdapter(soItemAdapter);
        cvItem = (CardView) findViewById(R.id.cvItem);
        /*item end*/

        /*notes start*/
        notesHeaderView = findViewById(R.id.ll_notes_list_header);
        View soNotesTitelView = findViewById(R.id.notesView);
        notesItemView = findViewById(R.id.so_notes_item);
        btPostNotes = (ImageView) findViewById(R.id.btPostNotes);
        etNotes = (EditText) findViewById(R.id.etNote);
        TextView tvNotesTitle = (TextView) soNotesTitelView.findViewById(R.id.tv_heading);
        tvNotesTitle.setText(getString(R.string.notes));
        tvNotesHeader = (TextView) notesItemView.findViewById(R.id.tvNotesHeader);
        tvNotesText = (TextView) notesItemView.findViewById(R.id.tvNotesText);
        btPostNotes.setOnClickListener(this);
        /*notes end*/

        /*header details*/
        tvCreditAmount = (TextView) findViewById(R.id.tvCreditAmount);
        tvCreditAmountFBD = (TextView) findViewById(R.id.tvCreditAmountFBD);
        tvSONo = (TextView) findViewById(R.id.tvSONo);
        tvAmount = (TextView) findViewById(R.id.tvAmount);
        tvOrderType = (TextView) findViewById(R.id.tvOrderType);
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

        llSalesArea = (LinearLayout) findViewById(R.id.llSalesArea);
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


        tvDate = (TextView) findViewById(R.id.tvDate);
        ivDeliveryStatus = (ImageView) findViewById(R.id.ivDeliveryStatus);
        cvOrderDetails = (CardView) findViewById(R.id.cvOrderDetails);
        Drawable delvStatusImg = SOUtils.displayStatusImage(soListBean.getStatus(), soListBean.getDelvStatus(), mContext);
        if (delvStatusImg != null) {
            ivDeliveryStatus.setImageDrawable(delvStatusImg);
        }
        /*header detial end*/

        /*condition item details*/
        llSOCondition = (LinearLayout) findViewById(R.id.llSOCondition);
        ivPricingDetails = (ImageView) findViewById(R.id.ivPricingDetails);
        ivPricingDetails.setOnClickListener(this);
        cvPricingDetails = (CardView) findViewById(R.id.cvPricingDetails);

        /*end condition item details*/

        /*view visibility based on coming from*/
        if (comingFrom == ConstantsUtils.SO_CREATE_ACTIVITY || comingFrom == ConstantsUtils.SO_EDIT_ACTIVITY || comingFrom == ConstantsUtils.SO_CREATE_SINGLE_MATERIAL) {
            notesHeaderView.setVisibility(View.GONE);
        }

    }


    @Override
    public void displayHeaderList(SOListBean soListBean) {
        /*title*/
        tvCustomerName.setText(soListBean.getShipToName());
        tvCustomerId.setText(soListBean.getShipTo());
        String totalQty ="";
        if (!TextUtils.isEmpty(soListBean.getmSteTotalQtyUOM())){
            try {
                totalQty = ConstantsUtils.checkNoUOMZero(soListBean.getmSteTotalQtyUOM(),soListBean.getQuantity()+" "+soListBean.getmSteTotalQtyUOM());
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }else {
            try {
                totalQty = ConstantsUtils.checkNoUOMZero("",soListBean.getQuantity());
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }
        try {
            tvTotlWeightAmt.setText(ConstantsUtils.checkNoUOMZero(soListBean.getmStrWeightUOM(),soListBean.getmStrTotalWeight())+" "+soListBean.getmStrWeightUOM()+" / "+totalQty);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }


        soitemlist=soListBean.getSoItemBeanArrayList();
        soItemAdapter.refreshAdapter(soitemlist);
        /*title*/
        if (comingFrom == ConstantsUtils.SO_APPROVAL_EDIT_ACTIVITY) {
            tvSONo.setText(soListBean.getSONo());
            displayCreditAmounts();
        } else if (fromSoCreate) {
            tvSONo.setText(getString(R.string.so_new_order));
            displayCreditAmounts();

        } else
            tvSONo.setText(soListBean.getSONo());
        if (!TextUtils.isEmpty(soListBean.getOrderDate())) {
            tvDate.setText(soListBean.getOrderDate());
        } else {
            Calendar calendarCurrent = Calendar.getInstance();
            tvDate.setText(ConstantsUtils.convertCalenderToDisplayDateFormat(calendarCurrent, ConstantsUtils.getDisplayDateFormat(SODetailsActivity.this)));
        }
        tvOrderType.setText(getString(R.string.po_details_display_value, soListBean.getOrderTypeDesc(), soListBean.getOrderType()));
        tvAmount.setText(UtilConstants.commaSeparator(soListBean.getTotalAmt()) + " " + soListBean.getCurrency());
        if (!TextUtils.isEmpty(soListBean.getShipToName())) {
            tvShipToPartyName.setText(getString(R.string.po_details_display_value, soListBean.getShipToName(), soListBean.getShipTo()));
            tvAddress.setText(soListBean.getAddress());
            llShipToAdd.setVisibility(View.VISIBLE);
        } else {
            llShipToAdd.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(soListBean.getSalesAreaDesc())) {
            tvSalesAreaDesc.setText(getString(R.string.po_details_display_value, soListBean.getSalesAreaDesc(), soListBean.getSalesArea()));
            llSalesArea.setVisibility(View.VISIBLE);
        } else {
            llSalesArea.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(soListBean.getPlantDesc())) {
            tvPlantDesc.setText(getString(R.string.po_details_display_value, soListBean.getPlantDesc(), soListBean.getPlant()));
            llPlant.setVisibility(View.GONE);
        } else {
            llPlant.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(soListBean.getSaleGrpDesc())) {
            tvSalesGroupDesc.setText(getString(R.string.po_details_display_value, soListBean.getSaleGrpDesc(), soListBean.getSalesGroup()));
            llSalesGroup.setVisibility(View.VISIBLE);
        } else {
            llSalesGroup.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(soListBean.getSaleOffDesc())) {
            tvSalesOfficeDesc.setText(getString(R.string.po_details_display_value, soListBean.getSaleOffDesc(), soListBean.getSalesOfficeId()));
            llSalesOffice.setVisibility(View.VISIBLE);
        } else {
            llSalesOffice.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(soListBean.getPONo())) {
            tvCustomerPODesc.setText(soListBean.getPONo());
            llCustomerPo.setVisibility(View.VISIBLE);
        } else {
            llCustomerPo.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(soListBean.getPODate())) {
            tvCustomerPODateDesc.setText(soListBean.getPODate());
            llCustomerPoDate.setVisibility(View.VISIBLE);
        } else {
            llCustomerPoDate.setVisibility(View.GONE);
        }
        if (llCustomerPoDate.getVisibility() == View.GONE && llCustomerPo.getVisibility() == View.GONE) {
            tvReferenceHeader.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(soListBean.getShippingPointDesc())) {
            tvShippingTypeDesc.setText(getString(R.string.po_details_display_value, soListBean.getShippingPointDesc(), soListBean.getShippingPoint()));
            llShippingType.setVisibility(View.VISIBLE);
        } else {
            llShippingType.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(soListBean.getMeansOfTranstypDesc())) {
            tvMeansOfTranstDesc.setText(getString(R.string.po_details_display_value, soListBean.getMeansOfTranstypDesc(), soListBean.getMeansOfTranstyp()));
            llMeansOfTranst.setVisibility(View.GONE);
        } else {
            llMeansOfTranst.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(soListBean.getIncoterm1Desc())) {
            tvIncoterm1Desc.setText(getString(R.string.po_details_display_value, soListBean.getIncoterm1Desc(), soListBean.getIncoTerm1()));
            llIncoTerm1.setVisibility(View.GONE);
        } else {
            llIncoTerm1.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(soListBean.getIncoterm2())) {
            tvIncoterm2Desc.setText(soListBean.getIncoterm2());
            llIncoTerm2.setVisibility(View.GONE);
        } else {
            llIncoTerm2.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(soListBean.getUnloadingPointDesc())) {
            tvUnloadingPointDesc.setText(soListBean.getUnloadingPointDesc());
            llUnloadingPoint.setVisibility(View.GONE);
        } else {
            llUnloadingPoint.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(soListBean.getReceivingPointDesc())) {
            tvReceivingPointDesc.setText(soListBean.getReceivingPointDesc());
            llReceivingPoint.setVisibility(View.GONE);
        } else {
            llReceivingPoint.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(soListBean.getOrderDate())) {
            tvOrderDateDesc.setText(soListBean.getOrderDate());
            llOrderDate.setVisibility(View.VISIBLE);
        } else {
            llOrderDate.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(soListBean.getPaymentTermDesc())) {
            tvPaytermDesc.setText(getString(R.string.po_details_display_value, soListBean.getPaymentTermDesc(), soListBean.getPaymentTerm()));
            llPaymentTerm.setVisibility(View.VISIBLE);
        } else {
            llPaymentTerm.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(soListBean.getRefDocCat())) {
            tvRefDocDesc.setText(getString(R.string.po_details_display_value, soListBean.getRefDocCat(), soListBean.getRefDocNo()));
            llRefDoc.setVisibility(View.VISIBLE);
        } else {
            llRefDoc.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(soListBean.getRefDocCat())) {
            tvRefDocDesc.setText(getString(R.string.po_details_display_value, soListBean.getRefDocCat(), soListBean.getRefDocNo()));
            llRefDoc.setVisibility(View.VISIBLE);
        } else {
            llRefDoc.setVisibility(View.GONE);
        }
        llBillTo.setVisibility(View.GONE);
        if (!soListBean.getCustomerPartnerFunctionList().isEmpty()) {
            for (CustomerPartnerFunctionBean customerPartnerFunctionBean : soListBean.getCustomerPartnerFunctionList()) {
                if (customerPartnerFunctionBean.getPartnerFunctionID().equalsIgnoreCase(Constants.RE)) {
                    tvBillTo.setText(getString(R.string.po_details_display_value, customerPartnerFunctionBean.getPartnerCustomerName(), customerPartnerFunctionBean.getPartnerCustomerNo()));
                    tvBillToDesc.setText(SOUtils.getAddressValue(customerPartnerFunctionBean));
                    llBillTo.setVisibility(View.VISIBLE);
                }
            }
        }
        if (!soListBean.getSoItemBeanArrayList().isEmpty())
            displayConditionItemDetails(soListBean.getSoItemBeanArrayList().get(0));
        ConstantsUtils.focusOnView(nestedScroll);
    }

    private void displayCreditAmounts() {
        if (creditLimitBean != null && creditLimitBean.size()>0){
            for(CreditLimitBean creditID : creditLimitBean){
                if(creditID.getCreditControlAreaID().equalsIgnoreCase("1010")){
                    tvCreditAmount.setText("Total Balance:- "+ConstantsUtils.commaSeparator(creditID.getBalanceAmount(), creditID.getCurrency()) + " " + creditID.getCurrency());
                }else if(creditID.getCreditControlAreaID().equalsIgnoreCase("1030")){

                    tvCreditAmountFBD.setVisibility(View.GONE);
                    // tvCreditAmountFBD.setText("FBD : "+ConstantsUtils.commaSeparator(creditID.getBalanceAmount(), creditID.getCurrency()) + " " + creditID.getCurrency());
                }
            }


        }
    }

    private void displayConditionItemDetails(SOItemBean soItemBean) {
        int conditionTotalSize = soItemBean.getConditionItemDetaiBeanArrayList().size();
        if (conditionTotalSize > 0) {
            try {
                llSOCondition.removeAllViews();
            } catch (Exception e) {
                e.printStackTrace();
            }
            TextView[] tvDescription = new TextView[conditionTotalSize];
            TextView[] tvTotalAmount = new TextView[conditionTotalSize];
            TableLayout tableScheduleHeading = (TableLayout) LayoutInflater.from(this).inflate(R.layout.table_view, null);
            for (int j = 0; j < conditionTotalSize; j++) {
                SOConditionItemDetaiBean soScheduleBean = soItemBean.getConditionItemDetaiBeanArrayList().get(j);
                if (!soScheduleBean.getViewType().equalsIgnoreCase("T")) {
                    LinearLayout rowScheduleItem = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.so_condition_item, null);
                    tvDescription[j] = (TextView) rowScheduleItem.findViewById(R.id.tvDescription);
                    tvTotalAmount[j] = (TextView) rowScheduleItem.findViewById(R.id.tvTotalAmount);
                    String strAmount = "";
                    if (!TextUtils.isEmpty(soScheduleBean.getCondCurrency())) {
                        strAmount = getString(R.string.po_details_display_value, soScheduleBean.getName(), ConstantsUtils.removeDecimalValueIfDecimalIsZero(soScheduleBean.getAmount()) + soScheduleBean.getCondCurrency());
                    } else {
                        strAmount = soScheduleBean.getName();
                    }
                    tvDescription[j].setText(strAmount);
                    tvTotalAmount[j].setText(UtilConstants.commaSeparator(soScheduleBean.getConditionValue()));
                    tableScheduleHeading.addView(rowScheduleItem);
                } else {
                    LinearLayout rowScheduleItem = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.so_condition_total_item, null);
                    tvDescription[j] = (TextView) rowScheduleItem.findViewById(R.id.tvDescription);
                    tvTotalAmount[j] = (TextView) rowScheduleItem.findViewById(R.id.tvTotalAmount);
                    tvDescription[j].setText(soScheduleBean.getName());
                    tvTotalAmount[j].setText(UtilConstants.commaSeparator(soScheduleBean.getConditionValue()));
                    tableScheduleHeading.addView(rowScheduleItem);
                }
            }
            llSOCondition.addView(tableScheduleHeading);
        }
        /*finished condition */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (comingFrom == ConstantsUtils.SO_CREATE_ACTIVITY || comingFrom == ConstantsUtils.SO_CREATE_SINGLE_MATERIAL || comingFrom == ConstantsUtils.SO_EDIT_ACTIVITY || comingFrom == ConstantsUtils.SO_APPROVAL_EDIT_ACTIVITY || comingFrom == ConstantsUtils.SO_EDIT_SINGLE_MATERIAL) {//so create
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_save, menu);
            MenuItem item = menu.findItem(R.id.menu_submit);
            menuSave = menu.findItem(R.id.menu_submit);
            if (comingFrom == ConstantsUtils.SO_APPROVAL_EDIT_ACTIVITY)
                item.setTitle(getString(R.string.menu_approve));
        } else if (comingFrom == ConstantsUtils.SO_CREATE_CC_ACTIVITY || comingFrom == ConstantsUtils.SO_MULTIPLE_MATERIAL || comingFrom == ConstantsUtils.SO_SINGLE_MATERIAL) {//so create
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_details, menu);
            MenuItem item = menu.findItem(R.id.menu_submit);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                //onBackPressed();
                Intent intent = new Intent(SODetailsActivity.this, ShipToDetailsActivity.class);
                intent.putExtra(Constants.EXTRA_HEADER_BEAN, soListBean);
                intent.putExtra(Constants.EXTRA_SO_CREATE_TITLE, true);
             //       intent.putExtra(Constants.CustomerName, mStrCustomerName);
             //        intent.putExtra(Constants.CustomerNo, mStrCustomerNo);
                // intent.putExtra(Constants.EXTRA_SO_CREDIT_LIMIT, limitBeanList);
              //  intent.putExtra(Constants.EXTRA_COME_FROM, comingFrom);
              //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                finish();


                break;
            case R.id.menu_submit:
                //save
                menuSave.setEnabled(false);
                if (ConstantsUtils.isAutomaticTimeZone(SODetailsActivity.this)) {
                    if (comingFrom == ConstantsUtils.SO_EDIT_ACTIVITY) {
                        if(Constants.writeDebug){
                            LogManager.writeLogDebug("SO Edit  Saving in progress");
                        }
                        presenter.onAsignData(getString(R.string.edit_so), "", "");
                    } else if (comingFrom == ConstantsUtils.SO_APPROVAL_EDIT_ACTIVITY) {
                        if(Constants.writeDebug){
                            LogManager.writeLogDebug("SO Edit  Saving in progress");
                        }
                        presenter.onAsignData(getString(R.string.edit_so), "", "");
                    } else {
                        if(!isClickable) {
                            menuSave.setEnabled(false);
                            isClickable=true;
                            if(Constants.writeDebug){
                                LogManager.writeLogDebug("SO Create Saving in progress");
                            }
                            presenter.onAsignData(getString(R.string.submit_so), "", "");
                        }
                    }
                } else {
                    menuSave.setEnabled(true);
                    ConstantsUtils.showAutoDateSetDialog(SODetailsActivity.this);
                }
                break;
            case R.id.menu_cancel:
                SOUtils.showCancelDialog(SODetailsActivity.this, this);
                break;
            /*case R.id.menu_edit:
                openSOEdit();
                break;*/
            case R.id.menu_print:
                presenter.pdfDownload();
                break;
            case R.id.menu_so_cancel:
                SOUtils.redirectMainActivity(SODetailsActivity.this, comingFrom);
                break;

        }
        return true;
    }

    private void openSOEdit() {
        Intent intent = new Intent(SODetailsActivity.this, SOCreateActivity.class);
        intent.putExtra(Constants.EXTRA_SESSION_REQUIRED, isSessionRequired);
        intent.putExtra(Constants.EXTRA_SO_HEADER, soListBean);
        if (ConstantsUtils.SO_SINGLE_MATERIAL == comingFrom) {
            intent.putExtra(Constants.EXTRA_COME_FROM, ConstantsUtils.SO_EDIT_SINGLE_MATERIAL);
        } else {
            intent.putExtra(Constants.EXTRA_COME_FROM, ConstantsUtils.SO_EDIT_ACTIVITY);
        }
        startActivity(intent);

    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = ConstantsUtils.showProgressDialog(SODetailsActivity.this, message);
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showMessage(String message, final boolean isSimpleDialog) {
        menuSave.setEnabled(true);
        UtilConstants.dialogBoxWithCallBack(SODetailsActivity.this, "", message, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (!isSimpleDialog) {
                    isClickable=false;
                    menuSave.setEnabled(true);
                    redirectActivity();
                }
            }
        });
    }

    private void redirectActivity() {
//        MainMenu.isRefresh = true;
        Intent intent = new Intent(SODetailsActivity.this, CustomerDetailsActivity.class);
//        intent.putExtra(Constants.CPNo, Constants.NavCustNo);
//        intent.putExtra(Constants.CPUID, Constants.NavCPUID);
//        intent.putExtra(Constants.RetailerName, Constants.NavCustName);
//        intent.putExtra(Constants.CPGUID32, Constants.NavCPGUID32);
//        intent.putExtra(Constants.comingFrom, Constants.NavComingFrom);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void redirectApprovalActivity() {
        Intent intent = new Intent(SODetailsActivity.this, SOApproveActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void conformationDialog(String message, int from) {
        if (from == 1) {
            UtilConstants.dialogBoxWithCallBack(SODetailsActivity.this, "", message, getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
                @Override
                public void clickedStatus(boolean b) {
                    if (b) {
                        if (ConstantsUtils.isAutomaticTimeZone(SODetailsActivity.this)) {
                            menuSave.setEnabled(false);
                            isClickable=true;
                            LogManager.writeLogDebug("Sales Order : Save in Progress");
                            presenter.onSaveData();
                        }else {
                            isClickable=false;
                            menuSave.setEnabled(true);
                            ConstantsUtils.showAutoDateSetDialog(SODetailsActivity.this);
                        }

                    }else{
                        isClickable=false;
                        menuSave.setEnabled(true);
                    }
                }
            });
        } else if (from == 2) {
            SOUtils.showCommentsDialog(SODetailsActivity.this, new com.rspl.sf.msfa.interfaces.CustomDialogCallBack() {
                @Override
                public void cancelDialogCallBack(boolean userClicked, String ids, String description) {
                    if (userClicked) {
                        presenter.approveData(ids, description, Constants.ApprovalStatus01);
//                        mStrID = Constants.ApprovalStatus01;
//                        approveOrder(mStrInstanceId, Constants.ApprovalStatus01, header.get(0).getOrderNo(), description + "");
                    }else{
                        menuSave.setEnabled(true);

                    }
                }
            }, getString(R.string.approve_title_comments));
        }
    }

    @Override
    public void displayNotes(ArrayList<SOTextBean> soTextBeanArrayList) {
        displayNotesList(soTextBeanArrayList);
    }

    @Override
    public void showApprovalSuccMsg(String string) {
        menuSave.setEnabled(true);
        UtilConstants.dialogBoxWithCallBack(SODetailsActivity.this, "", string, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                redirectApprovalActivity();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivOrderDetails:
                if (llHeader.getVisibility() == View.VISIBLE) {
                    ivExpandIcon.setImageResource(R.drawable.ic_arrow_down_black_24dp);
                    llHeader.setVisibility(View.GONE);
                    tvDate.setVisibility(View.VISIBLE);
                    ViewGroup.MarginLayoutParams layoutParams = getLayoutParams(cvOrderDetails);
                    int marginB = ConstantsUtils.dpToPx(8, mContext);
                    if (llSOCondition.getVisibility() == View.VISIBLE) {
                        if (getLayoutParams(cvPricingDetails).topMargin != 0) {
                            marginB = 0;
                        }
                    } else {
                        marginB = 0;
                        ViewGroup.MarginLayoutParams layoutParamss = getLayoutParams(cvPricingDetails);
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
                        if (getLayoutParams(cvPricingDetails).topMargin != 0) {
                            marginB = 0;
                        }
                    }
                    ViewGroup.MarginLayoutParams layoutParams = getLayoutParams(cvOrderDetails);
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
                        if (getLayoutParams(cvItem).topMargin != 0) {
                            marginB = 0;
                        }
                    } else {
                        marginB = 0;
                        ViewGroup.MarginLayoutParams layoutParamss = getLayoutParams(cvItem);
                        layoutParamss.setMargins(ConstantsUtils.dpToPx(8, mContext), 0, ConstantsUtils.dpToPx(8, mContext), 0);
                        cvItem.requestLayout();
                    }
                    if (llHeader.getVisibility() == View.VISIBLE) {
                        if (getLayoutParams(cvOrderDetails).bottomMargin != 0) {
                            marginT = 0;
                        }
                    } else {
                        marginT = 0;
                        ViewGroup.MarginLayoutParams layoutParamss = getLayoutParams(cvOrderDetails);
                        layoutParamss.setMargins(ConstantsUtils.dpToPx(8, mContext), ConstantsUtils.dpToPx(8, mContext), ConstantsUtils.dpToPx(8, mContext), 0);
                        cvOrderDetails.requestLayout();
                    }

                    ViewGroup.MarginLayoutParams layoutParams = getLayoutParams(cvPricingDetails);
                    layoutParams.setMargins(ConstantsUtils.dpToPx(8, mContext), marginT, ConstantsUtils.dpToPx(8, mContext), marginB);
                    cvPricingDetails.requestLayout();

                } else {
                    ivPricingDetails.setImageResource(R.drawable.ic_arrow_up_black_24dp);
                    llSOCondition.setVisibility(View.VISIBLE);
                    tvAmount.setVisibility(View.GONE);
                    int marginB = 0;//ConstantsUtils.dpToPx(8,mContext);
                    int marginT = ConstantsUtils.dpToPx(8, mContext);
                    if (llItemList.getVisibility() == View.VISIBLE) {
                        if (getLayoutParams(cvItem).topMargin != 0) {
                            marginB = 0;
                        }
                    }
                    if (llHeader.getVisibility() == View.VISIBLE) {
                        if (getLayoutParams(cvOrderDetails).bottomMargin != 0) {
                            marginT = 0;
                        }
                    }
                    ViewGroup.MarginLayoutParams layoutParams =
                            (ViewGroup.MarginLayoutParams) cvPricingDetails.getLayoutParams();
                    layoutParams.setMargins(ConstantsUtils.dpToPx(8, mContext), marginT, ConstantsUtils.dpToPx(8, mContext), marginB);
                    cvPricingDetails.requestLayout();
                }
                break;
            case R.id.btPostNotes:
                presenter.postNotes(etNotes.getText().toString());
                etNotes.setText("");
                break;
            case R.id.fabEdit:
                openSOEdit();
                break;
        }
    }

    private ViewGroup.MarginLayoutParams getLayoutParams(CardView cardView) {
        return (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
    }

    /*display notes list*/
    private void displayNotesList(ArrayList<SOTextBean> soTextBeanArrayList) {
        int totalSize = soTextBeanArrayList.size();
        if (totalSize > 0) {
            SOTextBean soTextBean = soTextBeanArrayList.get(0);
            notesItemView.setVisibility(View.GONE);
            tvNotesHeader.setText(soTextBean.getTextIDDesc());
            tvNotesText.setText(soTextBean.getText().replace("/n", "\n"));
            ConstantsUtils.makeTextViewResizable(tvNotesText, 3, getString(R.string.view_more), true, true);
        } else {
            notesItemView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(SOItemBean item, View view, int position) {
//        Intent intent = new Intent(this, SODocumentActivity.class);
//        intent.putExtra(Constants.EXTRA_SO_ITEM_HEADER,item);
//        intent.putExtra(Constants.EXTRA_SESSION_REQUIRED,isSessionRequired);
//        startActivity(intent);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, View viewItem) {
        return new SOItemDetailsVH1(viewItem, SODetailsActivity.this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, SOItemBean soItemBean) {
        ((SOItemDetailsVH1) holder).tvMaterialDesc.setText(soItemBean.getMatNoAndDesc());
        ((SOItemDetailsVH1) holder).tvQty.setText(soItemBean.getSoQty() + " " + soItemBean.getUom());
        ((SOItemDetailsVH1) holder).tvAmount.setText(UtilConstants.commaSeparator(UtilConstants.removeLeadingZero(soItemBean.getNetAmount())) + " " + soItemBean.getCurrency());
        Drawable delvStatusImg;
        if (soItemBean.getDelvStatusID().equals("")) {
            delvStatusImg = SOUtils.displayDelvStatusIcon("A", mContext);
            ((SOItemDetailsVH1) holder).ivDelvStatus.setImageDrawable(delvStatusImg);
        } else {
            delvStatusImg = SOUtils.displayDelvStatusIcon(soItemBean.getDelvStatusID(), mContext);
            if (delvStatusImg != null) {
                ((SOItemDetailsVH1) holder).ivDelvStatus.setImageDrawable(delvStatusImg);
            }
        }
        if(!soItemBean.getHighLevellItemNo().equalsIgnoreCase("000000")){
            Drawable img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
            if (img != null)
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.dimgray), PorterDuff.Mode.SRC_IN);

            ((SOItemDetailsVH1) holder).ivDelvStatus.setImageDrawable(img);
        }



        if(soItemBean.isRemoved())
            ((SOItemDetailsVH1) holder).iv_deleted.setVisibility(View.VISIBLE);
        else
            ((SOItemDetailsVH1) holder).iv_deleted.setVisibility(View.GONE);


        ((SOItemDetailsVH1) holder).ivAdditems.setVisibility(View.GONE);
        if(soItemBean.getHighLevellItemNo().equalsIgnoreCase("000000")){
            ((SOItemDetailsVH1) holder).ivAdditems.setVisibility(View.VISIBLE);
            Drawable img = ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_add_24).mutate();
            if (img != null)
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.secondaryColor), PorterDuff.Mode.SRC_IN);

            ((SOItemDetailsVH1) holder).ivAdditems.setImageDrawable(img);
        }

        ((SOItemDetailsVH1) holder).ivAdditems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addItemAlert(soItemBean,position);
            }
        });

    }

    public void addItemAlert(SOItemBean soItemBean,int pos)
    {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.qnty_alert_dilog_custom, null);
        dialogBuilder.setCancelable(false);
        final EditText et_qnty = (EditText) dialogView.findViewById(R.id.etd_qnty);
        Button buttonsubmit = (Button) dialogView.findViewById(R.id.btn_submit);
        Button buttoncancel = (Button) dialogView.findViewById(R.id.btn_cancel);

        buttonsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String qty= et_qnty.getText().toString();

                if(et_qnty.getText().toString().isEmpty()){
                    et_qnty.setError("Please Enter Qnty");
                    ConstantsUtils.displayLongToast(SODetailsActivity.this,"Please Enter Qnty!");
                }else {
                    dialogBuilder.dismiss();
                    ArrayList<SOItemBean> soitemlistnew=new ArrayList<>();
                    soitemlistnew.clear();
                //add all main Items to arrayList
                    for(int i=0;i<soitemlist.size();i++)
                    { SOItemBean bean=soitemlist.get(i);
                        if (bean.getHighLevellItemNo().equalsIgnoreCase("000000")) {
                            soitemlistnew.add(bean);
                        }
                    }
                 //add  updated qty
                    SOItemBean soitemBeannew= new SOItemBean();
                    ArrayList<SOItemBean> upDatedlist=Constants.exchangeBean(soItemBean,soitemBeannew);
                    for(int i=0;i<upDatedlist.size();i++)
                        { SOItemBean soItemBean1=upDatedlist.get(i);
                            soItemBean1.setSoQty(""+qty);
                            soItemBean1.setQuantity(""+qty);
                            soitemlistnew.add(soItemBean1);
                        }


                   soListBean.setSoItemBeanArrayList(soitemlistnew);
                    presenter.startSOSimulate(soListBean);


                }


            }
        });
        buttoncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DO SOMETHINGS
                dialogBuilder.dismiss();
            }
        });


        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

    }
    @Override
    public void cancelDialogCallBack(boolean userClicked, String ids, String description) {
        Log.d(SODetailsActivity.class.getName(), "cancelDialogCallBack: " + ids + " description :" + description);
        if (userClicked) {
            presenter.onAsignData(getString(R.string.cancel_so), ids, description);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        if (requestCode == LocationUtils.REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                presenter.onSaveData();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case UtilConstants.Location_PERMISSION_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationUtils.checkLocationPermission(SODetailsActivity.this, new LocationInterface() {
                        @Override
                        public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                            if (status) {
                                presenter.onSaveData();
                            }
                        }
                    });

                } else {
                }
                return;
            }


        }
    }
    @Override
    public void displayMessage(String message) {
        ConstantsUtils.displayLongToast(mContext, message);
    }

    @Override
    public void openReviewScreen(List<CreditLimitBean> limitBeanList, SOListBean soListBeanHeader) {
        soListBeanHeader.setCreditControlAreas(limitBeanList);

        soListBean=soListBeanHeader;

        displayHeaderList(soListBeanHeader);

       /* mContext = SODetailsActivity.this;
        presenter = new SODetailsPresenterImpl(SODetailsActivity.this,customerNo, this, new SODetailsModelImpl(), comingFrom, soListBeanHeader, isSessionRequired);
        presenter.onStart();
        initUI();


        displayConditionItemDetails(soListBeanHeader.getSoItemBeanArrayList().get(0));
        creditLimitBean.clear();
        creditLimitBean = soListBeanHeader.getCreditControlAreas();
        displayCreditAmounts();*/



    }


}
