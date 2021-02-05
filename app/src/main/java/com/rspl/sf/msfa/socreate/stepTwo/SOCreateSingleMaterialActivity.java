package com.rspl.sf.msfa.socreate.stepTwo;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterViewInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewTypeAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.filterlist.SearchFilterInterface;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.solist.SOListBean;

import java.util.ArrayList;

public class SOCreateSingleMaterialActivity extends AppCompatActivity implements SOCrtStpTwoView, AdapterViewInterface<SOItemBean>, SearchFilterInterface {

    private int comingFrom = 0;
    private SOCrtStpTwoPresenterImpl presenter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private TextView noRecordFound;
    private SimpleRecyclerViewTypeAdapter<SOItemBean> simpleRecyclerViewAdapter;
//    private Spinner spSearch;
//    private EditText etSearch;
    private ArrayList<SOItemBean> searchSOItemBean = new ArrayList<>();
    private String searchStr[] = {"Desc", "Code"};
    private SOListBean soListBeanHeader = null;
    private boolean isSessionRequired = false;
    private SOListBean soDefaultBean = null;
    private ArrayList<SOItemBean> selectedItemList = null;
    private boolean isScrolling = false;
    private Toolbar toolbar;
    private boolean checkAddItem=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socreate_stp_two);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            soListBeanHeader = (SOListBean) bundle.getSerializable(Constants.EXTRA_HEADER_BEAN);
            isSessionRequired = bundle.getBoolean(Constants.EXTRA_SESSION_REQUIRED, false);
            comingFrom = bundle.getInt(Constants.EXTRA_COME_FROM, 0);
            soDefaultBean = (SOListBean) bundle.getSerializable(Constants.EXTRA_SO_HEADER);
            selectedItemList = (ArrayList<SOItemBean>) bundle.getSerializable(Constants.EXTRA_SO_ITEM_LIST);
            checkAddItem = bundle.getBoolean(Constants.CHECK_ADD_MATERIAL_ITEM,false);
            if(comingFrom== ConstantsUtils.ADD_MATERIAL|| comingFrom== ConstantsUtils.SO_EDIT_SINGLE_MATERIAL){
                ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.menu_sos_edit),0);
            }else {
                ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.menu_sos_create),0);
            }
        }
        if (soListBeanHeader == null) {
            soListBeanHeader = new SOListBean();
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        noRecordFound = (TextView) findViewById(R.id.no_record_found);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewTypeAdapter<SOItemBean>(SOCreateSingleMaterialActivity.this, R.layout.so_material_item, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(simpleRecyclerViewAdapter);


        presenter = new SOCrtStpTwoPresenterImpl(checkAddItem,SOCreateSingleMaterialActivity.this, this, comingFrom, isSessionRequired, soDefaultBean, soListBeanHeader, selectedItemList,"");
        presenter.onStart();

    }


    @Override
    public void displayList(ArrayList<SOItemBean> soItemList) {
        refreshAdapter(soItemList);
//        initUI();
    }

    @Override
    public void displaySearchList(ArrayList<SOItemBean> soItemList) {

    }

    private void refreshAdapter(ArrayList<SOItemBean> soItemList) {
        searchSOItemBean.clear();
        searchSOItemBean = soItemList;
        Log.d("SOMaterial", "getSOMaterialList: adding");
        simpleRecyclerViewAdapter.refreshAdapter(soItemList);
        Log.d("SOMaterial", "getSOMaterialList: display");
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = ConstantsUtils.showProgressDialog(SOCreateSingleMaterialActivity.this, message);
    }

    @Override
    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void displayMessage(String message) {
        UtilConstants.dialogBoxWithCallBack(SOCreateSingleMaterialActivity.this, "", message, getString(R.string.ok), "", false, null);
    }

    @Override
    public void displayTotalSelectedMat(int finalSelectedCount) {

    }


    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onItemClick(SOItemBean item, View view, int position) {

    }

    @Override
    public int getItemViewType(int position, ArrayList<SOItemBean> arrayList) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, View viewItem) {
        return new SOMaterialVH(viewItem);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, final SOItemBean soItemBean, final ArrayList<SOItemBean> itemBeanArrayList) {
        isScrolling = true;
        ((SOMaterialVH) holder).checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                soItemBean.setChecked(isChecked);
                if (!isScrolling && isChecked) {
                    unCheckAll(itemBeanArrayList);
                    soItemBean.setChecked(isChecked);
                    simpleRecyclerViewAdapter.notifyDataSetChanged();
                }
                isScrolling = false;
            }
        });
        if (soItemBean.isChecked()) {
            ((SOMaterialVH) holder).checkBox.setChecked(true);
        } else {
            ((SOMaterialVH) holder).checkBox.setChecked(false);
            isScrolling = false;
        }
        ((SOMaterialVH) holder).checkBox.setText(soItemBean.getMatDesc() + " - " + soItemBean.getMatCode());

    }

    private void unCheckAll(ArrayList<SOItemBean> itemBeanArrayList) {
        for (SOItemBean soItemBean : itemBeanArrayList) {
            soItemBean.setChecked(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_so_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_next:
                //next step
                presenter.validateItem(comingFrom,recyclerView);
                break;

        }
        return true;
    }


    @Override
    public boolean applyConditionToAdd(Object o) {
        return false;
    }

    @Override
    public void setFilterDate(String filterType) {

    }
    @Override
    public void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus) {

    }
}