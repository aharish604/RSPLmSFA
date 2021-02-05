package com.rspl.sf.msfa.appointment;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.ActionBarView;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;

public class AppointmentList extends AppCompatActivity {

    private String mStrCustomerName = "",mStrUID = "",mStrCPGUID="";
    TextView tvCustomerID;
    TextView tvCustomerName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_list);
        ActionBarView.initActionBarView(this,true,"");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true,"", 0);
        initUi();
    }

    private void initUi()
    {
         tvCustomerID = (TextView) findViewById(R.id.tv_reatiler_id);
         tvCustomerName = (TextView) findViewById(R.id.tv_reatiler_name);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mStrCustomerName = extras.getString(Constants.RetailerName);
            mStrUID = extras.getString(Constants.CPUID);
            mStrCPGUID = extras.getString(Constants.CPGUID);
        }
        tvCustomerID.setText(mStrUID);
        tvCustomerName.setText(mStrCustomerName);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_appointment, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_appointment_create:
                onAppointmentCreate();
                break;


        }
        return true;
    }

    private void onAppointmentCreate() {
        Intent intent = new Intent(this,AppointmentCreate.class);
        intent.putExtra(Constants.RetailerName,mStrCustomerName);
        intent.putExtra(Constants.CPNo,mStrUID);
        intent.putExtra(Constants.CPGUID,mStrCPGUID);
        startActivity(intent);
    }
}
