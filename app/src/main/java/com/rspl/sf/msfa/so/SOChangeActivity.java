package com.rspl.sf.msfa.so;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.MyUtils;
import com.rspl.sf.msfa.mbo.SalesOrderBean;
import com.rspl.sf.msfa.reports.ShipToPartyListActivty;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by e10742 on 6/6/2017.
 */

public class SOChangeActivity extends AppCompatActivity {
    static final int DATE_DIALOG_ID = 0;
    static final int ShipTo_CATEGROIZE_REQUEST = 1;
    protected String selOrderTypeDesc = "";
    protected String selIncoTerms1 = "";
    protected String selIncoTerms1Desc = "";
    protected String selIncoTerms2 = "";
    protected String salesorg = "";
    protected String disChannel = "";
    protected String division = "";
    protected String selShippingPointDesc = "";
    protected String selPaymentTermDesc = "";
    private String customerNum = "", customerName = "", customerNo = "", shipToNum = "", shipToName = "";
    private Spinner spPaymentTerms, spIncoTerms, spShipTO, spShippingPoint,spSalesArea, spPlant,spOrderType;
    private TextView tvSalesArea, tvPlant, tvOrderType;
    private TextView tvSoldTo, /*tvShipTo,*/
            etComments;
    private String[][] soldTo, shipToValues, incoTerms, paymentTerms;
    private String[][] plantValues,orderTypes,salesAreaValues,shippingPoints;
    private String selSalesArea = "", selSalesAreaDesc = "";
    private String selPlant = "", selPlantDesc = "",selCustPoNo = "";
    private String selShippingPoint = "";
    private ImageView btSoldTo, btShipTo;
    private TextView tvpoDate;
    private int mYear;
    private int mMonth;
    private int mDay;
    private String selPaymentTerm = "";
    private ProgressDialog prgressDialog = null;
    private String selOrderType = "";
    private EditText etponum,etincoTerm1;
    private boolean isValidMandotry;
    private String strComments = "";
    private String dateSelected = "";
    private boolean customerDetail = false;

    private String mStrShipToDesc = "";
    private String mStrShipToId = "";
    private String stAddress = "";

    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    String mStrComingFrom = "",mStrBundleDate = "";
    TextView tvshipTo;
    protected String selIncoTerms = "";
    protected String selIncoTermsDesc = "";
    private ArrayList<SalesOrderBean> salesOrderBeenList=new ArrayList<>();

    int orderTypeSpPos = 0,salesAreaSpPos = 0,shippingPtSpPos =0,plantSpPos = 0,incoTermSpPos = 0,paymentTermSpPos = 0;


    String orderNo = "";
    SalesOrderBean headerSOBean = new SalesOrderBean();

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;

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
            dateSelected = mYear + "-" + mon + "-" + day;
            tvpoDate.setText(new StringBuilder().append(mDay).append("-")
                    .append(Constants.ORG_MONTHS[mMonth]).append("-").append("")
                    .append(mYear));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_so_change);
        //ActionBarView.initActionBarView(this, true, getString(R.string.title_sales_order_change));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_sales_order_change), 0);
        Bundle extra = getIntent().getExtras();

        tvSoldTo = (TextView) findViewById(R.id.tvSoldToValue);

        spShipTO = (Spinner) findViewById(R.id.sp_ship_to);


        spPaymentTerms = (Spinner) findViewById(R.id.spPaymentTerm);
        spShipTO = (Spinner) findViewById(R.id.sp_ship_to);
        spSalesArea = (Spinner) findViewById(R.id.spSalesArea);
        spShippingPoint = (Spinner) findViewById(R.id.sp_shipping_point);
        spPaymentTerms = (Spinner) findViewById(R.id.spPaymentTerm);
        spIncoTerms = (Spinner) findViewById(R.id.spIncoTerm);
        spOrderType = (Spinner) findViewById(R.id.spOrderType);
        spPlant = (Spinner) findViewById(R.id.spPlant);

        btShipTo = (ImageView) findViewById(R.id.btShipTo);
        btSoldTo = (ImageView) findViewById(R.id.btSoldTo);

        tvpoDate = (TextView) findViewById(R.id.tvPoDateValue);
        tvshipTo = (TextView) findViewById(R.id.tvShipToValue);

        etponum = (EditText) findViewById(R.id.etpo_num);
        etincoTerm1 = (EditText) findViewById(R.id.et_incoterms2);

        tvpoDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        etComments = (EditText) findViewById(R.id.etComments);
        etComments.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etComments.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println("2-->" + s.toString());
                if (!s.toString().equalsIgnoreCase(""))
                    strComments = (s.toString());
                else
                    strComments = "";
            }
        });

        if (extra != null) {
            salesOrderBeenList= (ArrayList<SalesOrderBean>) getIntent().getSerializableExtra("items");
            orderNo = extra.getString(Constants.OrderNo);
            customerNo = extra.getString(Constants.CPNo);
            customerName = extra.getString(Constants.RetailerName);
            mStrBundleRetID = extra.getString(Constants.CPNo);
            mStrBundleRetName = extra.getString(Constants.RetailerName);
            mStrComingFrom = extra.getString(Constants.comingFrom);
            mStrBundleDate = extra.getString(Constants.EXTRA_ORDER_DATE);

            selOrderType = extra.getString(Constants.ORDER_TYPE);

            selSalesArea = extra.getString(Constants.SALESAREA);


            selShippingPoint = extra.getString(Constants.SHIPPINTPOINT);

            mStrShipToId = extra.getString(Constants.SHIPTO);
            mStrShipToDesc = extra.getString(Constants.SHIPTONAME);


            selPlant = extra.getString(Constants.PLANT);

            selIncoTerms1 = extra.getString(Constants.INCOTERM1);

            selIncoTerms2 = extra.getString(Constants.INCOTERM2);


            selPaymentTerm = extra.getString(Constants.Payterm);
            selCustPoNo = extra.getString(Constants.CUSTOMERPO);

            strComments = extra.getString(Constants.Remarks);


            String defaultDate = extra.getString(Constants.CUSTOMERPODATE);

            if (defaultDate != null && defaultDate.length() >= 10) {
                String[] dateValues = defaultDate.split("/");
                mYear = Integer.parseInt(dateValues[2]);
                mMonth = Integer.parseInt(dateValues[1])-1;
                mDay = Integer.parseInt(dateValues[0]);
            } else {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
            }

            String mon = "";
            String day = "";
            int mnt = 0;
            if (defaultDate != null && defaultDate.length() >= 10)
                mnt = mMonth + 1;
            if (mnt < 10)
                mon = "0" + mnt;
            else
                mon = "" + mnt;
            day = "" + mDay;
            if (mDay < 10)
                day = "0" + mDay;
            dateSelected = mYear + "-" + mon + "-" + day;

        }
        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);
        retName.setText(customerName);
        retId.setText(customerNo);

        if (customerNo == null)
            customerNo = "";
        if (customerName == null)
            customerName = "";
//        if (customerDetail) {
        btSoldTo.setVisibility(View.GONE);
//        } else {
//            btSoldTo.setVisibility(View.VISIBLE);
//        }
        if (customerNo.equalsIgnoreCase("")) {
        } else {
            tvSoldTo.setText(customerName + " - " + customerNo);
            customerNum = customerNo;

            getSalesArea(customerNum);


        }

                btShipTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shipto = new Intent(SOChangeActivity.this, ShipToPartyListActivty.class);
                shipto.putExtra("custNo", customerNo);
                shipto.putExtra("SalesArea", selSalesArea);
                startActivityForResult(shipto, ShipTo_CATEGROIZE_REQUEST);
            }
        });




    }

    public void getSODetails() {
        String soEntityQry = Constants.SOs + "?$filter=" + Constants.SONo + " eq '" + orderNo + "'";
        try {
            headerSOBean = OfflineManager.getSalesOrder(soEntityQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        selSalesArea = headerSOBean.getSalesArea();
        selSalesAreaDesc = headerSOBean.getSalesAreaDesc();

        selPlant = headerSOBean.getPlant();
        selPlantDesc = headerSOBean.getPlantDesc();

        selOrderType = headerSOBean.getOrderType();
        selOrderTypeDesc = headerSOBean.getOrderTypeDesc();

        selShippingPoint = headerSOBean.getShippingPoint();
        selShippingPointDesc = headerSOBean.getShippingPointDesc();

        selIncoTerms = headerSOBean.getIncoTerm1();
        selIncoTerms2 = headerSOBean.getIncoterm1Desc();

        selPaymentTerm = headerSOBean.getPaymentTerm();
        selPaymentTermDesc = headerSOBean.getPaymentTermDesc();

        strComments = headerSOBean.getRemarks();

        String defaultDate = headerSOBean.getPODate();

        if (defaultDate != null && defaultDate.length() >= 10) {
            String[] dateValues = defaultDate.split("/");
            mYear = Integer.parseInt(dateValues[2]);
            mMonth = Integer.parseInt(dateValues[1])-1;
            mDay = Integer.parseInt(dateValues[0]);
        } else {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
        }

        String mon = "";
        String day = "";
        int mnt = 0;
        if (defaultDate != null && defaultDate.length() >= 10)
        mnt = mMonth + 1;
        if (mnt < 10)
            mon = "0" + mnt;
        else
            mon = "" + mnt;
        day = "" + mDay;
        if (mDay < 10)
            day = "0" + mDay;
        dateSelected = mYear + "-" + mon + "-" + day;

        tvpoDate.setText(new StringBuilder().append(mDay).append("-")
                .append(Constants.ORG_MONTHS[mMonth]).append("-").append("")
                .append(mYear));
        tvpoDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
    }

    void displayDefaultValues() {


        etComments.setText(headerSOBean.getRemarks());
        etponum.setText(headerSOBean.getPONo());

//        setDefaultValToSpinner();
}

    void setDefaultValToSpinner() {
        for (int i = 0; i < shipToValues[0].length; i++) {
            if (shipToValues[2][i].equalsIgnoreCase(headerSOBean.getShipTo())) {
                spShipTO.setSelection(i);
                break;
            }
        }

        for (int i = 0; i < incoTerms[0].length; i++) {
            if (incoTerms[0][i].equalsIgnoreCase(headerSOBean.getIncoTerm1())) {
                spIncoTerms.setSelection(i);
                break;
            }
        }

        for (int i = 0; i < paymentTerms[0].length; i++) {
            if (paymentTerms[0][i].equalsIgnoreCase(headerSOBean.getPaymentTerm())) {
                spPaymentTerms.setSelection(i);
                break;
            }
        }
    }



    /**
     * get plant
     *
     * @param customerNum
     * @param selSalesArea
     */
    private void getPlant(String customerNum, String selSalesArea) {
//        String query = Constants.CustSlsAreas + "?$filter= CustomerNumber eq '" + customerNum + "' and SalesArea eq '" + selSalesArea + "'";
        String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" +
                Constants.Plant + "' and " + Constants.ParentID + " eq '" + selSalesArea +
                "' and " + Constants.PartnerNo + " eq '" + customerNum + "' &$orderby = Description asc";
        try {
            plantValues = OfflineManager.getPaymentList(mStrConfigQry);
            if (plantValues == null || (plantValues[1].length == 1 && plantValues[1][0].equalsIgnoreCase(Constants.None))) {
                String mStrConfigQry1 = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" +
                        Constants.Plant + "' and " + Constants.ParentID + " eq '" + selSalesArea +
                        "' &$orderby = Description asc";
                plantValues = OfflineManager.getPaymentList(mStrConfigQry1);


                for(int i=0 ; i<plantValues[2].length;i++){

                    String temp = plantValues[0][i];
                    if( selPlant.equals(temp)){



                            plantSpPos = i;

                        break;
                    }

                }
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * get payment terms list
     *
     * @param mStrSalesAreaId
     */

    private void getPaymentTerms(String customerNum, String mStrSalesAreaId) {
//        String query = Constants.CustSlsAreas + "?$filter= CustomerNumber eq '" + customerNum + "'";
        String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" +
                Constants.Payterm + "' and " + Constants.ParentID + " eq '" + mStrSalesAreaId +
                "' and " + Constants.PartnerNo + " eq '" + customerNum + "' &$orderby = Description asc";
        try {
            paymentTerms = OfflineManager.getPaymentList(mStrConfigQry);
            if (paymentTerms == null || (paymentTerms[1].length == 1 && paymentTerms[1][0].equalsIgnoreCase(Constants.None))) {
                String mStrConfigQry1 = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" +
                        Constants.Payterm + "' and " + Constants.ParentID + " eq '" + mStrSalesAreaId +
                        "' &$orderby = Description asc";
                paymentTerms = OfflineManager.getPaymentList(mStrConfigQry1);
            }



            for(int i=0 ; i<paymentTerms[2].length;i++){

                String temp = paymentTerms[0][i];
                if( selPaymentTerm.equals(temp)){

                        paymentTermSpPos = i;



                    break;
                }

            }


        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * get inco terms
     *
     * @param mStrSalesAreaId
     */
    private void getIncoTerms(String customerNum, String mStrSalesAreaId) {
//        String query = Constants.CustSlsAreas + "?$filter= CustomerNumber eq '" + customerNum + "'";
        String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" +
                Constants.Incoterm1 + "' and " + Constants.ParentID + " eq '" + mStrSalesAreaId +
                "' and " + Constants.PartnerNo + " eq '" + customerNum + "' &$orderby = Description asc";
        try {
            incoTerms = OfflineManager.getPaymentList(mStrConfigQry);
            if (incoTerms == null || (incoTerms[1].length == 1 && incoTerms[1][0].equalsIgnoreCase(Constants.None))) {
                String mStrConfigQry1 = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" +
                        Constants.Incoterm1 + "' and " + Constants.ParentID + " eq '" + mStrSalesAreaId +
                        "' &$orderby = Description asc";
                incoTerms = OfflineManager.getPaymentList(mStrConfigQry1);
            }

            for(int i=0 ; i<incoTerms[2].length;i++){

                String temp = incoTerms[0][i];
                if( selIncoTerms.equals(temp)){



                        incoTermSpPos = i;

                    break;
                }

            }


        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * get shipping points
     *
     *  //selShippingPoint
     */
    private void getShippingPoint() {



        String query = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" +
                Constants.ShippingTypeID + "' &$orderby = Description asc";



        try {
            shippingPoints = OfflineManager.getShipPointList(query);


            for(int i=0 ; i<shippingPoints[2].length;i++){

                String temp = shippingPoints[0][i];
                if( selShippingPoint.equals(temp)){


                        shippingPtSpPos = i;


                    break;
                }

            }


        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

    }



    /**
     * get ship to list
     *
     * @param customerNum
     * @param selSalesArea
     */
    private void getShipTo(String customerNum, String selSalesArea) {
        String mStrConfigQry = Constants.CustomerPartnerFunctions + "?$filter=" + Constants.CustomerNo +
                " eq '" + customerNum + "' and SalesArea eq '" + selSalesArea +
                "' and PartnerFunctionID eq 'SH' &$orderby = PartnerCustomerName asc";
        try {
            shipToValues = OfflineManager.getCustomerPartnerDataFunction(mStrConfigQry);

//            mStrShipToId = shipToValues[0][0];
//            mStrShipToDesc = shipToValues[1][0];



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_back_next, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ShipTo_CATEGROIZE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == 1) {
                shipToNum = data.getStringExtra("ShipToCode");
                shipToName = data.getStringExtra("ShipToDesc");
                mStrShipToId = shipToNum;
                mStrShipToDesc = shipToName;
                tvshipTo.setText(mStrShipToDesc + " - " + mStrShipToId);


            }
        }
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

        }
        return true;
    }

    /**
     * next step
     */
    private void onNext() {
        if (!getvalues()) {
            salesorg = selSalesArea.substring(0, selSalesArea.indexOf("/"));
            String distChannel = selSalesArea.substring(selSalesArea.indexOf("/") + 1, selSalesArea.length());
            disChannel = distChannel.substring(0, distChannel.indexOf("/"));
            division = distChannel.substring(distChannel.indexOf("/") + 1, distChannel.length());
            HashMap<String, String> headerDetail = new HashMap<>();
            headerDetail.put(Constants.OrderNo, orderNo);
            headerDetail.put(Constants.OrderDate, mStrBundleDate);
            headerDetail.put("SoldTo", customerNum);
            headerDetail.put("SoldToName", customerName);
//            headerDetail.put("ShipToName", shipToName);
            headerDetail.put("ShipToName", mStrShipToDesc);
//            headerDetail.put("ShipTo", shipToNum);
            headerDetail.put("ShipTo", mStrShipToId);
            headerDetail.put("SalesArea", selSalesArea);
//            headerDetail.put("SalesArea", "1000/10/00");
            headerDetail.put("SalesAreaDesc", selSalesAreaDesc);
            headerDetail.put("Plant", selPlant);
            headerDetail.put("PlantDesc", selPlantDesc);
//            headerDetail.put("Plant", "1000");
            headerDetail.put("ShippingPoint", selShippingPoint);
            headerDetail.put("ShippingPointDesc", selShippingPointDesc);
            headerDetail.put("OrderType", selOrderType);
            headerDetail.put("OrderTypeDesc", selOrderTypeDesc);
            headerDetail.put("PODate", dateSelected + "T00:00:00");
            headerDetail.put("PODate1", tvpoDate.getText().toString());
            headerDetail.put("PaymentTerm", selPaymentTerm);
            headerDetail.put(Constants.PaytermDesc, selPaymentTermDesc);
            headerDetail.put("IncoTerm1", selIncoTerms1);
            headerDetail.put(Constants.Incoterm1Desc, selIncoTerms1Desc);
            headerDetail.put("IncoTerm2", selIncoTerms2);
            headerDetail.put("Remarks", strComments);
            headerDetail.put("SalesOrg", salesorg);
            headerDetail.put("DistChannel", disChannel);
            headerDetail.put("Division", division);
            if (!etponum.getText().toString().equalsIgnoreCase(""))
                headerDetail.put("PONo", etponum.getText().toString());
            else
                headerDetail.put("PONo", "");
            Intent soitem;
            if (!mStrComingFrom.equalsIgnoreCase("reports")){
                soitem = new Intent(SOChangeActivity.this, SOItemActivity.class);
                soitem.putExtra(Constants.comingFrom, Constants.VisitNavigationFrom);
            }else{
                soitem = new Intent(SOChangeActivity.this, SOEditQuantityActivity.class);
                soitem.putExtra("SOItemDetails",salesOrderBeenList);
                soitem.putExtra(Constants.comingFrom, mStrComingFrom);
            }
            soitem.putExtra("SalesArea", selSalesArea);
//            soitem.putExtra("SalesArea", "1000/10/00");
            soitem.putExtra("Header", headerDetail);
            soitem.putExtra(Constants.CPNo, mStrBundleRetID);
            soitem.putExtra(Constants.RetailerName, mStrBundleRetName);
            soitem.putExtra(Constants.comingFromChange, true);
            startActivity(soitem);
        } else {
            MyUtils.dialogBoxWithButton(this, "", "Required field cannot be left blank.", "Ok", "", null);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                DatePickerDialog dialog = new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                return dialog;
        }
        return null;
    }

    /**
     * check validation
     *
     * @return
     */
    private boolean getvalues() {
        // TODO Auto-generated method stub
        isValidMandotry = false;
//        if (!selShippingPoint.trim().equalsIgnoreCase(""))
//            selShippingPoint = selShippingPoint;
//        else
//            isValidMandotry = true;
        if (!strComments.trim().equalsIgnoreCase("")) ;
        else {
            etComments.setBackgroundResource(R.drawable.edittext_border);
            isValidMandotry = true;
        }

        if (mStrShipToId.equalsIgnoreCase("") || mStrShipToId.equalsIgnoreCase(Constants.None)) {
            spShipTO.setBackgroundResource(R.drawable.error_spinner);
            isValidMandotry = true;
        }

        return isValidMandotry;
    }
    boolean isBackPressed = false;

    //    @Override
    public void onBackPressed() {
        if(isBackPressed)
            super.onBackPressed();
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(SOChangeActivity.this, R.style.MyTheme);
            builder.setMessage(R.string.alert_exit_change_sales_order).setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            isBackPressed = true;
                            onBackPressed();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }

                    });
            builder.show();
        }
    }

    /**
     * get order types list
     */
    private void getOrderTypes() {
//        String query = Constants.CONFIGURATIONS + "?$filter= Type eq 'ORDTYP'";
        String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" +
                Constants.OrderType + "' &$orderby = Description asc";
        try {
            orderTypes = OfflineManager.getPaymentList(mStrConfigQry);



            for(int i=0 ; i<orderTypes[2].length;i++){

                String temp = orderTypes[0][i];
                if( selOrderType.equals(temp)){

                        orderTypeSpPos = i;

                    break;
                }

            }
//            if (orderTypes != null) {
//                ArrayAdapter<String> orderTypesAdapter = new ArrayAdapter<>(this, R.layout.custom_textview, orderTypes[2]);
//                orderTypesAdapter.setDropDownViewResource(R.layout.spinnerinside);
//                spOrderType.setAdapter(orderTypesAdapter);
//                spOrderType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                        selOrderType = orderTypes[0][position];
//                        selOrderTypeDesc = orderTypes[1][position];
//                        spOrderType.setBackgroundResource(R.drawable.spinner_bg);
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> parent) {
//
//                    }
//                });
//            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

    }

//    /**
//     * change another activity
//     */
//    private void onNavigateToCustDetilsActivity() {
//        Constants.ComingFromCreateSenarios = Constants.X;
//        Intent intentNavPrevScreen = new Intent(SOChangeActivity.this, CustomerDetailsActivity.class);
//        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
//        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
//        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetID);
//        intentNavPrevScreen.putExtra(Constants.comingFrom, Constants.VisitNavigationFrom);
//        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleRetID);
//        if (!Constants.OtherRouteNameVal.equalsIgnoreCase("")) {
//            intentNavPrevScreen.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
//            intentNavPrevScreen.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
//        }
//        startActivity(intentNavPrevScreen);
//    }



    /**
     * get sales area
     *
     * @param customerNum
     */
    private void getSalesArea(final String customerNum) {
        String query = Constants.CustomerSalesAreas + "?$filter= CustomerNo eq '" + customerNum + "'";
//        String query = Constants.CustomerSalesAreas + "?$filter= CustomerNo eq '0000001000'";
        try {
            salesAreaValues = OfflineManager.getSalesAreaList(query);


            for(int i=0 ; i<salesAreaValues[2].length;i++){

                String temp = salesAreaValues[0][i];
                if( selSalesArea.equals(temp)){

                            salesAreaSpPos = i;


                    break;
                }

            }

            if (salesAreaValues != null) {
                ArrayAdapter<CharSequence> salesAreaAdapter = new ArrayAdapter<CharSequence>(SOChangeActivity.this, R.layout.spinner_sales_area,
                        R.id.simple_spinner_dropdown, salesAreaValues[2]) {
                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        return getView(position, convertView, parent);
                    }
                };
                spSalesArea.setAdapter(salesAreaAdapter);
                spSalesArea.setSelection(salesAreaSpPos);
                spSalesArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selSalesArea = salesAreaValues[0][position];
                        selSalesAreaDesc = salesAreaValues[1][position];
                        new LoadData(SOChangeActivity.this).execute();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub

                    }
                });
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
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
            prgressDialog = MyUtils.showProgressDialog(mContext, "", mContext.getString(R.string.app_loading));


        }

        @Override
        protected Void doInBackground(Void... params) {



          //  getSODetails();
            getOrderTypes();
            getPaymentTerms(customerNum, selSalesArea);
            getPlant(customerNum, selSalesArea);
            getIncoTerms(customerNum, selSalesArea);
            getShipTo(customerNum, selSalesArea);
            getShippingPoint();
//
//            getOrderTypes();
//
//            getMatFright();
//            getMeansOfTrans();
//
//            getProcessingField();


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (prgressDialog != null) {
                prgressDialog.dismiss();
            }

            if(shipToValues.length ==1){
                btShipTo.setVisibility(View.INVISIBLE);
                tvshipTo.setText(mStrShipToDesc + " - " + mStrShipToId);

            }else{

                btShipTo.setVisibility(View.VISIBLE);
                tvshipTo.setText(mStrShipToDesc + " - " + mStrShipToId);

            }
            etincoTerm1.setText(selIncoTerms2);
            etponum.setText(selCustPoNo);
            tvpoDate.setText(dateSelected);
            if (paymentTerms != null) {
                ArrayAdapter<String> paymentTermsAdapter = new ArrayAdapter<>(SOChangeActivity.this, R.layout.custom_textview, paymentTerms[2]);
                paymentTermsAdapter.setDropDownViewResource(R.layout.spinnerinside);
                spPaymentTerms.setAdapter(paymentTermsAdapter);
                spPaymentTerms.setSelection(paymentTermSpPos);
                spPaymentTerms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // TODO Auto-generated method stub
                        selPaymentTerm = paymentTerms[0][position];
                        selPaymentTermDesc = paymentTerms[1][position];

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub

                    }
                });
            }


            if (incoTerms != null) {
                if (incoTerms != null && incoTerms.length > 0) {
                    ArrayAdapter<String> incoTermAdapter = new ArrayAdapter<>(SOChangeActivity.this, R.layout.custom_textview, incoTerms[2]);
                    incoTermAdapter.setDropDownViewResource(R.layout.spinnerinside);
                    spIncoTerms.setAdapter(incoTermAdapter);
                    spIncoTerms.setSelection(incoTermSpPos);
                    spIncoTerms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selIncoTerms = incoTerms[0][position];
                            selIncoTermsDesc = incoTerms[1][position];
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
            }



            if (orderTypes != null) {
                ArrayAdapter<String> orderTypesAdapter = new ArrayAdapter<>(SOChangeActivity.this, R.layout.custom_textview, orderTypes[2]);
                orderTypesAdapter.setDropDownViewResource(R.layout.spinnerinside);
                spOrderType.setAdapter(orderTypesAdapter);
                spOrderType.setSelection(orderTypeSpPos);
                spOrderType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selOrderType = orderTypes[0][position];
                        selOrderTypeDesc = orderTypes[1][position];
                        spOrderType.setBackgroundResource(R.drawable.spinner_bg);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            if (plantValues != null) {
//                ArrayAdapter<String> plantAdapter = new ArrayAdapter<>(this, R.layout.custom_textview, plantValues[2]);
//                plantAdapter.setDropDownViewResource(R.layout.spinnerinside);
                ArrayAdapter<CharSequence> plantAdapter = new ArrayAdapter<CharSequence>(SOChangeActivity.this, R.layout.spinner_sales_area,
                        R.id.simple_spinner_dropdown, plantValues[2]) {
                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        return getView(position, convertView, parent);
                    }
                };
                plantAdapter.setDropDownViewResource(R.layout.spinnerinside);
                spPlant.setAdapter(plantAdapter);
                spPlant.setSelection(plantSpPos);
                spPlant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                        selPlant = plantValues[0][position];
                        selPlantDesc = plantValues[1][position];

//                        spPlant.setBackgroundResource(R.drawable.spinner_bg);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                });
            }

            if (shippingPoints != null) {
                try {
                    ArrayAdapter<String> shippingPointAdapter = new ArrayAdapter<>(SOChangeActivity.this, R.layout.custom_textview, shippingPoints[2]);
                    shippingPointAdapter.setDropDownViewResource(R.layout.spinnerinside);
                    spShippingPoint.setAdapter(shippingPointAdapter);
                    spShippingPoint.setSelection(shippingPtSpPos);
                    spShippingPoint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                            selShippingPoint = shippingPoints[0][position];
                            selShippingPointDesc = shippingPoints[1][position];


                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            displayDefaultValues();

        }

    }

}

