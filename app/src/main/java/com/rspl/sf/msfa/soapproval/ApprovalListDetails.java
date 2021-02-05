package com.rspl.sf.msfa.soapproval;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.AsyncTaskCallBack;
import com.rspl.sf.msfa.interfaces.AsyncTaskCallBakSoNotes;
import com.rspl.sf.msfa.interfaces.CustomDialogCallBack;
import com.rspl.sf.msfa.mbo.SalesOrderBean;
import com.rspl.sf.msfa.reports.daySummary.DaySummaryPresenterImpl;
import com.rspl.sf.msfa.so.SOUtils;
import com.rspl.sf.msfa.socreate.CreditLimitBean;
import com.rspl.sf.msfa.socreate.stepOne.SOCreateActivity;
import com.rspl.sf.msfa.solist.SOListBean;
import com.rspl.sf.msfa.solist.SOTaskHistoryBean;
import com.rspl.sf.msfa.solist.SOTextBean;
import com.rspl.sf.msfa.store.GetOnlineODataInterface;
import com.rspl.sf.msfa.store.OnlineManager;
import com.rspl.sf.msfa.ui.fabTnsfmgToolBar.FABToolbarLayout;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ApprovalListDetails extends AppCompatActivity implements GetOnlineODataInterface, UIListener, AsyncTaskCallBack, CustomDialogCallBack, AsyncTaskCallBakSoNotes, View.OnClickListener, AdapterInterface<SalesOrderBean> {
    public static boolean isCancelledOrChanged = false;
    public ArrayList<SalesOrderBean> header;
    public ArrayList<SalesOrderBean> items = null;
    private TextView tvDate;
    private TextView tvSONo;
    private TextView tvAmount;
    private Hashtable<String, String> masterHeaderTable = new Hashtable<>();
    private ProgressDialog progressDialog = null;
    private String mStrInstanceId = "";

    private LinearLayout llSalesArea;
    private LinearLayout llPlant;
    private LinearLayout llRefDoc, llCustomerPo, llCustomerPoDate, llShipToAdd, llShippingType, llIncoTerm1, llIncoTerm2, llUnloadingPoint, llReceivingPoint, llMeansOfTranst, llPaymentTerm;
    private LinearLayout llBillTo;
    private TextView tvOrderDateDesc;
    private LinearLayout llOrderDate;
    private TextView tvOrderType;
    private TextView tvShipToPartyName;
    private TextView tvSalesAreaDesc;
    private TextView tvCustomerPODesc, tvCustomerPODateDesc;
    private TextView tvShippingTypeDesc;
    private TextView tvIncoterm1Desc;
    private TextView tvIncoterm2Desc;
    private TextView tvPaytermDesc;
    private ImageView ivExpandIcon;
    private LinearLayout llHeader;
    private Context mContext;
    private CardView cvOrderDetails;
    private LinearLayout llSOCondition;
    private ImageView ivPricingDetails;
    private CardView cvPricingDetails;
    private LinearLayout llItemList;
    private CardView cvItem;
    private RecyclerView recyclerView;
    private SimpleRecyclerViewAdapter<SalesOrderBean> soItemAdapter;
    private Toolbar toolbar;

    private NestedScrollView nestedScroll;
    private FloatingActionButton fabToolbar;
    private FABToolbarLayout fabToolbarContainer;
    private View viewLayout;
    private String mStrID = "";
    private String comments = "";
    private View tvReject;
    private View tvApprove;
    private List<CreditLimitBean> creditLimitBean;
    private TextView tvCreditAmount,tvCreditAmountFBD;

    private View tvEditApprove;
    private RecyclerView rvApprovalHistory;
    private TextView noRFApprovalHistory;
    private SimpleRecyclerViewAdapter<SOTaskHistoryBean> simpleApprovalHistoryAdapter;
    private CardView soApprovalHist;
    private ArrayList<SOTaskHistoryBean> soTaskHistoryList=null;
    private TextView tvTotlWeightAmt;
    private TextView tvReferenceHeader;
    private int retryCount = 0;
    boolean isClickable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_Sales_order_details), 0);
        setSupportActionBar(toolbar);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            header = (ArrayList<SalesOrderBean>) getIntent().getSerializableExtra("Header");
            items = (ArrayList<SalesOrderBean>) getIntent().getSerializableExtra("Items");
            soTaskHistoryList = (ArrayList<SOTaskHistoryBean>) getIntent().getSerializableExtra("ApprovalHistory");
            mStrInstanceId = bundleExtras.getString(Constants.EXTRA_SO_INSTANCE_ID, "");
            creditLimitBean =  (ArrayList<CreditLimitBean>) getIntent().getSerializableExtra(Constants.EXTRA_SO_CREDIT_LIMIT);
            mContext = ApprovalListDetails.this;
            Constants.SOBundleExtras = bundleExtras;
            if (header==null){
                header = new ArrayList<>();
            }if (items==null){
                items = new ArrayList<>();
            }
        }
        //declare UI
        setUI();
    }


    /**
     * declare UI
     */
    private void setUI() {
        try {
            tvReject = findViewById(R.id.tvReject);
            tvApprove = findViewById(R.id.tvApprove);
            tvEditApprove = findViewById(R.id.tvEditApprove);

            viewLayout = findViewById(android.R.id.content);
            tvDate = (TextView) findViewById(R.id.tvDate);
            tvSONo = (TextView) findViewById(R.id.tvSONo);
            tvAmount = (TextView) findViewById(R.id.tvAmount);
            nestedScroll = (NestedScrollView) findViewById(R.id.nestedScroll);
            fabToolbar = (FloatingActionButton) findViewById(R.id.fabtoolbar);
            fabToolbarContainer = (FABToolbarLayout) findViewById(R.id.fabtoolbarContainer);
            fabToolbar.setOnClickListener(this);
            tvReject.setOnClickListener(this);
            tvApprove.setOnClickListener(this);
            tvEditApprove.setOnClickListener(this);
            llSalesArea = (LinearLayout) findViewById(R.id.llSalesArea);
            llPlant = (LinearLayout) findViewById(R.id.llPlant);
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
            tvCreditAmount = (TextView) findViewById(R.id.tvCreditAmount);
            tvCreditAmountFBD = (TextView) findViewById(R.id.tvCreditAmountFBD);

            tvCreditAmountFBD.setVisibility(View.GONE);

            llPlant.setVisibility(View.GONE);
            llRefDoc.setVisibility(View.GONE);
            llCustomerPoDate.setVisibility(View.GONE);
            llUnloadingPoint.setVisibility(View.GONE);
            llReceivingPoint.setVisibility(View.GONE);
            llMeansOfTranst.setVisibility(View.GONE);
            llBillTo.setVisibility(View.GONE);


            tvOrderType = (TextView) findViewById(R.id.tvOrderType);
            tvShipToPartyName = (TextView) findViewById(R.id.tvShipToPartyName);
            tvSalesAreaDesc = (TextView) findViewById(R.id.tvSalesAreaDesc);
            tvCustomerPODesc = (TextView) findViewById(R.id.tvCustomerPODesc);
            tvCustomerPODateDesc = (TextView) findViewById(R.id.tvCustomerPODateDesc);
            tvShippingTypeDesc = (TextView) findViewById(R.id.tvShippingTypeDesc);
            tvIncoterm1Desc = (TextView) findViewById(R.id.tvIncoterm1Desc);
            tvIncoterm2Desc = (TextView) findViewById(R.id.tvIncoterm2Desc);
            tvPaytermDesc = (TextView) findViewById(R.id.tvPaytermDesc);
            tvOrderDateDesc = (TextView) findViewById(R.id.tvOrderDateDesc);
            tvTotlWeightAmt = (TextView) findViewById(R.id.tvTotlWeightAmt);
            tvReferenceHeader = (TextView) findViewById(R.id.tvReferenceHeader);

            ivExpandIcon = (ImageView) findViewById(R.id.ivOrderDetails);
            llHeader = (LinearLayout) findViewById(R.id.headerItem);
            cvOrderDetails = (CardView) findViewById(R.id.cvOrderDetails);
            llSOCondition = (LinearLayout) findViewById(R.id.llSOCondition);
            ivPricingDetails = (ImageView) findViewById(R.id.ivPricingDetails);
            llItemList = (LinearLayout) findViewById(R.id.llItemList);
            cvItem = (CardView) findViewById(R.id.cvItem);
            ivPricingDetails.setOnClickListener(this);
            cvPricingDetails = (CardView) findViewById(R.id.cvPricingDetails);
            ivExpandIcon.setOnClickListener(this);

            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            ViewCompat.setNestedScrollingEnabled(recyclerView, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            soItemAdapter = new SimpleRecyclerViewAdapter<SalesOrderBean>(ApprovalListDetails.this, R.layout.so_item_material, this, recyclerView, null);
            recyclerView.setAdapter(soItemAdapter);

            soApprovalHist = (CardView) findViewById(R.id.soApprovalHist);
            rvApprovalHistory = (RecyclerView) findViewById(R.id.rvApprovalHistory);
            noRFApprovalHistory = (TextView) findViewById(R.id.no_record_found);
            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
            rvApprovalHistory.setLayoutManager(linearLayoutManager1);
            rvApprovalHistory.setHasFixedSize(true);
            if (soTaskHistoryList == null) {
                soTaskHistoryList = new ArrayList<>();
            }

            if (header != null && header.size() > 0) {
                tvDate.setText(header.get(0).getOrderDate());
                tvSONo.setText(header.get(0).getOrderNo());
                tvOrderType.setText(getString(R.string.po_details_display_value, header.get(0).getOrderTypeDesc(), header.get(0).getOrderType()));
                tvAmount.setText(ConstantsUtils.commaSeparator(header.get(0).getTotalAmt(), header.get(0).getCurrency()) + " " + header.get(0).getCurrency());


                String totalQty = "";
                if (!TextUtils.isEmpty(header.get(0).getmSteTotalQtyUOM())) {
                    totalQty = header.get(0).getQAQty() + " " + header.get(0).getmSteTotalQtyUOM();
                } else {
                    totalQty = header.get(0).getQAQty();
                }
                try {
                    tvTotlWeightAmt.setText(ConstantsUtils.checkNoUOMZero(header.get(0).getmStrWeightUOM(), header.get(0).getmStrTotalWeight()) + " " + header.get(0).getmStrWeightUOM() + " / " + totalQty);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }

                if (!TextUtils.isEmpty(header.get(0).getOrderDate())) {
                    tvOrderDateDesc.setText(header.get(0).getOrderDate());
                    llOrderDate.setVisibility(View.GONE);
                } else {
                    llOrderDate.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(header.get(0).getSalesAreaDesc())) {
                    tvSalesAreaDesc.setText(getString(R.string.po_details_display_value, header.get(0).getSalesAreaDesc(), header.get(0).getSalesArea()));
                    llSalesArea.setVisibility(View.VISIBLE);
                } else {
                    llSalesArea.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(header.get(0).getShipToName())) {
                    tvShipToPartyName.setText(getString(R.string.po_details_display_value, header.get(0).getShipToName(), header.get(0).getShipTo()));
                    llShipToAdd.setVisibility(View.VISIBLE);
                } else {
                    llShipToAdd.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(header.get(0).getShippingPointDesc())) {
                    tvShippingTypeDesc.setText(getString(R.string.po_details_display_value, header.get(0).getShippingPointDesc(), header.get(0).getShippingPoint()));
                    llShippingType.setVisibility(View.VISIBLE);
                } else {
                    llShippingType.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(header.get(0).getIncoterm1Desc())) {
                    tvIncoterm1Desc.setText(getString(R.string.po_details_display_value, header.get(0).getIncoterm1Desc(), header.get(0).getIncoTerm1()));
                    llIncoTerm1.setVisibility(View.GONE);
                } else {
                    llIncoTerm1.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(header.get(0).getIncoterm2())) {
                    tvIncoterm2Desc.setText(header.get(0).getIncoterm2());
                    llIncoTerm2.setVisibility(View.GONE);
                } else {
                    llIncoTerm2.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(header.get(0).getPaytermDesc())) {
                    tvPaytermDesc.setText(getString(R.string.po_details_display_value, header.get(0).getPaytermDesc(), header.get(0).getPaymentTerm()));
                    llPaymentTerm.setVisibility(View.VISIBLE);
                } else {
                    llPaymentTerm.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(header.get(0).getPONo())) {
                    tvCustomerPODesc.setText(header.get(0).getPONo());
                    llCustomerPo.setVisibility(View.VISIBLE);
                } else {
                    llCustomerPo.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(header.get(0).getPODate())) {
                    tvCustomerPODateDesc.setText(header.get(0).getPODate());
                    llCustomerPoDate.setVisibility(View.VISIBLE);
                } else {
                    llCustomerPoDate.setVisibility(View.GONE);
                }

                if (llCustomerPoDate.getVisibility() == View.GONE && llCustomerPo.getVisibility() == View.GONE) {
                    tvReferenceHeader.setVisibility(View.GONE);
                }
                if (items != null && !items.isEmpty()) {
                    soItemAdapter.refreshAdapter(items);
                }
                /*if (creditLimitBean != null) {
                    tvCreditAmount.setText(ConstantsUtils.commaSeparator(creditLimitBean.getBalanceAmount(), creditLimitBean.getCurrency()) + " " + creditLimitBean.getCurrency());
                }*/

                if (creditLimitBean != null && creditLimitBean.size()>0){
                    for(CreditLimitBean creditID : creditLimitBean){
                        if(creditID.getCreditControlAreaID().equalsIgnoreCase("1010")){
                            tvCreditAmount.setText("Total Balance:- "+ConstantsUtils.commaSeparator(creditID.getBalanceAmount(), creditID.getCurrency()) + " " + creditID.getCurrency());
                        }else if(creditID.getCreditControlAreaID().equalsIgnoreCase("1030")){
                          //  tvCreditAmountFBD.setText("FBD : "+ConstantsUtils.commaSeparator(creditID.getBalanceAmount(), creditID.getCurrency()) + " " + creditID.getCurrency());
                        }
                    }


                }


                displayConditionItemDetails(header.get(0).getSalesOrderConditionsBeanArrayList());
            }
            displayApprovalHistory(soTaskHistoryList);
            nestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (!fabToolbarContainer.isFab())
                        fabToolbarContainer.hide();
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void refreshAppovalHistory(ArrayList<SOTaskHistoryBean> soTaskHistoryBeanArrayList) {
        simpleApprovalHistoryAdapter.refreshAdapter(soTaskHistoryBeanArrayList);
    }

    private void displayApprovalHistory(ArrayList<SOTaskHistoryBean> soTaskHistoryBeanArrayList) {
        simpleApprovalHistoryAdapter = new SimpleRecyclerViewAdapter<SOTaskHistoryBean>(ApprovalListDetails.this, R.layout.approval_item, new AdapterInterface<SOTaskHistoryBean>() {
            @Override
            public void onItemClick(SOTaskHistoryBean o, View view, int i) {
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
                return new TaskHistoryVH(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, SOTaskHistoryBean o) {
                int resImg = R.drawable.ic_hourglass_empty_black_24dp;
                int resColor = R.color.InvStatusOrange;
                if (o.getActionName().equalsIgnoreCase(getString(R.string.details_approved))) {
                    resImg = R.drawable.ic_done_black_24dp;
                    resColor = R.color.InvStatusGreen;
                } else if (o.getActionName().equalsIgnoreCase(getString(R.string.details_rejected))) {
                    resImg = R.drawable.ic_close_black_24dp;
                    resColor = R.color.InvStatusRed;
                }
                Drawable drawable = ContextCompat.getDrawable(ApprovalListDetails.this, resImg);
                drawable.setColorFilter(ContextCompat.getColor(ApprovalListDetails.this, resColor), PorterDuff.Mode.SRC_IN);
                ((TaskHistoryVH) viewHolder).ivStatus.setImageDrawable(drawable);
                ((TaskHistoryVH) viewHolder).tvActionName.setText(o.getPerformedByName());
                ((TaskHistoryVH) viewHolder).tvCustomerName.setText(o.getTimestamp());
                ((TaskHistoryVH) viewHolder).tvRemarks.setText(o.getComments());
            }
        }, soApprovalHist, null);
        rvApprovalHistory.setAdapter(simpleApprovalHistoryAdapter);
        refreshAppovalHistory(soTaskHistoryBeanArrayList);
    }

    private void displayConditionItemDetails(ArrayList<SalesOrderConditionsBean> salesOrderConditionsBeanArrayList) {

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
                        tvTotalAmount[j].setText(ConstantsUtils.commaSeparator(salesOrderConditionsBean.getconditionValue(),salesOrderConditionsBean.getCondCurrency()));
                        tableScheduleHeading.addView(rowScheduleItem);
                    } else {
                        LinearLayout rowScheduleItem = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.so_condition_total_item, null);
                        tvDescription[j] = (TextView) rowScheduleItem.findViewById(R.id.tvDescription);
                        tvTotalAmount[j] = (TextView) rowScheduleItem.findViewById(R.id.tvTotalAmount);
                        tvDescription[j].setText(salesOrderConditionsBean.getName());
                        tvTotalAmount[j].setText(ConstantsUtils.commaSeparator(salesOrderConditionsBean.getconditionValue(),salesOrderConditionsBean.getCondCurrency()));
                        tableScheduleHeading.addView(rowScheduleItem);
                    }
                }
                llSOCondition.addView(tableScheduleHeading);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
          /*  case R.id.menu_approve:
                if (!TextUtils.isEmpty(mStrInstanceId)) {
                    SOUtils.showCommentsDialog(ApprovalListDetails.this, new CustomDialogCallBack() {
                        @Override
                        public void cancelDialogCallBack(boolean userClicked, String ids, String description) {
                            if (userClicked) {
                                approveOrder(mStrInstanceId, "01", header.get(0).getOrderNo(), description + "");
                            }
                        }
                    }, getString(R.string.approve_title_comments));
                }
                break;
            case R.id.menu_reject:
                if (!TextUtils.isEmpty(mStrInstanceId)) {
                    SOUtils.showCommentsDialog(ApprovalListDetails.this, new CustomDialogCallBack() {
                        @Override
                        public void cancelDialogCallBack(boolean userClicked, String ids, String description) {
                            if (userClicked) {
                                approveOrder(mStrInstanceId, "02", header.get(0).getOrderNo(), description + "");
                            }
                        }
                    }, getString(R.string.reject_title_comments));
                }
                break;*/

        }
        return true;
    }


    /*approve order*/
    private void approveOrder(String mStrInstanceId, String desisionKey, String soNo, String comments) {
        try {
            if (UtilConstants.isNetworkAvailable(ApprovalListDetails.this)) {
                ConstantsUtils.APPROVALERRORMSG = "";
                if(Constants.writeDebug) {
                    LogManager.writeLogDebug("SO Approval Started");
                }
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
                String loginIdVal = sharedPreferences.getString(Constants.username, "");
                masterHeaderTable.clear();
                masterHeaderTable.put(Constants.InstanceID, mStrInstanceId);
                masterHeaderTable.put(Constants.EntityType, "SO");
                masterHeaderTable.put(Constants.DecisionKey, desisionKey);
                masterHeaderTable.put(Constants.LoginID, loginIdVal);
                masterHeaderTable.put(Constants.EntityKey, soNo);
                masterHeaderTable.put(Constants.Comments, comments);

                JSONObject headerObject = new JSONObject();
                try {
                    headerObject.putOpt(Constants.InstanceID, masterHeaderTable.get(Constants.InstanceID));
                    headerObject.putOpt(Constants.EntityType, masterHeaderTable.get(Constants.EntityType));
                    headerObject.putOpt(Constants.DecisionKey, masterHeaderTable.get(Constants.DecisionKey));
                    headerObject.putOpt(Constants.LoginID, masterHeaderTable.get(Constants.LoginID));
                    headerObject.putOpt(Constants.EntityKey, masterHeaderTable.get(Constants.EntityKey));
                    headerObject.putOpt(Constants.Comments, masterHeaderTable.get(Constants.Comments));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                String qry =Constants.Tasks + "(InstanceID='" + masterHeaderTable.get(Constants.InstanceID) + "',EntityType='" + masterHeaderTable.get(Constants.EntityType) + "')";


                progressDialog = ConstantsUtils.showProgressDialog(ApprovalListDetails.this, "Update data please wait...");
                OnlineManager.updateEntity("",headerObject.toString(),qry, ApprovalListDetails.this,mContext);
                /* new DirecySyncAsyncTask(ApprovalListDetails.this, ApprovalListDetails.this, ApprovalListDetails.this, masterHeaderTable, null, 2).execute();*/
                DaySummaryPresenterImpl.isReloadSOApproval = true;
            } else {
                isClickable = false;
                ConstantsUtils.displayLongToast(ApprovalListDetails.this,getString(R.string.no_network_conn));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestError(int i, Exception e) {
        try {
            isClickable = false;
            if(e.getLocalizedMessage()!=null && e.getLocalizedMessage().contains("CSRF token validation failed") && retryCount==0){
                /*Bundle bundle = new Bundle();
                bundle.putString(Constants.BUNDLE_RESOURCE_PATH, Constants.KPISet);
                bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 1);
                bundle.putInt(Constants.BUNDLE_OPERATION, Operation.GetRequest.getValue());
                bundle.putBoolean(Constants.BUNDLE_SESSION_REQUIRED, true);
                bundle.putBoolean(Constants.BUNDLE_SESSION_URL_REQUIRED, true);
                try {
                    OnlineManager.requestOnline(this, bundle, ApprovalListDetails.this);
                } catch (Exception exception) {
                    LogManager.writeLogError(Constants.error_txt1 + " : " + exception.getMessage());
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                        }
                    });
                }*/

                boolean isStoreOpened = false;
                Constants.IsOnlineStoreFailed = false;
                Log.d("ApprovalList","Opening Store");
                    try {
                        isStoreOpened = OnlineManager.openOnlineStore(ApprovalListDetails.this, true);
                        Log.d("ApprovalList","Store opened"+isStoreOpened);
                    } catch (com.rspl.sf.msfa.store.OnlineODataStoreException e3) {
                        e3.printStackTrace();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }

                if (isStoreOpened) {
                    try {
                        mStrID = Constants.ApprovalStatus01;
                        approveOrder(mStrInstanceId, Constants.ApprovalStatus01, header.get(0).getOrderNo(), comments + "");
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        LogManager.writeLogError(Constants.error_txt + " : " + e2.getMessage());
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                }else{
                    LogManager.writeLogError(Constants.error_txt + " : " + "Store not opened cant post Approve SO");
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                }
                retryCount++;

            }else {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                String statusMsg = mContext.getString(R.string.so_details_msg_approved);
                if (!TextUtils.isEmpty(ConstantsUtils.APPROVALERRORMSG)) {
                    statusMsg = ConstantsUtils.APPROVALERRORMSG;
                LogManager.writeLogDebug("SO Approval : Approve Failed"+e.getLocalizedMessage());
                } else if (mStrID.equalsIgnoreCase(Constants.RejectStatus)){
                    statusMsg = mContext.getString(R.string.so_details_msg_rejected);
            LogManager.writeLogDebug("SO Reject : Approve Failed"+e.getLocalizedMessage());
}
       /* MyUtils.dialogConformButton(ApprovalListDetails.this, getString(R.string.so_apprvol_failed, statusMsg), new DialogCallBack() {
            @Override
            public void clickedStatus(boolean clickedStatus) {
                if (clickedStatus) {
                    isCancelledOrChanged = true;
                    onListScreen();
                } else {
                    onListScreen();
                }
            }
        });*/

                UtilConstants.dialogBoxWithCallBack(ApprovalListDetails.this, "", getString(R.string.so_apprvol_failed, statusMsg)+"\nUnable to process request,Please try again", getString(R.string.ok), "", false, new com.arteriatech.mutils.interfaces.DialogCallBack() {
                    @Override
                    public void clickedStatus(boolean b) {
                        finish();
                    }
                });
            }
        } catch (Throwable e1) {
            e1.printStackTrace();
            LogManager.writeLogDebug("SO Reject : Approve Failed"+e1.getLocalizedMessage());
        }
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            String statusMsg = mContext.getString(R.string.so_details_msg_approved);
            if (mStrID.equalsIgnoreCase(Constants.RejectStatus))
                statusMsg = mContext.getString(R.string.so_details_msg_rejected);
            if(Constants.writeDebug) {
                LogManager.writeLogDebug("SO Approval : Success " + statusMsg );
            }
            UtilConstants.dialogBoxWithCallBack(ApprovalListDetails.this, "", getString(R.string.so_apprvol_success, statusMsg), getString(R.string.ok), "", false, new com.arteriatech.mutils.interfaces.DialogCallBack() {
                @Override
                public void clickedStatus(boolean b) {
                    onListScreen();
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
            if(Constants.writeDebug) {
                LogManager.writeLogDebug("SO Approval : Success " + e.getLocalizedMessage() );
            }
        }

    }

    public void onListScreen() {
        try {
            isClickable = false;
            SOApproveActivity.isRefresh = true;
            finish();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatus(boolean status, String values) {

    }

    @Override
    public void cancelDialogCallBack(boolean userClicked, String ids, String description) {

    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> entities, int operation, int requestCode, String resourcePath, Bundle bundle) {
        switch (requestCode) {
            case 1:
                try {
                    mStrID = Constants.ApprovalStatus01;
                    approveOrder(mStrInstanceId, Constants.ApprovalStatus01, header.get(0).getOrderNo(), comments + "");
                } catch (Exception e) {
                    e.printStackTrace();
                    LogManager.writeLogError(Constants.error_txt + " : " + e.getMessage());
                }
                break;
        }
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, int operation, int requestCode, String resourcePath, String errorMsg, Bundle bundle) {
        try {
            LogManager.writeLogError(Constants.error_txt + " : " + "SO Approval Retry after 1st attempt failed");
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.writeLogError(Constants.error_txt + " : " + e.getMessage());
        }
    }

    @Override
    public void notesList(boolean status, ArrayList<SOTextBean> soTextBeanArrayList) {

    }

    private ViewGroup.MarginLayoutParams getLayoutParams(CardView cardView) {
        return (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabtoolbar:
//                if (Double.parseDouble(creditLimitBean.getBalanceAmount()) <= 0) {
//                    ConstantsUtils.displayLongToast(this, getString(R.string.credit_balance));
//                } else {
                fabToolbarContainer.show();
//                }
                break;
            case R.id.tvApprove:
                try {
                    if(!isClickable) {
                        isClickable=true;
                        approveCredit();
                        fabToolbarContainer.hide();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tvReject:
                try {
                    if(!isClickable) {
                        isClickable = true;
                        rejectCredit();
                        fabToolbarContainer.hide();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tvEditApprove:
                try {
                    if (ConstantsUtils.isAutomaticTimeZone(ApprovalListDetails.this)) {
                        Intent intent = new Intent(ApprovalListDetails.this, SOCreateActivity.class);
                        SOListBean soListBean = SOUtils.getAllSOBean(header, items, mStrInstanceId);
                        intent.putExtra(Constants.EXTRA_SO_HEADER, soListBean);
                        intent.putExtra(Constants.RetailerName, soListBean.getSoldToName());
                        intent.putExtra(Constants.CPNo, soListBean.getSoldTo());
                        intent.putExtra(Constants.CPUID, soListBean.getSoldTo());
                        intent.putExtra(Constants.CPGUID32, soListBean.getSoldTo());
                        intent.putExtra(Constants.EXTRA_COME_FROM, ConstantsUtils.SO_APPROVAL_EDIT_ACTIVITY);
                        intent.putExtra(Constants.comingFrom, ConstantsUtils.SO_APPROVAL_EDIT_ACTIVITY);
                        startActivity(intent);
                        DaySummaryPresenterImpl.isReloadSOApproval = true;
                    } else {
                        ConstantsUtils.showAutoDateSetDialog(ApprovalListDetails.this);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ivOrderDetails:
                try {
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
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ivPricingDetails:
                try {
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
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
        }
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
        try {
            ((SODetailsViewHolder) viewHolder).tvMaterialDesc.setText(salesOrderBean.getMatNoAndDesc());
            ((SODetailsViewHolder) viewHolder).tvQty.setText(salesOrderBean.getQAQty() + " " + salesOrderBean.getUom());
            ((SODetailsViewHolder) viewHolder).tvAmount.setText(ConstantsUtils.commaSeparator(salesOrderBean.getNetAmount(), salesOrderBean.getCurrency()) + " " + salesOrderBean.getCurrency());
            Drawable delvStatusImg;
            if (salesOrderBean.getDelvStatus().equals("")) {
                delvStatusImg = SOUtils.displayDelvStatusIcon("A", this);
                ((SODetailsViewHolder) viewHolder).ivDelvStatus.setImageDrawable(delvStatusImg);
            } else {
                delvStatusImg = SOUtils.displayDelvStatusIcon(salesOrderBean.getDelvStatus(), this);
                if (delvStatusImg != null) {
                    ((SODetailsViewHolder) viewHolder).ivDelvStatus.setImageDrawable(delvStatusImg);
                }
            }
            if(!salesOrderBean.getHighLevellItemNo().equalsIgnoreCase("000000")){
                      if (!TextUtils.isEmpty(salesOrderBean.getStatusID()) && salesOrderBean.getStatusID().equalsIgnoreCase("D")) {
                            delvStatusImg = SOUtils.displayDelvStatusIcon(salesOrderBean.getDelvStatus(), this);
                            if (delvStatusImg != null) {
                                ((SODetailsViewHolder) viewHolder).ivDelvStatus.setImageDrawable(delvStatusImg);
                            }
                        }else {
                          Drawable img = ContextCompat.getDrawable(ApprovalListDetails.this, R.drawable.ic_shopping_cart_black_24dp).mutate();
                          if (img != null)
                              img.setColorFilter(ContextCompat.getColor(ApprovalListDetails.this, R.color.dimgray), PorterDuff.Mode.SRC_IN);

                          ((SODetailsViewHolder) viewHolder).ivDelvStatus.setImageDrawable(img);

                      }

            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (!fabToolbarContainer.isFab())
            fabToolbarContainer.hide();
        else
            super.onBackPressed();
    }

    private void rejectCredit() {
        try {
            if (!TextUtils.isEmpty(mStrInstanceId)) {
                if (header!=null&&!header.isEmpty()) {
                    SOUtils.showCommentsDialog(ApprovalListDetails.this, new CustomDialogCallBack() {
                        @Override
                        public void cancelDialogCallBack(boolean userClicked, String ids, String description) {
                            if (userClicked) {
                                mStrID = Constants.RejectStatus;
                                approveOrder(mStrInstanceId, Constants.RejectStatus, header.get(0).getOrderNo(), description + "");
                            }else{
                                isClickable = false;
                            }
                        }
                    }, getString(R.string.reject_title_comments));
                }else{
                    isClickable = false;
                }
            }else{
                isClickable = false;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void approveCredit() {
        try {
            if (!TextUtils.isEmpty(mStrInstanceId)) {
                if (header != null && !header.isEmpty()) {
                    SOUtils.showCommentsDialog(ApprovalListDetails.this, new CustomDialogCallBack() {
                        @Override
                        public void cancelDialogCallBack(boolean userClicked, String ids, String description) {
                            if (userClicked) {
                                mStrID = Constants.ApprovalStatus01;
                                approveOrder(mStrInstanceId, Constants.ApprovalStatus01, header.get(0).getOrderNo(), description + "");
                            }else{
                                isClickable = false;
                            }
                        }
                    }, getString(R.string.approve_title_comments));
                }else{
                    isClickable = false;

                }
            }else{
                isClickable = false;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
