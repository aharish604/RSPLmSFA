package com.rspl.sf.msfa.socreate.shipToDetails;


import com.rspl.sf.msfa.solist.SOListBean;

/**
 * Created by e10769 on 21-12-2017.
 */

public interface ShipToPresenter {
    void onStart();

    void onDestroy();

    boolean validateFields(SOListBean soListBean);

    void startSimulate(SOListBean soListBeanHeader);

    void basedOnSalesOffice(String salesOfficeId, String customerNo);

    void basedOnCountry(String countryId);
}
