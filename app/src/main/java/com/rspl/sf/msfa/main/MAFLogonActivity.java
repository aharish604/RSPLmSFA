/*
package com.rspl.sf.msfa.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.MSFAApplication;
import com.rspl.sf.msfa.database.EventDataSqlHelper;
import com.rspl.sf.msfa.database.EventUserDetail;
import com.rspl.sf.msfa.login.LoginActivity;
import com.rspl.sf.msfa.registration.Configuration;
import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreContext;
import com.sap.maf.tools.logon.core.LogonCoreException;
import com.sap.maf.tools.logon.logonui.api.LogonListener;
import com.sap.maf.tools.logon.logonui.api.LogonUIFacade;
import com.sap.maf.tools.logon.manager.LogonContext;
import com.sap.maf.tools.logon.manager.LogonManager;
import com.sap.smp.rest.ClientConnection;

import java.util.Hashtable;

public class MAFLogonActivity extends Activity implements LogonListener {
	private final String TAG = MAFLogonActivity.class.getSimpleName();
	private LogonUIFacade mLogonUIFacade;
	public Context mContext;
	public MSFAApplication mApplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		EventUserDetail eventDataSqlHelper = new EventUserDetail(this);
		Constants.EventUserHandler = eventDataSqlHelper.getWritableDatabase();
		Constants.events = new EventDataSqlHelper(getApplicationContext());
		LogManager.initialize(MAFLogonActivity.this);
		mApplication = (MSFAApplication) getApplication();

		//Initialize LOGONCORE for MAF LOGON
		initializeLogonCore();

		super.onCreate(savedInstanceState);


		//STEP1: Hide MobilePlace window
		SharedPreferences prefs = getSharedPreferences(LogonCore.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor pEditor = prefs.edit();
		pEditor.putBoolean(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_MOBILEPLACE.toString(), false);
		pEditor.commit();

		// get an instance of the LogonUIFacade
		mLogonUIFacade = LogonUIFacade.getInstance();

		// set context reference 
		mContext = this;

		mLogonUIFacade.init(this, this, Constants.APP_ID);

		mLogonUIFacade.isFieldHidden(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_SUPSERVERFARMID, true);
		mLogonUIFacade.isFieldHidden(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_URLSUFFIX, true);

		mLogonUIFacade.isFieldHidden(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_MOBILEUSER, true);
		mLogonUIFacade.isFieldHidden(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_ACTIVATIONCODE, true);
		mLogonUIFacade.isFieldHidden(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_GATEWAYCLIENT, true);
		mLogonUIFacade.isFieldHidden(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_SUPSERVERDOMAIN, true);
		mLogonUIFacade.isFieldHidden(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_PINGPATH, true);
		mLogonUIFacade.isFieldHidden(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_GWONLY, true);
		mLogonUIFacade.setDefaultValue(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_SUPSERVERURL, Configuration.server_Text);
		mLogonUIFacade.setDefaultValue(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_SUPSERVERPORT, Configuration.port_Text);
		mLogonUIFacade.setDefaultValue(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_SECCONFIG, Configuration.secConfig_Text);
		mLogonUIFacade.setDefaultValue(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_PASSWORD, Configuration.pwd_text);

		// Relay server Configuration formid and url suffix
//		mLogonUIFacade.setDefaultValue(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_URLSUFFIX, suffix);
//		mLogonUIFacade.setDefaultValue(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_SUPSERVERFARMID, farm_ID);

		this.showLogonScreen();
	}

	*/
/*Initializes LogonCore for MAFLOGON*//*

	void initializeLogonCore() {
		LogonCore lgCore = LogonCore.getInstance();
		if (lgCore != null && lgCore.isStoreAvailable()) {
			LogonCoreContext lgCtx = lgCore.getLogonContext();
			if (lgCtx != null && lgCtx.isSecureStoreOpen()) {
				try {
					if (!TextUtils.isEmpty(lgCtx.getConnId())) {
						Intent intent = new Intent();
						intent.setClass(this, MainMenu.class);
						startActivity(intent);
						finish();
					}
				} catch (LogonCoreException e) {
				}
			}
		}
	}

	@Override
	public void objectFromSecureStoreForKey() {

	}

	@Override
	public void onApplicationSettingsUpdated() {

	}

	@Override
	public void onBackendPasswordChanged(boolean arg0) {

	}

	*/
/*on logon finish this method executes*//*

	@Override
	public void onLogonFinished(String message, boolean isSuccess,
								LogonContext lgContext) {
		// Logon successful - setup global request manager
		Log.v(TAG, message);

		if (isSuccess) {
			try {
				ClientConnection clientConnection = new ClientConnection(
						getApplicationContext(), Configuration.appID_Text, Constants.default_txt, Configuration.secConfig_Text,
						mApplication.getRequestManager());
				clientConnection.setConnectionProfile(true, Configuration.server_Text, Configuration.port_Text, "", "");

				LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();
				String mStrBackEndUser = lgCtx.getBackendUser();

				// get Application Connection ID
				String appConnID = LogonCore.getInstance().getLogonContext()
						.getConnId();
				Log.d(TAG, Constants.logon_finished_appcid + appConnID);
				Log.d(TAG, Constants.logon_finished_aendpointurl + lgContext.getEndPointUrl());

				SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME,
						0);
				String strPref = settings.getString(Constants.username, null);
				if (strPref == null) {
					boolean isFromNotification = getIntent().getBooleanExtra(Constants.isFromNotification, false);

					LogManager.writeLogInfo(getString(R.string.msg_success_registration));

					SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString(Constants.AppName_Key, Constants.APPS_NAME);
					editor.putString(Constants.UserName_Key, mStrBackEndUser);
					editor.putString(UtilConstants.Password_Key, Configuration.pwd_text);
					editor.putString(Constants.serverHost_key, Configuration.server_Text);
					editor.putString(Constants.serverPort_key, Configuration.port_Text);
					editor.putString(Constants.serverClient_key, Configuration.client_Text);
					editor.putString(Constants.companyid_key, Configuration.cmpnyId_Text);
					editor.putString(Constants.securityConfig_key, Configuration.secConfig_Text);
					editor.putString(Constants.appConnID_key, Constants.appConID_Text);
					editor.putString(Constants.appID_key, Configuration.appID_Text);
					editor.putString(Constants.appEndPoint_Key, Constants.appEndPoint_Text);
					editor.putString(Constants.pushEndPoint_Key, Constants.pushEndPoint_Text);
					editor.putString(Constants.SalesPersonName, "");
					editor.putString(Constants.SalesPersonMobileNo, "");
					editor.putString(Constants.BirthDayAlertsDate, UtilConstants.getDate1());
					editor.putBoolean(Constants.isPasswordSaved, true);
					editor.putBoolean(Constants.isDeviceRegistered, true);
					editor.putBoolean(Constants.isFirstTimeReg, true);
					editor.putInt(Constants.VisitSeqId, 0);
					editor.commit();
					//Create database for sync history table
					createSyncDatabase();

					addAlertsKeyValueInDataVault();

					Intent goToNextActivity = new Intent(this, MainMenu.class);
					goToNextActivity.putExtra(Constants.isFromNotification, isFromNotification);
					//Starting Main Menu
					startActivity(goToNextActivity);
					finish();
				} else {
					boolean isFromNotification = getIntent().getBooleanExtra(Constants.isFromNotification, false);
					Intent goToNextActivity = new Intent(this, LoginActivity.class);
					goToNextActivity.putExtra(Constants.isFromNotification, isFromNotification);
					//Navigating to Login Activity
					startActivity(goToNextActivity);
					finish();
				}

			} catch (LogonManager.LogonManagerException e) {
				Log.e(TAG, e.getLocalizedMessage(), e);
				LogManager.writeLogError(Constants.device_reg_failed_txt, e);
			} catch (LogonCoreException e) {
				Log.e(TAG, e.getLocalizedMessage(), e);
				LogManager.writeLogError(Constants.device_reg_failed_txt, e);
			}
		}
	}

	*/
/*
	TODO First time registration add birthday alerts key and value in data vault
	 *//*

	private void addAlertsKeyValueInDataVault() {
		try {
			//noinspection deprecation
			LogonCore.getInstance().addObjectToStore(Constants.BirthDayAlertsKey, "");
		} catch (LogonCoreException e) {
			e.printStackTrace();
		}
	}

	*/
/*Creates table for Sync history in SQLite DB*//*

	private void createSyncDatabase() {
		Hashtable<String, String> hashtable = new Hashtable<>();
		hashtable.put(Constants.SyncGroup, "");
		hashtable.put(Constants.Collections, "");
		hashtable.put(Constants.TimeStamp, "");
		try {
			Constants.events.crateTableConfig(Constants.SYNC_TABLE, hashtable);
			getSyncHistoryTable();
		} catch (Exception e) {
			LogManager.writeLogError(Constants.error_creating_sync_db
					+ e.getMessage());
		}
	}
	*/
/*Sync History table for Sync*//*

	private void getSyncHistoryTable() {
		String[] definingReqArray = Constants.getDefinigReq(getApplicationContext());
		for (int i = 0; i < definingReqArray.length; i++) {
			String colName = definingReqArray[i];
			if (colName.contains("?$")) {
				String splitCollName[] = colName.split("\\?");
				colName = splitCollName[0];
			}
			try {
				Constants.events.inserthistortTable(Constants.SYNC_TABLE, "",
						Constants.Collections, colName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		createprospectedCustomerDatabase();

	}




	private void createprospectedCustomerDatabase() {
		Hashtable<String, String> hashtable = new Hashtable<>();
		hashtable.put(Constants.OfficerEmployeeCode, "");
		hashtable.put(Constants.CounterName, "");
		hashtable.put(Constants.LongitudeAndLatitude, "");
		hashtable.put(Constants.CounterType, "");
		hashtable.put(Constants.ContactPerson, "");
		hashtable.put(Constants.PCMobileNo, "");
		hashtable.put(Constants.ProspectecCustomerAddress, "");
		hashtable.put(Constants.PCcity, "");
		hashtable.put(Constants.PCDistrict, "");
		hashtable.put(Constants.Taluka, "");
		hashtable.put(Constants.PinCode, "");
		hashtable.put(Constants.Block, "");
		hashtable.put(Constants.TotalTradePottential, "");
		hashtable.put(Constants.TotalNonTradePottential, "");
		hashtable.put(Constants.PottentialAvailable, "");
		hashtable.put(Constants.UTCL, "");
		hashtable.put(Constants.OCL, "");
		hashtable.put(Constants.LAF, "");
		hashtable.put(Constants.ACC, "");
		hashtable.put(Constants.POPDistributed, "");
		hashtable.put(Constants.PCRemarks, "");
		hashtable.put(Constants.CustomerNo, "");



		try {
			Constants.events.crateTableConfig(Constants.PROSPECTED_TABLE, hashtable);

		} catch (Exception e) {
			LogManager.writeLogError(Constants.error_creating_sync_db
					+ e.getMessage());
		}
		createSchmeDatabase();
	}

	private void createSchmeDatabase() {
		Hashtable<String, String> hashtable = new Hashtable<>();
		hashtable.put(Constants.SchemeName, "");
		hashtable.put(Constants.SchemeID, "");
		hashtable.put(Constants.SchemeGUID, "");
		hashtable.put(Constants.ValidFromDate, "");
		hashtable.put(Constants.ValidToDate, "");


		try {
			Constants.events.crateTableConfig(Constants.SCHEME_TABLE, hashtable);

		} catch (Exception e) {
			LogManager.writeLogError(Constants.error_creating_sync_db
					+ e.getMessage());
		}

		createoutstandingAgeDatabase();
	}


	private void createoutstandingAgeDatabase() {
		Hashtable<String, String> hashtable = new Hashtable<>();
		hashtable.put(Constants.OACustomerNo, "");
		hashtable.put(Constants.OACustomerName, "");
		hashtable.put(Constants.OACityName, "");
		hashtable.put(Constants.OATelephone1, "");
		hashtable.put(Constants.OADistChannel, "");
		hashtable.put(Constants.OASecurityDeposit, "");
		hashtable.put(Constants.OACreditLimit, "");
		hashtable.put(Constants.OATotalDebitBal, "");
		hashtable.put(Constants.OA0_7Days, "");
		hashtable.put(Constants.OA7_15Days, "");
		hashtable.put(Constants.OA15_30Days, "");
		hashtable.put(Constants.OA30_45Days, "");
		hashtable.put(Constants.OA45_60Days, "");
		hashtable.put(Constants.OA60_90Days, "");
		hashtable.put(Constants.OA90_120Days, "");
		hashtable.put(Constants.OA120_180Days, "");
		hashtable.put(Constants.OA180Days, "");
		hashtable.put(Constants.OAPastDays, "");
		hashtable.put(Constants.OACurrentDays, "");
		hashtable.put(Constants.OA3160Days, "");
		hashtable.put(Constants.OA6190Days, "");
		hashtable.put(Constants.OA91120Days, "");
		hashtable.put(Constants.OA120Days, "");



		createorderinfoDatabase();

		try {
			Constants.events.crateTableConfig(Constants.OUTSTANDINGAGE_TABLE, hashtable);

		} catch (Exception e) {
			LogManager.writeLogError(Constants.error_creating_sync_db
					+ e.getMessage());
		}

	}


	private void createorderinfoDatabase() {
		Hashtable<String, String> hashtable = new Hashtable<>();
		hashtable.put(Constants.CustomerName, "");
		hashtable.put(Constants.CustomerNo, "");
		hashtable.put(Constants.OrderToRecivive, "");
		hashtable.put(Constants.DateofDispatch, "");
		hashtable.put(Constants.AmountOne, "");
		hashtable.put(Constants.DateOne, "");
		hashtable.put(Constants.AmountTwo, "");
		hashtable.put(Constants.DateTwo, "");
		hashtable.put(Constants.AmountThree, "");
		hashtable.put(Constants.DateThree, "");
		hashtable.put(Constants.AmountFour, "");
		hashtable.put(Constants.DateFour, "");


		try {
			Constants.events.crateTableConfig(Constants.ORDER_INFO_TABLE, hashtable);

		} catch (Exception e) {
			LogManager.writeLogError(Constants.error_creating_sync_db
					+ e.getMessage());
		}

		createPriceinfoDatabase();
	}

	private void createPriceinfoDatabase() {
		Hashtable<String, String> hashtable = new Hashtable<>();
		hashtable.put(Constants.CustomerName, "");
		hashtable.put(Constants.CustomerNo, "");
		hashtable.put(Constants.DateofDispatch, "");
		hashtable.put(Constants.PriceDate, "");
		hashtable.put(Constants.BrandName, "");
		hashtable.put(Constants.HDPE, "");
		hashtable.put(Constants.PaperBag, "");
		hashtable.put(Constants.PriceType, "");

		try {
			Constants.events.crateTableConfig(Constants.PRICE_INFO_TABLE, hashtable);

		} catch (Exception e) {
			LogManager.writeLogError(Constants.error_creating_sync_db
					+ e.getMessage());
		}

		createStockInfoDatabase();
	}


	private void createStockInfoDatabase() {
		Hashtable<String, String> hashtable = new Hashtable<>();
		hashtable.put(Constants.PriceDate, "");
		hashtable.put(Constants.CustomerName, "");
		hashtable.put(Constants.CustomerNo, "");
		hashtable.put(Constants.BrandName, "");
		hashtable.put(Constants.HDPE, "");
		hashtable.put(Constants.PaperBag, "");

		try {
			Constants.events.crateTableConfig(Constants.STOCK_INFO_TABLE, hashtable);

		} catch (Exception e) {
			LogManager.writeLogError(Constants.error_creating_sync_db + e.getMessage());
		}
		createPOPDatabase();
	}
	private void createPOPDatabase() {
		Hashtable<String, String> hashtable = new Hashtable<>();
		hashtable.put(Constants.DateofDispatch, "");
		hashtable.put(Constants.CustomerName, "");
		hashtable.put(Constants.CustomerNo, "");
		hashtable.put(Constants.diaryCheck, "");
		hashtable.put(Constants.chitPadCheck, "");
		hashtable.put(Constants.bannerCheck, "");
		try {
			Constants.events.crateTableConfig(Constants.POP_INFO_TABLE, hashtable);

		} catch (Exception e) {
			LogManager.writeLogError(Constants.error_creating_sync_db + e.getMessage());
		}
		createTradeInfoDatabase();
	}
	private void createTradeInfoDatabase() {
		Hashtable<String, String> hashtable = new Hashtable<>();
		hashtable.put(Constants.TradeDate, "");
		hashtable.put(Constants.CustomerName, "");
		hashtable.put(Constants.CustomerNo, "");
		hashtable.put(Constants.TradePotential, "");
		hashtable.put(Constants.NonTradePotential, "");
		hashtable.put(Constants.BgPotential, "");
		hashtable.put(Constants.TypeOfConstruction, "");
		hashtable.put(Constants.StageOfConstruction, "");
		hashtable.put(Constants.BrandUTCLCheck, "");
		hashtable.put(Constants.BrandACCCheck, "");
		hashtable.put(Constants.BrandOCLCheck, "");
		hashtable.put(Constants.ConfigType, "");

		try {
			Constants.events.crateTableConfig(Constants.TRADE_INFO_TABLE, hashtable);
		} catch (Exception e) {
			LogManager.writeLogError(Constants.error_creating_sync_db + e.getMessage());
		}
		createTradeInfoCustomerTechTeamDatabase();
	}
	private void createTradeInfoCustomerTechTeamDatabase() {
		Hashtable<String, String> hashtable = new Hashtable<>();
		hashtable.put(Constants.TradeDate, "");
		hashtable.put(Constants.CustomerName, "");
		hashtable.put(Constants.CustomerNo, "");
		hashtable.put(Constants.ActivityConducted, "");
		hashtable.put(Constants.TechnicalDate, "");

		try {
			Constants.events.crateTableConfig(Constants.TRADE_INFO_CUSTOMER_TECH_TEAM_TABLE, hashtable);

		} catch (Exception e) {
			LogManager.writeLogError(Constants.error_creating_sync_db + e.getMessage());
		}

		createDealerTargetVsAchivemnetDatabase();
	}


	private void createDealerTargetVsAchivemnetDatabase() {
		Hashtable<String, String> hashtable = new Hashtable<>();
		hashtable.put(Constants.TADealerNo, "");
		hashtable.put(Constants.TADealerName, "");
		hashtable.put(Constants.TADealerCity, "");
		hashtable.put(Constants.TACurMonthTraget, "");
		hashtable.put(Constants.TAProrataTraget, "");
		hashtable.put(Constants.TASaleACVD, "");
		hashtable.put(Constants.TAProrataAchivement, "");
		hashtable.put(Constants.TABalanceQty, "");
		hashtable.put(Constants.TADailyTarget, "");


		try {
			Constants.events.crateTableConfig(Constants.DEALER_TARGET_VS_ACHIVEMENT_TABLE, hashtable);

		} catch (Exception e) {
			LogManager.writeLogError(Constants.error_creating_sync_db + e.getMessage());
		}
		createSalesTargetVsAchivemnetDatabase();

	}


	private void createSalesTargetVsAchivemnetDatabase() {
		Hashtable<String, String> hashtable = new Hashtable<>();
		hashtable.put(Constants.TADepotNo, "");
		hashtable.put(Constants.TADepotName, "");
		hashtable.put(Constants.TADealerCity, "");
		hashtable.put(Constants.TACurMonthTraget, "");
		hashtable.put(Constants.TAProrataTraget, "");
		hashtable.put(Constants.TASaleACVD, "");
		hashtable.put(Constants.TAProrataAchivement, "");
		hashtable.put(Constants.TABalanceQty, "");
		hashtable.put(Constants.TADailyTarget, "");


		try {
			Constants.events.crateTableConfig(Constants.SALES_TARGET_VS_ACHIVEMENT_TABLE, hashtable);

		} catch (Exception e) {
			LogManager.writeLogError(Constants.error_creating_sync_db + e.getMessage());
		}


	}


//	private void createTargetVsAchivemnetDatabase() {
//		Hashtable<String, String> hashtable = new Hashtable<>();
//		hashtable.put(Constants.TADealerNo, "");
//		hashtable.put(Constants.TADealerName, "");
//		hashtable.put(Constants.TADealerCity, "");
//		hashtable.put(Constants.TACurMonthTraget, "");
//		hashtable.put(Constants.TAProrataTraget, "");
//		hashtable.put(Constants.TASaleACVD, "");
//		hashtable.put(Constants.TAProrataAchivement, "");
//		hashtable.put(Constants.TABalanceQty, "");
//		hashtable.put(Constants.TADailyTarget, "");
//
//
//		try {
//			Constants.events.crateTableConfig(Constants.TARGET_VS_ACHIVEMENT_TABLE, hashtable);
//
//		} catch (Exception e) {
//			LogManager.writeLogError(Constants.error_creating_sync_db + e.getMessage());
//		}
//	}

	@Override
	public void onSecureStorePasswordChanged(boolean arg0, String arg1) {

	}

	@Override
	public void onUserDeleted() {

	}

	@Override
	public void registrationInfo() {

	}

	*/
/*Displays MAF LOGON Screen*//*

	private void showLogonScreen() {
		// ask LogonUIFacede to present the logon screen
		// set the resulting view as the content view for this activity
		setContentView(mLogonUIFacade.logon());

		mLogonUIFacade.showSplashScreen(false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.showLogonScreen();
	}

	@Override
	public void onRefreshCertificate(boolean arg0, String arg1) {

	}

}
*/
