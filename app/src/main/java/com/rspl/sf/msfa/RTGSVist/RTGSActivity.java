package com.rspl.sf.msfa.RTGSVist;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.rspl.sf.msfa.R;

/**
 * Created by e10860 on 2/16/2018.
 */

public class RTGSActivity extends AppCompatActivity {

    private FrameLayout frameContainer;
    private LinearLayout llBottomLayout;
    BottomNavigationView navigation;
    private Button btnToday;
    private Button btnCurrent;
    private Button btnWeek;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtgs);
    }

}
