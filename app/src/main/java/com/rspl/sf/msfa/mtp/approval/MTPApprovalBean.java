package com.rspl.sf.msfa.mtp.approval;

import java.io.Serializable;

/**
 * Created by e10847 on 23-02-2018.
 */

public class MTPApprovalBean implements Serializable {
    private String instanceID="";
    private String entityKey="";
    private String entityKeyID="";
    private String entityKeyDesc="";
    private String entityAttribute1="";
    private String EntityAttribute5="";
    private String EntityAttribute6="";
    private String EntityAttribute7="";
    private String priorityNumber="";
    private String entityDate1="";
    private String entityType="";

    public String getEntityAttribute4() {
        return EntityAttribute4;
    }

    public void setEntityAttribute4(String entityAttribute4) {
        EntityAttribute4 = entityAttribute4;
    }

    private String EntityAttribute4="";

    public String getInitiator() {
        return Initiator;
    }

    public void setInitiator(String initiator) {
        Initiator = initiator;
    }

    private String Initiator="";

    public String getRouteSchGUID() {
        return routeSchGUID;
    }

    public void setRouteSchGUID(String routeSchGUID) {
        this.routeSchGUID = routeSchGUID;
    }

    private String routeSchGUID="";

    public String getInstanceID() {
        return instanceID;
    }

    public void setInstanceID(String instanceID) {
        this.instanceID = instanceID;
    }

    public String getEntityKey() {
        return entityKey;
    }

    public void setEntityKey(String entityKey) {
        this.entityKey = entityKey;
    }

    public String getEntityKeyID() {
        return entityKeyID;
    }

    public void setEntityKeyID(String entityKeyID) {
        this.entityKeyID = entityKeyID;
    }

    public String getEntityKeyDesc() {
        return entityKeyDesc;
    }

    public void setEntityKeyDesc(String entityKeyDesc) {
        this.entityKeyDesc = entityKeyDesc;
    }

    public String getEntityAttribute1() {
        return entityAttribute1;
    }

    public void setEntityAttribute1(String entityAttribute1) {
        this.entityAttribute1 = entityAttribute1;
    }

    public String getPriorityNumber() {
        return priorityNumber;
    }

    public void setPriorityNumber(String priorityNumber) {
        this.priorityNumber = priorityNumber;
    }

    public String getEntityDate1() {
        return entityDate1;
    }

    public void setEntityDate1(String entityDate1) {
        this.entityDate1 = entityDate1;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityAttribute5() {
        return EntityAttribute5;
    }

    public void setEntityAttribute5(String entityAttribute5) {
        EntityAttribute5 = entityAttribute5;
    }

    public String getEntityAttribute7() {
        return EntityAttribute7;
    }

    public void setEntityAttribute7(String entityAttribute7) {
        EntityAttribute7 = entityAttribute7;
    }

    public String getEntityAttribute6() {
        return EntityAttribute6;
    }

    public void setEntityAttribute6(String entityAttribute6) {
        EntityAttribute6 = entityAttribute6;
    }
}
