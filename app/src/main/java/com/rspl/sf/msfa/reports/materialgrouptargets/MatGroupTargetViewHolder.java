package com.rspl.sf.msfa.reports.materialgrouptargets;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.rspl.sf.msfa.R;

/**
 * Created by e10893 on 25-01-2018.
 */

public class MatGroupTargetViewHolder extends RecyclerView.ViewHolder {
    public TextView tv_kpi_name,tv_target_val,tv_achieved_val,tv_bal_val,tv_uom;
    public PieChart pieChart_target;

    public MatGroupTargetViewHolder(View itemView) {
        super(itemView);
        tv_kpi_name = (TextView) itemView.findViewById(R.id.tv_kpi_name);
        tv_target_val = (TextView) itemView.findViewById(R.id.tv_target_val);
        tv_achieved_val = (TextView) itemView.findViewById(R.id.tv_achieved_val);
        tv_bal_val = (TextView) itemView.findViewById(R.id.tv_bal_val);
        tv_uom = (TextView) itemView.findViewById(R.id.tv_uom);
        pieChart_target = (PieChart)itemView.findViewById(R.id.pieChart_target);
    }
}
