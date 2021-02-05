package com.rspl.sf.msfa.reports.daySummary;

import java.io.Serializable;

public class DashBoardBean implements Serializable{
    private String application = "";
    private String total = "";
    private String active = "";
    private boolean showProgress=false;

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }
}
