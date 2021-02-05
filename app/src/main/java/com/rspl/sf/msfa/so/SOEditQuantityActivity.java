package com.rspl.sf.msfa.so;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.ActionBarView;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.MyUtils;
import com.rspl.sf.msfa.interfaces.DialogCallBack;
import com.rspl.sf.msfa.mbo.SalesOrderBean;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.store.OnlineManager;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class SOEditQuantityActivity extends AppCompatActivity implements UIListener{
    public static final int MATERIAL_REQUEST_CODE=69;
    public static ArrayList<SOItemBean> finalSOItemBean = new ArrayList<>();
    public ArrayList<SalesOrderBean> salesOrderBeanArrayList = new ArrayList<>();
    public static HashMap<String, String> headerDetail = null;


    private EditText[] soQtyValue;
    private SOItemAdapter soItemadapter;
    private ArrayList<SOItemBean> items ;
    private SOItemBean singleItem;
    ArrayList<HashMap<String,String>> arrtable ;
    HashMap<Integer, String> quantityValue = new HashMap<>();
    public static HashMap<String, String> malterialHashmap = new HashMap<>();
    private boolean validQty = true;
//    private HashMap<String, String> headerDetail;
    private String qty[];
    private ListView lvList;

    private Hashtable dbHeadTable;
    private ArrayList<HashMap<String, String>> arrtableSimu = null;
    private ProgressDialog progressDialog = null;

    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    String mStrComingFrom = "";
    boolean isComingFromChange = false;
    private String quantity="";
    private String salesArea="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soquantity);
        //ActionBarView.initActionBarView(this, true, getString(R.string.title_sales_order_quantity));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_sales_order_quantity), 0);

        Bundle extra = getIntent().getExtras();
        malterialHashmap.clear();
        if(extra!=null){
            mStrBundleRetID = extra.getString(Constants.CPNo);
            mStrBundleRetName = extra.getString(Constants.RetailerName);
            mStrComingFrom = extra.getString(Constants.comingFrom);
            quantity = extra.getString(Constants.QAQty);
            salesArea = extra.getString(Constants.SalesArea);
            isComingFromChange = extra.getBoolean(Constants.comingFromChange, false);
            salesOrderBeanArrayList = (ArrayList<SalesOrderBean>) extra.getSerializable("SOItemDetails");
            headerDetail = (HashMap<String, String>) extra.getSerializable("Header");
        }
        if(isComingFromChange)
            ActionBarView.initActionBarView(this, true, getString(R.string.title_sales_order_change));
        lvList = (ListView)findViewById(R.id.list);
        items = new ArrayList<>();
        int itemNumber=0;
        if(salesOrderBeanArrayList!=null && salesOrderBeanArrayList.size()>0) {
            for (int i = 0; i < salesOrderBeanArrayList.size(); i++) {
                itemNumber = itemNumber + 10;
                singleItem = new SOItemBean();
                singleItem.setMatCode(salesOrderBeanArrayList.get(i).getMaterialNo());
                singleItem.setMatDesc(salesOrderBeanArrayList.get(i).getMaterialDesc());
                singleItem.setUom(salesOrderBeanArrayList.get(i).getUom());
                singleItem.setQuantity(salesOrderBeanArrayList.get(i).getQAQty());

                singleItem.setItemNo(itemNumber + "");
                items.add(singleItem);
                malterialHashmap.put(salesOrderBeanArrayList.get(i).getMaterialNo(),salesOrderBeanArrayList.get(i).getQAQty());
            }
        }
        qty = new String[items.size()];
        soQtyValue =  new EditText[items.size()];
        soItemadapter = new SOItemAdapter(SOEditQuantityActivity.this, items);
        lvList.setAdapter(SOEditQuantityActivity.this.soItemadapter);
        soItemadapter.notifyDataSetChanged();
    }

    /**
     * create adapter
     */
    public class SOItemAdapter extends ArrayAdapter<SOItemBean> {
        private Context context;

        int sum =0;

        private ArrayList<SOItemBean> items1;
        public SOItemAdapter(Context context, ArrayList<SOItemBean> items1) {
            super(context, 0, items1);
            // TODO Auto-generated constructor stub
            this.context = context;
            this.items1 = items1;
        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return super.getCount();
        }

        @Override
        public SOItemBean getItem(int item) {
            SOItemBean gi = null;
            gi = items1!=null ? items1.get(item) : null;
            return gi;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final int listIndex =position;
            View rowView = convertView;
            TextView itemValue = null;
            TextView matValue  = null;
            TextView matDescValue=null;
            TextView uomValue = null;
            ImageButton imageButtonDelete = null;
            rowView = null;
            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater
                        .inflate(R.layout.so_qty_listview, parent, false);
//                itemValue = (TextView) rowView
//                        .findViewById(R.id.tvitem_headingSO);
                matValue = (TextView) rowView
                        .findViewById(R.id.tvMaterialvalueSO);
                matDescValue = (TextView) rowView
                        .findViewById(R.id.tvMaterialDescvalueSO);
                uomValue = (TextView) rowView
                        .findViewById(R.id.tvUomSOvalue);
                imageButtonDelete = (ImageButton) rowView
                        .findViewById(R.id.imageButtonDelete);

                soQtyValue[listIndex] = (EditText) rowView.findViewById(R.id.etsoQty);
                soQtyValue[listIndex].setText(quantityValue.get(listIndex));
            }
            final SOItemBean itemDetailsPosition = items1.get(listIndex);
            if(!itemDetailsPosition.getQuantity().equalsIgnoreCase("") && quantityValue.get(listIndex)==null) {
                soQtyValue[listIndex].setText(OfflineManager.trimQtyDecimalPlace(itemDetailsPosition.getQuantity()));
                qty[listIndex] = OfflineManager.trimQtyDecimalPlace(itemDetailsPosition.getQuantity());
            }
//            itemValue.setText("Item # 0000" +itemDetailsPosition.getItemNo());
            itemValue.setText("Item # " +Constants.getItemNoInSixCharsWithPrefixZeros(itemDetailsPosition.getItemNo()));
            matValue.setText(itemDetailsPosition.getMatCode());
            matDescValue.setText(itemDetailsPosition.getMatDesc());
            if(itemDetailsPosition.getUom().toString() ==""){

                uomValue.setText("MT");
            }else{
                uomValue.setText(itemDetailsPosition.getUom());
            }

            soQtyValue[listIndex].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    soQtyValue[listIndex].requestFocus();
                    return false;
                }
            });
            soQtyValue[listIndex].addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // TODO Auto-generated method stub
                    soQtyValue[listIndex].setInputType(InputType.TYPE_CLASS_NUMBER);
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    soQtyValue[listIndex].setInputType(InputType.TYPE_CLASS_NUMBER);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String s1 =s.toString();
                    qty[listIndex] = s1;
                    itemDetailsPosition.setSoQty(s1);
//                    quantityValue.put(listIndex, s.toString());
                    itemDetailsPosition.setQuantity(s.toString());
                    malterialHashmap.put(itemDetailsPosition.getMatCode(),itemDetailsPosition.getQuantity());
                    System.out.println("1---*****--->" +listIndex + s1);

                }
            });
            rowView.setId(position);
            imageButtonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove(itemDetailsPosition);
                    notifyDataSetChanged();
                    SOEditQuantityActivity.malterialHashmap.remove(itemDetailsPosition.getMatCode());
                }
            });
            return rowView;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SOEditQuantityActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_competition_information).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        SOEditQuantityActivity.this.finish();
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
        inflater.inflate(R.menu.menu_so_edit_review, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_review:
                //next step
                onReview();
                break;

            case R.id.menu_add:
                //next step
                openSOItemActivity();
                break;

        }
        return true;
    }

    /**
     * next step
     */
    private void onReview() {
        // TODO Auto-generated method stub
        arrtable = new ArrayList<>();
        finalSOItemBean.clear();
        for(int i= 0 ; i<items.size();i++){
            System.out.println("22222@@@" +soQtyValue[i].getText().toString());
            if(qty[i]!=null && !qty[i].trim().toString().equalsIgnoreCase("") ){
                if(Double.parseDouble(qty[i])>0){
                    SOItemBean soItems=  items.get(i);
                    HashMap<String,String> singleItem = new HashMap<>();
                    singleItem.put("MatNo", soItems.getMatCode());
                    singleItem.put("MatDesc", soItems.getMatDesc());
                    singleItem.put("SOQty",soItems.getQuantity());
                    singleItem.put("UOM",soItems.getUom());
                    singleItem.put("ItemNo",soItems.getItemNo());
                    arrtable.add(singleItem);
                    soItems.getConditionItemDetaiBeanArrayList().clear();
                    finalSOItemBean.add(soItems);
                    validQty  =false;
                }
                else{
                    validQty  =true;
                    break;
                }
            }else{
                validQty  =true;
                break;
            }
        }


        if(!validQty){
//            Intent intent =new Intent(this,SOReviewActivity.class);
//            intent.putExtra("list",arrtable);
//            intent.putExtra("Header",headerDetail);
//            startActivity(intent);
            getSimulateValue();
        }else{
            MyUtils.dialogBoxWithButton(this, "",  "Enter the Quantity", "Ok", "", new DialogCallBack() {
                @Override
                public void clickedStatus(boolean clickedStatus) {

                }
            });
        }
    }

    private void getSimulateValue() {
        if (UtilConstants.isNetworkAvailable(SOEditQuantityActivity.this)) {
            if (headerDetail != null && arrtable != null) {
                new onRetriveUnitPriceAsyncTask().execute();
            }
        } else {
            ConstantsUtils.dialogBoxWithButton(SOEditQuantityActivity.this, "", getString(R.string.no_network_conn), "Ok", "", null);
//            finalSOItemBean.addAll(items);
//            headerDetail.put(Constants.Currency, "");
//            headerDetail.put(Constants.TotalAmount, "0");
//            openNextActivity(false);
        }
    }

    public class onRetriveUnitPriceAsyncTask extends AsyncTask<Void, Boolean, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ConstantsUtils.showProgressDialog(SOEditQuantityActivity.this);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean isStoreOpened = false;
            try {

                Thread.sleep(1000);
                try {
                    Constants.IsOnlineStoreFailed = false;

                    isStoreOpened = OnlineManager.openOnlineStore(SOEditQuantityActivity.this, true);
                    if (isStoreOpened) {
                        arrtableSimu = new ArrayList<HashMap<String, String>>();
                        String plant = headerDetail.get("Plant");
                        for (int i = 0; i < arrtable.size(); i++) {
                            HashMap<String, String> itemDesc = arrtable.get(i);
                            HashMap<String, String> singleItem = new HashMap<String, String>();
                            singleItem.put(Constants.ItemNo, ConstantsUtils.addZeroBeforeValue(i + 1, ConstantsUtils.ITEM_MAX_LENGTH));
                            singleItem.put(Constants.Material, itemDesc.get("MatNo"));
                            singleItem.put(Constants.Plant, plant);
                            singleItem.put(Constants.StorLoc, "");
                            singleItem.put(Constants.UOM, itemDesc.get("UOM"));
                            singleItem.put(Constants.Quantity, itemDesc.get("SOQty"));
                            singleItem.put(Constants.Currency, itemDesc.get("Currency"));
                            singleItem.put(Constants.UnitPrice, "0");
                            singleItem.put(Constants.NetAmount, "0");
                            singleItem.put(Constants.GrossAmount, "0");
//                            singleItem.put(Constants.Freight, itemDesc.get("Freight"));
                            singleItem.put(Constants.Freight, "0");
                            singleItem.put(Constants.Tax, "0");
                            singleItem.put(Constants.Discount, "0");
//                            singleItem.put(Constants.MatFrgtGrp,headerDetail.get("MaterialFrieght") );
//                            singleItem.put(Constants.StorLoc,headerDetail.get("StorageLoc") );


                            arrtableSimu.add(singleItem);
                        }
                        dbHeadTable = new Hashtable();
                        Log.d("SOQty", "doInBackground: " + arrtableSimu);
                        dbHeadTable.put(Constants.OrderType, headerDetail.get("OrderType"));
                        dbHeadTable.put(Constants.OrderDate, Constants.getNewDateTimeFormat());
                        dbHeadTable.put(Constants.CustomerNo, headerDetail.get("SoldTo"));
                        dbHeadTable.put(Constants.CustomerPO, headerDetail.get("PONo"));
                        dbHeadTable.put(Constants.CustomerPODate, headerDetail.get("PODate"));
                        dbHeadTable.put(Constants.ShippingTypeID, "");

//                        dbHeadTable.put(Constants.TransporterID, headerDetail.get("TransportNameID"));
//                        dbHeadTable.put(Constants.TransporterName, headerDetail.get("TransportName"));
                        dbHeadTable.put(Constants.ShipToParty, headerDetail.get("ShipTo"));
                        dbHeadTable.put(Constants.SalesArea, headerDetail.get("SalesArea"));
                        dbHeadTable.put(Constants.SalesOffice, "");
                        dbHeadTable.put(Constants.SalesGroup, "");
                        dbHeadTable.put(Constants.Plant, headerDetail.get("Plant"));
                        dbHeadTable.put(Constants.PlantDesc, headerDetail.get("PlantDesc"));
                        dbHeadTable.put(Constants.Incoterm1, headerDetail.get("IncoTerm1"));//headerDetail.get("IncoTerm1")
                        dbHeadTable.put(Constants.Incoterm1Desc, headerDetail.get(Constants.Incoterm1Desc));//headerDetail.get("IncoTerm1")
                        dbHeadTable.put(Constants.Incoterm2, headerDetail.get("IncoTerm2"));//headerDetail.get("IncoTerm1")
                        dbHeadTable.put(Constants.Payterm, headerDetail.get("PaymentTerm"));//headerDetail.get("PaymentTerm")
                        dbHeadTable.put(Constants.PaytermDesc, headerDetail.get(Constants.PaytermDesc));//headerDetail.get("PaymentTerm")
                        dbHeadTable.put(Constants.Currency, "EUR");
                        dbHeadTable.put(Constants.NetPrice, "0");
                        dbHeadTable.put(Constants.TotalAmount, "0");
                        dbHeadTable.put(Constants.TaxAmount, "0");
                        dbHeadTable.put(Constants.Freight, "0");
                        dbHeadTable.put(Constants.Discount, "0");
                        dbHeadTable.put(Constants.Testrun, "S");

//                        dbHeadTable.put(Constants.SalesDist, headerDetail.get(Constants.SalesDist));
//                        dbHeadTable.put(Constants.MeansOfTranstyp, headerDetail.get(Constants.MeansOfTranstyp));

//                        OnlineManager.getSimulateValue(dbHeadTable, arrtableSimu, SOEditQuantityActivity.this);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return isStoreOpened;
        }

        @Override
        protected void onPostExecute(Boolean openStore) {
            super.onPostExecute(openStore);
            if (!openStore) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                ConstantsUtils.dialogBoxWithButton(SOEditQuantityActivity.this, "", getString(R.string.online_store_failed), "Ok", "", null);
            }
        }
    }

    @Override
    public void onRequestError(int i, Exception e) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        ConstantsUtils.dialogBoxWithButton(SOEditQuantityActivity.this, "", "SO simulate failed", "Ok", "", null);

    }

    @Override
    public void onRequestSuccess(int opp, String s) throws ODataException, OfflineODataStoreException {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
            openNextActivity(true);
    }

    public void openNextActivity(boolean isSimulated){
        Intent intent =new Intent(this,SOEditReviewActivity.class);
        intent.putExtra("list",arrtable);
        intent.putExtra("Header",headerDetail);
        intent.putExtra(Constants.EXTRA_SO_ITEM_LIST, finalSOItemBean);
        intent.putExtra(Constants.EXTRA_Is_Simulated, isSimulated);
        intent.putExtra(Constants.CPNo, mStrBundleRetID);
        intent.putExtra(Constants.RetailerName, mStrBundleRetName);
        intent.putExtra(Constants.comingFrom, mStrComingFrom);
        intent.putExtra(Constants.comingFromChange,isComingFromChange);
        startActivity(intent);
    }
    public void openSOItemActivity(){
        Intent intent =new Intent(this,SOEditItemActivity.class);
        intent.putExtra("list",arrtable);
        intent.putExtra("Header",headerDetail);
        intent.putExtra(Constants.EXTRA_SO_ITEM_LIST, finalSOItemBean);
        intent.putExtra(Constants.CPNo, mStrBundleRetID);
        intent.putExtra(Constants.RetailerName, mStrBundleRetName);
        intent.putExtra(Constants.comingFrom, mStrComingFrom);
        intent.putExtra(Constants.comingFromChange,isComingFromChange);
        intent.putExtra(Constants.SalesArea,salesArea);
        startActivityForResult(intent,MATERIAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==MATERIAL_REQUEST_CODE){
            if (resultCode==MATERIAL_REQUEST_CODE){
                try {
                    ArrayList<SOItemBean> tempSOItemBeanList;
                    items = new ArrayList<>();
                    tempSOItemBeanList =(ArrayList<SOItemBean>) data.getExtras().getSerializable("items");
                    int itemNumber=0;
                    if(tempSOItemBeanList!=null && tempSOItemBeanList.size()>0) {
                        for (int i = 0; i < tempSOItemBeanList.size(); i++) {
                            itemNumber = itemNumber + 10;
                            singleItem = new SOItemBean();
                            singleItem.setMatCode(tempSOItemBeanList.get(i).getMatCode());
                            singleItem.setMatDesc(tempSOItemBeanList.get(i).getMatDesc());
                            singleItem.setUom(tempSOItemBeanList.get(i).getUom());
                            singleItem.setQuantity(tempSOItemBeanList.get(i).getQuantity());
                            singleItem.setItemNo(itemNumber + "");
                            items.add(singleItem);
                            malterialHashmap.put(tempSOItemBeanList.get(i).getMatCode(),tempSOItemBeanList.get(i).getQuantity());
                        }
                    }
                    qty = new String[items.size()];
                    soQtyValue =  new EditText[items.size()];
                    soItemadapter = new SOItemAdapter(SOEditQuantityActivity.this, items);
                    lvList.setAdapter(SOEditQuantityActivity.this.soItemadapter);
                    soItemadapter.notifyDataSetChanged();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
