package com.rspl.sf.msfa.mbo;

import java.io.Serializable;

/**
 * Created by ccb on 26-05-2017.
 */

public class SaleDistrictBean implements Serializable {

    public String getSaleDistCode() {
        return SaleDistCode;
    }

    public void setSaleDistCode(String saleDistCode) {
        SaleDistCode = saleDistCode;
    }

    public String getSalesDistDesc() {
        return SalesDistDesc;
    }

    public void setSalesDistDesc(String salesDistDesc) {
        SalesDistDesc = salesDistDesc;
    }

    String SaleDistCode,SalesDistDesc;

}
