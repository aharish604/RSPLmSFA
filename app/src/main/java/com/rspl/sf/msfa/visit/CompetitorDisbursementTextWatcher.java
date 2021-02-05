package com.rspl.sf.msfa.visit;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.rspl.sf.msfa.interfaces.TextWatcherInterface;
import com.rspl.sf.msfa.mbo.CompetitorMasterBean;

import java.util.List;

/**
 * Created by e10769 on 18-02-2017.
 */

public class CompetitorDisbursementTextWatcher implements TextWatcher {
    private int position;
    private List<CompetitorMasterBean> stocksInfoBeanList;
    private TextWatcherInterface textWatcherInterface = null;


    CompetitorMasterBean stocksInfoBean;
    private EditText qtyEditText;

    public CompetitorDisbursementTextWatcher(List<CompetitorMasterBean>stocksInfoBeanList , TextWatcherInterface textWatcherInterface){
        this.stocksInfoBeanList = stocksInfoBeanList;
        this.textWatcherInterface = textWatcherInterface;
        this.stocksInfoBean=new CompetitorMasterBean();
    }

    public void updatePosition(int position, EditText qtyEditText) {
        this.position = position;
        this.qtyEditText = qtyEditText;


    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        stocksInfoBeanList.get(position).setQuantityInputText(charSequence.toString());
        if (!TextUtils.isEmpty(charSequence.toString()))
//            dealerStocksInfoBeanArrayList.add(stocksInfoBean);
        if (textWatcherInterface != null) {
            textWatcherInterface.textChane(charSequence + "", position);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


}
