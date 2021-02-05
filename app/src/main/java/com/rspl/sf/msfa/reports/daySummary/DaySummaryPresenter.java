package com.rspl.sf.msfa.reports.daySummary;

import android.content.Intent;

/**
 * Created by e10847 on 19-12-2017.
 */

public interface DaySummaryPresenter {
    void onFilter();
    void onSearch(String searchText);
    void onRefresh();
    void startFilter(int requestCode, int resultCode, Intent data);
    void onStart();
    void onDestroy();
    void totalOrderValue();
    /*void reloadSOCount();
    void reloadMTPCount();*/
}
