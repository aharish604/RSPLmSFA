package com.rspl.sf.msfa.returnOrder.returnDetail;

import java.io.Serializable;

public class ReturnOrderItemBean implements Serializable {


    private String retOrdNo;

    private String stockGuid;

    private String itemNo;

    private String loginID;

    private String material;

    private String materialDesc;

    private String materialGroup;

    private String matGroupDesc;

    private String itemCategoryID;

    private String itemCategoryDesc;

    private String quantity;

    private String invoiceQty;

    private String uOM;

    private String batch;

    private String invoiceNo;

    private String invoiceItemNo;

    private String statusID;

    private String statusDesc;

    private String gRStatusID;

    private String gRStatusDesc;

    private String plant;

    private String plantDesc;

    private String currency;

    private String unitPrice;

    private String netAmount;

    private String rejReason;

    private String rejReasonDesc;

    private String depotStock;

    private String ownStock;

    private String mRP;

    private String mFD;

    private String expiryDate;

    private String priDiscAmt;

    private String priDiscPerc;

    private String tax1Amt;

    private String tax2Amt;

    private String tax3Amt;

    private String tax1Percent;

    private String tax2Percent;

    private String tax3Percent;

    public String getuOM() {
        return uOM;
    }

    public void setuOM(String uOM) {
        this.uOM = uOM;
    }

    public String getgRStatusID() {
        return gRStatusID;
    }

    public void setgRStatusID(String gRStatusID) {
        this.gRStatusID = gRStatusID;
    }

    public String getgRStatusDesc() {
        return gRStatusDesc;
    }

    public void setgRStatusDesc(String gRStatusDesc) {
        this.gRStatusDesc = gRStatusDesc;
    }

    public String getmRP() {
        return mRP;
    }

    public void setmRP(String mRP) {
        this.mRP = mRP;
    }

    public Object getmFD() {
        return mFD;
    }

    public void setmFD(String mFD) {
        this.mFD = mFD;
    }

    public String getReferenceUOM() {
        return ReferenceUOM;
    }

    public void setReferenceUOM(String referenceUOM) {
        ReferenceUOM = referenceUOM;
    }

    private String ReferenceUOM = "";

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    private String OrderDate = "";

    public String getROMaterialDescAndNo() {
        return ROMaterialDescAndNo;
    }

    public void setROMaterialDescAndNo(String ROMaterialDescAndNo) {
        this.ROMaterialDescAndNo = ROMaterialDescAndNo;
    }

    private String ROMaterialDescAndNo = "";

    public String getRetOrdNo() {
        return retOrdNo;
    }

    public void setRetOrdNo(String retOrdNo) {
        this.retOrdNo = retOrdNo;
    }

    public String getStockGuid() {
        return stockGuid;
    }

    public void setStockGuid(String stockGuid) {
        this.stockGuid = stockGuid;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getLoginID() {
        return loginID;
    }

    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getMaterialDesc() {
        return materialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        this.materialDesc = materialDesc;
    }

    public String getMaterialGroup() {
        return materialGroup;
    }

    public void setMaterialGroup(String materialGroup) {
        this.materialGroup = materialGroup;
    }

    public String getMatGroupDesc() {
        return matGroupDesc;
    }

    public void setMatGroupDesc(String matGroupDesc) {
        this.matGroupDesc = matGroupDesc;
    }

    public String getItemCategoryID() {
        return itemCategoryID;
    }

    public void setItemCategoryID(String itemCategoryID) {
        this.itemCategoryID = itemCategoryID;
    }

    public String getItemCategoryDesc() {
        return itemCategoryDesc;
    }

    public void setItemCategoryDesc(String itemCategoryDesc) {
        this.itemCategoryDesc = itemCategoryDesc;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getInvoiceQty() {
        return invoiceQty;
    }

    public void setInvoiceQty(String invoiceQty) {
        this.invoiceQty = invoiceQty;
    }

    public String getUOM() {
        return uOM;
    }

    public void setUOM(String uOM) {
        this.uOM = uOM;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getInvoiceItemNo() {
        return invoiceItemNo;
    }

    public void setInvoiceItemNo(String invoiceItemNo) {
        this.invoiceItemNo = invoiceItemNo;
    }

    public String getStatusID() {
        return statusID;
    }

    public void setStatusID(String statusID) {
        this.statusID = statusID;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getGRStatusID() {
        return gRStatusID;
    }

    public void setGRStatusID(String gRStatusID) {
        this.gRStatusID = gRStatusID;
    }

    public String getGRStatusDesc() {
        return gRStatusDesc;
    }

    public void setGRStatusDesc(String gRStatusDesc) {
        this.gRStatusDesc = gRStatusDesc;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getPlantDesc() {
        return plantDesc;
    }

    public void setPlantDesc(String plantDesc) {
        this.plantDesc = plantDesc;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(String netAmount) {
        this.netAmount = netAmount;
    }

    public String getRejReason() {
        return rejReason;
    }

    public void setRejReason(String rejReason) {
        this.rejReason = rejReason;
    }

    public String getRejReasonDesc() {
        return rejReasonDesc;
    }

    public void setRejReasonDesc(String rejReasonDesc) {
        this.rejReasonDesc = rejReasonDesc;
    }

    public String getDepotStock() {
        return depotStock;
    }

    public void setDepotStock(String depotStock) {
        this.depotStock = depotStock;
    }

    public String getOwnStock() {
        return ownStock;
    }

    public void setOwnStock(String ownStock) {
        this.ownStock = ownStock;
    }

    public String getMRP() {
        return mRP;
    }

    public void setMRP(String mRP) {
        this.mRP = mRP;
    }

    public String getMFD() {
        return mFD;
    }

    public void setMFD(String mFD) {
        this.mFD = mFD;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getPriDiscAmt() {
        return priDiscAmt;
    }

    public void setPriDiscAmt(String priDiscAmt) {
        this.priDiscAmt = priDiscAmt;
    }

    public String getPriDiscPerc() {
        return priDiscPerc;
    }

    public void setPriDiscPerc(String priDiscPerc) {
        this.priDiscPerc = priDiscPerc;
    }

    public String getTax1Amt() {
        return tax1Amt;
    }

    public void setTax1Amt(String tax1Amt) {
        this.tax1Amt = tax1Amt;
    }

    public String getTax2Amt() {
        return tax2Amt;
    }

    public void setTax2Amt(String tax2Amt) {
        this.tax2Amt = tax2Amt;
    }

    public String getTax3Amt() {
        return tax3Amt;
    }

    public void setTax3Amt(String tax3Amt) {
        this.tax3Amt = tax3Amt;
    }

    public String getTax1Percent() {
        return tax1Percent;
    }

    public void setTax1Percent(String tax1Percent) {
        this.tax1Percent = tax1Percent;
    }

    public String getTax2Percent() {
        return tax2Percent;
    }

    public void setTax2Percent(String tax2Percent) {
        this.tax2Percent = tax2Percent;
    }

    public String getTax3Percent() {
        return tax3Percent;
    }

    public void setTax3Percent(String tax3Percent) {
        this.tax3Percent = tax3Percent;
    }

}
