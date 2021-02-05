package com.rspl.sf.msfa.reports.salesorder.header;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rspl.sf.msfa.R;

/**
 * Created by e10769 on 29-06-2017.
 */

public class SalesOrderHeaderListViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewOrderIDDev,textViewOrderID,textViewOrderDate,textViewSalesOrderValue,textViewQuantity,textViewMaterialName;
    public ImageView imageViewDeliveryStatus;
    public SalesOrderHeaderListViewHolder(View itemView) {
        super(itemView);
        textViewOrderID = (TextView) itemView.findViewById(R.id.textViewRetailerName);
        textViewOrderDate = (TextView) itemView.findViewById(R.id.textViewCustomerID);
        textViewSalesOrderValue = (TextView) itemView.findViewById(R.id.tv_so_value);
        textViewOrderID = (TextView) itemView.findViewById(R.id.tv_order_id);
        textViewOrderDate = (TextView) itemView.findViewById(R.id.tv_order_date);
        textViewSalesOrderValue = (TextView) itemView.findViewById(R.id.tv_so_value);
      //  textViewQuantity = (TextView) itemView.findViewById(R.id.textViewQuantity);
       // textViewMaterialName = (TextView) itemView.findViewById(R.id.textViewMaterialName);
        imageViewDeliveryStatus = (ImageView) itemView.findViewById(R.id.ivDeliveryStatus);
    }
}
