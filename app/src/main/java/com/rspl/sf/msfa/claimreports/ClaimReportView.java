package com.rspl.sf.msfa.claimreports;

import com.rspl.sf.msfa.attendance.attendancesummary.AttendanceSummaryBean;

import java.util.ArrayList;

/**
 * Created by e10860 on 12/28/2017.
 */

public interface ClaimReportView {

    void showMessage(String message);

    void dialogMessage(String message);

    void showProgressDialog();

    void hideProgressDialog();

    void displayRefreshTime(String refreshTime);

    void displayList(ArrayList<ClaimReportBean> list, String totalClaimAmt, String totalMaxClaimAmt);
}
