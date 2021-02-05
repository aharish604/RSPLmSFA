package com.rspl.sf.msfa.reports.InvoiceHistory;

import com.rspl.sf.msfa.reports.invoicelist.InvoiceListBean;

import java.util.ArrayList;

/**
 * Created by e10769 on 29-06-2017.
 */

public interface InvoiceListView {
//    void displayList(ArrayList<AttendanceConfigTypesetTypesBean> configTypesetTypesBeen, ArrayList<AttendanceConfigTypesetTypesBean> configTypesetDeliveryList);
    interface SalesOrderResponse<T>{
        void success(ArrayList<T> success);
        void error(String message);
    }
    void showMessage(String message);

    void dialogMessage(String message, String msgType);

    void showProgressDialog();

    void hideProgressDialog();

    void searchResult(ArrayList<InvoiceListBean> salesOrderBeen);

    void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus);

    void setFilterDate(String filterType);

    void displayRefreshTime(String refreshTime);
}
