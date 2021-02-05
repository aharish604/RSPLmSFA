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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.CustomerDetailsActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.MyUtils;
import com.rspl.sf.msfa.interfaces.DialogCallBack;

import java.util.Hashtable;

public class PopActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
    private String customerNum = "", customerName = "", customerNo = "";
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    String mStrComingFrom = "";
    CheckBox checkBoxDiary,checkBoxChitPad,checkBoxBanner;
    String diaryCheck="",chitPadCheck="",bannerCheck ="";
    EditText editTextDiary,editChitPad,editBanner;
    LinearLayout llDiary,llChitPad,llBanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);

       // ActionBarView.initActionBarView(this, true, getString(R.string.title_pop));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_pop), 0);
        initializeUI();
    }
    private void initializeUI(){

        checkBoxDiary = (CheckBox)findViewById(R.id.checkBoxDiary);
        checkBoxChitPad = (CheckBox)findViewById(R.id.checkBoxChitPad);
        checkBoxBanner = (CheckBox)findViewById(R.id.checkBoxBanner);
        editTextDiary = (EditText) findViewById(R.id.editTextDiary);
        editChitPad = (EditText) findViewById(R.id.editChitPad);
        editBanner = (EditText) findViewById(R.id.editBanner);
        editTextDiary.setTransformationMethod(null);
        editChitPad.setTransformationMethod(null);
        editBanner.setTransformationMethod(null);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            customerNo = extra.getString(Constants.CPNo);
            customerName = extra.getString(Constants.RetailerName);
            mStrBundleRetID = extra.getString(Constants.CPNo);
            mStrBundleRetName = extra.getString(Constants.RetailerName);
            mStrComingFrom = extra.getString(Constants.comingFrom);

        }
        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);
        llDiary = (LinearLayout)findViewById(R.id.ll_TextDiary) ;
        llChitPad = (LinearLayout)findViewById(R.id.ll_ChitPad) ;
        llBanner = (LinearLayout)findViewById(R.id.ll_Banner) ;
        retName.setText(customerName);
        retId.setText(customerNo);
        checkBoxChitPad.setOnCheckedChangeListener(this);
        checkBoxDiary.setOnCheckedChangeListener(this);
        checkBoxBanner.setOnCheckedChangeListener(this);
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
//            case android.R.id.home:
//                onBackPressed();
//                break;

            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_save:
                //next step
//                saveData();
                if (validatingCheckBox()){
//                    showAlertDialog("Data Saved");
//                    if (checkBoxDiary.isChecked()&&editTextDiary.getText().toString().trim().length()==0){
//                        showAlertDialog("Please fill NOs");
//                    }else {
                        saveData();
//                    }
                }
                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PopActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_competition_information).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        PopActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();
    }

    private boolean validatingCheckBox(){
        if(!checkBoxChitPad.isChecked()&&!checkBoxDiary.isChecked()){
            showAlertDialog("Please select atleast one item ");
            return false;
        }else{
            return true;
        }
    }
    private void showAlertDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(PopActivity.this, R.style.MyTheme);
        builder.setMessage(message).setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder.show();
    }
    private void navigateToRetDetailsActivity(){
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(PopActivity.this,CustomerDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
//        if(!Constants.OtherRouteNameVal.equalsIgnoreCase("")){
//            intentNavPrevScreen.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
//            intentNavPrevScreen.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
//        }
        startActivity(intentNavPrevScreen);
    }
    public void saveData(){


        try {
            Hashtable<String, String> hashtable = new Hashtable<>();
            hashtable.put(Constants.DateofDispatch, UtilConstants.getNewDate());
            hashtable.put(Constants.CustomerName, customerName);
            hashtable.put(Constants.CustomerNo, customerNo);
            hashtable.put(Constants.diaryCheck, diaryCheck);
            hashtable.put(Constants.chitPadCheck, chitPadCheck);
            hashtable.put(Constants.bannerCheck, bannerCheck);


            Constants.events.insert(Constants.POP_INFO_TABLE, hashtable);
            Log.e("POP INFO",hashtable.toString());
            MyUtils.dialogBoxWithButton(this, "",  getString(R.string.pop_info), "Ok", "", new DialogCallBack() {
                @Override
                public void clickedStatus(boolean clickedStatus) {
                    navigateToRetDetailsActivity();
                    finish();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.checkBoxDiary:
                if (isChecked){
                    diaryCheck ="01";
                    editTextDiary.setEnabled(true);
                    editTextDiary.setTransformationMethod(null);
                }else{
                    editTextDiary.setEnabled(false);
                    editTextDiary.setText("");
                }
                break;
            case R.id.checkBoxChitPad:
                if (isChecked){
                    chitPadCheck ="01";
                    editChitPad.setEnabled(true);
                }else{
                    editChitPad.setEnabled(false);
                    editChitPad.setText("");

                }
                break;
            case R.id.checkBoxBanner:
                if (isChecked){
                    bannerCheck ="01";
                    editBanner.setEnabled(true);
                }else{
                    editBanner.setEnabled(false);
                    editBanner.setText("");

                }
                break;
        }
    }
}
