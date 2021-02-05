package com.rspl.sf.msfa.store;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.OnlineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.so.SOEditQuantityActivity;
import com.rspl.sf.msfa.so.SOQuantityActivity;
import com.rspl.sf.msfa.socreate.SOConditionItemDetaiBean;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.sap.smp.client.httpc.events.IReceiveEvent;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataEntitySet;
import com.sap.smp.client.odata.ODataError;
import com.sap.smp.client.odata.ODataNavigationProperty;
import com.sap.smp.client.odata.ODataPayload;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;
import com.sap.smp.client.odata.store.ODataRequestListener;
import com.sap.smp.client.odata.store.ODataRequestParamSingle;
import com.sap.smp.client.odata.store.ODataResponse;
import com.sap.smp.client.odata.store.ODataResponseBatch;
import com.sap.smp.client.odata.store.ODataResponseBatchItem;
import com.sap.smp.client.odata.store.ODataResponseSingle;
import com.sap.smp.client.odata.store.impl.ODataResponseChangeSetDefaultImpl;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by e10769 on 02-05-2017.
 */

public class OnlineRequestListener implements ODataRequestListener {

    private static String TAG = "OnlineRequestListener";
    private final int SUCCESS = 0;
    private final int ERROR = -1;
    private UIListener uiListener;
    private String autoSync;
    private int operation;
    private Handler uiHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == SUCCESS) {
                // Notify the Activity the is complete
                String key = (String) msg.obj;
                TraceLog.d("requestsuccess - status message key" + key);
                try {
//                    if (autoSync != null && autoSync.equalsIgnoreCase("AutoSync")) {
//                        UpdatePendingRequests.getInstance().onRequestSuccess(operation, key);
//                    } else {
                    uiListener.onRequestSuccess(operation, key);
//                    }

                } catch (ODataException e) {
                    e.printStackTrace();
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            } else if (msg.what == ERROR) {
                Exception e = (Exception) msg.obj;
//                if (autoSync != null && autoSync.equalsIgnoreCase("AutoSync")) {
//                    UpdatePendingRequests.getInstance().onRequestError(operation, e);
//                } else {
                uiListener.onRequestError(operation, e);
//                }

            }
        }
    };

    public OnlineRequestListener(int operation, UIListener uiListener) {
        super();
        this.operation = operation;
        this.uiListener = uiListener;
    }

    public OnlineRequestListener(int operation, String autoSync) {
        super();
        this.operation = operation;
        this.autoSync = autoSync;
    }

    /*****************
     * Methods that implements ODataRequestListener interface
     *****************/

    @Override
    public void requestCacheResponse(ODataRequestExecution request) {
        TraceLog.scoped(this).d("requestCacheResponse");

        ODataProperty property;
        ODataPropMap properties;
        //Verify request’s response is not null. Request is always not null
        if (request.getResponse() != null) {


            if (request.getResponse().isBatch()) {

            } else {
//Parse the response
                ODataResponseSingle response = (ODataResponseSingle) request.getResponse();
                if (response != null) {
                    //Get the response payload
                    ODataEntitySet feed = (ODataEntitySet) response.getPayload();
                    if (feed != null) {
                        //Get the list of ODataEntity
                        List<ODataEntity> entities = feed.getEntities();
                        //Loop to retrieve the information from the response
                        for (ODataEntity entity : entities) {
                            //Obtain the properties you want to display in the screen
                            properties = entity.getProperties();
                            property = properties.get("");
                        }
                        //TODO - Send content to the screen
                    }
                }
            }

        }

    }


    @Override
    public void requestFailed(ODataRequestExecution request, ODataException e) {
        try {
            TraceLog.scoped(this).d("requestFailed");
            if (request != null && request.getResponse() != null) {
                if (request.getResponse().isBatch()) {

                } else {
                    ODataPayload payload = ((ODataResponseSingle) request.getResponse()).getPayload();
                    if (payload != null && payload instanceof ODataError) {
                        ODataError oError = (ODataError) payload;
                        TraceLog.d("requestFailed - status message " + oError.getMessage());
                        ConstantsUtils.APPROVALERRORMSG=oError.getMessage();
                        LogManager.writeLogError("Error :" + oError.getMessage());
                        notifyErrorToListener(new Exception(oError.getMessage()));
                        return;
                    }
                }
            }
            notifyErrorToListener(e);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void requestFinished(ODataRequestExecution request) {
        TraceLog.scoped(this).d("requestFinished");
    }

    @Override
    public void requestServerResponse(ODataRequestExecution request) {
        try {
            TraceLog.scoped(this).d("requestServerResponse");
            if (request != null && request.getResponse() != null) {
                if (request.getResponse().isBatch()) {
                    try {
                        ODataResponse oDataResponse = request.getResponse();
                        if (oDataResponse != null) {

                            ODataResponseBatch batchResponse = (ODataResponseBatch) oDataResponse;

                            List<ODataResponseBatchItem> responses = batchResponse.getResponses();

                            for (ODataResponseBatchItem response : responses) {

                                if (response instanceof ODataResponseChangeSetDefaultImpl) {

                                    ODataResponseChangeSetDefaultImpl changesetResponse = (ODataResponseChangeSetDefaultImpl) response;

                                    List<ODataResponseSingle> singles = changesetResponse.getResponses();

                                    for (ODataResponseSingle singleResponse : singles) {
                                        // Get individual response

                                        ODataPayload payload = singleResponse.getPayload();
                                        if (payload != null) {

                                            if (payload instanceof ODataError) {

                                                ODataError oError = (ODataError) payload;

                                                notifyErrorToListener(new OnlineODataStoreException(oError.getMessage()));
                                                break;
                                            } else {
                                                TraceLog.d("requestsuccess - status message before success");
                                                notifySuccessToListener(null);
                                            }
                                        } else {
                                            TraceLog.d("requestsuccess - status message before success");
                                            notifySuccessToListener(null);
                                        }
                                    }
                                }

                            }


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    ODataResponseSingle response = (ODataResponseSingle) request.getResponse();
                    Map<ODataResponse.Headers, String> headerMap = response.getHeaders();
                    String code = headerMap.get(ODataResponse.Headers.Code);
                    TraceLog.d("requestServerResponse - status code " + code);
                    String eTag = headerMap.get(ODataResponse.Headers.ETag);
                    if (!TextUtils.isEmpty(eTag)) {
                        notifySuccessToListener(eTag);
                        return;
                    } else {
                        ODataPayload payload = ((ODataResponseSingle) request.getResponse()).getPayload();
                        if (payload != null && payload instanceof ODataEntity) {
                            ODataEntity oEntity = (ODataEntity) payload;
                            ODataPropMap properties = oEntity.getProperties();
                            ODataProperty property = null;
                            if (oEntity.getEntityType().equalsIgnoreCase(UtilConstants.getNameSpace(OfflineManager.offlineStore) + Constants.SOS_ENTITY)) {

                                ODataNavigationProperty navProp = oEntity.getNavigationProperty(Constants.SOItemDetails);
                                properties = oEntity.getProperties();

//                                property = properties.get(Constants.Testrun);
                                String testRun = "";

                                ODataPayload reqPayload = ((ODataRequestParamSingle) request.getRequest()).getPayload();
                                ODataEntity oEntityReq = (ODataEntity) reqPayload;
                                ODataPropMap propertiesReq = oEntityReq.getProperties();
                                property = propertiesReq.get(Constants.Testrun);
                                if (property != null) {
                                    testRun = property.getValue().toString();
                                }
                                if (testRun.equalsIgnoreCase("")) {
                                    property = properties.get(Constants.SONo);
                                    Constants.SO_ORDER_VALUE = property.getValue().toString();
                                    String popUpText = "Sales order # " + Constants.SO_ORDER_VALUE + " created";
                                    Log.d(TAG, "requestServerResponse: "+popUpText);
                                    LogManager.writeLogInfo(popUpText);
                                } else {
                                    property = properties.get(Constants.TotalAmount);

                                    if (property != null) {
                                        String totalAmt = property.getValue().toString();
                                        if (SOQuantityActivity.headerDetail != null) {
                                            SOQuantityActivity.headerDetail.put("TotalAmount", totalAmt);

                                            property = properties.get(Constants.Currency);
                                            SOQuantityActivity.headerDetail.put("Currency", property.getValue().toString());

                                        }
                                        if (SOEditQuantityActivity.headerDetail != null) {
                                            String totalAmts = property.getValue().toString();
                                            SOEditQuantityActivity.headerDetail.put("TotalAmount", totalAmts);

                                            property = properties.get(Constants.Currency);
                                            SOEditQuantityActivity.headerDetail.put("Currency", property.getValue().toString());

                                        }

                                    }
                                    property = properties.get(Constants.NetPrice);
                                    if (property != null) {
                                        String totalAmt = property.getValue().toString();
                                        if (SOQuantityActivity.headerDetail != null) {
                                            SOQuantityActivity.headerDetail.put("NetPrice", totalAmt);

                                        }
                                        if (SOEditQuantityActivity.headerDetail != null) {
                                            SOEditQuantityActivity.headerDetail.put("NetPrice", totalAmt);

                                        }
                                    }

                                    property = properties.get(Constants.TaxAmount);
                                    if (property != null) {
                                        String totalAmt = property.getValue().toString();
                                        if (SOQuantityActivity.headerDetail != null) {
                                            SOQuantityActivity.headerDetail.put("TaxAmount", totalAmt);
                                        }if (SOEditQuantityActivity.headerDetail != null) {
                                            SOEditQuantityActivity.headerDetail.put("TaxAmount", totalAmt);
                                        }
                                    }

                                    property = properties.get(Constants.Freight);
                                    if (property != null) {
                                        String totalAmt = property.getValue().toString();
                                        if (SOQuantityActivity.headerDetail != null) {
                                            SOQuantityActivity.headerDetail.put("Freight", totalAmt);
                                        }if (SOEditQuantityActivity.headerDetail != null) {
                                            SOEditQuantityActivity.headerDetail.put("Freight", totalAmt);
                                        }
                                    }

                                    property = properties.get(Constants.Discount);
                                    if (property != null) {
                                        String totalAmt = property.getValue().toString();
                                        if (SOQuantityActivity.headerDetail != null) {
                                            SOQuantityActivity.headerDetail.put("Discount", totalAmt);
                                        } if (SOEditQuantityActivity.headerDetail != null) {
                                            SOEditQuantityActivity.headerDetail.put("Discount", totalAmt);
                                        }
                                    }

                                    try {


                                        if (navProp.getNavigationType().toString().equalsIgnoreCase("EntitySet")) {
                                            try {
                                                ODataEntitySet feed = (ODataEntitySet) navProp.getNavigationContent();
                                                List<ODataEntity> entities = feed.getEntities();
//                                                SOItemBean soItemBeen = SOQuantityActivity.finalSOItemBean.get(0);
                                                int i = 0;

                                                for (ODataEntity entity : entities) {
                                                    SOItemBean soItemBeen = new SOItemBean();
                                                    if(i<SOQuantityActivity.finalSOItemBean.size()){
                                                        soItemBeen = SOQuantityActivity.finalSOItemBean.get(i);
                                                        i++;
                                                    }
                                                    if(i<SOEditQuantityActivity.finalSOItemBean.size()){
                                                        soItemBeen = SOEditQuantityActivity.finalSOItemBean.get(i);
                                                        i++;
                                                    }
                                                    properties = entity.getProperties();
                                                    property = properties.get(Constants.Currency);
                                                    if (property != null) {
                                                        soItemBeen.setCurrency(property.getValue().toString());
                                                    }

                                                    property = properties.get(Constants.UOM);
                                                    if (property != null) {
                                                        soItemBeen.setUom(property.getValue().toString());
                                                    }

                                                    property = properties.get(Constants.UnitPrice);
                                                    if (property != null) {
                                                        soItemBeen.setUnitPrice(property.getValue().toString());
                                                    }
                                                    property = properties.get(Constants.NetAmount);
                                                    if (property != null) {
                                                        soItemBeen.setNetAmount(property.getValue().toString());
                                                    }
                                                    property = properties.get(Constants.Freight);
                                                    if (property != null) {
                                                        soItemBeen.setFreight(property.getValue().toString());
                                                    }
                                                    property = properties.get(Constants.Tax);
                                                    if (property != null) {
                                                        soItemBeen.setTaxAmount(property.getValue().toString());
                                                    }
                                                    property = properties.get(Constants.GrossAmount);
                                                    if (property != null) {
                                                        soItemBeen.setTotalAmount(property.getValue().toString());
                                                    }

                                                    ODataNavigationProperty navProp2 = entity.getNavigationProperty(Constants.SOConditionItemDetails);
                                                    ArrayList<SOConditionItemDetaiBean> soConditionItemDetaiBeenList = soItemBeen.getConditionItemDetaiBeanArrayList();
                                                    if (navProp2.getNavigationType().toString().equalsIgnoreCase("EntitySet")) {
                                                        try {
                                                            ODataEntitySet feedCondition = (ODataEntitySet) navProp2.getNavigationContent();
                                                            List<ODataEntity> entitiesCondition = feedCondition.getEntities();
                                                            SOConditionItemDetaiBean soConditionItemDetaiBean=null;
                                                            BigDecimal totalNormalAmt = new BigDecimal("0.0");
                                                            BigDecimal subTotalAmt = new BigDecimal("0.0");
                                                            String currency = "";
                                                            for (ODataEntity entityCondition : entitiesCondition) {
                                                                soConditionItemDetaiBean = OfflineManager.getConditionItemDetails(entityCondition, soConditionItemDetaiBeenList);
                                                                if(soConditionItemDetaiBean!=null) {
                                                                    totalNormalAmt = totalNormalAmt.add(new BigDecimal(soConditionItemDetaiBean.getAmount()));
                                                                    subTotalAmt = subTotalAmt.add(new BigDecimal(soConditionItemDetaiBean.getConditionValue()));
                                                                    currency = soConditionItemDetaiBean.getCurrency();
                                                                    soConditionItemDetaiBeenList.add(soConditionItemDetaiBean);
                                                                }
                                                            }
                                                            soConditionItemDetaiBean = new SOConditionItemDetaiBean();
                                                            soConditionItemDetaiBean.setViewType("T");
                                                            soConditionItemDetaiBean.setName("Total");
                                                            soConditionItemDetaiBean.setAmount(totalNormalAmt + " " + currency);
                                                            soConditionItemDetaiBean.setConditionValue(subTotalAmt + " " + currency);
                                                            soConditionItemDetaiBeenList.add(soConditionItemDetaiBean);

                                                            soItemBeen.setConditionItemDetaiBeanArrayList(soConditionItemDetaiBeenList);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (oEntity.getEntityType().endsWith(Constants.CollectionEntity)) {
                                properties = oEntity.getProperties();
                                property = properties.get(Constants.DocumentNo);
                                Constants.FIPDocumentNumber = property.getValue().toString();
                                String popUpText = "Collection # " + Constants.FIPDocumentNumber + " created";

                                LogManager.writeLogInfo(popUpText);

                            } /*else if (oEntity.getEntityType().equalsIgnoreCase(Constants.FeedbackEntity)) {
                                property = properties.get(Constants.FeebackGUID);

                                String popUpText = "Feedback created";

                                LogManager.writeLogInfo(popUpText);
                            }*/
                            if (property != null)
                                notifySuccessToListener(property.getValue().toString() != null ? property.getValue().toString() : "");
                            else
                                notifySuccessToListener("");
                            return;
                        }
                    }


                    TraceLog.d("requestsuccess - status message before success");
                    notifySuccessToListener(null);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void requestStarted(ODataRequestExecution request) {
        TraceLog.scoped(this).d("requestStarted");
    }


    /*****************
     * Utils Methods
     *****************/


    /**
     * Notify the OnlineUIListener that the request was successful.
     */
    protected void notifySuccessToListener(String key) {
        Message msg = uiHandler.obtainMessage();
        msg.what = SUCCESS;
        msg.obj = key;
        uiHandler.sendMessage(msg);
    }

    /**
     * Notify the OnlineUIListener that the request has an error.
     *
     * @param exception an Exception that denotes the error that occurred.
     */
    protected void notifyErrorToListener(Exception exception) {
        Message msg = uiHandler.obtainMessage();
        msg.what = ERROR;
        msg.obj = exception;
        uiHandler.sendMessage(msg);
        TraceLog.e("OnlineRequestListener::notifyError", exception);
    }

    protected void notifyErrorToListener(IReceiveEvent var1) {
        Exception exception=null;
        try {
            try {
                String[] errorArr = String.valueOf(var1.getResponseURL()).split("/");
                Constants.Entity_Set.add(errorArr[errorArr.length-1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String responseBody = IReceiveEvent.Util.getResponseBody(var1.getReader());
            Log.d("OnlineReqListener", "notifyErrorToListener: "+responseBody+ " Error code :"+var1.getResponseStatusCode());
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(responseBody);
                JSONObject errorObject = jsonObject.getJSONObject("error");
                JSONObject erMesgObject = errorObject.getJSONObject("message");
                String errorMsg= erMesgObject.optString("value");
                Constants.AL_ERROR_MSG.add(errorMsg);
                exception= new OnlineODataStoreException(errorMsg);
            } catch (JSONException e) {
                e.printStackTrace();
                if (!TextUtils.isEmpty(responseBody)){
                    exception= new OnlineODataStoreException(responseBody);
                }else {
                    exception=e;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            exception=e;
        }
        Message msg = uiHandler.obtainMessage();
        msg.what = ERROR;
        msg.obj = exception;
        uiHandler.sendMessage(msg);
        TraceLog.e(Constants.OnlineRequestListenerNotifyError, exception);
    }

    protected void notifyErrorToListenerMsg(String responseBody) {
        Exception exception=null;
        try {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(responseBody);
                JSONObject errorObject = jsonObject.getJSONObject("error");
                JSONObject erMesgObject = errorObject.getJSONObject("message");
                String errorMsg= erMesgObject.optString("value");
                Constants.AL_ERROR_MSG.add(errorMsg);
                exception= new OnlineODataStoreException(errorMsg);
            } catch (JSONException e) {
                e.printStackTrace();
                if (!TextUtils.isEmpty(responseBody)){
                    exception= new OnlineODataStoreException(responseBody);
                }else {
                    exception=e;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            exception=e;
        }
        Message msg = uiHandler.obtainMessage();
        msg.what = ERROR;
        msg.obj = exception;
        uiHandler.sendMessage(msg);
        TraceLog.e(Constants.OnlineRequestListenerNotifyError, exception);
    }

    protected void notifyErrorToListener(String errorMsg) {
        Exception exception=null;
        try {

            try {

                exception= new OnlineODataStoreException(errorMsg);
            } catch (Exception e) {
                e.printStackTrace();

                exception=e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            exception=e;
        }
        Message msg = uiHandler.obtainMessage();
        msg.what = ERROR;
        msg.obj = exception;
        uiHandler.sendMessage(msg);
        TraceLog.e(Constants.OnlineRequestListenerNotifyError, exception);
    }
}
