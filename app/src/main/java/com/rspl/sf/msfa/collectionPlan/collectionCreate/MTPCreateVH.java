package com.rspl.sf.msfa.collectionPlan.collectionCreate;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.rspl.sf.msfa.R;

/**
 * Created by e10769 on 21-02-2018.
 */

public class MTPCreateVH extends RecyclerView.ViewHolder {
    public ImageView ivMobile;
    public CheckBox cbName;

    public MTPCreateVH(View view) {
        super(view);
        cbName= (CheckBox)view.findViewById(R.id.cbName);
        ivMobile = (ImageView) view.findViewById(R.id.iv_mobile);
    }
}
