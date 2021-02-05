package com.rspl.sf.msfa.finance;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.smp.client.odata.exception.ODataException;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by e10526 on 26-04-2016.
 *
 */
@SuppressLint("NewApi")

public class InvoiceSelectionActivity extends AppCompatActivity implements UIListener,
        View.OnClickListener,KeyboardView.OnKeyboardActionListener {
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "",mStrBundleCPGUID="",mStrBundleTotalOutAmt="",mStrBundleCPGUID32="";
    private ArrayList<CollectionBean> arrayListHeaderTable = null;
    private Hashtable<String,String> headerTable=new Hashtable<>();
    private ArrayList<InvoiceBean> alInvoiceList = null;
    String invoiceQuantity[];
    private ArrayList<InvoiceBean> selectedInvoice = null;
    private ProgressDialog pdLoadDialog;
    private CollectionBean headerBean;
    private String mStrInstrumentNo = "", mStrPaymentModeCode = "", mStrCurrentDate = "";
    private static EditText[] newInvoiceEdit = null;

    private String popUpText = "";
    public HashMap<String, String> mapCheckedStateHashMap = new HashMap<String, String>();
    ScrollView sv_invoice_list;

    ArrayList<HashMap<String, String>> arrtable;
    double mDoubleBundleTotalInvAmt =0.0,mDoubleTotalInvSum=0.0,mDoubleTempOutAmt=0.0,mDoubleTempTotalAmt=0.0;
    private String doc_no ;
    TextView tv_total_inv_value,tv_adjust_inv_value;
    private  double mDoubleTotInvAmt =0.0;
    private int passedValues;
private String[][] mArrayRetVal=null;
    //new 28112016
    private String mStrBundleRetailerUID = "";
    //new
    String mStrComingFrom = "",mStrRefTypeID="",mStrRefTypeDesc="";


    //TODO
    KeyboardView keyboardView;
    Keyboard keyboard;
    private static int lastSelectedEditText = 0;
    String mRouteSchGuid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
    //   ActionBarView.initActionBarView(this, true,getString(R.string.title_out_standing_bills));
        setContentView(R.layout.activity_invoice_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_out_standing_bills), 0);

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            Bundle bundleExtras = getIntent().getExtras();
            if (bundleExtras != null) {
                mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
                mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
                mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
                mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);

                mDoubleBundleTotalInvAmt = bundleExtras.getDouble(Constants.InvoiceAmount);
                mStrBundleTotalOutAmt = bundleExtras.getString(Constants.OutAmount);
                mStrInstrumentNo = bundleExtras.getString(Constants.InstrumentNo);
                mStrPaymentModeCode = bundleExtras.getString(Constants.PaymentMode);
                mStrCurrentDate = bundleExtras.getString(Constants.FIPDate);
                passedValues = bundleExtras.getInt(Constants.PassedFrom);
                mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
                mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
                mStrRefTypeID = bundleExtras.getString(Constants.ReferenceTypeID);
                mStrRefTypeDesc = bundleExtras.getString(Constants.ReferenceTypeDesc);

                mDoubleTempOutAmt = mDoubleBundleTotalInvAmt;
                tv_total_inv_value = (TextView) findViewById(R.id.tv_total_inv_value);
                tv_adjust_inv_value = (TextView) findViewById(R.id.tv_adjust_inv_value);

                arrayListHeaderTable = (ArrayList<CollectionBean>) getIntent().getSerializableExtra(Constants.CollectionHeaderTable);

                for (int i = 0; i < arrayListHeaderTable.size(); i++) {
                    headerBean = arrayListHeaderTable.get(i);
                }
                doc_no = (System.currentTimeMillis() + "");

                headerTable.put(Constants.BankName, headerBean.getBankName());
                headerTable.put(Constants.InstrumentNo, headerBean.getInstrumentNo());
                headerTable.put(Constants.Amount, headerBean.getAmount());
                headerTable.put(Constants.Remarks, headerBean.getRemarks());
                headerTable.put(Constants.CollectionTypeID, headerBean.getFIPDocType());
                headerTable.put(Constants.PaymentMethodID, headerBean.getPaymentModeID());
                headerTable.put(Constants.PaymentMethodDesc, headerBean.getPaymentModeDesc());
                headerTable.put(Constants.CollectionTypeDesc, mStrRefTypeDesc);
                headerTable.put(Constants.DocumentNo, doc_no.substring(3, 10));
                headerTable.put(Constants.CustomerNo, mStrBundleRetID);
                headerTable.put(Constants.CustomerName, mStrBundleRetName);
                headerTable.put(Constants.DocumentDate, headerBean.getFIPDate());
                headerTable.put(Constants.InstrumentDate, headerBean.getInstrumentDate());
                headerTable.put(Constants.Currency, headerBean.getCurrency());
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
                String loginIdVal = sharedPreferences.getString(Constants.username, "");
                headerTable.put(Constants.LOGINID, loginIdVal);
                headerTable.put(Constants.CreatedOn, UtilConstants.getNewDateTimeFormat());
                headerTable.put(Constants.CreatedAt, UtilConstants.getOdataDuration().toString());
                headerTable.put(Constants.EntityType, Constants.Collection);

            }
            initUI();
    }





    public void initializeKeyboardDependencies() {
        keyboardView = (KeyboardView) findViewById(R.id.keyboard_custom_invoice_sel);
        keyboard = new Keyboard(InvoiceSelectionActivity.this, R.xml.ll_up_down_keyboard);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    void initUI() {
        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView tv_coll_amt_label = (TextView) findViewById(R.id.tv_coll_amt_label);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);
        sv_invoice_list = (ScrollView) findViewById(R.id.sv_invoice_list);

        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetailerUID);

        if(mStrRefTypeID.equalsIgnoreCase(Constants.str_05)){
            tv_coll_amt_label.setText(getString(R.string.advance_coll_amount));
        }else{

            tv_coll_amt_label.setText(getString(R.string.total_coll_amount));
        }

        //get invoices for collection
        try {
            new GetInvoices().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initializeKeyboardDependencies();
    }

    @SuppressLint("InflateParams")
    /*Displays invoices for collection*/
    private void displayInvoiceValues() {


        @SuppressLint("InflateParams")
        TableLayout tlInvoiceList = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_table, null, false);

        LinearLayout llInvoiceList;

            if (!alInvoiceList.isEmpty()
                    && alInvoiceList.size() > 0) {

                    invoiceQuantity = new String[alInvoiceList.size()];
                    newInvoiceEdit = new EditText[alInvoiceList.size()];

                    for (int i = 0; i < alInvoiceList.size(); i++) {
                        final int selvalue = i;
                        final InvoiceBean newbean = alInvoiceList.get(i);
                        llInvoiceList = (LinearLayout) LayoutInflater.from(this)
                                .inflate(R.layout.invoice_selection_list_item,
                                        null, false);

                        TextView tvInvNo = (TextView)llInvoiceList.findViewById(R.id.tv_invoice_number);

                        tvInvNo.setText(alInvoiceList.get(i).getInvoiceNo());

                        if(alInvoiceList.get(i).getDeviceInvStatus().equalsIgnoreCase("")) {
                            tvInvNo.setTextColor(getResources().getColor(R.color.icon_text_blue));
                        }else{
                            tvInvNo.setTextColor(getResources().getColor(R.color.text_red));
                        }


                        ((TextView) llInvoiceList.findViewById(R.id.tv_invoice_amt))
                                .setText(UtilConstants.removeLeadingZerowithTwoDecimal(alInvoiceList.get(i).getInvoiceAmount())+" "+alInvoiceList.get(i).getCurrency());
                        //new 05122016
                        TextView tvBalAmt = (TextView)llInvoiceList.findViewById(R.id.tv_bal_amt);
                        tvBalAmt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(alInvoiceList.get(i).getInvoiceOutstanding())+" "+alInvoiceList.get(i).getCurrency());

                newInvoiceEdit[i] = (EditText) llInvoiceList.findViewById(R.id.ed_invoice_inv_amount);

                newInvoiceEdit[i] .setCursorVisible(true);
                newInvoiceEdit[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {

                        if (hasFocus) {
                            lastSelectedEditText = selvalue;
                            showCustomKeyboard(v);
                        } else {
                            lastSelectedEditText = selvalue;
                            hideCustomKeyboard();
                        }

                    }
                });
               newInvoiceEdit[i].setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        lastSelectedEditText = selvalue;
                        v.requestFocus();
                        showCustomKeyboard(v);
                        Constants.setCursorPostion(newInvoiceEdit[selvalue],v,event);
                        return true;
                    }
                });



                        mapCheckedStateHashMap.put(newbean.getInvoiceNo(), newbean.getInputInvAmount());

                      if(newbean.isItemSelected()){
                          newInvoiceEdit[selvalue].setText(newbean.getInputInvAmount());

                          newbean.setItemSelected(true);
                          selectedInvoice.add(newbean);

                      }

                        if(mDoubleTempOutAmt>0){
                            if(mDoubleTempOutAmt >= Double.parseDouble(alInvoiceList.get(i).getInvoiceOutstanding())  && mDoubleTempTotalAmt!=mDoubleBundleTotalInvAmt ){

                                mDoubleTempOutAmt = mDoubleTempOutAmt - Double.parseDouble(alInvoiceList.get(i).getInvoiceOutstanding());

                                mDoubleTempTotalAmt = mDoubleTempTotalAmt + Double.parseDouble(alInvoiceList.get(i).getInvoiceOutstanding());

                                newInvoiceEdit[selvalue].setText(UtilConstants.removeLeadingZero(alInvoiceList.get(i).getInvoiceOutstanding()));

                                newbean.setItemSelected(true);
                                newbean.setInputInvAmount(UtilConstants.removeLeadingZero(alInvoiceList.get(i).getInvoiceOutstanding()));

                                mapCheckedStateHashMap.put(newbean.getInvoiceNo(), UtilConstants.removeLeadingZero(alInvoiceList.get(i).getInvoiceOutstanding()));

                            }else if(mDoubleTempOutAmt <= Double.parseDouble(alInvoiceList.get(i).getInvoiceOutstanding()) && mDoubleTempTotalAmt!=mDoubleBundleTotalInvAmt ){

                                mDoubleTempTotalAmt = mDoubleTempTotalAmt + mDoubleTempOutAmt;

                                newInvoiceEdit[selvalue].setText(UtilConstants.removeLeadingZero(mDoubleTempOutAmt + ""));
                                newbean.setItemSelected(true);
                                newbean.setInputInvAmount(UtilConstants.removeLeadingZero(mDoubleTempOutAmt + ""));
                                mapCheckedStateHashMap.put(newbean.getInvoiceNo(), UtilConstants.removeLeadingZero(mDoubleTempOutAmt + ""));
                            }else{
                                newbean.setItemSelected(false);
                            }
                        }else{
                            newbean.setItemSelected(false);
                        }


                        selectedInvoice.add(newbean);

                UtilConstants.editTextDecimalFormat(newInvoiceEdit[selvalue],13,2);
                newInvoiceEdit[i].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence source, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        String s1 = s.toString();

                            if (s1.equals(".")) {
                                s1 = "0.";
                            }

                        if (!s1.equalsIgnoreCase(""))
                            newInvoiceEdit[selvalue].setBackgroundResource(R.drawable.edittext);
                        if (selectedInvoice.contains(newbean)) {
                            if (!s1.equalsIgnoreCase("")) {
                                newbean.setItemSelected(true);
                            } else {
                                newbean.setItemSelected(false);
                            }
                            newbean.setInputInvAmount(s1);
                        }
                        invoiceQuantity[selvalue] = s1;


                        if (mapCheckedStateHashMap.containsKey(newbean.getInvoiceNo())) {
                            mapCheckedStateHashMap.put(newbean.getInvoiceNo(), s1);
                        }




                                double mDouInvPrice = 0.0;
                                if (!mapCheckedStateHashMap.isEmpty()) {
                                    Iterator mapSelctedValues = mapCheckedStateHashMap.keySet()
                                            .iterator();
                                    while (mapSelctedValues.hasNext()) {
                                        String Key = (String) mapSelctedValues.next();
                                        Double invPrice = null;
                                        try {
                                            invPrice = Double.parseDouble(mapCheckedStateHashMap.get(Key).equalsIgnoreCase("") ? "0" : mapCheckedStateHashMap.get(Key));
                                        } catch (NumberFormatException e) {
                                            invPrice = 0.0;
                                        }
                                        mDouInvPrice = mDouInvPrice + invPrice;
                                    }
                                }
                                mDoubleTotInvAmt = mDouInvPrice;
                                tv_adjust_inv_value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(mDouInvPrice + "") + " " + headerBean.getCurrency());

                                if (mDoubleBundleTotalInvAmt == mDoubleTotInvAmt) {
                                    tv_adjust_inv_value.setTextColor(Color.GREEN);
                                } else if (mDoubleBundleTotalInvAmt > mDoubleTotInvAmt) {
                                    tv_adjust_inv_value.setTextColor(Color.parseColor(Constants.FFDA33));
                                } else {
                                    tv_adjust_inv_value.setTextColor(Color.RED);
                                }


                            }
                        });
                        tlInvoiceList.addView(llInvoiceList);
                    }


            } else {

                llInvoiceList = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.no_data_found_ll,
                                null,false);

                tlInvoiceList.addView(llInvoiceList);
            }

        double mDouInvPrice = 0.0;
        if (!mapCheckedStateHashMap.isEmpty()) {
            Iterator mapSelctedValues = mapCheckedStateHashMap.keySet()
                    .iterator();
            while (mapSelctedValues.hasNext()) {
                String Key = (String) mapSelctedValues.next();
                Double invPrice = null;
                try {
                    invPrice = Double.parseDouble(mapCheckedStateHashMap.get(Key).equalsIgnoreCase("") ? "0" : mapCheckedStateHashMap.get(Key));
                } catch (NumberFormatException e) {
                    invPrice = 0.0;
                }
                mDouInvPrice = mDouInvPrice + invPrice;
            }
        }
        mDoubleTotInvAmt = mDouInvPrice;

        tv_total_inv_value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(mDoubleBundleTotalInvAmt + "") + " " + headerBean.getCurrency());
        tv_adjust_inv_value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(mDoubleTotInvAmt + "") + " " + headerBean.getCurrency());

        if(mDoubleBundleTotalInvAmt==mDoubleTotInvAmt){
            tv_adjust_inv_value.setTextColor(Color.GREEN);
        }else if(mDoubleBundleTotalInvAmt>mDoubleTotInvAmt){
            tv_adjust_inv_value.setTextColor(Color.parseColor(Constants.FFDA33));
        }else {
            tv_adjust_inv_value.setTextColor(Color.RED);
        }

        sv_invoice_list.addView(tlInvoiceList);
        sv_invoice_list.requestLayout();

    }


    /*Shows popup below selected view*/
    public void showPopup(View v) {
        Context wrapper = new ContextThemeWrapper(getApplicationContext(), R.style.PopupMenu);
        PopupMenu popup = new PopupMenu(wrapper, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_collection_create, popup.getMenu());
            popup.getMenu().removeItem(R.id.menu_collection_next);


        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                onOptionsItemSelected(item);
                return true;
            }
        });

        popup.show();
    }

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_collection_create, menu);
        menu.removeItem(R.id.menu_collection_next);
        return true;
    }

    public boolean isCustomKeyboardVisible() {
        return keyboardView.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onBackPressed() {

        if (isCustomKeyboardVisible()) {
            hideCustomKeyboard();
        } else {
            Constants.CollAmount = mDoubleTotInvAmt + "";
            Intent backToInvoiceScreen = new Intent(this, CollectionCreateActivity.class);
            setResult(passedValues, backToInvoiceScreen);
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_collection_save:
                onSave();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    /*Save Created collection in to offline store*/
    private void onSave() {

        /*    if (!Constants.onGpsCheck(InvoiceSelectionActivity.this)) {
                return;
            }
            if(!UtilConstants.getLocation(InvoiceSelectionActivity.this)){
                return;
            }
*/
        LocationUtils.checkLocationPermission(InvoiceSelectionActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                Log.d("location fun","1");

                if (status) {
                    Constants.getLocation(InvoiceSelectionActivity.this, new LocationInterface() {
                        @Override
                        public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {

                            if(status){
                                Constants.FIPDocumentNumber="";
                                boolean errorFlag = false;
                                mDoubleTotalInvSum =0.0;
                                alInvoiceList.size();
                                arrtable = new ArrayList<HashMap<String, String>>();
                                int incVal = 1;
                                for (int i = 0; i < selectedInvoice.size(); i++) {
                                    if ( selectedInvoice.get(i).isItemSelected() && Double.parseDouble(selectedInvoice.get(i).getInputInvAmount().equalsIgnoreCase("")
                                            ?"0": selectedInvoice.get(i).getInputInvAmount().equalsIgnoreCase(".")?"0":selectedInvoice.get(i).getInputInvAmount() ) > 0) {
                                        if(Double.parseDouble(selectedInvoice.get(i).getInvoiceOutstanding())
                                                >= Double.parseDouble(selectedInvoice.get(i).getInputInvAmount().equalsIgnoreCase("")
                                                ?"0":selectedInvoice.get(i).getInputInvAmount())){

                                            InvoiceBean invoiceItems = selectedInvoice.get(i);

                                            HashMap<String, String> singleItem = new HashMap<>();

                                            singleItem.put(Constants.DocumentNo, doc_no.substring(3, 10));
                                            singleItem.put(Constants.InvoiceNo, invoiceItems.getInvoiceNo());
                                            singleItem.put(Constants.Amount, invoiceItems.getInvoiceAmount());
                                            singleItem.put(Constants.ItemNo, (incVal*10)+"");
                                            singleItem.put(Constants.LOGINID, headerTable.get(Constants.LOGINID));

                                            singleItem.put(Constants.OpenAmount, invoiceItems.getInvoiceOutstanding());
                                            singleItem.put(Constants.InvoicedAmount, invoiceItems.getInvoiceAmount());
                                            singleItem.put(Constants.CollectedAmount, Double.parseDouble(invoiceItems.getInputInvAmount())+"");

                                            singleItem.put(Constants.Currency, headerTable.get(Constants.Currency));
                                            singleItem.put(Constants.InvoiceDate, invoiceItems.getInvoiceDate());
                                            singleItem.put(Constants.InvoiceTypeID, invoiceItems.getInvoiceType());
                                            singleItem.put(Constants.InvoiceTypeDesc, invoiceItems.getInvoiceTypDesc());


                                            mDoubleTotalInvSum = mDoubleTotalInvSum + Double.parseDouble(selectedInvoice.get(i).getInputInvAmount());

                                            arrtable.add(singleItem);
                                            incVal++;
                                        }else{
                                            newInvoiceEdit[i].setBackgroundResource(R.drawable.edittext_border);
                                            errorFlag = true;
                                        }

                                    }
                                }
                                if(errorFlag){
                                    UtilConstants.showAlert(getString(R.string.alert_amt_greater_than_bal_amt), InvoiceSelectionActivity.this);
                                }else if(mStrRefTypeID.equalsIgnoreCase(Constants.str_05)){
                                    onSaveValToDataValult();
                                }else if(mDoubleBundleTotalInvAmt==mDoubleTotalInvSum) {
                                    onSaveValToDataValult();
                                } else{
                                    UtilConstants.showAlert(getString(R.string.alert_enter_valid_coll_amount), InvoiceSelectionActivity.this);
                                }
                            }else{
                                return;
                            }
                        }
                    });
                }else{
                    return;
                }
            }
        });




    }

    private void onSaveValToDataValult(){

        Set<String> set = new HashSet<>();

        Gson gson = new Gson();
        String jsonFromMap = "";
        try {
            jsonFromMap = gson.toJson(arrtable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        headerTable.put(Constants.ItemsText, jsonFromMap);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
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


        headerTable.put(Constants.Amount, mDoubleBundleTotalInvAmt + "");


        JSONObject jsonHeaderObject = new JSONObject(headerTable);

        try {
            ConstantsUtils.storeInDataVault(doc_no, jsonHeaderObject.toString(),this);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        /*sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);

        String loginIdVal = sharedPreferences.getString(Constants.username, "");

        //========>Start VisitActivity
        Constants.onVisitActivityUpdate(mStrBundleCPGUID32, loginIdVal,
                headerTable.get(Constants.FIPGUID).toUpperCase(), Constants.str_02, Constants.Collection);
        //========>End VisitActivity*/

        navigateToVisit();
    }

    public void changeEditTextFocus(int upDownStatus) {

        if (upDownStatus == 1) {
            int ListSize = alInvoiceList.size() - 1;
            if (lastSelectedEditText != ListSize) {
                if (newInvoiceEdit[lastSelectedEditText] != null)
                    newInvoiceEdit[lastSelectedEditText + 1].requestFocus();
            }

        } else {
            if (lastSelectedEditText != 0) {
                if (newInvoiceEdit[lastSelectedEditText - 1] != null)
                    newInvoiceEdit[lastSelectedEditText - 1].requestFocus();
            }

        }

    }


    @Override
    public void onRequestError(int operation, Exception e) {
        Toast.makeText(InvoiceSelectionActivity.this, getString(R.string.err_odata_unexpected, e.getMessage()),
                Toast.LENGTH_LONG).show();

        LogManager.writeLogError(Constants.error_in_collection + e.getMessage());

        pdLoadDialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(
                InvoiceSelectionActivity.this,R.style.MyTheme);
        builder.setMessage(R.string.error_occured_during_post)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();
                                    navigateToRetDetailsActivity();
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
            HashSet<String> setTemp=new HashSet<>();
            if(set!=null && !set.isEmpty()){
                Iterator itr = set.iterator();
                while(itr.hasNext())
                {
                    setTemp.add(itr.next().toString());
                }
            }

            setTemp.remove(doc_no);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(Constants.CollList, setTemp);
            editor.commit();

            String store=null;
            try {
                ConstantsUtils.storeInDataVault(doc_no, "",this);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            if(!UtilConstants.isNetworkAvailable(InvoiceSelectionActivity.this)){
                pdLoadDialog.dismiss();
                onNoNetwork();
            }else{
                OfflineManager.flushQueuedRequests(InvoiceSelectionActivity.this);
            }

        }else if (operation == Operation.OfflineFlush.getValue()) {
            OfflineManager.refreshRequests(getApplicationContext(), Constants.VisitActivities + "," + Constants.FinancialPostings
                    +","+Constants.FinancialPostingItemDetails+","+Constants.SSINVOICES+","+Constants.SSInvoiceItemDetails+","+Constants.RetailerSummarySet, InvoiceSelectionActivity.this);
        }else if(operation == Operation.OfflineRefresh.getValue()){

            pdLoadDialog.dismiss();


            popUpText =  Constants.getCollectionSuccessMsg(Constants.FIPDocumentNumber);

            AlertDialog.Builder builder = new AlertDialog.Builder(
                    InvoiceSelectionActivity.this,R.style.MyTheme);
            builder.setMessage(popUpText)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface Dialog,
                                        int id) {
                                    try {
                                        Dialog.cancel();
                                        navigateToRetDetailsActivity();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
            builder.show();
        }
    }

    private void navigateToRetDetailsActivity(){
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(InvoiceSelectionActivity.this,CustomerDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
//        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        if(!Constants.OtherRouteNameVal.equalsIgnoreCase("")){
            intentNavPrevScreen.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
            intentNavPrevScreen.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
        }
        startActivity(intentNavPrevScreen);
    }

    private void onNoNetwork() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                InvoiceSelectionActivity.this,R.style.MyTheme);
        builder.setMessage(
                getString(R.string.err_no_network))
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        navigateToRetDetailsActivity();
                    }
                });

        builder.show();
    }

    /*Navigate to visit screen*/
    public void navigateToVisit(){
        pdLoadDialog.dismiss();
        popUpText = getString(R.string.msg_Collection_created);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                InvoiceSelectionActivity.this,R.style.MyTheme);
        builder.setMessage(popUpText)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();
                                    Constants.ComingFromCreateSenarios = Constants.X;
                                    navigateToRetDetailsActivity();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
        builder.show();
    }

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

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
//        return super.onKeyLongPress(keyCode, event);
        if( keyCode == KeyEvent.KEYCODE_DEL ) {
            //Handle what you want in long press.
            super.onKeyLongPress(keyCode, event);
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public void onRelease(int primaryCode) {

    }

    public void onKey(int primaryCode, int[] keyCodes) {
        switch (primaryCode) {

            case 81:
                //Plus
                Constants.incrementTextValues(newInvoiceEdit[lastSelectedEditText], Constants.Y);
                break;
            case 69:
                //Minus
                Constants.decrementEditTextVal(newInvoiceEdit[lastSelectedEditText], Constants.Y);
                break;
            case 1:
                changeEditTextFocus(0);
                break;
            case 2:
                changeEditTextFocus(1);
                break;
            case 56:
                if (!checkAlreadyDotIsThere()) {
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


    public void hideCustomKeyboard() {
        keyboardView.setVisibility(View.GONE);
        keyboardView.setEnabled(false);
    }

    public void showCustomKeyboard(View v) {

        keyboardView.setVisibility(View.VISIBLE);
        keyboardView.setEnabled(true);
        if (v != null) {
            ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }


    private Boolean checkAlreadyDotIsThere() {
        ArrayList<EditText> myEditTextList = new ArrayList<EditText>();

        for (int i = 0; i < sv_invoice_list.getChildCount(); i++)
            if (sv_invoice_list.getChildAt(i) instanceof EditText) {
                myEditTextList.add((EditText) sv_invoice_list.getChildAt(i));
                if (myEditTextList.get(i).hasFocus()) {
                    String textValue = myEditTextList.get(i).getText().toString();
                    if (textValue.contains(".")) {
                        return true;
                    } else
                        return false;
                }
            }
        return false;
    }

    private void clearEditTextVal(EditText editText){
        editText.setText("");
    }


    /*AsyncTask to get Invoices*/
    private class GetInvoices extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(InvoiceSelectionActivity.this,R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
                selectedInvoice = new ArrayList<>();
                try {
                    if (Constants.alTempInvoiceList.size() > 0) {
                        alInvoiceList = Constants.alTempInvoiceList;
                    } else {
                        alInvoiceList = OfflineManager.getInvoicesByCustomerNo(Constants.OutstandingInvoices + "?$orderby=" + Constants.InvoiceNo + " asc&$filter=CustomerNo eq '" + mStrBundleRetID + "' "
                                , mStrBundleCPGUID, mStrBundleRetID, getApplicationContext());
                    }

                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pdLoadDialog.dismiss();
            displayInvoiceValues();
        }
    }
}
