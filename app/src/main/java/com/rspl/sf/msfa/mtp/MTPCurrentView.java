package com.rspl.sf.msfa.mtp;

import java.util.ArrayList;

/**
 * Created by e10860 on 2/16/2018.
 */

public interface MTPCurrentView {

    void onProgress();
    void ShowDialgueProgress();
    void HideDialgueProgress();
    void editAndApprove();

    void onHideProgress();

    void displayData(ArrayList<MTPHeaderBean> displayList);

    void displayLastRefreshedTime(String refreshTime);

    void displayViewPost(int pos);

    void showMsg(String message);

    void showSuccessMsg(String message);
}
