package com.rspl.sf.msfa.store;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.common.UtilOfflineManager;
import com.arteriatech.mutils.interfaces.AsyncTaskCallBackInterface;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.registration.UtilRegistrationActivity;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.asyncTask.GetSessionIdAsyncTask;
import com.rspl.sf.msfa.asyncTask.SessionIDAsyncTask;
import com.rspl.sf.msfa.attendance.attendancesummary.AttendanceSummaryBean;
import com.rspl.sf.msfa.claimreports.ClaimReportBean;
import com.rspl.sf.msfa.collectionPlan.WeekDetailsList;
import com.rspl.sf.msfa.collectionPlan.WeekHeaderList;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.MyUtils;
import com.rspl.sf.msfa.dealerstock.DealerStockBean;
import com.rspl.sf.msfa.grreport.GRReportBean;
import com.rspl.sf.msfa.interfaces.AsyncTaskCallBack;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.mbo.SalesOrderBean;
import com.rspl.sf.msfa.mbo.UserLoginBean;
import com.rspl.sf.msfa.mtp.MTPHeaderBean;
import com.rspl.sf.msfa.mtp.MTPRoutePlanBean;
import com.rspl.sf.msfa.mtp.approval.MTPApprovalBean;
import com.rspl.sf.msfa.registration.Configuration;
import com.rspl.sf.msfa.reports.daySummary.DashBoardBean;
import com.rspl.sf.msfa.returnOrder.ReturnOrderBean;
import com.rspl.sf.msfa.returnOrder.returnDetail.ReturnOrderItemBean;
import com.rspl.sf.msfa.so.SOUtils;
import com.rspl.sf.msfa.so.ValueHelpBean;
import com.rspl.sf.msfa.soDetails.SOTextBean;
import com.rspl.sf.msfa.soapproval.SalesApprovalBean;
import com.rspl.sf.msfa.soapproval.SalesOrderConditionsBean;
import com.rspl.sf.msfa.socreate.ConfigTypeValues;
import com.rspl.sf.msfa.socreate.CustomerPartnerFunctionBean;
import com.rspl.sf.msfa.socreate.DefaultValueBean;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.solist.SOListBean;
import com.rspl.sf.msfa.solist.SOTaskHistoryBean;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.httpc.HttpConversationManager;
import com.sap.smp.client.httpc.HttpMethod;
import com.sap.smp.client.httpc.events.IReceiveEvent;
import com.sap.smp.client.httpc.listeners.ICommunicationErrorListener;
import com.sap.smp.client.httpc.listeners.IConversationFlowListener;
import com.sap.smp.client.httpc.listeners.IResponseListener;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataEntitySet;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.ODataNavigationProperty;
import com.sap.smp.client.odata.ODataPayload;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.exception.ODataContractViolationException;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.exception.ODataNetworkException;
import com.sap.smp.client.odata.exception.ODataParserException;
import com.sap.smp.client.odata.impl.ODataPropertyDefaultImpl;
import com.sap.smp.client.odata.store.ODataRequestExecution;
import com.sap.smp.client.odata.store.ODataResponseSingle;
import com.sap.smp.client.odata.store.impl.ODataResponseSingleDefaultImpl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class OnlineManager {
    public static final String TAG = OnlineManager.class.getSimpleName();

    /**
     * Initialize an online OData store for online access
     *
     * @param context used only to access the application context
     * @return true if the online is open and false otherwise
     * @throws OnlineODataStoreException
     */

    public static boolean openOnlineStore(Context context, boolean isForceMetadata) throws OnlineODataStoreException {
        try {
            //OnlineOpenListener implements OpenListener interface
            //Listener to be invoked when the opening process of an OnlineODataStore object finishes
            /*if (isForceMetadata) {
                try {
                    OnlineStoreListener openListener = OnlineStoreListener.getInstance();
                    OnlineODataStore store = openListener.getStore();
                    if (store != null && store.isOpenCache())
                        store.closeCache();
                    if (store != null) {
                        store.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Constants.onlineStore = null;
            OnlineStoreListener.instance = null;
            Constants.IsOnlineStoreFailed = false;
            Constants.Error_Msg = "";
            try {
                OnlineStoreListener openListener = OnlineStoreListener.getInstance();
                LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();

                //The logon configurator uses the information obtained in the registration
                *//*IManagerConfigurator configurator = LogonUIFacade.getInstance().getLogonConfigurator(context);
                HttpConversationManager manager = new HttpConversationManager(context);
                configurator.configure(manager);*//*
                CredentialsProvider credProvider = CredentialsProvider
                        .getInstance(lgCtx);
                HttpConversationManager manager = new CommonAuthFlowsConfigurator(
                        context).supportBasicAuthUsing(credProvider).configure(
                        new HttpConversationManager(context));
                OnlineODataStore.OnlineStoreOptions onlineOptions = new OnlineODataStore.OnlineStoreOptions();
                SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME,0);
                String rollType = sharedPreferences.getString(Constants.USERROLE, "");
                if(Constants.getRollID(context) || TextUtils.isEmpty(rollType)) {
                    onlineOptions.useCache = true;//if true technical cache is enabled
                    onlineOptions.cacheEncryptionKey = Constants.EncryptKey;
                }
                if (ConstantsUtils.getFirstTimeRun(context) == 2) {
                    onlineOptions.forceMetadataDownload = true;
                } else {
                    onlineOptions.forceMetadataDownload = isForceMetadata;
                }
                //XCSRFTokenRequestFilter implements IRequestFilter
                //Request filter that is allowed to preprocess the request before sending
                XCSRFTokenRequestFilter requestFilter = XCSRFTokenRequestFilter.getInstance(context,lgCtx);
                XCSRFTokenResponseFilter responseFilter = XCSRFTokenResponseFilter.getInstance(context,
                        requestFilter);
                manager.addFilter(requestFilter);
                manager.addFilter(responseFilter);

                try {
                    String endPointURL = lgCtx.getAppEndPointUrl();
                    URL url = new URL(endPointURL);
                    //Method to open a new online store asynchronously

                    OnlineODataStore.open(context, url, manager, openListener, onlineOptions);


                    //            openListener.waitForCompletion();
                    if (openListener.getError() != null) {
                        throw openListener.getError();
                    }
                } catch (Exception e) {
                    throw new OnlineODataStoreException(e);
                }
                //Check if OnlineODataStore opened successfully

                while (!Constants.IsOnlineStoreFailed) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Constants.IsOnlineStoreFailed = false;


                return Constants.onlineStore != null;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }*/
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }
//    public static boolean openOnlineStore(Context context) throws OnlineODataStoreException {
//        //AgencyOpenListener implements OpenListener interface
//        //Listener to be invoked when the opening process of an OnlineODataStore object finishes
//        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
//            LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();
//
//            //The logon configurator uses the information obtained in the registration
//            IManagerConfigurator configurator = LogonUIFacade.getInstance().getLogonConfigurator(context);
//            HttpConversationManager manager = new HttpConversationManager(context);
//            configurator.configure(manager);
//
//            //XCSRFTokenRequestFilter implements IRequestFilter
//            //Request filter that is allowed to preprocess the request before sending
//            XCSRFTokenRequestFilter requestFilter = XCSRFTokenRequestFilter.getInstance(lgCtx);
//            XCSRFTokenResponseFilter responseFilter = XCSRFTokenResponseFilter.getInstance(context,
//                    requestFilter);
//            manager.addFilter(requestFilter);
//            manager.addFilter(responseFilter);
//
//            try {
//                String endPointURL = lgCtx.getAppEndPointUrl();
//                URL url = new URL(endPointURL);
//                //Method to open a new online store asynchronously
//
//                OnlineODataStore.open(context, url, manager, openListener, null);
////                openListener.waitForCompletion();
//                if (openListener.getError() != null) {
//                    throw openListener.getError();
//                }
//            } catch(Exception e){
//               throw new OnlineODataStoreException(e);
//           }
//            //Check if OnlineODataStore opened successfully
//            //OnlineODataStore store = openListener.getStore();
//            if (Constants.onlineStore != null) {
//                return true;
//            } else {
//                return false;
//            }
//    }


    /*DashBroad Online Store*/
    public static boolean openOnlineStoreDashBroad(Context context, boolean useCache) throws com.arteriatech.mutils.common.OnlineODataStoreException {
        /*try {
            //AgencyOpenListener implements OpenListener interface
            //Listener to be invoked when the opening process of an OnlineODataStore object finishes
            Constants.onlineStoreDashBroad = null;
            OnlineStoreListenerDashBroad.instance = null;
            Constants.IsOnlineStoreFailedDashBroad = false;
            OnlineStoreListenerDashBroad openListener = OnlineStoreListenerDashBroad.getInstance();
            LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();

            //The logon configurator uses the information obtained in the registration
        *//*IManagerConfigurator configurator = LogonUIFacade.getInstance().getLogonConfigurator(context);
        HttpConversationManager manager = new HttpConversationManager(context);
        configurator.configure(manager);*//*
            CredentialsProvider credProvider = CredentialsProvider
                    .getInstance(lgCtx);
            HttpConversationManager manager = new CommonAuthFlowsConfigurator(
                    context).supportBasicAuthUsing(credProvider).configure(
                    new HttpConversationManager(context));

            OnlineODataStore.OnlineStoreOptions onlineOptions = new OnlineODataStore.OnlineStoreOptions();
            onlineOptions.useCache = true;//if true technical cache is enabled
            onlineOptions.cacheEncryptionKey = Constants.EncryptKey;
            if (ConstantsUtils.getFirstTimeRunCFOnlineStore(context) == 2) {
                onlineOptions.forceMetadataDownload = true;
                Log.d("Performance","Test");
            } else {
    //            onlineOptions.forceMetadataDownload = false;
                onlineOptions.forceMetadataDownload = false;
            }

            //XCSRFTokenRequestFilter implements IRequestFilter
            //Request filter that is allowed to preprocess the request before sending
            XCSRFTokenRequestFilter requestFilter = XCSRFTokenRequestFilter.getInstance(context,lgCtx);
            XCSRFTokenResponseFilter responseFilter = XCSRFTokenResponseFilter.getInstance(context,
                    requestFilter);
            manager.addFilter(requestFilter);
            manager.addFilter(responseFilter);

            try {
    //            String endPointURL = lgCtx.getAppEndPointUrl();
    //            URL url = new URL(endPointURL);

                URL url = null;
                String protocol = lgCtx.isHttps() ? "https" : "http";
                url = new URL("" + protocol + "://" + lgCtx.getHost() + ":" + lgCtx.getPort() + "/" + Constants.DashBoards);
                //Method to open a new online store asynchronously

                OnlineODataStore.open(context, url, manager, openListener, onlineOptions);
    //            openListener.waitForCompletion();
                if (openListener.getError() != null) {
                    throw openListener.getError();
                }
            } catch (Exception e) {
                throw new com.arteriatech.mutils.common.OnlineODataStoreException(e);
            }
            while (!Constants.IsOnlineStoreFailedDashBroad) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Constants.IsOnlineStoreFailedDashBroad = false;

            return Constants.onlineStoreDashBroad != null;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }*/
        return true;
    }

    /**
     * Create Entity for collection creation and Schedule in Online Manager
     *
     * @throws OnlineODataStoreException
     */
    public static void createCollectionEntry(Hashtable<String, String> table, ArrayList<HashMap<String, String>> itemtable, UIListener uiListener) throws OnlineODataStoreException {
        /*OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                //Creates the entity payload
                ODataEntity collectionCreateEntity = createCollectionEntryEntity(table, itemtable, store);

                OnlineRequestListener collectionListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);

                String fipGUID32 = table.get(Constants.DocumentNo).replace("-", "");

                String collCreatedOn = table.get(Constants.CreatedOn);
                String collCreatedAt = table.get(Constants.CreatedAt);

                String mStrDateTime = UtilConstants.getReArrangeDateFormat(collCreatedOn) + Constants.T + UtilConstants.convertTimeOnly(collCreatedAt);

                Map<String, String> createHeaders = new HashMap<String, String>();
                createHeaders.put(Constants.RequestID, fipGUID32);
                createHeaders.put(Constants.RepeatabilityCreation, mStrDateTime);

                ODataRequestParamSingle collectionReq = new ODataRequestParamSingleDefaultImpl();
                collectionReq.setMode(ODataRequestParamSingle.Mode.Create);
                collectionReq.setResourcePath(collectionCreateEntity.getResourcePath());
                collectionReq.setPayload(collectionCreateEntity);
                collectionReq.getCustomHeaders().putAll(createHeaders);

                store.scheduleRequest(collectionReq, collectionListener);

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }*/
        //END

    }

    /**
     * Create Entity for collection creation
     *
     * @throws ODataParserException
     */
    /*private static ODataEntity createCollectionEntryEntity(Hashtable<String, String> hashtable, ArrayList<HashMap<String, String>> itemhashtable, OnlineODataStore store) throws ODataParserException {
        ODataEntity newHeaderEntity = null;
        ODataEntity newItemEntity = null;
        ArrayList<ODataEntity> tempArray = new ArrayList();
        try {
            if (hashtable != null) {
                newHeaderEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + Constants.CollectionEntity);

                newHeaderEntity.setResourcePath(Constants.Collections, Constants.Collections);

                try {
                    store.allocateProperties(newHeaderEntity, PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }
                //If available, it populates the navigation properties of an OData Entity
                store.allocateNavigationProperties(newHeaderEntity);

//                newHeaderEntity.getProperties().put(Constants.DocumentNo,
//                        new ODataPropertyDefaultImpl(Constants.DocumentNo, hashtable.get(Constants.DocumentNo)));


                newHeaderEntity.getProperties().put(Constants.CustomerNo,
                        new ODataPropertyDefaultImpl(Constants.CustomerNo, hashtable.get(Constants.CustomerNo)));
                newHeaderEntity.getProperties().put(Constants.CustomerName,
                        new ODataPropertyDefaultImpl(Constants.CustomerName, hashtable.get(Constants.CustomerName)));
                if (!hashtable.get(Constants.BankName).equalsIgnoreCase("")) {
                    newHeaderEntity.getProperties().put(Constants.BankName,
                            new ODataPropertyDefaultImpl(Constants.BankName, hashtable.get(Constants.BankName)));
                }
                if (!hashtable.get(Constants.InstrumentNo).equalsIgnoreCase("")) {
                    newHeaderEntity.getProperties().put(Constants.InstrumentNo,
                            new ODataPropertyDefaultImpl(Constants.InstrumentNo, hashtable.get(Constants.InstrumentNo)));
                }
                newHeaderEntity.getProperties().put(Constants.Amount,
                        new ODataPropertyDefaultImpl(Constants.Amount, BigDecimal.valueOf(Double.parseDouble(hashtable.get(Constants.Amount)))));

                if (!hashtable.get(Constants.Remarks).equalsIgnoreCase("")) {
                    newHeaderEntity.getProperties().put(Constants.Remarks,
                            new ODataPropertyDefaultImpl(Constants.Remarks, hashtable.get(Constants.Remarks)));
                }
                newHeaderEntity.getProperties().put(Constants.CollectionTypeID,
                        new ODataPropertyDefaultImpl(Constants.CollectionTypeID, hashtable.get(Constants.CollectionTypeID)));

                newHeaderEntity.getProperties().put(Constants.PaymentMethodID,
                        new ODataPropertyDefaultImpl(Constants.PaymentMethodID, hashtable.get(Constants.PaymentMethodID)));

                newHeaderEntity.getProperties().put(Constants.DocumentDate,
                        new ODataPropertyDefaultImpl(Constants.DocumentDate, UtilConstants.convertDateFormat(hashtable.get(Constants.DocumentDate))));

                if (!hashtable.get(Constants.InstrumentDate).equalsIgnoreCase("")) {
                    newHeaderEntity.getProperties().put(Constants.InstrumentDate,
                            new ODataPropertyDefaultImpl(Constants.InstrumentDate, UtilConstants.convertDateFormat(hashtable.get(Constants.InstrumentDate))));
                }
                newHeaderEntity.getProperties().put(Constants.LOGINID,
                        new ODataPropertyDefaultImpl(Constants.LOGINID, hashtable.get(Constants.LOGINID)));
                newHeaderEntity.getProperties().put(Constants.Currency,
                        new ODataPropertyDefaultImpl(Constants.Currency, hashtable.get(Constants.Currency)));


                int incremntVal = 0;
                for (int i = 0; i < itemhashtable.size(); i++) {

                    HashMap<String, String> singleRow = itemhashtable.get(i);

                    incremntVal = i + 1;

                    newItemEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + Constants.CollectionItemEntity);

                    newItemEntity.setResourcePath(Constants.CollectionItemDetails + "(" + singleRow.get(Constants.ItemNo) + ")", Constants.CollectionItemDetails + "(" + singleRow.get(Constants.ItemNo) + ")");
                    try {
                        store.allocateProperties(newItemEntity, PropMode.Keys);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }
                    newItemEntity.getProperties().put(Constants.ItemNo,
                            new ODataPropertyDefaultImpl(Constants.ItemNo, singleRow.get(Constants.ItemNo)));

//                    newItemEntity.getProperties().put(Constants.DocumentNo,
//                            new ODataPropertyDefaultImpl(Constants.DocumentNo,singleRow.get(Constants.DocumentNo)));

                    newItemEntity.getProperties().put(Constants.Currency,
                            new ODataPropertyDefaultImpl(Constants.Currency, hashtable.get(Constants.Currency)));

                    if (hashtable.get(Constants.CollectionTypeID).equalsIgnoreCase("03")) {
                        newItemEntity.getProperties().put(Constants.OpenAmount,
                                new ODataPropertyDefaultImpl(Constants.OpenAmount, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.OpenAmount)))));
                        newItemEntity.getProperties().put(Constants.CollectedAmount,
                                new ODataPropertyDefaultImpl(Constants.CollectedAmount, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.CollectedAmount)))));

                        newItemEntity.getProperties().put(Constants.InvoiceDate,
                                new ODataPropertyDefaultImpl(Constants.InvoiceDate, UtilConstants.convertDateFormat(singleRow.get(Constants.InvoiceDate))));

                        newItemEntity.getProperties().put(Constants.InvoicedAmount,
                                new ODataPropertyDefaultImpl(Constants.InvoicedAmount, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.InvoicedAmount)))));

                        newItemEntity.getProperties().put(Constants.InvoiceNo,
                                new ODataPropertyDefaultImpl(Constants.InvoiceNo, singleRow.get(Constants.InvoiceNo)));
                        newItemEntity.getProperties().put(Constants.InvoiceTypeID,
                                new ODataPropertyDefaultImpl(Constants.InvoiceTypeID, singleRow.get(Constants.InvoiceTypeID)));
                        newItemEntity.getProperties().put(Constants.InvoiceTypeDesc,
                                new ODataPropertyDefaultImpl(Constants.InvoiceTypeDesc, singleRow.get(Constants.InvoiceTypeDesc)));
                    }

                    tempArray.add(i, newItemEntity);

                }

                ODataEntitySetDefaultImpl itemEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itemEntity.getEntities().add(entity);
                }
                itemEntity.setResourcePath(Constants.CollectionItemDetails);

                ODataNavigationProperty navProp = newHeaderEntity.getNavigationProperty(Constants.CollectionItemDetails);
                navProp.setNavigationContent(itemEntity);
                newHeaderEntity.setNavigationProperty(Constants.CollectionItemDetails, navProp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newHeaderEntity;
    }

*/
    /**
     * Create Entity for collection creation and Schedule in Online Manager
     *
     * @throws OnlineODataStoreException
     */
    public static void createSOEntity(Hashtable<String, String> table, ArrayList<HashMap<String, String>> itemtable, UIListener uiListener) throws OnlineODataStoreException {
        /*OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                //Creates the entity payload
                ODataEntity soCreateEntity = createSOCreateEntity(table, itemtable, store);

                OnlineRequestListener collectionListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);

                String ssoGUID32 = table.get(Constants.SONo);

                String soCreatedOn = table.get(Constants.CreatedOn);
                String soCreatedAt = table.get(Constants.CreatedAt);

                String mStrDateTime = UtilConstants.getReArrangeDateFormat(soCreatedOn) + Constants.T + UtilConstants.convertTimeOnly(soCreatedAt);

                Map<String, String> createHeaders = new HashMap<String, String>();
                createHeaders.put(Constants.RequestID, ssoGUID32);
                createHeaders.put(Constants.RepeatabilityCreation, mStrDateTime);

                ODataRequestParamSingle collectionReq = new ODataRequestParamSingleDefaultImpl();
                collectionReq.setMode(ODataRequestParamSingle.Mode.Create);
                collectionReq.setResourcePath(soCreateEntity.getResourcePath());
                collectionReq.setPayload(soCreateEntity);
                collectionReq.getCustomHeaders().putAll(createHeaders);

                store.scheduleRequest(collectionReq, collectionListener);

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }
        //END*/

    }

    /**
     * Create Entity for collection creation
     *
     * @throws ODataParserException
     */
    /*private static ODataEntity createSOCreateEntity(Hashtable<String, String> hashtable, ArrayList<HashMap<String, String>> itemhashtable, OnlineODataStore store) throws ODataParserException {
        ODataEntity newHeaderEntity = null;
        ODataEntity newItemEntity = null;
        ArrayList<ODataEntity> tempArray = new ArrayList();
        try {
            if (hashtable != null) {
                newHeaderEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + Constants.SalesOrderEntity);

                newHeaderEntity.setResourcePath(Constants.SOs, Constants.SOs);

                try {
                    store.allocateProperties(newHeaderEntity, PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }
                //If available, it populates the navigation properties of an OData Entity
                store.allocateNavigationProperties(newHeaderEntity);
*//*
                newHeaderEntity.getProperties().put(Constants.SSSOGuid,
                        new ODataPropertyDefaultImpl(Constants.SSSOGuid, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SSSOGuid))));
                newHeaderEntity.getProperties().put(Constants.CPGUID,
                        new ODataPropertyDefaultImpl(Constants.CPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.CPGUID))));*//*
                newHeaderEntity.getProperties().put(Constants.OrderType,
                        new ODataPropertyDefaultImpl(Constants.OrderType, hashtable.get(Constants.OrderType)));
                newHeaderEntity.getProperties().put(Constants.SONo,
                        new ODataPropertyDefaultImpl(Constants.SONo, hashtable.get(Constants.SONo)));
//              newHeaderEntity.getProperties().put(Constants.LoginId,
//                        new ODataPropertyDefaultImpl(Constants.LoginId, hashtable.get(Constants.LOGINID)));
                newHeaderEntity.getProperties().put(Constants.CustomerNo,
                        new ODataPropertyDefaultImpl(Constants.CustomerNo, hashtable.get(Constants.CustomerNo)));
               *//* newHeaderEntity.getProperties().put(Constants.CustomerPO,
                        new ODataPropertyDefaultImpl(Constants.CustomerPO, hashtable.get(Constants.CustomerPO)));*//**//*
                newHeaderEntity.getProperties().put(Constants.CustomerName,
                        new ODataPropertyDefaultImpl(Constants.CustomerName, hashtable.get(Constants.CustomerName)));
                newHeaderEntity.getProperties().put(Constants.ShipToParty,
                        new ODataPropertyDefaultImpl(Constants.ShipToParty, hashtable.get(Constants.ShipToParty)));

                newHeaderEntity.getProperties().put(Constants.OrderType,
                        new ODataPropertyDefaultImpl(Constants.OrderType, hashtable.get(Constants.OrderType)));
                newHeaderEntity.getProperties().put(Constants.OrderTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.OrderTypeDesc, hashtable.get(Constants.OrderTypeDesc)));
                newHeaderEntity.getProperties().put(Constants.OrderDate,
                        new ODataPropertyDefaultImpl(Constants.OrderDate, UtilConstants.convertDateFormat(hashtable.get(Constants.OrderDate))));

                newHeaderEntity.getProperties().put(Constants.Discount,
                        new ODataPropertyDefaultImpl(Constants.Discount, BigDecimal.valueOf(Double.parseDouble(hashtable.get(Constants.Discount)))));
                newHeaderEntity.getProperties().put(Constants.NetPrice,
                        new ODataPropertyDefaultImpl(Constants.NetPrice, BigDecimal.valueOf(Double.parseDouble(hashtable.get(Constants.NetPrice)))));

                newHeaderEntity.getProperties().put(Constants.TaxAmount,
                        new ODataPropertyDefaultImpl(Constants.TaxAmount, BigDecimal.valueOf(Double.parseDouble(hashtable.get(Constants.TaxAmount)))));
                newHeaderEntity.getProperties().put(Constants.Freight,
                        new ODataPropertyDefaultImpl(Constants.Freight, BigDecimal.valueOf(Double.parseDouble(hashtable.get(Constants.Freight)))));*//*


                int incremntVal = 0;
                for (int incrementVal = 0; incrementVal < itemhashtable.size(); incrementVal++) {

                    HashMap<String, String> singleRow = itemhashtable.get(incrementVal);

                    incremntVal = incrementVal + 1;

                    newItemEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + Constants.SalesOrderItemEntity);

                    newItemEntity.setResourcePath(Constants.SOItemDetails + "(" + incremntVal + ")", Constants.SOItemDetails + "(" + incremntVal + ")");
                    try {
                        store.allocateProperties(newItemEntity, PropMode.Keys);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }

                    newItemEntity.getProperties().put(Constants.SONo,
                            new ODataPropertyDefaultImpl(Constants.SONo, singleRow.get(Constants.SONo)));

                    newItemEntity.getProperties().put(Constants.ItemNo,
                            new ODataPropertyDefaultImpl(Constants.ItemNo, singleRow.get(Constants.ItemNo)));
                  *//*  newItemEntity.getProperties().put(Constants.LoginID,
                            new ODataPropertyDefaultImpl(Constants.LoginID, singleRow.get(Constants.LoginID)));*//*
                    newItemEntity.getProperties().put(Constants.Material,
                            new ODataPropertyDefaultImpl(Constants.Material, singleRow.get(Constants.Material)));
                   *//* newItemEntity.getProperties().put(Constants.MaterialDesc,
                            new ODataPropertyDefaultImpl(Constants.MaterialDesc, singleRow.get(Constants.MaterialDesc)));
                    newItemEntity.getProperties().put(Constants.MaterialGroup,
                            new ODataPropertyDefaultImpl(Constants.MaterialGroup, singleRow.get(Constants.MaterialGroup)));
                    newItemEntity.getProperties().put(Constants.MatGroupDesc,
                            new ODataPropertyDefaultImpl(Constants.MatGroupDesc, singleRow.get(Constants.MatGroupDesc)));*//*


                    newItemEntity.getProperties().put(Constants.ItemCategory,
                            new ODataPropertyDefaultImpl(Constants.ItemCategory, "Test 1"));
                    newItemEntity.getProperties().put(Constants.HighLevellItemNo,
                            new ODataPropertyDefaultImpl(Constants.HighLevellItemNo, singleRow.get(Constants.HighLevellItemNo)));

                   *//* newItemEntity.getProperties().put(Constants.Quantity,
                            new ODataPropertyDefaultImpl(Constants.Quantity, singleRow.get(Constants.Quantity)));*//*



       *//*             newItemEntity.getProperties().put(Constants.Currency,
                            new ODataPropertyDefaultImpl(Constants.Currency, singleRow.get(Constants.Currency)));*//*

                    *//*newItemEntity.getProperties().put(Constants.Uom,
                            new ODataPropertyDefaultImpl(Constants.Uom, singleRow.get(Constants.Uom)));*//*
                   *//* newItemEntity.getProperties().put(Constants.UnitPrice,
                            new ODataPropertyDefaultImpl(Constants.UnitPrice, BigDecimal.valueOf(Double.parseDouble(hashtable.get(Constants.UnitPrice)))));

                    newItemEntity.getProperties().put(Constants.GrossAmount,
                            new ODataPropertyDefaultImpl(Constants.GrossAmount, BigDecimal.valueOf(Double.parseDouble(hashtable.get(Constants.GrossAmount)))));

                    newItemEntity.getProperties().put(Constants.Discount,
                            new ODataPropertyDefaultImpl(Constants.Discount, BigDecimal.valueOf(Double.parseDouble(hashtable.get(Constants.Discount)))));
                    newItemEntity.getProperties().put(Constants.NetPrice,
                            new ODataPropertyDefaultImpl(Constants.NetPrice, BigDecimal.valueOf(Double.parseDouble(hashtable.get(Constants.NetPrice)))));

                    newItemEntity.getProperties().put(Constants.TaxAmount,
                            new ODataPropertyDefaultImpl(Constants.TaxAmount, BigDecimal.valueOf(Double.parseDouble(hashtable.get(Constants.TaxAmount)))));
                    newItemEntity.getProperties().put(Constants.Freight,
                            new ODataPropertyDefaultImpl(Constants.Freight, BigDecimal.valueOf(Double.parseDouble(hashtable.get(Constants.Freight)))));

*//*


                    tempArray.add(incrementVal, newItemEntity);

                }

                ODataEntitySetDefaultImpl itemEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itemEntity.getEntities().add(entity);
                }
                itemEntity.setResourcePath(Constants.SOItemDetails);

                ODataNavigationProperty navProp = newHeaderEntity.getNavigationProperty(Constants.SOItemDetails);
                navProp.setNavigationContent(itemEntity);
                newHeaderEntity.setNavigationProperty(Constants.SOItemDetails, navProp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newHeaderEntity;
    }*/
    public static byte[] getMerchindisingImage(String merImgQuery) throws OnlineODataStoreException {
        //BEGIN
        final byte[][] bytes = {null};
       /* final boolean[] isDataAvailable = {false};
        //Get the open online store
        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        final InputStream[] inputStream = {null};


        if (store != null) {
            try {
                //Executor method for reading an Entity set synchronously


                URL urlPath = new URL(merImgQuery);
                ODataDownloadMediaListener oDataDownloadMediaListener = new ODataDownloadMediaListener() {
                    @Override
                    public void mediaDownloadStarted(ODataDownloadMediaExecution oDataDownloadMediaExecution) {

                    }

                    @Override
                    public void mediaDownloadCacheResponse(ODataDownloadMediaExecution oDataDownloadMediaExecution, ODataDownloadMediaResult oDataDownloadMediaResult) {

                    }

                    @Override
                    public void mediaDownloadServerResponse(ODataDownloadMediaExecution oDataDownloadMediaExecution, ODataDownloadMediaResult oDataDownloadMediaResult) {

                        ODataDownloadMediaResultDefaultImpl oDataDownloadMediaResultDefault = (ODataDownloadMediaResultDefaultImpl) oDataDownloadMediaResult;

                        InputStream is = oDataDownloadMediaResultDefault.getInputStream();
                        byte[] buf = null;
                        try {
                            int len;
                            int size = 100 * 1024;


                            if (is instanceof ByteArrayInputStream) {
                                size = is.available();
                                buf = new byte[size];
                                len = is.read(buf, 0, size);
                            } else {
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                buf = new byte[size];
                                while ((len = is.read(buf, 0, size)) != -1)
                                    bos.write(buf, 0, len);
                                buf = bos.toByteArray();


                            }
                        } catch (IOException e) {

                        }

                        bytes[0] = buf;

                        isDataAvailable[0] = true;
                    }

                    @Override
                    public void mediaDownloadFailed(ODataDownloadMediaExecution oDataDownloadMediaExecution, ODataException e) {
                        bytes[0] = null;
                        isDataAvailable[0] = true;

                    }

                    @Override
                    public void mediaDownloadFinished(ODataDownloadMediaExecution oDataDownloadMediaExecution) {

                    }
                };
                store.scheduleMediaDownload(urlPath, oDataDownloadMediaListener);

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }

        while (!isDataAvailable[0]) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isDataAvailable[0] = false;*/

        return bytes[0];
        //END
    }
    /*get simulate value*/
    public static void getSimulateValue(Hashtable<String, String> headerTable, ArrayList<HashMap<String, String>> itemtable, OnlineODataInterface onlineODataInterface, Bundle bundle) throws com.arteriatech.mutils.common.OnlineODataStoreException {
        //BEGIN
        //Get the open online store
        /*OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {

                ODataEntity newEntity = createSoSimulation(store, headerTable, itemtable, 1);

                OnlineRequestListeners listener = new OnlineRequestListeners(onlineODataInterface, bundle);

                store.scheduleCreateEntity(newEntity, Constants.SOs, listener, null);

            } catch (Exception e) {
                throw new com.arteriatech.mutils.common.OnlineODataStoreException(e);
            }
        }*/
        //END
    }

   /* public static void getSimulateValue(Hashtable<String, String> headerTable, ArrayList<HashMap<String, String>> itemtable, UIListener uiListener) throws com.arteriatech.mutils.common.OnlineODataStoreException {
        //BEGIN
        //Get the open online store
        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {

                ODataEntity newEntity = createSoSimulation(store, headerTable, itemtable, 1);

                OnlineRequestListener invoiceListener = new OnlineRequestListener(Operation.GetRequest.getValue(), uiListener);

                store.scheduleCreateEntity(newEntity, Constants.SOs, invoiceListener, null);

            } catch (Exception e) {
                throw new com.arteriatech.mutils.common.OnlineODataStoreException(e);
            }
        }
        //END
    }*/

    /*private static ODataEntity createSoSimulation(OnlineODataStore store, Hashtable<String, String> headerhashtable, ArrayList<HashMap<String, String>> itemhashtable,int isSimulateType) {
        ODataEntity newHeaderEntity = null;
        ODataEntity newItemEntity = null;
        ODataEntity newSubItemEntity = null;
        ODataEntity newTextEntity = null;
        ODataProperty property;
        ODataPropMap properties;

        ArrayList<ODataEntity> tempArray = new ArrayList();
        ArrayList<ODataEntity> tempSubArray = new ArrayList();
        ArrayList<ODataEntity> tempTextArray = new ArrayList();
        ArrayList<ODataEntity> tempSerialArray;

        try {
            if (headerhashtable != null) {
                //Use default implementation to create a new travel agency entity type
                newHeaderEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpace(OfflineManager.offlineStore)+""+Constants.SOS_ENTITY);

                String resourcePath = Constants.getEditResourcePath(Constants.SOs, Constants.SOs);
                newHeaderEntity.setResourcePath(Constants.SOs, Constants.SOs);

                //If available, it will populates those properties of an OData Entity
                //which are defined by the allocation mode
                try {
                    store.allocateProperties(newHeaderEntity, ODataStore.PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }
                //If available, it populates the navigation properties of an OData Entity
                store.allocateNavigationProperties(newHeaderEntity);

                //Set the corresponding properties
                if(headerhashtable.get(Constants.SONo)!=null) {
                    newHeaderEntity.getProperties().put(Constants.SONo,
                            new ODataPropertyDefaultImpl(Constants.SONo, headerhashtable.get(Constants.SONo)));
                }

                newHeaderEntity.getProperties().put(Constants.OrderType,
                        new ODataPropertyDefaultImpl(Constants.OrderType, headerhashtable.get(Constants.OrderType)));

                newHeaderEntity.getProperties().put(Constants.OrderDate,
                        new ODataPropertyDefaultImpl(Constants.OrderDate, Constants.convertDateFormat(headerhashtable.get(Constants.OrderDate))));

                newHeaderEntity.getProperties().put(Constants.CustomerNo,
                        new ODataPropertyDefaultImpl(Constants.CustomerNo, headerhashtable.get(Constants.CustomerNo)));
                newHeaderEntity.getProperties().put(Constants.CustomerPO,
                        new ODataPropertyDefaultImpl(Constants.CustomerPO, headerhashtable.get(Constants.CustomerPO)));
                newHeaderEntity.getProperties().put(Constants.CustomerPODate,
                        new ODataPropertyDefaultImpl(Constants.CustomerPODate, Constants.convertDateFormat(headerhashtable.get(Constants.CustomerPODate))));
                newHeaderEntity.getProperties().put(Constants.ShippingTypeID,
                        new ODataPropertyDefaultImpl(Constants.ShippingTypeID, headerhashtable.get(Constants.ShippingTypeID)));
//                newHeaderEntity.getProperties().put(Constants.MeansOfTranstyp,
//                        new ODataPropertyDefaultImpl(Constants.MeansOfTranstyp, headerhashtable.get(Constants.MeansOfTranstyp)));
                newHeaderEntity.getProperties().put(Constants.ShipToParty,
                        new ODataPropertyDefaultImpl(Constants.ShipToParty, headerhashtable.get(Constants.ShipToParty)));
                newHeaderEntity.getProperties().put(Constants.SalesArea,
                        new ODataPropertyDefaultImpl(Constants.SalesArea, headerhashtable.get(Constants.SalesArea)));
                newHeaderEntity.getProperties().put(Constants.SalesOffice,
                        new ODataPropertyDefaultImpl(Constants.SalesOffice, headerhashtable.get(Constants.SalesOffice)));
                newHeaderEntity.getProperties().put(Constants.SalesGroup,
                        new ODataPropertyDefaultImpl(Constants.SalesGroup, headerhashtable.get(Constants.SalesGroup)));
                newHeaderEntity.getProperties().put(Constants.Plant,
                        new ODataPropertyDefaultImpl(Constants.Plant, headerhashtable.get(Constants.Plant)));

                newHeaderEntity.getProperties().put(Constants.PlantDesc,
                        new ODataPropertyDefaultImpl(Constants.PlantDesc, headerhashtable.get(Constants.PlantDesc)));
//
//                newHeaderEntity.getProperties().put(Constants.TransporterID,
//                        new ODataPropertyDefaultImpl(Constants.TransporterID, headerhashtable.get(Constants.TransporterID)));
//                newHeaderEntity.getProperties().put(Constants.TransporterName,
//                        new ODataPropertyDefaultImpl(Constants.TransporterName, headerhashtable.get(Constants.TransporterName)));

                newHeaderEntity.getProperties().put(Constants.Incoterm1,
                        new ODataPropertyDefaultImpl(Constants.Incoterm1, headerhashtable.get(Constants.Incoterm1)));

                newHeaderEntity.getProperties().put(Constants.Incoterm1Desc,
                        new ODataPropertyDefaultImpl(Constants.Incoterm1Desc, headerhashtable.get(Constants.Incoterm1Desc)));

                newHeaderEntity.getProperties().put(Constants.Incoterm2,
                        new ODataPropertyDefaultImpl(Constants.Incoterm2, headerhashtable.get(Constants.Incoterm2)));

                newHeaderEntity.getProperties().put(Constants.Payterm,
                        new ODataPropertyDefaultImpl(Constants.Payterm, headerhashtable.get(Constants.Payterm)));

                newHeaderEntity.getProperties().put(Constants.PaytermDesc,
                        new ODataPropertyDefaultImpl(Constants.PaytermDesc, headerhashtable.get(Constants.PaytermDesc)));


//                newHeaderEntity.getProperties().put(Constants.SalesDist,
//                        new ODataPropertyDefaultImpl(Constants.SalesDist, hashtable.get(Constants.SalesDist)));
//
//                newHeaderEntity.getProperties().put(Constants.Plant,
//                        new ODataPropertyDefaultImpl(Constants.Plant, hashtable.get(Constants.Plant)));
//
//                newHeaderEntity.getProperties().put(Constants.MeansOfTranstyp,
//                        new ODataPropertyDefaultImpl(Constants.MeansOfTranstyp, hashtable.get(Constants.MeansOfTranstyp)));


                newHeaderEntity.getProperties().put(Constants.Currency,
                        new ODataPropertyDefaultImpl(Constants.Currency, headerhashtable.get(Constants.Currency)));
                newHeaderEntity.getProperties().put(Constants.NetPrice,
                        new ODataPropertyDefaultImpl(Constants.NetPrice, BigDecimal.valueOf(Double.parseDouble(headerhashtable.get(Constants.NetPrice)))));
                newHeaderEntity.getProperties().put(Constants.TotalAmount,
                        new ODataPropertyDefaultImpl(Constants.TotalAmount, BigDecimal.valueOf(Double.parseDouble(headerhashtable.get(Constants.TotalAmount)))));
                newHeaderEntity.getProperties().put(Constants.TaxAmount,
                        new ODataPropertyDefaultImpl(Constants.TaxAmount, BigDecimal.valueOf(Double.parseDouble(headerhashtable.get(Constants.TaxAmount)))));
                newHeaderEntity.getProperties().put(Constants.Freight,
                        new ODataPropertyDefaultImpl(Constants.Freight, BigDecimal.valueOf(Double.parseDouble(headerhashtable.get(Constants.Freight)))));
                newHeaderEntity.getProperties().put(Constants.Discount,
                        new ODataPropertyDefaultImpl(Constants.Discount, BigDecimal.valueOf(Double.parseDouble(headerhashtable.get(Constants.Discount)))));
                if(headerhashtable.get(Constants.Testrun)!=null) {
                    newHeaderEntity.getProperties().put(Constants.Testrun,
                            new ODataPropertyDefaultImpl(Constants.Testrun, headerhashtable.get(Constants.Testrun)));
                }
                if(isSimulateType==2) {
//                    String textString = headerhashtable.get("item_" + headerhashtable.get(Constants.SONo));
//                    if (!TextUtils.isEmpty(textString)) {
//                        ArrayList<HashMap<String, String>> textItemList = UtilConstants.convertToArrayListMap(textString);
//                        for (int j = 0; j < textItemList.size(); j++) {
//                            HashMap<String, String> subTextSingleItem = textItemList.get(j);
                            newTextEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpace(OfflineManager.offlineStore) + "" + Constants.SOS_SO_TEXT_ENTITY);

                            newTextEntity.setResourcePath(Constants.SOTexts + "(1)", Constants.SOTexts + "(1)");

                            try {
                                store.allocateProperties(newTextEntity, ODataStore.PropMode.All);
                            } catch (ODataException e) {
                                e.printStackTrace();
                            }
                            newTextEntity.getProperties().put(Constants.SONo,
                                    new ODataPropertyDefaultImpl(Constants.SONo, headerhashtable.get(Constants.SONo)));
                            newTextEntity.getProperties().put(Constants.Text,
                                    new ODataPropertyDefaultImpl(Constants.Text, headerhashtable.get(Constants.Remarks)));
                            newTextEntity.getProperties().put(Constants.TextID,
                                    new ODataPropertyDefaultImpl(Constants.TextID, "0001"));
                            tempTextArray.add(0, newTextEntity);

                            ODataEntitySetDefaultImpl itemTextEntity = new ODataEntitySetDefaultImpl(tempTextArray.size(), null, null);
                            for (ODataEntity entity : tempTextArray) {
                                itemTextEntity.getEntities().add(entity);
                            }
                            itemTextEntity.setResourcePath(Constants.SOTexts);
                            ODataNavigationProperty navTextProp = newHeaderEntity.getNavigationProperty(Constants.SOTexts);
                            navTextProp.setNavigationContent(itemTextEntity);
                            newHeaderEntity.setNavigationProperty(Constants.SOTexts, navTextProp);
                }
                for (int i = 0; i < itemhashtable.size(); i++) {

                    HashMap<String, String> singleRow = itemhashtable.get(i);
                    newItemEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpace(OfflineManager.offlineStore)+""+Constants.SOS_ITEM_DETAILS_ENTITY);

                    newItemEntity.setResourcePath(Constants.SOItemDetails + "(" + ((i + 1) * 10) + ")", Constants.SOItemDetails + "(" + ((i + 1) * 10) + ")");

                    try {
                        store.allocateProperties(newItemEntity, ODataStore.PropMode.All);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }
                    store.allocateNavigationProperties(newItemEntity);
                    if(singleRow.get(Constants.SONo)!=null) {
                        newItemEntity.getProperties().put(Constants.SONo,
                                new ODataPropertyDefaultImpl(Constants.SONo, singleRow.get(Constants.SONo)));
                    }

                    newItemEntity.getProperties().put(Constants.ItemNo,
                            new ODataPropertyDefaultImpl(Constants.ItemNo, singleRow.get(Constants.ItemNo)));
                    newItemEntity.getProperties().put(Constants.Material,
                            new ODataPropertyDefaultImpl(Constants.Material, singleRow.get(Constants.Material)));
                    newItemEntity.getProperties().put(Constants.Plant,
                            new ODataPropertyDefaultImpl(Constants.Plant, singleRow.get(Constants.Plant)));
//                    newItemEntity.getProperties().put(Constants.StorLoc,
//                            new ODataPropertyDefaultImpl(Constants.StorLoc, singleRow.get(Constants.StorLoc)));
                    newItemEntity.getProperties().put(Constants.UOM,
                            new ODataPropertyDefaultImpl(Constants.UOM, singleRow.get(Constants.UOM)));
                    newItemEntity.getProperties().put(Constants.Quantity,
                            new ODataPropertyDefaultImpl(Constants.Quantity, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.Quantity)))));
                    newItemEntity.getProperties().put(Constants.Currency,
                            new ODataPropertyDefaultImpl(Constants.Currency, singleRow.get(Constants.Currency)));

                    newItemEntity.getProperties().put(Constants.UnitPrice,
                            new ODataPropertyDefaultImpl(Constants.UnitPrice, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.UnitPrice).equalsIgnoreCase("")?"0":singleRow.get(Constants.UnitPrice)))));
                    newItemEntity.getProperties().put(Constants.NetAmount,
                            new ODataPropertyDefaultImpl(Constants.NetAmount, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.NetAmount).equalsIgnoreCase("")?"0":singleRow.get(Constants.NetAmount)))));
                    newItemEntity.getProperties().put(Constants.GrossAmount,
                            new ODataPropertyDefaultImpl(Constants.GrossAmount, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.GrossAmount).equalsIgnoreCase("")?"0":singleRow.get(Constants.GrossAmount)))));
                    newItemEntity.getProperties().put(Constants.Freight,
                            new ODataPropertyDefaultImpl(Constants.Freight, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.Freight).equalsIgnoreCase("")?"0":singleRow.get(Constants.Freight)))));
                    newItemEntity.getProperties().put(Constants.Tax,
                            new ODataPropertyDefaultImpl(Constants.Tax, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.Tax).equalsIgnoreCase("")?"0":singleRow.get(Constants.Tax)))));
                    newItemEntity.getProperties().put(Constants.Discount,
                            new ODataPropertyDefaultImpl(Constants.Discount, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.Discount).equalsIgnoreCase("")?"0":singleRow.get(Constants.Discount)))));

//                    newItemEntity.getProperties().put(Constants.MatFrgtGrp,
//                            new ODataPropertyDefaultImpl(Constants.MatFrgtGrp, singleRow.get(Constants.MatFrgtGrp)));
//
//                    newItemEntity.getProperties().put(Constants.StorLoc,
//                            new ODataPropertyDefaultImpl(Constants.StorLoc, singleRow.get(Constants.StorLoc)));


                    *//*simulate empty condition*//*
//                    if(isSimulateType==1){
////                        newSubItemEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpace(OfflineManager.offlineStore)+""+Constants.SOS_ITEM_CONDITION_ITEM_DETAILS_ENTITY);
////
////                        newSubItemEntity.setResourcePath(Constants.SOConditionItemDetails + "(" + ((0 + 1) * 10) + ")", Constants.SOConditionItemDetails + "(" + ((0 + 1) * 10) + ")");
////
////                        try {
////                            store.allocateProperties(newSubItemEntity, ODataStore.PropMode.All);
////                        } catch (ODataException e) {
////                            e.printStackTrace();
////                        }
////                        tempSubArray.add(0, newSubItemEntity);
////
////                        ODataEntitySetDefaultImpl itemSubEntity = new ODataEntitySetDefaultImpl(tempSubArray.size(), null, null);
////                        for (ODataEntity entity : tempSubArray) {
////                            itemSubEntity.getEntities().add(entity);
////                        }
////                        itemSubEntity.setResourcePath(Constants.SOConditionItemDetails);
////                        ODataNavigationProperty navSubProp = newItemEntity.getNavigationProperty(Constants.SOConditionItemDetails);
////                        navSubProp.setNavigationContent(itemSubEntity);
////                        newItemEntity.setNavigationProperty(Constants.SOConditionItemDetails, navSubProp);
//
//
//                    }else {
//                        String itemsString = singleRow.get("item_" + singleRow.get(Constants.Material));
//                        if (!TextUtils.isEmpty(itemsString)) {
//                            ArrayList<HashMap<String, String>> subItemList = UtilConstants.convertToArrayListMap(itemsString);
//                            for (int j = 0; j < subItemList.size(); j++) {
//                                HashMap<String, String> subSingleItem = subItemList.get(j);
//
//                                newSubItemEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpace(OfflineManager.offlineStore)+""+Constants.SOS_ITEM_SCHEDULE_ENTITY);
//
//                                newSubItemEntity.setResourcePath(Constants.SOItemSchedules + "(" + ((i + 1) * 10) + ")", Constants.SOItemSchedules + "(" + ((i + 1) * 10) + ")");
//
//                                try {
//                                    store.allocateProperties(newSubItemEntity, ODataStore.PropMode.All);
//                                } catch (ODataException e) {
//                                    e.printStackTrace();
//                                }
//
//                                if (singleRow.get(Constants.SONo) != null) {
//                                    newSubItemEntity.getProperties().put(Constants.SONo,
//                                            new ODataPropertyDefaultImpl(Constants.SONo, singleRow.get(Constants.SONo)));
//                                }
//
//                                newSubItemEntity.getProperties().put(Constants.DelSchLineNo,
//                                        new ODataPropertyDefaultImpl(Constants.DelSchLineNo, subSingleItem.get(Constants.DelSchLineNo)));
//                                newSubItemEntity.getProperties().put(Constants.ItemNo,
//                                        new ODataPropertyDefaultImpl(Constants.ItemNo, subSingleItem.get(Constants.ItemNo)));
//                                newSubItemEntity.getProperties().put(Constants.DeliveryDate,
//                                        new ODataPropertyDefaultImpl(Constants.DeliveryDate, Constants.convertDateFormat(subSingleItem.get(Constants.DeliveryDate))));
//                                newSubItemEntity.getProperties().put(Constants.MaterialNo,
//                                        new ODataPropertyDefaultImpl(Constants.MaterialNo, subSingleItem.get(Constants.MaterialNo)));
//                                newSubItemEntity.getProperties().put(Constants.OrderQty,
//                                        new ODataPropertyDefaultImpl(Constants.OrderQty, BigDecimal.valueOf(Double.parseDouble(subSingleItem.get(Constants.OrderQty)))));
//
//                                newSubItemEntity.getProperties().put(Constants.ConfirmedQty,
//                                        new ODataPropertyDefaultImpl(Constants.ConfirmedQty, BigDecimal.valueOf(Double.parseDouble(subSingleItem.get(Constants.ConfirmedQty)))));
//
//                                newSubItemEntity.getProperties().put(Constants.RequiredQty,
//                                        new ODataPropertyDefaultImpl(Constants.RequiredQty, BigDecimal.valueOf(Double.parseDouble(subSingleItem.get(Constants.RequiredQty)))));
//                                newSubItemEntity.getProperties().put(Constants.UOM,
//                                        new ODataPropertyDefaultImpl(Constants.UOM, subSingleItem.get(Constants.UOM)));
////                            newSubItemEntity.getProperties().put(Constants.ScheduleLineCatID,
////                                    new ODataPropertyDefaultImpl(Constants.ScheduleLineCatID, subSingleItem.get(Constants.ScheduleLineCatID)));
//
//                                tempSubArray.add(j, newSubItemEntity);
//                            }
//                            ODataEntitySetDefaultImpl itemSubEntity = new ODataEntitySetDefaultImpl(tempSubArray.size(), null, null);
//                            for (ODataEntity entity : tempSubArray) {
//                                itemSubEntity.getEntities().add(entity);
//                            }
//                            itemSubEntity.setResourcePath(Constants.SOItemSchedules);
//                            ODataNavigationProperty navSubProp = newItemEntity.getNavigationProperty(Constants.SOItemSchedules);
//                            navSubProp.setNavigationContent(itemSubEntity);
//                            newItemEntity.setNavigationProperty(Constants.SOItemSchedules, navSubProp);
//                        }
//                    }
                    tempArray.add(i, newItemEntity);
                }


                ODataEntitySetDefaultImpl itemEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itemEntity.getEntities().add(entity);
                }

                itemEntity.setResourcePath(Constants.SOItemDetails);
                ODataNavigationProperty navProp = newHeaderEntity.getNavigationProperty(Constants.SOItemDetails);
                navProp.setNavigationContent(itemEntity);
                newHeaderEntity.setNavigationProperty(Constants.SOItemDetails, navProp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return newHeaderEntity;
    }*/
    /*private static ODataEntity createSoSimulation(OnlineODataStore store, Hashtable<String, String> headerhashtable, ArrayList<HashMap<String, String>> itemhashtable, int isSimulateType) {
        ODataEntity newHeaderEntity = null;
        ODataEntity newItemEntity = null;
        ODataEntity newPartnerFunEntity = null;
        ODataEntity newConditionEntity = null;
        ODataEntity newSubItemEntity = null;
        ODataEntity newTextEntity = null;
        ODataProperty property;
        ODataPropMap properties;

        ArrayList<ODataEntity> tempArray = new ArrayList();
        ArrayList<ODataEntity> tempPartFunArray = new ArrayList();
        ArrayList<ODataEntity> tempSubArray = new ArrayList();
        ArrayList<ODataEntity> tempTextArray = new ArrayList();
        ArrayList<ODataEntity> tempConArray = new ArrayList();
        ArrayList<ODataEntity> tempSerialArray;

        try {


            if (headerhashtable != null) {
                //Use default implementation to create a new travel agency entity type
                newHeaderEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.SOS_ENTITY);

                String resourcePath = Constants.getEditResourcePath(Constants.SOs, Constants.SOs);
                if (isSimulateType == 3) {
                    newHeaderEntity.setResourcePath(Constants.SOs + "('" + headerhashtable.get(Constants.SONo) + "')", Constants.SOs + "('" + headerhashtable.get(Constants.SONo) + "')");
                } else {
                    newHeaderEntity.setResourcePath(Constants.SOs, Constants.SOs);
                }

                //If available, it will populates those properties of an OData Entity
                //which are defined by the allocation mode
                try {
                    store.allocateProperties(newHeaderEntity, ODataStore.PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }
                //If available, it populates the navigation properties of an OData Entity
                store.allocateNavigationProperties(newHeaderEntity);


                newHeaderEntity = getSoHeaderEntity(newHeaderEntity, headerhashtable);

                *//*  simulate create*//*
               *//* if(isSimulateType==1) {
                    newConditionEntity = new ODataEntityDefaultImpl(Constants.getNameSpace(OfflineManager.offlineStore) + "" + Constants.SOS_ITEM_CONDITION_ENTITY);

                    newConditionEntity.setResourcePath(Constants.SOConditions + "(1)", Constants.SOConditions + "(1)");

                    try {
                        store.allocateProperties(newConditionEntity, ODataStore.PropMode.All);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }
                    tempConArray.add(0, newConditionEntity);

                    ODataEntitySetDefaultImpl itemSubEntity = new ODataEntitySetDefaultImpl(tempConArray.size(), null, null);
                    for (ODataEntity entity : tempConArray) {
                        itemSubEntity.getEntities().add(entity);
                    }
                    itemSubEntity.setResourcePath(Constants.SOConditions);
                    ODataNavigationProperty navSubProp = newHeaderEntity.getNavigationProperty(Constants.SOConditions);
                    navSubProp.setNavigationContent(itemSubEntity);
                    newHeaderEntity.setNavigationProperty(Constants.SOConditions, navSubProp);
                }*//*
                if (headerhashtable.get(Constants.ONETIMESHIPTO) != null && headerhashtable.get(Constants.ONETIMESHIPTO).equalsIgnoreCase(Constants.X)) {
                    newPartnerFunEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.SOS_PARTNER_FUNCTIONS_ENTITY);

                    newPartnerFunEntity.setResourcePath(Constants.SOPartnerFunctions, Constants.SOPartnerFunctions);

                    try {
                        store.allocateProperties(newPartnerFunEntity, ODataStore.PropMode.All);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }
                    newPartnerFunEntity = getPartnerFunctionEntity(newPartnerFunEntity, headerhashtable);
                    tempPartFunArray.add(0, newPartnerFunEntity);

                    ODataEntitySetDefaultImpl itemSubEntity = new ODataEntitySetDefaultImpl(tempPartFunArray.size(), null, null);
                    for (ODataEntity entity : tempPartFunArray) {
                        itemSubEntity.getEntities().add(entity);
                    }
                    itemSubEntity.setResourcePath(Constants.SOPartnerFunctions);
                    ODataNavigationProperty navSubProp = newHeaderEntity.getNavigationProperty(Constants.SOPartnerFunctions);
                    navSubProp.setNavigationContent(itemSubEntity);
                    newHeaderEntity.setNavigationProperty(Constants.SOPartnerFunctions, navSubProp);
                }
                if (isSimulateType == 2 || isSimulateType == 3) {
                    String textString = headerhashtable.get("item_" + headerhashtable.get(Constants.SONo));
                    if (!TextUtils.isEmpty(textString)) {
                        ArrayList<HashMap<String, String>> textItemList = UtilConstants.convertToArrayListMap(textString);
                        for (int j = 0; j < textItemList.size(); j++) {
                            HashMap<String, String> subTextSingleItem = textItemList.get(j);
                            newTextEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.SOS_SO_TEXT_ENTITY);

                            newTextEntity.setResourcePath(Constants.SOTexts + "(1)", Constants.SOTexts + "(1)");

                            try {
                                store.allocateProperties(newTextEntity, ODataStore.PropMode.All);
                            } catch (ODataException e) {
                                e.printStackTrace();
                            }
                            newTextEntity = getTextEntity(newTextEntity, subTextSingleItem);

                            tempTextArray.add(0, newTextEntity);

                            ODataEntitySetDefaultImpl itemTextEntity = new ODataEntitySetDefaultImpl(tempTextArray.size(), null, null);
                            for (ODataEntity entity : tempTextArray) {
                                itemTextEntity.getEntities().add(entity);
                            }
                            itemTextEntity.setResourcePath(Constants.SOTexts);
                            ODataNavigationProperty navTextProp = newHeaderEntity.getNavigationProperty(Constants.SOTexts);
                            navTextProp.setNavigationContent(itemTextEntity);
                            newHeaderEntity.setNavigationProperty(Constants.SOTexts, navTextProp);
                        }
                    }
                }
                for (int i = 0; i < itemhashtable.size(); i++) {

                    HashMap<String, String> singleRow = itemhashtable.get(i);
                    newItemEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.SOS_ITEM_DETAILS_ENTITY);

                    newItemEntity.setResourcePath(Constants.SOItemDetails + "(" + ((i + 1) * 10) + ")", Constants.SOItemDetails + "(" + ((i + 1) * 10) + ")");

                    try {
                        store.allocateProperties(newItemEntity, ODataStore.PropMode.All);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }
                    store.allocateNavigationProperties(newItemEntity);
                    newItemEntity = getSoItemEntity(newItemEntity, singleRow);

                    *//*simulate empty condition*//*
                    if (isSimulateType == 1) {
                        newSubItemEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.SOS_ITEM_CONDITION_ITEM_DETAILS_ENTITY);

                        newSubItemEntity.setResourcePath(Constants.SOConditionItemDetails + "(" + ((0 + 1) * 10) + ")", Constants.SOConditionItemDetails + "(" + ((0 + 1) * 10) + ")");

                        try {
                            store.allocateProperties(newSubItemEntity, ODataStore.PropMode.All);
                        } catch (ODataException e) {
                            e.printStackTrace();
                        }
                        tempSubArray.add(0, newSubItemEntity);

                        ODataEntitySetDefaultImpl itemSubEntity = new ODataEntitySetDefaultImpl(tempSubArray.size(), null, null);
                        for (ODataEntity entity : tempSubArray) {
                            itemSubEntity.getEntities().add(entity);
                        }
                        itemSubEntity.setResourcePath(Constants.SOConditionItemDetails);
                        ODataNavigationProperty navSubProp = newItemEntity.getNavigationProperty(Constants.SOConditionItemDetails);
                        navSubProp.setNavigationContent(itemSubEntity);
                        newItemEntity.setNavigationProperty(Constants.SOConditionItemDetails, navSubProp);


                    } else {
                        String itemsString = singleRow.get("item_" + singleRow.get(Constants.Material));
                        if (!TextUtils.isEmpty(itemsString)) {
                            ArrayList<HashMap<String, String>> subItemList = UtilConstants.convertToArrayListMap(itemsString);
                            for (int j = 0; j < subItemList.size(); j++) {
                                HashMap<String, String> subSingleItem = subItemList.get(j);

                                newSubItemEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.SOS_ITEM_SCHEDULE_ENTITY);

                                newSubItemEntity.setResourcePath(Constants.SOItemSchedules + "(" + ((j + 1) * 10) + ")", Constants.SOItemSchedules + "(" + ((j + 1) * 10) + ")");

                                try {
                                    store.allocateProperties(newSubItemEntity, ODataStore.PropMode.All);
                                } catch (ODataException e) {
                                    e.printStackTrace();
                                }

                                if (singleRow.get(Constants.SONo) != null) {
                                    newSubItemEntity.getProperties().put(Constants.SONo,
                                            new ODataPropertyDefaultImpl(Constants.SONo, singleRow.get(Constants.SONo)));
                                }
                                newSubItemEntity = getSOItemScheduleEntity(newSubItemEntity, subSingleItem);

                                tempSubArray.add(j, newSubItemEntity);
                            }
                            ODataEntitySetDefaultImpl itemSubEntity = new ODataEntitySetDefaultImpl(tempSubArray.size(), null, null);
                            for (ODataEntity entity : tempSubArray) {
                                itemSubEntity.getEntities().add(entity);
                            }
                            itemSubEntity.setResourcePath(Constants.SOItemSchedules);
                            ODataNavigationProperty navSubProp = newItemEntity.getNavigationProperty(Constants.SOItemSchedules);
                            navSubProp.setNavigationContent(itemSubEntity);
                            newItemEntity.setNavigationProperty(Constants.SOItemSchedules, navSubProp);
                        }
                    }
                    tempArray.add(i, newItemEntity);
                }


                ODataEntitySetDefaultImpl itemEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itemEntity.getEntities().add(entity);
                }

                itemEntity.setResourcePath(Constants.SOItemDetails);
                ODataNavigationProperty navProp = newHeaderEntity.getNavigationProperty(Constants.SOItemDetails);
                navProp.setNavigationContent(itemEntity);
                newHeaderEntity.setNavigationProperty(Constants.SOItemDetails, navProp);
            }

        } catch (Exception e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog(e.getMessage());
        }
        return newHeaderEntity;
    }*/

    public static void createSOsEntity(Hashtable<String, String> table, ArrayList<HashMap<String, String>> itemtable, UIListener uiListener) throws com.arteriatech.mutils.common.OnlineODataStoreException {
        /*OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                //Creates the entity payload
                ODataEntity soCreateEntity = createSoSimulation(store, table, itemtable, 2);

                OnlineRequestListener collectionListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);

                String ssoGUID32 = table.get(Constants.ReferenceNo);

                String soCreatedOn = table.get(Constants.CreatedOn);
                String soCreatedAt = table.get(Constants.CreatedAt);

                String mStrDateTime = UtilConstants.getReArrangeDateFormat(soCreatedOn) + Constants.T + UtilConstants.convertTimeOnly(soCreatedAt);

                Map<String, String> createHeaders = new HashMap<String, String>();
                createHeaders.put(Constants.RequestID, ssoGUID32);
                createHeaders.put(Constants.RepeatabilityCreation, mStrDateTime);

                ODataRequestParamSingle collectionReq = new ODataRequestParamSingleDefaultImpl();
                collectionReq.setMode(ODataRequestParamSingle.Mode.Create);
                collectionReq.setResourcePath(soCreateEntity.getResourcePath());
                collectionReq.setPayload(soCreateEntity);
                collectionReq.getCustomHeaders().putAll(createHeaders);

                store.scheduleRequest(collectionReq, collectionListener);

            } catch (Exception e) {
                throw new com.arteriatech.mutils.common.OnlineODataStoreException(e);
            }
        }
*/
    }

    /*create daily expense*/
    public static void createDailyExpense(Hashtable<String, String> table, ArrayList<HashMap<String, String>> itemtable, UIListener uiListener) throws OnlineODataStoreException {
        /*OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                ODataEntity soCreateEntity = createDailyExpenseCreateEntity(table, itemtable, store);

                OnlineRequestListener collectionListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);

                ODataRequestParamSingle collectionReq = new ODataRequestParamSingleDefaultImpl();
                collectionReq.setMode(ODataRequestParamSingle.Mode.Create);
                collectionReq.setResourcePath(soCreateEntity.getResourcePath());
                collectionReq.setPayload(soCreateEntity);
//                collectionReq.getCustomHeaders().putAll(createHeaders);

                store.scheduleRequest(collectionReq, collectionListener);

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }
*/
    }

    /*entity for expense */
   /* public static ODataEntity createDailyExpenseCreateEntity(Hashtable<String, String> hashtable, ArrayList<HashMap<String, String>> itemhashtable, OnlineODataStore store) throws ODataParserException {
        ODataEntity newHeaderEntity = null;
        ODataEntity newItemEntity = null;
        ODataEntity newItemImageEntity = null;
        ArrayList<ODataEntity> tempArray = new ArrayList();
        ArrayList<ODataEntity> docmentArray = new ArrayList();
        try {
            if (hashtable != null) {
                newHeaderEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.ExpenseEntity);

                newHeaderEntity.setResourcePath(Constants.Expenses, Constants.Expenses);

                try {
                    store.allocateProperties(newHeaderEntity, PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }
                //If available, it populates the navigation properties of an OData Entity
                store.allocateNavigationProperties(newHeaderEntity);

                newHeaderEntity.getProperties().put(Constants.ExpenseGUID,
                        new ODataPropertyDefaultImpl(Constants.ExpenseGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.ExpenseGUID))));
//                newHeaderEntity.getProperties().put(Constants.OrderNo,
//                        new ODataPropertyDefaultImpl(Constants.OrderNo, hashtable.get(Constants.OrderNo)));
                newHeaderEntity.getProperties().put(Constants.ExpenseNo,
                        new ODataPropertyDefaultImpl(Constants.ExpenseNo, hashtable.get(Constants.ExpenseNo)));
                newHeaderEntity.getProperties().put(Constants.FiscalYear,
                        new ODataPropertyDefaultImpl(Constants.FiscalYear, hashtable.get(Constants.FiscalYear)));
                newHeaderEntity.getProperties().put(Constants.LoginID,
                        new ODataPropertyDefaultImpl(Constants.LoginID, hashtable.get(Constants.LoginID)));
               *//* newHeaderEntity.getProperties().put(Constants.OrderDate,
                        new ODataPropertyDefaultImpl(Constants.OrderDate, UtilConstants.convertDateFormat(hashtable.get(Constants.OrderDate))));*//*

                newHeaderEntity.getProperties().put(Constants.CPGUID,
                        new ODataPropertyDefaultImpl(Constants.CPGUID, hashtable.get(Constants.CPGUID)));
                newHeaderEntity.getProperties().put(Constants.CPNo,
                        new ODataPropertyDefaultImpl(Constants.CPNo, hashtable.get(Constants.CPNo)));
                newHeaderEntity.getProperties().put(Constants.CPName,
                        new ODataPropertyDefaultImpl(Constants.CPName, hashtable.get(Constants.CPName)));
                newHeaderEntity.getProperties().put(Constants.CPType,
                        new ODataPropertyDefaultImpl(Constants.CPType, hashtable.get(Constants.CPType)));
                newHeaderEntity.getProperties().put(Constants.CPTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.CPTypeDesc, hashtable.get(Constants.CPTypeDesc)));


                newHeaderEntity.getProperties().put(Constants.ExpenseType,
                        new ODataPropertyDefaultImpl(Constants.ExpenseType, hashtable.get(Constants.ExpenseType)));
                newHeaderEntity.getProperties().put(Constants.ExpenseTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.ExpenseTypeDesc, hashtable.get(Constants.ExpenseTypeDesc)));
                newHeaderEntity.getProperties().put(Constants.ExpenseDate,
                        new ODataPropertyDefaultImpl(Constants.ExpenseDate, UtilConstants.convertDateFormat(hashtable.get(Constants.ExpenseDate))));

                newHeaderEntity.getProperties().put(Constants.Status,
                        new ODataPropertyDefaultImpl(Constants.Status, hashtable.get(Constants.Status)));
                newHeaderEntity.getProperties().put(Constants.StatusDesc,
                        new ODataPropertyDefaultImpl(Constants.StatusDesc, hashtable.get(Constants.StatusDesc)));

                newHeaderEntity.getProperties().put(Constants.Amount,
                        new ODataPropertyDefaultImpl(Constants.Amount, BigDecimal.valueOf(Double.parseDouble(hashtable.get(Constants.Amount)))));
               *//* newHeaderEntity.getProperties().put(Constants.CreatedOn,
                        new ODataPropertyDefaultImpl(Constants.CreatedOn, hashtable.get(Constants.CreatedOn)));*//*
//                newHeaderEntity.getProperties().put(Constants.CreatedBy,
//                        new ODataPropertyDefaultImpl(Constants.CreatedBy, hashtable.get(Constants.CreatedBy)));


                newHeaderEntity.getProperties().put(Constants.Currency,
                        new ODataPropertyDefaultImpl(Constants.Currency, hashtable.get(Constants.Currency)));


                newHeaderEntity.getProperties().put(Constants.SPGUID,
                        new ODataPropertyDefaultImpl(Constants.SPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SPGUID))));
                newHeaderEntity.getProperties().put(Constants.SPNo,
                        new ODataPropertyDefaultImpl(Constants.SPNo, hashtable.get(Constants.SPNo)));
                newHeaderEntity.getProperties().put(Constants.SPName,
                        new ODataPropertyDefaultImpl(Constants.SPName, hashtable.get(Constants.SPName)));


                int incremntVal = 0;
                for (int incrementVal = 0; incrementVal < itemhashtable.size(); incrementVal++) {

                    HashMap<String, String> singleRow = itemhashtable.get(incrementVal);

                    incremntVal = incrementVal + 1;

                    newItemEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.ExpenseItemEntity);

                    newItemEntity.setResourcePath(Constants.ExpenseItemDetails + "(" + incremntVal + ")", Constants.ExpenseItemDetails + "(" + incremntVal + ")");
                    try {
                        store.allocateProperties(newItemEntity, PropMode.Keys);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }
                    *//*try {
                        store.allocateProperties(newItemEntity, PropMode.All);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }*//*
                    //If available, it populates the navigation properties of an OData Entity
                    store.allocateNavigationProperties(newItemEntity);

                    newItemEntity.getProperties().put(Constants.ExpenseItemGUID,
                            new ODataPropertyDefaultImpl(Constants.ExpenseItemGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.ExpenseItemGUID))));

                    newItemEntity.getProperties().put(Constants.ExpenseGUID,
                            new ODataPropertyDefaultImpl(Constants.ExpenseGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.ExpenseGUID))));

                    newItemEntity.getProperties().put(Constants.ExpeseItemNo,
                            new ODataPropertyDefaultImpl(Constants.ExpeseItemNo, singleRow.get(Constants.ExpeseItemNo)));


                    newItemEntity.getProperties().put(Constants.LoginID,
                            new ODataPropertyDefaultImpl(Constants.LoginID, singleRow.get(Constants.LoginID)));

                    newItemEntity.getProperties().put(Constants.ExpenseItemType,
                            new ODataPropertyDefaultImpl(Constants.ExpenseItemType, singleRow.get(Constants.ExpenseItemType)));
//
                    newItemEntity.getProperties().put(Constants.ExpenseItemTypeDesc,
                            new ODataPropertyDefaultImpl(Constants.ExpenseItemTypeDesc, singleRow.get(Constants.ExpenseItemTypeDesc)));
                    if (!singleRow.get(Constants.BeatGUID).equalsIgnoreCase("")) {
                        newItemEntity.getProperties().put(Constants.BeatGUID,
                                new ODataPropertyDefaultImpl(Constants.BeatGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.BeatGUID))));
                    }
                    newItemEntity.getProperties().put(Constants.Location,
                            new ODataPropertyDefaultImpl(Constants.Location, singleRow.get(Constants.Location)));
                    if (!singleRow.get(Constants.ConvenyanceMode).equals("")) {
                        newItemEntity.getProperties().put(Constants.ConvenyanceMode,
                                new ODataPropertyDefaultImpl(Constants.ConvenyanceMode, singleRow.get(Constants.ConvenyanceMode)));
                        newItemEntity.getProperties().put(Constants.ConvenyanceModeDs,
                                new ODataPropertyDefaultImpl(Constants.ConvenyanceModeDs, singleRow.get(Constants.ConvenyanceModeDs)));
                    }
                    if (!singleRow.get(Constants.BeatDistance).equals("")) {
                        newItemEntity.getProperties().put(Constants.BeatDistance,
                                new ODataPropertyDefaultImpl(Constants.BeatDistance, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.BeatDistance)))));
                    }
                    if (!singleRow.get(Constants.Amount).equalsIgnoreCase("")) {
                        newItemEntity.getProperties().put(Constants.Amount,
                                new ODataPropertyDefaultImpl(Constants.Amount, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.Amount)))));
                    }
                    newItemEntity.getProperties().put(Constants.UOM,
                            new ODataPropertyDefaultImpl(Constants.UOM, singleRow.get(Constants.UOM)));
                    newItemEntity.getProperties().put(Constants.Currency,
                            new ODataPropertyDefaultImpl(Constants.Currency, singleRow.get(Constants.Currency)));
                    newItemEntity.getProperties().put(Constants.Remarks,
                            new ODataPropertyDefaultImpl(Constants.Remarks, singleRow.get(Constants.Remarks)));


                    tempArray.add(incrementVal, newItemEntity);


                }

                ODataEntitySetDefaultImpl itemEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itemEntity.getEntities().add(entity);
                }
                itemEntity.setResourcePath(Constants.ExpenseItemDetails);

                ODataNavigationProperty navProp = newHeaderEntity.getNavigationProperty(Constants.ExpenseItemDetails);
                navProp.setNavigationContent(itemEntity);
                newHeaderEntity.setNavigationProperty(Constants.ExpenseItemDetails, navProp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newHeaderEntity;
    }*/

    /*private static ODataRequestChangeSet createRequestParameterBatchList(OnlineODataStore store, Hashtable<String, String> headerhashtable, ODataRequestChangeSet changeSetItem) throws ODataException {
        ODataRequestParamSingle batchItem = new ODataRequestParamSingleDefaultImpl();
        batchItem.setResourcePath(Constants.SOs + "(SONo='" + headerhashtable.get(Constants.SONo) + "')");
        batchItem.setMode(ODataRequestParamSingle.Mode.Update);
        batchItem.setContentID("1");
        ODataEntity oDataHeaderEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.SOS_ENTITY);
        oDataHeaderEntity.setResourcePath(Constants.SOs + "('" + headerhashtable.get(Constants.SONo) + "')", Constants.SOs + "('" + headerhashtable.get(Constants.SONo) + "')");

//        Map<String, String> createHeaders = new HashMap<String, String>();
//
//        createHeaders.put("accept", "application/atom+xml");
//
//        createHeaders.put("content-type", "application/atom+xml");
//
//
//        batchItem.setOptions(createHeaders);
        store.allocateProperties(oDataHeaderEntity, ODataStore.PropMode.Keys);
        store.allocateNavigationProperties(oDataHeaderEntity);
        oDataHeaderEntity = getSoHeaderEntity(oDataHeaderEntity, headerhashtable);
        batchItem.setPayload(oDataHeaderEntity);
        changeSetItem.add(batchItem);

        return changeSetItem;
    }*/

    /*private static ODataRequestChangeSet createRequestItemParameterBatchList(OnlineODataStore store, Hashtable<String, String> headerhashtable, ArrayList<HashMap<String, String>> itemhashtable, ODataRequestChangeSet changeSetItem, int type) throws ODataException {
        for (int i = 0; i < itemhashtable.size(); i++) {
            HashMap<String, String> singleRow = itemhashtable.get(i);

            ODataRequestParamSingle batchItem = new ODataRequestParamSingleDefaultImpl();
            batchItem.setResourcePath(Constants.SOItemDetails + "(SONo='" + headerhashtable.get(Constants.SONo) + "',ItemNo='" + singleRow.get(Constants.ItemNo) + "')");
            batchItem.setMode(ODataRequestParamSingle.Mode.Update);
            batchItem.setContentID("1");
            ODataEntity oDataHeaderEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.SOS_ITEM_DETAILS_ENTITY);
            oDataHeaderEntity.setResourcePath(Constants.SOItemDetails + "(" + ((i + 1) * 10) + ")", Constants.SOItemDetails + "(" + ((i + 1) * 10) + ")");

            store.allocateProperties(oDataHeaderEntity, ODataStore.PropMode.Keys);
            store.allocateNavigationProperties(oDataHeaderEntity);
            oDataHeaderEntity = getSoItemEntity(oDataHeaderEntity, singleRow);
            batchItem.setPayload(oDataHeaderEntity);
            changeSetItem.add(batchItem);
        }

        return changeSetItem;
    }*/

    public static void cancelSO(Hashtable<String, String> table, ArrayList<HashMap<String, String>> itemtable, UIListener uiListener) throws OnlineODataStoreException {
        /*OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();
        if (store != null) {
            try {
                ODataRequestParamBatch requestParamBatch = new ODataRequestParamBatchDefaultImpl();
                ODataRequestChangeSet changeSetItem = new ODataRequestChangeSetDefaultImpl();
                changeSetItem = createRequestParameterBatchList(store, table, changeSetItem);
                changeSetItem = createRequestItemParameterBatchList(store, table, itemtable, changeSetItem, 2);
//              OnlineRequestListener batchListener = new OnlineRequestListener(Operation.GetRequest.getValue(), uiListener);
                OnlineRequestListener batchListener = new OnlineRequestListener(Operation.Update.getValue(), uiListener);
                requestParamBatch.add(changeSetItem);
                try {
                    store.scheduleRequest(requestParamBatch, batchListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }*/
    }

    /*private static ODataEntity getSoHeaderEntity(ODataEntity newHeaderEntity, Hashtable<String, String> headerhashtable) {

        //Set the corresponding properties
        if (headerhashtable.get(Constants.SONo) != null) {
            newHeaderEntity.getProperties().put(Constants.SONo,
                    new ODataPropertyDefaultImpl(Constants.SONo, headerhashtable.get(Constants.SONo)));
        }

        newHeaderEntity.getProperties().put(Constants.OrderType,
                new ODataPropertyDefaultImpl(Constants.OrderType, headerhashtable.get(Constants.OrderType)));

        if (headerhashtable.get(Constants.OrderDate) != null && !headerhashtable.get(Constants.OrderDate).equalsIgnoreCase("")) {
            newHeaderEntity.getProperties().put(Constants.OrderDate,
                    new ODataPropertyDefaultImpl(Constants.OrderDate, UtilConstants.convertDateFormat(headerhashtable.get(Constants.OrderDate))));
        }

        newHeaderEntity.getProperties().put(Constants.CustomerNo,
                new ODataPropertyDefaultImpl(Constants.CustomerNo, headerhashtable.get(Constants.CustomerNo)));
        newHeaderEntity.getProperties().put(Constants.CustomerPO,
                new ODataPropertyDefaultImpl(Constants.CustomerPO, headerhashtable.get(Constants.CustomerPO)));
        if (headerhashtable.get(Constants.CustomerPODate) != null && !headerhashtable.get(Constants.CustomerPODate).equalsIgnoreCase("")) {
            newHeaderEntity.getProperties().put(Constants.CustomerPODate,
                    new ODataPropertyDefaultImpl(Constants.CustomerPODate, UtilConstants.convertDateFormat(headerhashtable.get(Constants.CustomerPODate))));
        }
//         newHeaderEntity.getProperties().put(Constants.CustomerPODate,
//                new ODataPropertyDefaultImpl(Constants.CustomerPODate, headerhashtable.get(Constants.CustomerPODate)));
        newHeaderEntity.getProperties().put(Constants.ShippingTypeID,
                new ODataPropertyDefaultImpl(Constants.ShippingTypeID, headerhashtable.get(Constants.ShippingTypeID)));
        newHeaderEntity.getProperties().put(Constants.MeansOfTranstyp,
                new ODataPropertyDefaultImpl(Constants.MeansOfTranstyp, headerhashtable.get(Constants.MeansOfTranstyp)));
        newHeaderEntity.getProperties().put(Constants.ShipToParty,
                new ODataPropertyDefaultImpl(Constants.ShipToParty, headerhashtable.get(Constants.ShipToParty)));
        newHeaderEntity.getProperties().put(Constants.SalesArea,
                new ODataPropertyDefaultImpl(Constants.SalesArea, headerhashtable.get(Constants.SalesArea)));
        newHeaderEntity.getProperties().put(Constants.SalesOffice,
                new ODataPropertyDefaultImpl(Constants.SalesOffice, headerhashtable.get(Constants.SalesOffice)));
        newHeaderEntity.getProperties().put(Constants.SalesGroup,
                new ODataPropertyDefaultImpl(Constants.SalesGroup, headerhashtable.get(Constants.SalesGroup)));
        newHeaderEntity.getProperties().put(Constants.Plant,
                new ODataPropertyDefaultImpl(Constants.Plant, headerhashtable.get(Constants.Plant)));

//        newHeaderEntity.getProperties().put(Constants.TransporterID,
//                new ODataPropertyDefaultImpl(Constants.TransporterID, headerhashtable.get(Constants.TransporterID)));
//        newHeaderEntity.getProperties().put(Constants.TransporterName,
//                new ODataPropertyDefaultImpl(Constants.TransporterName, headerhashtable.get(Constants.TransporterName)));

        newHeaderEntity.getProperties().put(Constants.Incoterm1,
                new ODataPropertyDefaultImpl(Constants.Incoterm1, headerhashtable.get(Constants.Incoterm1)));
//        if (headerhashtable.get(Constants.UnloadingPoint) != null) {
//            newHeaderEntity.getProperties().put(Constants.UnloadingPoint,
//                    new ODataPropertyDefaultImpl(Constants.UnloadingPoint, headerhashtable.get(Constants.UnloadingPoint)));
//        }
//        if (headerhashtable.get(Constants.ReceivingPoint) != null) {
//            newHeaderEntity.getProperties().put(Constants.ReceivingPoint,
//                    new ODataPropertyDefaultImpl(Constants.ReceivingPoint, headerhashtable.get(Constants.ReceivingPoint)));
//        }
        newHeaderEntity.getProperties().put(Constants.Incoterm2,
                new ODataPropertyDefaultImpl(Constants.Incoterm2, headerhashtable.get(Constants.Incoterm2)));

        newHeaderEntity.getProperties().put(Constants.Payterm,
                new ODataPropertyDefaultImpl(Constants.Payterm, headerhashtable.get(Constants.Payterm)));
        newHeaderEntity.getProperties().put(Constants.Currency,
                new ODataPropertyDefaultImpl(Constants.Currency, headerhashtable.get(Constants.Currency)));
        if (headerhashtable.get(Constants.NetPrice) != null && !TextUtils.isEmpty(headerhashtable.get(Constants.NetPrice))) {
            newHeaderEntity.getProperties().put(Constants.NetPrice,
                    new ODataPropertyDefaultImpl(Constants.NetPrice, BigDecimal.valueOf(Double.parseDouble(headerhashtable.get(Constants.NetPrice)))));
        }
        if (headerhashtable.get(Constants.TotalAmount) != null && !TextUtils.isEmpty(headerhashtable.get(Constants.TotalAmount))) {
            newHeaderEntity.getProperties().put(Constants.TotalAmount,
                    new ODataPropertyDefaultImpl(Constants.TotalAmount, BigDecimal.valueOf(Double.parseDouble(headerhashtable.get(Constants.TotalAmount)))));
        }
        if (headerhashtable.get(Constants.TaxAmount) != null && !TextUtils.isEmpty(headerhashtable.get(Constants.TaxAmount))) {
            newHeaderEntity.getProperties().put(Constants.TaxAmount,
                    new ODataPropertyDefaultImpl(Constants.TaxAmount, BigDecimal.valueOf(Double.parseDouble(headerhashtable.get(Constants.TaxAmount)))));
        }
        if (headerhashtable.get(Constants.Freight) != null && !TextUtils.isEmpty(headerhashtable.get(Constants.Freight))) {
            newHeaderEntity.getProperties().put(Constants.Freight,
                    new ODataPropertyDefaultImpl(Constants.Freight, BigDecimal.valueOf(Double.parseDouble(headerhashtable.get(Constants.Freight)))));
        }
        if (headerhashtable.get(Constants.Discount) != null && !TextUtils.isEmpty(headerhashtable.get(Constants.Discount))) {
            newHeaderEntity.getProperties().put(Constants.Discount,
                    new ODataPropertyDefaultImpl(Constants.Discount, BigDecimal.valueOf(Double.parseDouble(headerhashtable.get(Constants.Discount)))));
        }
        if (headerhashtable.get(Constants.ReferenceNo) != null && !TextUtils.isEmpty(headerhashtable.get(Constants.ReferenceNo))) {
            newHeaderEntity.getProperties().put(Constants.ReferenceNo,
                    new ODataPropertyDefaultImpl(Constants.ReferenceNo, headerhashtable.get(Constants.ReferenceNo)));
        }
        if (headerhashtable.get(Constants.Testrun) != null) {
            newHeaderEntity.getProperties().put(Constants.Testrun,
                    new ODataPropertyDefaultImpl(Constants.Testrun, headerhashtable.get(Constants.Testrun)));
        }
        return newHeaderEntity;
    }

    private static ODataEntity getSoItemEntity(ODataEntity newItemEntity, HashMap<String, String> singleRow) {
        if (singleRow.get(Constants.SONo) != null) {
            newItemEntity.getProperties().put(Constants.SONo,
                    new ODataPropertyDefaultImpl(Constants.SONo, singleRow.get(Constants.SONo)));
        }

        newItemEntity.getProperties().put(Constants.ItemNo,
                new ODataPropertyDefaultImpl(Constants.ItemNo, singleRow.get(Constants.ItemNo)));
        newItemEntity.getProperties().put(Constants.HighLevellItemNo,
                new ODataPropertyDefaultImpl(Constants.HighLevellItemNo, singleRow.get(Constants.HighLevellItemNo)));
        newItemEntity.getProperties().put(Constants.ItemFlag,
                new ODataPropertyDefaultImpl(Constants.ItemFlag, singleRow.get(Constants.ItemFlag)));
        newItemEntity.getProperties().put(Constants.ItemCategory,
                new ODataPropertyDefaultImpl(Constants.ItemCategory, singleRow.get(Constants.ItemCategory)));
        newItemEntity.getProperties().put(Constants.Material,
                new ODataPropertyDefaultImpl(Constants.Material, singleRow.get(Constants.Material)));
        newItemEntity.getProperties().put(Constants.Plant,
                new ODataPropertyDefaultImpl(Constants.Plant, singleRow.get(Constants.Plant)));
//        newItemEntity.getProperties().put(Constants.StorLoc,
//                new ODataPropertyDefaultImpl(Constants.StorLoc, singleRow.get(Constants.StorLoc)));
        newItemEntity.getProperties().put(Constants.UOM,
                new ODataPropertyDefaultImpl(Constants.UOM, singleRow.get(Constants.UOM)));
        newItemEntity.getProperties().put(Constants.Quantity,
                new ODataPropertyDefaultImpl(Constants.Quantity, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.Quantity)))));
        newItemEntity.getProperties().put(Constants.Currency,
                new ODataPropertyDefaultImpl(Constants.Currency, singleRow.get(Constants.Currency)));

        if (singleRow.get(Constants.RejReason) != null) {
            if (!TextUtils.isEmpty(singleRow.get(Constants.RejReason))) {
                newItemEntity.getProperties().put(Constants.RejReason,
                        new ODataPropertyDefaultImpl(Constants.RejReason, singleRow.get(Constants.RejReason)));
            }
        }
        if (singleRow.get(Constants.RejReasonDesc) != null) {
            if (!TextUtils.isEmpty(singleRow.get(Constants.RejReasonDesc))) {
                newItemEntity.getProperties().put(Constants.RejReasonDesc,
                        new ODataPropertyDefaultImpl(Constants.RejReasonDesc, singleRow.get(Constants.RejReasonDesc)));
            }
        }
        newItemEntity.getProperties().put(Constants.UnitPrice,
                new ODataPropertyDefaultImpl(Constants.UnitPrice, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.UnitPrice).equalsIgnoreCase("") ? "0" : singleRow.get(Constants.UnitPrice)))));
        newItemEntity.getProperties().put(Constants.NetAmount,
                new ODataPropertyDefaultImpl(Constants.NetAmount, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.NetAmount).equalsIgnoreCase("") ? "0" : singleRow.get(Constants.NetAmount)))));
        newItemEntity.getProperties().put(Constants.GrossAmount,
                new ODataPropertyDefaultImpl(Constants.GrossAmount, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.GrossAmount).equalsIgnoreCase("") ? "0" : singleRow.get(Constants.GrossAmount)))));
        newItemEntity.getProperties().put(Constants.Freight,
                new ODataPropertyDefaultImpl(Constants.Freight, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.Freight).equalsIgnoreCase("") ? "0" : singleRow.get(Constants.Freight)))));
        newItemEntity.getProperties().put(Constants.Tax,
                new ODataPropertyDefaultImpl(Constants.Tax, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.Tax).equalsIgnoreCase("") ? "0" : singleRow.get(Constants.Tax)))));
        newItemEntity.getProperties().put(Constants.Discount,
                new ODataPropertyDefaultImpl(Constants.Discount, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.Discount).equalsIgnoreCase("") ? "0" : singleRow.get(Constants.Discount)))));
        return newItemEntity;
    }

*/
    /*get SO list*/
    public static ArrayList<SOListBean> getSOList(String odataQry, ArrayList<SOListBean> soListBeanArrayList) throws com.arteriatech.mutils.common.OnlineODataStoreException {
        /*OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();
        SOListBean soListBean;
        if (store != null) {
            try {
                ODataRequestParamBatch requestParamBatch = new ODataRequestParamBatchDefaultImpl();
                ODataRequestParamSingle batchItem = new ODataRequestParamSingleDefaultImpl();
                batchItem.setResourcePath(odataQry);
                batchItem.setMode(ODataRequestParamSingle.Mode.Read);
                requestParamBatch.add(batchItem);
                ODataResponse oDataResponse = store.executeRequest(requestParamBatch);
                if (oDataResponse instanceof ODataResponseBatchDefaultImpl) {
                    ODataResponseBatch batchResponse = (ODataResponseBatch) oDataResponse;
                    List<ODataResponseBatchItem> responses = batchResponse.getResponses();
                    for (ODataResponseBatchItem response : responses) {
                        // Check if batch item is a change set
                        if (response instanceof ODataResponseChangeSetDefaultImpl) {
                            // Todo here multiple batch request will come
                        } else {
                            ODataResponseSingle oDataResponseSingle = (ODataResponseSingleDefaultImpl) response;
                            // TODO Check if batch item is a single READ request
                            ODataEntitySet feed = (ODataEntitySet) oDataResponseSingle.getPayload();
                            // Get the list of ODataEntity
                            List<ODataEntity> entities = feed.getEntities();

                            for (ODataEntity entity : entities) {
                                soListBean = new SOListBean();
                                soListBean = getTaskHeader(soListBean, entity);
                                soListBeanArrayList.add(soListBean);
                            }

                        }

                    }
                }

            } catch (Exception e) {
                throw new com.arteriatech.mutils.common.OnlineODataStoreException(e);
            }
        }*/
        return soListBeanArrayList;
    }


    private static SOListBean getTaskHeader(SOListBean soListBean, ODataEntity entity) {
        ODataProperty property;
        ODataPropMap properties;
        properties = entity.getProperties();
        property = properties.get(Constants.InstanceID);
        soListBean.setInstanceID(property.getValue().toString());
        property = properties.get(Constants.EntityKey);
        soListBean.setSONo(property.getValue().toString());
        property = properties.get(Constants.EntityDate1);
        String convertDateFormat = UtilConstants.convertCalenderToStringFormat((GregorianCalendar) property.getValue());
        soListBean.setOrderDate(convertDateFormat);
        property = properties.get(Constants.EntityKeyID);
        soListBean.setQuantity(property.getValue().toString());
        property = properties.get(Constants.EntityKeyDesc);
        soListBean.setTotalAmt(property.getValue().toString());
        property = properties.get(Constants.PriorityNumber);
        soListBean.setDelvStatus(property.getValue().toString());
        property = properties.get(Constants.EntityAttribute1);
        soListBean.setEntityAttribute1(property.getValue().toString());
        return soListBean;
    }

    public static void requestOnline(final GetOnlineODataInterface getOnlineODataInterface, final Bundle bundle, final Context mContext) throws Exception {
        String resourcePath = "";
        String sessionId = "";
        int operation = 0;
        int requestCode = 0;
        boolean isSessionRequired = false;
        boolean isSessionInResourceRequired = false;
        if (bundle == null) {
            throw new IllegalArgumentException("bundle is null");
        } else {
            resourcePath = bundle.getString(Constants.BUNDLE_RESOURCE_PATH, "");
            sessionId = bundle.getString(Constants.BUNDLE_SESSION_ID, "");
            operation = bundle.getInt(Constants.BUNDLE_OPERATION, 0);
            requestCode = bundle.getInt(Constants.BUNDLE_REQUEST_CODE, 0);
            //   isSessionRequired = bundle.getBoolean(Constants.BUNDLE_SESSION_REQUIRED, false);
            isSessionRequired = false;
            isSessionInResourceRequired = bundle.getBoolean(Constants.BUNDLE_SESSION_URL_REQUIRED, false);
        }
        if (TextUtils.isEmpty(resourcePath)) {
            throw new IllegalArgumentException("resource path is null");
        } else {
            final Map<String, String> createHeaders = new HashMap<String, String>();
//            createHeaders.put(Constants.arteria_dayfilter, Constants.NO_OF_DAYS);
            if (!TextUtils.isEmpty(sessionId)) {
                createHeaders.put(Constants.arteria_session_header, sessionId);
                if (isSessionInResourceRequired) {
                    resourcePath = String.format(resourcePath, sessionId);
                }
                requestOnlineWithSessionId(resourcePath, operation, requestCode, createHeaders, getOnlineODataInterface, bundle);
            } else if (isSessionRequired) {
                final int finalOperation = operation;
                final String finalResourcePath = resourcePath;
                final int finalRequestCode = requestCode;
                final boolean finalIsSessionInResourceRequired = isSessionInResourceRequired;
                new GetSessionIdAsyncTask(mContext, new AsyncTaskCallBack() {
                    @Override
                    public void onStatus(boolean status, String values) {
                        String resourcePath = finalResourcePath;
                        if (status) {
                            if (UtilConstants.isNetworkAvailable(mContext)) {
                                if (finalIsSessionInResourceRequired) {
                                    resourcePath = String.format(resourcePath, values);
                                }
                                createHeaders.put(Constants.arteria_session_header, values);
                                try {
                                    bundle.putString(Constants.BUNDLE_SESSION_ID, values);
                                    requestOnlineWithSessionId(resourcePath, finalOperation, finalRequestCode, createHeaders, getOnlineODataInterface, bundle);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (getOnlineODataInterface != null)
                                        getOnlineODataInterface.responseFailed(null, finalOperation, finalRequestCode, resourcePath, e.getMessage(), bundle);
                                }
                            } else {
                                if (getOnlineODataInterface != null)
                                    getOnlineODataInterface.responseFailed(null, finalOperation, finalRequestCode, resourcePath, mContext.getString(R.string.msg_no_network), bundle);
//                                throw new IllegalArgumentException(mContext.getString(R.string.msg_no_network));
                            }
                        } else {
                            if (getOnlineODataInterface != null)
                                getOnlineODataInterface.responseFailed(null, finalOperation, finalRequestCode, resourcePath, values, bundle);
//                            throw new IllegalArgumentException(values);
                        }
                    }
                }).execute();

            } else {
                requestOnlineWithSessionId(resourcePath, operation, requestCode, createHeaders, getOnlineODataInterface, bundle);
            }
        }
    }

    public static void requestOnlineSPMTPSubOrdinates(final GetOnlineODataInterface getOnlineODataInterface, final Bundle bundle, final Context mContext) throws Exception {
        String resourcePath = "";
        String sessionId = "";
        int operation = 0;
        int requestCode = 0;
        boolean isSessionRequired = false;
        boolean isSessionInResourceRequired = false;
        if (bundle == null) {
            throw new IllegalArgumentException("bundle is null");
        } else {
            resourcePath = bundle.getString(Constants.BUNDLE_RESOURCE_PATH, "");
            sessionId = bundle.getString(Constants.BUNDLE_SESSION_ID, "");
            operation = bundle.getInt(Constants.BUNDLE_OPERATION, 0);
            requestCode = bundle.getInt(Constants.BUNDLE_REQUEST_CODE, 0);
            //   isSessionRequired = bundle.getBoolean(Constants.BUNDLE_SESSION_REQUIRED, false);
            isSessionRequired = false;
            isSessionInResourceRequired = bundle.getBoolean(Constants.BUNDLE_SESSION_URL_REQUIRED, false);
        }
        if (TextUtils.isEmpty(resourcePath)) {
            throw new IllegalArgumentException("resource path is null");
        } else {
            final Map<String, String> createHeaders = new HashMap<String, String>();
            createHeaders.put(Constants.arteria_attfilter, "SE");
            createHeaders.put(Constants.arteria_spfilter, "X");
            if (!TextUtils.isEmpty(sessionId)) {
                createHeaders.put(Constants.arteria_session_header, sessionId);
                if (isSessionInResourceRequired) {
                    resourcePath = String.format(resourcePath, sessionId);
                }
                requestOnlineWithSessionId(resourcePath, operation, requestCode, createHeaders, getOnlineODataInterface, bundle);
            } else if (isSessionRequired) {
                final int finalOperation = operation;
                final String finalResourcePath = resourcePath;
                final int finalRequestCode = requestCode;
                final boolean finalIsSessionInResourceRequired = isSessionInResourceRequired;
                new GetSessionIdAsyncTask(mContext, new AsyncTaskCallBack() {
                    @Override
                    public void onStatus(boolean status, String values) {
                        String resourcePath = finalResourcePath;
                        if (status) {
                            if (UtilConstants.isNetworkAvailable(mContext)) {
                                if (finalIsSessionInResourceRequired) {
                                    resourcePath = String.format(resourcePath, values);
                                }
                                createHeaders.put(Constants.arteria_session_header, values);
                                try {
                                    bundle.putString(Constants.BUNDLE_SESSION_ID, values);
                                    requestOnlineWithSessionId(resourcePath, finalOperation, finalRequestCode, createHeaders, getOnlineODataInterface, bundle);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (getOnlineODataInterface != null)
                                        getOnlineODataInterface.responseFailed(null, finalOperation, finalRequestCode, resourcePath, e.getMessage(), bundle);
                                }
                            } else {
                                if (getOnlineODataInterface != null)
                                    getOnlineODataInterface.responseFailed(null, finalOperation, finalRequestCode, resourcePath, mContext.getString(R.string.msg_no_network), bundle);
//                                throw new IllegalArgumentException(mContext.getString(R.string.msg_no_network));
                            }
                        } else {
                            if (getOnlineODataInterface != null)
                                getOnlineODataInterface.responseFailed(null, finalOperation, finalRequestCode, resourcePath, values, bundle);
//                            throw new IllegalArgumentException(values);
                        }
                    }
                }).execute();

            } else {
                requestOnlineWithSessionId(resourcePath, operation, requestCode, createHeaders, getOnlineODataInterface, bundle);
            }
        }
    }

    public static void requestOnlineAtt(final GetOnlineODataInterface getOnlineODataInterface, final Bundle bundle, final Context mContext) throws Exception {
        String resourcePath = "";
        String sessionId = "";
        int operation = 0;
        int requestCode = 0;
        boolean isSessionRequired = false;
        boolean isSessionInResourceRequired = false;
        if (bundle == null) {
            throw new IllegalArgumentException("bundle is null");
        } else {
            resourcePath = bundle.getString(Constants.BUNDLE_RESOURCE_PATH, "");
            sessionId = bundle.getString(Constants.BUNDLE_SESSION_ID, "");
            operation = bundle.getInt(Constants.BUNDLE_OPERATION, 0);
            requestCode = bundle.getInt(Constants.BUNDLE_REQUEST_CODE, 0);
            //   isSessionRequired = bundle.getBoolean(Constants.BUNDLE_SESSION_REQUIRED, false);
            isSessionRequired = false;
            isSessionInResourceRequired = bundle.getBoolean(Constants.BUNDLE_SESSION_URL_REQUIRED, false);
        }
        if (TextUtils.isEmpty(resourcePath)) {
            throw new IllegalArgumentException("resource path is null");
        } else {
            final Map<String, String> createHeaders = new HashMap<String, String>();
            createHeaders.put(Constants.arteria_attfilter, "SE");
            if (!TextUtils.isEmpty(sessionId)) {
                createHeaders.put(Constants.arteria_session_header, sessionId);
                if (isSessionInResourceRequired) {
                    resourcePath = String.format(resourcePath, sessionId);
                }
                requestOnlineWithSessionId(resourcePath, operation, requestCode, createHeaders, getOnlineODataInterface, bundle);
            } else if (isSessionRequired) {
                final int finalOperation = operation;
                final String finalResourcePath = resourcePath;
                final int finalRequestCode = requestCode;
                final boolean finalIsSessionInResourceRequired = isSessionInResourceRequired;
                new GetSessionIdAsyncTask(mContext, new AsyncTaskCallBack() {
                    @Override
                    public void onStatus(boolean status, String values) {
                        String resourcePath = finalResourcePath;
                        if (status) {
                            if (UtilConstants.isNetworkAvailable(mContext)) {
                                if (finalIsSessionInResourceRequired) {
                                    resourcePath = String.format(resourcePath, values);
                                }
                                createHeaders.put(Constants.arteria_session_header, values);
                                try {
                                    bundle.putString(Constants.BUNDLE_SESSION_ID, values);
                                    requestOnlineWithSessionId(resourcePath, finalOperation, finalRequestCode, createHeaders, getOnlineODataInterface, bundle);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (getOnlineODataInterface != null)
                                        getOnlineODataInterface.responseFailed(null, finalOperation, finalRequestCode, resourcePath, e.getMessage(), bundle);
                                }
                            } else {
                                if (getOnlineODataInterface != null)
                                    getOnlineODataInterface.responseFailed(null, finalOperation, finalRequestCode, resourcePath, mContext.getString(R.string.msg_no_network), bundle);
//                                throw new IllegalArgumentException(mContext.getString(R.string.msg_no_network));
                            }
                        } else {
                            if (getOnlineODataInterface != null)
                                getOnlineODataInterface.responseFailed(null, finalOperation, finalRequestCode, resourcePath, values, bundle);
//                            throw new IllegalArgumentException(values);
                        }
                    }
                }).execute();

            } else {
                requestOnlineWithSessionId(resourcePath, operation, requestCode, createHeaders, getOnlineODataInterface, bundle);
            }
        }
    }

    public static void requestOnlineDashBoard(final GetOnlineODataInterface getOnlineODataInterface, final Bundle bundle, final Context mContext) throws Exception {
        String resourcePath = "";
        String sessionId = "";
        int operation = 0;
        int requestCode = 0;
        boolean isSessionRequired = false;
        boolean isSessionInResourceRequired = false;
        if (bundle == null) {
            throw new IllegalArgumentException("bundle is null");
        } else {
            resourcePath = bundle.getString(Constants.BUNDLE_RESOURCE_PATH, "");
            sessionId = bundle.getString(Constants.BUNDLE_SESSION_ID, "");
            operation = bundle.getInt(Constants.BUNDLE_OPERATION, 0);
            requestCode = bundle.getInt(Constants.BUNDLE_REQUEST_CODE, 0);
            //   isSessionRequired = bundle.getBoolean(Constants.BUNDLE_SESSION_REQUIRED, false);
            isSessionRequired = false;
            isSessionInResourceRequired = bundle.getBoolean(Constants.BUNDLE_SESSION_URL_REQUIRED, false);
        }
        if (TextUtils.isEmpty(resourcePath)) {
            throw new IllegalArgumentException("resource path is null");
        } else {
            final Map<String, String> createHeaders = new HashMap<String, String>();
//            createHeaders.put(Constants.arteria_dayfilter, Constants.NO_OF_DAYS);
            if (!TextUtils.isEmpty(sessionId)) {
                createHeaders.put(Constants.arteria_session_header, sessionId);
                if (isSessionInResourceRequired) {
                    resourcePath = String.format(resourcePath, sessionId);
                }
                requestOnlineWithSessionIdDashBoard(resourcePath, operation, requestCode, createHeaders, getOnlineODataInterface, bundle);
            } else if (isSessionRequired) {
                final int finalOperation = operation;
                final String finalResourcePath = resourcePath;
                final int finalRequestCode = requestCode;
                final boolean finalIsSessionInResourceRequired = isSessionInResourceRequired;
                new GetSessionIdAsyncTask(mContext, new AsyncTaskCallBack() {
                    @Override
                    public void onStatus(boolean status, String values) {
                        String resourcePath = finalResourcePath;
                        if (status) {
                            if (UtilConstants.isNetworkAvailable(mContext)) {
                                if (finalIsSessionInResourceRequired) {
                                    resourcePath = String.format(resourcePath, values);
                                }
                                createHeaders.put(Constants.arteria_session_header, values);
                                try {
                                    bundle.putString(Constants.BUNDLE_SESSION_ID, values);
                                    requestOnlineWithSessionIdDashBoard(resourcePath, finalOperation, finalRequestCode, createHeaders, getOnlineODataInterface, bundle);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (getOnlineODataInterface != null)
                                        getOnlineODataInterface.responseFailed(null, finalOperation, finalRequestCode, resourcePath, e.getMessage(), bundle);
                                }
                            } else {
                                if (getOnlineODataInterface != null)
                                    getOnlineODataInterface.responseFailed(null, finalOperation, finalRequestCode, resourcePath, mContext.getString(R.string.msg_no_network), bundle);
//                                throw new IllegalArgumentException(mContext.getString(R.string.msg_no_network));
                            }
                        } else {
                            if (getOnlineODataInterface != null)
                                getOnlineODataInterface.responseFailed(null, finalOperation, finalRequestCode, resourcePath, values, bundle);
//                            throw new IllegalArgumentException(values);
                        }
                    }
                }).execute();

            } else {
                requestOnlineWithSessionIdDashBoard(resourcePath, operation, requestCode, createHeaders, getOnlineODataInterface, bundle);
            }
        }
    }

    private static void requestOnlineWithSessionId(String resourcePath, int operation, int requestCode, Map<String, String> createHeaders, GetOnlineODataInterface getOnlineODataInterface, Bundle bundle) throws ODataException {
        /*OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();
//        LogManager.writeLogInfo(Constants.ERROR_ARCHIVE_ENTRY_REQUEST_URL + " : " + resourcePath);
        if (store != null) {
            boolean storeDataToTechCache = bundle.getBoolean(Constants.STORE_DATA_INTO_TECHNICAL_CACHE, false);
            if (storeDataToTechCache) {
                if (!store.isOpenCache()) {
                    store.reopenCache(Constants.EncryptKey);
                }
            }//else {
            // createHeaders.put("useCache","false");
            //}
            boolean isTechnicalCacheEnable = bundle.getBoolean(UtilConstants.BUNDLE_READ_FROM_TECHNICAL_CACHE, false);
            if (store.isOpenCache()) {
                store.setPassive(isTechnicalCacheEnable);
            }

            GetOnlineRequestListener getOnlineRequestListener = new GetOnlineRequestListener(getOnlineODataInterface, operation, requestCode, resourcePath, bundle);
            scheduleReadEntity(resourcePath, getOnlineRequestListener, createHeaders, store);
        } else {
            throw new IllegalArgumentException("Store not opened");
        }*/
    }
    private static void requestOnlineWithSessionIdDashBoard(String resourcePath, int operation, int requestCode, Map<String, String> createHeaders, GetOnlineODataInterface getOnlineODataInterface, Bundle bundle) throws ODataException {
       /* OnlineStoreListenerDashBroad openListener = OnlineStoreListenerDashBroad.getInstance();
        OnlineODataStore store = openListener.getStore();
//        LogManager.writeLogInfo(Constants.ERROR_ARCHIVE_ENTRY_REQUEST_URL + " : " + resourcePath);
        if (store != null) {
            boolean storeDataToTechCache = bundle.getBoolean(Constants.STORE_DATA_INTO_TECHNICAL_CACHE, false);
            if (storeDataToTechCache) {
                if (!store.isOpenCache()) {
                    store.reopenCache(Constants.EncryptKey);
                }
            }//else {
            // createHeaders.put("useCache","false");
            //}
            boolean isTechnicalCacheEnable = bundle.getBoolean(UtilConstants.BUNDLE_READ_FROM_TECHNICAL_CACHE, false);
            if (store.isOpenCache()) {
                store.setPassive(isTechnicalCacheEnable);
            }

            GetOnlineRequestListener getOnlineRequestListener = new GetOnlineRequestListener(getOnlineODataInterface, operation, requestCode, resourcePath, bundle);
            scheduleReadEntity(resourcePath, getOnlineRequestListener, createHeaders, store);
        } else {
            throw new IllegalArgumentException("Store not opened");
        }*/
    }

    /*private static ODataRequestExecution scheduleReadEntity(String resourcePath, ODataRequestListener listener, Map<String, String> options, OnlineODataStore store) throws ODataContractViolationException {
        if (TextUtils.isEmpty(resourcePath)) {
            throw new IllegalArgumentException("resourcePath is null");
        } else if (listener == null) {
            throw new IllegalArgumentException("listener is null");
        } else {
            ODataRequestParamSingleDefaultImpl requestParam = new ODataRequestParamSingleDefaultImpl();
            requestParam.setMode(ODataRequestParamSingle.Mode.Read);
            requestParam.setResourcePath(resourcePath);
            requestParam.setOptions(options);

            requestParam.getCustomHeaders().putAll(options);

            return store.scheduleRequest(requestParam, listener);
        }
    }*/


    public static ArrayList<SOListBean> getSOList(ArrayList<SOListBean> soListBeanArrayList, List<ODataEntity> entities) {
        SOListBean soListBean;
        ODataProperty property;
        ODataPropMap properties;
        if (entities != null) {
            for (ODataEntity entity : entities) {
                soListBean = new SOListBean();
                properties = entity.getProperties();
                try {
                    property = properties.get(Constants.InstanceID);
                    soListBean.setInstanceID(property.getValue().toString());
                    property = properties.get(Constants.EntityKey);
                    soListBean.setSONo(property.getValue().toString());
                    property = properties.get(Constants.EntityDate1);
                    String convertDateFormat = UtilConstants.convertCalenderToStringFormat((GregorianCalendar) property.getValue());
                    soListBean.setOrderDate(convertDateFormat);
                    property = properties.get(Constants.EntityKeyID);
                    soListBean.setSONo(property.getValue().toString());
                    property = properties.get(Constants.EntityKeyDesc);
                    soListBean.setCustomerName(property.getValue().toString());
                    property = properties.get(Constants.EntityCurrency);
                    if (property != null)
                        soListBean.setEntityCurrency(property.getValue().toString());
                    property = properties.get(Constants.EntityValue1);
                    if (property != null)
                        soListBean.setEntityValue1(property.getValue().toString());
                    property = properties.get(Constants.EntityAttribute1);
                    soListBean.setCustomerNo(((property.getValue().toString())));
                    soListBean.setSearchText(soListBean.getSONo() + soListBean.getCustomerName() + soListBean.getCustomerNo());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                soListBeanArrayList.add(soListBean);
            }
        }

        return soListBeanArrayList;
    }

    public static ArrayList<SOListBean> getSOList(ArrayList<SOListBean> soListBeanArrayList, JSONArray jsonArray) throws JSONException {
        SOListBean soListBean;
        try {
            if (jsonArray != null) {
                for (int i=0;i< jsonArray.length();i++) {
                    soListBean = new SOListBean();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    try {
                        soListBean.setInstanceID(jsonObject.optString(Constants.InstanceID));
                        soListBean.setSONo(jsonObject.optString(Constants.EntityKey));
                        soListBean.setOrderDate(ConstantsUtils.getJSONDate(jsonObject.optString(Constants.EntityDate1)));
                        soListBean.setSONo(jsonObject.optString(Constants.EntityKeyID));
                        soListBean.setCustomerName(jsonObject.optString(Constants.EntityKeyDesc));
                        soListBean.setEntityCurrency(jsonObject.optString(Constants.EntityCurrency));
                        soListBean.setEntityValue1(jsonObject.optString(Constants.EntityValue1));
//                        soListBean.setCustomerNo(UtilConstants.removeLagingZeros(jsonObject.optString(Constants.EntityAttribute1)));
                        soListBean.setCustomerNo((jsonObject.optString(Constants.EntityAttribute1)));
                        soListBean.setSearchText(soListBean.getSONo() + soListBean.getCustomerName() + soListBean.getCustomerNo());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    soListBeanArrayList.add(soListBean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return soListBeanArrayList;
    }

    /*get SO details list*/
    public static SalesApprovalBean getSOHeaderList(String odataQry,
                                                    String mStrInstanceId, Context mContext) throws com.arteriatech.mutils.common.OnlineODataStoreException {
       /* OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();
        SalesOrderBean salesOrderBean;
        ODataProperty property;
        ODataPropMap properties;
        SalesApprovalBean appbean = null;
        if (store != null) {
            try {

                ODataRequestParamBatch requestParamBatch = new ODataRequestParamBatchDefaultImpl();
                ODataRequestParamSingle batchItem = new ODataRequestParamSingleDefaultImpl();
                batchItem.setResourcePath(odataQry);
                batchItem.setMode(ODataRequestParamSingle.Mode.Read);
                final Map<String, String> createHeaders = new HashMap<>();
                ArrayList<SalesOrderBean> headerBeanList = new ArrayList<SalesOrderBean>();
             *//*   if (!TextUtils.isEmpty(sessionId)) {
                    createHeaders.put(Constants.arteria_session_header, sessionId);
                }*//*
                batchItem.getCustomHeaders().putAll(createHeaders);

                requestParamBatch.add(batchItem);
                ODataResponse oDataResponse = store.executeRequest(requestParamBatch);
                if (oDataResponse instanceof ODataResponseBatchDefaultImpl) {
                    ODataResponseBatch batchResponse = (ODataResponseBatch) oDataResponse;
                    List<ODataResponseBatchItem> responses = batchResponse.getResponses();
                    for (ODataResponseBatchItem response : responses) {
                        // Check if batch item is a change set
                        if (response instanceof ODataResponseChangeSetDefaultImpl) {
                            // Todo here multiple batch request will come
                        } else {
                            ODataResponseSingle oDataResponseSingle = (ODataResponseSingleDefaultImpl) response;
                            // TODO Check if batch item is a single READ request
                            ODataEntity entity = (ODataEntity) oDataResponseSingle.getPayload();
                            salesOrderBean = new SalesOrderBean();
                            properties = entity.getProperties();
                            boolean isCancelledSO = false;
                            properties = entity.getProperties();


                            property = properties.get(Constants.SONo);
                            String SoNo = (String) property.getValue();
                            salesOrderBean.setOrderNo((String) property.getValue());

                            property = properties.get(Constants.DelvStatus);
                            salesOrderBean.setDelvStatus((String) property.getValue());

                            property = properties.get(Constants.Currency);
                            salesOrderBean.setCurrency((String) property.getValue());

                            property = properties.get(Constants.NetPrice);
                            BigDecimal netAmount = (BigDecimal) property.getValue();
                            salesOrderBean.setNetAmount(netAmount.doubleValue() + "");


                            property = properties.get(Constants.TaxAmount);
                            BigDecimal taxAmount = (BigDecimal) property.getValue();
                            salesOrderBean.setTaxAmt(taxAmount.doubleValue() + "");


                            property = properties.get(Constants.TotalAmount);
                            BigDecimal totalAmount = (BigDecimal) property.getValue();
                            salesOrderBean.setTotalAmt(totalAmount.doubleValue() + "");

                            property = properties.get(Constants.OrderDate);
                            String convertDateFormat = ConstantsUtils.convertCalenderToDisplayDateFormat((GregorianCalendar) property.getValue());
                            salesOrderBean.setOrderDate(convertDateFormat);


                            property = properties.get(Constants.OrderType);
                            salesOrderBean.setOrderType((String) property.getValue());

                            property = properties.get(Constants.OrderTypeDesc);
                            salesOrderBean.setOrderTypeDesc((String) property.getValue());

                            property = properties.get(Constants.SalesArea);
                            salesOrderBean.setSalesArea((String) property.getValue());

                            property = properties.get(Constants.SalesAreaDesc);
                            salesOrderBean.setSalesAreaDesc((String) property.getValue());

                            property = properties.get(Constants.CustomerNo);
                            salesOrderBean.setSoldTo((String) property.getValue());

                            property = properties.get(Constants.CustomerName);
                            salesOrderBean.setSoldToName((String) property.getValue());


                            property = properties.get(Constants.ShipToParty);
                            salesOrderBean.setShipTo((String) property.getValue());

                            property = properties.get(Constants.ShipToPartyName);
                            salesOrderBean.setShipToName((String) property.getValue());


                            property = properties.get(Constants.ShippingTypeID);
                            salesOrderBean.setShippingTypeID((String) property.getValue());

                            property = properties.get(Constants.ShippingTypeDesc);
                            salesOrderBean.setShippingTypeDesc((String) property.getValue());

//                        property = properties.get("ZZFrwadgAgentName");
//                        salesOrderBean.setForwardingAgentName((String) property.getValue());
//
//
//                        property = properties.get("ZZFrwadgAgent");
//                        salesOrderBean.setForwardingAgent((String) property.getValue());

                            property = properties.get(Constants.Plant);
                            salesOrderBean.setPlant((String) property.getValue());

                            property = properties.get(Constants.PlantDesc);
                            salesOrderBean.setPlantDesc((String) property.getValue());

                            property = properties.get(Constants.Incoterm1);
                            salesOrderBean.setIncoTerm1((String) property.getValue());

                            property = properties.get(Constants.Incoterm1Desc);
                            salesOrderBean.setIncoterm1Desc((String) property.getValue());


                            property = properties.get(Constants.Incoterm2);
                            salesOrderBean.setIncoterm2((String) property.getValue());

                            property = properties.get(Constants.Payterm);
                            salesOrderBean.setPaymentTerm((String) property.getValue());

                            property = properties.get(Constants.PaytermDesc);
                            salesOrderBean.setPaytermDesc((String) property.getValue());

                            property = properties.get(Constants.CustomerNo);
                            salesOrderBean.setCustomerNo((String) property.getValue());









                           *//* try {
                                String remarksQry = Constants.SOs + "('" + soListBean.getSONo() + "')?$expand=SOTexts";
                                soListBean.setRemarks(getRemarksOnline(remarksQry));
                            } catch (OnlineODataStoreException e) {
                                e.printStackTrace();
                            }*//*

                            *//*ODataNavigationProperty soTextProp = entity.getNavigationProperty(Constants.SOTexts);
                            ODataEntitySet feed = (ODataEntitySet) soTextProp.getNavigationContent();
                            List<ODataEntity> soTextEntities = feed.getEntities();

                            for (ODataEntity soTextEntity : soTextEntities) {
                                properties = soTextEntity.getProperties();
                                property = properties.get(Constants.Text);
                                soListBean.setRemarks(property.getValue().toString());
                            }*//*

                           *//* try {
                                String sessionIdTasks = ConstantsUtils.getSessionIdNoError(mContext);
                                if (!TextUtils.isEmpty(sessionIdTasks)) {
                                    String approvalHisQry = Constants.Tasks + "(InstanceID='" + mStrInstanceId + "',EntityType='SO')?$expand=TaskHistorys";
                                    soListBean.setSoTaskHistoryBeanArrayList(getApprovalHistoryOnline(approvalHisQry, sessionIdTasks));
                                } else {
                                    throw new com.arteriatech.mutils.common.OnlineODataStoreException("");
                                }
                            } catch (com.arteriatech.mutils.common.OnlineODataStoreException e) {
                                e.printStackTrace();
                                throw new com.arteriatech.mutils.common.OnlineODataStoreException(e.getMessage());
                            }*//*
                            *//*soItem details*//*
                            ODataNavigationProperty soItemDetailsProp = entity.getNavigationProperty(Constants.SOItemDetails);
                            ODataEntitySet feed = (ODataEntitySet) soItemDetailsProp.getNavigationContent();
                            List<ODataEntity> entities = feed.getEntities();
                            ArrayList<SalesOrderBean> soItemBeanArrayList = new ArrayList<>();
                            SalesOrderBean soBean;
                            *//*soItem details finish*//*
                            *//*so condition start*//*
                            soItemDetailsProp = entity.getNavigationProperty(Constants.SOConditions);
                            feed = (ODataEntitySet) soItemDetailsProp.getNavigationContent();
                            List<ODataEntity> soConditionEntities = feed.getEntities();


                            for (ODataEntity soItemEntity : entities) {
                                soBean = new SalesOrderBean();
                                properties = soItemEntity.getProperties();


                                property = properties.get(Constants.Material);
                                soBean.setMaterialNo((String) property.getValue());

                                property = properties.get(Constants.MaterialDesc);
                                soBean.setMaterialDesc((String) property.getValue());

                                property = properties.get(Constants.Currency);
                                soBean.setCurrency((String) property.getValue());

                                property = properties.get(Constants.MaterialGroup);
                                soBean.setOrderMaterialGroupID((String) property.getValue());

                                property = properties.get(Constants.ItemNo);
                                soBean.setsItemNo((String) property.getValue());

                                property = properties.get(Constants.MatGroupDesc);
                                soBean.setOrderMaterialGroupDesc((String) property.getValue());

                                property = properties.get(Constants.ItemCategory);
                                soBean.setItemCat((String) property.getValue());
                                property = properties.get(Constants.ItemCatDesc);
                                soBean.setItemCatDesc((String) property.getValue());

                                property = properties.get(Constants.Quantity);

                                String qty = "0.000";
                                if (property != null) {
                                    qty = property.getValue().toString();
                                }

                                property = properties.get(Constants.UOM);
                                soBean.setUom((String) property.getValue());

                              *//*  if (checkNoUOMZero(soBean.getUom()))
                                    soBean.setQAQty(trimQtyDecimalPlace(qty));
                                else*//*
                                soBean.setQAQty(ConstantsUtils.checkNoUOMZero(soBean.getUom(), qty));

                                property = properties.get(Constants.DelvQty);
                                String DelvQty = "0.000";
                                if (property != null) {
                                    DelvQty = property.getValue().toString();
                                }

                                if (checkNoUOMZero(soBean.getUom()))
                                    soBean.setDelvQty(trimQtyDecimalPlace(DelvQty));
                                else
                                    soBean.setDelvQty(DelvQty);

                                property = properties.get(Constants.OpenQty);
                                String OpenQty = "0.000";
                                if (property != null) {
                                    OpenQty = property.getValue().toString();
                                }

                                if (checkNoUOMZero(soBean.getUom()))
                                    soBean.setOpenQty(trimQtyDecimalPlace(OpenQty));
                                else
                                    soBean.setOpenQty(OpenQty);

                                property = properties.get(Constants.DepotStock);
                                String DepotStock = "0.000";
                                if (property != null) {
                                    DepotStock = property.getValue().toString();
                                }

                                soBean.setDepotStock(DepotStock);

                                property = properties.get(Constants.OwnStock);
                                String OwnStock = "0.000";
                                if (property != null) {
                                    OwnStock = property.getValue().toString();
                                }

                                soBean.setOwnStock(OwnStock);

                                property = properties.get(Constants.NetAmount);
                                BigDecimal netAmount1 = (BigDecimal) property.getValue();
                                soBean.setNetAmount(netAmount1.doubleValue() + "");

                                property = properties.get(Constants.MRP);
                                BigDecimal mrpAmount = (BigDecimal) property.getValue();
                                soBean.setMRP(mrpAmount.doubleValue() + "");


                                property = properties.get(Constants.GrossAmount);
                                BigDecimal grossAmount = (BigDecimal) property.getValue();
                                soBean.setTotalAmt(grossAmount.doubleValue() + "");

                                property = properties.get(Constants.Tax);
                                BigDecimal taxAmount1 = (BigDecimal) property.getValue();
                                soBean.setTaxAmt(taxAmount1.doubleValue() + "");

                                soBean.setTotalAmt(Double.parseDouble(netAmount.toString()) + Double.parseDouble(taxAmount.toString()) + "");

                                property = properties.get(Constants.Freight);
                                BigDecimal freightAmount = (BigDecimal) property.getValue();
                                soBean.setFreightAmt(freightAmount.doubleValue() + "");

                                property = properties.get(Constants.Discount);
                                BigDecimal discountAmount = (BigDecimal) property.getValue();
                                soBean.setDiscountAmt(discountAmount.doubleValue() + "");

                                property = properties.get(Constants.DiscountPer);
                                BigDecimal discountPer = (BigDecimal) property.getValue();
                                soBean.setDiscountPer(discountPer.doubleValue() + "");

                                property = properties.get(Constants.UnitPrice);
                                if (property != null)
                                    soBean.setUnitPrice(property.getValue().toString());
                                property = properties.get(Constants.SONo);
                                String soNo = property.getValue().toString();
                                soBean.setOrderNo(soNo);
                                property = properties.get(Constants.Plant);
                                soBean.setPlant(property.getValue().toString());
                                property = properties.get(Constants.PlantDesc);
                                soBean.setPlantDesc(property.getValue().toString());

                                property = properties.get(Constants.StatusID);
                                soBean.setStatusID(property.getValue().toString());
                                property = properties.get(Constants.StatusDesc);
                                soBean.setStatusDesc(property.getValue().toString());

                                property = properties.get(Constants.DelvStatusID);
                                soBean.setDelvStatus(property.getValue().toString());
                                property = properties.get(Constants.DelvStatusDesc);
                                soBean.setDelvStatusDesc(property.getValue().toString());

                                property = properties.get(Constants.StorLoc);
                                soBean.setStorLoc(property.getValue().toString());
                                property = properties.get(Constants.StorLocDesc);
                                soBean.setStorLocDesc(property.getValue().toString());
//                                property = properties.get(Constants.SONo);
//                                soItemBean.setSONo(property.getValue().toString());

                                *//*start so Schedule*//*
                               *//* ArrayList<SOSubItemBean> scheduleConditionList = null;
                                try {
                                    String sessionIdSOItemSch = ConstantsUtils.getSessionIdNoError(mContext);
                                    if (!TextUtils.isEmpty(sessionIdSOItemSch)) {
                                        String scheduleQry = Constants.SOItemDetails + "(SONo='" + soItemBean.getSONo() + "',ItemNo='" + soItemBean.getItemNo() + "')?$expand=SOItemSchedules";
                                        scheduleConditionList = getSOScheduleList(scheduleQry, new ArrayList<SOSubItemBean>(), sessionIdSOItemSch);
                                    } else {
                                        throw new com.arteriatech.mutils.common.OnlineODataStoreException("");
                                    }
                                } catch (com.arteriatech.mutils.common.OnlineODataStoreException e) {
                                    e.printStackTrace();
                                    throw new com.arteriatech.mutils.common.OnlineODataStoreException(e.getMessage());
                                }
                                soItemBean.setSoSubItemBeen(scheduleConditionList);*//*
                                soItemBeanArrayList.add(soBean);
                            }
                            *//*start so condition*//*
                            ArrayList<SalesOrderConditionsBean> soConditionItemDetaiBeanArrayList = new ArrayList<>();
                            SalesOrderConditionsBean soConditionItemDetaiBean;
                            BigDecimal totalNormalAmt = new BigDecimal("0.00");
                            BigDecimal subTotalAmt = new BigDecimal("0.00");
                            for (ODataEntity soHeader : soConditionEntities) {
                                soConditionItemDetaiBean = OfflineManager.getConditionItemDetail(soHeader, soConditionItemDetaiBeanArrayList);
                                if (soConditionItemDetaiBean != null) {
                                    try {
                                        totalNormalAmt = totalNormalAmt.add(new BigDecimal(soConditionItemDetaiBean.getconditionAmount()));
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                    try {
                                        subTotalAmt = subTotalAmt.add(new BigDecimal(soConditionItemDetaiBean.getconditionValue()));
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                    soConditionItemDetaiBeanArrayList.add(soConditionItemDetaiBean);
                                }
                            }
                            soConditionItemDetaiBean = new SalesOrderConditionsBean();
                            soConditionItemDetaiBean.setViewType("T");
                            soConditionItemDetaiBean.setName("Total");
                            soConditionItemDetaiBean.setconditionAmount(totalNormalAmt + "");
                            soConditionItemDetaiBean.setconditionValue(subTotalAmt + "");
                            soConditionItemDetaiBeanArrayList.add(soConditionItemDetaiBean);
                            salesOrderBean.setSalesOrderConditionsBeanArrayList(soConditionItemDetaiBeanArrayList);
                            *//*end so condition*//*

                            headerBeanList.add(salesOrderBean);
                            appbean = new SalesApprovalBean();

                            appbean.setSoItemBeanArrayList(soItemBeanArrayList);
                            appbean.setSoHeaderBeanArrayList(headerBeanList);

                        }

                    }
                }

            } catch (Exception e) {
                throw new com.arteriatech.mutils.common.OnlineODataStoreException(e);
            }
        }*/
        return new SalesApprovalBean();
    }

    public static SalesApprovalBean getSOHeaderList(SalesApprovalBean appbean, ODataRequestExecution oDataRequestExecution, Context mContext) throws com.arteriatech.mutils.common.OnlineODataStoreException {

        ODataProperty property;
        ODataPropMap properties;
        ArrayList<SalesOrderBean> headerBeanList = new ArrayList<SalesOrderBean>();
        try {
            ArrayList<ConfigTypeValues> configTypeValuesList = OfflineManager.checkMaterialCodeDisplay();
            ODataResponseSingle oDataResponseSingle = (ODataResponseSingleDefaultImpl) oDataRequestExecution.getResponse();
            ODataEntity entity = (ODataEntity) oDataResponseSingle.getPayload();
            if (entity != null) {
                SalesOrderBean salesOrderBean = new SalesOrderBean();
                properties = entity.getProperties();
                boolean isCancelledSO = false;
                properties = entity.getProperties();


                property = properties.get(Constants.SONo);
                String SoNo = (String) property.getValue();
                salesOrderBean.setOrderNo((String) property.getValue());

                property = properties.get(Constants.DelvStatus);
                salesOrderBean.setDelvStatus((String) property.getValue());

                property = properties.get(Constants.Currency);
                salesOrderBean.setCurrency((String) property.getValue());

                property = properties.get(Constants.NetPrice);
                BigDecimal netAmount = (BigDecimal) property.getValue();
                salesOrderBean.setNetAmount(netAmount.doubleValue() + "");


                property = properties.get(Constants.TaxAmount);
                BigDecimal taxAmount = (BigDecimal) property.getValue();
                salesOrderBean.setTaxAmt(taxAmount.doubleValue() + "");


                property = properties.get(Constants.TotalAmount);
                BigDecimal totalAmount = (BigDecimal) property.getValue();
                salesOrderBean.setTotalAmt(totalAmount.doubleValue() + "");

                property = properties.get(Constants.OrderDate);
                String convertDateFormat = ConstantsUtils.convertCalenderToDisplayDateFormat((GregorianCalendar) property.getValue());
                salesOrderBean.setOrderDate(convertDateFormat);


                property = properties.get(Constants.OrderType);
                salesOrderBean.setOrderType((String) property.getValue());

                property = properties.get(Constants.OrderTypeDesc);
                salesOrderBean.setOrderTypeDesc((String) property.getValue());

                property = properties.get(Constants.SalesArea);
                salesOrderBean.setSalesArea((String) property.getValue());

                property = properties.get(Constants.SalesAreaDesc);
                salesOrderBean.setSalesAreaDesc((String) property.getValue());

                property = properties.get(Constants.CustomerNo);
                salesOrderBean.setSoldTo((String) property.getValue());

                property = properties.get(Constants.CustomerName);
                salesOrderBean.setSoldToName((String) property.getValue());


                property = properties.get(Constants.ShipToParty);
                salesOrderBean.setShipTo((String) property.getValue());

                property = properties.get(Constants.ShipToPartyName);
                salesOrderBean.setShipToName((String) property.getValue());


                property = properties.get(Constants.ShippingTypeID);
                salesOrderBean.setShippingTypeID((String) property.getValue());

                property = properties.get(Constants.ShippingTypeDesc);
                salesOrderBean.setShippingTypeDesc((String) property.getValue());

                property = properties.get(Constants.NetWeight);
                salesOrderBean.setmStrTotalWeight(property.getValue().toString());

                property = properties.get(Constants.NetWeightUom);
                salesOrderBean.setmStrWeightUOM(property.getValue().toString());

                if (checkNoUOMZero(salesOrderBean.getmStrWeightUOM()))
                    salesOrderBean.setmStrTotalWeight(trimQtyDecimalPlace(salesOrderBean.getmStrTotalWeight()));
                else
                    salesOrderBean.setmStrTotalWeight(salesOrderBean.getmStrTotalWeight());


                property = properties.get(Constants.QuantityUom);
                salesOrderBean.setmSteTotalQtyUOM(property.getValue().toString());

                property = properties.get(Constants.TotalQuantity);
                salesOrderBean.setQAQty(ConstantsUtils.checkNoUOMZero(salesOrderBean.getmSteTotalQtyUOM(), property.getValue().toString()));

//                        property = properties.get("ZZFrwadgAgentName");
//                        salesOrderBean.setForwardingAgentName((String) property.getValue());
//
//
//                        property = properties.get("ZZFrwadgAgent");
//                        salesOrderBean.setForwardingAgent((String) property.getValue());

                property = properties.get(Constants.Plant);
                salesOrderBean.setPlant((String) property.getValue());

                property = properties.get(Constants.PlantDesc);
                salesOrderBean.setPlantDesc((String) property.getValue());

                property = properties.get(Constants.Incoterm1);
                salesOrderBean.setIncoTerm1((String) property.getValue());

                property = properties.get(Constants.Incoterm1Desc);
                salesOrderBean.setIncoterm1Desc((String) property.getValue());


                property = properties.get(Constants.Incoterm2);
                salesOrderBean.setIncoterm2((String) property.getValue());

                property = properties.get(Constants.Payterm);
                salesOrderBean.setPaymentTerm((String) property.getValue());

                property = properties.get(Constants.PaytermDesc);
                salesOrderBean.setPaytermDesc((String) property.getValue());

                property = properties.get(Constants.CustomerNo);
                salesOrderBean.setCustomerNo((String) property.getValue());

                property = properties.get(Constants.CustomerPODate);
                try {
                    if (property != null)
                        salesOrderBean.setPODate(ConstantsUtils.convertCalenderToDisplayDateFormat((GregorianCalendar) property.getValue()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                property = properties.get(Constants.CustomerPO);
                if (property != null)
                    salesOrderBean.setPONo(property.getValue().toString());









                           /* try {
                                String remarksQry = Constants.SOs + "('" + soListBean.getSONo() + "')?$expand=SOTexts";
                                soListBean.setRemarks(getRemarksOnline(remarksQry));
                            } catch (OnlineODataStoreException e) {
                                e.printStackTrace();
                            }*/

                            /*ODataNavigationProperty soTextProp = entity.getNavigationProperty(Constants.SOTexts);
                            ODataEntitySet feed = (ODataEntitySet) soTextProp.getNavigationContent();
                            List<ODataEntity> soTextEntities = feed.getEntities();

                            for (ODataEntity soTextEntity : soTextEntities) {
                                properties = soTextEntity.getProperties();
                                property = properties.get(Constants.Text);
                                soListBean.setRemarks(property.getValue().toString());
                            }*/

                           /* try {
                                String sessionIdTasks = ConstantsUtils.getSessionIdNoError(mContext);
                                if (!TextUtils.isEmpty(sessionIdTasks)) {
                                    String approvalHisQry = Constants.Tasks + "(InstanceID='" + mStrInstanceId + "',EntityType='SO')?$expand=TaskHistorys";
                                    soListBean.setSoTaskHistoryBeanArrayList(getApprovalHistoryOnline(approvalHisQry, sessionIdTasks));
                                } else {
                                    throw new com.arteriatech.mutils.common.OnlineODataStoreException("");
                                }
                            } catch (com.arteriatech.mutils.common.OnlineODataStoreException e) {
                                e.printStackTrace();
                                throw new com.arteriatech.mutils.common.OnlineODataStoreException(e.getMessage());
                            }*/
                /*soItem details*/
                ODataNavigationProperty soItemDetailsProp = entity.getNavigationProperty(Constants.SOItemDetails);
                ODataEntitySet feed = (ODataEntitySet) soItemDetailsProp.getNavigationContent();
                List<ODataEntity> entities = feed.getEntities();
                ArrayList<SalesOrderBean> soItemBeanArrayList = new ArrayList<>();
                SalesOrderBean soBean;
                /*soItem details finish*/
                /*so condition start*/
                soItemDetailsProp = entity.getNavigationProperty(Constants.SOConditions);
                feed = (ODataEntitySet) soItemDetailsProp.getNavigationContent();
                List<ODataEntity> soConditionEntities = feed.getEntities();


                for (ODataEntity soItemEntity : entities) {
                    soBean = new SalesOrderBean();
                    properties = soItemEntity.getProperties();


                    property = properties.get(Constants.Material);
                    soBean.setMaterialNo((String) property.getValue());

                    property = properties.get(Constants.MaterialDesc);
                    soBean.setMaterialDesc((String) property.getValue());
                    if (!configTypeValuesList.isEmpty()) {
                        soBean.setMatNoAndDesc(mContext.getString(R.string.po_details_display_value, soBean.getMaterialDesc(), soBean.getMaterialNo()));
                    } else {
                        soBean.setMatNoAndDesc(soBean.getMaterialDesc());
                    }
                    property = properties.get(Constants.Currency);
                    soBean.setCurrency((String) property.getValue());

                    property = properties.get(Constants.HighLevellItemNo);
                    soBean.setHighLevellItemNo((String) property.getValue());

                    property = properties.get(Constants.ItemFlag);
                    soBean.setItemFlag((String) property.getValue());


                    property = properties.get(Constants.MaterialGroup);
                    soBean.setOrderMaterialGroupID((String) property.getValue());

                    property = properties.get(Constants.ItemNo);
                    soBean.setsItemNo((String) property.getValue());

                    property = properties.get(Constants.MatGroupDesc);
                    soBean.setOrderMaterialGroupDesc((String) property.getValue());

                    property = properties.get(Constants.ItemCategory);
                    soBean.setItemCat((String) property.getValue());
                    property = properties.get(Constants.ItemCatDesc);
                    soBean.setItemCatDesc((String) property.getValue());

                    property = properties.get(Constants.Quantity);

                    String qty = "0.000";
                    if (property != null) {
                        qty = property.getValue().toString();
                    }

                    property = properties.get(Constants.UOM);
                    soBean.setUom((String) property.getValue());

                    if (checkNoUOMZero(soBean.getUom()))
                        soBean.setQAQty(trimQtyDecimalPlace(qty));
                    else
                        soBean.setQAQty(trimQtyDecimalPlace(qty));

                    property = properties.get(Constants.DelvQty);
                    String DelvQty = "0.000";
                    if (property != null) {
                        DelvQty = property.getValue().toString();
                    }

                    if (checkNoUOMZero(soBean.getUom()))
                        soBean.setDelvQty(trimQtyDecimalPlace(DelvQty));
                    else
                        soBean.setDelvQty(DelvQty);

                    property = properties.get(Constants.OpenQty);
                    String OpenQty = "0.000";
                    if (property != null) {
                        OpenQty = property.getValue().toString();
                    }

                    if (checkNoUOMZero(soBean.getUom()))
                        soBean.setOpenQty(trimQtyDecimalPlace(OpenQty));
                    else
                        soBean.setOpenQty(OpenQty);

                    property = properties.get(Constants.DepotStock);
                    String DepotStock = "0.000";
                    if (property != null) {
                        DepotStock = property.getValue().toString();
                    }

                    soBean.setDepotStock(DepotStock);

                    property = properties.get(Constants.OwnStock);
                    String OwnStock = "0.000";
                    if (property != null) {
                        OwnStock = property.getValue().toString();
                    }

                    soBean.setOwnStock(OwnStock);

                    property = properties.get(Constants.NetAmount);
                    BigDecimal netAmount1 = (BigDecimal) property.getValue();
                    soBean.setNetAmount(netAmount1.doubleValue() + "");

                    property = properties.get(Constants.MRP);
                    BigDecimal mrpAmount = (BigDecimal) property.getValue();
                    soBean.setMRP(mrpAmount.doubleValue() + "");


                    property = properties.get(Constants.GrossAmount);
                    BigDecimal grossAmount = (BigDecimal) property.getValue();
                    soBean.setTotalAmt(grossAmount.doubleValue() + "");

                    property = properties.get(Constants.Tax);
                    BigDecimal taxAmount1 = (BigDecimal) property.getValue();
                    soBean.setTaxAmt(taxAmount1.doubleValue() + "");

                    soBean.setTotalAmt(Double.parseDouble(netAmount.toString()) + Double.parseDouble(taxAmount.toString()) + "");

                    property = properties.get(Constants.Freight);
                    BigDecimal freightAmount = (BigDecimal) property.getValue();
                    soBean.setFreightAmt(freightAmount.doubleValue() + "");

                    property = properties.get(Constants.Discount);
                    BigDecimal discountAmount = (BigDecimal) property.getValue();
                    soBean.setDiscountAmt(discountAmount.doubleValue() + "");

                    property = properties.get(Constants.DiscountPer);
                    BigDecimal discountPer = (BigDecimal) property.getValue();
                    soBean.setDiscountPer(discountPer.doubleValue() + "");

                    property = properties.get(Constants.UnitPrice);
                    if (property != null)
                        soBean.setUnitPrice(property.getValue().toString());
                    property = properties.get(Constants.SONo);
                    String soNo = property.getValue().toString();
                    soBean.setOrderNo(soNo);
                    property = properties.get(Constants.Plant);
                    soBean.setPlant(property.getValue().toString());
                    property = properties.get(Constants.PlantDesc);
                    soBean.setPlantDesc(property.getValue().toString());

                    property = properties.get(Constants.StatusID);
                    soBean.setStatusID(property.getValue().toString());
                    property = properties.get(Constants.StatusDesc);
                    soBean.setStatusDesc(property.getValue().toString());

                    property = properties.get(Constants.DelvStatusID);
                    soBean.setDelvStatus(property.getValue().toString());
                    property = properties.get(Constants.DelvStatusDesc);
                    soBean.setDelvStatusDesc(property.getValue().toString());

                    property = properties.get(Constants.StorLoc);
                    soBean.setStorLoc(property.getValue().toString());
                    property = properties.get(Constants.StorLocDesc);
                    soBean.setStorLocDesc(property.getValue().toString());
//                                property = properties.get(Constants.SONo);
//                                soItemBean.setSONo(property.getValue().toString());

                    /*start so Schedule*/
                               /* ArrayList<SOSubItemBean> scheduleConditionList = null;
                                try {
                                    String sessionIdSOItemSch = ConstantsUtils.getSessionIdNoError(mContext);
                                    if (!TextUtils.isEmpty(sessionIdSOItemSch)) {
                                        String scheduleQry = Constants.SOItemDetails + "(SONo='" + soItemBean.getSONo() + "',ItemNo='" + soItemBean.getItemNo() + "')?$expand=SOItemSchedules";
                                        scheduleConditionList = getSOScheduleList(scheduleQry, new ArrayList<SOSubItemBean>(), sessionIdSOItemSch);
                                    } else {
                                        throw new com.arteriatech.mutils.common.OnlineODataStoreException("");
                                    }
                                } catch (com.arteriatech.mutils.common.OnlineODataStoreException e) {
                                    e.printStackTrace();
                                    throw new com.arteriatech.mutils.common.OnlineODataStoreException(e.getMessage());
                                }
                                soItemBean.setSoSubItemBeen(scheduleConditionList);*/
                    soItemBeanArrayList.add(soBean);
                }
                Collections.sort(soItemBeanArrayList, new Comparator<SalesOrderBean>() {
                    @Override
                    public int compare(SalesOrderBean one, SalesOrderBean two) {
                        return one.getsItemNo().compareTo(two.getsItemNo());
                    }
                });
                /*start so condition*/
                ArrayList<SalesOrderConditionsBean> soConditionItemDetaiBeanArrayList = new ArrayList<>();
                SalesOrderConditionsBean soConditionItemDetaiBean;
                BigDecimal totalNormalAmt = new BigDecimal("0.00");
                BigDecimal subTotalAmt = new BigDecimal("0.00");
                for (ODataEntity soHeader : soConditionEntities) {
                    soConditionItemDetaiBean = OfflineManager.getConditionItemDetail(soHeader, soConditionItemDetaiBeanArrayList);
                    if (soConditionItemDetaiBean != null) {
                        try {
                            totalNormalAmt = totalNormalAmt.add(new BigDecimal(soConditionItemDetaiBean.getconditionAmount()));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        try {
                            subTotalAmt = subTotalAmt.add(new BigDecimal(soConditionItemDetaiBean.getconditionValue()));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        soConditionItemDetaiBeanArrayList.add(soConditionItemDetaiBean);
                    }
                }
                Collections.sort(soConditionItemDetaiBeanArrayList, new Comparator<SalesOrderConditionsBean>() {
                    @Override
                    public int compare(SalesOrderConditionsBean o1, SalesOrderConditionsBean o2) {
                        return o1.getconditionCounter().compareTo(o2.getconditionCounter());
                    }
                });
                soConditionItemDetaiBean = new SalesOrderConditionsBean();
                soConditionItemDetaiBean.setViewType("T");
                soConditionItemDetaiBean.setName("Total");
                soConditionItemDetaiBean.setconditionAmount(totalNormalAmt + "");
                soConditionItemDetaiBean.setconditionValue(subTotalAmt + "");
                soConditionItemDetaiBeanArrayList.add(soConditionItemDetaiBean);
                salesOrderBean.setSalesOrderConditionsBeanArrayList(soConditionItemDetaiBeanArrayList);
                /*end so condition*/

                headerBeanList.add(salesOrderBean);
                appbean = new SalesApprovalBean();

                appbean.setSoItemBeanArrayList(soItemBeanArrayList);
                appbean.setSoHeaderBeanArrayList(headerBeanList);

//                        }

//                    }
//                }
            }
        } catch (Exception e) {
            throw new com.arteriatech.mutils.common.OnlineODataStoreException(e);
        }
        return appbean;
    }

    public static SalesApprovalBean getSOHeaderList(SalesApprovalBean appbean, JSONObject jsonObject, Context mContext) throws com.arteriatech.mutils.common.OnlineODataStoreException {

        ArrayList<SalesOrderBean> headerBeanList = new ArrayList<SalesOrderBean>();
        try {
            ArrayList<ConfigTypeValues> configTypeValuesList = OfflineManager.checkMaterialCodeDisplay();
            SalesOrderBean salesOrderBean = new SalesOrderBean();
            boolean isCancelledSO = false;
            salesOrderBean.setOrderNo(ConstantsUtils.getJSONString(jsonObject, Constants.SONo));
            salesOrderBean.setDelvStatus(ConstantsUtils.getJSONString(jsonObject, Constants.DelvStatus));
            salesOrderBean.setCurrency(ConstantsUtils.getJSONString(jsonObject, Constants.Currency));
            salesOrderBean.setNetAmount(ConstantsUtils.getJSONString(jsonObject, Constants.NetPrice));
            salesOrderBean.setTaxAmt(ConstantsUtils.getJSONString(jsonObject, Constants.TaxAmount));
            salesOrderBean.setTotalAmt(ConstantsUtils.getJSONString(jsonObject, Constants.TotalAmount));
            salesOrderBean.setOrderDate(ConstantsUtils.getJSONDate(ConstantsUtils.getJSONString(jsonObject, Constants.OrderDate)));
            salesOrderBean.setOrderType(ConstantsUtils.getJSONString(jsonObject, Constants.OrderType));
            salesOrderBean.setOrderTypeDesc(ConstantsUtils.getJSONString(jsonObject, Constants.OrderTypeDesc));
            salesOrderBean.setSalesArea(ConstantsUtils.getJSONString(jsonObject, Constants.SalesArea));
            salesOrderBean.setSalesAreaDesc(ConstantsUtils.getJSONString(jsonObject, Constants.SalesAreaDesc));
            salesOrderBean.setSoldTo(ConstantsUtils.getJSONString(jsonObject, Constants.CustomerNo));

            salesOrderBean.setSoldToName(ConstantsUtils.getJSONString(jsonObject, Constants.CustomerName));


            salesOrderBean.setShipTo(ConstantsUtils.getJSONString(jsonObject, Constants.ShipToParty));

            salesOrderBean.setShipToName(ConstantsUtils.getJSONString(jsonObject, Constants.ShipToPartyName));


            salesOrderBean.setShippingTypeID(ConstantsUtils.getJSONString(jsonObject, Constants.ShippingTypeID));

            salesOrderBean.setShippingTypeDesc(ConstantsUtils.getJSONString(jsonObject, Constants.ShippingTypeDesc));

            salesOrderBean.setmStrTotalWeight(ConstantsUtils.getJSONString(jsonObject, Constants.NetWeight));

            salesOrderBean.setmStrWeightUOM(ConstantsUtils.getJSONString(jsonObject, Constants.NetWeightUom));

            if (checkNoUOMZero(salesOrderBean.getmStrWeightUOM()))
                salesOrderBean.setmStrTotalWeight(trimQtyDecimalPlace(salesOrderBean.getmStrTotalWeight()));
            else
                salesOrderBean.setmStrTotalWeight(salesOrderBean.getmStrTotalWeight());


            salesOrderBean.setmSteTotalQtyUOM(ConstantsUtils.getJSONString(jsonObject, Constants.QuantityUom));

            salesOrderBean.setQAQty(ConstantsUtils.checkNoUOMZero(salesOrderBean.getmSteTotalQtyUOM(), ConstantsUtils.getJSONString(jsonObject, Constants.TotalQuantity)));


            salesOrderBean.setPlant(ConstantsUtils.getJSONString(jsonObject, Constants.Plant));

            salesOrderBean.setPlantDesc(ConstantsUtils.getJSONString(jsonObject, Constants.PlantDesc));

            salesOrderBean.setIncoTerm1(ConstantsUtils.getJSONString(jsonObject, Constants.Incoterm1));

            salesOrderBean.setIncoterm1Desc(ConstantsUtils.getJSONString(jsonObject, Constants.Incoterm1Desc));

            salesOrderBean.setIncoterm2(ConstantsUtils.getJSONString(jsonObject, Constants.Incoterm2));

            salesOrderBean.setPaymentTerm(ConstantsUtils.getJSONString(jsonObject, Constants.Payterm));

            salesOrderBean.setPaytermDesc(ConstantsUtils.getJSONString(jsonObject, Constants.PaytermDesc));

            salesOrderBean.setCustomerNo(ConstantsUtils.getJSONString(jsonObject, Constants.CustomerNo));

            try {
                salesOrderBean.setPODate(ConstantsUtils.getJSONDate(ConstantsUtils.getJSONString(jsonObject, Constants.CustomerPODate)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            salesOrderBean.setPONo(ConstantsUtils.getJSONString(jsonObject, Constants.CustomerPO));
            JSONObject object = jsonObject.getJSONObject(Constants.SOItemDetails);
            JSONArray jsonArray =getJSONArrayBody(object);

            ArrayList<SalesOrderBean> soItemBeanArrayList = new ArrayList<>();
            SalesOrderBean soBean;

            for (int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                soBean = new SalesOrderBean();


                soBean.setMaterialNo(jsonObject1.optString(Constants.Material));

                soBean.setMaterialDesc(jsonObject1.optString(Constants.MaterialDesc));
                if (!configTypeValuesList.isEmpty()) {
                    soBean.setMatNoAndDesc(mContext.getString(R.string.po_details_display_value, soBean.getMaterialDesc(), soBean.getMaterialNo()));
                } else {
                    soBean.setMatNoAndDesc(soBean.getMaterialDesc());
                }
                soBean.setCurrency(jsonObject1.optString(Constants.Currency));

                soBean.setHighLevellItemNo(jsonObject1.optString(Constants.HighLevellItemNo));

                soBean.setItemFlag(jsonObject1.optString(Constants.ItemFlag));


                soBean.setOrderMaterialGroupID(jsonObject1.optString(Constants.MaterialGroup));

                soBean.setsItemNo(jsonObject1.optString(Constants.ItemNo));

                soBean.setOrderMaterialGroupDesc(jsonObject1.optString(Constants.MatGroupDesc));

                soBean.setItemCat(jsonObject1.optString(Constants.ItemCategory));
                soBean.setItemCatDesc(jsonObject1.optString(Constants.ItemCatDesc));


                String qty = "0.000";
                qty = jsonObject1.optString(Constants.Quantity);


                soBean.setUom(jsonObject1.optString(Constants.UOM));

                if (checkNoUOMZero(soBean.getUom()))
                    soBean.setQAQty(trimQtyDecimalPlace(qty));
                else
                    soBean.setQAQty(trimQtyDecimalPlace(qty));

                String DelvQty = "0.000";
                DelvQty = jsonObject1.optString(Constants.DelvQty);


                if (checkNoUOMZero(soBean.getUom()))
                    soBean.setDelvQty(trimQtyDecimalPlace(DelvQty));
                else
                    soBean.setDelvQty(DelvQty);

                String OpenQty = "0.000";
                OpenQty =jsonObject1.optString(Constants.OpenQty);


                if (checkNoUOMZero(soBean.getUom()))
                    soBean.setOpenQty(trimQtyDecimalPlace(OpenQty));
                else
                    soBean.setOpenQty(OpenQty);

                String DepotStock = "0.000";
                DepotStock =jsonObject1.optString(Constants.DepotStock);


                soBean.setDepotStock(DepotStock);

                String OwnStock = "0.000";
                OwnStock = jsonObject1.optString(Constants.OwnStock);


                soBean.setOwnStock(OwnStock);

                soBean.setNetAmount(jsonObject1.optString(Constants.NetAmount) + "");

                soBean.setMRP(jsonObject1.optString(Constants.MRP) + "");


                soBean.setTotalAmt(jsonObject1.optString(Constants.GrossAmount) + "");

                soBean.setTaxAmt(jsonObject1.optString(Constants.Tax) + "");

                soBean.setTotalAmt(Double.parseDouble(jsonObject.optString(Constants.NetPrice)) + Double.parseDouble(jsonObject.optString(Constants.TaxAmount)) + "");

                soBean.setFreightAmt(jsonObject1.optString(Constants.Freight) + "");

                soBean.setDiscountAmt(jsonObject1.optString(Constants.Discount) + "");

                soBean.setDiscountPer(jsonObject1.optString(Constants.DiscountPer) + "");

                soBean.setUnitPrice(jsonObject1.optString(Constants.UnitPrice));
                String soNo = jsonObject1.optString(Constants.SONo);
                soBean.setOrderNo(soNo);
                soBean.setPlant(jsonObject1.optString(Constants.Plant));
                soBean.setPlantDesc(jsonObject1.optString(Constants.PlantDesc));

                soBean.setStatusID(jsonObject1.optString(Constants.StatusID));
                soBean.setStatusDesc(jsonObject1.optString(Constants.StatusDesc));

                soBean.setDelvStatus(jsonObject1.optString(Constants.DelvStatusID));
                soBean.setDelvStatusDesc(jsonObject1.optString(Constants.DelvStatusDesc));

                soBean.setStorLoc(jsonObject1.optString(Constants.StorLoc));
                soBean.setStorLocDesc(jsonObject1.optString(Constants.StorLocDesc));
                soItemBeanArrayList.add(soBean);
            }
            Collections.sort(soItemBeanArrayList, new Comparator<SalesOrderBean>() {
                @Override
                public int compare(SalesOrderBean one, SalesOrderBean two) {
                    return one.getsItemNo().compareTo(two.getsItemNo());
                }
            });
            /*start so condition*/
            ArrayList<SalesOrderConditionsBean> soConditionItemDetaiBeanArrayList = new ArrayList<>();
            SalesOrderConditionsBean soConditionItemDetaiBean;
            BigDecimal totalNormalAmt = new BigDecimal("0.00");
            BigDecimal subTotalAmt = new BigDecimal("0.00");
            JSONObject objectCondition = jsonObject.getJSONObject(Constants.SOConditions);
            JSONArray jsonArrayCondition =getJSONArrayBody(objectCondition);
            for (int j=0;j<jsonArrayCondition.length();j++) {
                JSONObject jsonObjectCon = jsonArrayCondition.getJSONObject(j);
                soConditionItemDetaiBean = OfflineManager.getConditionItemDetail(jsonObjectCon, soConditionItemDetaiBeanArrayList);
                if (soConditionItemDetaiBean != null) {
                    try {
                        totalNormalAmt = totalNormalAmt.add(new BigDecimal(soConditionItemDetaiBean.getconditionAmount()));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    try {
                        subTotalAmt = subTotalAmt.add(new BigDecimal(soConditionItemDetaiBean.getconditionValue()));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    soConditionItemDetaiBeanArrayList.add(soConditionItemDetaiBean);
                }
            }
            soConditionItemDetaiBean = new SalesOrderConditionsBean();
            soConditionItemDetaiBean.setViewType("T");
            soConditionItemDetaiBean.setName("Total");
            soConditionItemDetaiBean.setconditionAmount(totalNormalAmt + "");
            soConditionItemDetaiBean.setconditionValue(subTotalAmt + "");
            soConditionItemDetaiBeanArrayList.add(soConditionItemDetaiBean);
            salesOrderBean.setSalesOrderConditionsBeanArrayList(soConditionItemDetaiBeanArrayList);
            /*end so condition*/

            headerBeanList.add(salesOrderBean);
            appbean = new SalesApprovalBean();

            appbean.setSoItemBeanArrayList(soItemBeanArrayList);
            appbean.setSoHeaderBeanArrayList(headerBeanList);

//                        }

//                    }
//                }
        } catch (Exception e) {
            throw new com.arteriatech.mutils.common.OnlineODataStoreException(e);
        }
        return appbean;
    }
    public static SalesApprovalBean getTaskHistoryList(SalesApprovalBean appItemBean, ODataRequestExecution oDataRequestExecution, Context mContext) {
        try {
            ODataResponseSingle oDataResponseSingle = (ODataResponseSingleDefaultImpl) oDataRequestExecution.getResponse();
            ODataEntity entity = (ODataEntity) oDataResponseSingle.getPayload();
            ODataProperty property;
            ODataPropMap properties;
            if (entity != null) {
                ODataNavigationProperty soPartnerProp = entity.getNavigationProperty(Constants.TaskHistorys);
                ODataEntitySet feed = (ODataEntitySet) soPartnerProp.getNavigationContent();
                List<ODataEntity> taskHistoryEntities = feed.getEntities();
                ArrayList<SOTaskHistoryBean> taskHistorysArrayList = new ArrayList<>();
                for (ODataEntity taskHistoryEntity : taskHistoryEntities) {
                    SOTaskHistoryBean soTaskHistoryBean = getTaskValues(taskHistoryEntity, mContext);
                    if (soTaskHistoryBean != null) {
                        taskHistorysArrayList.add(soTaskHistoryBean);
                    }
                }
                appItemBean.setTaskHistorysArrayList(taskHistorysArrayList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appItemBean;
    }

    public static SalesApprovalBean getTaskHistoryList(SalesApprovalBean appItemBean, JSONObject jsonObject, Context mContext) {
        try {
            JSONObject object = jsonObject.getJSONObject(Constants.SOItemDetails);
            JSONArray jsonArray = getJSONArrayBody(object);
            ArrayList<SOTaskHistoryBean> taskHistorysArrayList = new ArrayList<>();
            for (int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                SOTaskHistoryBean soTaskHistoryBean = getTaskValues(jsonObject1, mContext);
                if (soTaskHistoryBean != null) {
                    taskHistorysArrayList.add(soTaskHistoryBean);
                }
            }
            appItemBean.setTaskHistorysArrayList(taskHistorysArrayList);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return appItemBean;
    }

    private static SOTaskHistoryBean getTaskValues(ODataEntity taskHistoryEntity, Context mContext) {
        ODataPropMap properties = taskHistoryEntity.getProperties();
        SOTaskHistoryBean taskHistorys = new SOTaskHistoryBean();
        ODataProperty property = properties.get(Constants.ActionName);
        taskHistorys.setActionName((String) property.getValue());
//                    if (!taskHistorys.getActionName().equalsIgnoreCase("Pending for Approval")) {
        property = properties.get(Constants.InstanceID);
        taskHistorys.setInstanceID((String) property.getValue());
        property = properties.get(Constants.EntityType);
        taskHistorys.setEntityType((String) property.getValue());
        property = properties.get(Constants.Comments);
        taskHistorys.setComments((String) property.getValue());
        property = properties.get(Constants.TaskStatusID);
        taskHistorys.setTaskStatusID((String) property.getValue());
        property = properties.get(Constants.PerformedByName);
        taskHistorys.setPerformedByName((String) property.getValue());
        property = properties.get(Constants.Timestamp);
        if (property != null)
            taskHistorys.setTimestamp(ConstantsUtils.convertCalenderToDisplayDateTimeFormat((GregorianCalendar) property.getValue()));
        return taskHistorys;
    }

    private static SOTaskHistoryBean getTaskValues(JSONObject jsonObject, Context mContext) {
        SOTaskHistoryBean taskHistorys = new SOTaskHistoryBean();
        taskHistorys.setActionName(jsonObject.optString(Constants.ActionName));
        taskHistorys.setInstanceID(jsonObject.optString(Constants.InstanceID));
        taskHistorys.setEntityType(jsonObject.optString(Constants.EntityType));
        taskHistorys.setComments(jsonObject.optString(Constants.Comments));
        taskHistorys.setTaskStatusID(jsonObject.optString(Constants.TaskStatusID));
        taskHistorys.setPerformedByName(jsonObject.optString(Constants.PerformedByName));
        try {
            if(jsonObject.optString(Constants.Timestamp)!=null){
                taskHistorys.setTimestamp(ConstantsUtils.getJSONDate1(jsonObject.optString(Constants.Timestamp)));
            }else {
                taskHistorys.setTimestamp("");
            }
        } catch (Throwable e) {
            taskHistorys.setTimestamp("");
            e.printStackTrace();
        }
//        taskHistorys.setTimestamp(jsonObject.optString(Constants.Timestamp));
        return taskHistorys;
    }


    public static ArrayList<SOTaskHistoryBean> getTaskHistoryList(ODataRequestExecution oDataRequestExecution, Context mContext) {
        ArrayList<SOTaskHistoryBean> taskHistorysArrayList = new ArrayList<>();
        try {
            ODataResponseSingle oDataResponseSingle = (ODataResponseSingleDefaultImpl) oDataRequestExecution.getResponse();
            ODataEntitySet entity = (ODataEntitySet) oDataResponseSingle.getPayload();
            ODataProperty property;
            ODataPropMap properties;
            if (entity != null) {
                List<ODataEntity> taskHistoryEntities = entity.getEntities();
                for (ODataEntity taskHistoryEntity : taskHistoryEntities) {
                    SOTaskHistoryBean soTaskHistoryBean = getTaskValues(taskHistoryEntity, mContext);
                    if (soTaskHistoryBean != null) {
                        taskHistorysArrayList.add(soTaskHistoryBean);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return taskHistorysArrayList;
    }

    public static ArrayList<SOTaskHistoryBean> getTaskHistoryList(JSONArray jsonArray, Context mContext) {
        ArrayList<SOTaskHistoryBean> taskHistorysArrayList = new ArrayList<>();
        try {
            if (jsonArray != null && jsonArray.length()>0) {
                for (int i = 0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    SOTaskHistoryBean soTaskHistoryBean = getTaskValues(jsonObject, mContext);
                    if (soTaskHistoryBean != null) {
                        taskHistorysArrayList.add(soTaskHistoryBean);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return taskHistorysArrayList;
    }


    public static boolean checkNoUOMZero(String UOM) throws OfflineODataStoreException {
        boolean isNoUOMZero = false;
        String qry = Constants.ConfigTypesetTypes + "?$filter=" + Constants.Typeset + " eq '" + Constants.UOMNO0 + "' and " +
                Constants.Types + " eq '" + UOM + "'";

        if (UOM != null && !UOM.equalsIgnoreCase("")) {
            if (OfflineManager.offlineStore != null) {
                List<ODataEntity> entities = UtilOfflineManager.getEntities(OfflineManager.offlineStore, qry);
                if (entities != null && entities.size() > 0) {
                    isNoUOMZero = true;
                }

            }
        }
        return isNoUOMZero;
    }

    public static String trimQtyDecimalPlace(String qty) {
        try {
            if (qty.contains("."))
                return qty.substring(0, qty.indexOf("."));
            else
                return qty;
        } catch (Exception e) {
            e.printStackTrace();
            return qty;
        }
    }


    public static void updateTasksEntity(Hashtable<String, String> table, UIListener uiListener) throws com.arteriatech.mutils.common.OnlineODataStoreException {
      /*  OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                //Creates the entity payload
                ODataEntity soCreateEntity = createTaskEntity(store, table);

                OnlineRequestListener collectionListener = new OnlineRequestListener(Operation.Update.getValue(), uiListener);

                ODataRequestParamSingle collectionReq = new ODataRequestParamSingleDefaultImpl();
                collectionReq.setMode(ODataRequestParamSingle.Mode.Update);
                collectionReq.setResourcePath(soCreateEntity.getResourcePath());
                collectionReq.setPayload(soCreateEntity);

                final Map<String, String> createHeaders = new HashMap<>();
            *//*    if (!TextUtils.isEmpty(sessionId)) {
                    createHeaders.put(Constants.arteria_session_header, sessionId);
                }*//*
                collectionReq.getCustomHeaders().putAll(createHeaders);

                store.scheduleRequest(collectionReq, collectionListener);

            } catch (Exception e) {
                throw new com.arteriatech.mutils.common.OnlineODataStoreException(e);
            }
        }*/

    }

    /*private static ODataEntity createTaskEntity(OnlineODataStore store, Hashtable<String, String> hashtable) throws ODataParserException {
        ODataEntity headerEntity = null;
//        ArrayList<ODataEntity> tempArray = new ArrayList();
        try {
            if (hashtable != null) {
                // CreateOperation the parent Entity
                headerEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.SOS_SO_TASK_ENTITY);

                headerEntity.setResourcePath(Constants.Tasks + "(InstanceID='" + hashtable.get(Constants.InstanceID) + "',EntityType='SO')", Constants.Tasks + "(InstanceID='" + hashtable.get(Constants.InstanceID) + "',EntityType='SO')");


                try {
                    store.allocateProperties(headerEntity, ODataStore.PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }

                store.allocateNavigationProperties(headerEntity);

                headerEntity.getProperties().put(Constants.InstanceID,
                        new ODataPropertyDefaultImpl(Constants.InstanceID, hashtable.get(Constants.InstanceID)));
                headerEntity.getProperties().put(Constants.EntityType,
                        new ODataPropertyDefaultImpl(Constants.EntityType, hashtable.get(Constants.EntityType)));
                headerEntity.getProperties().put(Constants.DecisionKey,
                        new ODataPropertyDefaultImpl(Constants.DecisionKey, hashtable.get(Constants.DecisionKey)));

                headerEntity.getProperties().put(Constants.LoginID,
                        new ODataPropertyDefaultImpl(Constants.LoginID, hashtable.get(Constants.LoginID)));
                headerEntity.getProperties().put(Constants.EntityKey,
                        new ODataPropertyDefaultImpl(Constants.EntityKey, hashtable.get(Constants.EntityKey)));

                headerEntity.getProperties().put(Constants.Comments,
                        new ODataPropertyDefaultImpl(Constants.Comments, hashtable.get(Constants.Comments)));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return headerEntity;

    }*/

    public static boolean isCheckedItem(ArrayList<SOItemBean> soDefaultItemBeanList, SOItemBean soItemBeanNew) {
        if (soDefaultItemBeanList != null && !soDefaultItemBeanList.isEmpty()) {
            int i = 0;
            boolean isMatPresent = false;
            for (SOItemBean soItemBean : soDefaultItemBeanList) {
                if (soItemBean.getHighLevellItemNo().equalsIgnoreCase("000000")){
                    if (soItemBean.getMatCode().equalsIgnoreCase(soItemBeanNew.getMatCode())) {
                        isMatPresent = true;
                        soItemBeanNew.setSoQty(soItemBean.getSoQty());
                        soItemBeanNew.setChecked(true);
                        soItemBeanNew.setItemFlag(soItemBean.getItemFlag());
                        soItemBeanNew.setHighLevellItemNo(soItemBean.getHighLevellItemNo());
                        soItemBeanNew.setItemCategory(soItemBean.getItemCategory());
                        soItemBeanNew.setItemNo(soItemBean.getItemNo());
                        soItemBeanNew.setStatusID(soItemBean.getStatusID());
                        break;
                    } else {
                        soItemBeanNew.setSoQty("0");
                        soItemBeanNew.setChecked(false);
                    }
            }
                i++;
            }
            if (isMatPresent) {
//                soDefaultItemBeanList.remove(i);
                return true;
            }
        } else {
            soItemBeanNew.setSoQty("0");
            soItemBeanNew.setChecked(false);
        }
        return false;
    }

    /*get so item check list*/
    public static ArrayList<SOItemBean> getSOMaterialList(List<ODataEntity> entities, ArrayList<SOItemBean> soDefaultItemBeanList, int comingFrom, Context mContext) throws OfflineODataStoreException {
        ArrayList<SOItemBean> soItemBeanList = new ArrayList<>();
        ArrayList<String> removeSoDuplicateItemList = new ArrayList<>();
        SOItemBean soItemBean;
        ODataProperty property;
        ODataPropMap properties;
        // ConstantsUtils.writeDebugMsg("getSOMaterialList: start");
        try {
            if (entities != null && !entities.isEmpty()) {
                //Retrieve the data from the response
                ArrayList<ConfigTypeValues> configTypeValuesList = OfflineManager.checkMaterialCodeDisplay();
                ArrayList<ConfigTypeValues> configTypePriceZero = OfflineManager.checkMaterialPriceZeroDisplay();
                for (ODataEntity entity : entities) {
                    properties = entity.getProperties();
                    soItemBean = new SOItemBean();
                    property = properties.get(Constants.LandingPrice);
                    BigDecimal bLandingAmount = new BigDecimal("0");
                    if (property != null) {
                        bLandingAmount = (BigDecimal) property.getValue();
                        soItemBean.setLandingPrice(bLandingAmount.toString());
                    }
                    boolean isValidMat = false;
                    if (!configTypePriceZero.isEmpty()) {
                        if (bLandingAmount.compareTo(new BigDecimal("0")) == 1) {
                            isValidMat = true;
                        }
                    } else {
                        isValidMat = true;
                    }
                    if (isValidMat) {

                        property = properties.get(Constants.MaterialGroupDesc);
                        soItemBean.setMaterialGroupDesc((String) property.getValue());
                        property = properties.get(Constants.MaterialGroupID);
                        soItemBean.setMaterialGroupID((String) property.getValue());
                        property = properties.get(Constants.MaterialNo);
                        soItemBean.setMatCode((String) property.getValue());
                        if (isCheckedItem(soDefaultItemBeanList, soItemBean)) {
                            soItemBean.setChecked(true);
                        } else {
                            try {
//                                property = properties.get(Constants.HigherLevelItemno);
                                soItemBean.setHighLevellItemNo("000000");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            soItemBean.setChecked(false);
                        }
                        property = properties.get(Constants.MaterialDesc);
                        soItemBean.setMatDesc((String) property.getValue());

                        property = properties.get(Constants.MaterialDesc);
                        soItemBean.setMatDesc((String) property.getValue());

                        property = properties.get(Constants.BaseUOM);
                        soItemBean.setUom((String) property.getValue());
                        if (!configTypeValuesList.isEmpty()) {
                            soItemBean.setMatNoAndDesc(mContext.getString(R.string.po_details_display_value, soItemBean.getMatDesc(), soItemBean.getMatCode()));
                        } else {
                            soItemBean.setMatNoAndDesc(soItemBean.getMatDesc());
                        }
                        property = properties.get(Constants.Currency);
                        if (property != null)
                            soItemBean.setCurrency(property.getValue().toString());
                        property = properties.get(Constants.Brand);
                        if (property != null)
                            soItemBean.setBrand(property.getValue().toString());
                        soItemBean.setDecimalCheck(checkNoUOMZero(soItemBean.getUom()));
                        property = properties.get(Constants.NetWeight);
                        if (property != null) {
                            BigDecimal mStrAmount = (BigDecimal) property.getValue();
                            soItemBean.setNetWeight(mStrAmount.toString());
                        }
                        property = properties.get(Constants.NetWeightUOM);
                        if (property != null) {
                            soItemBean.setNetWeightUOM(property.getValue().toString());
                        }
                        Constants.mapMatGrpByMaterial.put(soItemBean.getMatCode(), soItemBean.getMaterialGroupID());
                        if(!removeSoDuplicateItemList.contains(soItemBean.getMatCode())) {
                            removeSoDuplicateItemList.add(soItemBean.getMatCode());
                            soItemBeanList.add(soItemBean);
                        }
                    }
                }
            }
            //   ConstantsUtils.writeDebugMsg("getSOMaterialList: end");
        } catch (Exception e) {
            throw new OfflineODataStoreException(e);
        }

        return soItemBeanList;
    }


    public static ArrayList<SOItemBean> getSOMaterialList(JSONArray entities, ArrayList<SOItemBean> soDefaultItemBeanList, int comingFrom, Context mContext) throws OfflineODataStoreException {
        ArrayList<SOItemBean> soItemBeanList = new ArrayList<>();
        ArrayList<String> removeSoDuplicateItemList = new ArrayList<>();
        SOItemBean soItemBean;
        ODataProperty property;
        ODataPropMap properties;
        // ConstantsUtils.writeDebugMsg("getSOMaterialList: start");
        try {
            if (entities != null && entities.length()>0) {
                //Retrieve the data from the response
                ArrayList<ConfigTypeValues> configTypeValuesList = OfflineManager.checkMaterialCodeDisplay();
                ArrayList<ConfigTypeValues> configTypePriceZero = OfflineManager.checkMaterialPriceZeroDisplay();
                for (int i=0;i<entities.length();i++) {
                    JSONObject jsonObject = entities.getJSONObject(i);
                    soItemBean = new SOItemBean();
                    if (jsonObject.has(Constants.LandingPrice)) {
                        soItemBean.setLandingPrice(jsonObject.optString(Constants.LandingPrice));
                    }
                    boolean isValidMat = false;
                    if (!configTypePriceZero.isEmpty()) {
                            isValidMat = true;

                    } else {
                        isValidMat = true;
                    }
                    if (isValidMat) {

                        soItemBean.setMaterialGroupDesc(jsonObject.optString(Constants.MaterialGroupDesc));
                        soItemBean.setMaterialGroupID(jsonObject.optString(Constants.MaterialGroupID));
                        soItemBean.setMatCode(jsonObject.optString(Constants.MaterialNo));
                        if (isCheckedItem(soDefaultItemBeanList, soItemBean)) {
                            soItemBean.setChecked(true);
                        } else {
                            try {
//                                property = properties.get(Constants.HigherLevelItemno);
                                soItemBean.setHighLevellItemNo("000000");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            soItemBean.setChecked(false);
                        }
                        soItemBean.setMatDesc(jsonObject.optString(Constants.MaterialDesc));

                        soItemBean.setMatDesc(jsonObject.optString(Constants.MaterialDesc));

                        soItemBean.setUom(jsonObject.optString(Constants.BaseUOM));
                        if (!configTypeValuesList.isEmpty()) {
                            soItemBean.setMatNoAndDesc(mContext.getString(R.string.po_details_display_value, soItemBean.getMatDesc(), soItemBean.getMatCode()));
                        } else {
                            soItemBean.setMatNoAndDesc(soItemBean.getMatDesc());
                        }
                        if (jsonObject.has(Constants.Currency))
                            soItemBean.setCurrency(jsonObject.optString(Constants.Currency));
                        if (jsonObject.has(Constants.Brand))
                            soItemBean.setBrand(jsonObject.optString(Constants.Brand));
                        soItemBean.setDecimalCheck(checkNoUOMZero(soItemBean.getUom()));
                        if (jsonObject.has(Constants.NetWeight)) {
                            soItemBean.setNetWeight(jsonObject.optString(Constants.NetWeight));
                        }
                        if (jsonObject.has(Constants.NetWeightUOM)) {
                            soItemBean.setNetWeightUOM(jsonObject.optString(Constants.NetWeightUOM));
                        }
                        Constants.mapMatGrpByMaterial.put(soItemBean.getMatCode(), soItemBean.getMaterialGroupID());
                        if(!removeSoDuplicateItemList.contains(soItemBean.getMatCode())) {
                            removeSoDuplicateItemList.add(soItemBean.getMatCode());
                            soItemBeanList.add(soItemBean);
                        }
                    }
                }
            }
            //   ConstantsUtils.writeDebugMsg("getSOMaterialList: end");
        } catch (Exception e) {
            throw new OfflineODataStoreException(e);
        }

        return soItemBeanList;
    }

    public static void requestQuery(final OnlineODataInterface onlineODataInterface, final Bundle bundle, final Context mContext) {
        String resourcePath = "";
        String sessionId = "";
        boolean isSessionRequired = false;
        int sessionType = 0;
        try {
            if (bundle == null) {
//            throw new IllegalArgumentException("bundle is null");
                if (onlineODataInterface != null)
                    onlineODataInterface.responseFailed(null, "bundle is null", bundle);
            } else {
                resourcePath = bundle.getString(Constants.BUNDLE_RESOURCE_PATH, "");
                sessionId = bundle.getString(Constants.BUNDLE_SESSION_ID, "");
                isSessionRequired = bundle.getBoolean(Constants.BUNDLE_SESSION_REQUIRED, false);
                sessionType = bundle.getInt(Constants.BUNDLE_SESSION_TYPE, 0);
            }
            if (TextUtils.isEmpty(resourcePath)) {
//            throw new IllegalArgumentException("resource path is null");
                if (onlineODataInterface != null)
                    onlineODataInterface.responseFailed(null, "resource path is null", bundle);
            } else {
                final Map<String, String> createHeaders = new HashMap<String, String>();
//            createHeaders.put(Constants.arteria_dayfilter, Constants.NO_OF_DAYS);
                if (isSessionRequired) {
                    if (!TextUtils.isEmpty(sessionId)) {
                        if (sessionType == ConstantsUtils.SESSION_HEADER) {
                            createHeaders.put(Constants.arteria_session_header, sessionId);
                        } else if (sessionType == ConstantsUtils.SESSION_QRY) {
                            resourcePath = getSessionResourcePath(resourcePath, sessionId);
                        } else if (sessionType == ConstantsUtils.SESSION_QRY_HEADER) {
                            createHeaders.put(Constants.arteria_session_header, sessionId);
                            resourcePath = getSessionResourcePath(resourcePath, sessionId);
                        }
                        requestScheduled(resourcePath, createHeaders, onlineODataInterface, bundle);
                    } else {
                        final String finalResourcePath = resourcePath;
                        final int finalsessionType = sessionType;
                        final Bundle finalBundle = bundle;
                        new SessionIDAsyncTask(mContext, new AsyncTaskCallBackInterface<ErrorBean>() {
                            @Override
                            public void asyncResponse(boolean status, ErrorBean errorBean, String values) {
                                String resourcePath = finalResourcePath;
                                if (status) {
                                    if (UtilConstants.isNetworkAvailable(mContext)) {
                                        if (finalsessionType == ConstantsUtils.SESSION_HEADER) {
                                            createHeaders.put(Constants.arteria_session_header, values);
                                        } else if (finalsessionType == ConstantsUtils.SESSION_QRY) {
//                                            resourcePath = String.format(resourcePath, values);
                                            resourcePath = getSessionResourcePath(resourcePath, values);
                                        } else if (finalsessionType == ConstantsUtils.SESSION_QRY_HEADER) {
                                            createHeaders.put(Constants.arteria_session_header, values);
                                            resourcePath = getSessionResourcePath(resourcePath, values);
                                        }
                                        try {
                                            bundle.putString(Constants.BUNDLE_SESSION_ID, values);
                                            requestScheduled(resourcePath, createHeaders, onlineODataInterface, finalBundle);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            if (onlineODataInterface != null)
                                                onlineODataInterface.responseFailed(null, e.getMessage(), finalBundle);
                                        }
                                    } else {
                                        if (onlineODataInterface != null)
                                            onlineODataInterface.responseFailed(null, mContext.getString(R.string.msg_no_network), finalBundle);
                                    }
                                } else {
                                    if (onlineODataInterface != null)
                                        onlineODataInterface.responseFailed(null, values, finalBundle);
                                }
                            }
                        }).execute();
                    }
                } else {
                    requestScheduled(resourcePath, createHeaders, onlineODataInterface, bundle);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (onlineODataInterface != null)
                onlineODataInterface.responseFailed(null, e.getMessage(), bundle);
        }
    }

    private static String getSessionResourcePath(String resourcePath, String sessionId) {
        if (resourcePath.contains("%1$s")) {
            resourcePath = String.format(resourcePath, sessionId);
        } else if (resourcePath.contains("?")) {
            resourcePath = resourcePath + "+and+LoginID+eq+'" + sessionId + "'";
        } else {
            resourcePath = resourcePath + "?$filter=LoginID+eq+'" + sessionId + "'";
        }
        return resourcePath;
    }

    private static void requestScheduled(String resourcePath, Map<String, String> createHeaders, OnlineODataInterface onlineODataInterface, Bundle bundle) throws ODataException {
        /*OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();
//        LogManager.writeLogInfo(Constants.ERROR_ARCHIVE_ENTRY_REQUEST_URL + " : " + resourcePath);
        if (store != null) {
            boolean isTechnicalCacheEnable = false;
            if (store.isOpenCache()) {
                if (bundle != null)
                    isTechnicalCacheEnable = bundle.getBoolean(UtilConstants.BUNDLE_READ_FROM_TECHNICAL_CACHE, false);
                store.setPassive(isTechnicalCacheEnable);
            } else {
                if (bundle != null)
                    bundle.putBoolean(UtilConstants.BUNDLE_READ_FROM_TECHNICAL_CACHE, false);
            }
            OnlineRequestListeners getOnlineRequestListener = new OnlineRequestListeners(onlineODataInterface, bundle);
            scheduledReqEntity(resourcePath, getOnlineRequestListener, createHeaders, store);

        } else {
            throw new IllegalArgumentException("Store not opened");
        }*/
    }

    /*private static ODataRequestExecution scheduledReqEntity(String resourcePath, ODataRequestListener listener, Map<String, String> options, OnlineODataStore store) throws ODataContractViolationException {
        if (TextUtils.isEmpty(resourcePath)) {
            throw new IllegalArgumentException("resourcePath is null");
        } else if (listener == null) {
            throw new IllegalArgumentException("listener is null");
        } else {
            ODataRequestParamSingleDefaultImpl requestParam = new ODataRequestParamSingleDefaultImpl();
            requestParam.setMode(ODataRequestParamSingle.Mode.Read);
            requestParam.setResourcePath(resourcePath);
            requestParam.setOptions(options);
            requestParam.getCustomHeaders().putAll(options);

            return store.scheduleRequest(requestParam, listener);
        }
    }*/

    /*get user customer array*/
    public static String[][] getUserCustomersArray(List<ODataEntity> entities) throws OfflineODataStoreException {
        String[] custVal[] = null;
        //Check if the offline oData store is initialized
        ODataProperty property;
        ODataPropMap properties;
        if (entities != null && entities.size() > 0) {
            custVal = new String[35][entities.size()];
            int incVal = 0;
            for (ODataEntity entity : entities) {
                properties = entity.getProperties();
                property = properties.get(Constants.CustomerNo);
                custVal[0][incVal] = (String) property.getValue();
                property = properties.get(Constants.Name);
                custVal[1][incVal] = (String) property.getValue();
                property = properties.get(Constants.Address1);
                custVal[2][incVal] = (String) property.getValue();
                property = properties.get(Constants.Address2);
                custVal[3][incVal] = (String) property.getValue();
                property = properties.get(Constants.Address3);
                custVal[4][incVal] = (String) property.getValue();
                property = properties.get(Constants.Address4);
                custVal[5][incVal] = (String) property.getValue();
                property = properties.get(Constants.District);
                custVal[6][incVal] = (String) property.getValue();
                property = properties.get(Constants.City);
                custVal[7][incVal] = (String) property.getValue();
                property = properties.get(Constants.Region);
                custVal[8][incVal] = (String) property.getValue();
                property = properties.get(Constants.RegionDesc);
                custVal[9][incVal] = (String) property.getValue();
                property = properties.get(Constants.CountryID);
                custVal[10][incVal] = (String) property.getValue();
                property = properties.get(Constants.CountryDesc);
                custVal[11][incVal] = (String) property.getValue();
                property = properties.get(Constants.PostalCode);
                custVal[12][incVal] = (String) property.getValue();
                property = properties.get(Constants.Mobile1);
                custVal[13][incVal] = (String) property.getValue();
                property = properties.get(Constants.Mobile2);
                custVal[14][incVal] = (String) property.getValue();
                property = properties.get(Constants.Landline);
                custVal[15][incVal] = (String) property.getValue();
                property = properties.get(Constants.EmailID);
                custVal[16][incVal] = (String) property.getValue();
                property = properties.get(Constants.ECCNo);
                custVal[17][incVal] = (String) property.getValue();
                property = properties.get(Constants.CSTNo);
                custVal[18][incVal] = (String) property.getValue();
                property = properties.get(Constants.LSTNo);
                custVal[19][incVal] = (String) property.getValue();
                property = properties.get(Constants.ExciseRegNo);
                custVal[20][incVal] = (String) property.getValue();
                property = properties.get(Constants.PAN);
                custVal[21][incVal] = (String) property.getValue();
                property = properties.get(Constants.ServiceTaxRegNo);
                custVal[22][incVal] = (String) property.getValue();
                property = properties.get(Constants.CreditLimit);
                BigDecimal creditLimitTotal = new BigDecimal("0.00");
                if (property != null) {
                    custVal[23][incVal] = property.getValue().toString();
                    creditLimitTotal = new BigDecimal(custVal[23][incVal]);
                } else {
                    custVal[23][incVal] = "0.00";
                }
                property = properties.get(Constants.CreditExposure);
                if (property != null) {
                    custVal[24][incVal] = property.getValue().toString();
                } else {
                    custVal[24][incVal] = "0.00";
                }
                property = properties.get(Constants.CreditLimitUsed);
                BigDecimal creditLimitUsed = new BigDecimal("0.00");
                if (property != null) {
                    custVal[25][incVal] = property.getValue().toString();
                    creditLimitUsed = new BigDecimal(custVal[25][incVal]);
                } else {
                    custVal[25][incVal] = "0.00";
                }
                property = properties.get(Constants.AnnualSales);
                if (property != null) {
                    custVal[26][incVal] = property.getValue().toString();
                } else {
                    custVal[26][incVal] = "0.00";
                }
                property = properties.get(Constants.AnnualSalesYear);
                custVal[27][incVal] = (String) property.getValue();
                property = properties.get(Constants.Currency);
                custVal[28][incVal] = (String) property.getValue();
                try {
                    custVal[29][incVal] = creditLimitTotal.subtract(creditLimitUsed) + "";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    BigDecimal creditExposure = new BigDecimal(custVal[24][incVal]);
                    BigDecimal creditLimits = new BigDecimal(custVal[23][incVal]);
                    BigDecimal soTotalAmt = new BigDecimal("0.00");
                    BigDecimal finalPer = SOUtils.getExposurePercentage(creditExposure, creditLimits, soTotalAmt);
                    custVal[30][incVal] = finalPer + "";
                } catch (Exception e) {
                    e.printStackTrace();
                    custVal[30][incVal] = "0";
                }
                property = properties.get(Constants.UnloadingPoint);
                if (property != null) {
                    custVal[31][incVal] = (String) property.getValue();
                } else {
                    custVal[31][incVal] = "";
                }
                custVal[32][incVal] = custVal[0][incVal] + " - " + custVal[1][incVal];
                custVal[33][incVal] = SOUtils.getAddressValue(properties);
                property = properties.get(Constants.GSTIN);
                custVal[34][incVal] = (String) property.getValue();
                incVal++;
            }
        }

        return custVal;
    }

    /*get data from value help*/
    public static ArrayList<ValueHelpBean> getConfigListFromValueHelp(List<ODataEntity> entities, String propName, String defaultValue) throws OfflineODataStoreException {
        //Check if the offline oData store is initialized
        ArrayList<ValueHelpBean> valueHelpArrayList = new ArrayList<>();
        ValueHelpBean valueHelp;
        ODataProperty property;
        ODataPropMap properties;
        if (!TextUtils.isEmpty(defaultValue)) {
            valueHelp = new ValueHelpBean();
            valueHelp.setDisplayData(defaultValue);
            valueHelpArrayList.add(0, valueHelp);
        }
        try {
            if (entities != null && !entities.isEmpty()) {

                for (ODataEntity entity : entities) {
                    valueHelp = new ValueHelpBean();
                    properties = entity.getProperties();
                    property = properties.get(Constants.PropName);
                    valueHelp.setPropName((String) property.getValue());
                    property = properties.get(Constants.ID);
                    valueHelp.setID((String) property.getValue());
                    if (propName.equalsIgnoreCase(valueHelp.getPropName()) && !TextUtils.isEmpty(valueHelp.getID())) {
                        property = properties.get(Constants.Description);
                        valueHelp.setDescription((String) property.getValue());
                       /* if(!TextUtils.isEmpty(typeSetVal)) {
                            String mStrTypeVal = Constants.getConfigTypeIndicator(Constants.ConfigTypsetTypeValues,
                                    Constants.TypeValue, Constants.Types, propIDVal, Constants.Typeset, typeSetVal);
                            valueHelp.setTypeValue(mStrTypeVal);
                        }*/
                        property = properties.get(Constants.IsDefault);
                        Boolean boolVal = null;
                        try {
                            boolVal = (Boolean) property.getValue();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        valueHelp.setIsDefault(boolVal.toString());
                        property = properties.get(Constants.ParentID);
                        valueHelp.setParentID((String) property.getValue());
                        valueHelp.setDisplayData(valueHelp.getID() + " - " + valueHelp.getDescription());
                       /* configVal[5][incVal] = configVal[0][incVal] + " - " + configVal[1][incVal];
                        incVal++;*/
                        valueHelpArrayList.add(valueHelp);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new OfflineODataStoreException(e);
        }
        /*if (valueHelpArrayList.size() != 1) {
            valueHelp = new ValueHelpBean();
            valueHelp.setDisplayData(Constants.None);
            valueHelpArrayList.add(0, valueHelp);
        }*/
        return valueHelpArrayList;

    }

    /*get data from value help*/
    public static ArrayList<ValueHelpBean> getConfigListFromValueHelp(List<ODataEntity> entities, String propName) throws OfflineODataStoreException {
        //Check if the offline oData store is initialized
        ArrayList<ValueHelpBean> valueHelpArrayList = new ArrayList<>();
        ValueHelpBean valueHelp;
        ODataProperty property;
        ODataPropMap properties;

        try {
            if (entities != null && !entities.isEmpty()) {

                for (ODataEntity entity : entities) {
                    valueHelp = new ValueHelpBean();
                    properties = entity.getProperties();
                    property = properties.get(Constants.PropName);
                    valueHelp.setPropName((String) property.getValue());
                    property = properties.get(Constants.ID);
                    valueHelp.setID((String) property.getValue());
                    if (propName.equalsIgnoreCase(valueHelp.getPropName()) && !TextUtils.isEmpty(valueHelp.getID())) {
                        property = properties.get(Constants.Description);
                        valueHelp.setDescription((String) property.getValue());
                       /* if(!TextUtils.isEmpty(typeSetVal)) {
                            String mStrTypeVal = Constants.getConfigTypeIndicator(Constants.ConfigTypsetTypeValues,
                                    Constants.TypeValue, Constants.Types, propIDVal, Constants.Typeset, typeSetVal);
                            valueHelp.setTypeValue(mStrTypeVal);
                        }*/
                        property = properties.get(Constants.IsDefault);
                        Boolean boolVal = null;
                        try {
                            boolVal = (Boolean) property.getValue();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        valueHelp.setIsDefault(boolVal.toString());
                        property = properties.get(Constants.ParentID);
                        valueHelp.setParentID((String) property.getValue());
                        valueHelp.setDisplayData(valueHelp.getID() + " - " + valueHelp.getDescription());
                       /* configVal[5][incVal] = configVal[0][incVal] + " - " + configVal[1][incVal];
                        incVal++;*/
                        valueHelpArrayList.add(valueHelp);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new OfflineODataStoreException(e);
        }
        if (valueHelpArrayList.size() != 1) {
            valueHelp = new ValueHelpBean();
            valueHelp.setDisplayData(Constants.None);
            valueHelpArrayList.add(0, valueHelp);
        }
        return valueHelpArrayList;

    }

    public static ArrayList<CustomerPartnerFunctionBean> getCustomerPartnerDataFunction(List<ODataEntity> entities) throws OfflineODataStoreException {
        ArrayList<CustomerPartnerFunctionBean> customerPartnerFunctionBeanArrayList = new ArrayList<>();
        CustomerPartnerFunctionBean partnerFunctions;
        ODataProperty property;
        ODataPropMap properties;
        try {
            if (entities != null && !entities.isEmpty()) {
                int entitiesSize = entities.size();
                if (entitiesSize > 1) {
                    partnerFunctions = new CustomerPartnerFunctionBean();
                    partnerFunctions.setDisplayName(Constants.None);
                    customerPartnerFunctionBeanArrayList.add(partnerFunctions);
                }
                for (ODataEntity entity : entities) {
                    partnerFunctions = new CustomerPartnerFunctionBean();
                    properties = entity.getProperties();
                    property = properties.get(Constants.PartnerVendorNo);
                    partnerFunctions.setPartnerVendorNo((String) property.getValue());
                    property = properties.get(Constants.PartnerVendorName);
                    partnerFunctions.setPartnerVendorName((String) property.getValue());
                    property = properties.get(Constants.PartnerCustomerNo);
                    partnerFunctions.setPartnerCustomerNo((String) property.getValue());
                    property = properties.get(Constants.PartnerCustomerName);
                    partnerFunctions.setPartnerCustomerName((String) property.getValue());
                    property = properties.get(Constants.PartnerFunctionID);
                    partnerFunctions.setPartnerFunctionID((String) property.getValue());
                    partnerFunctions.setDisplayName(partnerFunctions.getPartnerCustomerNo() + " - " + partnerFunctions.getPartnerCustomerName());
                    partnerFunctions = getCustomerPartnerFunction(properties, partnerFunctions);
                    customerPartnerFunctionBeanArrayList.add(partnerFunctions);
                }
            } else {
                partnerFunctions = new CustomerPartnerFunctionBean();
                partnerFunctions.setDisplayName(Constants.None);
                customerPartnerFunctionBeanArrayList.add(partnerFunctions);
            }
        } catch (Exception e) {
            throw new OfflineODataStoreException(e);
        }
        return customerPartnerFunctionBeanArrayList;
    }

    private static CustomerPartnerFunctionBean getCustomerPartnerFunction(ODataPropMap properties, CustomerPartnerFunctionBean partnerFunctions) {
        ODataProperty property;
        property = properties.get(Constants.PartnerFunctionDesc);
        partnerFunctions.setPartnerFunctionDesc(property.getValue().toString());
        property = properties.get(Constants.PartnerCustomerNo);
        partnerFunctions.setPartnerCustomerNo(property.getValue().toString());
        property = properties.get(Constants.PartnerCustomerName);
        if (property != null)
            partnerFunctions.setPartnerCustomerName(property.getValue().toString());
        property = properties.get(Constants.VendorNo);
        if (property != null)
            partnerFunctions.setPartnerVendorNo(property.getValue().toString());
        property = properties.get(Constants.VendorName);
        if (property != null)
            partnerFunctions.setPartnerVendorName(property.getValue().toString());
        property = properties.get(Constants.PersonnelNo);
        if (property != null)
            partnerFunctions.setPersonnelNo(property.getValue().toString());
        property = properties.get(Constants.PersonnelName);
        if (property != null)
            partnerFunctions.setPersonnelName(property.getValue().toString());
        property = properties.get(Constants.Address1);
        if (property != null)
            partnerFunctions.setAddress1(property.getValue().toString());
        property = properties.get(Constants.Address2);
        if (property != null)
            partnerFunctions.setAddress2(property.getValue().toString());
        property = properties.get(Constants.Address3);
        if (property != null)
            partnerFunctions.setAddress3(property.getValue().toString());
        property = properties.get(Constants.Address4);
        if (property != null)
            partnerFunctions.setAddress4(property.getValue().toString());
        property = properties.get(Constants.District);
        if (property != null)
            partnerFunctions.setDistrict(property.getValue().toString());
        property = properties.get(Constants.City);
        if (property != null)
            partnerFunctions.setCity(property.getValue().toString());
        property = properties.get(Constants.CityID);
        if (property != null)
            partnerFunctions.setCityID(property.getValue().toString());
        property = properties.get(Constants.RegionID);
        if (property != null)
            partnerFunctions.setRegionID(property.getValue().toString());
        property = properties.get(Constants.RegionDesc);
        if (property != null)
            partnerFunctions.setRegionDesc(property.getValue().toString());
        property = properties.get(Constants.CountryID);
        if (property != null)
            partnerFunctions.setCountryID(property.getValue().toString());
        property = properties.get(Constants.CountryDesc);
        if (property != null)
            partnerFunctions.setCountryDesc(property.getValue().toString());
        property = properties.get(Constants.PostalCode);
        if (property != null)
            partnerFunctions.setPostalCode(property.getValue().toString());
        property = properties.get(Constants.Mobile1);
        if (property != null)
            partnerFunctions.setMobile1(property.getValue().toString());
        property = properties.get(Constants.Mobile2);
        if (property != null)
            partnerFunctions.setMobile2(property.getValue().toString());
        property = properties.get(Constants.EmailID);
        if (property != null)
            partnerFunctions.setEmailID(property.getValue().toString());

        try {
            partnerFunctions.setCompleteAddress(getAddress(partnerFunctions));
        } catch (Exception e) {
            partnerFunctions.setCompleteAddress("");
            e.printStackTrace();
        }
        return partnerFunctions;
    }

    public static String getAddress(CustomerPartnerFunctionBean partnerFunctions) {
        String address = "";
        String add1 = partnerFunctions.getAddress1().replaceAll(","," ");
        String add2 = partnerFunctions.getAddress2().replaceAll(","," ");
        String add3 = partnerFunctions.getAddress3().replaceAll(","," ");
        String add4 = partnerFunctions.getAddress4().replaceAll(","," ");
        String dist = partnerFunctions.getDistrict().replaceAll(","," ");
        String city = partnerFunctions.getCity().replaceAll(","," ");
        try {
            if (!TextUtils.isEmpty(add1)) {
                if (!TextUtils.isEmpty(address)) {

                    address = address + "," + add1;

                } else {
                    address = address + add1;
                }
            } else {
                address = address;
            }

            if (!TextUtils.isEmpty(add2)) {
                if (!TextUtils.isEmpty(address)) {

                    address = address + "," + add2;

                } else {
                    address = address + add2;
                }
            } else {
                address = address;
            }

            if (!TextUtils.isEmpty(add3)) {
                if (!TextUtils.isEmpty(address)) {

                    address = address + "," + add3;

                } else {
                    address = address + add3;
                }
            } else {
                address = address;
            }

            if (!TextUtils.isEmpty(add4)) {
                if (!TextUtils.isEmpty(address)) {

                    address = address + "," + add4;

                } else {
                    address = address + add4;
                }
            } else {
                address = address;
            }

            if (!TextUtils.isEmpty(dist)) {
                if (!TextUtils.isEmpty(address)) {

                    address = address + "," + dist;

                } else {
                    address = address + dist;
                }
            } else {
                address = address;
            }

            if (!TextUtils.isEmpty(city)) {
                if (!TextUtils.isEmpty(address)) {

                    address = address + "," + city;

                } else {
                    address = address + city;
                }
            } else {
                address = address;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return address;
    }

    public static ArrayList<DefaultValueBean> getConfigListWithDefaultValAndNone(List<ODataEntity> entities) throws OfflineODataStoreException {
        ArrayList<DefaultValueBean> defaultValueBeenList = new ArrayList<>();
        //Check if the offline oData store is initialized
        DefaultValueBean defaultValueBeen;
        ODataProperty property;
        ODataPropMap properties;
        String unloadingPoint = "";
        try {
            if (entities != null && !entities.isEmpty()) {
                for (ODataEntity entity : entities) {
                    defaultValueBeen = new DefaultValueBean();
                    properties = entity.getProperties();
                    property = properties.get(Constants.SalesArea);
                    try {
                        String saleArea = (String) property.getValue();
                        String[] divisionArea = saleArea.split("/");
                        defaultValueBeen.setDivision(divisionArea[2]);
                        defaultValueBeen.setDistChannelID(divisionArea[1]);
                        defaultValueBeen.setSalesOrgID(divisionArea[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    defaultValueBeen.setSalesArea((String) property.getValue());
                    property = properties.get(Constants.SalesAreaDesc);
                    defaultValueBeen.setSalesAreaDesc((String) property.getValue());
                    property = properties.get(Constants.ShippingConditionID);
                    defaultValueBeen.setShippingConditionID((String) property.getValue());
                    property = properties.get(Constants.ShippingConditionDesc);
                    defaultValueBeen.setShippingConditionDesc((String) property.getValue());
                    property = properties.get(Constants.DeliveringPlantID);
                    defaultValueBeen.setDeliveringPlantID((String) property.getValue());
                    property = properties.get(Constants.DeliveringPlantDesc);
                    defaultValueBeen.setDeliveringPlantDesc((String) property.getValue());
                    property = properties.get(Constants.TransportationZoneID);
                    defaultValueBeen.setTransportationZoneID((String) property.getValue());
                    property = properties.get(Constants.TransportationZoneDesc);
                    defaultValueBeen.setTransportationZoneDesc((String) property.getValue());
                    property = properties.get(Constants.Incoterms1ID);
                    defaultValueBeen.setIncoterms1ID((String) property.getValue());
                    property = properties.get(Constants.Incoterms1Desc);
                    defaultValueBeen.setIncoterms1Desc((String) property.getValue());
                    property = properties.get(Constants.Incoterms2);
                    defaultValueBeen.setIncoterms2((String) property.getValue());
                    property = properties.get(Constants.PaymentTermID);
                    defaultValueBeen.setPaymentTermID((String) property.getValue());
                    property = properties.get(Constants.PaymentTermDesc);
                    defaultValueBeen.setPaymentTermDesc((String) property.getValue());
                    property = properties.get(Constants.CreditControlAreaID);
                    defaultValueBeen.setCreditControlAreaID((String) property.getValue());
                    property = properties.get(Constants.CreditControlAreaDesc);
                    defaultValueBeen.setCreditControlAreaDesc((String) property.getValue());
                    property = properties.get(Constants.SalesOfficeID);
                    defaultValueBeen.setSalesOfficeID((String) property.getValue());
                    property = properties.get(Constants.SalesOfficeDesc);
                    defaultValueBeen.setSalesOfficeDesc((String) property.getValue());
                    property = properties.get(Constants.CustomerGrpID);
                    defaultValueBeen.setCustomerGrpID((String) property.getValue());
                    property = properties.get(Constants.DeliveringPlantDesc);
                    defaultValueBeen.setDeliveringPlantDesc((String) property.getValue());
                    property = properties.get(Constants.DeliveringPlantID);
                    defaultValueBeen.setDeliveringPlantID((String) property.getValue());
                    defaultValueBeen.setUnloadingPoint(unloadingPoint);
                    defaultValueBeen.setDisplayDropDown(defaultValueBeen.getSalesArea() + " - " + defaultValueBeen.getSalesAreaDesc());
                    defaultValueBeenList.add(defaultValueBeen);
                }
            }
        } catch (Exception e) {
            throw new OfflineODataStoreException(e);
        }
        if (defaultValueBeenList.size() != 1) {
            defaultValueBeen = new DefaultValueBean();
            defaultValueBeen.setDisplayDropDown(Constants.None);
            defaultValueBeenList.add(0, defaultValueBeen);
        }
        return defaultValueBeenList;

    }

    public static ArrayList<DefaultValueBean> getCustomerPartnerWithDefaultValAndNone(List<ODataEntity> entities) throws OfflineODataStoreException {
        ArrayList<DefaultValueBean> defaultValueBeenList = new ArrayList<>();
        ArrayList<String> tempAll = new ArrayList<>();
        //Check if the offline oData store is initialized
        DefaultValueBean defaultValueBeen;
        ODataProperty property;
        ODataPropMap properties;
        String unloadingPoint = "";
        try {
            if (entities != null && !entities.isEmpty()) {
                for (ODataEntity entity : entities) {
                    defaultValueBeen = new DefaultValueBean();
                    properties = entity.getProperties();
                    property = properties.get(Constants.SalesArea);
                    defaultValueBeen.setSalesArea((String) property.getValue());
                    property = properties.get(Constants.SalesAreaDesc);
                    defaultValueBeen.setSalesAreaDesc((String) property.getValue());
                    defaultValueBeen.setDisplayDropDown(defaultValueBeen.getSalesArea() + " - " + defaultValueBeen.getSalesAreaDesc());
                    if(!tempAll.contains(defaultValueBeen.getSalesArea())) {
                        defaultValueBeenList.add(defaultValueBeen);
                        tempAll.add(defaultValueBeen.getSalesArea());
                    }

                }
            }
        } catch (Exception e) {
            throw new OfflineODataStoreException(e);
        }
        if (defaultValueBeenList.size() != 1) {
            defaultValueBeen = new DefaultValueBean();
            defaultValueBeen.setDisplayDropDown(Constants.None);
            defaultValueBeenList.add(0, defaultValueBeen);
        }
        return defaultValueBeenList;

    }

    public static ArrayList<SOTextBean> getSOTextList(ODataRequestExecution oDataRequestExecution) throws com.arteriatech.mutils.common.OnlineODataStoreException {
        ArrayList<SOTextBean> soTextBeanArrayList = new ArrayList<>();
        ODataPayload payload = ((ODataResponseSingle) oDataRequestExecution.getResponse()).getPayload();
        SOTextBean soListBean;
        if (payload != null && payload instanceof ODataEntity) {
            ODataEntity oEntity = (ODataEntity) payload;
            soListBean = getSOTextBean(oEntity);
            soTextBeanArrayList.add(soListBean);
        }
        return soTextBeanArrayList;
    }

    public static SOTextBean getSOTextBean(ODataEntity entity) throws com.arteriatech.mutils.common.OnlineODataStoreException {
        SOTextBean soBean = new SOTextBean();
        ODataProperty property;
        ODataPropMap properties;
        properties = entity.getProperties();
        property = properties.get(Constants.SONo);
        soBean.setSONo(property.getValue().toString());
        property = properties.get(Constants.TextID);
        soBean.setTextID(property.getValue().toString());
        property = properties.get(Constants.TextIDDesc);
        soBean.setTextIDDesc(property.getValue().toString());
        property = properties.get(Constants.ItemNo);
        soBean.setItemNo(property.getValue().toString());
        property = properties.get(Constants.Text);
        soBean.setText(property.getValue().toString());

        return soBean;
    }

    /*get SO details list*/
    public static List<ODataEntity> getOdataEntity(String odataQry) throws com.arteriatech.mutils.common.OnlineODataStoreException {
       /* OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();
        SalesApprovalBean appbean = null;
        ODataEntity entity = null;
        List<ODataEntity> entities = null;
        if (store != null) {
            try {


                ODataRequestParamBatch requestParamBatch = new ODataRequestParamBatchDefaultImpl();
                ODataRequestParamSingle batchItem = new ODataRequestParamSingleDefaultImpl();
                batchItem.setResourcePath(odataQry);
                batchItem.setMode(ODataRequestParamSingle.Mode.Read);
                final Map<String, String> createHeaders = new HashMap<>();
                batchItem.getCustomHeaders().putAll(createHeaders);
                requestParamBatch.add(batchItem);
                ODataResponse oDataResponse = store.executeRequest(requestParamBatch);
                if (oDataResponse instanceof ODataResponseBatchDefaultImpl) {
                    ODataResponseBatch batchResponse = (ODataResponseBatch) oDataResponse;
                    List<ODataResponseBatchItem> responses = batchResponse.getResponses();
                    for (ODataResponseBatchItem response : responses) {
                        // Check if batch item is a change set
                        if (response instanceof ODataResponseChangeSetDefaultImpl) {
                            // Todo here multiple batch request will come
                        } else {
                            ODataResponseSingle oDataResponseSingle = (ODataResponseSingleDefaultImpl) response;
                            // TODO Check if batch item is a single READ request


                            try {
                                ODataEntitySet payload = (ODataEntitySet) ((ODataResponseSingle) oDataResponseSingle).getPayload();
                                entities = payload.getEntities();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }

            } catch (Exception e) {
                throw new com.arteriatech.mutils.common.OnlineODataStoreException(e);
            }
        }*/
        return new ArrayList<>();
    }

    public static UserLoginBean getUserLogin(String resourcePath) throws OnlineODataStoreException {

        UserLoginBean userLoginBean = null;
        /*final boolean[] isDataAvailable = {false};
        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();
        ODataProperty property;
        ODataPropMap properties;
        if (store != null) {
            try {

                ODataRequestParamBatch requestParamBatch = new ODataRequestParamBatchDefaultImpl();

                ODataRequestParamSingle batchItem = new ODataRequestParamSingleDefaultImpl();

                batchItem.setResourcePath(resourcePath);

                batchItem.setMode(ODataRequestParamSingle.Mode.Read);

                requestParamBatch.add(batchItem);

                // Send request synchronously

                ODataResponse oDataResponse = store.executeRequest(requestParamBatch);

                // Get batch response

                if (oDataResponse instanceof ODataResponseBatchDefaultImpl) {

                    ODataResponseBatch batchResponse = (ODataResponseBatch) oDataResponse;

                    List<ODataResponseBatchItem> responses = batchResponse.getResponses();

                    for (ODataResponseBatchItem response : responses) {
                        // Check if batch item is a change set
                        if (response instanceof ODataResponseChangeSetDefaultImpl) {
                            // Todo here multiple batch request will come
                        } else {
                            ODataResponseSingle oDataResponseSingle = (ODataResponseSingleDefaultImpl) response;
                            ODataPayload oDataPayload = oDataResponseSingle.getPayload();
                            if (oDataPayload != null) {
                                if (oDataPayload instanceof ODataError) {
                                    ODataError oError = (ODataError) oDataPayload;
                                    String uiMessage = oError.getMessage();
                                } else {
                                    // TODO Check if batch item is a single READ request
//                                    ODataEntitySet feed = (ODataEntitySet) oDataResponseSingle.getPayload();
                                    // Get the list of ODataEntity
//                                    List<ODataEntity> entities = feed.getEntities();

                                    ODataEntity entity = (ODataEntity) oDataResponseSingle.getPayload();
//                                    for (ODataEntity entity : entities) {
                                    properties = entity.getProperties();
                                    userLoginBean = new UserLoginBean();
                                    property = properties.get(Constants.LoginID);
                                    userLoginBean.setLoginID(property.getValue().toString());
                                    property = properties.get(Constants.Application);
                                    userLoginBean.setApplication(property.getValue().toString());
                                    property = properties.get(Constants.ERPLoginID);
                                    userLoginBean.setERPLoginID(property.getValue().toString());
                                    property = properties.get(Constants.RoleID);
                                    userLoginBean.setRoleID(property.getValue().toString());
                                    property = properties.get(Constants.LoginName);
                                    userLoginBean.setLoginName(property.getValue().toString());
                                    property = properties.get(Constants.RoleDesc);
                                    userLoginBean.setRoleDesc(property.getValue().toString());
                                    property = properties.get(Constants.RoleCatID);
                                    userLoginBean.setRoleCatID(property.getValue().toString());
                                    property = properties.get(Constants.RoleCatDesc);
                                    userLoginBean.setRoleCatDesc(property.getValue().toString());
                                    property = properties.get(Constants.IsActive);
                                    userLoginBean.setIsActive(property.getValue().toString());
                                    property = properties.get(Constants.UserFunction1);
                                    userLoginBean.setUserFunction1ID(property.getValue().toString());
                                    property = properties.get(Constants.UserFunction1Desc);
                                    userLoginBean.setUserFunction1Desc(property.getValue().toString());
                                    property = properties.get(Constants.UserFunction2);
                                    userLoginBean.setUserFunction2ID(property.getValue().toString());
                                    property = properties.get(Constants.UserFunction2Desc);
                                    userLoginBean.setUserFunction2Desc(property.getValue().toString());
//                                    }
                                }
                            }
                            isDataAvailable[0] = true;

                        }

                    }
                }

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        } else {
            userLoginBean = null;
        }

        while (!isDataAvailable[0]) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isDataAvailable[0] = false;*/

        return userLoginBean;
    }

    private static ODataEntity getPartnerFunctionEntity(ODataEntity newTextEntity, Hashtable<String, String> headerText) {
        newTextEntity.getProperties().put(Constants.SONo,
                new ODataPropertyDefaultImpl(Constants.SONo, headerText.get(Constants.SONo)));
        newTextEntity.getProperties().put(Constants.PartnerFunctionID,
                new ODataPropertyDefaultImpl(Constants.PartnerFunctionID, headerText.get(Constants.PartnerFunctionID)));
        newTextEntity.getProperties().put(Constants.PartnerFunctionDesc,
                new ODataPropertyDefaultImpl(Constants.PartnerFunctionDesc, headerText.get(Constants.PartnerFunctionDesc)));
        newTextEntity.getProperties().put(Constants.CustomerNo,
                new ODataPropertyDefaultImpl(Constants.CustomerNo, headerText.get(Constants.CustomerNo)));
        newTextEntity.getProperties().put(Constants.CustomerName,
                new ODataPropertyDefaultImpl(Constants.CustomerName, headerText.get(Constants.CustomerName)));
        newTextEntity.getProperties().put(Constants.Address1,
                new ODataPropertyDefaultImpl(Constants.Address1, headerText.get(Constants.Address1)));
        newTextEntity.getProperties().put(Constants.Address2,
                new ODataPropertyDefaultImpl(Constants.Address2, headerText.get(Constants.Address2)));
        newTextEntity.getProperties().put(Constants.Address3,
                new ODataPropertyDefaultImpl(Constants.Address3, headerText.get(Constants.Address3)));
        newTextEntity.getProperties().put(Constants.Address4,
                new ODataPropertyDefaultImpl(Constants.Address4, headerText.get(Constants.Address4)));
        newTextEntity.getProperties().put(Constants.District,
                new ODataPropertyDefaultImpl(Constants.District, headerText.get(Constants.District)));
        newTextEntity.getProperties().put(Constants.CityID,
                new ODataPropertyDefaultImpl(Constants.CityID, headerText.get(Constants.CityID)));
        newTextEntity.getProperties().put(Constants.RegionID,
                new ODataPropertyDefaultImpl(Constants.RegionID, headerText.get(Constants.RegionID)));
        newTextEntity.getProperties().put(Constants.RegionDesc,
                new ODataPropertyDefaultImpl(Constants.RegionDesc, headerText.get(Constants.RegionDesc)));
        newTextEntity.getProperties().put(Constants.CountryID,
                new ODataPropertyDefaultImpl(Constants.CountryID, headerText.get(Constants.CountryID)));
        newTextEntity.getProperties().put(Constants.CountryDesc,
                new ODataPropertyDefaultImpl(Constants.CountryDesc, headerText.get(Constants.CountryDesc)));
        newTextEntity.getProperties().put(Constants.PostalCode,
                new ODataPropertyDefaultImpl(Constants.PostalCode, headerText.get(Constants.PostalCode)));

        return newTextEntity;
    }

    private static ODataEntity getTextEntity(ODataEntity newTextEntity, HashMap<String, String> subTextSingleItem) {
        newTextEntity.getProperties().put(Constants.SONo,
                new ODataPropertyDefaultImpl(Constants.SONo, subTextSingleItem.get(Constants.SONo)));
        newTextEntity.getProperties().put(Constants.Text,
                new ODataPropertyDefaultImpl(Constants.Text, subTextSingleItem.get(Constants.Text)));
        newTextEntity.getProperties().put(Constants.TextID,
                new ODataPropertyDefaultImpl(Constants.TextID, subTextSingleItem.get(Constants.TextID)));
        if (subTextSingleItem.get(Constants.LoginID) != null) {
            newTextEntity.getProperties().put(Constants.LoginID,
                    new ODataPropertyDefaultImpl(Constants.LoginID, subTextSingleItem.get(Constants.LoginID)));
        }
        return newTextEntity;
    }

    /* so item schedules*/
    private static ODataEntity getSOItemScheduleEntity(ODataEntity newSubItemEntity, HashMap<String, String> subSingleItem) {
        newSubItemEntity.getProperties().put(Constants.DelSchLineNo,
                new ODataPropertyDefaultImpl(Constants.DelSchLineNo, subSingleItem.get(Constants.DelSchLineNo)));
        newSubItemEntity.getProperties().put(Constants.ItemNo,
                new ODataPropertyDefaultImpl(Constants.ItemNo, subSingleItem.get(Constants.ItemNo)));
        newSubItemEntity.getProperties().put(Constants.DeliveryDate,
                new ODataPropertyDefaultImpl(Constants.DeliveryDate, UtilConstants.convertDateFormat(subSingleItem.get(Constants.DeliveryDate))));
        newSubItemEntity.getProperties().put(Constants.MaterialNo,
                new ODataPropertyDefaultImpl(Constants.MaterialNo, subSingleItem.get(Constants.MaterialNo)));
        newSubItemEntity.getProperties().put(Constants.OrderQty,
                new ODataPropertyDefaultImpl(Constants.OrderQty, BigDecimal.valueOf(Double.parseDouble(subSingleItem.get(Constants.OrderQty)))));

        newSubItemEntity.getProperties().put(Constants.ConfirmedQty,
                new ODataPropertyDefaultImpl(Constants.ConfirmedQty, BigDecimal.valueOf(Double.parseDouble(subSingleItem.get(Constants.ConfirmedQty)))));

        newSubItemEntity.getProperties().put(Constants.RequiredQty,
                new ODataPropertyDefaultImpl(Constants.RequiredQty, BigDecimal.valueOf(Double.parseDouble(subSingleItem.get(Constants.RequiredQty)))));

        newSubItemEntity.getProperties().put(Constants.UOM,
                new ODataPropertyDefaultImpl(Constants.UOM, subSingleItem.get(Constants.UOM)));
        newSubItemEntity.getProperties().put(Constants.SONo,
                new ODataPropertyDefaultImpl(Constants.SONo, subSingleItem.get(Constants.SONo)));
        if (subSingleItem.get(Constants.RequirementDate) != null) {
            newSubItemEntity.getProperties().put(Constants.RequirementDate,
                    new ODataPropertyDefaultImpl(Constants.RequirementDate, UtilConstants.convertDateFormat(subSingleItem.get(Constants.RequirementDate))));
        }
        if (subSingleItem.get(Constants.TransportationPlanDate) != null) {
            newSubItemEntity.getProperties().put(Constants.TransportationPlanDate,
                    new ODataPropertyDefaultImpl(Constants.TransportationPlanDate, UtilConstants.convertDateFormat(ConstantsUtils.convertDateForStore(subSingleItem.get(Constants.TransportationPlanDate)))));
        }
        if (subSingleItem.get(Constants.MaterialAvailDate) != null) {
            newSubItemEntity.getProperties().put(Constants.MaterialAvailDate,
                    new ODataPropertyDefaultImpl(Constants.MaterialAvailDate, UtilConstants.convertDateFormat(ConstantsUtils.convertDateForStore(subSingleItem.get(Constants.MaterialAvailDate)))));
        }

//                            newSubItemEntity.getProperties().put(Constants.ScheduleLineCatID,
//                                    new ODataPropertyDefaultImpl(Constants.ScheduleLineCatID, subSingleItem.get(Constants.ScheduleLineCatID)));
        return newSubItemEntity;
    }

    public static ArrayList<SOItemBean> getMaterialPriceList(JSONArray jsonArray, ArrayList<SOItemBean> soDefaultItemBeanList, int comingFrom, Context mContext) throws OfflineODataStoreException {
        ArrayList<SOItemBean> soItemBeanList = new ArrayList<>();
        SOItemBean soItemBean;
        Log.d("ProductPrice", "requestMaterial:  loop stated");
        ArrayList<String> stringArrayList = new ArrayList<>();
        try {
            if (jsonArray != null && jsonArray.length()>0) {
                //Retrieve the data from the response
                ArrayList<ConfigTypeValues> configTypeValuesList = OfflineManager.checkMaterialCodeDisplay();
                ArrayList<ConfigTypeValues> configTypePriceZero = OfflineManager.checkMaterialPriceZeroDisplay();
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    soItemBean = new SOItemBean();
                    soItemBean.setMatCode(jsonObject.optString(Constants.MaterialNo));
                    if (jsonObject.optString(Constants.LandingPrice) != null) {
                        soItemBean.setLandingPrice(jsonObject.optString(Constants.LandingPrice));
                    }
                    boolean isValidMat = false;
                    if (!configTypePriceZero.isEmpty()) {
                        if (jsonObject.optString(Constants.LandingPrice).compareTo("0") == 1) {
                            isValidMat = true;
                        }
                    } else {
                        isValidMat = true;
                    }
                    if (isValidMat) {
                        if (!stringArrayList.contains(soItemBean.getMatCode())) {
                            if (isCheckedItem(soDefaultItemBeanList, soItemBean)) {
                                if (comingFrom == ConstantsUtils.ADD_MATERIAL) {
                                    soItemBean.setHide(true);
                                }
                                soItemBean.setChecked(true);
                            } else {
                                soItemBean.setChecked(false);
                            }
                            soItemBean.setMatDesc(jsonObject.optString(Constants.MaterialDesc));
                            soItemBean.setUom(jsonObject.optString(Constants.BaseUOM));

                            String baseprice = jsonObject.optString(Constants.BasePrice);
                            soItemBean.setUnitPrice(baseprice + "");
                            soItemBean.setCurrency(jsonObject.optString(Constants.Currency));

                            if (!configTypeValuesList.isEmpty()) {
                                soItemBean.setMatNoAndDesc(mContext.getString(R.string.po_details_display_value, soItemBean.getMatDesc(), soItemBean.getMatCode()));
                            } else {
                                soItemBean.setMatNoAndDesc(soItemBean.getMatDesc());
                            }
                            soItemBean.setSoQty("");

                            //  soItemBean.setMatNoAndDesc(soItemBean.getMatDesc() + soItemBean.getMatCode());
                            soItemBeanList.add(soItemBean);
                            stringArrayList.add(soItemBean.getMatCode());
                        }
                    }
                }
            }
            Log.d("ProductPrice", "requestMaterial:  loop completed");
        } catch (Exception e) {
            throw new OfflineODataStoreException(e);
        }
        Log.d("ProductPrice", "requestMaterial:  sort start");
        Collections.sort(soItemBeanList, new Comparator<SOItemBean>() {
            @Override
            public int compare(SOItemBean one, SOItemBean two) {
                return one.getMatDesc().compareTo(two.getMatDesc());
            }
        });
        Log.d("ProductPrice", "requestMaterial:  sort completed");
        return soItemBeanList;
    }

    /* get so item check list*/
    public static ArrayList<SOItemBean> getMaterialPriceList(List<ODataEntity> entities, ArrayList<SOItemBean> soDefaultItemBeanList, int comingFrom, Context mContext) throws OfflineODataStoreException {
        ArrayList<SOItemBean> soItemBeanList = new ArrayList<>();
        SOItemBean soItemBean;
        ODataProperty property;
        ODataPropMap properties;
        Log.d("ProductPrice", "requestMaterial:  loop stated");
        ArrayList<String> stringArrayList = new ArrayList<>();
        try {
            if (entities != null && !entities.isEmpty()) {
                //Retrieve the data from the response
                ArrayList<ConfigTypeValues> configTypeValuesList = OfflineManager.checkMaterialCodeDisplay();
                ArrayList<ConfigTypeValues> configTypePriceZero = OfflineManager.checkMaterialPriceZeroDisplay();
                for (ODataEntity entity : entities) {
                    properties = entity.getProperties();
                    soItemBean = new SOItemBean();
                    property = properties.get(Constants.MaterialNo);
                    soItemBean.setMatCode((String) property.getValue());
                    property = properties.get(Constants.LandingPrice);
                    BigDecimal bLandingAmount = new BigDecimal("0");
                    if (property != null) {
                        bLandingAmount = (BigDecimal) property.getValue();
                        soItemBean.setLandingPrice(bLandingAmount.toString());
                    }
                    boolean isValidMat = false;
                    if (!configTypePriceZero.isEmpty()) {
                        if (bLandingAmount.compareTo(new BigDecimal("0")) == 1) {
                            isValidMat = true;
                        }
                    } else {
                        isValidMat = true;
                    }
                    if (isValidMat) {
                        if (!stringArrayList.contains(soItemBean.getMatCode())) {
                            if (isCheckedItem(soDefaultItemBeanList, soItemBean)) {
                                if (comingFrom == ConstantsUtils.ADD_MATERIAL) {
                                    soItemBean.setHide(true);
                                }
                                soItemBean.setChecked(true);
                            } else {
                                soItemBean.setChecked(false);
                            }
                            property = properties.get(Constants.MaterialDesc);
                            soItemBean.setMatDesc((String) property.getValue());
                            property = properties.get(Constants.BaseUOM);
                            soItemBean.setUom((String) property.getValue());

                            property = properties.get(Constants.BasePrice);
                            BigDecimal baseprice = (BigDecimal) property.getValue();
                            soItemBean.setUnitPrice(baseprice.doubleValue() + "");
                            property = properties.get(Constants.Currency);
                            soItemBean.setCurrency((String) property.getValue());

                            if (!configTypeValuesList.isEmpty()) {
                                soItemBean.setMatNoAndDesc(mContext.getString(R.string.po_details_display_value, soItemBean.getMatDesc(), soItemBean.getMatCode()));
                            } else {
                                soItemBean.setMatNoAndDesc(soItemBean.getMatDesc());
                            }
                            soItemBean.setSoQty("");

                            //  soItemBean.setMatNoAndDesc(soItemBean.getMatDesc() + soItemBean.getMatCode());
                            soItemBeanList.add(soItemBean);
                            stringArrayList.add(soItemBean.getMatCode());
                        }
                    }
                }
            }
            Log.d("ProductPrice", "requestMaterial:  loop completed");
        } catch (Exception e) {
            throw new OfflineODataStoreException(e);
        }
        Log.d("ProductPrice", "requestMaterial:  sort start");
        Collections.sort(soItemBeanList, new Comparator<SOItemBean>() {
            @Override
            public int compare(SOItemBean one, SOItemBean two) {
                return one.getMatDesc().compareTo(two.getMatDesc());
            }
        });
        Log.d("ProductPrice", "requestMaterial:  sort completed");
        return soItemBeanList;
    }

    public static ArrayList<SOItemBean> getMaterialStockList(List<ODataEntity> entities, ArrayList<SOItemBean> soDefaultItemBeanList, int comingFrom, Context mContext) throws OfflineODataStoreException {
        ArrayList<SOItemBean> soItemBeanList = new ArrayList<>();
        SOItemBean soItemBean;
        ODataProperty property;
        ODataPropMap properties;
        ArrayList<ConfigTypeValues> configVal = OfflineManager.checkMaterialCodeDisplay();
        String qry = Constants.ConfigTypesetTypes + "?$filter=" + Constants.Typeset + " eq '" + Constants.UOMNO0 + "' ";
        HashMap<String, String> mapUOM = OfflineManager.getUOMMapVal(qry);
        try {
            if (entities != null && !entities.isEmpty()) {
                //Retrieve the data from the response
//                ArrayList<ConfigTypeValues> configTypeValuesList = OfflineManager.checkMaterialCodeDisplay();
                for (ODataEntity entity : entities) {
                    properties = entity.getProperties();
                    soItemBean = new SOItemBean();
                    property = properties.get(Constants.MaterialNo);
                    soItemBean.setMatCode((String) property.getValue());
                    property = properties.get(Constants.MaterialDesc);
                    soItemBean.setMatDesc((String) property.getValue());
                    property = properties.get(Constants.UOM);
                    String mStrUom = (String) property.getValue();
                    soItemBean.setUom(mStrUom);
                    property = properties.get(Constants.Unrestricted);
                    BigDecimal depotStk = (BigDecimal) property.getValue();
                    property = properties.get(Constants.PlantID);
                    soItemBean.setPlantId(property.getValue().toString());
                    property = properties.get(Constants.PlantDesc);
                    soItemBean.setPlantDesc(property.getValue().toString());
                    try {
                      /*  if (mapUOM.containsKey(mStrUom))
                            soItemBean.setQuantity(trimQtyDecimalPlace(depotStk.doubleValue() + ""));
                        else
                            soItemBean.setQuantity(depotStk.doubleValue() + "");*/
                        soItemBean.setQuantity(ConstantsUtils.checkNoUOMZero(mStrUom, depotStk.toString()));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!configVal.isEmpty()) {
                        soItemBean.setMatNoAndDesc(mContext.getString(R.string.po_details_display_value, soItemBean.getMatDesc(), soItemBean.getMatCode()));
                    } else {
                        soItemBean.setMatNoAndDesc(soItemBean.getMatDesc());
                    }

                    soItemBean.setSearchField(soItemBean.getMatDesc() + soItemBean.getMatCode() + soItemBean.getPlantId() + soItemBean.getPlantDesc());
                    soItemBean.setSoQty("");

                    soItemBeanList.add(soItemBean);
                }
            }
        } catch (Exception e) {
            throw new OfflineODataStoreException(e);
        }

        return soItemBeanList;
    }

    public static ArrayList<MTPApprovalBean> getMTPApprovalList(List<ODataEntity> entities) {
        ArrayList<MTPApprovalBean> arrayList = new ArrayList<>();
        MTPApprovalBean mtpApprovalBean;
        ODataProperty property;
        ODataPropMap properties;
        if (entities != null) {
            for (ODataEntity entity : entities) {
                mtpApprovalBean = new MTPApprovalBean();
                properties = entity.getProperties();
                try {
                    property = properties.get(Constants.InstanceID);
                    mtpApprovalBean.setInstanceID(property.getValue().toString());

                    property = properties.get(Constants.Initiator);
                    mtpApprovalBean.setInitiator(property.getValue().toString());

                    property = properties.get(Constants.EntityAttribute4);
                    String mStrEntAttr4 = property.getValue().toString();
                    mtpApprovalBean.setEntityAttribute4(mStrEntAttr4);



                   /* property = properties.get(Constants.EntityDate1);
                    String convertDateFormat = UtilConstants.convertCalenderToStringFormat((GregorianCalendar) property.getValue());
                    mtpApprovalBean.setEntityDate1(convertDateFormat);*/
                    /*property = properties.get(Constants.EntityKeyID);
                    mtpApprovalBean.setEntityKeyID(property.getValue().toString());*/
                    property = properties.get(Constants.EntityKey);
                    mtpApprovalBean.setEntityKey(property.getValue().toString());/*
                    property = properties.get(Constants.EntityKeyDesc);
                    mtpApprovalBean.setEntityKeyDesc(property.getValue().toString());*//*
                    property = properties.get(Constants.PriorityNumber);
                    mtpApprovalBean.setPriorityNumber(property.getValue().toString());*/
                    property = properties.get(Constants.EntityAttribute1);
                    mtpApprovalBean.setEntityAttribute1(UtilConstants.removeLagingZeros(property.getValue().toString()));
                    property = properties.get(Constants.EntityAttribute5);
                    mtpApprovalBean.setEntityAttribute5(property.getValue().toString());
                    property = properties.get(Constants.EntityAttribute6);
                    mtpApprovalBean.setEntityAttribute6(ConstantsUtils.getMonthMMM(property.getValue().toString()));
                    property = properties.get(Constants.EntityAttribute7);
                    mtpApprovalBean.setEntityAttribute7(property.getValue().toString());
//                    mtpApprovalBean.setSearchText(soListBean.getSONo()+soListBean.getCustomerName());
                    /*property = properties.get(Constants.EntityType);
                    mtpApprovalBean.setEntityType(property.getValue().toString());*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!mtpApprovalBean.getEntityAttribute4().equalsIgnoreCase("01")) {
                    arrayList.add(mtpApprovalBean);
                }

            }
        }

        return arrayList;
    }

    public static ArrayList<MTPApprovalBean> getMTPApprovalList(JSONArray jsonArray) {
        ArrayList<MTPApprovalBean> arrayList = new ArrayList<>();
        MTPApprovalBean mtpApprovalBean;
        if (jsonArray != null) {
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    mtpApprovalBean = new MTPApprovalBean();
                    try {
                        mtpApprovalBean.setInstanceID(jsonObject.optString(Constants.InstanceID));

                        mtpApprovalBean.setInitiator(jsonObject.optString(Constants.Initiator));

                        String mStrEntAttr4 = jsonObject.optString(Constants.EntityAttribute4);
                        mtpApprovalBean.setEntityAttribute4(mStrEntAttr4);

                        mtpApprovalBean.setEntityKey(jsonObject.optString(Constants.EntityKey));

                        mtpApprovalBean.setEntityAttribute1(UtilConstants.removeLagingZeros(jsonObject.optString(Constants.EntityAttribute1)));
                        mtpApprovalBean.setEntityAttribute5(jsonObject.optString(Constants.EntityAttribute5));
                        mtpApprovalBean.setEntityAttribute6(ConstantsUtils.getMonthMMM(jsonObject.optString(Constants.EntityAttribute6)));
                        mtpApprovalBean.setEntityAttribute7(jsonObject.optString(Constants.EntityAttribute7));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!mtpApprovalBean.getEntityAttribute4().equalsIgnoreCase("01")) {
                        arrayList.add(mtpApprovalBean);
                    }

                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        return arrayList;
    }

    public static void createMTP(Hashtable<String, String> tableHdr, ArrayList<HashMap<String, String>> itemtable, UIListener uiListener, OnlineODataInterface onlineODataInterface) throws OnlineODataStoreException {
        /*OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                if (TextUtils.isEmpty(tableHdr.get(Constants.IS_UPDATE))) {
                    ODataEntity mtpEntity = createMTPEntity(tableHdr, itemtable, store);
                    OnlineRequestListener mtpListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);
                    ODataRequestParamSingle feedbackReq = new ODataRequestParamSingleDefaultImpl();
                    feedbackReq.setMode(ODataRequestParamSingle.Mode.Create);
                    feedbackReq.setResourcePath(mtpEntity.getResourcePath());
                    feedbackReq.setPayload(mtpEntity);
                    store.scheduleRequest(feedbackReq, mtpListener);
                } else {
                    updateMTP(tableHdr, itemtable, onlineODataInterface);
                  *//*  OnlineRequestListener mtpListener = new OnlineRequestListener(Operation.Update.getValue(), uiListener);
                    ODataRequestParamSingle updateReq = new ODataRequestParamSingleDefaultImpl();
                    updateReq.setMode(ODataRequestParamSingle.Mode.Update);
                    updateReq.setResourcePath(mtpEntity.getResourcePath());
                    updateReq.setPayload(mtpEntity);
                    store.scheduleRequest(updateReq, mtpListener);*//*
                }

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }*/
    }

    /*public static ODataEntity createMTPEntity(Hashtable<String, String> hashtable, ArrayList<HashMap<String, String>> itemhashtable, OnlineODataStore store) throws ODataParserException {
        ODataEntity newHeaderEntity = null;
        ODataEntity newItemEntity = null;
        ArrayList<ODataEntity> tempArray = new ArrayList();
        ArrayList<ODataEntity> tempTextArray = new ArrayList();
        ODataEntity newTextEntity = null;
        try {
            if (hashtable != null) {
                newHeaderEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.RouteScheduleEntity);
                if (TextUtils.isEmpty(hashtable.get(Constants.IS_UPDATE))) {
                    newHeaderEntity.setResourcePath(Constants.RouteSchedules, Constants.RouteSchedules);
                } else {
                    newHeaderEntity.setResourcePath(Constants.RouteSchedules + "(guid'" + hashtable.get(Constants.RouteSchGUID) + "')", Constants.RouteSchedules + "(guid'" + hashtable.get(Constants.RouteSchGUID) + "')");
                }
                try {
                    store.allocateProperties(newHeaderEntity, PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }
                //If available, it populates the navigation properties of an OData Entity
                store.allocateNavigationProperties(newHeaderEntity);
                newHeaderEntity = getMTPHeaderEntity(newHeaderEntity, hashtable);
                newTextEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.RouteScheduleSPEntity);


                try {
                    store.allocateProperties(newTextEntity, ODataStore.PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }
                newTextEntity = getMTPItemOneEntity(newTextEntity, hashtable, GUID.newRandom().toString36());
                tempTextArray.add(0, newTextEntity);

                ODataEntitySetDefaultImpl itemTextEntity = new ODataEntitySetDefaultImpl(tempTextArray.size(), null, null);
                for (ODataEntity entity : tempTextArray) {
                    itemTextEntity.getEntities().add(entity);
                }
                itemTextEntity.setResourcePath(Constants.RouteScheduleSPs);
                ODataNavigationProperty navTextProp = newHeaderEntity.getNavigationProperty(Constants.RouteScheduleSPs);
                navTextProp.setNavigationContent(itemTextEntity);
                newHeaderEntity.setNavigationProperty(Constants.RouteScheduleSPs, navTextProp);
//                        }
//                    }


                int incremntVal = 0;
                for (int incrementVal = 0; incrementVal < itemhashtable.size(); incrementVal++) {

                    HashMap<String, String> singleRow = itemhashtable.get(incrementVal);

                    incremntVal = incrementVal + 1;

                    newItemEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.RouteSchedulePlanEntity);

                    newItemEntity.setResourcePath(Constants.RouteSchedulePlans + "(" + singleRow.get(Constants.RouteSchPlanGUID) + ")", Constants.RouteSchedulePlans + "(" + singleRow.get(Constants.RouteSchPlanGUID) + ")");
                    try {
                        store.allocateProperties(newItemEntity, PropMode.Keys);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }
                    //If available, it populates the navigation properties of an OData Entity
                    store.allocateNavigationProperties(newItemEntity);
                    newItemEntity = getMTPItemTwoEntity(newItemEntity, singleRow);

                    tempArray.add(incrementVal, newItemEntity);


                }

                ODataEntitySetDefaultImpl itemEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itemEntity.getEntities().add(entity);
                }
                itemEntity.setResourcePath(Constants.RouteSchedulePlans);

                ODataNavigationProperty navProp = newHeaderEntity.getNavigationProperty(Constants.RouteSchedulePlans);
                navProp.setNavigationContent(itemEntity);
                newHeaderEntity.setNavigationProperty(Constants.RouteSchedulePlans, navProp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newHeaderEntity;
    }

    public static void updateMTP(Hashtable<String, String> table, ArrayList<HashMap<String, String>> itemtable, OnlineODataInterface onlineODataInterface) throws OnlineODataStoreException {
        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();
        if (store != null) {
            try {
                ODataRequestParamBatch requestParamBatch = new ODataRequestParamBatchDefaultImpl();
                ODataRequestChangeSet changeSetItem = new ODataRequestChangeSetDefaultImpl();
                changeSetItem = updateMTPHeaderRequestBatchList(store, table, changeSetItem);
                changeSetItem = updateMTPItemParameterBatchList(store, table, itemtable, changeSetItem, 2);
                changeSetItem = updateMTPItemTwoParameterBatchList(store, table, itemtable, changeSetItem, 2);
                ;
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BUNDLE_RESOURCE_PATH, Constants.RouteSchedules);
                OnlineRequestListeners listener = new OnlineRequestListeners(onlineODataInterface, bundle);
                requestParamBatch.add(changeSetItem);
                try {
                    store.scheduleRequest(requestParamBatch, listener);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }
    }*/

    /*private static ODataRequestChangeSet updateMTPHeaderRequestBatchList(OnlineODataStore store, Hashtable<String, String> headerhashtable, ODataRequestChangeSet changeSetItem) throws ODataException {
        ODataRequestParamSingle batchItem = new ODataRequestParamSingleDefaultImpl();
        batchItem.setResourcePath(Constants.RouteSchedules + "(RouteSchGUID=guid'" + headerhashtable.get(Constants.RouteSchGUID) + "')");
        batchItem.setMode(ODataRequestParamSingle.Mode.Update);
        batchItem.setContentID("1");
        ODataEntity oDataHeaderEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.RouteScheduleEntity);
        oDataHeaderEntity.setResourcePath(Constants.RouteSchedules + "('" + headerhashtable.get(Constants.RouteSchGUID) + "')", Constants.RouteSchedules + "('" + headerhashtable.get(Constants.RouteSchGUID) + "')");
        store.allocateProperties(oDataHeaderEntity, ODataStore.PropMode.Keys);
        store.allocateNavigationProperties(oDataHeaderEntity);
        oDataHeaderEntity = getMTPHeaderEntity(oDataHeaderEntity, headerhashtable);
        oDataHeaderEntity.getProperties().put(Constants.RoutId,
                new ODataPropertyDefaultImpl(Constants.RoutId, headerhashtable.get(Constants.RoutId)));
        oDataHeaderEntity.getProperties().put(Constants.CreatedBy,
                new ODataPropertyDefaultImpl(Constants.CreatedBy, headerhashtable.get(Constants.CreatedBy)));
        try {
            oDataHeaderEntity.getProperties().put(Constants.CreatedOn,
                    new ODataPropertyDefaultImpl(Constants.CreatedOn, UtilConstants.convertDateFormat(headerhashtable.get(Constants.CreatedOn))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        oDataHeaderEntity.getProperties().put(Constants.CreatedAt,
                new ODataPropertyDefaultImpl(Constants.CreatedAt, UtilConstants.getOdataDuration()));
        batchItem.setPayload(oDataHeaderEntity);
        changeSetItem.add(batchItem);
        return changeSetItem;
    }

    private static ODataEntity getMTPHeaderEntity(ODataEntity newHeaderEntity, Hashtable<String, String> hashtable) {
        newHeaderEntity.getProperties().put(Constants.RouteSchGUID,
                new ODataPropertyDefaultImpl(Constants.RouteSchGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.RouteSchGUID))));
        newHeaderEntity.getProperties().put(Constants.SalesPersonID,
                new ODataPropertyDefaultImpl(Constants.SalesPersonID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SalesPersonID))));
        newHeaderEntity.getProperties().put(Constants.Testrun,
                new ODataPropertyDefaultImpl(Constants.Testrun, hashtable.get(Constants.Testrun)));
        newHeaderEntity.getProperties().put(Constants.ValidFrom,
                new ODataPropertyDefaultImpl(Constants.ValidFrom, UtilConstants.convertDateFormat(hashtable.get(Constants.ValidFrom))));
        newHeaderEntity.getProperties().put(Constants.ValidTo,
                new ODataPropertyDefaultImpl(Constants.ValidTo, UtilConstants.convertDateFormat(hashtable.get(Constants.ValidTo))));
        newHeaderEntity.getProperties().put(Constants.Month,
                new ODataPropertyDefaultImpl(Constants.Month, hashtable.get(Constants.Month)));
        newHeaderEntity.getProperties().put(Constants.Year,
                new ODataPropertyDefaultImpl(Constants.Year, hashtable.get(Constants.Year)));
       *//* try {
            newHeaderEntity.getProperties().put(Constants.CPTypeID,
                    new ODataPropertyDefaultImpl(Constants.CPTypeID, "01"));
        } catch (Exception e) {
            e.printStackTrace();
        }*//*

        return newHeaderEntity;
    }

    private static ODataEntity getMTPItemOneEntity(ODataEntity newTextEntity, Hashtable<String, String> hashtable, String scheduleSPGuid) {
        newTextEntity.setResourcePath(Constants.RouteScheduleSPs + "(1)", Constants.RouteScheduleSPs + "(1)");
        newTextEntity.getProperties().put(Constants.RouteSchSPGUID,
                new ODataPropertyDefaultImpl(Constants.RouteSchSPGUID, ODataGuidDefaultImpl.initWithString32(scheduleSPGuid)));
        newTextEntity.getProperties().put(Constants.RouteSchGUID,
                new ODataPropertyDefaultImpl(Constants.RouteSchGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.RouteSchGUID))));
        newTextEntity.getProperties().put(Constants.SalesPersonID,
                new ODataPropertyDefaultImpl(Constants.SalesPersonID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SalesPersonID))));
        newTextEntity.getProperties().put(Constants.DMSDivision,
                new ODataPropertyDefaultImpl(Constants.DMSDivision, "00"));
        return newTextEntity;
    }

    private static ODataEntity getMTPItemTwoEntity(ODataEntity newItemEntity, HashMap<String, String> singleRow) {
        newItemEntity.getProperties().put(Constants.RouteSchPlanGUID,
                new ODataPropertyDefaultImpl(Constants.RouteSchPlanGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.RouteSchPlanGUID))));

        newItemEntity.getProperties().put(Constants.RouteSchGUID,
                new ODataPropertyDefaultImpl(Constants.RouteSchGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.RouteSchGUID))));

        newItemEntity.getProperties().put(Constants.VisitDate,
                new ODataPropertyDefaultImpl(Constants.VisitDate, UtilConstants.convertDateFormat(singleRow.get(Constants.VisitDate))));


        newItemEntity.getProperties().put(Constants.VisitCPGUID,
                new ODataPropertyDefaultImpl(Constants.VisitCPGUID, singleRow.get(Constants.VisitCPGUID)));

        newItemEntity.getProperties().put(Constants.VisitCPName,
                new ODataPropertyDefaultImpl(Constants.VisitCPName, singleRow.get(Constants.VisitCPName)));

        newItemEntity.getProperties().put(Constants.ActivityDesc,
                new ODataPropertyDefaultImpl(Constants.ActivityDesc, singleRow.get(Constants.ActivityDesc)));

        newItemEntity.getProperties().put(Constants.ActivityID,
                new ODataPropertyDefaultImpl(Constants.ActivityID, singleRow.get(Constants.ActivityID)));

        newItemEntity.getProperties().put(Constants.SalesDistrict,
                new ODataPropertyDefaultImpl(Constants.SalesDistrict, singleRow.get(Constants.SalesDistrict)));

        newItemEntity.getProperties().put(Constants.SalesDistrictDesc,
                new ODataPropertyDefaultImpl(Constants.SalesDistrictDesc, singleRow.get(Constants.SalesDistrictDesc)));
        newItemEntity.getProperties().put(Constants.Remarks,
                new ODataPropertyDefaultImpl(Constants.Remarks, singleRow.get(Constants.Remarks)));
        return newItemEntity;
    }

    private static ODataRequestChangeSet updateMTPItemParameterBatchList(OnlineODataStore store, Hashtable<String, String> headerhashtable, ArrayList<HashMap<String, String>> itemhashtable, ODataRequestChangeSet changeSetItem, int type) throws ODataException {
        String str = GUID.newRandom().toString36();
        ODataRequestParamSingle batchItem = new ODataRequestParamSingleDefaultImpl();
        batchItem.setResourcePath(Constants.RouteScheduleSPs + "(RouteSchSPGUID=guid'" + str + "')");
        batchItem.setMode(ODataRequestParamSingle.Mode.Update);
        batchItem.setContentID("1");
        ODataEntity oDataHeaderEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.RouteScheduleSPEntity);
        oDataHeaderEntity.setResourcePath(Constants.RouteScheduleSPs + "(1)", Constants.RouteScheduleSPs + "(1)");

        store.allocateProperties(oDataHeaderEntity, ODataStore.PropMode.Keys);
        store.allocateNavigationProperties(oDataHeaderEntity);
        oDataHeaderEntity = getMTPItemOneEntity(oDataHeaderEntity, headerhashtable, str);
        batchItem.setPayload(oDataHeaderEntity);
        changeSetItem.add(batchItem);

        return changeSetItem;
    }

    private static ODataRequestChangeSet updateMTPItemTwoParameterBatchList(OnlineODataStore store, Hashtable<String, String> headerhashtable, ArrayList<HashMap<String, String>> itemhashtable, ODataRequestChangeSet changeSetItem, int type) throws ODataException {
        for (int i = 0; i < itemhashtable.size(); i++) {
            HashMap<String, String> singleRow = itemhashtable.get(i);

            ODataRequestParamSingle batchItem = new ODataRequestParamSingleDefaultImpl();
            batchItem.setResourcePath(Constants.RouteSchedulePlans + "(RouteSchPlanGUID=guid'" + singleRow.get(Constants.RouteSchPlanGUID) + "')");
            batchItem.setMode(ODataRequestParamSingle.Mode.Update);
            batchItem.setContentID("1");
            ODataEntity oDataHeaderEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.RouteSchedulePlanEntity);
            oDataHeaderEntity.setResourcePath(Constants.RouteSchedulePlans + "(RouteSchPlanGUID='" + singleRow.get(Constants.RouteSchPlanGUID) + "')", Constants.RouteSchedulePlans + "(RouteSchPlanGUID='" + singleRow.get(Constants.RouteSchPlanGUID) + "')");

            store.allocateProperties(oDataHeaderEntity, ODataStore.PropMode.Keys);
            store.allocateNavigationProperties(oDataHeaderEntity);
            oDataHeaderEntity = getMTPItemTwoEntity(oDataHeaderEntity, singleRow);
            batchItem.setPayload(oDataHeaderEntity);
            changeSetItem.add(batchItem);
        }

        return changeSetItem;
    }*/

    public static ArrayList<MTPHeaderBean> getMTPApprovalListDetail(ODataRequestExecution oDataRequestExecution, boolean isAsmLogin) {
        ArrayList<MTPHeaderBean> routeHeaderBeanArrayList = new ArrayList<>();

        ODataProperty property;
        ODataPropMap properties;

        try {
            ODataResponseSingle oDataResponseSingle = (ODataResponseSingleDefaultImpl) oDataRequestExecution.getResponse();
            ODataEntity entity = (ODataEntity) oDataResponseSingle.getPayload();
            properties = entity.getProperties();
            MTPHeaderBean mtpHeaderBean = new MTPHeaderBean();
            property = properties.get(Constants.ValidFrom);
            String validFrom = "";
            if (property != null)
                validFrom = ConstantsUtils.convertCalenderToDisplayDateFormat((GregorianCalendar) property.getValue(), "M");
            Calendar currentCal = Calendar.getInstance();
            currentCal.set(Calendar.DAY_OF_MONTH, 1);
            currentCal.setFirstDayOfWeek(Calendar.SUNDAY);
            currentCal.setMinimalDaysInFirstWeek(1);
            // if(comingFrom.equalsIgnoreCase(ConstantsUtils.MONTH_NEXT)){
            if (!TextUtils.isEmpty(validFrom)) {
                currentCal.set(Calendar.MONTH, Integer.parseInt(validFrom) - 1);
            }
            //}
            int maxDay = currentCal.getActualMaximum(Calendar.DAY_OF_MONTH);

            int oldWeek = 0;
            for (int i = 1; i <= maxDay; i++) {
                int week = currentCal.get(Calendar.WEEK_OF_MONTH);
                currentCal.set(Calendar.DAY_OF_MONTH, i);
                String dayShortName = currentCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
                if (week != oldWeek) {
                    mtpHeaderBean = new MTPHeaderBean();
                    mtpHeaderBean.setWeekTitle("Week " + String.valueOf(week));
                    mtpHeaderBean.setWeek(week);
                    mtpHeaderBean.setTitle(true);
                    routeHeaderBeanArrayList.add(mtpHeaderBean);
                    oldWeek = week;
                }
                mtpHeaderBean = new MTPHeaderBean();
                mtpHeaderBean.setWeekTitle(String.valueOf(week));
                mtpHeaderBean.setDay(dayShortName);
                mtpHeaderBean.setDate(String.valueOf(currentCal.get(Calendar.DAY_OF_MONTH)));
                mtpHeaderBean.setFullDate(ConstantsUtils.convertCalenderToDisplayDateFormat(currentCal, "dd-MMM-yyyy"));

                property = properties.get(Constants.RouteSchGUID);
                String routeSchemGuid = "";
                try {
                    ODataGuid mRouteGUID = (ODataGuid) property.getValue();
                    routeSchemGuid = mRouteGUID.guidAsString36().toUpperCase();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                property = properties.get(Constants.SalesPersonID);
                String salesPersonsGuid = "";
                try {
                    if(property!=null && property.getValue()!=null) {
                        ODataGuid mSPGUID = (ODataGuid) property.getValue();
                        salesPersonsGuid = mSPGUID.guidAsString36().toUpperCase();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                mtpHeaderBean.setRouteSchGUID(routeSchemGuid);
                mtpHeaderBean.setSalesPersonGuid(salesPersonsGuid);
                property = properties.get(Constants.Month);
                if (property != null) {
                    mtpHeaderBean.setMonthForApproval(property.getValue().toString());
                }
                mtpHeaderBean.setIsUpdate("X");
                property = properties.get(Constants.ApprovalStatus);
                if (property != null) {
                    mtpHeaderBean.setApprovalStatus(property.getValue().toString());
                }
                if (TextUtils.isEmpty(mtpHeaderBean.getApprovalStatus())) {
                    mtpHeaderBean.setTestRun("D");
                }
                property = properties.get(Constants.ApprovalStatusDs);
                if (property != null) {
                    mtpHeaderBean.setApprovalStatusDs(property.getValue().toString());
                }
                property = properties.get(Constants.RoutId);
                if (property != null)
                    mtpHeaderBean.setRoutId(property.getValue().toString());
                property = properties.get(Constants.CreatedBy);
                if (property != null)
                    mtpHeaderBean.setCreatedBy(property.getValue().toString());
                property = properties.get(Constants.CreatedOn);
                if (property != null)
                    mtpHeaderBean.setCreatedOn(ConstantsUtils.convertCalenderToDisplayDateFormat((GregorianCalendar) property.getValue()));
                ODataNavigationProperty soItemDetailsProp = entity.getNavigationProperty(Constants.RouteSchedulePlans);
                ODataEntitySet feed = (ODataEntitySet) soItemDetailsProp.getNavigationContent();
                List<ODataEntity> entities = feed.getEntities();
                ArrayList<MTPRoutePlanBean> routePlanBeanArrayList = new ArrayList<>();
                for (ODataEntity mtpItemEntity : entities) {
                    properties = mtpItemEntity.getProperties();
                    MTPRoutePlanBean mtpRoutePlanBean = new MTPRoutePlanBean();
                    property = properties.get(Constants.VisitDate);
                    if (property != null) {
                        mtpRoutePlanBean.setVisitDate(ConstantsUtils.convertCalenderToDisplayDateFormat((GregorianCalendar) property.getValue()));
                        if (ConstantsUtils.convertCalenderToDisplayDateFormat(currentCal, "dd-MMM-yyyy").equalsIgnoreCase(mtpRoutePlanBean.getVisitDate())) {
                            mtpRoutePlanBean.setDay(dayShortName);
                            mtpRoutePlanBean.setDate(String.valueOf(currentCal.get(Calendar.DAY_OF_MONTH)));
                            routePlanBeanArrayList.add(ConstantsUtils.parseMTPItems(mtpRoutePlanBean, properties, isAsmLogin));
                        }
                    }
                    // Calendar calendarItem = ConstantsUtils.convertCalenderToDisplayDateFormat(mtpRoutePlanBean.getVisitDate(), "yyyy-MM-dd'T'HH:mm:ss");
//                    routePlanBeanArrayList.add(ConstantsUtils.parseMTPItems(mtpRoutePlanBean, properties));
                }
                mtpHeaderBean.setMTPRoutePlanBeanArrayList(routePlanBeanArrayList);
                if (!routePlanBeanArrayList.isEmpty()) {
                    OfflineManager.addMTPHeaderData(mtpHeaderBean, routePlanBeanArrayList, isAsmLogin);
                }
                routeHeaderBeanArrayList.add(mtpHeaderBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return routeHeaderBeanArrayList;
    }

    public static ArrayList<MTPHeaderBean> getMTPApprovalListDetail(JSONObject jsonObjectResult, boolean isAsmLogin) {
        ArrayList<MTPHeaderBean> routeHeaderBeanArrayList = new ArrayList<>();

        try {
            MTPHeaderBean mtpHeaderBean = new MTPHeaderBean();
            String validFrom = "";
            validFrom = ConstantsUtils.convertStringToCalDateFormat(ConstantsUtils.getJSONDate(jsonObjectResult.optString(Constants.ValidFrom)), "M");
            Calendar currentCal = Calendar.getInstance();
            currentCal.set(Calendar.DAY_OF_MONTH, 1);
            currentCal.setFirstDayOfWeek(Calendar.SUNDAY);
            currentCal.setMinimalDaysInFirstWeek(1);
            // if(comingFrom.equalsIgnoreCase(ConstantsUtils.MONTH_NEXT)){
            if (!TextUtils.isEmpty(validFrom)) {
                currentCal.set(Calendar.MONTH, Integer.parseInt(validFrom) - 1);
            }
            //}
            int maxDay = currentCal.getActualMaximum(Calendar.DAY_OF_MONTH);

            int oldWeek = 0;
            for (int i = 1; i <= maxDay; i++) {
                int week = currentCal.get(Calendar.WEEK_OF_MONTH);
                currentCal.set(Calendar.DAY_OF_MONTH, i);
                String dayShortName = currentCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
                if (week != oldWeek) {
                    mtpHeaderBean = new MTPHeaderBean();
                    mtpHeaderBean.setWeekTitle("Week " + String.valueOf(week));
                    mtpHeaderBean.setWeek(week);
                    mtpHeaderBean.setTitle(true);
                    routeHeaderBeanArrayList.add(mtpHeaderBean);
                    oldWeek = week;
                }
                mtpHeaderBean = new MTPHeaderBean();
                mtpHeaderBean.setWeekTitle(String.valueOf(week));
                mtpHeaderBean.setDay(dayShortName);
                mtpHeaderBean.setDate(String.valueOf(currentCal.get(Calendar.DAY_OF_MONTH)));
                mtpHeaderBean.setFullDate(ConstantsUtils.convertCalenderToDisplayDateFormat(currentCal, "dd-MMM-yyyy"));

                String routeSchemGuid = "";
                try {
                    routeSchemGuid = jsonObjectResult.optString(Constants.RouteSchGUID);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String salesPersonsGuid = "";
                try {
                    if (jsonObjectResult.optString(Constants.SalesPersonID) != null) {
                        salesPersonsGuid = jsonObjectResult.optString(Constants.SalesPersonID);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                mtpHeaderBean.setRouteSchGUID(routeSchemGuid);
                mtpHeaderBean.setSalesPersonGuid(salesPersonsGuid);

                mtpHeaderBean.setMonthForApproval(jsonObjectResult.optString(Constants.Month));

                mtpHeaderBean.setIsUpdate("X");

                mtpHeaderBean.setApprovalStatus(jsonObjectResult.optString(Constants.ApprovalStatus));

                if (TextUtils.isEmpty(mtpHeaderBean.getApprovalStatus())) {
                    mtpHeaderBean.setTestRun("D");
                }

                mtpHeaderBean.setApprovalStatusDs(jsonObjectResult.optString(Constants.ApprovalStatusDs));


                mtpHeaderBean.setRoutId(jsonObjectResult.optString(Constants.RoutId));
                mtpHeaderBean.setCreatedBy(jsonObjectResult.optString(Constants.CreatedBy));
                try {
                    mtpHeaderBean.setCreatedOn(ConstantsUtils.getJSONDate(jsonObjectResult.optString(Constants.CreatedOn)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                JSONObject object = jsonObjectResult.getJSONObject(Constants.RouteSchedulePlans);
                JSONArray jsonArray = OnlineManager.getJSONArrayBody(object);
                ArrayList<MTPRoutePlanBean> routePlanBeanArrayList = new ArrayList<>();
                for (int j=0;j<jsonArray.length();j++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(j);
                    MTPRoutePlanBean mtpRoutePlanBean = new MTPRoutePlanBean();
                    if (jsonObject.optString(Constants.VisitDate) != null) {
                        mtpRoutePlanBean.setVisitDate(ConstantsUtils.getJSONDate(jsonObject.optString(Constants.VisitDate)));
                        if (ConstantsUtils.convertCalenderToDisplayDateFormat(currentCal, "dd/MM/yyyy"/*"dd-MMM-yyyy"*/).equalsIgnoreCase(mtpRoutePlanBean.getVisitDate())) {
                            mtpRoutePlanBean.setDay(dayShortName);
                            mtpRoutePlanBean.setDate(String.valueOf(currentCal.get(Calendar.DAY_OF_MONTH)));
                            routePlanBeanArrayList.add(ConstantsUtils.parseMTPItems(mtpRoutePlanBean, jsonObject, isAsmLogin));
                        }
                    }
                    // Calendar calendarItem = ConstantsUtils.convertCalenderToDisplayDateFormat(mtpRoutePlanBean.getVisitDate(), "yyyy-MM-dd'T'HH:mm:ss");
//                    routePlanBeanArrayList.add(ConstantsUtils.parseMTPItems(mtpRoutePlanBean, properties));
                }
                mtpHeaderBean.setMTPRoutePlanBeanArrayList(routePlanBeanArrayList);
                if (!routePlanBeanArrayList.isEmpty()) {
                    OfflineManager.addMTPHeaderData(mtpHeaderBean, routePlanBeanArrayList, isAsmLogin);
                }
                routeHeaderBeanArrayList.add(mtpHeaderBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return routeHeaderBeanArrayList;
    }

    public static ArrayList<WeekHeaderList> getCollectionNextmonth(String qry, String comingFrom, Context mContext,
                                                                   String validFrom, String validTo, String period, String spGUID) throws OfflineODataStoreException {
        ArrayList<WeekHeaderList> weekHeaderLists = new ArrayList<>();
        try {
            Calendar currentCal = Calendar.getInstance();
            currentCal.set(Calendar.DAY_OF_MONTH, 1);
            if (comingFrom.equalsIgnoreCase(ConstantsUtils.MONTH_NEXT) || comingFrom.equalsIgnoreCase(ConstantsUtils.RTGS_SUBORDINATE_NEXT)) {
                currentCal.add(Calendar.MONTH, 1);
            }
            Calendar calendarVFrom = ConstantsUtils.convertCalenderToDisplayDateFormat(validFrom, "yyyy-MM-dd'T'HH:mm:ss");
            Calendar calendarVTo = ConstantsUtils.convertCalenderToDisplayDateFormat(validTo, "yyyy-MM-dd'T'HH:mm:ss");
            validFrom = ConstantsUtils.convertCalenderToDisplayDateFormat(calendarVFrom, ConstantsUtils.getDisplayDateFormat(mContext));
            validTo = ConstantsUtils.convertCalenderToDisplayDateFormat(calendarVTo, ConstantsUtils.getDisplayDateFormat(mContext));

            currentCal.setMinimalDaysInFirstWeek(1);
            int maxDay = currentCal.getActualMaximum(Calendar.DAY_OF_MONTH);
            WeekHeaderList weekHeaderList = null;
            int oldWeek = 0;
            List<ODataEntity> entities = UtilOfflineManager.getEntities(OfflineManager.offlineStore, qry);
            int saturdayCount = 1;
            for (int i = 1; i <= maxDay; i++) {
//                int week = currentCal.get(Calendar.WEEK_OF_MONTH);
                currentCal.set(Calendar.DAY_OF_MONTH, i);

                String dayShortName = currentCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
                int week = currentCal.get(Calendar.WEEK_OF_MONTH);
                if (week != oldWeek) {
                    weekHeaderList = new WeekHeaderList();
                    weekHeaderList.setWeekTitle("Week " + String.valueOf(week));
                    weekHeaderList.setTitle(true);
                    weekHeaderList.setWeek(week);
                    weekHeaderLists.add(weekHeaderList);
                    oldWeek = week;
                }
                weekHeaderList = new WeekHeaderList();
                weekHeaderList.setWeekTitle(String.valueOf(week));
                weekHeaderList.setDay(dayShortName);
                weekHeaderList.setDate(String.valueOf(currentCal.get(Calendar.DAY_OF_MONTH)));
                weekHeaderList.setFullDate(ConstantsUtils.convertCalenderToDisplayDateFormat(currentCal, "dd-MMM-yyyy"));
                weekHeaderList.setSunday(Constants.isSunday(currentCal));
                if (dayShortName.equalsIgnoreCase("Sat")) {
                    if (saturdayCount % 2 == 0) {
                        weekHeaderList.setSecondSat(true);
                    }
                    saturdayCount++;
                }
                ArrayList<WeekDetailsList> weekDetailsLists = null;
                ArrayList<WeekDetailsList> weekDetailsDataValtLists = getCollListFromDataValt(mContext, currentCal, weekHeaderList, validFrom, validTo, spGUID);
                if (!weekDetailsDataValtLists.isEmpty()) {
                    weekDetailsLists = weekDetailsDataValtLists;
                } else {
                    weekDetailsLists = getCollDetailsList(currentCal, entities, weekHeaderList);
                }


    //            if (!weekDetailsLists.isEmpty()) {
    //                BigDecimal mStrAmount = new BigDecimal("0");
    //                weekHeaderList.setCurrency(weekDetailsLists.get(0).getCurrency());
    //                for (int j = 0; j < weekDetailsLists.size(); j++) {
    //                    if (!"".equals(weekDetailsLists.get(j).getPlannedValue())) {
    //                        mStrAmount = mStrAmount.add(new BigDecimal(weekDetailsLists.get(j).getPlannedValue()));
    //                    }
    //                }
    //                weekHeaderList.setTotalAmount(mStrAmount.toString());
    //            }
                weekHeaderList.setWeekDetailsLists(weekDetailsLists);

                if (!weekDetailsLists.isEmpty()) {

                    if (weekDetailsLists.size() == 1) {
                        WeekDetailsList weekDetailsList = weekDetailsLists.get(0);
                        weekHeaderList.setName(weekDetailsList.getcPName());
                        weekHeaderList.setCustNo(weekDetailsList.getcPNo());
                        weekHeaderList.setRemarks(weekDetailsList.getRemarks());
                        weekHeaderList.setCollectionPlanGUID(weekDetailsList.getCollectionPlanGUID());
                    } else {
                        WeekDetailsList weekDetailsList = weekDetailsLists.get(0);
                        weekHeaderList.setName(weekDetailsList.getcPName() + "...");
                        weekHeaderList.setCustNo(weekDetailsList.getcPNo());
                        weekHeaderList.setRemarks(weekDetailsList.getRemarks());
                        weekHeaderList.setCollectionPlanGUID(weekDetailsList.getCollectionPlanGUID());
                    }

                }
                weekHeaderLists.add(weekHeaderList);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return weekHeaderLists;
    }


    private static ArrayList<WeekDetailsList> getCollectionPlanItemDetails(Calendar currentCal, List<ODataEntity> entities, String comingFrom) {
        ArrayList<WeekDetailsList> weekDetailsListArrayList = new ArrayList<>();
        ODataPropMap properties;
        ODataProperty property;
        for (ODataEntity oDataEntity : entities) {
            properties = oDataEntity.getProperties();
            if (comingFrom.equals(ConstantsUtils.MONTH_TODAY)) {
                if (getPayload(properties, currentCal) != null)
                    weekDetailsListArrayList.add(getPayload(properties, currentCal));
            } else {
                property = properties.get(Constants.CollectionPlanGUID);
                ODataGuid campaignGuid = (ODataGuid) property.getValue();
                String collePlanGUID32 = campaignGuid.guidAsString36().toUpperCase();
                String qry = Constants.CollectionPlanItem + "?$filter=" + Constants.CollectionPlanGUID + " eq guid'" + collePlanGUID32 + "'";
                try {
                    List<ODataEntity> itemEntity = UtilOfflineManager.getEntities(OfflineManager.offlineStore, qry);
                    for (ODataEntity itemODataEntity : itemEntity) {
                        properties = itemODataEntity.getProperties();
                        if (getPayload(properties, currentCal) != null)
                            weekDetailsListArrayList.add(getPayload(properties, currentCal));
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }
        }
        return weekDetailsListArrayList;
    }

    private static WeekDetailsList getPayload(ODataPropMap properties, Calendar currentCal) {
        ODataProperty property;
        property = properties.get(Constants.COllectionPlanDate);
        BigDecimal totalAmount = new BigDecimal("0");
        if (property != null) {
            String date = ConstantsUtils.convertCalenderToDisplayDateFormat((GregorianCalendar) property.getValue());
            if (ConstantsUtils.convertCalenderToDisplayDateFormat(currentCal, "dd-MMM-yyyy").equalsIgnoreCase(date)) {
                String dayShortName = currentCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
                WeekDetailsList weekDetailsList = new WeekDetailsList();
                weekDetailsList.setDay(dayShortName);
                weekDetailsList.setCurrentDate(ConstantsUtils.getDayDate());

                weekDetailsList.setDate(date);
                property = properties.get(Constants.COllectionPlanDate);
                if (property != null) {
                    weekDetailsList.setCollectionPlanDate(property.getValue().toString());
                }
                property = properties.get(Constants.ItemNo);
                if (property != null) {
                    weekDetailsList.setItemNo(property.getValue().toString());
                }
                property = properties.get(Constants.PlannedValue);
                if (property != null) {
                    BigDecimal mStrAmount = (BigDecimal) property.getValue();
                    weekDetailsList.setPlannedValue(mStrAmount.toString());
                }
                if (weekDetailsList.getPlannedValue() != null && !"".equals(weekDetailsList.getPlannedValue())) {
                    totalAmount = totalAmount.add(new BigDecimal(weekDetailsList.getPlannedValue()));
                }
                weekDetailsList.setTotalAmount(totalAmount.toString());
                property = properties.get(Constants.AchievedValue);
                if (property != null) {
                    BigDecimal mStrAmount = (BigDecimal) property.getValue();
                    weekDetailsList.setAchievedValue(mStrAmount.toString());
                }

                property = properties.get(Constants.SPName);
                if (property != null) {
                    weekDetailsList.setSPName(property.getValue().toString());
                }

                property = properties.get(Constants.Currency);
                if (property != null) {
                    weekDetailsList.setCurrency(property.getValue().toString());
                }
                property = properties.get(Constants.CrdtCtrlArea);
                if (property != null) {
                    weekDetailsList.setCrdtCtrlArea(property.getValue().toString());
                }

                property = properties.get(Constants.CrdtCtrlAreaDs);
                if (property != null) {
                    weekDetailsList.setCrdtCtrlAreaDs(property.getValue().toString());
                }
                property = properties.get(Constants.CPName);
                if (property != null) {
                    weekDetailsList.setCPName(property.getValue().toString());
                }

                property = properties.get(Constants.REMARKS);
                if (property != null) {
                    weekDetailsList.setRemarks(property.getValue().toString());
                }
                return weekDetailsList;
            }
        }
        return null;
    }

    public static ArrayList<WeekDetailsList> getCollListFromDataValt(Context context, Calendar currentCal,
                                                                     WeekHeaderList weekHeaderList, String validFrom, String validTo, String spGUID) throws OfflineODataStoreException {
        WeekDetailsList routePlanBean;
        ArrayList<WeekDetailsList> routePlanDetailsList = new ArrayList<>();
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        set = sharedPreferences.getStringSet(Constants.RTGSDataValt, null);
        String dayShortName = currentCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                String store = null, deviceNo = "";
                try {
                    deviceNo = itr.next().toString();
                    store = ConstantsUtils.getFromDataVault(deviceNo,context);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject fetchJsonHeaderObject = new JSONObject(store);
                    Calendar calendarVFrom = ConstantsUtils.convertCalenderToDisplayDateFormat(fetchJsonHeaderObject.optString(Constants.ValidFrom), "yyyy-MM-dd'T'HH:mm:ss");
                    Calendar calendarVTo = ConstantsUtils.convertCalenderToDisplayDateFormat(fetchJsonHeaderObject.optString(Constants.ValidTo), "yyyy-MM-dd'T'HH:mm:ss");

                    if (fetchJsonHeaderObject.getString(Constants.EntityType).equalsIgnoreCase(Constants.CollectionPlan) &&
                            ConstantsUtils.convertCalenderToDisplayDateFormat(calendarVFrom, ConstantsUtils.getDisplayDateFormat(context)).equalsIgnoreCase(validFrom)
                            && ConstantsUtils.convertCalenderToDisplayDateFormat(calendarVTo, ConstantsUtils.getDisplayDateFormat(context)).equalsIgnoreCase(validTo)
                            && fetchJsonHeaderObject.getString(Constants.SPGUID).equalsIgnoreCase(spGUID)) {
                        double mdoubleTotAmt = 0.0;
                        double mdoubleTotAchivedAmt = 0.0;
                        routePlanBean = new WeekDetailsList();
                        routePlanBean.setDeviceNo(deviceNo);
                        weekHeaderList.setDeviceNo(deviceNo);
                        weekHeaderList.setCollectionPlanGUID(fetchJsonHeaderObject.optString(Constants.CollectionPlanGUID));
                        weekHeaderList.setIsUpdate(fetchJsonHeaderObject.optString(Constants.IS_UPDATE));

                        String itemsString = fetchJsonHeaderObject.getString(Constants.CollectionPlanItem);
                        ArrayList<HashMap<String, String>> arrtable = UtilConstants.convertToArrayListMap(itemsString);
                        String mStrCurrency = "";
                        for (int incrementVal = 0; incrementVal < arrtable.size(); incrementVal++) {
                            HashMap<String, String> singleRow = arrtable.get(incrementVal);
                            WeekDetailsList mtpRoutePlanBean = new WeekDetailsList();
                            mtpRoutePlanBean.setCollectionPlanGUID(fetchJsonHeaderObject.optString(Constants.CollectionPlanGUID));
                            Calendar calendar = ConstantsUtils.convertCalenderToDisplayDateFormat(singleRow.get(Constants.CollectionPlanDate), "yyyy-MM-dd'T'HH:mm:ss");
                            mtpRoutePlanBean.setVisitDate(ConstantsUtils.convertCalenderToDisplayDateFormat(calendar, ConstantsUtils.getDisplayDateFormat(context)));
                            if (ConstantsUtils.convertCalenderToDisplayDateFormat(currentCal, "dd-MMM-yyyy").equalsIgnoreCase(mtpRoutePlanBean.getVisitDate())) {
//                                mtpRoutePlanBean.setCollectionPlanItemGUID(singleRow.get(Constants.CollectionPlanItemGUID));
                                mtpRoutePlanBean.setcPNo(singleRow.get(Constants.CPNo));
                                mtpRoutePlanBean.setcPName(singleRow.get(Constants.CPName));
                                mtpRoutePlanBean.setcPType(singleRow.get(Constants.CPType));
                                mtpRoutePlanBean.setCurrency(singleRow.get(Constants.Currency));
                                mtpRoutePlanBean.setDay(dayShortName);
//                                mtpRoutePlanBean.setRemarks(singleRow.get(Constants.Remarks));
//                                mtpRoutePlanBean.setPlannedValue(singleRow.get(Constants.PlannedValue));
                                try {
                                    mtpRoutePlanBean.setCrdtCtrlArea(singleRow.get(Constants.CrdtCtrlArea));
                                    if(singleRow.get(Constants.CrdtCtrlArea).equalsIgnoreCase("1030")){
                                        mtpRoutePlanBean.setPlannedValue2(singleRow.get(Constants.PlannedValue));
                                        mtpRoutePlanBean.setRemarks2(singleRow.get(Constants.Remarks));
                                        mtpRoutePlanBean.setCollectionPlanItemGUID1(singleRow.get(Constants.CollectionPlanItemGUID));
                                    }else if(singleRow.get(Constants.CrdtCtrlArea).equalsIgnoreCase("1010")){
                                        mtpRoutePlanBean.setPlannedValue(singleRow.get(Constants.PlannedValue));
                                        mtpRoutePlanBean.setRemarks(singleRow.get(Constants.Remarks));
                                        mtpRoutePlanBean.setCollectionPlanItemGUID(singleRow.get(Constants.CollectionPlanItemGUID));
                                    }
                                    mtpRoutePlanBean.setCrdtCtrlAreaDs(singleRow.get(Constants.CrdtCtrlAreaDs));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    mtpRoutePlanBean.setAchievedValue(singleRow.get(Constants.AchievedValue));
                                } catch (Exception e) {
                                    mtpRoutePlanBean.setAchievedValue("0");
                                    e.printStackTrace();
                                }
                                mtpRoutePlanBean.setDate(String.valueOf(currentCal.get(Calendar.DAY_OF_MONTH)));
                                double mdouPlanedVal = 0.0;
                                try {
                                    mdouPlanedVal = Double.parseDouble(singleRow.get(Constants.PlannedValue));
                                } catch (NumberFormatException e) {
                                    mdouPlanedVal = 0.0;
                                    e.printStackTrace();
                                }

                                double mdouAchivedVal = 0.0;
                                try {
                                    mdouAchivedVal = Double.parseDouble(singleRow.get(Constants.AchievedValue));
                                } catch (NumberFormatException e) {
                                    mdouAchivedVal = 0.0;
                                    e.printStackTrace();
                                }
                                mStrCurrency = mtpRoutePlanBean.getCurrency();
                                mdoubleTotAmt = mdoubleTotAmt + mdouPlanedVal;
                                mdoubleTotAchivedAmt = mdoubleTotAchivedAmt + mdouAchivedVal;
                                routePlanDetailsList.add(mtpRoutePlanBean);
                            }
                        }
                        weekHeaderList.setCurrency(mStrCurrency);
                        weekHeaderList.setTotalAmount(mdoubleTotAmt + "");
                        weekHeaderList.setTotalAchivedAmount(mdoubleTotAchivedAmt + "");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return routePlanDetailsList;

    }

    private static ArrayList<WeekDetailsList> getCollDetailsList(Calendar currentCal, List<ODataEntity> entities, WeekHeaderList weekHeaderList) throws OfflineODataStoreException {
        ArrayList<WeekDetailsList> mtpRoutePlanList = new ArrayList<>();
        List<ODataEntity> tempList = new ArrayList<>();
        ODataPropMap propertiesHeader;
        ODataPropMap propertiesItem;
        ODataProperty property;
        try {
            String dayShortName = currentCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
            for (ODataEntity oDataEntity : entities) {
                propertiesHeader = oDataEntity.getProperties();
                property = propertiesHeader.get(Constants.CollectionPlanGUID);
                String routeSchemGuid = "";
                try {
                    if (property!=null) {
                        if (property.getValue()!=null) {
                            ODataGuid mInvoiceGUID = (ODataGuid) property.getValue();
                            routeSchemGuid = mInvoiceGUID.guidAsString36().toUpperCase();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (weekHeaderList != null) {
                    property = propertiesHeader.get(Constants.CreatedOn);
                    String convertDateFormat = "";
                    if (property != null) {
                        if (property.getValue()!=null) {
                            if ((GregorianCalendar) property.getValue() != null) {
                                convertDateFormat = UtilConstants.convertGregorianCalendarToYYYYMMDDFormat((GregorianCalendar) property.getValue());
                            } else {
                                convertDateFormat = "";
                            }
                        }
                    } else {
                        convertDateFormat = "";
                    }

                    weekHeaderList.setCreatedOn(convertDateFormat);

                    property = propertiesHeader.get(Constants.CreatedBy);
                    String createdBy = "";
                    if (property != null) {
                        if (property.getValue()!=null) {
                            if ((String) property.getValue() != null) {
                                createdBy = (String) property.getValue();
                            } else {
                                createdBy = "";
                            }
                        }
                    } else {
                        createdBy = "";
                    }

                    weekHeaderList.setCreatedBy(createdBy);

                    weekHeaderList.setCollectionPlanGUID(routeSchemGuid);
                    weekHeaderList.setIsUpdate("X");
                }
                double mdoubleTotAmt = 0.0;
                double mdoubleTotAchivedAmt = 0.0;
                List<ODataEntity> entitiesItem = UtilOfflineManager.getEntities(OfflineManager.offlineStore, Constants.CollectionPlanItem + "?$filter=CollectionPlanGUID eq guid'" + routeSchemGuid + "'");
                String mStrCurrency = "";
                for (ODataEntity oDataEntityItem : entitiesItem) {
                    WeekDetailsList mtpRoutePlanBean = new WeekDetailsList();
                    mtpRoutePlanBean.setCollectionPlanGUID(routeSchemGuid);
                    propertiesItem = oDataEntityItem.getProperties();
                    property = propertiesItem.get(Constants.CollectionPlanDate);
                    if (property != null) {
                        mtpRoutePlanBean.setVisitDate(ConstantsUtils.convertCalenderToDisplayDateFormat((GregorianCalendar) property.getValue()));
                        if (ConstantsUtils.convertCalenderToDisplayDateFormat(currentCal, "dd-MMM-yyyy").equalsIgnoreCase(mtpRoutePlanBean.getVisitDate())) {
                            /*property = propertiesItem.get(Constants.CollectionPlanItemGUID);
                            try {
                                ODataGuid mInvoiceGUID = (ODataGuid) property.getValue();
                                mtpRoutePlanBean.setCollectionPlanItemGUID(mInvoiceGUID.guidAsString36().toUpperCase());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }*/
                            property = propertiesItem.get(Constants.CPNo);
                            if (property!=null) {
                                if (property.getValue()!=null) {
                                    mtpRoutePlanBean.setcPNo(property.getValue().toString());
                                }
                            }

                            property = propertiesItem.get(Constants.CPType);
                            if (property!=null) {
                                if (property.getValue()!=null) {
                                    mtpRoutePlanBean.setcPType(property.getValue().toString());
                                }
                            }

                            property = propertiesItem.get(Constants.CPName);
                            if (property!=null) {
                                if (property.getValue()!=null) {
                                    mtpRoutePlanBean.setcPName(property.getValue().toString());
                                }
                            }

                            mtpRoutePlanBean.setDay(dayShortName);

    //                        property = propertiesItem.get(Constants.Remarks);
    //                        mtpRoutePlanBean.setRemarks(property.getValue().toString());

                            /*property = propertiesItem.get(Constants.PlannedValue);
                            if (property != null) {
                                BigDecimal mStrAmount = (BigDecimal) property.getValue();
                                mtpRoutePlanBean.setPlannedValue(mStrAmount.toString());
                            }*/

                            property = propertiesItem.get(Constants.AchievedValue);
                            if (property != null) {
                                if (property.getValue()!=null) {
                                    BigDecimal mStrAmount = (BigDecimal) property.getValue();
                                    mtpRoutePlanBean.setAchievedValue(mStrAmount.toString());
                                }
                            }

                            property = propertiesItem.get(Constants.Currency);
                            if (property!=null) {
                                if (property.getValue()!=null) {
                                    mtpRoutePlanBean.setCurrency(property.getValue().toString());
                                }
                            }
                            property = propertiesItem.get(Constants.CrdtCtrlArea);
                            if (property!=null) {
                                if (property.getValue()!=null) {
                                    mtpRoutePlanBean.setCrdtCtrlArea(property.getValue().toString());
                                    if(property.getValue().toString().equalsIgnoreCase("1030")){
                                        property = propertiesItem.get(Constants.PlannedValue);
                                        if (property != null) {
                                            if (property.getValue()!=null) {
                                                BigDecimal mStrAmount = (BigDecimal) property.getValue();
                                                mtpRoutePlanBean.setPlannedValue2(mStrAmount.toString());
                                            }
                                        }
                                        property = propertiesItem.get(Constants.Remarks);
                                        if (property!=null) {
                                            if (property.getValue()!=null) {
                                                mtpRoutePlanBean.setRemarks2(property.getValue().toString());
                                            }
                                        }
                                        property = propertiesItem.get(Constants.CollectionPlanItemGUID);
                                        try {
                                            if (property!=null) {
                                                if (property.getValue()!=null) {
                                                    ODataGuid mInvoiceGUID = (ODataGuid) property.getValue();
                                                    mtpRoutePlanBean.setCollectionPlanItemGUID1(mInvoiceGUID.guidAsString36().toUpperCase());
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }else if(property.getValue().toString().equalsIgnoreCase("1010")){
                                        property = propertiesItem.get(Constants.PlannedValue);
                                        if (property != null) {
                                            if (property.getValue()!=null) {
                                                BigDecimal mStrAmount = (BigDecimal) property.getValue();
                                                mtpRoutePlanBean.setPlannedValue(mStrAmount.toString());
                                            }
                                        }
                                        property = propertiesItem.get(Constants.Remarks);
                                        if (property!=null) {
                                            if (property.getValue()!=null) {
                                                mtpRoutePlanBean.setRemarks(property.getValue().toString());
                                            }
                                        }
                                        property = propertiesItem.get(Constants.CollectionPlanItemGUID);
                                        try {
                                            if (property!=null) {
                                                if (property.getValue()!=null) {
                                                    ODataGuid mInvoiceGUID = (ODataGuid) property.getValue();
                                                    mtpRoutePlanBean.setCollectionPlanItemGUID(mInvoiceGUID.guidAsString36().toUpperCase());
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            property = propertiesItem.get(Constants.CrdtCtrlAreaDs);
                            if (property!=null) {
                                if (property.getValue()!=null) {
                                    mtpRoutePlanBean.setCrdtCtrlAreaDs(property.getValue().toString());
                                }
                            }

                            property = propertiesItem.get(Constants.CreatedOn);
                            String convertDateFormat = "";
                            if (property != null) {
                                if (property.getValue()!=null) {
                                    if ((GregorianCalendar) property.getValue() != null) {
                                        convertDateFormat = UtilConstants.convertGregorianCalendarToYYYYMMDDFormat((GregorianCalendar) property.getValue());
                                    } else {
                                        convertDateFormat = "";
                                    }
                                }
                            } else {
                                convertDateFormat = "";
                            }

                            mtpRoutePlanBean.setCreatedOn(convertDateFormat);

                            property = propertiesItem.get(Constants.CreatedBy);
                            String createdBy = "";
                            if (property != null) {
                                if (property.getValue()!=null) {
                                    if ((String) property.getValue() != null) {
                                        createdBy = (String) property.getValue();
                                    } else {
                                        createdBy = "";
                                    }
                                }
                            } else {
                                createdBy = "";
                            }

                            double mdouPlanedVal = 0;
                            try {
                                mtpRoutePlanBean.setCreatedBy(createdBy);


                                mtpRoutePlanBean.setDate(String.valueOf(currentCal.get(Calendar.DAY_OF_MONTH)));
                                mtpRoutePlanList.add(mtpRoutePlanBean);

                                mdouPlanedVal = 0.0;
                                try {
                                    mdouPlanedVal = Double.parseDouble(mtpRoutePlanBean.getPlannedValue())+Double.parseDouble(mtpRoutePlanBean.getPlannedValue2());
                                } catch (NumberFormatException e) {
                                    mdouPlanedVal = 0.0;
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                double mdouAchievedVal = 0.0;
                                try {
                                    mdouAchievedVal = Double.parseDouble(mtpRoutePlanBean.getAchievedValue());
                                } catch (NumberFormatException e) {
                                    mdouAchievedVal = 0.0;
                                    e.printStackTrace();
                                }
                                mStrCurrency = mtpRoutePlanBean.getCurrency();

                                mdoubleTotAmt = mdoubleTotAmt + mdouPlanedVal;
                                mdoubleTotAchivedAmt = mdoubleTotAchivedAmt + mdouAchievedVal;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }


                }
                weekHeaderList.setCurrency(mStrCurrency);
                weekHeaderList.setTotalAmount(mdoubleTotAmt + "");
                weekHeaderList.setTotalAchivedAmount(mdoubleTotAchivedAmt + "");
            }
            if (!tempList.isEmpty())
                entities.removeAll(tempList);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return mtpRoutePlanList;
    }


    public static void createRTGS(Hashtable<String, String> tableHdr, ArrayList<HashMap<String, String>> itemtable, UIListener uiListener, OnlineODataInterface onlineODataInterface) throws OnlineODataStoreException {
       /* OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                if (TextUtils.isEmpty(tableHdr.get(Constants.IS_UPDATE))) {
                    ODataEntity mtpEntity = createRTGSEntity(tableHdr, itemtable, store);
                    OnlineRequestListener mtpListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);
                    ODataRequestParamSingle feedbackReq = new ODataRequestParamSingleDefaultImpl();
                    feedbackReq.setMode(ODataRequestParamSingle.Mode.Create);
                    feedbackReq.setResourcePath(mtpEntity.getResourcePath());
                    feedbackReq.setPayload(mtpEntity);
                    store.scheduleRequest(feedbackReq, mtpListener);
                } else {
                    updateRTGS(tableHdr, itemtable, onlineODataInterface);
                  *//*  OnlineRequestListener mtpListener = new OnlineRequestListener(Operation.Update.getValue(), uiListener);
                    ODataRequestParamSingle updateReq = new ODataRequestParamSingleDefaultImpl();
                    updateReq.setMode(ODataRequestParamSingle.Mode.Update);
                    updateReq.setResourcePath(mtpEntity.getResourcePath());
                    updateReq.setPayload(mtpEntity);
                    store.scheduleRequest(updateReq, mtpListener);*//*
                }

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }*/
    }

    /*public static ODataEntity createRTGSEntity(Hashtable<String, String> hashtable, ArrayList<HashMap<String, String>> itemhashtable, OnlineODataStore store) throws ODataParserException {
        ODataEntity newHeaderEntity = null;
        ODataEntity newItemEntity = null;
        ArrayList<ODataEntity> tempArray = new ArrayList();
        ArrayList<ODataEntity> tempTextArray = new ArrayList();
        ODataEntity newTextEntity = null;
        try {
            if (hashtable != null) {
                newHeaderEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.CollectionPlanEntity);
                if (TextUtils.isEmpty(hashtable.get(Constants.IS_UPDATE))) {
                    newHeaderEntity.setResourcePath(Constants.CollectionPlan, Constants.CollectionPlan);
                } else {
                    newHeaderEntity.setResourcePath(Constants.CollectionPlan + "(guid'" + hashtable.get(Constants.CollectionPlanGUID) + "')", Constants.CollectionPlan + "(guid'" + hashtable.get(Constants.CollectionPlanGUID) + "')");
                }
                try {
                    store.allocateProperties(newHeaderEntity, PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }
                //If available, it populates the navigation properties of an OData Entity
                store.allocateNavigationProperties(newHeaderEntity);
                newHeaderEntity = getRTGSHeaderEntity(newHeaderEntity, hashtable);
                newTextEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.RouteScheduleSPEntity);


                try {
                    store.allocateProperties(newTextEntity, ODataStore.PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }


                int incremntVal = 0;
                for (int incrementVal = 0; incrementVal < itemhashtable.size(); incrementVal++) {

                    HashMap<String, String> singleRow = itemhashtable.get(incrementVal);

                    incremntVal = incrementVal + 1;

                    newItemEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.CollectionPlanItemDetailEntity);

                    newItemEntity.setResourcePath(Constants.CollectionPlanItemDetails + "(" + singleRow.get(Constants.CollectionPlanItemGUID) + ")", Constants.CollectionPlanItemDetails + "(" + singleRow.get(Constants.CollectionPlanItemGUID) + ")");
                    try {
                        store.allocateProperties(newItemEntity, PropMode.Keys);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }
                    //If available, it populates the navigation properties of an OData Entity
                    store.allocateNavigationProperties(newItemEntity);
                    newItemEntity = getRTGSItemTwoEntity(newItemEntity, singleRow);

                    tempArray.add(incrementVal, newItemEntity);


                }

                ODataEntitySetDefaultImpl itemEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itemEntity.getEntities().add(entity);
                }
                itemEntity.setResourcePath(Constants.CollectionPlanItemDetails);

                ODataNavigationProperty navProp = newHeaderEntity.getNavigationProperty(Constants.CollectionPlanItemDetails);
                navProp.setNavigationContent(itemEntity);
                newHeaderEntity.setNavigationProperty(Constants.CollectionPlanItemDetails, navProp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newHeaderEntity;
    }

    public static void updateRTGS(Hashtable<String, String> table, ArrayList<HashMap<String, String>> itemtable, OnlineODataInterface onlineODataInterface) throws OnlineODataStoreException {
        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();
        if (store != null) {
            try {
                ODataRequestParamBatch requestParamBatch = new ODataRequestParamBatchDefaultImpl();
                ODataRequestChangeSet changeSetItem = new ODataRequestChangeSetDefaultImpl();
                changeSetItem = updateRTGSHeaderRequestBatchList(store, table, changeSetItem);
                changeSetItem = updateRTGSItemTwoParameterBatchList(store, table, itemtable, changeSetItem, 2);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BUNDLE_RESOURCE_PATH, Constants.CollectionPlan);
                OnlineRequestListeners listener = new OnlineRequestListeners(onlineODataInterface, bundle);
                requestParamBatch.add(changeSetItem);
                try {
                    store.scheduleRequest(requestParamBatch, listener);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }
    }

    private static ODataRequestChangeSet updateRTGSHeaderRequestBatchList(OnlineODataStore store, Hashtable<String, String> headerhashtable, ODataRequestChangeSet changeSetItem) throws ODataException {
        ODataRequestParamSingle batchItem = new ODataRequestParamSingleDefaultImpl();
        batchItem.setResourcePath(Constants.CollectionPlan + "(CollectionPlanGUID=guid'" + headerhashtable.get(Constants.CollectionPlanGUID) + "')");
        batchItem.setMode(ODataRequestParamSingle.Mode.Update);
        batchItem.setContentID("1");
        ODataEntity oDataHeaderEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.CollectionPlanEntity);
        oDataHeaderEntity.setResourcePath(Constants.CollectionPlan + "('" + headerhashtable.get(Constants.CollectionPlanGUID) + "')", Constants.CollectionPlan + "('" + headerhashtable.get(Constants.CollectionPlanGUID) + "')");
        store.allocateProperties(oDataHeaderEntity, ODataStore.PropMode.Keys);
        store.allocateNavigationProperties(oDataHeaderEntity);
        oDataHeaderEntity = getRTGSHeaderEntity(oDataHeaderEntity, headerhashtable);
        batchItem.setPayload(oDataHeaderEntity);
        changeSetItem.add(batchItem);
        return changeSetItem;
    }

    private static ODataEntity getRTGSHeaderEntity(ODataEntity newHeaderEntity, Hashtable<String, String> hashtable) {
        newHeaderEntity.getProperties().put(Constants.CollectionPlanGUID,
                new ODataPropertyDefaultImpl(Constants.CollectionPlanGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.CollectionPlanGUID))));
        newHeaderEntity.getProperties().put(Constants.SPGUID,
                new ODataPropertyDefaultImpl(Constants.SPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SPGUID))));
//        newHeaderEntity.getProperties().put(Constants.CollectionPlanDate,
//                new ODataPropertyDefaultImpl(Constants.CollectionPlanDate, UtilConstants.convertDateFormat(hashtable.get(Constants.CollectionPlanDate))));
        newHeaderEntity.getProperties().put(Constants.Period,
                new ODataPropertyDefaultImpl(Constants.Period, hashtable.get(Constants.Period)));
        newHeaderEntity.getProperties().put(Constants.Fiscalyear,
                new ODataPropertyDefaultImpl(Constants.Fiscalyear, hashtable.get(Constants.Fiscalyear)));

        try {
            if (hashtable.get(Constants.CreatedOn) != null && !hashtable.get(Constants.CreatedOn).equalsIgnoreCase("")) {
                newHeaderEntity.getProperties().put(Constants.CreatedOn,
                        new ODataPropertyDefaultImpl(Constants.CreatedOn, UtilConstants.convertDateFormat(hashtable.get(Constants.CreatedOn))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (hashtable.get(Constants.CreatedBy) != null && !hashtable.get(Constants.CreatedBy).equalsIgnoreCase("")) {
                newHeaderEntity.getProperties().put(Constants.CreatedBy,
                        new ODataPropertyDefaultImpl(Constants.CreatedBy, hashtable.get(Constants.CreatedBy)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newHeaderEntity;
    }

    private static ODataEntity getRTGSItemTwoEntity(ODataEntity newItemEntity, HashMap<String, String> singleRow) {
        newItemEntity.getProperties().put(Constants.CollectionPlanItemGUID,
                new ODataPropertyDefaultImpl(Constants.CollectionPlanItemGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.CollectionPlanItemGUID))));


        try {
            newItemEntity.getProperties().put(Constants.CollectionPlanGUID,
                    new ODataPropertyDefaultImpl(Constants.CollectionPlanGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.CollectionPlanGUID))));
        } catch (Exception e) {
            e.printStackTrace();
        }

        newItemEntity.getProperties().put(Constants.CollectionPlanDate,
                new ODataPropertyDefaultImpl(Constants.CollectionPlanDate, UtilConstants.convertDateFormat(singleRow.get(Constants.CollectionPlanDate))));


        newItemEntity.getProperties().put(Constants.CPGUID,
                new ODataPropertyDefaultImpl(Constants.CPGUID, singleRow.get(Constants.CPNo)));

        newItemEntity.getProperties().put(Constants.CPNo,
                new ODataPropertyDefaultImpl(Constants.CPNo, singleRow.get(Constants.CPNo)));

        newItemEntity.getProperties().put(Constants.CPName,
                new ODataPropertyDefaultImpl(Constants.CPName, singleRow.get(Constants.CPName)));

        newItemEntity.getProperties().put(Constants.Remarks,
                new ODataPropertyDefaultImpl(Constants.Remarks, singleRow.get(Constants.Remarks)));

        newItemEntity.getProperties().put(Constants.PlannedValue,
                new ODataPropertyDefaultImpl(Constants.PlannedValue, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.PlannedValue)))));

        try {
            newItemEntity.getProperties().put(Constants.AchievedValue,
                    new ODataPropertyDefaultImpl(Constants.AchievedValue, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.AchievedValue)))));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        newItemEntity.getProperties().put(Constants.Remarks,
                new ODataPropertyDefaultImpl(Constants.Remarks, singleRow.get(Constants.Remarks)));

        newItemEntity.getProperties().put(Constants.Currency,
                new ODataPropertyDefaultImpl(Constants.Currency, singleRow.get(Constants.Currency)));

        try {
            newItemEntity.getProperties().put(Constants.CrdtCtrlArea,
                    new ODataPropertyDefaultImpl(Constants.CrdtCtrlArea, singleRow.get(Constants.CrdtCtrlArea)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            newItemEntity.getProperties().put(Constants.CrdtCtrlAreaDs,
                    new ODataPropertyDefaultImpl(Constants.CrdtCtrlAreaDs, singleRow.get(Constants.CrdtCtrlAreaDs)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (singleRow.get(Constants.CreatedOn) != null && !singleRow.get(Constants.CreatedOn).equalsIgnoreCase("")) {
                newItemEntity.getProperties().put(Constants.CreatedOn,
                        new ODataPropertyDefaultImpl(Constants.CreatedOn, UtilConstants.convertDateFormat(singleRow.get(Constants.CreatedOn))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (singleRow.get(Constants.CreatedBy) != null && !singleRow.get(Constants.CreatedBy).equalsIgnoreCase("")) {
                newItemEntity.getProperties().put(Constants.CreatedBy,
                        new ODataPropertyDefaultImpl(Constants.CreatedBy, singleRow.get(Constants.CreatedBy)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            newItemEntity.getProperties().put(Constants.CPType,
                    new ODataPropertyDefaultImpl(Constants.CPType, "01"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newItemEntity;
    }

    private static ODataRequestChangeSet updateRTGSItemTwoParameterBatchList(OnlineODataStore store, Hashtable<String, String> headerhashtable, ArrayList<HashMap<String, String>> itemhashtable, ODataRequestChangeSet changeSetItem, int type) throws ODataException {
        for (int i = 0; i < itemhashtable.size(); i++) {
            HashMap<String, String> singleRow = itemhashtable.get(i);

            ODataRequestParamSingle batchItem = new ODataRequestParamSingleDefaultImpl();
            batchItem.setResourcePath(Constants.CollectionPlanItemDetails + "(CollectionPlanItemGUID=guid'" + singleRow.get(Constants.CollectionPlanItemGUID) + "')");
            batchItem.setMode(ODataRequestParamSingle.Mode.Update);
            batchItem.setContentID("1");
            ODataEntity oDataHeaderEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.CollectionPlanItemDetailEntity);
            oDataHeaderEntity.setResourcePath(Constants.CollectionPlanItemDetails + "(CollectionPlanItemGUID='" + singleRow.get(Constants.CollectionPlanItemGUID) + "')", Constants.CollectionPlanItemDetails + "(CollectionPlanItemGUID='" + singleRow.get(Constants.CollectionPlanItemGUID) + "')");

            store.allocateProperties(oDataHeaderEntity, ODataStore.PropMode.Keys);
            store.allocateNavigationProperties(oDataHeaderEntity);
            oDataHeaderEntity = getRTGSItemTwoEntity(oDataHeaderEntity, singleRow);
            batchItem.setPayload(oDataHeaderEntity);
            changeSetItem.add(batchItem);
        }

        return changeSetItem;
    }
*/
    public static void updateSO(Hashtable<String, String> table, ArrayList<HashMap<String, String>> itemtable, OnlineODataInterface OdataInterface, Bundle bundle) throws com.arteriatech.mutils.common.OnlineODataStoreException {
       /* OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();
        if (store != null) {
            try {
                ODataRequestParamBatch requestParamBatch = new ODataRequestParamBatchDefaultImpl();
                ODataRequestChangeSet changeSetItem = new ODataRequestChangeSetDefaultImpl();
                changeSetItem = createRequestParameterBatchList(store, table, changeSetItem);
                changeSetItem = createRequestItemParameterBatchList(store, table, itemtable, changeSetItem, 1);
                changeSetItem = createRequestTextParameterBatchList(store, table, itemtable, changeSetItem);
                OnlineRequestListeners batchListener = new OnlineRequestListeners(OdataInterface, bundle);
                requestParamBatch.add(changeSetItem);
                try {
                    store.scheduleRequest(requestParamBatch, batchListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                throw new com.arteriatech.mutils.common.OnlineODataStoreException(e);
            }
        }*/
    }

   /* private static ODataRequestChangeSet createRequestTextParameterBatchList(OnlineODataStore store, Hashtable<String, String> headerhashtable, ArrayList<HashMap<String, String>> itemtable, ODataRequestChangeSet changeSetItem) throws ODataException {
        String textString = headerhashtable.get("item_" + headerhashtable.get(Constants.SONo));
        if (!TextUtils.isEmpty(textString)) {
            ArrayList<HashMap<String, String>> textItemList = UtilConstants.convertToArrayListMap(textString);
            for (int j = 0; j < textItemList.size(); j++) {
                HashMap<String, String> subTextSingleItem = textItemList.get(j);

                ODataRequestParamSingle batchItem = new ODataRequestParamSingleDefaultImpl();
                batchItem.setResourcePath(Constants.SOTexts + "(SONo='" + headerhashtable.get(Constants.SONo) + "',ItemNo='000000',TextID='0001',TextCategory='H')");
                batchItem.setMode(ODataRequestParamSingle.Mode.Update);
                batchItem.setContentID("1");
                ODataEntity oDataHeaderEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.SOS_SO_TEXT_ENTITY);
                oDataHeaderEntity.setResourcePath(Constants.SOTexts + "(0001)", Constants.SOTexts + "(0001)");

                store.allocateProperties(oDataHeaderEntity, ODataStore.PropMode.Keys);
                store.allocateNavigationProperties(oDataHeaderEntity);

                oDataHeaderEntity = getTextEntity(oDataHeaderEntity, subTextSingleItem);
                batchItem.setPayload(oDataHeaderEntity);
                changeSetItem.add(batchItem);
            }


        }
        return changeSetItem;
    }

    public static boolean getCSRFTokenRefresh(String qry) throws OfflineODataStoreException {
        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();
        if (store != null) {
            try {
                ODataRequestParamSingle request = new ODataRequestParamSingleDefaultImpl();
                request.setMode(ODataRequestParamSingle.Mode.Read);
                request.setResourcePath(qry);
                //Send a request to read the Invoices from the local database
                store.executeRequest(request);
                *//*ODataResponseSingle response = (ODataResponseSingle) store.executeRequest(request);
                //Check if the response is an error
                if (response.getPayloadType() == ODataPayload.Type.Error) {
                    ODataErrorDefaultImpl error = (ODataErrorDefaultImpl)
                            response.getPayload();
                    throw new OfflineODataStoreException(error.getMessage());
                    //Check if the response contains EntitySet
                } else if (response.getPayloadType() == ODataPayload.Type.EntitySet) {
                    ODataEntitySet feed = (ODataEntitySet) response.getPayload();
                    List<ODataEntity> entities = feed.getEntities();
                    //Retrieve the data from the response
                    for (ODataEntity entity : entities) {

                    }

                } else {
                    throw new OfflineODataStoreException(Constants.invalid_payload_entityset_expected + response.getPayloadType().name());
                }*//*

            } catch (Exception e) {
                throw new OfflineODataStoreException(e);
            }
        }
        return true;
    }*/

    public static void getUserRollInfo(String resourcePath,Context context,AsyncTaskCallBack asyncTaskCallBack) throws OnlineODataStoreException, ODataContractViolationException, ODataParserException, ODataNetworkException {

       /* OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();
        ODataProperty property;
        ODataPropMap properties;
        if (store != null) {
            ODataRequestParamSingle request = new ODataRequestParamSingleDefaultImpl();
            request.setMode(ODataRequestParamSingle.Mode.Read);
            request.setResourcePath(resourcePath);
            ODataResponseSingle response = (ODataResponseSingle) store.executeRequest(request);
            //Check if the response is an error
            if (response.getPayloadType() == ODataPayload.Type.Error) {
                ODataErrorDefaultImpl error = (ODataErrorDefaultImpl)
                        response.getPayload();
                throw new OnlineODataStoreException(error.getMessage());
                //Check if the response contains EntitySet
            } else if (response.getPayloadType() == ODataPayload.Type.EntitySet) {
                ODataEntitySet feed = (ODataEntitySet) response.getPayload();
                List<ODataEntity> entities = feed.getEntities();
                //Retrieve the data from the response
                for (ODataEntity entity : entities) {
                    properties = entity.getProperties();
                    property = properties.get(Constants.AuthOrgTypeID);
                    String typeId = property.getValue().toString();
                    if (!TextUtils.isEmpty(typeId) && typeId.equalsIgnoreCase("000014")) {

                        property = properties.get(Constants.LoginID);
                        if(property!=null) {
                            String loginID = property.getValue().toString();
                            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME,0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constants.USERROLELOGINID, loginID);
                            editor.apply();
                        }
                        property = properties.get(Constants.AuthOrgValue);
                        if (property != null) {
                            return property.getValue().toString();
                        }
                    }
                }
            }
        }

















          *//*
            try {

                ODataRequestParamBatch requestParamBatch = new ODataRequestParamBatchDefaultImpl();
                ODataRequestParamSingle batchItem = new ODataRequestParamSingleDefaultImpl();
                batchItem.setResourcePath(resourcePath);
                batchItem.setMode(ODataRequestParamSingle.Mode.Read);
                requestParamBatch.add(batchItem);
                // Send request synchronously
                ODataResponse oDataResponse = store.executeRequest(requestParamBatch);
                // Get batch response
                if (oDataResponse instanceof ODataResponseBatchDefaultImpl) {
                    ODataResponseBatch batchResponse = (ODataResponseBatch) oDataResponse;
                    List<ODataResponseBatchItem> responses = batchResponse.getResponses();
                    for (ODataResponseBatchItem response : responses) {
                        // Check if batch item is a change set
                        if (response instanceof ODataResponseChangeSetDefaultImpl) {
                            // Todo here multiple batch request will come
                        } else {
                            ODataResponseSingle oDataResponseSingle = (ODataResponseSingleDefaultImpl) response;
                            ODataPayload oDataPayload = oDataResponseSingle.getPayload();
                            if (oDataPayload != null) {
                                if (oDataPayload instanceof ODataError) {
                                    ODataError oError = (ODataError) oDataPayload;
                                    String uiMessage = oError.getMessage();
                                } else {
                                    // TODO Check if batch item is a single READ request
                                    ODataEntitySet feed = (ODataEntitySet) oDataResponseSingle.getPayload();
                                    // Get the list of ODataEntity
                                    List<ODataEntity> entities = feed.getEntities();

//                                    ODataEntity entity = (ODataEntity) oDataResponseSingle.getPayload();
                                    for (ODataEntity entity : entities) {
                                    properties = entity.getProperties();
                                    property = properties.get(Constants.AuthOrgValue);
                                    if (property!=null){
                                        return property.getValue().toString();
                                    }
                                    *//**//*userLoginBean.setLoginID(property.getValue().toString());
                                    property = properties.get(Constants.Application);
                                    userLoginBean.setApplication(property.getValue().toString());
                                    property = properties.get(Constants.ERPLoginID);
                                    userLoginBean.setERPLoginID(property.getValue().toString());
                                    property = properties.get(Constants.RoleID);
                                    userLoginBean.setRoleID(property.getValue().toString());
                                    property = properties.get(Constants.LoginName);
                                    userLoginBean.setLoginName(property.getValue().toString());
                                    property = properties.get(Constants.RoleDesc);
                                    userLoginBean.setRoleDesc(property.getValue().toString());
                                    property = properties.get(Constants.RoleCatID);
                                    userLoginBean.setRoleCatID(property.getValue().toString());
                                    property = properties.get(Constants.RoleCatDesc);
                                    userLoginBean.setRoleCatDesc(property.getValue().toString());
                                    property = properties.get(Constants.IsActive);
                                    userLoginBean.setIsActive(property.getValue().toString());
                                    property = properties.get(Constants.UserFunction1);
                                    userLoginBean.setUserFunction1ID(property.getValue().toString());
                                    property = properties.get(Constants.UserFunction1Desc);
                                    userLoginBean.setUserFunction1Desc(property.getValue().toString());
                                    property = properties.get(Constants.UserFunction2);
                                    userLoginBean.setUserFunction2ID(property.getValue().toString());
                                    property = properties.get(Constants.UserFunction2Desc);
                                    userLoginBean.setUserFunction2Desc(property.getValue().toString());*//**//*
                                    }
                                }
                            }
                        }

                    }
                }

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }*/
        final String[] authOrgTypeID = {""};
        OnlineManager.doOnlineGetRequest(resourcePath, context, event -> {
            if (event.getResponseStatusCode() == 200) {
//                String responseBody = getJSONBody(event);

                JSONObject jsonObj = null;
                try {
//                    jsonObj = new JSONObject(responseBody);
                    JSONObject dObject =  getJSONBody(event);
                    Log.d("OnlineManager", "getUserRollInfo: " + dObject + " " + event.getResponseStatusCode());
                    JSONArray resultArray = dObject.getJSONArray("results");
                    for (int i = 0; i < resultArray.length(); i++) {
                        try {
                            JSONObject jsonObject = resultArray.getJSONObject(i);
                            String typeId = jsonObject.optString(Constants.AuthOrgTypeID);
                            if (!TextUtils.isEmpty(typeId) && typeId.equalsIgnoreCase("000014")) {
                                authOrgTypeID[0] = jsonObject.optString(Constants.AuthOrgValue);
                                if (!TextUtils.isEmpty(authOrgTypeID[0])) {
                                    SharedPreferences sharedPerf = context.getSharedPreferences(Constants.PREFS_NAME, 0);
                                    SharedPreferences.Editor editor = sharedPerf.edit();
                                    editor.putString(Constants.USERROLE, authOrgTypeID[0]);
                                    editor.putBoolean(Constants.isRollResponseGot, true);
                                    editor.apply();
                                    asyncTaskCallBack.onStatus(true, "");
                                    Log.d("getUserRollInfor", "true");
                                } else {
                                    asyncTaskCallBack.onStatus(false, Constants.error_txt + "Not able to get roll information");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    String errorMsg="";
                    try {
                        errorMsg = Constants.getErrorMessage(event,context);
                        LogManager.writeLogError(errorMsg);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        errorMsg=e.getMessage();
                        LogManager.writeLogError(e.getMessage());
                    }
                    asyncTaskCallBack.onStatus(false,Constants.error_txt + errorMsg);
                } catch (Throwable e) {
                    asyncTaskCallBack.onStatus(false,Constants.error_txt + e.getMessage());
                    e.printStackTrace();
                }
            }
        }, iError -> {
            iError.printStackTrace();
            String errormessage = "";
            errormessage = ConstantsUtils.geterrormessageForInternetlost(iError.getMessage(),context);
            if(TextUtils.isEmpty(errormessage)){
                errormessage = iError.getMessage();
            }
            asyncTaskCallBack.onStatus(false, errormessage);
            Log.d("OnlineManager", "getUserRollInfo: ");
        });
    }

    /*public static String getUserPartnersnfo(String resourcePath,Context context) throws OnlineODataStoreException, ODataContractViolationException, ODataParserException, ODataNetworkException {

        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();
        ODataProperty property;
        ODataPropMap properties;
        if (store != null) {
            ODataRequestParamSingle request = new ODataRequestParamSingleDefaultImpl();
            request.setMode(ODataRequestParamSingle.Mode.Read);
            request.setResourcePath(resourcePath);
            ODataResponseSingle response = (ODataResponseSingle) store.executeRequest(request);
            //Check if the response is an error
            if (response.getPayloadType() == ODataPayload.Type.Error) {
                ODataErrorDefaultImpl error = (ODataErrorDefaultImpl)
                        response.getPayload();
                throw new OnlineODataStoreException(error.getMessage());
                //Check if the response contains EntitySet
            } else if (response.getPayloadType() == ODataPayload.Type.EntitySet) {
                ODataEntitySet feed = (ODataEntitySet) response.getPayload();
                List<ODataEntity> entities = feed.getEntities();
                //Retrieve the data from the response
                for (ODataEntity entity : entities) {
                    properties = entity.getProperties();
                    property = properties.get(Constants.PartnerTypeID);
                    if(property!=null) {
                        return (String) property.getValue();
                    }
                }
            }
        }

















          *//*
            try {

                ODataRequestParamBatch requestParamBatch = new ODataRequestParamBatchDefaultImpl();
                ODataRequestParamSingle batchItem = new ODataRequestParamSingleDefaultImpl();
                batchItem.setResourcePath(resourcePath);
                batchItem.setMode(ODataRequestParamSingle.Mode.Read);
                requestParamBatch.add(batchItem);
                // Send request synchronously
                ODataResponse oDataResponse = store.executeRequest(requestParamBatch);
                // Get batch response
                if (oDataResponse instanceof ODataResponseBatchDefaultImpl) {
                    ODataResponseBatch batchResponse = (ODataResponseBatch) oDataResponse;
                    List<ODataResponseBatchItem> responses = batchResponse.getResponses();
                    for (ODataResponseBatchItem response : responses) {
                        // Check if batch item is a change set
                        if (response instanceof ODataResponseChangeSetDefaultImpl) {
                            // Todo here multiple batch request will come
                        } else {
                            ODataResponseSingle oDataResponseSingle = (ODataResponseSingleDefaultImpl) response;
                            ODataPayload oDataPayload = oDataResponseSingle.getPayload();
                            if (oDataPayload != null) {
                                if (oDataPayload instanceof ODataError) {
                                    ODataError oError = (ODataError) oDataPayload;
                                    String uiMessage = oError.getMessage();
                                } else {
                                    // TODO Check if batch item is a single READ request
                                    ODataEntitySet feed = (ODataEntitySet) oDataResponseSingle.getPayload();
                                    // Get the list of ODataEntity
                                    List<ODataEntity> entities = feed.getEntities();

//                                    ODataEntity entity = (ODataEntity) oDataResponseSingle.getPayload();
                                    for (ODataEntity entity : entities) {
                                    properties = entity.getProperties();
                                    property = properties.get(Constants.AuthOrgValue);
                                    if (property!=null){
                                        return property.getValue().toString();
                                    }
                                    *//**//*userLoginBean.setLoginID(property.getValue().toString());
                                    property = properties.get(Constants.Application);
                                    userLoginBean.setApplication(property.getValue().toString());
                                    property = properties.get(Constants.ERPLoginID);
                                    userLoginBean.setERPLoginID(property.getValue().toString());
                                    property = properties.get(Constants.RoleID);
                                    userLoginBean.setRoleID(property.getValue().toString());
                                    property = properties.get(Constants.LoginName);
                                    userLoginBean.setLoginName(property.getValue().toString());
                                    property = properties.get(Constants.RoleDesc);
                                    userLoginBean.setRoleDesc(property.getValue().toString());
                                    property = properties.get(Constants.RoleCatID);
                                    userLoginBean.setRoleCatID(property.getValue().toString());
                                    property = properties.get(Constants.RoleCatDesc);
                                    userLoginBean.setRoleCatDesc(property.getValue().toString());
                                    property = properties.get(Constants.IsActive);
                                    userLoginBean.setIsActive(property.getValue().toString());
                                    property = properties.get(Constants.UserFunction1);
                                    userLoginBean.setUserFunction1ID(property.getValue().toString());
                                    property = properties.get(Constants.UserFunction1Desc);
                                    userLoginBean.setUserFunction1Desc(property.getValue().toString());
                                    property = properties.get(Constants.UserFunction2);
                                    userLoginBean.setUserFunction2ID(property.getValue().toString());
                                    property = properties.get(Constants.UserFunction2Desc);
                                    userLoginBean.setUserFunction2Desc(property.getValue().toString());*//**//*
                                    }
                                }
                            }
                        }

                    }
                }

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }*//*
        return "";
    }*/

    /*get Dealer stock check list*/
    public static ArrayList<DealerStockBean> getDealerStockMatList(List<ODataEntity> entities, ArrayList<DealerStockBean> soDefaultItemBeanList, Context mContext) throws OfflineODataStoreException {
        ArrayList<DealerStockBean> soItemBeanList = new ArrayList<>();
        DealerStockBean stkItemBean;
        ODataProperty property;
        ODataPropMap properties;
        String convertDateFormat = "";
        String oldDate = "";
        try {
            if (entities != null && !entities.isEmpty()) {
                //Retrieve the data from the response
                ArrayList<ConfigTypeValues> configTypeValuesList = OfflineManager.checkMaterialCodeDisplay();
                String qry = Constants.ConfigTypesetTypes + "?$filter=" + Constants.Typeset + " eq '" + Constants.UOMNO0 + "' ";
                HashMap<String, String> mapUOM = OfflineManager.getUOMMapVal(qry);
                for (ODataEntity entity : entities) {
                    properties = entity.getProperties();
                    stkItemBean = new DealerStockBean();
                    property = properties.get(Constants.Material);
                    stkItemBean.setMaterialNo((String) property.getValue());
                    if (isCheckedDealerStock(soDefaultItemBeanList, stkItemBean)) {
                        stkItemBean.setChecked(true);
                    } else {
                        stkItemBean.setChecked(false);
                    }

                    property = properties.get(Constants.StockGuid);
                    if (property != null) {
                        ODataGuid mInvGUID = (ODataGuid) property.getValue();
                        stkItemBean.setStockValue(mInvGUID.guidAsString36().toUpperCase());
                    }

                    property = properties.get(Constants.MaterialDesc);
                    stkItemBean.setMaterialDesc((String) property.getValue());

                    property = properties.get(Constants.AsOnDate);
                    if (property != null) {
                        convertDateFormat = UtilConstants.convertCalenderToStringFormat((GregorianCalendar) property.getValue());
                        stkItemBean.setAsOnDate(convertDateFormat);
                    }
                    property = properties.get(Constants.Brand);
                    stkItemBean.setBrand((String) property.getValue());

                    property = properties.get(Constants.BrandDesc);
                    stkItemBean.setBrandDesc((String) property.getValue());

                    property = properties.get(Constants.UOM);
                    String mStrUom = (String) property.getValue();
                    stkItemBean.setUOM(mStrUom);

                    property = properties.get(Constants.Unrestricted);
                    String qty = "0.000";
                    if (property != null) {
                        qty = property.getValue().toString();
                    }
                    try {
                        if (mapUOM.containsKey(mStrUom))
                            stkItemBean.setUnrestrictedQty(trimQtyDecimalPlace(qty));
                        else
                            stkItemBean.setUnrestrictedQty(qty);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    if (!configTypeValuesList.isEmpty()) {
                        stkItemBean.setMatNoAndDesc(mContext.getString(R.string.po_details_display_value, stkItemBean.getMaterialDesc(), stkItemBean.getMaterialNo()));
                    } else {
                        stkItemBean.setMatNoAndDesc(stkItemBean.getMaterialDesc());
                    }
                    stkItemBean.setDecimalCheck(checkNoUOMZero(stkItemBean.getUOM()));
                    stkItemBean.setEnterdQty("");

                    if (entity.getEtag() != null) {
                        stkItemBean.setEtag(entity.getEtag());
                    } else {
                        stkItemBean.setEtag("");
                    }

                    //  soItemBean.setMatNoAndDesc(soItemBean.getMatDesc() + soItemBean.getMatCode());
                    if (convertDateFormat.equalsIgnoreCase(oldDate) || TextUtils.isEmpty(oldDate)) {
                        soItemBeanList.add(stkItemBean);
                        oldDate = convertDateFormat;
                    } else {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            throw new OfflineODataStoreException(e);
        }

        return soItemBeanList;
    }

    public static boolean isCheckedDealerStock(ArrayList<DealerStockBean> soDefaultItemBeanList, DealerStockBean soItemBeanNew) {
        if (soDefaultItemBeanList != null && !soDefaultItemBeanList.isEmpty()) {
            int i = 0;
            boolean isMatPresent = false;
            for (DealerStockBean soItemBean : soDefaultItemBeanList) {
                if (soItemBean.getMaterialNo().equalsIgnoreCase(soItemBeanNew.getMaterialNo())) {
                    isMatPresent = true;
                    soItemBeanNew.setEnterdQty(soItemBean.getEnterdQty());
                    soItemBeanNew.setChecked(true);
                    break;
                } else {
                    soItemBeanNew.setEnterdQty("0");
                    soItemBeanNew.setChecked(false);
                }
                i++;
            }
            if (isMatPresent) {
//                soDefaultItemBeanList.remove(i);
                return true;
            }
        } else {
            soItemBeanNew.setEnterdQty("0");
            soItemBeanNew.setChecked(false);
        }
        return false;
    }

    public static List<ReturnOrderBean> getReturnOrderList(Context mContext, List<ODataEntity> list) {
        List<ReturnOrderBean> returnOrderBeanList = new ArrayList<>();
        ReturnOrderBean returnOrderBean;
        ODataProperty property;
        ODataPropMap properties;
        for (ODataEntity entity : list) {
            returnOrderBean = new ReturnOrderBean();
            properties = entity.getProperties();
            try {
                property = properties.get(Constants.RetOrdNo);
                if (property != null) {
                    returnOrderBean.setRetOrdNo(property.getValue().toString());
                }
                String convertDateFormat2 = null;
                property = properties.get(Constants.OrderDate);
                if (property != null) {
//                    convertDateFormat2 = UtilConstants.convertDateIntoDeviceFormat(mContext, UtilConstants.convertCalenderToStringFormat((GregorianCalendar) property.getValue()));
                    convertDateFormat2 = ConstantsUtils.convertCalenderToDisplayDateFormat((GregorianCalendar) property.getValue());
                    returnOrderBean.setOrderDate(convertDateFormat2);
                }

                property = properties.get(Constants.MaterialDesc);
                if (property != null) {
                    returnOrderBean.setMaterialDesc(property.getValue().toString());
                }

                property = properties.get(Constants.NetAmount);
                String outAmtStr = "0";
                if (property != null) {
                    BigDecimal mStrAmount = (BigDecimal) property.getValue();
                    outAmtStr = mStrAmount.toString();
                }
                returnOrderBean.setNetAmount(outAmtStr);

                property = properties.get(Constants.Currency);
                if (property != null) {
                    returnOrderBean.setCurrency(property.getValue().toString());
                }

                property = properties.get(Constants.UOM);
                if (property != null) {
                    returnOrderBean.setUOM(property.getValue().toString());
                }

                property = properties.get(Constants.InvoiceQty);
                if (property != null) {
                    returnOrderBean.setInvoiceQty(property.getValue().toString());
                }
                property = properties.get(Constants.Quantity);
                String qty = "0.000";
                if (property != null) {
                    qty = property.getValue().toString();
                }
                if (checkNoUOMZero(returnOrderBean.getUOM()))
                    returnOrderBean.setQuantity(trimQtyDecimalPlace(qty));
                else
                    returnOrderBean.setQuantity(qty);

                property = properties.get(Constants.InvoiceNo);
                if (property != null) {
                    returnOrderBean.setInvoiceNo(property.getValue().toString());
                }
                property = properties.get(Constants.StatusID);
                if (property != null) {
                    returnOrderBean.setStatusID(property.getValue().toString());
                }
                property = properties.get(Constants.GRStatusID);
                if (property != null) {
                    returnOrderBean.setGRStatusID(property.getValue().toString());
                }
                returnOrderBeanList.add(returnOrderBean);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        /*custom sorting start*/
        List<ReturnOrderBean> roArrayList = new ArrayList<>();
        String lastDate = "";
        for (int i = 0; i < returnOrderBeanList.size(); i++) {
            ReturnOrderBean soListBean = returnOrderBeanList.get(i);
            String currentDate = soListBean.getOrderDate();
            if (!currentDate.equalsIgnoreCase(lastDate)) {
                ArrayList<ReturnOrderBean> tempNumberSortingList = new ArrayList<>();
                for (int j = i; j < returnOrderBeanList.size(); j++) {
                    ReturnOrderBean soListBean1 = returnOrderBeanList.get(j);
                    if (soListBean1.getOrderDate().equalsIgnoreCase(currentDate)) {
                        tempNumberSortingList.add(soListBean1);
                    } else {
                        break;
                    }
                }
                Collections.sort(tempNumberSortingList, new Comparator<ReturnOrderBean>() {
                    public int compare(ReturnOrderBean one, ReturnOrderBean other) {
                        BigInteger i1 = null;
                        BigInteger i2 = null;
                        try {
                            i1 = new BigInteger(one.getRetOrdNo());
                        } catch (NumberFormatException e) {
                        }

                        try {
                            i2 = new BigInteger(other.getRetOrdNo());
                        } catch (NumberFormatException e) {
                        }
                        if (i1 != null && i2 != null) {
                            return i2.compareTo(i1);
                        } else {
                            return other.getRetOrdNo().compareTo(one.getRetOrdNo());
                        }
                    }
                });
                roArrayList.addAll(tempNumberSortingList);
                lastDate = currentDate;
            }
        }
        /*custom sorting end*/
        return roArrayList;
    }

    /*Return Order details*/
    public static ReturnOrderBean getRODetails(ODataRequestExecution oDataRequestExecution, Context mContext, String mStrInstanceId, boolean isSessionRequired, int isComingFrom) throws Exception {
        ReturnOrderBean roListBean = null;
        ODataProperty property;
        ODataPropMap properties;
        try {
            ODataResponseSingle oDataResponseSingle = (ODataResponseSingleDefaultImpl) oDataRequestExecution.getResponse();
            ODataEntity entity = (ODataEntity) oDataResponseSingle.getPayload();
            ArrayList<ConfigTypeValues> configTypeValuesList = OfflineManager.checkMaterialCodeDisplay();
            roListBean = new ReturnOrderBean();
            properties = entity.getProperties();
            property = properties.get(Constants.RetOrdNo);
            roListBean.setRetOrdNo(property.getValue().toString());

            try {
                property = properties.get(Constants.Tax1Amt);
                roListBean.setTax1Amt(property.getValue().toString());
                property = properties.get(Constants.Tax2Amt);
                roListBean.setTax2Amt(property.getValue().toString());
                property = properties.get(Constants.Tax3Amt);
                roListBean.setTax3Amt(property.getValue().toString());
                property = properties.get(Constants.NetAmount);
                roListBean.setNetAmount(property.getValue().toString());
//                property = properties.get(Constants.TotalAmount);
//                roListBean.setTotalAmt(property.getValue().toString());

                property = properties.get(Constants.Currency);
                roListBean.setCurrency(property.getValue().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            property = properties.get(Constants.SalesAreaDesc);
            roListBean.setSalesAreaDesc(property.getValue().toString());
            property = properties.get(Constants.SalesArea);
            roListBean.setSalesArea(property.getValue().toString());
            property = properties.get(Constants.OrderTypeDesc);
            roListBean.setOrderTypeDesc(property.getValue().toString());
            property = properties.get(Constants.OrderType);
            roListBean.setOrderType(property.getValue().toString());
            property = properties.get(Constants.ShipToPartyName);
            roListBean.setShipToPartyName(property.getValue().toString());
            property = properties.get(Constants.ShipToParty);
            roListBean.setShipToParty(property.getValue().toString());
            property = properties.get(Constants.CustomerName);
            roListBean.setCustomerName(property.getValue().toString());
            property = properties.get(Constants.CustomerNo);
            roListBean.setCustomerNo(property.getValue().toString());
            property = properties.get(Constants.OrderReasonID);
            roListBean.setOrderReasonID(property.getValue().toString());
            property = properties.get(Constants.OrderReasonDesc);
            roListBean.setOrderReasonDesc(property.getValue().toString());
            property = properties.get(Constants.SalesOff);
            roListBean.setSalesOff(property.getValue().toString());
            property = properties.get(Constants.SalesOffDesc);
            roListBean.setSalesOffDesc(property.getValue().toString());
            property = properties.get(Constants.StatusID);
            roListBean.setStatusID(property.getValue().toString());
            property = properties.get(Constants.GRStatusID);
            roListBean.setGRStatusID(property.getValue().toString());
            property = properties.get(Constants.District);
            roListBean.setDistrict(property.getValue().toString());
            property = properties.get(Constants.PostalCode);
            roListBean.setPostalCode(property.getValue().toString());
            property = properties.get(Constants.City);
            roListBean.setCity(property.getValue().toString());
            property = properties.get(Constants.State);
            roListBean.setState(property.getValue().toString());
            property = properties.get(Constants.StateDesc);
            roListBean.setStateDesc(property.getValue().toString());
            property = properties.get(Constants.CountryCode);
            roListBean.setCountryCode(property.getValue().toString());
            property = properties.get(Constants.CountryDesc);
            roListBean.setCountryDesc(property.getValue().toString());
            roListBean.setAddress(SOUtils.getAddressValue(properties));
            property = properties.get(Constants.OrderDate);
//            String convertDateFormat2 = UtilConstants.convertDateIntoDeviceFormat(mContext, UtilConstants.convertCalenderToStringFormat((GregorianCalendar) property.getValue()));
            String convertDateFormat2 = ConstantsUtils.convertCalenderToDisplayDateFormat((GregorianCalendar) property.getValue());
            roListBean.setOrderDate(convertDateFormat2);
            /*ROItem details*/
            ODataNavigationProperty soItemDetailsProp = entity.getNavigationProperty(Constants.ReturnOrderItemDetails);
            ODataEntitySet feed = (ODataEntitySet) soItemDetailsProp.getNavigationContent();
            List<ODataEntity> entities = feed.getEntities();
            ArrayList<ReturnOrderItemBean> roItemBeanArrayList = new ArrayList<>();
            ReturnOrderItemBean roItemBean;
            BigDecimal unitPrice = new BigDecimal("0.0");
            BigDecimal discAmt = new BigDecimal("0.0");
            for (ODataEntity soItemEntity : entities) {
                roItemBean = new ReturnOrderItemBean();
                properties = soItemEntity.getProperties();
                roItemBean.setRetOrdNo(roListBean.getRetOrdNo());
                roItemBean.setOrderDate(roListBean.getOrderDate());
                property = properties.get(Constants.Material);
                roItemBean.setMaterial(property.getValue().toString());
                property = properties.get(Constants.MaterialDesc);
                roItemBean.setMaterialDesc(property.getValue().toString());
                property = properties.get(Constants.UOM);
                roItemBean.setUOM(property.getValue().toString());
                property = properties.get(Constants.Quantity);
                String qty = "0.000";
                if (property != null) {
                    qty = property.getValue().toString();
                }
                if (checkNoUOMZero(roItemBean.getUOM()))
                    roItemBean.setQuantity(trimQtyDecimalPlace(qty));
                else
                    roItemBean.setQuantity(qty);
                property = properties.get(Constants.InvoiceQty);
                roItemBean.setInvoiceQty(property.getValue().toString());
                property = properties.get(Constants.MRP);
                roItemBean.setMRP(property.getValue().toString());
                property = properties.get(Constants.UnitPrice);
                roItemBean.setUnitPrice(property.getValue().toString());
                property = properties.get(Constants.NetAmount);
                roItemBean.setNetAmount(property.getValue().toString());
                property = properties.get(Constants.UnitPrice);
                unitPrice.add((BigDecimal) property.getValue());
                roItemBean.setUnitPrice(property.getValue().toString());
                property = properties.get(Constants.PlantDesc);
                roItemBean.setPlantDesc(property.getValue().toString());
                property = properties.get(Constants.Plant);
                roItemBean.setPlant(property.getValue().toString());
                property = properties.get(Constants.ReferenceUOM);
                roItemBean.setReferenceUOM(property.getValue().toString());
                property = properties.get(Constants.Currency);
                roItemBean.setCurrency(property.getValue().toString());
                property = properties.get(Constants.ItemNo);
                roItemBean.setItemNo(property.getValue().toString());
                property = properties.get(Constants.GRStatusID);
                roItemBean.setGRStatusID(property.getValue().toString());
                property = properties.get(Constants.StatusID);
                roItemBean.setStatusID(property.getValue().toString());
                property = properties.get(Constants.Batch);
                roItemBean.setBatch(property.getValue().toString());
                property = properties.get(Constants.MFD);
                if (property != null) {
//                    String convertMFD = UtilConstants.convertDateIntoDeviceFormat(mContext, UtilConstants.convertCalenderToStringFormat((GregorianCalendar) property.getValue()));
                    String convertMFD = ConstantsUtils.convertCalenderToDisplayDateFormat((GregorianCalendar) property.getValue());
                    roItemBean.setMFD(convertMFD);
                } else {
                    roItemBean.setMFD("");
                }
                property = properties.get(Constants.ExpiryDate);
                if (property != null) {
//                    String convertExpDate = UtilConstants.convertDateIntoDeviceFormat(mContext, UtilConstants.convertCalenderToStringFormat((GregorianCalendar) property.getValue()));
                    String convertMFD = ConstantsUtils.convertCalenderToDisplayDateFormat((GregorianCalendar) property.getValue());
                    roItemBean.setExpiryDate(convertMFD);
                } else {
                    roItemBean.setExpiryDate("");
                }
                property = properties.get(Constants.PriDiscAmt);
                discAmt.add((BigDecimal) property.getValue());
                roItemBean.setPriDiscAmt(property.getValue().toString());
                property = properties.get(Constants.PriDiscPerc);
                roItemBean.setPriDiscPerc(property.getValue().toString());
                property = properties.get(Constants.Tax1Amt);
                roItemBean.setTax1Amt(property.getValue().toString());
                property = properties.get(Constants.Tax2Amt);
                roItemBean.setTax2Amt(property.getValue().toString());
                property = properties.get(Constants.Tax3Amt);
                roItemBean.setTax3Amt(property.getValue().toString());
                property = properties.get(Constants.Tax1Percent);
                roItemBean.setTax1Percent(property.getValue().toString());
                property = properties.get(Constants.Tax2Percent);
                roItemBean.setTax2Percent(property.getValue().toString());
                property = properties.get(Constants.Tax3Percent);
                roItemBean.setTax3Percent(property.getValue().toString());
                if (!configTypeValuesList.isEmpty()) {
                    roItemBean.setROMaterialDescAndNo(mContext.getString(R.string.po_details_display_value, roItemBean.getMaterialDesc(), roItemBean.getMaterial()));
                } else {
                    roItemBean.setROMaterialDescAndNo(roItemBean.getMaterial());
                }
                roItemBeanArrayList.add(roItemBean);
            }

            roListBean.setUnitPrice(String.valueOf(unitPrice));
            roListBean.setPriDiscAmt(String.valueOf(discAmt));
            roListBean.setRoItemList(roItemBeanArrayList);
            return roListBean;
        } catch (Exception e) {
            e.printStackTrace();
            throw new com.arteriatech.mutils.common.OnlineODataStoreException(e.getMessage());
        }
    }

    public static ArrayList<DashBoardBean> getTransactionCountDBs(List<ODataEntity> entities) {
        ArrayList<DashBoardBean> alDashBoardBean = new ArrayList<>();
        ArrayList<DashBoardBean> alDashBoardBeanTemp = new ArrayList<>();
        DashBoardBean dashBoardBeanRTGS = new DashBoardBean();
//        dashBoardBeanRTGS.setApplication("RTGS Value( S & D )");
//        dashBoardBeanRTGS.setActive("0.0");
//        dashBoardBeanRTGS.setTotal("0.0");
        DashBoardBean dashBoardBeanRTGSFocus = new DashBoardBean();
//        dashBoardBeanRTGSFocus.setApplication("RTGS Value( Focus Brand )");
//        dashBoardBeanRTGSFocus.setActive("0.0");
//        dashBoardBeanRTGSFocus.setTotal("0.0");
        DashBoardBean dashBoardBeanSecSale = new DashBoardBean();
        dashBoardBeanSecSale.setApplication("Sec Order");
        dashBoardBeanSecSale.setActive("0");
        dashBoardBeanSecSale.setTotal("0");
        DashBoardBean dashBoardBeanSecInv = new DashBoardBean();
        dashBoardBeanSecInv.setApplication("Sec Invoice");
        dashBoardBeanSecInv.setActive("0");
        dashBoardBeanSecInv.setTotal("0");
       /* DashBoardBean dashBoardBeanOutsVal = new DashBoardBean();
        dashBoardBeanOutsVal.setApplication("Retailer Outstanding");
        dashBoardBeanOutsVal.setActive("0");
      //  dashBoardBeanOutsVal.setTotal("0");
        DashBoardBean dashBoardBeanStockVal = new DashBoardBean();
        dashBoardBeanStockVal.setApplication("Retailers Stock Value");
        dashBoardBeanStockVal.setActive("0");*/
        //  dashBoardBeanStockVal.setTotal("0");

        if (entities != null) {

            ODataProperty property;
            ODataPropMap properties = null;
            try {
                //Define the resource path
                for (ODataEntity dataEntity : entities) {

                    properties = dataEntity.getProperties();
                    property = properties.get(Constants.Application1);
                    if (((String) property.getValue()).equalsIgnoreCase("RTGS")) {
                        property = properties.get(Constants.DmsDivision);
                        String DmsDivision = property.getValue().toString();
                        if(DmsDivision.equalsIgnoreCase("01")) {
                            //dashBoardBeanRTGS.setApplication("RTGS Value ( S & D )");
                            dashBoardBeanRTGS.setApplication("RTGS Value ( Total Balance )");
                            property = properties.get(Constants.ActaulValue);
                            if (property != null) {
                                if (!dashBoardBeanRTGS.getActive().isEmpty()) {
                                    String actual = property.getValue().toString();
                                    double bigDecimal = (Double.parseDouble(dashBoardBeanRTGS.getActive())) + Double.parseDouble(actual);
                                    dashBoardBeanRTGS.setActive(String.valueOf(BigDecimal.valueOf(bigDecimal)));
                                } else {
                                    dashBoardBeanRTGS.setActive(property.getValue().toString());
                                }
                            }
                            property = properties.get(Constants.OrderValue);
                            if (property != null && !(property.getValue().toString()).isEmpty()) {
                                if (!dashBoardBeanRTGS.getTotal().isEmpty()) {
                                    String actual = property.getValue().toString();
                                    double bigDecimal = (Double.parseDouble(dashBoardBeanRTGS.getTotal())) + Double.parseDouble(actual);
                                    dashBoardBeanRTGS.setTotal(String.valueOf(BigDecimal.valueOf(bigDecimal)));
                                } else {
                                    dashBoardBeanRTGS.setTotal(property.getValue().toString());
                                }
                            }
                        }else if(DmsDivision.equalsIgnoreCase("10")){
                            dashBoardBeanRTGSFocus.setApplication("RTGS Value ( Focus Brand )");
                            property = properties.get(Constants.ActaulValue);
                            if (property != null) {
                                if (!dashBoardBeanRTGSFocus.getActive().isEmpty()) {
                                    String actual = property.getValue().toString();
                                    double bigDecimal = (Double.parseDouble(dashBoardBeanRTGSFocus.getActive())) + Double.parseDouble(actual);
                                    dashBoardBeanRTGSFocus.setActive(String.valueOf(BigDecimal.valueOf(bigDecimal)));
                                } else {
                                    dashBoardBeanRTGSFocus.setActive(property.getValue().toString());
                                }
                            }
                            property = properties.get(Constants.OrderValue);
                            if (property != null && !(property.getValue().toString()).isEmpty()) {
                                if (!dashBoardBeanRTGSFocus.getTotal().isEmpty()) {
                                    String actual = property.getValue().toString();
                                    double bigDecimal = (Double.parseDouble(dashBoardBeanRTGSFocus.getTotal())) + Double.parseDouble(actual);
                                    dashBoardBeanRTGSFocus.setTotal(String.valueOf(BigDecimal.valueOf(bigDecimal)));
                                } else {
                                    dashBoardBeanRTGSFocus.setTotal(property.getValue().toString());
                                }
                            }
                        }
                    }else if (((String) property.getValue()).equalsIgnoreCase("Secondary Sales Order")) {
                        dashBoardBeanSecSale.setApplication("Sec Order");
                        property = properties.get(Constants.Open);
                        if (property != null && !((String) property.getValue()).isEmpty()){
                            if (!dashBoardBeanSecSale.getActive().isEmpty()) {
                                dashBoardBeanSecSale.setActive(String.valueOf(Integer.parseInt(dashBoardBeanSecSale.getActive()) + Integer.parseInt((String) property.getValue())));
                            }else {
                                dashBoardBeanSecSale.setActive((String) property.getValue());
                            }
                        }
                        property = properties.get(Constants.Total);
                        if (property != null && !(((String) property.getValue()).isEmpty())){
                            if (!dashBoardBeanSecSale.getTotal().isEmpty()) {
                                dashBoardBeanSecSale.setTotal(String.valueOf(Integer.parseInt(dashBoardBeanSecSale.getTotal()) + Integer.parseInt((String) property.getValue())));
                            }else {
                                dashBoardBeanSecSale.setTotal((String) property.getValue());
                            }
                        }
                    }else if (((String) property.getValue()).equalsIgnoreCase("Secondary Invoice")) {
                        dashBoardBeanSecInv.setApplication("Sec Invoice");
                        property = properties.get(Constants.Open);
                        if (property != null && !(((String) property.getValue()).isEmpty())){
                            if (!dashBoardBeanSecInv.getActive().isEmpty()) {
                                dashBoardBeanSecInv.setActive(String.valueOf(Integer.parseInt(dashBoardBeanSecInv.getActive()) + Integer.parseInt((String) property.getValue())));
                            }else {
                                dashBoardBeanSecInv.setActive((String) property.getValue());
                            }
                        }
                        property = properties.get(Constants.Total);
                        if (property != null && !(((String) property.getValue()).isEmpty())){
                            if (!dashBoardBeanSecInv.getTotal().isEmpty()) {
                                dashBoardBeanSecInv.setTotal(String.valueOf(Integer.parseInt(dashBoardBeanSecInv.getTotal()) + Integer.parseInt((String) property.getValue())));
                            }else {
                                dashBoardBeanSecInv.setTotal((String) property.getValue());
                            }
                        }
                    }
                   /* else if (((String) property.getValue()).equalsIgnoreCase("Retailer Outstanding")) {
                        dashBoardBeanOutsVal.setApplication("Retailer Outstanding");
                        property = properties.get(Constants.OrderValue);
                        if (property != null){
                            String orderValue = property.getValue().toString();
                            double bigDecimal = (Double.parseDouble(orderValue));
                            dashBoardBeanOutsVal.setActive(String.valueOf(BigDecimal.valueOf(bigDecimal)));
                        }
                    }
                    else if (((String) property.getValue()).equalsIgnoreCase("Retailers Stock Value")) {
                        dashBoardBeanStockVal.setApplication("Retailers Stock Value");
                        property = properties.get(Constants.OrderValue);
                        if (property != null){
                            String orderValue = property.getValue().toString();
                            double bigDecimal = (Double.parseDouble(orderValue));
                            dashBoardBeanStockVal.setActive(String.valueOf(BigDecimal.valueOf(bigDecimal)));
                        }
                    }*/
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        alDashBoardBean.add(dashBoardBeanSecSale);
        alDashBoardBean.add(dashBoardBeanSecInv);
 /*  alDashBoardBean.add(dashBoardBeanOutsVal);
        alDashBoardBean.add(dashBoardBeanStockVal);*/
        if(dashBoardBeanRTGS.getApplication().equalsIgnoreCase("RTGS Value ( Total Balance )")) {
            alDashBoardBean.add(dashBoardBeanRTGS);
        }
        if(dashBoardBeanRTGSFocus.getApplication().equalsIgnoreCase("RTGS Value ( Focus Brand )")) {
            alDashBoardBean.add(dashBoardBeanRTGSFocus);
        }

        if(dashBoardBeanRTGS.getApplication().equalsIgnoreCase("") && dashBoardBeanRTGSFocus.getApplication().equalsIgnoreCase("")){
            DashBoardBean dashBoardBeanRTGSEmpty = new DashBoardBean();
            dashBoardBeanRTGSEmpty.setApplication("RTGS Value");
            dashBoardBeanRTGSEmpty.setActive("0.0");
            dashBoardBeanRTGSEmpty.setTotal("0.0");
            alDashBoardBean.add(dashBoardBeanRTGSEmpty);
        }
        return alDashBoardBean;
    }

    public static ArrayList<DashBoardBean> getTransactionCountDBs(JSONArray jsonArray) {
        ArrayList<DashBoardBean> alDashBoardBean = new ArrayList<>();
        ArrayList<DashBoardBean> alDashBoardBeanTemp = new ArrayList<>();
        DashBoardBean dashBoardBeanRTGS = new DashBoardBean();
//        dashBoardBeanRTGS.setApplication("RTGS Value( S & D )");
//        dashBoardBeanRTGS.setActive("0.0");
//        dashBoardBeanRTGS.setTotal("0.0");
        DashBoardBean dashBoardBeanRTGSFocus = new DashBoardBean();
//        dashBoardBeanRTGSFocus.setApplication("RTGS Value( Focus Brand )");
//        dashBoardBeanRTGSFocus.setActive("0.0");
//        dashBoardBeanRTGSFocus.setTotal("0.0");
        DashBoardBean dashBoardBeanSecSale = new DashBoardBean();
        dashBoardBeanSecSale.setApplication("Sec Order");
        dashBoardBeanSecSale.setActive("0");
        dashBoardBeanSecSale.setTotal("0");
        DashBoardBean dashBoardBeanSecInv = new DashBoardBean();
        dashBoardBeanSecInv.setApplication("Sec Invoice");
        dashBoardBeanSecInv.setActive("0");
        dashBoardBeanSecInv.setTotal("0");
       /* DashBoardBean dashBoardBeanOutsVal = new DashBoardBean();
        dashBoardBeanOutsVal.setApplication("Retailer Outstanding");
        dashBoardBeanOutsVal.setActive("0");
      //  dashBoardBeanOutsVal.setTotal("0");
        DashBoardBean dashBoardBeanStockVal = new DashBoardBean();
        dashBoardBeanStockVal.setApplication("Retailers Stock Value");
        dashBoardBeanStockVal.setActive("0");*/
        //  dashBoardBeanStockVal.setTotal("0");

        if (jsonArray != null) {

            try {
                //Define the resource path
                for (int i=0;i<jsonArray.length();i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if ((jsonObject.optString(Constants.Application1)).equalsIgnoreCase("RTGS")) {
                        String DmsDivision = jsonObject.optString(Constants.DmsDivision);
                        if(DmsDivision.equalsIgnoreCase("01")) {
                            dashBoardBeanRTGS.setApplication("RTGS Value ( Total Balance )");
                            if (jsonObject.optString(Constants.ActaulValue) != null) {
                                if (!dashBoardBeanRTGS.getActive().isEmpty()) {
                                    String actual = jsonObject.optString(Constants.ActaulValue);
                                    double bigDecimal = (Double.parseDouble(dashBoardBeanRTGS.getActive())) + Double.parseDouble(actual);
                                    dashBoardBeanRTGS.setActive(String.valueOf(BigDecimal.valueOf(bigDecimal)));
                                } else {
                                    dashBoardBeanRTGS.setActive(jsonObject.optString(Constants.ActaulValue));
                                }
                            }
                            if (jsonObject.optString(Constants.OrderValue) != null && !(jsonObject.optString(Constants.OrderValue)).isEmpty()) {
                                if (!dashBoardBeanRTGS.getTotal().isEmpty()) {
                                    String actual = jsonObject.optString(Constants.OrderValue);
                                    double bigDecimal = (Double.parseDouble(dashBoardBeanRTGS.getTotal())) + Double.parseDouble(actual);
                                    dashBoardBeanRTGS.setTotal(String.valueOf(BigDecimal.valueOf(bigDecimal)));
                                } else {
                                    dashBoardBeanRTGS.setTotal(jsonObject.optString(Constants.OrderValue));
                                }
                            }
                        }else if(DmsDivision.equalsIgnoreCase("10")){
                            dashBoardBeanRTGSFocus.setApplication("RTGS Value ( Focus Brand )");
                            if (jsonObject.optString(Constants.ActaulValue) != null) {
                                if (!dashBoardBeanRTGSFocus.getActive().isEmpty()) {
                                    String actual = jsonObject.optString(Constants.ActaulValue);
                                    double bigDecimal = (Double.parseDouble(dashBoardBeanRTGSFocus.getActive())) + Double.parseDouble(actual);
                                    dashBoardBeanRTGSFocus.setActive(String.valueOf(BigDecimal.valueOf(bigDecimal)));
                                } else {
                                    dashBoardBeanRTGSFocus.setActive(jsonObject.optString(Constants.ActaulValue));
                                }
                            }
                            if (jsonObject.optString(Constants.OrderValue) != null && !(jsonObject.optString(Constants.OrderValue)).isEmpty()) {
                                if (!dashBoardBeanRTGSFocus.getTotal().isEmpty()) {
                                    String actual = jsonObject.optString(Constants.OrderValue);
                                    double bigDecimal = (Double.parseDouble(dashBoardBeanRTGSFocus.getTotal())) + Double.parseDouble(actual);
                                    dashBoardBeanRTGSFocus.setTotal(String.valueOf(BigDecimal.valueOf(bigDecimal)));
                                } else {
                                    dashBoardBeanRTGSFocus.setTotal(jsonObject.optString(Constants.OrderValue));
                                }
                            }
                        }
                    }else if ((jsonObject.optString(Constants.Application1)).equalsIgnoreCase("Secondary Sales Order")) {
                        dashBoardBeanSecSale.setApplication("Sec Order");
                        if (jsonObject.optString(Constants.Open) != null && !(jsonObject.optString(Constants.Open)).isEmpty()){
                            if (!dashBoardBeanSecSale.getActive().isEmpty()) {
                                dashBoardBeanSecSale.setActive(String.valueOf(Integer.parseInt(dashBoardBeanSecSale.getActive()) + Integer.parseInt(jsonObject.optString(Constants.Open))));
                            }else {
                                dashBoardBeanSecSale.setActive(jsonObject.optString(Constants.Open));
                            }
                        }
                        if (jsonObject.optString(Constants.Total) != null && !((jsonObject.optString(Constants.Total)).isEmpty())){
                            if (!dashBoardBeanSecSale.getTotal().isEmpty()) {
                                dashBoardBeanSecSale.setTotal(String.valueOf(Integer.parseInt(dashBoardBeanSecSale.getTotal()) + Integer.parseInt(jsonObject.optString(Constants.Total))));
                            }else {
                                dashBoardBeanSecSale.setTotal(jsonObject.optString(Constants.Total));
                            }
                        }
                    }else if ((jsonObject.optString(Constants.Application1)).equalsIgnoreCase("Secondary Invoice")) {
                        dashBoardBeanSecInv.setApplication("Sec Invoice");
                        if (jsonObject.optString(Constants.Open) != null && !((jsonObject.optString(Constants.Open) ).isEmpty())){
                            if (!dashBoardBeanSecInv.getActive().isEmpty()) {
                                dashBoardBeanSecInv.setActive(String.valueOf(Integer.parseInt(dashBoardBeanSecInv.getActive()) + Integer.parseInt(jsonObject.optString(Constants.Open) )));
                            }else {
                                dashBoardBeanSecInv.setActive(jsonObject.optString(Constants.Open) );
                            }
                        }
                        if (jsonObject.optString(Constants.Total)  != null && !((jsonObject.optString(Constants.Total)).isEmpty())){
                            if (!dashBoardBeanSecInv.getTotal().isEmpty()) {
                                dashBoardBeanSecInv.setTotal(String.valueOf(Integer.parseInt(dashBoardBeanSecInv.getTotal()) + Integer.parseInt(jsonObject.optString(Constants.Total))));
                            }else {
                                dashBoardBeanSecInv.setTotal(jsonObject.optString(Constants.Total));
                            }
                        }
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        alDashBoardBean.add(dashBoardBeanSecSale);
        alDashBoardBean.add(dashBoardBeanSecInv);
 /*  alDashBoardBean.add(dashBoardBeanOutsVal);
        alDashBoardBean.add(dashBoardBeanStockVal);*/
        if(dashBoardBeanRTGS.getApplication().equalsIgnoreCase("RTGS Value ( Total Balance )")) {
            alDashBoardBean.add(dashBoardBeanRTGS);
        }
        if(dashBoardBeanRTGSFocus.getApplication().equalsIgnoreCase("RTGS Value ( Focus Brand )")) {
            alDashBoardBean.add(dashBoardBeanRTGSFocus);
        }

        if(dashBoardBeanRTGS.getApplication().equalsIgnoreCase("") && dashBoardBeanRTGSFocus.getApplication().equalsIgnoreCase("")){
            DashBoardBean dashBoardBeanRTGSEmpty = new DashBoardBean();
            dashBoardBeanRTGSEmpty.setApplication("RTGS Value");
            dashBoardBeanRTGSEmpty.setActive("0.0");
            dashBoardBeanRTGSEmpty.setTotal("0.0");
            alDashBoardBean.add(dashBoardBeanRTGSEmpty);
        }
        return alDashBoardBean;
    }

    public static ArrayList<DashBoardBean> getMasterCountDB(List<ODataEntity> entities) {
        ArrayList<DashBoardBean> alDashBoardBean = new ArrayList<>();
        ArrayList<String> alMasterCountString = new ArrayList<>();
        DashBoardBean dashBoardBean = null;
        if (entities != null) {
            ODataProperty property;
            ODataPropMap properties;
            try {
                //Define the resource path
                for (ODataEntity dataEntity : entities) {
                    dashBoardBean = new DashBoardBean();
                    properties = dataEntity.getProperties();
                    property = properties.get(Constants.Application1);
                    try {
                        alMasterCountString.add((String) property.getValue());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(((String) property.getValue()).equalsIgnoreCase("Distributor")) {
                        dashBoardBean.setApplication("Distributors");
                    }else if(((String) property.getValue()).equalsIgnoreCase("DSR")) {
                        dashBoardBean.setApplication("UNITS(DSR)");
                    }else if(((String) property.getValue()).equalsIgnoreCase("Beat")) {
                        dashBoardBean.setApplication("Beats");
                    }else if(((String) property.getValue()).equalsIgnoreCase("Retailer")) {
                        dashBoardBean.setApplication("Retailers");
                    }else if(((String) property.getValue()).equalsIgnoreCase("SalesPerson")) {
                        dashBoardBean.setApplication("Team Count");
                    }

                    property = properties.get(Constants.Active);
                    dashBoardBean.setActive((String) property.getValue());

                    property = properties.get(Constants.Total);
                    dashBoardBean.setTotal((String) property.getValue());
                    alDashBoardBean.add(dashBoardBean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(!alMasterCountString.contains("Distributor")){
            dashBoardBean = new DashBoardBean();
            dashBoardBean.setApplication("Distributors");
            dashBoardBean.setActive("0");
            dashBoardBean.setTotal("0");
            alDashBoardBean.add(dashBoardBean);
        }
        if(!alMasterCountString.contains("DSR")){
            dashBoardBean = new DashBoardBean();
            dashBoardBean.setApplication("UNITS(DSR)");
            dashBoardBean.setActive("0");
            dashBoardBean.setTotal("0");
            alDashBoardBean.add(dashBoardBean);
        }
        if(!alMasterCountString.contains("Beat")){
            dashBoardBean = new DashBoardBean();
            dashBoardBean.setApplication("Beats");
            dashBoardBean.setActive("0");
            dashBoardBean.setTotal("0");
            alDashBoardBean.add(dashBoardBean);
        }
        if(!alMasterCountString.contains("Retailer")){
            dashBoardBean = new DashBoardBean();
            dashBoardBean.setApplication("Retailers");
            dashBoardBean.setActive("0");
            dashBoardBean.setTotal("0");
            alDashBoardBean.add(dashBoardBean);
        }
        if(!alMasterCountString.contains("SalesPerson")){
            dashBoardBean = new DashBoardBean();
            dashBoardBean.setApplication("Team Count");
            dashBoardBean.setActive("");
            dashBoardBean.setTotal("0");
            alDashBoardBean.add(dashBoardBean);
        }
        return alDashBoardBean;
    }

    public static ArrayList<DashBoardBean> getMasterCountDB(JSONArray jsonArray) {
        ArrayList<DashBoardBean> alDashBoardBean = new ArrayList<>();
        ArrayList<String> alMasterCountString = new ArrayList<>();
        DashBoardBean dashBoardBean = null;
        if (jsonArray != null) {
            try {
                //Define the resource path
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    dashBoardBean = new DashBoardBean();
                    try {
                        alMasterCountString.add(jsonObject.optString(Constants.Application1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if((jsonObject.optString(Constants.Application1)).equalsIgnoreCase("Distributor")) {
                        dashBoardBean.setApplication("Distributors");
                    }else if((jsonObject.optString(Constants.Application1)).equalsIgnoreCase("DSR")) {
                        dashBoardBean.setApplication("UNITS(DSR)");
                    }else if((jsonObject.optString(Constants.Application1)).equalsIgnoreCase("Beat")) {
                        dashBoardBean.setApplication("Beats");
                    }else if((jsonObject.optString(Constants.Application1)).equalsIgnoreCase("Retailer")) {
                        dashBoardBean.setApplication("Retailers");
                    }else if((jsonObject.optString(Constants.Application1)).equalsIgnoreCase("SalesPerson")) {
                        dashBoardBean.setApplication("Team Count");
                    }

                    dashBoardBean.setActive(jsonObject.optString(Constants.Active));

                    dashBoardBean.setTotal(jsonObject.optString(Constants.Total));
                    alDashBoardBean.add(dashBoardBean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(!alMasterCountString.contains("Distributor")){
            dashBoardBean = new DashBoardBean();
            dashBoardBean.setApplication("Distributors");
            dashBoardBean.setActive("0");
            dashBoardBean.setTotal("0");
            alDashBoardBean.add(dashBoardBean);
        }
        if(!alMasterCountString.contains("DSR")){
            dashBoardBean = new DashBoardBean();
            dashBoardBean.setApplication("UNITS(DSR)");
            dashBoardBean.setActive("0");
            dashBoardBean.setTotal("0");
            alDashBoardBean.add(dashBoardBean);
        }
        if(!alMasterCountString.contains("Beat")){
            dashBoardBean = new DashBoardBean();
            dashBoardBean.setApplication("Beats");
            dashBoardBean.setActive("0");
            dashBoardBean.setTotal("0");
            alDashBoardBean.add(dashBoardBean);
        }
        if(!alMasterCountString.contains("Retailer")){
            dashBoardBean = new DashBoardBean();
            dashBoardBean.setApplication("Retailers");
            dashBoardBean.setActive("0");
            dashBoardBean.setTotal("0");
            alDashBoardBean.add(dashBoardBean);
        }
        if(!alMasterCountString.contains("SalesPerson")){
            dashBoardBean = new DashBoardBean();
            dashBoardBean.setApplication("Team Count");
            dashBoardBean.setActive("");
            dashBoardBean.setTotal("0");
            alDashBoardBean.add(dashBoardBean);
        }
        return alDashBoardBean;
    }

    public static ArrayList<AttendanceSummaryBean> getAttendanceSummaryList( List<ODataEntity> entities) {
        AttendanceSummaryBean summaryBean;
        ArrayList<AttendanceSummaryBean> attendanceSummaryBeanArrayList = new ArrayList<>();
        ODataProperty property;
        ODataPropMap properties;
        if (entities != null) {
            for (ODataEntity entity : entities) {
                summaryBean = new AttendanceSummaryBean();
                properties = entity.getProperties();
                try {
                    property = properties.get(Constants.SPName);
                    summaryBean.setSPName(property.getValue().toString());
                    property = properties.get(Constants.CreatedBy);
                    summaryBean.setCreatedBy(property.getValue().toString());
                    String convertStartDate = "";
                    property = properties.get(Constants.StartDate);
                    try {
                        if(property!=null) {
                            convertStartDate = UtilConstants.convertCalenderToStringFormat((GregorianCalendar) property.getValue());
                            summaryBean.setStartDate(convertStartDate);
                        }else {
                            summaryBean.setStartDate("");
                        }
                    } catch (Exception e) {
                        summaryBean.setStartDate("");
                        e.printStackTrace();
                    }
                    String convertEndDate = "";
                    property = properties.get(Constants.EndDate);
                    try {
                        if(property!=null) {
                            convertEndDate = UtilConstants.convertCalenderToStringFormat((GregorianCalendar) property.getValue());
                            summaryBean.setEndDate(convertEndDate);
                        }else {
                            summaryBean.setEndDate("");
                        }
                    } catch (Exception e) {
                        summaryBean.setEndDate("");
                        e.printStackTrace();
                    }

                    property = properties.get(Constants.EndTime);
                    try {
                        if(property!=null) {
                            if(!TextUtils.isEmpty(summaryBean.getEndDate())) {
                                summaryBean.setEndTime(Constants.convertTimeFormat(property.getValue().toString()));
                            }else {
                                summaryBean.setEndTime("");
                            }
                        }else {
                            summaryBean.setEndTime("");
                        }
                    } catch (Exception e) {
                        summaryBean.setEndTime("");
                        e.printStackTrace();
                    }

                    property = properties.get(Constants.StartTime);
                    try {
                        if(property!=null) {
                            if(!TextUtils.isEmpty(summaryBean.getStartDate())) {
                                summaryBean.setStartTime(Constants.convertTimeFormat(property.getValue().toString()));
                            }else {
                                summaryBean.setStartTime("");
                            }
                        }else {
                            summaryBean.setStartTime("");
                        }
                    } catch (Exception e) {
                        summaryBean.setStartTime("");
                        e.printStackTrace();
                    }

                    try {
                        if(!TextUtils.isEmpty(summaryBean.getStartDate()) && !TextUtils.isEmpty(summaryBean.getStartTime())){
                            summaryBean.setStartDateTime(summaryBean.getStartDate() + " " + summaryBean.getStartTime());
                        }else {
                            summaryBean.setStartDateTime("");
                        }
                    } catch (Exception e) {
                        summaryBean.setStartDateTime("");
                        e.printStackTrace();
                    }

                    try {
                        if(!TextUtils.isEmpty(summaryBean.getEndDate()) && !TextUtils.isEmpty(summaryBean.getEndTime())){
                            summaryBean.setEndDateTime(summaryBean.getEndDate() + " " + summaryBean.getEndTime());
                        }else {
                            summaryBean.setEndDateTime("");
                        }
                    } catch (Exception e) {
                        summaryBean.setEndDateTime("");
                        e.printStackTrace();
                    }

                    try {
                        summaryBean.setTimeDiff(Constants.getTimeDiff(summaryBean.getStartDateTime(),summaryBean.getEndDateTime()));
                    } catch (Exception e) {
                        summaryBean.setTimeDiff("00:00");
                        e.printStackTrace();
                    }

                    try {
                        summaryBean.setTotalWorkingHour(Constants.getTotalWorkingHour(summaryBean.getStartDateTime(),summaryBean.getEndDateTime()));
                    } catch (Exception e) {
                        summaryBean.setTotalWorkingHour("0");
                        e.printStackTrace();
                    }
                    attendanceSummaryBeanArrayList.add(summaryBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return attendanceSummaryBeanArrayList;
    }

    public static ArrayList<AttendanceSummaryBean> getAttendanceSummaryList(JSONArray jsonArray) {
        AttendanceSummaryBean summaryBean;
        ArrayList<AttendanceSummaryBean> attendanceSummaryBeanArrayList = new ArrayList<>();
        if (jsonArray != null) {
            try {
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    summaryBean = new AttendanceSummaryBean();
                    try {
                        summaryBean.setSPName(jsonObject.optString(Constants.SPName));
                        summaryBean.setCreatedBy(jsonObject.optString(Constants.CreatedBy));
                        String convertStartDate = "";
                        try {
                            if (jsonObject.optString(Constants.StartDate) != null) {
                                convertStartDate = ConstantsUtils.getJSONDate(jsonObject.optString(Constants.StartDate));
                                summaryBean.setStartDate(convertStartDate);
                            } else {
                                summaryBean.setStartDate("");
                            }
                        } catch (Exception e) {
                            summaryBean.setStartDate("");
                            e.printStackTrace();
                        }
                        String convertEndDate = "";
                        try {
                            if (jsonObject.optString(Constants.EndDate) != null) {
                                convertEndDate = ConstantsUtils.getJSONDate(jsonObject.optString(Constants.EndDate));
                                summaryBean.setEndDate(convertEndDate);
                            } else {
                                summaryBean.setEndDate("");
                            }
                        } catch (Exception e) {
                            summaryBean.setEndDate("");
                            e.printStackTrace();
                        }

                        try {
                            if (jsonObject.optString(Constants.EndTime) != null) {
                                if (!TextUtils.isEmpty(summaryBean.getEndDate())) {
                                    summaryBean.setEndTime(Constants.convertTimeFormat(jsonObject.optString(Constants.EndTime)));
                                } else {
                                    summaryBean.setEndTime("");
                                }
                            } else {
                                summaryBean.setEndTime("");
                            }
                        } catch (Exception e) {
                            summaryBean.setEndTime("");
                            e.printStackTrace();
                        }

                        try {
                            if (jsonObject.optString(Constants.StartTime) != null) {
                                if (!TextUtils.isEmpty(summaryBean.getStartDate())) {
                                    summaryBean.setStartTime(Constants.convertTimeFormat(jsonObject.optString(Constants.StartTime)));
                                } else {
                                    summaryBean.setStartTime("");
                                }
                            } else {
                                summaryBean.setStartTime("");
                            }
                        } catch (Exception e) {
                            summaryBean.setStartTime("");
                            e.printStackTrace();
                        }

                        try {
                            if (!TextUtils.isEmpty(summaryBean.getStartDate()) && !TextUtils.isEmpty(summaryBean.getStartTime())) {
                                summaryBean.setStartDateTime(summaryBean.getStartDate() + " " + summaryBean.getStartTime());
                            } else {
                                summaryBean.setStartDateTime("");
                            }
                        } catch (Exception e) {
                            summaryBean.setStartDateTime("");
                            e.printStackTrace();
                        }

                        try {
                            if (!TextUtils.isEmpty(summaryBean.getEndDate()) && !TextUtils.isEmpty(summaryBean.getEndTime())) {
                                summaryBean.setEndDateTime(summaryBean.getEndDate() + " " + summaryBean.getEndTime());
                            } else {
                                summaryBean.setEndDateTime("");
                            }
                        } catch (Exception e) {
                            summaryBean.setEndDateTime("");
                            e.printStackTrace();
                        }

                        try {
                            summaryBean.setTimeDiff(Constants.getTimeDiff(summaryBean.getStartDateTime(), summaryBean.getEndDateTime()));
                        } catch (Exception e) {
                            summaryBean.setTimeDiff("00:00");
                            e.printStackTrace();
                        }

                        try {
                            summaryBean.setTotalWorkingHour(Constants.getTotalWorkingHour(summaryBean.getStartDateTime(), summaryBean.getEndDateTime()));
                        } catch (Exception e) {
                            summaryBean.setTotalWorkingHour("0");
                            e.printStackTrace();
                        }
                        attendanceSummaryBeanArrayList.add(summaryBean);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return attendanceSummaryBeanArrayList;
    }


    public static ArrayList<ClaimReportBean> getClaimSumaryList(List<ODataEntity> entities) {
        ClaimReportBean summaryBean;
        HashMap<String, ClaimReportBean> distMap = new HashMap<>();
        ArrayList<ClaimReportBean> attendanceSummaryBeanArrayList = new ArrayList<>();
        ODataProperty property;
        ODataPropMap properties;
        if (entities != null) {
            for (ODataEntity entity : entities) {
                summaryBean = new ClaimReportBean();
                properties = entity.getProperties();
                try {
                    property = properties.get(Constants.ParentNo);
                    summaryBean.setParentNo(property.getValue().toString());


                    if (distMap.containsKey(summaryBean.getParentNo())) {
                        ClaimReportBean summaryBeanTemp = distMap.get(summaryBean.getParentNo());
                        double dTotalClaim=0.0;
                        if(!TextUtils.isEmpty(summaryBeanTemp.getTotalClaimAmount())){
                            dTotalClaim = dTotalClaim + Double.parseDouble(summaryBeanTemp.getTotalClaimAmount());
                        }else {
                            dTotalClaim = dTotalClaim + 0.0;
                        }

                        double dTotalMaxClaim=0.0;
                        if(!TextUtils.isEmpty(summaryBeanTemp.getTotalMaxClaimAmt())){
                            dTotalMaxClaim = dTotalMaxClaim + Double.parseDouble(summaryBeanTemp.getTotalMaxClaimAmt());
                        }else {
                            dTotalMaxClaim = dTotalMaxClaim + 0.0;
                        }
                        ArrayList<ClaimReportBean> detailsList = summaryBeanTemp.getClaimReportBeans();


                        property = properties.get(Constants.ZSchemeTypeDesc);
                        summaryBean.setZSchemeTypeDesc((String) property.getValue());
                        property = properties.get(Constants.ZSchemeType);
                        summaryBean.setZSchemeType((String) property.getValue());
                        property = properties.get(Constants.ParentName);
                        summaryBean.setParentName((String) property.getValue());

                        property = properties.get(Constants.ZSchemeValidTo);
                        String convertSchemeValidTo="";
                        try {
                            if(property!=null) {
                                convertSchemeValidTo = UtilConstants.convertCalenderToStringFormat((GregorianCalendar) property.getValue());
                                summaryBean.setZSchemeValidTo(convertSchemeValidTo);
                            }else {
                                summaryBean.setZSchemeValidTo("");
                            }
                        } catch (Exception e) {
                            summaryBean.setZSchemeValidTo("");
                            e.printStackTrace();
                        }

                        property = properties.get(Constants.ZSchemeValidFrm);
                        String convertSchemeValidFrom="";
                        try {
                            if(property!=null) {
                                convertSchemeValidFrom = UtilConstants.convertCalenderToStringFormat((GregorianCalendar) property.getValue());
                                summaryBean.setZSchemeValidFrm(convertSchemeValidFrom);
                            }else {
                                summaryBean.setZSchemeValidFrm("");
                            }
                        } catch (Exception e) {
                            summaryBean.setZSchemeValidFrm("");
                            e.printStackTrace();
                        }

                        property = properties.get(Constants.ClaimAmount);
                        BigDecimal dClaimAmount=null;
                        try {
                            if(property!=null) {
                                dClaimAmount = (BigDecimal) property.getValue();
                                if(dClaimAmount!=null) {
                                    summaryBean.setClaimAmount(""+dClaimAmount.doubleValue());
                                }else {
                                    summaryBean.setClaimAmount("0.00");
                                }
                            }else {
                                summaryBean.setClaimAmount("0.00");
                            }
                        } catch (Exception e) {
                            summaryBean.setClaimAmount("0.00");
                            e.printStackTrace();
                        }

                        property = properties.get(Constants.ZMaxClaimAmt);
                        BigDecimal dMaxClaimAmt=null;
                        try {
                            if(property!=null) {
                                dMaxClaimAmt = (BigDecimal) property.getValue();
                                if(dMaxClaimAmt!=null) {
                                    summaryBean.setZMaxClaimAmt(""+dMaxClaimAmt.doubleValue());
                                }else {
                                    summaryBean.setZMaxClaimAmt("0.00");
                                }
                            }else {
                                summaryBean.setZMaxClaimAmt("0.00");
                            }
                        } catch (Exception e) {
                            summaryBean.setZMaxClaimAmt("0.00");
                            e.printStackTrace();
                        }
                        if(!TextUtils.isEmpty(summaryBean.getClaimAmount())){
                            dTotalClaim = dTotalClaim + Double.parseDouble(summaryBean.getClaimAmount());
                        }else {
                            dTotalClaim = dTotalClaim + 0.0;
                        }
                        detailsList.add(summaryBean);
                        summaryBean.setTotalClaimAmount(""+dTotalClaim);
                        if(!TextUtils.isEmpty(summaryBean.getZMaxClaimAmt())){
                            dTotalMaxClaim = dTotalMaxClaim + Double.parseDouble(summaryBean.getZMaxClaimAmt());
                        }else {
                            dTotalMaxClaim = dTotalMaxClaim + 0.0;
                        }
                        summaryBean.setTotalMaxClaimAmt(""+dTotalMaxClaim);
                        summaryBean.setClaimReportBeans(detailsList);

                        distMap.put(summaryBean.getParentNo(), summaryBean);
                    } else {
                        property = properties.get(Constants.ZSchemeTypeDesc);
                        summaryBean.setZSchemeTypeDesc((String) property.getValue());
                        property = properties.get(Constants.ZSchemeType);
                        summaryBean.setZSchemeType((String) property.getValue());
                        property = properties.get(Constants.ParentName);
                        summaryBean.setParentName((String) property.getValue());

                        property = properties.get(Constants.ZSchemeValidTo);
                        String convertSchemeValidTo="";
                        try {
                            if(property!=null) {
                                convertSchemeValidTo = UtilConstants.convertCalenderToStringFormat((GregorianCalendar) property.getValue());
                                summaryBean.setZSchemeValidTo(convertSchemeValidTo);
                            }else {
                                summaryBean.setZSchemeValidTo("");
                            }
                        } catch (Exception e) {
                            summaryBean.setZSchemeValidTo("");
                            e.printStackTrace();
                        }

                        property = properties.get(Constants.ZSchemeValidFrm);
                        String convertSchemeValidFrom="";
                        try {
                            if(property!=null) {
                                convertSchemeValidFrom = UtilConstants.convertCalenderToStringFormat((GregorianCalendar) property.getValue());
                                summaryBean.setZSchemeValidFrm(convertSchemeValidFrom);
                            }else {
                                summaryBean.setZSchemeValidFrm("");
                            }
                        } catch (Exception e) {
                            summaryBean.setZSchemeValidFrm("");
                            e.printStackTrace();
                        }

                        property = properties.get(Constants.ClaimAmount);
                        BigDecimal dClaimAmount=null;
                        try {
                            if(property!=null) {
                                dClaimAmount = (BigDecimal) property.getValue();
                                if(dClaimAmount!=null) {
                                    summaryBean.setClaimAmount(""+dClaimAmount.doubleValue());
                                }else {
                                    summaryBean.setClaimAmount("0.00");
                                }
                            }else {
                                summaryBean.setClaimAmount("0.00");
                            }
                        } catch (Exception e) {
                            summaryBean.setClaimAmount("0.00");
                            e.printStackTrace();
                        }

                        property = properties.get(Constants.ZMaxClaimAmt);
                        BigDecimal dMaxClaimAmt=null;
                        try {
                            if(property!=null) {
                                dMaxClaimAmt = (BigDecimal) property.getValue();
                                if(dMaxClaimAmt!=null) {
                                    summaryBean.setZMaxClaimAmt(""+dMaxClaimAmt.doubleValue());
                                }else {
                                    summaryBean.setZMaxClaimAmt("0.00");
                                }
                            }else {
                                summaryBean.setZMaxClaimAmt("0.00");
                            }
                        } catch (Exception e) {
                            summaryBean.setZMaxClaimAmt("0.00");
                            e.printStackTrace();
                        }
                        double dTotalClaim=0.0;
                        if(!TextUtils.isEmpty(summaryBean.getClaimAmount())){
                            dTotalClaim = dTotalClaim + Double.parseDouble(summaryBean.getClaimAmount());
                        }else {
                            dTotalClaim = dTotalClaim + 0.0;
                        }
                        ArrayList<ClaimReportBean> detailsList = new ArrayList<>();
                        detailsList.add(summaryBean);
                        summaryBean.setTotalClaimAmount(""+dTotalClaim);
                        double dTotalMaxClaim=0.0;
                        if(!TextUtils.isEmpty(summaryBean.getZMaxClaimAmt())){
                            dTotalMaxClaim = dTotalMaxClaim + Double.parseDouble(summaryBean.getZMaxClaimAmt());
                        }else {
                            dTotalMaxClaim = dTotalMaxClaim + 0.0;
                        }
                        summaryBean.setTotalMaxClaimAmt(""+dTotalMaxClaim);
                        summaryBean.setClaimReportBeans(detailsList);
                        distMap.put(summaryBean.getParentNo(), summaryBean);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        Set keys = distMap.keySet();
        Iterator itr = keys.iterator();

        String key;
        ClaimReportBean value;
        while (itr.hasNext()) {
            key = (String) itr.next();
            value = (ClaimReportBean) distMap.get(key);
            attendanceSummaryBeanArrayList.add(value);
        }
        return attendanceSummaryBeanArrayList;
    }

    public static ArrayList<ClaimReportBean> getClaimSumaryList(JSONArray jsonArray) {
        ClaimReportBean summaryBean;
        HashMap<String, ClaimReportBean> distMap = new HashMap<>();
        ArrayList<ClaimReportBean> attendanceSummaryBeanArrayList = new ArrayList<>();
        try {
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    summaryBean = new ClaimReportBean();
                    try {
                        summaryBean.setParentNo(jsonObject.optString(Constants.ParentNo));


                        if (distMap.containsKey(summaryBean.getParentNo())) {
                            ClaimReportBean summaryBeanTemp = distMap.get(summaryBean.getParentNo());
                            double dTotalClaim = 0.0;
                            if (!TextUtils.isEmpty(summaryBeanTemp.getTotalClaimAmount())) {
                                dTotalClaim = dTotalClaim + Double.parseDouble(summaryBeanTemp.getTotalClaimAmount());
                            } else {
                                dTotalClaim = dTotalClaim + 0.0;
                            }

                            double dTotalMaxClaim = 0.0;
                            if (!TextUtils.isEmpty(summaryBeanTemp.getTotalMaxClaimAmt())) {
                                dTotalMaxClaim = dTotalMaxClaim + Double.parseDouble(summaryBeanTemp.getTotalMaxClaimAmt());
                            } else {
                                dTotalMaxClaim = dTotalMaxClaim + 0.0;
                            }
                            ArrayList<ClaimReportBean> detailsList = summaryBeanTemp.getClaimReportBeans();


                            summaryBean.setZSchemeTypeDesc((jsonObject.optString(Constants.ZSchemeTypeDesc)));
                            summaryBean.setZSchemeType(jsonObject.optString(Constants.ZSchemeType));
                            summaryBean.setParentName(jsonObject.optString(Constants.ParentName));

                            String convertSchemeValidTo = "";
                            try {
                                if (jsonObject.optString(Constants.ZSchemeValidTo) != null) {
                                    convertSchemeValidTo = ConstantsUtils.getJSONDate(jsonObject.optString(Constants.ZSchemeValidTo));
                                    summaryBean.setZSchemeValidTo(convertSchemeValidTo);
                                } else {
                                    summaryBean.setZSchemeValidTo("");
                                }
                            } catch (Exception e) {
                                summaryBean.setZSchemeValidTo("");
                                e.printStackTrace();
                            }

                            String convertSchemeValidFrom = "";
                            try {
                                if (jsonObject.optString(Constants.ZSchemeValidFrm) != null) {
                                    convertSchemeValidFrom = ConstantsUtils.getJSONDate(jsonObject.optString(Constants.ZSchemeValidFrm));
                                    summaryBean.setZSchemeValidFrm(convertSchemeValidFrom);
                                } else {
                                    summaryBean.setZSchemeValidFrm("");
                                }
                            } catch (Exception e) {
                                summaryBean.setZSchemeValidFrm("");
                                e.printStackTrace();
                            }

                            BigDecimal dClaimAmount = null;
                            try {
                                if (jsonObject.optString(Constants.ClaimAmount) != null) {
                                    String claimAmount ="0.00";
                                    if(!TextUtils.isEmpty(jsonObject.optString(Constants.ClaimAmount))){
                                        claimAmount=jsonObject.optString(Constants.ClaimAmount);
                                    }
                                    try {
                                        dClaimAmount = new BigDecimal(claimAmount);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (dClaimAmount != null) {
                                        summaryBean.setClaimAmount("" + dClaimAmount.doubleValue());
                                    } else {
                                        summaryBean.setClaimAmount("0.00");
                                    }
                                } else {
                                    summaryBean.setClaimAmount("0.00");
                                }
                            } catch (Exception e) {
                                summaryBean.setClaimAmount("0.00");
                                e.printStackTrace();
                            }

                            BigDecimal dMaxClaimAmt = null;
                            try {
                                if (jsonObject.optString(Constants.ZMaxClaimAmt) != null) {
                                    String zMaxAmount = "0.00";
                                    if(!TextUtils.isEmpty(jsonObject.optString(Constants.ZMaxClaimAmt))){
                                        zMaxAmount = jsonObject.optString(Constants.ZMaxClaimAmt);
                                    }
                                    try {
                                        dMaxClaimAmt = new BigDecimal(zMaxAmount);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (dMaxClaimAmt != null) {
                                        summaryBean.setZMaxClaimAmt("" + dMaxClaimAmt.doubleValue());
                                    } else {
                                        summaryBean.setZMaxClaimAmt("0.00");
                                    }
                                } else {
                                    summaryBean.setZMaxClaimAmt("0.00");
                                }
                            } catch (Exception e) {
                                summaryBean.setZMaxClaimAmt("0.00");
                                e.printStackTrace();
                            }
                            if (!TextUtils.isEmpty(summaryBean.getClaimAmount())) {
                                dTotalClaim = dTotalClaim + Double.parseDouble(summaryBean.getClaimAmount());
                            } else {
                                dTotalClaim = dTotalClaim + 0.0;
                            }
                            detailsList.add(summaryBean);
                            summaryBean.setTotalClaimAmount("" + dTotalClaim);
                            if (!TextUtils.isEmpty(summaryBean.getZMaxClaimAmt())) {
                                dTotalMaxClaim = dTotalMaxClaim + Double.parseDouble(summaryBean.getZMaxClaimAmt());
                            } else {
                                dTotalMaxClaim = dTotalMaxClaim + 0.0;
                            }
                            summaryBean.setTotalMaxClaimAmt("" + dTotalMaxClaim);
                            summaryBean.setClaimReportBeans(detailsList);

                            distMap.put(summaryBean.getParentNo(), summaryBean);
                        } else {
                            summaryBean.setZSchemeTypeDesc(jsonObject.optString(Constants.ZSchemeTypeDesc));
                            summaryBean.setZSchemeType(jsonObject.optString(Constants.ZSchemeType));
                            summaryBean.setParentName(jsonObject.optString(Constants.ParentName));

                            String convertSchemeValidTo = "";
                            try {
                                if (jsonObject.optString(Constants.ZSchemeValidTo) != null) {
                                    convertSchemeValidTo = ConstantsUtils.getJSONDate(jsonObject.optString(Constants.ZSchemeValidTo));
                                    summaryBean.setZSchemeValidTo(convertSchemeValidTo);
                                } else {
                                    summaryBean.setZSchemeValidTo("");
                                }
                            } catch (Exception e) {
                                summaryBean.setZSchemeValidTo("");
                                e.printStackTrace();
                            }

                            String convertSchemeValidFrom = "";
                            try {
                                if (jsonObject.optString(Constants.ZSchemeValidFrm) != null) {
                                    convertSchemeValidFrom = ConstantsUtils.getJSONDate(jsonObject.optString(Constants.ZSchemeValidFrm));
                                    summaryBean.setZSchemeValidFrm(convertSchemeValidFrom);
                                } else {
                                    summaryBean.setZSchemeValidFrm("");
                                }
                            } catch (Exception e) {
                                summaryBean.setZSchemeValidFrm("");
                                e.printStackTrace();
                            }

                            BigDecimal dClaimAmount = null;
                            try {
                                if (jsonObject.optString(Constants.ClaimAmount) != null) {
                                    String claimAmount ="0.00";
                                    if(!TextUtils.isEmpty(jsonObject.optString(Constants.ClaimAmount))){
                                        claimAmount=jsonObject.optString(Constants.ClaimAmount);
                                    }
                                    try {
                                        dClaimAmount = new BigDecimal(claimAmount);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (dClaimAmount != null) {
                                        summaryBean.setClaimAmount("" + dClaimAmount.doubleValue());
                                    } else {
                                        summaryBean.setClaimAmount("0.00");
                                    }
                                } else {
                                    summaryBean.setClaimAmount("0.00");
                                }
                            } catch (Exception e) {
                                summaryBean.setClaimAmount("0.00");
                                e.printStackTrace();
                            }
                            BigDecimal dMaxClaimAmt = null;
                            try {
                                if (jsonObject.optString(Constants.ZMaxClaimAmt) != null) {
                                    String zMaxAmount = "0.00";
                                    if(!TextUtils.isEmpty(jsonObject.optString(Constants.ZMaxClaimAmt))){
                                        zMaxAmount = jsonObject.optString(Constants.ZMaxClaimAmt);
                                    }
                                    try {
                                        dMaxClaimAmt = new BigDecimal(zMaxAmount);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (dMaxClaimAmt != null) {
                                        summaryBean.setZMaxClaimAmt("" + dMaxClaimAmt.doubleValue());
                                    } else {
                                        summaryBean.setZMaxClaimAmt("0.00");
                                    }
                                } else {
                                    summaryBean.setZMaxClaimAmt("0.00");
                                }
                            } catch (Exception e) {
                                summaryBean.setZMaxClaimAmt("0.00");
                                e.printStackTrace();
                            }
                            double dTotalClaim = 0.0;
                            if (!TextUtils.isEmpty(summaryBean.getClaimAmount())) {
                                dTotalClaim = dTotalClaim + Double.parseDouble(summaryBean.getClaimAmount());
                            } else {
                                dTotalClaim = dTotalClaim + 0.0;
                            }
                            ArrayList<ClaimReportBean> detailsList = new ArrayList<>();
                            detailsList.add(summaryBean);
                            summaryBean.setTotalClaimAmount("" + dTotalClaim);
                            double dTotalMaxClaim = 0.0;
                            if (!TextUtils.isEmpty(summaryBean.getZMaxClaimAmt())) {
                                dTotalMaxClaim = dTotalMaxClaim + Double.parseDouble(summaryBean.getZMaxClaimAmt());
                            } else {
                                dTotalMaxClaim = dTotalMaxClaim + 0.0;
                            }
                            summaryBean.setTotalMaxClaimAmt("" + dTotalMaxClaim);
                            summaryBean.setClaimReportBeans(detailsList);
                            distMap.put(summaryBean.getParentNo(), summaryBean);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Set keys = distMap.keySet();
        Iterator itr = keys.iterator();

        String key;
        ClaimReportBean value;
        while (itr.hasNext()) {
            key = (String) itr.next();
            value = (ClaimReportBean) distMap.get(key);
            attendanceSummaryBeanArrayList.add(value);
        }
        return attendanceSummaryBeanArrayList;
    }

    /*public static String doOnlineHeaderAttRequest(String resourcePath, Context mContext, IResponseListener iResponseListener, ICommunicationErrorListener iCommunicationErrorListener) {

        HttpConversationManager manager = new HttpConversationManager(mContext);

        manager.create(Uri.parse(MyUtils.getDefaultOnlineQryURL() + resourcePath))
                .setMethod(HttpMethod.GET)
                .addHeader("Authorization", MyUtils.getBasicAuthCredential(mContext))
                .addHeader("Accept", "application/json")
                .addHeader(Constants.arteria_attfilter, "SE")
                .setResponseListener(iResponseListener).setFlowListener(IConversationFlowListener
                .prepareFor()
                .communicationError(iCommunicationErrorListener).build())
                .start();
        return "";
    }*/

    public static String doOnlineGetRequest(String resourcePath, Context mContext, IResponseListener iResponseListener, ICommunicationErrorListener iCommunicationErrorListener) {

        HttpConversationManager manager = new HttpConversationManager(mContext);
        manager.create(Uri.parse(MyUtils.getDefaultOnlineQryURL() + resourcePath))
                .setMethod(HttpMethod.GET)
                .addHeader("Authorization", MyUtils.getBasicAuthCredential(mContext))
                .addHeader("Accept", "application/json")
                .addHeader("x-smp-appid", Configuration.APP_ID)
                .setResponseListener(iResponseListener).setFlowListener(IConversationFlowListener
                .prepareFor()
                .communicationError(iCommunicationErrorListener).build())
                .start();
        return "";
    }
    public static String doOnlineGetRequestDashBoard(String resourcePath, Context mContext, IResponseListener iResponseListener, ICommunicationErrorListener iCommunicationErrorListener) {

        HttpConversationManager manager = new HttpConversationManager(mContext);
        manager.create(Uri.parse(MyUtils.getDefaultOnlineQryURLDashBoard() + resourcePath))
                .setMethod(HttpMethod.GET)
                .addHeader("Authorization", MyUtils.getBasicAuthCredential(mContext))
                .addHeader("Accept", "application/json")
                .addHeader("x-smp-appid", Configuration.APP_ID)
                .setResponseListener(iResponseListener).setFlowListener(IConversationFlowListener
                .prepareFor()
                .communicationError(iCommunicationErrorListener).build())
                .start();
        return "";
    }

    public static String doOnlineHeaderAttRequest(String resourcePath, Context mContext,  IResponseListener iResponseListener, ICommunicationErrorListener iCommunicationErrorListener) {

        HttpConversationManager manager = new HttpConversationManager(mContext);

        manager.create(Uri.parse(MyUtils.getDefaultOnlineQryURL() + resourcePath))
                .setMethod(HttpMethod.GET)
                .addHeader("Authorization", MyUtils.getBasicAuthCredential(mContext))
                .addHeader("Accept", "application/json")
                .addHeader("x-smp-appid", Configuration.APP_ID)
                .addHeader(Constants.arteria_attfilter, "SE")
                .setResponseListener(iResponseListener).setFlowListener(IConversationFlowListener
                .prepareFor()
                .communicationError(iCommunicationErrorListener).build())
                .start();
        return "";
    }
    public static String doOnlineHeaderSPMTPRequest(String resourcePath, Context mContext,  IResponseListener iResponseListener, ICommunicationErrorListener iCommunicationErrorListener) {
        HttpConversationManager manager = new HttpConversationManager(mContext);

        manager.create(Uri.parse(MyUtils.getDefaultOnlineQryURL() + resourcePath))
                .setMethod(HttpMethod.GET)
                .addHeader("Authorization", MyUtils.getBasicAuthCredential(mContext))
                .addHeader("Accept", "application/json")
                .addHeader("x-smp-appid", Configuration.APP_ID)
                .addHeader(Constants.arteria_attfilter, "SE")
                .addHeader(Constants.arteria_spfilter, "X")
                .setResponseListener(iResponseListener).setFlowListener(IConversationFlowListener
                .prepareFor()
                .communicationError(iCommunicationErrorListener).build())
                .start();
        return "";
    }

    private static String CSRF_TOKEN ="";
    private static String CSRF_TOKEN_BAtch ="";
    public static void updateEntity(final String requestID,final String requestString, final String resourcePath, UIListener uiListener, Context mContext) {
        OnlineRequestListener onlineReqListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);
        SharedPreferences sharedPref = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        if (TextUtils.isEmpty(CSRF_TOKEN)){
            createCSRFToken(mContext, iReceiveEvent -> {
                if (iReceiveEvent.getResponseStatusCode() == 200) {
                    updateEntity(requestID,requestString, resourcePath, uiListener, mContext);
                }
            });
        }else {
            HttpConversationManager manager = new HttpConversationManager(mContext);
            // Create the conversation.
            manager.create(Uri.parse(MyUtils.getDefaultOnlineQryURL() + resourcePath))
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("X-SMP-APPCID", sharedPref.getString(UtilRegistrationActivity.KEY_appConnID, ""))
                    .addHeader("x-csrf-token", CSRF_TOKEN)
                    .addHeader("RequestID", requestID)
                    .addHeader("Authorization", MyUtils.getBasicAuthCredential(mContext))
                    .addHeader("Accept", "application/json")
                    .setMethod(HttpMethod.PUT)
                    .setRequestListener(event -> {
                        if (!TextUtils.isEmpty(requestString))
                            event.getWriter().write(requestString);
                        return null;
                    }).setResponseListener(event -> {
                // Process the results.
//                if (event.getReader()!=null) {
                //                    String responseBody = IReceiveEvent.Util.getResponseBody(event.getReader());
                //                    Log.d("OnlineManager", "getUserRollInfo: " + responseBody + " " + event.getResponseStatusCode());
                if (event.getResponseStatusCode() == 201 || event.getResponseStatusCode() == 204) {
                    CSRF_TOKEN="";
                    onlineReqListener.notifySuccessToListener(null);
                } else if (event.getResponseStatusCode() == 403) {
                    createCSRFToken(mContext, iReceiveEvent -> {
                        if (iReceiveEvent.getResponseStatusCode() == 200) {
                            updateEntity(requestID,requestString, resourcePath, uiListener, mContext);
                        } else {
                            onlineReqListener.notifyErrorToListener(iReceiveEvent);
                        }
                    });
                } else {
                    onlineReqListener.notifyErrorToListener(event);
                }
//                }
            }).start();
        }
    }

    public static void createEntity(final String requestID,final String requestDate,final String requestString, final String resourcePath, UIListener uiListener, Context mContext) {
        OnlineRequestListener onlineReqListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);
        SharedPreferences sharedPref = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        if (TextUtils.isEmpty(CSRF_TOKEN)){
            createCSRFToken(mContext, iReceiveEvent -> {
                if (iReceiveEvent.getResponseStatusCode() == 200) {
                    createEntity(requestID,requestDate,requestString, resourcePath, uiListener, mContext);
                }
            });
        }else {
            HttpConversationManager manager = new HttpConversationManager(mContext);
            // Create the conversation.
            manager.create(Uri.parse(MyUtils.getDefaultOnlineQryURL() + resourcePath))
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("X-SMP-APPCID", sharedPref.getString(UtilRegistrationActivity.KEY_appConnID, ""))
                    .addHeader("x-csrf-token", CSRF_TOKEN)
                    .addHeader("RequestID", requestID)
                    .addHeader(Constants.RepeatabilityCreation, requestDate)
                    .addHeader("Authorization", MyUtils.getBasicAuthCredential(mContext))
                    .addHeader("Accept", "application/json")
                    .setMethod(HttpMethod.POST)
                    .setRequestListener(event -> {
                        if (!TextUtils.isEmpty(requestString))
                            event.getWriter().write(requestString);
                        return null;
                    }).setResponseListener(event -> {
                // Process the results.
                if (event.getReader()!=null) {
                                        String responseBody = IReceiveEvent.Util.getResponseBody(event.getReader());
                    //                    Log.d("OnlineManager", "getUserRollInfo: " + responseBody + " " + event.getResponseStatusCode());
                    if (event.getResponseStatusCode() == 201) {
                        CSRF_TOKEN="";
                        onlineReqListener.notifySuccessToListener(null);
                    } else if (event.getResponseStatusCode() == 403) {
                        createCSRFToken(mContext, iReceiveEvent -> {
                            if (iReceiveEvent.getResponseStatusCode() == 200) {
                                createEntity(requestID,requestDate,requestString, resourcePath, uiListener, mContext);
                            } else {
                                if (iReceiveEvent.getReader()!=null) {
                                    String responseBodytemp = IReceiveEvent.Util.getResponseBody(iReceiveEvent.getReader());
                                    //                    Log.d("OnlineManager", "getUserRollInfo: " + responseBody + " " + event.getResponseStatusCode());
                                    onlineReqListener.notifyErrorToListenerMsg(responseBodytemp);
                                }
                            }
                        });
                    } else {
                        onlineReqListener.notifyErrorToListenerMsg(responseBody);
                    }
                }
            }).start();
        }
    }

    private static void createCSRFToken(Context mContext, IResponseListener var1) {
        HttpConversationManager manager = new HttpConversationManager(mContext);
        SharedPreferences sharedPref = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        manager.create(Uri.parse(MyUtils.getDefaultOnlineQryURL()))
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("X-SMP-APPCID", sharedPref.getString(UtilRegistrationActivity.KEY_appConnID, ""))
                .addHeader("x-csrf-token", "fetch")
                .addHeader("Authorization", MyUtils.getBasicAuthCredential(mContext))
                .addHeader("Accept", "application/json")
                .setMethod(HttpMethod.GET)
                .setResponseListener(event1 -> {
                    if (event1.getResponseStatusCode() == 200) {
                        Map<String, List<String>> mapList = event1.getResponseHeaders();
                        if (mapList != null && mapList.size() > 0) {
                            List<String> arrayList = mapList.get("x-csrf-token");
                            if (arrayList != null && !arrayList.isEmpty()) {
                                CSRF_TOKEN = arrayList.get(0);
                            }
                        }
                    }
                    var1.onResponseReceived(event1);
                }).start();
    }

   /* public static void createEntitySimulate(final String requestID, final String requestString, final String resourcePath, UIListenerSimulate uiListener, Context mContext) {
        OnlineRequestListenerSimulate onlineReqListener = new OnlineRequestListenerSimulate(Operation.Create.getValue(), uiListener);
        SharedPreferences sharedPref = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        if (TextUtils.isEmpty(CSRF_TOKEN)){
            createCSRFToken(mContext, iReceiveEvent -> {
                if (iReceiveEvent.getResponseStatusCode() == 200) {
                    createEntitySimulate(requestID,requestString, resourcePath, uiListener, mContext);
                }
            });
        }else {
            HttpConversationManager manager = new HttpConversationManager(mContext);
            // Create the conversation.
            manager.create(Uri.parse(MyUtils.getDefaultOnlineQryURL() + resourcePath))
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("X-SMP-APPCID", sharedPref.getString(UtilRegistrationActivity.KEY_appConnID, ""))
                    .addHeader("x-csrf-token", CSRF_TOKEN)
                    .addHeader("RequestID", requestID)
                    .addHeader("Authorization", MyUtils.getBasicAuthCredential(mContext))
                    .addHeader("Accept", "application/json")
                    .setMethod(HttpMethod.POST)
                    .setRequestListener(event -> {
                        if (!TextUtils.isEmpty(requestString))
                            event.getWriter().write(requestString);
                        return null;
                    }).setResponseListener(event -> {
                // Process the results.
                if (event.getReader()!=null) {
                    //                    String responseBody = IReceiveEvent.Util.getResponseBody(event.getReader());
                    //                    Log.d("OnlineManager", "getUserRollInfo: " + responseBody + " " + event.getResponseStatusCode());
                    if (event.getResponseStatusCode() == 201) {
                        CSRF_TOKEN="";
                        String responseBody = IReceiveEvent.Util.getResponseBody(event.getReader());
                        onlineReqListener.notifySuccessToListener(null,responseBody);
                    } else if (event.getResponseStatusCode() == 403) {
                        createCSRFToken(mContext, iReceiveEvent -> {
                            if (iReceiveEvent.getResponseStatusCode() == 200) {
                                createEntitySimulate(requestID,requestString, resourcePath, uiListener, mContext);
                            } else {
                                onlineReqListener.notifyErrorToListener(iReceiveEvent);
                            }
                        });
                    } else {
                        onlineReqListener.notifyErrorToListener(event);
                    }
                }
            }).start();
        }
    }*/

    public static JSONObject getJSONBody(final IReceiveEvent event) throws IOException {
        try {
            JSONObject jsonObject = new JSONObject(IReceiveEvent.Util.getResponseBody(event.getReader()));
            return jsonObject.getJSONObject("d");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }
    public static JSONArray getJSONArrayBody(JSONObject jsonObject) throws IOException {
        try {
            return jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public static void batchUpdateMTP(JSONObject jsonCountStr, Context context, UIListener uiListener) throws IOException{

        //generate uniqueId for a batch boundary
        String batchGuid = GUID.newRandom().toString32();

        //generate uniqueId for each item to be inserted
        String changeSetId = "1";

        //Begin of: Prepare Bulk Request Format for SharePoint Bulk-Insert-Query ----------------
        String batchContents = "";
        try {
            String batchCnt_Update = "";
            JSONObject jsonRounteSchedule = new JSONObject();
            try {
                if(jsonCountStr.has(Constants.RouteSchGUID)) {
                    jsonRounteSchedule.put(Constants.RouteSchGUID, jsonCountStr.getString(Constants.RouteSchGUID));
                }
                if(jsonCountStr.has(Constants.SalesPersonID)) {
                    jsonRounteSchedule.put(Constants.SalesPersonID, jsonCountStr.getString(Constants.SalesPersonID));
                }
                if(jsonCountStr.has(Constants.Testrun)) {
                    jsonRounteSchedule.put(Constants.Testrun, jsonCountStr.getString(Constants.Testrun));
                }
                if(jsonCountStr.has(Constants.ValidFrom)) {
                    jsonRounteSchedule.put(Constants.ValidFrom, jsonCountStr.getString(Constants.ValidFrom));
                }
                if(jsonCountStr.has(Constants.ValidTo)) {
                    jsonRounteSchedule.put(Constants.ValidTo, jsonCountStr.getString(Constants.ValidTo));
                }
                if(jsonCountStr.has(Constants.Month)) {
                    jsonRounteSchedule.put(Constants.Month, jsonCountStr.getString(Constants.Month));
                }
                if(jsonCountStr.has(Constants.Year)) {
                    jsonRounteSchedule.put(Constants.Year, jsonCountStr.getString(Constants.Year));
                }
                if(jsonCountStr.has(Constants.RoutId)) {
                    jsonRounteSchedule.put(Constants.RoutId, jsonCountStr.optString(Constants.RoutId));
                }
                if(jsonCountStr.has(Constants.CreatedBy)) {
                    jsonRounteSchedule.put(Constants.CreatedBy, jsonCountStr.optString(Constants.CreatedBy));
                }
                if(jsonCountStr.has(Constants.CreatedOn)) {
                    jsonRounteSchedule.put(Constants.CreatedOn, jsonCountStr.optString(Constants.CreatedOn));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String endpoint_Update = null;

            endpoint_Update = "RouteSchedules(RouteSchGUID=guid'"+jsonRounteSchedule.getString(Constants.RouteSchGUID)+"')";

            batchCnt_Update = batchCnt_Update
                    + "--changeset_" + changeSetId + "\n"
                    + "Content-Type: application/http" + "\n"
                    + "Content-Transfer-Encoding: binary" + "\n"
                    + "" + "\n"
                    + "PUT " + endpoint_Update  + " HTTP/1.1" + "\n"
                    + "Content-Type: application/json" + "\n"
                    + "Accept: application/json" + "\n"
                    + "Content-Length: "+batchCnt_Update.length() + "\n"
                    + "" + "\n"
                    + jsonRounteSchedule.toString() + "\n";


            //END:   changeset to update data ----------
            batchContents = batchContents + "--batch_" + batchGuid + "\n"
                    + "Content-Type: multipart/mixed; boundary=changeset_" + changeSetId + "\n"
                    + "Content-Transfer-Encoding: binary" + "\n"
                    + "" + "\n"
                    + batchCnt_Update + "\n";

            try {
                batchCnt_Update = "";
                JSONObject jsonRouteScheduleSPs = new JSONObject();
                jsonRouteScheduleSPs.put(Constants.RouteSchSPGUID,GUID.newRandom().toString36());
                if (jsonCountStr.has(Constants.RouteSchGUID)) {
                    jsonRouteScheduleSPs.put(Constants.RouteSchGUID, jsonCountStr.get(Constants.RouteSchGUID));
                }
                if (jsonCountStr.has(Constants.SalesPersonID)) {
                    jsonRouteScheduleSPs.put(Constants.SalesPersonID, jsonCountStr.get(Constants.SalesPersonID));
                }
                jsonRouteScheduleSPs.put(Constants.DMSDivision, "00");
                endpoint_Update = "RouteScheduleSPs(RouteSchSPGUID=guid'"+jsonRouteScheduleSPs.getString(Constants.RouteSchSPGUID)+"')";

                batchCnt_Update = batchCnt_Update
                        + "--changeset_" + changeSetId + "\n"
                        + "Content-Type: application/http" + "\n"
                        + "Content-Transfer-Encoding: binary" + "\n"
                        + "" + "\n"
                        + "PUT " + endpoint_Update  + " HTTP/1.1" + "\n"
                        + "Content-Type: application/json" + "\n"
                        + "Accept: application/json" + "\n"
                        + "Content-Length: "+batchCnt_Update.length() + "\n"
                        + "" + "\n"
                        + jsonRouteScheduleSPs.toString() + "\n";

                //END:   changeset to update data ----------

                //create batch for creating items
                batchContents = batchContents + batchCnt_Update + "\n";

                JSONArray itemsArray = new JSONArray(jsonCountStr.getString(Constants.RouteSchedulePlans));
                JSONArray jsonArray = new JSONArray();
                for (int incrementVal = 0; incrementVal < itemsArray.length(); incrementVal++) {
                    JSONObject singleRow = itemsArray.getJSONObject(incrementVal);

                    JSONObject itemObject = new JSONObject();
                    if (singleRow.has(Constants.RouteSchPlanGUID)) {
                        itemObject.put(Constants.RouteSchPlanGUID, singleRow.get(Constants.RouteSchPlanGUID));
                    }
                    if (singleRow.has(Constants.RouteSchGUID)) {
                        itemObject.put(Constants.RouteSchGUID, singleRow.get(Constants.RouteSchGUID));
                    }
                    if (singleRow.has(Constants.VisitDate)) {
                        itemObject.put(Constants.VisitDate, singleRow.get(Constants.VisitDate));
                    }
                    if (singleRow.has(Constants.VisitCPGUID)) {
                        itemObject.put(Constants.VisitCPGUID, singleRow.get(Constants.VisitCPGUID));
                    }
                    if (singleRow.has(Constants.VisitCPName)) {
                        itemObject.put(Constants.VisitCPName, singleRow.get(Constants.VisitCPName));
                    }
                    if (singleRow.has(Constants.ActivityDesc)) {
                        itemObject.put(Constants.ActivityDesc, singleRow.get(Constants.ActivityDesc));
                    }
                    if (singleRow.has(Constants.ActivityID)) {
                        itemObject.put(Constants.ActivityID, singleRow.get(Constants.ActivityID));
                    }
                    if (singleRow.has(Constants.SalesDistrict)) {
                        itemObject.put(Constants.SalesDistrict, singleRow.get(Constants.SalesDistrict));
                    }
                    if (singleRow.has(Constants.SalesDistrictDesc)) {
                        itemObject.put(Constants.SalesDistrictDesc, singleRow.get(Constants.SalesDistrictDesc));
                    }
                    if (singleRow.has(Constants.Remarks)) {
                        itemObject.put(Constants.Remarks, singleRow.get(Constants.Remarks));
                    }

                    try {
                        batchCnt_Update = "";

                        endpoint_Update = "RouteSchedulePlans(RouteSchPlanGUID=guid'" + itemObject.getString(Constants.RouteSchPlanGUID) + "')";

                        batchCnt_Update = batchCnt_Update
                                + "--changeset_" + changeSetId + "\n"
                                + "Content-Type: application/http" + "\n"
                                + "Content-Transfer-Encoding: binary" + "\n"
                                + "" + "\n"
                                + "PUT " + endpoint_Update + " HTTP/1.1" + "\n"
                                + "Content-Type: application/json" + "\n"
                                + "Accept: application/json" + "\n"
                                + "Content-Length: "+batchCnt_Update.length() + "\n"
                                + "" + "\n"
                                + itemObject.toString() + "\n";


                        //END:create INSERT-Statement for one Item .........................


                        //END:   changeset to update data ----------

                        //create batch for creating items
                        batchContents = batchContents  + batchCnt_Update + "\n";
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                batchCnt_Update = batchCnt_Update + "--changeset_" + changeSetId + "--\n";
                //Start:create request in batch to get all items after update ---------

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            //End   of: Prepare Bulk Request Format for SharePoint Bulk-Insert-Query ----------------

            //Call SharePoint-REST to POST Items
            System.out.println(batchContents);
            postBatchRequest(batchContents, batchGuid,context, uiListener,"RouteSchedules");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private static void postBatchRequest(String batchRequest, String batchGuid,Context context, UIListener uiListener,String CollectionStr) throws IOException{

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME,0);
                String loginUser=sharedPreferences.getString("username","");
                String login_pwd=sharedPreferences.getString("password","");

                OnlineRequestListener onlineReqListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);
                String hostBatch = "https://"+ Configuration.server_Text+"/"+Configuration.APP_ID+"/$batch";
                String host = "https://"+ Configuration.server_Text+"/"+Configuration.APP_ID+"/$metadata"/*+CollectionStr*/;
                HttpsURLConnection connection = null;
                try {
                    String result = getPuserIdUtilsReponse(new URL(host), loginUser, login_pwd);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    connection = (HttpsURLConnection) new URL(hostBatch).openConnection();
                    connection.setReadTimeout(30000);
                    connection.setConnectTimeout(30000);
                    String userCredentials = loginUser + ":" + login_pwd;
                    String basicAuth = "Basic " + Base64.encodeToString(userCredentials.getBytes("UTF-8"), 2);
                    connection.setRequestProperty("Authorization", basicAuth);
                    connection.setRequestProperty("x-smp-appid", "com.arteriatech.mSFA");
                    connection.setRequestProperty("X-CSRF-Token", CSRF_TOKEN_BAtch);
                    for (int i = 0; i < setCookies.size(); i++) {
                        connection.addRequestProperty("Cookie", setCookies.get(i));
                    }
                    connection.addRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "multipart/mixed");
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "multipart/mixed; boundary=batch_"+ batchGuid +"");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
                    wr.writeBytes(batchRequest);
                    wr.flush();
                    wr.close();
                    int responseCode = connection.getResponseCode();
                    if (responseCode != 200 && responseCode != 400  && responseCode != 201  && responseCode != 202) {
                        onlineReqListener.notifyErrorToListener(connection.getResponseMessage());
                        throw new IOException("HTTP error code: " + responseCode);
                    }
                    if(responseCode==201 || responseCode == 202){
                        onlineReqListener.notifySuccessToListener(null);
                    }else {
                        onlineReqListener.notifyErrorToListener(connection.getResponseMessage());
                    }
                } catch (Exception var14) {
                    var14.printStackTrace();
                } finally {

                    if (connection != null) {
                        connection.disconnect();
                    }

                }
            }
        });

        thread.start();



    }
    static List<String> setCookies= new ArrayList<>();
    public static String getPuserIdUtilsReponse(final URL url, final String userName, final String psw) throws IOException {
        String result="";

        try  {
            //Your code goes here

            HttpsURLConnection connection = null;
            try {
                connection = (HttpsURLConnection)url.openConnection();
                connection.setReadTimeout(30000);
                connection.setConnectTimeout(30000);
                String userCredentials = userName + ":" + psw;
                String basicAuth = "Basic " + Base64.encodeToString(userCredentials.getBytes("UTF-8"), 2);
                connection.setRequestProperty("Authorization", basicAuth);
                connection.setRequestProperty("x-smp-appid", "com.arteriatech.mSFA");
                connection.setRequestProperty("x-smp-enduser", userName);
                connection.setRequestProperty("X-CSRF-Token", "Fetch");
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                int responseCode = connection.getResponseCode();

                connection.getResponseMessage();
                InputStream stream = null;

                if (responseCode != 200) {
                    throw new IOException("HTTP error code: " + responseCode);
                }else if(responseCode==200){
                    CSRF_TOKEN_BAtch = connection.getHeaderField("X-CSRF-Token");
                    setCookies.addAll(connection.getHeaderFields().get("Set-Cookie"));
                }

            } catch (Exception var12) {
                var12.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

//                String host = "https://mobile-acf7a3df7.hana.ondemand.com/com.arteriatech.geotracker/SPGeos";


        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    public static void batchUpdateRTGS(JSONObject fetchJsonHeaderObject, Context context, UIListener uiListener) throws IOException{

        //generate uniqueId for a batch boundary
        String batchGuid = GUID.newRandom().toString32();

        //generate uniqueId for each item to be inserted
        String changeSetId = "1";

        //Begin of: Prepare Bulk Request Format for SharePoint Bulk-Insert-Query ----------------
        String batchContents = "";
        try {
            String batchCnt_Update = "";
            //Parse the output-count JSON
            //SharePoint URL to insert one item
            JSONObject jsonCollSchedule = new JSONObject();
            try {
                if(fetchJsonHeaderObject.has(Constants.CollectionPlanGUID)) {
                    jsonCollSchedule.put(Constants.CollectionPlanGUID, fetchJsonHeaderObject.getString(Constants.CollectionPlanGUID));
                }
                if(fetchJsonHeaderObject.has(Constants.SPGUID)) {
                    jsonCollSchedule.put(Constants.SPGUID, fetchJsonHeaderObject.getString(Constants.SPGUID));
                }
                if(fetchJsonHeaderObject.has(Constants.Period)) {
                    jsonCollSchedule.put(Constants.Period, fetchJsonHeaderObject.getString(Constants.Period));
                }
                if(fetchJsonHeaderObject.has(Constants.Fiscalyear)) {
                    jsonCollSchedule.put(Constants.Fiscalyear, fetchJsonHeaderObject.getString(Constants.Fiscalyear));
                }
                try {
                    if(fetchJsonHeaderObject.has(Constants.CreatedOn)) {
                        if(!TextUtils.isEmpty(fetchJsonHeaderObject.getString(Constants.CreatedOn))) {
                            jsonCollSchedule.put(Constants.CreatedOn, fetchJsonHeaderObject.getString(Constants.CreatedOn)+"T00:00:00");
                        }
                    }
                    if(fetchJsonHeaderObject.has(Constants.CreatedBy)) {
                        if(!TextUtils.isEmpty(fetchJsonHeaderObject.getString(Constants.CreatedBy))) {
                            jsonCollSchedule.put(Constants.CreatedBy, fetchJsonHeaderObject.getString(Constants.CreatedBy));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String endpoint_Update = null;

            endpoint_Update = "CollectionPlans(CollectionPlanGUID=guid'"+jsonCollSchedule.getString(Constants.CollectionPlanGUID)+"')";

            batchCnt_Update = batchCnt_Update
                    + "--changeset_" + changeSetId + "\n"
                    + "Content-Type: application/http" + "\n"
                    + "Content-Transfer-Encoding: binary" + "\n"
                    + "" + "\n"
                    + "PUT " + endpoint_Update  + " HTTP/1.1" + "\n"
                    + "Content-Type: application/json" + "\n"
                    + "Accept: application/json" + "\n"
                    + "Content-Length: "+batchCnt_Update.length() + "\n"
                    + "" + "\n"
                    + jsonCollSchedule.toString() + "\n";


            //END:   changeset to update data ----------
            batchContents = batchContents + "--batch_" + batchGuid + "\n"
                    + "Content-Type: multipart/mixed; boundary=changeset_" + changeSetId + "\n"
                    + "Content-Transfer-Encoding: binary" + "\n"
                    + "" + "\n"
                    + batchCnt_Update + "\n";

            JSONArray itemsArray = new JSONArray(fetchJsonHeaderObject.getString(Constants.CollectionPlanItem));
            JSONArray jsonArray = new JSONArray();
            for (int incrementVal = 0; incrementVal < itemsArray.length(); incrementVal++) {
                JSONObject singleRow = itemsArray.getJSONObject(incrementVal);

                JSONObject itemObject = new JSONObject();
                if (singleRow.has(Constants.CollectionPlanItemGUID)) {
                    itemObject.put(Constants.CollectionPlanItemGUID, singleRow.get(Constants.CollectionPlanItemGUID));
                }
                if (singleRow.has(Constants.CollectionPlanGUID)) {
                    itemObject.put(Constants.CollectionPlanGUID, singleRow.get(Constants.CollectionPlanGUID));
                }
                if (singleRow.has(Constants.CollectionPlanDate)) {
                    itemObject.put(Constants.CollectionPlanDate, singleRow.get(Constants.CollectionPlanDate));
                }
                if (singleRow.has(Constants.CPNo)) {
                    itemObject.put(Constants.CPGUID, singleRow.get(Constants.CPNo));
                }
                if (singleRow.has(Constants.CPNo)) {
                    itemObject.put(Constants.CPNo, singleRow.get(Constants.CPNo));
                }
                if (singleRow.has(Constants.CPName)) {
                    itemObject.put(Constants.CPName, singleRow.get(Constants.CPName));
                }
                if (singleRow.has(Constants.Remarks)) {
                    itemObject.put(Constants.Remarks, singleRow.get(Constants.Remarks));
                }
                if (singleRow.has(Constants.PlannedValue)) {
                    itemObject.put(Constants.PlannedValue, singleRow.get(Constants.PlannedValue));
                }
                if (singleRow.has(Constants.AchievedValue)) {
                    itemObject.put(Constants.AchievedValue, singleRow.get(Constants.AchievedValue));
                }
                if (singleRow.has(Constants.Remarks)) {
                    itemObject.put(Constants.Remarks, singleRow.get(Constants.Remarks));
                }
                if (singleRow.has(Constants.Currency)) {
                    itemObject.put(Constants.Currency, singleRow.get(Constants.Currency));
                }
                if (singleRow.has(Constants.CrdtCtrlArea)) {
                    itemObject.put(Constants.CrdtCtrlArea, singleRow.get(Constants.CrdtCtrlArea));
                }
                if (singleRow.has(Constants.CrdtCtrlAreaDs)) {
                    itemObject.put(Constants.CrdtCtrlAreaDs, singleRow.get(Constants.CrdtCtrlAreaDs));
                }
                if (singleRow.has(Constants.CreatedOn)) {
                    if(!TextUtils.isEmpty(singleRow.getString(Constants.CreatedOn))) {
                        itemObject.put(Constants.CreatedOn, singleRow.get(Constants.CreatedOn)+"T00:00:00");
                    }
                }
                if (singleRow.has(Constants.CreatedBy)) {
                    if(!TextUtils.isEmpty(singleRow.getString(Constants.CreatedBy))) {
                        itemObject.put(Constants.CreatedBy, singleRow.get(Constants.CreatedBy));
                    }
                }
                itemObject.put(Constants.CPType, "01");

                try {
                    batchCnt_Update = "";
                    endpoint_Update = "CollectionPlanItemDetails(CollectionPlanItemGUID=guid'" + itemObject.getString(Constants.CollectionPlanItemGUID) + "')";

                    batchCnt_Update = batchCnt_Update
                            + "--changeset_" + changeSetId + "\n"
                            + "Content-Type: application/http" + "\n"
                            + "Content-Transfer-Encoding: binary" + "\n"
                            + "" + "\n"
                            + "PUT " + endpoint_Update + " HTTP/1.1" + "\n"
                            + "Content-Type: application/json" + "\n"
                            + "Accept: application/json" + "\n"
                            + "Content-Length: "+batchCnt_Update.length() + "\n"
                            + "" + "\n"
                            + itemObject.toString() + "\n";


                    //END:create INSERT-Statement for one Item .........................


                    //END:   changeset to update data ----------

                    //create batch for creating items
                    batchContents = batchContents + batchCnt_Update + "\n";
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            batchCnt_Update = batchCnt_Update + "--changeset_" + changeSetId + "--\n";
            //Start:create request in batch to get all items after update ---------

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //End   of: Prepare Bulk Request Format for SharePoint Bulk-Insert-Query ----------------

        //Call SharePoint-REST to POST Items
        System.out.println(batchContents);
        postBatchRequest(batchContents, batchGuid,context, uiListener,"CollectionPlans");

    }

    public static void createEntitySimulate(final String requestID, final String requestString, final String resourcePath, UIListener uiListener, Context mContext) {
        OnlineRequestListener onlineReqListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);
        SharedPreferences sharedPref = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        if (TextUtils.isEmpty(CSRF_TOKEN)){
            createCSRFToken(mContext, iReceiveEvent -> {
                if (iReceiveEvent.getResponseStatusCode() == 200) {
                    createEntitySimulate(requestID,requestString, resourcePath, uiListener, mContext);
                }
            });
        }else {
            HttpConversationManager manager = new HttpConversationManager(mContext);
            // Create the conversation.
            manager.create(Uri.parse(MyUtils.getDefaultOnlineQryURL() + resourcePath))
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("X-SMP-APPCID", sharedPref.getString(UtilRegistrationActivity.KEY_appConnID, ""))
                    .addHeader("x-csrf-token", CSRF_TOKEN)
                    .addHeader("RequestID", requestID)
                    .addHeader("Authorization", MyUtils.getBasicAuthCredential(mContext))
                    .addHeader("Accept", "application/json")
                    .setMethod(HttpMethod.POST)
                    .setRequestListener(event -> {
                        if (!TextUtils.isEmpty(requestString))
                            event.getWriter().write(requestString);
                        return null;
                    }).setResponseListener(event -> {
                // Process the results.
                if (event.getReader()!=null) {
                                        String responseBody = IReceiveEvent.Util.getResponseBody(event.getReader());
                    //                    Log.d("OnlineManager", "getUserRollInfo: " + responseBody + " " + event.getResponseStatusCode());
                    if (event.getResponseStatusCode() == 201) {
                        CSRF_TOKEN="";
                        onlineReqListener.notifySuccessToListener(responseBody);
                    } else if (event.getResponseStatusCode() == 403) {
                        createCSRFToken(mContext, iReceiveEvent -> {
                            if (iReceiveEvent.getResponseStatusCode() == 200) {
                                createEntitySimulate(requestID,requestString, resourcePath, uiListener, mContext);
                            } else {
                                if (iReceiveEvent.getReader()!=null) {
                                    String responseBodytemp = IReceiveEvent.Util.getResponseBody(iReceiveEvent.getReader());
                                    //                    Log.d("OnlineManager", "getUserRollInfo: " + responseBody + " " + event.getResponseStatusCode());
                                    onlineReqListener.notifyErrorToListenerMsg(responseBodytemp);
                                }
                            }
                        });
                    } else {
                        onlineReqListener.notifyErrorToListenerMsg(responseBody);
                    }
                }
            }).start();
        }
    }

    public static void batchUpdateSO(Hashtable headerhashtable, ArrayList<HashMap<String, String>> itemhashtable, Context context, UIListener uiListener) throws IOException{

        //generate uniqueId for a batch boundary
        String batchGuid = GUID.newRandom().toString32();

        //generate uniqueId for each item to be inserted
        String changeSetId = "1";

        //Begin of: Prepare Bulk Request Format for SharePoint Bulk-Insert-Query ----------------
        String batchContents = "";
        try {
            String batchCnt_Update = "";
            //Parse the output-count JSON
            //SharePoint URL to insert one item
            JSONObject jsonCollSchedule = new JSONObject();
            try {
                if (headerhashtable.get(Constants.SONo) != null) {
                    jsonCollSchedule.put(Constants.SONo,headerhashtable.get(Constants.SONo));
                }

                jsonCollSchedule.put(Constants.OrderType, headerhashtable.get(Constants.OrderType));

                if (headerhashtable.get(Constants.OrderDate) != null) {
                    jsonCollSchedule.put(Constants.OrderDate,headerhashtable.get(Constants.OrderDate).toString());
                }

                jsonCollSchedule.put(Constants.CustomerNo, headerhashtable.get(Constants.CustomerNo));
                jsonCollSchedule.put(Constants.CustomerPO, headerhashtable.get(Constants.CustomerPO));
                if (headerhashtable.get(Constants.CustomerPODate) != null && !TextUtils.isEmpty(headerhashtable.get(Constants.CustomerPODate).toString())) {
                    jsonCollSchedule.put(Constants.CustomerPODate,headerhashtable.get(Constants.CustomerPODate));
                }
                jsonCollSchedule.put(Constants.ShippingTypeID, headerhashtable.get(Constants.ShippingTypeID));
                jsonCollSchedule.put(Constants.MeansOfTranstyp, headerhashtable.get(Constants.MeansOfTranstyp));
                jsonCollSchedule.put(Constants.ShipToParty, headerhashtable.get(Constants.ShipToParty));
                jsonCollSchedule.put(Constants.SalesArea,headerhashtable.get(Constants.SalesArea));
                jsonCollSchedule.put(Constants.SalesOffice,headerhashtable.get(Constants.SalesOffice));
                jsonCollSchedule.put(Constants.SalesGroup, headerhashtable.get(Constants.SalesGroup));
                jsonCollSchedule.put(Constants.Plant,headerhashtable.get(Constants.Plant));

                jsonCollSchedule.put(Constants.Incoterm1, headerhashtable.get(Constants.Incoterm1));
                jsonCollSchedule.put(Constants.Incoterm2, headerhashtable.get(Constants.Incoterm2));

                jsonCollSchedule.put(Constants.Payterm,headerhashtable.get(Constants.Payterm));
                jsonCollSchedule.put(Constants.Currency,headerhashtable.get(Constants.Currency));
                if (headerhashtable.get(Constants.NetPrice) != null && !TextUtils.isEmpty(headerhashtable.get(Constants.NetPrice).toString())) {
                    jsonCollSchedule.put(Constants.NetPrice,headerhashtable.get(Constants.NetPrice));
                }
                if (headerhashtable.get(Constants.TotalAmount) != null && !TextUtils.isEmpty(headerhashtable.get(Constants.TotalAmount).toString())) {
                    jsonCollSchedule.put(Constants.TotalAmount,headerhashtable.get(Constants.TotalAmount));
                }
                if (headerhashtable.get(Constants.TaxAmount) != null && !TextUtils.isEmpty(headerhashtable.get(Constants.TaxAmount).toString())) {
                    jsonCollSchedule.put(Constants.TaxAmount,headerhashtable.get(Constants.TaxAmount));
                }
                if (headerhashtable.get(Constants.Freight) != null && !TextUtils.isEmpty(headerhashtable.get(Constants.Freight).toString())) {
                    jsonCollSchedule.put(Constants.Freight,headerhashtable.get(Constants.Freight));
                }
                if (headerhashtable.get(Constants.Discount) != null && !TextUtils.isEmpty(headerhashtable.get(Constants.Discount).toString())) {
                    jsonCollSchedule.put(Constants.Discount,headerhashtable.get(Constants.Discount));
                }
                if (headerhashtable.get(Constants.ReferenceNo) != null && !TextUtils.isEmpty(headerhashtable.get(Constants.ReferenceNo).toString())) {
                    jsonCollSchedule.put(Constants.ReferenceNo, headerhashtable.get(Constants.ReferenceNo));
                }
                if (headerhashtable.get(Constants.Testrun) != null) {
                    jsonCollSchedule.put(Constants.Testrun,headerhashtable.get(Constants.Testrun));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String endpoint_Update = null;

            endpoint_Update = Constants.SOs + "('" + headerhashtable.get(Constants.SONo) + "')";

            batchCnt_Update = batchCnt_Update
                    + "--changeset_" + changeSetId + "\n"
                    + "Content-Type: application/http" + "\n"
                    + "Content-Transfer-Encoding: binary" + "\n"
                    + "" + "\n"
                    + "PUT " + endpoint_Update  + " HTTP/1.1" + "\n"
                    + "Content-Type: application/json" + "\n"
                    + "Accept: application/json" + "\n"
                    + "Content-Length: "+batchCnt_Update.length() + "\n"
                    + "" + "\n"
                    + jsonCollSchedule.toString() + "\n";


            //END:   changeset to update data ----------
//            batchCnt_Update = batchCnt_Update + "--changeset_" + changeSetId + "--\n";
            batchContents = batchContents + "--batch_" + batchGuid + "\n"
                    + "Content-Type: multipart/mixed; boundary=changeset_" + changeSetId + "\n"
                    /*+ "Content-Length: " + batchCnt_Update.length() + "\n"*/
                    + "Content-Transfer-Encoding: binary" + "\n"
                    + "" + "\n"
                    + batchCnt_Update + "\n";

            for (int incrementVal = 0; incrementVal < itemhashtable.size(); incrementVal++) {
                HashMap<String, String> singleRow = itemhashtable.get(incrementVal);

                JSONObject itemObject = new JSONObject();
                if (singleRow.get(Constants.SONo) != null) {
                    itemObject.put(Constants.SONo, singleRow.get(Constants.SONo));
                }

                itemObject.put(Constants.ItemNo, singleRow.get(Constants.ItemNo));
                itemObject.put(Constants.HighLevellItemNo,singleRow.get(Constants.HighLevellItemNo));
                itemObject.put(Constants.ItemFlag, singleRow.get(Constants.ItemFlag));
                itemObject.put(Constants.ItemCategory,singleRow.get(Constants.ItemCategory));
                itemObject.put(Constants.Material,singleRow.get(Constants.Material));
                itemObject.put(Constants.Plant,singleRow.get(Constants.Plant));
                itemObject.put(Constants.UOM,singleRow.get(Constants.UOM));
                itemObject.put(Constants.Quantity,singleRow.get(Constants.Quantity));
                itemObject.put(Constants.Currency,singleRow.get(Constants.Currency));

                if (singleRow.get(Constants.RejReason) != null) {
                    if (!TextUtils.isEmpty(singleRow.get(Constants.RejReason))) {
                        itemObject.put(Constants.RejReason,singleRow.get(Constants.RejReason));
                    }
                }
                if (singleRow.get(Constants.RejReasonDesc) != null) {
                    if (!TextUtils.isEmpty(singleRow.get(Constants.RejReasonDesc))) {
                        itemObject.put(Constants.RejReasonDesc,singleRow.get(Constants.RejReasonDesc));
                    }
                }
                itemObject.put(Constants.UnitPrice,singleRow.get(Constants.UnitPrice).equalsIgnoreCase("") ? "0" : singleRow.get(Constants.UnitPrice));
                itemObject.put(Constants.NetAmount,singleRow.get(Constants.NetAmount).equalsIgnoreCase("") ? "0" : singleRow.get(Constants.NetAmount));
                itemObject.put(Constants.GrossAmount,singleRow.get(Constants.GrossAmount).equalsIgnoreCase("") ? "0" : singleRow.get(Constants.GrossAmount));
                itemObject.put(Constants.Freight,singleRow.get(Constants.Freight).equalsIgnoreCase("") ? "0" : singleRow.get(Constants.Freight));
                itemObject.put(Constants.Tax,singleRow.get(Constants.Tax).equalsIgnoreCase("") ? "0" : singleRow.get(Constants.Tax));
                itemObject.put(Constants.Discount,singleRow.get(Constants.Discount).equalsIgnoreCase("") ? "0" : singleRow.get(Constants.Discount));

                try {
                    batchCnt_Update = "";
//                if (singleRow.has(Constants.RouteSchSPGUID)) {

                    endpoint_Update =Constants.SOItemDetails + "(SONo='" + headerhashtable.get(Constants.SONo) + "',ItemNo='" + singleRow.get(Constants.ItemNo) + "')";

                    batchCnt_Update = batchCnt_Update
                            + "--changeset_" + changeSetId + "\n"
                            + "Content-Type: application/http" + "\n"
                            + "Content-Transfer-Encoding: binary" + "\n"
                            + "" + "\n"
                            + "PUT " + endpoint_Update + " HTTP/1.1" + "\n"
                            + "Content-Type: application/json" + "\n"
                            + "Accept: application/json" + "\n"
                            + "Content-Length: "+batchCnt_Update.length() + "\n"
                            + "" + "\n"
                            + itemObject.toString() + "\n";


                    //END:create INSERT-Statement for one Item .........................


                    //END:   changeset to update data ----------
//                        batchCnt_Update = batchCnt_Update + "--changeset_" + changeSetId + "--\n";
                    //create batch for creating items
                    batchContents = batchContents /*+ "\n"*/
                               /* + "--batch_" + batchGuid + "\n"
                                + "Content-Type: multipart/mixed; boundary=\"changeset_" + changeSetId + "\"\n"
                                + "Content-Length: " + batchCnt_Update.length() + "\n"
                                + "Content-Transfer-Encoding: binary" + "\n"
                                + "" + "\n"*/
                            + batchCnt_Update + "\n";
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if( headerhashtable.get("item_" + headerhashtable.get(Constants.SONo))!=null){
                String textString = headerhashtable.get("item_" + headerhashtable.get(Constants.SONo)).toString();
                if (!TextUtils.isEmpty(textString)) {
                    ArrayList<HashMap<String, String>> textItemList = UtilConstants.convertToArrayListMap(textString);
                    for (int j = 0; j < textItemList.size(); j++) {
                        JSONObject itemObject = new JSONObject();
                        HashMap<String, String> subTextSingleItem = textItemList.get(j);
                        itemObject.put(Constants.SONo, subTextSingleItem.get(Constants.SONo));
                        itemObject.put(Constants.Text, subTextSingleItem.get(Constants.Text));
                        itemObject.put(Constants.TextID, subTextSingleItem.get(Constants.TextID));
                        if (subTextSingleItem.get(Constants.LoginID) != null) {
                            itemObject.put(Constants.LoginID, subTextSingleItem.get(Constants.LoginID));
                        }

                        try {
                            batchCnt_Update = "";
//                if (singleRow.has(Constants.RouteSchSPGUID)) {

                            endpoint_Update = Constants.SOTexts + "(SONo='" + headerhashtable.get(Constants.SONo) + "',ItemNo='000000',TextID='0001',TextCategory='H')";

                            batchCnt_Update = batchCnt_Update
                                    + "--changeset_" + changeSetId + "\n"
                                    + "Content-Type: application/http" + "\n"
                                    + "Content-Transfer-Encoding: binary" + "\n"
                                    + "" + "\n"
                                    + "PUT " + endpoint_Update + " HTTP/1.1" + "\n"
                                    + "Content-Type: application/json" + "\n"
                                    + "Accept: application/json" + "\n"
                                    + "Content-Length: "+batchCnt_Update.length() + "\n"
                                    + "" + "\n"
                                    + itemObject.toString() + "\n";


                            //END:create INSERT-Statement for one Item .........................


                            //END:   changeset to update data ----------
//                        batchCnt_Update = batchCnt_Update + "--changeset_" + changeSetId + "--\n";
                            //create batch for creating items
                            batchContents = batchContents /*+ "\n"*/
                               /* + "--batch_" + batchGuid + "\n"
                                + "Content-Type: multipart/mixed; boundary=\"changeset_" + changeSetId + "\"\n"
                                + "Content-Length: " + batchCnt_Update.length() + "\n"
                                + "Content-Transfer-Encoding: binary" + "\n"
                                + "" + "\n"*/
                                    + batchCnt_Update + "\n";
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }


            }
            batchCnt_Update = batchCnt_Update + "--changeset_" + changeSetId + "--\n";
            //Start:create request in batch to get all items after update ---------
           /* String endpoint = MyUtils.getDefaultOnlineQryURL();
            batchContents = batchContents
                    + "--batch_" + batchGuid + "\n"
                    + "Content-Type: application/http" + "\n"
                    + "Content-Transfer-Encoding: binary" + "\n"
                    + "" + "\n"
                    + "GET " + endpoint + " HTTP/1.1" + "\n"
                    + "Accept: application/json;odata=verbose" + "\n"
                    + "" + "\n";
            //End:create request in batch to get all items after update -----------

            batchContents = batchContents + "--batch_" + batchGuid + "--";*/

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //End   of: Prepare Bulk Request Format for SharePoint Bulk-Insert-Query ----------------

        //Call SharePoint-REST to POST Items
        System.out.println(batchContents);
        postBatchRequest(batchContents, batchGuid,context, uiListener,"SOs");

    }

    public static ArrayList<GRReportBean> getGRReportList(JSONArray jsonArray) {
        ArrayList<GRReportBean> soArrayList = new ArrayList<>();

        GRReportBean salesPromotionBean = null;

        for (int i=0; i<jsonArray.length();i++){
            salesPromotionBean = new GRReportBean();
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                try {
                    salesPromotionBean.setInvoiceNo(jsonObject.optString(Constants.InvoiceNo));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                salesPromotionBean.setCustomerNo(jsonObject.optString(Constants.CustomerNo));
                salesPromotionBean.setCustomerName(jsonObject.optString(Constants.CustomerName));
                String  gregorianDateFormat = jsonObject.optString(Constants.InvoiceDate);
                try {
                    String  convertDateFormat =   ConstantsUtils.getJSONDate(gregorianDateFormat);;
                    salesPromotionBean.setInvoiceDate(convertDateFormat);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                salesPromotionBean.setInvoiceStatus(jsonObject.optString(Constants.InvoiceStatus));
                salesPromotionBean.setGRStatus(jsonObject.optString(Constants.GRStatus));
                salesPromotionBean.setRefDocNo(jsonObject.optString(Constants.RefDocNo));
                salesPromotionBean.setZZStateDesc(jsonObject.optString(Constants.ZZStateDesc));
                salesPromotionBean.setZZState(jsonObject.optString(Constants.ZZState));
                salesPromotionBean.setZZShortDmgSts(jsonObject.optString(Constants.ZZShortDmgSts));
                salesPromotionBean.setZZSalesDistDesc(jsonObject.optString(Constants.ZZSalesDistDesc));
                salesPromotionBean.setZZSalesDist(jsonObject.optString(Constants.ZZSalesDist));
                salesPromotionBean.setZZSalesAreaDesc(jsonObject.optString(Constants.ZZSalesAreaDesc));
                salesPromotionBean.setZZDistrict(jsonObject.optString(Constants.ZZDistrict));
                salesPromotionBean.setZZGRNo(jsonObject.optString(Constants.ZZGRNo));
                salesPromotionBean.setZZSalesArea(jsonObject.optString(Constants.ZZSalesArea));

                String  ZZTranspDateFormat = jsonObject.optString(Constants.ZZTranspDate);
                try {
                    String  convertDateFormat =   ConstantsUtils.getJSONDate(ZZTranspDateFormat);;
                    salesPromotionBean.setZZTranspDate(convertDateFormat);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String  ZZGRDateFormat = jsonObject.optString(Constants.ZZGRDate);
                try {
                    String  convertDateFormat =   ConstantsUtils.getJSONDate(ZZGRDateFormat);;
                    salesPromotionBean.setZZGRDate(convertDateFormat);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (jsonObject.optString(Constants.ZZTranspTime) != null && !TextUtils.isEmpty(jsonObject.optString(Constants.ZZTranspTime))) {

                        salesPromotionBean.setZZTranspTime(Constants.convertTimeFormat(jsonObject.optString(Constants.ZZTranspTime)));
                    } else {
                        salesPromotionBean.setZZTranspTime("");
                    }
                } catch (Exception e) {
                    salesPromotionBean.setZZTranspTime("");
                    e.printStackTrace();
                }

                try {
                    if (jsonObject.optString(Constants.ZZTotalTime) != null && !TextUtils.isEmpty(jsonObject.optString(Constants.ZZTotalTime))) {

                        if(jsonObject.optString(Constants.ZZTotalTime).equalsIgnoreCase(":")){
                            salesPromotionBean.setZZTotalTime("");
                        }else {
                            salesPromotionBean.setZZTotalTime(jsonObject.optString(Constants.ZZTotalTime));
                        }
                    } else {
                        salesPromotionBean.setZZTotalTime("");
                    }
                } catch (Exception e) {
                    salesPromotionBean.setZZTotalTime("");
                    e.printStackTrace();
                }

                try {
                    if (jsonObject.optString(Constants.ZZGRTime) != null && !TextUtils.isEmpty(jsonObject.optString(Constants.ZZGRTime))) {

                        salesPromotionBean.setZZGRTime(Constants.convertTimeFormat(jsonObject.optString(Constants.ZZGRTime)));
                    } else {
                        salesPromotionBean.setZZGRTime("");
                    }
                } catch (Exception e) {
                    salesPromotionBean.setZZGRTime("");
                    e.printStackTrace();
                }

                soArrayList.add(salesPromotionBean);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return soArrayList;
    }
}
