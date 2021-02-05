package com.rspl.sf.msfa.visit;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.ViewPagerTabAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.UpdateListener;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;


/**
 * Created by e10526 on 19-12-2016.
 *
 */
public class MerchindisingListActivity extends AppCompatActivity implements UIListener,UpdateListener {

    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "",mStrBundleCPGUID="";
    private String mStrBundleRetUID = "";

    String concatFlushCollStr="";
    TextView tvRetName = null, tvUID = null;

    MenuItem menu_refresh,menu_sync;
    Menu menu;
    //This is viewPager for merchandising sections device/normal
    private ViewPager viewPager;
    TextView tv_last_sync_time_value,tv_merch_review_header;
    ViewPagerTabAdapter viewPagerAdapter;
    MerchandisingListFragment merchindisingListFragment;
    MerchandisingDeviceListFragment merchindisingDeviceListFragment;

    String concatCollectionStr = "";
    ArrayList<String> alFlushColl=new ArrayList<>();
    ArrayList<String> alAssignColl = new ArrayList<>();
    ProgressDialog syncProgDialog = null;
    boolean dialogCancelled = false;
    boolean mBoolSyncEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
        //ActionBarView.initActionBarView(this, true,getString(R.string.title_snapshot));
        setContentView(R.layout.activity_merchindising_list);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_snapshot), 0);

        initUI();
        setValuesIntoUI();


    }

    // TODO Initialize UI
    private void initUI() {
        tv_merch_review_header= (TextView) findViewById(R.id.tv_merch_review_header);
        tv_last_sync_time_value = (TextView)findViewById(R.id.tv_last_sync_time_value);
        tvRetName = (TextView) findViewById(R.id.tv_reatiler_name);
        tvUID = (TextView) findViewById(R.id.tv_reatiler_id);

        tabInitialize();
    }

    // TODO set values to UI
    private void setValuesIntoUI(){
        tv_merch_review_header.setText(getString(R.string.title_snapshot));
        tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.MerchReviews, Constants.TimeStamp,this));
        tvRetName.setText(mStrBundleRetName);
        tvUID.setText(mStrBundleRetUID);
        if( menu_refresh!=null && menu_sync!=null){
            menu_sync.setVisible(false);
            menu_refresh.setVisible(true);
        }
    }


    /*Initialize tab for Merchandising*/
    private  void tabInitialize(){

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        tabLayout.setupWithViewPager(viewPager);
    }


    /*Setting up ViewPager for Tabs*/
    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerTabAdapter(getSupportFragmentManager());

        merchindisingListFragment = new MerchandisingListFragment();
        merchindisingDeviceListFragment = new MerchandisingDeviceListFragment();

        Bundle bundleVisit = new Bundle();
        bundleVisit.putString(Constants.CPGUID, mStrBundleCPGUID);
        bundleVisit.putString(Constants.CPNo, mStrBundleRetID);
        bundleVisit.putString(Constants.RetailerName, mStrBundleRetName);
        bundleVisit.putString(Constants.CPUID, mStrBundleRetUID);

        merchindisingListFragment.setArguments(bundleVisit);

        merchindisingDeviceListFragment.setArguments(bundleVisit);

        viewPagerAdapter.addFrag(merchindisingListFragment, Constants.History);
        viewPagerAdapter.addFrag(merchindisingDeviceListFragment, Constants.PendingSync);
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (menu_refresh != null && menu_sync != null) {
                    if (position == 0) {
                        menu_sync.setVisible(false);
                        menu_refresh.setVisible(true);
                    } else if (position == 1) {
                        menu_sync.setVisible(true);
                        menu_refresh.setVisible(false);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }





    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_coll_his_list, menu);

        menu_refresh = menu.findItem(R.id.menu_refresh_coll);
        menu_sync = menu.findItem(R.id.menu_sync_coll);

        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_coll_his_list, menu);
        menu_refresh = menu.findItem(R.id.menu_refresh_coll);
        menu_sync = menu.findItem(R.id.menu_sync_coll);
        menu_refresh.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case R.id.menu_sync_coll:
                mBoolSyncEnabled = true;
                    onSync();
                break;
            case R.id.menu_refresh_coll:
                mBoolSyncEnabled = false;
                onRefresh();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }

    // ToDO Posting pending merchandising from device to ECC Server
    private void onSync() {
        try {
            if(OfflineManager.offlineStore.getRequestQueueIsEmpty() && merchindisingDeviceListFragment.pendingMerVal ==0)
            {
                UtilConstants.showAlert(getString(R.string.no_req_to_update_merchant), MerchindisingListActivity.this);
            } else {
                getRefreshList();
                if(merchindisingDeviceListFragment.pendingMerVal >0){

                    if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                        onPostOfflineData();
                    } else {
                        UtilConstants.showAlert(getString(R.string.no_network_conn),MerchindisingListActivity.this);
                    }
                }
            }

        } catch (ODataException e) {
            e.printStackTrace();
        }
    }

    /*
     TODO Assign collections to array list.
     */
    private void getRefreshList() {
        alAssignColl.clear();
        alFlushColl.clear();
        concatCollectionStr="";
        concatFlushCollStr = "";
        try {
            if(OfflineManager.getVisitStatusForCustomer(Constants.MerchReviews + Constants.isLocalFilterQry)){
                alAssignColl.add(Constants.MerchReviews);
                alAssignColl.add(Constants.MerchReviewImages);
                alFlushColl.add(Constants.MerchReviews);
            }

            if(OfflineManager.getVisitStatusForCustomer(Constants.VisitActivities + Constants.isLocalFilterQry)){
                alAssignColl.add(Constants.VisitActivities);
                alFlushColl.add(Constants.VisitActivities);
            }


        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }
    // Todo start async task
    private void onPostOfflineData(){
        Constants.isSync = true;
        try {
            new AsyncPostOfflineData().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // TODO Display progress dialog
    private void displayProgressDialog(){
        try {
            syncProgDialog
                    .show();
            syncProgDialog
                    .setCancelable(true);
            syncProgDialog
                    .setCanceledOnTouchOutside(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialogCancelled = false;
    }
    // TODO Post Pending Merchandisng data to server using async task
    public class AsyncPostOfflineData extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(MerchindisingListActivity.this, R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.updating_data_plz_wait));
            syncProgDialog.setCancelable(false);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();
            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    MerchindisingListActivity.this, R.style.MyTheme);
                            builder.setMessage(R.string.do_want_cancel_sync)
                                    .setCancelable(false)
                                    .setPositiveButton(
                                            R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {
                                                    dialogCancelled = true;
                                                    onBackPressed();
                                                }
                                            })
                                    .setNegativeButton(
                                            R.string.no,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {

                                                    displayProgressDialog();

                                                }
                                            });
                            builder.show();
                        }
                    });


        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);

                for(int incVal=0;incVal<alFlushColl.size();incVal++){
                    if (incVal == 0 && incVal == alFlushColl.size() - 1) {
                        concatFlushCollStr = concatFlushCollStr + alFlushColl.get(incVal);
                    } else if (incVal == 0) {
                        concatFlushCollStr = concatFlushCollStr + alFlushColl.get(incVal)+", ";
                    }else if(incVal == alFlushColl.size() - 1){
                        concatFlushCollStr = concatFlushCollStr + alFlushColl.get(incVal);
                    }else{
                        concatFlushCollStr = concatFlushCollStr + alFlushColl.get(incVal)+", ";
                    }
                }

                try{
                    if(!OfflineManager.offlineStore.getRequestQueueIsEmpty() ){
                        try {
                            dialogCancelled = false;
                            OfflineManager.flushQueuedRequests(MerchindisingListActivity.this,concatFlushCollStr);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (ODataException e) {
                    e.printStackTrace();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    // TODO make array list values to string
    private void assignArryListString(){
        concatCollectionStr="";
        for (int incVal = 0; incVal < alAssignColl.size(); incVal++) {
            if (incVal == 0 && incVal == alAssignColl.size() - 1) {
                concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
            } else if (incVal == 0) {
                concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
            } else if (incVal == alAssignColl.size() - 1) {
                concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
            } else {
                concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
            }
        }
    }

    /*refresh Merchindising list*/
    void onRefresh()
    {

        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {

            alAssignColl.clear();
            alAssignColl.add(Constants.MerchReviewImages);
            alAssignColl.add(Constants.MerchReviews);
            assignArryListString();
                try {
                    Constants.isSync = true;
                    dialogCancelled = false;
                    new LoadingData().execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        } else {
            UtilConstants.showAlert(getString(R.string.no_network_conn),MerchindisingListActivity.this);
        }

    }



    /*AsyncTask for refresh merchandising*/
    public class LoadingData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(MerchindisingListActivity.this, R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
            syncProgDialog.setCancelable(true);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();

            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    MerchindisingListActivity.this, R.style.MyTheme);
                            builder.setMessage(R.string.do_want_cancel_sync)
                                    .setCancelable(false)
                                    .setPositiveButton(
                                            R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {
                                                    dialogCancelled = true;

                                                    onBackPressed();
                                                }
                                            })
                                    .setNegativeButton(
                                            R.string.no,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {

                                                    try {
                                                        syncProgDialog
                                                                .show();
                                                        syncProgDialog
                                                                .setCancelable(true);
                                                        syncProgDialog
                                                                .setCanceledOnTouchOutside(false);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    dialogCancelled = false;

                                                }
                                            });
                            builder.show();
                        }
                    });
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                try {

                    OfflineManager.refreshStoreSync(getApplicationContext(), MerchindisingListActivity.this, Constants.Fresh, concatCollectionStr);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    @Override
    public void onRequestError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e, MerchindisingListActivity.this);
        if (errorBean.hasNoError()) {
        if (dialogCancelled == false ) {
            if (operation == Operation.OfflineFlush.getValue() ) {
                closingProgressDialog();
                UtilConstants.showAlert(getString(R.string.msg_error_occured_during_sync),MerchindisingListActivity.this);
            }else if (operation == Operation.OfflineRefresh.getValue()) {
               closingProgressDialog();
              UtilConstants.showAlert(getString(R.string.msg_error_occured_during_sync),MerchindisingListActivity.this);
            }
        }
        } else {
            Constants.isSync = false;
            closingProgressDialog();
            Constants.displayMsgReqError(errorBean.getErrorCode(), MerchindisingListActivity.this);
        }
    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {
        if (dialogCancelled == false ) {

            if (operation == Operation.OfflineFlush.getValue() ) {
                assignArryListString();
                try {
                    OfflineManager.refreshStoreSync(getApplicationContext(), MerchindisingListActivity.this, Constants.Fresh, concatCollectionStr);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }else if (operation == Operation.OfflineRefresh.getValue()) {


              // Constants.updateLastSyncTimeToTable(alAssignColl,this,Constants.DownLoad);


                tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.MerchReviews, Constants.TimeStamp,this));


                closingProgressDialog();

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            MerchindisingListActivity.this, R.style.MyTheme);
                    builder.setMessage(getString(R.string.msg_sync_successfully_completed))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {

                                            if(!mBoolSyncEnabled) {
                                                getRefreshFragment(0);
                                            }else {
                                                getRefreshFragment(1);
                                            }

                                        }
                                    });

                    builder.show();

            }
        }
    }

    private void closingProgressDialog(){
        try {
            syncProgDialog.dismiss();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*refresh pager and fragments*/
    public void getRefreshFragment(int position){

        viewPagerAdapter = new ViewPagerTabAdapter(getSupportFragmentManager());
        merchindisingListFragment = new MerchandisingListFragment();
        merchindisingDeviceListFragment = new MerchandisingDeviceListFragment();


        Bundle bundleVisit = new Bundle();
        bundleVisit.putString(Constants.CPGUID, mStrBundleCPGUID);
        bundleVisit.putString(Constants.CPNo, mStrBundleRetID);
        bundleVisit.putString(Constants.RetailerName, mStrBundleRetName);
        bundleVisit.putString(Constants.CPUID, mStrBundleRetUID);
        merchindisingListFragment.setArguments(bundleVisit);
        merchindisingDeviceListFragment.setArguments(bundleVisit);

        viewPagerAdapter.addFrag(merchindisingListFragment, Constants.Collections);
        viewPagerAdapter.addFrag(merchindisingDeviceListFragment, Constants.DeviceCollectionsText);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onUpdate() {
        getRefreshFragment(1);
    }
}
