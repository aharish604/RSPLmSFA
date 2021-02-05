package com.rspl.sf.msfa;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.appointment.AppointmentCreate;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;

@SuppressLint("NewApi")
public class AddressFragment extends Fragment implements View.OnClickListener {
    String mStrCPGUID="",mStrCustomerID="",mStrRetName="";

    ODataPropMap oDataProperties;
    ODataProperty oDataProperty;
    ImageView iv_mail, iv_sms, iv_call,iv_appointment;

    private String mStrEmailID = "",mStrAddressTwo="",mStrAddressThree="",
            mStrAddressFour="",mStrLandmark="",mStrCityDesc="",mStrDistrictDesc="",selCPTypeDesc="",
            mStrOwnerName="",mStrPostalCode ="";
    String mDistributorName = "", mContactNum = "", mRetCategory = "", mClassification = "",
            mWeeklyOff = "", mDOB = "", mAnniversary = "", mStrCPTypeId = "", mStrFirstAddress = "";

    ODataGuid mCpGuid = null;
String address ="",mobNo ="",comeFrom ="";
    private View myInflatedView = null;
    TextView tvFirstAddress,tvSecondAddress,tv_third_address,
            tv_fourth_address,tv_land_mark,tv_postalCode,tv_OwnerName,
            tvDistributorName,tvContactNum,tvRetailerCategory,tvClassification,tvWeeklyOff,tvDOB,tvAnniversary,tvEmilView;
    Context mContext;
    public static AddressFragment newInstance(String mStrRetId,String mStrRetName,String mStrCpGuid,String address,String mobileNo,String comefrom) {

        AddressFragment addressFragment = new AddressFragment();

        Bundle bundle = new Bundle();
		bundle.putString(Constants.CPGUID, mStrCpGuid);
		bundle.putString(Constants.RetName, mStrRetName);
		bundle.putString(Constants.CPNo, mStrRetId);
        bundle.putString("Address", address);
        bundle.putString("MobNo", mobileNo);
        bundle.putString("ComeFrom", comefrom);
        addressFragment.setArguments(bundle);
        return addressFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = container.getContext();
        mStrCPGUID = getArguments().getString(Constants.CPGUID);
        mStrCustomerID = getArguments().getString(Constants.CPNo);
        mStrRetName = getArguments().getString(Constants.RetName);
        myInflatedView = inflater.inflate(R.layout.fragment_address_lay, container,false);
        address = getArguments().getString("Address");
        mobNo = getArguments().getString("MobNo");
        comeFrom =  getArguments().getString("ComeFrom");


        iv_call = (ImageView)myInflatedView.findViewById(R.id.call);
        iv_call.setOnClickListener(this);

        iv_sms = (ImageView)myInflatedView.findViewById(R.id.sms);
        iv_sms.setOnClickListener(this);

        iv_mail = (ImageView)myInflatedView.findViewById(R.id.mail);
        iv_mail.setOnClickListener(this);

        ImageView iv_whatsApp = (ImageView) myInflatedView.findViewById(R.id.whats_app);
        iv_whatsApp.setOnClickListener(this);

        iv_appointment = (ImageView)myInflatedView.findViewById(R.id.appointment);
        iv_appointment.setOnClickListener(this);

        onInitUI();
        if(!comeFrom.equalsIgnoreCase(Constants.ProspectiveCustomerList) ) {
            if(Constants.CustomerType.equalsIgnoreCase("")){
                onRetailerDetails();
            }
        }else if(comeFrom.equalsIgnoreCase(Constants.ProspectiveCustomerList)){
            onProspectiveCustomerDetails();
        }
        setValuesToUI();
        return myInflatedView;
    }
    /*
          * TODO This method initialize UI
          */
    private void onInitUI(){
         tvFirstAddress = (TextView) myInflatedView.findViewById(R.id.tv_add1);
         tvSecondAddress = (TextView) myInflatedView.findViewById(R.id.tv_second_address);
         tv_third_address = (TextView) myInflatedView.findViewById(R.id.tv_third_address);
         tv_fourth_address = (TextView) myInflatedView.findViewById(R.id.tv_fourth_address);
         tv_land_mark = (TextView) myInflatedView.findViewById(R.id.tv_land_mark);
         tv_postalCode = (TextView) myInflatedView.findViewById(R.id.tv_postal_code);
         tv_OwnerName = (TextView) myInflatedView.findViewById(R.id.tv_ownerName);
         tvDistributorName = (TextView) myInflatedView.findViewById(R.id.tv_distributor_name);
         tvContactNum = (TextView) myInflatedView.findViewById(R.id.tv_contact_num);
         tvRetailerCategory = (TextView) myInflatedView.findViewById(R.id.tv_ret_category);
         tvClassification = (TextView) myInflatedView.findViewById(R.id.tv_classification);
         tvWeeklyOff = (TextView) myInflatedView.findViewById(R.id.tv_weekly_off);
         tvDOB = (TextView) myInflatedView.findViewById(R.id.tv_date_of_birth);
         tvAnniversary = (TextView) myInflatedView.findViewById(R.id.tv_anniversary);
         tvEmilView = (TextView) myInflatedView.findViewById(R.id.tv_email_id);
    }
    /*
         * TODO This method set values to UI
         */
    private void setValuesToUI(){
        tvRetailerCategory.setText(selCPTypeDesc);
        tvDistributorName.setText(mDistributorName);

        if(!comeFrom.equals("ProspectiveCustomerList")){

            tvContactNum.setText(mContactNum);
        }else{

            tvContactNum.setText(mobNo);
        }

        tvClassification.setText(mClassification);

        if (!TextUtils.isEmpty(mDOB)) {
            tvDOB.setText(UtilConstants.convertDateIntoDeviceFormat(getContext(),mDOB));
        }
        if (!TextUtils.isEmpty(mAnniversary)) {
            tvAnniversary.setText(UtilConstants.convertDateIntoDeviceFormat(getContext(),mAnniversary));
        }
        tvWeeklyOff.setText(mWeeklyOff);


        if(!comeFrom.equals("ProspectiveCustomerList")){

            if(!mStrFirstAddress.equalsIgnoreCase("")){
                tvFirstAddress.setText(mStrFirstAddress);
            }else{
                tvFirstAddress.setVisibility(View.GONE);

            }
        }else{

            tvFirstAddress.setVisibility(View.VISIBLE);
            tvFirstAddress.setText(address);
        }


        if(!mStrAddressTwo.equalsIgnoreCase("")){
            tvSecondAddress.setText(mStrAddressTwo);
        }else{
            tvSecondAddress.setVisibility(View.GONE);
        }

        if(!mStrAddressThree.equalsIgnoreCase("")){
            tv_third_address.setText(mStrAddressThree);
        }else{
            tv_third_address.setVisibility(View.GONE);
        }

        if(!mStrAddressFour.equalsIgnoreCase("")){
            tv_fourth_address.setText(mStrAddressFour);
        }else{
            tv_fourth_address.setVisibility(View.GONE);
        }

        if(!mStrPostalCode.equalsIgnoreCase("") && !mStrDistrictDesc.equalsIgnoreCase("")){
            tv_postalCode.setText(mStrDistrictDesc+" "+mStrPostalCode);
        }else if(!mStrPostalCode.equalsIgnoreCase("") && mStrDistrictDesc.equalsIgnoreCase("")){
            tv_postalCode.setText(mStrPostalCode);
        }else if(mStrPostalCode.equalsIgnoreCase("") && !mStrDistrictDesc.equalsIgnoreCase("")){
            tv_postalCode.setText(mStrDistrictDesc);
        }else{
            tv_postalCode.setVisibility(View.GONE);
        }


        if(!mStrLandmark.equalsIgnoreCase("") && !mStrCityDesc.equalsIgnoreCase("")){
            tv_land_mark.setText(mStrLandmark + "," + mStrCityDesc);
        }else if(!mStrLandmark.equalsIgnoreCase("") && mStrCityDesc.equalsIgnoreCase("")){
            tv_land_mark.setText(mStrLandmark);
        }else if(mStrLandmark.equalsIgnoreCase("") && !mStrDistrictDesc.equalsIgnoreCase("")){
            tv_land_mark.setText(mStrCityDesc);
        }else{
            tv_land_mark.setVisibility(View.GONE);
        }

        tv_OwnerName.setText(mStrOwnerName);
        tvEmilView.setText(mStrEmailID);
    }
    /*
       * TODO This method get retailer address details.
       */
    private void onRetailerDetails() {
        try {
            try {
                if(mStrCustomerID != null) {
                    String retDetgry = Constants.Customers + "?$filter = " + Constants.CustomerNo + " eq '" + mStrCustomerID + "'";
                    ODataEntity retilerEntity = OfflineManager.getRetDetails(retDetgry);
                    if (retilerEntity != null) {
                        oDataProperties = retilerEntity.getProperties();
                        oDataProperty = oDataProperties.get(Constants.EmailID);
                        mStrEmailID = (String) oDataProperty.getValue();

                        oDataProperty = oDataProperties.get(Constants.MobileNo);
                        mContactNum = (String) oDataProperty.getValue();
                        oDataProperty = oDataProperties.get(Constants.Address1);
                        mStrFirstAddress = (String) oDataProperty.getValue();
                        oDataProperty = oDataProperties.get(Constants.Address2);
                        mStrAddressTwo = (String) oDataProperty.getValue();
                        oDataProperty = oDataProperties.get(Constants.Address3);
                        mStrAddressThree = (String) oDataProperty.getValue();
                        oDataProperty = oDataProperties.get(Constants.Address4);
                        mStrAddressFour = (String) oDataProperty.getValue();
                        oDataProperty = oDataProperties.get(Constants.PostalCode);
                        mStrPostalCode = oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";
                        oDataProperty = oDataProperties.get(Constants.City);
                        mStrCityDesc = oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";
     /*
                oDataProperty = oDataProperties.get(Constants.Group3Desc);
                mClassification = (String) oDataProperty.getValue();
                oDataProperty = oDataProperties.get(Constants.WeeklyOffDesc);
                mWeeklyOff = (String) oDataProperty.getValue();
                oDataProperty = oDataProperties.get(Constants.DOB);
                mDOB = UtilConstants.convertCalenderToStringFormat((GregorianCalendar) oDataProperty.getValue());
                oDataProperty = oDataProperties.get(Constants.Anniversary);
                mAnniversary = UtilConstants.convertCalenderToStringFormat((GregorianCalendar) oDataProperty.getValue());
                oDataProperty = oDataProperties.get(Constants.CPTypeID);
                mStrCPTypeId = (String) oDataProperty.getValue();
                oDataProperty = oDataProperties.get(Constants.CPTypeDesc);
                selCPTypeDesc = (String) oDataProperty.getValue();
                oDataProperty = oDataProperties.get(Constants.CPGUID);
                mCpGuid = (ODataGuid)oDataProperty.getValue();
                oDataProperty = oDataProperties.get(Constants.Address1);
                mStrFirstAddress = (String)oDataProperty.getValue();
                oDataProperty = oDataProperties.get(Constants.Address2);
                mStrAddressTwo = (String)oDataProperty.getValue();
                oDataProperty = oDataProperties.get(Constants.Address3);
                mStrAddressThree = (String)oDataProperty.getValue();
                oDataProperty = oDataProperties.get(Constants.Address4);
                mStrAddressFour = (String)oDataProperty.getValue();
                oDataProperty = oDataProperties.get(Constants.Landmark);
                mStrLandmark = oDataProperty.getValue() !=null?(String)oDataProperty.getValue():"";
                oDataProperty = oDataProperties.get(Constants.PostalCode);
                mStrPostalCode = oDataProperty.getValue() !=null?(String)oDataProperty.getValue():"";
                oDataProperty = oDataProperties.get(Constants.DistrictDesc);
                mStrDistrictDesc = oDataProperty.getValue() !=null?(String)oDataProperty.getValue():"";
                oDataProperty = oDataProperties.get(Constants.CityDesc);
                mStrCityDesc = oDataProperty.getValue() !=null?(String)oDataProperty.getValue():"";
                oDataProperty = oDataProperties.get(Constants.OwnerName);
                mStrOwnerName = oDataProperty.getValue() !=null?(String)oDataProperty.getValue():"";*/

                    }
                }

            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }


    }


    private void onProspectiveCustomerDetails() {
        try {
            if(mStrCustomerID != null){
                String retDetgry = Constants.ChannelPartners+"?$filter = "+Constants.CPNo+" eq '"+mStrCustomerID+"'";
                ODataEntity retilerEntity = OfflineManager.getRetDetails(retDetgry);
                oDataProperties = retilerEntity.getProperties();
                oDataProperty = oDataProperties.get(Constants.EmailID);
                mStrEmailID = (String) oDataProperty.getValue();

                oDataProperty = oDataProperties.get(Constants.MobileNo);
                mContactNum = (String) oDataProperty.getValue();
                oDataProperty = oDataProperties.get(Constants.Address1);
                mStrFirstAddress = (String)oDataProperty.getValue();
                oDataProperty = oDataProperties.get(Constants.Address2);
                mStrAddressTwo = (String)oDataProperty.getValue();
                oDataProperty = oDataProperties.get(Constants.Address3);
                mStrAddressThree = (String)oDataProperty.getValue();
                oDataProperty = oDataProperties.get(Constants.Address4);
                mStrAddressFour = (String)oDataProperty.getValue();
                oDataProperty = oDataProperties.get(Constants.PostalCode);
                mStrPostalCode = oDataProperty.getValue() !=null?(String)oDataProperty.getValue():"";
                oDataProperty = oDataProperties.get(Constants.CityDesc);
                mStrCityDesc = oDataProperty.getValue() !=null?(String)oDataProperty.getValue():"";
 /*
            oDataProperty = oDataProperties.get(Constants.Group3Desc);
            mClassification = (String) oDataProperty.getValue();
            oDataProperty = oDataProperties.get(Constants.WeeklyOffDesc);
            mWeeklyOff = (String) oDataProperty.getValue();
            oDataProperty = oDataProperties.get(Constants.DOB);
            mDOB = UtilConstants.convertCalenderToStringFormat((GregorianCalendar) oDataProperty.getValue());
            oDataProperty = oDataProperties.get(Constants.Anniversary);
            mAnniversary = UtilConstants.convertCalenderToStringFormat((GregorianCalendar) oDataProperty.getValue());
            oDataProperty = oDataProperties.get(Constants.CPTypeID);
            mStrCPTypeId = (String) oDataProperty.getValue();
            oDataProperty = oDataProperties.get(Constants.CPTypeDesc);
            selCPTypeDesc = (String) oDataProperty.getValue();
            oDataProperty = oDataProperties.get(Constants.CPGUID);
            mCpGuid = (ODataGuid)oDataProperty.getValue();
            oDataProperty = oDataProperties.get(Constants.Address1);
            mStrFirstAddress = (String)oDataProperty.getValue();
            oDataProperty = oDataProperties.get(Constants.Address2);
            mStrAddressTwo = (String)oDataProperty.getValue();
            oDataProperty = oDataProperties.get(Constants.Address3);
            mStrAddressThree = (String)oDataProperty.getValue();
            oDataProperty = oDataProperties.get(Constants.Address4);
            mStrAddressFour = (String)oDataProperty.getValue();
            oDataProperty = oDataProperties.get(Constants.Landmark);
            mStrLandmark = oDataProperty.getValue() !=null?(String)oDataProperty.getValue():"";
            oDataProperty = oDataProperties.get(Constants.PostalCode);
            mStrPostalCode = oDataProperty.getValue() !=null?(String)oDataProperty.getValue():"";
            oDataProperty = oDataProperties.get(Constants.DistrictDesc);
            mStrDistrictDesc = oDataProperty.getValue() !=null?(String)oDataProperty.getValue():"";
            oDataProperty = oDataProperties.get(Constants.CityDesc);
            mStrCityDesc = oDataProperty.getValue() !=null?(String)oDataProperty.getValue():"";
            oDataProperty = oDataProperties.get(Constants.OwnerName);
            mStrOwnerName = oDataProperty.getValue() !=null?(String)oDataProperty.getValue():"";*/

            }

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }


    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call:
                onCall();
                break;
            case R.id.sms:
                onSMS();
                break;
            case R.id.mail:
                onMail();
                break;
            case R.id.tv_email_id:
                onMail();
                break;
            case R.id.whats_app:
                whatsAppCall();
                break;
            case R.id.appointment:
                appointment();
                break;


        }
    }

    private void appointment()
    {
        Intent intent  = new Intent(getActivity(), AppointmentCreate.class);
        intent.putExtra(Constants.RetailerName,mStrRetName);
        intent.putExtra(Constants.CPUID,mStrCustomerID);
        intent.putExtra(Constants.CPGUID,mStrCPGUID);
        startActivity(intent);
    }

    /*
   * TODO This method make a whats up call.
   */
    private void whatsAppCall() {
      /*  try {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setComponent(new ComponentName(Constants.whatsapp_packagename, Constants.whatsapp_conv_packagename));
            sendIntent.putExtra(Constants.jid, PhoneNumberUtils.stripSeparators(mContactNum) + Constants.whatsapp_domainname);
            startActivity(sendIntent);

        } catch (Exception e) {
            Constants.customAlertMessage(getActivity(),getString(R.string.alert_whatsapp_not_installed));
        }*/

        try {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setComponent(new ComponentName(Constants.whatsapp_packagename, Constants.whatsapp_conv_packagename));
            sendIntent.putExtra(Constants.jid, Constants.getCountryCode(mContext)+mContactNum + Constants.whatsapp_domainname);
            startActivity(sendIntent);

        } catch (Exception e) {
            UtilConstants.showAlert(getString(R.string.alert_whatsapp_not_installed), getActivity());
        }
    }
    /*
    * TODO This method make a sms.
    */
    private void onSMS() {
        if (!mContactNum.equalsIgnoreCase("")) {
            Uri smsUri = Uri.parse(Constants.sms_txt + mContactNum);
            Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
            startActivity(intent);
        }else{
            Constants.customAlertMessage(getActivity(),getString(R.string.alert_mobile_no_maintend));

        }
    }

    /*
     * TODO This method make a email.
     */
    private void onMail() {
        try {
            if (!mStrEmailID.equalsIgnoreCase("")) {
                Intent email = new Intent(Intent.ACTION_SEND);
                String[] emailList = {mStrEmailID};
                email.putExtra(Intent.EXTRA_EMAIL, emailList);
                email.setType(Constants.plain_text);
                startActivity(Intent
                        .createChooser(email, Constants.send_email));
            }else{
                Constants.customAlertMessage(getActivity(),getString(R.string.alert_mail_id_not_maintend));
            }

        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    /*
     * TODO This method make a call.
     */
    private void onCall() {
        try {
            if (!mContactNum.equalsIgnoreCase("")) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse(Constants.tel_txt + (mContactNum)));
                startActivity(dialIntent);
            }else{
                Constants.customAlertMessage(getActivity(),getString(R.string.alert_mobile_no_maintend));
            }
        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }
}
