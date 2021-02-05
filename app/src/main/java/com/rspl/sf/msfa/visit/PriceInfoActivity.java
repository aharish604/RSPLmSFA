package com.rspl.sf.msfa.visit;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.CustomerDetailsActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.DealerStockPriceRecyclerAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.MyUtils;
import com.rspl.sf.msfa.dealerstockprice.DealerPriceBean;
import com.rspl.sf.msfa.dealerstockprice.ReviewActivity;
import com.rspl.sf.msfa.interfaces.DialogCallBack;
import com.rspl.sf.msfa.interfaces.TextWatcherInterface;
import com.rspl.sf.msfa.mbo.WholesaleAndRetailSellingDTO;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

public class PriceInfoActivity extends AppCompatActivity {

    private String customerNum = "", customerName = "", customerNo = "";
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    String mStrComingFrom = "";
    String mStrCurrency="";
    WholesaleAndRetailSellingDTO wholesaleAndRetailSellingDTO;
    EditText editTextBgHDPE,editTextBgPerBag,editTextUTCLHDPE,editTextUTCLPerBag,editTextOCLHDPE,editTextOCLPerBag,editTextLAFHDPE,editTextLAFPerBag,editTextACCHDPE,editTextACCPerBag;
    EditText editTextRetailBgHDPE,editTextRetailBgPerBag,editTextRetailUTCLHDPE,editTextRetailUTCLPerBag,editTextRetailOCLHDPE,editTextRetailOCLPerBag,editTextRetailLAFHDPE,editTextRetailLAFPerBag,editTextRetailACCHDPE,editTextRetailACCPerBag;
    private String dateSelected = "";
    private int mYear;
    private int mMonth;
    private int mDay;
    boolean isWholesale;
    String mStrFrom="";
    ArrayList<DealerPriceBean> arrayList=new ArrayList<DealerPriceBean>();
    ArrayList<DealerPriceBean> valList=new ArrayList<DealerPriceBean>();
    private List<DealerPriceBean> searchList = new ArrayList();
    private RecyclerView recyclerView;
    private DealerStockPriceRecyclerAdapter mAdapter;
    public static boolean isEtFocused=false;
    private String finalSearchData="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isEtFocused=false;
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            mStrFrom = extra.getString("from");
            customerNo = extra.getString(Constants.CPNo);
            customerName = extra.getString(Constants.RetailerName);
//            customerDetail = extra.getBoolean("CustomerDetail");
            mStrBundleRetID = extra.getString(Constants.CPNo);
            mStrBundleRetName = extra.getString(Constants.RetailerName);
            mStrComingFrom = extra.getString(Constants.comingFrom);
            mStrCurrency= extra.getString(Constants.Currency);
        }

        if(mStrFrom.equalsIgnoreCase("dealer_price")){
            setContentView(R.layout.activity_price_info);
            //ActionBarView.initActionBarView(this, true, getString(R.string.title_price_info));

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_price_info), 0);
            initializeUI();

        }else if(mStrFrom.equalsIgnoreCase("dealer_stock")){
            setContentView(R.layout.activity_dealer_stock_price);
            //ActionBarView.initActionBarView(this, true, getString(R.string.title_price_info));

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_price_info), 0);

            initializeStockPriceUI();
            TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
            TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);


            retName.setText(customerName);
            retId.setText(customerNo);


           // exportDB();
            new AsynTaskGetdata(PriceInfoActivity.this).execute();
        }


    }

    private void initializeStockPriceUI(){

    }




    @SuppressLint("LongLogTag")
    private class AsynTaskGetdata extends AsyncTask<Void, Boolean, Boolean> {
        ProgressDialog p;
        Context mctx;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p=new ProgressDialog(PriceInfoActivity.this,R.style.ProgressDialogTheme);
            p.setMessage(getResources().getString(R.string.msg_loading_material_price));
            p.show();
        }
        public AsynTaskGetdata(Context ctx){

            mctx=ctx;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            boolean isException =false;
            try {
                String qry= "MaterialByCustomers?$filter="+Constants.CustomerNo+" eq '"+customerNo+"'";
                arrayList= OfflineManager.getMaterialPriceList(qry);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            return isException;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            p.dismiss();
            TextView tv_hprice = (TextView) findViewById(R.id.tv_hprice);
            if(arrayList.size()!=0){
                mStrCurrency=arrayList.get(0).getCurrency();
            }

            if(mStrCurrency!=null && !mStrCurrency.equalsIgnoreCase("")){
                tv_hprice.setText("Price ("+mStrCurrency+")");
            }else{
                tv_hprice.setText("Price");
            }
            recyclerView = (RecyclerView) findViewById(R.id.recyclerviewMaterail);

            mAdapter = new DealerStockPriceRecyclerAdapter(arrayList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(mLayoutManager);
            ((LinearLayoutManager)recyclerView.getLayoutManager()).setStackFromEnd(true);
            recyclerView.scrollToPosition(0);
            mAdapter.textWatcher(new TextWatcherInterface(){

                @Override
                public void textChane(String charSequence, int position) {

                }
            });
            //     recyclerView.setItemAnimator(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL)));
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
    }


    void filter(String text){
        TextView tv_no_records_found= (TextView) findViewById(R.id.tv_no_records_found);
        searchList.clear();
        if (!TextUtils.isEmpty(text)) {
            for (DealerPriceBean d : arrayList) {
                //or use .equal(text) with you want equal match
                //use .toLowerCase() for better matches
                if (d.getMaterial().toLowerCase().contains(text.toLowerCase())) {
                    searchList.add(d);
                }
            }
        }else {
            searchList.addAll(arrayList);
        }
        //update recyclerview
        if(searchList.size()==0){
            tv_no_records_found.setVisibility(View.VISIBLE);
        }
        else{
            tv_no_records_found.setVisibility(View.GONE);
        }
        mAdapter.updateList(searchList);


    }


    private void initializeUI(){
        wholesaleAndRetailSellingDTO= new WholesaleAndRetailSellingDTO();
        setTitle(this.getClass().getSimpleName());
        Bundle extra = getIntent().getExtras();
        editTextBgHDPE =(EditText)findViewById(R.id.editTextBgHDPE);
        editTextBgPerBag =(EditText)findViewById(R.id.editTextBgPerBag);
        editTextUTCLHDPE =(EditText)findViewById(R.id.editTextUTCLHDPE);
        editTextUTCLPerBag =(EditText)findViewById(R.id.editTextUTCLPerBag);
        editTextOCLHDPE =(EditText)findViewById(R.id.editTextOCLHDPE);
        editTextOCLPerBag =(EditText)findViewById(R.id.editTextOCLPerBag);
        editTextLAFHDPE =(EditText)findViewById(R.id.editTextLAFHDPE);
        editTextLAFPerBag =(EditText)findViewById(R.id.editTextLAFPerBag);
        editTextACCHDPE =(EditText)findViewById(R.id.editTextACCHDPE);
        editTextACCPerBag =(EditText)findViewById(R.id.editTextACCPerBag);

        editTextRetailBgHDPE =(EditText)findViewById(R.id.editTextRetailBgHDPE);
        editTextRetailBgPerBag =(EditText)findViewById(R.id.editTextRetailBgPerBag);
        editTextRetailUTCLHDPE =(EditText)findViewById(R.id.editTextRetailUTCLHDPE);
        editTextRetailUTCLPerBag =(EditText)findViewById(R.id.editTextRetailUTCLPerBag);
        editTextRetailOCLHDPE =(EditText)findViewById(R.id.editTextRetailOCLHDPE);
        editTextRetailOCLPerBag =(EditText)findViewById(R.id.editTextRetailOCLPerBag);
        editTextRetailLAFHDPE =(EditText)findViewById(R.id.editTextRetailLAFHDPE);
        editTextRetailLAFPerBag =(EditText)findViewById(R.id.editTextRetailLAFPerBag);
        editTextRetailACCHDPE =(EditText)findViewById(R.id.editTextRetailACCHDPE);
        editTextRetailACCPerBag =(EditText)findViewById(R.id.editTextRetailACCPerBag);



        if (extra != null) {
            customerNo = extra.getString(Constants.CPNo);
            customerName = extra.getString(Constants.RetailerName);
//            customerDetail = extra.getBoolean("CustomerDetail");
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
    }
    private boolean validatePrice(){
        if(editTextBgHDPE.getText().toString().trim().length()==0 && editTextBgPerBag.getText().toString().trim().length()==0&&editTextACCPerBag.getText().toString().trim().length()==0&&
                editTextUTCLHDPE.getText().toString().trim().length()==0&& editTextUTCLPerBag.getText().toString().trim().length()==0 && editTextOCLHDPE.getText().toString().trim().length()==0&&
                editTextOCLPerBag.getText().toString().trim().length()==0 && editTextLAFHDPE.getText().toString().trim().length()==0&&
                editTextLAFPerBag.getText().toString().trim().length()==0&&editTextACCHDPE.getText().toString().trim().length()==0&&
                editTextRetailBgHDPE.getText().toString().trim().length()==0&&editTextRetailBgPerBag.getText().toString().trim().length()==0&&
                editTextRetailUTCLHDPE.getText().toString().trim().length()==0&&editTextRetailUTCLPerBag.getText().toString().trim().length()==0&&
                editTextRetailOCLHDPE.getText().toString().trim().length()==0&&editTextRetailOCLPerBag.getText().toString().trim().length()==0&&
                editTextRetailLAFHDPE.getText().toString().trim().length()==0&&editTextRetailLAFPerBag.getText().toString().trim().length()==0&&
                editTextRetailACCHDPE.getText().toString().trim().length()==0&&editTextRetailACCPerBag.getText().toString().trim().length()==0){
            showAlertDialog(getString(R.string.please_enter_price));
            return false;
        }else{
            return true;
        }
    }
    private boolean validateMaterialPrice(){
        if(editTextBgHDPE.getText().toString().trim().length()==0 && editTextBgPerBag.getText().toString().trim().length()==0&&editTextACCPerBag.getText().toString().trim().length()==0&&
                editTextUTCLHDPE.getText().toString().trim().length()==0&& editTextUTCLPerBag.getText().toString().trim().length()==0 && editTextOCLHDPE.getText().toString().trim().length()==0&&
                editTextOCLPerBag.getText().toString().trim().length()==0 && editTextLAFHDPE.getText().toString().trim().length()==0&&
                editTextLAFPerBag.getText().toString().trim().length()==0&&editTextACCHDPE.getText().toString().trim().length()==0&&
                editTextRetailBgHDPE.getText().toString().trim().length()==0&&editTextRetailBgPerBag.getText().toString().trim().length()==0&&
                editTextRetailUTCLHDPE.getText().toString().trim().length()==0&&editTextRetailUTCLPerBag.getText().toString().trim().length()==0&&
                editTextRetailOCLHDPE.getText().toString().trim().length()==0&&editTextRetailOCLPerBag.getText().toString().trim().length()==0&&
                editTextRetailLAFHDPE.getText().toString().trim().length()==0&&editTextRetailLAFPerBag.getText().toString().trim().length()==0&&
                editTextRetailACCHDPE.getText().toString().trim().length()==0&&editTextRetailACCPerBag.getText().toString().trim().length()==0){
            showAlertDialog(getString(R.string.please_enter_price));
            return false;
        }else{
            return true;
        }
    }

    private void showAlertDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(PriceInfoActivity.this, R.style.MyTheme);
        builder.setMessage(message).setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_review, menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PriceInfoActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_price_info).setCancelable(false)
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
//            case android.R.id.home:
//                onBackPressed();
//                break;

            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_review:
                //next step
                if(mStrFrom.equalsIgnoreCase("dealer_stock")){
                  valList.clear();
                   for(int i=0;i<arrayList.size();i++){
                       if(arrayList.get(i).getInputPrice().length()!=0){
                           valList.add(arrayList.get(i));
                       }
                   }
                   if(valList.size()==0){
                       showAlertDialog(getString(R.string.please_enter_price));
                   }else{
                     //  showAlertDialog(getString());
                       boolean zeroFound=false;
                       for(int i=0;i<valList.size();i++){
                           if(valList.get(i).getInputPrice().length()!=0){
                               //valList.add(arrayList.get(i));
                               String price=valList.get(i).getInputPrice();
                               if(Double.parseDouble(price)==0.0){
                                 //  showAlertDialog("Please enter proper price values");
                                   zeroFound=true;
                               }
                           }
                       }

                       if(zeroFound==true){
                           showAlertDialog("Please enter valid price");
                       }else{


                         onReviewActivity();



                       }


                   }

                }else{
                    if (validatePrice()){
                        editTextData();
                        saveData();
                    }
                }
                break;

        }
        return true;
    }

    private void onReviewActivity() {

        Intent intentFeedBack = new Intent(PriceInfoActivity.this, ReviewActivity.class);
        intentFeedBack.putExtra(Constants.CPNo, customerNo);
        intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentFeedBack.putExtra("from", "dealer_stock");
        intentFeedBack.putExtra(Constants.Currency, mStrCurrency);
        intentFeedBack.putExtra("prices",valList);
        intentFeedBack.putExtra(Constants.CPUID, mStrBundleRetID);
        intentFeedBack.putExtra(Constants.comingFrom, mStrComingFrom);
        intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        startActivity(intentFeedBack);
    }

    public ArrayList<String> getEditTextWholeSaleHDPEData(){
        ArrayList<String> list = new ArrayList<>();
        list.add(wholesaleAndRetailSellingDTO.getWholesalebgHDPE());
        list.add(wholesaleAndRetailSellingDTO.getWholeSaleutclHDPE());
        list.add(wholesaleAndRetailSellingDTO.getWholeSaleoclHDPE());
        list.add(wholesaleAndRetailSellingDTO.getWholeSalelafHDPE());
        list.add(wholesaleAndRetailSellingDTO.getWholeSaleaccHDPE());
        return list;
    }
    public ArrayList<String> getEditTextWholeSalePerBagData(){
        ArrayList<String> list = new ArrayList<>();
        list.add(wholesaleAndRetailSellingDTO.getWholeSalebgPaperBag());
        list.add(wholesaleAndRetailSellingDTO.getWholeSaleutclPaperBag());
        list.add(wholesaleAndRetailSellingDTO.getWholeSaleoclPaperBag());
        list.add(wholesaleAndRetailSellingDTO.getWholeSalelafPaperBag());
        list.add(wholesaleAndRetailSellingDTO.getWholeSaleaccPaperBag());
        return list;
    }
    public ArrayList<String> getEditTextRetailHDPEData(){
        ArrayList<String> list = new ArrayList<>();
        list.add(wholesaleAndRetailSellingDTO.getRetailbgHDPE());
        list.add(wholesaleAndRetailSellingDTO.getRetailUTCLHDPE());
        list.add(wholesaleAndRetailSellingDTO.getRetailoclHDPE());
        list.add(wholesaleAndRetailSellingDTO.getRetaillafHDPE());
        list.add(wholesaleAndRetailSellingDTO.getRetailaccHDPE());
        return list;
    }
    public ArrayList<String> getEditTextRetailPerBagData(){
        ArrayList<String> list = new ArrayList<>();
        list.add(wholesaleAndRetailSellingDTO.getRetailbgPaperBag());
        list.add(wholesaleAndRetailSellingDTO.getRetailUTCLPaperBag());
        list.add(wholesaleAndRetailSellingDTO.getRetailoclPaperBag());
        list.add(wholesaleAndRetailSellingDTO.getRetaillafPaperBag());
        list.add(wholesaleAndRetailSellingDTO.getRetailaccPaperBag());
        return list;
    }
    public ArrayList<String> getBrandData(){
        ArrayList<String> list = new ArrayList<>();
        list.add("BG");
        list.add("UTCL");
        list.add("OCL");
        list.add("LAF");
        list.add("ACC");
        return list;
    }
    private void editTextData(){
        wholesaleAndRetailSellingDTO.setWholesalebgHDPE(editTextBgHDPE.getText().toString());
        wholesaleAndRetailSellingDTO.setWholeSalebgPaperBag(editTextBgPerBag.getText().toString());
        wholesaleAndRetailSellingDTO.setWholeSaleutclHDPE(editTextUTCLHDPE.getText().toString());
        wholesaleAndRetailSellingDTO.setWholeSaleutclPaperBag(editTextUTCLPerBag.getText().toString());
        wholesaleAndRetailSellingDTO.setWholeSaleoclHDPE(editTextOCLHDPE.getText().toString());
        wholesaleAndRetailSellingDTO.setWholeSaleoclPaperBag(editTextOCLPerBag.getText().toString());
        wholesaleAndRetailSellingDTO.setWholeSalelafHDPE(editTextLAFHDPE.getText().toString());
        wholesaleAndRetailSellingDTO.setWholeSalelafPaperBag(editTextLAFPerBag.getText().toString());
        wholesaleAndRetailSellingDTO.setWholeSaleaccHDPE(editTextACCHDPE.getText().toString());
        wholesaleAndRetailSellingDTO.setWholeSaleaccPaperBag(editTextACCPerBag.getText().toString());
        wholesaleAndRetailSellingDTO.setRetailbgHDPE(editTextRetailBgHDPE.getText().toString());
        wholesaleAndRetailSellingDTO.setRetailbgPaperBag(editTextRetailBgPerBag.getText().toString());
        wholesaleAndRetailSellingDTO.setRetailUTCLHDPE(editTextRetailUTCLHDPE.getText().toString());
        wholesaleAndRetailSellingDTO.setRetailUTCLPaperBag(editTextRetailUTCLPerBag.getText().toString());
        wholesaleAndRetailSellingDTO.setRetailoclHDPE(editTextRetailOCLHDPE.getText().toString());
        wholesaleAndRetailSellingDTO.setRetailoclPaperBag(editTextRetailOCLPerBag.getText().toString());
        wholesaleAndRetailSellingDTO.setRetaillafHDPE(editTextRetailLAFHDPE.getText().toString());
        wholesaleAndRetailSellingDTO.setRetaillafPaperBag(editTextRetailLAFPerBag.getText().toString());
        wholesaleAndRetailSellingDTO.setRetailaccHDPE(editTextRetailACCHDPE.getText().toString());
        wholesaleAndRetailSellingDTO.setRetailaccPaperBag(editTextRetailACCPerBag.getText().toString());
    }

    public void saveData(){


        try {
            Hashtable<String, String> hashtable = new Hashtable<>();
            hashtable.put(Constants.PriceDate, UtilConstants.getNewDate());
            hashtable.put(Constants.CustomerName, customerName);
            hashtable.put(Constants.CustomerNo, customerNo);
            for (int i = 0; i <getBrandData().size() ; i++) {
                hashtable.put(Constants.BrandName, getBrandData().get(i));
                hashtable.put(Constants.HDPE,getEditTextWholeSaleHDPEData().get(i));
                hashtable.put(Constants.PaperBag, getEditTextWholeSalePerBagData().get(i));
                hashtable.put(Constants.HDPE,getEditTextRetailHDPEData().get(i));
                hashtable.put(Constants.PaperBag, getEditTextRetailPerBagData().get(i));
            }
            if (isWholesale){
                hashtable.put(Constants.PriceType, "01");
            }else{
                hashtable.put(Constants.PriceType, "02");
            }
            hashtable.put(Constants.DateofDispatch, UtilConstants.getNewDate());
            Constants.events.insert(Constants.PRICE_INFO_TABLE, hashtable);
            MyUtils.dialogBoxWithButton(this, "",  "Price Info created successfully", "Ok", "", new DialogCallBack() {
                @Override
                public void clickedStatus(boolean clickedStatus) {

                    navigateToRetDetailsActivity();

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void navigateToRetDetailsActivity(){
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(PriceInfoActivity.this,CustomerDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
}
