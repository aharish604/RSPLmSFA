package com.rspl.sf.msfa.reports.invoicelist.invoiceDetails;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.reports.invoicelist.InvoiceListBean;
import com.rspl.sf.msfa.reports.invoicelist.SOItemDetailsVH1;
import com.rspl.sf.msfa.so.SOUtils;

import java.util.ArrayList;

public class InvoiceDetailsActivity extends AppCompatActivity implements InvoiceDetailsView, View.OnClickListener, AdapterInterface<InvoiceItemBean> {

//    TextView tvAmountCurr, tvConditionCurr;
    private InvoiceListBean invoiceListBean = null;
    private int comingFrom = 0;
    private InvoiceDetailsPresenterImpl presenter;
//    private ImageView ivExpandIcon;
//    private LinearLayout llHeader;
    private boolean isExpand = false;
//    private RecyclerView recyPartnerFun;
    private TextView tvCustomerName, tvCustomerId;
    private SimpleRecyclerViewAdapter<InvoiceItemBean> invoiceItemAdapter;
//    private SimpleRecyclerViewAdapter<InvoicePartnerFunctionsBean> invoicePartFunAdapter;
    private TextView tvSoNumber;
    private TextView tvSODate;
    private TextView tvSOAmount;
    private TextView tvSOAmtHint;
    private AppCompatTextView tvNoRecordFoundPartnerFun;
    private AppCompatTextView tvNoRecordFoundItemDetails;
//    private AppCompatTextView tvNoRecordFoundConditionItem;
    private ProgressDialog progressDialog;
    private boolean isSessionRequired;
    private Toolbar toolbar;
    private TextView tvInvoiceNo, tvInvoiceCutName;
    private TextView tvOrderDetails, tvDate, tvIncoterm1Desc;
    private TextView tvOrderDateDesc, tvRefDoc, tvRefDocDesc, tvPaytermDesc;
    private ImageView ivOrderDetails;
    private LinearLayout llHeaderSale;
    private InvoiceDetailsActivity mContext;
    private CardView cvOrderDetails, cvPricingDetails, cvItem;
    private LinearLayout llSOCondition;
    private TextView tvAmount;
    private ImageView ivPricingDetails;
    private LinearLayout llItemList;
    private RecyclerView recycler_view_data;
    private ImageView ivDeliveryStatus;
    private TextView tvBillTo, tvBillToDesc;
    private LinearLayout llBillTo;
    private NestedScrollView nestedScroll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_history_details);
        Intent intent = getIntent();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (intent != null) {
            comingFrom = intent.getIntExtra(Constants.EXTRA_COME_FROM, 0);
            invoiceListBean = (InvoiceListBean) intent.getSerializableExtra(Constants.INVOICE_ITEM);
            isSessionRequired = intent.getBooleanExtra(Constants.EXTRA_SESSION_REQUIRED, false);
//            if (getIntent().getStringExtra(Constants.EXTRA_INVOICE_STATUS).equals(getString(R.string.invc_details_title))) {
                ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.invc_details_title), 0);
//            } else {
//                ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.outstd_details_title), 0);
//            }
        }

        if (invoiceListBean == null) {
            invoiceListBean = new InvoiceListBean();
        }
        mContext = InvoiceDetailsActivity.this;
        presenter = new InvoiceDetailsPresenterImpl(InvoiceDetailsActivity.this, this, comingFrom, invoiceListBean, isSessionRequired);
        initUI();
//        presenter.onStart();
    }

    private void initUI() {
        /*init header */

        nestedScroll = (NestedScrollView) findViewById(R.id.nestedScroll);
        tvCustomerName = (TextView) findViewById(R.id.tv_header_title);
        tvCustomerId = (TextView) findViewById(R.id.tv_header_id);
        tvInvoiceCutName = (TextView) findViewById(R.id.tvCustomerName);
        tvInvoiceNo = (TextView) findViewById(R.id.tvInvoiceNo);
//        ivExpandIcon = (ImageView) findViewById(R.id.iv_expand_icon);
//        llHeader = (LinearLayout) findViewById(R.id.header_item);

        tvSoNumber = (TextView) findViewById(R.id.tv_d_so_number);
        tvSODate = (TextView) findViewById(R.id.tv_d_so_date);
        tvSOAmount = (TextView) findViewById(R.id.tv_d_so_amount);
        tvSOAmtHint = (TextView) findViewById(R.id.tv_d_so_dsc_hint);
        tvSOAmount.setVisibility(View.VISIBLE);
        tvSODate.setVisibility(View.VISIBLE);

//        ivExpandIcon.setOnClickListener(this);

        tvDate = (TextView) findViewById(R.id.tvDate);
        tvOrderDetails = (TextView) findViewById(R.id.tvOrderDetails);
        tvRefDocDesc = (TextView) findViewById(R.id.tvRefDocDesc);
        tvRefDoc = (TextView) findViewById(R.id.tvRefDoc);
        tvOrderDateDesc = (TextView) findViewById(R.id.tvOrderDateDesc);
        tvIncoterm1Desc = (TextView) findViewById(R.id.tvIncoterm1Desc);
        tvPaytermDesc = (TextView) findViewById(R.id.tvPaytermDesc);
        ivOrderDetails = (ImageView) findViewById(R.id.ivOrderDetails);
        ivOrderDetails.setOnClickListener(this);
        llHeaderSale = (LinearLayout) findViewById(R.id.headerItem);
        cvOrderDetails = (CardView) findViewById(R.id.cvOrderDetails);
        llSOCondition = (LinearLayout) findViewById(R.id.llSOCondition);
        cvPricingDetails = (CardView) findViewById(R.id.cvPricingDetails);
        cvItem = (CardView) findViewById(R.id.cvItem);
        tvAmount = (TextView) findViewById(R.id.tvAmount);
        ivPricingDetails = (ImageView) findViewById(R.id.ivPricingDetails);
        ivPricingDetails.setOnClickListener(this);
        llItemList = (LinearLayout) findViewById(R.id.llItemList);
        recycler_view_data = (RecyclerView) findViewById(R.id.recycler_view_data);
        llBillTo = (LinearLayout) findViewById(R.id.llBillTo);
        tvBillTo = (TextView) findViewById(R.id.tvBillTo);
        tvBillToDesc = (TextView) findViewById(R.id.tvBillToDesc);
        /*item start*/
        View soItemTitelView = findViewById(R.id.soItemTitelView);
        TextView tvItemTitle = (TextView) soItemTitelView.findViewById(R.id.tv_heading);
        tvItemTitle.setText(getString(R.string.item_details_title));
//        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        View noRecordItemDetails = findViewById(R.id.noRecordItemDetails);
        tvNoRecordFoundItemDetails = ((AppCompatTextView) noRecordItemDetails);
        View noRecordPartnerFun = findViewById(R.id.noRecordPartnerFun);
        tvNoRecordFoundPartnerFun = ((AppCompatTextView) noRecordPartnerFun);
//        View noRecordConditionItm = findViewById(R.id.noRecordConditionItm);
//        tvNoRecordFoundConditionItem = ((AppCompatTextView) noRecordConditionItm);
        recycler_view_data.setHasFixedSize(true);
//        recycler_view_data.setNestedScrollingEnabled(false);
        ViewCompat.setNestedScrollingEnabled(recycler_view_data, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_view_data.setLayoutManager(linearLayoutManager);
        invoiceItemAdapter = new SimpleRecyclerViewAdapter<InvoiceItemBean>(InvoiceDetailsActivity.this, R.layout.so_item_material, this, recycler_view_data, tvNoRecordFoundItemDetails);
        recycler_view_data.setAdapter(invoiceItemAdapter);
        ivDeliveryStatus = (ImageView) findViewById(R.id.ivDeliveryStatus);

        /*item end*/

//        View soPatFunTitelView = findViewById(R.id.soPatFunTitelView);
//        TextView tvPartFunTitle = (TextView) soPatFunTitelView.findViewById(R.id.tv_heading);
//        tvPartFunTitle.setText(getString(R.string.inv_partn_title));

//        recyPartnerFun = (RecyclerView) findViewById(R.id.recy_partfun);
//        recyPartnerFun.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
//        recyPartnerFun.setLayoutManager(linearLayoutManager2);


//        View invConTitelView = findViewById(R.id.invConTitelView);
//        TextView tvinvConTitelView = (TextView) invConTitelView.findViewById(R.id.tv_heading);
//        tvinvConTitelView.setText(getString(R.string.inv_cond_title));
//        linvConItem = (LinearLayout) findViewById(R.id.invCon_item);

//        TextView tvDescription = (TextView) findViewById(R.id.tv_description);
//        TextView tvAmount = (TextView) findViewById(R.id.tv_amount);
//        TextView tvTotalAnount = (TextView) findViewById(R.id.tv_total_anount);

//        tvAmountCurr = (TextView) findViewById(R.id.tv_percentage);
//        tvConditionCurr = (TextView) findViewById(R.id.tv_total_amt_curr);

//        tvDescription.setText(getString(R.string.cond_name));
        tvAmount.setText(getString(R.string.cond_amt));
//        tvTotalAnount.setText(getString(R.string.cond_value));


        /*invoicePartFunAdapter = new SimpleRecyclerViewAdapter<InvoicePartnerFunctionsBean>(InvoiceDetailsActivity.this, R.layout.inv_part_functn, new AdapterInterface() {
            @Override
            public void onItemClick(Object o, View view, int i) {

            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
                return new PartnerFunctionViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, Object o) {
                InvoicePartnerFunctionsBean invoicePartnerFunctionsBean = (InvoicePartnerFunctionsBean) o;
                ((PartnerFunctionViewHolder) viewHolder).tvcustomername.setText(invoicePartnerFunctionsBean.getCustomerName());
                ((PartnerFunctionViewHolder) viewHolder).tvpartnerfunctionID.setText(invoicePartnerFunctionsBean.getPartnerFunctionDesc());
                ((PartnerFunctionViewHolder) viewHolder).tvVenderNo.setText(invoicePartnerFunctionsBean.getVendorNo());
                ((PartnerFunctionViewHolder) viewHolder).tvPersNo.setText(invoicePartnerFunctionsBean.getPersonnelNo());
            }
        }, recyPartnerFun, tvNoRecordFoundPartnerFun);
        recyPartnerFun.setAdapter(invoicePartFunAdapter);*/
        //displayHeaderList(invoiceListBean);
        setViewValue();
    }

    private void setViewValue() {
        tvInvoiceCutName.setText(invoiceListBean.getInvoiceTypDesc() + " (" + invoiceListBean.getInvoiceType() + ")");
        tvInvoiceNo.setText(invoiceListBean.getInvoiceNo());
        tvAmount.setText(ConstantsUtils.commaSeparator(invoiceListBean.getNetAmount(),invoiceListBean.getCurrency()) + " " + invoiceListBean.getCurrency());
        tvOrderDateDesc.setText(invoiceListBean.getInvoiceDate());
        tvDate.setText(invoiceListBean.getInvoiceDate());
        tvRefDoc.setText("Invoice Type");
        tvRefDocDesc.setText(invoiceListBean.getInvoiceTypDesc() + " - " + invoiceListBean.getInvoiceType());
        tvIncoterm1Desc.setText(invoiceListBean.getIncoterm1Desc() + " " + invoiceListBean.getIncoTerm2());
        tvPaytermDesc.setText(invoiceListBean.getPaymentTermDesc());
        if (!invoiceListBean.getInvConditionItemDetaiBeanArrayList().isEmpty())
            OnInvCondDetails(llSOCondition, invoiceListBean);

        Drawable img = SOUtils.displayInvoiceStatusImage(invoiceListBean.getInvoiceStatus(), invoiceListBean.getDueDateStatus(), this);
        ivDeliveryStatus.setImageDrawable(img);

        invoiceItemAdapter.refreshAdapter(invoiceListBean.getInvoiceItemBeanArrayList());

        if (!invoiceListBean.getInvoicePartnerFunctionsArrayList().isEmpty()) {
            for (InvoicePartnerFunctionsBean customerPartnerFunctionBean : invoiceListBean.getInvoicePartnerFunctionsArrayList()) {
                if (customerPartnerFunctionBean.getPartnerFunctionID().equalsIgnoreCase(Constants.RE)) {
                    tvBillTo.setText(getString(R.string.po_details_display_value, customerPartnerFunctionBean.getCustomerName(), customerPartnerFunctionBean.getCustomerNo()));
                    tvBillToDesc.setText(SOUtils.getAddressValueInvoice(customerPartnerFunctionBean));
                    llBillTo.setVisibility(View.VISIBLE);
                }
            }
        }
//        ConstantsUtils.focusOnView(nestedScroll);
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
            case R.id.menu_print:
                presenter.pdfDownload();
                break;

        }
        return true;
    }

    @Override
    public void displayHeaderList(InvoiceListBean invoiceListBean) {
//        onHeaderDetails(llHeader, invoiceListBean);
        tvSoNumber.setText(invoiceListBean.getInvoiceNo());
        tvSODate.setText(invoiceListBean.getInvoiceDate());
        tvSOAmount.setText(ConstantsUtils.commaSeparator(invoiceListBean.getNetAmount(),invoiceListBean.getCurrency()) + " " + invoiceListBean.getCurrency());

        invoiceItemAdapter.refreshAdapter(invoiceListBean.getInvoiceItemBeanArrayList());
//        invoicePartFunAdapter.refreshAdapter(invoiceListBean.getInvoicePartnerFunctionsArrayList());
        tvSOAmtHint.setVisibility(View.VISIBLE);
        tvSOAmtHint.setText(getString(R.string.inv_amt) + " " + getString(R.string.colun));
//        OnInvCondDetails(linvConItem, invoiceListBean);
    }

    private void OnInvCondDetails(LinearLayout linvConItem, InvoiceListBean invoiceListBean) {
        ArrayList<InvoiceConditionsBean> invoiceConditionsArrayList = invoiceListBean.getInvoiceConditionsArrayList();


        int incCondSize = invoiceConditionsArrayList.size();
        if (incCondSize > 0) {
            TextView[] tvDescription = new TextView[incCondSize];
            TextView[] tvPercentage = new TextView[incCondSize];
            TextView[] tvConAmount = new TextView[incCondSize];
            TextView[] tvConVal = new TextView[incCondSize];

            TableLayout tableInvCond = (TableLayout) LayoutInflater.from(InvoiceDetailsActivity.this).inflate(R.layout.table_view, null);

//            if (invoiceConditionsArrayList.size() > 0) {
//                tvAmountCurr.setText("(" + invoiceConditionsArrayList.get(0).getCurrency() + ")");
//                tvConditionCurr.setText("(" + invoiceConditionsArrayList.get(0).getCurrency() + ")");
//            }
            for (int i = 0; i < invoiceConditionsArrayList.size(); i++) {
                if (invoiceConditionsArrayList.get(i).getName().length() > 0) {
                    LinearLayout rowScheduleItem = (LinearLayout) LayoutInflater.from(InvoiceDetailsActivity.this).inflate(R.layout.so_condition_item, null);
                    tvDescription[i] = (TextView) rowScheduleItem.findViewById(R.id.tvDescription);
                    //   tvConAmount[i] = (TextView) rowScheduleItem.findViewById(R.id.tv_amount);
                    tvConVal[i] = (TextView) rowScheduleItem.findViewById(R.id.tvTotalAmount);
                    tvDescription[i].setText(invoiceConditionsArrayList.get(i).getName());
                    //   tvConAmount[i].setText(invoiceConditionsArrayList.get(i).getConditionAmt());
                    tvConVal[i].setText(ConstantsUtils.commaSeparator(invoiceConditionsArrayList.get(i).getConditionValue(),invoiceConditionsArrayList.get(i).getCurrency()));
                    tableInvCond.addView(rowScheduleItem);
                }
            }
            LinearLayout totalDis = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.so_condition_total_item, null);
            TextView tvDescriptn = (TextView) totalDis.findViewById(R.id.tvDescription);
            //   TextView tvAmt = (TextView) totalDis.findViewById(R.id.tv_amount);
            TextView tvTotalAmt = (TextView) totalDis.findViewById(R.id.tvTotalAmount);
            tvDescriptn.setText(getString(R.string.inv_total));
            String sAmount = invoiceListBean.getInvConditionItemDetaiBeanArrayList().get(0).getConditionTotalAmt();
            String sTotalAmt = invoiceListBean.getInvConditionItemDetaiBeanArrayList().get(0).getConditionTotalValue();
            //  tvAmt.setText(sAmount);
            tvTotalAmt.setText(ConstantsUtils.commaSeparator(sTotalAmt,""));
            linvConItem.addView(tableInvCond);
            linvConItem.addView(totalDis);
//            tvNoRecordFoundConditionItem.setVisibility(View.GONE);
        } else {
//            tvNoRecordFoundConditionItem.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void showProgressDialog(String s) {
        progressDialog = ConstantsUtils.showProgressDialog(InvoiceDetailsActivity.this, s);
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showMessage(String message, final boolean isSimpleDialog) {
        UtilConstants.dialogBoxWithCallBack(InvoiceDetailsActivity.this, "", message, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (!isSimpleDialog) {
                    redirectActivity();
                }
            }
        });
    }

    private void redirectActivity() {
       /* MainActivity.isRefresh = true;
        Intent intent = new Intent(InvoiceDetailsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
           /* case R.id.iv_expand_icon:
                if (isExpand) {
                    isExpand = false;
                    ivExpandIcon.setImageResource(R.drawable.down);
                    llHeader.setVisibility(View.GONE);
                } else {
                    isExpand = true;
                    ivExpandIcon.setImageResource(R.drawable.up);
                    llHeader.setVisibility(View.VISIBLE);
                }
                break;*/
            case R.id.ivOrderDetails:
                if (llHeaderSale.getVisibility() == View.VISIBLE) {
                    ivOrderDetails.setImageResource(R.drawable.ic_arrow_down_black_24dp);
                    llHeaderSale.setVisibility(View.GONE);
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
                    ivOrderDetails.setImageResource(R.drawable.ic_arrow_up_black_24dp);
                    llHeaderSale.setVisibility(View.VISIBLE);
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
                    if (llHeaderSale.getVisibility() == View.VISIBLE) {
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
                    if (llHeaderSale.getVisibility() == View.VISIBLE) {
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

    /**
     * get header details list
     */
    private void onHeaderDetails(LinearLayout llHeaderItemList, InvoiceListBean headerBeanDetail) {

        try {
            llHeaderItemList.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvInvoiceCutName.setText(invoiceListBean.getCustomerName());
        tvInvoiceNo.setText(invoiceListBean.getInvoiceNo());


        LinearLayout table = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.item_qty_linearlayout, null);

        LinearLayout row = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.ll_item_qty_row_item, null);
        ((TextView) row.findViewById(R.id.item_lable)).setText(R.string.st_invoice_type);
        ((TextView) row.findViewById(R.id.item_blank)).setText(" :");
        ((TextView) row.findViewById(R.id.item_value)).setText(headerBeanDetail.getInvoiceTypDesc() + " - " + headerBeanDetail.getInvoiceType());
        table.addView(row);

        row = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.ll_item_qty_row_item, null);
        ((TextView) row.findViewById(R.id.item_lable)).setText(R.string.inv_inco_terms);
        ((TextView) row.findViewById(R.id.item_blank)).setText(" :");
        ((TextView) row.findViewById(R.id.item_value)).setText(headerBeanDetail.getIncoterm1Desc() + " " + headerBeanDetail.getIncoTerm2());
        table.addView(row);

        row = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.ll_item_qty_row_item, null);
        ((TextView) row.findViewById(R.id.item_lable)).setText(R.string.st_invoice_payment_terms);
        ((TextView) row.findViewById(R.id.item_blank)).setText(" :");
        ((TextView) row.findViewById(R.id.item_value)).setText(headerBeanDetail.getPaymentTermDesc());
        table.addView(row);

        llHeaderItemList.addView(table);

    }

    @Override
    public void onItemClick(InvoiceItemBean item, View view, int position) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, View viewItem) {
        return new SOItemDetailsVH1(viewItem, InvoiceDetailsActivity.this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, InvoiceItemBean invoiceItemBean) {
        Drawable img = SOUtils.displayInvoiceStatusImage(invoiceListBean.getInvoiceStatus(), invoiceListBean.getDueDateStatus(), this);
        ((SOItemDetailsVH1) holder).ivDelvStatus.setImageDrawable(img);
        ((SOItemDetailsVH1) holder).tvMaterialDesc.setText(invoiceItemBean.getInvoiceMaterialDescAndNo());
        try {
            ((SOItemDetailsVH1) holder).tvQty.setText(ConstantsUtils.checkNoUOMZero(invoiceItemBean.getUOM(),invoiceItemBean.getActualInvQty() )+ " " + invoiceItemBean.getUOM());
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        ((SOItemDetailsVH1) holder).tvAmount.setText(ConstantsUtils.commaSeparator(UtilConstants.removeLeadingZero(invoiceItemBean.getTotalAmount()),invoiceItemBean.getCurrency()) + " " + invoiceItemBean.getCurrency());

    }
}



