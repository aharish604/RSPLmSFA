package com.rspl.sf.msfa.routeplan.customerslist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.rspl.sf.msfa.BuildConfig;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.asyncTask.RefreshAsyncTask;
import com.rspl.sf.msfa.collectionPlan.WeekDetailsList;
import com.rspl.sf.msfa.collectionPlan.WeekHeaderList;
import com.rspl.sf.msfa.collectionPlan.collectionCreate.SaleAreaBean;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.filter.DateFilterFragment;
import com.rspl.sf.msfa.mbo.CustomerBean;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.mtp.MTPHeaderBean;
import com.rspl.sf.msfa.mtp.MTPRoutePlanBean;
import com.rspl.sf.msfa.routeplan.customerslist.filter.CustomersFilterActivity;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by e10847 on 19-12-2017.
 */

public class CustomerPresenter implements ICustomerPresenter, UIListener {
    public ArrayList<CustomerBean> retailerArrayList = new ArrayList<>(), searchBeanArrayList;
    ArrayList<String> alAssignColl = null;
    boolean isValidData = true;
    private Context context;
    private ICustomerViewPresenter iCustomerViewPresenter;
    private Activity activity;
    private String visitType = "", customerNumber = "", filterType = "", statusId = "", statusName = "", delvStatusId = "", delvStatusName = "";
    private boolean isErrorFromBackend = false;
    private String searchText = "";
    private String salesDistrictId = "";
    private String mStrCustName = "";
    private int countPerRequest = 300;
    private GUID refguid =null;
    private String initiator = "";
    private String fromWhere = "";

    public CustomerPresenter(Context context, Activity activity, ICustomerViewPresenter iCustomerViewPresenter, String visitType, @NonNull String customerNumber, String salesDistrictId) {
        this.context = context;
        this.iCustomerViewPresenter = iCustomerViewPresenter;
        this.retailerArrayList = new ArrayList<>();
        this.searchBeanArrayList = new ArrayList<>();
        this.visitType = visitType;
        this.activity = activity;
        this.customerNumber = customerNumber;
        this.salesDistrictId = salesDistrictId;
    }

    public static WeekHeaderList setCustomerToBean(ArrayList<CustomerBean> alcustbean, final WeekHeaderList mtpHeaderBean) {
        ArrayList<WeekDetailsList> rtgsWeekItemList = new ArrayList<>();
        if (!alcustbean.isEmpty()) {
            for (CustomerBean customerBean : alcustbean) {
                WeekDetailsList mtpRoutePlanBean = new WeekDetailsList();
                mtpRoutePlanBean.setVisitDate(mtpHeaderBean.getFullDate());
                mtpRoutePlanBean.setDate(mtpHeaderBean.getDate());
                mtpRoutePlanBean.setDay(mtpHeaderBean.getDay());
                mtpRoutePlanBean.setRemarks(customerBean.getRemarks());
                mtpRoutePlanBean.setcPNo(customerBean.getCustomerId());
                mtpRoutePlanBean.setcPName(customerBean.getCustomerName());
                mtpRoutePlanBean.setcPType(customerBean.getCustomerType());
                mtpRoutePlanBean.setPlannedValue(customerBean.getAmount());
                mtpRoutePlanBean.setPlannedValue2(customerBean.getAmount1());
                mtpRoutePlanBean.setRemarks2(customerBean.getRemarks1());
                mtpRoutePlanBean.setCollectionPlanGUID(mtpHeaderBean.getCollectionPlanGUID());
                mtpRoutePlanBean.setCollectionPlanItemGUID(customerBean.getRouteSchPlanGUID());
                mtpRoutePlanBean.setCollectionPlanItemGUID1(customerBean.getRouteSchPlanGUID1());
                rtgsWeekItemList.add(mtpRoutePlanBean);

            }
        }
        mtpHeaderBean.setWeekDetailsLists(rtgsWeekItemList);
        return mtpHeaderBean;
    }

    @Override
    public void onRequestError(int i, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(i, e, context);
        if (iCustomerViewPresenter != null) {
            iCustomerViewPresenter.hideProgressDialog();
        }
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
                if (i == Operation.OfflineRefresh.getValue()) {
                    Constants.isSync = false;
                    if (iCustomerViewPresenter != null) {
                        iCustomerViewPresenter.hideProgressDialog();
                        iCustomerViewPresenter.displayMsg(context.getString(R.string.msg_error_occured_during_sync));
                    }
                }else if (i == Operation.GetStoreOpen.getValue()){
                    Constants.isSync = false;
                    if (iCustomerViewPresenter != null) {
                        iCustomerViewPresenter.hideProgressDialog();
                        iCustomerViewPresenter.displayMsg(context.getString(R.string.msg_error_occured_during_sync));
                    }
                }

        }else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (iCustomerViewPresenter != null) {
                    iCustomerViewPresenter.showProgressDialog();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (iCustomerViewPresenter != null) {
                    iCustomerViewPresenter.hideProgressDialog();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        } else {
            Constants.isSync = false;
            if (iCustomerViewPresenter != null) {
                iCustomerViewPresenter.hideProgressDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        if (!Constants.isStoreClosed) {
            if (i == Operation.OfflineRefresh.getValue()) {
                Constants.updateLastSyncTimeToTable(alAssignColl,context,Constants.AdVst_sync,refguid.toString().toUpperCase());
                Constants.isSync = false;
                ConstantsUtils.startAutoSync(context,false);
//                ConstantsUtils.serviceReSchedule(context, true);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iCustomerViewPresenter != null) {
                            iCustomerViewPresenter.hideProgressDialog();
                            iCustomerViewPresenter.customersListSync();
                            AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false);
                        }
                    }
                });
            } else if (i == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
                Constants.isSync = false;
                try {
                    OfflineManager.getAuthorizations(context);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
//                ConstantsUtils.serviceReSchedule(context, true);
                ConstantsUtils.startAutoSync(context,false);
                Constants.setSyncTime(context,refguid.toString().toUpperCase());
                if (iCustomerViewPresenter != null) {
                    iCustomerViewPresenter.hideProgressDialog();
                    iCustomerViewPresenter.customersListSync();
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false);
                }
            }
        }
    }

    @Override
    public void onFilter() {
        if (iCustomerViewPresenter != null) {
            iCustomerViewPresenter.openFilter(filterType, statusId, delvStatusId);
        }
    }

    @Override
    public void onSearch(String searchText) {
        if (!this.searchText.equalsIgnoreCase(searchText)) {
            this.searchText = searchText;
            onSearchQuery(searchText);
        }
    }

    @Override
    public void onRefresh() {
        onRefreshRetailerList();
    }

    @Override
    public void startFilter(int requestCode, int resultCode, Intent data) {
        filterType = data.getStringExtra(DateFilterFragment.EXTRA_DEFAULT);
        statusId = data.getStringExtra(CustomersFilterActivity.EXTRA_INVOICE_STATUS);
        statusName = data.getStringExtra(CustomersFilterActivity.EXTRA_INVOICE_STATUS_NAME);
        delvStatusId = data.getStringExtra(CustomersFilterActivity.EXTRA_INVOICE_GR_STATUS);
        delvStatusName = data.getStringExtra(CustomersFilterActivity.EXTRA_INVOICE_GR_STATUS_NAME);
//        requestSOList(startDate, endDate);
        displayFilterType();
    }

    private void displayFilterType() {
        try {
            String statusDesc = "";
            if (!TextUtils.isEmpty(statusId)) {
                statusDesc = ", " + statusName;
            }
            if (!TextUtils.isEmpty(delvStatusId)) {
                statusDesc = statusDesc + ", " + delvStatusName;
            }
            if (iCustomerViewPresenter != null) {
                iCustomerViewPresenter.setFilterDate(statusDesc);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadAsyncTask() {
        retailerArrayList.clear();
        new ProspectCustomerAsyncTask(countPerRequest, 0).execute();
    }

    @Override
    public void sendResult(final MTPHeaderBean mtpResultHeaderBean, final MTPHeaderBean mtpHeaderBean, final boolean isAsmLogin) {
        if (iCustomerViewPresenter != null) {
            iCustomerViewPresenter.showProgressDialog();
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
            final String rollType = sharedPreferences.getString(Constants.USERROLE, "");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final ArrayList<MTPRoutePlanBean> mtpRoutePlanList = new ArrayList<>();
                    String qryPartnerFunctions = "";
                    String rolecprt = "";
                    if (!TextUtils.isEmpty(fromWhere) && fromWhere.equals(ConstantsUtils.MTP_APPROVAL) && !TextUtils.isEmpty(initiator)) {
                        qryPartnerFunctions = Constants.CustomerPartnerFunctions + "?$filter=PartnerVendorNo eq '" + initiator + "'";

                        if (!TextUtils.isEmpty(qryPartnerFunctions)) {
                            try {
                                rolecprt = OfflineManager.getRoleTYpe(qryPartnerFunctions);
                            } catch (OfflineODataStoreException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                            rolecprt = rollType;
                    }

                    if (!retailerArrayList.isEmpty()) {
                        for (CustomerBean customerBean : retailerArrayList) {
                            if (customerBean.isChecked()) {
                                MTPRoutePlanBean mtpRoutePlanBean = new MTPRoutePlanBean();
                                mtpRoutePlanBean.setVisitDate(mtpHeaderBean.getFullDate());
                                mtpRoutePlanBean.setDate(mtpHeaderBean.getDate());
                                mtpRoutePlanBean.setDay(mtpHeaderBean.getDay());
                                mtpRoutePlanBean.setRemarks(mtpResultHeaderBean.getRemarks());
                                mtpRoutePlanBean.setActivityDec(mtpResultHeaderBean.getActivityDec());
                                mtpRoutePlanBean.setActivityId(mtpResultHeaderBean.getActivityID());
//                                if (isAsmLogin) {

                                if (!TextUtils.isEmpty(fromWhere) && fromWhere.equals(ConstantsUtils.MTP_APPROVAL)) {
                                    try {
                                    if (rolecprt.equalsIgnoreCase("Z1") || rolecprt.equalsIgnoreCase("Z3")) {
                                        mtpRoutePlanBean.setSalesDistrict(customerBean.getCustomerId());
                                        mtpRoutePlanBean.setSalesDistrictDesc(customerBean.getCustomerName());
                                    } else {
                                        mtpRoutePlanBean.setCustomerNo(customerBean.getCustomerId());
                                        mtpRoutePlanBean.setCustomerName(customerBean.getCustomerName());
                                        mtpRoutePlanBean.setAddress(customerBean.getAddress1());
                                        mtpRoutePlanBean.setPostalCode(customerBean.getPostalCode());
                                        mtpRoutePlanBean.setMobile1(customerBean.getMobile1());
                                    }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }else {
                                    if (rollType.equalsIgnoreCase("Z1") || rollType.equalsIgnoreCase("Z3")) {
                                        mtpRoutePlanBean.setSalesDistrict(customerBean.getCustomerId());
                                        mtpRoutePlanBean.setSalesDistrictDesc(customerBean.getCustomerName());
                                    } else {
                                        mtpRoutePlanBean.setCustomerNo(customerBean.getCustomerId());
                                        mtpRoutePlanBean.setCustomerName(customerBean.getCustomerName());
                                        mtpRoutePlanBean.setAddress(customerBean.getAddress1());
                                        mtpRoutePlanBean.setPostalCode(customerBean.getPostalCode());
                                        mtpRoutePlanBean.setMobile1(customerBean.getMobile1());
                                    }
                                }
                                mtpRoutePlanBean.setRouteSchGUID(mtpHeaderBean.getRouteSchGUID());
                                mtpRoutePlanBean.setRouteSchPlanGUID(customerBean.getRouteSchPlanGUID());
                                mtpRoutePlanList.add(mtpRoutePlanBean);
                            }
                        }
                    }
                    if (mtpRoutePlanList.isEmpty() && !mtpResultHeaderBean.getActivityID().equalsIgnoreCase("01")) {
                        MTPRoutePlanBean mtpRoutePlanBean = new MTPRoutePlanBean();
                        mtpRoutePlanBean.setVisitDate(mtpHeaderBean.getFullDate());
                        mtpRoutePlanBean.setDate(mtpHeaderBean.getDate());
                        mtpRoutePlanBean.setDay(mtpHeaderBean.getDay());
                        mtpRoutePlanBean.setRemarks(mtpResultHeaderBean.getRemarks());
                        mtpRoutePlanBean.setActivityDec(mtpResultHeaderBean.getActivityDec());
                        mtpRoutePlanBean.setActivityId(mtpResultHeaderBean.getActivityID());
                        if (!mtpHeaderBean.getMTPRoutePlanBeanArrayList().isEmpty())
                            mtpRoutePlanBean.setRouteSchPlanGUID(mtpHeaderBean.getMTPRoutePlanBeanArrayList().get(0).getRouteSchPlanGUID());
                        mtpRoutePlanList.add(mtpRoutePlanBean);
                    }
                    if (!mtpRoutePlanList.isEmpty()) {
//                        if (isAsmLogin) {
                        if (!TextUtils.isEmpty(fromWhere) && fromWhere.equals(ConstantsUtils.MTP_APPROVAL)) {
                            try {
                                if (rolecprt.equalsIgnoreCase("Z1") || rolecprt.equalsIgnoreCase("Z3")) {
                                    if (mtpRoutePlanList.size() == 1) {
                                        mtpResultHeaderBean.setSalesDistrictDisc(mtpRoutePlanList.get(0).getSalesDistrictDesc());
                                    } else {
                                        mtpResultHeaderBean.setSalesDistrictDisc(mtpRoutePlanList.get(0).getSalesDistrictDesc() + "...");
                                    }
                                    mtpResultHeaderBean.setSalesDistrict(mtpRoutePlanList.get(0).getSalesDistrict());
                                } else {
                                    if (mtpRoutePlanList.size() == 1) {
                                        mtpResultHeaderBean.setCustomerName(mtpRoutePlanList.get(0).getCustomerName());
                                    } else {
                                        mtpResultHeaderBean.setCustomerName(mtpRoutePlanList.get(0).getCustomerName() + "...");
                                    }
                                    mtpResultHeaderBean.setCustomerNo(mtpRoutePlanList.get(0).getCustomerNo());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else{
                        if (rollType.equalsIgnoreCase("Z1") || rollType.equalsIgnoreCase("Z3")) {
                            if (mtpRoutePlanList.size() == 1) {
                                mtpResultHeaderBean.setSalesDistrictDisc(mtpRoutePlanList.get(0).getSalesDistrictDesc());
                            } else {
                                mtpResultHeaderBean.setSalesDistrictDisc(mtpRoutePlanList.get(0).getSalesDistrictDesc() + "...");
                            }
                            mtpResultHeaderBean.setSalesDistrict(mtpRoutePlanList.get(0).getSalesDistrict());
                        } else {
                            if (mtpRoutePlanList.size() == 1) {
                                mtpResultHeaderBean.setCustomerName(mtpRoutePlanList.get(0).getCustomerName());
                            } else {
                                mtpResultHeaderBean.setCustomerName(mtpRoutePlanList.get(0).getCustomerName() + "...");
                            }
                            mtpResultHeaderBean.setCustomerNo(mtpRoutePlanList.get(0).getCustomerNo());
                        }
                    }
                    }

                    mtpResultHeaderBean.setMTPRoutePlanBeanArrayList(mtpRoutePlanList);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mtpRoutePlanList.isEmpty()) {
                                if (iCustomerViewPresenter != null) {
                                    iCustomerViewPresenter.hideProgressDialog();
                                    iCustomerViewPresenter.displayMsg("Select customer");
                                }
                            } else {
                                Intent intent = new Intent();
                                intent.putExtra(Constants.EXTRA_BEAN, mtpResultHeaderBean);
                                if (iCustomerViewPresenter != null) {
                                    iCustomerViewPresenter.hideProgressDialog();
                                    iCustomerViewPresenter.sendSelectedItem(intent);
                                }
                            }
                        }
                    });

                }
            }).start();

        }
    }

    @Override
    public void loadMTPCustomerList(final ArrayList<MTPRoutePlanBean> mtpRoutePlanBeanArrayList, final boolean isAsmLogin, final String externalID, final String comingFrom) {
        initiator = externalID;
        fromWhere = comingFrom;
        new Thread(new Runnable() {
            @Override
            public void run() {
               /* ArrayList<String> salesArea = null;
                try {
                    salesArea = OfflineManager.getSaleAreaFromUsrAth("UserProfileAuthSet?$filter=Application%20eq%20%27PD%27"+" &$orderby=AuthOrgTypeID asc");
                } catch (Exception e) {
                    e.printStackTrace();
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
                if(salesArearlist!=null && !salesArearlist.isEmpty()) {
                    for (int i = 0; i < salesArearlist.size();i++){
                        if(i==salesArearlist.size()-1) {
                            stringSaleArea = stringSaleArea + "SalesArea eq '" + salesArearlist.get(i)+"'";
                        }else {
                            stringSaleArea = stringSaleArea + "SalesArea eq '" + salesArearlist.get(i) + "' or ";
                        }
                    }
                }
                SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
                String rollType = sharedPreferences.getString(Constants.USERROLE, "");

                try {
                    String routeQry = "";
                    if (comingFrom.equals(ConstantsUtils.MTP_APPROVAL)) {
                        String qryPartnerFunctions = Constants.CustomerPartnerFunctions +"?$filter=PartnerVendorNo eq '"+externalID+"'";
                        String role=OfflineManager.getRoleTYpe(qryPartnerFunctions);
                        if (role.equalsIgnoreCase("Z1") || role.equalsIgnoreCase("Z3")) {
                            if (!TextUtils.isEmpty(stringSaleArea)) {
                                String CustomerQry  = getCustomerQry(externalID);
                                if (!TextUtils.isEmpty(CustomerQry)) {
                                    routeQry = Constants.CustomerSalesAreas + "?$filter=" + stringSaleArea + " and "+ CustomerQry+" &$select=SalesDistrictID,SalesDistrictDesc";
                                    System.out.println("CustomerSalesAreas 3"+routeQry);

                                    retailerArrayList = OfflineManager.getCustomerList(routeQry, mtpRoutePlanBeanArrayList);
                                } else {
                                    ((Activity) context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context, "Customers not mapped for this user", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                retailerArrayList = OfflineManager.getCustomerSalesAreaList(routeQry, mtpRoutePlanBeanArrayList);
                            } else {
//                            routeQry = Constants.CustomerSalesAreas + "?$select=SalesDistrictID,SalesDistrictDesc";
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Sales Area not maintained for this user", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
//                        retailerArrayList = OfflineManager.getCustomerSalesAreaList(routeQry, mtpRoutePlanBeanArrayList);
                        } else {
                                if (!TextUtils.isEmpty(stringSaleArea)) {
//                                    String customerSaleAreaQry = Constants.CustomerSalesAreas + "?$filter=" + stringSaleArea;
//                                    String CustomerQry = OfflineManager.getCustomerSalesAreaQry(customerSaleAreaQry);
                                    String CustomerQry  = getCustomerQry(externalID);
                                    if (!TextUtils.isEmpty(CustomerQry)) {
                                        routeQry = Constants.Customers + "?$filter=" + CustomerQry + " &$select=CustomerNo,Name,Address1,Address2,Address3,District,City,PostalCode,Mobile1,Currency &$orderby=" + Constants.RetailerName + "%20asc";
                                        retailerArrayList = OfflineManager.getCustomerList(routeQry, mtpRoutePlanBeanArrayList);
                                    } else {
                                        ((Activity) context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(context, "Customers not mapped for this user", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
//                            retailerArrayList = OfflineManager.getCustomerList(routeQry, mtpRoutePlanBeanArrayList);
                                } else {
//                            routeQry = Constants.Customers + "?$select=CustomerNo,Name,Address1,Address2,Address3,District,City,PostalCode,Mobile1,Currency &$orderby=" + Constants.RetailerName + "%20asc";
                                    ((Activity) context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context, "Sales Area not maintained for this user", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                        }
                    }else {
                        if (rollType.equalsIgnoreCase("Z1") || rollType.equalsIgnoreCase("Z3")) {
                            if (!TextUtils.isEmpty(stringSaleArea)) {
                                routeQry = Constants.CustomerSalesAreas + "?$filter=" + stringSaleArea + " &$select=SalesDistrictID,SalesDistrictDesc";
                                retailerArrayList = OfflineManager.getCustomerSalesAreaList(routeQry, mtpRoutePlanBeanArrayList);
                            } else {
//                            routeQry = Constants.CustomerSalesAreas + "?$select=SalesDistrictID,SalesDistrictDesc";
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Sales Area not maintained for this user", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
//                        retailerArrayList = OfflineManager.getCustomerSalesAreaList(routeQry, mtpRoutePlanBeanArrayList);
                        } else {
                            if (!TextUtils.isEmpty(stringSaleArea)) {
                                String customerSaleAreaQry = Constants.CustomerSalesAreas + "?$filter=" + stringSaleArea;
                                String CustomerQry = OfflineManager.getCustomerSalesAreaQry(customerSaleAreaQry);
                                if (!TextUtils.isEmpty(CustomerQry)) {
                                    routeQry = Constants.Customers + "?$filter=" + CustomerQry + " &$select=CustomerNo,Name,Address1,Address2,Address3,District,City,PostalCode,Mobile1,Currency &$orderby=" + Constants.RetailerName + "%20asc";
                                    retailerArrayList = OfflineManager.getCustomerList(routeQry, mtpRoutePlanBeanArrayList);
                                } else {
                                    ((Activity) context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context, "Customers not mapped for this user", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
//                            retailerArrayList = OfflineManager.getCustomerList(routeQry, mtpRoutePlanBeanArrayList);
                            } else {
//                            routeQry = Constants.Customers + "?$select=CustomerNo,Name,Address1,Address2,Address3,District,City,PostalCode,Mobile1,Currency &$orderby=" + Constants.RetailerName + "%20asc";
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Sales Area not maintained for this user", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
//                        retailerArrayList = OfflineManager.getCustomerList(routeQry, mtpRoutePlanBeanArrayList);
                        }
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                /*try {
                    if (isAsmLogin) {
                        String routeQry = Constants.CustomerSalesAreas + "?$select=SalesDistrictID,SalesDistrictDesc";
                        retailerArrayList = OfflineManager.getCustomerSalesAreaList(routeQry, mtpRoutePlanBeanArrayList);
                    } else {
                        String routeQry = Constants.Customers + "?$select=CustomerNo,Name,Address1,Address2,Address3,District,City,PostalCode,Mobile1,Currency &$orderby=" + Constants.RetailerName + "%20asc";
                        retailerArrayList = OfflineManager.getCustomerList(routeQry, mtpRoutePlanBeanArrayList);
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }*/
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iCustomerViewPresenter != null) {
                            iCustomerViewPresenter.searchResult(retailerArrayList);
                            iCustomerViewPresenter.hideProgressDialog();
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * Searc query to update the retailerList
     *
     * @param searchText
     */
    private void onSearchQuery(String searchText) {
        this.searchText = searchText;
        searchBeanArrayList.clear();
        boolean isCustomerID = false;
        boolean isCustomerName = false;
        boolean isCity = false;
        if (retailerArrayList != null) {
            if (TextUtils.isEmpty(searchText)) {
                searchBeanArrayList.addAll(retailerArrayList);
            } else {
                for (CustomerBean item : retailerArrayList) {
                    isCustomerID = false;
                    isCustomerName = false;
                    isCity = false;

                    if (!TextUtils.isEmpty(searchText)) {
                        isCustomerID = item.getCustomerId().toLowerCase().contains(searchText.toLowerCase());
                        isCustomerName = item.getCustomerName().toLowerCase().contains(searchText.toLowerCase());
                        isCity = item.getCity().toLowerCase().contains(searchText.toLowerCase());
                    } else {
                        isCustomerID = true;
                        isCustomerName = true;
                        isCity = true;
                    }
                    if (isCustomerID || isCustomerName || isCity)
                        searchBeanArrayList.add(item);
                }
            }
        }
        if (iCustomerViewPresenter != null) {
            iCustomerViewPresenter.searchResult(searchBeanArrayList);
        }
    }

    /**
     * refreshing the RetailerList Online
     */
    private void onRefreshRetailerList() {
        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(context)) {
            alAssignColl.clear();
            concatCollectionStr = "";
            alAssignColl.add(Constants.Customers);
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            for (int incVal = 0; incVal < alAssignColl.size(); incVal++) {
                if (incVal == 0 && incVal == alAssignColl.size() - 1) {
                    concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
                } else if (incVal == 0) {
                    concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
                } else if (incVal == alAssignColl.size() - 1) {
                    concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
                } else {
                    concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
                }
            }

            if (Constants.iSAutoSync) {
                if (iCustomerViewPresenter != null) {
                    iCustomerViewPresenter.hideProgressDialog();
                    iCustomerViewPresenter.displayMsg(context.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    // progressDialog = Constants.showProgressDialog(context, "", context.getString(R.string.msg_sync_progress_msg_plz_wait));
                    refguid = GUID.newRandom();
                    Constants.updateStartSyncTime(context,Constants.AdVst_sync,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (iCustomerViewPresenter != null) {
                        iCustomerViewPresenter.hideProgressDialog();
                        iCustomerViewPresenter.displayMsg(e.getMessage());
                    }
                }
            }
        } else {
            if (iCustomerViewPresenter != null) {
                iCustomerViewPresenter.hideProgressDialog();
                iCustomerViewPresenter.displayMsg(context.getString(R.string.no_network_conn));
            }
        }
    }

    @Override
    public void loadRTGSCustomerList(final ArrayList<WeekDetailsList> rtgsBeanArrayList,final String ExternalRefID) {
        if (iCustomerViewPresenter != null) {
            iCustomerViewPresenter.showProgressDialog();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String routeQry = "";
                    if(ExternalRefID.equalsIgnoreCase("")){
                        routeQry = Constants.Customers + "?$select=CustomerNo,Name,Address1,Address2,Address3,District,City,PostalCode,Mobile1,Currency &$orderby=Name asc";
                    }else{
                        String mCPQry = getCustomerQry(ExternalRefID);
                        if(!mCPQry.equalsIgnoreCase("")){
                            routeQry = Constants.Customers + "?$select=CustomerNo,Name,Address1,Address2,Address3,District,City,PostalCode,Mobile1,Currency &$filter=("+mCPQry+") &$orderby=Name asc";
                        }
                    }


                    try {
                        retailerArrayList = OfflineManager.getCustomerListRTGS(routeQry, rtgsBeanArrayList);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (iCustomerViewPresenter != null) {
                                iCustomerViewPresenter.searchResult(retailerArrayList);
                                iCustomerViewPresenter.hideProgressDialog();
                            }
                        }
                    });
                }
            }).start();
        }

    }

    public static String getCustomerQry(String externalRefID){
        String custPartnerQry = Constants.CustomerPartnerFunctions + "?$select=CustomerNo &$filter= "+Constants.PartnerVendorNo+" eq '"+externalRefID+"' ";

        String mStrCustomerQry = "";
        try {
            mStrCustomerQry = OfflineManager.makeCustomerQryFromCustomerPartnerFunc(custPartnerQry, Constants.CustomerNo);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return mStrCustomerQry;
    }


    @Override
    public void loadRTGSList(final ArrayList<WeekDetailsList> rtgsBeanArrayList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<CustomerBean> retailerArrayList = new ArrayList<>();
                final ArrayList<String> customeDuplicateList = new ArrayList<>();
                Collections.sort(rtgsBeanArrayList, new Comparator<WeekDetailsList>() {
                    public int compare(WeekDetailsList one, WeekDetailsList other) {
                        return one.getcPNo().compareTo(other.getcPNo());
                    }
                });

                boolean checkValue = false;
                int value = 0;
                try {

                    boolean isAdd=false;
                    for (int i = 0; i < rtgsBeanArrayList.size(); i++) {
                        WeekDetailsList mergeSameCpno = new WeekDetailsList();
                        CustomerBean customerBean = new CustomerBean("");
                        isAdd = false;

                        customerBean.setRouteSchPlanGUID(rtgsBeanArrayList.get(i).getCollectionPlanItemGUID());
//                            customerBean.setAmount(weekHeaderList.getPlannedValue());
                            /*if (!weekHeaderList.getPlannedValue().equalsIgnoreCase("0")) {
                                customerBean.setAmount(weekHeaderList.getPlannedValue());
                            } else if (!weekHeaderList.getPlannedValue2().equalsIgnoreCase("0")) {
                                customerBean.setAmount1(weekHeaderList.getPlannedValue2());
                            }*/
                        try {
                            customerBean.setActualAmount(rtgsBeanArrayList.get(i).getAchievedValue());
                        } catch (Exception e) {
                            customerBean.setActualAmount("0.00");
                            e.printStackTrace();
                        }
                        customerBean.setCurrency(rtgsBeanArrayList.get(i).getCurrency());
//                            customerBean.setRemarks(weekHeaderList.getRemarks());
                        customerBean.setCustomerId(rtgsBeanArrayList.get(i).getcPNo());
                        customerBean.setCustomerName(rtgsBeanArrayList.get(i).getcPName());
//                        customerBean.setRouteSchPlanGUID(rtgsBeanArrayList.get(i).getCollectionPlanItemGUID());
                        customerBean.setCollPlanHeaderGUID(rtgsBeanArrayList.get(i).getCollectionPlanGUID());
                        customerBean.setCustomerType(rtgsBeanArrayList.get(i).getcPType());

                        for (int j = 0; j < rtgsBeanArrayList.size(); j++) {
                            if (rtgsBeanArrayList.get(i).getcPNo().equalsIgnoreCase(rtgsBeanArrayList.get(j).getcPNo())) {
                                if (!customeDuplicateList.contains(rtgsBeanArrayList.get(i).getcPNo())) {
                                    isAdd = true;


                                   /* ArrayList<String> salesArea = null;
                                    try {
                                        salesArea = OfflineManager.getSaleAreaFromUsrAth("UserProfileAuthSet?$filter=Application%20eq%20%27PD%27" + " &$orderby=AuthOrgTypeID asc");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }*/


                                    ArrayList<String> salesArea = new ArrayList<>();
                                    try {
                                        salesArea = OfflineManager.
                                                getSalesArea("UserProfileAuthSet?$filter=Application%20eq%20%27PD%27" + " &$orderby=AuthOrgTypeID asc");
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

                                    for (int m = 0; m < distributorChannel.size(); m++) {
                                        for (int n = 0; n < salesArea.size(); n++) {
                                            String saleArea[] = salesArea.get(n).split("/");

                                            salesArearlist.add(saleArea[0] + "/" + distributorChannel.get(m) + "/" + saleArea[1]);

                                            System.out.println("Sales Area List : " + saleArea[0] + "/" + distributorChannel.get(m) + "/" + saleArea[1]);

                                        }

                                    }
                                    String stringSaleArea = "";
                                    if (salesArearlist != null && !salesArearlist.isEmpty()) {
                                        for (int z = 0; z < salesArearlist.size(); z++) {
                                            if (z == salesArearlist.size() - 1) {
                                                stringSaleArea = stringSaleArea + "SalesArea eq '" + salesArearlist.get(z) + "'";
                                            } else {
                                                stringSaleArea = stringSaleArea + "SalesArea eq '" + salesArearlist.get(z) + "' or ";
                                            }
                                        }
                                    }
                                    if (!TextUtils.isEmpty(stringSaleArea)) {
                                        String qry = Constants.CustomerSalesAreas + "?$filter=" + Constants.CustomerNo + " eq '" + customerBean.getCustomerId() + "' and (" + stringSaleArea + ")";

                                        System.out.println("CustomerSalesAreas 4"+qry);

                                        ArrayList<SaleAreaBean> sortList =OfflineManager.getSaleAreaFromCustomerCreditLmt(qry);

                                        Collections.sort(sortList, new Comparator<SaleAreaBean>() {
                                            public int compare(SaleAreaBean one, SaleAreaBean other) {
                                                return one.getCreditControlAreaID().compareTo(other.getCreditControlAreaID());
                                            }
                                        });
                                        customerBean.setSaleAreaBeanAl(sortList);
                                    }
//                                if (!weekHeaderList.getPlannedValue2().equalsIgnoreCase("0")) {
//                                    if(!checkValue) {


//                                    if (rtgsBeanArrayList.get(j).getCrdtCtrlArea().equalsIgnoreCase("1010")) {

                                        customerBean.setAmount(rtgsBeanArrayList.get(j).getPlannedValue());
                                        customerBean.setRemarks(rtgsBeanArrayList.get(j).getRemarks());
//                                    }else if (rtgsBeanArrayList.get(j).getCrdtCtrlArea().equalsIgnoreCase("1030")){
                                        customerBean.setAmount1(rtgsBeanArrayList.get(j).getPlannedValue2());
                                        customerBean.setRemarks1(rtgsBeanArrayList.get(j).getRemarks2());
                                    customerBean.setRouteSchPlanGUID(rtgsBeanArrayList.get(i).getCollectionPlanItemGUID());
                                    customerBean.setRouteSchPlanGUID1(rtgsBeanArrayList.get(i).getCollectionPlanItemGUID1());
//                                    }






                                  /*
                                    if (rtgsBeanArrayList.get(j).getCrdtCtrlArea().equalsIgnoreCase("1010")) {
                                        mergeSameCpno = rtgsBeanArrayList.get(j);
                                        mergeSameCpno.setCrdtCtrlArea1(rtgsBeanArrayList.get(j).getCrdtCtrlArea());
                                        //  alWeakList.add(weekDetailsLists.get(i));
                                    } else if (rtgsBeanArrayList.get(j).getCrdtCtrlArea().equalsIgnoreCase("1030")) {
                                        mergeSameCpno.setCrdtCtrlArea2(rtgsBeanArrayList.get(j).getCrdtCtrlArea());
                                        mergeSameCpno.setRemarks2(rtgsBeanArrayList.get(j).getRemarks2());
                                        mergeSameCpno.setPlannedValue2(rtgsBeanArrayList.get(j).getPlannedValue2());
                                    }*/
                                    customeDuplicateList.add(rtgsBeanArrayList.get(i).getcPNo());
                                }else{
                                    if (customerBean.getAmount1().equalsIgnoreCase("0")) {
                                        customerBean.setAmount1(rtgsBeanArrayList.get(j).getPlannedValue2());
                                    }
                                    if (customerBean.getRouteSchPlanGUID().equalsIgnoreCase("")) {
                                        customerBean.setRouteSchPlanGUID(rtgsBeanArrayList.get(j).getCollectionPlanItemGUID());
                                    }
                                    if (customerBean.getRouteSchPlanGUID1().equalsIgnoreCase("")) {
                                        customerBean.setRouteSchPlanGUID1(rtgsBeanArrayList.get(j).getCollectionPlanItemGUID1());
                                    }
                                    if (customerBean.getAmount().equalsIgnoreCase("0")) {
                                        customerBean.setAmount(rtgsBeanArrayList.get(j).getPlannedValue());
                                    }
                                    if (customerBean.getRemarks().equalsIgnoreCase("")) {
                                        customerBean.setRemarks(rtgsBeanArrayList.get(j).getRemarks());
                                    }
                                    if (customerBean.getRemarks1().equalsIgnoreCase("")) {
                                        customerBean.setRemarks1(rtgsBeanArrayList.get(j).getRemarks2());
                                    }
                                }

                                // write else
                            }

                        }
                        if (isAdd) {
                            retailerArrayList.add(customerBean);

                        }
                    }


                    /*

                    if (rtgsBeanArrayList != null && rtgsBeanArrayList.size() > 0) {
                        for (int j = 0;j<rtgsBeanArrayList.size();j++) {
//                            checkValue = false;
                            value = value+j;
                            WeekDetailsList weekHeaderList = rtgsBeanArrayList.get(j);
                            CustomerBean customerBean = new CustomerBean("");
                            customerBean.setRouteSchPlanGUID(weekHeaderList.getCollectionPlanItemGUID());
//                            customerBean.setAmount(weekHeaderList.getPlannedValue());
                            *//*if (!weekHeaderList.getPlannedValue().equalsIgnoreCase("0")) {
                                customerBean.setAmount(weekHeaderList.getPlannedValue());
                            } else if (!weekHeaderList.getPlannedValue2().equalsIgnoreCase("0")) {
                                customerBean.setAmount1(weekHeaderList.getPlannedValue2());
                            }*//*
                            try {
                                customerBean.setActualAmount(weekHeaderList.getAchievedValue());
                            } catch (Exception e) {
                                customerBean.setActualAmount("0.00");
                                e.printStackTrace();
                            }
                            customerBean.setCurrency(weekHeaderList.getCurrency());
//                            customerBean.setRemarks(weekHeaderList.getRemarks());
                            customerBean.setCustomerId(weekHeaderList.getcPNo());
                            customerBean.setCustomerName(weekHeaderList.getcPName());
                            customerBean.setRouteSchPlanGUID(weekHeaderList.getCollectionPlanItemGUID());
                            customerBean.setCollPlanHeaderGUID(weekHeaderList.getCollectionPlanGUID());
                            customerBean.setCustomerType(weekHeaderList.getcPType());
//
                            if(!customeDuplicateList.contains(weekHeaderList.getcPNo())) {

                                ArrayList<String> salesArea = null;
                                try {
                                    salesArea = OfflineManager.getSaleAreaFromUsrAth("UserProfileAuthSet?$filter=Application%20eq%20%27PD%27" + " &$orderby=AuthOrgTypeID asc");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                String stringSaleArea = "";
                                if (salesArea != null && !salesArea.isEmpty()) {
                                    for (int i = 0; i < salesArea.size(); i++) {
                                        if (i == salesArea.size() - 1) {
                                            stringSaleArea = stringSaleArea + "SalesArea eq '" + salesArea.get(i) + "'";
                                        } else {
                                            stringSaleArea = stringSaleArea + "SalesArea eq '" + salesArea.get(i) + "' or ";
                                        }
                                    }
                                }
                                if (!TextUtils.isEmpty(stringSaleArea)) {
                                    String qry = Constants.CustomerSalesAreas + "?$filter=" + Constants.CustomerNo + " eq '" + customerBean.getCustomerId() + "' and (" + stringSaleArea + ")";
                                   ArrayList<SaleAreaBean> sortList =OfflineManager.getSaleAreaFromCustomerCreditLmt(qry);
                                           Collections.sort(sortList, new Comparator<SaleAreaBean>() {
                                               public int compare(SaleAreaBean one, SaleAreaBean other) {
                                                   return one.getCreditControlAreaID().compareTo(other.getCreditControlAreaID());
                                               }
                                           });
                                    customerBean.setSaleAreaBeanAl(sortList);
                                }
//                                if (!weekHeaderList.getPlannedValue2().equalsIgnoreCase("0")) {
//                                    if(!checkValue) {

                                    customerBean.setAmount(weekHeaderList.getPlannedValue());
                                    customerBean.setRemarks(weekHeaderList.getRemarks());

                                    customerBean.setAmount1(weekHeaderList.getPlannedValue2());
                                    customerBean.setRemarks1(weekHeaderList.getRemarks2());



//                                    }
//                                }
                                retailerArrayList.add(customerBean);
                                customeDuplicateList.add(weekHeaderList.getcPNo());
                            }else {
                                CustomerBean customerBean1 = retailerArrayList.get(value - 1);
                                *//*if (!weekHeaderList.getPlannedValue().equalsIgnoreCase("0")) {
                                    customerBean1.setAmount(weekHeaderList.getPlannedValue());
                                } else*//*
                                if (customerBean1.getAmount1().equalsIgnoreCase("0")) {
                                    customerBean1.setAmount1(weekHeaderList.getPlannedValue2());
                                }
                                if (customerBean1.getAmount().equalsIgnoreCase("0")) {
                                    customerBean1.setAmount(weekHeaderList.getPlannedValue());
                                }
                                if (customerBean1.getRemarks().equalsIgnoreCase("")) {
                                    customerBean1.setRemarks(weekHeaderList.getRemarks());
                                }
                                if (customerBean1.getRemarks1().equalsIgnoreCase("")) {
                                    customerBean1.setRemarks1(weekHeaderList.getRemarks2());
                                }
                                retailerArrayList.set(value - 1, customerBean1);
                            }
                        }
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iCustomerViewPresenter != null) {
                            iCustomerViewPresenter.searchResult(retailerArrayList);
                            iCustomerViewPresenter.hideProgressDialog();
                        }
                    }
                });

            }
        }).start();
    }

    @Override
    public void sendResultRTGS(final WeekHeaderList mtpResultHeaderBean, final WeekHeaderList mtpHeaderBean, final ArrayList<CustomerBean> selCustomers) {
        if (iCustomerViewPresenter != null) {
            iCustomerViewPresenter.showProgressDialog();
            new Thread(new Runnable() {
                @Override
                public void run() {

                    final ArrayList<WeekDetailsList> rtgsWeekItemList = new ArrayList<>();
                    if (!selCustomers.isEmpty()) {
                        isValidData = true;
                        mStrCustName = "";
                        double mTotalAmt = 0.0;
                        for (final CustomerBean customerBean : selCustomers) {
                            /*String divisionDesc="";
                            String divisionDesc1="";
                            if(customerBean.getSaleAreaBeanAl()!=null && !customerBean.getSaleAreaBeanAl().isEmpty()){
                                if(customerBean.getSaleAreaBeanAl().size()==1){
                                    if(customerBean.getSaleAreaBeanAl().get(0).getCreditControlAreaID().equalsIgnoreCase("01")){
                                        divisionDesc = customerBean.getSaleAreaBeanAl().get(0).getCreditControlAreaDesc();
                                    }else if(customerBean.getSaleAreaBeanAl().get(0).getCreditControlAreaID().equalsIgnoreCase("10")){
                                        divisionDesc1 = customerBean.getSaleAreaBeanAl().get(0).getCreditControlAreaDesc();
                                    }
                                } else if(customerBean.getSaleAreaBeanAl().size()==2){
                                        divisionDesc = customerBean.getSaleAreaBeanAl().get(0).getCreditControlAreaDesc();
                                        divisionDesc1 = customerBean.getSaleAreaBeanAl().get(1).getCreditControlAreaDesc();
                                }
                            }*/
                            double mdoubAmt = 0.0;
                            double mdoubAmt1=0.0;
                            try {
                                if (!TextUtils.isEmpty(customerBean.getAmount())) {
                                    mdoubAmt= Double.parseDouble(customerBean.getAmount());
                                }
                                if (!TextUtils.isEmpty(customerBean.getAmount1())) {
                                    mdoubAmt1= Double.parseDouble(customerBean.getAmount1());
                                }
                            } catch (NumberFormatException e) {
                                mdoubAmt = 0.0;
                                mdoubAmt1 = 0.0;
                                e.printStackTrace();
                            }

                            if(mdoubAmt==0&&!TextUtils.isEmpty(customerBean.getRemarks())){
//                                final String finalDivisionDesc = divisionDesc;
                                ((Activity)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        iCustomerViewPresenter.errorMsgEditText(context.getString(R.string.amount_errror));
                                    }
                                });
                                return;
                            }
                            if(mdoubAmt1==0&&!TextUtils.isEmpty(customerBean.getRemarks1())){
//                                final String finalDivisionDesc1 = divisionDesc1;
                                ((Activity)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        iCustomerViewPresenter.errorMsgEditText1(context.getString(R.string.amount_errror));
                                    }
                                });
                                return;
                            }
                            if (mdoubAmt > 0 || mdoubAmt1>0) {
                                mTotalAmt = mTotalAmt + mdoubAmt + mdoubAmt1;
                                WeekDetailsList mtpRoutePlanBean = new WeekDetailsList();
                                mtpRoutePlanBean.setVisitDate(mtpHeaderBean.getFullDate());
                                mtpRoutePlanBean.setDate(mtpHeaderBean.getDate());
                                mtpRoutePlanBean.setDay(mtpHeaderBean.getDay());
                                mtpRoutePlanBean.setRemarks(mtpResultHeaderBean.getRemarks());
                                mtpRoutePlanBean.setcPNo(customerBean.getCustomerId());
                                mtpRoutePlanBean.setcPName(customerBean.getCustomerName());
                                mtpRoutePlanBean.setcPType(customerBean.getCustomerType());
                                mtpRoutePlanBean.setPlannedValue(customerBean.getAmount());
                                mtpRoutePlanBean.setPlannedValue2(customerBean.getAmount1());
                                mtpRoutePlanBean.setRemarks(customerBean.getRemarks());
                                mtpRoutePlanBean.setRemarks2(customerBean.getRemarks1());
                                mtpRoutePlanBean.setCurrency(customerBean.getCurrency());
                                mtpRoutePlanBean.setCollectionPlanGUID(mtpHeaderBean.getCollectionPlanGUID());
                                mtpRoutePlanBean.setCollectionPlanItemGUID(customerBean.getRouteSchPlanGUID());
                                mtpRoutePlanBean.setCollectionPlanItemGUID1(customerBean.getRouteSchPlanGUID1());
                                mtpRoutePlanBean.setCreatedBy(mtpHeaderBean.getCreatedBy());
                                mtpRoutePlanBean.setCreatedOn(mtpHeaderBean.getCreatedOn());
                                mtpRoutePlanBean.setSaleAreaDetailsBean(customerBean.getSaleAreaBeanAl());
                                rtgsWeekItemList.add(mtpRoutePlanBean);
                            } else {
                                mStrCustName = customerBean.getCustomerName();
                                isValidData = false;
                                break;
                            }

                        }
                        if (isValidData) {
                            if (!rtgsWeekItemList.isEmpty()) {
                                if (rtgsWeekItemList.size() == 1) {
                                    mtpResultHeaderBean.setName(rtgsWeekItemList.get(0).getCPName());
                                } else {
                                    mtpResultHeaderBean.setName(rtgsWeekItemList.get(0).getCPName() + "...");
                                }
                                mtpResultHeaderBean.setCustNo(rtgsWeekItemList.get(0).getCPNo());
                                mtpResultHeaderBean.setCurrency(rtgsWeekItemList.get(0).getCurrency());
                                mtpResultHeaderBean.setTotalAmount(mTotalAmt + "");
                            }
                        }
                    }
                    mtpResultHeaderBean.setWeekDetailsLists(rtgsWeekItemList);


                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (selCustomers.isEmpty()) {
                                if (iCustomerViewPresenter != null) {
                                    iCustomerViewPresenter.hideProgressDialog();
                                    iCustomerViewPresenter.displayMsg("Select atleast one customer");
                                }
                            } else {
                                if (isValidData) {
                                    Intent intent = new Intent();
                                    intent.putExtra(Constants.EXTRA_BEAN, mtpResultHeaderBean);
                                    if (iCustomerViewPresenter != null) {
                                        iCustomerViewPresenter.hideProgressDialog();
                                        iCustomerViewPresenter.sendSelectedItem(intent);
                                    }
                                } else {
                                    if (iCustomerViewPresenter != null) {
                                        iCustomerViewPresenter.hideProgressDialog();
                                        iCustomerViewPresenter.displayMsg("Enter Amount for " + mStrCustName + "");
                                    }
                                }

                            }
                        }
                    });

                }
            }).start();

        }
    }

    private class ProspectCustomerAsyncTask extends AsyncTask<Void, Void, Void> {
        private int top = 0;
        private int skip = 0;
        private ArrayList<CustomerBean> retailerTempArrayList = new ArrayList<>();

        ProspectCustomerAsyncTask(int top, int skip) {
            this.top = top;
            this.skip = skip;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (skip == 0) {
                if (iCustomerViewPresenter != null) {
                    iCustomerViewPresenter.showProgressDialog();
                }
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String routeQry = "";
                if (TextUtils.isEmpty(salesDistrictId)) {
                    routeQry = Constants.Customers + "?$select=CustomerNo,Name,Address1,Address2,Address3,District,City,PostalCode,Mobile1,Currency &$orderby=Name asc";
                } else {
                    routeQry = Constants.CustomerSalesAreas + "?$select=CustomerNo &$filter=SalesDistrictID eq '" + salesDistrictId + "'";
                    routeQry = OfflineManager.getCustomerQry(routeQry);
                }
                if (!TextUtils.isEmpty(routeQry)) {
                    routeQry = routeQry + "&$skip=" + skip + "&$top=" + top + "";
                }
                retailerTempArrayList = OfflineManager.getCustomerList(routeQry);
                retailerArrayList.addAll(retailerTempArrayList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            onSearchQuery(searchText);
            if (top == retailerTempArrayList.size()) {
                int skipVal = skip + top;
                new ProspectCustomerAsyncTask(countPerRequest, skipVal).execute();
            } else {
                if (iCustomerViewPresenter != null) {
                    iCustomerViewPresenter.hideProgressDialog();
                }
            }

        }
    }
}
