package com.rspl.sf.msfa.collectionPlan;

import android.content.Intent;

/**
 * Created by e10860 on 2/16/2018.
 */

public interface RTGSCurrentPresenter {
   void onStart();
   void onDestroy();
   void onSaveData(String saveType, String comingFrom);
   void onActivityResult(int requestCode, int resultCode, Intent data);
   void onRefresh();
}
