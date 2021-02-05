package com.rspl.sf.msfa.visit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.CustomerDetailsActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.MyUtils;
import com.rspl.sf.msfa.interfaces.DialogCallBack;

import java.util.Calendar;
import java.util.Hashtable;

public class TechnicalTeamCustomerMainMenuActivity extends AppCompatActivity {

    TextView textViewTodayDate;
    Spinner spinnerActivityConducted;
    private String dateSelected = "";
    private String customerNum = "", customerName = "", customerNo = "";
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    String mStrComingFrom = "";
    String[] typeOfConstructionStringArray = {"01 - Mason","02 -Engineer","03 - Consumer and Other meet"};
    ArrayAdapter<String> spinnerActivityConductedAdapter;
    String activityConductedSelectedItem="";
    private int mYear;
    private int mMonth;
    private int mDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technical_team_customer_main_menu);

       // ActionBarView.initActionBarView(this,true,"Activity");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_activity), 0);
        initializeUI();
    }
    private void initializeUI(){

        textViewTodayDate =(TextView) findViewById(R.id.textViewTodayDate);
        spinnerActivityConducted =(Spinner) findViewById(R.id.spinnerActivityConducted);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            customerNo = extra.getString(Constants.CPNo);
            customerName = extra.getString(Constants.RetailerName);
            //            customerDetail = extra.getBoolean("CustomerDetail");
            mStrBundleRetID = extra.getString(Constants.CPNo);
            mStrBundleRetName = extra.getString(Constants.RetailerName);
            mStrComingFrom = extra.getString(Constants.comingFrom);

        }
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
        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);
        retName.setText(customerName);
        retId.setText(customerNo);
//        textViewTodayDate.setText(String.valueOf(dateSelected));
        textViewTodayDate.setText(new StringBuilder().append(mDay).append("-")
                .append(Constants.ORG_MONTHS[mMonth]).append("-").append("")
                .append(mYear));
        spinnerActivityConductedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,typeOfConstructionStringArray);

        spinnerData();
    }
    private void spinnerData(){
        spinnerActivityConducted.setAdapter(spinnerActivityConductedAdapter);
        spinnerActivityConducted.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                @Override
                                                                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                                                           int arg2, long arg3) {
                                                                    activityConductedSelectedItem = spinnerActivityConducted.getSelectedItem().toString();
                                                                    // TODO Auto-generated method stub
                                                                }
                                                                @Override
                                                                public void onNothingSelected(AdapterView<?> arg0) {
                                                                    // TODO Auto-generated method stub

                                                                }

                                                            }
        );

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_price_info_save, menu);
        return true;
    }
    private boolean validateTradeDetails(){
        if(activityConductedSelectedItem.trim().length()<=0){
            showAlertDialog("Please select activity conducted");
            return false;
        }else{
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_save:
                //next step
                if (validateTradeDetails()){
                    saveData();
                }

                break;
        }
        return true;
    }
    public void saveData(){
        try {
            Hashtable<String, String> hashtable = new Hashtable<>();
            hashtable.put(Constants.TradeDate, UtilConstants.getNewDate());
            hashtable.put(Constants.CustomerName, customerName);
            hashtable.put(Constants.CustomerNo, customerNo);
            hashtable.put(Constants.ActivityConducted, activityConductedSelectedItem);
            hashtable.put(Constants.TechnicalDate, dateSelected);

            Log.e("TRADE INFO",hashtable.toString());
            Constants.events.insert(Constants.TRADE_INFO_CUSTOMER_TECH_TEAM_TABLE, hashtable);

            MyUtils.dialogBoxWithButton(this, "",  getString(R.string.activity_created_successfully),getString(R.string.msg_ok), "", new DialogCallBack() {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(TechnicalTeamCustomerMainMenuActivity.this, R.style.MyTheme);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(TechnicalTeamCustomerMainMenuActivity.this, R.style.MyTheme);
        builder.setMessage("Do you want to exit from activity update").setCancelable(false)
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
        Intent intentNavPrevScreen = new Intent(TechnicalTeamCustomerMainMenuActivity.this,CustomerDetailsActivity.class);
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
}
