package com.rspl.sf.msfa.reports;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.google.gson.Gson;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.MyUtils;
import com.rspl.sf.msfa.interfaces.DialogCallBack;
import com.rspl.sf.msfa.mbo.SalesOrderBean;
import com.rspl.sf.msfa.reports.salesorder.SODetailsViewHolder;
import com.rspl.sf.msfa.so.SOChangeActivity;
import com.rspl.sf.msfa.so.SOUtils;
import com.rspl.sf.msfa.soapproval.SalesOrderConditionsBean;
import com.rspl.sf.msfa.soapproval.history.SOApproveHistoryActivity;
import com.rspl.sf.msfa.socreate.SOConditionItemDetaiBean;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.solist.SOListBean;
import com.rspl.sf.msfa.store.OfflineManager;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SalesOrderListDetailsActivity extends AppCompatActivity implements View.OnClickListener, AdapterInterface<SalesOrderBean> {
    public static boolean isCancelledOrChanged = false;
    TextView[] matDesc, matCode, netAmount, invQty, itemNo;
    TextView[] matDesc_ex, matCode_ex, netAmount_ex, taxAmount_ex, totalAmount_ex, invQty_ex, itemNo_ex;
    ImageView iv_expand_icon, iv_header_expand_icon;
    MenuItem menuCancel = null, menuEdit = null;
    boolean isSOChanged = false;
    boolean headerDetails = false;
    Double TotalAmount = 0.0;
    SalesOrderBean salesOrderBean;
    SOListBean soListBean;
    TextView tvSONo, tvOrderType, tvPricingDetail, tvAmount, tvShipToPartyName, tvSalesAreaDesc, tvSalesGroupDesc, tvSalesOfficeDesc, tvPlantDesc,
            tvCustomerPODesc, tvCustomerPODateDesc, tvNoRecordFound, tvShippingTypeDesc, tvMeansOfTranstDesc, tvIncoterm1Desc, tvIncoterm2Desc, tvPaytermDesc,
            tvUnloadingPointDesc, tvAddress, tvReceivingPointDesc, tvRefDocDesc, tvSalesDistDesc, tvBillToDesc, tvBillTo, tvOrderDateDesc, tvReferenceHeader, tvDate;
    ImageView ivDeliveryStatus, ivPricingDetails, ivExpandIcon, ivItemDetails;
    LinearLayout llHeader, llSalesArea, llPlant, llRefDoc, llCustomerPo, llCustomerPoDate, llShipToAdd, llShippingType, llIncoTerm1, llSalesGroup, llSalesOffice,
            llIncoTerm2, llUnloadingPoint, llReceivingPoint, llItemList, llMeansOfTranst, llPaymentTerm, llBillTo, llOrderDate, llSOCondition;
    CardView cvOrderDetails, cvPricingDetails;
    NestedScrollView nestedScroll;
    RecyclerView recyclerView;
    SimpleRecyclerViewAdapter<SalesOrderBean> recyclerViewAdapter;
    ArrayList<SalesOrderConditionsBean> salesOrderConditionsBeanArrayList = new ArrayList<>();
    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";
    private String mStrBundleSOGUID = "";
    private List<SalesOrderBean> SalesOrderBeanList = new ArrayList<>();
    private LinearLayout llDetailLayout;
    private boolean flag = true;
    private int cursorLength = 0;
    private String actionBarTitle = "";
    private int tabPosition = 0;
    private String mDeviceNo = "";
    private TextView tvHistoryStatus;
    private TextView tvInvDate;
    private TextView tvInvNo;
    private TextView tvOutStandValue;
    private String mStrBundleDate = "";
    private String mStrBundleOrderNo = "";
    private String mStrBundleAmount = "";
    private String mStrBundleStatus = "";
    private String mStrBundleOrderType = "";
    private String mStrBundleOrderTypeDesc = "";
    private String mStrBundleSalesArea = "";
    private String mStrBundleSalesAreaDesc = "";
    private String mStrBundleSoldTo = "";
    private String mStrBundleSoldToDesc = "";
    private String mStrBundleCustomerNumber = "";
    private String mStrBundleShippingPoint = "";
    private String mStrBundleShippingPointDesc = "";
    private String mStrBundleShipTo = "";
    private String mStrBundleShipToDesc = "";
    private String mStrBundleForwardingAgent = "";
    private String mStrBundleForwardingAgentDesc = "";
    private String mStrBundlePlant = "";
    private String mStrBundlePlantDesc = "";
    private String mStrBundleIncoTerm1 = "";
    private String mStrBundleIncoTerm1Desc = "";
    private String mStrBundleIncoTerm2 = "";
    private String mStrBundleSalesDistrict = "";
    private String mStrBundleSalesDistrictDesc = "";
    private String mStrBundleRoute = "";
    private String mStrBundleRouteDesc = "";
    private String mStrBundleSplProcessing = "";
    private String mStrBundleSplProcessingDesc = "";
    private String mStrBundleMeanOfTrans = "";
    private String mStrBundleMeanOfTransDesc = "";
    private String mStrBundleStorageLoc = "";
    private String mStrBundleStorageLocDesc = "";
    private String mStrBundleCurrency = "";
    private String mStrBundlePayment = "";
    private String mStrBundlePaymentDesc = "";
    private String mStrBundleCustomerPoNo = "";
    private String mStrBundleCustomerPoDate = "";
    private String mStrBundleRemarks = "";
    private String mStrDeliveryStatus = "";
    private String mStrPopUpText = "";
    private String mStrRejReason = "";
    private String mStrRejReasonDesc = "";
    private String mStrBundleSalesGrp = "";
    private String mStrBundleSalesGrpDesc = "";
    private String mStrBundleSalesOffDesc = "";
    private String mStrBundleSalesOffice = "";
    private LinearLayout llHeaderItemList;
    private String Quantity;
    private CardView cvItem;
    private Toolbar toolbar;
    private Drawable delvStatusImg = null;
    private MenuItem menuHistory = null;
    private TextView tvTotlWeightAmt;
    private String mStrBundleNetWeight = "";
    private String mStrBundleNetWeightUom = "";
    private String mStrBundleTotalQuantity = "";
    private String mStrBundleQuantityUom = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_so_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            soListBean = (SOListBean) bundleExtras.getSerializable(Constants.EXTRA_SO_DETAIL);
            if (soListBean == null) {
                mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
                mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
                mStrBundleCustomerNumber = bundleExtras.getString(Constants.CPNo);
                mStrBundleSOGUID = bundleExtras.getString(Constants.EXTRA_SSRO_GUID);
                mStrBundleDate = bundleExtras.getString(Constants.EXTRA_ORDER_DATE);
                mStrBundleOrderNo = bundleExtras.getString(Constants.EXTRA_ORDER_IDS);
                mStrBundleAmount = bundleExtras.getString(Constants.EXTRA_ORDER_AMOUNT);
                mStrBundleStatus = bundleExtras.getString(Constants.EXTRA_ORDER_SATUS, "");
                mStrBundleCurrency = bundleExtras.getString(Constants.EXTRA_ORDER_CURRENCY);
                mDeviceNo = bundleExtras.getString(Constants.DeviceNo, "");
                Quantity = bundleExtras.getString(Constants.QAQty);
                tabPosition = bundleExtras.getInt(Constants.EXTRA_TAB_POS, 0);
                isSOChanged = bundleExtras.getBoolean(Constants.comingFromChange, false);
                mStrBundleOrderType = bundleExtras.getString(Constants.ORDER_TYPE);
                mStrBundleOrderTypeDesc = bundleExtras.getString(Constants.ORDER_TYPE_DESC);
                mStrBundleSalesArea = bundleExtras.getString(Constants.SALESAREA);
                mStrBundleSalesAreaDesc = bundleExtras.getString(Constants.SALESAREA_DESC);
                mStrBundleSoldTo = bundleExtras.getString(Constants.SOLDTO);
                mStrBundleSoldToDesc = bundleExtras.getString(Constants.SOLDTONAME);
                mStrBundleShippingPoint = bundleExtras.getString(Constants.SHIPPINTPOINT);
                mStrBundleShippingPointDesc = bundleExtras.getString(Constants.SHIPPINTPOINTDESC);
                mStrBundleShipTo = bundleExtras.getString(Constants.SHIPTO);
                mStrBundleShipToDesc = bundleExtras.getString(Constants.SHIPTONAME);
                mStrBundleForwardingAgent = bundleExtras.getString(Constants.FORWARDINGAGENT);
                mStrBundleForwardingAgentDesc = bundleExtras.getString(Constants.FORWARDINGAGENTNAME);
                mStrBundlePlant = bundleExtras.getString(Constants.PLANT);
                mStrBundlePlantDesc = bundleExtras.getString(Constants.PLANTDESC);
                mStrBundleIncoTerm1 = bundleExtras.getString(Constants.INCOTERM1);
                mStrBundleIncoTerm1Desc = bundleExtras.getString(Constants.INCOTERM1DESC);
                mStrBundleIncoTerm2 = bundleExtras.getString(Constants.INCOTERM2);
                mStrBundleSalesDistrict = bundleExtras.getString(Constants.SALESDISTRICT);
                mStrBundleSalesDistrictDesc = bundleExtras.getString(Constants.SALESDISTRICTDESC);
                mStrBundleRoute = bundleExtras.getString(Constants.ROUTE);
                mStrBundleRouteDesc = bundleExtras.getString(Constants.ROUTEDESC);
                mStrBundleSplProcessing = bundleExtras.getString(Constants.SplProcessing);
                mStrBundleSplProcessingDesc = bundleExtras.getString(Constants.SplProcessingDesc);
                mStrBundleMeanOfTrans = bundleExtras.getString(Constants.MEANSOFTRANSPORT);
                mStrBundleMeanOfTransDesc = bundleExtras.getString(Constants.MEANSOFTRANSPORTDESC);
                mStrBundleStorageLoc = bundleExtras.getString(Constants.STORAGELOC);
                mStrBundleStorageLocDesc = bundleExtras.getString(Constants.STORAGELOC);
                mStrBundlePayment = bundleExtras.getString(Constants.Payterm);
                mStrBundlePaymentDesc = bundleExtras.getString(Constants.PaytermDesc);

                mStrBundleSalesGrp = bundleExtras.getString(Constants.SalesGroup);
                mStrBundleSalesGrpDesc = bundleExtras.getString(Constants.SalesGrpDesc);
                mStrBundleSalesOffDesc = bundleExtras.getString(Constants.SalesOffDesc);
                mStrBundleSalesOffice = bundleExtras.getString(Constants.SalesOffice);
                mStrBundleNetWeight = bundleExtras.getString(Constants.NetWeight);
                mStrBundleNetWeightUom = bundleExtras.getString(Constants.NetWeightUom);
                mStrBundleTotalQuantity = bundleExtras.getString(Constants.TotalQuantity);
                mStrBundleQuantityUom = bundleExtras.getString(Constants.QuantityUom);

                mStrBundleCustomerPoNo = bundleExtras.getString(Constants.CUSTOMERPO);
                mStrBundleCustomerPoDate = bundleExtras.getString(Constants.CUSTOMERPODATE);
                mStrBundleRemarks = bundleExtras.getString(Constants.Remarks);
                mStrDeliveryStatus = bundleExtras.getString(Constants.DELIVERY_STATUS);
                salesOrderBean = (SalesOrderBean) bundleExtras.get("SALES_ORDER_BEAN_OBJECT");
                Constants.SOBundleExtras = bundleExtras;
            }
        }
        //declare UI

        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_Sales_order_details), 0);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        recyclerView.setNestedScrollingEnabled(false);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new SimpleRecyclerViewAdapter<SalesOrderBean>(this, R.layout.so_item_material, this, recyclerView, tvNoRecordFound);
        recyclerView.setAdapter(recyclerViewAdapter);
//        ActionBarView.initActionBarView(this, true, getString(R.string.title_Sales_order_details));
        //get data from offline db
//        if (tabPosition == Constants.TAB_POS_1 && !isSOChanged) {
        setUI();
        if (soListBean == null) {
            delvStatusImg = getImageView();
            if (mDeviceNo.equalsIgnoreCase("")) {
                getSSSODataFromOfflineDb();
                getSalesOrderConditions();
            } else {
                getSODataFromDataValt();
            }
            displayConditionItemDetails();
        }
    }

    private Drawable getImageView() {
        Drawable delvStatusImg = null;
        try {
            delvStatusImg = SOUtils.displayStatusImage(salesOrderBean.getStatusID(), salesOrderBean.getDelvStatus(), SalesOrderListDetailsActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return delvStatusImg;
    }

    /*get ssso data from data valt*/
    private void getSODataFromDataValt() {
        try {
            SalesOrderBeanList.clear();
            SalesOrderBeanList.addAll(OfflineManager.getSODetailsListFromDataValt(mDeviceNo, SalesOrderListDetailsActivity.this));
            if (!SalesOrderBeanList.isEmpty()) {
                ArrayList<SalesOrderConditionsBean> soConditionBean = SalesOrderBeanList.get(0).getSalesOrderConditionsBeanArrayList();
                salesOrderConditionsBeanArrayList.clear();
                for (SalesOrderConditionsBean salesOrderConditionsBean : soConditionBean) {
                    SalesOrderConditionsBean soCondBean = new SalesOrderConditionsBean();
                    soCondBean.setName(salesOrderConditionsBean.getName());
                    soCondBean.setconditionAmount(salesOrderConditionsBean.getconditionAmount());
                    soCondBean.setconditionValue(salesOrderConditionsBean.getconditionValue());
                    soCondBean.setCondCurrency(salesOrderConditionsBean.getCondCurrency());
                    salesOrderConditionsBeanArrayList.add(soCondBean);
                }
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        /*display value*/
        getDisplayedValues(SalesOrderBeanList);
    }

    /*get ssso data from offlene database*/
    private void getSSSODataFromOfflineDb() {
        String qry = Constants.SOItemDetails + "?$filter=" + Constants.SONo + " eq '" + mStrBundleOrderNo + "'";
        try {
            SalesOrderBeanList.clear();
            SalesOrderBeanList.addAll(OfflineManager.getSecondarySalesOrderDetailsList(qry));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        /*display value*/
        getDisplayedValues(SalesOrderBeanList);
    }

    private ArrayList<SalesOrderConditionsBean> getSalesOrderConditions() {
        String query = Constants.SOs + "('" + mStrBundleOrderNo + "')/SOConditions";

        try {
            salesOrderConditionsBeanArrayList = OfflineManager.getSOConditionsList(query);
            SalesOrderConditionsBean salesOrderConditionsBean = new SalesOrderConditionsBean();
            double subAmount = 0.0, totalAmount = 0.0;
            for (int i = 0; i < salesOrderConditionsBeanArrayList.size(); i++) {
                subAmount = Double.parseDouble(salesOrderConditionsBeanArrayList.get(i).getconditionValue());
                totalAmount = totalAmount + subAmount;
            }
            salesOrderConditionsBean.setViewType("T");
            salesOrderConditionsBean.setName(getString(R.string.total_amount));
            salesOrderConditionsBean.setTotalAmount(String.valueOf(subAmount));
            salesOrderConditionsBean.setconditionValue(String.valueOf(totalAmount));
            salesOrderConditionsBeanArrayList.add(salesOrderConditionsBean);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return salesOrderConditionsBeanArrayList;
    }

    /**
     * declare UI
     */
    private void setUI() {
        tvSONo = (TextView) findViewById(R.id.tvSONo);
        tvOrderType = (TextView) findViewById(R.id.tvOrderType);
        tvPricingDetail = (TextView) findViewById(R.id.tvPricingDetail);
        tvAmount = (TextView) findViewById(R.id.tvAmount);
        llHeader = (LinearLayout) findViewById(R.id.headerItem);
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
        llSalesOffice = (LinearLayout) findViewById(R.id.llSalesOffice);
        llPlant = (LinearLayout) findViewById(R.id.llPlant);
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
        cvOrderDetails = (CardView) findViewById(R.id.cvOrderDetails);
        llSOCondition = (LinearLayout) findViewById(R.id.llSOCondition);
        ivPricingDetails = (ImageView) findViewById(R.id.ivPricingDetails);
        cvPricingDetails = (CardView) findViewById(R.id.cvPricingDetails);
        ivExpandIcon = (ImageView) findViewById(R.id.ivOrderDetails);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        llHeader = (LinearLayout) findViewById(R.id.headerItem);
        llItemList = (LinearLayout) findViewById(R.id.llItemList);
        tvNoRecordFound = (TextView) findViewById(R.id.no_record_found);
        ivDeliveryStatus = (ImageView) findViewById(R.id.ivDeliveryStatus);
        nestedScroll = (NestedScrollView) findViewById(R.id.nestedScroll);
        ivItemDetails = (ImageView) findViewById(R.id.ivItemDetails);
        cvItem = (CardView) findViewById(R.id.cvItem);
        tvTotlWeightAmt = (TextView) findViewById(R.id.tvTotlWeightAmt);
        ivItemDetails.setOnClickListener(this);
        ivExpandIcon.setOnClickListener(this);
        ivPricingDetails.setOnClickListener(this);

        if (soListBean == null) {
            setData();
            if (delvStatusImg != null) {
                ivDeliveryStatus.setImageDrawable(delvStatusImg);
            } else {
                Drawable drawable = SOUtils.getSODefaultDrawable(SalesOrderListDetailsActivity.this);
                if (drawable != null)
                    ivDeliveryStatus.setImageDrawable(drawable);
            }
        } else {
            setSOListData();
        }

    }

    private void setSOListData() {
        tvSONo.setText(soListBean.getSONo());
        tvDate.setText(soListBean.getOrderDate());
        tvOrderType.setText(getString(R.string.po_details_display_value, soListBean.getOrderTypeDesc(), soListBean.getOrderType()));
        String totalQty = "";
        if (!TextUtils.isEmpty(soListBean.getmSteTotalQtyUOM())) {
            totalQty = soListBean.getQuantity() + " " + soListBean.getmSteTotalQtyUOM();
        } else {
            totalQty = soListBean.getQuantity();
        }
        tvTotlWeightAmt.setText(soListBean.getmStrTotalWeight() + " " + soListBean.getmStrWeightUOM() + " / " + totalQty);

        tvAmount.setText(ConstantsUtils.commaSeparator(soListBean.getTotalAmt(), soListBean.getCurrency()) + " " + soListBean.getCurrency());

       /* SOItemBean soItemBean = soListBean.getSoItemBeanArrayList().get(0);
        int conditionTotalSize = soItemBean.getConditionItemDetaiBeanArrayList().size();
        for (int j = 0; j < conditionTotalSize; j++) {
           // SalesOrderConditionsBean salesOrderConditionsBean = salesOrderConditionsBeanArrayList.get(j);
            SOConditionItemDetaiBean soScheduleBean = soItemBean.getConditionItemDetaiBeanArrayList().get(j);
            if(soScheduleBean.getName().equalsIgnoreCase("Total"))
            {
                tvAmount.setText(""+ConstantsUtils.commaSeparator(soScheduleBean.getConditionValue(), soScheduleBean.getCondCurrency()));
                break;
            }
        }*/



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
            llSalesGroup.setVisibility(View.GONE);
        } else {
            llSalesGroup.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(soListBean.getSaleOffDesc())) {
            tvSalesOfficeDesc.setText(getString(R.string.po_details_display_value, soListBean.getSaleOffDesc(), soListBean.getSalesOfficeId()));
            llSalesOffice.setVisibility(View.GONE);
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
            llMeansOfTranst.setVisibility(View.VISIBLE);
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
            llOrderDate.setVisibility(View.GONE);
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
            llRefDoc.setVisibility(View.GONE);
        } else {
            llRefDoc.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(soListBean.getRefDocCat())) {
            tvRefDocDesc.setText(getString(R.string.po_details_display_value, soListBean.getRefDocCat(), soListBean.getRefDocNo()));
            llRefDoc.setVisibility(View.GONE);
        } else {
            llRefDoc.setVisibility(View.GONE);
        }
        llBillTo.setVisibility(View.GONE);


        delvStatusImg = SOUtils.displayStatusImage(soListBean.getStatus(), soListBean.getDelvStatus(), SalesOrderListDetailsActivity.this);
        ivDeliveryStatus.setImageDrawable(delvStatusImg);
        displayConditionItemDetails();
        SimpleRecyclerViewAdapter<SOItemBean> recyclerViewAdapter = new SimpleRecyclerViewAdapter<>(this, R.layout.so_item_material, new AdapterInterface<SOItemBean>() {
            @Override
            public void onItemClick(SOItemBean soItemBean, View view, int i) {

            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
                return new SODetailsViewHolder(view, SalesOrderListDetailsActivity.this);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, SOItemBean soItemBean) {
                ((SODetailsViewHolder) viewHolder).tvMaterialDesc.setText(soItemBean.getMatNoAndDesc());
                try {
                    ((SODetailsViewHolder) viewHolder).tvQty.setText(ConstantsUtils.checkNoUOMZero(soItemBean.getUom(), soItemBean.getSoQty()) + " " + soItemBean.getUom());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                ((SODetailsViewHolder) viewHolder).tvAmount.setText(ConstantsUtils.commaSeparator(UtilConstants.removeLeadingZero(soItemBean.getNetAmount()), soItemBean.getCurrency()) + " " + soItemBean.getCurrency());
                /*if (delvStatusImg != null) {
                    ((SODetailsViewHolder) viewHolder).ivDelvStatus.setImageDrawable(delvStatusImg);
                }*/
                if (!TextUtils.isEmpty(soItemBean.getStatusID()) && !TextUtils.isEmpty(soItemBean.getDelvStatusID())) {
                    delvStatusImg = SOUtils.displayStatusImage(soItemBean.getStatusID(), soItemBean.getDelvStatusID(), SalesOrderListDetailsActivity.this);
                    ((SODetailsViewHolder) viewHolder).ivDelvStatus.setImageDrawable(delvStatusImg);
                } else if (delvStatusImg != null) {
                    ((SODetailsViewHolder) viewHolder).ivDelvStatus.setImageDrawable(delvStatusImg);
                }else if (TextUtils.isEmpty(soItemBean.getStatusID())) {
                    Drawable drawable = SOUtils.getSODefaultDrawable(SalesOrderListDetailsActivity.this);
                    if (drawable != null)
                        ((SODetailsViewHolder) viewHolder).ivDelvStatus.setImageDrawable(drawable);
                }
                if(!soItemBean.getHighLevellItemNo().equalsIgnoreCase("000000")){
                    Drawable img = ContextCompat.getDrawable(SalesOrderListDetailsActivity.this, R.drawable.ic_shopping_cart_black_24dp).mutate();
                    if (img != null)
                        img.setColorFilter(ContextCompat.getColor(SalesOrderListDetailsActivity.this, R.color.dimgray), PorterDuff.Mode.SRC_IN);
                    /*if(!TextUtils.isEmpty(soItemBean.getRejectionId()) && soItemBean.getRejectionId().equalsIgnoreCase("99")) {*/
                        /*if (!TextUtils.isEmpty(soItemBean.getStatusID()) && soItemBean.getStatusID().equalsIgnoreCase("D")) {
                            try {
                                delvStatusImg = SOUtils.displayStatusImage(soItemBean.getStatusID(), soItemBean.getDelvStatusID(), SalesOrderListDetailsActivity.this);
                                ((SODetailsViewHolder) viewHolder).ivDelvStatus.setImageDrawable(delvStatusImg);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else {
                            try {
                                ((SODetailsViewHolder) viewHolder).ivDelvStatus.setImageDrawable(img);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }*/
                    try {
                        delvStatusImg = SOUtils.displayStatusImage(soItemBean.getStatusID(), soItemBean.getDelvStatusID(), SalesOrderListDetailsActivity.this);
                        delvStatusImg.setColorFilter(ContextCompat.getColor(SalesOrderListDetailsActivity.this, R.color.dimgray), PorterDuff.Mode.SRC_IN);
                        ((SODetailsViewHolder) viewHolder).ivDelvStatus.setImageDrawable(delvStatusImg);
                    } catch (Throwable e) {
                        try {
                            ((SODetailsViewHolder) viewHolder).ivDelvStatus.setImageDrawable(img);
                        } catch (Throwable e1) {
                            e1.printStackTrace();
                        }
                        e.printStackTrace();
                    }

                    /*}else {
                        ((SODetailsViewHolder) viewHolder).ivDelvStatus.setImageDrawable(img);
                    }*/
                }

                if(soItemBean.isRemoved())
                    ((SODetailsViewHolder) viewHolder).iv_deleted.setVisibility(View.VISIBLE);
                else
                    ((SODetailsViewHolder) viewHolder).iv_deleted.setVisibility(View.GONE);



            }
        }, recyclerView, tvNoRecordFound);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.refreshAdapter(soListBean.getSoItemBeanArrayList());
    }

    private void setData() {
        tvSONo.setText(String.valueOf(mStrBundleOrderNo));
        tvDate.setText(String.valueOf(mStrBundleDate));
        tvOrderType.setText(getString(R.string.po_details_display_value, mStrBundleOrderTypeDesc, mStrBundleOrderType));
        tvAmount.setText(ConstantsUtils.commaSeparator(mStrBundleAmount, mStrBundleCurrency) + " " + mStrBundleCurrency);
        tvSalesAreaDesc.setText(getString(R.string.po_details_display_value, mStrBundleSalesAreaDesc, mStrBundleSalesArea));
        tvOrderDateDesc.setText(mStrBundleDate);
        String totalQty = "";
        if (!TextUtils.isEmpty(mStrBundleQuantityUom)) {
            totalQty = mStrBundleTotalQuantity + " " + mStrBundleQuantityUom;
        } else {
            totalQty = mStrBundleTotalQuantity;
        }
        try {
            tvTotlWeightAmt.setText(ConstantsUtils.checkNoUOMZero(mStrBundleNetWeightUom, mStrBundleNetWeight) + " " + mStrBundleNetWeightUom + " / " + totalQty);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        try {
            tvPlantDesc.setText(Constants.formatValue(SalesOrderBeanList.get(0).getPlantDesc(), SalesOrderBeanList.get(0).getPlant()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvAddress.setText(mStrBundleSalesDistrictDesc);

        if (!TextUtils.isEmpty(mStrBundleShipToDesc)) {
            tvShipToPartyName.setText(getString(R.string.po_details_display_value, mStrBundleShipToDesc, mStrBundleShipTo));
            llShipToAdd.setVisibility(View.VISIBLE);
        } else {
            llShipToAdd.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mStrBundleSalesAreaDesc)) {
            tvSalesAreaDesc.setText(getString(R.string.po_details_display_value, mStrBundleSalesAreaDesc, mStrBundleSalesArea));
            llSalesArea.setVisibility(View.VISIBLE);
        } else {
            llSalesArea.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mStrBundlePlantDesc)) {
            tvPlantDesc.setText(getString(R.string.po_details_display_value, mStrBundlePlantDesc, mStrBundlePlant));
            llPlant.setVisibility(View.GONE);
        } else {
            llPlant.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mStrBundleSalesGrpDesc)) {
            tvSalesGroupDesc.setText(getString(R.string.po_details_display_value, mStrBundleSalesGrpDesc, mStrBundleSalesGrp));
            llSalesGroup.setVisibility(View.GONE);
        } else {
            llSalesGroup.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mStrBundleSalesOffDesc)) {
            tvSalesOfficeDesc.setText(getString(R.string.po_details_display_value, mStrBundleSalesOffDesc, mStrBundleSalesOffice));
            llSalesOffice.setVisibility(View.GONE);
        } else {
            llSalesOffice.setVisibility(View.GONE);
        }
       /* if (!mStrBundleCustomerPoNo.equalsIgnoreCase("") && mStrBundleCustomerPoDate.equalsIgnoreCase("")) {
            tvCustomerPODesc.setText(mStrBundleCustomerPoNo);
            tvCustomerPODateDesc.setText(mStrBundleCustomerPoDate);
        } else {
            llCustomerPo.setVisibility(View.GONE);
            llCustomerPoDate.setVisibility(View.GONE);
            tvReferenceHeader.setVisibility(View.GONE);
        }*/

        if (!TextUtils.isEmpty(mStrBundleCustomerPoNo)) {
            tvCustomerPODesc.setText(mStrBundleCustomerPoNo);
            llCustomerPo.setVisibility(View.VISIBLE);
        } else {
            llCustomerPo.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mStrBundleCustomerPoDate)) {
            tvCustomerPODateDesc.setText(mStrBundleCustomerPoDate);
            llCustomerPoDate.setVisibility(View.VISIBLE);
        } else {
            llCustomerPoDate.setVisibility(View.GONE);
        }


        if (llCustomerPoDate.getVisibility() == View.GONE && llCustomerPo.getVisibility() == View.GONE) {
            tvReferenceHeader.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mStrBundleShipToDesc)) {
            tvShippingTypeDesc.setText(getString(R.string.po_details_display_value, mStrBundleShippingPointDesc, mStrBundleShipToDesc));
            llShippingType.setVisibility(View.GONE);
        } else {
            llShippingType.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mStrBundleMeanOfTransDesc)) {
            tvMeansOfTranstDesc.setText(getString(R.string.po_details_display_value, mStrBundleMeanOfTransDesc, mStrBundleMeanOfTrans));
            llMeansOfTranst.setVisibility(View.VISIBLE);
        } else {
            llMeansOfTranst.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mStrBundleIncoTerm1Desc)) {
            tvIncoterm1Desc.setText(getString(R.string.po_details_display_value, mStrBundleIncoTerm1Desc, mStrBundleIncoTerm1));
            llIncoTerm1.setVisibility(View.GONE);
        } else {
            llIncoTerm1.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mStrBundleIncoTerm2)) {
            tvIncoterm2Desc.setText(mStrBundleIncoTerm2);
            llIncoTerm2.setVisibility(View.GONE);
        } else {
            llIncoTerm2.setVisibility(View.GONE);
        }
        llUnloadingPoint.setVisibility(View.GONE);
        llReceivingPoint.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(mStrBundleDate)) {
            tvOrderDateDesc.setText(mStrBundleDate);
            llOrderDate.setVisibility(View.GONE);
        } else {
            llOrderDate.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mStrBundlePaymentDesc)) {
            tvPaytermDesc.setText(getString(R.string.po_details_display_value, mStrBundlePaymentDesc, mStrBundlePayment));
            llPaymentTerm.setVisibility(View.VISIBLE);
        } else {
            llPaymentTerm.setVisibility(View.GONE);
        }
        llRefDoc.setVisibility(View.GONE);
        llRefDoc.setVisibility(View.GONE);
        llBillTo.setVisibility(View.GONE);

    }

    /**
     * get data and set it into linearlayout
     *
     * @param arrayList
     */
    private void getDisplayedValues(final List<SalesOrderBean> arrayList) {
        recyclerViewAdapter.refreshAdapter((ArrayList<SalesOrderBean>) arrayList);
    }

    private void displayConditionItemDetails() {

        try {
            if (salesOrderConditionsBeanArrayList.size() > 0) {
                try {
                    llSOCondition.removeAllViews();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                TextView[] tvDescription = new TextView[salesOrderConditionsBeanArrayList.size()];
                TextView[] tvTotalAmount = new TextView[salesOrderConditionsBeanArrayList.size()];
                TableLayout tableScheduleHeading = (TableLayout) LayoutInflater.from(this).inflate(R.layout.table_view, null);
                for (int j = 0; j < salesOrderConditionsBeanArrayList.size(); j++) {
                    SalesOrderConditionsBean salesOrderConditionsBean = salesOrderConditionsBeanArrayList.get(j);
                    if (!salesOrderConditionsBean.getViewType().equalsIgnoreCase("T")) {
                        LinearLayout rowScheduleItem = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.so_condition_item, null);
                        tvDescription[j] = (TextView) rowScheduleItem.findViewById(R.id.tvDescription);
                        tvTotalAmount[j] = (TextView) rowScheduleItem.findViewById(R.id.tvTotalAmount);
                        String strAmount = "";
                        if (!TextUtils.isEmpty(salesOrderConditionsBean.getCondCurrency())) {
                            strAmount = getString(R.string.po_details_display_value, salesOrderConditionsBean.getName(), ConstantsUtils.removeDecimalValueIfDecimalIsZero(salesOrderConditionsBean.getconditionAmount()) + salesOrderConditionsBean.getCondCurrency());
                        } else {
                            strAmount = salesOrderConditionsBean.getName();
                        }
                        tvDescription[j].setText(strAmount);
                        tvTotalAmount[j].setText(ConstantsUtils.commaSeparator(salesOrderConditionsBean.getconditionValue(), salesOrderConditionsBean.getCondCurrency()));
                        tableScheduleHeading.addView(rowScheduleItem);
                    } else {
                        LinearLayout rowScheduleItem = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.so_condition_total_item, null);
                        tvDescription[j] = (TextView) rowScheduleItem.findViewById(R.id.tvDescription);
                        tvTotalAmount[j] = (TextView) rowScheduleItem.findViewById(R.id.tvTotalAmount);
                        tvDescription[j].setText(salesOrderConditionsBean.getName());
                        tvTotalAmount[j].setText(ConstantsUtils.commaSeparator(salesOrderConditionsBean.getconditionValue(), salesOrderConditionsBean.getCondCurrency()));
                        tableScheduleHeading.addView(rowScheduleItem);
                    }
                }
                llSOCondition.addView(tableScheduleHeading);
            } else if (soListBean != null) {
                SOItemBean soItemBean = soListBean.getSoItemBeanArrayList().get(0);
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
                            tvTotalAmount[j].setText(ConstantsUtils.commaSeparator(soScheduleBean.getConditionValue(), soScheduleBean.getCondCurrency()));
                            tableScheduleHeading.addView(rowScheduleItem);
                        } else {
                            LinearLayout rowScheduleItem = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.so_condition_total_item, null);
                            tvDescription[j] = (TextView) rowScheduleItem.findViewById(R.id.tvDescription);
                            tvTotalAmount[j] = (TextView) rowScheduleItem.findViewById(R.id.tvTotalAmount);
                            tvDescription[j].setText(soScheduleBean.getName());
                            tvTotalAmount[j].setText(ConstantsUtils.commaSeparator(soScheduleBean.getConditionValue(), soScheduleBean.getCondCurrency()));
                            tableScheduleHeading.addView(rowScheduleItem);
                        }
                    }
                    llSOCondition.addView(tableScheduleHeading);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        /*finished condition */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cancel, menu);
        menuCancel = menu.findItem(R.id.menu_cancel);
        menuEdit = menu.findItem(R.id.menu_edit);
        menuHistory = menu.findItem(R.id.menu_history);

        if (!mStrBundleStatus.equalsIgnoreCase("A")) {
//        if (!mStrBundleStatus.equalsIgnoreCase("A") && !mStrBundleStatus.equalsIgnoreCase(""))

            menuCancel.setVisible(false);
            menuEdit.setVisible(false);
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
            String sharedVal = sharedPreferences.getString(Constants.isSOChangeEnabled, "");
            if (sharedVal.equalsIgnoreCase("/ARTEC/SF_SOCHN")) {
                menuEdit.setVisible(true);
            } else {
                menuEdit.setVisible(true);
            }

            sharedVal = sharedPreferences.getString(Constants.isSOCancelEnabled, "");
            if (sharedVal.equalsIgnoreCase("/ARTEC/SF_SOCNCL")) {
                menuCancel.setVisible(true);
            } else {
                menuCancel.setVisible(false);
            }
        }
        if (soListBean == null) {
            menuHistory.setVisible(false);
        } else if (soListBean.getStatus().equalsIgnoreCase("B")) {
            menuHistory.setVisible(true);
        } else {
            menuHistory.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_cancel:
                onCancelSO();
                break;
            case R.id.menu_edit:
                onEditScreen();
                break;
            case R.id.menu_history:
                onApproveHistory();
                break;

        }
        return true;
    }

    private void onApproveHistory() {
        Intent intent = new Intent(SalesOrderListDetailsActivity.this, SOApproveHistoryActivity.class);
        intent.putExtra(Constants.EXTRA_SO_DETAIL, soListBean);
        startActivity(intent);
    }

    private void onCancelSO() {
        showCancelDialog(SalesOrderListDetailsActivity.this);
    }

    /*displays alert with Message*/
    public void displayPopUpMsg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SalesOrderListDetailsActivity.this, R.style.MyTheme);
        builder.setMessage(mStrPopUpText)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();
                                    onBackPressed();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
        builder.show();
    }

    //    /**
//     * save data
//     */
    private void onCancelSave() {
        Set<String> set = new HashSet<>();
        ArrayList<HashMap<String, String>> itemTable = new ArrayList<>();
        String soEntityQry = Constants.SOs + "?$filter=" + Constants.SONo + " eq '" + mStrBundleOrderNo + "'";
        SalesOrderBean soHeaderItem = new SalesOrderBean();
        try {
            soHeaderItem = OfflineManager.getSalesOrder(soEntityQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        String doc_no = soHeaderItem.getOrderNo();
        Hashtable<String, String> masterHeaderTable = new Hashtable<>();
        masterHeaderTable.clear();
//        if (headerDetail != null) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        String loginIdVal = sharedPreferences.getString(Constants.username, "");

        masterHeaderTable.put(Constants.LOGINID, soHeaderItem.getLoginID());
        masterHeaderTable.put(Constants.SONo, doc_no);
        masterHeaderTable.put(Constants.OrderType, soHeaderItem.getOrderType());
        masterHeaderTable.put(Constants.OrderTypeText, soHeaderItem.getOrderTypeDesc());
        masterHeaderTable.put(Constants.OrderDate, soHeaderItem.getOrderDate());

        masterHeaderTable.put(Constants.CustomerNo, soHeaderItem.getSoldTo());
        masterHeaderTable.put(Constants.CustomerPO, soHeaderItem.getPONo());
        masterHeaderTable.put(Constants.CustomerPODate, soHeaderItem.getPODate());

        masterHeaderTable.put(Constants.ShipToParty, soHeaderItem.getShipTo());
        masterHeaderTable.put(Constants.ShipToPartyName, soHeaderItem.getShipToName());
        masterHeaderTable.put(Constants.SalesArea, soHeaderItem.getSalesArea());
        masterHeaderTable.put(Constants.SalesAreaDesc, soHeaderItem.getSalesAreaDesc());
        masterHeaderTable.put(Constants.SalesOffice, soHeaderItem.getSalesOfficeId());
        masterHeaderTable.put(Constants.SaleOffDesc, soHeaderItem.getSaleOffDesc());
        masterHeaderTable.put(Constants.SalesGroup, "");
        masterHeaderTable.put(Constants.SaleGrpDesc, "");
        masterHeaderTable.put(Constants.ShippingTypeID, soHeaderItem.getShippingTypeID());
        masterHeaderTable.put(Constants.ShippingTypeDesc, soHeaderItem.getShippingTypeDesc());
        masterHeaderTable.put(Constants.MeansOfTranstyp, "");
        masterHeaderTable.put(Constants.Plant, soHeaderItem.getPlant());
        masterHeaderTable.put(Constants.PlantDesc, soHeaderItem.getPlantDesc());
        masterHeaderTable.put(Constants.Incoterm1, soHeaderItem.getIncoTerm1());//headerDetail.get("IncoTerm1")
        masterHeaderTable.put(Constants.Incoterm2, soHeaderItem.getIncoterm1Desc());//headerDetail.get("IncoTerm1")
        masterHeaderTable.put(Constants.Payterm, soHeaderItem.getPaymentTerm());//headerDetail.get("PaymentTerm")
        masterHeaderTable.put(Constants.PaytermDesc, soHeaderItem.getPaymentTermDesc());


        masterHeaderTable.put(Constants.Remarks, soHeaderItem.getRemarks());
        masterHeaderTable.put(Constants.StatusUpdate, "NEW");

        masterHeaderTable.put(Constants.Currency, soHeaderItem.getCurrency());
        masterHeaderTable.put(Constants.NetPrice, soHeaderItem.getNetAmount());
        masterHeaderTable.put(Constants.TotalAmount, soHeaderItem.getTotalAmt());
        masterHeaderTable.put(Constants.TaxAmount, soHeaderItem.getTaxAmt());
        masterHeaderTable.put(Constants.Freight, soHeaderItem.getFreightAmt());
        masterHeaderTable.put(Constants.Discount, soHeaderItem.getDiscountAmt());
        masterHeaderTable.put(Constants.Testrun, "");

        masterHeaderTable.put(Constants.Status, soHeaderItem.getStatusID());
        masterHeaderTable.put(Constants.StatusDesc, soHeaderItem.getStatusDesc());
        masterHeaderTable.put(Constants.DelvStatus, soHeaderItem.getDelvStatus());
        masterHeaderTable.put(Constants.DelvStatusDesc, soHeaderItem.getDelvStatusDesc());

        masterHeaderTable.put(Constants.EntityType, Constants.SOUpdate);

        itemTable.clear();
        for (int itemIndex = 0; itemIndex < SalesOrderBeanList.size(); itemIndex++) {
            SalesOrderBean saveItem = SalesOrderBeanList.get(itemIndex);
            HashMap<String, String> dbItemTable = new HashMap<>();
            dbItemTable.put("SONo", doc_no);
            dbItemTable.put("ItemNo", saveItem.getsItemNo());
            dbItemTable.put("Material", saveItem.getMaterialNo());
            dbItemTable.put("MaterialText", saveItem.getMaterialDesc());
            dbItemTable.put("Quantity", saveItem.getQAQty());
            dbItemTable.put("UOM", saveItem.getUom());
            dbItemTable.put("Currency", saveItem.getCurrency());
            dbItemTable.put("OrderNo", doc_no);
            dbItemTable.put("Remarks", saveItem.getRemarks());
            dbItemTable.put(Constants.StorLoc, saveItem.getStorLoc());
            dbItemTable.put("Plant", saveItem.getPlant());
            dbItemTable.put(Constants.PlantDesc, saveItem.getPlantDesc());
            dbItemTable.put("StatusUpdate", "NEW");

            dbItemTable.put(Constants.UnitPrice, saveItem.getUnitPrice());
            dbItemTable.put(Constants.NetAmount, saveItem.getNetAmount());
            dbItemTable.put(Constants.GrossAmount, saveItem.getTotalAmt());
            dbItemTable.put(Constants.Freight, saveItem.getFreightAmt());
            dbItemTable.put(Constants.Tax, saveItem.getTaxAmt());
            dbItemTable.put(Constants.Discount, saveItem.getDiscountAmt());

            dbItemTable.put(Constants.StatusID, saveItem.getStatusID());
            dbItemTable.put(Constants.StatusDesc, saveItem.getStatusDesc());
            dbItemTable.put(Constants.DelvStatusID, saveItem.getDelvStatus());
            dbItemTable.put(Constants.DelvStatusDesc, saveItem.getDelvStatusDesc());

            dbItemTable.put(Constants.ItemCategory, saveItem.getItemCat());
            dbItemTable.put(Constants.ItemCatDesc, saveItem.getItemCatDesc());
            dbItemTable.put(Constants.DiscountPer, saveItem.getDiscountPer());
            dbItemTable.put(Constants.DepotStock, saveItem.getDepotStock());
            dbItemTable.put(Constants.OwnStock, saveItem.getOwnStock());
            dbItemTable.put(Constants.DelvQty, saveItem.getDelvQty());
            dbItemTable.put(Constants.OpenQty, saveItem.getOpenQty());

            dbItemTable.put(Constants.RejReason, mStrRejReason);
            dbItemTable.put(Constants.RejReasonDesc, mStrRejReasonDesc);
            itemTable.add(dbItemTable);
        }

        Gson gson1 = new Gson();
        String jsonFromMap = "";
        try {
            jsonFromMap = gson1.toJson(itemTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        masterHeaderTable.put(Constants.SalesOrderItems, jsonFromMap);
        set = sharedPreferences.getStringSet(Constants.SOCancel, null);

        HashSet<String> setTemp = new HashSet<>();
        if (set != null && !set.isEmpty()) {
            Iterator<String> itr = set.iterator();
            while (itr.hasNext()) {
                setTemp.add(itr.next().toString());
            }
        }
        setTemp.add(doc_no);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(Constants.SOCancel, setTemp);
        editor.apply();

        JSONObject jsonHeaderObject = new JSONObject(masterHeaderTable);

        try {
            ConstantsUtils.storeInDataVault(doc_no, jsonHeaderObject.toString(),this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        MyUtils.dialogConformButton(SalesOrderListDetailsActivity.this, "Sales order # " + doc_no + " cancelled successfully", new DialogCallBack() {
            @Override
            public void clickedStatus(boolean clickedStatus) {
                if (clickedStatus) {
                    SalesOrderTabActivity.isCancelledOrChanged = true;
                    onBackPressed();
//                        onUpdateSync();
                } else {
                    onBackPressed();
//                navtoPrevScreen();
                }
            }
        });
    }

    public void showCancelDialog(Activity activity) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.so_cancel_dialog);

        final Spinner spReject = (Spinner) dialog.findViewById(R.id.spRejectReason);
        String mStrConfigQry = Constants.ConfigTypesetTypes + "?$filter=" + Constants.Typeset + " eq '" + Constants.REJRSN + "' &$orderby = Types asc";
        String[][] mArrayRejReason = null;
        try {
            mArrayRejReason = OfflineManager.getConfigTysetTypesValues(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        if (mArrayRejReason == null) {
            mArrayRejReason = new String[2][1];
            mArrayRejReason[0][0] = "";
            mArrayRejReason[1][0] = Constants.None;
        }

        String[][] tempStatusArray = new String[2][mArrayRejReason[0].length + 1];
        tempStatusArray[0][0] = "";
        tempStatusArray[1][0] = Constants.None;
        for (int i = 1; i < mArrayRejReason[0].length + 1; i++) {
            tempStatusArray[0][i] = mArrayRejReason[0][i - 1];
            tempStatusArray[1][i] = mArrayRejReason[1][i - 1];
        }
        mArrayRejReason = tempStatusArray;
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, R.layout.custom_textview, mArrayRejReason[1]);
        arrayAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spReject.setAdapter(arrayAdapter);
        final String[][] finalMArrayRejReason = mArrayRejReason;
        spReject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                spReject.setBackgroundResource(R.drawable.spinner_bg);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        Button okButton = (Button) dialog.findViewById(R.id.btYes);
        Button cancleButton = (Button) dialog.findViewById(R.id.btNo);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPos = spReject.getSelectedItemPosition();
                //Perfome Action
                if (selectedPos > 0) {
                    dialog.dismiss();
                    mStrRejReason = finalMArrayRejReason[0][selectedPos];
                    mStrRejReasonDesc = finalMArrayRejReason[1][selectedPos];
                    onCancelSave();
                } else {
                    spReject.setBackgroundResource(R.drawable.error_spinner);
                }
            }
        });
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void onEditScreen() {
        Intent intentSOChange = new Intent(SalesOrderListDetailsActivity.this, SOChangeActivity.class);
        intentSOChange.putExtra(Constants.CPNo, mStrBundleCustomerNumber);
        intentSOChange.putExtra(Constants.CPUID, mStrBundleCustomerNumber);
        intentSOChange.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentSOChange.putExtra(Constants.CPGUID32, mStrBundleRetUID);
        intentSOChange.putExtra(Constants.comingFrom, "reports");
        intentSOChange.putExtra(Constants.OrderNo, mStrBundleOrderNo);
        intentSOChange.putExtra(Constants.ORDER_TYPE, mStrBundleOrderType);
        intentSOChange.putExtra(Constants.ORDER_TYPE_DESC, mStrBundleOrderTypeDesc);
        intentSOChange.putExtra(Constants.SALESAREA, mStrBundleSalesArea);
        intentSOChange.putExtra(Constants.SALESAREA_DESC, mStrBundleSalesAreaDesc);
        intentSOChange.putExtra(Constants.SOLDTO, mStrBundleSoldTo);
        intentSOChange.putExtra(Constants.SOLDTONAME, mStrBundleSoldToDesc);
        intentSOChange.putExtra(Constants.SHIPPINTPOINT, mStrBundleShippingPoint);
        intentSOChange.putExtra(Constants.SHIPPINTPOINTDESC, mStrBundleShippingPointDesc);
        intentSOChange.putExtra(Constants.SHIPTO, mStrBundleShipTo);
        intentSOChange.putExtra(Constants.SHIPTONAME, mStrBundleShipToDesc);
        intentSOChange.putExtra(Constants.PLANT, SalesOrderBeanList.get(0).getPlant());
        intentSOChange.putExtra(Constants.PLANTDESC, SalesOrderBeanList.get(0).getPlantDesc());
        intentSOChange.putExtra(Constants.INCOTERM1, mStrBundleIncoTerm1);
        intentSOChange.putExtra(Constants.INCOTERM1DESC, mStrBundleIncoTerm1Desc);
        intentSOChange.putExtra(Constants.INCOTERM2, mStrBundleIncoTerm2);
        intentSOChange.putExtra(Constants.SALESDISTRICT, mStrBundleSalesDistrict);
        intentSOChange.putExtra(Constants.SALESDISTRICTDESC, mStrBundleSalesDistrictDesc);
        intentSOChange.putExtra(Constants.ROUTE, mStrBundleRoute);
        intentSOChange.putExtra(Constants.ROUTEDESC, mStrBundleRouteDesc);
        intentSOChange.putExtra(Constants.SplProcessing, mStrBundleSplProcessing);
        intentSOChange.putExtra(Constants.SplProcessingDesc, mStrBundleSplProcessingDesc);
        intentSOChange.putExtra(Constants.MEANSOFTRANSPORT, mStrBundleMeanOfTrans);
        intentSOChange.putExtra(Constants.MEANSOFTRANSPORTDESC, mStrBundleMeanOfTransDesc);
        intentSOChange.putExtra(Constants.STORAGELOC, mStrBundleStorageLoc);
        intentSOChange.putExtra(Constants.STORAGELOCDESC, mStrBundleStorageLocDesc);
        intentSOChange.putExtra(Constants.Payterm, mStrBundlePayment);
        intentSOChange.putExtra(Constants.PaytermDesc, mStrBundlePaymentDesc);
        intentSOChange.putExtra(Constants.CUSTOMERPO, mStrBundleCustomerPoNo);
        intentSOChange.putExtra(Constants.CUSTOMERPODATE, mStrBundleCustomerPoDate);
        intentSOChange.putExtra(Constants.EXTRA_ORDER_DATE, mStrBundleDate);
        intentSOChange.putExtra(Constants.Remarks, mStrBundleRemarks);
        intentSOChange.putExtra("items", (Serializable) SalesOrderBeanList);
        startActivity(intentSOChange);
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
                    int marginB = ConstantsUtils.dpToPx(8, this);
                    if (llSOCondition.getVisibility() == View.VISIBLE) {
                        if (getLayoutParams(cvPricingDetails).topMargin != 0) {
                            marginB = 0;
                        }
                    } else {
                        marginB = 0;
                        ViewGroup.MarginLayoutParams layoutParamss = getLayoutParams(cvPricingDetails);
                        layoutParamss.setMargins(ConstantsUtils.dpToPx(8, this), 0, ConstantsUtils.dpToPx(8, this), layoutParamss.bottomMargin);
                        cvPricingDetails.requestLayout();
                    }
                    layoutParams.setMargins(ConstantsUtils.dpToPx(8, this), ConstantsUtils.dpToPx(8, this), ConstantsUtils.dpToPx(8, this), marginB);
                    cvOrderDetails.requestLayout();
                } else {
                    ivExpandIcon.setImageResource(R.drawable.ic_arrow_up_black_24dp);
                    llHeader.setVisibility(View.VISIBLE);
                    tvDate.setVisibility(View.GONE);
                    int marginB = ConstantsUtils.dpToPx(8, this);
                    if (llSOCondition.getVisibility() == View.VISIBLE) {
                        if (getLayoutParams(cvPricingDetails).topMargin != 0) {
                            marginB = 0;
                        }
                    }
                    ViewGroup.MarginLayoutParams layoutParams = getLayoutParams(cvOrderDetails);
                    layoutParams.setMargins(ConstantsUtils.dpToPx(8, this), ConstantsUtils.dpToPx(8, this), ConstantsUtils.dpToPx(8, this), marginB);

                    cvOrderDetails.requestLayout();

                }
                break;
            case R.id.ivPricingDetails:
                if (llSOCondition.getVisibility() == View.VISIBLE) {
                    ivPricingDetails.setImageResource(R.drawable.ic_arrow_down_black_24dp);
                    llSOCondition.setVisibility(View.GONE);
                    tvAmount.setVisibility(View.VISIBLE);
                    int marginB = ConstantsUtils.dpToPx(8, this);
                    int marginT = ConstantsUtils.dpToPx(8, this);
                    if (llItemList.getVisibility() == View.VISIBLE) {
                        if (getLayoutParams(cvItem).topMargin != 0) {
                            marginB = 0;
                        }
                    } else {
                        marginB = 0;
                        ViewGroup.MarginLayoutParams layoutParamss = getLayoutParams(cvItem);
                        layoutParamss.setMargins(ConstantsUtils.dpToPx(8, this), 0, ConstantsUtils.dpToPx(8, this), 0);
                        cvItem.requestLayout();
                    }
                    if (llHeader.getVisibility() == View.VISIBLE) {
                        if (getLayoutParams(cvOrderDetails).bottomMargin != 0) {
                            marginT = 0;
                        }
                    } else {
                        marginT = 0;
                        ViewGroup.MarginLayoutParams layoutParamss = getLayoutParams(cvOrderDetails);
                        layoutParamss.setMargins(ConstantsUtils.dpToPx(8, this), ConstantsUtils.dpToPx(8, this), ConstantsUtils.dpToPx(8, this), 0);
                        cvOrderDetails.requestLayout();
                    }

                    ViewGroup.MarginLayoutParams layoutParams = getLayoutParams(cvPricingDetails);
                    layoutParams.setMargins(ConstantsUtils.dpToPx(8, this), marginT, ConstantsUtils.dpToPx(8, this), marginB);
                    cvPricingDetails.requestLayout();

                } else {


                    ivPricingDetails.setImageResource(R.drawable.ic_arrow_up_black_24dp);
                    llSOCondition.setVisibility(View.VISIBLE);
                    tvAmount.setVisibility(View.GONE);
                    int marginB = 0;//ConstantsUtils.dpToPx(8,this);
                    int marginT = ConstantsUtils.dpToPx(8, this);
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
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) cvPricingDetails.getLayoutParams();
                    layoutParams.setMargins(ConstantsUtils.dpToPx(8, this), marginT, ConstantsUtils.dpToPx(8, this), marginB);
                    cvPricingDetails.requestLayout();
                }
                break;
        }
    }

    private ViewGroup.MarginLayoutParams getLayoutParams(CardView cardView) {
        return (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
    }

    @Override
    public void onItemClick(SalesOrderBean salesOrderBean, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new SODetailsViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, SalesOrderBean salesOrderBean) {
        ((SODetailsViewHolder) viewHolder).tvMaterialDesc.setText(salesOrderBean.getMatNoAndDesc());
        try {
            ((SODetailsViewHolder) viewHolder).tvQty.setText(ConstantsUtils.checkNoUOMZero(salesOrderBean.getUom(), salesOrderBean.getQAQty()) + " " + salesOrderBean.getUom());
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        ((SODetailsViewHolder) viewHolder).tvAmount.setText(ConstantsUtils.commaSeparator(UtilConstants.removeLeadingZero(salesOrderBean.getNetAmount()), salesOrderBean.getCurrency()) + " " + salesOrderBean.getCurrency());
        if (!TextUtils.isEmpty(salesOrderBean.getStatusID()) && !TextUtils.isEmpty(salesOrderBean.getDelvStatus())) {
            delvStatusImg = SOUtils.displayStatusImage(salesOrderBean.getStatusID(), salesOrderBean.getDelvStatus(), SalesOrderListDetailsActivity.this);
            ((SODetailsViewHolder) viewHolder).ivDelvStatus.setImageDrawable(delvStatusImg);
        } else if (TextUtils.isEmpty(salesOrderBean.getStatusID())) {
            Drawable drawable = SOUtils.getSODefaultDrawable(SalesOrderListDetailsActivity.this);
            if (drawable != null)
                ((SODetailsViewHolder) viewHolder).ivDelvStatus.setImageDrawable(drawable);
        } else if (delvStatusImg != null) {
            ((SODetailsViewHolder) viewHolder).ivDelvStatus.setImageDrawable(delvStatusImg);
        }
    }
}
