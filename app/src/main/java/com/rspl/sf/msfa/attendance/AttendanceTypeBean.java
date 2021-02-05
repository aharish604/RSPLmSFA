package com.rspl.sf.msfa.attendance;

public class AttendanceTypeBean {
    public String getTypeset() {
        return typeset;
    }

    public void setTypeset(String typeset) {
        this.typeset = typeset;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    private   String typeset="";
    private   String type="";
    private  String typeValue="";
    private  String typeName="";

    @Override
    public String toString() {
        return typeName;
    }
}
