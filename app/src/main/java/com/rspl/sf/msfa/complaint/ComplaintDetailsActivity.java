package com.rspl.sf.msfa.complaint;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;

public class ComplaintDetailsActivity extends AppCompatActivity {

    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "";
    private TextView retName;
    private TextView retUid;
    private TextView tv_invoice_document_number;
    private ComplaintBean extraComplaintBean = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_details);
        //ActionBarView.initActionBarView(this, true, getString(R.string.complaint_details));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.complaint_details), 0);
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            extraComplaintBean = (ComplaintBean) bundleExtras.getSerializable(Constants.EXTRA_COMPLAINT_BEAN);
        }
        initUI();
    }

    private void initUI() {
        retName = (TextView) findViewById(R.id.tv_reatiler_name);
        retUid = (TextView) findViewById(R.id.tv_reatiler_id);
        retName.setText(mStrBundleRetName);
        retUid.setText(mStrBundleRetID);

        tv_invoice_document_number = (TextView) findViewById(R.id.tv_invoice_document_number);

        ImageView invStatus = (ImageView) findViewById(R.id.tv_in_history_status);



        invStatus.setImageDrawable(null);
        if (extraComplaintBean != null) {
            displayValues();
            tv_invoice_document_number.setText(extraComplaintBean.getComplaintId());
        }
    }

    /*Display feedback details*/
    private void displayValues() {

        LinearLayout llDetailLayout = (LinearLayout) findViewById(R.id.ll_invoice_detail_list);


        TableLayout table = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_table, null);

        TableRow tableRow = (TableRow) LayoutInflater.from(this).inflate(R.layout.item_row, null);
        ((TextView) tableRow.findViewById(R.id.item_lable)).setText(getString(R.string.lbl_complaint_category));
        ((TextView) tableRow.findViewById(R.id.item_blank)).setText(getString(R.string.lbl_semi_colon));
        ((TextView) tableRow.findViewById(R.id.item_value)).setText(extraComplaintBean.getComplaintCategory());
        table.addView(tableRow);

        tableRow = (TableRow) LayoutInflater.from(this).inflate(R.layout.item_row, null);
        ((TextView) tableRow.findViewById(R.id.item_lable)).setText(getString(R.string.lbl_complaint));
        ((TextView) tableRow.findViewById(R.id.item_blank)).setText(getString(R.string.lbl_semi_colon));
        ((TextView) tableRow.findViewById(R.id.item_value)).setText(extraComplaintBean.getComplaint());
        table.addView(tableRow);

        if (extraComplaintBean.getComplaintCategory().equalsIgnoreCase("Product")) {
            tableRow = (TableRow) LayoutInflater.from(this).inflate(R.layout.item_row, null);
            ((TextView) tableRow.findViewById(R.id.item_lable)).setText(getString(R.string.lbl_details_mat_desc));
            ((TextView) tableRow.findViewById(R.id.item_blank)).setText(getString(R.string.lbl_semi_colon));
            ((TextView) tableRow.findViewById(R.id.item_value)).setText(extraComplaintBean.getMatDescription());
            table.addView(tableRow);

            tableRow = (TableRow) LayoutInflater.from(this).inflate(R.layout.item_row, null);
            ((TextView) tableRow.findViewById(R.id.item_lable)).setText(getString(R.string.lbl_quantity));
            ((TextView) tableRow.findViewById(R.id.item_blank)).setText(getString(R.string.lbl_semi_colon));
            ((TextView) tableRow.findViewById(R.id.item_value)).setText(extraComplaintBean.getQuantity());
            table.addView(tableRow);

            tableRow = (TableRow) LayoutInflater.from(this).inflate(R.layout.item_row, null);
            ((TextView) tableRow.findViewById(R.id.item_lable)).setText(getString(R.string.lbl_batch_number));
            ((TextView) tableRow.findViewById(R.id.item_blank)).setText(getString(R.string.lbl_semi_colon));
            ((TextView) tableRow.findViewById(R.id.item_value)).setText(extraComplaintBean.getBatchNo());
            table.addView(tableRow);

            tableRow = (TableRow) LayoutInflater.from(this).inflate(R.layout.item_row, null);
            ((TextView) tableRow.findViewById(R.id.item_lable)).setText(getString(R.string.lbl_mfd));
            ((TextView) tableRow.findViewById(R.id.item_blank)).setText(getString(R.string.lbl_semi_colon));
            ((TextView) tableRow.findViewById(R.id.item_value)).setText(extraComplaintBean.getMdf());
            table.addView(tableRow);
        }

        tableRow = (TableRow) LayoutInflater.from(this).inflate(R.layout.item_row, null);
        ((TextView) tableRow.findViewById(R.id.item_lable)).setText(getString(R.string.lbl_remarks));
        ((TextView) tableRow.findViewById(R.id.item_blank)).setText(getString(R.string.lbl_semi_colon));
        ((TextView) tableRow.findViewById(R.id.item_value)).setText(extraComplaintBean.getRemarks());
        table.addView(tableRow);


        llDetailLayout.addView(table);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ComplaintDetailsActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_competition_information).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        ComplaintDetailsActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();
    }
}
