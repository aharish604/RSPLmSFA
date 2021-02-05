package com.rspl.sf.msfa.socreate;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.solist.SOListBean;
import com.rspl.sf.msfa.store.OnlineManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by e10769 on 04-09-2017.
 */

public class SOSimulateAsyncTask extends AsyncTask<String, Boolean, Boolean> {
    private Context mContext;
    private SOListBean soListBeanHeader;
    private OnlineODataInterface onlineODataInterface = null;
    private Bundle bundle;
    private String isError = "";
    UIListener uiListener;

    public SOSimulateAsyncTask(Context mContext, SOListBean soListBeanHeader, UIListener uiListener, OnlineODataInterface onlineODataInterface, Bundle bundle) {
        this.mContext = mContext;
        this.soListBeanHeader = soListBeanHeader;
        this.onlineODataInterface = onlineODataInterface;
        this.bundle = bundle;
        this.uiListener = uiListener;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        boolean isStoreOpened = true;

        if (isStoreOpened) {
//                if (store.isOpenCache()){
//                    store.closeCache();
//                }
            ArrayList<HashMap<String, String>> arrtableSimu = new ArrayList<HashMap<String, String>>();
            int i = 0;
            JSONArray jsonArray = new JSONArray();
            JSONArray jsonSPArray = new JSONArray();


            try {
                for (SOItemBean soItemBean :soListBeanHeader.getSoItemBeanArrayList()) {
                    JSONObject singleItem = new JSONObject();

                    if (soItemBean.getHighLevellItemNo().equalsIgnoreCase("000000")) {
                        singleItem.put(Constants.ItemNo, ConstantsUtils.addZeroBeforeValue(i + 1, ConstantsUtils.ITEM_MAX_LENGTH));
                        singleItem.put(Constants.Material, soItemBean.getMatCode());
                        singleItem.put(Constants.MaterialDesc, soItemBean.getMatDesc());
                        String plant = "";
                        if (!TextUtils.isEmpty(soItemBean.getPlantId())) {
                            plant = soItemBean.getPlantId();
                        } else {
                            plant = soListBeanHeader.getPlant();
                        }
                        singleItem.put(Constants.Plant, plant);
                        singleItem.put(Constants.StorLoc, "");
                        singleItem.put(Constants.UOM, soItemBean.getUom());
                        singleItem.put(Constants.ItemFlag, soItemBean.getItemFlag());
                        singleItem.put(Constants.HighLevellItemNo, soItemBean.getHighLevellItemNo());
                        singleItem.put(Constants.ItemCategory, soItemBean.getItemCategory());
                        singleItem.put(Constants.Quantity, soItemBean.getSoQty());
                        singleItem.put(Constants.Currency, "");
                        singleItem.put(Constants.UnitPrice, "0");
                        singleItem.put(Constants.NetAmount, "0");
                        singleItem.put(Constants.GrossAmount, "0");
//                    singleItem.put(Constants.Freight, "");
                        singleItem.put(Constants.Tax, "0");
                        singleItem.put(Constants.Discount, "0");
                        if (!TextUtils.isEmpty(soItemBean.getRejectionId())) {
                            singleItem.put(Constants.RejReason, soItemBean.getRejectionId());
                            singleItem.put(Constants.RejReasonDesc, soItemBean.getRejectionStatusDesc());
                        }
                        singleItem.put(Constants.Discount, "0");
                        JSONArray subArray = new JSONArray();
                        singleItem.put(Constants.SOConditionItemDetails, subArray);
                        jsonArray.put(singleItem);
                        i++;
                    }
                }
                String podate = "";
                if (!TextUtils.isEmpty(soListBeanHeader.getPODate())) {
                    Calendar calendar = ConstantsUtils.convertCalenderToDisplayDateFormat(soListBeanHeader.getPODate(), ConstantsUtils.getDisplayDateFormat(mContext));
                    podate = ConstantsUtils.convertCalenderToDisplayDateFormat(calendar, "yyyy-MM-dd'T'HH:mm:ss");
//            podate = soListBean.getPODate();
                }


                JSONObject dbHeadTable = new JSONObject();
                JSONObject dbSpHeadTable = new JSONObject();

                dbHeadTable.put(Constants.OrderType, soListBeanHeader.getOrderType());

                dbHeadTable.put(Constants.OrderDate, Constants.getNewDateTimeFormat());
                dbHeadTable.put(Constants.CustomerNo, soListBeanHeader.getSoldTo());
                dbHeadTable.put(Constants.CustomerPO, soListBeanHeader.getPONo());
                if (!TextUtils.isEmpty(podate)) {
                    dbHeadTable.put(Constants.CustomerPODate, podate);
                }
                dbHeadTable.put(Constants.ShippingTypeID, "");
                dbHeadTable.put(Constants.MeansOfTranstyp, "");
                if(!TextUtils.isEmpty(soListBeanHeader.getTransportNameID())) {
                    dbHeadTable.put(Constants.TransporterID, soListBeanHeader.getTransportNameID());
                }
                if(!TextUtils.isEmpty(soListBeanHeader.getTransportName())) {
                    dbHeadTable.put(Constants.TransporterName, soListBeanHeader.getTransportName());
                }
                if(!TextUtils.isEmpty(soListBeanHeader.getShipTo())) {
                    dbHeadTable.put(Constants.ShipToParty, soListBeanHeader.getShipTo());
                }
                if(!TextUtils.isEmpty(soListBeanHeader.getSalesArea())) {
                    dbHeadTable.put(Constants.SalesArea, soListBeanHeader.getSalesArea());
                }
                if(!TextUtils.isEmpty(soListBeanHeader.getSalesOfficeId())) {
                    dbHeadTable.put(Constants.SalesOffice, soListBeanHeader.getSalesOfficeId());
                }
                if(!TextUtils.isEmpty(soListBeanHeader.getSaleOffDesc())) {
                    dbHeadTable.put(Constants.SaleOffDesc, soListBeanHeader.getSaleOffDesc());
                }
                if(!TextUtils.isEmpty(soListBeanHeader.getSalesGroup())) {
                    dbHeadTable.put(Constants.SalesGroup, soListBeanHeader.getSalesGroup());
                }
                if(!TextUtils.isEmpty(soListBeanHeader.getSaleGrpDesc())) {
                    dbHeadTable.put(Constants.SaleGrpDesc, soListBeanHeader.getSaleGrpDesc());
                }
                if(!TextUtils.isEmpty(soListBeanHeader.getPlant())) {
                    dbHeadTable.put(Constants.Plant, soListBeanHeader.getPlant());
                }
                if(!TextUtils.isEmpty(soListBeanHeader.getIncoTerm1())) {
                    dbHeadTable.put(Constants.Incoterm1, soListBeanHeader.getIncoTerm1());
                }
                if(!TextUtils.isEmpty(soListBeanHeader.getIncoterm2())) {
                    dbHeadTable.put(Constants.Incoterm2, soListBeanHeader.getIncoterm2());
                }
                if(!TextUtils.isEmpty(soListBeanHeader.getPaymentTerm())) {
                    dbHeadTable.put(Constants.Payterm, soListBeanHeader.getPaymentTerm());
                }
                dbHeadTable.put(Constants.Currency, "");
                dbHeadTable.put(Constants.NetPrice, "0");
                if(!TextUtils.isEmpty(soListBeanHeader.getTotalAmt())) {
                    dbHeadTable.put(Constants.TotalAmount, soListBeanHeader.getTotalAmt());
                }
                dbHeadTable.put(Constants.TaxAmount, "0");
                dbHeadTable.put(Constants.Freight, "0");
                dbHeadTable.put(Constants.Discount, "0");
                dbHeadTable.put(Constants.Testrun, "S");
                dbHeadTable.put(Constants.SOItemDetails, jsonArray);
                if (soListBeanHeader.isOneTimeShipTo()){
                    dbSpHeadTable.put(Constants.ONETIMESHIPTO, "X");
                    dbSpHeadTable.put(Constants.Address1, soListBeanHeader.getCustAddress1());
                    dbSpHeadTable.put(Constants.Address2, soListBeanHeader.getCustAddress2());
                    dbSpHeadTable.put(Constants.Address3, soListBeanHeader.getCustAddress3());
                    dbSpHeadTable.put(Constants.Address4, soListBeanHeader.getCustAddress4());
                    dbSpHeadTable.put(Constants.District, soListBeanHeader.getCustDistrict());
                    dbSpHeadTable.put(Constants.CityID, soListBeanHeader.getCustCity());
                    dbSpHeadTable.put(Constants.RegionID, soListBeanHeader.getCustRegion());
                    dbSpHeadTable.put(Constants.RegionDesc, soListBeanHeader.getCustRegionDesc());
                    dbSpHeadTable.put(Constants.CountryID, soListBeanHeader.getCustCountry());
                    dbSpHeadTable.put(Constants.CountryDesc, soListBeanHeader.getCustCountryDesc());
                    dbSpHeadTable.put(Constants.PostalCode, soListBeanHeader.getCustPostalCode());
                    dbSpHeadTable.put(Constants.CustomerName, soListBeanHeader.getCustLastName());
                    dbSpHeadTable.put(Constants.CustomerNo, soListBeanHeader.getShipTo());
                    dbSpHeadTable.put(Constants.PartnerFunctionDesc, soListBeanHeader.getCustFirstName());
                    dbSpHeadTable.put(Constants.PartnerFunctionID, Constants.SH);
                    jsonSPArray.put(dbSpHeadTable);
                    dbHeadTable.put(Constants.SOPartnerFunctions,jsonSPArray);
                }
                Constants.REPEATABLE_REQUEST_ID="";
                OnlineManager.createEntitySimulate(Constants.REPEATABLE_REQUEST_ID,dbHeadTable.toString(), Constants.SOs,uiListener , mContext);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return isStoreOpened;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (!aBoolean) {
            if (onlineODataInterface != null) {
                onlineODataInterface.responseFailed(null, Constants.makeMsgReqError(Constants.ErrorNo, mContext, false), bundle);
            }
        } else {
            if (!TextUtils.isEmpty(isError)) {
                if (onlineODataInterface != null) {
                    onlineODataInterface.responseFailed(null, isError, bundle);
                }
            }
        }
    }
}
