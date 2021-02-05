package com.rspl.sf.msfa.socreate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.CustomerDetailsActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.MaterialsBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataGuid;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;

public class SalesOrderPriceDetails extends AppCompatActivity {
    ArrayList<MaterialsBean> soListBean;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "",mStrBundleCPGUID32="";
    private LinearLayout llso_status;
    private Hashtable<String,String> headerTable=new Hashtable<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order_price_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, "Price Details",0);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null)
        {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            soListBean= getIntent().getParcelableArrayListExtra(Constants.Materials);

        }
        initUI();
        onItemDetails();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_back_save, menu);


        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.menu_save:
                onSave();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private void onSave()
    {
        createHeaderObjects();
    }

    private void createHeaderObjects()
    {
        String doc_no = (System.currentTimeMillis() + "");
        Random r = new Random();
        int SONumber = r.nextInt(9999 - 999) + 999;
        headerTable.put(Constants.SONo,SONumber+"");
        headerTable.put(Constants.LoginID,"");
        headerTable.put(Constants.CustomerNo,mStrBundleRetID);
        headerTable.put(Constants.CustomerName,mStrBundleRetName);
        headerTable.put(Constants.CustomerPO,"");
        headerTable.put(Constants.CustomerPODate,"");
        headerTable.put(Constants.ShipToParty,"");
        headerTable.put(Constants.ShipToPartyName,"");

        headerTable.put(Constants.OrderType, "OR");
        headerTable.put(Constants.OrderTypeDesc, "Standard Order");
        headerTable.put(Constants.OrderDate, UtilConstants.getNewDateTimeFormat());
        headerTable.put(Constants.EntryTime,"PT04H03M35S");

        headerTable.put(Constants.ShipToParty,"0000002000");
        headerTable.put(Constants.ShipToPartyName,"Carbor GmbH");
        headerTable.put(Constants.SalesArea,"");
        headerTable.put(Constants.NetPrice,"0.0");
        headerTable.put(Constants.TaxAmount,"0.0");
        headerTable.put(Constants.Discount,"0.0");
        headerTable.put(Constants.Freight,"0.0");
        headerTable.put(Constants.Freight,"0.0");
        headerTable.put(Constants.Freight,"0.0");
        headerTable.put(Constants.CreatedOn, UtilConstants.getNewDateTimeFormat());
        headerTable.put(Constants.CreatedAt, Constants.getOdataDuration().toString());



        ArrayList<HashMap<String,String>> soItems = new ArrayList<HashMap<String, String>>();
        for(int itemIncVal=0;itemIncVal<soListBean.size();itemIncVal++){
            HashMap<String, String> singleItem = new HashMap<String, String>();
            GUID ssoItemGuid = GUID.newRandom();
            singleItem.put(Constants.SONo,SONumber+"");
            singleItem.put(Constants.ItemNo, (itemIncVal+1)+"");
            singleItem.put(Constants.LoginID,"");
            singleItem.put(Constants.Material,soListBean.get(itemIncVal).getMaterialNo());
            singleItem.put(Constants.MaterialDesc,soListBean.get(itemIncVal).getMaterialDesc());
            singleItem.put(Constants.MaterialGroup,soListBean.get(itemIncVal).getMaterialGrp());
            singleItem.put(Constants.MatGroupDesc,soListBean.get(itemIncVal).getMaterialGrpDesc());
            singleItem.put(Constants.ItemCategory,"");
            singleItem.put(Constants.UOM,soListBean.get(itemIncVal).getBaseUom());
            singleItem.put(Constants.Quantity,"");
            singleItem.put(Constants.Currency,"");
            singleItem.put(Constants.UnitPrice,"0.0");
            singleItem.put(Constants.NetAmount,"0.0");
            singleItem.put(Constants.GrossAmount,"0.0");
            singleItem.put(Constants.Tax,"0.0");
            singleItem.put(Constants.Freight,"0.0");
            singleItem.put(Constants.Discount,"0.0");



            soItems.add(singleItem);
        }

        headerTable.put(Constants.entityType, Constants.SecondarySOCreate);
        headerTable.put(Constants.ITEM_TXT, Constants.convertArrListToGsonString(soItems));

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        updateVisitActivity();

        Constants.saveDeviceDocNoToSharedPref(SalesOrderPriceDetails.this, Constants.SOList,doc_no);

        headerTable.put(Constants.LOGINID, sharedPreferences.getString(Constants.username, "").toUpperCase());

        JSONObject jsonHeaderObject = new JSONObject(headerTable);

        ConstantsUtils.storeInDataVault(doc_no,jsonHeaderObject.toString(),this);

        navigateToVisit();


    }

    private void updateVisitActivity() {
        Hashtable visitActivityTable = new Hashtable();
        GUID mStrGuide = GUID.newRandom();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        String loginIdVal = sharedPreferences.getString(Constants.username, "");
        String mStrSPGUID = Constants.getSPGUID(Constants.SPGUID);
        String getVisitGuidQry = Constants.Visits + "?$filter=EndDate eq null and CPGUID eq '" + mStrBundleRetID + "' " +
                "and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and "+Constants.SPGUID+" eq guid'"+mStrSPGUID+"'";
        ODataGuid mGuidVisitId = null;
        try {
            mGuidVisitId = OfflineManager.getVisitDetails(getVisitGuidQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

        visitActivityTable.put(Constants.ActivityRefID, mStrGuide.toString());

        mStrGuide = GUID.newRandom();
        visitActivityTable.put(Constants.VisitActivityGUID, mStrGuide.toString());
        visitActivityTable.put(Constants.LOGINID, loginIdVal);
        visitActivityTable.put(Constants.VisitGUID, mGuidVisitId.guidAsString36());
        visitActivityTable.put(Constants.ActivityType, "03");
        visitActivityTable.put(Constants.ActivityTypeDesc, Constants.Merchendising_Snap);


        try {
            OfflineManager.createVisitActivity(visitActivityTable);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    /*Navigate to visit screen*/
    public void navigateToVisit(){

        AlertDialog.Builder builder = new AlertDialog.Builder(
                SalesOrderPriceDetails.this, R.style.MyTheme);
        builder.setMessage(getString(R.string.msg_secondary_so_created))
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();
                                    Constants.ComingFromCreateSenarios = Constants.X;
                                    navigateToRetDetailsActivity();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
        builder.show();
    }
    private void navigateToRetDetailsActivity()
    {
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(SalesOrderPriceDetails.this,CustomerDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);

        intentNavPrevScreen.putExtra(Constants.comingFrom, Constants.AdhocList);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);

        startActivity(intentNavPrevScreen);
    }
    private void initUI()
    {
        llso_status = (LinearLayout) findViewById(R.id.ll_so_review_Items);
        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);
        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetID);
    }
    private void onItemDetails() {


        llso_status.removeAllViews();
        TableLayout table = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_qty_table, null);

        if(soListBean!=null){
            for (int i = 0; i < soListBean.size(); i++) {
                TableRow row0 = (TableRow) LayoutInflater.from(this).inflate(
                        R.layout.item_headding, null);
                ((TextView) row0.findViewById(R.id.item_heading)).setText("Item # "+(i+1));
                table.addView(row0);

                TableRow row1 = (TableRow) LayoutInflater.from(this).inflate(
                        R.layout.item_row, null);
                ((TextView) row1.findViewById(R.id.item_lable)).setText("Material");
                ((TextView) row1.findViewById(R.id.item_blank)).setText(":");
                ((TextView) row1.findViewById(R.id.item_value)).setText(soListBean
                        .get(i).getMaterialNo());
                table.addView(row1);

                TableRow row2 = (TableRow) LayoutInflater.from(this).inflate(
                        R.layout.item_row, null);
                ((TextView) row2.findViewById(R.id.item_lable))
                        .setText("Description");
                ((TextView) row2.findViewById(R.id.item_blank)).setText(":");
                ((TextView) row2.findViewById(R.id.item_value)).setText(soListBean
                        .get(i).getMaterialDesc());
                table.addView(row2);



                TableRow row3 = (TableRow) LayoutInflater.from(this).inflate(
                        R.layout.item_row, null);
                ((TextView) row3.findViewById(R.id.item_lable))
                        .setText("Order Qty");
                ((TextView) row3.findViewById(R.id.item_blank)).setText(":");
                ((TextView) row3.findViewById(R.id.item_value)).setText(Constants
                        .removeLeadingZero(soListBean.get(i).getOrderQty())
                        + " "
                        + soListBean.get(i).getBaseUom());
                table.addView(row3);

                String materialPrice = "";
                if(!Constants.soItem.isEmpty())
                    materialPrice = (String) Constants.soItem.get(i).get("UnitPrice");
                TableRow rowItemPrice = (TableRow) LayoutInflater.from(this).inflate(
                        R.layout.item_row, null);
                ((TextView) rowItemPrice.findViewById(R.id.item_lable))
                        .setText("Material Price");
                ((TextView) rowItemPrice.findViewById(R.id.item_blank)).setText(":");

                TextView tvinvAmount=(TextView)rowItemPrice.findViewById(R.id.item_value);
                tvinvAmount.setGravity(Gravity.RIGHT);

                tvinvAmount.setText("0.0");
                table.addView(rowItemPrice);



                String priceString = "0.00";
                if(!Constants.soItem.isEmpty())
                    priceString = (String) Constants.soItem.get(i).get("NetPrice");

                priceString = priceString.equalsIgnoreCase("")?"0.00":priceString;

                TableRow row10 = (TableRow) LayoutInflater.from(this).inflate(
                        R.layout.item_row, null);
                ((TextView) row10.findViewById(R.id.item_lable))
                        .setText("Net Value");
                ((TextView) row10.findViewById(R.id.item_blank)).setText(":");

                TextView tvNetAmount=(TextView)row10.findViewById(R.id.item_value);
                tvNetAmount.setGravity(Gravity.RIGHT);

                tvNetAmount.setText("0.0");
                table.addView(row10);





//				 String tax="";
//				 if(!Constants.soItem.isEmpty())
//					 tax = (String) Constants.soItem.get(i).get("TaxAmount");
//
//					 TableRow row11 = (TableRow) LayoutInflater.from(this).inflate(
//					 R.layout.item_row, null);
//					 ((TextView) row11.findViewById(R.id.item_lable))
//					 .setText("Tax Value");
//					 ((TextView) row11.findViewById(R.id.item_blank)).setText(":");
//
//					 TextView tvtax=(TextView)row11.findViewById(R.id.item_value);
//					 tvtax.setGravity(Gravity.RIGHT);
//
//					 tvtax.setText(Constants
//								.removeLeadingZerowithTwoDecimal(tax)
//								+ " "
//								+ soListBean.get(i).getCurrency());
//					 table.addView(row11);
                TableRow line = (TableRow) LayoutInflater.from(this).inflate(
                        R.layout.line_row, null);

                table.addView(line);
            }
        }

        llso_status.addView(table);

//        tvTotalPrice.setText(Constants.removeLeadingZerowithTwoDecimal(Constants.round(totalPrice, 2)+"")+" "+currency);

    }
}
