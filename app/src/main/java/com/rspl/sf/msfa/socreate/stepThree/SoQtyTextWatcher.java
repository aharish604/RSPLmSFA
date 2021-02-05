package com.rspl.sf.msfa.socreate.stepThree;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.socreate.SOItemBean;

import java.util.ArrayList;


/**
 * Created by e10769 on 03-05-2017.
 */

public class SoQtyTextWatcher implements TextWatcher {
    private int position;
    private ArrayList<SOItemBean> soItemBeanArrayList;
    private TextWatcherInterface textWatcherInterface = null;
    private EditText qtyEditText;
    private SOQtyVH holder;
    private SOItemBean soItemBean;
    private SOSubItemAdapter soSubItemAdapter=null;
    private boolean isTyped=false;

    public SoQtyTextWatcher(ArrayList<SOItemBean> soItemBeanArrayList, TextWatcherInterface textWatcherInterface) {
        this.soItemBeanArrayList = soItemBeanArrayList;
        this.textWatcherInterface = textWatcherInterface;
    }

    public void updatePosition(int position, EditText qtyEditText, SOQtyVH holder, SOItemBean soItemBean, SOSubItemAdapter soSubItemAdapter, boolean isTyped) {
        this.position = position;
        this.qtyEditText = qtyEditText;
        this.holder=holder;
        this.soItemBean=soItemBean;
        this.soSubItemAdapter=soSubItemAdapter;
        this.isTyped=isTyped;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        soItemBeanArrayList.get(position).setSoQty(charSequence.toString());
        if (textWatcherInterface != null) {
            textWatcherInterface.textChane(charSequence + "", position,holder, soItemBean,soSubItemAdapter,isTyped);
        }
        isTyped=true;
        if (!charSequence.toString().isEmpty()) {
            qtyEditText.setBackgroundResource(R.drawable.edittext);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}