package com.rspl.sf.msfa.reports.salesorder.header;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.ViewPagerTabAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.reports.salesorder.pendingsync.SalesOrderHeaderPendingSyncFragment;

public class SalesOrderHeaderListActivity extends AppCompatActivity {
    ViewPagerTabAdapter viewPagerAdapter;
    ViewPager viewpagerSalesOrderHeader;
    TabLayout tabLayoutSalesOrderHeader;
    SalesOrderHeaderListFragment salesOrderHeaderListFragment=null;
    SalesOrderHeaderPendingSyncFragment salesOrderHeaderListFragmentPendingSync;
    Toolbar toolbar;
    private String TAG=SalesOrderHeaderListActivity.class.getSimpleName();
    public static boolean mBoolRefreshDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order_header_list);
        deleteSOPOstedDataVaultRecords();
        initializeUI();
    }
      private void deleteSOPOstedDataVaultRecords() {
      new Thread(new Runnable() {
          @Override
          public void run() {
              try {
                  Constants.deletePostedSOData(SalesOrderHeaderListActivity.this);
              } catch (Exception e) {
                  e.printStackTrace();
              }
          }
      }).start();
  }
    void initializeUI(){
        viewpagerSalesOrderHeader=(ViewPager)findViewById(R.id.viewpagerSalesOrderHeader);
        tabLayoutSalesOrderHeader=(TabLayout)findViewById(R.id.tabLayoutSalesOrderHeader);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_sales_order),0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initializeTabLayout();
    }
    private void initializeTabLayout(){
        setupViewPager(viewpagerSalesOrderHeader);
        tabLayoutSalesOrderHeader.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        tabLayoutSalesOrderHeader.setupWithViewPager(viewpagerSalesOrderHeader);
        viewpagerSalesOrderHeader.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
           @Override
           public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
           }
           @Override
           public void onPageSelected(int position) {
               Log.d(TAG, "onPageSelected: "+position);
               if(position==0){
                   if(salesOrderHeaderListFragment!=null && mBoolRefreshDone) {
                       mBoolRefreshDone=false;
                       salesOrderHeaderListFragment.onRefreshView();
                   }
               }
           }
           @Override
           public void onPageScrollStateChanged(int state) {

           }
       });
    }
    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerTabAdapter(getSupportFragmentManager());
        salesOrderHeaderListFragment = new SalesOrderHeaderListFragment();
        salesOrderHeaderListFragmentPendingSync=new SalesOrderHeaderPendingSyncFragment();
        Bundle bundle = getIntent().getExtras();
        Bundle bundle1 = getIntent().getExtras();
        salesOrderHeaderListFragment.setArguments(bundle);
        salesOrderHeaderListFragmentPendingSync.setArguments(bundle1);
        viewPagerAdapter.addFrag(salesOrderHeaderListFragment, getString(R.string.lbl_history));
        viewPagerAdapter.addFrag(salesOrderHeaderListFragmentPendingSync,getString(R.string.lbl_pending_sync));
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public void setActionBarSubTitle(String subTitle) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(subTitle);
        }
    }
}
