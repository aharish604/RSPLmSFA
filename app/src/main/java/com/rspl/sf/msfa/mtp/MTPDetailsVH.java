package com.rspl.sf.msfa.mtp;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rspl.sf.msfa.R;

/**
 * Created by e10769 on 19-02-2018.
 */

class MTPDetailsVH extends RecyclerView.ViewHolder {
    LinearLayout llName;
    TextView tvPlanNameDesc, tvPlanActivityDesc, tvPlanRemarksDesc;

    public MTPDetailsVH(View view) {
        super(view);
        tvPlanNameDesc = (TextView)view.findViewById(R.id.tvPlanNameDesc);
        tvPlanActivityDesc = (TextView)view.findViewById(R.id.tvPlanActivityDesc);
        tvPlanRemarksDesc = (TextView)view.findViewById(R.id.tvPlanRemarksDesc);
        llName = (LinearLayout)view.findViewById(R.id.llName);
    }
}
