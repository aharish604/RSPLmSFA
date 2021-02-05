package com.rspl.sf.msfa.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;
import com.arteriatech.mutils.registration.RegistrationModel;
import com.arteriatech.mutils.registration.SupportActivity;
import com.arteriatech.mutils.registration.UtilRegistrationActivity;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.rspl.sf.msfa.BuildConfig;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.SPGeo.services.AlaramRecevier;
import com.rspl.sf.msfa.adapter.MainMenuPagerAdapter;
import com.rspl.sf.msfa.asyncTask.RefreshAsyncTask;
import com.rspl.sf.msfa.asyncTask.SyncGeoAsyncTask;
import com.rspl.sf.msfa.autosync.AutoSyncDataLocationAlarmReceiver;
import com.rspl.sf.msfa.backgroundlocationtracker.TrackerService;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.MSFAApplication;
import com.rspl.sf.msfa.database.EventDataSqlHelper;
import com.rspl.sf.msfa.database.EventUserDetail;
import com.rspl.sf.msfa.expense.ExpenseListFragment;
import com.rspl.sf.msfa.interfaces.AsyncTaskCallBack;
import com.rspl.sf.msfa.interfaces.MessageWithBooleanCallBack;
import com.rspl.sf.msfa.log.LogActivity;
import com.rspl.sf.msfa.login.AboutUsActivity;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.notification.NotificationSetClass;
import com.rspl.sf.msfa.productPrice.ProductPriceActivity;
import com.rspl.sf.msfa.pushNotification.NotificationConfig;
import com.rspl.sf.msfa.registration.Configuration;
import com.rspl.sf.msfa.reports.daySummary.DaySummaryFragment;
import com.rspl.sf.msfa.reports.plantstock.PlantStockActivity;
import com.rspl.sf.msfa.soapproval.OpenOnlineManagerStore;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.store.OnlineManager;
import com.rspl.sf.msfa.sync.SyncSelectionActivity;
import com.rspl.sf.msfa.sync.UpdatePendingRequest;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;
import com.sybase.persistence.PrivateDataVault;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * After successfully logged in user navigates this activity.This activity
 * arrange icons grid view manner based on Authorization T codes.Every icon
 * maintain separate functionality.
 */
@SuppressLint("NewApi")
public class MainMenu extends AppCompatActivity implements UIListener, OnClickListener, FragmentCallbackInterface {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final String JOB_TAG = "MyJobService";
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private static final int REQUEST_GPS_PERMISSION = 9871;
    public static Context context;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,Manifest.permission.READ_PHONE_STATE
    };
    private GUID refguid =null;
    private static int REQUEST_SUPPORT = 350;
    private static String NAV_FRAGMENT_TAG = "navFragmentTag";
    private static String DASHBOARD_FRAGMENT_TAG = "dashboardFragmentTag";
    boolean flagforexportDB = true;
    boolean flagforimportDB = true;
    Toolbar toolbar;
    MainMenuPagerAdapter mainMenuPagerAdapter = null;
    ViewPager pagerMainMenu;
    Bundle bundleExtras = null;
    NavigationMenuFragment mainMenuFragment = null;
    String mStrUserName = "";
    private String mStrPopUpText = "";
    private boolean mBooleanIsApplicationExited = false;
    private ProgressDialog pdLoadDialog = null;
    private boolean mBooleanDayStartDialog = false, mBooleanDayEndDialog = false,
            mBooleanDayResetDialog = false;
    private ImageView ivIndicatorPage1, ivIndicatorPage2;
    private DrawerLayout drawerLayout = null;
    private ActionBarDrawerToggle drawerToggle;
    private ActionBar supportActionBar = null;
    private FrameLayout flBackDrop;
    private CollapsingToolbarLayout collapsingToolbar;
    private boolean isFromRegistration;
    private RegistrationModel registrationModel = null;
    private UIListener uiListener;
    private String pDialogStr = "";
    private DaySummaryFragment daySummaryFragment = null;
    private boolean isDialogBoxShowing = false;
    private String TAG = MainMenu.class.getSimpleName();
    private SharedPreferences sharedPerf;
    private BroadcastReceiver mRegistrationBroadcastReceiver = null;
    private boolean isRefresh = true;
    private LocationManager locationManager;
    private FirebaseJobDispatcher mDispatcher = null;
    private Map<String, String> mapTable;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;
    private String GEOSTRTTME = "8";
    private String GEOENDTME = "20";
    private int timeInterval = 30;
    private ReentrantLock reentrantLock = null;
    private String intialSyncTime="";
    private boolean isRefreshDB = false;
    private OpenOfflineStore openOfflineStore=null;


    /*Exports Offline store database*/
    public static boolean exportDB(Context context) {

        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String PACKAGE_NAME;
        PACKAGE_NAME = context.getPackageName();
        String currentDBPath = Constants.offlineDBPath;
        String currentrqDBPath = Constants.offlineReqDBPath;

        String backupDBPath = Constants.backupDBPath;
        String backuprqDBPath = Constants.backuprqDBPath;

        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        File currentrqDB = new File(data, currentrqDBPath);
        File backuprqDB = new File(sd, backuprqDBPath);
        try {
            // Exporting Offline DB
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            // Exporting Offline rq DB
            source = new FileInputStream(currentrqDB).getChannel();
            destination = new FileOutputStream(backuprqDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();

            return true;
        } catch (IOException e) {
            LogManager.writeLogError(e.getMessage());
            return false;
        }
    }

    //    public static void verifyStoragePermissions(Activity activity)
//    {
//        // Check if we have write permission
//        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            // We don't have permission so prompt the user
//            ActivityCompat.requestPermissions(
//                    activity,
//                    PERMISSIONS_STORAGE,
//                    REQUEST_EXTERNAL_STORAGE
//            );
//        }
//    }
    public static void verifyStoragePermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check if we have write permission
            int storage = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int location = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
            int camera = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
            int telephone = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);
            if (storage != PackageManager.PERMISSION_GRANTED || location != PackageManager.PERMISSION_GRANTED || camera != PackageManager.PERMISSION_GRANTED|| telephone != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }
        }
    }

    public static int exportDataVault(Context mContext) {
        boolean flagforlog = false;
        try {
            FileWriter fileWriter = null;
            String jsonData = null;
            try {
                jsonData = Constants.makePendingDataToJsonString(mContext);
            } catch (Exception e) {
                e.printStackTrace();
                LogManager.writeLogError("exportDataVault() : " + e.getMessage());
                jsonData = "";
            }
            if (jsonData != null && !jsonData.equalsIgnoreCase("")) {
                fileWriter = new FileWriter(Environment.getExternalStorageDirectory()
                        + "/" + Constants.DataVaultFileName + "");
                fileWriter.write(jsonData);
                fileWriter.close();
//                flagforlog = true;
                return 3;
            } else {
//                Constants.ExportDataFailedErrorMsg = "No Pending Requests Available";
//                flagforlog = false;
                return 2;
            }


        } catch (IOException e) {
            e.printStackTrace();
            LogManager.writeLogError("exportDataVault() (IOException) : " + e.getMessage());
            return 1;
        }
    }

    /*Import Offline DB into application*/
    public static boolean importDB(Context mContext, UIListener uiListener) {
        if (OfflineManager.isOfflineStoreOpen()) {
            try {
                OfflineManager.closeOfflineStore();
                LogManager.writeLogError(mContext.getString(R.string.msg_sync_terminated));
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_during_offline_close + e.getMessage());
            }
        }

        File isd = Environment.getExternalStorageDirectory();
        File idata = Environment.getDataDirectory();
        FileChannel isource = null;
        FileChannel idestination = null;
        File ibackupDB = new File(idata, Constants.icurrentUDBPath);
        File icurrentDB = new File(isd, Constants.ibackupUDBPath);

        File ibackupRqDB = new File(idata, Constants.icurrentRqDBPath);
        File icurrentRqDB = new File(isd, Constants.ibackupRqDBPath);

//        File ibackupuDB = new File(idata, Constants.icurrentDBPath);
//        File icurrentuDB = new File(isd, Constants.ibackupDBPath);
        try {
            isource = new FileInputStream(icurrentDB).getChannel();
            idestination = new FileOutputStream(ibackupDB).getChannel();
            idestination.transferFrom(isource, 0, isource.size());

            isource = new FileInputStream(icurrentRqDB).getChannel();
            idestination = new FileOutputStream(ibackupRqDB).getChannel();
            idestination.transferFrom(isource, 0, isource.size());

            isource.close();
            if (!OfflineManager.isOfflineStoreOpen()) {
                try {
                    OfflineManager.openOfflineStore(mContext, uiListener);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppThemeNoActionBar);
        setContentView(R.layout.activity_main_menu);
        PrivateDataVault.init(MainMenu.this);
        LogManager.writeLogInfo("Login Loading Completed");
        LogManager.writeLogInfo("Main menu loaded");
        if (!Constants.restartApp(MainMenu.this)) {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            flBackDrop = (FrameLayout) findViewById(R.id.flBackDrop);
            ConstantsUtils.initActionBarView(this, toolbar, false, getString(R.string.lbl_main_menu), 0);
            sharedPerf = getSharedPreferences(Constants.PREFS_NAME, 0);
            permissionStatus = getSharedPreferences("permissionStatus", 0);
            if (getSupportActionBar() != null) {
                supportActionBar = getSupportActionBar();
                supportActionBar.setIcon(R.mipmap.ic_action_bar_logo);
            }
            bundleExtras = getIntent().getExtras();
            if (bundleExtras != null) {
                isFromRegistration = bundleExtras.getBoolean(UtilRegistrationActivity.EXTRA_IS_FROM_REGISTRATION, false);
                registrationModel = (RegistrationModel) bundleExtras.getSerializable(UtilConstants.RegIntentKey);
            }
            //firebase authentication
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
            String loginId = sharedPreferences.getString("username", "");
            SharedPreferences mPrefs = getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(getString(R.string.transport_id), loginId);
            editor.putString(getString(R.string.email), "teamarteria@gmail.com");
            editor.putString(getString(R.string.password), "Welcome4");
            editor.putString(getString(R.string.password), "Welcome4");
            editor.putString(getString(R.string.password), "Welcome4");
            editor.apply();

            uiListener = this;
            disableCollapse();
            EventUserDetail eventDataSqlHelper = new EventUserDetail(this);
            Constants.EventUserHandler = eventDataSqlHelper.getWritableDatabase();
            Constants.events = new EventDataSqlHelper(getApplicationContext());
//        setSharedPerfVal();
//        if (isFromRegistration) {
            verifyStoragePermissions(this);
//        }
            setDrawer();
            context = MainMenu.this;
            Constants.mApplication = (MSFAApplication) getApplication();
            if (savedInstanceState == null) {
                mainMenuFragment = new NavigationMenuFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.EXTRA_COME_FROM, 1);
                mainMenuFragment.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.ll_container, mainMenuFragment, NAV_FRAGMENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();

                daySummaryFragment = new DaySummaryFragment();
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flContainer, daySummaryFragment, DASHBOARD_FRAGMENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
                isRefresh = false;
            } else {
                mainMenuFragment = (NavigationMenuFragment) getSupportFragmentManager().getFragment(savedInstanceState, NAV_FRAGMENT_TAG);
                daySummaryFragment = (DaySummaryFragment) getSupportFragmentManager().getFragment(savedInstanceState, DASHBOARD_FRAGMENT_TAG);
            }
     /*   try {
            boolean isOnlineStoreOpen = OnlineManager.openOnlineStore(getApplicationContext());
        } catch (OnlineODataStoreException e) {
            e.printStackTrace();
        }*/

            //initializing viewpager

            // openNavigationDrawer();
            //initializing indicator
            // setIndicator();
            if (isFromRegistration) {
                pDialogStr = getString(R.string.preparing_app);
                intialSyncTime = Constants.getSyncHistoryddmmyyyyTime();
            } else {
                pDialogStr = getString(R.string.app_loading);
            }
            openInitialStore(MainMenu.this);
//        registerReceiver();

       /* if (isFromRegistration) {
            pDialogStr = getString(R.string.preparing_app);
            createSyncHistoryTable();
            openStore();
        } else if (!OfflineManager.isOfflineStoreOpen()) {
            pDialogStr = getString(R.string.app_lprogressDialogoading);
            openStore();
        } else {
            setUI();
        }*/
            TraceLog.initialize(this, context.getString(R.string.app_name));
            TraceLog.scoped(this).d(getString(R.string.msg_on_create));

            //Initializing Log Trace
            TraceLog.initialize(this, context.getString(R.string.app_name));
            TraceLog.scoped(this).d(getString(R.string.msg_on_create));
            try {
//                ConstantsUtils.serviceReSchedule(MainMenu.this, true);
            }catch (Exception e){
                e.printStackTrace();
            }
//            systemWindonPermission();
//            registerReceiver(receiver, new IntentFilter("android.location.PROVIDERS_CHANGED"));
        }
    }
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 5469;
    public void systemWindonPermission() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
                if (!Settings.canDrawOverlays(this)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
                    builder.setMessage(R.string.system_alter_message)
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                                    Uri.parse("package:" + getPackageName()));
                                            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
                                        }
                                    });


                    builder.setCancelable(false);
                    builder.show();

                } else {
                    //            showDialog();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openInitialStore(Context mContext) {
        if (!sharedPerf.getBoolean(Constants.isRollResponseGot, false)) {
            if (UtilConstants.isNetworkAvailable(MainMenu.this)) {
                pdLoadDialog = new ProgressDialog(mContext, R.style.UtilsDialogTheme);
                pdLoadDialog.setMessage(pDialogStr);
                pdLoadDialog.setCancelable(false);
                pdLoadDialog.show();

                try {
                    OnlineManager.getUserRollInfo(Constants.UserProfileAuthSet + "?$filter=Application%20eq%20'PD'", MainMenu.this, new AsyncTaskCallBack() {
                        @Override
                        public void onStatus(boolean status, String values) {
                            try {
                                pdLoadDialog.dismiss();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                                LogManager.writeLogError(Constants.error_txt + e1.getMessage());
                            }

                            if(status){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new OpenOfflineStoreAsync(true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                    }
                                });
                            }else {
                                mStrPopUpText = values;
                                isDialogBoxShowing = false;
                                displayPopUpMsg();
                            }
                        }
                    });// and AuthOrgTypeID eq '000014'

                } catch (Exception e) {
                    e.printStackTrace();
                }



                /*new OpenOnlineManagerStore(mContext, new AsyncTaskCallBack() {
                    @Override
                    public void onStatus(boolean status, String values) {
                        Log.d(TAG, "onStatus: OnlineStore" + status);
                        if (status) {
                            LogManager.writeLogInfo("Online Store opened");
                            try {
                               OnlineManager.getUserRollInfo(Constants.UserProfileAuthSet + "?$filter=Application eq 'PD'", MainMenu.this, new AsyncTaskCallBack() {
                                   @Override
                                   public void onStatus(boolean status, String values) {
                                       try {
                                           pdLoadDialog.dismiss();
                                       } catch (Exception e1) {
                                           e1.printStackTrace();
                                           LogManager.writeLogError(Constants.error_txt + e1.getMessage());
                                       }
                                       if(status){
                                           new OpenOfflineStoreAsync(true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                       }else {
                                           mStrPopUpText = values;
                                           isDialogBoxShowing = false;
                                           displayPopUpMsg();
                                       }
                                   }
                               });// and AuthOrgTypeID eq '000014'

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            mStrPopUpText = values;
                            isDialogBoxShowing = false;
                            displayPopUpMsg();
                        }
                    }
                }).execute();*/
            } else {
                UtilConstants.onNoNetwork(MainMenu.this);
            }
        } else {
            new OpenOfflineStoreAsync(false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void setUIwithError() {
        try {
            AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, MainMenu.this, BuildConfig.APPLICATION_ID, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        setUpViewPager();
        openNavigationDrawer();
    }

    private void setUI() {
        setUIwithError();
        pDialogStr = getString(R.string.app_loading);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, NAV_FRAGMENT_TAG, mainMenuFragment);
        getSupportFragmentManager().putFragment(outState, DASHBOARD_FRAGMENT_TAG, daySummaryFragment);
    }

    private void openNavigationDrawer() {
        reloadSlider();
        reloadDashboard();
    }

    //    private void reloadSlider() {
//        if (mainMenuFragment != null)
//            mainMenuFragment.onRefresh();
//    }
    private void reloadDashboard() {
        if (daySummaryFragment != null)
            daySummaryFragment.TargetSync();
    }

   /* private void openStore() {
        if (OfflineManager.offlineStore != null) {
            if (!OfflineManager.isOfflineStoreOpen()) {
                new OpenOfflineStoreAsync().execute();
            }
        } else {
            try {
                new OpenOfflineStoreAsync().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        if (isRefresh) {
            reloadDashboard();
            reloadSlider();
        }
        isRefresh = true;
    }

    private void reloadSlider() {
        if (mainMenuFragment != null)
            mainMenuFragment.onRefresh();
    }

    /*Setting Viewpager*/
  /*  private void setUpViewPager() {
        mainMenuPagerAdapter = new MainMenuPagerAdapter(getSupportFragmentManager());
        mainMenuPagerAdapter.addFrag(new DaySummaryFragment());
        //  mainMenuPagerAdapter.addFrag(new SecondPageFragment());
        pagerMainMenu = (ViewPager) findViewById(R.id.pager_main_menu);
        pagerMainMenu.setAdapter(mainMenuPagerAdapter);
        pagerMainMenu.setCurrentItem(0);
        // initPagerIndicator();
    }*/

    private void createSyncHistoryTable() {
        if (!Constants.syncHistoryTableExist()) {
            try {
                Constants.createSyncDatabase(MainMenu.this);  // create sync history table
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setDrawer() {
       /* supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setIcon(R.mipmap.ic_action_bar_logo);
        }*/
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    /*Setting indicator for selected page*/
  /*  private void setIndicator() {

        pagerMainMenu.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int position) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            *//*executes when page changed on viewpager*//*
            @Override
            public void onPageSelected(int position) {
                ivIndicatorPage1.setImageResource(R.drawable.holo_circle);
                ivIndicatorPage2.setImageResource(R.drawable.holo_circle);
                indicatorPagerAction(position);
            }

        });
    }*/

    /*Initializing Pager indicator*/
    private void initPagerIndicator() {

        try {
            ivIndicatorPage1 = (ImageView) findViewById(R.id.iv_indicator1);
            ivIndicatorPage1.setImageResource(R.drawable.fill_circle);
            ivIndicatorPage2 = (ImageView) findViewById(R.id.iv_indicator2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Setting Icon to indicator for selected page*/
    private void indicatorPagerAction(int position) {
        switch (position) {
            case 0:
                ivIndicatorPage1.setImageResource(R.drawable.fill_circle);
                break;

            case 1:
                ivIndicatorPage2.setImageResource(R.drawable.fill_circle);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (mBooleanIsApplicationExited) {
                mBooleanIsApplicationExited = false;
            }
            if (mRegistrationBroadcastReceiver != null) {
                // register GCM registration complete receiver
                LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                        new IntentFilter(NotificationConfig.REGISTRATION_COMPLETE));
                LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                        new IntentFilter(NotificationConfig.PUSH_NOTIFICATION));
            }
            if (!ConstantsUtils.isAutomaticTimeZone(MainMenu.this)) {
                ConstantsUtils.showAutoDateSetDialog(MainMenu.this);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*UIListener on Error*/
    @Override
    public void onRequestError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e, MainMenu.this);
        //  if (!errorBean.getErrorMsg().contains("10340")){
        String customeNEO = Constants.checkUnknownNetworkerror(errorBean.getErrorMsg(), MainMenu.this);
        if (!TextUtils.isEmpty(errorBean.getErrorMsg()) && errorBean.getErrorMsg().contains("10340") && !isRefreshDB) {
            if(pdLoadDialog!=null && pdLoadDialog.isShowing())
                pdLoadDialog.dismiss();
            pDialogStr = "Updating applicaiton please wait";
            pdLoadDialog = new ProgressDialog(MainMenu.this, R.style.UtilsDialogTheme);
            pdLoadDialog.setMessage(pDialogStr);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
            isRefreshDB = true;
            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                Constants.isSync = true;
                Constants.closeStore(MainMenu.this);
                new RefreshAsyncTask(MainMenu.this, "", this).execute();
            }
            /*if (OfflineManager.isOfflineStoreOpen()) {
                try {
                    OfflineManager.closeOfflineStore();
                    LogManager.writeLogError(getString(R.string.msg_sync_terminated));
                } catch (OfflineODataStoreException ez) {
                    LogManager.writeLogError(Constants.error_during_offline_close + ez.getMessage());
                }
            }

            if (!OfflineManager.isOfflineStoreOpen()) {
                try {
                    OfflineManager.openOfflineStore(MainMenu.this, uiListener);
                } catch (OfflineODataStoreException ec) {
                    ec.printStackTrace();
                }
            }*/
        }else{
            if (customeNEO.equalsIgnoreCase("")) {
                if (errorBean.hasNoError()) {
                    Toast.makeText(MainMenu.this, getString(R.string.err_odata_unexpected, e.getMessage()),
                            Toast.LENGTH_LONG).show();
        /*if (mBooleanDayStartDialog)
            mStrPopUpText = getString(R.string.msg_start_upd_sync_error);
        if (mBooleanDayEndDialog)
            mStrPopUpText = getString(R.string.msg_end_upd_sync_error);
        if (mBooleanDayResetDialog)
            mStrPopUpText = getString(R.string.msg_reset_upd_sync_error);*/
//            mStrPopUpText = getString(R.string.alert_sync_cannot_be_performed);

                    if (mStrPopUpText.equalsIgnoreCase("")) {
                        try {
                            mStrPopUpText = errorBean.getErrorMsg();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (TextUtils.isEmpty(mStrPopUpText)) {
                        mStrPopUpText = getString(R.string.alert_sync_cannot_be_performed);
                    }
                    if (operation == Operation.Create.getValue()) {
                        try {
                            pdLoadDialog.dismiss();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        displayPopUpMsg();
                    } else if (operation == Operation.Update.getValue()) {
                        try {
                            pdLoadDialog.dismiss();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        displayPopUpMsg();
                    } else if (operation == Operation.OfflineFlush.getValue()) {
                        try {
                            pdLoadDialog.dismiss();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                        displayPopUpMsg();
                    } else if (operation == Operation.OfflineRefresh.getValue()) {
                        try {
                            pdLoadDialog.dismiss();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                        displayPopUpMsg();
                    } else if (operation == Operation.GetStoreOpen.getValue()) {
                        try {
                            try {
                                pdLoadDialog.dismiss();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            displayPopUpMsg();
                        } catch (Exception exc) {
                            exc.printStackTrace();
                        }
                    }
                } else if (errorBean.isStoreFailed()) {
                    if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                        Constants.isSync = true;
                        new RefreshAsyncTask(MainMenu.this, "", this).execute();
                    } else {
                        Constants.isSync = false;
                        try {
                            pdLoadDialog.dismiss();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        Constants.displayMsgReqError(errorBean.getErrorCode(), MainMenu.this);
                    }
                } else {
                    try {
                        pdLoadDialog.dismiss();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    Constants.displayMsgReqError(errorBean.getErrorCode(), MainMenu.this);
                }
    /*}else{
            if(pdLoadDialog!=null && pdLoadDialog.isShowing())
                pdLoadDialog.dismiss();
            if (OfflineManager.isOfflineStoreOpen()) {
                try {
                    OfflineManager.closeOfflineStore();
                    LogManager.writeLogError(getString(R.string.msg_sync_terminated));
                } catch (OfflineODataStoreException ez) {
                    LogManager.writeLogError(Constants.error_during_offline_close + ez.getMessage());
                }
            }

            if (!OfflineManager.isOfflineStoreOpen()) {
                try {
                    OfflineManager.openOfflineStore(MainMenu.this, uiListener);
                } catch (OfflineODataStoreException ec) {
                    ec.printStackTrace();
                }
            }

        }*/
            } else {
                UtilConstants.showAlert(customeNEO, MainMenu.this);
            }
        }

    }

    /*private void setSharedPerfVal() {
        SharedPreferences.Editor editor = sharedPerf.edit();
        try {
            if (!sharedPerf.contains(Constants.CURRENT_VERSION_CODE)) {
                editor.putInt(Constants.CURRENT_VERSION_CODE, Constants.NewDefingRequestVersion);
                editor.apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /*UIListener on Success*/
    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException,
            OfflineODataStoreException {
        if (operation == Operation.Create.getValue()) {
            if (Constants.getSyncType(getApplicationContext(), Constants.Attendances,
                    Constants.CreateOperation).equalsIgnoreCase("4")) {

                try {
                    pdLoadDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mBooleanDayStartDialog)
                    mStrPopUpText = getString(R.string.dialog_day_started);
                if (mBooleanDayEndDialog)
                    mStrPopUpText = getString(R.string.dialog_day_ended);
                if (mBooleanDayResetDialog)
                    mStrPopUpText = getString(R.string.dialog_day_reset);

                displayPopUpMsg();
            } else {
                if (!UtilConstants.isNetworkAvailable(MainMenu.this)) {
                    try {
                        pdLoadDialog.dismiss();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    UtilConstants.onNoNetwork(MainMenu.this);
                } else {
                    OfflineManager.flushQueuedRequests(MainMenu.this);
                }
            }
        } else if (operation == Operation.Update.getValue()) {
            if (Constants.getSyncType(getApplicationContext(), Constants.Attendances,
                    Constants.UpdateOperation).equalsIgnoreCase("4")) {

                try {
                    pdLoadDialog.dismiss();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                if (mBooleanDayStartDialog)
                    mStrPopUpText = getString(R.string.dialog_day_started);
                if (mBooleanDayEndDialog)
                    mStrPopUpText = getString(R.string.dialog_day_ended);
                if (mBooleanDayResetDialog)
                    mStrPopUpText = getString(R.string.dialog_day_reset);

                displayPopUpMsg();
            } else {
                if (!UtilConstants.isNetworkAvailable(MainMenu.this)) {
                    try {
                        pdLoadDialog.dismiss();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    UtilConstants.onNoNetwork(MainMenu.this);
                } else {
                    OfflineManager.flushQueuedRequests(MainMenu.this);
                }
            }

        } else if (operation == Operation.OfflineFlush.getValue()) {

            if (Constants.getSyncType(getApplicationContext(), Constants.Attendances,
                    Constants.ReadOperation).equalsIgnoreCase("4")) {
                try {
                    pdLoadDialog.dismiss();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                if (mBooleanDayStartDialog)
                    mStrPopUpText = getString(R.string.dialog_day_started);
                if (mBooleanDayEndDialog)
                    mStrPopUpText = getString(R.string.dialog_day_ended);
                if (mBooleanDayResetDialog)
                    mStrPopUpText = getString(R.string.dialog_day_reset);

                displayPopUpMsg();
            } else {
                if (!UtilConstants.isNetworkAvailable(MainMenu.this)) {
                    try {
                        pdLoadDialog.dismiss();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    UtilConstants.onNoNetwork(MainMenu.this);
                } else {

                    String allCollection = "";
                    if (mBooleanDayStartDialog) {
                        allCollection = Constants.Attendances + "," + Constants.SPStockItems + ","
                                + Constants.SPStockItemDetails + "," + Constants.SPStockItemSNos + "," + Constants.SFINVOICES + "," + Constants.SSInvoiceItemDetails
                                + "," + Constants.SSInvoiceItemSerials + "," + Constants.FinancialPostings
                                + "," + Constants.FinancialPostingItemDetails
                                + "," + Constants.CPStockItems + "," + Constants.CPStockItemDetails + "," + Constants.CPStockItemSnos + "," + Constants.Schemes + "," + Constants.Tariffs + "," + Constants.SegmentedMaterials;
                    } else {
                        allCollection = Constants.Attendances;
                    }


                    OfflineManager.refreshRequests(getApplicationContext(), allCollection, MainMenu.this);
                }
            }


        } else if (operation == Operation.OfflineRefresh.getValue()) {
            try {
                pdLoadDialog.dismiss();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            if (mBooleanDayStartDialog)
                mStrPopUpText = getString(R.string.dialog_day_started);
            if (mBooleanDayEndDialog)
                mStrPopUpText = getString(R.string.dialog_day_ended);
            if (mBooleanDayResetDialog)
                mStrPopUpText = getString(R.string.dialog_day_reset);

            displayPopUpMsg();
        } else if (operation == Operation.GetStoreOpen.getValue()) {


           /* if (sharedPerf.getBoolean(Constants.isFirstTimeReg, false)) {
                SharedPreferences.Editor editor = sharedPerf.edit();
                editor.putBoolean(Constants.isFirstTimeReg, false);
                editor.commit();

                try {
                    OfflineManager.getAuthorizations(getApplicationContext());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }

                try {
                    String syncTime = Constants.getSyncHistoryddmmyyyyTime();
                    String[] DEFINGREQARRAY = Constants.getDefinigReq(getApplicationContext());


                    for (int incReq = 0; incReq < DEFINGREQARRAY.length; incReq++) {
                        String colName = DEFINGREQARRAY[incReq];
                        if (colName.contains("?$")) {
                            String splitCollName[] = colName.split("\\?");
                            colName = splitCollName[0];
                        }

                        Constants.events.updateStatus(Constants.SYNC_TABLE,
                                colName, Constants.TimeStamp, syncTime
                        );
                    }
                } catch (Exception exce) {
                    LogManager.writeLogError(Constants.sync_table_history_txt + exce.getMessage());
                }

            }

            Constants.setSyncTime(MainMenu.this);
            ConstantsUtils.startAutoSync(MainMenu.this, true);
            if (UtilConstants.isNetworkAvailable(MainMenu.this)) {
                new OpenOnlineManagerStore(MainMenu.this, new AsyncTaskCallBack() {
                    @Override
                    public void onStatus(boolean status, String values) {
                        try {
                            pdLoadDialog.dismiss();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            LogManager.writeLogError(Constants.error_txt + e1.getMessage());
                        }
                        if (status) {
                            setAppointmentNotification();
                            setUIwithError();
                        } else {
                            mStrPopUpText = values;
                            displayPopUpMsg();
                        }
                    }
                }).execute();
            } else {
                try {
                    pdLoadDialog.dismiss();
                    setAppointmentNotification();
                    setUIwithError();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    LogManager.writeLogError(Constants.error_txt + e1.getMessage());
                }
//                UtilConstants.showAlert(getString(R.string.err_no_network), MainMenu.this);
            }*/
//            mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
//            Constants.getDataFromSqliteDB(this);
//            startBackGroundService();
//            ConstantsUtils.startAutoSyncLatLong(MainMenu.this, true);
            if (sharedPerf.getInt(Constants.CURRENT_VERSION_CODE, 0) == Constants.NewDefingRequestVersion) {
                if (sharedPerf.getInt(Constants.INTIALIZEDB, 0) == Constants.IntializeDBVersion) {
                    refreshStore();
                } else {
                    if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                        Constants.isSync = true;
                        increaseSharedPerfVal(Constants.INTIALIZEDB, Constants.IntializeDBVersion);
                        Constants.closeStore(MainMenu.this);
                        new RefreshAsyncTask(MainMenu.this, "", this).execute();
                    } else {
                        refreshStore();
                    }
                }
            } else {
                if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                    if(OfflineManager.offlineStore!=null) {
                        if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                            try {
                                OfflineManager.flushQueuedRequests(new UIListener() {
                                    @Override
                                    public void onRequestError(int i, Exception e) {
                                        refreshStore();
                                    }

                                    @Override
                                    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                                        increaseSharedPerfVal(Constants.CURRENT_VERSION_CODE, Constants.NewDefingRequestVersion);
                                        Constants.closeStore(MainMenu.this);
                                        new RefreshAsyncTask(MainMenu.this, "", MainMenu.this).execute();
                                    }
                                }, "");
                            } catch (OfflineODataStoreException e) {
                                refreshStore();
                                e.printStackTrace();
                            }
                        } else {
                            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                                Constants.isSync = true;
                                increaseSharedPerfVal(Constants.CURRENT_VERSION_CODE, Constants.NewDefingRequestVersion);
                                Constants.closeStore(MainMenu.this);
                                new RefreshAsyncTask(MainMenu.this, "", this).execute();
                            } else {
                                refreshStore();
                            }
                        }
                    }else {
                        refreshStore();
                    }
                } else {
                    refreshStore();
                }
            }

            String enableGeo = "";
            enableGeo = OfflineManager.getValueByColumnName("ConfigTypsetTypeValues?$filter=Typeset eq 'SP' and Types eq 'ENABLEGEO'", Constants.TypeValue);
            if (enableGeo.equalsIgnoreCase("X")){
                try {
                    refguid = GUID.newRandom();
                    // Todo commented on 18-01-2020 bt saikrishna
//                Constants.updateStartSyncTime(MainMenu.this,Constants.Geo_sync,Constants.StartSync,refguid.toString().toUpperCase());
                    new SyncGeoAsyncTask(MainMenu.this,refguid.toString().toUpperCase(), new MessageWithBooleanCallBack() {
                        @Override
                        public void clickedStatus(boolean clickedStatus, String errorMsg, ErrorBean errorBean) {
                            Log.d("Geo Offline : "," opened");
                            Log.d("clickedStatus Req", clickedStatus + "");
                            setUI();
                        }
                    }, Constants.All).execute();
                } catch (Exception e) {
                    setUI();
                    e.printStackTrace();
                }
            }
            else{
                Log.d("Geo Offline : "," not opened");
            }


        }

    }

    private void increaseSharedPerfVal(String versionCode, int version) {
        SharedPreferences.Editor editor = sharedPerf.edit();
        try {
            editor.putInt(versionCode, version);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDialogMessage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    pdLoadDialog = new ProgressDialog(MainMenu.this, R.style.UtilsDialogTheme);
                    pdLoadDialog.setMessage(getString(R.string.update_app_message));
                    pdLoadDialog.setCancelable(false);
                    pdLoadDialog.show();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    LogManager.writeLogError(Constants.error_txt + e1.getMessage());
                }
            }
        });
    }
    private void storeLoginIdToSp() {
        String loginId = "";
        loginId = OfflineManager.getLoginID("UserProfileAuthSet?$filter=Application%20eq%20%27PD%27");
        if(!TextUtils.isEmpty(loginId)) {
            SharedPreferences.Editor userNameEditor = sharedPerf.edit();
            userNameEditor.putString(Constants.username, loginId.toUpperCase());
            userNameEditor.putString(Constants.usernameExtra, loginId.toUpperCase());
            userNameEditor.apply();
        }else{
            LogManager.writeLogError("MainMenu Oflline query for UserAuthSet login Id returns null or empty"+":"+loginId);
        }
    }

    private void refreshStore() {
        try {
            String userLoginId = sharedPerf.getString("username", "");
            if(isFromRegistration) {
                storeLoginIdToSp();
            }else if(TextUtils.isEmpty(userLoginId)){
                storeLoginIdToSp();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

//        getUserPartnerDetails();

//        ConstantsUtils.startAutoSync(MainMenu.this, true);
        //    ConstantsUtils.startAutoSyncLocation(MainMenu.this, true); //TODo For location capturing
//        createSyncHistoryTable();
        refguid = GUID.newRandom();
        //  Constants.updateStartSyncTime(this,Constants.Sync_All,Constants.StartSync,refguid.toString().toUpperCase());
        if (sharedPerf.getBoolean(Constants.isFirstTimeReg, false)) {
            SharedPreferences.Editor editor = sharedPerf.edit();
            editor.putBoolean(Constants.isFirstTimeReg, false);
            editor.commit();

            try {
                OfflineManager.getAuthorizations(getApplicationContext());
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            try {

                List<String > DEFINGREQARRAY = Arrays.asList(Constants.getDefinigReq(getApplicationContext()));
                Constants.updateStartInitialSyncTime(this,Constants.Initial_sync,Constants.StartSync,refguid.toString().toUpperCase(),intialSyncTime);
                Constants.updateSyncTime(DEFINGREQARRAY,this,Constants.Initial_sync,refguid.toString().toUpperCase());
                /*String syncTime = Constants.getSyncHistoryddmmyyyyTime();
                String[] DEFINGREQARRAY = Constants.getDefinigReq(getApplicationContext());


                for (int incReq = 0; incReq < DEFINGREQARRAY.length; incReq++) {
                    String colName = DEFINGREQARRAY[incReq];
                    if (colName.contains("?$")) {
                        String splitCollName[] = colName.split("\\?");
                        colName = splitCollName[0];
                    }

                    Constants.events.updateStatus(Constants.SYNC_TABLE,
                            colName, Constants.TimeStamp, syncTime
                    );
                }*/
            } catch (Exception exce) {
                LogManager.writeLogError(Constants.sync_table_history_txt + exce.getMessage());
            }

            if (UtilConstants.isNetworkAvailable(MainMenu.this)) {
                new OpenOnlineManagerStore(MainMenu.this, new AsyncTaskCallBack() {
                    @Override
                    public void onStatus(boolean status, String values) {
                        try {
                            if (pdLoadDialog!=null&&pdLoadDialog.isShowing()) {
                                pdLoadDialog.dismiss();
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            LogManager.writeLogError(Constants.error_txt + e1.getMessage());
                        }
                        if (status) {
                            setAppointmentNotification();
                            setUIwithError();
                        } else {
                            mStrPopUpText = values;
                            displayPopUpMsg();
                        }
                    }
                }).execute();
            } else {
                try {
                    if (pdLoadDialog!=null&&pdLoadDialog.isShowing()) {
                        pdLoadDialog.dismiss();
                    }
                    setAppointmentNotification();
                    setUIwithError();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    LogManager.writeLogError(Constants.error_txt + e1.getMessage());
                }
//                UtilConstants.showAlert(getString(R.string.err_no_network), MainMenu.this);
            }
        } else {
            try {
                OfflineManager.getAuthorizations(MainMenu.this);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            if (UtilConstants.isNetworkAvailable(MainMenu.this)) {
                new OpenOnlineManagerStore(MainMenu.this, new AsyncTaskCallBack() {
                    @Override
                    public void onStatus(boolean status, String values) {
                        try {
                            if (pdLoadDialog!=null&&pdLoadDialog.isShowing()) {
                                pdLoadDialog.dismiss();
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            LogManager.writeLogError(Constants.error_txt + e1.getMessage());
                        }
                        if (status) {
                            setAppointmentNotification();
                            setUIwithError();
                        } else {
                            mStrPopUpText = values;
                            displayPopUpMsg();
                        }
                    }
                }).execute();
            } else {
                try {
                    if (pdLoadDialog!=null&&pdLoadDialog.isShowing()) {
                        pdLoadDialog.dismiss();
                    }
                    setAppointmentNotification();
                    setUIwithError();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    LogManager.writeLogError(Constants.error_txt + e1.getMessage());
                }
//                UtilConstants.showAlert(getString(R.string.err_no_network), MainMenu.this);
            }
        }
        Constants.setSyncTime(MainMenu.this,refguid.toString().toUpperCase());
//        TODo For location capturing
//        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(MainMenu.this));

        try {

            String firstTime = "";
            String endTime = "";
            String autoSyncTime = "";
            String lastSyncDate = "";
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
            firstTime = sharedPreferences.getString("FIRSTAUTOSYNCTIME", "");
            autoSyncTime = sharedPreferences.getString("AUTOSYNCTIME", "");
            lastSyncDate = sharedPreferences.getString("LASTSYNCDATE", "");
            try {
//                Date date = new Date();
//                Calendar c = Calendar.getInstance();
//                c.setTime(date);
//                date = c.getTime();
//                //else for example date1 = 01:55, date2 = 03:55.
//                long ms = date.getTime();
//                long diffMinutes = ms / (60 * 1000) % 60;
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                endTime = sdf.format(cal.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }

            long diffTime = 0;
            long finalTime = 0;
            if (!TextUtils.isEmpty(firstTime) && !TextUtils.isEmpty(endTime)) {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                Date date1 = format.parse(firstTime);
                Date date2 = format.parse(endTime);
                diffTime = date2.getTime() - date1.getTime();
                finalTime = diffTime / (60 * 1000) % 60;
            }
            int intFirstTime = (int) finalTime;
            int intAutoSyncTime = 0;
            if (!TextUtils.isEmpty(autoSyncTime)) {
                intAutoSyncTime = Integer.parseInt(autoSyncTime);
            }

            if (!TextUtils.isEmpty(autoSyncTime)) {
                if (lastSyncDate.equalsIgnoreCase(UtilConstants.getNewDate())) {
                    if (intFirstTime >= intAutoSyncTime) {
                        try {
                            LogManager.writeLogError("Skip AutoSync Started");
                            //  UpdatePendingRequest.getInstance(null).callScheduleFirstLoginSync();
                        } catch (Exception e) {
                            LogManager.writeLogError("UpdatePendingRequest : " + e.toString());
                            e.printStackTrace();
                        }
                        ConstantsUtils.startAutoSync(MainMenu.this, true);
                    }
                } else {
                    ConstantsUtils.startAutoSync(MainMenu.this, true);
                }
            } else {
                LogManager.writeLogError("AutoSync Started Date Not Matched");
                ConstantsUtils.startAutoSync(MainMenu.this, true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        boolean datavault = PrivateDataVault.vaultExists(Configuration.APP_ID);
        if(!sharedPerf.getBoolean(Constants.DataVaultUpdate,false)) {
            if (datavault) {
                LogManager.writeLogInfo("Vault is exists");
                datavaultData(MainMenu.this);
//            Toast.makeText(mContext, "Vault is exists", Toast.LENGTH_SHORT).show();
            } else {
                LogManager.writeLogInfo("Vault is not exists");
//            Toast.makeText(mContext, "Vault is not exists", Toast.LENGTH_SHORT).show();
            }
        }

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (reentrantLock==null){
                        reentrantLock = new ReentrantLock();
                    }
                    try {
                        Log.e("Main Menu REENTRANT:","LOCKED");
                        reentrantLock.lock();
                        Constants.getDataFromSqliteDB(getApplicationContext(),null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Main Menu EXCEPTION","ANR EXCEPTION OCCURRED");
                    }finally {
                        if (reentrantLock!=null&&reentrantLock.isHeldByCurrentThread())reentrantLock.unlock();
                        Log.e("Main Menu REENTRANT:","UNLOCKED FINALLY");
                    }
                }
            }).start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
//        systemWindonPermission();
//        startBackGroundService();
    }

    /*private void getUserPartnerDetails() {
        String userPartnerID = sharedPerf.getString(Constants.USERPARNTERID,"");
        try {
            if(TextUtils.isEmpty(userPartnerID)){
                if (UtilConstants.isNetworkAvailable(MainMenu.this)) {
                    OnlineStoreListener openListener = OnlineStoreListener.getInstance();
                    OnlineODataStore store = openListener.getStore();
                    final String userLoginId = sharedPerf.getString("username", "");
                    if (store != null) {
                        String parentID = "";
                        *//*String partnerID = sharedPerf.getString(Constants.USERROLELOGINID,"");*//*
                        if (!TextUtils.isEmpty(userLoginId)) {
                            try {
                                parentID = OnlineManager.getUserPartnersnfo(Constants.UserPartners + "?$filter= LoginID eq'" + userLoginId + "'", MainMenu.this);// and AuthOrgTypeID eq '000014'
                                SharedPreferences.Editor editor = sharedPerf.edit();
                                editor.putString(Constants.USERPARNTERID, parentID);
                                editor.apply();
                            } catch (OnlineODataStoreException e) {
                                e.printStackTrace();
                            } catch (ODataContractViolationException e) {
                                e.printStackTrace();
                            } catch (ODataParserException e) {
                                e.printStackTrace();
                            } catch (ODataNetworkException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        if (UtilConstants.isNetworkAvailable(MainMenu.this)) {
                            new OpenOnlineManagerStore(MainMenu.this, new AsyncTaskCallBack() {
                                @Override
                                public void onStatus(boolean status, String values) {
                                    if (status) {
                                        String parentID = "";
                                        *//* String login = sharedPerf.getString(Constants.USERROLELOGINID,"");
     *//*
                                        if (!TextUtils.isEmpty(userLoginId)) {
                                            try {
                                                parentID = OnlineManager.getUserPartnersnfo(Constants.UserPartners + "?$filter= LoginID eq'" + userLoginId + "'", MainMenu.this);// and AuthOrgTypeID eq '000014'
                                                SharedPreferences.Editor editor = sharedPerf.edit();
                                                editor.putString(Constants.USERPARNTERID, parentID);
                                                editor.apply();
                                            } catch (OnlineODataStoreException e) {
                                                e.printStackTrace();
                                            } catch (ODataContractViolationException e) {
                                                e.printStackTrace();
                                            } catch (ODataParserException e) {
                                                e.printStackTrace();
                                            } catch (ODataNetworkException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } else {

                                    }
                                }
                            }).execute();
                        }
                    }
                }else {
                    String parentID = "";
                    String strSPGUID = Constants.getSPGUID(Constants.SPGUID);
                    if (!TextUtils.isEmpty(strSPGUID)) {
                        String StrSPGUID32 = strSPGUID.replaceAll("-", "");
                        try {
                            if(Arrays.asList(Constants.getDefinigReq(MainMenu.this)).contains(Constants.UserPartners)) {
                                parentID = OfflineManager.getPartnerTypeID(Constants.UserPartners + "?$filter= PartnerID eq'" + StrSPGUID32 + "'");
                                SharedPreferences.Editor editor = sharedPerf.edit();
                                editor.putString(Constants.USERPARNTERID, parentID);
                                editor.apply();
                            }
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


    private void setAppointmentNotification() {
        new NotificationSetClass(this);

    }

    /*Displays message in alert dialog*/
    public void displayPopUpMsg() {
        if (!isDialogBoxShowing) {
            UtilConstants.dialogBoxWithCallBack(MainMenu.this, "", mStrPopUpText, getString(R.string.ok), "", false, new DialogCallBack() {
                @Override
                public void clickedStatus(boolean b) {
                    isDialogBoxShowing = false;
                }
            });
            isDialogBoxShowing = true;
        }
//        UtilConstants.showAlert(mStrPopUpText, MainMenu.this);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
        builder.setMessage(R.string.do_u_want_exit_app)
                .setCancelable(false)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Constants.iSAutoSync = false;
                                UpdatePendingRequest.instance = null;
                                if (OfflineManager.offlineStore != null) {
                                    try {
                                        OfflineManager.offlineStore.closeStore();
                                    } catch (ODataException e) {
                                        e.printStackTrace();
                                    }
                                }
                                OfflineManager.offlineStore = null;
                                OfflineManager.options = null;
                                OfflineManager.optionsGeo = null;
                                OfflineManager.offlineGeo = null;
                                android.os.Process.killProcess(android.os.Process.myPid());
//                                finishAffinity();
                                System.exit(1);
                            }
                        })
                .setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });


        builder.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_mainmenu_export:
                exportOfflineDB(MainMenu.this);
                break;
            case R.id.menu_mainmenu_import:
                importOfflineDB(MainMenu.this);
                break;
            case R.id.menu_mainmenu_aboutus:
                onAboutUs();
                break;

            case R.id.menu_mainmenu_log:
                onLog();
                break;
            case R.id.menu_mainmenu_settings:
                opensettingsFragment();
                // setActionBarTitle(getString(R.string.settings), true, false);
                return true;
            case R.id.menu_support:
                onSupport();
                return true;
            case R.id.menu_exportdatavault:
                if (Constants.isReadWritePermissionEnabled(MainMenu.this, MainMenu.this)) {
                    exportDatavaultData(MainMenu.this);
                }
                break;
            case R.id.menu_importdatavault:
                if (Constants.isReadWritePermissionEnabled(MainMenu.this, MainMenu.this)) {
                    importDatavaultData();
                }
                break;

            /*case R.id.att:
                Intent intent = new Intent(this, AttendanceSummaryActivity.class);
                startActivity(intent);
                break;*/
        }
        return true;
    }

    private void importDatavaultData() {
        pdLoadDialog = new ProgressDialog(MainMenu.this, R.style.UtilsDialogTheme);
        pdLoadDialog.setMessage(getString(R.string.import_datavault_data_from_sdcard));
        pdLoadDialog.setCancelable(false);
        pdLoadDialog.show();
        new Thread(new Runnable() {
            public void run() {
                String message = getString(R.string.import_datavault_from_sdcard_finish);

                try {
                    boolean isFileExists = Constants.isFileExits(Constants.DataVaultFileName);
                    if (isFileExists) {
                        String datavaultData = Constants.getTextFileData(Constants.DataVaultFileName);
                        Constants.setJsonStringDataToDataVault(datavaultData, MainMenu.this);
                    } else {
                        message = getString(R.string.file_not_exist);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogManager.writeLogError("importDatavaultData() (InterruptedException): " + e.getMessage());
                    message = getString(R.string.import_datavault_from_sdcard_error_occurred);
                }
                final String finalMessage = message;
                runOnUiThread(new Runnable() {
                    public void run() {
                        pdLoadDialog.cancel();
                        ConstantsUtils.displayLongToast(context, finalMessage);
                    }
                });
            }
        }).start();

    }

    private void exportDatavaultData(final Context mContext) {
        pdLoadDialog = new ProgressDialog(MainMenu.this, R.style.UtilsDialogTheme);
        pdLoadDialog.setMessage(getString(R.string.export_datavault_data_to_storage));
        pdLoadDialog.setCancelable(false);
        pdLoadDialog.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    final int isExportData = exportDataVault(mContext);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pdLoadDialog.dismiss();
                            String msg = mContext.getString(R.string.export_datavault_to_sdcard_error_occurred);
                            if (isExportData == 3) {//success
                                msg = mContext.getString(R.string.export_datavault_to_sdcard_finish);
                                Constants.removePendingList(mContext);
                            } else if (isExportData == 2) {
                                msg = "No Pending Requests Available";
                            }
                            ConstantsUtils.displayLongToast(context, msg);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void importOfflineDB(final Context mainMenu) {
        pdLoadDialog = new ProgressDialog(MainMenu.this, R.style.UtilsDialogTheme);
        pdLoadDialog.setMessage(getString(R.string.import_databse_from_sdcard));
        pdLoadDialog.setCancelable(false);
        pdLoadDialog.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    final boolean isImportDB = importDB(MainMenu.this, MainMenu.this);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pdLoadDialog.cancel();
                            String msg = mainMenu.getString(R.string.import_databse_from_sdcard_error_occurred);
                            if (isImportDB) {//success
                                msg = mainMenu.getString(R.string.import_databse_from_sdcard_finish);
                            }
                            ConstantsUtils.displayLongToast(context, msg);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void exportOfflineDB(final Context mContext) {
        pdLoadDialog = new ProgressDialog(MainMenu.this, R.style.UtilsDialogTheme);
        pdLoadDialog.setMessage(getString(R.string.export_databse_to_sdcard));
        pdLoadDialog.setCancelable(false);
        pdLoadDialog.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    final boolean isExportDB = exportDB(mContext);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pdLoadDialog.cancel();
                            String msg = mContext.getString(R.string.export_databse_to_sdcard_error_occurred);
                            if (isExportDB) {//success
                                msg = mContext.getString(R.string.export_databse_to_sdcard_finish);
                            }
                            ConstantsUtils.displayLongToast(context, msg);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void onSupport() {
        if (registrationModel != null) {
            Intent intent = new Intent(this, com.arteriatech.mutils.registration.SupportActivity.class);
            registrationModel.setDisplayDBReInitMenu(true);
            registrationModel.setDisplayImportMenu(false);
            registrationModel.setDisplayExportMenu(true);
            registrationModel.setDisplayExportDataMenu(true);
            registrationModel.setDisplayImportDataMenu(true);
            registrationModel.setDataVaultPassword(Constants.EncryptKey);
            registrationModel.setAlEntityNames(Constants.getEntityNames());
            intent.putExtra(UtilConstants.RegIntentKey, registrationModel);
            startActivityForResult(intent, REQUEST_SUPPORT);
        }
    }

    public void setActionBarTitle(String title, boolean showBackButton, boolean showAppIcon) {
        if (supportActionBar != null) {
            supportActionBar.setTitle(title);
//            enableViews(showBackButton, showAppIcon);
        }
    }

/*    private void openFragment(Fragment mainMenuFragment) {
    public void setActionBarSubTitle(String subTitle) {
        if (supportActionBar != null) {
            supportActionBar.setSubtitle(subTitle);
        }
    }
    private void openFragment(Fragment mainMenuFragment) {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.flContainer);
        if (current == null || !current.getClass().equals(mainMenuFragment.getClass())) {
//            disableCollapse();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mainMenuFragment.setArguments(bundleExtras);
            fragmentTransaction.add(R.id.flContainer, mainMenuFragment, mainMenuFragment.getClass().getName());
            if (current != null)
                fragmentTransaction.hide(current);
            fragmentTransaction.addToBackStack(mainMenuFragment.getClass().getName());
            fragmentTransaction.commit();
        }
    }*/

    private Intent onLogView() {
        Intent intentLogView = new Intent(this, LogActivity.class);
        startActivity(intentLogView);
        return intentLogView;
    }

    /*Navigates to Sync view*/
    private Intent onSyncView() {
        Intent intentSyncView = new Intent(this, SyncSelectionActivity.class);
        startActivity(intentSyncView);
        return intentSyncView;
    }

    public void disableCollapse() {
        flBackDrop.setVisibility(View.GONE);
        collapsingToolbar.setTitleEnabled(false);
//        collapsingToolbar.setBackground(ContextCompat.getDrawable(MainActivity.this,R.color.primaryColor));
    }

    private void openFragment(Fragment mainMenuFragment) {
        disableCollapse();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, mainMenuFragment, mainMenuFragment.getClass().getName());
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void onLog() {
        Intent intent = new Intent(this, LogActivity.class);
        startActivity(intent);
    }

    /*Navigates to About us*/
    private void onAboutUs() {
        Intent intent = new Intent(this, AboutUsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                onBackPressed();
                break;

        }
    }

    private void openDashboard() {
        daySummaryFragment = new DaySummaryFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.isFirstRegistration,isFromRegistration);
        daySummaryFragment.setArguments(bundle);
        openFragment(daySummaryFragment);
    }

    private void closeNavDrawer() {
        drawerLayout.closeDrawer(Gravity.LEFT, true);
    }

    @Override
    public void fragmentCallBack(final String title, Bundle bundles) {
        closeNavDrawer();
        Bundle bundle;
        if (bundles != null) {
            final int pos = bundles.getInt(Constants.EXTRA_POS, 0);
            Fragment fragment = null;
            Intent intent = null;
            switch (pos) {
                case 1:
                    if (ConstantsUtils.isAutomaticTimeZone(MainMenu.this)) {
                        openDashboard();
                    } else {
                        ConstantsUtils.showAutoDateSetDialog(MainMenu.this);
                    }
//                    fragment = new MainMenuFragment();
//                    bundle = new Bundle();
//                    bundle.putInt(Constants.EXTRA_COME_FROM, 1);
//                    fragment.setArguments(bundle);
                    break;
                case 3:
//                    ConstantsUtils.onBeatPlan(this);
                    break;
                case 4:
                    if (ConstantsUtils.isAutomaticTimeZone(MainMenu.this)) {
                        ConstantsUtils.onMyTargets(this);
                    } else {
                        ConstantsUtils.showAutoDateSetDialog(MainMenu.this);
                    }
                    break;
                case 5:
                    if (ConstantsUtils.isAutomaticTimeZone(MainMenu.this)) {
                        ConstantsUtils.onMTPActivity(this);
                    } else {
                        ConstantsUtils.showAutoDateSetDialog(MainMenu.this);
                    }
                    break;
                case 6:
                    ConstantsUtils.onAdhocList(this);
                    break;
                case 7:
                    ConstantsUtils.onAlerts(this);
                    break;
                case 8:
                    ConstantsUtils.onExpenseEntry(this);
                    break;

                case 9:
                    fragment = new ExpenseListFragment();
                    bundle = new Bundle();
                    bundle.putInt(Constants.EXTRA_COME_FROM, 1);
                    fragment.setArguments(bundle);
                    break;
                case 10:
//                    ConstantsUtils.onSchemes(this);
                    onPlantStock();
                    break;
                case 11:
                    ConstantsUtils.onSoApproval(this);
                    break;
                case 12:
                    onProductPriceActivityList();
//                    ConstantsUtils.onVisitSummary(this);
                    break;
                case 13: //retailers
                    ConstantsUtils.onRetailerList(this);
                    break;
                case 14: // dsr entry
                    ConstantsUtils.onDSREntry(this);
                    break;
                case 15: // cutomer list
                    ConstantsUtils.onCustomerList(this);
                    break;
                case 16: //sync
                    if (ConstantsUtils.isAutomaticTimeZone(MainMenu.this)) {
                        onSyncView();
                    } else {
                        ConstantsUtils.showAutoDateSetDialog(MainMenu.this);
                    }
                    break;
                case 17: //log
                    onLogView();
                    break;
                case 18: //setting
                    opensettingsFragment();
                    break;
                case 19: //support
                    onSupport();
                    break;
                case 20:
                    if (ConstantsUtils.isAutomaticTimeZone(MainMenu.this)) {
                        ConstantsUtils.onRTGS(this);
                    } else {
                        ConstantsUtils.showAutoDateSetDialog(MainMenu.this);
                    }
                    break;
                case 21:
                    ConstantsUtils.onDealerBehaver(this);
                    break;
                case 22:
                    if (ConstantsUtils.isAutomaticTimeZone(MainMenu.this)) {
                        ConstantsUtils.onSubordinateView(this, ConstantsUtils.MTP_SUBORDINATE);
                    } else {
                        ConstantsUtils.showAutoDateSetDialog(MainMenu.this);
                    }
                    break;
                case 23:
                    if (ConstantsUtils.isAutomaticTimeZone(MainMenu.this)) {
                        ConstantsUtils.onSubordinateView(this, ConstantsUtils.RTGS_SUBORDINATE);
                    } else {
                        ConstantsUtils.showAutoDateSetDialog(MainMenu.this);
                    }
                    break;

                case 24:
                    if (ConstantsUtils.isAutomaticTimeZone(MainMenu.this)) {
                        ConstantsUtils.onAttendanceSummary(this, ConstantsUtils.ATTND_SUMMARY);
                    } else {
                        ConstantsUtils.showAutoDateSetDialog(MainMenu.this);
                    }
                    break;

                case 26:
                    if (ConstantsUtils.isAutomaticTimeZone(MainMenu.this)) {
                        ConstantsUtils.onClaimSummary(this, ConstantsUtils.CLAIM_SUMMARY);
                    } else {
                        ConstantsUtils.showAutoDateSetDialog(MainMenu.this);
                    }
                    break;
                case 25:
                    ConstantsUtils.onResetPwd(this);
                    break;
            }
            if (fragment != null) {
                Handler handler = new Handler();
                final Fragment finalFragment = fragment;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        openFragment(finalFragment);
                        if (pos != 1)
                            setActionBarTitle(title, false, false);
                        else//home fragment
                            setActionBarTitle(getString(R.string.app_name), false, true);
                    }
                }, 300);

            } else if (intent != null) {
                Handler handler = new Handler();
                final Intent finalIntent = intent;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(finalIntent);
//                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    }
                }, 300);
            }
        }

    }

    private void onPlantStock() {
        Intent behaviour = new Intent(this, PlantStockActivity.class);
        startActivity(behaviour);
    }

    private void onProductPriceActivityList() {
        Intent behaviour = new Intent(this, ProductPriceActivity.class);
        startActivity(behaviour);
    }

    /*private void getUserName() {
        try {
            LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();
            mStrUserName = lgCtx.getBackendUser();
        } catch (LogonCoreException e) {
            e.printStackTrace();
        }
    }*/

    private void opensettingsFragment() {
        disableCollapse();
        if (registrationModel != null) {
            String userName = sharedPerf.getString("username","");
            registrationModel.setExtenndPwdReq(true);
            registrationModel.setUpdateAsPortalPwdReq(true);
            registrationModel.setIDPURL(Configuration.IDPURL);
            registrationModel.setUserName(userName);
            registrationModel.setExternalTUserName(Configuration.IDPTUSRNAME);
            registrationModel.setExternalTPWD(Configuration.IDPTUSRPWD);
            Intent intent = new Intent(this, com.arteriatech.mutils.support.SecuritySettingActivity.class);
            intent.putExtra(UtilConstants.RegIntentKey, registrationModel);
            startActivity(intent);
        }
    }

    private void closeStore() {
        try {
            OfflineManager.closeOfflineStore(MainMenu.this, OfflineManager.options);
            OfflineManager.closeOfflineStoreGeo(MainMenu.this, OfflineManager.optionsGeo);
            LogManager.writeLogInfo(getString(R.string.store_removed));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            LogManager.writeLogError(Constants.error_during_offline_close + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {

        System.out.println("OnDestory Triggred");
        if(openOfflineStore!=null)
        { try {
            System.out.println("OnDestory Triggred1");

            openOfflineStore.cancel(true);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        if (mRegistrationBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        }

        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SUPPORT && resultCode == SupportActivity.OFFLINE_DB_REINITIALIZE) {
            openOfflineStore=new OpenOfflineStore();
            if(openOfflineStore!=null)
            {   openOfflineStore.execute();}


        } else if (requestCode == REQUEST_SUPPORT && resultCode == SupportActivity.OFFLINE_DB_IMPORT) {
            importOfflineDB(MainMenu.this);
        }/* else if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(MainMenu.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MainMenu.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                startStep2(null);
            } else {
//                Toast.makeText(getBaseContext(),"Unable to start your service. Please enable your location permission",Toast.LENGTH_LONG).show();
            }
        }*/
//        TODo For location capturing
        /*else if(requestCode == REQUEST_GPS_PERMISSION){
            startStep3();
        }else if(requestCode == ConstantsUtils.DATE_SETTINGS_REQUEST_CODE){
            startBackGroundService();
        }*/
       /* if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                // You have permission
//                showDialog();
            }
        }*/
    }

    private void registerReceiver() {
       /* mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(NotificationConfig.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(NotificationConfig.TOPIC_GLOBAL);
                    NotificationConfig.sapRegistrationFirebaseRegId(getApplicationContext());
                } else if (intent.getAction().equals(NotificationConfig.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra(Constants.EXTRA_NOTIFICATION);
                } else if (intent.getAction().equals(NotificationConfig.PUSH_NOTIFICATION_FOREGROUND)) {
                    // new push notification is received

//                    String message = intent.getStringExtra(Constants.EXTRA_NOTIFICATION);
//                    int notifViewId = intent.getIntExtra(Constants.EXTRA_VIEW_ID, 0);
//                    String notifSOId = message;
//                    ConstantsUtils.notificationRedirection(MainMenu.this, notifViewId, notifSOId);
                }
            }
        };
        NotificationConfig.sapRegistrationFirebaseRegId(getApplicationContext());*/
    }



    private void startBackGroundService() {
        try {
            GEOSTRTTME = OfflineManager.getValueByColumnName("ConfigTypsetTypeValues?$filter=Typeset eq 'SP' and Types eq 'GEOSTRTTME'", Constants.TypeValue);
            if (TextUtils.isEmpty(GEOSTRTTME))
                GEOSTRTTME = "8";
            GEOENDTME = OfflineManager.getValueByColumnName("ConfigTypsetTypeValues?$filter=Typeset eq 'SP' and Types eq 'GEOENDTME'", Constants.TypeValue);
            if (TextUtils.isEmpty(GEOENDTME))
                GEOENDTME = "20";
            String locTimeInterval = OfflineManager.getValueByColumnName("ConfigTypsetTypeValues?$filter=Typeset eq 'SP' and Types eq 'TIMEINTRVL'", Constants.TypeValue);
            if (!TextUtils.isEmpty(locTimeInterval))
                timeInterval = Integer.parseInt(locTimeInterval);
            String enableGeo = "";
            SharedPreferences.Editor editor = sharedPerf.edit();
            enableGeo = OfflineManager.getValueByColumnName("ConfigTypsetTypeValues?$filter=Typeset eq 'SP' and Types eq 'ENABLEGEO'", Constants.TypeValue);
            if (enableGeo.equalsIgnoreCase("X"))
                editor.putBoolean(context.getString(R.string.enable_geo), true);
            else
                editor.putBoolean(context.getString(R.string.enable_geo), false);
            editor.apply();
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SharedPreferences mPrefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(getString(R.string.geo_start_time), GEOSTRTTME);
        editor.putString(getString(R.string.geo_end_time), GEOENDTME);
        editor.putInt(getString(R.string.geo_location_interval_time), timeInterval);
        try {
            editor.putString(getString(R.string.geo_smallest_displacement), OfflineManager.getValueByColumnName("ConfigTypsetTypeValues?$filter=Typeset eq 'SP' and Types eq 'DISPDIST'", Constants.TypeValue));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        editor.apply();

        Calendar cal = Calendar.getInstance();

        SimpleDateFormat fmt = new SimpleDateFormat("HH");
        int time = Integer.parseInt(fmt.format(cal.getTime()));
        if (ConstantsUtils.isAutomaticTimeZone(this)) {
            if (time >= Integer.parseInt(GEOSTRTTME) && time <=Integer.parseInt(GEOENDTME)) {
                startStep1();
            } else {
                if (sharedPerf.getBoolean(getString(R.string.enable_geo), false)) {
                    if (!isAlaramActive()) {
                        if (time < Integer.parseInt(GEOSTRTTME)) {
                            Constants.setScheduleAlaram(this, Integer.parseInt(GEOSTRTTME), 00, 00, 0);
                        } else {
                            Constants.setScheduleAlaram(this, Integer.parseInt(GEOSTRTTME), 00, 00, 1);
                        }
                    }
                }else{
//                    ConstantsUtils.serviceReSchedule(MainMenu.this,true);
                }
            }
        } else {
            ConstantsUtils.showAutoDateSetDialog(this);
        }

    }

    private boolean isAlaramActive() {
        Intent myIntent = new Intent(MainMenu.this, AlaramRecevier.class);

        boolean isWorking = (PendingIntent.getService(MainMenu.this, 0, myIntent, PendingIntent.FLAG_NO_CREATE) != null);
       /* boolean alarmUp = (PendingIntent.getService(context, 0,
                new Intent("com.my.package.MY_UNIQUE_ACTION"),
                PendingIntent.FLAG_NO_CREATE) != null);*/
        return isWorking;
    }

    /**
     * Step 1: Check Google Play services
     */
    private void startStep1() {

        //Check whether this user has installed Google play service which is being used by Location updates.
        if (isGooglePlayServicesAvailable()) {

            //Passing null to indicate that it is executing for the first time.
            startStep3();

        } else {
            Toast.makeText(MainMenu.this, R.string.no_google_playservice_available, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Step 2: Check & Prompt Internet connection
     */
    /*private Boolean startStep2(DialogInterface dialog) {
     *//*ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            promptInternetConnect();
            return false;
        }


        if (dialog != null) {
            dialog.dismiss();
        }*//*

        //Yes there is active internet connection. Next check Location is granted by user or not.

        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
            startStep3();
        } else {  //No user has not granted the permissions yet. Request now.
            requestPermission();
        }
        return true;
    }
*/
    /**
     * Start permissions requests.
     */
    private void permissions() {

        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);


        // Provide an additional rationale to the img_user. This would happen if the img_user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale || shouldProvideRationale2) {
//            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainMenu.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
//            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the img_user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainMenu.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(MainMenu.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MainMenu.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainMenu.this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(MainMenu.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                //Show Information about why you need the permission
                showSnackbar(R.string.permission_rationale,
                        android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Request permission
                                ActivityCompat.requestPermissions(MainMenu.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                        REQUEST_PERMISSIONS_REQUEST_CODE);
                            }
                        });
            } else if (permissionStatus.getBoolean(Manifest.permission.ACCESS_FINE_LOCATION, false) || (permissionStatus.getBoolean(Manifest.permission.ACCESS_COARSE_LOCATION, false))) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                showSnackbar(R.string.permission_rationale,
                        R.string.setting, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Request permission
                                sentToSettings = true;
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                            }
                        });
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(MainMenu.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_PERMISSIONS_REQUEST_CODE);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.ACCESS_FINE_LOCATION, true);
            editor.putBoolean(Manifest.permission.ACCESS_COARSE_LOCATION, true);
            editor.commit();


        }
    }

//    TODo For location capturing

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case UtilConstants.Location_PERMISSION_CONSTANT: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startStep3();
                } else {
                    startStep3();
                }
                return;
            }
        }
    }

    /**
     * Step 3: Start the Location Monitor Service
     */
    private void startStep3() {

        //And it will be keep running until you close the entire application from task manager.
        //This method will executed only once.

       /* if (!mAlreadyStartedService && mMsgView != null) {

            mMsgView.setText(R.string.msg_location_service_started);

            //Start location sharing service to app server.........
            Intent intent = new Intent(this, LocationMonitoringService.class);
            startService(intent);

            mAlreadyStartedService = true;
            //Ends................................................
        }*/
//        initializeLocationManager();
        if (sharedPerf.getBoolean(getString(R.string.enable_geo), false)) {
            Constants.getLocation(MainMenu.this, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                    if (status) {
                        scheduleJob();
                    }
                }
            });
        }else{
//            ConstantsUtils.serviceReSchedule(MainMenu.this,true);
        }
//        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//
//            showSnackbar(R.string.permission_GPS,
//                    R.string.Enable, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            // Request permission
//                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                            startActivity(intent);
//                        }
//                    });
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setMessage("To continue, Let your device turn on location")
//                    .setCancelable(false)
//                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                            startActivity(intent);
//                        }
//                    })
//                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.cancel();
//                        }
//                    });
//            builder.create().show();
//        } else {
//            scheduleJob();
//        }
    }

//    private void initializeLocationManager() {
//        if (locationManager == null) {
//            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        }
//    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private void scheduleJob() {
        try {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            Intent intent = new Intent();
            try {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
                        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            startService(new Intent(this, TrackerService.class));
            ConstantsUtils.stopAlarmManagerByID(MainMenu.this, AutoSyncDataLocationAlarmReceiver.class,AutoSyncDataLocationAlarmReceiver.REQUEST_CODE);
//        ConstantsUtils.startAutoSyncLocation(context,true);


//        DatabaseHelperGeo databaseHelper = new DatabaseHelperGeo(this);
//        Date dateMillSec = new Date();
//        String currentDateTimeString = DateFormat.getDateTimeInstance().format(dateMillSec);
        /*ServiceStartStopBean startStopBean = new ServiceStartStopBean(currentDateTimeString, "ServiceStart");
        databaseHelper.createRecordService(startStopBean);
        databaseHelper.getData();*/

//        Log.d(TAG, "Job Started Clicked");
//        mDispatcher.newRetryStrategy(RetryStrategy.RETRY_POLICY_EXPONENTIAL,60*10, 60*15);
       /* mDispatcher.newRetryStrategy(RetryStrategy.RETRY_POLICY_EXPONENTIAL, 60 * 5, 60 * 6);//Repeat interval time
        Job myJob = mDispatcher.newJobBuilder()
                .setService(LocationMonitoringService.class)
                .setTag(JOB_TAG)
                .setRecurring(true)
//                .setTrigger(Trigger.executionWindow(60*10, 60*15))
                .setTrigger(Trigger.executionWindow(60 * 5, 60 * 6))//First trigger time
                .setLifetime(Lifetime.FOREVER)
                .setReplaceCurrent(true)
//                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
//                .setTrigger(Constants.periodicTrigger(60 * 15, 1)) // repeated every 20 seconds with 1 second of tollerance
                .build();

        mDispatcher.mustSchedule(myJob);
        Log.d("LocationServiceCapture", "Job Started Clicked");*/
//        Toast.makeText(getActivity(), "Job Started", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionState2 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;

    }

    /**
     * Return the availability of GooglePlayServices
     */
    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, 2404).show();
            }
            return false;
        }
        return true;
    }
    /*
     *
     * AsyncTask for opening offline store
     *
     */
    private class OpenOfflineStore extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(MainMenu.this, R.style.UtilsDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            closeStore();
            try {
                OfflineManager.openOfflineStore(MainMenu.this, MainMenu.this);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    public class OpenOfflineStoreAsync extends AsyncTask<Void, Void, Void> {
        boolean isStoreOpened = false;
        boolean readRollId = false;
        ErrorBean errorBean = null;

        OpenOfflineStoreAsync(boolean readRollId) {
            this.readRollId = readRollId;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            LogManager.writeLogInfo("OfflineStore Started Loading");
            if (sharedPerf.getBoolean(Constants.isFirstTimeReg, false)) {
                pdLoadDialog = new ProgressDialog(MainMenu.this, R.style.UtilsDialogTheme);
                pdLoadDialog.setMessage(pDialogStr);
                pdLoadDialog.setCancelable(false);
                pdLoadDialog.show();
            }else if ((sharedPerf.getInt(Constants.CURRENT_VERSION_CODE, 0) != Constants.NewDefingRequestVersion) ||
                    (sharedPerf.getInt(Constants.INTIALIZEDB, 0) != Constants.IntializeDBVersion)) {
                showDialogMessage();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            LogManager.writeLogInfo("OfflineStore Loading InProgress..");
            if (isFromRegistration) {
                pDialogStr = getString(R.string.preparing_app);
            }
//        createSyncHistoryTable();
        /*if (readRollId) {
            Log.d(TAG, "onStatus: OnlineStore service started");
            try {
                String authOrgValue = OnlineManager.getUserRollInfo(Constants.UserProfileAuthSet + "?$filter=Application eq 'PD'",MainMenu.this);// and AuthOrgTypeID eq '000014'

//                Log.d("getUserRollInfor","true");
                *//*String parentID="";
                String login = sharedPerf.getString(Constants.USERROLELOGINID,"");
                if(!TextUtils.isEmpty(login)){
                    parentID = OnlineManager.getUserPartnersnfo( Constants.UserPartners + "?$filter= LoginID eq'" + login + "'",MainMenu.this);// and AuthOrgTypeID eq '000014'
                }*//*
                if(!TextUtils.isEmpty(authOrgValue)) {
                    SharedPreferences.Editor editor = sharedPerf.edit();
                    editor.putString(Constants.USERROLE, authOrgValue);
//                editor.putString(Constants.USERPARNTERID, parentID);
                    editor.putBoolean(Constants.isRollResponseGot, true);
                    editor.apply();
                    Log.d("getUserRollInfor", "true");
                }
                *//*try {
                    OnlineStoreListener openListener = OnlineStoreListener.getInstance();
                    OnlineODataStore store = openListener.getStore();
                    if (store.isOpenCache())
                        store.closeCache();
                } catch (ODataContractViolationException e) {
                    e.printStackTrace();
                }*//*
                errorBean = null;
            } catch (OnlineODataStoreException e) {
                e.printStackTrace();
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
                errorBean = Constants.getErrorCode(Operation.GetRequest.getValue(), e, MainMenu.this);
            } catch (ODataNetworkException e) {
                e.printStackTrace();
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
                errorBean = Constants.getErrorCode(Operation.GetRequest.getValue(), e, MainMenu.this);
            } catch (ODataParserException e) {
                e.printStackTrace();
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
                errorBean = Constants.getErrorCode(Operation.GetRequest.getValue(), e, MainMenu.this);
            } catch (ODataContractViolationException e) {
                e.printStackTrace();
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
                errorBean = Constants.getErrorCode(Operation.GetRequest.getValue(), e, MainMenu.this);
            }
        }*/
            String rollID = sharedPerf.getString(Constants.USERROLE,"");
            if(!TextUtils.isEmpty(rollID)) {
                if (errorBean == null) {
                    if (OfflineManager.offlineStore != null) {
                        if (!OfflineManager.isOfflineStoreOpen()) {
                            try {
                                OfflineManager.openOfflineStore(MainMenu.this, uiListener);
                            } catch (OfflineODataStoreException e) {
                                e.printStackTrace();
                                LogManager.writeLogError(Constants.error_txt + e.getMessage());
                            }
                        } else {
                            isStoreOpened = true;
                        }
                    } else {
                        Log.d(TAG, "onStatus: Open offline store");
                        try {
                            OfflineManager.openOfflineStore(MainMenu.this, uiListener);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                        }
                    }
                }
            }
            createSyncHistoryTable();
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            LogManager.writeLogInfo("OfflineStore Loaded Successfully");
            String rollID = sharedPerf.getString(Constants.USERROLE, "");
            if (TextUtils.isEmpty(rollID)) {
                try {
                    if (pdLoadDialog!=null&&pdLoadDialog.isShowing()) {
                        pdLoadDialog.dismiss();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                LogManager.writeLogError(Constants.error_txt + "Not able to get roll information");
//            Constants.displayMsgReqError(errorBean.getErrorCode(), MainMenu.this);
                UtilConstants.showAlert(Constants.error_txt + "Not able to get roll information", MainMenu.this);
            } else if (errorBean != null) {
                try {
                    if (pdLoadDialog!=null&&pdLoadDialog.isShowing()) {
                        pdLoadDialog.dismiss();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                LogManager.writeLogError(Constants.error_txt + "Not able to get roll information");
                Constants.displayMsgReqError(errorBean.getErrorCode(), MainMenu.this);
            } else if (isStoreOpened) {
                try {
                    String firstTime = "";
                    String endTime = "";
                    String autoSyncTime = "";
                    String lastSyncDate = "";
                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
                    firstTime = sharedPreferences.getString("FIRSTAUTOSYNCTIME","");
                    autoSyncTime = sharedPreferences.getString("AUTOSYNCTIME","");
                    lastSyncDate = sharedPreferences.getString("LASTSYNCDATE", "");

                    try {
//                Date date = new Date();
//                Calendar c = Calendar.getInstance();
//                c.setTime(date);
//                date = c.getTime();
//                //else for example date1 = 01:55, date2 = 03:55.
//                long ms = date.getTime();
//                long diffMinutes = ms / (60 * 1000) % 60;
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        endTime = sdf.format(cal.getTime());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    long diffTime = 0;
                    long finalTime = 0;
                    if(!TextUtils.isEmpty(firstTime) && !TextUtils.isEmpty(endTime)){
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                        Date date1 = format.parse(firstTime);
                        Date date2 = format.parse(endTime);
                        diffTime= date2.getTime() - date1.getTime();
                        finalTime=diffTime / (60 * 1000) % 60;
                    }
                    int intFirstTime = (int) finalTime;
                    int intAutoSyncTime = 0;
                    if(!TextUtils.isEmpty(autoSyncTime)){
                        intAutoSyncTime = Integer.parseInt(autoSyncTime);
                    }

                    if (!TextUtils.isEmpty(autoSyncTime)) {
                        if(lastSyncDate.equalsIgnoreCase(UtilConstants.getNewDate())) {
                            if (intFirstTime >= intAutoSyncTime) {
                                try {
                                    LogManager.writeLogError("Skip AutoSync Started");
                                    //  UpdatePendingRequest.getInstance(null).callScheduleFirstLoginSync();
                                } catch (Exception e) {
                                    LogManager.writeLogError("UpdatePendingRequest : " + e.toString());
                                    e.printStackTrace();
                                }
                                ConstantsUtils.startAutoSync(MainMenu.this, true);
                            }
                        }else{
                            ConstantsUtils.startAutoSync(MainMenu.this, true);
                        }
                    } else {
                        LogManager.writeLogError("AutoSync Started Date Not Matched");
                        ConstantsUtils.startAutoSync(MainMenu.this, true);
                    }

//                ConstantsUtils.startAutoSync(MainMenu.this, true);
                    //  ConstantsUtils.startAutoSyncLocation(MainMenu.this, true); //TODo For location capturing
                    // put tracker code for db
//               TODo For location capturing
                    try {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (reentrantLock==null){
                                    reentrantLock = new ReentrantLock();
                                }
                                try {
                                    Log.e("Main Menu REENTRANT:","LOCKED");
                                    reentrantLock.lock();
                                    Constants.getDataFromSqliteDB(getApplicationContext(),null);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e("Main Menu EXCEPTION","ANR EXCEPTION OCCURRED");
                                }finally {
                                    if (reentrantLock!=null&&reentrantLock.isHeldByCurrentThread())reentrantLock.unlock();
                                    Log.e("Main Menu REENTRANT:","UNLOCKED FINALLY");
                                }
                            }
                        }).start();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
//                systemWindonPermission();
//                startBackGroundService();
                    if (pdLoadDialog!=null&&pdLoadDialog.isShowing()) {
                        pdLoadDialog.dismiss();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                LogManager.writeLogInfo("OfflineStore Opened Successfully");
                boolean datavault = PrivateDataVault.vaultExists(Configuration.APP_ID);
                if(!sharedPerf.getBoolean(Constants.DataVaultUpdate,false)) {
                    if (datavault) {
                        LogManager.writeLogInfo("Vault is exists");
                        datavaultData(MainMenu.this);
//            Toast.makeText(mContext, "Vault is exists", Toast.LENGTH_SHORT).show();
                    } else {
                        LogManager.writeLogInfo("Vault is not exists");
//            Toast.makeText(mContext, "Vault is not exists", Toast.LENGTH_SHORT).show();
                    }
                }
                setUI();
            }
        }
    }

    private void datavaultData(Context context){
        try {
            if(pdLoadDialog!=null && pdLoadDialog.isShowing()){
                pdLoadDialog.dismiss();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        pdLoadDialog = new ProgressDialog(this, R.style.UtilsDialogTheme);
        pdLoadDialog.setMessage(getString(R.string.update_app_message));
        pdLoadDialog.setCancelable(false);
        pdLoadDialog.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    final int isExportData = exportDataVaultData(MainMenu.this);
                    runOnUiThread(new Runnable() {
                        public void run() {
//                            pdLoadDialog.dismiss();
                            String msg = getString(R.string.export_datavault_to_sdcard_error_occurred);
                            if (isExportData == 3) {//success
                                msg = getString(R.string.export_datavault_to_sdcard_finish);
                                Constants.removePendingList(context);
                                importDatavault(MainMenu.this);
                            } else if (isExportData == 2) {
                                pdLoadDialog.dismiss();
                                msg = "No Pending Requests Available";
                                SharedPreferences.Editor editor = sharedPerf.edit();
                                try {
                                    editor.putBoolean(Constants.DataVaultUpdate, true);
                                    editor.apply();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                setUI();
                            }else {
                                pdLoadDialog.dismiss();
                                setUI();
                            }
                            LogManager.writeLogError(msg);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static int exportDataVaultData(Context mContext) {
        boolean flagforlog = false;
        try {
            FileWriter fileWriter = null;
            String jsonData = null;
            try {
                jsonData = makePendingDataToJsonString(mContext);
            } catch (Exception e) {
                e.printStackTrace();
                LogManager.writeLogError("exportDataVault() : " + e.getMessage());
                jsonData = "";
            }
            if (jsonData != null && !jsonData.equalsIgnoreCase("")) {
                fileWriter = new FileWriter(Environment.getExternalStorageDirectory()
                        + "/" + Constants.DataVaultFileName + "");
                fileWriter.write(jsonData);
                fileWriter.close();
//                flagforlog = true;
                return 3;
            } else {
//                Constants.ExportDataFailedErrorMsg = "No Pending Requests Available";
//                flagforlog = false;
                return 2;
            }


        } catch (IOException e) {
            e.printStackTrace();
            LogManager.writeLogError("exportDataVault() (IOException) : " + e.getMessage());
            return 1;
        }
    }
    public static String makePendingDataToJsonString(Context context) {
        String mStrJson = "";
        ArrayList<Object> objectArrayList = Constants.getPendingDataVaultData(context);
        if (!objectArrayList.isEmpty()) {
            String[][] invKeyValues = (String[][]) objectArrayList.get(1);
            JSONArray jsonArray = new JSONArray();
            for (int k = 0; k < invKeyValues.length; k++) {
                JSONObject jsonObject = new JSONObject();
                String store = "";
                try {
                    store = getValueFromDataVault(invKeyValues[k][0].toString(),context,Constants.EncryptKey);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                try {
                    // Add the values to the jsonObject
                    jsonObject.put(Constants.KeyNo, invKeyValues[k][0]);
                    jsonObject.put(Constants.KeyType, invKeyValues[k][1]);
                    jsonObject.put(Constants.KeyValue, store);
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put(Constants.DataVaultData, jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mStrJson = jsonObj.toString();
        }
        return mStrJson;
    }

    private void importDatavault(final Context mContext) {
        new Thread(new Runnable() {
            public void run() {
                String message = getString(R.string.import_datavault_from_sdcard_finish);

                try {
                    boolean isFileExists = Constants.isFileExits(Constants.DataVaultFileName);
                    if (isFileExists) {
                        String datavaultData = Constants.getTextFileData(Constants.DataVaultFileName);
                        Constants.setJsonStringDataToDataVault(datavaultData, mContext);
                    } else {
                        message = getString(R.string.file_not_exist);
                    }
                    SharedPreferences.Editor editor = sharedPerf.edit();
                    try {
                        editor.putBoolean(Constants.DataVaultUpdate, true);
                        editor.apply();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogManager.writeLogError("importDatavaultData() (InterruptedException): " + e.getMessage());
                    message = getString(R.string.import_datavault_from_sdcard_error_occurred);
                }
                final String finalMessage = message;
                runOnUiThread(new Runnable() {
                    public void run() {
                        pdLoadDialog.cancel();
                        LogManager.writeLogInfo(finalMessage);
                        setUI();
//                        ConstantsUtils.displayLongToast(mContext, finalMessage);
                    }
                });
            }
        }).start();

    }

    public static String getValueFromDataVault(String key, Context mContext, String password) {
        String store = null;

        try {
            PrivateDataVault privateDataVault = PrivateDataVault.getVault(Configuration.APP_ID);
            if (privateDataVault != null) {
                if (privateDataVault.isLocked()) {
                    privateDataVault.unlock(null);
                }

                byte[] byteValue = privateDataVault.getValue(key);
                if (byteValue != null) {
                    store = new String(byteValue, "UTF-8");
                }
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return store;
    }
}
