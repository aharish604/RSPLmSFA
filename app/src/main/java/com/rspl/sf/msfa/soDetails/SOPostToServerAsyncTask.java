package com.rspl.sf.msfa.soDetails;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OnlineODataStoreException;
import com.arteriatech.mutils.log.TraceLog;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.store.OnlineManager;
import com.rspl.sf.msfa.store.OnlineStoreListener;
import com.sap.smp.client.odata.exception.ODataContractViolationException;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by e10769 on 04-09-2017.
 */

public class SOPostToServerAsyncTask extends AsyncTask<Void, Boolean, Boolean> {
    private Context mContext;
    private Hashtable dbHeadTable = null;
    private OnlineODataInterface onlineODataInterface = null;
    private ArrayList<HashMap<String, String>> arrtable = null;
    private int type = 0;
    private String isError = "";
    private Bundle bundle = null;

    public SOPostToServerAsyncTask(Context mContext, OnlineODataInterface onlineODataInterface, Hashtable dbHeadTable, ArrayList<HashMap<String, String>> arrtable, int type, Bundle bundle) {
        this.mContext = mContext;
        this.onlineODataInterface = onlineODataInterface;
        this.dbHeadTable = dbHeadTable;
        this.arrtable = arrtable;
        this.type = type;
        this.bundle = bundle;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean storeOpened = false;
        boolean isSessionReceived=false;
        try {

                storeOpened = true;
            String sessionId = "";
            boolean isSessionRequired = bundle.getBoolean(Constants.BUNDLE_SESSION_REQUIRED, false);
            if (isSessionRequired) {
                sessionId = ConstantsUtils.getSessionId(mContext);
                if (!TextUtils.isEmpty(sessionId)){
                    dbHeadTable.put(Constants.LoginID, sessionId);
                    isSessionReceived=true;
                }else {
                    isSessionReceived=false;
                }
            }else {
                isSessionReceived=true;
            }

            if (storeOpened && isSessionReceived) {

                if (type == 1) {
//                    OnlineManager.createSOsEntity(dbHeadTable, arrtable, onlineODataInterface, bundle);
                }/* else if (type == 2) {
                    OnlineManager.updateTasksEntity(dbHeadTable, uiListener, sessionId);
                } */ else if (type == 3) {
                    OnlineManager.updateSO(dbHeadTable, arrtable, onlineODataInterface,bundle);
                }/*else if (type == 4) {
                    dbHeadTable.put(Constants.TextID, SOUtils.getSOTextId(Constants.HDRNTTXTID));
                    OnlineManager.createSOText(dbHeadTable, onlineODataInterface, bundle);
                }*/ else if (type == 5) {
//                    OnlineManager.cancelSO(dbHeadTable, arrtable, onlineODataInterface,bundle);
                }
            }else {
                if (!isSessionReceived){
                    TraceLog.e(Constants.error_txt+" Session Id empty");
                    isError = mContext.getString(R.string.session_empty_value_error);
                }
            }
        }/* catch (com.arteriatech.sf.store.OnlineODataStoreException e) {
            e.printStackTrace();
            TraceLog.e(Constants.SyncOnRequestSuccess, e);
            isError = e.getMessage();
        }*/ catch (JSONException e) {
            e.printStackTrace();
            TraceLog.e(Constants.error_txt+e.getMessage());
            isError = mContext.getString(R.string.session_empty_value_error);
        } catch (IOException e) {
            e.printStackTrace();
            TraceLog.e(Constants.error_txt+e.getMessage());
            isError = mContext.getString(R.string.session_empty_value_error);
        } catch (Throwable e) {
            e.printStackTrace();
            TraceLog.e(Constants.error_txt+e.getMessage());
            isError = e.getMessage();
        }
        return storeOpened;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (!aBoolean) {
            if (onlineODataInterface != null) {
                onlineODataInterface.responseFailed(null, Constants.makeMsgReqError(Constants.ErrorNo, mContext, false), bundle);
            }
        } else {
            if (!TextUtils.isEmpty(isError)) {
                if (onlineODataInterface != null) {
                    onlineODataInterface.responseFailed(null, isError, bundle);
                }
            }
        }
    }
}