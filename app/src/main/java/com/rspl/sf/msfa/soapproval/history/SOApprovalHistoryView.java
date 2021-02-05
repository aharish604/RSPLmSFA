package com.rspl.sf.msfa.soapproval.history;

import com.rspl.sf.msfa.solist.SOTaskHistoryBean;

import java.util.ArrayList;

/**
 * Created by e10769 on 16-Mar-18.
 */

public interface SOApprovalHistoryView {
    void showProgressDialog();
    void hideProgressDialog();
    void displayResult(ArrayList<SOTaskHistoryBean> soTaskHistoryBeanArrayList);
    void showMessage(String msg);
}
