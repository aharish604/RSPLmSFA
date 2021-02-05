package com.rspl.sf.msfa.reports.plantstock.filter;



import com.rspl.sf.msfa.mbo.ConfigTypesetTypesBean;

import java.util.ArrayList;

/**
 * Created by e10769 on 31-10-2017.
 */

public interface StockFilterView {
    void displayList(ArrayList<ConfigTypesetTypesBean> brnadsVal, ArrayList<ConfigTypesetTypesBean> configTypesetDeliveryList);
    void showMessage(String message);
    void showProgressDialog();
    void hideProgressDialog();
}
