package com.rspl.sf.msfa.adapter;

import androidx.fragment.app.Fragment;

/**
 * Created by e10742 on 11-12-2016.
 */

public class FragmentWithTitleBean {
    Fragment fragment = null;
    String title = "";

    public FragmentWithTitleBean(Fragment fragment, String title) {
        this.fragment = fragment;
        this.title = title;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
