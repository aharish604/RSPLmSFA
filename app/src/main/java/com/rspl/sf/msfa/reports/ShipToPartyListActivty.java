package com.rspl.sf.msfa.reports;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.ShipToPartyAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.MyUtils;
import com.rspl.sf.msfa.listeners.OnClickItem;
import com.rspl.sf.msfa.mbo.CustomDividerItemDecoration;
import com.rspl.sf.msfa.mbo.ShipToPartyListBean;
import com.rspl.sf.msfa.so.SalesOrderHeaderViewActivity;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10604 on 16/5/2017.
 */

public class ShipToPartyListActivty extends AppCompatActivity implements OnClickItem {

    private RecyclerView recyclerView;
    private TextView noDataFound;
    private List<ShipToPartyListBean> shipToBean = new ArrayList<>();
    List<ShipToPartyListBean> shipToSearchList = new ArrayList<>();
    private ShipToPartyAdapter shipToAdapter;
    private EditText searchView;
    private Spinner spinnerView;
    private String[] searchList = {"Name", "Code"};
    private String[] searchstatus = {"01", "02"};
    private String selectedShipToCode = "01";
    String temp ="";
    private String exCustomer = "";
    private String exSalesarea = "";
    private String Cname = "";
    private String salesArea = "";
    private String customerNum = "";
    private String saledistId = "";
    private String saledistDesc = "";
    private String routeId = "";
    private String routeDesc = "";
    private ProgressDialog prgressDialog = null;
    private int saleDistrictpos;
    private int ordertypepos;
    private int meansOftransportpos;
    private int storeLocpos;
    private int incotermpos;
    private int processingFieldpos;
    private String incoterm2;
    private String shipToNum;
    private String shipToName;
    private boolean shipToSet;
    private int comeFrom = 0;
    private int pos = 0;
    private int shippingPointpos;
    private int plantpos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ship_to_list);
       // ActionBarView.initActionBarView(this, true, getString(R.string.title_ship_to));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_ship_to), 0);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {


            customerNum = extra.getString("custNo");
            salesArea = extra.getString("SalesArea");
//            saleDistrictpos =  extra.getInt("SalesDistrictPos");
//            meansOftransportpos =  extra.getInt("MeansTransportPos");
//            storeLocpos =  extra.getInt("StoreLocPos");
//            ordertypepos =  extra.getInt("OrderTypesPos");
//            incotermpos =  extra.getInt("IncotermPos");
//            routeId = extra.getString("RouteCode");
//            routeDesc = extra.getString("RouteDesc");
//            saledistId = extra.getString("SalesDistrictCode");
//            saledistDesc = extra.getString("SalesDistrictDesc");
//            processingFieldpos = extra.getInt("ProcessingFieldPos");
//            incoterm2 = extra.getString("IncoTerm2");
//            shipToNum = extra.getString("ShipToNum");
//            shipToName = extra.getString("ShipToName");
//            shipToSet = extra.getBoolean("shipToSet");
//            shippingPointpos = extra.getInt("ShippingPointPos");
//            plantpos = extra.getInt("PlantPos");
        }


        Intent intent = getIntent();
        if (intent != null) {
            comeFrom = intent.getIntExtra(Constants.EXTRA_COME_FROM, 0);
            pos = intent.getIntExtra("pos", 0);



        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        searchView = (EditText) findViewById(R.id.search_view);
        noDataFound = (TextView) findViewById(R.id.tv_empty_lay);
        spinnerView = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, searchList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.recycler_divider);
        recyclerView.addItemDecoration(new CustomDividerItemDecoration(dividerDrawable));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        shipToAdapter = new ShipToPartyAdapter(ShipToPartyListActivty.this, shipToBean, shipToSearchList);
        shipToAdapter.onClickListener(this);
        recyclerView.setAdapter(shipToAdapter);
        //get data and display
        new LoadData().execute();

    }


    /**
     * get data and display
     */
    private void getAndDisplayData() {
        try {
            shipToBean.clear();



            String mStrConfigQry = Constants.CustomerPartnerFunctions + "?$filter=" + Constants.CustomerNo +
                    " eq '" + customerNum + "' and SalesArea eq '" + salesArea +
                    "' and PartnerFunctionID eq 'SH' &$orderby = PartnerCustomerName asc";

            shipToBean = OfflineManager.getShipToParty(mStrConfigQry, shipToBean);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

    }


    /**
     * register listener
     */
    private void registerListener() {
        spinnerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {
                selectedShipToCode = searchstatus[position];
                searchView.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                shipToAdapter.filter(s + "", noDataFound, recyclerView, selectedShipToCode);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        shipToAdapter.filter("", noDataFound, recyclerView, selectedShipToCode);
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShipToPartyListActivty.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_competition_information).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        ShipToPartyListActivty.this.finish();
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
        return true;
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

    /**
     * item onclick listener
     *
     * @param view
     * @param position
     */
    @Override
    public void onClickItem(View view, int position) {

        ShipToPartyListBean shiptopartyBean = shipToSearchList.get(position);


        Intent intent = new Intent(this, SalesOrderHeaderViewActivity.class);
        intent.putExtra("ShipToCode", shiptopartyBean.getShipToPartyCode());
        intent.putExtra("ShipToDesc", shiptopartyBean.getShipToPartyDesc());

//        intent.putExtra("CustomerName", Cname);
//        intent.putExtra("customerNo", Cno);
//        intent.putExtra("SalesDistrictPos",saleDistrictpos);
//        intent.putExtra("MeansTransportPos",meansOftransportpos);
//        intent.putExtra("StoreLocPos",storeLocpos);
//        intent.putExtra("OrderTypesPos",ordertypepos);
//        intent.putExtra("IncotermPos",incotermpos);
//        intent.putExtra("RouteCode",routeId);
//        intent.putExtra("RouteDesc",routeDesc);
//        intent.putExtra("SalesDistrictCode",saledistId);
//        intent.putExtra("SalesDistrictDesc",saledistDesc);
//        intent.putExtra("ProcessingFieldPos",processingFieldpos);
//        intent.putExtra("IncoTerm2",incoterm2);
//        intent.putExtra("ShipToNum",shipToNum);
//        intent.putExtra("ShipToName",shipToName);
//        intent.putExtra("shipToSet", shipToSet);
//        intent.putExtra("ShippingPointPos",shippingPointpos);
//        intent.putExtra("PlantPos",plantpos);
        //startActivity(intent);
        setResult(1, intent);
        finish();

    }




    class LoadData extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgressDialog = MyUtils.showProgressDialog(ShipToPartyListActivty.this, "", getString(R.string.app_loading));
        }

        @Override
        protected Void doInBackground(Void... params) {
            getAndDisplayData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(prgressDialog!=null){
                prgressDialog.dismiss();
            }
            registerListener();

        }
    }
}
