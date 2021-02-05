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
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.adapter.AdhocListAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.CustomerBean;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10526 on 12-11-2016.
 *
 */
public class AdhocListActivity extends AppCompatActivity {
    private AdhocListAdapter retailerAdapter = null;
    ListView lv_route_ret_list = null;
    TextView tv_distributor_name;

    //new TODo Below Code Values remove when Route plan list will come from table.
    String [] beatsArray = Constants.beatsArray;

    ArrayList<CustomerBean> alRetailerList= new ArrayList<>();
    ArrayList<CustomerBean> searchRetailerList = new ArrayList<>();
    private ProgressDialog pdLoadDialog;
    EditText edNameSearch;
    String selectedSearchCode="";
    Spinner spBeatsPlan;
    private String[][] arrayRouteVal;
    private String mStrRouteID = "",mStrRouteSchGuid="";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar without back button(false)
      //  ActionBarView.initActionBarView(this, true,getString(R.string.lbl_retailer_list));
        setContentView(R.layout.activity_retailer_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_adhoc_list), 0);
        lv_route_ret_list = (ListView)findViewById(R.id.lv_route_ret_list);
        spBeatsPlan = (Spinner)findViewById(R.id.spnr_beat_list);
        setValuesToUI();
        tv_distributor_name = (TextView) findViewById(R.id.tv_distributor_name);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        edNameSearch = (EditText) findViewById(R.id.et_name_search);
        edNameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence cs, int i, int i1, int i2) {
                if(retailerAdapter!=null) {
                    retailerAdapter.getFilter(selectedSearchCode).filter(cs); //Filter from my adapter
                }

            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
//        loadAsyncTask();
    }
    /*
 TODO This method set values to UI
 */
    private void setValuesToUI() {
        getRouteNames();
        ArrayAdapter<String> spBeatAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview,R.id.tvItemValue, arrayRouteVal[1]);
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
            arrayRouteVal[1][0]=Constants.None;
            arrayRouteVal[2][0]="";
        }
    }


    private void loadAsyncTask(){
        try {
            new GetRetailerList().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
   TODO Get Retailers List
*/
    private void getRetailerList(){
        try {

//            if(mStrRouteID.equalsIgnoreCase(Constants.All)){
                alRetailerList =OfflineManager.getCustomerList(Constants.Customers+"?$orderby="+Constants.RetailerName+"%20asc");
            /*}else{


                alRetailerList = OfflineManager.getRetListByRouteSchudule(Constants.RouteSchedulePlans+ "?$filter="
                        +Constants.RouteSchGUID+" eq guid'"+mStrRouteSchGuid.toUpperCase()+"'");
            }*/
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
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

    /*AsyncTask to get Retailers List*/
    private class GetRetailerList extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(AdhocListActivity.this,R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(true);
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
        retailerAdapter = new AdhocListAdapter(AdhocListActivity.this, alRetailerList);
        lv_route_ret_list.setEmptyView(findViewById(R.id.tv_empty_lay) );
        lv_route_ret_list.setAdapter(retailerAdapter);
       //retailerAdapter.notifyDataSetChanged();


    }

}
