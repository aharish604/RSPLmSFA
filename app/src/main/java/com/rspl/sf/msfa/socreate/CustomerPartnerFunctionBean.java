package com.rspl.sf.msfa.socreate;

import java.io.Serializable;

/**
 * Created by e10769 on 29-06-2017.
 */

public class CustomerPartnerFunctionBean implements Serializable{
    private String PartnerVendorNo="";
    private String PartnerVendorName="";
    private String PartnerCustomerNo="";
    private String PartnerCustomerName="";
    private String PartnerFunctionID="";
    private String PartnerFunctionDesc="";
    private String displayName = "";
    private String Address1="";
    private String Address2="";
    private String Address3="";
    private String Address4="";
    private String District="";

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    private String City="";

    public String getCompleteAddress() {
        return CompleteAddress;
    }

    public void setCompleteAddress(String completeAddress) {
        CompleteAddress = completeAddress;
    }

    private String CompleteAddress
            ="";
    private String CityID="";
    private String RegionID="";
    private String RegionDesc="";
    private String CountryID="";
    private String CountryDesc="";
    private String PostalCode="";
    private String Mobile1="";
    private String Mobile2="";
    private String Landline1="";
    private String EmailID="";
    private String PersonnelNo="";
    private String PersonnelName="";


    public String getPartnerVendorNo() {
        return PartnerVendorNo;
    }

    public void setPartnerVendorNo(String partnerVendorNo) {
        PartnerVendorNo = partnerVendorNo;
    }

    public String getPartnerVendorName() {
        return PartnerVendorName;
    }

    public void setPartnerVendorName(String partnerVendorName) {
        PartnerVendorName = partnerVendorName;
    }

    public String getPartnerCustomerNo() {
        return PartnerCustomerNo;
    }

    public void setPartnerCustomerNo(String partnerCustomerNo) {
        PartnerCustomerNo = partnerCustomerNo;
    }

    public String getPartnerCustomerName() {
        return PartnerCustomerName;
    }

    public void setPartnerCustomerName(String partnerCustomerName) {
        PartnerCustomerName = partnerCustomerName;
    }

    public String getPartnerFunctionID() {
        return PartnerFunctionID;
    }

    public void setPartnerFunctionID(String partnerFunctionID) {
        PartnerFunctionID = partnerFunctionID;
    }

    @Override
    public String toString() {
        return displayName.toString();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAddress1() {
        return Address1;
    }

    public void setAddress1(String address1) {
        Address1 = address1;
    }

    public String getAddress2() {
        return Address2;
    }

    public void setAddress2(String address2) {
        Address2 = address2;
    }

    public String getAddress3() {
        return Address3;
    }

    public void setAddress3(String address3) {
        Address3 = address3;
    }

    public String getAddress4() {
        return Address4;
    }

    public void setAddress4(String address4) {
        Address4 = address4;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getCityID() {
        return CityID;
    }

    public void setCityID(String cityID) {
        CityID = cityID;
    }

    public String getRegionID() {
        return RegionID;
    }

    public void setRegionID(String regionID) {
        RegionID = regionID;
    }

    public String getRegionDesc() {
        return RegionDesc;
    }

    public void setRegionDesc(String regionDesc) {
        RegionDesc = regionDesc;
    }

    public String getCountryID() {
        return CountryID;
    }

    public void setCountryID(String countryID) {
        CountryID = countryID;
    }

    public String getCountryDesc() {
        return CountryDesc;
    }

    public void setCountryDesc(String countryDesc) {
        CountryDesc = countryDesc;
    }

    public String getPostalCode() {
        return PostalCode;
    }

    public void setPostalCode(String postalCode) {
        PostalCode = postalCode;
    }

    public String getMobile1() {
        return Mobile1;
    }

    public void setMobile1(String mobile1) {
        Mobile1 = mobile1;
    }

    public String getMobile2() {
        return Mobile2;
    }

    public void setMobile2(String mobile2) {
        Mobile2 = mobile2;
    }

    public String getLandline1() {
        return Landline1;
    }

    public void setLandline1(String landline1) {
        Landline1 = landline1;
    }

    public String getEmailID() {
        return EmailID;
    }

    public void setEmailID(String emailID) {
        EmailID = emailID;
    }

    public String getPersonnelNo() {
        return PersonnelNo;
    }

    public void setPersonnelNo(String personnelNo) {
        PersonnelNo = personnelNo;
    }

    public String getPersonnelName() {
        return PersonnelName;
    }

    public void setPersonnelName(String personnelName) {
        PersonnelName = personnelName;
    }

    public String getPartnerFunctionDesc() {
        return PartnerFunctionDesc;
    }

    public void setPartnerFunctionDesc(String partnerFunctionDesc) {
        PartnerFunctionDesc = partnerFunctionDesc;
    }
}
