package com.rspl.sf.msfa.dbstock;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;

public class DBStockActivity extends AppCompatActivity implements UIListener {

    private ArrayList<DBStockBean> alDBStockList = new ArrayList<>();
    ListView lvDBStock = null;
    EditText etSKUDescSearch = null;
    LinearLayout ltNoRecords = null;

    private String[][] mArrayBrandTypeVal, mArrayCateogryTypeVal, mArrayOrderedGroup;
    DBStockAdapter stockAdapter = null;
    TextView tv_last_sync_time_value;
    Menu menu = null;
    ProgressDialog syncProgDialog = null;
    boolean dialogCancelled = false;
    private Spinner sp_category, sp_brand, sp_crs_sku_group, sp_distributor;
    String concatCollectionStr = "";
    private Boolean isCatFirstTime = true, isBrandFirstTime = true;
    private String previousCategoryId = "", previousBrandId = "";
    private String mStrSelOrderMaterialID = "";
    ArrayList<String> alAssignColl = new ArrayList<>();
    ArrayAdapter<String> productCategoryAdapter;
    ArrayAdapter<String> brandAdapter;

    private String[][] distList = null;
    private String mStrSelDistGuid = Constants.None;
    private GUID refguid =null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_dbstock_rate_and_price);

        //Initialize action bar (without back button(false)/with back button(true))
//        ActionBarView.initActionBarView(this, true,getString(R.string.title_dbstoxk_and_price));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_dbstoxk_and_price), 0);


        initUI();
        getDistributor();

        getCategoryList();
        getBrandList();
        displayDistributorVal();
        getOrderedMaterials();

        getDBStockDetails();


    }

    //Lists the order materials from the CPSTOCKITEMS
    private void getOrderedMaterials() {

        try {
            String mStrConfigQry = Constants.OrderMaterialGroups;
            mArrayOrderedGroup = OfflineManager.getOrderedMaterialGroups(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        if (mArrayOrderedGroup == null) {
            mArrayOrderedGroup = new String[2][0];
            mArrayOrderedGroup[0][0] = "";
            mArrayOrderedGroup[1][0] = "";
        }


        ArrayAdapter<String> productOrderGroupAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, mArrayOrderedGroup[1]);
        productOrderGroupAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_crs_sku_group.setAdapter(productOrderGroupAdapter);
        sp_crs_sku_group
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


                    @Override
                    public void onItemSelected(AdapterView<?> parent, View arg1,
                                               int position, long arg3) {

                        mStrSelOrderMaterialID = mArrayOrderedGroup[0][position];
                    }

                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                });
    }

    private void updateBrandValuesInSpinner() {
        if (!previousCategoryId.equalsIgnoreCase(Constants.None)) {
            try {
                mArrayBrandTypeVal = OfflineManager.getBrandListValues(Constants.BrandsCategories + "?$filter= " + Constants.MaterialCategoryID + " eq '" + previousCategoryId + "'");
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            if (mArrayBrandTypeVal == null) {
                mArrayBrandTypeVal = new String[4][1];
                mArrayBrandTypeVal[0][0] = "";
                mArrayBrandTypeVal[1][0] = "";
            }
            brandAdapter = new ArrayAdapter<>(this,
                    R.layout.custom_textview, mArrayBrandTypeVal[1]);
            brandAdapter.setDropDownViewResource(R.layout.spinnerinside);
            sp_brand.setAdapter(brandAdapter);

            isBrandFirstTime = true;

            sp_brand.setSelection(getBrandValueIndexKey());


        } else {
            if (previousBrandId.equalsIgnoreCase(Constants.None) && !isBrandFirstTime) {
                isBrandFirstTime = true;
                try {
                    mArrayBrandTypeVal = OfflineManager.getBrandListValues(Constants.Brands);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                if (mArrayBrandTypeVal == null) {
                    mArrayBrandTypeVal = new String[4][1];
                    mArrayBrandTypeVal[0][0] = "";
                    mArrayBrandTypeVal[1][0] = "";
                }
                brandAdapter = new ArrayAdapter<>(this,
                        R.layout.custom_textview, mArrayBrandTypeVal[1]);
                brandAdapter.setDropDownViewResource(R.layout.spinnerinside);
                sp_brand.setAdapter(brandAdapter);

            }


        }


    }

    private void updateCategoryValuesInSpinner() {

        if (!previousBrandId.equalsIgnoreCase(Constants.None)) {
            try {
                String mStrConfigQry = Constants.BrandsCategories + "?$filter= " + Constants.BrandID + " eq '" + previousBrandId + "'";
                mArrayCateogryTypeVal = OfflineManager.getCategoryListValues(mStrConfigQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            if (mArrayCateogryTypeVal == null) {
                mArrayCateogryTypeVal = new String[2][1];
                mArrayCateogryTypeVal[0][0] = "";
                mArrayCateogryTypeVal[1][0] = "";
            }


            productCategoryAdapter = new ArrayAdapter<>(this,
                    R.layout.custom_textview, mArrayCateogryTypeVal[1]);
            productCategoryAdapter.setDropDownViewResource(R.layout.spinnerinside);
            sp_category.setAdapter(productCategoryAdapter);
            isCatFirstTime = true;
            sp_category.setSelection(getCategoryValueIndexKey());


        } else {
            if (previousCategoryId.equalsIgnoreCase(Constants.None) && !isCatFirstTime) {
                isCatFirstTime = true;
                try {
                    String mStrConfigQry = Constants.MaterialCategories;
                    mArrayCateogryTypeVal = OfflineManager.getCategoryListValues(mStrConfigQry);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
                if (mArrayCateogryTypeVal == null) {
                    mArrayCateogryTypeVal = new String[2][1];
                    mArrayCateogryTypeVal[0][0] = "";
                    mArrayCateogryTypeVal[1][0] = "";
                }


                productCategoryAdapter = new ArrayAdapter<>(this,
                        R.layout.custom_textview, mArrayCateogryTypeVal[1]);
                productCategoryAdapter.setDropDownViewResource(R.layout.spinnerinside);
                sp_category.setAdapter(productCategoryAdapter);

            }

        }


    }

    private int getBrandValueIndexKey() {

        int index = -1;
        for (int i = 0; i < mArrayBrandTypeVal.length; i++) {
            if (mArrayBrandTypeVal[0][i].equals(previousBrandId)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private int getCategoryValueIndexKey() {
        int index = -1;
        for (int i = 0; i < mArrayCateogryTypeVal.length; i++) {
            if (mArrayCateogryTypeVal[0][i].equals(previousCategoryId)) {
                index = i;
                break;
            }
        }
        return index;
    }

    //Update order materials based on the brands and category
    private void updateOrderMaterialGroups() {
        if (previousCategoryId.equalsIgnoreCase(Constants.None) && previousBrandId.equalsIgnoreCase(Constants.None)) {
            try {
                String mStrConfigQry = Constants.OrderMaterialGroups;
                mArrayOrderedGroup = OfflineManager.getOrderedMaterialGroups(mStrConfigQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
        } else if (previousCategoryId.equalsIgnoreCase(Constants.None)) {
            try {
                String mStrConfigQry = Constants.OrderMaterialGroups + "?$filter=" + Constants.BrandID + " eq '" + previousBrandId + "'";
                mArrayOrderedGroup = OfflineManager.getOrderedMaterialGroups(mStrConfigQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
        } else if (previousBrandId.equalsIgnoreCase(Constants.None)) {
            try {
                String mStrConfigQry = Constants.OrderMaterialGroups + "?$filter=" + Constants.MaterialCategoryID + " eq '" + previousCategoryId + "'";
                mArrayOrderedGroup = OfflineManager.getOrderedMaterialGroups(mStrConfigQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
        } else {
            try {
                String mStrConfigQry = Constants.OrderMaterialGroups + "?$filter=" + Constants.MaterialCategoryID + " eq '" + previousCategoryId + "' and " + Constants.BrandID + " eq '" + previousBrandId + "'";
                mArrayOrderedGroup = OfflineManager.getOrderedMaterialGroups(mStrConfigQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
        }
        if (mArrayOrderedGroup == null) {
            mArrayOrderedGroup = new String[2][0];
            mArrayOrderedGroup[0][0] = "";
            mArrayOrderedGroup[1][0] = "";
        }


        ArrayAdapter<String> productOrderGroupAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, mArrayOrderedGroup[1]);
        productOrderGroupAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_crs_sku_group.setAdapter(productOrderGroupAdapter);
        sp_crs_sku_group
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


                    @Override
                    public void onItemSelected(AdapterView<?> parent, View arg1,
                                               int position, long arg3) {

                        mStrSelOrderMaterialID = mArrayOrderedGroup[0][position];
                        getDBStockDetails();
                    }

                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                });

    }

    //Lists of Category from the Material Categories to the Spinner Category list
    private void getCategoryList() {
        try {
            String mStrConfigQry = Constants.MaterialCategories;
            mArrayCateogryTypeVal = OfflineManager.getCategoryListValues(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        if (mArrayCateogryTypeVal == null) {
            mArrayCateogryTypeVal = new String[2][0];
            mArrayCateogryTypeVal[0][0] = "";
            mArrayCateogryTypeVal[1][0] = "";
        }


        productCategoryAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, mArrayCateogryTypeVal[1]);
        productCategoryAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_category.setAdapter(productCategoryAdapter);
        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {
                previousCategoryId = mArrayCateogryTypeVal[0][position];
                if (isCatFirstTime) {
                    isCatFirstTime = false;
                } else if (previousCategoryId.equalsIgnoreCase(Constants.None) && previousBrandId.equalsIgnoreCase(Constants.None)) {
                    resetSpinnervalues();
                } else {
                    updateBrandValuesInSpinner();

                }

                updateOrderMaterialGroups();

            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }

    //Lists of Category from the Brands to the Spinner List
    public void getBrandList() {
        try {
            String mStrConfigQry = Constants.Brands;
            mArrayBrandTypeVal = OfflineManager.getBrandListValues(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        if (mArrayBrandTypeVal == null) {
            mArrayBrandTypeVal = new String[4][1];
            mArrayBrandTypeVal[0][0] = "";
            mArrayBrandTypeVal[1][0] = "";
        }

        brandAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, mArrayBrandTypeVal[1]);
        brandAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_brand.setAdapter(brandAdapter);
        sp_brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                previousBrandId = mArrayBrandTypeVal[0][position];
                if (isBrandFirstTime) {
                    isBrandFirstTime = false;

                } else {

                    updateCategoryValuesInSpinner();

                }
                updateOrderMaterialGroups();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    //DBStock details from CPStockItems
    private void getDBStockDetails() {
        if (mStrSelOrderMaterialID.equalsIgnoreCase(Constants.None)) {
            try {
                String mStrMyStockQry = Constants.CPStockItems +
                        "?$filter=" + Constants.CPGUID + " eq '" + mStrSelDistGuid + "' ";
                alDBStockList = OfflineManager.getDBStockList(mStrMyStockQry);
                displayDBStockList();
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
            }
        } else {
            try {

                String mStrMyStockQry = Constants.CPStockItems + "?$filter=" + Constants.OrderMaterialGroupID + " eq '" + mStrSelOrderMaterialID + "'";
                alDBStockList = OfflineManager.getDBStockList(mStrMyStockQry);
                displayDBStockList();
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
            }
        }

    }

    //Distributors from SalesPersons
    private void getDistributor() {
        try {
            String mStrDistQry = Constants.SalesPersons + " ?$filter=" + Constants.CPTypeID + " eq '01' ";
            distList = OfflineManager.getDistributors(mStrDistQry);
            if (distList == null) {
                distList = new String[3][1];
                distList[0][0] = "";
                distList[1][0] = "";
                distList[2][0] = "";
            }


        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    private void resetSpinnervalues() {
        try {
            String mStrConfigQry = Constants.Brands;
            mArrayBrandTypeVal = OfflineManager.getBrandListValues(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        if (mArrayBrandTypeVal == null) {
            mArrayBrandTypeVal = new String[4][1];
            mArrayBrandTypeVal[0][0] = "";
            mArrayBrandTypeVal[1][0] = "";
        }

        brandAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, mArrayBrandTypeVal[1]);
        brandAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_brand.setAdapter(brandAdapter);

        try {
            String mStrConfigQry = Constants.MaterialCategories;
            mArrayCateogryTypeVal = OfflineManager.getCategoryListValues(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        if (mArrayCateogryTypeVal == null) {
            mArrayCateogryTypeVal = new String[2][0];
            mArrayCateogryTypeVal[0][0] = "";
            mArrayCateogryTypeVal[1][0] = "";
        }


        productCategoryAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, mArrayCateogryTypeVal[1]);
        productCategoryAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_category.setAdapter(productCategoryAdapter);
    }
    //Set updated brand values in the spinner


    void initUI() {
        tv_last_sync_time_value = (TextView) findViewById(R.id.tv_last_sync_time_value);
        tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.CPStockItems, Constants.TimeStamp, this));
        lvDBStock = (ListView) findViewById(R.id.lv_dbstk);
        etSKUDescSearch = (EditText) findViewById(R.id.et_dbstk_search);
        sp_category = (Spinner) findViewById(R.id.sp_dbskt_cat);
        sp_brand = (Spinner) findViewById(R.id.sp_dbskt_brand);
        sp_crs_sku_group = (Spinner) findViewById(R.id.sp_dbskt_crs_sku_group);
        sp_distributor = (Spinner) findViewById(R.id.sp_distributor);
        ltNoRecords = (LinearLayout) findViewById(R.id.lay_no_records);

    }

    private void displayDistributorVal() {
        ArrayAdapter<String> productCategoryAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, distList[2]);
        productCategoryAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_distributor.setAdapter(productCategoryAdapter);

        sp_distributor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {
                mStrSelDistGuid = distList[1][position];
                getDBStockDetails();
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        if (distList[1].length == 1) {
            mStrSelDistGuid = distList[1][0];
            sp_distributor.setVisibility(View.GONE);
        }
    }


    void displayDBStockList() {
        if (alDBStockList.size() == 0)
            ltNoRecords.setVisibility(View.VISIBLE);
        else
            ltNoRecords.setVisibility(View.GONE);
        stockAdapter = new DBStockAdapter(getApplicationContext(), alDBStockList);
        lvDBStock.setEmptyView(findViewById(R.id.tv_empty_lay));
        lvDBStock.setAdapter(stockAdapter);
        etSKUDescSearch = (EditText) findViewById(R.id.et_dbstk_search);
        etSKUDescSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                stockAdapter.getFilter().filter(s);
                stockAdapter.notifyDataSetChanged();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    void onRefresh() {
        alAssignColl.clear();
        concatCollectionStr = "";
        alAssignColl.add(Constants.CPStockItems);

        for (int incVal = 0; incVal < alAssignColl.size(); incVal++) {
            if (incVal == 0 && incVal == alAssignColl.size() - 1) {
                concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
            } else if (incVal == 0) {
                concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
            } else if (incVal == alAssignColl.size() - 1) {
                concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
            } else {
                concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
            }
        }

        try {
            Constants.isSync = true;
            dialogCancelled = false;
            refguid = GUID.newRandom();
            Constants.updateStartSyncTime(DBStockActivity.this,Constants.DownLoad,Constants.StartSync,refguid.toString().toUpperCase());
            new LoadingData().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e, DBStockActivity.this);
        if (errorBean.hasNoError()) {
            if (operation == Operation.OfflineRefresh.getValue()) {
                try {
                    /*String syncTime = Constants.getSyncHistoryddmmyyyyTime();
                    for (int incReq = 0; incReq < alAssignColl.size(); incReq++) {

                        String colName = alAssignColl.get(incReq);
                        if (colName.contains("?$")) {
                            String splitCollName[] = colName.split("\\?");
                            colName = splitCollName[0];
                        }

                        Constants.events.updateStatus(Constants.SYNC_TABLE,
                                colName, Constants.TimeStamp, syncTime
                        );
                    }*/
                    Constants.updateSyncTime(alAssignColl,this,Constants.DownLoad,refguid.toString().toUpperCase());
                } catch (Exception exce) {
                    LogManager.writeLogError(Constants.SyncTableHistory + " " + exce.getMessage());
                }

                syncProgDialog.dismiss();
                Constants.isSync = false;
                if (!Constants.isStoreClosed) {
                    UtilConstants.showAlert(errorBean.getErrorMsg(), DBStockActivity.this);
                } else {
                    UtilConstants.showAlert(getString(R.string.msg_sync_terminated), DBStockActivity.this);
                }
            }
        } else {
            syncProgDialog.dismiss();
            Constants.isSync = false;
            Constants.displayMsgReqError(errorBean.getErrorCode(), DBStockActivity.this);
        }
    }

    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {

        if (operation == Operation.OfflineRefresh.getValue()) {
            try {
                OfflineManager.getAuthorizations(getApplicationContext());
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            try {
               /* String syncTime = Constants.getSyncHistoryddmmyyyyTime();
                for (int incReq = 0; incReq < alAssignColl.size(); incReq++) {
                    String colName = alAssignColl.get(incReq);
                    if (colName.contains("?$")) {
                        String splitCollName[] = colName.split("\\?");
                        colName = splitCollName[0];
                    }

                    Constants.events.updateStatus(Constants.SYNC_TABLE,
                            colName, Constants.TimeStamp, syncTime
                    );
                }*/
                Constants.updateSyncTime(alAssignColl,this,Constants.DownLoad,refguid.toString().toUpperCase());
            } catch (Exception exce) {
                LogManager.writeLogError(Constants.SyncTableHistory + " " + exce.getMessage());
            }

            tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.OutstandingInvoices, Constants.TimeStamp, this));

            syncProgDialog.dismiss();
            Constants.isSync = false;
            if (!Constants.isStoreClosed) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        DBStockActivity.this, R.style.MyTheme);
                builder.setMessage(getString(R.string.msg_sync_successfully_completed))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.CPStockItems, Constants.TimeStamp, DBStockActivity.this));
                                        getDBStockDetails();

                                    }
                                });

                builder.show();
            } else {
                UtilConstants.showAlert(getString(R.string.msg_sync_terminated), DBStockActivity.this);
            }
        }

    }

    /**
     * Adapter for displaying retailerList in ListView
     */
    public class DBStockAdapter extends BaseAdapter {

        Context context;
        LayoutInflater inflater;
        DBStockBean stock;
        private RetailerListFilter filter;
        private ArrayList<DBStockBean> dbStockOriginalValues = new ArrayList<>();
        private ArrayList<DBStockBean> dbStockDisplayValues = new ArrayList<>();

        DBStockAdapter(Context context, ArrayList<DBStockBean> items) {

            this.context = context;
            this.dbStockOriginalValues = items;
            this.dbStockDisplayValues = items;
        }

        @Override
        public int getCount() {
            return dbStockDisplayValues.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int pos, View view, ViewGroup arg2) {
            if (inflater == null) {
                inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            if (view == null) {
                view = inflater
                        .inflate(R.layout.item_dbstock, null, true);
            }
            stock = dbStockDisplayValues.get(pos);
            final TextView tvSKUDesc = (TextView) view
                    .findViewById(R.id.item_dbstk_sku_desc);
            TextView tvCRSSKUGroup = (TextView) view
                    .findViewById(R.id.item_dbstk_crs_sku_group);
            final TextView tvDBStock = (TextView) view
                    .findViewById(R.id.item_dbstk_dbstock);
            ImageView expandIcon = (ImageView) view.findViewById(R.id.iv_expand_icon);
            tvSKUDesc.setText(stock.getMaterialDesc());
            tvCRSSKUGroup.setText(stock.getOrderMaterialGroupDesc());
            tvDBStock.setText(UtilConstants.removeLeadingZeroQuantity(stock.getQAQty()) + " " + stock.getUom());
            expandIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToDbStockDetails(pos);
                }
            });


            return view;
        }

        public android.widget.Filter getFilter() {
            if (filter == null) {
                filter = new RetailerListFilter();
            }
            return filter;
        }

        private void goToDbStockDetails(int pos) {
            Intent intent = new Intent(context, DBStockDetails.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constants.MaterialNo, dbStockDisplayValues.get(pos).getMaterialNo());
            intent.putExtra(Constants.QAQty, dbStockDisplayValues.get(pos).getQAQty());
            intent.putExtra(Constants.UOM, dbStockDisplayValues.get(pos).getUom());
            intent.putExtra(Constants.MaterialDesc, dbStockDisplayValues.get(pos).getMaterialDesc());
            intent.putExtra(Constants.ManufacturingDate, dbStockDisplayValues.get(pos).getMFD());
            intent.putExtra(Constants.CPStockItemGUID, dbStockDisplayValues.get(pos).getCPStockItemGUID());
            context.startActivity(intent);

        }

        /**
         * This class search name based on Retailer name from list.
         */
        private class RetailerListFilter extends android.widget.Filter {
            protected FilterResults performFiltering(CharSequence prefix) {
                FilterResults results = new FilterResults();
                if (dbStockOriginalValues == null) {
                    dbStockOriginalValues = new ArrayList<>(dbStockDisplayValues);
                }
                if (prefix == null || prefix.length() == 0) {
                    results.values = dbStockOriginalValues;
                    results.count = dbStockOriginalValues.size();
                } else {
                    String prefixString = prefix.toString().toLowerCase();
                    ArrayList<DBStockBean> filteredItems = new ArrayList<>();
                    int count = dbStockOriginalValues.size();

                    for (int i = 0; i < count; i++) {
                        DBStockBean item = dbStockOriginalValues.get(i);
                        String mStrRetName = item.getMaterialDesc().toLowerCase();
                        if (mStrRetName.contains(prefixString)) {
                            filteredItems.add(item);
                        }
                    }
                    results.values = filteredItems;
                    results.count = filteredItems.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence prefix, FilterResults results) {
                //noinspection unchecked
                dbStockDisplayValues = (ArrayList<DBStockBean>) results.values; // has the filtered values
                notifyDataSetChanged();
                alDBStockList = dbStockDisplayValues;
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_invoice_his_list, menu);
        MenuItem menu_refresh = menu.findItem(R.id.menu_refresh_inv);
        if (!Constants.isSpecificCollTodaySyncOrNot(Constants.getLastSyncDate(Constants.SYNC_TABLE, Constants.Collections,
                Constants.CPStockItems, Constants.TimeStamp, DBStockActivity.this))) {
            menu_refresh.setVisible(true);
        } else
            menu_refresh.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_refresh_inv:

                onRefresh();
                break;
        }
        return false;
    }

    public class LoadingData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(DBStockActivity.this, R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
            syncProgDialog.setCancelable(true);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();

            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    DBStockActivity.this, R.style.MyTheme);
                            builder.setMessage(R.string.do_want_cancel_sync)
                                    .setCancelable(false)
                                    .setPositiveButton(
                                            R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {
                                                    dialogCancelled = true;

                                                    onBackPressed();
                                                }
                                            })
                                    .setNegativeButton(
                                            R.string.no,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {

                                                    try {
                                                        syncProgDialog
                                                                .show();
                                                        syncProgDialog
                                                                .setCancelable(true);
                                                        syncProgDialog
                                                                .setCanceledOnTouchOutside(false);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    dialogCancelled = false;

                                                }
                                            });
                            builder.show();
                        }
                    });
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                try {

                    OfflineManager.refreshStoreSync(getApplicationContext(), DBStockActivity.this, Constants.Fresh, concatCollectionStr);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
