package com.rspl.sf.msfa.reports.salesorder.header;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.rspl.sf.msfa.BuildConfig;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.asyncTask.RefreshAsyncTask;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.filter.DateFilterFragment;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.mbo.SalesOrderBean;
import com.rspl.sf.msfa.reports.salesorder.filter.SOFilterActivity;
import com.rspl.sf.msfa.solist.SOListBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataPayload;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;
import com.sap.smp.client.odata.store.ODataResponseSingle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by e10847 on 07-12-2017.
 */

public class SalesOrderHeaderListPresenter implements ISalesOrderHeaderListPresenter, UIListener,OnlineODataInterface {
    // private ProgressDialog progressDialog = null;
    ArrayList<String> alAssignColl = null;
    private String startDate = "";
    private String endDate = "";
    private String filterType = "";
    private Activity context = null;
    private String customerNumber = "";
    private ArrayList<SalesOrderBean> salesOrderHeaderArrayList = null;
    private ArrayList<SalesOrderBean> searchBeanArrayList = null;
    private com.rspl.sf.msfa.reports.salesorder.header.ISalesOrderListView ISalesOrderListView = null;
    private String searchText = "";
    private String delvStatusId = "";
    private String statusId = "";
    private String statusName = "";
    private String delvStatusName = "";
    private long refreshTime = 0;
    private boolean isMaterialEnabled = false;
    private boolean isErrorFromBackend = false;
    private View view = null;
    private String status = "";
    private GUID refguid =null;

    public SalesOrderHeaderListPresenter(Activity context, String customerNumber, com.rspl.sf.msfa.reports.salesorder.header.ISalesOrderListView ISalesOrderListView, boolean isMaterialEnabled, View view) {
        this.context = context;
        this.customerNumber = customerNumber;
        this.ISalesOrderListView = ISalesOrderListView;
        this.salesOrderHeaderArrayList = new ArrayList<>();
        this.isMaterialEnabled = isMaterialEnabled;
        this.view = view;
        this.searchBeanArrayList = new ArrayList<>();
    }

    @Override
    public void connectToOfflineDB() {
        if (ISalesOrderListView != null) {
            ISalesOrderListView.showProgressDialog();
        }
//        new GetSalesOrderAsyncTask(isMaterialEnabled).execute();
        if (isMaterialEnabled) {
            getSOList(statusId, delvStatusId);
        } else {
            getSalesOrderList();
        }
        //  deleteSOPOstedDataVaultRecords();
    }

  /*  private void deleteSOPOstedDataVaultRecords() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }*/

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        ISalesOrderListView = null;
    }

    @Override
    public void onResume() {
        if (ISalesOrderListView != null) {
            if (refreshTime != 0)
                ISalesOrderListView.displayRefreshTime(ConstantsUtils.getLastSeenDateFormat(context, refreshTime));
        }
    }

    @Override
    public void onFilter() {
        if (ISalesOrderListView != null) {
            ISalesOrderListView.openFilter(startDate, endDate, filterType, statusId, delvStatusId);
        }
    }

    @Override
    public void onSearch(String searchText) {
        if (!this.searchText.equalsIgnoreCase(searchText)) {
            this.searchText = searchText;
            onSearch(searchText, statusId, delvStatusId);
        }
    }

    private void onSearch(String searchText, String soStatus, String delivetyType) {
        this.searchText = searchText;
        searchBeanArrayList.clear();
        boolean soTypeStatus = false;
        boolean soDelStatus = false;
        boolean soSearchStatus = false;
        if (salesOrderHeaderArrayList != null) {
            if (TextUtils.isEmpty(searchText) && TextUtils.isEmpty(soStatus) && (TextUtils.isEmpty(delivetyType) || delivetyType.equalsIgnoreCase(Constants.All))) {
                searchBeanArrayList.addAll(salesOrderHeaderArrayList);
            } else {
                for (SalesOrderBean item : salesOrderHeaderArrayList) {
                    soTypeStatus = false;
                    soDelStatus = false;
                    soSearchStatus = false;
                    if (!TextUtils.isEmpty(soStatus)) {
                        soTypeStatus = item.getStatusID().toLowerCase().contains(soStatus.toLowerCase());
                    } else {
                        soTypeStatus = true;
                    }
                    if (!TextUtils.isEmpty(delivetyType) && !delivetyType.equalsIgnoreCase(Constants.None)) {
                        soDelStatus = item.getDelvStatus().toLowerCase().contains(delivetyType.toLowerCase());
                    } else {
                        soDelStatus = true;
                    }
                    if (!TextUtils.isEmpty(searchText)) {
                        soSearchStatus = item.getOrderNo().toLowerCase().contains(searchText.toLowerCase());
                    } else {
                        soSearchStatus = true;
                    }
                    if (soTypeStatus && soDelStatus && soSearchStatus)
                        searchBeanArrayList.add(item);
                }
            }
        }
        if (ISalesOrderListView != null) {
            ISalesOrderListView.searchResult(searchBeanArrayList);
        }
    }

    @Override
    public void onRefresh() {
        onRefreshSOrder();
    }

    @Override
    public void startFilter(int requestCode, int resultCode, Intent data) {
        filterType = data.getStringExtra(DateFilterFragment.EXTRA_DEFAULT);
        startDate = data.getStringExtra(DateFilterFragment.EXTRA_START_DATE);
        endDate = data.getStringExtra(DateFilterFragment.EXTRA_END_DATE);
        statusId = data.getStringExtra(SOFilterActivity.EXTRA_SO_STATUS);
        statusName = data.getStringExtra(SOFilterActivity.EXTRA_SO_STATUS_NAME);
        delvStatusId = data.getStringExtra(SOFilterActivity.EXTRA_DELV_STATUS);
        delvStatusName = data.getStringExtra(SOFilterActivity.EXTRA_DELV_STATUS_NAME);
//        requestSOList(startDate, endDate);\
        displayFilterType();

        connectToOfflineDB();
    }

    @Override
    public void getRefreshTime() {
        if (ISalesOrderListView != null) {
//            if (refreshTime != 0)
            ISalesOrderListView.displayRefreshTime(UtilConstants.getLastSyncTime(context, "collection", "SOs"));
        }
    }

    @Override
    public void getDetails(String no) {
//        new GetSODetailsASync(no).execute();

        if (ISalesOrderListView != null)
            ISalesOrderListView.showProgressDialog();

        String query = Constants.SOs + "('" + no + "')?$expand=SOItemDetails,SOConditions,SOTexts,SOPartnerFunctions";
//        String query = Constants.SOs +  "?$filter=" + Constants.SONo +" eq '" + no + "') &$expand=SOItemDetails,SOConditions,SOTexts,SOPartnerFunctions";
        ConstantsUtils.onlineRequest(context,query,false,3,ConstantsUtils.SESSION_QRY,this,false,Constants.getRollID(context));
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
            if (ISalesOrderListView != null) {
                ISalesOrderListView.setFilterDate(statusDesc);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void onRefreshSOrder() {
        Log.d("Test :","Refresh swipe So List start");
        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(context)) {
            alAssignColl.clear();
            concatCollectionStr = "";
            alAssignColl.add(Constants.SOs);
            alAssignColl.add(Constants.SOItemDetails);
            alAssignColl.add(Constants.SOItems);
            alAssignColl.add(Constants.SOTexts);
            alAssignColl.add(Constants.SOConditions);
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);

            if (Constants.iSAutoSync) {
                if (ISalesOrderListView != null) {
                    ISalesOrderListView.hideProgressDialog();
                    ISalesOrderListView.showMessage(context.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    refguid = GUID.newRandom();
                    Constants.updateStartSyncTime(context,Constants.SOPD_sync,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (ISalesOrderListView != null) {
                ISalesOrderListView.hideProgressDialog();
                ISalesOrderListView.showMessage(context.getString(R.string.no_network_conn));
            }
        }
    }

    @Override
    public void onRequestError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e, context);
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
            if (!Constants.isStoreClosed) {
                if (operation == Operation.OfflineRefresh.getValue()) {

                    Constants.isSync = false;
                    if (!Constants.isStoreClosed) {
                        if (ISalesOrderListView != null) {
                            ISalesOrderListView.hideProgressDialog();
                            ISalesOrderListView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                        }


                    } else {
                        if (ISalesOrderListView != null) {
                            ISalesOrderListView.hideProgressDialog();
                            ISalesOrderListView.showMessage(context.getString(R.string.msg_sync_terminated));
                        }
                    }
                } else if (operation == Operation.GetStoreOpen.getValue()) {
                    Constants.isSync = false;
                    if (ISalesOrderListView != null) {
                        ISalesOrderListView.hideProgressDialog();
                        ISalesOrderListView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                    }
                }
            }

        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (ISalesOrderListView != null) {
                    ISalesOrderListView.showProgressDialog();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (ISalesOrderListView != null) {
                    ISalesOrderListView.hideProgressDialog();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        } else {
            Constants.isSync = false;
            if (ISalesOrderListView != null) {
                ISalesOrderListView.hideProgressDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }
    }


    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.OfflineRefresh.getValue()) {
            Constants.updateLastSyncTimeToTable(alAssignColl,context,Constants.SOPD_sync,refguid.toString().toUpperCase());
            ConstantsUtils.startAutoSync(context,false);
//            ConstantsUtils.serviceReSchedule(context, true);
            Constants.isSync = false;
            if (ISalesOrderListView != null) {
                ISalesOrderListView.hideProgressDialog();
                connectToOfflineDB();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, context, BuildConfig.APPLICATION_ID, false);
            }
        } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
            Constants.isSync = false;
            try {
                OfflineManager.getAuthorizations(context);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setSyncTime(context,refguid.toString().toUpperCase());
            ConstantsUtils.startAutoSync(context,false);
//            ConstantsUtils.serviceReSchedule(context, true);
            if (ISalesOrderListView != null) {
                ISalesOrderListView.hideProgressDialog();
                connectToOfflineDB();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, context, BuildConfig.APPLICATION_ID, false);
            }
        }
    }

    /**
     * get SOs HeaderList from OfflineDB
     */
    private void getSalesOrderList() {
        Log.d("Test :","Test So List sent Request");
        /*try {
            String qryBlck = Constants.ZZInactiveCustBlks+"?$filter="+Constants.CustomerNo+" eq '"+customerNumber+"'";
            //+" and SalesArea eq '"+soListBean.getSalesArea()+"' and Application eq 'PO_CRT'

            HashMap<String, String> blockCustomer = new HashMap<>();
            String jSONStr ="";
            try {
                jSONStr= OfflineManager.checkBlockedCustomer(context,qryBlck);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError("isBlock Customer Error "+e.getMessage());
                e.printStackTrace();
            }
            try {
                JSONObject jsonObject = new JSONObject(jSONStr);
                JSONArray jsonItem = jsonObject.getJSONArray("configdata");
                blockCustomer = Constants.getBlockCustomerKeyValues(jsonItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }*/
        String sortStr = " &$orderby=" + Constants.SONo + " desc";
        String qry = Constants.SOs + "?$select=SONo,DelvStatus,Status,Currency,TotalAmount,OrderDate&$filter=" + Constants.CustomerNo + " eq '" + customerNumber + "'";
        if (!TextUtils.isEmpty(statusId)) {
            qry = qry + " and Status eq '" + statusId + "'";
        }
        if (!TextUtils.isEmpty(delvStatusId)) {
            qry = qry + " and DelvStatus eq '" + delvStatusId + "'";
        }
        qry = qry + sortStr;
        ConstantsUtils.onlineRequest(context,qry,false,2,ConstantsUtils.SESSION_QRY,this,false,Constants.getRollID(context));
        /*try {
            if (!salesOrderHeaderArrayList.isEmpty()) {
                salesOrderHeaderArrayList.clear();
                salesOrderHeaderArrayList = new ArrayList<>();
            }
            this.salesOrderHeaderArrayList.addAll(OfflineManager.getSecondarySalesOrderList(context, qry, customerNumber, "00"));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * get SOs with material from offline DB
     *
     * @param status
     */
    private void getSOList(String status, String delvStatusId) {

        this.status = status;
        String sortStr = " &$orderby=" + Constants.SONo + " desc";
        String qry = Constants.SOItems + "?$filter=" + Constants.CustomerNo + " eq '" + customerNumber + "'";
        if (!TextUtils.isEmpty(status)) {
            qry = qry + " and StatusID eq '" + status + "'";
        }
        if (!TextUtils.isEmpty(delvStatusId)) {
            qry = qry + " and DelvStatusID eq '" + delvStatusId + "'";
        }
        qry = qry + sortStr;

        ConstantsUtils.onlineRequest(context,qry,false,1,ConstantsUtils.SESSION_QRY,this,false,Constants.getRollID(context));

        /*try {
            if (!salesOrderHeaderArrayList.isEmpty()) {
                salesOrderHeaderArrayList.clear();
                salesOrderHeaderArrayList = new ArrayList<>();
            }
            salesOrderHeaderArrayList.addAll(OfflineManager.getSOListDB(context, qry, customerNumber, status));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }*/

    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        switch (type) {
            case 1:
                try {
                    if (!salesOrderHeaderArrayList.isEmpty()) {
                        salesOrderHeaderArrayList.clear();
                        salesOrderHeaderArrayList = new ArrayList<>();
                    }
                    salesOrderHeaderArrayList.addAll(OfflineManager.getSOListDBOnline(context, list, customerNumber, status));
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (ISalesOrderListView != null) {
                                if (salesOrderHeaderArrayList != null) {
                                    onSearch(searchText, statusId, delvStatusId);
                                }
                                ISalesOrderListView.success();
                                ISalesOrderListView.hideProgressDialog();
                            }
                        }
                    });

                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                break;

            case 2:
                try {
                    Log.d("Test :","Test So List response Success");
                    if (!salesOrderHeaderArrayList.isEmpty()) {
                        salesOrderHeaderArrayList.clear();
                        salesOrderHeaderArrayList = new ArrayList<>();
                    }
                    this.salesOrderHeaderArrayList.addAll(OfflineManager.getSecondarySalesOrderListOnline(context, list, customerNumber, "00"));

                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (ISalesOrderListView != null) {
                                if (salesOrderHeaderArrayList != null) {
                                    onSearch(searchText, statusId, delvStatusId);
                                }
                                ISalesOrderListView.success();
                                ISalesOrderListView.hideProgressDialog();
                            }
                        }
                    });
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                break;

            case 3:
                try {
                    ODataPayload oDataPayload = ((ODataResponseSingle) oDataRequestExecution.getResponse()).getPayload();
                    ODataEntity oDataEntity = (ODataEntity) oDataPayload;
                    final SOListBean salesOrderBeenArrayList = OfflineManager.getSODetailsOnline(oDataEntity, context);
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (ISalesOrderListView != null) {
                                ISalesOrderListView.hideProgressDialog();
                                ISalesOrderListView.openSODetail(salesOrderBeenArrayList);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, final String error, Bundle bundle) {
        if (ISalesOrderListView != null) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ISalesOrderListView.hideProgressDialog();
                    ISalesOrderListView.showMessage(error);
                }
            });
        }

    }

    /**
     * Get SOs on Background Thread
     */
    public class GetSalesOrderAsyncTask extends AsyncTask<Void, Void, ArrayList<SalesOrderBean>> {
        boolean isMaterialEnabled = false;

        public GetSalesOrderAsyncTask(boolean isMaterialEnabled) {
            this.isMaterialEnabled = isMaterialEnabled;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (ISalesOrderListView != null)
                ISalesOrderListView.showProgressDialog();
        }

        @Override
        protected ArrayList<SalesOrderBean> doInBackground(Void... params) {
            if (isMaterialEnabled) {
                getSOList(statusId, delvStatusId);
            } else {
                getSalesOrderList();
            }
            return salesOrderHeaderArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<SalesOrderBean> salesOrderBeenArrayList) {
            super.onPostExecute(salesOrderBeenArrayList);
            if (ISalesOrderListView != null) {
                if (salesOrderBeenArrayList != null) {
                    onSearch(searchText, statusId, delvStatusId);
                }
                ISalesOrderListView.success();
                ISalesOrderListView.hideProgressDialog();
            }
        }

    }

    public class GetSODetailsASync extends AsyncTask<Void, Void, SOListBean> {

        SOListBean soListBean;
        String soNO;

        public GetSODetailsASync(String soNO) {
            this.soNO = soNO;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (ISalesOrderListView != null)
                ISalesOrderListView.showProgressDialog();
        }

        @Override
        protected SOListBean doInBackground(Void... params) {
            String query = Constants.SOs + "('" + soNO + "')?$expand=SOItemDetails,SOConditions,SOTexts,SOPartnerFunctions";
            return OfflineManager.getSODetails(query, context);
        }

        @Override
        protected void onPostExecute(SOListBean salesOrderBeenArrayList) {
            super.onPostExecute(salesOrderBeenArrayList);
            if (ISalesOrderListView != null) {
                ISalesOrderListView.hideProgressDialog();
                ISalesOrderListView.openSODetail(salesOrderBeenArrayList);
            }
        }

    }

}
