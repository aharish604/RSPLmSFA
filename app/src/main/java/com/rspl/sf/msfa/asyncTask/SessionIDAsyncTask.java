package com.rspl.sf.msfa.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import com.arteriatech.mutils.interfaces.AsyncTaskCallBackInterface;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.ConstantsUtils;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by e10769 on 08-09-2017.
 */

public class SessionIDAsyncTask extends AsyncTask<Void, String, String> {
    boolean isStatus = false;
    private Context mContext;
    private AsyncTaskCallBackInterface asyncTaskCallBack;

    public SessionIDAsyncTask(Context mContext, AsyncTaskCallBackInterface asyncTaskCallBack) {
        this.mContext = mContext;
        this.asyncTaskCallBack = asyncTaskCallBack;

    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            String sessionId = ConstantsUtils.getSessionId(mContext);
            isStatus = true;
            return sessionId;
        } catch (IOException e) {
            e.printStackTrace();
            LogManager.writeLogError("Session Error : " + e.getMessage());
            return mContext.getString(R.string.data_conn_lost_during_sync_error_code,"500");
        } catch (JSONException e) {
            LogManager.writeLogError("Session Error : " + e.getMessage());
            e.printStackTrace();
            return mContext.getString(R.string.data_conn_lost_during_sync_error_code,"500");
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (asyncTaskCallBack != null) {
            asyncTaskCallBack.asyncResponse(isStatus, null, s);
        }
    }

}
