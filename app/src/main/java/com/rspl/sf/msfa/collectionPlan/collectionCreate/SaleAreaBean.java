package com.rspl.sf.msfa.collectionPlan.collectionCreate;

import java.io.Serializable;

public class SaleAreaBean implements Serializable{
    private String creditControlAreaID= "";
    private String creditControlAreaDesc= "";

    public String getCreditControlAreaID() {
        return creditControlAreaID;
    }

    public void setCreditControlAreaID(String creditControlAreaID) {
        this.creditControlAreaID = creditControlAreaID;
    }

    public String getCreditControlAreaDesc() {
        return creditControlAreaDesc;
    }

    public void setCreditControlAreaDesc(String creditControlAreaDesc) {
        this.creditControlAreaDesc = creditControlAreaDesc;
    }
}
