package com.rspl.sf.msfa.returnOrder.retrunFilter;


import com.rspl.sf.msfa.mbo.ConfigTypesetTypesBean;

import java.util.ArrayList;

/**
 * Created by e10860 on 12/29/2017.
 */

public interface FilterReturnOrderView {
    void displayList(ArrayList<ConfigTypesetTypesBean> configTypesetTypesBeen, ArrayList<ConfigTypesetTypesBean> configTypesetDeliveryList);

    void showMessage(String message);

    void showProgressDialog();

    void hideProgressDialog();
}
