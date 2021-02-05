package com.rspl.sf.msfa.socreate.stepThree;


import com.rspl.sf.msfa.socreate.SOItemBean;

/**
 * Created by e10769 on 13-05-2017.
 */

public interface OnAddScheduleInterface {
    void onAddListener(SOQtyVH holder, SOItemBean soItemBean);
    void onDeleteListener(SOQtyVH holder, SOItemBean soItemBean, int pos);
}
