package com.rspl.sf.msfa.socreate.viewSelectedMaterial;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.socreate.stepTwo.SOCreateQtyTextWatcher;


/**
 * Created by e10769 on 21-12-2017.
 */

class SelectedMatVH extends RecyclerView.ViewHolder {

    TextView tvSelQty, tvMatDesc, tvUom;
    ImageView ivSelectedImg, ivLeft, ivRight;
    EditText etQty;
    SOCreateQtyTextWatcher soCreateQtyTextWatcher;
    RelativeLayout viewBackground;
    ConstraintLayout viewForeground;
    ImageView iv_deleted;

    public SelectedMatVH(View view, SOCreateQtyTextWatcher soCreateQtyTextWatcher) {
        super(view);
        tvSelQty = (TextView) view.findViewById(R.id.tvSelQty);
        tvMatDesc = (TextView) view.findViewById(R.id.tvMatDesc);
        ivSelectedImg = (ImageView) view.findViewById(R.id.ivSelectedImg);
        iv_deleted = (ImageView) view.findViewById(R.id.iv_deleted);
        etQty = (EditText) view.findViewById(R.id.etQty);
        tvUom = (TextView) view.findViewById(R.id.tvUom);
        viewBackground = (RelativeLayout) view.findViewById(R.id.view_background);
        viewForeground = (ConstraintLayout) view.findViewById(R.id.view_foreground);
        ivLeft = (ImageView) view.findViewById(R.id.ivLeft);
        ivRight = (ImageView) view.findViewById(R.id.ivRight);
        etQty.setCursorVisible(false);
        this.soCreateQtyTextWatcher = soCreateQtyTextWatcher;
        etQty.addTextChangedListener(soCreateQtyTextWatcher);

    }
}
