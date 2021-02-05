package com.rspl.sf.msfa.schemes;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.arteriatech.mutils.filterlist.SearchFilter;
import com.arteriatech.mutils.filterlist.SearchFilterInterface;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.ConstantsUtils;

import java.util.ArrayList;

/**
 * Created by e10742 on 6/7/2017.
 */

public class SchemesActivity extends AppCompatActivity {
    RecyclerView rvSchemeList = null;
    TextView tvEmptyListLay = null;
    ArrayList<SchemeBean> alScheme = new ArrayList<>();
    EditText etSearchScheme = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schemes);
        //ActionBarView.initActionBarView(this, true, getString(R.string.title_schemes));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_schemes), 0);
        initUI();
    }

    /*Initializes UI*/
    void initUI() {
        rvSchemeList = (RecyclerView) findViewById(R.id.rv_scheme_list);
        rvSchemeList.setLayoutManager(new LinearLayoutManager(this));
        tvEmptyListLay = (TextView) findViewById(R.id.tv_empty_lay);
        etSearchScheme = (EditText) findViewById(R.id.et_name_search);

        getSchemeList();

        etSearchScheme.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                ArrayList<SchemeBean> alTempList = new ArrayList<>();
                alTempList.clear();
                alTempList = (ArrayList<SchemeBean>) SearchFilter.filter(alScheme, new SearchFilterInterface<SchemeBean>() {
                    @Override
                    public boolean applyConditionToAdd(SchemeBean item) {
                        if (item.getSchemeName().contains(s.toString()))
                            return true;
                        else
                            return false;
                    }
                });
                rvSchemeList.setAdapter(new SchemeListAdapter(alTempList, SchemesActivity.this, tvEmptyListLay));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

    /*Gets Expense List*/
    void getSchemeList() {

        alScheme.clear();
//        try {
//            alScheme = OfflineManager.getExpenseListJK(Constants.Expenses);
//        } catch (OfflineODataStoreException e) {
//            e.printStackTrace();
//        }
        alScheme.add(new SchemeBean("Scheme1", "31/01/2017", "10/08/2017"));
        alScheme.add(new SchemeBean("Scheme2", "10/02/2017", "15/06/2017"));

        rvSchemeList.setAdapter(new SchemeListAdapter(alScheme, this, tvEmptyListLay));
    }

    public class SchemeListAdapter extends RecyclerView.Adapter<SchemeListAdapter.ViewHolder> {
        private ArrayList<SchemeBean> alSchemeItemList;
        private Context context;
        private TextView tvEmptyListLay;

        public SchemeListAdapter(ArrayList<SchemeBean> alSchemeItemList, Context context, TextView tvEmptyListLay) {
            this.alSchemeItemList = alSchemeItemList;
            this.context = context;
            this.tvEmptyListLay = tvEmptyListLay;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_scheme_list, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {

            final SchemeBean expenseListItem = alSchemeItemList.get(i);

            viewHolder.tvSchemeName.setText(expenseListItem.getSchemeName());
            viewHolder.tvValidFrom.setText(expenseListItem.getValidFrom());
            viewHolder.tvValidTo.setText(expenseListItem.getValidTo());
        }

        @Override
        public int getItemCount() {
            if (alSchemeItemList.size() == 0) {
                tvEmptyListLay.setVisibility(View.VISIBLE);
            } else {
                tvEmptyListLay.setVisibility(View.GONE);
            }
            return alSchemeItemList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvSchemeName = null;
            TextView tvValidFrom = null;
            TextView tvValidTo = null;

            public ViewHolder(View view) {
                super(view);
                tvSchemeName = (TextView) view.findViewById(R.id.tv_scheme_name);
                tvValidFrom = (TextView) view.findViewById(R.id.tv_valid_from);
                tvValidTo = (TextView) view.findViewById(R.id.tv_valid_to);
            }
        }
    }
}
