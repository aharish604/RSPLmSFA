package com.rspl.sf.msfa.mtp.create;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mtp.MTPHeaderBean;

public class MTPCreateActivity extends AppCompatActivity {


    public static final int REQUEST_CODE_CREATE_NEXT_MONTH = 205;
    private MTPHeaderBean mtpHeaderBean = null;
    private int listPos=-1;
    private String initior="";
    private String comingFrom="";
    private boolean isAsmLogin=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mtp_create);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Intent intent = getIntent();
        String date = "";
        if (intent != null) {
            date = intent.getStringExtra(ConstantsUtils.EXTRA_DATE);
            mtpHeaderBean = (MTPHeaderBean) intent.getSerializableExtra(Constants.EXTRA_BEAN);
            listPos = intent.getIntExtra(ConstantsUtils.EXTRA_POS,0);
            isAsmLogin = intent.getBooleanExtra(ConstantsUtils.EXTRA_ISASM_LOGIN, false);
            initior = intent.getStringExtra(Constants.Initiator);
            comingFrom = intent.getStringExtra(Constants.comingFrom);
        }
        if (mtpHeaderBean == null) {
            mtpHeaderBean = new MTPHeaderBean();
        }
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.mtp_create_title), 0);
        if (savedInstanceState == null)
            initView();
    }

    private void initView() {
        Fragment fragment = new MTPCreateFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantsUtils.EXTRA_POS,listPos);
        bundle.putBoolean(ConstantsUtils.EXTRA_ISASM_LOGIN,isAsmLogin);
        bundle.putString(Constants.Initiator,initior);
        bundle.putString(Constants.comingFrom,comingFrom);
        bundle.putSerializable(Constants.EXTRA_BEAN, mtpHeaderBean);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, fragment);
        fragmentTransaction.commitAllowingStateLoss();
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
