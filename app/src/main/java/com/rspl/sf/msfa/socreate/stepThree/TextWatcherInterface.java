package com.rspl.sf.msfa.socreate.stepThree;


import com.rspl.sf.msfa.socreate.SOItemBean;

public interface TextWatcherInterface {
    void textChane(String charSequence, int position, SOQtyVH holder, SOItemBean soItemBean, SOSubItemAdapter soSubItemAdapter, boolean isTyped);
}