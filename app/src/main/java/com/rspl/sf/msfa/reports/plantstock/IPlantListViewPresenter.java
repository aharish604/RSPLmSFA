package com.rspl.sf.msfa.reports.plantstock;

/**
 * Created by e10893 on 15-02-2018.
 */

public interface IPlantListViewPresenter {
    void ProductListFresh();
    void displayRefreshTime(String refreshTime);
    void initializeClickListeners();
    void showProgressDialog();
    void hideProgressDialog1();
}
