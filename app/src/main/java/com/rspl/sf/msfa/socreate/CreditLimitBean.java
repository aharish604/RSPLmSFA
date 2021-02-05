package com.rspl.sf.msfa.socreate;

import java.io.Serializable;

/**
 * Created by e10769 on 19-05-2017.
 */

public class CreditLimitBean implements Serializable {
    private String Currency = "";
    private String CreditLimit = "";
    private String CreditExposure = "";
    private String Recievables = "";
    private String SpecialLiabilities = "";
    private String SalesValue = "";
    private String CreditLimitUsedPerc = "";
    private String BalanceAmount = "0.0";
    private String LastPaymentAmount = "";
    private String LastPaymentDate = "";
    private String CreditAccountID = "";
    private String Customer = "";
    private String CustomerName = "";
    private String CreditControlAreaID = "";

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getCreditLimit() {
        return CreditLimit;
    }

    public void setCreditLimit(String creditLimit) {
        CreditLimit = creditLimit;
    }

    public String getCreditExposure() {
        return CreditExposure;
    }

    public void setCreditExposure(String creditExposure) {
        CreditExposure = creditExposure;
    }

    public String getRecievables() {
        return Recievables;
    }

    public void setRecievables(String recievables) {
        Recievables = recievables;
    }

    public String getSpecialLiabilities() {
        return SpecialLiabilities;
    }

    public void setSpecialLiabilities(String specialLiabilities) {
        SpecialLiabilities = specialLiabilities;
    }

    public String getSalesValue() {
        return SalesValue;
    }

    public void setSalesValue(String salesValue) {
        SalesValue = salesValue;
    }

    public String getCreditLimitUsedPerc() {
        return CreditLimitUsedPerc;
    }

    public void setCreditLimitUsedPerc(String creditLimitUsedPerc) {
        CreditLimitUsedPerc = creditLimitUsedPerc;
    }

    public String getBalanceAmount() {
        return BalanceAmount;
    }

    public void setBalanceAmount(String balanceAmount) {
        BalanceAmount = balanceAmount;
    }

    public String getLastPaymentAmount() {
        return LastPaymentAmount;
    }

    public void setLastPaymentAmount(String lastPaymentAmount) {
        LastPaymentAmount = lastPaymentAmount;
    }

    public String getLastPaymentDate() {
        return LastPaymentDate;
    }

    public void setLastPaymentDate(String lastPaymentDate) {
        LastPaymentDate = lastPaymentDate;
    }

    public String getCreditAccountID() {
        return CreditAccountID;
    }

    public void setCreditAccountID(String creditAccountID) {
        CreditAccountID = creditAccountID;
    }

    public String getCustomer() {
        return Customer;
    }

    public void setCustomer(String customer) {
        Customer = customer;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getCreditControlAreaID() {
        return CreditControlAreaID;
    }

    public void setCreditControlAreaID(String creditControlAreaID) {
        CreditControlAreaID = creditControlAreaID;
    }
}
