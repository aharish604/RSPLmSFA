package com.rspl.sf.msfa.grreport;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.filter.DateFilterFragment;
import com.rspl.sf.msfa.socreate.ConfigTypeValues;

import java.util.List;

/**
 * Created by e10860 on 12/2/2017.
 */

public class GRFilterActivity extends AppCompatActivity implements  DateFilterFragment.OnFragmentInteractionListener {

    public static final String EXTRA_AS_COMPNY_CODE= "extraAsCompanyCode";
    public static final String EXTRA_AS_COMPNY_CODE_NAME = "extraAsCompanyCodeName";

    private Toolbar toolbar;
    private DateFilterFragment dateFilterFragment;
    private ProgressDialog progressDialog = null;
    private LinearLayout rgDelvStatus;
    private String oldCompanyCode = "";
    private String newCompanyCode = "";
    private String newCompanyCodeName = "";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gr_filter);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, "GR filter", 0);
        //date filter fragment open
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            oldCompanyCode = bundle.getString(EXTRA_AS_COMPNY_CODE, "");
            dateFilterFragment = new DateFilterFragment();
            dateFilterFragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.flContainer, dateFilterFragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
        initUI();

    }

    private void initUI() {
        rgDelvStatus = (LinearLayout) findViewById(R.id.rgDelvStatus);
    }

    @Override
    public void onFragmentInteraction(String startDate, String endDate, String filterType) {
        Intent intent = new Intent();
        intent.putExtra(DateFilterFragment.EXTRA_DEFAULT, filterType);
        intent.putExtra(DateFilterFragment.EXTRA_START_DATE, startDate);
        intent.putExtra(DateFilterFragment.EXTRA_END_DATE, endDate);
        intent.putExtra(EXTRA_AS_COMPNY_CODE, newCompanyCode);
        intent.putExtra(EXTRA_AS_COMPNY_CODE_NAME, newCompanyCodeName);
        setResult(ConstantsUtils.ACTIVITY_RESULT_FILTER, intent);
        finish();
    }


    private void displayStatusList(Context mContext, List<ConfigTypeValues> configTypesetTypesList) {
        try {
            rgDelvStatus.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
        RadioGroup radioGroupHeader = new RadioGroup(this);
        radioGroupHeader.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
        radioGroupHeader.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                if (radioButton != null) {
                    newCompanyCode = radioButton.getTag(R.id.delv_id_value).toString();
                    newCompanyCodeName = radioButton.getTag(R.id.delv_name_value).toString();
                }
            }
        });

        rgDelvStatus.addView(radioGroupHeader);
        if (configTypesetTypesList != null) {
            if (configTypesetTypesList.size() > 0) {
                for (int i = 0; i < configTypesetTypesList.size(); i++) {
                    ConfigTypeValues configTypesetTypesBean = configTypesetTypesList.get(i);
                    RadioButton radioButtonView = (RadioButton) LayoutInflater.from(mContext).inflate(R.layout.radio_button_item, null, false);
                    radioButtonView.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                    radioButtonView.setId(i + 10000);
                    radioButtonView.setTag(R.id.delv_id_value, configTypesetTypesBean.getType());
                    radioButtonView.setTag(R.id.delv_name_value, configTypesetTypesBean.getTypeName());
                    radioButtonView.setText(configTypesetTypesBean.getTypeName());
                    radioGroupHeader.addView(radioButtonView);
                    if (oldCompanyCode.equalsIgnoreCase(configTypesetTypesBean.getType())) {
                        radioButtonView.setChecked(true);
                    }
                }
            }
        }
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
                dateFilterFragment.getDataBasedOnDate(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
