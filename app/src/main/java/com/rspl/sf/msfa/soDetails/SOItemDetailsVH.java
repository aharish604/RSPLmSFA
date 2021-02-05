package com.rspl.sf.msfa.soDetails;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by e10769 on 03-07-2017.
 */

public class SOItemDetailsVH extends RecyclerView.ViewHolder {
    public LinearLayout llSOCondition, llSOSchedule,ll_condition_list_header,ll_schedule_list_header;
    public TextView tvItemNo,tvMaterial,tvDescription,tvQuantity,tvUnitPrice,tvCurrencyType,tvTotalCurrencyType;
    public  TextView tv_item_qty,tvDiscountValue,tvPlant,tvFreightValue,tvGrossValue;
    public  LinearLayout ll_unit_price,ll_discount,ll_plant,ll_freight,ll_gross_amount;
    public SOItemDetailsVH(View itemView, Context mContext) {
        super(itemView);
//        tvItemNo = (TextView)itemView.findViewById(R.id.tvItemNo);
//        tvMaterial = (TextView)itemView.findViewById(R.id.tvMaterial);
//        tvDescription = (TextView)itemView.findViewById(R.id.tvDescription);
//        tvQuantity = (TextView)itemView.findViewById(R.id.tvQuantity);
//        tvUnitPrice = (TextView)itemView.findViewById(R.id.tvUnitPrice);
//        llSOCondition = (LinearLayout)itemView.findViewById(R.id.ll_so_condition);
//        llSOSchedule = (LinearLayout)itemView.findViewById(R.id.ll_schedule_list);
//        tvCurrencyType = (TextView)itemView.findViewById(R.id.tv_percentage);
//        tvTotalCurrencyType = (TextView)itemView.findViewById(R.id.tv_total_amt_curr);
//        View viewCondition = itemView.findViewById(R.id.viewCondition);
//        View viewSchedule = itemView.findViewById(R.id.viewSchedule);
//        TextView tvConditionTitle = (TextView)viewCondition.findViewById(R.id.tv_heading);
//        TextView tvScheduleTitle = (TextView)viewSchedule.findViewById(R.id.tv_heading);
//        tvConditionTitle.setText(mContext.getString(R.string.price_details));
//        tvScheduleTitle.setText(mContext.getString(R.string.delivery_schedule));
//        tv_item_qty= (TextView) itemView.findViewById(R.id.tv_item_qty);
//        tvDiscountValue= (TextView) itemView.findViewById(R.id.tvDiscountValue);
//        tvPlant = (TextView) itemView.findViewById(R.id.tvPlant);
//        tvFreightValue = (TextView) itemView.findViewById(R.id.tvFreightValue);
//        ll_unit_price = (LinearLayout) itemView.findViewById(R.id.ll_unit_price);
//        ll_discount = (LinearLayout) itemView.findViewById(R.id.ll_discount);
//        ll_plant = (LinearLayout) itemView.findViewById(R.id.ll_plant);
//        ll_freight = (LinearLayout) itemView.findViewById(R.id.ll_freight);
//        ll_gross_amount = (LinearLayout) itemView.findViewById(R.id.ll_gross_amount);
//        tvGrossValue= (TextView) itemView.findViewById(R.id.tvGrossValue);
//        ll_condition_list_header = (LinearLayout)itemView.findViewById(R.id.ll_condition_list_header);
//        ll_schedule_list_header = (LinearLayout)itemView.findViewById(R.id.ll_schedule_list_header);
    }
}
