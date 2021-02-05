package com.rspl.sf.msfa.reports.invoicelist.invoiceDetails;

import java.io.Serializable;

/**
 * Created by e10769 on 04-07-2017.
 */

public class InvoiceItemBean implements Serializable {
    private String ItemNo = "";
    private String InvoiceNo = "";
    private String NetAmount = "";
    private String InvoiceDate = "";
    private String InvoiceStatus = "";
    private String Currency = "";
    private String Material = "";
    private String MaterialDesc = "";
    private String Quantity = "";
    private String UOM = "";
    private String MaterialGroup = "";
    private String MatGroupDesc = "";
    private String CustomerName = "";
    private String InvoiceType = "";
    private String InvoiceTypDesc = "";
    private String InvoiceMaterialDescAndNo = "";
    private String Tax = "";
    private String TotalAmount = "";
    private String GrossAmount = "";
    private String UnitPrice = "", ActualInvQty, MaterialNo, ConditionAmt, ConditionValue;

    public String getInvoiceNo() {
        return InvoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        InvoiceNo = invoiceNo;
    }

    public String getNetAmount() {
        return NetAmount;
    }

    public void setNetAmount(String netAmount) {
        NetAmount = netAmount;
    }

    public String getInvoiceDate() {
        return InvoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        InvoiceDate = invoiceDate;
    }

    public String getInvoiceStatus() {
        return InvoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus) {
        InvoiceStatus = invoiceStatus;
    }

    public String getInvoiceMaterialDescAndNo() {
        return InvoiceMaterialDescAndNo;
    }

    public void setInvoiceMaterialDescAndNo(String invoiceMaterialDescAndNo) {
        InvoiceMaterialDescAndNo = invoiceMaterialDescAndNo;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getMaterial() {
        return Material;
    }

    public void setMaterial(String material) {
        Material = material;
    }

    public String getMaterialDesc() {
        return MaterialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        MaterialDesc = materialDesc;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public String getMaterialGroup() {
        return MaterialGroup;
    }

    public void setMaterialGroup(String materialGroup) {
        MaterialGroup = materialGroup;
    }

    public String getMatGroupDesc() {
        return MatGroupDesc;
    }

    public void setMatGroupDesc(String matGroupDesc) {
        MatGroupDesc = matGroupDesc;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getInvoiceType() {
        return InvoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        InvoiceType = invoiceType;
    }

    public String getInvoiceTypDesc() {
        return InvoiceTypDesc;
    }

    public void setInvoiceTypDesc(String invoiceTypDesc) {
        InvoiceTypDesc = invoiceTypDesc;
    }

    public String getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        UnitPrice = unitPrice;
    }

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
    }

    public String getActualInvQty() {
        return ActualInvQty;
    }

    public void setActualInvQty(String actualInvQty) {
        ActualInvQty = actualInvQty;
    }

    public String getMaterialNo() {
        return MaterialNo;
    }

    public void setMaterialNo(String materialNo) {
        MaterialNo = materialNo;
    }

    public String getConditionAmt() {
        return ConditionAmt;
    }

    public void setConditionAmt(String conditionAmt) {
        ConditionAmt = conditionAmt;
    }

    public String getConditionValue() {
        return ConditionValue;
    }

    public String getTax() {
        return Tax;
    }

    public void setTax(String tax) {
        Tax = tax;
    }

    public String getGrossAmount() {
        return GrossAmount;
    }

    public void setGrossAmount(String grossAmount) {
        GrossAmount = grossAmount;
    }

    public String getTotalAmount() {
        return TotalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        TotalAmount = totalAmount;
    }
}
