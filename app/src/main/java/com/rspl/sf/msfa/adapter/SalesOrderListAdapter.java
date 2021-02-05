package com.rspl.sf.msfa.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.mbo.SalesOrderBean;
import com.rspl.sf.msfa.reports.InvoiceHistoryBean;

import java.util.ArrayList;

/**
 * Created by e10526 on 09-12-2016.
 *
 */

public class SalesOrderListAdapter extends ArrayAdapter<SalesOrderBean> {
    private ArrayList<SalesOrderBean> SalesHisOriginalValues;
    private ArrayList<SalesOrderBean> SalesHisDisplayValues;
    private InvoiceListFilter filter;
    private ArrayList<SalesOrderBean> salesOrderBeen;
    private Context context;
    private InvoiceHistoryBean selectedList;
    private String mStrCPGUID,mStrCPNO,mStrCpName,mStrCPUID;

    public SalesOrderListAdapter(Context context, int textViewResourceId,
                                 ArrayList<SalesOrderBean> items, Bundle bundle)
    {
        super(context, R.layout.activity_invoice_history_list, items);
        this.SalesHisOriginalValues = items;
        this.SalesHisDisplayValues = items;
        salesOrderBeen = items;
        this.context = context;
        this.mStrCpName = bundle.getString(Constants.RetailerName);
        this.mStrCPNO = bundle.getString(Constants.CPNo);
        this.mStrCPGUID= bundle.getString(Constants.CPGUID);
        this.mStrCPUID= bundle.getString(Constants.CPUID);
    }

    @Override
    public int getCount() {
        return SalesHisDisplayValues != null ? SalesHisDisplayValues.size() : 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context. getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.invoice_history_item_list, null);
        }
        SalesOrderBean lb = SalesHisDisplayValues.get(position);

        if (lb != null) {

            TextView invNO = (TextView) v.findViewById(R.id.tv_in_history_no);
            TextView invDate = (TextView) v.findViewById(R.id.tv_in_history_date);
            TextView invAmount = (TextView) v.findViewById(R.id.tv_in_history_amt);
            TextView tvStatusIndicator = (TextView) v.findViewById(R.id.tv_status_indicator);
//            invNO.setText(lb.getSalesOrderNo());
//            invDate.setText(lb.getOrderDate());
//            invAmount.setText(lb.getTotalAmount());
//            invAmount.setText((UtilConstants.removeLeadingZerowithTwoDecimal(lb.getInvoiceAmount())+" "+ lb.getCurrency()).trim());

          /*  if(lb.getInvoiceStatus().toString().equals("01")){
                tvStatusIndicator.setBackgroundResource(R.color.RED);

            }else if(lb.getInvoiceStatus().toString().equals("02")){
                tvStatusIndicator.setBackgroundResource(R.color.YELLOW);
            }
            else if(lb.getInvoiceStatus().toString().equals("03")){
                tvStatusIndicator.setBackgroundResource(R.color.GREEN);
            }*/

            v.setId(position);
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                selectedList = new InvoiceHistoryBean();
//                selectedList = salesOrderBeen.get(v.getId());
//
//                Intent toInvoiceHisdetails = new Intent(context, InvoiceDetailsActivity.class);
//                toInvoiceHisdetails.putExtra(Constants.CPNo, mStrCPNO);
//                toInvoiceHisdetails.putExtra(Constants.RetailerName, mStrCpName);
//                toInvoiceHisdetails.putExtra(Constants.CPUID, mStrCPUID);
//                toInvoiceHisdetails.putExtra(Constants.InvoiceNo, selectedList.getInvoiceNo());
//                toInvoiceHisdetails.putExtra(Constants.InvoiceGUID, selectedList.getInvoiceGuid());
//                toInvoiceHisdetails.putExtra(Constants.CPGUID, mStrCPNO);
//                toInvoiceHisdetails.putExtra(Constants.Currency, selectedList.getCurrency());
//                toInvoiceHisdetails.putExtra(Constants.DeviceNo, selectedList.getDeviceNo());
//                toInvoiceHisdetails.putExtra(Constants.CollectionAmount, "10.0");
//                toInvoiceHisdetails.putExtra(Constants.STATUS, selectedList.getInvoiceStatus());
//                toInvoiceHisdetails.putExtra(Constants.DeviceStatus, selectedList.getDeviceStatus());
//                toInvoiceHisdetails.putExtra(Constants.InvDate, selectedList.getInvoiceDate());
//                toInvoiceHisdetails.putExtra(Constants.InvAmount, selectedList.getInvoiceAmount());

             //   context.startActivity(toInvoiceHisdetails);
            }
        });

        return v;
    }

    public android.widget.Filter getFilter() {
        if (filter == null) {
            filter = new InvoiceListFilter();
        }
        return filter;
    }

    /**
     * This class search invoices based on invoice number from list.
     */
    private class InvoiceListFilter extends android.widget.Filter {
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (SalesHisOriginalValues == null) {
                SalesHisOriginalValues = new ArrayList<>(SalesHisDisplayValues);
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = SalesHisOriginalValues;
                results.count = SalesHisOriginalValues.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();
                ArrayList<SalesOrderBean> filteredItems = new ArrayList<>();
                int count = SalesHisOriginalValues.size();

                for (int i = 0; i < count; i++) {
                    SalesOrderBean item = SalesHisOriginalValues.get(i);
//                    String mSirSchemeDescription = item.getSalesOrderNo().toLowerCase();
//                    if (mSirSchemeDescription.contains(prefixString)) {
//                        filteredItems.add(item);
//                    }
                }
                results.values = filteredItems;
                results.count = filteredItems.size();
            }
            return results;
        }
        @Override
        protected void publishResults(CharSequence prefix, FilterResults results) {
            //noinspection unchecked
            SalesHisDisplayValues = (ArrayList<SalesOrderBean>) results.values; // has the filtered values
            notifyDataSetChanged();
            salesOrderBeen = SalesHisDisplayValues;
        }
    }
}
