package com.rspl.sf.msfa.reports.invoicelist;



import com.rspl.sf.msfa.reports.invoicelist.invoiceDetails.InvConItemBean;
import com.rspl.sf.msfa.reports.invoicelist.invoiceDetails.InvoiceConditionsBean;
import com.rspl.sf.msfa.reports.invoicelist.invoiceDetails.InvoiceItemBean;
import com.rspl.sf.msfa.reports.invoicelist.invoiceDetails.InvoicePartnerFunctionsBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by e10526 on 4/28/2017.
 */

public class InvoiceListBean implements Serializable {
    private String InvoiceNo = "";
    private String NetAmount = "";
    private String InvoiceDate = "";
    private String InvoiceStatus = "";
    private String Currency = "";
    private String Material = "";
    private String MaterialDesc = "";
    private String InvoiceQty = "";
    private String UOM = "";
    private String MaterialGroup = "";
    private String MatGroupDesc = "";
    private String CustomerName = "", CustomerNo;
    private String InvoiceType = "";
    private String InvoiceTypDesc = "";
    private String PaymentTerm = "";
    private String PaymentTermDesc = "";
    private String transporterName = "";
    private String Incoterm1Desc = "";
    private String IncoTerm2 = "";
    private String UnitPrice = "", GRStatus, PaymentStatus;
    private ArrayList<InvoiceItemBean> invoiceItemBeanArrayList = new ArrayList<>();
    private ArrayList<InvoicePartnerFunctionsBean> invoicePartnerFunctionsArrayList = new ArrayList<>();
    private ArrayList<InvoiceConditionsBean> invoiceConditionsArrayList = new ArrayList<>();
    private ArrayList<InvConItemBean> invConditionItemDetaiBeanArrayList = new ArrayList<>();
    private Boolean DetailEnabled = false;
    private String ItemNo = "";
    private String NetPrice = "";
    private String dueDateStatus="";
    private String MatCodeAndDesc="";
    String deviceStatus = "";
    String invQty="";
    String totAmount="";

    public String getIncoterm1Desc() {
        return Incoterm1Desc;
    }

    public void setIncoterm1Desc(String incoterm1Desc) {
        Incoterm1Desc = incoterm1Desc;
    }

    public String getIncoTerm2() {
        return IncoTerm2;
    }

    public void setIncoTerm2(String incoTerm2) {
        IncoTerm2 = incoTerm2;
    }

    public Boolean getDetailEnabled() {
        return DetailEnabled;
    }

    public void setDetailEnabled(Boolean detailEnabled) {
        DetailEnabled = detailEnabled;
    }

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
        return InvoiceQty;
    }

    public void setQuantity(String quantity) {
        InvoiceQty = quantity;
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

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
    }

    public String getNetPrice() {
        return NetPrice;
    }

    public void setNetPrice(String netPrice) {
        NetPrice = netPrice;
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

    public String getPaymentTerm() {
        return PaymentTerm;
    }

    public void setPaymentTerm(String paymentTerm) {
        PaymentTerm = paymentTerm;
    }

    public String getPaymentTermDesc() {
        return PaymentTermDesc;
    }

    public void setPaymentTermDesc(String paymentTermDesc) {
        PaymentTermDesc = paymentTermDesc;
    }

    public String getTransporterName() {
        return transporterName;
    }

    public void setTransporterName(String transporterName) {
        this.transporterName = transporterName;
    }

    public String getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        UnitPrice = unitPrice;
    }

    public ArrayList<InvoiceItemBean> getInvoiceItemBeanArrayList() {
        return invoiceItemBeanArrayList;
    }

    public void setInvoiceItemBeanArrayList(ArrayList<InvoiceItemBean> invoiceItemBeanArrayList) {
        this.invoiceItemBeanArrayList = invoiceItemBeanArrayList;
    }

    public String getGRStatus() {
        return GRStatus;
    }

    public void setGRStatus(String GRStatus) {
        this.GRStatus = GRStatus;
    }

    public String getPaymentStatus() {
        return PaymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        PaymentStatus = paymentStatus;
    }

    public String getCustomerNo() {
        return CustomerNo;
    }

    public void setCustomerNo(String customerNo) {
        CustomerNo = customerNo;
    }


    public ArrayList<InvoicePartnerFunctionsBean> getInvoicePartnerFunctionsArrayList() {
        return invoicePartnerFunctionsArrayList;
    }

    public void setInvoicePartnerFunctionsArrayList(ArrayList<InvoicePartnerFunctionsBean> invoicePartnerFunctionsArrayList) {
        this.invoicePartnerFunctionsArrayList = invoicePartnerFunctionsArrayList;
    }

    public ArrayList<InvoiceConditionsBean> getInvoiceConditionsArrayList() {
        return invoiceConditionsArrayList;
    }

    public void setInvoiceConditionsArrayList(ArrayList<InvoiceConditionsBean> invoiceConditionsArrayList) {
        this.invoiceConditionsArrayList = invoiceConditionsArrayList;
    }


    public ArrayList<InvConItemBean> getInvConditionItemDetaiBeanArrayList() {
        return invConditionItemDetaiBeanArrayList;
    }

    public void setInvConditionItemDetaiBeanArrayList(ArrayList<InvConItemBean> invConditionItemDetaiBeanArrayList) {
        this.invConditionItemDetaiBeanArrayList = invConditionItemDetaiBeanArrayList;
    }

    public String getDueDateStatus() {
        return dueDateStatus;
    }

    public void setDueDateStatus(String dueDateStatus) {
        this.dueDateStatus = dueDateStatus;
    }

    public String getMatCodeAndDesc() {
        return MatCodeAndDesc;
    }

    public void setMatCodeAndDesc(String matCodeAndDesc) {
        MatCodeAndDesc = matCodeAndDesc;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getTotAmount() {
        return totAmount;
    }

    public void setTotAmount(String totAmount) {
        this.totAmount = totAmount;
    }

    public String getInvQty() {
        return invQty;
    }

    public void setInvQty(String invQty) {
        this.invQty = invQty;
    }


}
