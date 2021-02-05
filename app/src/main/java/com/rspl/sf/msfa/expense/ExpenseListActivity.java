package com.rspl.sf.msfa.expense;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10742 on 2/17/2017.
 */

public class ExpenseListActivity extends AppCompatActivity {

    RecyclerView rvexpenseList = null;
    TextView tvEmptyListLay = null;

    ArrayList<ExpenseBeanJK> alExpense = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        //ActionBarView.initActionBarView(this, true, getString(R.string.title_expense_list));

        setContentView(R.layout.activity_expense_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_expense_list), 0);

        initUI();
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

    /*Initializes UI*/
    void initUI() {
        rvexpenseList = (RecyclerView) findViewById(R.id.rv_expense_list);
        rvexpenseList.setLayoutManager(new LinearLayoutManager(this));
        tvEmptyListLay = (TextView) findViewById(R.id.tv_empty_lay);

        getExpenseList();
    }

    /*Gets Expense List*/
    void getExpenseList() {

        alExpense.clear();
        try {
            alExpense = OfflineManager.getExpenseListJK(Constants.Expenses);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }



//        alExpense.add(new ExpenseBean("1701310002","31/01/2017", "Food","Approved", "554669", "855.00"));
//        alExpense.add(new ExpenseBean("1702100001","10/02/2017", "Travel","Approval Pending", "565", "1,285.00"));

        rvexpenseList.setAdapter(new ExpenseListAdapter(alExpense, this, tvEmptyListLay));
    }

    public class ExpenseListAdapter extends RecyclerView.Adapter<ExpenseListAdapter.ViewHolder> {
        private ArrayList<ExpenseBeanJK> alExpenseItemList;
        private Context context;
        private TextView tvEmptyListLay;

        public ExpenseListAdapter(ArrayList<ExpenseBeanJK> alExpenseItemList, Context context, TextView tvEmptyListLay) {
            this.alExpenseItemList = alExpenseItemList;
            this.context = context;
            this.tvEmptyListLay = tvEmptyListLay;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_expense_list, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {

            final ExpenseBeanJK expenseListItem = alExpenseItemList.get(i);

            viewHolder.tvClaimNumber.setText(expenseListItem.getClaimNo());
            viewHolder.tvRaisedDate.setText(expenseListItem.getRaisedDate());
            viewHolder.tvExpType.setText(expenseListItem.getExpanseTypeDesc());
            viewHolder.tvAmount.setText(UtilConstants.removeLeadingZerowithTwoDecimal(expenseListItem.getAmount()) + " " + expenseListItem.getCurrency());

            switch (expenseListItem.getStatus()) {
                case "Approved":
                    viewHolder.llExpenseStatus.setBackgroundColor(getResources().getColor(R.color.GREEN));
                    break;

                case "Approval Pending":
                    viewHolder.llExpenseStatus.setBackgroundColor(getResources().getColor(R.color.YELLOW));
                    break;
                default:
                    viewHolder.llExpenseStatus.setBackgroundColor(Color.TRANSPARENT);
            }

            viewHolder.llExpenseItemLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentDCRDetails = new Intent(ExpenseListActivity.this, ExpenseDetailActivity.class);
                    intentDCRDetails.putExtra(Constants.ExpenseGUID, expenseListItem.getExpenseGuid());
                    intentDCRDetails.putExtra(Constants.ExpenseNo, expenseListItem.getClaimNo());
                    intentDCRDetails.putExtra(Constants.ExpenseDate, expenseListItem.getRaisedDate());
                    intentDCRDetails.putExtra(Constants.ExpenseTypeDesc, expenseListItem.getExpanseTypeDesc());
                    intentDCRDetails.putExtra(Constants.Status, expenseListItem.getStatusDesc());
                    startActivity(intentDCRDetails);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (alExpenseItemList.size() == 0) {
                tvEmptyListLay.setVisibility(View.VISIBLE);
            } else {
                tvEmptyListLay.setVisibility(View.GONE);
            }
            return alExpenseItemList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvClaimNumber = null;
            TextView tvRaisedDate = null;
            TextView tvExpType = null;
            TextView tvAmount = null;
            LinearLayout llExpenseItemLay = null;
            LinearLayout llExpenseStatus = null;

            public ViewHolder(View view) {
                super(view);
                tvClaimNumber = (TextView) view.findViewById(R.id.tv_exp_claim_no);
                tvRaisedDate = (TextView) view.findViewById(R.id.tv_exp_raised_date);
                tvExpType = (TextView) view.findViewById(R.id.tv_exp_type);
                tvAmount = (TextView) view.findViewById(R.id.tv_exp_amt);
                llExpenseItemLay = (LinearLayout) view.findViewById(R.id.ll_expense_item);
                llExpenseStatus = (LinearLayout) view.findViewById(R.id.ll_status);
            }
        }
    }
}
