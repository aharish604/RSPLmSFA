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

import com.rspl.sf.msfa.CustomerDetailsActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ExpandAnimation;
import com.rspl.sf.msfa.prospectedCustomer.ProspectedCustomerBean;

import java.util.ArrayList;

/**
 * Created by ccb on 26-09-2017.
 */

public class ProspectedCustomerAdapter  extends ArrayAdapter<ProspectedCustomerBean> {
    private ArrayList<ProspectedCustomerBean> retOriginalValues;
    private ArrayList<ProspectedCustomerBean> retDisplayValues;
    private ProspectedCustomerAdapter.RetailerListFilter filter;

    private ArrayList<ProspectedCustomerBean> alRetailerList = null;
    private Context context;
    public String searchCode="";

    public ProspectedCustomerAdapter(Context context, ArrayList<ProspectedCustomerBean> items) {
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
    public ProspectedCustomerBean getItem(int item) {
        ProspectedCustomerBean retListBean;
        retListBean = this.retDisplayValues != null ? this.retDisplayValues.get(item) : null;
        return retListBean;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.prospected_customer_list_item, parent,false);

        }

        TextView tvRetailerName = (TextView) view.findViewById(R.id.tv_RetailerName);
        ImageView ivMobileNo = (ImageView) view.findViewById(R.id.iv_mobile);
        TextView tv_retailer_mob_no = (TextView) view.findViewById(R.id.tv_retailer_mob_no);
        TextView tv_status_color = (TextView) view.findViewById(R.id.tv_status_color);
        TextView tv_down_color = (TextView) view.findViewById(R.id.tv_down_color);


        TextView tv_address2 = (TextView) view.findViewById(R.id.tv_address2);
        ImageView iv_expand_icon = (ImageView) view.findViewById(R.id.iv_expand_icon);

        final ProspectedCustomerBean retailerListBean = retDisplayValues.get(position);

        tvRetailerName.setText(retailerListBean.getCustName());
        tv_retailer_mob_no.setText(retailerListBean.getMobNo()+" , "+retailerListBean.getCity());


        String cityVal;

      if( !retailerListBean.getCity().equalsIgnoreCase("")){
            cityVal =retailerListBean.getCity();
        }else{
            cityVal = "";
        }

        String disticVal ;

        if(!retailerListBean.getDistrict().equalsIgnoreCase("") && !retailerListBean.getPostalCode().equalsIgnoreCase("")){
            disticVal = retailerListBean.getDistrict()+" "+retailerListBean.getPostalCode();
        }else if(!retailerListBean.getDistrict().equalsIgnoreCase("") && retailerListBean.getPostalCode().equalsIgnoreCase("")){
            disticVal = retailerListBean.getDistrict();
        }else if(retailerListBean.getDistrict().equalsIgnoreCase("") && !retailerListBean.getPostalCode().equalsIgnoreCase("")){
            disticVal =retailerListBean.getPostalCode();
        }else{
            disticVal = "";
        }

        String addressVa ="";
        if(!retailerListBean.getAddress().equalsIgnoreCase("")){
            addressVa = retailerListBean.getAddress();
        }





        tv_address2.setText(context.getString(R.string.str_concat_two_texts_with_coma,addressVa , "\n"+cityVal +"\n"+ disticVal));



        final View testView = view;
        iv_expand_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageView img = (ImageView)v;

               /* if (retailerListBean.isAddressEnabled()) {
                    retailerListBean.setAddressEnabled(false);
                    img.setImageResource(R.drawable.down);

                } else {
                    retailerListBean.setAddressEnabled(true);
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


//        try {
//            String mStrVisitStartEndQry= Constants.Visits+"?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq datetime'" + UtilConstants.getNewDate() + "' " +
//                    "and CPGUID eq '"+retailerListBean.getCpGuidStringFormat().toUpperCase()+"'" ;
//
//            String mStrVisitStartedQry=Constants.Visits+"?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "'  " +
//                    "and CPGUID eq '"+retailerListBean.getCpGuidStringFormat().toUpperCase()+"'" ;
//
//            if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartEndQry)) {
//                tv_status_color.setBackgroundResource(R.color.GREEN);
//                tv_down_color.setBackgroundResource(R.color.GREEN);
//            }else if(OfflineManager.getVisitStatusForCustomer(mStrVisitStartedQry)){
//                tv_status_color.setBackgroundResource(R.color.YELLOW);
//                tv_down_color.setBackgroundResource(R.color.YELLOW);
//            }else{
                tv_status_color.setBackgroundResource(R.color.RED);
                tv_down_color.setBackgroundResource(R.color.RED);
        //    }
//        } catch (OfflineODataStoreException e) {
//            e.printStackTrace();
//            tv_status_color.setBackgroundResource(R.color.RED);
//        }

        ivMobileNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!retailerListBean.getMobNo().equalsIgnoreCase("")) {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.tel_txt + (retailerListBean.getMobNo())));
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
    public android.widget.Filter getFilter(String type) {
        if (filter == null) {
            filter = new RetailerListFilter();
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
                ArrayList<ProspectedCustomerBean> filteredItems = new ArrayList<>();
                int count = retOriginalValues.size();

                for (int i = 0; i < count; i++) {
                    ProspectedCustomerBean item = retOriginalValues.get(i);



                    if (searchCode.equals("1")) {
                        if (item.getCustName().toLowerCase().contains(prefixString)) {
                            filteredItems.add(item);
                        }
                    } else if (searchCode.equals("2")) {
                        if (item.getCounterType().toLowerCase().contains(prefixString)) {
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
            retDisplayValues = (ArrayList<ProspectedCustomerBean>) results.values; // has the filtered values
            notifyDataSetChanged();
            alRetailerList = retDisplayValues;
        }
    }
    private void onListDetails(int id) {
        Constants.CustomerType = "";
        ProspectedCustomerBean retailerLB = alRetailerList.get(id);

        Intent intentRetailerDetails =new Intent(context, CustomerDetailsActivity.class);
        intentRetailerDetails.putExtra(Constants.RetailerName, retailerLB.getCustName());
        intentRetailerDetails.putExtra(Constants.CPUID, retailerLB.getCpNo());
        intentRetailerDetails.putExtra(Constants.CPNo, retailerLB.getCpNo());
        intentRetailerDetails.putExtra(Constants.comingFrom, Constants.ProspectiveCustomerList);
        intentRetailerDetails.putExtra("Address",  retailerLB.getAddress());
        intentRetailerDetails.putExtra("MobileNo", retailerLB.getMobNo());
        intentRetailerDetails.putExtra(Constants.VisitCatID, "");
        intentRetailerDetails.putExtra(Constants.CPGUID, retailerLB.getCpNo());

        Constants.VisitNavigationFrom = "";
        context.startActivity(intentRetailerDetails);
    }
}

