package com.rspl.sf.msfa.reports.invoicelist;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

/**
 * Created by e10847 on 19-12-2017.
 */

public interface IInvoiceListViewPresenter {
    void initializeUI(Context context);
    void initializeClickListeners();
    void initializeObjects(Context context);
    void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager);
    void showMessage(String message);
    void dialogMessage(String message, String msgType);
    void showProgressDialog();
    void hideProgressDialog();
    void openFilter(String startDate, String endDate, String filterType, String status, String grStatus);
    void searchResult(ArrayList<InvoiceListBean> feedbackBeanArrayList);
    void displayResult(ArrayList<InvoiceListBean> feedbackBeanArrayList);
    void setFilterDate(String filterType);
    void invoiceDetails(InvoiceListBean invoiceListBean);
    void invoiceListFresh();
    void displayRefreshTime(String refreshTime);


}
