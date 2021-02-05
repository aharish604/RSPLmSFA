package com.rspl.sf.msfa.claimreports;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rspl.sf.msfa.R;


/**
 * Created by e10769 on 20-12-2017.
 */

 public class ClaimDetailsReportVH extends RecyclerView.ViewHolder {
    public TextView  tvSchemeDesc, totalClaim, totalMaxClaim;

    public ClaimDetailsReportVH(View viewItem) {
        super(viewItem);
        tvSchemeDesc = (TextView) viewItem.findViewById(R.id.tvSchemeDesc);
        totalClaim = (TextView) viewItem.findViewById(R.id.totalClaim);
        totalMaxClaim = (TextView) viewItem.findViewById(R.id.totalMaxClaim);
    }
}
