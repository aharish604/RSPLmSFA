package com.rspl.sf.msfa.soapproval;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.solist.SOListBean;

import java.util.ArrayList;

/**
 * Created by e10769 on 21-06-2017.
 */

public class SOApprovalAdapter extends RecyclerView.Adapter<SOApprovalVH> {
    private final Context mContext;
    private ArrayList<SOListBean> soBeanList, soBeanSearchList;
    private OnClickInterface onClickInterface = null;
    private int comingFrom = 0;
    private RecyclerView recyclerView;
    private TextView noDataFound;
    private SOApprovalView approvalView;


    public SOApprovalAdapter(Context mContext, ArrayList<SOListBean> soBeanList, ArrayList<SOListBean> soBeanSearchList, int comingFrom, RecyclerView recyclerView, TextView noDataFound,SOApprovalView approvalView) {
        this.soBeanList = soBeanList;
        this.mContext = mContext;
        this.soBeanSearchList = soBeanSearchList;
        this.soBeanSearchList.addAll(soBeanList);
        this.comingFrom = comingFrom;
        this.recyclerView = recyclerView;
        this.noDataFound = noDataFound;
        this.noDataFound = noDataFound;
        this.approvalView = approvalView;
    }

    @Override
    public SOApprovalVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ll_so_approval_item, parent, false);
        return new SOApprovalVH(view);
    }


    @Override
    public void onBindViewHolder(SOApprovalVH holder, final int position) {
        SOListBean soBean = soBeanSearchList.get(position);
        holder.tvOrderDate.setText(ConstantsUtils.convertDateIntoDDMMYYYY(soBean.getOrderDate()));
        holder.tvOrderId.setText(soBean.getSONo());
        holder.tvSOCCName.setText(soBean.getCustomerName());
        holder.tvSOValue.setText(ConstantsUtils.commaSeparator(soBean.getEntityValue1(),soBean.getEntityCurrency()) + " " + soBean.getEntityCurrency());
        Drawable img = displayImage(soBean.getEntityType(), mContext);
        if (img != null) {
            holder.ivStatus.setImageDrawable(img);
        }
//        holder.tvSOCC.setText(soBean.getQuantity());
      /*  if (soBean.getDelvStatus().equals("1")) {
            holder.tvPriority.setBackgroundResource(R.color.RED);
        } else if (soBean.getDelvStatus().equals("2")) {
            holder.tvPriority.setBackgroundResource(R.color.YELLOW);
        } else if (soBean.getDelvStatus().equals("3")) {
            holder.tvPriority.setBackgroundResource(R.color.GREEN);
        } else if (soBean.getDelvStatus().equals("4")) {
            holder.tvPriority.setBackgroundResource(R.color.ORANGE);
        } else if (soBean.getDelvStatus().equals("5")) {
            holder.tvPriority.setBackgroundResource(R.color.ORANGE);
        } else {
            holder.tvPriority.setBackgroundResource(android.R.color.transparent);
        }*/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickInterface != null) {
                    onClickInterface.onItemClicks(v, position);
                }
            }
        });

    }

    private Drawable displayImage(String entityType, Context mContext) {
        Drawable img = null;
        if (entityType.equalsIgnoreCase("SO")) {
            img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
            if (img != null)
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.PendingApprovalColor), PorterDuff.Mode.SRC_IN);
        } else {
            img = ContextCompat.getDrawable(mContext, R.drawable.ic_assignment_black_24dp).mutate();
            if (img != null)
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.PendingApprovalColor), PorterDuff.Mode.SRC_IN);
        }
        return img;
    }

    @Override
    public int getItemCount() {
        return soBeanSearchList.size();
    }

    public void onItemClick(OnClickInterface onClickInterface) {
        this.onClickInterface = onClickInterface;
    }

    /*search filter*/
    public void filter(final String text) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    soBeanSearchList.clear();
                    if (TextUtils.isEmpty(text)) {
                        soBeanSearchList.addAll(soBeanList);
                    } else {
                        for (SOListBean item : soBeanList) {
                            if (item.getSearchText().toLowerCase().contains(text.toLowerCase())) {
                                soBeanSearchList.add(item);
                            }
                        }
                    }
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (soBeanSearchList.isEmpty()) {
                                noDataFound.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                noDataFound.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
    //                            notifyDataSetChanged();
                                if(approvalView!=null){
                                    approvalView.displaySearchList(soBeanSearchList);
                                }
                            }
                        }
                    });
                } catch (Throwable e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }
}
