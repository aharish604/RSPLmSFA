package com.rspl.sf.msfa.mtp.create;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.rspl.sf.msfa.R;

/**
 * Created by e10769 on 21-02-2018.
 */

class MTPCreateVH extends RecyclerView.ViewHolder {
    ImageView ivMobile;
    CheckBox cbName;

    public MTPCreateVH(View view) {
        super(view);
        cbName= (CheckBox)view.findViewById(R.id.cbName);
        ivMobile = (ImageView) view.findViewById(R.id.iv_mobile);
    }
}
