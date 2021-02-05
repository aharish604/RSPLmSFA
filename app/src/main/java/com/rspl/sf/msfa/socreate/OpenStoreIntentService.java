package com.rspl.sf.msfa.socreate;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.rspl.sf.msfa.common.MSFAApplication;
import com.rspl.sf.msfa.store.OnlineManager;
import com.rspl.sf.msfa.store.OnlineODataStoreException;

public class OpenStoreIntentService extends IntentService {
    public static String ACTION_SERVICE_KEY = "actionService";
    public static String EXTRA_FINISHED_KEY = "isFinished";
    public static String EXTRA_ERROR_KEY = "serviceErrorMsg";
    private boolean storeOpened=false;

    public OpenStoreIntentService() {
        super("OpenStoreIntentService");
    }

    public static void startServices(Context context) {
        Intent intent = new Intent(context, OpenStoreIntentService.class);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {
                storeOpened = OnlineManager.openOnlineStore(OpenStoreIntentService.this, true);
                setServiceFinished(true,"Success");
            } catch (OnlineODataStoreException e) {
                e.printStackTrace();
                setServiceFinished(true,e.getMessage());
            }

        }
    }

    public void setServiceFinished(boolean isFinished, String serviceErrorMsg) {
        MSFAApplication mApplication = (MSFAApplication) getApplication();
        mApplication.setServiceFinished(isFinished, serviceErrorMsg);
    }
}
