package com.rspl.sf.msfa.mtp;

import java.io.Serializable;

/**
 * Created by e10769 on 19-02-2018.
 */

public class MTPRoutePlanBean implements Serializable {
    private String CustomerNo = "";
    private String CustomerName = "";
    private String VisitDate = "";
    private String activityDec = "";
    private String activityId = "";
    private String remarks = "";
    private String date = "";
    private String month = "";
    private String day = "";
    private String address = "";
    private String PostalCode = "";
    private String Mobile1 = "";
    private boolean isItemVisible = false;
    private String RouteSchGUID = "";
    private String RouteSchPlanGUID = "";
    private String SalesPersonID = "";
    private String SalesPersonName = "";
    private String ValidTo = "";
    private String ValidFrom = "";
    private String ApprovalStatus = "";
    private String ApprovalStatusDs = "";
    private String SalesDistrict = "";
    private String SalesDistrictDesc = "";
    private String deviceNo = "";
    private String city="";

    public String getRouteSchGUID() {
        return RouteSchGUID;
    }

    public void setRouteSchGUID(String routeSchGUID) {
        RouteSchGUID = routeSchGUID;
    }

    public String getRouteSchPlanGUID() {
        return RouteSchPlanGUID;
    }

    public void setRouteSchPlanGUID(String routeSchPlanGUID) {
        RouteSchPlanGUID = routeSchPlanGUID;
    }

    public String getSalesPersonID() {
        return SalesPersonID;
    }

    public void setSalesPersonID(String salesPersonID) {
        SalesPersonID = salesPersonID;
    }

    public String getSalesPersonName() {
        return SalesPersonName;
    }

    public void setSalesPersonName(String salesPersonName) {
        SalesPersonName = salesPersonName;
    }

    public String getValidTo() {
        return ValidTo;
    }

    public void setValidTo(String validTo) {
        ValidTo = validTo;
    }

    public String getValidFrom() {
        return ValidFrom;
    }

    public void setValidFrom(String validFrom) {
        ValidFrom = validFrom;
    }

    public String getApprovalStatus() {
        return ApprovalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        ApprovalStatus = approvalStatus;
    }

    public String getApprovalStatusDs() {
        return ApprovalStatusDs;
    }

    public void setApprovalStatusDs(String approvalStatusDs) {
        ApprovalStatusDs = approvalStatusDs;
    }

    public String getCustomerNo() {
        return CustomerNo;
    }

    public void setCustomerNo(String customerNo) {
        CustomerNo = customerNo;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getVisitDate() {
        return VisitDate;
    }

    public void setVisitDate(String visitDate) {
        VisitDate = visitDate;
    }

    public String getActivityDec() {
        return activityDec;
    }

    public void setActivityDec(String activityDec) {
        this.activityDec = activityDec;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public boolean isItemVisible() {
        return isItemVisible;
    }

    public void setItemVisible(boolean itemVisible) {
        isItemVisible = itemVisible;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getSalesDistrict() {
        return SalesDistrict;
    }

    public void setSalesDistrict(String salesDistrict) {
        SalesDistrict = salesDistrict;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getSalesDistrictDesc() {
        return SalesDistrictDesc;
    }

    public void setSalesDistrictDesc(String salesDistrictDesc) {
        SalesDistrictDesc = salesDistrictDesc;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
