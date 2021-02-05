package com.rspl.sf.msfa.mbo;

import java.io.Serializable;

/**
 * Created by e10847 on 17-10-2017.
 */

public class StocksInfoBean implements Serializable {

    private String materialDesc="";
    private String materialNo="";
    private String asOnDateQuantity ="";
    private String quantityInputText="";
    private String UOM="";
    private Boolean isChecked= false;
    private boolean stockType= false;
    private String StockGuid="";
    private String etag="";

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getStockGuid() {
        return StockGuid;
    }

    public void setStockGuid(String stockGuid) {
        StockGuid = stockGuid;
    }

    public boolean isStockType() {
        return stockType;
    }

    public void setStockType(boolean stockType) {
        this.stockType = stockType;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public String getMaterialDesc() {
        return materialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        this.materialDesc = materialDesc;
    }

    public String getMaterialNo() {
        return materialNo;
    }

    public void setMaterialNo(String materialNo) {
        this.materialNo = materialNo;
    }

    public String getAsOnDateQuantity() {
        return asOnDateQuantity;
    }

    public void setAsOnDateQuantity(String asOnDateQuantity) {
        this.asOnDateQuantity = asOnDateQuantity;
    }

    public String getQuantityInputText() {
        return quantityInputText;
    }

    public void setQuantityInputText(String quantityInputText) {
        this.quantityInputText = quantityInputText;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }
}
