package com.rspl.sf.msfa.collectionPlan.collectionCreate;

import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.rspl.sf.msfa.mbo.CustomerBean;

/**
 * Created by e10526 on 26-02-2018.
 */

public class RTGSTextWatcher implements TextWatcher {
    private CustomerBean customerBean = null;
    private RecyclerView.ViewHolder holder = null;
    EditText etAmount,etRemarks,etAmount1,etRemarks2;
    public RTGSTextWatcher() {

    }

    public void updateTextWatcher(CustomerBean customerBean, RecyclerView.ViewHolder holder,EditText etAmount,EditText etRemarks,EditText etAmount1,EditText etRemarks2) {
        this.customerBean = customerBean;
        this.holder = holder;
        this.etAmount = etAmount;
        this.etRemarks = etRemarks;
        this.etAmount1 = etAmount1;
        this.etRemarks2 = etRemarks2;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        /*if(holder!=null){
            ((RTGSCreateVH)holder).tiCollectionAmount.setErrorEnabled(false);
        }
        try {
            if (customerBean != null) {
                    customerBean.setAmount(s.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void afterTextChanged(Editable s) {
        try {
            if(s!=null) {
                if (s == etAmount.getEditableText()) {
                    if (customerBean != null) {
                        customerBean.setAmount(s.toString());
                    }
                } else if (s == etAmount1.getEditableText()) {
                    if (customerBean != null) {
                        customerBean.setAmount1(s.toString());
                    }
                } else if (s == etRemarks.getEditableText()) {
                    if (customerBean != null) {
                        customerBean.setRemarks(s.toString());
                    }
                } else if (s == etRemarks2.getEditableText()) {
                    if (customerBean != null) {
                        customerBean.setRemarks1(s.toString());
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
