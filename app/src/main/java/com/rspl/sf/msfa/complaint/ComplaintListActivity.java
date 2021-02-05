package com.rspl.sf.msfa.complaint;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;

public class ComplaintListActivity extends AppCompatActivity {

    private ListView lvComplaint;
    private EditText edSearch;
    private TextView tvEmpty;
    ArrayList<ComplaintBean> complaintBeanArrayList= new ArrayList<>();
    private String mStrBundleRetName="";
    private String mStrBundleRetID="";
    private String mStrCPGUID="";
    private ComplaintsAdapter complaintAdapter;
    private TextView retName;
    private TextView retUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_list);
       // ActionBarView.initActionBarView(this, true,getString(R.string.complaint_list));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.complaint_list), 0);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            mStrBundleRetID = bundle.getString(Constants.CPNo);
            mStrCPGUID = bundle.getString(Constants.CPGUID);
            mStrBundleRetName = bundle.getString(Constants.RetailerName);
        }
        retName = (TextView) findViewById(R.id.tv_reatiler_name);
        retUid = (TextView) findViewById(R.id.tv_reatiler_id);
        retName.setText(mStrBundleRetName);
        retUid.setText(mStrBundleRetID);

        lvComplaint= (ListView)findViewById(R.id.lv_complaint);
        edSearch= (EditText)findViewById(R.id.edSearch);
        tvEmpty= (TextView)findViewById(R.id.tv_empty_lay);
        getFeedBackList();
    }

    private void getFeedBackList() {
        try {
            complaintBeanArrayList = OfflineManager.getComplaintList(Constants.Complaints);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        complaintAdapter = new ComplaintsAdapter(ComplaintListActivity.this, complaintBeanArrayList,tvEmpty, mStrBundleRetID, mStrBundleRetName);
        lvComplaint.setAdapter(complaintAdapter);
        complaintAdapter.notifyDataSetChanged();

        if (complaintBeanArrayList != null && complaintBeanArrayList.size() > 0) {
            lvComplaint.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }else {
            lvComplaint.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ComplaintListActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_competition_information).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        ComplaintListActivity.this.finish();
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
