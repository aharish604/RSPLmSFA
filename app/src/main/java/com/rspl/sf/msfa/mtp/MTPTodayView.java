package com.rspl.sf.msfa.mtp;

import java.util.ArrayList;

/**
 * Created by e10769 on 19-02-2018.
 */

public interface MTPTodayView {
    void onProgress();
    void onHideProgress();
    void displayData(ArrayList<MTPRoutePlanBean> displayList);
    void displayLastRefreshedTime(String displayTime);
    void showMsg(String msg);
}
