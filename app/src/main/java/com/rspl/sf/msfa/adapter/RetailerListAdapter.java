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
 import com.rspl.sf.msfa.mbo.CustomerBean;

 import java.util.ArrayList;

/**
 * Created by e10526 on 08-12-2016.
 *
 */

public class RetailerListAdapter extends ArrayAdapter<CustomerBean> {
    private ArrayList<CustomerBean> retOriginalValues;
    private ArrayList<CustomerBean> retDisplayValues;
    private ArrayList<CustomerBean> alRetailerList = null;
    private RetailerListFilter filter;
    private Context context;
    public String searchCode="";
    public String From="";

    public RetailerListAdapter(Context context, ArrayList<CustomerBean> items,String from) {
        super(context, R.layout.retailer_list_adapter, items);
        this.retOriginalValues = items;
        this.retDisplayValues = items;
        this.alRetailerList = items;
        this.context = context;
        this.From=from;
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
            LayoutInflater inflater = (LayoutInflater)context. getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.retailer_list_adapter, parent,false);
        }

        TextView tvRetailerName = (TextView) view.findViewById(R.id.tv_RetailerName);
        ImageView ivMobileNo = (ImageView) view.findViewById(R.id.iv_mobile);
        TextView tv_retailer_mob_no = (TextView) view.findViewById(R.id.tv_retailer_mob_no);
        TextView tvRetailerCatTypeDesc = (TextView) view.findViewById(R.id.tv_retailer_cat_type_desc);
        TextView tv_status_color = (TextView) view.findViewById(R.id.tv_status_color);
        TextView tv_down_color = (TextView) view.findViewById(R.id.tv_down_color);

        tv_status_color.setVisibility(View.GONE);
        tv_down_color.setVisibility(View.GONE);

        TextView tv_address2 = (TextView) view.findViewById(R.id.tv_address2);
        ImageView iv_expand_icon = (ImageView) view.findViewById(R.id.iv_expand_icon);

        final CustomerBean retailerListBean = retDisplayValues.get(position);

        tvRetailerName.setText(retailerListBean.getCustomerName());

        tvRetailerCatTypeDesc.setText("");
        tv_retailer_mob_no.setText(retailerListBean.getCustomerId()+" , "+retailerListBean.getCity());


        String cityVal;

        cityVal = retailerListBean.getCity();

        String disticVal;

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
        if(!retailerListBean.getAddress1().equalsIgnoreCase("")){
            addressVa = retailerListBean.getAddress1();
        }

        if(!retailerListBean.getAddress2().equalsIgnoreCase("")){
            addressVa = addressVa+","+retailerListBean.getAddress2();
        }

        if(!retailerListBean.getAddress3().equalsIgnoreCase("")){
            addressVa = addressVa+","+retailerListBean.getAddress3();
        }
        addressVa = retailerListBean.getAddress3();


        tv_address2.setText(context.getString(R.string.str_concat_two_texts_with_coma,addressVa , "\n"+cityVal +"\n"+ disticVal));



        final View testView = view;
        iv_expand_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageView img = (ImageView)v;

               /* if (retailerListBean.isAddressEnabled()) {
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


        ivMobileNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!retailerListBean.getMobile1().equalsIgnoreCase("")) {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.tel_txt + (retailerListBean.getMobile1())));
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
        }
    }

    private void onListDetails(int id) {
        CustomerBean retailerLB = alRetailerList.get(id);
        Intent intentRetailerDetails =new Intent(context, CustomerDetailsActivity.class);
        intentRetailerDetails.putExtra(Constants.RetailerName, retailerLB.getCustomerName());
//        intentRetailerDetails.putExtra(Constants.CPUID, retailerLB.getUID());
        intentRetailerDetails.putExtra(Constants.CPNo, retailerLB.getCustomerId());

        intentRetailerDetails.putExtra(Constants.Address, retailerLB.getAddress1());
        intentRetailerDetails.putExtra("MobileNo", retailerLB.getMobile1());
       // intentRetailerDetails.putExtra(Constants.VisitCatID,);

        if(From.equalsIgnoreCase(Constants.ProspectiveCustomerList)){
            intentRetailerDetails.putExtra(Constants.comingFrom, Constants.ProspectiveCustomerList);
        }
        else{
            intentRetailerDetails.putExtra(Constants.comingFrom, Constants.RetailerList);
        }

        intentRetailerDetails.putExtra(Constants.NAVFROM, Constants.Retailer);
//        intentRetailerDetails.putExtra(Constants.CPGUID, retailerLB.getCPGUID());
        Constants.VisitNavigationFrom = "";
        context.startActivity(intentRetailerDetails);
    }
}
