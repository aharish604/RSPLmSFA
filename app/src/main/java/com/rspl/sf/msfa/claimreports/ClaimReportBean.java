package com.rspl.sf.msfa.claimreports;

import java.io.Serializable;
import java.util.ArrayList;

public class ClaimReportBean implements Serializable {
    private String ClaimAmount="";
    private String totalClaimAmount="";

    public String getTotalClaimAmount() {
        return totalClaimAmount;
    }

    public void setTotalClaimAmount(String totalClaimAmount) {
        this.totalClaimAmount = totalClaimAmount;
    }

    public String getTotalMaxClaimAmt() {
        return totalMaxClaimAmt;
    }

    public void setTotalMaxClaimAmt(String totalMaxClaimAmt) {
        this.totalMaxClaimAmt = totalMaxClaimAmt;
    }

    private String totalMaxClaimAmt="";
    private String ZMaxClaimAmt="";
    private String ZSchemeTypeDesc="";
    private String ZSchemeType="";
    private String ZSchemeValidTo="";
    private String ZSchemeValidFrm="";
    private String ParentName="";
    private ArrayList<ClaimReportBean> claimReportBeans = new ArrayList<>();

    public ArrayList<ClaimReportBean> getClaimReportBeans() {
        return claimReportBeans;
    }

    public void setClaimReportBeans(ArrayList<ClaimReportBean> claimReportBeans) {
        this.claimReportBeans = claimReportBeans;
    }

    public String getParentName() {
        return ParentName;
    }

    public void setParentName(String parentName) {
        ParentName = parentName;
    }

    public String getParentNo() {
        return ParentNo;
    }

    public void setParentNo(String parentNo) {
        ParentNo = parentNo;
    }

    private String ParentNo="";

    public String getClaimAmount() {
        return ClaimAmount;
    }

    public void setClaimAmount(String claimAmount) {
        ClaimAmount = claimAmount;
    }

    public String getZMaxClaimAmt() {
        return ZMaxClaimAmt;
    }

    public void setZMaxClaimAmt(String ZMaxClaimAmt) {
        this.ZMaxClaimAmt = ZMaxClaimAmt;
    }

    public String getZSchemeTypeDesc() {
        return ZSchemeTypeDesc;
    }

    public void setZSchemeTypeDesc(String ZSchemeTypeDesc) {
        this.ZSchemeTypeDesc = ZSchemeTypeDesc;
    }

    public String getZSchemeType() {
        return ZSchemeType;
    }

    public void setZSchemeType(String ZSchemeType) {
        this.ZSchemeType = ZSchemeType;
    }

    public String getZSchemeValidTo() {
        return ZSchemeValidTo;
    }

    public void setZSchemeValidTo(String ZSchemeValidTo) {
        this.ZSchemeValidTo = ZSchemeValidTo;
    }

    public String getZSchemeValidFrm() {
        return ZSchemeValidFrm;
    }

    public void setZSchemeValidFrm(String ZSchemeValidFrm) {
        this.ZSchemeValidFrm = ZSchemeValidFrm;
    }


}
