package com.rspl.sf.msfa.competitorInfo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;


public class CompetitorInformation extends AppCompatActivity implements UIListener {

    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "",mStrBundleCPGUID32="";
    private String mStrBundleRetailerUID = "",mStrComingFrom="";
    private EditText etProductName,etMRP,etRetailerMargin,etSchemeDetails,etLandingprice,etWholesaleLandingRate,etConsumerOffer,etTradeOffer,etShelfLife,etOtherInformation;
    private Spinner spSchemeLaunched,spCompetitorName;
    private String[] mArraySchemeLaunchValues;
    private LinearLayout ltSchemeRemarks;
    private ProgressDialog pdLoadDialog;
    private Boolean saveClicked = false;
    private Boolean isMrpValid = false;

    private String[][] mArrayCompNames;
    ArrayList<Hashtable<String,String>>  arrtable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_competitor_information);
      //  ActionBarView.initActionBarView(this,true,getString(R.string.title_competitor_information));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_competitor_information), 0);
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null)
        {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32= bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
        }
        initUI();
        getCompetitorName();
    }

    private void getCompetitorName()
    {
        try {
            mArrayCompNames = OfflineManager.getCompetitorNames(Constants.CompetitorMasters);
            if(mArrayCompNames==null)
            {
                mArrayCompNames = new String[2][1];
                mArrayCompNames[0][0] = Constants.None;
                mArrayCompNames[1][0] = Constants.None;
            }
            else
            {
                ArrayAdapter<String> competitorAdapter = new ArrayAdapter<String>(this,
                        R.layout.custom_textview, mArrayCompNames[1]);
                competitorAdapter.setDropDownViewResource(R.layout.spinnerinside);
                spCompetitorName.setAdapter(competitorAdapter);
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_competitor_info_create, menu);


        return true;
    }
    private void initUI()
    {
        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);
        TextView etMrpCur = (TextView)findViewById(R.id.tv_mrp_currency);
        TextView etLandPriceCur = (TextView)findViewById(R.id.tv_landing_price_currency);
        TextView etWholeSalePriceCur = (TextView)findViewById(R.id.tv_wholesale_landing_rate_currency);
        etMrpCur.setText(Constants.getCurrency());
        etLandPriceCur.setText(Constants.getCurrency());
        etWholeSalePriceCur.setText(Constants.getCurrency());

        ltSchemeRemarks = (LinearLayout)findViewById(R.id.ll_scheme_remarks);
        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetID);
        spCompetitorName = (Spinner)findViewById(R.id.sp_competitor_name);
        etProductName = (EditText)findViewById(R.id.edit_product_name);
        etMRP = (EditText)findViewById(R.id.edit_mrp);
        etRetailerMargin = (EditText)findViewById(R.id.edit_retailer_margin);
        etSchemeDetails = (EditText)findViewById(R.id.edit_scheme_details);
        etLandingprice = (EditText)findViewById(R.id.edit_retailer_landing_rate);
        etWholesaleLandingRate = (EditText)findViewById(R.id.edit_wholesaler_landing_rate);
        etConsumerOffer = (EditText)findViewById(R.id.edit_consumer_offer);
        etTradeOffer = (EditText)findViewById(R.id.edit_trade_offer);
        etShelfLife = (EditText)findViewById(R.id.edit_shelf_life);
        etOtherInformation = (EditText)findViewById(R.id.edit_other_information);



        mrpStartStopEditing(etMRP,13,2);
        Constants.editTextDecimalFormat(etLandingprice,13,2);
        Constants.editTextDecimalFormat(etWholesaleLandingRate,13,2);
        Constants.editTextDecimalFormat(etRetailerMargin,13,2);
        resetEditTextBackground(etProductName);
        resetEditTextBackground(etMRP);
        resetEditTextBackground(etRetailerMargin);
        resetEditTextBackground(etSchemeDetails);


        spSchemeLaunched = (Spinner)findViewById(R.id.sp_scheme_launched);
        mArraySchemeLaunchValues = new String[3];
        mArraySchemeLaunchValues[0] = Constants.None;
        mArraySchemeLaunchValues[1] = getString(R.string.yes);
        mArraySchemeLaunchValues[2] = getString(R.string.no);
        ArrayAdapter<String> schemeLaunchAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_textview, mArraySchemeLaunchValues);
        schemeLaunchAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spSchemeLaunched.setAdapter(schemeLaunchAdapter);
        spSchemeLaunched.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==1)
                {
                    ltSchemeRemarks.setVisibility(View.VISIBLE);
                }
                else
                {
                    ltSchemeRemarks.setVisibility(View.GONE);
                }
                if(position!=0)
                    spSchemeLaunched.setBackgroundResource(R.drawable.spinner_bg);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spCompetitorName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!mArrayCompNames[1][position].equalsIgnoreCase(Constants.None))
                {
                  spCompetitorName.setBackgroundResource(R.drawable.spinner_bg);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }
    private  void resetEditTextBackground(final EditText selEditText)
    {
        selEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                selEditText.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

    private void onSave()
    {
        if(isValidationSuccess())
        {
            String mrpTemp  = etMRP.getText().toString();
            float marginPercentage  = Float.parseFloat(etRetailerMargin.getText().toString());
           if(mrpTemp.equals("0.0")||mrpTemp.equals("0.00") ||mrpTemp.equals("0") ||mrpTemp.equals("0.") )
           {
               Constants.customAlertMessage(this,getString(R.string.validation_plz_enter_valid_mrp));
               etMRP.setBackgroundResource(R.drawable.edittext_border);
           }
           else if(marginPercentage>100)
           {
               Constants.customAlertMessage(this,getString(R.string.validation_plz_enter_valid_margin));
               etRetailerMargin.setBackgroundResource(R.drawable.edittext_border);
           }
            else
           {
               createCompetitorInformation();
           }

        }
        else
        {

            Constants.customAlertMessage(this,getString(R.string.validation_plz_enter_mandatory_flds));
        }
    }

    private void createCompetitorInformation()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        String loginIdVal = sharedPreferences.getString(Constants.username, "");
        Hashtable visitActivityTable = new Hashtable();
        String mStrSPGUID = Constants.getSPGUID(Constants.SPGUID);
        String getVisitGuidQry = Constants.Visits + "?$filter=EndDate eq null and CPGUID eq '" + mStrBundleCPGUID32.toUpperCase() + "' " +
                "and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and "+Constants.SPGUID+" eq guid'"+mStrSPGUID+"'";
        ODataGuid mGuidVisitId = null;
        try {
            mGuidVisitId = OfflineManager.getVisitDetails(getVisitGuidQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        GUID mStrGuide = GUID.newRandom();

        GUID guid = GUID.newRandom();

        visitActivityTable.put(Constants.VisitActivityGUID, mStrGuide.toString());
        visitActivityTable.put(Constants.LOGINID, loginIdVal);
        visitActivityTable.put(Constants.VisitGUID, mGuidVisitId.guidAsString36());
        visitActivityTable.put(Constants.ActivityType, "04");
        visitActivityTable.put(Constants.ActivityTypeDesc, Constants.CompetitorInfos);
        visitActivityTable.put(Constants.ActivityRefID, guid.toString());

        try {
            OfflineManager.createVisitActivity(visitActivityTable);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        Hashtable<String, String> singleItem = new Hashtable<String, String>();
        GUID guidItem = GUID.newRandom();
        singleItem.put(Constants.CompInfoGUID, guidItem.toString());
        singleItem.put(Constants.CPTypeID, Constants.getName(Constants.ChannelPartners, Constants.CPTypeID, Constants.CPUID,mStrBundleRetID));
        singleItem.put(Constants.CPGUID, mStrBundleCPGUID32);
        singleItem.put(Constants.SPGUID, Constants.getSPGUID(Constants.SPGUID));
        singleItem.put(Constants.CompGUID,mArrayCompNames[0][spCompetitorName.getSelectedItemPosition()]);
        singleItem.put(Constants.CompName,mArrayCompNames[1][spCompetitorName.getSelectedItemPosition()]);

        singleItem.put(Constants.MatGrp1Amount,"");
        singleItem.put(Constants.MatGrp2Amount,"");
        singleItem.put(Constants.MatGrp3Amount,"");

        singleItem.put(Constants.MatGrp4Amount,"");
        singleItem.put(Constants.Earnings, "1");
        singleItem.put(Constants.SchemeName,"Test");
        singleItem.put(Constants.UpdatedOn, UtilConstants.getNewDateTimeFormat());

        if(etMRP.getText().toString().equalsIgnoreCase(""))
            singleItem.put(Constants.MRP,"0.0");
        else
            singleItem.put(Constants.MRP,etMRP.getText().toString());
        singleItem.put(Constants.MaterialDesc,etProductName.getText().toString());
        if(etRetailerMargin.getText().toString().equalsIgnoreCase(""))
            singleItem.put(Constants.Margin,"0.0");
        else
            singleItem.put(Constants.Margin,etRetailerMargin.getText().toString());
        if(etLandingprice.getText().toString().equalsIgnoreCase(""))
            singleItem.put(Constants.LandingPrice,"0.0");
        else
            singleItem.put(Constants.LandingPrice,etLandingprice.getText().toString());
        if(etWholesaleLandingRate.getText().toString().equalsIgnoreCase(""))
            singleItem.put(Constants.WholeSalesLandingPrice,"0.0");
        else
             singleItem.put(Constants.WholeSalesLandingPrice,etWholesaleLandingRate.getText().toString());
        singleItem.put(Constants.ConsumerOffer,etConsumerOffer.getText().toString().trim());
        singleItem.put(Constants.TradeOffer,etTradeOffer.getText().toString().trim());
        if(etShelfLife.getText().toString().equals(""))
            singleItem.put(Constants.ShelfLife,"0");
        else
        singleItem.put(Constants.ShelfLife,etShelfLife.getText().toString());
        singleItem.put(Constants.Remarks,etOtherInformation.getText().toString().trim());

        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        month++;
        singleItem.put(Constants.Period,String.valueOf(month));

        singleItem.put(Constants.LOGINID,loginIdVal);
        try {
            //noinspection unchecked
            OfflineManager.createCompetitorInfo(singleItem, CompetitorInformation.this);

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.Error+" : " + e.getMessage());
        }
    }

    private boolean isValidationSuccess()
    {
        int validCount = 0;
        saveClicked = true;
        if(etProductName.getText().toString().trim().equals(""))
        {
            etProductName.setBackgroundResource(R.drawable.edittext_border);
            validCount++;
        }
        if(etMRP.getText().toString().equals(""))
        {

            etMRP.setBackgroundResource(R.drawable.edittext_border);
            validCount++;
        }


        if(etRetailerMargin.getText().toString().equals(""))
        {
            etRetailerMargin.setBackgroundResource(R.drawable.edittext_border);
            validCount++;
        }
        else if(etRetailerMargin.getText().toString().equals("."))
        {
            etRetailerMargin.setBackgroundResource(R.drawable.edittext_border);
            validCount++;
        }

         if(etLandingprice.getText().toString().equals("."))
        {
            etLandingprice.setBackgroundResource(R.drawable.edittext_border);
            validCount++;
        }
         if(etWholesaleLandingRate.getText().toString().equals("."))
        {
            etWholesaleLandingRate.setBackgroundResource(R.drawable.edittext_border);
            validCount++;
        }




        if(mArrayCompNames[1][spCompetitorName.getSelectedItemPosition()].equalsIgnoreCase(Constants.None))
        {
            validCount++;
            spCompetitorName.setBackgroundResource(R.drawable.error_spinner);
        }
        if(mArraySchemeLaunchValues[spSchemeLaunched.getSelectedItemPosition()].equalsIgnoreCase(getString(R.string.yes)))
        {
            String schemeRemarks = etSchemeDetails.getText().toString().trim();
            if(schemeRemarks.equalsIgnoreCase(""))
            {
               validCount++;
                etSchemeDetails.setBackgroundResource(R.drawable.edittext_border);
            }
        }
        else if(mArraySchemeLaunchValues[spSchemeLaunched.getSelectedItemPosition()].equalsIgnoreCase(Constants.None))
        {
            validCount++;
           spSchemeLaunched.setBackgroundResource(R.drawable.error_spinner);
        }
        if(validCount>0)
            return false;
        else
            return true;
    }

    private String[] splitString(String value)
    {

        String[] parts = value.split("\\."); // escape .
        return parts;
    }

    @Override
    public void onRequestError(int i, Exception e)
    {

        Constants.customAlertMessage(this,e.getMessage());
        LogManager.writeLogError(Constants.error_in_collection+ e.getMessage());
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(CompetitorInformation.this, R.style.MyTheme);
        builder.setMessage(getString(R.string.comp_info_created_success,mStrBundleRetName)).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        clearDatas();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        CompetitorInformation.this.finish();
                    }

                });
        builder.show();




    }

    private void clearDatas()
    {
        etProductName.setText("");
        etSchemeDetails.setText("");
        spSchemeLaunched.setSelection(0);
        etMRP.setText("");
        etRetailerMargin.setText("");
        etProductName.setBackgroundResource(R.drawable.edittext);
        etSchemeDetails.setBackgroundResource(R.drawable.edittext);
        etMRP.setBackgroundResource(R.drawable.edittext);
        etRetailerMargin.setBackgroundResource(R.drawable.edittext);
        spCompetitorName.setBackgroundResource(R.drawable.spinner_bg);
        etOtherInformation.setText("");
        etWholesaleLandingRate.setText("");
        etLandingprice.setText("");
        etConsumerOffer.setText("");
        etTradeOffer.setText("");
        etShelfLife.setText("");


    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(CompetitorInformation.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_competition_information).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        CompetitorInformation.this.finish();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();


    }
    public  void mrpStartStopEditing(final EditText editText,final int beforeDecimal,final int afterDecimal) {

        editText.setFilters(new InputFilter[] { new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                StringBuilder sbText = new StringBuilder(source);
                String text = sbText.toString();

                if (dstart == 0) {
                    if (text.contains("0")) {
                        return "0";
                    }
                    else if(text.contains("0.0")){
                        return "0.";
                    }
                    else if(text.contains(".")){
                        return "0.";
                    }

                    else if(text.contains("0..")){
                        return "0.";
                    }
                    else{
                        return source;
                    }
                }

                String etText = editText.getText().toString();
                if(!etText.equals("0.0")|| !etText.equals("0.00") || !etText.equals("0") || !etText.equals("0."))
                    editText.setBackgroundResource(R.drawable.edittext);

                if (etText.isEmpty()) {
                    return null;
                }
                String temp = editText.getText() + source.toString();

                if (temp.equals(".")) {
                    return "0.";
                }
                 if(text.contains("0.0")){
                    return "0.";
                }
                if(temp.contains("0.."))
                {
                    return "";
                } else if (temp.toString().indexOf(".") == -1) {
                    // no decimal point placed yet
                    if (temp.length() > beforeDecimal) {
                        return "";
                    }
                } else {
                    int dotPosition;
                    int cursorPositon = editText.getSelectionStart();
                    if (etText.indexOf(".") == -1) {
                        Log.i("First time Dot", etText.toString().indexOf(".") + " " + etText);
                        dotPosition = temp.indexOf(".");
                    } else {
                        dotPosition = etText.indexOf(".");
                    }
                    if (cursorPositon <= dotPosition) {
                        String beforeDot = etText.substring(0, dotPosition);
                        if (beforeDot.length() < beforeDecimal) {
                            return source;
                        } else {
                            if (source.toString().equalsIgnoreCase(".")) {
                                return source;
                            } else {
                                return "";
                            }

                        }
                    } else {
                        temp = temp.substring(temp.indexOf(".") + 1);
                        if (temp.length() > afterDecimal) {
                            return "";
                        }else if(etText.contains(source) && source.equals(".")){
                            return "";
                        }
                    }
                }
                return null;


            }
        } });


    }

}
