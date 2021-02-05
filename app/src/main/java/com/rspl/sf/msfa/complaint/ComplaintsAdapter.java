package com.rspl.sf.msfa.complaint;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;

import java.util.ArrayList;

/**
 * Created by e10769 on 03-10-2017.
 */

public class ComplaintsAdapter extends ArrayAdapter<ComplaintBean> {
    private ArrayList<ComplaintBean> complaintsOriginalValues;
    private ArrayList<ComplaintBean> complaintsDisplayValues;
    private ArrayList<ComplaintBean> alComplaintBean;
    private ComplaintsAdapter.Filter filter = null;
    Context context = null;
    TextView tvEmptyLay = null;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "";

    public ComplaintsAdapter(Context context, ArrayList<ComplaintBean> items,
                               TextView tvEmptyLay, String mStrBundleRetID, String mStrBundleRetName) {
        super(context, R.layout.activity_invoice_history_list, items);
        this.complaintsOriginalValues = items;
        this.complaintsDisplayValues = items;
        alComplaintBean = items;
        this.context = context;
        this.tvEmptyLay = tvEmptyLay;
        this.mStrBundleRetID = mStrBundleRetID;
        this.mStrBundleRetName = mStrBundleRetName;
    }

    @Override
    public int getCount() {
        return complaintsDisplayValues != null ? complaintsDisplayValues.size() : 0;
    }


    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.feed_back_list_item, null);
        }
        ComplaintBean lb = complaintsDisplayValues.get(position);

        if (lb != null) {

            TextView tv_feed_back_no = (TextView) v.findViewById(R.id.tv_feed_back_no);
            TextView tvFeedbackType = (TextView) v.findViewById(R.id.tv_feedback_type_val);

            tv_feed_back_no.setText(lb.getComplaintId());
            tvFeedbackType.setText(lb.getComplaintCategory());



            v.setId(position);
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComplaintBean selectedList = new ComplaintBean();
                selectedList = alComplaintBean.get(v.getId());

                Intent toInvoiceHisdetails = new Intent(context, ComplaintDetailsActivity.class);
                toInvoiceHisdetails.putExtra(Constants.CPNo, mStrBundleRetID);
                toInvoiceHisdetails.putExtra(Constants.RetailerName, mStrBundleRetName);
                toInvoiceHisdetails.putExtra(Constants.EXTRA_COMPLAINT_BEAN, selectedList);
                context.startActivity(toInvoiceHisdetails);
            }
        });

        return v;
    }

    public android.widget.Filter getFilter() {
        if (filter == null) {
            filter = new ComplaintsAdapter.Filter();
        }
        return filter;
    }

    /**
     * This class search name based on customer name from list.
     */
    private class Filter extends android.widget.Filter {
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (complaintsOriginalValues == null) {
                if(complaintsDisplayValues==null){
                    complaintsDisplayValues=new ArrayList<>();
                }
                complaintsOriginalValues = new ArrayList<>(complaintsDisplayValues);
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = complaintsOriginalValues;
                results.count = complaintsOriginalValues.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();
                ArrayList<ComplaintBean> filteredItems = new ArrayList<>();
                int count = complaintsOriginalValues.size();

                for (int i = 0; i < count; i++) {
                    ComplaintBean item = complaintsOriginalValues.get(i);
                    String mSirSchemeDescription = item.getComplaintId().toLowerCase();
                    if (mSirSchemeDescription.contains(prefixString)) {
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
            complaintsDisplayValues = (ArrayList<ComplaintBean>) results.values; // has the filtered values
            notifyDataSetChanged();
            alComplaintBean = complaintsDisplayValues;

            if (alComplaintBean != null && alComplaintBean.size() > 0)
                tvEmptyLay.setVisibility(View.GONE);
            else
                tvEmptyLay.setVisibility(View.VISIBLE);
        }
    }
}
