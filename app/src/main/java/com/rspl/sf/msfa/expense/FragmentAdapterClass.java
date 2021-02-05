package com.rspl.sf.msfa.expense;

/**
 * Created by e10854 on 18-10-2017.
 */


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


/**
 * Created by JUNED on 5/30/2016.
 */
public class FragmentAdapterClass extends FragmentStatePagerAdapter {

    int TabCount=2;


    public FragmentAdapterClass(FragmentManager fragmentManager,int tabCount) {
        super(fragmentManager);
        this.TabCount=tabCount;



    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:

                ExpenseListFragment tab2 = new ExpenseListFragment();
                return tab2;


            case 1:

                DeviceExpenseListFragment tab1 = new DeviceExpenseListFragment();

                return tab1;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return TabCount;
    }
}
