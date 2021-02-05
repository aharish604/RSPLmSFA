package com.rspl.sf.msfa.soapproval;

import com.rspl.sf.msfa.mbo.SalesOrderBean;
import com.rspl.sf.msfa.socreate.CreditLimitBean;
import com.rspl.sf.msfa.solist.SOTaskHistoryBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10854 on 02-12-2017.
 */

public class SalesApprovalBean {
    ArrayList<SalesOrderBean> soItemBeanArrayList = new ArrayList<>();
    ArrayList<SalesOrderBean> soHeaderBeanArrayList = new ArrayList<>();
    private ArrayList<SOTaskHistoryBean> taskHistorysArrayList = new ArrayList<>();
    private List<CreditLimitBean> creditLimitAreas = new ArrayList<>();

    public List<CreditLimitBean> getCreditLimitAreas() {
        return creditLimitAreas;
    }

    public void setCreditLimitAreas(List<CreditLimitBean> creditLimitAreas) {
        this.creditLimitAreas = creditLimitAreas;
    }

    public ArrayList<SalesOrderBean> getSoItemBeanArrayList() {
        return soItemBeanArrayList;
    }

    public void setSoItemBeanArrayList(ArrayList<SalesOrderBean> soItemBeanArrayList) {
        this.soItemBeanArrayList = soItemBeanArrayList;
    }

    public ArrayList<SalesOrderBean> getSoHeaderBeanArrayList() {
        return soHeaderBeanArrayList;
    }

    public void setSoHeaderBeanArrayList(ArrayList<SalesOrderBean> soHeaderBeanArrayList) {
        this.soHeaderBeanArrayList = soHeaderBeanArrayList;
    }

    public ArrayList<SOTaskHistoryBean> getTaskHistorysArrayList() {
        return taskHistorysArrayList;
    }

    public void setTaskHistorysArrayList(ArrayList<SOTaskHistoryBean> taskHistorysArrayList) {
        this.taskHistorysArrayList = taskHistorysArrayList;
    }
}
