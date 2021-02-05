package com.rspl.sf.msfa.mbo;

import java.io.Serializable;

/**
 * Created by e10604 on 12/5/2017.
 */

public class StorageLocBean  implements Serializable {

    public String getStoLocCode() {
        return stoLocCode;
    }

    public void setStoLocCode(String stoLocCode) {
        this.stoLocCode = stoLocCode;
    }

    public String getStoLocDesc() {
        return stoLocDesc;
    }

    public void setStoLocDesc(String stoLocDesc) {
        this.stoLocDesc = stoLocDesc;
    }

    private String stoLocCode;
    private String stoLocDesc;

}
