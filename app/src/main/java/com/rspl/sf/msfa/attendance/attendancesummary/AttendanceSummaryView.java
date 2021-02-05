package com.rspl.sf.msfa.attendance.attendancesummary;

import com.rspl.sf.msfa.returnOrder.ReturnOrderBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10860 on 12/28/2017.
 */

public interface AttendanceSummaryView {

    void showMessage(String message);

    void dialogMessage(String message);

    void showProgressDialog();

    void hideProgressDialog();

    void displayRefreshTime(String refreshTime);

    void displayList(ArrayList<AttendanceSummaryBean> list);
}
