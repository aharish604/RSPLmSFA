package com.rspl.sf.msfa.socreate.stepThree;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rspl.sf.msfa.R;


/**
 * Created by e10769 on 03-05-2017.
 */

public class SOQtyVH extends RecyclerView.ViewHolder {
    public RecyclerView rvScheduleItem;
    public LinearLayout llDeliverySchedule;
    public ImageView ivAddSchedule, ivDeleteItem;
    public EditText soQtyValue;
    public TextView matValue,matDescValue,uomValue,tvItemNo;
    public SoQtyTextWatcher soQtyTextWatcher;
    public SOQtyVH(View itemView, SoQtyTextWatcher soQtyTextWatcher) {
        super(itemView);
        matValue = (TextView) itemView.findViewById(R.id.tvMaterialvalueSO);
        tvItemNo = (TextView) itemView.findViewById(R.id.tvItemNo);
        matDescValue = (TextView) itemView.findViewById(R.id.tvMaterialDescvalueSO);
        uomValue = (TextView) itemView.findViewById(R.id.tvUomSOvalue);
        soQtyValue = (EditText) itemView.findViewById(R.id.etsoQty);
        llDeliverySchedule = (LinearLayout) itemView.findViewById(R.id.ll_delivery_schedule);
        ivAddSchedule = (ImageView) itemView.findViewById(R.id.iv_add_schedule);
        ivDeleteItem = (ImageView) itemView.findViewById(R.id.iv_delete_item);
        rvScheduleItem = (RecyclerView) itemView.findViewById(R.id.recycler_view);
        this.soQtyTextWatcher = soQtyTextWatcher;
        soQtyValue.addTextChangedListener(soQtyTextWatcher);
    }
}
