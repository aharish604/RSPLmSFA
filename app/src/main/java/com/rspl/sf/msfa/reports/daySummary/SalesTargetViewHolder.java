package com.rspl.sf.msfa.reports.daySummary;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.ui.TextProgressBar;

/**
 * Created by e10893 on 25-01-2018.
 */

public class SalesTargetViewHolder extends RecyclerView.ViewHolder {
    public TextView tv_sales_value_lbl,tv_target_val,tv_ach_sal_val;
    public TextProgressBar pbSalesPer;
    public LinearLayout ll_actual_layout;
    public ProgressBar pbCount;

    public SalesTargetViewHolder(View itemView) {
        super(itemView);
        tv_sales_value_lbl = (TextView) itemView.findViewById(R.id.tv_sales_value_lbl);
        tv_target_val = (TextView) itemView.findViewById(R.id.tv_tar_sal_val);
        tv_ach_sal_val = (TextView) itemView.findViewById(R.id.tv_ach_sal_val);
        pbSalesPer = (TextProgressBar)itemView.findViewById(R.id.pbSalesPer);
        ll_actual_layout = (LinearLayout) itemView.findViewById(R.id.ll_actual_layout);
        pbCount = (ProgressBar) itemView.findViewById(R.id.pbCount);
    }
}
