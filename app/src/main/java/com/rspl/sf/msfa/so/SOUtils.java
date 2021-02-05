package com.rspl.sf.msfa.so;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.OnlineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.rspl.sf.msfa.CustomerDetailsActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.attendance.AttendanceConfigTypesetTypesBean;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.CustomDialogCallBack;
import com.rspl.sf.msfa.mbo.SalesOrderBean;
import com.rspl.sf.msfa.reports.invoicelist.invoiceDetails.InvoicePartnerFunctionsBean;
import com.rspl.sf.msfa.soapproval.ApprovalListDetails;
import com.rspl.sf.msfa.socreate.CustomerPartnerFunctionBean;
import com.rspl.sf.msfa.socreate.DefaultValueBean;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.socreate.SOSubItemBean;
import com.rspl.sf.msfa.socreate.stepThree.SOSubItemVH;
import com.rspl.sf.msfa.solist.SOListBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by e10769 on 08-05-2017.
 */

public class SOUtils {

    /*get address from odata*/
    public static String getAddressValue(ODataPropMap properties) {
        String address = "";
        ODataProperty property;
        property = properties.get(Constants.Address1);
        if (property != null) {
            String ad1 = property.getValue().toString();
            if (!TextUtils.isEmpty(ad1)) {
                address = ad1;
            }
        }
        property = properties.get(Constants.Address2);
        if (property != null) {
            String ad1 = property.getValue().toString();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        property = properties.get(Constants.Address3);
        if (property != null) {
            String ad1 = property.getValue().toString();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }

        property = properties.get(Constants.Address4);
        if (property != null) {
            String ad1 = property.getValue().toString();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        property = properties.get(Constants.District);
        if (property != null) {
            String ad1 = property.getValue().toString();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        property = properties.get(Constants.City);
        if (property != null) {
            String ad1 = property.getValue().toString();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        property = properties.get(Constants.RegionDesc);
        if (property != null) {
            String ad1 = property.getValue().toString();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        property = properties.get(Constants.CountryDesc);
        if (property != null) {
            String ad1 = property.getValue().toString();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        property = properties.get(Constants.PostalCode);
        if (property != null) {
            String ad1 = property.getValue().toString();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + " - " + ad1;
                else
                    address = ad1;
            }
        }
        return address;
    }

    public static String getAddressValue(String salesArea, String shipToId) {
        String address = "";
        String mStrConfigQry = Constants.CustomerPartnerFunctions + "?$select=Address1,Address2,Address3,Address4,City,RegionDesc,CountryDesc,PostalCode &$filter=" + Constants.SalesArea + " eq '" + salesArea + "'  and " + Constants.PartnerFunctionID + " eq 'SH' and "+Constants.PartnerCustomerNo+" eq '"+shipToId+"'";
        try {
            String[][] mArrAddress = OfflineManager.getCustomerPartnerFunction(mStrConfigQry);
            if (mArrAddress != null) {
                if (mArrAddress[0].length > 0) {

                    if (!TextUtils.isEmpty(mArrAddress[0][0])) {
                        address = mArrAddress[0][0];
                    }
                    if (!TextUtils.isEmpty(mArrAddress[1][0])) {
                        if (!TextUtils.isEmpty(address))
                            address = address + ", " + mArrAddress[1][0];
                        else
                            address = mArrAddress[1][0];
                    }
                    if (!TextUtils.isEmpty(mArrAddress[2][0])) {
                        if (!TextUtils.isEmpty(address))
                            address = address + ", " + mArrAddress[2][0];
                        else
                            address = mArrAddress[2][0];
                    }
                    if (!TextUtils.isEmpty(mArrAddress[3][0])) {
                        if (!TextUtils.isEmpty(address))
                            address = address + ", " + mArrAddress[3][0];
                        else
                            address = mArrAddress[3][0];
                    }
                    if (!TextUtils.isEmpty(mArrAddress[4][0])) {
                        if (!TextUtils.isEmpty(address))
                            address = address + ", " + mArrAddress[4][0];
                        else
                            address = mArrAddress[4][0];
                    }
                    if (!TextUtils.isEmpty(mArrAddress[5][0])) {
                        if (!TextUtils.isEmpty(address))
                            address = address + ", " + mArrAddress[5][0];
                        else
                            address = mArrAddress[5][0];
                    }
                    if (!TextUtils.isEmpty(mArrAddress[6][0])) {
                        if (!TextUtils.isEmpty(address))
                            address = address + ", " + mArrAddress[6][0];
                        else
                            address = mArrAddress[6][0];
                    }
                    if (!TextUtils.isEmpty(mArrAddress[7][0])) {
                        if (!TextUtils.isEmpty(address))
                            address = address + " - " + mArrAddress[7][0];
                        else
                            address = mArrAddress[7][0];
                    }
                }
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return address;
    }

    public static Drawable displayStatusIcon(String status, Context mContext) {
        Drawable img = null;
        switch (status) {
            case "A"://OK
//                img = VectorDrawableCompat.create(mContext.getResources(), R.drawable.ic_assignment_black_24dp, null);
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_assignment_black_24dp).mutate();
                break;
            case "B"://Pending for approval
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
                if (img != null)
                    img.setColorFilter(ContextCompat.getColor(mContext, R.color.PendingApprovalColor), PorterDuff.Mode.SRC_IN);
                break;
            case "C"://Approved
                img =ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
                if (img != null)
                    img.setColorFilter(ContextCompat.getColor(mContext, R.color.ApprovedColor), PorterDuff.Mode.SRC_IN);
                break;
            case "D"://Rejected
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_remove_shopping_cart_black_24dp).mutate();
                if (img != null)
                    img.setColorFilter(ContextCompat.getColor(mContext, R.color.RejectedColor), PorterDuff.Mode.SRC_IN);
                break;
            case "01":
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_receipt_black_24dp).mutate().mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.InvStatusRed), PorterDuff.Mode.SRC_IN);
                break;
            case "02":
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_account_balance_wallet_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.InvStatusOrange), PorterDuff.Mode.SRC_IN);
                break;
            case "03":
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_account_balance_wallet_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.InvStatusGreen), PorterDuff.Mode.SRC_IN);
                break;
            default:
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_transparent).mutate();
//                mDrawable.setColorFilter(new
//                        PorterDuffColorFilter(0xff2196F3,PorterDuff.Mode.SRC_IN));
//                img = VectorDrawableCompat.create(mContext.getResources(), R.drawable.ic_transparent, null);
                break;
        }
        return img;
    }

    public static int getPoss(ArrayList<ValueHelpBean> arraData, String id) {
        if (arraData != null) {
            if (arraData.size() > 0) {
                for (int i = 0; i < arraData.size(); i++) {
                    if (arraData.get(i).getID().equalsIgnoreCase(id)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public static int getSalesAreaPos(ArrayList<DefaultValueBean> arraData, String id) {
        if (arraData != null) {
            if (arraData.size() > 0) {
                for (int i = 0; i < arraData.size(); i++) {
                    if (arraData.get(i).getSalesArea().equalsIgnoreCase(id)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public static Drawable displayDelvStatusIcon(String delvStatus, Context mContext) {
        Drawable img=null;
        switch (delvStatus) {
            case "A"://Open

                img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
                if (img != null)
                    img.setColorFilter(ContextCompat.getColor(mContext, R.color.OpenColor), PorterDuff.Mode.SRC_IN);
                break;
            case "B"://Partially processed
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_local_shipping_black_24dp).mutate();
                if (img != null)
                    img.setColorFilter(ContextCompat.getColor(mContext, R.color.PartialyClosedColor), PorterDuff.Mode.SRC_IN);
                break;
            case "C"://Closed
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_local_shipping_black_24dp).mutate();
                if (img != null)
                    img.setColorFilter(ContextCompat.getColor(mContext, R.color.ClosedColor), PorterDuff.Mode.SRC_IN);
                break;
            case "D"://Rejected
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_remove_shopping_cart_black_24dp).mutate();
                if (img != null)
                    img.setColorFilter(ContextCompat.getColor(mContext, R.color.RejectedColor), PorterDuff.Mode.SRC_IN);
                break;
            case "F"://Not relevant
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_delete_forever_black_24dp).mutate();
                break;
            default:
                img = ContextCompat.getDrawable(mContext,R.drawable.ic_transparent).mutate();
                break;
        }
        return img;
    }

    public static Drawable displayStatusImage(String delvStatus, Context mContext) {
        Drawable img=null;
        switch (delvStatus) {
            case "A"://OK
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_assignment_black_24dp);
                break;
            case "B"://Pending for approval
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp);
                if (img != null)
                    img.setColorFilter(ContextCompat.getColor(mContext, R.color.PendingApprovalColor), PorterDuff.Mode.SRC_IN);
                break;
            case "C"://Approved
                int resImage =  R.drawable.ic_local_shipping_black_24dp;
                int resColor = R.color.ApprovedColor;
                if (delvStatus.equalsIgnoreCase("A")){
                    resImage = R.drawable.ic_shopping_cart_black_24dp;
                    resColor = R.color.ApprovedColor;
                }else if (delvStatus.equalsIgnoreCase("B")){
                    resImage = R.drawable.ic_local_shipping_black_24dp;
                    resColor = R.color.PartialyClosedColor;
                }else if (delvStatus.equalsIgnoreCase("C")){
                    resImage = R.drawable.ic_local_shipping_black_24dp;
                    resColor = R.color.ClosedColor;
                }
                img = ContextCompat.getDrawable(mContext, resImage);
                if (img != null)
                    img.setColorFilter(ContextCompat.getColor(mContext, resColor), PorterDuff.Mode.SRC_IN);
                break;
            case "D"://Rejected
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_remove_shopping_cart_black_24dp);
                if (img != null)
                    img.setColorFilter(ContextCompat.getColor(mContext, R.color.RejectedColor), PorterDuff.Mode.SRC_IN);
                break;
            default:
                img = ContextCompat.getDrawable(mContext,R.drawable.ic_transparent);
                break;
        }
        return img;
    }

    public static String getStartDate(Context mContext, String type) {
        Calendar currentDate = Calendar.getInstance();
        if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_last_one_month))) {
            currentDate.set(Calendar.DAY_OF_MONTH, (currentDate.get(Calendar.DAY_OF_MONTH)) - 30);
            return setFromDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_today))) {
            return setFromDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_today_and_yesterday))) {
            currentDate.set(Calendar.DAY_OF_MONTH, (currentDate.get(Calendar.DAY_OF_MONTH)) - 1);
            return setFromDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_current_mont))) {
            currentDate.set(Calendar.DAY_OF_MONTH, 1);
            return setFromDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_next_mont))) {
            currentDate.add(Calendar.MONTH, 1);
            currentDate.set(Calendar.DAY_OF_MONTH, 1);
            return setFromDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        }else {//if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_last_seven_days)))
            currentDate.set(Calendar.DAY_OF_MONTH, (currentDate.get(Calendar.DAY_OF_MONTH)) - 7);
            return setFromDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        }
    }

    public static String getEndDate(Context mContext, String type) {
        Calendar currentDate = Calendar.getInstance();
//        currentDate.set(Calendar.DAY_OF_MONTH,-30);
        if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_last_one_month))) {
            return setFromDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_today))) {
            return setFromDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_today_and_yesterday))) {
            return setFromDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_current_mont))) {
            currentDate.set(Calendar.DAY_OF_MONTH, currentDate.getActualMaximum(Calendar.DAY_OF_MONTH));
            return setFromDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_next_mont))) {
            currentDate.add(Calendar.MONTH, 1);
            currentDate.set(Calendar.DAY_OF_MONTH, currentDate.getActualMaximum(Calendar.DAY_OF_MONTH));
            return setFromDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else {//if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_last_seven_days)))
            return setFromDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        }
    }

    private static String setFromDate(int mYear, int mMonth, int mDay) {
        String mon = "";
        String day = "";
        int mnt = 0;
        mnt = mMonth + 1;
        if (mnt < 10)
            mon = "0" + mnt;
        else
            mon = "" + mnt;
        day = "" + mDay;
        if (mDay < 10)
            day = "0" + mDay;
        return mYear + "-" + mon + "-" + day + "T00:00:00";
    }

    public static boolean isStartDateIsSmall(String startDate, String endDate) {
        try {
            String[] arrD1 = startDate.split("T0");
            String[] arrD2 = endDate.split("T0");
            String d1 = arrD1[0];
            String d2 = arrD2[0];
            SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
            boolean b = false;
            try {
                if (dfDate.parse(d1).before(dfDate.parse(d2))) {
                    b = true;//If start date is before end date
                } else if (dfDate.parse(d1).equals(dfDate.parse(d2))) {
                    b = true;//If two dates are equal
                } else {
                    b = false; //If start date is after the end date
                }
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return b;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    static HashMap<String, String> convertDataToHashMap(String soldTo, String soldToName, String shipToName, String shipTo, String salesAreaId, String mStrSalesAreaDesc, String mStrPlantId, String mStrPlantDesc, String mStrShippingPointId, String mStrShippingPointDesc, String mStrOrderTypeId, String mStrOrderTypeDesc, String dateSelected, String poDate1, String mStrPayTermId, String mStrPayTermDesc, String mStrIncoTermId, String mStrIncoTermDesc, String etPoNo, String stIncoTerm2, String stAddress, String stRemarks, String stTransportName, String stTransportNameID) {
        HashMap<String, String> headerDetail = new HashMap<>();
        headerDetail.put("SoldTo", soldTo);
        headerDetail.put("SoldToName", soldToName);
        headerDetail.put("ShipToName", shipToName);
        headerDetail.put("ShipTo", shipTo);
        headerDetail.put("SalesArea", salesAreaId);
        headerDetail.put("SalesAreaDesc", mStrSalesAreaDesc);
        headerDetail.put("Plant", mStrPlantId);
        headerDetail.put("PlantDesc", mStrPlantDesc);
        headerDetail.put("ShippingPoint", mStrShippingPointId);
        headerDetail.put("ShippingPointDesc", mStrShippingPointDesc);
        headerDetail.put("OrderType", mStrOrderTypeId);
        headerDetail.put("OrderTypeDesc", mStrOrderTypeDesc);
        headerDetail.put("PODate", dateSelected);
        headerDetail.put("PODate1", poDate1);
        headerDetail.put("PaymentTerm", mStrPayTermId);
        headerDetail.put("PaymentTermDesc", mStrPayTermDesc);
        headerDetail.put("IncoTerm1", mStrIncoTermId);
        headerDetail.put("IncoTermDesc", mStrIncoTermDesc);
        headerDetail.put("IncoTerm2", stIncoTerm2);
        headerDetail.put("Remarks", stRemarks);
        headerDetail.put("Address", stAddress);
        headerDetail.put("TransportName", stTransportName);
        headerDetail.put("TransportNameID", stTransportNameID);
//            headerDetail.put("SalesOrg", salesorg);
//            headerDetail.put("DistChannel", disChannel);
//            headerDetail.put("Division", division);
        if (!etPoNo.equalsIgnoreCase(""))
            headerDetail.put("PONo", etPoNo);
        else
            headerDetail.put("PONo", "");

        return headerDetail;
    }

//    public static int checkItemValidation(SOItemBean soItems) {
//        try {
//            double totalQty = Double.parseDouble(soItems.getSoQty());
//            double subTotal = 0;
//            int size = soItems.getSoSubItemBeen().size();
//            if (size > 0) {
//                for (SOSubItemBean soSubItemBean : soItems.getSoSubItemBeen()) {
//                    double subItemValue = Double.parseDouble(soSubItemBean.getSubQty());
//                    subTotal = subItemValue + subTotal;
//                    if (TextUtils.isEmpty(soSubItemBean.getDateForStore())) {
//                        return 2;
//                    }
//                }
//                if (totalQty == subTotal) {
//                    return 0;
//                }
//            } else {
//                return 3;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return 1;
//    }
//
//    public static ArrayList<SOSubItemBean> getSoSubItem(String matNo, ArrayList<SOItemBean> soItemBeanArrayList) {
//        ArrayList<SOSubItemBean> soSubItemBeen = new ArrayList<>();
//        for (SOItemBean soItemBean : soItemBeanArrayList) {
//            if (soItemBean.getMatCode().equalsIgnoreCase(matNo)) {
//                soSubItemBeen.addAll(soItemBean.getSoSubItemBeen());
//                break;
//            }
//        }
//        return soSubItemBeen;
//    }
//
//    public static String[][] getPlantList(String orderType, String salesArea) {
//        String[][] plantList = null;
//        String mStrConfigQry = Constants.MaterialPlants + "?$filter=" + Constants.OrderType + " eq '" + orderType + "' and Division eq '" + ConstantsUtils.getPerticularName(salesArea, 2) + "' &$orderby = Plant asc";
//        try {
//            plantList = null;
//            plantList = OfflineManager.getMaterialPlantValAndNone(mStrConfigQry);
//        } catch (OfflineODataStoreException e) {
//            e.printStackTrace();
//        }
//        return plantList;
//    }
//
//    public static String getShippingCondition(String shippingId) {
//        if (shippingId.equalsIgnoreCase("01")) {
//            return "By Road";
//        } else if (shippingId.equalsIgnoreCase("03")) {
//            return "By Rail";
//        } else if (shippingId.equalsIgnoreCase("04")) {
//            return "By Ship";
//        } else return "";
//
//    }
//
//    public static int checkButtonEnable(String totalQty, ArrayList<SOSubItemBean> subItemBeen) {
//        int viewType = 0;
//        if (!subItemBeen.isEmpty()) {
//            if (!TextUtils.isEmpty(totalQty)) {
//                BigDecimal bTotalQty = new BigDecimal(totalQty);
//                BigDecimal bSubItemQty = new BigDecimal("0");
//                for (SOSubItemBean soSubItemBean : subItemBeen) {
//                    try {
//                        String subItemQty = soSubItemBean.getSubQty();
//                        if (!TextUtils.isEmpty(subItemQty))
//                            bSubItemQty = bSubItemQty.add(new BigDecimal(subItemQty));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        break;
//                    }
//                }
//                BigDecimal bsubValue = bSubItemQty.subtract(bTotalQty);
//                if (bTotalQty.equals(bSubItemQty)) {
//                    viewType = 0;
//                } else if (bsubValue.compareTo(BigDecimal.ZERO) == 1) {
//                    viewType = 2;
//                } else {
//                    viewType = 1;
//                }
//            }
//        } else {
//            if (!TextUtils.isEmpty(totalQty)) {
//                viewType = 1;
//            }
//        }
//        return viewType;
//    }

    public static BigDecimal getExposurePercentage(BigDecimal creditExposure, BigDecimal creditLimits, BigDecimal soTotalAmt) {
        BigDecimal finalPer = new BigDecimal("0");
        try {
            BigDecimal totalCreditExp = creditExposure.add(soTotalAmt);
            MathContext mathContext = new MathContext(2, RoundingMode.HALF_UP);
            BigDecimal totalCreditAmt = totalCreditExp.divide(creditLimits, mathContext);
            finalPer = totalCreditAmt.multiply(new BigDecimal("100"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalPer;

    }

    public static Date getDateFromString(String stDate) {
        Date dates =null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.dtFormat_ddMMyyyy);

            dates = sdf.parse(stDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dates;
    }
//    public static HashSet<String> getSORefreshList(){
//        HashSet<String> alRefresh = new HashSet<>();
//        alRefresh.add(Constants.SOs);
//        alRefresh.add(Constants.SOItemDetails);
//        alRefresh.add(Constants.SOItems);
//        alRefresh.add(Constants.SOItemSchedules);
//        alRefresh.add(Constants.SOConditions);
//        alRefresh.add(Constants.SOConditionItemDetails);
//        alRefresh.add(Constants.SOTexts);
//        return alRefresh;
//    }

    /*show comment dialog*/
    public static void showCommentsDialog(Activity activity, final CustomDialogCallBack customDialogCallBack, String title) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.so_approval_dialog);

        final EditText etComments = (EditText) dialog.findViewById(R.id.etComments);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
        tvTitle.setText(title);
        Button okButton = (Button) dialog.findViewById(R.id.btYes);
        Button cancleButton = (Button) dialog.findViewById(R.id.btNo);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (customDialogCallBack != null) {
                    customDialogCallBack.cancelDialogCallBack(true, "", etComments.getText().toString());
                }
            }
        });
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (customDialogCallBack != null) {
                    customDialogCallBack.cancelDialogCallBack(false, "", "");
                }
            }
        });

        dialog.show();

    }

    public static void redirectMainActivity(final Activity mActivity, final int comingFrom){
        UtilConstants.dialogBoxWithCallBack(mActivity, "", mActivity.getString(R.string.so_create_cancel_so_msg), mActivity.getString(R.string.yes), mActivity.getString(R.string.no), false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean clickedStatus) {
                if (clickedStatus) {
                    if (comingFrom == ConstantsUtils.SO_APPROVAL_EDIT_ACTIVITY) {
                        Intent intent = new Intent(mActivity, ApprovalListDetails.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mActivity.startActivity(intent);
                    }else {
                        Intent intent = new Intent(mActivity, CustomerDetailsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mActivity.startActivity(intent);
                    }
                }
            }
        });

    }
    /*get address from array list*/
    public static String getAddressValue(CustomerPartnerFunctionBean customerPartnerFunctionBean) {
        String address = "";
        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getAddress1())) {
            String ad1 = customerPartnerFunctionBean.getAddress1();
            if (!TextUtils.isEmpty(ad1)) {
                address = ad1;
            }
        }
        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getAddress2())) {
            String ad1 = customerPartnerFunctionBean.getAddress2();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getAddress3())) {
            String ad1 = customerPartnerFunctionBean.getAddress3();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }

        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getAddress4())) {
            String ad1 = customerPartnerFunctionBean.getAddress4();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getDistrict())) {
            String ad1 = customerPartnerFunctionBean.getDistrict();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getCityID())) {
            String ad1 = customerPartnerFunctionBean.getCityID();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getRegionDesc())) {
            String ad1 = customerPartnerFunctionBean.getRegionDesc();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getCountryDesc())) {
            String ad1 = customerPartnerFunctionBean.getCountryDesc();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getPostalCode())) {
            String ad1 = customerPartnerFunctionBean.getPostalCode();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + " - " + ad1;
                else
                    address = ad1;
            }
        }
        return address;
    }

    /*check button enable*/
    public static int checkButtonEnable(String totalQty, ArrayList<SOSubItemBean> subItemBeen) {
        int viewType = 1;
        try {
            if (!subItemBeen.isEmpty()) {
                if (!TextUtils.isEmpty(totalQty)) {
                    BigDecimal bTotalQty = new BigDecimal(totalQty);
                    BigDecimal bSubItemQty = new BigDecimal("0");
                    for (SOSubItemBean soSubItemBean : subItemBeen) {
                        try {
                            String subItemQty = soSubItemBean.getSubQty();
                            if (!TextUtils.isEmpty(subItemQty))
                                bSubItemQty = bSubItemQty.add(new BigDecimal(subItemQty));
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                    BigDecimal bsubValue = bSubItemQty.subtract(bTotalQty);
                    if (bTotalQty.equals(bSubItemQty)) {
                        viewType = 0;
                    } else if (bsubValue.compareTo(BigDecimal.ZERO) == 1) {
                        viewType = 2;
                    } else {
                        viewType = 1;
                    }
                }
            } else {
                if (!TextUtils.isEmpty(totalQty)) {
                    viewType = 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return viewType;
    }
    /*check item validation*/
    public static int checkItemValidation(SOItemBean soItems, RecyclerView scheduleView, Context mContext) {
        try {
            BigDecimal totalQty = new BigDecimal(soItems.getSoQty());
            BigDecimal subTotal = new BigDecimal(0);
            int scheduleCount = scheduleView.getChildCount();
            int errorId = 0;
//            int size = soItems.getSoSubItemBeen().size();
            if (scheduleCount > 0) {
                for (int i = 0; i < scheduleCount; i++) {
                    SOSubItemBean soSubItemBean = soItems.getSoSubItemBeen().get(i);
                    if (scheduleView.findViewHolderForLayoutPosition(i) instanceof SOSubItemVH) {
                        SOSubItemVH subItemVH = (SOSubItemVH) scheduleView.findViewHolderForLayoutPosition(i);
                        if (!TextUtils.isEmpty(soSubItemBean.getSubQty())) {
                            BigDecimal subItemValue = new BigDecimal(soSubItemBean.getSubQty());
                            if (subItemValue.compareTo(new BigDecimal(0)) == 0) {
                                subItemVH.etSubQty.setError(mContext.getString(R.string.delivery_qty_zero_error));
                                subItemVH.etSubQty.setBackgroundResource(R.drawable.edittext_border);
                            } else {
                                subTotal = subItemValue.add(subTotal);
                            }
                        } else {
                            subItemVH.etSubQty.setError(mContext.getString(R.string.delivery_qty_error));
                            subItemVH.etSubQty.setBackgroundResource(R.drawable.edittext_border);
                        }
                        if (TextUtils.isEmpty(soSubItemBean.getDateForStore())) {
//                            subItemVH.tvDatePicker.setError(mContext.getString(R.string.select_delivery_schedule_date));
                            subItemVH.tvDatePicker.setBackgroundResource(R.drawable.edittext_border);
                            errorId = 2;
                        } else {
                            boolean isNotPastDate = ConstantsUtils.checkPresentFutureDate(soSubItemBean.getDateForStore());
                            if (!isNotPastDate) {
//                                subItemVH.tvDatePicker.setError(mContext.getString(R.string.delivery_date_not_be_past));
                                subItemVH.tvDatePicker.setBackgroundResource(R.drawable.edittext_border);
                                errorId = 4;
                            }
                        }
                    }
                }
                if (totalQty.compareTo(subTotal) == 0 && errorId == 0) {
                    return 0;
                } else if (errorId == 0) {
                    return 1;
                } else {
                    return errorId;
                }
            } else {
                return 3;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }
    public static String getSOTextId(String types) {
        return Constants.getConfigTypeIndicator(Constants.ConfigTypsetTypeValues,
                Constants.TypeValue, Constants.Types, types, Constants.Typeset, Constants.SFSO);
    }

    public static SOItemBean getItemBasedOnId(ArrayList<SOItemBean> getSoItemBeanArrayList, String getItemId) {
        for (SOItemBean soItemBean : getSoItemBeanArrayList) {
            if (soItemBean.getItemNo().equals(getItemId)) {
                return soItemBean;
            }
        }
        return null;
    }
    /*show cancel dialog*/
    public static void showCancelDialog(Activity activity, final com.rspl.sf.msfa.soDetails.CustomDialogCallBack customDialogCallBack) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.so_cancel_spinner_dialog);

        final Spinner spReject = (Spinner) dialog.findViewById(R.id.spRejectReason);
        String mStrConfigQry = Constants.ConfigTypesetTypes + "?$filter=" + Constants.Typeset + " eq '" + Constants.REJRSN + "' &$orderby = Types asc";
        String[][] mArrayRejReason = null;
        try {
            mArrayRejReason = OfflineManager.getConfigTypesetTypesSO(mStrConfigQry);
        } catch (OnlineODataStoreException e) {
            e.printStackTrace();
        }
        if (mArrayRejReason == null) {
            mArrayRejReason = new String[5][1];
            mArrayRejReason[0][0] = "";
            mArrayRejReason[1][0] = Constants.None;
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, R.layout.custom_textview, mArrayRejReason[1]);
        arrayAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spReject.setAdapter(arrayAdapter);
        final String[][] finalMArrayRejReason = mArrayRejReason;
        spReject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                spReject.setBackgroundResource(R.drawable.spinner_bg);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        Button okButton = (Button) dialog.findViewById(R.id.btYes);
        Button cancleButton = (Button) dialog.findViewById(R.id.btNo);
        TextView tvReason = (TextView) dialog.findViewById(R.id.tv_reason_hint);
        ConstantsUtils.setStarMandatory(tvReason);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPos = spReject.getSelectedItemPosition();
                if (selectedPos > 0) {
                    dialog.dismiss();
                    if (customDialogCallBack != null) {
                        customDialogCallBack.cancelDialogCallBack(true, finalMArrayRejReason[0][selectedPos], finalMArrayRejReason[1][selectedPos]);
                    }
                } else {
                    spReject.setBackgroundResource(R.drawable.error_spinner);
                }
            }
        });
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (customDialogCallBack != null) {
                    customDialogCallBack.cancelDialogCallBack(false, "", "");
                }
            }
        });

        dialog.show();

    }
    public static Drawable displayInvoiceStatusImage(String status, String delvStatus, Context mContext) {
        Drawable img = null;
        int resImage;
        int resColor;
        switch (status) {
            case "01"://open
                resImage = R.drawable.ic_receipt_black_24dp;
                resColor = R.color.StatusDefaultColor;
                if (delvStatus.equalsIgnoreCase("C")) {//over Due
                    resImage = R.drawable.ic_receipt_black_24dp;
                    resColor = R.color.InvStatusRed;
                } else if (delvStatus.equalsIgnoreCase("B")) {//not due
                    resImage = R.drawable.ic_receipt_black_24dp;
                    resColor = R.color.InvStatusGreyColor;
                } else if (delvStatus.equalsIgnoreCase("A")) {//near due
                    resImage = R.drawable.ic_receipt_black_24dp;
                    resColor = R.color.InvStatusOrange;
                }
                img = ContextCompat.getDrawable(mContext, resImage).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, resColor), PorterDuff.Mode.SRC_IN);
                break;
            case "02"://Partially processed
                resImage = R.drawable.ic_account_balance_wallet_black_24dp;
                resColor = R.color.StatusDefaultColor;
                if (delvStatus.equalsIgnoreCase("C")) {//over Due
                    resImage = R.drawable.ic_account_balance_wallet_black_24dp;
                    resColor = R.color.InvStatusRed;
                } else if (delvStatus.equalsIgnoreCase("B")) {//not due
                    resImage = R.drawable.ic_account_balance_wallet_black_24dp;
                    resColor = R.color.InvStatusGreyColor;
                } else if (delvStatus.equalsIgnoreCase("A")) {//near due
                    resImage = R.drawable.ic_account_balance_wallet_black_24dp;
                    resColor = R.color.InvStatusOrange;
                }
                img = ContextCompat.getDrawable(mContext, resImage).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, resColor), PorterDuff.Mode.SRC_IN);
                break;
            case "03"://closed
                resImage = R.drawable.ic_account_balance_wallet_black_24dp;
                resColor = R.color.InvStatusGreen;
                img = ContextCompat.getDrawable(mContext, resImage).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, resColor), PorterDuff.Mode.SRC_IN);
                break;
            default:
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_transparent).mutate();
                break;
        }
        return img;
    }
    public static String getAddressValueInvoice(InvoicePartnerFunctionsBean customerPartnerFunctionBean) {
        String address = "";
        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getAddress1())) {
            String ad1 = customerPartnerFunctionBean.getAddress1();
            if (!TextUtils.isEmpty(ad1)) {
                address = ad1;
            }
        }
        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getAddress2())) {
            String ad1 = customerPartnerFunctionBean.getAddress2();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getAddress3())) {
            String ad1 = customerPartnerFunctionBean.getAddress3();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }

        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getAddress4())) {
            String ad1 = customerPartnerFunctionBean.getAddress4();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getDistrict())) {
            String ad1 = customerPartnerFunctionBean.getDistrict();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getCityID())) {
            String ad1 = customerPartnerFunctionBean.getCityID();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getRegionDesc())) {
            String ad1 = customerPartnerFunctionBean.getRegionDesc();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getCountryDesc())) {
            String ad1 = customerPartnerFunctionBean.getCountryDesc();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getPostalCode())) {
            String ad1 = customerPartnerFunctionBean.getPostalCode();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + " - " + ad1;
                else
                    address = ad1;
            }
        }
        return address;
    }
    public static Drawable displayDueStatusInvoiceFilter(String delvStatus, Context mContext) {
        Drawable img = null;
        int resImage;
        int resColor;
        resImage = R.drawable.ic_stop_black_24dp;
        resColor = R.color.StatusDefaultColor;
        if (delvStatus.equalsIgnoreCase("C")) {//over Due
            resImage = R.drawable.ic_stop_black_24dp;
            resColor = R.color.InvStatusRed;
        } else if (delvStatus.equalsIgnoreCase("B")) {//not due
            resImage = R.drawable.ic_stop_black_24dp;
            resColor = R.color.InvStatusGreyColor;
        } else if (delvStatus.equalsIgnoreCase("A")) {//near due
            resImage = R.drawable.ic_stop_black_24dp;
            resColor = R.color.InvStatusOrange;
        }
        img = ContextCompat.getDrawable(mContext, resImage).mutate();
        img.setColorFilter(ContextCompat.getColor(mContext, resColor), PorterDuff.Mode.SRC_IN);

        return img;
    }
    public static Drawable displayStatusImage(String status, String delvStatus, Context mContext) {
        Drawable img = null;
        switch (status) {
            case "A"://OK
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_assignment_black_24dp).mutate();
                break;
            case "B"://Pending for approval
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
                if (img != null)
                    img.setColorFilter(ContextCompat.getColor(mContext, R.color.PendingApprovalColor), PorterDuff.Mode.SRC_IN);
                break;
            case "C"://Approved
                int resImage = R.drawable.ic_local_shipping_black_24dp;
                int resColor = R.color.StatusDefaultColor;
                if (delvStatus.equalsIgnoreCase("A")) {
                    resImage = R.drawable.ic_shopping_cart_black_24dp;
                    resColor = R.color.ApprovedColor;
                } else if (delvStatus.equalsIgnoreCase("B")) {
                    resImage = R.drawable.ic_local_shipping_black_24dp;
                    resColor = R.color.PartialyClosedColor;
                } else if (delvStatus.equalsIgnoreCase("C")) {
                    resImage = R.drawable.ic_local_shipping_black_24dp;
                    resColor = R.color.ClosedColor;
                }
                img = ContextCompat.getDrawable(mContext, resImage).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, resColor), PorterDuff.Mode.SRC_IN);
                break;
            case "D"://Rejected
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_remove_shopping_cart_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.RejectedColor), PorterDuff.Mode.SRC_IN);
                break;
            default:
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_transparent).mutate();
                break;
        }
        return img;
    }
    public static int getPossATConfig(List<AttendanceConfigTypesetTypesBean> arraData, String id) {
        if (arraData != null) {
            if (arraData.size() > 0) {
                for (int i = 0; i < arraData.size(); i++) {
                    if (arraData.get(i).getTypes().equalsIgnoreCase(id)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public static String getYearFromCalender(Context mContext, String type) {
        Calendar currentDate = Calendar.getInstance();
        if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_last_one_month))) {
            currentDate.set(Calendar.DAY_OF_MONTH, (currentDate.get(Calendar.DAY_OF_MONTH)) - 30);
            return getYear(currentDate.get(Calendar.YEAR));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_today))) {
            return getYear(currentDate.get(Calendar.YEAR));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_today_and_yesterday))) {
            currentDate.set(Calendar.DAY_OF_MONTH, (currentDate.get(Calendar.DAY_OF_MONTH)) - 1);
            return getYear(currentDate.get(Calendar.YEAR));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_current_mont))) {
            currentDate.set(Calendar.DAY_OF_MONTH, 1);
            return getYear(currentDate.get(Calendar.YEAR));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_next_mont))) {
            currentDate.add(Calendar.MONTH, 1);
            currentDate.set(Calendar.DAY_OF_MONTH, 1);
            return getYear(currentDate.get(Calendar.YEAR));
        }else {//if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_last_seven_days)))
            currentDate.set(Calendar.DAY_OF_MONTH, (currentDate.get(Calendar.DAY_OF_MONTH)) - 7);
            return getYear(currentDate.get(Calendar.YEAR));
        }
    }

    public static String getYear(int mYear){
        return mYear+"";
    }

    public static SOListBean getAllSOBean(ArrayList<SalesOrderBean> header, ArrayList<SalesOrderBean> items, String mStrInstanceId) {
        SOListBean soListBean = new SOListBean();
        if (header!=null && !header.isEmpty()){
            SalesOrderBean salesOrderHeaderBean = header.get(0);
            soListBean.setPONo(salesOrderHeaderBean.getPONo());
            soListBean.setRemarks(salesOrderHeaderBean.getRemarks());
            soListBean.setIncoterm2(salesOrderHeaderBean.getIncoterm2());
            soListBean.setPODate(salesOrderHeaderBean.getPODate());
            soListBean.setSoldTo(salesOrderHeaderBean.getSoldTo());
            soListBean.setSoldToName(salesOrderHeaderBean.getSoldToName());
            soListBean.setSalesArea(salesOrderHeaderBean.getSalesArea());
            soListBean.setSalesAreaDesc(salesOrderHeaderBean.getSalesAreaDesc());
            soListBean.setOrderType(salesOrderHeaderBean.getOrderType());
            soListBean.setOrderTypeDesc(salesOrderHeaderBean.getOrderTypeDesc());
            soListBean.setPlant(salesOrderHeaderBean.getPlant());
            soListBean.setPlantDesc(salesOrderHeaderBean.getPlantDesc());
            soListBean.setSONo(salesOrderHeaderBean.getOrderNo());
            soListBean.setOrderDate(salesOrderHeaderBean.getOrderDate());
            soListBean.setInstanceID(mStrInstanceId);
//            soListBean.setUnloadingPointDesc(salesOrderHeaderBean.getU());
//            soListBean.setUnloadingPointId(salesOrderHeaderBean.getPlantDesc());
            soListBean.setIncoTerm1(salesOrderHeaderBean.getIncoTerm1());
            soListBean.setIncoterm1Desc(salesOrderHeaderBean.getIncoterm1Desc());
            soListBean.setShipTo(salesOrderHeaderBean.getShipTo());
            soListBean.setShipToName(salesOrderHeaderBean.getShipToName());
            soListBean.setSalesOfficeId(salesOrderHeaderBean.getSalesOfficeId());
            soListBean.setSaleOffDesc(salesOrderHeaderBean.getSalesOffDesc());
            soListBean.setPaymentTerm(salesOrderHeaderBean.getPaymentTerm());
            soListBean.setPaymentTermDesc(salesOrderHeaderBean.getPaymentTermDesc());
            soListBean.setShippingPoint(salesOrderHeaderBean.getShippingPoint());
            soListBean.setShippingPointDesc(salesOrderHeaderBean.getShippingPointDesc());
            soListBean.setSalesGroup(salesOrderHeaderBean.getSalesGroup());
            soListBean.setSaleGrpDesc(salesOrderHeaderBean.getSalesGrpDesc());
            ArrayList<SOItemBean> getSoItemList = soListBean.getSoItemBeanArrayList();
            for (SalesOrderBean salesOrderBeanItemBean :items){
                SOItemBean soItemBean = new SOItemBean();
                soItemBean.setMatCode(salesOrderBeanItemBean.getMaterialNo());
                soItemBean.setMatDesc(salesOrderBeanItemBean.getMaterialDesc());
                soItemBean.setSoQty(salesOrderBeanItemBean.getQAQty());
                soItemBean.setItemNo(salesOrderBeanItemBean.getsItemNo());
                soItemBean.setItemCategory(salesOrderBeanItemBean.getItemCat());
                soItemBean.setHighLevellItemNo(salesOrderBeanItemBean.getHighLevellItemNo());
                soItemBean.setItemFlag(salesOrderBeanItemBean.getItemFlag());
                soItemBean.setSONo(salesOrderBeanItemBean.getOrderNo());
                soItemBean.setStatusID(salesOrderBeanItemBean.getStatusID());
                try {
                    soItemBean.setUom(salesOrderBeanItemBean.getUom());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getSoItemList.add(soItemBean);
            }
            soListBean.setSoItemBeanArrayList(getSoItemList);
        }
        return soListBean;
    }

    public static Drawable getSODefaultDrawable(Context mContext){
        Drawable img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
        if (img != null)
            img.setColorFilter(ContextCompat.getColor(mContext, R.color.secondaryColor), PorterDuff.Mode.SRC_IN);
        return img;
    }
    public static boolean isHideVisit(Context mContext){
        return true;
    }
    public static Drawable displayReturnOrderStatusImage(String status, String grStatus, Context mContext) {
        Drawable img = null;
        switch (status) {
            case "20"://OK
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_assignment_black_24dp).mutate();
                break;
            case "10"://Pending for approval
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
                if (grStatus.equalsIgnoreCase("A") || grStatus.equalsIgnoreCase("10")) {
                    img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
                    img.setColorFilter(ContextCompat.getColor(mContext, R.color.PendingApprovalColor), PorterDuff.Mode.SRC_IN);
                }
                break;
            case "30"://Approved
                int resImage = R.drawable.ic_local_shipping_black_24dp;
                int resColor = R.color.StatusDefaultColor;
                if (grStatus.equalsIgnoreCase("A") || grStatus.equalsIgnoreCase("10")) {
                    resImage = R.drawable.ic_shopping_cart_black_24dp;
                    resColor = R.color.ApprovedColor;
                } else if (grStatus.equalsIgnoreCase("20")) {
                    resImage = R.drawable.ic_local_shipping_black_24dp;
                    resColor = R.color.PartialyClosedColor;
                } else if (grStatus.equalsIgnoreCase("30")) {
                    resImage = R.drawable.ic_local_shipping_black_24dp;
                    resColor = R.color.ClosedColor;
                }
                img = ContextCompat.getDrawable(mContext, resImage).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, resColor), PorterDuff.Mode.SRC_IN);
                break;
            case "40"://credit note issued
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_assignment_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.RejectedColor), PorterDuff.Mode.SRC_IN);
                if (grStatus.equalsIgnoreCase("30")) {
                    img = ContextCompat.getDrawable(mContext, R.drawable.ic_assignment_black_24dp).mutate();
                    img.setColorFilter(ContextCompat.getColor(mContext, R.color.ClosedColor), PorterDuff.Mode.SRC_IN);
                }
                break;
            case "50"://Replacement
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_add_shopping_cart_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.RejectedColor), PorterDuff.Mode.SRC_IN);
                if (grStatus.equalsIgnoreCase("30")) {
                    img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
                    img.setColorFilter(ContextCompat.getColor(mContext, R.color.ClosedColor), PorterDuff.Mode.SRC_IN);
                }
                if (grStatus.equalsIgnoreCase("10")) {
                    img = ContextCompat.getDrawable(mContext, R.drawable.ic_remove_shopping_cart_black_24dp).mutate();
                    img.setColorFilter(ContextCompat.getColor(mContext, R.color.RejectedColor), PorterDuff.Mode.SRC_IN);
                }
                break;
            case "60"://Rejected
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.PartialyClosedColor), PorterDuff.Mode.SRC_IN);
                break;
            default:
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_transparent).mutate();
                break;
        }
        return img;
    }
    public static Drawable returnGrStatus(String delvStatus, Context mContext) {
        Drawable img = null;
        switch (delvStatus) {
            case "A"://Open
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.OpenColor), PorterDuff.Mode.SRC_IN);
                break;
            case "20"://Partially processed
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_local_shipping_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.PartialyClosedColor), PorterDuff.Mode.SRC_IN);
                break;
            case "30"://Closed
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_local_shipping_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.ClosedColor), PorterDuff.Mode.SRC_IN);
                break;
            case "10"://Rejected
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.OpenColor), PorterDuff.Mode.SRC_IN);
                break;
            case "F"://Not relevant
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_delete_forever_black_24dp).mutate();
                break;
            default:
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_transparent).mutate();
                break;
        }
        return img;
    }
    public static Drawable displayReturnStatus(String status, Context mContext) {
        Drawable img = null;
        switch (status) {
            case "20"://OK
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.PendingApprovalColor), PorterDuff.Mode.SRC_IN);
                break;
            case "10"://Pending for approval
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.PendingApprovalColor), PorterDuff.Mode.SRC_IN);
                break;
            case "30"://Approved
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.ApprovedColor), PorterDuff.Mode.SRC_IN);
                break;
            case "40"://Credit note
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_receipt_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.ApprovedColor), PorterDuff.Mode.SRC_IN);
                break;
            case "50"://Replacement
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_add_shopping_cart_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.ApprovedColor), PorterDuff.Mode.SRC_IN);
                break;
            case "60"://Rejected
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_remove_shopping_cart_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.RejectedColor), PorterDuff.Mode.SRC_IN);
                break;
        }
        return img;
    }
    public static String getStartDate1(Context mContext, String type) {
        Calendar currentDate = Calendar.getInstance();
        if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_last_one_month))) {
            currentDate.set(Calendar.DAY_OF_MONTH, (currentDate.get(Calendar.DAY_OF_MONTH)) - 30);
            return setFromDate1(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_today))) {
            return setFromDate1(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_today_and_yesterday))) {
            currentDate.set(Calendar.DAY_OF_MONTH, (currentDate.get(Calendar.DAY_OF_MONTH)) - 1);
            return setFromDate1(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_current_mont))) {
            currentDate.set(Calendar.DAY_OF_MONTH, 1);
            return setFromDate1(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_next_mont))) {
            currentDate.add(Calendar.MONTH, 1);
            currentDate.set(Calendar.DAY_OF_MONTH, 1);
            return setFromDate1(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        }else {//if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_last_seven_days)))
            currentDate.set(Calendar.DAY_OF_MONTH, (currentDate.get(Calendar.DAY_OF_MONTH)) - 7);
            return setFromDate1(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        }
    }
    private static String setFromDate1(int mYear, int mMonth, int mDay) {
        String mon = "";
        String day = "";
        int mnt = 0;
        mnt = mMonth + 1;
        if (mnt < 10)
            mon = "0" + mnt;
        else
            mon = "" + mnt;
        day = "" + mDay;
        if (mDay < 10)
            day = "0" + mDay;
        return day + "/" + mon + "/" + mYear;
    }

    public static String getEndDate1(Context mContext, String type) {
        Calendar currentDate = Calendar.getInstance();
//        currentDate.set(Calendar.DAY_OF_MONTH,-30);
        if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_last_one_month))) {
            return setFromDate1(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_today))) {
            return setFromDate1(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_today_and_yesterday))) {
            return setFromDate1(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_current_mont))) {
            currentDate.set(Calendar.DAY_OF_MONTH, currentDate.getActualMaximum(Calendar.DAY_OF_MONTH));
            return setFromDate1(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_next_mont))) {
            currentDate.add(Calendar.MONTH, 1);
            currentDate.set(Calendar.DAY_OF_MONTH, currentDate.getActualMaximum(Calendar.DAY_OF_MONTH));
            return setFromDate1(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else {//if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_last_seven_days)))
            return setFromDate1(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        }
    }
}
