package com.rspl.sf.msfa.routeplan;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.adapter.OtherRoutePlanAdapter;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.mbo.CustomerBean;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10526 on 11-12-2016.
 *
 */

public class OtherRoutePlanFragment extends Fragment implements TextWatcher {
    String  mStrRouteType ="";
    EditText et_name_search;
    private ArrayList<CustomerBean> alOtherRSCHList =new ArrayList<>();
    private OtherRoutePlanAdapter retailerAdapter = null;
    ListView lv_route_ret_list = null;
    TextView tvEmptyLay_other = null;
    View myInflatedView;
    String [] searchArray = {"Name","Code","City"};
    String [] searchCode = {"1","2","3"};
    Spinner spSearchCode;
    String selectedSearchCode="";

    public OtherRoutePlanFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mStrRouteType = getArguments().getString(Constants.RouteType);
        myInflatedView = inflater.inflate(R.layout.fragment_other_beat_plan, container,false);
        onInitUI(myInflatedView);
        LoadingData();

        return myInflatedView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }

    /*
               * TODO This method initialize UI
               */
    private void onInitUI(View myInflatedView){
        et_name_search = (EditText)myInflatedView.findViewById(R.id.et_name_search_otherroute_paln);
        et_name_search.setVisibility(View.GONE);
        spSearchCode = (Spinner)myInflatedView.findViewById(R.id.spnr_customer_search_routeplan_list);
        spSearchCode.setVisibility(View.GONE);
        et_name_search.setHint(getResources().getString(R.string.lbl_search_by_beat_name));
        lv_route_ret_list = (ListView) myInflatedView.findViewById(R.id.lv_route_ret_list);
        tvEmptyLay_other = (TextView)myInflatedView.findViewById(R.id.tv_empty_lay_other_beat);

        ArrayAdapter<String> searchadapter = new ArrayAdapter<>(getActivity(), R.layout.custom_textview, searchArray);
        searchadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSearchCode.setAdapter(searchadapter);

        et_name_search.addTextChangedListener(this);
    }
    /*
              * TODO Get Route List
              */
    private  void getRouteList(){
        try {
                String routeQry = Constants.RouteSchedules ;
                alOtherRSCHList = OfflineManager.getRetailerListForOtherRoute1(routeQry);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        Constants.BoolOtherBeatLoaded = true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence cs, int start, int before, int count) {
        retailerAdapter.getFilter().filter(cs); //Filter from my adapter
        retailerAdapter.notifyDataSetChanged(); //Update my view
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private void onDisplyOtherRoute(){
        retailerAdapter = new OtherRoutePlanAdapter( myInflatedView, alOtherRSCHList,mStrRouteType, tvEmptyLay_other);
        lv_route_ret_list.setEmptyView(getActivity().findViewById(R.id.tv_empty_lay_other_beat) );
        lv_route_ret_list.setAdapter(retailerAdapter);
        retailerAdapter.notifyDataSetChanged();

        if(alOtherRSCHList !=null && alOtherRSCHList.size()>0) {
            if (alOtherRSCHList.size() < 1) {
                tvEmptyLay_other.setVisibility(View.VISIBLE);
            } else
                tvEmptyLay_other.setVisibility(View.GONE);
        }else{
            tvEmptyLay_other.setVisibility(View.VISIBLE);
        }
    }

    private void LoadingData() {
        try {
            new AsynLoadOtherBeats().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    /*AsyncTask to get Route Plans*/
    private class AsynLoadOtherBeats extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... params) {
            getRouteList();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            loadSpinner();
            onDisplyOtherRoute();

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
}
