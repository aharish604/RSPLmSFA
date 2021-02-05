package com.rspl.sf.msfa.reports.behaviourlist;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rspl.sf.msfa.R;


public class BehaviourListViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewRetailerName,textViewCustomerID,textViewMtdValue;

    public BehaviourListViewHolder(View itemView) {
        super(itemView);
        textViewRetailerName = (TextView) itemView.findViewById(R.id.textViewRetailerName);
        textViewCustomerID = (TextView) itemView.findViewById(R.id.textViewCustomerID);
        textViewMtdValue = (TextView) itemView.findViewById(R.id.textViewMTDValue);

    }
}
