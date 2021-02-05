package com.rspl.sf.msfa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.dbstock.DBStockBean;

import java.util.ArrayList;

/**
 * Created by e10762 on 09-01-2017.
 *
 */




public class DbStockDetailAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    DBStockBean stock;


    private ArrayList<DBStockBean> dbStockDisplayValues = new ArrayList<DBStockBean>();

    public DbStockDetailAdapter(Context context, ArrayList<DBStockBean> items) {

        this.context = context;

        this.dbStockDisplayValues = items;
    }

    @Override
    public int getCount() {

        return dbStockDisplayValues.size();
    }

    @Override
    public Object getItem(int arg0) {

        return null;
    }

    @Override
    public long getItemId(int arg0) {

        return 0;
    }

    @Override
    public View getView(final int pos, View view, ViewGroup arg2) {
        if (inflater == null) {
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null) {
            view = inflater
                    .inflate(R.layout.item_detail_dbstock, null, true);
        }
        stock = dbStockDisplayValues.get(pos);
        TextView tvBatch = (TextView) view
                .findViewById(R.id.item_detail_dbstk_batch);
        TextView tvMFD = (TextView) view
                .findViewById(R.id.item_detail_dbstk_mfd);
        TextView tvQuantity = (TextView) view
                .findViewById(R.id.item_detail_dbstk_quantity);

        TextView tvMRP = (TextView) view
                .findViewById(R.id.item_dbstk_mrp);
        TextView tvRetPrice = (TextView) view
                .findViewById(R.id.item_dbstk_ret_price);
        tvBatch.setText(stock.getBatch());
        tvMFD.setText(stock.getMFD());
        tvQuantity.setText(stock.getQAQty()+" "+stock.getUom());
        tvMRP.setText(stock.getMRP()+" "+stock.getCurrency());
        tvRetPrice.setText(stock.getLandingPrice()+" "+stock.getCurrency());

        return view;
    }




}





