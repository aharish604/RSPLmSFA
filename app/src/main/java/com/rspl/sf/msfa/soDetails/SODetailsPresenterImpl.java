package com.rspl.sf.msfa.soDetails;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.OnlineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.AsyncTaskCallBackInterface;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;
import com.arteriatech.mutils.security.PermissionUtils;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.google.gson.Gson;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.asyncTask.RefreshAsyncTask;
import com.rspl.sf.msfa.asyncTask.SessionIDAsyncTask;
import com.rspl.sf.msfa.asyncTask.SyncFromDataValtAsyncTask;
import com.rspl.sf.msfa.collectionPlan.collectionCreate.SaleAreaBean;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.DialogCallBack;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.mbo.SalesOrderBean;
import com.rspl.sf.msfa.reports.salesorder.header.SalesOrderHeaderListActivity;
import com.rspl.sf.msfa.reports.salesorder.pendingsync.UploadSOListService;
import com.rspl.sf.msfa.so.SOUtils;
import com.rspl.sf.msfa.socreate.ConfigTypeValues;
import com.rspl.sf.msfa.socreate.CreditLimitBean;
import com.rspl.sf.msfa.socreate.SOConditionItemDetaiBean;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.socreate.SOSimulateAsyncTask;
import com.rspl.sf.msfa.socreate.SOSubItemBean;
import com.rspl.sf.msfa.solist.SOListBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.store.OnlineManager;
import com.rspl.sf.msfa.sync.FlushDataAsyncTask;
import com.rspl.sf.msfa.sync.SyncSelectionActivity;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.httpc.events.IReceiveEvent;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.rspl.sf.msfa.socreate.shipToDetails.ShipToPresenterImpl.getJSONBody;

/**
 * Created by e10769 on 03-07-2017.
 */

public class SODetailsPresenterImpl implements SODetailsPresenter, SODetailsModel.OnFinishedListener, OnlineODataInterface, UIListener {
    String doc_no = "";
    private Activity mContext;
    private SODetailsView soDetailsView;
    private SODetailsModel soDetailsModel;
    private int comingFrom;
    private SOListBean soListBean;
    private Hashtable<String, String> masterHeaderTable = new Hashtable<>();
    private ArrayList<HashMap<String, String>> itemTable = new ArrayList<>();
    private ArrayList<HashMap<String, String>> subItemTable = new ArrayList<>();
    private ArrayList<HashMap<String, String>> conditionItemTable = new ArrayList<>();
    private boolean isSessionRequired = false;
    private boolean isErrorSuccessCalled = false;
    private Set<String> set = new HashSet<>();
    private ODataDuration mStartTimeDuration;
    private int isFromWhere=0;
    private GUID refguid =null;
    private List<String> alAssignColl = new ArrayList<>();
    List<CreditLimitBean> limitBeanList = new ArrayList<>();
    private String customerNo;

    SOListBean soListBeanHeader=new SOListBean();

    public SODetailsPresenterImpl(Activity mContext,String customerNo, SODetailsView soDetailsView, SODetailsModel soDetailsModel, int comingFrom, SOListBean soListBean, boolean isSessionRequired) {
        this.mContext = mContext;
        this.soDetailsView = soDetailsView;
        this.soDetailsModel = soDetailsModel;
        this.comingFrom = comingFrom;
        this.soListBean = soListBean;
        this.customerNo=customerNo;
        this.isSessionRequired = isSessionRequired;
        this.mStartTimeDuration = UtilConstants.getOdataDuration();
    }

    @Override
    public void onStart() {
        if (soDetailsView != null) {

        }
        onFinished(soListBean);
//        soDetailsModel.findItems(mContext,this, comingFrom, soListBean);
    }

    @Override
    public void onDestroy() {
        soDetailsView = null;
    }

    @Override
    public void onSaveData() {
        if (soListBean.isVisitActivity())
            getLocation();
        else
            finalSaveCondition();
    }

    private void getLocation() {
        if (soDetailsView != null) {
            soDetailsView.showProgressDialog(mContext.getString(R.string.checking_pemission));
            LocationUtils.checkLocationPermission(mContext, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                    if (soDetailsView != null) {
                        soDetailsView.hideProgressDialog();
                    }
                    if (status) {
                        locationPerGranted();
                    }
                }
            });
        }
    }

    private void finalSaveCondition() {
//        if (UtilConstants.isNetworkAvailable(mContext)) {
        Bundle bundle = new Bundle();
        if (comingFrom == ConstantsUtils.SO_CREATE_ACTIVITY || comingFrom == ConstantsUtils.SO_CREATE_SINGLE_MATERIAL) {
            if (soDetailsView != null) {
                soDetailsView.showProgressDialog(mContext.getString(R.string.saving_data_wait));
            }
            bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 1);

//                new SOPostToServerAsyncTask(mContext, this, masterHeaderTable, itemTable, 1, bundle).execute();
        } /*else if (comingFrom == 4) {
                progressDialog = ConstantsUtils.showProgressDialog(SOSimulateReviewActivity.this, getString(R.string.changing_data_wait));
                new DirecySyncAsyncTask(SOSimulateReviewActivity.this, SOSimulateReviewActivity.this, SOSimulateReviewActivity.this, masterHeaderTable, itemTable, 3).execute();
            } else if (comingFrom == 5) {
                progressDialog = ConstantsUtils.showProgressDialog(SOSimulateReviewActivity.this, getString(R.string.updating_data_wit));
                new DirecySyncAsyncTask(SOSimulateReviewActivity.this, SOSimulateReviewActivity.this, SOSimulateReviewActivity.this, masterHeaderTable, itemTable, 3).execute();
            } else if (comingFrom == 2) {
                progressDialog = ConstantsUtils.showProgressDialog(SOSimulateReviewActivity.this, getString(R.string.canceling_data_wait));
                new DirecySyncAsyncTask(SOSimulateReviewActivity.this, SOSimulateReviewActivity.this, SOSimulateReviewActivity.this, masterHeaderTable, itemTable, 5).execute();
            }*/
        onSave();
        /*} else {
            if (soDetailsView != null) {
                soDetailsView.showMessage(mContext.getString(R.string.no_network_conn), true);
            }
        }*/
    }

    private ArrayList<SalesOrderBean> salesOrderHeaderArrayList = new ArrayList<>();
    private ArrayList<String> pendingCollectionList = new ArrayList<>();
    private ArrayList<String> alFlushColl = new ArrayList<>();
    private int penROReqCount = 0;
    private int pendingROVal = 0;
    private String[] tempRODevList = null;
    private void onSave() {
        doc_no = masterHeaderTable.get(Constants.SONo);
        GUID ssoHeaderGuid = GUID.newRandom();

//        masterHeaderTable.put(Constants.SONo, doc_no);
//        masterHeaderTable.put(Constants.SONo, doc_no);
        masterHeaderTable.put(Constants.ReferenceNo, ssoHeaderGuid.toString32().toUpperCase());
        masterHeaderTable.put(Constants.entityType, Constants.SalesOrderDataValt);
        masterHeaderTable.put(Constants.TLSD, soListBean.getSoItemBeanArrayList().size() + "");
        masterHeaderTable.put(Constants.CreatedOn, UtilConstants.getNewDateTimeFormat());
        masterHeaderTable.put(Constants.CreatedAt, UtilConstants.getOdataDuration().toString());
        Gson gson1 = new Gson();
        String jsonFromMap = "";
        try {
            jsonFromMap = gson1.toJson(itemTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        masterHeaderTable.put(Constants.SalesOrderItems, jsonFromMap);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        set = sharedPreferences.getStringSet(Constants.SalesOrderDataValt, null);

        HashSet<String> setTemp = new HashSet<>();
        if (set != null && !set.isEmpty()) {
            Iterator<String> itr = set.iterator();
            while (itr.hasNext()) {
                setTemp.add(itr.next().toString());
            }
        }
        setTemp.add(doc_no);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(Constants.SalesOrderDataValt, setTemp);
        editor.apply();

        JSONObject jsonHeaderObject = new JSONObject(masterHeaderTable);



        try {
                    ConstantsUtils.storeInDataVault(doc_no, jsonHeaderObject.toString(),mContext);

        } catch (Throwable e) {
            LogManager.writeLogError("SODetailsPresenterImpl Throwable onSave : "+e.toString());
            e.printStackTrace();
        }
        if (soListBean.isVisitActivity()) {
            Constants.onVisitActivityUpdate(mContext, soListBean.getmStrCPGUID32(), ssoHeaderGuid.toString36().toUpperCase(), "03", Constants.PrimarySOCreate, mStartTimeDuration);
//        startSyncSerrvice();
        }
        if(UtilConstants.isNetworkAvailable(mContext)){
                LogManager.writeLogDebug("Create SO: Online Sync Starts");
                isFromWhere = 4;
                pendingCollectionList.clear();
                pendingCollectionList.add(Constants.SOs);
                pendingCollectionList.add(Constants.SOItemDetails);
                pendingCollectionList.add(Constants.SOTexts);
                pendingCollectionList.add(Constants.SOItems);
                pendingCollectionList.add(Constants.SOConditions);
                pendingCollectionList.add(Constants.ConfigTypsetTypeValues);
                pendingROVal = 0;
                if (tempRODevList != null) {
                    tempRODevList = null;
                    penROReqCount = 0;
                }
                alFlushColl.clear();
                ArrayList<String> allAssignColl = SyncSelectionActivity.getRefreshList(mContext);
                if (!allAssignColl.isEmpty()) {
//                alAssignColl.addAll(allAssignColl);
                    pendingCollectionList.addAll(allAssignColl);
                    alFlushColl.addAll(allAssignColl);
                }

                try {
                    salesOrderHeaderArrayList = (ArrayList<SalesOrderBean>) OfflineManager.getSoListPendingFromDataValt(mContext, soListBean.getmStrCPGUID32());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                if (salesOrderHeaderArrayList != null && salesOrderHeaderArrayList.size() > 0) {
                    tempRODevList = new String[salesOrderHeaderArrayList.size()];

                    for (com.rspl.sf.msfa.mbo.SalesOrderBean SalesOrderBean : salesOrderHeaderArrayList) {
                        tempRODevList[pendingROVal] = SalesOrderBean.getDeviceNo();
                        pendingROVal++;
                    }
                    if (Constants.iSAutoSync || Constants.isLocationSync) {
                        if (Constants.iSAutoSync) {
                            LogManager.writeLogError(mContext.getString(R.string.alert_auto_sync_is_progress));
                        } else if (Constants.isLocationSync) {
                            LogManager.writeLogError(mContext.getString(R.string.alert_auto_sync_location_is_progress));
                        }
                    } else {
                        Constants.isSync = true;
                        Constants.isBackGroundSync = true;
                        refguid = GUID.newRandom();
                        Constants.updateStartSyncTime(mContext, Constants.SOPOSTBG_sync, Constants.StartSync, refguid.toString().toUpperCase());
                        new SyncFromDataValtAsyncTask(mContext, tempRODevList, this, new DialogCallBack() {
                            @Override
                            public void clickedStatus(boolean clickedStatus) {

                            }
                        }).execute();
                    }
                }
            }else {
                LogManager.writeLogError("SODetailsPresenterImpl onSave : Logon Core null not able post So Create");
            }

            navigateToDetails();

    }
    private boolean isErrorFromBackend = false;
    private String concatCollectionStr = "";
    private boolean dialogCancelled = false;

    private void startSyncSerrvice() {
        if (UtilConstants.isNetworkAvailable(mContext)) {
            Intent intent = new Intent(mContext, UploadSOListService.class);
            mContext.startService(intent);
        }
    }

    private void navigateToDetails() {
        if (soDetailsView != null) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    soDetailsView.hideProgressDialog();
                    soDetailsView.showMessage(mContext.getString(R.string.msg_secondary_so_created), false);
                }
            });
        }
    }

    @Override
    public void onUpdate() {
        if (soDetailsView != null) {
            soDetailsView.showProgressDialog(mContext.getString(R.string.updating_data_wit));
        }
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 5);
        new SOPostToServerAsyncTask(mContext, this, masterHeaderTable, itemTable, 3, bundle).execute();
    }

    @Override
    public void onAsignData(final String save, final String strRejReason, final String strRejReasonDesc) {
        assignDataVar(save, strRejReason, strRejReasonDesc);

    }

    private void locationPerGranted() {
        if (soDetailsView != null) {
            soDetailsView.showProgressDialog(mContext.getString(R.string.checking_pemission));
            Constants.getLocation(mContext, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                    if (soDetailsView != null) {
                        soDetailsView.hideProgressDialog();
                    }
                    if (status) {
                        if (ConstantsUtils.isAutomaticTimeZone(mContext)) {
                            finalSaveCondition();
                        } else {
                            if (soDetailsView != null)
                                ConstantsUtils.showAutoDateSetDialog(mContext);
                        }
                    }
                }
            });
        }
    }

    private void assignDataVar(String save, String strRejReason, String strRejReasonDesc) {
        String doc_no = "";
        masterHeaderTable.clear();
        String orderDate = Constants.getNewDateTimeFormat();
        if (save.equals(mContext.getString(R.string.submit_so))) {
            if(Constants.writeDebug) {
                LogManager.writeLogDebug("Create Sales Order Starts: Save");
            }
            doc_no = (System.currentTimeMillis() + "").substring(3, 10);
        } else if (save.equals(mContext.getString(R.string.cancel_so))) {
            doc_no = soListBean.getSONo();
        } else if (save.equals(mContext.getString(R.string.edit_so))) {
            if(Constants.writeDebug) {
                LogManager.writeLogDebug("Edit Sales Order Starts: Edit");
            }
            doc_no = soListBean.getSONo();
            masterHeaderTable.put(Constants.InstanceID, soListBean.getInstanceID());
            masterHeaderTable.put(Constants.EntityKey, doc_no);
            String dates = soListBean.getOrderDate();
            if (!TextUtils.isEmpty(dates)) {
                Calendar calendar = ConstantsUtils.convertCalenderToDisplayDateFormat(dates, ConstantsUtils.getDisplayDateFormat(mContext));
                orderDate = ConstantsUtils.convertCalenderToDisplayDateFormat(calendar, "yyyy-MM-dd'T'HH:mm:ss");
//                orderDate = dates + "T00:00:00";
            }
        }

        String poNo = "";
        if (!TextUtils.isEmpty(soListBean.getPONo())) {
            poNo = soListBean.getPONo();
        }
        String podate = "";//Constants.getNewDateTimeFormat();
        if (!TextUtils.isEmpty(soListBean.getPODate())) {
            Calendar calendar = ConstantsUtils.convertCalenderToDisplayDateFormat(soListBean.getPODate(), ConstantsUtils.getDisplayDateFormat(mContext));
            podate = ConstantsUtils.convertCalenderToDisplayDateFormat(calendar, "yyyy-MM-dd'T'HH:mm:ss");
//            podate = soListBean.getPODate();
        }
        masterHeaderTable.put(Constants.SONo, doc_no);
        masterHeaderTable.put(Constants.OrderType, soListBean.getOrderType());
        masterHeaderTable.put(Constants.OrderTypeDesc, soListBean.getOrderTypeDesc());
        masterHeaderTable.put(Constants.OrderDate, orderDate);
        masterHeaderTable.put(Constants.CustomerNo, soListBean.getSoldTo());
        masterHeaderTable.put(Constants.CustomerPO, poNo);
        masterHeaderTable.put(Constants.CustomerPODate, podate);
        masterHeaderTable.put(Constants.ShippingTypeID, soListBean.getShippingPoint());
        masterHeaderTable.put(Constants.TransporterID, soListBean.getTransportNameID());
        masterHeaderTable.put(Constants.TransporterName, soListBean.getTransportName());
        masterHeaderTable.put(Constants.MeansOfTranstyp, "");
        masterHeaderTable.put(Constants.ShipToParty, soListBean.getShipTo());
        masterHeaderTable.put(Constants.ShipToPartyName, soListBean.getShipToName());
        masterHeaderTable.put(Constants.SalesArea, soListBean.getSalesArea());
        masterHeaderTable.put(Constants.SalesAreaDesc, soListBean.getSalesAreaDesc());
        masterHeaderTable.put(Constants.SalesOffice, soListBean.getSalesOfficeId());
        masterHeaderTable.put(Constants.SaleOffDesc, soListBean.getSaleOffDesc());
        masterHeaderTable.put(Constants.SalesGroup, soListBean.getSalesGroup());
        masterHeaderTable.put(Constants.SaleGrpDesc, soListBean.getSaleGrpDesc());
        masterHeaderTable.put(Constants.Plant, soListBean.getPlant());
        masterHeaderTable.put(Constants.Incoterm1, soListBean.getIncoTerm1());
        masterHeaderTable.put(Constants.Incoterm2, soListBean.getIncoterm2());
        masterHeaderTable.put(Constants.Payterm, soListBean.getPaymentTerm());
        masterHeaderTable.put(Constants.PaytermDesc, soListBean.getPaymentTermDesc());
        masterHeaderTable.put(Constants.UnloadingPoint, soListBean.getUnloadingPointId());
        masterHeaderTable.put(Constants.ReceivingPoint, soListBean.getReceivingPointId());
        masterHeaderTable.put(Constants.Currency, soListBean.getCurrency());
        masterHeaderTable.put(Constants.NetPrice, soListBean.getTotalAmt());
        masterHeaderTable.put(Constants.TotalAmount, soListBean.getTotalAmt());
        masterHeaderTable.put(Constants.NetWeight, soListBean.getmStrTotalWeight());
        masterHeaderTable.put(Constants.NetWeightUom, soListBean.getmStrWeightUOM());
        masterHeaderTable.put(Constants.TotalQuantity, soListBean.getQuantity());
        masterHeaderTable.put(Constants.QuantityUom, soListBean.getmSteTotalQtyUOM());
        masterHeaderTable.put(Constants.TaxAmount, "0");
        masterHeaderTable.put(Constants.Freight, "0");
        masterHeaderTable.put(Constants.Discount, "0");
        if (soListBean.isOneTimeShipTo()) {
            masterHeaderTable.put(Constants.ONETIMESHIPTO, "X");
            masterHeaderTable.put(Constants.Address1, soListBean.getCustAddress1());
            masterHeaderTable.put(Constants.Address2, soListBean.getCustAddress2());
            masterHeaderTable.put(Constants.Address3, soListBean.getCustAddress3());
            masterHeaderTable.put(Constants.Address4, soListBean.getCustAddress4());
            masterHeaderTable.put(Constants.District, soListBean.getCustDistrict());
            masterHeaderTable.put(Constants.CityID, soListBean.getCustCity());
            masterHeaderTable.put(Constants.RegionID, soListBean.getCustRegion());
            masterHeaderTable.put(Constants.RegionDesc, soListBean.getCustRegionDesc());
            masterHeaderTable.put(Constants.CountryID, soListBean.getCustCountry());
            masterHeaderTable.put(Constants.CountryDesc, soListBean.getCustCountryDesc());
            masterHeaderTable.put(Constants.PostalCode, soListBean.getCustPostalCode());
            masterHeaderTable.put(Constants.CustomerName, soListBean.getCustLastName());
            masterHeaderTable.put(Constants.CustomerNo, soListBean.getShipTo());
            masterHeaderTable.put(Constants.PartnerFunctionDesc, soListBean.getCustFirstName());
            masterHeaderTable.put(Constants.PartnerFunctionID, Constants.SH);

        }
       /* if (activityFrom == 5) {
            masterHeaderTable.put(Constants.Testrun, "E");
        } else {
            masterHeaderTable.put(Constants.Testrun, "");
        }*/
        if (!TextUtils.isEmpty(soListBean.getRemarks())) {
            /*remarks started*/
            ArrayList<HashMap<String, String>> soTextItemTable = new ArrayList<>();
            HashMap<String, String> soTextMap = new HashMap<String, String>();
            soTextMap.put(Constants.TextID, "0001");
            soTextMap.put(Constants.Text, soListBean.getRemarks());
            soTextMap.put(Constants.SONo, doc_no);
            soTextItemTable.add(soTextMap);
            masterHeaderTable.put("item_" + doc_no, UtilConstants.convertArrListToGsonString(soTextItemTable));
            /*remarks end*/
        }
        itemTable.clear();
        for (int i = 0; i < soListBean.getSoItemBeanArrayList().size(); i++) {
            SOItemBean itemDesc = soListBean.getSoItemBeanArrayList().get(i);
            HashMap<String, String> singleItem = new HashMap<String, String>();
            singleItem.put(Constants.SONo, doc_no);
            String itemNo = ConstantsUtils.addZeroBeforeValue((i + 1), ConstantsUtils.ITEM_MAX_LENGTH);
            if (!TextUtils.isEmpty(itemDesc.getItemNo())) {
                itemNo = itemDesc.getItemNo();
            }
            singleItem.put(Constants.ItemNo, itemNo);
            try {
                singleItem.put(Constants.MaterialGroup, Constants.mapMatGrpByMaterial.get(itemDesc.getMatCode()));
            } catch (Exception e) {
                singleItem.put(Constants.MaterialGroup, "");
                e.printStackTrace();
            }
            singleItem.put(Constants.Material, itemDesc.getMatCode());
            singleItem.put(Constants.HighLevellItemNo, itemDesc.getHighLevellItemNo());
            singleItem.put(Constants.ItemFlag, itemDesc.getItemFlag());
            singleItem.put(Constants.ItemCategory, itemDesc.getItemCategory());
            singleItem.put(Constants.MaterialDesc, itemDesc.getMatDesc());
            singleItem.put(Constants.Plant, soListBean.getPlant());
            singleItem.put(Constants.StorLoc, "");
            singleItem.put(Constants.UOM, itemDesc.getUom());
            if (!itemDesc.getSoQty().equalsIgnoreCase(""))
                singleItem.put(Constants.Quantity, itemDesc.getSoQty());
            else
                singleItem.put(Constants.Quantity, itemDesc.getQuantity());
            singleItem.put(Constants.Currency, soListBean.getCurrency());
            if (!itemDesc.getUnitPrice().equalsIgnoreCase(""))
                singleItem.put(Constants.UnitPrice, itemDesc.getUnitPrice());
            else
                singleItem.put(Constants.UnitPrice, "0");
            if (!itemDesc.getNetAmount().equalsIgnoreCase(""))
                singleItem.put(Constants.NetAmount, itemDesc.getNetAmount());
            else
                singleItem.put(Constants.NetAmount, "0");

            Double calAlteWeiQty = 0.00,calAlteWeightQty=0.00;
            try {
                calAlteWeightQty = Double.parseDouble(itemDesc.getAlternateWeight());
            } catch (NumberFormatException e) {
                calAlteWeightQty = 0.00;
                e.printStackTrace();
            }
            if(calAlteWeightQty>0){
                singleItem.put(Constants.AlternateWeight, calAlteWeightQty+"");
            }else{
                try {
                    calAlteWeiQty = Double.parseDouble(itemDesc.getQuantity())* Double.parseDouble(itemDesc.getNetWeight());
                } catch (NumberFormatException e) {
                    calAlteWeiQty = 0.00;
                    e.printStackTrace();
                }
                try {
                    if(calAlteWeiQty.isNaN() || calAlteWeiQty.isInfinite()){
                        calAlteWeiQty = 0.00;
                    }
                } catch (Exception e) {
                    calAlteWeiQty = 0.00;
                    e.printStackTrace();
                }
                singleItem.put(Constants.AlternateWeight, calAlteWeiQty+"");
            }


            singleItem.put(Constants.GrossAmount, "0");
            singleItem.put(Constants.Freight, itemDesc.getFreight());
            singleItem.put(Constants.Tax, itemDesc.getTaxAmount());
            singleItem.put(Constants.Discount, itemDesc.getDiscount());
            if(!TextUtils.isEmpty(itemDesc.getRejectionId())) {
                singleItem.put(Constants.RejReason, itemDesc.getRejectionId());
                singleItem.put(Constants.RejReasonDesc, itemDesc.getRejectionStatusDesc());
            }else{
                singleItem.put(Constants.RejReason, strRejReason);
                singleItem.put(Constants.RejReasonDesc, strRejReasonDesc);
            }
            subItemTable.clear();
            for (int j = 0; j < itemDesc.getSoSubItemBeen().size(); j++) {
                SOSubItemBean subItems = itemDesc.getSoSubItemBeen().get(j);
                HashMap<String, String> subItemMap = new HashMap<String, String>();
                subItemMap.put(Constants.DelSchLineNo, ConstantsUtils.addZeroBeforeValue(j + 1, ConstantsUtils.ITEM_MAX_LENGTH_3));
                subItemMap.put(Constants.ItemNo, itemNo);
                subItemMap.put(Constants.DeliveryDate, subItems.getDateForStore() + "T00:00:00");
                subItemMap.put(Constants.MaterialNo, itemDesc.getMatCode());
                subItemMap.put(Constants.OrderQty, subItems.getSubQty());
                subItemMap.put(Constants.UOM, itemDesc.getUom());
                subItemMap.put(Constants.ConfirmedQty, subItems.getSubQty());
                subItemMap.put(Constants.RequiredQty, subItems.getSubQty());
                subItemMap.put(Constants.SONo, doc_no);
                subItemMap.put(Constants.RequirementDate, subItems.getDateForStore() + "T00:00:00");
                if (!TextUtils.isEmpty(subItems.getTransportationPlanDate())) {
                    subItemMap.put(Constants.TransportationPlanDate, subItems.getTransportationPlanDate());
                }
                if (!TextUtils.isEmpty(subItems.getMaterialAvailDate())) {
                    subItemMap.put(Constants.MaterialAvailDate, subItems.getMaterialAvailDate());
                }
                subItemTable.add(subItemMap);

            }

            /*so Condition Item*/
            conditionItemTable.clear();
            for (int j = 0; j < itemDesc.getConditionItemDetaiBeanArrayList().size(); j++) {
                SOConditionItemDetaiBean conditionItem = itemDesc.getConditionItemDetaiBeanArrayList().get(j);
                HashMap<String, String> contitionItemMap = new HashMap<String, String>();
                contitionItemMap.put(Constants.Amount, conditionItem.getAmount());
                contitionItemMap.put(Constants.Name, conditionItem.getName());
                contitionItemMap.put(Constants.ConditionAmtPer, conditionItem.getConditionAmtPer());
                contitionItemMap.put(Constants.ConditionValue, conditionItem.getConditionValue());
                contitionItemMap.put(Constants.ConditionTypeID, conditionItem.getConditionTypeID());
                contitionItemMap.put(Constants.Currency, conditionItem.getCurrency());
                contitionItemMap.put(Constants.CondCurrency, conditionItem.getCondCurrency());
                conditionItemTable.add(contitionItemMap);
            }
            //finished
            singleItem.put("item_" + itemDesc.getMatCode(), UtilConstants.convertArrListToGsonString(subItemTable));
            singleItem.put("conditionItem_" + itemDesc.getMatCode(), UtilConstants.convertArrListToGsonString(conditionItemTable));

            itemTable.add(singleItem);
        }


        if (soDetailsView != null) {
            if (comingFrom == ConstantsUtils.SO_APPROVAL_EDIT_ACTIVITY) {
                soDetailsView.conformationDialog(mContext.getString(R.string.save_conformation_msg), 2);
            } else {
                if (save.equals(mContext.getString(R.string.submit_so))) {
                    soDetailsView.conformationDialog(mContext.getString(R.string.save_conformation_msg), 1);
                } else if (save.equals(mContext.getString(R.string.cancel_so))) {
                    onCancelData();
                } else if (save.equals(mContext.getString(R.string.edit_so))) {
                    onUpdate();
                }
            }
        }

    }

    public void onError(int operation, Exception e) {
        e.printStackTrace();
        ErrorBean errorBean = Constants.getErrorCode(operation, e, mContext);
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
            if (isFromWhere == 1 || isFromWhere == 2) {
                if (!dialogCancelled && !Constants.isStoreClosed) {
                    if (operation == Operation.OfflineRefresh.getValue()) {
//                        if (soDetailsView != null) {
//                            soDetailsView.hideProgressDialog();
                            Constants.isSync = false;
                        Constants.isBackGroundSync = false;
                            if (!Constants.isStoreClosed) {
//                                soDetailsView.showMessage(mContext.getString(R.string.msg_error_occured_during_sync),false);
                                LogManager.writeLogError(mContext.getString(R.string.msg_error_occured_during_sync));
                            } else {
//                                soDetailsView.showMessage(mContext.getString(R.string.msg_sync_terminated),false);
                                LogManager.writeLogError(mContext.getString(R.string.msg_sync_terminated));
                            }
//                        }
                    } else if (operation == Operation.GetStoreOpen.getValue()) {
                        Constants.isSync = false;
                        Constants.isBackGroundSync = false;
//                        if (soDetailsView != null) {
//                            soDetailsView.hideProgressDialog();
//                            soDetailsView.showMessage(mContext.getString(R.string.msg_error_occured_during_sync),false);
                            LogManager.writeLogError(mContext.getString(R.string.msg_error_occured_during_sync));
//                        }
                    }


                }
            } else {
                penROReqCount++;
                if ((operation == Operation.Create.getValue()) && (penROReqCount == pendingROVal)) {
                    e.printStackTrace();
                    try {
                        concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(pendingCollectionList);
                        new RefreshAsyncTask(mContext, concatCollectionStr, this).execute();
//                        OfflineManager.refreshRequests(context, concatCollectionStr, this);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

                if (operation == Operation.OfflineFlush.getValue()) {
                    try {
                        new RefreshAsyncTask(mContext, concatCollectionStr, this).execute();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else if (operation == Operation.OfflineRefresh.getValue()) {
                   /* e.printStackTrace();
                    try {
                        String syncTime = UtilConstants.getSyncHistoryddmmyyyyTime();
                        String[] DEFINGREQARRAY = Constants.getDefinigReq(context);
                        for (int incReq = 0; incReq < DEFINGREQARRAY.length; incReq++) {

                            String colName = DEFINGREQARRAY[incReq];
                            if (colName.contains("?$")) {
                                String splitCollName[] = colName.split("\\?");
                                colName = splitCollName[0];
                            }

                            Constants.events.updateStatus(Constants.SYNC_TABLE,
                                    colName, Constants.TimeStamp, syncTime
                            );
                        }
                    } catch (Exception exce) {
                        e.printStackTrace();
                        ConstantsUtils.printErrorLog(e.getMessage());
                    }*/
//                    if (soDetailsView != null) {
//                        soDetailsView.hideProgressDialog();
//                        soDetailsView.showMessage(mContext.getString(R.string.msg_error_occured_during_sync),false);
                        LogManager.writeLogError(mContext.getString(R.string.msg_error_occured_during_sync));
//                    }
                } else if (operation == Operation.GetStoreOpen.getValue()) {
                    Constants.isSync = false;
                    Constants.isBackGroundSync = false;
//                    if (soDetailsView != null) {
//                        soDetailsView.hideProgressDialog();
//                        soDetailsView.showMessage(mContext.getString(R.string.msg_error_occured_during_sync),false);
                        LogManager.writeLogError(mContext.getString(R.string.msg_error_occured_during_sync));
//                    }
                }
            }
        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(mContext)) {
                Constants.isSync = true;
                Constants.isBackGroundSync = true;
                dialogCancelled = false;
                /*if (soDetailsView != null) {
                    soDetailsView.showProgressDialog("Loading...");
                }*/
                new RefreshAsyncTask(mContext, concatCollectionStr, this).execute();
            } else {
                Constants.isSync = false;
                Constants.isBackGroundSync = false;
//                if (soDetailsView != null) {
//                    soDetailsView.hideProgressDialog();
//                    Constants.displayMsgReqError(errorBean.getErrorCode(), mContext);
                    LogManager.writeLogError(errorBean.getErrorMsg());
//                }
            }
        } else {
            Constants.isSync = false;
            Constants.isBackGroundSync = false;
//            if (soDetailsView != null) {
//                soDetailsView.hideProgressDialog();
//                Constants.displayMsgReqError(errorBean.getErrorCode(), mContext);
                LogManager.writeLogError(errorBean.getErrorMsg());
//            }
        }
    }

    @Override
    public void postNotes(String messageNote) {
        if (validateNotes(messageNote)) {
            if (UtilConstants.isNetworkAvailable(mContext)) {
                Hashtable<String, String> headerTable = new Hashtable<>();
                headerTable.put(Constants.SONo, soListBean.getSONo());
                headerTable.put(Constants.ItemNo, Constants.str_0000);
                headerTable.put(Constants.Text, messageNote);
                headerTable.put(Constants.TextCategory, Constants.H);
                if (soDetailsView != null) {
                    soDetailsView.showProgressDialog(mContext.getString(R.string.submitting_notes));
                }
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 2);
                new SOPostToServerAsyncTask(mContext, this, headerTable, null, 4, bundle).execute();

            } else {
                if (soDetailsView != null) {
                    soDetailsView.showMessage(mContext.getString(R.string.no_network_conn), true);
                }
            }
        } else {
            if (soDetailsView != null) {
                soDetailsView.showMessage(mContext.getString(R.string.please_enter_notes), true);
            }
        }
    }

    @Override
    public void pdfDownload() {
        if (PermissionUtils.checkStoragePermission(mContext)) {
            if (soDetailsView != null) {
                soDetailsView.showProgressDialog(mContext.getString(R.string.downloading_pdf));
            }
            if (isSessionRequired) {
                new SessionIDAsyncTask(mContext, new AsyncTaskCallBackInterface() {
                    @Override
                    public void asyncResponse(boolean b, Object o, String s) {
                        if (b) {
                            downloadFiles(s);
                        } else {
                            if (soDetailsView != null) {
                                soDetailsView.hideProgressDialog();
                                soDetailsView.showMessage(s, true);
                            }
                        }
                    }
                });
            } else {
                downloadFiles("");
            }

        }
    }

    @Override
    public void onCancelData() {
        if (soDetailsView != null) {
            soDetailsView.showProgressDialog(mContext.getString(R.string.canceling_data_wait));
        }
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 4);
        new SOPostToServerAsyncTask(mContext, this, masterHeaderTable, itemTable, 5, bundle).execute();

    }

    @Override
    public void approveData(String ids, String description, String approvalStatus) {
        if (UtilConstants.isNetworkAvailable(mContext)) {
            masterHeaderTable.put(Constants.DecisionKey, approvalStatus);
            masterHeaderTable.put(Constants.Comments, description);

            Bundle bundle = new Bundle();
            if (soDetailsView != null) {
                soDetailsView.showProgressDialog(mContext.getString(R.string.updating_data_wit));
            }
            bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 6);
            isErrorSuccessCalled = false;
            try {
                OnlineManager.batchUpdateSO(masterHeaderTable, itemTable, mContext, new UIListener() {
                    @Override
                    public void onRequestError(int i, Exception e) {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                isErrorSuccessCalled = true;
                                soDetailsView.hideProgressDialog();
                                soDetailsView.showMessage(e.getMessage(), true);
                            }
                        });
                    }

                    @Override
                    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                isErrorSuccessCalled = true;
//                            soDetailsView.hideProgressDialog();
                                approveOrder(masterHeaderTable.get(Constants.InstanceID), masterHeaderTable.get(Constants.DecisionKey), masterHeaderTable.get(Constants.EntityKey), masterHeaderTable.get(Constants.Comments));
                            }
                        });
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
//            new SOPostToServerAsyncTask(mContext, this, masterHeaderTable, itemTable, 3, bundle).execute();
        } else {
            if (soDetailsView != null) {
                soDetailsView.showMessage(mContext.getString(R.string.no_network_conn), true);
            }
        }
    }

    private void downloadFiles(String sessionId) {
        String soItemDetailsURL = "/SOItemDetails(SONo=%27" + soListBean.getSONo() + "%27,ItemNo=%27" + soListBean.getItemNo() + "%27)/$value";
        ConstantsUtils.downloadFiles(mContext, new AsyncTaskCallBackInterface() {
            @Override
            public void asyncResponse(boolean status, Object response, String message) {
                if (soDetailsView != null) {
                    soDetailsView.hideProgressDialog();
                }
                if (status) {
                    UtilConstants.openViewer(mContext, message, UtilConstants.PDF_MINE_TYPE);
                } else {
                    if (soDetailsView != null) {
                        soDetailsView.showMessage(message, true);
                    }
                }
            }
        }, soItemDetailsURL, soListBean.getSONo(), UtilConstants.PDF_MINE_TYPE, sessionId, isSessionRequired);
    }

    private boolean validateNotes(String messageNote) {
        return messageNote.trim().length() > 0;
    }

    @Override
    public void onFinished(SOListBean soListBean) {
        if (soDetailsView != null) {
            soDetailsView.displayHeaderList(soListBean);
        }
    }

    @Override
    public void responseSuccess(final ODataRequestExecution oDataRequestExecution, List<ODataEntity> entities, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        switch (type) {
            case 1:
                if (soDetailsView != null) {
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            soDetailsView.hideProgressDialog();
                            soDetailsView.showMessage(ConstantsUtils.getResponseMessage(oDataRequestExecution, mContext.getString(R.string.so_created_successfully)), false);
                        }
                    });
                }
                break;
            case 2:
                if (soDetailsView != null) {
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            soDetailsView.hideProgressDialog();
                            String qry = Constants.SOTexts + "(SONo='" + soListBean.getSONo() + "',ItemNo='0000',TextID='" + SOUtils.getSOTextId(Constants.HDRNTTXTID) + "',TextCategory='H')";
                            soDetailsView.showProgressDialog(mContext.getString(R.string.loading_notes));
                            ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 3, ConstantsUtils.SESSION_HEADER, SODetailsPresenterImpl.this, false);
                        }
                    });
                }
                break;
            case 3:
                final ArrayList<SOTextBean> soTextBeanArrayList = new ArrayList<>();
                try {
                    soTextBeanArrayList.addAll(OnlineManager.getSOTextList(oDataRequestExecution));
                } catch (OnlineODataStoreException e) {
                    e.printStackTrace();
                }
                if (soDetailsView != null) {
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            soDetailsView.hideProgressDialog();
                            soDetailsView.displayNotes(soTextBeanArrayList);
                        }
                    });
                }
                break;
            case 4:
                if (!isErrorSuccessCalled) {
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isErrorSuccessCalled = true;
                            soDetailsView.hideProgressDialog();
                            soDetailsView.showMessage(mContext.getString(R.string.so_Cancelled_successfully), false);
                        }
                    });
                }

                break;
            case 5:
                if (!isErrorSuccessCalled) {
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isErrorSuccessCalled = true;
                            soDetailsView.hideProgressDialog();
                            soDetailsView.showMessage(mContext.getString(R.string.so_updated_successfully), false);
                        }
                    });
                }
                break;
            case 6:
                if (!isErrorSuccessCalled) {
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isErrorSuccessCalled = true;
//                            soDetailsView.hideProgressDialog();
                            approveOrder(masterHeaderTable.get(Constants.InstanceID), masterHeaderTable.get(Constants.DecisionKey), masterHeaderTable.get(Constants.EntityKey), masterHeaderTable.get(Constants.Comments));
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void responseFailed(final ODataRequestExecution oDataRequestExecution, final String errorMsg, Bundle bundle) {
        TraceLog.e(Constants.RequestFailed_status_message + " : " + errorMsg);
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        if (type == 4 || type == 5 || type == 6) {
            if (!isErrorSuccessCalled) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isErrorSuccessCalled = true;
                        soDetailsView.hideProgressDialog();
                        soDetailsView.showMessage(errorMsg, true);
                    }
                });
            }
        } else {
            if (soDetailsView != null) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        soDetailsView.hideProgressDialog();
                        soDetailsView.showMessage(errorMsg, true);
                    }
                });

            }
        }
    }

    /*approve order*/
    private void approveOrder(String mStrInstanceId, String desisionKey, String soNo, String comments) {
        if (UtilConstants.isNetworkAvailable(mContext)) {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
            String loginIdVal = sharedPreferences.getString(Constants.username, "");
            masterHeaderTable.clear();
            masterHeaderTable.put(Constants.EntityType, "SO");
            masterHeaderTable.put(Constants.LoginID, loginIdVal);
            masterHeaderTable.put(Constants.InstanceID, mStrInstanceId);
            masterHeaderTable.put(Constants.DecisionKey, desisionKey);
            masterHeaderTable.put(Constants.EntityKey, soNo);
            masterHeaderTable.put(Constants.Comments, comments);


            JSONObject headerObject = new JSONObject();
            try {
                headerObject.putOpt(Constants.InstanceID, masterHeaderTable.get(Constants.InstanceID));
                headerObject.putOpt(Constants.EntityType, masterHeaderTable.get(Constants.EntityType));
                headerObject.putOpt(Constants.DecisionKey, masterHeaderTable.get(Constants.DecisionKey));
                headerObject.putOpt(Constants.LoginID, masterHeaderTable.get(Constants.LoginID));
                headerObject.putOpt(Constants.EntityKey, masterHeaderTable.get(Constants.EntityKey));
                headerObject.putOpt(Constants.Comments, masterHeaderTable.get(Constants.Comments));
            } catch (Throwable e) {
                e.printStackTrace();
            }
            String qry =Constants.Tasks + "(InstanceID='" + masterHeaderTable.get(Constants.InstanceID) + "',EntityType='" + masterHeaderTable.get(Constants.EntityType) + "')";


            OnlineManager.updateEntity("",headerObject.toString(),qry, this,mContext);


//            progressDialog = ConstantsUtils.showProgressDialog(getActivity(), "Update data please wait...");
//            new DirecySyncAsyncTask(mContext, null, SODetailsPresenterImpl.this, masterHeaderTable, null, 2).execute();
        } else {
            if (soDetailsView != null) {
                soDetailsView.hideProgressDialog();
                soDetailsView.showMessage(mContext.getString(R.string.no_network_conn), true);
            }
        }
    }

    @Override
    public void onRequestError(int i, Exception e) {
        Constants.isBackGroundSync=false;
        e.printStackTrace();
        LogManager.writeLogDebug(" So Create Failed "+ e.getLocalizedMessage());
        if(isFromWhere==4){
            onError(i,e);
        }else {
            if (soDetailsView != null) {
                soDetailsView.hideProgressDialog();
                soDetailsView.showMessage(mContext.getString(R.string.so_apprvol_failed), false);
            }
        }
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
//        Constants.isBackGroundSync=false;
        if(isFromWhere==4){
            onSuccess(i,s);
        }else {
            if (soDetailsView != null) {
                soDetailsView.hideProgressDialog();
                String statusMsg = mContext.getString(R.string.so_details_msg_approved);
                if(Constants.writeDebug){
                    LogManager.writeLogDebug(" So Create Success");
                }
                soDetailsView.showApprovalSuccMsg(mContext.getString(R.string.so_apprvol_success, statusMsg));
                Constants.isBackGroundSync=false;
            }
        }
    }

    public void onSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        if (!dialogCancelled && !Constants.isStoreClosed) {
            if (isFromWhere == 4) {
                if (operation == Operation.Create.getValue() && pendingROVal > 0) {
                    Constants.updateLastSyncTimeToTable(pendingCollectionList,mContext,Constants.SOPOSTBG_sync,refguid.toString().toUpperCase());
                    Set<String> set = new HashSet<>();
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
                    set = sharedPreferences.getStringSet(Constants.SalesOrderDataValt, null);

                    HashSet<String> setTemp = new HashSet<>();
                    if (set != null && !set.isEmpty()) {
                        Iterator itr = set.iterator();
                        while (itr.hasNext()) {
                            setTemp.add(itr.next().toString());
                        }
                    }

                    try {
                        if(tempRODevList!=null && tempRODevList.length>0) {
                            setTemp.remove(tempRODevList[penROReqCount]);

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putStringSet(Constants.SalesOrderDataValt, setTemp);
                            editor.commit();

                            try {
                                ConstantsUtils.storeInDataVault(tempRODevList[penROReqCount], "",mContext);
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    penROReqCount++;


                }
                if ((operation == Operation.Create.getValue()) && (penROReqCount == pendingROVal)) {

                    /*try {
                        for (int incVal = 0; incVal < pendingCollectionList.size(); incVal++) {
                            if (incVal == 0 && incVal == pendingCollectionList.size() - 1) {
                                concatCollectionStr = concatCollectionStr + pendingCollectionList.get(incVal);
                            } else if (incVal == 0) {
                                concatCollectionStr = concatCollectionStr + pendingCollectionList.get(incVal) + ", ";
                            } else if (incVal == pendingCollectionList.size() - 1) {
                                concatCollectionStr = concatCollectionStr + pendingCollectionList.get(incVal);
                            } else {
                                concatCollectionStr = concatCollectionStr + pendingCollectionList.get(incVal) + ", ";
                            }
                        }
                        new RefreshAsyncTask(mContext, concatCollectionStr, this).execute();
//                        OfflineManager.refreshRequests(context, concatCollectionStr, this);
                    } catch (Exception e) {
                        e.printStackTrace();
                        TraceLog.e(Constants.SyncOnRequestSuccess, e);
                    }*/

                    try {
                        if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                            if (UtilConstants.isNetworkAvailable(mContext)) {
                                try {
                                    new FlushDataAsyncTask(this, alFlushColl).execute();
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                }
                            } else {
                                Constants.iSAutoSync = false;
                                Constants.isBackGroundSync=false;
                                LogManager.writeLogError(mContext.getString(R.string.data_conn_lost_during_sync));
                            }
                        } else {
                            if (UtilConstants.isNetworkAvailable(mContext)) {
//                            alAssignColl.clear();
//                            alAssignColl.addAll(Constants.getDefinigReqList(mContext));
                                try {
                                    for (int incVal = 0; incVal < pendingCollectionList.size(); incVal++) {
                                        if (incVal == 0 && incVal == pendingCollectionList.size() - 1) {
                                            concatCollectionStr = concatCollectionStr + pendingCollectionList.get(incVal);
                                        } else if (incVal == 0) {
                                            concatCollectionStr = concatCollectionStr + pendingCollectionList.get(incVal) + ", ";
                                        } else if (incVal == pendingCollectionList.size() - 1) {
                                            concatCollectionStr = concatCollectionStr + pendingCollectionList.get(incVal);
                                        } else {
                                            concatCollectionStr = concatCollectionStr + pendingCollectionList.get(incVal) + ", ";
                                        }
                                    }
                                    new RefreshAsyncTask(mContext, concatCollectionStr, this).execute();
//                        OfflineManager.refreshRequests(context, concatCollectionStr, this);

                                } catch (Exception e) {
                                    TraceLog.e(Constants.SyncOnRequestSuccess, e);
                                }
                            } else {
                                Constants.iSAutoSync = false;
                                Constants.isBackGroundSync = false;
                                LogManager.writeLogError(mContext.getString(R.string.data_conn_lost_during_sync));
                            }
                        }

                    } catch (ODataException e) {
                        e.printStackTrace();
                    }


                } else if (operation == Operation.OfflineFlush.getValue()) {

                    try {
                        for (int incVal = 0; incVal < pendingCollectionList.size(); incVal++) {
                            if (incVal == 0 && incVal == pendingCollectionList.size() - 1) {
                                concatCollectionStr = concatCollectionStr + pendingCollectionList.get(incVal);
                            } else if (incVal == 0) {
                                concatCollectionStr = concatCollectionStr + pendingCollectionList.get(incVal) + ", ";
                            } else if (incVal == pendingCollectionList.size() - 1) {
                                concatCollectionStr = concatCollectionStr + pendingCollectionList.get(incVal);
                            } else {
                                concatCollectionStr = concatCollectionStr + pendingCollectionList.get(incVal) + ", ";
                            }
                        }
                        new RefreshAsyncTask(mContext, concatCollectionStr, this).execute();
//                        OfflineManager.refreshRequests(context, concatCollectionStr, this);

                    } catch (Exception e) {
                        TraceLog.e(Constants.SyncOnRequestSuccess, e);
                    }
                } else if (operation == Operation.OfflineRefresh.getValue()) {
                    Constants.isBackGroundSync = false;
//                    if (soDetailsView != null) {
                        ConstantsUtils.startAutoSync(mContext,false);
//                        ConstantsUtils.serviceReSchedule(mContext, true);
//                        soDetailsView.hideProgressDialog();
                        String msg = "";
//                        if (soDetailsView != null) {
                            LogManager.writeLogError("SO Create Posted");
                            if (!isErrorFromBackend) {
                                SalesOrderHeaderListActivity.mBoolRefreshDone = true;
//                                salesOrderView.onReloadData();
//                                soDetailsView.showMessage(mContext.getString(R.string.msg_sync_successfully_completed),false);
                                LogManager.writeLogError(mContext.getString(R.string.msg_sync_successfully_completed));
                            } else {
//                                soDetailsView.showMessage(mContext.getString(R.string.msg_error_occured_during_sync),false);
                                LogManager.writeLogError(mContext.getString(R.string.msg_error_occured_during_sync));
//                                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, mContext, BuildConfig.APPLICATION_ID, false);
                            }
//                        }

//                    } else
                        if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
                        Constants.isSync = false;
                        Constants.isBackGroundSync = false;
                        try {
                            OfflineManager.getAuthorizations(mContext);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                        Constants.setSyncTime(mContext,refguid.toString().toUpperCase());
                        ConstantsUtils.startAutoSync(mContext,false);
//                        if (soDetailsView != null) {
//                            salesOrderView.onReloadData();
//                            soDetailsView.showMessage(mContext.getString(R.string.msg_sync_successfully_completed),false);
                            LogManager.writeLogError(mContext.getString(R.string.msg_sync_successfully_completed));
//                            AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, mContext, BuildConfig.APPLICATION_ID, false);
//                        }
                    }
                }
            }

        }
    }


    public void startSOSimulate(SOListBean soListBeanHeader) {

/*
        newlist.get(1).setQuantity(qty);
        newlist.get(1).setSoQty(qty);*/

      //  soListBeanHeader.setSoItemBeanArrayList(newlist);



        this.soListBeanHeader=soListBeanHeader;

        System.out.println("SoListSize1 :"+soListBeanHeader.getSoItemBeanArrayList().size());

        if (UtilConstants.isNetworkAvailable(mContext)) {
            if (soDetailsView != null) {
                soDetailsView.showProgressDialog(mContext.getString(R.string.app_loading));
            }

            Bundle bundle = new Bundle();
            bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 5);
            new SOSimulateAsyncTask(mContext, soListBeanHeader,new UIListener(){

                @Override
                public void onRequestError(int var1, Exception var2) {
                    if (soDetailsView != null) {
                        soDetailsView.hideProgressDialog();
                        soDetailsView.displayMessage(var2.getMessage());
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
                    refreshCreditLimit(soListBeanHeader);
                }
            }, this, bundle).execute();
        } else {
            if (soDetailsView != null) {
                soDetailsView.displayMessage(mContext.getString(R.string.no_network_conn));
            }
            getCreditLimitValue(soListBeanHeader.getCustomerNo());
        }
    }


    private void refreshCreditLimit(SOListBean soListBeanHeader) {
        try {

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
                                                if (soDetailsView != null) {
                                                    soDetailsView.hideProgressDialog();
                                                    soDetailsView.openReviewScreen(new ArrayList<CreditLimitBean>(),soListBeanHeader);
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
                                            if (soDetailsView != null) {
                                                soDetailsView.hideProgressDialog();
                                                soDetailsView.displayMessage(finalResponseBody);
//                                                    shipToView.openReviewScreen(new ArrayList<CreditLimitBean>());
                                                soDetailsView.showProgressDialog(finalResponseBody);
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
                                        if (soDetailsView != null) {
                                            soDetailsView.hideProgressDialog();
                                            soDetailsView.displayMessage(finalErrormessage);
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
                                    if (soDetailsView != null) {
                                        soDetailsView.hideProgressDialog();
                                        soDetailsView.displayMessage(e.getMessage());
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
                    if (soDetailsView != null) {
                        soDetailsView.hideProgressDialog();
                    }
                }
            });
        }
    }



    private void getCreditLimitValue(String mStrBundleRetID) {
//        String qry = Constants.CustomerCreditLimits + "?$filter=" +
//                Constants.Customer + " eq '" + customerNo + "'";
//        if(!Constants.getRollID(mContext)) {

        if (!UtilConstants.isNetworkAvailable(mContext)) {
            if(!ConstantsUtils.isPinging()) {
                String qry = Constants.CustomerCreditLimits + "?$filter=" +
                        Constants.Customer + " eq '" + customerNo + "' and " + Constants.CreditControlAreaID + " eq '" + soListBean.getCreditControlAreaID() + "'";
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
                if (soDetailsView != null) {
                    soDetailsView.hideProgressDialog();
                    if (UtilConstants.isNetworkAvailable(mContext)) {
                        if (!limitBeanList.isEmpty())
                            soDetailsView.openReviewScreen(limitBeanList, soListBeanHeader);
                        else
                            soDetailsView.openReviewScreen(new ArrayList<CreditLimitBean>(), soListBeanHeader);
                    } else {
//                        ArrayList<SOItemBean> soItemList = new ArrayList<>();
//                        soListBeanHeader.setSoItemBeanArrayList(soItemList);
                        if (!limitBeanList.isEmpty())
                            soDetailsView.openReviewScreen(new ArrayList<CreditLimitBean>(), soListBeanHeader);
                        else
                            soDetailsView.openReviewScreen(new ArrayList<CreditLimitBean>(), soListBeanHeader);
                    }
                }
            }
        });
    }


}
