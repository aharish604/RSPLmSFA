package com.rspl.sf.msfa.reports.invoicelist.invoiceDetails;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rspl.sf.msfa.R;


/**
 * Created by e10849 on 12-10-2017.
 */

public class PartnerFunctionViewHolder extends RecyclerView.ViewHolder {

   public TextView tvpartnerfunctionID, tvcustomername, tvVenderNo, tvPersNo;

    public PartnerFunctionViewHolder(View view) {
        super(view);
        tvpartnerfunctionID = (TextView) view.findViewById(R.id.tvpartnerfunctionID);
        tvcustomername = (TextView) view.findViewById(R.id.tvcustomername);
        tvVenderNo = (TextView) view.findViewById(R.id.tvVenderNo);
        tvPersNo = (TextView) view.findViewById(R.id.tvPersNo);
    }
}