package com.rspl.sf.msfa.mbo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by e10762 on 08-02-2017.
 */

public class MaterialsBean implements Parcelable
{
    private String MaterialNo;
    private String MaterialDesc;
    private String MaterialGrp;
    private String MaterialGrpDesc;
    private String BaseUom;
    private String NetWeight;
    private String DepotStock;
    private String OrderQty = "";

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

    public String getMaterialGrp() {
        return MaterialGrp;
    }

    public void setMaterialGrp(String materialGrp) {
        MaterialGrp = materialGrp;
    }

    public String getMaterialGrpDesc() {
        return MaterialGrpDesc;
    }

    public void setMaterialGrpDesc(String materialGrpDesc) {
        MaterialGrpDesc = materialGrpDesc;
    }

    public String getBaseUom() {
        return BaseUom;
    }

    public void setBaseUom(String baseUom) {
        BaseUom = baseUom;
    }

    public String getNetWeight() {
        return NetWeight;
    }

    public void setNetWeight(String netWeight) {
        NetWeight = netWeight;
    }

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    public String getMaterialType() {
        return MaterialType;
    }

    public void setMaterialType(String materialType) {
        MaterialType = materialType;
    }

    public String getMaterialTypeDesc() {
        return MaterialTypeDesc;
    }

    public void setMaterialTypeDesc(String materialTypeDesc) {
        MaterialTypeDesc = materialTypeDesc;
    }

    public String getNetWeightUom() {
        return NetWeightUom;
    }

    public void setNetWeightUom(String netWeightUom) {
        NetWeightUom = netWeightUom;
    }

    private String MRP;
    private String MaterialType;
    private String MaterialTypeDesc;
    private String NetWeightUom;

    public void setDepotStock(String depotStock) {
        DepotStock = depotStock;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i)
    {

        dest.writeString(MaterialNo);
        dest.writeString(MaterialDesc);
        dest.writeString(BaseUom);
        dest.writeString(OrderQty);

    }
    public MaterialsBean()
    {

    }
    private MaterialsBean(Parcel in)
    {
        this.MaterialNo = in.readString();
        this.MaterialDesc = in.readString();
        this.BaseUom = in.readString();
        this.OrderQty = in.readString();

    }
    public String getDepotStock() {
        return DepotStock;
    }


    public static final Parcelable.Creator<MaterialsBean> CREATOR = new Parcelable.Creator<MaterialsBean>() {

        @Override
        public MaterialsBean createFromParcel(Parcel source) {
            return new MaterialsBean(source);
        }

        @Override
        public MaterialsBean[] newArray(int size) {
            return new MaterialsBean[size];
        }
    };

    public String getOrderQty() {
        return OrderQty;
    }

    public void setOrderQty(String orderQty) {
        OrderQty = orderQty;
    }
}
