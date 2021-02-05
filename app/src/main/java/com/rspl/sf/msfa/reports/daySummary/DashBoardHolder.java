package com.rspl.sf.msfa.reports.daySummary;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.ui.TextProgressBar;

public class DashBoardHolder extends RecyclerView.ViewHolder {
    public TextView tv_ach_dashboard_val_tittle,tv_tar_dashbord_val,tv_actual_dashbord_val,tv_dashboard_val;
    public CardView cv_visit;
    public TextProgressBar pbSalesPerActucal;
    public ProgressBar pbCount;
    public DashBoardHolder(View itemView) {
        super(itemView);
        tv_ach_dashboard_val_tittle = (TextView) itemView.findViewById(R.id.tv_ach_dashboard_val_tittle);
        tv_tar_dashbord_val = (TextView) itemView.findViewById(R.id.tv_tar_dashbord_val);
        tv_actual_dashbord_val = (TextView) itemView.findViewById(R.id.tv_actual_dashbord_val);
        tv_dashboard_val = (TextView) itemView.findViewById(R.id.tv_dashboard_val);
        pbSalesPerActucal = (TextProgressBar) itemView.findViewById(R.id.pbSalesPerActucal);
        pbCount = (ProgressBar) itemView.findViewById(R.id.pbCount);
    }
}