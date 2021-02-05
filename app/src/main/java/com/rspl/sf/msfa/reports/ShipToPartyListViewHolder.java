package com.rspl.sf.msfa.reports;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rspl.sf.msfa.R;

/**
 * Created by e10604 on 27-10-2017.
 */

public class ShipToPartyListViewHolder extends RecyclerView.ViewHolder{
public final TextView tvshipToCode,tvshipToDesc;

public ShipToPartyListViewHolder(View itemView) {
        super(itemView);
        tvshipToCode = (TextView)itemView.findViewById(R.id.tvShipToCode);
        tvshipToDesc = (TextView)itemView.findViewById(R.id.tvShipToDesc);

        }
 }
