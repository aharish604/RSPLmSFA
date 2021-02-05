package com.rspl.sf.msfa.reports.behaviourlist.filter;


import com.rspl.sf.msfa.mbo.ConfigTypesetTypesBean;

import java.util.ArrayList;

/**
 * Created by e10893 on 25-01-2018.
 */

public interface IBehaviourFilterView {
    void displayList(ArrayList<ConfigTypesetTypesBean> configTypesetTypesBeen, ArrayList<ConfigTypesetTypesBean> configTypesetDeliveryList);
    void showMessage(String message);
    void showProgressDialog();
    void hideProgressDialog();
}
