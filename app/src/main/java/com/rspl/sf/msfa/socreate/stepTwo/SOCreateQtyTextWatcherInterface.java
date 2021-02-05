package com.rspl.sf.msfa.socreate.stepTwo;

import androidx.recyclerview.widget.RecyclerView;

import com.rspl.sf.msfa.socreate.SOItemBean;


/**
 * Created by e10769 on 20-12-2017.
 */

public interface SOCreateQtyTextWatcherInterface {
    void onTextChange(String charSequence, SOItemBean soItemBean, RecyclerView.ViewHolder holder);
}
