package com.rspl.sf.msfa.expense;

import java.util.ArrayList;

/**
 * Created by e10742 on 2/17/2017.
 */

public class ExpenseBeanJK {

    String ExpenseGuid = "";
    String ExpenseItemGuid = "";
    String ClaimNo = "";
    String RaisedDate = "";
    String ExpanseType = "";
    String ExpanseTypeDesc = "";
    String Status = "";
    String StatusDesc = "";
    String BillNo = "";
    String Amount = "";
    String ApprovedBy = "";
    String ApprovedOn = "";
    String Remarks = "";
    String ModeOfTransportation = "";
    String FromPlace = "";
    String ToPlace = "";
    String Currency = "";

    String Beat = "";
    String Location = "";
    String Distance = "";
    String UOM = "";

    public String getExpenseItemGuid() {
        return ExpenseItemGuid;
    }

    public void setExpenseItemGuid(String expenseItemGuid) {
        ExpenseItemGuid = expenseItemGuid;
    }

    public ArrayList<MediaLink> getMedialink() {
        return medialink;
    }

    public void setMedialink(ArrayList<MediaLink> medialink) {
        this.medialink = medialink;
    }

    ArrayList<MediaLink> medialink;

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public String getBeat() {
        return Beat;
    }

    public void setBeat(String beat) {
        Beat = beat;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getStatusDesc() {
        return StatusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        StatusDesc = statusDesc;
    }

    public String getExpanseTypeDesc() {
        return ExpanseTypeDesc;
    }

    public void setExpanseTypeDesc(String expanseTypeDesc) {
        ExpanseTypeDesc = expanseTypeDesc;
    }

    public String getExpenseGuid() {
        return ExpenseGuid;
    }

    public void setExpenseGuid(String expenseGuid) {
        ExpenseGuid = expenseGuid;
    }

    public ExpenseBeanJK() {
    }

    public ExpenseBeanJK(String claimNo, String raisedDate, String expanseType, String status, String billNo, String amount) {
        ClaimNo = claimNo;
        RaisedDate = raisedDate;
        ExpanseType = expanseType;
        Status = status;
        BillNo = billNo;
        Amount = amount;
    }

    public String getClaimNo() {
        return ClaimNo;
    }

    public void setClaimNo(String claimNo) {
        ClaimNo = claimNo;
    }

    public String getRaisedDate() {
        return RaisedDate;
    }

    public void setRaisedDate(String raisedDate) {
        RaisedDate = raisedDate;
    }

    public String getExpanseType() {
        return ExpanseType;
    }

    public void setExpanseType(String expanseType) {
        ExpanseType = expanseType;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getBillNo() {
        return BillNo;
    }

    public void setBillNo(String billNo) {
        BillNo = billNo;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getApprovedBy() {
        return ApprovedBy;
    }

    public void setApprovedBy(String approvedBy) {
        ApprovedBy = approvedBy;
    }

    public String getApprovedOn() {
        return ApprovedOn;
    }

    public void setApprovedOn(String approvedOn) {
        ApprovedOn = approvedOn;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getModeOfTransportation() {
        return ModeOfTransportation;
    }

    public void setModeOfTransportation(String modeOfTransportation) {
        ModeOfTransportation = modeOfTransportation;
    }

    public String getFromPlace() {
        return FromPlace;
    }

    public void setFromPlace(String fromPlace) {
        FromPlace = fromPlace;
    }

    public String getToPlace() {
        return ToPlace;
    }

    public void setToPlace(String toPlace) {
        ToPlace = toPlace;
    }
}
