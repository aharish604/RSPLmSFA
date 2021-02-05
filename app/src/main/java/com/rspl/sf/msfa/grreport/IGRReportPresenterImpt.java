package com.rspl.sf.msfa.grreport;

import android.content.Intent;

public interface IGRReportPresenterImpt {
    void connectToOfflineDB();
    void onStart();
    void onUploadData();
    void onDestroy();
    void onResume();
    void onFilter();
    void onSearch(String searchText);
    void onRefresh();
    void startFilter(int requestCode, int resultCode, Intent data);
    void getRefreshTime();

    void getDetails(String no);
}
