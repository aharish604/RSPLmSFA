package com.rspl.sf.msfa.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.ExpandAnimation;
import com.rspl.sf.msfa.stock.DepotStockBean;

import java.util.ArrayList;

/**
 * Created by ccb on 12-09-2017.
 */

public class DepotStockAdapter extends ArrayAdapter<DepotStockBean> {

    private ArrayList<DepotStockBean> alSchemeItemList;
    private ArrayList<DepotStockBean> aldepotItemList;
    private ArrayList<DepotStockBean> aldepotList;
    private Context context;
    private DepotStockAdapter.RetailerListFilter filter;

    TextView tvMatName = null;
    TextView tvMatNo = null;
    TextView tvQty = null;
    ImageView imgexpand = null;
    TextView storage1 = null;
    TextView storage1id = null;
    TextView storagedesc1 = null;
    TextView storage2 = null;
    TextView storage2id = null;
    TextView storagedesc2 = null;
    TextView storage3 = null;
    TextView storage3id = null;
    TextView storagedesc3 = null;
    TextView storage4 = null;
    TextView storage4id = null;
    TextView storagedesc4 = null;
    TextView storage5 = null;
    TextView storage5id = null;
    TextView storagedesc5 = null;

    public DepotStockAdapter(Context context,ArrayList<DepotStockBean> alSchemeItemList) {
        super(context, R.layout.item_depot_stock_list, alSchemeItemList);
        this.alSchemeItemList = alSchemeItemList;
        this.aldepotItemList = alSchemeItemList;
        this.aldepotList = alSchemeItemList;
        this.context = context;


    }


    @Override
    public int getCount() {
        return this.alSchemeItemList != null ? this.alSchemeItemList.size() : 0;
    }


    @NonNull
    @Override
    public DepotStockBean getItem(int item) {
        DepotStockBean retListBean;
        retListBean = this.alSchemeItemList != null ? this.alSchemeItemList.get(item) : null;
        return retListBean;
    }

    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {


        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context. getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_depot_stock_list, parent,false);
        }






        tvMatName = (TextView) view.findViewById(R.id.tv_mat_name);
                tvMatNo = (TextView) view.findViewById(R.id.tv_mat_no);
                tvQty = (TextView) view.findViewById(R.id.tv_qty);
                imgexpand = (ImageView) view.findViewById(R.id.iv_depotstock_expand_icon);
        imgexpand.setTag(position);
                storage1 = (TextView) view.findViewById(R.id.storage1);
                storage1id = (TextView) view.findViewById(R.id.storage1id);
                storagedesc1 = (TextView) view.findViewById(R.id.storage1desc);
                storage2 = (TextView) view.findViewById(R.id.storage2);
                storage2id = (TextView) view.findViewById(R.id.storage2id);
                storagedesc2 = (TextView) view.findViewById(R.id.storage2desc);
                storage3 = (TextView) view.findViewById(R.id.storage3);
                storage3id = (TextView) view.findViewById(R.id.storage3id);
                storagedesc3 = (TextView) view.findViewById(R.id.storage3desc);
                storage4 = (TextView) view.findViewById(R.id.storage4);
                storage4id = (TextView) view.findViewById(R.id.storage4id);
                storagedesc4 = (TextView) view.findViewById(R.id.storage4desc);
                storage5 = (TextView) view.findViewById(R.id.storage5);
                storage5id = (TextView) view.findViewById(R.id.storage5id);
                storagedesc5 = (TextView) view.findViewById(R.id.storage5desc);
        final  LinearLayout liner = (LinearLayout) view.findViewById(R.id.depot_itemList);

        final DepotStockBean depotStockListItem = alSchemeItemList.get(position);

        tvMatName.setText(depotStockListItem.getMatDesc());
        tvMatNo.setText(depotStockListItem.getMatNo());
        tvQty.setText(depotStockListItem.getQty()+" "+ depotStockListItem.getUOM());

        storage1.setText(depotStockListItem.getStorage1());
        storage1id.setText(depotStockListItem.getStorage1Id());
        storagedesc1.setText(depotStockListItem.getStorage1Desc());

        storage2.setText(depotStockListItem.getStorage2());
        storage2id.setText(depotStockListItem.getStorage2Id());
        storagedesc2.setText(depotStockListItem.getStorage2Desc());



        storage3.setText(depotStockListItem.getStorage3());
        storage3id.setText(depotStockListItem.getStorage3Id());
        storagedesc3.setText(depotStockListItem.getStorage3Desc());

        storage4.setText(depotStockListItem.getStorage4());
        storage4id.setText(depotStockListItem.getStorage4Id());
        storagedesc4.setText(depotStockListItem.getStorage4Desc());

        storage5.setText(depotStockListItem.getStorage5());
        storage5id.setText(depotStockListItem.getStorage5Id());
        storagedesc5.setText(depotStockListItem.getStorage5Desc());



        if (depotStockListItem.getStorage1Desc().toString().equals("")) {

            storage1.setVisibility(View.GONE);
            storage1id.setVisibility(View.GONE);
            storagedesc1.setVisibility(View.GONE);

        }


        if (depotStockListItem.getStorage2Desc().toString().equals("")) {

            storage2.setVisibility(View.GONE);
           storage2id.setVisibility(View.GONE);
            storagedesc2.setVisibility(View.GONE);


        }


        if (depotStockListItem.getStorage3Desc().toString().equals("")) {

           storage3.setVisibility(View.GONE);
           storage3id.setVisibility(View.GONE);
          storagedesc3.setVisibility(View.GONE);


        }


        if (depotStockListItem.getStorage4Desc().toString().equals("")) {

            storage4.setVisibility(View.GONE);
           storage4id.setVisibility(View.GONE);
           storagedesc4.setVisibility(View.GONE);

        }


        if (depotStockListItem.getStorage5Desc().toString().equals("")) {

            storage5.setVisibility(View.GONE);
           storage5id.setVisibility(View.GONE);
            storagedesc5.setVisibility(View.GONE);


        }


        final View testView = view;


        imgexpand.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {

                                             ImageView img = (ImageView) v;
                                             final int pos = (int)v.getTag();
                                             /*if (depotStockListItem.isAddressEnabled()) {
                                                 depotStockListItem.setAddressEnabled(false);
                                                 img.setImageResource(R.drawable.down);

                                             } else {
                                                 depotStockListItem.setAddressEnabled(true);
                                                 img.setImageResource(R.drawable.up);

                                             }*/


                                                 View toolbarEmptyText1 = testView.findViewById(R.id.depot_itemList);
                                                 ExpandAnimation expandemptytext1 = new ExpandAnimation(toolbarEmptyText1, 50);
                                                 toolbarEmptyText1.startAnimation(expandemptytext1);




//                                             View toolbarEmptyText2 = testView.findViewById(R.id.depot_layout_storage2);
//                                             ExpandAnimation expandemptytext2 = new ExpandAnimation(toolbarEmptyText2, 50);
//                                             toolbarEmptyText2.startAnimation(expandemptytext2);
//
//
//                                             View toolbarEmptyText3 = testView.findViewById(R.id.depot_layout_storage3);
//                                             ExpandAnimation expandemptytext3 = new ExpandAnimation(toolbarEmptyText3, 50);
//                                             toolbarEmptyText3.startAnimation(expandemptytext3);
//
//
//
//                                             View toolbarEmptyText4 = testView.findViewById(R.id.depot_layout_storage4);
//                                             ExpandAnimation expandemptytext4 = new ExpandAnimation(toolbarEmptyText4, 50);
//                                             toolbarEmptyText4.startAnimation(expandemptytext4);
//
//
//
//                                             View toolbarEmptyText5 = testView.findViewById(R.id.depot_layout_storage5);
//                                             ExpandAnimation expandemptytext5 = new ExpandAnimation(toolbarEmptyText5, 50);
//                                             toolbarEmptyText5.startAnimation(expandemptytext5);









                                         }

                                     });

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

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


    private class RetailerListFilter extends android.widget.Filter {
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (aldepotItemList == null) {
                aldepotItemList = new ArrayList<>(alSchemeItemList);
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = aldepotItemList;
                results.count = aldepotItemList.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();
                ArrayList<DepotStockBean> filteredItems = new ArrayList<>();
                int count = aldepotItemList.size();

                for (int i = 0; i < count; i++) {
                    DepotStockBean item = aldepotItemList.get(i);
                    String mStrRetName = item.getMatDesc().toLowerCase();
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
            alSchemeItemList = (ArrayList<DepotStockBean>) results.values; // has the filtered values
            notifyDataSetChanged();
            aldepotList = alSchemeItemList;
        }
    }
}
