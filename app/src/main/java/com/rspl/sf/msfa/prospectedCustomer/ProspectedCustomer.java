package com.rspl.sf.msfa.prospectedCustomer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.MyUtils;
import com.rspl.sf.msfa.interfaces.DialogCallBack;
import com.rspl.sf.msfa.routeplan.RoutePlanListActivity;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.Hashtable;
import java.util.Random;

public class ProspectedCustomer extends AppCompatActivity implements UIListener{

EditText etOfficerEmployeeCode,etCounterName,etLongitudeAndLatitude,etCounterType,etContactPerson,etPCMobileNo,
        etProspectecCustomerAddress,etPCDistrict,etTaluka,etPinCode,etBlock,etTotalTradePottential,etTotalNonTradePottential,
        etPottentialAvailable,etUTCL,etOCL,etLAF,etACC,etPOPDistributed,etPCRemarks,etPCCity;

    Spinner spCounterType;

    String [] counterTypeArray = {"None","Dealer","Retailer","Non Trade"};
    String [] counterTypeCode = {"00","01","02","03"};
    String selectedcounterType="";
    private ProgressDialog pdLoadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prospected_customer);

       // ActionBarView.initActionBarView(this, true,getString(R.string.lbl_prospected_customer));


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_prospected_customer), 0);
        etOfficerEmployeeCode = (EditText)findViewById(R.id.edit_pc_officer_emp_code);
        etCounterName = (EditText)findViewById(R.id.edit_pc_counter_name);
        etLongitudeAndLatitude = (EditText)findViewById(R.id.edit_pc_lat_long);
        spCounterType = (Spinner) findViewById(R.id.sp_pc_counter_type);
        etContactPerson = (EditText)findViewById(R.id.edit_pc_contact_person);
        etPCMobileNo = (EditText)findViewById(R.id.edit_pc_mob_no);
        etProspectecCustomerAddress = (EditText)findViewById(R.id.edit_pc_address);
        etPCCity = (EditText)findViewById(R.id.edit_pc_city);
        etPCDistrict = (EditText)findViewById(R.id.edit_pc_district);
        etTaluka = (EditText)findViewById(R.id.edit_pc_taluka);
        etPinCode = (EditText)findViewById(R.id.edit_pc_pin_code);
        etBlock = (EditText)findViewById(R.id.edit_pc_block);
        etTotalTradePottential = (EditText)findViewById(R.id.edit_pc_trade_potential);
        etTotalNonTradePottential = (EditText)findViewById(R.id.edit_pc_total_non_potential);
        etPottentialAvailable = (EditText)findViewById(R.id.edit_pc_potential_available);
        etUTCL = (EditText)findViewById(R.id.edit_pc_utcl);
        etOCL = (EditText)findViewById(R.id.edit_pc_ocl);
        etLAF = (EditText)findViewById(R.id.edit_pc_laf);
        etACC = (EditText)findViewById(R.id.edit_pc_acc);
        etPOPDistributed = (EditText)findViewById(R.id.edit_pc_pop_distributed);
        etPCRemarks = (EditText)findViewById(R.id.edit_pc_remarks);

        ArrayAdapter<String> searchadapter = new ArrayAdapter<>(this, R.layout.custom_textview, counterTypeArray);
        searchadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCounterType.setAdapter(searchadapter);

        spCounterType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {
                selectedcounterType = counterTypeCode[position];
                if(!selectedcounterType.trim().equalsIgnoreCase("00")){
                    spCounterType.setBackgroundResource(R.drawable.spinner_bg);
                }
               /* else{
                    spCounterType.setBackgroundResource(R.drawable.error_spinner);
                }*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


        etCounterName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!etCounterName.getText().toString().trim().equalsIgnoreCase("")){
                    etCounterName.setBackgroundResource(R.drawable.edittext);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPCMobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
             //   if(etPCMobileNo.getText().toString().trim().length()==10) {
                    etPCMobileNo.setBackgroundResource(R.drawable.edittext);
              /*  }else{
                    etPCMobileNo.setBackgroundResource(R.drawable.edittext_border);
                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etProspectecCustomerAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!etProspectecCustomerAddress.getText().toString().trim().equalsIgnoreCase("")){
                    etProspectecCustomerAddress.setBackgroundResource(R.drawable.edittext);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPCCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!etPCCity.getText().toString().trim().equalsIgnoreCase("")) {
                    etPCCity.setBackgroundResource(R.drawable.edittext);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPCDistrict.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(!etPCDistrict.getText().toString().trim().equalsIgnoreCase("")) {
                    etPCDistrict.setBackgroundResource(R.drawable.edittext);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPinCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               /* if(etPinCode.getText().toString().trim().length()!=6) {

                    etPinCode.setBackgroundResource(R.drawable.edittext_border);
                }else{*/
                    etPinCode.setBackgroundResource(R.drawable.edittext);
               // }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });





        getData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_prospected_customer, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_prospected_customer:
              //  saveData();
                if(validate()){
                    saveDataToCustomer();
                }

                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ProspectedCustomer.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_competition_information).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        ProspectedCustomer.this.finish();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();
    }

    private boolean validate() {
        boolean valid= false;

      if(  (etCounterName.getText().toString().trim().equalsIgnoreCase(""))
              ||(etPCMobileNo.getText().toString().trim().length()==0)
              ||(selectedcounterType.equalsIgnoreCase(""))
              ||(etProspectecCustomerAddress.getText().toString().trim().equalsIgnoreCase(""))
              ||(etPCCity.getText().toString().trim().equalsIgnoreCase(""))
              ||(etPCDistrict.getText().toString().trim().equalsIgnoreCase(""))
              ||(etPinCode.getText().toString().trim().length()==0)
            ||(selectedcounterType.trim().equalsIgnoreCase("00"))
              ){
          if((etCounterName.getText().toString().trim().equalsIgnoreCase(""))){
              etCounterName.setBackgroundResource(R.drawable.edittext_border);
          }
          if((etPCMobileNo.getText().toString().trim().equalsIgnoreCase(""))){
              etPCMobileNo.setBackgroundResource(R.drawable.edittext_border);
          }
          if((selectedcounterType.trim().equalsIgnoreCase("00"))){
           spCounterType.setBackgroundResource(R.drawable.error_spinner);
          }
          if((etProspectecCustomerAddress.getText().toString().trim().equalsIgnoreCase(""))){
              etProspectecCustomerAddress.setBackgroundResource(R.drawable.edittext_border);
          }
          if((etPCCity.getText().toString().trim().equalsIgnoreCase(""))){
              etPCCity.setBackgroundResource(R.drawable.edittext_border);
          }
          if((etPCDistrict.getText().toString().trim().equalsIgnoreCase(""))){
              etPCDistrict.setBackgroundResource(R.drawable.edittext_border);
          }
          if((etPinCode.getText().toString().trim().equalsIgnoreCase(""))){

                  etPinCode.setBackgroundResource(R.drawable.edittext_border);
          }
          MyUtils.dialogBoxWithButton(this, "",  "Please enter mandatory fields", "Ok", "", new DialogCallBack() {
              @Override
              public void clickedStatus(boolean clickedStatus) {

              }
          });


          valid=false;
      }else{

          if((etPCMobileNo.getText().toString().trim().length()!=10)){
              valid=false;
              etPCMobileNo.setBackgroundResource(R.drawable.edittext_border);
              UtilConstants.showAlert("Please enter valid mobile number", ProspectedCustomer.this);
          }else if((etPCMobileNo.getText().toString().trim().equalsIgnoreCase("0000000000"))){
              valid=false;
              etPCMobileNo.setBackgroundResource(R.drawable.edittext_border);
              UtilConstants.showAlert("Please enter valid mobile number", ProspectedCustomer.this);
          }
          else if((etPinCode.getText().toString().trim().length()!=6)){
              valid=false;
              etPinCode.setBackgroundResource(R.drawable.edittext_border);
              UtilConstants.showAlert("Please enter valid pin code", ProspectedCustomer.this);
          }else if((etPinCode.getText().toString().trim().equalsIgnoreCase("000000"))){
              valid=false;
              etPinCode.setBackgroundResource(R.drawable.edittext_border);
              UtilConstants.showAlert("Please enter valid pin code", ProspectedCustomer.this);
          }
          else{
              valid=true;
          }

      }
       return valid;

    }


    public void saveData(){


       try {
           Hashtable hashtable = new Hashtable();
           hashtable.put(Constants.OfficerEmployeeCode, etOfficerEmployeeCode.getText().toString());
           hashtable.put(Constants.CounterName, etCounterName.getText().toString());
           hashtable.put(Constants.LongitudeAndLatitude, etLongitudeAndLatitude.getText().toString());
           hashtable.put(Constants.CounterType, selectedcounterType);
           hashtable.put(Constants.ContactPerson, etContactPerson.getText().toString());
           hashtable.put(Constants.PCMobileNo, etPCMobileNo.getText().toString());
           hashtable.put(Constants.ProspectecCustomerAddress, etProspectecCustomerAddress.getText().toString());
           hashtable.put(Constants.PCcity, etPCCity.getText().toString());
           hashtable.put(Constants.PCDistrict, etPCDistrict.getText().toString());
           hashtable.put(Constants.Taluka, etTaluka.getText().toString());
           hashtable.put(Constants.PinCode, etPinCode.getText().toString());
           hashtable.put(Constants.Block, etBlock.getText().toString());
           hashtable.put(Constants.TotalTradePottential, etTotalTradePottential.getText().toString());
           hashtable.put(Constants.TotalNonTradePottential, etTotalNonTradePottential.getText().toString());
           hashtable.put(Constants.PottentialAvailable, etPottentialAvailable.getText().toString());
           hashtable.put(Constants.UTCL, etUTCL.getText().toString());
           hashtable.put(Constants.OCL, etOCL.getText().toString());
           hashtable.put(Constants.LAF, etLAF.getText().toString());
           hashtable.put(Constants.ACC, etACC.getText().toString());
           hashtable.put(Constants.POPDistributed, etPOPDistributed.getText().toString());
           hashtable.put(Constants.PCRemarks, etPCRemarks.getText().toString());
           String cust_no = (System.currentTimeMillis() + "").substring(5, 10);
           hashtable.put(Constants.CustomerNo, cust_no);


           Constants.events.insert(Constants.PROSPECTED_TABLE,
                   hashtable);

           MyUtils.dialogBoxWithButton(this, "",  "Prospected customer created successfully", "Ok", "", new DialogCallBack() {
               @Override
               public void clickedStatus(boolean clickedStatus) {

                   Intent retList = new Intent(ProspectedCustomer.this,
                           RoutePlanListActivity.class);
                   startActivity(retList);

               }
           });


       } catch (Exception e) {
           e.printStackTrace();
       }

   }


    public void saveDataToCustomer(){


        try {
            Hashtable hashtable = new Hashtable();
            Random rn = new Random();
            int i = rn.nextInt();
            GUID guid = GUID.newRandom();
          //  hashtable.put(Constants.CustomerNo, i+"");
            hashtable.put(Constants.CPGUID, guid.toString36());

            hashtable.put(Constants.Name, etCounterName.getText().toString());
          //  hashtable.put(Constants.LongitudeAndLatitude, etLongitudeAndLatitude.getText().toString());
            hashtable.put(Constants.CPTypeID, selectedcounterType);
        //   hashtable.put(Constants.ContactPerson, etContactPerson.getText().toString());
            hashtable.put(Constants.Mobile1, etPCMobileNo.getText().toString());
            hashtable.put(Constants.Address1, etProspectecCustomerAddress.getText().toString());
            hashtable.put(Constants.CityDesc, etPCCity.getText().toString());
            hashtable.put(Constants.DistrictDesc, etPCDistrict.getText().toString());
         //   hashtable.put(Constants.Taluka, etTaluka.getText().toString());
            hashtable.put(Constants.PostalCode, etPinCode.getText().toString());
         //   hashtable.put(Constants.Block, etBlock.getText().toString());
        //    hashtable.put(Constants.TotalTradePottential, etTotalTradePottential.getText().toString());
        //    hashtable.put(Constants.TotalNonTradePottential, etTotalNonTradePottential.getText().toString());
        //    hashtable.put(Constants.PottentialAvailable, etPottentialAvailable.getText().toString());
        //    hashtable.put(Constants.UTCL, etUTCL.getText().toString());
        //    hashtable.put(Constants.OCL, etOCL.getText().toString());
        //    hashtable.put(Constants.LAF, etLAF.getText().toString());
        //    hashtable.put(Constants.ACC, etACC.getText().toString());
         //   hashtable.put(Constants.POPDistributed, etPOPDistributed.getText().toString());
        //    hashtable.put(Constants.PCRemarks, etPCRemarks.getText().toString());
        //    String cust_no = (System.currentTimeMillis() + "").substring(5, 10);
        //    hashtable.put(Constants.CustomerNo, cust_no);

/*

            Constants.events.insert(Constants.PROSPECTED_TABLE,
                    hashtable);
*/

            OfflineManager.createProspectedCustomer(hashtable,this);




        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public void getData(){
        try {



            Cursor lvdValues = Constants.events
                    .getEvents(Constants.PROSPECTED_TABLE);
            if (lvdValues != null) {
                while (lvdValues.moveToNext()) {

                    String lvdMatDesc = "";
                    String lvdBasePrice = "";



                    lvdMatDesc = lvdValues
                            .getString(lvdValues
                                    .getColumnIndex(Constants.OfficerEmployeeCode));
                    lvdBasePrice = lvdValues
                            .getString(lvdValues
                                    .getColumnIndex(Constants.CounterName));




                }
                lvdValues.deactivate();
                lvdValues.close();
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
    public void onRequestError(int i, Exception e) {
        MyUtils.dialogBoxWithButton(this, "",  "Prospected customer create failed", "Ok", "", new DialogCallBack() {
            @Override
            public void clickedStatus(boolean clickedStatus) {

                Intent retList = new Intent(ProspectedCustomer.this,
                        RoutePlanListActivity.class);
                startActivity(retList);

            }
        });

    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {

        MyUtils.dialogBoxWithButton(this, "",  "Prospected customer created successfully", "Ok", "", new DialogCallBack() {
            @Override
            public void clickedStatus(boolean clickedStatus) {

                Intent retList = new Intent(ProspectedCustomer.this,
                        RoutePlanListActivity.class);
                startActivity(retList);

            }
        });

    }
}
