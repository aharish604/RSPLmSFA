package com.rspl.sf.msfa.collectionPlan;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rspl.sf.msfa.R;

/**
 * Created by e10769 on 19-02-2018.
 */

public class TodayVH extends RecyclerView.ViewHolder {
//    public TextView tvDate;
    public TextView tvName;
    public TextView tvRemarks;
    public TextView tvDesc;
//    public TextView tvDay;


    public TodayVH(View itemView) {
        super(itemView);
      //  tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        tvName = (TextView) itemView.findViewById(R.id.tvName);
        tvRemarks = (TextView) itemView.findViewById(R.id.tvRemarks);
        tvDesc = (TextView) itemView.findViewById(R.id.tvDesc);
      //  tvDay = (TextView) itemView.findViewById(R.id.tvDay);
    }
}
