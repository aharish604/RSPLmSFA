package com.rspl.sf.msfa.claimreports;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rspl.sf.msfa.R;


/**
 * Created by e10769 on 20-12-2017.
 */

 public class ClaimReportVH extends RecyclerView.ViewHolder {
    public TextView  tvSPName, tvRSCode, tvclaimAmount, maxClaimAmount;

    public ClaimReportVH(View viewItem) {
        super(viewItem);
        tvSPName = (TextView) viewItem.findViewById(R.id.tvSPName);
        tvRSCode = (TextView) viewItem.findViewById(R.id.tvRSCode);
        tvclaimAmount = (TextView) viewItem.findViewById(R.id.tvclaimAmount);
        maxClaimAmount = (TextView) viewItem.findViewById(R.id.maxClaimAmount);

    }
}
