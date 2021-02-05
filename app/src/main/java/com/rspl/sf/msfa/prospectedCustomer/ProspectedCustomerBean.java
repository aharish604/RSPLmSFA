package com.rspl.sf.msfa.prospectedCustomer;

import java.io.Serializable;

/**
 * Created by ccb on 26-09-2017.
 */

public class ProspectedCustomerBean implements Serializable{

    public ProspectedCustomerBean()
    {

    }
    public ProspectedCustomerBean(String customerno){
        this.customerNo=customerno;

    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCounterType() {
        return counterType;
    }

    public void setCounterType(String counterType) {
        this.counterType = counterType;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getMobNo() {
        return mobNo;
    }

    public void setMobNo(String mobNo) {
        this.mobNo = mobNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTaluka() {
        return taluka;
    }

    public void setTaluka(String taluka) {
        this.taluka = taluka;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getTotalTrade() {
        return totalTrade;
    }

    public void setTotalTrade(String totalTrade) {
        this.totalTrade = totalTrade;
    }

    public String getTotalNonTrade() {
        return totalNonTrade;
    }

    public void setTotalNonTrade(String totalNonTrade) {
        this.totalNonTrade = totalNonTrade;
    }

    public String getPotentialBg() {
        return potentialBg;
    }

    public void setPotentialBg(String potentialBg) {
        this.potentialBg = potentialBg;
    }

    public String getPopDitributed() {
        return popDitributed;
    }

    public void setPopDitributed(String popDitributed) {
        this.popDitributed = popDitributed;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isAddressEnabled() {
        return isAddressEnabled;
    }

    public void setAddressEnabled(boolean addressEnabled) {
        isAddressEnabled = addressEnabled;
    }



    private boolean isAddressEnabled;
    String custName="";
    String counterType="";
    String contactPerson="";
    String mobNo="";
    String address="";
    String city="";
    String district="";
    String taluka="";
    String pincode="";
    String block="";
    String totalTrade="";
    String totalNonTrade="";
    String potentialBg="";
    private String postalCode = "";
    private String mobile1;
    private String Currency="";

    public String getAddresss2() {
        return addresss2;
    }

    public void setAddresss2(String addresss2) {
        this.addresss2 = addresss2;
    }

    String popDitributed="";
    String remarks="";
    String addresss2="";

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    String address3="";
    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    String customerNo="";

    public String getCpNo() {
        return cpNo;
    }

    public void setCpNo(String cpNo) {
        this.cpNo = cpNo;
    }

    String cpNo = "";

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    public String getMobile1() {
        return mobile1;
    }

    public void setMobile1(String mobile1) {
        this.mobile1 = mobile1;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

}
