package com.rspl.sf.msfa.socreate.stepThree;

import android.widget.ImageView;

import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.socreate.SOSubItemBean;


/**
 * Created by e10769 on 09-05-2017.
 */

public interface SubTextWatcherInterface {
    void subTextChange(String charSequence, int subPos, int headerPos, SOItemBean soItemBean, SOSubItemBean soSubItemBean, ImageView ivAddSchedule);
}
