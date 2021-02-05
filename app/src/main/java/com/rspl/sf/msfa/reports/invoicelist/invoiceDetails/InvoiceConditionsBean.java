package com.rspl.sf.msfa.reports.invoicelist.invoiceDetails;

import java.io.Serializable;

/**
 * Created by e10849 on 12-10-2017.
 */

public class InvoiceConditionsBean  implements Serializable {
    private String ConditionCatDesc = "";
    private String ConditionCatID = "";
    private String ConditionAmt = "";
    private String ConditionAmtPerUOM = "";
    private String ConditionValue = "",Currency="", SequenceNo="";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name="";

    public String getConditionCatDesc() {
        return ConditionCatDesc;
    }

    public void setConditionCatDesc(String conditionCatDesc) {
        ConditionCatDesc = conditionCatDesc;
    }

    public String getConditionCatID() {
        return ConditionCatID;
    }

    public void setConditionCatID(String conditionCatID) {
        ConditionCatID = conditionCatID;
    }

    public String getConditionAmt() {
        return ConditionAmt;
    }

    public void setConditionAmt(String conditionAmt) {
        ConditionAmt = conditionAmt;
    }

    public String getConditionAmtPerUOM() {
        return ConditionAmtPerUOM;
    }

    public void setConditionAmtPerUOM(String conditionAmtPerUOM) {
        ConditionAmtPerUOM = conditionAmtPerUOM;
    }

    public String getConditionValue() {
        return ConditionValue;
    }

    public void setConditionValue(String conditionValue) {
        ConditionValue = conditionValue;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getSequenceNo() {
        return SequenceNo;
    }

    public void setSequenceNo(String sequenceNo) {
        SequenceNo = sequenceNo;
    }
}
