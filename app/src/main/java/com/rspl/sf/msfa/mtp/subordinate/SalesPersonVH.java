package com.rspl.sf.msfa.mtp.subordinate;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rspl.sf.msfa.R;

/**
 * Created by e10769 on 10-Apr-18.
 */

class SalesPersonVH extends RecyclerView.ViewHolder {
    TextView tvSPName, tvSPPhone;

    public SalesPersonVH(View view) {
        super(view);
        tvSPName = (TextView) view.findViewById(R.id.tvSPName);
        tvSPPhone = (TextView) view.findViewById(R.id.tvSPPhone);
    }
}
