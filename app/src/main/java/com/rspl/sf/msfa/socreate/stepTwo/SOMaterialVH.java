package com.rspl.sf.msfa.socreate.stepTwo;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;

import com.rspl.sf.msfa.R;


/**
 * Created by e10769 on 30-06-2017.
 */

public class SOMaterialVH extends RecyclerView.ViewHolder {
    public CheckBox checkBox;

    public SOMaterialVH(View itemView) {
        super(itemView);
        checkBox = (CheckBox)itemView.findViewById(R.id.checkBox);
    }
}
