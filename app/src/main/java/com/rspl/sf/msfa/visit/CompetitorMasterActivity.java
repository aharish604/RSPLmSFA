package com.rspl.sf.msfa.visit;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.CompetitorMasterRecyclerViewAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.TextWatcherInterface;
import com.rspl.sf.msfa.mbo.CompetitorMasterBean;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;
import java.util.Calendar;

public class CompetitorMasterActivity extends AppCompatActivity implements TextWatcherInterface {
    private String customerNum = "", customerName = "", customerNo = "", MobNum ="",address="";
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    String mStrComingFrom = "";

    private String dateSelected = "";
    private int mYear;
    private int mMonth;
    private int mDay;
    EditText editTextCompetitorSearch;
    RecyclerView recyclerViewCompetitor;
    ArrayList<CompetitorMasterBean>competitorInfoBeanArrayList,competitorBeanArrayList,passToReviewActvitityArraylist;
    ArrayList<String>competitorNameList;
    Spinner spinnerCompetitorMaster;
    TextView textViewNoRecordFound,textViewRetailerName,textViewRetailerNumber;
    ProgressDialog progressDialog;
    CompetitorMasterRecyclerViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competitor_master);

        //ActionBarView.initActionBarView(this, true, getString(R.string.competitor_master));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.competitor_master), 0);
        initializeUI();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_competitor_master, menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CompetitorMasterActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_competition_information).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_review:
                if (validateQuantity()) {
                    Intent  intent = new Intent(CompetitorMasterActivity.this, CompetitorMasterReviewActivity.class);
                    intent.putExtra(Constants.CustomerName,customerName);
                    intent.putExtra(Constants.Customer,customerNo);
                    intent.putExtra(Constants.CompetitorMasterInfo,passToReviewActvitityArraylist);
                    startActivityForResult(intent, Constants.NAVIGATE_TO_CHILD_ACTIVITY);
                } else {
                    UtilConstants.showAlert(getString(R.string.validation_plz_enter_qty),CompetitorMasterActivity.this);
                }
                break;
        }
        return true;
    }

    /**
     * initiliazing UI components
     */
    private void initializeUI(){

        competitorNameList = new ArrayList<>();
        competitorBeanArrayList = new ArrayList<>();
        competitorNameList.add(0,"ALL");
        editTextCompetitorSearch=(EditText)findViewById(R.id.editTextCompetitorSearch);
        textViewNoRecordFound=(TextView) findViewById(R.id.textViewNoRecordFound);
        textViewRetailerNumber=(TextView) findViewById(R.id.textViewRetailerNumber);
        textViewRetailerName=(TextView) findViewById(R.id.textViewRetailerName);
        spinnerCompetitorMaster=(Spinner) findViewById(R.id.spinnerCompetitorMaster);
        recyclerViewCompetitor =(RecyclerView)findViewById(R.id.recyclerViewCompetitor);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            customerNo = extra.getString(Constants.CPNo);
            customerName = extra.getString(Constants.RetailerName);
            MobNum = extra.getString(Constants.SalesPersonMobileNo);
            address = extra.getString(Constants.Address);
            mStrBundleRetID = extra.getString(Constants.CPNo);
            mStrBundleRetName = extra.getString(Constants.RetailerName);
            mStrComingFrom = extra.getString(Constants.comingFrom);

        }
        textViewRetailerName.setText(customerName);
        textViewRetailerNumber.setText(customerNo);
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
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
        dateSelected = mYear + "-" + mon + "-" + day;
        new CompetitorMasterAsyncTask().execute();
        editTextCompetitorSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString(),competitorInfoBeanArrayList,competitorBeanArrayList,1);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * populate competitor spinner list
     */
    private void initializeSpinnerItems(){
        ArrayAdapter<String>spinnerCompetitorAdapter = new ArrayAdapter<>(CompetitorMasterActivity.this,R.layout.custom_textview,competitorNameList);
        spinnerCompetitorAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spinnerCompetitorMaster.setAdapter(spinnerCompetitorAdapter);
        spinnerCompetitorMaster.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                assert competitorBeanArrayList != null;
                if (competitorBeanArrayList.size()>0)
                    competitorBeanArrayList.clear();
                if (position==0&&competitorNameList.get(position).equals("ALL")){
                    initializeRecyclerViewItems();
                }else{
                    competitorBeanArrayList.clear();
                    for (int i = 0; i <competitorInfoBeanArrayList.size() ; i++) {
                        CompetitorMasterBean competitorMasterBean = competitorInfoBeanArrayList.get(i);
                        String searchText = competitorMasterBean.getCompName().toLowerCase();
                        if (searchText.matches(competitorNameList.get(position).toLowerCase())){
                            competitorBeanArrayList.add(competitorMasterBean);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /**
     * initializing competitor master recyclerView
     */
    private void initializeRecyclerViewItems(){
        recyclerViewCompetitor.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.scrollToPosition(0);
        recyclerViewCompetitor.setLayoutManager(linearLayoutManager);
        for (int i = 0; i <competitorInfoBeanArrayList.size(); i++) {
            CompetitorMasterBean competitorMasterBean = new CompetitorMasterBean();
            competitorMasterBean.setCompName(competitorInfoBeanArrayList.get(i).getCompName());
            competitorMasterBean.setCompGUID(competitorInfoBeanArrayList.get(i).getCompGUID());
            competitorBeanArrayList.add(competitorMasterBean);
        }
        adapter = new CompetitorMasterRecyclerViewAdapter(getApplicationContext(),competitorBeanArrayList);
//      adapter.setOnRecyclerViewClickListener(this);
        adapter.textWatcher(this);
        recyclerViewCompetitor.setAdapter(adapter);
//
    }

    @Override
    public void textChane(String charSequence, int position) {

    }

    /**
     * fetching offline data from UDB
     */
    public class CompetitorMasterAsyncTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CompetitorMasterActivity.this, R.style.ProgressDialogTheme);
            progressDialog.setMessage(getString(R.string.app_loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                competitorInfoBeanArrayList = OfflineManager.getCompetitorMasterList(Constants.CompetitorMasters);
                for (int i = 0; i <competitorInfoBeanArrayList.size() ; i++) {
                    competitorNameList.add(competitorInfoBeanArrayList.get(i).getCompName());
                }
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            initializeSpinnerItems();
            progressDialog.dismiss();
        }
    }

    /**
     * validate quantity to save data
     * @return
     */
    private boolean validateQuantity(){
        passToReviewActvitityArraylist = new ArrayList<>();
        for (int i = 0; i <competitorBeanArrayList.size() ; i++) {
            CompetitorMasterBean competitorMasterBean = competitorBeanArrayList.get(i);
            if (!competitorMasterBean.getQuantityInputText().trim().equalsIgnoreCase("")){
                passToReviewActvitityArraylist.add(competitorMasterBean);
            }
        }
        if (passToReviewActvitityArraylist.size()<=0){
            return false;
        }else {
            return true;
        }
    }
    /**
     * filtering the data by material description
     * @param search
     */
    void filter(String search,ArrayList<CompetitorMasterBean>infoBeanArrayList,ArrayList<CompetitorMasterBean> beanArrayList,int code){
        beanArrayList.clear();
        for (int i = 0; i <infoBeanArrayList.size() ; i++) {
            CompetitorMasterBean competitorMasterBean = infoBeanArrayList.get(i);
            String searchText = competitorMasterBean.getCompName().toLowerCase();
            if (searchText.contains(search)){
                beanArrayList.add(competitorMasterBean);
            }
        }
        if (code==1) {
            adapter.notifyDataSetChanged();
            if (beanArrayList.size() > 0) {
                if (textViewNoRecordFound != null)
                    textViewNoRecordFound.setVisibility(View.GONE);
            } else {
                if (textViewNoRecordFound != null)
                    textViewNoRecordFound.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Constants.NAVIGATE_TO_CHILD_ACTIVITY:
                if (resultCode==Constants.NAVIGATE_TO_PARENT_ACTIVITY){
                    this.finish();
                }
                break;
        }
    }
}
