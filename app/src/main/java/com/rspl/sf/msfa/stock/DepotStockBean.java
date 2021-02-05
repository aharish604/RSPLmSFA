package com.rspl.sf.msfa.stock;

/**
 * Created by e10742 on 6/7/2017.
 */

public class DepotStockBean {

    String MatNo = "";
    String MatDesc = "";
    String Qty = "";
    String UOM = "";

    public String getStorage1() {
        return storage1;
    }

    public void setStorage1(String storage1) {
        this.storage1 = storage1;
    }

    public String getStorage1Id() {
        return storage1Id;
    }

    public void setStorage1Id(String storage1Id) {
        this.storage1Id = storage1Id;
    }

    public String getStorage1Desc() {
        return storage1Desc;
    }

    public void setStorage1Desc(String storage1Desc) {
        this.storage1Desc = storage1Desc;
    }

    public String getStorage2() {
        return storage2;
    }

    public void setStorage2(String storage2) {
        this.storage2 = storage2;
    }

    public String getStorage2Id() {
        return storage2Id;
    }

    public void setStorage2Id(String storage2Id) {
        this.storage2Id = storage2Id;
    }

    public String getStorage2Desc() {
        return storage2Desc;
    }

    public void setStorage2Desc(String storage2Desc) {
        this.storage2Desc = storage2Desc;
    }

    public String getStorage3() {
        return storage3;
    }

    public void setStorage3(String storage3) {
        this.storage3 = storage3;
    }

    public String getStorage3Id() {
        return storage3Id;
    }

    public void setStorage3Id(String storage3Id) {
        this.storage3Id = storage3Id;
    }

    public String getStorage3Desc() {
        return storage3Desc;
    }

    public void setStorage3Desc(String storage3Desc) {
        this.storage3Desc = storage3Desc;
    }

    public String getStorage4() {
        return storage4;
    }

    public void setStorage4(String storage4) {
        this.storage4 = storage4;
    }

    public String getStorage4Id() {
        return storage4Id;
    }

    public void setStorage4Id(String storage4Id) {
        this.storage4Id = storage4Id;
    }

    public String getStorage4Desc() {
        return storage4Desc;
    }

    public void setStorage4Desc(String storage4Desc) {
        this.storage4Desc = storage4Desc;
    }

    public String getStorage5() {
        return storage5;
    }

    public void setStorage5(String storage5) {
        this.storage5 = storage5;
    }

    public String getStorage5Id() {
        return storage5Id;
    }

    public void setStorage5Id(String storage5Id) {
        this.storage5Id = storage5Id;
    }

    public String getStorage5Desc() {
        return storage5Desc;
    }

    public void setStorage5Desc(String storage5Desc) {
        this.storage5Desc = storage5Desc;
    }

    String storage1,storage1Id,storage1Desc,storage2,storage2Id,storage2Desc,storage3,storage3Id,storage3Desc,storage4,
    storage4Id,storage4Desc,storage5,storage5Id,storage5Desc;

    public boolean isAddressEnabled() {
        return isAddressEnabled;
    }

    public void setAddressEnabled(boolean addressEnabled) {
        isAddressEnabled = addressEnabled;
    }

    private boolean isAddressEnabled;

    public DepotStockBean() {

    }

    public String getMatNo() {
        return MatNo;
    }

    public void setMatNo(String matNo) {
        MatNo = matNo;
    }

    public String getMatDesc() {
        return MatDesc;
    }

    public void setMatDesc(String matDesc) {
        MatDesc = matDesc;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }
}
