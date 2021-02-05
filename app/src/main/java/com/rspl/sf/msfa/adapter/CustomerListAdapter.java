package com.rspl.sf.msfa.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.CustomerDetailsActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ExpandAnimation;
import com.rspl.sf.msfa.mbo.CustomerBean;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10526 on 09-12-2016.
 *
 */

public class CustomerListAdapter extends ArrayAdapter<CustomerBean> {
    private ArrayList<CustomerBean> retOriginalValues;
    private ArrayList<CustomerBean> retDisplayValues;
    private RetailerListFilter filter;

    private ArrayList<CustomerBean> alRetailerList = null;
    private Context context;

    public CustomerListAdapter(Context context, ArrayList<CustomerBean> items) {
        super(context, R.layout.adhoc_list_adapter, items);
        this.retOriginalValues = items;
        this.retDisplayValues = items;
        alRetailerList = items;
        this.context =context;

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


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.adhoc_list_adapter, parent,false);

        }

        TextView tvRetailerName = (TextView) view.findViewById(R.id.tv_RetailerName);
        ImageView ivMobileNo = (ImageView) view.findViewById(R.id.iv_mobile);
        TextView tv_retailer_mob_no = (TextView) view.findViewById(R.id.tv_retailer_mob_no);
        TextView tv_status_color = (TextView) view.findViewById(R.id.tv_status_color);
        TextView tv_down_color = (TextView) view.findViewById(R.id.tv_down_color);


        TextView tv_address2 = (TextView) view.findViewById(R.id.tv_address2);
        ImageView iv_expand_icon = (ImageView) view.findViewById(R.id.iv_expand_icon);

        final CustomerBean retailerListBean = retDisplayValues.get(position);

        tvRetailerName.setText(retailerListBean.getRetailerName());
        tv_retailer_mob_no.setText(retailerListBean.getMobileNumber());


        String cityVal;

        if(!retailerListBean.getLandMark().equalsIgnoreCase("") && !retailerListBean.getCity().equalsIgnoreCase("")){
            cityVal = retailerListBean.getLandMark()+","+retailerListBean.getCity();
        }else if(!retailerListBean.getLandMark().equalsIgnoreCase("") && retailerListBean.getCity().equalsIgnoreCase("")){
            cityVal = retailerListBean.getLandMark();
        }else if(retailerListBean.getLandMark().equalsIgnoreCase("") && !retailerListBean.getCity().equalsIgnoreCase("")){
            cityVal =retailerListBean.getCity();
        }else{
            cityVal = "";
        }

        String disticVal ;

        if(!retailerListBean.getDistrictDesc().equalsIgnoreCase("") && !retailerListBean.getPostalCode().equalsIgnoreCase("")){
            disticVal = retailerListBean.getDistrictDesc()+" "+retailerListBean.getPostalCode();
        }else if(!retailerListBean.getDistrictDesc().equalsIgnoreCase("") && retailerListBean.getPostalCode().equalsIgnoreCase("")){
            disticVal = retailerListBean.getDistrictDesc();
        }else if(retailerListBean.getDistrictDesc().equalsIgnoreCase("") && !retailerListBean.getPostalCode().equalsIgnoreCase("")){
            disticVal =retailerListBean.getPostalCode();
        }else{
            disticVal = "";
        }

        String addressVa ="";
        if(!retailerListBean.getAddress1().equalsIgnoreCase("")){
            addressVa = retailerListBean.getAddress1();
        }

        if(!retailerListBean.getAddress2().equalsIgnoreCase("")){
            addressVa = addressVa+","+retailerListBean.getAddress2();
        }

        if(!retailerListBean.getAddress3().equalsIgnoreCase("")){
            addressVa = addressVa+","+retailerListBean.getAddress3();
        }



        tv_address2.setText(context.getString(R.string.str_concat_two_texts_with_coma,addressVa , "\n"+cityVal +"\n"+ disticVal));



        final View testView = view;
        iv_expand_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageView img = (ImageView)v;

                /*if (retailerListBean.isAddressEnabled()) {
                    retailerListBean.setIsAddressEnabled(false);
                    img.setImageResource(R.drawable.down);

                } else {
                    retailerListBean.setIsAddressEnabled(true);
                    img.setImageResource(R.drawable.up);

                }*/

                View toolbarEmptyText = testView.findViewById(R.id.tv_empty_text);
                ExpandAnimation expandemptytext = new ExpandAnimation(toolbarEmptyText, 50);
                toolbarEmptyText.startAnimation(expandemptytext);

                View toolbar = testView.findViewById(R.id.tv_address2);
                ExpandAnimation expandAni = new ExpandAnimation(toolbar, 50);
                toolbar.startAnimation(expandAni);

                View toolbarSpace = testView.findViewById(R.id.tv_down_color);
                ExpandAnimation expandAniSpace = new ExpandAnimation(toolbarSpace, 50);
                toolbarSpace.startAnimation(expandAniSpace);
            }
        });


        try {
            String mStrSPGUID = Constants.getSPGUID(Constants.SPGUID);

            String mStrVisitStartEndQry= Constants.Visits+"?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq datetime'" + UtilConstants.getNewDate() + "' " +
                    "and CPGUID eq '"+retailerListBean.getCpGuidStringFormat().toUpperCase()+"' and "+Constants.SPGUID+" eq guid'"+mStrSPGUID+"'" ;

            String mStrVisitStartedQry=Constants.Visits+"?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "'  " +
                    "and CPGUID eq '"+retailerListBean.getCpGuidStringFormat().toUpperCase()+"' and "+Constants.SPGUID+" eq guid'"+mStrSPGUID+"'" ;

            if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartEndQry)) {
                tv_status_color.setBackgroundResource(R.color.GREEN);
                tv_down_color.setBackgroundResource(R.color.GREEN);
            }else if(OfflineManager.getVisitStatusForCustomer(mStrVisitStartedQry)){
                tv_status_color.setBackgroundResource(R.color.YELLOW);
                tv_down_color.setBackgroundResource(R.color.YELLOW);
            }else{
                tv_status_color.setBackgroundResource(R.color.RED);
                tv_down_color.setBackgroundResource(R.color.RED);
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            tv_status_color.setBackgroundResource(R.color.RED);
        }

        ivMobileNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!retailerListBean.getMobileNumber().equalsIgnoreCase("")) {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.tel_txt + (retailerListBean.getMobileNumber())));
                    context.startActivity(dialIntent);
                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onListDetails(v.getId());
            }
        });
        view.setId(position);
        return view;
    }

    @NonNull
    public android.widget.Filter getFilter() {
        if (filter == null) {
            filter = new RetailerListFilter();
        }
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
                    String mStrRetName = item.getRetailerName().toLowerCase();
                    if (mStrRetName.contains(prefixString)) {
                        filteredItems.add(item);
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
        }
    }
    private void onListDetails(int id) {
        CustomerBean retailerLB = alRetailerList.get(id);

        Intent intentRetailerDetails =new Intent(context, CustomerDetailsActivity.class);
        intentRetailerDetails.putExtra(Constants.RetailerName, retailerLB.getRetailerName());
        intentRetailerDetails.putExtra(Constants.CPNo, retailerLB.getCPNo());
        intentRetailerDetails.putExtra(Constants.comingFrom, Constants.CustomerList);
        intentRetailerDetails.putExtra(Constants.CPGUID, retailerLB.getCPGUID());
        Constants.VisitNavigationFrom = "";
        context.startActivity(intentRetailerDetails);
    }
}
