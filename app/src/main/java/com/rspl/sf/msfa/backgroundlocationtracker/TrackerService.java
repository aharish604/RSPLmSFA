/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rspl.sf.msfa.backgroundlocationtracker;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.registration.RegistrationModel;
import com.arteriatech.mutils.support.PasscodeActivity;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.rspl.sf.msfa.BuildConfig;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.SPGeo.database.DatabaseHelperGeo;
import com.rspl.sf.msfa.SPGeo.database.LocationBean;
import com.rspl.sf.msfa.autosync.AutoSyncDataAlarmReceiver;
import com.rspl.sf.msfa.autosync.AutoSyncDataLocationAlarmReceiver;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.login.LoginActivity;
import com.rspl.sf.msfa.main.MainMenu;
import com.rspl.sf.msfa.registration.Configuration;
import com.rspl.sf.msfa.registration.RegistrationActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static com.rspl.sf.msfa.SPGeo.services.LocationMonitoringService.locationLog;

public class TrackerService extends Service implements LocationListener /*, LogonListener*/ {

    public static final String STATUS_INTENT = "status";
    // to check internet background starts
    static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    // to check internet background ends
    private static final String TAG = TrackerService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 2;
    private static final int FOREGROUND_SERVICE_ID = 1;
    private static final int CONFIG_CACHE_EXPIRY = 600;  // 10 minutes.
    private static TrackerService instance = null;
    NotificationManager manager;
    File path = null;
    LatLng origin, destination;
    Polyline line;
    private GoogleApiClient mGoogleApiClient = null;
    private DatabaseReference mFirebaseTransportRef;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private LinkedList<Map<String, Object>> mTransportStatuses = new LinkedList<>();
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;
    private PowerManager.WakeLock mWakelock = null;
    private String currentDate = "";
    private String currentDateTimeString = "";
    private String mStrbatterLevel = "";
    private String mStrDistance = "";
    private String SMALLESTDISPLACEMENT = "";
    private String GEOSTRTTME = "";
    private String GEOENDTME = "";
    private int timeInterval = 30;
    private String doc_no = "";
    private int FASTEST_INTERVAL = 2000; // use whatever suits you
    private Location currentLocation = null;
    private long locationUpdatedAt = Long.MIN_VALUE;
    private SharedPreferences mPrefs;
    private AlertDialog.Builder builder;
    private AlertDialog alert=null;
    private SharedPreferences sharedPreferences=null;
    //    LogonCore logonCore = null;
    RegistrationModel registrationModel = new RegistrationModel();
//    private LogonUIFacade mLogonUIFacade;


    // thread
    ReentrantLock reentrantLock;
    private GoogleApiClient.ConnectionCallbacks mLocationRequestCallback = new GoogleApiClient
            .ConnectionCallbacks() {

        @Override
        public void onConnected(Bundle bundle) {
            LocationRequest request = new LocationRequest();
            //request.setInterval(mFirebaseRemoteConfig.getLong("LOCATION_REQUEST_INTERVAL"));
            request.setInterval(timeInterval * 1000);
            request.setFastestInterval(timeInterval * 1000);
            Log.d(TAG, "onConnected: timeInterval :" + timeInterval);
            // request.setSmallestDisplacement(Integer.parseInt(SMALLESTDISPLACEMENT));
            // request.setFastestInterval(mFirebaseRemoteConfig.getLong("LOCATION_REQUEST_INTERVAL_FASTEST"));
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if (ActivityCompat.checkSelfPermission(TrackerService.this, "android.permission.ACCESS_FINE_LOCATION") != 0 && ActivityCompat.checkSelfPermission(TrackerService.this, "android.permission.ACCESS_COARSE_LOCATION") != 0) {
                locationLog("Please grant permission for Location in app settings");
            } else {
                locationLog("Location Tracking Permission Granted");

            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    request, TrackerService.this);
            setStatusMessage(R.string.tracking);

            // Hold a partial wake lock to keep CPU awake when the we're tracking location.
            try {
                PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                if (powerManager != null) {
                    mWakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
                    mWakelock.acquire();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConnectionSuspended(int reason) {
            // TODO: Handle gracefully
        }
    };

    public TrackerService() {
    }
    private Context receiverContext;
    public TrackerService(Context context) {
        this.receiverContext =context;
    }

    public static boolean isInstanceCreated() {
        return instance != null;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //Session.setmGlobalReceiverCallback(this);
        //MSFAApplication.getInstance().setConnectivityListener(TrackerService.this);

        sharedPreferences = null;
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_MULTI_PROCESS);
        SMALLESTDISPLACEMENT = sharedPreferences.getString(getString(R.string.geo_smallest_displacement), "50");
        if (TextUtils.isEmpty(SMALLESTDISPLACEMENT))
            SMALLESTDISPLACEMENT = "50";



        GEOSTRTTME = sharedPreferences.getString(getString(R.string.geo_start_time), "8");
        if (TextUtils.isEmpty(GEOSTRTTME))
            GEOSTRTTME = "8";
        GEOENDTME = sharedPreferences.getString(getString(R.string.geo_end_time), "20");
        if (TextUtils.isEmpty(GEOENDTME))
            GEOENDTME = "20";
        timeInterval = sharedPreferences.getInt(getString(R.string.geo_location_interval_time), 30);

        path = new File(Environment.getExternalStoragePublicDirectory(""),
                "transport-tracker-log.txt");
        if (!path.exists()) {
            try {
                path.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            IntentFilter intentFilter= new IntentFilter();
            intentFilter.addAction(Intent.ACTION_TIME_TICK);
            registerReceiver(broadcastReceiver,intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        buildNotification();
        setStatusMessage(R.string.connecting);
        if (LocationUtils.isGPSEnabled(TrackerService.this) && GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(TrackerService.this) == 0) {
            if (ActivityCompat.checkSelfPermission(TrackerService.this, "android.permission.ACCESS_FINE_LOCATION") == 0 && ActivityCompat.checkSelfPermission(TrackerService.this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
                /*try {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(Constants.timer_flag,false);
                    editor.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                Constants.isFlagVisiable=false;
                startLocationTracking();
//                startTimer("");
            } else {
                locationLog("Location permission not enabled");
                stopSelf();
            }
        } else {
//            startTimer("");
            locationLog("GPS not enabled");
            stopSelf();
        }



       /* mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
                mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);*/

  /*      mPrefs = getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        String email = mPrefs.getString(getString(R.string.email), "");
        String password = mPrefs.getString(getString(R.string.password), "");*/
        //  authenticate(email, password);
//        registerReceiver(receiver, new IntentFilter("android.location.PROVIDERS_CHANGED"));
//        registerReceiver(receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
//        registerReceiver(receiver, new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED"));
        /*int cur_time = 0;
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat fmt = new SimpleDateFormat("HH");
            cur_time = Integer.parseInt(fmt.format(cal.getTime()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (cur_time >= Integer.parseInt(GEOSTRTTME) && cur_time < Integer.parseInt(GEOENDTME)) {
            startTimer("");
        }*/
    }

    @Override
    public void onDestroy() {
        int cur_time = 0;
        super.onDestroy();
        try {
            // Set activity title to not tracking.
            setStatusMessage(R.string.tracking_stopped);

            // Stop the persistent notification.
            //     mNotificationManager.cancel(NOTIFICATION_ID);

            // Stop receiving location updates.
            try {
                locationLog("Tracking service Stopped");
                if (mGoogleApiClient != null) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, TrackerService.this);
                }
                if(broadcastReceiver!=null) {
                    unregisterReceiver(broadcastReceiver);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Release the wakelock
            if (mWakelock != null) {
                mWakelock.release();
            }
            /*try {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Constants.timer_flag,false);
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            instance = null;
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat fmt = new SimpleDateFormat("HH");
            cur_time = Integer.parseInt(fmt.format(cal.getTime()));

        } catch (Throwable e) {
            e.printStackTrace();
        }finally {
            if(!sharedPreferences.getBoolean(getString(R.string.enable_geo), false)) {
                if (cur_time >= Integer.parseInt(GEOSTRTTME) && cur_time < Integer.parseInt(GEOENDTME)) {
                    Constants.setScheduleAlaram(this, 0, 00, 00, 0);
//                startTimer("");
                }
            }
        }


    }

    private void authenticate(String email, String password) {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        Log.i(TAG, "authenticate: " + task.isSuccessful());
                        if (task.isSuccessful()) {
                            fetchRemoteConfig();
                            //  loadPreviousStatuses();
                        } else {
                            Toast.makeText(TrackerService.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                            stopSelf();
                        }
                    }
                });
    }

    private void fetchRemoteConfig() {
        long cacheExpiration = CONFIG_CACHE_EXPIRY;
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Remote config fetched");
                        mFirebaseRemoteConfig.activateFetched();
                    }
                });
    }

    /**
     * Loads previously stored statuses from Firebase, and once retrieved,
     * start location tracking.
     */
    private void loadPreviousStatuses() {
        String transportId = mPrefs.getString(getString(R.string.transport_id), "");
        FirebaseAnalytics.getInstance(this).setUserProperty("transportID", transportId);
        String path = getString(R.string.firebase_path) + transportId;
        mFirebaseTransportRef = FirebaseDatabase.getInstance().getReference(path);
        mFirebaseTransportRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot != null) {
                    for (DataSnapshot transportStatus : snapshot.getChildren()) {
                        mTransportStatuses.add(Integer.parseInt(transportStatus.getKey()),
                                (Map<String, Object>) transportStatus.getValue());
                    }
                }
                startLocationTracking();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // TODO: Handle gracefully
            }
        });
    }

    /**
     * Starts location tracking by creating a Google API client, and
     * requesting location updates.
     */
    private void startLocationTracking() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(mLocationRequestCallback)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * Determines if the current location is approximately the same as the location
     * for a particular status. Used to check if we'll add a new status, or
     * update the most recent status of we're stationary.
     */
    private boolean locationIsAtStatus(Location location, int statusIndex) {
        if (mTransportStatuses.size() <= statusIndex) {
            return false;
        }
        Map<String, Object> status = mTransportStatuses.get(statusIndex);
        Location locationForStatus = new Location("");
        locationForStatus.setLatitude((double) status.get("lat"));
        locationForStatus.setLongitude((double) status.get("lng"));
//        float distance = location.distanceTo(locationForStatus);
        float distance = distanceCalculation(location.getLatitude(), location.getLongitude(), (double) status.get("lat"), (double) status.get("lng"));
        try {
            mStrDistance = String.valueOf(distance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            LogManager.writeLogInfo("Distance from status is " + String.valueOf(distance));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, String.format("Distance from status %s is %sm", statusIndex, distance));
        return distance < Integer.parseInt(SMALLESTDISPLACEMENT);
    }

    private float distanceCalculation(double lat1, double lon1, double lat2, double lon2) {

        origin = new LatLng(lat2, lon2);
        destination = new LatLng(lat1, lon1);
        float distance = 0.0f;
        int Radius = 6371;// radius of earth in Km
//        13.0169005,77.6689547
//        13.0171067,77.6694075
        double dLat = Math.toRadians(lat1 - lat2);
        double dLon = Math.toRadians(lon1 - lon2);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
//        LogManager.writeLogInfo("Distance in KM : " + String.valueOf(kmInDec));
        double meter = valueResult * 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
//        LogManager.writeLogInfo("Distance in MM : " + String.valueOf(meterInDec));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);
        distance = (float) (meter);
        //  build_retrofit_and_get_response("driving");
        return distance;
    }

    private double rad2deg(double rad) {
        return (rad *180.0 / Math.PI);
    }

    private double deg2rad(double deg) {
        return (deg *Math.PI / 180.0);

    }

    private int getBatteryLevel() {
        Intent batteryStatus = registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int batteryLevel = -1;
        int batteryScale = 1;
        int battery = 0;
        if (batteryStatus != null) {
            batteryLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, batteryLevel);
            batteryScale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, batteryScale);
        }
        try {
            battery = (int) (batteryLevel / (float) batteryScale * 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return /*batteryLevel / (float) batteryScale * 100*/battery;
    }

    private void logStatusToStorage(String data) {

        path = new File(Environment.getExternalStoragePublicDirectory(""),
                "transport-tracker-log.txt");
        if (!path.exists()) {
            try {
                path.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            if (path.exists()) {

                BufferedWriter writer = new BufferedWriter(new FileWriter(path.getAbsolutePath(), true));
                writer.write(data);
                writer.newLine();
                writer.close();

                /*FileWriter logFile = new FileWriter(path.getAbsolutePath());
                PrintWriter pw = new PrintWriter(logFile);
                pw.println();
                pw.println(data);
                pw.close();
                logFile.close();*/
            }
        } catch (Exception e) {
            Log.e(TAG, "Log file error", e);
        }

    }

    private void shutdownAndScheduleStartup(int when) {
        Log.i(TAG, "overnight shutdown, seconds to startup: " + when);
        com.google.android.gms.gcm.Task task = new OneoffTask.Builder()
                .setService(TrackerTaskService.class)
                .setExecutionWindow(when, when + 60)
                .setUpdateCurrent(true)
                .setTag(TrackerTaskService.TAG)
                .setRequiredNetwork(com.google.android.gms.gcm.Task.NETWORK_STATE_ANY)
                .setRequiresCharging(false)
                .build();
        GcmNetworkManager.getInstance(this).schedule(task);
        stopSelf();
    }

    /**
     * Pushes a new status to Firebase when location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
      /*  try {
            sharedQueue.put(location);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

*/
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        String currentDateandTime = sdf.format(new Date());
        if(location!=null) {

            int latitude = (int) location.getLatitude();
            int longitude = (int) location.getLongitude();
            if (latitude == 0 || longitude == 0) {
                LogManager.writeLogInfo("0 value for lat :" + latitude + ", or long" + longitude);
            }
            logStatusToStorage(currentDateandTime+"::"+"0 value for lat :" + location.getLatitude( )+ ", or long" + location.getLongitude());
            //com.arteriatech.mutils.log.LogManager.writeLogInfo("0 value for lat :" + latitude + ", or long" + longitude);

            boolean updateLocationandReport = false;
            if (currentLocation == null) {
                currentLocation = location;
                locationUpdatedAt = System.currentTimeMillis();
                updateLocationandReport = true;
            } else {
                long secondsElapsed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - locationUpdatedAt);
                if (secondsElapsed >= 180) {
                    stopSelf();
                    Constants.setScheduleAlaram(this, 0, 0, 10, 0);
                } else if (secondsElapsed >= TimeUnit.MILLISECONDS.toSeconds(FASTEST_INTERVAL)) {
                    // check location accuracy here
                    currentLocation = location;
                    locationUpdatedAt = System.currentTimeMillis();
                    updateLocationandReport = true;
                }
            }

            if (updateLocationandReport) {
                //send your location to server
                //   logStatusToStorage(TAG + "Step:1" + location.getLatitude() + "--" + location.getLongitude());
                doc_no = (System.currentTimeMillis() + "");
                Date dateMillSec = new Date();
                currentDateTimeString = DateFormat.getDateTimeInstance().format(dateMillSec);
                Log.d(TAG, "DateandTime : " + currentDateTimeString);
                currentDate = new SimpleDateFormat("yyyy-MM-dd").format(dateMillSec);
                Log.d(TAG, "Date : " + currentDate);

                try {
                    mStrbatterLevel = String.valueOf(getBatteryLevel());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat fmt = new SimpleDateFormat("HH");
                SimpleDateFormat fmt1 = new SimpleDateFormat("MM");
                int cur_time1 = Integer.parseInt(fmt1.format(cal.getTime()));
                int cur_time = Integer.parseInt(fmt.format(cal.getTime()));
                if (cur_time >= Integer.parseInt(GEOENDTME)) {
            /*ServiceStartStopBean startStopBean = new ServiceStartStopBean(currentDateTimeString, "ServiceStop");
            databaseHelper.createRecordService(startStopBean);*/
                    /*try {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(Constants.timer_flag,false);
                        editor.commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    Constants.isFlagVisiable=false;
                    try {
                        if(broadcastReceiver!=null) {
                            unregisterReceiver(broadcastReceiver);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Constants.setScheduleAlaram(TrackerService.this, Integer.parseInt(GEOSTRTTME), 0, 00, 1);
//                    stoptimertask();
                    ConstantsUtils.stopAlarmManagerByID(TrackerService.this, AutoSyncDataAlarmReceiver.class,AutoSyncDataAlarmReceiver.REQUEST_CODE);
                    ConstantsUtils.stopAlarmManagerByID(TrackerService.this, AutoSyncDataLocationAlarmReceiver.class,AutoSyncDataLocationAlarmReceiver.REQUEST_CODE);
                    stopSelf();
                } else {
                    //      logStatusToStorage(TAG + "Step:2 Before Distance Checking" + location.getLatitude() + "--" + location.getLongitude());
                    if (!locationIsAtStatus(location, 0)) {
                        //  logStatusToStorage(TAG + "Step:3 After distance Checking" + location.getLatitude() + "--" + location.getLongitude());
                        Map<String, Object> transportStatus = new HashMap<>();
                        transportStatus.put("lat", location.getLatitude());
                        transportStatus.put("lng", location.getLongitude());
                        transportStatus.put("time", new Date().getTime());

                        try {
                            transportStatus.put("power", getBatteryLevel());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        while (mTransportStatuses.size() >= 1) {
                            mTransportStatuses.removeLast();
                        }
                        mTransportStatuses.addFirst(transportStatus);
                        // We push the entire list at once since each key/index changes, to
                        // minimize network requests.
                        //   mFirebaseTransportRef.setValue(mTransportStatuses);
                        //    logStatusToStorage(TAG + "Step:4 Before store location" + location.getLatitude() + "--" + location.getLongitude());
                        locationLog("battery percentage received");
                        LocationBean locationBean = new LocationBean("", "", location.getLatitude() + "", location.getLongitude() + "", currentDate + "T00:00:00", "Time", "X", doc_no, currentDateTimeString, "false", mStrbatterLevel, mStrDistance);
                        //    logStatusToStorage(TAG + "Step:5 location bean created" + location.getLatitude() + "--" + location.getLongitude());
                        DatabaseHelperGeo databaseHelper = DatabaseHelperGeo.getInstance(TrackerService.this);
                        if (ConstantsUtils.isAutomaticTimeZone(this)) {
                            if (cur_time >= Integer.parseInt(GEOSTRTTME) && cur_time < Integer.parseInt(GEOENDTME)) {
                                if (latitude != 0 && longitude != 0) {
                                    databaseHelper.createRecord(locationBean);
                                }
                            }
                        }else {
                            showDialog(getString(R.string.dateTime_not_enable));
//                        Toast.makeText(getApplicationContext(), "Data Not Inserted", Toast.LENGTH_LONG).show();
                        }
                        // logStatusToStorage(TAG + "Step:6 Record Created" + location.getLatitude() + "--" + location.getLongitude());
                        locationLog("battery percentage received inserted in DB old/new Value(" + mStrbatterLevel + ")");

                        //  batteryLevel(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
/*
        LocationBean locationBean = new LocationBean("", "", lat + "", lng + "", currentDate + "T00:00:00", "Time", "X", doc_no, currentDateTimeString, mStrAppBackground,mStrbatterLevel);
        databaseHelper.createRecord(locationBean);*/

//        diconectLocation();
                        //  logStatusToStorage(TAG + "Step:7 Bfr get from Sqlite " + location.getLatitude() + "--" + location.getLongitude());
                        try {
                           /* if (!OfflineManager.isOfflineStoreOpen()) {
                                intializeRegistrationModel();
                                initLogonCore(TrackerService.this, registrationModel);
                                try {
                                    String password = sharedPreferences.getString("username", "");
                                    logonCore.unlockStore(password);
                                } catch (LogonCoreException var6) {
                                    var6.printStackTrace();
                                }


                                    try {
                                        OfflineManager.openOfflineStore(TrackerService.this, new UIListener() {
                                            @Override
                                            public void onRequestError(int i, Exception e) {
                                                Log.e("TrackerService", "Offline Store Open Falied");

                                            }

                                            @Override
                                            public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                                                Log.e("TrackerService", "Offline Store Open Success");

                                            }
                                        });
                                    } catch (OfflineODataStoreException e) {
                                        e.printStackTrace();
                                    }


                            }*/

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (reentrantLock == null) {
                                        reentrantLock = new ReentrantLock();
                                    }
                                    try {
                                        Log.e("TrackService REENTRANT:", "LOCKED");
                                        reentrantLock.lock();
                                        Constants.getDataFromSqliteDB(TrackerService.this,null);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.e("TrackService EXCEPTION", "ANR EXCEPTION OCCURRED");
                                    } finally {
                                        if (reentrantLock != null && reentrantLock.isHeldByCurrentThread()) {
                                            reentrantLock.unlock();
                                        }
                                        Log.e("TrackService REENTRANT:", "UNLOCKED FINALLY");
                                    }
                                }
                            }).start();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        //    storeLocation(location);
                    }

                    NetworkInfo info = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
                            .getActiveNetworkInfo();
                    boolean connected = info != null && info.isConnectedOrConnecting();
                    setStatusMessage(connected ? R.string.tracking : R.string.not_tracking);

                }
            }
        }
/*

        long hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int startupSeconds = (int) (mFirebaseRemoteConfig.getDouble("SLEEP_HOURS_DURATION") * 3600);
        if (hour == mFirebaseRemoteConfig.getLong("SLEEP_HOUR_OF_DAY")) {
            shutdownAndScheduleStartup(startupSeconds);
            return;
        }
*/


    }

    private void intializeRegistrationModel() {
        registrationModel = new RegistrationModel();
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

        registrationModel.setRegisterSuccessActivity(MainMenu.class);
        registrationModel.setLogInActivity(LoginActivity.class);
        registrationModel.setAppActionBarIcon(R.mipmap.ic_action_bar_logo);
        registrationModel.setAppLogo(R.drawable.arteria_new_logo_transparent);
        registrationModel.setAppVersionName(BuildConfig.VERSION_NAME);
        registrationModel.setEmainId(getString(R.string.register_support_email));
        registrationModel.setPhoneNo(getString(R.string.register_support_phone));
        registrationModel.setEmailSubject("");//getString(R.string.email_subject)
        registrationModel.setMainActivity(MainMenu.class);
        registrationModel.setRegisterActivity(RegistrationActivity.class);
        registrationModel.setRegisterSuccessActivity(PasscodeActivity.class);
    }

    private void storeLocation(Location location) {
        Log.d(TAG, "Sending info...Latitide :" + location.getLatitude() + " Longtidude :" + location.getLongitude());

       /* Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGITUDE, lng);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);*/

//        this.latitude = lat;
//        this.longitude = lng;
//        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
//        Date date = new Date();
//        String currentDateTimeString = null;
//        try {
//            currentDateTimeString = dateFormat.format(date);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        Log.d(TAG, currentDateTimeString);
    /*    boolean mBoolAppBackGround = isAppIsInBackground(getApplicationContext());
        if (mBoolAppBackGround) {
            mStrAppBackground = "Background";
        } else {
            mStrAppBackground = "Foreground";
        }*/
       /* if(isAppRunning(getApplicationContext(), "tracklocation.devdeeds.com.tracklocationproject")){
            mStrAppBackground = "Foreground";
        }else{
            mStrAppBackground = "Background";
        }*/


    }

    private void batteryLevel(final String lat, final String lng) {
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

                int batteryLevel = -1;
                int batteryScale = 1;
                if (intent != null) {
                    batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, batteryLevel);
                    batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, batteryScale);
                }
                float newbatteryValue = batteryLevel / (float) batteryScale * 100;


                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int level = -1;
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
                mStrbatterLevel = String.valueOf(level);
                locationLog("battery percentage received");
                LocationBean locationBean = new LocationBean("", "", lat + "", lng + "", currentDate + "T00:00:00", "Time", "X", doc_no, currentDateTimeString, "false", mStrbatterLevel, mStrDistance);
                DatabaseHelperGeo databaseHelper = DatabaseHelperGeo.getInstance(context);
                databaseHelper.createRecord(locationBean);
                locationLog("battery percentage received inserted in DB old/new Value(" + mStrbatterLevel + "/" + newbatteryValue + ")");
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }


    private void buildNotification() {

        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_app_launcher)
                    .setContentTitle("App is running in background")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(FOREGROUND_SERVICE_ID, notification);
        } else {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, RegistrationActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            mNotificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_app_launcher)
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setContentTitle(getString(R.string.app_name))
                    .setOngoing(true)
                    .setContentIntent(resultPendingIntent);
            startForeground(FOREGROUND_SERVICE_ID, mNotificationBuilder.build());
        }


    }


    /**
     * Sets the current status message (connecting/tracking/not tracking).
     */
    private void setStatusMessage(int stringId) {
      /*  mNotificationBuilder.setContentText(getString(stringId));
        mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());*/
        Log.e(TAG, getString(stringId));

        // Also display the status message in the activity.
      /*  Intent intent = new Intent(STATUS_INTENT);
        intent.putExtra(getString(R.string.status), stringId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);*/
    }


    private  BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
            final ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                if (manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
//                    Toast.makeText(context, "ON", Toast.LENGTH_SHORT).show();
                    stoptimertask();
                }else{
                    startTimer("GPS off");
//                    Toast.makeText(context, "OFF", Toast.LENGTH_SHORT).show();
                }
//                Intent pushIntent = new Intent(context, TrackerService.class);
//                context.startService(pushIntent);
            }

            /*else if(intent.getAction().matches("android.net.conn.CONNECTIVITY_CHANGE") || intent.getAction().matches("android.net.wifi.WIFI_STATE_CHANGED")){
                if (connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected() || connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
                    Toast.makeText(context, "ON", Toast.LENGTH_SHORT).show();
                    stoptimertask();
                }else{
                    startTimer("Internet off");
                    Toast.makeText(context, "OFF", Toast.LENGTH_SHORT).show();
                }
            }*//*else if(intent.getAction().matches("android.net.wifi.WIFI_STATE_CHANGED")){
                if (connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
                    Toast.makeText(context, "ON", Toast.LENGTH_SHORT).show();
//                    stoptimertask();
                }else{
//                    startTimer();
                    Toast.makeText(context, "OFF", Toast.LENGTH_SHORT).show();
//                    new MainMenu().showDialog();
                }
            }*/
        }
    };

    public void showDialog(final String type){
        if (receiverContext==null){
            receiverContext = this;
        }
        try {
           /* WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View mView = mInflater.inflate(R.layout.aboutus_activity, null);

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                    PixelFormat.TRANSLUCENT
            );

            wm.addView(mView, params);*/
            if (Constants.alert == null || !Constants.alert.isShowing()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (Settings.canDrawOverlays(receiverContext)) {
//                        if (alert == null || !alert.isShowing()) {
                        builder = new AlertDialog.Builder(receiverContext);
                        builder.setTitle("Geo Tracking");
                        builder.setIcon(R.mipmap.ic_app_launcher);
                        builder.setMessage(type);
//                builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //Do something
                                dialog.dismiss();
                                if (type.equalsIgnoreCase(receiverContext.getString(R.string.gps_not_enable))) {
                                    Intent I = new Intent(
                                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    I.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    receiverContext.startActivity(I);
                                } else if (type.equalsIgnoreCase(receiverContext.getString(R.string.dateTime_not_enable))) {
                                    Intent I = new Intent(
                                            Settings.ACTION_DATE_SETTINGS);
                                    I.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    receiverContext.startActivity(I);
                                } else if (type.equalsIgnoreCase(receiverContext.getString(R.string.internet_not_enable))) {
                                    try {
                                        internetSetting();
                                    } catch (Throwable throwable) {
                                        throwable.printStackTrace();
                                    }
                                }
                                /*Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
                                intent.putExtra("enabled", true);
                                sendBroadcast(intent);*/

                            }
                        });
                        Constants.alert = builder.create();
                        Constants.alert.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                        Constants.alert.setCancelable(false);
                        Constants.alert.show();
//                        }
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    if (Settings.canDrawOverlays(receiverContext)) {
//                        if (alert == null || !alert.isShowing()) {
                        builder = new AlertDialog.Builder(receiverContext);
                        builder.setTitle("Geo Tracking");
                        builder.setIcon(R.mipmap.ic_app_launcher);
                        builder.setMessage(type);
//                builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //Do something
                                dialog.dismiss();
                                if (type.equalsIgnoreCase(receiverContext.getString(R.string.gps_not_enable))) {
                                    Intent I = new Intent(
                                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    I.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    receiverContext.startActivity(I);
                                } else if (type.equalsIgnoreCase(receiverContext.getString(R.string.dateTime_not_enable))) {
                                    Intent I = new Intent(
                                            Settings.ACTION_DATE_SETTINGS);
                                    I.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    receiverContext.startActivity(I);
                                } else if (type.equalsIgnoreCase(receiverContext.getString(R.string.internet_not_enable))) {
                                    try {
                                        internetSetting();
                                    } catch (Throwable throwable) {
                                        throwable.printStackTrace();
                                    }
                                }
                                /*Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
                                intent.putExtra("enabled", true);
                                sendBroadcast(intent);*/

                            }
                        });
                        Constants.alert = builder.create();
                        Constants.alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        Constants.alert.setCancelable(false);
                        Constants.alert.show();
//                        }
                    }
                } else {
//                    if (alert == null || !alert.isShowing()) {
                    builder = new AlertDialog.Builder(receiverContext);
                    builder.setTitle("Geo Tracking");
                    builder.setIcon(R.mipmap.ic_app_launcher);
                    builder.setMessage(type);
//                builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Do something
                            dialog.dismiss();
                            if (type.equalsIgnoreCase(receiverContext.getString(R.string.gps_not_enable))) {
                                Intent I = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                I.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                receiverContext.startActivity(I);
                            } else if (type.equalsIgnoreCase(receiverContext.getString(R.string.dateTime_not_enable))) {
                                Intent I = new Intent(
                                        Settings.ACTION_DATE_SETTINGS);
                                I.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                receiverContext.startActivity(I);
                            } else if (type.equalsIgnoreCase(receiverContext.getString(R.string.internet_not_enable))) {
                                try {
                                    internetSetting();
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            }
                                /*Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
                                intent.putExtra("enabled", true);
                                sendBroadcast(intent);*/

                        }
                    });
                    Constants.alert = builder.create();
                    Constants.alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    Constants.alert.setCancelable(false);
                    Constants.alert.show();
//                    }
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void internetSetting() throws Throwable{
        try {
            Intent I = null;
            /**
             * ACTION_DATA_USAGE_SETTINGS Added in API level 28 (Android P)
             */
            I = new Intent(Settings.ACTION_DATA_USAGE_SETTINGS);
            I.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            receiverContext.startActivity(I);
        } catch (Exception e) {

            /**
             * ACTION_DATA_ROAMING_SETTINGS Added in API level 3 hence it work for all version lower to 28 API Level.
             */
            try {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                receiverContext.startActivity(intent);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }
    Timer timer = null;
    TimerTask timerTask = null;
    final Handler handler = new Handler();

    public void startTimer(String type) {
//        boolean isFlag = sharedPreferences.getBoolean(Constants.timer_flag, false);
        if(!Constants.isFlagVisiable) {


            /*try {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Constants.timer_flag, true);
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            Constants.isFlagVisiable=true;
            //set a new Timer
            if (timer == null) {
                timer = new Timer();

                //initialize the TimerTask's job
                initializeTimerTask(type);

                //schedule the timer, after the first 5000ms the TimerTask will run every 60000ms
                timer.schedule(timerTask, 5000, 60000); //
            }
        }
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timerTask.cancel();
            timer = null;
            timerTask = null;
        }
    }

    public void initializeTimerTask(final String type) {
        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp
//                        int duration = Toast.LENGTH_SHORT;
//                        Toast toast = Toast.makeText(getApplicationContext(), "Start timer", duration);
//                        toast.show();
//                        showDialog(type);


                        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        NetworkInfo info = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
                                .getActiveNetworkInfo();
                        boolean connected = info != null && info.isConnectedOrConnecting();
                        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            showDialog(getString(R.string.gps_not_enable));
                        } else if (!connected) {
                            showDialog(getString(R.string.internet_not_enable));
                        } else if (!ConstantsUtils.isAutomaticTimeZone(TrackerService.this)) {
                            showDialog(getString(R.string.dateTime_not_enable));
                        }
                    }
                });
            }
        };
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String value = intent.getAction();
            switch (value) {
                case Intent.ACTION_TIME_TICK:
                    try {
                        final LocationManager manager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
                        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                                .getActiveNetworkInfo();
                        boolean connected = info != null && info.isConnectedOrConnecting();
                        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            showDialog(context.getString(R.string.gps_not_enable));
                        } else if (!connected) {
                            showDialog(context.getString(R.string.internet_not_enable));
                        } else if (!ConstantsUtils.isAutomaticTimeZone(context)) {
                            showDialog(context.getString(R.string.dateTime_not_enable));
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(context, "Time Ticking", Toast.LENGTH_LONG).show();
                    break;
            }
        }

    };
    public BroadcastReceiver getInstance(){
        return broadcastReceiver;
    }

    public static Boolean isLocationEnabled(Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
// This is new method provided in API 28
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
// This is Deprecated in API 28
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return  (mode != Settings.Secure.LOCATION_MODE_OFF);

        }
    }
    private void initLogonCore(Context mContext, RegistrationModel registrationModel) {
        /*try {
            this.logonCore = LogonCore.getInstance();
            mLogonUIFacade = LogonUIFacade.getInstance();
            mLogonUIFacade.init(this, mContext, registrationModel.getAppID());
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
        }*/

    }

   /* @Override
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
}
