package com.rspl.sf.msfa.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.listeners.OnClickItem;
import com.rspl.sf.msfa.mbo.SaleDistrictBean;
import com.rspl.sf.msfa.reports.SalesDistrictHolder;

import java.util.List;

/**
 * Created by ccb on 01-06-2017.
 */

public class SalesDistrictAdapter extends RecyclerView.Adapter<SalesDistrictHolder>  {
    private List<SaleDistrictBean> salesDistrictBean, searchsalesDistrictBean;
    private Context mContext;
    private OnClickItem onClickItem=null;
    // private CustomFilter mFilter;



    public SalesDistrictAdapter(Context mContext, List<SaleDistrictBean> salesDistrictBeanList,List<SaleDistrictBean> salesDistrictsearchBeanList) {
        this.salesDistrictBean = salesDistrictBeanList;
        this.searchsalesDistrictBean = salesDistrictsearchBeanList;
        this.searchsalesDistrictBean.addAll(salesDistrictBeanList);
        this.mContext = mContext;
        //  mFilter = new CustomFilter(ForwardingAgentAdapter.this);
    }

    @Override
    public SalesDistrictHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.salesdistrict_list_item, parent, false);
        return new SalesDistrictHolder(view);
    }

    @Override
    public void onBindViewHolder(SalesDistrictHolder holder,final int position) {
        SaleDistrictBean forwardingAgentBean = searchsalesDistrictBean.get(position);

        holder.tvSalesDistrictCode.setText(forwardingAgentBean.getSaleDistCode() + " - ");
        holder.tvSalesDistrictDesc.setText(forwardingAgentBean.getSalesDistDesc());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickItem!=null){
                    onClickItem.onClickItem(v,position);

                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return searchsalesDistrictBean.size();
    }


    public void onClickListener(OnClickItem onClickItem){
        this.onClickItem=onClickItem;
    }

    public void filter(final String text, final TextView tvEmptyRecord, final RecyclerView recyclerView, final String searchType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                searchsalesDistrictBean.clear();

                if (TextUtils.isEmpty(text)) {
                    searchsalesDistrictBean.addAll(salesDistrictBean);
                } else {
                    for (SaleDistrictBean item : salesDistrictBean) {
                        if (searchType.equals("01")) {
                            if (item.getSalesDistDesc().toLowerCase().contains(text.toLowerCase())) {
                                searchsalesDistrictBean.add(item);
                            }
                        } else if (searchType.equals("02")) {
                            if (item.getSaleDistCode().toLowerCase().contains(text.toLowerCase())) {
                                searchsalesDistrictBean.add(item);
                            }
                        }
                    }

                }

                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                        if (searchsalesDistrictBean.isEmpty()) {
                            tvEmptyRecord.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            tvEmptyRecord.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            notifyDataSetChanged();
                        }
                    }
                });
            }

            // Set on UI Thread


        }).start();

    }


}
