package com.rspl.sf.msfa.asyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.DialogCallBack;
import com.rspl.sf.msfa.store.OnlineManager;
import com.rspl.sf.msfa.store.OnlineODataStoreException;
import com.rspl.sf.msfa.sync.SyncSelectionActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by e10769 on 04-03-2017.
 */

public class SyncFromDataValtAsyncTask extends AsyncTask<String, Boolean, Boolean> {
    private Context mContext;
    private UIListener uiListener;
    private Hashtable dbHeadTable;
    private ArrayList<HashMap<String, String>> arrtable;
    private String[] invKeyValues = null;
    private DialogCallBack dialogCallBack = null;
    private OnlineODataInterface onlineODataInterface;

    public SyncFromDataValtAsyncTask(Context context, String[] invKeyValues, UIListener uiListener, DialogCallBack dialogCallBack) {
        this.mContext = context;
        this.uiListener = uiListener;
        this.invKeyValues = invKeyValues;
        this.dialogCallBack = dialogCallBack;
    }
    public SyncFromDataValtAsyncTask(Context context, String[] invKeyValues,UIListener uiListener,OnlineODataInterface onlineODataInterface, DialogCallBack dialogCallBack) {
        this.mContext = context;
        this.invKeyValues = invKeyValues;
        this.uiListener = uiListener;
        this.dialogCallBack = dialogCallBack;
        this.onlineODataInterface = onlineODataInterface;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        boolean offlineStoreOpen = false;
        try {
            offlineStoreOpen = OnlineManager.openOnlineStore(mContext, true);

            if (invKeyValues != null) {
                for (int k = 0; k < invKeyValues.length; k++) {
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
                        if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SecondarySOCreate)) {
                            Constants.REPEATABLE_REQUEST_ID = "";
                            Constants.REPEATABLE_DATE = "";
                            JSONObject dbHeadTable = Constants.getSOHeaderValuesFrmJsonObject(fetchJsonHeaderObject);
                            OnlineManager.createEntity(Constants.REPEATABLE_REQUEST_ID,Constants.REPEATABLE_DATE, dbHeadTable.toString(), Constants.SOs, uiListener, mContext);
                        } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SalesOrderDataValt)) {
                            Constants.REPEATABLE_REQUEST_ID = "";
                            Constants.REPEATABLE_DATE = "";
                            JSONObject dbHeadTable = Constants.getSOsHeaderValueFrmJsonObject1(fetchJsonHeaderObject);
                            OnlineManager.createEntity(Constants.REPEATABLE_REQUEST_ID,Constants.REPEATABLE_DATE, dbHeadTable.toString(), Constants.SOs, uiListener, mContext);
                        } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.RouteSchedules)) {
                            Constants.REPEATABLE_REQUEST_ID = "";
                            Constants.REPEATABLE_DATE = "";
                            // preparing entity pending
                            JSONObject dbHeadTable = Constants.getMTPHeaderValuesFrmJsonObject(fetchJsonHeaderObject);
                            if (TextUtils.isEmpty(String.valueOf(fetchJsonHeaderObject.get(Constants.IS_UPDATE)))) {
                                OnlineManager.createEntity(Constants.REPEATABLE_REQUEST_ID,Constants.REPEATABLE_DATE, dbHeadTable.toString(), Constants.RouteSchedules, uiListener, mContext);
                            } else {
                                try {
                                    OnlineManager.batchUpdateMTP(fetchJsonHeaderObject, mContext, uiListener);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.CollectionPlan)) {
                            // preparing entity pending
                            Constants.REPEATABLE_REQUEST_ID="";
                            Constants.REPEATABLE_DATE="";
                            if (TextUtils.isEmpty(fetchJsonHeaderObject.getString(Constants.IS_UPDATE))) {
                                JSONObject dbHeadTable = Constants.getRTGSHeaderValuesFrmJsonObject(fetchJsonHeaderObject);
                                OnlineManager.createEntity(Constants.REPEATABLE_REQUEST_ID,Constants.REPEATABLE_DATE, dbHeadTable.toString(), Constants.CollectionPlan, uiListener, mContext);
                            }else {
                                try {
                                    OnlineManager.batchUpdateRTGS(fetchJsonHeaderObject,mContext,uiListener);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        ConstantsUtils.printErrorLog(e.getMessage());
                    }
                }
                offlineStoreOpen = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog(e.getMessage());
        }
        return offlineStoreOpen;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (dialogCallBack != null) {
            dialogCallBack.clickedStatus(aBoolean);
        }

    }
}
