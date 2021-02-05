package com.rspl.sf.msfa.socreate;

import java.io.Serializable;

/**
 * Created by e10769 on 08-05-2017.
 */

public class SOSubItemBean implements Serializable {
    private String date="";
    private String subQty="";
    private String dateForStore="";
    private String materialNo="";
    private String UOM="";
    private String RequirementDate="";
    private String TransportationPlanDate="";
    private String MaterialAvailDate="";

    public String getSubQty() {
        return subQty;
    }

    public void setSubQty(String subQty) {
        this.subQty = subQty;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateForStore() {
        return dateForStore;
    }

    public void setDateForStore(String dateForStore) {
        this.dateForStore = dateForStore;
    }

    public String getMaterialNo() {
        return materialNo;
    }

    public void setMaterialNo(String materialNo) {
        this.materialNo = materialNo;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public String getRequirementDate() {
        return RequirementDate;
    }

    public void setRequirementDate(String requirementDate) {
        RequirementDate = requirementDate;
    }

    public String getTransportationPlanDate() {
        return TransportationPlanDate;
    }

    public void setTransportationPlanDate(String transportationPlanDate) {
        TransportationPlanDate = transportationPlanDate;
    }

    public String getMaterialAvailDate() {
        return MaterialAvailDate;
    }

    public void setMaterialAvailDate(String materialAvailDate) {
        MaterialAvailDate = materialAvailDate;
    }
}
