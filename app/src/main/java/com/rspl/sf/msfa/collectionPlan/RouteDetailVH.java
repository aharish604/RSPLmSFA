package com.rspl.sf.msfa.collectionPlan;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rspl.sf.msfa.R;

/**
 * Created by e10860 on 2/16/2018.
 */

public class RouteDetailVH extends RecyclerView.ViewHolder {
    public TextView tvDate;
    public TextView tvName;
    public TextView tvRemarks;
    public TextView tvDesc;
    public TextView tvDay;
    public CardView cvItem;


    public RouteDetailVH(View itemView) {
        super(itemView);
        tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        tvName = (TextView) itemView.findViewById(R.id.tvName);
        tvRemarks = (TextView) itemView.findViewById(R.id.tvRemarks);
        tvDesc = (TextView) itemView.findViewById(R.id.tvDesc);
        tvDay = (TextView) itemView.findViewById(R.id.tvDay);
        cvItem = (CardView) itemView.findViewById(R.id.cvItem);
    }
}
