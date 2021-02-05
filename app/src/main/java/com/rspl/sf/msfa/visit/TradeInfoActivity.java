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
import android.widget.EditText;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.CustomerDetailsActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.MyUtils;
import com.rspl.sf.msfa.interfaces.DialogCallBack;

import java.util.Hashtable;

public class TradeInfoActivity extends AppCompatActivity {

    EditText editTextBgPotential,editTextTotalNonTradePotential,editTextTotalTradePotential;

    private String customerNum = "", customerName = "", customerNo = "";
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    String mStrComingFrom = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_info);
       // ActionBarView.initActionBarView(this,true,getString(R.string.trade_info));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.trade_info), 0);
        initializeUI();
    }
    private void initializeUI(){

        editTextBgPotential =(EditText)findViewById(R.id.editTextBgPotential);
        editTextTotalNonTradePotential =(EditText)findViewById(R.id.editTextTotalNonTradePotential);
        editTextTotalTradePotential =(EditText)findViewById(R.id.editTextTotalTradePotential);

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
                editTextBgPotential.getText().toString().trim().length()==0){
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
            hashtable.put(Constants.TypeOfConstruction, "");
            hashtable.put(Constants.StageOfConstruction, "");
            hashtable.put(Constants.BrandUTCLCheck, "");
            hashtable.put(Constants.BrandACCCheck, "");
            hashtable.put(Constants.BrandOCLCheck, "");
            hashtable.put(Constants.ConfigType, "01");

            Log.e("TRADE INFO",hashtable.toString());
            Constants.events.insert(Constants.TRADE_INFO_TABLE, hashtable);

            MyUtils.dialogBoxWithButton(this, "",  "Trade Info updated successfully",getString(R.string.msg_ok), "", new DialogCallBack() {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(TradeInfoActivity.this, R.style.MyTheme);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(TradeInfoActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_trade_info).setCancelable(false)
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
        Intent intentNavPrevScreen = new Intent(TradeInfoActivity.this,CustomerDetailsActivity.class);
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
