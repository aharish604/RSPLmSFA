package com.rspl.sf.msfa.socreate.stepTwo;


import com.rspl.sf.msfa.socreate.SOItemBean;

import java.util.ArrayList;

/**
 * Created by e10769 on 30-06-2017.
 */

public interface SOCrtStpTwoView {
    void displayList(ArrayList<SOItemBean> soItemList);
    void displaySearchList(ArrayList<SOItemBean> soItemList);
    void showProgressDialog(String message);
    void hideProgressDialog();
    void displayMessage(String message);

    void displayTotalSelectedMat(int finalSelectedCount);

    void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus);
    void setFilterDate(String filterType);
}
