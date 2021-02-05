package com.rspl.sf.msfa.dealerstockprice;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.DealerPriceReviewAdapter;
import com.rspl.sf.msfa.common.ActionBarView;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.MyUtils;
import com.rspl.sf.msfa.interfaces.DialogCallBack;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;
import java.util.Hashtable;

public class ReviewActivity extends AppCompatActivity implements UIListener{
    private String customerNum = "", customerName = "", customerNo = "";
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    String mStrComingFrom = "";
    String mStrCurrency="";
    String mStrFrom="";
    private RecyclerView recyclerView;
    private DealerPriceReviewAdapter mAdapter;
    public static boolean isEtFocused=false;
    private String finalSearchData="";
    ArrayList<DealerPriceBean> arrayList=new ArrayList<DealerPriceBean>();
    private ProgressDialog pdLoadDialog;
    private ArrayList<DealerPriceBean> distStockList = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        initialize();

    }

    private void initialize() {



        Bundle extra = getIntent().getExtras();
        if (extra != null) {

            arrayList = (ArrayList<DealerPriceBean>) getIntent().getSerializableExtra("prices");

            mStrFrom = extra.getString("from");
            customerNo = extra.getString(Constants.CPNo);
            customerName = extra.getString(Constants.RetailerName);
//            customerDetail = extra.getBoolean("CustomerDetail");
            mStrBundleRetID = extra.getString(Constants.CPNo);
            mStrBundleRetName = extra.getString(Constants.RetailerName);
            mStrComingFrom = extra.getString(Constants.comingFrom);
            mStrCurrency= extra.getString(Constants.Currency);
        }

        ActionBarView.initActionBarView(this, true, getString(R.string.title_price_review));
        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);
        retName.setText(customerName);
        retId.setText(customerNo);



        TextView tv_hprice = (TextView) findViewById(R.id.tv_hprice);
        mStrCurrency=arrayList.get(0).getCurrency();
        if(mStrCurrency!=null && !mStrCurrency.equalsIgnoreCase("")){
            tv_hprice.setText("Price ("+mStrCurrency+")");
        }else{
            tv_hprice.setText("Price");
        }
        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewMaterail);

        mAdapter = new DealerPriceReviewAdapter(arrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        ((LinearLayoutManager)recyclerView.getLayoutManager()).setStackFromEnd(true);
        recyclerView.scrollToPosition(0);
         recyclerView.setAdapter(mAdapter);


        EditText search= (EditText) findViewById(R.id.ed_material_search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isEtFocused=true;
                // TODO Auto-generated method stub
                finalSearchData = s + "";
                mAdapter.filterSampleDisbursement(finalSearchData);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                //  isEtFocused=true;
                // filter your list from your input
                // filter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_price_info_save, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_save:
                onSave();

                break;

        }
        return true;
    }

    private void navigateToRetDetailsActivity(){
      /*  Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(ReviewActivity.this,CustomerDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);

        startActivity(intentNavPrevScreen);*/
    }



    //Save All stock items in Offline
    private void saveAllStockItems()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        String loginIdVal = sharedPreferences.getString(Constants.username, "");

        for(int i=0;i<arrayList.size();i++)
        {
            Hashtable<String, String> singleItem = new Hashtable<>();

            singleItem.put(Constants.LOGINID,loginIdVal);
            singleItem.put(Constants.CPTypeID, /*Constants.getName(Constants.Customers, Constants.CPTypeID, Constants.CustomerNo,customerNo)*/"");
            singleItem.put(Constants.CPNo, customerNo);
            singleItem.put(Constants.CPName,customerName);
            singleItem.put(Constants.CPTypeDesc,/* Constants.getName(Constants.Customers, Constants.CPTypeDesc, Constants.CustomerNo,customerNo)*/"");
            singleItem.put(Constants.MaterialNo,arrayList.get(i).getMaterialno());
            singleItem.put(Constants.MaterialDesc,arrayList.get(i).getMaterial());
            singleItem.put(Constants.AsOnDate, UtilConstants.getNewDate());

/*            singleItem.put(Constants.OrderMaterialGroupID,arrayList.get(i).getOrderMaterialGroupID());
            singleItem.put(Constants.OrderMaterialGroupDesc,arrayList.get(i).getOrderMaterialGroupDesc());*/
     /*       singleItem.put(Constants.UOM,arrayList.get(i).getUom());
            if(edEnterQty[i].getText().toString().equals(""))
                singleItem.put(Constants.QAQty,arrayList.get(i).getQAQty());
            else
                singleItem.put(Constants.QAQty,edEnterQty[i].getText().toString());*/


            singleItem.put(Constants.Currency,arrayList.get(i).getCurrency());
            singleItem.put(Constants.StockValue,arrayList.get(i).getInputPrice());


        //    if(arrayList.get(i).getStockType().equalsIgnoreCase("Dist")){

            /*    singleItem.put(Constants.CPGUID,mStrBundleCPGUID32);*/

            if(distStockList!=null){
                distStockList.clear();
            }
            try {
                distStockList = OfflineManager.getDBStockMaterialsforPrice(Constants.CPStockItems +
                        "?$filter="+ Constants.MaterialNo+" eq '"+arrayList.get(i).getMaterialno()+"'");
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

           int size=0;
            if(distStockList!=null){
                distStockList.clear();
                size=distStockList.size();
            }
            if(size>0){

                singleItem.put(Constants.ETAG,arrayList.get(i).getEtag());
                singleItem.put(Constants.SetResourcePath, Constants.CPStockItems + "(guid'" + arrayList.get(i).getCpstockitemguid().guidAsString36() + "')");

                singleItem.put(Constants.CPStockItemGUID,arrayList.get(i).getCpstockitemguid().guidAsString36().toUpperCase());
                try
                {

                    OfflineManager.updateCPStockItemsforPrice(singleItem,this);

                }
                catch (OfflineODataStoreException e)
                {
                    LogManager.writeLogError(Constants.error_txt+ e.getMessage());
                }

            }
            else{
                GUID guid= GUID.newRandom();
                singleItem.put(Constants.CPStockItemGUID,guid.toString36().toUpperCase());
                try
                {

                    OfflineManager.createCPStockItemsforPrice(singleItem,this);

                }
                catch (OfflineODataStoreException e)
                {
                    LogManager.writeLogError(Constants.error_txt+ e.getMessage());
                }
            }



           // }
            /*else{
                singleItem.put(Constants.CPStockItemGUID,arrayList.get(i).getCPStockItemGUID());
                singleItem.put(Constants.CPGUID,mStrBundleCPGUID32);
                try
                {

                    OfflineManager.updateCPStockItems(singleItem,this);

                }
                catch (OfflineODataStoreException e)
                {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
            }*/




        }

    }

    private void onSave()
    {
        new onCreateRetailerStockAsyncTask().execute();

    }

    @Override
    public void onRequestError(int i, Exception e) {
        pdLoadDialog.dismiss();
        MyUtils.dialogBoxWithButton(this, "", "failed", "Ok", "", new DialogCallBack() {
            @Override
            public void clickedStatus(boolean clickedStatus) {

                navigateToRetDetailsActivity();
            }
        });

    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        pdLoadDialog.dismiss();
      MyUtils.dialogBoxWithButton(this, "", getResources().getString(R.string.dealer_price_created_successfully), "Ok", "", new DialogCallBack() {
                               @Override
                               public void clickedStatus(boolean clickedStatus) {

                                   navigateToRetDetailsActivity();
                               }
                           });
    }

    /*AsyncTask to create retailer*/
    public class onCreateRetailerStockAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(ReviewActivity.this,R.style.ProgressDialogTheme);
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

}
