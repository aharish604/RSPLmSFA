package com.rspl.sf.msfa.dealerstock;

import java.io.Serializable;

/**
 * Created by e10526 on 05-04-2018.
 */

public class DealerStockBean implements Serializable {
    private String CPStockItemGUID = "";
    private String CPGUID = "";
    private String CPNo = "";
    private String CPName = "";
    private String CPTypeID = "";
    private String UnrestrictedQty = "";
    private String EnterdQty = "";
    private String UOM = "";
    private String StockValue = "";
    private String StockOwner = "";
    private String MRP = "";
    private String LandingPrice = "";
    private String OrderMaterialGroupID = "";
    private String MaterialNo = "";
    private String OrderMaterialGroupDesc = "";
    private String MaterialDesc = "";
    private String AsOnDate = "";
    private String DMSDivision = "";
    private String BrandDesc = "";
    private String Brand = "";

    public String getBannerDesc() {
        return BannerDesc;
    }

    public void setBannerDesc(String bannerDesc) {
        BannerDesc = bannerDesc;
    }

    private String BannerDesc = "";

    public String getBanner() {
        return Banner;
    }

    public void setBanner(String banner) {
        Banner = banner;
    }

    private String Banner = "";

    public String getSkuGroupDesc() {
        return SkuGroupDesc;
    }

    public void setSkuGroupDesc(String skuGroupDesc) {
        SkuGroupDesc = skuGroupDesc;
    }

    private String SkuGroupDesc = "";

    public String getSkuGroup() {
        return SkuGroup;
    }

    public void setSkuGroup(String skuGroup) {
        SkuGroup = skuGroup;
    }

    private String SkuGroup = "";

    public String getEtag() {
        return Etag;
    }

    public void setEtag(String etag) {
        Etag = etag;
    }

    private String Etag = "";

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getBrandDesc() {
        return BrandDesc;
    }

    public void setBrandDesc(String brandDesc) {
        BrandDesc = brandDesc;
    }

    public String getCPStockItemGUID() {
        return CPStockItemGUID;
    }

    public void setCPStockItemGUID(String CPStockItemGUID) {
        this.CPStockItemGUID = CPStockItemGUID;
    }

    public String getCPGUID() {
        return CPGUID;
    }

    public void setCPGUID(String CPGUID) {
        this.CPGUID = CPGUID;
    }

    public String getCPNo() {
        return CPNo;
    }

    public void setCPNo(String CPNo) {
        this.CPNo = CPNo;
    }

    public String getCPName() {
        return CPName;
    }

    public void setCPName(String CPName) {
        this.CPName = CPName;
    }

    public String getCPTypeID() {
        return CPTypeID;
    }

    public void setCPTypeID(String CPTypeID) {
        this.CPTypeID = CPTypeID;
    }

    public String getUnrestrictedQty() {
        return UnrestrictedQty;
    }

    public void setUnrestrictedQty(String unrestrictedQty) {
        UnrestrictedQty = unrestrictedQty;
    }

    public String getEnterdQty() {
        return EnterdQty;
    }

    public void setEnterdQty(String enterdQty) {
        EnterdQty = enterdQty;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public String getStockValue() {
        return StockValue;
    }

    public void setStockValue(String stockValue) {
        StockValue = stockValue;
    }

    public String getStockOwner() {
        return StockOwner;
    }

    public void setStockOwner(String stockOwner) {
        StockOwner = stockOwner;
    }

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    public String getLandingPrice() {
        return LandingPrice;
    }

    public void setLandingPrice(String landingPrice) {
        LandingPrice = landingPrice;
    }

    public String getOrderMaterialGroupID() {
        return OrderMaterialGroupID;
    }

    public void setOrderMaterialGroupID(String orderMaterialGroupID) {
        OrderMaterialGroupID = orderMaterialGroupID;
    }

    public String getMaterialNo() {
        return MaterialNo;
    }

    public void setMaterialNo(String materialNo) {
        MaterialNo = materialNo;
    }

    public String getOrderMaterialGroupDesc() {
        return OrderMaterialGroupDesc;
    }

    public void setOrderMaterialGroupDesc(String orderMaterialGroupDesc) {
        OrderMaterialGroupDesc = orderMaterialGroupDesc;
    }

    public String getMaterialDesc() {
        return MaterialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        MaterialDesc = materialDesc;
    }

    public String getAsOnDate() {
        return AsOnDate;
    }

    public void setAsOnDate(String asOnDate) {
        AsOnDate = asOnDate;
    }

    public String getDMSDivision() {
        return DMSDivision;
    }

    public void setDMSDivision(String DMSDivision) {
        this.DMSDivision = DMSDivision;
    }

    public String getDmsDivisionDesc() {
        return DmsDivisionDesc;
    }

    public void setDmsDivisionDesc(String dmsDivisionDesc) {
        DmsDivisionDesc = dmsDivisionDesc;
    }

    private String DmsDivisionDesc = "";

    public String getStockType() {
        return StockType;
    }

    public void setStockType(String stockType) {
        StockType = stockType;
    }

    private String StockType = "";
    private boolean isChecked;
    private boolean isHide = false;
    private String matNoAndDesc="";
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        isHide = hide;
    }
    public String getMatNoAndDesc() {
        return matNoAndDesc;
    }

    public void setMatNoAndDesc(String matNoAndDesc) {
        this.matNoAndDesc = matNoAndDesc;
    }
    public boolean isDecimalCheck() {
        return decimalCheck;
    }

    public void setDecimalCheck(boolean decimalCheck) {
        this.decimalCheck = decimalCheck;
    }

    public boolean decimalCheck=false;

}
