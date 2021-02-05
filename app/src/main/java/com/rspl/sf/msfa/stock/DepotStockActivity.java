package com.rspl.sf.msfa.stock;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.DepotStockAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.ExpandAnimation;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10742 on 6/7/2017.
 */

public class DepotStockActivity extends AppCompatActivity {
    RecyclerView rvSchemeList = null;
    TextView tvEmptyListLay = null;
    ArrayList<DepotStockBean> depotStockBeen = new ArrayList<>();
    EditText etSearchScheme = null;
    private ProgressDialog pdLoadDialog;
    private DepotStockAdapter depotStockAdapter = null;
    ListView lv_depot_stock_list = null;
    String cs ="";

    LinearLayout depot_linerlayout;
    boolean matflag = true;
    int matcursorLength = 0;
    private ArrayList<DepotStockBean> filteredArraylist=null;
    private String[][] arrPlants = null;

//    private String searchStr[] = {"Desc", "Code"};
   private Spinner spnrPlant = null;
    private String selectedPlant = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depot_stock);
        //ActionBarView.initActionBarView(this, true, getString(R.string.title_depot_stock));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_depot_stock), 0);
        initUI();

    }

    /*Initializes UI*/
    void initUI() {
        //rvSchemeList = (RecyclerView) findViewById(R.id.rv_stock_list);
       // lv_depot_stock_list = (ListView)findViewById(R.id.lv_depot_stock_list);
       // rvSchemeList.setLayoutManager(new LinearLayoutManager(this));
       // tvEmptyListLay = (TextView) findViewById(R.id.tv_empty_lay);
        etSearchScheme = (EditText) findViewById(R.id.et_name_search);
        spnrPlant = (Spinner) findViewById(R.id.spnr_plant);

        getPlants();





        etSearchScheme.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged( CharSequence s, int start, int before, int count) {


        filteredArraylist = new ArrayList<>();
        for (int i = 0; i < depotStockBeen.size(); i++) {
            DepotStockBean item = depotStockBeen.get(i);

            if(item.getMatDesc()!=null && !item.getMatDesc().equalsIgnoreCase("")){
                if (item.getMatDesc().toLowerCase()
                        .contains(s.toString().toLowerCase().trim())) {
                    filteredArraylist.add(item);

                }
            }
            getDisplayValueMat(filteredArraylist);
        }



            }

            @Override
            public void afterTextChanged(Editable s) {

             //  new  GetSearchedList().execute(s.toString());

            }
        });
    }

public void getPlants(){


    try {
        String mStrConfigQry = "UserProfileAuthSet?$filter=Application eq 'PD' and AuthOrgTypeID eq '000001'";
        arrPlants = OfflineManager.getPlantList(mStrConfigQry);
    } catch (OfflineODataStoreException e) {
        LogManager.writeLogError(Constants.error_txt  + e.getMessage());
    }

        ArrayAdapter<String> searchadapter = new ArrayAdapter<>(this, R.layout.custom_textview, arrPlants[1]);
        searchadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrPlant.setAdapter(searchadapter);
        spnrPlant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {
                selectedPlant = arrPlants[0][position];
                etSearchScheme.setText("");

                try {
                    new GetDepotStockList().execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


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
    void getDepotStockList() {

        depotStockBeen.clear();
        try {
            depotStockBeen = OfflineManager.getDepotStock(Constants.PlantStock+"?$filter=PlantID eq '"+selectedPlant+"'");
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

    }




    private void getDisplayValueMat(final ArrayList<DepotStockBean> depotStockBeen) {
        // TODO Auto-generated method stub


        if (!matflag) {
            depot_linerlayout.removeAllViews();
        }
        matflag = false;

        depot_linerlayout = (LinearLayout) findViewById(R.id.depot_stock_layout);

        final TableLayout tableHeading = (TableLayout) LayoutInflater.from(this)
                .inflate(R.layout.table_view, null);
        matcursorLength = depotStockBeen.size();

        matcursorLength = depotStockBeen.size();


        TextView[] tvMatName = new TextView[matcursorLength];
        TextView[] tvMatNo = new TextView[matcursorLength];
        TextView[] tvQty = new TextView[matcursorLength];
        final ImageView[] imgexpand = new ImageView[matcursorLength];
        TextView[] storage1 = new TextView[matcursorLength];
        TextView[] storage1id = new TextView[matcursorLength];
        TextView[] storagedesc1 = new TextView[matcursorLength];
        TextView[] storage2 = new TextView[matcursorLength];
        TextView[] storage2id = new TextView[matcursorLength];
        TextView[] storagedesc2 = new TextView[matcursorLength];
        TextView[] storage3 = new TextView[matcursorLength];
        TextView[] storage3id = new TextView[matcursorLength];
        TextView[] storagedesc3 = new TextView[matcursorLength];
        TextView[] storage4 = new TextView[matcursorLength];
        TextView[] storage4id = new TextView[matcursorLength];
        TextView[] storagedesc4 = new TextView[matcursorLength];
        TextView[] storage5 = new TextView[matcursorLength];
        TextView[] storage5id = new TextView[matcursorLength];
        TextView[] storagedesc5 = new TextView[matcursorLength];


        if (matcursorLength > 0) {
            for (int i = 0; i < matcursorLength; i++) {
               final DepotStockBean depotStockListItem = depotStockBeen.get(i);
                final int selvalue = i;
                final LinearLayout rowRelativeLayout = (LinearLayout) LayoutInflater
                        .from(this).inflate(
                                R.layout.item_depot_stock_list, null);



                tvMatName[i] = (TextView) rowRelativeLayout.findViewById(R.id.tv_mat_name);
                tvMatNo[i] = (TextView) rowRelativeLayout.findViewById(R.id.tv_mat_no);
                tvQty[i] = (TextView) rowRelativeLayout.findViewById(R.id.tv_qty);
                imgexpand[i] = (ImageView) rowRelativeLayout.findViewById(R.id.iv_depotstock_expand_icon);
                storage1[i] = (TextView) rowRelativeLayout.findViewById(R.id.storage1);
                storage1id[i] = (TextView) rowRelativeLayout.findViewById(R.id.storage1id);
                storagedesc1[i] = (TextView) rowRelativeLayout.findViewById(R.id.storage1desc);
                storage2[i] = (TextView) rowRelativeLayout.findViewById(R.id.storage2);
                storage2id[i] = (TextView) rowRelativeLayout.findViewById(R.id.storage2id);
                storagedesc2[i] = (TextView) rowRelativeLayout.findViewById(R.id.storage2desc);
                storage3[i] = (TextView) rowRelativeLayout.findViewById(R.id.storage3);
                storage3id[i] = (TextView) rowRelativeLayout.findViewById(R.id.storage3id);
                storagedesc3[i] = (TextView) rowRelativeLayout.findViewById(R.id.storage3desc);
                storage4[i] = (TextView) rowRelativeLayout.findViewById(R.id.storage4);
                storage4id[i] = (TextView) rowRelativeLayout.findViewById(R.id.storage4id);
                storagedesc4[i] = (TextView) rowRelativeLayout.findViewById(R.id.storage4desc);
                storage5[i] = (TextView) rowRelativeLayout.findViewById(R.id.storage5);
                storage5id[i] = (TextView) rowRelativeLayout.findViewById(R.id.storage5id);
                storagedesc5[i] = (TextView) rowRelativeLayout.findViewById(R.id.storage5desc);





            tvMatName[i].setText(depotStockListItem.getMatDesc());
           tvMatNo[i].setText(depotStockListItem.getMatNo());
           tvQty[i].setText(depotStockListItem.getQty()+" "+ depotStockListItem.getUOM());

          storage1[i].setText(depotStockListItem.getStorage1());
            storage1id[i].setText(depotStockListItem.getStorage1Id());
           storagedesc1[i].setText(depotStockListItem.getStorage1Desc());

            storage2[i].setText(depotStockListItem.getStorage2());
            storage2id[i].setText(depotStockListItem.getStorage2Id());
           storagedesc2[i].setText(depotStockListItem.getStorage2Desc());

            storage3[i].setText(depotStockListItem.getStorage3());
            storage3id[i].setText(depotStockListItem.getStorage3Id());
            storagedesc3[i].setText(depotStockListItem.getStorage3Desc());

         storage4[i].setText(depotStockListItem.getStorage4());
            storage4id[i].setText(depotStockListItem.getStorage4Id());
           storagedesc4[i].setText(depotStockListItem.getStorage4Desc());

            storage5[i].setText(depotStockListItem.getStorage5());
          storage5id[i].setText(depotStockListItem.getStorage5Id());
            storagedesc5[i].setText(depotStockListItem.getStorage5Desc());


                if (depotStockListItem.getStorage1Desc().toString().equals("")) {

                storage1[i].setVisibility(View.GONE);
               storage1id[i].setVisibility(View.GONE);
                storagedesc1[i].setVisibility(View.GONE);

            }


            if (depotStockListItem.getStorage2Desc().toString().equals("")) {

                storage2[i].setVisibility(View.GONE);
                storage2id[i].setVisibility(View.GONE);
                storagedesc2[i].setVisibility(View.GONE);


            }


            if (depotStockListItem.getStorage3Desc().toString().equals("")) {

               storage3[i].setVisibility(View.GONE);
               storage3id[i].setVisibility(View.GONE);
                storagedesc3[i].setVisibility(View.GONE);


            }


            if (depotStockListItem.getStorage4Desc().toString().equals("")) {

                storage4[i].setVisibility(View.GONE);
                storage4id[i].setVisibility(View.GONE);
                storagedesc4[i].setVisibility(View.GONE);

            }


            if (depotStockListItem.getStorage5Desc().toString().equals("")) {

                storage5[i].setVisibility(View.GONE);
                storage5id[i].setVisibility(View.GONE);
               storagedesc5[i].setVisibility(View.GONE);


            }


                          final View testView = rowRelativeLayout;
            imgexpand[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    if (depotStockListItem.isAddressEnabled()) {
                        depotStockListItem.setAddressEnabled(false);
                        imgexpand[selvalue].setImageResource(R.drawable.down);

                    } else {
                        depotStockListItem.setAddressEnabled(true);
                        imgexpand[selvalue].setImageResource(R.drawable.up);

                    }
                    View toolbarEmptyText = testView.findViewById(R.id.depot_itemList);
                    ExpandAnimation expandemptytext = new ExpandAnimation(toolbarEmptyText, 50);
                    toolbarEmptyText.startAnimation(expandemptytext);


                }
            });

                tableHeading.addView(rowRelativeLayout);
        }







            }

            depot_linerlayout.addView(tableHeading);

        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


//    public class StockListAdapter extends RecyclerView.Adapter<StockListAdapter.ViewHolder> {
//        private ArrayList<DepotStockBean> alSchemeItemList;
//        private Context context;
//        private TextView tvEmptyListLay;
//        View view;
//
//        public StockListAdapter(ArrayList<DepotStockBean> alSchemeItemList, Context context, TextView tvEmptyListLay) {
//            this.alSchemeItemList = alSchemeItemList;
//            this.context = context;
//            this.tvEmptyListLay = tvEmptyListLay;
//
//        }
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//             view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_depot_stock_list, viewGroup, false);
//            return new ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(ViewHolder viewHolder, int i) {
//
//            final DepotStockBean depotStockListItem = alSchemeItemList.get(i);
//
//            viewHolder.tvMatName.setText(depotStockListItem.getMatDesc());
//            viewHolder.tvMatNo.setText(depotStockListItem.getMatNo());
//            viewHolder.tvQty.setText(depotStockListItem.getQty()+" "+ depotStockListItem.getUOM());
//
//            viewHolder.storage1.setText(depotStockListItem.getStorage1());
//            viewHolder.storage1id.setText(depotStockListItem.getStorage1Id());
//            viewHolder.storagedesc1.setText(depotStockListItem.getStorage1Desc());
//
//            viewHolder.storage2.setText(depotStockListItem.getStorage2());
//            viewHolder.storage2id.setText(depotStockListItem.getStorage2Id());
//            viewHolder.storagedesc2.setText(depotStockListItem.getStorage2Desc());
//
//
//
//            viewHolder.storage3.setText(depotStockListItem.getStorage3());
//            viewHolder.storage3id.setText(depotStockListItem.getStorage3Id());
//            viewHolder.storagedesc3.setText(depotStockListItem.getStorage3Desc());
//
//            viewHolder.storage4.setText(depotStockListItem.getStorage4());
//            viewHolder.storage4id.setText(depotStockListItem.getStorage4Id());
//            viewHolder.storagedesc4.setText(depotStockListItem.getStorage4Desc());
//
//            viewHolder.storage5.setText(depotStockListItem.getStorage5());
//            viewHolder.storage5id.setText(depotStockListItem.getStorage5Id());
//            viewHolder.storagedesc5.setText(depotStockListItem.getStorage5Desc());
//
//
//
//            if (depotStockListItem.getStorage1Desc().toString().equals("")) {
//
//                viewHolder.storage1.setVisibility(View.GONE);
//                viewHolder.storage1id.setVisibility(View.GONE);
//                viewHolder.storagedesc1.setVisibility(View.GONE);
//
//            }
//
//
//            if (depotStockListItem.getStorage2Desc().toString().equals("")) {
//
//                viewHolder.storage2.setVisibility(View.GONE);
//                viewHolder.storage2id.setVisibility(View.GONE);
//                viewHolder.storagedesc2.setVisibility(View.GONE);
//
//
//            }
//
//
//            if (depotStockListItem.getStorage3Desc().toString().equals("")) {
//
//                viewHolder.storage3.setVisibility(View.GONE);
//                viewHolder.storage3id.setVisibility(View.GONE);
//                viewHolder.storagedesc3.setVisibility(View.GONE);
//
//
//            }
//
//
//            if (depotStockListItem.getStorage4Desc().toString().equals("")) {
//
//                viewHolder.storage4.setVisibility(View.GONE);
//                viewHolder.storage4id.setVisibility(View.GONE);
//                viewHolder.storagedesc4.setVisibility(View.GONE);
//
//            }
//
//
//            if (depotStockListItem.getStorage5Desc().toString().equals("")) {
//
//                viewHolder.storage5.setVisibility(View.GONE);
//                viewHolder.storage5id.setVisibility(View.GONE);
//                viewHolder.storagedesc5.setVisibility(View.GONE);
//
//
//            }
//
//
//            final View testView = view;
//            viewHolder.imgexpand.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    ImageView img = (ImageView) v;
//
//                    if (depotStockListItem.isAddressEnabled()) {
//                        depotStockListItem.setAddressEnabled(false);
//                        img.setImageResource(R.drawable.down);
//
//                    } else {
//                        depotStockListItem.setAddressEnabled(true);
//                        img.setImageResource(R.drawable.up);
//
//                    }
//                    View toolbarEmptyText = testView.findViewById(R.id.depot_itemList);
//                    ExpandAnimation expandemptytext = new ExpandAnimation(toolbarEmptyText, 50);
//                    toolbarEmptyText.startAnimation(expandemptytext);
//
////                    View toolbar = testView.findViewById(R.id.tv_address2);
////                    ExpandAnimation expandAni = new ExpandAnimation(toolbar, 50);
////                    toolbar.startAnimation(expandAni);
////
////                    View toolbarSpace = testView.findViewById(R.id.tv_down_color);
////                    ExpandAnimation expandAniSpace = new ExpandAnimation(toolbarSpace, 50);
////                    toolbarSpace.startAnimation(expandAniSpace);
//
//                }
//            });
//        }
//
//        @Override
//        public int getItemCount() {
//            if (alSchemeItemList.size() == 0) {
//                tvEmptyListLay.setVisibility(View.VISIBLE);
//            } else {
//                tvEmptyListLay.setVisibility(View.GONE);
//            }
//            return alSchemeItemList.size();
//        }
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//            TextView tvMatName = null;
//            TextView tvMatNo = null;
//            TextView tvQty = null;
//            ImageView imgexpand = null;
//            TextView storage1 = null;
//            TextView storage1id = null;
//            TextView storagedesc1 = null;
//            TextView storage2 = null;
//            TextView storage2id = null;
//            TextView storagedesc2 = null;
//            TextView storage3 = null;
//            TextView storage3id = null;
//            TextView storagedesc3 = null;
//            TextView storage4 = null;
//            TextView storage4id = null;
//            TextView storagedesc4 = null;
//            TextView storage5 = null;
//            TextView storage5id = null;
//            TextView storagedesc5 = null;
//
//
//
//
//            public ViewHolder(View view) {
//                super(view);
//                tvMatName = (TextView) view.findViewById(R.id.tv_mat_name);
//                tvMatNo = (TextView) view.findViewById(R.id.tv_mat_no);
//                tvQty = (TextView) view.findViewById(R.id.tv_qty);
//                imgexpand = (ImageView) view.findViewById(R.id.iv_depotstock_expand_icon);
//                storage1 = (TextView) view.findViewById(R.id.storage1);
//                storage1id = (TextView) view.findViewById(R.id.storage1id);
//                storagedesc1 = (TextView) view.findViewById(R.id.storage1desc);
//                storage2 = (TextView) view.findViewById(R.id.storage2);
//                storage2id = (TextView) view.findViewById(R.id.storage2id);
//                storagedesc2 = (TextView) view.findViewById(R.id.storage2desc);
//                storage3 = (TextView) view.findViewById(R.id.storage3);
//                storage3id = (TextView) view.findViewById(R.id.storage3id);
//                storagedesc3 = (TextView) view.findViewById(R.id.storage3desc);
//                storage4 = (TextView) view.findViewById(R.id.storage4);
//                storage4id = (TextView) view.findViewById(R.id.storage4id);
//                storagedesc4 = (TextView) view.findViewById(R.id.storage4desc);
//                storage5 = (TextView) view.findViewById(R.id.storage5);
//                storage5id = (TextView) view.findViewById(R.id.storage5id);
//                storagedesc5 = (TextView) view.findViewById(R.id.storage5desc);
//
//
//
//
//            }
//        }
//    }

    private  class GetDepotStockList extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(DepotStockActivity.this,R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {

            getDepotStockList();



            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


            getDisplayValueMat(depotStockBeen);
//            depotStockAdapter = new DepotStockAdapter(DepotStockActivity.this, depotStockBeen);
//            lv_depot_stock_list.setEmptyView(findViewById(R.id.tv_empty_lay) );
//            lv_depot_stock_list.setAdapter(depotStockAdapter);
//            depotStockAdapter.notifyDataSetChanged();
           // rvSchemeList.setAdapter(new DepotStockAdapter(depotStockBeen, DepotStockActivity.this, tvEmptyListLay));

        }
    }



    private  class GetSearchedList extends AsyncTask<String ,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(DepotStockActivity.this,R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }
        @Override
        protected Void doInBackground(String... params) {

if(params.length == 0){
    filteredArraylist = new ArrayList<>();
    for (int i = 0; i < depotStockBeen.size(); i++) {
        DepotStockBean item = depotStockBeen.get(i);

        if(item.getMatDesc()!=null && !item.getMatDesc().equalsIgnoreCase("")){
            if (item.getMatDesc().toLowerCase()
                    .contains(params.toString().toLowerCase().trim())) {
                filteredArraylist.add(item);

            }
        }

    }


}else{

    initUI();
}


            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


            getDisplayValueMat(filteredArraylist);
//            depotStockAdapter = new DepotStockAdapter(DepotStockActivity.this, depotStockBeen);
//            lv_depot_stock_list.setEmptyView(findViewById(R.id.tv_empty_lay) );
//            lv_depot_stock_list.setAdapter(depotStockAdapter);
//            depotStockAdapter.notifyDataSetChanged();
            // rvSchemeList.setAdapter(new DepotStockAdapter(depotStockBeen, DepotStockActivity.this, tvEmptyListLay));

        }
    }

}





