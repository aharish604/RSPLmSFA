package com.rspl.sf.msfa.dealerstock;


import java.util.ArrayList;

/**
 * Created by e10769 on 30-06-2017.
 */

public interface StockCrtStpTwoView {
    void displayList(ArrayList<DealerStockBean> soItemList);
    void displaySearchList(ArrayList<DealerStockBean> soItemList);
    void showProgressDialog(String message);
    void hideProgressDialog();
    void displayMessage(String message);

    void displayTotalSelectedMat(int finalSelectedCount);
    void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus);
    void setFilterDate(String filterType);
    void onCreateUpdateSuccess();
}
