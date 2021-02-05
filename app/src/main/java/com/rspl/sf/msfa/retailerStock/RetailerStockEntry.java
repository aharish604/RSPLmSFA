package com.rspl.sf.msfa.retailerStock;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.CustomerDetailsActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;


public class RetailerStockEntry extends AppCompatActivity implements UIListener {
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "",mStrBundleCPGUID32="";
    private String mStrBundleRetailerUID = "",mStrComingFrom="";
    ArrayList<RetailerStockBean> retailerCrsList;
    ArrayList<RetailerStockBean> filteredCrsList;
    private LinearLayout llDelStockLayout;
    int incrementVal = 0;
    private EditText[] edEnterQty;
    private EditText etRetailerSkuSearch;
    private Boolean flag = true,mBoolFirstTime=false;
    private ArrayList<String> deletedItems;
    private int dataAddedCount = 0;

    private ArrayList<RetailerStockBean> distStockList = null;
    private PopupWindow popwind;

    private HashMap<String,RetailerStockBean> mapRetStock =new HashMap<>();
    private HashMap<String,RetailerStockBean> mapDistStockSelected =new HashMap<>();
    private ProgressDialog pdLoadDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_stock_entry);
       // ActionBarView.initActionBarView(this,true,getString(R.string.lbl_retailer_stock_entry));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_retailer_stock_entry), 0);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null)
        {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32= bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
        }
        initUI();
        getCRSStockItems();
        getDistributorStock();
        displayCRSStockValues();
        etRetailerSkuSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDataValues(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    //Filter data values as per the Sku search
    private void filterDataValues(CharSequence prefix)
    {

        if (prefix == null || prefix.length() == 0) {
            filteredCrsList = retailerCrsList;
        }
        else
        {
            {
                String prefixString = prefix.toString().toLowerCase();
                ArrayList<RetailerStockBean> filteredItems = new ArrayList<>();
                int count = retailerCrsList.size();

                for (int i = 0; i < count; i++) {
                    RetailerStockBean item = retailerCrsList.get(i);
                    String mStrRetName = item.getMaterialDesc().toLowerCase();
                    if (mStrRetName.contains(prefixString)) {
                        filteredItems.add(item);
                    }
                }
               filteredCrsList = filteredItems;
            }
        }
        displayCRSStockValues();
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_retailer_stock, menu);

        return true;
    }
    //display the list values in the view
    private void displayCRSStockValues()
    {
        if (!flag) {
            llDelStockLayout.removeAllViews();
        }
        flag = false;

        final TableLayout tableHeading = (TableLayout) LayoutInflater.from(this)
                .inflate(R.layout.retailer_stock_table_view, null);
        int cursorLength = filteredCrsList.size();
        TextView[] tvMaterialDesc = new TextView[cursorLength];
        TextView[] tvMaterialStockQty = new TextView[cursorLength];
        ImageButton[] ibDeleteItem = new ImageButton[cursorLength];
        edEnterQty = new EditText[cursorLength];
        if (cursorLength > 0) {

            for (int i = 0; i < cursorLength; i++) {
                LinearLayout rowRelativeLayout = (LinearLayout) LayoutInflater
                        .from(this).inflate(R.layout.dealer_stock_list, null);

                incrementVal = i;
                tableHeading.setTag(i);
                tvMaterialDesc[i] = (TextView)rowRelativeLayout.findViewById(R.id.tvQuantityHeading);
                tvMaterialDesc[i].setText(filteredCrsList.get(i).getMaterialDesc());

                tvMaterialStockQty[i] = (TextView) rowRelativeLayout.findViewById(R.id.tvProdQuntyView);


                if(!mBoolFirstTime){
                    mapRetStock.put(filteredCrsList.get(i).getMaterialNo(),filteredCrsList.get(i));
                }

                tvMaterialStockQty[i].setText(UtilConstants.removeLeadingZeroQuantity(filteredCrsList.get(i).getQAQty()));



                edEnterQty[i] = (EditText) rowRelativeLayout.findViewById(R.id.editTextQuantityView);
                edEnterQty[i].setTag(i);
                if(filteredCrsList.get(i).getNewStockValue()!=null)
                    edEnterQty[i].setText(filteredCrsList.get(i).getNewStockValue());


                ibDeleteItem[i]= (ImageButton)rowRelativeLayout.findViewById(R.id.ib_delete_item);
                ibDeleteItem[i].setTag(i);
                 ibDeleteItem[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                    deleteItem(v.getTag()+"");

                        }
                    });



                        rowRelativeLayout.setId(i);
                if(!deletedItems.contains(filteredCrsList.get(incrementVal).getMaterialNo()))
                         tableHeading.addView(rowRelativeLayout);
            }

            mBoolFirstTime =true;

            llDelStockLayout.addView(tableHeading);

        }
    }
    private void addStockToEntry()
    {

        ArrayList<RetailerStockBean> alTempRetStock = new ArrayList<>();
        if(filteredCrsList!=null && filteredCrsList.size()>0){
            if(!mapDistStockSelected.isEmpty()){
                Iterator mapSelctedValues = mapDistStockSelected.keySet()
                        .iterator();
                while (mapSelctedValues.hasNext()) {
                    String Key = (String) mapSelctedValues.next();
                    if(!mapRetStock.containsKey(Key)){
                        RetailerStockBean retailerStockBean = new RetailerStockBean();
                        retailerStockBean.setMaterialNo(Key);
                        retailerStockBean.setMaterialDesc(mapDistStockSelected.get(Key).getMaterialDesc());
                        retailerStockBean.setOrderMaterialGroupID(mapDistStockSelected.get(Key).getOrderMaterialGroupID());
                        retailerStockBean.setOrderMaterialGroupDesc(mapDistStockSelected.get(Key).getOrderMaterialGroupDesc());
                        retailerStockBean.setCurrency(mapDistStockSelected.get(Key).getCurrency());
                        retailerStockBean.setUom(mapDistStockSelected.get(Key).getUom());
                        retailerStockBean.setStockType("Dist");
                        alTempRetStock.add(retailerStockBean);
                    }

                }
            }
            filteredCrsList.addAll(filteredCrsList.size(),alTempRetStock);
        }else{
            if(!mapDistStockSelected.isEmpty()){
                Iterator mapSelctedValues = mapDistStockSelected.keySet()
                        .iterator();
                while (mapSelctedValues.hasNext()) {
                    String Key = (String) mapSelctedValues.next();
                    RetailerStockBean retailerStockBean = new RetailerStockBean();
                    retailerStockBean.setMaterialNo(Key);
                    retailerStockBean.setMaterialDesc(mapDistStockSelected.get(Key).getMaterialDesc());
                    retailerStockBean.setOrderMaterialGroupID(mapDistStockSelected.get(Key).getOrderMaterialGroupID());
                    retailerStockBean.setOrderMaterialGroupDesc(mapDistStockSelected.get(Key).getOrderMaterialGroupDesc());
                    retailerStockBean.setCurrency(mapDistStockSelected.get(Key).getCurrency());
                    retailerStockBean.setUom(mapDistStockSelected.get(Key).getUom());
                    retailerStockBean.setStockType("Dist");
                    alTempRetStock.add(retailerStockBean);
                }
            }
            filteredCrsList.addAll(alTempRetStock);
        }

        displayCRSStockValues();

    }
    //Delete the item from the list
    private void deleteItem(String itemId)
    {
        final int selectedID = Integer.parseInt(itemId);
        final String selectedStockName = filteredCrsList.get(selectedID).getMaterialDesc();
        AlertDialog.Builder builder = new AlertDialog.Builder(RetailerStockEntry.this, R.style.MyTheme);
        builder.setMessage(getString(R.string.do_want_to_delete_retailer_stock,selectedStockName)).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        filteredCrsList.remove(selectedID);
                        displayCRSStockValues();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                    }

                });
        builder.show();

    }
    private void initUI()
    {
        retailerCrsList = new ArrayList<>();
        filteredCrsList = new ArrayList<>();
        deletedItems = new ArrayList<>();
        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);

        TextView tvAsOnDateView = (TextView) findViewById(R.id.tvAsOnDateView);
        etRetailerSkuSearch = (EditText)findViewById(R.id.et_retiler_sku_search);
        llDelStockLayout = (LinearLayout) findViewById(R.id.llDealerStockCreate);
        tvAsOnDateView.setText(getString(R.string.msg_as_on)+" "+UtilConstants.convertDateIntoDeviceFormat(this,UtilConstants.getDate1()));
        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetID);
    }

    //get The Crs stock items from the CPStockItems
    private void getCRSStockItems()
    {
        String mStrMyStockQry= Constants.CPStockItems+"?$filter="+ Constants.CPGUID+" eq '"+mStrBundleCPGUID32+"'";

        try {
            retailerCrsList = OfflineManager.getRetailerStockList(mStrMyStockQry);
            filteredCrsList = retailerCrsList;
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void getDistributorStock(){
        try {
            distStockList = OfflineManager.getDBStockMaterials(Constants.CPStockItems +
                    "?$filter="+ Constants.CPTypeID+" eq '20' ");
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                onSave();
                break;
            case R.id.menu_add:
                onAddItems();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
    //Add new items to the array list
    private void onAddItems()
    {
        displayDistributorStock();
    }

    private void onSave()
    {
        new onCreateRetailerStockAsyncTask().execute();

    }
    //Save All stock items in Offline
    private void saveAllStockItems()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        String loginIdVal = sharedPreferences.getString(Constants.username, "");

         for(int i=0;i<filteredCrsList.size();i++)
         {
             Hashtable<String, String> singleItem = new Hashtable<>();

             singleItem.put(Constants.LOGINID,loginIdVal);
             singleItem.put(Constants.CPTypeID, Constants.getName(Constants.ChannelPartners, Constants.CPTypeID, Constants.CPUID,mStrBundleRetID));
             singleItem.put(Constants.CPNo, Constants.getName(Constants.ChannelPartners, Constants.CPNo, Constants.CPUID,mStrBundleRetID));
             singleItem.put(Constants.CPName,mStrBundleRetName);
             singleItem.put(Constants.CPTypeDesc, Constants.getName(Constants.ChannelPartners, Constants.CPTypeDesc, Constants.CPUID,mStrBundleRetID));
             singleItem.put(Constants.MaterialNo,filteredCrsList.get(i).getMaterialNo());
             singleItem.put(Constants.MaterialDesc,filteredCrsList.get(i).getMaterialDesc());
             singleItem.put(Constants.OrderMaterialGroupID,filteredCrsList.get(i).getOrderMaterialGroupID());
             singleItem.put(Constants.OrderMaterialGroupDesc,filteredCrsList.get(i).getOrderMaterialGroupDesc());
             singleItem.put(Constants.UOM,filteredCrsList.get(i).getUom());
             if(edEnterQty[i].getText().toString().equals(""))
                 singleItem.put(Constants.QAQty,filteredCrsList.get(i).getQAQty());
             else
                 singleItem.put(Constants.QAQty,edEnterQty[i].getText().toString());


             singleItem.put(Constants.Currency,filteredCrsList.get(i).getCurrency());



             if(filteredCrsList.get(i).getStockType().equalsIgnoreCase("Dist")){
                 GUID guid= GUID.newRandom();
                 singleItem.put(Constants.CPGUID,mStrBundleCPGUID32);
                 singleItem.put(Constants.CPStockItemGUID,guid.toString36().toUpperCase());
                 try
                 {

                     OfflineManager.createCPStockItems(singleItem,this);

                 }
                 catch (OfflineODataStoreException e)
                 {
                     LogManager.writeLogError(Constants.error_txt+ e.getMessage());
                 }
             }else{
                 singleItem.put(Constants.CPStockItemGUID,filteredCrsList.get(i).getCPStockItemGUID());
                 singleItem.put(Constants.CPGUID,mStrBundleCPGUID32);
                 try
                 {

                     OfflineManager.updateCPStockItems(singleItem,this);

                 }
                 catch (OfflineODataStoreException e)
                 {
                     LogManager.writeLogError(Constants.error_txt + e.getMessage());
                 }
             }




         }

    }


    @Override
    public void onRequestError(int i, Exception e) {

        Constants.customAlertMessage(this,e.getMessage());
        closingProgressDialog();
    }

    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.Create.getValue()) {
            if(++dataAddedCount==filteredCrsList.size()) {
                backToPrevScreenDialog();
            }
        } else if (operation == Operation.Update.getValue()) {
            if(++dataAddedCount==filteredCrsList.size()) {
                backToPrevScreenDialog();

            }
        }
    }

    private void closingProgressDialog(){
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Navigate to previous screen dialog*/
    private void backToPrevScreenDialog(){
        closingProgressDialog();

        AlertDialog.Builder builder = new AlertDialog.Builder(
                RetailerStockEntry.this,R.style.MyTheme);
        builder.setMessage(getString(R.string.msg_ret_stock_created))
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();
                                    onNavigateToRetDetilsActivity();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });
        builder.show();
    }

    @SuppressWarnings("deprecation")
    private void displayDistributorStock(){

        LayoutInflater inflater = (LayoutInflater) RetailerStockEntry.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.pop_up_window_multi_sel, (ViewGroup) findViewById(R.id.PopUpView));

        RelativeLayout layout1 = (RelativeLayout)findViewById(R.id.relative_layout_spinner);
        popwind = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        popwind.setBackgroundDrawable(new BitmapDrawable());
        popwind.setTouchable(true);

        popwind.setOutsideTouchable(true);
        popwind.setHeight(ActionBar.LayoutParams.WRAP_CONTENT);

        popwind.setTouchInterceptor(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popwind.dismiss();
                    return true;
                }

                return false;
            }
        });

        popwind.setContentView(layout);

        popwind.showAsDropDown(layout1);

        ScrollView list_drop_down = (ScrollView) layout.findViewById(R.id.scroll_my_stock_list);

        try {
            list_drop_down.removeAllViews();
        }
        catch (Exception e){
            LogManager.writeLogError(e.getMessage());
        }

        displyExtraMatGrp(list_drop_down,distStockList);

        Button btn_submit = (Button) layout.findViewById(R.id.btn_submit);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (int incVal=0;incVal<distStockList.size();incVal++){
                    RetailerStockBean retailerStockBean = distStockList.get(incVal);
                    if(retailerStockBean.getSelected()) {
                        mapDistStockSelected.put(retailerStockBean.getMaterialNo(),retailerStockBean);
                    }
                }

                popwind.dismiss();
                addStockToEntry();
            }
        });
    }

    private void displyExtraMatGrp(ScrollView list_drop_down,ArrayList<RetailerStockBean> distStockList){
        @SuppressLint("InflateParams")
        TableLayout tlMyStock = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_table, null, false);

        LinearLayout llRetStock;

            if (distStockList!=null
                    && distStockList.size() > 0) {

                final CheckBox[] cbRetStockSel;
                cbRetStockSel = new CheckBox[distStockList.size()];

                for (int i = 0; i < distStockList.size(); i++) {
                    final RetailerStockBean retailerStockBean = distStockList.get(i);

                    llRetStock = (LinearLayout) LayoutInflater.from(this)
                            .inflate(R.layout.drop_down_check_box_item,
                                    null, false);

                    ((TextView) llRetStock.findViewById(R.id.tv_dropdown))
                            .setText(retailerStockBean.getMaterialDesc());

                    cbRetStockSel[i] = (CheckBox) llRetStock
                            .findViewById(R.id.cb_mat_grp_sel);

                    if(mapRetStock.containsKey(retailerStockBean.getMaterialNo())){
                        cbRetStockSel[i].setClickable(false);
                        cbRetStockSel[i].setFocusable(false);
                        cbRetStockSel[i].setEnabled(false);
                    }else if(mapDistStockSelected.containsKey(retailerStockBean.getMaterialNo())){
                        cbRetStockSel[i].setClickable(false);
                        cbRetStockSel[i].setFocusable(false);
                        cbRetStockSel[i].setEnabled(false);
                        cbRetStockSel[i].setChecked(true);
                    }

                    cbRetStockSel[i].setOnClickListener( new View.OnClickListener()
                    {
                        public void onClick(View v)
                        {
                            CheckBox cb = (CheckBox) v;
                            retailerStockBean.setSelected(cb.isChecked());
                        }
                    });

                    tlMyStock.addView(llRetStock);

                }
            }

        list_drop_down.addView(tlMyStock);
        list_drop_down.requestLayout();
    }


    /*AsyncTask to create retailer*/
    public class onCreateRetailerStockAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(RetailerStockEntry.this,R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.pop_up_msg_retailer_stock));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                saveAllStockItems();
            }catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    @Override
    public void onBackPressed() {

            AlertDialog.Builder builder = new AlertDialog.Builder(RetailerStockEntry.this, R.style.MyTheme);
            builder.setMessage(R.string.alert_exit_create_retailer_stock).setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            onNavigateToRetDetilsActivity();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }

                    });
            builder.show();
    }

    private void onNavigateToRetDetilsActivity(){
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(RetailerStockEntry.this, CustomerDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        if(!Constants.OtherRouteNameVal.equalsIgnoreCase("")){
            intentNavPrevScreen.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
            intentNavPrevScreen.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
        }
        startActivity(intentNavPrevScreen);
    }
}
