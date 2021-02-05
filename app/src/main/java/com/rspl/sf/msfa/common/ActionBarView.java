package com.rspl.sf.msfa.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rspl.sf.msfa.R;

/**
 * Created by e10742 on 28-10-2016.
 */
public class ActionBarView {

    //Initializing Action for particular activity
    //mActivity-> Calling Activity
    //homeUpEnabled-> for showing back button if true Back button will be enabled else disabled

    public static void initActionBarView(final AppCompatActivity mActivity, boolean homeUpEnabled, String title) {

        if (homeUpEnabled)
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        else
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        mActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_title_bar, null);
        TextView textView = (TextView) view.findViewById(R.id.txtTitle);

        ImageView backImg = (ImageView) view.findViewById(R.id.img_back);
        textView.setText(title);
        if (mActivity.getString(R.string.lbl_main_menu).equalsIgnoreCase(title)) {
            backImg.setVisibility(View.GONE);
        } else {
            backImg.setVisibility(View.VISIBLE);
        }
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.onBackPressed();
            }
        });
        //  displayBackButton(backImg,title);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, Gravity.START);
        mActivity.getSupportActionBar().setDisplayShowCustomEnabled(true);
        mActivity.getSupportActionBar().setCustomView(view, params);
    }
//    public static void initActionBarView(final AppCompatActivity mActivity, boolean homeUpEnabled, String title) {
//
//       if(homeUpEnabled)
//            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        else
//            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//
//
//       // mActivity.getSupportActionBar().setIcon(R.drawable.arteria_new_logo_transparent);
//
//
//        mActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
//        mActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
////        if(findViewById(android.R.id.home)!=null)
////            findViewById(android.R.id.home).setVisibility(View.GONE);
//        LayoutInflater inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.actionbar_center_img_lay, null);
//        TextView titleHead = (TextView)view.findViewById(R.id.title_head);
//        titleHead.setText(title);
//        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
//                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL);
//        mActivity.getSupportActionBar().setDisplayShowCustomEnabled(true);
//        mActivity.getSupportActionBar().setCustomView(view, params);
//
////        if (homeUpEnabled)
////            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
////
////        else
////            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
////
////        mActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
////        mActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
////
////        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
////        View view = inflater.inflate(R.layout.layout_title_bar, null);
////        TextView textView = (TextView) view.findViewById(R.id.txtTitle);
////
////        ImageView backImg = (ImageView) view.findViewById(R.id.img_back);
////        textView.setText(title);
////        if (mActivity.getString(R.string.lbl_main_menu).equalsIgnoreCase(title)
////               /* || mActivity.getString(R.string.title_forgot_password).equalsIgnoreCase(title)
////                || mActivity.getString(R.string.lbl_retailer_list).equalsIgnoreCase(title)
////                || mActivity.getString(R.string.lbl_alerts).equalsIgnoreCase(title)
////                || mActivity.getString(R.string.title_my_targets).equalsIgnoreCase(title)
////                || mActivity.getString(R.string.title_dbstoxk_and_price).equalsIgnoreCase(title)
////                || mActivity.getString(R.string.lbl_day_summary).equalsIgnoreCase(title)
////                || mActivity.getString(R.string.sync_menu).equalsIgnoreCase(title)
////                || mActivity.getString(R.string.log_menu).equalsIgnoreCase(title)*/) {
////            backImg.setVisibility(View.GONE);
////        } else {
////            backImg.setVisibility(View.VISIBLE);
////        }
////        backImg.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                mActivity.onBackPressed();
////            }
////        });
////        //  displayBackButton(backImg,title);
////        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
////                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, Gravity.START);
////        mActivity.getSupportActionBar().setDisplayShowCustomEnabled(true);
////        mActivity.getSupportActionBar().setCustomView(view, params);
//    }
}
