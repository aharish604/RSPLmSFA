package com.rspl.sf.msfa.collectionPlan.collectionCreate;

import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;

import com.rspl.sf.msfa.mbo.CustomerBean;

/**
 * Created by e10526 on 26-02-2018.
 */

public class RTGSRemarksWatcher implements TextWatcher {
    private CustomerBean customerBean = null;
    private RecyclerView.ViewHolder holder = null;

    public RTGSRemarksWatcher() {

    }

    public void updateTextWatcher(CustomerBean customerBean, RecyclerView.ViewHolder holder) {
        this.customerBean = customerBean;
        this.holder = holder;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (customerBean != null) {
            customerBean.setRemarks(s.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
