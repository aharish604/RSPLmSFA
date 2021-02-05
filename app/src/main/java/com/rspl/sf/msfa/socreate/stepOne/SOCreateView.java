package com.rspl.sf.msfa.socreate.stepOne;


import com.rspl.sf.msfa.so.PaymentTermBean;
import com.rspl.sf.msfa.so.UnlRecBean;
import com.rspl.sf.msfa.so.ValueHelpBean;
import com.rspl.sf.msfa.socreate.DefaultValueBean;

import java.util.ArrayList;

/**
 * Created by e10769 on 29-06-2017.
 */

public interface SOCreateView {
    void showProgressDialog(String message);
    void hideProgressDialog();
    void displaySoldToParty(String[][] arrCustomers);
    void displayByCustomer(ArrayList<DefaultValueBean> customerSalesAreaArrayList, ArrayList<ValueHelpBean> OrdtypeList);
    void displayBySalesArea(ArrayList<ValueHelpBean> salesOfficeList, ArrayList<ValueHelpBean> plantList, ArrayList<ValueHelpBean> salesGrpList);
    void displayMessage(String message);
    void displayPaymentTerm(ArrayList<PaymentTermBean> paymentTermList);
    void displayUnloadingPt(ArrayList<UnlRecBean> unloadingList);
    void displayReceivingPt(ArrayList<UnlRecBean> receivingList);
    void errorSoldTo();
    void errorSalesArea();
    void errorOrderType();
    void errorPlant();
    void errorShipToParty();
    void errorPaymentTerm();
    void errorIncoTerm();
    void errorIncoTerm2();
    void errorShippingCondition();
    void errorUnloading();
    void errorReceiving();


}
