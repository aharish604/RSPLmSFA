package com.rspl.sf.msfa.socreate;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.mbo.MaterialsBean;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SalesOrderReviewItems extends AppCompatActivity {
    private LinearLayout llso_status;
    ArrayList<MaterialsBean> soListBean;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "",mStrBundleCPGUID32="";
    private String mStrBundleRetailerUID = "",mStrComingFrom="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order_review_items);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, "Sales Order Review",0);
        Bundle bundleExtras = getIntent().getExtras();


        if (bundleExtras != null)
        {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            soListBean= getIntent().getParcelableArrayListExtra(Constants.Materials);

        }
        initUI();
        onItemDetails();
    }

    private void initUI() {
        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);
        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetID);
        llso_status = (LinearLayout) findViewById(R.id.ll_so_review_Items);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_back_next, menu);


        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.menu_next:
                onNextNavigation();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private void onNextNavigation()
    {
        Intent intentFeedBack = new Intent(SalesOrderReviewItems.this,SalesOrderPriceDetails.class);
        intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetID);
        intentFeedBack.putExtra(Constants.CPUID, mStrBundleCPGUID);
        intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentFeedBack.putParcelableArrayListExtra(Constants.Materials,soListBean);

        startActivity(intentFeedBack);
    }

    private void onItemDetails() {

        //soListBean = new ArrayList<MaterialsBean>();
//        soListBean = Constants.selMaterialList;
        llso_status.removeAllViews();
        TableLayout table = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_qty_table_2, null);

        for (int i = 0; i < soListBean.size(); i++) {
            TableRow row0 = (TableRow) LayoutInflater.from(this).inflate(
                    R.layout.item_headding, null);
            ((TextView) row0.findViewById(R.id.item_heading)).setText("Item # "+(i+1));
            table.addView(row0);

            TableRow row1 = (TableRow) LayoutInflater.from(this).inflate(
                    R.layout.item_row, null);
            ((TextView) row1.findViewById(R.id.item_lable)).setText("Material");
            ((TextView) row1.findViewById(R.id.item_blank)).setText(" :");
            ((TextView) row1.findViewById(R.id.item_value)).setText(soListBean
                    .get(i).getMaterialNo());
            table.addView(row1);

            TableRow row2 = (TableRow) LayoutInflater.from(this).inflate(
                    R.layout.item_row, null);
            ((TextView) row2.findViewById(R.id.item_lable))
                    .setText("Description");
            ((TextView) row2.findViewById(R.id.item_blank)).setText(" :");
            ((TextView) row2.findViewById(R.id.item_value)).setText(soListBean
                    .get(i).getMaterialDesc());
            table.addView(row2);


//			int randomNo = (int) (Math.random() * 9);


            String materialStockAval = "",uomValue="";


            TableRow rowMatGrp = (TableRow) LayoutInflater.from(this).inflate(
                    R.layout.item_row, null);
            ((TextView) rowMatGrp.findViewById(R.id.item_lable))
                    .setText("Material Stock");
            ((TextView) rowMatGrp.findViewById(R.id.item_blank)).setText(" :");
            ((TextView) rowMatGrp.findViewById(R.id.item_value))
                    .setText(Constants
                            .removeLeadingZero(materialStockAval)+" "+uomValue);
            table.addView(rowMatGrp);



            TableRow row4 = (TableRow) LayoutInflater.from(this).inflate(
                    R.layout.item_qty_enter_row, null);
            ((TextView) row4.findViewById(R.id.item_lable))
                    .setText("Order Qty");
            ((TextView) row4.findViewById(R.id.item_blank)).setText(" :");

            EditText edText = (EditText) row4.findViewById(R.id.edItemQty);
            if(soListBean.get(i).getOrderQty()!=null)
                edText.setText(soListBean.get(i).getOrderQty());
            final MaterialsBean bean = soListBean.get(i);

            edText.setFilters(new InputFilter[] { filter });

            EditTextWatcher watcher = new EditTextWatcher();
            edText.addTextChangedListener(watcher);
            watcher.setTarget(bean);
            bean.setOrderQty(edText.getText().toString());
            table.addView(row4);



            TableRow line = (TableRow) LayoutInflater.from(this).inflate(
                    R.layout.line_row, null);

            table.addView(line);
        }
        llso_status.addView(table);

    }
    InputFilter filter = new InputFilter() {

        Pattern mPattern = Pattern.compile("[0-9]{0," + (12)
                + "}+((\\.[0-9]{0," + (2) + "})?)||(\\.)?");

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            StringBuilder sbText = new StringBuilder(source);
            String text = sbText.toString();
            if (dstart == 0) {
                if (text.contains("0") || text.contains(".")) {
                    return "";
                } else {
                    return source;
                }
            }

            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }

    };
    private class EditTextWatcher implements TextWatcher {

        private MaterialsBean target;

        public void setTarget(MaterialsBean bean) {
            this.target = bean;
        }

        @Override
        public void afterTextChanged(Editable s) {
            target.setOrderQty(s.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }

    }

}
