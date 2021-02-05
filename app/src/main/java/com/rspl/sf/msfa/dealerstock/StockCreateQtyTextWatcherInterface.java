package com.rspl.sf.msfa.dealerstock;

import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by e10769 on 20-12-2017.
 */

public interface StockCreateQtyTextWatcherInterface {
    void onTextChange(String charSequence, DealerStockBean soItemBean, RecyclerView.ViewHolder holder);
}
