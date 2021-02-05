package com.rspl.sf.msfa.reports.targets;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rspl.sf.msfa.mbo.MyTargetsBean;

import java.util.ArrayList;

/**
 * Created by e10893 on 25-01-2018.
 */

public interface TargetViewPresenter {

    interface TargetResponse<T>{
        void success(ArrayList<T> success);
        void error(String message);
    }
    void initializeUI(Context context);
    void initializeClickListeners();
    void initializeObjects(Context context);
    void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager);
    void showMessage(String message);
    void dialogMessage(String message, String msgType);
    void showProgressDialog();
    void hideProgressDialog();
    void setFilterDate(String filterType);
    void searchResult(ArrayList<MyTargetsBean> alMyTargets);
    void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus);
    void TargetSync();
    void displayRefreshTime(String refreshTime);
    void displayList(ArrayList<MyTargetsBean> alTargets);
    void displayMessage(String msg);
}
