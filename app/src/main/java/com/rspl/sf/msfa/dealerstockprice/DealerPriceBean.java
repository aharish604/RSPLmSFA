package com.rspl.sf.msfa.dealerstockprice;

import com.sap.smp.client.odata.ODataGuid;

import java.io.Serializable;

/**
 * Created by e10854 on 23-10-2017.
 */

public class DealerPriceBean implements Serializable{

    String material="";
    String asonDate="";
    String price="";
    String materialno="";
    String inputPrice="";
    String currency="";
    ODataGuid cpstockitemguid;

    public String getResourcepath() {
        return resourcepath;
    }

    public void setResourcepath(String resourcepath) {
        this.resourcepath = resourcepath;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    String resourcepath="";
    String etag="";

    public ODataGuid getCpstockitemguid() {
        return cpstockitemguid;
    }

    public void setCpstockitemguid(ODataGuid cpstockitemguid) {
        this.cpstockitemguid = cpstockitemguid;
    }



    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getMaterialno() {
        return materialno;
    }

    public void setMaterialno(String materialno) {
        this.materialno = materialno;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getAsonDate() {
        return asonDate;
    }

    public void setAsonDate(String asonDate) {
        this.asonDate = asonDate;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getInputPrice() {
        return inputPrice;
    }

    public void setInputPrice(String inputPrice) {
        this.inputPrice = inputPrice;
    }
}
