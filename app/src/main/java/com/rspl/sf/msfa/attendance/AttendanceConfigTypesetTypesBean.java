package com.rspl.sf.msfa.attendance;

/**
 * Created by e10769 on 29-06-2017.
 */

public class AttendanceConfigTypesetTypesBean {
    private String Types = "";


    public String getMandatory() {
        return mandatory;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    private String mandatory = "";

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    private String typeName = "";

    public String getTypes() {
        return Types;
    }

    public void setTypes(String types) {
        Types = types;
    }

   public AttendanceConfigTypesetTypesBean() {
    }

    AttendanceConfigTypesetTypesBean(String types, String typeName){
        this.Types=types;
        this.typeName=typeName;
    }
    @Override
    public String toString() {
        return typeName.toString();
    }
}
