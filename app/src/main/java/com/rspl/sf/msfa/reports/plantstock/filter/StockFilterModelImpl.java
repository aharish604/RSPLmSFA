package com.rspl.sf.msfa.reports.plantstock.filter;

import android.content.Context;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.mbo.ConfigTypesetTypesBean;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10769 on 31-10-2017.
 */

public class StockFilterModelImpl implements StockFilterModel {
    private Context mContext;
    private StockFilterView filterView = null;

    public StockFilterModelImpl(Context mContext, StockFilterView filterView) {
        this.mContext = mContext;
        this.filterView = filterView;
    }

    @Override
    public void onStart() {
        String mStrConfigQry = Constants.Brands ;
        final ArrayList<ConfigTypesetTypesBean> brnadsVal = new ArrayList<>();
        final ArrayList<ConfigTypesetTypesBean> orderMatGrpVal = new ArrayList<>();
        try {
            brnadsVal.addAll(OfflineManager.getBrandsVal(mStrConfigQry));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        if (filterView!=null) {
            filterView.displayList(brnadsVal, orderMatGrpVal);
        }

    }

    @Override
    public void onDestroy() {
        filterView=null;
    }
}
