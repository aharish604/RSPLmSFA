package com.rspl.sf.msfa.pushNotification;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.main.MainMenu;
import com.rspl.sf.msfa.mtp.approval.MTPApprovalActivity;
import com.rspl.sf.msfa.mtp.approval.MTPApprovalDetailActivity;
import com.rspl.sf.msfa.registration.RegistrationActivity;
import com.rspl.sf.msfa.soapproval.ApprovalListDetails;
import com.rspl.sf.msfa.soapproval.SOApproveActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by e10769 on 07-08-2017.
 */

public class NotificationUtils {
    private static String TAG = NotificationUtils.class.getSimpleName();
    private static ArrayList<String> messageList = new ArrayList<>();
    private static ArrayList<String> messageListSO = new ArrayList<>();
    private static ArrayList<String> messageListContract = new ArrayList<>();
    private static ArrayList<String> messageListCredit = new ArrayList<>();
    private String CONTRACT = "CONTRACT";
    private String SO = "SO";
    private String CREDIT = "Credit Limit";
    private String MTP = "Route";


    private Context mContext;
    private int approvalFor = 0;// 1- SO ,2- Credit limit , 3 - Contract limit,4 - All
    private int soCount = 0;
    private int contractCount = 0;
    private int creditCount = 0;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        messageList.clear();
        messageListSO.clear();
        messageListContract.clear();
        messageListCredit.clear();
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public void showNotificationMessage(String title, String message, String timeStamp) {
        showNotificationMessage(title, message, timeStamp, null);
    }

    public void showNotificationMessage(final String title, final String message, final String timeStamp, String imageUrl) {
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;
        messageList.add(message);
        boolean multipleNotification = false;
        boolean multipleNotificationSO = false;
        boolean multipleNotificationMTP = false;
        boolean multipleNotificationContract = false;
        boolean multipleNotificationCredit = false;

        if (messageList.size() > 1) {
            for (String s : messageList) {
                if (s.contains(SO)) {
                    messageListSO.add(s);
                    multipleNotificationSO = true;
                    if (multipleNotificationContract || multipleNotificationCredit || multipleNotificationMTP)
                        multipleNotification = true;
                }
                if (s.contains(CONTRACT)) {
                    multipleNotificationContract = true;
                    messageListContract.add(s);
                    if (multipleNotificationSO || multipleNotificationCredit || multipleNotificationMTP) {
                        multipleNotification = true;
                    }
                }
                if (!multipleNotification && s.contains(CREDIT)) {
                    multipleNotificationCredit = true;
                    messageListCredit.add(s);
                    if (multipleNotificationSO || multipleNotificationContract || multipleNotificationMTP) {
                        multipleNotification = true;
                    }
                }
                if (!multipleNotification && s.contains(MTP)) {
                    multipleNotificationMTP = true;
                    messageListCredit.add(s);
                    if (multipleNotificationSO || multipleNotificationContract || multipleNotificationCredit) {
                        multipleNotification = true;
                    }
                }
            }
        } else {
            if (message.contains(SO)) {
                multipleNotificationSO = true;
            } else if (message.contains(CONTRACT)) {
                multipleNotificationContract = true;
            } else if (message.contains(CREDIT)) {
                multipleNotificationCredit = true;
            } else if (message.contains(MTP)) {
                multipleNotificationMTP = true;
            }
        }

        int from = 0;
        // notification icon
        final int icon = R.mipmap.ic_app_launcher;
        Intent intent = null;
        TaskStackBuilder stackBuilder = null;
        if (multipleNotification) {
            stackBuilder = goToDashBoard(message);
        } else if (multipleNotificationSO) {
            if (messageListSO.size() > 1)
                from = 1;
            else
                from = 2;
            stackBuilder = SOStackBuilder(message, from);
        }/* else if (multipleNotificationContract) {
            if (messageListContract.size() > 1)
                from = 1;
            else
                from = 2;
            stackBuilder = contractStackBuilder(message, from);
        } else if (multipleNotificationCredit) {
            if (messageListCredit.size() > 1)
                from = 1;
            else
                from = 2;
            stackBuilder = creditLimitStackBuilder(message, from);
        }*/ else if (multipleNotificationMTP) {
            if (messageListCredit.size() > 1)
                from = 1;
            else
                from = 2;
            stackBuilder = getMTPStackBuilder(message, from);
        }

        Log.d("Approval count", "Approval count--" + soCount + "--" + creditCount + "--" + contractCount + "--" + multipleNotification);
        if (stackBuilder != null) {
            final PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);

            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    mContext);

            final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification");

            showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
            playNotificationSound();
        }
    }

    private TaskStackBuilder getMTPStackBuilder(String message, int from) {
        Intent intent;
        TaskStackBuilder stackBuilder = null;
        if (!NotificationUtils.isAppIsInBackground(mContext)) {
            if (from == 1) {
                Intent intentComeBack = new Intent(mContext, MainMenu.class);
                intentComeBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentComeBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent = new Intent(mContext, MTPApprovalActivity.class);
                stackBuilder = TaskStackBuilder.create(mContext);
                // Adds the back stack
                stackBuilder.addParentStack(MTPApprovalActivity.class);
                // Adds the Intent to the top of the stack
                stackBuilder.addNextIntent(intentComeBack);
                stackBuilder.addNextIntent(intent);
                intent.putExtra(Constants.EXTRA_VIEW_ID, from);
                intent.putExtra(Constants.EXTRA_NOTIFICATION, message);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            } else {
                Intent intentComeBack = new Intent(mContext, MainMenu.class);
                intentComeBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentComeBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent = new Intent(mContext, MTPApprovalDetailActivity.class);
                stackBuilder = TaskStackBuilder.create(mContext);
                // Adds the back stack
                stackBuilder.addParentStack(MTPApprovalDetailActivity.class);
                // Adds the Intent to the top of the stack
                stackBuilder.addNextIntent(intentComeBack);
                stackBuilder.addNextIntent(intent);
                intent.putExtra(Constants.EXTRA_VIEW_ID, from);
                intent.putExtra(Constants.EXTRA_NOTIFICATION, message);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            }
        } else {
            Intent intentComeBack = new Intent(mContext, MainMenu.class);
            intentComeBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intentComeBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent = new Intent(mContext, RegistrationActivity.class);
            stackBuilder = TaskStackBuilder.create(mContext);
            // Adds the back stack
            stackBuilder.addParentStack(RegistrationActivity.class);
            // Adds the Intent to the top of the stack
            stackBuilder.addNextIntent(intentComeBack);
            stackBuilder.addNextIntent(intent);
            intent.putExtra(Constants.EXTRA_NOTIFICATION, message);
            intent.putExtra(Constants.EXTRA_VIEW_ID, from);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        return stackBuilder;
    }

    private TaskStackBuilder goToDashBoard(String message) {
        Intent intent;
        TaskStackBuilder stackBuilder = null;
        if (!NotificationUtils.isAppIsInBackground(mContext)) {
            final SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
            intent = new Intent(mContext, MainMenu.class);
            stackBuilder = TaskStackBuilder.create(mContext);
           /* // Adds the back stack
            stackBuilder.addParentStack(MainMenu.class);*/
            // Adds the Intent to the top of the stack
            stackBuilder.addNextIntent(intent);
            intent.putExtra(Constants.EXTRA_VIEW_ID, 1);
            intent.putExtra(Constants.fromNotificationDetail, true);
            intent.putExtra(Constants.EXTRA_NOTIFICATION, message);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(Constants.EXTRA_FROM_CC, false);

        } else {
            intent = new Intent(mContext, RegistrationActivity.class);
            stackBuilder = TaskStackBuilder.create(mContext);
            // Adds the back stack
            stackBuilder.addParentStack(MainMenu.class);
            // Adds the Intent to the top of the stack
            stackBuilder.addNextIntent(intent);
            intent.putExtra(Constants.EXTRA_NOTIFICATION, message);
            intent.putExtra(Constants.EXTRA_VIEW_ID, 1);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        return stackBuilder;
    }

   /* private TaskStackBuilder creditLimitStackBuilder(String message, int from) {
        Intent intent;
        TaskStackBuilder stackBuilder = null;
        if (!NotificationUtils.isAppIsInBackground(mContext)) {
            final SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
            if (from == 1) {
                Intent intentComeBack = new Intent(mContext, MainMenu.class);
                intentComeBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentComeBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent = new Intent(mContext, CreditLimitApprovalActivity.class);
                stackBuilder = TaskStackBuilder.create(mContext);
                // Adds the back stack
                stackBuilder.addParentStack(CreditLimitApprovalActivity.class);
                // Adds the Intent to the top of the stack
                stackBuilder.addNextIntent(intentComeBack);
                stackBuilder.addNextIntent(intent);
                intent.putExtra(Constants.EXTRA_VIEW_ID, from);
                intent.putExtra(Constants.EXTRA_NOTIFICATION, message);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra(Constants.CustomerNo, sharedPreferences.getString(Constants.CPNo, ""));
                intent.putExtra(Constants.CustomerName, sharedPreferences.getString(Constants.RetailerName, ""));
            } else {
                Intent intentComeBack = new Intent(mContext, MainMenu.class);
                intentComeBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentComeBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent = new Intent(mContext, CreditLmtAprlDetailsActivity.class);
                stackBuilder = TaskStackBuilder.create(mContext);
                // Adds the back stack
                stackBuilder.addParentStack(CreditLmtAprlDetailsActivity.class);
                // Adds the Intent to the top of the stack
                stackBuilder.addNextIntent(intentComeBack);
                stackBuilder.addNextIntent(intent);
                intent.putExtra(Constants.EXTRA_VIEW_ID, from);
                intent.putExtra(Constants.fromNotificationDetail, true);
                intent.putExtra(Constants.EXTRA_NOTIFICATION, message);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra(Constants.EXTRA_FROM_CC, false);
                intent.putExtra(Constants.CustomerNo, sharedPreferences.getString(Constants.CPNo, ""));
                intent.putExtra(Constants.CustomerName, sharedPreferences.getString(Constants.RetailerName, ""));
            }
        } else {
            Intent intentComeBack = new Intent(mContext, MainMenu.class);
            intentComeBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intentComeBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent = new Intent(mContext, RegistrationActivity.class);
            stackBuilder = TaskStackBuilder.create(mContext);
            // Adds the back stack
            stackBuilder.addParentStack(RegistrationActivity.class);
            // Adds the Intent to the top of the stack
            stackBuilder.addNextIntent(intentComeBack);
            stackBuilder.addNextIntent(intent);
            intent.putExtra(Constants.EXTRA_NOTIFICATION, message);
            intent.putExtra(Constants.EXTRA_VIEW_ID, from);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        return stackBuilder;
    }

    private TaskStackBuilder contractStackBuilder(String message, int from) {
        Intent intent;
        TaskStackBuilder stackBuilder = null;
        if (!NotificationUtils.isAppIsInBackground(mContext)) {
            final SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
            if (from == 1) {
                Intent intentComeBack = new Intent(mContext, MainMenu.class);
                intentComeBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentComeBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent = new Intent(mContext, SOApproveActivity.class);
                stackBuilder = TaskStackBuilder.create(mContext);
                // Adds the back stack
                stackBuilder.addParentStack(SOApproveActivity.class);
                // Adds the Intent to the top of the stack
                stackBuilder.addNextIntent(intentComeBack);
                stackBuilder.addNextIntent(intent);
                intent.putExtra(Constants.EXTRA_VIEW_ID, from);
                intent.putExtra(Constants.EXTRA_NOTIFICATION, message);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra(Constants.fromNotificationDetail, true);
                intent.putExtra(Constants.CustomerNo, sharedPreferences.getString(Constants.CPNo, ""));
                intent.putExtra(Constants.CustomerName, sharedPreferences.getString(Constants.RetailerName, ""));
                //  intent.putExtra(Constants.EXTRA_CUSTOMER_REGION, sharedPreferences.getString(Constants.KEY_CUSTOMER_REGION, ""));//TODO region id passing need to change
                intent.putExtra(Constants.comingFrom, 1);
                //  mContext.startActivity(intent);

            } else {
                Intent intentComeBack = new Intent(mContext, MainMenu.class);
                intentComeBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentComeBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent = new Intent(mContext, ContractDetailsActivity.class);
                stackBuilder = TaskStackBuilder.create(mContext);
                // Adds the back stack
                stackBuilder.addParentStack(ContractDetailsActivity.class);
                // Adds the Intent to the top of the stack
                stackBuilder.addNextIntent(intentComeBack);
                stackBuilder.addNextIntent(intent);
                intent.putExtra(Constants.EXTRA_VIEW_ID, from);
                intent.putExtra(Constants.fromNotificationDetail, true);
                intent.putExtra(Constants.EXTRA_NOTIFICATION, message);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra(Constants.EXTRA_FROM_CC, false);
                intent.putExtra(Constants.CustomerNo, sharedPreferences.getString(Constants.CPNo, ""));
                intent.putExtra(Constants.CustomerName, sharedPreferences.getString(Constants.RetailerName, ""));
                intent.putExtra(Constants.comingFrom, 1);// intent its fro contract approval
            }
        } else {
            Intent intentComeBack = new Intent(mContext, MainMenu.class);
            intentComeBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intentComeBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent = new Intent(mContext, RegistrationActivity.class);
            stackBuilder = TaskStackBuilder.create(mContext);
            // Adds the back stack
            stackBuilder.addParentStack(RegistrationActivity.class);
            // Adds the Intent to the top of the stack
            stackBuilder.addNextIntent(intentComeBack);
            stackBuilder.addNextIntent(intent);
            intent.putExtra(Constants.EXTRA_NOTIFICATION, message);
            intent.putExtra(Constants.EXTRA_VIEW_ID, from);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        return stackBuilder;
    }*/

    private TaskStackBuilder SOStackBuilder(String message, int from) {
        Intent intent;
        TaskStackBuilder stackBuilder = null;
        final SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        if (!NotificationUtils.isAppIsInBackground(mContext)) {
            if (from == 1) {
                Intent intentComeBack = new Intent(mContext, MainMenu.class);
                intentComeBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentComeBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent = new Intent(mContext, SOApproveActivity.class);
                stackBuilder = TaskStackBuilder.create(mContext);
                // Adds the back stack
                stackBuilder.addParentStack(SOApproveActivity.class);
                // Adds the Intent to the top of the stack
                stackBuilder.addNextIntent(intentComeBack);
                stackBuilder.addNextIntent(intent);
                intent.putExtra(Constants.EXTRA_VIEW_ID, from);
                intent.putExtra(Constants.EXTRA_NOTIFICATION, message);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra(Constants.fromNotificationDetail, true);
                intent.putExtra(Constants.CustomerNo, sharedPreferences.getString(Constants.CPNo, ""));
                intent.putExtra(Constants.CustomerName, sharedPreferences.getString(Constants.RetailerName, ""));
                //  intent.putExtra(Constants.EXTRA_CUSTOMER_REGION, sharedPreferences.getString(Constants.KEY_CUSTOMER_REGION, ""));//TODO region id passing need to change
                intent.putExtra(Constants.comingFrom, 0);
                //  mContext.startActivity(intent);

            } else {
                Intent intentComeBack = new Intent(mContext, MainMenu.class);
                intentComeBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentComeBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent = new Intent(mContext, ApprovalListDetails.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                stackBuilder = TaskStackBuilder.create(mContext);
                // Adds the back stack
                stackBuilder.addParentStack(ApprovalListDetails.class);
//                stackBuilder.addNextIntentWithParentStack(new Intent(mContext, ApprovalListDetails.class));
                // Adds the Intent to the top of the stack
                stackBuilder.addNextIntent(intentComeBack);
                stackBuilder.addNextIntent(intent);
                intent.putExtra(Constants.EXTRA_VIEW_ID, from);
                intent.putExtra(Constants.fromNotificationDetail, true);
                intent.putExtra(Constants.EXTRA_NOTIFICATION, message);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                //  intent.putExtra(Constants.comingFrom, Constants.SO_LIST_POS_3);
                intent.putExtra(Constants.EXTRA_FROM_CC, false);
                intent.putExtra(Constants.CustomerNo, sharedPreferences.getString(Constants.CPNo, ""));
                intent.putExtra(Constants.CustomerName, sharedPreferences.getString(Constants.RetailerName, ""));
                // intent.putExtra(Constants.EXTRA_CUSTOMER_REGION, sharedPreferences.getString(Constants.KEY_CUSTOMER_REGION, ""));
            }
        } else {
            Intent intentComeBack = new Intent(mContext, MainMenu.class);
            intentComeBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intentComeBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent = new Intent(mContext, RegistrationActivity.class);
            stackBuilder = TaskStackBuilder.create(mContext);
            // Adds the back stack
            stackBuilder.addParentStack(RegistrationActivity.class);
            // Adds the Intent to the top of the stack
            stackBuilder.addNextIntent(intentComeBack);
            stackBuilder.addNextIntent(intent);
            intent.putExtra(Constants.EXTRA_NOTIFICATION, message);
            intent.putExtra(Constants.EXTRA_VIEW_ID, from);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        return stackBuilder;
    }

    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {
        try {
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            for (String messages : messageList) {
                inboxStyle.addLine(messages);
            }

            Notification notification;
            notification = mBuilder.setTicker(title)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setNumber(messageList.size())
                    .setPriority(Notification.PRIORITY_MAX)
                    .setStyle(inboxStyle)
//                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setLights(Color.RED, 5000, 5000)
                    .setWhen(getTimeMilliSec(timeStamp))
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .setSmallIcon(R.mipmap.ic_app_launcher)
//                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                    .setContentText(message)
                    .setGroupSummary(true)
                    .setGroup("Group")
//                    .setSubText(String.valueOf(messageList.size()))
                    .build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
            notificationManager.notify(0, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Playing notification sound
    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
