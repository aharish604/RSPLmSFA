package com.rspl.sf.msfa.soDetails;

/**
 * Created by e10769 on 03-07-2017.
 */

public interface SODetailsPresenter {
    void onStart();
    void onDestroy();
    void onSaveData();
    void onUpdate();
    void onAsignData(String save, String strRejReason, String strRejReasonDesc);
    void postNotes(String messageNote);
    void pdfDownload();
    void onCancelData();

    void approveData(String ids, String description, String approvalStatus);
}
