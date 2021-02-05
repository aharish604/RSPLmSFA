package com.rspl.sf.msfa.main;

import android.content.Context;
import android.content.SharedPreferences;

import com.arteriatech.mutils.registration.MainMenuBean;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;

import java.util.ArrayList;

/**
 * Created by e10860 on 2/1/2018.
 */

public class MenuModelImpl implements MenuModel {

    @Override
    public void findItems(Context mContext, OnFinishedListener listener, int viewType) {
        ArrayList<MainMenuBean> mainMenuBeenList = new ArrayList<>();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        MainMenuBean mainMenuBean;
        if (viewType == 1) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName("Home");
            mainMenuBean.setId(1);
            mainMenuBean.setItemType("");
            mainMenuBean.setTitleFlag("");
            mainMenuBean.setMenuImage(R.drawable.ic_home_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }

        /* DSR Entry, Schems,Expense entry ,Expense List,Retailers*/
       /* if (sharedPreferences.getString(Constants.isStartCloseEnabled, "").equalsIgnoreCase(Constants.isStartCloseTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.menu_start));
            mainMenuBean.setId(2);
            mainMenuBean.setItemType("Today");
             mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_start);
            mainMenuBeenList.add(mainMenuBean);
        }
        if (sharedPreferences.getString(Constants.isRouteEnabled, "").equalsIgnoreCase(Constants.isRoutePlaneTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.menu_beat_plan));
            mainMenuBean.setId(3);
            mainMenuBean.setItemType("Today");
             mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_beat_plan);
            mainMenuBeenList.add(mainMenuBean);
        }*/
        if (sharedPreferences.getString(Constants.isMyTargetsEnabled, "").equalsIgnoreCase(Constants.isMyTargetsTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.menu_my_targets));
            mainMenuBean.setId(4);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_my_targets_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }
        if (sharedPreferences.getString(Constants.isMTPEnabled, "").equalsIgnoreCase(Constants.isMTPTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.mtp_details_title));
            mainMenuBean.setId(5);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_date_range_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }
        if (sharedPreferences.getString(Constants.isAdhocVisitEnabled, "").equalsIgnoreCase(Constants.isAdhocVistTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.menu_un_plan_adhoc));
            mainMenuBean.setId(6);
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setItemType("Today");
            mainMenuBean.setMenuImage(R.drawable.ic_adhoc_visit_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }
       /* if (sharedPreferences.getString(Constants.isAlertsEnabled, "").equalsIgnoreCase(Constants.isAlertTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.menu_alert));
            mainMenuBean.setId(7);
            mainMenuBean.setItemType("Today");
             mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_alerts_bell_icon);
            mainMenuBeenList.add(mainMenuBean);
        }*/

     /*  if (sharedPreferences.getString(Constants.isExpenseEntryEnabled, "").equalsIgnoreCase(Constants.isExpEnteryTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.menu_expense_ntry));
            mainMenuBean.setId(8);
            mainMenuBean.setItemType("Today");
           mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_expenses);
            mainMenuBeenList.add(mainMenuBean);
        }
       if (sharedPreferences.getString(Constants.isExpenseListEnabled, "").equalsIgnoreCase(Constants.isExpListTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.menu_expense_list));
            mainMenuBean.setId(9);
            mainMenuBean.setItemType("Today");
           mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_expenses);
            mainMenuBeenList.add(mainMenuBean);
       }
*/
        if (sharedPreferences.getString(Constants.isPlantStockKey, "").equalsIgnoreCase(Constants.isPlantStockTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.title_plant_stock));

            mainMenuBean.setId(10);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_shopping_basket_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }

      /*  if (sharedPreferences.getString(Constants.isSOApprovalKey, "").equalsIgnoreCase(Constants.isSOApprovalTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.menu_so_apporval));
            mainMenuBean.setId(11);
            mainMenuBean.setItemType("Today");
             mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_assignment_turned_in_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }*/

        if (sharedPreferences.getString(Constants.isMatPriceKey, "").equalsIgnoreCase(Constants.isMatpriceTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.title_product_pricing));
            mainMenuBean.setId(12);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_product_price_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }
/*
        if (sharedPreferences.getString(Constants.isRetailerEnabled, "").equalsIgnoreCase(Constants.isRetailerTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.menu_retailers));
            mainMenuBean.setId(13);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_retailer);
            mainMenuBeenList.add(mainMenuBean);
        }

        if (sharedPreferences.getString(Constants.isDSREntryEnabled, "").equalsIgnoreCase(Constants.isDSREntryTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.menu_dsr_entry));
            mainMenuBean.setId(14);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_summary);
            mainMenuBeenList.add(mainMenuBean);
        }*/

        if (sharedPreferences.getString(Constants.isCustomerListEnabled, "").equalsIgnoreCase(Constants.isCustomerListTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.menu_customer_list));
            mainMenuBean.setId(15);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_people_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }
        if (sharedPreferences.getString(Constants.isRTGSEnabled, "").equalsIgnoreCase(Constants.isRTGSTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.ll_rtgs));
            mainMenuBean.setId(20);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_rtgs_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }
        if (sharedPreferences.getString(Constants.isDealerBehaviourEnabled, "").equalsIgnoreCase(Constants.isDealerBehaviourTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.dealer_behaviour_title));
            mainMenuBean.setId(21);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_rtgs_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }
        if (sharedPreferences.getString(Constants.isMTPSubOrdinateEnabled, "").equalsIgnoreCase(Constants.isMTPSubOrdinateTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.mtp_sub_ord_title));
            mainMenuBean.setId(22);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_date_range_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }
        if (sharedPreferences.getString(Constants.isRTGSSubOrdinateEnabled, "").equalsIgnoreCase(Constants.isRTGSSubOrdinateTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.rtgs_sub_ord_title));
            mainMenuBean.setId(23);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_rtgs_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }

        if (sharedPreferences.getString(Constants.isAttndSumryEnabled, "").equalsIgnoreCase(Constants.isAttndSumryTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.title_attendance_Sum));
            mainMenuBean.setId(24);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.start);
            mainMenuBeenList.add(mainMenuBean);
        }

        if (sharedPreferences.getString(Constants.isClaimSumryEnabled, "").equalsIgnoreCase(Constants.isClaimSumryTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.title_claim_Sum));
            mainMenuBean.setId(26);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_monetization_on_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }

        if (viewType == 1) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.sync_menu));
            mainMenuBean.setId(16);
            mainMenuBean.setItemType("Admin");
            mainMenuBean.setMenuImage(R.drawable.ic_sync_black_24dp);
            mainMenuBeenList.add(mainMenuBean);

            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.settings));
            mainMenuBean.setId(18);
            mainMenuBean.setItemType("");
            mainMenuBean.setTitleFlag("");
            mainMenuBean.setMenuImage(R.drawable.ic_settings_black_24dp);
            mainMenuBeenList.add(mainMenuBean);

            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.title_support));
            mainMenuBean.setId(19);
            mainMenuBean.setItemType("");
            mainMenuBean.setTitleFlag("");
            mainMenuBean.setMenuImage(R.drawable.ic_help_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
            listener.onFinished(mainMenuBeenList);

//            mainMenuBean = new MainMenuBean();
//            mainMenuBean.setMenuName(mContext.getString(R.string.title_reset_pwd));
//            mainMenuBean.setId(25);
//            mainMenuBean.setItemType("");
//            mainMenuBean.setTitleFlag("");
//            mainMenuBean.setMenuImage(R.drawable.ic_help_black_24dp);
//            mainMenuBeenList.add(mainMenuBean);
//            listener.onFinished(mainMenuBeenList);
        }
    }
}
