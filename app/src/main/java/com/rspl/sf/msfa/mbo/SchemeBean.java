package com.rspl.sf.msfa.mbo;

/**
 * Created by e10849 on 25-09-2017.
 */

public class SchemeBean {
    private String SchemeName = "";
    private String ValidFrom = "";

    public String getSchemeName() {
        return SchemeName;
    }

    public void setSchemeName(String schemeName) {
        SchemeName = schemeName;
    }

    public String getValidFrom() {
        return ValidFrom;
    }

    public void setValidFrom(String validFrom) {
        ValidFrom = validFrom;
    }

    public String getValidTo() {
        return ValidTo;
    }

    public void setValidTo(String validTo) {
        ValidTo = validTo;
    }

    private  String ValidTo = "";
}
