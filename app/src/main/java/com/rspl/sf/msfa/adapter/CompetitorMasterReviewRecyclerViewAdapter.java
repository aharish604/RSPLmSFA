package com.rspl.sf.msfa.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.interfaces.TextWatcherInterface;
import com.rspl.sf.msfa.mbo.CompetitorMasterBean;

import java.util.ArrayList;


/**
 * Created by e10847 on 25-09-2017.
 */

public class CompetitorMasterReviewRecyclerViewAdapter extends RecyclerView.Adapter<CompetitorMasterReviewRecyclerViewAdapter.ViewHolder> {
    public ArrayList<CompetitorMasterBean>competitorMasterBeanArrayList;
    Context context;
    private TextWatcherInterface textWatcherInterface = null;
    public OnRecyclerViewClickListener onRecyclerViewClickListener;


    public interface OnRecyclerViewClickListener{
        void onRecyclerViewItemClickListener(@NonNull View view, @NonNull CompetitorMasterBean competitorMasterBean, int position);
    }
    public void setOnRecyclerViewClickListener(OnRecyclerViewClickListener onRecyclerViewClickListener){
        this.onRecyclerViewClickListener=onRecyclerViewClickListener;
    }
    public CompetitorMasterReviewRecyclerViewAdapter(Context context, ArrayList<CompetitorMasterBean> competitorMasterBeanArrayList) {
        this.context = context;
        this.competitorMasterBeanArrayList = competitorMasterBeanArrayList;

    }

    @Override
    public CompetitorMasterReviewRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_competitor_master_review, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CompetitorMasterReviewRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        final CompetitorMasterBean competitorMasterBean = competitorMasterBeanArrayList.get(position);
        viewHolder.textViewMaterial.setText(competitorMasterBean.getMaterialDesc());
        viewHolder.textViewInputQuantity.setText(competitorMasterBean.getQuantityInputText());
        viewHolder.textViewUOM.setText(competitorMasterBean.getUOM());
        viewHolder.textViewCompetitorName.setText(competitorMasterBean.getCompName());

    }

    @Override
    public int getItemCount() {
        return competitorMasterBeanArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewMaterial,textViewInputQuantity,textViewUOM,textViewCompetitorName;
        public ViewHolder(View view) {
            super(view);
            textViewMaterial = (TextView)view.findViewById(R.id.textViewMaterial);
            textViewInputQuantity = (TextView) view.findViewById(R.id.textViewInputQuantity);
            textViewUOM = (TextView) view.findViewById(R.id.textViewUOM);
            textViewCompetitorName = (TextView) view.findViewById(R.id.textViewCompetitorName);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecyclerViewClickListener.onRecyclerViewItemClickListener(v,competitorMasterBeanArrayList.get(getAdapterPosition()),getAdapterPosition());
                }
            });
        }
    }
}
