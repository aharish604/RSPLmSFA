package com.rspl.sf.msfa.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.interfaces.TextWatcherInterface;
import com.rspl.sf.msfa.mbo.StocksInfoBean;
import com.rspl.sf.msfa.visit.StocksInfoDisbursementTextWatcher;

import java.util.ArrayList;




/**
 * Created by e10847 on 25-09-2017.
 */

public class StocksInfoRecyclerViewAdapter extends RecyclerView.Adapter<StocksInfoRecyclerViewAdapter.ViewHolder> {
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
    public StocksInfoRecyclerViewAdapter(Context context, ArrayList<StocksInfoBean> stocksInfoBeanArrayList) {
        this.context = context;
        this.stocksInfoBeanArrayList = stocksInfoBeanArrayList;

    }

    @Override
    public StocksInfoRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_stocks_competetor_info, viewGroup, false);
        return new ViewHolder(view, new StocksInfoDisbursementTextWatcher(stocksInfoBeanArrayList,textWatcherInterface));
    }

    @Override
    public void onBindViewHolder(StocksInfoRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        final StocksInfoBean stocksInfoBean = stocksInfoBeanArrayList.get(position);
        if (position==0){
            viewHolder.editTextInputQuantity.requestFocus();
        }
        viewHolder.textViewMaterial.setText(stocksInfoBean.getMaterialDesc());
        viewHolder.textViewAsOnDate.setText(stocksInfoBean.getAsOnDateQuantity());
        viewHolder.textViewUOM.setText(stocksInfoBean.getUOM());
        viewHolder.stocksInfoDisbursementTextWatcher.updatePosition(position,viewHolder.editTextInputQuantity);
        UtilConstants.editTextDecimalFormat(viewHolder.editTextInputQuantity, 13, 3);
        UtilConstants.removeLeadingZerowithTwoDecimal(stocksInfoBean.getQuantityInputText());
        viewHolder.editTextInputQuantity.setText(stocksInfoBean.getQuantityInputText());

    }
    public void textWatcher(TextWatcherInterface textWatcherInterface){
       this.textWatcherInterface =textWatcherInterface;
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
        private TextView textViewMaterial,textViewAsOnDate,textViewUOM;
        private EditText editTextInputQuantity;
        ImageButton imageButtonDelete;
        public StocksInfoDisbursementTextWatcher stocksInfoDisbursementTextWatcher;
        public ViewHolder(View view,StocksInfoDisbursementTextWatcher stocksInfoDisbursementTextWatcher) {
            super(view);
            textViewMaterial = (TextView)view.findViewById(R.id.textViewMaterial);
            textViewAsOnDate = (TextView) view.findViewById(R.id.textViewAsOnDate);
            editTextInputQuantity = (EditText) view.findViewById(R.id.editTextInputQuantity);
            textViewUOM = (TextView) view.findViewById(R.id.textViewUOM);
            this.stocksInfoDisbursementTextWatcher=stocksInfoDisbursementTextWatcher;
            imageButtonDelete=(ImageButton)view.findViewById(R.id.imageButtonDelete);
            editTextInputQuantity.addTextChangedListener(stocksInfoDisbursementTextWatcher);
            imageButtonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecyclerViewClickListener.onRecyclerViewItemClickListener(v,stocksInfoBeanArrayList.get(getAdapterPosition()),getAdapterPosition());
                }
            });
        }
    }
}
