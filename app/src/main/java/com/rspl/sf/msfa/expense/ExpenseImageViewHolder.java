package com.rspl.sf.msfa.expense;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.rspl.sf.msfa.R;

/**
 * Created by e10769 on 04-03-2017.
 */
public class ExpenseImageViewHolder extends RecyclerView.ViewHolder{
    public ImageView ivThumb;
    public ExpenseImageViewHolder(View itemView) {
        super(itemView);
        ivThumb=(ImageView)itemView.findViewById(R.id.imageView);
        itemView.setClickable(true);
    }
}