/*
package com.rspl.sf.msfa.reports;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.BehaviourListAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.CustomerBean;
import com.rspl.sf.msfa.store.OfflineManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

*/
/**
 * Created by e10526 on 03-02-2017.
 *
 *//*

public class BehaviourListActivity extends AppCompatActivity {
    private ArrayList<CustomerBean> alBehaviourList = null;
    private String[][] mArrayRetailerCategory =null;


    private  String mStrBehaviuorTypeCode ="";
    private ListView lv_behaviour_ret_list;
    Spinner sp_behaviour_type;

    TextView tv_mtd_heading,tv_sno_headig,tvfrom_date,tv_to_date;



    String[] dealerNo = {"300646","300757","300894","301161","301875","303979","304436","308827","309160","310976","311238","311824","313138","314063"
    ,"314082","314083","314507","314757","315045","315082"};
    String[] dealerName = {"A.A.RAZVI","AJAY D.SAKURE","ARUN K.LANJEWAR","CHAYTANYA CONSTRUCTION CO","K.K.NAIR & COMPANY",
            "SUNNY CONSTRUCTIONS","VINOD T.JIBHAKATE","SAIBABA INFRASTRUCTURE PVT LTD","RAJ CONSTRUCTIONS","SUNFLAG IRON & STEEL CO.LTD","MADHUKARRAO PANDAV"
    ,"KGN BRICKS","GRAM PANCHAYAT OFFICE","RAJENDRAKUMAR SITARAM HEDA","SHRI SAI BABA CONSTRUCTION","VENDANT CONSTRUCTION"
    ,"M.R.CONSTRUCTIONS","SIDDHESH INFRA & PROJECTS","CHANDRESH JAIN","SHEIKH CONSTRUCTION"};
    String[] dealerType = {"102162","102180","102193","102082","102194","102243","102621","102144","102825","102810","102869"};
    String[] dealerMTDValue = {"102162","102180","102193","102082","102194","102243","102621","102144","102825","102810","102869"};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
       // ActionBarView.initActionBarView(this, true,getString(R.string.title_behaviour_list));
        setContentView(R.layout.activity_behaviour_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_behaviour_list), 0);
        initUI();
        setValuesToUI();
    }

    private  void initUI(){
        tv_mtd_heading = (TextView) findViewById(R.id.tv_mtd_heading_val);
        tv_sno_headig = (TextView) findViewById(R.id.tv_sno_headig);
        lv_behaviour_ret_list = (ListView)findViewById(R.id.lv_behaviour_ret_list);
        sp_behaviour_type = (Spinner) findViewById(R.id.sp_behaviour_type);
        tvfrom_date = (TextView) findViewById(R.id.tv_behave_from_date);
        tv_to_date = (TextView) findViewById(R.id.tv_behave_to_date);


        Calendar c = Calendar.getInstance();

        SimpleDateFormat S = new SimpleDateFormat("dd-MMM-yyyy");
        String date = S.format(c.getTime());
        tv_to_date.setText("To Date : "+date);


        Calendar c1 = Calendar.getInstance();
        c1.add(Calendar.DAY_OF_MONTH, -30);

        SimpleDateFormat S1 = new SimpleDateFormat("dd-MMM-yyyy");
        String date1 = S1.format(c1.getTime());
        tvfrom_date.setText("From Date : "+date1);


    }

    private void setValuesToUI(){
//        getRetailerCategory();
     setSpinnerValues();
    }

    private void setSpinnerValues(){

        if (mArrayRetailerCategory == null) {
            mArrayRetailerCategory = new String[2][3];
            mArrayRetailerCategory[0][0] = "1";
            mArrayRetailerCategory[1][0] = "Top 10 Performers";
            mArrayRetailerCategory[0][1] = "2";
            mArrayRetailerCategory[1][1] = "Bottom 10 Performers";
            mArrayRetailerCategory[0][2] = "3";
            mArrayRetailerCategory[1][2] = "Not purchased for a period";

        }

        ArrayAdapter<String> distributorNameAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, mArrayRetailerCategory[1]);
        distributorNameAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_behaviour_type.setAdapter(distributorNameAdapter);

        sp_behaviour_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                mStrBehaviuorTypeCode = mArrayRetailerCategory[0][position];

                if(mStrBehaviuorTypeCode.equalsIgnoreCase(Constants.NotPurchasedType)){
                    LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT,1f);
                    llp.setMargins(1, 0, 1, 0); // llp.setMargins(left, top, right, bottom);
                    tv_sno_headig.setLayoutParams(llp);
                    tv_mtd_heading.setVisibility(View.GONE);
                }else {
                    LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(20, ViewGroup.LayoutParams.MATCH_PARENT,1f);
                    llp.setMargins(1, 0, 1, 0); // llp.setMargins(left, top, right, bottom);
                    tv_sno_headig.setLayoutParams(llp);
                    tv_mtd_heading.setVisibility(View.VISIBLE);
                }

                getBehaviourList(mStrBehaviuorTypeCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }



    private void getBehaviourList(String retailerCategory){
        try {

//            alBehaviourList= OfflineManager.getBehavoiurList(Constants.SPChannelEvaluationList + "?$filter="
//                    + Constants.EvaluationTypeID + " eq '" + retailerCategory + "' ");
if(retailerCategory == "1"){

    tvfrom_date.setVisibility(View.GONE);
    tv_to_date.setVisibility(View.GONE);

    CustomerBean c= new CustomerBean("");

    c.setRetailerName("GUPTA HARDWARE");
    c.setCustomerId("101983");
    c.setMtdValue("100000.0");
    CustomerBean c1= new CustomerBean("");
    c1.setRetailerName("GURUDEO TRADERS");
    c1.setCustomerId("101984");
    c1.setMtdValue("95000.0");
    CustomerBean c2= new CustomerBean("");
    c2.setRetailerName("LAXMI TRADRS");
    c2.setCustomerId("102066");
    c2.setMtdValue("90000.0");

    CustomerBean c3= new CustomerBean("");
    c3.setRetailerName("OMKAR CEMENT SUPPLIERS");
    c3.setCustomerId("102141");
    c3.setMtdValue("85000.0");

    CustomerBean c5= new CustomerBean("");
    c5.setRetailerName("RAZA SALES CORPORATION");
    c5.setCustomerId("108106");
    c5.setMtdValue("80000.0");


    CustomerBean c6= new CustomerBean("");
    c6.setRetailerName("RADHIKA TRADERS");
    c6.setCustomerId("108295");
    c6.setMtdValue("75000.0");


    CustomerBean c7= new CustomerBean("");
    c7.setRetailerName("AYUSH ENTERPRISES");
    c7.setCustomerId("108334");
    c7.setMtdValue("70000.0");


    CustomerBean c8= new CustomerBean("");
    c8.setRetailerName("S M TRADERS");
    c8.setCustomerId("108431");
    c8.setMtdValue("65000.0");


    CustomerBean c9= new CustomerBean("");
    c9.setRetailerName("MESHRAM BUILDING MATERIAL SUPPLIERS");
    c9.setCustomerId("109394");
    c9.setMtdValue("60000.0");


    CustomerBean c10= new CustomerBean("");
    c10.setRetailerName("Y K GHATOLE TRADES");
    c10.setCustomerId("109466");
    c10.setMtdValue("55000.0");

    alBehaviourList = new ArrayList<CustomerBean>();
    alBehaviourList.add(c);
    alBehaviourList.add(c1);
    alBehaviourList.add(c2);
    alBehaviourList.add(c3);
    alBehaviourList.add(c5);
    alBehaviourList.add(c6);
    alBehaviourList.add(c7);
    alBehaviourList.add(c8);
    alBehaviourList.add(c9);
    alBehaviourList.add(c10);


}else if(retailerCategory == "2"){

    tvfrom_date.setVisibility(View.GONE);
    tv_to_date.setVisibility(View.GONE);

    CustomerBean c= new CustomerBean("");

    c.setRetailerName("PRANAY TRADERS");
    c.setCustomerId("109704");
    c.setMtdValue("5000.0");

    CustomerBean c1= new CustomerBean("");
    c1.setRetailerName("AYUSH ENTERPRISES");
    c1.setCustomerId("109988");
    c1.setMtdValue("10000.0");

    CustomerBean c2= new CustomerBean("");
    c2.setRetailerName("VAISHNAVI KRUSHI SEWA KENDRA");
    c2.setCustomerId("110010");
    c2.setMtdValue("12000.0");



    CustomerBean c3= new CustomerBean("");
    c3.setRetailerName("SHRIRAM SANJAYRAO POHARKAR");
    c3.setCustomerId("110016");
    c3.setMtdValue("15000.0");

    CustomerBean c5= new CustomerBean("");
    c5.setRetailerName("JAI BAJRANG MINERALS");
    c5.setCustomerId("110567");
    c5.setMtdValue("18000.0");

    CustomerBean c6= new CustomerBean("");
    c6.setRetailerName("RENUKA TRADERS");
    c6.setCustomerId("110704");
    c6.setMtdValue("20000.0");


    CustomerBean c7= new CustomerBean("");
    c7.setRetailerName("TALMALE HARDWARE");
    c7.setCustomerId("110725");
    c7.setMtdValue("21000.0");


    CustomerBean c8= new CustomerBean("");
    c8.setRetailerName("S.M. TRADING COMPANY");
    c8.setCustomerId("111289");
    c8.setMtdValue("21500.0");


    CustomerBean c9= new CustomerBean("");
    c9.setRetailerName("ARIHANT HARDWARE");
    c9.setCustomerId("111381");
    c9.setMtdValue("24000.0");


    CustomerBean c10= new CustomerBean("");
    c10.setRetailerName("SUN AGROTECH KANDRI");
    c10.setCustomerId("111546");
    c10.setMtdValue("25000.0");



    alBehaviourList = new ArrayList<CustomerBean>();
    alBehaviourList.add(c);
    alBehaviourList.add(c1);
    alBehaviourList.add(c2);
    alBehaviourList.add(c3);
    alBehaviourList.add(c5);
    alBehaviourList.add(c6);
    alBehaviourList.add(c7);
    alBehaviourList.add(c8);
    alBehaviourList.add(c9);
    alBehaviourList.add(c10);
}else{

    tvfrom_date.setVisibility(View.VISIBLE);
    tv_to_date.setVisibility(View.VISIBLE);
    tv_mtd_heading.setVisibility(View.GONE);



    CustomerBean c= new CustomerBean("");

    c.setRetailerName("SHREE GURUDEV PAINTS & HARDWARE");
    c.setCustomerId("112292");
    c.setMtdValue("0.0");
    CustomerBean c1= new CustomerBean("");
    c1.setRetailerName("MANGESH HARDWARE AND ELECTRICALS");
    c1.setCustomerId("112152");
    c1.setMtdValue("0.0");




    alBehaviourList = new ArrayList<CustomerBean>();
    alBehaviourList.add(c);
    alBehaviourList.add(c1);



}





        }
//        catch (OfflineODataStoreException e) {
//            LogManager.writeLogError(Constants.error_txt + e.getMessage());
//        }
        catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        displayBehaviuorList();
    }

    private void displayBehaviuorList(){
        BehaviourListAdapter behaviourListAdapter = new BehaviourListAdapter(this, alBehaviourList,mStrBehaviuorTypeCode,mStrBehaviuorTypeCode);
        lv_behaviour_ret_list.setEmptyView(findViewById(R.id.tv_empty_lay) );
        lv_behaviour_ret_list.setAdapter(behaviourListAdapter);
        behaviourListAdapter.notifyDataSetChanged();
    }



    private  void getRetailerCategory(){

        try{
            String mStrConfigQry = Constants.ValueHelps + "?$filter="+ Constants.EntityType+" eq 'Evaluation'";
            mArrayRetailerCategory = OfflineManager.getConfigListWithDefaultValAndNone(mStrConfigQry,"");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BehaviourListActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_competition_information).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        BehaviourListActivity.this.finish();
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
*/
