package com.rspl.sf.msfa.socreate;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by e10769 on 28-04-2017.
 */

public class SOItemBean implements Serializable {

    String salesOrdNo = "", soldToNo = "", soldToName = "", shipToName = "", orderTypeText = "", docDate = "", remarks = "", paymentTermsText = "";
    String currency = "", netAmount = "0", status = "", shipTo = "", orderType = "";
    String salesOrg = "", distChannel = "", division = "", incoTerm1Text = "";
    String salesItemNo = "", materialNo = "", materialText = "", quantity = "", delvQty = "", unitOfMeasure = "", customerPo = "";
    private String matCode;
    private String matDesc;
    private String uom = "";
    private String itemNo = "";
    private String soQty = "0";
    private String editAndApproveQty = "0";
    private String totalAmount;
    private String taxAmount = "0";
    private boolean isChecked;
    private boolean isRemoved;
    private String Discount = "0";
    private String UnitPrice = "0";
    private String Freight = "0";
    private String plantId = "";
    private String plantDesc = "";
    private String SONo = "";
    private boolean isButtonOnClick = false;
    private boolean isHide = false;
    private String DelvStatusID = "";
    private String StatusID = "";
    private ArrayList<SOSubItemBean> soSubItemBeen = new ArrayList<>();
    private ArrayList<SOConditionItemDetaiBean> conditionItemDetaiBeanArrayList = new ArrayList<>();
    private String[][] plantData = null;
    private String matNoAndDesc = "";
    private String Brand = "";
    private String MaterialGroupID = "";
    private String MaterialGroupDesc = "";
    private String CalTonVal = "";
    private String NetWeight = "0";
    private String NetWeightUOM = "";
    private String rejectionId = "";
    private String rejectionStatusDesc = "";
    private boolean newAdded = false;
    private boolean notnew = false;
    private String HighLevellItemNo = "";
    private String ItemCategory = "";
    private String ItemFlag = "";

    public String getItemFlag() {
        return ItemFlag;
    }

    public void setItemFlag(String itemFlag) {
        ItemFlag = itemFlag;
    }

    public String getItemCategory() {
        return ItemCategory;
    }

    public void setItemCategory(String itemCategory) {
        ItemCategory = itemCategory;
    }

    public String getHighLevellItemNo() {
        return HighLevellItemNo;
    }

    public void setHighLevellItemNo(String highLevellItemNo) {
        HighLevellItemNo = highLevellItemNo;
    }



    public String getEditAndApproveQty() {
        return editAndApproveQty;
    }

    public void setEditAndApproveQty(String editAndApproveQty) {
        this.editAndApproveQty = editAndApproveQty;
    }

    public boolean isNotnew() {
        return notnew;
    }

    public void setNotnew(boolean notnew) {
        this.notnew = notnew;
    }

    public boolean isNewAdded() {
        return newAdded;
    }

    public void setNewAdded(boolean newAdded) {
        this.newAdded = newAdded;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public void setRemoved(boolean removed) {
        isRemoved = removed;
    }

    public String getRejectionId() {
        return rejectionId;
    }

    public void setRejectionId(String rejectionId) {
        this.rejectionId = rejectionId;
    }

    public String getRejectionStatusDesc() {
        return rejectionStatusDesc;
    }

    public void setRejectionStatusDesc(String rejectionStatusDesc) {
        this.rejectionStatusDesc = rejectionStatusDesc;
    }

    public String getStatusID() {
        return StatusID;
    }

    public void setStatusID(String statusID) {
        StatusID = statusID;
    }

    public String getAlternateWeight() {
        return AlternateWeight;
    }

    public void setAlternateWeight(String alternateWeight) {
        AlternateWeight = alternateWeight;
    }

    private String AlternateWeight = "0";

    public String getSearchField() {
        return searchField;
    }

    public String getMaterialGroupID() {
        return MaterialGroupID;
    }

    public void setMaterialGroupID(String materialGroupID) {
        MaterialGroupID = materialGroupID;
    }

    public String getMaterialGroupDesc() {
        return MaterialGroupDesc;
    }

    public void setMaterialGroupDesc(String materialGroupDesc) {
        MaterialGroupDesc = materialGroupDesc;
    }

    public String getCalTonVal() {
        return CalTonVal;
    }

    public void setCalTonVal(String calTonVal) {
        CalTonVal = calTonVal;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

    private String searchField = "";

    public String getLandingPrice() {
        return landingPrice;
    }

    public void setLandingPrice(String landingPrice) {
        this.landingPrice = landingPrice;
    }

    private String landingPrice = "";

    public String getSetSONo() {
        return setSONo;
    }

    public boolean isDecimalCheck() {
        return decimalCheck;
    }

    public void setDecimalCheck(boolean decimalCheck) {
        this.decimalCheck = decimalCheck;
    }

    public boolean decimalCheck = false;

    public void setSetSONo(String setSONo) {
        this.setSONo = setSONo;
    }

    private String setSONo = "";

    public String getCustomerPo() {
        return customerPo;
    }

    public void setCustomerPo(String customerPo) {
        this.customerPo = customerPo;
    }

    public String getSalesItemNo() {
        return salesItemNo;
    }

    public void setSalesItemNo(String salesItemNo) {
        this.salesItemNo = salesItemNo;
    }

    public String getMaterialNo() {
        return materialNo;
    }

    public void setMaterialNo(String materialNo) {
        this.materialNo = materialNo;
    }

    public String getMaterialText() {
        return materialText;
    }

    public void setMaterialText(String materialText) {
        this.materialText = materialText;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDelvQty() {
        return delvQty;
    }

    public void setDelvQty(String delvQty) {
        this.delvQty = delvQty;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public String getIncoTerm1Text() {
        return incoTerm1Text;
    }

    public void setIncoTerm1Text(String incoTerm1Text) {
        this.incoTerm1Text = incoTerm1Text;
    }

    public String getSalesOrg() {
        return salesOrg;
    }

    public void setSalesOrg(String salesOrg) {
        this.salesOrg = salesOrg;
    }

    public String getDistChannel() {
        return distChannel;
    }

    public void setDistChannel(String distChannel) {
        this.distChannel = distChannel;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getShipTo() {
        return shipTo;
    }

    public void setShipTo(String shipTo) {
        this.shipTo = shipTo;
    }
    public void setSoldToName(String soldToName) {
        this.soldToName = soldToName;
    }

    public String getShipToName() {
        return shipToName;
    }

    public void setShipToName(String shipToName) {
        this.shipToName = shipToName;
    }
    public void setDocDate(String docDate) {
        this.docDate = docDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getSalesOrdNo() {
        return salesOrdNo;
    }

    public void setSalesOrdNo(String salesOrdNo) {
        this.salesOrdNo = salesOrdNo;
    }

    public String getSoldToNo() {
        return soldToNo;
    }

    public void setSoldToNo(String soldToNo) {
        this.soldToNo = soldToNo;
    }

    public String getSoldToName() {
        return soldToName;
    }


    public String getOrderTypeText() {
        return orderTypeText;
    }

    public void setOrderTypeText(String orderTypeText) {
        this.orderTypeText = orderTypeText;
    }

    public String getDocDate() {
        return docDate;
    }



    public String getPaymentTermsText() {
        return paymentTermsText;
    }

    public void setPaymentTermsText(String paymentTermsText) {
        this.paymentTermsText = paymentTermsText;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(String netAmount) {
        this.netAmount = netAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }

    public String getMatDesc() {
        return matDesc;
    }

    public void setMatDesc(String matDesc) {
        this.matDesc = matDesc;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getSoQty() {
        return soQty;
    }

    public void setSoQty(String soQty) {
        this.soQty = soQty;
    }


    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        UnitPrice = unitPrice;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getFreight() {
        return Freight;
    }

    public void setFreight(String freight) {
        Freight = freight;
    }

    public ArrayList<SOSubItemBean> getSoSubItemBeen() {
        return soSubItemBeen;
    }

    public void setSoSubItemBeen(ArrayList<SOSubItemBean> soSubItemBeen) {
        this.soSubItemBeen = soSubItemBeen;
    }

    public String getPlantId() {
        return plantId;
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId;
    }

    public String getPlantDesc() {
        return plantDesc;
    }

    public void setPlantDesc(String plantDesc) {
        this.plantDesc = plantDesc;
    }

    public String[][] getPlantData() {
        return plantData;
    }

    public void setPlantData(String[][] plantData) {
        this.plantData = plantData;
    }

    public ArrayList<SOConditionItemDetaiBean> getConditionItemDetaiBeanArrayList() {
        return conditionItemDetaiBeanArrayList;
    }

    public void setConditionItemDetaiBeanArrayList(ArrayList<SOConditionItemDetaiBean> conditionItemDetaiBeanArrayList) {
        this.conditionItemDetaiBeanArrayList = conditionItemDetaiBeanArrayList;
    }

    public boolean isButtonOnClick() {
        return isButtonOnClick;
    }

    public void setButtonOnClick(boolean buttonOnClick) {
        isButtonOnClick = buttonOnClick;
    }

    public String getSONo() {
        return SONo;
    }

    public void setSONo(String SONo) {
        this.SONo = SONo;
    }

    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        isHide = hide;
    }

    public String getDelvStatusID() {
        return DelvStatusID;
    }

    public void setDelvStatusID(String delvStatusID) {
        DelvStatusID = delvStatusID;
    }

    public String getMatNoAndDesc() {
        return matNoAndDesc;
    }

    public void setMatNoAndDesc(String matNoAndDesc) {
        this.matNoAndDesc = matNoAndDesc;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getNetWeight() {
        return NetWeight;
    }

    public void setNetWeight(String netWeight) {
        NetWeight = netWeight;
    }

    public String getNetWeightUOM() {
        return NetWeightUOM;
    }

    public void setNetWeightUOM(String netWeightUOM) {
        NetWeightUOM = netWeightUOM;
    }
}
