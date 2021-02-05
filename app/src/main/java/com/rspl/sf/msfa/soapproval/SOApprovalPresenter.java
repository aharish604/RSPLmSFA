package com.rspl.sf.msfa.soapproval;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.solist.SOListBean;
import com.rspl.sf.msfa.store.GetOnlineODataInterface;
import com.rspl.sf.msfa.store.OnlineManager;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SOApprovalPresenter implements SOApprovalPresenterView,GetOnlineODataInterface {
    private Context context;
    private SOApprovalView approvalView;
    private ArrayList<SOListBean> soBeanList = new ArrayList<>(), soBeanSearchList = new ArrayList<>();
    private String searchText="";
    private int comingFrom = 0;
    private static final String TAG = "SOApprovalPresenter";
    public SOApprovalPresenter(Context context, SOApprovalView approvalView, int comingFrom) {
        this.context = context;
        this.approvalView = approvalView;
        this.comingFrom = comingFrom;
    }


    @Override
    public void onSearch(String searchText) {
        this.searchText=searchText;
        soBeanSearchList.clear();
        if (soBeanList != null) {
            if (TextUtils.isEmpty(searchText)) {
                soBeanSearchList.addAll(soBeanList);
            } else {
                for (SOListBean item : soBeanList) {
                    if (item.getSearchText().toLowerCase().contains(searchText.toLowerCase())) {
                        soBeanSearchList.add(item);
                    }
                }
            }
        }
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (approvalView != null) {
                    approvalView.displaySearchList(soBeanSearchList);
                }
            }
        });

    }

    @Override
    public void onstart() {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(approvalView!=null){
                    approvalView.showProgress();
                }
            }
        });

        Bundle bundle = new Bundle();
        String qry = "";
        if (comingFrom == 2)//Contract approval
            qry = Constants.Tasks + "/?$filter=" + Constants.EntityType + "%20eq%20'CONTRACT'";
        else //SO approval
            qry = Constants.Tasks + "/?$select=InstanceID,EntityKey,EntityDate1,EntityKeyID,EntityKeyDesc,EntityCurrency,EntityValue1,EntityAttribute1&$filter=" + Constants.EntityType + "%20eq%20'SO'";

        try {

            OnlineManager.doOnlineGetRequest(qry, context, iReceiveEvent -> {
                if (iReceiveEvent.getResponseStatusCode()==200){
                    JSONObject jsonObject = OnlineManager.getJSONBody(iReceiveEvent);
                    JSONArray jsonArray = OnlineManager.getJSONArrayBody(jsonObject);
                    try {
                        if (Constants.writeDebug){
                            LogManager.writeLogDebug("Request Success SO Approval ");
                        }
                        SOApproveActivity.SOTotalCount = String.valueOf(jsonArray.length());
                        if (Constants.writeDebug){
                            LogManager.writeLogDebug("Request Success SO Approval Count : "+  SOApproveActivity.SOTotalCount);
                        }
                        Log.d(TAG, "response came xml parse started");
                        soBeanList.clear();
                        soBeanList = OnlineManager.getSOList(soBeanList, jsonArray);
                        try {
                            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constants.TotalSOCount, String.valueOf(soBeanList.size()));
                            editor.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "SO List Loaded  ");
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                long refreshTime = ConstantsUtils.getCurrentTimeLong();
                                if(approvalView!=null) {
                                    approvalView.displayRefreshTime(refreshTime);
//                            displayListView();
                                    approvalView.displaySearchList(soBeanList);
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogManager.writeLogError(Constants.error_txt + " : " + e.getMessage());
                    } finally {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(approvalView!=null) {
                                    approvalView.hideProgress();
                                }
                            }
                        });

                    }
//
                }else {
                    Log.e("Online request error : ",iReceiveEvent.toString());
                    LogManager.writeLogError(iReceiveEvent.toString());
                    String errorMsg="";
                    try {
                        errorMsg = Constants.getErrorMessage(iReceiveEvent,context);
                        LogManager.writeLogError(errorMsg);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        errorMsg = (e.getMessage());
                        LogManager.writeLogError(e.getMessage());
                    }
                    String finalErrorMsg = errorMsg;
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(approvalView!=null) {
                                approvalView.hideProgress();
                                ConstantsUtils.displayErrorDialog(context, finalErrorMsg);
                            }
                        }
                    });
                }
            }, e -> {
                e.printStackTrace();
                String errormessage = "";
                errormessage = ConstantsUtils.geterrormessageForInternetlost(e.getMessage(),context);
                if(TextUtils.isEmpty(errormessage)){
                    errormessage = e.getMessage();
                }
                String finalErrormessage = errormessage;
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(approvalView!=null) {
                            approvalView.hideProgress();
                            ConstantsUtils.displayErrorDialog(context, finalErrormessage);
                        }
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog(e.getMessage());
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(approvalView!=null) {
                        approvalView.hideProgress();
                        ConstantsUtils.displayErrorDialog(context, e.toString());
                    }
                }
            });

        }
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> entities, int operation, int requestCode, String resourcePath, Bundle bundle) {
        switch (requestCode) {
            case 1:
                try {
                    if (Constants.writeDebug){
                        LogManager.writeLogDebug("Request Success SO Approval ");
                    }
                    SOApproveActivity.SOTotalCount = String.valueOf(entities.size());
                    if (Constants.writeDebug){
                        LogManager.writeLogDebug("Request Success SO Approval Count : "+  SOApproveActivity.SOTotalCount);
                    }
                    Log.d(TAG, "response came xml parse started");
                    soBeanList.clear();
                    soBeanList = OnlineManager.getSOList(soBeanList, entities);
                    try {
                        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constants.TotalSOCount, String.valueOf(soBeanList.size()));
                        editor.commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "SO List Loaded  ");
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            long refreshTime = ConstantsUtils.getCurrentTimeLong();
                            if(approvalView!=null) {
                                approvalView.displayRefreshTime(refreshTime);
//                            displayListView();
                                approvalView.displaySearchList(soBeanList);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    LogManager.writeLogError(Constants.error_txt + " : " + e.getMessage());
                } finally {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(approvalView!=null) {
                                approvalView.hideProgress();
                            }
                        }
                    });

                }
                break;
        }
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, int operation, int requestCode, String resourcePath, final String errorMsg, Bundle bundle) {
        try {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(approvalView!=null) {
                        approvalView.hideProgress();
                    }
                    LogManager.writeLogDebug("Request failed for SO Approval " + errorMsg);

                    if(errorMsg.contains("HTTP Status 401 ? Unauthorized")){
                        Constants.customAlertDialogWithScroll(context,errorMsg);
                    }else{
                        ConstantsUtils.displayErrorDialog(context, errorMsg);
                    }

                }
            });

        } catch (Exception e) {
            LogManager.writeLogDebug(Constants.error_txt + " : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
