package com.rspl.sf.msfa.returnOrder.returnDetail;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.returnOrder.ReturnOrderBean;
import com.rspl.sf.msfa.so.SOUtils;

/**
 * Created by e10526 on 13-03-2018.
 */

public class ROItemDetailsActivity extends AppCompatActivity implements RODetailsView , View.OnClickListener, AdapterInterface<ReturnOrderItemBean> {

    private TextView tvReturnNo,tvOrderType;
    private Context context;
    private ReturnOrderItemBean roItemListBean = null;
    private RODetailsPresenterImpl presenter;

    private NestedScrollView nestedScroll;

    private ImageView ivExpandIcon;
    private LinearLayout llHeader;
    private boolean isSessionRequired;
    private Toolbar toolbar;
    private Context mContext;
    private String soNumber = "";
    private TextView tvDate;
    private CardView cvOrderDetails;
    private LinearLayout llInvQty;
    LinearLayout llOrderQty,llBatchNo,llMFD,llExpDate,llMRP,ll_unit_price,ll_net_amt,
            llPrimaryDisAmt,llPrimaryDis,llTaxOneAmt,llTaxTwoAmt,llTaxThreeAmt,
            llTaxOnePer,llTaxTwoPer,llTaxThreePer,llPlant;
    TextView tvInvQtyVal,tvOrderQtyVal,tvBatchNoVal,tvMFDVal,tvExpDateVal,tvMRPVal,tvUnitPriceVal,
            tvNetAmtVal,tvPrimaryDisAmtVal,tvPrimaryDiscountVal,tvTaxOneAmtVal,tvTaxTwoAmtVal,
            tvTaxThreeAmtVal,tvTaxOnePerVal,tvTaxTwoPerVal,tvTaxThreePerVal,tvPlantDesc;
    private ImageView ivDeliveryStatus;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ro_item_details_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Bundle bundle = getIntent().getExtras();
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.return_detail), 0);
        if (bundle != null) {
            roItemListBean = (ReturnOrderItemBean) bundle.getSerializable(Constants.EXTRA_SO_HEADER);
            isSessionRequired = bundle.getBoolean(Constants.EXTRA_SESSION_REQUIRED, false);
        }

        if (roItemListBean == null) {
            roItemListBean = new ReturnOrderItemBean();
        }
        initUI();
    }

    private void initUI(){
        mContext = ROItemDetailsActivity.this;
        nestedScroll = (NestedScrollView) findViewById(R.id.nestedScroll);
        tvReturnNo= (TextView) findViewById(R.id.tvReturnNo);
        tvOrderType= (TextView) findViewById(R.id.tvOrderType);
        ivDeliveryStatus = (ImageView) findViewById(R.id.ivDeliveryStatus);


        tvDate = (TextView) findViewById(R.id.tvDate);
        cvOrderDetails = (CardView) findViewById(R.id.cvOrderDetails);

        ivExpandIcon = (ImageView) findViewById(R.id.ivOrderDetails);
        llHeader = (LinearLayout) findViewById(R.id.headerItem);
        ivExpandIcon.setOnClickListener(this);

        llInvQty = (LinearLayout) findViewById(R.id.llInvQty);
        llOrderQty = (LinearLayout) findViewById(R.id.llOrderQty);
        llBatchNo = (LinearLayout) findViewById(R.id.llBatchNo);
        llMFD = (LinearLayout) findViewById(R.id.llMFD);
        llExpDate = (LinearLayout) findViewById(R.id.llExpDate);
        llMRP = (LinearLayout) findViewById(R.id.llMRP);
        ll_unit_price = (LinearLayout) findViewById(R.id.ll_unit_price);
        ll_net_amt = (LinearLayout) findViewById(R.id.ll_net_amt);
        llPrimaryDisAmt = (LinearLayout) findViewById(R.id.llPrimaryDisAmt);
        llPrimaryDis = (LinearLayout) findViewById(R.id.llPrimaryDis);
        llTaxOneAmt = (LinearLayout) findViewById(R.id.llTaxOneAmt);
        llTaxTwoAmt = (LinearLayout) findViewById(R.id.llTaxTwoAmt);
        llTaxThreeAmt = (LinearLayout) findViewById(R.id.llTaxThreeAmt);
        llTaxOnePer = (LinearLayout) findViewById(R.id.llTaxOnePer);
        llTaxTwoPer = (LinearLayout) findViewById(R.id.llTaxTwoPer);
        llTaxThreePer = (LinearLayout) findViewById(R.id.llTaxThreePer);
        llPlant = (LinearLayout) findViewById(R.id.llPlant);

        tvInvQtyVal = (TextView) findViewById(R.id.tvInvQtyVal);
        tvOrderQtyVal = (TextView) findViewById(R.id.tvOrderQtyVal);
        tvBatchNoVal = (TextView) findViewById(R.id.tvBatchNoVal);
        tvMFDVal = (TextView) findViewById(R.id.tvMFDVal);
        tvExpDateVal = (TextView) findViewById(R.id.tvExpDateVal);
        tvMRPVal = (TextView) findViewById(R.id.tvMRPVal);
        tvUnitPriceVal = (TextView) findViewById(R.id.tvUnitPriceVal);
        tvNetAmtVal = (TextView) findViewById(R.id.tvNetAmtVal);
        tvPrimaryDisAmtVal = (TextView) findViewById(R.id.tvPrimaryDisAmtVal);
        tvPrimaryDiscountVal = (TextView) findViewById(R.id.tvPrimaryDiscountVal);
        tvTaxOneAmtVal = (TextView) findViewById(R.id.tvTaxOneAmtVal);
        tvTaxTwoAmtVal = (TextView) findViewById(R.id.tvTaxTwoAmtVal);
        tvTaxThreeAmtVal = (TextView) findViewById(R.id.tvTaxThreeAmtVal);
        tvTaxOnePerVal = (TextView) findViewById(R.id.tvTaxOnePerVal);
        tvTaxTwoPerVal = (TextView) findViewById(R.id.tvTaxTwoPerVal);
        tvTaxThreePerVal = (TextView) findViewById(R.id.tvTaxThreePerVal);
        tvPlantDesc = (TextView) findViewById(R.id.tvPlantDesc);


        context = ROItemDetailsActivity.this;
        presenter = new RODetailsPresenterImpl(ROItemDetailsActivity.this, this, 1, roItemListBean, isSessionRequired);
        setUI();
//        displayHeaderList(roListBean);
    }
    private void setUI(){
        tvDate.setText(roItemListBean.getOrderDate());
        tvReturnNo.setText(roItemListBean.getRetOrdNo()+" / "+roItemListBean.getItemNo());
        tvOrderType.setText(roItemListBean.getROMaterialDescAndNo());

        if (!TextUtils.isEmpty(roItemListBean.getInvoiceQty())) {
            tvInvQtyVal.setText(roItemListBean.getInvoiceQty()+ " " + roItemListBean.getReferenceUOM());
            llInvQty.setVisibility(View.VISIBLE);
        } else {
            llInvQty.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(roItemListBean.getQuantity())) {
            tvOrderQtyVal.setText(roItemListBean.getQuantity()+ " " + roItemListBean.getUOM());
            llOrderQty.setVisibility(View.VISIBLE);
        } else {
            llOrderQty.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(roItemListBean.getBatch())) {
            tvBatchNoVal.setText(roItemListBean.getBatch());
            llBatchNo.setVisibility(View.VISIBLE);
        } else {
            llBatchNo.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(roItemListBean.getMFD())) {
            tvMFDVal.setText(roItemListBean.getMFD());
            llMFD.setVisibility(View.VISIBLE);
        } else {
            llMFD.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(roItemListBean.getExpiryDate())) {
            tvExpDateVal.setText(roItemListBean.getExpiryDate());
            llExpDate.setVisibility(View.VISIBLE);
        } else {
            llExpDate.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(roItemListBean.getMRP())) {
            tvMRPVal.setText(UtilConstants.commaSeparator(UtilConstants.removeLeadingZero(roItemListBean.getMRP())) + " " + roItemListBean.getCurrency());
            llMRP.setVisibility(View.VISIBLE);
        } else {
            llMRP.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(roItemListBean.getUnitPrice())) {
            tvUnitPriceVal.setText(UtilConstants.commaSeparator(UtilConstants.removeLeadingZero(roItemListBean.getUnitPrice())) + " " + roItemListBean.getCurrency());
            ll_unit_price.setVisibility(View.VISIBLE);
        } else {
            ll_unit_price.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(roItemListBean.getNetAmount())) {
            tvNetAmtVal.setText(UtilConstants.commaSeparator(UtilConstants.removeLeadingZero(roItemListBean.getNetAmount())) + " " + roItemListBean.getCurrency());
            ll_net_amt.setVisibility(View.VISIBLE);
        } else {
            ll_net_amt.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(roItemListBean.getPriDiscAmt())) {
            tvPrimaryDisAmtVal.setText(UtilConstants.commaSeparator(UtilConstants.removeLeadingZero(roItemListBean.getPriDiscAmt())) + " " + roItemListBean.getCurrency());
            llPrimaryDisAmt.setVisibility(View.VISIBLE);
        } else {
            llPrimaryDisAmt.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(roItemListBean.getPriDiscPerc())) {
            tvPrimaryDiscountVal.setText(roItemListBean.getPriDiscPerc());
            llPrimaryDis.setVisibility(View.VISIBLE);
        } else {
            llPrimaryDis.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(roItemListBean.getTax1Amt())) {
            tvTaxOneAmtVal.setText(UtilConstants.commaSeparator(UtilConstants.removeLeadingZero(roItemListBean.getTax1Amt())) + " " + roItemListBean.getCurrency());
            llTaxOneAmt.setVisibility(View.VISIBLE);
        } else {
            llTaxOneAmt.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(roItemListBean.getTax2Amt())) {
            tvTaxTwoAmtVal.setText(UtilConstants.commaSeparator(UtilConstants.removeLeadingZero(roItemListBean.getTax2Amt())) + " " + roItemListBean.getCurrency());
            llTaxTwoAmt.setVisibility(View.VISIBLE);
        } else {
            llTaxTwoAmt.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(roItemListBean.getTax3Amt())) {
            tvTaxThreeAmtVal.setText(UtilConstants.commaSeparator(UtilConstants.removeLeadingZero(roItemListBean.getTax3Amt())) + " " + roItemListBean.getCurrency());
            llTaxThreeAmt.setVisibility(View.VISIBLE);
        } else {
            llTaxThreeAmt.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(roItemListBean.getTax1Percent())) {
            tvTaxOnePerVal.setText(roItemListBean.getTax1Percent());
            llTaxOnePer.setVisibility(View.VISIBLE);
        } else {
            llTaxOnePer.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(roItemListBean.getTax2Percent())) {
            tvTaxTwoPerVal.setText(roItemListBean.getTax2Percent());
            llTaxTwoPer.setVisibility(View.VISIBLE);
        } else {
            llTaxTwoPer.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(roItemListBean.getTax3Percent())) {
            tvTaxThreePerVal.setText(roItemListBean.getTax3Percent());
            llTaxThreePer.setVisibility(View.VISIBLE);
        } else {
            llTaxThreePer.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(roItemListBean.getPlantDesc())) {
            tvPlantDesc.setText(getString(R.string.po_details_display_value, roItemListBean.getPlantDesc(), roItemListBean.getPlant()));
            llPlant.setVisibility(View.VISIBLE);
        } else {
            llPlant.setVisibility(View.GONE);
        }

        Drawable img = SOUtils.displayReturnOrderStatusImage(roItemListBean.getStatusID(), roItemListBean.getGRStatusID(), this);
        ivDeliveryStatus.setImageDrawable(img);
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
    public void displayHeaderList(ReturnOrderBean soListBean) {


        setUI();
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
            case R.id.ivOrderDetails:
                if (llHeader.getVisibility() == View.VISIBLE) {
                    ivExpandIcon.setImageResource(R.drawable.ic_arrow_down_black_24dp);
                    llHeader.setVisibility(View.GONE);
                    tvDate.setVisibility(View.VISIBLE);
                    ViewGroup.MarginLayoutParams layoutParams = ConstantsUtils.getLayoutParams(cvOrderDetails);
                    int marginB = ConstantsUtils.dpToPx(8, mContext);
                    /*if (llSOCondition.getVisibility() == View.VISIBLE) {
                        if (ConstantsUtils.getLayoutParams(cvPricingDetails).topMargin != 0) {
                            marginB = 0;
                        }
                    } else {
                        marginB = 0;
                        ViewGroup.MarginLayoutParams layoutParamss = ConstantsUtils.getLayoutParams(cvPricingDetails);
                        layoutParamss.setMargins(ConstantsUtils.dpToPx(8, mContext), 0, ConstantsUtils.dpToPx(8, mContext), layoutParamss.bottomMargin);
                        cvPricingDetails.requestLayout();
                    }*/
                    layoutParams.setMargins(ConstantsUtils.dpToPx(8, mContext), ConstantsUtils.dpToPx(8, mContext), ConstantsUtils.dpToPx(8, mContext), marginB);
                    cvOrderDetails.requestLayout();
                } else {
                    ivExpandIcon.setImageResource(R.drawable.ic_arrow_up_black_24dp);
                    llHeader.setVisibility(View.VISIBLE);
                    tvDate.setVisibility(View.GONE);
                    int marginB = ConstantsUtils.dpToPx(8, mContext);
//                    if (llSOCondition.getVisibility() == View.VISIBLE) {
//                        if (ConstantsUtils.getLayoutParams(cvPricingDetails).topMargin != 0) {
//                            marginB = 0;
//                        }
//                    }
                    ViewGroup.MarginLayoutParams layoutParams = ConstantsUtils.getLayoutParams(cvOrderDetails);
                    layoutParams.setMargins(ConstantsUtils.dpToPx(8, mContext), ConstantsUtils.dpToPx(8, mContext), ConstantsUtils.dpToPx(8, mContext), marginB);

                    cvOrderDetails.requestLayout();

                }
                break;

        }
    }


    @Override
    public void onItemClick(ReturnOrderItemBean returnOrderItemBean, View view, int i) {
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View viewItem) {
        return new ReturnOrderItemVH(viewItem, ROItemDetailsActivity.this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, ReturnOrderItemBean returnOrderItemBean) {
    }

}
