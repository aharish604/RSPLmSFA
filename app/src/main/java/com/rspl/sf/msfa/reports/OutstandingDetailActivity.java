package com.rspl.sf.msfa.reports;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.store.OfflineManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by e10604 on 27/4/2016.
 *
 */
public class OutstandingDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private String mStrBundleRetName = "",mStrBundleRetID="";
    private String mStrBundleInvoiceNo = "",mStrBundleCPGUID="",mStrDevCollAmount="";
    private String mStrBundleInvoiceGuid = "",mStrStatus="",mStrInvDate="",mStrInvAmount="",
            mStrBundleDeviceStatus="",mStrDeviceNo="",mStrPendingAmount="",
            mStrInvAmountCurr="",mCollectionAmount="";;
    ImageView iv_expand_icon;
    TextView tv_invoice_document_number;
    TextView tv_inv_date;
    private ArrayList<OutstandingBean> alOutstandingBean;
    TextView[] matDesc_ex, matCode_ex,netAmount_ex,invQty_ex;
    private LinearLayout llDetailLayout;
    boolean flag = true;
    int cursorLength = 0;
    TextView[] matDesc, matCode,netAmount,invQty,itemNo,taxAmount,totAmount;

    TextView tvBillPaid = null;
    TextView tvBalanceAmount = null;
    //new
    TextView tvBillValue = null;
    TextView tvBillOutDays = null;
    private String mStrBundleRetUID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
      //  ActionBarView.initActionBarView(this, true,getString(R.string.title_OutstandingBillDetails));

        setContentView(R.layout.activity_out_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_OutstandingBillDetails), 0);
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);

            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);

            mStrBundleCPGUID= bundleExtras.getString(Constants.CPGUID);
            mStrDevCollAmount= bundleExtras.getString(Constants.DevCollAmount);

            mStrBundleInvoiceNo = bundleExtras.getString(Constants.InvoiceNo);
            mStrBundleInvoiceGuid = bundleExtras.getString(Constants.InvoiceGUID);
            mStrStatus = bundleExtras.getString(Constants.InvoiceStatus);
            mStrInvDate = bundleExtras.getString(Constants.InvDate);
            mStrInvAmount = bundleExtras.getString(Constants.InvAmount);
            mStrInvAmountCurr = bundleExtras.getString(Constants.Currency);
            mCollectionAmount = bundleExtras.getString(Constants.CollectionAmount);
            mStrBundleDeviceStatus = bundleExtras.getString(Constants.DeviceStatus);
            mStrDeviceNo = bundleExtras.getString(Constants.DeviceNo);
        }
        initUI();

    }

    void initUI(){
        //new
        tvBillValue = (TextView)findViewById(R.id.tv_bill_val);
        tvBillOutDays = (TextView) findViewById(R.id.tv_bill_out_date);
        tvBillPaid = (TextView)findViewById(R.id.tv_bill_paid);

        tvBalanceAmount =(TextView)findViewById(R.id.tv_balance_amount);

        SimpleDateFormat sdf = new SimpleDateFormat(Constants.dtFormat_ddMMyyyywithslash);
        Date date = new Date();
        try {
            date = sdf.parse(mStrInvDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long timeDifferenceInMIliSecond = (new Date().getTime())-date.getTime();
        int billOutDays = (int) (timeDifferenceInMIliSecond / (1000*60*60*24));
        tvBillOutDays.setText(String.valueOf(billOutDays));

        tv_inv_date = (TextView) findViewById(R.id.tv_inv_date);

        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);

        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetUID);
        tv_inv_date.setText(mStrInvDate);

        mStrDevCollAmount = "0";
        mCollectionAmount = "0";

        try {
//            double doublePenAmt = Double.parseDouble(mStrInvAmount) -
//                    (Double.parseDouble(mCollectionAmount) +
//                            Double.parseDouble(mStrDevCollAmount)) ;

            double doublePenAmt = Double.parseDouble(mStrInvAmount);
            mStrPendingAmount = doublePenAmt+"";
        } catch (NumberFormatException e) {
            mStrPendingAmount = "0";
            e.printStackTrace();
        }
        tvBillValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(mStrInvAmount) +" "+mStrInvAmountCurr);
//        tvBillPaid.setText(UtilConstants.removeLeadingZerowithTwoDecimal((Double.parseDouble(mCollectionAmount)
//                +  Double.parseDouble(mStrDevCollAmount))+"") +" "+mStrInvAmountCurr);
        tvBillPaid.setText(UtilConstants.removeLeadingZerowithTwoDecimal((Double.parseDouble(mCollectionAmount)
                +  Double.parseDouble(mStrDevCollAmount))+"") +" "+mStrInvAmountCurr);
        tvBalanceAmount.setText(UtilConstants.removeLeadingZerowithTwoDecimal(mStrPendingAmount) +" "+mStrInvAmountCurr);



        tv_invoice_document_number= (TextView) findViewById(R.id.tv_invoice_document_number);
        tv_invoice_document_number.setText(mStrBundleInvoiceNo);

        getOutstandingDetails();
    }






    /*Gets Outstanding details for selected invoice*/
    private void getOutstandingDetails(){
        try {
            if(!mStrBundleDeviceStatus.equalsIgnoreCase(Constants.X)){
                alOutstandingBean = OfflineManager.getOutstandingDetails(""+ Constants.OutstandingInvoices+"('" + mStrBundleInvoiceNo + "')/"+ Constants.OutstandingInvoiceItemDetails+"");
            }else{
                String store=null;
                try {
                    store = ConstantsUtils.getFromDataVault(mStrDeviceNo,this);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject fetchJsonHeaderObject = new JSONObject(store);
                    ArrayList<HashMap<String, String>>  arrtable = new ArrayList<>();

                    String itemsString = fetchJsonHeaderObject.getString(Constants.ItemsText);


                    ArrayList<OutstandingBean> alInvoiceHisDetails = new ArrayList<OutstandingBean>();
                    OutstandingBean invoiceHisBean;
                    arrtable= UtilConstants.convertToArrayListMap(itemsString);
                    for (int i = 0; i < arrtable.size();i++) {
                        HashMap<String, String> singleRow = arrtable.get(i);

                        invoiceHisBean = new OutstandingBean();

                        invoiceHisBean.setUom(singleRow.get(Constants.UOM));
                        invoiceHisBean.setMatCode(singleRow.get(Constants.MatCode));
                        invoiceHisBean.setMatDesc(singleRow.get(Constants.MatDesc));
                        invoiceHisBean.setItemNo(""+(i+1));

                        invoiceHisBean.setInvoiceAmount(singleRow.get(Constants.NetAmount));
                        invoiceHisBean.setInvQty(singleRow.get(Constants.Qty));
                        alInvoiceHisDetails.add(invoiceHisBean);
                    }

                    if(alInvoiceHisDetails!=null && alInvoiceHisDetails.size()>0){
                        alOutstandingBean = alInvoiceHisDetails;
                    }

                }  catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            getDisplayedValues(alOutstandingBean);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }
    /*Display Invoices in list*/
    private void getDisplayedValues(final ArrayList<OutstandingBean> arrayList) {
        // TODO Auto-generated method stub
        if (!flag) {
            llDetailLayout.removeAllViews();
        }
        flag = false;
        llDetailLayout = (LinearLayout) findViewById(R.id.ll_invoice_detail_list);

        TableLayout tableHeading = (TableLayout) LayoutInflater.from(this)
                .inflate(R.layout.item_table, null);
        cursorLength = arrayList.size();
        matDesc = new TextView[cursorLength];
        matCode = new TextView[cursorLength];
        netAmount = new TextView[cursorLength];
        invQty = new TextView[cursorLength];
        itemNo = new TextView[cursorLength];

        matDesc_ex = new TextView[cursorLength];
        matCode_ex = new TextView[cursorLength];
        netAmount_ex = new TextView[cursorLength];
        taxAmount = new TextView[cursorLength];
        totAmount = new TextView[cursorLength];
        invQty_ex = new TextView[cursorLength];


        if (cursorLength > 0) {
            for (int i = 0; i < cursorLength; i++) {
                final OutstandingBean lvdbean = arrayList.get(i);
                final int selvalue = i;
                LinearLayout rowRelativeLayout = (LinearLayout) LayoutInflater
                        .from(this).inflate(
                                R.layout.invoice_history_details_list, null);
                iv_expand_icon = (ImageView)rowRelativeLayout.findViewById(R.id.iv_expand_icon);
                itemNo[i] = (TextView) rowRelativeLayout.findViewById(R.id.tv_ivoice_details_item_no);

                matDesc[i] = (TextView) rowRelativeLayout
                        .findViewById(R.id.tv_invoice_details_mat_desc);
                matCode[i] = (TextView)rowRelativeLayout.findViewById(R.id.tv_invoice_details_mat_code);
                netAmount[i] = (TextView)rowRelativeLayout.findViewById(R.id.tv_invoice_details_net_amt);
                taxAmount[i] = (TextView)rowRelativeLayout.findViewById(R.id.tv_invoice_details_tax_amt_ex);
                totAmount[i] = (TextView)rowRelativeLayout.findViewById(R.id.tv_invoice_details_total_amt_ex);

                invQty[i] = (TextView)rowRelativeLayout.findViewById(R.id.tv_invoice_details_inv_qty);


                matDesc_ex[i] = (TextView)rowRelativeLayout
                        .findViewById(R.id.tv_invoice_details_mat_desc_ex);
                matCode_ex[i] = (TextView)rowRelativeLayout.findViewById(R.id.tv_invoice_details_mat_code_ex);
                netAmount_ex[i] = (TextView)rowRelativeLayout.findViewById(R.id.tv_invoice_details_net_amt_ex);
                invQty_ex[i] = (TextView)rowRelativeLayout.findViewById(R.id.tv_invoice_details_inv_qty_ex);


                itemNo[i].setText("" + UtilConstants.removeLeadingZeros(arrayList.get(i).getItemNo()));
                matCode[i].setText(""+arrayList.get(i).getMatCode());
                matDesc[i].setText(""+arrayList.get(i).getMatDesc());

                netAmount[i].setText(UtilConstants.removeLeadingZerowithTwoDecimal(arrayList.get(i).getInvoiceAmount())
                        +" "+arrayList.get(i).getCurrency());
                invQty[i].setText("" + UtilConstants.removeDecimalPoints(arrayList.get(i).getInvQty())+" "+arrayList.get(i).getUom());




                matDesc_ex[i].setText(""+arrayList.get(i).getMatDesc());
                matCode_ex[i].setText(""+arrayList.get(i).getMatCode());

                netAmount_ex[i].setText(UtilConstants.removeLeadingZerowithTwoDecimal(arrayList.get(i).getInvoiceAmount())
                        +" "+arrayList.get(i).getCurrency());
                invQty_ex[i].setText("" + UtilConstants.removeDecimalPoints(arrayList.get(i).getInvQty()) + " " + arrayList.get(i).getUom());

                taxAmount[i].setText(UtilConstants.removeLeadingZerowithTwoDecimal(arrayList.get(i).getTaxAmount())
                        +" "+arrayList.get(i).getCurrency());

                double total= 0.0;
                try {
                    total = Double.parseDouble(arrayList.get(i).getTaxAmount())+Double.parseDouble(arrayList.get(i).getInvoiceAmount());
                } catch (NumberFormatException e) {
                    total=0.0;
                    e.printStackTrace();
                }
                totAmount[i].setText(UtilConstants.removeLeadingZerowithTwoDecimal(total+"")
                        +" "+arrayList.get(i).getCurrency());


                final View testView = rowRelativeLayout;
                iv_expand_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageView img = (ImageView)v;
                        LinearLayout lt_last_line  =  (LinearLayout)testView.findViewById(R.id.lt_lastLine_ex);
                        LinearLayout ll_down_colour  =  (LinearLayout)testView.findViewById(R.id.ll_down_color);
                        Log.d(Constants.STATUS, arrayList.get(selvalue).getIsDetailEnabled() + "");

                        if(arrayList.get(selvalue).getIsDetailEnabled())
                        {
                            arrayList.get(selvalue).setIsDetailEnabled(false);
                            img.setImageResource(R.drawable.down);
                            lt_last_line.setVisibility(View.GONE);
                            ll_down_colour.setVisibility(View.GONE);
                        }
                        else
                        {
                            arrayList.get(selvalue).setIsDetailEnabled(true);
                            img.setImageResource(R.drawable.up);
                            lt_last_line.setVisibility(View.VISIBLE);

                            ll_down_colour.setVisibility(View.VISIBLE);
                        }

                    }
                });

                tableHeading.addView(rowRelativeLayout);
            }

            llDetailLayout.addView(tableHeading);

        }else{

            matDesc = new TextView[1];
            matCode = new TextView[1];
            netAmount = new TextView[1];
            invQty = new TextView[1];
            itemNo = new TextView[1];

            LinearLayout rowRelativeLayout = (LinearLayout) LayoutInflater
                    .from(this).inflate(
                            R.layout.invoice_history_details_list, null);

            itemNo[0] = (TextView)rowRelativeLayout.findViewById(R.id.tv_ivoice_details_item_no);

            matDesc[0] = (TextView)rowRelativeLayout
                    .findViewById(R.id.tv_invoice_details_mat_desc);
            matCode[0] = (TextView)rowRelativeLayout.findViewById(R.id.tv_invoice_details_mat_code);
            netAmount[0] = (TextView)rowRelativeLayout.findViewById(R.id.tv_invoice_details_net_amt);
            invQty[0] = (TextView)rowRelativeLayout.findViewById(R.id.tv_invoice_details_inv_qty);


            itemNo[0].setText("");
            matDesc[0].setText("");
            matCode[0].setText("");
            netAmount[0].setText(getString(R.string.str_rupee_symbol)+" 0");
            invQty[0].setText("0");

            tableHeading.addView(rowRelativeLayout);

            llDetailLayout.addView(tableHeading);
        }

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
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tv_back:
                onBackPressed();
                break;

        }
    }

}
