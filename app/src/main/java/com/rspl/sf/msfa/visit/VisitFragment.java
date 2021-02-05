package com.rspl.sf.msfa.visit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.competitorInfo.CompetitorInformation;
import com.rspl.sf.msfa.complaint.ComplaintCreateActivity;
import com.rspl.sf.msfa.dealerstock.StockCreateStpTwoActivity;
import com.rspl.sf.msfa.finance.CollectionCreateActivity;
import com.rspl.sf.msfa.orderinfocreate.OrderInfoCreate;
import com.rspl.sf.msfa.retailerStock.RetailerStockEntry;
import com.rspl.sf.msfa.so.SalesOrderHeaderViewActivity;
import com.rspl.sf.msfa.socreate.stepOne.SOCreateActivity;
import com.rspl.sf.msfa.store.OfflineManager;

/**
 * Created by e10742 on 02-12-2016.
 */
public class VisitFragment extends Fragment implements View.OnClickListener {

    ImageButton ib_so_info_create_selection, ib_price_info_selection, ib_pop_selection, ib_trade_info, ib_trade_info_tech_team, ib_trade_info_tech_team_customer_main_menu, ib_trade_info_tech_team_customer;
    View myInflatedView = null;
    ImageButton ib_collection_create, ib_merchndising, ib_so_create_multiple;
    String mStrVisitStartedOrNotQuery = "";
    TextView textViewDealerStock;
    ImageView ib_so_create, ib_stock_info_selection;
    private String mStrBundleRetailerNo = "";
    private String mStrBundleRetailerName = "";
    private String mUID = "";
    private String mComingFrom = "";
    private String mStrBundleCPGUID32 = "";
    private String mStrCurrency = "";
    private String mStrSPGUID = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mStrBundleCPGUID32 = getArguments().getString(Constants.CPGUID32);

        mStrBundleRetailerName = getArguments().getString(Constants.RetailerName);
        mUID = getArguments().getString(Constants.CPUID);
        mComingFrom = getArguments().getString(Constants.comingFrom);
        mStrBundleRetailerNo = getArguments().getString(Constants.CPNo);
        mStrCurrency = getArguments().getString(Constants.Currency);
        // Inflate the layout for this fragment
        mStrSPGUID = Constants.getSPGUID(Constants.SPGUID);
        myInflatedView = inflater.inflate(R.layout.activity_visit_view, container, false);
        return myInflatedView;
    }

    @Override
    public void onStart() {
        initUI();
        super.onStart();
    }

    void initUI() {
        ImageView ivMustSell = (ImageView) myInflatedView.findViewById(R.id.ib_must_sell_selection);
        ivMustSell.setOnClickListener(this);

        LinearLayout ll_must_sell = (LinearLayout) myInflatedView.findViewById(R.id.ll_must_sell);
        LinearLayout ll_must_sell_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_must_sell_line);

        LinearLayout ll_so_create = (LinearLayout) myInflatedView.findViewById(R.id.ll_so_create);
        LinearLayout ll_so_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_so_line);
        ll_so_create.setOnClickListener(this);

        LinearLayout ll_so_create_single = (LinearLayout) myInflatedView.findViewById(R.id.ll_so_create_single);
        LinearLayout ll_so_line_single = (LinearLayout) myInflatedView.findViewById(R.id.ll_so_line_single);
        ll_so_create.setOnClickListener(this);

        LinearLayout ll_price_info = (LinearLayout) myInflatedView.findViewById(R.id.ll_price_info);
        LinearLayout ll_price_info_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_price_info_line);
        ll_price_info.setOnClickListener(this);


        LinearLayout ll_pop = (LinearLayout) myInflatedView.findViewById(R.id.ll_pop);
        LinearLayout ll_pop_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_pop_line);
        ll_pop.setVisibility(View.GONE);
        ll_pop_line.setVisibility(View.GONE);
        ll_pop.setOnClickListener(this);

        LinearLayout ll_trade_info = (LinearLayout) myInflatedView.findViewById(R.id.ll_trade_info);
        LinearLayout ll_trade_info_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_trade_info_line);
        ll_trade_info.setOnClickListener(this);

        LinearLayout ll_competitor_master = (LinearLayout) myInflatedView.findViewById(R.id.ll_competitorList_main_menu);
        LinearLayout ll_competitor_master_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_competitorList_main_menu_line);
        ll_competitor_master.setVisibility(View.GONE);
        ll_competitor_master_line.setVisibility(View.GONE);
        ll_competitor_master.setOnClickListener(this);

        LinearLayout ll_trade_info_tech_team = (LinearLayout) myInflatedView.findViewById(R.id.ll_trade_info_tech_team);
        ll_trade_info_tech_team.setVisibility(View.GONE);
        LinearLayout ll_trade_info_tech_team_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_trade_info_tech_team_line);
        ll_trade_info_tech_team_line.setVisibility(View.GONE);
        ll_trade_info_tech_team.setOnClickListener(this);

        LinearLayout ll_trade_info_tech_team_customer = (LinearLayout) myInflatedView.findViewById(R.id.ll_trade_info_tech_team_customer);
        ll_trade_info_tech_team_customer.setVisibility(View.GONE);
        LinearLayout ll_trade_info_tech_team_customer_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_trade_info_tech_team_customer_line);
        ll_trade_info_tech_team_customer_line.setVisibility(View.GONE);
        ll_trade_info_tech_team_customer.setOnClickListener(this);

        LinearLayout ll_trade_info_tech_team_main_menu = (LinearLayout) myInflatedView.findViewById(R.id.ll_trade_info_tech_team_main_menu);
        LinearLayout ll_trade_info_tech_team_customer_main_menu_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_trade_info_tech_team_customer_main_menu_line);
        ll_trade_info_tech_team_main_menu.setOnClickListener(this);

        LinearLayout ll_stock_info = (LinearLayout) myInflatedView.findViewById(R.id.ll_stock_info);
        LinearLayout ll_stock_info_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_stock_info_line);
        ll_stock_info.setVisibility(View.GONE);
        ll_stock_info_line.setVisibility(View.GONE);
        ll_stock_info.setOnClickListener(this);


        LinearLayout ll_stock_price = (LinearLayout) myInflatedView.findViewById(R.id.ll_stock_price);
        LinearLayout ll_stock_price_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_stock_price_line);
        ll_stock_price.setOnClickListener(this);


        LinearLayout ll_so_info_create = (LinearLayout) myInflatedView.findViewById(R.id.ll_so_info_create);
        LinearLayout ll_so_info_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_so_info_line);
        ll_so_info_create.setOnClickListener(this);


        LinearLayout ll_collection_create = (LinearLayout) myInflatedView.findViewById(R.id.ll_collection_create);
        LinearLayout ll_collection_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_collection_line);


        LinearLayout ll_competitor_info_create = (LinearLayout) myInflatedView.findViewById(R.id.ll_competitor_info_create);
        LinearLayout ll_competitor_info_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_competitor_info_line);

        LinearLayout ll_mer_snap_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_mer_snap_line);
        LinearLayout ll_snap_create = (LinearLayout) myInflatedView.findViewById(R.id.ll_snap_create);

        LinearLayout ll_feed_back_create = (LinearLayout) myInflatedView.findViewById(R.id.ll_feed_back_create);
        LinearLayout ll_feed_back_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_feed_back_line);
        ll_feed_back_create.setOnClickListener(this);

        LinearLayout ll_sample_disbursement_create = (LinearLayout) myInflatedView.findViewById(R.id.ll_sample_disbursement_create);
        LinearLayout ll_sample_disbursement_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_sample_disbursement_line);

        LinearLayout ll_mer_det = (LinearLayout) myInflatedView.findViewById(R.id.ll_mer_det);
        LinearLayout ll_mer_details_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_mer_details_line);
        textViewDealerStock = (TextView) myInflatedView.findViewById(R.id.textViewDealerStock);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        String sharedVal = sharedPreferences.getString(Constants.isCollCreateEnabledKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isCollCreateTcode)) {
            ll_collection_create.setVisibility(View.VISIBLE);
            ll_collection_line.setVisibility(View.VISIBLE);
        } else {
            ll_collection_create.setVisibility(View.GONE);
            ll_collection_line.setVisibility(View.GONE);
        }
        ll_collection_create.setOnClickListener(this);
        ll_sample_disbursement_create.setVisibility(View.GONE);
        ll_sample_disbursement_line.setVisibility(View.GONE);


        sharedVal = sharedPreferences.getString(Constants.isMerchReviewKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isMerchReviewTcode)) {
            ll_snap_create.setVisibility(View.VISIBLE);
            ll_mer_snap_line.setVisibility(View.VISIBLE);
        } else {
            ll_snap_create.setVisibility(View.GONE);
            ll_mer_snap_line.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString("isFeedbackCreateEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SF_FDBKCRT")) {
            ll_feed_back_create.setVisibility(View.VISIBLE);
            ll_feed_back_line.setVisibility(View.VISIBLE);
        } else {
            ll_feed_back_create.setVisibility(View.GONE);
            ll_feed_back_line.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString("isSOCreateEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SF_SOCRT")) {
            ll_so_create.setVisibility(View.VISIBLE);
            ll_so_line.setVisibility(View.VISIBLE);
        } else {
            ll_so_create.setVisibility(View.GONE);
            ll_so_line.setVisibility(View.GONE);
        }


        sharedVal = sharedPreferences.getString(Constants.isSOWithSingleItemEnabled, "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SF_SOCRT01")) {
            ll_so_create_single.setVisibility(View.VISIBLE);
            ll_so_line_single.setVisibility(View.VISIBLE);
        } else {
            ll_so_create_single.setVisibility(View.GONE);
            ll_so_line_single.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString(Constants.isCompInfoEnabled, "");
        if (sharedVal.equalsIgnoreCase(Constants.isCompInfoTcode)) {
            ll_competitor_info_create.setVisibility(View.VISIBLE);
            ll_competitor_info_line.setVisibility(View.VISIBLE);
        } else {
            ll_competitor_info_create.setVisibility(View.GONE);
            ll_competitor_info_line.setVisibility(View.GONE);
        }

        ll_must_sell.setVisibility(View.GONE);
        ll_must_sell_line.setVisibility(View.GONE);

        ll_mer_det.setVisibility(View.GONE);
        ll_mer_details_line.setVisibility(View.GONE);

        LinearLayout ll_visit_invoice_His = (LinearLayout) myInflatedView.findViewById(R.id.ll_visit_invoice_His);
        LinearLayout ll_inv_his_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_inv_his_line);

        LinearLayout ll_visit_retailer_stock = (LinearLayout) myInflatedView.findViewById(R.id.ll_visit_retailer_stock);
        LinearLayout ll_ret_stock_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_ret_stock_line);

        LinearLayout ll_trends = (LinearLayout) myInflatedView.findViewById(R.id.ll_trends);
        LinearLayout ll_trends_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_trends_line);

        LinearLayout ll_act_status = (LinearLayout) myInflatedView.findViewById(R.id.ll_act_status);
        LinearLayout ll_act_status_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_act_status_line);

        LinearLayout ll_coll_his = (LinearLayout) myInflatedView.findViewById(R.id.ll_coll_his);
        LinearLayout ll_coll_his_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_coll_his_line);

        LinearLayout ll_new_product = (LinearLayout) myInflatedView.findViewById(R.id.ll_new_product);
        LinearLayout ll_new_product_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_new_product_line);

        LinearLayout ll_focused_prd_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_focused_prd_line);
        LinearLayout ll_focused_prd = (LinearLayout) myInflatedView.findViewById(R.id.ll_focused_prd);

        LinearLayout ll_complaint_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_complaint_line);
        LinearLayout ll_complaint = (LinearLayout) myInflatedView.findViewById(R.id.ll_complaint);
        ll_complaint.setOnClickListener(this);

        ll_focused_prd.setVisibility(View.GONE);
        ll_focused_prd_line.setVisibility(View.GONE);

        ll_complaint_line.setVisibility(View.GONE);
        ll_complaint.setVisibility(View.GONE);

        ll_new_product.setVisibility(View.GONE);
        ll_new_product_line.setVisibility(View.GONE);
        ll_visit_invoice_His.setVisibility(View.GONE);
        ll_inv_his_line.setVisibility(View.GONE);
        ll_visit_retailer_stock.setVisibility(View.GONE);
        ll_ret_stock_line.setVisibility(View.GONE);
        ll_trends.setVisibility(View.GONE);
        ll_trends_line.setVisibility(View.GONE);
        ll_act_status.setVisibility(View.GONE);
        ll_act_status_line.setVisibility(View.GONE);
        ll_coll_his.setVisibility(View.GONE);
        ll_coll_his_line.setVisibility(View.GONE);

        ib_merchndising = (ImageButton) myInflatedView.findViewById(R.id.ib_merchndising_selection);
        ib_merchndising.setOnClickListener(this);
        ib_trade_info = (ImageButton) myInflatedView.findViewById(R.id.ib_trade_info);
        ib_trade_info.setOnClickListener(this);
        ib_trade_info_tech_team = (ImageButton) myInflatedView.findViewById(R.id.ib_trade_info_tech_team);
        ib_trade_info_tech_team.setOnClickListener(this);

        ib_trade_info_tech_team_customer_main_menu = (ImageButton) myInflatedView.findViewById(R.id.ib_trade_info_tech_team_customer_main_menu);
        ib_trade_info_tech_team_customer_main_menu.setOnClickListener(this);
        ib_trade_info_tech_team_customer = (ImageButton) myInflatedView.findViewById(R.id.ib_trade_info_tech_team_customer);
        ib_trade_info_tech_team_customer.setOnClickListener(this);

        ib_so_create = (ImageView) myInflatedView.findViewById(R.id.ib_so_create_selection);
        ib_so_create.setOnClickListener(this);

        ib_so_create_multiple = (ImageButton) myInflatedView.findViewById(R.id.ib_so_create_selection_single);
        ib_so_create_multiple.setOnClickListener(this);

        ib_collection_create = (ImageButton) myInflatedView.findViewById(R.id.ib_collection_create_selection);
        ib_collection_create.setOnClickListener(this);

        ImageButton ib_feed_back_create_selection = (ImageButton) myInflatedView.findViewById(R.id.ib_feed_back_create_selection);
        ib_feed_back_create_selection.setOnClickListener(this);

        ImageButton ib_competitor_info_create_selection = (ImageButton) myInflatedView.findViewById(R.id.ib_competitor_info_create_selection);
        ib_competitor_info_create_selection.setOnClickListener(this);

        ib_so_info_create_selection = (ImageButton) myInflatedView.findViewById(R.id.ib_so_info_create_selection);
        ib_so_info_create_selection.setOnClickListener(this);


        ib_stock_info_selection = (ImageView) myInflatedView.findViewById(R.id.ib_stock_info_selection);
        ib_stock_info_selection.setOnClickListener(this);
        ib_pop_selection = (ImageButton) myInflatedView.findViewById(R.id.ib_pop_selection);
        ib_pop_selection.setOnClickListener(this);

        ib_price_info_selection = (ImageButton) myInflatedView.findViewById(R.id.ib_price_info_selection);
        ib_price_info_selection.setOnClickListener(this);

        ImageButton ib_inv_his = (ImageButton) myInflatedView.findViewById(R.id.ib_visit_invoice_his_next);
        ib_inv_his.setOnClickListener(this);

        ImageButton ib_ret_stock = (ImageButton) myInflatedView.findViewById(R.id.ib_visit_retailer_stock_next);
        ib_ret_stock.setOnClickListener(this);

        ImageButton ib_trends = (ImageButton) myInflatedView.findViewById(R.id.ib_visit_trends_next);
        ib_trends.setOnClickListener(this);

        ImageButton ib_act_status = (ImageButton) myInflatedView.findViewById(R.id.ib_visit_act_status_next);
        ib_act_status.setOnClickListener(this);

        ImageButton ib_coll_his = (ImageButton) myInflatedView.findViewById(R.id.ib_visit_coll_his_next);
        ib_coll_his.setOnClickListener(this);


        ImageButton ib_visit_focused_prd_next = (ImageButton) myInflatedView.findViewById(R.id.ib_visit_focused_prd_next);
        ib_visit_focused_prd_next.setOnClickListener(this);

        ImageButton ib_visit_new_product_next = (ImageButton) myInflatedView.findViewById(R.id.ib_visit_new_product_next);
        ib_visit_new_product_next.setOnClickListener(this);

        ImageView ivAddressCollapse = (ImageView) myInflatedView.findViewById(R.id.iv_visit_address_collapse);
        ivAddressCollapse.setOnClickListener(this);

        ImageView ib_mer_details_next = (ImageView) myInflatedView.findViewById(R.id.ib_mer_details_next);
        ib_mer_details_next.setOnClickListener(this);

        ImageView ib_sample_disbursement_next = (ImageView) myInflatedView.findViewById(R.id.ib_sample_disbursement_create_selection);
        ib_sample_disbursement_next.setOnClickListener(this);

        String mStrVisitQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() +
                "' and CPGUID eq '" + mStrBundleCPGUID32.toUpperCase() + "' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";

        mStrVisitStartedOrNotQuery = Constants.Visits + "?$filter=EndDate eq null and CPGUID eq '" + mStrBundleCPGUID32.toUpperCase() + "' " +
                "and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";

        checkTodayCollCreateOrNot(mStrVisitQry);
        checkTodayMerchReviewCreateOrNot(mStrVisitQry);
        checkTodaySOCreateOrNot(mStrVisitQry);
        checkOrderInfoCreated();
        checkDealerStockInfoCreated();
        checkDealerPriceInfoCreated();
        checkPopInfoCreated();
        checkTradeInfoCreated();
        checkTradeTechTeamInfoCreated();
        checkTradeTechTeamInfoCustomerCreated();
        checkTradeTechTeamInfoCustomerMainMenuCreated();
        try {
            if (mComingFrom.equalsIgnoreCase(Constants.ProspectiveCustomerList)) {
                ll_so_create.setVisibility(View.GONE);
                ll_so_line.setVisibility(View.GONE);
                ll_feed_back_create.setVisibility(View.GONE);
                ll_feed_back_line.setVisibility(View.GONE);
                ll_price_info.setVisibility(View.GONE);
                ll_price_info_line.setVisibility(View.GONE);
                ll_so_info_create.setVisibility(View.GONE);
                ll_so_info_line.setVisibility(View.GONE);
                ll_trade_info_tech_team_customer.setVisibility(View.GONE);
                ll_trade_info_tech_team_customer_line.setVisibility(View.GONE);
                ll_trade_info_tech_team_main_menu.setVisibility(View.GONE);
                ll_trade_info_tech_team_customer_main_menu_line.setVisibility(View.GONE);
                ll_complaint_line.setVisibility(View.GONE);
                ll_complaint.setVisibility(View.GONE);

                textViewDealerStock.setText(R.string.dealer_sale);
                ll_trade_info.setVisibility(View.VISIBLE);
                ll_trade_info_line.setVisibility(View.VISIBLE);
                ll_trade_info_tech_team.setVisibility(View.VISIBLE);
                ll_trade_info_tech_team_line.setVisibility(View.VISIBLE);

            } else {
                ll_so_create.setVisibility(View.VISIBLE);
                ll_so_line.setVisibility(View.VISIBLE);
//            ll_feed_back_create.setVisibility(View.VISIBLE);
//            ll_feed_back_line.setVisibility(View.VISIBLE);
                ll_price_info.setVisibility(View.GONE);
                ll_price_info_line.setVisibility(View.GONE);
                ll_trade_info_line.setVisibility(View.GONE);
                ll_so_info_create.setVisibility(View.GONE);
                ll_so_info_line.setVisibility(View.GONE);
                ll_so_info_line.setVisibility(View.GONE);
                ll_trade_info.setVisibility(View.GONE);

                ll_trade_info_tech_team.setVisibility(View.GONE);
                ll_trade_info_tech_team_line.setVisibility(View.GONE);
                ll_trade_info_tech_team_main_menu.setVisibility(View.GONE);
                ll_trade_info_tech_team_customer_main_menu_line.setVisibility(View.GONE);


                textViewDealerStock.setText(R.string.dealer_stock);
//            ll_trade_info_tech_team_customer.setVisibility(View.VISIBLE);
//            ll_trade_info_tech_team_customer_line.setVisibility(View.VISIBLE);
//            ll_trade_info_tech_team_customer.setVisibility(View.VISIBLE);
//
//            ll_trade_info_tech_team_customer_main_menu_line.setVisibility(View.VISIBLE);
//            ll_complaint_line.setVisibility(View.VISIBLE);
//            ll_complaint.setVisibility(View.VISIBLE);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void checkTodayCollCreateOrNot(String mStrVisitQry) {
        try {
            if (OfflineManager.getVisitActivityStatusForCustomer(mStrVisitQry, "02")) {
                ib_collection_create.setImageResource(R.drawable.ic_done_black_24dp);
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void checkTodayMerchReviewCreateOrNot(String mStrVisitQry) {
        try {
            if (OfflineManager.getVisitActivityStatusForCustomer(mStrVisitQry, "01")) {
                ib_merchndising.setImageResource(R.drawable.ic_done_black_24dp);
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void checkTodaySOCreateOrNot(String mStrVisitQry) {
        try {
            if (OfflineManager.getVisitActivityStatusForCustomer(mStrVisitQry, "03")) {
                ib_so_create.setImageResource(R.drawable.ic_done_black_24dp);
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void checkOrderInfoCreated() {
        try {
            String orderCreated = Constants.events.getVisitActDone(Constants.ORDER_INFO_TABLE, Constants.CustomerNo,
                    mStrBundleRetailerNo, Constants.DateofDispatch, UtilConstants.getNewDate());
            if (!orderCreated.equalsIgnoreCase("")) {
                ib_so_info_create_selection.setImageResource(R.drawable.ic_done_black_24dp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkDealerStockInfoCreated() {
        try {
        String dealerStockCreated = Constants.events.getVisitActDone(Constants.STOCK_INFO_TABLE,
                Constants.CustomerNo, mStrBundleRetailerNo, Constants.PriceDate, UtilConstants.getNewDate());
        if (dealerStockCreated!=null && !dealerStockCreated.equalsIgnoreCase("")) {
            ib_stock_info_selection.setImageResource(R.drawable.ic_done_black_24dp);
        }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void checkDealerPriceInfoCreated() {
        try {
            String dealerStockCreated = Constants.events.getVisitActDone(Constants.PRICE_INFO_TABLE,
                    Constants.CustomerNo, mStrBundleRetailerNo, Constants.DateofDispatch, UtilConstants.getNewDate());
            if (!dealerStockCreated.equalsIgnoreCase("")) {
                ib_price_info_selection.setImageResource(R.drawable.ic_done_black_24dp);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void checkPopInfoCreated() {
        try {
            String dealerStockCreated = Constants.events.getVisitActDone(Constants.POP_INFO_TABLE,
                    Constants.CustomerNo, mStrBundleRetailerNo, Constants.DateofDispatch, UtilConstants.getNewDate());
            if (!dealerStockCreated.equalsIgnoreCase("")) {
                ib_pop_selection.setImageResource(R.drawable.ic_done_black_24dp);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void checkTradeInfoCreated() {
        try {
            String dealerStockCreated = Constants.events.getVisitActDone(Constants.TRADE_INFO_TABLE,
                    Constants.CustomerNo, mStrBundleRetailerNo, Constants.TradeDate, UtilConstants.getNewDate());
            if (!dealerStockCreated.equalsIgnoreCase("")) {
                ib_trade_info.setImageResource(R.drawable.ic_done_black_24dp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTradeTechTeamInfoCreated() {
        try {
            String dealerStockCreated = Constants.events.getVisitActDone(Constants.TRADE_INFO_TABLE,
                    Constants.CustomerNo, mStrBundleRetailerNo, Constants.TradeDate, UtilConstants.getNewDate());
            if (!dealerStockCreated.equalsIgnoreCase("")) {
                ib_trade_info_tech_team.setImageResource(R.drawable.ic_done_black_24dp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTradeTechTeamInfoCustomerCreated() {
        try {
            String dealerStockCreated = Constants.events.getVisitActDone(Constants.TRADE_INFO_TABLE,
                    Constants.CustomerNo, mStrBundleRetailerNo, Constants.TradeDate, UtilConstants.getNewDate());
            if (!dealerStockCreated.equalsIgnoreCase("")) {
                ib_trade_info_tech_team_customer.setImageResource(R.drawable.ic_done_black_24dp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTradeTechTeamInfoCustomerMainMenuCreated() {
        try {
            String dealerStockCreated = Constants.events.getVisitActDone(Constants.TRADE_INFO_CUSTOMER_TECH_TEAM_TABLE,
                    Constants.CustomerNo, mStrBundleRetailerNo, Constants.TradeDate, UtilConstants.getNewDate());
            if (!dealerStockCreated.equalsIgnoreCase("")) {
                ib_trade_info_tech_team_customer_main_menu.setImageResource(R.drawable.ic_done_black_24dp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        if (sharedPreferences.getString(Constants.isStartCloseEnabled, "").equalsIgnoreCase(Constants.isStartCloseTcode)) {
            Constants.MapEntityVal.clear();

            String attdIdStr = "";
            String attnQry = Constants.Attendances + "?$filter=EndDate eq null and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
            try {
                attdIdStr = OfflineManager.getAttendance(attnQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            if (!attdIdStr.equalsIgnoreCase("")) {
                onItemClick(v);
            } else {
                attdIdStr = "";
                String dayEndqry = Constants.Attendances + "?$filter=EndDate eq null and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
                try {
                    attdIdStr = OfflineManager.getAttendance(dayEndqry);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
                if (!TextUtils.isEmpty(attdIdStr)) {
                    Toast.makeText(getActivity(), getString(R.string.attend_close_prev_day), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.alert_plz_start_day), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            onItemClick(v);
        }

    }

    private void onItemClick(View v) {
        if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
            switch (v.getId()) {
                case R.id.ll_collection_create:
                    onNavToCollectionCreateActivity(mStrVisitStartedOrNotQuery);
                    break;
                case R.id.ib_collection_create_selection:
                    onNavToCollectionCreateActivity(mStrVisitStartedOrNotQuery);
                    break;
                case R.id.ib_merchndising_selection:
                    onNavToMerchReviewCreateActivity(mStrVisitStartedOrNotQuery);
                    break;
                case R.id.ll_snap_create:
                    onNavToMerchReviewCreateActivity(mStrVisitStartedOrNotQuery);
                    break;
                case R.id.ll_so_create:
                    // onSalesOrderCreate();
                    openSOCreate();
                    break;
                case R.id.ib_so_create_selection:
                    //  onSalesOrderCreate();
                    openSOCreate();
                    break;
                case R.id.ll_so_create_single:
                    onSalesOrderCreateWithSingleSeclection();
                    break;
                case R.id.ib_so_create_selection_single:
                    onSalesOrderCreateWithSingleSeclection();
                    break;
                case R.id.ib_competitor_info_create_selection:
                    onCompetitorInfoCreat();
                    break;
                case R.id.ll_competitor_info_create:
                    onCompetitorInfoCreat();
                    break;
                case R.id.ib_visit_retailer_stock_next:
                    onRetailerStockEntry();
                    break;
                case R.id.ll_visit_retailer_stock:
                    onRetailerStockEntry();
                    break;
                case R.id.ll_must_sell:
                    onMustSell();
                    break;
                case R.id.ib_must_sell_selection:
                    onMustSell();
                    break;
                case R.id.ll_focused_prd:
                    onFocusedProduct();
                    break;
                case R.id.ib_visit_focused_prd_next:
                    onFocusedProduct();
                    break;
                case R.id.ll_feed_back_create:
                    onFeedBack();
                    break;
                case R.id.ib_feed_back_create_selection:
                    onFeedBack();
                    break;
                case R.id.ll_so_info_create:
                    onOrderInfoCreate();
                    break;
                case R.id.ib_so_info_create_selection:
                    onOrderInfoCreate();
                    break;
                case R.id.ll_price_info:
                    onPriceInfoCreate();
                    break;
                case R.id.ib_price_info_selection:
                    onPriceInfoCreate();
                    break;
                case R.id.ll_stock_info:
                    onStockInfoCreate();
                    break;
                case R.id.ll_pop:
                    onPopCreate();
                    break;
                case R.id.ib_pop_selection:
                    onPopCreate();
                    break;
                case R.id.ll_trade_info:
                    onTradeInfoCreate();
                    break;
                case R.id.ib_trade_info:
                    onTradeInfoCreate();
                    break;
                case R.id.ll_trade_info_tech_team:
                    onTradeInfoTechTeamCreate();
                    break;
                case R.id.ib_trade_info_tech_team:
                    onTradeInfoTechTeamCreate();
                    break;
                case R.id.ll_trade_info_tech_team_customer:
                    onTradeInfoTechTeamCustomerCreate();
                    break;
                case R.id.ib_trade_info_tech_team_customer:
                    onTradeInfoTechTeamCustomerCreate();
                    break;
                case R.id.ll_trade_info_tech_team_main_menu:
                    onTradeInfoTechTeamCustomerMainMenuCreate();
                    break;
                case R.id.ib_trade_info_tech_team_customer_main_menu:
                    onTradeInfoTechTeamCustomerMainMenuCreate();
                    break;
                case R.id.ll_competitorList_main_menu:
                    onCompetitorListCreate();
                    break;
                case R.id.ll_complaint:
                    complaintCreate();
                    break;
                case R.id.ib_visit_complaint:
                    complaintCreate();
                    break;

            }
        } else {
            ConstantsUtils.showAutoDateSetDialog(getActivity());
        }
    }

    private void openSOCreate() {
//        Constants.NavComingFrom = mComingFrom;
//        Constants.NavCustNo = mStrBundleRetailerNo;
//        Constants.NavCPUID = mUID;
//        Constants.NavCustName = mStrBundleRetailerName;
//        Constants.NavCPGUID32 = mStrBundleRetailerNo;

        Intent intent = new Intent(getActivity(), SOCreateActivity.class);
        intent.putExtra(Constants.EXTRA_SESSION_REQUIRED, false);
        intent.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intent.putExtra(Constants.CPUID, mUID);
        intent.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        intent.putExtra(Constants.CPGUID32, mStrBundleRetailerNo);
        intent.putExtra(Constants.comingFrom, mComingFrom);
        intent.putExtra(Constants.EXTRA_COME_FROM, 1);
        startActivity(intent);

    }

    private void complaintCreate() {
        Intent intent = new Intent(getContext(), ComplaintCreateActivity.class);
        intent.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intent.putExtra(Constants.CPUID, mUID);
        intent.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        intent.putExtra(Constants.CPGUID32, mStrBundleRetailerNo);
        intent.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
        intent.putExtra(Constants.comingFrom, mComingFrom);
        startActivity(intent);
    }

    private void onSalesOrderCreate() {
//        Intent intentFeedBack = new Intent(getActivity(),SalesOrderCreate.class);
        Intent intentFeedBack = new Intent(getActivity(), SalesOrderHeaderViewActivity.class);
        intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.CPUID, mUID);
        intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
//        intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
        startActivity(intentFeedBack);
    }

    private void onRetailerStockEntry() {
        Intent intentFeedBack = new Intent(getActivity(), RetailerStockEntry.class);
        intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.CPUID, mUID);
        intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
//        intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
        startActivity(intentFeedBack);
    }

    private void onMustSell() {
        Intent intentFeedBack = new Intent(getActivity(), MustSellActivity.class);
        intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.CPUID, mUID);
        intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
//        intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
        startActivity(intentFeedBack);
    }

    private void onFocusedProduct() {
        Intent intentFeedBack = new Intent(getActivity(), FeedBack.class);
        intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.CPUID, mUID);
        intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
//        intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
        startActivity(intentFeedBack);
    }

    private void onOrderInfoCreate() {
        Intent intentFeedBack = new Intent(getActivity(), OrderInfoCreate.class);
        intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.CPUID, mUID);
        intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        // intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
        startActivity(intentFeedBack);
    }

    private void onPriceInfoCreate() {
        Intent intentFeedBack = new Intent(getActivity(), PriceInfoActivity.class);
        intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.CPUID, mUID);
        intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        // intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
        intentFeedBack.putExtra("from", "dealer_stock");
        startActivity(intentFeedBack);
    }

    private void onStockInfoCreate() {
        Intent intentFeedBack = new Intent(getActivity(), StockCreateStpTwoActivity.class);
        intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.CPUID, mUID);
        intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        // intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
        startActivity(intentFeedBack);
    }

    private void onPopCreate() {
        Intent intentFeedBack = new Intent(getActivity(), PopActivity.class);
        intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.CPUID, mUID);
        intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        // intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
        startActivity(intentFeedBack);
    }

    private void onTradeInfoCreate() {
        Intent intentFeedBack = new Intent(getActivity(), TradeInfoActivity.class);
        intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.CPUID, mUID);
        intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        // intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
        startActivity(intentFeedBack);
    }

    private void onTradeInfoTechTeamCreate() {
        Intent intentFeedBack = new Intent(getActivity(), TradeInfoTechTeamActivity.class);
        intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.CPUID, mUID);
        intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        // intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
        startActivity(intentFeedBack);
    }

    private void onTradeInfoTechTeamCustomerCreate() {
        Intent intentFeedBack = new Intent(getActivity(), TradeInfoTechTeamCustomerActivity.class);
        intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.CPUID, mUID);
        intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        // intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
        startActivity(intentFeedBack);
    }

    private void onTradeInfoTechTeamCustomerMainMenuCreate() {
        Intent intentFeedBack = new Intent(getActivity(), TechnicalTeamCustomerMainMenuActivity.class);
        intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.CPUID, mUID);
        intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        // intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
        startActivity(intentFeedBack);
    }

    private void onFeedBack() {
        Intent intentFeedBack = new Intent(getActivity(), FeedBackCreateActivity.class);
        intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.CPUID, mUID);
        intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        // intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
        startActivity(intentFeedBack);
    }

    private void onCompetitorListCreate() {
        Intent intentFeedBack = new Intent(getActivity(), CompetitorMasterActivity.class);
        intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.CPUID, mUID);
        intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        // intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
        startActivity(intentFeedBack);
    }

    private void onCompetitorInfoCreat() {
        Intent intentFeedBack = new Intent(getActivity(), CompetitorInformation.class);
        intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.CPUID, mUID);
        intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
//        intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
        startActivity(intentFeedBack);
    }


    private void onNavToCollectionCreateActivity(String mStrVisitQry) {
        try {
            if (OfflineManager.getVisitStatusForCustomer(mStrVisitQry)) {
                Intent intentFeedBack = new Intent(getActivity(), CollectionCreateActivity.class);
                intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
                intentFeedBack.putExtra(Constants.CPUID, mUID);
                intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);

                intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleRetailerNo);
                intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
                startActivity(intentFeedBack);

            } else {
                UtilConstants.showAlert(getString(R.string.alert_please_start_visit), getActivity());
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void onNavToMerchReviewCreateActivity(String mStrVisitQry) {
        try {
            if (OfflineManager.getVisitStatusForCustomer(mStrVisitQry)) {
                Intent intentFeedBack = new Intent(getActivity(), MerchndisingActivity.class);
                intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
                intentFeedBack.putExtra(Constants.CPUID, mUID);
                intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
//                intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
                intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleRetailerNo);
                intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
                startActivity(intentFeedBack);

            } else {
                UtilConstants.showAlert(getString(R.string.alert_please_start_visit), getActivity());
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void onSalesOrderCreateWithSingleSeclection() {
//        Intent intentFeedBack = new Intent(getActivity(),SalesOrderCreate.class);
        Intent intentFeedBack = new Intent(getActivity(), SalesOrderHeaderViewActivity.class);
        intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.CPUID, mUID);
        intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
//        intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
        intentFeedBack.putExtra("SingleSelection", true);
        startActivity(intentFeedBack);
    }

}
