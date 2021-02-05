package com.rspl.sf.msfa.store;

import android.os.Bundle;
import android.util.Log;

import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataEntitySet;
import com.sap.smp.client.odata.ODataError;
import com.sap.smp.client.odata.ODataPayload;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;
import com.sap.smp.client.odata.store.ODataRequestListener;
import com.sap.smp.client.odata.store.ODataResponseSingle;

import java.util.List;

/**
 * Created by e10769 on 20-07-2017.
 */

public class GetOnlineRequestListener implements ODataRequestListener {
    private GetOnlineODataInterface getOnlineODataInterface;
    private int operation;
    private int requestCode;
    private String resourcePath;
    private String TAG = "GetOnlineRequest";
    private Bundle bundle = new Bundle();

    public GetOnlineRequestListener(GetOnlineODataInterface getOnlineODataInterface, int operation, int requestCode, String resourcePath, Bundle bundle) {
        this.getOnlineODataInterface = getOnlineODataInterface;
        this.operation = operation;
        this.requestCode = requestCode;
        this.resourcePath = resourcePath;
        this.bundle = bundle;
    }

    @Override
    public void requestStarted(ODataRequestExecution oDataRequestExecution) {
        Log.d(TAG, "requestStarted: ");
        TraceLog.scoped(this).d("requestStarted");
        /*LogManager.writeLogError("requestStarted");*/
    }

    @Override
    public void requestCacheResponse(ODataRequestExecution oDataRequestExecution) {
        Log.d(TAG, "requestCacheResponse: ");
        TraceLog.scoped(this).d("requestCacheResponse");
       /* LogManager.writeLogError("requestCacheResponse");*/
        if (this.getOnlineODataInterface != null && this.bundle != null && this.bundle.getBoolean("readFromTechnicalCacheBundle", false)) {
            List entities = null;
            try {
                ODataEntitySet payload = (ODataEntitySet)((ODataResponseSingle)oDataRequestExecution.getResponse()).getPayload();
                entities = payload.getEntities();
            } catch (Exception var4) {
                var4.printStackTrace();
            }

            this.getOnlineODataInterface.responseSuccess(oDataRequestExecution, entities, operation, requestCode, resourcePath, bundle);
        }
    }

    @Override
    public void requestServerResponse(ODataRequestExecution oDataRequestExecution) {
        Log.d(TAG, "requestServerResponse: ");
        TraceLog.scoped(this).d("requestServerResponse");
       /* LogManager.writeLogError("requestServerResponse");*/
        List<ODataEntity> entities = null;
        try {
            ODataEntitySet payload = (ODataEntitySet) ((ODataResponseSingle) oDataRequestExecution.getResponse()).getPayload();
            entities = payload.getEntities();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ConstantsUtils.insertSyncTimes(resourcePath);
        if(getOnlineODataInterface!=null)
        getOnlineODataInterface.responseSuccess(oDataRequestExecution, entities, operation, requestCode, resourcePath, bundle);
    }

    @Override
    public void requestFailed(ODataRequestExecution request, ODataException e) {
        Log.d(TAG, "requestFailed: ");
        TraceLog.scoped(this).d("requestFailed");
        LogManager.writeLogError("requestFailed :" + e.getMessage() + " " + resourcePath);
        String errorMsg = e.getMessage();
        if (request != null && request.getResponse() != null) {
            if (request.getResponse().isBatch()) {

            } else {
                ODataPayload payload = ((ODataResponseSingle) request.getResponse()).getPayload();
                if (payload != null && payload instanceof ODataError) {
                    ODataError oError = (ODataError) payload;
                    TraceLog.d("requestFailed - status message " + oError.getMessage());
//                    LogManager.writeLogError("requestFailed");
                    LogManager.writeLogError("Error :" + oError.getMessage());
                    errorMsg = oError.getMessage();
                }
            }
        }
        if(getOnlineODataInterface!=null)
        getOnlineODataInterface.responseFailed(request, operation, requestCode, resourcePath, errorMsg, bundle);
    }

    @Override
    public void requestFinished(ODataRequestExecution oDataRequestExecution) {
        Log.d(TAG, "requestFinished: ");
        TraceLog.scoped(this).d("requestFinished");
       /* LogManager.writeLogError("requestFinished");*/
    }
}
