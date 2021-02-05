package com.rspl.sf.msfa.routeplan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.MapActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.CustomerBean;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;

public class OtherBeatListActivity extends AppCompatActivity implements TextWatcher {
    String  mStrRouteType ="",mStrBeatName="",mStrCustomerType="";
    EditText et_name_search;
    ArrayList<CustomerBean> alRetailerList=new ArrayList<>();
    private OtherBeatListAdapter retailerAdapter = null;
    ListView lv_route_ret_list = null;
    String mStrOtherRouteguid = "";
    TextView tvEmptyLay = null,tvBeatName=null;
    LinearLayout layoutAboveLine = null;

    ProgressDialog pdLoadDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_beat_list);
//        ActionBarView.initActionBarView(this, true,getString(R.string.lbl_other_beats));

        onInitUI();

        LoadingData();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_other_beats), 0);


    }
    private void onInitUI() {
        mStrOtherRouteguid = getIntent().getExtras().getString(Constants.OtherRouteGUID);
        mStrBeatName = getIntent().getExtras().getString(Constants.OtherRouteName);
        mStrRouteType= getIntent().getExtras().getString(Constants.RouteType);
        mStrCustomerType= getIntent().getExtras().getString("CustomerType");

        if(mStrCustomerType == null){

            mStrCustomerType ="";
        }

        TextView tv_retailer_header=(TextView)findViewById(R.id.tv_retailer_header);
        tv_retailer_header.setText(mStrRouteType);

        Constants.OtherRouteNameVal = "";
        Constants.OtherRouteGUIDVal = "";

        et_name_search = (EditText)findViewById(R.id.et_name_search);
        tvEmptyLay = (TextView) findViewById(R.id.tv_empty_lay);
        tvBeatName = (TextView)findViewById(R.id.tv_beat_name);

        lv_route_ret_list = (ListView) findViewById(R.id.lv_route_ret_list);
        et_name_search.addTextChangedListener(this);
        layoutAboveLine = (LinearLayout)findViewById(R.id.ll_edit_text_above_line);
        tvBeatName.setText(getString(R.string.lbl_beat_name)+" "+getString(R.string.str_colon)+" "+mStrBeatName);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        retailerAdapter.getFilter().filter(s); //Filter from my adapter
        retailerAdapter.notifyDataSetChanged();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void LoadingData() {
        try {
            new AsynLoadTodaysBeat().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private class AsynLoadTodaysBeat extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Constants.BoolTodayBeatLoaded = false;
            pdLoadDialog = new ProgressDialog(OtherBeatListActivity.this, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            getRouteList();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pdLoadDialog.dismiss();
            onDisplyTodaysRoute();
        }
    }


    private void getRouteList() {
        try {

                 // String qryForTodaysBeat = Constants.RouteSchedulePlans+ "?$filter=" + Constants.RouteSchGUID+ " eq guid'" + mStrOtherRouteguid.toUpperCase()+"' &$orderby="+ Constants.SequenceNo+"";
            String qryForTodaysBeat = Constants.RouteSchedulePlans+ "?$filter=" + Constants.RouteSchGUID+ " eq guid'" + mStrOtherRouteguid.toUpperCase()+"'&$orderby="+ Constants.SequenceNo+"";

                alRetailerList = OfflineManager.getRetailerListForOtherBeats(qryForTodaysBeat);


        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

    }
    private void onDisplyTodaysRoute() {
        retailerAdapter = new OtherBeatListAdapter(getApplicationContext(), alRetailerList,mStrRouteType, tvEmptyLay,mStrOtherRouteguid,mStrBeatName,mStrCustomerType);

        lv_route_ret_list.setAdapter(retailerAdapter);
        retailerAdapter.notifyDataSetChanged();


        if(alRetailerList!=null && alRetailerList.size()>0) {
            if (alRetailerList.size() < 1) {
                tvEmptyLay.setVisibility(View.VISIBLE);
            } else
                tvEmptyLay.setVisibility(View.GONE);
        }else{
            tvEmptyLay.setVisibility(View.VISIBLE);
        }



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map_back, menu);
//        try {
//            if(!mStrRouteType.equalsIgnoreCase(getString(R.string.lbl_today_beats))){
//                menu.removeItem(R.id.menu_map);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_map:
                displyLocation();
                break;

        }
        return true;
    }
    private void displyLocation() {
        Intent i = new Intent(this,MapActivity.class);
        i.putExtra(Constants.NAVFROM, Constants.BeatPlan);
        i.putExtra(Constants.OtherRouteGUID, mStrOtherRouteguid);
        startActivity(i);
    }
}
