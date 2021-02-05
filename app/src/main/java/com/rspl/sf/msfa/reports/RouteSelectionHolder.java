package com.rspl.sf.msfa.reports;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rspl.sf.msfa.R;

/**
 * Created by ccb on 01-06-2017.
 */

public class RouteSelectionHolder extends RecyclerView.ViewHolder  {

    public final TextView tvRouteCode,tvRouteDesc;

    public RouteSelectionHolder(View itemView) {
        super(itemView);
        tvRouteCode = (TextView)itemView.findViewById(R.id.tvrouteCode);
        tvRouteDesc = (TextView)itemView.findViewById(R.id.tvrouteDesc);

    }
}
