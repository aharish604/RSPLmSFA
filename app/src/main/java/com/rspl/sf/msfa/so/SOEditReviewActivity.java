package com.rspl.sf.msfa.so;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.google.gson.Gson;
import com.rspl.sf.msfa.CustomerDetailsActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.ActionBarView;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.CustomComparator;
import com.rspl.sf.msfa.common.MyUtils;
import com.rspl.sf.msfa.interfaces.DialogCallBack;
import com.rspl.sf.msfa.interfaces.StoreStatusInterface;
import com.rspl.sf.msfa.reports.NewSalesOrderActivity;
import com.rspl.sf.msfa.reports.SalesOrderTabActivity;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.store.OfflineManager;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import static com.arteriatech.mutils.common.UtilConstants.trimQtyDecimalPlace;

public class SOEditReviewActivity extends AppCompatActivity implements UIListener, StoreStatusInterface {
    private static final String TAG = "SOReviewActivity";
    ArrayList<HashMap<String, String>> listDetail;
    private String select_review[] = {"Header", "Item"};
    private Spinner spselect_review;
    private String selectedItem;
    private HashMap<String, String> headerDetail = null;
    private LinearLayout soReview;
    private SOItemBean singleItem;
    private SOItemBean saveItem = new SOItemBean();
    private String doc_no = (System.currentTimeMillis() + "").substring(3, 10),
            popUpText = "";
    private Hashtable<String, String> masterHeaderTable = new Hashtable<>();
    private ArrayList<HashMap<String, String>> itemTable = new ArrayList<>();
    private Set<String> set = new HashSet<>();
    private ProgressDialog progressDialog = null;
    private int penReqCount = 0;
    private int mIntPendingCollVal = 0;
    private String[] invKeyValues = null;

    private ArrayList<SOItemBean> soItemBeansArrayList;
    private boolean isSimulated = false;

    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    String mStrComingFrom = "";
    boolean isComingFromChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_review);
        //ActionBarView.initActionBarView(this, true, getString(R.string.title_sales_order_review));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_sales_order_review), 0);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            soItemBeansArrayList = (ArrayList<SOItemBean>) extra.getSerializable(Constants.EXTRA_SO_ITEM_LIST);
            listDetail = (ArrayList<HashMap<String, String>>) extra.getSerializable("list");
            headerDetail = (HashMap<String, String>) extra.getSerializable("Header");
            isSimulated = extra.getBoolean(Constants.EXTRA_Is_Simulated);
            mStrBundleRetID = extra.getString(Constants.CPNo);
            mStrBundleRetName = extra.getString(Constants.RetailerName);
            mStrComingFrom = extra.getString(Constants.comingFrom);
            isComingFromChange = extra.getBoolean(Constants.comingFromChange, false);
        }
        soReview = (LinearLayout) findViewById(R.id.llSOReviewItems);
        spselect_review = (Spinner) findViewById(R.id.spSelectReview);
        ArrayAdapter<String> select_review_adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, select_review);
        select_review_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spselect_review.setAdapter(select_review_adapter);
        spselect_review.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {
                selectedItem = select_review[position];
                updateUI();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        if (isComingFromChange)
            ActionBarView.initActionBarView(this, true, getString(R.string.title_sales_order_change));
    }

    /**
     * update ui based on condition
     */
    private void updateUI() {
        // TODO Auto-generated method stub
        if (selectedItem.equals("Header")) {
            onHeaderDetails();
        } else if (selectedItem.equals("Item")) {
            onItemDetails();
        }
    }

    /**
     * get item details
     */
    private void onItemDetails() {
        // TODO Auto-generated method stub
        soReview.removeAllViews();

        TableLayout table = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_qty_table, null);
        for (int i = 0; i < soItemBeansArrayList.size(); i++) {
            singleItem = soItemBeansArrayList.get(i);
            TableRow row0 = (TableRow) LayoutInflater.from(this).inflate(
                    R.layout.item_headding, null);
            ((TextView) row0.findViewById(R.id.item_heading)).setText("Item # "
                    + Constants.getItemNoInSixCharsWithPrefixZeros(singleItem.getItemNo()));
            table.addView(row0);

            TableRow row1 = (TableRow) LayoutInflater.from(this).inflate(
                    R.layout.item_qty_row, null);
            ((TextView) row1.findViewById(R.id.item_lable)).setText("Material");
            ((TextView) row1.findViewById(R.id.item_blank)).setText(" :");
            ((TextView) row1.findViewById(R.id.item_value)).setText(singleItem
                    .getMatCode());
            table.addView(row1);

            TableRow row2 = (TableRow) LayoutInflater.from(this).inflate(
                    R.layout.item_qty_row, null);
            ((TextView) row2.findViewById(R.id.item_lable))
                    .setText("Description");
            ((TextView) row2.findViewById(R.id.item_blank)).setText(" :");
            ((TextView) row2.findViewById(R.id.item_value)).setText(singleItem
                    .getMatDesc());
            table.addView(row2);

            TableRow row3 = (TableRow) LayoutInflater.from(this).inflate(
                    R.layout.item_qty_row, null);
            ((TextView) row3.findViewById(R.id.item_lable)).setText("Quantity");
            ((TextView) row3.findViewById(R.id.item_blank)).setText(" :");
            TextView tvQty = (TextView) row3.findViewById(R.id.item_value);
            try {
                if (OfflineManager.checkNoUOMZero(singleItem.getUom()))
                    tvQty.setText(trimQtyDecimalPlace(singleItem.getQuantity()) + " " + singleItem.getUom());
                else
                tvQty.setText(trimQtyDecimalPlace(singleItem.getQuantity()) + " " + singleItem.getUom());
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            tvQty.setGravity(Gravity.RIGHT);
            table.addView(row3);

            TableRow row4 = (TableRow) LayoutInflater.from(this).inflate(
                    R.layout.item_qty_row, null);
            ((TextView) row4.findViewById(R.id.item_lable)).setText("Unit Price");
            ((TextView) row4.findViewById(R.id.item_blank)).setText(" :");
            TextView tvUnitPrice = (TextView) row4.findViewById(R.id.item_value);
            tvUnitPrice.setText(UtilConstants.removeLeadingZero(singleItem
                    .getUnitPrice()) + " " + singleItem.getCurrency());
            tvUnitPrice.setGravity(Gravity.RIGHT);
            table.addView(row4);

            TableRow row5 = (TableRow) LayoutInflater.from(this).inflate(
                    R.layout.item_qty_row, null);
            ((TextView) row5.findViewById(R.id.item_lable)).setText("Net Amount");
            ((TextView) row5.findViewById(R.id.item_blank)).setText(" :");
            TextView tvNetAmt = (TextView) row5.findViewById(R.id.item_value);
            tvNetAmt.setText(UtilConstants.removeLeadingZero(singleItem.getNetAmount()) + " " + singleItem.getCurrency());
            tvNetAmt.setGravity(Gravity.RIGHT);
            table.addView(row5);

            TableRow row6 = (TableRow) LayoutInflater.from(this).inflate(
                    R.layout.item_qty_row, null);
            ((TextView) row6.findViewById(R.id.item_lable)).setText("Discount");
            ((TextView) row6.findViewById(R.id.item_blank)).setText(" :");
            TextView tvDiscount = (TextView) row6.findViewById(R.id.item_value);
            tvDiscount.setText(UtilConstants.removeLeadingZero(singleItem.getDiscount()) + " " + singleItem.getCurrency());
            tvDiscount.setGravity(Gravity.RIGHT);
            table.addView(row6);

            TableRow row8 = (TableRow) LayoutInflater.from(this).inflate(
                    R.layout.item_qty_row, null);
            ((TextView) row8.findViewById(R.id.item_lable)).setText("Tax");
            ((TextView) row8.findViewById(R.id.item_blank)).setText(" :");
            TextView tvTax = (TextView) row8.findViewById(R.id.item_value);
            tvTax.setText(UtilConstants.removeLeadingZero(singleItem.getTaxAmount()) + " " + singleItem.getCurrency());
            tvTax.setGravity(Gravity.RIGHT);
            table.addView(row8);

            TableRow row9 = (TableRow) LayoutInflater.from(this).inflate(
                    R.layout.item_qty_row, null);
            ((TextView) row9.findViewById(R.id.item_lable)).setText("Freight");
            ((TextView) row9.findViewById(R.id.item_blank)).setText(" :");
            TextView tvFreight = (TextView) row9.findViewById(R.id.item_value);
            tvFreight.setText(UtilConstants.removeLeadingZero(singleItem.getFreight()) + " " + singleItem.getCurrency());
            tvFreight.setGravity(Gravity.RIGHT);
            table.addView(row9);

            TableRow row7 = (TableRow) LayoutInflater.from(this).inflate(
                    R.layout.item_qty_row, null);
            ((TextView) row7.findViewById(R.id.item_lable)).setText("Total Amount");
            ((TextView) row7.findViewById(R.id.item_blank)).setText(" :");
            TextView tvTotalAmt = (TextView) row7.findViewById(R.id.item_value);
            try {
                double netAmount = Double.parseDouble(singleItem.getNetAmount());
                double taxAmount = Double.parseDouble(singleItem.getTaxAmount());
                double totalAmount = netAmount+taxAmount;
                DecimalFormat format = new DecimalFormat("0.00");
                String total = format.format(totalAmount);
                tvTotalAmt.setText(total+ " " + singleItem.getCurrency());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            tvTotalAmt.setGravity(Gravity.RIGHT);
            table.addView(row7);
            TableRow line = (TableRow) LayoutInflater.from(this).inflate(R.layout.line_row, null);
            table.addView(line);
        }
        soReview.addView(table);

    }

    /**
     * get header details list
     */
    private void onHeaderDetails() {
        // TODO Auto-generated method stub
        soReview.removeAllViews();
        TableLayout table = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_qty_table, null);
        TableRow row0 = (TableRow) LayoutInflater.from(this).inflate(
                R.layout.item_headding, null);
        ((TextView) row0.findViewById(R.id.item_heading)).setText("SalesOrder");
        table.addView(row0);

        TableRow row1 = (TableRow) LayoutInflater.from(this).inflate(
                R.layout.item_qty_row, null);
        ((TextView) row1.findViewById(R.id.item_lable)).setText("Sold To");
        ((TextView) row1.findViewById(R.id.item_blank)).setText(" :");
        TextView tvSoldTo = ((TextView) row1.findViewById(R.id.item_value));
        tvSoldTo.setText(headerDetail.get("SoldTo"));
        if (!headerDetail.get("SoldToName").equalsIgnoreCase(""))
            tvSoldTo.append(" - " + headerDetail.get("SoldToName"));
        table.addView(row1);

        TableRow row6 = (TableRow) LayoutInflater.from(this).inflate(
                R.layout.item_qty_row, null);
        ((TextView) row6.findViewById(R.id.item_lable)).setText("Sales Area");
        ((TextView) row6.findViewById(R.id.item_blank)).setText(" :");
        TextView tvSalesArea = ((TextView) row6.findViewById(R.id.item_value));
        tvSalesArea.setText(headerDetail.get("SalesArea"));
        if (!headerDetail.get("SalesAreaDesc").equalsIgnoreCase(""))
            tvSalesArea.append("-" + headerDetail.get("SalesAreaDesc"));
        table.addView(row6);

        TableRow row4 = (TableRow) LayoutInflater.from(this).inflate(
                R.layout.item_qty_row, null);
        ((TextView) row4.findViewById(R.id.item_lable)).setText("Plant");
        ((TextView) row4.findViewById(R.id.item_blank)).setText(" :");
        TextView tvPlant = ((TextView) row4.findViewById(R.id.item_value));
        tvPlant.setText(headerDetail.get("Plant"));
        if (!headerDetail.get("PlantDesc").equalsIgnoreCase(""))
            tvPlant.append("-" + headerDetail.get("PlantDesc"));
        table.addView(row4);

        TableRow row5 = (TableRow) LayoutInflater.from(this).inflate(
                R.layout.item_qty_row, null);
        ((TextView) row5.findViewById(R.id.item_lable))
                .setText("Shipping Point");
        ((TextView) row5.findViewById(R.id.item_blank)).setText(" :");
        TextView tvShipingPoint = ((TextView) row5.findViewById(R.id.item_value));
        tvShipingPoint.setText(headerDetail.get("ShippingPoint"));
        if (!headerDetail.get("ShippingPointDesc").equalsIgnoreCase(""))
            tvShipingPoint.append("-" + headerDetail.get("ShippingPointDesc"));
        table.addView(row5);

        TableRow row2 = (TableRow) LayoutInflater.from(this).inflate(
                R.layout.item_qty_row, null);
        ((TextView) row2.findViewById(R.id.item_lable)).setText("Ship To");
        ((TextView) row2.findViewById(R.id.item_blank)).setText(" :");
        TextView tvShipTo = ((TextView) row2.findViewById(R.id.item_value));
        tvShipTo.setText(headerDetail.get("ShipTo"));
        if (!headerDetail.get("ShipToName").equalsIgnoreCase("")&&!headerDetail.get("ShipToName").equalsIgnoreCase("null")
                &&headerDetail.get("ShipToName")!=null)
            tvShipTo.append("-" + headerDetail.get("ShipToName"));
        table.addView(row2);

        TableRow row3 = (TableRow) LayoutInflater.from(this).inflate(
                R.layout.item_qty_row, null);
        ((TextView) row3.findViewById(R.id.item_lable)).setText("Order type");
        ((TextView) row3.findViewById(R.id.item_blank)).setText(" :");
        TextView tvOrderType = ((TextView) row3.findViewById(R.id.item_value));
        tvOrderType.setText(headerDetail.get("OrderType"));
        if (!headerDetail.get("OrderTypeDesc").equalsIgnoreCase(""))
            tvOrderType.append("-" + headerDetail.get("OrderTypeDesc"));
        table.addView(row3);

        TableRow row12 = (TableRow) LayoutInflater.from(this).inflate(
                R.layout.item_qty_row, null);
        ((TextView) row12.findViewById(R.id.item_lable)).setText("Total Amount");
        ((TextView) row12.findViewById(R.id.item_blank)).setText(" :");
        TextView tvTotalAmt = ((TextView) row12.findViewById(R.id.item_value));
        tvTotalAmt.setText(headerDetail.get(Constants.TotalAmount) + " " +
                headerDetail.get(Constants.Currency));
        tvTotalAmt.setGravity(Gravity.RIGHT);
        table.addView(row12);

        TableRow row7 = (TableRow) LayoutInflater.from(this).inflate(
                R.layout.item_headding, null);
        ((TextView) row7.findViewById(R.id.item_heading))
                .setText("Purchase Order");
        table.addView(row7);

        TableRow poNumber = (TableRow) LayoutInflater.from(this).inflate(
                R.layout.item_qty_row, null);
        ((TextView) poNumber.findViewById(R.id.item_lable)).setText("PO #");
        ((TextView) poNumber.findViewById(R.id.item_blank)).setText(" :");
        ((TextView) poNumber.findViewById(R.id.item_value)).setText(headerDetail
                .get("PONo"));
        table.addView(poNumber);

        TableRow row8 = (TableRow) LayoutInflater.from(this).inflate(
                R.layout.item_qty_row, null);
        ((TextView) row8.findViewById(R.id.item_lable)).setText("PO Date");
        ((TextView) row8.findViewById(R.id.item_blank)).setText(" :");
        ((TextView) row8.findViewById(R.id.item_value)).setText(headerDetail
                .get("PODate1"));
        table.addView(row8);

        TableRow termsHeader = (TableRow) LayoutInflater.from(this).inflate(
                R.layout.item_headding, null);
        ((TextView) termsHeader.findViewById(R.id.item_heading))
                .setText("Terms");
        table.addView(termsHeader);

        TableRow row9 = (TableRow) LayoutInflater.from(this).inflate(
                R.layout.item_qty_row, null);
        ((TextView) row9.findViewById(R.id.item_lable)).setText("Payment Term");
        ((TextView) row9.findViewById(R.id.item_blank)).setText(" :");
        TextView tvPayTerm = ((TextView) row9.findViewById(R.id.item_value));
        tvPayTerm.setText(headerDetail.get("PaymentTerm"));
        if (!headerDetail.get(Constants.PaytermDesc).equalsIgnoreCase(""))
            tvPayTerm.append("-" + headerDetail.get(Constants.PaytermDesc));
        table.addView(row9);

        TableRow row10 = (TableRow) LayoutInflater.from(this).inflate(
                R.layout.item_qty_row, null);
        ((TextView) row10.findViewById(R.id.item_lable)).setText("Inco term");
        ((TextView) row10.findViewById(R.id.item_blank)).setText(" :");
        TextView tvIncoTerm = ((TextView) row10.findViewById(R.id.item_value));
        tvIncoTerm.setText(headerDetail.get("IncoTerm1"));
        if (!headerDetail.get(Constants.Incoterm1Desc).equalsIgnoreCase(""))
            tvIncoTerm.append("-" + headerDetail.get(Constants.Incoterm1Desc));
        table.addView(row10);

        TableRow rowIncoTerm1 = (TableRow) LayoutInflater.from(this).inflate(
                R.layout.item_qty_row, null);
        ((TextView) rowIncoTerm1.findViewById(R.id.item_lable)).setText("Inco term 1");
        ((TextView) rowIncoTerm1.findViewById(R.id.item_blank)).setText(" :");
        TextView tvIncoTerm1 = ((TextView) rowIncoTerm1.findViewById(R.id.item_value));
        tvIncoTerm1.setText(headerDetail.get("IncoTerm2"));
        table.addView(rowIncoTerm1);

        TableRow remarks_header = (TableRow) LayoutInflater.from(this).inflate(
                R.layout.item_headding, null);
        ((TextView) remarks_header.findViewById(R.id.item_heading)).setText("Comments");
        table.addView(remarks_header);
        TableRow row11 = (TableRow) LayoutInflater.from(this).inflate(
                R.layout.item_qty_row, null);
        ((TextView) row11.findViewById(R.id.item_lable)).setText("Comments");
        ((TextView) row11.findViewById(R.id.item_blank)).setText(" :");
        ((TextView) row11.findViewById(R.id.item_value)).setText(headerDetail.get("Remarks"));
        table.addView(row11);
        TableRow line = (TableRow) LayoutInflater.from(this).inflate(R.layout.line_row, null);
        table.addView(line);
        soReview.addView(table);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SOEditReviewActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_competition_information).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        SOEditReviewActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_back_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_save:
                //save
                onSave();
                break;

        }
        return true;
    }

    /**
     * save data
     */
    private void onSave() {
        if (isComingFromChange)
            doc_no = headerDetail.get(Constants.OrderNo);
        else
            doc_no = (System.currentTimeMillis() + "").substring(3, 10);
        masterHeaderTable.clear();
        if (headerDetail != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
            String loginIdVal = sharedPreferences.getString(Constants.username, "");

            masterHeaderTable.put(Constants.LOGINID, loginIdVal);
            masterHeaderTable.put(Constants.SONo, doc_no);
            masterHeaderTable.put(Constants.OrderType, headerDetail.get("OrderType"));
            masterHeaderTable.put(Constants.OrderTypeText, headerDetail.get("OrderTypeDesc"));
            if (isComingFromChange)
                masterHeaderTable.put(Constants.OrderDate, headerDetail.get(Constants.OrderDate));
            else
                masterHeaderTable.put(Constants.OrderDate, Constants.getNewDateTimeFormat());

             masterHeaderTable.put(Constants.CustomerNo, headerDetail.get("SoldTo"));
            masterHeaderTable.put(Constants.CustomerPO, headerDetail.get("PONo"));
            masterHeaderTable.put(Constants.CustomerPODate, headerDetail.get("PODate"));

            masterHeaderTable.put(Constants.ShipToParty, headerDetail.get("ShipTo"));
            masterHeaderTable.put(Constants.SalesArea, headerDetail.get("SalesArea"));
            masterHeaderTable.put(Constants.SalesOffice, "");
            masterHeaderTable.put(Constants.SalesGroup, "");
            masterHeaderTable.put(Constants.ShippingTypeID,headerDetail.get("ShippingPoint"));
           // masterHeaderTable.put(Constants.MeansOfTranstyp, "");
            masterHeaderTable.put(Constants.Plant, headerDetail.get("Plant"));
            masterHeaderTable.put(Constants.PlantDesc, headerDetail.get("PlantDesc"));
            masterHeaderTable.put(Constants.Incoterm1, headerDetail.get("IncoTerm1"));//headerDetail.get("IncoTerm1")
            masterHeaderTable.put(Constants.Incoterm1Desc, headerDetail.get(Constants.Incoterm1Desc));//headerDetail.get("IncoTerm1")
            masterHeaderTable.put(Constants.Incoterm2, headerDetail.get("IncoTerm2"));//headerDetail.get("IncoTerm1")
            masterHeaderTable.put(Constants.Payterm, headerDetail.get("PaymentTerm") != null ? headerDetail.get("PaymentTerm") : "");//headerDetail.get("PaymentTerm")
            masterHeaderTable.put(Constants.PaytermDesc, headerDetail.get(Constants.PaytermDesc) != null ? headerDetail.get(Constants.PaytermDesc) : "");//headerDetail.get("PaymentTerm")

//            masterHeaderTable.put(Constants.SalesDist, headerDetail.get(Constants.SalesDist));
//            masterHeaderTable.put(Constants.MeansOfTranstyp, headerDetail.get(Constants.MeansOfTranstyp));
            //masterHeaderTable.put(Constants.SplProcessing, headerDetail.get(Constants.SplProcessing));
           // masterHeaderTable.put(Constants.PriceList, headerDetail.get("PriceList"));
           // masterHeaderTable.put(Constants.Route, headerDetail.get(Constants.Route));


            //masterHeaderTable.put("StorageLoc", headerDetail.get("StorageLoc"));
            masterHeaderTable.put(Constants.Remarks, headerDetail.get("Remarks"));
            masterHeaderTable.put(Constants.StatusUpdate, "NEW");

            if (isSimulated) {
                masterHeaderTable.put(Constants.Currency, headerDetail.get(Constants.Currency));
                masterHeaderTable.put(Constants.NetPrice, headerDetail.get(Constants.NetPrice));
                masterHeaderTable.put(Constants.TotalAmount, headerDetail.get(Constants.TotalAmount));
                masterHeaderTable.put(Constants.TaxAmount, headerDetail.get(Constants.TaxAmount));
                masterHeaderTable.put(Constants.Freight, headerDetail.get(Constants.Freight));
                masterHeaderTable.put(Constants.Discount, headerDetail.get(Constants.Discount));
                masterHeaderTable.put(Constants.Testrun, "");
            } else {
                masterHeaderTable.put(Constants.Currency, "");
                masterHeaderTable.put(Constants.NetPrice, "0");
                masterHeaderTable.put(Constants.TotalAmount, "0");
                masterHeaderTable.put(Constants.TaxAmount, "0");
                masterHeaderTable.put(Constants.Freight, "0");
                masterHeaderTable.put(Constants.Discount, "0");
                masterHeaderTable.put(Constants.Testrun, "M");
            }

            if (isComingFromChange)
                masterHeaderTable.put(Constants.EntityType, Constants.SOUpdate);
            else
                masterHeaderTable.put(Constants.EntityType, Constants.SalesOrderDataValt);

            itemTable.clear();
            for (int itemIndex = 0; itemIndex < soItemBeansArrayList.size(); itemIndex++) {
                saveItem = soItemBeansArrayList.get(itemIndex);
                HashMap<String, String> dbItemTable = new HashMap<>();
                dbItemTable.put(Constants.SONo, doc_no);
                dbItemTable.put("ItemNo", saveItem.getItemNo());
                dbItemTable.put("Material", saveItem.getMatCode());
                dbItemTable.put("MaterialText", saveItem.getMatDesc());
                dbItemTable.put("Quantity", saveItem.getQuantity());
                dbItemTable.put("UOM", saveItem.getUom());
                dbItemTable.put("Currency", saveItem.getCurrency());
                dbItemTable.put("OrderNo", doc_no);
                dbItemTable.put("Remarks", headerDetail.get("Remarks"));
                dbItemTable.put("Plant", headerDetail.get("Plant"));
                dbItemTable.put("StatusUpdate", "NEW");
                dbItemTable.put("ShippingPoint", headerDetail.get("ShippingPoint"));
//                dbItemTable.put("MatFrgtGrp",headerDetail.get("MaterialFrieght") );
//                dbItemTable.put("StorLoc",headerDetail.get("StorageLoc") );
//                dbItemTable.put("DelvQty", "0.00");
////                dbItemTable.put("BalanceQty", "0.00");
//                dbItemTable.put("BalanceQty", saveItem.getSoQty());
////                dbItemTable.put("NetPrice", "0.00");
//                dbItemTable.put("NetPrice", saveItem.getUnitPrice());
////                dbItemTable.put("NetValue", "0.00");
//                dbItemTable.put("NetValue", saveItem.getNetAmount());
////                dbItemTable.put("TaxAmount", "0.00");
//                dbItemTable.put("TaxAmount", saveItem.getTotalAmount());

                dbItemTable.put(Constants.UnitPrice, saveItem.getUnitPrice());
                dbItemTable.put(Constants.NetAmount, saveItem.getNetAmount());
                dbItemTable.put(Constants.GrossAmount, saveItem.getTotalAmount());
//                            singleItem.put(Constants.Freight, itemDesc.get("Freight"));
                dbItemTable.put(Constants.Freight, saveItem.getFreight());
                dbItemTable.put(Constants.Tax, saveItem.getTaxAmount());
                dbItemTable.put(Constants.Discount, saveItem.getDiscount());
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
            if (isComingFromChange)
                set = sharedPreferences.getStringSet(Constants.SOUpdate, null);
            else
                set = sharedPreferences.getStringSet(Constants.SalesOrderDataValt, null);

            HashSet<String> setTemp = new HashSet<>();
            if (set != null && !set.isEmpty()) {
                Iterator<String> itr = set.iterator();
                while (itr.hasNext()) {
                    setTemp.add(itr.next().toString());
                }
            }
            setTemp.add(doc_no);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (isComingFromChange)
                editor.putStringSet(Constants.SOUpdate, setTemp);
            else
                editor.putStringSet(Constants.SalesOrderDataValt, setTemp);
            editor.apply();

            JSONObject jsonHeaderObject = new JSONObject(masterHeaderTable);

            try {
                ConstantsUtils.storeInDataVault(doc_no, jsonHeaderObject.toString(),this);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            String msg = "";
            if (isComingFromChange)
                msg = "Sales order changed successfully";
            else
                msg = "Sales order created successfully";
            MyUtils.dialogConformButton(SOEditReviewActivity.this, msg, new DialogCallBack() {
                @Override
                public void clickedStatus(boolean clickedStatus) {
//                    if (clickedStatus) {
//                        onUpdateSync();
//                    } else {
                    if(isComingFromChange)
                        navToSODetailScreen(true);
                    else
                        navtoPrevScreen();
//                    }
                }
            });


        } else {
            MyUtils.dialogConformButton(SOEditReviewActivity.this, "Data not found", new DialogCallBack() {
                @Override
                public void clickedStatus(boolean clickedStatus) {
                    if(isComingFromChange)
                        navToSODetailScreen(false);
                    else
                        navtoPrevScreen();
                }
            });

        }
    }

//    /**
//     * start sync
//     */
//    private void onUpdateSync() {
//        progressDialog = MyUtils.showProgressDialog(SOReviewActivity.this, "", getString(R.string.progressbar_upload_message));
//        new GRFlushAsyncTask(SOReviewActivity.this, Constants.SalesOrderDataValt, this, getDeliveryList(SOReviewActivity.this), this).execute();
//    }

    /**
     * change another activity
     */
    private void navtoPrevScreen() {
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(SOEditReviewActivity.this, CustomerDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleRetID);
        if (!Constants.OtherRouteNameVal.equalsIgnoreCase("")) {
            intentNavPrevScreen.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
            intentNavPrevScreen.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
        }
        startActivity(intentNavPrevScreen);
    }

    /**
     * change another activity
     */
    private void navToSODetailScreen(boolean isSaveSuccess) {
        Constants.ComingFromCreateSenarios = Constants.X;
        SalesOrderTabActivity.isCancelledOrChanged = true;
        //Intent intent = new Intent(SOReviewActivity.this, ApprovalListDetails.class);
        Intent intent = new Intent(SOEditReviewActivity.this, NewSalesOrderActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        intent.putExtra(Constants.CPNo, mStrBundleRetID);
        intent.putExtra(Constants.RetailerName, mStrBundleRetName);
        intent.putExtra(Constants.CPUID, mStrBundleRetID);
//        intent.putExtra(Constants.comingFrom, mStrComingFrom);
        intent.putExtra(Constants.EXTRA_TAB_POS, Constants.SOBundleExtras.getInt(Constants.EXTRA_TAB_POS, 0));
        intent.putExtra(Constants.DeviceNo, doc_no);
        intent.putExtra(Constants.EXTRA_SSRO_GUID, Constants.SOBundleExtras.getString(Constants.EXTRA_SSRO_GUID));
        intent.putExtra(Constants.EXTRA_ORDER_DATE, headerDetail.get(Constants.OrderDate));
        intent.putExtra(Constants.EXTRA_ORDER_IDS, headerDetail.get(Constants.OrderNo));
        intent.putExtra(Constants.EXTRA_ORDER_AMOUNT,headerDetail.get(Constants.TotalAmount));
        intent.putExtra(Constants.EXTRA_ORDER_SATUS, Constants.SOBundleExtras.getString(Constants.EXTRA_ORDER_SATUS, ""));
        intent.putExtra(Constants.EXTRA_ORDER_CURRENCY, headerDetail.get(Constants.Currency));
        if(isSaveSuccess)
            intent.putExtra(Constants.comingFromChange, true);
        startActivity(intent);

    }

    /**
     * get delivery list
     *
     * @param context
     * @return
     */
    private String[] getDeliveryList(Context context) {
        Set<String> set = new HashSet<>();
        penReqCount = 0;
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);

        set = sharedPreferences.getStringSet(Constants.SalesOrderDataValt, null);

        mIntPendingCollVal = 0;

        if (set != null && !set.isEmpty()) {
            invKeyValues = new String[set.size()];
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal] = itr.next().toString();
                mIntPendingCollVal++;
            }
        }

        if (mIntPendingCollVal > 0) {
            Arrays.sort(invKeyValues, new CustomComparator());
        }
        return invKeyValues;
    }

    @Override
    public void onTaskCompleted(boolean storeStatus) {
        if (!storeStatus) {
            if (progressDialog != null) {
                MyUtils.hideProgressDialog(progressDialog);
            }
            Constants.dialogSingleButton(SOEditReviewActivity.this, getString(R.string.online_store_error), new DialogCallBack() {
                @Override
                public void clickedStatus(boolean clickedStatus) {
                    clearConstants();
                }
            });
        }
    }

    @Override
    public void onRequestError(int operation, Exception e) {
        Log.d(TAG, "onRequestError: ");
        if (progressDialog != null) {
            MyUtils.hideProgressDialog(progressDialog);
        }
        Toast.makeText(this, "onRequestFailed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestSuccess(int operation, String key) {
        Log.d(TAG, "onRequestSuccess: ");
        if (operation == Operation.Create.getValue() && mIntPendingCollVal > 0) {
            Set<String> set = new HashSet<>();
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
            set = sharedPreferences.getStringSet(Constants.SalesOrderDataValt, null);

            HashSet<String> setTemp = new HashSet<>();
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
                while (itr.hasNext()) {
                    setTemp.add(itr.next().toString());
                }
            }

            setTemp.remove(invKeyValues[penReqCount]);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(Constants.SalesOrderDataValt, setTemp);
            editor.apply();

            try {
                ConstantsUtils.storeInDataVault(invKeyValues[penReqCount], "",this);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            penReqCount++;


        }
        if (operation == Operation.Create.getValue() && penReqCount == mIntPendingCollVal) {
            if (!Constants.isNetworkAvailable(SOEditReviewActivity.this)) {
                if (progressDialog != null) {
                    MyUtils.hideProgressDialog(progressDialog);
                }
                onNoNetwork();
            } else {
                try {
                    OfflineManager.flushQueuedRequests(SOEditReviewActivity.this);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }
        } else if (operation == Operation.OfflineFlush.getValue()) {
            try {
                OfflineManager.refreshRequests(getApplicationContext(), Constants.SOs + "," + Constants.SOItemDetails, this);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        } else if (operation == Operation.OfflineRefresh.getValue()) {
            popUpText = getString(R.string.msg_so_created);
            if (progressDialog != null) {
                MyUtils.hideProgressDialog(progressDialog);
            }
            Constants.dialogSingleButton(SOEditReviewActivity.this, popUpText, new DialogCallBack() {
                @Override
                public void clickedStatus(boolean clickedStatus) {
                    clearConstants();
                }
            });

        }

    }

    /**
     * change activity
     */
    private void clearConstants() {
//        Intent intent = new Intent(this, TradeInfoActivity.class);
//        startActivity(intent);
    }

    /**
     * open network dialog
     */
    private void onNoNetwork() {
        Constants.dialogSingleButton(SOEditReviewActivity.this, getString(R.string.alert_sync_cannot_be_performed), new DialogCallBack() {
            @Override
            public void clickedStatus(boolean clickedStatus) {
                clearConstants();
            }
        });
    }
}
