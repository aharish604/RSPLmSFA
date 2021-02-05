package com.rspl.sf.msfa.socreate.stepThree;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.socreate.SOSubItemBean;

import java.util.ArrayList;

/**
 * Created by e10769 on 08-05-2017.
 */

public class SOSubItemAdapter extends RecyclerView.Adapter<SOSubItemVH> {
    private Context mContex;
    private ArrayList<SOSubItemBean> soSubItemBeanArrayList;
    private SubTextWatcherInterface subTextWatcherInterface = null;
    private int headerPos;
    private DatePickerInterface datePickerInterface = null;
    private SubItemOnClickInterface subItemOnClickInterface = null;
    private String uom="";
    private ImageView ivAddSchedule;
    private SOItemBean soItemBean;
    public SOSubItemAdapter(Context mContext, ArrayList<SOSubItemBean> soSubItemBeanArrayList, SubTextWatcherInterface subTextWatcherInterface, int headerPos, DatePickerInterface datePickerInterface, SubItemOnClickInterface subItemOnClickInterface, String uom, ImageView ivAddSchedule, SOItemBean soItemBean) {
        this.mContex = mContext;
        this.soSubItemBeanArrayList = soSubItemBeanArrayList;
        this.subTextWatcherInterface = subTextWatcherInterface;
        this.headerPos = headerPos;
        this.datePickerInterface = datePickerInterface;
        this.subItemOnClickInterface = subItemOnClickInterface;
        this.uom = uom;
        this.ivAddSchedule=ivAddSchedule;
        this.soItemBean=soItemBean;
    }

    @Override
    public SOSubItemVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.so_sub_item, parent, false);
        return new SOSubItemVH(view, new SOSubQtyTextWatcher(soSubItemBeanArrayList, subTextWatcherInterface));
    }

    @Override
    public void onBindViewHolder(final SOSubItemVH holder, final int position) {
        SOSubItemBean soSubItemBean = soSubItemBeanArrayList.get(position);

        holder.tvDatePicker.setText(soSubItemBean.getDate());
        holder.soSubQtyTextWatcher.updatePosition(position, holder.etSubQty, headerPos,soItemBean,soSubItemBean,ivAddSchedule);
        holder.tvUOM.setText(uom);
        holder.tvDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (datePickerInterface != null) {
                    datePickerInterface.datePicker(holder.tvDatePicker, position, headerPos);
                }

            }
        });

        holder.etSubQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    holder.etSubQty.setHint("");
                }
            }
        });
        holder.etSubQty.setText(soSubItemBean.getSubQty()+"");
//        UtilConstants.editTextDecimalFormat(holder.etSubQty, 13, 3);

        holder.ivDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subItemOnClickInterface != null) {
                    subItemOnClickInterface.onItemClick(v, position, headerPos);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return soSubItemBeanArrayList.size();
    }
}
