package com.rspl.sf.msfa.mtp;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.ViewPagerTabAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.store.OfflineManager;

public class MTPActivity extends AppCompatActivity {
    TabLayout tabLayout;
    private ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mtp);
        init();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.mtp_title), 0);
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
        boolean isAsmLogin = OfflineManager.isASMUser();
        String spGuid = Constants.getSPGUID(Constants.SPGUID);
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsUtils.EXTRA_SPGUID, spGuid);
        bundle.putBoolean(ConstantsUtils.EXTRA_ISASM_LOGIN, isAsmLogin);
        MTPTodayFragment todayFragment = new MTPTodayFragment();
        todayFragment.setArguments(bundle);
        adapter.addFrag(todayFragment, getString(R.string.today_route_plan));

        MTPCurrentFragment rtgsFragment = new MTPCurrentFragment();
        bundle = new Bundle();
        bundle.putString(ConstantsUtils.EXTRA_COMING_FROM, ConstantsUtils.MONTH_CURRENT);
        bundle.putString(ConstantsUtils.EXTRA_SPGUID, spGuid);
        bundle.putBoolean(ConstantsUtils.EXTRA_ISASM_LOGIN, isAsmLogin);
        rtgsFragment.setArguments(bundle);
        adapter.addFrag(rtgsFragment, getString(R.string.current_route_plan));

        MTPNextMthFragment nextFragment = new MTPNextMthFragment();
        bundle = new Bundle();
        bundle.putString(ConstantsUtils.EXTRA_SPGUID, spGuid);
        bundle.putString(ConstantsUtils.EXTRA_COMING_FROM, ConstantsUtils.MONTH_NEXT);
        bundle.putBoolean(ConstantsUtils.EXTRA_ISASM_LOGIN, isAsmLogin);
        nextFragment.setArguments(bundle);
        adapter.addFrag(nextFragment, getString(R.string.next_route_plan));

        viewpager.setAdapter(adapter);

    }
    public void displayTitle(String title){
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(getString(R.string.mtp_title)+" "+title);
        }
    }
    public void displaySubTitles(String title){
        if (getSupportActionBar()!=null){
            getSupportActionBar().setSubtitle(title);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
