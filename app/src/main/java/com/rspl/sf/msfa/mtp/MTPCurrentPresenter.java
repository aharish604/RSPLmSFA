package com.rspl.sf.msfa.mtp;

import android.content.Intent;

import com.rspl.sf.msfa.mtp.approval.MTPApprovalBean;

/**
 * Created by e10860 on 2/16/2018.
 */

public interface MTPCurrentPresenter {
    void onStart();

    void onDestroy();

    void onRefresh();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onSaveData(String saveType);

    void mtpDetails(MTPApprovalBean mtpApprovalBean);
}
