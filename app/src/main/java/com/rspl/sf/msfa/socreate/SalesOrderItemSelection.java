package com.rspl.sf.msfa.socreate;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.MaterialsBean;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;
import java.util.Iterator;

public class SalesOrderItemSelection extends AppCompatActivity {

     String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "",mStrBundleCPGUID32="";
    private String mStrBundleRetailerUID = "",mStrComingFrom="";
    private String[][] mArrayMaterialGroups;
     Spinner spMaterialGroups;
    ArrayAdapter<String> materialAdapter;
    ArrayList<MaterialsBean> mArrayMaterialList;
    private LinearLayout llMaterialListContainer;
     EditText etMaterialSearch;
     ArrayList<MaterialsBean> filteredMaterialList;
    ArrayList<MaterialsBean> selectedMaterialList;
    ArrayList<String> checkedMaterials;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order_item_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, "Sales Order Create",0);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null)
        {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
        }
        initUI();
        getMaterialGroupValues();
    }

    private void getMaterialGroupValues()
    {
        String qry = Constants.Materials;
        try {
            mArrayMaterialGroups = OfflineManager.geMaterialGroupValues(qry);
            if (mArrayMaterialGroups == null)
            {
                mArrayMaterialGroups = new String[4][1];
                mArrayMaterialGroups[0][0] = Constants.None;
                mArrayMaterialGroups[1][0] = Constants.None;;
            }
            spMaterialGroups = (Spinner)findViewById(R.id.sp_soi_material_group);
            materialAdapter = new ArrayAdapter<>(this,
                    R.layout.custom_textview, mArrayMaterialGroups[1]);
            materialAdapter.setDropDownViewResource(R.layout.spinnerinside);
            spMaterialGroups.setAdapter(materialAdapter);
            spMaterialGroups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    if(!mArrayMaterialGroups[0][i].equals(Constants.None))
                            getMaterialList(mArrayMaterialGroups[0][i]);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void getMaterialList(String matGroupID)
    {

        String query = "";
        if(matGroupID.equals(Constants.All))
             query = Constants.Materials;
        else
             query = Constants.Materials+"?$filter= "+ Constants.MaterialGrp+" eq '"+matGroupID+"'";
        try {
            mArrayMaterialList = OfflineManager.getMaterialList(query);
            if(mArrayMaterialList!=null)
            {

                loadItemList(mArrayMaterialList);
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void initUI()
    {
        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);
        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetID);
        checkedMaterials = new ArrayList<>();
        selectedMaterialList = new ArrayList<MaterialsBean>();
        llMaterialListContainer = (LinearLayout)findViewById(R.id.ll_material_container);
        etMaterialSearch = (EditText)findViewById(R.id.et_material_search);


        etMaterialSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int i, int i1, int i2) {
                    filterMaterialList(cs);
//               / loadItemList(filteredMaterialList);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void filterMaterialList(CharSequence cs)
    {
        filteredMaterialList = new ArrayList<>();
        for (int j = 0; j < mArrayMaterialList.size(); j++) {
            MaterialsBean item = mArrayMaterialList.get(j);

            if (item.getMaterialDesc().toLowerCase()
                    .contains(cs.toString().toLowerCase().trim())) {
                filteredMaterialList.add(item);
            }
            else if(checkedMaterials.contains(item.getMaterialNo()))
            {
                filteredMaterialList.add(item);
            }


        }
        loadItemList(filteredMaterialList);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_back_next, menu);


        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.menu_next:
                onNextNavigation();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private void onNextNavigation()
    {
        Intent intentFeedBack = new Intent(SalesOrderItemSelection.this,SalesOrderReviewItems.class);
        intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetID);
        intentFeedBack.putExtra(Constants.CPUID, mStrBundleCPGUID);
        intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentFeedBack.putParcelableArrayListExtra(Constants.Materials,selectedMaterialList);

        startActivity(intentFeedBack);
    }

    private void loadItemList(ArrayList<MaterialsBean> filteredArrayList) {

        llMaterialListContainer.removeAllViews();
        for (int matInx = 0; matInx < filteredArrayList.size(); matInx++) {
            final MaterialsBean bean = filteredArrayList.get(matInx);


            RelativeLayout rowRelativeLayout = (RelativeLayout) LayoutInflater
                    .from(this).inflate(R.layout.so_items_sel_screen, null);

            CheckBox cbMaterial = (CheckBox) rowRelativeLayout
                    .findViewById(R.id.so_checkBx);

            TextView tvMatrCode = (TextView) rowRelativeLayout

                    .findViewById(R.id.item_create_value);

            TextView tvMatrDesc = (TextView) rowRelativeLayout

                    .findViewById(R.id.item_create_value1);

            tvMatrCode.setText(bean.getMaterialDesc() );
            tvMatrDesc.setText( bean.getMaterialNo());
            if (checkedMaterials.contains(bean.getMaterialNo())) {
                cbMaterial.setChecked(true);
            }


            cbMaterial
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton arg0,
                                                     boolean isChecked) {

                            if (isChecked) {

                                Constants.mapCheckedStateHashMap
                                        .put(bean.getMaterialNo(), "Y");

                                Constants.mapEnteredMaterialDescHashTable.put(bean.getMaterialNo(),
                                        bean.getMaterialDesc());
                                Constants.mapEnteredMatrialUOMHashTable.put(bean.getMaterialNo(), bean.getBaseUom());
                                checkedMaterials.add(bean.getMaterialNo());
                                selectedMaterialList.add(bean);
                            } else {
                                Constants.mapCheckedStateHashMap.remove(bean.getMaterialNo());
                                Constants.mapEnteredMaterialDescHashTable.remove(bean.getMaterialNo());
                                Constants.mapEnteredMatrialUOMHashTable.remove(bean.getMaterialNo());
                                checkedMaterials.remove(bean.getMaterialNo());
                                selectedMaterialList.remove(bean);
                            }
                        }
                    });
            llMaterialListContainer.addView(rowRelativeLayout);

            TableRow rowLine = (TableRow) LayoutInflater.from(this)
                    .inflate(R.layout.line_row, null);
            llMaterialListContainer.addView(rowLine);
        }


    }

    private void onInsertItems() {
        // TODO Auto-generated method stub
        int val = 0;
        Constants.selMaterialList = new ArrayList<MaterialsBean>();
       // matrialNoArraList =new ArrayList<String>();
        MaterialsBean itemLst;
        int incrementValue = 0;
        Iterator mapSelctedValues = Constants.mapEnteredMaterialDescHashTable.keySet()
                .iterator();
        while (mapSelctedValues.hasNext()) {
            String Key = (String) mapSelctedValues.next();
            itemLst = new MaterialsBean();
            itemLst.setMaterialNo(Key);
            itemLst.setMaterialDesc( Constants.mapEnteredMaterialDescHashTable
                    .get(Key));
            itemLst.setMaterialGrp( Constants.mapEnteredMaterialGroupHashTable
                    .get(Key));
//			itemLst.setBrand((String) Constants.mapEnteredBrandHashTable
//					.get(Key));
            itemLst.setBaseUom(Constants.mapEnteredMatrialUOMHashTable
                    .get(Key));


            incrementValue += 10;
            itemLst.setMaterialNo("" + incrementValue);
            Constants.selMaterialList.add(itemLst);
           // matrialNoArraList.add(Key);
        }
    }
}
