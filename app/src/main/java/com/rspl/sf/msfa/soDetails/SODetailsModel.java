package com.rspl.sf.msfa.soDetails;

import android.content.Context;

import com.rspl.sf.msfa.solist.SOListBean;


/**
 * Created by e10769 on 03-07-2017.
 */

public interface SODetailsModel {

    interface OnFinishedListener {
        void onFinished(SOListBean soListBean);
    }
    void findItems(Context mContext, OnFinishedListener listener, int comingFrom, SOListBean soListBean);
}
