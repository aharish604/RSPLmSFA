package com.rspl.sf.msfa.reports.salesorder.pendingsync;

import java.util.ArrayList;

/**
 * Created by e10769 on 29-06-2017.
 */

public interface ISalesOrderPendingSyncView {
    interface SalesOrderResponse<T>{
        void success(ArrayList<T> success);
        void error(String message);
    }
    void showProgressDialog();
    void onReloadData();
    void hideProgressDialog();
    void showMessage(String message);

}
