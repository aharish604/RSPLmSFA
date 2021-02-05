package com.rspl.sf.msfa.reports.materialgrouptargets;

import android.content.Intent;

/**
 * Created by e10847 on 19-12-2017.
 */

public interface MaterialGroupPresenter {
    void onFilter();
    void onSearch(String searchText);
    void onRefresh();
    void startFilter(int requestCode, int resultCode, Intent data);
    void onStart();
    void onDestroy();
}
