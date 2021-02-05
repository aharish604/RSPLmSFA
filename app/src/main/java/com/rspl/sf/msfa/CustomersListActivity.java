package com.rspl.sf.msfa;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.adapter.RetailerListAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.CustomerBean;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by ${e10526} on ${15-11-2016}.
 *
 */
public class CustomersListActivity extends AppCompatActivity  {
    private RetailerListAdapter retailerAdapter = null;
    ListView lv_route_ret_list = null;

    //new TODo Below Code Values remove when Route plan list will come from table.
    String[] beatsArray = Constants.beatsArray;
    Spinner spBeatsPlan;
    String [] searchArray = {"Name","Code","City"};
    String [] searchCode = {"1","2","3"};
    private ProgressDialog pdLoadDialog;
    ArrayList<CustomerBean> alRetailerList;


    Spinner spSearchCode;
    EditText edNameSearch;
    String selectedSearchCode="";
    private String salesArea = "";
    private String Cno = "";
     String ComeFrom = "";
    String shipToQuery = "";
    private String[][] arrayRouteVal;
    private String mStrRouteID = "",mStrRouteSchGuid="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
      //  ActionBarView.initActionBarView(this, true,getString(R.string.lbl_retailer_list));
        setContentView(R.layout.activity_retailer_list_for_ret_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_retailer_list), 0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

//        Bundle extra = getIntent().getExtras();
//        if (extra != null) {
//
//
////            Cno = extra.getString("custNo");
////            salesArea = extra.getString("SalesArea");
//            ComeFrom = extra.getString(Constants.EXTRA_COME_FROM);
//            shipToQuery = extra.getString("SHIP_TO_QUERY");
//
//        }

        onInitUI();
        setValuesToUI();
        loadAsyncTask();
    }

    private void loadAsyncTask(){

        loadSpinner();
        try {
            new GetRetailerList().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
            * TODO This method initialize UI
            */
    private void onInitUI() {
        lv_route_ret_list = (ListView) findViewById(R.id.lv_route_ret_list);
        spBeatsPlan = (Spinner) findViewById(R.id.spnr_beat_list);

        spSearchCode = (Spinner)findViewById(R.id.spnr_customer_search_list);
        ArrayAdapter<String> searchadapter = new ArrayAdapter<>(this, R.layout.custom_textview, searchArray);
        searchadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSearchCode.setAdapter(searchadapter);


        edNameSearch = (EditText) findViewById(R.id.et_name_search);

    }

    private void clearEditTextSearchBox(){
        if(edNameSearch!=null && edNameSearch.getText().toString().length()>0)
            edNameSearch.setText("");
    }

    /*
    TODO This method set values to UI
    */
    private void setValuesToUI() {
        getRouteNames();
        ArrayAdapter<String> spBeatAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, arrayRouteVal[1]);
        spBeatAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spBeatsPlan.setAdapter(spBeatAdapter);

        spBeatsPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {

                mStrRouteID = arrayRouteVal[0][position];
                mStrRouteSchGuid = arrayRouteVal[2][position];
                loadAsyncTask();

            }
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    /*get Route Names*/
    private  void getRouteNames(){
        try{
            arrayRouteVal = OfflineManager.getBeatPlanArray(Constants.RouteSchedules );
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.Error+" : " + e.getMessage());
        }

        if(arrayRouteVal ==null){
            arrayRouteVal = new String[3][1];
            arrayRouteVal[0][0]="";
            arrayRouteVal[1][0]="";
            arrayRouteVal[2][0]="";
        }
    }

    /*
        TODO Get Retailer List Based On Route
     */
    private void getRetailerList() {
        try {

            if(mStrRouteID.equalsIgnoreCase(Constants.All)){
                alRetailerList =OfflineManager.getCustomerList(Constants.Customers+"?$orderby="+Constants.RetailerName+"%20asc");
            }else{
                alRetailerList = OfflineManager.getRetListByRouteSchudule(Constants.RouteSchedulePlans+ "?$filter="
                        +Constants.RouteSchGUID+" eq guid'"+mStrRouteSchGuid.toUpperCase()+"'");
            }


        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_back:
                onBackPressed();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
    /*AsyncTask to get Retailers List*/
    private class GetRetailerList extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(CustomersListActivity.this,R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {

            getRetailerList();

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                pdLoadDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            displayRetailerList();
        }
    }

    private void displayRetailerList(){
        this.retailerAdapter = new RetailerListAdapter(this, alRetailerList,Constants.RetailerList);
        lv_route_ret_list.setEmptyView(findViewById(R.id.tv_empty_lay));
        lv_route_ret_list.setAdapter(this.retailerAdapter);
        //this.retailerAdapter.notifyDataSetChanged();
        edNameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int i, int i1, int i2) {
                retailerAdapter.getFilter(selectedSearchCode).filter(cs); //Filter from my adapter

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void loadSpinner(){
        spSearchCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {
                selectedSearchCode = searchCode[position];
                edNameSearch.setText("");

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }
}


