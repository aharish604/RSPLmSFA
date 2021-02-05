package com.rspl.sf.msfa.mbo;

import java.io.Serializable;

/**
 * Created by e10847 on 27-09-2017.
 */

public class StocksInfoDTO implements Serializable {
    private String bgHDPE;
    private String bgPerBag;
    private String utclHDPE;
    private String utclPerBag;
    private String oclPerBag;
    private String oclHDPE;
    private String lafHDPE;
    private String lafPerBag;
    private String accPerBag;
    private String accHDPE;

    public String getBgHDPE() {
        return bgHDPE;
    }

    public void setBgHDPE(String bgHDPE) {
        this.bgHDPE = bgHDPE;
    }

    public String getBgPerBag() {
        return bgPerBag;
    }

    public void setBgPerBag(String bgPerBag) {
        this.bgPerBag = bgPerBag;
    }

    public String getUtclHDPE() {
        return utclHDPE;
    }

    public void setUtclHDPE(String utclHDPE) {
        this.utclHDPE = utclHDPE;
    }

    public String getUtclPerBag() {
        return utclPerBag;
    }

    public void setUtclPerBag(String utclPerBag) {
        this.utclPerBag = utclPerBag;
    }

    public String getOclPerBag() {
        return oclPerBag;
    }

    public void setOclPerBag(String oclPerBag) {
        this.oclPerBag = oclPerBag;
    }

    public String getOclHDPE() {
        return oclHDPE;
    }

    public void setOclHDPE(String oclHDPE) {
        this.oclHDPE = oclHDPE;
    }

    public String getLafHDPE() {
        return lafHDPE;
    }

    public void setLafHDPE(String lafHDPE) {
        this.lafHDPE = lafHDPE;
    }

    public String getLafPerBag() {
        return lafPerBag;
    }

    public void setLafPerBag(String lafPerBag) {
        this.lafPerBag = lafPerBag;
    }

    public String getAccPerBag() {
        return accPerBag;
    }

    public void setAccPerBag(String accPerBag) {
        this.accPerBag = accPerBag;
    }

    public String getAccHDPE() {
        return accHDPE;
    }

    public void setAccHDPE(String accHDPE) {
        this.accHDPE = accHDPE;
    }
}
