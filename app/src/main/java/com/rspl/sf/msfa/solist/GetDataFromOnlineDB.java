/*
package com.rspl.sf.msfa.solist;

import android.content.Context;
import android.os.AsyncTask;

import com.arteriatech.mutils.common.OnlineODataStoreException;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.interfaces.AsyncTaskSOItemInterface;
import com.rspl.sf.msfa.soapproval.SalesApprovalBean;
import com.rspl.sf.msfa.store.OnlineManager;
import com.rspl.sf.msfa.store.OnlineStoreListener;
import com.sap.smp.client.odata.online.OnlineODataStore;

*/
/**
 * Created by e10769 on 23-05-2017.
 *//*


public class GetDataFromOnlineDB extends AsyncTask<Void, Boolean, Boolean> {
    SalesApprovalBean appBean = new SalesApprovalBean();
    //    ProgressDialog progressDialog;
    private Context mContext;
    private String soNo = "";
    private AsyncTaskSOItemInterface asyncTaskSOItemInterface = null;
    private String mStrInstanceId = "";
    private String Cname = "";
    private String Uid = "";
    private SOListBean soListBean = null;

    public GetDataFromOnlineDB(Context mContext, SOListBean selectedSOItem, AsyncTaskSOItemInterface asyncTaskSOItemInterface) {
        this.mContext = mContext;
        this.soNo = selectedSOItem.getSONo();
        this.asyncTaskSOItemInterface = asyncTaskSOItemInterface;
        this.mStrInstanceId = selectedSOItem.getInstanceID();
        this.Cname = selectedSOItem.getCustomerName();
        this.Uid = selectedSOItem.getCustomerNo();
        this.soListBean = selectedSOItem;
    }


    @Override
    protected void onPreExecute() {
//        progressDialog = ConstantsUtils.showProgressDialog(mContext, mContext.getResources().getString(R.string.app_loading));
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        boolean isStoreOpened = false;

        Constants.IsOnlineStoreFailed = false;
        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();
        if (store != null) {
            isStoreOpened = true;
        } else {
            try {
                isStoreOpened = OnlineManager.openOnlineStore(mContext);
            } catch (com.rspl.sf.msfa.store.OnlineODataStoreException e) {
                e.printStackTrace();
            }
        }
        if (isStoreOpened) {
            try {
                // if (soListBean.getEntityType().equalsIgnoreCase("SO")) {
                String queryHeader = Constants.SOs + "('" + soNo + "')?$expand=SOItemDetails,SOConditions";//,SOConditions,SOTexts
                appBean = OnlineManager.getSOHeaderList(queryHeader, mStrInstanceId, mContext);
                //}
                */
/*else {
                    String queryHeader = Constants.Contracts + "('" + soNo + "')?$expand=ContractItemDetails";//,SOConditions,SOTexts
                    contractBean = OnlineManager.getContractHeader(queryHeader, mStrInstanceId, mContext);
                }*//*

            } catch (OnlineODataStoreException e) {
                e.printStackTrace();
                Constants.ErrorNo = Constants.Network_Error_Code_Offline;
                isStoreOpened = false;
            }
        }
        return isStoreOpened;
    }

    @Override
    protected void onPostExecute(Boolean storeStatus) {
        super.onPostExecute(storeStatus);
        if (asyncTaskSOItemInterface != null) {
            asyncTaskSOItemInterface.soItemLoaded(appBean,storeStatus);
        }
    }
}
*/
