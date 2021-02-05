package com.rspl.sf.msfa.reports;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rspl.sf.msfa.R;

/**
 * Created by e10769 on 21-02-2017.
 */
public class NewSalesOrderListViewHolder extends RecyclerView.ViewHolder {
    public TextView invNO,invDate,invAmount,tvStatusIndicator,tv_quantity,tv_material_number;
    public NewSalesOrderListViewHolder(View itemView) {
        super(itemView);
        invNO = (TextView) itemView.findViewById(R.id.tv_in_history_no);
        invDate = (TextView) itemView.findViewById(R.id.tv_in_history_date);
        invAmount = (TextView) itemView.findViewById(R.id.tv_in_history_amt);
        tvStatusIndicator = (TextView) itemView.findViewById(R.id.tv_status_indicator);
        tv_quantity = (TextView) itemView.findViewById(R.id.tv_quantity);
        tv_material_number = (TextView) itemView.findViewById(R.id.tv_material_number);
    }
}
