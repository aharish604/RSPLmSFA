package com.rspl.sf.msfa.login;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;

/**
 * Created by e10526 on 22-07-2016.
 */
public class ForgotPasswordActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        //Initialize action bar with back button(true)
        //ActionBarView.initActionBarView(this, true,getString(R.string.title_forgot_password));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_forgot_password), 0);


        TextView tvSalesPersonName = (TextView)findViewById(R.id.tvRegistrationHeader);
        TextView tv_sales_person_mobile_no = (TextView) findViewById(R.id.tv_sales_person_mobile_no);
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME,
                0);
        String salesPersonName=settings.getString(Constants.SalesPersonName, "");
        String salesPersonMobNo=settings.getString(Constants.SalesPersonMobileNo, "");
        tvSalesPersonName.setText(salesPersonName);
        tv_sales_person_mobile_no.setText(salesPersonMobNo);

        RadioButton rd_mob=(RadioButton)findViewById(R.id.rd_mob);
        rd_mob.setChecked(true);

    }

}
