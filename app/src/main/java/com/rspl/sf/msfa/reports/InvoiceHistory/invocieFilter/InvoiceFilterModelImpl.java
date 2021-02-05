package com.rspl.sf.msfa.reports.InvoiceHistory.invocieFilter;

import android.content.Context;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.mbo.ConfigTypesetTypesBean;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10860 on 12/2/2017.
 */

public class InvoiceFilterModelImpl implements InvoiceFilterModel {

    private Context mContext;
    private InvoiceFilterView filterView;

    public InvoiceFilterModelImpl(Context mContext, InvoiceFilterView filterView) {
        this.mContext = mContext;
        this.filterView = filterView;
    }
    @Override
    public void onStart() {

        ArrayList<ConfigTypesetTypesBean> invoicePaymentStatusArrayList = new ArrayList<>();
        ArrayList<ConfigTypesetTypesBean> invoiceGRStatusArrayList = new ArrayList<>();


        String mStrConfigQry = Constants.ConfigTypesetTypes + "?$filter=" + Constants.Typeset + " eq '" + Constants.INVST + "'";
        try {
            invoicePaymentStatusArrayList.addAll(OfflineManager.getStatusConfig(mStrConfigQry, Constants.ALL));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        ConfigTypesetTypesBean configTypesetTypesBean = new ConfigTypesetTypesBean();
        configTypesetTypesBean.setTypes("C");
        configTypesetTypesBean.setTypesName("Over due");
        invoiceGRStatusArrayList.add(configTypesetTypesBean);
        configTypesetTypesBean = new ConfigTypesetTypesBean();
        configTypesetTypesBean.setTypes("A");
        configTypesetTypesBean.setTypesName("Near due");
        invoiceGRStatusArrayList.add(configTypesetTypesBean);
        configTypesetTypesBean = new ConfigTypesetTypesBean();
        configTypesetTypesBean.setTypes("B");
        configTypesetTypesBean.setTypesName("Not due");
        invoiceGRStatusArrayList.add(configTypesetTypesBean);
        if (filterView!=null) {
            filterView.displayList(invoicePaymentStatusArrayList,invoiceGRStatusArrayList);
        }


    }

    @Override
    public void onDestroy() {
        filterView=null;
    }
}
