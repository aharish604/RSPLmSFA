package com.rspl.sf.msfa.reports.InvoiceHistory;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.reports.invoicelist.InvoiceListBean;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;


/**
 * Created by e10847 on 07-12-2017.
 */

public class InvoiceHeaderListPresenter implements IInvoiceHeaderListPresenter {
    private String startDate = "";
    private String endDate = "";
    private String filterType = "";
    private Context context=null;
    private String customerNumber="";
    private ArrayList<InvoiceListBean>salesOrderHeaderArrayList=null;
    private ArrayList<InvoiceListBean>searchBeanArrayList=null;
    private InvoiceListView salesOrderListView = null;
    private String searchText = "";
    private String delvStatusId = "";
    private String statusId = "";
    private String statusName = "";
    private String delvStatusName = "";
    private long refreshTime = 0;
    private String Cpguid="";

    public InvoiceHeaderListPresenter(Context context, String customerNumber, InvoiceListView salesOrderListView,String cpguid) {
        this.context = context;
        this.customerNumber = customerNumber;
        this.salesOrderListView=salesOrderListView;
        this.salesOrderHeaderArrayList=new ArrayList<>();
        this.searchBeanArrayList=new ArrayList<>();
        this.Cpguid=cpguid;
    }

    @Override
    public void connectToOfflineDB(InvoiceListView.SalesOrderResponse<InvoiceListBean> salesOrderResponse) {
        GetSalesOrderAsyncTask salesOrderAsyncTask = new GetSalesOrderAsyncTask(salesOrderResponse);
        salesOrderAsyncTask.execute();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        salesOrderListView = null;
    }

    @Override
    public void onResume() {
        if (salesOrderListView != null) {
            if (refreshTime != 0)
                salesOrderListView.displayRefreshTime(ConstantsUtils.getLastSeenDateFormat(context, refreshTime));
        }
    }

    @Override
    public void onFilter() {
        if (salesOrderListView != null) {
            salesOrderListView.openFilter(startDate, endDate, filterType, statusId, delvStatusId);
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
                for (InvoiceListBean item : salesOrderHeaderArrayList) {
                    soTypeStatus = false;
                    soDelStatus = false;
                    soSearchStatus = false;
                    if (!TextUtils.isEmpty(soStatus)) {
                     //   soTypeStatus = item.getStatusID().toLowerCase().contains(soStatus.toLowerCase());
                    } else {
                        soTypeStatus = true;
                    }
                    if (!TextUtils.isEmpty(delivetyType) && !delivetyType.equalsIgnoreCase(Constants.None)) {
                     //   soDelStatus = item.getDelvStatus().toLowerCase().contains(delivetyType.toLowerCase());
                    } else {
                        soDelStatus = true;
                    }
                    if (!TextUtils.isEmpty(searchText)) {
                      //  soSearchStatus = item.getOrderNo().toLowerCase().contains(searchText.toLowerCase());
                    } else {
                        soSearchStatus = true;
                    }
                    if (soTypeStatus && soDelStatus && soSearchStatus)
                        searchBeanArrayList.add(item);
                }
            }
        }
        if (salesOrderListView != null) {
            salesOrderListView.searchResult(searchBeanArrayList);
        }
    }
    @Override
    public void onRefresh() {

    }

    @Override
    public void startFilter(String startDate, String endDate, String filterType, String statusId, String delvStatusId, String statusName, String delvStatusName) {
    }
   /* private void displayFilterType() {
        String statusDesc = "";
        if (!TextUtils.isEmpty(statusId)) {
            statusDesc = ", " + statusName;
        }
        if (!TextUtils.isEmpty(delvStatusId)) {
            statusDesc = statusDesc + ", " + delvStatusName;
        }
        String displayFilterType = filterType;
        if (filterType.equalsIgnoreCase(context.getString(R.string.so_filter_manual_selection))){
            displayFilterType = ConstantsUtils.convertDateIntoDeviceFormat(startDate)+" - "+ ConstantsUtils.convertDateIntoDeviceFormat(endDate);
        }
        if (salesOrderListView != null) {
            salesOrderListView.setFilterDate(displayFilterType + statusDesc);
        }
    }*/
    /**
     * Get SOs on Background Thread
     */
    public class GetSalesOrderAsyncTask extends AsyncTask<Void,Void,ArrayList<InvoiceListBean>>{
        InvoiceListView.SalesOrderResponse<InvoiceListBean>salesOrderResponse;
        public GetSalesOrderAsyncTask(InvoiceListView.SalesOrderResponse<InvoiceListBean> salesOrderResponse) {
            this.salesOrderResponse = salesOrderResponse;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            salesOrderListView.showProgressDialog();
        }
        @Override
        protected ArrayList<InvoiceListBean> doInBackground(Void... params) {
            getSalesOrderList();
            return salesOrderHeaderArrayList;
        }
        @Override
        protected void onPostExecute(ArrayList<InvoiceListBean> salesOrderBeenArrayList) {
            super.onPostExecute(salesOrderBeenArrayList);
            salesOrderResponse.success(salesOrderBeenArrayList);
            salesOrderListView.hideProgressDialog();
        }
    }

    /**
     * get SOs HeaderList from OfflineDB
     */
    private void getSalesOrderList(){
        String getSalesOrderHeaderList = Constants.InvoiceItems+"?$filter="+ Constants.CustomerNo+" eq '"+customerNumber+"' " +
                "and "+ Constants.InvoiceDate+" ge datetime'" + Constants.getLastMonthDate() + "' ";
        try {
            this.salesOrderHeaderArrayList.addAll(OfflineManager.getNewInvoiceHistoryList(getSalesOrderHeaderList,context,"",Cpguid));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

}
