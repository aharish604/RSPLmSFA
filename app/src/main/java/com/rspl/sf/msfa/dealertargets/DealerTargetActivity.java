package com.rspl.sf.msfa.dealertargets;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.AllDealerTargetRecyclerViewAdapter;
import com.rspl.sf.msfa.alldealertarget.AllDealerTargetActivity;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.AllDealerTargetDTO;

import java.util.ArrayList;

public class DealerTargetActivity extends AppCompatActivity {
    RecyclerView recyclerViewAllDealerTarget;
    ArrayList<AllDealerTargetDTO> allDealerTargetDTOArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_target);
        //ActionBarView.initActionBarView(this, true, getString(R.string.title_dealer_targets));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_dealer_targets), 0);
        initializeUI();
        initializeRecyclerViewItems();
    }
    private void initializeUI(){
        recyclerViewAllDealerTarget = (RecyclerView)findViewById(R.id.recyclerViewAllDealerTarget);

    }
    private void initializeRecyclerViewItems(){
        recyclerViewAllDealerTarget.setHasFixedSize(true);
        recyclerViewAllDealerTarget.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        AllDealerTargetRecyclerViewAdapter adapter = new AllDealerTargetRecyclerViewAdapter(getApplicationContext(),getAllDealerTargetDTOArrayList());
        recyclerViewAllDealerTarget.setAdapter(adapter);

    }

    private ArrayList<AllDealerTargetDTO> getAllDealerTargetDTOArrayList(){
        this.allDealerTargetDTOArrayList = new ArrayList<>();
        for (int i = 0; i < AllDealerTargetActivity.getMaterialList().size() ; i++) {
            AllDealerTargetDTO allDealerTargetDTO = new AllDealerTargetDTO();
            allDealerTargetDTO.setDealer(AllDealerTargetActivity.getMaterialList().get(i));
            allDealerTargetDTO.setTarget(AllDealerTargetActivity.getTargetsList().get(i));
            allDealerTargetDTO.setMTD(AllDealerTargetActivity.getMTDList().get(i));
            allDealerTargetDTO.setLYSMTD(AllDealerTargetActivity.getLYSMTDList().get(i));
            allDealerTargetDTO.setLYSMAchieved(AllDealerTargetActivity.getLYSMAchievedList().get(i));
            allDealerTargetDTOArrayList.add(allDealerTargetDTO);
        }

        return allDealerTargetDTOArrayList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DealerTargetActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_competition_information).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        DealerTargetActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();
    }
}
