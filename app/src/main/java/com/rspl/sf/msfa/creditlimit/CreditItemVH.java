package com.rspl.sf.msfa.creditlimit;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rspl.sf.msfa.R;

/**
 * Created by e10860 on 4/20/2018.
 */

public class CreditItemVH extends RecyclerView.ViewHolder {
    public TextView tvDate;
    public TextView tvDocNo;
    public TextView tvDocAmt;


    public CreditItemVH(View itemView) {
        super(itemView);
        tvDate = (TextView) itemView.findViewById(R.id.tvCreditDocDate);
        tvDocAmt = (TextView) itemView.findViewById(R.id.tvCreditDocAmt);
        tvDocNo = (TextView) itemView.findViewById(R.id.tvCreditDocNo);
    }
}
