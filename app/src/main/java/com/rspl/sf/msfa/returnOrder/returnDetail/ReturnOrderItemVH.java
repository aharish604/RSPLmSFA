package com.rspl.sf.msfa.returnOrder.returnDetail;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rspl.sf.msfa.R;


/**
 * Created by e10526 on 12-03-2018.
 */

public class ReturnOrderItemVH  extends RecyclerView.ViewHolder {
    public final ImageView ivDelvStatus;
    public final TextView tvMaterialDesc, tvQty, tvAmount;

    public ReturnOrderItemVH(View viewItem, Context mContext) {
        super(viewItem);
        ivDelvStatus = (ImageView) viewItem.findViewById(R.id.ivDelvStatus);
        tvMaterialDesc = (TextView) viewItem.findViewById(R.id.tvMaterialDesc);
        tvQty = (TextView) viewItem.findViewById(R.id.tvQty);
        tvAmount = (TextView) viewItem.findViewById(R.id.tvAmount);
    }
}
