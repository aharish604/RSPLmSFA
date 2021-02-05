package com.rspl.sf.msfa.grreport;


import java.util.ArrayList;

public interface IGRReportView {
    void dialogMessage(String message);

    void showProgressDialog();

    void hideProgressDialog();
    void onRefreshView();

    void searchResult(ArrayList<GRReportBean> salesOrderBeen);
    void displayData(ArrayList<GRReportBean> salesOrderBeen);

    void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus);

    void setFilterDate(String filterType);

    void displayRefreshTime(String refreshTime);
}
