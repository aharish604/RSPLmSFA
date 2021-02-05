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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.CustomerDetailsActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.MyUtils;
import com.rspl.sf.msfa.interfaces.DialogCallBack;

import java.util.Hashtable;

public class TradeInfoTechTeamActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
    EditText editTextBgPotential,editTextTotalNonTradePotential,editTextTotalTradePotential;
    private String customerNum = "", customerName = "", customerNo = "";
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    String mStrComingFrom = "";
    Spinner spinnerTypeOfConstruction,spinnerStageOfConstruction;
    CheckBox checkBoxUTCL,checkBoxACC,checkBoxOCL;
    String[] typeOfConstructionStringArray = {"Commercial","Apartment","Bridge"};
    String[] stageOfConstructionStringArray = {"Excavation and timbering","Foundations","Concrete floors", "Reinforced concrete frames","Roofs"};
    ArrayAdapter<String> typeOfConstructionAdapter,stageOfConstructionAdapter;
    String typeOfConstructionSelectedItem="",stageOfConstructionSelectedItem="";
    String utclCheck="",accheck="",oclCheck="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_info_tech_team);

        //ActionBarView.initActionBarView(this,true,getString(R.string.trade_info));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.trade_info), 0);
        initializeUI();
    }
    private void initializeUI(){

        editTextBgPotential =(EditText)findViewById(R.id.editTextBgPotential);
        editTextTotalNonTradePotential =(EditText)findViewById(R.id.editTextTotalNonTradePotential);
        editTextTotalTradePotential =(EditText)findViewById(R.id.editTextTotalTradePotential);
        spinnerTypeOfConstruction =(Spinner) findViewById(R.id.spinnerTypeOfConstruction);
        spinnerStageOfConstruction =(Spinner) findViewById(R.id.spinnerStageOfConstruction);
        checkBoxUTCL =(CheckBox) findViewById(R.id.checkBoxUTCL);
        checkBoxACC =(CheckBox) findViewById(R.id.checkBoxACC);
        checkBoxOCL =(CheckBox) findViewById(R.id.checkBoxOCL);

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

        spinnerData();
    }
    private void spinnerData(){
        typeOfConstructionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,typeOfConstructionStringArray);
        stageOfConstructionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,stageOfConstructionStringArray);
        spinnerTypeOfConstruction.setAdapter(typeOfConstructionAdapter);
        spinnerTypeOfConstruction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        typeOfConstructionSelectedItem = spinnerTypeOfConstruction.getSelectedItem().toString();
                        // TODO Auto-generated method stub
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }

                }
        );
        spinnerStageOfConstruction.setAdapter(stageOfConstructionAdapter);
        spinnerStageOfConstruction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                       stageOfConstructionSelectedItem = spinnerTypeOfConstruction.getSelectedItem().toString();
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
        if(editTextTotalTradePotential.getText().toString().trim().length()==0 &&
                editTextTotalNonTradePotential.getText().toString().trim().length()==0&&
                editTextBgPotential.getText().toString().trim().length()==0&&
                !checkBoxUTCL.isChecked()&&!checkBoxACC.isChecked()&&!checkBoxOCL.isChecked()){
            showAlertDialog(getString(R.string.alert_trade_details));
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
            hashtable.put(Constants.TradePotential,editTextTotalTradePotential.getText().toString());
            hashtable.put(Constants.NonTradePotential, editTextTotalNonTradePotential.getText().toString());
            hashtable.put(Constants.BgPotential, editTextBgPotential.getText().toString());
            hashtable.put(Constants.TypeOfConstruction, typeOfConstructionSelectedItem);
            hashtable.put(Constants.StageOfConstruction, stageOfConstructionSelectedItem);
            hashtable.put(Constants.BrandUTCLCheck, utclCheck);
            hashtable.put(Constants.BrandACCCheck, accheck);
            hashtable.put(Constants.BrandOCLCheck, oclCheck);
            hashtable.put(Constants.ConfigType, "02");

            Log.e("TRADE CUSTOMER INFO",hashtable.toString());
            Constants.events.insert(Constants.TRADE_INFO_TABLE, hashtable);

            MyUtils.dialogBoxWithButton(this, "","Trade Info updated successfully",getString(R.string.msg_ok), "", new DialogCallBack() {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(TradeInfoTechTeamActivity.this, R.style.MyTheme);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(TradeInfoTechTeamActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_trade_info_tech_team).setCancelable(false)
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
        Intent intentNavPrevScreen = new Intent(TradeInfoTechTeamActivity.this,CustomerDetailsActivity.class);
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.checkBoxUTCL:
                if (isChecked){
                    utclCheck ="01";
                }
                break;
            case R.id.checkBoxACC:
                if (isChecked){
                    accheck ="02";
                }
                break;
            case R.id.checkBoxOCL:
                if (isChecked){
                    oclCheck ="03";
                }
                break;
        }
    }
}
