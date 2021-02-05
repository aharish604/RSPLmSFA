package com.rspl.sf.msfa.soapproval;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rspl.sf.msfa.R;


/**
 * Created by e10769 on 21-06-2017.
 */

public class SOApprovalVH extends RecyclerView.ViewHolder {
    public TextView tvOrderId,tvOrderDate,tvSOCCName, tvSOValue;
    ImageView ivStatus;
    public SOApprovalVH(View itemView) {
        super(itemView);
        tvOrderId = (TextView)itemView.findViewById(R.id.tv_order_id);
        tvOrderDate = (TextView)itemView.findViewById(R.id.tv_order_date);
        ivStatus = (ImageView) itemView.findViewById(R.id.ivStatus);
//        tvSOCC = (TextView)itemView.findViewById(R.id.tv_so_cc);
//        tvPriority = (TextView)itemView.findViewById(R.id.tvPriority);
        tvSOCCName = (TextView)itemView.findViewById(R.id.tv_so_cc_name);
        tvSOValue = (TextView)itemView.findViewById(R.id.tv_so_value);
    }
}
