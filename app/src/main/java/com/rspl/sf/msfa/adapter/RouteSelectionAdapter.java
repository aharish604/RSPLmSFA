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
import com.rspl.sf.msfa.mbo.RouteBean;
import com.rspl.sf.msfa.reports.RouteSelectionHolder;

import java.util.List;

/**
 * Created by ccb on 01-06-2017.
 */

public class RouteSelectionAdapter extends RecyclerView.Adapter<RouteSelectionHolder> {

    private List<RouteBean> routeBean, searchrouteBean;
    private Context mContext;
    private OnClickItem onClickItem=null;
    // private CustomFilter mFilter;

    public RouteSelectionAdapter(Context mContext, List<RouteBean> routeBeanList, List<RouteBean> routeserchBeanList) {
        this.routeBean = routeBeanList;
        this.searchrouteBean = routeserchBeanList;
        this.searchrouteBean.addAll(routeBeanList);
        this.mContext = mContext;
        //  mFilter = new CustomFilter(ForwardingAgentAdapter.this);
    }

    @Override
    public RouteSelectionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.routesearch_list_item, parent, false);
        return new RouteSelectionHolder(view);
    }

    @Override
    public void onBindViewHolder(RouteSelectionHolder holder,final int position) {
        RouteBean routeBean = searchrouteBean.get(position);

        holder.tvRouteCode.setText(routeBean.getRouteId() + " - ");
        holder.tvRouteDesc.setText(routeBean.getRouteDesc());

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
        return searchrouteBean.size();
    }


    public void onClickListener(OnClickItem onClickItem){
        this.onClickItem=onClickItem;
    }

    public void filter(final String text, final TextView tvEmptyRecord, final RecyclerView recyclerView, final String searchType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                searchrouteBean.clear();

                if (TextUtils.isEmpty(text)) {
                    searchrouteBean.addAll(routeBean);
                } else {
                    for (RouteBean item : routeBean) {
                        if (searchType.equals("01")) {
                            if (item.getRouteDesc().toLowerCase().contains(text.toLowerCase())) {
                                searchrouteBean.add(item);
                            }
                        } else if (searchType.equals("02")) {
                            if (item.getRouteId().toLowerCase().contains(text.toLowerCase())) {
                                searchrouteBean.add(item);
                            }
                        }
                    }

                }

                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                        if (searchrouteBean.isEmpty()) {
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
