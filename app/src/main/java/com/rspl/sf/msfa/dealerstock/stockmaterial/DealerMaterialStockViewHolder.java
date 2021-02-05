package com.rspl.sf.msfa.dealerstock.stockmaterial;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;

import com.rspl.sf.msfa.R;

@SuppressWarnings("all")
public class DealerMaterialStockViewHolder extends RecyclerView.ViewHolder {
    CheckBox checkBoxMaterial;
    public DealerMaterialStockViewHolder(View itemView) {
        super(itemView);
        checkBoxMaterial = (CheckBox)itemView.findViewById(R.id.checkBoxMaterial);
    }
}
