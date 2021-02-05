package com.rspl.sf.msfa.visit;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
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
import com.arteriatech.mutils.common.Operation;
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
import com.sap.smp.client.odata.impl.ODataDurationDefaultImpl;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by e10526 on 05-05-2016.
 *
 */
@SuppressLint("NewApi")
public class FeedBackCreateActivity extends AppCompatActivity implements UIListener {
    private EditText editRemraks, edit_bts_id, edit_location;
    private String[][] arrFeedBackType = null,arrFeedBackSubType=null;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    private String mStrSelFeedBackType = "", mStrSelFeedBackTypeDesc = "",mStrParentID="",
            mStrSelFeedBackSubType = "", mStrSelFeedBackSubTypeDesc = "",
            mStrRemarks = "", mStrBtsId = "", mStrLocation = "", popUpText = "";
    ArrayList<HashMap<String, String>> arrtable;
    Hashtable tableHdr;
    private String doc_no = "";
    private String[][] mArrayDistributors;
//    String selDistributorCode = "", mStrCPTypeID = "", mStrSPGuid = "", mStrSPNO = "";

    private String mStrBundleRetailerUID = "", mStrBundleCPGUID32 = "";
    String mStrComingFrom = "";
    LinearLayout ll_payment_related;
    TextView tv_feed_back_rel;
    Spinner sp_payment_related;
    Spinner spinnerFeedBackType;
    private ODataDuration mStartTimeDuration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
       // ActionBarView.initActionBarView(this, true,getString(R.string.lbl_feed_back_create));

        setContentView(R.layout.activity_feed_back);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_feed_back_create), 0);

        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initUI();
    }

    /*Initializes UI*/
    void initUI() {
        mStartTimeDuration=UtilConstants.getOdataDuration();
        TextView tvRetName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView tvUID = (TextView) findViewById(R.id.tv_reatiler_id);
        tv_feed_back_rel = (TextView) findViewById(R.id.tv_feed_back_rel);
        ll_payment_related = (LinearLayout)findViewById(R.id.ll_payment_related);
        sp_payment_related = (Spinner)findViewById(R.id.sp_payment_related);

        tvRetName.setText(mStrBundleRetName);
        tvUID.setText(mStrBundleRetID);


        getFeedBackType();
        //TO DO
       getDistributorValues();

        if (arrFeedBackType == null) {
            arrFeedBackType = new String[1][1];
            arrFeedBackType[0][0] = "";


        }
        spinnerFeedBackType = (Spinner) findViewById(R.id.sp_feed_back_type);

        ArrayAdapter<String> arrayAdepterFeedBackTypeValues = new ArrayAdapter<>(this, R.layout.custom_textview, arrFeedBackType[1]);
        arrayAdepterFeedBackTypeValues.setDropDownViewResource(R.layout.spinnerinside);
        spinnerFeedBackType.setAdapter(arrayAdepterFeedBackTypeValues);
        spinnerFeedBackType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                mStrSelFeedBackType = arrFeedBackType[0][position];
                mStrSelFeedBackTypeDesc = arrFeedBackType[1][position];
                mStrParentID  = arrFeedBackType[0][position];
                spinnerFeedBackType.setBackgroundResource(R.drawable.spinner_bg);

                if(mStrSelFeedBackType.equalsIgnoreCase("05")){
                    ll_payment_related.setVisibility(View.VISIBLE);
                    getFeedBackSubType(mStrSelFeedBackType);
                    paymentRelatedDropDown();
                }else{
                    ll_payment_related.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        editRemraks = (EditText) findViewById(R.id.edit_remarks);

        editRemraks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editRemraks.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edit_bts_id = (EditText) findViewById(R.id.edit_bts_id);

        edit_location = (EditText) findViewById(R.id.edit_location);
    }

    private void paymentRelatedDropDown(){
        if (arrFeedBackSubType == null) {
            arrFeedBackSubType = new String[1][1];
            arrFeedBackSubType[0][0] = "";
        }

        ArrayAdapter<String> arrAdpFeedBackSubTypeVal = new ArrayAdapter<>(this, R.layout.custom_textview, arrFeedBackSubType[1]);
        arrAdpFeedBackSubTypeVal.setDropDownViewResource(R.layout.spinnerinside);
        sp_payment_related.setAdapter(arrAdpFeedBackSubTypeVal);
        sp_payment_related.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                sp_payment_related.setBackgroundResource(R.drawable.spinner_bg);
                mStrSelFeedBackSubType = arrFeedBackSubType[0][position];
                mStrSelFeedBackSubTypeDesc = arrFeedBackSubType[1][position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    /*Gets feedback Types from value helps*/
    private void getFeedBackType() {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.FeedbackType + "'";
            arrFeedBackType = OfflineManager.getConfigListWithDefaultValAndNone(mStrConfigQry,"");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }
    private void getFeedBackSubType(String mStrParentId) {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '"
                    + Constants.FeedbackSubType + "' and "+Constants.ParentID+" eq '"+mStrParentId+"' ";
            arrFeedBackSubType = OfflineManager.getConfigListWithDefaultValAndNone(mStrConfigQry,"");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt  + e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_feed_back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_feedback_save:
                onSave();

                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FeedBackCreateActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_create_feed_back).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        navigateToRetDetailsActivity();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();
    }

    /*Validating Data*/
    private boolean validateData() {

        boolean hasError = false;


        if (mStrSelFeedBackType.equalsIgnoreCase("")) {
            // Full day attendance type = None
            if (mStrSelFeedBackType.equalsIgnoreCase("")) {
                // error
                spinnerFeedBackType.setBackgroundResource(R.drawable.error_spinner);
                hasError = true;
            }

            if (editRemraks.getText() == null || editRemraks.getText().toString().trim().equalsIgnoreCase("")) {
                // error
                editRemraks.setBackgroundResource(R.drawable.edittext_border);
                hasError = true;
            }

        } else  if (mStrSelFeedBackType.equalsIgnoreCase("05")) {

            if (mStrSelFeedBackSubType.equalsIgnoreCase("")) {
                // error
                hasError = true;
                sp_payment_related.setBackgroundResource(R.drawable.error_spinner);
            }

                if (editRemraks.getText() == null || editRemraks.getText().toString().trim().equalsIgnoreCase("")) {
                    // error
                    hasError = true;
                    editRemraks.setBackgroundResource(R.drawable.edittext_border);
                }
        }else{
            if (editRemraks.getText() == null || editRemraks.getText().toString().trim().equalsIgnoreCase("")) {
                // error
                hasError = true;
                editRemraks.setBackgroundResource(R.drawable.edittext_border);
            }
        }


        return hasError;
    }

    /*Saves feedback into data vault*/
    private void onSave() {

        if (validateData()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
            builder.setMessage(R.string.validation_plz_enter_mandatory_flds)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            builder.show();
        } else {
        /*    if (!Constants.onGpsCheck(FeedBackCreateActivity.this)) {
                return;
            }*/


            LocationUtils.checkLocationPermission(FeedBackCreateActivity.this, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                    Log.d("location fun","1");

                    if (status) {

                    }else
                        {
                        return;
                    }
                }
            });
            {

                Constants.getLocation(FeedBackCreateActivity.this, new LocationInterface() {
                    @Override
                    public void location(boolean status, LocationModel locationModel, String s, int i) {

                        if(status){

                            UtilConstants.getLocation(FeedBackCreateActivity.this);
                            Set<String> set = new HashSet<>();
                            doc_no = (System.currentTimeMillis() + "");
                            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);

                            String loginIdVal = sharedPreferences.getString(Constants.username, "");


                            GUID mStrGuide = GUID.newRandom();
                            Constants.onVisitActivityUpdate(FeedBackCreateActivity.this,mStrBundleCPGUID32,
                                    mStrGuide.toString36().toUpperCase(),Constants.FeedbackID, Constants.Feedback, mStartTimeDuration);

                            String tempInvNo = (System.currentTimeMillis() + "");
                            mStrRemarks = editRemraks.getText().toString();
                            mStrBtsId = edit_bts_id.getText().toString().trim().equalsIgnoreCase("") ? "" : edit_bts_id.getText().toString();
                            mStrLocation = edit_location.getText().toString().trim().equalsIgnoreCase("") ? "" : edit_location.getText().toString();

                            tableHdr = new Hashtable();

                            //noinspection unchecked
                            tableHdr.put(Constants.FeebackGUID, mStrGuide.toString());
                            //noinspection unchecked
                            tableHdr.put(Constants.Remarks, mStrRemarks);
                            //noinspection unchecked
                            tableHdr.put(Constants.CPNo, UtilConstants.removeLeadingZeros(mStrBundleRetID));
                            //noinspection unchecked
                            tableHdr.put(Constants.CPGUID, mStrBundleCPGUID32.toUpperCase());

                            //noinspection unchecked
                            tableHdr.put(Constants.FeedbackType, mStrSelFeedBackType);

                            tableHdr.put(Constants.FeedbackTypeDesc, mStrSelFeedBackTypeDesc);

                            if(mStrSelFeedBackType.equalsIgnoreCase("05")) {
                                //noinspection unchecked
                                tableHdr.put(Constants.FeedbackSubTypeID, mStrSelFeedBackSubType);

                                tableHdr.put(Constants.FeedbackSubTypeDesc, mStrSelFeedBackSubTypeDesc);
                            }else{
                                tableHdr.put(Constants.FeedbackSubTypeID, "");

                                tableHdr.put(Constants.FeedbackSubTypeDesc, "");
                            }
                            //noinspection unchecked
                            tableHdr.put(Constants.Location1, mStrLocation);

                            tableHdr.put(Constants.BTSID, mStrBtsId);

                            tableHdr.put(Constants.FeedbackNo, tempInvNo);
                            tableHdr.put(Constants.CPTypeID,mArrayDistributors[8][0]);
                            tableHdr.put(Constants.SPGUID, mArrayDistributors[0][0].toUpperCase());
                            tableHdr.put(Constants.SPNo, mArrayDistributors[2][0]);
                            tableHdr.put(Constants.ParentID, mArrayDistributors[4][0]);
                            tableHdr.put(Constants.ParentName, mArrayDistributors[7][0]);
                            tableHdr.put(Constants.ParentTypeID, mArrayDistributors[5][0]);
                            tableHdr.put(Constants.ParentTypDesc, mArrayDistributors[6][0]);


                            //noinspection unchecked
                            tableHdr.put(Constants.LOGINID, loginIdVal);

                            tableHdr.put(Constants.CreatedOn, UtilConstants.getNewDateTimeFormat());
                            final Calendar calCurrentTime = Calendar.getInstance();
                            int hourOfDay = calCurrentTime.get(Calendar.HOUR_OF_DAY); // 24 hour clock
                            int minute = calCurrentTime.get(Calendar.MINUTE);
                            int second = calCurrentTime.get(Calendar.SECOND);
                            ODataDuration oDataDuration = null;
                            try {
                                oDataDuration = new ODataDurationDefaultImpl();
                                oDataDuration.setHours(hourOfDay);
                                oDataDuration.setMinutes(minute);
                                oDataDuration.setSeconds(BigDecimal.valueOf(second));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            tableHdr.put(Constants.CreatedAt, oDataDuration.toString());

                            tableHdr.put(Constants.entityType, Constants.Feedback);

                            arrtable = new ArrayList<HashMap<String, String>>();

                            HashMap tableItm = new HashMap();

                            try {
                                //noinspection unchecked
                                tableItm.put(Constants.FeebackGUID, mStrGuide.toString());
                                mStrGuide = GUID.newRandom();
                                //noinspection unchecked
                                tableItm.put(Constants.FeebackItemGUID, mStrGuide.toString());
                                //noinspection unchecked
                                tableItm.put(Constants.FeedbackType, mStrSelFeedBackType);
                                //noinspection unchecked
                                tableItm.put(Constants.Remarks, mStrRemarks);

                                if(mStrSelFeedBackType.equalsIgnoreCase("05")) {
                                    //noinspection unchecked
                                    tableItm.put(Constants.FeedbackSubTypeID, mStrSelFeedBackSubType);

                                    tableItm.put(Constants.FeedbackSubTypeDesc, mStrSelFeedBackSubTypeDesc);
                                }else{
                                    tableItm.put(Constants.FeedbackSubTypeID, "");

                                    tableItm.put(Constants.FeedbackSubTypeDesc, "");
                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                            arrtable.add(tableItm);

                            tableHdr.put(Constants.ITEM_TXT, UtilConstants.convertArrListToGsonString(arrtable));


                            Constants.saveDeviceDocNoToSharedPref(FeedBackCreateActivity.this, Constants.FeedbackList,doc_no);

                            tableHdr.put(Constants.LOGINID, sharedPreferences.getString(Constants.username, "").toUpperCase());

                            JSONObject jsonHeaderObject = new JSONObject(tableHdr);

                            ConstantsUtils.storeInDataVault(doc_no,jsonHeaderObject.toString(),FeedBackCreateActivity.this);

                            backToVisit();

                        }

                    }
                });


            }

        }


    }

    private void backToVisit() {


        popUpText = getString(R.string.lbl_feed_back_created);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                FeedBackCreateActivity.this, R.style.MyTheme);
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

    @Override
    public void onRequestError(int operation, Exception e) {
        LogManager.writeLogError("Error in FeedBack : " + e.getMessage());


        AlertDialog.Builder builder = new AlertDialog.Builder(
                FeedBackCreateActivity.this, R.style.MyTheme);
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
            set = sharedPreferences.getStringSet(Constants.InvList, null);
            HashSet<String> setTemp = new HashSet<>();
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
                while (itr.hasNext()) {
                    setTemp.add(itr.next().toString());
                }
            }

            setTemp.remove(doc_no);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(Constants.InvList, setTemp);
            editor.commit();

            String store = null;
            try {
                ConstantsUtils.storeInDataVault(doc_no, "",this);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            if (!UtilConstants.isNetworkAvailable(FeedBackCreateActivity.this)) {
                onNoNetwork();
            } else {
                OfflineManager.flushQueuedRequests(FeedBackCreateActivity.this);
            }

        } else if (operation == Operation.OfflineFlush.getValue()) {
            OfflineManager.refreshRequests(getApplicationContext(), Constants.VisitActivities, FeedBackCreateActivity.this);
        } else if (operation == Operation.OfflineRefresh.getValue()) {

            popUpText = getString(R.string.Feedback_created);

            AlertDialog.Builder builder = new AlertDialog.Builder(
                    FeedBackCreateActivity.this, R.style.MyTheme);
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

    private void onNoNetwork() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                FeedBackCreateActivity.this, R.style.MyTheme);
        builder.setMessage(
                getString(R.string.alert_sync_cannot_be_performed))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        navigateToRetDetailsActivity();
                    }
                });

        builder.show();
    }
    /**
     * get distributor value
     */
    private void getDistributorValues() {
        mArrayDistributors = Constants.getDistributorsByCPGUID(mStrBundleCPGUID32);
    }
    private void navigateToRetDetailsActivity(){
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(FeedBackCreateActivity.this,CustomerDetailsActivity.class);
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
}
