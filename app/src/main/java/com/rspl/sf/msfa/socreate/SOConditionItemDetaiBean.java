package com.rspl.sf.msfa.socreate;

import java.io.Serializable;

/**
 * Created by e10769 on 12-05-2017.
 */

public class SOConditionItemDetaiBean implements Serializable {
    private String Name = "";
    private String MaterialNo = "";
    private String MaterialDesc = "";
    private String ConditionCatID = "";
    private String ConditionCounter = "";
    private String ConditionCatDesc = "";

    public String getConditionCounter() {
        return ConditionCounter;
    }

    public void setConditionCounter(String conditionCounter) {
        ConditionCounter = conditionCounter;
    }

    private String Amount = "0.00";
    private String ConditionAmtPer = "";
    private String ConditionAmtPerUOM = "";
    private String Currency = "";
    private String Quantity = "";
    private String ourTotal = "";
    private String viewType="";
    private String ConditionTypeID="";
    private String CondCurrency="";
    private String ConditionValue="0.00";
    private String ConditionBaseValue="0.00";
    private String SequenceNo = "";
    private String ItemNo = "";

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMaterialNo() {
        return MaterialNo;
    }

    public void setMaterialNo(String materialNo) {
        MaterialNo = materialNo;
    }

    public String getMaterialDesc() {
        return MaterialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        MaterialDesc = materialDesc;
    }

    public String getConditionCatID() {
        return ConditionCatID;
    }

    public void setConditionCatID(String conditionCatID) {
        ConditionCatID = conditionCatID;
    }

    public String getConditionCatDesc() {
        return ConditionCatDesc;
    }

    public void setConditionCatDesc(String conditionCatDesc) {
        ConditionCatDesc = conditionCatDesc;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getConditionAmtPer() {
        return ConditionAmtPer;
    }

    public void setConditionAmtPer(String conditionAmtPer) {
        ConditionAmtPer = conditionAmtPer;
    }

    public String getConditionAmtPerUOM() {
        return ConditionAmtPerUOM;
    }

    public void setConditionAmtPerUOM(String conditionAmtPerUOM) {
        ConditionAmtPerUOM = conditionAmtPerUOM;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getOurTotal() {
        return ourTotal;
    }

    public void setOurTotal(String ourTotal) {
        this.ourTotal = ourTotal;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public String getConditionBaseValue() {
        return ConditionBaseValue;
    }

    public void setConditionBaseValue(String conditionBaseValue) {
        ConditionBaseValue = conditionBaseValue;
    }

    public String getConditionValue() {
        return ConditionValue;
    }

    public void setConditionValue(String conditionValue) {
        ConditionValue = conditionValue;
    }

    public String getConditionTypeID() {
        return ConditionTypeID;
    }

    public void setConditionTypeID(String conditionTypeID) {
        ConditionTypeID = conditionTypeID;
    }

    public String getCondCurrency() {
        return CondCurrency;
    }

    public void setCondCurrency(String condCurrency) {
        CondCurrency = condCurrency;
    }

    public String getSequenceNo() {
        return SequenceNo;
    }

    public void setSequenceNo(String sequenceNo) {
        SequenceNo = sequenceNo;
    }

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
    }
}
