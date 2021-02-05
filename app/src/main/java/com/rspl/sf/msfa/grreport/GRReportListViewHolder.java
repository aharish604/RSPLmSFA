package com.rspl.sf.msfa.grreport;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rspl.sf.msfa.R;


/**
 * Created by e10769 on 29-06-2017.
 */

public class GRReportListViewHolder extends RecyclerView.ViewHolder {
    public TextView tv_invoice_no,tv_invoice_date,tv_gr_date,tv_gr_time,tv_trans_date,tv_gr_no;
    public ImageView imageViewDeliveryStatus;
    public GRReportListViewHolder(View itemView) {
        super(itemView);
        tv_invoice_no = (TextView) itemView.findViewById(R.id.tv_invoice_no);
        tv_gr_date = (TextView) itemView.findViewById(R.id.tv_gr_date);
        tv_invoice_date = (TextView) itemView.findViewById(R.id.tv_invoice_date);
        tv_gr_time = (TextView) itemView.findViewById(R.id.tv_gr_time);
        tv_trans_date = (TextView) itemView.findViewById(R.id.tv_trans_date);
        tv_gr_no = (TextView) itemView.findViewById(R.id.tv_gr_no);
        imageViewDeliveryStatus = (ImageView) itemView.findViewById(R.id.ivDeliveryStatus);
    }
}
