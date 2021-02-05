package com.rspl.sf.msfa.reports;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.interfaces.OnClickInterface;
import com.rspl.sf.msfa.mbo.SalesOrderBean;
import com.rspl.sf.msfa.mbo.SalesOrderItemBean;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewSalesOrderListFragment extends Fragment implements OnClickInterface {
    List<SalesOrderBean> SalesOrderBeanList = new ArrayList<>();
    NewSalesOrderListAdapter returnOrderListAdapter;
    private RecyclerView recyclerView;
    private TextView noDataFound;
    private EditText edSearch;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";
    private String mStrBundleCPGUID = "";
//    private String mStrComingFrom = "";
    private TextView tvOrderValue;
    private TextView tvOrderId;
    private TextView tvOrderDate;
    private int tabPosition = 0;
    Bundle bundleExtras;

    public NewSalesOrderListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundleExtras = this.getArguments();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
//          mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
            tabPosition = bundleExtras.getInt(Constants.EXTRA_TAB_POS, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_sales_order_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        noDataFound = (TextView) view.findViewById(R.id.no_record_found);
        noDataFound.setVisibility(View.INVISIBLE);
        edSearch = (EditText) view.findViewById(R.id.ed_search);

        tvOrderId = (TextView) view.findViewById(R.id.tv_order_id);
        tvOrderDate = (TextView) view.findViewById(R.id.tv_order_date);
        tvOrderValue = (TextView) view.findViewById(R.id.tv_order_value);

        recyclerView.setHasFixedSize(true);
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        returnOrderListAdapter = new NewSalesOrderListAdapter(getContext(), SalesOrderBeanList);
        returnOrderListAdapter.onItemClick(this);
        recyclerView.setAdapter(returnOrderListAdapter);
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                returnOrderListAdapter.filter(s + "", noDataFound, recyclerView);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //get data from offline database

        if (tabPosition == Constants.TAB_POS_1) {
//            getSSSODataFromOfflineDb();
        } else {
            getSSSODataFromDataValt();
        }

    }
    /*get ssso data from data valt*/
    private void getSSSODataFromDataValt() {
        SalesOrderBeanList.clear();
        try {
            SalesOrderBeanList.clear();
            SalesOrderBeanList.addAll(OfflineManager.getSoListFromDataValt(getContext(), mStrBundleCPGUID,false));
            returnOrderListAdapter.filter("", noDataFound, recyclerView);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(View view, Object item) {
//        SalesOrderBean SalesOrderBean = SalesOrderBeanList.get(position);
        SalesOrderItemBean salesOrderItemBean=null;
        SalesOrderBean salesOrderBean = (SalesOrderBean)item;
        Intent intent = new Intent(getContext(), SalesOrderListDetailsActivity.class);
         intent.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        intent.putExtra(Constants.CPNo, mStrBundleRetID);
        intent.putExtra(Constants.RetailerName, mStrBundleRetName);
        intent.putExtra(Constants.CPUID, mStrBundleRetUID);
//        intent.putExtra(Constants.comingFrom, mStrComingFrom);
        intent.putExtra(Constants.EXTRA_TAB_POS, tabPosition);
        intent.putExtra(Constants.DeviceNo, salesOrderBean.getDeviceNo());
        intent.putExtra(Constants.EXTRA_SSRO_GUID, salesOrderBean.getSSROGUID());
        intent.putExtra(Constants.EXTRA_ORDER_DATE, salesOrderBean.getOrderDate());
        intent.putExtra(Constants.EXTRA_ORDER_IDS, salesOrderBean.getOrderNo());
        intent.putExtra(Constants.EXTRA_ORDER_AMOUNT, salesOrderBean.getTotalAmt());
        intent.putExtra(Constants.EXTRA_ORDER_SATUS, salesOrderBean.getDelvStatus());
        intent.putExtra(Constants.EXTRA_ORDER_CURRENCY, salesOrderBean.getCurrency());
        intent.putExtra(Constants.QAQty, salesOrderBean.getQAQty());


        if(tabPosition==1){

            String soQuery = Constants.SOs + "?$filter=" + Constants.CustomerNo + " eq '" + mStrBundleRetID
                    +"' and "+Constants.SONo+ " eq '" + salesOrderBean.getOrderNo()+"'";
            try {
                salesOrderItemBean =OfflineManager.getSecondarySalesOrderBean(soQuery);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            try {

                intent.putExtra(Constants.ORDER_TYPE, salesOrderItemBean.getOrderType());
                intent.putExtra(Constants.ORDER_TYPE_DESC, salesOrderItemBean.getOrderTypeDesc());

                intent.putExtra(Constants.SALESAREA, salesOrderItemBean.getSalesArea());
                intent.putExtra(Constants.SALESAREA_DESC, salesOrderItemBean.getSalesAreaDesc());

                intent.putExtra(Constants.SOLDTO, salesOrderItemBean.getSoldTo());
                intent.putExtra(Constants.SOLDTONAME, salesOrderItemBean.getSoldToName());
                intent.putExtra(Constants.SHIPPINTPOINT, salesOrderItemBean.getShippingTypeID());
                intent.putExtra(Constants.SHIPPINTPOINTDESC, salesOrderItemBean.getShippingTypeDesc());
                intent.putExtra(Constants.SHIPTO, salesOrderItemBean.getShipTo());
                intent.putExtra(Constants.SHIPTONAME, salesOrderItemBean.getShipToName());
                intent.putExtra(Constants.FORWARDINGAGENT, salesOrderItemBean.getForwardingAgent());
                intent.putExtra(Constants.FORWARDINGAGENTNAME, salesOrderItemBean.getForwardingAgentName());
                intent.putExtra(Constants.PLANT, salesOrderItemBean.getPlant());
                intent.putExtra(Constants.PLANTDESC, salesOrderItemBean.getPlantDesc());
                intent.putExtra(Constants.INCOTERM1, salesOrderItemBean.getIncoTerm1());
                intent.putExtra(Constants.INCOTERM1DESC, salesOrderItemBean.getIncoterm1Desc());
                intent.putExtra(Constants.INCOTERM2, salesOrderItemBean.getIncoterm2());
                intent.putExtra(Constants.SALESDISTRICT, salesOrderItemBean.getSalesDistrict());
                intent.putExtra(Constants.SALESDISTRICTDESC, salesOrderItemBean.getSalesDistrictDesc());
                intent.putExtra(Constants.SplProcessing, salesOrderItemBean.getSplProcessing());
                intent.putExtra(Constants.SplProcessingDesc, salesOrderItemBean.getSplProcessingDesc());
                intent.putExtra(Constants.Payterm, salesOrderItemBean.getPaymentTerm());
                intent.putExtra(Constants.PaytermDesc, salesOrderItemBean.getPaytermDesc());
                intent.putExtra(Constants.ROUTE, salesOrderItemBean.getRoute());
                intent.putExtra(Constants.ROUTEDESC, salesOrderItemBean.getRouteDesc());
                intent.putExtra(Constants.MEANSOFTRANSPORT, salesOrderItemBean.getMeansOfTranstyp());
                intent.putExtra(Constants.MEANSOFTRANSPORTDESC, salesOrderItemBean.getMeansOfTranstypDesc());
                intent.putExtra(Constants.STORAGELOC, salesOrderItemBean.getStorLoc());
                intent.putExtra(Constants.Remarks, salesOrderItemBean.getRemarks());
                intent.putExtra(Constants.DELIVERY_STATUS, salesOrderItemBean.getDelvStatus());
                startActivity(intent);
                // getActivity().finish();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }else{



            intent.putExtra(Constants.ORDER_TYPE, salesOrderBean.getOrderType());
            intent.putExtra(Constants.ORDER_TYPE_DESC, salesOrderBean.getOrderTypeDesc());

            intent.putExtra(Constants.SALESAREA, salesOrderBean.getSalesArea());
            intent.putExtra(Constants.SALESAREA_DESC, salesOrderBean.getSalesAreaDesc());

            intent.putExtra(Constants.SOLDTO, salesOrderBean.getSoldTo());
            intent.putExtra(Constants.SOLDTONAME, salesOrderBean.getSoldToName());
            intent.putExtra(Constants.SHIPPINTPOINT, salesOrderBean.getShippingTypeID());
            intent.putExtra(Constants.SHIPPINTPOINTDESC, salesOrderBean.getShippingTypeDesc());
            intent.putExtra(Constants.SHIPTO, salesOrderBean.getShipTo());
            intent.putExtra(Constants.SHIPTONAME, salesOrderBean.getShipToName());
            intent.putExtra(Constants.FORWARDINGAGENT, salesOrderBean.getForwardingAgent());
            intent.putExtra(Constants.FORWARDINGAGENTNAME, salesOrderBean.getForwardingAgentName());
            intent.putExtra(Constants.PLANT, salesOrderBean.getPlant());
            intent.putExtra(Constants.PLANTDESC, salesOrderBean.getPlantDesc());
            intent.putExtra(Constants.INCOTERM1, salesOrderBean.getIncoTerm1());
            intent.putExtra(Constants.INCOTERM1DESC, salesOrderBean.getIncoterm1Desc());
            intent.putExtra(Constants.INCOTERM2, salesOrderBean.getIncoterm2());
            intent.putExtra(Constants.SALESDISTRICT, salesOrderBean.getSalesDistrict());
            intent.putExtra(Constants.SALESDISTRICTDESC, salesOrderBean.getSalesDistrictDesc());
            intent.putExtra(Constants.SplProcessing, salesOrderBean.getSplProcessing());
            intent.putExtra(Constants.SplProcessingDesc, salesOrderBean.getSplProcessingDesc());
            intent.putExtra(Constants.Payterm, salesOrderBean.getPaymentTerm());
            intent.putExtra(Constants.PaytermDesc, salesOrderBean.getPaytermDesc());
            intent.putExtra(Constants.ROUTE, salesOrderBean.getRoute());
            intent.putExtra(Constants.ROUTEDESC, salesOrderBean.getRouteDesc());
            intent.putExtra(Constants.MEANSOFTRANSPORT, salesOrderBean.getMeansOfTranstyp());
            intent.putExtra(Constants.MEANSOFTRANSPORTDESC, salesOrderBean.getMeansOfTranstypDesc());
            intent.putExtra(Constants.STORAGELOC, salesOrderBean.getStorLoc());
            intent.putExtra(Constants.CUSTOMERPO, salesOrderBean.getPONo());
            intent.putExtra(Constants.CUSTOMERPODATE, salesOrderBean.getPODate());
            intent.putExtra(Constants.Remarks, salesOrderBean.getRemarks());
            startActivity(intent);

        }



    }
    public void updateListForSelectedStatus(ArrayList<SalesOrderBean> alUpdatedList){
        SalesOrderBeanList.clear();
        SalesOrderBeanList.addAll(alUpdatedList);

        returnOrderListAdapter.filter("", noDataFound, recyclerView);
//        returnOrderListAdapter.notifyDataSetChanged();
    }
}
