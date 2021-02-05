package com.rspl.sf.msfa.dealerstock.stockmaterial;

import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rspl.sf.msfa.mbo.StocksInfoBean;

import java.util.ArrayList;

/**
 * Created by e10847 on 21-04-2018.
 */

public interface IDealerStockMaterialViewPresenter {
    void initializeUI();
    void initializeClickListeners();
    void initializeObjects();
    void initializeRecyclerViewAdapter(LinearLayoutManager layoutManager);
    void showProgressDialog();
    void hideProgressDialog();
    void showMessage(String message, int status);
    void refreshAdapter(ArrayList<?> arrayList);
    void loadIntentData(Intent intent);
    void searchResult(ArrayList<StocksInfoBean> searchBeanArrayList);

    interface IDealerStockMaterialPresenter{
        String getMaterials();
        void loadMaterialData();
        void onSearch(String s);
    }

}
