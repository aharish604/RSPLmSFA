package com.rspl.sf.msfa.socreate.stepTwo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.filter.DateFilterFragment;
import com.rspl.sf.msfa.interfaces.AsyncTaskCallBack;
import com.rspl.sf.msfa.soapproval.OpenOnlineManagerStore;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.socreate.filter.BrandFilterActivity;
import com.rspl.sf.msfa.socreate.shipToDetails.ShipToDetailsActivity;
import com.rspl.sf.msfa.socreate.stepThree.SOQuantityActivity;
import com.rspl.sf.msfa.socreate.viewSelectedMaterial.ViewSelectedMaterialActivity;
import com.rspl.sf.msfa.solist.SOListBean;
import com.rspl.sf.msfa.store.OnlineManager;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by e10769 on 30-06-2017.
 */

public class SOCrtStpTwoPresenterImpl implements SOCrtStpTwoPresenter, OnlineODataInterface {
    private Context mContext;
    private String filterType = "";
    private SOCrtStpTwoView createView = null;
    private int comingFrom;
    private boolean isSessionRequired = false;
    private SOListBean soDefaultBean = null;
    private ArrayList<SOItemBean> soItemBeanArrayList = new ArrayList<>();
    private ArrayList<SOItemBean> searchBeanArrayList = new ArrayList<>();
    private SOListBean soListBeanHeader = null;
    private ArrayList<SOItemBean> selectedItemList = null;
    private boolean checkAddItem;
    private String startDate = "";
    private String endDate = "";
    private String delvStatusId = "";
    private String statusId = "";
    private String statusName = "";
    private String delvStatusName = "";
    private String mstrCustomerNo = "";
    private String searchTexts = "";

    public SOCrtStpTwoPresenterImpl(boolean checkAddItem, Context mContext, SOCrtStpTwoView createView,
                                    int comingFrom, boolean isSessionRequired, SOListBean soDefaultBean,
                                    SOListBean soListBeanHeader, ArrayList<SOItemBean> selectedItemList, String mstrCustomerNo) {
        this.mContext = mContext;
        this.createView = createView;
        this.comingFrom = comingFrom;
        this.isSessionRequired = isSessionRequired;
        this.soDefaultBean = soDefaultBean;
        this.soListBeanHeader = soListBeanHeader;
        this.selectedItemList = selectedItemList;
        this.checkAddItem = checkAddItem;
        this.filterType = filterType;
        this.mstrCustomerNo = mstrCustomerNo;
    }

    @Override
    public void onStart() {
        requestMaterial(false);
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
        searchTexts = searchText;
        requestFilterMaterial(delvStatusId, searchTexts);
    }

    @Override
    public void validateItem(final int activityRedirectType, RecyclerView recyclerView) {
        if (createView != null) {
            createView.showProgressDialog(mContext.getString(R.string.app_loading));
        }
        final ArrayList<SOItemBean> soItemCheckedList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isValidQty = true;
                BigDecimal totalAmount = new BigDecimal("0");
                BigDecimal totalNetWeight = new BigDecimal("0");
                BigDecimal totalQty = new BigDecimal("0");
                String netWeightUOM = "";
                String strCurrency = "";
                try {
                    Constants.MapMatGrpByMaterial.clear();
                    for (SOItemBean soItemBean : soItemBeanArrayList) {
                        if (soItemBean.isChecked() && !soItemBean.isHide()) {
                            if (TextUtils.isEmpty(soItemBean.getSoQty()) || Double.parseDouble(soItemBean.getSoQty()) <= 0) {
                                isValidQty = false;
                                if(soItemBean.isRemoved()){
                                    isValidQty = true;
                                }
                            } else {
                                if(soItemBean.getHighLevellItemNo().equalsIgnoreCase("000000")){
                                    BigDecimal bgItemValue = new BigDecimal(soItemBean.getLandingPrice()).multiply(new BigDecimal(soItemBean.getSoQty()));
                                    soItemBean.setNetAmount(String.valueOf(bgItemValue));
                                    totalAmount = totalAmount.add(bgItemValue);
                                    totalNetWeight = totalNetWeight.add(new BigDecimal(soItemBean.getNetWeight()));
                                    totalQty = totalQty.add(new BigDecimal(soItemBean.getSoQty()));
                                    netWeightUOM = soItemBean.getNetWeightUOM();
                                    strCurrency = soItemBean.getCurrency();
                                }
                            }
                            soItemCheckedList.add(soItemBean);
                            Constants.MapMatGrpByMaterial.put(soItemBean.getMatCode(),soItemBean);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (isValidQty) {
                    soListBeanHeader.setTotalAmt(String.valueOf(totalAmount));
                    soListBeanHeader.setmStrTotalWeight(String.valueOf(totalNetWeight));
                    soListBeanHeader.setQuantity(String.valueOf(totalQty));
                    soListBeanHeader.setmStrWeightUOM(netWeightUOM);
                    soListBeanHeader.setCurrency(strCurrency);
                }

                ArrayList<SOItemBean> sortingEmtyItemNo= new ArrayList<>();
                ArrayList<SOItemBean> sortingItemNos= new ArrayList<>();
                if(soItemCheckedList!=null && soItemCheckedList.size()>0){
                    for(SOItemBean dataLoop : soItemCheckedList){
                        if(TextUtils.isEmpty(dataLoop.getItemNo())){
                            sortingEmtyItemNo.add(dataLoop);
                        }else{
                            sortingItemNos.add(dataLoop);
                        }
                    }
                }



                Collections.sort(sortingItemNos, new Comparator<SOItemBean>() {
                    @Override
                    public int compare(SOItemBean one, SOItemBean two) {
                        return one.getItemNo().compareTo(two.getItemNo());
                    }
                });
                sortingItemNos.addAll(sortingEmtyItemNo);
                soItemCheckedList.clear();
                soItemCheckedList.addAll(sortingItemNos);

                final boolean finalIsValidQty = isValidQty;
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (createView != null) {
                            createView.hideProgressDialog();
                            if (!soItemCheckedList.isEmpty()) {
                                if (finalIsValidQty) {
                                    if (activityRedirectType == ConstantsUtils.SO_VIEW_SELECTED_MATERIAL) {
                                        Intent intent = new Intent(mContext, ViewSelectedMaterialActivity.class);
                                        intent.putExtra(Constants.EXTRA_SO_ITEM_LIST, soItemCheckedList);
                                        if (soDefaultBean != null) {
                                            intent.putExtra(Constants.EXTRA_SO_HEADER, soDefaultBean);
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
                                            intent.putExtra(Constants.EXTRA_SO_HEADER, soDefaultBean);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isValidQty = true;
                int selectedCount = 0;
                try {
                    for (SOItemBean soItemBean : soItemBeanArrayList) {
                        if (soItemBean.isChecked() && !soItemBean.isHide()) {
                            try {
                                if (!TextUtils.isEmpty(soItemBean.getSoQty()) && Double.parseDouble(soItemBean.getSoQty()) > 0) {
                                    selectedCount++;
                                }else if(TextUtils.isEmpty(soItemBean.getSoQty()) && soItemBean.isNotnew()){
//                                    soItemBean.setSoQty(soItemBean.getEditAndApproveQty());
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
        if (requestCode == ConstantsUtils.ACTIVITY_RESULT_MATERIAL) {
            if (resultCode == ConstantsUtils.ACTIVITY_RESULT_MATERIAL) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    selectedItemList = (ArrayList<SOItemBean>) bundle.getSerializable(Constants.EXTRA_SO_ITEM_LIST);
                    for (SOItemBean soItemBean : soItemBeanArrayList) {
                        if(!TextUtils.isEmpty(soItemBean.getHighLevellItemNo()) && soItemBean.getHighLevellItemNo().equalsIgnoreCase("000000"))
                            OnlineManager.isCheckedItem(selectedItemList, soItemBean);
//                        selectedItemList.add(soItemBean);
                    }
//                    if (createView != null) {
//                        createView.displayList(soItemBeanArrayList);
                    getCheckedCount();
                    requestFilterMaterial(delvStatusId, searchTexts);
//                    }
                }

            }
        }
    }

    private void requestMaterial(boolean withoutCustomer) {

        try{
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
            if (sharedPreferences.getBoolean("writeDBGLog", false)) {
                Constants.writeDebug = sharedPreferences.getBoolean("writeDBGLog", false);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        if (createView != null) {
            createView.showProgressDialog(mContext.getString(R.string.app_loading));
        }
        String qry = "";
//        if (withoutCustomer) {
//            qry = Constants.MaterialByCustomers + "?$orderby=MaterialDesc asc";
//        } else {
//            qry = Constants.MaterialByCustomers + "?$filter=CustomerNo eq '" + mstrCustomerNo + "'&$orderby=MaterialDesc asc";
        Constants.mapMatGrpByMaterial.clear();
//            qry = Constants.MaterialByCustomers + "?$filter=PlantID eq '" + soListBeanHeader.getPlant() + "'&$orderby=MaterialDesc asc";
        qry = Constants.MaterialByCustomers + "?$filter=PlantID eq '" + soListBeanHeader.getPlant() +"' and DivisionID eq '" +soListBeanHeader.getDivison()+"' and DistChannelID eq '" +soListBeanHeader.getDistChannelID()+"' and SalesOrgID eq '" +soListBeanHeader.getSalesOrgID()+"'&$orderby=MaterialDesc asc";
//        }
        if(Constants.getRollID(mContext)){
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
                            soItemBeanArrayList = OnlineManager.getSOMaterialList(jsonArray, soDefaultItemBeanList, comingFrom, mContext);
                            if(soDefaultItemBeanList!=null && soDefaultItemBeanList.size()>0){
                                for (SOItemBean itemList : soDefaultItemBeanList){
//                    if(!TextUtils.isEmpty(itemList.getItemCategory()) && itemList.getItemCategory().equalsIgnoreCase("TANN")){
                                    if(!TextUtils.isEmpty(itemList.getHighLevellItemNo()) && !itemList.getHighLevellItemNo().equalsIgnoreCase("000000")){
                                        itemList.setChecked(true);
                                        soItemBeanArrayList.add(itemList);
                                    }
                                }
                            }
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (createView != null) {
                   /* if (soItemBeanArrayList.isEmpty()) {
                        createView.hideProgressDialog();
                        requestMaterial(true);
                    } else {*/
                                    createView.hideProgressDialog();
                                    createView.displayList(soItemBeanArrayList);
                                    getCheckedCount();
//                    }
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
                   /* if (soItemBeanArrayList.isEmpty()) {
                        createView.hideProgressDialog();
                        requestMaterial(true);
                    } else {*/
                                        createView.hideProgressDialog();
                                        createView.displayList(soItemBeanArrayList);
                                        createView.displayMessage(finalErrorMsg);
                                        getCheckedCount();
//                    }
                                    }
                                }
                            });
                            LogManager.writeLogError(errorMsg);
                        } catch (Throwable e) {
                            e.printStackTrace();
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (createView != null) {
                   /* if (soItemBeanArrayList.isEmpty()) {
                        createView.hideProgressDialog();
                        requestMaterial(true);
                    } else {*/
                                        createView.hideProgressDialog();
                                        createView.displayList(soItemBeanArrayList);
                                        createView.displayMessage(e.getMessage());
                                        getCheckedCount();
//                    }
                                    }
                                }
                            });
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
                   /* if (soItemBeanArrayList.isEmpty()) {
                        createView.hideProgressDialog();
                        requestMaterial(true);
                    } else {*/
                                createView.hideProgressDialog();
                                createView.displayList(soItemBeanArrayList);
                                createView.displayMessage(finalErrormessage);
                                getCheckedCount();
//                    }
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
                   /* if (soItemBeanArrayList.isEmpty()) {
                        createView.hideProgressDialog();
                        requestMaterial(true);
                    } else {*/
                            createView.hideProgressDialog();
                            createView.displayList(soItemBeanArrayList);
                            createView.displayMessage(e.getMessage());
                            getCheckedCount();
//                    }
                        }
                    }
                });
            }



            try {
                if(Constants.writeDebug){
                    LogManager.writeLogDebug("So Create loading materials : Store opening ");
                }
                ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 1, ConstantsUtils.SESSION_QRY, this, true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            if(Constants.writeDebug){
                LogManager.writeLogDebug("So Create loading materials : URL "+qry);
            }
            ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 1, ConstantsUtils.SESSION_QRY, this, false);
        }

    }

    private void openOnlineStore(final String qry) {
        //optional store open
        if (UtilConstants.isNetworkAvailable(mContext)) {
            new OpenOnlineManagerStore(mContext, new AsyncTaskCallBack() {
                @Override
                public void onStatus(boolean status, String values) {
                    if (status) {
                        ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 1, ConstantsUtils.SESSION_QRY, SOCrtStpTwoPresenterImpl.this, true);
                    }
                }
            }).execute();
        }
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> entities, Bundle bundle) {
        soItemBeanArrayList.clear();
        if(Constants.writeDebug)
            LogManager.writeLogDebug("SO Create: Requesting Materials Success ");
        try {
            ArrayList<SOItemBean> soDefaultItemBeanList = new ArrayList<>();
            if (soDefaultBean != null) {
                soDefaultItemBeanList = soDefaultBean.getSoItemBeanArrayList();
            } else if (selectedItemList != null) {
                soDefaultItemBeanList = selectedItemList;
            }
            soItemBeanArrayList = OnlineManager.getSOMaterialList(entities, soDefaultItemBeanList, comingFrom, mContext);
            if(soDefaultItemBeanList!=null && soDefaultItemBeanList.size()>0){
                for (SOItemBean itemList : soDefaultItemBeanList){
//                    if(!TextUtils.isEmpty(itemList.getItemCategory()) && itemList.getItemCategory().equalsIgnoreCase("TANN")){
                    if(!TextUtils.isEmpty(itemList.getHighLevellItemNo()) && !itemList.getHighLevellItemNo().equalsIgnoreCase("000000")){
                        itemList.setChecked(true);
                        soItemBeanArrayList.add(itemList);
                    }
                }
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (createView != null) {
                   /* if (soItemBeanArrayList.isEmpty()) {
                        createView.hideProgressDialog();
                        requestMaterial(true);
                    } else {*/
                    createView.hideProgressDialog();
                    createView.displayList(soItemBeanArrayList);
                    getCheckedCount();
//                    }
                }
            }
        });
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, String errorMsg, Bundle bundle) {
        LogManager.writeLogDebug("SO Create: Requesting Materials Failed "+errorMsg);
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

        requestFilterMaterial(delvStatusId, searchTexts);
    }

    private void requestFilterMaterial(String delvStatusId, String searchText) {
        searchBeanArrayList.clear();
        if (soItemBeanArrayList != null) {
            if (TextUtils.isEmpty(searchText) && TextUtils.isEmpty(delvStatusId)) {
                searchBeanArrayList.addAll(soItemBeanArrayList);
            } else {
                for (SOItemBean item : soItemBeanArrayList) {
                    boolean isBrandFilter = false;
                    boolean isSearchFilter = false;
                    if (!TextUtils.isEmpty(delvStatusId) && item.getBrand().equals(delvStatusId)) {
                        isBrandFilter = true;
                    } else if (TextUtils.isEmpty(delvStatusId)) {
                        isBrandFilter = true;
                    }

                    if (item.getMatNoAndDesc().toLowerCase().contains(searchText.toLowerCase())) {
                        isSearchFilter = true;
                    } else if (TextUtils.isEmpty(searchText)) {
                        isSearchFilter = true;
                    }
                    if (isBrandFilter && isSearchFilter)
                        searchBeanArrayList.add(item);

                }
            }
        }
        if (createView != null) {
            createView.displaySearchList(searchBeanArrayList);
        }
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
}
