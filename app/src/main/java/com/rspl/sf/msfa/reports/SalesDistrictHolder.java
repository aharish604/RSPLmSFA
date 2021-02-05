package com.rspl.sf.msfa.reports;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rspl.sf.msfa.R;

/**
 * Created by ccb on 01-06-2017.
 */

public class SalesDistrictHolder extends RecyclerView.ViewHolder {

    public final TextView tvSalesDistrictCode,tvSalesDistrictDesc;

    public SalesDistrictHolder(View itemView) {
        super(itemView);
        tvSalesDistrictCode = (TextView)itemView.findViewById(R.id.tvsaledistrictCode);
        tvSalesDistrictDesc = (TextView)itemView.findViewById(R.id.tvsalesdistrictDesc);

    }

}
