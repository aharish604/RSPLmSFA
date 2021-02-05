package com.rspl.sf.msfa.reports;

import java.io.Serializable;

/**
 *
 * Created by ${e10604} on ${27/4/2016}.
 *
 */
public class OutstandingBean implements Serializable{

    String invoiceNo;
    String invoiceDate;
    String invoiceAmount;
    String invoiceStatus;
    String matDesc;
    String matCode;
    String collectionAmount;
    private String materialNumber="";
    private String netAmount="";

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    private String materialDescription="";
    private String quantity="";
    private String taxAmount="";


    public String getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(String netAmount) {
        this.netAmount = netAmount;
    }

    public String getMaterialNumber() {
        return materialNumber;
    }

    public void setMaterialNumber(String materialNumber) {
        this.materialNumber = materialNumber;
    }

    public String getMaterialDescription() {
        return materialDescription;
    }

    public void setMaterialDescription(String materialDescription) {
        this.materialDescription = materialDescription;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getBucket1() {
        return Bucket1;
    }

    public void setBucket1(String bucket1) {
        Bucket1 = bucket1;
    }

    public String getBucket2() {
        return Bucket2;
    }

    public void setBucket2(String bucket2) {
        Bucket2 = bucket2;
    }

    public String getBucket3() {
        return Bucket3;
    }

    public void setBucket3(String bucket3) {
        Bucket3 = bucket3;
    }

    public String getBucket4() {
        return Bucket4;
    }

    public void setBucket4(String bucket4) {
        Bucket4 = bucket4;
    }

    public String getBucket5() {
        return Bucket5;
    }

    public void setBucket5(String bucket5) {
        Bucket5 = bucket5;
    }

    public String getBucket6() {
        return Bucket6;
    }

    public void setBucket6(String bucket6) {
        Bucket6 = bucket6;
    }

    public String getBucket7() {
        return Bucket7;
    }

    public void setBucket7(String bucket7) {
        Bucket7 = bucket7;
    }

    public String getBucket8() {
        return Bucket8;
    }

    public void setBucket8(String bucket8) {
        Bucket8 = bucket8;
    }

    public String getBucket9() {
        return Bucket9;
    }

    public void setBucket9(String bucket9) {
        Bucket9 = bucket9;
    }

    public String getBucket10() {
        return Bucket10;
    }

    public void setBucket10(String bucket10) {
        Bucket10 = bucket10;
    }

    String Bucket1 = "";
    String Bucket2 = "";
    String Bucket3 = "";
    String Bucket4 = "";
    String Bucket5 = "";
    String Bucket6 = "";
    String Bucket7 = "";
    String Bucket8 = "";
    String Bucket9 = "";
    String Bucket10 = "";


    public String getDevCollAmount() {
        return devCollAmount;
    }

    public void setDevCollAmount(String devCollAmount) {
        this.devCollAmount = devCollAmount;
    }

    String devCollAmount = "";

    public Boolean getIsDetailEnabled() {
        return isDetailEnabled;
    }

    public void setIsDetailEnabled(Boolean isDetailEnabled) {
        this.isDetailEnabled = isDetailEnabled;
    }

    Boolean isDetailEnabled;
    public String getCollectionAmount() {
        return collectionAmount;
    }

    public void setCollectionAmount(String collectionAmount) {
        this.collectionAmount = collectionAmount;
    }

    public String getCpGuid() {
        return cpGuid;
    }

    public void setCpGuid(String cpGuid) {
        this.cpGuid = cpGuid;
    }

    String cpGuid ="";

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    String deviceNo = "";

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    String deviceStatus = "";

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    String itemNo = "";

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    String uom="";

    public String getInvoiceGuid() {
        return invoiceGuid;
    }

    public void setInvoiceGuid(String invoiceGuid) {
        this.invoiceGuid = invoiceGuid;
    }

    String invoiceGuid;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    String currency;


    public String getMatDesc() {
        return matDesc;
    }

    public void setMatDesc(String matDesc) {
        this.matDesc = matDesc;
    }

    public String getInvQty() {
        return invQty;
    }

    public void setInvQty(String invQty) {
        this.invQty = invQty;
    }



    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }

    String invQty;

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }


}
