package com.rspl.sf.msfa.dealerstock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.filter.DateFilterFragment;
import com.rspl.sf.msfa.mbo.StocksInfoBean;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.socreate.filter.BrandFilterActivity;
import com.rspl.sf.msfa.solist.SOListBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.store.OnlineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by e10769 on 30-06-2017.
 */

public class StockCrtStpTwoPresenterImpl implements StockCrtStpTwoPresenter, OnlineODataInterface, UIListener {
    private Context mContext;
    private String filterType = "";
    private StockCrtStpTwoView createView = null;
    private String mStrComingFrom;
    private boolean isSessionRequired = false;
    private SOListBean soDefaultBean = null;
    private ArrayList<DealerStockBean> dlrStockBeanArrayList = new ArrayList<>();
    private ArrayList<DealerStockBean> searchBeanArrayList = new ArrayList<>();
    private SOListBean soListBeanHeader = null;
    private ArrayList<DealerStockBean> selectedItemList = null;
    private boolean checkAddItem;
    private String startDate = "";
    private String endDate = "";
    private String delvStatusId = "";
    private String statusId = "";
    private String statusName = "";
    private String delvStatusName = "";
    private String custNo = "";
    private ArrayList<DealerStockBean> stockRevItmList = null;
    private boolean isReviewScreen = false;
    private Activity activity;
    private String mStrCPGUID32 = "";
    private String custName;
    private int totalCount = 0;
    private int currentCount = 0;
    private String mStrVisitActRefID = "";
    private String mStrUID = "";

    public StockCrtStpTwoPresenterImpl(boolean checkAddItem, Activity mContext, StockCrtStpTwoView createView,
                                       String mStrComingFrom, boolean isSessionRequired, SOListBean soDefaultBean,
                                       SOListBean soListBeanHeader, ArrayList<DealerStockBean> selectedItemList,
                                       String custNo, boolean isReviewScreen, String custName, String mStrCPGUID32, String mStrUID) {
        this.mContext = mContext;
        this.createView = createView;
        this.mStrComingFrom = mStrComingFrom;
        this.isSessionRequired = isSessionRequired;
        this.soDefaultBean = soDefaultBean;
        this.soListBeanHeader = soListBeanHeader;
        this.selectedItemList = selectedItemList;
        this.checkAddItem = checkAddItem;
        this.filterType = filterType;
        this.custNo = custNo;
        this.isReviewScreen = isReviewScreen;
        this.custName = custName;
        this.activity = mContext;
        this.mStrCPGUID32 = mStrCPGUID32;
        this.mStrUID = mStrUID;
    }

    public StockCrtStpTwoPresenterImpl(Activity mContext, StockCrtStpTwoView createView,
                                       String mStrComingFrom, boolean isSessionRequired,
                                       ArrayList<DealerStockBean> stockRevItmList, String custNo,
                                       boolean isReviewScreen, String custName, String mStrCPGUID32) {
        this.mContext = mContext;
        this.createView = createView;
        this.mStrComingFrom = mStrComingFrom;
        this.isSessionRequired = isSessionRequired;
        this.stockRevItmList = stockRevItmList;
        this.custNo = custNo;
        this.custName = custName;
        this.isReviewScreen = isReviewScreen;
        this.activity = mContext;
        this.mStrCPGUID32 = mStrCPGUID32;
    }

    @Override
    public void onStart() {
        if (!isReviewScreen) {
            requestMaterial(soListBeanHeader, "", custNo);
        } else {
            viewReview();
        }

    }

    private void viewReview() {
        if (createView != null) {
            createView.showProgressDialog(mContext.getString(R.string.app_loading));
        }
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (createView != null) {
                    createView.hideProgressDialog();
                    createView.displayList(stockRevItmList);//
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        createView = null;
    }

    @Override
    public void onFilter() {
        if (createView != null) {
            createView.openFilter("", "", filterType, delvStatusId, delvStatusId);
        }
    }

    @Override
    public boolean onSearch(String searchText, Object objects) {
        SOItemBean item = (SOItemBean) objects;
        if (item.getMatNoAndDesc().toLowerCase().contains(searchText.toLowerCase())) {
            return true;
        }
        return false;


    }

    @Override
    public void onSearch(String searchText) {
        searchBeanArrayList.clear();
        if (dlrStockBeanArrayList != null) {
            if (TextUtils.isEmpty(searchText)) {
                searchBeanArrayList.addAll(stockRevItmList);
            } else {
                for (DealerStockBean item : stockRevItmList) {
                    if (item.getMatNoAndDesc().toLowerCase().contains(searchText.toLowerCase())) {
                        searchBeanArrayList.add(item);
                    }

                }
            }
        }
        if (createView != null) {
            createView.displaySearchList(searchBeanArrayList);
        }
    }

    public void removeItem(DealerStockBean item) {
        if (dlrStockBeanArrayList != null)
            dlrStockBeanArrayList.remove(item);
    }

    public void onSearchMaterial(String searchText) {
        searchBeanArrayList.clear();
        if (dlrStockBeanArrayList != null) {
            if (TextUtils.isEmpty(searchText)) {
                searchBeanArrayList.addAll(dlrStockBeanArrayList);
            } else {
                for (DealerStockBean item : dlrStockBeanArrayList) {
                    if (item.getMatNoAndDesc().toLowerCase().contains(searchText.toLowerCase())) {
                        searchBeanArrayList.add(item);
                    }

                }
            }
        }
        if (createView != null) {
            createView.displaySearchList(searchBeanArrayList);
        }
    }


    @Override
    public void validateItem(final int activityRedirectType, RecyclerView recyclerView) {
        if (createView != null) {
            createView.showProgressDialog(mContext.getString(R.string.app_loading));
        }
        final ArrayList<DealerStockBean> soItemCheckedList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (createView != null) {
                            createView.hideProgressDialog();
                            if (!dlrStockBeanArrayList.isEmpty()) {
                                for (DealerStockBean soItemBean : dlrStockBeanArrayList) {
                                    if (TextUtils.isEmpty(soItemBean.getEnterdQty()) || (Integer.parseInt(soItemBean.getEnterdQty()) < 0)) {
                                        createView.displayMessage(mContext.getString(R.string.so_error_enter_valid_qty));
                                        return;
                                    }
                                }
                                DealerStockHeaderBean dealerStockHeaderBean = new DealerStockHeaderBean();
                                dealerStockHeaderBean.setAlStockList(dlrStockBeanArrayList);
                                navigateReviewScreen(dealerStockHeaderBean);
                            } else {
                                createView.displayMessage(mContext.getString(R.string.lbl_no_items_selected));
                            }
                        }
                    }
                });
            }
        }).start();
    }

    private void navigateReviewScreen(DealerStockHeaderBean dealerStockHeaderBean) {
        Intent intent = new Intent(mContext, DealerStockReviewActivity.class);
        DealerStockHeaderBean dealerStockHeader = new DealerStockHeaderBean();
        dealerStockHeader.setAlStockList(dealerStockHeaderBean.getAlStockList());
        dealerStockHeader.setCustomerName(custName);
        dealerStockHeader.setCustomerNumber(custNo);
        intent.putExtra(Constants.EXTRA_SO_HEADER, dealerStockHeader);
        intent.putExtra(Constants.comingFrom, mStrComingFrom);
        intent.putExtra(Constants.CPGUID32, mStrCPGUID32);
        intent.putExtra(Constants.CPUID, mStrUID);
        mContext.startActivity(intent);
    }

    private void locationPerGranted(final ArrayList<DealerStockBean> filteredCrsList) {
        if (createView != null) {
            createView.showProgressDialog(mContext.getString(R.string.gps_progress));
        }
        Constants.getLocation(activity, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                createView.hideProgressDialog();
                if (status) {
                    new onCreateRetailerStockAsyncTask(filteredCrsList).execute();
                }
            }
        });
    }

    @Override
    public void onRequestError(int i, Exception e) {
        currentCount++;
        if (totalCount == currentCount) {
            if (createView != null) {
                createView.hideProgressDialog();
                createView.displayMessage(e.getMessage());
            }
        }
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        currentCount++;
        if (totalCount == currentCount) {
            if (createView != null)
                createView.hideProgressDialog();
            if (i == Operation.Create.getValue()) {
                if (createView != null) {
                    createView.onCreateUpdateSuccess();
                }
            } else if (i == Operation.Update.getValue()) {
                if (createView != null) {
                    createView.onCreateUpdateSuccess();
                }
            }
            Constants.onVisitActivityUpdate(mContext, mStrCPGUID32, mStrVisitActRefID, Constants.DealerStockID, Constants.PrimaryDealerStockCreate, UtilConstants.getOdataDuration());
        }
    }

    //Save All stock items in Offline
    private void saveAllDealerStockItems(ArrayList<DealerStockBean> filteredCrsList) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        String loginIdVal = sharedPreferences.getString(Constants.username, "");

        boolean mBoolOneTimeSavedVisitAct = false;
        mStrVisitActRefID = "";
        for (int i = 0; i < filteredCrsList.size(); i++) {
            Hashtable<String, String> singleItem = new Hashtable<>();
            //  singleItem.put(Constants.LOGINID, loginIdVal.toUpperCase());
            singleItem.put(Constants.Customer, custNo);
            singleItem.put(Constants.CustomerName, custName);
            singleItem.put(Constants.StockOwner, "04");
            // singleItem.put(Constants.StockOwner, "01");
            singleItem.put(Constants.Material, filteredCrsList.get(i).getMaterialNo());
            singleItem.put(Constants.MaterialDesc, filteredCrsList.get(i).getMaterialDesc());
            singleItem.put(Constants.ProdCatg, "");
            singleItem.put(Constants.ProdCatgDesc, "");
            singleItem.put(Constants.Unrestricted, filteredCrsList.get(i).getEnterdQty());
            singleItem.put(Constants.UOM, filteredCrsList.get(i).getUOM());
            singleItem.put(Constants.SkuGroup, filteredCrsList.get(i).getSkuGroup());

            singleItem.put(Constants.SkuGroupDesc, filteredCrsList.get(i).getSkuGroupDesc());
            singleItem.put(Constants.Banner, filteredCrsList.get(i).getBanner());
            singleItem.put(Constants.BannerDesc, filteredCrsList.get(i).getBannerDesc());
            singleItem.put(Constants.Brand, filteredCrsList.get(i).getBrand());
            singleItem.put(Constants.BrandDesc, filteredCrsList.get(i).getBrandDesc());

            singleItem.put(Constants.AsOnDate, UtilConstants.getNewDateTimeFormat());
            singleItem.put(Constants.Etag, filteredCrsList.get(i).getEtag());


            if (TextUtils.isEmpty(filteredCrsList.get(i).getStockValue())) {
                GUID guid = GUID.newRandom();
                singleItem.put(Constants.StockGuid, guid.toString36().toUpperCase());
                try {

                    OfflineManager.createDealerStock(singleItem, this);

                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
                if (!mBoolOneTimeSavedVisitAct) {
                    mStrVisitActRefID = guid.toString36().toUpperCase();
                    mBoolOneTimeSavedVisitAct = true;
                }
            } else {
                singleItem.put(Constants.StockGuid, filteredCrsList.get(i).getStockValue());
                try {

                    OfflineManager.updateDealerStockEntry(singleItem, this);

                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }

                if (!mBoolOneTimeSavedVisitAct) {
                    mStrVisitActRefID = filteredCrsList.get(i).getCPStockItemGUID().toUpperCase();
                    mBoolOneTimeSavedVisitAct = true;
                }
            }

        }

    }

    @Override
    public void saveItem(RecyclerView recyclerView, ArrayList<DealerStockBean> filteredCrsList) {
        totalCount = filteredCrsList.size();
        currentCount = 0;
        locationPerGranted(filteredCrsList);
       /* final ArrayList<DealerStockBean> soItemCheckedList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isValidQty = true;
                try {
                    for (DealerStockBean soItemBean : dlrStockBeanArrayList) {
                        if (soItemBean.isChecked() && !soItemBean.isHide()) {
                            if (Double.parseDouble(soItemBean.getEnterdQty()) <= 0) {
                                isValidQty = false;
                            }
                            soItemCheckedList.add(soItemBean);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final boolean finalIsValidQty = isValidQty;
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (createView != null) {
                            createView.hideProgressDialog();
                            if (!soItemCheckedList.isEmpty()) {
                                if (finalIsValidQty) {

                                } else {
                                    createView.displayMessage(mContext.getString(R.string.so_error_enter_valid_qty));
                                }
                            } else {
                                createView.displayMessage(mContext.getString(R.string.lbl_no_items_selected));
                            }
                        }

                    }
                });
            }
        }).start();*/
    }

    @Override
    public void getCheckedCount() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isValidQty = true;
                int selectedCount = 0;
                try {
                    for (DealerStockBean soItemBean : dlrStockBeanArrayList) {
                        if (soItemBean.isChecked() && !soItemBean.isHide()) {
                            try {
                                if (Double.parseDouble(soItemBean.getEnterdQty()) > 0) {
                                    selectedCount++;
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final int finalSelectedCount = selectedCount;
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (createView != null) {
                            createView.displayTotalSelectedMat(finalSelectedCount);
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == StockCreateStpTwoActivity.INTENT_RESULT_STOCK_CREATE) {
            ArrayList<StocksInfoBean> materialArrayList = new ArrayList<>();
            materialArrayList.addAll((ArrayList<StocksInfoBean>) data.getSerializableExtra(Constants.INTENT_EXTRA_MATERIAL_LIST));
            for (StocksInfoBean stocksInfoBean : materialArrayList) {
                DealerStockBean dealerStockBean = new DealerStockBean();
                dealerStockBean.setMaterialNo(stocksInfoBean.getMaterialNo());
                dealerStockBean.setMaterialDesc(stocksInfoBean.getMaterialDesc());
                dealerStockBean.setUOM(stocksInfoBean.getUOM());
                dealerStockBean.setUnrestrictedQty("0");
                dealerStockBean.setMatNoAndDesc(mContext.getString(R.string.po_details_display_value, stocksInfoBean.getMaterialDesc(), stocksInfoBean.getMaterialNo()));
                dlrStockBeanArrayList.add(dealerStockBean);
            }
            if (createView != null) {
                createView.displayList(dlrStockBeanArrayList);
//                        getCheckedCount();
            }
        }
       /* if (requestCode == ConstantsUtils.ACTIVITY_RESULT_MATERIAL) {
            if (resultCode == ConstantsUtils.ACTIVITY_RESULT_MATERIAL) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    selectedItemList = (ArrayList<DealerStockBean>) bundle.getSerializable(Constants.EXTRA_SO_ITEM_LIST);
                    for (DealerStockBean soItemBean : dlrStockBeanArrayList) {
                        OnlineManager.isCheckedDealerStock(selectedItemList, soItemBean);
//                        selectedItemList.add(soItemBean);
                    }
                    if (createView != null) {
                        createView.displayList(dlrStockBeanArrayList);
//                        getCheckedCount();
                    }
                }

            }
        }*/

    }

    private void requestMaterial(SOListBean soListBeanHeader, String brandId, String customerNo) {
        if (createView != null) {
            createView.showProgressDialog(mContext.getString(R.string.app_loading));
        }

        String sortStr = "&$orderby=" + Constants.AsOnDate + " asc";

        String qry = Constants.Stocks + "?$filter=";
        if (!TextUtils.isEmpty(brandId)) {
            qry = qry + " " + Constants.Brand + " eq '" + brandId + "'";
        }
        if (!TextUtils.isEmpty(customerNo)) {
            qry = qry + " " + Constants.Customer + " eq '" + customerNo + "'";
        }
        qry = qry + sortStr;

        ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 1, ConstantsUtils.SESSION_QRY, this, false);

    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> entities, Bundle bundle) {
        dlrStockBeanArrayList.clear();
        try {
            ArrayList<DealerStockBean> soDefaultItemBeanList = new ArrayList<>();
           /* if (soDefaultBean != null) {
                soDefaultItemBeanList = soDefaultBean.getSoItemBeanArrayList();
            } else if (selectedItemList != null) {
                soDefaultItemBeanList = selectedItemList;
            }*/
            dlrStockBeanArrayList = OnlineManager.getDealerStockMatList(entities, soDefaultItemBeanList, mContext);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (createView != null) {
                    createView.hideProgressDialog();
                    createView.displayList(dlrStockBeanArrayList);
//                    getCheckedCount();
                }
            }
        });
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, String errorMsg, Bundle bundle) {
        showErrorResponse(errorMsg);
    }

    private void showErrorResponse(final String errorMsg) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (createView != null) {
                    createView.hideProgressDialog();
                    createView.displayMessage(errorMsg);
                }
            }
        });
    }

    @Override
    public void startFilter(int requestCode, int resultCode, Intent data) {
        filterType = data.getStringExtra(DateFilterFragment.EXTRA_DEFAULT);
        startDate = data.getStringExtra(DateFilterFragment.EXTRA_START_DATE);
        endDate = data.getStringExtra(DateFilterFragment.EXTRA_END_DATE);
        statusId = data.getStringExtra(BrandFilterActivity.EXTRA_SO_STATUS);
        statusName = data.getStringExtra(BrandFilterActivity.EXTRA_SO_STATUS_NAME);
        delvStatusId = data.getStringExtra(BrandFilterActivity.EXTRA_DELV_STATUS);
        delvStatusName = data.getStringExtra(BrandFilterActivity.EXTRA_DELV_STATUS_NAME);
        displayFilterType();

        requestMaterial(null, delvStatusId, custNo);
    }

    private void displayFilterType() {
        try {
            String statusDesc = "";
            if (!TextUtils.isEmpty(statusId)) {
                statusDesc = ", " + statusName;
            }
            if (!TextUtils.isEmpty(delvStatusId)) {
                statusDesc = statusDesc + ", " + delvStatusName;
            }
            if (createView != null) {
                createView.setFilterDate(statusDesc);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /*AsyncTask to create retailer*/
    public class onCreateRetailerStockAsyncTask extends AsyncTask<Void, Void, Void> {
        ArrayList<DealerStockBean> filteredCrsList;

        onCreateRetailerStockAsyncTask(ArrayList<DealerStockBean> filteredCrsList) {
            this.filteredCrsList = filteredCrsList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            createView.showProgressDialog(mContext.getString(R.string.pop_up_msg_dealer_stock));

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                saveAllDealerStockItems(filteredCrsList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
