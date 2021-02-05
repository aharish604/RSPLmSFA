package com.rspl.sf.msfa.routeplan;

import android.content.Intent;
import android.graphics.Color;
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

import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.MapActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.ViewPagerTabAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.prospectedCustomer.ProspectedCustomer;

/**
 * Created by ${e10526} on ${17-11-2016}.
 *
 */
public class RoutePlanListActivity extends AppCompatActivity {

    //This is our viewPager
    private ViewPager viewPager;
    TextView tv_retailer_header;
    public TextView tv_last_sync_time_value;

    Menu menu = null;

    RoutePlanFragment beatPlanFragment;
    OtherRoutePlanFragment nonFiledWorkFragment;
    ViewPagerTabAdapter adapter;
    Bundle bundleBeat;
    Bundle bundleNonFieldWork;

    String dateInDeviceFormat;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_route_plan_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_beat_paln), 0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        onInitUI();
        setValuesToUI();

        //Initialize action bar with back button(true)
       // ActionBarView.initActionBarView(this, true,getString(R.string.str_concat_two_texts,getString(R.string.lbl_beat_paln), dateInDeviceFormat));

        tabInitialize();

    }



    /*
                 * TODO This method initialize UI
                 */
    private void onInitUI(){

        tv_last_sync_time_value = (TextView)findViewById(R.id.tv_last_sync_time_value);
//        tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE,Constants.Collections,Constants.FinancialPostings,Constants.TimeStamp,this));

        tv_retailer_header = (TextView) findViewById(R.id.tv_retailer_header);
        Constants.BoolTodayBeatLoaded = false;
        Constants.BoolOtherBeatLoaded = false;
        Constants.BoolMoreThanOneRoute = false;
        Constants.mSetTodayRouteSch.clear();
    }
    /*
     TODO This method set values to UI
    */
    private void setValuesToUI(){
        String todaysDate = UtilConstants.getDate1();
        dateInDeviceFormat = UtilConstants.convertDateIntoDeviceFormat(this,todaysDate);
        tv_retailer_header.setText(getString(R.string.str_concat_two_texts,getString(R.string.lbl_beat_paln), dateInDeviceFormat));
    }


    /*
                 TODO Initialize Tab
                 */
    private  void tabInitialize(){

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setBackgroundColor(Color.BLUE);

        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0 : showOption(R.id.menu_map);
                        break;
                    case 1:  hideOption(R.id.menu_map);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /*
            TODO Set up fragments into adapter

            */
    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerTabAdapter(getSupportFragmentManager());

        bundleBeat = new Bundle();
        bundleBeat.putString(Constants.RouteType, Constants.BeatPlan);

        bundleNonFieldWork = new Bundle();
        bundleNonFieldWork.putString(Constants.RouteType, Constants.NonFieldWork);


        beatPlanFragment = new RoutePlanFragment();
        beatPlanFragment.setArguments(bundleBeat);

        nonFiledWorkFragment = new OtherRoutePlanFragment();
        nonFiledWorkFragment.setArguments(bundleNonFieldWork);

        adapter.addFrag(beatPlanFragment, getString(R.string.lbl_today_beats));
        adapter.addFrag(nonFiledWorkFragment, getString(R.string.lbl_other_beats));
        viewPager.setAdapter(adapter);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map_back, menu);
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
            case R.id.menu_prospected_customer:
                addProspectedCustomer();
                break;
        }
        return true;
    }
    /*
        TODO disable menu option
             */
    public void hideOption(int id)
    {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }
    /*
     TODO enable menu option
          */
    private void showOption(int id)
    {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    @Override
    public void onBackPressed() {
        /*Intent retList = new Intent(RoutePlanListActivity.this,
                MainMenu.class);
        startActivity(retList);*/
        super.onBackPressed();
    }

    /*
                    TODO Navigate to Map Activity

                    */
    private void displyLocation() {
        Intent i = new Intent(this,MapActivity.class);
        i.putExtra(Constants.NAVFROM, Constants.BeatPlan);
        i.putExtra(Constants.OtherRouteGUID, "");
        startActivity(i);
    }

    private void addProspectedCustomer() {
        Intent i = new Intent(this,ProspectedCustomer.class);
        startActivity(i);
    }

}