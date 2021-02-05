package com.rspl.sf.msfa.soDetails;/*
package com.arteriatech.sf.mdc.soDetails;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.sf.mdc.R;
import com.arteriatech.sf.mdc.constant.Constants;
import com.arteriatech.sf.mdc.soCreate.SOConditionItemDetaiBean;

import java.util.ArrayList;

*/
/**
 * A simple {@link Fragment} subclass.
 *//*

public class SOPricingDetailsFragment extends Fragment implements AdapterInterface<SOConditionItemDetaiBean> {


    private ArrayList<SOConditionItemDetaiBean> pricingItemList=null;
    private RecyclerView recyclerView;
    private TextView tvNoRecordFound;
    private SimpleRecyclerViewAdapter<SOConditionItemDetaiBean> soConditionItemAdapter;

    public SOPricingDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle!=null){
            pricingItemList = (ArrayList<SOConditionItemDetaiBean>)bundle.getSerializable(Constants.EXTRA_SO_ITEM_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sopricing_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        tvNoRecordFound = (TextView) view.findViewById(R.id.no_record_found);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        soConditionItemAdapter = new SimpleRecyclerViewAdapter<SOConditionItemDetaiBean>(getContext(), R.layout.so_condition_item, this, recyclerView, tvNoRecordFound);
        recyclerView.setAdapter(soConditionItemAdapter);
        if (pricingItemList!=null){
            soConditionItemAdapter.refreshAdapter(pricingItemList);
        }
    }

    @Override
    public void onItemClick(SOConditionItemDetaiBean soConditionItemDetaiBean, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new PricingDetailsVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, SOConditionItemDetaiBean soConditionItemDetaiBean) {
        ((PricingDetailsVH) viewHolder).tvDescription.setText(soConditionItemDetaiBean.getName());
        ((PricingDetailsVH) viewHolder).tvAmount.setText(UtilConstants.commaSeparator(soConditionItemDetaiBean.getAmount()));
        ((PricingDetailsVH) viewHolder).tvTotalAmount.setText(UtilConstants.commaSeparator(soConditionItemDetaiBean.getConditionValue()));
    }
}
*/
