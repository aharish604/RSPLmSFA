package com.rspl.sf.msfa.collectionPlan.detail;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rspl.sf.msfa.R;

/**
 * Created by e10860 on 2/19/2018.
 */

public class CollectionPlanVH extends RecyclerView.ViewHolder {
    public TextView tvPlanNameDesc;
    public TextView  tvPlanName;
    public TextView tvActivity;
    public TextView tvActivity1;
    public TextView tvPlanActivityDesc;
    public TextView tvPlanActivityDesc1;
    public TextView tvActualActivityDesc;
    public TextView tvRemarks;
    public TextView tvRemarks1;
    public TextView tvPlanRemarksDesc;
    public TextView tvActualActivityDesc1;
    public TextView tvPlanRemarksDesc1;
    public LinearLayout llRemarks;
    public LinearLayout llActivity;
    public LinearLayout llActivity1;
    public LinearLayout llName;
    public LinearLayout llActivityActual;
    public LinearLayout llActivityActual1;
    public LinearLayout llRemarks1;

    public CollectionPlanVH(View itemView) {
        super(itemView);
        tvPlanNameDesc = (TextView) itemView.findViewById(R.id.tvPlanNameDesc);
        tvPlanName = (TextView) itemView.findViewById(R.id.tvPlanName);
        tvRemarks = (TextView) itemView.findViewById(R.id.tvRemarks);
        tvRemarks1 = (TextView) itemView.findViewById(R.id.tvRemarks1);
        tvPlanRemarksDesc = (TextView) itemView.findViewById(R.id.tvPlanRemarksDesc);
        tvPlanRemarksDesc1 = (TextView) itemView.findViewById(R.id.tvPlanRemarksDesc1);
        tvActivity = (TextView) itemView.findViewById(R.id.tvActivity);
        tvActivity1 = (TextView) itemView.findViewById(R.id.tvActivity1);
        tvPlanActivityDesc = (TextView) itemView.findViewById(R.id.tvPlanActivityDesc);
        tvActualActivityDesc = (TextView) itemView.findViewById(R.id.tvActualActivityDesc);
        tvActualActivityDesc1 = (TextView) itemView.findViewById(R.id.tvActualActivityDesc1);
        tvPlanActivityDesc1 = (TextView) itemView.findViewById(R.id.tvPlanActivityDesc1);
        llRemarks = (LinearLayout) itemView.findViewById(R.id.llRemarks);
        llRemarks1 = (LinearLayout) itemView.findViewById(R.id.llRemarks1);
        llActivity1 = (LinearLayout) itemView.findViewById(R.id.llActivity1);
        llActivity = (LinearLayout) itemView.findViewById(R.id.llActivity);
        llName = (LinearLayout) itemView.findViewById(R.id.llName);
        llActivityActual = (LinearLayout) itemView.findViewById(R.id.llActivityActual);
        llActivityActual1 = (LinearLayout) itemView.findViewById(R.id.llActivityActual1);
    }

}
