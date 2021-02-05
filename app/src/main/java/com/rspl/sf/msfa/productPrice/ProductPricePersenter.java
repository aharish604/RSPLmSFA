package com.rspl.sf.msfa.productPrice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.rspl.sf.msfa.BuildConfig;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.asyncTask.RefreshAsyncTask;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.filter.DateFilterFragment;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.socreate.filter.BrandFilterActivity;
import com.rspl.sf.msfa.socreate.shipToDetails.ShipToDetailsActivity;
import com.rspl.sf.msfa.socreate.stepThree.SOQuantityActivity;
import com.rspl.sf.msfa.socreate.stepTwo.SOCrtStpTwoPresenter;
import com.rspl.sf.msfa.socreate.stepTwo.SOCrtStpTwoView;
import com.rspl.sf.msfa.socreate.viewSelectedMaterial.ViewSelectedMaterialActivity;
import com.rspl.sf.msfa.solist.SOListBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.store.OnlineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10893 on 15-02-2018.
 */

public class ProductPricePersenter implements SOCrtStpTwoPresenter, OnlineODataInterface, UIListener {

    ArrayList<String> alAssignColl = null;
    private Context mContext;
    private SOCrtStpTwoView createView = null;
    private int comingFrom;
    private boolean isSessionRequired = false;
    private SOListBean soDefaultBean = null;
    private ArrayList<SOItemBean> soItemBeanArrayList = new ArrayList<>();
    private ArrayList<SOItemBean> searchBeanArrayList = new ArrayList<>();
    private SOListBean soListBeanHeader = null;
    private ArrayList<SOItemBean> selectedItemList = null;
    private boolean checkAddItem;
    private IProductListViewPresenter iReqListViewPresenter;
    private Activity activity;
    private String startDate = "";
    private String endDate = "";
    private String delvStatusId = "";
    private String statusId = "";
    private String statusName = "";
    private String delvStatusName = "";
    private String filterType = "";
    private String searchText="";
    private GUID refguid =null;

    public ProductPricePersenter(boolean checkAddItem, Context mContext, SOCrtStpTwoView createView, int comingFrom, boolean isSessionRequired, SOListBean soDefaultBean, SOListBean soListBeanHeader, ArrayList<SOItemBean> selectedItemList, IProductListViewPresenter iReqListViewPresenter, Activity activity) {
        this.mContext = mContext;
        this.createView = createView;
        this.comingFrom = comingFrom;
        this.isSessionRequired = isSessionRequired;
        this.soDefaultBean = soDefaultBean;
        this.soListBeanHeader = soListBeanHeader;
        this.selectedItemList = selectedItemList;
        this.checkAddItem = checkAddItem;
        this.activity = activity;
        this.iReqListViewPresenter = iReqListViewPresenter;
        this.filterType = filterType;
    }

    @Override
    public void onStart() {

//        soCrtStpTwoModel.findItems(mContext,this,comingFrom, soListBeanHeader);
        requestMaterial(soListBeanHeader, "");
//        ArrayList<SOItemBean> soMaterial = soListBeanHeader.getSoItemBeanArrayList();
//        if (createView != null) {
//            createView.displayList(soMaterial);
//        }
    }


    @Override
    public void onDestroy() {
        createView = null;
        iReqListViewPresenter = null;
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
        this.searchText=searchText;
        searchBeanArrayList.clear();
        if (soItemBeanArrayList != null) {
            if (TextUtils.isEmpty(searchText)) {
                searchBeanArrayList.addAll(soItemBeanArrayList);
            } else {
                for (SOItemBean item : soItemBeanArrayList) {
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
            // createView.showProgressDialog(mContext.getString(R.string.app_loading));
            iReqListViewPresenter.showProgressDialog();
        }
        final ArrayList<SOItemBean> soItemCheckedList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isValidQty = true;
                try {
                    for (SOItemBean soItemBean : soItemBeanArrayList) {
                        if (soItemBean.isChecked() && !soItemBean.isHide()) {
                            if (Double.parseDouble(soItemBean.getSoQty()) <= 0) {
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
                            //createView.hideProgressDialog();
                            iReqListViewPresenter.hideProgressDialog1();
                            if (!soItemCheckedList.isEmpty()) {
                                if (finalIsValidQty) {
                                    if (activityRedirectType == ConstantsUtils.SO_VIEW_SELECTED_MATERIAL) {
                                        Intent intent = new Intent(mContext, ViewSelectedMaterialActivity.class);
                                        intent.putExtra(Constants.EXTRA_SO_ITEM_LIST, soItemCheckedList);
                                        if (soDefaultBean != null) {
                                            ArrayList<SOItemBean> defaultItemList = soDefaultBean.getSoItemBeanArrayList();
                                            intent.putExtra(Constants.EXTRA_SO_HEADER, defaultItemList);
                                        }
                                        soListBeanHeader.setSoItemBeanArrayList(soItemCheckedList);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra(Constants.EXTRA_SESSION_REQUIRED, isSessionRequired);
                                        intent.putExtra(Constants.EXTRA_HEADER_BEAN, soListBeanHeader);
                                        intent.putExtra(Constants.EXTRA_COME_FROM, comingFrom);
                                        ((Activity) mContext).startActivityForResult(intent, ConstantsUtils.ACTIVITY_RESULT_MATERIAL);
                                    } else if (ConstantsUtils.ADD_MATERIAL != activityRedirectType) {
                                        Intent intent = new Intent(mContext, ShipToDetailsActivity.class);
                                        intent.putExtra(Constants.EXTRA_SO_ITEM_LIST, soItemCheckedList);
                                        if (soDefaultBean != null) {
                                            ArrayList<SOItemBean> defaultItemList = soDefaultBean.getSoItemBeanArrayList();
                                            intent.putExtra(Constants.EXTRA_SO_HEADER, defaultItemList);
                                        }
                                        soListBeanHeader.setSoItemBeanArrayList(soItemCheckedList);
                                        intent.putExtra(Constants.EXTRA_HEADER_BEAN, soListBeanHeader);
                                        intent.putExtra(Constants.EXTRA_COME_FROM, comingFrom);
                                        mContext.startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(mContext, SOQuantityActivity.class);
                                        intent.putExtra(Constants.EXTRA_SO_ITEM_LIST, soItemCheckedList);
                                        if (soDefaultBean != null) {
                                            ArrayList<SOItemBean> defaultItemList = soDefaultBean.getSoItemBeanArrayList();
                                            intent.putExtra(Constants.EXTRA_SO_HEADER, defaultItemList);
                                            soListBeanHeader.setSONo(soDefaultBean.getSONo());
                                        }
                                        intent.putExtra(Constants.EXTRA_HEADER_BEAN, soListBeanHeader);
                                        intent.putExtra(Constants.EXTRA_COME_FROM, activityRedirectType);
                                        intent.putExtra(Constants.CHECK_ADD_MATERIAL_ITEM, checkAddItem);
                                        ((Activity) mContext).setResult(ConstantsUtils.ACTIVITY_RESULT_MATERIAL, intent);
                                        ((Activity) mContext).finish();
                                    }
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
        }).start();
    }

    @Override
    public void getCheckedCount() {
       /* new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isValidQty = true;
                int selectedCount = 0;
                try {
                    for (SOItemBean soItemBean : soItemBeanArrayList) {
                        if (soItemBean.isChecked() && !soItemBean.isHide()) {
                            try {
                                if (Double.parseDouble(soItemBean.getSoQty()) > 0) {
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
        }).start();*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ConstantsUtils.ACTIVITY_RESULT_MATERIAL) {
            if (resultCode == ConstantsUtils.ACTIVITY_RESULT_MATERIAL) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    selectedItemList = (ArrayList<SOItemBean>) bundle.getSerializable(Constants.EXTRA_SO_ITEM_LIST);
                    for (SOItemBean soItemBean : soItemBeanArrayList) {
                        OnlineManager.isCheckedItem(selectedItemList, soItemBean);
//                        selectedItemList.add(soItemBean);
                    }
                    if (createView != null) {
                        onSearch(searchText);
                    }
                }

            }
        }
    }

    private void requestMaterial(SOListBean soListBeanHeader, String brand) {
        if (iReqListViewPresenter != null) {
            iReqListViewPresenter.showProgressDialog();

        }
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME,0);
        String rollType = sharedPreferences.getString(Constants.USERROLE, "");
        if (rollType.equalsIgnoreCase("Z5")) {
            String qry = Constants.MaterialByCustomers+"?$orderby=CustomerNo%20asc%20&$select=Currency,LandingPrice,BasePrice,MaterialDesc,MaterialNo,BaseUOM";
            if (!TextUtils.isEmpty(brand)) {
                qry = Constants.MaterialByCustomers+"?$filter=" + Constants.Brand + "%20eq%20'" + brand + "'&$orderby=LandingPrice%20desc%20&$select=Currency,LandingPrice,BasePrice,MaterialDesc,MaterialNo,BaseUOM";
            }
            Log.d("ProductPrice", "requestMaterial: stat");


            try {
                OnlineManager.doOnlineGetRequest(qry, mContext, iReceiveEvent -> {
                    if (iReceiveEvent.getResponseStatusCode() == 200) {
                        JSONObject jsonObject = OnlineManager.getJSONBody(iReceiveEvent);
                        JSONArray jsonArray = OnlineManager.getJSONArrayBody(jsonObject);
                        soItemBeanArrayList.clear();
                        Log.d("ProductPrice", "requestMaterial: response"+jsonArray.length());
                        try {
                            ArrayList<SOItemBean> soDefaultItemBeanList = new ArrayList<>();
                            if (soDefaultBean != null) {
                                soDefaultItemBeanList = soDefaultBean.getSoItemBeanArrayList();
                            } else if (selectedItemList != null) {
                                soDefaultItemBeanList = selectedItemList;
                            }
                            soItemBeanArrayList = OnlineManager.getMaterialPriceList(jsonArray, soDefaultItemBeanList, comingFrom, mContext);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (createView != null) {
                                    iReqListViewPresenter.hideProgressDialog1();
                                    //createView.hideProgressDialog();
                                    iReqListViewPresenter.ProductListFresh();
                                    Log.d("ProductPrice", "requestMaterial:  display UI");
                                    onSearch(searchText);
//                    createView.displayList(soItemBeanArrayList);
//                    getCheckedCount();
                                }
                            }
                        });
                    }else {
                        String errorMsg="";
                        try {
                            errorMsg = Constants.getErrorMessage(iReceiveEvent,mContext);
                            String finalErrorMsg = errorMsg;
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (createView != null) {
                                        iReqListViewPresenter.hideProgressDialog1();
                                        //createView.hideProgressDialog();
                                        iReqListViewPresenter.ProductListFresh();
                                        Log.d("ProductPrice", "requestMaterial:  display UI");
                                        onSearch(searchText);
                                        iReqListViewPresenter.displayMsg(finalErrorMsg);
                                    }
                                }
                            });
                            LogManager.writeLogError(errorMsg);
                        } catch (Throwable e) {
                            e.printStackTrace();
                            iReqListViewPresenter.displayMsg(e.getMessage());
                            LogManager.writeLogError(e.getMessage());
                        }
                    }
                }, e -> {
                    e.printStackTrace();
                    String errormessage = "";
                    errormessage = ConstantsUtils.geterrormessageForInternetlost(e.getMessage(),mContext);
                    if(TextUtils.isEmpty(errormessage)){
                        errormessage = e.getMessage();
                    }
                    String finalErrormessage = errormessage;
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (createView != null) {
                                iReqListViewPresenter.hideProgressDialog1();
                                //createView.hideProgressDialog();
                                iReqListViewPresenter.ProductListFresh();
                                Log.d("ProductPrice", "requestMaterial:  display UI");
                                onSearch(searchText);
                                iReqListViewPresenter.displayMsg(finalErrormessage);
//                    createView.displayList(soItemBeanArrayList);
//                    getCheckedCount();
                            }
                        }
                    });
                });
            } catch (Exception e) {
                e.printStackTrace();
                ConstantsUtils.printErrorLog(e.getMessage());
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (createView != null) {
                            iReqListViewPresenter.hideProgressDialog1();
                            //createView.hideProgressDialog();
                            iReqListViewPresenter.ProductListFresh();
                            Log.d("ProductPrice", "requestMaterial:  display UI");
                            onSearch(searchText);
                            iReqListViewPresenter.displayMsg(e.getMessage());
//                    createView.displayList(soItemBeanArrayList);
//                    getCheckedCount();
                        }
                    }
                });
            }



//            ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 1, ConstantsUtils.SESSION_QRY, this, true);

        }else {
            String qry = Constants.MaterialByCustomers+"?$select=Currency,LandingPrice,BasePrice,MaterialDesc,MaterialNo,BaseUOM &$orderby=CustomerNo asc";
            if (!TextUtils.isEmpty(brand)) {
//                qry = Constants.PlantStocks+"?$select=MaterialNo,MaterialDesc,UOM,Unrestricted,PlantID,PlantDesc,Brand &$filter="+Constants.Brand+" eq '" + brand + "'&$orderby=MaterialDesc asc";
                qry = Constants.MaterialByCustomers+"?$filter=" + Constants.Brand + " eq '" + brand + "'&$orderby=LandingPrice desc &$select=Currency,LandingPrice,BasePrice,MaterialDesc,MaterialNo,BaseUOM";
            }
            Log.d("ProductPrice", "requestMaterial: stat");
            ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 1, ConstantsUtils.SESSION_QRY, this, false);
        }
//




    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> entities, Bundle bundle) {
        soItemBeanArrayList.clear();
        Log.d("ProductPrice", "requestMaterial: response"+entities.size());
        try {
            ArrayList<SOItemBean> soDefaultItemBeanList = new ArrayList<>();
            if (soDefaultBean != null) {
                soDefaultItemBeanList = soDefaultBean.getSoItemBeanArrayList();
            } else if (selectedItemList != null) {
                soDefaultItemBeanList = selectedItemList;
            }
            soItemBeanArrayList = OnlineManager.getMaterialPriceList(entities, soDefaultItemBeanList, comingFrom, mContext);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (createView != null) {
                    iReqListViewPresenter.hideProgressDialog1();
                    //createView.hideProgressDialog();
                    iReqListViewPresenter.ProductListFresh();
                    Log.d("ProductPrice", "requestMaterial:  display UI");
                    onSearch(searchText);
//                    createView.displayList(soItemBeanArrayList);
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
                    // createView.hideProgressDialog();
                    iReqListViewPresenter.hideProgressDialog1();
                    createView.displayMessage(errorMsg);
                }
            }
        });
    }

    public void onRefresh() {
        onRefreshSOrder();
    }

    private void onRefreshSOrder() {
        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(mContext)) {
            alAssignColl.clear();
            concatCollectionStr = "";
            alAssignColl.add(Constants.MaterialByCustomers);
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
            if (Constants.iSAutoSync) {
                if (iReqListViewPresenter != null) {
                    iReqListViewPresenter.hideProgressDialog1();
                    createView.displayMessage(mContext.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    refguid = GUID.newRandom();
                    Constants.updateStartSyncTime(mContext,Constants.ProdPrc_sync,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(mContext, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (iReqListViewPresenter != null) {
                        iReqListViewPresenter.hideProgressDialog1();
                        createView.displayMessage(e.getMessage());
                    }
                }
            }
        } else {
            if (iReqListViewPresenter != null) {
                iReqListViewPresenter.hideProgressDialog1();
                createView.displayMessage(mContext.getString(R.string.no_network_conn));
            }
        }
    }

    @Override
    public void onRequestError(int i, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(i, e, mContext);
        if (errorBean.hasNoError()) {
            //isErrorFromBackend = true;
            if (i == Operation.OfflineRefresh.getValue()) {
                Constants.isSync = false;
                if (iReqListViewPresenter != null) {
                    iReqListViewPresenter.hideProgressDialog1();
                    iReqListViewPresenter.displayMsg(mContext.getString(R.string.msg_error_occured_during_sync));
                }
            }else if (i == Operation.GetStoreOpen.getValue()){
                Constants.isSync = false;
                if (iReqListViewPresenter != null) {
                    iReqListViewPresenter.hideProgressDialog1();
                    iReqListViewPresenter.displayMsg(mContext.getString(R.string.msg_error_occured_during_sync));
                }
            }

        }else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(mContext)) {
                Constants.isSync = true;
                if (createView != null) {
                    iReqListViewPresenter.showProgressDialog();
                }
                new RefreshAsyncTask(mContext, "", this).execute();
            } else {
                Constants.isSync = false;
                if (mContext != null) {
                    iReqListViewPresenter.hideProgressDialog1();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), mContext);
                }
            }
        }  else {
            Constants.isSync = false;
            if (iReqListViewPresenter != null) {
                iReqListViewPresenter.hideProgressDialog1();
                Constants.displayMsgReqError(errorBean.getErrorCode(), mContext);
            }
        }

    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        if (!Constants.isStoreClosed) {
            if (i == Operation.OfflineRefresh.getValue()) {
                Constants.updateLastSyncTimeToTable(alAssignColl,mContext,Constants.ProdPrc_sync,refguid.toString().toUpperCase());
                ConstantsUtils.startAutoSync(mContext,false);
//                ConstantsUtils.serviceReSchedule(mContext, true);
                Constants.isSync = false;
                if (iReqListViewPresenter != null) {
                    iReqListViewPresenter.hideProgressDialog1();
                    requestMaterial(null, delvStatusId);
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false);
                }

            }else if (i == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
                Constants.isSync = false;
                try {
                    OfflineManager.getAuthorizations(mContext);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                Constants.setSyncTime(mContext,refguid.toString().toUpperCase());
//                ConstantsUtils.serviceReSchedule(mContext, true);
                ConstantsUtils.startAutoSync(mContext,false);
                if (iReqListViewPresenter != null) {
                    iReqListViewPresenter.hideProgressDialog1();
                    requestMaterial(null, delvStatusId);
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false);
                }
            }

        }
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

        requestMaterial(null, delvStatusId);
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

    @Override
    public void onFilter() {
        if (createView != null) {
            createView.openFilter("", "", filterType, delvStatusId, delvStatusId);
        }
    }
}
