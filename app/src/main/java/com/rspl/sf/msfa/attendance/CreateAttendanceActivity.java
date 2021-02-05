package com.rspl.sf.msfa.attendance;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.rspl.sf.msfa.BuildConfig;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.asyncTask.RefreshAsyncTask;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.ui.MaterialDesignSpinner;
import com.rspl.sf.msfa.ui.toggleButton.MultiStateToggleButton;
import com.rspl.sf.msfa.ui.toggleButton.ToggleButton;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.impl.ODataDurationDefaultImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by e10526 on 26-10-2016.
 */
public class CreateAttendanceActivity extends AppCompatActivity implements UIListener, View.OnClickListener {
    private static final String TAG = CreateAttendanceActivity.class.getSimpleName();
    ArrayList<String> alAssignColl = new ArrayList<>();
    ArrayList<String> alFlushColl = new ArrayList<>();
    String concatCollectionStr = "";
    String concatFlushCollStr = "";
    Toolbar toolbar;
    boolean closeScreen = false;
    private EditText editRemraks;
    private String[][] arrWorkType = Constants.arrWorkType;
    private String[][] arrAttendanceType = null;
    ArrayList<AttendanceTypeBean> arrOtherType = new ArrayList<>();
    private String mStrSelFieldWorkTypeType = "", mStrSelFullday = "",
            mStrFullDayRemarksFlag = "", mStrFirstHalfRemarksFlag = "", mStrSecondHalfRemarksFlag = "",
            mStrSelFirstHalf = "", mStrSelSecondHalf = "";
    private String mStrPopUpText;
    private ProgressDialog pdLoadDialog;
    private TextInputLayout tiRemarks;
    private MultiStateToggleButton mSTBAttendanceType;
    private MultiStateToggleButton mSTBFullDay;
    private LinearLayout llFullDay;
    private TextView tvDate;
    private LinearLayout llFirstHalf;
    private LinearLayout llSecondHalf;
    private MultiStateToggleButton mSTBFirstHalf;
    private MultiStateToggleButton mSTBSecondHalf;
    private MaterialDesignSpinner spFullDay;
    private MaterialDesignSpinner spFirstHalf;
    private MaterialDesignSpinner spSecondHalf;
    private LinearLayout llSplitView;
    private MenuItem itemSave=null;
    private GUID refguid =null;
    String tempFirstHalf="";
    String tempSecondHalf="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_create);

        //Initialize action bar with back button(true/false
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_attendance), 0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        findVIewById();
        init();
    }

    private void init() {
        getAttendanceType();

        setFulldaySpinner();
        setFirstHalfSpinner();
        setSecondHalfSpinner();


        mSTBAttendanceType.setElements(arrWorkType[1]);
        mSTBAttendanceType.setColorRes(R.color.button_primary_color, R.color.button_secondary_color);
        mSTBAttendanceType.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int position) {
                Log.d(TAG, "Position: " + position);
                mStrSelFieldWorkTypeType = arrWorkType[0][position];
                if (mStrSelFieldWorkTypeType.equalsIgnoreCase("")) {
                    tiRemarks.setVisibility(View.GONE);
                    mSTBFullDay.setVisibility(View.GONE);
                    llFirstHalf.setVisibility(View.GONE);
                    llSecondHalf.setVisibility(View.GONE);
                    llFullDay.setVisibility(View.GONE);
                    llSplitView.setVisibility(View.GONE);
                } else if (mStrSelFieldWorkTypeType.equalsIgnoreCase("01")) {
                    mSTBFullDay.setVisibility(View.VISIBLE);
                    llFullDay.setVisibility(View.VISIBLE);
                    llFirstHalf.setVisibility(View.GONE);
                    llSecondHalf.setVisibility(View.GONE);
                    llSplitView.setVisibility(View.GONE);
                    mSTBFullDay.setValue(-1);
                    setFulldaySpinner();
                    /*for (int i = 0; i < arrAttendanceType[1].length; i++) {
                        if (mStrDefultFullDay.equalsIgnoreCase(arrAttendanceType[0][i])) {
                            mSTBFullDay.setValue(i);
                            break;
                        }
                    }*/
                } else {
                    tiRemarks.setVisibility(View.VISIBLE);
                    mSTBFullDay.setVisibility(View.GONE);
                    llFullDay.setVisibility(View.GONE);
                    llSplitView.setVisibility(View.VISIBLE);
                    llFirstHalf.setVisibility(View.VISIBLE);
                    llSecondHalf.setVisibility(View.VISIBLE);
                    mSTBFirstHalf.setValue(-1);
                    mSTBSecondHalf.setValue(-1);
                }
            }
        });


        mSTBFullDay.setElements(arrAttendanceType[1]);
        mSTBFullDay.setColorRes(R.color.button_primary_color, R.color.button_secondary_color);
        mSTBFullDay.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int position) {
                if (position > -1) {
                    mStrSelFullday = arrAttendanceType[0][position];
                    if(arrAttendanceType[2][position].contains("=")){
                        String[] remarksSlipt = arrAttendanceType[2][position].split("=");
                        mStrFullDayRemarksFlag = remarksSlipt[1];
                    }else {
                        mStrFullDayRemarksFlag = arrAttendanceType[2][position];
                    }
//                    mStrFullDayRemarksFlag = arrAttendanceType[2][position];
                    if (mStrSelFullday.equalsIgnoreCase("99")) {
                        spFullDay.setVisibility(View.VISIBLE);
                    }else {
                        spFullDay.setVisibility(View.GONE);
                    }
                } else {
                    mStrSelFullday = "";
                    mStrFullDayRemarksFlag = "";
                    if (mStrSelFullday.equalsIgnoreCase("99")) {
                        spFullDay.setVisibility(View.VISIBLE);
                    }else {
                        spFullDay.setVisibility(View.GONE);
                    }
                }
                if (!TextUtils.isEmpty(mStrFullDayRemarksFlag)) {
                    tiRemarks.setHint(getString(R.string.tvRemarks));
                    tiRemarks.setErrorEnabled(false);
                } else {
                    tiRemarks.setHint(getString(R.string.tvRemarks_not_mandatory));
                    tiRemarks.setErrorEnabled(false);
                }
            }
        });

        mSTBFirstHalf.setElements(arrAttendanceType[1], -1, 1);
        mSTBFirstHalf.setColorRes(R.color.button_primary_color, R.color.button_secondary_color);
        mSTBFirstHalf.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int position) {
                if (position > -1) {
                    if(mStrSelSecondHalf.equalsIgnoreCase("")){
                        if (!arrAttendanceType[0][position].equalsIgnoreCase("99")) {
                            mStrSelFirstHalf = arrAttendanceType[0][position];
                            tempFirstHalf = arrAttendanceType[0][position];
                            if (arrAttendanceType[2][position].contains("=")) {
                                String[] remarksSlipt = arrAttendanceType[2][position].split("=");
                                mStrFirstHalfRemarksFlag = remarksSlipt[1];
                            } else {
                                mStrFirstHalfRemarksFlag = arrAttendanceType[2][position];
                            }
                        } else {
                            mStrSelFirstHalf = "";
                            tempFirstHalf = "";
                            mStrFirstHalfRemarksFlag = "";

                        }
//                    mStrFirstHalfRemarksFlag = arrAttendanceType[2][position];
                        if (arrAttendanceType[0][position].equalsIgnoreCase("99")) {
                            spFirstHalf.setVisibility(View.VISIBLE);
                            setFirstHalfSpinner();

                        }else {
                            spFirstHalf.setVisibility(View.GONE);
                        }
                    }else{
                        if (!arrAttendanceType[0][position].equalsIgnoreCase("99") && tempSecondHalf.equalsIgnoreCase(arrAttendanceType[0][position])) {
                            mSTBFirstHalf.setValue(-1);
                            ConstantsUtils.displayShortToast(getApplicationContext(), arrAttendanceType[1][position] + " already selected in 2nd half");
                        } else {
                            if (!arrAttendanceType[0][position].equalsIgnoreCase("99")) {
                                mStrSelFirstHalf = arrAttendanceType[0][position];
                                tempFirstHalf = arrAttendanceType[0][position];
                                if (arrAttendanceType[2][position].contains("=")) {
                                    String[] remarksSlipt = arrAttendanceType[2][position].split("=");
                                    mStrFirstHalfRemarksFlag = remarksSlipt[1];
                                } else {
                                    mStrFirstHalfRemarksFlag = arrAttendanceType[2][position];
                                }
                            } else {
                                mStrSelFirstHalf = "";
                                tempFirstHalf = "";
                                mStrFirstHalfRemarksFlag = "";
                            }
//                            mStrFirstHalfRemarksFlag = arrAttendanceType[2][position];
                            if (arrAttendanceType[0][position].equalsIgnoreCase("99")) {
                                spFirstHalf.setVisibility(View.VISIBLE);
                                setFirstHalfSpinner();
                            } else {
                                spFirstHalf.setVisibility(View.GONE);
                            }
                        }
                    }
                } else {
                    mStrSelFirstHalf = "";
                    tempFirstHalf = "";
                    mStrFirstHalfRemarksFlag = "";
                    if (position > -1 && arrAttendanceType[0][position].equalsIgnoreCase("99")) {
                        spFirstHalf.setVisibility(View.VISIBLE);
                        setFirstHalfSpinner();
                    } else {
                        spFirstHalf.setVisibility(View.GONE);
                    }
                }
                if (!TextUtils.isEmpty(mStrFirstHalfRemarksFlag) || !TextUtils.isEmpty(mStrSecondHalfRemarksFlag)) {
                    tiRemarks.setHint(getString(R.string.tvRemarks));
                    tiRemarks.setErrorEnabled(false);
                } else {
                    tiRemarks.setHint(getString(R.string.tvRemarks_not_mandatory));
                    tiRemarks.setErrorEnabled(false);
                }
            }
        });


        mSTBSecondHalf.setElements(arrAttendanceType[1], -1, 1);
        mSTBSecondHalf.setColorRes(R.color.button_primary_color, R.color.button_secondary_color);
        mSTBSecondHalf.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int position) {
                if (position > -1) {
                    if (mStrSelFirstHalf.equalsIgnoreCase("")) {
                        if (!arrAttendanceType[0][position].equalsIgnoreCase("99")) {
                            mStrSelSecondHalf = arrAttendanceType[0][position];
                            tempSecondHalf = arrAttendanceType[0][position];
                            if (arrAttendanceType[2][position].contains("=")) {
                                String[] remarksSlipt = arrAttendanceType[2][position].split("=");
                                mStrSecondHalfRemarksFlag = remarksSlipt[1];
                            } else {
                                mStrSecondHalfRemarksFlag = arrAttendanceType[2][position];
                            }
                        } else {
                            mStrSelSecondHalf = "";
                            tempSecondHalf = "";
                            mStrSecondHalfRemarksFlag = "";
                        }
//                        mStrSecondHalfRemarksFlag = arrAttendanceType[2][position];
                        if (arrAttendanceType[0][position].equalsIgnoreCase("99")) {
                            spSecondHalf.setVisibility(View.VISIBLE);
                            setSecondHalfSpinner();
                        } else {
                            spSecondHalf.setVisibility(View.GONE);
                        }
                    } else {
                        if (!arrAttendanceType[0][position].equalsIgnoreCase("99") && tempFirstHalf.equalsIgnoreCase(arrAttendanceType[0][position])) {
                            ConstantsUtils.displayShortToast(getApplicationContext(), arrAttendanceType[1][position] + " already selected in 1st half");
                            mSTBSecondHalf.setValue(-1);
                        } else {
                            if (!arrAttendanceType[0][position].equalsIgnoreCase("99")) {
                                mStrSelSecondHalf = arrAttendanceType[0][position];
                                tempSecondHalf = arrAttendanceType[0][position];
                                if (arrAttendanceType[2][position].contains("=")) {
                                    String[] remarksSlipt = arrAttendanceType[2][position].split("=");
                                    mStrSecondHalfRemarksFlag = remarksSlipt[1];
                                } else {
                                    mStrSecondHalfRemarksFlag = arrAttendanceType[2][position];
                                }
                            } else {
                                mStrSelSecondHalf = "";
                                tempSecondHalf = "";
                                mStrSecondHalfRemarksFlag = "";
                            }
//                            mStrSecondHalfRemarksFlag = arrAttendanceType[2][position];
                            if (arrAttendanceType[0][position].equalsIgnoreCase("99")) {
                                spSecondHalf.setVisibility(View.VISIBLE);
                                setSecondHalfSpinner();

                            } else {
                                spSecondHalf.setVisibility(View.GONE);
                            }
                        }
                    }
                } else {
                    mStrSelSecondHalf = "";
                    tempSecondHalf = "";
                    mStrSecondHalfRemarksFlag = "";
                    if (position > -1 && arrAttendanceType[0][position].equalsIgnoreCase("99")) {
                        spSecondHalf.setVisibility(View.VISIBLE);
                        setSecondHalfSpinner();

                    } else {
                        spSecondHalf.setVisibility(View.GONE);
                    }
                }
                if (!TextUtils.isEmpty(mStrFirstHalfRemarksFlag) || !TextUtils.isEmpty(mStrSecondHalfRemarksFlag)) {
                    tiRemarks.setHint(getString(R.string.tvRemarks));
                    tiRemarks.setErrorEnabled(false);
                } else {
                    tiRemarks.setHint(getString(R.string.tvRemarks_not_mandatory));
                    tiRemarks.setErrorEnabled(false);
                }
            }
        });


        mSTBAttendanceType.setValue(0);
       /*
        //first half
        String mStrNoneSplit = Constants.str_00;
        for (int i = 0; i < arrAttendanceType[1].length; i++) {
            if (mStrNoneSplit.equalsIgnoreCase(arrAttendanceType[0][i])) {
                mSTBFirstHalf.setValue(i);
                break;
            }
        }
        //second half
        for (int i = 0; i < arrAttendanceType[1].length; i++) {
            if (mStrNoneSplit.equalsIgnoreCase(arrAttendanceType[0][i])) {
                mSTBSecondHalf.setValue(i);
                break;
            }
        }*/
    }

    private void setSecondHalfSpinner() {

        ArrayAdapter<AttendanceTypeBean> adapterSecondHalf = new ArrayAdapter<AttendanceTypeBean>(this, R.layout.custom_textview, R.id.tvItemValue, arrOtherType) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spSecondHalf, position, getContext());
                return v;
            }
        };
        adapterSecondHalf.setDropDownViewResource(R.layout.spinnerinside);
        spSecondHalf.setAdapter(adapterSecondHalf);

        spSecondHalf.showFloatingLabel();
        spSecondHalf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mStrSelSecondHalf=arrOtherType.get(position).getType();
                    if (!TextUtils.isEmpty(mStrSelSecondHalf)&& mStrSelFirstHalf.equalsIgnoreCase( mStrSelSecondHalf)) {
                        ConstantsUtils.displayShortToast(getApplicationContext(), arrOtherType.get(position).getTypeName() + " already selected in 1st half");
                        mStrSelSecondHalf = "";
                        setSecondHalfSpinner();
                    }
                    /*if(arrOtherType.get(position).getTypeValue().contains("=")){
                        String[] remarksSlipt = arrOtherType.get(position).getTypeValue().split("=");
                        mStrSecondHalfRemarksFlag = remarksSlipt[1];
                    }else {
                        mStrSecondHalfRemarksFlag = arrOtherType.get(position).getTypeValue();
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setFulldaySpinner(){
        ArrayAdapter<AttendanceTypeBean> adapterFullDay = new ArrayAdapter<AttendanceTypeBean>(this, R.layout.custom_textview, R.id.tvItemValue, arrOtherType) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spFullDay, position, getContext());
                return v;
            }
        };
        adapterFullDay.setDropDownViewResource(R.layout.spinnerinside);
        spFullDay.setAdapter(adapterFullDay);

        spFullDay.showFloatingLabel();
        spFullDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mStrSelFullday=arrOtherType.get(position).getType();
                    /*if(arrOtherType.get(position).getTypeValue().contains("=")){
                        String[] remarksSlipt = arrOtherType.get(position).getTypeValue().split("=");
                        mStrFullDayRemarksFlag = remarksSlipt[1];
                    }else {
                        mStrFullDayRemarksFlag = arrOtherType.get(position).getTypeValue();
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private void setFirstHalfSpinner() {
        ArrayAdapter<AttendanceTypeBean> adapterFirstHalf = new ArrayAdapter<AttendanceTypeBean>(this, R.layout.custom_textview, R.id.tvItemValue, arrOtherType) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spFirstHalf, position, getContext());
                return v;
            }
        };
        adapterFirstHalf.setDropDownViewResource(R.layout.spinnerinside);
        spFirstHalf.setAdapter(adapterFirstHalf);

        spFirstHalf.showFloatingLabel();
        spFirstHalf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mStrSelFirstHalf = arrOtherType.get(position).getType();
                    if (!TextUtils.isEmpty(mStrSelFirstHalf)&&mStrSelSecondHalf.equalsIgnoreCase(mStrSelFirstHalf)) {
                        ConstantsUtils.displayShortToast(getApplicationContext(), arrOtherType.get(position).getTypeName() + " already selected in 2nd half");
                        mStrSelFirstHalf = "";
                        setFirstHalfSpinner();
                    }
                    /*if(arrOtherType.get(position).getTypeValue().contains("=")){
                        String[] remarksSlipt = arrOtherType.get(position).getTypeValue().split("=");
                        mStrFirstHalfRemarksFlag = remarksSlipt[1];
                    }else {
                        mStrFirstHalfRemarksFlag = arrOtherType.get(position).getTypeValue();
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void findVIewById() {
        llFullDay = (LinearLayout) findViewById(R.id.llFullDay);
        llFirstHalf = (LinearLayout) findViewById(R.id.llFirstHalf);
        llSecondHalf = (LinearLayout) findViewById(R.id.llSecondHalf);
        llSplitView = (LinearLayout) findViewById(R.id.llSplitView);

        mSTBAttendanceType = (MultiStateToggleButton) findViewById(R.id.mSTBAttendanceType);
        mSTBFullDay = (MultiStateToggleButton) findViewById(R.id.mSTBFullDay);
        mSTBFirstHalf = (MultiStateToggleButton) findViewById(R.id.mSTBFirstHalf);
        mSTBSecondHalf = (MultiStateToggleButton) findViewById(R.id.mSTBSecondHalf);
        spFullDay = (MaterialDesignSpinner) findViewById(R.id.spFullDay);
        spFirstHalf = (MaterialDesignSpinner) findViewById(R.id.spFirstHalf);
        spSecondHalf = (MaterialDesignSpinner) findViewById(R.id.spSecondHalf);
        tiRemarks = (TextInputLayout) findViewById(R.id.tiRemarks);
        editRemraks = (EditText) findViewById(R.id.editRemraks);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvDate.setText(ConstantsUtils.getCurrentDateFormat());
        editRemraks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiRemarks.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //Gets attendances type from offline manager
    private void getAttendanceType() {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.EntityType
                    + " eq 'Attendance'&$orderby=" + Constants.ID + " asc";
            arrAttendanceType = OfflineManager.getConfigListAttendance(mStrConfigQry);
            String[] otherTypeValue = null;
            if(arrAttendanceType!=null) {
                for (int i = 0; i < arrAttendanceType[1].length; i++) {
                    if (arrAttendanceType[1][i].equalsIgnoreCase("others")) {
                        try {
//                        String[] otherSlipt = arrAttendanceType[1][i].split(",");;
                            String[] otherAttype = arrAttendanceType[2][i].split("=");
//                        otherTypeValue = otherAttype[1].split(",");
                            otherTypeValue = otherAttype[1].split(",");
                            arrAttendanceType[2][i] = "";
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
                ArrayList<String> strOtherValue = new ArrayList<>();
                ArrayList<AttendanceTypeBean> arrAttType = new ArrayList<>();
                if (otherTypeValue != null) {
                    for (int i = 0; i < otherTypeValue.length; i++) {
                        strOtherValue.add(otherTypeValue[i]);
                    }
                    for (int i = 0; i < arrAttendanceType[0].length; i++) {
                        AttendanceTypeBean attendanceTypeBean = new AttendanceTypeBean();
                        if (!strOtherValue.contains(arrAttendanceType[0][i])) {
                            attendanceTypeBean.setType(arrAttendanceType[0][i]);
                            attendanceTypeBean.setTypeName(arrAttendanceType[1][i]);
                            attendanceTypeBean.setTypeValue(arrAttendanceType[2][i]);
                            arrAttType.add(attendanceTypeBean);
                        } else {
                            attendanceTypeBean.setType(arrAttendanceType[0][i]);
                            attendanceTypeBean.setTypeName(arrAttendanceType[1][i]);
                            attendanceTypeBean.setTypeValue(arrAttendanceType[2][i]);
                            arrOtherType.add(attendanceTypeBean);
                        }
                    }
                    AttendanceTypeBean attendanceTypeNone = new AttendanceTypeBean();
                    attendanceTypeNone.setType("");
                    attendanceTypeNone.setTypeName("None");
                    attendanceTypeNone.setTypeValue("");
                    arrOtherType.add(attendanceTypeNone);
                    Collections.sort(arrOtherType, new Comparator<AttendanceTypeBean>() {
                        @Override
                        public int compare(AttendanceTypeBean o1, AttendanceTypeBean o2) {
                            return o1.getType().compareTo(o2.getType());
                        }
                    });
                /*try {
                    AttendanceTypeBean attendanceTypeBean = new AttendanceTypeBean();
                    attendanceTypeBean.setTypeName("None");
                    arrOtherType.set(0,attendanceTypeBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                    String[][] tempAttendanceType = new String[3][arrAttType.size()];
                    for (int i = 0; i < arrAttType.size(); i++) {
                        AttendanceTypeBean configTypeValues = arrAttType.get(i);
                        tempAttendanceType[0][i] = configTypeValues.getType();
                        tempAttendanceType[1][i] = configTypeValues.getTypeName();
                        tempAttendanceType[2][i] = configTypeValues.getTypeValue();
                    }
                    arrAttendanceType = null;
                    arrAttendanceType = tempAttendanceType;
                }
            }
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        if (arrAttendanceType == null) {
            /*arrAttendanceType = new String[3][1];
            arrAttendanceType[0][0] = "";
            arrAttendanceType[1][0] = "";
            arrAttendanceType[2][0] = "";*/
            dummyValuesForAttendanceType();
        }
    }

    public void dummyValuesForAttendanceType() {
        arrAttendanceType = new String[3][1];
        arrAttendanceType[0][0] = "";
        arrAttendanceType[1][0] = "Field work";
        arrAttendanceType[2][0] = "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_attendance, menu);
        itemSave = menu.findItem(R.id.menu_attendance_save);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_attendance_save:
                itemSave.setEnabled(false);
                UtilConstants.dialogBoxWithCallBack(CreateAttendanceActivity.this, "", getString(R.string.so_save_Attendence_msg), getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
                    @Override
                    public void clickedStatus(boolean b) {
                        if (b) {
                            onSave();
                        }else {
                            itemSave.setEnabled(true);
                        }
                    }
                });
//                onSave();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }

    /*Starts Attendance*/
    private void onLoadDialog() {
      /*  if (Constants.onGpsCheck(getApplicationContext())) {*/
        mStrPopUpText = getString(R.string.msg_marking_attendance);
        try {
            new LoadingData().execute();
        } catch (Exception e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog("Create Atnd" + e.getMessage());
        }
        //  }
    }

    /*Validating Data*/
    private boolean validateData() {
        // Full Day
        if (mStrSelFieldWorkTypeType.equalsIgnoreCase("01")) {
            // Full day attendance type = None
            if (mStrSelFullday.equalsIgnoreCase("00") || TextUtils.isEmpty(mStrSelFullday)) {
                showErrorMsg("Select Full Day");
                return true;
            } else if (!mStrSelFullday.equalsIgnoreCase("01")) {

                if (mStrFullDayRemarksFlag.equalsIgnoreCase(Constants.X)) {
                    if (editRemraks.getText() == null || editRemraks.getText().toString().trim().equalsIgnoreCase("")) {
                        // error
                        errorEditText(tiRemarks, getString(R.string.error_att_remarks));
                        return true;
                        //  errorDropDown(spAttendanceType,"Remarks can't be empty");
                    }
                }
            }
        } else {
            if (mStrSelFirstHalf.equalsIgnoreCase("00") || TextUtils.isEmpty(mStrSelFirstHalf)) {
                // error
                showErrorMsg(getString(R.string.error_first_half));
                return true;
            }
            if (mStrSelSecondHalf.equalsIgnoreCase("00") || TextUtils.isEmpty(mStrSelSecondHalf)) {
                // error
                showErrorMsg(getString(R.string.error_second_half));
                return true;
            }
            if (mStrFirstHalfRemarksFlag.equalsIgnoreCase(Constants.X) || mStrSecondHalfRemarksFlag.equalsIgnoreCase(Constants.X)) {
                if (editRemraks.getText() == null || TextUtils.isEmpty(editRemraks.getText().toString())) {
                    // error
                    errorEditText(tiRemarks, getString(R.string.error_att_remarks));
                    return true;
                } else if (editRemraks.getText().toString().trim().length() < 1) {
                    errorEditText(tiRemarks, getString(R.string.error_att_remarks_valid));
                    return true;
                }
            }
        }
        return false;
    }

    private void showErrorMsg(String s) {
        ConstantsUtils.displayLongToast(CreateAttendanceActivity.this, s);
    }

    /*Saves start attendance data into store*/
    private void onSave() {
        if (!validateData()) {
            LocationUtils.checkLocationPermission(CreateAttendanceActivity.this, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                    Log.d("location fun", "1");
                    onCloseProgressDialog();
                    if (status) {
                        locationPerGranted();
                    }else {
                        itemSave.setEnabled(true);
                    }
                }
            });

    /*
            if (Constants.onGpsCheck(CreateAttendanceActivity.this)) {
                if (UtilConstants.getLocation(CreateAttendanceActivity.this))
                    onLoadDialog();
            }*/

        } else {
//            Constants.customAlertMessage(this, getString(R.string.validation_plz_enter_mandatory_flds));
            itemSave.setEnabled(true);
        }
    }

    private void locationPerGranted() {
        pdLoadDialog = Constants.showProgressDialog(CreateAttendanceActivity.this, "", getString(R.string.gps_progress));
        Constants.getLocation(CreateAttendanceActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                onCloseProgressDialog();
                if (status) {
                    if (ConstantsUtils.isAutomaticTimeZone(CreateAttendanceActivity.this)) {
                        onLoadDialog();
                    } else {
                        itemSave.setEnabled(true);
                        ConstantsUtils.showAutoDateSetDialog(CreateAttendanceActivity.this);
                    }
                }else {
                    itemSave.setEnabled(true);
                }
            }
        });
    }

    public void errorDropDown(MaterialDesignSpinner spType, String errorMsg) {
//        spSoldTo.setBackgroundResource(R.drawable.error_spinner);
        spType.setError(errorMsg);
    }

    public void errorEditText(TextInputLayout spType, String errorMsg) {
//        spSoldTo.setBackgroundResource(R.drawable.error_spinner);
        spType.setErrorEnabled(true);
        spType.setError(errorMsg);
    }

    /*displays alert with message*/
    public void displayPopUpMsg() {
        UtilConstants.dialogBoxWithCallBack(CreateAttendanceActivity.this, "", mStrPopUpText, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                closeScreen = true;
                if (!AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, CreateAttendanceActivity.this, BuildConfig.APPLICATION_ID, true)) {
                    onBackPressed();
                }
            }
        });
        /*AlertDialog.Builder alertDialogAlerts = new AlertDialog.Builder(CreateAttendanceActivity.this, R.style.MyTheme);
        alertDialogAlerts.setMessage(mStrPopUpText)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();
//                                    onAlerts();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });
        alertDialogAlerts.show();*/
    }


    @Override
    public void onRequestError(int operation, Exception e) {
        itemSave.setEnabled(true);
        e.printStackTrace();
        ErrorBean errorBean = Constants.getErrorCode(operation, e, CreateAttendanceActivity.this);
        if (errorBean.hasNoError()) {
            try {
                mStrPopUpText = getString(R.string.err_msg_concat, getString(R.string.lbl_attence_start), e.getMessage());
            } catch (Exception er) {
                er.printStackTrace();
                mStrPopUpText = getString(R.string.msg_start_upd_sync_error);
            }
            if (operation == Operation.Create.getValue()) {
                Constants.isSync = false;
                onCloseProgressDialog();
                displayPopUpMsg();
            } else if (operation == Operation.OfflineFlush.getValue()) {
                Constants.isSync = false;
                onCloseProgressDialog();
                displayPopUpMsg();
            } else {
                Constants.isSync = false;
                onCloseProgressDialog();
                displayPopUpMsg();
            }
        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                Constants.isSync = true;
                new RefreshAsyncTask(CreateAttendanceActivity.this, concatCollectionStr, this).execute();
            } else {
                Constants.isSync = false;
                onCloseProgressDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), CreateAttendanceActivity.this);
            }
        } else {
            Constants.isSync = false;
            onCloseProgressDialog();
            mStrPopUpText = Constants.makeMsgReqError(errorBean.getErrorCode(), CreateAttendanceActivity.this, false);
            displayPopUpMsg();
//            Constants.displayMsgReqError(errorBean.getErrorCode(),CreateAttendanceActivity.this);
        }
       /* ErrorBean errorBean = Constants.getErrorCode(operation, e, CreateAttendanceActivity.this);
        if (errorBean.hasNoError()) {
            mStrPopUpText = getString(R.string.msg_start_upd_sync_error);
            onCloseProgressDialog();
            if (operation == Operation.Create.getValue()) {
                onCloseProgressDialog();
                displayPopUpMsg();
            }else if (operation == Operation.GetStoreOpen.getValue()){
                onCloseProgressDialog();
                Constants.isSync = false;
                mStrPopUpText = errorBean.getErrorMsg();
                displayPopUpMsg();
            }
        } else {
            Constants.isSync = false;
            onCloseProgressDialog();
            Constants.displayMsgReqError(errorBean.getErrorCode(), CreateAttendanceActivity.this);
        }*/

    }

    private void onCloseProgressDialog() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private ReentrantLock reentrantLock = null;
    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {
        itemSave.setEnabled(true);
        if (operation == Operation.Create.getValue()) {
            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                alFlushColl = Constants.getPendingList();
                alAssignColl = Constants.getRefreshList();
                concatFlushCollStr = Constants.getConcatinatinFlushCollectios(alFlushColl);
                try {
                    onCloseProgressDialog();
                    mStrPopUpText = getString(R.string.dialog_day_started);
                    displayPopUpMsg();
                    try {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (reentrantLock==null){
                                    reentrantLock = new ReentrantLock();
                                }
                                try {
                                    Log.e("REENTRANT Attendance :","LOCKED");
                                    reentrantLock.lock();
                                    OfflineManager.flushQueuedRequests(CreateAttendanceActivity.this, concatFlushCollStr);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e("Attendance EXCEPTION : ","ANR EXCEPTION OCCURRED");
                                }finally {
                                    if (reentrantLock!=null&&reentrantLock.isHeldByCurrentThread()){
                                        reentrantLock.unlock();
                                    }
                                    Log.e("REENTRANT Attendance :","UNLOCKED FINALLY");
                                }
                            }
                        }).start();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Constants.isSync = false;
                onCloseProgressDialog();
                mStrPopUpText = getString(R.string.no_network_conn);
                displayPopUpMsg();
            }

        } else if (operation == Operation.OfflineFlush.getValue()) {
            Constants.isSync = false;
            if (!Constants.isSpecificCollTodaySyncOrNot(Constants.getLastSyncDate(Constants.SYNC_TABLE, Constants.Collections,
                    Constants.CPStockItems, Constants.TimeStamp, CreateAttendanceActivity.this))) {
//                addCPStockItemToArrayList();
            }
            try {
                concatCollectionStr = Constants.getConcatinatinFlushCollectios(alAssignColl);
                Constants.updateLastSyncTimeToTable(alAssignColl,this,Constants.Attnd_sync,refguid.toString().toUpperCase());
                refguid = GUID.newRandom();
                Constants.updateStartSyncTime(CreateAttendanceActivity.this,Constants.Attnd_refresh_sync,Constants.StartSync,refguid.toString().toUpperCase());
                new RefreshAsyncTask(CreateAttendanceActivity.this, concatCollectionStr, this).execute();
            } catch (Exception e) {
                e.printStackTrace();
                TraceLog.e(Constants.SyncOnRequestSuccess, e);
            }
        } else if (operation == Operation.OfflineRefresh.getValue()) {
            Constants.isSync = false;
            Constants.updateLastSyncTimeToTable(alAssignColl,this,Constants.Attnd_refresh_sync,refguid.toString().toUpperCase());
            ConstantsUtils.startAutoSync(CreateAttendanceActivity.this,false);
            onCloseProgressDialog();
//            mStrPopUpText = getString(R.string.dialog_day_started);
//            displayPopUpMsg();
        } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
            Constants.isSync = false;
            try {
                OfflineManager.getAuthorizations(CreateAttendanceActivity.this);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setSyncTime(CreateAttendanceActivity.this,refguid.toString().toUpperCase());
            ConstantsUtils.startAutoSync(CreateAttendanceActivity.this,false);
            mStrPopUpText = getString(R.string.dialog_day_started);
            displayPopUpMsg();
        }

    }

    private void addCPStockItemToArrayList() {
        if (!alAssignColl.contains(Constants.CPStockItems)) {
            alAssignColl.add(Constants.CPStockItems);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                onBackPressed();
                break;
            case R.id.tv_submit:
                onSave();
                break;
        }
    }

    @Override
    public void onBackPressed() {

        if (closeScreen) {
            super.onBackPressed();
        } else {
            mStrPopUpText = getString(R.string.msg_exit_attendance);
            /*
             * disply alert dialog for exit current activity
             */
            AlertDialog.Builder alertDailogExitAttendance = new AlertDialog.Builder(CreateAttendanceActivity.this, R.style.MyTheme);
            alertDailogExitAttendance.setMessage(mStrPopUpText)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes,
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface Dialog,
                                        int id) {
                                    try {
                                        Dialog.cancel();
                                        closeScreen = true;
                                        onBackPressed();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                }
                            }).setNegativeButton(R.string.no,
                    new DialogInterface.OnClickListener() {
                        public void onClick(
                                DialogInterface Dialog,
                                int id) {
                            try {
                                closeScreen = false;
                                Dialog.cancel();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    });
            alertDailogExitAttendance.show();
        }
    }

    /*Save Day start data on offline store*/
    private void onSaveDayStartData() {
        try {

            Constants.isSync = true;
            Constants.MapEntityVal.clear();
            GUID guid = GUID.newRandom();
            Hashtable hashTableAttendanceValues = new Hashtable();
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
            String loginIdVal = sharedPreferences.getString(Constants.username, "");

            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.LOGINID, loginIdVal);
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.AttendanceGUID, guid.toString());
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.StartDate, UtilConstants.getNewDateTimeFormat());

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
            hashTableAttendanceValues.put(Constants.StartTime, oDataDuration);
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.StartLat, BigDecimal.valueOf(UtilConstants.round(UtilConstants.latitude, 12)));
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.StartLong, BigDecimal.valueOf(UtilConstants.round(UtilConstants.longitude, 12)));
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.EndLat, "");
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.EndLong, "");
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.EndDate, "");
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.EndTime, "");

            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.Remarks, editRemraks.getText().toString() != null ? editRemraks.getText().toString() : "");

            if (mStrSelFieldWorkTypeType.equalsIgnoreCase("01")) {
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.AttendanceTypeH1, mStrSelFullday);
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.AttendanceTypeH2, mStrSelFullday);
            } else {
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.AttendanceTypeH1, mStrSelFirstHalf);
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.AttendanceTypeH2, mStrSelSecondHalf);
            }


            hashTableAttendanceValues.put(Constants.SPGUID, Constants.getSPGUID(Constants.SPGUID));

            hashTableAttendanceValues.put(Constants.SetResourcePath, "guid'" + guid.toString() + "'");

            SharedPreferences sharedPreferencesVal = getSharedPreferences(Constants.PREFS_NAME, 0);
            SharedPreferences.Editor editor = sharedPreferencesVal.edit();
            editor.putInt(Constants.VisitSeqId, 0);
            editor.commit();
            try {
                //noinspection unchecked
                OfflineManager.createAttendance(hashTableAttendanceValues, CreateAttendanceActivity.this);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
                ConstantsUtils.printErrorLog("Create Atnd" + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog("Create Atnd" + e.getMessage());
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
                    LocationUtils.checkLocationPermission(CreateAttendanceActivity.this, new LocationInterface() {
                        @Override
                        public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                            if (status) {
                                locationPerGranted();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LocationUtils.REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationPerGranted();
            }
        }
    }

    /*AsyncTask to store attendance data into offline store*/
    private class LoadingData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(CreateAttendanceActivity.this, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(mStrPopUpText);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                refguid = GUID.newRandom();
                Constants.updateStartSyncTime(CreateAttendanceActivity.this,Constants.Attnd_sync,Constants.StartSync,refguid.toString().toUpperCase());
                onSaveDayStartData();
            } catch (InterruptedException e) {
                e.printStackTrace();
                ConstantsUtils.printErrorLog("Create Atnd" + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            itemSave.setEnabled(true);
            super.onPostExecute(result);
        }
    }

}
