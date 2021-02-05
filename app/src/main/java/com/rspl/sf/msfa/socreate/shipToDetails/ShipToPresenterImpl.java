package com.rspl.sf.msfa.socreate.shipToDetails;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.collectionPlan.collectionCreate.SaleAreaBean;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.MSFAApplication;
import com.rspl.sf.msfa.so.ValueHelpBean;
import com.rspl.sf.msfa.soapproval.SalesOrderConditionsBean;
import com.rspl.sf.msfa.socreate.ConfigTypeValues;
import com.rspl.sf.msfa.socreate.CreditLimitBean;
import com.rspl.sf.msfa.socreate.CustomerPartnerFunctionBean;
import com.rspl.sf.msfa.socreate.OpenStoreIntentService;
import com.rspl.sf.msfa.socreate.SOConditionItemDetaiBean;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.socreate.SOSimulateAsyncTask;
import com.rspl.sf.msfa.solist.SOListBean;
import com.rspl.sf.msfa.store.GetOnlineODataInterface;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.store.OnlineManager;
import com.rspl.sf.msfa.store.OnlineStoreListener;
import com.sap.smp.client.httpc.events.IReceiveEvent;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataEntitySet;
import com.sap.smp.client.odata.ODataNavigationProperty;
import com.sap.smp.client.odata.ODataPayload;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;
import com.sap.smp.client.odata.store.ODataResponseSingle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by e10769 on 21-12-2017.
 */

public class ShipToPresenterImpl implements ShipToPresenter, OnlineODataInterface, UIListener,GetOnlineODataInterface {
    private final MSFAApplication mApplication;
    private Context mContext = null;
    private List<String> alAssignColl = new ArrayList<>();
    private ShipToView shipToView = null;
    private boolean isSessionRequired = false;
    private int totalRequest = 0;
    private int currentRequest = 0;
    private ArrayList<CustomerPartnerFunctionBean> shipToList = new ArrayList<>();
    private ArrayList<CustomerPartnerFunctionBean> oneTimeShipToList = new ArrayList<>();
    private ArrayList<ValueHelpBean> salesOfficeList = new ArrayList<>();
    private ArrayList<ValueHelpBean> salesGrpList = new ArrayList<>();
    private ArrayList<ValueHelpBean> paymentTermList = new ArrayList<>();
    private ArrayList<ValueHelpBean> incoterm1List = new ArrayList<>();
    private ArrayList<ValueHelpBean> shippingConditionList = new ArrayList<>();
    private ArrayList<ValueHelpBean> countryList = new ArrayList<>();
    private ArrayList<ValueHelpBean> regionList = new ArrayList<>();
    private SOListBean soListBeanHeader = null;
    private SOListBean soListBeanHeaderSimulation = null;
    private boolean requestShipTo = false;
    private String customerNo;
    private boolean isFinish = false;
    private BroadcastReceiver openReceiver = null;
    private String isErrorMsg = "";
    private boolean startSimulate = false;
    private SOListBean soListBeanHeaders = null;
    private int retryCount = 0;


    public ShipToPresenterImpl(String customerNo, Context mContexts, ShipToView shipToViews, boolean isSessionRequired, SOListBean soListBeanHeader) {
        this.mContext = mContexts;
        this.shipToView = shipToViews;
        this.isSessionRequired = isSessionRequired;
        this.soListBeanHeader = soListBeanHeader;
        this.customerNo = customerNo;
        mApplication = (MSFAApplication) mContexts.getApplicationContext();
    }

    @Override
    public void onStart() {
        if (!OfflineManager.checkOneTimeShipTo().isEmpty()) {
            if (shipToView != null) {
                shipToView.displayOneTimeShipToParty();
            }
            requestShipTo = true;
        }
        requestSalesAreaBased(soListBeanHeader.getSoldTo(), soListBeanHeader.getSalesArea());
    }

    private void requestSalesAreaBased(String customerNo, String salesArea) {
        if (shipToView != null) {
            shipToView.showProgressDialog(mContext.getString(R.string.app_loading));
        }

//        String qry = Constants.CustomerPartnerFunctions + "/?$filter=" + Constants.CustomerNo + "+eq+'" + customerNo + "'+and+SalesArea+eq+'" + salesArea + "'+and+PartnerFunctionID+eq+'SH'";
        String qry = Constants.CustomerPartnerFunctions + "?$filter=" + Constants.CustomerNo + " eq '" + customerNo + "' and SalesArea eq '" + salesArea + "' and PartnerFunctionID eq 'SH'";
        totalRequest = 2;
        currentRequest = 0;
        try {
            if(Constants.getRollID(mContext)){
                ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 3, ConstantsUtils.SESSION_HEADER, this, true);
            }else {
                ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 3, ConstantsUtils.SESSION_HEADER, this, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        qry = Constants.ValueHelps + "/?$filter=" + Constants.VHELP_MODELID_ENTITY_TYPE + "+and+(" + Constants.PropName + "+eq+'" + Constants.SalesOffice + "'+or+PropName+eq+'Payterm'+or+PropName+eq+'Incoterm1'+or+PropName+eq+'ShippingTypeID')+and+ParentID+eq+'" + salesArea + "'+and+PartnerNo+eq+'" + customerNo + "'";
        qry = Constants.ValueHelps + "?$filter=" + Constants.VHELP_MODELID_ENTITY_TYPE + " and (" + Constants.PropName + " eq '" + Constants.SalesOffice + "' or PropName eq 'Payterm' or PropName eq 'Incoterm1' or PropName eq 'ShippingTypeID') and PartnerNo eq '" + customerNo + "' and ParentID eq '" + salesArea + "'";
        ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 4, ConstantsUtils.SESSION_HEADER, this, false);
        if (requestShipTo) {
//            totalRequest++;
//            qry = Constants.Shiptos + "/?$filter=" + Constants.CustomerNo + "+eq+'" + customerNo + "'+and+SalesArea+eq+'" + salesArea + "'";
//            ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 7, ConstantsUtils.SESSION_HEADER, this);
            totalRequest++;
//            qry = Constants.ValueHelps + "/?$filter=" + Constants.VHELP_MODELID_ENTITY_TYPE_CHANNELPART + "+and+" + Constants.PropName + "+eq+'" + Constants.Country + "'";
            qry = Constants.ValueHelps + "/?$filter=" + Constants.VHELP_MODELID_ENTITY_TYPE_CHANNELPART + " and " + Constants.PropName + " eq '" + Constants.Country + "'";
            ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 8, ConstantsUtils.SESSION_HEADER, this, false);
        }
    }

    @Override
    public void onDestroy() {
        shipToView = null;
    }

    @Override
    public boolean validateFields(SOListBean soListBean) {
        boolean isNotError = true;
       /* if (TextUtils.isEmpty(soListBean.getOrderType())) {
            shipToView.errorOrderType("Select order type");
            isNotError = false;
        }*/
        if (TextUtils.isEmpty(soListBean.getShipTo())) {
            shipToView.errorShipToParty("Select ship to");
            isNotError = false;
        }
        if (TextUtils.isEmpty(soListBean.getPaymentTerm())) {
            shipToView.errorPaymentTerm("Select payment term");
            isNotError = false;
        }
        if (!TextUtils.isEmpty(soListBean.getIncoTerm1())) {
            if (TextUtils.isEmpty(soListBean.getIncoterm2())) {
                shipToView.errorIncoTerm2("Incoterm2 is required");
                isNotError = false;
            }
        }
        if (!TextUtils.isEmpty(soListBean.getIncoterm2())) {
            if (TextUtils.isEmpty(soListBean.getIncoTerm1())) {
                shipToView.errorIncoTerm("Select incoterm1");
                isNotError = false;
            }
        }

        if (soListBean.isOneTimeShipTo()) {
            if (TextUtils.isEmpty(soListBean.getCustLastName())) {
                shipToView.errorLastName("Last Name is required");
                isNotError = false;
            }
            if (TextUtils.isEmpty(soListBean.getCustAddress1())) {
                shipToView.errorAddress1("Address is required");
                isNotError = false;
            }
            if (TextUtils.isEmpty(soListBean.getCustDistrict())) {
                shipToView.errorDistrict("District is required");
                isNotError = false;
            }
            if (TextUtils.isEmpty(soListBean.getCustCity())) {
                shipToView.errorCity("City is required");
                isNotError = false;
            }
            if (TextUtils.isEmpty(soListBean.getCustCountry())) {
                shipToView.errorCountry("Select Country");
                isNotError = false;
            }
            if (TextUtils.isEmpty(soListBean.getCustRegion())) {
                shipToView.errorRegion("Select Region");
                isNotError = false;
            }
            if (TextUtils.isEmpty(soListBean.getCustPostalCode())) {
                shipToView.errorPostalCode("Postal Code is required");
                isNotError = false;
            }
        }
       /* if (TextUtils.isEmpty(soListBean.getIncoterm2())) {
            shipToView.errorIncoTerm2("Incoterm2 is required");
            isNotError = false;
        }*/
       /* if (TextUtils.isEmpty(soListBean.getShippingPoint())) {
            shipToView.errorShippingCondition("Select shipping condition");
            isNotError = false;
        }*/
        return isNotError;
    }

    @Override
    public void startSimulate(final SOListBean soListBeanHeader) {
        soListBeanHeaderSimulation = soListBeanHeader;
        if (UtilConstants.isNetworkAvailable(mContext)) {
            if (mApplication.isServiceFinished()) {
                startSOSimulate(soListBeanHeader);
            } else {
                if (shipToView != null && retryCount==0) {
                    shipToView.showProgressDialog(mContext.getString(R.string.app_loading));
                }
                mApplication.setBroadCastReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (shipToView != null) {
                            shipToView.hideProgressDialog();
                        }
                        if (intent.getAction().equals(OpenStoreIntentService.ACTION_SERVICE_KEY)) {
                            mApplication.unRegisterReceiver();
                            startSOSimulate(soListBeanHeader);
                        }
                    }
                });
            }
        } else {
            startSOSimulate(soListBeanHeader);
        }
       /* startSimulate = true;
        soListBeanHeaders=soListBeanHeader;
        if (isFinish && isErrorMsg.equalsIgnoreCase("Success")) {
            startSOSimulate(soListBeanHeader);
        }else if (!isFinish){
            if (shipToView != null) {
                shipToView.showProgressDialog(mContext.getString(R.string.app_loading));
            }
        }else if (isFinish){
            if (shipToView != null) {
                shipToView.displayMessage(isErrorMsg);
            }
        }*/
    }

    private void startSOSimulate(SOListBean soListBeanHeader) {

        if (UtilConstants.isNetworkAvailable(mContext)) {
            if (shipToView != null && retryCount==0) {
                shipToView.showProgressDialog(mContext.getString(R.string.app_loading));
            }
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 5);
            totalRequest = 1;
            currentRequest = 0;
            new SOSimulateAsyncTask(mContext, soListBeanHeader,new UIListener(){

                @Override
                public void onRequestError(int var1, Exception var2) {
                    if (shipToView != null) {
                        shipToView.hideProgressDialog();
                        shipToView.displayMessage(var2.getMessage());
                    }
                }

                @Override
                public void onRequestSuccess(int var1, String responseBody) throws ODataException, OfflineODataStoreException {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = getJSONBody(responseBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (jsonObject != null) {
                        try {
                            soListBeanHeader.setTotalAmt(jsonObject.optString(Constants.TotalAmount));
                            soListBeanHeader.setmStrTotalWeight(jsonObject.optString(Constants.NetWeight));
                            soListBeanHeader.setmStrWeightUOM(jsonObject.optString(Constants.NetWeightUom));
                            if (OfflineManager.checkNoUOMZero(soListBeanHeader.getmStrWeightUOM()))
                                soListBeanHeader.setmStrTotalWeight(OfflineManager.trimQtyDecimalPlace(soListBeanHeader.getmStrTotalWeight()));
                            else
                                soListBeanHeader.setmStrTotalWeight(soListBeanHeader.getmStrTotalWeight());
                            soListBeanHeader.setQuantity(OfflineManager.trimQtyDecimalPlace(jsonObject.optString(Constants.TotalQuantity)));
                            soListBeanHeader.setmSteTotalQtyUOM(jsonObject.optString(Constants.QuantityUom));
                            soListBeanHeader.setCurrency(jsonObject.optString(Constants.Currency));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONObject jsonObject1 = jsonObject.getJSONObject(Constants.SOItemDetails);
                            JSONArray entities = OnlineManager.getJSONArrayBody(jsonObject1);
                            ArrayList<SOItemBean> soItemList = new ArrayList<>();
                            soItemList.clear();
                            ArrayList<ConfigTypeValues> configTypeValuesList = OfflineManager.checkMaterialCodeDisplay();
                            for (int i = 0; i < entities.length(); i++) {
                                JSONObject jsonObject2 = entities.getJSONObject(i);
                                String itemNo = "";
                                if (jsonObject2.optString(Constants.ItemNo) != null) {
                                    itemNo = jsonObject2.optString(Constants.ItemNo);
                                }
                                SOItemBean soItemBeen = new SOItemBean();
                                if (jsonObject2.optString(Constants.Currency) != null) {
                                    soItemBeen.setCurrency(jsonObject2.optString(Constants.Currency));
                                }
                                if (jsonObject2.optString(Constants.ItemNo) != null) {
                                    soItemBeen.setItemNo(jsonObject2.optString(Constants.ItemNo));
                                }
                                if (jsonObject2.has(Constants.HighLevellItemNo)) {
                                    if (jsonObject2.optString(Constants.HighLevellItemNo) != null) {
                                        soItemBeen.setHighLevellItemNo(jsonObject2.optString(Constants.HighLevellItemNo));
                                    }
                                }

                                if (jsonObject2.has(Constants.ItemFlag)) {
                                    if (jsonObject2.optString(Constants.ItemFlag) != null) {
                                        soItemBeen.setItemFlag(jsonObject2.optString(Constants.ItemFlag));
                                    }
                                }

                                if (jsonObject2.has(Constants.ItemCategory)) {
                                    if (jsonObject2.optString(Constants.ItemCategory) != null) {
                                        soItemBeen.setItemCategory(jsonObject2.optString(Constants.ItemCategory));
                                    }
                                }

                                if (jsonObject2.optString(Constants.UOM) != null) {
                                    soItemBeen.setUom(jsonObject2.optString(Constants.UOM));
                                }

                                if (jsonObject2.optString(Constants.RejReason) != null) {
                                    soItemBeen.setRejectionId(jsonObject2.optString(Constants.RejReason));
                                }
                                if (jsonObject2.optString(Constants.RejReasonDesc) != null) {
                                    soItemBeen.setRejectionStatusDesc(jsonObject2.optString(Constants.RejReasonDesc));
                                }
                                if (!TextUtils.isEmpty(soItemBeen.getRejectionId()) && soItemBeen.getRejectionId().equalsIgnoreCase("99"))
                                    soItemBeen.setRemoved(true);
                                else
                                    soItemBeen.setRemoved(false);


                                if (jsonObject2.optString(Constants.AlternateWeight) != null) {
                                    soItemBeen.setAlternateWeight(jsonObject2.optString(Constants.AlternateWeight));
                                }
                                if (jsonObject2.optString(Constants.UnitPrice) != null) {
                                    soItemBeen.setUnitPrice(jsonObject2.optString(Constants.UnitPrice));
                                }
                                if (jsonObject2.optString(Constants.NetAmount) != null) {
                                    soItemBeen.setNetAmount(jsonObject2.optString(Constants.NetAmount));
                                }

                                if (jsonObject2.optString(Constants.Currency) != null) {
                                    soItemBeen.setCurrency(jsonObject2.optString(Constants.Currency));
                                }

                                if (jsonObject2.optString(Constants.Material) != null) {
                                    soItemBeen.setMatCode(jsonObject2.optString(Constants.Material));
                                }

                                if (jsonObject2.optString(Constants.MaterialDesc) != null) {
                                    soItemBeen.setMatDesc(jsonObject2.optString(Constants.MaterialDesc));
                                }
                                if (jsonObject2.optString(Constants.Quantity) != null) {
                                    soItemBeen.setQuantity(jsonObject2.optString(Constants.Quantity));
                                }

                                if (!configTypeValuesList.isEmpty()) {
                                    soItemBeen.setMatNoAndDesc(mContext.getString(R.string.po_details_display_value, soItemBeen.getMatDesc(), soItemBeen.getMatCode()));
                                } else {
                                    soItemBeen.setMatNoAndDesc(soItemBeen.getMatDesc());
                                }

                                if (jsonObject2.optString(Constants.Quantity) != null) {
                                    soItemBeen.setSoQty(jsonObject2.optString(Constants.Quantity));
                                }

                                if (!jsonObject2.isNull(Constants.SOConditionItemDetails)) {
                                    JSONObject jsonObject3 = jsonObject2.getJSONObject(Constants.SOConditionItemDetails);
                                    JSONArray array = OnlineManager.getJSONArrayBody(jsonObject3);
                                    ArrayList<SOConditionItemDetaiBean> soConditionItemDetaiBeenList = new ArrayList<>();
                                    ArrayList<SOConditionItemDetaiBean> soArrayListBeforeSort = new ArrayList<>();
                                    if (array != null && array.length() > 0) {

                                        SOConditionItemDetaiBean soConditionItemDetaiBean = null;
                                        ArrayList<String> tempList = new ArrayList<>();
                                        for (int j = 0; j < array.length(); j++) {
                                            JSONObject object = array.getJSONObject(j);
                                            soConditionItemDetaiBean = OfflineManager.getConditionItemDetails(object, soArrayListBeforeSort);
                                            if (soConditionItemDetaiBean != null && !tempList.contains(soConditionItemDetaiBean.getConditionTypeID())) {

                                                soArrayListBeforeSort.add(soConditionItemDetaiBean);
                                                tempList.add(soConditionItemDetaiBean.getConditionTypeID());
                                            }
                                        }
                                        soConditionItemDetaiBeenList.addAll(soArrayListBeforeSort);
                                        soItemBeen.setConditionItemDetaiBeanArrayList(soConditionItemDetaiBeenList);
                                        soItemList.add(soItemBeen);

                                    } else {
                                        soItemList.add(soItemBeen);
                                    }
                                }else {
                                    soItemList.add(soItemBeen);
                                }
                            }
                            ArrayList<SOConditionItemDetaiBean> finalConditionItemList = new ArrayList<>();
                            for (SOItemBean soItemBean : soItemList) {
                                ArrayList<SOConditionItemDetaiBean> soConditionItemList = soItemBean.getConditionItemDetaiBeanArrayList();
                                for (SOConditionItemDetaiBean soConditionItemDetaiBean : soConditionItemList) {
                                    SOConditionItemDetaiBean soCondition = OfflineManager.addConditionValues(soConditionItemDetaiBean, finalConditionItemList);
                                    if (soCondition != null) {
                                        finalConditionItemList.add(soConditionItemDetaiBean);
                                    }
                                }

                            }
                            Collections.sort(finalConditionItemList, new Comparator<SOConditionItemDetaiBean>() {
                                @Override
                                public int compare(SOConditionItemDetaiBean o1, SOConditionItemDetaiBean o2) {
                                    return o1.getConditionCounter().compareTo(o2.getConditionCounter());
                                }
                            });
                            BigDecimal totalNormalAmt = new BigDecimal("0.0");
                            if (!soItemList.isEmpty()) {
                                for (SOConditionItemDetaiBean soConditionItemDetaiBean : finalConditionItemList) {
                                    totalNormalAmt = totalNormalAmt.add(new BigDecimal(soConditionItemDetaiBean.getConditionValue()));
                                }
                                SOConditionItemDetaiBean soConditionItemDetaiBean = new SOConditionItemDetaiBean();
                                soConditionItemDetaiBean.setViewType("T");
                                soConditionItemDetaiBean.setName("Total");
                                soConditionItemDetaiBean.setConditionValue(totalNormalAmt + "");
                                finalConditionItemList.add(soConditionItemDetaiBean);
                                soItemList.get(0).setConditionItemDetaiBeanArrayList(finalConditionItemList);
                            }
                            soListBeanHeader.setTotalAmt(String.valueOf(totalNormalAmt));
                            soListBeanHeader.setSoItemBeanArrayList(soItemList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    refreshCreditLimit();
                }
            }, this, bundle).execute();
        } else {
            if (shipToView != null) {
                shipToView.displayMessage(mContext.getString(R.string.no_network_conn));
            }
            getCreditLimitValue(soListBeanHeader.getCustomerNo());
        }
    }

    public static JSONObject getJSONBody(final String responseBody) throws IOException {
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            return jsonObject.getJSONObject("d");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    @Override
    public void basedOnSalesOffice(String salesOfficeId, String customerNo) {
        if (shipToView != null) {
            shipToView.showProgressDialog(mContext.getString(R.string.app_loading));
        }

        totalRequest = 1;
        currentRequest = 0;
        String qry = Constants.ValueHelps + "/?$filter=" + Constants.VHELP_MODELID_ENTITY_TYPE + "+and+" + Constants.PropName + "+eq+'" + Constants.SalesGroup + "'+and+ParentID+eq+'" + salesOfficeId + "'+and+PartnerNo+eq+'" + customerNo + "'";
        ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 6, ConstantsUtils.SESSION_HEADER, this, false);
    }

    @Override
    public void basedOnCountry(String countryId) {
        if (shipToView != null) {
            shipToView.showProgressDialog(mContext.getString(R.string.app_loading));
        }

        totalRequest = 1;
        currentRequest = 0;
        String qry = Constants.ValueHelps + "/?$filter=" + Constants.VHELP_MODELID_ENTITY_TYPE_CHANNELPART + "+and+" + Constants.PropName + "+eq+'" + Constants.StateID + "'+and+ParentID+eq+'000003" + countryId + "'";
        ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 9, ConstantsUtils.SESSION_HEADER, this, false);
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        Log.d("responseSuccess","291");
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        switch (type) {
            case 3:
                shipToList.clear();
                try {
                    shipToList.addAll(OnlineManager.getCustomerPartnerDataFunction(list));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                currentRequest++;
                break;
            case 4:
                salesOfficeList.clear();
                try {
                    salesOfficeList.addAll(OnlineManager.getConfigListFromValueHelp(list, Constants.SalesOffice));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                paymentTermList.clear();
                try {
                    paymentTermList.addAll(OnlineManager.getConfigListFromValueHelp(list, Constants.Payterm));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                incoterm1List.clear();
                try {
                    incoterm1List.addAll(OnlineManager.getConfigListFromValueHelp(list, Constants.Incoterm1, Constants.None));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                shippingConditionList.clear();
                try {
                    shippingConditionList.addAll(OnlineManager.getConfigListFromValueHelp(list, Constants.ShippingTypeID));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                currentRequest++;

                break;
            case 5:
                ODataPayload payload = ((ODataResponseSingle) oDataRequestExecution.getResponse()).getPayload();
                String testRun = "";
                if (payload != null && payload instanceof ODataEntity) {
                    ODataEntity oEntity = (ODataEntity) payload;
                    ODataEntity oEntityReq = oEntity;
                    ODataPropMap properties = oEntityReq.getProperties();
                    ODataProperty property;
                    try {
                        property = properties.get(Constants.TotalAmount);
                        soListBeanHeader.setTotalAmt(property.getValue().toString());
                        property = properties.get(Constants.NetWeight);
                        soListBeanHeader.setmStrTotalWeight(property.getValue().toString());
                        property = properties.get(Constants.NetWeightUom);
                        soListBeanHeader.setmStrWeightUOM(property.getValue().toString());
                        if (OfflineManager.checkNoUOMZero(soListBeanHeader.getmStrWeightUOM()))
                            soListBeanHeader.setmStrTotalWeight(OfflineManager.trimQtyDecimalPlace(soListBeanHeader.getmStrTotalWeight()));
                        else
                            soListBeanHeader.setmStrTotalWeight(soListBeanHeader.getmStrTotalWeight());
                        property = properties.get(Constants.TotalQuantity);
                        soListBeanHeader.setQuantity(OfflineManager.trimQtyDecimalPlace(property.getValue().toString()));
                        property = properties.get(Constants.QuantityUom);
                        soListBeanHeader.setmSteTotalQtyUOM(property.getValue().toString());
                        property = properties.get(Constants.Currency);
                        soListBeanHeader.setCurrency(property.getValue().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ODataNavigationProperty navProp = oEntity.getNavigationProperty(Constants.SOItemDetails);
                    ArrayList<SOItemBean> soItemList = new ArrayList<>();
                    soItemList.clear();
                    ArrayList<ConfigTypeValues> configTypeValuesList = OfflineManager.checkMaterialCodeDisplay();
                    if (navProp.getNavigationType().toString().equalsIgnoreCase("EntitySet")) {
                        ODataEntitySet feed = (ODataEntitySet) navProp.getNavigationContent();
                        List<ODataEntity> entities = feed.getEntities();
                        for (ODataEntity entity : entities) {
                            properties = entity.getProperties();
                            property = properties.get(Constants.ItemNo);
                            String itemNo = "";
                            if (property != null) {
                                itemNo = property.getValue().toString();
                            }
                            SOItemBean soItemBeen = new SOItemBean();// = SOUtils.getItemBasedOnId(soListBeanHeader.getSoItemBeanArrayList(), itemNo);
//                            if (soItemBeen != null) {
                            property = properties.get(Constants.Currency);
                            if (property != null) {
                                soItemBeen.setCurrency(property.getValue().toString());
                            }
                            property = properties.get(Constants.ItemNo);
                            if (property != null) {
                                soItemBeen.setItemNo(property.getValue().toString());
                            }
                            if(properties.get(Constants.HighLevellItemNo)!=null) {
                                property = properties.get(Constants.HighLevellItemNo);
                                if (property != null) {
                                    soItemBeen.setHighLevellItemNo(property.getValue().toString());
                                }
                            }

                            if(properties.get(Constants.ItemFlag)!=null) {
                                property = properties.get(Constants.ItemFlag);
                                if (property != null) {
                                    soItemBeen.setItemFlag(property.getValue().toString());
                                }
                            }

                            if (properties.get(Constants.ItemCategory) != null) {
                                property = properties.get(Constants.ItemCategory);
                                if (property != null) {
                                    soItemBeen.setItemCategory(property.getValue().toString());
                                }
                            }

                            property = properties.get(Constants.UOM);
                            if (property != null) {
                                soItemBeen.setUom(property.getValue().toString());
                            }

                            property = properties.get(Constants.RejReason);
                            if (property != null) {
                                soItemBeen.setRejectionId(property.getValue().toString());
                            }
                            property = properties.get(Constants.RejReasonDesc);
                            if (property != null) {
                                soItemBeen.setRejectionStatusDesc(property.getValue().toString());
                            }
                            if(!TextUtils.isEmpty(soItemBeen.getRejectionId()) && soItemBeen.getRejectionId().equalsIgnoreCase("99"))
                                soItemBeen.setRemoved(true);
                            else
                                soItemBeen.setRemoved(false);



                            property = properties.get(Constants.AlternateWeight);
                            if (property != null) {
                                soItemBeen.setAlternateWeight(property.getValue().toString());
                            }
                            property = properties.get(Constants.UnitPrice);
                            if (property != null) {
                                soItemBeen.setUnitPrice(property.getValue().toString());
                            }
                            property = properties.get(Constants.NetAmount);
                            if (property != null) {
                                soItemBeen.setNetAmount(property.getValue().toString());
                            }

                            property = properties.get(Constants.Currency);
                            if (property != null) {
                                soItemBeen.setCurrency(property.getValue().toString());
                            }

                            property = properties.get(Constants.Material);
                            if (property != null) {
                                soItemBeen.setMatCode(property.getValue().toString());
                            }

                            property = properties.get(Constants.MaterialDesc);
                            if (property != null) {
                                soItemBeen.setMatDesc(property.getValue().toString());
                            }
                            property = properties.get(Constants.Quantity);
                            if (property != null) {
                                soItemBeen.setQuantity(property.getValue().toString());
                            }

                            if (!configTypeValuesList.isEmpty()) {
                                soItemBeen.setMatNoAndDesc(mContext.getString(R.string.po_details_display_value, soItemBeen.getMatDesc(), soItemBeen.getMatCode()));
                            } else {
                                soItemBeen.setMatNoAndDesc(soItemBeen.getMatDesc());
                            }

                            property = properties.get(Constants.Quantity);
                            if (property != null) {
                                soItemBeen.setSoQty(property.getValue().toString());
                            }

                            ODataNavigationProperty navProp2 = entity.getNavigationProperty(Constants.SOConditionItemDetails);
                            ArrayList<SOConditionItemDetaiBean> soConditionItemDetaiBeenList = new ArrayList<>();
                            ArrayList<SOConditionItemDetaiBean> soArrayListBeforeSort = new ArrayList<>();
                            if (navProp2.getNavigationType().toString().equalsIgnoreCase("EntitySet")) {

                                ODataEntitySet feedCondition = (ODataEntitySet) navProp2.getNavigationContent();
                                List<ODataEntity> entitiesCondition = feedCondition.getEntities();
                                SOConditionItemDetaiBean soConditionItemDetaiBean = null;
                                ArrayList<String> tempList = new ArrayList<>();
//                                BigDecimal totalNormalAmt = new BigDecimal("0.0");
//                                BigDecimal subTotalAmt = new BigDecimal("0.0");
                                Map<String, Double> mapConditionItemDetails = new HashMap<>();
                                for (ODataEntity entityCondition : entitiesCondition) {
                                    soConditionItemDetaiBean = OfflineManager.getConditionItemDetails(entityCondition, soArrayListBeforeSort);
                                   /* if(soConditionItemDetaiBean!=null) {
                                        if (mapConditionItemDetails.containsKey(soConditionItemDetaiBean.getConditionTypeID())) {
                                            double mDoubConditionCounter = 0;
                                            try {
                                                if(!TextUtils.isEmpty(soConditionItemDetaiBean.getConditionCounter())) {
                                                    mDoubConditionCounter = Double.parseDouble(soConditionItemDetaiBean.getConditionCounter()) + mapConditionItemDetails.get(soConditionItemDetaiBean.getConditionTypeID());
                                                }
                                            } catch (Throwable e) {
                                                e.printStackTrace();
                                            }
                                            mapConditionItemDetails.put(soConditionItemDetaiBean.getConditionTypeID(), mDoubConditionCounter);
                                        }else {
                                            double mDoubConditionCounter = 0;
                                            try {
                                                if(!TextUtils.isEmpty(soConditionItemDetaiBean.getConditionCounter())) {
                                                    mDoubConditionCounter = Double.parseDouble(soConditionItemDetaiBean.getConditionCounter());
                                                }
                                            } catch (Throwable e) {
                                                e.printStackTrace();
                                            }
                                            mapConditionItemDetails.put(soConditionItemDetaiBean.getConditionTypeID(), mDoubConditionCounter);
                                        }
                                    }*/
//                                    if (soConditionItemDetaiBean != null && !tempList.contains(soConditionItemDetaiBean.getConditionTypeID())) {
                                    if (soConditionItemDetaiBean != null && !tempList.contains(soConditionItemDetaiBean.getConditionCounter())) {
//                                        totalNormalAmt = totalNormalAmt.add(new BigDecimal(soConditionItemDetaiBean.getAmount()));
//                                        subTotalAmt = subTotalAmt.add(new BigDecimal(soConditionItemDetaiBean.getConditionValue()));
                                        soArrayListBeforeSort.add(soConditionItemDetaiBean);
//                                        tempList.add(soConditionItemDetaiBean.getConditionTypeID());
                                        tempList.add(soConditionItemDetaiBean.getConditionCounter());
                                    }
                                }
                                /* for(SOConditionItemDetaiBean soConditionItemDetaiBean1 : soArrayListBeforeSort){
                                    double conditionCounter = mapConditionItemDetails.get(soConditionItemDetaiBean1.getConditionTypeID());
                                    soConditionItemDetaiBean1.setConditionCounter(""+conditionCounter);
                                }*/
                                soConditionItemDetaiBeenList.addAll(soArrayListBeforeSort);
                                soItemBeen.setConditionItemDetaiBeanArrayList(soConditionItemDetaiBeenList);
                                soItemList.add(soItemBeen);

                            } else {
                                soItemList.add(soItemBeen);
                            }

                           /* ODataNavigationProperty navProp2 = oEntity.getNavigationProperty(Constants.SOConditions);
                            ArrayList<SOConditionItemDetaiBean> soConditionItemDetaiBeenList = new ArrayList<>();
                            ArrayList<SOConditionItemDetaiBean> soArrayListBeforeSort = new ArrayList<>();
                            if (navProp2.getNavigationType().toString().equalsIgnoreCase("EntitySet")) {

                                ODataEntitySet feedCondition = (ODataEntitySet) navProp2.getNavigationContent();
                                List<ODataEntity> entitiesCondition = feedCondition.getEntities();
                                SOConditionItemDetaiBean soConditionItemDetaiBean = null;
                                BigDecimal totalNormalAmt = new BigDecimal("0.0");
                                BigDecimal subTotalAmt = new BigDecimal("0.0");
                                for (ODataEntity entityCondition : entitiesCondition) {
                                    soConditionItemDetaiBean = OfflineManager.getConditionItemDetails(entityCondition);
                                    if (soConditionItemDetaiBean != null) {
                                        totalNormalAmt = totalNormalAmt.add(new BigDecimal(soConditionItemDetaiBean.getAmount()));
                                        subTotalAmt = subTotalAmt.add(new BigDecimal(soConditionItemDetaiBean.getConditionValue()));
                                        soArrayListBeforeSort.add(soConditionItemDetaiBean);
                                    }
                                }
                                soConditionItemDetaiBeenList.addAll(soArrayListBeforeSort);
                                soItemBeen.setConditionItemDetaiBeanArrayList(soConditionItemDetaiBeenList);
                                soItemList.add(soItemBeen);

                            } else {
                                soItemList.add(soItemBeen);
                            }*/


//                            }
                        }
                        ArrayList<SOConditionItemDetaiBean> finalConditionItemList = new ArrayList<>();
                        for (SOItemBean soItemBean : soItemList) {
                            if (soItemBean.getRejectionId()!=null && !soItemBean.getRejectionId().equalsIgnoreCase("99")){
                                ArrayList<SOConditionItemDetaiBean> soConditionItemList = soItemBean.getConditionItemDetaiBeanArrayList();
                                for (SOConditionItemDetaiBean soConditionItemDetaiBean : soConditionItemList) {
                                    SOConditionItemDetaiBean soCondition = OfflineManager.addConditionValues(soConditionItemDetaiBean, finalConditionItemList);
                                    if (soCondition != null) {
                                        finalConditionItemList.add(soConditionItemDetaiBean);
                                    }
                                }
                            }
                        }
                        Collections.sort(finalConditionItemList, new Comparator<SOConditionItemDetaiBean>() {
                            @Override
                            public int compare(SOConditionItemDetaiBean o1, SOConditionItemDetaiBean o2) {
                                return o1.getConditionCounter().compareTo(o2.getConditionCounter());
                            }
                        });
                        BigDecimal totalNormalAmt = new BigDecimal("0.0");
                        if (!soItemList.isEmpty()) {
                            for (SOConditionItemDetaiBean soConditionItemDetaiBean : finalConditionItemList) {
                                totalNormalAmt = totalNormalAmt.add(new BigDecimal(soConditionItemDetaiBean.getConditionValue()));
                            }
                            SOConditionItemDetaiBean soConditionItemDetaiBean = new SOConditionItemDetaiBean();
                            soConditionItemDetaiBean.setViewType("T");
                            soConditionItemDetaiBean.setName("Total");
                            soConditionItemDetaiBean.setConditionValue(totalNormalAmt + "");
                            finalConditionItemList.add(soConditionItemDetaiBean);
                            soItemList.get(0).setConditionItemDetaiBeanArrayList(finalConditionItemList);
                        }
                        soListBeanHeader.setTotalAmt(String.valueOf(totalNormalAmt));
                        soListBeanHeader.setSoItemBeanArrayList(soItemList);
                    }
                }
                refreshCreditLimit();

                break;
            case 6:
                salesGrpList.clear();
                try {
                    salesGrpList.addAll(OnlineManager.getConfigListFromValueHelp(list, Constants.SalesGroup));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                currentRequest++;
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (shipToView != null) {
                            shipToView.hideProgressDialog();
                            shipToView.displaySalesGrp(salesGrpList);
                        }
                    }
                });
                break;
           /* case 7:
                oneTimeShipToList.clear();
                try {
                    oneTimeShipToList.addAll(OnlineManager.getCustomerPartnerDataFunction(list));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                currentRequest++;
                break;*/
            case 8:
                countryList.clear();
                try {
                    countryList.addAll(OnlineManager.getConfigListFromValueHelp(list, Constants.Country, Constants.None));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                currentRequest++;
                break;
            case 9:
                regionList.clear();
                try {
                    regionList.addAll(OnlineManager.getConfigListFromValueHelp(list, Constants.StateID, Constants.None));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                currentRequest++;
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (shipToView != null) {
                            shipToView.hideProgressDialog();
                            shipToView.displayRegion(regionList);
                        }
                    }
                });
                break;
        }
        if (type != 5 && currentRequest == totalRequest && type != 6 && type != 9) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (shipToView != null) {
                        shipToView.hideProgressDialog();
                        shipToView.displayBySalesArea(shipToList, incoterm1List, paymentTermList, salesOfficeList, shippingConditionList, countryList);
                    }
                }
            });
        }
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, String s, Bundle bundle) {
        try {
            if (!TextUtils.isEmpty(s) && s.contains("CSRF token validation failed") && retryCount == 0) {
               /* Bundle bundlereq = new Bundle();
                bundlereq.putString(Constants.BUNDLE_RESOURCE_PATH, Constants.KPISet);
                bundlereq.putInt(Constants.BUNDLE_REQUEST_CODE, 1);
                bundlereq.putInt(Constants.BUNDLE_OPERATION, Operation.GetRequest.getValue());
                bundlereq.putBoolean(Constants.BUNDLE_SESSION_REQUIRED, true);
                bundlereq.putBoolean(Constants.BUNDLE_SESSION_URL_REQUIRED, true);
                try {
                    OnlineManager.requestOnline(this, bundlereq, mContext);
                } catch (Exception exception) {
                    LogManager.writeLogError(Constants.error_txt1 + " : " + exception.getMessage());
                    exception.printStackTrace();
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (shipToView != null) {
                                shipToView.hideProgressDialog();
                            }
                        }
                    });
                }*/
                boolean isStoreOpened = true;
                Constants.IsOnlineStoreFailed = false;
                Log.d("ApprovalList","Opening Store");

                if (isStoreOpened) {
                    try {
                        startSimulate(soListBeanHeaderSimulation);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        LogManager.writeLogError(Constants.error_txt + " : " + e2.getMessage());
                        if (shipToView != null) {
                            shipToView.hideProgressDialog();
                        }
                    }
                }else{
                    LogManager.writeLogError(Constants.error_txt + " : " + "Store not opened cant post Approve SO");
                    if (shipToView != null) {
                        shipToView.hideProgressDialog();
                    }
                }
                retryCount++;

            } else {
                currentRequest++;
                if (totalRequest == currentRequest) {
                    showErrorResponse(s);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showErrorResponse(final String errorMsg) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (shipToView != null) {
                    shipToView.hideProgressDialog();
                    shipToView.displayMessage(errorMsg);
                }
            }
        });
    }

    private void refreshCreditLimit() {
        try {
//            if(Constants.getRollID(mContext)) {

           /* ArrayList<String> salesArea = null;
            try {
                salesArea = OfflineManager.getSaleAreaFromUsrAth("UserProfileAuthSet?$filter=Application%20eq%20%27PD%27" + " &$orderby=AuthOrgTypeID asc");
            } catch (Exception e) {
                e.printStackTrace();
            }
            String stringSaleArea = "";
            if (salesArea != null && !salesArea.isEmpty()) {
                for (int z = 0; z < salesArea.size(); z++) {
                    if (z == salesArea.size() - 1) {
                        stringSaleArea = stringSaleArea + "SalesArea%20eq%20'" + salesArea.get(z) + "'";
                    } else {
                        stringSaleArea = stringSaleArea + "SalesArea%20eq%20'" + salesArea.get(z) + "'%20or%20";
                    }
                }
            }*/


            ArrayList<String> salesArea = new ArrayList<>();
            try {
                salesArea = OfflineManager.getSalesArea("UserProfileAuthSet?$filter=Application%20eq%20%27PD%27" + " &$orderby=AuthOrgTypeID asc");
            } catch (Exception e) {
                e.printStackTrace();
            }

            ArrayList<String> distributorChannel = new ArrayList<>();
            try {
                distributorChannel = OfflineManager.getDistibuterChannelIds("UserProfileAuthSet?$filter=Application%20eq%20%27PD%27" + " and AuthOrgTypeID eq '000008'");
            } catch (Exception e) {
                e.printStackTrace();
            }

            ArrayList<String> salesArearlist = new ArrayList<>();
            salesArearlist.clear();

            for (int i = 0; i < distributorChannel.size(); i++) {
                for (int j = 0; j < salesArea.size(); j++) {
                    String saleArea[] = salesArea.get(j).split("/");

                    salesArearlist.add(saleArea[0] + "/" + distributorChannel.get(i) + "/" + saleArea[1]);

                    System.out.println("Sales Area List : " + saleArea[0] + "/" + distributorChannel.get(i) + "/" + saleArea[1]);

                }

            }


            String stringSaleArea = "";
            if (salesArearlist != null && !salesArearlist.isEmpty()) {
                for (int z = 0; z < salesArearlist.size(); z++) {
                    if (z == salesArearlist.size() - 1) {
                        stringSaleArea = stringSaleArea + "SalesArea%20eq%20'" + salesArearlist.get(z) + "'";
                    } else {
                        stringSaleArea = stringSaleArea + "SalesArea%20eq%20'" + salesArearlist.get(z) + "'%20or%20";
                    }
                }
            }


            String creditControlAreas = "";
            if (!TextUtils.isEmpty(stringSaleArea)) {
                String qry = Constants.CustomerSalesAreas + "?$filter=" + Constants.CustomerNo + "%20eq%20'" + customerNo + "'%20and%20(" + stringSaleArea + ")";
                System.out.println("CustomerSalesAreas 6"+qry);

                ArrayList<SaleAreaBean> sortList =OfflineManager.getSaleAreaFromCustomerCreditLmt(qry);
                Collections.sort(sortList, new Comparator<SaleAreaBean>() {
                    public int compare(SaleAreaBean one, SaleAreaBean other) {
                        return one.getCreditControlAreaID().compareTo(other.getCreditControlAreaID());
                    }
                });
                //  customerBean.setSaleAreaBeanAl(sortList);

                if (sortList != null && !sortList.isEmpty()) {
                    for (int z = 0; z < sortList.size(); z++) {
                        if (z == sortList.size() - 1) {
                            creditControlAreas = creditControlAreas + "CreditControlAreaID%20eq%20'" + sortList.get(z).getCreditControlAreaID() + "'";
                        } else {
                            creditControlAreas = creditControlAreas + "CreditControlAreaID%20eq%20'" + sortList.get(z).getCreditControlAreaID() + "'%20or%20";
                        }
                    }
                }
            }



            try {
//                String qry = Constants.CustomerCreditLimits + "?$select=BalanceAmount,Currency&$filter=" + Constants.Customer + " eq '" + selectedSOItem.getCustomerNo() + "'";
                if (UtilConstants.isNetworkAvailable(mContext)) {
                    if(ConstantsUtils.isPinging()) {
                        String qry = Constants.CustomerCreditLimits + "?$select=BalanceAmount,CreditControlAreaID,Currency&$filter=" + Constants.Customer + "%20eq%20'" + customerNo + "'%20and%20(" + creditControlAreas + ")";
                        try {
                            OnlineManager.doOnlineGetRequest(qry, mContext, iReceiveEvent -> {
                                if (iReceiveEvent.getResponseStatusCode()==200){
                                    JSONObject jsonObject = OnlineManager.getJSONBody(iReceiveEvent);
                                    JSONArray jsonArray = OnlineManager.getJSONArrayBody(jsonObject);
                                    try {
                                        limitBeanList = OfflineManager.getCreditLimitOnline(jsonArray);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (limitBeanList!=null && !limitBeanList.isEmpty()) {
//                    getCreditLimitValue();
                                        getCreditLimitValue("");
                                    }else{
                                        LogManager.writeLogError("Customer Credit Limit Throws empty feed entity size is null or zero");
                                        ((Activity)mContext).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (shipToView != null) {
                                                    shipToView.hideProgressDialog();
                                                    shipToView.openReviewScreen(new ArrayList<CreditLimitBean>());
                                                }
                                            }
                                        });

                                    }

                                }else {
                                    String responseBody="";
                                    try {
                                        responseBody = IReceiveEvent.Util.getResponseBody(iReceiveEvent.getReader());
                                    } catch (IOException e) {
                                        responseBody=e.toString();
                                        e.printStackTrace();
                                    }
                                    String finalResponseBody = responseBody;
                                    ((Activity)mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (shipToView != null) {
                                                shipToView.hideProgressDialog();
                                                shipToView.displayMessage(finalResponseBody);
//                                                    shipToView.openReviewScreen(new ArrayList<CreditLimitBean>());
                                                shipToView.showProgressDialog(finalResponseBody);
                                            }
                                        }
                                    });
                                }
                            }, e -> {
                                e.printStackTrace();
                                String errormessage = "";
                                errormessage = ConstantsUtils.geterrormessageForInternetlost(e.getMessage(),mContext);
                                if(TextUtils.isEmpty(errormessage)){
                                    errormessage = e.getMessage();
                                }
                                String finalErrormessage = errormessage;
                                ((Activity)mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (shipToView != null) {
                                            shipToView.hideProgressDialog();
                                            shipToView.displayMessage(finalErrormessage);
//                                                shipToView.openReviewScreen(new ArrayList<CreditLimitBean>());
                                        }
                                    }
                                });

                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            ConstantsUtils.printErrorLog(e.getMessage());
                            ((Activity)mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (shipToView != null) {
                                        shipToView.hideProgressDialog();
                                        shipToView.displayMessage(e.getMessage());
//                                            shipToView.openReviewScreen(new ArrayList<CreditLimitBean>());
                                    }
                                }
                            });

                        }


                           /* Bundle bundle = new Bundle();
//                    bundle.putString(Constants.BUNDLE_RESOURCE_PATH, Constants.CustomerCreditLimits + "?$select=BalanceAmount,Currency&$filter=" + Constants.Customer + " eq '" + selectedSOItem.getCustomerNo() + "'");
                            bundle.putString(Constants.BUNDLE_RESOURCE_PATH, Constants.CustomerCreditLimits + "?$select=BalanceAmount,CreditControlAreaID,Currency&$filter=" + Constants.Customer + " eq '" + customerNo + "' and (" + creditControlAreas + ")");
                            bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 4);
                            bundle.putInt(Constants.BUNDLE_OPERATION, Operation.GetRequest.getValue());
                            bundle.putBoolean(Constants.BUNDLE_SESSION_REQUIRED, true);
                            bundle.putBoolean(Constants.BUNDLE_SESSION_URL_REQUIRED, true);
                            try {
                                OnlineManager.requestOnline(this, bundle, mContext);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }*/
                    }else {
                        alAssignColl.add(Constants.CustomerCreditLimits);
                        String syncColl = TextUtils.join(", ", alAssignColl);
                        OfflineManager.refreshStoreSync(mContext, this, Constants.Fresh, syncColl);
                    }
                }else {
                    alAssignColl.add(Constants.CustomerCreditLimits);
                    String syncColl = TextUtils.join(", ", alAssignColl);
                    OfflineManager.refreshStoreSync(mContext, this, Constants.Fresh, syncColl);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//            }else {
//                alAssignColl.add(Constants.CustomerCreditLimits);
//                String syncColl = TextUtils.join(", ", alAssignColl);
//                OfflineManager.refreshStoreSync(mContext, this, Constants.Fresh, syncColl);
//            }
        } catch (final Exception e) {
            e.printStackTrace();
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (shipToView != null) {
                        shipToView.hideProgressDialog();
                    }
                }
            });
        }
    }

    @Override
    public void onRequestError(int i, final Exception e) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (shipToView != null) {
                    shipToView.hideProgressDialog();
                }
            }
        });
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        if (!Constants.isStoreClosed) {
            if (i == Operation.OfflineRefresh.getValue()) {
                try {
                    /*String syncTime = Constants.getSyncHistoryddmmyyyyTime();
                    for (int incReq = 0; incReq < alAssignColl.size(); incReq++) {
                        String colName = alAssignColl.get(incReq);
                        if (colName.contains("?$")) {
                            String splitCollName[] = colName.split("\\?");
                            colName = splitCollName[0];
                        }
                        Constants.events.updateStatus(Constants.SYNC_TABLE,
                                colName, Constants.TimeStamp, syncTime
                        );
                    }*/
                    //   Constants.updateSyncTime(alAssignColl,mContext,Constants.DownLoad);
                } catch (Exception exce) {
                    LogManager.writeLogError(Constants.SyncTableHistory + exce.getMessage());
                }
                getCreditLimitValue(soListBeanHeader.getCustomerNo());

                /*Constants.isSync = false;
                if (!Constants.isStoreClosed) {
                    if (shipToView != null) {
                        shipToView.hideProgressDialog();
                    }
                    onStart();
                } else {
                    if (shipToView != null) {
                        shipToView.hideProgressDialog();
                       // views.showMsg(mContext.getString(R.string.msg_sync_terminated));
                    }
                }*/
            }

        }
    }
    List<CreditLimitBean> limitBeanList = new ArrayList<>();
    private void getCreditLimitValue(String mStrBundleRetID) {
//        String qry = Constants.CustomerCreditLimits + "?$filter=" +
//                Constants.Customer + " eq '" + customerNo + "'";
//        if(!Constants.getRollID(mContext)) {

        if (!UtilConstants.isNetworkAvailable(mContext)) {
            if(!ConstantsUtils.isPinging()) {
                String qry = Constants.CustomerCreditLimits + "?$filter=" +
                        Constants.Customer + " eq '" + customerNo + "' and " + Constants.CreditControlAreaID + " eq '" + soListBeanHeader.getCreditControlAreaID() + "'";
                if(Constants.writeDebug){
                    LogManager.writeLogDebug("SO Create : Requesting Credit Limits : URL : "+qry);
                }
                limitBeanList = OfflineManager.getCreditLimit(qry);
            }
        }
//        }
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (shipToView != null) {
                    shipToView.hideProgressDialog();
                    if (UtilConstants.isNetworkAvailable(mContext)) {
                        if (!limitBeanList.isEmpty())
                            shipToView.openReviewScreen(limitBeanList);
                        else
                            shipToView.openReviewScreen(new ArrayList<CreditLimitBean>());
                    } else {
//                        ArrayList<SOItemBean> soItemList = new ArrayList<>();
//                        soListBeanHeader.setSoItemBeanArrayList(soItemList);
                        if (!limitBeanList.isEmpty())
                            shipToView.openReviewScreen(new ArrayList<CreditLimitBean>());
                        else
                            shipToView.openReviewScreen(new ArrayList<CreditLimitBean>());
                    }
                }
            }
        });
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> entities, int operation, int requestCode, String resourcePath, Bundle bundle) {
        Log.d("responseSuccess","773");
        switch (requestCode) {
            case 1:
                startSimulate(soListBeanHeaderSimulation);
                break;
            case 4:
                try {
                    limitBeanList = OfflineManager.getCreditLimitOnline(entities);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (limitBeanList!=null && !limitBeanList.isEmpty()) {
//                    getCreditLimitValue();
                    getCreditLimitValue("");
                }else{
                    LogManager.writeLogError("Customer Credit Limit Throws empty feed entity size is null or zero");
                    if (shipToView != null) {
                        shipToView.hideProgressDialog();
                        shipToView.openReviewScreen(new ArrayList<CreditLimitBean>());
                    }
                }
                break;
        }
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, int operation, int requestCode, String resourcePath, String errorMsg, Bundle bundle) {
        showErrorResponse(errorMsg);
    }

   /* public void startService(Context mContext) {
        isFinish = false;
        OpenStoreIntentService.startServices(mContext);
    }

    public void registerBroadCastReceiver(BroadcastReceiver broadCastReceiver) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(OpenStoreIntentService.ACTION_SERVICE_KEY);
        this.openReceiver = broadCastReceiver;
        mContext.registerReceiver(openReceiver, intentFilter);
    }

    public void unRegisterReceiver() {
        try {
            if (openReceiver != null) {
                mContext.unregisterReceiver(openReceiver);
                openReceiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
