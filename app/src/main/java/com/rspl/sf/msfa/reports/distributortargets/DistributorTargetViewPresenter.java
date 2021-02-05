package com.rspl.sf.msfa.reports.distributortargets;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rspl.sf.msfa.mbo.MyTargetsBean;

import java.util.ArrayList;

/**
 * Created by E10953 on 31-07-2019.
 */
public interface DistributorTargetViewPresenter {
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
    void displayRefreshTime(String refreshTime);
    void displayList(ArrayList<MyTargetsBean> alTargets);
    void displayMessage(String msg);
}
