package com.rspl.sf.msfa.socreate.stepOne;


import com.rspl.sf.msfa.solist.SOListBean;

/**
 * Created by e10769 on 29-06-2017.
 */

public interface SOCreatePresenter {
    void onStart();
    void onDestroy();
    void getBasedOnCustomer(String customerNo);
    void getBasedOnBsdOnSaleArea(String customerNo, String salesArea);
    void getBasedOnBsdOnOrderNo(String customerNo, String salesArea, String orderNo);
    void getUnloading(String customerNo, String salesArea, String orderNo, String plant, String mStrIncoTermId, String mStrShippingConditionId);
    void getReceiving(String customerNo, String salesArea, String orderNo, String plant, String mStrIncoTermId, String mStrShippingConditionId, String unloadingPoint);
    void getOnPlant(String customerNo, String salesArea, String orderNo, String plantId);
    boolean validateHeader(SOListBean soListBean);
    void getMaterial(String customerNo, String plant);
}
