package com.rspl.sf.msfa.dealerstock.stockmaterial;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.dealerstock.DealerStockBean;
import com.rspl.sf.msfa.dealerstock.StockCreateStpTwoActivity;
import com.rspl.sf.msfa.mbo.StocksInfoBean;

import java.util.ArrayList;

/**
 * Created by e10860 on 4/20/2018.
 */

public class DealerStockAddMaterialActivity extends AppCompatActivity implements IDealerStockMaterialViewPresenter,AdapterInterface<StocksInfoBean> {
    // android components
    RecyclerView recyclerView;
    ProgressDialog progressDialog=null;
    TextView textViewNoRecordFound;
    Toolbar toolbar;
    SearchView mSearchView;
    // variables
    ArrayList<StocksInfoBean> materialArrayList,selectedArrayList;
    ArrayList<DealerStockBean> dealerStockBeanArrayList=null;
    SimpleRecyclerViewAdapter<StocksInfoBean> recyclerViewAdapter;
    DealerStockMaterialPresenter presenter;
    int position =0;
    DealerStockBean dealerStockBean;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_stock);
        initializeUI();
    }

    @Override
    public void initializeUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.dealer_stock_title), 0);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(getString(R.string.dealer_stock_sub_mat_sel));
        Intent intent = getIntent();
        if (intent!=null) {
            dealerStockBeanArrayList = (ArrayList<DealerStockBean>) intent.getSerializableExtra(Constants.INTENT_EXTRA_DEALER_STOCK_BEAN);
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        textViewNoRecordFound = (TextView) findViewById(R.id.no_record_found);
        initializeClickListeners();
        initializeRecyclerViewAdapter(new LinearLayoutManager(this));
        initializeObjects();
    }

    @Override
    public void initializeClickListeners() {

    }

    @Override
    public void initializeObjects() {
        try {
            materialArrayList = new ArrayList<>();
            presenter = new DealerStockMaterialPresenter(this,this,this,dealerStockBeanArrayList);
            presenter.loadMaterialData();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initializeRecyclerViewAdapter(LinearLayoutManager layoutManager) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewAdapter = new SimpleRecyclerViewAdapter<>(this,R.layout.recycler_view_dealer_material_stock,this,recyclerView,textViewNoRecordFound);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void showProgressDialog() {
//        progressDialog = ConstantsUtils.showProgressDialog(DealerStockAddMaterialActivity.this,getString(R.string.please_wait));
    }

    @Override
    public void hideProgressDialog() {
//        if (progressDialog!=null) {
//            progressDialog.dismiss();
//        }
    }

    @Override
    public void showMessage(String message, int status) {
        ConstantsUtils.displayLongToast(DealerStockAddMaterialActivity.this,message);
    }

    @SuppressWarnings("all")
    @Override
    public void refreshAdapter(ArrayList<?> arrayList) {
        materialArrayList = (ArrayList<StocksInfoBean>) arrayList;
       /* for (int j = 0; j <dealerStockBeanArrayList.size() ; j++) {
            DealerStockBean dealerStockBean = dealerStockBeanArrayList.get(j);
            for (int i = 0; i <materialArrayList.size() ; i++) {
                StocksInfoBean stocksInfoBean =materialArrayList.get(i);
                if (dealerStockBean.getMaterialNo().equalsIgnoreCase(materialArrayList.get(i).getMaterialNo())){
                    materialArrayList.remove(stocksInfoBean);
                }
            }

        }*/
        recyclerViewAdapter.refreshAdapter((ArrayList<StocksInfoBean>) materialArrayList);
    }
    @SuppressWarnings("all")
    @Override
    public void loadIntentData(Intent intent) {


    }

    @Override
    public void searchResult(ArrayList<StocksInfoBean> searchBeanArrayList) {
        recyclerViewAdapter.refreshAdapter(searchBeanArrayList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(StocksInfoBean dealerStockBean, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new DealerMaterialStockViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, final StocksInfoBean stocksInfoBean) {
        this.position = position;
        selectedArrayList = new ArrayList<>();
        ((DealerMaterialStockViewHolder)viewHolder).checkBoxMaterial.setText(stocksInfoBean.getMaterialDesc()+" ("+stocksInfoBean.getMaterialNo()+")");
//        ((DealerMaterialStockViewHolder)viewHolder).checkBoxMaterial.setChecked(stocksInfoBean.getChecked());
        ((DealerMaterialStockViewHolder)viewHolder).checkBoxMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dealerStockBean  = new DealerStockBean();
                if (isChecked){
                    stocksInfoBean.setChecked(true);
                    selectedArrayList.add(stocksInfoBean);
                }else{
                    stocksInfoBean.setChecked(false);
                    selectedArrayList.remove(stocksInfoBean);
                }
            }
        });

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dlr_stock_add, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        View view = mSearchView.findViewById(androidx.appcompat.R.id.search_plate);
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.transperant));
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getComponentName());
        mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.so_mat_search_hint));


        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                simpleRecyclerViewAdapter.searchFilter(searchSOItemBean, StockCreateStpTwoActivity.this);
                presenter.onSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                simpleRecyclerViewAdapter.searchFilter(searchSOItemBean, StockCreateStpTwoActivity.this);
                presenter.onSearch(newText);
                return false;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.apply:
                if (!selectedArrayList.isEmpty()) {
                    setResult(StockCreateStpTwoActivity.INTENT_RESULT_STOCK_CREATE,new Intent().putExtra(Constants.INTENT_EXTRA_MATERIAL_LIST,selectedArrayList));
                    finish();
                }else{
                    Toast.makeText(this, "please select material", Toast.LENGTH_SHORT).show();
                }
                break;
            case android.R.id.home:
                finish();
            case R.id.menu_search_item:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
