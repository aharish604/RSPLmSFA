package com.rspl.sf.msfa.reports.invoicelist;

import android.content.Intent;

/**
 * Created by e10847 on 19-12-2017.
 */

public interface IInvoiceListPresenter {
    void onFilter();
    void onSearch(String searchText);
    void onRefresh();
    void startFilter(int requestCode, int resultCode, Intent data);
    void getInvoiceList();
    void onDestroy();
}
