package com.rspl.sf.msfa.priceUpdate;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.DialogCallBack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class PriceUpdateActivity extends AppCompatActivity {
    EditText bg1, bg2, bg3, bg4, utcl1, utcl2, utcl3, utcl4, ocl1, ocl2, ocl3, ocl4, laf1, laf2, laf3, laf4, acc1, acc2, acc3, acc4;
    EditText pb_bg1, pb_bg2, pb_bg3, pb_bg4, pb_utcl1, pb_utcl2, pb_utcl3, pb_utcl4, pb_ocl1, pb_ocl2, pb_ocl3, pb_ocl4, pb_laf1, pb_laf2, pb_laf3, pb_laf4, pb_acc1, pb_acc2, pb_acc3, pb_acc4;
    ArrayList<PriceBean> price_obj = new ArrayList<PriceBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_update);
//Initialize action bar with back button(true)
       // ActionBarView.initActionBarView(this, true, getString(R.string.title_price_update_list));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_price_update_list), 0);

        initVar();
        initDB();

        saveDB("create");//show the dialog if  data is already present


    }

    private void initVar() {

        bg1 = (EditText) findViewById(R.id.bg1);
        bg2 = (EditText) findViewById(R.id.bg2);
        bg3 = (EditText) findViewById(R.id.bg3);
        bg4 = (EditText) findViewById(R.id.bg4);
        utcl1 = (EditText) findViewById(R.id.utcl1);
        utcl2 = (EditText) findViewById(R.id.utcl2);
        utcl3 = (EditText) findViewById(R.id.utcl3);
        utcl4 = (EditText) findViewById(R.id.utcl4);
        ocl1 = (EditText) findViewById(R.id.ocl1);
        ocl2 = (EditText) findViewById(R.id.ocl2);
        ocl3 = (EditText) findViewById(R.id.ocl3);
        ocl4 = (EditText) findViewById(R.id.ocl4);
        laf1 = (EditText) findViewById(R.id.laf1);
        laf2 = (EditText) findViewById(R.id.laf2);
        laf3 = (EditText) findViewById(R.id.laf3);
        laf4 = (EditText) findViewById(R.id.laf4);
        acc1 = (EditText) findViewById(R.id.acc1);
        acc2 = (EditText) findViewById(R.id.acc2);
        acc3 = (EditText) findViewById(R.id.acc3);
        acc4 = (EditText) findViewById(R.id.acc4);


        pb_bg1 = (EditText) findViewById(R.id.pb_bg1);
        pb_bg2 = (EditText) findViewById(R.id.pb_bg2);
        pb_bg3 = (EditText) findViewById(R.id.pb_bg3);
        pb_bg4 = (EditText) findViewById(R.id.pb_bg4);
        pb_utcl1 = (EditText) findViewById(R.id.pb_utcl1);
        pb_utcl2 = (EditText) findViewById(R.id.pb_utcl2);
        pb_utcl3 = (EditText) findViewById(R.id.pb_utcl3);
        pb_utcl4 = (EditText) findViewById(R.id.pb_utcl4);
        pb_ocl1 = (EditText) findViewById(R.id.pb_ocl1);
        pb_ocl2 = (EditText) findViewById(R.id.pb_ocl2);
        pb_ocl3 = (EditText) findViewById(R.id.pb_ocl3);
        pb_ocl4 = (EditText) findViewById(R.id.pb_ocl4);
        pb_laf1 = (EditText) findViewById(R.id.pb_laf1);
        pb_laf2 = (EditText) findViewById(R.id.pb_laf2);
        pb_laf3 = (EditText) findViewById(R.id.pb_laf3);
        pb_laf4 = (EditText) findViewById(R.id.pb_laf4);
        pb_acc1 = (EditText) findViewById(R.id.pb_acc1);
        pb_acc2 = (EditText) findViewById(R.id.pb_acc2);
        pb_acc3 = (EditText) findViewById(R.id.pb_acc3);
        pb_acc4 = (EditText) findViewById(R.id.pb_acc4);

        bg1.setText("0.0");
        bg2.setText("0.0");
        bg3.setText("0.0");
        bg4.setText("0.0");
        utcl1.setText("0.0");
        utcl2.setText("0.0");
        utcl3.setText("0.0");
        utcl4.setText("0.0");
        ocl1.setText("0.0");
        ocl2.setText("0.0");
        ocl3.setText("0.0");
        ocl4.setText("0.0");
        laf1.setText("0.0");
        laf2.setText("0.0");
        laf3.setText("0.0");
        laf4.setText("0.0");
        acc1.setText("0.0");
        acc2.setText("0.0");
        acc3.setText("0.0");
        acc4.setText("0.0");


        pb_bg1.setText("0.0");
        pb_bg2.setText("0.0");
        pb_bg3.setText("0.0");
        pb_bg4.setText("0.0");
        pb_utcl1.setText("0.0");
        pb_utcl2.setText("0.0");
        pb_utcl3.setText("0.0");
        pb_utcl4.setText("0.0");
        pb_ocl1.setText("0.0");
        pb_ocl2.setText("0.0");
        pb_ocl3.setText("0.0");
        pb_ocl4.setText("0.0");
        pb_laf1.setText("0.0");
        pb_laf2.setText("0.0");
        pb_laf3.setText("0.0");
        pb_laf4.setText("0.0");
        pb_acc1.setText("0.0");
        pb_acc2.setText("0.0");
        pb_acc3.setText("0.0");
        pb_acc4.setText("0.0");
    }

    private void initDB() {
        Constants.dbHelper = new PricingDatabaseHelper(this);
        Constants.database = Constants.dbHelper.getWritableDatabase();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.price_update_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.save:
                getData();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PriceUpdateActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_competition_information).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        PriceUpdateActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();
    }

    private void getData() {


        String s_hdpe_bg1 = bg1.getText().toString();
        String s_hdpe_bg2 = bg2.getText().toString();
        String s_hdpe_bg3 = bg3.getText().toString();
        String s_hdpe_bg4 = bg4.getText().toString();

        String s_hdpe_utcl1 = utcl1.getText().toString();
        String s_hdpe_utcl2 = utcl2.getText().toString();
        String s_hdpe_utcl3 = utcl3.getText().toString();
        String s_hdpe_utcl4 = utcl4.getText().toString();

        String s_hdpe_ocl1 = ocl1.getText().toString();
        String s_hdpe_ocl2 = ocl2.getText().toString();
        String s_hdpe_ocl3 = ocl3.getText().toString();
        String s_hdpe_ocl4 = ocl4.getText().toString();


        String s_hdpe_laf1 = ocl1.getText().toString();
        String s_hdpe_laf2 = laf2.getText().toString();
        String s_hdpe_laf3 = laf3.getText().toString();
        String s_hdpe_laf4 = laf4.getText().toString();

        String s_hdpe_acc1 = acc1.getText().toString();
        String s_hdpe_acc2 = acc2.getText().toString();
        String s_hdpe_acc3 = acc3.getText().toString();
        String s_hdpe_acc4 = acc4.getText().toString();


        String s_pb_bg1 = pb_bg1.getText().toString();
        String s_pb_bg2 = pb_bg2.getText().toString();
        String s_pb_bg3 = pb_bg3.getText().toString();
        String s_pb_bg4 = pb_bg4.getText().toString();

        String s_pb_utcl1 = pb_utcl1.getText().toString();
        String s_pb_utcl2 = pb_utcl2.getText().toString();
        String s_pb_utcl3 = pb_utcl3.getText().toString();
        String s_pb_utcl4 = pb_utcl4.getText().toString();

        String s_pb_ocl1 = pb_ocl1.getText().toString();
        String s_pb_ocl2 = pb_ocl2.getText().toString();
        String s_pb_ocl3 = pb_ocl3.getText().toString();
        String s_pb_ocl4 = pb_ocl4.getText().toString();


        String s_pb_laf1 = pb_laf1.getText().toString();
        String s_pb_laf2 = pb_laf2.getText().toString();
        String s_pb_laf3 = pb_laf3.getText().toString();
        String s_pb_laf4 = pb_laf4.getText().toString();

        String s_pb_acc1 = pb_acc1.getText().toString();
        String s_pb_acc2 = pb_acc2.getText().toString();
        String s_pb_acc3 = pb_acc3.getText().toString();
        String s_pb_acc4 = pb_acc4.getText().toString();


        String final_value = s_hdpe_bg1 + s_hdpe_bg2 + s_hdpe_bg3 + s_hdpe_bg4 +
                s_hdpe_utcl1 + s_hdpe_utcl2 + s_hdpe_utcl3 + s_hdpe_utcl4 +
                s_hdpe_ocl1 + s_hdpe_ocl2 + s_hdpe_ocl3 + s_hdpe_ocl4 +
                s_hdpe_laf1 + s_hdpe_laf2 + s_hdpe_laf3 + s_hdpe_laf4 +
                s_hdpe_acc1 + s_hdpe_acc2 + s_hdpe_acc3 + s_hdpe_acc4 +
                s_pb_bg1 + s_pb_bg2 + s_pb_bg3 + s_pb_bg4 +
                s_pb_utcl1 + s_pb_utcl2 + s_pb_utcl3 + s_pb_utcl4 +
                s_pb_ocl1 + s_pb_ocl2 + s_pb_ocl3 + s_pb_ocl4 +
                s_pb_laf1 + s_pb_laf2 + s_pb_laf3 + s_pb_laf4 +
                s_pb_acc1 + s_pb_acc2 + s_pb_acc3 + s_pb_acc4;
        if (!TextUtils.isEmpty(final_value))//value is entered
        {
            if (!TextUtils.isEmpty(s_hdpe_bg1) || !TextUtils.isEmpty(s_hdpe_bg2) || !TextUtils.isEmpty(s_hdpe_bg3) || !TextUtils.isEmpty(s_hdpe_bg4))//any value entered
            {
                PriceBean pricebean = new PriceBean();
                pricebean.setmaster_brand("HDPE");
                pricebean.setbrand("BG");
                pricebean.setBP_EX(s_hdpe_bg1);
                pricebean.setBP_For(s_hdpe_bg2);
                pricebean.setWSP(s_hdpe_bg3);
                pricebean.setRSP(s_hdpe_bg4);
                price_obj.add(pricebean);
            }
            if (!TextUtils.isEmpty(s_hdpe_utcl1) || !TextUtils.isEmpty(s_hdpe_utcl2) || !TextUtils.isEmpty(s_hdpe_utcl3) || !TextUtils.isEmpty(s_hdpe_utcl4))//any value entered
            {
                PriceBean pricebean = new PriceBean();
                pricebean.setmaster_brand("HDPE");
                pricebean.setbrand("UTCL");
                pricebean.setBP_EX(s_hdpe_utcl1);
                pricebean.setBP_For(s_hdpe_utcl2);
                pricebean.setWSP(s_hdpe_utcl3);
                pricebean.setRSP(s_hdpe_utcl4);
                price_obj.add(pricebean);


            }


            if (!TextUtils.isEmpty(s_hdpe_ocl1) || !TextUtils.isEmpty(s_hdpe_ocl2) || !TextUtils.isEmpty(s_hdpe_ocl3) || !TextUtils.isEmpty(s_hdpe_ocl4))//any value entered
            {

                PriceBean pricebean = new PriceBean();
                pricebean.setmaster_brand("HDPE");
                pricebean.setbrand("OCL");
                pricebean.setBP_EX(s_hdpe_ocl1);
                pricebean.setBP_For(s_hdpe_ocl2);
                pricebean.setWSP(s_hdpe_ocl3);
                pricebean.setRSP(s_hdpe_ocl4);
                price_obj.add(pricebean);
            }

            if (!TextUtils.isEmpty(s_hdpe_laf1) || !TextUtils.isEmpty(s_hdpe_laf2) || !TextUtils.isEmpty(s_hdpe_laf3) || !TextUtils.isEmpty(s_hdpe_laf4))//any value entered
            {
                PriceBean pricebean = new PriceBean();
                pricebean.setmaster_brand("HDPE");
                pricebean.setbrand("LAF");
                pricebean.setBP_EX(s_hdpe_laf1);
                pricebean.setBP_For(s_hdpe_laf2);
                pricebean.setWSP(s_hdpe_laf3);
                pricebean.setRSP(s_hdpe_laf4);
                price_obj.add(pricebean);


            }


            if (!TextUtils.isEmpty(s_hdpe_acc1) || !TextUtils.isEmpty(s_hdpe_acc2) || !TextUtils.isEmpty(s_hdpe_acc3) || !TextUtils.isEmpty(s_hdpe_acc4))//any value entered
            {
                PriceBean pricebean = new PriceBean();
                pricebean.setmaster_brand("HDPE");
                pricebean.setbrand("ACC");
                pricebean.setBP_EX(s_hdpe_acc1);
                pricebean.setBP_For(s_hdpe_acc2);
                pricebean.setWSP(s_hdpe_acc3);
                pricebean.setRSP(s_hdpe_acc4);
                price_obj.add(pricebean);


            }

//*******************************Brand Paper Bag

            if (!TextUtils.isEmpty(s_pb_bg1) || !TextUtils.isEmpty(s_pb_bg2) || !TextUtils.isEmpty(s_pb_bg3) || !TextUtils.isEmpty(s_pb_bg4))//any value entered
            {


                PriceBean pricebean = new PriceBean();
                pricebean.setmaster_brand("PB");
                pricebean.setbrand("BG");
                pricebean.setBP_EX(s_pb_bg1);
                pricebean.setBP_For(s_pb_bg2);
                pricebean.setWSP(s_pb_bg3);
                pricebean.setRSP(s_pb_bg4);
                price_obj.add(pricebean);


            }


            if (!TextUtils.isEmpty(s_pb_utcl1) || !TextUtils.isEmpty(s_pb_utcl2) || !TextUtils.isEmpty(s_pb_utcl3) || !TextUtils.isEmpty(s_pb_utcl4))//any value entered
            {


                PriceBean pricebean = new PriceBean();
                pricebean.setmaster_brand("PB");
                pricebean.setbrand("UTCL");
                pricebean.setBP_EX(s_pb_utcl1);
                pricebean.setBP_For(s_pb_utcl2);
                pricebean.setWSP(s_pb_utcl3);
                pricebean.setRSP(s_pb_utcl4);
                price_obj.add(pricebean);


            }


            if (!TextUtils.isEmpty(s_pb_ocl1) || !TextUtils.isEmpty(s_pb_ocl2) || !TextUtils.isEmpty(s_pb_ocl3) || !TextUtils.isEmpty(s_pb_ocl4))//any value entered
            {

                PriceBean pricebean = new PriceBean();
                pricebean.setmaster_brand("PB");
                pricebean.setbrand("OCL");
                pricebean.setBP_EX(s_pb_ocl1);
                pricebean.setBP_For(s_pb_ocl2);
                pricebean.setWSP(s_pb_ocl3);
                pricebean.setRSP(s_pb_ocl4);
                price_obj.add(pricebean);


            }


            if (!TextUtils.isEmpty(s_pb_laf1) || !TextUtils.isEmpty(s_pb_laf2) || !TextUtils.isEmpty(s_pb_laf3) || !TextUtils.isEmpty(s_pb_laf4))//any value entered
            {

                PriceBean pricebean = new PriceBean();
                pricebean.setmaster_brand("PB");
                pricebean.setbrand("LAF");
                pricebean.setBP_EX(s_pb_laf1);
                pricebean.setBP_For(s_pb_laf2);
                pricebean.setWSP(s_pb_laf3);
                pricebean.setRSP(s_pb_laf4);
                price_obj.add(pricebean);


            }


            if (!TextUtils.isEmpty(s_pb_acc1) || !TextUtils.isEmpty(s_pb_acc2) || !TextUtils.isEmpty(s_pb_acc3) || !TextUtils.isEmpty(s_pb_acc4))//any value entered
            {
                PriceBean pricebean = new PriceBean();
                pricebean.setmaster_brand("PB");
                pricebean.setbrand("ACC");
                pricebean.setBP_EX(s_pb_acc1);
                pricebean.setBP_For(s_pb_acc2);
                pricebean.setWSP(s_pb_acc3);
                pricebean.setRSP(s_pb_acc4);
                price_obj.add(pricebean);


            }


            String final_value_new = final_value.replaceAll("0.0", "");

            if (TextUtils.isEmpty(final_value_new))//value not entered
            {
                if (saveDB("")) {
                    ConstantsUtils.dialogBoxWithButton(PriceUpdateActivity.this, "", "Price not entered.Do you want to save?", "Yes", "No",  new DialogCallBack() {
                        @Override
                        public void clickedStatus(boolean clickedStatus) {
                            if(clickedStatus) {
                                saveData();
                            }
                        }
                    });
                }

            } else {
                if (saveDB("")) {
                    saveData();
                }
            }


        }
    }

    public String getTodaysdate() {


        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public void createRecords() {
        for (int index = 0; index < price_obj.size(); index++) {
            ContentValues values = new ContentValues();
            values.put(Constants.master_brand, price_obj.get(index).getmaster_brand());
            values.put(Constants.brand, price_obj.get(index).getbrand());
            values.put(Constants.BP_EX, price_obj.get(index).getBP_EX());
            values.put(Constants.BP_For, price_obj.get(index).getBP_For());
            values.put(Constants.WSP, price_obj.get(index).getWSP());
            values.put(Constants.RSP, price_obj.get(index).getRSP());
            values.put(Constants.date, getTodaysdate());
            Constants.database.insert(Constants.TABLE_NAME, null, values);


        }
        ConstantsUtils.dialogBoxWithButton(PriceUpdateActivity.this, "", "Price updated successfully", getString(R.string.ok), "", new DialogCallBack() {
            @Override
            public void clickedStatus(boolean clickedStatus) {
                finish();
            }
        });
    }


    public Cursor selectRecords() {
        String[] cols = new String[]{Constants.Price_ID, Constants.master_brand, Constants.brand, Constants.BP_EX, Constants.BP_For, Constants.WSP, Constants.RSP, Constants.date};
        Cursor mCursor = Constants.database.query(true, Constants.TABLE_NAME, cols, null
                , null, null, null, null, null);
        if (mCursor != null) {

            if (mCursor.moveToFirst()) {

                mCursor.moveToFirst();
                Log.d("Price_ID " + mCursor.getString(0) + "  master_brand " + mCursor.getString(1) + "  brand " + mCursor.getString(2) + "  BP_EX " + mCursor.getString(3) + "  BP_For " + mCursor.getString(4) + "  WSP " + mCursor.getString(5) + "   RSP " + mCursor.getString(6) + "  date " + mCursor.getString(7), "DB_ROW");
            }
        }
        return mCursor; // iterate to get each value.
    }

    private void saveData(){
        createRecords();
//        selectRecords();
    }
    public boolean saveDB(String tag) {
        String Query = "Select * from " + Constants.TABLE_NAME + " where " + Constants.date + " = '" + getTodaysdate() + "'";
        Cursor cursor = Constants.database.rawQuery(Query, null);
        if (cursor.getCount() > 0) {
            if (tag.equals("create")) {
                ConstantsUtils.dialogBoxWithButton(PriceUpdateActivity.this, "", "Price already updated for the day", "OK", "", new DialogCallBack() {
                    @Override
                    public void clickedStatus(boolean clickedStatus) {
                        finish();
                    }
                });
            } else {
                ConstantsUtils.dialogBoxWithButton(PriceUpdateActivity.this, "", "Data not saved!", getString(R.string.ok), "", new DialogCallBack() {
                    @Override
                    public void clickedStatus(boolean clickedStatus) {
                        finish();
                    }
                });
            }

            cursor.close();
            return false;
        }

        cursor.close();
        return true;
    }

}
