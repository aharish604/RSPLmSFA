package com.rspl.sf.msfa.socreate.stepOne;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.DialogCallBack;
import com.rspl.sf.msfa.so.PaymentTermBean;
import com.rspl.sf.msfa.so.SOUtils;
import com.rspl.sf.msfa.so.UnlRecBean;
import com.rspl.sf.msfa.so.ValueHelpBean;
import com.rspl.sf.msfa.socreate.DefaultValueBean;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.socreate.stepThree.SOQuantityActivity;
import com.rspl.sf.msfa.socreate.stepTwo.SOCreateSingleMaterialActivity;
import com.rspl.sf.msfa.socreate.stepTwo.SOCreateStpTwoActivity;
import com.rspl.sf.msfa.solist.SOListBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.ui.MaterialDesignSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 */
public class SOCreateFragment extends Fragment implements SOCreateView, View.OnClickListener, DatePickerDialog.OnDateSetListener {


    //    public static boolean isRefresh = false;
    private int comingFrom = 0;
    private String mstrCustomerNo = "", mStrCPGUID32 = "", mStrComingFrom = "", mStrUID = "", mStrCustomerName = "";
    private SOCreatePresenterImp presenter;
    private Spinner spShipTo, spPaymentTerm, spIncoTerm1, spSpingCondition, spUnloading, spReceiving;
    private SOListBean soListBean = new SOListBean();
    private EditText etRemarks, etIncoTerm2;
    private TextView tvPoDate;
    private ImageView ivPODate;
    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;
    private DatePickerDialog dialog;
    private TextView lblShippingCondition, lblShipToParty, lblPaymentTerm, lblUnloading, lblReceiving;
    private boolean isSessionRequired = false;
    private ProgressDialog progressDialog = null;
    private SOListBean soDefaultBean = null;
    //    private LinearLayout llPlant;
    private LinearLayout llHeaderView;
    //    private LinearLayout llSoldTo;
//    private LinearLayout llSalesArea;
    private LinearLayout llShipToParty;
    private LinearLayout llPaymentTerm;
    private LinearLayout llIncoTerm1;
    private LinearLayout llIncoTerm2;
    private LinearLayout llShippingCondition;
    private MaterialDesignSpinner spSalesArea;
    private MaterialDesignSpinner spPlant, spSoldTo, spOrderType;
    private TextView tvOrderType;
    private EditText etPONumber;
    private EditText etPODate;
    private TextInputLayout tiPoDate;
    private TextView tvNewOrder;
    String jSONStr="";
    HashMap<String, String> blockCustomer = new HashMap<>();
//    private View oneTimeShip;
//    private TextInputLayout tiFirstName, tiLasttName, tiAddrName, tiStreet2Name, tiStreet3Name, tiStreet4Name, tiDistrict, tiCity, tiCountry, tiRegion, tiPostalCode;
//    private EditText etFirstName, etLastName, etAddrName, etStreet2Name, etStreet3Name, etStreet4Name, etDistrict, etCity, etCountry, etRegion, etPostalCode;
//    private MaterialDesignSpinner spSalesOffice;
//    private MaterialDesignSpinner spSalesGroup;

    public SOCreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = getArguments();
        if (bundle != null) {
            isSessionRequired = bundle.getBoolean(Constants.EXTRA_SESSION_REQUIRED, false);
            comingFrom = bundle.getInt(Constants.EXTRA_COME_FROM, 0);
            soDefaultBean = (SOListBean) bundle.getSerializable(Constants.EXTRA_SO_HEADER);

            mstrCustomerNo = bundle.getString(Constants.CPNo);
            mStrCustomerName = bundle.getString(Constants.RetailerName);
            mStrUID = bundle.getString(Constants.CPUID);
            mStrComingFrom = bundle.getString(Constants.comingFrom);
            mStrCPGUID32 = bundle.getString(Constants.CPGUID32);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_socreate, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        spSoldTo = (MaterialDesignSpinner) view.findViewById(R.id.spSoldTo);
//        tvSoldTo = (TextView) view.findViewById(R.id.tvSoldTo);
        spSalesArea = (MaterialDesignSpinner) view.findViewById(R.id.spSalesArea);
        spPlant = (MaterialDesignSpinner) view.findViewById(R.id.spPlant);
        spOrderType = (MaterialDesignSpinner) view.findViewById(R.id.spOrderType);
        tvNewOrder = (TextView) view.findViewById(R.id.tvNewOrder);
//        spSalesOffice = (MaterialDesignSpinner) view.findViewById(R.id.spSalesOffice);
//        spSalesGroup = (MaterialDesignSpinner) view.findViewById(R.id.spSalesGroup);

        spShipTo = (Spinner) view.findViewById(R.id.spShipTo);
        tvPoDate = (TextView) view.findViewById(R.id.tvPoDate);
        ivPODate = (ImageView) view.findViewById(R.id.ivPODate);
        spPaymentTerm = (Spinner) view.findViewById(R.id.spPaymentTerm);
        spIncoTerm1 = (Spinner) view.findViewById(R.id.spIncoTerm1);
        etIncoTerm2 = (EditText) view.findViewById(R.id.etIncoTerm2);
        spSpingCondition = (Spinner) view.findViewById(R.id.spSpingCondition);
        spUnloading = (Spinner) view.findViewById(R.id.spUnloading);
        spReceiving = (Spinner) view.findViewById(R.id.spReceiving);
        etRemarks = (EditText) view.findViewById(R.id.etRemarks);
        tvOrderType = (TextView) view.findViewById(R.id.tvOrderType);
        etPODate = (EditText) view.findViewById(R.id.etPODate);
        etPONumber = (EditText) view.findViewById(R.id.etPONumber);
        tiPoDate = (TextInputLayout) view.findViewById(R.id.tiPoDate);
        lblShippingCondition = (TextView) view.findViewById(R.id.lblShippingCondition);
        lblShipToParty = (TextView) view.findViewById(R.id.lblShipToParty);
        lblPaymentTerm = (TextView) view.findViewById(R.id.lblPaymentTerm);
        lblUnloading = (TextView) view.findViewById(R.id.lblUnloading);
        lblReceiving = (TextView) view.findViewById(R.id.lblReceiving);
//        oneTimeShip = view.findViewById(R.id.oneTimeShip);
        ConstantsUtils.setStarMandatory(lblShippingCondition);
        ConstantsUtils.setStarMandatory(lblShipToParty);
        ConstantsUtils.setStarMandatory(lblPaymentTerm);
        ConstantsUtils.setStarMandatory(lblUnloading);
        ConstantsUtils.setStarMandatory(lblReceiving);

        llHeaderView = (LinearLayout) view.findViewById(R.id.llHeaderView);
//        llSoldTo = (LinearLayout) view.findViewById(R.id.llSoldTo);
//        llSalesArea = (LinearLayout) view.findViewById(R.id.llSalesArea);
        llShipToParty = (LinearLayout) view.findViewById(R.id.llShipToParty);
//        llPlant = (LinearLayout) view.findViewById(R.id.llPlant);
        llPaymentTerm = (LinearLayout) view.findViewById(R.id.llPaymentTerm);
        llIncoTerm1 = (LinearLayout) view.findViewById(R.id.llIncoTerm1);
        llIncoTerm2 = (LinearLayout) view.findViewById(R.id.llIncoTerm2);
        llShippingCondition = (LinearLayout) view.findViewById(R.id.llShippingCondition);
        llHeaderView.setVisibility(View.GONE);
        spSoldTo.setVisibility(View.GONE);
//        spSalesArea.setVisibility(View.GONE);
        llShipToParty.setVisibility(View.GONE);
        spPlant.setVisibility(View.GONE);
        llPaymentTerm.setVisibility(View.GONE);
        llIncoTerm1.setVisibility(View.GONE);
        llIncoTerm2.setVisibility(View.GONE);
        llShippingCondition.setVisibility(View.GONE);

        String qry = Constants.ZZInactiveCustBlks+"?$filter="+Constants.CustomerNo+" eq '"+mstrCustomerNo+"'";
        //+" and SalesArea eq '"+soListBean.getSalesArea()+"' and Application eq 'PO_CRT'


        try {
            jSONStr= OfflineManager.checkBlockedCustomer(getActivity(),qry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError("isBlock Customer Error "+e.getMessage());
            e.printStackTrace();
        }
        try {
            JSONObject jsonObject = new JSONObject(jSONStr);
            JSONArray jsonItem = jsonObject.getJSONArray("configdata");
            blockCustomer = Constants.getBlockCustomerKeyValues(jsonItem);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        uiTextListener();
        presenter = new SOCreatePresenterImp(getActivity(), this, comingFrom, mstrCustomerNo, isSessionRequired);
        presenter.onStart();
    }

    private void uiTextListener() {
        etPONumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                soListBean.setPONo(s + "");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                soListBean.setRemarks(s + "");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etIncoTerm2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                soListBean.setIncoterm2(s + "");
                etIncoTerm2.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
//        setDateToView(mYear, mMonth, mDay);

        tvPoDate.setOnClickListener(this);
        ivPODate.setOnClickListener(this);
        etPODate.setOnClickListener(this);
        tiPoDate.setOnClickListener(this);
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        dialog = new DatePickerDialog(getContext(), this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        /*default name*/
        if (soDefaultBean != null) {
//            etIncoTerm2.setText(soDefaultBean.getIncoterm2());
            etRemarks.setText(soDefaultBean.getRemarks());
            etPONumber.setText(soDefaultBean.getPONo());
            String poDate = soDefaultBean.getPODate();
            soListBean.setSONo(soDefaultBean.getSONo());
            soListBean.setOrderDate(soDefaultBean.getOrderDate());
            soListBean.setInstanceID(soDefaultBean.getInstanceID());
            tvNewOrder.setText(soListBean.getSONo());


            if (!TextUtils.isEmpty(poDate)) {
                Calendar calendar1 = ConstantsUtils.convertCalenderToDisplayDateFormat(poDate,ConstantsUtils.getDisplayDateFormat(getActivity()));

//                String[] dateArr = poDate.split("-");
                int year = calendar1.get(Calendar.YEAR);//Integer.parseInt(dateArr[0]);
                int month = calendar1.get(Calendar.MONTH);
                int date = calendar1.get(Calendar.DAY_OF_MONTH);
                setDateToView(year, month, date);
            }
        }
        if (!TextUtils.isEmpty(mStrCPGUID32))
            soListBean.setmStrCPGUID32(mStrCPGUID32);
        if (comingFrom == ConstantsUtils.SO_EDIT_ACTIVITY) {
            spPlant.setVisibility(View.GONE);
        } else {
            spPlant.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mStrComingFrom) && mStrComingFrom.equalsIgnoreCase(Constants.AdhocList) && SOUtils.isHideVisit(getContext())){
            soListBean.setVisitActivity(false);
        }else {
            soListBean.setVisitActivity(true);
        }
    }

    /*set date to text view*/
    private void setDateToView(int mYear, int mMonth, int mDay) {
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
        String poDate = String.valueOf(new StringBuilder().append(mDay).append("-")
                .append(Constants.ORG_MONTHS[mMonth]).append("-").append("")
                .append(mYear));
        soListBean.setPODate(poDate);
        tiPoDate.setErrorEnabled(false);
        etPODate.setText(poDate);
        displayRemovePoDateIcon();
    }

    private void displayRemovePoDateIcon() {
        if (tvPoDate.getText().toString().length() > 0) {
            ivPODate.setVisibility(View.VISIBLE);
        } else {
            ivPODate.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mYear = year;
        mMonth = month;
        mDay = dayOfMonth;
        setDateToView(mYear, mMonth, mDay);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvPoDate:
                Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
                cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
                cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
                cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));

                dialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                dialog.show();
                getActivity().showDialog(Constants.DATE_DIALOG_ID);
                break;
            case R.id.ivPODate:
                soListBean.setPODate("");
                tvPoDate.setText("");
                displayRemovePoDateIcon();
                break;
            case R.id.etPODate:
                openCallender();
                break;
            case R.id.tiPoDate:
                openCallender();
                break;
        }
    }

    private void openCallender() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
        dialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
        dialog.show();
    }

    @Override
    public void showProgressDialog(String message) {
        if(progressDialog==null) {
            progressDialog = ConstantsUtils.showProgressDialog(getContext(), message);
        }else {
            if(!progressDialog.isShowing()){
                progressDialog = ConstantsUtils.showProgressDialog(getContext(), message);
            }
        }
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void displaySoldToParty(final String[][] arrCustomers) {
//        if (arrCustomers[32] != null && arrCustomers[32].length > 1) {
//            tvSoldTo.setVisibility(View.GONE);
        spSoldTo.setVisibility(View.VISIBLE);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.custom_textview, R.id.tvItemValue, arrCustomers[32]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.custom_textview, R.id.tvItemValue, arrCustomers[32]) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spSoldTo, position, getContext());
                return v;
            }
        };
        adapter.setDropDownViewResource(R.layout.spinnerinside);
        spSoldTo.setAdapter(adapter);
        spSoldTo.showFloatingLabel();
        spSoldTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    spSoldTo.setBackgroundResource(R.drawable.spinner_bg);
//                    presenter.getBasedOnCustomer(arrCustomers[0][position]);
                soListBean.setSoldTo(arrCustomers[0][position]);
                soListBean.setSoldToName(arrCustomers[1][position]);
                presenter.getBasedOnCustomer(arrCustomers[0][position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
      /*  } else {
            tvSoldTo.setVisibility(View.VISIBLE);
            spSoldTo.setVisibility(View.GONE);
            try {
                tvSoldTo.setText(arrCustomers[32][0]);
                soListBean.setSoldTo(arrCustomers[0][0]);
                soListBean.setSoldToName(arrCustomers[1][0]);
                presenter.getBasedOnCustomer(arrCustomers[0][0]);
            } catch (Exception e) {
                e.printStackTrace();
                presenter.getBasedOnCustomer("");
            }
        }*/
        llHeaderView.setVisibility(View.VISIBLE);
        spSoldTo.setVisibility(View.VISIBLE);

//        spSalesArea.setVisibility(View.GONE);
        spOrderType.setVisibility(View.GONE);
        llShipToParty.setVisibility(View.GONE);
        spPlant.setVisibility(View.GONE);
//        spSalesGroup.setVisibility(View.GONE);
//        spSalesOffice.setVisibility(View.GONE);
        llPaymentTerm.setVisibility(View.GONE);
        llIncoTerm1.setVisibility(View.GONE);
        llIncoTerm2.setVisibility(View.GONE);
        llShippingCondition.setVisibility(View.GONE);
    }

    @Override
    public void displayByCustomer(final ArrayList<DefaultValueBean> customerSalesAreaArrayList, final ArrayList<ValueHelpBean> ordtypeList) {

        ArrayAdapter<DefaultValueBean> adapter = new ArrayAdapter<DefaultValueBean>(getActivity(), R.layout.custom_textview, R.id.tvItemValue, customerSalesAreaArrayList) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spSalesArea, position, getContext());
                return v;
            }
        }; //android.R.layout.simple_spinner_item
        adapter.setDropDownViewResource(R.layout.spinnerinside);//android.R.layout.simple_spinner_dropdown_item
//        SpinnerBaseAdapter spinnerBaseAdapter = new SpinnerBaseAdapter(getContext(),customerSalesAreaArrayList);
//        adapter.createFromResource(getContext(),customerSalesAreaArrayList.g,R.layout.custom_textview);
        if (comingFrom == 36) {
            spSalesArea.setEnabled(false);
        }
        spSalesArea.setAdapter(adapter);
        if (!customerSalesAreaArrayList.isEmpty()) {
            soListBean.setSalesArea(customerSalesAreaArrayList.get(0).getSalesArea());
            soListBean.setSalesAreaDesc(customerSalesAreaArrayList.get(0).getSalesAreaDesc());
            soListBean.setPlant(customerSalesAreaArrayList.get(0).getDeliveringPlantID());
            soListBean.setPlantDesc(customerSalesAreaArrayList.get(0).getDeliveringPlantDesc());
        }
        if (soDefaultBean != null) {
            spSalesArea.setSelection(SOUtils.getSalesAreaPos(customerSalesAreaArrayList, soDefaultBean.getSalesArea()));
        }
        spSalesArea.showFloatingLabel();
        spSalesArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                spSalesArea.setBackgroundResource(R.drawable.spinner_bg);
                /*if(!customerSalesAreaArrayList.get(position).getDisplayDropDown().equalsIgnoreCase("None")) {
                    DefaultValueBean defaultValueBean = null;
                    try {
                        defaultValueBean = OfflineManager.getConfigListWithDefaultValAndNone(Constants.CustomerSalesAreas + "?$filter=" + Constants.SalesArea + " eq'" + customerSalesAreaArrayList.get(position).getSalesArea() + "' and CustomerNo eq '"+mstrCustomerNo+"'");
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                    if (defaultValueBean != null) {
                        soListBean.setSalesArea(defaultValueBean.getSalesArea());
                        soListBean.setSalesAreaDesc(defaultValueBean.getSalesAreaDesc());
                        //selected based on division
                        soListBean.setPlant(defaultValueBean.getDeliveringPlantID());
                        soListBean.setPlantDesc(defaultValueBean.getDeliveringPlantDesc());
                        soListBean.setDivison(defaultValueBean.getDivision());
                        soListBean.setDistChannelID(defaultValueBean.getDistChannelID());
                        soListBean.setSalesOrgID(defaultValueBean.getSalesOrgID());
                        soListBean.setCreditControlAreaID(defaultValueBean.getCreditControlAreaID());
                    }
                }
                    if (!TextUtils.isEmpty(soListBean.getSalesArea())) {
                        presenter.getBasedOnBsdOnSaleArea(soListBean.getSoldTo(), soListBean.getSalesArea());
                    } else {
                        spPlant.setVisibility(View.GONE);
//                    spSalesGroup.setVisibility(View.GONE);
//                    spSalesOffice.setVisibility(View.GONE);
                        soListBean.setPlant("");
                        soListBean.setPlantDesc("");

                    }*/
                soListBean.setSalesArea(customerSalesAreaArrayList.get(position).getSalesArea());
                soListBean.setSalesAreaDesc(customerSalesAreaArrayList.get(position).getSalesAreaDesc());
                //selected based on division
                soListBean.setPlant(customerSalesAreaArrayList.get(position).getDeliveringPlantID());
                soListBean.setPlantDesc(customerSalesAreaArrayList.get(position).getDeliveringPlantDesc());
                soListBean.setDivison(customerSalesAreaArrayList.get(position).getDivision());
                soListBean.setDistChannelID(customerSalesAreaArrayList.get(position).getDistChannelID());
                soListBean.setSalesOrgID(customerSalesAreaArrayList.get(position).getSalesOrgID());
                soListBean.setCreditControlAreaID(customerSalesAreaArrayList.get(position).getCreditControlAreaID());
                if (!TextUtils.isEmpty(soListBean.getSalesArea())) {
                    String isBlock="";
                    try {
                        isBlock = blockCustomer.get("PO_CRT"+"_"+soListBean.getSalesArea());
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    if(isBlock!=null && isBlock.equalsIgnoreCase("X")){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ConstantsUtils.dialogBoxWithButton(getActivity(), "", getString(R.string.block_custome_error_msg)+" "+soListBean.getSalesArea(), getString(R.string.ok), "", new DialogCallBack() {
                                    @Override
                                    public void clickedStatus(boolean clickedStatus) {
                                        getActivity().finish();
                                    }
                                });
                            }
                        });
                    }else {
                        presenter.getBasedOnBsdOnSaleArea(soListBean.getSoldTo(), soListBean.getSalesArea());
                    }

                } else {
                    spPlant.setVisibility(View.GONE);
//                    spSalesGroup.setVisibility(View.GONE);
//                    spSalesOffice.setVisibility(View.GONE);
                    soListBean.setPlant("");
                    soListBean.setPlantDesc("");

                }
//                    displayBySalesArea(null, null, null, null, null, new ArrayList<ValueHelpBean>());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ArrayAdapter<ValueHelpBean> adapterOrderType = new ArrayAdapter<ValueHelpBean>(getContext(), R.layout.custom_textview, R.id.tvItemValue, ordtypeList) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spOrderType, position, getContext());
                return v;
            }
        };
        adapterOrderType.setDropDownViewResource(R.layout.spinnerinside);
        spOrderType.setAdapter(adapterOrderType);
        if (soDefaultBean != null) {
            spOrderType.setSelection(SOUtils.getPoss(ordtypeList, soDefaultBean.getOrderType()));
        }
        if (!ordtypeList.isEmpty()) {
            soListBean.setOrderType(ordtypeList.get(0).getID());
            soListBean.setOrderTypeDesc(ordtypeList.get(0).getDescription());
            tvOrderType.setText(getString(R.string.po_details_display_value, soListBean.getOrderTypeDesc(), soListBean.getOrderType()));
        }
        spOrderType.showFloatingLabel();
        spOrderType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                soListBean.setOrderType(ordtypeList.get(position).getID());
                soListBean.setOrderTypeDesc(ordtypeList.get(position).getDescription());
                if (!TextUtils.isEmpty(ordtypeList.get(position).getID()))
                    tvOrderType.setText(getString(R.string.po_details_display_value, soListBean.getOrderTypeDesc(), soListBean.getOrderType()));
                else
                    tvOrderType.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


//        spSalesArea.setVisibility(View.GONE);

        llShipToParty.setVisibility(View.GONE);
        spOrderType.setVisibility(View.GONE);
        spPlant.setVisibility(View.GONE);
//        spSalesGroup.setVisibility(View.GONE);
//        spSalesOffice.setVisibility(View.GONE);
        llPaymentTerm.setVisibility(View.GONE);
        llIncoTerm1.setVisibility(View.GONE);
        llIncoTerm2.setVisibility(View.GONE);
        llShippingCondition.setVisibility(View.GONE);
    }

    @Override
    public void displayBySalesArea(final ArrayList<ValueHelpBean> salesOfficeList, final ArrayList<ValueHelpBean> plantList, final ArrayList<ValueHelpBean> salesGrpList) {
        ArrayAdapter<ValueHelpBean> adapterPlant = new ArrayAdapter<ValueHelpBean>(getContext(), R.layout.custom_textview, R.id.tvItemValue, plantList) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spPlant, position, getContext());
                return v;
            }
        };
        adapterPlant.setDropDownViewResource(R.layout.spinnerinside);
        spPlant.setAdapter(adapterPlant);
        if (soDefaultBean != null) {
            spPlant.setSelection(SOUtils.getPoss(plantList, soDefaultBean.getPlant()));
        }
        spPlant.showFloatingLabel();
        spPlant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                soListBean.setPlant(plantList.get(position).getID());
                soListBean.setPlantDesc(plantList.get(position).getDescription());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


//        llShipToParty.setVisibility(View.VISIBLE);
        if (comingFrom == ConstantsUtils.SO_EDIT_ACTIVITY) {
            spPlant.setVisibility(View.GONE);
        } else {
            if (plantList.isEmpty()) {
                soListBean.setPlant("");
                soListBean.setPlantDesc("");
//                spPlant.setVisibility(View.GONE);
            }
            spPlant.setVisibility(View.GONE);
        }
//        spSalesOffice.setVisibility(View.VISIBLE);
//        spSalesGroup.setVisibility(View.VISIBLE);
//        llPaymentTerm.setVisibility(View.VISIBLE);
//        llIncoTerm1.setVisibility(View.VISIBLE);
//        llIncoTerm2.setVisibility(View.VISIBLE);
//        llShippingCondition.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayMessage(String message) {
        if (getContext() != null && message != null)
            ConstantsUtils.displayLongToast(getContext(), message);
    }

    @Override
    public void displayPaymentTerm(ArrayList<PaymentTermBean> paymentTermList) {

    }

    @Override
    public void displayUnloadingPt(final ArrayList<UnlRecBean> unloadingList) {
        ArrayAdapter<UnlRecBean> adapter = new ArrayAdapter<>(getActivity(), R.layout.custom_textview, unloadingList);
        adapter.setDropDownViewResource(R.layout.spinnerinside);
        spUnloading.setAdapter(adapter);
        spUnloading.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spUnloading.setBackgroundResource(R.drawable.spinner_bg);
                soListBean.setUnloadingPointDesc(unloadingList.get(position).getText());
                soListBean.setUnloadingPointId(unloadingList.get(position).getText());
                presenter.getReceiving(soListBean.getSoldTo(), "", "", "", "", "", soListBean.getUnloadingPointId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void displayReceivingPt(final ArrayList<UnlRecBean> receivingList) {
        ArrayAdapter<UnlRecBean> adapter = new ArrayAdapter<>(getActivity(), R.layout.custom_textview, receivingList);
        adapter.setDropDownViewResource(R.layout.spinnerinside);
        spReceiving.setAdapter(adapter);
        spReceiving.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spReceiving.setBackgroundResource(R.drawable.spinner_bg);
                soListBean.setReceivingPointDesc(receivingList.get(position).getText());
                soListBean.setReceivingPointId(receivingList.get(position).getText());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void errorSoldTo() {
//        spSoldTo.setBackgroundResource(R.drawable.error_spinner);
        if (spSoldTo.getVisibility() == View.VISIBLE)
            spSoldTo.setError("Select sold to party");
    }

    @Override
    public void errorSalesArea() {
        if (spSalesArea.getVisibility() == View.VISIBLE)
            spSalesArea.setError("Select Sales Area");
    }

    @Override
    public void errorOrderType() {
        if (spOrderType.getVisibility() == View.VISIBLE)
            spOrderType.setError("Select Order Type");
    }

    @Override
    public void errorPlant() {
        if (spPlant.getVisibility() == View.VISIBLE)
            spPlant.setError("Select Plant");
//        spPlant.setBackgroundResource(R.drawable.error_spinner);
    }

    @Override
    public void errorShipToParty() {
        spShipTo.setBackgroundResource(R.drawable.error_spinner);
    }

    @Override
    public void errorPaymentTerm() {
        spPaymentTerm.setBackgroundResource(R.drawable.error_spinner);
    }

    @Override
    public void errorIncoTerm() {
        spIncoTerm1.setBackgroundResource(R.drawable.error_spinner);
    }

    @Override
    public void errorIncoTerm2() {
        etIncoTerm2.setBackgroundResource(R.drawable.edittext_border);
    }

    @Override
    public void errorShippingCondition() {
        spSpingCondition.setBackgroundResource(R.drawable.error_spinner);
    }

    @Override
    public void errorUnloading() {
        spUnloading.setBackgroundResource(R.drawable.error_spinner);
    }

    @Override
    public void errorReceiving() {
        spReceiving.setBackgroundResource(R.drawable.error_spinner);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_so_create, menu);
//        ((MainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.menu_sos_create), false, false);
//        ((MainActivity) getActivity()).setActionBarSubTitle(getString(R.string.so_stp1_create_sub_title));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_next:
                //next step
                if (!TextUtils.isEmpty(soListBean.getSalesArea())) {
                    if(Constants.getRollID(getActivity())){
                        if(Constants.isNetworkAvailable(getActivity())){
                            if(ConstantsUtils.isPinging()){
                                onNext();
                            }else {
                                try {
                                    ConstantsUtils.toastAMessage("Internet is ON, But it is not Active",getContext());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
//                                Toast.makeText(getActivity(), "Internet is ON, But it is not Active", Toast.LENGTH_LONG).show();
                            }
                        }else {
                            try {
                                ConstantsUtils.toastAMessage(getActivity().getString(R.string.no_network_conn),getContext());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//                            Toast.makeText(getActivity(), getActivity().getString(R.string.no_network_conn), Toast.LENGTH_LONG).show();
                        }
                    }else {
                        onNext();
                    }
                }else {
                    errorSalesArea();
                }
                return true;
            case R.id.menu_so_cancel:
//                SOUtils.redirectMainActivity(getActivity(), false);
               /* UtilConstants.dialogBoxWithCallBack(getActivity(), "", getString(R.string.so_create_cancel_so_msg), getString(R.string.yes), getString(R.string.no), false, new DialogCallBack() {
                    @Override
                    public void clickedStatus(boolean clickedStatus) {
                        if (clickedStatus) {
//                            getActivity().getSupportFragmentManager().popBackStack();

                        }
                    }
                });*/
                SOUtils.redirectMainActivity(getActivity(), comingFrom);

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void onNext() {
        if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
            if (presenter.validateHeader(soListBean)) {
                Intent intent = null;
                if (comingFrom == ConstantsUtils.SO_EDIT_ACTIVITY) {
                    intent = new Intent(getContext(), SOQuantityActivity.class);
                    ArrayList<SOItemBean> defaultItemList = soDefaultBean.getSoItemBeanArrayList();
                    intent.putExtra(Constants.EXTRA_SO_HEADER, defaultItemList);
                    intent.putExtra(Constants.EXTRA_SO_ITEM_LIST, defaultItemList);
                    soListBean.setSONo(soDefaultBean.getSONo());
                } else if (comingFrom == ConstantsUtils.SO_CREATE_SINGLE_MATERIAL) {
                    intent = new Intent(getContext(), SOCreateSingleMaterialActivity.class);
                    intent.putExtra(Constants.CHECK_ADD_MATERIAL_ITEM, false);
                } else if (comingFrom == ConstantsUtils.SO_EDIT_SINGLE_MATERIAL) {
                    intent = new Intent(getContext(), SOQuantityActivity.class);
                    ArrayList<SOItemBean> defaultItemList = soDefaultBean.getSoItemBeanArrayList();
                    intent.putExtra(Constants.EXTRA_SO_HEADER, defaultItemList);
                    intent.putExtra(Constants.EXTRA_SO_ITEM_LIST, defaultItemList);
                    soListBean.setSONo(soDefaultBean.getSONo());
                } else {
                    intent = new Intent(getContext(), SOCreateStpTwoActivity.class);
                    intent.putExtra(Constants.CPNo, mstrCustomerNo);
                    intent.putExtra(Constants.CPUID, mStrUID);
                    intent.putExtra(Constants.RetailerName, mStrCustomerName);
                    intent.putExtra(Constants.CPGUID32, mStrCPGUID32);
                    intent.putExtra(Constants.comingFrom, mStrComingFrom);
                    intent.putExtra(Constants.CHECK_ADD_MATERIAL_ITEM, false);
                }
                intent.putExtra(Constants.EXTRA_HEADER_BEAN, soListBean);
                intent.putExtra(Constants.EXTRA_SESSION_REQUIRED, isSessionRequired);
                if (soDefaultBean != null) {
                    intent.putExtra(Constants.EXTRA_SO_HEADER, soDefaultBean);
                }
                intent.putExtra(Constants.EXTRA_COME_FROM, comingFrom);
                startActivity(intent);
            }
        } else {
            ConstantsUtils.showAutoDateSetDialog(getActivity());
        }

    }

    @Override
    public void onStart() {
        super.onStart();
//        if (isRefresh) {
//            getActivity().getSupportFragmentManager().popBackStack();
//            isRefresh = false;
//        }
    }
}
