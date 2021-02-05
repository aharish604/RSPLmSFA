package com.rspl.sf.msfa.dbstock;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.DbStockDetailAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;

public class DBStockDetails extends AppCompatActivity {

    private ListView listDbStock;
    private ArrayList<DBStockBean> alDBStockList;
    private String mStrMaterialNo = "",mStrQuantity = "",mStrMaterialDesc="",mStrQuantityUnit,mStrMFD,mStrCPStockItemGUID;
    private DbStockDetailAdapter stockAdapter;
    private TextView tvMaterialDesc,tvMaterialNo,tvTotalQuantity,tv_crs_sku_grp_val;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbstock_details);
//        ActionBarView.initActionBarView(this,true,getString(R.string.title_dbstoxk_and_price));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_dbstoxk_and_price), 0);


        initUI();
        getDBStockDetails();

    }

    private void initUI()
    {
        listDbStock = (ListView)findViewById(R.id.listDbStock);
        alDBStockList = new ArrayList<>();
        mStrMaterialNo = getIntent().getExtras().getString(Constants.MaterialNo);
        mStrMaterialDesc = getIntent().getExtras().getString(Constants.MaterialDesc);
        mStrQuantity = getIntent().getExtras().getString(Constants.QAQty);
        mStrQuantityUnit = getIntent().getExtras().getString(Constants.UOM);
        mStrMFD = getIntent().getExtras().getString(Constants.ManufacturingDate);
        mStrCPStockItemGUID = getIntent().getExtras().getString(Constants.CPStockItemGUID);

        tvMaterialDesc = (TextView)findViewById(R.id.tv_material_desc);
        tvMaterialNo = (TextView)findViewById(R.id.tv_material_number);
        tvTotalQuantity = (TextView)findViewById(R.id.tv_total_quantity);
        tv_crs_sku_grp_val = (TextView)findViewById(R.id.tv_crs_sku_grp_val);
        tv_crs_sku_grp_val.setText("");
        tvMaterialDesc.setText(mStrMaterialDesc);
        tvMaterialNo.setText(mStrMaterialNo);

        tvTotalQuantity.setText(UtilConstants.removeLeadingZeroQuantity(mStrQuantity)+" "+mStrQuantityUnit);

    }
    private void getDBStockDetails()
    {
        try {


            String mStrMyStockQry= Constants.CPStockItemSnos+"?$filter="+ Constants.CPStockItemGUID+" eq guid'"+mStrCPStockItemGUID+"' ";
            alDBStockList = OfflineManager.getCPStockSNosList(mStrMyStockQry);
            displayDBStockList();
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
    }

    void displayDBStockList(){
        stockAdapter = new DbStockDetailAdapter(getApplicationContext(), alDBStockList);
        listDbStock.setAdapter(stockAdapter);
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
