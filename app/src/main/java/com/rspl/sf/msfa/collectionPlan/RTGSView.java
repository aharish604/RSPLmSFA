package com.rspl.sf.msfa.collectionPlan;

import java.util.ArrayList;

/**
 * Created by e10860 on 2/16/2018.
 */

public interface RTGSView {

    void onProgress();
    void onHideProgress();
    void displayData(ArrayList<WeekHeaderList> displayList);
    void displayLastRefreshedTime();
    void displayViewPost(int i);
    void showMsg(String message);
    void showSuccessMsg(String message);
    void displayLastRefreshedTime(String refreshTime);
}
