package com.rspl.sf.msfa.returnOrder.retrunFilter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.ConfigTypesetTypesBean;
import com.rspl.sf.msfa.so.SOUtils;

import java.util.ArrayList;

/**
 * Created by e10860 on 12/29/2017.
 */

public class FilterReturnActivity extends AppCompatActivity implements FilterReturnOrderView {

    public static final String EXTRA_RETURN_STATUS = "extraReturnStatus";
    public static final String EXTRA_RETURN_STATUS_NAME = "extraReturnStatusName";
    public static final String EXTRA_GR_STATUS = "extraReturnGRStatus";
    public static final String EXTRA_GR_STATUS_NAME = "extraReturnGRStatusName";
    private Toolbar toolbar;
    private String oldGRStatus;
    private String oldStatus;
    //    private DateFilterFragment dateFilterFragment;
    private LinearLayout rgStatus;
    private LinearLayout rgGrStatus;
    private FilterModelImpl filterModel;
    private String newStatusId, newGrStatus;
    private String newStatusName, newGRStatusName;
    private Context mContext;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_return);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.return_filter), 0);
        //date filter fragment open
        mContext = FilterReturnActivity.this;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            oldGRStatus = bundle.getString(EXTRA_GR_STATUS, "");
            oldStatus = bundle.getString(EXTRA_RETURN_STATUS, "");
           /* dateFilterFragment = new DateFilterFragment();
            dateFilterFragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.flContainer, dateFilterFragment);
            fragmentTransaction.commitAllowingStateLoss();*/
        }
        initUI();
        filterModel = new FilterModelImpl(this, FilterReturnActivity.this);
        filterModel.onStart();
    }

    private void initUI() {
        rgStatus = (LinearLayout) findViewById(R.id.rgStatus);
        rgGrStatus = (LinearLayout) findViewById(R.id.rgGrStatus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_apply, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.apply:
                getData();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void displayList(ArrayList<ConfigTypesetTypesBean> configStatus, ArrayList<ConfigTypesetTypesBean> configGRStatus) {
        displayStatus(configStatus);
        displayGRStatus(configGRStatus);
    }

    private void displayGRStatus(ArrayList<ConfigTypesetTypesBean> configGRStatus) {
        try {
            rgGrStatus.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
        RadioGroup radioGroupHeader = new RadioGroup(this);
        radioGroupHeader.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
        radioGroupHeader.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                if (radioButton != null) {
                    newGrStatus = radioButton.getTag(R.id.delv_id_value).toString();
                    newGRStatusName = radioButton.getTag(R.id.delv_name_value).toString();
                }
            }
        });
        rgGrStatus.addView(radioGroupHeader);
        Drawable img = null;
        if (configGRStatus != null) {
            if (configGRStatus.size() > 0) {
                for (int i = 0; i < configGRStatus.size(); i++) {
                    ConfigTypesetTypesBean configTypesetTypesBean = configGRStatus.get(i);
                    RadioButton radioButtonView = (RadioButton) LayoutInflater.from(mContext).inflate(R.layout.radio_button_item, null, false);
                    radioButtonView.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                    radioButtonView.setId(i + 10000);
                    radioButtonView.setTag(R.id.delv_id_value, configTypesetTypesBean.getTypes());
                    radioButtonView.setTag(R.id.delv_name_value, configTypesetTypesBean.getTypesName());
                    radioButtonView.setText(configTypesetTypesBean.getTypesName());
                    img = SOUtils.returnGrStatus(configTypesetTypesBean.getTypes(), mContext);
                    if (img != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            radioButtonView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, img, null);
                        } else {
                            radioButtonView.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                        }
                    }
                    radioGroupHeader.addView(radioButtonView);
                    if (oldGRStatus.equalsIgnoreCase(configTypesetTypesBean.getTypes())) {
                        radioButtonView.setChecked(true);
                    }
                }
            }
        }
    }

    private void displayStatus(ArrayList<ConfigTypesetTypesBean> configStatus) {
        try {
            rgStatus.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
        RadioGroup radioGroupHeader = new RadioGroup(this);
        radioGroupHeader.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
        radioGroupHeader.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                if (radioButton != null) {
                    newStatusId = radioButton.getTag(R.id.id_value).toString();
                    newStatusName = radioButton.getTag(R.id.name_value).toString();
                }
            }
        });
        Drawable img = null;
        rgStatus.addView(radioGroupHeader);
        if (configStatus != null) {
            if (configStatus.size() > 0) {
                for (int i = 0; i < configStatus.size(); i++) {
                    ConfigTypesetTypesBean configTypesetTypesBean = configStatus.get(i);
                    RadioButton radioButtonView = (RadioButton) LayoutInflater.from(mContext).inflate(R.layout.radio_button_item, null, false);
                    radioButtonView.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                    radioButtonView.setId(i + 1000);
                    radioButtonView.setTag(R.id.id_value, configTypesetTypesBean.getTypes());
                    radioButtonView.setTag(R.id.name_value, configTypesetTypesBean.getTypesName());
                    radioButtonView.setText(configTypesetTypesBean.getTypesName());
                    img = SOUtils.displayReturnStatus(configTypesetTypesBean.getTypes(), mContext);
                    if (img != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            radioButtonView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, img, null);
                        } else {
                            radioButtonView.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                        }
                    }
                    radioGroupHeader.addView(radioButtonView);
                    if (oldStatus.equalsIgnoreCase(configTypesetTypesBean.getTypes())) {
                        radioButtonView.setChecked(true);
                    }
                }
            }
        }

    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void hideProgressDialog() {

    }

    public void getData() {
        Intent intent = new Intent();
//        intent.putExtra(DateFilterFragment.EXTRA_DEFAULT, filterType);
//        intent.putExtra(DateFilterFragment.EXTRA_START_DATE, startDate);
//        intent.putExtra(DateFilterFragment.EXTRA_END_DATE, endDate);
        intent.putExtra(EXTRA_RETURN_STATUS, newStatusId);
        intent.putExtra(EXTRA_RETURN_STATUS_NAME, newStatusName);
        intent.putExtra(EXTRA_GR_STATUS, newGrStatus);
        intent.putExtra(EXTRA_GR_STATUS_NAME, newGRStatusName);
        setResult(ConstantsUtils.ACTIVITY_RESULT_FILTER, intent);
        finish();

    }
}
