package com.rspl.sf.msfa.socreate;

import java.io.Serializable;

/**
 * Created by e10769 on 22-05-2017.
 */

public class DefaultValueBean implements Serializable {
    private String SalesArea = "";
    private String SalesAreaDesc = "";
    private String SalesDistrictID = "";
    private String SalesDistrictDesc = "";
    private String SalesOfficeID = "";
    private String SalesOfficeDesc = "";
    private String SalesGroupID = "";
    private String SalesGroupDesc = "";
    private String ShippingConditionID = "";
    private String ShippingConditionDesc = "";
    private String DeliveringPlantID = "";
    private String DeliveringPlantDesc = "";
    private String TransportationZoneID = "";
    private String TransportationZoneDesc = "";
    private String Incoterms1ID = "";
    private String Incoterms1Desc = "";
    private String Incoterms2 = "";
    private String PaymentTermID = "";
    private String PaymentTermDesc = "";
    private String CreditControlAreaID = "";
    private String CreditControlAreaDesc = "";
    private String CustomerGrpID = "";
    private String UnloadingPoint = "";
    private String displayDropDown="";

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    private String division="";
    private String distChannelID="";

    public String getDistChannelID() {
        return distChannelID;
    }

    public void setDistChannelID(String distChannelID) {
        this.distChannelID = distChannelID;
    }

    public String getSalesOrgID() {
        return salesOrgID;
    }

    public void setSalesOrgID(String salesOrgID) {
        this.salesOrgID = salesOrgID;
    }

    private String salesOrgID="";

    public String getSalesArea() {
        return SalesArea;
    }

    public void setSalesArea(String salesArea) {
        SalesArea = salesArea;
    }

    public String getSalesAreaDesc() {
        return SalesAreaDesc;
    }

    public void setSalesAreaDesc(String salesAreaDesc) {
        SalesAreaDesc = salesAreaDesc;
    }

    public String getSalesDistrictID() {
        return SalesDistrictID;
    }

    public void setSalesDistrictID(String salesDistrictID) {
        SalesDistrictID = salesDistrictID;
    }

    public String getSalesDistrictDesc() {
        return SalesDistrictDesc;
    }

    public void setSalesDistrictDesc(String salesDistrictDesc) {
        SalesDistrictDesc = salesDistrictDesc;
    }

    public String getSalesOfficeID() {
        return SalesOfficeID;
    }

    public void setSalesOfficeID(String salesOfficeID) {
        SalesOfficeID = salesOfficeID;
    }

    public String getSalesOfficeDesc() {
        return SalesOfficeDesc;
    }

    public void setSalesOfficeDesc(String salesOfficeDesc) {
        SalesOfficeDesc = salesOfficeDesc;
    }

    public String getSalesGroupID() {
        return SalesGroupID;
    }

    public void setSalesGroupID(String salesGroupID) {
        SalesGroupID = salesGroupID;
    }

    public String getSalesGroupDesc() {
        return SalesGroupDesc;
    }

    public void setSalesGroupDesc(String salesGroupDesc) {
        SalesGroupDesc = salesGroupDesc;
    }

    public String getShippingConditionID() {
        return ShippingConditionID;
    }

    public void setShippingConditionID(String shippingConditionID) {
        ShippingConditionID = shippingConditionID;
    }

    public String getShippingConditionDesc() {
        return ShippingConditionDesc;
    }

    public void setShippingConditionDesc(String shippingConditionDesc) {
        ShippingConditionDesc = shippingConditionDesc;
    }

    public String getDeliveringPlantID() {
        return DeliveringPlantID;
    }

    public void setDeliveringPlantID(String deliveringPlantID) {
        DeliveringPlantID = deliveringPlantID;
    }

    public String getDeliveringPlantDesc() {
        return DeliveringPlantDesc;
    }

    public void setDeliveringPlantDesc(String deliveringPlantDesc) {
        DeliveringPlantDesc = deliveringPlantDesc;
    }

    public String getTransportationZoneID() {
        return TransportationZoneID;
    }

    public void setTransportationZoneID(String transportationZoneID) {
        TransportationZoneID = transportationZoneID;
    }

    public String getTransportationZoneDesc() {
        return TransportationZoneDesc;
    }

    public void setTransportationZoneDesc(String transportationZoneDesc) {
        TransportationZoneDesc = transportationZoneDesc;
    }

    public String getIncoterms1ID() {
        return Incoterms1ID;
    }

    public void setIncoterms1ID(String incoterms1ID) {
        Incoterms1ID = incoterms1ID;
    }

    public String getIncoterms1Desc() {
        return Incoterms1Desc;
    }

    public void setIncoterms1Desc(String incoterms1Desc) {
        Incoterms1Desc = incoterms1Desc;
    }

    public String getIncoterms2() {
        return Incoterms2;
    }

    public void setIncoterms2(String incoterms2) {
        Incoterms2 = incoterms2;
    }

    public String getPaymentTermID() {
        return PaymentTermID;
    }

    public void setPaymentTermID(String paymentTermID) {
        PaymentTermID = paymentTermID;
    }

    public String getPaymentTermDesc() {
        return PaymentTermDesc;
    }

    public void setPaymentTermDesc(String paymentTermDesc) {
        PaymentTermDesc = paymentTermDesc;
    }

    public String getCreditControlAreaID() {
        return CreditControlAreaID;
    }

    public void setCreditControlAreaID(String creditControlAreaID) {
        CreditControlAreaID = creditControlAreaID;
    }

    public String getCreditControlAreaDesc() {
        return CreditControlAreaDesc;
    }

    public void setCreditControlAreaDesc(String creditControlAreaDesc) {
        CreditControlAreaDesc = creditControlAreaDesc;
    }

    public String getCustomerGrpID() {
        return CustomerGrpID;
    }

    public void setCustomerGrpID(String customerGrpID) {
        CustomerGrpID = customerGrpID;
    }

    public String getUnloadingPoint() {
        return UnloadingPoint;
    }

    public void setUnloadingPoint(String unloadingPoint) {
        UnloadingPoint = unloadingPoint;
    }

    @Override
    public String toString() {
        return displayDropDown.toString();
    }

    public String getDisplayDropDown() {
        return displayDropDown;
    }

    public void setDisplayDropDown(String displayDropDown) {
        this.displayDropDown = displayDropDown;
    }
}
