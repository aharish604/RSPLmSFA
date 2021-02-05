package com.rspl.sf.msfa.collectionPlan.collectionCreate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.collectionPlan.WeekDetailsList;
import com.rspl.sf.msfa.collectionPlan.WeekHeaderList;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.CustomerBean;
import com.rspl.sf.msfa.routeplan.customerslist.CustomerPresenter;
import com.rspl.sf.msfa.routeplan.customerslist.ICustomerViewPresenter;

import java.util.ArrayList;

/**
 * Created by e10860 on 2/21/2018.
 *
 */

public class CollectionPlanCreateActivity extends AppCompatActivity implements AdapterInterface<CustomerBean>,ICustomerViewPresenter<CustomerBean> {

    private TextInputLayout tiCollectionAmount;
    private TextInputLayout tiCollecRemarks;
    private EditText edtCollectionAmount;
    private EditText edtCollecRemarks;
    public static final int CUSTOMER_CATEGROIZE_REQUEST = 205;
    ArrayList<WeekDetailsList> alDistList = new ArrayList<>();
    RecyclerView recyclerView;
    TextView no_record_found;
    SimpleRecyclerViewAdapter<CustomerBean> recyclerViewAdapter = null;
    LinearLayout linearLayoutFlowLayout;
    private CustomerPresenter presenter;
    private WeekHeaderList mtpHeaderBean=null;
    private WeekHeaderList mtpResultHeaderBean=null;
    private int listPos= -1;
    private String comingFrom="",ExternalRefID="";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_plan_create);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mtpHeaderBean = (WeekHeaderList) bundle.getSerializable(Constants.EXTRA_BEAN);
            listPos = bundle.getInt(ConstantsUtils.EXTRA_POS, 0);
            comingFrom = bundle.getString(Constants.comingFrom,"");
            ExternalRefID = bundle.getString(ConstantsUtils.EXTRA_ExternalRefID,"");
        }
        if (mtpHeaderBean == null) {
            mtpHeaderBean = new WeekHeaderList();
        }
        mtpResultHeaderBean = mtpHeaderBean;
        init();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this,toolbar,true,getString(R.string.ll_collec_create),0);
//        tiCollecRemarks = (TextInputLayout) findViewById(R.id.tiCollecRemarks);
//        tiCollectionAmount = (TextInputLayout) findViewById(R.id.tiCollectionAmount);
//        edtCollecRemarks = (EditText) findViewById(R.id.edtCollecRemarks);
//        edtCollectionAmount = (EditText) findViewById(R.id.edtCollectionAmount);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        no_record_found = (TextView) findViewById(R.id.no_record_found);
        linearLayoutFlowLayout = (LinearLayout) findViewById(R.id.llFilterLayout);
        initializeRecyclerViewItems(new LinearLayoutManager(this));
        presenter = new CustomerPresenter(CollectionPlanCreateActivity.this, this, this, "", "","");
        presenter.loadRTGSList(mtpHeaderBean.getWeekDetailsLists());

    }

    @Override
    public void initializeUI(Context context) {

    }

    @Override
    public void initializeClickListeners() {

    }

    @Override
    public void initializeObjects(Context context) {

    }

    public void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new SimpleRecyclerViewAdapter<>(this, R.layout.item_coll_plan_create, this, recyclerView, no_record_found);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void showProgressDialog() {
    }

    @Override
    public void hideProgressDialog() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_rtgs_add, menu);
//        if(comingFrom.equalsIgnoreCase(ConstantsUtils.RTGS_SUBORDINATE_CURRENT) || comingFrom.equalsIgnoreCase(ConstantsUtils.RTGS_SUBORDINATE_NEXT)){
//            menu.removeItem(R.id.menu_add_rtgs);
//        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_add_rtgs:

                onNavigateToCustomers();

                //next step
//                presenter.validateItem(comingFrom, recyclerView);
                return true;
            case R.id.menu_apply_rtgs:
                    if(checkValidation()) {
                        presenter.sendResultRTGS(mtpResultHeaderBean, mtpHeaderBean, selectedItemList);
                    }
                return true;
            default:
                return super.onOptionsItemSelected(item);


        }

    }

    private boolean checkValidation() {
        if (selectedItemList.isEmpty()) {
            displayMsg(getString(R.string.error_sel_atleast_one_cust));
            return false;
        }else{
            int childCount = selectedItemList.size();
            int initialCount = 0;
//            boolean allFieldEntered = false;
//            boolean allValidFieldEntered = false;
//            boolean enteredAmt = false;
//            boolean isRemarks = false;
            for (int i = 0; i < childCount; i++) {
                CustomerBean customerBean = selectedItemList.get(i);
                RTGSCreateVH childHolder = (RTGSCreateVH) recyclerView.findViewHolderForLayoutPosition(i);
                if(childHolder!=null){
                    String mStrAmt = childHolder.edtCollectionAmount.getText().toString();
                    String mStrAmt1 = childHolder.edtCollectionAmount1.getText().toString();
                    if (mStrAmt.isEmpty() && mStrAmt1.isEmpty()) {
//                        allValidFieldEntered = true;
                        if (mStrAmt.isEmpty()) {
                            Constants.errorEditText(childHolder.tiCollectionAmount, getString(R.string.amount_errror));
                        }
                        if (mStrAmt1.isEmpty()){
                            Constants.errorEditText(childHolder.tiCollectionAmount1, getString(R.string.amount_errror));
                        }
                    } else {
//                        allFieldEntered = true;

                        if(!TextUtils.isEmpty(mStrAmt)){
                            int amount=Integer.parseInt(mStrAmt);
                            if(amount==0){
                                Constants.errorEditText(childHolder.tiCollectionAmount, "Enter vaild amount");
                                return false;
                            }

                        }

                        childHolder.tiCollectionAmount.setErrorEnabled(false);


                        if(!TextUtils.isEmpty(mStrAmt1)){
                            int amount1 =Integer.parseInt(mStrAmt1);
                            if(amount1==0){
                                Constants.errorEditText(childHolder.tiCollectionAmount1, "Enter vaild amount");
                                return false;
                            }

                        }

                        childHolder.tiCollectionAmount1.setErrorEnabled(false);

                        /*try {
                            childHolder.tiCollectionAmount.setErrorEnabled(false);
                            childHolder.tiCollectionAmount1.setErrorEnabled(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                        try {
                            double doubAmt = 0;
                            double doubAmt1 = 0;
                            try {
                                doubAmt = Double.parseDouble(customerBean.getAmount());
                            } catch (NumberFormatException e) {
                                doubAmt = 0;
                                e.printStackTrace();
                            }
                            try {
                                doubAmt1 = Double.parseDouble(customerBean.getAmount1());
                            } catch (NumberFormatException e) {
                                doubAmt1 = 0;
                                e.printStackTrace();
                            }
                            if (doubAmt > 0 || doubAmt1>0) {
                                initialCount++;
                            } else {
//                                enteredAmt = true;
                                Constants.errorEditText(childHolder.tiCollectionAmount, getString(R.string.amount_errror));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }else{
                    try {
                        double doubAmt = 0;
                        double doubAmt1 = 0;
                        try {
                            doubAmt = Double.parseDouble(customerBean.getAmount());
                        } catch (NumberFormatException e) {
                            doubAmt = 0;
                            e.printStackTrace();
                        }

                        try {
                            doubAmt1 = Double.parseDouble(customerBean.getAmount1());
                        } catch (NumberFormatException e) {
                            doubAmt1 = 0;
                            e.printStackTrace();
                        }

                        if (doubAmt > 0 || doubAmt1 > 0) {
                            initialCount++;
                        } else {
//                                enteredAmt = true;
//                            Constants.errorEditText(childHolder.tiCollectionAmount, getString(R.string.amount_errror));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (initialCount == selectedItemList.size()) {
                return true;
            }
                displayMsg(getString(R.string.validation_plz_enter_mandatory_flds));
        }
        return false;
    }
    private void onNavigateToCustomers(){
        mtpHeaderBean = CustomerPresenter.setCustomerToBean(selectedItemList,mtpHeaderBean);
        Intent custIntent = new Intent(this, CustomerSelectionActivity.class);
        custIntent.putExtra(Constants.EXTRA_BEAN, mtpHeaderBean);
        custIntent.putExtra(ConstantsUtils.EXTRA_ExternalRefID, ExternalRefID);
        startActivityForResult(custIntent, CUSTOMER_CATEGROIZE_REQUEST);
    }

    ArrayList<CustomerBean> selectedItemList=new ArrayList<>();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == CUSTOMER_CATEGROIZE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == 205) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    selectedItemList.clear();
                    selectedItemList.addAll(CustomerSelectionActivity.customerSelectedListTemp);
//                    selectedItemList = (ArrayList<CustomerBean>) bundle.getSerializable(Constants.EXTRA_SO_ITEM_LIST);
                    mtpHeaderBean = CustomerPresenter.setCustomerToBean(selectedItemList,mtpHeaderBean);
                    onRefreshData();
                }

            }
        }
    }
    public void onRefreshData() {
        if(selectedItemList!=null) {
            recyclerViewAdapter.refreshAdapter(selectedItemList);
        }
    }

    @Override
    public void customersListSync() {

    }

    @Override
    public void openFilter(String filterType, String status, String grStatus) {

    }

    @Override
    public void searchResult(ArrayList<CustomerBean> retailerSearchList) {
        if(retailerSearchList!=null && retailerSearchList.size()>0) {
            if(selectedItemList!=null){
                selectedItemList.addAll(retailerSearchList);
            }
            recyclerViewAdapter.refreshAdapter(retailerSearchList);
        }

    }

    @Override
    public void setFilterDate(String filterType) {

    }

    @Override
    public void displayRefreshTime(String refreshTime) {

    }

    @Override
    public void displayMsg(String msg) {
        ConstantsUtils.displayLongToast(CollectionPlanCreateActivity.this, msg);
    }

    @Override
    public void sendSelectedItem(Intent intent) {
        intent.putExtra(ConstantsUtils.EXTRA_POS, listPos);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void errorMsgEditText(String error) {
        try {
            int childCount = selectedItemList.size();
            for (int i = 0; i < childCount; i++) {
                RTGSCreateVH childHolder = (RTGSCreateVH) recyclerView.findViewHolderForLayoutPosition(i);
                if(childHolder!=null) {
                    String mStrAmt = childHolder.edtCollectionAmount.getText().toString();
                    if(!TextUtils.isEmpty(mStrAmt)){
                        childHolder.tiCollectionAmount.setErrorEnabled(false);
                    }else {
                        Constants.errorEditText(childHolder.tiCollectionAmount, getString(R.string.amount_errror));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void errorMsgEditText1(String error) {
        try {
            int childCount = selectedItemList.size();
            for (int i = 0; i < childCount; i++) {
                RTGSCreateVH childHolder = (RTGSCreateVH) recyclerView.findViewHolderForLayoutPosition(i);
                if(childHolder!=null) {
                    String mStrAmt1 = childHolder.edtCollectionAmount1.getText().toString();
                    if (!TextUtils.isEmpty(mStrAmt1)) {
                        childHolder.tiCollectionAmount1.setErrorEnabled(false);
                    } else {
                        Constants.errorEditText(childHolder.tiCollectionAmount1, getString(R.string.amount_errror));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(CustomerBean customerBean, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new RTGSCreateVH(view,new RTGSTextWatcher(),new RTGSRemarksWatcher());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, CustomerBean customerBean) {
        ((RTGSCreateVH) viewHolder).tvPlanNameDesc.setText(customerBean.getCustomerName());
        ((RTGSCreateVH) viewHolder).rtgsTextWatcher.updateTextWatcher(customerBean, viewHolder,((RTGSCreateVH) viewHolder).edtCollectionAmount,((RTGSCreateVH) viewHolder).edtCollectionRemarks,((RTGSCreateVH) viewHolder).edtCollectionAmount1,((RTGSCreateVH) viewHolder).edtCollectionRemarks1);
        String collAmount = "";
        String collAmount1 = "";
        if(customerBean.getAmount()!=null && !customerBean.getAmount().equalsIgnoreCase("")){
            collAmount = ConstantsUtils.removeLeadingZero(customerBean.getAmount());

        }else{
            collAmount = "";
        }

        if(customerBean.getAmount1()!=null && !customerBean.getAmount1().equalsIgnoreCase("")){
            collAmount1 = ConstantsUtils.removeLeadingZero(customerBean.getAmount1());

        }else{
            collAmount1 = "";
        }
       /* ArrayList<String> salesArea = null;
        try {
            salesArea = OfflineManager.getSaleAreaFromUsrAth("UserProfileAuthSet?$filter=Application%20eq%20%27PD%27"+" &$orderby=AuthOrgTypeID asc");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String stringSaleArea = "";
        if(salesArea!=null && !salesArea.isEmpty()) {
            for (int j = 0; j < salesArea.size();j++){
                if(j==salesArea.size()-1) {
                    stringSaleArea = stringSaleArea + "SalesArea eq '" + salesArea.get(j)+"'";
                }else {
                    stringSaleArea = stringSaleArea + "SalesArea eq '" + salesArea.get(j) + "' or ";
                }
            }
        }
        ArrayList<SaleAreaBean> saleAreaBeanArrayList = null;
        if(!TextUtils.isEmpty(stringSaleArea)){
            String qry = Constants.CustomerSalesAreas + "?$filter=" + Constants.CustomerNo + " eq '" + customerBean.getCustomerId() + "' and (" + stringSaleArea+")";
            saleAreaBeanArrayList= (OfflineManager.getSaleAreaFromCustomerCreditLmt(qry));
        }*/
        ArrayList<SaleAreaBean> saleAreaBeanArrayList = null;
        saleAreaBeanArrayList = customerBean.getSaleAreaBeanAl();

        try {
            ((RTGSCreateVH) viewHolder).tvActualNameDesc.setText(ConstantsUtils.commaSeparator(customerBean.getActualAmount(),customerBean.getCurrency())+" "+customerBean.getCurrency());
        } catch (Exception e) {
            e.printStackTrace();
        }
        double mDouAchValue = 0.0;
        try {
            mDouAchValue = Double.parseDouble(customerBean.getActualAmount());
        } catch (NumberFormatException e) {
            mDouAchValue = 0.00;
            e.printStackTrace();
        }

        if(saleAreaBeanArrayList!=null && !saleAreaBeanArrayList.isEmpty()){
            if (saleAreaBeanArrayList.size() == 1) {
                if(saleAreaBeanArrayList.get(0).getCreditControlAreaID().equalsIgnoreCase("1010")) {
                    if(!collAmount.equalsIgnoreCase("0")) {
                        ((RTGSCreateVH) viewHolder).edtCollectionAmount.setText(collAmount);
                    }else {
                        ((RTGSCreateVH) viewHolder).edtCollectionAmount.setText("");
                    }


                    ((RTGSCreateVH) viewHolder).edtCollectionRemarks.setText(customerBean.getRemarks());


                   // ((RTGSCreateVH) viewHolder).tiCollectionAmount.setHint("Amount(" + saleAreaBeanArrayList.get(0).getCreditControlAreaDesc() + ")*");
                    ((RTGSCreateVH) viewHolder).tiCollectionAmount.setHint("Amount *");
                    ((RTGSCreateVH) viewHolder).ccCollectionAmount.setVisibility(View.VISIBLE);
                    ((RTGSCreateVH) viewHolder).ccCollectionRemarks.setVisibility(View.VISIBLE);
                    ((RTGSCreateVH) viewHolder).ccCollectionAmount1.setVisibility(View.GONE);
                    ((RTGSCreateVH) viewHolder).ccCollectionRemarks1.setVisibility(View.GONE);
                }else if(saleAreaBeanArrayList.get(0).getCreditControlAreaID().equalsIgnoreCase("1030")) {
                   // ((RTGSCreateVH) viewHolder).tiCollectionAmount1.setHint("Amount(" + saleAreaBeanArrayList.get(0).getCreditControlAreaDesc() + ")*");
                    ((RTGSCreateVH) viewHolder).tiCollectionAmount1.setHint("Amount *");
                    ((RTGSCreateVH) viewHolder).edtCollectionRemarks1.setText(customerBean.getRemarks1());
                    if(!collAmount1.equalsIgnoreCase("0")) {
                        ((RTGSCreateVH) viewHolder).edtCollectionAmount1.setText(collAmount1);
                    }else {
                        ((RTGSCreateVH) viewHolder).edtCollectionAmount1.setText("");
                    }
                    ((RTGSCreateVH) viewHolder).ccCollectionAmount1.setVisibility(View.VISIBLE);
                    ((RTGSCreateVH) viewHolder).ccCollectionRemarks1.setVisibility(View.VISIBLE);
                    ((RTGSCreateVH) viewHolder).ccCollectionAmount.setVisibility(View.GONE);
                    ((RTGSCreateVH) viewHolder).ccCollectionRemarks.setVisibility(View.GONE);
                }
            } else if (saleAreaBeanArrayList.size() == 2) {
                if(saleAreaBeanArrayList.get(0).getCreditControlAreaID().equalsIgnoreCase("1010")) {
                   // ((RTGSCreateVH) viewHolder).tiCollectionAmount.setHint("Amount("+saleAreaBeanArrayList.get(0).getCreditControlAreaDesc()+")*");
                    ((RTGSCreateVH) viewHolder).tiCollectionAmount.setHint("Amount *");
                    if(!collAmount.equalsIgnoreCase("0")) {
                        ((RTGSCreateVH) viewHolder).edtCollectionAmount.setText(collAmount);
                    }else {
                        ((RTGSCreateVH) viewHolder).edtCollectionAmount.setText("");
                    }


                    ((RTGSCreateVH) viewHolder).edtCollectionRemarks.setText(customerBean.getRemarks());
                }

                if(saleAreaBeanArrayList.get(1).getCreditControlAreaID().equalsIgnoreCase("1030")) {
                 //   ((RTGSCreateVH) viewHolder).tiCollectionAmount1.setHint("Amount(" + saleAreaBeanArrayList.get(1).getCreditControlAreaDesc() + ")*");
                    ((RTGSCreateVH) viewHolder).tiCollectionAmount1.setHint("Amount *");
                    if(!collAmount1.equalsIgnoreCase("0")) {
                        ((RTGSCreateVH) viewHolder).edtCollectionAmount1.setText(collAmount1);
                    }else {
                        ((RTGSCreateVH) viewHolder).edtCollectionAmount1.setText("");
                    }


                    ((RTGSCreateVH) viewHolder).edtCollectionRemarks1.setText(customerBean.getRemarks1());
                }
                ((RTGSCreateVH) viewHolder).ccCollectionAmount.setVisibility(View.VISIBLE);
                ((RTGSCreateVH) viewHolder).ccCollectionRemarks.setVisibility(View.VISIBLE);
                /* Harish did  for RTGS Plan Chnages */
                ((RTGSCreateVH) viewHolder).ccCollectionAmount1.setVisibility(View.GONE);
                ((RTGSCreateVH) viewHolder).ccCollectionRemarks1.setVisibility(View.GONE);
            } else {
                ((RTGSCreateVH) viewHolder).ccCollectionAmount.setVisibility(View.GONE);
                ((RTGSCreateVH) viewHolder).ccCollectionRemarks1.setVisibility(View.GONE);
                ((RTGSCreateVH) viewHolder).ccCollectionRemarks.setVisibility(View.GONE);
                ((RTGSCreateVH) viewHolder).ccCollectionAmount1.setVisibility(View.GONE);
            }
        }else {
            ((RTGSCreateVH) viewHolder).ccCollectionAmount.setVisibility(View.GONE);
            ((RTGSCreateVH) viewHolder).ccCollectionRemarks1.setVisibility(View.GONE);
            ((RTGSCreateVH) viewHolder).ccCollectionRemarks.setVisibility(View.GONE);
            ((RTGSCreateVH) viewHolder).ccCollectionAmount1.setVisibility(View.GONE);
        }

        if(mDouAchValue>0){
            ((RTGSCreateVH) viewHolder).ll_achivedline.setVisibility(View.VISIBLE);
            ((RTGSCreateVH) viewHolder).cl_achivedValue.setVisibility(View.VISIBLE);
        }else{
            ((RTGSCreateVH) viewHolder).ll_achivedline.setVisibility(View.GONE);
            ((RTGSCreateVH) viewHolder).cl_achivedValue.setVisibility(View.GONE);
        }
        try {
            ((RTGSCreateVH) viewHolder).tiCollectionAmount.setErrorEnabled(false);
            ((RTGSCreateVH) viewHolder).tiCollectionAmount1.setErrorEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        UtilConstants.editTextDecimalFormat(((RTGSCreateVH) viewHolder).edtCollectionAmount, 13, 2);

/*        String collAmount = "";
        String collAmount1 = "";
        if(customerBean.getAmount()!=null && !customerBean.getAmount().equalsIgnoreCase("")){
            collAmount = ConstantsUtils.removeLeadingZero(customerBean.getAmount());

        }else{
            collAmount = "";
        }

        if(customerBean.getAmount1()!=null && !customerBean.getAmount1().equalsIgnoreCase("")){
            collAmount1 = ConstantsUtils.removeLeadingZero(customerBean.getAmount1());

        }else{
            collAmount1 = "";
        }*/

        /*((RTGSCreateVH) viewHolder).edtCollectionAmount.setText(collAmount);

        ((RTGSCreateVH) viewHolder).edtCollectionAmount1.setText(collAmount1);
        ((RTGSCreateVH) viewHolder).edtCollectionRemarks.setText(customerBean.getRemarks());

        ((RTGSCreateVH) viewHolder).edtCollectionRemarks1.setText(customerBean.getRemarks1());*/

//        ((RTGSCreateVH) viewHolder).rtgsTextWatcher.updateTextWatcher(customerBean, viewHolder);
//        ((RTGSCreateVH) viewHolder).rtgsRemarksWatcher.updateTextWatcher(customerBean, viewHolder);
    }
}
