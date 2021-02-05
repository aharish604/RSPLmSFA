package com.rspl.sf.msfa.filter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.so.SOUtils;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by e10769 on 18-09-2017.
 */

public class DateFilterFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    public static String EXTRA_DEFAULT = "extraDefaultValues";
    public static String EXTRA_END_DATE = "extraEndDateValues";
    public static String EXTRA_START_DATE = "extraStartDateValues";
    private RadioGroup radioGroup;
    private RadioButton rbToday, rbTodayYesterday, rbLastSeven, rbLastOneMonth, rbManualSelection;
    private LinearLayout manualSelect;
    private TextView tvStartDate, Header,tvEndDate;
    private String filterType = "";
    private Context mContext;
    private TextView dateSet = null;
    private boolean isStartDate = false;
    private DatePickerDialog dialogBox;
    private int DATE_DIALOG_ID = 0;
    private String displayStartDate = "";
    private String displayEndDate = "";
    private String storeStartDate = "";
    private String storeEndDate = "";
    private String comingFrom = "";
    private MenuItem submitItem = null;
    private OnFragmentInteractionListener mListener;
    private String oldFilterType ="";

   /* public DateFilterFragment(OnFragmentInteractionListener mListener) {
        // Required empty public constructor
        this.mListener=mListener;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
        if (getArguments() != null) {
            filterType = getArguments().getString(EXTRA_DEFAULT);
            oldFilterType= filterType;
            storeStartDate = getArguments().getString(EXTRA_START_DATE);
            storeEndDate = getArguments().getString(EXTRA_END_DATE);
            comingFrom = getArguments().getString(Constants.comingFrom,"");
            if (!TextUtils.isEmpty(storeStartDate)) {
                displayStartDate = getDisplayDate(storeStartDate);
            }
            if (!TextUtils.isEmpty(storeEndDate)) {
                displayEndDate = getDisplayDate(storeEndDate);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sofilter, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        radioGroup = (RadioGroup) view.findViewById(R.id.rgHeader);
        rbToday = (RadioButton) view.findViewById(R.id.rbToday);
        rbTodayYesterday = (RadioButton) view.findViewById(R.id.rbTodayYesterday);
        rbLastSeven = (RadioButton) view.findViewById(R.id.rbLastSeven);
        rbLastOneMonth = (RadioButton) view.findViewById(R.id.rbLastOneMonth);
        rbManualSelection = (RadioButton) view.findViewById(R.id.rbManualSelection);
        manualSelect = (LinearLayout) view.findViewById(R.id.manualSelect);
        tvStartDate = (TextView) view.findViewById(R.id.tvStartDate);
        tvEndDate = (TextView) view.findViewById(R.id.tvEndDate);
        Header = (TextView) view.findViewById(R.id.Header);
        if(comingFrom.equalsIgnoreCase("GRReport")){
            Header.setText("Invoice Date");
        }
        mContext = getContext();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) view.findViewById(checkedId);
                if (radioButton != null) {
                    filterType = radioButton.getText().toString();
                }
                if (!oldFilterType.equalsIgnoreCase(mContext.getString(R.string.so_filter_manual_selection))){
                    tvStartDate.setText("");
                    tvEndDate.setText("");
                    storeStartDate="";
                    storeEndDate="";
                }
                if (filterType.equalsIgnoreCase(mContext.getString(R.string.so_filter_manual_selection))) {
                    manualSelect.setVisibility(View.VISIBLE);
                } else {
                    manualSelect.setVisibility(View.GONE);
                }
            }
        });
        if (filterType.equalsIgnoreCase(mContext.getString(R.string.so_filter_today))) {
            rbToday.setChecked(true);
        } else if (filterType.equalsIgnoreCase(mContext.getString(R.string.so_filter_today_and_yesterday))) {
            rbTodayYesterday.setChecked(true);
        } else if (filterType.equalsIgnoreCase(mContext.getString(R.string.so_filter_last_seven_days))) {
            rbLastSeven.setChecked(true);
        } else if (filterType.equalsIgnoreCase(mContext.getString(R.string.so_filter_last_one_month))) {
            rbLastOneMonth.setChecked(true);
        } else if (filterType.equalsIgnoreCase(mContext.getString(R.string.so_filter_manual_selection))) {
            rbManualSelection.setChecked(true);
        }
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        dialogBox = new DatePickerDialog(mContext, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartDate = true;
                dateSet = tvStartDate;
                dialogBox.show();
                getActivity().showDialog(DATE_DIALOG_ID);
            }
        });
        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartDate = false;
                dateSet = tvEndDate;
                dialogBox.show();
                getActivity().showDialog(DATE_DIALOG_ID);
            }
        });
        tvStartDate.setText(displayStartDate);
        tvEndDate.setText(displayEndDate);
    }


    public void onSendBackData(String startDate, String endDate, String filterType) {
        if (submitItem != null) {
            submitItem.setVisible(false);
        }
       /* Intent intent = new Intent();
        intent.putExtra(DateFilterFragment.EXTRA_DEFAULT, filterType);
        intent.putExtra(DateFilterFragment.EXTRA_START_DATE, startDate);
        intent.putExtra(DateFilterFragment.EXTRA_END_DATE, endDate);
        getActivity().setResult(ConstantsUtils.ACTIVITY_RESULT_FILTER, intent);
        getActivity().finish();*/
        if (mListener != null) {
            mListener.onFragmentInteraction(startDate, endDate, filterType);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        setDateToView(year, month, dayOfMonth, dateSet);
    }

    private void setDateToView(int mYear, int mMonth, int mDay, TextView dateSet) {
        String mon = "";
        String day = "";
        int mnt = 0;
        mnt = mMonth + 1;
        if (mnt < 10)
            mon = "0" + mnt;
        else
            mon = "" + mnt;
        day = "" + mDay;
        if (mDay < 10)
            day = "0" + mDay;
        if (isStartDate) {
            storeStartDate = mYear + "-" + mon + "-" + day + "T00:00:00";
            displayStartDate = new StringBuilder().append(mDay).append("-")
                    .append(Constants.ORG_MONTHS[mMonth]).append("-").append("")
                    .append(mYear).toString();
        } else {
            storeEndDate = mYear + "-" + mon + "-" + day + "T00:00:00";
            displayEndDate = new StringBuilder().append(mDay).append("-")
                    .append(Constants.ORG_MONTHS[mMonth]).append("-").append("")
                    .append(mYear).toString();
        }
        if (dateSet != null) {
            dateSet.setText(new StringBuilder().append(mDay).append("-")
                    .append(Constants.ORG_MONTHS[mMonth]).append("-").append("")
                    .append(mYear));
        }
    }

     @Override
     public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
         super.onCreateOptionsMenu(menu, inflater);
         inflater.inflate(R.menu.menu_apply, menu);
     }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.apply:
                getDataBasedOnDate(getContext());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getDataBasedOnDate(Context mContext) {
        String stStartDate = "";
        String stEndDate = "";
        if (filterType.equalsIgnoreCase(mContext.getString(R.string.so_filter_manual_selection))) {
            if (!TextUtils.isEmpty(storeStartDate) && !TextUtils.isEmpty(storeEndDate)) {
                if (SOUtils.isStartDateIsSmall(storeStartDate, storeEndDate)) {
                    stStartDate = storeStartDate;
                    stEndDate = storeEndDate;
                } else {
                    stStartDate = storeEndDate;
                    stEndDate = storeStartDate;
                }
            } else {
                ConstantsUtils.displayErrorDialog(mContext, getString(R.string.so_list_enter_valid_date));
                return;
            }
        } else {
            stStartDate = SOUtils.getStartDate(mContext, filterType);
            stEndDate = SOUtils.getEndDate(mContext, filterType);
        }
        onSendBackData(stStartDate, stEndDate, filterType);
    }

    private String getDisplayDate(String dates) {
        String displayDate = "";
        try {
            String[] dateArr = dates.split("T0");
            String[] dme = dateArr[0].split("-");
            String d = dme[2];
            String m = dme[1];
            String y = dme[0];

            displayDate = new StringBuilder().append(d).append("-")
                    .append(Constants.ORG_MONTHS[(Integer.parseInt(m)) - 1]).append("-").append("")
                    .append(y).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return displayDate;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String startDate, String endDate, String filterType);
    }
}
