package com.rspl.sf.msfa.visit;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.NewLaunchedProductAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.Config;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;


/**
 * This class may be will use in future for the displaying USP description,This class coming from NewProductListActivity.Java
 */
public class FeedBack extends AppCompatActivity {



    ArrayList<Config> visitMatList;
    ListView lv_focused_product_list = null;
    TextView tvRetName = null, tvUID = null, tv_focused_product_header =null;
    EditText edMaterialNameSearch;
    private NewLaunchedProductAdapter newMustSellAdapter;

    String concatCollectionStr = "";
    ArrayList<String> alAssignColl = new ArrayList<>();
    ProgressDialog syncProgDialog = null;
    boolean dialogCancelled = false;
    TextView tv_last_sync_time_value;

    private String mStrStatus ="",mStrDescription="";

    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focused_product_list);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {


            mStrStatus =extras.getString(Constants.ID);
            mStrDescription =extras.getString(Constants.Description);
            mStrBundleRetName = extras.getString(Constants.RetailerName);
            mStrBundleRetUID = extras.getString(Constants.CPUID);
        }

        initUI();
        getSegmentedMaterials();
        setValuesIntoUI();

        //Initialize action bar with back button(true)
      //  ActionBarView.initActionBarView(this, true,"Feedback");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, "Feedback", 0);


    }




    private void initUI() {
        tvRetName = (TextView) findViewById(R.id.tv_reatiler_name);
        tvUID = (TextView) findViewById(R.id.tv_reatiler_id);
        tv_last_sync_time_value = (TextView)findViewById(R.id.tv_last_sync_time_value);

        tv_focused_product_header = (TextView) findViewById(R.id.tv_focused_product_header);
        lv_focused_product_list = (ListView)findViewById(R.id.lv_focused_prod_list);
        edMaterialNameSearch = (EditText) findViewById(R.id.ed_mat_name_search);
        edMaterialNameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                newMustSellAdapter.getFilter().filter(cs); //Filter from my adapter
                newMustSellAdapter.notifyDataSetChanged(); //Update my view
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
            }
        });
    }
    private void clearEditTextSearchBox(){
        if(edMaterialNameSearch!=null && edMaterialNameSearch.getText().toString().length()>0)
            edMaterialNameSearch.setText("");
    }
    private void getSegmentedMaterials(){
        visitMatList = new ArrayList<>();
        String mStrFocusedProductQry = Constants.SegmentedMaterials + "?$filter="
                + Constants.SegmentId + " eq '" + mStrStatus + "' &$orderby=" + Constants.MaterialDesc + "%20asc";
        try {
            visitMatList = OfflineManager.getFocusedProdList(mStrFocusedProductQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    private void setValuesIntoUI(){
       // tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE,Constants.Collections,Constants.SegmentedMaterials,Constants.TimeStamp,this));
        tvRetName.setText(mStrBundleRetName);
        tvUID.setText(mStrBundleRetUID);
        tv_focused_product_header.setText(mStrDescription);

        loadAdapter();
    }

    private void loadAdapter(){
        newMustSellAdapter = new NewLaunchedProductAdapter(this, visitMatList);
        lv_focused_product_list.setEmptyView(findViewById(R.id.tv_empty_lay) );
        lv_focused_product_list.setAdapter(newMustSellAdapter);
        newMustSellAdapter.notifyDataSetChanged();
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


}
