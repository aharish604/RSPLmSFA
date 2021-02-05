package com.rspl.sf.msfa.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.interfaces.TextWatcherInterface;
import com.rspl.sf.msfa.mbo.StocksInfoBean;
import com.rspl.sf.msfa.visit.StocksInfoDisbursementTextWatcher;

import java.util.ArrayList;


/**
 * Created by e10847 on 25-09-2017.
 */

public class StocksInfoReviewRecyclerViewAdapter extends RecyclerView.Adapter<StocksInfoReviewRecyclerViewAdapter.ViewHolder> {
    public ArrayList<StocksInfoBean> stocksInfoBeanArrayList;
    Context context;
    private TextWatcherInterface textWatcherInterface = null;
    public OnRecyclerViewClickListener onRecyclerViewClickListener;


    public interface OnRecyclerViewClickListener{
        void onRecyclerViewItemClickListener(@NonNull View view, @NonNull StocksInfoBean stocksInfoBean, int position);
    }
    public void setOnRecyclerViewClickListener(OnRecyclerViewClickListener onRecyclerViewClickListener){
        this.onRecyclerViewClickListener=onRecyclerViewClickListener;
    }
    public StocksInfoReviewRecyclerViewAdapter(Context context, ArrayList<StocksInfoBean> stocksInfoBeanArrayList) {
        this.context = context;
        this.stocksInfoBeanArrayList = stocksInfoBeanArrayList;

    }

    @Override
    public StocksInfoReviewRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_stocks_review, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StocksInfoReviewRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        final StocksInfoBean stocksInfoBean = stocksInfoBeanArrayList.get(position);
        viewHolder.textViewMaterial.setText(stocksInfoBean.getMaterialDesc());
        viewHolder.textViewQuantity.setText(stocksInfoBean.getQuantityInputText());
        viewHolder.textViewUOM.setText(stocksInfoBean.getUOM());

    }

    @Override
    public int getItemCount() {
        return stocksInfoBeanArrayList.size();
//        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewMaterial,textViewQuantity,textViewUOM;

        ImageButton imageButtonDelete;
        public StocksInfoDisbursementTextWatcher stocksInfoDisbursementTextWatcher;
        public ViewHolder(View view) {
            super(view);
            textViewMaterial = (TextView)view.findViewById(R.id.textViewMaterial);
            textViewQuantity = (TextView) view.findViewById(R.id.textViewQuantity);
            textViewUOM = (TextView) view.findViewById(R.id.textViewUOM);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecyclerViewClickListener.onRecyclerViewItemClickListener(v,stocksInfoBeanArrayList.get(getAdapterPosition()),getAdapterPosition());
                }
            });
        }
    }
}
