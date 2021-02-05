package com.rspl.sf.msfa.prospectedCustomer;

import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.ProspectedCustomerAdapter;
import com.rspl.sf.msfa.common.ActionBarView;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;

public class ProspectedCustomerList_01 extends AppCompatActivity implements TextWatcher {

    private ProspectedCustomerAdapter retailerAdapter = null;
    ListView lv_route_ret_list = null;
    private String mStrRetNo = "",mStrVisitType="",mStrRouteKey="",mStrBeatType="";
    String [] searchArray = {"Name","Code","City"};
    String [] searchCode = {"1","2","3"};
    Spinner spSearchCode;
    EditText edNameSearch;
    String selectedSearchCode="";
    ArrayList<ProspectedCustomerBean> prospectedCustList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prospected_customer_list);
        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.lbl_prospected_customer_list));

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mStrRetNo = extras.getString(Constants.CPNo);
            mStrVisitType = extras.getString(Constants.VisitType);
            mStrBeatType = extras.getString(Constants.BeatType);

        }
        onInitUI();
        getRetailerList();

    }

    /*
             * TODO This method initialize UI
             */
    private void onInitUI(){
        lv_route_ret_list = (ListView)findViewById(R.id.lv_route_ret_list);
         edNameSearch = (EditText) findViewById(R.id.et_name_search);

        spSearchCode = (Spinner)findViewById(R.id.spnr_customer_search_list);

        ArrayAdapter<String> searchadapter = new ArrayAdapter<>(this, R.layout.custom_textview, searchArray);
        searchadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSearchCode.setAdapter(searchadapter);

        edNameSearch = (EditText) findViewById(R.id.et_name_search);
        edNameSearch.addTextChangedListener(this);


    }

    /*
       TODO Get Retailer List Based On Route
    */
    private void getRetailerList(){
        try {
//            String routeQry="";
//            if(mStrVisitType.equalsIgnoreCase(Constants.RouteBased)){
//                routeQry = Constants.ChannelPartners+"?$filter="+ Constants.RouteID+" eq '"+mStrRetNo+"' " +
//                        "&$orderby="+ Constants.RetailerName+"%20asc";
//            }else{
//                routeQry = Constants.ChannelPartners+"?$filter="+ Constants.CPNo+" eq '"+mStrRetNo+"' " +
//                        "&$orderby="+ Constants.RetailerName+"%20asc";
//
//            }

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


            getData();

//            List<CustomerBean> listRetailers= OfflineManager.getRetailerList(routeQry);
//            ArrayList<CustomerBean> alRetailerList;
//            alRetailerList = (ArrayList<CustomerBean>) listRetailers;

            try {
                prospectedCustList = OfflineManager.getProspectedCustomerList(Constants.Customers+"?$orderby="+Constants.RetailerName+"%20asc");

            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }

            this.retailerAdapter = new ProspectedCustomerAdapter(this, prospectedCustList);
            lv_route_ret_list.setEmptyView(findViewById(R.id.tv_empty_lay) );
            lv_route_ret_list.setAdapter(this.retailerAdapter);
           // this.retailerAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }



    public void getData(){
        try {

            prospectedCustList = new ArrayList<ProspectedCustomerBean>();

            ProspectedCustomerBean lb = null;

            Cursor prospectedCustVal = Constants.events
                    .getEvents(Constants.PROSPECTED_TABLE);
            if (prospectedCustVal != null) {
                while (prospectedCustVal.moveToNext()) {




                    String counterName = "";
                    String cpNo = "";
                    String counterType = "";
                    String contactPerson = "";
                    String mobileNo = "";
                    String address = "";
                    String city = "";
                    String district = "";
                    String taluka    = "";
                    String pinCode = "";
                    String block = "";
                    String totalTradepontential = "";
                    String totalNontradepotential = "";
                    String potentialAvilable = "";
                    String popDistributed = "";
                    String Remarks = "";


                    counterName = prospectedCustVal
                            .getString(prospectedCustVal
                                    .getColumnIndex("CounterName"));
                    counterType = prospectedCustVal
                            .getString(prospectedCustVal
                                    .getColumnIndex("CounterType"));

                    contactPerson = prospectedCustVal
                            .getString(prospectedCustVal
                                    .getColumnIndex("ContactPerson"));
                    mobileNo = prospectedCustVal
                            .getString(prospectedCustVal
                                    .getColumnIndex("PCMobileNo"));
                    address = prospectedCustVal
                            .getString(prospectedCustVal
                                    .getColumnIndex("ProspectecCustomerAddress"));

                    city = prospectedCustVal
                            .getString(prospectedCustVal
                                    .getColumnIndex("City"));
                    cpNo = prospectedCustVal
                            .getString(prospectedCustVal
                                    .getColumnIndex(Constants.CustomerNo));

                    district = prospectedCustVal.getString(prospectedCustVal
                            .getColumnIndex("PCDistrict")) != null ? prospectedCustVal
                            .getString(prospectedCustVal.getColumnIndex("PCDistrict"))
                            : "";



                    taluka = prospectedCustVal.getString(prospectedCustVal
                            .getColumnIndex("Taluka")) != null ? prospectedCustVal
                            .getString(prospectedCustVal.getColumnIndex("Taluka"))
                            : "";



                    pinCode = prospectedCustVal.getString(prospectedCustVal
                            .getColumnIndex("PinCode")) != null ? prospectedCustVal
                            .getString(prospectedCustVal.getColumnIndex("PinCode"))
                            : "";




                    block = prospectedCustVal.getString(prospectedCustVal
                            .getColumnIndex("Block")) != null ? prospectedCustVal
                            .getString(prospectedCustVal.getColumnIndex("Block"))
                            : "";




                    totalTradepontential = prospectedCustVal.getString(prospectedCustVal
                            .getColumnIndex("TotalTradePottential")) != null ? prospectedCustVal
                            .getString(prospectedCustVal.getColumnIndex("TotalTradePottential"))
                            : "";




                    totalNontradepotential = prospectedCustVal.getString(prospectedCustVal
                            .getColumnIndex("TotalNonTradePottential")) != null ? prospectedCustVal
                            .getString(prospectedCustVal.getColumnIndex("TotalNonTradePottential"))
                            : "";

                    potentialAvilable = prospectedCustVal.getString(prospectedCustVal
                            .getColumnIndex("PottentialAvailable")) != null ? prospectedCustVal
                            .getString(prospectedCustVal.getColumnIndex("PottentialAvailable"))
                            : "";


                    popDistributed = prospectedCustVal.getString(prospectedCustVal
                            .getColumnIndex("POPDistributed")) != null ? prospectedCustVal
                            .getString(prospectedCustVal.getColumnIndex("POPDistributed"))
                            : "";


                    Remarks = prospectedCustVal.getString(prospectedCustVal
                            .getColumnIndex("PCRemarks")) != null ? prospectedCustVal
                            .getString(prospectedCustVal.getColumnIndex("PCRemarks"))
                            : "";

                    if (counterName != null) {
                        lb = new ProspectedCustomerBean();
                        lb.setCustName(counterName);
                        lb.setCpNo(cpNo);
                        lb.setCounterType(counterType);
                        lb.setContactPerson(contactPerson);
                        lb.setMobNo(mobileNo);
                        lb.setAddress(address);
                        lb.setCity(city);
                        lb.setDistrict(district);
                        lb.setTaluka(taluka);
                        lb.setPincode(pinCode);
                        lb.setBlock(block);
                        lb.setTotalTrade(totalTradepontential);
                        lb.setTotalNonTrade(totalNontradepotential);
                        lb.setPotentialBg(potentialAvilable);
                        lb.setPopDitributed(popDistributed);
                        lb.setRemarks(Remarks);

                        prospectedCustList.add(lb);
                    }

                }
                prospectedCustVal.deactivate();
                prospectedCustVal.close();
            }

        } catch (Exception e) {
            // if (e != null)
            // LogController.getInstance(getApplicationContext()).E(
            // e.getMessage());

            String err = e.getMessage();

            //Toast.makeText(this, err, Toast.LENGTH_LONG).show();
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
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence cs, int start, int before, int count) {
        retailerAdapter.getFilter().filter(cs); //Filter from my adapter
        retailerAdapter.notifyDataSetChanged(); //Update my view
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}


