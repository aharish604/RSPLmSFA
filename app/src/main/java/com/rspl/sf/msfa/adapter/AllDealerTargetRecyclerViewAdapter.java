package com.rspl.sf.msfa.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.mbo.AllDealerTargetDTO;

import java.util.ArrayList;


/**
 * Created by e10847 on 25-09-2017.
 */

public class AllDealerTargetRecyclerViewAdapter extends RecyclerView.Adapter<AllDealerTargetRecyclerViewAdapter.ViewHolder> {
    private ArrayList<AllDealerTargetDTO> allDealerTargetDTOArrayList;
    Context context;
    public AllDealerTargetRecyclerViewAdapter(Context context, ArrayList<AllDealerTargetDTO> allDealerTargetDTOArrayList) {
        this.context = context;
        this.allDealerTargetDTOArrayList = allDealerTargetDTOArrayList;
    }

    @Override
    public AllDealerTargetRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_all_dealer_target, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AllDealerTargetRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        AllDealerTargetDTO allDealerTargetDTO = allDealerTargetDTOArrayList.get(position);
        viewHolder.textViewDealer.setText(allDealerTargetDTO.getDealer());
        viewHolder.textViewTarget.setText(String.valueOf(allDealerTargetDTO.getTarget()));
        viewHolder.textViewMtd.setText(String.valueOf(allDealerTargetDTO.getMTD()));
        viewHolder.textViewLysMtd.setText(String.valueOf(allDealerTargetDTO.getLYSMTD()));
        viewHolder.textViewLysmAchieved.setText(String.valueOf(allDealerTargetDTO.getLYSMAchieved()));
    }

    @Override
    public int getItemCount() {
        return allDealerTargetDTOArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewDealer,textViewTarget,textViewMtd,textViewLysMtd,textViewLysmAchieved;

        public ViewHolder(View view) {
            super(view);
            textViewDealer = (TextView)view.findViewById(R.id.textViewDealer);
            textViewTarget = (TextView)view.findViewById(R.id.textViewTarget);
            textViewMtd = (TextView)view.findViewById(R.id.textViewMtd);
            textViewLysMtd = (TextView)view.findViewById(R.id.textViewLysMtd);
            textViewLysmAchieved = (TextView)view.findViewById(R.id.textViewLysmAchieved);
        }
    }

}
