//package com.arteriatech.sf.reports.InvoiceHistory;
//
//import android.os.Bundle;
//import android.support.design.widget.TabLayout;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.view.ViewPager;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.view.MenuItem;
//import android.view.WindowManager;
//
//import com.arteriatech.sf.adapter.ViewPagerTabAdapter;
//import Constants;
//import com.arteriatech.sf.common.ConstantsUtils;
//import R;
//
//public class InvoiceHistoryHeaderListActivity extends AppCompatActivity {
//    ViewPagerTabAdapter viewPagerAdapter;
//    ViewPager viewpagerSalesOrderHeader;
//    TabLayout tabLayoutSalesOrderHeader;
//    InvoiceHistoryListFragment salesOrderHeaderListFragment,salesOrderHeaderListFragmentPendingSync;
//    Toolbar toolbar;
//
//    private String mStrBundleRetID = "",mStrBundleCPGUID="";
//    private String mStrBundleRetName = "";
//    private String mStrBundleRetUID = "";
//    private Bundle bundleExtras;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_invoicehistory);
//        bundleExtras = getIntent().getExtras();
//        if (bundleExtras != null) {
//            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
//            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
//            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
//            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
//        }
//
//        initializeUI();
//    }
//    void initializeUI(){
//        viewpagerSalesOrderHeader=(ViewPager)findViewById(R.id.viewpagerSalesOrderHeader);
//        tabLayoutSalesOrderHeader=(TabLayout)findViewById(R.id.tabLayoutSalesOrderHeader);
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_sales_order),0);
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        initializeTabLayout();
//    }
//    private void initializeTabLayout(){
//        setupViewPager(viewpagerSalesOrderHeader);
//        tabLayoutSalesOrderHeader.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
//        tabLayoutSalesOrderHeader.setupWithViewPager(viewpagerSalesOrderHeader);
//        viewpagerSalesOrderHeader.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//           @Override
//           public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//           }
//           @Override
//           public void onPageSelected(int position) {
//
//           }
//           @Override
//           public void onPageScrollStateChanged(int state) {
//
//           }
//       });
//    }
//    private void setupViewPager(ViewPager viewPager) {
//        viewPagerAdapter = new ViewPagerTabAdapter(getSupportFragmentManager());
//        salesOrderHeaderListFragment = new InvoiceHistoryListFragment();
//        salesOrderHeaderListFragmentPendingSync=new InvoiceHistoryListFragment();
//        Bundle bundle = getIntent().getExtras();
//        salesOrderHeaderListFragment.setArguments(bundle);
//        salesOrderHeaderListFragmentPendingSync.setArguments(bundle);
//        viewPagerAdapter.addFrag(salesOrderHeaderListFragment, getString(R.string.lbl_history));
//        viewPagerAdapter.addFrag(salesOrderHeaderListFragmentPendingSync,getString(R.string.lbl_pending_sync));
//        viewPager.setAdapter(viewPagerAdapter);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case android.R.id.home:
//                finish();
//                return true;
//            default:return super.onOptionsItemSelected(item);
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
//    }
//}
