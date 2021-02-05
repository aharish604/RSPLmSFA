package com.rspl.sf.msfa.expensemaihar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.expense.AddExpenseImageAdapter;
import com.rspl.sf.msfa.expense.ExpenseBean;
import com.rspl.sf.msfa.expense.ExpenseImageBean;
import com.rspl.sf.msfa.interfaces.DialogCallBack;
import com.rspl.sf.msfa.interfaces.OnClickInterface;
import com.rspl.sf.msfa.main.MainMenu;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;

/**
 * Created by e10742 on 08-06-2017.
 */

public class ExpenseEntryActivityMhr extends AppCompatActivity implements UIListener, OnClickInterface {

    private static final String TAG = "ExpenseEntryMhr";
    Spinner spnrExpenseType = null;
    Spinner spnrModeOfConv = null;
    TextView tvExpenseDate = null;
    private SimpleDateFormat dateFormatter;
    private String[][] arrayExpModeVal = null;
    private String[][] mArrayExpenseType = null;
    private String[][] mArraySalesPerson = null;
    private String stMode = "";
    private String stModeDesc = "";
    private String mStrSeleExpenseTypeId = "";
    private String mStrSeleExpenseTypeDesc = "";
    private String strAmtVal = "";

    EditText etAmount = null;
    EditText etRemarks = null;
    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;

    private ArrayList<ExpenseBean> expenseBeanList = new ArrayList<>();
    private Hashtable masterHashTable = new Hashtable();
    private String mStrCurrentDate = "";
    private ArrayList<HashMap<String, String>> arrItemTable = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> arrImageItemTable = new ArrayList<HashMap<String, String>>();
//    private HashSet<String> headerDataUniq = new HashSet<>();
    AddExpenseImageAdapter addExpenseImageAdapter;
    private RecyclerView recyclerView;
    private ArrayList<ExpenseImageBean> imageBeanList = new ArrayList<>();
    private ArrayList<ExpenseImageBean> finalImageBeanList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
       // ActionBarView.initActionBarView(this, true, getString(R.string.title_expense_entry));

        setContentView(R.layout.activity_expense_entry_mhr);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_expense_entry), 0);

        initUI();
    }

    /*InitializesUI*/
    void initUI() {

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        tvExpenseDate = (TextView) findViewById(R.id.tv_expense_date);
        setDateIntoTextView(mMonth, mDay, mYear);

        tvExpenseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseDatePicker();
            }
        });
        spnrExpenseType = (Spinner) findViewById(R.id.spnr_expense_type);
        spnrModeOfConv = (Spinner) findViewById(R.id.spnr_mode_of_conveyance);
        etAmount = (EditText) findViewById(R.id.et_amount);
        etRemarks = (EditText) findViewById(R.id.et_remarks);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        addExpenseImageAdapter = new AddExpenseImageAdapter(ExpenseEntryActivityMhr.this, imageBeanList);
        addExpenseImageAdapter.onImageAddClick(this);
        recyclerView.setAdapter(addExpenseImageAdapter);
        refreshRecyclerView("", "", "", "", 0);

        try {
            mArraySalesPerson = Constants.getDistributors();
        } catch (Exception e) {
        }

        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etAmount.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getExpenseType();
        getModeOfConv();
    }

    private void getExpenseType() {

//        String query = Constants.ExpenseConfigs + "?$filter=" + Constants.ExpenseFreq + " eq '" + expenseFreq + "'";
        String query = Constants.ExpenseConfigs;
        try {
//            mArrayExpenseType = OfflineManager.getConfigExpenseType(query, "");
            mArrayExpenseType = OfflineManager.getConfigExpense(query, "");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
        }

        if (mArrayExpenseType == null) {
            mArrayExpenseType = new String[2][1];
            mArrayExpenseType[0][0] = "";
            mArrayExpenseType[1][0] = Constants.None;
        }
        ArrayAdapter<String> expenseTypeAdapter = new ArrayAdapter<String>(ExpenseEntryActivityMhr.this,
                R.layout.custom_textview, mArrayExpenseType[3]);
        expenseTypeAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spnrExpenseType.setAdapter(expenseTypeAdapter);
        spnrExpenseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                mStrSeleExpenseTypeId = mArrayExpenseType[2][position];
                mStrSeleExpenseTypeDesc = mArrayExpenseType[3][position];
                spnrExpenseType.setBackgroundResource(R.drawable.spinner_bg);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getModeOfConv() {
        String query = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.ConvenyanceMode + "'";
        try {
            arrayExpModeVal = OfflineManager.getConfigListWithDefaultValAndNone(query, "");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
        }

        if (arrayExpModeVal == null) {
            arrayExpModeVal = new String[2][1];
            arrayExpModeVal[0][0] = "";
            arrayExpModeVal[1][0] = Constants.None;
        } else {
            //TODO
            arrayExpModeVal = Constants.CheckForOtherInConfigValue(arrayExpModeVal);
        }
        ArrayAdapter<String> modeWorkAdapter = new ArrayAdapter<String>(ExpenseEntryActivityMhr.this,
                R.layout.custom_textview, arrayExpModeVal[1]);
        modeWorkAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spnrModeOfConv.setAdapter(modeWorkAdapter);
        spnrModeOfConv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stMode = arrayExpModeVal[0][position];
                stModeDesc = arrayExpModeVal[1][position];
                if (!stMode.equalsIgnoreCase("")) {
                    spnrModeOfConv.setBackgroundResource(R.drawable.spinner_bg);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_back_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_save:
                onSaveData(ExpenseEntryActivityMhr.this);
                break;
        }
        return true;
    }

    /*expense date picker dialog*/
    void expenseDatePicker() {
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mStrCurrentDate = year + "-" + monthOfYear + 1 + "-" + dayOfMonth;
                tvExpenseDate.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        fromDatePickerDialog.getDatePicker().setMaxDate(newCalendar.getTimeInMillis());
        fromDatePickerDialog.show();
    }

    private void onSaveData(Context context) {
        String errorMsg = "";
        int validationCode = checkValidations();
        if (validationCode == 0) {
            saveDataToDataValt(context);
        } else if (validationCode == 1) {
            errorMsg = getString(R.string.validation_plz_fill_mandatory_flds);
        } else if (validationCode == 2) {
            errorMsg = getString(R.string.validation_plz_fill_mandatory_flds);
        } else if (validationCode == 3) {
            errorMsg = getString(R.string.expense_error_enter_valid_amount);
        } else if (validationCode == 4) {
            errorMsg = getString(R.string.expense_error_amount);
        }
        if (validationCode > 0) {
            Constants.dialogBoxWithButton(ExpenseEntryActivityMhr.this, "", errorMsg, getString(R.string.ok), "", null);
        }

    }

    private int checkValidations() {
        int validationCode = 0;
        strAmtVal = etAmount.getText().toString();
        if (TextUtils.isEmpty(mStrSeleExpenseTypeId)) {
            validationCode = 1;
            spnrExpenseType.setBackgroundResource(R.drawable.error_spinner);
        }
        if (TextUtils.isEmpty(stMode)) {
            validationCode = 1;
            spnrModeOfConv.setBackgroundResource(R.drawable.error_spinner);
        }
        if (TextUtils.isEmpty(strAmtVal)) {
            validationCode = 1;
            etAmount.setBackgroundResource(R.drawable.edittext_border);
        }
        finalImageBeanList.clear();

        if (imageBeanList.size() < 2) {
            validationCode = 1;
//            Constants.dialogBoxWithButton(this, "", getString(R.string.validation_plz_enter_mandatory_flds), getString(R.string.ok), "", null);
        } else {
            for (ExpenseImageBean expenseImageBean : imageBeanList) {
                if (!expenseImageBean.getImagePath().equals(""))
                    finalImageBeanList.add(expenseImageBean);
            }
        }
        return validationCode;
    }

    /*save data into data valt*/
    private void saveDataToDataValt(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        String loginIdVal = sharedPreferences.getString(Constants.username, "");
        GUID guid = GUID.newRandom();
        String doc_no = (System.currentTimeMillis() + "").substring(3, 10);
//        headerDataUniq.clear();
        ArrayList<ExpenseImageBean> expenseImageBeanArrayList = null;
        sortArrayList();
        int expenseItemNo = 0;
        int headerNo = 0;

//        int arrayTotalSize = expenseBeanList.size();
//        for (ExpenseBean expenseBean : expenseBeanList) {

            //header
//            if (!headerDataUniq.contains(expenseBean.getExpenseType())) {
                masterHashTable.clear();
                arrItemTable.clear();
                headerNo = 0;
                guid = GUID.newRandom();
                doc_no = (System.currentTimeMillis() + "").substring(3, 10);
                masterHashTable.put(Constants.ExpenseGUID, guid.toString36().toUpperCase());
                masterHashTable.put(Constants.ExpenseNo, doc_no);
//                        masterHashTable.put(Constants.FiscalYear, fiscalYear + "");
                masterHashTable.put(Constants.FiscalYear, "");
                masterHashTable.put(Constants.LoginID, loginIdVal);
                masterHashTable.put(Constants.CPGUID, "");
                masterHashTable.put(Constants.CPNo, "");
                masterHashTable.put(Constants.CPName, "");
                masterHashTable.put(Constants.CPType, "");
                masterHashTable.put(Constants.CPTypeDesc, "");
                masterHashTable.put(Constants.SPGUID, mArraySalesPerson[4][0]);
                masterHashTable.put(Constants.SPNo, mArraySalesPerson[6][0]);
                masterHashTable.put(Constants.SPName, mArraySalesPerson[7][0]);
                masterHashTable.put(Constants.ExpenseType, "");
                masterHashTable.put(Constants.ExpenseTypeDesc, "");
                masterHashTable.put(Constants.ExpenseDate, mStrCurrentDate);
                masterHashTable.put(Constants.Status, "");
                masterHashTable.put(Constants.StatusDesc, "");
                masterHashTable.put(Constants.Amount, strAmtVal);
                masterHashTable.put(Constants.Currency, "INR");

//            }

            //item start
            HashMap<String, String> singleItem = new HashMap<String, String>();
            GUID itemGuid = GUID.newRandom();
            singleItem.put(Constants.ExpenseItemGUID, itemGuid.toString36().toUpperCase());
            singleItem.put(Constants.ExpenseGUID, guid.toString36().toUpperCase());
            singleItem.put(Constants.ExpeseItemNo, expenseItemNo + "");
            singleItem.put(Constants.LoginID, loginIdVal);
            singleItem.put(Constants.ExpenseItemType, mStrSeleExpenseTypeId);
            singleItem.put(Constants.ExpenseItemTypeDesc, mStrSeleExpenseTypeDesc);
            singleItem.put(Constants.BeatGUID, "");
            singleItem.put(Constants.Location, "");
            singleItem.put(Constants.ConvenyanceMode, stMode);
            singleItem.put(Constants.ConvenyanceModeDs, stModeDesc);
            singleItem.put(Constants.BeatDistance, "");
            singleItem.put(Constants.UOM, "");
            singleItem.put(Constants.Amount, strAmtVal);
            singleItem.put(Constants.Currency, "INR");
            singleItem.put(Constants.Remarks, etRemarks.getText().toString());
            arrImageItemTable.clear();
//            expenseImageBeanArrayList = expenseBean.getExpenseImageBeanArrayList();
            expenseImageBeanArrayList = finalImageBeanList;
            if (expenseImageBeanArrayList != null) {
                for (ExpenseImageBean expenseImageBean : expenseImageBeanArrayList) {
                    HashMap<String, String> singleImageItem = new HashMap<String, String>();
                    GUID itemImageGuid = GUID.newRandom();
                    singleImageItem.put(Constants.ExpenseDocumentID, itemImageGuid.toString36().toUpperCase());
                    singleImageItem.put(Constants.ExpenseItemGUID, itemGuid.toString36().toUpperCase());
                    singleImageItem.put(Constants.DocumentStore, "");
                    singleImageItem.put(Constants.DocumentTypeID, "");
                    singleImageItem.put(Constants.DocumentTypeDesc, "");
                    singleImageItem.put(Constants.LoginID, loginIdVal);
                    singleImageItem.put(Constants.DocumentStatusID, "");
                    singleImageItem.put(Constants.DocumentStatusDesc, "");
                    singleImageItem.put(Constants.ValidFrom, UtilConstants.getNewDateTimeFormat());
                    singleImageItem.put(Constants.ValidTo, UtilConstants.getNewDateTimeFormat());
                    singleImageItem.put(Constants.DocumentLink, expenseImageBean.getImagePath());
                    singleImageItem.put(Constants.FileName, expenseImageBean.getFileName() + "." + expenseImageBean.getImageExtensions());
                    singleImageItem.put(Constants.DocumentMimeType, expenseImageBean.getDocumentMimeType());
                    singleImageItem.put(Constants.DocumentSize, expenseImageBean.getDocumentSize());
                    singleImageItem.put(Constants.Remarks, etRemarks.getText().toString());
                    Constants.storeInDataVault(itemImageGuid.toString36().toUpperCase(), "",context);
                    arrImageItemTable.add(singleImageItem);
                }
            }
            singleItem.put("item_no" + headerNo, UtilConstants.convertArrListToGsonString(arrImageItemTable));
            arrItemTable.add(singleItem);
            //item end

            /*store data*/
//                    if (arrayTotalSize > expenseItemNo + 1) {
//                        if (expenseBean.getExpenseType().equals(expenseBeanList.get(expenseItemNo + 1).getExpenseType())) {
//                            storeToDataValt = false;
//                        } else {
//                            storeToDataValt = true;
//                        }
//                    } else {
//                        storeToDataValt = true;
//                    }

//                    if (storeToDataValt) {
            masterHashTable.put(Constants.entityType, Constants.Expenses);
            masterHashTable.put(Constants.ITEM_TXT, UtilConstants.convertArrListToGsonString(arrItemTable));
            Constants.saveDeviceDocNoToSharedPref(ExpenseEntryActivityMhr.this, Constants.Expenses, doc_no);

            JSONObject jsonHeaderObject = new JSONObject(masterHashTable);
            Log.d(TAG, "jsonHeaderObject: " + jsonHeaderObject);
           ConstantsUtils.storeInDataVault(doc_no, jsonHeaderObject.toString(),this);

            saveDocumentEntityToTable();
//                    }
//            headerDataUniq.add(expenseBean.getExpenseType());
            headerNo++;
            expenseItemNo++;
//        }
        String message;
        message = getString(R.string.expense_daily_created_success);
        Constants.dialogBoxWithButton(ExpenseEntryActivityMhr.this, "", message, getString(R.string.ok), "", new DialogCallBack() {
            @Override
            public void clickedStatus(boolean clickedStatus) {
                if (clickedStatus) {
                    Intent intBack = new Intent(ExpenseEntryActivityMhr.this, MainMenu.class);
                    intBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intBack);
                }
            }
        });
    }

    /*sorting array list based on expense type*/
    private void sortArrayList() {
        Collections.sort(expenseBeanList, new Comparator<ExpenseBean>() {
            @Override
            public int compare(ExpenseBean expenseBean1, ExpenseBean expenseBean2) {

                return expenseBean2.getExpenseType().compareTo(expenseBean1.getExpenseType());
            }
        });
    }

    /*save image into offline db*/
    private void saveDocumentEntityToTable() {
        try {
            //noinspection unchecked
            int getNumb = 0;
            for (HashMap<String, String> arrItemTables : arrItemTable) {
                String imageStringArray = arrItemTables.get("item_no" + getNumb);
                ArrayList<HashMap<String, String>> convertedString = UtilConstants.convertToArrayListMap(imageStringArray);

                for (HashMap<String, String> hashItem : convertedString) {
                    OfflineManager.createExpensesingItem(arrItemTables, hashItem, this);
                }
                Log.d(TAG, "onSaveData: " + imageStringArray);
                Log.d(TAG, "convertedString: " + convertedString);
                getNumb++;
            }

        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    @Override
    public void onRequestError(int i, Exception e) {
        Log.d(TAG, "onRequestError: " + e.toString());

    }

    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        Log.d(TAG, "onRequestSuccess: ");
    }

    private void refreshRecyclerView(String path, String filename, String strMimeType, String mimeType, int mLongBitmapSize) {
        int totalSize = imageBeanList.size();
        if (checkEmptyImage(totalSize)) {
            ExpenseImageBean expenseImageBean = new ExpenseImageBean();
            expenseImageBean.setImagePath(path);
            expenseImageBean.setDocumentMimeType(mimeType);
            expenseImageBean.setDocumentSize(mLongBitmapSize + "");
            expenseImageBean.setImageExtensions(strMimeType + "");
            expenseImageBean.setFileName(filename + "");
            imageBeanList.add(totalSize - 1, expenseImageBean);
            addExpenseImageAdapter.notifyItemInserted(totalSize - 1);
        } else {
            imageBeanList.add(totalSize, getEmptyImage());
            if (!path.isEmpty()) {
                totalSize = imageBeanList.size();
                ExpenseImageBean expenseImageBeans = new ExpenseImageBean();
                expenseImageBeans.setImagePath(path);
                expenseImageBeans.setDocumentMimeType(mimeType);
                expenseImageBeans.setDocumentSize(mLongBitmapSize + "");
                expenseImageBeans.setImageExtensions(strMimeType + "");
                expenseImageBeans.setFileName(filename + "");
                imageBeanList.add(totalSize - 1, expenseImageBeans);
            }
            addExpenseImageAdapter.notifyDataSetChanged();
        }

    }

    /*set empty image path*/
    private ExpenseImageBean getEmptyImage() {
        ExpenseImageBean expenseImageBean = new ExpenseImageBean();
        expenseImageBean.setImagePath("");
        return expenseImageBean;
    }

    /*check image is present or not*/
    private boolean checkEmptyImage(int size) {
        boolean emptyImageNotFound = false;
        if (size > 0) {
            ExpenseImageBean expenseImageBean = imageBeanList.get(size - 1);
            if (expenseImageBean.getImagePath().equalsIgnoreCase("")) {
                emptyImageNotFound = true;
            }
        }
        return emptyImageNotFound;
    }


    @Override
    public void onItemClick(View view, Object object) {
        Constants.openCameraWindow(ExpenseEntryActivityMhr.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.TAKE_PICTURE && resultCode == RESULT_OK && data != null) {
            Bundle bundleExtrasResult = data.getExtras();
            final Bitmap bitMap = (Bitmap) bundleExtrasResult.get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            assert bitMap != null;
            bitMap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] imageInByte = stream.toByteArray();
            int mLongBitmapSize = imageInByte.length;

            String filename = (System.currentTimeMillis() + "");
            File fileName = Constants.SaveImageInDevice(filename, bitMap);
            String strMimeType = MimeTypeMap.getFileExtensionFromUrl(fileName.getPath());
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    strMimeType);


            refreshRecyclerView(fileName.getPath(), filename, strMimeType, mimeType, mLongBitmapSize);
        }

    }
    /*set date into textview*/
    private void setDateIntoTextView(int mMonth, int mDay, int mYear) {
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
        tvExpenseDate.setText(new StringBuilder().append(mDay).append("/").append(mMonth + 1).append("/").append(mYear).append(" "));

    }
}
