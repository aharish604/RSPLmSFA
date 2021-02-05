package com.rspl.sf.msfa.collectionPlan;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by e10860 on 2/16/2018.
 *
 */

public class WeekHeaderList implements Serializable {

    private String weekTitle = "";

    public String getDeviceNo() {
        return DeviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        DeviceNo = deviceNo;
    }

    private String DeviceNo = "";
    private String date = "";
    private String desc = "";
    private String name = "";
    private String CustNo = "";
    private String remarks = "";
    private String fullDate = "";
    private boolean isSecondSat = false;
    private String isUpdate = "";

    public String getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(String createdOn) {
        CreatedOn = createdOn;
    }

    public String getChangedBy() {
        return ChangedBy;
    }

    public void setChangedBy(String changedBy) {
        ChangedBy = changedBy;
    }

    private String CreatedOn = "";

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    private String CreatedBy = "";
    private String ChangedBy = "";

    public boolean isSunday() {
        return isSunday;
    }

    public void setSunday(boolean sunday) {
        isSunday = sunday;
    }

    private boolean isSunday = false;

    public String getCollectionPlanGUID() {
        return CollectionPlanGUID;
    }

    public void setCollectionPlanGUID(String collectionPlanGUID) {
        CollectionPlanGUID = collectionPlanGUID;
    }

    private String CollectionPlanGUID = "";

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    private int week =0;
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    private String currency = "";
    private boolean isTitle = false;
    private String day = "";

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    private String totalAmount = "";

    public String getTotalAchivedAmount() {
        return totalAchivedAmount;
    }

    public void setTotalAchivedAmount(String totalAchivedAmount) {
        this.totalAchivedAmount = totalAchivedAmount;
    }

    private String totalAchivedAmount = "";
    private ArrayList<WeekDetailsList> weekDetailsLists = new ArrayList<>();

    public String getWeekTitle() {
        return weekTitle;
    }

    public void setWeekTitle(String weekTitle) {
        this.weekTitle = weekTitle;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isTitle() {
        return isTitle;
    }

    public void setTitle(boolean title) {
        isTitle = title;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public ArrayList<WeekDetailsList> getWeekDetailsLists() {
        return weekDetailsLists;
    }

    public void setWeekDetailsLists(ArrayList<WeekDetailsList> weekDetailsLists) {
        this.weekDetailsLists = weekDetailsLists;
    }
    public String getCustNo() {
        return CustNo;
    }

    public void setCustNo(String custNo) {
        CustNo = custNo;
    }


    public String getFullDate() {
        return fullDate;
    }

    public void setFullDate(String fullDate) {
        this.fullDate = fullDate;
    }

    public boolean isSecondSat() {
        return isSecondSat;
    }

    public void setSecondSat(boolean secondSat) {
        isSecondSat = secondSat;
    }

    public String getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(String isUpdate) {
        this.isUpdate = isUpdate;
    }
}
