package com.rspl.sf.msfa.soapproval;

import android.content.Context;
import android.os.AsyncTask;

import com.arteriatech.mutils.common.OnlineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.interfaces.AsyncTaskCallBack;
import com.rspl.sf.msfa.store.OnlineManager;
import com.rspl.sf.msfa.store.OnlineStoreListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by e10769 on 15-05-2017.
 */

public class DirecySyncAsyncTask extends AsyncTask<Void, Boolean, Boolean> {
    private Context mContext;
    private UIListener uiListener = null;
    private Hashtable dbHeadTable = null;
    private ArrayList<HashMap<String, String>> arrtable = null;
    private int type = 0;

    public DirecySyncAsyncTask(Context mContext, AsyncTaskCallBack asyncTaskCallBack, UIListener uiListener, Hashtable dbHeadTable, ArrayList<HashMap<String, String>> arrtable, int type) {
        this.mContext = mContext;
        this.uiListener = uiListener;
        this.dbHeadTable = dbHeadTable;
        this.arrtable = arrtable;
        this.type = type;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean storeOpened = false;

            storeOpened = true;
        try {
            if (storeOpened) {
                if (type == 2) {
                    if(Constants.writeDebug) {
                        LogManager.writeLogDebug("SO Approval : Approve in Progress");
                    }
                    OnlineManager.updateTasksEntity(dbHeadTable, uiListener);
                }
            }
        } catch (OnlineODataStoreException e) {
            e.printStackTrace();
            TraceLog.e(Constants.SyncOnRequestSuccess, e);
            storeOpened = false;
            Constants.ErrorNo = Constants.Network_Error_Code_Offline;

                LogManager.writeLogDebug("SO Approval : Approve Failed : "+e.getLocalizedMessage());

        }
        return storeOpened;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (!aBoolean) {
            if (uiListener != null) {
                uiListener.onRequestError(0, new Exception(mContext.getString(R.string.no_network_conn)));
            }
        }
    }
}
