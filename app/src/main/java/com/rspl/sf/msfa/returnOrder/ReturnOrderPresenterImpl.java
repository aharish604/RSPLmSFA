package com.rspl.sf.msfa.returnOrder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.asyncTask.RefreshAsyncTask;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.returnOrder.retrunFilter.FilterReturnActivity;
import com.rspl.sf.msfa.returnOrder.returnDetail.ReturnOrderDetailActivity;
import com.rspl.sf.msfa.store.OnlineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10860 on 12/28/2017.
 */

public class ReturnOrderPresenterImpl implements ReturnOrderPresenter, OnlineODataInterface, UIListener {
    ArrayList<String> alAssignColl = null;
    private Context mContext;
    private int comingFrom;
    private ReturnOrderView returnOrderView;
    private boolean isSessionRequired;
    private String customerNo;
    //    private String filterType, startDate, endDate;
    private List<ReturnOrderBean> returnOrderList;
    private List<ReturnOrderBean> searchReturnOrderList = new ArrayList<>();
    private String status = "", grStatus = "";
    private String statusName = "", grStatusName = "";
    private String searchText = "";
    private GUID refguid =null;

    public ReturnOrderPresenterImpl(Context mContext, int comingFrom, ReturnOrderView returnOrderView, boolean isSessionRequired, String customerNo) {
        this.mContext = mContext;
        this.comingFrom = comingFrom;
        this.returnOrderView = returnOrderView;
        this.isSessionRequired = isSessionRequired;
        this.customerNo = customerNo;
//        filterType = mContext.getString(R.string.so_filter_last_one_month);
    }

    @Override
    public void onStart() {
//        startDate = SOUtils.getStartDate(mContext, filterType);
//        endDate = SOUtils.getEndDate(mContext, filterType);
        requestROList();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onSearch(String searchText) {
        if (!this.searchText.equalsIgnoreCase(searchText)) {
            this.searchText = searchText;
            onSearchView(searchText);
        }
    }

    private void onSearchView(String searchText) {
        this.searchText = searchText;
        searchReturnOrderList.clear();
        boolean soSearchStatus = false;
        if (returnOrderList != null) {
            if (TextUtils.isEmpty(searchText)) {
                searchReturnOrderList.addAll(returnOrderList);
            } else {
                for (ReturnOrderBean item : returnOrderList) {
                    soSearchStatus = false;
                    soSearchStatus = TextUtils.isEmpty(searchText) || item.getRetOrdNo().toLowerCase().contains(searchText.toLowerCase());
                    if (soSearchStatus)
                        searchReturnOrderList.add(item);
                }
            }
        }
        if (returnOrderView != null) {
            returnOrderView.searchResult((ArrayList<ReturnOrderBean>) searchReturnOrderList);
        }
    }


    @Override
    public void onRefresh() {
//        requestROList(startDate,endDate);
        onRefreshROrder();
    }

    @Override
    public void getRefreshTime() {

    }

    @Override
    public void onFilter() {
        if (returnOrderView != null) {
            returnOrderView.openFilter("", "", "", status, grStatus);
        }
    }

    @Override
    public void startFilter(int requestCode, int resultCode, Intent data) {
//        filterType = data.getStringExtra(DateFilterFragment.EXTRA_DEFAULT);
//        startDate = data.getStringExtra(DateFilterFragment.EXTRA_START_DATE);
//        endDate = data.getStringExtra(DateFilterFragment.EXTRA_END_DATE);
        status = data.getStringExtra(FilterReturnActivity.EXTRA_RETURN_STATUS);
        statusName = data.getStringExtra(FilterReturnActivity.EXTRA_RETURN_STATUS_NAME);
        grStatus = data.getStringExtra(FilterReturnActivity.EXTRA_GR_STATUS);
        grStatusName = data.getStringExtra(FilterReturnActivity.EXTRA_GR_STATUS_NAME);
        requestROList();
    }

    @Override
    public void onItemClick(ReturnOrderBean returnOrderBeanDeatil) {
//        if (UtilConstants.isNetworkAvailable(mContext)) {
            if (returnOrderView != null) {
                returnOrderView.showProgressDialog();
            }
            String qry = Constants.ReturnOrders + "(RetOrdNo='" + returnOrderBeanDeatil.getRetOrdNo() + "')?$expand=ReturnOrderItemDetails";

            ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 2, ConstantsUtils.SESSION_QRY, this, false);
       /* } else {
            if (returnOrderView != null) {
                returnOrderView.hideProgressDialog();
                returnOrderView.showMessage(mContext.getString(R.string.no_network_conn));
            }
        }*/
//        openDetailScreen();
    }

    private void openDetailScreen(ReturnOrderBean returnOrderBean) {
        Intent intent = new Intent(mContext, ReturnOrderDetailActivity.class);
        intent.putExtra(Constants.EXTRA_SESSION_REQUIRED, isSessionRequired);
        intent.putExtra(Constants.EXTRA_SO_HEADER, returnOrderBean);
        mContext.startActivity(intent);
    }

    private void displayFilterType() {
        String statusDesc = "";
        if (!TextUtils.isEmpty(status)) {
            statusDesc = ", " + statusName;
        }
        if (!TextUtils.isEmpty(grStatus)) {
            statusDesc = statusDesc + ", " + grStatusName;
        }
//        String displayFilterType = filterType;
//        if (filterType.equalsIgnoreCase(mContext.getString(R.string.so_filter_manual_selection))) {
//            displayFilterType = ConstantsUtils.convertDateIntoDeviceFormat(startDate) + " - " + ConstantsUtils.convertDateIntoDeviceFormat(endDate);
//        }
        if (returnOrderView != null) {
            returnOrderView.setFilterDate(statusDesc);
        }
    }

    private void requestROList() {
//        if (UtilConstants.isNetworkAvailable(mContext)) {
        if (returnOrderView != null) {
            returnOrderView.showProgressDialog();
        }
           /* String qry = "";
            qry = Constants.ReturnOrderItems + "/?$filter=(OrderDate+ge+datetime'" + startDate + "'+and+OrderDate+le+datetime'" + endDate + "')";

            if (!TextUtils.isEmpty(status))
                qry = qry + "+and+StatusID+eq+'" + status + "'";
            if (!TextUtils.isEmpty(grStatus))
                qry = qry + "+and+GRStatusID+eq+'" + grStatus + "'";*/

        String qry = "";
//            qry = Constants.ReturnOrderItems + "?$filter=(OrderDate ge datetime'" + startDate + "' and OrderDate le datetime'" + endDate + "') and "+Constants.CustomerNo+" eq '"+customerNo+"' ";
        if (comingFrom == 1) {
            qry = Constants.ReturnOrderItems + "?$filter=" + Constants.CustomerNo + " eq '" + customerNo + "' ";

            if (!TextUtils.isEmpty(status))
                qry = qry + " and StatusID eq '" + status + "' ";
            if (!TextUtils.isEmpty(grStatus))
                qry = qry + " and GRStatusID eq '" + grStatus + "' ";
            qry = qry + "&$orderby=" + Constants.RetOrdNo + " desc";
        } else {
            qry = Constants.ReturnOrders + "?$filter=" + Constants.CustomerNo + " eq '" + customerNo + "' ";

            if (!TextUtils.isEmpty(status))
                qry = qry + " and StatusID eq '" + status + "' ";
            if (!TextUtils.isEmpty(grStatus))
                qry = qry + " and GRStatusID eq '" + grStatus + "' ";
            qry = qry + "&$orderby=" + Constants.RetOrdNo + " desc";
        }
        ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 1, ConstantsUtils.SESSION_QRY, this, false);
       /* } else {
            if (returnOrderView != null) {
                returnOrderView.hideProgressDialog();
                returnOrderView.showMessage(mContext.getString(R.string.no_network_conn));
            }
        }*/
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        switch (type) {
            case 1:
                try {
                    returnOrderList = OnlineManager.getReturnOrderList(mContext,list);
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (returnOrderView != null) {
                                returnOrderView.hideProgressDialog();
                                displayFilterType();
                                displayFilterTime();
                                onSearchView(searchText);
//                                returnOrderView.displayList(returnOrderList);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    showErrorResponse(e.getMessage());
                }
                break;
            case 2:
                try {
                    final ReturnOrderBean roListBean = OnlineManager.getRODetails(oDataRequestExecution, mContext, "", isSessionRequired, comingFrom);
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (returnOrderView != null) {
                                returnOrderView.hideProgressDialog();
                                openDetailScreen(roListBean);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    showErrorResponse(e.getMessage());
                }
//                currentRequest++;
                break;
        }

    }

    private void displayFilterTime() {
        if (returnOrderView != null) {
            returnOrderView.displayRefreshTime(mContext.getString(R.string.po_last_refreshed) + " " + ConstantsUtils.getLastSeenDateFormat(mContext, ConstantsUtils.getMilliSeconds(
                    ConstantsUtils.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.ReturnOrders, Constants.TimeStamp, mContext))));
        }
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, String s, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        switch (type) {
            case 1:
                showErrorResponse(s);
                break;
            case 2:
                showErrorResponse(s);
                break;
        }
    }

    private void showErrorResponse(final String errorMsg) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (returnOrderView != null) {
                    returnOrderView.hideProgressDialog();
                    returnOrderView.showMessage(errorMsg);
                }
            }
        });
    }

    private void onRefreshROrder() {
        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(mContext)) {
            if (returnOrderView != null) {
                returnOrderView.showProgressDialog();
            }
            alAssignColl.clear();
            concatCollectionStr = "";
            alAssignColl.add(Constants.ReturnOrders);
            alAssignColl.add(Constants.ReturnOrderItems);
            alAssignColl.add(Constants.ReturnOrderItemDetails);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
            if (Constants.iSAutoSync) {
                if (returnOrderView != null) {
                    returnOrderView.hideProgressDialog();
                    returnOrderView.showMessage(mContext.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    refguid = GUID.newRandom();
                    Constants.updateStartSyncTime(mContext,Constants.DownLoad,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(mContext, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (returnOrderView != null) {
                returnOrderView.hideProgressDialog();
                returnOrderView.showMessage(mContext.getString(R.string.no_network_conn));
            }
        }
    }

    @Override
    public void onRequestError(int i, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(i, e, mContext);
        if (errorBean.hasNoError()) {
            if (i == Operation.OfflineRefresh.getValue()) {
                Constants.isSync = false;
                if (returnOrderView != null) {
                    returnOrderView.hideProgressDialog();
                    returnOrderView.showMessage(mContext.getString(R.string.msg_error_occured_during_sync));
                }

            }

        } else {
            Constants.isSync = false;
            Constants.displayMsgReqError(errorBean.getErrorCode(), mContext);
        }
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        if (i == Operation.OfflineRefresh.getValue()) {
            Constants.updateLastSyncTimeToTable(alAssignColl,mContext,Constants.DownLoad,refguid.toString().toUpperCase());
            Constants.isSync = false;
            if (returnOrderView != null) {
                returnOrderView.hideProgressDialog();
                onStart();
            }
        }

    }
}
