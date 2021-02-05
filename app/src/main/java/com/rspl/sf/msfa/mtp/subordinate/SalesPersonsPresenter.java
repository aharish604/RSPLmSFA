package com.rspl.sf.msfa.mtp.subordinate;

import com.rspl.sf.msfa.mbo.SalesPersonBean;

import java.util.ArrayList;

/**
 * Created by e10769 on 10-Apr-18.
 */

public interface SalesPersonsPresenter {
    void onStart();
    void onDestroy();
    void onRefresh();
    void onSearch(String searchTxt);
    void onSearch(String searchTxt, ArrayList<SalesPersonBean> salesPersonBeans);
    void startFilter(String data);
}
