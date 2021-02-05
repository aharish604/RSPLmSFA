package com.rspl.sf.msfa.so;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.rspl.sf.msfa.R;

/**
 * Created by e10769 on 09-02-2017.
 */
public class SOItemViewHolder extends RecyclerView.ViewHolder{
    public TextView tvMaterial;
    public CheckBox cbMaterial;

    public SOItemViewHolder(View itemView) {
        super(itemView);
    cbMaterial=(CheckBox)itemView.findViewById(R.id.cbMaterial);
    tvMaterial=(TextView)itemView.findViewById(R.id.tvMaterial);
    }
}
