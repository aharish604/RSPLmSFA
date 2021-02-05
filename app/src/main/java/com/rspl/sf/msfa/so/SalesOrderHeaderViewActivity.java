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
import com.rspl.sf.msfa.CustomerDetailsActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.MyUtils;
import com.rspl.sf.msfa.mbo.MaterialFright;
import com.rspl.sf.msfa.mbo.MeansOfTransport;
import com.rspl.sf.msfa.mbo.ProcessingField;
import com.rspl.sf.msfa.mbo.SaleDistrictBean;
import com.rspl.sf.msfa.mbo.StorageLocBean;
import com.rspl.sf.msfa.reports.ForwardingAgentActivity;
import com.rspl.sf.msfa.reports.RouteSelectionActivity;
import com.rspl.sf.msfa.reports.SalesDistrictActivity;
import com.rspl.sf.msfa.reports.ShipToPartyListActivty;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SalesOrderHeaderViewActivity extends AppCompatActivity {
    static final int DATE_DIALOG_ID = 0;
    static final int PRICING_DATE_DIALOG_ID = 1;
    static final int DELIVERY_DATE_DIALOG_ID = 2;
    static final int ShipTo_CATEGROIZE_REQUEST = 1;
    static final int FORWARDINGAGENT_CATEGROIZE_REQUEST = 2;
    static final int SALESDISTRICT_CATEGROIZE_REQUEST = 3;
    static final int ROUTE_CATEGROIZE_REQUEST = 4;
    protected String selOrderTypeDesc = "";
    protected String selIncoTerms = "";
    protected String selIncoTermsDesc = "";
    protected String selIncoTerms1 = "";
    protected String salesorg = "";
    protected String disChannel = "";
    protected String division = "";
    protected String selShippingPointDesc = "";
    protected String selPaymentTermDesc = "";
    private String customerNum = "", customerName = "", customerNo = "", shipToNum = "", shipToName = "";
    private Spinner spSalesArea, spPlant, spShippingPoint, spPaymentTerms, spIncoTerms, spOrderType, spShipTO, spMeansOfTrans, spSalesDist, spProcessingField, spMatFright, spStorageLoc;
    private TextView tvSoldTo, /*tvShipTo,*/
            etComments;
    private String[][] soldTo, salesAreaValues, plantValues, shippingPoints, shipToValues, incoTerms, paymentTerms, orderTypes;
    private String selSalesArea = "", selSalesAreaDesc = "";
    private String selPlant = "", selPlantDesc = "";
    private String selShippingPoint = "";
    private ImageView btSoldTo, btShipTo, btForwardAgent, btSalesDistrict, btRoute;
    private TextView tvpoDate, tvPricingDate, tvDeliveryDate;
    private int mYear;
    private int mMonth;
    private int mDay;
    private String selPaymentTerm = "";
    private String selOrderType = "";
    private EditText etponum;
    private boolean isValidMandotry;
    private String strComments = "";
    private String dateSelected = "";
    private boolean customerDetail = false;

    private String mStrShipToDesc = "";
    private String mStrShipToId = "";
    private String stAddress = "";

    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    String mStrComingFrom = "";
    private EditText etIncoTerms1 = null;
    boolean singleItemSelectionScreen = false;

    private ArrayList<String> salesDistCode;
    private ArrayList<String> salesDistDesc;
    private ArrayList<String> salesDistCodedesc;

    private ArrayList<String> processingFieldCode;
    private ArrayList<String> processingFieldDesc;
    private ArrayList<String> processingFieldCodedesc;

    private ArrayList<String> transCode;
    private ArrayList<String> transDesc;
    private ArrayList<String> transCodedesc;

    private ArrayList<String> matFrightCode;
    private ArrayList<String> matFrightDesc;
    private ArrayList<String> matFrightCodedesc;
    private String transId = "";
    private String transdesc = "";
    private String saledistId = "";
    private String saledistDesc = "";
    private String matFgtId = "";
    private String matFgtDesc = "";

    private String processFieldId = "";
    private String processFieldDesc = "";

    private String StorageLocCode = "";
    private String StorageLocDesc = "";
    private String selStorageLocCode = "";
    private String selStorageLocDesc = "";

    private ProgressDialog prgressDialog = null;

    private ArrayList<StorageLocBean> storageLoc;
    //private ArrayList<RouteBean> route;


    private ArrayList<String> stoLoc;
    private ArrayList<String> stoDesc;
    private ArrayList<String> stoLocDesc;

    private String selSaleDistCode = "";
    private String selSaleDistDesc = "";
    private String incoterm2 = "";

    private String selProcessingFieldCode = "";
    private String selProcessingFieldDesc = "";

    private String selTransId = "";
    private String selTransDesc = "";
    private String selMatFrightCode = "";
    private String selMatFrightDesc = "";

    private int processingFieldpos;
    private int ordertypepos = 0;
    private int meansOftransportpos;
    private int matFrightpos;
    private int storeLocpos;
    private int incotermpos;
    private int shippingPointPos;
    private int plantPos;

    String mStrShipToQry ="";


    private ArrayList<ProcessingField> processingFieldBeen;
    private ArrayList<SaleDistrictBean> saleDistrictBeen;
    private ArrayList<MeansOfTransport> meansOfTransportArrayList;
    private ArrayList<MaterialFright> matFrightArrayList;

    private String[][] DefaultSalesDistrict = null;

    TextView tvshipTo, tvForwardingAgent, tvSalesDistrict, tvRoute;

    private String selForwardingAgentCode = "";
    private String selForwardingAgentDesc = "";

    private String selRouteCode = "";
    private String selRouteDesc = "";

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

    private DatePickerDialog.OnDateSetListener mPricingDateSetListener = new DatePickerDialog.OnDateSetListener() {
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
            tvPricingDate.setText(new StringBuilder().append(mDay).append("-")
                    .append(Constants.ORG_MONTHS[mMonth]).append("-").append("")
                    .append(mYear));
        }
    };

    private DatePickerDialog.OnDateSetListener mDeliveryDateSetListener = new DatePickerDialog.OnDateSetListener() {
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
            tvDeliveryDate.setText(new StringBuilder().append(mDay).append("-")
                    .append(Constants.ORG_MONTHS[mMonth]).append("-").append("")
                    .append(mYear));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order_header_view);

      //  ActionBarView.initActionBarView(this, true, getString(R.string.title_sales_order_create));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_sales_order_create), 0);
        Bundle extra = getIntent().getExtras();
        tvSoldTo = (TextView) findViewById(R.id.tvSoldToValue);
        spPlant = (Spinner) findViewById(R.id.spPlant);
//        tvShipTo = (TextView) findViewById(R.id.tvShipToValue);
        tvForwardingAgent = (TextView) findViewById(R.id.tvForwardingAgent);
        tvSalesDistrict = (TextView) findViewById(R.id.tvSalesDistrict);
        tvRoute = (TextView) findViewById(R.id.tvRoute);
        spShipTO = (Spinner) findViewById(R.id.sp_ship_to);
        spSalesArea = (Spinner) findViewById(R.id.spSalesArea);
        spShippingPoint = (Spinner) findViewById(R.id.sp_shipping_point);
        spPaymentTerms = (Spinner) findViewById(R.id.spPaymentTerm);
        spIncoTerms = (Spinner) findViewById(R.id.spIncoTerm);
        spOrderType = (Spinner) findViewById(R.id.spOrderType);
        spMeansOfTrans = (Spinner) findViewById(R.id.spMeanstransp);
        spProcessingField = (Spinner) findViewById(R.id.spProcessingField);
        spMatFright = (Spinner) findViewById(R.id.spMaterialFright);
        spStorageLoc = (Spinner) findViewById(R.id.spStorageLoc);

        etIncoTerms1 = (EditText) findViewById(R.id.et_incoterms2);

        btForwardAgent = (ImageView) findViewById(R.id.btForwardingAgent);
        btRoute = (ImageView) findViewById(R.id.btRoute);

        btShipTo = (ImageView) findViewById(R.id.btShipTo);
        btSoldTo = (ImageView) findViewById(R.id.btSoldTo);
        btSalesDistrict = (ImageView) findViewById(R.id.btSalesDistrict);
        tvshipTo = (TextView) findViewById(R.id.tvShipToValue);

        tvpoDate = (TextView) findViewById(R.id.tvPoDateValue);
        tvPricingDate = (TextView) findViewById(R.id.tvPricingDateValue);
        tvDeliveryDate = (TextView) findViewById(R.id.tvDeliveryDateValue);
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
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

        tvPricingDate.setText(new StringBuilder().append(mDay).append("-")
                .append(Constants.ORG_MONTHS[mMonth]).append("-").append("")
                .append(mYear));

        tvDeliveryDate.setText(new StringBuilder().append(mDay).append("-")
                .append(Constants.ORG_MONTHS[mMonth]).append("-").append("")
                .append(mYear));
        tvpoDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        tvPricingDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(PRICING_DATE_DIALOG_ID);
            }
        });

        tvDeliveryDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DELIVERY_DATE_DIALOG_ID);
            }
        });

        etponum = (EditText) findViewById(R.id.etpo_num);
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

        etIncoTerms1.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etIncoTerms1.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println("2-->" + s.toString());
                if (!s.toString().equalsIgnoreCase(""))
                    selIncoTerms1 = (s.toString());
                else
                    selIncoTerms1 = "";
            }
        });

//        btSoldTo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent soldto = new Intent(SalesOrderHeaderViewActivity.this, CustomerListActivity.class);
//                soldto.putExtra(Constants.EXTRA_COME_FROM, 2);
//                startActivity(soldto);
//            }
//        });


        btShipTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shipto = new Intent(SalesOrderHeaderViewActivity.this, ShipToPartyListActivty.class);
                shipto.putExtra("custNo", customerNo);
                shipto.putExtra("SalesArea", selSalesArea);
                startActivityForResult(shipto, ShipTo_CATEGROIZE_REQUEST);
            }
        });


        btForwardAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toFrowardingAgent = new Intent(SalesOrderHeaderViewActivity.this, ForwardingAgentActivity.class);
                toFrowardingAgent.putExtra("custNo", customerNo);
                toFrowardingAgent.putExtra("SalesArea", selSalesArea);
                startActivityForResult(toFrowardingAgent, FORWARDINGAGENT_CATEGROIZE_REQUEST);
            }
        });

        btSalesDistrict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toSalesDistrict = new Intent(SalesOrderHeaderViewActivity.this, SalesDistrictActivity.class);
                toSalesDistrict.putExtra("custNo", customerNo);
                toSalesDistrict.putExtra("SalesArea", selSalesArea);
                startActivityForResult(toSalesDistrict, SALESDISTRICT_CATEGROIZE_REQUEST);
            }
        });

        btRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toRoute = new Intent(SalesOrderHeaderViewActivity.this, RouteSelectionActivity.class);
                toRoute.putExtra("custNo", customerNo);
                toRoute.putExtra("SalesArea", selSalesArea);
                startActivityForResult(toRoute, ROUTE_CATEGROIZE_REQUEST);
            }
        });

        if (extra != null) {
            customerNo = extra.getString(Constants.CPNo);
            customerName = extra.getString(Constants.RetailerName);
//            customerDetail = extra.getBoolean("CustomerDetail");

            mStrBundleRetID = extra.getString(Constants.CPNo);
            mStrBundleRetName = extra.getString(Constants.RetailerName);
            mStrComingFrom = extra.getString(Constants.comingFrom);
            singleItemSelectionScreen = extra.getBoolean("SingleSelection");
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
        //    btSoldTo.setVisibility(View.GONE);
//        } else {
//            btSoldTo.setVisibility(View.VISIBLE);
//        }
        if (customerNo.equalsIgnoreCase("")) {
//            String query = Constants.CustPartners + "?$filter= PartnerFunction eq 'SP' &$orderby= Customer";
//            try {
//                soldTo = OfflineManager.getPartnerList(query);
//                if (soldTo[0].length > Constants.selectedIndex) {
//                    if (soldTo != null) {
//                        tvSoldTo.setText(soldTo[2][Constants.selectedIndex]);
//                        customerNum = soldTo[0][Constants.selectedIndex];
//                        customerName = soldTo[1][Constants.selectedIndex];
//                        getSalesArea(customerNum);
//                        getPaymentTerms(customerNum);
//                        getIncoTerms(customerNum);
//                        getPlant(customerNum, selSalesArea);
//                        getShippingPoint(selPlant);
//                        getShipTo(customerNum, selSalesArea);
//                    }
//                } else {
//                    tvSoldTo.setText(soldTo[2][0]);
//                    customerNum = soldTo[0][0];
//                    customerName = soldTo[1][0];
//                    getSalesArea(customerNum);
//                    getPaymentTerms(customerNum);
//                    getIncoTerms(customerNum);
//                    getPlant(customerNum, selSalesArea);
//                    getShippingPoint(selPlant);
//                    getShipTo(customerNum, selSalesArea);
//                }
//            } catch (OfflineODataStoreException e) {
//                e.printStackTrace();
//            }
        } else {
            tvSoldTo.setText(customerName + " - " + customerNo);
            customerNum = customerNo;
            getSalesArea(customerNum);
////            getPaymentTerms(customerNum);
//            getPaymentTerms(selSalesArea);
////            getIncoTerms(customerNum);
//            getIncoTerms(selSalesArea);
//            getPlant(customerNum, selSalesArea);
//            getShippingPoint(selPlant);
//            getShipTo(customerNum, selSalesArea);
        }
       // getOrderTypes();
//        btShipTo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent shipto = new Intent(SalesOrderHeaderViewActivity.this, CustomerListActivity.class);
//                shipto.putExtra("Customer", customerNum);
//                shipto.putExtra("Salesarea", selSalesArea);
//                shipto.putExtra(Constants.EXTRA_COME_FROM, 3);
//                startActivityForResult(shipto, ShipTo_CATEGROIZE_REQUEST);
//
//            }
//        });
    }

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
            if (salesAreaValues != null) {
                ArrayAdapter<CharSequence> salesAreaAdapter = new ArrayAdapter<CharSequence>(this, R.layout.spinner_sales_area,
                        R.id.simple_spinner_dropdown, salesAreaValues[2]) {
                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        return getView(position, convertView, parent);
                    }
                };
                spSalesArea.setAdapter(salesAreaAdapter);
                spSalesArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selSalesArea = salesAreaValues[0][position];
                        selSalesAreaDesc = salesAreaValues[1][position];
                        spSalesArea.setBackgroundResource(R.drawable.spinner_bg);


                        new LoadData(SalesOrderHeaderViewActivity.this).execute();
//                        getPaymentTerms(customerNum, selSalesArea);
//                        getIncoTerms(customerNum, selSalesArea);
//                        getPlant(customerNum, selSalesArea);
////                        getShippingPoint(selPlant);
//                        getShipTo(customerNum, selSalesArea);

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
//            if (paymentTerms != null) {
//                ArrayAdapter<String> paymentTermsAdapter = new ArrayAdapter<>(this, R.layout.custom_textview, paymentTerms[2]);
//                paymentTermsAdapter.setDropDownViewResource(R.layout.spinnerinside);
//                spPaymentTerms.setAdapter(paymentTermsAdapter);
//                spPaymentTerms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                        // TODO Auto-generated method stub
//                        selPaymentTerm = paymentTerms[0][position];
//                        selPaymentTermDesc = paymentTerms[1][position];
//
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> parent) {
//                        // TODO Auto-generated method stub
//
//                    }
//                });
//            }
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
//            if (incoTerms != null) {
//                if (incoTerms != null && incoTerms.length > 0) {
//                    ArrayAdapter<String> incoTermAdapter = new ArrayAdapter<>(this, R.layout.custom_textview, incoTerms[2]);
//                    incoTermAdapter.setDropDownViewResource(R.layout.spinnerinside);
//                    spIncoTerms.setAdapter(incoTermAdapter);
//                    spIncoTerms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            selIncoTerms = incoTerms[0][position];
//                            selIncoTermsDesc = incoTerms[1][position];
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent) {
//                        }
//                    });
//                }
//            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
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
            }
//            if (plantValues != null) {
////                ArrayAdapter<String> plantAdapter = new ArrayAdapter<>(this, R.layout.custom_textview, plantValues[2]);
////                plantAdapter.setDropDownViewResource(R.layout.spinnerinside);
//                ArrayAdapter<CharSequence> plantAdapter = new ArrayAdapter<CharSequence>(this, R.layout.spinner_sales_area,
//                        R.id.simple_spinner_dropdown, plantValues[2]) {
//                    @Override
//                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
//                        return getView(position, convertView, parent);
//                    }
//                };
//                spPlant.setAdapter(plantAdapter);
//                spPlant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
//                        selPlant = plantValues[0][position];
//                        selPlantDesc = plantValues[1][position];
//                        getShippingPoint(selPlant);
//                        getStorageLoc(selPlant);
//                        spPlant.setBackgroundResource(R.drawable.spinner_bg);
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> arg0) {
//
//                    }
//                });
//            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * get shipping points
     *
     * @param selPlant
     */
    private void getShippingPoint(String selPlant) {

//        String query = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" +
//                Constants.ShippingTypeID + "' and " + Constants.ParentID + " eq '" + selSalesArea +
//                "' and "+Constants.PartnerNo+" eq '"+customerNum+"' &$orderby = Description asc";

        String query = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" +
                Constants.ShippingTypeID + "' &$orderby = Description asc";


        // String query = Constants.ShippingPoints + "?$filter= Plant eq '" + selPlant + "'";
        try {
            shippingPoints = OfflineManager.getShipPointList(query);
            if (shippingPoints != null) {
                try {
                    ArrayAdapter<String> shippingPointAdapter = new ArrayAdapter<>(this, R.layout.custom_textview, shippingPoints[2]);
                    shippingPointAdapter.setDropDownViewResource(R.layout.spinnerinside);
                    spShippingPoint.setAdapter(shippingPointAdapter);
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

            mStrShipToId = shipToValues[0][0];
            mStrShipToDesc = shipToValues[1][0];



        } catch (Exception e) {
            e.printStackTrace();
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

        if (requestCode == FORWARDINGAGENT_CATEGROIZE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == 2) {
                selForwardingAgentCode = data.getStringExtra("ForwardingAgentCode");
                selForwardingAgentDesc = data.getStringExtra("ForwardingAgentDesc");
                tvForwardingAgent.setText(selForwardingAgentCode + " - " + selForwardingAgentDesc);


            }
        }

        if (requestCode == SALESDISTRICT_CATEGROIZE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == 3) {
                selSaleDistCode = data.getStringExtra("SalesDistrictCode");
                selSaleDistDesc = data.getStringExtra("SalesDistrictDesc");
                tvSalesDistrict.setText(selSaleDistCode + " - " + selSaleDistDesc);


            }
        }

        if (requestCode == ROUTE_CATEGROIZE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == 4) {
                selRouteCode = data.getStringExtra("RouteCode");
                selRouteDesc = data.getStringExtra("RouteDesc");
                tvRoute.setText(selRouteCode + " - " + selRouteDesc);


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
            headerDetail.put(Constants.OrderNo, "");
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
            headerDetail.put(Constants.PlantDesc, selPlantDesc);
//            headerDetail.put("Plant", "1000");
            headerDetail.put("ShippingPoint", selShippingPoint);
            headerDetail.put("ShippingPointDesc", selShippingPointDesc);
            headerDetail.put("OrderType", selOrderType);
            headerDetail.put("OrderTypeDesc", selOrderTypeDesc);
            headerDetail.put("PODate", dateSelected + "T00:00:00");
            headerDetail.put("PODate1", tvpoDate.getText().toString());
            headerDetail.put("PaymentTerm", selPaymentTerm);
            headerDetail.put(Constants.PaytermDesc, selPaymentTermDesc);
            headerDetail.put("IncoTerm1", selIncoTerms);
            headerDetail.put(Constants.Incoterm1Desc, selIncoTermsDesc);
            headerDetail.put("IncoTerm2", selIncoTerms1);
            headerDetail.put("Remarks", strComments);
            headerDetail.put("SalesOrg", salesorg);
            headerDetail.put("DistChannel", disChannel);
            headerDetail.put("Division", division);

            headerDetail.put("ForwardingAgent", selForwardingAgentCode);
            headerDetail.put("Plant", selPlant);
            headerDetail.put(Constants.SalesDist, selSaleDistCode);
            headerDetail.put(Constants.Route, selRouteCode);
            headerDetail.put("MaterialFrieght", selMatFrightCode);
            headerDetail.put(Constants.MeansOfTranstyp, selTransId);
            headerDetail.put("StorageLoc", selStorageLocCode);
            headerDetail.put("ProcessingField", selProcessingFieldCode);
            if (!etponum.getText().toString().equalsIgnoreCase(""))
                headerDetail.put("PONo", etponum.getText().toString());
            else
                headerDetail.put("PONo", "");

            if(singleItemSelectionScreen){

                Intent soitem = new Intent(SalesOrderHeaderViewActivity.this, SoSingleItemActivity.class);
                soitem.putExtra("SalesArea", selSalesArea);
//            soitem.putExtra("SalesArea", "1000/10/00");
                soitem.putExtra("Header", headerDetail);
                soitem.putExtra(Constants.CPNo, mStrBundleRetID);
                soitem.putExtra(Constants.RetailerName, mStrBundleRetName);
                soitem.putExtra(Constants.comingFrom, mStrComingFrom);
                startActivity(soitem);
            }else{

                Intent soitem = new Intent(SalesOrderHeaderViewActivity.this, SOItemActivity.class);
                soitem.putExtra("SalesArea", selSalesArea);
//            soitem.putExtra("SalesArea", "1000/10/00");
                soitem.putExtra("Header", headerDetail);
                soitem.putExtra(Constants.CPNo, mStrBundleRetID);
                soitem.putExtra(Constants.RetailerName, mStrBundleRetName);
                soitem.putExtra(Constants.comingFrom, mStrComingFrom);
                startActivity(soitem);

            }

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


            case PRICING_DATE_DIALOG_ID:
                DatePickerDialog pricedialog = new DatePickerDialog(this, mPricingDateSetListener, mYear, mMonth, mDay);
                pricedialog.getDatePicker().setMaxDate(new Date().getTime());
                return pricedialog;


            case DELIVERY_DATE_DIALOG_ID:
                DatePickerDialog deliverydialog = new DatePickerDialog(this, mDeliveryDateSetListener, mYear, mMonth, mDay);
                deliverydialog.getDatePicker().setMaxDate(new Date().getTime());
                return deliverydialog;
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

//        if (!selIncoTerms1.trim().equalsIgnoreCase("")) ;
//        else {
//            etIncoTerms1.setBackgroundResource(R.drawable.edittext_border);
//            isValidMandotry = true;
//        }

        if (mStrShipToId.equalsIgnoreCase("") || mStrShipToId.equalsIgnoreCase(Constants.None)) {
            spShipTO.setBackgroundResource(R.drawable.error_spinner);
            isValidMandotry = true;
        }

        if (selOrderType.equalsIgnoreCase("") || selOrderType.equalsIgnoreCase(Constants.None)) {
            spOrderType.setBackgroundResource(R.drawable.error_spinner);
            isValidMandotry = true;
        }

        if (selSalesArea.equalsIgnoreCase("") || selSalesArea.equalsIgnoreCase(Constants.None)) {
            spSalesArea.setBackgroundResource(R.drawable.error_spinner);
            isValidMandotry = true;
        }

        if (selPlant.equalsIgnoreCase("") || selPlant.equalsIgnoreCase(Constants.None)) {
            spPlant.setBackgroundResource(R.drawable.error_spinner);
            isValidMandotry = true;
        }
//        if(selIncoTerms.equalsIgnoreCase("")|| selIncoTerms.equalsIgnoreCase(Constants.None)){
//            spIncoTerms.setBackgroundResource(R.drawable.error_spinner);
//            isValidMandotry = true;
//        }
//
        if (selPaymentTerm.equalsIgnoreCase("") || selPaymentTerm.equalsIgnoreCase(Constants.None)) {
            spPaymentTerms.setBackgroundResource(R.drawable.error_spinner);
            isValidMandotry = true;
        }
        if (etponum.getText().toString().equalsIgnoreCase("") || etponum.getText().toString().equalsIgnoreCase(Constants.None)) {
            etponum.setBackgroundResource(R.drawable.edittext_border);
            isValidMandotry = true;
        }

        return isValidMandotry;
    }

    //    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SalesOrderHeaderViewActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_create_sales_order).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        onNavigateToCustDetilsActivity();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();
    }

    /**
     * change another activity
     */
    private void onNavigateToCustDetilsActivity() {
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(SalesOrderHeaderViewActivity.this, CustomerDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleRetID);
        if (!Constants.OtherRouteNameVal.equalsIgnoreCase("")) {
            intentNavPrevScreen.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
            intentNavPrevScreen.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
        }
        startActivity(intentNavPrevScreen);
    }


    private void getProcessingField() {
        boolean defaultIncoDine = false;

//       String query = Constants.CustSlsAreas + "?$filter= CustomerNumber eq '" + customerNum + "'"+ "' and SalesArea eq '" + selSalesArea + "'";

        String query = Constants.CustSlsAreas + "?$filter= CustomerNumber eq '" + customerNum + "'";

        try {
            DefaultSalesDistrict = OfflineManager.getDefaultSalesDistrictList(query);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }


        String query1 = Constants.ValueHelps + "?$filter= PropName eq '" + "SPLProceIND" + "'";
        ;
        try {
            processingFieldBeen = OfflineManager.getProcessingFieldList(query1);

            processingFieldCode = new ArrayList<>();
            processingFieldDesc = new ArrayList<>();
            processingFieldCodedesc = new ArrayList<>();
            processingFieldCode.add("");
            processingFieldDesc.add("");
            processingFieldCodedesc.add("");


            // int j=0;
            for (int i = 0; i < processingFieldBeen.size(); i++) {

//                if (!defaultIncoDine) {
//                    salesDistCode.add(DefaultSalesDistrict[0][i]);
//                    salesDistDesc.add(DefaultSalesDistrict[1][i]);
//                    salesDistCodedesc.add(DefaultSalesDistrict[2][i]);
//                    defaultIncoDine = true;
//                }else{
                ProcessingField bean = processingFieldBeen.get(i);
                processFieldId = bean.getProcessingFieldCode().toString();
                processFieldDesc = bean.getProcessingFieldDesc().toString();
                processingFieldCode.add(processFieldId);
                processingFieldDesc.add(processFieldDesc);
                processingFieldCodedesc.add(processFieldDesc + " - " + processFieldId);
                // j++;
                //    }

            }


            if (processingFieldBeen != null) {
                ArrayAdapter<String> plantAdapter = new ArrayAdapter<>(this, R.layout.custom_textview, processingFieldCodedesc);
                plantAdapter.setDropDownViewResource(R.layout.spinnerinside);
                spProcessingField.setAdapter(plantAdapter);
                spProcessingField.setSelection(processingFieldpos);
                spProcessingField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id) {

                        processingFieldpos = position;
                        if (processingFieldCode.size() > 0) {
                            selProcessingFieldCode = processingFieldCode.get(position).toString();
                            selProcessingFieldDesc = processingFieldCode.get(position).toString();
                        } else {

                            selProcessingFieldCode = "";
                            selProcessingFieldDesc = "";
                        }


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getMeansOfTrans() {


        String query = Constants.ValueHelps + "?$filter= PropName eq '" + "MeansOfTranstyp" + "'";
        try {
            meansOfTransportArrayList = OfflineManager.getMeansOftrans(query);

            transCode = new ArrayList<>();
            transDesc = new ArrayList<>();
            transCodedesc = new ArrayList<>();
//            routeCode.add("330902");
//            routeCode.add("330961");
//            routeDesc.add("CG-ABHANPUR");
//            routeDesc.add("CG-RAIPUR(1)");
//            routeCodedesc.add("CG-ABHANPUR - 330902");
//            routeCodedesc.add("CG-RAIPUR(1) - 330961");


            for (int i = 0; i < meansOfTransportArrayList.size(); i++) {

                MeansOfTransport bean = meansOfTransportArrayList.get(i);
                transId = bean.getTransportId().toString();
                transdesc = bean.getTransportDesc().toString();
                transCode.add(transId);
                transDesc.add(transdesc);
                transCodedesc.add(transdesc + " - " + transId);
            }


            if (meansOfTransportArrayList != null) {
                ArrayAdapter<String> plantAdapter = new ArrayAdapter<>(this, R.layout.custom_textview, transCodedesc);
                plantAdapter.setDropDownViewResource(R.layout.spinnerinside);
                spMeansOfTrans.setAdapter(plantAdapter);
                spMeansOfTrans.setSelection(meansOftransportpos);
                spMeansOfTrans.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id) {

                        meansOftransportpos = position;
                        if (transCode.size() > 0) {
                            selTransId = transCode.get(position).toString();
                            selTransDesc = transDesc.get(position).toString();
                        } else {
                            selTransId = "";
                            selTransDesc = "";
                        }


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getMatFright() {

        String query = Constants.ValueHelps + "?$filter= PropName eq '" + "MatFrgtGrp" + "'";
        //String query = Constants.CONFIGURATIONS + "?$filter= Type eq '" + "MTFTGP" + "'&$orderby=Value";
        try {
            matFrightArrayList = OfflineManager.getMatFright(query);

            matFrightCode = new ArrayList<>();
            matFrightDesc = new ArrayList<>();
            matFrightCodedesc = new ArrayList<>();

            for (int i = 0; i < matFrightArrayList.size(); i++) {

                MaterialFright bean = matFrightArrayList.get(i);
                matFgtId = bean.getMatFrightCode().toString();
                matFgtDesc = bean.getMatFrightDesc().toString();
                matFrightCode.add(matFgtId);
                matFrightDesc.add(matFgtDesc);
                matFrightCodedesc.add(matFgtDesc + " - " + matFgtId);
            }


            if (matFrightArrayList != null) {
                ArrayAdapter<String> matFrtAdapter = new ArrayAdapter<>(this, R.layout.custom_textview, matFrightCodedesc);
                matFrtAdapter.setDropDownViewResource(R.layout.spinnerinside);
                spMatFright.setAdapter(matFrtAdapter);
                spMatFright.setSelection(matFrightpos);
                spMatFright.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id) {

                        matFrightpos = position;
                        if (matFrightCode.size() > 0) {
                            selMatFrightCode = matFrightCode.get(position).toString();
                            selMatFrightDesc = matFrightDesc.get(position).toString();
                        } else {
                            selMatFrightCode = "";
                            selMatFrightDesc = "";
                        }


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getStorageLoc() {

        stoLoc = new ArrayList<>();
        stoDesc = new ArrayList<>();
        stoLocDesc = new ArrayList<>();
        //String query = Constants.PlantStorLocs + "?$filter= Plant eq '" + selPlant + "'";
        String query = Constants.ValueHelps + "?$filter= PropName eq '" + "StorageLoc" + "' and ParentID eq '"+selPlant +"' &$orderby = Description asc";;
        //String query = Constants.CONFIGURATIONS + "?$filter= Type eq '" + "STRLOC" + "'";
        try {
            storageLoc = OfflineManager.getPlantBasedStorageLocList(query);

            //storageLoc = OfflineDataManager.getStorageLocList(query);

            for (int i = 0; i < storageLoc.size(); i++) {

                StorageLocBean bean = storageLoc.get(i);
                StorageLocCode = bean.getStoLocCode().toString();
                StorageLocDesc = bean.getStoLocDesc().toString();
                stoLoc.add(StorageLocCode);
                stoDesc.add(StorageLocDesc);
                stoLocDesc.add(StorageLocDesc + " - " + StorageLocCode);
            }

            if (storageLoc != null) {
                ArrayAdapter<String> plantAdapter = new ArrayAdapter<>(this, R.layout.custom_textview, stoLocDesc);
                plantAdapter.setDropDownViewResource(R.layout.spinnerinside);
                spStorageLoc.setAdapter(plantAdapter);
                spStorageLoc.setSelection(storeLocpos);
                spStorageLoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id) {

//                        Object item = parent.getItemAtPosition(position);
                        storeLocpos = position;
                        if (stoLoc.size() > 0 && stoDesc.size() > 0) {

                            selStorageLocCode = stoLoc.get(position).toString();
                            selStorageLocDesc = stoDesc.get(position).toString();
                        }


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

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

            getPaymentTerms(customerNum, selSalesArea);
            getIncoTerms(customerNum, selSalesArea);
            getPlant(customerNum, selSalesArea);
//                        getShippingPoint(selPlant);

            getShipTo(customerNum, selSalesArea);

            getOrderTypes();

            getMatFright();
            getMeansOfTrans();

            getProcessingField();


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
                tvshipTo.setText("");

            }

            if (paymentTerms != null) {
                ArrayAdapter<String> paymentTermsAdapter = new ArrayAdapter<>(SalesOrderHeaderViewActivity.this, R.layout.custom_textview, paymentTerms[2]);
                paymentTermsAdapter.setDropDownViewResource(R.layout.spinnerinside);
                spPaymentTerms.setAdapter(paymentTermsAdapter);
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
                    ArrayAdapter<String> incoTermAdapter = new ArrayAdapter<>(SalesOrderHeaderViewActivity.this, R.layout.custom_textview, incoTerms[2]);
                    incoTermAdapter.setDropDownViewResource(R.layout.spinnerinside);
                    spIncoTerms.setAdapter(incoTermAdapter);
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
                ArrayAdapter<String> orderTypesAdapter = new ArrayAdapter<>(SalesOrderHeaderViewActivity.this, R.layout.custom_textview, orderTypes[2]);
                orderTypesAdapter.setDropDownViewResource(R.layout.spinnerinside);
                spOrderType.setAdapter(orderTypesAdapter);
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
                ArrayAdapter<CharSequence> plantAdapter = new ArrayAdapter<CharSequence>(SalesOrderHeaderViewActivity.this, R.layout.spinner_sales_area,
                        R.id.simple_spinner_dropdown, plantValues[2]) {
                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        return getView(position, convertView, parent);
                    }
                };
                plantAdapter.setDropDownViewResource(R.layout.spinnerinside);
                spPlant.setAdapter(plantAdapter);
                spPlant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                        selPlant = plantValues[0][position];
                        selPlantDesc = plantValues[1][position];
                        getShippingPoint(selPlant);
                       getStorageLoc();
                        spPlant.setBackgroundResource(R.drawable.spinner_bg);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                });
            }

            if (matFrightArrayList != null) {
                ArrayAdapter<String> matFrtAdapter = new ArrayAdapter<>(SalesOrderHeaderViewActivity.this, R.layout.custom_textview, matFrightCodedesc);
                matFrtAdapter.setDropDownViewResource(R.layout.spinnerinside);
                spMatFright.setAdapter(matFrtAdapter);
                spMatFright.setSelection(matFrightpos);
                spMatFright.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id) {

                        matFrightpos = position;
                        if (matFrightCode.size() > 0) {
                            selMatFrightCode = matFrightCode.get(position).toString();
                            selMatFrightDesc = matFrightDesc.get(position).toString();
                        } else {
                            selMatFrightCode = "";
                            selMatFrightDesc = "";
                        }


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                });
            }

            if (meansOfTransportArrayList != null) {
                ArrayAdapter<String> plantAdapter = new ArrayAdapter<>(SalesOrderHeaderViewActivity.this, R.layout.custom_textview, transCodedesc);
                plantAdapter.setDropDownViewResource(R.layout.spinnerinside);
                spMeansOfTrans.setAdapter(plantAdapter);
                spMeansOfTrans.setSelection(meansOftransportpos);
                spMeansOfTrans.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id) {

                        meansOftransportpos = position;
                        if (transCode.size() > 0) {
                            selTransId = transCode.get(position).toString();
                            selTransDesc = transDesc.get(position).toString();
                        } else {
                            selTransId = "";
                            selTransDesc = "";
                        }


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                });
            }

            if (processingFieldBeen != null) {
                ArrayAdapter<String> plantAdapter = new ArrayAdapter<>(SalesOrderHeaderViewActivity.this, R.layout.custom_textview, processingFieldCodedesc);
                plantAdapter.setDropDownViewResource(R.layout.spinnerinside);
                spProcessingField.setAdapter(plantAdapter);
                spProcessingField.setSelection(processingFieldpos);
                spProcessingField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id) {

                        processingFieldpos = position;
                        if (processingFieldCode.size() > 0) {
                            selProcessingFieldCode = processingFieldCode.get(position).toString();
                            selProcessingFieldDesc = processingFieldDesc.get(position).toString();
                        } else {

                            selProcessingFieldCode = "";
                            selProcessingFieldDesc = "";
                        }


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                });
            }


        }

    }

}
