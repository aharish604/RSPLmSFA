package com.rspl.sf.msfa.returnOrder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rspl.sf.msfa.R;

/**
 * Created by e10860 on 12/28/2017.
 */

public class ReturnOrderVH extends RecyclerView.ViewHolder {

    public TextView tvOrderId, tvOrderDate, tvSOValue, tvSOQTY, tvMaterialName;
    public ImageView ivDelvStatus;

    public ReturnOrderVH(View itemView) {
        super(itemView);
        tvOrderId = (TextView) itemView.findViewById(R.id.tv_order_id);
        tvOrderDate = (TextView) itemView.findViewById(R.id.tv_order_date);
        tvSOValue = (TextView) itemView.findViewById(R.id.tv_so_value);
        tvSOQTY = (TextView) itemView.findViewById(R.id.tv_so_qty);
        tvMaterialName = (TextView) itemView.findViewById(R.id.tvMaterialName);
        ivDelvStatus = (ImageView) itemView.findViewById(R.id.ivDeliveryStatus);
    }
}
