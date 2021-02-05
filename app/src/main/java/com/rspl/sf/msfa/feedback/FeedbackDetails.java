package com.rspl.sf.msfa.feedback;

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

public class FeedbackDetails extends AppCompatActivity {
    TextView tv_invoice_document_number;
    TextView retName, retUid;
    boolean flag = true;
    private String mStrBundleRetName = "", mStrBundleRetID = "";
    private String mStrBundleFeedbackNo = "", mStrBtsID = "";
    private String mStrBundleFeedbackGuid = "", mStrStatus = "", mStrFeedBackDesc = "",
            mStrRemarks = "", mStrLocation = "",
            mStrBundleDeviceStatus = "", mStrDeviceNo = "";
    private LinearLayout llDetailLayout;
    private String feedbackGuid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        //ActionBarView.initActionBarView(this, true, getString(R.string.title_feed_back_details));

        setContentView(R.layout.activity_feedback_details);
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_feed_back_details), 0);

        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleFeedbackNo = bundleExtras.getString(Constants.FeedbackNo);
            mStrBundleFeedbackGuid = bundleExtras.getString(Constants.FeedBackGuid);
            mStrFeedBackDesc = bundleExtras.getString(Constants.FeedbackDesc);
            mStrBtsID = bundleExtras.getString(Constants.BTSID);
            mStrLocation = bundleExtras.getString(Constants.Location);
            mStrRemarks = bundleExtras.getString(Constants.Remarks);
            mStrBundleDeviceStatus = bundleExtras.getString(Constants.DeviceStatus);
            mStrDeviceNo = bundleExtras.getString(Constants.DeviceNo);

        }
//        if (!Constants.restartApp(FeedbackDetails.this)) {
        initUI();
//        }
    }

    /*Initializes UI*/
    void initUI() {
        retName = (TextView) findViewById(R.id.tv_reatiler_name);
        retUid = (TextView) findViewById(R.id.tv_reatiler_id);
        retName.setText(mStrBundleRetName);
        retUid.setText(mStrBundleRetID);

        tv_invoice_document_number = (TextView) findViewById(R.id.tv_invoice_document_number);

        ImageView invStatus = (ImageView) findViewById(R.id.tv_in_history_status);
        tv_invoice_document_number.setText(mStrBundleFeedbackNo);


        invStatus.setImageDrawable(null);

        String store = null;
//        try {
//            store = LogonCore.getInstance().getObjectFromStore(mStrDeviceNo);
//        } catch (LogonCoreException e) {
//            e.printStackTrace();
//        }
//        try {
//            JSONObject fetchJsonHeaderObject = new JSONObject(store);
//            ArrayList<HashMap<String, String>> arrtable = new ArrayList<>();
//
//            feedbackGuid = fetchJsonHeaderObject.getString(Constants.FeebackGUID);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        displayValues();
    }

    /*Display feedback details*/
    private void displayValues() {
        if (!flag) {
            llDetailLayout.removeAllViews();
        }
        flag = false;
        llDetailLayout = (LinearLayout) findViewById(R.id.ll_invoice_detail_list);


        TableLayout table = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_table, null);

        TableRow trFeedbackDesc = (TableRow) LayoutInflater.from(this).inflate(
                R.layout.item_row, null);
        ((TextView) trFeedbackDesc.findViewById(R.id.item_lable)).setText(getString(R.string.lbl_feed_back_desc));
        ((TextView) trFeedbackDesc.findViewById(R.id.item_blank)).setText(getString(R.string.lbl_semi_colon));
        ((TextView) trFeedbackDesc.findViewById(R.id.item_value)).setText(mStrFeedBackDesc);
        table.addView(trFeedbackDesc);

        TableRow trRemarks = (TableRow) LayoutInflater.from(this).inflate(
                R.layout.item_row, null);
        ((TextView) trRemarks.findViewById(R.id.item_lable))
                .setText(getString(R.string.lbl_remarks));
        ((TextView) trRemarks.findViewById(R.id.item_blank)).setText(getString(R.string.lbl_semi_colon));
        ((TextView) trRemarks.findViewById(R.id.item_value)).setText(mStrRemarks);
        table.addView(trRemarks);


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
        AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackDetails.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_competition_information).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        FeedbackDetails.this.finish();
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
