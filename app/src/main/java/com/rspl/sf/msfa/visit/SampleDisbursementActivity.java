package com.rspl.sf.msfa.visit;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.ActionBarView;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;

public class SampleDisbursementActivity extends AppCompatActivity {

    private Spinner sp_brand,sp_crs_sku_group,sp_item_desc;
    private ArrayList<String> brandSpinnerList,crsSkuSpinnerList,itemDescSpinnerList;
    private String[][] mArrayBrandTypeVal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_sample_disbursement);
        ActionBarView.initActionBarView(this, true,"");

        initalizeUI();
        for(int i=0;i<5;i++)
            brandSpinnerList.add("Item "+i);
        crsSkuSpinnerList.add("None");
        itemDescSpinnerList.add("None");

        initalizeSpinners();

        initSpinnerSelection();
        getBrandList();

    }

    private void initSpinnerSelection()
    {
        sp_brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addCRSSpinnerValues(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_crs_sku_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addItemDescSpinnerValues(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void addItemDescSpinnerValues(int pos)
    {
        itemDescSpinnerList.clear();
        for(int i=0;i<5;i++)
            itemDescSpinnerList.add("Item "+pos+" - "+i);

        ArrayAdapter<String> itemDescAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_textview, itemDescSpinnerList);
        itemDescAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_item_desc.setAdapter(itemDescAdapter);
    }

    private void addCRSSpinnerValues(int pos)
    {

        crsSkuSpinnerList.clear();
        for(int i=0;i<5;i++)
            crsSkuSpinnerList.add("Item "+pos+" - "+i);

        ArrayAdapter<String> crsSkudapter = new ArrayAdapter<String>(this,
                R.layout.custom_textview, crsSkuSpinnerList);
        crsSkudapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_crs_sku_group.setAdapter(crsSkudapter);
    }

    private void initalizeSpinners()
    {
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_textview, brandSpinnerList);
        brandAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_brand.setAdapter(brandAdapter);
        ArrayAdapter<String> crsSkudapter = new ArrayAdapter<String>(this,
                R.layout.custom_textview, crsSkuSpinnerList);
        crsSkudapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_crs_sku_group.setAdapter(crsSkudapter);
        ArrayAdapter<String> itemDescAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_textview, itemDescSpinnerList);
        itemDescAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_item_desc.setAdapter(itemDescAdapter);

    }

    private void initalizeUI()
    {
      /*  sp_brand = (Spinner) findViewById(R.id.sp_brand);
        sp_crs_sku_group = (Spinner) findViewById(R.id.sp_crs_sku_group);
        sp_item_desc = (Spinner) findViewById(R.id.sp_item_desc);
        brandSpinnerList = new ArrayList<String>();
        crsSkuSpinnerList = new ArrayList<String>();
        itemDescSpinnerList = new ArrayList<String>();*/
    }
    public void getBrandList()
    {
        try {
            String mStrConfigQry = Constants.Brands;
            mArrayBrandTypeVal = OfflineManager.getBrandListValues(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        if(mArrayBrandTypeVal==null)
        {
            mArrayBrandTypeVal = new String[4][1];
            mArrayBrandTypeVal[0][0]="";
            mArrayBrandTypeVal[1][0]="";
        }
        else
        {
            mArrayBrandTypeVal[1][0]="All";
        }
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_textview, mArrayBrandTypeVal[1]);
        brandAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_brand.setAdapter(brandAdapter);
    }


}
