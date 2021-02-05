package com.rspl.sf.msfa.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.mbo.CustomerBean;
import com.rspl.sf.msfa.routeplan.OtherBeatListActivity;

import java.util.ArrayList;

/**
 * Created by e10762 on 15-12-2016.
 *
 */

public class OtherRoutePlanAdapter extends ArrayAdapter<CustomerBean> {
    private ArrayList<CustomerBean> retDisplayValues;
    private ArrayList<CustomerBean> retOriginalValues;
    private RetailerListFilter filter;
    private ArrayList<CustomerBean> alRetailerList = null;
    private View context;
    private TextView tvEmptyLay;
    public String searchCode="";

    public OtherRoutePlanAdapter(View context, ArrayList<CustomerBean> items, String mStrRouteType, TextView tvEmptyLay) {
        super(context.getContext(), R.layout.beat_plan_line_item, items);
        this.retDisplayValues = items;
        alRetailerList = items;
        this.context=context;
        this.tvEmptyLay = tvEmptyLay;
    }

    @Override
    public int getCount() {
        return this.retDisplayValues != null ? this.retDisplayValues.size() : 0;
    }

    @Override
    public CustomerBean getItem(int item) {
        CustomerBean retListBean;
        retListBean = this.retDisplayValues != null ? this.retDisplayValues.get(item) : null;
        return retListBean;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {

        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.other_beat_list_adapter, parent,false);

        }

        TextView tvBeatName = (TextView) view.findViewById(R.id.tv_BeatName);
        TextView tvROUTEID = (TextView) view.findViewById(R.id.tv_ROUTEID);
        final CustomerBean retailerListBean = retDisplayValues.get(position);
        tvBeatName.setText(retailerListBean.getRouteDesc());
        tvROUTEID.setText(retailerListBean.getRouteID());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent other_beat_list = new Intent(context.getContext(), OtherBeatListActivity.class);
                other_beat_list.putExtra(Constants.OtherRouteGUID,retailerListBean.getRouteGuid36());
                other_beat_list.putExtra(Constants.OtherRouteName,retailerListBean.getRouteDesc());
                other_beat_list.putExtra(Constants.RouteType,context.getContext().getString(R.string.lbl_other_beats));
                context.getContext().startActivity(other_beat_list);
            }
        });
        view.setId(position);
        return view;
    }


    public android.widget.Filter getFilter(String type) {
        if (filter == null) {
            filter = new OtherRoutePlanAdapter.RetailerListFilter();
        }
        searchCode=type;
        return filter;
    }

    /**
     * This class search name based on customer name from list.
     */
    private class RetailerListFilter extends android.widget.Filter {
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (retOriginalValues == null) {
                retOriginalValues = new ArrayList<>(retDisplayValues);
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = retOriginalValues;
                results.count = retOriginalValues.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();
                ArrayList<CustomerBean> filteredItems = new ArrayList<>();
                int count = retOriginalValues.size();

                for (int i = 0; i < count; i++) {
                    CustomerBean item = retOriginalValues.get(i);

                    if (searchCode.equals("1")) {
                        if (item.getCustomerName().toLowerCase().contains(prefixString)) {
                            filteredItems.add(item);
                        }
                    } else if (searchCode.equals("2")) {
                        if (item.getCustomerId().toLowerCase().contains(prefixString)) {
                            filteredItems.add(item);
                        }
                    } else if (searchCode.equals("3")) {
                        if (item.getCity().toLowerCase().contains(prefixString)) {
                            filteredItems.add(item);
                        }
                    }

                }
                results.values = filteredItems;
                results.count = filteredItems.size();
            }
            return results;
        }
        @Override
        protected void publishResults(CharSequence prefix, FilterResults results) {
            //noinspection unchecked
            retDisplayValues = (ArrayList<CustomerBean>) results.values; // has the filtered values
            notifyDataSetChanged();
            alRetailerList = retDisplayValues;
            if(alRetailerList.size()>0){
                tvEmptyLay.setVisibility(View.GONE);
            } else
                tvEmptyLay.setVisibility(View.VISIBLE);

        }
    }


}