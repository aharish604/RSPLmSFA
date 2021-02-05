package com.rspl.sf.msfa.reports.plantstock;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.socreate.stepTwo.SOCreateQtyTextWatcher;


/**
 * Created by e10769 on 20-12-2017.
 */

 public class PlantStockMatlVH extends RecyclerView.ViewHolder {
    public TextView tvMatDesc, tvPrice, tvPlant;

    public PlantStockMatlVH(View viewItem, SOCreateQtyTextWatcher soCreateQtyTextWatcher) {
        super(viewItem);
        tvMatDesc = (TextView) viewItem.findViewById(R.id.tvMatDesc);
        tvPrice = (TextView) viewItem.findViewById(R.id.tv_price);
        tvPlant = (TextView) viewItem.findViewById(R.id.tvPlant);

    }
}
