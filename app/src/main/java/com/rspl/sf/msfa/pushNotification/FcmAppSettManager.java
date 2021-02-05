/*
package com.rspl.sf.msfa.pushNotification;

*/
/**
 * Created by e10769 on 07-08-2017.
 *//*


import android.util.Log;

import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreException;
import com.sap.maf.tools.logon.core.reg.AppSettings;
import com.sap.maf.tools.logon.core.reg.AppSettingsProperty;

import java.util.HashMap;

*/
/**
 * This class handles the download and upload of the application  * settings to the SAP Mobile Platform
 *//*

public class FcmAppSettManager {
    public static final String TAG = FcmAppSettManager.class.getSimpleName();

    */
/**
     * Download all application settings      * @return true if the download was successful, false otherwise      * @throws GenericException
     *//*

    public static boolean downloadAppSettings() throws Exception {
        FcmAppSettDownListener settingsListener = FcmAppSettDownListener.getInstance();
        if (settingsListener.getAppSettings() == null) {
            try {
                LogonCore logonCore = LogonCore.getInstance();
                AppSettings settings = logonCore.getAppSettings();
                settings.setListener(settingsListener);
                settings.downloadAppSettings();
                settingsListener.waitForCompletion();
                if (settingsListener.getError() != null) {
                    Log.e(TAG, "registerInBackground", settingsListener.getError());
                }
            } catch (LogonCoreException e) {
                e.printStackTrace();
                Log.e(TAG, "registerInBackground", e);
                throw new GenericException(e);
            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG, "registerInBackground", e);
                throw new GenericException(e);
            }
            AppSettings settings = settingsListener.getAppSettings();
            if (settings != null) {
                Log.d(TAG, "downloadAppSettings::AppSettings downloaded");
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    */
/**
     * Upload application settings      * @param appSettingsValue The settings that will be uploaded to the SAP mobile          * platform      * @throws GenericException
     *//*

    public static void uploadAppSettings(HashMap<String, AppSettingsProperty> appSettingsValue) throws GenericException {
        FcmAppSettUpListener uploadListener = FcmAppSettUpListener.getInstance();
        try {
            LogonCore logonCore = LogonCore.getInstance();
            AppSettings settings = logonCore.getAppSettings();
            settings.setListener(uploadListener);
            settings.updateAppSettings(appSettingsValue);
            uploadListener.waitForCompletion();
            if (uploadListener.getError() != null) {
                Log.e(TAG, "uploadAppSettings", uploadListener.getError());
            }
        } catch (LogonCoreException e) {
            throw new GenericException(e);
        }
    }
}
*/
