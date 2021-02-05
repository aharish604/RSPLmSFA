package com.rspl.sf.msfa.mbo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by e10526 on 02-07-2016.
 *
 */
public class MyTargetsBean implements Serializable{

    private  String KPICode = "";
    private  String TargetItemGUID = "";
    private  String TargetGUID = "";

    public ArrayList<String> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }

    private ArrayList<String> arrayList = new ArrayList<>(

    );

    private  String KPIName = "";
    private  String MonthTarget = "";
    private  String MTDA = "";
    private  String CRR = "";
    private  String ARR = "";
    private  String NetAmount = "";
    private  String matCat = "";
    private  String kpiGuid = "";
    private String MaterialNo="";
    private String MaterialDesc="";
    private String AmtLMTD="";
    private String AmtMTD="";
    private String AmtMonth1PrevPerf="";
    private String AmtMonth2PrevPerf="";
    private String AmtMonth3PrevPerf="";
    private String GrPer="";
    private String KPIFor ="";
    private String CalculationBase="";
    private String CalculationSource="";

    private String OrderMaterialGroupID="";
    private String OrderMaterialGroupDesc="";
    private String MaterialGroup="";
    private String MaterialGrpDesc="";
    private String PartnerNo="";
    private String PartnerName="";
    private String Currency="";
    private boolean showProgress = false;

    public String getPartnerNo() {
        return PartnerNo;
    }

    public void setPartnerNo(String partnerNo) {
        PartnerNo = partnerNo;
    }

    public String getTargetGUID() {
        return TargetGUID;
    }

    public void setTargetGUID(String targetGUID) {
        TargetGUID = targetGUID;
    }

    public String getPartnerName() {
        return PartnerName;
    }

    public void setPartnerName(String partnerName) {
        PartnerName = partnerName;
    }

    public String getTargetItemGUID() {
        return TargetItemGUID;
    }

    public void setTargetItemGUID(String targetItemGUID) {
        TargetItemGUID = targetItemGUID;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    private String UOM="";

    public HashSet<String> getKpiNames() {
        return kpiNames;
    }

    public void setKpiNames(HashSet<String> kpiNames) {
        this.kpiNames = kpiNames;
    }

    private HashSet<String> kpiNames = new HashSet<>();

    public String getMaterialGrpDesc() {
        return MaterialGrpDesc;
    }

    public void setMaterialGrpDesc(String materialGrpDesc) {
        MaterialGrpDesc = materialGrpDesc;
    }

    public String getMaterialGroup() {
        return MaterialGroup;
    }

    public void setMaterialGroup(String materialGroup) {
        MaterialGroup = materialGroup;
    }

    public String getOrderMaterialGroupDesc() {
        return OrderMaterialGroupDesc;
    }

    public void setOrderMaterialGroupDesc(String orderMaterialGroupDesc) {
        OrderMaterialGroupDesc = orderMaterialGroupDesc;
    }

    public String getOrderMaterialGroupID() {
        return OrderMaterialGroupID;
    }

    public void setOrderMaterialGroupID(String orderMaterialGroupID) {
        OrderMaterialGroupID = orderMaterialGroupID;
    }



    public String getAchivedPercentage() {
        return AchivedPercentage;
    }

    public void setAchivedPercentage(String achivedPercentage) {
        AchivedPercentage = achivedPercentage;
    }

    private String AchivedPercentage="";

    public String getRollUpTo() {
        return RollUpTo;
    }

    public void setRollUpTo(String rollUpTo) {
        RollUpTo = rollUpTo;
    }

    private String RollUpTo="";

    public String getCalculationSource() {
        return CalculationSource;
    }

    public void setCalculationSource(String calculationSource) {
        CalculationSource = calculationSource;
    }

    public String getCalculationBase() {
        return CalculationBase;
    }

    public void setCalculationBase(String calculationBase) {
        CalculationBase = calculationBase;
    }

    public String getKPIFor() {
        return KPIFor;
    }

    public void setKPIFor(String KPIFor) {
        this.KPIFor = KPIFor;
    }



    public String getBTD() {
        return BTD;
    }

    public void setBTD(String BTD) {
        this.BTD = BTD;
    }

    private String BTD="";

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
    public String getKpiGuid() {
        return kpiGuid;
    }

    public void setKpiGuid(String kpiGuid) {
        this.kpiGuid = kpiGuid;
    }


    public String getKpiGuid32() {
        return kpiGuid32;
    }

    public void setKpiGuid32(String kpiGuid32) {
        this.kpiGuid32 = kpiGuid32;
    }

    private  String kpiGuid32 = "";

    private  String MatNo = "";

    public String getMatDesc() {
        return MatDesc;
    }

    public void setMatDesc(String matDesc) {
        MatDesc = matDesc;
    }

    public String getMatNo() {
        return MatNo;
    }

    public void setMatNo(String matNo) {
        MatNo = matNo;
    }

    private  String MatDesc = "";

    public String getNetAmount() {
        return NetAmount;
    }

    public void setNetAmount(String netAmount) {
        NetAmount = netAmount;
    }

    public String getMatCat() {
        return matCat;
    }

    public void setMatCat(String matCat) {
        this.matCat = matCat;
    }




    public String getARR() {
        return ARR;
    }

    public void setARR(String ARR) {
        this.ARR = ARR;
    }

    public String getKPIName() {
        return KPIName;
    }

    public void setKPIName(String KPIName) {
        this.KPIName = KPIName;
    }

    public String getMonthTarget() {
        return MonthTarget;
    }

    public void setMonthTarget(String monthTarget) {
        MonthTarget = monthTarget;
    }

    public String getMTDA() {
        return MTDA;
    }

    public void setMTDA(String MTDA) {
        this.MTDA = MTDA;
    }

    public String getCRR() {
        return CRR;
    }

    public void setCRR(String CRR) {
        this.CRR = CRR;
    }

    public String getKPICode() {
        return KPICode;
    }

    public void setKPICode(String KPICode) {
        this.KPICode = KPICode;
    }

    private String KPICategory="";

    public String getPeriodicity() {
        return Periodicity;
    }

    public void setPeriodicity(String periodicity) {
        Periodicity = periodicity;
    }

    private String Periodicity="";

    public String getPeriodicityDesc() {
        return PeriodicityDesc;
    }

    public void setPeriodicityDesc(String periodicityDesc) {
        PeriodicityDesc = periodicityDesc;
    }

    private String PeriodicityDesc="";
    public String getKPICategory() {
        return KPICategory;
    }

    public void setKPICategory(String KPICategory) {
        this.KPICategory = KPICategory;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }
}
