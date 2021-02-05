package com.rspl.sf.msfa.complaint;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.CustomerDetailsActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.TimeZone;

public class ComplaintCreateActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, UIListener, KeyboardView.OnKeyboardActionListener {
    private static String TAG = "ComplaintCreateActivity";
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    private String mStrBundleRetailerUID = "", mStrBundleCPGUID32 = "", mStrComingFrom = "";
    private LinearLayout llProduct;
    private Spinner spCrsSkuGroup, spItemDesc, spComplaints;
    private TextView tvMFD;
    private String[][] mArrayOrderedGroup, mArrayOrderedItem;
    private Spinner spComplaintCategory;
    private String mStrSeleCatId = "", mStrSelectedCatDesc = Constants.None, mStrSelectedCRSID = Constants.None, mStrSelectedCRSItem = Constants.None, mStrSelectedCRSDesc = Constants.None;
    private String[][] mArrayComplaintCat = null, mArrayComplaints;
    private EditText edRemarks, edQuantity, edBatchNumber;
    private String[][] mArrayDistributors = null, mArraySPValues = null;
    private String mStrSeleComplaintsId = "";
    private String mStrSeleComplaintsDesc = Constants.None;
    private String mStrSelectedCRSItemDesc = Constants.None;
    private String mStrCurrentDate = "";
    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;
    private KeyboardView keyboardView;
    private Keyboard keyboard;
    private EditText focusEditText;
    private String strVisitActRefId = "";
    private EditText etMaterialDesc;
    private ODataDuration mStartTimeDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_create);
       // ActionBarView.initActionBarView(this, true, getString(R.string.create_complaint));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.create_complaint), 0);

        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
        }
        mStartTimeDuration=UtilConstants.getOdataDuration();
//        if (!Constants.restartApp(ComplaintCreateActivity.this)) {
        initializeKeyboardDependencies();
        initUI();
        getSalesPersonValues();
        getDistributorValues();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_back_save, menu);
        return true;
    }

    public void onBackPressed() {
        if (Constants.isCustomKeyboardVisible(keyboardView)) {
            Constants.hideCustomKeyboard(keyboardView);
        } else {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ComplaintCreateActivity.this, R.style.MyTheme);
            builder.setMessage(R.string.alert_exit_customer_complaints).setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            onNavigateToRetDetilsActivity();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }

                    });
            builder.show();
        }

    }

    /*navigation to another activity*/
    private void onNavigateToRetDetilsActivity() {
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(ComplaintCreateActivity.this, CustomerDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        if (!Constants.OtherRouteNameVal.equalsIgnoreCase("")) {
            intentNavPrevScreen.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
            intentNavPrevScreen.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
        }

        startActivity(intentNavPrevScreen);
    }

    /*initilize view*/
    private void initUI() {
//        Constants.mStartTimeDuration = UtilConstants.getOdataDuration();
        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);
        spComplaintCategory = (Spinner) findViewById(R.id.sp_complaint_category);
        etMaterialDesc = (EditText) findViewById(R.id.edit_material_desc);
        spItemDesc = (Spinner) findViewById(R.id.sp_prod_item_desc);
        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetID);
        llProduct = (LinearLayout) findViewById(R.id.ll_cust_product);
        edRemarks = (EditText) findViewById(R.id.edit_cust_remarks);
        spComplaints = (Spinner) findViewById(R.id.sp_cust_complaints);
        spCrsSkuGroup = (Spinner) findViewById(R.id.sp_prod_crs_group);
        edQuantity = (EditText) findViewById(R.id.edit_cust_quantity);
        edBatchNumber = (EditText) findViewById(R.id.edit_cust_batch_number);
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        final DatePickerDialog dialog = new DatePickerDialog(ComplaintCreateActivity.this, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        tvMFD = (TextView) findViewById(R.id.tv_cust_mfd);
        tvMFD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
                cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
                cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
                cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));

                dialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                dialog.show();
            }
        });
        edRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edRemarks.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        UtilConstants.editTextDecimalFormat(edQuantity, 13, 3);
        edQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edQuantity.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edBatchNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edBatchNumber.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
       /* edQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    focusEditText = edQuantity;
                    Constants.showCustomKeyboard(v, keyboardView, ComplaintCreateActivity.this);
                } else {
                    Constants.hideCustomKeyboard(keyboardView);
                }
            }
        });
        edQuantity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                Constants.showCustomKeyboard(v, keyboardView, ComplaintCreateActivity.this);
                Constants.setCursorPostion(edQuantity, v, event);
                return true;
            }
        });*/
        /*get complaint category*/
        getComplaintCategory();
        //get order material list
//        getOrderedMaterials();
        //get item description
        getItemDescription();
    }

    public void initializeKeyboardDependencies() {
        keyboardView = (KeyboardView) findViewById(R.id.keyboard_custom_invoice_sel);
        keyboard = new Keyboard(ComplaintCreateActivity.this, R.xml.ll_with_out_dot_inc_dec_up_down);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    /*get item description dropdown*/
    private void getItemDescription() {
        if (mStrSelectedCRSID.equals(Constants.None)) {
            mArrayOrderedItem = new String[2][1];
            mArrayOrderedItem[0][0] = Constants.None;
            mArrayOrderedItem[1][0] = Constants.None;
        } else {
            try {
                String mStrConfigQry = Constants.CPStockItems + "?$orderby=" + Constants.MaterialDesc + " &$filter=" + Constants.OrderMaterialGroupID + " eq '" + mStrSelectedCRSID + "' and " + Constants.StockOwner + " eq '01'";
                mArrayOrderedItem = OfflineManager.getStockOwnerGroups(mStrConfigQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            if (mArrayOrderedItem == null) {
                mArrayOrderedItem = new String[2][1];
                mArrayOrderedItem[0][0] = Constants.None;
                mArrayOrderedItem[1][0] = Constants.None;
            } else {
                mArrayOrderedItem = Constants.CheckForOtherInConfigValue(mArrayOrderedItem);
            }
        }


        ArrayAdapter<String> productOrderItemAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, mArrayOrderedItem[1]);
        productOrderItemAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spItemDesc.setAdapter(productOrderItemAdapter);
        spItemDesc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mStrSelectedCRSItem = mArrayOrderedItem[0][i];
                mStrSelectedCRSItemDesc = mArrayOrderedItem[1][i];
                if (!mStrSelectedCRSItem.equals(Constants.None)) {
                    spItemDesc.setBackgroundResource(R.drawable.spinner_bg);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    /*get order material spinner*/
    private void getOrderedMaterials() {

        try {
            String mStrConfigQry = Constants.CPStockItems + "?$orderby=" + Constants.OrderMaterialGroupDesc + " &$filter=" + Constants.StockOwner + " eq '01' and " + Constants.OrderMaterialGroupID + " ne '' ";
            mArrayOrderedGroup = OfflineManager.getOrderedMaterialGroups(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        if (mArrayOrderedGroup == null) {
            mArrayOrderedGroup = new String[2][1];
            mArrayOrderedGroup[0][0] = "";
            mArrayOrderedGroup[1][0] = "";
        } else {
            mArrayOrderedGroup = Constants.CheckForOtherInConfigValue(mArrayOrderedGroup);
        }


        ArrayAdapter<String> productOrderGroupAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, mArrayOrderedGroup[1]);
        productOrderGroupAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spCrsSkuGroup.setAdapter(productOrderGroupAdapter);
        spCrsSkuGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mStrSelectedCRSID = mArrayOrderedGroup[0][i];
                mStrSelectedCRSDesc = mArrayOrderedGroup[1][i];
                if (!mStrSelectedCRSID.equals(Constants.None)) {
                    spCrsSkuGroup.setBackgroundResource(R.drawable.spinner_bg);
                }
                getItemDescription();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    /*get complaint category list*/
    private void getComplaintCategory() {
//        String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.ComplaintCategory + "'";// &$orderby=" + Constants.Description + "%20asc";
        try {
//            mArrayComplaintCat = OfflineManager.getConfigListWithDefaultValAndNone(mStrConfigQry, "");
            if (mArrayComplaintCat == null) {
                mArrayComplaintCat = new String[5][5];
                mArrayComplaintCat[0][0] = "";
                mArrayComplaintCat[1][0] = Constants.None;
                mArrayComplaintCat[2][0] = "";
                mArrayComplaintCat[3][0] = "";
                mArrayComplaintCat[4][0] = "";
                mArrayComplaintCat[0][1] = "Product";
                mArrayComplaintCat[1][1] = "Product";
                mArrayComplaintCat[2][1] = "";
                mArrayComplaintCat[3][1] = "";
                mArrayComplaintCat[4][1] = "";
                mArrayComplaintCat[0][2] = "Scheme";
                mArrayComplaintCat[1][2] = "Scheme";
                mArrayComplaintCat[2][2] = "";
                mArrayComplaintCat[3][2] = "";
                mArrayComplaintCat[4][2] = "";
                mArrayComplaintCat[0][3] = "Service";
                mArrayComplaintCat[1][3] = "Service";
                mArrayComplaintCat[2][3] = "";
                mArrayComplaintCat[3][3] = "";
                mArrayComplaintCat[4][3] = "";
                mArrayComplaintCat[0][4] = "Others";
                mArrayComplaintCat[1][4] = "Others";
                mArrayComplaintCat[2][4] = "";
                mArrayComplaintCat[3][4] = "";
                mArrayComplaintCat[4][4] = "";
            } else {
                mArrayComplaintCat = Constants.CheckForOtherInConfigValue(mArrayComplaintCat);
            }

            ArrayAdapter<String> compCatAdapter = new ArrayAdapter<String>(this,
                    R.layout.custom_textview, mArrayComplaintCat[1]);
            compCatAdapter.setDropDownViewResource(R.layout.spinnerinside);
            spComplaintCategory.setAdapter(compCatAdapter);
            spComplaintCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    mStrSeleCatId = mArrayComplaintCat[0][i];
                    mStrSelectedCatDesc = mArrayComplaintCat[1][i];
                    if (!mStrSeleCatId.equals("")) {
                        spComplaintCategory.setBackgroundResource(R.drawable.spinner_bg);

                    }
                    setCategoryVisibility();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * get distributor value
     */
    private void getDistributorValues() {
//        mArrayDistributors = Constants.getDistributorsByCPGUID(mStrBundleCPGUID);
    }

    /**
     * get salesPerson values
     */
    private void getSalesPersonValues() {
//        mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(mStrBundleCPGUID);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_save:
                onSave();
                break;
        }
        return true;
    }

    /*save data into data valt*/
    private void onSave() {
//        if (Constants.isValidTime(UtilConstants.convertTimeOnly(Constants.mStartTimeDuration.toString()),
//                UtilConstants.convertTimeOnly(UtilConstants.getOdataDuration().toString())) && Constants.isValidDate()) {
        if (isValidationSucess()) {

        /*    if (!Constants.onGpsCheck(ComplaintCreateActivity.this)) {
                return;
            }
            if (!UtilConstants.getLocation(ComplaintCreateActivity.this)) {
                return;
            }
*/

            LocationUtils.checkLocationPermission(ComplaintCreateActivity.this, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                    Log.d("location fun","1");

                    if (status) {

                    }else{
                        return;
                    }
                }
            });
            Constants.getLocation(ComplaintCreateActivity.this, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {

                    if(status){
                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
                        String loginIdVal = sharedPreferences.getString(Constants.username, "");
                        String sQuantity = "";
                        String batchNumber = "";
                        String sRemarks = edRemarks.getText().toString();
                        if (mStrSeleCatId.equalsIgnoreCase("Product")) {
                            sQuantity = edQuantity.getText().toString();
                            batchNumber = edBatchNumber.getText().toString();
                        } else {
                            mStrSelectedCRSID = "";
                            mStrSelectedCRSDesc = "";
                            mStrSelectedCRSItem = "";
                            mStrSelectedCRSItemDesc = "";

                        }

                        Hashtable complaintsTable = new Hashtable();
                        GUID guid = GUID.newRandom();
                        String tempInvNo = (System.currentTimeMillis() + "");
                        complaintsTable.put(Constants.ComplaintNo, tempInvNo);
                        complaintsTable.put(Constants.ComplaintCategoryID, mStrSeleCatId);
                        complaintsTable.put(Constants.ComplainCategoryDesc, mStrSelectedCatDesc);
                        complaintsTable.put(Constants.ComplaintTypeID, mStrSeleComplaintsId);
                        complaintsTable.put(Constants.ComplaintTypeDesc, mStrSeleComplaintsDesc);
                        complaintsTable.put(Constants.ComplaintPriorityID, "");
                        complaintsTable.put(Constants.ComplaintPriorityDesc, "");
                        complaintsTable.put(Constants.SPGUID, "");
                        complaintsTable.put(Constants.SPNo,"");
                        complaintsTable.put(Constants.SPName, "");
                        complaintsTable.put(Constants.CPTypeID, Constants.str_02);
                        complaintsTable.put(Constants.CPTypeDesc, "");
                        complaintsTable.put(Constants.CPGUID, mStrBundleCPGUID32);
                        complaintsTable.put(Constants.CPNo, mStrBundleRetID);
                        complaintsTable.put(Constants.CPName, mStrBundleRetName);
                        complaintsTable.put(Constants.OrderMaterialGroupID, mStrSelectedCRSID);
                        complaintsTable.put(Constants.OrderMaterialGroupDesc, mStrSelectedCRSDesc);
                        complaintsTable.put(Constants.MaterialGrp, "");
                        complaintsTable.put(Constants.MaterialGrpDesc, "");
                        complaintsTable.put(Constants.Material, mStrSelectedCRSItem);
                        complaintsTable.put(Constants.MaterialDesc, etMaterialDesc.getText().toString());
                        complaintsTable.put(Constants.ComplaintDate, UtilConstants.getNewDateTimeFormat());
                        complaintsTable.put(Constants.ComplaintStatusID, "");
                        complaintsTable.put(Constants.ComplaintStatusDesc, "");
                        complaintsTable.put(Constants.Quantity, sQuantity);
                        complaintsTable.put(Constants.UOM, "");
                        complaintsTable.put(Constants.Batch, batchNumber.toUpperCase());
                        complaintsTable.put(Constants.MFD, mStrCurrentDate);
                        complaintsTable.put(Constants.Remarks, sRemarks);
                        complaintsTable.put(Constants.CreatedBy, loginIdVal);
                        complaintsTable.put(Constants.SetResourcePath, "guid'" + guid.toString() + "'");
                        strVisitActRefId = guid.toString36().toUpperCase();
                        try {

                            //noinspection unchecked
                            OfflineManager.createCustomerComplaints(complaintsTable, ComplaintCreateActivity.this);
                        } catch (OfflineODataStoreException e) {
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                        }
                    }else{
                        return;
                    }
                }
            });


        }
      /*  } else {
            UtilConstants.showAlert(getString(R.string.validation_app_time_incorrect), ComplaintCreateActivity.this);
        }*/
    }

    /*check validation*/
    private boolean isValidationSucess() {
        int validCount = 0;
        if (edRemarks.getText().toString().equals("")) {
            validCount++;
            edRemarks.setBackgroundResource(R.drawable.edittext_border);
        }
        if (mStrSeleComplaintsId.equals("")) {
            validCount++;
            spComplaints.setBackgroundResource(R.drawable.error_spinner);
        }
        if (mStrSeleCatId.equals("") || mStrSeleCatId.equalsIgnoreCase("Product")) {
            if (mStrSeleCatId.equals("")) {
                validCount++;
                spComplaintCategory.setBackgroundResource(R.drawable.error_spinner);
            }
            if (mStrSeleComplaintsId.equals("")) {
                validCount++;
                spComplaints.setBackgroundResource(R.drawable.error_spinner);
            }
           /* if (mStrSelectedCRSID.equals(Constants.None)) {
                validCount++;
                spCrsSkuGroup.setBackgroundResource(R.drawable.error_spinner);
            }*/
           /* if (mStrSelectedCRSItem.equals(Constants.None) || edQuantity.getText().toString().equals("")) {
                validCount++;
                etMaterialDesc.setBackgroundResource(R.drawable.edittext_border);
            }*/
            if (edQuantity.getText().toString().equals("")) {
                validCount++;
                edQuantity.setBackgroundResource(R.drawable.edittext_border);
            }
            if (edBatchNumber.getText().toString().equals("")) {
                validCount++;
                edBatchNumber.setBackgroundResource(R.drawable.edittext_border);
            }
            if (tvMFD.getText().toString().equalsIgnoreCase("")) {
                validCount++;
                tvMFD.setBackgroundResource(R.drawable.textview_border);
            }

        }
        if (validCount == 0) {
            return true;
        } else {
            Constants.dialogBoxWithButton(this, "", getString(R.string.alert_please_fill_mandatory_fields), getString(R.string.ok), "", null);
            return false;
        }
    }

    /*set category visibility based on condition*/
    private void setCategoryVisibility() {
        if (mStrSeleCatId.equals("Product")) {
            llProduct.setVisibility(View.VISIBLE);
            mArrayComplaints = new String[2][5];
            mArrayComplaints[0][0] = "";
            mArrayComplaints[1][0] = Constants.None;
            mArrayComplaints[0][1] = "Leakages";
            mArrayComplaints[1][1] = "Leakages";
            mArrayComplaints[0][2] = "Packaging";
            mArrayComplaints[1][2] = "Packaging";
            mArrayComplaints[0][3] = "Quality";
            mArrayComplaints[1][3] = "Quality";
            mArrayComplaints[0][4] = "Others";
            mArrayComplaints[1][4] = "Others";

            setComplaints();
        } else if (mStrSeleCatId.equals("Scheme")) {
            llProduct.setVisibility(View.GONE);
            mArrayComplaints = new String[2][5];
            mArrayComplaints[0][0] = "";
            mArrayComplaints[1][0] = Constants.None;
            mArrayComplaints[0][1] = "Margin";
            mArrayComplaints[1][1] = "Margin";
            mArrayComplaints[0][2] = "Pricing";
            mArrayComplaints[1][2] = "Pricing";
            mArrayComplaints[0][3] = "Scheme";
            mArrayComplaints[1][3] = "Scheme";
            mArrayComplaints[0][4] = "Others";
            mArrayComplaints[1][4] = "Others";

            setComplaints();
        } else if (mStrSeleCatId.equals("Service")) {
            llProduct.setVisibility(View.GONE);
            mArrayComplaints = new String[2][5];
            mArrayComplaints[0][0] = "";
            mArrayComplaints[1][0] = Constants.None;
            mArrayComplaints[0][1] = "Claim Settlement";
            mArrayComplaints[1][1] = "Claim Settlement";
            mArrayComplaints[0][2] = "Credit";
            mArrayComplaints[1][2] = "Credit";
            mArrayComplaints[0][3] = "Supply";
            mArrayComplaints[1][3] = "Supply";
            mArrayComplaints[0][4] = "Others";
            mArrayComplaints[1][4] = "Others";

            setComplaints();
        } else if (mStrSeleCatId.equals("Others")) {
            llProduct.setVisibility(View.GONE);
            mArrayComplaints = new String[2][2];
            mArrayComplaints[0][0] = "";
            mArrayComplaints[1][0] = Constants.None;
            mArrayComplaints[0][1] = "Others";
            mArrayComplaints[1][1] = "Others";

            setComplaints();
        } else if (mStrSeleCatId.equals("")) {
            llProduct.setVisibility(View.GONE);
            mArrayComplaints = new String[2][1];
            mArrayComplaints[0][0] = "";
            mArrayComplaints[1][0] = Constants.None;
            setComplaints();
        }
       /* if (!mStrSeleCatId.equals("")) {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.ComplaintType + "' and ParentID eq '" + mStrSeleCatId + "' &$orderby=" + Constants.Description + "%20asc";
            try {
                mArrayComplaints = OfflineManager.getConfigListWithDefaultValAndNone(mStrConfigQry, "");
                setComplaints();
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }*/
    }

    /*set complaints in spinner*/
    private void setComplaints() {
        if (mArrayComplaints == null) {
            mArrayComplaints = new String[5][1];
            mArrayComplaints[0][0] = "";
            mArrayComplaints[1][0] = "";
            mArrayComplaints[2][0] = "";
            mArrayComplaints[3][0] = "";
            mArrayComplaints[4][0] = "";
        } else {
            mArrayComplaints = Constants.CheckForOtherInConfigValue(mArrayComplaints);
        }

        ArrayAdapter<String> compAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_textview, mArrayComplaints[1]);
        compAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spComplaints.setAdapter(compAdapter);
        spComplaints.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mStrSeleComplaintsId = mArrayComplaints[0][position];
                mStrSeleComplaintsDesc = mArrayComplaints[1][position];
                if (!mStrSeleComplaintsId.equals("")) {
                    spComplaints.setBackgroundResource(R.drawable.spinner_bg);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        mYear = year;
        mMonth = monthOfYear;
        mDay = dayOfMonth;
        String mon = "";
        String day = "";
        int mnt = 0;
        mnt = mMonth + 1;
        if (mnt < 10)
            mon = "0" + mnt;
        else
            mon = "" + mnt;
        day = "" + mDay;
        if (mDay < 10)
            day = "0" + mDay;
        mStrCurrentDate = mYear + "-" + mon + "-" + day;

        if (!mStrCurrentDate.equalsIgnoreCase(""))
            tvMFD.setBackgroundResource(R.drawable.textview_transprent);

        tvMFD.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(dayOfMonth).append("/").append(monthOfYear + 1).append("/").append(year).append(" "));

    }

    @Override
    public void onRequestError(int i, Exception e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ComplaintCreateActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.msg_customer_complaints_failed_created).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder.show();
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        // Visit Activity updated
        Constants.onVisitActivityUpdate(ComplaintCreateActivity.this,mStrBundleCPGUID32,
                strVisitActRefId, Constants.CustomerCompCreateID, Constants.CustomerComplaintsCreate, mStartTimeDuration);

        AlertDialog.Builder builder = new AlertDialog.Builder(ComplaintCreateActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.msg_customer_complaints_created).setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        onNavigateToRetDetilsActivity();
                    }
                });

        builder.show();
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        switch (primaryCode) {

            case 81:
                //Plus
                Constants.incrementTextValues(focusEditText, Constants.N);
                break;
            case 69:
                //Minus
                Constants.decrementEditTextVal(focusEditText, Constants.N);
                break;
            case 1:
                break;
            case 2:
                break;
            case 56:
                KeyEvent event = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                dispatchKeyEvent(event);
                break;

            default:
                //default numbers
                KeyEvent events = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                dispatchKeyEvent(events);
                break;
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}
