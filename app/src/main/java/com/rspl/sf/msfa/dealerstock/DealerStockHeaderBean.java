package com.rspl.sf.msfa.dealerstock;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by e10526 on 09-04-2018.
 */

public class DealerStockHeaderBean implements Serializable{
    public ArrayList<DealerStockBean> getAlStockList() {
        return alStockList;
    }

    public void setAlStockList(ArrayList<DealerStockBean> alStockList) {
        this.alStockList = alStockList;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    private String customerNumber="";
    private String customerName="";

    private ArrayList<DealerStockBean> alStockList = null;
}
