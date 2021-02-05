package com.rspl.sf.msfa.returnOrder.returnDetail;

/**
 * Created by e10526 on 12-03-2018.
 */

public interface RODetailsPresenter {
    void onStart();
    void onDestroy();
    void pdfDownload();
    void onItemClick(ReturnOrderItemBean returnOrderBeanDeatil);
}
