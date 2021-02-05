package com.rspl.sf.msfa.returnOrder;

import android.content.Intent;

/**
 * Created by e10860 on 12/28/2017.
 */

public interface ReturnOrderPresenter {
    void onStart();
    void onDestroy();
    void onResume();
    void onSearch(String searchText);
    void onRefresh();
    void getRefreshTime();
    void onFilter();
    void startFilter(int requestCode, int resultCode, Intent data);

    void onItemClick(ReturnOrderBean returnOrderBeanDeatil);

}
