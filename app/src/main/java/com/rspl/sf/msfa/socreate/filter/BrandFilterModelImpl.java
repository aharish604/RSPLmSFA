package com.rspl.sf.msfa.socreate.filter;

import android.content.Context;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.mbo.ConfigTypesetTypesBean;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10769 on 31-10-2017.
 */

public class BrandFilterModelImpl implements BrandFilterModel {
    private Context mContext;
    private BrandFilterView filterView = null;

    public BrandFilterModelImpl(Context mContext, BrandFilterView filterView) {
        this.mContext = mContext;
        this.filterView = filterView;
    }

    @Override
    public void onStart(int comingFrom, String brandID) {
        String mStrConfigQry ="";
        try {
            if(comingFrom == 1 || comingFrom==36){
                if(!TextUtils.isEmpty(brandID)) {
                    mStrConfigQry = Constants.Brands + "?$filter=" + Constants.DMSDivision + " eq '" + brandID + "'";
                }else {
                    mStrConfigQry = Constants.Brands;
                }
            }else {
                mStrConfigQry = Constants.Brands;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
