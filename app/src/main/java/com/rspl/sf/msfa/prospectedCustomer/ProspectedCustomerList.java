package com.rspl.sf.msfa.prospectedCustomer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.RetailerListAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.CustomerBean;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by ${e10526} on ${15-11-2016}.
 *
 */
public class ProspectedCustomerList extends AppCompatActivity  {
    private RetailerListAdapter retailerAdapter = null;
    ListView lv_route_ret_list = null;

    //new TODo Below Code Values remove when Route plan list will come from table.
    String[] beatsArray = Constants.beatsArray;
    Spinner spBeatsPlan;
    String [] searchArray = {"Name","Code","City"};
    String [] searchCode = {"1","2","3"};
    private ProgressDialog pdLoadDialog;
    ArrayList<CustomerBean> alRetailerList;
    ArrayList<CustomerBean> localRetailerList;


    Spinner spSearchCode;
    EditText edNameSearch;
    String selectedSearchCode="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
        //ActionBarView.initActionBarView(this, true,getString(R.string.lbl_retailer_list));

        setContentView(R.layout.activity_retailer_list_for_ret_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_retailer_list), 0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
        ArrayAdapter<String> spBeatAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, beatsArray);
        spBeatAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spBeatsPlan.setAdapter(spBeatAdapter);

        spBeatsPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {
                clearEditTextSearchBox();
            }
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    /*
        TODO Get Retailer List Based On Route
     */
    private void getRetailerList() {
        try {
            alRetailerList = OfflineManager.getProspectiveCustomerList(Constants.ChannelPartners+"?$filter= not sap.islocal()"+"&$orderby="+Constants.RetailerName+"%20asc");
            localRetailerList=OfflineManager.getProspectiveCustomerList(Constants.ChannelPartners+ Constants.isLocalFilterQry+"&$orderby="+Constants.RetailerName+"%20asc");

       /*     for(int i=0;i<localRetailerList.size();i++) {


               CustomerBean customerBean = localRetailerList.get(i);

                int index = Collections.binarySearch(alRetailerList, customerBean,
                        new Comparator<CustomerBean>() {
                            public int compare(CustomerBean car1, CustomerBean car2) {
                                return car1.getCustomerName().compareToIgnoreCase(car2.getCustomerName());
                            }
                        });

                if (index < 0) {
                    index = (index * -1) - 1;
                }
                int index = Collections.binarySearch(alRetailerList, customerBean,
                        new Comparator<CustomerBean>() {
                            public int compare(CustomerBean car1, CustomerBean car2) {
                                return car1.getCustomerName().compareToIgnoreCase(car2.getCustomerName());
                            }
                        });
                alRetailerList.add(index,customerBean);
            }
*/

            alRetailerList.addAll(localRetailerList);
            Collections.sort(alRetailerList, new Comparator<CustomerBean>() {
                @Override
                public int compare(CustomerBean s1, CustomerBean s2) {
                    return s1.getCustomerName().compareToIgnoreCase(s2.getCustomerName());
                }
            });


        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProspectedCustomerList.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_competition_information).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        ProspectedCustomerList.this.finish();
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
            pdLoadDialog = new ProgressDialog(ProspectedCustomerList.this,R.style.ProgressDialogTheme);
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





        this.retailerAdapter = new RetailerListAdapter(this, alRetailerList,Constants.ProspectiveCustomerList);
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


