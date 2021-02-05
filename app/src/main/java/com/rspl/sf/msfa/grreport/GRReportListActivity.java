package com.rspl.sf.msfa.grreport;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.ViewPagerTabAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.reports.salesorder.header.SalesOrderHeaderListActivity;

public class GRReportListActivity extends AppCompatActivity {
    public static boolean mBoolRefreshDone = false;
    ViewPagerTabAdapter viewPagerAdapter;
    ViewPager viewpagerGRHeader;
    TabLayout tabLayoutGRHeader;
    GRReportListFragment GRReportListFragment = null;
    GRReportListFragment grPendingSyncListFragment = null;
    Toolbar toolbar;
    private String TAG = SalesOrderHeaderListActivity.class.getSimpleName();
    TextView tv_RetailerName,tv_RetailerID;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "";
    private Bundle bundleExtras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grreport);
        bundleExtras = getIntent().getExtras();
        if (!Constants.restartApp(GRReportListActivity.this)) {
            initializeUI();
        }
    }
    void initializeUI() {
        viewpagerGRHeader = (ViewPager) findViewById(R.id.viewpagerGRHeader);
        tabLayoutGRHeader = (TabLayout) findViewById(R.id.tabLayoutGRHeader);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_gr_report), 0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initializeTabLayout();
    }
    private void initializeTabLayout() {
        setupViewPager(viewpagerGRHeader);
        tabLayoutGRHeader.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        tabLayoutGRHeader.setupWithViewPager(viewpagerGRHeader);
        viewpagerGRHeader.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: " + position);
                if (position == 0) {
                    if (GRReportListFragment != null && mBoolRefreshDone) {
                        mBoolRefreshDone = false;
                        GRReportListFragment.onRefreshView();
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
        GRReportListFragment = new GRReportListFragment();
        Bundle bundle = getIntent().getExtras();
        Bundle bundle1 = getIntent().getExtras();
        GRReportListFragment.setArguments(bundle);
        viewPagerAdapter.addFrag(GRReportListFragment, getString(R.string.lbl_history));
        viewPager.setAdapter(viewPagerAdapter);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
