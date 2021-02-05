package com.rspl.sf.msfa.reports.plantstock;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.asyncTask.RefreshAsyncTask;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.filter.DateFilterFragment;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.reports.plantstock.filter.StockPresenter;
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
import java.util.List;

/**
 * Created by e10893 on 15-02-2018.
 */

public class PlantStockPersenter implements StockPresenter, OnlineODataInterface,UIListener {

    private Context mContext;
    private PlantStockView createView = null;
    private int comingFrom;
    private boolean isSessionRequired = false;
    private SOListBean soDefaultBean = null;
    private ArrayList<SOItemBean> soItemBeanArrayList = new ArrayList<>();
    private ArrayList<SOItemBean> searchBeanArrayList = new ArrayList<>();
    private SOListBean soListBeanHeader = null;
    private ArrayList<SOItemBean> selectedItemList = null;
    private boolean checkAddItem;
    ArrayList<String> alAssignColl = null;
    private IPlantListViewPresenter iReqListViewPresenter;
    private Activity activity;
    private String startDate = "";
    private String endDate = "";
    private String delvStatusId = "";
    private String statusId = "";
    private String statusName = "";
    private String delvStatusName = "";
    private String filterType="";
    private GUID refguid =null;
    public PlantStockPersenter(boolean checkAddItem, Context mContext, PlantStockView createView, int comingFrom, boolean isSessionRequired, SOListBean soDefaultBean, SOListBean soListBeanHeader, ArrayList<SOItemBean> selectedItemList, IPlantListViewPresenter iReqListViewPresenter, Activity activity) {
        this.mContext = mContext;
        this.createView = createView;
        this.comingFrom = comingFrom;
        this.isSessionRequired = isSessionRequired;
        this.soDefaultBean = soDefaultBean;
        this.soListBeanHeader = soListBeanHeader;
        this.selectedItemList = selectedItemList;
        this.checkAddItem = checkAddItem;
        this.activity=activity;
        this.iReqListViewPresenter = iReqListViewPresenter;
        this.filterType = filterType;
    }

    @Override
    public void onStart() {

        requestMaterial(soListBeanHeader,"");
    }


    @Override
    public void onDestroy() {
        createView = null;
        iReqListViewPresenter=null;
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
        if (soItemBeanArrayList != null) {
            if (TextUtils.isEmpty(searchText)) {
                searchBeanArrayList.addAll(soItemBeanArrayList);
            } else {
                for (SOItemBean item : soItemBeanArrayList) {

//                        boolean isMatID = false;
//                        boolean isMatName = false;
//
//                        if (!TextUtils.isEmpty(searchText)) {
//                            isMatID = item.getMatCode().toLowerCase().contains(searchText.toLowerCase());
//                            isMatName = item.getMatDesc().toLowerCase().contains(searchText.toLowerCase());
//                        } else {
//                            isMatID = true;
//                            isMatName = true;
//                        }
//                        if (isMatID||isMatName)
//                            searchBeanArrayList.add(item);

                    if (item.getSearchField().toLowerCase().contains(searchText.toLowerCase())) {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ConstantsUtils.ACTIVITY_RESULT_MATERIAL) {
            if (resultCode == ConstantsUtils.ACTIVITY_RESULT_MATERIAL) {
                Bundle bundle = data.getExtras();
                if (bundle != null){
                    selectedItemList=(ArrayList<SOItemBean>) bundle.getSerializable(Constants.EXTRA_SO_ITEM_LIST);
                    for (SOItemBean soItemBean : soItemBeanArrayList) {
                        OnlineManager.isCheckedItem(selectedItemList,soItemBean);
                    }
                    if (createView != null) {
                        createView.displayList(soItemBeanArrayList);
                    }
                }

            }
        }
    }

    private void requestMaterial(SOListBean soListBeanHeader, String brand) {
        if (iReqListViewPresenter != null) {
            iReqListViewPresenter.showProgressDialog();

        }
//        String sortStr = "";

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME,0);
        String rollType = sharedPreferences.getString(Constants.USERROLE, "");
        if (rollType.equalsIgnoreCase("Z5")) {
            String qry = Constants.PlantStocks+"?$orderby=MaterialDesc asc &$select=MaterialNo,MaterialDesc,UOM,Unrestricted,PlantID,PlantDesc,Brand";
            if (!TextUtils.isEmpty(brand)) {
                qry = Constants.PlantStocks+"?$filter="+Constants.Brand+" eq '" + brand + "'&$orderby=MaterialDesc asc &$select=MaterialNo,MaterialDesc,UOM,Unrestricted,PlantID,PlantDesc,Brand";
            }
            ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 1, ConstantsUtils.SESSION_QRY, this, true);

        }else {
            String qry = Constants.PlantStocks + "?$select=MaterialNo,MaterialDesc,UOM,Unrestricted,PlantID,PlantDesc,Brand &$orderby=MaterialDesc asc";
            if (!TextUtils.isEmpty(brand)) {
                qry = Constants.PlantStocks + "?$select=MaterialNo,MaterialDesc,UOM,Unrestricted,PlantID,PlantDesc,Brand &$filter=" + Constants.Brand + " eq '" + brand + "'&$orderby=MaterialDesc asc";
            }
                ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 1, ConstantsUtils.SESSION_QRY, this, false);

        }
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> entities, Bundle bundle) {
        soItemBeanArrayList.clear();
        try {
            ArrayList<SOItemBean> soDefaultItemBeanList = new ArrayList<>();
            if (soDefaultBean != null) {
                soDefaultItemBeanList = soDefaultBean.getSoItemBeanArrayList();
            } else if (selectedItemList != null) {
                soDefaultItemBeanList = selectedItemList;
            }
            soItemBeanArrayList = OnlineManager.getMaterialStockList(entities, soDefaultItemBeanList, comingFrom,mContext);
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
                    createView.displayList(soItemBeanArrayList);
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
            alAssignColl.add(Constants.PlantStocks);
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
            if (Constants.iSAutoSync) {
                if (iReqListViewPresenter!=null){
                    iReqListViewPresenter.hideProgressDialog1();
                    createView.displayMessage(mContext.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    refguid = GUID.newRandom();
                    Constants.updateStartSyncTime(mContext,Constants.DownLoad,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(mContext, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (iReqListViewPresenter!=null){
                        iReqListViewPresenter.hideProgressDialog1();
                        createView.displayMessage(e.getMessage());
                    }
                }
            }
        } else {
            if (iReqListViewPresenter!=null){
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
            if (!Constants.isStoreClosed) {
                if (i == Operation.OfflineRefresh.getValue()) {
                    try {
                        /*String syncTime = UtilConstants.getSyncHistoryddmmyyyyTime();
                        for (int incReq = 0; incReq < alAssignColl.size(); incReq++) {

                            String colName = alAssignColl.get(incReq);
                            if (colName.contains("?$")) {
                                String splitCollName[] = colName.split("\\?");
                                colName = splitCollName[0];
                            }

                            Constants.events.updateStatus(Constants.SYNC_TABLE,
                                    colName, Constants.TimeStamp, syncTime
                            );
                        }*/
                        Constants.updateSyncTime(alAssignColl,mContext,Constants.DownLoad,refguid.toString().toUpperCase());
                    } catch (Exception exce) {
                        LogManager.writeLogError(Constants.SyncTableHistory + exce.getMessage());
                    }

                    Constants.isSync = false;
                    if (iReqListViewPresenter != null) {
                        iReqListViewPresenter.hideProgressDialog1();
                        if (!Constants.isStoreClosed) {
                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(
                                    mContext, R.style.MyTheme);
                            builder.setMessage(mContext.getString(R.string.msg_error_occured_during_sync))
                                    .setCancelable(false)
                                    .setPositiveButton(mContext.getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,
                                                                    int id) {

                                                    dialog.cancel();
                                                }
                                            });

                            builder.show();


                        } else {
                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(
                                    mContext, R.style.MyTheme);
                            builder.setMessage(mContext.getString(R.string.msg_sync_terminated))
                                    .setCancelable(false)
                                    .setPositiveButton(mContext.getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,
                                                                    int id) {

                                                    dialog.cancel();

                                                }
                                            });

                            builder.show();
                        }
                    }
                }
            }

        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(mContext)) {
                Constants.isSync = true;
                if (createView != null) {
                    iReqListViewPresenter.showProgressDialog();
                }
                new RefreshAsyncTask(mContext, "", this).execute();
            } else {
                Constants.isSync = false;
                if (createView != null) {
                    iReqListViewPresenter.hideProgressDialog1();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), mContext);
                }
            }
        } else {
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
                Constants.updateLastSyncTimeToTable(alAssignColl,mContext,Constants.DownLoad,refguid.toString().toUpperCase());
                ConstantsUtils.startAutoSync(mContext,false);
                Constants.isSync = false;
                if (iReqListViewPresenter != null) {
                    iReqListViewPresenter.hideProgressDialog1();
                    iReqListViewPresenter.ProductListFresh();
                }
            }else if (i == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
                Constants.isSync = false;
                try {
                    OfflineManager.getAuthorizations(mContext);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                Constants.setSyncTime(mContext,refguid.toString().toUpperCase());
                ConstantsUtils.startAutoSync(mContext,false);
                if (iReqListViewPresenter != null) {
                    iReqListViewPresenter.hideProgressDialog1();
                    iReqListViewPresenter.ProductListFresh();
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

        requestMaterial(null,delvStatusId);
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
