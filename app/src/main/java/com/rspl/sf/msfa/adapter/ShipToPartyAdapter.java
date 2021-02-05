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
import com.rspl.sf.msfa.mbo.ShipToPartyListBean;
import com.rspl.sf.msfa.reports.ShipToPartyListViewHolder;

import java.util.List;

/**
 * Created by e10604 on 16/5/2017.
 */

public class ShipToPartyAdapter extends RecyclerView.Adapter<ShipToPartyListViewHolder>  {
    private List<ShipToPartyListBean> shipToBean, searchShipToBean;
    private Context mContext;
    private OnClickItem onClickItem=null;
    // private CustomFilter mFilter;

    public ShipToPartyAdapter(Context mContext, List<ShipToPartyListBean> shipToList, List<ShipToPartyListBean> shipTosearchBeanList) {
        this.shipToBean = shipToList;
        this.searchShipToBean = shipTosearchBeanList;
        this.searchShipToBean.addAll(shipToList);
        this.mContext = mContext;
        //  mFilter = new CustomFilter(ForwardingAgentAdapter.this);
    }

    @Override
    public ShipToPartyListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shipto_list_item, parent, false);
        return new ShipToPartyListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShipToPartyListViewHolder holder, final int position) {
        ShipToPartyListBean shipToBean = searchShipToBean.get(position);

        holder.tvshipToCode.setText(shipToBean.getShipToPartyCode());
        holder.tvshipToDesc.setText(shipToBean.getShipToPartyDesc());

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
        return searchShipToBean.size();
    }


    public void onClickListener(OnClickItem onClickItem){
        this.onClickItem=onClickItem;
    }

    public void filter(final String text, final TextView tvEmptyRecord, final RecyclerView recyclerView, final String searchType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                searchShipToBean.clear();

                if (TextUtils.isEmpty(text)) {
                    searchShipToBean.addAll(shipToBean);
                } else {
                    for (ShipToPartyListBean item : shipToBean) {
                        if (searchType.equals("01")) {
                            if (item.getShipToPartyDesc().toLowerCase().contains(text.toLowerCase())) {
                                searchShipToBean.add(item);
                            }
                        } else if (searchType.equals("02")) {
                            if (item.getShipToPartyCode().toLowerCase().contains(text.toLowerCase())) {
                                searchShipToBean.add(item);
                            }
                        }
                    }

                }

                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                        if (searchShipToBean.isEmpty()) {
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
