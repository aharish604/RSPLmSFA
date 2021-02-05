package com.rspl.sf.msfa.soapproval;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.interfaces.AsyncTaskCallBack;
import com.rspl.sf.msfa.store.OnlineManager;
import com.rspl.sf.msfa.store.OnlineODataStoreException;
import com.rspl.sf.msfa.store.OnlineStoreListener;

/**
 * Created by e10860 on 11/13/2017.
 */

public class OpenOnlineManagerStore extends AsyncTask<String, Boolean, Boolean> {
    Context mContext;
    boolean isOnlineStoreOpened = false;
    private AsyncTaskCallBack asyncTaskCallBack;
    private String errorMessage = "";

    public OpenOnlineManagerStore(Context mContext, AsyncTaskCallBack asyncTaskCallBack) {
        this.mContext = mContext;
        this.asyncTaskCallBack = asyncTaskCallBack;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        isOnlineStoreOpened = true;

        return isOnlineStoreOpened;
    }

    @Override
    protected void onPostExecute(Boolean s) {
        super.onPostExecute(s);
        if (asyncTaskCallBack != null) {
            if (!isOnlineStoreOpened && TextUtils.isEmpty(errorMessage))
                try {
                    if(Constants.Error_Msg.equalsIgnoreCase("")){
                        errorMessage = mContext.getString(R.string.alert_sync_cannot_be_performed);
                    }else{
                        if(Constants.Error_Msg.contains("401")){
                            errorMessage = Constants.PasswordExpiredMsg;
                        }else{
                            errorMessage = Constants.Error_Msg;
                        }

                    }
                } catch (Exception e) {
                    errorMessage = mContext.getString(R.string.alert_sync_cannot_be_performed);
                    e.printStackTrace();
                }

            asyncTaskCallBack.onStatus(isOnlineStoreOpened, errorMessage);
        }
    }
}
