package com.rspl.sf.msfa.soapproval.history;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.AsyncTaskCallBack;
import com.rspl.sf.msfa.soapproval.OpenOnlineManagerStore;
import com.rspl.sf.msfa.soapproval.SOApproveActivity;
import com.rspl.sf.msfa.solist.SOListBean;
import com.rspl.sf.msfa.solist.SOTaskHistoryBean;
import com.rspl.sf.msfa.store.GetOnlineODataInterface;
import com.rspl.sf.msfa.store.OnlineManager;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10769 on 16-Mar-18.
 */

public class SOApprovalHistoryPresImpl implements SOApprovalHistoryPresenter, GetOnlineODataInterface {
    private Context mContext;
    private SOApprovalHistoryView views = null;
    private ArrayList<SOTaskHistoryBean> sotTaskList = new ArrayList<>();
    private SOListBean soListBean = null;

    public SOApprovalHistoryPresImpl(Context mContext, SOApprovalHistoryView views, SOListBean soListBean) {
        this.mContext = mContext;
        this.views = views;
        this.soListBean = soListBean;
    }

    @Override
    public void onStart() {
        if (UtilConstants.isNetworkAvailable(mContext)) {
            if (views != null)
                views.showProgressDialog();
            new OpenOnlineManagerStore(mContext, new AsyncTaskCallBack() {
                @Override
                public void onStatus(boolean status, String values) {
                    if (status) {
                        requestApprovalList();
                    } else {
                        if (views != null) {
                            views.hideProgressDialog();
                            views.showMessage(values);
                        }
                    }
                }
            }).execute();
        } else {
            if (views != null) {
                views.hideProgressDialog();
                views.showMessage(mContext.getString(R.string.no_network_conn));
            }
        }

    }

    private void requestApprovalList() {
        /*Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_RESOURCE_PATH, "TaskHistorys?$filter=EntityType eq 'SO' and EntityKey eq '" + soListBean.getSONo() + "'");
        bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 3);
        bundle.putInt(Constants.BUNDLE_OPERATION, Operation.GetRequest.getValue());
        bundle.putBoolean(Constants.BUNDLE_SESSION_REQUIRED, true);
        bundle.putBoolean(Constants.BUNDLE_SESSION_URL_REQUIRED, true);
        try {
            OnlineManager.requestOnline(this, bundle, mContext);
        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt1 + " : " + e.getMessage());
            e.printStackTrace();
            if (views != null) {
                views.hideProgressDialog();
                views.showMessage(e.getMessage());
            }
        }*/

        try {

            String qry =  "TaskHistorys?$filter=EntityType%20eq%20'SO'%20and%20EntityKey%20eq%20'" + soListBean.getSONo() + "'";
            OnlineManager.doOnlineGetRequest(qry, mContext, iReceiveEvent -> {
                if (iReceiveEvent.getResponseStatusCode()==200){
                    JSONObject jsonObject = OnlineManager.getJSONBody(iReceiveEvent);
                    JSONArray jsonArray = OnlineManager.getJSONArrayBody(jsonObject);
                    try {
                        sotTaskList = OnlineManager.getTaskHistoryList(jsonArray, mContext);
                       /* ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (views != null) {
                                    views.hideProgressDialog();
                                    views.displayResult(sotTaskList);
                                }
                            }
                        });*/
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogManager.writeLogError(Constants.error_txt + " : " + e.getMessage());
                    } finally {
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (views != null) {
                                    views.hideProgressDialog();
                                    views.displayResult(sotTaskList);
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
                        errorMsg = Constants.getErrorMessage(iReceiveEvent,mContext);
                        LogManager.writeLogError(errorMsg);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        errorMsg = (e.getMessage());
                        LogManager.writeLogError(e.getMessage());
                    }
                    String finalErrorMsg = errorMsg;
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(views!=null) {
                                views.hideProgressDialog();
                                ConstantsUtils.displayErrorDialog(mContext, finalErrorMsg);
                            }
                        }
                    });
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
                        if(views!=null) {
                            views.hideProgressDialog();
                            ConstantsUtils.displayErrorDialog(mContext, finalErrormessage);
                        }
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog(e.getMessage());
            ((Activity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(views!=null) {
                        views.hideProgressDialog();
                        ConstantsUtils.displayErrorDialog(mContext, e.toString());
                    }
                }
            });

        }
    }

    @Override
    public void onDestroy() {
        views = null;
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> entities, int operation, int requestCode, String resourcePath, Bundle bundle) {
        sotTaskList = OnlineManager.getTaskHistoryList(oDataRequestExecution, mContext);
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (views != null) {
                    views.hideProgressDialog();
                    views.displayResult(sotTaskList);
                }
            }
        });

    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, int operation, int requestCode, String resourcePath, final String errorMsg, Bundle bundle) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (views != null) {
                    views.hideProgressDialog();
                    views.showMessage(errorMsg);
                }
            }
        });
    }
}
