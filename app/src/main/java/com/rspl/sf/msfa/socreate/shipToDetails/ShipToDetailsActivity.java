package com.rspl.sf.msfa.socreate.shipToDetails;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.so.SOUtils;
import com.rspl.sf.msfa.so.ValueHelpBean;
import com.rspl.sf.msfa.soDetails.SODetailsActivity;
import com.rspl.sf.msfa.socreate.CreditLimitBean;
import com.rspl.sf.msfa.socreate.CustomerPartnerFunctionBean;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.solist.SOListBean;
import com.rspl.sf.msfa.ui.MaterialDesignSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class ShipToDetailsActivity extends AppCompatActivity implements ShipToView, View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private Toolbar toolbar;
    private Context mContext;
    private ProgressDialog progressDialog;
    private ShipToPresenterImpl presenter;
    private boolean isSessionRequired = false;
    private SOListBean soListBeanHeader = null;
    private MaterialDesignSpinner spShipTo;
    //    private MaterialDesignSpinner spOrderType;
    private MaterialDesignSpinner spPaymentTerm;
    private MaterialDesignSpinner spIncoTerm1;
    private MaterialDesignSpinner spShippingCondition;
    // private MovableFloatingActionButton fabMatView;
    private NestedScrollView nsvContainer;
    private EditText etIncoterm2;
    private TextInputLayout tiIncoterm2;
    // private EditText etPODate;
    private DatePickerDialog datePickerDialog;
    private MaterialDesignSpinner spPODate;
    private ArrayList<String> dateList = new ArrayList<String>();
    private int comingFrom = 0;
    private TextView tvSONo, tvOrderType,tvaddress;
    private MaterialDesignSpinner spSalesOffice;
    private MaterialDesignSpinner spSalesGroup;
    private Switch switchOneTimeShip;
    private View oneTimeShip;
    private MaterialDesignSpinner spCountry;
    private MaterialDesignSpinner spRegion;
    private TextView tvCreditLimit;
    private String customerNo;
    // private TextInputLayout tiPoDate;
    private TextInputLayout tiFirstName, tiLasttName, tiAddrName, tiStreet2Name, tiStreet3Name, tiStreet4Name, tiDistrict, tiCity, tiCountry, tiRegion, tiPostalCode;
    private EditText etFirstName, etLastName, etAddrName, etStreet2Name, etStreet3Name, etStreet4Name, etDistrict, etCity, etPostalCode;
    private SOListBean soDefaultBean = null;
    private String orderTypeValue = "";
    private ArrayList<SOItemBean> selectedItemList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ship_to_details);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            soListBeanHeader = (SOListBean) bundle.getSerializable(Constants.EXTRA_HEADER_BEAN);
            comingFrom = bundle.getInt(Constants.EXTRA_COME_FROM, 0);
            soDefaultBean = (SOListBean) bundle.getSerializable(Constants.EXTRA_SO_HEADER);
        }
        if (soListBeanHeader == null) {
            soListBeanHeader = new SOListBean();
        }
        selectedItemList = soListBeanHeader.getSoItemBeanArrayList();
        customerNo = soListBeanHeader.getSoldTo();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mContext = ShipToDetailsActivity.this;
        orderTypeValue = getString(R.string.so_new_order);
        if (comingFrom == ConstantsUtils.SO_APPROVAL_EDIT_ACTIVITY) {
            ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.menu_sos_edit), 0);
            orderTypeValue = soListBeanHeader.getSONo();
        } else
            ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.menu_sos_create), 0);
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(getString(R.string.so_shipping_sub_title));
        initUI();
        presenter = new ShipToPresenterImpl(customerNo, ShipToDetailsActivity.this, this, isSessionRequired, soListBeanHeader);
        presenter.onStart();

    }

    private void initUI() {
        spShipTo = (MaterialDesignSpinner) findViewById(R.id.spShipTo);
//        spOrderType = (MaterialDesignSpinner) findViewById(R.id.spOrderType);
        spPaymentTerm = (MaterialDesignSpinner) findViewById(R.id.spPaymentTerm);
        spIncoTerm1 = (MaterialDesignSpinner) findViewById(R.id.spIncoTerm1);
        spShippingCondition = (MaterialDesignSpinner) findViewById(R.id.spShippingCondition);
        spSalesOffice = (MaterialDesignSpinner) findViewById(R.id.spSalesOffice);
        spSalesGroup = (MaterialDesignSpinner) findViewById(R.id.spSalesGroup);
       /* fabMatView = (MovableFloatingActionButton) findViewById(R.id.fabMatView);
        fabMatView.setOnClickListener(this);*/
        nsvContainer = (NestedScrollView) findViewById(R.id.nsvContainer);
        tiIncoterm2 = (TextInputLayout) findViewById(R.id.tiIncoterm2);
        //tiPoDate = (TextInputLayout) findViewById(R.id.tiPoDate);
        etIncoterm2 = (EditText) findViewById(R.id.etIncoterm2);
        tvSONo = (TextView) findViewById(R.id.tvSONo);
        tvaddress = (TextView) findViewById(R.id.tvaddress);
        tvOrderType = (TextView) findViewById(R.id.tvOrderType);
        switchOneTimeShip = (Switch) findViewById(R.id.switchOneTimeShip);
        switchOneTimeShip.setVisibility(View.GONE);
        oneTimeShip = (View) findViewById(R.id.oneTimeShip);
        tiFirstName = (TextInputLayout) oneTimeShip.findViewById(R.id.tiFirstName);
        tiLasttName = (TextInputLayout) oneTimeShip.findViewById(R.id.tiLasttName);
        tiAddrName = (TextInputLayout) oneTimeShip.findViewById(R.id.tiAddrName);
        tiStreet2Name = (TextInputLayout) oneTimeShip.findViewById(R.id.tiStreet2Name);
        tiStreet3Name = (TextInputLayout) oneTimeShip.findViewById(R.id.tiStreet3Name);
        tiStreet4Name = (TextInputLayout) oneTimeShip.findViewById(R.id.tiStreet4Name);
        tiStreet3Name = (TextInputLayout) oneTimeShip.findViewById(R.id.tiStreet3Name);
        tiDistrict = (TextInputLayout) oneTimeShip.findViewById(R.id.tiDistrict);
        tiCity = (TextInputLayout) oneTimeShip.findViewById(R.id.tiCity);
        tiPostalCode = (TextInputLayout) oneTimeShip.findViewById(R.id.tiPostalCode);

        etFirstName = (EditText) oneTimeShip.findViewById(R.id.etFirstName);
        etLastName = (EditText) oneTimeShip.findViewById(R.id.etLastName);
        etAddrName = (EditText) oneTimeShip.findViewById(R.id.etAddrName);
        etStreet2Name = (EditText) oneTimeShip.findViewById(R.id.etStreet2Name);
        etStreet3Name = (EditText) oneTimeShip.findViewById(R.id.etStreet3Name);
        etStreet4Name = (EditText) oneTimeShip.findViewById(R.id.etStreet4Name);
        etDistrict = (EditText) oneTimeShip.findViewById(R.id.etDistrict);
        etCity = (EditText) oneTimeShip.findViewById(R.id.etCity);
        spCountry = (MaterialDesignSpinner) oneTimeShip.findViewById(R.id.spCountry);
        spRegion = (MaterialDesignSpinner) oneTimeShip.findViewById(R.id.spRegion);
        etPostalCode = (EditText) oneTimeShip.findViewById(R.id.etPostalCode);
        tvCreditLimit = (TextView) findViewById(R.id.tvAmount);
        TextView tvNewOrder = (TextView) findViewById(R.id.tvNewOrder);
        tvNewOrder.setText(orderTypeValue);

        //etPODate = (EditText) findViewById(R.id.etPODate);
//        spPODate = (MaterialDesignSpinner) findViewById(R.id.spPODate);
//        etPODate.setInputType(InputType.TYPE_NULL);
//        etPODate.setShowSoftInputOnFocus(false);
        // etPODate.setOnClickListener(this);
        //  tiPoDate.setOnClickListener(this);
        nsvContainer.setVisibility(View.GONE);
        dateList.add("0");
        tvOrderType.setText(getString(R.string.po_details_display_value, soListBeanHeader.getOrderTypeDesc(), soListBeanHeader.getOrderType()));
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        datePickerDialog = new DatePickerDialog(ShipToDetailsActivity.this, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        etIncoterm2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiIncoterm2.setErrorEnabled(false);
                soListBeanHeader.setIncoterm2(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        switchOneTimeShip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                soListBeanHeader.setOneTimeShipTo(isChecked);
                if (isChecked) {
                    oneTimeShip.setVisibility(View.VISIBLE);
                } else {
                    oneTimeShip.setVisibility(View.GONE);
                }
            }
        });
        etFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                soListBeanHeader.setCustFirstName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiLasttName.setErrorEnabled(false);
                soListBeanHeader.setCustLastName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etAddrName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiAddrName.setErrorEnabled(false);
                soListBeanHeader.setCustAddress1(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etStreet2Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                soListBeanHeader.setCustAddress2(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etStreet3Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                soListBeanHeader.setCustAddress3(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etStreet4Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                soListBeanHeader.setCustAddress4(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etDistrict.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiDistrict.setErrorEnabled(false);
                soListBeanHeader.setCustDistrict(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiCity.setErrorEnabled(false);
                soListBeanHeader.setCustCity(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        /*etCountry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                soListBeanHeader.setCustCountry(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etRegion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                soListBeanHeader.setCustRegion(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
        etPostalCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiPostalCode.setErrorEnabled(false);
                soListBeanHeader.setCustPostalCode(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = ConstantsUtils.showProgressDialog(mContext, message);
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void displayMessage(String message) {
        ConstantsUtils.displayLongToast(mContext, message);
    }

    @Override
    public void displayBySalesArea(final ArrayList<CustomerPartnerFunctionBean> shipToList, final ArrayList<ValueHelpBean> incoterm1List, final ArrayList<ValueHelpBean> paymentTermList, final ArrayList<ValueHelpBean> salesOfficeList, final ArrayList<ValueHelpBean> shippingConditionList, final ArrayList<ValueHelpBean> countryList) {

        ArrayAdapter<CustomerPartnerFunctionBean> adapterShipToList = new ArrayAdapter<CustomerPartnerFunctionBean>(mContext, R.layout.custom_textview, R.id.tvItemValue, shipToList) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spShipTo, position, getContext());
                return v;
            }
        };
        adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
        spShipTo.setAdapter(adapterShipToList);
        spShipTo.showFloatingLabel();
        spShipTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                soListBeanHeader.setShipTo(shipToList.get(position).getPartnerCustomerNo());
                soListBeanHeader.setShipToName(shipToList.get(position).getPartnerCustomerName());
                ArrayList<CustomerPartnerFunctionBean> customerPartnerFunctionBeanArrayList = new ArrayList<>();
                customerPartnerFunctionBeanArrayList.add(shipToList.get(position));
                soListBeanHeader.setCustomerPartnerFunctionList(customerPartnerFunctionBeanArrayList);
                if(!TextUtils.isEmpty(shipToList.get(position).getCompleteAddress())){
                    tvaddress.setVisibility(View.VISIBLE);
                    try {
                        tvaddress.setText(shipToList.get(position).getCompleteAddress());
                    } catch (Exception e) {
                        tvaddress.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }else {
                    tvaddress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ArrayAdapter<ValueHelpBean> adapterSalesOffice = new ArrayAdapter<ValueHelpBean>(ShipToDetailsActivity.this, R.layout.custom_textview, R.id.tvItemValue, salesOfficeList) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spSalesOffice, position, getContext());
                return v;
            }
        };
        adapterSalesOffice.setDropDownViewResource(R.layout.spinnerinside);
        spSalesOffice.setAdapter(adapterSalesOffice);
//        if (soDefaultBean != null) {
//            spSalesOffice.setSelection(SOUtils.getPoss(salesOfficeList, soDefaultBean.getSalesOfficeId()));
//        }
        spSalesOffice.showFloatingLabel();
        spSalesOffice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                soListBeanHeader.setSalesOfficeId(salesOfficeList.get(position).getID());
                soListBeanHeader.setSaleOffDesc(salesOfficeList.get(position).getDescription());
                if (TextUtils.isEmpty(soListBeanHeader.getSalesOfficeId())) {
                    spSalesGroup.setVisibility(View.GONE);
                    soListBeanHeader.setSaleGrpDesc("");
                    soListBeanHeader.setSalesGroup("");
                } else {
                    spSalesGroup.setVisibility(View.VISIBLE);
                    presenter.basedOnSalesOffice(soListBeanHeader.getSalesOfficeId(), soListBeanHeader.getCustomerNo());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

       /* ArrayAdapter<ValueHelpBean> valueHelpAdapter = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_textview, R.id.tvItemValue, ordtypeList) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spOrderType, position, getContext());
                return v;
            }
        };
        valueHelpAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spOrderType.setAdapter(valueHelpAdapter);
        spOrderType.showFloatingLabel();
        spOrderType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                soListBeanHeader.setOrderType(ordtypeList.get(position).getID());
                soListBeanHeader.setOrderTypeDesc(ordtypeList.get(position).getDescription());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
        ArrayAdapter<ValueHelpBean> valueHelpAdapter = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_textview, R.id.tvItemValue, paymentTermList) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spPaymentTerm, position, getContext());
                return v;
            }
        };
        valueHelpAdapter.setDropDownViewResource(R.layout.spinnerinside);
        if (soDefaultBean != null) {
            spPaymentTerm.setSelection(SOUtils.getPoss(paymentTermList, soDefaultBean.getPaymentTerm()));
        }
        spPaymentTerm.setAdapter(valueHelpAdapter);
        spPaymentTerm.showFloatingLabel();
        spPaymentTerm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                soListBeanHeader.setPaymentTerm(paymentTermList.get(position).getID());
                soListBeanHeader.setPaymentTermDesc(paymentTermList.get(position).getDescription());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        valueHelpAdapter = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_textview, R.id.tvItemValue, incoterm1List) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spIncoTerm1, position, getContext());
                return v;
            }
        };
        valueHelpAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spIncoTerm1.setAdapter(valueHelpAdapter);
        spIncoTerm1.showFloatingLabel();
        spIncoTerm1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                soListBeanHeader.setIncoTerm1(incoterm1List.get(position).getID());
                soListBeanHeader.setIncoterm1Desc(incoterm1List.get(position).getDescription());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        valueHelpAdapter = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_textview, R.id.tvItemValue, shippingConditionList) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spShippingCondition, position, getContext());
                return v;
            }
        };
        valueHelpAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spShippingCondition.setAdapter(valueHelpAdapter);
        spShippingCondition.showFloatingLabel();
        spShippingCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                soListBeanHeader.setShippingPoint(shippingConditionList.get(position).getID());
                soListBeanHeader.setShippingPointDesc(shippingConditionList.get(position).getDescription());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        valueHelpAdapter = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_textview, R.id.tvItemValue, countryList) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spCountry, position, getContext());
                return v;
            }
        };
        valueHelpAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spCountry.setAdapter(valueHelpAdapter);
        spCountry.showFloatingLabel();
        spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                soListBeanHeader.setCustCountry(countryList.get(position).getID());
                soListBeanHeader.setCustCountryDesc(countryList.get(position).getDescription());
                if (TextUtils.isEmpty(soListBeanHeader.getCustCountry())) {
                    spRegion.setVisibility(View.GONE);
                    soListBeanHeader.setCustRegion("");
                    soListBeanHeader.setCustRegionDesc("");
                } else {
                    spRegion.setVisibility(View.VISIBLE);
                    presenter.basedOnCountry(soListBeanHeader.getCustCountry());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

       /* ArrayAdapter<String> arrayStringAdapter = new ArrayAdapter<String>(mContext, R.layout.custom_textview, R.id.tvItemValue, dateList) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
//                ConstantsUtils.selectedView(v, spShippingCondition, position, getContext());
                return v;
            }
        };
        arrayStringAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spPODate.setAdapter(arrayStringAdapter);
        spPODate.showFloatingLabel();
        spPODate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                soListBeanHeader.setShippingPoint(shippingConditionList.get(position).getID());
//                soListBeanHeader.setShippingPointDesc(shippingConditionList.get(position).getDescription());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
        nsvContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void errorOrderType(String message) {
//        if (spOrderType.getVisibility() == View.VISIBLE)
//            spOrderType.setError(message);
    }

    @Override
    public void errorShipToParty(String message) {
        if (spShipTo.getVisibility() == View.VISIBLE)
            spShipTo.setError(message);
    }

    @Override
    public void errorPaymentTerm(String message) {
        if (spPaymentTerm.getVisibility() == View.VISIBLE)
            spPaymentTerm.setError(message);
    }

    @Override
    public void errorIncoTerm(String message) {
        if (spIncoTerm1.getVisibility() == View.VISIBLE)
            spIncoTerm1.setError(message);
    }

    @Override
    public void errorIncoTerm2(String message) {
        tiIncoterm2.setErrorEnabled(true);
        tiIncoterm2.setError(message);
    }

    @Override
    public void errorShippingCondition(String message) {
        if (spShippingCondition.getVisibility() == View.VISIBLE)
            spShippingCondition.setError(message);
    }

    @Override
    public void errorLastName(String message) {
        tiLasttName.setErrorEnabled(true);
        tiLasttName.setError(message);

    }

    @Override
    public void errorAddress1(String s) {
        tiAddrName.setErrorEnabled(true);
        tiAddrName.setError(s);
    }

    @Override
    public void errorDistrict(String s) {
        tiDistrict.setErrorEnabled(true);
        tiDistrict.setError(s);
    }

    @Override
    public void errorCity(String s) {
        tiCity.setErrorEnabled(true);
        tiCity.setError(s);
    }

    @Override
    public void errorCountry(String s) {
        if (spCountry.getVisibility() == View.VISIBLE)
            spCountry.setError(s);
    }

    @Override
    public void errorRegion(String s) {
        if (spRegion.getVisibility() == View.VISIBLE)
            spRegion.setError(s);
    }

    @Override
    public void errorPostalCode(String s) {
        tiPostalCode.setErrorEnabled(true);
        tiPostalCode.setError(s);
    }

    @Override
    public void openReviewScreen(List<CreditLimitBean> limitBeanList) {
        soListBeanHeader.setCreditControlAreas(limitBeanList);
        Intent intent = new Intent(this, SODetailsActivity.class);
        intent.putExtra(Constants.EXTRA_HEADER_BEAN, soListBeanHeader);
        intent.putExtra(Constants.EXTRA_SO_CREATE_TITLE, true);
//       intent.putExtra(Constants.CustomerName, mStrCustomerName);
//        intent.putExtra(Constants.CustomerNo, mStrCustomerNo);
       // intent.putExtra(Constants.EXTRA_SO_CREDIT_LIMIT, limitBeanList);
        intent.putExtra(Constants.EXTRA_COME_FROM, comingFrom);
        startActivity(intent);
    }

    @Override
    public void displaySalesGrp(final ArrayList<ValueHelpBean> salesGrpList) {
        ArrayAdapter<ValueHelpBean> adapterSalesGrp = new ArrayAdapter<ValueHelpBean>(ShipToDetailsActivity.this, R.layout.custom_textview, R.id.tvItemValue, salesGrpList) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spSalesGroup, position, getContext());
                return v;
            }
        };
        adapterSalesGrp.setDropDownViewResource(R.layout.spinnerinside);
        spSalesGroup.setAdapter(adapterSalesGrp);
//        if (soDefaultBean != null) {
//            spSalesGroup.setSelection(SOUtils.getPoss(salesGrpList, soDefaultBean.getSalesGroup()));
//        }
        spSalesGroup.showFloatingLabel();
        spSalesGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                soListBeanHeader.setSalesGroup(salesGrpList.get(position).getID());
                soListBeanHeader.setSaleGrpDesc(salesGrpList.get(position).getDescription());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spSalesGroup.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayOneTimeShipToParty() {
        switchOneTimeShip.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayRegion(final ArrayList<ValueHelpBean> regionList) {
        ArrayAdapter<ValueHelpBean> valueHelpAdapter = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_textview, R.id.tvItemValue, regionList) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spRegion, position, getContext());
                return v;
            }
        };
        valueHelpAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spRegion.setAdapter(valueHelpAdapter);
        spRegion.showFloatingLabel();
        spRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                soListBeanHeader.setCustRegion(regionList.get(position).getID());
                soListBeanHeader.setCustRegionDesc(regionList.get(position).getDescription());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        if (progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_so_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_next:
                //next step
                try{
                    SharedPreferences sharedPreferences = ShipToDetailsActivity.this.getSharedPreferences(Constants.PREFS_NAME, 0);
                    if (sharedPreferences.getBoolean("writeDBGLog", false)) {
                        Constants.writeDebug = sharedPreferences.getBoolean("writeDBGLog", false);
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                if (ConstantsUtils.isAutomaticTimeZone(ShipToDetailsActivity.this)) {
                    if (presenter.validateFields(soListBeanHeader)) {
                        if(Constants.getRollID(ShipToDetailsActivity.this)){
                            if(Constants.isNetworkAvailable(ShipToDetailsActivity.this)){
                                if(ConstantsUtils.isPinging()){

                                    if(Constants.writeDebug){
                                        LogManager.writeLogDebug(" SO Create validating Order");
                                    }
                                    presenter.startSimulate(soListBeanHeader);
                                }else {
                                    try {
                                        ConstantsUtils.toastAMessage("Internet is ON, But it is not Active",ShipToDetailsActivity.this);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
//                                    Toast.makeText(ShipToDetailsActivity.this, "Internet is ON, But it is not Active", Toast.LENGTH_LONG).show();
                                }
                            }else {
                                try {
                                    ConstantsUtils.toastAMessage(this.getString(R.string.no_network_conn),ShipToDetailsActivity.this);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
//                                Toast.makeText(ShipToDetailsActivity.this, this.getString(R.string.no_network_conn), Toast.LENGTH_LONG).show();
                            }
                        }else {
                            presenter.startSimulate(soListBeanHeader);
                        }
                    }
                } else {
                    ConstantsUtils.showAutoDateSetDialog(ShipToDetailsActivity.this);
                }
                break;
            case R.id.menu_so_cancel:
                SOUtils.redirectMainActivity(ShipToDetailsActivity.this, comingFrom);
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
          /*  case R.id.fabMatView:
                Intent intent = new Intent(mContext, ViewSelectedMaterialActivity.class);
                intent.putExtra(Constants.EXTRA_SO_ITEM_LIST, soListBeanHeader.getSoItemBeanArrayList());
                mContext.startActivity(intent);
                break;*/
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

        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        setDateToView(year, month, dayOfMonth);
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
        soListBeanHeader.setPODate(mYear + "-" + mon + "-" + day);

     /*   etPODate.setText(new StringBuilder().append(mDay).append("-")
                .append(Constants.ORG_MONTHS[mMonth]).append("-").append("")
                .append(mYear).toString());*/
//        displayRemovePoDateIcon();
    }

    @Override
    public void onBackPressed() {
        if (selectedItemList != null) {
            Intent intent = new Intent();
            intent.putExtra(Constants.EXTRA_SO_ITEM_LIST, selectedItemList);
            setResult(ConstantsUtils.ACTIVITY_RESULT_MATERIAL, intent);
            finish();
        }
    }
}
