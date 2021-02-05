package com.rspl.sf.msfa.socreate.stepThree;

import android.app.Activity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.socreate.SOItemBean;

import java.util.ArrayList;

/**
 * Created by e10769 on 03-05-2017.
 */

public class SOQuantityAdapter extends RecyclerView.Adapter<SOQtyVH> {
    private Activity mContext;
    private ArrayList<SOItemBean> items;
    private TextWatcherInterface textWatcherInterface = null;
    private SubTextWatcherInterface subTextWatcherInterface = null;
    private DatePickerInterface datePickerInterface = null;
    private SubItemOnClickInterface subItemOnClickInterface = null;
    private OnAddScheduleInterface onAddScheduleInterface = null;
    private int mComeFrom=0;

    public SOQuantityAdapter(Activity context, ArrayList<SOItemBean> items, TextWatcherInterface textWatcherInterface, SubTextWatcherInterface subTextWatcherInterface, DatePickerInterface datePickerInterface, SubItemOnClickInterface subItemOnClickInterface, OnAddScheduleInterface onAddScheduleInterface, OnFocusChangeListener onFocusChangeListener, int mComeFrom) {
        this.mContext = context;
        this.items = items;
        this.textWatcherInterface = textWatcherInterface;
        this.subTextWatcherInterface = subTextWatcherInterface;
        this.datePickerInterface = datePickerInterface;
        this.subItemOnClickInterface = subItemOnClickInterface;
        this.onAddScheduleInterface = onAddScheduleInterface;
        this.mComeFrom=mComeFrom;
    }

    @Override
    public SOQtyVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.so_qty_listview, parent, false);
        return new SOQtyVH(view, new SoQtyTextWatcher(items, textWatcherInterface));
    }

    @Override
    public void onBindViewHolder(final SOQtyVH holder, final int position) {
        final SOItemBean soItemBean = items.get(position);
        UtilConstants.editTextDecimalFormat(holder.soQtyValue, 13, 3);
        holder.tvItemNo.setText("Item #"+soItemBean.getItemNo());
        holder.matValue.setText(soItemBean.getMatCode());
        holder.matDescValue.setText(soItemBean.getMatDesc());
        holder.uomValue.setText(soItemBean.getUom());

          /*subItem*/
        holder.rvScheduleItem.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        holder.rvScheduleItem.setLayoutManager(linearLayoutManager);
        SOSubItemAdapter soSubItemAdapter = new SOSubItemAdapter(mContext, soItemBean.getSoSubItemBeen(), subTextWatcherInterface, position, datePickerInterface, subItemOnClickInterface, soItemBean.getUom(),holder.ivAddSchedule,soItemBean);
        holder.rvScheduleItem.setAdapter(soSubItemAdapter);
        /*finished sub item*/


        holder.soQtyTextWatcher.updatePosition(position, holder.soQtyValue,holder, soItemBean,soSubItemAdapter,false);
        holder.soQtyValue.setText(soItemBean.getSoQty());


        holder.ivAddSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(soItemBean.isButtonOnClick()) {
                    if (onAddScheduleInterface != null) {
                        onAddScheduleInterface.onAddListener(holder, soItemBean);
                    }
                }


            }
        });
        if (mComeFrom== ConstantsUtils.SO_CREATE_SINGLE_MATERIAL) {
            holder.ivDeleteItem.setVisibility(View.GONE);
        }else {
            holder.ivDeleteItem.setVisibility(View.VISIBLE);
        }
        holder.ivDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAddScheduleInterface != null) {
                    onAddScheduleInterface.onDeleteListener(holder, soItemBean, position);
                }
            }
        });
        if(soItemBean.getSoSubItemBeen().isEmpty()){
            soItemBean.setButtonOnClick(true);
            holder.ivAddSchedule.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }
}
