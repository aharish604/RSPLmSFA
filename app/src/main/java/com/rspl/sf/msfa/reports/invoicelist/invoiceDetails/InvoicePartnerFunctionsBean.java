package com.rspl.sf.msfa.reports.invoicelist.invoiceDetails;

import java.io.Serializable;

/**
 * Created by e10849 on 12-10-2017.
 */

public class InvoicePartnerFunctionsBean  implements Serializable {


    private String PartnerFunctionID = "";
    private String PartnerFunctionDesc = "";
    private String CustomerName = "";
    private String TransportationZoneDesc = "",VendorNo,PersonnelNo,CustomerNo;

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

    private String Address1="";
    private String Address2="";
    private String Address3="";
    private String Address4="";
    private String District="";
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

    public String getPartnerFunctionID() {
        return PartnerFunctionID;
    }

    public void setPartnerFunctionID(String partnerFunctionID) {
        PartnerFunctionID = partnerFunctionID;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getTransportationZoneDesc() {
        return TransportationZoneDesc;
    }

    public void setTransportationZoneDesc(String transportationZoneDesc) {
        TransportationZoneDesc = transportationZoneDesc;
    }

    public String getVendorNo() {
        return VendorNo;
    }

    public void setVendorNo(String vendorNo) {
        VendorNo = vendorNo;
    }

    public String getPersonnelNo() {
        return PersonnelNo;
    }

    public void setPersonnelNo(String personnelNo) {
        PersonnelNo = personnelNo;
    }

    public String getCustomerNo() {
        return CustomerNo;
    }

    public void setCustomerNo(String customerNo) {
        CustomerNo = customerNo;
    }
    public String getPartnerFunctionDesc() {
        return PartnerFunctionDesc;
    }

    public void setPartnerFunctionDesc(String partnerFunctionDesc) {
        PartnerFunctionDesc = partnerFunctionDesc;
    }
}
