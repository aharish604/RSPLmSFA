package com.rspl.sf.msfa.returnOrder.retrunFilter;

import android.content.Context;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.mbo.ConfigTypesetTypesBean;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10860 on 12/29/2017.
 */

public class FilterModelImpl implements FilterReturnModel {

    private Context mContext;
    private FilterReturnOrderView returnOrderView;

    public FilterModelImpl(FilterReturnOrderView returnOrderView, Context mContext) {
        this.returnOrderView = returnOrderView;
        this.mContext = mContext;
    }


    @Override
    public void onStart() {
        final ArrayList<ConfigTypesetTypesBean> configStatusTypes = new ArrayList<>();
        final ArrayList<ConfigTypesetTypesBean> configGrStatusTypes = new ArrayList<>();
        String mStrConfigQry = Constants.ConfigTypesetTypes + "?$filter=Typeset eq 'RODLST' &$orderby = Types asc";
        try {
            configStatusTypes.addAll(OfflineManager.getConfigTypesetTypes(mStrConfigQry, Constants.All));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        mStrConfigQry = Constants.ConfigTypesetTypes + "?$filter=Typeset eq 'ROGRST'  &$orderby = Types asc";
        try {
            configGrStatusTypes.addAll(OfflineManager.getConfigTypesetTypes(mStrConfigQry, Constants.All));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        if (returnOrderView != null) {
            returnOrderView.displayList(configStatusTypes, configGrStatusTypes);
        }

    }
}
