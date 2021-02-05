package com.rspl.sf.msfa.socreate.stepOne;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.common.MSFAApplication;


public class SOCreateActivity extends AppCompatActivity {


    int comingFrom;
    private Toolbar toolbar;
    private MSFAApplication mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socreate);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Bundle bundle = getIntent().getExtras();
        mApplication = (MSFAApplication) getApplicationContext();
        if (UtilConstants.isNetworkAvailable(SOCreateActivity.this)) {
            mApplication.startService(SOCreateActivity.this);
        }
        if (bundle != null) {
           /* isSessionRequired = bundle.getBoolean(Constants.EXTRA_SESSION_REQUIRED, false);
            comingFrom = bundle.getInt(Constants.EXTRA_COME_FROM, 0);
            soDefaultBean = (SOListBean) bundle.getSerializable(Constants.EXTRA_SO_HEADER);*/
            comingFrom = bundle.getInt(Constants.EXTRA_COME_FROM, 0);
            if (comingFrom == ConstantsUtils.SO_EDIT_ACTIVITY || comingFrom == ConstantsUtils.SO_EDIT_SINGLE_MATERIAL || comingFrom == ConstantsUtils.SO_APPROVAL_EDIT_ACTIVITY) {
                ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.menu_sos_edit), 0);
            } else {
                ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.menu_sos_create), 0);
            }

            SOCreateFragment fragment = new SOCreateFragment();
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.flContainer, fragment);
            fragmentTransaction.commit();
        }
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

    @Override
    public void onBackPressed() {
        if (comingFrom == ConstantsUtils.SO_APPROVAL_EDIT_ACTIVITY) {
            UtilConstants.dialogBoxWithCallBack(SOCreateActivity.this, "", getString(R.string.on_back_press_so_edit_msg), getString(R.string.yes), getString(R.string.no), false, new DialogCallBack() {
                @Override
                public void clickedStatus(boolean clickedStatus) {
                    if (clickedStatus) {
                        finish();
                    }
                }
            });
        } else {
            UtilConstants.dialogBoxWithCallBack(SOCreateActivity.this, "", getString(R.string.on_back_press_so_msg), getString(R.string.yes), getString(R.string.no), false, new DialogCallBack() {
                @Override
                public void clickedStatus(boolean clickedStatus) {
                    if (clickedStatus) {
                        finish();
//                        redirectActivity();
                    }
                }
            });
        }
    }

   /* private void redirectActivity() {
        Intent intent = new Intent(SOCreateActivity.this, CustomerDetailsActivity.class);
        intent.putExtra(Constants.CPNo, Constants.NavCustNo);
        intent.putExtra(Constants.CPUID, Constants.NavCPUID);
        intent.putExtra(Constants.RetailerName, Constants.NavCustName);
        intent.putExtra(Constants.CPGUID32, Constants.NavCPGUID32);
        intent.putExtra(Constants.comingFrom, Constants.NavComingFrom);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }*/
}
