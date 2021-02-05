package com.rspl.sf.msfa.store;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.registration.RegistrationModel;
import com.arteriatech.mutils.registration.UtilRegistrationActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.MSFAApplication;
import com.rspl.sf.msfa.main.MainMenu;

public class WaitTillStoreOpenActivity extends AppCompatActivity {
    private MSFAApplication mApplication = null;
    private boolean isFromRegistration = false;
    private RegistrationModel registrationModel = null;
    private String TAG = "WaitTillStoreOpenActivity";
    Handler handler = new Handler();
    Runnable runnable = null;
    private boolean isMainActivityOpened = false;
    private SharedPreferences sharedPerf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_till_store_open);
        setTheme(R.style.AppThemeNoActionBar);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            isFromRegistration = bundleExtras.getBoolean(UtilRegistrationActivity.EXTRA_IS_FROM_REGISTRATION, false);
            registrationModel = (RegistrationModel) bundleExtras.getSerializable(UtilConstants.RegIntentKey);
        }
        sharedPerf = getSharedPreferences(Constants.PREFS_NAME, 0);
       boolean isregFlag =  sharedPerf.getBoolean(Constants.isFirstTimeReg, false);
        mApplication = (MSFAApplication) getApplicationContext();
        if(!isregFlag && (sharedPerf.getInt(Constants.CURRENT_VERSION_CODE, 0) == Constants.NewDefingRequestVersion) ) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            // execute some code
                            if(!isMainActivityOpened)
                                startMainActivity();
                        }
                    };
                    handler.postDelayed(runnable, 6000);

                }
            });
            waitForStoreOpen(WaitTillStoreOpenActivity.this);
        }
        else
            startMainActivity();


    }
    private void waitForStoreOpen(Context mContext) {
        if (mApplication.isServiceFinished()) {
            //displayUI(mApplication);
            isMainActivityOpened = true;
            if(runnable!=null)
            handler.removeCallbacksAndMessages(runnable);
            startMainActivity();
        } else {
            mApplication.setBroadCastReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d(TAG, "onReceive: ");
                    if (intent.getAction().equals(MSFAApplication.ACTION_SERVICE_KEY)) {
                        /*if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();*/
                        mApplication.unRegisterReceiver();
                        isMainActivityOpened = true;
                        //displayUI(mApplication);(
                        if(runnable!=null)
                            handler.removeCallbacksAndMessages(runnable);
                        startMainActivity();
                    }else{
                        isMainActivityOpened = true;
                        if(runnable!=null)
                            handler.removeCallbacksAndMessages(runnable);
                        startMainActivity();
                    }
                }
            });
           /* if (isFromRegistration)
                progressDialog = ConstantsUtils.showProgressDialog(mContext, pDialogMessage);
            else
                refreshUI(MainActivity.this);*/
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainMenu.class);
        intent.putExtra(UtilConstants.RegIntentKey, registrationModel);
        intent.putExtra(UtilRegistrationActivity.EXTRA_IS_FROM_REGISTRATION,isFromRegistration);
        startActivity(intent);
        finish();
    }

}
