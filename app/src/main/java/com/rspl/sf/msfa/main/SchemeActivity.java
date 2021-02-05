package com.rspl.sf.msfa.main;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.ListAdapter;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.SchemeBean;

import java.io.File;
import java.util.ArrayList;

public class SchemeActivity extends AppCompatActivity {

    //LinearLayout ll_pdf;
    ArrayList<SchemeBean> data=new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schemes_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_schemes_list),0);
        String[] spinnerArray=new String[]{"ASC","DESC"};

        SchemeBean schemeBean = new SchemeBean();
        schemeBean.setSchemeName("Mansoon Dhamaka");
        schemeBean.setValidFrom("12-08-2016");
        schemeBean.setValidTo("30-09-2017");
        data.add(schemeBean);

        ListView list= (ListView) findViewById(R.id.list);

        ListAdapter listAdapter=new ListAdapter(SchemeActivity.this,data);
        list.setAdapter(listAdapter);

    }


    public void readPdf(){

        File dir = Environment.getExternalStorageDirectory();
        File file = new File(dir + "/" + "FolderName" + "/" + "monsoon_dhamaka_q2_fy18_od.pdf");
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(file), "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Intent intent = Intent.createChooser(target, "Open File");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {

            // Toast.makeText(RequestPage.this, "You may not have a proper app for viewing this content ", Toast.LENGTH_LONG).show();
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_back:
                onBackPressed();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

  /*  private void deleteFile(String inputPath, String inputFile) {
        try {
            // delete the original file
            new File(inputPath + inputFile).delete();
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }*/
}
