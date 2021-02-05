package com.rspl.sf.msfa.backgroundlocationtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (!UtilConstants.isNetworkAvailable(context)) {
            // Do something

            if(Constants.isBackGroundSync)Constants.isBackGroundSync=false;
            if(Constants.iSAutoSync)Constants.iSAutoSync=false;
            if(Constants.isSync)Constants.isSync=false;
            if(Constants.isPullDownSync)Constants.isPullDownSync=false;
            if(Constants.isLocationSync)Constants.isLocationSync=false;

            LogManager.writeLogError("NetworkChangeReceiver : Network not Available");
            Log.d("Network not Available ", "Flag No 1");
        }
    }
}
