package com.rspl.sf.msfa.soDetails;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rspl.sf.msfa.R;


/**
 * Created by e10769 on 02-12-2017.
 */

public class SOItemDetailsVH1 extends RecyclerView.ViewHolder {
    public final ImageView ivDelvStatus,iv_deleted,ivAdditems;
    public final TextView tvMaterialDesc, tvQty, tvAmount;

    public SOItemDetailsVH1(View viewItem, Context mContext) {
        super(viewItem);
        ivDelvStatus = (ImageView)viewItem.findViewById(R.id.ivDelvStatus);
        ivAdditems = (ImageView)viewItem.findViewById(R.id.ivAdditems);
        iv_deleted = (ImageView)viewItem.findViewById(R.id.iv_deleted);
        tvMaterialDesc = (TextView)viewItem.findViewById(R.id.tvMaterialDesc);
        tvQty = (TextView)viewItem.findViewById(R.id.tvQty);
        tvAmount = (TextView)viewItem.findViewById(R.id.tvAmount);
    }
}
