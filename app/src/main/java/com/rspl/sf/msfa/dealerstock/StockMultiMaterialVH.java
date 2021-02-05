package com.rspl.sf.msfa.dealerstock;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.ui.EditextClearButton;

/**
 * Created by e10769 on 20-12-2017.
 */

class StockMultiMaterialVH extends RecyclerView.ViewHolder {
    ImageView ivLeft, ivRight;
    ConstraintLayout viewForeground;
    RelativeLayout viewBackground;
    //    ImageView ivSelectedImg;
    TextView tvMatDesc, tvUom,tvLandingPrice;
    LinearLayout clView;
    EditextClearButton etQty;
    Button btAdd;//, tvPlus, tvMinus;
    StockCreateQtyTextWatcher soCreateQtyTextWatcher;

    public StockMultiMaterialVH(View viewItem, StockCreateQtyTextWatcher soCreateQtyTextWatcher) {
        super(viewItem);
//        ivSelectedImg = (ImageView) viewItem.findViewById(R.id.ivSelectedImg);
        tvMatDesc = (TextView) viewItem.findViewById(R.id.tvMatDesc);
        tvLandingPrice = (TextView) viewItem.findViewById(R.id.tvLandingPrice);
//        tvMatId = (TextView) viewItem.findViewById(R.id.tvMatId);
//        tvPlus = (Button) viewItem.findViewById(R.id.tvPlus);
//        tvQty = (TextView) viewItem.findViewById(R.id.tvQty);
//        tvMinus = (Button) viewItem.findViewById(R.id.tvMinus);
        tvUom = (TextView) viewItem.findViewById(R.id.tvUom);
        etQty = (EditextClearButton) viewItem.findViewById(R.id.etQty);
        btAdd = (Button) viewItem.findViewById(R.id.btAdd);
        viewBackground = (RelativeLayout) viewItem.findViewById(R.id.view_background);
        viewForeground = (ConstraintLayout) viewItem.findViewById(R.id.view_foreground);
        ivLeft = (ImageView) viewItem.findViewById(R.id.ivLeft);
        ivRight = (ImageView) viewItem.findViewById(R.id.ivRight);

//        etQty.setClearButtonMode();
//        tilQty = (TextInputLayout)viewItem.findViewById(R.id.tilQty);
//        clViewMT = (ConstraintLayout) viewItem.findViewById(R.id.clViewMT);
//        clViewPC = (LinearLayout) viewItem.findViewById(R.id.clViewPC);
        clView = (LinearLayout) viewItem.findViewById(R.id.clView);
        this.soCreateQtyTextWatcher = soCreateQtyTextWatcher;
        etQty.addTextChangedListener(soCreateQtyTextWatcher);
        setIsRecyclable(false);

    }
}
