package com.rspl.sf.msfa.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.store.OnlineManager;
import com.rspl.sf.msfa.store.OnlineODataStoreException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by e10769 on 22-04-2017.
 */

public class PostDataFromDataValt extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private UIListener uiListener;
    private String[][] invKeyValues;
    private Hashtable dbHeadTable;
    private ArrayList<HashMap<String, String>> arrtable;
    private static String TAG = "UpdatePendingRequest";

    public PostDataFromDataValt(Context mContext, UIListener uiListener, String[][] invKeyValues) {
        this.mContext = mContext;
        this.uiListener = uiListener;
        this.invKeyValues = invKeyValues;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Thread.sleep(1000);

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
                        store = ConstantsUtils.getFromDataVault(invKeyValues[k][0].toString(),mContext);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    //Fetch object from data vault
                    try {

                        JSONObject fetchJsonHeaderObject = new JSONObject(store);
                        dbHeadTable = new Hashtable();
                        arrtable = new ArrayList<>();

                        if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.Collection)) {
                            dbHeadTable = Constants.getCollHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                            String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);

                            arrtable = UtilConstants.convertToArrayListMap(itemsString);

                            try {
                                OnlineManager.createCollectionEntry(dbHeadTable, arrtable, uiListener);

                            } catch (OnlineODataStoreException e) {
                                e.printStackTrace();
                            }

                        } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SecondarySOCreate)) {
                            Constants.REPEATABLE_REQUEST_ID="";
                            JSONObject dbHeadTable = Constants.getSOHeaderValuesFrmJsonObject(fetchJsonHeaderObject);
                            OnlineManager.createEntity( Constants.REPEATABLE_REQUEST_ID,Constants.REPEATABLE_DATE,dbHeadTable.toString(), Constants.SOs, uiListener, mContext);
                        }else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SalesOrderDataValt)) {
                            Constants.REPEATABLE_REQUEST_ID="";
                            JSONObject dbHeadTable = Constants.getSOsHeaderValueFrmJsonObject1(fetchJsonHeaderObject);
                            OnlineManager.createEntity(Constants.REPEATABLE_REQUEST_ID,Constants.REPEATABLE_DATE,dbHeadTable.toString(), Constants.SOs, uiListener, mContext);
                        } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SOUpdate)) {
                            dbHeadTable = Constants.getSOCancelHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                            String itemsString = fetchJsonHeaderObject.getString(Constants.SalesOrderItems);
                            arrtable = UtilConstants.convertToArrayListMap(itemsString);
                            try {
                                OnlineManager.cancelSO(dbHeadTable, arrtable, uiListener);
                            } catch (OnlineODataStoreException e) {
                                e.printStackTrace();
                            }
                        } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.Expenses)) {
                            dbHeadTable = Constants.getExpenseHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                            String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                            arrtable = UtilConstants.convertToArrayListMap(itemsString);
                            try {
                                OnlineManager.createDailyExpense(dbHeadTable, arrtable, uiListener);
                            } catch (OnlineODataStoreException e) {
                                e.printStackTrace();
                            }
                        }/* else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.RouteSchedules)) {
                            isBatchReqs = false;
                            dbHeadTable = Constants.getMTPHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                            String itemsString = fetchJsonHeaderObject.getString(Constants.RouteSchedulePlans);
                            arrtable = UtilConstants.convertToArrayListMap(itemsString);
                            try {
                                OnlineManager.createMTP(dbHeadTable, arrtable, SyncSelectionActivity.this, SyncSelectionActivity.this);
                            } catch (OnlineODataStoreException e) {
                                e.printStackTrace();
                            }
                        } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.CollectionPlan)) {
                            isBatchReqs = false;
                            dbHeadTable = Constants.getRTGSHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                            String itemsString = fetchJsonHeaderObject.getString(Constants.CollectionPlanItem);
                            arrtable = UtilConstants.convertToArrayListMap(itemsString);
                            try {
                                OnlineManager.createRTGS(dbHeadTable, arrtable, SyncSelectionActivity.this, SyncSelectionActivity.this);
                            } catch (OnlineODataStoreException e) {
                                e.printStackTrace();
                            }
                        }*/

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Constants.iSAutoSync = false;
                    }

                }

        } catch (InterruptedException e) {
            e.printStackTrace();
            Constants.iSAutoSync = false;
        }
        return null;
    }
}
