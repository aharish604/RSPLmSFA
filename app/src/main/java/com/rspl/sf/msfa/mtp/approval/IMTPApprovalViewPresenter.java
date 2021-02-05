package com.rspl.sf.msfa.mtp.approval;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rspl.sf.msfa.mtp.MTPHeaderBean;

import java.util.ArrayList;

/**
 * Created by e10847 on 19-12-2017.
 */

public interface IMTPApprovalViewPresenter {
    void initializeUI(Context context);
    void initializeClickListeners();
    void initializeObjects(Context context);
    void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager);
    void showProgressDialog();
    void hideProgressDialog();
    void searchResult(ArrayList<MTPApprovalBean> retailerSearchList);
    void refreshList();
    void showMessage(Object t);
    void hideMessage();
    void openDetailScreen(ArrayList<MTPHeaderBean> mtpRoutePlanBeanArrayList, MTPApprovalBean mtpApprovalBean);

    interface IMTPApprovalPresenter{
        void onSearch(String searchText);
        void onRefresh();
        void loadAsyncTask();
        void mtpDetails(MTPApprovalBean mtpApprovalBean);
    }


}
