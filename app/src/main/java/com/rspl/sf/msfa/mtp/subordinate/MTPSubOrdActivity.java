package com.rspl.sf.msfa.mtp.subordinate;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.ViewPagerTabAdapter;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mtp.MTPCurrentFragment;
import com.rspl.sf.msfa.mtp.MTPNextMthFragment;

public class MTPSubOrdActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mtp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.mtp_title), 0);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager();
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tabLayout.setupWithViewPager(viewpager);
    }
    private void setupViewPager() {
        Bundle bundles = getIntent().getExtras();
        ViewPagerTabAdapter adapter = new ViewPagerTabAdapter(getSupportFragmentManager());
        boolean isAsmLogin = false;
        MTPCurrentFragment rtgsFragment = new MTPCurrentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsUtils.EXTRA_COMING_FROM, ConstantsUtils.MTP_SUBORDINATE_CURRENT);
        bundle.putString(ConstantsUtils.EXTRA_SPGUID, bundles.getString(ConstantsUtils.EXTRA_SPGUID));
        bundle.putBoolean(ConstantsUtils.EXTRA_ISASM_LOGIN, isAsmLogin);
        rtgsFragment.setArguments(bundle);
        adapter.addFrag(rtgsFragment, getString(R.string.current_route_plan));

        MTPNextMthFragment nextFragment = new MTPNextMthFragment();
        bundle = new Bundle();
        bundle.putString(ConstantsUtils.EXTRA_SPGUID, bundles.getString(ConstantsUtils.EXTRA_SPGUID));
        bundle.putString(ConstantsUtils.EXTRA_COMING_FROM,ConstantsUtils.MTP_SUBORDINATE_NEXT);
        bundle.putBoolean(ConstantsUtils.EXTRA_ISASM_LOGIN, isAsmLogin);
        nextFragment.setArguments(bundle);
        adapter.addFrag(nextFragment, getString(R.string.next_route_plan));

        viewpager.setAdapter(adapter);

    }

    public void displayTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.mtp_title) + " " + title);
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
