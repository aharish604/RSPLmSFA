package com.rspl.sf.msfa.returnOrder.returnDetail;

import com.rspl.sf.msfa.returnOrder.ReturnOrderBean;

/**
 * Created by e10526 on 12-03-2018.
 */

public interface RODetailsView {
    void displayHeaderList(ReturnOrderBean roListBean);
    void showProgressDialog(String s);
    void hideProgressDialog();
    void showMessage(String message, boolean isSimpleDialog);
}
