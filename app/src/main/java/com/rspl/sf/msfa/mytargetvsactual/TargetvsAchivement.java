package com.rspl.sf.msfa.mytargetvsactual;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.TargetVsAchivementBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

/**
 * Created by ccb on 29-09-2017.
 */

public class TargetvsAchivement extends AppCompatActivity {



    HorizontalScrollView svHeader = null, svItem = null;
    private LinearLayout llDelStockLayout;

    ArrayList<TargetVsAchivementBean> targetVsachivementList = new ArrayList<>();
    boolean flag = true;
    int cursorLength = 0;
    SQLiteDatabase db = Constants.EventUserHandler;;

    String[] dealerCode = {"102162","102180","102193","102082"};
    String[] dealerName = {"PRAKASH DEVRAVASA KSHTRIYA","RADHESHYAM  BRIJLAL RAWAT","RAMDEVBABA TILES","MAHAVIR TRADERS"};
    String[] dealercity = {"MG-MANMAD","MG-NASIK","MG-YAVATMAL","MG-NASIK"};

    String[] curMonthTraget = {"1208","2051","880","790"};
    String[] prorataTrag = {"1268.4","2153.5","924","829.5"};
    String[] salesAchived = {"900","1609","830","700"};
    String[] prorataPer = {"70.9","74.7","89.8","84.3"};
    String[] Balqty = {"368.4","544.5","94","129.5"};

   // String[] dailyTrag = {"176,708.28","902.921.57","1.700.00","856.804.86","109.538.99","  0.00","155.670.00"," 3.600.00","0.00","0.00","579.740.00"};
//    String[] s715 = {" 0.00"," 0.00"," 0.00"," 0.00"," 0.00"," 0.00"," 70.850.43","0.00","464.248.06"," 0.00","3.100.00"};
//    String[] s1530 = {" 0.00"," 0.00","990.00"," 0.00"," 0.00"," 0.00"," 0.00","9,130.00","0.00","0.00","0.00"};
//    String[] s3045 = {"0.00","0.00","117,900.00","0.00","0.00","0.00","0.00","201,600.00","0.00","10,200.00","119,831.78"};
//    String[] s4560 = {"0.00","0.00","247,345.87","0.00","0.00","0.00","0.00","0.00","0.00","0.00","0.00"};
//    String[] s6090 = {"0.00","0.00","0.00","0.00","0.00","0.00","0.00","3.100.00","0.00","0.00","0.00"};
//    String[] s90120 = {"0.00","0.00","0.00","0.00","0.00","0.00","0.00","40.488.89","0.00","0.00","0.00"};
//    String[] s180 = {"0.00","0.00","0.00","0.00","0.00","1,352,917.53","0.00","0.00","0.00","0,00","0.00"};

 int daysleftinMonth,daysfinishedinMonth;
    TextView tvDealerCode, tvDealerName, tvDealerCity, tvSeptTGT, tvProrataTarget, tvSaleACVD, tvProrataAchivement, tvBalQty, tvDailytarget,tv715,tv1530,tv3045,tv4560,tv6090,tv90120,tv120180,tv180;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traget_vs_achivement);

        //ActionBarView.initActionBarView(this, true,getString(R.string.title_Target_vs_Achivement));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_Target_vs_Achivement), 0);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Constants.deleteTable(db,Constants.DEALER_TARGET_VS_ACHIVEMENT_TABLE);


        Calendar calCurr = Calendar.getInstance();


           daysleftinMonth =  calCurr.getActualMaximum(Calendar.DAY_OF_MONTH) - calCurr.get(Calendar.DAY_OF_MONTH);

        daysfinishedinMonth =  calCurr.getActualMaximum(Calendar.DAY_OF_MONTH) - daysleftinMonth;



        for(int i=0;i<dealerCode.length;i++){

            Hashtable dbItemTable = new Hashtable();

            dbItemTable.put(Constants.TADealerNo, dealerCode[i]);
            dbItemTable.put(Constants.TADealerName, dealerName[i]);
            dbItemTable.put(Constants.TADealerCity, dealercity[i]);
            dbItemTable.put(Constants.TACurMonthTraget, curMonthTraget[i]);
            dbItemTable.put(Constants.TAProrataTraget,prorataTrag[i]);
            dbItemTable.put(Constants.TASaleACVD,salesAchived[i]);
            dbItemTable.put(Constants.TAProrataAchivement, String.format("%.2f", ((Double.valueOf(prorataTrag[i]) - Double.valueOf(salesAchived[i]))/daysfinishedinMonth)));
            dbItemTable.put(Constants.TABalanceQty,  String.format("%.2f",((Double.valueOf(prorataTrag[i]) - Double.valueOf(salesAchived[i])))));
            dbItemTable.put(Constants.TADailyTarget,String.format("%.2f",((Double.valueOf(prorataTrag[i]) - Double.valueOf(salesAchived[i])))/daysleftinMonth));
//            dbItemTable.put(Constants.OA15_30Days, s1530[i]);
//            dbItemTable.put(Constants.OA30_45Days, s3045[i]);
//            dbItemTable.put(Constants.OA45_60Days, s4560[i]);
//            dbItemTable.put(Constants.OA60_90Days, s6090[i]);`
//            dbItemTable.put(Constants.OA90_120Days, s90120[i]);
//            dbItemTable.put(Constants.OA120_180Days, "0.00");
//            dbItemTable.put(Constants.OA180Days, s180[i]);


            Constants.events.insert(Constants.DEALER_TARGET_VS_ACHIVEMENT_TABLE,
                    dbItemTable);

        }
        getData();

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




    public void getDisplayValues(ArrayList<TargetVsAchivementBean> outageBean){


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
            for (int i = 0; i < cursorLength; i++) {
                final TargetVsAchivementBean outbean = outageBean.get(i);
                final int selvalue = i;
//                LinearLayout rowRelativeLayout = (LinearLayout) LayoutInflater
//                        .from(this).inflate(
//                                R.layout.outstanding_age_item_list, null);

                llLineItemVal = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.ll_traget_vs_achivement_scroll_item, null, false);
                llRetName = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.ll_traget_vs_achivement_name, null, false);

                tvDealerCode =(TextView)llRetName.findViewById(R.id.ta_dealer_no) ;

                tvDealerName =(TextView)llLineItemVal.findViewById(R.id.ta_dealer_name) ;
                tvDealerCity =(TextView)llLineItemVal.findViewById(R.id.ta_city) ;
                tvSeptTGT =(TextView)llLineItemVal.findViewById(R.id.ta_sept_tgt) ;
                tvProrataTarget =(TextView)llLineItemVal.findViewById(R.id.ta_prorata_traget) ;
                tvProrataAchivement=(TextView)llLineItemVal.findViewById(R.id.ta_prorata_achivement) ;
                tvSaleACVD =(TextView)llLineItemVal.findViewById(R.id.ta_scale_acvd) ;
                tvBalQty =(TextView)llLineItemVal.findViewById(R.id.ta_bal_qty) ;

                tvDailytarget =(TextView)llLineItemVal.findViewById(R.id.ta_daily_traget) ;
//                tv715 =(TextView)llLineItemVal.findViewById(R.id.oa_715_days) ;
//                tv1530 =(TextView)llLineItemVal.findViewById(R.id.oa_1530_days) ;
//                tv3045 =(TextView)llLineItemVal.findViewById(R.id.oa_3045_days) ;
//                tv4560 =(TextView)llLineItemVal.findViewById(R.id.oa_4560_days) ;
//                tv6090 =(TextView)llLineItemVal.findViewById(R.id.oa_6090_days) ;
//                tv90120 =(TextView)llLineItemVal.findViewById(R.id.oa_90120_days) ;
//                tv120180 =(TextView)llLineItemVal.findViewById(R.id.oa_120180_days) ;
//                tv180 =(TextView)llLineItemVal.findViewById(R.id.oa_180_days) ;


                tvDealerCode.setText(outbean.getDealerNo());
                tvDealerName.setText(outbean.getDealerName());
                Constants.setFontSizeByMaxText(tvDealerName);
                tvDealerCity.setText(outbean.getDealerCity());
                tvSeptTGT.setText(outbean.getCurMonthTarget());
                tvProrataTarget.setText(outbean.getProrataTraget());
                tvProrataAchivement.setText(outbean.getProrataAchivement());
                tvSaleACVD.setText(outbean.getSaleAcvd());
                tvBalQty.setText(outbean.getBalQty());
                tvDailytarget.setText(outbean.getDailytraget());
//                tv715.setText(outbean.getOA7_15Days());
//                tv1530.setText(outbean.getOA15_30Days());
//                tv3045.setText(outbean.getOA30_45Days());
//                tv4560.setText(outbean.getOA45_60Days());
//                tv6090.setText(outbean.getOA60_90Days());
//                tv90120.setText(outbean.getOA90_120Days());
//                tv120180.setText(outbean.getOA120_180Days());
//                tv180.setText(outbean.getOA180Days());

                // tableHeading.addView(llLineItemVal);

                tlReportList.addView(llLineItemVal);
                tlvisitSummList.addView(llRetName);
            }

            // llDelStockLayout.addView(tableHeading);

        }else {
            tlReportList = (TableLayout) findViewById(R.id.report_table);

            LinearLayout llEmptyLayout = (LinearLayout) LayoutInflater.from(TargetvsAchivement.this)
                    .inflate(R.layout.empty_layout, null);

            tlReportList.addView(llEmptyLayout);
        }

        //llDelStockLayout.addView(tableHeading);

    }


    public void getData(){
        try {

            targetVsachivementList = new ArrayList<TargetVsAchivementBean>();

            TargetVsAchivementBean lb = null;

            Cursor lvdValues = Constants.events
                    .getEvents(Constants.DEALER_TARGET_VS_ACHIVEMENT_TABLE);
            if (lvdValues != null) {
                while (lvdValues.moveToNext()) {

                    String dealerNo,dealerName,dealerCity,curmonthTarget,prorataTarget,saleACVD,prorataAchivement,Balqty,dailyTarget;



                    dealerNo = lvdValues
                            .getString(lvdValues
                                    .getColumnIndex(Constants.TADealerNo));
                    dealerName = lvdValues
                            .getString(lvdValues
                                    .getColumnIndex(Constants.TADealerName));
                    dealerCity = lvdValues
                            .getString(lvdValues
                                    .getColumnIndex(Constants.TADealerCity));
                    curmonthTarget = lvdValues
                            .getString(lvdValues
                                    .getColumnIndex(Constants.TACurMonthTraget));
                    prorataTarget = lvdValues
                            .getString(lvdValues
                                    .getColumnIndex(Constants.TAProrataTraget));
                    saleACVD = lvdValues
                            .getString(lvdValues
                                    .getColumnIndex(Constants.TASaleACVD));
                    prorataAchivement = lvdValues
                            .getString(lvdValues
                                    .getColumnIndex(Constants.TAProrataAchivement));
                    Balqty = lvdValues
                            .getString(lvdValues
                                    .getColumnIndex(Constants.TABalanceQty));
                    dailyTarget = lvdValues
                            .getString(lvdValues
                                    .getColumnIndex(Constants.TADailyTarget));






                    if (dealerNo != null) {
                        lb = new TargetVsAchivementBean();
                        lb.setDealerNo(dealerNo);
                        lb.setDealerName(dealerName);
                        lb.setDealerCity(dealerCity);
                        lb.setCurMonthTarget(curmonthTarget);
                        lb.setProrataTraget(prorataTarget);
                        lb.setSaleAcvd(saleACVD);
                        lb.setProrataAchivement(prorataAchivement);
                        lb.setBalQty(Balqty);
                        lb.setDailytraget(dailyTarget);
                        targetVsachivementList.add(lb);
                    }

                }
                lvdValues.deactivate();
                lvdValues.close();

                getDisplayValues(targetVsachivementList);
            }

        } catch (Exception e) {
            // if (e != null)
            // LogController.getInstance(getApplicationContext()).E(
            // e.getMessage());

            String err = e.getMessage();

            //Toast.makeText(this, err, Toast.LENGTH_LONG).show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(TargetvsAchivement.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_competition_information).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        TargetvsAchivement.this.finish();
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
