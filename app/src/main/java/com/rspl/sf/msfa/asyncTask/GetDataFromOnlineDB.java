package com.rspl.sf.msfa.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import com.arteriatech.mutils.common.OnlineODataStoreException;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.store.OnlineManager;
import com.sap.smp.client.odata.ODataEntity;

import java.util.List;

/**
 * Created by e10526 on 12/28/2017.
 *
 */

public class GetDataFromOnlineDB extends AsyncTask<Void,Boolean,Boolean> {
    private Context mContext;
    private String mReqID ="";
    private AsyncTaskItemInterface asyncTaskInterface =null;
    private String mStrQry ="";
    private List<ODataEntity> entity = null;
    private boolean isStoreOpnd = false;
    public GetDataFromOnlineDB(Context mContext, String mReqID,
                               AsyncTaskItemInterface asyncTaskSOItemInterface, String mStrQry , boolean isStoreOpnd){
        this.mContext=mContext;
        this.mReqID =mReqID;
        this.asyncTaskInterface =asyncTaskSOItemInterface;
        this.mStrQry =mStrQry;
        this.isStoreOpnd =isStoreOpnd;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean isStoreOpened=false;
        if(!isStoreOpnd){
            try {
                Constants.IsOnlineStoreFailed = false;
                isStoreOpened = OnlineManager.openOnlineStore(mContext, false);
            } catch (com.rspl.sf.msfa.store.OnlineODataStoreException e) {
                e.printStackTrace();
            }

            if(isStoreOpened){
                isStoreOpened = getDataFromOnline(isStoreOpened);
            }else{
                return isStoreOpened;
            }

        }else{
            isStoreOpened = getDataFromOnline(isStoreOpnd);
            return isStoreOpened;
        }

        return isStoreOpened;
    }

    @Override
    protected void onPostExecute(Boolean storeStatus) {
        super.onPostExecute(storeStatus);
        if (asyncTaskInterface !=null){
            asyncTaskInterface.soItemLoaded(entity,storeStatus,mReqID);
        }
    }

    private boolean getDataFromOnline(boolean isStoreOpened){
        try {
            entity = OnlineManager.getOdataEntity(mStrQry);
        } catch (OnlineODataStoreException e) {
            e.printStackTrace();
            Constants.ErrorNo = Constants.Network_Error_Code_Offline;
            isStoreOpened=false;
        }
        return isStoreOpened;
    }
}
