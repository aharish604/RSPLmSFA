package com.rspl.sf.msfa.mbo;

import java.io.Serializable;

/**
 * Created by ccb on 21-08-2017.
 */

public class MaterialFright implements Serializable{

    public String getMatFrightCode() {
        return matFrightCode;
    }

    public void setMatFrightCode(String matFrightCode) {
        this.matFrightCode = matFrightCode;
    }

    public String getMatFrightDesc() {
        return matFrightDesc;
    }

    public void setMatFrightDesc(String matFrightDesc) {
        this.matFrightDesc = matFrightDesc;
    }

    String matFrightCode,matFrightDesc;
}
