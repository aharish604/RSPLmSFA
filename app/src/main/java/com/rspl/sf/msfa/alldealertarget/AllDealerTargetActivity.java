package com.rspl.sf.msfa.alldealertarget;

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
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.AllDealerTargetDTO;

import java.util.ArrayList;


public class AllDealerTargetActivity extends AppCompatActivity {

    RecyclerView recyclerViewAllDealerTarget;
    ArrayList<AllDealerTargetDTO> allDealerTargetDTOArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_dealer_target);
       // ActionBarView.initActionBarView(this, true, getString(R.string.title_all_dealer_targets));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_all_dealer_targets), 0);

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

    private ArrayList<AllDealerTargetDTO>getAllDealerTargetDTOArrayList(){
        this.allDealerTargetDTOArrayList = new ArrayList<>();
        for (int i = 0; i <getDealersList().size() ; i++) {
            AllDealerTargetDTO allDealerTargetDTO = new AllDealerTargetDTO();
            allDealerTargetDTO.setDealer(getDealersList().get(i));
            allDealerTargetDTO.setTarget(getTargetsList().get(i));
            allDealerTargetDTO.setMTD(getMTDList().get(i));
            allDealerTargetDTO.setLYSMTD(getLYSMTDList().get(i));
            allDealerTargetDTO.setLYSMAchieved(getLYSMAchievedList().get(i));
            allDealerTargetDTOArrayList.add(allDealerTargetDTO);
        }

        return allDealerTargetDTOArrayList;
    }

    public static ArrayList<String>getMaterialList(){
        ArrayList<String> materialList = new ArrayList<>();
        materialList.add("BIRLA GOLD PPC\n301116010002");
        materialList.add("BIRLA GOLD PREMIUM CEMENT PPC\n301116010051");
        return materialList;
    }
    public static ArrayList<String>getDealersList(){
        ArrayList<String> dealersList = new ArrayList<>();
        dealersList.add("GOEL TRADERS\n0000102713");
        dealersList.add("SHIVAM ENTERPRISES\n0000102723");
        return dealersList;
    }
    public static ArrayList<Integer>getTargetsList(){
        ArrayList<Integer> targets = new ArrayList<>();
        targets.add(200);
        targets.add(250);
        return targets;
    }
    public static ArrayList<Integer>getMTDList(){
        ArrayList<Integer> mtdList = new ArrayList<>();
        mtdList.add(120);
        mtdList.add(130);
        return mtdList;
    }
    public static ArrayList<Integer>getLYSMTDList(){
        ArrayList<Integer> lysmtdList = new ArrayList<>();
        lysmtdList.add(110);
        lysmtdList.add(115);
        return lysmtdList;
    }
    public static ArrayList<Integer>getLYSMAchievedList(){
        ArrayList<Integer> lysmAchievedList = new ArrayList<>();
        lysmAchievedList.add(190);
        lysmAchievedList.add(220);
        return lysmAchievedList;
    }
    public static ArrayList<Integer>getMyTargetvsActualList(){
        ArrayList<Integer> lysmAchievedList = new ArrayList<>();
        lysmAchievedList.add(400);
        lysmAchievedList.add(450);
        return lysmAchievedList;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(AllDealerTargetActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_competition_information).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        AllDealerTargetActivity.this.finish();
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
