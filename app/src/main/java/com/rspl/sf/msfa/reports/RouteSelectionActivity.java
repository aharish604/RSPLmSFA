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

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.RouteSelectionAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.MyUtils;
import com.rspl.sf.msfa.listeners.OnClickItem;
import com.rspl.sf.msfa.mbo.CustomDividerItemDecoration;
import com.rspl.sf.msfa.mbo.RouteBean;
import com.rspl.sf.msfa.so.SalesOrderHeaderViewActivity;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;
import java.util.List;

public class RouteSelectionActivity extends AppCompatActivity implements OnClickItem {

    private RecyclerView recyclerView;
    private TextView noDataFound;
    List<RouteBean> routeSearchList = new ArrayList<>();
    private RouteSelectionAdapter routeAdapter;
    private EditText searchView;
    private Spinner spinnerView;
    private String[] searchList = {"Name", "Code"};
    private String[] searchstatus = {"01", "02"};
    private String selectedrouteCode = "01";
    private String Cname = "";
    private String Cno = "";
    private ProgressDialog prgressDialog = null;
    private int ordertypepos;
    private int meansOftransportpos;
    private int storeLocpos;
    private int incotermpos;
    private List<RouteBean> route = new ArrayList<>();
    private String selPlant = "";
    private String selSalesDistrictCode = "";
    private String selSalesDistrictDesc = "";
    private String forwardingagentCode = "";
    private String forwardingagentDesc = "";
    private String incoterm2 = "";
    private int processingFieldpos;
    private int shippingPointpos;
    private int plantpos;
    private String shipToNum;
    private String shipToName;
    private boolean shipToSet;
    private int comeFrom = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_selection);


       // ActionBarView.initActionBarView(this, true, getString(R.string.title_route));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_route), 0);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {

            Cname = extra.getString("custName");
            Cno = extra.getString("custNo");
            meansOftransportpos = extra.getInt("MeansTransportPos");
            storeLocpos = extra.getInt("StoreLocPos");
            ordertypepos = extra.getInt("OrderTypesPos");
            incotermpos = extra.getInt("IncotermPos");
            selPlant = extra.getString("Plant");
            selSalesDistrictCode = extra.getString("SalesDistrictCode");
            selSalesDistrictDesc = extra.getString("SalesDistrictDesc");
            forwardingagentCode = extra.getString("ForwardAgentCode");
            forwardingagentDesc = extra.getString("ForwardAgentDesc");
            processingFieldpos = extra.getInt("ProcessingFieldPos");
            incoterm2 = extra.getString("IncoTerm2");
            shipToNum = extra.getString("ShipToNum");
            shipToName = extra.getString("ShipToName");
            shipToSet = extra.getBoolean("shipToSet");
            shippingPointpos = extra.getInt("ShippingPointPos");
            plantpos = extra.getInt("PlantPos");
        }

        Intent intent = getIntent();
        if (intent != null) {
            comeFrom = intent.getIntExtra(Constants.EXTRA_COME_FROM, 0);



        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        searchView = (EditText) findViewById(R.id.search_view);
        noDataFound = (TextView) findViewById(R.id.tv_no_records_found);
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
        routeAdapter = new RouteSelectionAdapter(RouteSelectionActivity.this, route, routeSearchList);
        routeAdapter.onClickListener(this);
        recyclerView.setAdapter(routeAdapter);
        //get data and display
        new LoadData().execute();

    }

    /**
     * get data and display
     */
    private void getAndDisplayData() {

//        String query = Constants.Routes + "?$filter= Plant eq '" + selPlant + "'";

        String query = Constants.ValueHelps+ "?$filter= PropName eq '" + "Route" + "'";;
        try {
            route = OfflineManager.getRouteSearchList(query);


        } catch (Exception e) {
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
                selectedrouteCode = searchstatus[position];
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

                routeAdapter.filter(s + "", noDataFound, recyclerView, selectedrouteCode);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        routeAdapter.filter("", noDataFound, recyclerView, selectedrouteCode);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RouteSelectionActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_competition_information).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        RouteSelectionActivity.this.finish();
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

        RouteBean routeBean = routeSearchList.get(position);


//        if(comeFrom == 7) {
//            Intent intent = new Intent(this, DeliveryHeaderActivity.class);
//            intent.putExtra("RouteCode", routeBean.getRouteId());
//            intent.putExtra("RouteDesc", routeBean.getRouteDesc());
//            setResult(1, intent);
//            finish();
//
//        }else{

            Intent intent = new Intent(RouteSelectionActivity.this, SalesOrderHeaderViewActivity.class);
            intent.putExtra("RouteCode", routeBean.getRouteId());
            intent.putExtra("RouteDesc", routeBean.getRouteDesc());

//            intent.putExtra("CustomerName", Cname);
//            intent.putExtra("customerNo", Cno);
//            intent.putExtra("MeansTransportPos",meansOftransportpos);
//            intent.putExtra("StoreLocPos",storeLocpos);
//            intent.putExtra("OrderTypesPos",ordertypepos);
//            intent.putExtra("IncotermPos",incotermpos);
//            intent.putExtra("SalesDistrictCode",selSalesDistrictCode);
//            intent.putExtra("SalesDistrictDesc",selSalesDistrictDesc);
//            intent.putExtra("ForwardingAgentCode",forwardingagentCode);
//            intent.putExtra("ForwardingAgentDesc",forwardingagentDesc);
//            intent.putExtra("ProcessingFieldPos",processingFieldpos);
//            intent.putExtra("IncoTerm2",incoterm2);
//            intent.putExtra("ShipToNum",shipToNum);
//            intent.putExtra("ShipToName",shipToName);
//            intent.putExtra("shipToSet", shipToSet);
//            intent.putExtra("ShippingPointPos",shippingPointpos);
//            intent.putExtra("PlantPos",plantpos);
        setResult(4, intent);
            finish();

      //  }




    }


    class LoadData extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(prgressDialog==null) {
                prgressDialog = MyUtils.showProgressDialog(RouteSelectionActivity.this, "", getString(R.string.msg_sync_progress_msg_plz_wait));
            }
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
