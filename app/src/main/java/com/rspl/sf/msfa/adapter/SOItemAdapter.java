package com.rspl.sf.msfa.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.interfaces.CheckBoxInterface;
import com.rspl.sf.msfa.so.SOItemViewHolder;
import com.rspl.sf.msfa.socreate.SOItemBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10769 on 09-02-2017.
 */

public class SOItemAdapter extends RecyclerView.Adapter<SOItemViewHolder> {
    private CheckBoxInterface checkBoxInterface=null;
    private Context mContext;
    private List<SOItemBean> soItemBeanList,soSearchBeanList;
    public SOItemAdapter(Context mContext, List<SOItemBean> soItemBeanList){
        this.soItemBeanList=soItemBeanList;
        this.mContext= mContext;
        this.soSearchBeanList=new ArrayList<>();
        this.soSearchBeanList.addAll(this.soItemBeanList);
    }
    @Override
    public SOItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.so_item_check,parent,false);
        return new SOItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder( SOItemViewHolder holder, final int position) {
        final SOItemBean soItemBean = soSearchBeanList.get(position);
        holder.cbMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    soItemBean.setChecked(true);

                }else {
                    soItemBean.setChecked(false);

                }
                if(checkBoxInterface!=null){
                    checkBoxInterface.onCheckedListener(buttonView,isChecked,soItemBean);
                }
            }
        });
       holder.cbMaterial.setChecked(soItemBean.isChecked());
        holder.tvMaterial.setText(soItemBean.getMatCode()+" - "+soItemBean.getMatDesc());



    }



    @Override
    public int getItemCount() {
        return soSearchBeanList.size();
    }
    public void onCheckSelected(CheckBoxInterface checkBoxInterface){
        this.checkBoxInterface=checkBoxInterface;
    }
    public void filter(final String text, final TextView tvEmptyRecord, final RecyclerView recyclerView, final String searchType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                soSearchBeanList.clear();
                if (TextUtils.isEmpty(text)) {
                    soSearchBeanList.addAll(soItemBeanList);
                } else {
                    for (SOItemBean item : soItemBeanList) {
                        if (searchType.equals("Code")) {
                            if (item.getMatCode().toLowerCase().contains(text.toLowerCase())) {
                                soSearchBeanList.add(item);
                            }
                        } else if (searchType.equals("Desc")) {
                            if (item.getMatDesc().toLowerCase().contains(text.toLowerCase())) {
                                soSearchBeanList.add(item);
                            }
                        }
                    }
                }
                // Set on UI Thread
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                        if (soSearchBeanList.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }
        }).start();

    }
}
