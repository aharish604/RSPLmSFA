package com.rspl.sf.msfa.asyncTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;

import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.interfaces.MessageWithBooleanCallBack;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by e10526 on 23-03-2018.
 */

public class SyncGeoAsyncTask extends AsyncTask<String, Boolean, Boolean> {
    private Context mContext;
    private MessageWithBooleanCallBack dialogCallBack = null;
    boolean onlineStoreOpen = false;
    private String mSyncType="";
    private String refGuid =null;
    public SyncGeoAsyncTask(Context context,String guid, MessageWithBooleanCallBack dialogCallBack, String mSyncType) {
        this.mContext = context;
        this.dialogCallBack = dialogCallBack;
        this.mSyncType = mSyncType;
        refGuid = guid;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        onlineStoreOpen = false;
        try {
                    Log.d("BeforeCallMustSell Req", UtilConstants.getSyncHistoryddmmyyyyTime());
                    try {
                        if(!OfflineManager.isOfflineStoreOpenGeo()) {
                            try {
                                OfflineManager.openOfflineStoreGeo(mContext, new UIListener() {
                                    @Override
                                    public void onRequestError(int i, Exception e) {
                                        Log.d("opOffStoreMS onReqError", UtilConstants.getSyncHistoryddmmyyyyTime());
                                        setCallBackToUI(true,"");
                                    }

                                    @Override
                                    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                                        Log.d("opOffStoreMS onReqSuc", UtilConstants.getSyncHistoryddmmyyyyTime());
                                        List<String> alString=new ArrayList<>();
                                        alString.add(Constants.SPGeos);
                                        // Todo commented on 18-01-2020 bt saikrishna
//                                        Constants.updateSyncTime(alString,mContext,Constants.Geo_sync,refGuid);
                                        if(!mContext.getSharedPreferences(Constants.PREFS_NAME,0).getBoolean("GEOFLAG",false)) {
                                            Constants.events.inserthistortTable(Constants.SYNC_TABLE, "",
                                                    Constants.Collections, Constants.SPGeos);
                                             String syncTime = Constants.getSyncHistoryddmmyyyyTime();
                                             Constants.events.updateStatus(Constants.SYNC_TABLE, Constants.SPGeos, Constants.TimeStamp, syncTime);
                                            SharedPreferences.Editor editor = mContext.getSharedPreferences(Constants.PREFS_NAME,0).edit();
                                            editor.putBoolean("GEOFLAG",true);
                                            editor.apply();
                                        }
                                        setCallBackToUI(true,"");
                                    }
                                });
                            } catch (OfflineODataStoreException e) {
                                onlineStoreOpen =true;
                                LogManager.writeLogError(Constants.error_txt + e.getMessage());
                            }

                        }else{
                        if(mSyncType.equalsIgnoreCase(Constants.Fresh) || mSyncType.equalsIgnoreCase(Constants.All)){
                            try {
                                if (UtilConstants.isNetworkAvailable(mContext)) {
                                    OfflineManager.refreshRequestsGeo(mContext, Constants.SPGeos, new UIListener() {
                                        @Override
                                        public void onRequestError(int operation, Exception exception) {
                                            ErrorBean errorBean = Constants.getErrorCodeGeo(operation, exception,mContext);
                                            try {
                                                if (!errorBean.hasNoError()) {
                                                    if (errorBean.getErrorCode() == Constants.Resource_not_found) {
                                                        UtilConstants.closeStore(mContext,
                                                                OfflineManager.optionsGeo, errorBean.getErrorMsg(),
                                                                OfflineManager.offlineGeo, Constants.PREFS_NAME,"");
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            Log.d("refReqMust onReqError", UtilConstants.getSyncHistoryddmmyyyyTime());
                                            setCallBackToUI(true,"");
                                        }

                                        @Override
                                        public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                                            Log.d("refReqMust onReqError", UtilConstants.getSyncHistoryddmmyyyyTime());
                                            setCallBackToUI(true,"");
                                        }
                                    });
                                }else{
                                    onlineStoreOpen =true;
                                }
                            } catch (OfflineODataStoreException e) {
                                onlineStoreOpen =true;
                                TraceLog.e("Sync::onRequestSuccess", e);
                            }
                        }else{
                            onlineStoreOpen =true;
                        }
                        }
                    } catch (Exception e) {
                        onlineStoreOpen =true;
                        e.printStackTrace();
                    }
                    Log.d("AfterCallMustSell Req", UtilConstants.getSyncHistoryddmmyyyyTime());
        } catch (Exception e) {
            onlineStoreOpen =true;
            e.printStackTrace();
        }
        return onlineStoreOpen;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if(aBoolean) {
            setCallBackToUI(aBoolean,Constants.makeMsgReqError(Constants.ErrorNo,mContext,false));
        }

    }

    private void setCallBackToUI(boolean status, String error_Msg){
        if (dialogCallBack!=null){
            dialogCallBack.clickedStatus(status,error_Msg,null);
        }
    }

}
