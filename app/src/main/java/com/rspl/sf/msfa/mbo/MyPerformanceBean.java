package com.rspl.sf.msfa.mbo;

import java.util.HashMap;

/**
 * Created by ${e10526} on ${23-06-2016}.
 */
public class MyPerformanceBean {
    private String MaterialNo = "";
    private String MaterialDesc = "";
    private String AmtLMTD = "";
    private String AmtMTD = "";
    private String AmtMonth1PrevPerf = "";
    private String AmtMonth2PrevPerf = "";
    private String AmtMonth3PrevPerf = "";
    private String GrPer = "";
    private String BTD = "";
    private String AvgLstThreeMonth = "";
    private String CMTarget = "";
    private String BalToDo = "";
    private String AchivedPer = "";
    private String LyGrowthPer = "";
    private String UOM="";

    public String getReportType() {
        return ReportType;
    }

    public void setReportType(String reportType) {
        ReportType = reportType;
    }

    private String ReportType="";

    public HashMap<String, String> getMapUOM() {
        return mapUOM;
    }

    public void setMapUOM(HashMap<String, String> mapUOM) {
        this.mapUOM = mapUOM;
    }

    HashMap<String, String> mapUOM =null;

    public String getBTD() {
        return BTD;
    }

    public void setBTD(String BTD) {
        this.BTD = BTD;
    }

    public String getGrPer() {
        return GrPer;
    }

    public void setGrPer(String grPer) {
        GrPer = grPer;
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

    public String getAmtLMTD() {
        return AmtLMTD;
    }

    public void setAmtLMTD(String amtLMTD) {
        AmtLMTD = amtLMTD;
    }

    public String getAmtMTD() {
        return AmtMTD;
    }

    public void setAmtMTD(String amtMTD) {
        AmtMTD = amtMTD;
    }

    public String getAmtMonth1PrevPerf() {
        return AmtMonth1PrevPerf;
    }

    public void setAmtMonth1PrevPerf(String amtMonth1PrevPerf) {
        AmtMonth1PrevPerf = amtMonth1PrevPerf;
    }

    public String getAmtMonth2PrevPerf() {
        return AmtMonth2PrevPerf;
    }

    public void setAmtMonth2PrevPerf(String amtMonth2PrevPerf) {
        AmtMonth2PrevPerf = amtMonth2PrevPerf;
    }

    public String getAmtMonth3PrevPerf() {
        return AmtMonth3PrevPerf;
    }

    public void setAmtMonth3PrevPerf(String amtMonth3PrevPerf) {
        AmtMonth3PrevPerf = amtMonth3PrevPerf;
    }

    public String getAvgLstThreeMonth() {
        return AvgLstThreeMonth;
    }

    public void setAvgLstThreeMonth(String avgLstThreeMonth) {
        AvgLstThreeMonth = avgLstThreeMonth;
    }

    public String getCMTarget() {
        return CMTarget;
    }

    public void setCMTarget(String CMTarget) {
        this.CMTarget = CMTarget;
    }

    public String getBalToDo() {
        return BalToDo;
    }

    public void setBalToDo(String balToDo) {
        BalToDo = balToDo;
    }

    public String getLyGrowthPer() {
        return LyGrowthPer;
    }

    public void setLyGrowthPer(String lyGrowthPer) {
        LyGrowthPer = lyGrowthPer;
    }

    public String getAchivedPer() {
        return AchivedPer;
    }

    public void setAchivedPer(String achivedPer) {
        AchivedPer = achivedPer;
    }


    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }
}
