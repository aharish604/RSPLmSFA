package com.rspl.sf.msfa.soapproval;

import java.io.Serializable;

/**
 * Created by e10847 on 08-01-2018.
 */

public class SalesOrderConditionsBean implements Serializable {
    private String soNo="";
    private String conditionTypeID="";
    private String conditionTypeDescription="";
    private String conditionPricingDate="";
    private String conditionAmount="";
    private String conditionValue="";
    private String currency="";
    private String condCurrency="";
    private String conditionCounter="";
    private String name="";
    private String viewType="";
    private String totalAmount="";
    private String ConditionAmtPer="";
    private String ItemNo="";

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSoNo() {
        return soNo;
    }

    public void setSoNo(String soNo) {
        this.soNo = soNo;
    }

    public String getconditionTypeID() {
        return conditionTypeID;
    }

    public void setconditionTypeID(String conditionTypeID) {
        this.conditionTypeID = conditionTypeID;
    }

    public String getconditionTypeDescription() {
        return conditionTypeDescription;
    }

    public void setconditionTypeDescription(String conditionTypeDescription) {
        this.conditionTypeDescription = conditionTypeDescription;
    }

    public String getconditionPricingDate() {
        return conditionPricingDate;
    }

    public void setconditionPricingDate(String conditionPricingDate) {
        this.conditionPricingDate = conditionPricingDate;
    }

    public String getconditionAmount() {
        return conditionAmount;
    }

    public void setconditionAmount(String conditionAmount) {
        this.conditionAmount = conditionAmount;
    }

    public String getconditionValue() {
        return conditionValue;
    }

    public void setconditionValue(String conditionValue) {
        this.conditionValue = conditionValue;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCondCurrency() {
        return condCurrency;
    }

    public void setCondCurrency(String condCurrency) {
        this.condCurrency = condCurrency;
    }

    public String getconditionCounter() {
        return conditionCounter;
    }

    public void setconditionCounter(String conditionCounter) {
        this.conditionCounter = conditionCounter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConditionAmtPer() {
        return ConditionAmtPer;
    }

    public void setConditionAmtPer(String conditionAmtPer) {
        ConditionAmtPer = conditionAmtPer;
    }

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
    }
}
