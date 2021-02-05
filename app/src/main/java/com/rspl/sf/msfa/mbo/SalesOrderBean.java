package com.rspl.sf.msfa.mbo;

import com.rspl.sf.msfa.soapproval.SalesOrderConditionsBean;
import com.rspl.sf.msfa.solist.SOTaskHistoryBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by e10762 on 16-02-2017.
 */

public class SalesOrderBean implements Serializable {
    String ItemCat = "";
    String ItemCatDesc = "";
    String DiscountPer = "0";
    String DepotStock = "0";
    String OwnStock = "0";
    String DelvQty = "0";
    String OpenQty = "0";
    String LoginID = "";
    String StorLoc = "";
    String StorLocDesc = "";
    String MRP = "";
    String RLPrice = "";
    String UnrestrictedQty = "";
    String Uom = "";
    String SerialNoTo = "";
    private String CPStockItemGUID = "";
    private String MaterialDesc = "";
    private String MaterialNo = "";
    private String QAQty = "";
    private String BlockedQty = "";
    private String StockValue = "";
    private String LandingPrice = "";
    private String OrderMaterialGroupID = "";
    private String OrderMaterialGroupDesc = "";
    private String OrderNo = "";
    private String OrderDate = "";
    private String SSROGUID = "";
    private String NetAmount = "";
    private String StatusID = "";
    private String StatusDesc = "";
    private Boolean IsDetailEnabled = false;
    private String sItemNo = "";
    private String deviceNo = "";
    private String Brand = "";
    private String ProductCategoryID = "";
    private String Remarks = "";
    private String DelvStatus = "";
    private String DelvStatusDesc = "";
    private String ShippingTypeDesc = "";
    private String ShippingTypeID = "";
    private String MeansOfTranstypDesc = "";
    private String ShipToParty = "";
    private String PaytermDesc = "";
    private String Incoterm1Desc = "";
    private String SalesAreaDesc = "";
    private String Incoterm2 = "";
    private String SoldTo = "";
    private String SoldToName = "";
    private String ShipToName = "";
    private String ShipTo = "";
    private String Plant = "";
    private String PlantDesc = "";
    private String ShippingPoint = "";
    private String ShippingPointDesc = "";
    private String PODate = "";
    private String PODate1 = "";
    private String PaymentTerm = "";
    private String PaymentTermDesc = "";
    private String IncoTerm1 = "";
    private String TransportName = "";
    private String TransportNameID = "";
    private String PONo = "";
    private String address = "";
    private String SalesArea = "";
    private String SalesOfficeId = "";
    private String SaleOffDesc = "";
    private String UnitPrice = "0.00";
    private String OrderType = "";
    private String OrderTypeDesc = "";
    private String TotalAmt = "0";
    private String TaxAmt = "0";
    private String FreightAmt = "0";
    private String DiscountAmt = "0";
    private String MatFrgtGrpDesc = "";
    private String MatFrgtGrp = "";
    private String SalesDistrict = "";
    private String SalesDistrictDesc = "";
    private String MeansOfTranstyp = "";
    private String ForwardingAgent = "";
    private String ForwardingAgentName = "";
    private Boolean isSelected = false;
    private Boolean isDisplayed = false;
    private int itemNo = 0;
    private String CPGUID = "";
    private String SerialNoFrom = "";
    private String Currency = "";
    private String Batch = "";
    private String returnQty = "";
    private String returnDesc = "";
    private String returnMrp = "";
    private String returnBatchNumber = "";
    private String returnReason = "";
    private Boolean isItemToReturn = false;
    private String MFD = "";
    private String CrsSksGroup;
    private String Route = "";
    private String RouteDesc = "";
    private String SplProcessing = "";
    private String SplProcessingDesc = "";

    private String SalesGroup = "";
    private String SalesGrpDesc = "";
    private String SalesOffDesc = "";
    private String SalesOffice = "";
    private ArrayList<SalesOrderConditionsBean> salesOrderConditionsBeanArrayList = new ArrayList<>();
    private String CustomerNo = "";
    private ArrayList<SOTaskHistoryBean> soTaskHistoryBeanArrayList = new ArrayList<>();
    private String mStrTotalWeight = "";
    private String mStrWeightUOM = "";
    private String mSteTotalQtyUOM = "";
    private String matNoAndDesc = "";
    private String itemFlag = "";
    private String HighLevellItemNo = "";

    public String getHighLevellItemNo() {
        return HighLevellItemNo;
    }

    public void setHighLevellItemNo(String highLevellItemNo) {
        HighLevellItemNo = highLevellItemNo;
    }

    public String getItemFlag() {
        return itemFlag;
    }

    public void setItemFlag(String itemFlag) {
        this.itemFlag = itemFlag;
    }

    public String getSalesGroup() {
        return SalesGroup;
    }

    public void setSalesGroup(String salesGroup) {
        SalesGroup = salesGroup;
    }

    public String getSalesGrpDesc() {
        return SalesGrpDesc;
    }

    public void setSalesGrpDesc(String salesGrpDesc) {
        SalesGrpDesc = salesGrpDesc;
    }

    public String getSalesOffDesc() {
        return SalesOffDesc;
    }

    public void setSalesOffDesc(String salesOffDesc) {
        SalesOffDesc = salesOffDesc;
    }

    public String getSalesOffice() {
        return SalesOffice;
    }

    public void setSalesOffice(String salesOffice) {
        SalesOffice = salesOffice;
    }

    public ArrayList<SalesOrderConditionsBean> getSalesOrderConditionsBeanArrayList() {
        return salesOrderConditionsBeanArrayList;
    }

    public void setSalesOrderConditionsBeanArrayList(ArrayList<SalesOrderConditionsBean> salesOrderConditionsBeanArrayList) {
        this.salesOrderConditionsBeanArrayList = salesOrderConditionsBeanArrayList;
    }

    public String getCustomerNo() {
        return CustomerNo;
    }

    public void setCustomerNo(String customerNo) {
        CustomerNo = customerNo;
    }

    public String getCPStockItemGUID() {
        return CPStockItemGUID;
    }

    public void setCPStockItemGUID(String CPStockItemGUID) {
        this.CPStockItemGUID = CPStockItemGUID;
    }

    public String getMaterialDesc() {
        return MaterialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        MaterialDesc = materialDesc;
    }

    public String getMaterialNo() {
        return MaterialNo;
    }

    public void setMaterialNo(String materialNo) {
        MaterialNo = materialNo;
    }

    public String getQAQty() {
        return QAQty;
    }

    public void setQAQty(String QAQty) {
        this.QAQty = QAQty;
    }

    public String getBlockedQty() {
        return BlockedQty;
    }

    public void setBlockedQty(String blockedQty) {
        BlockedQty = blockedQty;
    }

    public String getStockValue() {
        return StockValue;
    }

    public void setStockValue(String stockValue) {
        StockValue = stockValue;
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

    public String getOrderMaterialGroupDesc() {
        return OrderMaterialGroupDesc;
    }

    public void setOrderMaterialGroupDesc(String orderMaterialGroupDesc) {
        OrderMaterialGroupDesc = orderMaterialGroupDesc;
    }

    public String getOrderNo() {
        return OrderNo;
    }

    public void setOrderNo(String orderNo) {
        OrderNo = orderNo;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public String getSSROGUID() {
        return SSROGUID;
    }

    public void setSSROGUID(String SSROGUID) {
        this.SSROGUID = SSROGUID;
    }

    public String getNetAmount() {
        return NetAmount;
    }

    public void setNetAmount(String netAmount) {
        NetAmount = netAmount;
    }

    public String getStatusID() {
        return StatusID;
    }

    public void setStatusID(String statusID) {
        StatusID = statusID;
    }

    public String getStatusDesc() {
        return StatusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        StatusDesc = statusDesc;
    }

    public Boolean getDetailEnabled() {
        return IsDetailEnabled;
    }

    public void setDetailEnabled(Boolean detailEnabled) {
        IsDetailEnabled = detailEnabled;
    }

    public String getsItemNo() {
        return sItemNo;
    }

    public void setsItemNo(String sItemNo) {
        this.sItemNo = sItemNo;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getProductCategoryID() {
        return ProductCategoryID;
    }

    public void setProductCategoryID(String productCategoryID) {
        ProductCategoryID = productCategoryID;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getDelvStatus() {
        return DelvStatus;
    }

    public void setDelvStatus(String delvStatus) {
        DelvStatus = delvStatus;
    }

    public String getDelvStatusDesc() {
        return DelvStatusDesc;
    }

    public void setDelvStatusDesc(String delvStatusDesc) {
        DelvStatusDesc = delvStatusDesc;
    }

    public String getShippingTypeDesc() {
        return ShippingTypeDesc;
    }

    public void setShippingTypeDesc(String shippingTypeDesc) {
        ShippingTypeDesc = shippingTypeDesc;
    }

    public String getShippingTypeID() {
        return ShippingTypeID;
    }

    public void setShippingTypeID(String shippingTypeID) {
        ShippingTypeID = shippingTypeID;
    }

    public String getMeansOfTranstypDesc() {
        return MeansOfTranstypDesc;
    }

    public void setMeansOfTranstypDesc(String meansOfTranstypDesc) {
        MeansOfTranstypDesc = meansOfTranstypDesc;
    }

    public String getShipToParty() {
        return ShipToParty;
    }

    public void setShipToParty(String shipToParty) {
        ShipToParty = shipToParty;
    }

    public String getPaytermDesc() {
        return PaytermDesc;
    }

    public void setPaytermDesc(String paytermDesc) {
        PaytermDesc = paytermDesc;
    }

    public String getIncoterm1Desc() {
        return Incoterm1Desc;
    }

    public void setIncoterm1Desc(String incoterm1Desc) {
        Incoterm1Desc = incoterm1Desc;
    }

    public String getSalesAreaDesc() {
        return SalesAreaDesc;
    }

    public void setSalesAreaDesc(String salesAreaDesc) {
        SalesAreaDesc = salesAreaDesc;
    }

    public String getIncoterm2() {
        return Incoterm2;
    }

    public void setIncoterm2(String incoterm2) {
        Incoterm2 = incoterm2;
    }

    public String getSoldTo() {
        return SoldTo;
    }

    public void setSoldTo(String soldTo) {
        SoldTo = soldTo;
    }

    public String getSoldToName() {
        return SoldToName;
    }

    public void setSoldToName(String soldToName) {
        SoldToName = soldToName;
    }

    public String getShipToName() {
        return ShipToName;
    }

    public void setShipToName(String shipToName) {
        ShipToName = shipToName;
    }

    public String getShipTo() {
        return ShipTo;
    }

    public void setShipTo(String shipTo) {
        ShipTo = shipTo;
    }

    public String getPlant() {
        return Plant;
    }

    public void setPlant(String plant) {
        Plant = plant;
    }

    public String getPlantDesc() {
        return PlantDesc;
    }

    public void setPlantDesc(String plantDesc) {
        PlantDesc = plantDesc;
    }

    public String getShippingPoint() {
        return ShippingPoint;
    }

    public void setShippingPoint(String shippingPoint) {
        ShippingPoint = shippingPoint;
    }

    public String getShippingPointDesc() {
        return ShippingPointDesc;
    }

    public void setShippingPointDesc(String shippingPointDesc) {
        ShippingPointDesc = shippingPointDesc;
    }

    public String getPODate() {
        return PODate;
    }

    public void setPODate(String PODate) {
        this.PODate = PODate;
    }

    public String getPODate1() {
        return PODate1;
    }

    public void setPODate1(String PODate1) {
        this.PODate1 = PODate1;
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

    public String getIncoTerm1() {
        return IncoTerm1;
    }

    public void setIncoTerm1(String incoTerm1) {
        IncoTerm1 = incoTerm1;
    }

    public String getTransportName() {
        return TransportName;
    }

    public void setTransportName(String transportName) {
        TransportName = transportName;
    }

    public String getTransportNameID() {
        return TransportNameID;
    }

    public void setTransportNameID(String transportNameID) {
        TransportNameID = transportNameID;
    }

    public String getPONo() {
        return PONo;
    }

    public void setPONo(String PONo) {
        this.PONo = PONo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSalesArea() {
        return SalesArea;
    }

    public void setSalesArea(String salesArea) {
        SalesArea = salesArea;
    }

    public String getSalesOfficeId() {
        return SalesOfficeId;
    }

    public void setSalesOfficeId(String salesOfficeId) {
        SalesOfficeId = salesOfficeId;
    }

    public String getSaleOffDesc() {
        return SaleOffDesc;
    }

    public void setSaleOffDesc(String saleOffDesc) {
        SaleOffDesc = saleOffDesc;
    }

    public String getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        UnitPrice = unitPrice;
    }

    public String getOrderType() {
        return OrderType;
    }

    public void setOrderType(String orderType) {
        OrderType = orderType;
    }

    public String getOrderTypeDesc() {
        return OrderTypeDesc;
    }

    public void setOrderTypeDesc(String orderTypeDesc) {
        OrderTypeDesc = orderTypeDesc;
    }

    public String getTotalAmt() {
        return TotalAmt;
    }

    public void setTotalAmt(String totalAmt) {
        TotalAmt = totalAmt;
    }

    public String getTaxAmt() {
        return TaxAmt;
    }

    public void setTaxAmt(String taxAmt) {
        TaxAmt = taxAmt;
    }

    public String getFreightAmt() {
        return FreightAmt;
    }

    public void setFreightAmt(String freightAmt) {
        FreightAmt = freightAmt;
    }

    public String getDiscountAmt() {
        return DiscountAmt;
    }

    public void setDiscountAmt(String discountAmt) {
        DiscountAmt = discountAmt;
    }

    public String getItemCat() {
        return ItemCat;
    }

    public void setItemCat(String itemCat) {
        ItemCat = itemCat;
    }

    public String getItemCatDesc() {
        return ItemCatDesc;
    }

    public void setItemCatDesc(String itemCatDesc) {
        ItemCatDesc = itemCatDesc;
    }

    public String getDiscountPer() {
        return DiscountPer;
    }

    public void setDiscountPer(String discountPer) {
        DiscountPer = discountPer;
    }

    public String getDepotStock() {
        return DepotStock;
    }

    public void setDepotStock(String depotStock) {
        DepotStock = depotStock;
    }

    public String getOwnStock() {
        return OwnStock;
    }

    public void setOwnStock(String ownStock) {
        OwnStock = ownStock;
    }

    public String getDelvQty() {
        return DelvQty;
    }

    public void setDelvQty(String delvQty) {
        DelvQty = delvQty;
    }

    public String getOpenQty() {
        return OpenQty;
    }

    public void setOpenQty(String openQty) {
        OpenQty = openQty;
    }

    public String getLoginID() {
        return LoginID;
    }

    public void setLoginID(String loginID) {
        LoginID = loginID;
    }

    public String getStorLoc() {
        return StorLoc;
    }

    public void setStorLoc(String storLoc) {
        StorLoc = storLoc;
    }

    public String getStorLocDesc() {
        return StorLocDesc;
    }

    public void setStorLocDesc(String storLocDesc) {
        StorLocDesc = storLocDesc;
    }

    public String getMatFrgtGrpDesc() {
        return MatFrgtGrpDesc;
    }

    public void setMatFrgtGrpDesc(String matFrgtGrpDesc) {
        MatFrgtGrpDesc = matFrgtGrpDesc;
    }

    public String getMatFrgtGrp() {
        return MatFrgtGrp;
    }

    public void setMatFrgtGrp(String matFrgtGrp) {
        MatFrgtGrp = matFrgtGrp;
    }

    public String getSalesDistrict() {
        return SalesDistrict;
    }

    public void setSalesDistrict(String salesDistrict) {
        SalesDistrict = salesDistrict;
    }

    public String getSalesDistrictDesc() {
        return SalesDistrictDesc;
    }

    public void setSalesDistrictDesc(String salesDistrictDesc) {
        SalesDistrictDesc = salesDistrictDesc;
    }

    public String getMeansOfTranstyp() {
        return MeansOfTranstyp;
    }

    public void setMeansOfTranstyp(String meansOfTranstyp) {
        MeansOfTranstyp = meansOfTranstyp;
    }

    public String getForwardingAgent() {
        return ForwardingAgent;
    }

    public void setForwardingAgent(String forwardingAgent) {
        ForwardingAgent = forwardingAgent;
    }

    public String getForwardingAgentName() {
        return ForwardingAgentName;
    }

    public void setForwardingAgentName(String forwardingAgentName) {
        ForwardingAgentName = forwardingAgentName;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public Boolean getDisplayed() {
        return isDisplayed;
    }

    public void setDisplayed(Boolean displayed) {
        isDisplayed = displayed;
    }

    public int getItemNo() {
        return itemNo;
    }

    public void setItemNo(int itemNo) {
        this.itemNo = itemNo;
    }

    public String getCPGUID() {
        return CPGUID;
    }

    public void setCPGUID(String CPGUID) {
        this.CPGUID = CPGUID;
    }

    public String getSerialNoFrom() {
        return SerialNoFrom;
    }

    public void setSerialNoFrom(String serialNoFrom) {
        SerialNoFrom = serialNoFrom;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getBatch() {
        return Batch;
    }

    public void setBatch(String batch) {
        Batch = batch;
    }

    public String getReturnQty() {
        return returnQty;
    }

    public void setReturnQty(String returnQty) {
        this.returnQty = returnQty;
    }

    public String getReturnDesc() {
        return returnDesc;
    }

    public void setReturnDesc(String returnDesc) {
        this.returnDesc = returnDesc;
    }

    public String getReturnMrp() {
        return returnMrp;
    }

    public void setReturnMrp(String returnMrp) {
        this.returnMrp = returnMrp;
    }

    public String getReturnBatchNumber() {
        return returnBatchNumber;
    }

    public void setReturnBatchNumber(String returnBatchNumber) {
        this.returnBatchNumber = returnBatchNumber;
    }

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    public Boolean getItemToReturn() {
        return isItemToReturn;
    }

    public void setItemToReturn(Boolean itemToReturn) {
        isItemToReturn = itemToReturn;
    }

    public String getMFD() {
        return MFD;
    }

    public void setMFD(String MFD) {
        this.MFD = MFD;
    }

    public String getCrsSksGroup() {
        return CrsSksGroup;
    }

    public void setCrsSksGroup(String crsSksGroup) {
        CrsSksGroup = crsSksGroup;
    }

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    public String getRLPrice() {
        return RLPrice;
    }

    public void setRLPrice(String RLPrice) {
        this.RLPrice = RLPrice;
    }

    public String getUnrestrictedQty() {
        return UnrestrictedQty;
    }

    public void setUnrestrictedQty(String unrestrictedQty) {
        UnrestrictedQty = unrestrictedQty;
    }

    public String getUom() {
        return Uom;
    }

    public void setUom(String uom) {
        Uom = uom;
    }

    public String getSerialNoTo() {
        return SerialNoTo;
    }

    public void setSerialNoTo(String serialNoTo) {
        SerialNoTo = serialNoTo;
    }

    public String getRoute() {
        return Route;
    }

    public void setRoute(String route) {
        Route = route;
    }

    public String getRouteDesc() {
        return RouteDesc;
    }

    public void setRouteDesc(String routeDesc) {
        RouteDesc = routeDesc;
    }

    public String getSplProcessing() {
        return SplProcessing;
    }

    public void setSplProcessing(String splProcessing) {
        SplProcessing = splProcessing;
    }

    public String getSplProcessingDesc() {
        return SplProcessingDesc;
    }

    public void setSplProcessingDesc(String splProcessingDesc) {
        SplProcessingDesc = splProcessingDesc;
    }

    public ArrayList<SOTaskHistoryBean> getSoTaskHistoryBeanArrayList() {
        return soTaskHistoryBeanArrayList;
    }

    public void setSoTaskHistoryBeanArrayList(ArrayList<SOTaskHistoryBean> soTaskHistoryBeanArrayList) {
        this.soTaskHistoryBeanArrayList = soTaskHistoryBeanArrayList;
    }

    public String getmStrTotalWeight() {
        return mStrTotalWeight;
    }

    public void setmStrTotalWeight(String mStrTotalWeight) {
        this.mStrTotalWeight = mStrTotalWeight;
    }

    public String getmStrWeightUOM() {
        return mStrWeightUOM;
    }

    public void setmStrWeightUOM(String mStrWeightUOM) {
        this.mStrWeightUOM = mStrWeightUOM;
    }

    public String getmSteTotalQtyUOM() {
        return mSteTotalQtyUOM;
    }

    public void setmSteTotalQtyUOM(String mSteTotalQtyUOM) {
        this.mSteTotalQtyUOM = mSteTotalQtyUOM;
    }

    public String getMatNoAndDesc() {
        return matNoAndDesc;
    }

    public void setMatNoAndDesc(String matNoAndDesc) {
        this.matNoAndDesc = matNoAndDesc;
    }
}
