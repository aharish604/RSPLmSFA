package com.rspl.sf.msfa.collectionPlan;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rspl.sf.msfa.R;

/**
 * Created by e10860 on 2/16/2018.
 */

public class HeaderVH extends RecyclerView.ViewHolder {
    public TextView tvWeekHeader;

    public HeaderVH(View itemView) {
        super(itemView);
        tvWeekHeader = (TextView) itemView.findViewById(R.id.tvWeekHeader);
    }
}
