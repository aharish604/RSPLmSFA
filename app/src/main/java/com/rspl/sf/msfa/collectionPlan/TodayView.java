package com.rspl.sf.msfa.collectionPlan;

import java.util.ArrayList;

/**
 * Created by e10769 on 19-02-2018.
 */

public interface TodayView {
    void onProgress();
    void onHideProgress();
    void displayData(ArrayList<WeekHeaderList> displayList);
    void displayLastRefreshedTime(String displayTime);
    void showMsg(String msg);
}
