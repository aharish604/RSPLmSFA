package com.rspl.sf.msfa.reports.invoicelist.invoiceDetails;

import java.io.Serializable;

/**
 * Created by e10849 on 13-10-2017.
 */

public class InvConItemBean implements Serializable {
    private String ConditionValue = "";
    private String ConditionAmt = "";
    private String ConditionTotalAmt="";
    private String ConditionTotalValue="", ConditionCatDesc = "", SequenceNo = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name="";

    public String getConditionValue() {
        return ConditionValue;
    }

    public void setConditionValue(String conditionValue) {
        ConditionValue = conditionValue;
    }

    public String getConditionAmt() {
        return ConditionAmt;
    }

    public void setConditionAmt(String conditionAmt) {
        ConditionAmt = conditionAmt;
    }


    public String getConditionTotalAmt() {
        return ConditionTotalAmt;
    }

    public void setConditionTotalAmt(String conditionTotalAmt) {
        ConditionTotalAmt = conditionTotalAmt;
    }

    public String getConditionTotalValue() {
        return ConditionTotalValue;
    }

    public void setConditionTotalValue(String conditionTotalValue) {
        ConditionTotalValue = conditionTotalValue;
    }

    public String getConditionCatDesc() {
        return ConditionCatDesc;
    }

    public void setConditionCatDesc(String conditionCatDesc) {
        ConditionCatDesc = conditionCatDesc;
    }

    public String getSequenceNo() {
        return SequenceNo;
    }

    public void setSequenceNo(String sequenceNo) {
        SequenceNo = sequenceNo;
    }
}
