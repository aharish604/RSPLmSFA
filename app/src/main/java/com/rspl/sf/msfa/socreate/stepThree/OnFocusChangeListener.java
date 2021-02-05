package com.rspl.sf.msfa.socreate.stepThree;

import android.view.View;

import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.socreate.SOSubItemBean;


/**
 * Created by e10769 on 16-05-2017.
 */

public interface OnFocusChangeListener {
    void focusChangeListenerHeader(View v, boolean hasFocus, SOQtyVH holder, SOItemBean soItemBean);
    void focusChangeListenerItem(View v, boolean hasFocus, SOQtyVH holder, SOItemBean soItemBean, SOSubItemBean soSubItemBean);
}
