package com.rspl.sf.msfa.reports.distributortargets;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.rspl.sf.msfa.R;

/**
 * Created by E10953 on 31-07-2019.
 */

public class DistibutorTargetViewHolder extends RecyclerView.ViewHolder {
    public TextView tv_kpi_name,tv_target_val,tv_achieved_val,tv_bal_val,tv_uom,tv_percentage_val;
    public PieChart pieChart_target;
    public ConstraintLayout cl_achev_status;

    public DistibutorTargetViewHolder(View itemView) {
        super(itemView);
        tv_kpi_name = (TextView) itemView.findViewById(R.id.tv_kpi_name);
        tv_target_val = (TextView) itemView.findViewById(R.id.tv_target_val);
        tv_achieved_val = (TextView) itemView.findViewById(R.id.tv_achieved_val);
        tv_bal_val = (TextView) itemView.findViewById(R.id.tv_bal_val);
        tv_uom = (TextView) itemView.findViewById(R.id.tv_uom);
        tv_percentage_val = (TextView) itemView.findViewById(R.id.tv_percentage_val);
        cl_achev_status = (ConstraintLayout) itemView.findViewById(R.id.cl_achev_status);
        pieChart_target = (PieChart)itemView.findViewById(R.id.pieChart_target);
    }
}
