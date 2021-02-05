package com.rspl.sf.msfa.routeplan.customerslist;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rspl.sf.msfa.mbo.CustomerBean;

import java.util.ArrayList;

/**
 * Created by e10847 on 19-12-2017.
 */

public interface ICustomerViewPresenter<T> {
    void initializeUI(Context context);
    void initializeClickListeners();
    void initializeObjects(Context context);
    void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager);
    void showProgressDialog();
    void hideProgressDialog();
    void onRefreshData();
    void customersListSync();
    void openFilter(String filterType, String status, String grStatus);
    void searchResult(ArrayList<CustomerBean> retailerSearchList);
    void setFilterDate(String filterType);
    void displayRefreshTime(String refreshTime);
    void displayMsg(String msg);
    void sendSelectedItem(Intent intent);
    void errorMsgEditText(String error);
    void errorMsgEditText1(String error);

}
