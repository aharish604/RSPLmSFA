package com.rspl.sf.msfa.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.registration.UtilRegistrationActivity;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.alldealertarget.AllDealerTargetActivity;
import com.rspl.sf.msfa.attendance.CreateAttendanceActivity;
import com.rspl.sf.msfa.attendance.DayEndRemarksActivity;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.ScrollableGridView;
import com.rspl.sf.msfa.log.LogActivity;
import com.rspl.sf.msfa.mbo.BirthdaysBean;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.mbo.UserLoginBean;
import com.rspl.sf.msfa.mytargetvsactual.MyTargetvsActualTargetActivity;
import com.rspl.sf.msfa.notification.NotificationSetClass;
import com.rspl.sf.msfa.priceUpdate.PriceUpdateActivity;
import com.rspl.sf.msfa.productPrice.ProductPriceActivity;
import com.rspl.sf.msfa.prospectedCustomer.ProspectedCustomerList;
import com.rspl.sf.msfa.registration.Configuration;
import com.rspl.sf.msfa.reports.daySummary.DaySummaryActivity;
import com.rspl.sf.msfa.reports.plantstock.PlantStockActivity;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.store.OnlineManager;
import com.rspl.sf.msfa.store.OnlineODataStoreException;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.impl.ODataDurationDefaultImpl;
import com.sap.smp.client.odata.impl.ODataGuidDefaultImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by e10742 on 05-12-2016.
 */
public class MainMenuFragment extends Fragment implements UIListener {

    private final String[] mArrStrTodayIconName = Constants.todayIconArray;
    private final String[] mArrStrReportsIconName = Constants.reportIconArray;
    private final String[] mArrStrAdminTextName = Constants.admintIconArray;
    public static Context context;
    private int[] mArrIntMainMenuOriginalStatus;
    private final int[] mArrIntMainMenuTempStatus = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,28,29};

//    private final int[] mArrIntMainMenuTempStatus = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,13,14,15,16,17,18,19,20,21};

    private int[] mArrIntMainMenuReportsOriginalStatus = {1, 1, 1};
    private final int[] mArrIntMainMenuReportsTempStatus = {1, 1, 1};

    private final int[] mArrIntAdminOriginalStatus = {0, 0, 0};
    private final int[] mArrIntAdminTempStatus = {0, 1, 2};
    private boolean mBooleanEndFlag = false;
    private boolean mBooleanStartFalg = false;
    private boolean mBooleanCompleteFlag = false;
    private String mStrPreviousDate = "";
    private String mStrSPGUID = "";
    private String mStrPopUpText = "";
    private ProgressDialog pdLoadDialog;
    private String mStrAttendanceId = "";
    String mStrOtherRetailerGuid = "";

    private ODataPropMap oDataProperties;
    private ODataProperty oDataProperty;

    private boolean mBooleanDayStartDialog = false, mBooleanDayEndDialog = false, mBooleanDayResetDialog = false;

    String[][] delList = null;
    View myInflatedView = null;
    ODataGuid mStrVisitId = null;
    String mStrVisitEndRemarks = "";
    private UserLoginBean userLoginBean = null;
    /*    private String userName="";*/
    String appConnID = "", userName = "", pwd = "";


    /**
     * Code used in requesting runtime permissions.
     */
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private LocationManager locationManager;
    private FirebaseJobDispatcher mDispatcher;
    private static final String JOB_TAG = "MyJobService";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        myInflatedView = inflater.inflate(R.layout.fragment_main_menu, container, false);

        /*Initialize user interfaces*/
        initUI();

        return myInflatedView;
    }

    /*Initializes user interfaces*/
    void initUI() {
//        openOfflineStore();
        getDetailsFromLogonContext();
        loadUserLogin();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (pdLoadDialog == null) {
                setIconVisibility();
            } else if (!pdLoadDialog.isShowing()) {
                setIconVisibility();
            }
        } catch (Exception e) {
            setIconVisibility();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /*
     *
     * This class display icons in grid view manner for today's activities
     *
     */
    public class TodayAchievedImageAdapter extends BaseAdapter {
        @SuppressWarnings("unused")
        final Context mContext;

        public TodayAchievedImageAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            int mCountTemp = 0;
            for (int mainMenuOriginalStatu : mArrIntMainMenuOriginalStatus) {
                if (mainMenuOriginalStatu == 1) {
                    mCountTemp++;
                }
            }
            return mCountTemp;
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, final View convertView, ViewGroup parent) {
            int Position = mArrIntMainMenuTempStatus[position];
            View view;
            if (convertView == null) {
                LayoutInflater liAdmin = LayoutInflater.from(getActivity());
                view = liAdmin.inflate(R.layout.mainmenu_inside, parent, false);
            } else {
                view = convertView;
            }
            view.requestFocus();


            final TextView tvIconName = (TextView) view
                    .findViewById(R.id.icon_text);
            tvIconName.setText(mArrStrTodayIconName[Position]);
            final ImageView ivIcon = (ImageView) view
                    .findViewById(R.id.ib_must_sell);
            if (Position == 0) {
                Constants.MapEntityVal.clear();
                mStrPreviousDate = "";
                mStrSPGUID = Constants.getSPGUID(Constants.SPGUID);
                String prvDayQry = Constants.Attendances + "?$filter=EndDate eq null and StartDate ne datetime'" + UtilConstants.getNewDate() + "' and "+Constants.SPGUID+" eq guid'"+mStrSPGUID+"' ";
                try {
                    mStrAttendanceId = OfflineManager.getAttendance(prvDayQry);
                    if (!mStrAttendanceId.equalsIgnoreCase("")) {
                        mStrPreviousDate = UtilConstants.getConvertCalToStirngFormat((Calendar) Constants.MapEntityVal.get(Constants.StartDate));
                    } else {
                        mStrPreviousDate = "";
                    }

                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }

                String dayEndqry = Constants.Attendances + "?$filter=EndDate eq null and "+Constants.SPGUID+" eq guid'"+mStrSPGUID+"'";
                try {
                    mStrAttendanceId = OfflineManager.getAttendance(dayEndqry);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }

                String startDateStr;
                String endDateStr;
                if (Constants.MapEntityVal.isEmpty()) {

                    String dayEndClosedqry = Constants.Attendances + "?$filter=EndDate eq datetime'" + UtilConstants.getNewDate() + "' and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and "+Constants.SPGUID+" eq guid'"+mStrSPGUID+"' ";
                    try {
                        mStrAttendanceId = OfflineManager.getAttendance(dayEndClosedqry);
                    } catch (OfflineODataStoreException e) {
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }

                    startDateStr = UtilConstants.getConvertCalToStirngFormat((Calendar) Constants.MapEntityVal.get(Constants.StartDate));
                    endDateStr = UtilConstants.getConvertCalToStirngFormat((Calendar) Constants.MapEntityVal.get(Constants.EndDate));

                    if (startDateStr.equalsIgnoreCase(UtilConstants.getNewDate()) && endDateStr.equalsIgnoreCase(UtilConstants.getNewDate())) {
                        ivIcon.setImageResource(R.drawable.stop);
                        tvIconName.setText(R.string.tv_complete);
                        mBooleanCompleteFlag = true;
                        mBooleanEndFlag = false;
                        mBooleanStartFalg = true;
                    } else {
                        ivIcon.setImageResource(R.drawable.start);
                    }

                } else {
                    if (Constants.MapEntityVal.get(Constants.EndDate) == null) {
                        ivIcon.setImageResource(R.drawable.stop);
                        tvIconName.setText(R.string.tv_end);
                        mBooleanEndFlag = true;
                    } else {
                        ivIcon.setImageResource(R.drawable.start);
                    }
                }
                ivIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.secondaryColor), android.graphics.PorterDuff.Mode.SRC_IN);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        imageView = ivIcon;
                        textView = tvIconName;

                        pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.checking_pemission));

                        LocationUtils.checkLocationPermission(getActivity(), new LocationInterface() {
                            @Override
                            public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                                closeProgressDialog();
                                if (status) {
                                    onDayStartOrEnd();
                                }
                            }
                        });


                    }
                });
            } else if (Position == 1) {
//                ivIcon.setImageResource(R.drawable.ic_route_plan);
                ivIcon.setImageResource(R.drawable.ic_beat_plan);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        ConstantsUtils.onBeatPlan(getActivity());

                    }
                });
            } else if (Position == 2) {
//                ivIcon.setImageResource(R.drawable.ic_route_plan);
                ivIcon.setImageResource(R.drawable.ic_my_targets);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        ConstantsUtils.onMyTargets(getActivity());

                    }
                });
            } else if (Position == 3) {
                ivIcon.setImageResource(R.drawable.ic_schemes);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        ConstantsUtils.onSchemes(getActivity());

                    }
                });
            } else if (Position == 4) {
                ivIcon.setImageResource(R.drawable.ic_db_stock);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
//                        onDBStockList();
                        ConstantsUtils.onDepotStockList(getActivity());

                    }
                });
            } else if (Position == 5) {
                ivIcon.setImageResource(R.drawable.ic_summary);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
//                        ConstantsUtils.onDaySummary(getActivity());
                        onDaySummary();

                    }
                });
            } else if (Position == 8) {
                ivIcon.setImageResource(R.drawable.ic_adhoc_visit);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        ConstantsUtils.onAdhocList(getActivity());
                    }
                });
            } else if (Position == 9) {

                ivIcon.setImageResource(R.drawable.ic_alerts_bell_icon);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        ConstantsUtils.onAlerts(getActivity());
                    }
                });


            } else if (Position == 10) {

                ivIcon.setImageResource(R.drawable.ic_expenses);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        ConstantsUtils.onExpenseEntry(getActivity());
                    }
                });


            } else if (Position == 11) {

                ivIcon.setImageResource(R.drawable.ic_expenses);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        ConstantsUtils.onExpenseList(getActivity());
                    }
                });


            } else if (Position == 12) {
// change to schems later
                ivIcon.setImageResource(R.drawable.ic_track_com);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        ConstantsUtils.onSoApproval(getActivity());
                        // ConstantsUtils.onSchemes(getActivity());
                    }
                });

            } else if (Position == 13) {

                ivIcon.setImageResource(R.drawable.ic_price);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        //onPriceUpdate();
                        onProductPriceActivityList();
                    }
                });


            } else if (Position == 14) {
                ivIcon.setImageResource(R.drawable.ic_db_stock);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
//                        ConstantsUtils.onTargetVsAchivement(getActivity());
                        onPlantStock();
                    }
                });
            } else if (Position == 15) {
                ivIcon.setImageResource(R.drawable.ic_my_targets);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
//                        ConstantsUtils.onSalesTargetVsAchivement(getActivity());
                        ConstantsUtils.onMTPActivity(getActivity());
                    }
                });
            } /*else if (Position == 16) {
                ivIcon.setImageResource(R.drawable.ic_outstanding);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        ConstantsUtils.onOutstandingAge(getActivity());
                    }
                });
            } else if (Position == 17) {
                ivIcon.setImageResource(R.drawable.ic_dealer_behaviour);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        ConstantsUtils.onDealerBehaviour(getActivity());
                    }
                });
            } else if (Position == 18) {
                ivIcon.setImageResource(R.drawable.ic_track_com);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        ConstantsUtils.onSoApproval(getActivity());
                    }
                });
            } else if (Position == 19) {
                ivIcon.setImageResource(R.drawable.ic_visualnew);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        onVisualAid();
                    }
                });
            } else if (Position == 20) {
                ivIcon.setImageResource(R.drawable.ic_digital_prod);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        onDigitalProducts();
                    }
                });
            }*//*else if(Position ==21){
                ivIcon.setImageResource(R.drawable.ic_summary);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                      //  onDigitalProducts();
                    }
                });


            }*/ /*else if (Position == 21) {
                ivIcon.setImageResource(R.drawable.ic_my_targets);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        ConstantsUtils.onMatWiseTargetVsAchivement(getActivity());
                    }
                });
            } else if (Position == 22) {
                ivIcon.setImageResource(R.drawable.ic_retailer);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        ConstantsUtils.onVisitSummary(getActivity());
                    }
                });


            } else if (Position == 23) {
                ivIcon.setImageResource(R.drawable.ic_retailer);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        ConstantsUtils.onRetailerList(getActivity());
                    }
                });


            } else if (Position == 24) {
                ivIcon.setImageResource(R.drawable.ic_summary);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        ConstantsUtils.onDSREntry(getActivity());
                    }
                });


            } else if (Position == 25) {
                ivIcon.setImageResource(R.drawable.ic_retailer);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                       // onTechnicalActivity();
                    }
                });
            } else if (Position == 26) {
                ivIcon.setImageResource(R.drawable.ic_retailer);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                       // onTechnicalActivityList();
                    }
                });


            }else if (Position == 27) {
                ivIcon.setImageResource(R.drawable.ic_retailer);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                      //  onCreditLimitApprovalActivityList();
                    }
                });
            }else if (Position == 29) {
                ivIcon.setImageResource(R.drawable.ic_retailer);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        ConstantsUtils.onMTPActivity(getActivity());
                    }
                });
            }*/

         /*   else if (Position == 28) {
                ivIcon.setImageResource(R.drawable.ic_price);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        onProductPriceActivityList();
                    }
                });

            }*/
            view.setId(Position);
            return view;
        }


    }
    private void onDaySummary() {
        Intent behaviour = new Intent(getActivity(), DaySummaryActivity.class);
        startActivity(behaviour);

    }
    private void onPlantStock(){
        Intent behaviour = new Intent(getActivity(), PlantStockActivity.class);
        startActivity(behaviour);
    }
    private void onProductPriceActivityList(){
        Intent behaviour = new Intent(getActivity(), ProductPriceActivity.class);
        startActivity(behaviour);
    }

    private void onVisualAid() {
       /* Intent behaviour = new Intent(getActivity(),
                VisualAidActivity.class);
        startActivity(behaviour);*/
    }

   /* private void onTechnicalActivity() {
        Intent behaviour = new Intent(getActivity(), TechnicalActivity.class);
        startActivity(behaviour);
    }

    private void onTechnicalActivityList() {
        Intent behaviour = new Intent(getActivity(), TechnicalListActivity.class);
        startActivity(behaviour);
    }

    private void onCreditLimitApprovalActivityList(){
        Intent behaviour = new Intent(getActivity(), CreditLimitApprovalActivity.class);
        startActivity(behaviour);
    }
    private void onVisualAid() {
        Intent behaviour = new Intent(getActivity(),
                VisualAidActivity.class);
        startActivity(behaviour);
    }*/

    private void onDigitalProducts() {
      /*  Intent behaviour = new Intent(getActivity(),
                DigitalProductActivity.class);
        startActivity(behaviour);*/
    }


    private void attendanceFunctionality(final ImageView ivIcon, final TextView tvIconName) {
        if (mBooleanEndFlag) {
            String message;
            if (mStrPreviousDate.equalsIgnoreCase("")) {
                //For Today
                mStrOtherRetailerGuid = "";
                String otherRetVisitQuery = Constants.Visits + "?$filter=EndDate eq null " +
                        "and StartDate eq datetime'" + UtilConstants.getNewDate() + "'and " + Constants.StatusID + " eq '01'";

                String[] otherRetDetails = new String[2];
                try {
                    otherRetDetails = OfflineManager.checkVisitForOtherRetailer(otherRetVisitQuery);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
                final String[] finalOtherRetDetails = otherRetDetails;

                mStrOtherRetailerGuid = finalOtherRetDetails[1];
                if (mStrOtherRetailerGuid != null && !mStrOtherRetailerGuid.equalsIgnoreCase("")) {
                                         /*
                                         ToDo display alert dialog for visit started but not ended retailer
                                          */
                    AlertDialog.Builder alertDialogVisitEnd = new AlertDialog.Builder(
                            getActivity(), R.style.MyTheme);

                    alertDialogVisitEnd.setMessage(getString(R.string.visit_end_not_marked_for_specific_retailer, otherRetDetails[0]))
                            .setCancelable(false)
                            .setPositiveButton(
                                    R.string.yes,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int id) {
                                            dialog.cancel();

                                            pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.gps_progress));
                                            Constants.getLocation(getActivity(), new LocationInterface() {
                                                @Override
                                                public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                    closeProgressDialog();
                                                    if (status) {
                                                        boolean isVisitActivities = false;
                                                        try {
                                                            isVisitActivities = OfflineManager.checkVisitActivitiesForRetailer(Constants.VisitActivities + "?$filter=" + Constants.VISITKEY + " eq guid'" + mStrOtherRetailerGuid + "'");
                                                        } catch (OfflineODataStoreException e) {
                                                            e.printStackTrace();
                                                        }
                                                        mStrVisitId = ODataGuidDefaultImpl.initWithString36(mStrOtherRetailerGuid);
                                                        if (isVisitActivities) {
                                                            onSaveVisitClose();
                                                        } else {
                                                            wantToCloseDialog = false;
                                                            onAlertDialogForVisitDayEndRemarks();
                                                        }
                                                    }
                                                }
                                            });


                                        }
                                    });
                    alertDialogVisitEnd.setNegativeButton(R.string.no,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }

                            });

                    alertDialogVisitEnd.show();
                } else {
//                    delList = checkNonVisitedRetailers(UtilConstants.getNewDate());

                    String alrtConfMsg = "", alrtNegtiveMsg = "";

                    if (delList == null) {


                        message = getString(R.string.msg_confirm_day_end);
                        alrtConfMsg = getString(R.string.yes);
                        alrtNegtiveMsg = getString(R.string.no);

                    } else {
                        message = getString(R.string.msg_confirm_day_end);
                        alrtConfMsg = getString(R.string.ok);
                        alrtNegtiveMsg = getString(R.string.cancel);
                    }

                                         /*
                                           ToDo display alert dialog for Day end or non visited retailers
                                         */

                    AlertDialog.Builder alertDialogDayEnd = new AlertDialog.Builder(
                            getActivity(), R.style.MyTheme);
                    alertDialogDayEnd.setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton(
                                    alrtConfMsg,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int id) {
                                            dialog.cancel();
                                            if (delList == null) {
                                                pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.gps_progress));
                                                Constants.getLocation(getActivity(), new LocationInterface() {
                                                    @Override
                                                    public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                        closeProgressDialog();
                                                        if (status) {
                                                            mBooleanEndFlag = false;
                                                            tvIconName
                                                                    .setText(R.string.tv_complete);
                                                            mBooleanStartFalg = true;
                                                            mBooleanCompleteFlag = true;
                                                            mStrPopUpText = getString(R.string.msg_update_end);
                                                            mBooleanDayStartDialog = false;
                                                            mBooleanDayEndDialog = true;
                                                            mBooleanDayResetDialog = false;
                                                            onSaveClose();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Intent intentNavEndRemarksScreen = new Intent(getActivity(), DayEndRemarksActivity.class);
                                                intentNavEndRemarksScreen.putExtra(Constants.ClosingeDayType, Constants.Today);
                                                intentNavEndRemarksScreen.putExtra(Constants.ClosingeDay, UtilConstants.getNewDate());
                                                startActivity(intentNavEndRemarksScreen);
                                            }
                                        }

                                    })
                            .setNegativeButton(alrtNegtiveMsg,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int id) {
                                            dialog.cancel();
                                        }

                                    });
                    alertDialogDayEnd.show();
                }

            } else {
                message = getString(R.string.msg_previous_day_end);

                /*
                 *ToDo display alert dialog for previous day is not ended.
                 */
                AlertDialog.Builder alertDialogPreviousDay = new AlertDialog.Builder(getActivity(), R.style.MyTheme);
                alertDialogPreviousDay.setMessage(
                        message)
                        .setCancelable(false)
                        .setPositiveButton(
                                getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();

                                        mStrOtherRetailerGuid = "";

                                        String otherRetVisitQuery = Constants.Visits + "?$filter=EndDate eq null " +
                                                "and StartDate eq datetime'" + mStrPreviousDate + "'and " + Constants.StatusID + " eq '01'";

                                        String[] otherRetDetails = new String[2];
                                        try {
                                            otherRetDetails = OfflineManager.checkVisitForOtherRetailer(otherRetVisitQuery);
                                        } catch (OfflineODataStoreException e) {
                                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                                        }
                                        final String[] finalOtherRetDetails = otherRetDetails;

                                        mStrOtherRetailerGuid = finalOtherRetDetails[1];
                                        if (mStrOtherRetailerGuid != null && !mStrOtherRetailerGuid.equalsIgnoreCase("")) {
                                            /*
                                             *ToDo display alert dialog for visit started but not ended retailer
                                             */
                                            AlertDialog.Builder alertDialogVisitEnd = new AlertDialog.Builder(
                                                    getActivity(), R.style.MyTheme);

                                            alertDialogVisitEnd.setMessage(getString(R.string.visit_end_not_marked_for_specific_retailer, otherRetDetails[0]))
                                                    .setCancelable(false)
                                                    .setPositiveButton(
                                                            R.string.yes,
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(
                                                                        DialogInterface dialog,
                                                                        int id) {
                                                                    dialog.cancel();
                                                                    pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.gps_progress));
                                                                    Constants.getLocation(getActivity(), new LocationInterface() {
                                                                        @Override
                                                                        public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                                            closeProgressDialog();
                                                                            if (status) {
                                                                                boolean isVisitActivities = false;
                                                                                try {
                                                                                    isVisitActivities = OfflineManager.checkVisitActivitiesForRetailer(Constants.VisitActivities + "?$filter=" + Constants.VISITKEY + " eq guid'" + mStrOtherRetailerGuid + "'");
                                                                                } catch (OfflineODataStoreException e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                                mStrVisitId = ODataGuidDefaultImpl.initWithString36(mStrOtherRetailerGuid);
                                                                                if (isVisitActivities) {
                                                                                    onSaveVisitClose();
                                                                                } else {
                                                                                    wantToCloseDialog = false;
                                                                                    onAlertDialogForVisitDayEndRemarks();
                                                                                }
                                                                            }
                                                                        }
                                                                    });


                                                                }
                                                            });
                                            alertDialogVisitEnd.setNegativeButton(R.string.no,
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog,
                                                                            int id) {
                                                            dialog.cancel();
                                                        }

                                                    });

                                            alertDialogVisitEnd.show();
                                        } else {
                                            String msg = "";
//                                            delList = checkNonVisitedRetailers(mStrPreviousDate);

                                            String alrtConfMsg = "", alrtNegtiveMsg = "";

                                            if (delList == null) {
                                                msg = getString(R.string.msg_confirm_day_end);
                                                alrtConfMsg = getString(R.string.yes);
                                                alrtNegtiveMsg = getString(R.string.no);
                                            } else {
                                                msg = getString(R.string.msg_remarks_pending_visit);
                                                alrtConfMsg = getString(R.string.ok);
                                                alrtNegtiveMsg = getString(R.string.cancel);
                                            }

                                                                     /*
                                                                     ToDo display alert dialog for Day end  and non visited retailers
                                                                       */
                                            AlertDialog.Builder alertDialogDayEnd = new AlertDialog.Builder(
                                                    getActivity(), R.style.MyTheme);
                                            alertDialogDayEnd.setMessage(msg)
                                                    .setCancelable(false)
                                                    .setPositiveButton(
                                                            alrtConfMsg,
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(
                                                                        DialogInterface dialog,
                                                                        int id) {
                                                                    dialog.cancel();
                                                                    if (delList == null) {
                                                                        pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.gps_progress));
                                                                        Constants.getLocation(getActivity(), new LocationInterface() {
                                                                            @Override
                                                                            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                                                closeProgressDialog();
                                                                                if (status) {
                                                                                    mStrPopUpText = getString(R.string.msg_update_previous_day_end);
                                                                                    mBooleanDayStartDialog = false;
                                                                                    mBooleanDayEndDialog = true;
                                                                                    mBooleanDayResetDialog = false;

                                                                                    onSaveClose();
                                                                                    mBooleanEndFlag = false;
                                                                                    tvIconName
                                                                                            .setText(R.string.tv_start);
                                                                                    mBooleanStartFalg = false;
                                                                                    mBooleanCompleteFlag = false;
                                                                                    ivIcon.setImageResource(R.drawable.stop);
                                                                                }
                                                                            }
                                                                        });

                                                                    } else {
                                                                        Intent intentNavEndRemarksScreen = new Intent(getActivity(), DayEndRemarksActivity.class);
                                                                        intentNavEndRemarksScreen.putExtra(Constants.ClosingeDayType, Constants.PreviousDay);
                                                                        intentNavEndRemarksScreen.putExtra(Constants.ClosingeDay, mStrPreviousDate);
                                                                        startActivity(intentNavEndRemarksScreen);
                                                                    }
                                                                }

                                                            })
                                                    .setNegativeButton(alrtNegtiveMsg,
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(
                                                                        DialogInterface dialog,
                                                                        int id) {
                                                                    dialog.cancel();
                                                                }

                                                            });
                                            alertDialogDayEnd.show();
                                        }
                                    }

                                })
                        .setNegativeButton(
                                getString(R.string.no),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();
                                    }

                                });
                alertDialogPreviousDay.show();
            }


        } else {

            if (!mBooleanStartFalg) {

                Intent intentNavPrevScreen = new Intent(getActivity(), CreateAttendanceActivity.class);
                startActivity(intentNavPrevScreen);

            }

            if (mBooleanCompleteFlag) {

                                    /*
                                    ToDo display alert dialog for Day end reset
                                     */
                AlertDialog.Builder alertDialogDayEndReset = new AlertDialog.Builder(getActivity(), R.style.MyTheme);
                alertDialogDayEndReset.setMessage(
                        getString(R.string.msg_reset_day_end))
                        .setCancelable(false)
                        .setPositiveButton(
                                //commenting
                                getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();
                                        pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.gps_progress));
                                        Constants.getLocation(getActivity(), new LocationInterface() {
                                            @Override
                                            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                closeProgressDialog();
                                                if (status) {
                                                    ivIcon.setImageResource(R.drawable.stop);
                                                    tvIconName
                                                            .setText(R.string.tv_end);
                                                    mBooleanEndFlag = true;
                                                    mBooleanCompleteFlag = false;
                                                    mBooleanStartFalg = true;


                                                    mBooleanDayStartDialog = false;
                                                    mBooleanDayEndDialog = false;
                                                    mBooleanDayResetDialog = true;
                                                    onCloseUpdate();
                                                }
                                            }
                                        });
                                    }
                                })
                        .setNegativeButton(
                                getString(R.string.no),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();
                                    }

                                });
                alertDialogDayEndReset.show();

            }

        }
    }

    private void onPriceUpdate() {
        Intent intent = new Intent(getActivity(), PriceUpdateActivity.class);
        startActivity(intent);
    }

    //    private void onDealerTargets() {
//       startActivity(new Intent(getActivity(), DealerTargetActivity.class));
//    }
    private void onAllDealerTargets() {
        startActivity(new Intent(getActivity(), AllDealerTargetActivity.class));
    }

    private void onMyTargetVsActualTargets() {
        startActivity(new Intent(getActivity(), MyTargetvsActualTargetActivity.class));
    }


    public boolean checkAlertsRecordsAvailable() {
        ArrayList<BirthdaysBean> alRetBirthDayList = null;
        ArrayList<BirthdaysBean> alAppointmentList = null;
        String[][] oneWeekDay;
        String splitDayMonth[] = null;
        oneWeekDay = UtilConstants.getOneweekValues(1);
        if (oneWeekDay != null && oneWeekDay.length > 0) {
            for (int i = 0; i < oneWeekDay[0].length; i++) {

                splitDayMonth = oneWeekDay[0][i].split("-");

                String mStrBirthdayAvlQry = Constants.ChannelPartners + "?$filter=(month%28" + Constants.DOB + "%29%20eq " + splitDayMonth[0] + " " +
                        "and day%28" + Constants.DOB + "%29%20eq " + UtilConstants.removeLeadingZeros(splitDayMonth[1]) + ") or (month%28" + Constants.Anniversary + "%29%20eq " + splitDayMonth[0] + " " +
                        "and day%28" + Constants.Anniversary + "%29%20eq " + UtilConstants.removeLeadingZeros(splitDayMonth[1]) + ") ";
                try {
                    if (OfflineManager.getVisitStatusForCustomer(mStrBirthdayAvlQry)) {

                        try {
                            alRetBirthDayList = OfflineManager.getTodayBirthDayList(mStrBirthdayAvlQry);
                        } catch (OfflineODataStoreException e) {
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                        }
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }

        }

        String mStrAppointmentListQuery = Constants.Visits + "?$filter=" + Constants.StatusID + " eq '00'";
        try {
            alAppointmentList = OfflineManager.getAppointmentListForAlert(mStrAppointmentListQuery);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if ((alRetBirthDayList != null && alRetBirthDayList.size() > 0) || (alAppointmentList != null && alAppointmentList.size() > 0)) {
            return true;
        } else {
            return false;
        }
    }

    /*
     ToDO Check selected date any pending retailers are available or not
     */
    private String[][] checkNonVisitedRetailers(String selctedDate) {
        String retList[][] = null;
        try {

            String routeQry = Constants.RoutePlans + "?$filter=" + Constants.VisitDate + " eq datetime'" + selctedDate + "'";

            String mGetRouteQry = OfflineManager.getRouteQry(routeQry);

            if (!mGetRouteQry.equalsIgnoreCase("")) {
                mGetRouteQry = Constants.RouteSchedulePlans + "?$filter=" + mGetRouteQry;
                retList = OfflineManager.getNotVisitedRetailerList(mGetRouteQry, UtilConstants.getNewDate());
            } else {
                retList = null;
            }

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        return retList;
    }

    /*
     * This method navigates to log view
     *
     */
    private void onLogView() {
        Intent intentLogView = new Intent(getActivity(), LogActivity.class);
        startActivity(intentLogView);
    }


    /*Ends day*/
    private void onSaveClose() {
        try {
            new ClosingDate().execute();
        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

    }

    /*resets day*/
    private void onCloseUpdate() {
        mStrPopUpText = getString(R.string.msg_resetting_day_end);
        try {
            new ResettingDate().execute();
        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

    }

    /*AsyncTask to Close Attendance for day*/
    private class ClosingDate extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(mStrPopUpText);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);

                Constants.MapEntityVal.clear();

                String qry = Constants.Attendances + "?$filter=EndDate eq null ";
                try {
                    mStrAttendanceId = OfflineManager.getAttendance(qry);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }

                Hashtable hashTableAttendanceValues;

                hashTableAttendanceValues = new Hashtable();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);

                String loginIdVal = sharedPreferences.getString(Constants.username, "");
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.LOGINID, loginIdVal);
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.AttendanceGUID, Constants.MapEntityVal.get(Constants.AttendanceGUID));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.StartDate, Constants.MapEntityVal.get(Constants.StartDate));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.StartTime, Constants.MapEntityVal.get(Constants.StartTime));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.StartLat, Constants.MapEntityVal.get(Constants.StartLat));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.StartLong, Constants.MapEntityVal.get(Constants.StartLong));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.EndLat, BigDecimal.valueOf(UtilConstants.round(UtilConstants.latitude, 12)));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.EndLong, BigDecimal.valueOf(UtilConstants.round(UtilConstants.longitude, 12)));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.EndDate, UtilConstants.getNewDateTimeFormat());

                hashTableAttendanceValues.put(Constants.SPGUID, Constants.getSPGUID(Constants.SPGUID));

                hashTableAttendanceValues.put(Constants.SetResourcePath, Constants.MapEntityVal.get(Constants.SetResourcePath));

                if (Constants.MapEntityVal.get(Constants.Etag) != null) {
                    hashTableAttendanceValues.put(Constants.Etag, Constants.MapEntityVal.get(Constants.Etag));
                } else {
                    hashTableAttendanceValues.put(Constants.Etag, "");
                }

                hashTableAttendanceValues.put(Constants.Remarks, Constants.MapEntityVal.get(Constants.Remarks));
                hashTableAttendanceValues.put(Constants.AttendanceTypeH1, Constants.MapEntityVal.get(Constants.AttendanceTypeH1));
                hashTableAttendanceValues.put(Constants.AttendanceTypeH2, Constants.MapEntityVal.get(Constants.AttendanceTypeH2));

                final Calendar calCurrentTime = Calendar.getInstance();
                int hourOfDay = calCurrentTime.get(Calendar.HOUR_OF_DAY); // 24 hour clock
                int minute = calCurrentTime.get(Calendar.MINUTE);
                int second = calCurrentTime.get(Calendar.SECOND);
                ODataDuration oDataDuration = null;
                try {
                    oDataDuration = new ODataDurationDefaultImpl();
                    oDataDuration.setHours(hourOfDay);
                    oDataDuration.setMinutes(minute);
                    oDataDuration.setSeconds(BigDecimal.valueOf(second));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.EndTime, oDataDuration);

                //noinspection unchecked

                SharedPreferences sharedPreferencesVal = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
                SharedPreferences.Editor editor = sharedPreferencesVal.edit();
                editor.putInt("VisitSeqId", 0);
                editor.commit();

                try {
                    //noinspection unchecked
                    OfflineManager.updateAttendance(hashTableAttendanceValues, MainMenuFragment.this);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
            } catch (InterruptedException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    /*AsyncTask to reset attendance for day*/
    private class ResettingDate extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(mStrPopUpText);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);

                Constants.MapEntityVal.clear();

                String dayEndClosedqry = Constants.Attendances + "?$filter=EndDate eq datetime'" + UtilConstants.getNewDate() + "' and StartDate eq datetime'" + UtilConstants.getNewDate() + "' ";
                try {
                    mStrAttendanceId = OfflineManager.getAttendance(dayEndClosedqry);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }

                Hashtable hashTableAttendanceValues;


                hashTableAttendanceValues = new Hashtable();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);

                String loginIdVal = sharedPreferences.getString(Constants.username, "");
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.LOGINID, loginIdVal);
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.AttendanceGUID, Constants.MapEntityVal.get(Constants.AttendanceGUID));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.StartDate, Constants.MapEntityVal.get(Constants.StartDate));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.StartTime, Constants.MapEntityVal.get(Constants.StartTime));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.StartLat, Constants.MapEntityVal.get(Constants.StartLat));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.StartLong, Constants.MapEntityVal.get(Constants.StartLong));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.EndLat, "");
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.EndLong, "");
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.EndDate, "");

                hashTableAttendanceValues.put(Constants.Remarks, Constants.MapEntityVal.get(Constants.Remarks));
                hashTableAttendanceValues.put(Constants.AttendanceTypeH1, Constants.MapEntityVal.get(Constants.AttendanceTypeH1));
                hashTableAttendanceValues.put(Constants.AttendanceTypeH2, Constants.MapEntityVal.get(Constants.AttendanceTypeH2));

                hashTableAttendanceValues.put(Constants.SPGUID, Constants.getSPGUID(Constants.SPGUID));

                hashTableAttendanceValues.put(Constants.SetResourcePath, Constants.MapEntityVal.get(Constants.SetResourcePath));

                if (Constants.MapEntityVal.get(Constants.Etag) != null) {
                    hashTableAttendanceValues.put(Constants.Etag, Constants.MapEntityVal.get(Constants.Etag));
                } else {
                    hashTableAttendanceValues.put(Constants.Etag, "");
                }

                final Calendar calCurrentTime = Calendar.getInstance();
                int hourOfDay = calCurrentTime.get(Calendar.HOUR_OF_DAY); // 24 hour clock
                int minute = calCurrentTime.get(Calendar.MINUTE);
                int second = calCurrentTime.get(Calendar.SECOND);
                ODataDuration oDataDuration = null;
                try {
                    oDataDuration = new ODataDurationDefaultImpl();
                    oDataDuration.setHours(hourOfDay);
                    oDataDuration.setMinutes(minute);
                    oDataDuration.setSeconds(BigDecimal.valueOf(second));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.EndTime, "");

                try {
                    //noinspection unchecked
                    OfflineManager.resetAttendanceEntity(hashTableAttendanceValues, MainMenuFragment.this);
                } catch (OfflineODataStoreException e) {
//					e.printStackTrace();
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
            } catch (InterruptedException e) {
//				e.printStackTrace();
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private void setAppointmentNotification() {
/*        ArrayList<AppointmentBean> temp;
        temp = NotificationSetClass.getAppointmentList();*/
    }

    @Override
    public void onRequestError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e, getContext());
        if (errorBean.hasNoError()) {
            Toast.makeText(getActivity(), getString(R.string.err_odata_unexpected, e.getMessage()),
                    Toast.LENGTH_LONG).show();

            if (mBooleanDayStartDialog)
                mStrPopUpText = getString(R.string.msg_start_upd_sync_error);
            else if (mBooleanDayEndDialog)
                mStrPopUpText = getString(R.string.msg_end_upd_sync_error);
            else if (mBooleanDayResetDialog) {
                mStrPopUpText = getString(R.string.msg_reset_upd_sync_error);
            }
            if (mStrPopUpText.equalsIgnoreCase("")) {
                try {
                    mStrPopUpText = errorBean.getErrorMsg();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            if (operation == Operation.Create.getValue()) {
                try {
                    pdLoadDialog.dismiss();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                displayPopUpMsg();
            } else if (operation == Operation.Update.getValue()) {
                try {
                    pdLoadDialog.dismiss();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                displayPopUpMsg();
            } else if (operation == Operation.OfflineFlush.getValue()) {
                try {
                    pdLoadDialog.dismiss();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                displayPopUpMsg();
            } else if (operation == Operation.OfflineRefresh.getValue()) {
                try {
                    pdLoadDialog.dismiss();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                displayPopUpMsg();
            } else if (operation == Operation.GetStoreOpen.getValue()) {
                try {

                    try {
                        pdLoadDialog.dismiss();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    UtilConstants.showAlert(getString(R.string.msg_offline_store_failure), getActivity());
                    setIconVisibility();
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        } else {
            try {
                pdLoadDialog.dismiss();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            Constants.displayMsgReqError(errorBean.getErrorCode(), getActivity());
            setIconVisibility();
        }
    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.Create.getValue()) {
            if (Constants.getSyncType(getActivity(), Constants.Attendances, Constants.CreateOperation).equalsIgnoreCase("4")) {
                try {
                    pdLoadDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (mBooleanDayStartDialog)
                    mStrPopUpText = getString(R.string.dialog_day_started);
                else if (mBooleanDayEndDialog)
                    mStrPopUpText = getString(R.string.dialog_day_ended);
                else if (mBooleanDayResetDialog)
                    mStrPopUpText = getString(R.string.dialog_day_reset);
                else if (!mStrOtherRetailerGuid.equalsIgnoreCase(""))
                    mStrPopUpText = getString(R.string.visit_ended);

                displayPopUpMsg();
            } else {
                if (!UtilConstants.isNetworkAvailable(getActivity())) {
                    try {
                        pdLoadDialog.dismiss();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    UtilConstants.onNoNetwork(getActivity());
                } else {
                    OfflineManager.flushQueuedRequests(MainMenuFragment.this);
                }
            }
        } else if (operation == Operation.Update.getValue()) {
            if (Constants.getSyncType(getActivity(), Constants.Attendances, Constants.UpdateOperation).equalsIgnoreCase("4")) {

                try {
                    pdLoadDialog.dismiss();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                if (mBooleanDayStartDialog)
                    mStrPopUpText = getString(R.string.dialog_day_started);
                else if (mBooleanDayEndDialog)
                    mStrPopUpText = getString(R.string.dialog_day_ended);
                else if (mBooleanDayResetDialog)
                    mStrPopUpText = getString(R.string.dialog_day_reset);
                try {
                    if (!mStrOtherRetailerGuid.equalsIgnoreCase(""))
                        mStrPopUpText = getString(R.string.visit_ended);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                displayPopUpMsg();
            } else {
                if (!UtilConstants.isNetworkAvailable(getActivity())) {
                    try {
                        pdLoadDialog.dismiss();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    UtilConstants.onNoNetwork(getActivity());
                } else {
                    OfflineManager.flushQueuedRequests(MainMenuFragment.this);
                }
            }

        } else if (operation == Operation.OfflineFlush.getValue()) {

            if (Constants.getSyncType(getActivity(), Constants.Attendances, Constants.ReadOperation).equalsIgnoreCase("4")) {
                try {
                    pdLoadDialog.dismiss();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                if (mBooleanDayStartDialog)
                    mStrPopUpText = getString(R.string.dialog_day_started);
                else if (mBooleanDayEndDialog)
                    mStrPopUpText = getString(R.string.dialog_day_ended);
                else if (mBooleanDayResetDialog)
                    mStrPopUpText = getString(R.string.dialog_day_reset);
                else if (!mStrOtherRetailerGuid.equalsIgnoreCase(""))
                    mStrPopUpText = getString(R.string.visit_ended);

                displayPopUpMsg();
            } else {
                if (!UtilConstants.isNetworkAvailable(getActivity())) {
                    try {
                        pdLoadDialog.dismiss();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    UtilConstants.onNoNetwork(getActivity());
                } else {

                    String allCollection = "";
                    if (mBooleanDayStartDialog) {
                        allCollection = Constants.Attendances + "," + Constants.SPStockItems + "," + Constants.SPStockItemDetails + "," + Constants.SPStockItemSNos + "," + Constants.SFINVOICES + "," + Constants.SSInvoiceItemDetails
                                + "," + Constants.SSInvoiceItemSerials + "," + Constants.FinancialPostings
                                + "," + Constants.FinancialPostingItemDetails
                                + "," + Constants.CPStockItems + "," + Constants.CPStockItemDetails + "," + Constants.CPStockItemSnos + "," + Constants.Schemes + "," + Constants.Tariffs + "," + Constants.SegmentedMaterials;
                    } else {
                        allCollection = Constants.Attendances;
                    }


                    OfflineManager.refreshRequests(getActivity(), allCollection, MainMenuFragment.this);
                }
            }


        } else if (operation == Operation.OfflineRefresh.getValue()) {
            try {
                pdLoadDialog.dismiss();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            if (mBooleanDayStartDialog)
                mStrPopUpText = getString(R.string.dialog_day_started);
            else if (mBooleanDayEndDialog)
                mStrPopUpText = getString(R.string.dialog_day_ended);
            else if (mBooleanDayResetDialog)
                mStrPopUpText = getString(R.string.dialog_day_reset);

            displayPopUpMsg();
        } else if (operation == Operation.GetStoreOpen.getValue()) {
            new NotificationSetClass(getContext());
            try {
                OfflineManager.getAuthorizations(getActivity());
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFS_NAME,
                    0);
            if (settings.getBoolean(Constants.isFirstTimeReg, false)) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(Constants.isFirstTimeReg, false);
                editor.commit();
                try {
                    OfflineManager.getAuthorizations(getActivity());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                try {
                    /*String syncTime = Constants.getSyncHistoryddmmyyyyTime();
                    String[] DEFINGREQARRAY = Constants.getDefinigReq(getActivity());


                    for (int incReq = 0; incReq < DEFINGREQARRAY.length; incReq++) {
                        String colName = DEFINGREQARRAY[incReq];
                        if (colName.contains("?$")) {
                            String splitCollName[] = colName.split("\\?");
                            colName = splitCollName[0];
                        }

                        Constants.events.updateStatus(Constants.SYNC_TABLE,
                                colName, Constants.TimeStamp, syncTime
                        );
                    }*/
                    List<String > DEFINGREQARRAY = Arrays.asList(Constants.getDefinigReq(getActivity()));
                    //     Constants.updateSyncTime(DEFINGREQARRAY,getActivity(),Constants.Sync_All);
                } catch (Exception exce) {
                    LogManager.writeLogError(Constants.sync_table_history_txt + exce.getMessage());
                }
            }

            try {

                try {
                    pdLoadDialog.dismiss();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                setIconVisibility();
                //setAppointmentNotification();
            } catch (Exception e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }

        }

    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }


    public void displayPopUpMsg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyTheme);
        builder.setMessage(mStrPopUpText)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();
                                    setIconVisibility();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });
        builder.show();
    }

    /*Sets icons visibility in screen for different T-Codes*/
    private void setIconVisibility() {

        if (checkAlertsRecordsAvailable())
            Constants.isAlertRecordsAvailable = true;
        else
            Constants.isAlertRecordsAvailable = false;

        mArrIntMainMenuOriginalStatus = new int[]{0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0};

        TextView tv_today_achieved = (TextView) myInflatedView.findViewById(R.id.tv_today_achieved);
        tv_today_achieved.setText(getString(R.string.str_concat_today_achieved, "0 %"));

        TextView tv_visit_achieved = (TextView) myInflatedView.findViewById(R.id.tv_visit_achieved);
//        tv_visit_achieved.setText(getString(R.string.str_concat_visit_achieved, Constants.getVisitedRetailerCount(), Constants.getVisitTargetForToday()));
        tv_visit_achieved.setText(getString(R.string.str_concat_visit_achieved, Constants.getVisitedRetailerCount(), "0"));
        mBooleanEndFlag = false;
        mBooleanStartFalg = false;
        mBooleanCompleteFlag = false;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);

        Constants.setIconVisibilty(sharedPreferences, mArrIntMainMenuOriginalStatus, mArrIntMainMenuReportsOriginalStatus);

        int arrInc = 0;
        int len = mArrIntMainMenuOriginalStatus.length;
        for (int incVal = 0; incVal < len; incVal++) {
            if (mArrIntMainMenuOriginalStatus[incVal] == 1) {
                mArrIntMainMenuTempStatus[arrInc] = incVal;
                arrInc++;
            }
        }


        arrInc = 0;
        len = mArrIntMainMenuReportsOriginalStatus.length;
        for (int incVal = 0; incVal < len; incVal++) {
            if (mArrIntMainMenuReportsOriginalStatus[incVal] == 1) {
                mArrIntMainMenuReportsTempStatus[arrInc] = incVal;
                arrInc++;
            }
        }

        mArrIntAdminOriginalStatus[0] = 1;
        mArrIntAdminOriginalStatus[1] = 1;
        mArrIntAdminOriginalStatus[2] = 1;
        arrInc = 0;
        len = mArrIntAdminOriginalStatus.length;
        for (int incVal = 0; incVal < len; incVal++) {
            if (mArrIntAdminOriginalStatus[incVal] == 1) {
                mArrIntAdminTempStatus[arrInc] = incVal;
                arrInc++;
            }
        }

        GridView gvTodayAchieved = (ScrollableGridView) getActivity().findViewById(R.id.gv_today_view);
        gvTodayAchieved.setAdapter(new TodayAchievedImageAdapter(getActivity()));

        GridView gvReportsView = (ScrollableGridView) getActivity().findViewById(R.id.gv_reports);
        gvReportsView.setAdapter(new ReportsImageAdapter(getActivity()));

        GridView gvAdminView = (ScrollableGridView) getActivity().findViewById(R.id.gv_admin);
        gvAdminView.setAdapter(new AdminImageAdapter(getActivity()));

        checkForPendingUpdates();

    }

    void checkForPendingUpdates() {
        try {
            if (OfflineManager.offlineStore != null) {
                if (!OfflineManager.offlineStore.getRequestQueueIsEmpty() || checkPendingReqIsAval()) {
                    Toast.makeText(getActivity(), getString(R.string.please_update_pending_requests), Toast.LENGTH_LONG).show();
                }
            }
        } catch (ODataException e) {
            e.printStackTrace();
        }
    }

    private boolean checkPendingReqIsAval() {
        try {
            Set<String> set = new HashSet<>();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
            set = sharedPreferences.getStringSet("InvList", null);
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
                while (itr.hasNext()) {
                    return true;
                }
            }
            set = sharedPreferences.getStringSet("CollList", null);
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
                while (itr.hasNext()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /*
     *
     * This class display admin icons in grid view manner
     *
     */
    public class AdminImageAdapter extends BaseAdapter {
        @SuppressWarnings("unused")
        final Context mContext;

        public AdminImageAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            int counttemp = 0;
            for (int adminOriginalStatu : mArrIntAdminOriginalStatus) {
                if (adminOriginalStatu == 1) {
                    counttemp++;
                }
            }
            return counttemp;
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int Position = mArrIntAdminTempStatus[position];
            View view;
            if (convertView == null) {
                LayoutInflater liAdmin = LayoutInflater.from(getActivity());
                view = liAdmin.inflate(R.layout.mainmenu_inside, parent, false);
            } else {
                view = convertView;
            }
            view.requestFocus();
            final TextView tvIconName = (TextView) view
                    .findViewById(R.id.icon_text);
            tvIconName.setText(mArrStrAdminTextName[Position]);
            final ImageView ivIcon = (ImageView) view
                    .findViewById(R.id.ib_must_sell);
            if (Position == 0) {
                ivIcon.setImageResource(R.drawable.ic_sync_black_24dp);
                ivIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.secondaryColor), android.graphics.PorterDuff.Mode.SRC_IN);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        ConstantsUtils.onSyncView(getActivity());
                    }
                });
            }
            if (Position == 1) {
                ivIcon.setImageResource(R.drawable.ic_reorder_black_24dp);
                ivIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.secondaryColor), android.graphics.PorterDuff.Mode.SRC_IN);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        onLogView();
                    }
                });
            }
            view.setId(Position);
            return view;
        }
    }

    /*
     *
     * This class display admin icons in grid view manner
     *
     */
    public class ReportsImageAdapter extends BaseAdapter {
        @SuppressWarnings("unused")
        final Context mContext;

        public ReportsImageAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            int counttemp = 0;
            for (int adminOriginalStatu : mArrIntMainMenuReportsOriginalStatus) {
                if (adminOriginalStatu == 1) {
                    counttemp++;
                }
            }
            return counttemp;
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            int Position = mArrIntMainMenuReportsTempStatus[position];
            View view;
            if (convertView == null) {
                LayoutInflater liAdmin = LayoutInflater.from(getActivity());
                view = liAdmin.inflate(R.layout.mainmenu_inside, parent, false);
            } else {
                view = convertView;
            }
            view.requestFocus();
            final TextView tvIconName = (TextView) view
                    .findViewById(R.id.icon_text);
            tvIconName.setText(mArrStrReportsIconName[position]);
            final ImageView ivIcon = (ImageView) view
                    .findViewById(R.id.ib_must_sell);

            if (position == 0) {
                ivIcon.setImageResource(R.drawable.ic_people_black_24dp);
                ivIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.secondaryColor), android.graphics.PorterDuff.Mode.SRC_IN);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        ConstantsUtils.onCustomerList(getActivity());
                    }
                });
            } else if (position == 1) {
                ivIcon.setImageResource(R.drawable.ic_retailer);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {

                        Intent retList = new Intent(getActivity(),
                                ProspectedCustomerList.class);
                        startActivity(retList);
                    }
                });
            } else if (position == 2) {
                ivIcon.setImageResource(R.drawable.ic_retailer);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {

                       /* Intent retList = new Intent(getActivity(), IHBActivity.class);
                        startActivity(retList);*/
                    }
                });
            }


            view.setId(position);
            return view;
        }
    }

    /*checks whether store open or not and if not opened opens store*/
    private void openOfflineStore() {
        if (OfflineManager.offlineStore != null) {
            if (!OfflineManager.isOfflineStoreOpen()) {
                try {
                    OfflineManager.openOfflineStore(getActivity(), MainMenuFragment.this);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                new OpenOfflineStore().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
     *
     * AsyncTask for opening offline store
     *
     */
    private class OpenOfflineStore extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);

                try {

                    OfflineManager.openOfflineStore(getActivity(), MainMenuFragment.this);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }


            } catch (InterruptedException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    /*gets service document for */


    boolean wantToCloseDialog = false;
    /*
             TODO Enter remarks in visit table if activity is not done.

             */


    private void onAlertDialogForVisitDayEndRemarks() {
        AlertDialog.Builder alertDialogVisitEndRemarks = new AlertDialog.Builder(getActivity(), R.style.MyTheme);
        alertDialogVisitEndRemarks.setMessage(R.string.alert_plz_enter_remarks);
        alertDialogVisitEndRemarks.setCancelable(false);
        int MAX_LENGTH = 255;

        final EditText etVisitEndRemarks = new EditText(getActivity());

        if (wantToCloseDialog) {
            etVisitEndRemarks.setBackgroundResource(R.drawable.edittext_border);

        } else {
            etVisitEndRemarks.setBackgroundResource(R.drawable.edittext);
        }

        etVisitEndRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (wantToCloseDialog) {
                    etVisitEndRemarks.setBackgroundResource(R.drawable.edittext_border);
                    wantToCloseDialog = false;
                } else {
                    etVisitEndRemarks.setBackgroundResource(R.drawable.edittext);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(MAX_LENGTH);
        etVisitEndRemarks.setFilters(FilterArray);

        etVisitEndRemarks.setText(mStrVisitEndRemarks.equalsIgnoreCase("") ? mStrVisitEndRemarks : "");
        etVisitEndRemarks.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        etVisitEndRemarks.setLayoutParams(lp);
        alertDialogVisitEndRemarks.setView(etVisitEndRemarks);
        alertDialogVisitEndRemarks.setPositiveButton(R.string.save,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mStrVisitEndRemarks = etVisitEndRemarks.getText().toString();
                        if (mStrVisitEndRemarks.equalsIgnoreCase("")) {
                            wantToCloseDialog = true;
                            onAlertDialogForVisitDayEndRemarks();
                        } else {
                            wantToCloseDialog = false;
                            onSaveVisitClose();
                        }
                    }
                });

        alertDialogVisitEndRemarks.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        mStrVisitEndRemarks = etVisitEndRemarks.getText().toString();
                    }
                });
        AlertDialog alertDialog = alertDialogVisitEndRemarks.create();
        alertDialog.show();

    }

    private void onSaveVisitClose() {
        mStrPopUpText = getString(R.string.marking_visit_end_plz_wait);
        try {
            new ClosingVisit().execute();
        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }


    /*
    TODO Async task for Closing Visit End
    */
    private class ClosingVisit extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(mStrPopUpText);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(100);

                Hashtable table = new Hashtable();

                try {

                    if (!mStrOtherRetailerGuid.equalsIgnoreCase("")) {
                        mStrVisitId = ODataGuidDefaultImpl.initWithString36(mStrOtherRetailerGuid);
                    }
                    ODataEntity visitEntity;
                    visitEntity = OfflineManager.getVisitDetailsByKey(mStrVisitId);

                    if (visitEntity != null) {
                        oDataProperties = visitEntity.getProperties();
                        oDataProperty = oDataProperties.get(Constants.StartLat);
                        //noinspection unchecked
                        table.put(Constants.StartLat, oDataProperty.getValue());
                        oDataProperty = oDataProperties.get(Constants.StartLong);
                        //noinspection unchecked
                        table.put(Constants.StartLong, oDataProperty.getValue());
                        oDataProperty = oDataProperties.get(Constants.STARTDATE);
                        //noinspection unchecked
                        table.put(Constants.STARTDATE, oDataProperty.getValue());

                        oDataProperty = oDataProperties.get(Constants.STARTTIME);
                        //noinspection unchecked
                        table.put(Constants.STARTTIME, oDataProperty.getValue());

                        //noinspection unchecked
                        table.put(Constants.EndLat, BigDecimal.valueOf(UtilConstants.round(UtilConstants.latitude, 12)));
                        //noinspection unchecked
                        table.put(Constants.EndLong, BigDecimal.valueOf(UtilConstants.round(UtilConstants.longitude, 12)));
                        //noinspection unchecked
                        table.put(Constants.ENDDATE, UtilConstants.getNewDateTimeFormat());

                        //noinspection unchecked
                        oDataProperty = oDataProperties.get(Constants.CPNo);
                        table.put(Constants.CPNo, UtilConstants.removeLeadingZeros((String) (oDataProperty.getValue())));
                        //noinspection unchecked
                        table.put(Constants.VISITKEY, mStrVisitId.guidAsString36().toUpperCase());
                        //noinspection unchecked
                        table.put(Constants.Remarks, mStrVisitEndRemarks);

                        table.put(Constants.SPGUID, Constants.getSPGUID(Constants.SPGUID));

                        oDataProperty = oDataProperties.get(Constants.ROUTEPLANKEY);

                        //noinspection unchecked
                        if (oDataProperty.getValue() == null) {
                            table.put(Constants.ROUTEPLANKEY, "");
                        } else {
                            ODataGuid mRouteGuid = (ODataGuid) oDataProperty.getValue();

                            table.put(Constants.ROUTEPLANKEY, mRouteGuid.guidAsString36().toUpperCase());
                        }


                        oDataProperty = oDataProperties.get(Constants.StatusID);
                        table.put(Constants.StatusID, oDataProperty.getValue());

                        oDataProperty = oDataProperties.get(Constants.CPTypeID);
                        table.put(Constants.CPTypeID, oDataProperty.getValue());

                        table.put(Constants.VisitDate, UtilConstants.getNewDateTimeFormat());

                        oDataProperty = oDataProperties.get(Constants.VisitSeq);
                        table.put(Constants.VisitSeq, oDataProperty.getValue());

                        oDataProperty = oDataProperties.get(Constants.CPGUID);
                        table.put(Constants.CPGUID, oDataProperty.getValue());


                        final Calendar calCurrentTime = Calendar.getInstance();
                        int hourOfDay = calCurrentTime.get(Calendar.HOUR_OF_DAY); // 24 hour clock
                        int minute = calCurrentTime.get(Calendar.MINUTE);
                        int second = calCurrentTime.get(Calendar.SECOND);
                        ODataDuration oDataDuration = null;
                        try {
                            oDataDuration = new ODataDurationDefaultImpl();
                            oDataDuration.setHours(hourOfDay);
                            oDataDuration.setMinutes(minute);
                            oDataDuration.setSeconds(BigDecimal.valueOf(second));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        table.put(Constants.ENDTIME, oDataDuration);

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
                        String loginIdVal = sharedPreferences.getString("username", "");
                        //noinspection unchecked
                        table.put(Constants.LOGINID, loginIdVal);

                        table.put(Constants.SetResourcePath, Constants.Visits + "(guid'" + mStrVisitId.guidAsString36().toUpperCase() + "')");

                        if (visitEntity.getEtag() != null) {
                            table.put(Constants.Etag, visitEntity.getEtag());
                        } else {
                        }

                    }
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
                try {
                    //noinspection unchecked
                    OfflineManager.updateVisit(table, MainMenuFragment.this);
                } catch (OfflineODataStoreException e) {
//                    e.printStackTrace();
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }


            } catch (InterruptedException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case UtilConstants.Location_PERMISSION_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationUtils.checkLocationPermission(getActivity(), new LocationInterface() {
                        @Override
                        public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                            if (status) {
                                onDayStartOrEnd();
                            }
                        }
                    });
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }


        }
        // other 'case' lines to check for other
        // permissions this app might request
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LocationUtils.REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                onDayStartOrEnd();
            }
        }
    }

    private void getNonVisitedDealers(String strDate) {
        try {
            new GetNonVistedRetailers().execute(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ImageView imageView = null;
    TextView textView = null;

    private void onDayStartOrEnd() {
        if (mBooleanEndFlag) {
            if (mStrPreviousDate.equalsIgnoreCase("")) {
                getNonVisitedDealers(UtilConstants.getNewDate());
            } else {
                getNonVisitedDealers(mStrPreviousDate);
            }
        } else {
            attendanceFunctionality(imageView, textView);
        }
    }

    private class GetNonVistedRetailers extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            String mStrDate = params[0];
            try {
                Thread.sleep(1000);
                delList = checkNonVisitedRetailers(mStrDate);
//                delList = DayEndRemarksActivity.getDealer(mStrDate);

            } catch (InterruptedException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                pdLoadDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            attendanceFunctionality(imageView, textView);
        }
    }

    private void closeProgressDialog() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void getDetailsFromLogonContext() {
        // TO-DO Delete try catch after checking invoice create
//        try {
        String endPointURL = "";
        ;
        try {
            // get Application Connection ID
            /*LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();
            endPointURL = lgCtx.getAppEndPointUrl();
            userName = lgCtx.getBackendUser();
            pwd = lgCtx.getBackendPassword();*/
            SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
            userName = sharedPref.getString(UtilRegistrationActivity.KEY_username,"");
            pwd = sharedPref.getString(UtilRegistrationActivity.KEY_password,"");
            appConnID = Configuration.APP_ID;
            //   Constants.mApplication.getParameters(userName, pwd);
        } catch (Exception e) {
            LogManager.writeLogError(this.getClass().getSimpleName() + ".getDetailsFromLogonContext: " + e.getMessage());
        }
    }

    private void loadUserLogin() {
        try {
            SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFS_NAME,
                    0);
            if (settings.getBoolean(Constants.isFirstTimeReg, false)) {
                try {
                    new OpenOnlineStore().execute();
                } catch (Exception e) {
//                    e.printStackTrace();
                    LogManager.writeLogError(this.getClass().getSimpleName() + ".loadUserLogin: " + e.getMessage());
                }
            } else {
                openOfflineStore();
            }
        } catch (Exception e) {
            LogManager.writeLogError(this.getClass().getSimpleName() + ".loadUserLogin: " + e.getMessage());
        }
    }


    private class OpenOnlineStore extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                userLoginBean = null;
                Constants.ErrorCode = 0;
                Constants.ErrorNo = 0;
                Constants.ErrorName = "";
                Constants.IsOnlineStoreFailed = false;
//                OnlineStoreListener.instance = null;

                 /*   String mStrUserRoleQry = Constants.UserLogins + "/?$filter=" + Constants.Application +
                        "+eq+'" + Constants.ApplicationTypeID + "'+and+" + Constants.LoginID + "+eq+'" + userName.toUpperCase() + "'";*/
                 /*  String mStrUserRoleQry = Constants.UserLogins + "/?$filter=" + Constants.Application +
                        "+eq+'" + "PU" + "'+and+" + Constants.LoginID + "+eq+'" + userName.toUpperCase() + "'";*/
                String mStrUserRoleQry = Constants.UserProfiles + "(Application='PD')"/* + "'+and+" + Constants.LoginID + "+eq+'" + userName.toUpperCase() + "'"*/;


                OnlineManager.openOnlineStore(getActivity(), false);

                userLoginBean = OnlineManager.getUserLogin(mStrUserRoleQry);

            } catch (OnlineODataStoreException e) {
                LogManager.writeLogError(this.getClass().getSimpleName() + ".doInBackground: " + e.getMessage());
            } catch (InterruptedException e) {
                LogManager.writeLogError(this.getClass().getSimpleName() + ".doInBackground: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (userLoginBean != null) {
                SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFS_NAME,
                        0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(Constants.USERROLE, userLoginBean.getRoleID());
                editor.commit();
                closeProgressDialog();
                openOfflineStore();
                Constants.getSyncHistoryTable(getActivity());
            } else {
                onClosingProgressDialog();
                setIconVisibility();
                if (Constants.ErrorNo == Constants.Network_Error_Code && Constants.ErrorName.equalsIgnoreCase(Constants.NetworkError_Name)) {
                    Constants.showAlert(getString(R.string.data_conn_lost_during_sync_error_code, Constants.ErrorNo + ""), getActivity());

                } else if (Constants.ErrorNo == Constants.UnAuthorized_Error_Code && Constants.ErrorName.equalsIgnoreCase(Constants.NetworkError_Name)) {
                    Constants.showAlert(getString(R.string.auth_fail_plz_contact_admin, Constants.ErrorNo + ""), getActivity());
                } else if (Constants.ErrorNo == Constants.Comm_Error_Code) {
                    Constants.showAlert(getString(R.string.data_conn_lost_during_sync_error_code, Constants.ErrorNo + ""), getActivity());
                } else {
                    Constants.showAlert(getString(R.string.msg_error_occurred_open_online_sore), getActivity());
                }
            }
        }
    }

    private void onClosingProgressDialog() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
//            e.printStackTrace();
            LogManager.writeLogError(this.getClass().getSimpleName() + ".onClosingProgressDialog: " + e.getMessage());
        }
    }

}
