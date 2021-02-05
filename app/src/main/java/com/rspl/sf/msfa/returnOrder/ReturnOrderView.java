package com.rspl.sf.msfa.returnOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10860 on 12/28/2017.
 */

public interface ReturnOrderView {

    void showMessage(String message);

    void dialogMessage(String message, String msgType);

    void showProgressDialog();

    void hideProgressDialog();

    void searchResult(ArrayList<ReturnOrderBean> soListBeen);

    void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus);

    void setFilterDate(String filterType);

    void displayRefreshTime(String refreshTime);

    void displayList(List<ReturnOrderBean> list);
}
