package com.rspl.sf.msfa.mtp;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rspl.sf.msfa.R;

/**
 * Created by e10769 on 20-02-2018.
 */

class MTPTodayVH extends RecyclerView.ViewHolder {
    ImageView ivMobile;
    //    ConstraintLayout clView4, clView2;
    public TextView tvName;
    public TextView tvRemarks;
    public TextView tvDesc;
    public TextView tvHName;


    public MTPTodayVH(View itemView) {
        super(itemView);
        tvName = (TextView) itemView.findViewById(R.id.tvName);
        tvRemarks = (TextView) itemView.findViewById(R.id.tvRemarks);
        tvDesc = (TextView) itemView.findViewById(R.id.tvDesc);
        tvHName = (TextView) itemView.findViewById(R.id.tvHName);
        ivMobile = (ImageView) itemView.findViewById(R.id.iv_mobile);
//        clView4 = (ConstraintLayout) itemView.findViewById(R.id.clView4);
//        clView2 = (ConstraintLayout) itemView.findViewById(R.id.clView2);
    }
}
