package com.rspl.sf.msfa.mbo;

import java.io.Serializable;

/**
 * Created by ccb on 29-09-2017.
 */

public class SalesTargetVsAchivementBean implements Serializable{


    public String getDealerNo() {
        return dealerNo;
    }

    public void setDealerNo(String dealerNo) {
        this.dealerNo = dealerNo;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getDealerCity() {
        return dealerCity;
    }

    public void setDealerCity(String dealerCity) {
        this.dealerCity = dealerCity;
    }

    public String getCurMonthTarget() {
        return curMonthTarget;
    }

    public void setCurMonthTarget(String curMonthTarget) {
        this.curMonthTarget = curMonthTarget;
    }

    public String getProrataTraget() {
        return prorataTraget;
    }

    public void setProrataTraget(String prorataTraget) {
        this.prorataTraget = prorataTraget;
    }

    public String getSaleAcvd() {
        return saleAcvd;
    }

    public void setSaleAcvd(String saleAcvd) {
        this.saleAcvd = saleAcvd;
    }

    public String getProrataAchivement() {
        return prorataAchivement;
    }

    public void setProrataAchivement(String prorataAchivement) {
        this.prorataAchivement = prorataAchivement;
    }

    public String getBalQty() {
        return balQty;
    }

    public void setBalQty(String balQty) {
        this.balQty = balQty;
    }

    public String getDailytraget() {
        return dailytraget;
    }

    public void setDailytraget(String dailytraget) {
        this.dailytraget = dailytraget;
    }

    String dealerNo,dealerName,dealerCity, curMonthTarget,prorataTraget,saleAcvd,prorataAchivement,balQty,dailytraget;

}
