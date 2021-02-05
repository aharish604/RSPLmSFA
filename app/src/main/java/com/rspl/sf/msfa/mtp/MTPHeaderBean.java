package com.rspl.sf.msfa.mtp;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by e10769 on 19-02-2018.
 */

public class MTPHeaderBean implements Serializable {
    private String CustomerNo = "";
    private String CustomerName = "";
    private String activityDec = "";
    private String activityID = "";
    private String remarks = "";
    private String date = "";
    private String fullDate = "";
    private String day = "";
    private boolean isTitle = false;
    private String weekTitle = "";
    private int week = 0;
    private String RouteSchGUID = "";
    private String salesPersonGuid = "";
    private String deviceNo = "";
    private String ApprovalStatus = "";
    private String ApprovalStatusDs = "";
    private String RoutId = "";
    private String CreatedBy = "";
    private String CreatedOn = "";
    private String CreatedAt = "";
    private String isUpdate = "";
    private String SalesDistrict = "";
    private String SalesDistrictDisc = "";
    private String testRun="";
    private boolean isFromDataValt=false;
    private String monthForApproval="";

    public String getMonthForApproval() {
        return monthForApproval;
    }

    public void setMonthForApproval(String monthForApproval) {
        this.monthForApproval = monthForApproval;
    }

    public String getSalesPersonGuid() {
        return salesPersonGuid;
    }

    public void setSalesPersonGuid(String salesPersonGuid) {
        this.salesPersonGuid = salesPersonGuid;
    }

    private ArrayList<MTPRoutePlanBean> MTPRoutePlanBeanArrayList = new ArrayList<>();

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

    public boolean isTitle() {
        return isTitle;
    }

    public void setTitle(boolean title) {
        isTitle = title;
    }

    public String getWeekTitle() {
        return weekTitle;
    }

    public void setWeekTitle(String weekTitle) {
        this.weekTitle = weekTitle;
    }

    public ArrayList<MTPRoutePlanBean> getMTPRoutePlanBeanArrayList() {
        return MTPRoutePlanBeanArrayList;
    }

    public void setMTPRoutePlanBeanArrayList(ArrayList<MTPRoutePlanBean> MTPRoutePlanBeanArrayList) {
        this.MTPRoutePlanBeanArrayList = MTPRoutePlanBeanArrayList;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public String getFullDate() {
        return fullDate;
    }

    public void setFullDate(String fullDate) {
        this.fullDate = fullDate;
    }

    public String getActivityID() {
        return activityID;
    }

    public void setActivityID(String activityID) {
        this.activityID = activityID;
    }

    public String getRouteSchGUID() {
        return RouteSchGUID;
    }

    public void setRouteSchGUID(String routeSchGUID) {
        RouteSchGUID = routeSchGUID;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
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

    public String getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(String isUpdate) {
        this.isUpdate = isUpdate;
    }

    public String getSalesDistrict() {
        return SalesDistrict;
    }

    public void setSalesDistrict(String salesDistrict) {
        SalesDistrict = salesDistrict;
    }

    public String getSalesDistrictDisc() {
        return SalesDistrictDisc;
    }

    public void setSalesDistrictDisc(String salesDistrictDisc) {
        SalesDistrictDisc = salesDistrictDisc;
    }

    public String getTestRun() {
        return testRun;
    }

    public void setTestRun(String testRun) {
        this.testRun = testRun;
    }

    public boolean isFromDataValt() {
        return isFromDataValt;
    }

    public void setFromDataValt(boolean fromDataValt) {
        isFromDataValt = fromDataValt;
    }

    public String getRoutId() {
        return RoutId;
    }

    public void setRoutId(String routId) {
        RoutId = routId;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public String getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(String createdOn) {
        CreatedOn = createdOn;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }
}
