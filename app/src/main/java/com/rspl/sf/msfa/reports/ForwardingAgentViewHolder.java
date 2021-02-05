package com.rspl.sf.msfa.reports;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rspl.sf.msfa.R;

/**
 * Created by e10604 on 16/5/2017.
 */

public class ForwardingAgentViewHolder extends RecyclerView.ViewHolder{
    public final TextView tvforwardAgentCode,tvforwardAgentDesc;

    public ForwardingAgentViewHolder(View itemView) {
        super(itemView);
        tvforwardAgentCode = (TextView)itemView.findViewById(R.id.tvforwardAgentCode);
        tvforwardAgentDesc = (TextView)itemView.findViewById(R.id.tvforwardAgentDesc);

    }



}
