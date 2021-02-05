package com.rspl.sf.msfa.mtp.subordinate;

import android.content.Intent;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.ViewPagerTabAdapter;
import com.rspl.sf.msfa.collectionPlan.RTGSCurrentFragment;
import com.rspl.sf.msfa.common.ConstantsUtils;

public class RTGSSubOrdActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mtp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.ll_rtgs), 0);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager();
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tabLayout.setupWithViewPager(viewpager);
    }
    private void setupViewPager() {
        Bundle bundles = getIntent().getExtras();
        ViewPagerTabAdapter adapter = new ViewPagerTabAdapter(getSupportFragmentManager());
        RTGSCurrentFragment rtgsFragment = new RTGSCurrentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsUtils.EXTRA_SPGUID, bundles.getString(ConstantsUtils.EXTRA_SPGUID));
        bundle.putString(ConstantsUtils.EXTRA_ExternalRefID, bundles.getString(ConstantsUtils.EXTRA_ExternalRefID));
        bundle.putString(ConstantsUtils.EXTRA_COMING_FROM, ConstantsUtils.RTGS_SUBORDINATE_CURRENT);
        rtgsFragment.setArguments(bundle);
        adapter.addFrag(rtgsFragment, getString(R.string.current_route_plan));

        RTGSCurrentFragment nextFragment = new RTGSCurrentFragment();
        bundle = new Bundle();
        bundle.putString(ConstantsUtils.EXTRA_SPGUID, bundles.getString(ConstantsUtils.EXTRA_SPGUID));
        bundle.putString(ConstantsUtils.EXTRA_ExternalRefID, bundles.getString(ConstantsUtils.EXTRA_ExternalRefID));
        bundle.putString(ConstantsUtils.EXTRA_COMING_FROM, ConstantsUtils.RTGS_SUBORDINATE_NEXT);
        nextFragment.setArguments(bundle);
        adapter.addFrag(nextFragment, getString(R.string.next_route_plan));

        viewpager.setAdapter(adapter);

    }
    public void displayTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.ll_rtgs_today) + " " + title);
        }
    }

    public void displaySubTitles(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(title);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
}
