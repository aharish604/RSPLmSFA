package com.rspl.sf.msfa.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.mbo.StocksInfoBean;
import com.rspl.sf.msfa.visit.StocksInfoActivity;

import java.util.ArrayList;


/**
 * Created by e10847 on 25-09-2017.
 */

public class MaterialByCustomerRecyclerViewAdapter extends RecyclerView.Adapter<MaterialByCustomerRecyclerViewAdapter.ViewHolder> {
    public ArrayList<StocksInfoBean> stocksInfoBeanArrayList;
    Context context;
    boolean isCheckedChanged =false;
    public OnRecyclerViewClickListener onRecyclerViewClickListener;

    public interface OnRecyclerViewClickListener{
        void onRecyclerViewItemClickListener(@NonNull View view, @NonNull StocksInfoBean entitySetCollectionDTO, int position);
    }
    public void setOnRecyclerViewClickListener(OnRecyclerViewClickListener onRecyclerViewClickListener){
        this.onRecyclerViewClickListener=onRecyclerViewClickListener;
    }

    public MaterialByCustomerRecyclerViewAdapter(Context context, ArrayList<StocksInfoBean> entitySetCollectionDTOArrayList) {
        this.context = context;
        this.stocksInfoBeanArrayList = entitySetCollectionDTOArrayList;
    }

    @Override
    public MaterialByCustomerRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_material_by_customers, viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MaterialByCustomerRecyclerViewAdapter.ViewHolder viewHolder, final int position) {

        final StocksInfoBean stocksInfoBean = stocksInfoBeanArrayList.get(position);
        viewHolder.checkBoxMaterials.setText(stocksInfoBean.getMaterialDesc());
        viewHolder.checkBoxMaterials.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MaterialByCustomerRecyclerViewAdapter.this.isCheckedChanged=isChecked;
                if (isChecked){
                    stocksInfoBean.setChecked(true);
                    StocksInfoActivity.dealerStocksInfoBeanArrayList.add(stocksInfoBean);
                }else{
                    stocksInfoBean.setChecked(false);
                    StocksInfoActivity.dealerStocksInfoBeanArrayList.remove(stocksInfoBean);
                }
            }
        });

        if (!stocksInfoBean.getChecked()){
            viewHolder.checkBoxMaterials.setChecked(false);
        }else{
            viewHolder.checkBoxMaterials.setChecked(true);
            if (!isCheckedChanged){
                viewHolder.checkBoxMaterials.setChecked(false);
            }else{
                viewHolder.checkBoxMaterials.setChecked(true);
            }
        }
    }

    @Override
    public int getItemCount() {
        return stocksInfoBeanArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
      CheckBox checkBoxMaterials;

        public ViewHolder(View view) {
            super(view);
            checkBoxMaterials = (CheckBox)view.findViewById(R.id.checkBoxMaterials);
            checkBoxMaterials.setChecked(false);

        }
    }


}
