package com.rspl.sf.msfa.main;

import android.content.Context;

import com.arteriatech.mutils.registration.MainMenuBean;

import java.util.ArrayList;

/**
 * Created by e10860 on 2/1/2018.
 */

public interface MenuModel {
    interface OnFinishedListener {
        void onFinished(ArrayList<MainMenuBean> mainMenuBeenList);
    }
    void findItems(Context mContext, OnFinishedListener listener, int viewType);
}
