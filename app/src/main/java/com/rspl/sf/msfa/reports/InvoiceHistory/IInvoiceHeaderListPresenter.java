package com.rspl.sf.msfa.reports.InvoiceHistory;

import com.rspl.sf.msfa.reports.invoicelist.InvoiceListBean;

/**
 * Created by e10847 on 07-12-2017.
 */

public interface IInvoiceHeaderListPresenter {
    /**
     * This will connect to offline Manager using AsyncTask and return the Result From OfflineManger to View.
     * @param salesOrderResponse
     */
    void connectToOfflineDB(InvoiceListView.SalesOrderResponse<InvoiceListBean> salesOrderResponse);
    void onStart();
    void onDestroy();
    void onResume();
    void onFilter();
    void onSearch(String searchText);
    void onRefresh();
    void startFilter(String startDate, String endDate, String filterType, String statusId, String delvStatusId, String statusName, String delvStatusName);

}
