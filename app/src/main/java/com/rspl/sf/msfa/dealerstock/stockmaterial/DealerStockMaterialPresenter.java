package com.rspl.sf.msfa.dealerstock.stockmaterial;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.dealerstock.DealerStockBean;
import com.rspl.sf.msfa.mbo.StocksInfoBean;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;

public class DealerStockMaterialPresenter implements IDealerStockMaterialViewPresenter.IDealerStockMaterialPresenter {
    Context context;
    Activity activity;
    IDealerStockMaterialViewPresenter presenter=null;
    private ArrayList<StocksInfoBean> stocksInfoBeanArrayList, searchBeanArrayList;
    private String searchText = "";
    private ArrayList<DealerStockBean> dealerStockBeanArrayList = null;

    public DealerStockMaterialPresenter(Context context, Activity activity, IDealerStockMaterialViewPresenter presenter, ArrayList<DealerStockBean> dealerStockBeanArrayList) {
        this.context = context;
        this.activity = activity;
        this.presenter = presenter;
        stocksInfoBeanArrayList = new ArrayList<>();
        searchBeanArrayList = new ArrayList<>();
        this.dealerStockBeanArrayList = dealerStockBeanArrayList;
    }

    @Override
    public String getMaterials() {
        String query = Constants.MaterialByCustomers;
        try {
            stocksInfoBeanArrayList = OfflineManager.getMaterialByCustomersList(query);
            if (dealerStockBeanArrayList != null) {
                for (int j = 0; j < dealerStockBeanArrayList.size(); j++) {
                    DealerStockBean dealerStockBean = dealerStockBeanArrayList.get(j);
                    for (int i = 0; i < stocksInfoBeanArrayList.size(); i++) {
                        StocksInfoBean stocksInfoBean = stocksInfoBeanArrayList.get(i);
                        if (dealerStockBean.getMaterialNo().equalsIgnoreCase(stocksInfoBeanArrayList.get(i).getMaterialNo())) {
                            stocksInfoBeanArrayList.remove(stocksInfoBean);
                        }
                    }
                }
            }
            return "";
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }

    @Override
    public void loadMaterialData() {
        try {
            new GetMaterialStockAsyncTask().execute();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSearch(String searchText) {
        this.searchText = searchText;
        onSearchQuery(searchText);
    }

    private void onSearchQuery(String searchText) {
        this.searchText = searchText;
        searchBeanArrayList.clear();
        boolean isCustomerID = false;
        boolean isCustomerName = false;
        if (stocksInfoBeanArrayList != null) {
            if (TextUtils.isEmpty(searchText)) {
                searchBeanArrayList.addAll(stocksInfoBeanArrayList);
            } else {
                for (StocksInfoBean item : stocksInfoBeanArrayList) {
                    if (!TextUtils.isEmpty(searchText)) {
                        isCustomerID = item.getMaterialNo().toLowerCase().contains(searchText.toLowerCase());
                        isCustomerName = item.getMaterialDesc().toLowerCase().contains(searchText.toLowerCase());
                    } else {
                        isCustomerID = true;
                        isCustomerName = true;
                    }
                    if (isCustomerID || isCustomerName)
                        searchBeanArrayList.add(item);
                }
            }
        }
        if (presenter != null) {
            presenter.searchResult(searchBeanArrayList);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetMaterialStockAsyncTask extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            presenter.showProgressDialog();
        }

        @Override
        protected String doInBackground(Void... voids) {
            return getMaterials();
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            if (presenter!=null) {
                presenter.hideProgressDialog();
                if (TextUtils.isEmpty(aVoid)) {
                    if (stocksInfoBeanArrayList.isEmpty()) {
                        presenter.showMessage(context.getString(R.string.no_materials_found), 0);
                    } else {
                        presenter.refreshAdapter(stocksInfoBeanArrayList);
                    }
                } else {
                    presenter.showMessage(aVoid, 0);
                }
            }
        }
    }
}
