package com.rspl.sf.msfa.reports.invoicelist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

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
import com.rspl.sf.msfa.reports.InvoiceHistory.invocieFilter.InvoiceFilterActivity;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataPayload;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;
import com.sap.smp.client.odata.store.ODataResponseSingle;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by e10847 on 19-12-2017.
 */

public class InvoiceListPresenter implements IInvoiceListPresenter, UIListener,OnlineODataInterface{

    ArrayList<String> alAssignColl = null;
    private Context context;
    private IInvoiceListViewPresenter iReqListViewPresenter;
    private Activity activity;
    private ArrayList<InvoiceListBean> invoiceBeanArrayList;
    private ArrayList<InvoiceListBean> searchBeanArrayList;
    private Hashtable<String, String> headerTable;
    private String SPGUID = "";
    private String searchText = "";
    private String CPGUID = "", CPUID = "", cpNo = "", cpName = "";
    private String mStrBundleRetID = "", mStrBundleCPGUID = "";
    private String startDate = "";
    private String endDate = "";
    private String filterType = "";
    private String delvStatusId = "";
    private String statusId = "";
    private String statusName = "";
    private String delvStatusName = "";
    private String SONo = "";
    private boolean isErrorFromBackend = false;
    InvoiceListBean invoiceDetailBean = new InvoiceListBean();
    private GUID refguid =null;

    public InvoiceListPresenter(Context context, IInvoiceListViewPresenter iReqListViewPresenter, Activity activity, String mStrBundleRetID, String mStrBundleCPGUID) {
        this.context = context;
        this.iReqListViewPresenter = iReqListViewPresenter;
        this.invoiceBeanArrayList = new ArrayList<>();
        this.searchBeanArrayList = new ArrayList<>();
        this.headerTable = new Hashtable<>();
        this.activity = activity;
        this.mStrBundleRetID = mStrBundleRetID;
        this.mStrBundleCPGUID = mStrBundleCPGUID;
    }


    @Override
    public void onFilter() {
        if (iReqListViewPresenter != null) {
            iReqListViewPresenter.openFilter(startDate, endDate, filterType, statusId, delvStatusId);
        }
    }

    @Override
    public void onSearch(String searchText) {
        if (!this.searchText.equalsIgnoreCase(searchText)) {
            this.searchText = searchText;
            onSearchQuery(searchText, delvStatusId, statusId);
        }
    }

    private void onSearchQuery(String searchText, String dueDateStatus, String invoiceStatus) {
        this.searchText = searchText;
        searchBeanArrayList.clear();
        boolean isInvStatus = false;
        boolean isDelvStatus = false;
        boolean soSearchStatus = false;
        if (invoiceBeanArrayList != null) {
            if (TextUtils.isEmpty(searchText) && TextUtils.isEmpty(dueDateStatus) && TextUtils.isEmpty(invoiceStatus)) {
                searchBeanArrayList.addAll(invoiceBeanArrayList);
            } else {
                for (InvoiceListBean item : invoiceBeanArrayList) {
                    isInvStatus = false;
                    isDelvStatus = false;
                    soSearchStatus = false;

                    if (!TextUtils.isEmpty(searchText)) {
                        soSearchStatus = item.getInvoiceNo().toLowerCase().contains(searchText.toLowerCase());
                    } else {
                        soSearchStatus = true;
                    }
                    if (!TextUtils.isEmpty(dueDateStatus)) {
                        if (item.getDueDateStatus().contains(dueDateStatus)) {
                            isDelvStatus = true;
                        }
                    } else {
                        isDelvStatus = true;
                    }
                    if (!TextUtils.isEmpty(invoiceStatus)) {
                        if (item.getInvoiceStatus().contains(invoiceStatus)) {
                            isInvStatus = true;
                        }
                    } else {
                        isInvStatus = true;
                    }

                    if (soSearchStatus && isInvStatus && isDelvStatus)
                        searchBeanArrayList.add(item);
                }
            }
        }
        if (iReqListViewPresenter != null) {
            iReqListViewPresenter.searchResult(searchBeanArrayList);
        }
    }

    @Override
    public void onRefresh() {
        onRefreshSOrder();
    }

    @Override
    public void startFilter(int requestCode, int resultCode, Intent data) {
        filterType = data.getStringExtra(DateFilterFragment.EXTRA_DEFAULT);
        statusId = data.getStringExtra(InvoiceFilterActivity.EXTRA_INVOICE_STATUS);
        statusName = data.getStringExtra(InvoiceFilterActivity.EXTRA_INVOICE_STATUS_NAME);
        delvStatusId = data.getStringExtra(InvoiceFilterActivity.EXTRA_INVOICE_GR_STATUS);
        delvStatusName = data.getStringExtra(InvoiceFilterActivity.EXTRA_INVOICE_GR_STATUS_NAME);
        onSearchQuery(searchText, delvStatusId, statusId);
        displayFilterType();
    }

    @Override
    public void getInvoiceList() {
        if (iReqListViewPresenter != null) {
            iReqListViewPresenter.showProgressDialog();
        }
       /* new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    invoiceBeanArrayList = OfflineManager.getInvoiceHistoryList(Constants.SFINVOICES + "?$filter=" + Constants.CustomerNo + " eq '" + mStrBundleRetID + "' " +
                            "and " + Constants.InvoiceDate + " ge datetime'" + Constants.getLastMonthDate() + "' ", activity, "", mStrBundleCPGUID);

                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
                ((Activity) activity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iReqListViewPresenter != null) {
                            onSearchQuery(searchText, delvStatusId, statusId);
                            iReqListViewPresenter.hideProgressDialog();
                        }
                    }
                });
            }
        }).start();*/

        String qry = Constants.SFINVOICES + "?$filter=" + Constants.CustomerNo + " eq '" + mStrBundleRetID + "' " +
                "and " + Constants.InvoiceDate + " ge datetime'" + Constants.getLastMonthDate() + "'";

        ConstantsUtils.onlineRequest(context, qry, false, 2, ConstantsUtils.SESSION_QRY, this, false, Constants.getRollID(context));
    }


    @Override
    public void onDestroy() {
        iReqListViewPresenter = null;
    }

    /**
     * Getting the Invoice Items List from DB
     *
     * @return
     */
    public void getInvoiceItemsList() {
        if (iReqListViewPresenter != null) {
            iReqListViewPresenter.showProgressDialog();
        }
       /* new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    invoiceBeanArrayList = OfflineManager.getNewInvoiceHistoryList(Constants.InvoiceItems + "?$filter=" + Constants.CustomerNo + " eq '" + mStrBundleRetID + "' " +
                            "and " + Constants.InvoiceDate + " ge datetime'" + Constants.getLastMonthDate() + "' ", activity, "", mStrBundleCPGUID);

                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
                ((Activity) activity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iReqListViewPresenter != null) {
                            onSearchQuery(searchText, delvStatusId, statusId);
                            iReqListViewPresenter.hideProgressDialog();
                        }
                    }
                });
            }
        }).start();*/
        String qry = Constants.InvoiceItems + "?$filter=" + Constants.CustomerNo + " eq '" + mStrBundleRetID + "' " +
                "and " + Constants.InvoiceDate + " ge datetime'" + Constants.getLastMonthDate() + "'";

        ConstantsUtils.onlineRequest(context,qry,false,1,ConstantsUtils.SESSION_QRY,this,false,Constants.getRollID(context));
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
            if (iReqListViewPresenter != null) {
                iReqListViewPresenter.setFilterDate(statusDesc);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void getInvoiceDetails(String invoiceNumber) {
        /*try {
            new InvoiceDetailsAsyncTask(invoiceNumber).execute();
        } catch (Throwable e) {
            e.printStackTrace();
        }*/
        if (iReqListViewPresenter != null)
            iReqListViewPresenter.showProgressDialog();

        this.SONo= invoiceNumber;
        String query = Constants.Invoices + "(InvoiceNo='" + invoiceNumber + "')?$expand=InvoiceItemDetails,InvoiceConditions,InvoicePartnerFunctions";
        try {
             ConstantsUtils.onlineRequest(context,query,false,3,ConstantsUtils.SESSION_QRY,InvoiceListPresenter.this,false,Constants.getRollID(context));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestError(int i, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(i, e, context);
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
            if (!Constants.isStoreClosed) {
                if (i == Operation.OfflineRefresh.getValue()) {

                    Constants.isSync = false;
                    if (!Constants.isStoreClosed) {
                        if (iReqListViewPresenter != null) {
                            iReqListViewPresenter.hideProgressDialog();
                            iReqListViewPresenter.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                        }


                    } else {
                        if (iReqListViewPresenter != null) {
                            iReqListViewPresenter.hideProgressDialog();
                            iReqListViewPresenter.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                        }
                    }
                } else if (i == Operation.GetStoreOpen.getValue()) {
                    Constants.isSync = false;
                    if (iReqListViewPresenter != null) {
                        iReqListViewPresenter.hideProgressDialog();
                        iReqListViewPresenter.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                    }
                }
            }

        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (iReqListViewPresenter != null) {
                    iReqListViewPresenter.showProgressDialog();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (iReqListViewPresenter != null) {
                    iReqListViewPresenter.hideProgressDialog();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        }else {
            Constants.isSync = false;
            if (iReqListViewPresenter != null) {
                iReqListViewPresenter.hideProgressDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        if (!Constants.isStoreClosed) {
            if (i == Operation.OfflineRefresh.getValue()) {
                Constants.updateLastSyncTimeToTable(alAssignColl,context,Constants.Invoice_sync,refguid.toString().toUpperCase());
                ConstantsUtils.startAutoSync(context,false);
//                ConstantsUtils.serviceReSchedule(context, true);
                Constants.isSync = false;
                if (iReqListViewPresenter != null) {
                    iReqListViewPresenter.hideProgressDialog();
                    iReqListViewPresenter.invoiceListFresh();
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false);
                }
            } else if (i == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
                Constants.isSync = false;
                try {
                    OfflineManager.getAuthorizations(context);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                Constants.setSyncTime(context,refguid.toString().toUpperCase());
                ConstantsUtils.startAutoSync(context,false);
//                ConstantsUtils.serviceReSchedule(context, true);
                if (iReqListViewPresenter != null) {
                    iReqListViewPresenter.hideProgressDialog();
                    iReqListViewPresenter.invoiceListFresh();
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false);
                }
            }

        }
    }

    private void onRefreshSOrder() {
        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(context)) {
            alAssignColl.clear();
            concatCollectionStr = "";
            alAssignColl.add(Constants.Invoices);
            alAssignColl.add(Constants.InvoiceItemDetails);
            alAssignColl.add(Constants.InvoiceItems);
            alAssignColl.add(Constants.InvoiceConditions);
            alAssignColl.add(Constants.InvoicePartnerFunctions);
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
            if (Constants.iSAutoSync) {
                if (iReqListViewPresenter != null) {
                    iReqListViewPresenter.hideProgressDialog();
                    iReqListViewPresenter.showMessage(context.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    refguid = GUID.newRandom();
                    Constants.updateStartSyncTime(context,Constants.Invoice_sync,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (iReqListViewPresenter != null) {
                        iReqListViewPresenter.hideProgressDialog();
                        iReqListViewPresenter.showMessage(e.getMessage());
                    }
                }
            }
        } else {
            if (iReqListViewPresenter != null) {
                iReqListViewPresenter.hideProgressDialog();
                iReqListViewPresenter.showMessage(context.getString(R.string.no_network_conn));
            }
        }
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        switch (type) {
            case 1:
                try {
                    invoiceBeanArrayList = OfflineManager.getNewInvoiceHistoryOnlineList(list, "");

                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
                ((Activity) activity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iReqListViewPresenter != null) {
                            onSearchQuery(searchText, delvStatusId, statusId);
                            iReqListViewPresenter.hideProgressDialog();
                            iReqListViewPresenter.displayResult(invoiceBeanArrayList);
                        }
                    }
                });
                break;

            case 2:
                try {
                    invoiceBeanArrayList = OfflineManager.getInvoiceHistoryOnlineList(list,"");
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
                ((Activity) activity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iReqListViewPresenter != null) {
                            onSearchQuery(searchText, delvStatusId, statusId);
                            iReqListViewPresenter.hideProgressDialog();
                            iReqListViewPresenter.displayResult(invoiceBeanArrayList);
                        }
                    }
                });
                break;

            case 3:
//                InvoiceListBean invoiceListBean = new InvoiceListBean();
                invoiceDetailBean = new InvoiceListBean();
                try {
                    ODataPayload oDataPayload = ((ODataResponseSingle) oDataRequestExecution.getResponse()).getPayload();
                    ODataEntity oDataEntity = (ODataEntity) oDataPayload;
                    invoiceDetailBean = OfflineManager.getInvoiceDetailsList(oDataEntity, context, SONo);
//                    this.invoiceDetailBean = invoiceListBean;

                    ((Activity)activity).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (iReqListViewPresenter != null) {
//                iReqListViewPresenter.invoiceDetails(invoiceListBean);
                                iReqListViewPresenter.invoiceDetails(invoiceDetailBean);
                                iReqListViewPresenter.hideProgressDialog();
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
    public void responseFailed(ODataRequestExecution oDataRequestExecution, String errorMessage, Bundle bundle) {
        if (iReqListViewPresenter != null) {
            iReqListViewPresenter.hideProgressDialog();
            iReqListViewPresenter.showMessage(errorMessage);
        }
    }

    public class InvoiceDetailsAsyncTask extends AsyncTask<Void, Void, Void> {
        InvoiceListBean invoiceListBean = new InvoiceListBean();
        private String soNo = "";

        public InvoiceDetailsAsyncTask(String soNo) {
            this.soNo = soNo;
            SONo = soNo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (iReqListViewPresenter != null)
                iReqListViewPresenter.showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String query = Constants.Invoices + "(InvoiceNo='" + soNo + "')?$expand=InvoiceItemDetails,InvoiceConditions,InvoicePartnerFunctions";
            try {
                invoiceListBean = OfflineManager.getInvoiceDetails(query, context, soNo);
               /* ConstantsUtils.onlineRequest(context,query,false,3,ConstantsUtils.SESSION_QRY,InvoiceListPresenter.this,false,Constants.getRollID(context));*/

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (iReqListViewPresenter != null) {
                iReqListViewPresenter.invoiceDetails(invoiceListBean);
                iReqListViewPresenter.hideProgressDialog();
            }
        }
    }

}
