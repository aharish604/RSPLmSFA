package com.rspl.sf.msfa.SPGeo.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.rspl.sf.msfa.SPGeo.database.DatabaseHelperGeo;
import com.rspl.sf.msfa.SPGeo.database.ServiceStartStopBean;
import com.rspl.sf.msfa.backgroundlocationtracker.TrackerService;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;

import java.text.DateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class AlaramRecevier extends BroadcastReceiver {
    private static final String JOB_TAG = "MyJobService";
    private FirebaseJobDispatcher mDispatcher;


    public AlaramRecevier() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        DatabaseHelperGeo databaseHelper = DatabaseHelperGeo.getInstance(context);
        Date dateMillSec = new Date();
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(dateMillSec);
        ServiceStartStopBean startStopBean = new ServiceStartStopBean(currentDateTimeString, "ServiceStart");
        databaseHelper.createRecordService(startStopBean);
//        databaseHelper.getData();
        Log.d(TAG, "Job Started Clicked");
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

        mDispatcher.mustSchedule(myJob);*/
        context.startService(new Intent(context, TrackerService.class));
    //    ConstantsUtils.startAutoSync(context,true);
//        ConstantsUtils.startAutoSyncLocation(context,true);
    }


    /*@Override
    protected void onHandleIntent(@Nullable Intent intent) {
        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        DatabaseHelperGeo databaseHelper = DatabaseHelperGeo.getInstance(this);
        Date dateMillSec = new Date();
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(dateMillSec);
        ServiceStartStopBean startStopBean = new ServiceStartStopBean(currentDateTimeString, "ServiceStart");
        databaseHelper.createRecordService(startStopBean);
        databaseHelper.getData();
        Log.d(TAG, "Job Started Clicked");
//        mDispatcher.newRetryStrategy(RetryStrategy.RETRY_POLICY_EXPONENTIAL,60*10, 60*15);
        mDispatcher.newRetryStrategy(RetryStrategy.RETRY_POLICY_EXPONENTIAL, 60 * 5, 60 * 6);//Repeat interval time
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
    }*/
}
