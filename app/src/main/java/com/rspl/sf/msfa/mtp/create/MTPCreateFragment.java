package com.rspl.sf.msfa.mtp.create;


import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import androidx.core.view.MenuItemCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.attendance.AttendanceConfigTypesetTypesBean;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.CustomerBean;
import com.rspl.sf.msfa.mtp.MTPHeaderBean;
import com.rspl.sf.msfa.routeplan.customerslist.CustomerPresenter;
import com.rspl.sf.msfa.routeplan.customerslist.ICustomerViewPresenter;
import com.rspl.sf.msfa.so.SOUtils;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.ui.MaterialDesignSpinner;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MTPCreateFragment extends Fragment implements ICustomerViewPresenter<CustomerBean>, AdapterInterface<CustomerBean> {


    private RecyclerView recyclerView;
    private SimpleRecyclerViewAdapter<CustomerBean> simpleAdapter;
    private CustomerPresenter presenter;
    private SwipeRefreshLayout swipeRefresh;
    private MaterialDesignSpinner spActivity;
    private TextView tvActivityDesc;
    private TextInputLayout tiRemarks;
    private String strActivityDesc="";
    private TextView tvRemarks;
    private TextView tvDate;
    private TextView tvDay;
    private ConstraintLayout clHeader;
    private MTPHeaderBean mtpHeaderBean;
    private MTPHeaderBean mtpResultHeaderBean;
    private EditText etRemarks;
    private int listPos = -1;
    private ArrayList<CustomerBean> customerSelectedList = new ArrayList<>();
    private LinearLayout llHeader = null;
    private boolean isAsmLogin=false;
    private String initior="";
    private String comingFrom="";

    public MTPCreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mtpHeaderBean = (MTPHeaderBean) bundle.getSerializable(Constants.EXTRA_BEAN);
            listPos = bundle.getInt(ConstantsUtils.EXTRA_POS, 0);
            isAsmLogin = bundle.getBoolean(ConstantsUtils.EXTRA_ISASM_LOGIN, false);
            initior = bundle.getString(Constants.Initiator, "");
            comingFrom = bundle.getString(Constants.comingFrom, "");
        }
        if (mtpHeaderBean == null) {
            mtpHeaderBean = new MTPHeaderBean();
        }
        mtpResultHeaderBean = mtpHeaderBean;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mtp_create, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spActivity = (MaterialDesignSpinner) view.findViewById(R.id.spActivity);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setDistanceToTriggerSync(ConstantsUtils.SWIPE_REFRESH_DISABLE);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        etRemarks = (EditText) view.findViewById(R.id.etRemarks);
        llHeader = (LinearLayout) view.findViewById(R.id.llHeader);

        tvActivityDesc = (TextView) view.findViewById(R.id.tvActivityDesc);
        tiRemarks = (TextInputLayout) view.findViewById(R.id.tiRemarks);
        tvRemarks = (TextView) view.findViewById(R.id.tvRemarks);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvDay = (TextView) view.findViewById(R.id.tvDay);
        clHeader = (ConstraintLayout) view.findViewById(R.id.clHeader);
        tvDate.setText(mtpHeaderBean.getDate());
        tvDay.setText(mtpHeaderBean.getDay());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        simpleAdapter = new SimpleRecyclerViewAdapter<CustomerBean>(getContext(), R.layout.mtp_customer_select_item, this, null, null);
        recyclerView.setAdapter(simpleAdapter);
        initializeObjects(getContext());
    }

    @Override
    public void onItemClick(CustomerBean customerBean, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new MTPCreateVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, final CustomerBean customerBean) {
        ((MTPCreateVH) viewHolder).cbName.setText(customerBean.getCustomerName());
        if (!TextUtils.isEmpty(customerBean.getMobile1())) {
            ((MTPCreateVH) viewHolder).ivMobile.setVisibility(View.VISIBLE);
            ((MTPCreateVH) viewHolder).ivMobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(customerBean.getMobile1())) {
                        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.tel_txt + (customerBean.getMobile1())));
                        startActivity(dialIntent);
                    }
                }
            });
        }else {
            ((MTPCreateVH) viewHolder).ivMobile.setVisibility(View.GONE);
        }
        ((MTPCreateVH) viewHolder).cbName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                customerBean.setChecked(isChecked);
                if (customerSelectedList.contains(customerBean)) {
                    customerSelectedList.remove(customerBean);
                } else {
                    customerSelectedList.add(customerBean);
                }
            }
        });
        ((MTPCreateVH) viewHolder).cbName.setChecked(customerBean.isChecked());
    }

    @Override
    public void initializeUI(Context context) {

    }

    @Override
    public void initializeClickListeners() {

    }

    @Override
    public void initializeObjects(Context context) {
        presenter = new CustomerPresenter(getContext(), getActivity(), this, "", "","");
        presenter.loadMTPCustomerList(mtpHeaderBean.getMTPRoutePlanBeanArrayList(),isAsmLogin,initior,comingFrom);
        String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.EntityType
                + " eq 'Attendance'&$orderby=" + Constants.ID + " asc";
        try {
            final List<AttendanceConfigTypesetTypesBean> list = OfflineManager.getAttendanceConfig(mStrConfigQry);
//            arrAttendanceType = OfflineManager.getConfigListAttendance(mStrConfigQry);

            ArrayAdapter<AttendanceConfigTypesetTypesBean> adapterPlant = new ArrayAdapter<AttendanceConfigTypesetTypesBean>(getContext(), R.layout.custom_textview, R.id.tvItemValue, list) {
                @Override
                public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                    final View v = super.getDropDownView(position, convertView, parent);
                    ConstantsUtils.selectedView(v, spActivity, position, getContext());
                    return v;
                }
            };
            adapterPlant.setDropDownViewResource(R.layout.spinnerinside);
            spActivity.setAdapter(adapterPlant);
            if (!TextUtils.isEmpty(mtpHeaderBean.getActivityID()))
                spActivity.setSelection(SOUtils.getPossATConfig(list, mtpHeaderBean.getActivityID()));
            spActivity.showFloatingLabel();
            spActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    strActivityDesc=list.get(position).getTypes();
                    tvActivityDesc.setText(list.get(position).getTypeName());
                    mtpResultHeaderBean.setActivityDec(list.get(position).getTypeName());
                    mtpResultHeaderBean.setActivityID(list.get(position).getTypes());
//                    soListBean.setPlant(plantList.get(position).getID());
//                    soListBean.setPlantDesc(plantList.get(position).getDescription());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        etRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvRemarks.setText(s.toString());
                mtpResultHeaderBean.setRemarks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etRemarks.setText(mtpHeaderBean.getRemarks());
    }

    @Override
    public void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager) {

    }

    @Override
    public void showProgressDialog() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideProgressDialog() {
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void onRefreshData() {

    }

    @Override
    public void customersListSync() {

    }

    @Override
    public void openFilter(String filterType, String status, String grStatus) {

    }

    @Override
    public void setFilterDate(String filterType) {

    }

    @Override
    public void displayRefreshTime(String refreshTime) {

    }

    @Override
    public void displayMsg(String msg) {
        ConstantsUtils.displayLongToast(getContext(), msg);
    }

    @Override
    public void sendSelectedItem(Intent intent) {
        intent.putExtra(ConstantsUtils.EXTRA_POS, listPos);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void errorMsgEditText(String error) {

    }

    @Override
    public void errorMsgEditText1(String error) {

    }

    @Override
    public void searchResult(ArrayList<CustomerBean> retailerSearchList) {
        simpleAdapter.refreshAdapter(retailerSearchList);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_mtp_create, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.menu_search_item);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getActivity().getComponentName());
        MenuItemCompat.setOnActionExpandListener(searchMenuItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        if (llHeader != null)
                            llHeader.setVisibility(View.GONE);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        if (llHeader != null)
                            llHeader.setVisibility(View.VISIBLE);
                        return true;
                    }
                });
        SearchView mSearchView = (SearchView) searchMenuItem.getActionView();
        MenuItemCompat.setActionView(searchMenuItem, mSearchView);
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.mtp_create_search_hint));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.onSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                presenter.onSearch(newText);
                return false;
            }
        });
        presenter.onSearch("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.apply:
                if(llHeader != null && llHeader.getVisibility() == View.GONE) {
                    if (TextUtils.isEmpty(mtpResultHeaderBean.getActivityID())) {
                        llHeader.setVisibility(View.VISIBLE);
                        UtilConstants.hideKeyboardFrom(getContext());
                    }
                }
                if (listPos != -1 && validateItem()) {
                    presenter.sendResult(mtpResultHeaderBean, mtpHeaderBean, isAsmLogin);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private boolean validateItem() {
        boolean isCheck = true;
            if (strActivityDesc.equalsIgnoreCase("99") && etRemarks.getText().toString().trim().equals("")) {
                tiRemarks.setError("Enter remarks");
                isCheck = false;
            } else {
                tiRemarks.setErrorEnabled(false);
            }
            if (TextUtils.isEmpty(mtpResultHeaderBean.getActivityID())) {
                spActivity.setError("Select activity");
                isCheck = false;
            } else {
                spActivity.setEnableErrorLabel(false);
            }

        if(isCheck  && !TextUtils.isEmpty(strActivityDesc) && (strActivityDesc.equalsIgnoreCase("01") ||
                strActivityDesc.equalsIgnoreCase("04"))){
            if(customerSelectedList.isEmpty()){
                isCheck = false;
                ConstantsUtils.displayLongToast(getContext(), getString(R.string.error_sel_atleast_one_cust));
            }
        }

        return isCheck;
    }

  /*  private void sendResult() {
        ArrayList<MTPRoutePlanBean> mtpRoutePlanList = new ArrayList<>();
        if (!customerSelectedList.isEmpty()) {
            for (CustomerBean customerBean : customerSelectedList) {
                MTPRoutePlanBean mtpRoutePlanBean = new MTPRoutePlanBean();
                mtpRoutePlanBean.setVisitDate(mtpHeaderBean.getFullDate());
                mtpRoutePlanBean.setDate(mtpHeaderBean.getDate());
                mtpRoutePlanBean.setDay(mtpHeaderBean.getDay());
                mtpRoutePlanBean.setRemarks(mtpResultHeaderBean.getRemarks());
                mtpRoutePlanBean.setActivityDec(mtpResultHeaderBean.getActivityDec());
                mtpRoutePlanBean.setActivityId(mtpResultHeaderBean.getActivityID());
                mtpRoutePlanBean.setCustomerNo(customerBean.getCustomerId());
                mtpRoutePlanBean.setCustomerName(customerBean.getCustomerName());
                mtpRoutePlanBean.setAddress(customerBean.getAddress1());
                mtpRoutePlanBean.setPostalCode(customerBean.getPostalCode());
                mtpRoutePlanBean.setMobile1(customerBean.getMobile1());
//                mtpRoutePlanBean.setRouteSchPlanGUID();
                mtpRoutePlanList.add(mtpRoutePlanBean);
            }
            if (customerSelectedList.size() == 1) {
                mtpResultHeaderBean.setCustomerName(customerSelectedList.get(0).getCustomerName());
            } else {
                mtpResultHeaderBean.setCustomerName(customerSelectedList.get(0).getCustomerName() + "...");
            }
            mtpResultHeaderBean.setCustomerNo(customerSelectedList.get(0).getCustomerId());
        }
        mtpResultHeaderBean.setMTPRoutePlanBeanArrayList(mtpRoutePlanList);
        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_BEAN, mtpResultHeaderBean);
        intent.putExtra(ConstantsUtils.EXTRA_POS, listPos);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }*/
}
