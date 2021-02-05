package com.rspl.sf.msfa.sync;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class SyncHistoryActivity extends AppCompatActivity {

	private LinearLayout llsyn;
	TextView syncHist;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		//Initialize action bar with back button(true)
		//ActionBarView.initActionBarView(this, true,getString(R.string.sync_hist));
		setContentView(R.layout.activity_sync_history);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.sync_hist), 0);
		onInitUI();
		getSyncHistory();
	}
	/*
               * TODO This method initialize UI
               */
	private void onInitUI(){
		llsyn = (LinearLayout) findViewById(R.id.llsyn);
		syncHist = (TextView) findViewById(R.id.textViewSyncHistory);
	}
	/*
	 TODO This method get Collections last sync time and display
	     */
	private void getSyncHistory() {
		llsyn.removeAllViews();

		TableLayout table = (TableLayout) LayoutInflater.from(this).inflate(
				R.layout.item_table, null);
		try {
			LinearLayout row1 = (LinearLayout) LayoutInflater.from(this).inflate(
					R.layout.table_layout_synchist, null);
			((TextView) row1.findViewById(R.id.tv_MatCodeHeader))
					.setText(Constants.Collections);
			((TextView) row1.findViewById(R.id.tv_MatDescHeader))
					.setText(Constants.time_stamp);
			table.addView(row1);

			/*
			 ToDO Get collection last sync time
			 */
			Cursor syncHistCursor = SyncHist.getInstance().findAllSyncHist();

			if (syncHistCursor.getCount() > 0) {
				syncHist.setVisibility(View.GONE);
				int i = 0;
				while (syncHistCursor.moveToNext()) {

					LinearLayout row = (LinearLayout) LayoutInflater.from(this)
							.inflate(R.layout.table_layot_without_bc, null);
					((TextView) row.findViewById(R.id.tvcode))
							.setText(syncHistCursor.getString(syncHistCursor
									.getColumnIndex(Constants.Collections)));
					String deviceDateFormat = syncHistCursor.getString(syncHistCursor
							.getColumnIndex(Constants.TimeStamp)) != null ? (syncHistCursor
							.getString(syncHistCursor
									.getColumnIndex(Constants.TimeStamp))) : "";
					((TextView) row.findViewById(R.id.tvDesc))
							.setText(syncHistCursor.getString(syncHistCursor
									.getColumnIndex(Constants.TimeStamp)) != null ? (syncHistCursor
									.getString(syncHistCursor
											.getColumnIndex(Constants.TimeStamp))) : "");
					table.addView(row);
					i++;
				}
				syncHistCursor.close();
				syncHistCursor.deactivate();
				llsyn.addView(table);
			} else {
				syncHist.setVisibility(View.VISIBLE);
			}

		} catch (Exception e) {
			LogManager.writeLogError(Constants.getSyncHistory  + e.getMessage());
		}

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

	public static String convertDateIntoDeviceFormat(Context context, String dateString) {
		java.text.DateFormat dateFormat1 = DateFormat.getDateFormat(context);
		String pattern = ((SimpleDateFormat)dateFormat1).toLocalizedPattern();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
		Date convertedDate = new Date();

		try {
			convertedDate = dateFormat.parse(dateString);
		} catch (ParseException var7) {
			var7.printStackTrace();
		}

		String dateInDeviceFormat = (String)DateFormat.format(pattern, convertedDate);
		return dateInDeviceFormat;
	}


}
