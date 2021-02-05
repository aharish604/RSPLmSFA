package com.rspl.sf.msfa.visit;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.CustomerDetailsActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.MaterialByCustomerRecyclerViewAdapter;
import com.rspl.sf.msfa.adapter.StocksInfoRecyclerViewAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.TextWatcherInterface;
import com.rspl.sf.msfa.mbo.StocksInfoBean;
import com.rspl.sf.msfa.mbo.StocksInfoDTO;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;
import java.util.Calendar;

public class StocksInfoActivity extends AppCompatActivity implements TextWatcherInterface,View.OnClickListener,StocksInfoRecyclerViewAdapter.OnRecyclerViewClickListener {

    StocksInfoDTO stocksInfoDTO;
    private String customerNum = "", customerName = "", customerNo = "", MobNum ="",address="";
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    String mStrComingFrom = "";

    private String dateSelected = "";
    private int mYear;
    private int mMonth;
    private int mDay;
    RecyclerView recyclerViewStockInfo,recyclerViewMaterialInfo;
    StocksInfoRecyclerViewAdapter adapter;
    MaterialByCustomerRecyclerViewAdapter materialByCustomerRecyclerViewAdapter;
    ArrayList<StocksInfoBean>stocksInfoBeanArrayList,stocksBeanArrayList,materialBeanArrayList,materialInfoBeanArrayList;
    ArrayList<StocksInfoBean>passToReviewActvitityArraylist;
    public static ArrayList<StocksInfoBean>dealerStocksInfoBeanArrayList = new ArrayList<>();
    ProgressDialog progressDialog;
    EditText editTextDealerStockSearch;
    TextView textViewNoRecordFound,textViewNoRecordFoundMaterial;
    LinearLayout linearLayoutHeader;
    RelativeLayout relativeLayoutMaterials,linearLayoutStocks;
    Button buttonOk,buttonCancel;
    boolean isAddEnabled=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocks_info);
       // ActionBarView.initActionBarView(this, true, getString(R.string.dealer_stock_entry));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.dealer_stock_entry), 0);
        initializeUI();
    }
    private void initializeUI(){
        stocksInfoDTO = new StocksInfoDTO();
        materialBeanArrayList = new ArrayList<>();
        passToReviewActvitityArraylist = new ArrayList<>();
        dealerStocksInfoBeanArrayList.clear();
        recyclerViewStockInfo = (RecyclerView)findViewById(R.id.recyclerViewStockInfo);
        recyclerViewMaterialInfo = (RecyclerView)findViewById(R.id.recyclerViewMaterialInfo);
        editTextDealerStockSearch = (EditText) findViewById(R.id.editTextDealerStockSearch);
        textViewNoRecordFound =(TextView)findViewById(R.id.textViewNoRecordFound);
        textViewNoRecordFoundMaterial =(TextView)findViewById(R.id.textViewNoRecordFoundMaterial);
        linearLayoutStocks =(RelativeLayout) findViewById(R.id.linearLayoutStocks);
        linearLayoutHeader =(LinearLayout) findViewById(R.id.linearLayoutHeader);
        relativeLayoutMaterials =(RelativeLayout) findViewById(R.id.relativeLayoutMaterials);
        buttonOk =(Button) findViewById(R.id.buttonOk);
        buttonCancel =(Button) findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(this);
        buttonOk.setOnClickListener(this);
        stocksBeanArrayList= new ArrayList<>();
        stocksInfoBeanArrayList= new ArrayList<>();
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            customerNo = extra.getString(Constants.CPNo);
            customerName = extra.getString(Constants.RetailerName);
            MobNum = extra.getString(Constants.SalesPersonMobileNo);
            address = extra.getString(Constants.Address);
            mStrBundleRetID = extra.getString(Constants.CPNo);
            mStrBundleRetName = extra.getString(Constants.RetailerName);
            mStrComingFrom = extra.getString(Constants.comingFrom);

        }
        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);
        retName.setText(customerName);
        retId.setText(customerNo);
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        String mon = "";
        String day = "";
        int mnt = 0;
        mnt = mMonth + 1;
        if (mnt < 10)
            mon = "0" + mnt;
        else
            mon = "" + mnt;
        day = "" + mDay;
        if (mDay < 10)
            day = "0" + mDay;
        dateSelected = mYear + "-" + mon + "-" + day;
        isComingFromPerspectiveCustomerList();
        recyclerViewStockInfo.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.scrollToPosition(0);
        recyclerViewStockInfo.setLayoutManager(linearLayoutManager);
        new MaterialsByCustomersAsyncTask().execute();
        editTextDealerStockSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                adapter.filter(s.toString(),textViewNoRecordFound,recyclerViewStockInfo,StocksInfoActivity.this);
                if (!isAddEnabled){
                    filter(s.toString(),stocksInfoBeanArrayList,stocksBeanArrayList,1);
                }else{
                    filter(s.toString(),materialInfoBeanArrayList,materialBeanArrayList,2);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void initializeRecyclerViewItems(){
        for (int materialCustomers = 0; materialCustomers <stocksInfoBeanArrayList.size(); materialCustomers++) {
            StocksInfoBean stocksInfoBean = new StocksInfoBean();
            stocksInfoBean.setMaterialDesc(stocksInfoBeanArrayList.get(materialCustomers).getMaterialDesc());
            stocksInfoBean.setAsOnDateQuantity(stocksInfoBeanArrayList.get(materialCustomers).getAsOnDateQuantity());
            stocksInfoBean.setUOM(stocksInfoBeanArrayList.get(materialCustomers).getUOM());
            stocksInfoBean.setMaterialNo(stocksInfoBeanArrayList.get(materialCustomers).getMaterialNo());
            stocksInfoBean.setStockGuid(stocksInfoBeanArrayList.get(materialCustomers).getStockGuid());
            stocksInfoBean.setStockType(true);
            stocksInfoBean.setEtag(stocksInfoBeanArrayList.get(materialCustomers).getEtag());
            stocksBeanArrayList.add(stocksInfoBean);
        }
        adapter = new StocksInfoRecyclerViewAdapter(getApplicationContext(),stocksBeanArrayList);
        adapter.setOnRecyclerViewClickListener(this);
        adapter.textWatcher(this);
        recyclerViewStockInfo.setAdapter(adapter);

    }
    private String materialCustomersQuery(){
        return Constants.MaterialByCustomers+"?$filter="+Constants.CustomerNo+" eq '1190'";
    }
    private String stocksQuery(){
        return Constants.Stocks+"?$filter="+Constants.Customer+" eq '"+customerNo+"'";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dealer_stocks, menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(StocksInfoActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_stock_info).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        navigateToRetDetailsActivity();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_review:
                if (validateQuantity()) {
                    Intent  intent = new Intent(StocksInfoActivity.this, StocksInfoReviewActivity.class);
                    intent.putExtra(Constants.CustomerName,customerName);
                    intent.putExtra(Constants.Customer,customerNo);
                    intent.putExtra(Constants.StocksList,passToReviewActvitityArraylist);
                    startActivity(intent);
                } else {
                    UtilConstants.showAlert(getString(R.string.validation_plz_enter_qty),StocksInfoActivity.this);

                }
                break;
            case R.id.menu_add:
                isAddEnabled=true;
                dealerStocksInfoBeanArrayList.clear();
                linearLayoutHeader.setVisibility(View.GONE);
                openMaterialDetails();
                break;
        }
        return true;
    }

    private void openMaterialDetails() {
        linearLayoutStocks.setVisibility(View.GONE);
        relativeLayoutMaterials.setVisibility(View.VISIBLE);
        recyclerViewMaterialInfo.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewMaterialInfo.setLayoutManager(linearLayoutManager);
        for (int materialCustomers = 0; materialCustomers <materialInfoBeanArrayList.size(); materialCustomers++) {
            StocksInfoBean stocksInfoBean = new StocksInfoBean();
            stocksInfoBean.setMaterialDesc(materialInfoBeanArrayList.get(materialCustomers).getMaterialDesc());
            stocksInfoBean.setAsOnDateQuantity(materialInfoBeanArrayList.get(materialCustomers).getAsOnDateQuantity());
            stocksInfoBean.setUOM(materialInfoBeanArrayList.get(materialCustomers).getUOM());
            stocksInfoBean.setMaterialNo(materialInfoBeanArrayList.get(materialCustomers).getMaterialNo());

            materialBeanArrayList.add(stocksInfoBean);
        }
        materialByCustomerRecyclerViewAdapter = new MaterialByCustomerRecyclerViewAdapter(getApplicationContext(),materialBeanArrayList);
        recyclerViewMaterialInfo.setAdapter(materialByCustomerRecyclerViewAdapter);

    }

    private void navigateToRetDetailsActivity(){
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen=null;
        if(!mStrComingFrom.equalsIgnoreCase(Constants.ProspectiveCustomerList)){
            intentNavPrevScreen = new Intent(StocksInfoActivity.this,CustomerDetailsActivity.class);
            intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }else{
            intentNavPrevScreen = new Intent(StocksInfoActivity.this,CustomerDetailsActivity.class);
            intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intentNavPrevScreen.putExtra(Constants.Address, address);
            intentNavPrevScreen.putExtra(Constants.SalesPersonMobileNo, MobNum);
        }

        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
//        if(!Constants.OtherRouteNameVal.equalsIgnoreCase("")){
//            intentNavPrevScreen.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
//            intentNavPrevScreen.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
//        }
        startActivity(intentNavPrevScreen);
    }
    private void isComingFromPerspectiveCustomerList(){
//        if (mStrComingFrom.equalsIgnoreCase(Constants.ProspectiveCustomerList)){
//            editTextBgPerBag.setVisibility(View.GONE);
//            editTextACCPerBag.setVisibility(View.GONE);
//            editTextLAFPerBag.setVisibility(View.GONE);
//            editTextUTCLPerBag.setVisibility(View.GONE);
//            editTextOCLPerBag.setVisibility(View.GONE);
//            textViewRetailPaperBag.setVisibility(View.GONE);
//            textViewStockPosition.setText("Brand wise Sale");
//            textViewRetailHDPE.setText("Qty(MT)");
//        }else{
//            editTextBgPerBag.setVisibility(View.VISIBLE);
//            editTextACCPerBag.setVisibility(View.VISIBLE);
//            editTextLAFPerBag.setVisibility(View.VISIBLE);
//            editTextUTCLPerBag.setVisibility(View.VISIBLE);
//            editTextOCLPerBag.setVisibility(View.VISIBLE);
//            textViewRetailPaperBag.setVisibility(View.VISIBLE);
//            textViewStockPosition.setText("Stock Position");
//            textViewRetailHDPE.setText("HDPE(MT)");
//
//        }
    }

    @Override
    public void textChane(String charSequence, int position) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonOk:
                isAddEnabled=false;
//                stocksBeanArrayList.clear();
                for (int i = 0; i < dealerStocksInfoBeanArrayList.size(); i++) {
                    StocksInfoBean stocksInfoBean = new StocksInfoBean();
                    stocksInfoBean.setChecked(dealerStocksInfoBeanArrayList.get(i).getChecked());
                    stocksInfoBean.setMaterialDesc(dealerStocksInfoBeanArrayList.get(i).getMaterialDesc());
                    stocksInfoBean.setUOM(dealerStocksInfoBeanArrayList.get(i).getUOM());
                    stocksInfoBean.setAsOnDateQuantity(dealerStocksInfoBeanArrayList.get(i).getAsOnDateQuantity());
                    stocksInfoBean.setMaterialNo(dealerStocksInfoBeanArrayList.get(i).getMaterialNo());
                    stocksInfoBean.setStockType(false);
                    stocksBeanArrayList.add(stocksInfoBean);

                    materialBeanArrayList.remove(dealerStocksInfoBeanArrayList.get(i));
                    materialInfoBeanArrayList.remove(dealerStocksInfoBeanArrayList.get(i));
                }
                relativeLayoutMaterials.setVisibility(View.GONE);
                linearLayoutHeader.setVisibility(View.VISIBLE);
                linearLayoutStocks.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
                materialByCustomerRecyclerViewAdapter.notifyDataSetChanged();
                break;
            case R.id.buttonCancel:
                linearLayoutStocks.setVisibility(View.VISIBLE);
                relativeLayoutMaterials.setVisibility(View.GONE);
                linearLayoutHeader.setVisibility(View.VISIBLE);
                isAddEnabled=false;
                break;
        }
    }

    @Override
    public void onRecyclerViewItemClickListener(@NonNull View view, @NonNull final StocksInfoBean stocksInfoBean, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(StocksInfoActivity.this, R.style.MyTheme);
        builder.setMessage("Do you want to delete "+stocksInfoBean.getMaterialDesc()+" ?").setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        stocksBeanArrayList.remove(position);
                        stocksInfoBean.setChecked(false);
                        materialInfoBeanArrayList.add(stocksInfoBean);
                        materialBeanArrayList.add(stocksInfoBean);
                        adapter.notifyDataSetChanged();
                        if (materialByCustomerRecyclerViewAdapter!=null)
                            materialByCustomerRecyclerViewAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();

    }


    /**
     * fetching offline data from UDB
     */
    public class MaterialsByCustomersAsyncTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(StocksInfoActivity.this, R.style.ProgressDialogTheme);
            progressDialog.setMessage(getString(R.string.app_loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                materialInfoBeanArrayList = OfflineManager.getMaterialByCustomersList(materialCustomersQuery());
                stocksInfoBeanArrayList= OfflineManager.getStocksList(stocksQuery());
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            initializeRecyclerViewItems();
            progressDialog.dismiss();

        }
    }

    /**
     * validate quantity to go review activity
     * @return
     */
    private boolean validateQuantity(){
        passToReviewActvitityArraylist.clear();
            for (int i = 0; i <stocksBeanArrayList.size() ; i++) {
                StocksInfoBean stocksInfoBean = stocksBeanArrayList.get(i);
                if (!stocksInfoBean.getQuantityInputText().equalsIgnoreCase("")){
                    passToReviewActvitityArraylist.add(stocksInfoBean);
                }
            }
            if (passToReviewActvitityArraylist.size()<=0){
                return false;
            }else {
                return true;
            }
    }

    /**
     * filtering the data by material description
     * @param search
     */
    void filter(String search,ArrayList<StocksInfoBean>infoBeanArrayList,ArrayList<StocksInfoBean> beanArrayList,int code){
        beanArrayList.clear();
        for (int i = 0; i <infoBeanArrayList.size() ; i++) {
            StocksInfoBean stocksInfoBean = infoBeanArrayList.get(i);
            String searchText = stocksInfoBean.getMaterialDesc().toLowerCase();
            if (searchText.contains(search)){
                beanArrayList.add(stocksInfoBean);
            }
        }
        if (code==1){
            adapter.notifyDataSetChanged();
            if (beanArrayList.size()>0){
                if (textViewNoRecordFound!=null)
                    textViewNoRecordFound.setVisibility(View.GONE);
            }else{
                if (textViewNoRecordFound!=null)
                    textViewNoRecordFound.setVisibility(View.VISIBLE);
            }

        }else{
            materialByCustomerRecyclerViewAdapter.notifyDataSetChanged();
            if (beanArrayList.size()>0){
                if (textViewNoRecordFoundMaterial!=null)
                    textViewNoRecordFoundMaterial.setVisibility(View.GONE);
            }else{
                if (textViewNoRecordFoundMaterial!=null)
                    textViewNoRecordFoundMaterial.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==99){
            initializeUI();
        }
    }
}
