package com.rspl.sf.msfa.mtp.subordinate.mtpfilter;

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
import android.widget.TextView;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;

import java.util.ArrayList;

/**
 * Created by e10860 on 12/2/2017.
 */

public class MTPSubOrdinateFilterActivity extends AppCompatActivity{

    public static final String EXTRA_INVOICE_STATUS = "extraInvoiceStatus";
    public static final String EXTRA_INVOICE_STATUS_NAME = "extraInvoiceStatusName";
    public static final String EXTRA_INVOICE_GR_STATUS = "extraInvoiceGrStatus";
    public static final String EXTRA_INVOICE_GR_STATUS_NAME = "extraInvoiceGrStatusName";

    private Toolbar toolbar;
    private ProgressDialog progressDialog = null;
    private LinearLayout rgStatus;
    private TextView tv_inv_status_lbl;
    private LinearLayout rgDelvStatus;
    private String Status_ID = "";
    private String oldPaymentStatus = "";
    private String newPaymentStatusId = "";
    private String newGrStatusId = "";
    private String newGrStatusName = "";
    private String newPaymentStatusName = "";
    private ArrayList<String> filterList = new ArrayList<>();



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mtp_sub_filter);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, "Role Filter", 0);
        //date filter fragment open
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Status_ID = bundle.getString(Constants.Status_ID, "");
            filterList = bundle.getStringArrayList(Constants.FilterList);
        }
        initUI();
    }

    private void initUI() {
        rgStatus = (LinearLayout) findViewById(R.id.rgStatus);
        tv_inv_status_lbl = (TextView) findViewById(R.id.tv_inv_status_lbl);
        displayStatusList(this,filterList);
//        tv_inv_status_lbl.setText(getString(R.string.outstanding_status));
//        rgDelvStatus = (LinearLayout) findViewById(R.id.rgDelvStatus);
    }

    public void onFragmentInteraction(String startDate, String endDate, String filterType) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_INVOICE_STATUS, newPaymentStatusId);
        intent.putExtra(EXTRA_INVOICE_STATUS_NAME, newPaymentStatusName);
        intent.putExtra(EXTRA_INVOICE_GR_STATUS, newGrStatusId);
        intent.putExtra(EXTRA_INVOICE_GR_STATUS_NAME, newGrStatusName);
        setResult(ConstantsUtils.ACTIVITY_RESULT_FILTER, intent);
        finish();
    }

   /* public void displayList(ArrayList<ConfigTypesetTypesBean> configTypesetTypesBeen, ArrayList<ConfigTypesetTypesBean> configTypesetDeliveryList) {
        displayStatusList(OutstandingFilterActivity.this, configTypesetTypesBeen);
//        displayOverDueStatusList(OutstandingFilterActivity.this, configTypesetDeliveryList);
    }*/

   private void displayStatusList(Context mContext, ArrayList<String> configTypesetTypesList) {
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
//                   newPaymentStatusId = radioButton.getTag(R.id.id_value).toString();
                   Status_ID = radioButton.getTag(R.id.name_value).toString();
                   /*if(Status_ID.equalsIgnoreCase("All")){
                       Status_ID = "";
                   }*/
               }
           }
       });

       rgStatus.addView(radioGroupHeader);
       if (configTypesetTypesList != null) {
           if (configTypesetTypesList.size() > 0) {
               for (int i = 0; i < configTypesetTypesList.size(); i++) {
                   Drawable img;
                   String status = configTypesetTypesList.get(i);
                   RadioButton radioButtonView = (RadioButton) LayoutInflater.from(mContext).inflate(R.layout.radio_button_item, null, false);
                   radioButtonView.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                   radioButtonView.setId(i + 1000);
//                   radioButtonView.setTag(R.id.id_value, status);
                   radioButtonView.setTag(R.id.name_value, status);
                   radioButtonView.setText(status);
                   if(status.equalsIgnoreCase("All")){
                       status = "";
                   }
                   /*img = SOUtils.displayStatusIcon(configTypesetTypesBean.getTypes(), mContext);
                   if (img != null)
                       radioButtonView.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);*/
                   radioGroupHeader.addView(radioButtonView);
                   if (Status_ID.equalsIgnoreCase(status)) {
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
                intent.putExtra(Constants.Status_ID, Status_ID);
                setResult(ConstantsUtils.ACTIVITY_RESULT_FILTER, intent);
                finish();
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
