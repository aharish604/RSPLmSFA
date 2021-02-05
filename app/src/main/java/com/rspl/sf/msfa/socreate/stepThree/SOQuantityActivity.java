package com.rspl.sf.msfa.socreate.stepThree;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.so.SOUtils;
import com.rspl.sf.msfa.soDetails.SODetailsActivity;
import com.rspl.sf.msfa.socreate.CreditAmountInterface;
import com.rspl.sf.msfa.socreate.CreditLimitBean;
import com.rspl.sf.msfa.socreate.SOConditionItemDetaiBean;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.socreate.SOSubItemBean;
import com.rspl.sf.msfa.socreate.stepTwo.SOCreateSingleMaterialActivity;
import com.rspl.sf.msfa.socreate.stepTwo.SOCreateStpTwoActivity;
import com.rspl.sf.msfa.solist.SOListBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataEntitySet;
import com.sap.smp.client.odata.ODataNavigationProperty;
import com.sap.smp.client.odata.ODataPayload;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;
import com.sap.smp.client.odata.store.ODataResponseSingle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class SOQuantityActivity extends AppCompatActivity implements UIListener, TextWatcherInterface, SubTextWatcherInterface, DatePickerInterface, DatePickerDialog.OnDateSetListener, SubItemOnClickInterface, OnAddScheduleInterface, OnFocusChangeListener, CreditAmountInterface, OnlineODataInterface {
    private ArrayList<SOItemBean> itemList = null;
    private SOQuantityAdapter soQuantityAdapter;
    private ArrayList<SOItemBean> itemsList = new ArrayList<>();

    private ProgressDialog progressDialog = null;
    private RecyclerView recyclerView;
    private SOSubItemBean soSubItem = null;
    private DatePickerDialog datePickerDialog;
    private int DATE_DIALOG_ID = 0;
    private TextView tvDialogDate = null;
    private boolean isDialogBoxShown = false;
    private String mStrCustomerName = "";
    private String mStrCustomerNo = "";
    private int mComeFrom = 0;
    private SOListBean soListBeanHeader = null;
    private ArrayList<SOItemBean> soDefaultItemBeanList = null;
    private int itemNo = 0;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soquantity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            itemList = (ArrayList<SOItemBean>) extra.getSerializable(Constants.EXTRA_SO_ITEM_LIST);
            soListBeanHeader = (SOListBean) extra.getSerializable(Constants.EXTRA_HEADER_BEAN);
            mStrCustomerName = extra.getString(Constants.CustomerName);
            mStrCustomerNo = extra.getString(Constants.CustomerNo);
            mComeFrom = extra.getInt(Constants.EXTRA_COME_FROM, 0);
            soDefaultItemBeanList = (ArrayList<SOItemBean>) extra.getSerializable(Constants.EXTRA_SO_HEADER);
        }

        String title = "";
        if (mComeFrom == 2) {
            title = getString(R.string.menu_sos_create_cc);
        }  else   if(mComeFrom==3 || mComeFrom== ConstantsUtils.SO_EDIT_SINGLE_MATERIAL){
            title = getString(R.string.menu_sos_edit);
        }else {
            title = getString(R.string.menu_sos_create);
        }
        ConstantsUtils.initActionBarView(this, toolbar, true, title,0);
        TextView tvCustName = (TextView) findViewById(R.id.tv_header_title);
        TextView tvCustNo = (TextView) findViewById(R.id.tv_header_id);
        tvCustName.setText(mStrCustomerName);
        tvCustNo.setText(mStrCustomerNo);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);


        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        datePickerDialog = new DatePickerDialog(SOQuantityActivity.this, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        soQuantityAdapter = new SOQuantityAdapter(SOQuantityActivity.this, itemsList, SOQuantityActivity.this, SOQuantityActivity.this, SOQuantityActivity.this, SOQuantityActivity.this, SOQuantityActivity.this, SOQuantityActivity.this, mComeFrom);
        recyclerView.setAdapter(soQuantityAdapter);

        displayItem();

    }

    private void displayItem() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (itemList != null) {
                    for (SOItemBean soItemBean : itemList) {
                        if (soDefaultItemBeanList != null) {
                            for (SOItemBean soDefaultItemBean : soDefaultItemBeanList) {
                                if (soDefaultItemBean.getMatCode().equals(soItemBean.getMatCode())) {
                                    soItemBean.setSoQty(soDefaultItemBean.getSoQty());
                                    soItemBean.setSoSubItemBeen(soDefaultItemBean.getSoSubItemBeen());
                                    break;
                                }
                            }
                        }
                        soItemBean.setItemNo(ConstantsUtils.addZeroBeforeValue(itemNo + 1, ConstantsUtils.ITEM_MAX_LENGTH));
                        itemsList.add(soItemBean);
                        itemNo++;
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        soQuantityAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onRequestError(int i, Exception e) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        ErrorBean errorBean = Constants.getErrorCode(i, e, SOQuantityActivity.this);
        if (errorBean.hasNoError()) {
            UtilConstants.dialogBoxWithCallBack(SOQuantityActivity.this, "", getString(R.string.so_simulate_failed), getString(R.string.ok), "", false, null);
        } else {
            Constants.displayMsgReqError(errorBean.getErrorCode(), this);
        }
    }

    @Override
    public void onRequestSuccess(int opp, String s) throws ODataException, OfflineODataStoreException {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
       /* if (mComeFrom == 2) {
            openNextActivity(true);
        } else {
            getCreditAmountDataFromOnline();

        }*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_material, menu);
        MenuItem addMat = menu.findItem(R.id.addMaterial);
        if (mComeFrom == ConstantsUtils.SO_CREATE_SINGLE_MATERIAL) {
            addMat.setVisible(false);
        } else {
            addMat.setVisible(true);
        }
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
                onNext();
                break;
            case R.id.addMaterial:
                //next step
                addMaterial();
                break;

        }
        return true;
    }

    private void addMaterial() {
        Intent intent=null;
        if(mComeFrom== ConstantsUtils.SO_EDIT_SINGLE_MATERIAL) {
             intent = new Intent(SOQuantityActivity.this, SOCreateSingleMaterialActivity.class);
            intent.putExtra(Constants.CHECK_ADD_MATERIAL_ITEM,true);
        }else {
            intent = new Intent(SOQuantityActivity.this, SOCreateStpTwoActivity.class);
            intent.putExtra(Constants.CHECK_ADD_MATERIAL_ITEM,false);
        }
        intent.putExtra(Constants.EXTRA_HEADER_BEAN, soListBeanHeader);
        intent.putExtra(Constants.EXTRA_COME_FROM, ConstantsUtils.ADD_MATERIAL);
        intent.putExtra(Constants.EXTRA_SO_ITEM_LIST, itemsList);
        startActivityForResult(intent, ConstantsUtils.ACTIVITY_RESULT_MATERIAL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantsUtils.ACTIVITY_RESULT_MATERIAL) {
            if (resultCode == ConstantsUtils.ACTIVITY_RESULT_MATERIAL) {
                Bundle bundle = data.getExtras();
                if (bundle != null)
                    if(bundle.getBoolean(Constants.CHECK_ADD_MATERIAL_ITEM,false)){
                        itemsList.clear();
                       // itemsList.addAll((ArrayList<SOItemBean>) bundle.getSerializable(Constants.EXTRA_SO_ITEM_LIST));
                        itemNo=0;
                        for (SOItemBean soItemBean : (ArrayList<SOItemBean>) bundle.getSerializable(Constants.EXTRA_SO_ITEM_LIST)) {
                            soItemBean.setItemNo(ConstantsUtils.addZeroBeforeValue(itemNo + 1, ConstantsUtils.ITEM_MAX_LENGTH));
                            itemsList.add(soItemBean);
                        }
                        soQuantityAdapter.notifyDataSetChanged();
                }else {
                        itemList = (ArrayList<SOItemBean>) bundle.getSerializable(Constants.EXTRA_SO_ITEM_LIST);
                        soDefaultItemBeanList = null;
                        displayItem();
                    }

            }
        }

    }

    /*get simulate value*/
    private void getSimulateValue() {
        if (UtilConstants.isNetworkAvailable(SOQuantityActivity.this)) {
            Bundle bundle = new Bundle();
            progressDialog = ConstantsUtils.showProgressDialog(SOQuantityActivity.this);
           /* new SOSimulateAsyncTask(SOQuantityActivity.this, soListBeanHeader, new UIListener() {
                @Override
                public void onRequestError(int i, Exception e) {

                }

                @Override
                public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {

                }
            }, bundle).execute();*/
        } else {
            UtilConstants.dialogBoxWithCallBack(SOQuantityActivity.this, "", getString(R.string.no_network_conn), "Ok", "", false, null);
        }
    }

    /*open next activity*/
    private void openNextActivity() {
//        SOListBean soListBean = convertHasToBean(headerDetail);
        if(mComeFrom== ConstantsUtils.SO_EDIT_SINGLE_MATERIAL){
            mComeFrom= ConstantsUtils.SO_EDIT_ACTIVITY;
        }
        Intent intent = new Intent(this, SODetailsActivity.class);
        intent.putExtra(Constants.EXTRA_SO_HEADER, soListBeanHeader);
        intent.putExtra(Constants.CustomerName, mStrCustomerName);
        intent.putExtra(Constants.CustomerNo, mStrCustomerNo);
        intent.putExtra(Constants.EXTRA_COME_FROM, mComeFrom);
        intent.putExtra(Constants.EXTRA_SO_CREATE_TITLE, false);
        startActivity(intent);
    }

    /*convert hashmap to bean*/
   /* private SOListBean convertHasToBean(HashMap<String, String> headerDetail) {
        SOListBean soListBean = new SOListBean();
        if (headerDetail != null) {
            soListBean.setSoldTo(headerDetail.get("SoldTo"));
            soListBean.setSoldToName(headerDetail.get("SoldToName"));
            soListBean.setShipToName(headerDetail.get("ShipToName"));
            soListBean.setShipTo(headerDetail.get("ShipTo"));
            soListBean.setSalesArea(headerDetail.get("SalesArea"));
            soListBean.setSalesAreaDesc(headerDetail.get("SalesAreaDesc"));
            soListBean.setPlant(headerDetail.get("Plant"));
            soListBean.setPlantDesc(headerDetail.get("PlantDesc"));
            soListBean.setShippingPoint(headerDetail.get("ShippingPoint"));
            soListBean.setShippingPointDesc(headerDetail.get("ShippingPointDesc"));
            soListBean.setOrderType(headerDetail.get("OrderType"));
            soListBean.setOrderTypeDesc(headerDetail.get("OrderTypeDesc"));
            soListBean.setPODate(headerDetail.get("PODate"));
            soListBean.setPODate1(headerDetail.get("PODate1"));
            soListBean.setPaymentTerm(headerDetail.get("PaymentTerm"));
            soListBean.setPaymentTermDesc(headerDetail.get("PaymentTermDesc"));
            soListBean.setIncoTerm1(headerDetail.get("IncoTerm1"));
            soListBean.setIncoterm1Desc(headerDetail.get("IncoTermDesc"));
            soListBean.setIncoterm2(headerDetail.get("IncoTerm2"));
            soListBean.setRemarks(headerDetail.get("Remarks"));
            soListBean.setAddress(headerDetail.get("Address"));
            soListBean.setTransportName(headerDetail.get("TransportName"));
            soListBean.setTransportNameID(headerDetail.get("TransportNameID"));
            soListBean.setPONo(headerDetail.get("PONo"));
            soListBean.setCurrency(Constants.headerDetail.get("Currency"));
            soListBean.setTotalAmt(Constants.headerDetail.get("TotalAmt"));
            soListBean.setUnloadingPointId(headerDetail.get("UploadingPointID"));
            soListBean.setUnloadingPointDesc(headerDetail.get("UploadingPointDesc"));
            soListBean.setReceivingPointId(headerDetail.get("ReceivingPointId"));
            soListBean.setReceivingPointDesc(headerDetail.get("ReceivingPointDesc"));
        }
        return soListBean;
    }*/

  /*  private void getCreditAmountDataFromOnline() {
        try {
            progressDialog = ConstantsUtils.showProgressDialog(SOQuantityActivity.this, "Fetching credit limit please wait...");
            String divisionId = ConstantsUtils.getPerticularName(headerDetail.get("SalesArea"), 2);
            String controlAreaId = "1500";
            if (divisionId.equals("02")) {
                controlAreaId = "1300";
            }
            String qry = Constants.CustomerCreditLimits + "/?$filter=Customer+eq+'" + mStrCustomerNo + "'+and+CreditControlAreaID+eq+'" + controlAreaId + "'";
            new GetCreditAmountAsyncTask(SOQuantityActivity.this, creditLimitBeanArrayList, this, qry).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /*validate credit limit*/
    /*private void validateCreditLimit(ArrayList<CreditLimitBean> creditLimitBeenList) {
        Log.d("soQty", "validateCreditLimit: ");
        if (!creditLimitBeenList.isEmpty()) {
            try {
                CreditLimitBean creditLimitBean = creditLimitBeenList.get(0);
                BigDecimal creditExposure = new BigDecimal(creditLimitBean.getCreditExposure());
                BigDecimal creditLimits = new BigDecimal(creditLimitBean.getCreditLimit());
                BigDecimal soTotalAmt = new BigDecimal(Constants.headerDetail.get("TotalAmt"));
                BigDecimal finalPer = SOUtils.getExposurePercentage(creditExposure, creditLimits, soTotalAmt);
                if (finalPer.compareTo(new BigDecimal(140)) == -1) {
                    openNextActivity(true);
                } else {
                    UtilConstants.dialogBoxWithCallBack(SOQuantityActivity.this, "", "Credit limit exceeded", "Ok", "",false, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            UtilConstants.dialogBoxWithCallBack(SOQuantityActivity.this, "", "Credit limit not found", "Ok", "",false, null);
        }
    }*/

    /**
     * next step
     */
    private void onNext() {
        if (validateFinalAllData(SOQuantityActivity.this)) {
            if (soListBeanHeader != null) {
                soListBeanHeader.setSoItemBeanArrayList(itemsList);
            }
            getSimulateValue();
//            openNextActivity();
        }
    }

    /*list validation*/
    private boolean validateFinalAllData(Context mContext) {
        int childCount = recyclerView.getChildCount();
        boolean isValidation = true;
        int validationType = 0;
        for (int i = 0; i < childCount; i++) {
            SOItemBean soItemBean = itemsList.get(i);
            if (recyclerView.findViewHolderForLayoutPosition(i) instanceof SOQtyVH) {
                SOQtyVH childHolder = (SOQtyVH) recyclerView.findViewHolderForLayoutPosition(i);
                if (!checkValidData(childHolder, soItemBean)) {
                    isValidation = false;
                } else {
                    /*schedule item validation*/
                    validationType = SOUtils.checkItemValidation(soItemBean, childHolder.rvScheduleItem, mContext);
                    if (validationType > 0) {
                        isValidation = false;
                        break;
                    }
                }


            }
        }
        if (childCount == 0) {
            UtilConstants.dialogBoxWithCallBack(this, "", getString(R.string.atleast_one_material), getString(R.string.ok), "", false, null);
            isValidation = false;
        } else if (validationType == 3) {
            UtilConstants.dialogBoxWithCallBack(this, "", getString(R.string.atleast_one_delivery_schedule), getString(R.string.ok), "", false, null);
        } else if (validationType == 1) {
            UtilConstants.dialogBoxWithCallBack(this, "", getString(R.string.order_qty_delivery_qty_not_match), getString(R.string.ok), "", false, null);
        } else if (validationType == 2) {
            UtilConstants.dialogBoxWithCallBack(this, "", getString(R.string.select_delivery_schedule_date), getString(R.string.ok), "", false, null);
        } else if (validationType == 4) {
            UtilConstants.dialogBoxWithCallBack(this, "", getString(R.string.delivery_date_not_be_past), getString(R.string.ok), "", false, null);
        } else if (!isValidation) {
            UtilConstants.dialogBoxWithCallBack(this, "", getString(R.string.validation_plz_enter_mandatory_flds_val, UtilConstants.ERROR_CODE_UI_2000), getString(R.string.ok), "", false, null);
        }
        return isValidation;
    }

    @Override
    public void textChane(String charSequence, int position, SOQtyVH holder, SOItemBean soItemBean, SOSubItemAdapter soSubItemAdapter, boolean isTyped) {
        if (isTyped) {
            ArrayList<SOSubItemBean> soSubItemArralList = soItemBean.getSoSubItemBeen();
            soSubItemArralList.clear();
            soSubItem = new SOSubItemBean();

            Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            setDateToView(mYear, mMonth, mDay);
            soSubItem.setSubQty(soItemBean.getSoQty());

            soSubItem.setMaterialNo(soItemBean.getMatCode());
            soSubItemArralList.add(soSubItem);
            soItemBean.setSoSubItemBeen(soSubItemArralList);


            if (soSubItemAdapter != null) {
                soSubItemAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void subTextChange(String charSequence, int subPos, int headerPos, SOItemBean soItemBean, SOSubItemBean soSubItemBean, ImageView ivAddSchedule) {

        String soQty = soItemBean.getSoQty();
        int checkSoStatus = SOUtils.checkButtonEnable(soQty, soItemBean.getSoSubItemBeen());
        if (checkSoStatus == 1) {
            ivAddSchedule.setVisibility(View.VISIBLE);
            soItemBean.setButtonOnClick(true);
            isDialogBoxShown = false;
        } else if (checkSoStatus == 0) {
            soItemBean.setButtonOnClick(false);
            ivAddSchedule.setVisibility(View.VISIBLE);
        } else if (checkSoStatus == 2) {
            if (!isDialogBoxShown) {
                UtilConstants.dialogBoxWithCallBack(this, "", "Delivery schedule should not be greater then order quantity", "Ok", "", false, null);
                isDialogBoxShown = true;
            }
            ivAddSchedule.setVisibility(View.VISIBLE);
            soItemBean.setButtonOnClick(false);
        }

    }

    @Override
    public void datePicker(TextView textView, int pos, int headerPos) {
        soSubItem = itemsList.get(headerPos).getSoSubItemBeen().get(pos);
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));

        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        datePickerDialog.show();
        showDialog(DATE_DIALOG_ID);

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        setDateToView(year, month, dayOfMonth);
        soQuantityAdapter.notifyDataSetChanged();
    }

    /*set data to bean*/
    private void setDateToView(int mYear, int mMonth, int mDay) {
        String mon = "";
        String day = "";
        int mnt = 0;
        mnt = mMonth + 1;
        if (mnt < 10)
            mon = "0" + mnt;
        else
            mon = "" + mnt;
        day = "" + mDay;
        if (mDay < 10)
            day = "0" + mDay;

        if (soSubItem != null) {
            soSubItem.setDateForStore(mYear + "-" + mon + "-" + day);
            soSubItem.setDate(new StringBuilder().append(mDay).append("-")
                    .append(Constants.ORG_MONTHS[mMonth]).append("-").append("")
                    .append(mYear).toString());
            if (tvDialogDate != null) {
                tvDialogDate.setText(soSubItem.getDate());
            }
        }
    }

    @Override
    public void onItemClick(View view, final int position, final int headerPos) {
        UtilConstants.dialogBoxWithCallBack(SOQuantityActivity.this, "", "Are you sure delete this item?", getString(R.string.yes), getString(R.string.no), false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean clickedStatus) {
                if (clickedStatus) {
                    SOItemBean soHeaderBean = itemsList.get(headerPos);
                    soHeaderBean.getSoSubItemBeen().remove(position);
                    soQuantityAdapter.notifyDataSetChanged();
                }
            }
        });


    }

    @Override
    public void onAddListener(SOQtyVH holder, SOItemBean soItemBean) {
        if (checkValidData(holder, soItemBean)) {
            holder.rvScheduleItem.setVisibility(View.VISIBLE);
            ArrayList<SOSubItemBean> soSubItemArralList = soItemBean.getSoSubItemBeen();
            soSubItem = new SOSubItemBean();
            if (!soSubItemArralList.isEmpty()) {
                soSubItem.setDate(getString(R.string.so_schedule_date_item_hint));
                soSubItem.setSubQty("");
            } else {
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                setDateToView(mYear, mMonth, mDay);
                soSubItem.setSubQty(soItemBean.getSoQty());
            }

            soSubItem.setMaterialNo(soItemBean.getMatCode());
            soSubItemArralList.add(soSubItem);
            soItemBean.setSoSubItemBeen(soSubItemArralList);
            soQuantityAdapter.notifyDataSetChanged();
        } else {
            UtilConstants.dialogBoxWithCallBack(this, "", getString(R.string.validation_plz_enter_mandatory_flds_val, UtilConstants.ERROR_CODE_UI_2000), "Ok", "", false, null);
        }
    }

    @Override
    public void onDeleteListener(SOQtyVH holder, SOItemBean soItemBean, final int pos) {
        UtilConstants.dialogBoxWithCallBack(SOQuantityActivity.this, "", getString(R.string.msg_confirm_delete_material), getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (b) {
                    itemsList.remove(pos);
                    soQuantityAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    /*show custom dialog*/
   /* public void showCustomDialog(final Activity activity, SOItemBean soItemBean) {
        final ArrayList<SOSubItemBean> soSubItemBean = soItemBean.getSoSubItemBeen();

        soSubItem = new SOSubItemBean();
       *//* Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        setDateToView(mYear,mMonth,mDay);*//*
        soSubItem.setDate(getString(R.string.so_schedule_date_item_hint));
        final AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater factory = LayoutInflater.from(this);
        View customDialogView = factory.inflate(R.layout.so_qty_dialog_view, null);
        dialog.setView(customDialogView);
//        dialog.setV(R.layout.so_qty_dialog_view);

        tvDialogDate = (TextView) customDialogView.findViewById(R.id.tv_date_picker);
        TextView tvUOM = (TextView) customDialogView.findViewById(R.id.tv_uom);
        final EditText etQty = (EditText) customDialogView.findViewById(R.id.et_subQty);
        UtilConstants.editTextDecimalFormat(etQty, 13, 0);
        tvUOM.setText(soItemBean.getUom());
        tvDialogDate.setHint(soSubItem.getDate());
        Button btOk = (Button) customDialogView.findViewById(R.id.btn_ok);
        Button btCancel = (Button) customDialogView.findViewById(R.id.btn_cancel);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(soSubItem.getDateForStore()) || TextUtils.isEmpty(soSubItem.getSubQty())) {
                    if (TextUtils.isEmpty(soSubItem.getSubQty())) {
                        etQty.setBackgroundResource(R.drawable.edittext_border);
                    }
                    ConstantsUtils.dialogBoxWithButton(activity, "", getString(R.string.validation_plz_enter_mandatory_flds), "Ok", "", null);
                }*//*else if(TextUtils.isEmpty(soSubItem.getDateForStore())){
                    ConstantsUtils.dialogBoxWithButton(activity, "", "Please select date", "Ok", "", null);
                }else  if(TextUtils.isEmpty(soSubItem.getSubQty())){
                    etQty.setBackgroundResource(R.drawable.edittext_border);
                    ConstantsUtils.dialogBoxWithButton(activity, "", "Please enter qty", "Ok", "", null);
                }*//* else {
                    dialog.dismiss();
                    soSubItemBean.add(soSubItem);
                    soQuantityAdapter.notifyDataSetChanged();
                }

            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tvDialogDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
                cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
                cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
                cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));

                datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                datePickerDialog.show();
                showDialog(DATE_DIALOG_ID);
            }
        });
        etQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                soSubItem.setSubQty(s.toString());
                etQty.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etQty.setHint("");
                }
            }
        });

        dialog.show();

    }*/

    /*check valid data*/
    private boolean checkValidData(SOQtyVH holder, SOItemBean soItemBean) {
        boolean validationStatus = true;
        if (TextUtils.isEmpty(soItemBean.getSoQty())) {
            holder.soQtyValue.setBackgroundResource(R.drawable.edittext_border);
            validationStatus = false;
        } else {
            try {
                BigDecimal bigDecimal = new BigDecimal(soItemBean.getSoQty());
                if (bigDecimal.compareTo(new BigDecimal(0)) != 1) {
                    holder.soQtyValue.setBackgroundResource(R.drawable.edittext_border);
                    validationStatus = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                holder.soQtyValue.setBackgroundResource(R.drawable.edittext_border);
                validationStatus = false;
            }
        }
        return validationStatus;
    }

    @Override
    public void focusChangeListenerHeader(View v, boolean hasFocus, SOQtyVH holder, SOItemBean soItemBean) {

    }

    @Override
    public void focusChangeListenerItem(View v, boolean hasFocus, SOQtyVH holder, SOItemBean soItemBean, SOSubItemBean soSubItemBean) {

    }

    @Override
    public void creditAmount(ArrayList<CreditLimitBean> creditLimitBeenList) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
//        validateCreditLimit(creditLimitBeenList);
    }

    @Override
    public void responseSuccess(ODataRequestExecution request, List<ODataEntity> entitiesHead, Bundle bundle) {
        ODataPayload payload = ((ODataResponseSingle) request.getResponse()).getPayload();
        String testRun = "";
        if (payload != null && payload instanceof ODataEntity) {
            ODataEntity oEntity = (ODataEntity) payload;
            ODataEntity oEntityReq = oEntity;
            ODataPropMap properties = oEntityReq.getProperties();
            ODataProperty property = properties.get(Constants.Testrun);
            if (property != null) {
                testRun = property.getValue().toString();
            }
//            if (!TextUtils.isEmpty(testRun)) {
            try {
                property = properties.get(Constants.TotalAmount);
                soListBeanHeader.setTotalAmt(property.getValue().toString());
                property = properties.get(Constants.Currency);
                soListBeanHeader.setCurrency(property.getValue().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            ODataNavigationProperty navProp = oEntity.getNavigationProperty(Constants.SOItemDetails);
//                ArrayList<SOItemBean> soItemBeenList = soListBeanHeader.getSoItemBeanArrayList();
//            for (SOItemBean soItemBeen : soListBeanHeader.getSoItemBeanArrayList())
//                SOItemBean soItemBeen = soListBeanHeader.getSoItemBeanArrayList().get(0);
            ArrayList<SOItemBean> soItemList = new ArrayList<>();
            if (navProp.getNavigationType().toString().equalsIgnoreCase("EntitySet")) {
                ODataEntitySet feed = (ODataEntitySet) navProp.getNavigationContent();
                List<ODataEntity> entities = feed.getEntities();
                for (ODataEntity entity : entities) {
                    properties = entity.getProperties();
                    property = properties.get(Constants.ItemNo);
                    String itemNo = "";
                    if (property != null) {
                        itemNo = property.getValue().toString();
                    }
                    SOItemBean soItemBeen = SOUtils.getItemBasedOnId(soListBeanHeader.getSoItemBeanArrayList(), itemNo);
                    if (soItemBeen != null) {
                        property = properties.get(Constants.Currency);
                        if (property != null) {
                            soItemBeen.setCurrency(property.getValue().toString());
                        }

                        property = properties.get(Constants.UOM);
                        if (property != null) {
                            soItemBeen.setUom(property.getValue().toString());
                        }
                        property = properties.get(Constants.UnitPrice);
                        if (property != null) {
                            soItemBeen.setUnitPrice(property.getValue().toString());
                        }
                        property = properties.get(Constants.NetAmount);
                        if (property != null) {
                            soItemBeen.setNetAmount(property.getValue().toString());
                        }

                        ODataNavigationProperty navProp2 = entity.getNavigationProperty(Constants.SOConditionItemDetails);
                        ArrayList<SOConditionItemDetaiBean> soConditionItemDetaiBeenList =new ArrayList<>();/*soItemBeen.getConditionItemDetaiBeanArrayList();*/
                        ArrayList<SOConditionItemDetaiBean> soArrayListBeforeSort = new ArrayList<>();
                        if (navProp2.getNavigationType().toString().equalsIgnoreCase("EntitySet")) {

                            ODataEntitySet feedCondition = (ODataEntitySet) navProp2.getNavigationContent();
                            List<ODataEntity> entitiesCondition = feedCondition.getEntities();
                            SOConditionItemDetaiBean soConditionItemDetaiBean = null;
                            BigDecimal totalNormalAmt = new BigDecimal("0.0");
                            BigDecimal subTotalAmt = new BigDecimal("0.0");
                            for (ODataEntity entityCondition : entitiesCondition) {
                                soConditionItemDetaiBean = OfflineManager.getConditionItemDetails(entityCondition,soArrayListBeforeSort);
                                if (soConditionItemDetaiBean != null) {
                                    totalNormalAmt = totalNormalAmt.add(new BigDecimal(soConditionItemDetaiBean.getAmount()));
                                    subTotalAmt = subTotalAmt.add(new BigDecimal(soConditionItemDetaiBean.getConditionValue()));
                                    soArrayListBeforeSort.add(soConditionItemDetaiBean);
                                }
                            }
                            soConditionItemDetaiBeenList.addAll(soArrayListBeforeSort);
                            soItemBeen.setConditionItemDetaiBeanArrayList(soConditionItemDetaiBeenList);
                            soItemList.add(soItemBeen);

                        }
                    }
                }
                soListBeanHeader.setSoItemBeanArrayList(soItemList);
            }
//            }
        }
        if (progressDialog != null) {
            progressDialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    openNextActivity();
                }
            });
        }
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, final String errorMsg, Bundle bundle) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UtilConstants.dialogBoxWithCallBack(SOQuantityActivity.this, "", errorMsg, getString(R.string.ok), "", false, null);
            }
        });

    }

   /* *//*start simulate*//*
    public class startSimulateAsyncTask extends AsyncTask<Void, Boolean, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ConstantsUtils.showProgressDialog(SOQuantityActivity.this);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean isStoreOpened = false;
            try {

                Thread.sleep(1000);
                try {
                    Constants.IsOnlineStoreFailed = false;

                    isStoreOpened = OnlineManager.openOnlineStore(SOQuantityActivity.this);
                    if (isStoreOpened) {
                        ArrayList<HashMap<String, String>> arrtableSimu = new ArrayList<HashMap<String, String>>();
//                        String plant = headerDetail.get("Plant");
                        int i = 0;
                        for (SOItemBean soItemBean : itemsList) {
//                            HashMap<String, String> itemDesc = arrtable.get(i);
                            HashMap<String, String> singleItem = new HashMap<String, String>();
                            singleItem.put(Constants.ItemNo, ConstantsUtils.addZeroBeforeValue(i + 1, ConstantsUtils.ITEM_MAX_LENGTH));
                            singleItem.put(Constants.Material, soItemBean.getMatCode());
                            singleItem.put(Constants.Plant, soListBeanHeader.getPlant());
                            singleItem.put(Constants.StorLoc, "");
                            singleItem.put(Constants.UOM, "");
                            singleItem.put(Constants.Quantity, soItemBean.getSoQty());
                            singleItem.put(Constants.Currency, "");//itemDesc.get("Currency")
                            singleItem.put(Constants.UnitPrice, "0");
                            singleItem.put(Constants.NetAmount, "0");
                            singleItem.put(Constants.GrossAmount, "0");
                            singleItem.put(Constants.Freight, "");//itemDesc.get("Freight")
                            singleItem.put(Constants.Tax, "0");
                            singleItem.put(Constants.Discount, "0");


                            arrtableSimu.add(singleItem);
                            i++;
                        }
                        Hashtable dbHeadTable = new Hashtable();
//                        Log.d("SOQty", "doInBackground: " + arrtableSimu);
                        dbHeadTable.put(Constants.OrderType, soListBeanHeader.getOrderType());
                        dbHeadTable.put(Constants.OrderDate, Constants.getNewDateTimeFormat());
                        dbHeadTable.put(Constants.CustomerNo, soListBeanHeader.getSoldTo());
                        dbHeadTable.put(Constants.CustomerPO, soListBeanHeader.getPONo());
                        dbHeadTable.put(Constants.CustomerPODate, soListBeanHeader.getPODate());
                        dbHeadTable.put(Constants.ShippingTypeID, "");
                        dbHeadTable.put(Constants.MeansOfTranstyp, "");
                        dbHeadTable.put(Constants.TransporterID, soListBeanHeader.getTransportNameID());
                        dbHeadTable.put(Constants.TransporterName, soListBeanHeader.getTransportName());
                        dbHeadTable.put(Constants.ShipToParty, soListBeanHeader.getShipTo());
                        dbHeadTable.put(Constants.SalesArea, soListBeanHeader.getSalesArea());
                        dbHeadTable.put(Constants.SalesOffice, "");
                        dbHeadTable.put(Constants.SalesGroup, "");
                        dbHeadTable.put(Constants.Plant, soListBeanHeader.getPlant());
                        dbHeadTable.put(Constants.Incoterm1, soListBeanHeader.getIncoTerm1());//headerDetail.get("IncoTerm1")
                        dbHeadTable.put(Constants.Incoterm2, soListBeanHeader.getIncoterm2());//headerDetail.get("IncoTerm1")
                        dbHeadTable.put(Constants.Payterm, soListBeanHeader.getPaymentTerm());//headerDetail.get("PaymentTerm")
                        dbHeadTable.put(Constants.Currency, "INR");
                        dbHeadTable.put(Constants.NetPrice, "0");
                        dbHeadTable.put(Constants.TotalAmount, "0");
                        dbHeadTable.put(Constants.TaxAmount, "0");
                        dbHeadTable.put(Constants.Freight, "0");
                        dbHeadTable.put(Constants.Discount, "0");
                        dbHeadTable.put(Constants.Testrun, "S");

                        OnlineManager.getSimulateValue(dbHeadTable, arrtableSimu, SOQuantityActivity.this);
                    }
                } catch (OnlineODataStoreException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return isStoreOpened;
        }

        @Override
        protected void onPostExecute(Boolean openStore) {
            super.onPostExecute(openStore);
            if (!openStore) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                UtilConstants.dialogBoxWithCallBack(SOQuantityActivity.this, "", Constants.makeMsgReqError(Constants.ErrorNo, SOQuantityActivity.this, false), getString(R.string.ok), "", false, null);
            }
        }
    }*/
}
