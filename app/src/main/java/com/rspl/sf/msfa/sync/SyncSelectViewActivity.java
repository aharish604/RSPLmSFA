package com.rspl.sf.msfa.sync;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.registration.RegistrationModel;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.rspl.sf.msfa.BuildConfig;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.SyncHistoryCallBack;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.notification.NotificationSetClass;
import com.rspl.sf.msfa.registration.Configuration;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * This class displays check box type sync selections.User select particular check boxes and
 * press sync button it navigates to Sync activity.
 */
@SuppressLint("NewApi")
public class SyncSelectViewActivity extends AppCompatActivity implements UIListener, View.OnClickListener {
    private boolean isClickable = false;
    ProgressDialog syncProgDialog;
    String concatCollectionStr = "";
    ArrayList<String> alAssignColl = new ArrayList<>();
    boolean backButtonPressed = false;
    private CheckBox ch_all, ch_sales_persons, chAttendeces, ch_outstanding,
            chAuth, ch_visits,
            ch_route_plan, ch_customers, ch_financial_postings,
            ch_ss_invoices, ch_value_helps, ch_ss_targets, ch_db_stock, ch_focused_prd,
            ch_merch_reivew, ch_comp_info, ch_credit_limit, ch_sales_order,ch_dealer_behaviour, ch_ro,ch_rtgs;
    private boolean dialogCancelled = false;
    private GUID refguid =null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Initialize action bar with back button(true)
        //ActionBarView.initActionBarView(this, true, getString(R.string.lbl_sync_sel));
        setContentView(R.layout.activity_sync_select_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_sync_sel), 0);
       SharedPreferences mSharedPrefs = getSharedPreferences(Constants.PREFS_NAME, 0);
        if (mSharedPrefs.getBoolean("writeDBGLog", false)) {
            Constants.writeDebug = mSharedPrefs.getBoolean("writeDBGLog", false);
        }
        onInitUI();
        setValuesToUI();


    }

    private void setValuesToUI() {
        ch_all.setOnClickListener(this);
        ch_all.setVisibility(View.VISIBLE);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        if (sharedPreferences.getString(Constants.isStartCloseEnabled, "").equalsIgnoreCase(Constants.isStartCloseTcode)) {
            chAttendeces.setVisibility(View.VISIBLE);
        } else {
            chAttendeces.setVisibility(View.GONE);
        }

        if (sharedPreferences.getString(Constants.isOutstandingEnabled, "").equalsIgnoreCase(Constants.isOutStandingTcode)) {
            ch_outstanding.setVisibility(View.VISIBLE);
        } else {
            ch_outstanding.setVisibility(View.GONE);
        }

        if (sharedPreferences.getString(Constants.isCustomerListEnabled, "").equalsIgnoreCase(Constants.isCustomerListTcode)) {
            ch_customers.setVisibility(View.VISIBLE);
        } else {
            ch_customers.setVisibility(View.GONE);
        }

        if (sharedPreferences.getString(Constants.isRouteEnabled, "").equalsIgnoreCase(Constants.isRoutePlaneTcode) || sharedPreferences.getString(Constants.isMTPEnabled, "").equalsIgnoreCase(Constants.isMTPTcode)) {
            ch_route_plan.setVisibility(View.VISIBLE);
        } else {
            ch_route_plan.setVisibility(View.GONE);
        }

        if (sharedPreferences.getString(Constants.isInvHistoryEnabled, "").equalsIgnoreCase(Constants.isInvoiceHistoryTcode)) {
            ch_ss_invoices.setVisibility(View.VISIBLE);
        } else {
            ch_ss_invoices.setVisibility(View.GONE);
        }

        if (sharedPreferences.getString(Constants.isSOListEnabled, "").equalsIgnoreCase(Constants.isSOListTcode)) {
            ch_sales_order.setVisibility(View.VISIBLE);
        } else {
            ch_sales_order.setVisibility(View.GONE);
        }
        if (sharedPreferences.getString(Constants.isDealerBehaviourEnabled, "").equalsIgnoreCase(Constants.isDealerBehaviourTcode)) {
            ch_dealer_behaviour.setVisibility(View.VISIBLE);
        } else {
            ch_dealer_behaviour.setVisibility(View.GONE);
        }
        if (sharedPreferences.getString(Constants.isROListKey, "").equalsIgnoreCase(Constants.isROLisTcode) || sharedPreferences.getString(Constants.isROListItemKey, "").equalsIgnoreCase(Constants.isROLisItemTcode)) {
            ch_ro.setVisibility(View.VISIBLE);
        } else {
            ch_ro.setVisibility(View.GONE);
        }

        if (sharedPreferences.getString(Constants.isRTGSEnabled, "").equalsIgnoreCase(Constants.isRTGSTcode) || sharedPreferences.getString(Constants.isRTGSSubOrdinateEnabled, "").equalsIgnoreCase(Constants.isRTGSSubOrdinateTcode)) {
            ch_rtgs.setVisibility(View.VISIBLE);
        } else {
            ch_rtgs.setVisibility(View.GONE);
        }
        chAuth.setVisibility(View.VISIBLE);
        ch_visits.setVisibility(View.VISIBLE);
        ch_financial_postings.setVisibility(View.GONE);
        ch_sales_persons.setVisibility(View.VISIBLE);
        ch_value_helps.setVisibility(View.VISIBLE);
//        ch_visits_act.setVisibility(View.VISIBLE);
        ch_credit_limit.setVisibility(View.VISIBLE);
        ch_focused_prd.setVisibility(View.GONE);
        ch_merch_reivew.setVisibility(View.GONE);
        ch_ss_targets.setVisibility(View.GONE);
        ch_comp_info.setVisibility(View.GONE);



        // Todo check current day cpstock items synced or not
        if (!Constants.isSpecificCollTodaySyncOrNot(Constants.getLastSyncDate(Constants.SYNC_TABLE, Constants.Collections,
                Constants.CPStockItems, Constants.TimeStamp, SyncSelectViewActivity.this))) {
            ch_db_stock.setVisibility(View.GONE);
        } else {
            ch_db_stock.setVisibility(View.GONE);
        }
    }

    /*
               * TODO This method initialize UI
               */
    private void onInitUI() {
        ch_all = (CheckBox) findViewById(R.id.ch_all);
        chAuth = (CheckBox) findViewById(R.id.ch_authorization);
        ch_sales_persons = (CheckBox) findViewById(R.id.ch_sales_persons);
        chAttendeces = (CheckBox) findViewById(R.id.ch_attendances_lists);
        ch_visits = (CheckBox) findViewById(R.id.ch_visits);
        ch_route_plan = (CheckBox) findViewById(R.id.ch_route_plan);
        ch_customers = (CheckBox) findViewById(R.id.ch_customers);
        ch_focused_prd = (CheckBox) findViewById(R.id.ch_focused_prd);
        ch_financial_postings = (CheckBox) findViewById(R.id.ch_financial_postings);
        ch_ss_invoices = (CheckBox) findViewById(R.id.ch_ss_invoices);
        ch_value_helps = (CheckBox) findViewById(R.id.ch_value_helps);
        ch_outstanding = (CheckBox) findViewById(R.id.ch_outstanding);
        ch_merch_reivew = (CheckBox) findViewById(R.id.ch_merch_reivew);
        ch_db_stock = (CheckBox) findViewById(R.id.ch_db_stock);
        ch_ss_targets = (CheckBox) findViewById(R.id.ch_ss_targets);
        ch_comp_info = (CheckBox) findViewById(R.id.ch_comp_info);
        ch_credit_limit = (CheckBox) findViewById(R.id.ch_credit_limit);
        ch_sales_order = (CheckBox) findViewById(R.id.ch_sales_order);
        ch_dealer_behaviour = (CheckBox) findViewById(R.id.ch_dealer_behaviour);
        ch_ro = (CheckBox) findViewById(R.id.ch_ro);
        ch_rtgs = (CheckBox) findViewById(R.id.ch_rtgs);
        backButtonPressed = false;

    }

    @Override
    public void onRequestError(int operation, Exception e) {
     isClickable = false;
     String strErrorMsg=e.toString();
        ErrorBean errorBean = Constants.getErrorCode(operation, e, SyncSelectViewActivity.this);
        if (errorBean.hasNoError()) {
            if (dialogCancelled == false && !Constants.isStoreClosed) {
                if (operation == Operation.OfflineRefresh.getValue()) {
                    if (strErrorMsg.contains("invalid authentication")) {
                        inValidPasswordDialog(strErrorMsg);
                    } else {
                        updatingSyncTime();
                        Constants.isSync = false;
                        if(refguid==null){
                            refguid = GUID.newRandom();
                        }
                        Constants.updateSyncTime(alAssignColl, this, Constants.DownLoad,refguid.toString().toUpperCase(), new SyncHistoryCallBack() {
                            @Override
                            public void displaySuccessMessage() {
                                closingProgressDialog();
                                syncCompletedWithErrorDialog();

                            }
                        });
                    }
                }else if (operation == Operation.GetStoreOpen.getValue()) {
                    closingProgressDialog();
                    Constants.isSync = false;
                    syncCompletedWithErrorDialog();
                }
            }
        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                closingProgressDialog();
                Constants.isSync = true;
                dialogCancelled = false;
                new AsyncSyncData().execute();
            } else {
                Constants.isSync = false;
                closingProgressDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), SyncSelectViewActivity.this);
            }
        } else {
            Constants.isSync = false;
            closingProgressDialog();
            Constants.displayMsgReqError(errorBean.getErrorCode(), SyncSelectViewActivity.this);
        }
    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {
       isClickable = false;
        if (dialogCancelled == false && !Constants.isStoreClosed) {
            if (operation == Operation.OfflineRefresh.getValue()) {
                try {
                    OfflineManager.getAuthorizations(getApplicationContext());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                Constants.setBirthdayListToDataValut(SyncSelectViewActivity.this);
                ConstantsUtils.startAutoSync(SyncSelectViewActivity.this,false);
                setAppointmentNotification();
                updatingSyncTime();
                Constants.isSync = false;
//                ConstantsUtils.serviceReSchedule(SyncSelectViewActivity.this, true);
                if(refguid==null){
                    refguid = GUID.newRandom();
                }
                Constants.updateSyncTime(alAssignColl, this, Constants.DownLoad,refguid.toString().toUpperCase(), new SyncHistoryCallBack() {
                    @Override
                    public void displaySuccessMessage() {
                        closingProgressDialog();
                        syncCompletedDialog();
                    }
                });
            } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
                Constants.isSync = false;
                try {
                    OfflineManager.getAuthorizations(getApplicationContext());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                if(refguid==null){
                    refguid = GUID.newRandom();
                }
                Constants.setSyncTime(SyncSelectViewActivity.this,refguid.toString().toUpperCase());
                ConstantsUtils.startAutoSync(SyncSelectViewActivity.this,false);
                closingProgressDialog();
//                ConstantsUtils.serviceReSchedule(SyncSelectViewActivity.this, true);
                syncCompletedDialog();
            }
        }
    }

    private void setAppointmentNotification() {
        new NotificationSetClass(this);

    }

    private void syncCompletedWithErrorDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    SyncSelectViewActivity.this, R.style.MyTheme);
            builder.setMessage(getString(R.string.msg_error_occured_during_sync))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    isClickable = false;
                                    onBackPressed();

                                }
                            });

            builder.show();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    private void inValidPasswordDialog(String mErrTxt) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.custom_dialog_scroll, null);

        TextView textview = (TextView) view.findViewById(R.id.tv_err_msg);
        final TextView tvdetailmsg = (TextView) view.findViewById(R.id.tv_detail_msg);

        String temp_errMsg = mErrTxt;
        temp_errMsg = Constants.makecustomHttpErrormessage(temp_errMsg);
        if (!TextUtils.isEmpty(temp_errMsg) && temp_errMsg.equalsIgnoreCase(mErrTxt)) {
            if (mErrTxt.contains("invalid authentication")) {
                textview.setText(Constants.PasswordExpiredMsg);
                tvdetailmsg.setText(mErrTxt);
            } else if (mErrTxt.contains("HTTP Status 401 ? Unauthorized")) {
                textview.setText(Constants.PasswordExpiredMsg);
                tvdetailmsg.setText(mErrTxt);
            }
        }

        else {
            textview.setText("\n" + temp_errMsg);
        }

        final AlertDialog dialog = new AlertDialog.Builder(SyncSelectViewActivity.this)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNeutralButton("Details", null)
                .setNegativeButton("Settings", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button b = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                       onBackPressed();
                    }
                });

                Button mesg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                mesg.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something

                        tvdetailmsg.setVisibility(View.VISIBLE);
                        // dialog.dismiss();
                    }
                });

                Button change = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                change.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        RegistrationModel<Serializable> registrationModel = new RegistrationModel<>();
                        Intent intent = new Intent(SyncSelectViewActivity.this, com.arteriatech.mutils.support.SecuritySettingActivity.class);
                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
                        String userName = sharedPreferences.getString("username","");
                        registrationModel.setExtenndPwdReq(true);
                        registrationModel.setUpdateAsPortalPwdReq(true);
                        registrationModel.setIDPURL(Configuration.IDPURL);
                        registrationModel.setUserName(userName);
                        registrationModel.setExternalTUserName(Configuration.IDPTUSRNAME);
                        registrationModel.setExternalTPWD(Configuration.IDPTUSRPWD);
                        intent.putExtra(UtilConstants.RegIntentKey, registrationModel);
                        //context.startActivityForResult(intent, 350);
                        startActivity(intent);
                        // dialog.dismiss();
                    }
                });

            }
        });
        dialog.show();

    }

    private void syncCompletedDialog() {
        UtilConstants.dialogBoxWithCallBack(SyncSelectViewActivity.this, "", getString(R.string.msg_sync_successfully_completed), getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {

                if (!AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, SyncSelectViewActivity.this, BuildConfig.APPLICATION_ID, true)){
                    isClickable = false;
                    onBackPressed();
                }
            }
        });
    }

    private void closingProgressDialog() {
        try {
            isClickable = false;
            syncProgDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    ToDo Update Last Sync time into DB table
	 */
    private void updatingSyncTime() {
        try {
            /*String syncTime = Constants.getSyncHistoryddmmyyyyTime();
            for (int incReq = 0; incReq < alAssignColl.size(); incReq++) {
                String colName = alAssignColl.get(incReq);
                if (colName.contains("?$")) {
                    String splitCollName[] = colName.split("\\?");
                    colName = splitCollName[0];
                }
                Constants.events.updateStatus(Constants.SYNC_TABLE,
                        colName, Constants.timeStamp, syncTime
                );
            }*/
          //  Constants.updateSyncTime(alAssignColl,this,Constants.DownLoad,refguid.toString().toUpperCase());
        } catch (Exception exce) {
            LogManager.writeLogError(Constants.sync_table_history_txt + exce.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sync_back, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sync:
                if (UtilConstants.isNetworkAvailable(this)) {
                    if (Constants.isPullDownSync||Constants.iSAutoSync||Constants.isBackGroundSync) {
                        if (Constants.iSAutoSync){
                            showAlert(getString(R.string.alert_auto_sync_is_progress));
                        }else{
                            showAlert(getString(R.string.alert_backgrounf_sync_is_progress));
                        }
                    }else{
                        if(!isClickable) {
                            isClickable = true;
                            onSync();
                        }
                    }
                } else {
                    showAlert(getString(R.string.data_conn_lost_during_sync));
                }
                break;
            case R.id.menu_back:
                backButtonPressed = true;
                onBackPressed();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private void onSync() {
        if (!chAuth.isChecked() && !chAttendeces.isChecked() && !ch_sales_persons.isChecked()
                && !ch_visits.isChecked() && !ch_route_plan.isChecked()
                && !ch_customers.isChecked() && !ch_financial_postings.isChecked() && !ch_ss_invoices.isChecked()
                && !ch_value_helps.isChecked() && !ch_ss_targets.isChecked()
                && !ch_outstanding.isChecked() && !ch_db_stock.isChecked()
                && !ch_focused_prd.isChecked() && !ch_merch_reivew.isChecked()
                && !ch_comp_info.isChecked() && !ch_credit_limit.isChecked()
                && !ch_sales_order.isChecked() && !ch_dealer_behaviour.isChecked()
                && !ch_ro.isChecked() && !ch_rtgs.isChecked()) {
            UtilConstants.showAlert(getString(R.string.plz_select_one_coll), SyncSelectViewActivity.this);
            isClickable = false;
        } else {
            if (Constants.iSAutoSync) {
                ConstantsUtils.showAlert(getString(R.string.alert_auto_sync_is_progress), SyncSelectViewActivity.this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isClickable = false;
                        dialog.cancel();
                    }
                });
            }else {
                try {
                    Constants.Entity_Set.clear();
                    Constants.AL_ERROR_MSG.clear();
                    Constants.isSync = true;
                    dialogCancelled = false;
                    new AsyncSyncData().execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void assignCollToArrayList() {
        Constants.isStoreClosed = false;
        alAssignColl.clear();
        concatCollectionStr = "";

        if (chAuth.isChecked()) {
            LogManager.writeLogDebug("Download Sync Starts: "+Constants.UserProfileAuthSet);
            alAssignColl.add(Constants.UserProfileAuthSet);
        }
        if (ch_sales_persons.isChecked()) {
            LogManager.writeLogDebug("Download Sync Starts: "+Constants.SalesPersons);
            alAssignColl.add(Constants.SalesPersons);
        }
        if (chAttendeces.isChecked()) {
            LogManager.writeLogDebug("Download Sync Starts: "+Constants.Attendances);
            alAssignColl.add(Constants.Attendances);
        }

        if (ch_visits.isChecked()) {
            alAssignColl.add(Constants.Visits);
            alAssignColl.add(Constants.VisitActivities);
            LogManager.writeLogDebug("Download Sync Starts: "+Constants.Visits+","+Constants.VisitActivities);
        }


        if (ch_route_plan.isChecked()) {
            alAssignColl.add(Constants.RoutePlans);
            alAssignColl.add(Constants.RouteSchedulePlans);
            alAssignColl.add(Constants.RouteSchedules);

            LogManager.writeLogDebug("Download Sync Starts: "+Constants.RoutePlans+","+Constants.RouteSchedulePlans+","+Constants.RouteSchedules);
        }


        if (ch_customers.isChecked()) {
            alAssignColl.add(Constants.Customers);
            alAssignColl.add(Constants.UserSalesPersons);
            LogManager.writeLogDebug("Download Sync Starts: "+Constants.Customers+","+Constants.UserSalesPersons);
//            alAssignColl.add(Constants.CPDMSDivisions);
        }

        if (ch_ss_invoices.isChecked()) {
            alAssignColl.add(Constants.InvoiceItemDetails);
            alAssignColl.add(Constants.INVOICES);
            alAssignColl.add(Constants.InvoiceConditions);
            alAssignColl.add(Constants.InvoicePartnerFunctions);
            LogManager.writeLogDebug("Download Sync Starts: "+Constants.InvoiceItemDetails+","+Constants.INVOICES+","+Constants.InvoiceConditions+","+Constants.InvoicePartnerFunctions);
        }

        if (ch_financial_postings.isChecked()) {
            alAssignColl.add(Constants.FinancialPostingItemDetails);
            alAssignColl.add(Constants.FinancialPostings);
            LogManager.writeLogDebug("Download Sync Starts: "+Constants.FinancialPostingItemDetails+","+Constants.FinancialPostings);
        }

        if (ch_value_helps.isChecked()) {
            alAssignColl.add(Constants.ValueHelps);
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            alAssignColl.add(Constants.ConfigTypesetTypes);
            LogManager.writeLogDebug("Download Sync Starts: "+Constants.ValueHelps);
        }

        if (ch_outstanding.isChecked()) {
            alAssignColl.add(Constants.OutstandingInvoices);
            alAssignColl.add(Constants.OutstandingInvoiceItemDetails);
        }
        if (ch_focused_prd.isChecked()) {
            alAssignColl.add(Constants.SegmentedMaterials);
        }
        if (ch_merch_reivew.isChecked()) {
            alAssignColl.add(Constants.MerchReviews);
            alAssignColl.add(Constants.MerchReviewImages);
        }

        if (ch_db_stock.isChecked()) {
            alAssignColl.add(Constants.CPStockItems);
        }

        if (ch_ss_targets.isChecked()) {
            alAssignColl.add(Constants.KPISet);
            alAssignColl.add(Constants.Targets);
            alAssignColl.add(Constants.TargetItems);
            alAssignColl.add(Constants.KPIItems);
            LogManager.writeLogDebug("Download Sync Starts: "+Constants.KPISet+","+Constants.Targets+","+Constants.TargetItems+","+Constants.KPIItems);
        }

        if (ch_comp_info.isChecked()) {
            alAssignColl.add(Constants.CompetitorInfos);
            alAssignColl.add(Constants.CompetitorMasters);
        }


        if (ch_dealer_behaviour.isChecked()) {
            alAssignColl.add(Constants.SPChannelEvaluationList);
        }


        if (ch_ro.isChecked()) {
            alAssignColl.add(Constants.ReturnOrders);
            alAssignColl.add(Constants.ReturnOrderItems);
            alAssignColl.add(Constants.ReturnOrderItemDetails);
        }
        if (ch_rtgs.isChecked()) {
            alAssignColl.add(Constants.CollectionPlan);
            alAssignColl.add(Constants.CollectionPlanItem);
            alAssignColl.add(Constants.CollectionPlanItemDetails);
            LogManager.writeLogDebug("Download Sync Starts: "+Constants.CollectionPlan+","+Constants.CollectionPlanItem+","+Constants.CollectionPlanItemDetails);
        }

        if (ch_sales_order.isChecked()) {
            alAssignColl.add(Constants.SOs);
            alAssignColl.add(Constants.SOItemDetails);
            alAssignColl.add(Constants.SOTexts);
            alAssignColl.add(Constants.SOItems);
            alAssignColl.add(Constants.SOConditions);
            alAssignColl.add(Constants.MaterialByCustomers);

            LogManager.writeLogDebug("Download Sync Starts: "+Constants.SOs+","+Constants.SOItemDetails+","+Constants.SOTexts);
        }
        if (ch_credit_limit.isChecked()) {
            alAssignColl.add(Constants.CustomerCreditLimits);
            LogManager.writeLogDebug("Download Sync Starts: "+Constants.CustomerCreditLimits);
        }
        if (!alAssignColl.contains(Constants.ConfigTypsetTypeValues))
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
        LogManager.writeLogDebug("Download Sync Starts: "+Constants.ConfigTypsetTypeValues);

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ch_all:
                if (ch_all.isChecked()) {
                    checkAll();
                } else {
                    unCheckAll();
                }
                break;
            case R.id.tv_back:
                onBackPressed();
                break;
            case R.id.tv_submit:
                if (!isClickable) {
                    isClickable = true;
                    onSync();
                }
                break;

        }

    }

    private void checkAll() {
        if (ch_sales_persons.getVisibility() == View.VISIBLE)
            ch_sales_persons.setChecked(true);
        if (chAttendeces.getVisibility() == View.VISIBLE)
            chAttendeces.setChecked(true);
        if (ch_outstanding.getVisibility() == View.VISIBLE)
            ch_outstanding.setChecked(true);
        if (chAuth.getVisibility() == View.VISIBLE)
            chAuth.setChecked(true);
        if (ch_visits.getVisibility() == View.VISIBLE)
            ch_visits.setChecked(true);
        if (ch_route_plan.getVisibility() == View.VISIBLE)
            ch_route_plan.setChecked(true);
        if (ch_customers.getVisibility() == View.VISIBLE)
            ch_customers.setChecked(true);
        if (ch_financial_postings.getVisibility() == View.VISIBLE)
            ch_financial_postings.setChecked(true);
        if (ch_ss_invoices.getVisibility() == View.VISIBLE)
            ch_ss_invoices.setChecked(true);
        if (ch_value_helps.getVisibility() == View.VISIBLE)
            ch_value_helps.setChecked(true);
       /* if (ch_visits_act.getVisibility() == View.VISIBLE)
            ch_visits_act.setChecked(true);*/
        if (ch_focused_prd.getVisibility() == View.VISIBLE)
            ch_focused_prd.setChecked(true);
        if (ch_merch_reivew.getVisibility() == View.VISIBLE)
            ch_merch_reivew.setChecked(true);
        if (ch_db_stock.getVisibility() == View.VISIBLE)
            ch_db_stock.setChecked(true);
        if (ch_ss_targets.getVisibility() == View.VISIBLE)
            ch_ss_targets.setChecked(true);
        if (ch_comp_info.getVisibility() == View.VISIBLE)
            ch_comp_info.setChecked(true);
        if (ch_credit_limit.getVisibility() == View.VISIBLE)
            ch_credit_limit.setChecked(true);
        if (ch_sales_order.getVisibility() == View.VISIBLE)
            ch_sales_order.setChecked(true);
        if (ch_dealer_behaviour.getVisibility() == View.VISIBLE)
            ch_dealer_behaviour.setChecked(true);
        if (ch_ro.getVisibility() == View.VISIBLE)
            ch_ro.setChecked(true);
        if (ch_rtgs.getVisibility() == View.VISIBLE)
            ch_rtgs.setChecked(true);

    }

    private void unCheckAll() {
        if (ch_sales_persons.getVisibility() == View.VISIBLE)
            ch_sales_persons.setChecked(false);
        if (chAttendeces.getVisibility() == View.VISIBLE)
            chAttendeces.setChecked(false);
        if (ch_outstanding.getVisibility() == View.VISIBLE)
            ch_outstanding.setChecked(false);
        if (chAuth.getVisibility() == View.VISIBLE)
            chAuth.setChecked(false);
        if (ch_visits.getVisibility() == View.VISIBLE)
            ch_visits.setChecked(false);
        if (ch_route_plan.getVisibility() == View.VISIBLE)
            ch_route_plan.setChecked(false);
        if (ch_customers.getVisibility() == View.VISIBLE)
            ch_customers.setChecked(false);
        if (ch_financial_postings.getVisibility() == View.VISIBLE)
            ch_financial_postings.setChecked(false);
        if (ch_ss_invoices.getVisibility() == View.VISIBLE)
            ch_ss_invoices.setChecked(false);
        if (ch_value_helps.getVisibility() == View.VISIBLE)
            ch_value_helps.setChecked(false);
       /* if (ch_visits_act.getVisibility() == View.VISIBLE)
            ch_visits_act.setChecked(false);*/
        if (ch_focused_prd.getVisibility() == View.VISIBLE)
            ch_focused_prd.setChecked(false);
        if (ch_merch_reivew.getVisibility() == View.VISIBLE)
            ch_merch_reivew.setChecked(false);
        if (ch_db_stock.getVisibility() == View.VISIBLE)
            ch_db_stock.setChecked(false);
        if (ch_ss_targets.getVisibility() == View.VISIBLE)
            ch_ss_targets.setChecked(false);
        if (ch_comp_info.getVisibility() == View.VISIBLE)
            ch_comp_info.setChecked(false);
        if (ch_credit_limit.getVisibility() == View.VISIBLE)
            ch_credit_limit.setChecked(false);
        if (ch_sales_order.getVisibility() == View.VISIBLE)
            ch_sales_order.setChecked(false);
        if (ch_dealer_behaviour.getVisibility() == View.VISIBLE)
            ch_dealer_behaviour.setChecked(false);
        if (ch_ro.getVisibility() == View.VISIBLE)
            ch_ro.setChecked(false);
        if (ch_rtgs.getVisibility() == View.VISIBLE)
            ch_rtgs.setChecked(false);
    }

    public class AsyncSyncData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(SyncSelectViewActivity.this, R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
            syncProgDialog.setCancelable(true);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();

            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    SyncSelectViewActivity.this, R.style.MyTheme);
                            builder.setMessage(R.string.do_want_cancel_sync)
                                    .setCancelable(false)
                                    .setPositiveButton(
                                            R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {
                                                    dialogCancelled = true;
                                                    isClickable = false;
                                                    if(refguid==null){
                                                        refguid = GUID.newRandom();
                                                    }
                                                    Constants.updateStartSyncTime(SyncSelectViewActivity.this,Constants.download_cancel_sync,Constants.EndSync,refguid.toString().toUpperCase());
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
            assignCollToArrayList();
            if (!OfflineManager.isOfflineStoreOpen()) {
                try {
                    if (!OfflineManager.openOfflineStore(SyncSelectViewActivity.this, SyncSelectViewActivity.this))
                        closingProgressDialog();
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
            } else {
                try {
                    refguid = GUID.newRandom();
                    Constants.updateStartSyncTime(SyncSelectViewActivity.this, Constants.DownLoad, Constants.StartSync,refguid.toString().toUpperCase());
                    OfflineManager.refreshStoreSync(getApplicationContext(), SyncSelectViewActivity.this, Constants.Fresh, concatCollectionStr);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private void showAlert(String message){
        ConstantsUtils.showAlert(message, this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isClickable=false;
                dialog.cancel();
            }
        });
    }
}
