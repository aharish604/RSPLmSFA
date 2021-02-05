package com.rspl.sf.msfa.reports.InvoiceHistory.filter;

import android.content.Context;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.mbo.ConfigTypesetTypesBean;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10769 on 31-10-2017.
 */

public class SOFilterModelImpl implements SOFilterModel {
    private Context mContext;
    private SOFilterView filterView = null;

    public SOFilterModelImpl(Context mContext, SOFilterView filterView) {
        this.mContext = mContext;
        this.filterView = filterView;
    }

    @Override
    public void onStart() {
        String mStrConfigQry = Constants.ConfigTypesetTypes + "?$filter=" + Constants.Typeset + " eq '" +
                Constants.SOITST + "' &$orderby=" + Constants.Types + " asc";
        final ArrayList<ConfigTypesetTypesBean> configTypesetTypesBeanArrayList = new ArrayList<>();
        final ArrayList<ConfigTypesetTypesBean> configTypesetDeliveryList = new ArrayList<>();
        try {
            configTypesetTypesBeanArrayList.addAll(OfflineManager.getConfigTypesetTypes(mStrConfigQry));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        mStrConfigQry = Constants.ConfigTypesetTypes + "?$filter=" + Constants.Typeset + " eq '" + Constants.DELVST + "' &$orderby = Types asc";
        try {
            configTypesetDeliveryList.addAll(OfflineManager.getConfigTypesetTypes(mStrConfigQry));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        if (filterView!=null) {
            filterView.displayList(configTypesetTypesBeanArrayList, configTypesetDeliveryList);
        }

    }

    @Override
    public void onDestroy() {
        filterView=null;
    }
}
