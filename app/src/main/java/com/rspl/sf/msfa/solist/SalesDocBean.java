package com.rspl.sf.msfa.solist;

import java.io.Serializable;

/**
 * Created by e10769 on 31-05-2017.
 */

public class SalesDocBean implements Serializable {

    private String SalesDocCat="";
    private String SalesDocNo="";
    private String ItemNo="";
    private String SalesDocCatDesc="";
    private String PreSalesDocNo="";
    private String PreSalesDocType="";
    private String Qty="";
    private String NetValue="";
    private String CreatedOn="";
    String Currency = "";

    public String getPreSalesDocType() {
        return PreSalesDocType;
    }

    public void setPreSalesDocType(String preSalesDocType) {
        PreSalesDocType = preSalesDocType;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getSalesDocCat() {
        return SalesDocCat;
    }

    public void setSalesDocCat(String salesDocCat) {
        SalesDocCat = salesDocCat;
    }

    public String getSalesDocNo() {
        return SalesDocNo;
    }

    public void setSalesDocNo(String salesDocNo) {
        SalesDocNo = salesDocNo;
    }

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
    }

    public String getSalesDocCatDesc() {
        return SalesDocCatDesc;
    }

    public void setSalesDocCatDesc(String salesDocCatDesc) {
        SalesDocCatDesc = salesDocCatDesc;
    }

    public String getPreSalesDocNo() {
        return PreSalesDocNo;
    }

    public void setPreSalesDocNo(String preSalesDocNo) {
        PreSalesDocNo = preSalesDocNo;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getNetValue() {
        return NetValue;
    }

    public void setNetValue(String netValue) {
        NetValue = netValue;
    }

    public String getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(String createdOn) {
        CreatedOn = createdOn;
    }
}
