package com.rspl.sf.msfa.mtp.subordinate;

import com.rspl.sf.msfa.mbo.SalesPersonBean;

import java.util.ArrayList;

/**
 * Created by e10769 on 10-Apr-18.
 */

public interface SalesPersonsView {
    void displayMsg(String msg);
    void showProgress();
    void hideProgress();
    void setFilterDate(String data);
    void displayLastRefreshedTime(String refreshTime);
    void displayList(ArrayList<SalesPersonBean> salesPersonBeanArrayList);
    void displayFilter(ArrayList<String> filterList);
}
