package com.rspl.sf.msfa.mtp.approval;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.AsyncTaskCallBack;
import com.rspl.sf.msfa.mtp.MTPHeaderBean;
import com.rspl.sf.msfa.soapproval.OpenOnlineManagerStore;
import com.rspl.sf.msfa.store.GetOnlineODataInterface;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.store.OnlineManager;
import com.rspl.sf.msfa.store.OnlineStoreListener;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10847 on 19-12-2017.
 */

public class MTPApprovalPresenter implements IMTPApprovalViewPresenter.IMTPApprovalPresenter, UIListener, GetOnlineODataInterface {
    public static String mtpTotalCount = "";
    public ArrayList<MTPApprovalBean> approvalBeanArrayList, searchBeanArrayList;
    ArrayList<MTPHeaderBean> mtpRoutePlanBeanArrayList;
    private Context context;
    private IMTPApprovalViewPresenter imtpApprovalViewPresenter;
    private Activity activity;
    private String searchText = "";
    private MTPApprovalBean mtpApprovalBean;

    public MTPApprovalPresenter(Context context, Activity activity, IMTPApprovalViewPresenter imtpApprovalViewPresenter) {
        this.context = context;
        this.imtpApprovalViewPresenter = imtpApprovalViewPresenter;
        this.approvalBeanArrayList = new ArrayList<>();
        this.searchBeanArrayList = new ArrayList<>();
        this.activity = activity;
    }


    @Override
    public void onSearch(String searchText) {
        if (!this.searchText.equalsIgnoreCase(searchText)) {
            this.searchText = searchText;
//            onSearchQuery(searchText);
        }
    }

    @Override
    public void onRefresh() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //  if (approvalBeanArrayList.size() > 0) {
                imtpApprovalViewPresenter.refreshList();
                imtpApprovalViewPresenter.hideProgressDialog();
                //}
            }
        });

    }


    @Override
    public void loadAsyncTask() {
        if (UtilConstants.isNetworkAvailable(context)) {
            if (imtpApprovalViewPresenter != null) {
                imtpApprovalViewPresenter.showProgressDialog();
            }
            try {

                requestSoApprovalList();
            } catch (Exception e) {
                e.printStackTrace();
                if (imtpApprovalViewPresenter != null) {
                    imtpApprovalViewPresenter.hideProgressDialog();
                }
            }
        } else {
            if (imtpApprovalViewPresenter != null) {
                imtpApprovalViewPresenter.hideProgressDialog();
                imtpApprovalViewPresenter.showMessage(context.getString(R.string.no_network_conn));
            }
        }
    }

    @Override
    public void mtpDetails(MTPApprovalBean mtpApprovalBean) {
        String qry = Constants.RouteSchedules + "(guid'" + mtpApprovalBean.getEntityKey() + "')?$expand=RouteSchedulePlans";
        this.mtpApprovalBean = mtpApprovalBean;

        if (imtpApprovalViewPresenter != null) {
            imtpApprovalViewPresenter.showProgressDialog();
        }

        try {

            OnlineManager.doOnlineGetRequest(qry, context, iReceiveEvent -> {
                if (iReceiveEvent.getResponseStatusCode()==200){
                    JSONObject jsonObject = OnlineManager.getJSONBody(iReceiveEvent);
                    JSONArray jsonArray = OnlineManager.getJSONArrayBody(jsonObject);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (imtpApprovalViewPresenter != null) {
                                imtpApprovalViewPresenter.hideProgressDialog();
                                mtpRoutePlanBeanArrayList = OnlineManager.getMTPApprovalListDetail(jsonObject, OfflineManager.isASMUser());
                                if (!mtpRoutePlanBeanArrayList.isEmpty()) {
                                    ((Activity)context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            imtpApprovalViewPresenter.openDetailScreen(mtpRoutePlanBeanArrayList, mtpApprovalBean);
                                        }
                                    });

                                }
                            }
                        }
                    });
//
                }else {
                    String errorMsg="";
                    try {
                        errorMsg = Constants.getErrorMessage(iReceiveEvent,context);
                        String finalErrorMsg = errorMsg;
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (imtpApprovalViewPresenter != null) {
                                    imtpApprovalViewPresenter.hideProgressDialog();
                                    imtpApprovalViewPresenter.showMessage(finalErrorMsg);
                                }
                            }
                        });
                        LogManager.writeLogError(errorMsg);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (imtpApprovalViewPresenter != null) {
                                    imtpApprovalViewPresenter.hideProgressDialog();
                                    imtpApprovalViewPresenter.showMessage(e.getMessage());
                                }
                            }
                        });
                        LogManager.writeLogError(e.getMessage());
                    }
                }
            }, e -> {
                e.printStackTrace();
                String errormessage = "";
                errormessage = ConstantsUtils.geterrormessageForInternetlost(e.getMessage(),context);
                if(TextUtils.isEmpty(errormessage)){
                    errormessage = e.getMessage();
                }
                String finalErrormessage = errormessage;
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (imtpApprovalViewPresenter != null) {
                            imtpApprovalViewPresenter.hideProgressDialog();
                            imtpApprovalViewPresenter.showMessage(finalErrormessage);
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
                    if (imtpApprovalViewPresenter != null) {
                        imtpApprovalViewPresenter.hideProgressDialog();
                        imtpApprovalViewPresenter.showMessage(e.getMessage());
                    }
                }
            });

        }
    }

    @Override
    public void onRequestError(int i, Exception e) {

    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {

    }

    @Override
    public void responseSuccess(final ODataRequestExecution oDataRequestExecution, List<ODataEntity> entities, int operation, int requestCode, String resourcePath, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        switch (type) {
            case 1:
                try {
                    try {
                        mtpTotalCount = OfflineManager.getRouteCount(entities);
                        try {
                            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME,0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constants.TotalMTPCount,mtpTotalCount);
                            editor.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
//                    mtpTotalCount = String.valueOf(entities.size());
                    approvalBeanArrayList = OnlineManager.getMTPApprovalList(entities);
                    onRefresh();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (imtpApprovalViewPresenter != null)
                        imtpApprovalViewPresenter.hideProgressDialog();
                    LogManager.writeLogError(Constants.error_txt + " : " + e.getMessage());
                }
                break;
            case 2:
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (imtpApprovalViewPresenter != null) {
                            imtpApprovalViewPresenter.hideProgressDialog();
                            mtpRoutePlanBeanArrayList = OnlineManager.getMTPApprovalListDetail(oDataRequestExecution, OfflineManager.isASMUser());
                            if (!mtpRoutePlanBeanArrayList.isEmpty()) {
                                imtpApprovalViewPresenter.openDetailScreen(mtpRoutePlanBeanArrayList, mtpApprovalBean);
                            }
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, int operation, int requestCode, String resourcePath, String errorMsg, Bundle bundle) {
        LogManager.writeLogError(Constants.error_txt + " : " + errorMsg);
        if (imtpApprovalViewPresenter != null) {
            try {
                imtpApprovalViewPresenter.showMessage(errorMsg);
                imtpApprovalViewPresenter.hideProgressDialog();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                imtpApprovalViewPresenter.hideProgressDialog();
            }
        }
    }

    /**
     * Searc query to update the retailerList
     */
    /*private void onSearchQuery(String searchText) {
        this.searchText = searchText;
        searchBeanArrayList.clear();
        boolean isCustomerID = false;
        boolean isCustomerName = false;
        boolean isCity = false;
        if (retailerArrayList != null) {
            if (TextUtils.isEmpty(searchText)) {
                searchBeanArrayList.addAll(retailerArrayList);
            } else {
                for (CustomerBean item : retailerArrayList) {
                    isCustomerID = false;
                    isCustomerName = false;
                    isCity = false;

                    if (!TextUtils.isEmpty(searchText)) {
                        isCustomerID = item.getCustomerId().toLowerCase().contains(searchText.toLowerCase());
                        isCustomerName = item.getCustomerName().toLowerCase().contains(searchText.toLowerCase());
                        isCity = item.getCity().toLowerCase().contains(searchText.toLowerCase());
                    } else {
                        isCustomerID = true;
                        isCustomerName = true;
                        isCity = true;
                    }
                    if (isCustomerID||isCustomerName||isCity)
                        searchBeanArrayList.add(item);
                }
            }
        }
        if (imtpApprovalViewPresenter != null) {
            imtpApprovalViewPresenter.searchResult(searchBeanArrayList);
        }
    }*/
    private void requestSoApprovalList() {
        if (UtilConstants.isNetworkAvailable(activity)) {
            getMTPApprovalList(context);
        } else {
            if (imtpApprovalViewPresenter != null) {
                imtpApprovalViewPresenter.hideProgressDialog();
                imtpApprovalViewPresenter.showMessage(context.getString(R.string.err_no_network));
            }
        }
    }

    /*get SO data from offline db*/
    private void getMTPApprovalList(Context mContext) {
        String qry = Constants.Tasks + "/?$filter=" + Constants.EntityType + "+eq+'ROUTE'";
        try {

            OnlineManager.doOnlineGetRequest(qry, context, iReceiveEvent -> {
                if (iReceiveEvent.getResponseStatusCode()==200){
                    JSONObject jsonObject = OnlineManager.getJSONBody(iReceiveEvent);
                    JSONArray jsonArray = OnlineManager.getJSONArrayBody(jsonObject);
                    try {
                        try {
                            mtpTotalCount = OfflineManager.getRouteCount(jsonArray);
                            try {
                                SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME,0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Constants.TotalMTPCount,mtpTotalCount);
                                editor.commit();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                    mtpTotalCount = String.valueOf(entities.size());
                        approvalBeanArrayList = OnlineManager.getMTPApprovalList(jsonArray);
                        onRefresh();
                    } catch (Exception e) {
                        e.printStackTrace();
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (imtpApprovalViewPresenter != null) {
                                    imtpApprovalViewPresenter.hideProgressDialog();
                                    imtpApprovalViewPresenter.showMessage(e.getMessage());
                                }
                            }
                        });

                        LogManager.writeLogError(Constants.error_txt + " : " + e.getMessage());
                    }
//
                }else {
                    String errorMsg="";
                    try {
                        errorMsg = Constants.getErrorMessage(iReceiveEvent,context);
                        String finalErrorMsg = errorMsg;
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (imtpApprovalViewPresenter != null) {
                                    imtpApprovalViewPresenter.hideProgressDialog();
                                    imtpApprovalViewPresenter.showMessage(finalErrorMsg);
                                }
                            }
                        });
                        LogManager.writeLogError(errorMsg);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (imtpApprovalViewPresenter != null) {
                                    imtpApprovalViewPresenter.hideProgressDialog();
                                    imtpApprovalViewPresenter.showMessage(e.getMessage());
                                }
                            }
                        });
                        LogManager.writeLogError(e.getMessage());
                    }
                }
            }, e -> {
                e.printStackTrace();
                String errormessage = "";
                errormessage = ConstantsUtils.geterrormessageForInternetlost(e.getMessage(),context);
                if(TextUtils.isEmpty(errormessage)){
                    errormessage = e.getMessage();
                }
                String finalErrormessage = errormessage;
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (imtpApprovalViewPresenter != null) {
                            imtpApprovalViewPresenter.hideProgressDialog();
                            imtpApprovalViewPresenter.showMessage(finalErrormessage);
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
                    if (imtpApprovalViewPresenter != null) {
                        imtpApprovalViewPresenter.hideProgressDialog();
                        imtpApprovalViewPresenter.showMessage(e.getMessage());
                    }
                }
            });

        }
    }

    private void openStore() {
        try {
            new OpenOnlineManagerStore(activity, new AsyncTaskCallBack() {
                @Override
                public void onStatus(boolean status, String values) {
                    if (status) {
                        requestSoApprovalList();
                    } else {
                        if (imtpApprovalViewPresenter != null) {
                            imtpApprovalViewPresenter.showMessage(values);
                            imtpApprovalViewPresenter.hideProgressDialog();
                        }
                    }
                }
            }).execute();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
