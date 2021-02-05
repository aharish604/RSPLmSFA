package com.rspl.sf.msfa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.complaint.ComplaintListActivity;
import com.rspl.sf.msfa.creditlimit.CreditLimitActivity;
import com.rspl.sf.msfa.dealertargets.DealerTargetActivity;
import com.rspl.sf.msfa.feedback.FeedBackListActivity;
import com.rspl.sf.msfa.grreport.GRReportListActivity;
import com.rspl.sf.msfa.reports.CollectionHistoryActivity;
import com.rspl.sf.msfa.reports.NewOutstandingHistoryActivity;
import com.rspl.sf.msfa.reports.NewSalesOrderActivity;
import com.rspl.sf.msfa.reports.OutstandingHistoryActivity;
import com.rspl.sf.msfa.reports.distributorTrend.DistributorTrendsActivity;
import com.rspl.sf.msfa.reports.invoicelist.InvoiceListActivity;
import com.rspl.sf.msfa.reports.salesorder.header.SalesOrderHeaderListActivity;
import com.rspl.sf.msfa.returnOrder.ReturnOrderActivity;
import com.rspl.sf.msfa.visit.MerchindisingListActivity;
import com.rspl.sf.msfa.visit.MustSellActivity;


public class ReportsFragment extends Fragment{

    String mStrCPGUID="",mStrRetID="",mStrRetName="",mStrCPGUID36="", mStrRetUID = "";
    private GridView gvRetailerDetails;

    private final String[] mArrStrIconNames = Constants.reportsArray;
    private int[] mArrIntMinVisibility ;

    public int[] mArrIntIconPosition = Constants.IconPositionReportFragment;

    public ReportsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mStrCPGUID = getArguments().getString(Constants.CPGUID32);
        mStrRetID = getArguments().getString(Constants.CPNo);
        mStrRetName = getArguments().getString(Constants.RetailerName);
        mStrCPGUID36 = getArguments().getString(Constants.CPGUID);
        mStrRetUID = getArguments().getString(Constants.CPUID);

        // Inflate the layout for this fragment
        View myInflatedView = inflater.inflate(R.layout.fragment_ret_reports, container,false);



        onInitUI(myInflatedView);
        setIconVisibility();
        setValuesToUI(myInflatedView);


        return myInflatedView;
    }
    /*
             * TODO This method initialize UI
             */
    private void onInitUI( View myInflatedView){
        gvRetailerDetails = (GridView)myInflatedView.findViewById(R.id.gv_retailer_details);
    }
/*
    TODO This method set values to UI
    */
    private void setValuesToUI(View myInflatedView){
        gvRetailerDetails.setAdapter(new ReportsAdapter(myInflatedView.getContext()));
    }

    class ReportsAdapter extends BaseAdapter {


        private Context mContext;
        ReportsAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }
        @Override
        public int getCount() {
            int count = 0;
            for (int aMinVisibility : mArrIntMinVisibility) {
                if (aMinVisibility == 1) {
                    count++;
                }
            }
            return count;
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int mIntIconPos = mArrIntIconPosition[position];
            View view;
            if (convertView == null) {
                LayoutInflater liRelatedLinks = getActivity().getLayoutInflater();
                view = liRelatedLinks.inflate(R.layout.retailer_menu_inside, parent,false);
                view.requestFocus();
                final TextView tvIconName = (TextView) view
                        .findViewById(R.id.icon_text);
                tvIconName.setTextColor(getResources().getColor(R.color.icon_text_blue));
                tvIconName.setText(mArrStrIconNames[mIntIconPos]);
                final ImageView ivIconId = (ImageView) view
                        .findViewById(R.id.ib_must_sell);
                ivIconId.setColorFilter(ContextCompat.getColor(getContext(), R.color.secondaryColor), android.graphics.PorterDuff.Mode.SRC_IN);
                if (mIntIconPos == 0) {
                    ivIconId.setImageResource(R.drawable.ic_shopping_cart_black_24dp);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onSalesOrderList(); //so list
                        }
                    });

                }else if (mIntIconPos == 1) {
                    ivIconId.setImageResource(R.drawable.collection_history);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onCollectionHistory();
                        }
                    });
                } else if (mIntIconPos == 2) {
                    ivIconId.setImageResource(R.drawable.ic_account_balance_wallet_black_24dp);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onOutHistory();
                        }
                    });
                } else if (mIntIconPos == 3) {
                    ivIconId.setImageResource(R.drawable.ic_must_sell);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onSegmentedMaterials("01");
                        }
                    });
                } else if (mIntIconPos == 4) {
                    ivIconId.setImageResource(R.drawable.ic_focused_product);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onSegmentedMaterials("02");
                        }
                    });
                } else if (mIntIconPos == 5) {
                    ivIconId.setImageResource(R.drawable.ic_new_product);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onSegmentedMaterials("03");
                        }
                    });
                } else if (mIntIconPos == 6) {
                    ivIconId.setImageResource(R.drawable.ic_merch_list);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onMerchindisingList();
                        }
                    });
                }
                else if (mIntIconPos == 7) {
                    ivIconId.setImageResource(R.drawable.ic_account_balance_wallet_black_24dp);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onNewInvoiceHistoryList();//single line item
                        }
                    });
                } else if (mIntIconPos == 8) {
                    ivIconId.setImageResource(R.drawable.ic_credit_status_black_24dp);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onCreditLimit();
                        }
                    });
                }else if (mIntIconPos == 9) {
                    ivIconId.setImageResource(R.drawable.ic_my_targets);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onDealerTargets();
                        }
                    });
                }
                else if (mIntIconPos == 9) {
//                    ivIconId.setImageResource(R.drawable.ic_merchindising_snap);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onMerchindisingList();
                        }
                    });
                }
                else if (mIntIconPos == 10) {
                    ivIconId.setImageResource(R.drawable.ic_feedback);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onFeedBackList();
                        }
                    });
                }
                else if (mIntIconPos == 11) {
//                    ivIconId.setImageResource(R.drawable.ic_retailer_trends_medium);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onFeedBackList();
                        }
                    });
                } else if (mIntIconPos == 12) {
                    ivIconId.setImageResource(R.drawable.ic_sample_disp);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onComplaintList();
                        }
                    });
                }else if (mIntIconPos == 13) {
                    ivIconId.setImageResource(R.drawable.ic_shopping_cart_black_24dp);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onNewSalesOrderList();
                        }
                    });
                }else if (mIntIconPos == 14) {
                    ivIconId.setImageResource(R.drawable.ic_account_balance_wallet_black_24dp);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onInvoiceHistory(); // multiple line item
                        }
                    });
                }else if (mIntIconPos == 15) {
                    ivIconId.setImageResource(R.drawable.ic_description_black_24dp);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onNewOutstandingList();
                        }
                    });
                }else if (mIntIconPos == 16) {
                    ivIconId.setImageResource(R.drawable.ic_trending_up_black_24dp);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent contractIntent= new Intent(getActivity(),DistributorTrendsActivity.class);
                            contractIntent.putExtra(Constants.EXTRA_CUSTOMER_NO,mStrRetID );
                            contractIntent.putExtra(Constants.EXTRA_CUSTOMER_NAME, mStrRetName);
                            contractIntent.putExtra(Constants.CPGUID, mStrCPGUID);
                            contractIntent.putExtra(Constants.CPUID, mStrRetUID);
                            startActivity(contractIntent);
                        }
                    });
                }else if (mIntIconPos == 17) {
                    ivIconId.setImageResource(R.drawable.ic_shopping_cart_black_24dp);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent contractIntent= new Intent(getActivity(),ReturnOrderActivity.class);
                            contractIntent.putExtra(Constants.EXTRA_CUSTOMER_NO,mStrRetID );
                            contractIntent.putExtra(Constants.EXTRA_COME_FROM, 2);
                            contractIntent.putExtra(Constants.EXTRA_CUSTOMER_NAME, mStrRetName);
                            startActivity(contractIntent);
                        }
                    });
                }else if (mIntIconPos == 18) {
                    ivIconId.setImageResource(R.drawable.ic_shopping_cart_black_24dp);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent contractIntent= new Intent(getActivity(),ReturnOrderActivity.class);
                            contractIntent.putExtra(Constants.EXTRA_CUSTOMER_NO,mStrRetID );
                            contractIntent.putExtra(Constants.EXTRA_COME_FROM, 1);
                            contractIntent.putExtra(Constants.EXTRA_CUSTOMER_NAME, mStrRetName);
                            startActivity(contractIntent);
                        }
                    });
                }else if (mIntIconPos == 19) {
                    ivIconId.setImageResource(R.drawable.ic_account_balance_wallet_black_24dp);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onGRReportList();
                        }
                    });
                }
                view.setId(position);
            } else {
                view = convertView;
            }
            return view;
        }

    }

    private void onComplaintList() {
        Intent intentVisit = new Intent(getActivity(), ComplaintListActivity.class);
        intentVisit.putExtra(Constants.CPNo, mStrRetID);
        intentVisit.putExtra(Constants.RetailerName, mStrRetName);
        intentVisit.putExtra(Constants.CPGUID, mStrCPGUID);
        intentVisit.putExtra(Constants.CPUID, mStrRetUID);
        intentVisit.putExtra(Constants.comingFrom, Constants.RetDetails);
        intentVisit.putExtra(Constants.EXTRA_TAB_POS, 0);
        startActivity(intentVisit);
    }
    private void onNewSalesOrderList() {
        Intent intentVisit = new Intent(getActivity(), NewSalesOrderActivity.class);
        intentVisit.putExtra(Constants.CPNo, mStrRetID);
        intentVisit.putExtra(Constants.RetailerName, mStrRetName);
        intentVisit.putExtra(Constants.CPGUID, mStrCPGUID);
        intentVisit.putExtra(Constants.CPUID, mStrRetUID);
        intentVisit.putExtra(Constants.comingFrom, Constants.RetDetails);
        intentVisit.putExtra(Constants.EXTRA_TAB_POS, 0);
        startActivity(intentVisit);
    }
    private void onNewOutstandingList() {
        Intent intentVisit = new Intent(getActivity(), NewOutstandingHistoryActivity.class);
        intentVisit.putExtra(Constants.CPNo, mStrRetID);
        intentVisit.putExtra(Constants.RetailerName, mStrRetName);
        intentVisit.putExtra(Constants.CPGUID, mStrCPGUID);
        intentVisit.putExtra(Constants.CPUID, mStrRetUID);
        intentVisit.putExtra(Constants.comingFrom, Constants.RetDetails);
        intentVisit.putExtra(Constants.EXTRA_TAB_POS, 0);
        startActivity(intentVisit);
    }
//    private void onNewInvoiceHistoryList() {
//        Intent intentVisit = new Intent(getActivity(), NewInvoiceHistoryActivity.class);
//        intentVisit.putExtra(Constants.CPNo, mStrRetID);
//        intentVisit.putExtra(Constants.RetailerName, mStrRetName);
//        intentVisit.putExtra(Constants.CPGUID, mStrCPGUID);
//        intentVisit.putExtra(Constants.CPUID, mStrRetUID);
//        intentVisit.putExtra(Constants.comingFrom, Constants.RetDetails);
//        intentVisit.putExtra(Constants.EXTRA_TAB_POS, 0);
//        startActivity(intentVisit);
//    }
    private void onNewInvoiceHistoryList() {
        Intent intentInvoiceHistoryActivity = new Intent(getActivity(), InvoiceListActivity.class);
        intentInvoiceHistoryActivity.putExtra(Constants.CPNo, mStrRetID);
        intentInvoiceHistoryActivity.putExtra(Constants.RetailerName,mStrRetName);
        intentInvoiceHistoryActivity.putExtra(Constants.CPGUID, mStrCPGUID);
        intentInvoiceHistoryActivity.putExtra(Constants.CPUID, mStrRetUID);
        intentInvoiceHistoryActivity.putExtra(Constants.isInvoiceItemsEnabled, false);
        startActivity(intentInvoiceHistoryActivity);
    }

    private void onSalesOrderList()
    {
//        Intent intentVisit = new Intent(getActivity(), SalesOrderList.class);
        Intent intentVisit = new Intent(getActivity(), SalesOrderHeaderListActivity.class);
        intentVisit.putExtra(Constants.CPNo, mStrRetID);
        intentVisit.putExtra(Constants.CustomerName, mStrRetName);
        intentVisit.putExtra(Constants.CPGUID, mStrCPGUID);
        intentVisit.putExtra(Constants.CPUID, mStrRetUID);
        intentVisit.putExtra(Constants.comingFrom, 0);
        intentVisit.putExtra(Constants.EXTRA_TAB_POS, 0);
        startActivity(intentVisit);
    }

    private void onGRReportList()
    {
        Intent intentVisit = new Intent(getActivity(), GRReportListActivity.class);
        intentVisit.putExtra(Constants.CPNo, mStrRetID);
        intentVisit.putExtra(Constants.CustomerName, mStrRetName);
        intentVisit.putExtra(Constants.CPGUID, mStrCPGUID);
        intentVisit.putExtra(Constants.CPUID, mStrRetUID);
        intentVisit.putExtra(Constants.comingFrom, 0);
        intentVisit.putExtra(Constants.EXTRA_TAB_POS, 0);
        startActivity(intentVisit);
    }

    /*
    ToDo navigate to OutstandingHistory activity
   */
    private void onOutHistory(){
        Intent intentVisit = new Intent(getActivity(), OutstandingHistoryActivity.class);
        intentVisit.putExtra(Constants.CPNo, mStrRetID);
        intentVisit.putExtra(Constants.RetailerName, mStrRetName);
        intentVisit.putExtra(Constants.CPGUID, mStrCPGUID);
        intentVisit.putExtra(Constants.CPUID, mStrRetUID);
        intentVisit.putExtra(Constants.comingFrom, Constants.RetDetails);
        startActivity(intentVisit);
    }
    /*
    ToDo navigate to CollectionHistory activity
   */
    private void onCollectionHistory() {
        Intent intentCollHisActivity = new Intent(getActivity(), CollectionHistoryActivity.class);
        intentCollHisActivity.putExtra(Constants.CPNo, mStrRetID);
        intentCollHisActivity.putExtra(Constants.RetailerName,mStrRetName);
        intentCollHisActivity.putExtra(Constants.CPGUID, mStrCPGUID);
        intentCollHisActivity.putExtra(Constants.CPUID, mStrRetUID);
        startActivity(intentCollHisActivity);
    }
    /*
    ToDo navigate to BillHistory activity
   */
    private void onInvoiceHistory() {
//        Intent intentInvoiceHistoryActivity = new Intent(getActivity(),
//                InvoiceHistoryActivity.class);
//        intentInvoiceHistoryActivity.putExtra(Constants.CPNo, mStrRetID);
//        intentInvoiceHistoryActivity.putExtra(Constants.RetailerName,mStrRetName);
//        intentInvoiceHistoryActivity.putExtra(Constants.CPGUID, mStrCPGUID);
//        intentInvoiceHistoryActivity.putExtra(Constants.CPUID, mStrRetUID);
//        startActivity(intentInvoiceHistoryActivity);

        Intent intentInvoiceHistoryActivity = new Intent(getActivity(),
                InvoiceListActivity.class);
        intentInvoiceHistoryActivity.putExtra(Constants.CPNo, mStrRetID);
        intentInvoiceHistoryActivity.putExtra(Constants.RetailerName,mStrRetName);
        intentInvoiceHistoryActivity.putExtra(Constants.CPGUID, mStrCPGUID);
        intentInvoiceHistoryActivity.putExtra(Constants.CPUID, mStrRetUID);
        intentInvoiceHistoryActivity.putExtra(Constants.isInvoiceItemsEnabled, true);
        startActivity(intentInvoiceHistoryActivity);
    }

    /*
    ToDo navigate to Credit limit activity
   */
    private void onCreditLimit() {
        Intent intentCreditLimit = new Intent(getActivity(),
                CreditLimitActivity.class);
        intentCreditLimit.putExtra(Constants.CPNo, mStrRetID);
        intentCreditLimit.putExtra(Constants.RetailerName,mStrRetName);
        intentCreditLimit.putExtra(Constants.CPGUID, mStrCPGUID);
        intentCreditLimit.putExtra(Constants.CPUID, mStrRetUID);
        startActivity(intentCreditLimit);
    }

    private void onDealerTargets(){
        startActivity(new Intent(getActivity(), DealerTargetActivity.class));
    }

    /*
  ToDo navigate to Must sell ,New Product ,Focused Product
 */
    private void onSegmentedMaterials(String segmentedType) {
        Intent intentNewProdListActivity = new Intent(getActivity(),
                MustSellActivity.class);

        intentNewProdListActivity.putExtra(Constants.CPNo, mStrRetID);
        intentNewProdListActivity.putExtra(Constants.RetailerName,mStrRetName);
        intentNewProdListActivity.putExtra(Constants.CPGUID, mStrCPGUID);
        intentNewProdListActivity.putExtra(Constants.CPUID, mStrRetUID);
        if(segmentedType.equalsIgnoreCase("01")){
            intentNewProdListActivity.putExtra(Constants.ID, segmentedType);
            intentNewProdListActivity.putExtra(Constants.Description, Constants.MustSellProduct);
        }else if(segmentedType.equalsIgnoreCase("02")){
            intentNewProdListActivity.putExtra(Constants.ID, segmentedType);
            intentNewProdListActivity.putExtra(Constants.Description, Constants.FocusedProduct);
        }else if(segmentedType.equalsIgnoreCase("03")){
            intentNewProdListActivity.putExtra(Constants.ID, segmentedType);
            intentNewProdListActivity.putExtra(Constants.Description, Constants.NewLaunchedProduct);
        }

        startActivity(intentNewProdListActivity);
    }

    /*
   ToDo navigate to Merchandising List activity
  */
    private void onMerchindisingList() {
        Intent intentMerchListActivity = new Intent(getActivity(),
                MerchindisingListActivity.class);
        intentMerchListActivity.putExtra(Constants.CPNo, mStrRetID);
        intentMerchListActivity.putExtra(Constants.RetailerName,mStrRetName);
        intentMerchListActivity.putExtra(Constants.CPGUID, mStrCPGUID);
        intentMerchListActivity.putExtra(Constants.CPUID, mStrRetUID);
        startActivity(intentMerchListActivity);
    }

    private void onFeedBackList() {
        Intent intentfeddbackActivity = new Intent(getActivity(),
                FeedBackListActivity.class);
        intentfeddbackActivity.putExtra(Constants.CPNo, mStrRetID);
        intentfeddbackActivity.putExtra(Constants.RetailerName,mStrRetName);
        intentfeddbackActivity.putExtra(Constants.CPGUID, mStrCPGUID);
        intentfeddbackActivity.putExtra(Constants.CPUID, mStrRetUID);
        startActivity(intentfeddbackActivity);
    }


    /*
    ToDo enable icons based on authorization tcodes
     */
    private void setIconVisibility() {
        mArrIntMinVisibility = Constants.IconVisibiltyReportFragment;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        Constants.setIconVisibiltyReports(sharedPreferences,mArrIntMinVisibility);
        int iconCount = 0;
        for (int iconVisibleCount = 0; iconVisibleCount < mArrIntMinVisibility.length; iconVisibleCount++) {
            if (mArrIntMinVisibility[iconVisibleCount] == 1) {
                mArrIntIconPosition[iconCount] = iconVisibleCount;
                iconCount++;
            }
        }

    }

}
