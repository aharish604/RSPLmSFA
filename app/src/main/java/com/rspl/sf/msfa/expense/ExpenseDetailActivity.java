package com.rspl.sf.msfa.expense;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10742 on 5/30/2017.
 */

public class ExpenseDetailActivity extends AppCompatActivity {

    RecyclerView rvexpenseList = null;
    TextView tvEmptyListLay = null;

    TextView tvClaimNumber = null;
    TextView tvRaisedDate = null;
    TextView tvExpType = null;
    LinearLayout llStatus = null;

    ArrayList<ExpenseBeanJK> alExpense = new ArrayList<>();

    String mStrExpenseGuid = "", mStrExpenseNo = "", mStrExpenseDate = "",
            mStrExpenseTypeDesc = "", mStrStatus = "";
    String mStrFrom="";
    byte[] imageByteArray =null;
    ImageView imageViewFront;
    LinearLayout ll_vertical;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
       // ActionBarView.initActionBarView(this, true, getString(R.string.title_expense_details));

        setContentView(R.layout.activity_expense_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_expense_details), 0);
        Bundle bundle = getIntent().getExtras();
        mStrExpenseGuid = bundle.getString(Constants.ExpenseGUID);
        mStrExpenseNo = bundle.getString(Constants.ExpenseNo);
        mStrExpenseDate = bundle.getString(Constants.ExpenseDate);
        mStrExpenseTypeDesc = bundle.getString(Constants.ExpenseTypeDesc);
        mStrStatus = bundle.getString(Constants.Status);
        mStrFrom=bundle.getString("from");

        initUI();
    }

    /*Initializes UI*/
    private void initUI() {
        rvexpenseList = (RecyclerView) findViewById(R.id.rv_expense_det_list);
        rvexpenseList.setLayoutManager(new LinearLayoutManager(this));
        tvEmptyListLay = (TextView) findViewById(R.id.tv_empty_lay);
        imageViewFront = (ImageView) findViewById(R.id.iv_image_front);
        ll_vertical= (LinearLayout) findViewById(R.id.ll_vertical);

        tvClaimNumber = (TextView) findViewById(R.id.tv_expense_no);
        tvRaisedDate = (TextView) findViewById(R.id.tv_expense_date);
        tvExpType = (TextView) findViewById(R.id.tv_expense_type);
        llStatus = (LinearLayout) findViewById(R.id.ll_status);

        tvClaimNumber.setText(mStrExpenseNo);
        tvRaisedDate.setText(mStrExpenseDate);
        tvExpType.setText(mStrExpenseTypeDesc);

        getExpenseList();

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    /*Gets Expense List*/
    void getExpenseList() {


        if(mStrFrom.equalsIgnoreCase("expense list")){
            alExpense.clear();
            try {
                alExpense = OfflineManager.getExpenseDetails(Constants.ExpenseItemDetails + "?$filter=" + Constants.ExpenseGUID +
                        " eq guid'" + mStrExpenseGuid + "'");
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            if(alExpense!=null && alExpense.size()!=0){
                for(int i=0;i<alExpense.size();i++){
                    try {



                        ArrayList<MediaLink> medialink =OfflineManager.getExpenseDocumentMedialink(Constants.ExpenseDocuments + "?$filter=" + Constants.ExpenseItemGUID +
                                " eq guid'" +alExpense.get(i).getExpenseItemGuid()+ "'",alExpense.get(i).getExpenseItemGuid(),"ExpenseList");
                    /*    if((medialink.size())!=0 && (medialink!=null)){
                            for(int j=0;j<medialink.size();j++){

                            }
                        }*/
                    alExpense.get(i).setMedialink(medialink);


                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }


                }
            }

        }else{
            alExpense.clear();
            try {
                alExpense = OfflineManager.getExpenseDetailsDevice(ExpenseDetailActivity.this,Constants.Expenses,mStrExpenseNo);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

        /*    if(alExpense!=null && alExpense.size()!=0){
                for(int i=0;i<alExpense.size();i++){
                    try {

                        ArrayList<MediaLink> medialink =OfflineManager.getExpenseDocumentMedialink(Constants.ExpenseDocuments + "?$filter=" + Constants.ExpenseItemGUID +
                                " eq guid'" +alExpense.get(i).getExpenseItemGuid()+ "'",alExpense.get(i).getExpenseItemGuid(),"ExpenseDeviceList");
                    *//*    if((medialink.size())!=0 && (medialink!=null)){
                            for(int j=0;j<medialink.size();j++){

                            }
                        }*//*
                        alExpense.get(i).setMedialink(medialink);


                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }


                }
            }*/


        }


     

        rvexpenseList.setAdapter(new ExpenseItemAdapter(alExpense, this, tvEmptyListLay));
    }
    private void getImageDetails(String medialink) {
        try {
            imageByteArray = OfflineManager.getImageList(medialink);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        setImageToImageView();
    }


    private void setImageToImageView() {

        final Dialog dialog = new Dialog(ExpenseDetailActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.img_expand);
        // set the custom dialog components - text, image and
        // button
        ImageView image = (ImageView) dialog.findViewById(R.id.imageView1);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        image.setImageBitmap(bitmap);
        dialog.show();


       /* try {

            Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
            LinearLayoutCompat.LayoutParams params=new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

       *//*     imageViewFront=new ImageView(ExpenseDetailActivity.this);
            imageViewFront.setLayoutParams(params);*//*
            imageViewFront.setImageBitmap(bitmap);
          //  ll_vertical.addView(imageViewFront);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        imageViewFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                final Dialog dialog = new Dialog(ExpenseDetailActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.img_expand);
                // set the custom dialog components - text, image and
                // button
                ImageView image = (ImageView) dialog.findViewById(R.id.imageView1);

                image.setImageBitmap(BitmapFactory.decodeByteArray(imageByteArray, 0,
                        imageByteArray.length));
                dialog.show();

            }
        });*/
//        }
    }

    public class ExpenseItemAdapter extends RecyclerView.Adapter<ExpenseItemAdapter.ViewHolder> {
        private ArrayList<ExpenseBeanJK> alExpenseList = new ArrayList<>();
        private Context context;
        private TextView tvEmptyListLay;

        public ExpenseItemAdapter(ArrayList<ExpenseBeanJK> alExpenseList, Context context, TextView tvEmptyListLay) {
            this.alExpenseList = alExpenseList;
            this.context = context;
            this.tvEmptyListLay = tvEmptyListLay;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_expense_detail,
                    viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {

            final ExpenseBeanJK productListItem = alExpenseList.get(position);

            viewHolder.tvBeat.setText(productListItem.getBeat());
            viewHolder.tvLocation.setText(productListItem.getLocation());
            viewHolder.tvModeOfTransport.setText(productListItem.getModeOfTransportation());
            viewHolder.tvDistance.setText(productListItem.getDistance()+" "+productListItem.getUOM());
            viewHolder.tvAmount.setText(UtilConstants.removeLeadingZerowithTwoDecimal(productListItem.getAmount()) +
                    " " + productListItem.getCurrency());
            viewHolder.tvRemarks.setText(productListItem.getRemarks());
            viewHolder.tvItemNo.setText(String.valueOf((position + 1) * 10));
            viewHolder.tv_attachment_image.setText("");

            for(int i=0;i<alExpenseList.get(position).getMedialink().size();i++){
                TextView tv_image=new TextView(ExpenseDetailActivity.this);
                LinearLayoutCompat.LayoutParams params=new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tv_image.setLayoutParams(params);
                tv_image.setText(alExpenseList.get(position).getMedialink().get(i).getFilename());
                final String link=alExpenseList.get(position).getMedialink().get(i).getLink();
                tv_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getImageDetails(link);

                    }
                });

                viewHolder.ll_vertical_layout.addView(tv_image);
            }

        }

        @Override
        public int getItemCount() {
            if (alExpenseList.size() == 0) {
                tvEmptyListLay.setVisibility(View.VISIBLE);
            } else {
                tvEmptyListLay.setVisibility(View.GONE);
            }
            return alExpenseList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvBeat = null, tvLocation = null, tvModeOfTransport = null, tvDistance = null,
                    tvAmount = null, tvRemarks = null, tvItemNo = null, tv_attachment_image=null;
            LinearLayout ll_vertical_layout=null;

            public ViewHolder(View view) {
                super(view);
                tvBeat = (TextView) view.findViewById(R.id.tv_beat);
                tvLocation = (TextView) view.findViewById(R.id.tv_location);
                tvModeOfTransport = (TextView) view.findViewById(R.id.tv_mode_of_trans);
                tvDistance = (TextView) view.findViewById(R.id.tv_distance);
                tvAmount = (TextView) view.findViewById(R.id.tv_amount);
                tvRemarks = (TextView) view.findViewById(R.id.tv_remarks);
                tvItemNo = (TextView) view.findViewById(R.id.tv_expense_item_no);
                tv_attachment_image= (TextView) view.findViewById(R.id.tv_attachment_image);
                ll_vertical_layout= (LinearLayout) view.findViewById(R.id.ll_vertical_layout);
            }
        }
    }

   /* private void getImage() {

        if (mStrFrom.equalsIgnoreCase("expense list")) {
            alExpense.clear();
            try {
                alExpense = OfflineManager.getExpenseDetails(Constants.ExpenseItemDetails + "?$filter=" + Constants.ExpenseGUID +
                        " eq guid'" + mStrExpenseGuid + "'");
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }


        }

    }*/
}
