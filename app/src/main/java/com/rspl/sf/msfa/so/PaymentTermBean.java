package com.rspl.sf.msfa.so;

/**
 * Created by e10769 on 30-06-2017.
 */

public class PaymentTermBean {
    private String Payterm="";
    private String PaytermDesc="";

    public String getPayterm() {
        return Payterm;
    }

    public void setPayterm(String payterm) {
        Payterm = payterm;
    }

    public String getPaytermDesc() {
        return PaytermDesc;
    }

    public void setPaytermDesc(String paytermDesc) {
        PaytermDesc = paytermDesc;
    }

    @Override
    public String toString() {
        return Payterm.toString()+" - "+PaytermDesc.toString();
    }
}
