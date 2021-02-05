package com.rspl.sf.msfa.reports.InvoiceHistory.invocieFilter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.rspl.sf.msfa.filter.DateFilterFragment;
import com.rspl.sf.msfa.mbo.ConfigTypesetTypesBean;
import com.rspl.sf.msfa.so.SOUtils;

import java.util.ArrayList;

/**
 * Created by e10860 on 12/2/2017.
 */

public class InvoiceFilterActivity  extends AppCompatActivity implements InvoiceFilterView, DateFilterFragment.OnFragmentInteractionListener {

    public static final String EXTRA_INVOICE_STATUS = "extraInvoiceStatus";
    public static final String EXTRA_INVOICE_STATUS_NAME = "extraInvoiceStatusName";
    public static final String EXTRA_INVOICE_GR_STATUS = "extraInvoiceGrStatus";
    public static final String EXTRA_INVOICE_GR_STATUS_NAME = "extraInvoiceGrStatusName";

    private Toolbar toolbar;
    private DateFilterFragment dateFilterFragment;
    private InvoiceFilterModelImpl filterModel;
    private ProgressDialog progressDialog = null;
    private LinearLayout rgStatus;
    private LinearLayout rgDelvStatus;
    private String oldGrStatus = "";
    private String oldPaymentStatus = "";
    private String newPaymentStatusId = "";
    private String newGrStatusId = "";
    private String newGrStatusName = "";
    private String newPaymentStatusName = "";
    private ArrayList<ConfigTypesetTypesBean> configTypesetTypesBeen;
    private ArrayList<ConfigTypesetTypesBean> configTypesetDeliveryList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_filter);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.invoice_filter), 0);
        //date filter fragment open
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            oldGrStatus = bundle.getString(EXTRA_INVOICE_GR_STATUS, "");
            oldPaymentStatus = bundle.getString(EXTRA_INVOICE_STATUS, "");
        }
        initUI();
        //other filter type
        filterModel = new InvoiceFilterModelImpl(InvoiceFilterActivity.this, this);
        filterModel.onStart();
    }

    private void initUI() {
        rgStatus = (LinearLayout) findViewById(R.id.rgStatus);
        rgDelvStatus = (LinearLayout) findViewById(R.id.rgDelvStatus);
    }

    @Override
    public void onFragmentInteraction(String startDate, String endDate, String filterType) {
        Intent intent = new Intent();
        intent.putExtra(DateFilterFragment.EXTRA_DEFAULT, filterType);
        intent.putExtra(DateFilterFragment.EXTRA_START_DATE, startDate);
        intent.putExtra(DateFilterFragment.EXTRA_END_DATE, endDate);
        intent.putExtra(EXTRA_INVOICE_STATUS, newPaymentStatusId);
        intent.putExtra(EXTRA_INVOICE_STATUS_NAME, newPaymentStatusName);
        intent.putExtra(EXTRA_INVOICE_GR_STATUS, newGrStatusId);
        intent.putExtra(EXTRA_INVOICE_GR_STATUS_NAME, newGrStatusName);
        setResult(ConstantsUtils.ACTIVITY_RESULT_FILTER, intent);
        finish();
    }

    @Override
    public void displayList(ArrayList<ConfigTypesetTypesBean> configTypesetTypesBeen, ArrayList<ConfigTypesetTypesBean> configTypesetDeliveryList) {
        displayStatusList(InvoiceFilterActivity.this, configTypesetTypesBeen);
        displayOverDueStatusList(InvoiceFilterActivity.this, configTypesetDeliveryList);
    }

   private void displayStatusList(Context mContext, ArrayList<ConfigTypesetTypesBean> configTypesetTypesList) {
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
                   newPaymentStatusId = radioButton.getTag(R.id.id_value).toString();
                   newPaymentStatusName = radioButton.getTag(R.id.name_value).toString();
               }
           }
       });

       rgStatus.addView(radioGroupHeader);
       if (configTypesetTypesList != null) {
           if (configTypesetTypesList.size() > 0) {
               for (int i = 0; i < configTypesetTypesList.size(); i++) {
                   Drawable img;
                   ConfigTypesetTypesBean configTypesetTypesBean = configTypesetTypesList.get(i);
                   RadioButton radioButtonView = (RadioButton) LayoutInflater.from(mContext).inflate(R.layout.radio_button_item, null, false);
                   radioButtonView.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                   radioButtonView.setId(i + 1000);
                   radioButtonView.setTag(R.id.id_value, configTypesetTypesBean.getTypes());
                   radioButtonView.setTag(R.id.name_value, configTypesetTypesBean.getTypesName());
                   radioButtonView.setText(configTypesetTypesBean.getTypesName());
                   img = SOUtils.displayStatusIcon(configTypesetTypesBean.getTypes(), mContext);
                   if (img != null)
                       radioButtonView.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                   radioGroupHeader.addView(radioButtonView);
                   if (oldPaymentStatus.equalsIgnoreCase(configTypesetTypesBean.getTypes())) {
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
                Intent intent = new Intent();
                intent.putExtra(EXTRA_INVOICE_STATUS, newPaymentStatusId);
                intent.putExtra(EXTRA_INVOICE_STATUS_NAME, newPaymentStatusName);
                intent.putExtra(EXTRA_INVOICE_GR_STATUS, newGrStatusId);
                intent.putExtra(EXTRA_INVOICE_GR_STATUS_NAME, newGrStatusName);
                setResult(ConstantsUtils.ACTIVITY_RESULT_FILTER, intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showMessage(String message) {
        ConstantsUtils.displayLongToast(InvoiceFilterActivity.this, message);
    }

    @Override
    public void showProgressDialog() {
        progressDialog = ConstantsUtils.showProgressDialog(InvoiceFilterActivity.this, getString(R.string.app_loading));
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        filterModel.onDestroy();
        super.onDestroy();
    }
    private void displayOverDueStatusList(Context mContext, ArrayList<ConfigTypesetTypesBean> configTypesetTypesList) {
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
                    newGrStatusId = radioButton.getTag(R.id.delv_id_value).toString();
                    newGrStatusName = radioButton.getTag(R.id.delv_name_value).toString();
                }
            }
        });
        rgDelvStatus.addView(radioGroupHeader);
        Drawable img=null;
        if (configTypesetTypesList != null) {
            if (configTypesetTypesList.size() > 0) {
                for (int i = 0; i < configTypesetTypesList.size(); i++) {
                    ConfigTypesetTypesBean configTypesetTypesBean = configTypesetTypesList.get(i);
                    RadioButton radioButtonView = (RadioButton) LayoutInflater.from(mContext).inflate(R.layout.radio_button_item, null, false);
                    radioButtonView.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                    radioButtonView.setId(i + 10000);
                    radioButtonView.setTag(R.id.delv_id_value, configTypesetTypesBean.getTypes());
                    radioButtonView.setTag(R.id.delv_name_value, configTypesetTypesBean.getTypesName());
                    radioButtonView.setText(configTypesetTypesBean.getTypesName());
                    img = SOUtils.displayDueStatusInvoiceFilter(configTypesetTypesBean.getTypes(), mContext);
                    if (img != null)
                        radioButtonView.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                    radioGroupHeader.addView(radioButtonView);
                    if (oldGrStatus.equalsIgnoreCase(configTypesetTypesBean.getTypes())) {
                        radioButtonView.setChecked(true);
                    }
                }
            }
        }
    }

}
