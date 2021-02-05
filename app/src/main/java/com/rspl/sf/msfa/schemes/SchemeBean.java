package com.rspl.sf.msfa.schemes;

/**
 * Created by e10742 on 6/7/2017.
 */

public class SchemeBean {

    String SchemeName = "";
    String ValidFrom = "";
    String ValidTo = "";

    public SchemeBean(String schemeName, String validFrom, String validTo) {
        SchemeName = schemeName;
        ValidFrom = validFrom;
        ValidTo = validTo;
    }

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
}
