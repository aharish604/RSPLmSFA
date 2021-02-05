package com.rspl.sf.msfa.collectionPlan;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.ViewPagerTabAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;

/**
 * Created by e10860 on 2/16/2018.
 */

public class RTGSActivity extends AppCompatActivity {

    private ViewPager viewpager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtgs);
        init();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.ll_rtgs), 0);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager();
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tabLayout.setupWithViewPager(viewpager);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void setupViewPager() {
        ViewPagerTabAdapter adapter = new ViewPagerTabAdapter(getSupportFragmentManager());

        String spGuid = Constants.getSPGUID(Constants.SPGUID);


        Bundle bundle = new Bundle();
        bundle.putString(ConstantsUtils.EXTRA_SPGUID, spGuid);
        RTGSTodayFragment todayFragment = new RTGSTodayFragment();
        todayFragment.setArguments(bundle);
        adapter.addFrag(todayFragment, getString(R.string.today_route_plan));

        RTGSCurrentFragment rtgsFragment = new RTGSCurrentFragment();
        bundle = new Bundle();
        bundle.putString(ConstantsUtils.EXTRA_SPGUID, spGuid);
        bundle.putString(ConstantsUtils.EXTRA_COMING_FROM, ConstantsUtils.MONTH_CURRENT);
        bundle.putString(ConstantsUtils.EXTRA_ExternalRefID, "");
        rtgsFragment.setArguments(bundle);
        adapter.addFrag(rtgsFragment, getString(R.string.current_route_plan));

        RTGSCurrentFragment nextFragment = new RTGSCurrentFragment();
        bundle = new Bundle();
        bundle.putString(ConstantsUtils.EXTRA_SPGUID, spGuid);
        bundle.putString(ConstantsUtils.EXTRA_COMING_FROM, ConstantsUtils.MONTH_NEXT);
        bundle.putString(ConstantsUtils.EXTRA_ExternalRefID, "");
        nextFragment.setArguments(bundle);
        adapter.addFrag(nextFragment, getString(R.string.next_route_plan));

        viewpager.setAdapter(adapter);
        viewpager.setOffscreenPageLimit(2);

    }

    public void displaySubTitle(String title){
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(getString(R.string.ll_rtgs_today)+" "+title);
        }
    }
    public void displaySubTitles(String title){
        if (getSupportActionBar()!=null){
            getSupportActionBar().setSubtitle(title);
        }
    }
}
