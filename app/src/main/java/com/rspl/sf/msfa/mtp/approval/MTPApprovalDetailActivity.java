package com.rspl.sf.msfa.mtp.approval;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mtp.MTPNextMthFragment;

/**
 * Created by e10860 on 2/27/2018.
 */

public class MTPApprovalDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mtp_approval_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, "MTP Approval", 0);
        MTPNextMthFragment mtpNextMthFragment = new MTPNextMthFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsUtils.EXTRA_COMING_FROM, ConstantsUtils.MTP_APPROVAL);

        if (getIntent() != null) {
            bundle.putSerializable(Constants.EXTRA_BEAN, getIntent().getSerializableExtra(Constants.EXTRA_BEAN));
            bundle.putString(ConstantsUtils.ROUTE_INSTANCE_ID, getIntent().getStringExtra(ConstantsUtils.ROUTE_INSTANCE_ID));
            bundle.putString(ConstantsUtils.ROUTE_ENTITY_KEY, getIntent().getStringExtra(ConstantsUtils.ROUTE_ENTITY_KEY));
            bundle.putString(Constants.Initiator, getIntent().getStringExtra(Constants.Initiator));
        }
        mtpNextMthFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mtpApprovalFrame, mtpNextMthFragment, mtpNextMthFragment.getClass().getName());
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
