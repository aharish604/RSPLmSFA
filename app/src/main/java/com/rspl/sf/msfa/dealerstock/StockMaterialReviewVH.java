package com.rspl.sf.msfa.dealerstock;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rspl.sf.msfa.R;


/**
 * Created by e10769 on 30-06-2017.
 */

public class StockMaterialReviewVH extends RecyclerView.ViewHolder {
    TextView tvMatDesc, tvLandingPrice;
    public StockMaterialReviewVH(View viewItem,StockCreateQtyTextWatcher soCreateQtyTextWatcher) {
        super(viewItem);
        tvMatDesc = (TextView) viewItem.findViewById(R.id.tvMatDesc);
        tvLandingPrice = (TextView) viewItem.findViewById(R.id.tvLandingPrice);
    }
}
