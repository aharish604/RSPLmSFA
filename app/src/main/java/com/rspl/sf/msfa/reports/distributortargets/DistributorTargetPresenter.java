package com.rspl.sf.msfa.reports.distributortargets;

import android.content.Context;
import android.content.Intent;

import com.rspl.sf.msfa.mbo.MyTargetsBean;

import java.util.ArrayList;

/**
 * Created by E10953 on 31-07-2019.
 */
public interface DistributorTargetPresenter {
    void onFilter();
    void onSearch(String searchText);
    void onRefresh();
    void startFilter(int requestCode, int resultCode, Intent data);
    void onStart();
    void onDestroy();
}
