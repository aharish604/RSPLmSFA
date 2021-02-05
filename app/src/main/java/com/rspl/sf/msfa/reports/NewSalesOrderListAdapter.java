package com.rspl.sf.msfa.reports;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.interfaces.OnClickInterface;
import com.rspl.sf.msfa.mbo.SalesOrderBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10769 on 21-02-2017.
 */

public class NewSalesOrderListAdapter extends RecyclerView.Adapter<NewSalesOrderListViewHolder> {
    private final Context mContext;
    private List<SalesOrderBean> SalesOrderBeanList, SalesOrderBeanSearchList;
    private OnClickInterface onClickInterface = null;

    public NewSalesOrderListAdapter(Context mContext, List<SalesOrderBean> SalesOrderBeanList) {
        this.SalesOrderBeanList = SalesOrderBeanList;
        this.mContext = mContext;
        this.SalesOrderBeanSearchList = new ArrayList<>();
        this.SalesOrderBeanSearchList.addAll(SalesOrderBeanList);
    }

    @Override
    public NewSalesOrderListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_new_sales_order_item_list, parent, false);
        return new NewSalesOrderListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewSalesOrderListViewHolder holder, final int position) {
        final SalesOrderBean SalesOrderBean = SalesOrderBeanSearchList.get(position);
        holder.invNO.setText(SalesOrderBean.getOrderNo());
        holder.invDate.setText(SalesOrderBean.getOrderDate());
        holder.tv_quantity.setText(
                SalesOrderBean.getQAQty() +" "+SalesOrderBean.getUom());
        holder.tv_material_number.setText(SalesOrderBean.getMaterialDesc());
        holder.invAmount.setText(UtilConstants.removeLeadingZerowithTwoDecimal(SalesOrderBean.getNetAmount()) + " " + SalesOrderBean.getCurrency());
        switch (SalesOrderBean.getDelvStatus()) {
            case "A":
                holder.tvStatusIndicator.setBackgroundResource(R.color.RED);
                break;
            case "B":
                holder.tvStatusIndicator.setBackgroundResource(R.color.YELLOW);
                break;
            case "C":
                holder.tvStatusIndicator.setBackgroundResource(R.color.GREEN);
                break;
            case "D":
                holder.tvStatusIndicator.setBackgroundResource(R.color.BLACK);
                break;
            case "F":
                holder.tvStatusIndicator.setBackgroundResource(R.color.dimgray);
                break;
            default:
                holder.tvStatusIndicator.setBackgroundResource(android.R.color.transparent);
                break;
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickInterface != null) {
                    onClickInterface.onItemClick(v, SalesOrderBean);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return SalesOrderBeanSearchList.size();
    }

    public void onItemClick(OnClickInterface onClickInterface) {
        this.onClickInterface = onClickInterface;
    }

    /*search filter*/
    public void filter(final String text, final TextView tvEmptyRecord, final RecyclerView recyclerView) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SalesOrderBeanSearchList.clear();
                if (TextUtils.isEmpty(text)) {
                    SalesOrderBeanSearchList.addAll(SalesOrderBeanList);
                } else {
                    for (SalesOrderBean item : SalesOrderBeanList) {

                        if (item.getOrderNo().toLowerCase().contains(text.toLowerCase())) {
                            SalesOrderBeanSearchList.add(item);
                        }

                    }
                }
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                        if (SalesOrderBeanSearchList.isEmpty()) {
                            tvEmptyRecord.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            tvEmptyRecord.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }
        }).start();

    }
}
