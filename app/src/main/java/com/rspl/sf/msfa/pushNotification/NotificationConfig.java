package com.rspl.sf.msfa.pushNotification;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by e10769 on 07-08-2017.
 */

public class NotificationConfig {
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String PUSH_NOTIFICATION_FOREGROUND = "pushNotificationForeground";


    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";

    // Fetches reg id from shared preferences
    // and displays on the screen
    public static void sapRegistrationFirebaseRegId(Context mContext) {
        SharedPreferences pref = mContext.getSharedPreferences(NotificationConfig.SHARED_PREF, 0);
        String regId = pref.getString("regId", "");
        if (!TextUtils.isEmpty(regId))
//            GcmManager.registerInBackground(regId);
        Log.e("displayFirebaseRegId", "Firebase reg id: " + regId);
    }
    /*get device registration id*/
    public static String getFirebaseRegId(Context mContext) {
        SharedPreferences pref = mContext.getSharedPreferences(NotificationConfig.SHARED_PREF, 0);
        return pref.getString("regId", "");
    }
}
