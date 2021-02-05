package com.rspl.sf.msfa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.retailerStock.RetailerStockBean;

import java.util.ArrayList;

/**
 * Created by e10526 on 1/28/2017.
 *
 */

public class MultiSelectionAdapter  extends BaseAdapter {

    private ArrayList<RetailerStockBean> custom_list;
    private LayoutInflater layoutInflater;
    private MultiSelectionAdapter.ViewHolder holder;
    private static String selected = "";
    PopupWindow pw;

    public static String getSelected() {
        return selected;
    }


    public MultiSelectionAdapter(Context context, ArrayList<RetailerStockBean> items, PopupWindow pw) {
        this.pw=pw;
        custom_list = new ArrayList<>();
        custom_list.clear();
        custom_list.addAll(items);
        layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return custom_list.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.drop_down_check_box_item, null);
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.tv_dropdown);
            holder.chkbox = (CheckBox) convertView.findViewById(R.id.cb_mat_grp_sel);
            convertView.setTag(holder);
            holder.chkbox.setOnClickListener( new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    CheckBox cb = (CheckBox) v;
                    RetailerStockBean _state = (RetailerStockBean) cb.getTag();
                    _state.setSelected(cb.isChecked());
                }
            });

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RetailerStockBean retailerStockBean=custom_list.get(position);
        holder.tv.setText(retailerStockBean.getMaterialDesc());
        holder.chkbox.setTag(retailerStockBean);

//        if(retailerStockBean.getSelected())
//            holder.chkbox.setBackgroundResource(android.R.drawable.checkbox_on_background);
//        else
//            holder.chkbox.setBackgroundResource(android.R.drawable.checkbox_off_background);

        return convertView;
    }



    private class ViewHolder {
        TextView tv;
        CheckBox chkbox;
    }




    public RetailerStockBean setSelected(int position){


        return custom_list.get(position);
    }
}
