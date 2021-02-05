package com.rspl.sf.msfa.routeplan.customerslist;

import android.content.Intent;

import com.rspl.sf.msfa.collectionPlan.WeekDetailsList;
import com.rspl.sf.msfa.collectionPlan.WeekHeaderList;
import com.rspl.sf.msfa.mbo.CustomerBean;
import com.rspl.sf.msfa.mtp.MTPHeaderBean;
import com.rspl.sf.msfa.mtp.MTPRoutePlanBean;

import java.util.ArrayList;

/**
 * Created by e10847 on 19-12-2017.
 */

public interface ICustomerPresenter {
    void onFilter();
    void onSearch(String searchText);
    void onRefresh();
    void startFilter(int requestCode, int resultCode, Intent data);
    void loadAsyncTask();

    void sendResult(MTPHeaderBean mtpResultHeaderBean, MTPHeaderBean mtpHeaderBean, boolean isAsmLogin);

    void loadMTPCustomerList(ArrayList<MTPRoutePlanBean> mtpRoutePlanBeanArrayList, boolean isAsmLogin, String externalID, String comingFrom);

    void sendResultRTGS(WeekHeaderList mtpResultHeaderBean, WeekHeaderList mtpHeaderBean, ArrayList<CustomerBean> alSelectedList);

    void loadRTGSCustomerList(ArrayList<WeekDetailsList> rtgsBeanArrayList,String ExternalRefID);
    void loadRTGSList(ArrayList<WeekDetailsList> rtgsBeanArrayList);
}
