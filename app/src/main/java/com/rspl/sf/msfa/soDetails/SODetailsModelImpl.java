package com.rspl.sf.msfa.soDetails;

import android.content.Context;

import com.rspl.sf.msfa.solist.SOListBean;


/**
 * Created by e10769 on 03-07-2017.
 */

public class SODetailsModelImpl implements SODetailsModel {
    @Override
    public void findItems(Context mContext, OnFinishedListener listener, int comingFrom, SOListBean soListBean) {
//        if (comingFrom == 2) {
//            soListBean = SOUtils.getHeaderData(soListBean, soListBean.getSONo());
//        }
        listener.onFinished(soListBean);
    }
}
