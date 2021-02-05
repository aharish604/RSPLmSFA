package com.rspl.sf.msfa.visit;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.CompetitorMasterReviewRecyclerViewAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.CompetitorMasterBean;

import java.util.ArrayList;

public class CompetitorMasterReviewActivity extends AppCompatActivity {

    private String customerNum = "", customerName = "", customerNo = "", MobNum ="",address="";
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "", mStrComingFrom = "";
    RecyclerView recyclerViewCompetitor;
    TextView textViewNoRecordFound;
    private int dataAddedCount = 0;
    ArrayList<CompetitorMasterBean> competitorInfoBeanArrayList,competitorBeanArrayList;
    CompetitorMasterReviewRecyclerViewAdapter adapter;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competitor_master_review);

       // ActionBarView.initActionBarView(this, true, getString(R.string.competitor_master_review));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.competitor_master_review), 0);

        initializeUI();
    }
    private void initializeUI(){

        recyclerViewCompetitor =(RecyclerView)findViewById(R.id.recyclerViewCompetitor);
        textViewNoRecordFound=(TextView)findViewById(R.id.textViewNoRecordFound);

        competitorBeanArrayList= new ArrayList<>();
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            customerNo = extra.getString(Constants.Customer);
            customerName = extra.getString(Constants.CustomerName);
            competitorInfoBeanArrayList= (ArrayList<CompetitorMasterBean>) extra.get(Constants.CompetitorMasterInfo);
        }
        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);
        retName.setText(customerName);
        retId.setText(customerNo);
        recyclerViewCompetitor.setHasFixedSize(true);
        recyclerViewCompetitor.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        initializeRecyclerViewItems();
    }
    private void initializeRecyclerViewItems(){
        for (int materialCustomers = 0; materialCustomers <competitorInfoBeanArrayList.size(); materialCustomers++) {
            CompetitorMasterBean competitorMasterBean = new CompetitorMasterBean();
            competitorMasterBean.setMaterialDesc(competitorInfoBeanArrayList.get(materialCustomers).getMaterialDesc());
            competitorMasterBean.setQuantityInputText(competitorInfoBeanArrayList.get(materialCustomers).getQuantityInputText());
            competitorMasterBean.setUOM(competitorInfoBeanArrayList.get(materialCustomers).getUOM());
            competitorMasterBean.setStockType(competitorInfoBeanArrayList.get(materialCustomers).isStockType());
            competitorMasterBean.setMaterialNo(competitorInfoBeanArrayList.get(materialCustomers).getMaterialNo());
            competitorMasterBean.setEtag(competitorInfoBeanArrayList.get(materialCustomers).getEtag());
            competitorMasterBean.setCompName(competitorInfoBeanArrayList.get(materialCustomers).getCompName());
            competitorBeanArrayList.add(competitorMasterBean);
        }
        adapter = new CompetitorMasterReviewRecyclerViewAdapter(getApplicationContext(),competitorBeanArrayList);
        recyclerViewCompetitor.setAdapter(adapter);

    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CompetitorMasterReviewActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_competition_information).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        CompetitorMasterReviewActivity.this.finish();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_price_info_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_save:
//                new StocksInfoReviewActivity.SaveStocksToUDBAsyncTask().execute();
                UtilConstants.dialogBoxWithCallBack(CompetitorMasterReviewActivity.this, "", getString(R.string.quanity_saved_successfully), getString(R.string.ok), "", false, new DialogCallBack() {
                    @Override
                    public void clickedStatus(boolean b) {
                        setResult(Constants.NAVIGATE_TO_PARENT_ACTIVITY,null);
                        finish();
                    }
                });
                break;

        }
        return true;
    }
}
