package com.rspl.sf.msfa.socreate.stepTwo;

import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by e10769 on 30-06-2017.
 */

public interface SOCrtStpTwoPresenter {
    void onStart();

    void onDestroy();

    boolean onSearch(String searchText, Object objects);

    void onSearch(String searchText);

    void validateItem(int activityRedirectType, RecyclerView recyclerView);

    void getCheckedCount();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onFilter();
    void startFilter(int requestCode, int resultCode, Intent data);
}
