package com.rspl.sf.msfa.reports.distributorTrend;

import com.rspl.sf.msfa.mbo.MyPerformanceBean;

import java.util.ArrayList;

public interface DistributorTrendView {
    void showProgress();

    void hideProgress();

    void displayMsg(String msg);

    void displayList(ArrayList<MyPerformanceBean> alRetTrends);

    void displayLstSyncTime(String lastSeenDateFormat);
}
