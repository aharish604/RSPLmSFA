package com.rspl.sf.msfa.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
 */

public class RoutePlanAdapter extends ArrayAdapter<CustomerBean> {
    private ArrayList<CustomerBean> retDisplayValues;
    private ArrayList<CustomerBean> retOriginalValues;
    private RetailerListFilter filter;
    private ArrayList<CustomerBean> alRetailerList = null;
    private View context;
    private String mStrRouteType;
    TextView tvEmptyLay;
    public String searchCode="";

    public RoutePlanAdapter(View context, ArrayList<CustomerBean> items,String mStrRouteType, TextView tvEmptyLay) {
        super(context.getContext(), R.layout.beat_plan_line_item, items);
        this.retDisplayValues = items;
        alRetailerList = items;
        this.context=context;
        this.mStrRouteType = mStrRouteType;
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
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.adhoc_list_adapter, parent,false);

        }

        TextView tvRetailerName = (TextView) view.findViewById(R.id.tv_RetailerName);
        ImageView ivMobileNo = (ImageView) view.findViewById(R.id.iv_mobile);
        TextView tv_retailer_mob_no = (TextView) view.findViewById(R.id.tv_retailer_mob_no);
        TextView tvRetailerCatTypeDesc = (TextView) view.findViewById(R.id.tv_retailer_cat_type_desc);
        TextView tv_status_color = (TextView) view.findViewById(R.id.tv_status_color);
        TextView tv_down_color = (TextView) view.findViewById(R.id.tv_down_color);


        TextView tv_address2 = (TextView) view.findViewById(R.id.tv_address2);
        ImageView iv_expand_icon = (ImageView) view.findViewById(R.id.iv_expand_icon);

        final CustomerBean retailerListBean = retDisplayValues.get(position);

        tvRetailerName.setText(retailerListBean.getCustomerName());
        tvRetailerCatTypeDesc.setText(retailerListBean.getMobileNumber());
        tv_retailer_mob_no.setText(retailerListBean.getCustomerId());

        if(mStrRouteType.equalsIgnoreCase(Constants.BeatPlan)) {

            try {
                if (!OfflineManager.getAtleastOneEntity(Constants.getCurrentMonthInvoiceQry(retailerListBean.getUID()))){
                   tvRetailerName.setTextColor(Color.RED);
                }else{
                    tvRetailerName.setTextColor(Color.BLACK);
                }
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }

        String cityVal;

        cityVal = retailerListBean.getCity();

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



        tv_address2.setText(context.getContext().getString(R.string.str_concat_two_texts_with_coma, addressVa, "\n" + cityVal + "\n" + disticVal));



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


        try {
            String mStrVisitStartEndQry = "";
            String mStrVisitStartedQry = "";
            String mStrSPGUID = Constants.getSPGUID(Constants.SPGUID);
            if(mStrRouteType.equalsIgnoreCase(Constants.BeatPlan)) {
                mStrVisitStartEndQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq datetime'" + UtilConstants.getNewDate() + "' " +
                        "and CPGUID eq '" + retailerListBean.getCustomerId() + "'and StatusID eq '01' and "+Constants.SPGUID+" eq guid'"+mStrSPGUID+"'";

                mStrVisitStartedQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq null " +
                        "and CPGUID eq '" + retailerListBean.getCustomerId() + "'and StatusID eq '01' and "+Constants.SPGUID+" eq guid'"+mStrSPGUID+"'";
            }
            else
            {
                mStrVisitStartEndQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq datetime'" + UtilConstants.getNewDate() + "' " +
                        "and CPGUID eq '" + retailerListBean.getCustomerId() + "' and "+Constants.StatusID+" eq '01' and "+Constants.SPGUID+" eq guid'"+mStrSPGUID+"'";

                mStrVisitStartedQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq null " +
                        "and CPGUID eq '" + retailerListBean.getCustomerId() + "' and "+Constants.StatusID+" eq '01' and "+Constants.SPGUID+" eq guid'"+mStrSPGUID+"'";
            }

            if(OfflineManager.getVisitStatusForCustomer(mStrVisitStartedQry)){
                tv_status_color.setBackgroundResource(R.color.YELLOW);
                tv_down_color.setBackgroundResource(R.color.YELLOW);
            } else if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartEndQry)) {
                tv_status_color.setBackgroundResource(R.color.GREEN);
                tv_down_color.setBackgroundResource(R.color.GREEN);
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
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.tel_txt+ (retailerListBean.getMobileNumber())));
                    context.getContext().startActivity(dialIntent);
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
            if(alRetailerList!=null && alRetailerList.size()>0){
                tvEmptyLay.setVisibility(View.GONE);
            } else
                tvEmptyLay.setVisibility(View.VISIBLE);

        }
    }

    private void onListDetails(int id) {
        CustomerBean retailerLB = alRetailerList.get(id);
        try {

            if(retailerLB.getCustomerType().equals(Constants.ProspectiveCustomerList)){
                Intent intent=null;
                intent =new Intent(context.getContext(), CustomerDetailsActivity.class);
                intent.putExtra(Constants.RetailerName, retailerLB.getCustomerName());
                intent.putExtra(Constants.CPUID, retailerLB.getCustomerId());
                intent.putExtra(Constants.CPNo, retailerLB.getCustomerId());
                intent.putExtra(Constants.CPGUID, retailerLB.getCustomerId());
                intent.putExtra(Constants.BeatType, Constants.RouteList);
                intent.putExtra("Address", retailerLB.getAddress1());
                intent.putExtra("MobileNo",retailerLB.getMobileNumber());
                intent.putExtra(Constants.VisitType, retailerLB.getVisitType());
                intent.putExtra(Constants.comingFrom, Constants.ProspectiveCustomerList);
                intent.putExtra("BeatPlanProspectiveCustomer", "BeatPlanProspectiveCustomer");
                intent.putExtra(Constants.VisitCatID,"01");
                Constants.Route_Plan_No = retailerLB.getRouteID();
                Constants.Route_Plan_Desc = retailerLB.getRouteDesc();
                Constants.Route_Plan_Key =retailerLB.getRoutePlanKey();
                Constants.Visit_Type = retailerLB.getVisitType();
                Constants.CustomerType = retailerLB.getCustomerType();

                Constants.VisitNavigationFrom = "";
                context.getContext().startActivity(intent);

            }else{

                Intent intent=null;
                intent =new Intent(context.getContext(), CustomerDetailsActivity.class);
                intent.putExtra(Constants.RetailerName, retailerLB.getCustomerName());
                intent.putExtra(Constants.CPUID, retailerLB.getCustomerId());
                intent.putExtra(Constants.CPNo, retailerLB.getCustomerId());
                intent.putExtra(Constants.CPGUID, retailerLB.getCustomerId());
                intent.putExtra(Constants.BeatType, Constants.RouteList);
                intent.putExtra(Constants.VisitType, retailerLB.getVisitType());
                intent.putExtra(Constants.comingFrom, Constants.RouteList);
                intent.putExtra(Constants.VisitCatID,"01");
                Constants.Route_Plan_No = retailerLB.getRouteID();
                Constants.Route_Plan_Desc = retailerLB.getRouteDesc();
                Constants.Route_Plan_Key =retailerLB.getRoutePlanKey();
                Constants.Visit_Type = retailerLB.getVisitType();
                Constants.CustomerType = retailerLB.getCustomerType();

                Constants.VisitNavigationFrom = "";
                context.getContext().startActivity(intent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
