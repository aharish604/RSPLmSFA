package com.rspl.sf.msfa.so;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.SOItemAdapter;
import com.rspl.sf.msfa.common.ActionBarView;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.MyUtils;
import com.rspl.sf.msfa.interfaces.CheckBoxInterface;
import com.rspl.sf.msfa.interfaces.DialogCallBack;
import com.rspl.sf.msfa.mbo.SalesOrderBean;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SOItemActivity extends AppCompatActivity  implements CheckBoxInterface {
    private String salesArea = "";
    private String searchStr[] = {"Desc", "Code"};
    private Spinner spSearch;
    private String selectedSearchType;
    private EditText etSearchInput;
    private List<SOItemBean> soItemBeanList = new ArrayList<>();
    private ArrayList<SOItemBean> tempSOItemBeanList = new ArrayList<>();
    private ArrayList<SOItemBean> filteredArraylist=null;
    private TextView tvItemCount;
    private HashMap<String, String> headerDetail;
    private RecyclerView recyclerView;
    private SOItemAdapter soItemAdapter;
    private LinearLayout llMatLayout;
    boolean flag = true;
    int cursorLength = 0;

    TextView[] matNoDesc;
    CheckBox[] selectedMatcb;

    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    String mStrComingFrom = "";
    boolean isComingFromChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_so_item);
       // ActionBarView.initActionBarView(this, true, getString(R.string.title_sales_order_item));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_sales_order_item), 0);
        spSearch = (Spinner) findViewById(R.id.spSearch);
        etSearchInput = (EditText) findViewById(R.id.etSearchInput);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        soItemAdapter = new SOItemAdapter(SOItemActivity.this, soItemBeanList);
        recyclerView.setAdapter(soItemAdapter);
        soItemAdapter.onCheckSelected(this);
        tvItemCount = (TextView) findViewById(R.id.tv_item_count);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            salesArea = extra.getString("SalesArea");
            headerDetail = (HashMap<String, String>) extra.getSerializable("Header");
            mStrBundleRetID = extra.getString(Constants.CPNo);
            mStrBundleRetName = extra.getString(Constants.RetailerName);
            mStrComingFrom = extra.getString(Constants.comingFrom);
            isComingFromChange = extra.getBoolean(Constants.comingFromChange, false);
        }

        if(isComingFromChange)
            ActionBarView.initActionBarView(this, true, getString(R.string.title_sales_order_change));
        initUI();
    }

    void initUI(){
        ArrayAdapter<String> searchadapter = new ArrayAdapter<>(this, R.layout.custom_textview, searchStr);
        searchadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSearch.setAdapter(searchadapter);
        spSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {
                selectedSearchType = searchStr[position];
                etSearchInput.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        getMaterial();
        etSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                soItemAdapter.filter(cs+"", null, recyclerView, selectedSearchType);

               // filteredArraylist = new ArrayList<>();

//                for (int i = 0; i < soItemBeanList.size(); i++) {
//                    SOItemBean item = soItemBeanList.get(i);
//                    if(selectedSearchType.equalsIgnoreCase("Desc")){
//                        if(item.getMatDesc()!=null && !item.getMatDesc().equalsIgnoreCase("")){
//                            if (item.getMatDesc().toLowerCase()
//                                    .contains(cs.toString().toLowerCase().trim())) {
//                                filteredArraylist.add(item);
//
//                            }
//                        }
//
//
//                    }else{
//                        if(item.getMatCode()!=null && !item.getMatCode().equalsIgnoreCase("")) {
//                            if (item.getMatCode().toLowerCase()
//                                    .contains(cs.toString().toLowerCase().trim())) {
//                                filteredArraylist.add(item);
//
//                            }
//                        }
//                    }
//
//
//
//                }

               // getDisPlay(filteredArraylist);

            }

            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        if(isComingFromChange){
            String qry = Constants.SOItemDetails + "?$filter=" + Constants.SONo + " eq '" + headerDetail.get(Constants.OrderNo) + "'";
            ArrayList<SalesOrderBean> alSOItemList = new ArrayList();
            try {
                alSOItemList.clear();
                alSOItemList.addAll(OfflineManager.getSecondarySalesOrderDetailsList(qry));
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            for(SalesOrderBean SOItem :alSOItemList) {
                for (SOItemBean matItem : soItemBeanList){
                    if(SOItem.getMaterialNo().equalsIgnoreCase(matItem.getMatCode())){
                        matItem.setChecked(true);
                        matItem.setQuantity(SOItem.getQAQty());
                        tempSOItemBeanList.add(matItem);
                        break;
                    }
                }
            }
            soItemAdapter.notifyDataSetChanged();
        }
        tvItemCount.setText("" + tempSOItemBeanList.size());
    }

    /**
     * get material data
     */
    private void getMaterial() {
        try {
//            String strQuery = Constants.MaterialList + "?$filter= SalesArea eq '" + salesArea + "'";
            String strQuery = Constants.MaterialSaleAreas+"?$filter= SaleArea1 eq '"+salesArea+"' or SaleArea2 eq '"+salesArea+"' or SaleArea3 eq '"+salesArea+"' or SaleArea4 eq '"+salesArea+"' or SaleArea5 eq '"+salesArea+"' or SaleArea6 eq '"+salesArea+"' or SaleArea7 eq '"+salesArea+"' or SaleArea8 eq '"+salesArea+"' or SaleArea9 eq '"+salesArea+"' or SaleArea10 eq '"+salesArea+"'";
            soItemBeanList = OfflineManager.getSOItemCheckList(strQuery, soItemBeanList);
            soItemAdapter.filter("", null, recyclerView, selectedSearchType);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckedListener(CompoundButton buttonView, boolean isChecked, Object item) {
//        SOItemBean soItemBean = soItemBeanList.get(position);
        SOItemBean soItemBean = (SOItemBean) item;
        if (isChecked) {

            if (!tempSOItemBeanList.contains(soItemBean)) {
                tempSOItemBeanList.add(soItemBean);
            }
        } else {
            if (tempSOItemBeanList.contains(soItemBean)) {
                tempSOItemBeanList.remove(soItemBean);
            }
        }
        tvItemCount.setText("" + tempSOItemBeanList.size());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_back_next, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_next:
                //next step
                onNext();
                break;

        }
        return true;
    }

    /**
     * next step
     */
    private void onNext() {
        if (tempSOItemBeanList.size() > 0) {
            Intent soQt = new Intent(SOItemActivity.this, SOQuantityActivity.class);
            soQt.putExtra("items", tempSOItemBeanList);
            soQt.putExtra("Header", headerDetail);
            soQt.putExtra("SalesArea",salesArea);
            soQt.putExtra("Header", headerDetail);
            soQt.putExtra(Constants.CPNo, mStrBundleRetID);
            soQt.putExtra(Constants.RetailerName, mStrBundleRetName);
            soQt.putExtra(Constants.comingFrom, mStrComingFrom);
            soQt.putExtra(Constants.comingFromChange, isComingFromChange);
            startActivity(soQt);
        } else {
            MyUtils.dialogBoxWithButton(this, "",  "Please select at least one item", "Ok", "", new DialogCallBack() {
                @Override
                public void clickedStatus(boolean clickedStatus) {

                }
            });

        }
    }


//    public void getDisPlay(final ArrayList<SOItemBean> matList){
//
//        tempSOItemBeanList = new ArrayList<SOItemBean>();
//        // TODO Auto-generated method stub
//        if (!flag) {
//            llMatLayout.removeAllViews();
//        }
//        flag = false;
//
//
//        llMatLayout = (LinearLayout) findViewById(R.id.matlinerlayout);
//
//        TableLayout tableHeading = (TableLayout) LayoutInflater.from(this)
//                .inflate(R.layout.material_table, null);
//        cursorLength = matList.size();
//        matNoDesc = new TextView[cursorLength];
//
//
//        selectedMatcb = new CheckBox[cursorLength];
//
//
//
//        if (cursorLength > 0) {
//            for (int i = 0; i < cursorLength; i++) {
//                final SOItemBean soItemBean = matList.get(i);
//
//                final int selvalue = i;
//
//
//                LinearLayout rowRelativeLayout = (LinearLayout) LayoutInflater
//                        .from(this).inflate(R.layout.so_item_check, null);
//
//                matNoDesc[i] = (TextView) rowRelativeLayout
//                        .findViewById(R.id.tvMaterial);
//
//                selectedMatcb[i] = (CheckBox) rowRelativeLayout
//                        .findViewById(R.id.cbMaterial);
//
//                matNoDesc[i].setText(soItemBean.getMatCode()+" - "+soItemBean.getMatDesc());
//
//
//                selectedMatcb[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//
//                        if(isChecked){
//                            if (!tempSOItemBeanList.contains(soItemBean)) {
//                                tempSOItemBeanList.add(soItemBean);
//
//                            }
//
//                            for(int j= 0 ; j< matList.size();j++){
//
//                                if(!selectedMatcb[j].isChecked()){
//
//
//                                    selectedMatcb[j].setEnabled(false);
//                                }else{
//
//                                    selectedMatcb[j].setEnabled(true);
//                                }
//                            }
//
//
//                        }else{
//
//                            if (tempSOItemBeanList.contains(soItemBean)) {
//                                tempSOItemBeanList.remove(soItemBean);
//
//
//                                for(int j= 0 ; j< matList.size();j++){
//
//
//
//
//                                    selectedMatcb[j].setEnabled(true);
//
//                                }
//
//                            }
//
//                        }
//
//                        tvItemCount.setText("" + tempSOItemBeanList.size());
//
//                    }
//                });
//
//
//                View line = new View(this);
//                line.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 1));
//                line.setBackgroundColor(Color.rgb(51, 51, 51));
//
//
//                tableHeading.addView(rowRelativeLayout);
//                tableHeading.addView(line);
//            }
//            llMatLayout.addView(tableHeading);
//        }else{
//
//
//            LinearLayout rowRelativeLayout = (LinearLayout) LayoutInflater
//                    .from(this).inflate(R.layout.emptyrecord, null);
//            tableHeading.addView(rowRelativeLayout);
//            llMatLayout.addView(tableHeading);
//        }
//    }
}
