package com.rspl.sf.msfa.interfaces;



import com.rspl.sf.msfa.soapproval.SalesApprovalBean;

/**
 * Created by e10769 on 23-05-2017.
 */

public interface AsyncTaskSOItemInterface {
    void soItemLoaded(SalesApprovalBean soItemBeanArrayList, Boolean isStoreOpened);
}
