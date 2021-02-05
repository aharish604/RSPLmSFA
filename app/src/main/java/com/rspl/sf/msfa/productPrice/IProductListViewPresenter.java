package com.rspl.sf.msfa.productPrice;

/**
 * Created by e10893 on 15-02-2018.
 */

public interface IProductListViewPresenter {
    void ProductListFresh();
    void displayRefreshTime(String refreshTime);
    void initializeClickListeners();
    void showProgressDialog();
    void hideProgressDialog1();
    void displayMsg(String msg);
}
