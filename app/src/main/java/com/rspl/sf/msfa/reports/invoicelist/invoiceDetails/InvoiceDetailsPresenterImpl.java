package com.rspl.sf.msfa.reports.invoicelist.invoiceDetails;

import android.app.Activity;

import com.rspl.sf.msfa.reports.invoicelist.InvoiceListBean;


/**
 * Created by e10769 on 04-07-2017.
 */

public class InvoiceDetailsPresenterImpl implements InvoiceDetailsPresenter{
    private Activity mContext;
    private InvoiceDetailsView invoiceDetailsView;
    private int comingFrom;
    private InvoiceListBean invoiceListBean;
    private boolean isSessionRequired;
    public InvoiceDetailsPresenterImpl(Activity mContext, InvoiceDetailsView invoiceDetailsView, int comingFrom, InvoiceListBean invoiceListBean,boolean isSessionRequired){
        this.mContext=mContext;
        this.invoiceDetailsView=invoiceDetailsView;
        this.comingFrom=comingFrom;
        this.invoiceListBean=invoiceListBean;
        this.isSessionRequired= isSessionRequired;
    }
    @Override
    public void onStart() {
    }

    @Override
    public void onDestroy() {
        invoiceDetailsView=null;
    }

    @Override
    public void pdfDownload() {
        /*if (PermissionUtils.checkStoragePermission(mContext)) {
            if (invoiceDetailsView != null) {
                invoiceDetailsView.showProgressDialog(mContext.getString(R.string.downloading_pdf));
            }
            if (isSessionRequired){
                new SessionIDAsyncTask(mContext, new AsyncTaskCallBackInterface() {
                    @Override
                    public void asyncResponse(boolean b, Object o, String s) {
                        if (b){
                            downloadFiles(s);
                        }else {
                            if (invoiceDetailsView != null) {
                                invoiceDetailsView.hideProgressDialog();
                               invoiceDetailsView.showMessage(s, true);
                            }
                        }
                    }
                });
            }else {
                downloadFiles("");
            }

        }*/
    }

    private void downloadFiles(String sessionId) {
        /*String soItemDetailsURL = "/Invoices(InvoiceNo=%27" + invoiceListBean.getInvoiceNo() + "%27)/$value";
        ConstantsUtils.downloadFiles(mContext, new AsyncTaskCallBackInterface() {
            @Override
            public void asyncResponse(boolean status, Object response, String message) {
                if (invoiceDetailsView != null) {
                    invoiceDetailsView.hideProgressDialog();
                }
                if (status) {
                    UtilConstants.openViewer(mContext, message, UtilConstants.PDF_MINE_TYPE);
                } else {
                    if (invoiceDetailsView != null) {
                        invoiceDetailsView.showMessage(message, true);
                    }
                }
            }
        },soItemDetailsURL, invoiceListBean.getInvoiceNo(), UtilConstants.PDF_MINE_TYPE, sessionId, isSessionRequired);*/
    }

}
