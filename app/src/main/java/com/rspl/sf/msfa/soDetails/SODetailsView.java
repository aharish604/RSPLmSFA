package com.rspl.sf.msfa.soDetails;


import com.rspl.sf.msfa.socreate.CreditLimitBean;
import com.rspl.sf.msfa.solist.SOListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10769 on 03-07-2017.
 */

public interface SODetailsView {

    void displayHeaderList(SOListBean soListBean);
    void showProgressDialog(String message);
    void hideProgressDialog();
    void displayMessage(String message);
    void showMessage(String message, boolean isSimpleDialog);
    void conformationDialog(String message, int from);
    void displayNotes(ArrayList<SOTextBean> soTextBeanArrayList);
    void openReviewScreen(List<CreditLimitBean> limitBeanList, SOListBean soListBeanHeader);
    void showApprovalSuccMsg(String string);
}
