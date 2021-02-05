package com.rspl.sf.msfa.reports;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.SalesOrderListAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.SalesOrderBean;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;

public class SalesOrderList extends AppCompatActivity {

    private String mStrBundleRetID = "",mStrBundleCPGUID="";
    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";
    private Bundle bundleExtras;
    TextView tvRetName = null, tvUID = null;
    private ListView lvSalesList;
    private ArrayList<SalesOrderBean> alSalesOrderBean;
    private ArrayList<SalesOrderBean> alTempSalesOrderBean;
    private com.rspl.sf.msfa.adapter.SalesOrderListAdapter salesOrderListAdapter = null;
    private EditText edSalesSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // ActionBarView.initActionBarView(this, true,getString(R.string.title_SalesOrderList));
        setContentView(R.layout.activity_sales_order_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_SalesOrderList), 0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
        }
        initUI();
        getSalesOrderList();
    }

    private void getSalesOrderList() {
        try {
           alSalesOrderBean = OfflineManager.getSalesOrderList(Constants.SOs+"?$filter="+ Constants.CustomerNo+" eq '"+mStrBundleRetID+"' " +
                    "and "+ Constants.OrderDate+" ge datetime'" + Constants.getLastMonthDate() + "' ");

            alTempSalesOrderBean =new ArrayList<>();
            alTempSalesOrderBean.addAll(alSalesOrderBean);
            lvSalesList.setEmptyView(findViewById(R.id.tv_empty_lay));
            salesOrderListAdapter = new SalesOrderListAdapter(SalesOrderList.this, R.layout.activity_invoice_history_list,alTempSalesOrderBean,bundleExtras);
            lvSalesList.setAdapter(salesOrderListAdapter);
            salesOrderListAdapter.notifyDataSetChanged();
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

    }

    private void initUI()
    {
        tvRetName = (TextView) findViewById(R.id.tv_bill_hist_ret_name);
        tvUID = (TextView) findViewById(R.id.tv_bill_hist_uid);
        tvRetName.setText(mStrBundleRetName);
        tvUID.setText(mStrBundleRetUID);
        lvSalesList = (ListView)findViewById(R.id.lv_sales_order_list);
        edSalesSearch = (EditText)findViewById(R.id.ed_sales_search);
        edSalesSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                salesOrderListAdapter.getFilter().filter(charSequence);
                salesOrderListAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }
}
