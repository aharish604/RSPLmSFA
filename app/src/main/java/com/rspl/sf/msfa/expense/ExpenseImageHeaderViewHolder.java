package com.rspl.sf.msfa.expense;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.rspl.sf.msfa.R;

/**
 * Created by e10769 on 07-03-2017.
 */

public class ExpenseImageHeaderViewHolder extends RecyclerView.ViewHolder {
    public ImageView ivThumb;
    public ExpenseImageHeaderViewHolder(View itemView) {
        super(itemView);
        ivThumb=(ImageView)itemView.findViewById(R.id.imageView);
        itemView.setClickable(true);
    }
}
