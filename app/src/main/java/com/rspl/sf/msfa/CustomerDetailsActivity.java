package com.rspl.sf.msfa;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.rspl.sf.msfa.adapter.FragmentWithTitleBean;
import com.rspl.sf.msfa.adapter.RetailerDetailPagetTabAdapter;
import com.rspl.sf.msfa.adapter.ViewPagerTabAdapter;
import com.rspl.sf.msfa.asyncTask.RefreshAsyncTask;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.MSFAApplication;
import com.rspl.sf.msfa.interfaces.CustomDialogCallBack;
import com.rspl.sf.msfa.so.SOUtils;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.visit.StocksInfoActivity;
import com.rspl.sf.msfa.visit.VisitFragment;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.impl.ODataDurationDefaultImpl;
import com.sap.smp.client.odata.impl.ODataGuidDefaultImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by ${e10526} on ${16-11-2016}.
 */
@SuppressLint("NewApi")
public class CustomerDetailsActivity extends AppCompatActivity implements View.OnClickListener, UIListener {
    String mStrCPTypeId = "";
    ImageView iv_visit_status;
    Map<String, String> startParameterMap;
    String address = "", mobNo = "";
    TabLayout tabLayout;
    /*
                 Enter remarks in visit table if activity is not done.

                */
    boolean wantToCloseDialog = false;
    private String mStrCustomerName = "";
    private String mStrUID = "";
    private String mStrCustomerId = "";
    private String mStrBundleCpGuid = "";
    private String mStrComingFrom = "";
    private String mStrRouteGuid = "";
    private String mStrRouteName = "";
    private String mStrCurrency = "";
    private String mStrPopUpText = "";
    private String mStrCustNo = "";
    private String mStrOtherRetailerGuid = "";
    //new
    private String mStrVisitEndRemarks = "";
    private ODataPropMap oDataProperties;
    private ODataProperty oDataProperty;
    private ODataGuid mStrVisitId = null;
    private boolean mBooleanVisitStarted = false;
    private ProgressDialog pdLoadDialog;
    private boolean mBooleanNavPrvVisitClosed = false;
    private boolean mBooleanSaveStart = false;
    private boolean mBooleanVisitStartDialog = false, mBooleanVisitEndDialog = false;
    private String mStrVisitCatId = "";
    //This is our viewPager
    private ViewPager viewPager;
    private boolean mBoolMsg = false;
    private String mStrComingFromBeatPlanProspective = "";
    private String mStrSPGUID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)

        setContentView(R.layout.activity_retailer_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Constants.mApplication = (MSFAApplication) getApplication();
//        Constants.parser = Constants.mApplication.getParser();

        TextView tvCustomerID = (TextView) findViewById(R.id.tv_RetailerID);
        TextView tvCustomerName = (TextView) findViewById(R.id.tv_RetailerName);

        startParameterMap = new HashMap<String, String>();

        iv_visit_status = (ImageView) findViewById(R.id.iv_visit_status);
        iv_visit_status.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mStrCustomerName = extras.getString(Constants.RetailerName);
            mStrUID = extras.getString(Constants.CPUID);
            mStrCustomerId = extras.getString(Constants.CPNo);
            mStrComingFrom = extras.getString(Constants.comingFrom);
            mStrComingFromBeatPlanProspective = extras.getString("BeatPlanProspectiveCustomer");
            if (mStrComingFromBeatPlanProspective == null) {

                mStrComingFromBeatPlanProspective = "";

            }
            try {
                address = extras.getString("Address");
                mobNo = extras.getString("MobileNo");
            } catch (Exception e) {
                e.printStackTrace();
            }
            mStrBundleCpGuid = extras.getString(Constants.CPGUID) != null ? extras.getString(Constants.CPGUID) : "";
            if (mStrBundleCpGuid.equalsIgnoreCase(""))
                mStrBundleCpGuid = mStrCustomerId;
            mStrRouteName = extras.getString(Constants.OtherRouteName) != null ? extras.getString(Constants.OtherRouteName) : "";
            mStrRouteGuid = extras.getString(Constants.OtherRouteGUID) != null ? extras.getString(Constants.OtherRouteGUID) : "";
            mStrVisitCatId = extras.getString(Constants.VisitCatID);
            mStrCurrency = extras.getString(Constants.Currency);
            Constants.VisitNavigationFrom = mStrComingFrom;

        }
        mStrSPGUID = Constants.getSPGUID(Constants.SPGUID);

        if (!mStrComingFrom.equals("ProspectiveCustomerList")) {
            ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_retailer_details), 0);
        } else {
            ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_prospected_customer), 0);

        }


        mStrCustNo = mStrCustomerId;
        tvCustomerName.setText(mStrCustomerName);

        if (!TextUtils.isEmpty(mStrCustomerId)) {
            tvCustomerID.setText(mStrCustomerId);

        }

        if (!mStrComingFrom.equals(Constants.AdhocList) || !SOUtils.isHideVisit(CustomerDetailsActivity.this)) {
            displayVisitIcon();
        } else {
            mBooleanVisitStarted = true;
            iv_visit_status.setVisibility(View.GONE);
        }

        tabIntilize();
    }

    /*
        Display Visit Status Icon
    */
    private void displayVisitIcon() {
        if (!mStrComingFrom.equalsIgnoreCase(Constants.RouteList)) {
            Constants.Route_Plan_Key = "";
        }
        if (mStrComingFrom.equalsIgnoreCase(Constants.AdhocList)
                || mStrComingFrom.equalsIgnoreCase(Constants.CustomerList)
                || mStrComingFrom.equalsIgnoreCase(Constants.RouteList)
                || mStrComingFrom.equalsIgnoreCase(Constants.OtherRouteList)
                || mStrComingFrom.equalsIgnoreCase(Constants.ProspectiveCustomerList)
                || mStrComingFrom.equalsIgnoreCase(Constants.MTPList)) {
            iv_visit_status.setVisibility(View.VISIBLE);
        } else {
            iv_visit_status.setVisibility(View.GONE);
        }

//        if(!mStrComingFrom.equalsIgnoreCase(Constants.ProspectiveCustomerList)){
        mBooleanVisitStartDialog = false;
        mBooleanVisitEndDialog = false;
        String mStrVisitStartEndQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq datetime'" + UtilConstants.getNewDate() + "'" +
                "and " + Constants.CPNo + " eq '" + mStrCustomerId + "' and " + Constants.StatusID + " eq '01' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";


        String mStrVisitStartedQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq null " +
                "and " + Constants.CPNo + " eq '" + mStrCustomerId + "'and " + Constants.StatusID + " eq '01' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";

        try {
            if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartedQry)) {
                iv_visit_status.setImageResource(R.drawable.stop);
                mBooleanVisitStarted = true;
            } else if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartEndQry)) {
                iv_visit_status.setImageResource(R.drawable.ic_done_black_24dp);
                mBooleanVisitStarted = false;
            } else {
                Constants.MapEntityVal.clear();

                String qry = Constants.Visits + "?$filter=EndDate eq null and " + Constants.CPGUID + " eq '" + mStrCustomerId + "' " +
                        "and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.StatusID + " eq '01' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";

                try {
                    mStrVisitId = OfflineManager.getVisitDetails(qry);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                    ConstantsUtils.printErrorLog(e.getMessage());
                }

                if (!Constants.MapEntityVal.isEmpty()) {
                    iv_visit_status.setImageResource(R.drawable.stop);
                    mBooleanVisitStarted = true;
                } else {
                    iv_visit_status.setImageResource(R.drawable.start);
                    mBooleanVisitStarted = false;
                }
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog(e.getMessage());
        }
//        }else{
//            onStockInfoCreate();
//        }


    }

    private void onStockInfoCreate() {
        Intent intentNavScreen = new Intent(CustomerDetailsActivity.this, StocksInfoActivity.class);
        intentNavScreen.putExtra(Constants.CPNo, mStrCustomerId);
        intentNavScreen.putExtra(Constants.CPUID, mStrCustomerId);
        intentNavScreen.putExtra(Constants.RetailerName, mStrCustomerName);
        intentNavScreen.putExtra(Constants.Address, address);
        intentNavScreen.putExtra(Constants.SalesPersonMobileNo, mobNo);
        // intentNavScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentNavScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        startActivity(intentNavScreen);
    }

    /*
       Navigate to Previous List Screens
   */
    private void NavigateToListScreen() {
        finish();
       /* if (mStrComingFrom.equalsIgnoreCase(Constants.CustomerList)) {
            Intent intRouteList = new Intent(CustomerDetailsActivity.this,
                    CustomerListActivity.class);
            intRouteList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intRouteList.putExtra(Constants.RetailerName, Constants.Route_Plan_Desc);
            intRouteList.putExtra(Constants.CPNo, Constants.Route_Plan_No);
            intRouteList.putExtra(Constants.VISITTYPE, Constants.Visit_Type);
            startActivity(intRouteList);
        } else if (mStrComingFrom.equalsIgnoreCase(Constants.RouteList)) {
            Intent intRouteList = new Intent(CustomerDetailsActivity.this,
                    RoutePlanListActivity.class);
            intRouteList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intRouteList);
        } else if (mStrComingFrom.equalsIgnoreCase(Constants.AdhocList)) {
            Intent intRouteList = new Intent(CustomerDetailsActivity.this,
                    AdhocListActivity.class);
            intRouteList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intRouteList);
        } else if (mStrComingFrom.equalsIgnoreCase(Constants.MTPList)) {
            finish();
        } else if (mStrComingFrom.equalsIgnoreCase(Constants.OtherRouteList)) {
            Intent intRouteList = new Intent(CustomerDetailsActivity.this,
                    OtherBeatListActivity.class);
            intRouteList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intRouteList.putExtra(Constants.OtherRouteGUID, mStrRouteGuid);
            intRouteList.putExtra(Constants.OtherRouteName, mStrRouteName);
            startActivity(intRouteList);
        } else if (mStrComingFrom.equalsIgnoreCase(Constants.ProspectiveCustomerList)) {

            if (mStrComingFromBeatPlanProspective.equals("BeatPlanProspectiveCustomer")) {
                Intent intRouteList = new Intent(CustomerDetailsActivity.this,
                        RoutePlanListActivity.class);
                intRouteList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intRouteList);

            } else {

                Intent intRouteList = new Intent(CustomerDetailsActivity.this,
                        ProspectedCustomerList.class);
                intRouteList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intRouteList.putExtra(Constants.OtherRouteGUID, mStrRouteGuid);
                intRouteList.putExtra(Constants.OtherRouteName, mStrRouteName);
                startActivity(intRouteList);
            }

        } else {
            Intent intRouteList = new Intent(CustomerDetailsActivity.this,
                    CustomersListActivity.class);
            intRouteList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intRouteList);
        }*/
    }

    /*
          Dismiss Progress Dialog

      */
    private void dismissProgressDialog() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
      Display error message
  */
    @Override
    public void onRequestError(int operation, Exception e) {
        e.printStackTrace();
        dismissProgressDialog();
        Toast.makeText(CustomerDetailsActivity.this, getString(R.string.err_odata_unexpected, e.getMessage()),
                Toast.LENGTH_LONG).show();
    }

    /*
     Display Success message
 */
    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {

        if (operation == Operation.Create.getValue()) {
            alertPopupMessage();
        } else if (operation == Operation.Update.getValue()) {
            alertPopupMessage();
        } else if (operation == Operation.OfflineFlush.getValue()) {
            if (!UtilConstants.isNetworkAvailable(CustomerDetailsActivity.this)) {
                dismissProgressDialog();
                onNoNetwork();
            } else {
                new RefreshAsyncTask(getApplicationContext(), Constants.Visits, this).execute();
//                OfflineManager.refreshRequests(getApplicationContext(), Constants.Visits, CustomerDetailsActivity.this);
            }

        } else if (operation == Operation.OfflineRefresh.getValue()) {

            dismissProgressDialog();

            if (mBooleanNavPrvVisitClosed) {
                NavigateToListScreen();
            } else {
                if (mBooleanSaveStart) {
                }
            }
        } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
            Constants.isSync = false;
            try {
                OfflineManager.getAuthorizations(getApplicationContext());
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
          //  Constants.setSyncTime(getApplicationContext(),refguid.toString().toUpperCase());
            if (mBooleanNavPrvVisitClosed) {
                NavigateToListScreen();
            } else {
                if (mBooleanSaveStart) {
                }
            }
        }
    }

    /*
    Display Alert message regarding visit started or visit ended.
    */
    private void alertPopupMessage() {
        dismissProgressDialog();
        if (mBooleanVisitStartDialog) {
            mStrPopUpText = getString(R.string.visit_started);
        }
        if (mBooleanVisitEndDialog)
            mStrPopUpText = getString(R.string.visit_ended);


        AlertDialog.Builder builder = new AlertDialog.Builder(
                CustomerDetailsActivity.this, R.style.MyTheme);
        builder.setMessage(mStrPopUpText)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();

                                    if (mBooleanNavPrvVisitClosed) {
                                        NavigateToListScreen();
                                    } else {
                                        if (mBooleanSaveStart) {
                                            mBooleanVisitStarted = true;
                                            setupViewPagerWithVisit();
                                            tabLayout.setupWithViewPager(viewPager);
                                            viewPager.setCurrentItem(1);
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });
        builder.show();


    }

    private void onSaveClose() {
        mStrPopUpText = getString(R.string.marking_visit_end_plz_wait);
        try {
            new ClosingVisit().execute();
        } catch (Exception e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog(e.getMessage());
        }
    }

    private void onSaveStart() {
        mStrPopUpText = getString(R.string.marking_visit_start_plz_wait);
        try {
            String cpId = UtilConstants.removeLeadingZeros(mStrCustomerId);
            startParameterMap.put(Constants.CPNo, cpId);
            startParameterMap.put(Constants.CPName, mStrCustomerName);
            startParameterMap.put(Constants.CPTypeID, "01");
            startParameterMap.put(Constants.VisitCatID, mStrVisitCatId);
            startParameterMap.put(Constants.StatusID, "01");
            startParameterMap.put(Constants.PlannedDate, null);
            startParameterMap.put(Constants.PlannedStartTime, null);
            startParameterMap.put(Constants.PlannedEndTime, null);
            startParameterMap.put(Constants.VisitTypeID, "");
            startParameterMap.put(Constants.VisitTypeDesc, "");
            startParameterMap.put(Constants.Remarks, "");


            Constants.createVisit(startParameterMap, mStrCustomerId, CustomerDetailsActivity.this, this);
//            new StartVisit().execute();
        } catch (Exception e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog(e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_visit_status:
                checkVisitStartLocPermission();
                break;
        }
    }

    private void checkVisitStartLocPermission() {
        pdLoadDialog = Constants.showProgressDialog(CustomerDetailsActivity.this, "", getString(R.string.checking_pemission));
        LocationUtils.checkLocationPermission(CustomerDetailsActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                dismissProgressDialog();
                if (status) {
                    if (ConstantsUtils.isAutomaticTimeZone(CustomerDetailsActivity.this)) {
                        onVisitAction();
                    } else {
                        ConstantsUtils.showAutoDateSetDialog(CustomerDetailsActivity.this);
                    }

                }
            }
        });
    }

    private void selectPage(int pageIndex) {
        tabLayout.setScrollPosition(pageIndex, 0f, false);
        viewPager.setCurrentItem(pageIndex, false);
    }

    private void onVisitAction() {
        if (mStrComingFrom.equalsIgnoreCase(Constants.AdhocList) ||
                mStrComingFrom.equalsIgnoreCase(Constants.CustomerList)
                || mStrComingFrom.equalsIgnoreCase(Constants.RouteList)
                || mStrComingFrom.equalsIgnoreCase(Constants.OtherRouteList)
                || mStrComingFrom.equalsIgnoreCase(Constants.ProspectiveCustomerList)
                || mStrComingFrom.equalsIgnoreCase(Constants.MTPList)) {

            Constants.MapEntityVal.clear();

            String qry = Constants.Visits + "?$filter=EndDate eq null and " + Constants.CPGUID + " eq '" + mStrCustomerId + "' " +
                    "and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.StatusID + " eq '01' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";

            try {
                mStrVisitId = OfflineManager.getVisitDetails(qry);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
                ConstantsUtils.printErrorLog(e.getMessage());
            }

            if (!Constants.MapEntityVal.isEmpty()) {
                mBooleanVisitStarted = true;
            } else {
                mBooleanVisitStarted = false;
            }


            mBooleanSaveStart = false;
            mBooleanNavPrvVisitClosed = false;
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
            if (sharedPreferences.getString(Constants.isStartCloseEnabled, "").equalsIgnoreCase(Constants.isStartCloseTcode)) {
                Constants.MapEntityVal.clear();

                String attdIdStr = "";
                String attnQry = Constants.Attendances + "?$filter=EndDate eq null and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
                try {
                    attdIdStr = OfflineManager.getAttendance(attnQry);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                    ConstantsUtils.printErrorLog(e.getMessage());
                }
                if (!attdIdStr.equalsIgnoreCase("")) {
                    onVisitStartFunctionality(iv_visit_status);
                } else {
                    attdIdStr = "";
                    String dayEndqry = Constants.Attendances + "?$filter=EndDate eq null and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
                    try {
                        attdIdStr = OfflineManager.getAttendance(dayEndqry);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                        ConstantsUtils.printErrorLog(e.getMessage());
                    }
                    if (!TextUtils.isEmpty(attdIdStr)) {
                        Toast.makeText(getApplicationContext(), getString(R.string.attend_close_prev_day), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.alert_plz_start_day), Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                onVisitStartFunctionality(iv_visit_status);
            }


        }
    }

    private void onVisitStartFunctionality(final ImageView iv_visit_status) {
        if (!mBooleanVisitStarted) {
            mStrOtherRetailerGuid = "";

            //new 28112016
            String otherRetVisitQuery = Constants.Visits + "?$filter=EndDate eq null and " + Constants.CPGUID + " ne '" + mStrCustomerId + "' " +
                    "and StartDate eq datetime'" + UtilConstants.getNewDate() + "'and " + Constants.StatusID + " eq '01' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";

            String[] otherRetDetails = new String[2];
            try {


                otherRetDetails = OfflineManager.checkVisitForOtherRetailer(otherRetVisitQuery);


            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
                ConstantsUtils.printErrorLog(e.getMessage());
            }

            if (otherRetDetails[0] == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        CustomerDetailsActivity.this, R.style.MyTheme);

                builder.setMessage(R.string.alert_start_visit)
                        .setCancelable(false)
                        .setPositiveButton(
                                R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();
                                        if (ConstantsUtils.isAutomaticTimeZone(CustomerDetailsActivity.this)) {
                                            mBooleanSaveStart = true;
                                            mBooleanVisitStartDialog = true;
                                            mBooleanVisitEndDialog = false;
                                            onSaveStart();
                                            iv_visit_status.setImageResource(R.drawable.stop);
                                        } else {
                                            onRefreshVisitIcon();
                                            ConstantsUtils.showAutoDateSetDialog(CustomerDetailsActivity.this);
                                        }

                                    }
                                });
                builder.setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                                onRefreshVisitIcon();
                            }

                        });

                builder.show();
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        CustomerDetailsActivity.this, R.style.MyTheme);
                final String[] finalOtherRetDetails = otherRetDetails;

                builder.setMessage(getString(R.string.visit_end_not_marked_for_specific_retailer, otherRetDetails[0]))
                        .setCancelable(false)
                        .setPositiveButton(
                                R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();
                                        if (ConstantsUtils.isAutomaticTimeZone(CustomerDetailsActivity.this)) {
                                            mStrOtherRetailerGuid = finalOtherRetDetails[1];

                                            boolean isVisitActivities = false;
                                            try {
                                                isVisitActivities = OfflineManager.checkVisitActivitiesForRetailer(Constants.VisitActivities + "?$filter=" + Constants.VISITKEY + " eq guid'" + mStrOtherRetailerGuid + "'");
                                            } catch (OfflineODataStoreException e) {
                                                e.printStackTrace();
                                            }
                                            mStrVisitId = ODataGuidDefaultImpl.initWithString36(mStrOtherRetailerGuid);
                                            if (isVisitActivities) {
                                                mBooleanVisitEndDialog = true;
                                                onSaveClose();
                                            } else {
                                                wantToCloseDialog = false;
                                                onAlertDialogForVisitDayEndRemarks();
                                            }
                                        } else {
                                            onRefreshVisitIcon();
                                            ConstantsUtils.showAutoDateSetDialog(CustomerDetailsActivity.this);
                                        }

                                    }
                                });
                builder.setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                                onRefreshVisitIcon();
                            }

                        });

                builder.show();

            }
        } else {
            mBoolMsg = false;
            onVisitClosingAction();
        }
    }

    @Override
    public void onBackPressed() {

//        if(!mStrComingFrom.equals("ProspectiveCustomerList")){

//        finish();
        mBoolMsg = true;
        onVisitClosingAction();
//        }else{
//
//            Intent intRouteList = new Intent(CustomerDetailsActivity.this,
//                    ProspectedCustomerList_01.class);
//            intRouteList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intRouteList);
//
//        }


    }

    private void onVisitClosingAction() {
        if (mStrComingFrom.equalsIgnoreCase(Constants.RetailerList)) {
           /* Intent intRouteList = new Intent(CustomerDetailsActivity.this,
                    CustomersListActivity.class);
            intRouteList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intRouteList);*/
            finish();
        } else {

            mStrVisitId = null;

            String visitQry = Constants.Visits + "?$filter=EndDate eq null and CPGUID eq '" +
                    mStrCustomerId + "' and StartDate eq datetime'" +
                    UtilConstants.getNewDate() + "' and " + Constants.StatusID + " eq '01' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";


            try {
                mStrVisitId = OfflineManager.getVisitDetails(visitQry);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
                ConstantsUtils.printErrorLog(e.getMessage());
            }

            if (mStrVisitId != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        CustomerDetailsActivity.this, R.style.MyTheme);

                String mStrEndDilaog = "", mStrPostive = "", mStrNegative = "";

                if (mBoolMsg) {
                    mStrEndDilaog = getString(R.string.alert_visit_pause);
                    mStrPostive = getString(R.string.mark_now);
                    mStrNegative = getString(R.string.later);
                } else {
                    mStrEndDilaog = getString(R.string.alert_end_visit);
                    mStrPostive = getString(R.string.yes);
                    mStrNegative = getString(R.string.no);
                }


                builder.setMessage(mStrEndDilaog)
                        .setCancelable(false)
                        .setPositiveButton(mStrPostive,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {

                                        LocationUtils.checkLocationPermission(CustomerDetailsActivity.this, new LocationInterface() {
                                            @Override
                                            public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                                                if (status) {
                                                    if (ConstantsUtils.isAutomaticTimeZone(CustomerDetailsActivity.this)) {
                                                        boolean isVisitActivities = false;
                                                        try {
                                                            isVisitActivities = OfflineManager.checkVisitActivitiesForRetailer(Constants.VisitActivities + "?$filter=" + Constants.VISITKEY + " eq guid'" + mStrVisitId.guidAsString36() + "'");
                                                        } catch (OfflineODataStoreException e) {
                                                            e.printStackTrace();
                                                        }
                                                        if (isVisitActivities) {
                                                            mBooleanNavPrvVisitClosed = true;
                                                            mBooleanVisitStartDialog = false;
                                                            mBooleanVisitEndDialog = true;
                                                            iv_visit_status.setImageResource(R.drawable.start);
                                                            onSaveClose();
                                                        } else {
                                                            wantToCloseDialog = false;
                                                            onAlertDialogForVisitDayEndRemarks();
                                                        }
                                                    } else {
                                                        ConstantsUtils.showAutoDateSetDialog(CustomerDetailsActivity.this);
                                                    }
                                                }
                                            }
                                        });

                                    }
                                });
                builder.setNegativeButton(mStrNegative,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                                NavigateToListScreen();

                            }

                        });


                builder.show();
            } else {
                NavigateToListScreen();
            }
        }
    }

    /*
        Check Network available or not
       */
    private void onNoNetwork() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                CustomerDetailsActivity.this, R.style.MyTheme);
        builder.setMessage(
                R.string.alert_sync_cannot_be_performed)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (mBooleanNavPrvVisitClosed) {
                            NavigateToListScreen();
                        } else {
                            if (mBooleanSaveStart) {
                            }
                        }


                    }
                });

        builder.show();
    }


    private String getRouteNo() {

        String mStrRouteKey = "";
        String qryStr = Constants.RouteSchedulePlans + "?$filter=" + Constants.VisitCPGUID + " eq '" + mStrCustomerId.toUpperCase() + "' ";
        try {
            mStrRouteKey = OfflineManager.getRoutePlanKeyNew(qryStr);

        } catch (OfflineODataStoreException e) {
            mStrRouteKey = "";
            e.printStackTrace();
        }
        return mStrRouteKey;
    }

 /*
            Refresh Visit Status Icon
           */

    private void onRefreshVisitIcon() {

        String mStrVisitStartEndQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq datetime'" + UtilConstants.getNewDate() + "' " +
                "and CPGUID eq '" + mStrCustomerId + "' and " + Constants.StatusID + " eq '01' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
        try {
            if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartEndQry)) {
                iv_visit_status.setImageResource(R.drawable.ic_done_black_24dp);
                mBooleanVisitStarted = false;
            } else {
                Constants.MapEntityVal.clear();
                String qry = Constants.Visits + "?$filter=EndDate eq null and CPGUID eq '" + mStrCustomerId + "' " +
                        "and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.StatusID + " eq '01' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
                try {
                    mStrVisitId = OfflineManager.getVisitDetails(qry);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                    ConstantsUtils.printErrorLog(e.getMessage());
                }

                if (!Constants.MapEntityVal.isEmpty()) {
                    iv_visit_status.setImageResource(R.drawable.stop);
                    mBooleanVisitStarted = true;
                } else {
                    iv_visit_status.setImageResource(R.drawable.start);
                    mBooleanVisitStarted = false;
                }
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog(e.getMessage());
        }
    }

    /*
               Initialize Tab
              */
    private void tabIntilize() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager();

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        tabLayout.setupWithViewPager(viewPager);
    }

    /*
              Set up fragments into adapter

             */
    private void setupViewPager() {
        ViewPagerTabAdapter adapter = new ViewPagerTabAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();

        bundle.putString(Constants.RetailerName, mStrCustomerName);
        bundle.putString(Constants.CPNo, mStrCustomerId);
        bundle.putString(Constants.CPUID, mStrUID);
        bundle.putString(Constants.CPUID, mStrUID);
        bundle.putString(Constants.CPGUID, mStrCustomerId);
        bundle.putString(Constants.CPGUID32, mStrCustomerId);
        bundle.putString("Address", address);
        bundle.putString("MobNo", mobNo);
        bundle.putString("ComeFrom", mStrComingFrom);


        AddressFragment addressFragment = AddressFragment.newInstance(mStrCustNo, mStrCustomerName, mStrBundleCpGuid, address, mobNo, mStrComingFrom);

        ReportsFragment reportsFragment = new ReportsFragment();
        reportsFragment.setArguments(bundle);

//        SummaryFragment summaryFragment = new SummaryFragment();
//        summaryFragment.setArguments(bundle);

        adapter.addFrag(addressFragment, Constants.Address);
        if (mBooleanVisitStarted) {
            if (mStrComingFrom.equalsIgnoreCase(Constants.AdhocList) ||
                    mStrComingFrom.equalsIgnoreCase(Constants.CustomerList)
                    || mStrComingFrom.equalsIgnoreCase(Constants.RouteList)
                    || mStrComingFrom.equalsIgnoreCase(Constants.OtherRouteList)
                    || mStrComingFrom.equalsIgnoreCase(Constants.ProspectiveCustomerList)
                    || mStrComingFrom.equalsIgnoreCase(Constants.MTPList)) {

                VisitFragment visitFragment = new VisitFragment();

                Bundle bundleVisit = new Bundle();
                bundleVisit.putString(Constants.CPGUID32, mStrCustomerId);
                bundleVisit.putString(Constants.RetailerName, mStrCustomerName);
                bundleVisit.putString(Constants.CPNo, mStrCustomerId);
                bundleVisit.putString(Constants.CPUID, mStrUID);
                bundleVisit.putString(Constants.CPGUID, mStrCustomerId);
                bundleVisit.putString(Constants.comingFrom, mStrComingFrom);
                bundleVisit.putString(Constants.Currency, mStrCurrency);

                visitFragment.setArguments(bundleVisit);
                adapter.addFrag(visitFragment, Constants.Visit);
            }
        }

        if (!mStrComingFrom.equals("ProspectiveCustomerList")) {
            adapter.addFrag(reportsFragment, Constants.Reports);

        }


//        adapter.addFrag(summaryFragment, Constants.Summary);

        viewPager.setAdapter(adapter);


        if (Constants.ComingFromCreateSenarios.equalsIgnoreCase(Constants.X))
            viewPager.setCurrentItem(1);
        Constants.ComingFromCreateSenarios = "";
    }

    private void setupViewPagerWithVisit() {
        viewPager.setAdapter(null);
        ArrayList<FragmentWithTitleBean> fragmentWithTitleBeanArrayList = new ArrayList<>();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.CPGUID32, mStrCustomerId);
        bundle.putString(Constants.RetailerName, mStrCustomerName);
        bundle.putString(Constants.CPNo, mStrCustomerId);
        bundle.putString(Constants.CPUID, mStrUID);

        AddressFragment addressFragment = AddressFragment.newInstance(mStrCustNo, mStrCustomerName, mStrBundleCpGuid, address, mobNo, mStrComingFrom);

        ReportsFragment reportsFragment = new ReportsFragment();
        reportsFragment.setArguments(bundle);


        fragmentWithTitleBeanArrayList.add(new FragmentWithTitleBean(addressFragment, Constants.Address));

        VisitFragment visitFragment = new VisitFragment();

        Bundle bundleVisit = new Bundle();
        bundleVisit.putString(Constants.CPGUID32, mStrCustomerId);
        bundleVisit.putString(Constants.RetailerName, mStrCustomerName);
        bundleVisit.putString(Constants.CPNo, mStrCustomerId);
        bundleVisit.putString(Constants.CPUID, mStrUID);
        bundleVisit.putString(Constants.CPGUID, mStrCustomerId);
        bundleVisit.putString(Constants.comingFrom, mStrComingFrom);
        visitFragment.setArguments(bundleVisit);
        fragmentWithTitleBeanArrayList.add(new FragmentWithTitleBean(visitFragment, Constants.Visit));

        if (!mStrComingFrom.equals(Constants.ProspectiveCustomerList)) {
            fragmentWithTitleBeanArrayList.add(new FragmentWithTitleBean(reportsFragment, Constants.Reports));

        }


//        fragmentWithTitleBeanArrayList.add(new FragmentWithTitleBean(summaryFragment, Constants.Summary));

        RetailerDetailPagetTabAdapter visitAdapter = new RetailerDetailPagetTabAdapter(getSupportFragmentManager(), fragmentWithTitleBeanArrayList);
        viewPager.setAdapter(visitAdapter);

        viewPager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                viewPager.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });


        if (Constants.ComingFromCreateSenarios.equalsIgnoreCase(Constants.X))
            viewPager.setCurrentItem(1);
        Constants.ComingFromCreateSenarios = "";
    }

    private void onAlertDialogForVisitDayEndRemarks() {
        ConstantsUtils.showVisitRemarksDialog(CustomerDetailsActivity.this, new CustomDialogCallBack() {
            @Override
            public void cancelDialogCallBack(boolean userClicked, String ids, String description) {
                mStrVisitEndRemarks = description;
                if (userClicked) {
                    if (ConstantsUtils.isAutomaticTimeZone(CustomerDetailsActivity.this)) {
                        if (mStrOtherRetailerGuid.equalsIgnoreCase(""))
                            mBooleanNavPrvVisitClosed = true;
                        else
                            mBooleanNavPrvVisitClosed = false;

                        mBooleanVisitStartDialog = false;
                        mBooleanVisitEndDialog = true;

                        wantToCloseDialog = false;
                        onSaveClose();
                        onRefreshVisitIcon();
                    } else {
                        ConstantsUtils.showAutoDateSetDialog(CustomerDetailsActivity.this);
                    }
                }
            }
        }, getString(R.string.alert_plz_enter_remarks));





/*

        AlertDialog.Builder alertDialogVisitEndRemarks = new AlertDialog.Builder(CustomerDetailsActivity.this, R.style.MyTheme);
        alertDialogVisitEndRemarks.setMessage(R.string.alert_plz_enter_remarks);
        alertDialogVisitEndRemarks.setCancelable(false);
        int MAX_LENGTH = 255;

        final EditText etVisitEndRemarks = new EditText(CustomerDetailsActivity.this);
        etVisitEndRemarks.setMaxLines(5);
        etVisitEndRemarks.setHeight(140);
        etVisitEndRemarks.setGravity(Gravity.START);

        if (wantToCloseDialog) {
            etVisitEndRemarks.setBackgroundResource(R.drawable.edittext_border);

        } else {
            etVisitEndRemarks.setBackgroundResource(R.drawable.edittext);
        }

        etVisitEndRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (wantToCloseDialog) {
                    etVisitEndRemarks.setBackgroundResource(R.drawable.edittext_border);
                    wantToCloseDialog = false;
                } else {
                    etVisitEndRemarks.setBackgroundResource(R.drawable.edittext);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(MAX_LENGTH);
        etVisitEndRemarks.setFilters(FilterArray);

        etVisitEndRemarks.setText(mStrVisitEndRemarks.equalsIgnoreCase("") ? mStrVisitEndRemarks : "");
        etVisitEndRemarks.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        etVisitEndRemarks.setLayoutParams(lp);
        alertDialogVisitEndRemarks.setView(etVisitEndRemarks);
        alertDialogVisitEndRemarks.setPositiveButton(R.string.save,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mStrVisitEndRemarks = etVisitEndRemarks.getText().toString();
                        if (mStrVisitEndRemarks.equalsIgnoreCase("")) {
                            etVisitEndRemarks.setBackgroundResource(R.drawable.edittext_border);
                            wantToCloseDialog = true;
                            onAlertDialogForVisitDayEndRemarks();
                        } else {
                            if (mStrOtherRetailerGuid.equalsIgnoreCase(""))
                                mBooleanNavPrvVisitClosed = true;
                            else
                                mBooleanNavPrvVisitClosed = false;

                            mBooleanVisitStartDialog = false;
                            mBooleanVisitEndDialog = true;

                            wantToCloseDialog = false;
                            onSaveClose();
                            onRefreshVisitIcon();
                        }
                    }
                });

        alertDialogVisitEndRemarks.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        mStrVisitEndRemarks = etVisitEndRemarks.getText().toString();
                    }
                });

        final AlertDialog alertDialog = alertDialogVisitEndRemarks.create();
        alertDialog.show();*/


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                finish();
                onBackPressed();
                break;
        }
        return true;
    }

    /*
     Async task for Closing Visit End
    */
    private class ClosingVisit extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(CustomerDetailsActivity.this, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(mStrPopUpText);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(100);

                Hashtable table = new Hashtable();

                try {

                    if (!mStrOtherRetailerGuid.equalsIgnoreCase("")) {
                        mStrVisitId = ODataGuidDefaultImpl.initWithString36(mStrOtherRetailerGuid);
                    }
                    ODataEntity visitEntity;
                    visitEntity = OfflineManager.getVisitDetailsByKey(mStrVisitId);

                    if (visitEntity != null) {
                        oDataProperties = visitEntity.getProperties();
                        oDataProperty = oDataProperties.get(Constants.StartLat);
                        //noinspection unchecked
                        table.put(Constants.StartLat, oDataProperty.getValue());
                        oDataProperty = oDataProperties.get(Constants.StartLong);
                        //noinspection unchecked
                        table.put(Constants.StartLong, oDataProperty.getValue());
                        oDataProperty = oDataProperties.get(Constants.STARTDATE);
                        //noinspection unchecked
                        table.put(Constants.STARTDATE, oDataProperty.getValue());

                        oDataProperty = oDataProperties.get(Constants.STARTTIME);
                        //noinspection unchecked
                        table.put(Constants.STARTTIME, oDataProperty.getValue());

                        //noinspection unchecked
                        table.put(Constants.EndLat, BigDecimal.valueOf(UtilConstants.round(UtilConstants.latitude, 12)));
                        //noinspection unchecked
                        table.put(Constants.EndLong, BigDecimal.valueOf(UtilConstants.round(UtilConstants.longitude, 12)));
                        //noinspection unchecked
                        table.put(Constants.ENDDATE, UtilConstants.getNewDateTimeFormat());

                        //noinspection unchecked
                        oDataProperty = oDataProperties.get(Constants.CPNo);
                        table.put(Constants.CPNo, UtilConstants.removeLeadingZeros((String) (oDataProperty.getValue())));
                        //noinspection unchecked
                        table.put(Constants.VISITKEY, mStrVisitId.guidAsString36().toUpperCase());
                        //noinspection unchecked
                        table.put(Constants.Remarks, mStrVisitEndRemarks);

                        table.put(Constants.SPGUID, mStrSPGUID);

                        oDataProperty = oDataProperties.get(Constants.ROUTEPLANKEY);

                        //noinspection unchecked
                        if (oDataProperty.getValue() == null) {
                            table.put(Constants.ROUTEPLANKEY, "");
                        } else {
                            ODataGuid mRouteGuid = (ODataGuid) oDataProperty.getValue();

                            table.put(Constants.ROUTEPLANKEY, mRouteGuid.guidAsString36().toUpperCase());
                        }


                        oDataProperty = oDataProperties.get(Constants.StatusID);
                        table.put(Constants.StatusID, oDataProperty.getValue());

                        oDataProperty = oDataProperties.get(Constants.CPTypeID);
                        table.put(Constants.CPTypeID, oDataProperty.getValue());

                        oDataProperty = oDataProperties.get(Constants.VisitCatID);
                        table.put(Constants.VisitCatID, oDataProperty.getValue());

                      //  table.put(Constants.VisitDate, UtilConstants.getNewDateTimeFormat());
                        try {
                            oDataProperty = oDataProperties.get(Constants.VisitDate);
                            table.put(Constants.VisitDate, oDataProperty != null ? oDataProperty.getValue() : null);
                        } catch (Exception e) {
                            oDataProperty = null;
                            table.put(Constants.VisitDate, "");
                        }

                        oDataProperty = oDataProperties.get(Constants.VisitSeq);
                        table.put(Constants.VisitSeq, oDataProperty.getValue());

                        oDataProperty = oDataProperties.get(Constants.CPGUID);
                        table.put(Constants.CPGUID, oDataProperty.getValue());


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

                        table.put(Constants.ENDTIME, oDataDuration);

                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
                        String loginIdVal = sharedPreferences.getString("username", "");
                        //noinspection unchecked
                        table.put(Constants.LOGINID, loginIdVal);

                        table.put(Constants.SetResourcePath, Constants.Visits + "(guid'" + mStrVisitId.guidAsString36().toUpperCase() + "')");

                        if (visitEntity.getEtag() != null) {
                            table.put(Constants.Etag, visitEntity.getEtag());
                        } else {
                        }

                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                    ConstantsUtils.printErrorLog(e.getMessage());
                }
                try {
                    //noinspection unchecked
                    OfflineManager.updateVisit(table, CustomerDetailsActivity.this);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                    ConstantsUtils.printErrorLog(e.getMessage());
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
                ConstantsUtils.printErrorLog(e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }
}
