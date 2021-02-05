package com.rspl.sf.msfa.reports.salesorder.pendingsync;

import com.rspl.sf.msfa.mbo.SalesOrderBean;

/**
 * Created by e10847 on 07-12-2017.
 */

public interface ISalesOrderHeaderPendingSyncPresenter {
    /**
     * This will connect to offline Manager using AsyncTask and return the Result From OfflineManger to View.
     * @param salesOrderResponse
     */
    void connectToOfflineDB(ISalesOrderPendingSyncView.SalesOrderResponse<SalesOrderBean> salesOrderResponse);
    void onSync();
    void onDestroy();
}
