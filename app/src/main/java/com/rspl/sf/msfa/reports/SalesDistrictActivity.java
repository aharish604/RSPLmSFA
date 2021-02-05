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
import com.rspl.sf.msfa.adapter.SalesDistrictAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.MyUtils;
import com.rspl.sf.msfa.listeners.OnClickItem;
import com.rspl.sf.msfa.mbo.CustomDividerItemDecoration;
import com.rspl.sf.msfa.mbo.SaleDistrictBean;
import com.rspl.sf.msfa.so.SalesOrderHeaderViewActivity;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;
import java.util.List;

public class SalesDistrictActivity extends AppCompatActivity implements OnClickItem {


    private RecyclerView recyclerView;
    private TextView noDataFound;
    private List<SaleDistrictBean> salesDisrtictBean = new ArrayList<>();
    private List<SaleDistrictBean> newsalesDisrtictBean = new ArrayList<>();
    List<SaleDistrictBean> salesDistrictSearchList = new ArrayList<>();
    private SalesDistrictAdapter salesdistrictAdapter;
    private EditText searchView;
    private Spinner spinnerView;
    private String[] searchList = {"Name", "Code"};
    private String[] searchstatus = {"01", "02"};
    private String selectedsalesDistrioctCode = "01";
    String temp = "";
    private String exCustomer = "";
    private String exSalesarea = "";
    private String Cname = "";
    private String Cno = "";
    private ProgressDialog prgressDialog = null;
    private int saleDistrictpos;
    private int ordertypepos;
    private int meansOftransportpos;
    private int storeLocpos;
    private int incotermpos;
    private ArrayList<SaleDistrictBean> saleDistrictBeen;
    private ArrayList<String> salesDistCode;
    private ArrayList<String> salesDistDesc;
    private ArrayList<String> salesDistCodedesc;
    private String selSaleDistCode = "";
    private String selSaleDistDesc = "";
    private String[][] DefaultSalesDistrict = null;
    private String saledistId = "";
    private String saledistDesc = "";
    private String routeId = "";
    private String routeDesc = "";
    private String forwardingagentCode = "";
    private String forwardingagentDesc = "";
    private String incoterm2 = "";
    private String selSalesArea = "";
    private int processingFieldpos;
    private String shipToNum;
    private String shipToName;
    private boolean shipToSet;
    private int shippingPointpos;
    private int plantpos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_district);

        //ActionBarView.initActionBarView(this, true, getString(R.string.title_sales_district));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_sales_district), 0);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {

            Cname = extra.getString("custName");
            Cno = extra.getString("custNo");
            meansOftransportpos = extra.getInt("MeansTransportPos");
            storeLocpos = extra.getInt("StoreLocPos");
            ordertypepos = extra.getInt("OrderTypesPos");
            incotermpos = extra.getInt("IncotermPos");
            routeId = extra.getString("RouteCode");
            routeDesc = extra.getString("RouteDesc");
            forwardingagentCode = extra.getString("ForwardAgentCode");
            forwardingagentDesc = extra.getString("ForwardAgentDesc");
            incoterm2 = extra.getString("IncoTerm2");
            processingFieldpos = extra.getInt("ProcessingFieldPos");
            selSalesArea = extra.getString("SalesArea");
            shipToNum = extra.getString("ShipToNum");
            shipToName = extra.getString("ShipToName");
            shipToSet = extra.getBoolean("shipToSet");
            shippingPointpos = extra.getInt("ShippingPointPos");
            plantpos = extra.getInt("PlantPos");
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
        salesdistrictAdapter = new SalesDistrictAdapter(SalesDistrictActivity.this, newsalesDisrtictBean, salesDistrictSearchList);
        salesdistrictAdapter.onClickListener(this);
        recyclerView.setAdapter(salesdistrictAdapter);
        //get data and display
        new LoadData().execute();

    }


    /**
     * get data and display
     */
    private void getAndDisplayData() {

            boolean defaultIncoDine = false;

      String query = Constants.CustSlsAreas + "?$filter= CustomerNumber eq '" + Cno + "'"+ " and SalesArea eq '" + selSalesArea + "'";


        //String query = Constants.ValueHelps + "?$filter= PropName eq '" + "SalesDist" + "'";
          //  String query = Constants.CustSlsAreas + "?$filter= CustomerNumber eq '" + Cno + "'";

//            try {
//                DefaultSalesDistrict = OfflineManager.getDefaultSalesDistrictList(query);
//            } catch (OfflineODataStoreException e) {
//                e.printStackTrace();
//            }


            String query1 = Constants.ValueHelps + "?$filter= PropName eq '" + "SalesDistrict" + "'";

            try {
                saleDistrictBeen = OfflineManager.getSalesDistList(query1);

                salesDistCode = new ArrayList<>();
                salesDistDesc = new ArrayList<>();
                salesDistCodedesc = new ArrayList<>();


                int j = 0;
                for (int i = 0; i < saleDistrictBeen.size(); i++) {

//                    if (!defaultIncoDine) {
//                        salesDistCode.add(DefaultSalesDistrict[0][i]);
//                        salesDistDesc.add(DefaultSalesDistrict[1][i]);
//                        salesDistCodedesc.add(DefaultSalesDistrict[2][i]);
//                        defaultIncoDine = true;
//                    } else {
                        SaleDistrictBean bean = saleDistrictBeen.get(i);
                        saledistId = bean.getSaleDistCode().toString();
                        saledistDesc = bean.getSalesDistDesc().toString();
                        salesDistCode.add(saledistId);
                        salesDistDesc.add(saledistDesc);
                        salesDistCodedesc.add(saledistDesc + " - " + saledistId);
                       // j++;
                  //  }

                }
                    SaleDistrictBean temp;
                for (int i = 0; i < salesDistCodedesc.size() ; i++) {

                    temp = new SaleDistrictBean();
                    temp.setSaleDistCode(salesDistCode.get(i));
                    temp.setSalesDistDesc(salesDistDesc.get(i));

                        newsalesDisrtictBean.add(temp);
                }
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
                selectedsalesDistrioctCode = searchstatus[position];
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

                salesdistrictAdapter.filter(s + "", noDataFound, recyclerView, selectedsalesDistrioctCode);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        salesdistrictAdapter.filter("", noDataFound, recyclerView, selectedsalesDistrioctCode);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SalesDistrictActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_competition_information).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        SalesDistrictActivity.this.finish();
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

        SaleDistrictBean salesDistrictBean = salesDistrictSearchList.get(position);

        Intent intent = new Intent(this, SalesOrderHeaderViewActivity.class);
        intent.putExtra("SalesDistrictCode", salesDistrictBean.getSaleDistCode());
        intent.putExtra("SalesDistrictDesc", salesDistrictBean.getSalesDistDesc());
//        intent.putExtra("CustomerName", Cname);
//        intent.putExtra("customerNo", Cno);
//        intent.putExtra("MeansTransportPos",meansOftransportpos);
//        intent.putExtra("StoreLocPos",storeLocpos);
//        intent.putExtra("OrderTypesPos",ordertypepos);
//        intent.putExtra("IncotermPos",incotermpos);
//        intent.putExtra("RouteCode",routeId);
//        intent.putExtra("RouteDesc",routeDesc);
//        intent.putExtra("ForwardingAgentCode",forwardingagentCode);
//        intent.putExtra("ForwardingAgentDesc",forwardingagentDesc);
//        intent.putExtra("ProcessingFieldPos",processingFieldpos);
//        intent.putExtra("IncoTerm2",incoterm2);
//        intent.putExtra("ShipToNum",shipToNum);
//        intent.putExtra("ShipToName",shipToName);
//        intent.putExtra("shipToSet", shipToSet);
//        intent.putExtra("ShippingPointPos",shippingPointpos);
//        intent.putExtra("PlantPos",plantpos);
        setResult(3, intent);
        finish();
    }


    class LoadData extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(prgressDialog==null) {
                prgressDialog = MyUtils.showProgressDialog(SalesDistrictActivity.this, "", getString(R.string.msg_sync_progress_msg_plz_wait));
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
