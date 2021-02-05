package com.rspl.sf.msfa.orderinfocreate;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.CustomerDetailsActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.MyUtils;
import com.rspl.sf.msfa.interfaces.DialogCallBack;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

public class OrderInfoCreate extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{


    private EditText etOrdertoReceive = null;
    private EditText etAmountReceive1 = null;
    private EditText etAmountReceive2 = null;
    private EditText etAmountReceive3 = null;
    private EditText etAmountReceive4 = null;


    static final int DATE_DIALOG_ID = 0;

    static final int DATE_DIALOG_ID1 = 1;

    static final int DATE_DIALOG_ID2 = 2;

    static final int DATE_DIALOG_ID3 = 3;

    static final int DATE_DIALOG_ID4 = 4;
    private String dateSelected = "";
    private TextView tvdispatchDate;
    private TextView tvpaymentDate1;
    private TextView tvpaymentDate2;
    private TextView tvpaymentDate3;
    private TextView tvpaymentDate4;
    private int mYear;
    private int mMonth;
    private int mDay;

    private String customerNum = "", customerName = "", customerNo = "";
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    String mStrComingFrom = "";

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


            tvdispatchDate.setText(new StringBuilder().append(mDay).append("-")
                    .append(Constants.ORG_MONTHS[mMonth]).append("-").append("")
                    .append(mYear));
        }
    };


    private DatePickerDialog.OnDateSetListener mDateSetListener1 = new DatePickerDialog.OnDateSetListener() {
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


                tvpaymentDate1.setText(new StringBuilder().append(mDay).append("-")
                        .append(Constants.ORG_MONTHS[mMonth]).append("-").append("")
                        .append(mYear));
        }
    };


    private DatePickerDialog.OnDateSetListener mDateSetListener2 = new DatePickerDialog.OnDateSetListener() {
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


                tvpaymentDate2.setText(new StringBuilder().append(mDay).append("-")
                        .append(Constants.ORG_MONTHS[mMonth]).append("-").append("")
                        .append(mYear));
        }
    };



    private DatePickerDialog.OnDateSetListener mDateSetListener3 = new DatePickerDialog.OnDateSetListener() {
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


                tvpaymentDate3.setText(new StringBuilder().append(mDay).append("-")
                        .append(Constants.ORG_MONTHS[mMonth]).append("-").append("")
                        .append(mYear));
        }
    };



    private DatePickerDialog.OnDateSetListener mDateSetListener4 = new DatePickerDialog.OnDateSetListener() {
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


                tvpaymentDate4.setText(new StringBuilder().append(mDay).append("-")
                        .append(Constants.ORG_MONTHS[mMonth]).append("-").append("")
                        .append(mYear));
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info_create);
       //ActionBarView.initActionBarView(this,true,"Order Info");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_OrderInfo), 0);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            customerNo = extra.getString(Constants.CPNo);
            customerName = extra.getString(Constants.RetailerName);
            //            customerDetail = extra.getBoolean("CustomerDetail");
            mStrBundleRetID = extra.getString(Constants.CPNo);
            mStrBundleRetName = extra.getString(Constants.RetailerName);
            mStrComingFrom = extra.getString(Constants.comingFrom);

        }

        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);
        retName.setText(customerName);
        retId.setText(customerNo);

        etOrdertoReceive = (EditText) findViewById(R.id.sp_order_info_recevive);
        etAmountReceive1 = (EditText) findViewById(R.id.edit_so_info_amount1);
        etAmountReceive2 = (EditText) findViewById(R.id.edit_so_info_amount2);
        etAmountReceive3 = (EditText) findViewById(R.id.edit_so_info_amount3);
        etAmountReceive4 = (EditText) findViewById(R.id.edit_so_info_amount4);
        tvdispatchDate = (TextView) findViewById(R.id.tv_orders_date_date_of_dispatch);
        tvpaymentDate1 = (TextView) findViewById(R.id.tv_so_info_date1);
        tvpaymentDate2 = (TextView) findViewById(R.id.tv_so_info_date2);
        tvpaymentDate3 = (TextView) findViewById(R.id.tv_so_info_date3);
        tvpaymentDate4 = (TextView) findViewById(R.id.tv_so_info_date4);


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
//        dateSelected = mYear + "-" + mon + "-" + day;
        dateSelected = "";

        tvdispatchDate.setText("");

        tvdispatchDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        tvpaymentDate1.setText("");

        tvpaymentDate1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID1);
            }
        });


        tvpaymentDate2.setText("");
        tvpaymentDate2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID2);
            }
        });


        tvpaymentDate3.setText("");
        tvpaymentDate3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID3);
            }
        });


        tvpaymentDate4.setText("");

        tvpaymentDate4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID4);
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                DatePickerDialog dialog = null;
                    dialog = new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
                    dialog.getDatePicker().setMinDate(new Date().getTime()-10000);
                return dialog;

            case DATE_DIALOG_ID1:
                DatePickerDialog dialog1 = new DatePickerDialog(this, mDateSetListener1, mYear, mMonth, mDay);
                dialog1.getDatePicker().setMinDate(new Date().getTime()-10000);
                return dialog1;


            case DATE_DIALOG_ID2:
                DatePickerDialog dialog2 = new DatePickerDialog(this, mDateSetListener2, mYear, mMonth, mDay);
                dialog2.getDatePicker().setMinDate(new Date().getTime()-10000);
                return dialog2;


            case DATE_DIALOG_ID3:
                DatePickerDialog dialog3 = new DatePickerDialog(this, mDateSetListener3, mYear, mMonth, mDay);
                dialog3.getDatePicker().setMinDate(new Date().getTime()-10000);
                return dialog3;


            case DATE_DIALOG_ID4:
                DatePickerDialog dialog4 = new DatePickerDialog(this, mDateSetListener4, mYear, mMonth, mDay);
                dialog4.getDatePicker().setMinDate(new Date().getTime()-10000);
                return dialog4;
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_price_info_save, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_save:
                //next step
                if (validateAmountAndDate()){
                    saveData();
                }


                break;

        }
        return true;
    }
    private boolean validateAmountAndDate(){
        if (etAmountReceive1.getText().toString().trim().length()>0 && tvpaymentDate1.getText().toString().trim().length()==0){
            showAlertDialog(getString(R.string.please_select_date_payment_followup));
            return false;
        }else if(tvpaymentDate1.getText().toString().trim().length()>=2&&etAmountReceive1.getText().toString().trim().length()<=0){
            showAlertDialog(getString(R.string.enter_amount));
            return false;
        }else if (etAmountReceive2.getText().toString().trim().length()>0 && tvpaymentDate2.getText().toString().trim().length()==0){
            showAlertDialog(getString(R.string.please_select_date_payment_followup));
            return false;
        }else if (tvpaymentDate2.getText().toString().trim().length()>=2&&etAmountReceive2.getText().toString().trim().length()<=0){
            showAlertDialog(getString(R.string.enter_amount));
            return false;
        }else if(etAmountReceive3.getText().toString().trim().length()>0 && tvpaymentDate3.getText().toString().trim().length()==0){
            showAlertDialog(getString(R.string.please_select_date_payment_followup));
            return false;
        }else if(tvpaymentDate3.getText().toString().trim().length()>=2&&etAmountReceive3.getText().toString().trim().length()<=0){
            showAlertDialog(getString(R.string.enter_amount));
            return false;
        }else if(etAmountReceive4.getText().toString().trim().length()>0 && tvpaymentDate4.getText().toString().trim().length()==0){
            showAlertDialog(getString(R.string.please_select_date_payment_followup));
            return false;
        }else if(tvpaymentDate4.getText().toString().trim().length()>=2&&etAmountReceive4.getText().toString().trim().length()<=0){
            showAlertDialog(getString(R.string.enter_amount));
            return false;
        } else if(etAmountReceive1.getText().toString().trim().length()==0 && tvpaymentDate1.getText().toString().trim().length()==0&&
                etAmountReceive2.getText().toString().trim().length()==0 && tvpaymentDate2.getText().toString().trim().length()==0&&
                etAmountReceive3.getText().toString().trim().length()==0 && tvpaymentDate3.getText().toString().trim().length()==0&&
                etAmountReceive4.getText().toString().trim().length()==0 && tvpaymentDate4.getText().toString().trim().length()==0&&
                etOrdertoReceive.getText().toString().trim().length()==0){
            showAlertDialog(getString(R.string.payment_follow_up_details));
            return false;
        }else{
            return true;
        }
    }
    public void saveData(){
        try {
            Hashtable<String, String> hashtable = new Hashtable<>();
            hashtable.put(Constants.OrderToRecivive, etOrdertoReceive.getText().toString());

            hashtable.put(Constants.AmountOne, etAmountReceive1.getText().toString());
            hashtable.put(Constants.AmountTwo, etAmountReceive2.getText().toString());
            hashtable.put(Constants.AmountThree, etAmountReceive3.getText().toString());
            hashtable.put(Constants.AmountFour, etAmountReceive4.getText().toString());
            hashtable.put(Constants.DateofDispatch, UtilConstants.getNewDate());
            hashtable.put(Constants.DateOne, tvpaymentDate1.getText().toString());
            hashtable.put(Constants.DateTwo, tvpaymentDate2.getText().toString());
            hashtable.put(Constants.DateThree, tvpaymentDate3.getText().toString());
            hashtable.put(Constants.DateFour, tvpaymentDate4.getText().toString());
            hashtable.put(Constants.CustomerNo, customerNo);
            hashtable.put(Constants.CustomerName, customerName);
            Log.e("ORDER CREATE",hashtable.toString());
            Constants.events.insert(Constants.ORDER_INFO_TABLE, hashtable);

            MyUtils.dialogBoxWithButton(this, "",  getString(R.string.order_info_created_successfully),getString(R.string.msg_ok), "", new DialogCallBack() {
                @Override
                public void clickedStatus(boolean clickedStatus) {
                    navigateToRetDetailsActivity();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void showAlertDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderInfoCreate.this, R.style.MyTheme);
        builder.setMessage(message).setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder.show();
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderInfoCreate.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_order_info).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        navigateToRetDetailsActivity();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();
    }
    private void navigateToRetDetailsActivity(){
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(OrderInfoCreate.this,CustomerDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        if(!Constants.OtherRouteNameVal.equalsIgnoreCase("")){
            intentNavPrevScreen.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
            intentNavPrevScreen.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
        }
        startActivity(intentNavPrevScreen);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

    }
}
