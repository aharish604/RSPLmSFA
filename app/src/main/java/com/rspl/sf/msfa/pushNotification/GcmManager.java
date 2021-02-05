/*
package com.rspl.sf.msfa.pushNotification;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.sap.maf.tools.logon.core.LogonCoreException;
import com.sap.maf.tools.logon.core.reg.AppSettings;
import com.sap.maf.tools.logon.core.reg.AppSettingsProperty;

import java.util.HashMap;

*/
/**
 * Created by e10769 on 07-08-2017.
 *//*


public class GcmManager {

    private static final String TAG = GcmManager.class.getSimpleName();
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static Context context;

    */
/**
     * Check the device to make sure it has the Google Play Services APK. If      * it doesn't, display a dialog that allows users to download the APK from      * the Google Play Store or enable it in the device's system settings.
     *//*

   */
/* public static boolean checkPlayServices(Context ctx) {
        context = ctx;
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }*//*



    */
/**
     * Stores the registration ID and the app versionCode in the application's      * {@code SharedPreferences}.      * @param regId registration ID
     *//*

    private static void storeRegistrationId(String regId) {
        if (context != null) {
            final SharedPreferences prefs = getGcmPreferences(context);
            int appVersion = getAppVersion(context);
            Log.i(TAG, "Saving regId on app version " + appVersion);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PROPERTY_REG_ID, regId);
            editor.putInt(PROPERTY_APP_VERSION, appVersion);
            editor.commit();
        } else {
            Log.d(TAG, "context is null");
        }
    }

    */
/**
     * Gets the current registration ID for application on GCM service, if there is one.      * If result is empty, the app needs to register.      * @return registration ID, or empty string if there is no existing
     *//*

    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (TextUtils.isEmpty(registrationId)) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion)
        {
            Log.i(TAG, "App version changed.");
            return "";
        }


        return registrationId;
    }


    */
/**
     * Registers the application with GCM servers asynchronously.      * Stores the registration ID and the app versionCode in the application's      * shared preferences.
     *//*

    public static void registerInBackground(final String regid) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Log.d(TAG, "registerInBackground::doInBackground");
                String msg = "";
                try { //check if the application settings has been downloaded successfully
                    if (FcmAppSettManager.downloadAppSettings()) {
                      */
/*  //Get the application settings
                        FcmAppSettDownListener settingsListener = FcmAppSettDownListener.getInstance();
                        AppSettings downloaded = settingsListener.getAppSettings();
                        //Get the GCM Sender ID from the server
                        AppSettingsReadOnlyProperty propSenderID = downloaded.getSettingProperties().get(AppSettings.ANDROID_GCM_SENDER_ID);
                        if (propSenderID != null) {
                            String senderID = (String) propSenderID.getValue();
                            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                            String regid = gcm.register(senderID);*//*

                            msg = "Device registered, registration ID=" + regid;
                            // You should send the registration ID to your
                            // server over HTTP, so it
                            // can use GCM/HTTP or CCS to send messages to your
                            // app.
                            sendRegistrationIdToBackend(regid); // Persist the regID - no need to register again.
                            storeRegistrationId(regid);
//                        }
                    }

                } catch (GenericException e) {
                    msg = "registerInBackground AgencyAppSettingsException :" + e.getMessage();
                    Log.e(TAG, "registerInBackground", e);
                } catch (LogonCoreException e) {
                    msg = "registerInBackground LogonCoreException :" + e.getMessage();
                    Log.e(TAG, "registerInBackground", e);
                }catch (Exception e) {
                    msg = "registerInBackground AgencyAppSettingsException :" + e.getMessage();
                    Log.e(TAG, "registerInBackground", e);
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d(TAG, msg);
            }
        }.execute(null, null, null);
    }

    */
/**
     * @return Application's version code from the {@code PackageManager}.
     *//*

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    */
/**
     * @return Application's {@code SharedPreferences}.
     *//*

    private static SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(GcmManager.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    */
/**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to       * send      * messages to your app. Not needed for this demo since the device sends upstream messages      * to a server that echoes back the message using the 'from' address in the message.      * @throws GenericException
     *//*

    private static void sendRegistrationIdToBackend(String gcmRegistrationId) throws GenericException, LogonCoreException {
        if (gcmRegistrationId != null) {
            FcmAppSettDownListener settingsListener = FcmAppSettDownListener.getInstance();
            AppSettings downloaded = settingsListener.getAppSettings(); //Get the GCM Sender ID from the server
            AppSettingsProperty gcmRegId = (AppSettingsProperty) downloaded.getSettingProperties().get(AppSettings.ANDROID_GCM_REGISTRATION_ID);
            //Update GCM Sender ID
            gcmRegId.setValue(gcmRegistrationId);
            HashMap<String, AppSettingsProperty> gcmPushValues = new HashMap<>();
            gcmPushValues.put(AppSettings.ANDROID_GCM_REGISTRATION_ID, gcmRegId); //Upload changes
            FcmAppSettManager.uploadAppSettings(gcmPushValues);
        }
    }
}
*/
