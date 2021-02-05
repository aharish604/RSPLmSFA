package com.rspl.sf.msfa.dealerstockprice;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.interfaces.TextWatcherInterface;

import java.util.List;

/**
 * Created by e10769 on 18-02-2017.
 */

/*
public class StocksInfoDisbursementTextWatcher implements TextWatcher {
    private int position;
    private List<DealerPriceBean> dealerPriceList;
    private TextWatcherInterface textWatcherInterface = null;
    private EditText qtyEditText;

    public StocksInfoDisbursementTextWatcher(List<DealerPriceBean>dealerpriceList , TextWatcherInterface textWatcherInterface){
        this.dealerPriceList = dealerpriceList;
        this.textWatcherInterface = textWatcherInterface;
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
        dealerPriceList.get(position).setInputPrice(charSequence.toString());
        if (textWatcherInterface != null) {
            textWatcherInterface.textChane(charSequence + "", position);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}
*/


public class StocksInfoDisbursementTextWatcher implements TextWatcher {
    private int position;
    private List<DealerPriceBean> retailerStockBeanList;
    private com.rspl.sf.msfa.interfaces.TextWatcherInterface textWatcherInterface = null;
    private EditText qtyEditText;

    public StocksInfoDisbursementTextWatcher(List<DealerPriceBean> retailerStockBeanList, TextWatcherInterface textWatcherInterface) {
        this.retailerStockBeanList = retailerStockBeanList;
        this.textWatcherInterface = textWatcherInterface;
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
        retailerStockBeanList.get(position).setInputPrice(charSequence.toString());
        if (textWatcherInterface != null) {
            textWatcherInterface.textChane(charSequence + "", position);
        }
        if (!charSequence.toString().isEmpty()) {
            qtyEditText.setBackgroundResource(R.drawable.edittext);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}

