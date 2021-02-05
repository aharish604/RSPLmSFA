//package com.arteriatech.sf.reports;
//
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.DialogInterface;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.Spinner;
//import android.widget.TextView;
//
//import com.arteriatech.mutils.common.OfflineODataStoreException;
//import com.arteriatech.mutils.common.Operation;
//import com.arteriatech.mutils.common.UIListener;
//import com.arteriatech.mutils.common.UtilConstants;
//import com.arteriatech.mutils.log.LogManager;
//import com.arteriatech.sf.adapter.NewInvoiceHisListAdapter;
//import com.arteriatech.sf.common.ActionBarView;
//import Constants;
//import com.arteriatech.sf.mbo.ErrorBean;
//import R;
//import OfflineManager;
//import com.sap.smp.client.odata.exception.ODataException;
//
//import java.util.ArrayList;
//
///**
// * Created by e10604 on 27/4/2016.
// *
// */
//public class NewInvoiceHistoryActivity extends AppCompatActivity implements UIListener {
//
//    private NewInvoiceHisListAdapter invoiceHisListAdapter = null;
//    private ArrayList<InvoiceHistoryBean> alInvoiceBean;
//
//    private ArrayList<InvoiceHistoryBean> alTempInvoiceBean;
//    private String mStrBundleRetID = "",mStrBundleCPGUID="";
//    private String mStrBundleRetName = "";
//    private String mStrBundleRetUID = "";
//
//    Spinner spinvHisStatus;
//
//    String selectedStatus;
//    //new
//    TextView tvEmptyLay = null;
//    String concatCollectionStr = "";
//    ArrayList<String> alAssignColl = new ArrayList<>();
//    ProgressDialog syncProgDialog = null;
//    boolean dialogCancelled = false;
//
//    private String[][] arrayInvStatusVal;
//    EditText edNameSearch;
//
//    //new
//    TextView tvRetName = null, tvUID = null;
//    ListView lv_inv_his_list = null;
//    TextView tv_last_sync_time_value;
//    private Bundle bundleExtras;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //Initialize action bar with back button(true)
//        ActionBarView.initActionBarView(this, true,getString(R.string.title_invoice_History));
//
//        setContentView(R.layout.activity_new_invoice_history);
//
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//
//        bundleExtras = getIntent().getExtras();
//        if (bundleExtras != null) {
//            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
//            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
//            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
//            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
//        }
//
//        initUI();
//    }
//
//    void initUI(){
//        lv_inv_his_list = (ListView)findViewById(R.id.lv_route_ret_list);
//
//
//        tv_last_sync_time_value = (TextView)findViewById(R.id.tv_last_sync_time_value);
////        tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.SSINVOICES, Constants.TimeStamp,this));
//        tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.SFINVOICES, Constants.TimeStamp,this));
//
//        tvRetName = (TextView) findViewById(R.id.tv_bill_hist_ret_name);
//        tvUID = (TextView) findViewById(R.id.tv_bill_hist_uid);
//
//        tvEmptyLay = (TextView)findViewById(R.id.tv_empty_lay);
//
//        spinvHisStatus = (Spinner)findViewById(R.id.spin_invoice_his_status_id);
//
//        tvRetName.setText(mStrBundleRetName);
//        tvUID.setText(mStrBundleRetUID);
//        edNameSearch = (EditText) findViewById(R.id.ed_invoice_search);
//        edNameSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
//                invoiceHisListAdapter.getFilter().filter(cs); //Filter from my adapter
//                invoiceHisListAdapter.notifyDataSetChanged(); //Update my view
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//            }
//
//            public void afterTextChanged(Editable arg0) {
//            }
//        });
//        getInvStatus();
//        getStatus();
//
//
//
//    }
//    private void clearEditTextSearchBox(){
//        if(edNameSearch!=null && edNameSearch.getText().toString().length()>0)
//            edNameSearch.setText("");
//    }
//
//    /*gets status for invoices*/
//    private void getStatus(){
//        if(arrayInvStatusVal ==null){
//            arrayInvStatusVal = new String[2][1];
//            arrayInvStatusVal[0][0]="";
//            arrayInvStatusVal[1][0]="";
//        }
//
//        String[][] tempStatusArray = new String[2][arrayInvStatusVal[0].length+1];
//        tempStatusArray[0][0] = Constants.str_00;
//        tempStatusArray[1][0] = Constants.All;
//        for(int i=1; i<arrayInvStatusVal[0].length+1;i++){
//            tempStatusArray[0][i] = arrayInvStatusVal[0][i-1];
//            tempStatusArray[1][i] = arrayInvStatusVal[1][i-1];
//        }
//        arrayInvStatusVal = tempStatusArray;
//
//        ArrayAdapter<String> productCategoryAdapter = new ArrayAdapter<String>(this,
//                R.layout.custom_textview,R.id.tvItemValue ,arrayInvStatusVal[1]);
//        productCategoryAdapter.setDropDownViewResource(R.layout.spinnerinside);
//        spinvHisStatus.setAdapter(productCategoryAdapter);
//
//
//        spinvHisStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View arg1,
//                                       int position, long arg3) {
//
//                selectedStatus = arrayInvStatusVal[0][position];
//
//                clearEditTextSearchBox();
//                if (selectedStatus.equalsIgnoreCase(Constants.str_00)) {
//
//                    getInvoiceList("");
//                }else{
//                    getInvoiceList(selectedStatus);
//                }
//
//            }
//            public void onNothingSelected(AdapterView<?> arg0) {
//
//            }
//        });
//    }
//
//
//    /*gets List of invoices*/
//    private void getInvoiceList(String status){
//        try {
//            if(status.equalsIgnoreCase("")){
//
//                alInvoiceBean = OfflineManager.getNewInvoiceHistoryList(Constants.InvoiceItems+"?$filter="+ Constants.CustomerNo+" eq '"+mStrBundleRetID+"' " +
//                        "and "+ Constants.InvoiceDate+" ge datetime'" + Constants.getLastMonthDate() + "' ",getApplicationContext(),status,mStrBundleCPGUID);
//                alTempInvoiceBean =new ArrayList<>();
//                alTempInvoiceBean.addAll(alInvoiceBean);
//
//            /*    for(int i=0;i<alTempInvoiceBean.size();i++){
//
//                    InvoiceHistoryBean invBean = OfflineManager.getInvoiceDetails(Constants.Invoices + "('" + alTempInvoiceBean.get(i).getInvoiceNo() + "')/" + Constants.SFInvoiceItemDetails);
//
//                }
//*/
//            }else{
////                alInvoiceBean = OfflineManager.getInvoiceHistoryList(Constants.SSINVOICES+"?$filter="+Constants.SoldToID+" eq '"+mStrBundleRetID+"'"+" " +
////                        "and "+Constants.PaymentStatusID+" eq '"+status+"' and "+Constants.InvoiceDate+" ge datetime'" + Constants.getLastMonthDate() + "' ",getApplicationContext(),status,mStrBundleCPGUID);
//
//                alInvoiceBean.clear();
//
//                switch (status) {
//                    case "01": {
//                        for (InvoiceHistoryBean item : alTempInvoiceBean) {
//                            if (item.getInvoiceStatus().equalsIgnoreCase("01"))
//                                alInvoiceBean.add(item);
//                        }
//                    }
//                    break;
//
//                    case "02": {
//                        for (InvoiceHistoryBean item : alTempInvoiceBean) {
//                            if (item.getInvoiceStatus().equalsIgnoreCase("02"))
//                                alInvoiceBean.add(item);
//                        }
//                    }
//                    break;
//
//                    case "03": {
//                        for (InvoiceHistoryBean item : alTempInvoiceBean) {
//                            if (item.getInvoiceStatus().equalsIgnoreCase("03"))
//                                alInvoiceBean.add(item);
//                        }
//                    }
//                    break;
//
//                }
//
//
//            }
//
//            NewInvoiceHistoryActivity.this.invoiceHisListAdapter = new NewInvoiceHisListAdapter( NewInvoiceHistoryActivity.this, R.layout.activity_invoice_history_list,alInvoiceBean,bundleExtras);
//            lv_inv_his_list.setEmptyView(findViewById(R.id.tv_empty_lay) );
//            lv_inv_his_list.setAdapter(invoiceHisListAdapter);
//            NewInvoiceHistoryActivity.this.invoiceHisListAdapter.notifyDataSetChanged();
//
//        } catch (OfflineODataStoreException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//
//    /*get different status for invoices*/
//    private  void getInvStatus(){
//        try{
////            String mStrConfigQry = Constants.ValueHelps + "?$filter="+ Constants.PropName+" eq '"+ Constants.PaymentStatusID+"' and " + Constants.EntityType+" eq '"+ Constants.SSInvoice+"' &$orderby="+ Constants.ID+"%20asc";
//            String mStrConfigQry = Constants.ConfigTypesetTypes + "?$filter="+ Constants.Typeset+" eq '"+
//                    Constants.INVST+ "' &$orderby="+ Constants.Types+" asc";
//
//            arrayInvStatusVal = OfflineManager.getConfigTysetTypesValues(mStrConfigQry);
//        } catch (OfflineODataStoreException e) {
//            LogManager.writeLogError(Constants.Error+" : " + e.getMessage());
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.menu_invoice_his_list, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//
//
//            case R.id.menu_refresh_inv:
//                onRefresh();
//                break;
//            case android.R.id.home:
//                onBackPressed();
//                break;
//        }
//        return false;
//    }
//
//    /*Refresh Invoice list from backEnd*/
//    void onRefresh()
//    {
//        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
//            alAssignColl.clear();
//            concatCollectionStr="";
//            alAssignColl.add(Constants.InvoiceItemDetails);
//            alAssignColl.add(Constants.INVOICES);
//            for (int incVal = 0; incVal < alAssignColl.size(); incVal++) {
//                if (incVal == 0 && incVal == alAssignColl.size() - 1) {
//                    concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
//                } else if (incVal == 0) {
//                    concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
//                } else if (incVal == alAssignColl.size() - 1) {
//                    concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
//                } else {
//                    concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
//                }
//            }
//
//            if (Constants.iSAutoSync) {
//                UtilConstants.showAlert(getString(R.string.alert_auto_sync_is_progress),NewInvoiceHistoryActivity.this);
//            } else {
//                try {
//                    Constants.isSync = true;
//                    dialogCancelled = false;
//                    new LoadingData().execute();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//        } else {
//            UtilConstants.showAlert(getString(R.string.no_network_conn),NewInvoiceHistoryActivity.this);
//        }
//    }
//
//    /*AsyncTask to refresh Invoices from backend*/
//    public class LoadingData extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected void onCancelled(Void aVoid) {
//            super.onCancelled(aVoid);
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            syncProgDialog = new ProgressDialog(NewInvoiceHistoryActivity.this, R.style.ProgressDialogTheme);
//            syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
//            syncProgDialog.setCancelable(true);
//            syncProgDialog.setCanceledOnTouchOutside(false);
//            syncProgDialog.show();
//
//            syncProgDialog
//                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
//                        @Override
//                        public void onCancel(DialogInterface Dialog) {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(
//                                    NewInvoiceHistoryActivity.this, R.style.MyTheme);
//                            builder.setMessage(R.string.do_want_cancel_sync)
//                                    .setCancelable(false)
//                                    .setPositiveButton(
//                                            R.string.yes,
//                                            new DialogInterface.OnClickListener() {
//                                                public void onClick(
//                                                        DialogInterface Dialog,
//                                                        int id) {
//                                                    dialogCancelled = true;
//
//                                                    onBackPressed();
//                                                }
//                                            })
//                                    .setNegativeButton(
//                                            R.string.no,
//                                            new DialogInterface.OnClickListener() {
//                                                public void onClick(
//                                                        DialogInterface Dialog,
//                                                        int id) {
//
//                                                    try {
//                                                        syncProgDialog
//                                                                .show();
//                                                        syncProgDialog
//                                                                .setCancelable(true);
//                                                        syncProgDialog
//                                                                .setCanceledOnTouchOutside(false);
//                                                    } catch (Exception e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                    dialogCancelled = false;
//                                                }
//                                            });
//                            builder.show();
//                        }
//                    });
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                Thread.sleep(1000);
//                try {
//
//                    OfflineManager.refreshStoreSync(getApplicationContext(), NewInvoiceHistoryActivity.this, Constants.Fresh, concatCollectionStr);
//                } catch (OfflineODataStoreException e) {
//                    e.printStackTrace();
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//        }
//    }
//
//    @Override
//    public void onRequestError(int operation, Exception e) {
//        ErrorBean errorBean = Constants.getErrorCode(operation, e, NewInvoiceHistoryActivity.this);
//        if (errorBean.hasNoError()) {
//            if (dialogCancelled == false && !Constants.isStoreClosed) {
//                if (operation == Operation.OfflineRefresh.getValue()) {
//                    try {
//                        String syncTime = Constants.getSyncHistoryddmmyyyyTime();
//                        for (int incReq = 0; incReq < alAssignColl.size(); incReq++) {
//
//                            String colName = alAssignColl.get(incReq);
//                            if (colName.contains("?$")) {
//                                String splitCollName[] = colName.split("\\?");
//                                colName = splitCollName[0];
//                            }
//
//                            Constants.events.updateStatus(Constants.SYNC_TABLE,
//                                    colName, Constants.TimeStamp, syncTime
//                            );
//                        }
//                    } catch (Exception exce) {
//                        LogManager.writeLogError(Constants.SyncTableHistory + exce.getMessage());
//                    }
//
//                    syncProgDialog.dismiss();
//                    Constants.isSync = false;
//                    if (!Constants.isStoreClosed) {
//                        UtilConstants.showAlert(getString(R.string.msg_error_occured_during_sync), NewInvoiceHistoryActivity.this);
//
//
//                    } else {
//                        UtilConstants.showAlert(getString(R.string.msg_sync_terminated), NewInvoiceHistoryActivity.this);
//                    }
//                }
//            }
//        } else {
//            Constants.isSync = false;
//            syncProgDialog.dismiss();
//            Constants.displayMsgReqError(errorBean.getErrorCode(), NewInvoiceHistoryActivity.this);
//        }
//    }
//
//    @Override
//    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {
//        if (dialogCancelled == false && !Constants.isStoreClosed) {
//            if (operation == Operation.OfflineRefresh.getValue()) {
//                try {
//                    OfflineManager.getAuthorizations(getApplicationContext());
//                } catch (OfflineODataStoreException e) {
//                    e.printStackTrace();
//                }
//
//
//
//                try {
//                    String syncTime = Constants.getSyncHistoryddmmyyyyTime();
//                    for (int incReq = 0; incReq < alAssignColl.size(); incReq++) {
//                        String colName = alAssignColl.get(incReq);
//                        if (colName.contains("?$")) {
//                            String splitCollName[] = colName.split("\\?");
//                            colName = splitCollName[0];
//                        }
//
//                        Constants.events.updateStatus(Constants.SYNC_TABLE,
//                                colName, Constants.TimeStamp, syncTime
//                        );
//                    }
//                } catch (Exception exce) {
//                    LogManager.writeLogError(Constants.SyncTableHistory + exce.getMessage());
//                }
//
//                tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.SFINVOICES, Constants.TimeStamp,this));
//
//
//                syncProgDialog.dismiss();
//                Constants.isSync = false;
//                if (!Constants.isStoreClosed) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(
//                            NewInvoiceHistoryActivity.this, R.style.MyTheme);
//                    builder.setMessage(getString(R.string.msg_sync_successfully_completed))
//                            .setCancelable(false)
//                            .setPositiveButton(getString(R.string.ok),
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog,
//                                                            int id) {
//                                            getInvStatus();
//                                            getStatus();
//                                        }
//                                    });
//
//                    builder.show();
//                } else {
//                    UtilConstants.showAlert(getString(R.string.msg_sync_terminated), NewInvoiceHistoryActivity.this);
//                }
//            }
//        }
//    }
//}
//
