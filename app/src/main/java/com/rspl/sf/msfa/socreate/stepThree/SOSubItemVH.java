package com.rspl.sf.msfa.socreate.stepThree;

import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rspl.sf.msfa.R;


/**
 * Created by e10769 on 08-05-2017.
 */

public class SOSubItemVH extends ViewHolder {
    public ImageView ivDeleteItem;
    public TextView tvDatePicker,tvUOM;
    public EditText etSubQty;
    public SOSubQtyTextWatcher soSubQtyTextWatcher=null;
    public SOSubItemVH(View itemView, SOSubQtyTextWatcher soSubQtyTextWatcher) {
        super(itemView);
        tvDatePicker = (TextView)itemView.findViewById(R.id.tv_date_picker);
        etSubQty = (EditText)itemView.findViewById(R.id.et_sub_qty);
        tvUOM = (TextView)itemView.findViewById(R.id.tvUomSOvalue);
        ivDeleteItem = (ImageView)itemView.findViewById(R.id.iv_delete_item);
        this.soSubQtyTextWatcher=soSubQtyTextWatcher;
        etSubQty.addTextChangedListener(soSubQtyTextWatcher);
    }
}
