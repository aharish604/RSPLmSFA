package com.rspl.sf.msfa.main;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterViewInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewTypeAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.FragmentCallbackInterface;
import com.arteriatech.mutils.registration.MainMenuBean;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.mbo.SalesPersonBean;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationMenuFragment extends Fragment implements MainMenuView, AdapterViewInterface<MainMenuBean>/*, View.OnClickListener */ {

    ArrayList<MainMenuBean> mainMenuBeenList;
    private RecyclerView recyclerView;
    private MenuPresenterImpl presenter;
    private int viewType = 1;
    private SimpleRecyclerViewTypeAdapter<MainMenuBean> simpleRecyclerViewAdapter;
    private TextView tvHeaderName;
    private TextView tvHeaderMobileNo;
    private SharedPreferences sharedPreferences;
    private ImageView ivProfile;
    private FragmentCallbackInterface fragmentCallBackInterface = null;
    private NestedScrollView nestedScroll;
    private LinearLayout llProfile;
    private SalesPersonBean salesPersonBean;
    private MainMenu activity;

    public NavigationMenuFragment() {
        // Required empty public constructor
    }

    /*   @Override
       public void onAttach(Context context) {
           super.onAttach(context);
           try {
               fragmentCallBackInterface = (FragmentCallbackInterface) context;
           } catch (ClassCastException e) {
               throw new ClassCastException(context.toString()
                       + " must implement OnHeadlineSelectedListener");
           }
       }
   */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            viewType = bundle.getInt(Constants.EXTRA_COME_FROM);
            // salesPersonBean = (SalesPersonBean) bundle.getSerializable(Constants.EXTRA_SALES_PERSON);
        }
        if (getActivity() instanceof MainMenu)
            activity = (MainMenu) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getContext().getSharedPreferences(Constants.PREFS_NAME, 0);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        tvHeaderName = (TextView) view.findViewById(R.id.tvHeaderName);
        tvHeaderMobileNo = (TextView) view.findViewById(R.id.tvHeaderMobileNo);
        ivProfile = (ImageView) view.findViewById(R.id.ivProfile);
        llProfile = (LinearLayout) view.findViewById(R.id.llProfile);
        nestedScroll = (NestedScrollView) view.findViewById(R.id.nestedScroll);
        //llProfile.setOnClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewTypeAdapter<MainMenuBean>(getContext(), R.layout.nav_menu_item, this, null, null);
        recyclerView.setAdapter(simpleRecyclerViewAdapter);
        presenter = new MenuPresenterImpl(getContext(), new MenuModelImpl(), viewType, this);
    }

    private void setHeader(List<SalesPersonBean> salesPersonBeanList) {

        if (!salesPersonBeanList.isEmpty())
            salesPersonBean = salesPersonBeanList.get(0);
        if (salesPersonBean != null) {
            tvHeaderName.setText(salesPersonBean.getFirstName() + " " + salesPersonBean.getLastName());
            tvHeaderMobileNo.setText(salesPersonBean.getMobileNo());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //onItemLoaded(mainMenuBeenList);
        presenter.onActivityCreated();
    }

    @Override
    public void onRefresh() {
        presenter.onActivityCreated();
    }

    @Override
    public void onItemLoaded(ArrayList<MainMenuBean> mainMenuBeenList, List<SalesPersonBean> salesPersonList) {
        simpleRecyclerViewAdapter.refreshAdapter(mainMenuBeenList);
        setHeader(salesPersonList);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                nestedScroll.scrollTo(0,nestedScroll.getTop());
//                nestedScroll.fullScroll(View.FOCUS_UP);
                nestedScroll.scrollTo(0, 0);
            }
        }, 200);

    }

    @Override
    public void showMessage(String message) {
        UtilConstants.dialogBoxWithCallBack(getContext(), "", message, "Ok", "", false, null);
    }


    @Override
    public void onItemClick(MainMenuBean mainMenuBean, View view, int position) {
        if (activity != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.EXTRA_BEAN, mainMenuBean);
            bundle.putInt(Constants.EXTRA_POS, mainMenuBean.getId());
            activity.fragmentCallBack(mainMenuBean.getMenuName(), bundle);
        }
    }


    @Override
    public int getItemViewType(int position, ArrayList<MainMenuBean> arrayList) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, View viewItem) {
        return new MainMenuVH(viewItem);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, MainMenuBean mainMenuBean, ArrayList<MainMenuBean> arrayList) {
        boolean isLineDisplay = false;
        boolean isTitleDisplay = false;
        int totalSize = arrayList.size();
        String currentGroup = mainMenuBean.isTitleFlag();
        String curentTitle = mainMenuBean.getItemType();
        if (position < totalSize - 1) {
            String ftrType = arrayList.get(position + 1).isTitleFlag();
            if (currentGroup != null && ftrType != null && !currentGroup.equalsIgnoreCase(ftrType)) {
                isLineDisplay = true;
            }
        }

        if (mainMenuBean.getId() == 1 || mainMenuBean.getId() == 18 || mainMenuBean.getId() == 19) {
            ((MainMenuVH) holder).ivMenuImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.secondaryColor), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        if (position > -1) {
            if (position == 0) {
                if (!TextUtils.isEmpty(mainMenuBean.getItemType())) {
                    isTitleDisplay = true;
                }
            } else {
                if (position < totalSize - 1) {
                    String ftrItem = arrayList.get(position - 1).getItemType();
                    String ftrType = arrayList.get(position - 1).isTitleFlag();
                    if (!TextUtils.isEmpty(ftrItem) && !TextUtils.isEmpty(curentTitle)) {
                        if (currentGroup != null && ftrType != null && !currentGroup.equalsIgnoreCase(ftrType)) {
                            isTitleDisplay = true;
                        }
                    } else {
                        isTitleDisplay = false;
                    }
                }
            }
        }
        if (isLineDisplay) {
            ((MainMenuVH) holder).viewLine.setVisibility(View.VISIBLE);
        } else
            ((MainMenuVH) holder).viewLine.setVisibility(View.GONE);
        if (isTitleDisplay)
            ((MainMenuVH) holder).tvGroupTitle.setVisibility(View.VISIBLE);
        else
            ((MainMenuVH) holder).tvGroupTitle.setVisibility(View.GONE);
        ((MainMenuVH) holder).tvMenuText.setText(mainMenuBean.getMenuName());

        ((MainMenuVH) holder).tvGroupTitle.setText(mainMenuBean.getItemType());
        try {
            ((MainMenuVH) holder).ivMenuImage.setImageDrawable(ContextCompat.getDrawable(getContext(), mainMenuBean.getMenuImage()));
//            ((MainMenuVH) holder).ivMenuImage.setImageDrawable(ContextCompat.getDrawable(getContext(),mainMenuBean.getMenuImage()));

        } catch (Exception e) {
            e.printStackTrace();
            ((MainMenuVH) holder).ivMenuImage.setImageResource(mainMenuBean.getMenuImage());
        }

    }

   /* @Override
    public void onClick(View v) {
        switch (v.getId()) {
          *//*  case R.id.llProfile:
//                presenter.onItemClick(v, 9);
                if (fragmentCallBackInterface != null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.EXTRA_POS, 9);
                    fragmentCallBackInterface.fragmentCallBack(getString(R.string.my_profile), bundle);
                }
                break;*//*
        }
    }*/
}
