package com.rspl.sf.msfa.main;


import com.rspl.sf.msfa.mbo.SalesPersonBean;

import java.util.List;

/**
 * Created by e10860 on 1/31/2018.
 */

public interface MenuPresenter {

    List<SalesPersonBean> setSideMenuData();

    void onActivityCreated();

}
