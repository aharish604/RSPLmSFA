package com.rspl.sf.msfa.socreate.stepThree;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.socreate.SOSubItemBean;

import java.util.ArrayList;

/**
 * Created by e10769 on 09-05-2017.
 */

public class SOSubQtyTextWatcher implements TextWatcher {
    private int position;
    private ArrayList<SOSubItemBean> soSubItemBeanArrayList;
    private SubTextWatcherInterface subTextWatcherInterface = null;
    private EditText qtyEditText;
    private int headerPos;
    private SOItemBean soItemBean;
    private SOSubItemBean soSubItemBean;
    private ImageView ivAddSchedule;

    public SOSubQtyTextWatcher(ArrayList<SOSubItemBean> soSubItemBeanArrayList, SubTextWatcherInterface subTextWatcherInterface) {
        this.soSubItemBeanArrayList = soSubItemBeanArrayList;
        this.subTextWatcherInterface = subTextWatcherInterface;
    }

    public void updatePosition(int position, EditText qtyEditText, int headerPos, SOItemBean soItemBean, SOSubItemBean soSubItemBean, ImageView ivAddSchedule) {
        this.position = position;
        this.headerPos = headerPos;
        this.qtyEditText = qtyEditText;
        this.soItemBean = soItemBean;
        this.soSubItemBean=soSubItemBean;
        this.ivAddSchedule = ivAddSchedule;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        soSubItemBeanArrayList.get(position).setSubQty(charSequence.toString());
        if (subTextWatcherInterface != null) {
            subTextWatcherInterface.subTextChange(charSequence + "", position,headerPos,soItemBean,soSubItemBean,ivAddSchedule);
        }
        if (!charSequence.toString().isEmpty()) {
            qtyEditText.setBackgroundResource(R.drawable.edittext);
            qtyEditText.setError(null);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}
