package com.rspl.sf.msfa.returnOrder.returnDetail;

import android.app.Activity;
import android.content.Intent;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.AsyncTaskCallBackInterface;
import com.arteriatech.mutils.security.PermissionUtils;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.asyncTask.SessionIDAsyncTask;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.returnOrder.ReturnOrderBean;

/**
 * Created by e10526 on 12-03-2018.
 */

public class RODetailsPresenterImpl implements RODetailsPresenter{
    private Activity mContext;
    private RODetailsView roDetailsView;
    private int comingFrom;
    private ReturnOrderBean roListBean;
    private ReturnOrderItemBean returnOrderItemBean;
    private boolean isSessionRequired;
    public RODetailsPresenterImpl(Activity mContext, RODetailsView invoiceDetailsView, int comingFrom, ReturnOrderBean roListBean,boolean isSessionRequired){
        this.mContext=mContext;
        this.roDetailsView =invoiceDetailsView;
        this.comingFrom=comingFrom;
        this.roListBean =roListBean;
        this.isSessionRequired= isSessionRequired;
    }
    public RODetailsPresenterImpl(Activity mContext, RODetailsView invoiceDetailsView, int comingFrom, ReturnOrderItemBean returnOrderItemBean, boolean isSessionRequired){
        this.mContext=mContext;
        this.roDetailsView =invoiceDetailsView;
        this.comingFrom=comingFrom;
        this.returnOrderItemBean = returnOrderItemBean;
        this.isSessionRequired= isSessionRequired;
    }
    @Override
    public void onStart() {
    }

    @Override
    public void onDestroy() {
        roDetailsView =null;
    }

    @Override
    public void pdfDownload() {
        if (PermissionUtils.checkStoragePermission(mContext)) {
            if (roDetailsView != null) {
                roDetailsView.showProgressDialog(mContext.getString(R.string.downloading_pdf));
            }
            if (isSessionRequired){
                new SessionIDAsyncTask(mContext, new AsyncTaskCallBackInterface() {
                    @Override
                    public void asyncResponse(boolean b, Object o, String s) {
                        if (b){
                            downloadFiles(s);
                        }else {
                            if (roDetailsView != null) {
                                roDetailsView.hideProgressDialog();
                                roDetailsView.showMessage(s, true);
                            }
                        }
                    }
                });
            }else {
                downloadFiles("");
            }

        }
    }

    @Override
    public void onItemClick(ReturnOrderItemBean returnOrderBeanDeatil) {
        openDetailScreen(returnOrderBeanDeatil);
    }
    private void openDetailScreen(ReturnOrderItemBean returnOrderBeanDeatil){
        Intent intent = new Intent(mContext, ROItemDetailsActivity.class);
        intent.putExtra(Constants.EXTRA_SESSION_REQUIRED, isSessionRequired);
        intent.putExtra(Constants.EXTRA_SO_HEADER, returnOrderBeanDeatil);
        mContext.startActivity(intent);
    }
    private void downloadFiles(String sessionId) {
        String soItemDetailsURL = "/Invoices(InvoiceNo=%27" + roListBean.getInvoiceNo() + "%27)/$value";
        ConstantsUtils.downloadFiles(mContext, new AsyncTaskCallBackInterface() {
            @Override
            public void asyncResponse(boolean status, Object response, String message) {
                if (roDetailsView != null) {
                    roDetailsView.hideProgressDialog();
                }
                if (status) {
                    UtilConstants.openViewer(mContext, message, UtilConstants.PDF_MINE_TYPE);
                } else {
                    if (roDetailsView != null) {
                        roDetailsView.showMessage(message, true);
                    }
                }
            }
        },soItemDetailsURL, roListBean.getInvoiceNo(), UtilConstants.PDF_MINE_TYPE, sessionId, isSessionRequired);
    }

}
