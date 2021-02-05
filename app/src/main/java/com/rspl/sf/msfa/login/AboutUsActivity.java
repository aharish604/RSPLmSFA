package com.rspl.sf.msfa.login;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;

/**
 * 
 * This class checks weather when apk is released date and company information.
 *
 */
public class AboutUsActivity extends AppCompatActivity{
	 
	TextView aboutDate,aboutversion; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Initialize action bar with back button(true)
		//ActionBarView.initActionBarView(this, true,getString(R.string.title_about_us));
		setContentView(R.layout.aboutus_activity);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_about_us), 0);
		aboutDate = (TextView) findViewById(R.id.aboutdate);
		aboutversion = (TextView) findViewById(R.id.aboutversion);
		Button back = (Button) findViewById(R.id.back);

		aboutversion.setText(Constants.About_Version);
		aboutDate.setText(Constants.Last_Relese_Date);
		   
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				break;
		}
		return true;
	}

}
