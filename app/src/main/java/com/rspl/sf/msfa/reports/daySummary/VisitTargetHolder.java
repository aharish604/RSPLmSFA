package com.rspl.sf.msfa.reports.daySummary;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rspl.sf.msfa.R;

public class VisitTargetHolder extends RecyclerView.ViewHolder {
    public TextView tv_no_of_outlets,tv_order_val;
    public CardView cv_visit;
    public ProgressBar pbCount;
    public VisitTargetHolder(View itemView) {
        super(itemView);
        tv_no_of_outlets = (TextView) itemView.findViewById(R.id.tv_no_of_outlets);
        tv_order_val = (TextView) itemView.findViewById(R.id.tv_order_val);
        cv_visit = (CardView) itemView.findViewById(R.id.cv_visit);
        pbCount = (ProgressBar) itemView.findViewById(R.id.pbCount);
    }
}