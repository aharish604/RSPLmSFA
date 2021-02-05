package com.rspl.sf.msfa.routeplan;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.RoutePlanAdapter;
import com.rspl.sf.msfa.adapter.TodayRoutePlanAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.mbo.CustomerBean;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10526 on 27-10-2016.
 *
 */
public class RoutePlanFragment extends Fragment  {
    String mStrRouteType = "";
    EditText et_name_search;
    private RoutePlanAdapter retailerAdapter = null;
    ListView lv_route_ret_list = null;
    TextView tvEmptyLay = null,tv_route_name=null;
    LinearLayout ll_route_name_line;
    ProgressDialog pdLoadDialog;
    View myInflatedView;
    ArrayList<CustomerBean> alRSCHList = null,alRouteName=null,alRetailerList=null,  prospectedCustomer = null;
    String routeQry=null,routeName = "";
    String [] searchArray = {"Name","Code","City"};
    String [] searchCode = {"1","2","3"};
    Spinner spSearchCode;
    String selectedSearchCode="";
    TodayRoutePlanAdapter routeAdapter= null;

    public RoutePlanFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mStrRouteType = getArguments().getString(Constants.RouteType);
        myInflatedView = inflater.inflate(R.layout.fragment_route_plan, container, false);

        onInitUI(myInflatedView);
        loadSpinner();
        LoadingData();
        return myInflatedView;
    }

    /*
           * TODO This method initialize UI
           */
    private void onInitUI(View myInflatedView) {
        et_name_search = (EditText) myInflatedView.findViewById(R.id.et_name_search_route_paln);
        spSearchCode = (Spinner)myInflatedView.findViewById(R.id.spnr_customer_search_routeplan_list);
        tv_route_name = (TextView) myInflatedView.findViewById(R.id.tv_route_name);
        ll_route_name_line= (LinearLayout) myInflatedView.findViewById(R.id.ll_route_name_line);
        tvEmptyLay = (TextView) myInflatedView.findViewById(R.id.tv_empty_lay_today_beat);
        lv_route_ret_list = (ListView) myInflatedView.findViewById(R.id.lv_route_ret_list);

        ArrayAdapter<String> searchadapter = new ArrayAdapter<>(getActivity(), R.layout.custom_textview, searchArray);
        searchadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSearchCode.setAdapter(searchadapter);



    }

    /*
              * TODO Get Route List
              */
    private void getRouteList() {



        try {

            if (mStrRouteType.equalsIgnoreCase(Constants.BeatPlan))
            {
                routeQry = Constants.RoutePlans + "?$filter=" + Constants.VisitDate + " eq datetime'" + UtilConstants.getNewDate() + "'";

                alRSCHList = OfflineManager.getTodayRoutes1(routeQry);

                if(alRSCHList!=null && alRSCHList.size()>0){
                    // Based on RouteScope value need to decide where to fetch applicable retailers for the beat
                    // RouteScope = 000001, Get from RoutePlanSchedules
                    // RouteScope = 000002, Get from Routeplans
                    if(alRSCHList.size()>1)
                    {

                    }
                    else
                    {
                        String routeSchopeVal = alRSCHList.get(0).getRoutSchScope();
                        Constants.Route_Plan_Key = alRSCHList.get(0).getRoutePlanKey();
                        routeName = alRSCHList.get(0).getRouteDesc();

                        if(routeSchopeVal.equalsIgnoreCase("000001"))
                        {
                            // Get the list of retailers from RouteSchedulePlans
                            String qryForTodaysBeat = Constants.RouteSchedulePlans + "?$filter=" + Constants.RouteSchGUID + " eq guid'"
                                    + alRSCHList.get(0).getRschGuid().toUpperCase() + "' &$orderby=" + Constants.SequenceNo + "";

                            List<CustomerBean> listRetailers = OfflineManager.getBeatList(qryForTodaysBeat);
                            alRetailerList = (ArrayList<CustomerBean>) listRetailers;



                        }
                        else if(routeSchopeVal.equalsIgnoreCase("000002"))
                        {
                            // Get the list of retailers from RoutePlans
                        }
                    }
                }


            }
            loadProspectedCustomer();


        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        Constants.BoolTodayBeatLoaded = true;
    }


    public void loadProspectedCustomer(){

        try {
            prospectedCustomer = new ArrayList<CustomerBean>();

            CustomerBean lb = null;

            Cursor prospectedCustVal = Constants.events
                    .getEvents(Constants.PROSPECTED_TABLE);
            if (prospectedCustVal != null) {
                while (prospectedCustVal.moveToNext()) {

                    String counterName = "";
                    String counterType = "";
                    String contactPerson = "";
                    String mobileNo = "";
                    String address = "";
                    String city = "";
                    String district = "";
                    String taluka    = "";
                    String pinCode = "";
                    String block = "";
                    String totalTradepontential = "";
                    String totalNontradepotential = "";
                    String potentialAvilable = "";
                    String popDistributed = "";
                    String Remarks = "";
                    String cpNo = "";


                    counterName = prospectedCustVal
                            .getString(prospectedCustVal
                                    .getColumnIndex("CounterName"));
                    cpNo = prospectedCustVal
                            .getString(prospectedCustVal
                                    .getColumnIndex(Constants.CustomerNo))!= null ? prospectedCustVal
                            .getString(prospectedCustVal.getColumnIndex(Constants.CustomerNo))
                            : "";
                    counterType = prospectedCustVal
                            .getString(prospectedCustVal
                                    .getColumnIndex("CounterType"));

                    contactPerson = prospectedCustVal
                            .getString(prospectedCustVal
                                    .getColumnIndex("ContactPerson"));
                    mobileNo = prospectedCustVal
                            .getString(prospectedCustVal
                                    .getColumnIndex("PCMobileNo"));
                    address = prospectedCustVal
                            .getString(prospectedCustVal
                                    .getColumnIndex("ProspectecCustomerAddress"));

                    city = prospectedCustVal
                            .getString(prospectedCustVal
                                    .getColumnIndex("City"));

                    district = prospectedCustVal.getString(prospectedCustVal
                            .getColumnIndex("PCDistrict")) != null ? prospectedCustVal
                            .getString(prospectedCustVal.getColumnIndex("PCDistrict"))
                            : "";



                    taluka = prospectedCustVal.getString(prospectedCustVal
                            .getColumnIndex("Taluka")) != null ? prospectedCustVal
                            .getString(prospectedCustVal.getColumnIndex("Taluka"))
                            : "";



                    pinCode = prospectedCustVal.getString(prospectedCustVal
                            .getColumnIndex("PinCode")) != null ? prospectedCustVal
                            .getString(prospectedCustVal.getColumnIndex("PinCode"))
                            : "";




                    block = prospectedCustVal.getString(prospectedCustVal
                            .getColumnIndex("Block")) != null ? prospectedCustVal
                            .getString(prospectedCustVal.getColumnIndex("Block"))
                            : "";




                    totalTradepontential = prospectedCustVal.getString(prospectedCustVal
                            .getColumnIndex("TotalTradePottential")) != null ? prospectedCustVal
                            .getString(prospectedCustVal.getColumnIndex("TotalTradePottential"))
                            : "";




                    totalNontradepotential = prospectedCustVal.getString(prospectedCustVal
                            .getColumnIndex("TotalNonTradePottential")) != null ? prospectedCustVal
                            .getString(prospectedCustVal.getColumnIndex("TotalNonTradePottential"))
                            : "";

                    potentialAvilable = prospectedCustVal.getString(prospectedCustVal
                            .getColumnIndex("PottentialAvailable")) != null ? prospectedCustVal
                            .getString(prospectedCustVal.getColumnIndex("PottentialAvailable"))
                            : "";


                    popDistributed = prospectedCustVal.getString(prospectedCustVal
                            .getColumnIndex("POPDistributed")) != null ? prospectedCustVal
                            .getString(prospectedCustVal.getColumnIndex("POPDistributed"))
                            : "";


                    Remarks = prospectedCustVal.getString(prospectedCustVal
                            .getColumnIndex("PCRemarks")) != null ? prospectedCustVal
                            .getString(prospectedCustVal.getColumnIndex("PCRemarks"))
                            : "";




                    if (counterName != null) {
                        lb = new CustomerBean("");
                        lb.setCustomerName(counterName);
                        lb.setRetailerName(counterName);
                        lb.setMobileNumber(mobileNo);
                        lb.setAddress1(address);
                        lb.setCity(city);
                        lb.setDistrict(district);
                        lb.setPostalCode(pinCode);
                        lb.setCustomerId(cpNo);
                        lb.setCustomerType(Constants.ProspectiveCustomerList);
                        prospectedCustomer.add(lb);
                    }
                }
                prospectedCustVal.deactivate();
                prospectedCustVal.close();
            }

        } catch (Exception e) {
            // if (e != null)
            // LogController.getInstance(getApplicationContext()).E(
            // e.getMessage());

            String err = e.getMessage();

            //Toast.makeText(this, err, Toast.LENGTH_LONG).show();
        }






    }



//    @Override
//    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//    }
//
//    @Override
//    public void onTextChanged(CharSequence cs, int start, int before, int count) {
//        retailerAdapter.getFilter().filter(cs); //Filter from my adapter
//        retailerAdapter.notifyDataSetChanged(); //Update my view
//    }
//
//    @Override
//    public void afterTextChanged(Editable s) {
//    }


    private void LoadingData() {
        try {
            new AsynLoadTodaysBeat().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /*AsyncTask to get Route Plans*/
    private class AsynLoadTodaysBeat extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoadDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            getRouteList();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


            checkListLoaded();
        }
    }

    public void checkListLoaded() {
        if (Constants.BoolTodayBeatLoaded && Constants.BoolOtherBeatLoaded) {
            pdLoadDialog.dismiss();
            onDisplyTodaysRoute();

        } else {
            try {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkListLoaded();
                    }
                }, 100);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void onDisplyTodaysRoute() {
        if(alRSCHList==null){
            alRSCHList =new ArrayList<>();

        }

        if(alRetailerList==null){
            alRetailerList =new ArrayList<>();

        }

        for(int i=0 ;i<prospectedCustomer.size();i++){

            CustomerBean c =  prospectedCustomer.get(i);

            alRetailerList.add(c);
        }
        if(alRSCHList.size()>1){


            routeAdapter = new TodayRoutePlanAdapter(myInflatedView, alRSCHList, mStrRouteType, tvEmptyLay);
            lv_route_ret_list.setEmptyView(getActivity().findViewById(R.id.tv_empty_lay_today_beat) );
            lv_route_ret_list.setAdapter(routeAdapter);
            routeAdapter.notifyDataSetChanged();

            et_name_search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence cs, int i, int i1, int i2) {
                    routeAdapter.getFilter(selectedSearchCode).filter(cs); //Filter from my adapter

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }else{
            retailerAdapter = new RoutePlanAdapter(myInflatedView, alRetailerList, mStrRouteType, tvEmptyLay);
            lv_route_ret_list.setEmptyView(getActivity().findViewById(R.id.tv_empty_lay_today_beat) );
            lv_route_ret_list.setAdapter(retailerAdapter);
            retailerAdapter.notifyDataSetChanged();


            et_name_search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence cs, int i, int i1, int i2) {
                    retailerAdapter.getFilter(selectedSearchCode).filter(cs); //Filter from my adapter

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }




        if(Constants.BoolMoreThanOneRoute){
            ll_route_name_line.setVisibility(View.GONE);
            tv_route_name.setVisibility(View.GONE);
            spSearchCode.setVisibility(View.GONE);
            et_name_search.setVisibility(View.GONE);
        }else{
            ll_route_name_line.setVisibility(View.VISIBLE);
            tv_route_name.setVisibility(View.VISIBLE);

                tv_route_name.setText(getString(R.string.lbl_beat_name)+" "+getString(R.string.str_colon)+" "+routeName);
        }


        if(alRSCHList !=null && alRSCHList.size()>0) {
            if (alRSCHList.size() < 1) {
                tvEmptyLay.setVisibility(View.VISIBLE);
            } else
                tvEmptyLay.setVisibility(View.GONE);
        }else{
            tvEmptyLay.setVisibility(View.VISIBLE);
        }
    }

    private void loadSpinner(){

        spSearchCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {
                selectedSearchCode = searchCode[position];
                et_name_search.setText("");

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });



    }
}