package com.rspl.sf.msfa.socreate.stepOne;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.AsyncTaskCallBack;
import com.rspl.sf.msfa.so.ValueHelpBean;
import com.rspl.sf.msfa.soapproval.OpenOnlineManagerStore;
import com.rspl.sf.msfa.socreate.DefaultValueBean;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.solist.SOListBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.store.OnlineManager;
import com.rspl.sf.msfa.store.OnlineStoreListener;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10769 on 29-06-2017.
 */

public class SOCreatePresenterImp implements SOCreatePresenter, OnlineODataInterface {
    private Context mContext;
    private SOCreateView createView;
    private int comingFrom;
    private String customerNo = "";
    private boolean isSessionRequired = false;
    private int totalRequest = 0;
    private int currentRequest = 0;
    private ArrayList<DefaultValueBean> customerSalesAreaArrayList = new ArrayList<>();
    private ArrayList<ValueHelpBean> orderType = new ArrayList<>();
    private ArrayList<ValueHelpBean> salesGrp = new ArrayList<>();
    private ArrayList<ValueHelpBean> salesOfficeList = new ArrayList<>();

    private ArrayList<ValueHelpBean> paymentTermList = new ArrayList<>();
    private ArrayList<ValueHelpBean> incoterm1List = new ArrayList<>();
    private ArrayList<ValueHelpBean> plantList = new ArrayList<>();
    private ArrayList<SOItemBean> soItemBeanArrayList = new ArrayList<>();

    public SOCreatePresenterImp(Context mContext, SOCreateView createView, int comingFrom, String customerNo, boolean isSessionRequired) {
        this.mContext = mContext;
        this.createView = createView;
        this.comingFrom = comingFrom;
        this.customerNo = customerNo;
        this.isSessionRequired = isSessionRequired;
    }

    @Override
    public void onStart() {
//        soCreateModel.findItems(mContext, this, comingFrom, customerNo);
        requestCustomers();
    }

    @Override
    public void onDestroy() {

    }


    /*@Override
    public void onBsdOnSaleArea(ArrayList<CustomerPartnerFunctionBean> shipToList, ArrayList<ValueHelpBean> incoterm1List, ArrayList<ValueHelpBean> paymentTermList) {
        if (createView!=null){
            createView.displayBySalesArea(shipToList,incoterm1List,paymentTermList);
        }
    }

    @Override
    public void onBsdOnPlant(ArrayList<PaymentTermBean> paymentTermList) {

    }

    @Override
    public void onFinished(String[][] arrCustomers) {
        if (createView!=null){
            createView.displaySoldToParty(arrCustomers);
        }
    }

    @Override
    public void onBasedOnCustomer(ArrayList<DefaultValueBean> customerSalesAreaArrayList, ArrayList<ValueHelpBean> OrdtypeList, ArrayList<ValueHelpBean> shippingConditionList) {
        if (createView!=null){
            createView.displayByCustomer(customerSalesAreaArrayList,OrdtypeList, shippingConditionList);
        }
    }

    @Override
    public void onUnloading(ArrayList<UnlRecBean> unloadingList) {
        if (createView!=null){
            createView.displayUnloadingPt(unloadingList);
        }
    }

    @Override
    public void onReceiving(ArrayList<UnlRecBean> receivingList) {
        if (createView!=null){
            createView.displayReceivingPt(receivingList);
        }
    }

    @Override
    public void onBsdOnOrderNo(ArrayList<ValueHelpBean> plantList) {
        if (createView!=null){
            createView.displayByOrderNo(plantList);
        }
    }*/

    @Override
    public void getBasedOnCustomer(String customerNo) {
//        soCreateModel.findItemsBasedOnCustomer(mContext, this, comingFrom, customerNo);
        requestCustomerBased(customerNo);
    }

    @Override
    public void getBasedOnBsdOnSaleArea(String customerNo, String salesArea) {
//        soCreateModel.fndItmBsdOnSaleArea(mContext, this, comingFrom, salesArea, customerNo);
        requestSalesAreaBased(customerNo, salesArea);
    }

    @Override
    public void getBasedOnBsdOnOrderNo(String customerNo, String salesArea, String orderNo) {
//        soCreateModel.fndItmBsdOnOrderNo(mContext,this,comingFrom,salesArea,customerNo,orderNo);
    }

    @Override
    public void getUnloading(String customerNo, String salesArea, String orderNo, String plant, String mStrIncoTermId, String mStrShippingConditionId) {
//        soCreateModel.fndItemUnloading(mContext,this,comingFrom,orderNo,salesArea,plant,customerNo,mStrIncoTermId,mStrShippingConditionId);
    }

    @Override
    public void getReceiving(String customerNo, String salesArea, String orderNo, String plant, String mStrIncoTermId, String mStrShippingConditionId, String unloadingPoint) {
//        soCreateModel.fndItemReceiving(mContext,this,comingFrom,orderNo,salesArea,plant,customerNo,mStrIncoTermId,mStrShippingConditionId,unloadingPoint);
    }

    @Override
    public void getOnPlant(String customerNo, String salesArea, String orderNo, String plantId) {
//        soCreateModel.fndItmBsdOnPlant(mContext, this, comingFrom, orderNo, salesArea, plantId, customerNo);
    }

    @Override
    public boolean validateHeader(SOListBean soListBean) {
        boolean isNotError = true;
        if (createView != null) {
            if (TextUtils.isEmpty(soListBean.getSoldTo())) {
                createView.errorSoldTo();
                isNotError = false;
            }
           /* if (TextUtils.isEmpty(soListBean.getSalesArea())) {
                createView.errorSalesArea();
                isNotError = false;
            }
            if (TextUtils.isEmpty(soListBean.getOrderType())) {
                createView.errorOrderType();
                isNotError = false;
            }*/

           /* if (comingFrom != ConstantsUtils.SO_EDIT_ACTIVITY) {
                if (TextUtils.isEmpty(soListBean.getPlant())) {
                    createView.errorPlant();
                    isNotError = false;
                }
            }*/
           /* if (TextUtils.isEmpty(soListBean.getShipTo())) {
                createView.errorShipToParty();
                isNotError = false;
            }
            if (TextUtils.isEmpty(soListBean.getPaymentTerm())) {
                createView.errorPaymentTerm();
                isNotError = false;
            }
            if (TextUtils.isEmpty(soListBean.getIncoTerm1())) {
                createView.errorIncoTerm();
                isNotError = false;
            }

            if (TextUtils.isEmpty(soListBean.getShippingPoint())) {
                createView.errorShippingCondition();
                isNotError = false;
            }*/
           /* if (soItemBeanArrayList.isEmpty()) {
                errorType=1;
            } else {
                soListBean.setSoItemBeanArrayList(soItemBeanArrayList);
            }*/
           /* if (TextUtils.isEmpty(soListBean.getUnloadingPointId())) {
                createView.errorUnloading();
                isNotError = false;
            }
            if (TextUtils.isEmpty(soListBean.getReceivingPointId())) {
                createView.errorReceiving();
                isNotError = false;
            }*/

//            if (!isNotError) {
//                createView.displayMessage(mContext.getString(R.string.validation_plz_enter_mandatory_flds, UtilConstants.ERROR_CODE_UI_2000));
//            }
            /*else if (errorType>0){
                isNotError = false;
                createView.displayMessage(mContext.getString(R.string.validation_material_empty));
            }*/

        }
        return isNotError;
    }

    @Override
    public void getMaterial(String customerNo, String plant) {
        /*if (!TextUtils.isEmpty(plant) && !TextUtils.isEmpty(customerNo)) {
            if (createView != null) {
                createView.showProgressDialog(mContext.getString(R.string.app_loading));
            }
            totalRequest = 1;
            currentRequest = 0;
            String qry = Constants.MaterialByCustomers + "/?$filter=PlantID+eq+'" + plant + "'+and+CustomerNo+eq+'" + customerNo + "'";
            ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 5, ConstantsUtils.SESSION_HEADER, this);
        }*/
    }

    private void requestCustomers() {
        if (createView != null) {
            createView.showProgressDialog(mContext.getString(R.string.app_loading));
        }
        String qry = Constants.UserCustomers + "?$filter=" + Constants.CustomerNo + " eq '" + customerNo + "'";
        totalRequest = 1;
        currentRequest = 0;
        ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 1, ConstantsUtils.SESSION_HEADER, this, false);

    }

    private void requestCustomerBased(String customerNo) {//1000/01/01,1000/02/01,1000/01/10,1000/02/10
        if (createView != null) {
            createView.showProgressDialog(mContext.getString(R.string.app_loading));
        }

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
            for (int i = 0; i < salesArearlist.size(); i++) {
                if (i == salesArearlist.size() - 1) {
                    stringSaleArea = stringSaleArea + "SalesArea eq '" + salesArearlist.get(i) + "'";
                } else {
                    stringSaleArea = stringSaleArea + "SalesArea eq '" + salesArearlist.get(i) + "' or ";
                }
            }
        }
//        String loginId = OfflineManager.getLoginID("UserProfileAuthSet?$filter=Application%20eq%20%27PD%27");
//        String qry = Constants.CustomerSalesAreas + "/?$filter=" + Constants.CustomerNo + "+eq+'" + customerNo + "'";
        String qry = "";
        if (!TextUtils.isEmpty(stringSaleArea)) {
            qry = Constants.CustomerSalesAreas + "?$filter=" + Constants.CustomerNo + " eq '" + customerNo + "' and (" + stringSaleArea + ")";
        } else {
            if (createView != null) {
                createView.hideProgressDialog();
            }
        }
//        String qry = Constants.CustomerPartnerFunctions + "?$filter=" + Constants.CustomerNo + " eq '" + customerNo + "' and PartnerVendorNo eq '"+loginId+"'";
        totalRequest = 2;
        currentRequest = 0;
        if (!TextUtils.isEmpty(qry)) {
            if (Constants.getRollID(mContext)) {
                try {

                    ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 2, ConstantsUtils.SESSION_HEADER, this, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 2, ConstantsUtils.SESSION_HEADER, this, false);
            }

            System.out.println("CustomerSalesAreas 7"+qry);
        } else {
            if (createView != null) {
                createView.hideProgressDialog();
            }
        }
//        qry = Constants.ValueHelps + "/?$filter=" + Constants.VHELP_MODELID_ENTITY_TYPE + "+and+" + Constants.PropName + "+eq+'" + Constants.OrderType + "'";
        qry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" +
                Constants.OrderType + "' &$orderby = Description asc";
        ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 6, ConstantsUtils.SESSION_HEADER, this, false);
    }

    private void openOnlineStore(final String qry) {
        //optional store open
        if (UtilConstants.isNetworkAvailable(mContext)) {
            new OpenOnlineManagerStore(mContext, new AsyncTaskCallBack() {
                @Override
                public void onStatus(boolean status, String values) {
                    if (status) {
                        ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 2, ConstantsUtils.SESSION_QRY, SOCreatePresenterImp.this, true);
                    }
                }
            }).execute();
        }
    }

    private void requestSalesAreaBased(String customerNo, String salesArea) {
        if (createView != null) {
            createView.showProgressDialog(mContext.getString(R.string.app_loading));
        }
        totalRequest = 1;
        currentRequest = 0;
        String qry = Constants.ValueHelps + " ?$filter=" + Constants.VHELP_MODELID_ENTITY_TYPE + " and (PropName eq 'Plant' or PropName eq 'SalesOffice') and ParentID eq '" + salesArea + "' and PartnerNo eq '" + customerNo + "'";
        ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 4, ConstantsUtils.SESSION_HEADER, this, false);
//        qry = Constants.ValueHelps + "/?$filter=" + Constants.VHELP_MODELID_ENTITY_TYPE + "+and+" + Constants.PropName + "+eq+'" + Constants.SalesGroup + "'+and+ParentID+eq+'" + ConstantsUtils.getPerticularName(salesArea,0) + "'+and+PartnerNo+eq+'" + customerNo + "'";
//        ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 7, ConstantsUtils.SESSION_HEADER, this);
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> entities, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        switch (type) {
            case 1:
                String[][] arrCustomers = null;
                try {
                    arrCustomers = OnlineManager.getUserCustomersArray(entities);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                final String[][] finalArrCustomers = arrCustomers;
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (createView != null) {
                            createView.hideProgressDialog();
                            createView.displaySoldToParty(finalArrCustomers);
                        }
                    }
                });
                break;
            case 2:
                try {
                    customerSalesAreaArrayList.clear();
                    customerSalesAreaArrayList.addAll(OnlineManager.getConfigListWithDefaultValAndNone(entities));
//                    customerSalesAreaArrayList.addAll(OnlineManager.getCustomerPartnerWithDefaultValAndNone(entities));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
               /* ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (createView != null) {
                            createView.hideProgressDialog();

                        }
                    }
                });*/
                currentRequest++;
                break;
            /*  case 3:
             *//* shipToList.clear();
                try {
                    shipToList.addAll(OnlineManager.getCustomerPartnerDataFunction(entities));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }*//*
                currentRequest++;
                break;*/
            case 4:
               /* paymentTermList.clear();
                try {
                    paymentTermList.addAll(OnlineManager.getConfigListFromValueHelp(entities, Constants.Payterm));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                incoterm1List.clear();
                try {
                    incoterm1List.addAll(OnlineManager.getConfigListFromValueHelp(entities, Constants.Incoterm1));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }*/
                plantList.clear();
                try {
                    plantList.addAll(OnlineManager.getConfigListFromValueHelp(entities, Constants.Plant));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                salesOfficeList.clear();
                try {
                    salesOfficeList.addAll(OnlineManager.getConfigListFromValueHelp(entities, Constants.SalesOffice));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                currentRequest++;

                break;
            case 5:
               /* try {
                    soItemBeanArrayList.addAll(OnlineManager.getSOMaterialList(entities));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (createView != null) {
                            createView.hideProgressDialog();
                        }
                    }
                });*/
                break;
            case 6:
                orderType.clear();
                try {
                    orderType.addAll(OnlineManager.getConfigListFromValueHelp(entities, Constants.OrderType));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                currentRequest++;
                break;
            case 7:
                salesGrp.clear();
                try {
                    salesGrp.addAll(OnlineManager.getConfigListFromValueHelp(entities, Constants.SalesGroup));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                currentRequest++;
                break;
        }
        if (currentRequest == totalRequest) {
            Log.e("SOCreatePresenterImpl", "currentRequest :" + currentRequest + ", totalRequest :" + totalRequest);
            if (type == 4 || type == 3 || type == 7) {
               /* shippingConditionList.clear();
                ValueHelpBean shippingCondition = new ValueHelpBean();
                shippingCondition.setID("");
                shippingCondition.setDescription(Constants.None);
                shippingCondition.setDisplayData(Constants.None);
                shippingConditionList.add(shippingCondition);
                shippingCondition = new ValueHelpBean();
                shippingCondition.setID("TR");
                shippingCondition.setDescription("By Road");
                shippingCondition.setDisplayData(shippingCondition.getID() + " - " + shippingCondition.getDescription());
                shippingConditionList.add(shippingCondition);
                shippingCondition = new ValueHelpBean();
                shippingCondition.setID("WG");
                shippingCondition.setDescription("By Rail");
                shippingCondition.setDisplayData(shippingCondition.getID() + " - " + shippingCondition.getDescription());
                shippingConditionList.add(shippingCondition);
                shippingCondition = new ValueHelpBean();
                shippingCondition.setID("SH");
                shippingCondition.setDescription("By Ship");
                shippingCondition.setDisplayData(shippingCondition.getID() + " - " + shippingCondition.getDescription());
                shippingConditionList.add(shippingCondition);*/
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (createView != null) {
                            createView.hideProgressDialog();
                            createView.displayBySalesArea(salesOfficeList, plantList, salesGrp);
                        }
                    }
                });
            } else if (type == 2 || type == 6) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (createView != null) {
                            createView.hideProgressDialog();
                            createView.displayByCustomer(customerSalesAreaArrayList, orderType);
                        }
                    }
                });
            }
        }

    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, String errorMsg, Bundle bundle) {
        currentRequest++;
        if (totalRequest == currentRequest) {
            showErrorResponse(errorMsg);
        }
    }

    private void showErrorResponse(final String errorMsg) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (createView != null) {
                    createView.hideProgressDialog();
                    createView.displayMessage(errorMsg);
                }
            }
        });
    }
}
