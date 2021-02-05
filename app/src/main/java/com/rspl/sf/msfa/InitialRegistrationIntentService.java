package com.rspl.sf.msfa;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.registration.RegistrationModel;
import com.arteriatech.mutils.registration.UtilRegistrationActivity;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.MSFAApplication;
import com.rspl.sf.msfa.main.MainMenu;
import com.rspl.sf.msfa.registration.Configuration;
import com.rspl.sf.msfa.registration.RegistrationActivity;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;
import com.sybase.persistence.DataVault;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static com.rspl.sf.msfa.common.Constants.isStoreOpened;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class InitialRegistrationIntentService extends IntentService implements UIListener, OnlineODataInterface/*, LogonCoreListener, LogonListener */{
    private static String TAG = InitialRegistrationIntentService.class.getSimpleName();
    private static InitialRegistrationIntentService initialRegistrationIntentService = null;
    private Context mContext;
    private boolean isFromRegistratoin = false;
    private int totalRequest = 0;
    private int currentRequest = 0;
    private SharedPreferences sharedPreferences;

    public InitialRegistrationIntentService() {
        super("InitialRegistrationIntentService");
    }

    public static void startInitialRegService(Context context, boolean isFromRegistration) {
        try {
            SharedPreferences sharedPerf = context.getSharedPreferences(Constants.PREFS_NAME, 0);
            if (!sharedPerf.getBoolean(Constants.isFirstTimeReg, false)) {
                Intent intent = new Intent(context, InitialRegistrationIntentService.class);
                intent.putExtra(UtilRegistrationActivity.EXTRA_IS_FROM_REGISTRATION, isFromRegistration);
                context.startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getTime() {
        return new SimpleDateFormat("yyyyMMdd_HH mm ss").format(Calendar.getInstance().getTime());
    }

    /*LogonCore logonCore = null;
    private LogonUIFacade mLogonUIFacade;*/
    RegistrationModel registrationModel = new RegistrationModel();
   /* private void initLogonCore(Context mContext, RegistrationModel registrationModel) {
        try {
            this.logonCore = LogonCore.getInstance();
            this.mLogonUIFacade = LogonUIFacade.getInstance();
            this.mLogonUIFacade.init(this, mContext, registrationModel.getAppID());
            this.logonCore.setLogonCoreListener(this);
            this.logonCore.init(this, registrationModel.getAppID());

            try {
                if (!this.logonCore.isStoreAvailable()) {
                    this.logonCore.createStore((String)null, false);
                }
            } catch (LogonCoreException var4) {
                var4.printStackTrace();
            }
        } catch (Exception var5) {
            LogManager.writeLogError(this.getClass().getSimpleName() + ".initLogonCore: " + var5.getMessage());
        }

    }*/

    private void intializeRegistrationModel() {
        registrationModel.setAppID(Configuration.APP_ID);
        registrationModel.setHttps(Configuration.IS_HTTPS);
        registrationModel.setPassword(Configuration.pwd_text);
        registrationModel.setPort(Configuration.port_Text);
        registrationModel.setSecConfig(Configuration.secConfig_Text);
        registrationModel.setServerText(Configuration.server_Text);
        registrationModel.setShredPrefKey(Constants.PREFS_NAME);
        registrationModel.setFormID(Configuration.farm_ID);
        registrationModel.setSuffix(Configuration.suffix);

        registrationModel.setDataVaultFileName(Constants.DataVaultFileName);
        registrationModel.setOfflineDBPath(Constants.offlineDBPath);
        registrationModel.setOfflineReqDBPath(Constants.offlineReqDBPath);
        registrationModel.setIcurrentUDBPath(Constants.icurrentUDBPath);
        registrationModel.setIbackupUDBPath(Constants.ibackupUDBPath);
        registrationModel.setIcurrentRqDBPath(Constants.icurrentRqDBPath);
        registrationModel.setIbackupRqDBPath(Constants.ibackupRqDBPath);
        //noPasscodeClasses.add(MainMenu.class.getName());
        // registrationModel.setNoPasscodeActivity(noPasscodeClasses);
        registrationModel.setAppActionBarIcon(R.mipmap.ic_action_bar_logo);
        registrationModel.setAppLogo(R.drawable.arteria_new_logo_transparent);
        registrationModel.setAppVersionName(BuildConfig.VERSION_NAME);
        registrationModel.setEmainId(getString(R.string.register_support_email));
        registrationModel.setPhoneNo(getString(R.string.register_support_phone));
        registrationModel.setEmailSubject("");//getString(R.string.email_subject)
        registrationModel.setRegisterActivity(RegistrationActivity.class);

//        registrationModel.setMainMenuActivity(LoginActivity.class);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_FOREGROUND);
        if (intent != null) {
            isFromRegistratoin = intent.getBooleanExtra(UtilRegistrationActivity.EXTRA_IS_FROM_REGISTRATION, false);
            mContext = InitialRegistrationIntentService.this;
            sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
            Log.e("STORE", "Trying to open Store: ");
            intializeRegistrationModel();
//            initLogonCore(mContext, registrationModel);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                String password = sharedPreferences.getString("username", "");
//                logonCore.unlockStore(password);
            } catch (Exception var6) {
                var6.printStackTrace();
            }
            Log.e("STORE", "Checking Store ");
            if (OfflineManager.offlineStore != null) {
                if (!OfflineManager.isOfflineStoreOpen()) {
                    try {
                        OfflineManager.openOfflineStore(mContext, InitialRegistrationIntentService.this);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Log.e("STORE", " Store  is null");

                openOffileStore(mContext);
            }
        }
    }

    private void openOffileStore(Context mContext) {
        boolean offlineStatus = false;
        try {

            Log.e("Before Offline ","store");
            offlineStatus = OfflineManager.openOfflineStore(mContext, this);
            if (offlineStatus) {
                Log.e("STORE","Offline Store Opened");
                isStoreOpened=true;
            } else {
                Log.e("STORE","Offline Store failed to open");
                isStoreOpened=false;
            }
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
            sendErrorMsg(e.getMessage());
            isStoreOpened=false;
            Log.e("STORE", String.valueOf(e));
            Log.e("Offline ","failed");
        }
    }

    private void sendErrorMsg(String s) {
        sendReceiver(false, s);
    }

    private void sendSuccess() {
        sendReceiver(true, "");
    }

    private void sendReceiver(boolean status, String error) {
        MSFAApplication mApplication = (MSFAApplication) mContext.getApplicationContext();
        if (!status) {
            if (TextUtils.isEmpty(error))
                error = mContext.getString(R.string.no_network_conn);
        }
        mApplication.setServiceFinished(true, error);
    }

    private void initialOnlineCall(Context mContext) {
        /*Log.d("open Online", " start of online store" + getTime());
        if (UtilConstants.isNetworkAvailable(mContext)) {
            if (ConstantsUtils.openOnlineStore(mContext)) {
                fetchOnlineData(mContext);
            } else {
                sendErrorMsg("Online store not open. Please check internet connection");
            }
        } else {
            sendErrorMsg(mContext.getString(R.string.no_network_conn));
        }
        Log.d("open Online", " stop online store" + getTime());*/
    }

    private void callSuccess() {
        try {
            OfflineManager.getAuthorizations(mContext);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
//        MainActivity.isRefresh = true;
        sendSuccess();
    }
/*

    private void fetchOnlineData(Context mContext) {
        Log.d("open Online", " stop of online store" + getTime());
        Bundle bundle = new Bundle();
        boolean isSessionRequired = Constants.isSessionRequired(mContext);
        totalRequest = 2;
        */
    /*user profile*//*

        bundle.putString(Constants.BUNDLE_RESOURCE_PATH, Constants.UserProfiles + "(Application='PD')");
        bundle.putBoolean(Constants.BUNDLE_SESSION_REQUIRED, isSessionRequired);
        bundle.putInt(Constants.BUNDLE_SESSION_TYPE, ConstantsUtils.SESSION_HEADER);
        bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 1);
        if (isFromRegistratoin || TextUtils.isEmpty(sharedPreferences.getString(Constants.KEY_ROLL_ID, "")))
            bundle.putBoolean(UtilConstants.BUNDLE_READ_FROM_TECHNICAL_CACHE, false);
        else
            bundle.putBoolean(UtilConstants.BUNDLE_READ_FROM_TECHNICAL_CACHE, true);
        OnlineManager.requestQuery(this, bundle, mContext);

        */
    /*user customer*//*

        bundle = new Bundle();
        String query = "";
        if (isSessionRequired) {
            query = Constants.UserCustomers + "/?$filter=LoginID+eq+'%1$s'";
        } else {
            query = Constants.UserCustomers;
        }
        bundle.putString(Constants.BUNDLE_RESOURCE_PATH, query);
        bundle.putBoolean(Constants.BUNDLE_SESSION_REQUIRED, isSessionRequired);
        bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 2);
        bundle.putInt(Constants.BUNDLE_SESSION_TYPE, ConstantsUtils.SESSION_QRY_HEADER);
        if (isFromRegistratoin || TextUtils.isEmpty(sharedPreferences.getString(Constants.KEY_CUSTOMER_NO, "")))
            bundle.putBoolean(UtilConstants.BUNDLE_READ_FROM_TECHNICAL_CACHE, false);
        else
            bundle.putBoolean(UtilConstants.BUNDLE_READ_FROM_TECHNICAL_CACHE, true);
        OnlineManager.requestQuery(this, bundle, mContext);

    }
*/

   /* private boolean openOnlineStore(Context mContext) {
        boolean isOnlineStoreOpened = false;
        try {
            OnlineStoreListener openListener = OnlineStoreListener.getInstance();
            OnlineODataStore store = openListener.getStore();
            if (store == null) {
                Log.e(TAG, "opening online store from service");
                LogManager.writeLogInfo("opening store from service");
                isOnlineStoreOpened = OnlineManager.openOnlineStore(mContext,true);
            } else {
                isOnlineStoreOpened = true;
            }
        } catch (OnlineODataStoreException e) {
            e.printStackTrace();
            isOnlineStoreOpened = false;
            LogManager.writeLogError("Error : " + e.getMessage());
//            errorMessage = e.getMessage();
        }
        return isOnlineStoreOpened;
    }*/

    @Override
    public void onRequestError(int i, Exception e) {
        e.printStackTrace();
        Log.d(TAG, "onRequestError: ");
        sendErrorMsg(e.getMessage());

    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        Log.d(TAG, "onRequestSuccess: ");
        if (i == Operation.GetStoreOpen.getValue() /*&& isFromRegistratoin || TextUtils.isEmpty(sharedPreferences.getString(Constants.KEY_ROLL_ID, ""))*/) {
            //   SyncUtils.updateAllSyncHistory(mContext);
            callSuccess();

        }
       /* try {
            Activity activity = (Activity) mContext;
            AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
       /* int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        switch (type) {
            case 1:
                OnlineManager.getUserProfile(oDataRequestExecution, sharedPreferences);
                currentRequest++;
                break;
            case 2:
                String[][] arrCustomerData = OnlineManager.getUserCustomerArray(list);
                if (arrCustomerData != null) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constants.KEY_CUSTOMER_NO, arrCustomerData[0][0]);
                    editor.putString(Constants.KEY_CUSTOMER_NAME, arrCustomerData[1][0]);
                    editor.putString(Constants.KEY_CUSTOMER_REGION, arrCustomerData[8][0]);
                    editor.putString(Constants.KEY_CUSTOMER_GRP, arrCustomerData[33][0]);
                    editor.putString(Constants.KEY_COORPORATE_GRP, arrCustomerData[34][0]);
                    editor.putString(Constants.KEY_UNLOADING_PT, arrCustomerData[31][0]);
                    editor.putString(Constants.KEY_CUSTOMER_PHONE, arrCustomerData[13][0]);
                    editor.apply();
                }
                currentRequest++;
                break;
            case 3:
                OnlineManager.getAuthorizations(mContext, list);
                currentRequest++;
                break;
            default:
                *//*((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        asyncTaskCallBackInterface.asyncResponse(false, "", mContext.getString(R.string.error_bundle));
                    }
                });*//*
                sendErrorMsg(mContext.getString(R.string.error_bundle));
//                currentRequest++;
                break;
        }
        if (currentRequest == totalRequest) {
           *//* try {
                OfflineManager.getAuthorizations(mContext);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            MainActivity.isRefresh = true;
            sendSuccess();*//*
            callSuccess();*/
//        }
    }


    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, String s, Bundle bundle) {
        currentRequest++;
        if (currentRequest == totalRequest) {
            sendErrorMsg(s);
        }
    }
/*
    @Override
    public void registrationFinished(boolean b, String s, int i, DataVault.DVPasswordPolicy dvPasswordPolicy) {

    }

    @Override
    public void deregistrationFinished(boolean b) {

    }

    @Override
    public void backendPasswordChanged(boolean b) {

    }

    @Override
    public void applicationSettingsUpdated() {

    }

    @Override
    public void traceUploaded() {

    }

    @Override
    public void onLogonFinished(String s, boolean b, LogonContext logonContext) {

    }

    @Override
    public void onSecureStorePasswordChanged(boolean b, String s) {

    }

    @Override
    public void onBackendPasswordChanged(boolean b) {

    }

    @Override
    public void onUserDeleted() {

    }

    @Override
    public void onApplicationSettingsUpdated() {

    }

    @Override
    public void registrationInfo() {

    }

    @Override
    public void objectFromSecureStoreForKey() {

    }

    @Override
    public void onRefreshCertificate(boolean b, String s) {

    }*/

    public interface OnStoreSuccess{
        void onStoreSuccess(boolean status);
    }
}
