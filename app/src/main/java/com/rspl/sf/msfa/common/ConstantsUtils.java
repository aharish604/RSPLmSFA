package com.rspl.sf.msfa.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.common.UtilOfflineManager;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.download.DownloadFileAsyncTask;
import com.arteriatech.mutils.interfaces.AsyncTaskCallBackInterface;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.registration.UtilRegistrationActivity;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.rspl.sf.msfa.BirthdayAlertsActivity;
import com.rspl.sf.msfa.BuildConfig;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.attendance.attendancesummary.AttendanceSummaryActivity;
import com.rspl.sf.msfa.autosync.AutoSyncDataAlarmReceiver;
import com.rspl.sf.msfa.autosync.AutoSyncDataLocationAlarmReceiver;
import com.rspl.sf.msfa.backgroundlocationtracker.TrackerService;
import com.rspl.sf.msfa.claimreports.ClaimReportActivity;
import com.rspl.sf.msfa.collectionPlan.RTGSActivity;
import com.rspl.sf.msfa.dbstock.DBStockActivity;
import com.rspl.sf.msfa.expense.ExpenseEntryActivity;
import com.rspl.sf.msfa.expense.ExpenseListPage;
import com.rspl.sf.msfa.interfaces.CustomDialogCallBack;
import com.rspl.sf.msfa.interfaces.DialogCallBack;
import com.rspl.sf.msfa.main.ResetPassword;
import com.rspl.sf.msfa.mbo.CustomerBean;
import com.rspl.sf.msfa.mtp.MTPActivity;
import com.rspl.sf.msfa.mtp.MTPRoutePlanBean;
import com.rspl.sf.msfa.mtp.approval.MTPApprovalActivity;
import com.rspl.sf.msfa.mtp.subordinate.SalesPersonsViewActivity;
import com.rspl.sf.msfa.reports.OutstandingAgeReport;
import com.rspl.sf.msfa.reports.behaviourlist.BehaviourListActivity;
import com.rspl.sf.msfa.reports.targets.TargetsActivity;
import com.rspl.sf.msfa.routeplan.RoutePlanListActivity;
import com.rspl.sf.msfa.routeplan.customerslist.CustomerListActivity;
import com.rspl.sf.msfa.soapproval.SOApproveActivity;
import com.rspl.sf.msfa.stock.DepotStockActivity;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.store.OnlineManager;
import com.rspl.sf.msfa.sync.SyncHist;
import com.rspl.sf.msfa.sync.SyncSelectionActivity;
import com.rspl.sf.msfa.sync.UpdatePendingLatLongRequest;
import com.rspl.sf.msfa.ui.FlowLayout;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.ODataPayload;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.store.ODataRequestExecution;
import com.sap.smp.client.odata.store.ODataResponseSingle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by e10769 on 27-04-2017.
 */

public class ConstantsUtils {

    public static final int ITEM_MAX_LENGTH = 6;
    public static final int ITEM_MAX_LENGTH_3 = 3;
    public static final String EXTRA_ARRAY_LIST = "arrayList";
    public static final String MAXCLMDOC = "MAXCLMDOC";
    public static final String MAXREGDOC = "MAXREGDOC";
    public static final String ZDMS_SCCLM = "ZDMS_SCCLM";
    public static final String EXTRA_FROM = "comingFrom";
    public static final String DISC_PERCENTAGE = "Disc %";
    public static final String FREE_QTY = "Free Qty";
    public static final String TargetBasedID = "TargetBasedID";
    public static final String Training = "Training";
    public static final String Meeting = "Meeting";
    public static final String B = "B";
    public static final String C = "C";
    public static final int ACTIVITY_RESULT_FILTER = 850;
    public static final int ACTIVITY_RESULT_MATERIAL = 750;
    public static final int ADD_MATERIAL = 30;
    public static final int SO_CREATE_SINGLE_MATERIAL = 31;
    public static final int SO_EDIT_SINGLE_MATERIAL = 32;
    public static final int SO_SINGLE_MATERIAL = 33;
    public static final int SO_VIEW_SELECTED_MATERIAL = 34;
    public static final int SO_MULTIPLE_MATERIAL = 4;
    public static final int SO_CREATE_ACTIVITY = 1;
    public static final int SO_CREATE_CC_ACTIVITY = 2;
    public static final int SO_EDIT_ACTIVITY = 3;
    public static final int SO_APPROVAL_EDIT_ACTIVITY = 36;
    /*session type*/
    public static final int NO_SESSION = 0;// session passing only  app header
    public static final int SESSION_HEADER = 1;// session passing only  app header
    public static final int SESSION_QRY = 2;// session passing only qry
    public static final int SESSION_QRY_HEADER = 3;// session passing both app header and qry
    public static final String BannerDesc = "BannerDesc";
    public static final String ProductCatDesc = "ProductCatDesc";
    public static final String DISC_AMOUNT = "Disc Amount";
    public static final String ProductCatID = "ProductCatID";
    public static final String ROUTE_INSTANCE_ID = "routeInstanceId";
    public static final String ROUTE_ENTITY_KEY = "routeEntityKey";
    public static final int SWIPE_REFRESH_DISTANCE = 300;
    public static final int SWIPE_REFRESH_DISABLE = 999999;
    public static final int DATE_SETTINGS_REQUEST_CODE = 998;
    public static final String EXTRA_COMING_FROM = "comingFrom";
    public static final String MONTH_CURRENT = "CurrentMonth";
    public static final String MONTH_NEXT = "NextMonth";
    public static final String MTP_SUBORDINATE = "mtpSubOrdinate";
    public static final String MTP_SUBORDINATE_CURRENT = "mtpSubOrdinateCurrent";
    public static final String MTP_SUBORDINATE_NEXT = "mtpSubOrdinateNext";
    public static final String RTGS_SUBORDINATE = "RTGSSubOrdinate";
    public static final String ATTND_SUMMARY = "ATTNDSUMMARY";
    public static final String CLAIM_SUMMARY = "CLAIMSUMMARY";
    public static final String RTGS_SUBORDINATE_CURRENT = "RTGSSubOrdinateCurrent";
    public static final String RTGS_SUBORDINATE_NEXT = "RTGSSubOrdinateNext";
    public static final String MTP_APPROVAL = "mtpApproval";
    public static final String MONTH_TODAY = "Today";
    public static final String EXTRA_DATE = "extraDate";
    public static final String EXTRA_SPGUID = "extraSPGUID";
    public static final String EXTRA_ExternalRefID = "extraExternalRefID";
    public static final String EXTRA_ISASM_LOGIN = "extraIsASMLogin";
    public static final String EXTRA_POS = "extraPOS";
    private static final String MC = "MC";
    private static final String DAYEND = "DAYEND";
    public static String ApprovalStatusID = "ApprovalStatusID";
    public static String APPROVALERRORMSG = "";
    public static int SO_RESULT_CODE = 2300;
    public static Toast toast = null;
    public static String messageToToast = "";
    public static PendingIntent alarmPendingIntent;


    public static void dialogBoxWithButton(Context context, String title, String message, String positiveButton, String negativeButton, final DialogCallBack dialogCallBack) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyTheme);
            if (!title.equalsIgnoreCase("")) {
                builder.setTitle(title);
            }
            builder.setMessage(message).setCancelable(false).setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    if (dialogCallBack != null)
                        dialogCallBack.clickedStatus(true);
                }
            });
            if (!negativeButton.equalsIgnoreCase("")) {
                builder.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (dialogCallBack != null)
                            dialogCallBack.clickedStatus(false);
                    }
                });
            }
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void setProgressColor(Context mContext, SwipeRefreshLayout swipeRefresh) {
        swipeRefresh.setDistanceToTriggerSync(ConstantsUtils.SWIPE_REFRESH_DISTANCE);
        swipeRefresh.setColorSchemeColors(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
    }

    public static String getLastSeenDateFormat(Context context, long smsTimeInMilis) {
        return UtilConstants.getLastSeenDateFormat(context,smsTimeInMilis);
    }

    public static ProgressDialog showProgressDialog(Context mContext) {
        ProgressDialog pdLoadDialog = null;
        try {
            pdLoadDialog = new ProgressDialog(mContext, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(mContext.getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pdLoadDialog;
    }

    public static void onlineRequest(final Context mContext, final String query, boolean isSessionRequired, int requestId, int sessionType, final OnlineODataInterface onlineODataInterface, final boolean isReqOnline) {
        final Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_RESOURCE_PATH, query);
        bundle.putBoolean(Constants.BUNDLE_SESSION_REQUIRED, isSessionRequired);
        bundle.putInt(Constants.BUNDLE_REQUEST_CODE, requestId);
        bundle.putInt(Constants.BUNDLE_SESSION_TYPE, sessionType);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isReqOnline) {
                    if(query.contains("MaterialByCustomers")){
                        if(Constants.writeDebug)
                        LogManager.writeLogDebug("SO Create: Requesting Materials Online");
                    }
                    OnlineManager.requestQuery(onlineODataInterface, bundle, mContext);
                } else {
                    if(query.contains("MaterialByCustomers")){
                        if(Constants.writeDebug)
                        LogManager.writeLogDebug("SO Create: Requesting Materials Offline");
                    }
                    OfflineManager.requestQueryOffline(onlineODataInterface, bundle, mContext);
                }
            }
        }).start();
    }

    public static void onlineRequest(final Context mContext, String query, boolean isSessionRequired, int requestId, int sessionType, final OnlineODataInterface onlineODataInterface, boolean readFromTechnicalCache, final boolean isReqOnline) {
        final Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_RESOURCE_PATH, query);
        bundle.putBoolean(Constants.BUNDLE_SESSION_REQUIRED, isSessionRequired);
        bundle.putInt(Constants.BUNDLE_REQUEST_CODE, requestId);
        bundle.putInt(Constants.BUNDLE_SESSION_TYPE, sessionType);
        bundle.putBoolean(UtilConstants.BUNDLE_READ_FROM_TECHNICAL_CACHE, readFromTechnicalCache);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isReqOnline) {
                    OnlineManager.requestQuery(onlineODataInterface, bundle, mContext);
                } else {
                    OfflineManager.requestQueryOffline(onlineODataInterface, bundle, mContext);
                }
            }
        }).start();
    }

    public static ProgressDialog showProgressDialog(Context mContext, String message) {
        ProgressDialog pdLoadDialog = null;
        try {
            pdLoadDialog = new ProgressDialog(mContext, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(message);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pdLoadDialog;
    }

    /*create star programaticaly*/
    public static void setStarMandatory(TextView tvView) {
        String viewText = tvView.getText().toString();
        String colored = " *";
        SpannableStringBuilder builder = new SpannableStringBuilder();

        builder.append(viewText);
        int start = builder.length();
        builder.append(colored);
        int end = builder.length();

        builder.setSpan(new ForegroundColorSpan(Color.RED), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvView.setText(builder);
    }

    /*actionbar center image*/
    public static void initActionBarView(final AppCompatActivity mActivity, Toolbar toolbar, boolean homeUpEnabled) {
        mActivity.setSupportActionBar(toolbar);
        if (homeUpEnabled) {
            final Drawable upArrow = mActivity.getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(mActivity.getResources().getColor(R.color.GREY), PorterDuff.Mode.SRC_ATOP);
            mActivity.getSupportActionBar().setHomeAsUpIndicator(upArrow);

            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
//        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
//        mActivity.getSupportActionBar().setIcon(R.drawable.ic_msfa);
        mActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        mActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(mActivity.getResources().getColor(R.color.WHITE)));
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.actionbar_center_img_lay, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mActivity.getSupportActionBar().setDisplayShowCustomEnabled(true);
        mActivity.getSupportActionBar().setCustomView(view, params);
    }

    /*actionbar center image*/
    public static void initActionBarView(AppCompatActivity mActivity, Toolbar toolbar, boolean homeUpEnabled, String title, int appIcon) {
        com.arteriatech.mutils.actionbar.ActionBarView.initActionBarView(mActivity, toolbar, homeUpEnabled, title, appIcon, 0);
    }

    public static String addZeroBeforeValue(int values, int minLenght) {
        String finalValues = "";
        try {
            if (values == 0) {
                values = values + 1;
            }
            String stringValues = values + "";
            int currentLength = stringValues.length();
            String typeValues = "10";/*OfflineManager.getValueByColumnName(Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +Constants.SS + "' and " + Constants.Types + " eq '" + Constants.SMINVITMNO + "' &$top=1", Constants.TypeValue);*/
            if (typeValues.equalsIgnoreCase("10")) {
                finalValues = values + "0";
            } else if ((typeValues.equalsIgnoreCase("1")) && !getNoItemZero()) {
                if (minLenght == 6) {
                    if (currentLength == 1) {
                        finalValues = "00000" + values;
                    } else if (currentLength == 2) {
                        finalValues = "0000" + values;
                    } else if (currentLength == 3) {
                        finalValues = "000" + values;
                    } else if (currentLength == 4) {
                        finalValues = "00" + values;
                    } else if (currentLength == 5) {
                        finalValues = "0" + values;
                    } else if (currentLength == 6) {
                        finalValues = "" + values;
                    } else {
                        finalValues = values + "";
                    }
                }
                if (minLenght == 3) {
                    if (currentLength == 1) {
                        finalValues = "000" + values;
                    } else if (currentLength == 2) {
                        finalValues = "00" + values;
                    } else if (currentLength == 3) {
                        finalValues = "0" + values;
                    } else if (currentLength == 4) {
                        finalValues = "" + values;
                    } else {
                        finalValues = values + "";
                    }
                } else {
                    finalValues = values + "";
                }
            } else {
                finalValues = values + "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalValues;
    }

    public static boolean getNoItemZero() {
         /*String query = Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +
               Constants.SS + "' and " + Constants.Types + " eq '" + Constants.NOITMZEROS + "' &$top=1";
        try {
            String typeValues = OfflineManager.getValueByColumnName(query, Constants.TypeValue);
            if (typeValues.equalsIgnoreCase(Constants.X)) {
                return true;
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }*/
        return false;
    }

    public static ArrayList<Object> getPendingCollList(Context mContext) {
        ArrayList<Object> objectsArrayList = new ArrayList<>();
        ArrayList<String> syncItemList = new ArrayList<>();
        int mIntPendingCollVal = 0;
        String[][] invKeyValues = null;
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        set = sharedPreferences.getStringSet(Constants.SOs, null);
        invKeyValues = new String[getPendingListSize(mContext)][2];
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.SOs;
                mIntPendingCollVal++;
            }
            syncItemList.add(Constants.SOs);
            syncItemList.add(Constants.SOItemDetails);
            syncItemList.add(Constants.SOTexts);
            syncItemList.add(Constants.SOConditions);
        }
       /* set = sharedPreferences.getStringSet(Constants.SOList, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.SOList;
                mIntPendingCollVal++;
            }
        }*/

        if (mIntPendingCollVal > 0) {
            Arrays.sort(invKeyValues, new ArrayComarator());
            objectsArrayList.add(mIntPendingCollVal);
            objectsArrayList.add(invKeyValues);
            objectsArrayList.add(syncItemList);
        }

        return objectsArrayList;

    }

    /*Gets list of collection to refresh*/
    public static ArrayList<String> getRefreshList() {
        ArrayList<String> alAssignColl = new ArrayList<>();
      /*  try {
            if (OfflineManager.getVisitStatusForCustomer(Constants.ChannelPartners + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.ChannelPartners);
            }

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError("Error : " + e.getMessage());
        }*/
        return alAssignColl;
    }

    private static int getPendingListSize(Context mContext) {
        int size = 0;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);

        Set<String> set = new HashSet<>();

        set = sharedPreferences.getStringSet(Constants.SOs, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }

        return size;
    }

    public static HashSet<String> getAllSyncValue(Context mContext) {
        String concatCollectionStr = "";
        HashSet<String> alAssignColl = new HashSet<>();
//        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);

       /* String sharedVal = sharedPreferences.getString(Constants.isComplintsListKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isComplintsListTcode)) {
            alAssignColl.add(Constants.Complaints);
        }*/
       /* alAssignColl.add(Constants.Customers);
        alAssignColl.add(Constants.SOs);
        alAssignColl.add(Constants.SOItemDetails);
        alAssignColl.add(Constants.SOItems);
        alAssignColl.add(Constants.SOItemSchedules);
        alAssignColl.add(Constants.SOConditions);
        alAssignColl.add(Constants.SOConditionItemDetails);
        alAssignColl.add(Constants.MaterialPlants);
        alAssignColl.add(Constants.Invoices);
        alAssignColl.add(Constants.InvoiceItemDetails);
        alAssignColl.add(Constants.Materials);
        alAssignColl.add(Constants.ConfigTypesetTypes);
        alAssignColl.add(Constants.ValueHelps);*/
        String[] DEFINGREQARRAY = Constants.getDefinigReq(mContext);
        for (String collectionName : DEFINGREQARRAY) {
            if (collectionName.contains("?")) {
                String splitCollName[] = collectionName.split("\\?");
                collectionName = splitCollName[0];
            }
            alAssignColl.add(collectionName);
        }
        return alAssignColl;
    }

    /**
     * split for so
     *
     * @param description
     * @param pos         start from 0
     * @return
     */
    public static String getPerticularName(String description, int pos) {
        String outPut = "";
        try {
            String[] splitedData = description.split("/");
            outPut = splitedData[pos];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outPut;
    }

    public static String convertDateIntoDeviceFormat(String dateString) {
        String stringDateReturns = "";
        Date date = null;
        try {
            date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).parse(dateString);
            stringDateReturns = (new SimpleDateFormat("dd-MMM-yyyy")).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return stringDateReturns;
    }

    public static String convertDateIntoDisplayFormat(String dateString) {
        String stringDateReturns = "";
        Date date = null;
        try {
            date = (new SimpleDateFormat("dd/MM/yyyy")).parse(dateString);
            stringDateReturns = (new SimpleDateFormat("dd-MMM-yyyy")).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return stringDateReturns;
    }

    public static void openImageInDialogBox(Context context, byte[] imageByteArray) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.img_expand);
        // set the custom dialog components - text, image and
        // button
        ImageView image = (ImageView) dialog.findViewById(R.id.imageView1);

        image.setImageBitmap(BitmapFactory.decodeByteArray(imageByteArray, 0,
                imageByteArray.length));
        dialog.show();
    }

    public static int getMaxImagesforWindowDis(String types) {
        int maxImage = 0;
        try {
            String stMaxValue = OfflineManager.getValueByColumnName(Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +
                    Constants.SC + "' and " + Constants.Types + " eq '" + types + "' &$top=1", Constants.TypeValue);

            if (!TextUtils.isEmpty(stMaxValue))
                maxImage = Integer.parseInt(stMaxValue);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxImage;
    }

    public static String convertDateFromString(String date) {
        String dateFinal = "";
        try {
            String[] splited = date.split("/");
            String d = splited[0];
            String m = splited[1];
            String y = splited[2];
            return y + "-" + m + "-" + d + "T00:00:00";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateFinal;
    }

    public static void displayShortToast(Context mContext, String message) {
        try {
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void displayLongToast(Context mContext, String message) {
        try {
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void displayToastCustDue(Context mContext, String message, int sec) {
        // Set the toast and duration
        int toastDurationInMilliSeconds = 1000 * sec;
        final Toast mToastToShow = Toast.makeText(mContext, message, Toast.LENGTH_LONG);
        // Set the countdown to display the toast
        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 1000 /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                mToastToShow.show();
            }

            public void onFinish() {
                mToastToShow.cancel();
            }
        };
        // Show the toast and starts the countdown
        mToastToShow.show();
        toastCountDown.start();
    }

    public static void displayErrorDialog(Context mContext, String message) {
        if (!TextUtils.isEmpty(message)) {
            UtilConstants.dialogBoxWithCallBack(mContext, "", message, mContext.getString(R.string.ok), "", false, null);
        } else {
            UtilConstants.dialogBoxWithCallBack(mContext, "", mContext.getString(R.string.msg_no_network), mContext.getString(R.string.ok), "", false, null);
        }
    }

    public static String getSessionId(Context mContext) throws IOException, JSONException {
        String sessionId = "1";
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        String loginId = sharedPreferences.getString("username", "");
        String psw = sharedPreferences.getString(UtilConstants.Password_Key, "");
        String connectionId = sharedPreferences.getString(UtilConstants.appConnID_key, "");
        URL getURL = getSessionURL(mContext);
        if (getURL != null) {
            //https://ppcutilsc7c0ed3a1-ce507821b.ap1.hana.ondemand.com/ppcutils/GetLoginID/?destname=pugw_utils_op&Application=PD&Object=mDC&Method=read&FmName=&IsTestRun=
            String strJson = downloadUrl(getURL, loginId, psw, connectionId);
            JSONObject jsonObject = new JSONObject(strJson);
            sessionId = jsonObject.optString("UserSession");
        }
        Log.e("Main", "Session ID Loaded");
        LogManager.writeLogInfo("Session ID Loaded");
        return sessionId;
    }

    private static URL getSessionURL(Context mContext) throws MalformedURLException {
        URL url = null;
//        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
     /*   LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();
        String protocol = lgCtx.isHttps() ? "https" : "http";
        String relayUrlSuffix = lgCtx.getResourcePath();
        if (relayUrlSuffix.equalsIgnoreCase(""))
            url = new URL("" + protocol + "://" + lgCtx.getHost() + ":" + lgCtx.getPort() + "/UserSession");
        else {
            String farmId = lgCtx.getFarmId();
            url = new URL("" + protocol + "://" + lgCtx.getHost() + ":" + lgCtx.getPort() + "/" + relayUrlSuffix + "/" + farmId + "/" + "UserSession");
        }*/
        return url;
    }

    /*http url connection*/
    public static String downloadUrl(URL url, String userName, String psw, String connectionId) throws IOException {
        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(1000 * 30);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(1000 * 30);
            String userCredentials = userName + ":" + psw;
            String basicAuth = "Basic " + Base64.encodeToString(userCredentials.getBytes("UTF-8"), Base64.NO_WRAP);
            connection.setRequestProperty("Authorization", basicAuth);
            connection.setRequestProperty("X-SMP-APPCID", connectionId);
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod("GET");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);
            // Open communications link (network traffic occurs here).
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();
            if (stream != null) {
                // Converts Stream to String with max length of 500.
                result = readStream(stream);
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    private static String readStream(InputStream stream)
            throws IOException {
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        StringBuilder buffer = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append('\n');
        }
        return buffer.toString();
    }

    public static void insertSyncTimes(String colName) {
        if (colName.contains("?$")) {
            String splitCollName[] = colName.split("\\?");
            colName = splitCollName[0];
            if (colName.contains("/")) {
                String splitColl[] = colName.split("/");
                colName = splitColl[0];
            }
        }
        String syncTime = UtilConstants.getSyncHistoryddmmyyyyTime();
        try {
            Constants.events.insertLogHistoryTable(Constants.SYNC_TABLE, colName, syncTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void selectedView(final View v, final Spinner spinner, final int position, final Context mContext) {
        v.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ((TextView) v.findViewById(R.id.tvItemValue)).setSingleLine(false);
                    if (position == spinner.getSelectedItemPosition())
                        ((TextView) v.findViewById(R.id.tvItemValue)).setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * This method checks spaces available or not.
     */
    public static boolean checkIfSpaces(String str) {
        boolean result = false;
        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(str);
        result = matcher.find();
        return result;

    }

    public static String getLastSyncTime(String collName, String whereCol, String whereColVal, String retiveColName, Context context) {
        String lastSyncTime = "";
        Cursor cursorLastSync = SyncHist.getInstance().getLastSyncTime(collName, whereCol, whereColVal);
        try {
            if (cursorLastSync != null
                    && cursorLastSync.getCount() > 0) {
                while (cursorLastSync.moveToNext()) {
                    lastSyncTime = cursorLastSync
                            .getString(cursorLastSync
                                    .getColumnIndex(retiveColName)) != null ? cursorLastSync
                            .getString(cursorLastSync
                                    .getColumnIndex(retiveColName)) : "";
                    LogManager.writeLogDebug("Dashboard refresh time value from db: " + lastSyncTime);
                }
            }
            return lastSyncTime;
        }catch (Exception ex){

            ex.printStackTrace();
            LogManager.writeLogDebug("Dashboard refresh time value from db error: " + ex.getLocalizedMessage());
        }
        return lastSyncTime;

    }

    public static void displayFilter(String[] strArray, ViewGroup llFlowLayout, Context mContext) {
        try {
            llFlowLayout.removeAllViews();
            if (strArray != null) {
                FlowLayout.LayoutParams filterParams = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
                filterParams.setMargins(4, 4, 4, 4);
                for (String typeText : strArray) {
                    if (!TextUtils.isEmpty(typeText)) {
                        TextView textView = new TextView(mContext);
                        textView.setLayoutParams(filterParams);
                        textView.setPadding(4, 4, 4, 4);
                        textView.setTextColor(ContextCompat.getColor(mContext, R.color.secondaryTextColor));
                        textView.setText(typeText);
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.util_small_text_sp));
                        textView.setBackgroundResource(R.drawable.chip_shape);
                        llFlowLayout.addView(textView);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showKeyboard(Context mContext, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void downloadFiles(Activity mContext, AsyncTaskCallBackInterface asyncTaskCallBackInterface, String downloadType, String objectNo, String itemNo, String sessionId, boolean isSessionRequired) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        String userId = sharedPreferences.getString(UtilRegistrationActivity.KEY_username, "");
        String psw = sharedPreferences.getString(UtilRegistrationActivity.KEY_password, "");
        String connectionId = sharedPreferences.getString(UtilRegistrationActivity.KEY_appConnID, "");
        new DownloadFileAsyncTask(mContext, asyncTaskCallBackInterface, downloadType, objectNo, itemNo, userId, /*psw, */connectionId, /*Configuration.APP_ID,*/ sessionId, isSessionRequired).execute();
    }

    public static int dpToPx(int dp, Context mContext) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
//        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics));
    }

    public static String commaFormat(BigDecimal number, String format) {
        DecimalFormat df = new DecimalFormat(format);
        return df.format(number);
    }

    public static String getResponseMessage(ODataRequestExecution request, String defaultMessage) {
        String msg = "";
        ODataPayload payload = ((ODataResponseSingle) request.getResponse()).getPayload();
        if (payload != null && payload instanceof ODataEntity) {
            ODataEntity oEntity = (ODataEntity) payload;
            ODataPropMap properties = oEntity.getProperties();
            ODataProperty property = null;
            if (oEntity.getEntityType().equalsIgnoreCase(UtilConstants.getNameSpace(OfflineManager.offlineStore) + Constants.SOS_ENTITY)) {
                try {
                    List<String> responseMessage = request.getResponse().getAllHttpHeaders().get("sap-message");
                    if (responseMessage != null) {
                        if (!responseMessage.isEmpty()) {
                            msg = responseMessage.get(0).split("<message>")[1].split("</message>")[0];
                        }
                    }
                    if (!TextUtils.isEmpty(msg)) {
                        defaultMessage = msg;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return defaultMessage;
    }

    public static String commaSeparator(String value, String uom) {
        String returnValue = "0.00";
        try {
            if (uom.equalsIgnoreCase("INR"))
                returnValue = value != null && !value.equalsIgnoreCase("") ? commaFormat(new BigDecimal(value), "#,##,##0.00") : "0.00";
            else if (uom.equalsIgnoreCase("EUR"))
                returnValue = value != null && !value.equalsIgnoreCase("") ? commaFormat(new BigDecimal(value), "###,###,##0.00") : "0.00";
            else
                returnValue = value != null && !value.equalsIgnoreCase("") ? commaFormat(new BigDecimal(value), "#,##,##0.00") : "0.00";
        } catch (Exception var2) {
            var2.printStackTrace();
            returnValue = "0.00";
        }
        return returnValue;
    }

    public static void focusOnView(final NestedScrollView nestedScroll) {
        nestedScroll.post(new Runnable() {
            @Override
            public void run() {
                nestedScroll.scrollTo(0, 0);
            }
        });
    }

    /*check present future date*/
    public static boolean checkPresentFutureDate(String dateForStore) {
        try {
            Date entered = (new SimpleDateFormat("yyyy-MM-dd")).parse(dateForStore);
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            Date today = c.getTime();
            Calendar cal = Calendar.getInstance();
            cal.setTime(entered);
            Date dateSpecified = cal.getTime();
            if (!dateSpecified.before(today)) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*load more, load less*/
    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore, boolean storeData) {

        if (tv.getTag() == null || storeData) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(tv.getText().toString(), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(tv.getText().toString(), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(tv.getText().toString(), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    /*load more, load less*/
    private static SpannableStringBuilder addClickablePartTextViewResizable(final String strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {

                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "View Less", false, false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 3, "View More", true, false);
                    }

                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }

    /*convert date format*/
    public static String convertDateForStore(String date) {
        String dateFinal = "";
        try {
            String[] splited = date.split("/");
            String d = splited[0];
            String m = splited[1];
            String y = splited[2];
            return y + "-" + m + "-" + d + "T00:00:00";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateFinal;
    }

    public static String removeDecimalValueIfDecimalIsZero(String decimalValue) {
        try {
            if (!TextUtils.isEmpty(decimalValue)) {
                String decimalNumber = decimalValue.substring(decimalValue.indexOf(".")).substring(1);
                Double doubleValue = Double.parseDouble(decimalNumber);
                if (doubleValue > 0) {
                    return decimalValue;
                } else {
                    return commaFormat(new BigDecimal(decimalValue), "##0");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decimalValue;
    }

    /*Navigates to Sync view*/
    public static void onSyncView(Activity activity) {
        Intent intentSyncView = new Intent(activity, SyncSelectionActivity.class);
        activity.startActivity(intentSyncView);
    }

    /*Navigates to Beat Plan*/
    public static void onBeatPlan(Activity activity) {
        Constants.BoolOtherBeatLoaded = false;
        Constants.BoolTodayBeatLoaded = false;
        Intent intentBeatPlanActivity = new Intent(activity,
                RoutePlanListActivity.class);
        activity.startActivity(intentBeatPlanActivity);
    }

    /*Navigates to My Targets*/
    public static void onMyTargets(Activity activity) {
        Intent intentTarget = new Intent(activity,
                TargetsActivity.class);
        activity.startActivity(intentTarget);
      /*  Intent intentMyTargertActivity = new Intent(activity,
                SalesPersonList.class);
        intentMyTargertActivity.putExtra("from","targets");
        activity.startActivity(intentMyTargertActivity);*/
    }

    public static void onMTPApprovals(Activity activity) {
        Intent intent = new Intent(activity, MTPApprovalActivity.class);
        activity.startActivity(intent);
    }

    /*Navigates to Adhoc List*/
    public static void onAdhocList(Activity activity) {
        Constants.CustomerType = "";
        Intent syncSelection = new Intent(activity, CustomerListActivity.class);
        syncSelection.putExtra(Constants.comingFrom, Constants.AdhocList);
        activity.startActivity(syncSelection);
    }

    /*Navigates to Dealer Behaviour List*/
    public static void onDealerBehaviour(Activity activity) {
//        Intent behaviour = new Intent(activity,
//                BehaviourListActivity.class);

       /* Intent behaviour = new Intent(activity,
                DealerBehaviourActivity.class);
        activity.startActivity(behaviour);*/
    }

    /*Navigates to Outstanding Age List*/
    public static void onOutstandingAge(Activity activity) {
        Intent behaviour = new Intent(activity,
                OutstandingAgeReport.class);
        activity.startActivity(behaviour);
    }

    /*Navigates to DB Stock List*/
    public static void onSchemes(Activity activity) {
       /* Intent syncSelection = new Intent(activity,
                SchemeListActivity.class);
        activity.startActivity(syncSelection);*/
    }

    /*Navigates to DB Stock List*/
    public static void onDBStockList(Activity activity) {
        Intent syncSelection = new Intent(activity,
                DBStockActivity.class);
        activity.startActivity(syncSelection);
    }

    /*Navigates to Depot Stock List*/
    public static void onDepotStockList(Activity activity) {
        Intent syncSelection = new Intent(activity,
                DepotStockActivity.class);
        activity.startActivity(syncSelection);
    }

    /*Navigates to Day Summary*/
    public static void onDaySummary(Activity activity) {
      /*  Intent syncSelection = new Intent(activity,
                SalesPersonList.class);
        syncSelection.putExtra("from","daysummary");
        activity.startActivity(syncSelection);*/
    }

    public static void onVisitSummary(Activity activity) {
       /* Intent syncSelection = new Intent(activity,
                SalesPersonList.class);
        syncSelection.putExtra("from","visitsummary");
        activity.startActivity(syncSelection);*/
    }

    /*Navigates to Alerts Screen*/
    public static void onAlerts(Activity activity) {
        Intent syncSelection = new Intent(activity, BirthdayAlertsActivity.class);
        activity.startActivity(syncSelection);
    }

    /*Navigates to Expense Entry Screen*/
    public static void onExpenseEntry(Activity activity) {
//        Intent syncSelection = new Intent(activity,
//                ExpenseEntryActivity.class);
        Intent syncSelection = new Intent(activity,
                ExpenseEntryActivity.class);
        activity.startActivity(syncSelection);
    }

    /*Navigates to Expense List Screen*/
    public static void onExpenseList(Activity activity) {
        Intent syncSelection = new Intent(activity,
                ExpenseListPage.class);
        activity.startActivity(syncSelection);
    }

    /*Navigates to Dealer Dealer Target */
    public static void onTargetVsAchivement(Activity activity) {
//        Intent behaviour = new Intent(activity,
//                TargetvsAchivement.class);
/*
        Intent behaviour = new Intent(activity,
                MyDealerWiseTargetsActivity.class);
        activity.startActivity(behaviour);*/
    }

    /*Navigates to Depot Sales Traget*/
    public static void onSalesTargetVsAchivement(Activity activity) {
//        Intent behaviour = new Intent(activity,
//                SalesTargetVsAchivement.class);

      /*  Intent behaviour = new Intent(activity,
                MyDepotWiseTargetsActivity.class);
        activity.startActivity(behaviour);*/
    }

    /*Navigates to Matwise Traget*/
    public static void onMatWiseTargetVsAchivement(Activity activity) {
       /* Intent behaviour = new Intent(activity,
                MyMatWiseTargetsActivity.class);
        activity.startActivity(behaviour);*/
    }

    /*Navigates to Dealer Behaviour List*/
    public static void onSoApproval(Activity activity) {
        Intent behaviour = new Intent(activity, SOApproveActivity.class);
        activity.startActivityForResult(behaviour,SO_RESULT_CODE);
    }

    public static void onRetailerList(Activity activity) {
       /* Intent behaviour = new Intent(activity,
                RetailerListSelectionActivity.class);
        activity.startActivity(behaviour);*/
    }

    public static void onDSREntry(Activity activity) {
        /*Intent behaviour = new Intent(activity,
                DSREntryActivity.class);
        activity.startActivity(behaviour);*/
    }

    public static void onCustomerList(Activity activity) {
        Constants.CustomerType = "";
        Intent retList = new Intent(activity, CustomerListActivity.class);
        retList.putExtra(Constants.PassedFrom, 100);
        activity.startActivity(retList);
    }

    public static void onRTGS(Activity activity) {
        Intent behaviour = new Intent(activity, RTGSActivity.class);
        activity.startActivity(behaviour);
    }

    public static void onSubordinateView(Activity activity, String type) {
        Intent intent = new Intent(activity, SalesPersonsViewActivity.class);
        intent.putExtra(ConstantsUtils.EXTRA_COMING_FROM,type);
        activity.startActivity(intent);
    }

    public static void onAttendanceSummary(Activity activity, String type) {
        Intent intent = new Intent(activity, AttendanceSummaryActivity.class);
        intent.putExtra(ConstantsUtils.EXTRA_COMING_FROM,type);
        activity.startActivity(intent);
    }

    public static void onClaimSummary(Activity activity, String type) {
        Intent intent = new Intent(activity, ClaimReportActivity.class);
        intent.putExtra(ConstantsUtils.EXTRA_COMING_FROM,type);
        activity.startActivity(intent);
    }

    public static void onDealerBehaver(Activity activity) {
        Intent behaviour = new Intent(activity, BehaviourListActivity.class);
        activity.startActivity(behaviour);
    }
    public static void onResetPwd(Activity activity) {
        Intent behaviour = new Intent(activity, ResetPassword.class);
        activity.startActivity(behaviour);
    }
    public static void onMTPActivity(Activity activity) {
        Intent intent = new Intent(activity, MTPActivity.class);
        activity.startActivity(intent);
    }

    public static long getCurrentTimeLong() {
        Calendar rightNow = Calendar.getInstance();
        return rightNow.getTimeInMillis();
    }

    public static String convertDateIntoDDMMYYYY(String dateString) {
        String stringDateReturns = "";
        Date date = null;
        try {
            date = (new SimpleDateFormat("dd/MM/yyyy")).parse(dateString);
            stringDateReturns = (new SimpleDateFormat("dd-MMM-yyyy")).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return stringDateReturns;
    }

    public static String convertCalenderToDisplayDateFormat(GregorianCalendar calendar) {
        String dateFormatted = "";
        try {
            if (calendar != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                simpleDateFormat.setCalendar(calendar);
                dateFormatted = simpleDateFormat.format(calendar.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateFormatted;
    }

    public static String convertCalenderToDisplayDateFormatNew(GregorianCalendar calendar) {
        String dateFormatted = "";
        try {
            if (calendar != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                simpleDateFormat.setCalendar(calendar);
                dateFormatted = simpleDateFormat.format(calendar.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateFormatted;
    }


    public static long getMilliSeconds(String givenDateString) {
        long timeInMilliseconds = 0;
        if (!TextUtils.isEmpty(givenDateString)) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            try {
                Date mDate = sdf.parse(givenDateString);
                timeInMilliseconds = mDate.getTime();
                System.out.println("Date in milli :: " + timeInMilliseconds);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return timeInMilliseconds;
    }

    public static long getMilliSecondsFormat(String givenDateString) {
        long timeInMilliseconds = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            Date mDate = sdf.parse(givenDateString);
            timeInMilliseconds = mDate.getTime();
            System.out.println("Date in milli :: " + timeInMilliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }

    public static ViewGroup.MarginLayoutParams getLayoutParams(CardView cardView) {
        return (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
    }

    public static SpannableString generateCenterSpannableText(String totalPercent) {

        SpannableString s = new SpannableString(totalPercent);
        s.setSpan(new RelativeSizeSpan(2f), 0, s.length(), 0);
        return s;
    }

    public static String getCurrentDateFormat() {
        String dateFormatted = "";
        try {
            Calendar calendar = Calendar.getInstance();
            if (calendar != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                TimeZone timeZone = TimeZone.getTimeZone("Asia/Calcutta");
                simpleDateFormat.setTimeZone(timeZone);
                dateFormatted = simpleDateFormat.format(calendar.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateFormatted;
    }

    public static String convertCalenderToDisplayDateFormat(Calendar calendar, String format) {
        String dateFormatted = "";
        try {
            if (calendar != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                simpleDateFormat.setCalendar(calendar);
                dateFormatted = simpleDateFormat.format(calendar.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateFormatted;
    }

    public static String getDayDate() {
        String date = "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        Date today = new Date();
        date = formatter.format(today);
        return date;
    }

    public static String getMonth() {
        String date = "";
        SimpleDateFormat formatter = new SimpleDateFormat("MM");
        Date today = new Date();
        date = formatter.format(today);
        return date;
    }

    public static String getYear() {
        String date = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        Date today = new Date();
        date = formatter.format(today);
        return date;
    }

    public static String getNextMonth() {
        String date = "";
        SimpleDateFormat formatter = new SimpleDateFormat("MM");
        java.util.Date now = new Date();
        Calendar myCal = Calendar.getInstance();
        myCal.setTime(now);
        myCal.add(Calendar.MONTH, +1);
        now = myCal.getTime();
        date = formatter.format(now);
        return date;
    }

    public static String getNextYear() {
        String date = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        java.util.Date now = new Date();
        Calendar myCal = Calendar.getInstance();
        myCal.setTime(now);
        myCal.add(Calendar.YEAR, +1);
        now = myCal.getTime();
        date = formatter.format(now);
        return date;
    }

    public static Calendar convertCalenderToDisplayDateFormat(String date, String inPutFormat) {
        Calendar cal = Calendar.getInstance();
        try {
            if (!TextUtils.isEmpty(date)) {
                SimpleDateFormat sdf = new SimpleDateFormat(inPutFormat, Locale.ENGLISH);
                cal.setTime(sdf.parse(date));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cal;
    }

    public static String getDisplayDateFormat(Context mContext) {
        return "dd-MMM-yyyy";
    }

    public static MTPRoutePlanBean parseMTPItems(MTPRoutePlanBean mtpRoutePlanBean, ODataPropMap propertiesItem, boolean isAsmLogin) {
        ODataProperty property;
        property = propertiesItem.get(Constants.RouteSchPlanGUID);
        try {
            ODataGuid mInvoiceGUID = (ODataGuid) property.getValue();
            mtpRoutePlanBean.setRouteSchPlanGUID(mInvoiceGUID.guidAsString36().toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
        property = propertiesItem.get(Constants.RouteSchGUID);
        try {
            ODataGuid mInvoiceGUID = (ODataGuid) property.getValue();
            mtpRoutePlanBean.setRouteSchGUID(mInvoiceGUID.guidAsString36().toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
        property = propertiesItem.get(Constants.VisitCPGUID);
        mtpRoutePlanBean.setCustomerNo(property.getValue().toString());
        ArrayList<CustomerBean> customerList = null;
        try {
            if (!TextUtils.isEmpty(mtpRoutePlanBean.getCustomerNo())) {
                customerList = OfflineManager.getCustomerList(Constants.Customers + "?$filter=" + Constants.CustomerNo + " eq '" + mtpRoutePlanBean.getCustomerNo() + "' ");
                if (!customerList.isEmpty()) {
                    CustomerBean customerBean = customerList.get(0);
                    mtpRoutePlanBean.setCustomerName(customerBean.getCustomerName());
                    mtpRoutePlanBean.setAddress(customerBean.getAddress1());
                    mtpRoutePlanBean.setPostalCode(customerBean.getPostalCode());
                    mtpRoutePlanBean.setMobile1(customerBean.getMobile1());
                    mtpRoutePlanBean.setCity(customerBean.getCity());
                }
            } else {
                property = propertiesItem.get(Constants.SalesDistrict);
                if (property != null)
                    mtpRoutePlanBean.setSalesDistrict(property.getValue().toString());
                if (!TextUtils.isEmpty(mtpRoutePlanBean.getSalesDistrict())) {
                    property = propertiesItem.get(Constants.SalesDistrictDesc);
                    if (property != null)
                        mtpRoutePlanBean.setSalesDistrictDesc(property.getValue().toString());
                }
            }
            property = propertiesItem.get(Constants.Remarks);
            mtpRoutePlanBean.setRemarks(property.getValue().toString());
            property = propertiesItem.get(Constants.ActivityDesc);
            mtpRoutePlanBean.setActivityDec(property.getValue().toString());
            property = propertiesItem.get(Constants.ActivityID);
            mtpRoutePlanBean.setActivityId(property.getValue().toString());
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return mtpRoutePlanBean;
    }

    public static String getMonthMMM(String month) {
        String mStrMonth = "";
        if (month != null && !month.equalsIgnoreCase("")) {
            try {
                Calendar cal = Calendar.getInstance();
                int monthVal = 0;
                if (!TextUtils.isEmpty(month) && TextUtils.isDigitsOnly(month)) {
                    try {
                        monthVal = Integer.parseInt(month);
                        cal.set(Calendar.MONTH, monthVal - 1);
                        mStrMonth = new SimpleDateFormat("MMM").format(cal.getTime());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                mStrMonth = "";
                e.printStackTrace();
            }
        }
        return mStrMonth;
    }

    public static void showVisitRemarksDialog(final Activity activity, final CustomDialogCallBack customDialogCallBack, String title) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.visit_remarks_dialog);

        final EditText etRemarks = (EditText) dialog.findViewById(R.id.etRemarks);
        final TextInputLayout tilRemarks = (TextInputLayout) dialog.findViewById(R.id.tilRemarks);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
        tvTitle.setText(title);
        Button okButton = (Button) dialog.findViewById(R.id.btYes);
        Button cancleButton = (Button) dialog.findViewById(R.id.btNo);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etRemarks.getText().toString().trim().length() < 1) {
                    tilRemarks.setErrorEnabled(true);
                    tilRemarks.setError(activity.getString(R.string.visit_error_remarks));
                } else {
                    dialog.dismiss();
                    if (customDialogCallBack != null) {
                        customDialogCallBack.cancelDialogCallBack(true, "", etRemarks.getText().toString());
                    }
                }
            }
        });
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (customDialogCallBack != null) {
                    customDialogCallBack.cancelDialogCallBack(false, "", "");
                }
            }
        });
        etRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tilRemarks.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();

    }

    public static boolean isAutomaticTimeZone(Context mContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return android.provider.Settings.Global.getInt(mContext.getContentResolver(), android.provider.Settings.Global.AUTO_TIME, 0) == 1;
//                return Settings.Global.getInt(mContext.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(mContext.getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0) == 1;
        }
    }

    public static void showAutoDateSetDialog(final Activity mContext) {
        UtilConstants.dialogBoxWithCallBack(mContext, "", mContext.getString(R.string.autodate_change_msg), mContext.getString(R.string.autodate_change_btn), "", false, new com.arteriatech.mutils.interfaces.DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                mContext.startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), DATE_SETTINGS_REQUEST_CODE);
            }
        });
    }

    public static void printDebugLog(String message) {
        Log.d("mSFADebuLog-RSPL", "debuLog : " + message);
//        LogManager.writeLogDebug("debuLog : " + message);
    }

    public static void printErrorLog(String message) {
        Log.d("mSFAErrorLog-RSPL", "ErrorLog : " + message);
        LogManager.writeLogError(Constants.error_txt + message);
    }

    public static String convertCalenderToDisplayDateTimeFormat(GregorianCalendar calendar) {
        String dateFormatted = "";
        try {
            if (calendar != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
                TimeZone timeZone = TimeZone.getTimeZone("Asia/Calcutta");
                calendar.add(Calendar.HOUR, 5);
                calendar.add(Calendar.MINUTE, 30);
                simpleDateFormat.setTimeZone(timeZone);
                dateFormatted = simpleDateFormat.format(calendar.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateFormatted;
    }
public static void stopAlarmManagerByID(Context sContetx, Class<?> cls, int requestID) {
        Intent intent = new Intent(sContetx, cls);
        AlarmManager alarmManager = (AlarmManager) sContetx.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(sContetx.getApplicationContext(), requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (pendingIntent != null) {
            pendingIntent.cancel();
            alarmManager.cancel(pendingIntent);
        }

       /* myIntent = new Intent(SetActivity.this, AlarmActivity.class);
        pendingIntent = PendingIntent.getActivity(CellManageAddShowActivity.this,
                id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent.cancel();
        alarmManager.cancel(pendingIntent);*/


    }

    public static class ArrayComarator implements Comparator<String[]> {

        @Override
        public int compare(String s1[], String s2[]) {
            BigInteger i1 = null;
            BigInteger i2 = null;
            try {
                i1 = new BigInteger(s1[0]);
            } catch (NumberFormatException e) {
            }

            try {
                i2 = new BigInteger(s2[0]);
            } catch (NumberFormatException e) {
            }

            if (i1 != null && i2 != null) {
                return i1.compareTo(i2);
            } else {
                return s1[0].compareTo(s2[0]);
            }
        }

    }

    public static String checkNoUOMZero(String UOM, String qty) throws OfflineODataStoreException {
        boolean isNoUOMZero = false;
        String qry = Constants.ConfigTypesetTypes + "?$filter=" + Constants.Typeset + " eq '" + Constants.UOMNO0 + "' and " +
                Constants.Types + " eq '" + UOM + "'";

        if (UOM != null && !UOM.equalsIgnoreCase("")) {
            if (OfflineManager.offlineStore != null) {
                List<ODataEntity> entities = UtilOfflineManager.getEntities(OfflineManager.offlineStore, qry);
                if (entities != null && entities.size() > 0) {
                    isNoUOMZero = true;
                }
            }
        }
        try {
            if (isNoUOMZero)
                return qty != null && !qty.equalsIgnoreCase("") ? commaFormat(new BigDecimal(qty), "###,###,##0") : "000";
            else if (UOM != null && UOM.equals("")) {
                return qty != null && !qty.equalsIgnoreCase("") ? commaFormat(new BigDecimal(qty), "###,###,##0") : "000";
            } else
                return qty != null && !qty.equalsIgnoreCase("") ? commaFormat(new BigDecimal(qty), "###,###,##0.00") : "0.00";
        } catch (Exception e) {
            e.printStackTrace();
            if (qty != null)
                return qty;
            else
                return "";
        }
    }

    public static String getAutoSyncTimeInMin() {
        try {
            //time in minutes
            return OfflineManager.getValueByColumnName(Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +
                    Constants.SF + "' and " + Constants.Types + " eq '" + Constants.AUTOSYNC + "' &$top=1", Constants.TypeValue);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getLocationAutoSyncTimeInMin() {
        try {
            //time in minutes
            return OfflineManager.getValueByColumnName(Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +
                    Constants.SF + "' and " + Constants.Types + " eq '" + Constants.GEOAUTOSYN + "' &$top=1", Constants.TypeValue);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static void startAutoSync(Context mContext, boolean isForceReset) {
        try {
            if (ConstantsUtils.isAutomaticTimeZone(mContext)) {
                SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
                try {
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("FIRSTAUTOSYNCTIME", sdf.format(cal.getTime()));
                    editor.putString("LASTSYNCDATE", UtilConstants.getNewDate());
                    editor.apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Constants.isSync = false;
                String autoSyncTime = ConstantsUtils.getAutoSyncTimeInMin();
                if (isForceReset || !sharedPreferences.getString("AUTOSYNCTIME", "").equalsIgnoreCase(autoSyncTime)) {
//                if (!TextUtils.isEmpty(autoSyncTime)) {
                    //  UpdatePendingRequest.getInstance(null).callSchedule(autoSyncTime);
                    Intent intent = new Intent(mContext.getApplicationContext(), AutoSyncDataAlarmReceiver.class);
                    // Create a PendingIntent to be triggered when the alarm goes off
                    alarmPendingIntent = PendingIntent.getBroadcast(mContext, AutoSyncDataAlarmReceiver.REQUEST_CODE,
                            intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    // Setup periodic alarm every 5 seconds
                    long firstMillis = System.currentTimeMillis(); // first run of alarm is immediate
                    int intervalMillis = 1000 * 60 * Integer.parseInt(autoSyncTime); // as of API 19, alarm manager will be forced up to 60000 to save battery
                    AlarmManager alarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                    // See https://developer.android.com/training/scheduling/alarms.html
                    alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, intervalMillis, alarmPendingIntent);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("AUTOSYNCTIME", autoSyncTime);
                    editor.apply();
//                }
                }
            }else{
                LogManager.writeLogError("Auto Sync not started because date is not valid ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    public static void startAutoSyncLocation(Context mContext, boolean isForceReset) {
        try {
            if (ConstantsUtils.isAutomaticTimeZone(mContext)) {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
            Constants.isSync = false;
            String autoSyncTime = ConstantsUtils.getLocationAutoSyncTimeInMin();  /*"15"*/
           // autoSyncTime = "2";
            ;
            if (TextUtils.isEmpty(autoSyncTime)) {
                autoSyncTime = "15";
            }
            if (isForceReset || !sharedPreferences.getString("LocationServiceAutoSync", "").equalsIgnoreCase(autoSyncTime)) {
                if (!TextUtils.isEmpty(autoSyncTime)) {
                    // UpdatePendingLatLongRequest.getInstance(null).callSchedule(autoSyncTime);
                    Intent intent = new Intent(mContext.getApplicationContext(), AutoSyncDataLocationAlarmReceiver.class);
                    // Create a PendingIntent to be triggered when the alarm goes off
                    alarmPendingIntent = PendingIntent.getBroadcast(mContext, AutoSyncDataLocationAlarmReceiver.REQUEST_CODE,
                            intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    // Setup periodic alarm every 5 seconds
                    long firstMillis = System.currentTimeMillis(); // first run of alarm is immediate
                    int intervalMillis = 1000 * 60 * Integer.parseInt(autoSyncTime); // as of API 19, alarm manager will be forced up to 60000 to save battery
                    AlarmManager alarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                    // See https://developer.android.com/training/scheduling/alarms.html
                    alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, intervalMillis, alarmPendingIntent);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("LocationServiceAutoSync", autoSyncTime);
                    editor.apply();
                }
            }
            }else{
                LogManager.writeLogError("Auto Location Sync not started because date is not valid ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    public static BigDecimal decimalRoundOff(BigDecimal bigDecimalValue, int numberOfDigitsAfterDecimalPoint) {
        bigDecimalValue = bigDecimalValue.setScale(numberOfDigitsAfterDecimalPoint,
                BigDecimal.ROUND_HALF_UP);
        return bigDecimalValue;
    }

    public static String removeLeadingZero(String mStrAmount) {
        String mStrRetAmount = "";
        try {
            if (mStrAmount != null && !mStrAmount.equalsIgnoreCase("")) {
                BigDecimal number = new BigDecimal(mStrAmount);
                DecimalFormat df = new DecimalFormat("#####0");
                mStrRetAmount = df.format(number);
            } else {
                mStrRetAmount = "";
            }
        } catch (Exception e) {
            mStrRetAmount = "";
            e.printStackTrace();
        }
        return mStrRetAmount;
    }

    public static String loadComplaintsURl() {
        // https://awfpkkad0.accounts.ondemand.com/ui/createForgottenPasswordMail?spId=5aa1705ce4b0f2fd8f5a4303&targetUrl=&sourceUrl=
        String domainName = "";
        domainName = ConstantsUtils.getConfigTypeSet(Constants.IDPACCNAME);
        domainName = domainName + ".";
        domainName = domainName + ConstantsUtils.getConfigTypeSet(Constants.DOMAINNAME);
        domainName = domainName + "/ui/protected/profilemanagement";
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https://")
                .appendPath(domainName);
        return "https://" + domainName;
    }

    /*get config type set*/
    public static String getConfigTypeSet(String typesValue) {
        return Constants.getConfigTypeIndicator(Constants.ConfigTypsetTypeValues,
                Constants.TypeValue, Constants.Types, typesValue, Constants.Typeset, Constants.SF);
    }

    public static void showPasswordRemarksDialog(final Activity activity, final CustomDialogCallBack customDialogCallBack, String title) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.ll_pwd_edit1);

        final EditText newPassword = (EditText) dialog.findViewById(R.id.etNewPsw);
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isWhitespace(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }

        };
        newPassword.setFilters(new InputFilter[]{filter});
        final TextInputLayout tilRemarks = (TextInputLayout) dialog.findViewById(R.id.tilRemarks);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
        tvTitle.setText(title);
        Button okButton = (Button) dialog.findViewById(R.id.btYes);
        Button cancleButton = (Button) dialog.findViewById(R.id.btNo);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(newPassword.getText().toString())){
//                    System.out.println("Not Valid");
                    tilRemarks.setErrorEnabled(true);
                    tilRemarks.setError("Please enter password");
                }else if(!isValidPassword(newPassword.getText().toString())){
//                    System.out.println("Not Valid");
                    tilRemarks.setErrorEnabled(true);
                    tilRemarks.setError("Password must contain mix of upper and lower case letters as well as digits and one special character(8-20)");
                }else{
                    System.out.println("Valid");
                    dialog.dismiss();
                    if (customDialogCallBack != null) {
                        customDialogCallBack.cancelDialogCallBack(true, "", newPassword.getText().toString());
                    }
                }
            }
        });
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (customDialogCallBack != null) {
                    customDialogCallBack.cancelDialogCallBack(false, "", "");
                }
            }
        });
        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tilRemarks.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();

    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
//        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        final String PASSWORD_PATTERN =   "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20})";;

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public static int getFirstTimeRun(Context mContext) {
        SharedPreferences sp = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        int result, currentVersionCode = BuildConfig.VERSION_CODE;
        int lastVersionCode = sp.getInt(Constants.KEY_FIRST_TIME_RUN, -1);
        if (lastVersionCode == -1) result = 0; else
            result = (lastVersionCode == currentVersionCode) ? 1 : 2;
        sp.edit().putInt(Constants.KEY_FIRST_TIME_RUN, currentVersionCode).apply();
        return result;
    }

    public static int getFirstTimeRunCFOnlineStore(Context mContext) {
        SharedPreferences sp = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        int result, currentVersionCode = BuildConfig.VERSION_CODE;
        int lastVersionCode = sp.getInt(Constants.KEY_FIRST_TIME_RUN_DashBroad, -1);
        if (lastVersionCode == -1) result = 0; else
            result = (lastVersionCode == currentVersionCode) ? 1 : 2;
        sp.edit().putInt(Constants.KEY_FIRST_TIME_RUN_DashBroad, currentVersionCode).apply();
        return result;
    }

    public static ArrayList<String> getMTPCollection(){
        ArrayList<String> alAssignColl = new ArrayList<>();
        alAssignColl.add(Constants.RoutePlans);
        alAssignColl.add(Constants.RouteSchedulePlans);
        alAssignColl.add(Constants.RouteSchedules);
        return alAssignColl;
    }


    public static ArrayList<String> getRGTCollection(){
        ArrayList<String> alAssignColl = new ArrayList<>();
        alAssignColl.add(Constants.CollectionPlan);
        alAssignColl.add(Constants.CollectionPlanItem);
        alAssignColl.add(Constants.CollectionPlanItemDetails);
        alAssignColl.add(Constants.Collections);
        return alAssignColl;
    }

    public static ArrayList<String> getSaleOrderCollection(){
        ArrayList<String> alAssignColl = new ArrayList<>();
        alAssignColl.add(Constants.SOs);
        alAssignColl.add(Constants.SOItemDetails);
        alAssignColl.add(Constants.SOTexts);
        alAssignColl.add(Constants.SOItems);
        alAssignColl.add(Constants.SOConditions);
        alAssignColl.add(Constants.MaterialByCustomers);
        return alAssignColl;
    }

    public static ArrayList<String> getCustomerCollection(){
        ArrayList<String> alAssignColl = new ArrayList<>();
        alAssignColl.add(Constants.Customers);
        alAssignColl.add(Constants.CustomerPartnerFunctions);
        alAssignColl.add(Constants.CustomerSalesAreas);
        alAssignColl.add(Constants.UserCustomers);
        return alAssignColl;
    }

    public static ArrayList<String> getInvoiceeCollection(){
        ArrayList<String> alAssignColl = new ArrayList<>();
        alAssignColl.add(Constants.InvoiceItemDetails);
        alAssignColl.add(Constants.InvoiceItems);
        alAssignColl.add(Constants.INVOICES);
        alAssignColl.add(Constants.InvoiceConditions);
        alAssignColl.add(Constants.InvoicePartnerFunctions);
        return alAssignColl;
    }

    public static ArrayList<String> getValueHelps(){
        ArrayList<String> alAssignColl = new ArrayList<>();
        alAssignColl.add(Constants.ValueHelps);
        alAssignColl.add(Constants.ConfigTypsetTypeValues);
        alAssignColl.add(Constants.ConfigTypesetTypes);
        return alAssignColl;
    }
    public static ArrayList<String> getTargets(){
        ArrayList<String> alAssignColl = new ArrayList<>();
        alAssignColl.add(Constants.KPISet);
        alAssignColl.add(Constants.Targets);
        alAssignColl.add(Constants.TargetItems);
        alAssignColl.add(Constants.KPIItems);
        return alAssignColl;
    }

    public static ArrayList<String> getSalePerson(){
        ArrayList<String> alAssignColl = new ArrayList<>();
        alAssignColl.add(Constants.UserSalesPersons);
        alAssignColl.add(Constants.SalesPersons);
        return alAssignColl;
    }

    public static ArrayList<String> getMaster(){
        ArrayList<String> alAssignColl = new ArrayList<>();
//        alAssignColl.add(Constants.MaterialSaleAreas);
        alAssignColl.add(Constants.OrderMaterialGroups);
        return alAssignColl;
    }

    public static ArrayList<String> getRO(){
        ArrayList<String> alAssignColl = new ArrayList<>();
        alAssignColl.add(Constants.ReturnOrders);
        alAssignColl.add(Constants.ReturnOrderItems);
        alAssignColl.add(Constants.ReturnOrderItemDetails);
        return alAssignColl;
    }
    public static void serviceReSchedule(Context context, boolean isReSchedule) {
        String GEOSTRTTME = "8";
        String GEOENDTME = "20";
        int timeInterval = 30;
        String distance = "0";
        String enableGeo = "";
        try {
            SharedPreferences mPrefs = context.getSharedPreferences(Constants.PREFS_NAME, 0);
            int oldTimeInterval = mPrefs.getInt(context.getString(R.string.geo_location_interval_time), 30);

            SharedPreferences.Editor editor = mPrefs.edit();

            //Start time
            GEOSTRTTME = OfflineManager.getValueByColumnName("ConfigTypsetTypeValues?$filter=Typeset eq 'SP' and Types eq 'GEOSTRTTME'", Constants.TypeValue);
            if (TextUtils.isEmpty(GEOSTRTTME))
                GEOSTRTTME = "8";
            editor.putString(context.getString(R.string.geo_start_time), GEOSTRTTME);

            //End time
            GEOENDTME = OfflineManager.getValueByColumnName("ConfigTypsetTypeValues?$filter=Typeset eq 'SP' and Types eq 'GEOENDTME'", Constants.TypeValue);
            if (TextUtils.isEmpty(GEOENDTME))
                GEOENDTME = "20";
            editor.putString(context.getString(R.string.geo_end_time), GEOENDTME);

            //timeInterval
            String locTimeInterval = OfflineManager.getValueByColumnName("ConfigTypsetTypeValues?$filter=Typeset eq 'SP' and Types eq 'TIMEINTRVL'", Constants.TypeValue);
            if (!TextUtils.isEmpty(locTimeInterval))
                timeInterval = Integer.parseInt(locTimeInterval);
            editor.putInt(context.getString(R.string.geo_location_interval_time), timeInterval);

            //distance
            distance = OfflineManager.getValueByColumnName("ConfigTypsetTypeValues?$filter=Typeset eq 'SP' and Types eq 'DISPDIST'", Constants.TypeValue);
            if (TextUtils.isEmpty(distance))
                distance = "0";
            editor.putString(context.getString(R.string.geo_smallest_displacement), distance);

            //enableGeo
            enableGeo = OfflineManager.getValueByColumnName("ConfigTypsetTypeValues?$filter=Typeset eq 'SP' and Types eq 'ENABLEGEO'", Constants.TypeValue);
            if (TextUtils.isEmpty(enableGeo))
                enableGeo = "";
            if (enableGeo.equalsIgnoreCase("X"))
                editor.putBoolean(context.getString(R.string.enable_geo), true);
            else
                editor.putBoolean(context.getString(R.string.enable_geo), false);
            editor.apply();
            //service start depends on enable geo value(X-enable)
            if (isReSchedule && enableGeo.equalsIgnoreCase("")) {
                try {
                    context.stopService(new Intent(context, TrackerService.class));
                    UpdatePendingLatLongRequest.getInstance(null).canceScheduledTask();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                if (!isMyServiceRunning(TrackerService.class, context))
                    context.startService(new Intent(context, TrackerService.class));
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
public static void stopTrackerService(Context mContext) {
        try {
         //   UpdatePendingLatLongRequest.getInstance(null).canceScheduledTask();
            mContext.stopService(new Intent(mContext, TrackerService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public synchronized static boolean isPinging(){
        try {
            return isReachable();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    private static boolean isReachable() throws IOException {
        final ReentrantLock[] reentrantLock = {null};
        final Socket[] sock = {null};
        final boolean[] isError = new boolean[1];
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (reentrantLock[0] == null){
                            reentrantLock[0] = new ReentrantLock();
                        }
                        reentrantLock[0].lock();
                        int timeoutMs = 1500;
                        sock[0] = new Socket();
                        SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);
                        sock[0].connect(sockaddr, timeoutMs);
                        sock[0].close();
                        isError[0] =true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        isError[0] =false;
                    }finally {
                        assert reentrantLock[0] != null;
                        reentrantLock[0].unlock();
                    }
                }
            });
            thread.start();
            thread.join();
        } catch (Throwable e) {
            isError[0]=false;
        }finally {
            if (sock[0]!=null) {
                if (!sock[0].isClosed()){
                    sock[0].close();
                }
            }
        }
        return isError[0];
    }

    public static void toastAMessage(String msg, final Context context) {
        try {
            if (toast != null && toast.getView().getWindowVisibility() == View.VISIBLE) {
                msg =msg;
            }
            else {  //clear any previously shown toasts that have since stopped being displayed
                messageToToast = "";
            }
            messageToToast = msg;
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(context, messageToToast, Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 public static androidx.appcompat.app.AlertDialog.Builder showAlert(String message, Context context, DialogInterface.OnClickListener listener) {
        androidx.appcompat.app.AlertDialog.Builder builder = null;
        try {
            builder = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.MyTheme);
            builder.setMessage(message).setCancelable(false).setPositiveButton("Ok", listener);
            builder.show();
        } catch (Exception var3) {
            var3.printStackTrace();
        }
        return builder;
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context mContext) {
        try {
            ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return false;
    }
    private static int getMonthFromDate(String date) throws ParseException{
        Date d = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        int month = cal.get(Calendar.MONTH);
        return month + 1;
    }

    public static String getFromDataVault(String docNo, Context mContext) {
        return UtilDataVault.getValueFromDataVault(docNo,mContext,Constants.EncryptKey);
    }

    public static void storeInDataVault(String docNo, String jsonHeaderObjectAsString, Context mContext) {
        UtilDataVault.storeInDataVault(docNo,jsonHeaderObjectAsString,mContext,Constants.EncryptKey);
    }
    public static String getJSONDate(String dateProperty){
        Calendar calendar = null;
        try {
            SimpleDateFormat simpleDateFormat = null;

            if (dateProperty!=null&&!TextUtils.isEmpty(dateProperty)) {
                long timeStamp = Long.parseLong(dateProperty.replaceAll("\\D+",""));
                Date date = new Date(timeStamp);
                calendar = Calendar.getInstance();
                calendar.setTime(date);
                simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                return simpleDateFormat.format(calendar.getTime());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getJSONDate1(String dateProperty){
        Calendar calendar = null;
        try {
            SimpleDateFormat simpleDateFormat = null;

            if (dateProperty!=null&&!TextUtils.isEmpty(dateProperty)) {
                long timeStamp = Long.parseLong(dateProperty.replaceAll("\\D+",""));
                Date date = new Date(timeStamp);
                calendar = Calendar.getInstance();
                calendar.setTime(date);
                simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm aa");
                return simpleDateFormat.format(calendar.getTime());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getJSONString(JSONObject jsonObject,String property){
        try {
            if (!jsonObject.isNull(property))
                return jsonObject.optString(property);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }

    public static MTPRoutePlanBean parseMTPItems(MTPRoutePlanBean mtpRoutePlanBean, JSONObject jsonObject, boolean isAsmLogin) {
        try {
            mtpRoutePlanBean.setRouteSchPlanGUID(jsonObject.optString(Constants.RouteSchPlanGUID).toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mtpRoutePlanBean.setRouteSchGUID(jsonObject.optString(Constants.RouteSchGUID).toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mtpRoutePlanBean.setCustomerNo(jsonObject.optString(Constants.VisitCPGUID));
        ArrayList<CustomerBean> customerList = null;
        try {
            if (!TextUtils.isEmpty(mtpRoutePlanBean.getCustomerNo())) {
                customerList = OfflineManager.getCustomerList(Constants.Customers + "?$filter=" + Constants.CustomerNo + " eq '" + mtpRoutePlanBean.getCustomerNo() + "' ");
                if (!customerList.isEmpty()) {
                    CustomerBean customerBean = customerList.get(0);
                    mtpRoutePlanBean.setCustomerName(customerBean.getCustomerName());
                    mtpRoutePlanBean.setAddress(customerBean.getAddress1());
                    mtpRoutePlanBean.setPostalCode(customerBean.getPostalCode());
                    mtpRoutePlanBean.setMobile1(customerBean.getMobile1());
                    mtpRoutePlanBean.setCity(customerBean.getCity());
                }
            } else {
                mtpRoutePlanBean.setSalesDistrict(jsonObject.optString(Constants.SalesDistrict));
                if (!TextUtils.isEmpty(mtpRoutePlanBean.getSalesDistrict())) {
                    mtpRoutePlanBean.setSalesDistrictDesc(jsonObject.optString(Constants.SalesDistrictDesc));
                }
            }
            mtpRoutePlanBean.setRemarks(jsonObject.optString(Constants.Remarks));
            mtpRoutePlanBean.setActivityDec(jsonObject.optString(Constants.ActivityDesc));
            mtpRoutePlanBean.setActivityId(jsonObject.optString(Constants.ActivityID));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return mtpRoutePlanBean;
    }

    public static String convertStringToCalDateFormat(String date, String format) {
        String dateFormatted = "";

        try {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            Calendar calendar  = Calendar.getInstance();
            calendar.setTime(df.parse(date));
            if (calendar != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                simpleDateFormat.setCalendar(calendar);
                dateFormatted = simpleDateFormat.format(calendar.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateFormatted;
    }
    public static String geterrormessageForInternetlost(String error, Context context){
        String errormessage="";
        if(error.contains("No address associated with hostname"))
        {
            errormessage = context.getString(R.string.data_conn_lost_during_sync);

        }else if(error.contains("Network is unreachable")) {
            errormessage = context.getString(R.string.data_conn_lost_during_sync);
        }else if(error.contains("Software caused connection")) {
            errormessage = context.getString(R.string.data_conn_lost_during_sync);
        }

        return errormessage;
    }
}
