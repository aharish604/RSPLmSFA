package com.rspl.sf.msfa.attendance.attendancesummary;

public class AttendanceSummaryBean {
    private String SPName="";
    private String StartDate="";
    private String EndDate="";
    private String StartTime="";
    private String StartDateTime="";

    public String getStartDateTime() {
        return StartDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        StartDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return EndDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        EndDateTime = endDateTime;
    }

    private String EndDateTime="";

    public String getTimeDiff() {
        return TimeDiff;
    }

    public void setTimeDiff(String timeDiff) {
        TimeDiff = timeDiff;
    }

    private String TimeDiff="";

    public String getTotalWorkingHour() {
        return TotalWorkingHour;
    }

    public void setTotalWorkingHour(String totalWorkingHour) {
        TotalWorkingHour = totalWorkingHour;
    }

    private String TotalWorkingHour="";

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    private String EndTime="";

    public String getSPName() {
        return SPName;
    }

    public void setSPName(String SPName) {
        this.SPName = SPName;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    private String CreatedBy="";
}
