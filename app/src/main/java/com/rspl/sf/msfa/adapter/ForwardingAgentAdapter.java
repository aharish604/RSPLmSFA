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
import com.rspl.sf.msfa.mbo.ForwardingAgentBean;
import com.rspl.sf.msfa.reports.ForwardingAgentViewHolder;

import java.util.List;

/**
 * Created by e10604 on 16/5/2017.
 */

public class ForwardingAgentAdapter extends RecyclerView.Adapter<ForwardingAgentViewHolder>  {
    private List<ForwardingAgentBean> forwardingAgentBean, searchForwardingAgentBean;
    private Context mContext;
    private OnClickItem onClickItem=null;
   // private CustomFilter mFilter;

    public ForwardingAgentAdapter(Context mContext, List<ForwardingAgentBean> forwardListBeanList,List<ForwardingAgentBean> forwardsearchBeanList) {
        this.forwardingAgentBean = forwardListBeanList;
        this.searchForwardingAgentBean = forwardsearchBeanList;
        this.searchForwardingAgentBean.addAll(forwardListBeanList);
        this.mContext = mContext;
      //  mFilter = new CustomFilter(ForwardingAgentAdapter.this);
    }

    @Override
    public ForwardingAgentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forward_list_item, parent, false);
        return new ForwardingAgentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ForwardingAgentViewHolder holder,final int position) {
        ForwardingAgentBean forwardingAgentBean = searchForwardingAgentBean.get(position);

        holder.tvforwardAgentCode.setText(forwardingAgentBean.getForAgentCode() + " - ");
        holder.tvforwardAgentDesc.setText(forwardingAgentBean.getForAgentDesc());

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
        return searchForwardingAgentBean.size();
    }


    public void onClickListener(OnClickItem onClickItem){
        this.onClickItem=onClickItem;
    }

    public void filter(final String text, final TextView tvEmptyRecord, final RecyclerView recyclerView, final String searchType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                searchForwardingAgentBean.clear();

                if (TextUtils.isEmpty(text)) {
                    searchForwardingAgentBean.addAll(forwardingAgentBean);
                } else {
                    for (ForwardingAgentBean item : forwardingAgentBean) {
                        if (searchType.equals("01")) {
                            if (item.getForAgentDesc().toLowerCase().contains(text.toLowerCase())) {
                                searchForwardingAgentBean.add(item);
                            }
                        } else if (searchType.equals("02")) {
                            if (item.getForAgentCode().toLowerCase().contains(text.toLowerCase())) {
                                searchForwardingAgentBean.add(item);
                            }
                        }
                    }

                }

                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                        if (searchForwardingAgentBean.isEmpty()) {
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
