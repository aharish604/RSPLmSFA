package com.rspl.sf.msfa.dealerstockprice;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.ActionBarView;

public class DealerStockPriceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_stock_price);
        ActionBarView.initActionBarView(this, true, getString(R.string.title_dealer_stocks_price));

    }
}
