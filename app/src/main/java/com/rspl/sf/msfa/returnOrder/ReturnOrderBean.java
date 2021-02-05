package com.rspl.sf.msfa.returnOrder;



import com.rspl.sf.msfa.returnOrder.returnDetail.ReturnOrderItemBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by e10860 on 12/28/2017.
 */

public class ReturnOrderBean implements Serializable {

    private String retOrdNo;

    private String itemNo;

    private String loginID;

    private String orderDate;

    private String orderType;

    private String orderTypeDesc;

    private String customerNo;

    private String customerName;

    private String material;

    private String materialDesc="";

    private String materialGroup;

    private String matGroupDesc;

    private String quantity="";

    private String invoiceQty="";

    private String uOM="";

    private String batch;

    private String invoiceNo="";

    private String invoiceItemNo;

    private String orderReasonID;

    private String orderReasonDesc;

    private String statusID="";

    private String statusDesc;

    private String gRStatusID="";

    private String gRStatusDesc;

    private String plant;

    private String plantDesc;

    private String currency="";

    private String unitPrice;

    private String netAmount="";

    private String priDiscPerc;

    private String mRP;

    private Object mFD;

    private Object expiryDate;

    private String priDiscAmt;

    private String tax1Amt;

    private String tax2Amt;

    private String tax3Amt;

    private String tax1Percent;

    private String tax2Percent;

    private String tax3Percent;
    private String ShipToPartyName="";
    private String SalesAreaDesc="";
    private String SalesArea="";
    private String SalesOffDesc="";
    private String SalesOff="";
    private String Address1="";
    private String District="";
    private String PostalCode="";
    private String City="";
    private String State="";
    private String StateDesc="";
    private String CountryCode="";

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    private String Address="";

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getPostalCode() {
        return PostalCode;
    }

    public void setPostalCode(String postalCode) {
        PostalCode = postalCode;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getStateDesc() {
        return StateDesc;
    }

    public void setStateDesc(String stateDesc) {
        StateDesc = stateDesc;
    }

    public String getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(String countryCode) {
        CountryCode = countryCode;
    }

    public String getCountryDesc() {
        return CountryDesc;
    }

    public void setCountryDesc(String countryDesc) {
        CountryDesc = countryDesc;
    }

    private String CountryDesc="";

    public String getShipToParty() {
        return ShipToParty;
    }

    public void setShipToParty(String shipToParty) {
        ShipToParty = shipToParty;
    }

    private String ShipToParty="";

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

    public void setmFD(Object mFD) {
        this.mFD = mFD;
    }

    public String getShipToPartyName() {
        return ShipToPartyName;
    }

    public void setShipToPartyName(String shipToPartyName) {
        ShipToPartyName = shipToPartyName;
    }

    public String getSalesAreaDesc() {
        return SalesAreaDesc;
    }

    public void setSalesAreaDesc(String salesAreaDesc) {
        SalesAreaDesc = salesAreaDesc;
    }

    public String getSalesArea() {
        return SalesArea;
    }

    public void setSalesArea(String salesArea) {
        SalesArea = salesArea;
    }

    public String getSalesOffDesc() {
        return SalesOffDesc;
    }

    public void setSalesOffDesc(String salesOffDesc) {
        SalesOffDesc = salesOffDesc;
    }

    public String getSalesOff() {
        return SalesOff;
    }

    public void setSalesOff(String salesOff) {
        SalesOff = salesOff;
    }

    public String getAddress1() {
        return Address1;
    }

    public void setAddress1(String address1) {
        Address1 = address1;
    }

    public String getAddress2() {
        return Address2;
    }

    public void setAddress2(String address2) {
        Address2 = address2;
    }

    public String getAddress3() {
        return Address3;
    }

    public void setAddress3(String address3) {
        Address3 = address3;
    }

    public String getAddress4() {
        return Address4;
    }

    public void setAddress4(String address4) {
        Address4 = address4;
    }

    private String Address2="";
    private String Address3="";
    private String Address4="";

    public String getRetOrdNo() {
        return retOrdNo;
    }

    public void setRetOrdNo(String retOrdNo) {
        this.retOrdNo = retOrdNo;
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

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderTypeDesc() {
        return orderTypeDesc;
    }

    public void setOrderTypeDesc(String orderTypeDesc) {
        this.orderTypeDesc = orderTypeDesc;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public String getOrderReasonID() {
        return orderReasonID;
    }

    public void setOrderReasonID(String orderReasonID) {
        this.orderReasonID = orderReasonID;
    }

    public String getOrderReasonDesc() {
        return orderReasonDesc;
    }

    public void setOrderReasonDesc(String orderReasonDesc) {
        this.orderReasonDesc = orderReasonDesc;
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

    public String getPriDiscPerc() {
        return priDiscPerc;
    }

    public void setPriDiscPerc(String priDiscPerc) {
        this.priDiscPerc = priDiscPerc;
    }

    public String getMRP() {
        return mRP;
    }

    public void setMRP(String mRP) {
        this.mRP = mRP;
    }

    public Object getMFD() {
        return mFD;
    }

    public void setMFD(Object mFD) {
        this.mFD = mFD;
    }

    public Object getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Object expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getPriDiscAmt() {
        return priDiscAmt;
    }

    public void setPriDiscAmt(String priDiscAmt) {
        this.priDiscAmt = priDiscAmt;
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

    public ArrayList<ReturnOrderItemBean> getRoItemList() {
        return roItemList;
    }

    public void setRoItemList(ArrayList<ReturnOrderItemBean> roItemList) {
        this.roItemList = roItemList;
    }

    private ArrayList<ReturnOrderItemBean> roItemList=null;

}
