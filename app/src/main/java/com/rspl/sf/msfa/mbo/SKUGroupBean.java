package com.rspl.sf.msfa.mbo;

/**
 * Created by e10526 on 12/22/2016.
 */

public class SKUGroupBean {
    String LastInvoiceNo = "";
    String LastMaterialNo = "";
    String SKUGroup = "";
    String MRP = "";
    String DBSTK = "";
    String RETSTK = "";
    String SOQ = "";
    String ORDQty = "";
    String NetAmount = "";
    String PRMScheme = "";
    String SecScheme = "";
    String Brand = "";
    String Category = "";
    String MatTypeVal = "";
    String MatTypeDesc = "";
    String Currency = "";
    String MaterialDesc = "";
    String MaterialNo ="";
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

    String LastPurchasedMaterial = "";

    public String getChildItemTag() {
        return ChildItemTag;
    }

    public void setChildItemTag(String childItemTag) {
        ChildItemTag = childItemTag;
    }

    String ChildItemTag = "";

    public String getLastPurchasedMaterial() {
        return LastPurchasedMaterial;
    }

    public void setLastPurchasedMaterial(String lastPurchasedMaterial) {
        LastPurchasedMaterial = lastPurchasedMaterial;
    }


    public String getLastMaterialNo() {
        return LastMaterialNo;
    }

    public void setLastMaterialNo(String lastMaterialNo) {
        LastMaterialNo = lastMaterialNo;
    }

    public String getLastInvoiceNo() {
        return LastInvoiceNo;
    }

    public void setLastInvoiceNo(String lastInvoiceNo) {
        LastInvoiceNo = lastInvoiceNo;
    }


    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    String UOM = "";
    public String getUnBilledStatus() {
        return UnBilledStatus;
    }

    public void setUnBilledStatus(String unBilledStatus) {
        UnBilledStatus = unBilledStatus;
    }

    String UnBilledStatus = "";
    public String getMultipleMatAval() {
        return MultipleMatAval;
    }

    public void setMultipleMatAval(String multipleMatAval) {
        MultipleMatAval = multipleMatAval;
    }

    String MultipleMatAval = "";

    public boolean isQtyEntered() {
        return IsQtyEntered;
    }

    public void setQtyEntered(boolean qtyEntered) {
        IsQtyEntered = qtyEntered;
    }

    boolean IsQtyEntered =false;
    public String getMatTypeVal() {
        return MatTypeVal;
    }

    public void setMatTypeVal(String matTypeVal) {
        MatTypeVal = matTypeVal;
    }

    public String getMatTypeDesc() {
        return MatTypeDesc;
    }

    public void setMatTypeDesc(String matTypeDesc) {
        MatTypeDesc = matTypeDesc;
    }



    public String getSKUGroupDesc() {
        return SKUGroupDesc;
    }

    public void setSKUGroupDesc(String SKUGroupDesc) {
        this.SKUGroupDesc = SKUGroupDesc;
    }

    String SKUGroupDesc = "";

    public boolean isMustSell() {
        return MustSell;
    }

    public void setMustSell(boolean mustSell) {
        MustSell = mustSell;
    }

    public String getSKUGroup() {
        return SKUGroup;
    }

    public void setSKUGroup(String SKUGroup) {
        this.SKUGroup = SKUGroup;
    }

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    public String getDBSTK() {
        return DBSTK;
    }

    public void setDBSTK(String DBSTK) {
        this.DBSTK = DBSTK;
    }

    public String getRETSTK() {
        return RETSTK;
    }

    public void setRETSTK(String RETSTK) {
        this.RETSTK = RETSTK;
    }

    public String getSOQ() {
        return SOQ;
    }

    public void setSOQ(String SOQ) {
        this.SOQ = SOQ;
    }

    public String getORDQty() {
        return ORDQty;
    }

    public void setORDQty(String ORDQty) {
        this.ORDQty = ORDQty;
    }

    public String getNetAmount() {
        return NetAmount;
    }

    public void setNetAmount(String netAmount) {
        NetAmount = netAmount;
    }

    public String getPRMScheme() {
        return PRMScheme;
    }

    public void setPRMScheme(String PRMScheme) {
        this.PRMScheme = PRMScheme;
    }

    public String getSecScheme() {
        return SecScheme;
    }

    public void setSecScheme(String secScheme) {
        SecScheme = secScheme;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    boolean MustSell = false;

}
