package com.rspl.sf.msfa.mbo;

import java.io.Serializable;

/**
 * Created by e10847 on 25-09-2017.
 */

public class AllDealerTargetDTO implements Serializable {


    private String dealer;
    private int target;
    private int MTD;
    private int LYSMTD;
    private int LYSMAchieved;

    public String getDealer() {
        return dealer;
    }

    public void setDealer(String material) {
        this.dealer = material;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getMTD() {
        return MTD;
    }

    public void setMTD(int MTD) {
        this.MTD = MTD;
    }

    public int getLYSMTD() {
        return LYSMTD;
    }

    public void setLYSMTD(int LYSMTD) {
        this.LYSMTD = LYSMTD;
    }

    public int getLYSMAchieved() {
        return LYSMAchieved;
    }

    public void setLYSMAchieved(int LYSMAchieved) {
        this.LYSMAchieved = LYSMAchieved;
    }
}
