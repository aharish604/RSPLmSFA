package com.rspl.sf.msfa.reports;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.MyUtils;
import com.rspl.sf.msfa.mbo.OutstandingAgeBean;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.HashMap;
import java.util.Iterator;

public class OutstandingAgeReport extends AppCompatActivity {

    HorizontalScrollView svHeader = null, svItem = null;
    private LinearLayout llDelStockLayout;

    //ArrayList<OutstandingAgeBean> outstandingAgeList = new ArrayList<>();

    HashMap<String,OutstandingAgeBean> outstandingAgeList = new HashMap<>();
    boolean flag = true;
    int cursorLength = 0;
    private ProgressDialog prgressDialog = null;
    SQLiteDatabase db = Constants.EventUserHandler;;

    String[] scustNo = {"102162","102180","102193","102082","102194","102243","102621","102144","102825","102810","102869"};
    String[] scustName = {"PRAKASH DEVRAVASA KSHTRIYA","RADHESHYAM  BRIJLAL RAWAT","RAMDEVBABA TILES","MAHAVIR TRADERS","RAMESHWAR TRADERS","SEDHURAM JAGDISH PRASAD JOSHI","JAI TRADERS","PAINT HOUSE","SOUMYA  ENTERPRISES","KRISHNA  ENTERPRISES","SARALA CEMENT STORE"};
    String[] scity = {"MG-MANMAD","MG-NASIK","MG-YAVATMAL","MG-NASIK","MG-PARBHANI","MG-NAGPUR","MC-BANDA","MG-AKOLA","CC-BALASORE","CC-BALASORE","CC-CUTTACK-2"};

    String[] steleNo = {"02556-252052","9422248495","9422923262","0253-2514605","02452-223110","7182224241","9415570131","7254243686","0","0","0"};
    String[] ssecurity = {" 437,409.00","1,055,046.00","158,582.00"," 1,348,952.00","351,565.00","219,434.00","173,586.00","200,372.00","435,298.00","0.00"," 848,255.00"};
    String[] screditlimit = {"62,381,00"," 349,566,00","1.00"," 1.00","93,592.00","1.00","37,799.00","132,717.00","838,000.00"," 0.00","806,000.00"};
    String[] stotaldebit = {"176,708.28","902,921.57","367,935.87","856,804.86","109,538.99","1,352,917.53","226,520.43","257,918.89","464,248.06","10,200.00","702,671.78"};

    String[] s07 = {"176,708.28","902.921.57","1.700.00","856.804.86","109.538.99","  0.00","155.670.00"," 3.600.00","0.00","0.00","579.740.00"};
    String[] s715 = {" 0.00"," 0.00"," 0.00"," 0.00"," 0.00"," 0.00"," 70.850.43","0.00","464.248.06"," 0.00","3.100.00"};
    String[] s1530 = {" 0.00"," 0.00","990.00"," 0.00"," 0.00"," 0.00"," 0.00","9,130.00","0.00","0.00","0.00"};
    String[] s3045 = {"0.00","0.00","117,900.00","0.00","0.00","0.00","0.00","201,600.00","0.00","10,200.00","119,831.78"};
    String[] s4560 = {"0.00","0.00","247,345.87","0.00","0.00","0.00","0.00","0.00","0.00","0.00","0.00"};
    String[] s6090 = {"0.00","0.00","0.00","0.00","0.00","0.00","0.00","3.100.00","0.00","0.00","0.00"};
    String[] s90120 = {"0.00","0.00","0.00","0.00","0.00","0.00","0.00","40.488.89","0.00","0.00","0.00"};
    String[] s180 = {"0.00","0.00","0.00","0.00","0.00","1,352,917.53","0.00","0.00","0.00","0,00","0.00"};

    String[] spast = {"6,61,200.00","1,181.69","1,181.69","1,418.01","1,418.01","1,181.69","472.66","472.66","709.00","1,181.69","-1.00"};
    String[] scurrent = {"0.00","0.00","0.00","0.00","0.00","0.00","0.00","0.00","0.00","0.00","0.00"};
    String[] s3160 = {"0.00","0.00","0.00","338.00","338.00","5,800.00","5,800.00","5,800.00","0.00","0,00","5,800.00"};
    String[] s6190 = {"0.00","0.00","0.00","0.00","0.00","0.00","0.00","0.00","0.00","0.00","0.00"};
    String[] s91120 = {"0.00","0.00","0.00","0.00","0.00","0.00","0.00","0.00","0.00","0.00","0.00"};
    String[] s120 = {"0.00","0.00","0.00","0.00","0.00","0.00","0.00","0.00","0.00","0.00","0.00"};

    TextView tvCustNo, tvCustName,tvCustCity,tvtele,tvdistChannel,tvsecuridtyDeposit,tvcreditlimit,tvdebitBal,tv07,tv715,tv1530,tv3045,tv4560,tv6090,tv90120,tv120180,tv180;
    TextView tvpast,tvcurrent,tv3160,tv6190,tv91120,tv120;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outstanding_ageing);

       // ActionBarView.initActionBarView(this, true,getString(R.string.title_OutstandingSummary));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_OutstandingSummary), 0);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        new LoadData().execute();

       Constants.deleteTable(db,Constants.OUTSTANDINGAGE_TABLE);


//for(int i=0;i<scustNo.length;i++){
//
//    Hashtable dbItemTable = new Hashtable();
//
//    dbItemTable.put(Constants.OACustomerNo, scustNo[i]);
//    dbItemTable.put(Constants.OACustomerName, scustName[i]);
//    dbItemTable.put(Constants.OACityName, scity[i]);
//    dbItemTable.put(Constants.OATelephone1, steleNo[i]);
//    dbItemTable.put(Constants.OADistChannel, "Trade");
//    dbItemTable.put(Constants.OASecurityDeposit, ssecurity[i]);
//    dbItemTable.put(Constants.OACreditLimit, screditlimit[i]);
//    dbItemTable.put(Constants.OATotalDebitBal, stotaldebit[i]);
//    dbItemTable.put(Constants.OA0_7Days, s07[i]);
//    dbItemTable.put(Constants.OA7_15Days, s715[i]);
//    dbItemTable.put(Constants.OA15_30Days, s1530[i]);
//
//    dbItemTable.put(Constants.OA30_45Days, s3045[i]);
//    dbItemTable.put(Constants.OA45_60Days, s4560[i]);
//    dbItemTable.put(Constants.OA60_90Days, s6090[i]);
//    dbItemTable.put(Constants.OA90_120Days, s90120[i]);
//    dbItemTable.put(Constants.OA120_180Days, "0.00");
//    dbItemTable.put(Constants.OA180Days, s180[i]);
//
//    dbItemTable.put(Constants.OAPastDays, spast[i]);
//    dbItemTable.put(Constants.OACurrentDays, scurrent[i]);
//    dbItemTable.put(Constants.OA3160Days, s3160[i]);
//    dbItemTable.put(Constants.OA6190Days, s6190[i]);
//    dbItemTable.put(Constants.OA91120Days, s91120[i]);
//    dbItemTable.put(Constants.OA120Days, s120[i]);
//
//
//
//
//
//
//
//    Constants.events.insert(Constants.OUTSTANDINGAGE_TABLE,
//            dbItemTable);
//
//}
  //      getData();

    }

    /**
     * get data and display
     */
    private void getAndDisplayData() {
        try {
            outstandingAgeList.clear();
            LogManager.writeLogDebug("before");

            outstandingAgeList = OfflineManager.getOutstandingInvoiceList(Constants.OutstandingInvoices);

            LogManager.writeLogDebug("after");



        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

    }


    class LoadData extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgressDialog = MyUtils.showProgressDialog(OutstandingAgeReport.this, "", getString(R.string.progressbar_message));
        }

        @Override
        protected Void doInBackground(Void... params) {

            getAndDisplayData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(prgressDialog!=null){
                prgressDialog.dismiss();
            }

            getDisplayValues(outstandingAgeList);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initScroll();
    }


        void initScroll(){
            svHeader = (HorizontalScrollView) findViewById(R.id.sv_header);
            svItem = (HorizontalScrollView) findViewById(R.id.sv_item);

            svHeader.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        svHeader.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                            @Override
                            public void onScrollChange(View view, int scrollX, int scrollY, int i2, int i3) {
                                svItem.scrollTo(scrollX,scrollY);
                            }
                        });
                    }
                    return false;
                }
            });

            svItem.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Log.e("Tag1",String.valueOf(getWindow().getCurrentFocus()));
                    svItem.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                        @Override
                        public void onScrollChanged() {
                            Log.e("Tag2",String.valueOf(getWindow().getCurrentFocus()));
                            int scrollY = svItem.getScrollY(); // For ScrollView
                            int scrollX = svItem.getScrollX(); // For HorizontalScrollView
                            // DO SOMETHING WITH THE SCROLL COORDINATES
                            svHeader.scrollTo(scrollX,scrollY);
                            Log.e("Tag3",String.valueOf(getWindow().getCurrentFocus()));
                        }
                    });
                    return false;
                }
            });
        }




    public void getDisplayValues(HashMap<String,OutstandingAgeBean> outageBean){


        TableLayout tlvisitSummList = (TableLayout) findViewById(R.id.crs_sku);
        TableLayout tlReportList = (TableLayout) findViewById(R.id.report_table);

        tlvisitSummList.removeAllViews();
        tlReportList.removeAllViews();
        LinearLayout llLineItemVal;
        LinearLayout llRetName;

        if (!flag) {
            llDelStockLayout.removeAllViews();
        }
        flag = false;
        llDelStockLayout = (LinearLayout) findViewById(R.id.ll_outstanding_age);

//        TableLayout tableHeading = (TableLayout) LayoutInflater.from(this)
//                .inflate(R.layout.table_view, null);
//
//        LinearLayout headerLayout = (LinearLayout) LayoutInflater
//                .from(this).inflate(
//                        R.layout.outsatanding_age_header, null);

       // tableHeading.addView(headerLayout);
        cursorLength = outageBean.size();

        if (cursorLength > 0) {

            Iterator iterator = outageBean.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next().toString();
                final OutstandingAgeBean outbean = outageBean.get(key);


                llLineItemVal = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.ll_outstanding_ageing_scroll_item, null, false);
                llRetName = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.ll_outstanding_ageingname, null, false);

                tvCustNo =(TextView)llRetName.findViewById(R.id.oa_cust_no) ;

                tvCustName =(TextView)llRetName.findViewById(R.id.oa_cust_name) ;
                tvCustCity =(TextView)llRetName.findViewById(R.id.oa_cust_city) ;
                tvtele =(TextView)llLineItemVal.findViewById(R.id.oa_tele) ;
                tvdistChannel =(TextView)llLineItemVal.findViewById(R.id.oa_dist_channel) ;
                tvsecuridtyDeposit =(TextView)llLineItemVal.findViewById(R.id.oa_security_deposit) ;
                tvcreditlimit  =(TextView)llLineItemVal.findViewById(R.id.oa_credit_limit) ;
                tvdebitBal =(TextView)llLineItemVal.findViewById(R.id.oa_total_debit) ;

                tv07 =(TextView)llLineItemVal.findViewById(R.id.oa_07_days) ;
                tv715 =(TextView)llLineItemVal.findViewById(R.id.oa_715_days) ;
                tv1530 =(TextView)llLineItemVal.findViewById(R.id.oa_1530_days) ;

                tvpast =(TextView)llLineItemVal.findViewById(R.id.oa_past_days) ;
                tvcurrent =(TextView)llLineItemVal.findViewById(R.id.oa_current_days) ;
                tv3160 =(TextView)llLineItemVal.findViewById(R.id.oa_3160_days) ;
                tv6190 =(TextView)llLineItemVal.findViewById(R.id.oa_6190_days) ;
                tv91120 =(TextView)llLineItemVal.findViewById(R.id.oa_91120_days) ;
                tv120 =(TextView)llLineItemVal.findViewById(R.id.oa_120_days) ;



                tv3045 =(TextView)llLineItemVal.findViewById(R.id.oa_3045_days) ;
                tv4560 =(TextView)llLineItemVal.findViewById(R.id.oa_4560_days) ;
                tv6090 =(TextView)llLineItemVal.findViewById(R.id.oa_6090_days) ;
                tv90120 =(TextView)llLineItemVal.findViewById(R.id.oa_90120_days) ;
                tv120180 =(TextView)llLineItemVal.findViewById(R.id.oa_120180_days) ;
                tv180 =(TextView)llLineItemVal.findViewById(R.id.oa_180_days) ;


                tvCustNo.setText(outbean.getCustomerNo());
                tvCustName.setText(outbean.getCustomerName());
                Constants.setFontSizeByMaxText(tvCustName);
                tvCustCity.setText(outbean.getCityName());
                tvtele.setText(outbean.getTelephone1());
                tvdistChannel.setText(outbean.getDistChannel());
                tvsecuridtyDeposit.setText(UtilConstants.removeLeadingZerowithTwoDecimal(outbean.getSecurityDeposit()));
                tvdebitBal.setText(UtilConstants.removeLeadingZerowithTwoDecimal(outbean.getTotalDebitBal()));
                tvcreditlimit.setText(UtilConstants.removeLeadingZerowithTwoDecimal(outbean.getCreditLimit()));
                tv07.setText(UtilConstants.commaSeparator(UtilConstants.removeLeadingZerowithTwoDecimal(outbean.getBucket1())));
                tv715.setText(UtilConstants.removeLeadingZerowithTwoDecimal(outbean.getBucket2()));
                tv1530.setText(UtilConstants.removeLeadingZerowithTwoDecimal(outbean.getBucket3()));
                tv3045.setText(UtilConstants.removeLeadingZerowithTwoDecimal(outbean.getBucket4()));
                tv4560.setText(UtilConstants.removeLeadingZerowithTwoDecimal(outbean.getBucket5()));
                tv6090.setText(UtilConstants.removeLeadingZerowithTwoDecimal(outbean.getBucket6()));
                tv90120.setText(UtilConstants.removeLeadingZerowithTwoDecimal(outbean.getBucket7()));
                tv120180.setText(UtilConstants.removeLeadingZerowithTwoDecimal(outbean.getBucket8()));
                tv180.setText(UtilConstants.removeLeadingZerowithTwoDecimal(outbean.getBucket9()));


                tvpast.setText(UtilConstants.removeLeadingZerowithTwoDecimal(outbean.getBucket1()));
                tvcurrent.setText(UtilConstants.removeLeadingZerowithTwoDecimal(outbean.getBucket2()));
                tv3160.setText(UtilConstants.removeLeadingZerowithTwoDecimal(outbean.getBucket3()));
                tv6190.setText(UtilConstants.removeLeadingZerowithTwoDecimal(outbean.getBucket4()));
                tv91120.setText(UtilConstants.removeLeadingZerowithTwoDecimal(outbean.getBucket5()));
                tv120.setText(UtilConstants.removeLeadingZerowithTwoDecimal(outbean.getBucket6()));

                // tableHeading.addView(llLineItemVal);


                if(!outbean.getCustomerNo().toString().isEmpty()) {

                    tlReportList.addView(llLineItemVal);
                    tlvisitSummList.addView(llRetName);
                }

            }



        }else {
            tlReportList = (TableLayout) findViewById(R.id.report_table);

            LinearLayout llEmptyLayout = (LinearLayout) LayoutInflater.from(OutstandingAgeReport.this)
                    .inflate(R.layout.empty_layout, null);

            tlReportList.addView(llEmptyLayout);
        }

        //llDelStockLayout.addView(tableHeading);

    }


//    public void getData(){
//        try {
//
//            outstandingAgeList = new ArrayList<OutstandingAgeBean>();
//
//            OutstandingAgeBean lb = null;
//
//            Cursor lvdValues = Constants.events
//                    .getEvents(Constants.OUTSTANDINGAGE_TABLE);
//            if (lvdValues != null) {
//                while (lvdValues.moveToNext()) {
//
//                    String CustomerNo,CustomerName,CityName,Telephone1,DistChannel,SecurityDeposit,CreditLimit,TotalDebitBal,
//                            OA0_7Days,OA7_15Days,OA15_30Days,OA30_45Days,OA45_60Days,OA60_90Days,OA90_120Days,OA120_180Days,OA180Days;
//
//String OA31_60Days,OA61_90Days,OA91_120Days,OA120Days,OAPast,OACurrent;
//
//                    CustomerNo = lvdValues
//                            .getString(lvdValues
//                                    .getColumnIndex(Constants.OACustomerNo));
//                    CustomerName = lvdValues
//                            .getString(lvdValues
//                                    .getColumnIndex(Constants.OACustomerName));
//                    CityName = lvdValues
//                            .getString(lvdValues
//                                    .getColumnIndex(Constants.OACityName));
//                    Telephone1 = lvdValues
//                            .getString(lvdValues
//                                    .getColumnIndex(Constants.OATelephone1));
//                    DistChannel = lvdValues
//                            .getString(lvdValues
//                                    .getColumnIndex(Constants.OADistChannel));
//                    SecurityDeposit = lvdValues
//                            .getString(lvdValues
//                                    .getColumnIndex(Constants.OASecurityDeposit));
//                    CreditLimit = lvdValues
//                            .getString(lvdValues
//                                    .getColumnIndex(Constants.OACreditLimit));
//                    TotalDebitBal = lvdValues
//                            .getString(lvdValues
//                                    .getColumnIndex(Constants.OATotalDebitBal));
//                    OA0_7Days = lvdValues
//                            .getString(lvdValues
//                                    .getColumnIndex(Constants.OA0_7Days));
//                    OA7_15Days = lvdValues
//                            .getString(lvdValues
//                                    .getColumnIndex(Constants.OA7_15Days));
//
//                    OA15_30Days = lvdValues
//                            .getString(lvdValues
//                                    .getColumnIndex(Constants.OA15_30Days));
//
//
//                    OA31_60Days = lvdValues
//                            .getString(lvdValues
//                                    .getColumnIndex(Constants.OA3160Days));
//
//                    OA61_90Days = lvdValues
//                            .getString(lvdValues
//                                    .getColumnIndex(Constants.OA6190Days));
//
//                    OA91_120Days = lvdValues
//                            .getString(lvdValues
//                                    .getColumnIndex(Constants.OA91120Days));
//
//                    OA120Days = lvdValues
//                            .getString(lvdValues
//                                    .getColumnIndex(Constants.OA120Days));
//
//                    OAPast = lvdValues
//                            .getString(lvdValues
//                                    .getColumnIndex(Constants.OAPastDays));
//
//                    OACurrent = lvdValues
//                            .getString(lvdValues
//                                    .getColumnIndex(Constants.OACurrentDays));
//
//
//                    OA30_45Days = lvdValues
//                            .getString(lvdValues
//                                    .getColumnIndex(Constants.OA30_45Days));
//                    OA45_60Days = lvdValues
//                            .getString(lvdValues
//                                    .getColumnIndex(Constants.OA45_60Days));
//                    OA60_90Days = lvdValues
//                            .getString(lvdValues
//                                    .getColumnIndex(Constants.OA60_90Days));
//                    OA90_120Days = lvdValues
//                            .getString(lvdValues
//                                    .getColumnIndex(Constants.OA90_120Days));
//                    OA120_180Days = lvdValues
//                            .getString(lvdValues
//                                    .getColumnIndex(Constants.OA120_180Days));
//
//                    OA180Days = lvdValues
//                            .getString(lvdValues
//                                    .getColumnIndex(Constants.OA180Days));
//
//
//
//
//
//                    if (CustomerNo != null) {
//                        lb = new OutstandingAgeBean();
//                        lb.setCustomerNo(CustomerNo);
//                        lb.setCityName(CityName);
//                        lb.setCustomerName(CustomerName);
//                        lb.setTelephone1(Telephone1);
//                        lb.setDistChannel(DistChannel);
//                        lb.setSecurityDeposit(SecurityDeposit);
//                        lb.setCreditLimit(CreditLimit);
//                        lb.setTotalDebitBal(TotalDebitBal);
//                        lb.setOA0_7Days(OA0_7Days);
//                        lb.setOA7_15Days(OA7_15Days);
//                        lb.setOA15_30Days(OA15_30Days);
//
//                        lb.setOA31_60Days(OA31_60Days);
//                        lb.setOA61_90Days(OA61_90Days);
//                        lb.setOA91_120Days(OA91_120Days);
//                        lb.setOA120Days(OA120Days);
//                        lb.setOAPast(OAPast);
//                        lb.setOACurrent(OACurrent);
//
//
//                        lb.setOA30_45Days(OA30_45Days);
//                        lb.setOA45_60Days(OA45_60Days);
//                        lb.setOA60_90Days(OA60_90Days);
//                        lb.setOA90_120Days(OA90_120Days);
//                        lb.setOA120_180Days(OA120_180Days);
//                        lb.setOA180Days(OA180Days);
//                        outstandingAgeList.add(lb);
//                    }
//
//                }
//                lvdValues.deactivate();
//                lvdValues.close();
//
//                getDisplayValues(outstandingAgeList);
//            }
//
//        } catch (Exception e) {
//            // if (e != null)
//            // LogController.getInstance(getApplicationContext()).E(
//            // e.getMessage());
//
//            String err = e.getMessage();
//
//            //Toast.makeText(this, err, Toast.LENGTH_LONG).show();
//        }
//
//
//    }


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
        AlertDialog.Builder builder = new AlertDialog.Builder(OutstandingAgeReport.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_competition_information).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        OutstandingAgeReport.this.finish();
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
