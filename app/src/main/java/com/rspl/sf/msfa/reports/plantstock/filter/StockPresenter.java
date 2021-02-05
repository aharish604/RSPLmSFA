package com.rspl.sf.msfa.reports.plantstock.filter;

import android.content.Intent;

/**
 * Created by e10769 on 16-02-2018.
 */

public interface StockPresenter {
    void onStart();

    void onDestroy();

    boolean onSearch(String searchText, Object objects);

    void onSearch(String searchText);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onFilter();
    void startFilter(int requestCode, int resultCode, Intent data);
}
