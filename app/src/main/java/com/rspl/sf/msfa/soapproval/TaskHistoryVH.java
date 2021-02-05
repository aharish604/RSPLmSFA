package com.rspl.sf.msfa.soapproval;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rspl.sf.msfa.R;

/**
 * Created by e10769 on 16-Mar-18.
 */

public class TaskHistoryVH extends RecyclerView.ViewHolder {
    public ImageView ivStatus;
    public TextView tvCustomerName, tvActionName, tvRemarks;

    public TaskHistoryVH(View view) {
        super(view);
        tvActionName = (TextView)view.findViewById(R.id.tvActionName);
        tvCustomerName = (TextView)view.findViewById(R.id.tvCustomerName);
        tvRemarks = (TextView)view.findViewById(R.id.tvRemarks);
        ivStatus = (ImageView)view.findViewById(R.id.ivStatus);
    }
}
