package com.rspl.sf.msfa.mtp.approval;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rspl.sf.msfa.R;


/**
 * Created by e10769 on 29-06-2017.
 */

public class MTPApprovalListViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewMTPApprovalName,textViewOrderDate,textViewOrderID;
    public  ImageView imageViewStatus,iv_expand_icon;


    public MTPApprovalListViewHolder(View itemView) {
        super(itemView);
        textViewMTPApprovalName = (TextView) itemView.findViewById(R.id.textViewMTPApprovalName);
        textViewOrderDate = (TextView) itemView.findViewById(R.id.textViewOrderDate);
        textViewOrderID = (TextView) itemView.findViewById(R.id.textViewOrderID);
        imageViewStatus = (ImageView) itemView.findViewById(R.id.imageViewStatus);

    }
}
