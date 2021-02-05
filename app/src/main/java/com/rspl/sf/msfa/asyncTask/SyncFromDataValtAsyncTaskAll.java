package com.rspl.sf.msfa.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.MessageWithBooleanCallBack;
import com.rspl.sf.msfa.store.OnlineManager;
import com.rspl.sf.msfa.store.OnlineODataStoreException;
import com.rspl.sf.msfa.store.OnlineStoreListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by e10769 on 04-03-2017.
 *
 */

public class SyncFromDataValtAsyncTaskAll extends AsyncTask<String, Boolean, Boolean> {
    private Context mContext;
    private UIListener uiListener;
    private Hashtable dbHeadTable;
    private ArrayList<HashMap<String, String>> arrtable;
    private String[] invKeyValues = null;
    private MessageWithBooleanCallBack dialogCallBack = null;

    public SyncFromDataValtAsyncTaskAll(Context context, String[] invKeyValues, UIListener uiListener, MessageWithBooleanCallBack dialogCallBack) {
        this.mContext = context;
        this.uiListener = uiListener;
        this.invKeyValues = invKeyValues;
        this.dialogCallBack = dialogCallBack;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        boolean onlineStoreOpen = false;
        try {
            Thread.sleep(1000);
            Constants.IsOnlineStoreFailed = false;
            Constants.AL_ERROR_MSG.clear();

            Constants.ErrorCode = 0;
            Constants.ErrorNo = 0;
            Constants.ErrorName = "";

            onlineStoreOpen = OnlineManager.openOnlineStore(mContext, true);

            if(onlineStoreOpen){
                if (invKeyValues != null) {
                    for (int k = 0; k < invKeyValues.length; k++) {

                        while (!Constants.mBoolIsReqResAval) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        if(Constants.mBoolIsNetWorkNotAval){
                            break;
                        }
                        Constants.mBoolIsReqResAval= false;


                        String store = null;
                        try {
                            store = ConstantsUtils.getFromDataVault(invKeyValues[k].toString(),mContext);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                        //Fetch object from data vault
                        try {

                            JSONObject fetchJsonHeaderObject = new JSONObject(store);
                            dbHeadTable = new Hashtable();
                            arrtable = new ArrayList<>();
                            if (fetchJsonHeaderObject.getString(Constants.EntityType).equalsIgnoreCase(Constants.Collection)) {
                                dbHeadTable = Constants.getCollHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                String itemsString = fetchJsonHeaderObject.getString(Constants.ItemsText);

                                arrtable = UtilConstants.convertToArrayListMap(itemsString);

                                try {
                                    OnlineManager.createCollectionEntry(dbHeadTable, arrtable, uiListener);

                                } catch (OnlineODataStoreException e) {
                                    e.printStackTrace();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    onlineStoreOpen = true;
                }
            }else{
                return onlineStoreOpen;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return onlineStoreOpen;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if(!aBoolean) {
//            if (dialogCallBack != null) {
//                dialogCallBack.clickedStatus(aBoolean,"");
//            }

            setCallBackToUI(aBoolean,Constants.makeMsgReqError(Constants.ErrorNo,mContext,false));
        }

    }




    private void setCallBackToUI(boolean status,String error_Msg){
        if (dialogCallBack!=null){
            dialogCallBack.clickedStatus(status,error_Msg,null);
        }
    }

//    private void closingproDialog(){
//        try {
//            syncProgDialog.dismiss();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
