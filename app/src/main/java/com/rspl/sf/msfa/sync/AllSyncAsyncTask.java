package com.rspl.sf.msfa.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10769 on 22-04-2017.
 */

public class AllSyncAsyncTask extends AsyncTask<Void,Void,Void> {
    private UIListener uiListener;
    private ArrayList<String> allCollection;
    private Context mContext;
    private String concatCollectionStr="";
    private String refguid="";

    public AllSyncAsyncTask(Context mContext, UIListener uiListener, ArrayList<String> allCollection,String RefGuid) {
        this.uiListener = uiListener;
        this.allCollection = allCollection;
        this.mContext=mContext;
        this.refguid=RefGuid;
    }
    @Override
    protected Void doInBackground(Void... params) {
        if (!OfflineManager.isOfflineStoreOpen()) {
            try {
                OfflineManager.openOfflineStore(mContext, uiListener);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
        } else {
            Constants.isStoreClosed = false;
            try {
                if (Constants.writeDebug){
                    LogManager.writeLogDebug("All Sync is in progress");
            }
                Constants.updateStartSyncTime(mContext,Constants.Sync_All,Constants.StartSync,refguid);
                OfflineManager.refreshStoreSync(mContext, uiListener, Constants.All, "");
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
