package com.rspl.sf.msfa.soDetails;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rspl.sf.msfa.R;


/**
 * Created by e10769 on 01-12-2017.
 */

public class PricingDetailsVH extends RecyclerView.ViewHolder {
    public final TextView tvTotalAmount;
    public final TextView tvAmount;
    public final TextView tvDescription;
    public final TextView tvPercentage;

    public PricingDetailsVH(View itemView) {
        super(itemView);
        tvDescription = (TextView) itemView.findViewById(R.id.tv_description);
        tvPercentage = (TextView) itemView.findViewById(R.id.tv_percentage);
        tvAmount = (TextView) itemView.findViewById(R.id.tv_amount);
        tvTotalAmount = (TextView) itemView.findViewById(R.id.tv_total_anount);
    }
}
