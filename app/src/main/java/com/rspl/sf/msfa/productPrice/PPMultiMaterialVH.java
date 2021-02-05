package com.rspl.sf.msfa.productPrice;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.socreate.stepTwo.SOCreateQtyTextWatcher;
import com.rspl.sf.msfa.ui.EditextClearButton;


/**
 * Created by e10769 on 20-12-2017.
 */

 public class PPMultiMaterialVH extends RecyclerView.ViewHolder {
    public ImageView ivLeft, ivRight;
    public  ConstraintLayout viewForeground;
    public  RelativeLayout viewBackground;
    //    ImageView ivSelectedImg;
    public TextView tvMatDesc, tvUom,tvPrice,tvCurreny;
    public  LinearLayout clView;
    public EditextClearButton etQty;
    //public  Button btAdd;//, tvPlus, tvMinus;
    public SOCreateQtyTextWatcher soCreateQtyTextWatcher;

    public PPMultiMaterialVH(View viewItem, SOCreateQtyTextWatcher soCreateQtyTextWatcher) {
        super(viewItem);
        tvMatDesc = (TextView) viewItem.findViewById(R.id.tvMatDesc);
        tvPrice = (TextView) viewItem.findViewById(R.id.tv_price);
        tvUom = (TextView) viewItem.findViewById(R.id.tvUom);
        etQty = (EditextClearButton) viewItem.findViewById(R.id.etQty);
        viewBackground = (RelativeLayout) viewItem.findViewById(R.id.view_background);
        viewForeground = (ConstraintLayout) viewItem.findViewById(R.id.view_foreground);
        ivLeft = (ImageView) viewItem.findViewById(R.id.ivLeft);
        ivRight = (ImageView) viewItem.findViewById(R.id.ivRight);
        clView = (LinearLayout) viewItem.findViewById(R.id.clView);
        this.soCreateQtyTextWatcher = soCreateQtyTextWatcher;
        etQty.addTextChangedListener(soCreateQtyTextWatcher);
        setIsRecyclable(false);

    }
}
