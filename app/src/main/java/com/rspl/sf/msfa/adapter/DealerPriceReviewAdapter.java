package com.rspl.sf.msfa.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.dealerstockprice.DealerPriceBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10854 on 25-10-2017.
 */

public class DealerPriceReviewAdapter extends RecyclerView.Adapter<DealerPriceReviewAdapter.MyViewHolder> {

    private List<DealerPriceBean> dealerPriceList;
    private List<DealerPriceBean> priceList;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_material, tv_ason_price,tv_material_no;
        public TextView tv_price;
        public MyViewHolder(View view) {
            super(view);
            tv_material = (TextView) view.findViewById(R.id.tv_material);
            tv_ason_price = (TextView) view.findViewById(R.id.tv_ason_date);
            tv_price = (TextView) view.findViewById(R.id.tv_price);
            tv_material_no= (TextView) view.findViewById(R.id.tv_material_no);

        }
    }


    public DealerPriceReviewAdapter(List<DealerPriceBean> dealerpriceList) {
        this.dealerPriceList = dealerpriceList;
        this.priceList = new ArrayList<>();
        this.priceList.addAll(this.dealerPriceList);

       /* texts=new String[dealerpriceList.size()];
        for(int i=0;i<dealerpriceList.size();i++){
            texts[i]="";
        }*/
    }

    public void updateList(List<DealerPriceBean> list){
        //  dealerPriceList = list;
        priceList=list;
        notifyDataSetChanged();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dealer_price_review_listitem, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        DealerPriceBean dealerpriceBean = priceList.get(position);
        holder.tv_material.setText(dealerpriceBean.getMaterial());
        holder.tv_material_no.setText(dealerpriceBean.getMaterialno());
        holder.tv_ason_price.setText(UtilConstants.removeLeadingZerowithTwoDecimal(dealerpriceBean.getPrice()));
        holder.tv_price.setText(dealerpriceBean.getInputPrice());



        //   holder.et_price.setText("");
       // UtilConstants.editTextDecimalFormat(holder.tv_price, 13, 2);


       /* holder.et_price.setText(texts [position]);
        holder.et_price.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                //setting data to array, when changed
                texts [position] = s.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                //blank
            }


        });*/


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* //   Intent i=new Intent(view.getRootView().getContext(),RetailerDetails.class);

                Intent intentRetailerDetails = new Intent(view.getRootView().getContext(), RetailerDetails.class);
                intentRetailerDetails.putExtra(Constants.RetailerName, retailerList.get(position).getName());
                intentRetailerDetails.putExtra(Constants.CPUID,  retailerList.get(position).getCpuid());
                intentRetailerDetails.putExtra(Constants.CPNo,  retailerList.get(position).getNumber());
                //   intentRetailerDetails.putExtra(Constants.comingFrom, Constants.RetailerList);
                //  intentRetailerDetails.putExtra(Constants.NAVFROM, Constants.Retailer);
                intentRetailerDetails.putExtra(Constants.CPGUID, retailerList.get(position).getCpguid());
                intentRetailerDetails.putExtra("Guid", retailerList.get(position).getGuid());
                intentRetailerDetails.putExtra("Currency", retailerList.get(position).getCurrency());
                view.getRootView().getContext().startActivity(intentRetailerDetails);*/

            }
        });



    }

    @Override
    public int getItemCount() {
        return priceList.size();
    }




    public void filterSampleDisbursement(final String text) {
      /*  new Thread(new Runnable() {
            @Override
            public void run() {*/
        priceList.clear();
        if (TextUtils.isEmpty(text)) {
            priceList.addAll(dealerPriceList);
        } else {
            for (DealerPriceBean item : dealerPriceList) {
//                        if(displayType==1) {
                DealerPriceBean retailerStockBean = null;

                if (!TextUtils.isEmpty(text)) {
                    if (item.getMaterial().toLowerCase().contains(text.toLowerCase())) {
                        retailerStockBean = item;
                    } else {
                        retailerStockBean = null;
                        continue;
                    }
                }
                if (retailerStockBean != null) {
                    priceList.add(item);

                }

            }
        }
//                ((Activity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
        updateList(priceList);
        //notifyDataSetChanged();

//                    }
//                });

//            }
      /*  }).start();*/

    }






}