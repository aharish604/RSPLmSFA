package com.rspl.sf.msfa.mbo;

/**
 * Created by e10526 on 11-12-2016.
 */

public class BirthdaysBean {
    private String OwnerName="";
    private String CPUID="";
    private String MobileNo="";
    private String DOB = "";
    private String Anniversary ="";
    private String DOBStatus ="";
    private String AnniversaryStatus ="";
    private String AppointmentStatus ="";
    public String getAppointmentStatus() {
        return AppointmentStatus;
    }

    public void setAppointmentStatus(String appointmentStatus) {
        AppointmentStatus = appointmentStatus;
    }



    public Boolean getAppointmentAlert() {
        return isAppointmentAlert;
    }

    public void setAppointmentAlert(Boolean appointmentAlert) {
        isAppointmentAlert = appointmentAlert;
    }

    private Boolean isAppointmentAlert = false;
    public String getAppointMentDate() {
        return appointMentDate;
    }

    public void setAppointMentDate(String appointMentDate) {
        this.appointMentDate = appointMentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }


    private String appointMentDate = "";
    private String appointmentTime = "";
    private String appointmentType = "";
    private String appointmentEndTime = "";
    public String getAppointmentEndTime() {
        return appointmentEndTime;
    }

    public void setAppointmentEndTime(String appointmentEndTime) {
        this.appointmentEndTime = appointmentEndTime;
    }



    public String getRetailerName() {
        return RetailerName;
    }

    public void setRetailerName(String retailerName) {
        RetailerName = retailerName;
    }

    private String RetailerName="";
    public String getAnniversaryStatus() {
        return AnniversaryStatus;
    }

    public void setAnniversaryStatus(String anniversaryStatus) {
        AnniversaryStatus = anniversaryStatus;
    }

    public String getOwnerName() {
        return OwnerName;
    }

    public void setOwnerName(String ownerName) {
        OwnerName = ownerName;
    }

    public String getCPUID() {
        return CPUID;
    }

    public void setCPUID(String CPUID) {
        this.CPUID = CPUID;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getAnniversary() {
        return Anniversary;
    }

    public void setAnniversary(String anniversary) {
        Anniversary = anniversary;
    }

    public String getDOBStatus() {
        return DOBStatus;
    }

    public void setDOBStatus(String DOBStatus) {
        this.DOBStatus = DOBStatus;
    }





}
