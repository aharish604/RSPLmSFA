package com.rspl.sf.msfa.dealerstock;

import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;


/**
 * Created by e10769 on 20-12-2017.
 */

public class StockCreateQtyTextWatcher implements TextWatcher {
    private DealerStockBean dlrstockBean = null;
    private RecyclerView.ViewHolder holder = null;
    private StockCreateQtyTextWatcherInterface createQtyTextWatcherInterface = null;

    public StockCreateQtyTextWatcher() {

    }

    public void updateTextWatcher(DealerStockBean soItemBean, RecyclerView.ViewHolder holder, StockCreateQtyTextWatcherInterface createQtyTextWatcherInterface) {
        this.dlrstockBean = soItemBean;
        this.holder = holder;
        this.createQtyTextWatcherInterface = createQtyTextWatcherInterface;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (dlrstockBean != null) {
            dlrstockBean.setEnterdQty(s.toString());
        }
        if (createQtyTextWatcherInterface != null) {
            createQtyTextWatcherInterface.onTextChange(s.toString(), dlrstockBean, holder);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
       /* if (!TextUtils.isEmpty(s.toString())) {
            String text = Double.toString(Math.abs(Double.parseDouble(s.toString())));
            int integerPlaces = text.indexOf('.');
            int decimalPlaces = text.length() - integerPlaces - 1;
            int charLength = integerPlaces + 1 + decimalPlaces;
            if (charLength<=13 && decimalPlaces >=3) {
                if (holder instanceof SOMultiMaterialVH) {
                    ((SOMultiMaterialVH) holder).etQty.setFilters(new InputFilter[]{new InputFilter.LengthFilter(charLength)});
                }
            }
        }*/
    }

}
