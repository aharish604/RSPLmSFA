package com.rspl.sf.msfa.socreate.shipToDetails;


import com.rspl.sf.msfa.so.ValueHelpBean;
import com.rspl.sf.msfa.socreate.CreditLimitBean;
import com.rspl.sf.msfa.socreate.CustomerPartnerFunctionBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10769 on 21-12-2017.
 */

public interface ShipToView {
    void showProgressDialog(String message);

    void hideProgressDialog();

    void displayMessage(String message);

    void displayBySalesArea(ArrayList<CustomerPartnerFunctionBean> shipToList, ArrayList<ValueHelpBean> incoterm1List, ArrayList<ValueHelpBean> paymentTermList, ArrayList<ValueHelpBean> salesOfficeList, ArrayList<ValueHelpBean> shippingConditionList, ArrayList<ValueHelpBean> countryList);

    void errorOrderType(String message);

    void errorShipToParty(String message);

    void errorPaymentTerm(String message);

    void errorIncoTerm(String message);

    void errorIncoTerm2(String message);

    void errorShippingCondition(String message);

    void errorLastName(String message);

    void errorAddress1(String s);

    void errorDistrict(String s);

    void errorCity(String s);

    void errorCountry(String s);

    void errorRegion(String s);

    void errorPostalCode(String s);

    void openReviewScreen( List<CreditLimitBean> limitBeanList);

    void displaySalesGrp(ArrayList<ValueHelpBean> salesGrpList);

    void displayOneTimeShipToParty();

    void displayRegion(ArrayList<ValueHelpBean> regionList);


}
