package com.rspl.sf.msfa.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.dealerstockprice.DealerPriceBean;
import com.rspl.sf.msfa.dealerstockprice.StocksInfoDisbursementTextWatcher;
import com.rspl.sf.msfa.interfaces.TextWatcherInterface;
import com.rspl.sf.msfa.visit.PriceInfoActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10854 on 23-10-2017.
 */

public class DealerStockPriceRecyclerAdapter extends RecyclerView.Adapter<DealerStockPriceRecyclerAdapter.MyViewHolder> {

    private List<DealerPriceBean> dealerPriceList;
    private List<DealerPriceBean> priceList;
   // private String[] texts;
   private TextWatcherInterface textWatcherInterface = null;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_material, tv_ason_price,tv_material_no;
        public EditText et_price;
        public StocksInfoDisbursementTextWatcher stocksInfoDisbursementTextWatcher;
        public MyViewHolder(View view,StocksInfoDisbursementTextWatcher stocksInfoDisbursementTextWatcher) {
            super(view);
            tv_material = (TextView) view.findViewById(R.id.tv_material);
            tv_ason_price = (TextView) view.findViewById(R.id.tv_ason_date);
            et_price = (EditText) view.findViewById(R.id.et_price);
         //   et_price.requestFocus();
            tv_material_no= (TextView) view.findViewById(R.id.tv_material_no);
            this.stocksInfoDisbursementTextWatcher=stocksInfoDisbursementTextWatcher;
            et_price.addTextChangedListener(stocksInfoDisbursementTextWatcher);
        }
    }


    public DealerStockPriceRecyclerAdapter(List<DealerPriceBean> dealerpriceList) {
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
                .inflate(R.layout.dealer_stockprice_listitem, parent, false);


        return new MyViewHolder(itemView, new StocksInfoDisbursementTextWatcher(priceList,textWatcherInterface));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        DealerPriceBean dealerpriceBean = priceList.get(position);
       holder.tv_material.setText(dealerpriceBean.getMaterial());
        holder.tv_material_no.setText(dealerpriceBean.getMaterialno());
        holder.tv_ason_price.setText(UtilConstants.removeLeadingZerowithTwoDecimal(dealerpriceBean.getPrice()));
        holder.stocksInfoDisbursementTextWatcher.updatePosition(position,holder.et_price);

        if(dealerpriceBean.getInputPrice()!=null && !dealerpriceBean.getInputPrice().equalsIgnoreCase("")){
            holder.et_price.setText(dealerpriceBean.getInputPrice());
        }else{
            holder.et_price.setText("");
        }


       if(position==0){
           if(PriceInfoActivity.isEtFocused==false){
               holder.et_price.requestFocus();
           }

       }


     //   holder.et_price.setText("");
        UtilConstants.editTextDecimalFormat(holder.et_price, 13, 2);


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

    public void textWatcher(TextWatcherInterface textWatcherInterface){
        this.textWatcherInterface =textWatcherInterface;
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