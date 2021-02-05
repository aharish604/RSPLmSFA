package com.rspl.sf.msfa.solist;


import com.rspl.sf.msfa.socreate.CreditLimitBean;
import com.rspl.sf.msfa.socreate.CustomerPartnerFunctionBean;
import com.rspl.sf.msfa.socreate.SOConditionItemDetaiBean;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.socreate.SOSubItemBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10526 on 4/28/2017.
 */

public class SOListBean implements Serializable {
    private String SONo = "";
    private String OrderType = "";
    private String OrderTypeDesc = "";
    private String OrderDate = "";
    private String CustomerNo = "";
    private String CustomerName = "";
    private String NetPrice = "";
    private String Currency = "";
    private String Material = "";
    private String MaterialDesc = "";
    private String Quantity = "";
    private String UOM = "";
    private String MaterialGroup = "";
    private String MatGroupDesc = "";
    private String ItemNo = "";
    private String DelvStatus = "";
    private String DelvStatusDesc = "";
    private String ShippingTypeDesc = "";
    private String ShippingTypeID = "";
    private String MeansOfTranstypDesc = "";
    private String CustomerPO = "";
    private String ShipToParty = "";
    private String PaytermDesc = "";
    private String Incoterm1Desc = "";
    private String SalesAreaDesc = "";
    private String Incoterm2 = "";
    private String UnitPrice = "0.00";
    private String address = "";
    private String SalesArea = "";
    private String remarks = "";
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
    private String TotalAmt = "0";
    private String InstanceID = "";
    private String UnloadingPointId = "";
    private String UnloadingPointDesc = "";
    private String ReceivingPointId = "";
    private String ReceivingPointDesc = "";
    private String SalesOfficeId = "";
    private String SaleOffDesc = "";
    private String EntityAttribute1 = "";
    private String IfChanged = "";
    private int materialSize = 0;
    private int priceSize = 0;
    private String searchText = "";
    private String EntityType = "";
    private String SalesGroup = "";
    private String SaleGrpDesc = "";
    private String MatCodeAndDesc = "";
    private boolean oneTimeShipTo = false;
    private String CustFirstName = "";
    private String CustLastName = "";
    private String CustAddress1 = "";
    private String CustAddress2 = "";
    private String CustAddress3 = "";
    private String CustAddress4 = "";
    private String CustDistrict = "";
    private String CustCity = "";
    private String CustCountry = "";
    private String CustCountryDesc = "";
    private String CustRegion = "";
    private String CustRegionDesc = "";
    private String CustPostalCode = "";
    private String MeansOfTranstyp = "";
    private String RefDocNo = "";
    private String RefDocCat = "";
    private String CustomerPODate = "";
    private String salesDistDesc = "";
    private String salesDist = "";
    private String EntityValue1 = "";
    private String EntityCurrency = "";
    private ArrayList<SOSubItemBean> soSubItemBeen = new ArrayList<>();
    private ArrayList<SOConditionItemDetaiBean> conditionItemDetaiBeanArrayList = new ArrayList<>();
    private ArrayList<SOItemBean> soItemBeanArrayList = new ArrayList<>();
    private ArrayList<SalesDocBean> salesDocBeanArrayList = new ArrayList<>();
    private ArrayList<SOTextBean> soTextBeanArrayList = new ArrayList<>();
    private ArrayList<SOTextBean> soApprovalBeanArrayList = new ArrayList<>();
    private ArrayList<SOTaskHistoryBean> soTaskHistoryBeanArrayList = new ArrayList<>();
    private ArrayList<CustomerPartnerFunctionBean> customerPartnerFunctionList = new ArrayList<>();
    private List<CreditLimitBean> CreditControlAreas = new ArrayList<>();

    private String DeviceNo = "";
    private boolean DetailEnabled = false;
    private String Status = "";
    private String StatusDesc = "";
    private boolean isCheckedSO = false;
    private String VolumeAffected = "0";
    private String InspectionPointsLot = "";
    private String mStrCPGUID32 = "";
    private String mStrTotalWeight = "";
    private String mStrWeightUOM = "";
    private String mSteTotalQtyUOM = "";
    private String distChannelID="";

    public List<CreditLimitBean> getCreditControlAreas() {
        return CreditControlAreas;
    }

    public void setCreditControlAreas(List<CreditLimitBean> creditControlAreas) {
        CreditControlAreas = creditControlAreas;
    }

    public String getCreditControlAreaID() {
        return creditControlAreaID;
    }

    public void setCreditControlAreaID(String creditControlAreaID) {
        this.creditControlAreaID = creditControlAreaID;
    }

    private String creditControlAreaID="";

    public String getDistChannelID() {
        return distChannelID;
    }

    public void setDistChannelID(String distChannelID) {
        this.distChannelID = distChannelID;
    }

    public String getSalesOrgID() {
        return salesOrgID;
    }

    public void setSalesOrgID(String salesOrgID) {
        this.salesOrgID = salesOrgID;
    }

    private String salesOrgID="";
    public String getDivison() {
        return divison;
    }

    public void setDivison(String divison) {
        this.divison = divison;
    }

    private String divison = "";
    private boolean isVisitActivity=false;

    public String getCustomerPODate() {
        return CustomerPODate;
    }

    public void setCustomerPODate(String customerPODate) {
        CustomerPODate = customerPODate;
    }

    public String getSalesDistDesc() {
        return salesDistDesc;
    }

    public void setSalesDistDesc(String salesDistDesc) {
        this.salesDistDesc = salesDistDesc;
    }

    public String getSalesDist() {
        return salesDist;
    }

    public void setSalesDist(String salesDist) {
        this.salesDist = salesDist;
    }

    public String getMatCodeAndDesc() {
        return MatCodeAndDesc;
    }

    public void setMatCodeAndDesc(String matCodeAndDesc) {
        MatCodeAndDesc = matCodeAndDesc;
    }

    public String getEntityValue1() {
        return EntityValue1;
    }

    public void setEntityValue1(String entityValue1) {
        EntityValue1 = entityValue1;
    }

    public String getEntityCurrency() {
        return EntityCurrency;
    }

    public void setEntityCurrency(String entityCurrency) {
        EntityCurrency = entityCurrency;
    }

    public String getDeviceNo() {
        return DeviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        DeviceNo = deviceNo;
    }

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
    }

    public boolean isDetailEnabled() {
        return DetailEnabled;
    }

    public void setDetailEnabled(boolean detailEnabled) {
        DetailEnabled = detailEnabled;
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

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getStatusDesc() {
        return StatusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        StatusDesc = statusDesc;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getSONo() {
        return SONo;
    }

    public void setSONo(String SONo) {
        this.SONo = SONo;
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

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public String getCustomerNo() {
        return CustomerNo;
    }

    public void setCustomerNo(String customerNo) {
        CustomerNo = customerNo;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getNetPrice() {
        return NetPrice;
    }

    public void setNetPrice(String netPrice) {
        NetPrice = netPrice;
    }

    public String getDelvStatus() {
        return DelvStatus;
    }

    public void setDelvStatus(String delvStatus) {
        DelvStatus = delvStatus;
    }

    public ArrayList<SOSubItemBean> getSoSubItemBeen() {
        return soSubItemBeen;
    }

    public void setSoSubItemBeen(ArrayList<SOSubItemBean> soSubItemBeen) {
        this.soSubItemBeen = soSubItemBeen;
    }

    public ArrayList<SOConditionItemDetaiBean> getConditionItemDetaiBeanArrayList() {
        return conditionItemDetaiBeanArrayList;
    }

    public void setConditionItemDetaiBeanArrayList(ArrayList<SOConditionItemDetaiBean> conditionItemDetaiBeanArrayList) {
        this.conditionItemDetaiBeanArrayList = conditionItemDetaiBeanArrayList;
    }

    public String getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        UnitPrice = unitPrice;
    }

    public String getShippingTypeDesc() {
        return ShippingTypeDesc;
    }

    public void setShippingTypeDesc(String shippingTypeDesc) {
        ShippingTypeDesc = shippingTypeDesc;
    }

    public String getMeansOfTranstypDesc() {
        return MeansOfTranstypDesc;
    }

    public void setMeansOfTranstypDesc(String meansOfTranstypDesc) {
        MeansOfTranstypDesc = meansOfTranstypDesc;
    }

    public String getCustomerPO() {
        return CustomerPO;
    }

    public void setCustomerPO(String customerPO) {
        CustomerPO = customerPO;
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

    public String getIncoterm2() {
        return Incoterm2;
    }

    public void setIncoterm2(String incoterm2) {
        Incoterm2 = incoterm2;
    }

    public String getSalesAreaDesc() {
        return SalesAreaDesc;
    }

    public void setSalesAreaDesc(String salesAreaDesc) {
        SalesAreaDesc = salesAreaDesc;
    }

    public String getShippingTypeID() {
        return ShippingTypeID;
    }

    public void setShippingTypeID(String shippingTypeID) {
        ShippingTypeID = shippingTypeID;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public String getTotalAmt() {
        return TotalAmt;
    }

    public void setTotalAmt(String totalAmt) {
        TotalAmt = totalAmt;
    }

    public ArrayList<SOItemBean> getSoItemBeanArrayList() {
        return soItemBeanArrayList;
    }

    public void setSoItemBeanArrayList(ArrayList<SOItemBean> soItemBeanArrayList) {
        this.soItemBeanArrayList = soItemBeanArrayList;
    }

    public String getInstanceID() {
        return InstanceID;
    }

    public void setInstanceID(String instanceID) {
        InstanceID = instanceID;
    }

    public String getUnloadingPointId() {
        return UnloadingPointId;
    }

    public void setUnloadingPointId(String unloadingPointId) {
        UnloadingPointId = unloadingPointId;
    }

    public String getUnloadingPointDesc() {
        return UnloadingPointDesc;
    }

    public void setUnloadingPointDesc(String unloadingPointDesc) {
        UnloadingPointDesc = unloadingPointDesc;
    }

    public String getReceivingPointId() {
        return ReceivingPointId;
    }

    public void setReceivingPointId(String receivingPointId) {
        ReceivingPointId = receivingPointId;
    }

    public String getReceivingPointDesc() {
        return ReceivingPointDesc;
    }

    public void setReceivingPointDesc(String receivingPointDesc) {
        ReceivingPointDesc = receivingPointDesc;
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

    public ArrayList<SalesDocBean> getSalesDocBeanArrayList() {
        return salesDocBeanArrayList;
    }

    public void setSalesDocBeanArrayList(ArrayList<SalesDocBean> salesDocBeanArrayList) {
        this.salesDocBeanArrayList = salesDocBeanArrayList;
    }

    public ArrayList<SOTextBean> getSoTextBeanArrayList() {
        return soTextBeanArrayList;
    }

    public void setSoTextBeanArrayList(ArrayList<SOTextBean> soTextBeanArrayList) {
        this.soTextBeanArrayList = soTextBeanArrayList;
    }

    public ArrayList<SOTextBean> getSoApprovalBeanArrayList() {
        return soApprovalBeanArrayList;
    }

    public void setSoApprovalBeanArrayList(ArrayList<SOTextBean> soApprovalBeanArrayList) {
        this.soApprovalBeanArrayList = soApprovalBeanArrayList;
    }

    public ArrayList<SOTaskHistoryBean> getSoTaskHistoryBeanArrayList() {
        return soTaskHistoryBeanArrayList;
    }

    public void setSoTaskHistoryBeanArrayList(ArrayList<SOTaskHistoryBean> soTaskHistoryBeanArrayList) {
        this.soTaskHistoryBeanArrayList = soTaskHistoryBeanArrayList;
    }

    public String getEntityAttribute1() {
        return EntityAttribute1;
    }

    public void setEntityAttribute1(String entityAttribute1) {
        EntityAttribute1 = entityAttribute1;
    }

    public String getIfChanged() {
        return IfChanged;
    }

    public void setIfChanged(String ifChanged) {
        IfChanged = ifChanged;
    }

    public int getMaterialSize() {
        return materialSize;
    }

    public void setMaterialSize(int materialSize) {
        this.materialSize = materialSize;
    }

    public int getPriceSize() {
        return priceSize;
    }

    public void setPriceSize(int priceSize) {
        this.priceSize = priceSize;
    }

    public String getDelvStatusDesc() {
        return DelvStatusDesc;
    }

    public void setDelvStatusDesc(String delvStatusDesc) {
        DelvStatusDesc = delvStatusDesc;
    }

    public boolean isCheckedSO() {
        return isCheckedSO;
    }

    public void setCheckedSO(boolean checkedSO) {
        isCheckedSO = checkedSO;
    }

    public String getVolumeAffected() {
        return VolumeAffected;
    }

    public void setVolumeAffected(String volumeAffected) {
        VolumeAffected = volumeAffected;
    }

    public String getInspectionPointsLot() {
        return InspectionPointsLot;
    }

    public void setInspectionPointsLot(String inspectionPointsLot) {
        InspectionPointsLot = inspectionPointsLot;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getEntityType() {
        return EntityType;
    }

    public void setEntityType(String entityType) {
        EntityType = entityType;
    }

    public ArrayList<CustomerPartnerFunctionBean> getCustomerPartnerFunctionList() {
        return customerPartnerFunctionList;
    }

    public void setCustomerPartnerFunctionList(ArrayList<CustomerPartnerFunctionBean> customerPartnerFunctionList) {
        this.customerPartnerFunctionList = customerPartnerFunctionList;
    }

    public String getSalesGroup() {
        return SalesGroup;
    }

    public void setSalesGroup(String salesGroup) {
        SalesGroup = salesGroup;
    }

    public String getSaleGrpDesc() {
        return SaleGrpDesc;
    }

    public void setSaleGrpDesc(String saleGrpDesc) {
        SaleGrpDesc = saleGrpDesc;
    }

    public boolean isOneTimeShipTo() {
        return oneTimeShipTo;
    }

    public void setOneTimeShipTo(boolean oneTimeShipTo) {
        this.oneTimeShipTo = oneTimeShipTo;
    }

    public String getCustFirstName() {
        return CustFirstName;
    }

    public void setCustFirstName(String custFirstName) {
        CustFirstName = custFirstName;
    }

    public String getCustLastName() {
        return CustLastName;
    }

    public void setCustLastName(String custLastName) {
        CustLastName = custLastName;
    }

    public String getCustAddress1() {
        return CustAddress1;
    }

    public void setCustAddress1(String custAddress1) {
        CustAddress1 = custAddress1;
    }

    public String getCustAddress2() {
        return CustAddress2;
    }

    public void setCustAddress2(String custAddress2) {
        CustAddress2 = custAddress2;
    }

    public String getCustAddress3() {
        return CustAddress3;
    }

    public void setCustAddress3(String custAddress3) {
        CustAddress3 = custAddress3;
    }

    public String getCustAddress4() {
        return CustAddress4;
    }

    public void setCustAddress4(String custAddress4) {
        CustAddress4 = custAddress4;
    }

    public String getCustDistrict() {
        return CustDistrict;
    }

    public void setCustDistrict(String custDistrict) {
        CustDistrict = custDistrict;
    }

    public String getCustCity() {
        return CustCity;
    }

    public void setCustCity(String custCity) {
        CustCity = custCity;
    }

    public String getCustCountry() {
        return CustCountry;
    }

    public void setCustCountry(String custCountry) {
        CustCountry = custCountry;
    }

    public String getCustRegion() {
        return CustRegion;
    }

    public void setCustRegion(String custRegion) {
        CustRegion = custRegion;
    }

    public String getCustPostalCode() {
        return CustPostalCode;
    }

    public void setCustPostalCode(String custPostalCode) {
        CustPostalCode = custPostalCode;
    }

    public String getCustCountryDesc() {
        return CustCountryDesc;
    }

    public void setCustCountryDesc(String custCountryDesc) {
        CustCountryDesc = custCountryDesc;
    }

    public String getCustRegionDesc() {
        return CustRegionDesc;
    }

    public void setCustRegionDesc(String custRegionDesc) {
        CustRegionDesc = custRegionDesc;
    }

    public String getMeansOfTranstyp() {
        return MeansOfTranstyp;
    }

    public void setMeansOfTranstyp(String meansOfTranstyp) {
        MeansOfTranstyp = meansOfTranstyp;
    }

    public String getRefDocNo() {
        return RefDocNo;
    }

    public void setRefDocNo(String refDocNo) {
        RefDocNo = refDocNo;
    }

    public String getRefDocCat() {
        return RefDocCat;
    }

    public void setRefDocCat(String refDocCat) {
        RefDocCat = refDocCat;
    }

    public String getmStrCPGUID32() {
        return mStrCPGUID32;
    }

    public void setmStrCPGUID32(String mStrCPGUID32) {
        this.mStrCPGUID32 = mStrCPGUID32;
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

    public boolean isVisitActivity() {
        return isVisitActivity;
    }

    public void setVisitActivity(boolean visitActivity) {
        isVisitActivity = visitActivity;
    }

    public String getmSteTotalQtyUOM() {
        return mSteTotalQtyUOM;
    }

    public void setmSteTotalQtyUOM(String mSteTotalQtyUOM) {
        this.mSteTotalQtyUOM = mSteTotalQtyUOM;
    }
}
