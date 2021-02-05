package com.rspl.sf.msfa.visit;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.StocksInfoReviewRecyclerViewAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.StocksInfoBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;
import java.util.Hashtable;

public class StocksInfoReviewActivity extends AppCompatActivity implements StocksInfoReviewRecyclerViewAdapter.OnRecyclerViewClickListener,UIListener{
    EditText editTextDealerStockSearch;
    private String customerNum = "", customerName = "", customerNo = "", MobNum ="",address="";
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "", mStrComingFrom = "";
    RecyclerView recyclerViewStockInfo;
    TextView textViewNoRecordFound;
    private int dataAddedCount = 0;
    ArrayList<StocksInfoBean> stocksInfoBeanArrayList,stocksBeanArrayList;
    StocksInfoReviewRecyclerViewAdapter adapter;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocks_info_review);

        //ActionBarView.initActionBarView(this, true, getString(R.string.dealer_stock_review));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.dealer_stock_review), 0);

        initializeUI();
    }

    private void initializeUI(){

        recyclerViewStockInfo =(RecyclerView)findViewById(R.id.recyclerViewStockInfo);
        textViewNoRecordFound=(TextView)findViewById(R.id.textViewNoRecordFound);
        editTextDealerStockSearch =(EditText)findViewById(R.id.editTextDealerStockSearch);
        stocksBeanArrayList= new ArrayList<>();
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            customerNo = extra.getString(Constants.Customer);
            customerName = extra.getString(Constants.CustomerName);
            stocksInfoBeanArrayList= (ArrayList<StocksInfoBean>) extra.get(Constants.StocksList);
        }
        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);
        retName.setText(customerName);
        retId.setText(customerNo);
        recyclerViewStockInfo.setHasFixedSize(true);
        recyclerViewStockInfo.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        editTextDealerStockSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                adapter.filter(s.toString(),textViewNoRecordFound,recyclerViewStockInfo,StocksInfoActivity.this);
                filter(s.toString(),stocksInfoBeanArrayList,stocksBeanArrayList,1);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        initializeRecyclerViewItems();
    }
    private void initializeRecyclerViewItems(){
        for (int materialCustomers = 0; materialCustomers <stocksInfoBeanArrayList.size(); materialCustomers++) {
            StocksInfoBean stocksInfoBean = new StocksInfoBean();
            stocksInfoBean.setMaterialDesc(stocksInfoBeanArrayList.get(materialCustomers).getMaterialDesc());
            stocksInfoBean.setQuantityInputText(stocksInfoBeanArrayList.get(materialCustomers).getQuantityInputText());
            stocksInfoBean.setUOM(stocksInfoBeanArrayList.get(materialCustomers).getUOM());
            stocksInfoBean.setStockType(stocksInfoBeanArrayList.get(materialCustomers).isStockType());
            stocksInfoBean.setMaterialNo(stocksInfoBeanArrayList.get(materialCustomers).getMaterialNo());
            stocksInfoBean.setStockGuid(stocksInfoBeanArrayList.get(materialCustomers).getStockGuid());
            stocksInfoBean.setEtag(stocksInfoBeanArrayList.get(materialCustomers).getEtag());
            stocksBeanArrayList.add(stocksInfoBean);
        }
        adapter = new StocksInfoReviewRecyclerViewAdapter(getApplicationContext(),stocksBeanArrayList);
        adapter.setOnRecyclerViewClickListener(this);
        recyclerViewStockInfo.setAdapter(adapter);

    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(StocksInfoReviewActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_competition_information).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        StocksInfoReviewActivity.this.finish();
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
        inflater.inflate(R.menu.menu_price_info_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_save:
                new SaveStocksToUDBAsyncTask().execute();
                break;

        }
        return true;
    }
    void filter(String search,ArrayList<StocksInfoBean>infoBeanArrayList,ArrayList<StocksInfoBean> beanArrayList,int code){
        beanArrayList.clear();
        for (int i = 0; i <infoBeanArrayList.size() ; i++) {
            StocksInfoBean stocksInfoBean = infoBeanArrayList.get(i);
            String searchText = stocksInfoBean.getMaterialDesc().toLowerCase();
            if (searchText.contains(search)){
                beanArrayList.add(stocksInfoBean);
            }
        }
        adapter.notifyDataSetChanged();
        if (beanArrayList.size()>0){
            if (textViewNoRecordFound!=null)
                textViewNoRecordFound.setVisibility(View.GONE);
            }else{
                if (textViewNoRecordFound!=null)
                    textViewNoRecordFound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRecyclerViewItemClickListener(@NonNull View view, @NonNull StocksInfoBean stocksInfoBean, int position) {

    }
    public class SaveStocksToUDBAsyncTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(StocksInfoReviewActivity.this,R.style.ProgressDialogTheme);
            progressDialog.setMessage(getString(R.string.pop_up_msg_dealer_stock));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            saveAllItemsToUDB();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
    private void saveAllItemsToUDB(){
        try {
            for (int i = 0; i <stocksBeanArrayList.size() ; i++) {
                Hashtable<String,String> hashtable = new Hashtable<>();
                hashtable.put(Constants.Customer,customerNo);
                hashtable.put(Constants.CustomerName,customerName);
                hashtable.put(Constants.Material,stocksBeanArrayList.get(i).getMaterialNo());
                hashtable.put(Constants.MaterialDesc,stocksBeanArrayList.get(i).getMaterialDesc());
                hashtable.put(Constants.UOM,stocksBeanArrayList.get(i).getUOM());
                hashtable.put(Constants.Unrestricted,stocksBeanArrayList.get(i).getQuantityInputText());
                if (!stocksBeanArrayList.get(i).isStockType()){
                    GUID guid = GUID.newRandom();
                    try {
                        hashtable.put(Constants.StockGuid,guid.toString36().toUpperCase());
                        OfflineManager.createMaterail(hashtable,this);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                }else{
                    hashtable.put(Constants.StockGuid, stocksBeanArrayList.get(i).getStockGuid());
                    hashtable.put(Constants.ETAG, stocksBeanArrayList.get(i).getEtag());
                    try {
                        OfflineManager.updateStock(hashtable,this);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestError(int i, Exception e) {
        Constants.customAlertMessage(this, e.getMessage());
//        if (++dataAddedCount == stocksBeanArrayList.size()) {
//
//        }
        closingProgressDialog();
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        try {
            if (i == Operation.Create.getValue()) {
                if (++dataAddedCount == stocksBeanArrayList.size()) {
                    backToPrevScreenDialog();
                }
            } else if (i == Operation.Update.getValue()) {
                if (++dataAddedCount == stocksBeanArrayList.size()) {
                    backToPrevScreenDialog();
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    private void closingProgressDialog() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Navigate to previous screen dialog*/
    private void backToPrevScreenDialog() {
        closingProgressDialog();

        AlertDialog.Builder builder = new AlertDialog.Builder(
                StocksInfoReviewActivity.this, R.style.MyTheme);
        builder.setMessage(getString(R.string.msg_dealer_stock_created))
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

    private void onNavigateToRetDetilsActivity() {
        finishActivity(99);
    }
}
