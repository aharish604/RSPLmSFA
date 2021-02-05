package com.rspl.sf.msfa.reports.behaviourlist;


import com.rspl.sf.msfa.mbo.CustomerBean;

import java.util.ArrayList;

public interface IBehaviourListView {

    void showMessage(String message);


    void showProgressDialog();

    void hideProgressDialog();

    void searchResult(ArrayList<CustomerBean> CustomerBeanArrayList);

    void displayRefreshTime(String refreshTime);

}