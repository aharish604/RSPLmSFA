package com.rspl.sf.msfa.reports;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.OutstandingListAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.MyUtils;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by e10604 on 27/11/2016.
 *
 */
public class OutstandingHistoryActivity extends AppCompatActivity implements View.OnClickListener, UIListener {


    private OutstandingListAdapter outstandingListAdapter = null;
    private ArrayList<OutstandingBean> alOutstandingsBean;
    private ArrayList<OutstandingBean> alMainInvoiceBean = new ArrayList<>();
    private String mStrBundleRetID = "", mStrBundleCPGUID = "";
    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";

    Spinner spOutstandingStatus;
    String selectedStatus;

    String currencyText = "";
    double totalOutVal = 0.00;
    private ProgressDialog prgressDialog = null;


    TextView tvEmptyLay = null;
    TextView tvTotalOutValCurr = null;

    TextView tvRetName = null, tvUID = null;
    TextView tvTotalOutVal = null;
    ListView lv_out_his_list = null;

    String concatCollectionStr = "";
    ArrayList<String> alAssignColl = new ArrayList<>();
    ProgressDialog syncProgDialog = null;
    boolean dialogCancelled = false;
    TextView tv_last_sync_time_value;
    private Bundle bundleExtras;
    EditText etInvoiceSearch;
    private String[][] arrayInvStatusVal;
    private GUID refguid =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
       // ActionBarView.initActionBarView(this, true,getString(R.string.title_OutstandingHistory));

        setContentView(R.layout.activity_outstanding_hist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_OutstandingHistory), 0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
        }
        initUI();
    }

    void initUI() {
        tv_last_sync_time_value = (TextView) findViewById(R.id.tv_last_sync_time_value);
        tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.OutstandingInvoices, Constants.TimeStamp,this));
        lv_out_his_list = (ListView) findViewById(R.id.lv_out_standing_list);

        //new
        tvRetName = (TextView) findViewById(R.id.tv_bill_hist_ret_name);
        tvUID = (TextView) findViewById(R.id.tv_bill_hist_uid);
        tvTotalOutVal = (TextView) findViewById(R.id.tv_total_out_val);
        tvTotalOutValCurr = (TextView) findViewById(R.id.tv_total_out_val_currency);

        spOutstandingStatus = (Spinner) findViewById(R.id.spin_invoice_his_status_id);

        //new
        tvRetName.setText(mStrBundleRetName);
        tvUID.setText(mStrBundleRetUID);

        tvEmptyLay = (TextView) findViewById(R.id.tv_empty_lay);
        etInvoiceSearch = (EditText) findViewById(R.id.ed_invoice_search);
        etInvoiceSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                outstandingListAdapter.getFilter().filter(cs); //Filter from my adapter
                outstandingListAdapter.notifyDataSetChanged(); //Update my view
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
            }
        });

        getStatus();



    }

    /*get different status for outstanding*/
    private void getStatus() {
      /*  try {
            alMainInvoiceBean = OfflineManager.getOutstandingList(Constants.OutstandingInvoices + "?$filter="
//                    + Constants.SoldToID + " eq '" + mStrBundleRetID + "'" + " and "
                    + Constants.CustomerNo + " eq '" + mStrBundleRetID + "'" + " and "
//                    + Constants.PaymentStatusID + " ne '" + "03" + "'", getApplicationContext(), "", mStrBundleCPGUID);
                    + Constants.InvoiceStatus + " ne '" + "03" + "'", getApplicationContext(), "", mStrBundleCPGUID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();


        }*/

        try {
            String mStrConfigQry = Constants.ConfigTypesetTypes + "?$filter=" + Constants.Typeset + " eq '" +
                    Constants.OINVAG + "' &$orderby=" + Constants.Types + " asc";

            arrayInvStatusVal = OfflineManager.getConfigTysetTypesValues(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
        }


        if (arrayInvStatusVal == null) {
            arrayInvStatusVal = new String[2][1];
            arrayInvStatusVal[0][0] = "";
            arrayInvStatusVal[1][0] = "";
        }

        String[][] tempStatusArray = new String[2][arrayInvStatusVal[0].length + 1];
        tempStatusArray[0][0] = Constants.str_00;
        tempStatusArray[1][0] = Constants.All;
        for (int i = 1; i < arrayInvStatusVal[0].length + 1; i++) {
            tempStatusArray[0][i] = arrayInvStatusVal[0][i - 1];
            tempStatusArray[1][i] = arrayInvStatusVal[1][i - 1];
        }
        arrayInvStatusVal = tempStatusArray;


        ArrayAdapter<String> productCategoryAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_textview,R.id.tvItemValue, arrayInvStatusVal[1]);
        productCategoryAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spOutstandingStatus.setAdapter(productCategoryAdapter);

        spOutstandingStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {
                selectedStatus = arrayInvStatusVal[0][position];

                clearEditTextSearchBox();

                new LoadData(OutstandingHistoryActivity.this).execute();


            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    private void clearEditTextSearchBox(){
        if(etInvoiceSearch!=null && etInvoiceSearch.getText().toString().length()>0)
            etInvoiceSearch.setText("");
    }
    /*Get List of outstanding invoices */
    private void getInvoiceList(String status) {
        try {
            alOutstandingsBean = new ArrayList<>();
            if (status.equalsIgnoreCase("")) {
                alOutstandingsBean = OfflineManager.getOutstandingList(Constants.OutstandingInvoices
//                                + "?$filter=" + Constants.SoldToID + " eq '" + mStrBundleRetID + "'"
                                + "?$filter=" + Constants.CustomerNo + " eq '" + mStrBundleRetID + "' &$orderby=" + Constants.InvoiceDate + " asc",
                        //+ "'"
//                                + " and " + Constants.PaymentStatusID + " ne '" + "03" + "'",
                        // + " and " + Constants.InvoiceStatus + " ne '" + "03" + "'",
                        getApplicationContext(), "", mStrBundleCPGUID);
                alMainInvoiceBean.clear();
                alMainInvoiceBean.addAll(alOutstandingsBean);

//                OutstandingHistoryActivity.this.outstandingListAdapter = new OutstandingListAdapter(OutstandingHistoryActivity.this, R.layout.activity_invoice_history_list,
//                        alOutstandingsBean,bundleExtras);
//                lv_out_his_list.setEmptyView(findViewById(R.id.tv_empty_lay) );
//                lv_out_his_list.setAdapter(outstandingListAdapter);
//                OutstandingHistoryActivity.this.outstandingListAdapter.notifyDataSetChanged();

            } else {
                alOutstandingsBean.clear();
//                switch (status) {
//                    case "01": {
//                        for (OutstandingBean item : alMainInvoiceBean) {
//                            if (getBillAge(item) >= 0 && getBillAge(item) <= 30)
//                                alOutstandingsBean.add(item);
//                        }
//                    }
//                    break;
//
//                    case "02": {
//                        for (OutstandingBean item : alMainInvoiceBean) {
//                            if (getBillAge(item) > 30 && getBillAge(item) <= 60)
//                                alOutstandingsBean.add(item);
//                        }
//                    }
//                    break;
//
//                    case "03": {
//                        for (OutstandingBean item : alMainInvoiceBean) {
//                            if (getBillAge(item) > 60 && getBillAge(item) <= 90)
//                                alOutstandingsBean.add(item);
//                        }
//                    }
//                    break;
//
//                    case "04": {
//                        for (OutstandingBean item : alMainInvoiceBean) {
//                            if (getBillAge(item) > 90)
//                                alOutstandingsBean.add(item);
//                        }
//                    }
//                    break;
//                }
                boolean  invPayStatus = false;

                for (OutstandingBean item : alMainInvoiceBean) {
                    invPayStatus = false;
                    if (status.equalsIgnoreCase(Constants.Bucket1) && !"0.00".equals(item.getBucket1())) {
                        invPayStatus = true;
                    } else if (status.equalsIgnoreCase(Constants.Bucket2) && !"0.00".equals(item.getBucket2())) {
                        invPayStatus = true;
                    } else if (status.equalsIgnoreCase(Constants.Bucket3) && !"0.00".equals(item.getBucket3())) {
                        invPayStatus = true;
                    } else if (status.equalsIgnoreCase(Constants.Bucket4) && !"0.00".equals(item.getBucket4())) {
                        invPayStatus = true;
                    } else if (status.equalsIgnoreCase(Constants.Bucket5) && !"0.00".equals(item.getBucket5())) {
                        invPayStatus = true;
                    } else if (status.equalsIgnoreCase(Constants.Bucket6) && !"0.00".equals(item.getBucket6())) {
                        invPayStatus = true;
                    } else if (status.equalsIgnoreCase(Constants.Bucket7) && !"0.00".equals(item.getBucket7())) {
                        invPayStatus = true;
                    } else if (status.equalsIgnoreCase(Constants.Bucket8) && !"0.00".equals(item.getBucket8())) {
                        invPayStatus = true;
                    } else if (status.equalsIgnoreCase(Constants.Bucket9) && !"0.00".equals(item.getBucket9())) {
                        invPayStatus = true;
                    } else if (status.equalsIgnoreCase(Constants.Bucket10) && !"0.00".equals(item.getBucket10())) {
                        invPayStatus = true;
                    }

                    if (invPayStatus)
                        alOutstandingsBean.add(item);
                }
                   /* if(item.getInvoiceNo().contains(edtSearch)){
                        invPayStatus = true;
                    }*/



                if (alOutstandingsBean != null && alOutstandingsBean.size() > 0) {
                    Collections.sort(alOutstandingsBean, new Comparator<OutstandingBean>() {
                        public int compare(OutstandingBean one, OutstandingBean other) {
                            return one.getInvoiceDate().compareTo(other.getInvoiceDate());
                        }
                    });

                }


            }
//            if (alOutstandingsBean.size() < 1) {
//                tvEmptyLay.setVisibility(View.VISIBLE);
//            } else
//                tvEmptyLay.setVisibility(View.GONE);

            totalOutVal = 0.00;
            for (OutstandingBean invoice : alOutstandingsBean) {
//                totalOutVal = totalOutVal + (Double.parseDouble(invoice.getInvoiceAmount()) -
//                        (Double.parseDouble(invoice.getCollectionAmount()) + Double.parseDouble(invoice.getDevCollAmount())));
                totalOutVal = totalOutVal + (Double.parseDouble(invoice.getInvoiceAmount()));
//                        (Double.parseDouble(invoice.getCollectionAmount()) + Double.parseDouble(invoice.getDevCollAmount())));
            }
            for (OutstandingBean invoice : alOutstandingsBean) {
                currencyText = invoice.getCurrency();
                break;
            }

//            tvTotalOutVal.setText(" " + UtilConstants.removeLeadingZerowithTwoDecimal(String.valueOf(totalOutVal)));
//            tvTotalOutValCurr.setText(currencyText);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_invoice_his_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_refresh_inv:
                onRefresh();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                onBackPressed();
                break;
        }
    }

    public static int getBillAge(OutstandingBean item) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.dtFormat_ddMMyyyywithslash);
        Date date = new Date();
        try {
            date = sdf.parse(item.getInvoiceDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long timeDifferenceInMIliSecond = (new Date().getTime()) - date.getTime();
        int billOutDays = (int) (timeDifferenceInMIliSecond / (1000 * 60 * 60 * 24));
        return billOutDays;
    }

    /*Refreshes outstanding invoice list from backend*/
    void onRefresh() {


        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
            alAssignColl.clear();
            concatCollectionStr = "";
            alAssignColl.add(Constants.OutstandingInvoiceItemDetails);
            alAssignColl.add(Constants.OutstandingInvoices);
            for (int incVal = 0; incVal < alAssignColl.size(); incVal++) {
                if (incVal == 0 && incVal == alAssignColl.size() - 1) {
                    concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
                } else if (incVal == 0) {
                    concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
                } else if (incVal == alAssignColl.size() - 1) {
                    concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
                } else {
                    concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
                }
            }

            try {
                Constants.isSync = true;
                dialogCancelled = false;
                refguid = GUID.newRandom();
                Constants.updateStartSyncTime(OutstandingHistoryActivity.this,Constants.DownLoad,Constants.StartSync,refguid.toString().toUpperCase());
                new LoadingData().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            UtilConstants.showAlert(getString(R.string.no_network_conn),OutstandingHistoryActivity.this);
        }

    }

    /*AsyncTask to refresh outstanding invoices*/
    public class LoadingData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(OutstandingHistoryActivity.this, R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
            syncProgDialog.setCancelable(true);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();

            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    OutstandingHistoryActivity.this, R.style.MyTheme);
                            builder.setMessage(R.string.do_want_cancel_sync)
                                    .setCancelable(false)
                                    .setPositiveButton(
                                            R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {
                                                    dialogCancelled = true;

                                                    onBackPressed();
                                                }
                                            })
                                    .setNegativeButton(
                                            R.string.no,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {

                                                    try {
                                                        syncProgDialog
                                                                .show();
                                                        syncProgDialog
                                                                .setCancelable(true);
                                                        syncProgDialog
                                                                .setCanceledOnTouchOutside(false);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    dialogCancelled = false;

                                                }
                                            });
                            builder.show();
                        }
                    });
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                try {

                    OfflineManager.refreshStoreSync(getApplicationContext(), OutstandingHistoryActivity.this, Constants.Fresh, concatCollectionStr);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    @Override
    public void onRequestError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e, OutstandingHistoryActivity.this);
        if (errorBean.hasNoError()) {
            if (dialogCancelled == false && !Constants.isStoreClosed) {
                if (operation == Operation.OfflineRefresh.getValue()) {
                    try {
                        /*String syncTime = Constants.getSyncHistoryddmmyyyyTime();
                        for (int incReq = 0; incReq < alAssignColl.size(); incReq++) {

                            String colName = alAssignColl.get(incReq);
                            if (colName.contains("?$")) {
                                String splitCollName[] = colName.split("\\?");
                                colName = splitCollName[0];
                            }

                            Constants.events.updateStatus(Constants.SYNC_TABLE,
                                    colName, Constants.TimeStamp, syncTime
                            );
                        }*/
                        Constants.updateSyncTime(alAssignColl,this,Constants.DownLoad,refguid.toString().toUpperCase());
                    } catch (Exception exce) {
                        LogManager.writeLogError(Constants.SyncTableHistory + " " + exce.getMessage());
                    }

                    syncProgDialog.dismiss();
                    Constants.isSync = false;
                    if (!Constants.isStoreClosed) {
                        UtilConstants.showAlert(getString(R.string.msg_error_occured_during_sync), OutstandingHistoryActivity.this);
                    } else {
                        UtilConstants.showAlert(getString(R.string.msg_sync_terminated), OutstandingHistoryActivity.this);
                    }
                }
            }
        } else {
            Constants.isSync = false;
            syncProgDialog.dismiss();
            Constants.displayMsgReqError(errorBean.getErrorCode(), OutstandingHistoryActivity.this);
        }
    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {
        if (dialogCancelled == false && !Constants.isStoreClosed) {
            if (operation == Operation.OfflineRefresh.getValue()) {
                try {
                    OfflineManager.getAuthorizations(getApplicationContext());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                try {
                    /*String syncTime = Constants.getSyncHistoryddmmyyyyTime();
                    for (int incReq = 0; incReq < alAssignColl.size(); incReq++) {
                        String colName = alAssignColl.get(incReq);
                        if (colName.contains("?$")) {
                            String splitCollName[] = colName.split("\\?");
                            colName = splitCollName[0];
                        }

                        Constants.events.updateStatus(Constants.SYNC_TABLE,
                                colName, Constants.TimeStamp, syncTime
                        );
                    }*/
                    Constants.updateSyncTime(alAssignColl,this,Constants.DownLoad,refguid.toString().toUpperCase());
                } catch (Exception exce) {
                    LogManager.writeLogError(Constants.SyncTableHistory + " " + exce.getMessage());
                }

                tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.OutstandingInvoices, Constants.TimeStamp,this));

                syncProgDialog.dismiss();
                Constants.isSync = false;
                if (!Constants.isStoreClosed) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            OutstandingHistoryActivity.this, R.style.MyTheme);
                    builder.setMessage(getString(R.string.msg_sync_successfully_completed))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            getStatus();
                                        }
                                    });

                    builder.show();
                } else {
                    UtilConstants.showAlert(getString(R.string.msg_sync_terminated), OutstandingHistoryActivity.this);
                }
            }
        }
    }

    class LoadData extends AsyncTask<Void, Void, Void> {

        Context mContext;

        public LoadData(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgressDialog = MyUtils.showProgressDialog(mContext, "", getString(R.string.app_loading));
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (selectedStatus.equalsIgnoreCase("00")) {

                getInvoiceList("");
            } else {
                getInvoiceList(selectedStatus);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (prgressDialog != null) {
                prgressDialog.dismiss();
            }

            if (alOutstandingsBean.size() < 1) {
                tvEmptyLay.setVisibility(View.VISIBLE);
            } else
                tvEmptyLay.setVisibility(View.GONE);


            OutstandingHistoryActivity.this.outstandingListAdapter = new OutstandingListAdapter(OutstandingHistoryActivity.this,
                    R.layout.activity_invoice_history_list, alOutstandingsBean, bundleExtras);
            lv_out_his_list.setAdapter(outstandingListAdapter);
            OutstandingHistoryActivity.this.outstandingListAdapter.notifyDataSetChanged();


            tvTotalOutVal.setText(" " + UtilConstants.removeLeadingZerowithTwoDecimal(String.valueOf(totalOutVal)));
            tvTotalOutValCurr.setText(currencyText);


        }
    }}
