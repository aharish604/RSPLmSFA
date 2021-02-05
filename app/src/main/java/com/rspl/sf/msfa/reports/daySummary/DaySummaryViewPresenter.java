package com.rspl.sf.msfa.reports.daySummary;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rspl.sf.msfa.mbo.MyTargetsBean;

import java.util.ArrayList;

/**
 * Created by e10893 on 25-01-2018.
 */

public interface DaySummaryViewPresenter {

    interface DaySummResponse<T>{
        void success(ArrayList<T> success);
        void error(String message);
    }
    void initializeUI(Context context);
    void initializeClickListeners();
    void initializeObjects(Context context);
    void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager);
    void initializeRecyclerViewDashBoardItems(LinearLayoutManager linearLayoutManager);
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
    void displayDashBoardList(ArrayList<DashBoardBean> boardBeans);
    void displayDashBoardError(String error);
    void displayAttendanceview();
    void showMTPProgress();
    void showDashMonthProgress();
    void showDashDayProgress();
    void showSOProgress();
    void showAttendancePB();
    void hideMTPProgress();
    void hideSOProgress();
    void hideAttendancePB();
    void disPlayMTPCount(String count);
    void disPlaySOCount(String count);
    void refreshTotalOrderVale();
}
