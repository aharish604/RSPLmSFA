package com.rspl.sf.msfa.attendance;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.rspl.sf.msfa.BuildConfig;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.impl.ODataDurationDefaultImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

public class DayEndRemarksActivity extends AppCompatActivity implements UIListener {
    private EditText etRemarks;
    private Spinner spDealers;
    private String selDealer, selDelName = "", selDelGuid = "", selRoutePlanKey = "",
            closingDayType = "", closingDate = "";
    private String[][] delList = null;
    private String mStrPopUpText = "";
    private String[] retailerRemarks;
    private ArrayList<String> checkedRetailers;
    private int currentRetailerId = 0;
    private Boolean nextRetailer = false;
    private Menu menu;
    private ProgressDialog pdLoadDialog;
    private String mStrSPGUID = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_day_end_remarks);
        //Initialize action bar without back button(false)
//		ActionBarView.initActionBarView(this, true,getString(R.string.lbl_not_visited_retailer));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_not_visited_retailer), 0);
        Bundle bundle = getIntent().getExtras();
        closingDayType = bundle.getString(Constants.ClosingeDayType);
        closingDate = bundle.getString(Constants.ClosingeDay);
        mStrSPGUID = Constants.getSPGUID(Constants.SPGUID);
        //Initialize UI
        initUI();

    }

    /*Initializes UI for screen*/
    void initUI() {
        checkedRetailers = new ArrayList<>();
        Constants.MAX_LENGTH = 100;
        etRemarks = (EditText) findViewById(R.id.etRemarks);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(Constants.MAX_LENGTH);
        etRemarks.setFilters(FilterArray);


        etRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etRemarks.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        spDealers = (Spinner) findViewById(R.id.spDealers);
        getDealer();
        setDealers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_collection_create, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_collection_save);
        menuItem.setVisible(false);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_collection_next:
                if (ConstantsUtils.isAutomaticTimeZone(DayEndRemarksActivity.this)) {
                    onNext();
                } else {
                    ConstantsUtils.showAutoDateSetDialog(DayEndRemarksActivity.this);
                }
                break;
            case R.id.menu_collection_save:
                if (ConstantsUtils.isAutomaticTimeZone(DayEndRemarksActivity.this)) {
                    saveAllRetailers();
                } else {
                    ConstantsUtils.showAutoDateSetDialog(DayEndRemarksActivity.this);
                }
                break;

            case android.R.id.home:
                onBackPressed();
                break;

        }
        return true;
    }

    private void setDealers() {


        if (delList == null) {
            delList = new String[1][6];
            delList[0][0] = "";
            delList[0][1] = "";
            delList[0][2] = "";
            delList[0][3] = "";
            delList[0][4] = "";
            delList[0][5] = "";
        }

        //displaying retailer name and number in spinner
        String[] dealerList = new String[delList.length];
        for (int i = 0; i < delList.length; i++) {

            dealerList[i] = delList[i][2];
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                this, R.layout.custom_textview, dealerList);
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDealers.setAdapter(spinnerAdapter);
        spDealers.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                currentRetailerId = position;
                selDealer = delList[position][0];
                selDelName = delList[position][2];
                selDelGuid = delList[position][3];
                selRoutePlanKey = delList[position][4];
                etRemarks.setText(delList[position][5]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (nextRetailer) {
            if (currentRetailerId <= delList.length) {
                spDealers.setSelection(currentRetailerId + 1);
                etRemarks.setText(delList[currentRetailerId][5]);
            }

        }
        int remarksIncompleted = 0;
        for (int i = 0; i < delList.length; i++) {
            if (delList[i][5].equals("")) {
                remarksIncompleted++;

            }
        }
        if (remarksIncompleted == 0) {
            MenuItem menuItem1 = menu.findItem(R.id.menu_collection_next);
            menuItem1.setVisible(false);
            MenuItem menuItem2 = menu.findItem(R.id.menu_collection_save);
            menuItem2.setVisible(true);
        }


    }

    /*gets dealer who are not visited today from route plan*/
    private void getDealer() {


        try {
            String routeQry = Constants.RoutePlans + "?$filter=" + Constants.VisitDate + " eq datetime'" + closingDate + "'";

            String mGetRouteQry = OfflineManager.getRouteQry(routeQry);

            if (!mGetRouteQry.equalsIgnoreCase("")) {
                mGetRouteQry = Constants.RouteSchedulePlans + "?$filter=" + mGetRouteQry;
                delList = OfflineManager.getNotVisitedRetailerList(mGetRouteQry, UtilConstants.getNewDate());
                retailerRemarks = new String[delList.length];
            } else {
                delList = null;
                retailerRemarks = new String[1];
            }

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }


    }

    private void onNext() {
        if (etRemarks.getText() != null
                && !etRemarks.getText().toString().trim()
                .equalsIgnoreCase("")) {
            delList[currentRetailerId][5] = etRemarks.getText().toString();
            if (currentRetailerId < delList.length - 1)
                nextRetailer = true;
            else
                nextRetailer = false;
            setDealers();
//				retailerRemarks[currentRetailerId] = etRemarks.getText().toString();
        } else {
            etRemarks.setBackgroundResource(R.drawable.edittext_border);
            UtilConstants.showAlert(getString(R.string.msg_remarks), DayEndRemarksActivity.this);
        }
    }

    private void saveAllRetailers() {
        for (int i = 0; i < delList.length; i++) {
            GUID guid = GUID.newRandom();

            Hashtable table = new Hashtable();
            //noinspection unchecked
            table.put(Constants.CPNo, UtilConstants.removeLeadingZeros(delList[i][0]));

            table.put(Constants.CPName, delList[i][2]);
            //noinspection unchecked
            table.put(Constants.STARTDATE, closingDate);
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

            table.put(Constants.STARTTIME, oDataDuration);
            //noinspection unchecked
            table.put(Constants.StartLat, BigDecimal.valueOf(UtilConstants.latitude));
            //noinspection unchecked
            table.put(Constants.StartLong, BigDecimal.valueOf(UtilConstants.longitude));
            //noinspection unchecked
            table.put(Constants.EndLat, BigDecimal.valueOf(UtilConstants.latitude));
            //noinspection unchecked
            table.put(Constants.EndLong, BigDecimal.valueOf(UtilConstants.latitude));
//				//noinspection unchecked
//				table.put(Constants.ENDDATE,  UtilConstants.getNewDateTimeFormat());

            //noinspection unchecked
            table.put(Constants.ENDDATE, closingDate);


            //noinspection unchecked
            table.put(Constants.ENDTIME, oDataDuration);
            //noinspection unchecked
            table.put(Constants.VISITKEY, guid.toString());

            table.put(Constants.ROUTEPLANKEY, Constants.convertStrGUID32to36(delList[i][4].toUpperCase()));

            table.put(Constants.StatusID, "02");

            table.put(Constants.REMARKS, delList[i][5].trim());

//				table.put(Constants.CPTypeID, mStrCPTypeId);

            table.put(Constants.VisitDate, UtilConstants.getNewDateTimeFormat());

            table.put(Constants.CPGUID, delList[i][3]);

            table.put(Constants.SPGUID, mStrSPGUID);

//                table.put(Constants.CPGUID,mCpGuid);


            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);

            int sharedVal = sharedPreferences.getInt(Constants.VisitSeqId, 0);

            String loginIdVal = sharedPreferences.getString(Constants.username, "");
            //noinspection unchecked
            table.put(Constants.LOGINID, loginIdVal);

            table.put(Constants.VisitSeq, sharedVal + "");

            sharedVal++;

            SharedPreferences sharedPreferencesVal = getSharedPreferences(Constants.PREFS_NAME, 0);
            SharedPreferences.Editor editor = sharedPreferencesVal.edit();
            editor.putInt(Constants.VisitSeqId, sharedVal);
            editor.commit();

            try {
                //noinspection unchecked
                OfflineManager.createVisitStartEnd(table);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }

        }

        AlertDialog.Builder businessbuilder = new AlertDialog.Builder(
                DayEndRemarksActivity.this, R.style.MyTheme);
        businessbuilder
                .setMessage(getString(R.string.Saved_Successfully))
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                                if (ConstantsUtils.isAutomaticTimeZone(DayEndRemarksActivity.this)) {
                                    onSaveClose();
                                } else {
                                    ConstantsUtils.showAutoDateSetDialog(DayEndRemarksActivity.this);
                                }


                            }
                        });
        businessbuilder.show();


    }

    private void closeProgressDialog() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /*marks visit for not visited retailer with remark*/
    private void onSave() {

        //	if (Constants.onGpsCheck(getApplicationContext())) {


        pdLoadDialog = Constants.showProgressDialog(DayEndRemarksActivity.this, "", getString(R.string.checking_pemission));
        LocationUtils.checkLocationPermission(DayEndRemarksActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                closeProgressDialog();
                if (status) {
                    if (etRemarks.getText() != null
                            && !etRemarks.getText().toString().trim()
                            .equalsIgnoreCase("")) {

                        GUID guid = GUID.newRandom();

                        Hashtable table = new Hashtable();
                        //noinspection unchecked
                        table.put(Constants.CPNo, UtilConstants.removeLeadingZeros(selDealer));

                        table.put(Constants.CPName, selDelName);
                        //noinspection unchecked
                        table.put(Constants.STARTDATE, closingDate);

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

                        table.put(Constants.STARTTIME, oDataDuration);
                        //noinspection unchecked
                        table.put(Constants.StartLat, BigDecimal.valueOf(UtilConstants.latitude));
                        //noinspection unchecked
                        table.put(Constants.StartLong, BigDecimal.valueOf(UtilConstants.longitude));
                        //noinspection unchecked
                        table.put(Constants.EndLat, BigDecimal.valueOf(UtilConstants.latitude));
                        //noinspection unchecked
                        table.put(Constants.EndLong, BigDecimal.valueOf(UtilConstants.latitude));
//				//noinspection unchecked
//				table.put(Constants.ENDDATE,  UtilConstants.getNewDateTimeFormat());

                        //noinspection unchecked
                        table.put(Constants.ENDDATE, closingDate);


                        //noinspection unchecked
                        table.put(Constants.ENDTIME, oDataDuration);
                        //noinspection unchecked
                        table.put(Constants.VISITKEY, guid.toString());

                        table.put(Constants.ROUTEPLANKEY, Constants.convertStrGUID32to36(selRoutePlanKey.toUpperCase()));

                        table.put(Constants.StatusID, "02");

                        table.put(Constants.REMARKS, etRemarks.getText().toString().trim());

//				table.put(Constants.CPTypeID, mStrCPTypeId);

                        table.put(Constants.VisitDate, UtilConstants.getNewDateTimeFormat());

                        table.put(Constants.CPGUID, selDelGuid);

                        table.put(Constants.SPGUID, mStrSPGUID);

//                table.put(Constants.CPGUID,mCpGuid);


                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);

                        int sharedVal = sharedPreferences.getInt(Constants.VisitSeqId, 0);

                        String loginIdVal = sharedPreferences.getString(Constants.username, "");
                        //noinspection unchecked
                        table.put(Constants.LOGINID, loginIdVal);

                        table.put(Constants.VisitSeq, sharedVal + "");

                        sharedVal++;

                        SharedPreferences sharedPreferencesVal = getSharedPreferences(Constants.PREFS_NAME, 0);
                        SharedPreferences.Editor editor = sharedPreferencesVal.edit();
                        editor.putInt(Constants.VisitSeqId, sharedVal);
                        editor.commit();

                        try {
                            //noinspection unchecked
                            OfflineManager.createVisitStartEnd(table);
                        } catch (OfflineODataStoreException e) {
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                        }

                        AlertDialog.Builder businessbuilder = new AlertDialog.Builder(
                                DayEndRemarksActivity.this, R.style.MyTheme);
                        businessbuilder
                                .setMessage(getString(R.string.Saved_Successfully))
                                .setCancelable(false)
                                .setPositiveButton(R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                dialog.cancel();
                                                etRemarks.setText("");

                                                if (delList.length == 1) {
                                                    onSaveClose();
                                                } else {
                                                    getDealer();
                                                }

                                            }
                                        });
                        businessbuilder.show();
                    } else {
                        // error
                        etRemarks.setBackgroundResource(R.drawable.edittext_border);
                        UtilConstants.showAlert(getString(R.string.msg_remarks), DayEndRemarksActivity.this);
                    }
                }
            }
        });


        //	}

    }

    /*Close day if remarks for all not visited retailer filled*/
    private void onSaveClose() {
        Constants.MapEntityVal.clear();

        String qry = Constants.Attendances + "?$filter=EndDate eq null and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
        try {
            OfflineManager.getAttendance(qry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

        Hashtable hashTableAttendanceValues;


        hashTableAttendanceValues = new Hashtable();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);

        String loginIdVal = sharedPreferences.getString(Constants.username, "");
        //noinspection unchecked
        hashTableAttendanceValues.put(Constants.LOGINID, loginIdVal);
        //noinspection unchecked
        hashTableAttendanceValues.put(Constants.AttendanceGUID, Constants.MapEntityVal.get(Constants.AttendanceGUID));
        //noinspection unchecked
        hashTableAttendanceValues.put(Constants.StartDate, Constants.MapEntityVal.get(Constants.StartDate));
        //noinspection unchecked
        hashTableAttendanceValues.put(Constants.StartTime, Constants.MapEntityVal.get(Constants.StartTime));
        //noinspection unchecked
        hashTableAttendanceValues.put(Constants.StartLat, Constants.MapEntityVal.get(Constants.StartLat));
        //noinspection unchecked
        hashTableAttendanceValues.put(Constants.StartLong, Constants.MapEntityVal.get(Constants.StartLong));
        //noinspection unchecked
        hashTableAttendanceValues.put(Constants.EndLat, BigDecimal.valueOf(UtilConstants.latitude));
        //noinspection unchecked
        hashTableAttendanceValues.put(Constants.EndLong, BigDecimal.valueOf(UtilConstants.longitude));
        //noinspection unchecked
        hashTableAttendanceValues.put(Constants.EndDate, UtilConstants.getNewDateTimeFormat());

        hashTableAttendanceValues.put(Constants.SPGUID, mStrSPGUID);


        hashTableAttendanceValues.put(Constants.SetResourcePath, Constants.MapEntityVal.get(Constants.SetResourcePath));

        if (Constants.MapEntityVal.get(Constants.Etag) != null) {
            hashTableAttendanceValues.put(Constants.Etag, Constants.MapEntityVal.get(Constants.Etag));
        } else {
            hashTableAttendanceValues.put(Constants.Etag, "");
        }

        hashTableAttendanceValues.put(Constants.Remarks, Constants.MapEntityVal.get(Constants.Remarks));
        hashTableAttendanceValues.put(Constants.AttendanceTypeH1, Constants.MapEntityVal.get(Constants.AttendanceTypeH1));
        hashTableAttendanceValues.put(Constants.AttendanceTypeH2, Constants.MapEntityVal.get(Constants.AttendanceTypeH2));

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

        //noinspection unchecked
        hashTableAttendanceValues.put(Constants.EndTime, oDataDuration);

        SharedPreferences sharedPreferencesVal = getSharedPreferences(Constants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferencesVal.edit();
        editor.putInt(Constants.VisitSeqId, 0);
        editor.commit();

        try {
            //noinspection unchecked
            OfflineManager.updateAttendance(hashTableAttendanceValues, DayEndRemarksActivity.this);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }


    @Override
    public void onRequestError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e, DayEndRemarksActivity.this);
        if (errorBean.hasNoError()) {
            mStrPopUpText = getString(R.string.close_update_with_err);

            if (operation == Operation.Create.getValue()) {

                displayPopUpMsg();
            } else if (operation == Operation.Update.getValue()) {

                displayPopUpMsg();
            }
        } else {
            Constants.isSync = false;
//		closeProgressDialog();
            Constants.displayMsgReqError(errorBean.getErrorCode(), DayEndRemarksActivity.this);
//		onBackPressed();
        }
    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.Create.getValue()) {
            mStrPopUpText = getString(R.string.Day_ended);
            displayPopUpMsg();

        } else if (operation == Operation.Update.getValue()) {
            mStrPopUpText = getString(R.string.Day_ended);

            displayPopUpMsg();

        }

    }

    /*displays alert with Message*/
    public void displayPopUpMsg() {
        UtilConstants.dialogBoxWithCallBack(DayEndRemarksActivity.this, "", mStrPopUpText, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (!AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore,DayEndRemarksActivity.this, BuildConfig.APPLICATION_ID,true));{
                    onBackPressed();
                }
            }
        });
       /* AlertDialog.Builder builder = new AlertDialog.Builder(DayEndRemarksActivity.this, R.style.MyTheme);
        builder.setMessage(mStrPopUpText)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();
                                    onBackPressed();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
        builder.show();*/
    }


}
