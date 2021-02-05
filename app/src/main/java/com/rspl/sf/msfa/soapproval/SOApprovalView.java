package com.rspl.sf.msfa.soapproval;

import com.rspl.sf.msfa.solist.SOListBean;

import java.util.ArrayList;

public interface SOApprovalView {
    void displaySearchList(ArrayList<SOListBean> soItemList);
    void showProgress();
    void hideProgress();
    void displayRefreshTime(long refreshTime);
}
