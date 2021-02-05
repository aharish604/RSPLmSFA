package com.rspl.sf.msfa.finance;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.google.gson.Gson;
import com.rspl.sf.msfa.CustomerDetailsActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.reports.OutstandingBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.TimeZone;

/**
 * Created by ${e10526} on ${19-04-2016}.
 *
 */

@SuppressLint("NewApi")
public class CollectionCreateActivity extends AppCompatActivity implements UIListener , View.OnClickListener
        , KeyboardView.OnKeyboardActionListener,DatePickerDialog.OnDateSetListener{

    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "", mStrBundleCPGUID32 = "";

    private Spinner sp_collection_method, sp_payment_method, sp_bank_names;

    private TextView tv_collection_date, tv_current_invoice_value, tv_outstanding_value,
            tv_cheque_date,tv_utr_label,tv_branch_name_lbl;

    private int mYear, mMonth, mDay, mnt;

    private String mon = "", day = "", mStrCurrentDate = "", mStrSelChequeDate ="";

    private EditText /*edit_instrument_no,*/
            edit_amount, edit_remarks, edit_utr_no, edit_branch_name;

    MenuItem menu_save, menu_next;
    private boolean mBooleanCollectionWithReference = false;

    private String mStrCollectionTypeCode = "",mStrCollectionTypeDesc="", mStrPaymentModeDesc = "",
            mStrPaymentModeCode = "", mStrBankNamesCode = "", mStrBankName = "",
            mStrInstrumentNo = "", mStrAmount = "", mStrRemarks = "", mStrUTRNo = "", mStrBranchName = "";

    private String[][] mArrayCollectionTypeVal;
    private String[][] mArrayBankNameVal;
    private String[][] mArrayPaymentModeVal, mArrayRetVal;
    private LinearLayout ll_bank_name,/* ll_instrument_no,*/
            ll_current_invoice, ll_utr_number, ll_branch_name,ll_cheque_date,
            ll_advance_adj_amt,ll_coll_amount,ll_payment_mode;

    ArrayList<HashMap<String, String>> arrtable;

    Hashtable dbHeaderTable;

    private String popUpText = "";
    private double mDoubleOutAmount = 0.0, mDoubBundleCurrentInv = 0.0;
    private String doc_no;


    private String mStrDefultRefType = "true", mStrParentID = "";

    TextView tv_inv_collection_amount,tv_advance_adjust_amt;

    private String mStrBundleRetailerUID = "";
    private ArrayList<OutstandingBean> alOutstandingsBean;

    private String mStrCurrencyVal = "";

    String mStrComingFrom = "";


    KeyboardView keyboardView;
    Keyboard keyboard;

    private double mDouAdvanceAmt = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
     //   ActionBarView.initActionBarView(this, true,getString(R.string.title_collection_entry));
        setContentView(R.layout.activity_collection_create);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_collection_entry), 0);
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);

            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
        }
            initializeUI();
    }

    /*initializes UI for screen*/
    void initializeUI() {
        mYear = 0;
        mMonth = 0;
        mDay = 0;
        mnt = 0;
        sp_collection_method = (Spinner) findViewById(R.id.sp_collection_method);
        sp_payment_method = (Spinner) findViewById(R.id.sp_payment_method);
        sp_bank_names = (Spinner) findViewById(R.id.sp_bank_names);
        ll_bank_name = (LinearLayout) findViewById(R.id.ll_bank_name);
        ll_bank_name.setVisibility(View.GONE);

        ll_cheque_date = (LinearLayout) findViewById(R.id.ll_cheque_date);
        tv_branch_name_lbl = (TextView) findViewById(R.id.tv_branch_name_lbl);
        tv_branch_name_lbl.setText(getString(R.string.lbl_bank_name));
        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);
        getRetValues();
        mStrCurrencyVal = mArrayRetVal[0][0];

        TextView tv_coll_amt_currency = (TextView) findViewById(R.id.tv_coll_amt_currency);
        tv_coll_amt_currency.setText(mStrCurrencyVal);
        TextView tv_outstanding_currency_val = (TextView) findViewById(R.id.tv_outstanding_currency_val);
        tv_outstanding_currency_val.setText(mArrayRetVal[0][0]);

        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetailerUID);

        Constants.alTempInvoiceList.clear();
        Constants.CollAmount = "0";

        mStrParentID = Constants.getName(Constants.ChannelPartners, Constants.ParentID, Constants.CPNo, mStrBundleRetID);

        tv_inv_collection_amount = (TextView) findViewById(R.id.tv_inv_collection_amount);

        ll_current_invoice = (LinearLayout) findViewById(R.id.ll_current_invoice);
        ll_advance_adj_amt = (LinearLayout) findViewById(R.id.ll_advance_adj_amt);
        ll_advance_adj_amt.setVisibility(View.GONE);

        ll_coll_amount = (LinearLayout) findViewById(R.id.ll_coll_amount);
        ll_payment_mode = (LinearLayout) findViewById(R.id.ll_payment_mode);

        tv_advance_adjust_amt = (TextView) findViewById(R.id.tv_advance_adjust_amt);

        ll_branch_name = (LinearLayout) findViewById(R.id.ll_branch_name);
        ll_utr_number = (LinearLayout) findViewById(R.id.ll_utr_number);
        tv_utr_label= (TextView) findViewById(R.id.tv_utr_label);
        tv_collection_date = (TextView) findViewById(R.id.tv_collection_date);
        tv_cheque_date= (TextView) findViewById(R.id.tv_cheque_date);
        ll_branch_name.setVisibility(View.VISIBLE);
        setDatePickerForChequeDate();
        tv_current_invoice_value = (TextView) findViewById(R.id.tv_current_invoice_value);
        tv_outstanding_value = (TextView) findViewById(R.id.tv_outstanding_value);

        if (mDoubBundleCurrentInv > 0) {
            ll_current_invoice.setVisibility(View.VISIBLE);
            tv_current_invoice_value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(mDoubBundleCurrentInv + ""));
        } else {
            ll_current_invoice.setVisibility(View.GONE);
        }
//        getAdvanceAmt();

//        getOutStandingAmount();
        edit_amount = (EditText) findViewById(R.id.edit_collection_amount);
        edit_remarks = (EditText) findViewById(R.id.edit_remarks);
        edit_utr_no = (EditText) findViewById(R.id.edit_utr_number);
        edit_branch_name = (EditText) findViewById(R.id.edit_branch_name);
        edit_utr_no.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        edit_utr_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edit_utr_no.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_branch_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edit_branch_name.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getPaymentModeDetails();
        getCollectionMethodDetails();
//        getBankNames();



        edit_amount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        edit_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edit_amount.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        UtilConstants.editTextDecimalFormat(edit_amount, 13, 2);
        tv_inv_collection_amount.setVisibility(View.GONE);
        edit_remarks.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});

        final Calendar calCollectionDate = Calendar.getInstance();
        mYear = calCollectionDate.get(Calendar.YEAR);
        mMonth = calCollectionDate.get(Calendar.MONTH);
        mDay = calCollectionDate.get(Calendar.DAY_OF_MONTH);
        mnt = mMonth + 1;
        if (mnt < 10)
            mon = "0" + mnt;
        else
            mon = "" + mnt;
        day = "" + mDay;
        if (mDay < 10)
            day = "0" + mDay;

        mStrCurrentDate = mYear + "-" + mon + "-" + day;
        tv_collection_date.setText(new StringBuilder().append(mDay)
                .append("/").append(UtilConstants.MONTHS_NUMBER[mMonth])
                .append("").append("/").append(mYear));

        edit_remarks.addTextChangedListener(watcher);
        edit_utr_no.addTextChangedListener(watcher);
        initializeKeyboardDependencies();

        edit_amount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.requestFocus();
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                showCustomKeyboard(view);
                Constants.setCursorPostion(edit_amount,view,motionEvent);
                return true;
            }
        });
        edit_amount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus)
                {

                    showCustomKeyboard(view);
                }

                else
                {
                    hideCustomKeyboard();
                }

            }
        });
    }
    private void getRetValues() {
        mArrayRetVal = Constants.getDistributorsByCPNO(mStrBundleCPGUID32);
    }
    private void setDatePickerForChequeDate() {

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        final DatePickerDialog dialog = new DatePickerDialog(CollectionCreateActivity.this, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        tv_cheque_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
                cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
                cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
                cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));

                //Get last three months date
                Calendar calendarPast = Calendar.getInstance();
                calendarPast.add(Calendar.MONTH, -3);

                //Get Next three months date
                Calendar calendarFuture = Calendar.getInstance();
                calendarFuture.add(Calendar.MONTH, 3);

                // Cheque Date allow past and future three months only
                dialog.getDatePicker().setMinDate(calendarPast.getTimeInMillis());
                dialog.getDatePicker().setMaxDate(calendarFuture.getTimeInMillis());

//                dialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                dialog.show();
            }
        });


    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        if ((monthOfYear + 1) > 9)
            mStrSelChequeDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + "T00:00:00";
        else
            mStrSelChequeDate = year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth + "T00:00:00";
        if (!mStrSelChequeDate.equalsIgnoreCase(""))
            tv_cheque_date.setBackgroundResource(R.drawable.textview_transprent);

        tv_cheque_date.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(dayOfMonth).append("/").append(monthOfYear + 1).append("/").append(year).append(" "));
    }

    public void initializeKeyboardDependencies()
    {
        keyboardView = (KeyboardView)findViewById(R.id.keyboard);
        keyboard = new Keyboard(CollectionCreateActivity.this, R.xml.ll_dot_key_board);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    //Text watcher for editTexts text change
    TextWatcher watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (edit_remarks.getText().hashCode() == s.hashCode()) {
                edit_remarks.setError(null);
            } else if (edit_utr_no.getText().hashCode() == s.hashCode()) {
                edit_utr_no.setError(null);
            } else if (edit_amount.getText().hashCode() == s.hashCode()) {
                edit_amount.setError(null);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /*Gets outstanding amount for retailer*/
    private void getOutStandingAmount() {

        try {
            alOutstandingsBean = OfflineManager.getOutstandingList(Constants.OutstandingInvoices + "?$filter=" + Constants.SoldToID + " eq '" + mStrBundleRetID + "'" + " and " + Constants.PaymentStatusID + " ne '" + "03" + "'", getApplicationContext(), "", mStrBundleCPGUID32);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        double mdouDevCollAmt=0.0;
        try {
            mdouDevCollAmt = OfflineManager.getDeviceCollAmt(CollectionCreateActivity.this,mStrBundleRetID);
        } catch (Exception e) {
            mdouDevCollAmt = 0.0;
        }


        double totalOutVal = 0.00;
        for (OutstandingBean invoice : alOutstandingsBean) {
            totalOutVal = totalOutVal + (Double.parseDouble(invoice.getInvoiceAmount()) - Double.parseDouble(invoice.getCollectionAmount()));
        }
        tv_outstanding_value.setText(UtilConstants.removeLeadingZerowithTwoDecimal((totalOutVal -mdouDevCollAmt) + ""));
    }

    /*gets collections methods(values for drop down like Collection Mode)*/
    private void getCollectionMethodDetails() {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq 'CollectionTypeID' and "+Constants.EntityType+" eq 'Collection'";
//            mArrayCollectionTypeVal = OfflineManager.getConfigListWithDefultVal(mStrConfigQry);
            mArrayCollectionTypeVal = OfflineManager.getConfigListWithDefaultValAndNone(mStrConfigQry,"");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

        if (mArrayCollectionTypeVal == null) {
            mArrayCollectionTypeVal = new String[4][1];
            mArrayCollectionTypeVal[0][0] = "";
            mArrayCollectionTypeVal[1][0] = "";
            mArrayCollectionTypeVal[2][0] = "";
            mArrayCollectionTypeVal[3][0] = "";
        }

        ArrayAdapter<String> paymentModeAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, mArrayCollectionTypeVal[1]);
        paymentModeAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_collection_method.setAdapter(paymentModeAdapter);

        for (int i = 0; i < mArrayCollectionTypeVal[1].length; i++) {
            if (mStrDefultRefType.equalsIgnoreCase(mArrayCollectionTypeVal[3][i])) {
                sp_collection_method.setSelection(i);
                break;
            }
        }
        sp_collection_method.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                sp_collection_method.setBackgroundResource(R.drawable.spinner_bg);
                mStrCollectionTypeCode = mArrayCollectionTypeVal[0][position];
                mStrCollectionTypeDesc = mArrayCollectionTypeVal[1][position];
                if (mStrCollectionTypeCode.equalsIgnoreCase(Constants.str_03)) {
                    mBooleanCollectionWithReference = false;

                    menu_save.setVisible(false);
                    menu_next.setVisible(true);

                    /*if( mStrCollectionTypeCode.equalsIgnoreCase(Constants.str_05)){
                        ll_advance_adj_amt.setVisibility(View.VISIBLE);
                        ll_coll_amount.setVisibility(View.GONE);
                        ll_payment_mode.setVisibility(View.GONE);
                        tv_advance_adjust_amt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(mDouAdvanceAmt + "")+" "+Constants.getCurrency());
                        if(mDouAdvanceAmt<=0){
                            menu_next.setVisible(false);
                        }

                    }else{
                        edit_amount.setText("");
                        ll_advance_adj_amt.setVisibility(View.GONE);
                        ll_coll_amount.setVisibility(View.VISIBLE);
                        ll_payment_mode.setVisibility(View.VISIBLE);
                    }*/

                } else {

                    ll_advance_adj_amt.setVisibility(View.GONE);
                    ll_coll_amount.setVisibility(View.VISIBLE);
                    ll_payment_mode.setVisibility(View.VISIBLE);

                    tv_inv_collection_amount.setVisibility(View.GONE);
                    edit_amount.setVisibility(View.VISIBLE);
                    Constants.alTempInvoiceList.clear();
                    Constants.CollAmount = "0";
                    mBooleanCollectionWithReference = true;

                    menu_save.setVisible(true);
                    menu_next.setVisible(false);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

//    private void getAdvanceAmt(){
//        double mDouDevAdvAmt =  OfflineManager.getDeviceAdvAmtOrAdjustAmt(CollectionCreateActivity.this,mStrBundleRetID,Constants.str_02);
//        double mDouAdvAmtFromCP =  OfflineManager.getAdvnceAmtFromCP(Constants.ChannelPartners + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrBundleCPGUID.toUpperCase() + "' ",Constants.OpenAdvanceAmt);
//        double mDouDevAdvAdjAmt = OfflineManager.getDeviceAdvAmtOrAdjustAmt(CollectionCreateActivity.this,mStrBundleRetID,Constants.str_05);
//
//        mDouAdvanceAmt = mDouDevAdvAmt + mDouAdvAmtFromCP - mDouDevAdvAdjAmt;
//    }

    /*gets Payment modes(values for drop down)*/
    private void getPaymentModeDetails() {
        try {

            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq 'PaymentMethodID' and "+Constants.EntityType+" eq 'Collection' ";
            mArrayPaymentModeVal = OfflineManager.getConfigListWithDefaultValAndNone(mStrConfigQry,"");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

        if (mArrayPaymentModeVal == null) {
            mArrayPaymentModeVal = new String[4][1];
            mArrayPaymentModeVal[0][0] = "";
            mArrayPaymentModeVal[1][0] = "";
            mArrayPaymentModeVal[2][0] = "";
            mArrayPaymentModeVal[3][0] = "";
        }

        ArrayAdapter<String> paymentModeAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_textview, mArrayPaymentModeVal[1]);
        paymentModeAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_payment_method.setAdapter(paymentModeAdapter);
        sp_payment_method.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                sp_payment_method.setBackgroundResource(R.drawable.spinner_bg);
                mStrPaymentModeCode = mArrayPaymentModeVal[0][position];
                mStrPaymentModeDesc = mArrayPaymentModeVal[1][position];


                if (mStrPaymentModeCode.equalsIgnoreCase("C") || mStrPaymentModeCode.equalsIgnoreCase("")) {
                    mStrSelChequeDate = "";
                    tv_cheque_date.setText("");
                    edit_utr_no.setText("");
                    edit_branch_name.setText("");

                    ll_cheque_date.setVisibility(View.GONE);
                    ll_bank_name.setVisibility(View.GONE);
                    ll_branch_name.setVisibility(View.GONE);
                    ll_utr_number.setVisibility(View.GONE);
                    sp_bank_names.setSelection(0);
                    sp_bank_names.setBackgroundResource(R.drawable.spinner_bg);
                } else  {
                    ll_cheque_date.setVisibility(View.VISIBLE);
//                    ll_bank_name.setVisibility(View.VISIBLE);
                    ll_branch_name.setVisibility(View.VISIBLE);
                    ll_utr_number.setVisibility(View.VISIBLE);
                    sp_bank_names.setSelection(0);
                    sp_bank_names.setBackgroundResource(R.drawable.spinner_bg);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /*gets Banks Names(values for drop down bank name)*/
    private void getBankNames() {
       /* try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq 'BankID' " +
                    "&$orderby=" + Constants.Description + "%20asc";

            mArrayBankNameVal = OfflineManager.getConfigListWithDefaultValAndNone(mStrConfigQry,"");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }*/
        if (mArrayBankNameVal == null) {
            mArrayBankNameVal = new String[2][4];
            mArrayBankNameVal[0][0] = "00";
            mArrayBankNameVal[1][0] = "None";

            mArrayBankNameVal[0][1] = "01";
            mArrayBankNameVal[1][1] = "Axis Bank";

            mArrayBankNameVal[0][2] = "02";
            mArrayBankNameVal[1][2] = "Hdfc Bank";

            mArrayBankNameVal[0][3] = "03";
            mArrayBankNameVal[1][3] = "SBI Bank";
        }

        ArrayAdapter<String> bankNameAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, mArrayBankNameVal[1]);
        bankNameAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_bank_names.setAdapter(bankNameAdapter);
        sp_bank_names.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                sp_bank_names.setBackgroundResource(R.drawable.spinner_bg);
                mStrBankNamesCode = mArrayBankNameVal[0][position];
                mStrBankName = mArrayBankNameVal[1][position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    @SuppressLint("NewApi")
    /*dialog to select date from date picker*/
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case Constants.DATE_DIALOG_ID:
                DatePickerDialog datePicker = new DatePickerDialog(this, mDateSetListener,
                        mYear, mMonth, mDay);
                Calendar c = Calendar.getInstance();
                Date newDate = c.getTime();
                datePicker.getDatePicker().setMaxDate(newDate.getTime());
                return datePicker;
        }
        return null;
    }

    /*On selection of date from date picker this class will be called*/
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker v, int year, int monthOfYear,
                              int dayOfMonth) {
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

            tv_collection_date.setText(new StringBuilder().append(mDay)
                    .append("/").append(UtilConstants.MONTHS_NUMBER[mMonth])
                    .append("").append("/").append(mYear));
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_collection_create, menu);
        menu_save = menu.findItem(R.id.menu_collection_save);
        menu_next = menu.findItem(R.id.menu_collection_next);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mBooleanCollectionWithReference) {
            menu_save.setVisible(true);
            menu_next.setVisible(false);
        } else {
            menu_save.setVisible(false);
            menu_next.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_collection_save:
                onSave();
                break;
            case R.id.menu_collection_next:
                onNext();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private void getValuesFromEditText() {
        if (mStrPaymentModeCode.equalsIgnoreCase("C")) {
            mStrInstrumentNo = "";
            mStrBranchName = "";
            mStrUTRNo = "";
        } else if (mStrPaymentModeCode.equalsIgnoreCase("D")) {
            mStrInstrumentNo = !edit_utr_no.getText().toString().equalsIgnoreCase("") ? edit_utr_no.getText().toString() : "";
        } else {
            mStrInstrumentNo = !edit_utr_no.getText().toString().equalsIgnoreCase("") ? edit_utr_no.getText().toString() : "";
        }
        mStrBankName = !edit_branch_name.getText().toString().equalsIgnoreCase("") ? edit_branch_name.getText().toString() : "";

    }

    /*Navigating to next screen with values from this screen(Invoice selection Activity)*/
    private void onNext() {
        if (validateAllField()) {


            getValuesFromEditText();

            mStrAmount = edit_amount.getText().toString();
            mStrRemarks = !edit_remarks.getText().toString().equalsIgnoreCase("") ? edit_remarks.getText().toString() : "";
            CollectionBean headerBean = new CollectionBean();

            headerBean.setCPNo(mStrBundleRetID);

            if (mStrPaymentModeCode.equalsIgnoreCase("C")) {
                headerBean.setBankID("");
                headerBean.setBankName("");
                headerBean.setInstrumentNo("");
                headerBean.setURTNo("");
                headerBean.setBranchName("");
                headerBean.setInstrumentDate("");
            }  else {
                headerBean.setBankID(mStrBankNamesCode);
                headerBean.setBankName(mStrBankName);
                headerBean.setInstrumentNo(mStrInstrumentNo);
                headerBean.setURTNo("");
                headerBean.setBranchName(mStrBranchName);
                headerBean.setInstrumentDate(mStrSelChequeDate);
            }

            headerBean.setAmount(mStrAmount);
            headerBean.setCPTypeID(Constants.str_02);
            headerBean.setRemarks(mStrRemarks);

                headerBean.setFIPDocType(mStrCollectionTypeCode);

            headerBean.setFIPDate(mStrCurrentDate);
                headerBean.setPaymentModeID(mStrPaymentModeCode);
                headerBean.setPaymentModeDesc(mStrPaymentModeDesc);

            headerBean.setCurrency(mArrayRetVal[0][0]);

            headerBean.setParentID(mStrParentID);
            headerBean.setCPGUID(mStrBundleCPGUID32);

            ArrayList<CollectionBean> arrayListHeaderNew = new ArrayList<>();
            arrayListHeaderNew.add(headerBean);

            Intent intentInvSelectionActivity = new Intent(CollectionCreateActivity.this,
                    InvoiceSelectionActivity.class);
            intentInvSelectionActivity.putExtra(Constants.CollectionHeaderTable, arrayListHeaderNew);
            intentInvSelectionActivity.putExtra(Constants.CPNo, mStrBundleRetID);
            intentInvSelectionActivity.putExtra(Constants.RetailerName, mStrBundleRetName);
            intentInvSelectionActivity.putExtra(Constants.CPUID, mStrBundleRetailerUID);
            intentInvSelectionActivity.putExtra(Constants.CPGUID, mStrBundleCPGUID);
            intentInvSelectionActivity.putExtra(Constants.CPGUID32, mStrBundleCPGUID32);
            intentInvSelectionActivity.putExtra(Constants.CPGUID, mStrBundleCPGUID);
            intentInvSelectionActivity.putExtra(Constants.ReferenceTypeID, mStrCollectionTypeCode);
            intentInvSelectionActivity.putExtra(Constants.ReferenceTypeDesc, mStrCollectionTypeDesc);
            intentInvSelectionActivity.putExtra(Constants.comingFrom, mStrComingFrom);

                intentInvSelectionActivity.putExtra(Constants.InvoiceAmount, Double.parseDouble(mStrAmount));
                intentInvSelectionActivity.putExtra(Constants.PaymentMode, mStrPaymentModeCode);


            intentInvSelectionActivity.putExtra(Constants.OutAmount, mDoubleOutAmount + "");

            intentInvSelectionActivity.putExtra(Constants.InstrumentNo, mStrInstrumentNo);
            intentInvSelectionActivity.putExtra(Constants.FIPDate, mStrCurrentDate);
            intentInvSelectionActivity.putExtra(Constants.PassedFrom, 100);
            startActivityForResult(intentInvSelectionActivity, 100);
        } else {
            UtilConstants.showAlert(getString(R.string.validation_plz_enter_mandatory_flds), CollectionCreateActivity.this);

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        if (requestCode == 100) {
            // Make sure the request was successful
            if (resultCode == 100) {
                //TODO
            }
        }
    }

    /*save collection in offline store*/
    private void onSave() {

        LocationUtils.checkLocationPermission(CollectionCreateActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                Log.d("location fun","1");

                if (status) {

                }else{
                    return;
                }
            }
        });
        Constants.getLocation(CollectionCreateActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {

                if(status){
                    Constants.FIPDocumentNumber = "";
                    if (validateAllField()) {
                        getValuesFromEditText();
                        mStrAmount = edit_amount.getText().toString();
                        mStrRemarks = !edit_remarks.getText().toString().equalsIgnoreCase("") ? edit_remarks.getText().toString() : "";

                        Set<String> set = new HashSet<>();
                        doc_no = (System.currentTimeMillis() + "");

                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);

                        String loginIdVal = sharedPreferences.getString(Constants.username, "");

                        GUID guid = GUID.newRandom();



                        arrtable = new ArrayList<HashMap<String, String>>();

                        HashMap<String, String> singleItem = new HashMap<String, String>();
                        GUID guidItem = GUID.newRandom();
                        singleItem.put(Constants.DocumentNo, doc_no.substring(3, 10));
                        singleItem.put(Constants.ItemNo, "10");
                        singleItem.put(Constants.Currency, mArrayRetVal[0][0]);
                        singleItem.put(Constants.CollectedAmount, Double.parseDouble(mStrAmount)+"");
                        arrtable.add(singleItem);
                        Gson gson = new Gson();
                        String jsonFromMap = "";
                        try {
                            jsonFromMap = gson.toJson(arrtable);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        dbHeaderTable = new Hashtable();

                        dbHeaderTable.put(Constants.DocumentNo, doc_no.substring(3, 10));
                        dbHeaderTable.put(Constants.CustomerNo, mStrBundleRetID);
                        dbHeaderTable.put(Constants.CustomerName, mStrBundleRetName);
                        if (!mStrPaymentModeCode.equalsIgnoreCase("C")) {
                            dbHeaderTable.put(Constants.BankName, mStrBankName);
                        } else {
                            dbHeaderTable.put(Constants.BankName, "");
                        }

                        dbHeaderTable.put(Constants.Amount, Double.parseDouble(mStrAmount)+"");
                        dbHeaderTable.put(Constants.Remarks, mStrRemarks);
                        dbHeaderTable.put(Constants.CollectionTypeID, mStrCollectionTypeCode);

                        dbHeaderTable.put(Constants.PaymentMethodID, mStrPaymentModeCode);
                        dbHeaderTable.put(Constants.PaymentMethodDesc, mStrPaymentModeDesc);
                        dbHeaderTable.put(Constants.CollectionTypeDesc, mStrCollectionTypeDesc);

                        dbHeaderTable.put(Constants.DocumentDate, mStrCurrentDate);

                        if (!mStrPaymentModeCode.equalsIgnoreCase("C")) {
                            dbHeaderTable.put(Constants.InstrumentDate, mStrSelChequeDate);
                            dbHeaderTable.put(Constants.InstrumentNo, mStrInstrumentNo);
                        }else{
                            dbHeaderTable.put(Constants.InstrumentDate, "");
                            dbHeaderTable.put(Constants.InstrumentNo, "");
                        }


                        dbHeaderTable.put(Constants.LOGINID, loginIdVal);
                        dbHeaderTable.put(Constants.Currency,  mArrayRetVal[0][0]);


                        dbHeaderTable.put(Constants.CreatedOn, UtilConstants.getNewDateTimeFormat());

                        dbHeaderTable.put(Constants.CreatedAt, UtilConstants.getOdataDuration().toString());

                        dbHeaderTable.put(Constants.EntityType, Constants.Collection);

                        dbHeaderTable.put(Constants.ItemsText, jsonFromMap);

                        set = sharedPreferences.getStringSet(Constants.CollList, null);

                        HashSet<String> setTemp = new HashSet<>();
                        if (set != null && !set.isEmpty()) {
                            Iterator itr = set.iterator();
                            while (itr.hasNext()) {
                                setTemp.add(itr.next().toString());
                            }
                        }
                        setTemp.add(doc_no);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putStringSet(Constants.CollList, setTemp);
                        editor.commit();

                        JSONObject jsonHeaderObject = new JSONObject(dbHeaderTable);

                        try {
                            ConstantsUtils.storeInDataVault(doc_no, jsonHeaderObject.toString(),CollectionCreateActivity.this);

                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                /*Constants.onVisitActivityUpdate(mStrBundleCPGUID32, loginIdVal,
                        guid.toString36().toUpperCase(), Constants.str_02, Constants.Collection);*/

                        navigateToVisit();

                    } else {
                        UtilConstants.showAlert(getString(R.string.validation_plz_enter_mandatory_flds), CollectionCreateActivity.this);
                    }

                }else{
                    return;
                }
            }
        });

          /*  if (!Constants.onGpsCheck(CollectionCreateActivity.this)) {
                return;
            }
            if (!UtilConstants.getLocation(CollectionCreateActivity.this)) {
                return;
            }*/



    }
    public boolean isCustomKeyboardVisible() {
        return keyboardView.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onBackPressed() {
        if(isCustomKeyboardVisible())
        {
            hideCustomKeyboard();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(CollectionCreateActivity.this, R.style.MyTheme);
            builder.setMessage(R.string.alert_exit_create_collection).setCancelable(false)
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

    private void onNavigateToRetDetilsActivity() {
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(CollectionCreateActivity.this, CustomerDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        if(!Constants.OtherRouteNameVal.equalsIgnoreCase("")){
            intentNavPrevScreen.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
            intentNavPrevScreen.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
        }

        startActivity(intentNavPrevScreen);
    }

    /*Validates required data for Payment mode RTGS/NEFT*/
    public boolean validateRTGSNEFT() {
        boolean returnVal = true;
//        if (mStrBankNamesCode.equalsIgnoreCase("")) {
//            sp_bank_names.setBackgroundResource(R.drawable.error_spinner);
//            returnVal = false;
//        }

        if (edit_branch_name.getText() == null) {
            edit_branch_name.setBackgroundResource(R.drawable.edittext_border);
            returnVal = false;
        } else if (edit_branch_name.getText().toString().trim().equalsIgnoreCase("")) {
            if (edit_branch_name.getText().toString().trim().equalsIgnoreCase(""))
                edit_branch_name.setBackgroundResource(R.drawable.edittext_border);
            returnVal = false;
        }
        if (mStrSelChequeDate.equalsIgnoreCase("")) {
            tv_cheque_date.setBackgroundResource(R.drawable.textview_border);
            returnVal = false;
        }
        if (edit_utr_no.getText() == null) {
            edit_utr_no.setBackgroundResource(R.drawable.edittext_border);
            returnVal = false;
        } else if (edit_utr_no.getText().toString().trim().equalsIgnoreCase("")) {
            if (edit_utr_no.getText().toString().trim().equalsIgnoreCase(""))
                edit_utr_no.setBackgroundResource(R.drawable.edittext_border);
            returnVal = false;
        }
        return returnVal;
    }

    /*Validates required data for Payment mode Cheque/DD*/
    public boolean validateChequeDD() {
        boolean returnVal = true;
//        if (mStrBankNamesCode.equalsIgnoreCase("")) {
//            sp_bank_names.setBackgroundResource(R.drawable.error_spinner);
//            returnVal = false;
//        }
        if (mStrSelChequeDate.equalsIgnoreCase("")) {
            tv_cheque_date.setBackgroundResource(R.drawable.textview_border);
            returnVal = false;
        }
        if (edit_branch_name.getText() == null) {
            edit_branch_name.setBackgroundResource(R.drawable.edittext_border);
            returnVal = false;
        } else if (edit_branch_name.getText().toString().trim().equalsIgnoreCase("")) {
            if (edit_branch_name.getText().toString().trim().equalsIgnoreCase(""))
                edit_branch_name.setBackgroundResource(R.drawable.edittext_border);
            returnVal = false;
        }
        if (edit_utr_no.getText() == null || edit_amount.getText() == null) {
            if (edit_utr_no.getText() == null)
                edit_utr_no.setBackgroundResource(R.drawable.edittext_border);
            if (edit_amount.getText() == null)
                edit_amount.setBackgroundResource(R.drawable.edittext_border);
            returnVal = false;
        } else if (edit_utr_no.getText().toString().trim().equalsIgnoreCase("") || edit_amount.getText().toString().trim()
                .equalsIgnoreCase("") || edit_amount.getText().toString().trim()
                .equalsIgnoreCase(".")) {
            if (edit_utr_no.getText().toString().trim().equalsIgnoreCase(""))
                edit_utr_no.setBackgroundResource(R.drawable.edittext_border);
            if (edit_amount.getText().toString().trim().equalsIgnoreCase(""))
                edit_amount.setBackgroundResource(R.drawable.edittext_border);
            returnVal = false;
        } else if (Double.parseDouble(edit_amount.getText().toString()) <= 0) {
            edit_amount.setBackgroundResource(R.drawable.edittext_border);
            returnVal = false;
        }
        return returnVal;
    }

    /*Validates required data for Payment mode Cash*/
    public boolean validateCash() {
        boolean returnVal = true;

        if( !mStrCollectionTypeCode.equalsIgnoreCase(Constants.str_05)) {
            if (edit_amount.getText() == null) {
                returnVal = false;
            } else if (edit_amount.getText().toString().trim()
                    .equalsIgnoreCase("") || edit_amount.getText().toString().trim()
                    .equalsIgnoreCase(".")) {
                returnVal = false;
            } else if (Double.parseDouble(edit_amount.getText().toString()) <= 0) {
                returnVal = false;
            }
        }

        return returnVal;
    }


    /*Validates required data*/
    public boolean validateAllField() {
        boolean mboolTemp = true;
        if (!validateCash()) {
            edit_amount.setBackgroundResource(R.drawable.edittext_border);
            mboolTemp = false;
        }
        if (mStrCollectionTypeCode.equalsIgnoreCase("")) {
            sp_collection_method.setBackgroundResource(R.drawable.error_spinner);
            mboolTemp = false;
        }
        if( !mStrCollectionTypeCode.equalsIgnoreCase(Constants.str_05)) {
            if (mStrPaymentModeCode.equalsIgnoreCase("")) {
                sp_payment_method.setBackgroundResource(R.drawable.error_spinner);

                mboolTemp = false;
            } else {
                if ((mStrPaymentModeDesc.contains(Constants.RTGS) || mStrPaymentModeDesc.contains(Constants.NEFT))) {
                    if (!validateRTGSNEFT()) {
                        mboolTemp = false;
                    }
                } else if (mStrPaymentModeDesc.equalsIgnoreCase(Constants.DD) || mStrPaymentModeDesc.equalsIgnoreCase(Constants.Cheque)) {
                    if (!validateChequeDD()) {
                        mboolTemp = false;
                    }
                }

            }
        }

        return mboolTemp;
    }

    @Override
    public void onRequestError(int operation, Exception e) {
        LogManager.writeLogError(Constants.error_in_collection + e.getMessage());

        AlertDialog.Builder builder = new AlertDialog.Builder(
                CollectionCreateActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.error_occured_during_save)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();
                                    onNavigateToRetDetilsActivity();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });
        builder.show();
    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.Create.getValue()) {


            Set<String> set = new HashSet<>();
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
            set = sharedPreferences.getStringSet(Constants.CollList, null);
            HashSet<String> setTemp = new HashSet<>();
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
                while (itr.hasNext()) {
                    setTemp.add(itr.next().toString());
                }
            }

            setTemp.remove(doc_no);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(Constants.CollList, setTemp);
            editor.commit();

            try {
                ConstantsUtils.storeInDataVault(doc_no, "",this);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            if (!UtilConstants.isNetworkAvailable(CollectionCreateActivity.this)) {
                onNoNetwork();
            } else {
                OfflineManager.flushQueuedRequests(CollectionCreateActivity.this);
            }
        } else if (operation == Operation.OfflineFlush.getValue()) {
            OfflineManager.refreshRequests(getApplicationContext(), Constants.VisitActivities + "," + Constants.FinancialPostings
                    + "," + Constants.FinancialPostingItemDetails + "," + Constants.SSINVOICES + "," + Constants.SSInvoiceItemDetails, CollectionCreateActivity.this);
        } else if (operation == Operation.OfflineRefresh.getValue()) {

            popUpText = getString(R.string.msg_coll_created);

            AlertDialog.Builder builder = new AlertDialog.Builder(
                    CollectionCreateActivity.this, R.style.MyTheme);
            builder.setMessage(popUpText)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface Dialog,
                                        int id) {
                                    try {
                                        Dialog.cancel();
                                        onNavigateToRetDetilsActivity();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                }
                            });
            builder.show();
        }
    }


    public void navigateToVisit() {
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
        popUpText = getString(R.string.msg_coll_created);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                CollectionCreateActivity.this, R.style.MyTheme);
        builder.setMessage(popUpText)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();
                                    onNavigateToRetDetilsActivity();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });
        builder.show();
    }

    private void onNoNetwork() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                CollectionCreateActivity.this, R.style.MyTheme);
        builder.setMessage(
                R.string.alert_sync_cannot_be_performed)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        onNavigateToRetDetilsActivity();
                    }
                });

        builder.show();
    }
    /**
     * get salesPerson values
     */


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                onBackPressed();
                break;
            case R.id.tv_submit:
                onSave();
                break;
        }
    }
    public void hideCustomKeyboard() {
        keyboardView.setVisibility(View.GONE);
        keyboardView.setEnabled(false);
    }
    public void showCustomKeyboard( View v) {

        keyboardView.setVisibility(View.VISIBLE);
        keyboardView.setEnabled(true);
        if( v!=null ){
            ((InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    public void onKey(int primaryCode, int[] keyCodes) {
        switch (primaryCode)
        {
            case 81:
                //Plus
                Constants.incrementTextValues(edit_amount, Constants.Y);
                break;
            case 69:
                //Minus
                Constants.decrementEditTextVal(edit_amount, Constants.Y);
                break;
            case 1:
                //changeEditTextFocus(0);
                break;
            case 2:
                // changeEditTextFocus(1);
                break;
            case 56:
                if(!checkAlreadyDotIsThere())
                {
                    KeyEvent event = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                    dispatchKeyEvent(event);
                }

                break;

            default:
                //default numbers
                KeyEvent event = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                dispatchKeyEvent(event);
                break;
        }


    }

    private Boolean checkAlreadyDotIsThere() {

        String textValue = edit_amount.getText().toString();
        if(textValue.contains("."))
        {
            return true;
        }
        else
            return false;
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
