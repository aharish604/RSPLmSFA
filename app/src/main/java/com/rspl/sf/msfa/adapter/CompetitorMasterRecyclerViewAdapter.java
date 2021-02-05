package com.rspl.sf.msfa.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.interfaces.TextWatcherInterface;
import com.rspl.sf.msfa.mbo.CompetitorMasterBean;
import com.rspl.sf.msfa.visit.CompetitorDisbursementTextWatcher;

import java.util.ArrayList;


/**
 * Created by e10847 on 25-09-2017.
 */

public class CompetitorMasterRecyclerViewAdapter extends RecyclerView.Adapter<CompetitorMasterRecyclerViewAdapter.ViewHolder> {
    public ArrayList<CompetitorMasterBean> competitorMasterBeanArrayList;
    Context context;
    private TextWatcherInterface textWatcherInterface = null;
    public OnRecyclerViewClickListener onRecyclerViewClickListener;

    public interface OnRecyclerViewClickListener{
        void onRecyclerViewItemClickListener(@NonNull View view, @NonNull CompetitorMasterBean competitorMasterBean, int position);
    }
    public void setOnRecyclerViewClickListener(OnRecyclerViewClickListener onRecyclerViewClickListener){
        this.onRecyclerViewClickListener=onRecyclerViewClickListener;
    }
    public CompetitorMasterRecyclerViewAdapter(Context context, ArrayList<CompetitorMasterBean> stocksInfoBeanArrayList) {
        this.context = context;
        this.competitorMasterBeanArrayList = stocksInfoBeanArrayList;

    }

    @Override
    public CompetitorMasterRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view__competetor_master, viewGroup, false);
        return new ViewHolder(view, new CompetitorDisbursementTextWatcher(competitorMasterBeanArrayList,textWatcherInterface));
    }

    @Override
    public void onBindViewHolder(CompetitorMasterRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        final CompetitorMasterBean competitorMasterBean = competitorMasterBeanArrayList.get(position);
        if (position==0){
            viewHolder.editTextInputQuantity.requestFocus();
        }
        viewHolder.textViewCompetitorName.setText(competitorMasterBean.getCompName());

        viewHolder.textViewMaterial.setText(competitorMasterBean.getMaterialDesc());
        viewHolder.textViewAsOnDate.setText(competitorMasterBean.getAsOnDateQuantity());
        viewHolder.textViewUOM.setText(competitorMasterBean.getUOM());
        viewHolder.competitorDisbursementTextWatcher.updatePosition(position,viewHolder.editTextInputQuantity);
        UtilConstants.editTextDecimalFormat(viewHolder.editTextInputQuantity, 13, 3);
        UtilConstants.removeLeadingZerowithTwoDecimal(competitorMasterBean.getQuantityInputText());
        viewHolder.editTextInputQuantity.setText(competitorMasterBean.getQuantityInputText());

    }
    public void textWatcher(TextWatcherInterface textWatcherInterface){
       this.textWatcherInterface =textWatcherInterface;
    }


    @Override
    public int getItemCount() {
        return competitorMasterBeanArrayList.size();
//        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewMaterial,textViewAsOnDate,textViewUOM,textViewCompetitorName;
        private EditText editTextInputQuantity;

        public CompetitorDisbursementTextWatcher competitorDisbursementTextWatcher;
        public ViewHolder(View view,CompetitorDisbursementTextWatcher competitorDisbursementTextWatcher) {
            super(view);
            textViewMaterial = (TextView)view.findViewById(R.id.textViewMaterial);
            textViewAsOnDate = (TextView) view.findViewById(R.id.textViewAsOnDate);
            textViewCompetitorName = (TextView) view.findViewById(R.id.textViewCompetitorName);
            editTextInputQuantity = (EditText) view.findViewById(R.id.editTextInputQuantity);
            textViewUOM = (TextView) view.findViewById(R.id.textViewUOM);
            this.competitorDisbursementTextWatcher=competitorDisbursementTextWatcher;
            editTextInputQuantity.addTextChangedListener(competitorDisbursementTextWatcher);

//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onRecyclerViewClickListener.onRecyclerViewItemClickListener(v,competitorMasterBeanArrayList.get(getAdapterPosition()),getAdapterPosition());
//                }
//            });
        }
    }
}
