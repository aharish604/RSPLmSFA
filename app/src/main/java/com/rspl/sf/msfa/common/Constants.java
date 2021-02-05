package com.rspl.sf.msfa.common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.inputmethodservice.KeyboardView;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.system.ErrnoException;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.registration.RegistrationModel;
import com.arteriatech.mutils.support.SecuritySettingActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.rspl.sf.msfa.BuildConfig;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.SPGeo.database.DatabaseHelperGeo;
import com.rspl.sf.msfa.SPGeo.database.LocationBean;
import com.rspl.sf.msfa.SPGeo.services.AlaramRecevier;
import com.rspl.sf.msfa.SPGeo.services.LocationMonitoringService;
import com.rspl.sf.msfa.database.EventDataSqlHelper;
import com.rspl.sf.msfa.finance.InvoiceBean;
import com.rspl.sf.msfa.interfaces.DialogCallBack;
import com.rspl.sf.msfa.interfaces.SyncHistoryCallBack;
import com.rspl.sf.msfa.mbo.BirthdaysBean;
import com.rspl.sf.msfa.mbo.Config;
import com.rspl.sf.msfa.mbo.CustomerBean;
import com.rspl.sf.msfa.mbo.ErrorBean;
import com.rspl.sf.msfa.mbo.MaterialsBean;
import com.rspl.sf.msfa.mbo.MyTargetsBean;
import com.rspl.sf.msfa.mbo.SKUGroupBean;
import com.rspl.sf.msfa.mbo.SalesPersonBean;
import com.rspl.sf.msfa.mtp.MTPRoutePlanBean;
import com.rspl.sf.msfa.priceUpdate.PricingDatabaseHelper;
import com.rspl.sf.msfa.registration.Configuration;
import com.rspl.sf.msfa.so.CreditLimitBean;
import com.rspl.sf.msfa.so.SOUtils;
import com.rspl.sf.msfa.socreate.SOItemBean;
import com.rspl.sf.msfa.store.OfflineManager;
import com.rspl.sf.msfa.sync.SyncHist;
import com.rspl.sf.msfa.sync.SyncSelectionActivity;
import com.rspl.sf.msfa.visit.VisitActivityBean;
import com.sap.client.odata.v4.core.CharBuffer;
import com.sap.client.odata.v4.core.GUID;
import com.sap.client.odata.v4.core.StringFunction;
import com.sap.smp.client.httpc.events.IReceiveEvent;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.exception.ODataNetworkException;
import com.sap.smp.client.odata.impl.ODataDurationDefaultImpl;
import com.sap.smp.client.odata.metadata.ODataMetadata;
import com.sap.smp.client.odata.offline.ODataOfflineException;
import com.sap.smp.client.odata.offline.ODataOfflineStore;
import com.sap.smp.client.odata.store.ODataRequestChangeSet;
import com.sap.smp.client.odata.store.ODataRequestParamBatch;
import com.sap.smp.client.odata.store.ODataRequestParamSingle;
import com.sap.smp.client.odata.store.impl.ODataRequestChangeSetDefaultImpl;
import com.sap.smp.client.odata.store.impl.ODataRequestParamBatchDefaultImpl;
import com.sap.smp.client.odata.store.impl.ODataRequestParamSingleDefaultImpl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import static android.content.Context.MODE_PRIVATE;
import static com.rspl.sf.msfa.store.OfflineManager.offlineStore;

public class Constants {
    public static final String DataVaultUpdate = "DataVaultUpdate";
    public static final String PASSWORD_LOCKED = "PASSWORD_LOCKED";
    public static final String Unauthorized = "Unauthorized";
    public static final String PASSWORD_DISABLED = "PASSWORD_DISABLED";
    public static final String USER_INACTIVE = "USER_INACTIVE";
    public static final String PASSWORD_RESET_REQUIRED = "PASSWORD_RESET_REQUIRED";
    public static final String PASSWORD_CHANGE_REQUIRED = "PASSWORD_CHANGE_REQUIRED";
    public static final String CLBASE = "CLBASE";
    public static final String INVST = "INVST";
    public static final String INTENT_EXTRA_DEALER_STOCK_BEAN = "dealer_stock_bean";
    public static final String INTENT_EXTRA_MATERIAL_LIST = "material_list";
    public static final String EXTRA_NOTIFICATION = "notificationData";
    public static final String EXTRA_VIEW_ID = "notificationViewId";
    public static final String KEY_FIRST_TIME_RUN = "firstTimeRun";
    public static final String APP_UPGRADE_TYPESET_VALUE = "MSFA";
    public static final Handler handler = new Handler();
    public static final String timer_flag = "timer_flag";
    public static final String KEY_FIRST_TIME_RUN_DashBroad = "firstTimeRunDashBroad";
    public static final int LOCATION_INTERVAL = 60 * 1000;
    public static final int FASTEST_LOCATION_INTERVAL = LOCATION_INTERVAL / 2;
    public static final int MAX_WAIT_TIME = LOCATION_INTERVAL * 5;
    public static final String GeoDate = "GeoDate";
    public static final String GeoTime = "GeoTime";
    public static final String GeoGUID = "GeoGUID";
    /* Odata Queries */
    public static final String GEOENABLE = "GEOENABLE";
    public static final String SPNO = "SPNO";
    public static final String Reason = "Reason";
    public static final String ReasonDesc = "ReasonDesc";
    public static final String BatteryPerc = "BatteryPerc";
    public static final String APKVersion = "APKVersion";
    public static final String APKVersionCode = "APKVersionCode";
    public static final String MobileNo11 = "MobileNo";
    public static final String OsVersion = "OSVersion";
    public static final String MobileModel = "MobileModel";
    public static final String IMEI1 = "IMEI1";
    public static final String IMEI2 = "IMEI2";
    public static final String SPGeos = "SPGeos";
    public static final String ZZInactiveCustBlks = "ZZInactiveCustBlks";
    public static final String EXTRA_CUSTOMER_NO = "customerNo";
    public static final String EXTRA_CUSTOMER_NAME = "customerName";
    public static final String EXTRA_TITLE = "extraTitle";
    public static final String LOG_TABLE = "log";
    public static final String SYNC_TABLE = "SyncTable";
    public static final String PROSPECTED_TABLE = "ProspectedCustomer";
    public static final String OUTSTANDINGAGE_TABLE = "OutstandingAge";
    public static final String SCHEME_TABLE = "Schemes";
    public static final String DEALER_TABLE = "DEALERBEHAVIOUR";
    public static final String ORDER_INFO_TABLE = "ORDERINFO";
    public static final String PRICE_INFO_TABLE = "PRICEINFO";
    public static final String STOCK_INFO_TABLE = "STOCKINFO";
    public static final String POP_INFO_TABLE = "POPINFO";
    public static final String TRADE_INFO_TABLE = "TRADEINFO";
    public static final String TRADE_INFO_CUSTOMER_TECH_TEAM_TABLE = "TRADEINFOCUSTOMERTECHTEAM";
    public static final String DEALER_TARGET_VS_ACHIVEMENT_TABLE = "DealerTargetVsAchivement";
    public static final String SALES_TARGET_VS_ACHIVEMENT_TABLE = "SalesTargetVsAchivement";
    public static final String STATRTEND_TABLE = "StartEnd";
    public static final String STOCKLIST = "StockList";
    public static final String PriceList = "PriceList";
    public static final String FocusedProducts = "FocusedProducts";
    public static final String SegmentedMaterials = "SegmentedMaterials";
    public static final String EXTRA_POS = "extraPOS";
    public static final String EXTRA_SO_CREATE_TITLE = "SO_CREATE";
    public static final String EXTRA_SO_CREDIT_LIMIT = "SO_CREATE_CREDIT_LIMIT";
    public static final String VHELP_MODELID_ENTITY_TYPE = "EntityType eq 'SO'";
    public static final String VHELP_MODELID_ENTITY_TYPE_CHANNELPART = "EntityType eq 'ChannelPartner'";
    public static final String SHADECARDCUST = "ShadeCardCust";
    public static final String CUSTOMERCOMPLAINTS = "CustomerComplaints";
    public static final String DEALERSTOCKENTRY = "DealerStockEntry";
    public static final String DealerStocks = "DealerStocks";
    public static final String DealerStockItemDetails = "DealerStockItemDetails";
    public static final String DEALERSTOCKCONFIGURE = "DealerStockConfigure";
    public static final String RELATIONSHIPCALL = "RelationshipCall";
    public static final String ALLOC_STOCK_LIST = "AllocStockList";
    public static final String CUSTOMERTARGETS = "CustomerTargets";
    public static final String OVERALLSUMMARY = "OverAllSummary";
    public static final String ChequeBounceSummary = "ChequeBounceSummary";
    public static final String CreditNotes = "CreditNotes";
    public static final String PartnerFunctions = "PartnerFunctions";
    public static final String DerivedSecSales = "DerivedSecSales";
    public static final String CreateEditSO = "CreateEditSO";
    public static final String CompStocks = "CompStocks";
    public static final String CompStockItemDetails = "CompStockItemDetails";
    public static final String CompMasters = "CompMasters";
    public static final ArrayList<String> matGrpArrList = new ArrayList<String>();
    public static final String UserProfiles = "UserProfiles";
    public static final String EXTRA_BEAN = "onBean";
    public static final String ForwardindAgents = "ForwardingAgents";
    public static final String ShippingPoints = "ShippingPoints";
    public static final String CustSlsAreas = "CustSlsAreas";
    public static final String CONFIGURATIONS = "Configurations";
    public static final String PlantStorLocs = "PlantStorLocs";
    public static final String processFieldId = "ID";
    public static final String processFieldDesc = "Description";
    public static final String SalesDistrictCode = "SalesDistrict";
    public static final String SalesDistrictDesc = "SalesDistrictDesc";
    public static final String Stocks = "Stocks";
    //------>This id our testing purpose added based on route plan approval all levels(12-08-2015)
    public static final String LOGIN_ID_NAME = "userLevel";
    public static final String PREFS_NAME = "mSFAPreference";
    public static final String AUTH_NAME = "Auth";
    public static final String RequirementDate = "RequirementDate";
    public static final String TransportationPlanDate = "TransportationPlanDate";
    public static final String MaterialAvailDate = "MaterialAvailDate";
    public static final String str_0000 = "0000";
    public static final String str_000000 = "000000";
    public static final String TextCategory = "TextCategory";
    public static final String SSINVOICES = "SSInvoices";
    public static final String SO_ORDER_HEADER = "SalesOrders";
    public static final String COMPETITORSTOCK = "CompetitorStock";
    public static final String CompetitorStocks = "CompetitorStocks";
    public static final String PRICINGLISTTABLE = "PricingList";
    public static final String INCENTIVETRACKINGTABLE = "IncentiveTracking";
    public static final String MaterialRgb = "RegularShades";
    public static final String upcomingShades = "upcomingShades";
    // public static int INDEX_TEMP_NEW[] = null;
    public final static String PROPERTY_APPLICATION_ID = "d:ApplicationConnectionId";
    public static final String SOItemSchedules = "SOItemSchedules";
    public static final String FeedbackList = "FeedbackList";
    public static final String PrimaryDealerStockCreate = "Dealer Stock Create";
    public static final String DealerStockID = "07";
    //Customer
    public static final String KEY_ROLL_ID = "customerRollIdKey";
    public static final String KEY_LOGIN_NAME = "cLoginNameKey";
    /*bundle*/
    public static final String BUNDLE_RESOURCE_PATH = "resourcePath";
    public static final String BUNDLE_OPERATION = "operationBundle";
    public static final String BUNDLE_REQUEST_CODE = "requestCodeBundle";
    public static final String BUNDLE_SESSION_ID = "sessionIdBundle";
    public static final String BUNDLE_SESSION_REQUIRED = "isSessionRequired";
    public static final String BUNDLE_SESSION_URL_REQUIRED = "isSessionTOUrlRequired";
    public static final String BUNDLE_SESSION_TYPE = "sessionTypeBundle";
    public static final String BUNDLE_IS_BATCH_REQUEST = "isBatchRequestBundle";
    public static final String STORE_DATA_INTO_TECHNICAL_CACHE = "storeDataIntoTechnicalCache";
    public static final String BUNDLE_READ_FROM_TECHNICAL_CACHE = "readFromTechnicalCacheBundle";
    public static final String Tasks = "Tasks";
    public static final String MasterCountDBs = "MasterCountDBs";
    public static final String TransactionCountDBs = "TransactionCountDBs";
    public static final String UnloadingPoint = "UnloadingPoint";
    public static final String ReceivingPoint = "ReceivingPoint";
    public static final String CURRENT_VERSION_CODE = "currentVersionCode";
    public static final String INTIALIZEDB = "intializedb";
    //for ID4/DEV
    public static final String OutstandingInvoices = "OutstandingInvoices";
    public static final String OutstandingInvoiceItemDetails = "OutstandingInvoiceItemDetails";
    public static final String OutstandingInvoiceItems = "OutstandingInvoiceItems";

    //    public static  String NavCustNo = "";
//    public static  String NavCPUID = "";
//    public static  String NavComingFrom = "";
//    public static  String NavCustName = "";
//    public static  String NavCPGUID32 = "";
    public static final String Complaints = "Complaints";
    public static final String RouteScheduleSPs = "RouteScheduleSPs";
    public static final String STORE_NAME = "mSFA_Offline";
    public static final String STORE_NAMEGEO = "mSFA_Offline_Geo";
    public static final String backupDBPath = "mSFA_Offline.udb";
    public static final String backuprqDBPath = "mSFA_Offline.rq.udb";
    public static final String CUSTOMERS = "Customers";
    public static final String CustomerNo = "CustomerNo";
    public static String REPEATABLE_REQUEST_ID = "";
    public static String REPEATABLE_DATE = "";
    public static final String CustomerPO = "CustomerPO";
    public static final String CustomerPODate = "CustomerPODate";
    public static final String SalesArea = "SalesArea";
    public static final String AmtPastDue = "AmtPastDue";
    public static final String AmtCurrentDue = "AmtCurrentDue";
    public static final String Amt31To60 = "Amt31To60";
    public static final String Amt61To90 = "Amt61To90";
    public static final String Amt91To120 = "Amt91To120";
    public static final String AmtOver120 = "AmtOver120";
    public static final String TradePotential = "TradePotential";
    public static final String NonTradePotential = "NonTradePotential";
    public static final String BgPotential = "BgPotential";
    public static final String TypeOfConstruction = "TypeOfConstruction";
    public static final String StageOfConstruction = "StageOfConstruction";
    public static final String BrandUTCLCheck = "BrandUTCLCheck";
    public static final String BrandACCCheck = "BrandACCCheck";
    public static final String BrandOCLCheck = "BrandOCLCheck";
    public static final String ConfigType = "ConfigType";
    public static final String ActivityConducted = "ActivityConducted";
    public static final String TechnicalDate = "TechnicalDate";
    public static final String City = "City";
    public static final String MobileNumber = "MobileNumber";
    public static final String MailId = "MailId";
    public static final String CustDOB = "CustDOB";
    //	public static final String Anniversary = "Anniversary";
    public static final String SpouseDOB = "SpouseDOB";
    public static final String Child1DOB = "Child1DOB";
    public static final String Child2DOB = "Child2DOB";
    public static final String Child3DOB = "Child3DOB";
    public static final String Child1Name = "Child1Name";
    public static final String Child2Name = "Child2Name";
    public static final String Child3Name = "Child3Name";
    public static final String MaterialGroupID = "MaterialGroupID";
    public static final String MaterialGroupDesc = "MaterialGroupDesc";
    public static final String DbStock = "DbStock";
    public static final String MaterialNo = "MaterialNo";
    public static final String MatGrpDesc = "MatGrpDesc";
    public static final String UspMustSell = "UspMustSell";
    public static final String UspFocused = "UspFocused";
    public static final String UspNew = "UspNew";
    public static final String UspDesc = "UspDesc";
    public static final String MaterialDesc = "MaterialDesc";
    public static final String DepotStock = "DepotStock";
    public static final String BannerDesc = "BannerDesc";
    public static final String MaterialGroup = "MaterialGroup";
    public static final String MaterialGrpDesc = "MaterialGrpDesc";
    public static final String TargetItemGUID = "TargetItemGUID";
    public static final String MatGroupDesc = "MatGroupDesc";
    public static final String ItemCategory = "ItemCategory";
    public static final String DevCollAmount = "DevCollAmount";
    public static final String RouteSchedules = "RouteSchedules";
    public static final String RouteSchedulePlans = "RouteSchedulePlans";
    public static final String TYPE = "Type";
    public static final String VALUE = "Value";
    public static final String DESCRIPTION = "Description";
    public static final String EntityType = "EntityType";
    public static final String IsDefault = "IsDefault";
    public static final String AppntRmnDur = "AppntRmnDur";
    public static final String PropName = "PropName";
    public static final String ID = "ID";
    public static final String CustomerCreditLimits = "CustomerCreditLimits";
    public static final String TRDGRPTYPE = "TRDGRPTYPE";
    public static final String DISPDIST = "DISPDIST";
    public static final String DSPPRCNO0 = "DSPPRCNO0";
    public static final String DSPMATNO = "DSPMATNO";
    public static final String EVLTYP = "EVLTYP";
    public static final String TypeValue = "TypeValue";
    public static final String Typeset = "Typeset";
    public static final String Types = "Types";
    public static final String Typesname = "Typesname";
    public static final String PROP_ATTTYP = "ATTTYP";
    public static final String PROP_ACTTYP = "ACTTYP";
    public static final String PROP_MER_TYPE = "RVWTYP";
    public static final String SSSOs = "SSSOs";
    public static final String SOItemDetails = "SOItemDetails";
    public static final String ConfigTypsetTypeValues = "ConfigTypsetTypeValues";
    public static final String ConfigTypesetTypes = "ConfigTypesetTypes";
    public static final String RoutePlans = "RoutePlans";
    public static final String Bucket1 = "Bucket1";
    public static final String Bucket2 = "Bucket2";
    public static final String Bucket3 = "Bucket3";
    public static final String Bucket4 = "Bucket4";
    public static final String Bucket5 = "Bucket5";
    public static final String Bucket6 = "Bucket6";
    public static final String Bucket7 = "Bucket7";
    public static final String Bucket8 = "Bucket8";
    public static final String Bucket9 = "Bucket9";
    public static final String Bucket10 = "Bucket10";
    public static final String ONETIMESHIPTO = "OneTimeShipTo";
    public static final String SalesOfficeDesc = "SalesOfficeDesc";
    public static final String EXTRA_SESSION_REQUIRED = "isSessionRequired";
    public static final String CHECK_ADD_MATERIAL_ITEM = "checkAddItem";
    public static final String EXTRA_HEADER_BEAN = "onHeaderBean";
    public static final String GRStatusID = "GRStatusID";
    public static final String SalesOff = "SalesOff";
    public static final String CountryCode = "CountryCode";
    public static final String PriDiscAmt = "PriDiscAmt";
    public static final String PriDiscPerc = "PriDiscPerc";
    public static final String PreSalesDocCatDesc = "PreSalesDocCatDesc";
    public static final String ExpiryDate = "ExpiryDate";
    public static final String SalesDistDesc = "SalesDistDesc";
    //SyncHistory
    public static final String SyncHistorysENTITY = ".SyncHistory";
    public static final String ALLOCSTOCKLIST = "AllocStockList";
    public static final String ORG_MONTHS[] = {"Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public static final String NEW_MONTHSCODE[] = {"11", "12", "01", "02",
            "03", "04", "05", "06", "07", "08", "09", "10"};
    public static final String CONFIG_TABLE = "Config2";
    public static final String AUTHORIZATION_TABLE = "Authorizations";
    public static final String SPACE = "%20";
    public static final String ERROR_ARCHIVE_COLLECTION = "ErrorArchive";
    public static final String ERROR_ARCHIVE_ENTRY_REQUEST_METHOD = "RequestMethod";
    public static final String ERROR_ARCHIVE_ENTRY_REQUEST_BODY = "RequestBody";
    public static final String ERROR_ARCHIVE_ENTRY_HTTP_CODE = "HTTPStatusCode";
    public static final String ERROR_ARCHIVE_ENTRY_MESSAGE = "Message";
    public static final String ERROR_ARCHIVE_ENTRY_CUSTOM_TAG = "CustomTag";
    public static final String ERROR_ARCHIVE_ENTRY_REQUEST_URL = "RequestURL";
    public static final String PERSISTEDMETADATA = "metadata";
    public static final String PERSISTEDSERVICEDOC = "servicedoc";
    public static final String PERSISTEDFEEDS = "feeds";
    public static final String TOWN = "TownDistributorList";
    public static final String TRADES = "Trades";
    public static final String FJPLIST = "FJPList";
    public static final String ROUTES = "Routes";
    public static final String OUTLETS = "Outlets";
    public static final String VILLAGELIST = "VillageList";
    public static final String COLLECTIONS = "Collections";
    public static final String COMPETITOR = "CompetitorStocks";
    public static final String COMPETITORITEMS = "CompetitorStockItems";
    public static final String SECONDARYSALES = "SecondarySales";
    public static final String COUNTERSALES = "CounterSales";
    public static final String TERTIARYSALES = "TertiarySales";
    public static final String TERTIARYCOMPETITORS = "TertiaryCompetitors";
    public static final String PRODUCTGROUPS = "ProductGroups";
    public static final String COMPITITORPRODUCTGROUP = "CompProductGrps";
    public static final String RECEIPT_TABLE = "ReceiptTable";
    public static final String SALESORDTYPES = "OrderTypes";
    public static final String SALESAREAS = "SaleAreas";
    public static final String PAYMENTTERMS = "PaymentTerms";
    public static final String CREDITLIMIT = "CreditLimits";
    public static final String CustomerLatLong = "CustomerLatLong";
    public static final String ChangePassword = "ChangePassword";
    public static final String CompetitorMasters = "CompetitorMasters";
    public static final String TEXT_CATEGORY_SET = "TextCategorySet";
    public static final String CONFIGURATION = "Configurations";
    public static final String ValueHelps = "ValueHelps";
    public static final String SALES_ORDER_DELIVERIES = "SalesOrderDeliveries";
    public static final String DELIVERY_STATUS = "DeliveryStatus";
    public static final String PRODUCTPRICES = "ProductPrices";
    public static final String SECONDARYCOMPETITORS = "SecondaryCompetitors";
    public static final String SCHEMESMATERIALS = "SchemeMaterials";
    public static final String POPORDERLISTS = "PopOrderLists";
    public static final String SIGN_BOARD_REQUESTS = "SignBoardRequests";
    public static final String MATERIALS = "Materials";
    public static final String OUTLETFLIST = "OutletF4List";
    public static final String CUSTOMER_MATERIALS = "CustomerMaterials";
    public static final String ACTVITYF4 = "ActivityF4List";
    public static final String CustomerPerformances = "CustomerPerformances";
    public static final String MATERIALLIST = "MaterialList";
    public static final String INVOICE_HEADER = "InvoiceHeaders";
    public static final String INVOICE_ITEM = "InvoiceItems";
    public static final String INVOICEDELIVERIES = "InvoiceDeliveries";
    public static final String ACTIVITY_HDR = "JourneyCycles";
    public static final String REPORTDEALER = "ReportDealerTable";
    public static final String REPORTDEALER_ITEM = "ReportDealeritemTable";
    public static final String REPORTDEALER_COMMENTS = "ReportDealerCommentTable";
    public static final String SO_TEST = "SalesOrders";
    public static final String SO_ITEM_TEST = "Test";
    public static final String MEETINGS = "Meetings";
    public static final String DEALERMEETINGS = "DealerMeetings";
    public static final String POPMATERIALS = "PopMaterials";
    public static final String SOSIMULATELIST = "SoSimulateList";
    public static final String SO_ORDER_SCHEMES = "SalesOrderSchemes";
    public static final String COLL_TARGETS = "CollectionTargets";
    public static final String SALES_TARGETS = "SalesTargets";
    public static final String DLR_OFFTAKE = "DealerOfftakes";
    public static final String DLR_PREFS = "DealerPerfs";
    public static final String CONTACTPERSON = "ContactPersons";
    public static final String CUSTSALESAREAS = "CustSalesAreas";
    public static final String SALESAREAORDTYPES = "SalesAreaOrdTypes";
    public static final String Attendances = "Attendances";
    public static final String Claims = "Claims";
    public static final String Visits = "Visits";
    public static final String ChannelPartners = "ChannelPartners";
    public static final String CPDMSDivisions = "CPDMSDivisions";
    public static final String FinancialPostings = "FinancialPostings";
    public static final String FinancialPostingItemDetails = "FinancialPostingItemDetails";
    public static final String FinancialPostingItems = "FinancialPostingItems";

    //----->ID4 and ID6 HCPMS
   /* public static String server_Text = "mobile-a4597c6af.hana.ondemand.com", port_Text = "443", cmpnyId_Text = "0",
            client_Text = "", actCode_Text = "", loginUser_Text = "", appID_Text = "",
            secConfig_Text = "GW", userName_text = "s0012486235", pwd_text = "Sap@0517";*/

//-----> ID6 HCPMS
  /*  public static String server_Text = "mobile-a4597c6af.hana.ondemand.com", port_Text = "443", cmpnyId_Text = "0",
            client_Text = "", actCode_Text = "", loginUser_Text = "", appID_Text = "",
            secConfig_Text = "GW", userName_text = "s0012486235", pwd_text = "Sap@0917";*/

    //-----> ID4 HCPMS
 /*   public static String server_Text = "mobile-ab64db6e6.hana.ondemand.com", port_Text = "443", cmpnyId_Text = "0",
            client_Text = "", actCode_Text = "", loginUser_Text = "", appID_Text = "",
            secConfig_Text = "GW", userName_text = "s0012486235", pwd_text = "Sap@0917";*/

    //grasim dev ID
 /* public static String server_Text = "mobile-a84ecce64.hana.ondemand.com", port_Text = "443", cmpnyId_Text = "0",
            client_Text = "", actCode_Text = "", loginUser_Text = "", appID_Text = "",
            secConfig_Text = "GW", userName_text = "s0012486235", pwd_text = "Sap@0517";*/

    //grasim qua ID
    /*public static String server_Text = "mobile-aa1a539f6.hana.ondemand.com", port_Text = "443", cmpnyId_Text = "0",
            client_Text = "", actCode_Text = "", loginUser_Text = "", appID_Text = "",
            secConfig_Text = "GW", userName_text = "s0012486235", pwd_text = "Sap@0517";*/


    //SS Demo Account
//    public static String server_Text = "mobile-ac89cf43a.hana.ondemand.com", port_Text = "443", cmpnyId_Text = "0",
//            client_Text = "", actCode_Text = "", loginUser_Text = "", appID_Text = "",
//            secConfig_Text = "GW", userName_text = "P383751", pwd_text = "Sap@0517";


    //----->Maihar HCPMS DEV
   /* public static String server_Text = "mobile-c810f2bda.ap1.hana.ondemand.com", port_Text = "443", cmpnyId_Text = "0",
            client_Text = "", actCode_Text = "", loginUser_Text = "", appID_Text = "",
    secConfig_Text = "GW", userName_text = "P000003", pwd_text = "Welcome1";*/


    //----->Maihar HCPMS New - DEV
    /*public static String server_Text = "mobile-c33d0a1c5.ap1.hana.ondemand.com", port_Text = "443", cmpnyId_Text = "0",
            client_Text = "", actCode_Text = "", loginUser_Text = "", appID_Text = "",
            secConfig_Text = "GW", userName_text = "P000003", pwd_text = "Welcome2";*/


    //----->Maihar HCPMS New - QA
    /*public static String server_Text = "mobile-cadb43466.ap1.hana.ondemand.com", port_Text = "443", cmpnyId_Text = "0",
            client_Text = "", actCode_Text = "", loginUser_Text = "", appID_Text = "",
            secConfig_Text = "GW", userName_text = "P000003", pwd_text = "Welcome1";*/

    //----->Maihar HCPMS New - PRD
    /*public static String server_Text = "mobile-c4b62c619.ap1.hana.ondemand.com", port_Text = "443", cmpnyId_Text = "0",
            client_Text = "", actCode_Text = "", loginUser_Text = "", appID_Text = "",
            secConfig_Text = "GW", userName_text = "P000003", pwd_text = "Welcome1";*/


//    ----->Maihar HCPMS QA
//    public static String server_Text = "mobile-cf6081b4c.ap1.hana.ondemand.com", port_Text = "443", cmpnyId_Text = "0",
//            client_Text = "", actCode_Text = "", loginUser_Text = "", appID_Text = "",
//            secConfig_Text = "GW", userName_text = "P000003", pwd_text = "Welcome1";

    // emami dev hcpms
//    public static String server_Text="mobile-ac89cf43a.hana.ondemand.com", port_Text="443", cmpnyId_Text="0",
//			 client_Text="", actCode_Text="", loginUser_Text="",appID_Text = "",
//			 secConfig_Text = "GW",userName_text="p383751",pwd_text="JayaVel@56";

////	 //----->ID4

//    public static String server_Text = "172.25.12.10", port_Text = "8080", cmpnyId_Text = "0",
//            client_Text = "", actCode_Text = "", loginUser_Text = "", appID_Text = "",
//            secConfig_Text = "mSFA_GW1", userName_text = "ss_fos01", pwd_text = "welcome1";

    //----->Emami smp dev
//	public static String server_Text="221.134.108.20", port_Text="8080", cmpnyId_Text="0",
//			client_Text="", actCode_Text="", loginUser_Text="",appID_Text = "",
//			secConfig_Text = "com.arteriatech.mSecSales",userName_text="18194-1",pwd_text="welcome1";

    ////	 //----->ID4 QA

//		 public static String server_Text="172.25.12.10", port_Text="8080", cmpnyId_Text="0",
//			 client_Text="", actCode_Text="", loginUser_Text="",appID_Text = "",
//			 secConfig_Text = "com.arteriatech.mSecSalesQA",userName_text="ss_fos01",pwd_text="welcome1";


    ////	 //----->ID6 QA
//	public static String server_Text="172.25.12.10", port_Text="8080", cmpnyId_Text="0",
//			client_Text="", actCode_Text="", loginUser_Text="",appID_Text = "",
//			secConfig_Text = "com.arteriatech.mSecSalesQA",userName_text="ss_fos01",pwd_text="welcome1";

// --->ID4 Relay server configuration
//	 public static String server_Text="aprins07", port_Text="80", cmpnyId_Text="0",
//			 client_Text="", actCode_Text="", loginUser_Text="",appID_Text = "",
//			 secConfig_Text = "mSecSales",userName_text="900143",pwd_text="welcome1",farm_ID = "ART.Rly.mSFA",
//	 suffix="rs17/client/rs.dll";

    //JK Hcpms Dev
//	public static String server_Text="mobile-cedb1a002.ap1.hana.ondemand.com", port_Text="443", cmpnyId_Text="0",
//			client_Text="", actCode_Text="", loginUser_Text="",appID_Text = "",
//			secConfig_Text = "GW",userName_text="P000001",pwd_text="Welcome_1";

    //for MAF LOGON End

    //App id
    //JK HCPMS
//    public static String APP_ID = "com.arteria.mSFA";

    //ID4
//    public static String APP_ID = "mSFA_GW";
    public static final String RetailerSummarySet = "RetailerSummarySet";
//    ID6 Hcpms
//    public static String APP_ID = "com.arteriatech.mSFAQA";


    //Maihar hcpms Dev/QA
//    public static String APP_ID = "com.arteriatech.mSFA";


//    //emami dev hcpms
//	public static String APP_ID = "com.arteriatech.mSFAEmami";

    // QAS Emami hcpms SS_FOS02
//	public static String APP_ID = "com.arteriatech.mSFAQA";

    // QAS Emami hcpms SS_FOS01
//	public static String APP_ID = "com.arteriatech.mSFAQA1";


//	public static String APP_ID = "com.arteria.mSecSales";

    //ID6-QA
//	public static String APP_ID = "com.arteriatech.mSecSalesQA";

    //HCPMS SS_FOS02
//	public static String APP_ID = "com.arteriatech.mSFAQA2";

//	public static String APP_ID = "com.arteria.secSales";

    //----->Emami smp dev
//	public static String APP_ID = "com.arteriatech.mSecSales";


    //Test for himatsingka
//    public static String server_Text="mobile-cd4e24a7c.ap1.hana.ondemand.com", port_Text="443", cmpnyId_Text="0",
//            client_Text="", actCode_Text="", loginUser_Text="",appID_Text = "",
//            secConfig_Text = "GW",userName_text="P000107",pwd_text="Welcome1";
//    public static String APP_ID = "com.himatsingka.mAuditConnect";
    public static final String SPChannelEvaluationList = "MSPChannelEvaluationList";
    public static final String RequestID = "RequestID";
    public static final String RepeatabilityCreation = "RepeatabilityCreation";
    public static final String AttributeTypesetTypes = "AttributeTypesetTypes";

    //for ID6/QA
//	public static final String OutstandingInvoices = "Invoices";
//	public static final String OutstandingInvoiceItemDetails = "SSInvoiceItemDetails";
//	public static final String OutstandingInvoiceItems = "SSInvoiceItems";
    public static final String SFINVOICES = "Invoices";
    public static final String SSInvoiceItemDetails = "SSInvoiceItemDetails";
    public static final String SSInvoiceItemSerials = "SSInvoiceItemSerialNos";
    public static final String CompetitorInfos = "CompetitorInfos";
    public static final String SPStockItemDetails = "SPStockItemDetails";
    public static final String SPStockItemSNos = "SPStockItemSNos";
    public static final String SPStockItems = "SPStockItems";
    public static final String UserProfileAuthSet = "UserProfileAuthSet";
    public static final String Performances = "MPerformances";
    public static final String RetailerActivationStatusSet = "RetailerActivationStatusSet";
    public static final String CEFStatusID = "CEFStatusID";
    public static final String Status111BID = "Status111BID";
    public static final String Status222ID = "Status222ID";
    public static final String SubsMSIDN = "SubsMSIDN";
    public static final String Targets = "Targets";
    public static final String KPISet = "KPISet";
    public static final String KPIItems = "KPIItems";
    public static final String TargetItems = "TargetItems";
    public static final String Month = "Month";
    public static final String Year = "Year";
    public static final String Period = "Period";
    public static final String KPIGUID = "KPIGUID";
    public static final String KPICode = "KPICode";
    public static final String KPIName = "Name";
    public static final String TargetQty = "TargetQty";
    public static final String ActualQty = "ActualQty";
    public static final String TargetValue = "TargetValue";
    public static final String ActualValue = "ActualValue";
    public static final String ActaulValue = "ActaulValue";
    public static final String TargetGUID = "TargetGUID";
    public static final String CalculationBase = "CalculationBase";
    public static final String CalculationSource = "CalculationSource";
    public static final String KPIFor = "KPIFor";
    public static final String RollUpTo = "RollUpTo";
    public static final String RollupStatus = "RollupStatus";
    public static final String RollupStatusDesc = "RollupStatusDesc";
    public static final String KPICategory = "KPICategory";
    public static final String Periodicity = "Periodicity";
    public static final String PeriodicityDesc = "PeriodicityDesc";
    public static final String CEFStatusDesc = "CEFStatusDesc";
    public static final String Status111BDesc = "Status111BDesc";
    public static final String Status222Desc = "Status222Desc";
    public static final String PartnerGUID = "PartnerGUID";
    public static final String PartnerNo = "PartnerNo";
    public static final String PartnerName = "PartnerName";
    public static final String Refersh = "Attendances,Invoices,SSInvoiceItemDetails,FinancialPostings,FinancialPostingItemDetails";
    public static final String CPStockItemDetails = "CPStockItemDetails";
    public static final String CPStockItemSnos = "CPStockItemSnos";
    public static final String CPStockItems = "CPStockItems";
    public static final String DAYTARGETS = "DayTarget";
    public static final String MONTHTARGETS = "MonthlyTarget";
    public static final String CHEQUESUMMARY = "ChequeSummary";
    public static final String PAINTERVISIT = "PainterVisit";
    public static final String QUARTERTARGETS = "QuarterlyTarget";
    public static final String DEALERWISETARGETS = "DealerWiseTarget";
    public static final String DEALERWISETARGETSVALUE = "DealerWiseTargetValue";
    public static final String STOCKOVERVIEWS = "StockOverviews";
    public static final String AUTHORIZATIONS = "Authorizations";
    public static final String DEALERREQUEST = "DealerRequests";
    public static final String CITYCODES = "CityCodes";
    public static final String SALESRETURNINVOICES = "SalesReturnInvoices";
    public static final String SALESCOLLECTIONDATA = "SaleCollectionDatas";
    public static final String STOCKVALUEDATA = "StockValueDatas";
    public static final String PRODUCTDESKDATA = "PrdDeskDatas";
    public static final String ACTIVITYS = "Activities";
    public static final String OUTLETTYPES = "OutletTypes";
    public static final String OUTLETCATEGORIES = "OutletCategories";
    public static final String OUTLETCLASSES = "OutletClasses";
    public static final String BATCHBLOCKLIST = "BatchBlockList";
    public static final String EXCLUDEDMATERIALLIST = "ExcludedMaterialList";
    public static final String FOCUSPRODUCTLIST = "FocusProductList";
    public static final String VISITLIST = "VisitList";
    public static final String FOCUSPRODREASONLIST = "FocusProdReasons";
    public static final String MATERIALSTOCK = "MaterialStocks";
    public static final String RECEIPT = "Receipt";
    public static final String VISITTYPECONFIG = "VisitTypeConfig";
    public static final String CREDITLIMITTABLE = "CustomerCreditLimits";
    public static final String MerchandisingReviews = "MerchandisingReviews";
    public static final String RETILERIMGTABLE = "RetailerImgTable";
    public static final String AGEINGREPORT = "AgeingReport";
    public static final String CREDITNOTE = "CreditNote";
    public static final String Leads = "Leads";
    public static final String Surveys = "Surveys";
    public static final String SurveyQuestions = "SurveyQuestions";
    public static final String SurveyQuestionOptions = "SurveyQuestionOptions";
    public static final String MATERIALSTOCKQTY = "MaterialStock";
    public static final String DEALERWISESECSALES = "DealerWiseSecSales";
    public static final String COMPLAINTSTRACKING = "ComplaintsTracking";
    public static final String SALESORDER = "SOs";
    public static final String SALESORDERITEMS = "SalesOrderItems";
    public static final String SALESORDERITEMSDETAILS = "SalesOrderItemDetails";
    public static final String BrandPerforms = "BrandPerforms";
    public static final String Trends = "Trends";
    public static final String InvoiceItemDetails = "InvoiceItemDetails";
    public static final String PlantStocks = "PlantStocks";
    public static final String UserSalesPersons = "UserSalesPersons";
    public static final String CustomerComplaints = "CustomerComplaints";
    public static final String Feedbacks = "Feedbacks";
    public static final String FeedbackItemDetails = "FeedbackItemDetails";
    public static final String MerchReviews = "MerchReviews";
    public static final String MerchReviewImages = "MerchReviewImages";
    public static final String MerchReviewsAssociativeType = "MerchReview_MerchReviewImage";
    public static final String VisitSurveys = "VisitSurveys";
    public static final String VisitSurveyResults = "VisitSurveyResults";
    public static final String CollectionLists = "CollectionLists";
    public static final String Schemes = "Schemes";
    public static final String Tariffs = "Tariffs";
    public static final String ExpenseEntryItemDetails = "ExpenseEntryItemDetails";
    public static final String ExpenseEntryImages = "ExpenseEntryImages";
    public static final String LeadItemDetails = "LeadItemDetails";
    public static final String ShadeCards = "ShadeCards";
    public static final String TargetMatGrpCustomers = "TargetMatGrpCustomers";
    public static final String ShadeCardFeedbacks = "ShadeCardFeedbacks";
    public static final String DishonourChqs = "DishonourChqs";
    public static final String DepotTargets = "DepotTargets";
    public static final String DishonourChqItemDetails = "DishonourChqItemDetails";
    public static final String DishonourCheques = "DishonourCheques";
    public static final String SalesOrderSummary = "SalesOrderSummary";
    public static final String CollectionAmtSummary = "CollectionAmtSummary";
    public static final String InvoiceSummary = "InvoiceSummary";
    public static final String VisitActivities = "VisitActivities";
    public static final String ActivitySummarys = "ActivitySummarys";
    public static final String SalesHierarchies = "SalesHierarchies";
    public static final String PasswordChanges = "PasswordChanges";
    public static final String arteria_session_header = "x-arteria-loginid";
    public static final int DATE_DIALOG_ID = 0;
    //Anns : Constants
    public static final String ErrorInParser = "Error in initializing the parser!";
    public static final String ODATA_METADATA_COMMAND = "$metadata";
    public static final String ATOM_CONTENT_TYPE = "application/atom+xml";
    public static final String HTTP_CONTENT_TYPE = "content-type";
    public static final String ODATA_TOP_FILTER = "$top=";
    public static final String ODATA_FILTER = "$filter=";
    public static final String RequestFlushResponse = "requestFlushResponse - status code ";
    public static final String OfflineStoreRequestFailed = "offlineStoreRequestFailed";
    public static final String PostedSuccessfully = "posted successfully";
    public static final String SynchronizationCompletedSuccessfully = "Synchronization completed successfully";
    public static final String OfflineStoreFlushStarted = "offlineStoreFlushStarted";
    public static final String OfflineStoreFlushFinished = "offlineStoreFlushFinished";
    public static final String OfflineStoreFlushSucceeded = "offlineStoreFlushSucceeded";
    public static final String OfflineStoreFlushFailed = "offlineStoreFlushFailed";
    public static final String FlushListenerNotifyError = "FlushListener::notifyError";
    public static final String OfflineStoreRefreshStarted = "OfflineStoreRefreshStarted";
    public static final String OfflineStoreRefreshSucceeded = "OfflineStoreRefreshSucceeded";
    public static final String OfflineStoreRefreshFailed = "OfflineStoreRefreshFailed";
    public static final String ALL = "ALL";
    public static final String MerchandisingSnapshot = "Merchandising Snapshot";
    public static final String RequestCacheResponse = "requestCacheResponse";
    public static final String RequestFailed = "requestFailed";
    public static final String Status_message = "status message";
    public static final String Status_code = "status code";
    public static final String RequestFinished = "requestFinished";
    public static final String RequestServerResponse = "requestServerResponse";
    public static final String BeforeReadRequestServerResponse = "Before Read requestServerResponse";
    public static final String BeforeReadentity = "Before Read entity";
    public static final String AfterReadentity = "After Read entity";
    public static final String RequestStarted = "requestStarted";
    public static final String OfflineRequestListenerNotifyError = "OfflineRequestListener::notifyError";
    public static final String ErrorWhileRequest = "Error while request";
    public static final String TimeStamp = "TimeStamp";
    public static final String Error = "Error";
    public static final String SyncTableHistory = "Sync table(History)";
    public static final String CollList = "CollList";
    public static final String SyncOnRequestSuccess = "Sync::onRequestSuccess";
    public static final String SubmittingDeviceCollectionsPleaseWait = "Submitting device collections, please wait";
    public static final String ORDER_TYPE = "ORDERTYPE";
    public static final String ORDER_TYPE_DESC = "ORDERTYPE_DESC";
    public static final String SALESAREA = "SALESAREA";
    public static final String SALESAREA_DESC = "SALESAREADESC";
    public static final String SOLDTO = "SOLDTO";
    public static final String SOLDTONAME = "SOLDTONAME";
    public static final String SHIPPINTPOINT = "SHIPPINTPOINT";
    public static final String SHIPPINTPOINTDESC = "SHIPPINTPOINTDESC";
    public static final String SHIPTO = "SHIPTO";
    public static final String SHIPTONAME = "SHIPTONAME";
    public static final String FORWARDINGAGENT = "FORWARDINGAGENT";
    public static final String FORWARDINGAGENTNAME = "FORWARDINGAGENTNAME";
    public static final String PLANT = "PLANT";
    public static final String PLANTDESC = "PLANTDSEC";
    public static final String INCOTERM1 = "INCOTERM1";
    public static final String INCOTERM1DESC = "INCOTERM1DESC";
    public static final String INCOTERM2 = "INCOTERM2";
    public static final String SALESDISTRICT = "SALESDISTRICT";
    public static final String SALESDISTRICTDESC = "SALESDISTRICTDESC";
    public static final String ROUTE = "ROUTE";
    public static final String ROUTEDESC = "ROUTEDESC";
    public static final String MEANSOFTRANSPORT = "MEANSOFTRANSPORT";
    public static final String MEANSOFTRANSPORTDESC = "MEANSOFTRANSPORTDESC";
    public static final String STORAGELOC = "STORAGELOC";
    public static final String CUSTOMERPO = "CUSTOMERPO";
    public static final String CUSTOMERPODATE = "CUSTOMERPODATE";
    public static final String Collection = "Collection";
    public static final String Merchendising_Snap = "Merchendising Snapshot";
    public static final String IMGTYPE = "JPEG";
    public static final String OfflineStoreOpenFailed = "offlineStoreOpenFailed";
    public static final String OfflineStoreOpenedFailed = "Offline store opened failed";
    public static final String OfflineStoreStateChanged = "offlineStoreStateChanged";
    public static final String OfflineStoreOpenFinished = "offlineStoreOpenFinished";
    public static final String Requestsuccess_status_message_key = "requestsuccess - status message key";
    public static final String RequestFailed_status_message = "requestFailed - status message ";
    public static final String RequestServerResponseStatusCode = "requestServerResponse - status code";
    public static final String FeedbackCreated = "Feedback created";
    public static final String RequestsuccessStatusMessageBeforeSuccess = "requestsuccess - status message before success";
    public static final String OnlineRequestListenerNotifyError = "OnlineRequestListener::notifyError";
    public static final String HTTP_HEADER_SUP_APPCID = "X-SUP-APPCID";
    public static final String HTTP_HEADER_SMP_APPCID = "X-SMP-APPCID";
    public static final String[][] billAges = {{"00", "01", "02", "03", "04"}, {"All", "0 - 30 Days", "31 - 60 Days", "61 - 90 Days", "> 90 Days"}};
    public static final String SalesPersonName = "SalesPersonName";
    public static final String DeviceCollectionsText = "Device Collections";
    public static final String ItemsText = "ITEMS";
    public static final String H = "H";
    public static final String All = "All";
    public static final String Invoices = "Invoices";
    public static final String MatCode = "MatCode";
    public static final String MatDesc = "MatDesc";
    public static final String Qty = "Qty";
    public static final String SSInvoice = "SSInvoice";
    public static final String InvList = "InvList";
    public static final String SnapshotList = "Snapshot List";
    public static final String plain_text = "plain/text";
    public static final String send_email = "Send your email in:";
    public static final String error_txt = "Error :";
    public static final String LOCATION_LOG = "Location : ";
    public static final String whatsapp_packagename = "com.whatsapp";
    public static final String whatsapp_conv_packagename = "com.whatsapp.Conversation";
    public static final String whatsapp_domainname = "@s.whatsapp.net";
    public static final String jid = "jid";
    public static final String sms_txt = "sms:";
    public static final String tel_txt = "tel:";
    public static final String[] beatsArray = {"All"};
    public static final String AdhocList = "AdhocList";
    public static final String comingFrom = "ComingFrom";
    public static final String red_hex_color_code = "#D32F2F";
    public static final String salesPersonName = "SalesPersonName";
    public static final String salesPersonMobileNo = "SalesPersonMobileNo";
    public static final String statusID_03 = "03";
    public static final String dtFormat_ddMMyyyywithslash = "dd/MM/yyyy";
    public static final String X = "X";
    public static final String offlineStoreRequestFailed = "offlineStoreRequestFailed";
    public static final String isPasswordSaved = "isPasswordSaved";
    public static final String isDeviceRegistered = "isDeviceRegistered";
    public static final String appEndPoint_Key = "appEndPoint";
    public static final String pushEndPoint_Key = "pushEndPoint";
    public static final String RetDetails = "RetDetails";
    public static final String RetailerList = "RetailerList";
    public static final String Retailer = "Retailer";
    public static final String NAVFROM = "NAVFROM";
    public static final String getSyncHistory = "getSyncHistory: ";
    public static final String time_stamp = "Time Stamp";
    public static final String[] syncMenu = {"All", "Download", "Upload", "Sync History"};
    public static final String isLocalFilterQry = "?$filter= sap.islocal() ";
    public static final String device_reg_failed_txt = "Device registration failed";
    public static final String SHOWNOTIFICATION = "SHOWNOTIFICATION";
    public static final String timeStamp = "TimeStamp";
    public static final String sync_table_history_txt = "Sync table(History)";
    public static final String ITEM_TXT = "ITEMS";
    public static final String SecondarySOCreate = "Secondary SO Create";
    public static final String PrimarySOCreate = "Primary SO Create";
    public static final String SOItems = "SOItems";
    public static final String arteria_dayfilter = "x-arteria-daysfilter";
    public static final String arteria_attfilter = "x-arteria-vst";
    public static final String arteria_spfilter = "x-arteria-sp";
    public static final String RouteType = "RouteType";
    public static final String BeatPlan = "BeatPlan";
    public static final String NonFieldWork = "NonFieldWork";
    public static final String sync_req_sucess_txt = "Sync::onRequestSuccess";
    public static final String collection = "Collection";
    public static final String entityType = "EntityType";
    public static final String savePass = "savePass";
    public static final String offlineDBPath = "/data/com.rspl.sf.msfa/files/mSFA_Offline.udb";
    public static final String offlineReqDBPath = "/data/com.rspl.sf.msfa/files/mSFA_Offline.rq.udb";
    public static final String isFirstTimeReg = "isFirstTimeReg";
    public static final String isFirstRegistration = "isFirstRegistration";
    public static final String isReIntilizeDB = "isReIntilizeDB";
    public static final String[] todayIconArray = {"Start", "Beat Plan", "My Targets",
            "Schemes", "Depot Stock", "Day Summary",
            "Expense Entry",
            "Visual Aid", "Adhoc Visit", "Alerts", "Expense Entry", "Expense List",/*"Schemes", "Price Update","Dealerwise Target","My Targets"
           ,"Oustanding Summary","Dealer Behaviour",*/"SO Approval", "Product Pricing", "Plant Stock", "MTP"};
    public static final String[] reportIconArray = {"Customers", "Prospective Customer List", "Appointment"};
    public static final String[] admintIconArray = {"Sync", "Log", ""};
    public static final String BeatType = "BeatType";
    public static final String RouteList = "RouteList";
    public static final String OtherRouteList = "OtherRouteList";
    public static final String VisitType = "VisitType";
    public static final String OtherRouteGUID = "OtherRouteGUID";
    public static final String OtherRouteName = "OtherRouteName";
    public static final String VisitCatID = "VisitCatID";
    public static final String AdhocVisitCatID = "02";
    public static final String BeatVisitCatID = "01";
    public static final String OtherBeatVisitCatID = "02";
    public static final String CustomerList = "CustomerList";
    public static final String ProspectiveCustomerList = "ProspectiveCustomerList";

    //	public static String ParentTypeID = "ParentTypeID";
    public static final String Address = "Address";
    public static final String Visit = "Visit";
    public static final String Reports = "Reports";
    public static final String Summary = "Summary";
    public static final String default_txt = "default";
    public static final String logon_finished_appcid = "onLogonFinished: appcid:";
    public static final String logon_finished_aendpointurl = "onLogonFinished: endpointurl:";
    public static final String isFromNotification = "isFromNotification";
    public static final String username = "username";
    public static final String usernameExtra = "usernameExtra";
    public static final String VisitSeqId = "VisitSeqId";
    public static final String RouteBased = "RouteBased";
    public static final String full_Day = "Full Day";
    public static final String first_half = "1st Half";
    public static final String second_half = "2nd Half";
    public static final String[][] arrWorkType = {{"01", "02"}, {"Full Day", "Split"}};
    public static final String DeviceStatus = "DeviceStatus";
    public static final String InvDate = "InvDate";
    public static final String InvAmount = "InvAmount";
    public static final String DeviceNo = "DeviceNo";
    public static final String RetailerNo = "RetailerNo";
    public static final String FFDA33 = "#FFDA33";
    public static final String EntitySet = "EntitySet";
    public static final String T = "T";
    public static final String offline_store_not_closed = "Offline store not closed: ";
    public static final String invalid_payload_entityset_expected = "Invalid payload:EntitySet expected but got ";
    public static final String None = "None";
    public static final String str_00 = "00";
    public static final String str_01 = "01";
    public static final String str_04 = "04";
    public static final String str_false = "false";
    public static final String str_0 = "0";
    public static final String error_txt1 = "Error";
    public static final String error_archive_called_txt = "Error Arcive is called";
    public static final String error = "error";
    public static final String message = "message";
    public static final String CollectionHeaderTable = "CollectionHeaderTable";
    public static final String value = "value";
    public static final String error_during_offline_close = "Error during store close: ";
    public static final String icurrentUDBPath = "/data/com.rspl.sf.msfa/files/mSFA_Offline.udb";
    public static final String ibackupUDBPath = "mSFA_Offline.udb";
    public static final String icurrentRqDBPath = "/data/com.rspl.sf.msfa/files/mSFA_Offline.rq.udb";
    public static final String ibackupRqDBPath = "mSFA_Offline.rq.udb";
    public static final String icurrentDBPath = "/data/com.rspl.sf.msfa/files/mSFA_Offline.rq.udb";
    public static final String ibackupDBPath = "mSFA_Offline.rq.udb";
    public static final String error_creating_sync_db = "Registration:createSyncDatabase Error while creating sync database";
    public static final String error_in_collection = "Error in Collection :";
    public static final String RetName = "RetName";
    public static final String RetID = "RetID";
    public static final String delete_from = "DELETE FROM ";
    public static final String create_table = "create table IF NOT EXISTS ";
    public static final String EventsData = "EventsData";
    public static final String on_Create = "onCreate:";
    public static final String RTGS = "RTGS";
    public static final String NEFT = "NEFT";
    public static final String DD = "DD";
    public static final String Cheque = "Cheque";
    public static final String Margin = "Margin";
    public static final String WholeSalesLandingPrice = "WholeSalesLandingPrice";
    public static final String ConsumerOffer = "ConsumerOffer";
    public static final String TradeOffer = "TradeOffer";
    public static final String ShelfLife = "ShelfLife";
    public static final String SOList = "SOList";
    public static final String CustomerComplaintsCreate = "Consumer Complaints Create";
    public static final String DeviceMechindising = "DeviceMechindising";
    public static final String NonDeviceMechindising = "NonDeviceMechindising";
    public static final String MerchList = "MerchList";
    public static final String VendorNo = "VendorNo";
    public static final String PersonnelNo = "PersonnelNo";
    public static final String VendorName = "VendorName";
    public static final String PersonnelName = "PersonnelName";
    public static final String CustomerNumber = "CustomerNumber";
    public static final String CustomerName = "CustomerName";
    public static final String Street = "Street";
    public static final String Email = "Email";
    public static final String Telephone1 = "Telephone1";
    public static final String Telephone2 = "Telephone2";
    public static final String Feature = "Feature";
    public static final String DelvNo = "DelvNo";
    public static final String StoNo = "StoNo";
    public static final String DeliveryDate = "DeliveryDate";
    public static final String IssueingPlant = "IssueingPlant";
    public static final String Value = "Value";
    public static final String Type = "Type";
    public static final String CustomerAccount = "CustomerAccount";
    public static final String DocumentNbr = "DocumentNbr";
    public static final String PostingDate = "PostingDate";
    public static final String SalesOrdNo = "SalesOrdNo";
    public static final String DocDate = "DocDate";
    public static final String PlantName = "PlantName";
    public static final String MaterialCode = "MaterialCode";
    public static final String Unrestricted = "Unrestricted";
    public static final String PlantID = "PlantID";
    public static final String StorageLoc = "StorageLoc";
    public static final String StorageLocDesc = "StorageLocDesc";
    public static final String OrderTypeText = "OrderTypeText";
    public static final String SoldToNo = "SoldToNo";
    public static final String ShipToNo = "ShipToNo";
    public static final String SalesOrg = "SalesOrg";
    public static final String DistChannel = "DistChannel";
    public static final String Division = "Division";
    public static final String IncoTerms1Text = "IncoTerms1Text";
    public static final String CustomerPo = "CustomerPo";
    public static final String SalesItemNo = "SalesItemNo";
    public static final String MaterialText = "MaterialText";
    public static final String DelvQty = "DelvQty";
    public static final String UnitOfMeasure = "UnitOfMeasure";
    public static final String DeliveryNo = "DeliveryNo";
    public static final String DocumentDate = "DocumentDate";
    public static final String ShipPoint = "ShipPoint";
    public static final String IssueQuantity = "IssueQuantity";
    public static final String WarehouseNo = "WarehouseNo";
    public static final String SalesOrderNo = "SalesOrderNo";
    public static final String ActualGiDate = "ActualGiDate";
    public static final String WarehouseNoTxt = "WarehouseNoTxt";
    public static final String CurrencyKey = "CurrencyKey";
    public static final String DelvItem = "DelvItem";
    public static final String StoItem = "StoItem";
    public static final String BalanceQty = "BalanceQty";
    public static final String Customer = "Customer";
    public static final String PaymentTerm = "PaymentTerm";
    public static final String PaymentTermDesc = "PaymentTermDesc";
    public static final String PaymentTermCode = "PaymentTermCode";
    public static final String Inco1 = "Inco1";
    public static final String Inco2 = "Inco2";
    public static final String ShippPoint = "ShippPoint";
    public static final String ShipPointDesc = "ShipPointDesc";
    public static final String ShippingPoint = "ShippingPoint";
    public static final String BatchInd = "BatchInd";
    public static final String UnitOfMeasureText = "UnitOfMeasureText";
    public static final String NetValue = "NetValue";
    public static final String StorLocDesc = "StorLocDesc";
    public static final String DelvPlant = "DelvPlant";
    public static final String CustPartnerNo = "CustPartnerNo";
    public static final String GrNo = "GrNo";
    public static final String IssuePlant = "IssuePlant";
    public static final String GrItemNo = "GrItemNo";
    public static final String Material = "Material";
    public static final String ReceivedQty = "ReceivedQty";
    public static final String ReceiptDate = "ReceiptDate";
    public static final String MatCondCat = "MatCondCat";
    public static final String PgiIndicator = "PgiIndicator";
    public static final String UomText = "UomText";
    public static final String SalesOrderItemNo = "SalesOrderItemNo";
    public static final String ActualQuantity = "ActualQuantity";
    public static final String PaymentDescription = "PaymentDescription";
    public static final String CompanyCode = "CompanyCode";
    public static final String StatusUpdate = "StatusUpdate";
    public static final String TaxRate = "TaxRate";
    public static final String Amounts = "Amount";
    public static final String CustomerPoDate = "CustomerPoDate";
    public static final String IncoTerms1 = "IncoTerms1";
    public static final String IncoTerms2 = "IncoTerms2";
    public static final String NetPrice = "NetPrice";
    public static final String BatchNo = "BatchNo";
    public static final String ShelExpDate = "ShelExpDate";
    public static final String ManfDate = "ManfDate";
    public static final String PaymentTermsText = "PaymentTermsText";
    public static final String PaymentTerms = "PaymentTerms";
    public static final String SalesOrders = "SalesOrders";
    public static final String SalesOrderItems = "SalesOrderItems";
    public static final String SalesOrderDataValt = "SalesOrderDataValt";
    public static final String NOTIFICATION_ITEM = "fromNotificationItem";
    public static final String MTPDataValt = "MTPDataValt";
    public static final String EXTRA_COME_FROM = "comeFrom";
    public static final String Plant = "Plant";
    public static final String ShippingTypeID = "ShippingTypeID";
    public static final String Payterm = "Payterm";
    public static final String Incoterm1 = "Incoterm1";
    public static final String Incoterm2 = "Incoterm2";
    public static final String MeansOfTranstyp = "MeansOfTranstyp";
    public static final String MeansOfTranstypDesc = "MeansOfTranstypDesc";
    public static final String SalesGroup = "SalesGroup";
    public static final String StorLoc = "StorLoc";
    public static final String UOMNO0 = "UOMNO0";
    public static final String AccountingDocNumber = "AccountingDocNumber";
    public static final String PartnerCustomerNo = "PartnerCustomerNo";
    public static final String OrderQty = "OrderQty";
    public static final String PartnerFunctionDesc = "PartnerFunctionDesc";
    public static final String RegionID = "RegionID";
    public static final String RegionDesc = "RegionDesc";
    public static final String CountryDesc = "CountryDesc";
    public static final String ECCNo = "ECCNo";
    public static final String CSTNo = "CSTNo";
    public static final String LSTNo = "LSTNo";
    public static final String ExciseRegNo = "ExciseRegNo";
    public static final String ServiceTaxRegNo = "ServiceTaxRegNo";
    public static final String CreditExposure = "CreditExposure";
    public static final String CreditLimitUsed = "CreditLimitUsed";
    public static final String AnnualSales = "AnnualSales";
    public static final String AnnualSalesYear = "AnnualSalesYear";
    public static final String ORDTY = "ORDTY";
    public static final String SPORTY = "SPORTY";
    public static final String PlantDesc = "PlantDesc";
    public static final String PaytermDesc = "PaytermDesc";
    public static final String Incoterm1Desc = "Incoterm1Desc";
    public static final String SalesAreaDesc = "SalesAreaDesc";
    public static final String PartnerFunctionID = "PartnerFunctionID";
    public static final String GSTIN = "GSTIN";
    public static final String ShippingConditionID = "ShippingConditionID";
    public static final String ShippingConditionDesc = "ShippingConditionDesc";
    public static final String DeliveringPlantID = "DeliveringPlantID";
    public static final String DeliveringPlantDesc = "DeliveringPlantDesc";
    public static final String TransportationZoneID = "TransportationZoneID";
    public static final String TransportationZoneDesc = "TransportationZoneDesc";
    public static final String Incoterms1ID = "Incoterms1ID";
    public static final String Incoterms1Desc = "Incoterms1Desc";
    public static final String Incoterms2 = "Incoterms2";
    public static final String PaymentTermID = "PaymentTermID";
    public static final String CreditControlAreaDesc = "CreditControlAreaDesc";
    public static final String CustomerGrpID = "CustomerGrpID";
    public static final String SH = "SH";
    public static final String PartnerTypeID = "PartnerTypeID";
    public static final String PartnerCustomerName = "PartnerCustomerName";
    public static final String Recievables = "Recievables";
    public static final String SpecialLiabilities = "SpecialLiabilities";
    public static final String SalesValue = "SalesValue";
    public static final String CreditLimitUsedPerc = "CreditLimitUsedPerc";
    public static final String dtFormat_ddMMyyyy = "dd/MM/yyyy";
    public static final String CreditControlAreaID = "CreditControlAreaID";
    public static final String CreditControlDesc = "CreditControlDesc";
    public static final String MaterialByCustomers = "MaterialByCustomers";
    public static final int NAVIGATE_TO_PARENT_ACTIVITY = 99;
    public static final int NAVIGATE_TO_CHILD_ACTIVITY = 99;
    public static final String SFSO = "SFSO";
    public static final String TextIDDesc = "TextIDDesc";
    public static final String SOTexts = "SOTexts";
    public static final String ONETIMESHP = "ONETIMESHP";
    public static final String SOS_ENTITY = ".SO";
    public static final String SOS_ITEM_DETAILS_ENTITY = ".SOItemDetail";
    public static final String SOS_ITEM_SCHEDULE_ENTITY = ".SOItemSchedule";
    public static final String SOS_ITEM_CONDITION_ITEM_DETAILS_ENTITY = ".SOConditionItemDetail";
    public static final String SOS_ITEM_CONDITION_ENTITY = ".SOCondition";
    public static final String SOS_SO_TEXT_ENTITY = ".SOText";
    public static final String RE = "RE";
    public static final String LIST = "lists";
    public static final String CUSTOMER_ENTITY = ".Customer";
    public static final String HDRNTTXTID = "HDRNTTXTID";
    public static final String SOS_PARTNER_FUNCTIONS_ENTITY = ".SOPartnerFunction";
    public static final String SOPartnerFunctions = "SOPartnerFunctions";
    public static final String EXTRA_SO_HEADER = "Header";
    public static final String EXTRA_SO_ITEM_LIST = "itemList";
    public static final String EXTRA_Is_Simulated = "isSimulated";
    public static final String ExpenseFreq = "ExpenseFreq";
    public static final String ExpenseDaily = "000010";
    public static final String ExpenseMonthly = "000030";
    public static final String ExpenseType = "ExpenseType";
    public static final String ExpenseTypeDesc = "ExpenseTypeDesc";
    public static final String ExpenseItemType = "ExpenseItemType";
    public static final String ExpenseItemTypeDesc = "ExpenseItemTypeDesc";
    public static final String ExpenseFreqDesc = "ExpenseFreqDesc";
    public static final String ExpenseItemCat = "ExpenseItemCat";
    public static final String ExpenseItemCatDesc = "ExpenseItemCatDesc";
    public static final String DefaultItemCat = "DefaultItemCat";
    public static final String DefaultItemCatDesc = "DefaultItemCatDesc";
    public static final String AmountCategory = "AmountCategory";
    public static final String AmountCategoryDesc = "AmountCategoryDesc";
    public static final String MaxAllowancePer = "MaxAllowancePer";
    public static final String ExpenseQuantityUom = "ExpenseQuantityUom";
    public static final String ItemFieldSet = "ItemFieldSet";
    public static final String ItemFieldSetDesc = "ItemFieldSetDesc";
    public static final String Allowance = "Allowance";
    public static final String IsSupportDocReq = "IsSupportDocReq";
    public static final String IsRemarksReq = "IsRemarksReq";
    public static final String ExpenseGUID = "ExpenseGUID";
    public static final String FiscalYear = "FiscalYear";
    public static final String ExpenseNo = "ExpenseNo";
    public static final String ExpenseDate = "ExpenseDate";
    public static final String ExpenseItemGUID = "ExpenseItemGUID";
    public static final String ExpeseItemNo = "ExpeseItemNo";
    public static final String BeatGUID = "BeatGUID";
    public static final String ConvenyanceMode = "ConvenyanceMode";
    public static final String ConvenyanceModeDs = "ConvenyanceModeDs";
    public static final String Distance = "Distance";
    public static final String BeatDistance = "BeatDistance";
    public static final String ConveyanceAmt = "ConveyanceAmt";
    public static final String ExpenseDocumentID = "ExpenseDocumentID";
    public static final String DocumentTypeID = "DocumentTypeID";
    public static final String DocumentTypeDesc = "DocumentTypeDesc";
    public static final String DocumentStatusID = "DocumentStatusID";
    public static final String DocumentStatusDesc = "DocumentStatusDesc";
    public static final String DocumentMimeType = "DocumentMimeType";
    public static final String DocumentSize = "DocumentSize";
    public static final String ExpenseConfigs = "ExpenseConfigs";
    public static final String ExpenseAllowances = "ExpenseAllowances";
    public static final int TAKE_PICTURE = 190;
    public static final String UserCustomers = "UserCustomers";
    public final static String TABLE_NAME = "PriceUpdate"; // name of table
    public final static String Price_ID = "_id";
    public final static String master_brand = "master_brand";
    public final static String brand = "brand";
    public final static String BP_EX = "BP_EX";
    public final static String BP_For = "BP_For";
    public final static String WSP = "WSP";
    public final static String RSP = "RSP";
    public final static String date = "todays_date";
    public static final String str_03 = "03";
    public static final String str_05 = "05";
    public static final String AmtDue = "AmtDue";
    public static final String DocumentNo = "DocumentNo";
    public static final String CollectionTypeID = "CollectionTypeID";
    public static final String PaymentMethodID = "PaymentMethodID";
    public static final String PaymentMethodDesc = "PaymentMethodDesc";
    public static final String CollectionTypeDesc = "CollectionTypeDesc";
    public static final String InvoicedAmount = "InvoicedAmount";
    public static final String CollectedAmount = "CollectedAmount";
    public static final String OpenAmount = "OpenAmount";
    public static final String InvoiceTypeDesc = "InvoiceTypeDesc";
    public static final String InvoiceType = "InvoiceType";
    public static final String InvoiceTypDesc = "InvoiceTypDesc";
    public static final String SOS_SO_TASK_ENTITY = ".Task";
    public static final int PERMISSION_REQUEST_CODE = 110;
    public static final String InvoicePartnerFunctions = "InvoicePartnerFunctions";
    public static final String ConditionCatDesc = "ConditionCatDesc";
    public static final String ConditionCatID = "ConditionCatID";
    public static final String EXTRA_SO_DETAIL = "openSODetails";
    public static final String STORAGELOCDESC = "STORAGELOCDESC";
    public static final String MTPList = "MTPList";
    public static final String Brand = "Brand";
    public static final String TECHNICAL_HEADER_DETAILS = "header_details";
    public static final String EXTRA_COLLECTION_DETAIL = "CollectionDetails";
    /* COllection plan properties*/
    public static final String CollectionPlan = "CollectionPlans";
    public static final String CollectionPlanItemDetails = "CollectionPlanItemDetails";
    public static final String Fiscalyear = "Fiscalyear";
    public static final String RTGSDataValt = "RTGSDataValt";
    public static final int STORAGE_PERMISSION_CONSTANT = 890;
    public static String fromNotificationDetail = "fromNotificationDetail";
    public static AlertDialog alert = null;
    public static Timer timer = null;
    public static TimerTask timerTask = null;
    public static boolean isFlagVisiable = false;
    public static boolean isStoreOpened = false;
    public static final String isBlocked = "isBlocked";
    public static final String String1 = "String1";
    public static final String application = "application";
    public static final String salesArea = "salesArea";
    /*SPGeo*/
    /*DashBroad Online Store*/
    public static String DashBroad_Error_Msg = "";
    public static String DashBoards = "DashBoards";
    public static Boolean IsOnlineStoreFailedDashBroad = false;
    public static String SPGEOENTITY = ".SPGEO";
    public static String Total_Order_Value_KEY = "Total_Order_Value_KEY";
    public static String Last_Relese_Date = "29-09-2017 21:10";
    public static String About_Version = "3.0.0.1h";
    public static EventDataSqlHelper events;
    public static int SO_LIST_POS = 2;
    public static int SO_LIST_POS_3 = 3;
    public static int SO_LIST_POS_4 = 4;
    public static int SO_LIST_POS_5 = 5;
    public static String EXTRA_SO_BEAN = "extraSOBean";
    public static String EXTRA_FROM_CC = "isFromCC";
    public static String EXTRA_SO_TITLE = "actionBarTitle";
    public static String EXTRA_SO_INSTANCE_ID = "instanceID";
    public static String EXTRA_SO_NO = "extraSONo";
    public static String CUSTOMERNUMBER = "";
    public static String CUSTOMERNAME = "";
    public static boolean isInvoicesCountDone = false;
    public static boolean isInvoicesItemsCountDone = false;
    public static boolean isAuthDone = false;
    public static String ComingFromCreateSenarios = "";
    public static boolean isSync = false;
    public static boolean isBackGroundSync = false;
    public static boolean isPullDownSync = false;
    public static boolean isLocationSync = false;
    public static String CollDate = "CollDate";
    public static String FISDocNo = "FISDocNo";
    public static HashMap<String, ArrayList<SKUGroupBean>> HashMapSubMaterials = new HashMap<>();
    public static ArrayList<SKUGroupBean> selectedSOItems = new ArrayList<>();
    public static Boolean isAlertRecordsAvailable = false;
    public static String ForwarAgentCode = "FrwadgAgent";
    public static String ForwarAgentDesc = "FrwadgAgentName";
    public static String USERROLE = "UserRole";
    public static String USERPARNTERID = "UserPartnerID";
    public static String USERROLELOGINID = "UserRoleLoginID";
    public static String isRollResponseGot = "isRollResponseGot";
    public static SQLiteDatabase EventUserHandler;
    public static boolean devicelogflag = false;
    public static boolean importdbflag = false;
    public static boolean FlagForUpdate = false;
    public static boolean FlagForSecurConnection = false;
    public static MSFAApplication mApplication = null;
    public static boolean FlagForSyncAllUpdate = false;
    public static boolean FlagErrorLogAllSync = false;
    public static String DATABASE_NAME = "mSFAAIRCEL.db";
    public static String DATABASE_REGISTRATION_TABLE = "registrationtable";
    public static String APPS_NAME = "mSFAAIRCEL";
    public static String AppName_Key = "AppName";
    public static String UserName_Key = "username";
    public static String Customers = "Customers";
    public static String SONo = "SONo";
    public static String LoginID = "LoginID";
    public static String SOs = "SOs";
    public static String TaskHistorys = "TaskHistorys";
    public static String ActionName = "ActionName";
    public static String TaskStatusID = "TaskStatusID";
    public static String PerformedByName = "PerformedByName";
    public static String Timestamp = "Timestamp";
    public static String TotalAmount = "TotalAmount";
    public static String TotalMTPCount = "TotalMTPCount";
    public static String TotalSOCount = "TotalSOCount";
    public static String YES = "YES";
    public static String SegmentId = "SegmentId";
    public static String SegmentDesc = "SegmentDesc";
    public static String BrandsCategories = "BrandsCategories";
    public static String OrderMaterialGroups = "OrderMaterialGroups";
    public static String Brands = "Brands";
    public static String MaterialCategories = "MaterialCategories";
    public static String BrandID = "BrandID";
    public static String Materials = "Materials";
    public static String BrandDesc = "BrandDesc";
    public static String MaterialCategoryID = "MaterialCategoryID";
    public static String MaterialCategoryDesc = "MaterialCategoryDesc";
    public static String DMSDivision = "DMSDivision";
    public static String DMSDivisionDesc = "DMSDivisionDesc";
    public static String Category = "Category";
    public static String CRS_SKU_GROUP = "CRS SKU Group";
    public static String OrderMaterialGroupDesc = "OrderMaterialGroupDesc";
    public static String OrderMaterialGroupID = "OrderMaterialGroupID";
    public static String Others = "Others";
    public static String EncryptKey = "welcome1";
    public static String collections[] = null;
    public static String Table[] = null;
    public static String clumsName[] = null;
    public static String serviceDoc = null;
    public static String cookies = "";
    public static String metaDoc = null;
    public static String x_csrf_token = "";
    public static String ABOUTVERSION = "3.0";
    public static String ABOUTDATE = "Nov 13,2015, 23:59:00";
    public static int autoduration = 30;
    public static String USERTYPE = "T";
    public static String CollAmount = "";
    public static String SyncTime = "11";
    public static volatile boolean iSAutoSync = false;
    // public static LiteMessagingClient lm = null;
    // public static LiteUserManager lurm = null;
    public static int autoSyncDur = 360;
    public static boolean crashlogflag = false;
    public static double MaterialUnitPrice = 0.0, MaterialNetAmount = 0.0, InvoiceTotalAmount = 0.0, InvoiceUnitPrice = 0.0;
    public static ArrayList<InvoiceBean> alTempInvoiceList = new ArrayList<>();
    public static Hashtable<String, ArrayList<InvoiceBean>> HashTableSerialNoSelection = new Hashtable<String, ArrayList<InvoiceBean>>();
    public static String ReferenceTypeID = "ReferenceTypeID";
    public static String ReferenceTypeDesc = "ReferenceTypeDesc";
    public static String Name = "Name";
    public static String CPUID = "CPUID";
    public static String BankName = "BankName";
    public static String Fresh = "Fresh";
    public static String CRDCTL = "CRDCTL";
    public static boolean BoolMoreThanOneRoute = false;
    public static String SFInvoiceItemDetails = "InvoiceItemDetails";
    public static String ActualInvQty = "ActualInvQty";
    public static String titleHistory = "History";
    public static String titlePending = "Pending Sync";
    public static String Status = "Status";
    public static String ViisitCPNo = "ViisitCPNo";
    public static String STATUS = "Status";
    public static String CustomerPartnerFunctions = "CustomerPartnerFunctions";
    public static String CustomerSalesAreas = "CustomerSalesAreas";
    public static String MaterialSaleAreas = "MaterialSaleAreas";
    public static String SOConditionItemDetails = "SOConditionItemDetails";
    public static String DelvStatus = "DelvStatus";
    public static String DelvStatusId = "DelvStatusID";
    public static String TypesName = "TypesName";
    public static String DELVST = "DELVST";
    public static String RejReason = "RejReason";
    public static String RejReasonDesc = "RejReasonDesc";
    public static String SOUpdate = "SOUpdate";
    public static String SaleOffDesc = "SaleOffDesc";
    public static String DelvStatusID = "DelvStatusID";
    public static String DelvStatusDesc = "DelvStatusDesc";
    public static String DiscountPer = "DiscountPer";
    public static String OpenQty = "OpenQty";
    public static String OwnStock = "OwnStock";
    public static String ItemCatDesc = "ItemCatDesc";
    public static String SaleGrpDesc = "SaleGrpDesc";
    public static String REJRSN = "REJRSN";
    public static String SOChange = "SOChange";
    public static String comingFromChange = "comingFromChange";
    public static String comingFromVal = "";
    public static String SOCancel = "SOCancel";
    public static String isSOChangeEnabled = "isSOChangeEnabled";
    public static String isSOCancelEnabled = "isSOCancelEnabled";
    public static String isRetailerListEnabled = "isRetailerListEnabled";
    public static String isSOWithSingleItemEnabled = "isSOWithSingleItemEnabled";
    public static String FeedbackSubTypeID = "FeedbackSubTypeID";
    public static String FeedbackSubTypeDesc = "FeedbackSubTypeDesc";
    public static String FeedbackSubType = "FeedbackSubType";
    public static String FeedbackID = "03";
    public static String Feedback = "Feedback";
    public static String FeedBackGuid = "FeedBackGuid";
    public static String FeedbackDesc = "FeedbackDesc";
    public static String NotPurchasedType = "000004";
    public static String AsOnDate = "AsOnDate";
    public static String InvoiceQty = "InvoiceQty";
    public static String PaymentStatus = "PaymentStatus";
    public static String ROLL_ID_CSTMR = "000003";
    public static String InstanceID = "InstanceID";
    public static String Initiator = "Initiator";
    public static String EntityKeyID = "EntityKeyID";
    public static String EntityKey = "EntityKey";
    public static String EntityDate1 = "EntityDate1";
    public static String EntityKeyDesc = "EntityKeyDesc";
    public static String EntityValue1 = "EntityValue1";
    public static String EntityCurrency = "EntityCurrency";
    public static String PriorityNumber = "PriorityNumber";
    public static String EntityAttribute1 = "EntityAttribute1";
    public static String EntityAttribute5 = "EntityAttribute5";
    public static String EntityAttribute6 = "EntityAttribute6";
    public static String EntityAttribute7 = "EntityAttribute7";
    public static String PasswordExpiredMsg = "User locked or password expired. Click settings to change/update password or please contact channel team";
    public static String IDPACCNAME = "IDPACCNAME";
    public static String DOMAINNAME = "DOMAINNAME";
    public static String SPID = "SPID";
    public static String SITENAME = "SITENAME";
    public static String TRGURL = "TRGURL";
    public static String SRCURL = "SRCURL";
    public static String FGTURL = "FGTURL";
    public static String RejectedStatusID = "04";
    public static int NewDefingRequestVersion = 34;
    public static int IntializeDBVersion = 3;
    public static HashMap<String, String> httpErrorCodes = new HashMap<String, String>();
    //for MAF LOGON Start
    public static String appConID_Text = "", appEndPoint_Text = "",
            pushEndPoint_Text;
    //ID4 HCPMS
    //public static String APP_ID = "com.arteriatech.mSFADev";
    public static String APP_ID = "com.arteriatech.mSFA";
    //ID4`
    public static String FeedbackEntity = ".Feedback";
    public static String VISITACTIVITYENTITY = ".VisitActivity";
    public static String FeedbackItemDetailEntity = ".FeedbackItemDetail";
    public static String VISITENTITY = ".Visit";
    public static String CUSTOMERENTITY = ".Customer";
    public static String ATTENDANCEENTITY = ".Attendance";
    public static String MERCHINDISINGENTITY = ".MerchReview";
    public static String MERCHINDISINGITEMENTITY = ".MerchReviewImage";
    public static String ChannelPartnerEntity = ".ChannelPartner";
    public static String InvoiceEntity = ".SSInvoice";
    public static String InvoiceItemEntity = ".SSInvoiceItemDetail";
    public static String InvoiceSerialNoEntity = ".SSInvoiceItemSerialNo";
    public static String FinancialPostingsEntity = ".FinancialPosting";
    public static String FinancialPostingsItemEntity = ".FinancialPostingItemDetail";
    public static String CompetitorInfoEntity = ".CompetitorInfo";
    public static String SPStockSNosEntity = ".SPStockItemSNo";
    public static String CPStockItemEntity = ".CPStockItem";
    public static String ComplaintEntity = ".Complaint";
    public static String STOCK_ENTITY = ".Stock";
    public static String RouteScheduleEntity = ".RouteSchedule";
    public static String RouteSchedulePlanEntity = ".RouteSchedulePlan";
    public static String RouteScheduleSPEntity = ".RouteScheduleSP";
    public static String BaseUOM = "BaseUOM";
    public static String CustomerStock = "CustomerStock";
    public static String NO_OF_DAYS = "0";
    public static String SalesPersons = "SalesPersons";
    public static String CollectionEntity = ".Collection";
    public static String CollectionItemEntity = ".CollectionItemDetail";
    public static HashMap<String, SOItemBean> MapMatGrpByMaterial = new HashMap<>();
    public static String ProdCatg = "ProdCatg";
    public static String ProdCatgDesc = "ProdCatgDesc";
    public static String SkuGroup = "SkuGroup";
    public static String SkuGroupDesc = "SkuGroupDesc";
    public static String Banner = "Banner";
    public static String InvoiceNo = "InvoiceNo";
    public static String InvoiceTypeID = "InvoiceTypeID";
    public static String UnitPrice = "UnitPrice";
    public static String NetAmount = "NetAmount";
    public static String CollectionAmount = "CollectionAmount";
    public static String ShipToName = "ShipToName";
    public static String ShipTo = "ShipTo";
    public static String ReferenceNo = "ReferenceNo";
    public static String GrossAmount = "GrossAmount";
    public static String InvoiceNumber = "";
    public static String FIPDocumentNumber = "";
    public static ODataGuid VisitActivityRefID = null;
    public static String CompetitorName = "CompName";
    public static String CompetitorGUID = "CompGUID";
    public static String PerformanceTypeID = "PerformanceTypeID";
    public static String AttendanceTypeH1 = "AttendanceTypeH1";
    public static String AttendanceTypeH2 = "AttendanceTypeH2";
    public static String AutoClosed = "AutoClosed";
    public static String PerformanceOnIDDesc = "PerformanceOnIDDesc";
    public static String Material_Catgeory = "MaterialCategory";
    public static String DbBatch = "Batch";
    public static String ManufacturingDate = "ManufacturingDate";
    public static String Material_No = "MaterialNo";
    public static String Material_Desc = "MaterialDesc";
    public static String BaseUom = "BaseUom";
    public static String BasePrice = "BasePrice";
    public static String SPStockItemGUID = "SPStockItemGUID";
    public static String SPSNoGUID = "SPSNoGUID";
    public static String SerialNoTo = "SerialNoTo";
    public static String SerialNoFrom = "SerialNoFrom";
    public static String Option = "Option";
    public static String StockTypeID = "StockTypeID";
    public static String QAQty = "QAQty";
    public static String UnrestrictedQty = "UnrestrictedQty";
    public static String BlockedQty = "BlockedQty";
    public static String PrefixLength = "PrefixLength";
    public static String Zzindicator = "Zzindicator";
    public static String EvaluationTypeID = "EvaluationTypeID";
    public static String ReportOnID = "ReportOnID";
    public static String QtyTarget = "QtyTarget";
    public static String QtyLMTD = "QtyLMTD";
    public static String QtyMTD = "QtyMTD";
    public static String QtyMonthlyGrowth = "QtyMonthlyGrowth";
    public static String QtyMonth1PrevPerf = "QtyMonth1PrevPerf";
    public static String QtyMonth2PrevPerf = "QtyMonth2PrevPerf";
    public static String QtyMonth3PrevPerf = "QtyMonth3PrevPerf";
    public static String AmtTarget = "AmtTarget";
    public static String AmtLMTD = "AmtLMTD";
    public static String AmtMTD = "AmtMTD";
    public static String AmtMonth1PrevPerf = "AmtMonth1PrevPerf";
    public static String AmtMonthlyGrowth = "AmtMonthlyGrowth";
    public static String AmtMonth2PrevPerf = "AmtMonth2PrevPerf";
    public static String AmtMonth3PrevPerf = "AmtMonth3PrevPerf";
    public static String PerformanceOnID = "PerformanceOnID";
    public static String PerformanceGUID = "PerformanceGUID";
    public static String QtyLastYearMTD = "QtyLastYearMTD";
    public static String AmtLastYearMTD = "AmtLastYearMTD";
    public static double RCVStockValueDouble = 0.0;
    public static double SIMStockValue = 0.0;
    public static String StockValue = "StockValue";
    public static String CPStockItemGUID = "CPStockItemGUID";
    public static String ComplaintCategory = "ComplaintCategory";
    public static String RschGuid = "RschGuid";
    public static String RouteSchGUID = "RouteSchGUID";
    public static String VisitCPGUID = "VisitCPGUID";
    public static String VisitCPName = "VisitCPName";
    public static String SalesPersonID = "SalesPersonID";
    public static String ShortName = "ShortName";
    public static String RoutId = "RoutId";
    public static String SequenceNo = "SequenceNo";
    public static String DayOfWeek = "DayOfWeek";
    public static String DayOfMonth = "DayOfMonth";
    public static String DayDashBoardList = "DayDashBoardList";
    public static String MonthDashBoardList = "MonthDashBoardList";
    public static String SharMonthDashBoardList = "SharMonthDashBoardList";
    public static String DOW = "DOW";
    public static String DOM = "DOM";
    public static String SalesOrderEntity = ".SO";
    public static String SalesOrderItemEntity = ".SOItemDetail";
    public static boolean isImgCapAtGmLevel = false;
    public static boolean isRunning = false;
    public static boolean isStoreClosed = false;
    public static Boolean IsOnlineStoreFailed = false;
    public static ArrayList<String> selectedPositionsDemon = new ArrayList<String>();
    public static ArrayList<String> selectedPositionsProm = new ArrayList<String>();
    public static HashMap<String, String> selectedMatGrpStatusDemon = new HashMap<String, String>();
    public static HashMap<String, String> selectedMatGrpStatusPrompt = new HashMap<String, String>();
    public static String collectionName[] = null;
    public static boolean isCustContactLists;
    public static boolean isCustomerLists;
    public static boolean FlagForSyncError = false;
    public static String resSO = "";
    public static String reqSO = "";
    public static String CREATEREQUEST = "Create_Request";
    public static String UPDATEREQUEST = "Update_Request";
    public static String STOREOPENREQUEST = "Store_Open_Request";
    public static String DELETEREQUEST = "Delete_Request";
    public static String UPLOADFILEREQUEST = "Create_Request";
    public static String FLUSHREQUEST = "Flush_Request";
    public static String REFRESHREQUEST = "Refresh_Request";
    public static HashMap<String, Object> MapEntityVal = new HashMap<String, Object>();
    public static String COLLECTIONHDRS = "CollectionHdrs";
    public static String COLLECTIONITEMS = "CollectionItems";
    public static String OPEN_INVOICE_LIST = "OpenInvList";
    public static String INVOICES = "Invoices";
    public static String VISITACTIVITIES = "VisitActivities";
    public static String INVOICESSERIALNUMS = "InvoiceItmSerNumList";
    public static String ENDLONGITUDE = "EndLongitude";
    public static String REMARKS = "Remarks";
    public static String VISITKEY = "VisitGUID";
    public static String ROUTEPLANKEY = "RoutePlanGUID";
    public static String LOGINID = "LoginID";
    public static String DATE = "Date";
    ;
    public static String VISITTYPE = "VisitType";
    public static String CUSTOMERNO = "CustomerNo";
    public static String REASON = "Reason";
    public static String STARTDATE = "StartDate";
    public static String STARTTIME = "StartTime";
    public static String STARTLATITUDE = "StartLatitude";
    public static String STARTLONGITUDE = "StartLongitude";
    public static String ENDTIME = "EndTime";
    public static String ENDDATE = "EndDate";
    public static String ENDLATITUDE = "EndLatitude";
    public static String ETAG = "ETAG";
    public static String VisitActivityGUID = "VisitActivityGUID";
    public static String VisitGUID = "VisitGUID";
    public static String ActivityType = "ActivityType";
    public static String ActivityTypeDesc = "ActivityTypeDesc";
    public static String ActivityRefID = "ActivityRefID";
    public static boolean flagforexportDB;
    public static String Validity = "Validity";
    public static String Benefits = "Benefits";
    public static String Price = "Price";
    public static String ItemNo = "ItemNo";
    public static String SchemeDesc = "SchemeDesc";
    public static String SchemeGuid = "SchemeGuid";
    public static String ReviewDate = "ReviewDate";
    public static String CPTypeID = "CPTypeID";
    public static String SPGuid = "SPGuid";
    public static String EntityAttribute4 = "EntityAttribute4";
    public static String SoldToCPGUID = "SoldToCPGUID";
    public static String ShipToCPGUID = "ShipToCPGUID";
    public static String SoldToTypeID = "SoldToTypeID";
    public static String ShipToTypeID = "ShipToTypeID";
    public static String CPName = "CPName";
    public static String Address1 = "Address1";
    public static String CountryID = "CountryID";
    //	public static String Country = "Country";
    public static String BTSCircle = "BTSCircle";
    public static String DesignationID = "DesignationID";
    public static String DesignationDesc = "DesignationDesc";
    public static String DistrictDesc = "DistrictDesc";
    public static String CityDesc = "CityDesc";
    public static String CityID = "CityID";
    public static String DistrictID = "DistrictID";
    public static String VisitNavigationFrom = "";
    public static String BirthDayAlertsKey = "BirthDayAlertsKey";
    public static String BirthDayAlertsDate = "BirthDayAlertsDate";
    public static String DBStockKey = "DBStockKey";
    public static String DBStockKeyDate = "DBStockKeyDate";
    public static String District = "District";
    public static String StateID = "StateID";
    public static String Landmark = "Landmark";
    public static String PostalCode = "PostalCode";
    public static String SalesPersonMobileNo = "MobileNo";
    public static String MobileNo = "Mobile1";
    public static String CPMobileNo = "CPMobileNo";
    public static String EmailID = "EmailID";
    public static String ExternalRefID = "ExternalRefID";
    public static String DOB = "DOB";
    public static String PAN = "PAN";
    public static String VATNo = "VATNo";
    public static String TIN = "TIN";
    public static String OwnerName = "OwnerName";
    public static String OutletName = "Name";
    public static String RetailerProfile = "Group1";
    public static String Group2 = "Group2";
    public static String Latitude = "Latitude";
    public static String Longitude = "Longitude";
    public static String SetResourcePath = "SetResourcePath";
    public static String PartnerMgrGUID = "PartnerMgrGUID";
    public static String OtherCustGuid = "OtherCustGuid";
    public static String CPGUID32 = "CPGUID32";
    public static String CPGUID = "CPGUID";
    public static String ZSchemeTypeDesc = "ZSchemeTypeDesc";
    public static String ZSchemeType = "ZSchemeType";
    public static String ZSchemeValidTo = "ZSchemeValidTo";
    public static String ZSchemeValidFrm = "ZSchemeValidFrm";
    public static String ClaimAmount = "ClaimAmount";
    public static String ZMaxClaimAmt = "ZMaxClaimAmt";
    public static String CPGuid = "CPGuid";
    public static String SyncHistorys = "SyncHistorys";
    public static String UserPartners = "UserPartners";
    public static String OINVAG = "OINVAG";
    public static String AccountGrp = "AccountGrp";
    public static String Anniversary = "Anniversary";
    public static String ApprovedAt = "ApprovedAt";
    public static String ApprovedBy = "ApprovedBy";
    public static String ApprovedOn = "ApprovedOn";
    public static String ApprvlStatusDesc = "ApprvlStatusDesc";
    public static String ApprvlStatusID = "ApprvlStatusID";
    public static String ChangedAt = "ChangedAt";
    public static String ChangedOn = "ChangedOn";
    public static String Country = "Country";
    public static String CountryName = "CountryName";
    public static String CPStock = "CPStock";
    public static String CPTypeDesc = "CPTypeDesc";
    public static String EvaluationTypeDesc = "EvaluationTypeDesc";
    public static String CreatedAt = "CreatedAt";
    public static String CreditDays = "CreditDays";
    public static String CreditLimit = "CreditLimit";
    public static String Totaldebit = "TotDebitBal";
    public static String Group1Desc = "Group1Desc";
    public static String Group2Desc = "Group2Desc";
    public static String Group3 = "Group3";
    public static String Group3Desc = "Group3Desc";
    public static String Group4 = "Group4";
    public static String Group4Desc = "Group4Desc";
    public static String IsKeyCP = "IsKeyCP";
    public static String Landline = "Landline";
    public static String Mobile2 = "Mobile2";
    public static String ParentTypDesc = "ParentTypDesc";
    public static String ParentTypeID = "ParentTypeID";
    public static String PartnerMgrName = "PartnerMgrName";
    public static String PartnerMgrNo = "PartnerMgrNo";
    public static String SalesGroupID = "SalesGroupID";
    public static String SalesGrpDesc = "SalesGrpDesc";
    public static String SalesOffDesc = "SalesOffDesc";
    public static String SalesOfficeID = "SalesOfficeID";
    public static String SearchTerm = "SearchTerm";
    public static String StateDesc = "StateDesc";
    public static String StatusDesc = "StatusDesc";
    public static String TownID = "TownID";
    public static String UOM = "UOM";
    public static String ItemFlag = "ItemFlag";
    public static String ZoneDesc = "ZoneDesc";
    public static String ZoneID = "ZoneID";
    public static String SIMStockUOM = "";
    public static String str_02 = "02";
    public static String InvoiceHisNo = "InvoiceNo";
    public static String InvoiceDate = "InvoiceDate";
    public static String InvoiceAmount = "InvoiceAmount";
    public static String InvoiceAmount1 = "GrossAmount";
    public static String InvoiceStatus = "InvoiceStatus";
    public static String GRStatus = "GRStatus";
    public static String ZZStateDesc = "ZZStateDesc";
    public static String ZZState = "ZZState";
    public static String ZZShortDmgSts = "ZZShortDmgSts";
    public static String ZZSalesDistDesc = "ZZSalesDistDesc";
    public static String ZZSalesDist = "ZZSalesDist";
    public static String ZZSalesAreaDesc = "ZZSalesAreaDesc";
    public static String ZZDistrict = "ZZDistrict";
    public static String ZZGRNo = "ZZGRNo";
    public static String ZZSalesArea = "ZZSalesArea";
    public static String ZZTranspDate = "ZZTranspDate";
    public static String ZZGRDate = "ZZGRDate";
    public static String ZZTranspTime = "ZZTranspTime";
    public static String ZZTotalTime = "ZZTotalTime";
    public static String ZZGRTime = "ZZGRTime";
    public static String RefDocNo = "RefDocNo";
    public static String InvoiceGUID = "InvoiceGUID";
    public static String OutAmount = "OutAmount";
    public static String SoldToName = "SoldToName";
    public static String SoldToID = "SoldToID";
    public static String TypesValue = "TypeValue";
    public static String PassedFrom = "PassedFrom";
    public static String CPNo = "CPNo";
    public static String RetailerName = "Name";
    public static String Address2 = "Address2";
    public static String Address3 = "Address3";
    public static String Address4 = "Address4";
    public static String TownDesc = "TownDesc";
    public static String ParentID = "ParentID";
    public static String ParentName = "ParentName";
    public static String StatusID = "StatusID";
    public static String StatusIdRetailer = "01";
    public static String VisitSeq = "VisitSeq";
    public static String Description = "Description";
    public static String EXTRA_COMPLAINT_BEAN = "ExtraComplaintBean";
    public static String CategoryId = "CategoryId";
    public static String VoiceBalance = "VoiceBalance";
    public static String DataBalance = "DataBalance";
    public static String Last111Date = "Last111Date";
    public static String OutstandingAmt = "OutstandingAmt";
    public static String LastInvAmt = "LastInvAmt";
    public static String NewLaunchedProduct = "New Launched Product";
    public static String MustSellProduct = "Must Sell Product";
    public static String FocusedProduct = "Focused Product";
    public static final String[] reportsArray = {SALESORDER,
            "Collection History", "Outstanding", MustSellProduct, FocusedProduct, NewLaunchedProduct,
            SnapshotList, "Invoices", "Credit Status", "Merchandising Snapshot List", "Feedback List", "Dealer Trends",
            "Complaint List", SALESORDER, "Invoice History", "Outstanding", "Distributor Trend", "ROs", "ROs","GR Report"};
    public static String SalesOrderCreate = "Sales Order Create";
    public static String StockGuid = "StockGuid";
    public static String MerchReviewGUID = "MerchReviewGUID";
    public static String SPNo = "SPNo";
    public static String SPName = "SPName";
    public static String SPGUID = "SPGUID";
    public static String DistanceUOM = "DistanceUOM";
    public static String AppVisibility = "AppVisibility";
    public static String MerchReviewType = "MerchReviewType";
    public static String MerchReviewTypeDesc = "MerchReviewTypeDesc";
    public static String MerchReviewTime = "MerchReviewTime";
    public static String CreatedBy = "CreatedBy";
    public static String CrdtCtrlArea = "CrdtCtrlArea";
    public static String CrdtCtrlAreaDs = "CrdtCtrlAreaDesc";
    public static String CreatedOn = "CreatedOn";
    public static String ChangedBy = "ChangedBy";
    public static String TestRun = "TestRun";
    public static String SPCategoryDesc = "SPCategoryDesc";
    public static String FeedbackNo = "FeedbackNo";
    public static String FeebackGUID = "FeebackGUID";
    public static String FeedbackType = "FeedbackType";
    public static String FeedbackTypeDesc = "FeedbackTypeDesc";
    public static String SPCategoryID = "SPCategoryID";
    public static String Location = "Location";
    public static String Location1 = "Location1";
    public static String BTSID = "BTSID";
    public static String Testrun = "Testrun";
    public static String FeebackItemGUID = "FeebackItemGUID";
    public static String MerchReviewDate = "MerchReviewDate";
    public static String MerchReviewLat = "MerchReviewLat";
    public static String MerchReviewLong = "MerchReviewLong";
    public static String MerchImageGUID = "MerchImageGUID";
    public static String ImageMimeType = "ImageMimeType";
    public static String ImageSize = "ImageSize";
    public static String Image = "Image";
    public static String ImagePath = "ImagePath";
    public static String ImageByteArray = "ImageByteArray";
    public static String DocumentStore = "DocumentStore";
    public static String FileName = "FileName";
    public static String PlannedDate = "PlannedDate";
    public static String PlannedStartTime = "PlannedStartTime";
    public static String PlannedEndTime = "PlannedEndTime";
    public static String VisitTypeID = "VisitTypeID";
    public static String VisitTypeDesc = "VisitTypeDesc";
    public static String VisitDate = "VisitDate";
    public static String ProposedRoute = "ProposedRoute";
    public static String ApprovedRoute = "ApprovedRoute";
    public static String RouteID = "RouteID";
    public static String RouteDesc = "RouteDesc";
    public static String RoutePlanKey = "RoutePlanKey";
    public static String PaymentStatusID = "PaymentStatusID";
    public static String PaymentModeID = "PaymentModeID";
    public static String PaymentMode = "PaymentMode";
    public static String PaymentModeDesc = "PaymentModeDesc";
    public static String PaymetModeDesc = "PaymetModeDesc";
    public static String BranchName = "BranchName";
    public static String InstrumentNo = "InstrumentNo";
    public static String InstrumentDate = "InstrumentDate";
    public static String BankID = "BankID";
    public static String Remarks = "Remarks";
    public static String Currency = "Currency";
    public static String Amount = "Amount";
    public static String HighLevellItemNo = "HighLevellItemNo";


   /* public static String getLoginName() {

        String loginQry = Constants.SalesPersons;
        String mStrLoginName = "";
        try {
            mStrLoginName = OfflineManager.getLoginName(loginQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

        return mStrLoginName;
    }*/

    /* public static String getSalesPeronMobileNo() {

         String loginQry = Constants.SalesPersons + "?$select=" + Constants.SalesPersonMobileNo + " ";
         String mStrMobNo = "";
         try {
             mStrMobNo = OfflineManager.getSalePersonMobileNo(loginQry);
         } catch (OfflineODataStoreException e) {
             LogManager.writeLogError(Constants.error_txt + e.getMessage());
         }

         return mStrMobNo;
     }*/
    public static String FIPGUID = "FIPGUID";
    public static String FIPDocType = "FIPDocType";
    public static String FIPDate = "FIPDate";
    public static String FIPDocNo = "FIPDocNo";
    public static String FIPAmount = "FIPAmount";
    public static String DebitCredit = "DebitCredit";
    public static String ParentNo = "ParentNo";
    public static String SPFirstName = "SPFirstName";
    public static String Tax1Amt = "Tax1Amt";
    public static String Tax2Amt = "Tax2Amt";
    public static String Tax3Amt = "Tax3Amt";
    public static String Tax1Percent = "Tax1Percent";
    public static String Tax2Percent = "Tax2Percent";
    public static String Tax3Percent = "Tax3Percent";
    public static String ReferenceUOM = "ReferenceUOM";
    public static String RetOrdNo = "RetOrdNo";
    public static String OrderReasonID = "OrderReasonID";
    public static String OrderReasonDesc = "OrderReasonDesc";
    public static String State = "State";
    public static String SalesDist = "SalesDist";
    public static String Route = "Route";
    public static String SplProcessing = "SplProcessing";
    public static String SplProcessingDesc = "SplProcessingDs";
    public static String MatFrgtGrp = "MatFrgtGrp";
    public static String MatFrgtGrpDesc = "MatFrgtGrpDs";
    public static String SyncHisGuid = "SyncHisGuid";
    public static String SyncCollection = "Collection";
    public static String SyncApplication = "Application";
    public static String SyncDate = "SyncDate";
    public static String SyncHisTime = "SyncTime";
    public static String SyncTypeDesc = "SyncTypeDesc";
    public static String SyncType = "SyncType";
    public static String SyncHistroy = "SyncHistorys";

    //    public static boolean onGpsCheckCustomMessage(final Context context, String message) {
//        UtilConstants.canGetLocation(context);
//        if (!UtilConstants.canGetLocation(context)) {
//            AlertDialog.Builder gpsEnableDlg = new AlertDialog.Builder(context, R.style.MyTheme);
//            gpsEnableDlg
//                    .setMessage(message);
//            gpsEnableDlg.setPositiveButton("Enable",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent(
//                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                            context.startActivity(intent);
//                        }
//                    });
//            // on pressing cancel button
//            gpsEnableDlg.setNegativeButton("Cancel",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    });
//            // Showing Alert Message
//            gpsEnableDlg.show();
//        }
//        return GpsTracker.isGPSEnabled;
//    }
    public static String PartnerId = "PartnerId";
    public static String PartnerType = "PartnerType";
    public static String Sync_All = "000001";
    //    public static String All_DownLoad = "000002";
    public static String DownLoad = "000002";
    public static String UpLoad = "000003";
    public static String Auto_Sync = "000004";
    public static String Attnd_sync= "000005";
    public static String Target_sync= "000006";
    public static String MatGrpTrg_sync= "000007";
    public static String MTP_sync= "000008";
    public static String AdVst_sync= "000009";
    public static String SOPD_sync= "000010";
    public static String Invoice_sync= "000011";
    public static String CrdStatus_sync= "000012";
    public static String ProdPrc_sync= "000013";
    public static String BP_sync= "000014";
    public static String RTGS_sync= "000015";
    public static String Behav_sync= "000016";
    public static String SOBackground_sync= "000017";
    public static String SOPOSTBG_sync= "000018";
    public static String SOPostPD_sync= "000019";
    public static String Attnd_refresh_sync= "000020";
    public static String Initial_sync= "000021";
    public static String Geo_sync= "000022";
    public static String DB_pull_sync= "000023";
    public static String download_all_cancel_sync= "000024";
    public static String download_all_net_sync= "000028";
    public static String download_cancel_sync= "000026";
    public static String upload_cancel_sync= "000025";
    public static String upload_net_sync= "000027";



    public static String EndSync = "End";
    public static String StartSync = "Start";
    public static String RefGUID = "RefGUID";
    public static String FIPItemGUID = "FIPItemGUID";
    public static String ReferenceID = "ReferenceID";
    public static String ReferenceDate = "ReferenceDate";
    public static String BalanceAmount = "BalanceAmount";
    public static String ClearedAmount = "ClearedAmount";
    public static String FIPItemNo = "FIPItemNo";
    public static String FirstName = "FirstName";
    public static String SalesOffice = "SalesOffice";
    public static String LastName = "LastName";
    public static String AttendanceGUID = "AttendanceGUID";
    public static String StartDate = "StartDate";
    public static String StartTime = "StartTime";
    public static String EndTime = "EndTime";
    public static String StartLat = "StartLat";
    public static String StartLong = "StartLong";
    public static String EndDate = "EndDate";
    public static String EndLat = "EndLat";
    public static String EndLong = "EndLong";
    public static String Etag = "Etag";
    public static String TextCategoryID = "TextCategoryID";
    public static String TextCategoryTypeID = "TextCategoryTypeID";
    public static String TextCategoryDesc = "TextCategoryDesc";
    public static String TextCategoryTypeDesc = "TextCategoryTypeDesc";
    public static String Text = "Text";
    public static String InvoiceHisMatNo = "MaterialNo";
    public static String InvoiceHisMatDesc = "MaterialDesc";
    public static String InvoiceHisAmount = "GrossAmount";
    public static String InvoiceHisQty = "Quantity";
    public static String CompName = "CompName";
    public static String CompGUID = "CompGUID";
    public static String CompInfoGUID = "CompInfoGUID";
    public static String Earnings = "Earnings";
    public static String SchemeAmount = "SchemeAmount";
    public static String SchemeName = "SchemeName";
    public static String SchemeGUID = "SchemeGUID";
    public static String ValidFromDate = "ValidFromDate";
    public static String ValidToDate = "ValidToDate";
    public static String MatGrp1Amount = "MatGrp1Amount";
    public static String MatGrp2Amount = "MatGrp2Amount";
    public static String MatGrp3Amount = "MatGrp3Amount";
    public static String MatGrp4Amount = "MatGrp4Amount";
    public static String UpdatedOn = "UpdatedOn";
    public static String PurchaseQty = "PurchaseQty";
    public static String PurchaseAmount = "PurchaseAmount";
    public static String DealerName = "DealerName";
    public static String DealerCode = "DealerCode";
    public static String DealerType = "DealerType";
    public static String MTDValue = "MTDValue";
    public static String OrderToRecivive = "OrderToRecivive";
    public static String DateofDispatch = "DateofDispatch";
    public static String TradeDate = "TradeDate";
    public static String PriceDate = "PriceDate";
    public static String BrandName = "BrandName";
    public static String HDPE = "HDPE";
    public static String PaperBag = "PaperBag";
    public static String PriceType = "PriceType";
    public static String diaryCheck = "diaryCheck";
    public static String chitPadCheck = "chitPadCheck";
    public static String bannerCheck = "bannerCheck";
    public static String AmountOne = "AmountOne";
    public static String DateOne = "DateOne";
    public static String AmountTwo = "AmountTwo";
    public static String DateTwo = "DateTwo";
    public static String AmountThree = "AmountThree";
    public static String DateThree = "DateThree";
    public static String AmountFour = "AmountFour";
    public static String DateFour = "DateFour";
    public static boolean collCreate = false;
    public static boolean CEFCreate = false;
    public static boolean returnOrdeCreate = false;
    public static boolean beatlist = false;
    public static HashMap<String, Integer> mapCount = new HashMap<String, Integer>();
    public static HashMap<String, String> MapSpinnerSelectedValue = new HashMap<String, String>();
    public static HashMap<String, String> MapRejectionReason = new HashMap<String, String>();
    public static HashMap<String, String> MapRoutePlanReason = new HashMap<String, String>();
    public static HashMap<String, String> MapApprovalReason = new HashMap<String, String>();
    public static HashMap<String, Integer> MapApprovalStatusIndexValue = new HashMap<String, Integer>();
    public static String ReturnDealer = "";
    public static int count = 0;
    public static String RETURNORDERMATERIAL = "MDStockSerList";
    public static String[] retailer_names = null;
    public static String[] retailer_codes = null;
    public static Map<String, List<String>> matSer = new HashMap<String, List<String>>();
    public static String selOutletCode = null;
    public static String selOutletName = null;
    public static String RegError = null;
    public static String uniqueId = "";
    public static String UserID = "";
    public static String APP_PKG_NME = "";
    public static String host = null;
    public static String port = null;
    public static String CLIENT = "";
    public static String customerCodeName = null;
    public static String customerCode = null;
    public static String outletCode = null;
    public static String outletCodeName = null;
    public static String Dealer = "DEALER";
    public static String Dealer_Synckey = "";
    public static String Dealer_Name = "";
    public static String Dealer_date = "";
    public static String Dealer_Id = "";
    public static int PLANNED_VISIT = 0;
    public static int ACT_VISIT = 6;
    public static double TODAY_ACH_TARG = 0;
    public static int MONTH_ACH = 0;
    public static double GrosPrice = 0.0;
    public static double Vat = 0.0;
    // sync time
    public static String SYNC_START_TIME = "";
    public static String SYNC_END_TIME = "";
    public static String CREDIT_LIMIT_SYNC_TIME = "";
    public static String STOCKOVERVIEW_SYNC_TIME = "";
    // public static boolean isTerminateSync = false;
    public static boolean flagUpt = false;
    public static boolean chkdata = true;
    public static boolean isReg = false;
    public static boolean isRegError = false;
    public static boolean isSavePassChk = false;
    public static boolean iSAutoSyncStarted1 = false;
    public static boolean iSInsideCreate = false;
    public static boolean iSAttendancesync = false;
    public static boolean iSStartsync = false;
    public static boolean iSClosesync = false;
    public static boolean iSDealersync = false;
    public static boolean iSOutletsync = false;
    public static boolean iSFjpsync = false;
    public static boolean iSSignboardsync = false;
    public static boolean iSVisitsync = false;
    public static boolean iSDealerVisitsync = false;
    public static boolean iSSOCreatesync = false;
    public static boolean isSOCreateTstSync = false;
    public static boolean isSOCreateTstSync1 = false;
    public static boolean iSoutletCreatesync = false;
    public static boolean iSstartCreatesync = false;
    public static boolean iSCloseCreatesync = false;
    public static boolean iSServiceCreated = false;
    public static boolean iSMetaDocCreated = false;
    public static boolean iSClose = false;
    public static boolean iSStart = false;
    public static boolean iSfirstStart = true;
    public static boolean iSfirstStarted = false;
    public static boolean iSfirstclosed = false;
    public static boolean isCollectionsync = false;
    public static boolean isBatchmatstocksync = false;
    public static boolean iSfjpvisit = false;
    public static boolean iSclosevisit = false;
    public static boolean iSclose = false;
    public static boolean isupdatespinnervisit = false;
    public static boolean isupdateclosevisit = false;
    public static boolean iSfirstclose = false;
    public static boolean issolist = false;
    public static boolean isoutletlist = false;
    public static boolean isactivitylist = false;
    public static double latitude, longitude;
    public static boolean isInvoiceVisit = false;
    public static SQLiteDatabase dbCnt;
    public static int beforPendingcount = 0;
    public static int afterPendingcount = 0;
    public static Hashtable<String, String> hashtable;
    public static Hashtable<String, String> headerValues;
    public static Hashtable<String, String> itemValues;
    public static Hashtable[] itemValues_Responce = null;
    ;
    public static Cursor cursor;
    public static String DATABASE_PATH = "";
    public static Context ctx;
    public static String UserNameSyc = "";
    public static int NoOfItems = 0;
    public static int lastQty = 0;
    public static double totalUnitPrice = 0.0;
    public static String matNo = null;
    public static Hashtable INVOICEITEM = null;
    public static boolean iSItemview = false;
    public static Vector checkedSerialNo = new Vector();
    public static Vector beatRetailor = new Vector();
    public static ArrayList<String> list1 = new ArrayList<String>();
    public static ArrayList<String> enterEditTextValList = new ArrayList<String>();
    public static Hashtable<String, String> mapEnteredTextsHashTable = new Hashtable<String, String>();
    public static Hashtable<String, String> mapEnteredPricesHashTable = new Hashtable<String, String>();
    public static Hashtable<String, String> mapEnteredMaterialDescHashTable = new Hashtable<String, String>();
    public static Hashtable<String, String> mapEnteredMaterialGroupHashTable = new Hashtable<String, String>();
    public static Hashtable<String, String> mapEnteredBrandHashTable = new Hashtable<String, String>();
    public static Hashtable<String, String> mapEnteredMatrialUOMHashTable = new Hashtable<String, String>();
    public static HashMap<String, String> mapCheckedStateHashMap = new HashMap<String, String>();
    public static HashMap<String, String> mapEnteredTextsHashMap = new HashMap<String, String>();
    public static HashMap<String, String> mapEnteredPricesHashMap = new HashMap<String, String>();
    public static HashMap<String, String> dealerStockEnteredQtyHashMap = new HashMap<String, String>();
    public static HashMap<String, String> dealerStockMatAndDescHashMap = new HashMap<String, String>();
    public static HashMap<String, String> dealerStockMatAndBrandHashMap = new HashMap<String, String>();
    public static HashMap<String, String> dealerStockEnteredPurchasedQtyHashMap = new HashMap<String, String>();
    public static HashMap<String, String> dealerStockVerfiedQtyHashMap = new HashMap<String, String>();
    public static HashMap<String, String> dealerStockUOMHashMap = new HashMap<String, String>();
    public static ArrayList<String> serialnumlist = new ArrayList<String>();
    public static HashMap<String, Double> InvoiceCreateVat = new HashMap<String, Double>();
    public static HashMap<String, Double> InvoiceCreateGross = new HashMap<String, Double>();
    public static HashMap<String, Double> MapVat = new HashMap<String, Double>();
    public static HashMap<String, Double> EXTRAVat = new HashMap<String, Double>();
    public static String DeviceTble = "Devicecollection";
    public static int congSel = 0;
    public static int congList = 0;
    public static Hashtable SALESORDER_COMMENTS = null;
    public static Hashtable SALESORDER_HEADER = null;
    public static Hashtable[] SALESORDER_ITEMS = null;
    public static Hashtable SALESORDER_RESPONCE = null;
    public static Hashtable[] SALESORDER__HEADER_RESPONCE = null;
    public static Hashtable[] SALESORDER__ITEM_RESPONCE = null;
    public static Hashtable[] SALESORDER_SCHEMES = null;
    public static Hashtable[] ITEMS = null;
    public static Hashtable[] BATCH = null;
    public static Hashtable[] BATCH_COMMENTS = null;
    public static Hashtable fjpVlaues = null;
    public static ArrayList<String> matList = new ArrayList<String>();
    public static ArrayList<ArrayList<String>> batchList = new ArrayList<ArrayList<String>>();
    public static ArrayList<String> focusMatBean = new ArrayList<String>();
    public static ArrayList<String> withoutSelMat = new ArrayList<String>();
    public static ArrayList<String> list = new ArrayList<String>();
    public static ArrayList<String> matCodeDecList = new ArrayList<String>();
    public static ArrayList<String> matDesclist = new ArrayList<String>();
    public static ArrayList<String> selectmatlist = new ArrayList<String>();
    public static ArrayList<String> selectbatchlist = new ArrayList<String>();
    // for temarary storage
    public static ArrayList SALESORDER_CHECK_TEMP = null;
    public static int INDEX_TEMP[] = null;
    public static int INDEX_TEMP1[] = null;
    public static int INDEX_TEMP_NEW[] = null;
    public static int INDEX_TEMP_IN[] = null;
    public static int FOCUS_MATERIAL[] = null;
    public static boolean closeFlag = false;
    public static boolean issaveclose = false;
    public static Boolean isChequeRequired = false;
    public static String[][] matDesc = null;
    public static String FROM_PER = "";
    public static String FROM_PER1 = "";
    public static String TO_PER = "";
    // for visit storage
    public static Hashtable VISIT_HEADER = null;
    public static Hashtable[] VISIT_ITEMS = null;
    public static Hashtable[][] VISIT2_ITEMS = null;
    public static Hashtable INVERTER_QUANTITY = null;
    public static Hashtable[] DISTRIBUTOR_ITEMS = null;
    public static Hashtable[] COMPENTITOR_ITEMS = null;
    public static Hashtable[] MATERIALBATCHITEMS = null;
    public static int lengthofdealer = 0;
    public static int lengthofproducts = 0;
    public static int lengthofdistributor = 0;
    // for Dealer visit storage
    public static Hashtable DEALERVISIT_HEADER = null;
    public static Hashtable[] DEALER_ITEMS = null;
    public static int lengthofdealeritems = 0;
    public static Hashtable DEALERVISIT_POP = null;
    public static Hashtable[] DEALERCOMPENTITOR_ITEMS = null;
    public static int lengthofdealercomp = 0;
    public static boolean isSalesTargetSync = false;
    public static boolean iscollTargetSync = false;
    public static boolean isdlrofftakeSync = false;
    public static boolean isdlrprefSync = false;
    public static boolean issoitemSync = false;
    // for star image
    public static boolean is_accounts = false;
    public static boolean is_product_price = false;
    public static boolean isstock = false;
    public static boolean is_sales_order = false;
    public static boolean is_invoice = false;
    public static boolean is_collections = false;
    public static boolean is_activity = false;
    public static boolean is_target = false;
    public static Hashtable<String, String> HashTableSerialNoAllocatedQty = new Hashtable<String, String>();
    public static String AuthOrgValue = "AuthOrgValue";
    public static String AuthOrgTypeID = "AuthOrgTypeID";
    public static String AuthOrgValDesc = "AuthOrgValueDesc";
    public static String AuthOrgTypeDesc = "AuthOrgTypeDesc";
    public static String StockOwner = "StockOwner";
    public static String SO_RESPONCE_ORDNO = "";
    public static String SO_RESQUEST_ORDNO = "";
    public static String OUTLET_RESPONCENO = "";
    public static String OUTLET_RESQUESTNO = "";
    public static Map<String, List<String>> focusmaterials = new HashMap<String, List<String>>();
    public static Hashtable[] SALESORDER_FOCUSMATERIALS = null;
    public static String selectedOutletCode = "";
    public static String selectedOutletDesc = "";
    public static boolean isMaterDataSyncEnable, isFocuPrdSyncEnable,
            isCollectionSyncEnable, isFJPSyncEnable, isActSyncEnable,
            isBatchBlockSyncEnable, isExcMaterialSyncEnable,
            isMatStockSyncEnable, isOutstandSyncEnable, isSOSyncEnable,
            isStartCloseSyncEnable, isAuthSyncEnable, isVisitSyncEnable, isMaterialSyncEnable,
            isSTOSyncEnable, isSalesOrderSyncEnable, isDeliverySyncEnable,
            isInvoiceSync, isStockSyncEnable,
            isCollSyncEnable, isVisitStartSyncEnable;
    public static Hashtable[] SERIALNUMS;
    public static String SubOrdinates = "SubOrdinates";
    public static String CustomerComplaintTxts = "CustomerComplaintTxts";
    public static String RoutePlanApprovals = "RoutePlanApprovals";
    public static String BUSINESSCALKEYNO = "";
    public static Date dateFrom;
    public static Date dateTo;
    public static boolean OrderCreated = false;
    public static boolean ReturnOrderCreated = false;
    public static boolean collectionUpdated = false;
    public static boolean snapshotTaken = false;
    public static boolean bussinessCallSavedSucessfully = false;
    public static boolean relationshipCallSavedSuccessfully = false;
    public static boolean ShadeCardSuccessfully = false;
    public static boolean CustomerComplaintsSavedSuccessfully = false;
    public static boolean DealerStockEnteredSuccessfully = false;
    public static boolean CompetitorStockSuccessfully = false;
    public static HashMap<String, Boolean> mapAllDone = new HashMap<String, Boolean>();
    public static String retailerIDSelected = "";
    public static String retailerNameSelected = "";
    public static String mobileNo = "";
    public static String address1 = "";
    public static String address2 = "";
    public static int selectednumber = 0;
    public static boolean newProduct = false;
    public static boolean FocusProduct = false;
    public static boolean MustSell = false;
    public static String RRETAILERMOBILENO = "";
    public static String RRETAILERFITSTADDRESS = "";
    public static String RRETAILERSECONDADDRESS = "";
    public static String MaterialGrpAndCode = "MaterialGrpAndCode";
    public static String BalanceConfirmationHeader = "BalanceConfirmationHeader";
    public static String BalanceConfirmationItems = "BalanceConfirmationItems";
    public static String BalanceConfirmations = "BalanceConfirmations";
    public static String BalConfirmItemDetaills = "BalConfirmItemDetails";
    public static boolean isCreateFlag = false;
    public static boolean isSOCountDone = false;
    public static boolean isAppEndPointDone = false;
    public static boolean isMetaDataDone = false;
    public static boolean isSOItmCountDone = false;
    public static boolean isMaterialsCountDone = false;
    public static boolean isPriceListCountDone = false;
    public static String OutstandingSummary = "OutstandingSummary";
    public static String CustomerwiseOSs = "CustomerwiseOSs";
    public static String Promotion = "Promotion";
    public static String BrandPerformanc = "BrandPerformance";
    public static String ErrorMsg = "";
    public static String newMPNo = "";
    public static String MerchndisingKeyNo = "";
    public static String VisitKeyNo = "";
    public static String VisitTypeNo = "";
    public static String VisitStartKeyNo = "";
    public static String VisitStartKeyNoCurrentDealerNo = "";
    public static String Collections = "Collections";
    public static String CollectionItemDetails = "CollectionItemDetails";
    public static String History = "History";
    public static String PendingSync = "Pending Sync";
    public static String Merchindising = "Merchandising";
    public static String DeviceMerchindising = "Device Merchandising";
    public static String SyncGroup = "SyncGroup";
    public static String MasterPainter = "MasterPainter";
    public static String reqExpensesNo;
    public static String resExpensesNo;
    public static String resLeadProjectNo;
    public static String reqLeadProjectNo;
    public static String MerchandisingReview = "MerchandisingReview";
    public static String reqMerchandisingNo;
    public static String resMerchandisingNo;
    public static String resBusinessCallNo;
    public static String reqBusinessCallNo;
    public static String reqRelationShipCallNo;
    public static String resRelationShipCallNo;
    public static String reqCustomerComplaintNo;
    public static String resCustomerComplaintNo;
    public static String collectionresDocNo = "";
    public static String collectionreqDocNo = "";
    public static String reqMasterPainterNo = "";
    public static String resMasterPainterNo = "";
    public static String reqAttendanceID = "";
    public static String resAttendanceID = "";
    public static boolean SoCreateSeaniro = false;
    public static double latitudeValue;
    public static double longitudeValue;
    public static boolean isCollListsAuthEnabled;
    public static String CustomerComplaintNo = "";
    public static boolean isMerchSyncEnable;
    public static String PreviousMaterialGrp = "";
    public static String ExpenseEntrys = "ExpenseEntrys";
    public static boolean isCustPerFormances, isAttencesSync;
    public static boolean isShadeCardEnabled;
    public static boolean isTargetsEnabled;
    public static String reqShadeCardFeedBackNo;
    public static String resShadeCardFeedBackNo;
    public static boolean isDishonourSyncEnable;
    public static String reqLeadChangeProjectNo;
    public static String resLeadChangeProjectNo;
    public static String resMatSO;
    public static String reqMatSO;
    public static String OfficerEmployeeCode = "OfficerEmployeeCode";
    public static String CounterName = "CounterName";
    public static String LongitudeAndLatitude = "LongitudeAndLatitude";
    public static String CounterType = "CounterType";
    public static String ContactPerson = "ContactPerson";
    public static String PCMobileNo = "PCMobileNo";
    public static String ProspectecCustomerAddress = "ProspectecCustomerAddress";
    public static String PCDistrict = "PCDistrict";
    public static String Taluka = "Taluka";
    public static String PinCode = "PinCode";
    public static String PCcity = "City";
    public static String Block = "Block";
    public static String TotalTradePottential = "TotalTradePottential";
    public static String TotalNonTradePottential = "TotalNonTradePottential";
    public static String PottentialAvailable = "PottentialAvailable";
    public static String UTCL = "UTCL";
    public static String OCL = "OCL";
    public static String LAF = "LAF";
    public static String ACC = "ACC";
    public static String POPDistributed = "POPDistributed";
    public static String PCRemarks = "PCRemarks";
    public static String OACustomerNo = "CustomerNo";
    public static String OACustomerName = "CustomerName";
    public static String OACityName = "CityName";
    public static String OATelephone1 = "Telephone1";
    public static String OADistChannel = "DistChannel";
    public static String OASecurityDeposit = "SecurityDeposit";
    public static String OACreditLimit = "CreditLimit";
    public static String OATotalDebitBal = "TotalDebitBal";
    public static String OA0_7Days = "SevenDays";
    public static String OA7_15Days = "FifteenDays";
    public static String OA15_30Days = "ThirtyDays";
    public static String OA30_45Days = "FortyfiveDays";
    public static String OA45_60Days = "SixtyDays";
    public static String OA60_90Days = "NintyDays";
    public static String OA90_120Days = "OneTwentyDays";
    public static String OA120_180Days = "OneEightyDays";
    public static String OA180Days = "OneEightyPlusDays";
    public static String OAPastDays = "PastDays";
    public static String OACurrentDays = "CurrentDays";
    public static String OA3160Days = "ThirtyoneDays";
    public static String OA6190Days = "SixtyoneDays";
    public static String OA91120Days = "NintyoneDays";
    public static String OA120Days = "OneTwentyPlusDays";
    public static String TADealerNo = "DealerNo";
    public static String TADealerName = "DealerName";
    public static String TADealerCity = "DealerCity";
    public static String TACurMonthTraget = "CurrentMonthTraget";
    public static String TAProrataTraget = "ProrataTraget";
    public static String TASaleACVD = "SaleACVD";
    public static String TAProrataAchivement = "ProrataAchivement";
    public static String TABalanceQty = "BalanceQty";
    public static String TADailyTarget = "DailyTarget";
    public static String TADepotNo = "DepotNo";
    public static String TADepotName = "DepotName";
    public static String Mobile1 = "Mobile1";
    public static ArrayList<HashMap> soItem = new ArrayList<HashMap>();
    public static HashMap selBrand = new HashMap();
    public static ArrayList<HashMap> soDaySummary = new ArrayList<HashMap>();
    public static ArrayList<HashMap> collDaySummary = new ArrayList<HashMap>();
    public static ArrayList<HashMap> invDaySummary = new ArrayList<HashMap>();
    public static String VisitSurveyNo;
    public static String FocusedCustomers = "FocusedCustomers";
    public static String LeadNo = "";
    public static String NewPwd = "";
    public static String CreateOperation = "Create";
    public static String ReadOperation = "Read";
    public static String UpdateOperation = "Update";
    public static String DeleteOperation = "Delete";
    public static String QueryOperation = "Query";
    //new 28112016 Ramu
    public static String Route_Plan_No = "";
    public static String Route_Plan_Desc = "";
    public static String Route_Plan_Key = "";
    public static String Visit_Type = "";
    public static String CustomerType = "";
    public static String VISIT_TYPE = "VISIT_TYPE";
    public static String PlannedRoute = "PlannedRoute";
    public static String PlannedRouteName = "PlannedRouteName";
    public static String PlanedCustomerName = "CustomerName";
    public static int MAX_LENGTH = 100;
    public static String WeeklyOffDesc = "WeeklyOffDesc";
    public static String Error_Msg = "";
    /*error code*/
    public static int ErrorCode = 0;
    public static int ErrorNo = 0;
    public static int ErrorNo_Get_Token = 0;
    public static String ErrorName = "";
    public static String NetworkError_Name = "NetworkError";
    public static String Comm_error_name = "Communication error";
    public static String Network_Name = "Network";
    public static String Unothorized_Error_Name = "401";
    public static String Max_restart_reached = "Maximum restarts reached";
    public static int Network_Error_Code = 101;
    public static int Comm_Error_Code = 110;
    public static int UnAuthorized_Error_Code = 401;
    public static int UnAuthorized_Error_Code_Offline = -10207;
    public static int Network_Error_Code_Offline = -10205;
    public static int Unable_to_reach_server_offline = -10208;
    public static int Resource_not_found = -10210;
    public static int Unable_to_reach_server_failed_offline = -10204;
    public static String Executing_SQL_Commnd_Error = "10001";
    public static int Execu_SQL_Error_Code = -10001;
    public static int Store_Def_Not_matched_Code = -10247;
    public static String Store_Defining_Req_Not_Matched = "10247";
    public static String Invalid_Store_Option_Value = "InvalidStoreOptionValue";
    public static int Build_Database_Failed_Error_Code1 = -100036;
    public static int Build_Database_Failed_Error_Code2 = -100097;
    public static int Build_Database_Failed_Error_Code3 = -10214;
    public static String RFC_ERROR_CODE_100027 = "100027";
    public static String RFC_ERROR_CODE_100029 = "100029";
    public static String ZZForwarAgentCode = "ZZFrwadgAgent";
    public static ArrayList<MaterialsBean> selMaterialList = null;
    public static String OtherRouteGUIDVal = "";
    public static String OtherRouteNameVal = "";
    public static int[] IconVisibiltyReportFragment = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public static int[] IconPositionReportFragment = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public static String isSOCreateKey = "isSOCreate";
    public static String isSOCreateTcode = "/ARTEC/SS_SOCRET";
    public static final String isGRReportKey = "isGRReportEnabled";
    public static final String isGRReportTcode = "/ARTEC/SF_GR_REPORT";
    public static String isCollCreateEnabledKey = "isCollCreateEnabled";
    public static String isCollCreateTcode = "/ARTEC/SF_COLLCRT";
    public static String isMerchReviewKey = "isMerCreateEnabled";
    public static String isMerchReviewTcode = "/ARTEC/SS_MERRVW";
    public static String isMerchReviewListKey = "isMerCreateListEnabled";
    public static String isMerchReviewListTcode = "/ARTEC/SS_MERRVWLST";
    public static String isMustSellKey = "isMustSellEnabled";
    public static String isMustSellTcode = "/ARTEC/MC_MSTSELL";
    public static String isFocusedProductKey = "isFocusedProductEnabled";
    public static String isFocusedProductTcode = "/ARTEC/SS_FOCPROD";
    public static String isNewProductKey = "isNewProductEnabled";
    public static String isNewProductTcode = "/ARTEC/SS_NEWPROD";
    public static String isDBStockKey = "isDBStockEnabled";
    public static String isDBStockTcode = "/ARTEC/SS_DBSTK";
    public static String isCompInfoEnabled = "isCompInfoEnabled";
    public static String isCompInfoTcode = "/ARTEC/SS_COMPINFO";
    public static String isSOApprovalKey = "isSOApprovalEnabled";
    public static String isSOApprovalTcode = "/ARTEC/SF_SOAPRL";
    public static String isPlantStockKey = "isPlantStockEnabled";
    public static String isPlantStockTcode = "/ARTEC/SF_PLNTSTK";
    public static String isMatPriceKey = "isMatPriceKey";
    public static String isMatpriceTcode = "/ARTEC/SF_MATPRICE";
    public static String isMTPApprovalKey = "isMTPApprovalKey";
    public static String isMTPApprovalTcode = "/ARTEC/SF_MTP_APRL";
    public static boolean BoolTodayBeatLoaded = false;
    public static boolean BoolOtherBeatLoaded = false;
    public static String ClosingeDay = "ClosingeDay";
    public static String Today = "Today";
    public static String PreviousDay = "PreviousDay";
    public static String ClosingeDayType = "ClosingeDayType";
    public static String SSSOGuid = "SSSOGuid";
    public static String OrderNo = "OrderNo";
    public static String OrderType = "OrderType";
    public static String OrderTypeDesc = "OrderTypeDesc";
    public static String OrderDate = "OrderDate";
    public static String EntryTime = "EntryTime";
    public static String DmsDivision = "DmsDivision";
    public static String DmsDivisionDesc = "DmsDivisionDesc";
    public static String PONo = "PONo";
    public static String PODate = "PODate";
    public static String FromCPGUID = "FromCPGUID";
    public static String FromCPNo = "FromCPNo";
    public static String FromCPName = "FromCPName";
    public static String FromCPTypId = "FromCPTypId";
    public static String FromCPTypDs = "FromCPTypDs";
    public static String SoldToUID = "SoldToUID";
    public static String SoldToDesc = "SoldToDesc";
    public static String SoldToType = "SoldToType";
    public static String SoldToTypDs = "SoldToTypDs";
    public static String ShipToIdCPGUID = "ShipToIdCPGUID";
    public static String ShipToUID = "ShipToUID";
    public static String ShipToDesc = "ShipToDesc";
    public static String ShipToType = "ShipToType";
    public static String ShipToTypDs = "ShipToTypDs";
    public static String GrossAmt = "GrossAmt";
    public static String Freight = "Freight";
    public static String TAX = "TAX";
    public static String Tax = "Tax";
    public static String Discount = "Discount";
    public static String TaxAmount = "TaxAmount";
    public static String CPType = "CPType";
    public static String SoldToId = "SoldToId";
    public static String ShipToParty = "ShipToParty";
    public static String ShipToPartyName = "ShipToPartyName";
    public static String SSSOItemGUID = "SSSOItemGUID";
    public static String OrderMatGrp = "OrderMatGrp";
    public static String OrderMatGrpDesc = "OrderMatGrpDesc";
    public static String Quantity = "Quantity";
    public static String AlternateWeight = "AlternateWeight";
    public static String Uom = "Uom";
    public static String HigherLevelItemno = "HigherLevelItemno";
    public static String IsfreeGoodsItem = "IsfreeGoodsItem";
    public static String RefdocItmGUID = "RefdocItmGUID ";
    public static String Batch = "Batch";
    public static String MRP = "MRP";
    public static String LandingPrice = "LandingPrice";
    public static String SecDiscount = "SecDiscount ";
    public static String PriDiscount = "PriDiscount";
    public static String CashDiscount = "CashDiscount";
    public static String LoginId = "LoginId";
    public static String CustomerCompCreateID = "10";
    public static String ComplaintType = "ComplaintType";
    public static String ComplaintNo = "ComplaintNo";
    public static String ComplaintGUID = "ComplaintGUID";
    public static String ComplaintPriorityID = "ComplaintPriorityID";
    public static String ComplaintPriorityDesc = "ComplaintPriorityDesc";
    public static String MaterialGrp = "MaterialGrp";
    public static String ComplaintDate = "ComplaintDate";
    public static String ComplaintStatusID = "ComplaintStatusID";
    public static String ComplaintStatusDesc = "ComplaintStatusDesc";
    public static String MFD = "MFD";
    public static String SchFreeMatGrpGUID = "SchFreeMatGrpGUID";
    public static String ComplaintCategoryID = "ComplaintCategoryID";
    public static String ComplainCategoryDesc = "ComplainCategoryDesc";
    public static String ComplaintTypeDesc = "ComplaintTypeDesc";
    public static String ComplaintTypeID = "ComplaintTypeID";
    public static String strErrorWithColon = "Error : ";
    public static String SystemKPI = "SystemKPI";
    public static String SO_Cust_QRY = "";
    public static ArrayList<String> alRetailersGuid = new ArrayList<>();
    public static ArrayList<String> alCustomers = new ArrayList<>();
    public static ArrayList<MTPRoutePlanBean> alTodayBeatCustomers = new ArrayList<>();
    public static int TAB_POS_1 = 1;
    public static int TAB_POS_2 = 2;
    public static String EXTRA_SSRO_GUID = "extraSSROguid";
    public static String EXTRA_TAB_POS = "extraTabPos";
    public static String EXTRA_ORDER_DATE = "extraDate";
    public static String EXTRA_ORDER_IDS = "extraIDS";
    public static String EXTRA_ORDER_AMOUNT = "extraAmount";
    public static String EXTRA_ORDER_SATUS = "extraStatus";
    public static String EXTRA_ORDER_CURRENCY = "extraCurrency";
    public static HashSet<String> mSetTodayRouteSch = new HashSet<>();
    public static String RoutSchScope = "RoutSchScope";
    public static String InvoiceItems = "InvoiceItems";
    public static String PaymentMethod = "";
    public static String IssuingBank = "";
    public static int selectedIndex = 0;
    public static String ShippingTypeDesc = "ShippingTypeDesc";
    public static String TransporterID = "TransporterID";
    public static String TransporterName = "TransporterName";
    public static String PartnerVendorName = "PartnerVendorName";
    public static String PartnerVendorNo = "PartnerVendorNo";
    public static String Region = "Region";
    public static String StocksList = "StocksList";
    public static String CompetitorMasterInfo = "CompetitorMasterInfo";
    public static String EXTRA_CUSTOMER_REGION = "extraCustomerRegion";
    public static String ConditionAmtPer = "ConditionAmtPer";
    public static String ConditionAmtPerUOM = "ConditionAmtPerUOM";
    public static String ConditionTypeDesc = "ConditionTypeDesc";
    public static String ConditionBaseValue = "ConditionBaseValue";
    public static String ConditionValue = "ConditionValue";
    public static String ConditionCounter = "ConditionCounter";
    public static String TextID = "TextID";
    public static String DelSchLineNo = "DelSchLineNo";
    public static String ConfirmedQty = "ConfirmedQty";
    public static String RequiredQty = "RequiredQty";
    public static String SO_ORDER_VALUE = "";
    public static String SchemeID = "SchemeID";
    public static String ValidFrom = "ValidFrom";
    public static String ValidTo = "ValidTo";
    public static String FilterList = "FilterList";
    public static String Status_ID = "Status_ID";
    public static String DocumentID = "DocumentID";
    public static String DocumentSt = "DocumentStore";
    public static String Application = "Application";
    public static String Application1 = "Application";
    public static String Active = "Active";
    public static String Total = "Total";
    public static String Open = "Open";
    public static String OrderValue = "OrderValue";
    public static String DocumentLink = "DocumentLink";
    public static String DocumentName = "FileName";
    public static String FolderName = "VisualVid";
    public static String RoleID = "RoleID";
    public static String LoginName = "LoginName";
    public static String RoleDesc = "RoleDesc";
    public static String RoleCatID = "RoleCatID";
    public static String RoleCatDesc = "RoleCatDesc";
    public static String IsActive = "IsActive";
    public static String ERPLoginID = "ERPLoginID";
    public static String UserFunction1 = "UserFunction1";
    public static String UserFunction1Desc = "UserFunction1Desc";
    public static String UserFunction2 = "UserFunction2";
    public static String UserFunction2Desc = "UserFunction2Desc";
    public static String PotentialType = "PotentialType";
    public static String ChannelPartner = "ChannelPartner";
    public static String CampaignExpense = "CampaignExpense";
    public static String MobileNoSales = "MobileNo";
    public static String EXTRA_SCHEME_IS_SECONDTIME = "isSecondTime";
    public static String SC = "SC";
    public static String SS = "SS";
    public static String[][] customerArrData = null;
    public static ArrayList<CreditLimitBean> creditLimitBeenList = new ArrayList<>();
    public static String isMyTargetsEnabled = "isMyTargetsEnabled";
    public static String isMyTargetsTcode = "/ARTEC/SF_MYTRGTS";
    public static String isMatarialwiseEnabled = "isMatarialwiseEnabled";
    public static String isMatarialwiseTcode = "/ARTEC/SF_TRGT_MAT";
    public static String isStartCloseEnabled = "isStartCloseEnabled";
    public static String isStartCloseTcode = "/ARTEC/SF_ATTND";
    public static String isOutstandingEnabled = "isOutstandingHistory";
    public static String isOutStandingTcode = "/ARTEC/SF_OUTSTND";
    public static String isRouteEnabled = "isRouteEnabled";
    public static String isRoutePlaneTcode = "/ARTEC/SF_ROUTPLAN";
    public static String isInvHistoryEnabled = "isInvHistory";
    public static String isInvoiceHistoryTcode = "/ARTEC/SF_INVHIS";
    public static String isSOListEnabled = "isSOListEnabled";
    public static String isSOListTcode = "/ARTEC/SF_SOLIST";
    public static String isDaySummaryEnabled = "isDaySummaryEnabled";
    public static String isAdhocVisitEnabled = "isAdhocVisitEnabled";
    public static String isAlertsEnabled = "isAlertsEnabled";
    public static String isExpenseEntryEnabled = "isExpenseEntryEnabled";
    public static String isExpenseListEnabled = "isExpenseListEnabled";
    public static String isVisitSummaryEnabled = "isVisitSummaryEnabled";
    public static String isDigitalProductEnabled = "isDigitalProductEnabled";
    public static String isDaySummaryTcode = "/ARTEC/SF_DAYSMRY";
    public static String isAdhocVistTcode = "/ARTEC/SF_ADHOCVST";
    public static String isAlertTcode = "/ARTEC/SF_ALRT";
    public static String isExpEnteryTcode = "/ARTEC/SF_EXPCRT";
    public static String isExpListTcode = "/ARTEC/SF_EXPLIST";
    public static String isVisualAidsTcode = "/ARTEC/SF_VSULAID";
    public static String isVisitSummaryTcode = "/ARTEC/SF_VISTSMRY";
    public static String isCustomerListTcode = "/ARTEC/SF_CUST_LST";
    public static String isCustomerListEnabled = "isCustomerListEnabled";
    public static String isRTGSTcode = "/ARTEC/SF_COLLPLN_CRT";
    public static String isRTGSEnabled = "isRTGSEnabled";
    public static String isProspectiveCustomerListEnabled = "isProspectiveCustomerListEnabled";
    public static String isProspectiveCustomerListTcode = "/ARTEC/SF_PROSCUST_LST";
    public static String isSchemeKey = "isSchemeEnabled";
    public static String isSchemeTcode = "/ARTEC/SF_SCHEMES";
    public static String isDSREntryEnabled = "isDsrEntryEnabled";
    public static String isDSREntryTcode = "/ARTEC/SF_DSR_CRT";
    public static String isROListKey = "isROListEnabled";
    public static String isROLisTcode = "/ARTEC/SF_ROLIST";
    public static String isROListItemKey = "isROItemListEnabled";
    public static String isROLisItemTcode = "/ARTEC/SF_ROLIST01";
    public static String isRetailerEnabled = "isRetailerEnabled";
    public static String isRetailerTcode = "/ARTEC/SF_CP_LST";
    public static String isMTPEnabled = "isMTPEnabled";
    public static String isMTPTcode = "/ARTEC/SF_MTP_CRT";
    public static String isDealerBehaviourEnabled = "isDealerBehaviourEnabled";
    public static String isDealerBehaviourTcode = "/ARTEC/SF_SPCP_EVAL";
    public static String isMTPSubOrdinateEnabled = "isMTPSubOrdinateEnabled";
    public static String isMTPSubOrdinateTcode = "/ARTEC/SF_MTP_SUBLST";
    public static String isRTGSSubOrdinateEnabled = "isRTGSSubOrdinateEnabled";
    public static String isRTGSSubOrdinateTcode = "/ARTEC/SF_COLLPLN_SUBLST";
    public static String isAttndSumryEnabled = "isAttndSumryEnabled";
    public static String isClaimSumryEnabled = "isClaimSumryEnabled";
    public static String isAttndSumryTcode = "/ARTEC/SF_ATND_SMRY";
    public static String isClaimSumryTcode = "/ARTEC/SF_PND_CLM_SMRY";
    public static String EXTRA_ARRAY_LIST = "arrayList";
    public static String Y = "Y";
    public static String N = "N";
    public static String WindowDisplayID = "11";
    public static String WindowDisplayClaimID = "13";
    public static String WindowDisplayValueHelp = "WindowDisplay";
    public static String CameraPackage = "android.media.action.IMAGE_CAPTURE";
    public static String MAXEXPALWD = "MAXEXPALWD";
    public static String MAXEXPALWM = "MAXEXPALWM";
    public static String SF = "SF";
    public static String AUTOSYNC = "AUTOSYNC";
    public static String GEOAUTOSYN = "GEOAUTOSYN ";
    public static String Expenses = "Expenses";
    public static String ExpenseItemDetails = "ExpenseItemDetails";
    public static String ExpenseDocuments = "ExpenseDocuments";
    public static String ExpenseEntity = ".Expense";
    public static String ExpenseItemEntity = ".ExpenseItemDetail";
    public static String ExpenseItemDocumentEntity = ".ExpenseItemDetail_ExpenseDocuments";
    public static String PlantStock = "PlantStocks";
    public static Bundle SOBundleExtras = null;
    public static boolean isDayStartSyncEnbled = false;
    public static int mErrorCount = 0;
    public static ArrayList<String> AL_ERROR_MSG = new ArrayList<>();
    public static Set<String> Entity_Set = new HashSet<>();
    public static PricingDatabaseHelper dbHelper;
    public static SQLiteDatabase database;
    public static String DecisionKey = "DecisionKey";
    public static String Comments = "Comments";
    public static boolean mBoolIsReqResAval = false;
    public static boolean mBoolIsNetWorkNotAval = false;
    public static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    public static List<ODataEntity> oDataEntity = null;
    public static List<ODataEntity> oDataEntityRegion = null;
    public static List<ODataEntity> oDataEntityZone = null;
    public static List<ODataEntity> oDataEntitySD = null;
    public static List<ODataEntity> oDataEntityDist = null;
    public static String Geo1 = "Geo1";
    public static String Geo2 = "Geo2";
    public static ODataEntity oDataEntityRetailer = null;
    public static String IncoTerm1 = "IncoTerm1";
    public static String IncoTerm2 = "IncoTerm2";
    public static String IncoTerm1Desc = "IncoTerm1Desc";
    public static String PaymentTermDes = "PaymentTermDes";
    public static String isInvoiceItemsEnabled = "isInvoiceItemsEnabled";
    public static String InvoiceConditions = "InvoiceConditions";
    public static String ConditionAmt = "ConditionAmt";
    public static String isMaterialEnabled = "isMaterialEnabled";
    public static String SOITST = "SOITST";
    public static String RSFRJN = "RSFRJN";
    public static String SOConditions = "SOConditions";
    public static String ConditionPricingDate = "ConditionPricingDate";
    public static String CondCurrency = "CondCurrency";
    public static String ConditionTypeID = "ConditionTypeID";
    public static String ConditionAmount = "ConditionAmount";
    public static String NetWeight = "NetWeight";
    public static String NetWeightUom = "NetWeightUom";
    public static String TotalQuantity = "TotalQuantity";
    public static String QuantityUom = "QuantityUom";
    public static String NetWeightUOM = "NetWeightUOM";
    public static String RejectStatus = "02";
    public static String ApprovalStatus01 = "01";
    public static String RouteSchPlanGUID = "RouteSchPlanGUID";
    public static String SalesDistrict = "SalesDistrict";
    public static String WeekOfMonth = "WeekOfMonth";
    public static String ActivityID = "ActivityID";
    public static String ActivityDesc = "ActivityDesc";
    public static String RouteSchSPGUID = "RouteSchSPGUID";
    public static String IS_UPDATE = "isUpdate";
    public static String ApprovalStatusDs = "ApprovalStatusDs";
    public static String ApprovalStatus = "ApprovalStatus";
    public static String TLSD = "TLSD";
    public static String Conv_Mode_Type_Other = "0000000001";
    public static String CollectionPlanItem = "CollectionPlanItems";
    public static String COllectionPlanDate = "CollectionPlanDate";
    public static String CollectionPlanGUID = "CollectionPlanGUID";
    public static String CollectionPlanItemGUID = "CollectionPlanItemGUID";
    public static String CollectionType = "CollectionType";
    public static String PlannedValue = "PlannedValue";
    public static String AchievedValue = "AchievedValue";
    public static String SalesDistrictID = "SalesDistrictID";
    public static String ReturnOrders = "ReturnOrders";
    public static String ReturnOrderItemDetails = "ReturnOrderItemDetails";
    public static String ReturnOrderItems = "ReturnOrderItems";
    public static String CollectionPlanDate = "CollectionPlanDate";
    public static String CollectionPlanEntity = ".CollectionPlan";
    public static String CollectionPlanItemDetailEntity = ".CollectionPlanItemDetail";
    public static String KeyNo = "KeyNo";
    public static String KeyValue = "KeyValue";
    public static String KeyType = "KeyType";
    public static String DataVaultData = "DataVaultData";
    public static String DataVaultFileName = "mSFADataVault.txt";
    public static HashMap<String, ArrayList<String>> mapMatGrpBasedOnUOM = new HashMap<>();
    public static HashMap<String, MyTargetsBean> mapMatGrpBasedOnUOMTemp = new HashMap<>();
    public static HashMap<String, String> mapMatGrpByMaterial = new HashMap<>();
    public static boolean isComingFromDashBoard = false;
    public static ReentrantLock reentrantLock = null;
    public static String colName = "";
    public static String StrSPGUID32 = "";
    public static String parternTypeID = "";
    public static AlertDialog.Builder builder = null;
    public static boolean writeDebug = false;
    static ArrayList<Hashtable<String, String>> itemtable = null;
    private static volatile String SALES_PERSON_GUID = "";
    private static HashMap<String, String> mapTable;
    private static int HOUR_PM = 0;
    private static int ZERO_MINUTES = 0;
    public static boolean ReIntilizeStore = false;
    public static String[] getDefinigReq(Context context) {//TODO Sp need to add and need to increase the version
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        String loginId = sharedPreferences.getString("username", "");
        String rollType = sharedPreferences.getString(USERROLE, "");
        if (sharedPreferences.getInt(CURRENT_VERSION_CODE, 0) == 20) {
            if (rollType.equalsIgnoreCase("Z5")) {
                String[] DEFINGREQARRAY = {"Attendances",// need to check for ASM and above role by removing
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        "Customers", KPISet, Targets, TargetItems, KPIItems,
                        "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                        CustomerPartnerFunctions,
                        "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                        "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, "MaterialSaleAreas",
                        "Alerts?$filter=Application eq 'PD'",
                        "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions, "InvoiceItems", "InvoiceConditions",
                        "VisitActivities", "Visits",
                        "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                        "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                        ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS'  or Typeset eq 'SP' ",
                        "MaterialByCustomers",
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

                };
                return DEFINGREQARRAY;

            } else {
                String[] DEFINGREQARRAY = {"Attendances",
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        "Customers", KPISet, Targets, TargetItems, KPIItems,
                        "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                        CustomerPartnerFunctions,
                        "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                        "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, "MaterialSaleAreas",
                        "Alerts?$filter=Application eq 'PD'",
                        "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions,
                        "VisitActivities", "Visits", "InvoiceItems", "InvoiceConditions",
                        "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                        "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                        ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' ",
                        "MaterialByCustomers",
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

                };
                return DEFINGREQARRAY;
            }
        } else if (sharedPreferences.getInt(CURRENT_VERSION_CODE, 0) == 21) {
            if (rollType.equalsIgnoreCase("Z5")) {
                String[] DEFINGREQARRAY = {"Attendances",// need to check for ASM and above role by removing
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        "Customers", KPISet, Targets, TargetItems, KPIItems,
                        "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                        CustomerPartnerFunctions,
                        "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                        "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, "MaterialSaleAreas",
                        "Alerts?$filter=Application eq 'PD'",
                        "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions, "InvoiceItems", "InvoiceConditions",
                        "VisitActivities", "Visits",
                        "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                        "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                        ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' ",
                        "MaterialByCustomers",
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

                };
                return DEFINGREQARRAY;

            } else {
                String[] DEFINGREQARRAY = {"Attendances",
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                       // "SPGeos",/*SyncHistorys,UserPartners,*/
                        "Customers", KPISet, Targets, TargetItems, KPIItems,
                        "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                        CustomerPartnerFunctions,
                        "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                        "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, "MaterialSaleAreas",
                        "Alerts?$filter=Application eq 'PD'",
                        "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions,
                        "VisitActivities", "Visits", "InvoiceItems", "InvoiceConditions",
                        "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                        "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                        ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' ",
                        "MaterialByCustomers",
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

                };
                return DEFINGREQARRAY;
            }
        } else if (sharedPreferences.getInt(CURRENT_VERSION_CODE, 0) == 23) {
            if (rollType.equalsIgnoreCase("Z5")) {
                String[] DEFINGREQARRAY = {"Attendances",// need to check for ASM and above role by removing
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        "Customers", KPISet, Targets, TargetItems, KPIItems,
                        "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                        CustomerPartnerFunctions,
                        "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                        "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, "MaterialSaleAreas",
                        "Alerts?$filter=Application eq 'PD'",
                        "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions, "InvoiceItems", "InvoiceConditions",
                        "VisitActivities", "Visits",
                        "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                        "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                        ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' ",
                        "MaterialByCustomers",
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

                };
                return DEFINGREQARRAY;

            } else {
                String[] DEFINGREQARRAY = {"Attendances",
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        SyncHistorys, UserPartners,
                        "Customers", KPISet, Targets, TargetItems, KPIItems,
                        "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                        CustomerPartnerFunctions,
                        "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                        "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, "MaterialSaleAreas",
                        "Alerts?$filter=Application eq 'PD'",
                        "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions,
                        "VisitActivities", "Visits", "InvoiceItems", "InvoiceConditions",
                        "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                        "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                        ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' ",
                        "MaterialByCustomers",
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

                };
                return DEFINGREQARRAY;
            }
        } else if (sharedPreferences.getInt(CURRENT_VERSION_CODE, 0) == 24) {
            if (rollType.equalsIgnoreCase("Z5")) {
                String[] DEFINGREQARRAY = {"Attendances",// need to check for ASM and above role by removing
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        "Customers", KPISet, Targets, TargetItems, KPIItems,
                        "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                        CustomerPartnerFunctions,
                        "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                        "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, "MaterialSaleAreas",
                        "Alerts?$filter=Application eq 'PD'",
                        "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions, "InvoiceItems", "InvoiceConditions",
                        "VisitActivities", "Visits",
                        "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                        "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                        ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' ",
                        "MaterialByCustomers",
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

                };
                return DEFINGREQARRAY;

            } else {
                String[] DEFINGREQARRAY = {"Attendances",
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        //SPGeos,
                        SyncHistorys, UserPartners,
                        "Customers", KPISet, Targets, TargetItems, KPIItems,
                        "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                        CustomerPartnerFunctions,
                        "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                        "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, "MaterialSaleAreas",
                        "Alerts?$filter=Application eq 'PD'",
                        "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions,
                        "VisitActivities", "Visits", "InvoiceItems", "InvoiceConditions",
                        "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                        "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                        ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' ",
                        "MaterialByCustomers",
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

                };
                return DEFINGREQARRAY;
            }
        } else if (sharedPreferences.getInt(CURRENT_VERSION_CODE, 0) == 26) {
            if (rollType.equalsIgnoreCase("Z5")) {
                String[] DEFINGREQARRAY = {/*"Attendances",*/// need to check for ASM and above role by removing
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        "Customers", KPISet, Targets, TargetItems, KPIItems, SyncHistorys, UserPartners,
                        "SalesPersons", OrderMaterialGroups, Brands,/* PlantStocks,*/ UserSalesPersons,
                        /*CustomerPartnerFunctions,*/
                        /*"MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",*/
                        /*"CustomerCreditLimits",*/ UserCustomers, CustomerSalesAreas, "MaterialSaleAreas",
                        "Alerts?$filter=Application eq 'PD'",
                        /*"Invoices", "InvoiceItemDetails", InvoicePartnerFunctions, "InvoiceItems", "InvoiceConditions",
                        "VisitActivities", "Visits",*/
                        "RoutePlans", /*"RouteSchedulePlans",*/ "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        /*"SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",*/
                        /*"MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",*/
                        /*ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,*/
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' ",
                        /*"MaterialByCustomers",*/
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "//or PropName eq 'Route'

                };
                return DEFINGREQARRAY;

            } else {
                String[] DEFINGREQARRAY = {"Attendances",
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        SyncHistorys, UserPartners,
                        "Customers", KPISet, Targets, TargetItems, KPIItems,
                        "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                        CustomerPartnerFunctions,
                        "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                        "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, "MaterialSaleAreas",
                        "Alerts?$filter=Application eq 'PD'",
                        "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions,
                        "VisitActivities", "Visits", "InvoiceItems", "InvoiceConditions",
                        "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                        "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                        ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' ",
                        "MaterialByCustomers",
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

                };
                return DEFINGREQARRAY;
            }
        } else if (sharedPreferences.getInt(CURRENT_VERSION_CODE, 0) == 28) {
            if (rollType.equalsIgnoreCase("Z5")) {
                String[] DEFINGREQARRAY = {/*"Attendances",*/// need to check for ASM and above role by removing
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        "Customers", KPISet, Targets, TargetItems, KPIItems, SyncHistorys, UserPartners,
                        "SalesPersons", OrderMaterialGroups, Brands,/* PlantStocks,*/ UserSalesPersons,
                        /*CustomerPartnerFunctions,*/
                        /*"MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",*/
                        /*"CustomerCreditLimits",*/ UserCustomers, CustomerSalesAreas, "MaterialSaleAreas",
                        "Alerts?$filter=Application eq 'PD'",
                        /*"Invoices", "InvoiceItemDetails", InvoicePartnerFunctions, "InvoiceItems", "InvoiceConditions",
                        "VisitActivities", "Visits",*/
                        "RoutePlans", /*"RouteSchedulePlans",*/ "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        /*"SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",*/
                        /*"MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",*/
                        /*ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,*/
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' ",
                        /*"MaterialByCustomers",*/
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "//or PropName eq 'Route'

                };
                return DEFINGREQARRAY;

            } else {
                String[] DEFINGREQARRAY = {"Attendances",
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        SyncHistorys, UserPartners,
                       // SPGeos,
                        "Customers", KPISet, Targets, TargetItems, KPIItems,
                        "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                        CustomerPartnerFunctions,
                        "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                        "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, "MaterialSaleAreas",
                        "Alerts?$filter=Application eq 'PD'",
                        "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions,
                        "VisitActivities", "Visits", "InvoiceItems", "InvoiceConditions",
                        "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                        "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                        ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP'",
                        "MaterialByCustomers",
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

                };
                return DEFINGREQARRAY;
            }
        } else if (sharedPreferences.getInt(CURRENT_VERSION_CODE, 0) == 29) {
            if (rollType.equalsIgnoreCase("Z5")) {
                String[] DEFINGREQARRAY = {/*"Attendances",*/// need to check for ASM and above role by removing
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        "Customers", KPISet, Targets, TargetItems, KPIItems, SyncHistorys, UserPartners,
                        "SalesPersons", OrderMaterialGroups, Brands,/* PlantStocks,*/ UserSalesPersons,
                        /*CustomerPartnerFunctions,*/
                        /*"MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",*/
                        /*"CustomerCreditLimits",*/ UserCustomers, CustomerSalesAreas, "MaterialSaleAreas",
                        "Alerts?$filter=Application eq 'PD'",
                        /*"Invoices", "InvoiceItemDetails", InvoicePartnerFunctions, "InvoiceItems", "InvoiceConditions",
                        "VisitActivities", "Visits",*/
                        "RoutePlans", /*"RouteSchedulePlans",*/ "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        /*"SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",*/
                        /*"MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",*/
                        /*ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,*/
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST' or Typeset eq 'RSFRJN'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' ",
                        /*"MaterialByCustomers",*/
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "//or PropName eq 'Route'

                };
                return DEFINGREQARRAY;

            } else if (rollType.equalsIgnoreCase("Z1") || rollType.equalsIgnoreCase("Z3")) {
                String[] DEFINGREQARRAY = {/*"Attendances",*/
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        SyncHistorys, UserPartners,
                        //SPGeos,
                        "Customers", KPISet, Targets, TargetItems, KPIItems,
                        "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                        CustomerPartnerFunctions,
                        "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                        "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, "MaterialSaleAreas",
                        "Alerts?$filter=Application eq 'PD'",
                        "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions,
                        "VisitActivities", "Visits", "InvoiceItems", "InvoiceConditions",
                        "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                        "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                        ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST' or Typeset eq 'RSFRJN'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP'",
                        "MaterialByCustomers",
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

                };
                return DEFINGREQARRAY;
            } else {
                String[] DEFINGREQARRAY = {"Attendances",
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        SyncHistorys, UserPartners,
                        //SPGeos,
                        "Customers", KPISet, Targets, TargetItems, KPIItems,
                        "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                        CustomerPartnerFunctions,
                        "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                        "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, "MaterialSaleAreas",
                        "Alerts?$filter=Application eq 'PD'",
                        "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions,
                        "VisitActivities", "Visits", "InvoiceItems", "InvoiceConditions",
                        "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                        "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                        ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST' or Typeset eq 'RSFRJN'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP'",
                        "MaterialByCustomers",
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

                };
                return DEFINGREQARRAY;
            }
        }else if (sharedPreferences.getInt(CURRENT_VERSION_CODE, 0) == 30) {
            if (rollType.equalsIgnoreCase("Z5")) {
                String[] DEFINGREQARRAY = {/*"Attendances",*/// need to check for ASM and above role by removing
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        "Customers", KPISet, Targets, TargetItems, KPIItems, SyncHistorys, UserPartners,
                        "SalesPersons", OrderMaterialGroups, Brands,/* PlantStocks,*/ UserSalesPersons,
                        /*CustomerPartnerFunctions,*/
                        /*"MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",*/
                        /*"CustomerCreditLimits",*/ UserCustomers, CustomerSalesAreas, "MaterialSaleAreas",
                        "Alerts?$filter=Application eq 'PD'",
                        /*"Invoices", "InvoiceItemDetails", InvoicePartnerFunctions, "InvoiceItems", "InvoiceConditions",
                        "VisitActivities", "Visits",*/
                        "RoutePlans", /*"RouteSchedulePlans",*/ "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        /*"SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",*/
                        /*"MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",*/
                        /*ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,*/
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST' or Typeset eq 'RSFRJN'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' or Typeset eq 'MTPSTY' ",
                        /*"MaterialByCustomers",*/
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "//or PropName eq 'Route'

                };
                return DEFINGREQARRAY;

            } else if (rollType.equalsIgnoreCase("Z1") || rollType.equalsIgnoreCase("Z3")) {
                String[] DEFINGREQARRAY = {/*"Attendances",*/
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        SyncHistorys, UserPartners,
                        //SPGeos,
                        "Customers", KPISet, Targets, TargetItems, KPIItems,
                        "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                        CustomerPartnerFunctions,
                        "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                        "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, "MaterialSaleAreas",
                        "Alerts?$filter=Application eq 'PD'",
                        "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions,
                        "VisitActivities", "Visits", "InvoiceItems", "InvoiceConditions",
                        "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                        "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                        ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST' or Typeset eq 'RSFRJN'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' or Typeset eq 'MTPSTY'",
                        "MaterialByCustomers",
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

                };
                return DEFINGREQARRAY;
            } else {
                String[] DEFINGREQARRAY = {"Attendances",
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        SyncHistorys, UserPartners,
                        //SPGeos,
                        "Customers", KPISet, Targets, TargetItems, KPIItems,
                        "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                        CustomerPartnerFunctions,
                        "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                        "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, "MaterialSaleAreas",
                        "Alerts?$filter=Application eq 'PD'",
                        "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions,
                        "VisitActivities", "Visits", "InvoiceItems", "InvoiceConditions",
                        "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                        "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                        ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST' or Typeset eq 'RSFRJN'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' or Typeset eq 'MTPSTY'",
                        "MaterialByCustomers",
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

                };
                return DEFINGREQARRAY;
            }
        } else if (sharedPreferences.getInt(CURRENT_VERSION_CODE, 0) == 31) {
            if (rollType.equalsIgnoreCase("Z5")) {
                String[] DEFINGREQARRAY = {/*"Attendances",*/// need to check for ASM and above role by removing
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        "Customers", KPISet, Targets, TargetItems, KPIItems, SyncHistorys, /*UserPartners,*/
                        "SalesPersons", OrderMaterialGroups, Brands,/* PlantStocks,*/ UserSalesPersons,
                        /*CustomerPartnerFunctions,*/
                        /*"MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",*/
                        /*"CustomerCreditLimits",*/ UserCustomers, CustomerSalesAreas, /*"MaterialSaleAreas",*/
                        "Alerts?$filter=Application eq 'PD'",
                        /*"Invoices", "InvoiceItemDetails", InvoicePartnerFunctions, "InvoiceItems", "InvoiceConditions",
                        "VisitActivities", "Visits",*/
                        "RoutePlans", /*"RouteSchedulePlans",*/ "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        /*"SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",*/
                        /*"MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",*/
                        /*ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,*/
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST' or Typeset eq 'RSFRJN'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' or Typeset eq 'MTPSTY' ",
                        /*"MaterialByCustomers",*/
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "//or PropName eq 'Route'

                };
                return DEFINGREQARRAY;

            } else if (rollType.equalsIgnoreCase("Z1") || rollType.equalsIgnoreCase("Z3")) {
                String[] DEFINGREQARRAY = {/*"Attendances",*/
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        SyncHistorys, /*UserPartners,*/
                        //SPGeos,
                        "Customers", KPISet, Targets, TargetItems, KPIItems,
                        "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                        CustomerPartnerFunctions,
                        "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                        "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, /*"MaterialSaleAreas",*/
                        "Alerts?$filter=Application eq 'PD'",
                        "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions,
                        "VisitActivities", "Visits", "InvoiceItems", "InvoiceConditions",
                        "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                        "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                        ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST' or Typeset eq 'RSFRJN'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' or Typeset eq 'MTPSTY'",
                        "MaterialByCustomers",
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

                };
                return DEFINGREQARRAY;
            } else {
                String[] DEFINGREQARRAY = {"Attendances",
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        SyncHistorys, /*UserPartners,*/
                        //SPGeos,
                        "Customers", KPISet, Targets, TargetItems, KPIItems,
                        "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                        CustomerPartnerFunctions,
                        "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                        "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, /*"MaterialSaleAreas",*/
                        "Alerts?$filter=Application eq 'PD'",
                        "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions,
                        "VisitActivities", "Visits", "InvoiceItems", "InvoiceConditions",
                        "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                        "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                        ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST' or Typeset eq 'RSFRJN'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' or Typeset eq 'MTPSTY'",
                        "MaterialByCustomers",
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

                };
                return DEFINGREQARRAY;
            }
        } else if (sharedPreferences.getInt(CURRENT_VERSION_CODE, 0) == 32) {
            if (rollType.equalsIgnoreCase("Z5")) {
                String[] DEFINGREQARRAY = {/*"Attendances",*/// need to check for ASM and above role by removing
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        "Customers", KPISet, Targets, TargetItems, KPIItems, SyncHistorys, UserPartners,
                        "SalesPersons", OrderMaterialGroups, Brands,/* PlantStocks,*/ UserSalesPersons,
                        /*CustomerPartnerFunctions,*/
                        /*"MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",*/
                        /*"CustomerCreditLimits",*/ UserCustomers, CustomerSalesAreas, /*"MaterialSaleAreas",*/
                        "Alerts?$filter=Application eq 'PD'",
                        /*"Invoices", "InvoiceItemDetails", InvoicePartnerFunctions, "InvoiceItems", "InvoiceConditions",
                        "VisitActivities", "Visits",*/
                        "RoutePlans", /*"RouteSchedulePlans",*/ "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        /*"SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",*/
                        /*"MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",*/
                        /*ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,*/
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST' or Typeset eq 'RSFRJN'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' or Typeset eq 'MTPSTY' ",
                        /*"MaterialByCustomers",*/
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "//or PropName eq 'Route'

                };
                return DEFINGREQARRAY;

            } else if (rollType.equalsIgnoreCase("Z1") || rollType.equalsIgnoreCase("Z3")) {
                String[] DEFINGREQARRAY = {/*"Attendances",*/
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        SyncHistorys, UserPartners,
                        //SPGeos,
                        "Customers", KPISet, Targets, TargetItems, KPIItems,
                        "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                        CustomerPartnerFunctions,
                        "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                        "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, /*"MaterialSaleAreas",*/
                        "Alerts?$filter=Application eq 'PD'",
                        "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions,
                        "VisitActivities", "Visits", "InvoiceItems", "InvoiceConditions",
                        "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                        "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                        ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST' or Typeset eq 'RSFRJN'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' or Typeset eq 'MTPSTY'",
                        "MaterialByCustomers",
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

                };
                return DEFINGREQARRAY;
            } else {
                String[] DEFINGREQARRAY = {"Attendances",
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        SyncHistorys, UserPartners,
                        //SPGeos,
                        "Customers", KPISet, Targets, TargetItems, KPIItems,
                        "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                        CustomerPartnerFunctions,
                        "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                        "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, /*"MaterialSaleAreas",*/
                        "Alerts?$filter=Application eq 'PD'",
                        "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions,
                        "VisitActivities", "Visits", "InvoiceItems", "InvoiceConditions",
                        "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                        "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                        ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST' or Typeset eq 'RSFRJN'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' or Typeset eq 'MTPSTY'",
                        "MaterialByCustomers",
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

                };
                return DEFINGREQARRAY;
            }
        } else if (sharedPreferences.getInt(CURRENT_VERSION_CODE, 0) == 33) {
            if (rollType.equalsIgnoreCase("Z5")) {
                String[] DEFINGREQARRAY = {/*"Attendances",*/// need to check for ASM and above role by removing
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        "Customers", KPISet, Targets, TargetItems, KPIItems, SyncHistorys, UserPartners,
                        "SalesPersons", OrderMaterialGroups, Brands,/* PlantStocks,*/ UserSalesPersons,
                        /*CustomerPartnerFunctions,*/
                        /*"MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",*/
                        /*"CustomerCreditLimits",*/ UserCustomers, CustomerSalesAreas, /*"MaterialSaleAreas",*/
                        "Alerts?$filter=Application eq 'PD'",
                        /*"Invoices", "InvoiceItemDetails", InvoicePartnerFunctions, "InvoiceItems", "InvoiceConditions",
                        "VisitActivities", "Visits",*/
                        "RoutePlans", /*"RouteSchedulePlans",*/ "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        /*"SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",*/
                        /*"MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",*/
                        /*ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,*/
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST' or Typeset eq 'RSFRJN'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' or Typeset eq 'MTPSTY' or Typeset eq 'MTLYTG'",
                        /*"MaterialByCustomers",*/
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "//or PropName eq 'Route'

                };
                return DEFINGREQARRAY;

            } else if (rollType.equalsIgnoreCase("Z1") || rollType.equalsIgnoreCase("Z3")) {
                String[] DEFINGREQARRAY = {/*"Attendances",*/
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        SyncHistorys, UserPartners,
                        //SPGeos,
                        "Customers", KPISet, Targets, TargetItems, KPIItems,
                        "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                        CustomerPartnerFunctions,
                        "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                        "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, /*"MaterialSaleAreas",*/
                        "Alerts?$filter=Application eq 'PD'",
                        "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions,
                        "VisitActivities", "Visits", "InvoiceItems", "InvoiceConditions",
                        "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                        "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                        ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST' or Typeset eq 'RSFRJN'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' or Typeset eq 'MTPSTY' or Typeset eq 'MTLYTG'",
                        "MaterialByCustomers",
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

                };
                return DEFINGREQARRAY;
            } else {
                String[] DEFINGREQARRAY = {"Attendances",
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        SyncHistorys, UserPartners,
                        //SPGeos,
                        "Customers", KPISet, Targets, TargetItems, KPIItems,
                        "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                        CustomerPartnerFunctions,
                        "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                        "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, /*"MaterialSaleAreas",*/
                        "Alerts?$filter=Application eq 'PD'",
                        "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions,
                        "VisitActivities", "Visits", "InvoiceItems", "InvoiceConditions",
                        "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                        "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                        ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST' or Typeset eq 'RSFRJN'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' or Typeset eq 'MTPSTY' or Typeset eq 'MTLYTG'",
                        "MaterialByCustomers",
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

                };
                return DEFINGREQARRAY;
            }
        }else if (sharedPreferences.getInt(CURRENT_VERSION_CODE, 0) == 34) {
            if (rollType.equalsIgnoreCase("Z5")) {
                String[] DEFINGREQARRAY = {/*"Attendances",*/// need to check for ASM and above role by removing
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27","ZZInactiveCustBlks",
                        "Customers", KPISet, Targets, TargetItems, KPIItems, SyncHistorys, UserPartners,
                        "SalesPersons", OrderMaterialGroups, Brands,/* PlantStocks,*/ UserSalesPersons,
                        /*CustomerPartnerFunctions,*/
                        /*"MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",*/
                        /*"CustomerCreditLimits",*/ UserCustomers, CustomerSalesAreas, /*"MaterialSaleAreas",*/
                        "Alerts?$filter=Application eq 'PD'",
                        /*"Invoices", "InvoiceItemDetails", InvoicePartnerFunctions, "InvoiceItems", "InvoiceConditions",
                        "VisitActivities", "Visits",*/
                        "RoutePlans", /*"RouteSchedulePlans",*/ "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        /*"SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",*/
                        /*"MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",*/
                        /*ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,*/
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST' or Typeset eq 'RSFRJN'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' or Typeset eq 'MTPSTY' or Typeset eq 'MTLYTG'",
                        /*"MaterialByCustomers",*/
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "//or PropName eq 'Route'

                };
                return DEFINGREQARRAY;

            } else if (rollType.equalsIgnoreCase("Z1") || rollType.equalsIgnoreCase("Z3")) {
                String[] DEFINGREQARRAY = {/*"Attendances",*/
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27","ZZInactiveCustBlks",
                        SyncHistorys, UserPartners,
                        //SPGeos,
                        "Customers", KPISet, Targets, TargetItems, KPIItems,
                        "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                        CustomerPartnerFunctions,
                        "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                        "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, /*"MaterialSaleAreas",*/
                        "Alerts?$filter=Application eq 'PD'",
                        "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions,
                        "VisitActivities", "Visits", "InvoiceItems", "InvoiceConditions",
                        "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                        "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                        ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST' or Typeset eq 'RSFRJN'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' or Typeset eq 'MTPSTY' or Typeset eq 'MTLYTG'",
                        "MaterialByCustomers",
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

                };
                return DEFINGREQARRAY;
            } else {
                String[] DEFINGREQARRAY = {"Attendances",
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        SyncHistorys, UserPartners,
                        //SPGeos,
                        "Customers", KPISet, Targets, TargetItems, KPIItems,"ZZInactiveCustBlks",
                        "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                        CustomerPartnerFunctions,
                        "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                        "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, /*"MaterialSaleAreas",*/
                        "Alerts?$filter=Application eq 'PD'",
                        "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions,
                        "VisitActivities", "Visits", "InvoiceItems", "InvoiceConditions",
                        "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                        "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                        ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST' or Typeset eq 'RSFRJN'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' or Typeset eq 'MTPSTY' or Typeset eq 'MTLYTG'",
                        "MaterialByCustomers",
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

                };
                return DEFINGREQARRAY;
            }
        } else {
            if (rollType.equalsIgnoreCase("Z5")) {
                String[] DEFINGREQARRAY = {"Attendances",// need to check for ASM and above role by removing
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        "Customers", KPISet, Targets, TargetItems, KPIItems,
                        "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                        CustomerPartnerFunctions,
                        "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                        "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, "MaterialSaleAreas",
                        "Alerts?$filter=Application eq 'PD'",
                        "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions, "InvoiceItems", "InvoiceConditions",
                        "VisitActivities", "Visits",
                        "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                        "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                        ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' ",
                        "MaterialByCustomers",
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

                };
                return DEFINGREQARRAY;

            } else {
                String[] DEFINGREQARRAY = {"Attendances",
                        "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                        "Customers", KPISet, Targets, TargetItems, KPIItems,
                        "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                        CustomerPartnerFunctions,
                        "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                        "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, "MaterialSaleAreas",
                        "Alerts?$filter=Application eq 'PD'",
                        "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions,
                        "VisitActivities", "Visits", "InvoiceItems", "InvoiceConditions",
                        "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                        "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                        "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                        "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                        ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                        Collections, Stocks,
                        "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                                "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST'",
                        "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                                "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                                "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' ",
                        "MaterialByCustomers",
                        "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                                "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                                "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                                "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

                };
                return DEFINGREQARRAY;
            }
        }
    }

    public static Hashtable<String, ArrayList<InvoiceBean>> convertToMapArryList(String jsonString) {
        Hashtable<String, ArrayList<InvoiceBean>> hashTableItemSerialNos = null;
        try {
            Gson gson = new Gson();
            Type stringStringMap = new TypeToken<Hashtable<String, ArrayList<InvoiceBean>>>() {
            }.getType();
            hashTableItemSerialNos = gson.fromJson(jsonString, stringStringMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashTableItemSerialNos;
    }

    public static ArrayList<BirthdaysBean> convertToBirthDayArryList(String jsonString) {
        ArrayList<BirthdaysBean> alBirthDayList = null;
        try {
            Gson gson = new Gson();
            Type stringStringMap = new TypeToken<ArrayList<BirthdaysBean>>() {
            }.getType();
            alBirthDayList = gson.fromJson(jsonString, stringStringMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alBirthDayList;
    }

    public static void customAlertMessage(Activity activity, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyTheme);
        builder.setMessage(msg).setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();


                    }
                });

        builder.show();
    }

    public static String removeLeadingZero(String value) {
        String textReturn = "";
        try {
            if (!value.equalsIgnoreCase("") && value != null) {
                textReturn = removeLeadingZero1(new BigDecimal(value));

            } else {
                textReturn = "0.00";
            }
        } catch (Exception e) {
            textReturn = "0.00";
            e.printStackTrace();
        }
        return textReturn;
    }

    public static String removeLeadingZero1(BigDecimal number) {
        // for your case use this pattern -> #,##0.00
        DecimalFormat df = new DecimalFormat("#####0.00");
        return df.format(number);
    }

    public static String getCurrency() {

        String currecyQry = Constants.SalesPersons + "?$select=" + Constants.Currency + "";
        String mStrCurrency = "";
        try {
            mStrCurrency = OfflineManager.getCurrency(currecyQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

        return mStrCurrency;
    }

    public static String getNameByCPGUID(String collName, String columnName, String whereColumnn, String whereColval) {
        String colmnVal = "";
        try {
            colmnVal = OfflineManager.getValueByColumnName(collName + "?$select=" + columnName + " &$filter = " + whereColumnn + " eq guid'" + whereColval + "'", columnName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return colmnVal;
    }

    public static String getName(String collName, String columnName, String whereColumnn, String whereColval) {
        String colmnVal = "";
        try {
            colmnVal = OfflineManager.getValueByColumnName(collName + "?$select=" + columnName + " &$filter = " + whereColumnn + " eq '" + whereColval + "'", columnName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return colmnVal;
    }

    public static String getConfigTypeIndicator(String collName, String columnName, String whereColumnn, String whereColval, String propertyColumn, String propVal) {
        String colmnVal = "";
        try {
            colmnVal = OfflineManager.getValueByColumnName(collName + "?$select=" + columnName + " &$filter = " + whereColumnn + " eq '" + whereColval + "' and " + propertyColumn + " eq '" + propVal + "' ", columnName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return colmnVal;
    }

    public static String getSPGUID(final String columnName) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SALES_PERSON_GUID = OfflineManager.getGuidValueByColumnName(Constants.UserSalesPersons + "?$select=" + columnName, columnName);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return SALES_PERSON_GUID;
    }

    public static JSONObject prepareInvoiceJsonObject(Hashtable<String, String> dbHeadTable, ArrayList<HashMap<String, String>> arrtable,
                                                      Hashtable<String, ArrayList<InvoiceBean>> hashTableItemSerialNos) {
        JSONObject invoiceHeader = new JSONObject();

        JSONObject invoiceItemDetails;

        JSONObject invoiceSerilaItemDetails;

        JSONArray invoiceItemDetailsArray = new JSONArray();

        JSONArray invoiceSerialItemDetailsArray;

        try {

            invoiceHeader.put("InvoiceGUID", dbHeadTable.get(Constants.InvoiceGUID));
            invoiceHeader.put("LoginID", dbHeadTable.get(Constants.LOGINID));
            invoiceHeader.put("InvoiceTypeID", dbHeadTable.get(Constants.InvoiceTypeID));
            invoiceHeader.put("InvoiceDate", dbHeadTable.get(Constants.InvoiceDate));
            invoiceHeader.put("CPNo", dbHeadTable.get(Constants.CPNo));
            invoiceHeader.put(Constants.CPGUID, dbHeadTable.get(Constants.CPGUID));
            invoiceHeader.put("SoldToID", dbHeadTable.get(Constants.SoldToID));
            invoiceHeader.put("ShipToID", dbHeadTable.get(Constants.SoldToID));
            invoiceHeader.put(Constants.CPTypeID, dbHeadTable.get(Constants.CPTypeID));
            invoiceHeader.put(Constants.SPGUID, dbHeadTable.get(Constants.SPGuid));
            invoiceHeader.put(Constants.NetAmount, dbHeadTable.get(Constants.NetAmount));
            invoiceHeader.put(Constants.TestRun, dbHeadTable.get(Constants.TestRun));
            invoiceHeader.put(Constants.Currency, dbHeadTable.get(Constants.Currency));

            invoiceHeader.put(Constants.SoldToCPGUID, dbHeadTable.get(Constants.SoldToCPGUID));
            invoiceHeader.put(Constants.SoldToTypeID, dbHeadTable.get(Constants.SoldToTypeID));
            invoiceHeader.put(Constants.ShipToCPGUID, dbHeadTable.get(Constants.SoldToCPGUID));
            invoiceHeader.put(Constants.ShipToTypeID, dbHeadTable.get(Constants.SoldToTypeID));
            invoiceHeader.put(Constants.SPNo, dbHeadTable.get(Constants.SPNo));


            for (int i = 0; i < arrtable.size(); i++) {

                invoiceItemDetails = new JSONObject();
                HashMap<String, String> singleRow = arrtable.get(i);
                invoiceItemDetails.put("ItemNo", ((i + 1)) + "");
                invoiceItemDetails.put("MaterialNo", singleRow.get("MatCode"));
                invoiceItemDetails.put("MaterialDesc", singleRow.get("MatDesc"));
                invoiceItemDetails.put("Quantity", singleRow.get("Qty"));
                invoiceItemDetails.put("InvoiceItemGUID", singleRow.get("InvoiceItemGUID"));
                invoiceItemDetails.put("InvoiceGUID", dbHeadTable.get("InvoiceGUID"));
                invoiceItemDetails.put("StockGuid", singleRow.get("StockGuid"));
                invoiceItemDetails.put("UOM", singleRow.get("UOM"));
                invoiceItemDetails.put(Constants.UnitPrice, singleRow.get(Constants.UnitPrice));


                invoiceItemDetails.put(Constants.NetAmount, singleRow.get(Constants.NetAmount));

                ArrayList<InvoiceBean> alItemSerialNo = hashTableItemSerialNos.get(singleRow.get("StockGuid"));

                int incementsize = 0;
                invoiceSerialItemDetailsArray = new JSONArray();
                if (alItemSerialNo != null && alItemSerialNo.size() > 0) {
                    for (int j = 0; j < alItemSerialNo.size(); j++) {
                        InvoiceBean serialNoInvoiceBean = alItemSerialNo.get(j);
                        if (!serialNoInvoiceBean.getStatus().equalsIgnoreCase("04")) {
                            invoiceSerilaItemDetails = new JSONObject();
                            invoiceSerilaItemDetails.put("ItemNo", ((incementsize + 1)) + "");
                            invoiceSerilaItemDetails.put("SerialNoFrom", serialNoInvoiceBean.getSerialNoFrom());
                            invoiceSerilaItemDetails.put("SerialNoTo", serialNoInvoiceBean.getSerialNoTo());
                            invoiceSerilaItemDetails.put("PrefixLength", serialNoInvoiceBean.getPrefixLength());
                            invoiceSerilaItemDetails.put("InvoiceItemSNoGUID", serialNoInvoiceBean.getSPSNoGUID());
                            invoiceSerilaItemDetails.put("InvoiceItemGUID", singleRow.get("InvoiceItemGUID"));
                            invoiceSerilaItemDetails.put("StatusID", serialNoInvoiceBean.getStatus());
                            invoiceSerilaItemDetails.put("Option", serialNoInvoiceBean.getOption());
                            invoiceSerilaItemDetails.put(Constants.UOM, serialNoInvoiceBean.getUom());
                            BigInteger qty = null;
                            try {
                                int prefixLen = (int) Double.parseDouble(serialNoInvoiceBean.getPrefixLength());
                                BigInteger doubAvalTo = new BigInteger(UtilConstants.removeAlphanumericText(serialNoInvoiceBean.getSerialNoTo(), prefixLen));

                                BigInteger doubAvalFrom = new BigInteger(UtilConstants.removeAlphanumericText(serialNoInvoiceBean.getSerialNoFrom(), prefixLen));

                                qty = (doubAvalTo.subtract(doubAvalFrom).add(new BigInteger("1")));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (qty != null) {
                                invoiceSerilaItemDetails.put("Quantity", qty.toString());
                            }
                            invoiceSerialItemDetailsArray.put(invoiceSerilaItemDetails);
                            incementsize++;
                        }
                    }
                }
                invoiceItemDetails.put("SSInvoiceItemSerialNos", invoiceSerialItemDetailsArray);
                invoiceItemDetailsArray.put(invoiceItemDetails);

            }

            invoiceHeader.put("SSInvoiceItemDetails", invoiceItemDetailsArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return invoiceHeader;
    }

    @SuppressLint("NewApi")
    public static void deletePendingReqFromDataVault(Context context, String tempNo) {
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        set = sharedPreferences.getStringSet("InvList", null);

        HashSet<String> setTemp = new HashSet<>();
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                setTemp.add(itr.next().toString());
            }
        }

        setTemp.remove(tempNo);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("InvList", setTemp);
        editor.commit();

        try {
            ConstantsUtils.storeInDataVault(tempNo, "",context);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void deletePendingVisitActivity(String visitActRefID) {

        ArrayList<InvoiceBean> alDeleteSnoList = null;
        VisitActivityBean visitActivityBean = null;
        try {
            visitActivityBean = OfflineManager.getVisitActivityGuid(Constants.VisitActivities + "?$filter=" + Constants.ActivityRefID + " eq guid'" + visitActRefID + "' ");

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        try {
            OfflineManager.deleteVisitActivity(visitActivityBean);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    @SuppressLint("NewApi")
    public static String getSyncType(Context context, String collectionName, String operation) {
        String mStrSyncType = "4";
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        String sharedVal = sharedPreferences.getString(collectionName, "");
        if (!sharedVal.equalsIgnoreCase("")) {
            if (operation.equalsIgnoreCase(CreateOperation)) {
                if (sharedVal.substring(0, 1).equalsIgnoreCase("0")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(0, 1).equalsIgnoreCase("1")) {
                    mStrSyncType = "1";
                } else if (sharedVal.substring(0, 1).equalsIgnoreCase("2")) {
                    mStrSyncType = "2";
                } else if (sharedVal.substring(0, 1).equalsIgnoreCase("3")) {
                    mStrSyncType = "3";
                } else if (sharedVal.substring(0, 1).equalsIgnoreCase("4")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(0, 1).equalsIgnoreCase("5")) {
                    mStrSyncType = "5";
                }
            } else if (operation.equalsIgnoreCase(ReadOperation)) {
                if (sharedVal.substring(1, 2).equalsIgnoreCase("0")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(1, 2).equalsIgnoreCase("1")) {
                    mStrSyncType = "1";
                } else if (sharedVal.substring(1, 2).equalsIgnoreCase("2")) {
                    mStrSyncType = "2";
                } else if (sharedVal.substring(1, 2).equalsIgnoreCase("3")) {
                    mStrSyncType = "3";
                } else if (sharedVal.substring(1, 2).equalsIgnoreCase("4")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(1, 2).equalsIgnoreCase("5")) {
                    mStrSyncType = "5";
                }

            } else if (operation.equalsIgnoreCase(UpdateOperation)) {
                if (sharedVal.substring(2, 3).equalsIgnoreCase("0")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(2, 3).equalsIgnoreCase("1")) {
                    mStrSyncType = "1";
                } else if (sharedVal.substring(2, 3).equalsIgnoreCase("2")) {
                    mStrSyncType = "2";
                } else if (sharedVal.substring(2, 3).equalsIgnoreCase("3")) {
                    mStrSyncType = "3";
                } else if (sharedVal.substring(2, 3).equalsIgnoreCase("4")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(2, 3).equalsIgnoreCase("5")) {
                    mStrSyncType = "5";
                }
            } else if (operation.equalsIgnoreCase(DeleteOperation)) {
                if (sharedVal.substring(3, 4).equalsIgnoreCase("0")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(3, 4).equalsIgnoreCase("1")) {
                    mStrSyncType = "1";
                } else if (sharedVal.substring(3, 4).equalsIgnoreCase("2")) {
                    mStrSyncType = "2";
                } else if (sharedVal.substring(3, 4).equalsIgnoreCase("3")) {
                    mStrSyncType = "3";
                } else if (sharedVal.substring(3, 4).equalsIgnoreCase("4")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(3, 4).equalsIgnoreCase("5")) {
                    mStrSyncType = "5";
                }
            } else if (operation.equalsIgnoreCase(QueryOperation)) {
                if (sharedVal.substring(4, 5).equalsIgnoreCase("0")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(4, 5).equalsIgnoreCase("1")) {
                    mStrSyncType = "1";
                } else if (sharedVal.substring(4, 5).equalsIgnoreCase("2")) {
                    mStrSyncType = "2";
                } else if (sharedVal.substring(4, 5).equalsIgnoreCase("3")) {
                    mStrSyncType = "3";
                } else if (sharedVal.substring(4, 5).equalsIgnoreCase("4")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(4, 5).equalsIgnoreCase("5")) {
                    mStrSyncType = "5";
                }
            }
        } else {
            mStrSyncType = "4";
        }


        return mStrSyncType;
    }

    public static void setAmountPattern(EditText editTxt, final int beforeDecimal, final int afterDecimal) {

        try {
            editTxt.setFilters(new InputFilter[]{new DecimalFilter(editTxt, beforeDecimal, afterDecimal)});

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static String getCPGUID(String collName, String columnName, String whereColumn, String whereColumnValue) {
        String getGuid = "";
        try {
            getGuid = OfflineManager.getGuidValueByColumnName(collName +
                    "?$select=" + columnName + " &$filter = " + whereColumn + " eq '" + whereColumnValue + "'", columnName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getGuid;
    }

    public static String getLastSyncTime(String collName, String whereCol, String whereColVal, String retiveColName, Context context) {
        String lastSyncTime = "";
        Cursor cursorLastSync = SyncHist.getInstance()
                .getLastSyncTime(collName, whereCol, whereColVal);

        if (cursorLastSync != null
                && cursorLastSync.getCount() > 0) {
            while (cursorLastSync.moveToNext()) {
                lastSyncTime = cursorLastSync
                        .getString(cursorLastSync
                                .getColumnIndex(retiveColName)) != null ? cursorLastSync
                        .getString(cursorLastSync
                                .getColumnIndex(retiveColName)) : "";
            }
        }
        String lastSyncDuration = "";
        if (!TextUtils.isEmpty(lastSyncTime))
            lastSyncDuration = UtilConstants.getTimeAgo(lastSyncTime, context);

        return lastSyncDuration;
    }

    public static String getSyncHistoryddmmyyyyTime() {
        String currentDateTimeString1 = (String) android.text.format.DateFormat.format("dd/MM/yyyy", new Date());
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        String currentDateTimeString2 = dateFormat.format(new Date());
        String currentDateTimeString = currentDateTimeString1 + "T" + currentDateTimeString2;
        return currentDateTimeString1 + " " + currentDateTimeString2;
    }

    public static String getSyncHistoryddmmyyyyTimeDelay() {
        String currentDateTimeString1 = (String) android.text.format.DateFormat.format("dd/MM/yyyy", new Date());
        Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
        calendar.add(Calendar.SECOND, 2);
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        String currentDateTimeString2 = dateFormat.format(date);

        String currentDateTimeString = currentDateTimeString1 + "T" + currentDateTimeString2;
        return currentDateTimeString1 + " " + currentDateTimeString2;
    }

    public static String convertStrGUID32to36(String strGUID32) {
        return CharBuffer.join9(StringFunction.substring(strGUID32, 0, 8), "-", StringFunction.substring(strGUID32, 8, 12), "-", StringFunction.substring(strGUID32, 12, 16), "-", StringFunction.substring(strGUID32, 16, 20), "-", StringFunction.substring(strGUID32, 20, 32));
    }

    public static String getFirstDateOfCurrentMonth() {
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return simpleDateFormat.format(cal.getTime()) + "T00:00:00";
    }

    public static long getFirstDateOfCurrentMonthInMiliseconds() {
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTimeInMillis();
    }

    /*Checks for GPS*/
    public static boolean onGpsCheck(final Context context) {
//        if(!UtilConstants.isGPSEnabled(context)){
        if (!UtilConstants.isGPSEnabled(context)) {
            AlertDialog.Builder gpsEnableDlg = new AlertDialog.Builder(context, R.style.MyTheme);
            gpsEnableDlg
                    .setMessage("GPS is not enabled. Do you want to go to settings menu?");
            gpsEnableDlg.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            context.startActivity(intent);
                        }
                    });
            // on pressing cancel button
            gpsEnableDlg.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            // Showing Alert Message
            gpsEnableDlg.show();
        }
        return UtilConstants.isGPSEnabled(context);
//        UtilConstants.canGetLocation(context);
//        if (!UtilConstants.canGetLocation(context)) {
//            AlertDialog.Builder gpsEnableDlg = new AlertDialog.Builder(context, R.style.MyTheme);
//            gpsEnableDlg
//                    .setMessage("GPS is not enabled. Do you want to go to settings menu?");
//            gpsEnableDlg.setPositiveButton("Settings",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent(
//                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                            context.startActivity(intent);
//                        }
//                    });
//            // on pressing cancel button
//            gpsEnableDlg.setNegativeButton("Cancel",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    });
//            // Showing Alert Message
//            gpsEnableDlg.show();
//        }
//        return GpsTracker.isGPSEnabled;
    }

    public static Hashtable getCollHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {

        Hashtable dbHeadTable = new Hashtable();

        try {
            dbHeadTable.put(Constants.DocumentNo, fetchJsonHeaderObject.getString(Constants.DocumentNo));
            dbHeadTable.put(Constants.BankName, fetchJsonHeaderObject.getString(Constants.BankName));
            dbHeadTable.put(Constants.CustomerNo, fetchJsonHeaderObject.getString(Constants.CustomerNo));
            dbHeadTable.put(Constants.CustomerName, fetchJsonHeaderObject.getString(Constants.CustomerName));
            dbHeadTable.put(Constants.InstrumentNo, fetchJsonHeaderObject.getString(Constants.InstrumentNo));
            dbHeadTable.put(Constants.Amount, fetchJsonHeaderObject.getString(Constants.Amount));
            dbHeadTable.put(Constants.Remarks, fetchJsonHeaderObject.getString(Constants.Remarks));
            dbHeadTable.put(Constants.CollectionTypeID, fetchJsonHeaderObject.getString(Constants.CollectionTypeID));
            dbHeadTable.put(Constants.PaymentMethodID, fetchJsonHeaderObject.getString(Constants.PaymentMethodID));
            dbHeadTable.put(Constants.PaymentMethodDesc, fetchJsonHeaderObject.getString(Constants.PaymentMethodDesc));
            dbHeadTable.put(Constants.InstrumentDate, fetchJsonHeaderObject.getString(Constants.InstrumentDate));
            dbHeadTable.put(Constants.LOGINID, fetchJsonHeaderObject.getString(Constants.LOGINID));
            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));
            dbHeadTable.put(Constants.DocumentDate, fetchJsonHeaderObject.getString(Constants.DocumentDate));
            dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.getString(Constants.CreatedOn));
            dbHeadTable.put(Constants.CreatedAt, fetchJsonHeaderObject.getString(Constants.CreatedAt));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static Hashtable getSOHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        Hashtable dbHeadTable = new Hashtable();
        try {


            dbHeadTable.put(Constants.SONo, fetchJsonHeaderObject.getString(Constants.SONo));
            dbHeadTable.put(Constants.LoginID, fetchJsonHeaderObject.getString(Constants.LoginID));
            dbHeadTable.put(Constants.CustomerNo, fetchJsonHeaderObject.getString(Constants.CustomerNo));
            dbHeadTable.put(Constants.CustomerName, fetchJsonHeaderObject.getString(Constants.CustomerName));
            dbHeadTable.put(Constants.CustomerPO, fetchJsonHeaderObject.getString(Constants.CustomerPO));
            dbHeadTable.put(Constants.CustomerPODate, fetchJsonHeaderObject.getString(Constants.CustomerPODate));
            dbHeadTable.put(Constants.OrderType, fetchJsonHeaderObject.getString(Constants.OrderType));
            dbHeadTable.put(Constants.OrderTypeDesc, fetchJsonHeaderObject.getString(Constants.OrderTypeDesc));
            dbHeadTable.put(Constants.OrderDate, fetchJsonHeaderObject.getString(Constants.OrderDate));
            dbHeadTable.put(Constants.EntryTime, fetchJsonHeaderObject.getString(Constants.EntryTime));
            dbHeadTable.put(Constants.ShipToParty, fetchJsonHeaderObject.getString(Constants.ShipToParty));
            dbHeadTable.put(Constants.ShipToPartyName, fetchJsonHeaderObject.getString(Constants.ShipToPartyName));
            dbHeadTable.put(Constants.SalesArea, fetchJsonHeaderObject.getString(Constants.SalesArea));
            dbHeadTable.put(Constants.NetPrice, fetchJsonHeaderObject.getString(Constants.NetPrice));
            dbHeadTable.put(Constants.TaxAmount, fetchJsonHeaderObject.getString(Constants.TaxAmount));
            dbHeadTable.put(Constants.Discount, fetchJsonHeaderObject.getString(Constants.Discount));
            dbHeadTable.put(Constants.Freight, fetchJsonHeaderObject.getString(Constants.Freight));

            dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.getString(Constants.CreatedOn));
            dbHeadTable.put(Constants.CreatedAt, fetchJsonHeaderObject.getString(Constants.CreatedAt));

            //dbHeadTable.put(Constants.SalesDist, fetchJsonHeaderObject.getString(Constants.SalesDist));
            dbHeadTable.put(Constants.Plant, fetchJsonHeaderObject.getString(Constants.SalesDist));
            //dbHeadTable.put(Constants.MeansOfTranstyp, fetchJsonHeaderObject.getString(Constants.SalesDist));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static JSONObject getSOHeaderValuesFrmJsonObject(JSONObject fetchJsonHeaderObject) {
        JSONObject dbHeadTable = new JSONObject();
        try {

            if(fetchJsonHeaderObject.has(Constants.SONo)) {
                dbHeadTable.put(Constants.SONo, fetchJsonHeaderObject.getString(Constants.SONo));
                REPEATABLE_REQUEST_ID = fetchJsonHeaderObject.getString(Constants.SONo);
            }
//            if(fetchJsonHeaderObject.has(Constants.LoginID)) {
//                dbHeadTable.put(Constants.LoginID, fetchJsonHeaderObject.getString(Constants.LoginID));
//            }
            if(fetchJsonHeaderObject.has(Constants.CustomerNo)) {
                dbHeadTable.put(Constants.CustomerNo, fetchJsonHeaderObject.getString(Constants.CustomerNo));
            }
            if(fetchJsonHeaderObject.has(Constants.CustomerName)) {
                dbHeadTable.put(Constants.CustomerName, fetchJsonHeaderObject.getString(Constants.CustomerName));
            }
            if(fetchJsonHeaderObject.has(Constants.CustomerPO)) {
                dbHeadTable.put(Constants.CustomerPO, fetchJsonHeaderObject.getString(Constants.CustomerPO));
            }
            if(fetchJsonHeaderObject.has(Constants.CustomerPODate)) {
                dbHeadTable.put(Constants.CustomerPODate, fetchJsonHeaderObject.getString(Constants.CustomerPODate));
            }
            if(fetchJsonHeaderObject.has(Constants.OrderType)) {
                dbHeadTable.put(Constants.OrderType, fetchJsonHeaderObject.getString(Constants.OrderType));
            }
            if(fetchJsonHeaderObject.has(Constants.OrderTypeDesc)) {
                dbHeadTable.put(Constants.OrderTypeDesc, fetchJsonHeaderObject.getString(Constants.OrderTypeDesc));
            }
            if(fetchJsonHeaderObject.has(Constants.OrderDate)) {
                dbHeadTable.put(Constants.OrderDate, fetchJsonHeaderObject.getString(Constants.OrderDate));
            }
            if(fetchJsonHeaderObject.has(Constants.EntryTime)) {
                dbHeadTable.put(Constants.EntryTime, fetchJsonHeaderObject.getString(Constants.EntryTime));
            }
            if(fetchJsonHeaderObject.has(Constants.ShipToParty)) {
                dbHeadTable.put(Constants.ShipToParty, fetchJsonHeaderObject.getString(Constants.ShipToParty));
            }
            if(fetchJsonHeaderObject.has(Constants.ShipToPartyName)) {
                dbHeadTable.put(Constants.ShipToPartyName, fetchJsonHeaderObject.getString(Constants.ShipToPartyName));
            }
            if(fetchJsonHeaderObject.has(Constants.SalesArea)) {
                dbHeadTable.put(Constants.SalesArea, fetchJsonHeaderObject.getString(Constants.SalesArea));
            }
            if(fetchJsonHeaderObject.has(Constants.NetPrice)) {
                dbHeadTable.put(Constants.NetPrice, fetchJsonHeaderObject.getString(Constants.NetPrice));
            }
            if(fetchJsonHeaderObject.has(Constants.TaxAmount)) {
                dbHeadTable.put(Constants.TaxAmount, fetchJsonHeaderObject.getString(Constants.TaxAmount));
            }
            if(fetchJsonHeaderObject.has(Constants.Discount)) {
                dbHeadTable.put(Constants.Discount, fetchJsonHeaderObject.getString(Constants.Discount));
            }
            if(fetchJsonHeaderObject.has(Constants.Freight)) {
                dbHeadTable.put(Constants.Freight, fetchJsonHeaderObject.getString(Constants.Freight));
            }
            if(fetchJsonHeaderObject.has(Constants.CreatedOn)) {
                dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.getString(Constants.CreatedOn));
            }
            if(fetchJsonHeaderObject.has(Constants.CreatedAt)) {
                dbHeadTable.put(Constants.CreatedAt, fetchJsonHeaderObject.getString(Constants.CreatedAt));
            }
//            if(fetchJsonHeaderObject.has(Constants.SalesDist)) {
            //dbHeadTable.put(Constants.SalesDist, fetchJsonHeaderObject.getString(Constants.SalesDist));
//            }
            if(fetchJsonHeaderObject.has(Constants.SalesDist)) {
                dbHeadTable.put(Constants.Plant, fetchJsonHeaderObject.getString(Constants.SalesDist));
            }
//            if(fetchJsonHeaderObject.has(Constants.MeansOfTranstyp)) {
            //dbHeadTable.put(Constants.MeansOfTranstyp, fetchJsonHeaderObject.getString(Constants.SalesDist));
//            }

            JSONArray itemsArray = new JSONArray(fetchJsonHeaderObject.getString(Constants.ITEM_TXT));
            JSONArray jsonArray = new JSONArray();
            for (int incrementVal = 0; incrementVal < itemsArray.length(); incrementVal++) {
                JSONObject singleRow = itemsArray.getJSONObject(incrementVal);

                JSONObject itemObject = new JSONObject();
                if (singleRow.has(Constants.SONo)) {
                    itemObject.put(Constants.SONo, singleRow.get(Constants.SONo));
                }
                if (singleRow.has(Constants.ItemNo)) {
                    itemObject.put(Constants.ItemNo, singleRow.get(Constants.ItemNo));
                }
                if (singleRow.has(Constants.MaterialGroup)) {
                    itemObject.put(Constants.MaterialGroup, singleRow.get(Constants.MaterialGroup));
                }
                if (singleRow.has(Constants.Material)) {
                    itemObject.put(Constants.Material, singleRow.get(Constants.Material));
                }
                if (singleRow.has(Constants.HighLevellItemNo)) {
                    itemObject.put(Constants.HighLevellItemNo, singleRow.get(Constants.HighLevellItemNo));
                }
                if (singleRow.has(Constants.ItemFlag)) {
                    itemObject.put(Constants.ItemFlag, singleRow.get(Constants.ItemFlag));
                }
                if (singleRow.has(Constants.ItemCategory)) {
                    itemObject.put(Constants.ItemCategory, singleRow.get(Constants.ItemCategory));
                }
                if (singleRow.has(Constants.MaterialDesc)) {
                    itemObject.put(Constants.MaterialDesc, singleRow.get(Constants.MaterialDesc));
                }
                if (singleRow.has(Constants.Plant)) {
                    itemObject.put(Constants.Plant, singleRow.get(Constants.Plant));
                }
                if (singleRow.has(Constants.StorLoc)) {
                    itemObject.put(Constants.StorLoc, singleRow.get(Constants.StorLoc));
                }
                if (singleRow.has(Constants.UOM)) {
                    itemObject.put(Constants.UOM, singleRow.get(Constants.UOM));
                }
                if (singleRow.has(Constants.Quantity)) {
                    itemObject.put(Constants.Quantity, singleRow.get(Constants.Quantity));
                }
                if (singleRow.has(Constants.Currency)) {
                    itemObject.put(Constants.Currency, singleRow.get(Constants.Currency));
                }
                if (singleRow.has(Constants.UnitPrice)) {
                    itemObject.put(Constants.UnitPrice, singleRow.get(Constants.UnitPrice));
                }
                if (singleRow.has(Constants.NetAmount)) {
                    itemObject.put(Constants.NetAmount, singleRow.get(Constants.NetAmount));
                }
                if (singleRow.has(Constants.AlternateWeight)) {
                    itemObject.put(Constants.AlternateWeight, singleRow.get(Constants.AlternateWeight));
                }
                if (singleRow.has(Constants.GrossAmount)) {
                    itemObject.put(Constants.GrossAmount, singleRow.get(Constants.GrossAmount));
                }
                if (singleRow.has(Constants.Freight)) {
                    itemObject.put(Constants.Freight, singleRow.get(Constants.Freight));
                }
                if (singleRow.has(Constants.Tax)) {
                    itemObject.put(Constants.Tax, singleRow.get(Constants.Tax));
                }
                if (singleRow.has(Constants.Discount)) {
                    itemObject.put(Constants.Discount, singleRow.get(Constants.Discount));
                }
                if (singleRow.has(Constants.RejReason)) {
                    itemObject.put(Constants.RejReason, singleRow.get(Constants.RejReason));
                }
                if (singleRow.has(Constants.RejReasonDesc)) {
                    itemObject.put(Constants.RejReasonDesc, singleRow.get(Constants.RejReasonDesc));
                }
                jsonArray.put(itemObject);
            }
            dbHeadTable.put(SOItemDetails, jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }


    public static final String getLastSyncTimeStamp(String tableName, String columnName, String columnValue) {
        return "select *  from  " + tableName + " Where " + columnName + "='" + columnValue + "'  ;";

    }

    public static final String getCollectionSuccessMsg(String fipDocNo) {
        return "Collection # " + fipDocNo + " created";
    }

    public static final void updateTCodetoSharedPreference(SharedPreferences sharedPreferences, SharedPreferences.Editor editor, ArrayList<Config> authList) {
        if (authList != null && authList.size() > 0) {
            if (sharedPreferences.contains("isStartCloseEnabled")) {
                editor.remove("isStartCloseEnabled");
            }
            if (sharedPreferences.contains("isRetailerListEnabled")) {
                editor.remove("isRetailerListEnabled");
            }
            if (sharedPreferences.contains("isRetailerUpdate")) {
                editor.remove("isRetailerUpdate");
            }
            if (sharedPreferences.contains("isRetailerCreate")) {
                editor.remove("isRetailerCreate");
            }
            if (sharedPreferences.contains("isHelpLine")) {
                editor.remove("isHelpLine");
            }
            if (sharedPreferences.contains("isMyStock")) {
                editor.remove("isMyStock");
            }
            if (sharedPreferences.contains("isVisitCreate")) {
                editor.remove("isVisitCreate");
            }
            if (sharedPreferences.contains("isTrends")) {
                editor.remove("isTrends");
            }
            if (sharedPreferences.contains("isRetailerStock")) {
                editor.remove("isRetailerStock");
            }
            if (sharedPreferences.contains("isCollHistory")) {
                editor.remove("isCollHistory");
            }
            if (sharedPreferences.contains(isInvHistoryEnabled)) {
                editor.remove(isInvHistoryEnabled);
            }
            if (sharedPreferences.contains("isRouteEnabled")) {
                editor.remove("isRouteEnabled");
            }
            if (sharedPreferences.contains("isAdhocVisitEnabled")) {
                editor.remove("isAdhocVisitEnabled");
            }
            if (sharedPreferences.contains("isTariffEnabled")) {
                editor.remove("isTariffEnabled");
            }
            if (sharedPreferences.contains("isSchemeEnabled")) {
                editor.remove("isSchemeEnabled");
            }
            if (sharedPreferences.contains("isBehaviourEnabled")) {
                editor.remove("isBehaviourEnabled");
            }
            if (sharedPreferences.contains("isMyTargetsEnabled")) {
                editor.remove("isMyTargetsEnabled");
            }
            if (sharedPreferences.contains("isMyPerformanceEnabled")) {
                editor.remove("isMyPerformanceEnabled");
            }
            if (sharedPreferences.contains(isSOApprovalKey)) {
                editor.remove(isSOApprovalKey);
            }
            if (sharedPreferences.contains(isFocusedProductKey)) {
                editor.remove(isFocusedProductKey);
            }
            if (sharedPreferences.contains(isNewProductKey)) {
                editor.remove(isNewProductKey);
            }
            if (sharedPreferences.contains(isFocusedProductKey)) {
                editor.remove(isFocusedProductKey);
            }
            if (sharedPreferences.contains(isOutstandingEnabled)) {
                editor.remove(isOutstandingEnabled);
            }
            if (sharedPreferences.contains(isDBStockKey)) {
                editor.remove(isDBStockKey);
            }
            if (sharedPreferences.contains(isAttndSumryEnabled)) {
                editor.remove(isAttndSumryEnabled);
            }
            if (sharedPreferences.contains(isClaimSumryEnabled)) {
                editor.remove(isClaimSumryEnabled);
            }


            if (sharedPreferences.contains("isActStatusEnabled")) {
                editor.remove("isActStatusEnabled");
            }
            if (sharedPreferences.contains(isMerchReviewKey)) {
                editor.remove(isMerchReviewKey);
            }
            if (sharedPreferences.contains(isMerchReviewListKey)) {
                editor.remove(isMerchReviewListKey);
            }
            if (sharedPreferences.contains(isSOCreateKey)) {
                editor.remove(isSOCreateKey);
            }
            if (sharedPreferences.contains("isCreateInvoiceEnabled")) {
                editor.remove("isCreateInvoiceEnabled");
            }
            if (sharedPreferences.contains(isCollCreateEnabledKey)) {
                editor.remove(isCollCreateEnabledKey);
            }
            if (sharedPreferences.contains("isFeedbackCreateEnabled")) {
                editor.remove("isFeedbackCreateEnabled");
            }
            if (sharedPreferences.contains("isCompInfoEnabled")) {
                editor.remove("isCompInfoEnabled");
            }

            if (sharedPreferences.contains(isSOCancelEnabled)) {
                editor.remove(isSOCancelEnabled);
            }

            if (sharedPreferences.contains(isSOChangeEnabled)) {
                editor.remove(isSOChangeEnabled);
            }
            if (sharedPreferences.contains(isSOWithSingleItemEnabled)) {
                editor.remove(isSOWithSingleItemEnabled);
            }
            if (sharedPreferences.contains(isPlantStockKey)) {
                editor.remove(isPlantStockKey);
            }
            if (sharedPreferences.contains(isMatPriceKey)) {
                editor.remove(isMatPriceKey);
            }
            if (sharedPreferences.contains(isRTGSEnabled)) {
                editor.remove(isRTGSEnabled);
            }
            if (sharedPreferences.contains(isMTPEnabled)) {
                editor.remove(isMTPEnabled);
            }
            if (sharedPreferences.contains(isMTPApprovalKey)) {
                editor.remove(isMTPApprovalKey);
            }
            if (sharedPreferences.contains(isMyTargetsEnabled)) {
                editor.remove(isMyTargetsEnabled);
            }
            if (sharedPreferences.contains(isCustomerListEnabled)) {
                editor.remove(isCustomerListEnabled);
            }
            if (sharedPreferences.contains(isDealerBehaviourEnabled)) {
                editor.remove(isDealerBehaviourEnabled);
            }
            if (sharedPreferences.contains(isMTPSubOrdinateEnabled)) {
                editor.remove(isMTPSubOrdinateEnabled);
            }
            if (sharedPreferences.contains(isRTGSSubOrdinateEnabled)) {
                editor.remove(isRTGSSubOrdinateEnabled);
            }
            if (sharedPreferences.contains(isROListKey)) {
                editor.remove(isROListKey);
            }
            if (sharedPreferences.contains(isROListItemKey)) {
                editor.remove(isROListItemKey);
            }
            if (sharedPreferences.contains(isGRReportKey)) {
                editor.remove(isGRReportKey);
            }

            editor.commit();

            for (int incVal = 0; incVal < authList.size(); incVal++) {

                if (authList.get(incVal).getFeature().equalsIgnoreCase("ENABLEDBG")) {
                    editor.putBoolean("writeDBGLog", true);
                }
                if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_ATTND")) {
                    editor.putString("isStartCloseEnabled", "/ARTEC/SF_ATTND");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_CP_GETLST")) {
                    editor.putString("isRetailerListEnabled", "/ARTEC/SS_CP_GETLST");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_CP_CHG")) {
                    editor.putString("isRetailerUpdate", "/ARTEC/SS_CP_CHG");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_CP_CRT")) {
                    editor.putString("isRetailerCreate", "/ARTEC/SS_CP_CRT");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_HELPLINE")) {
                    editor.putString("isHelpLine", "/ARTEC/SF_HELPLINE");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_MYSTK")) {
                    editor.putString("isMyStock", "/ARTEC/SF_MYSTK");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_VST")) {
                    editor.putString("isVisitCreate", "/ARTEC/SF_VST");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_TRENDS")) {
                    editor.putString("isTrends", "/ARTEC/SF_TRENDS");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_CPSTK")) {
                    editor.putString("isRetailerStock", "/ARTEC/SS_CPSTK");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_COLLLIST")) {
                    editor.putString("isCollHistory", "/ARTEC/SF_COLLLIST");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isInvoiceHistoryTcode)) {
                    editor.putString(isInvHistoryEnabled, isInvoiceHistoryTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_ROUTPLAN")) {
                    editor.putString("isRouteEnabled", "/ARTEC/SF_ROUTPLAN");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_ADHOCVST")) {
                    editor.putString("isAdhocVisitEnabled", "/ARTEC/SF_ADHOCVST");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_TARIFF")) {
                    editor.putString("isTariffEnabled", "/ARTEC/SS_TARIFF");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_SCHEME")) {
                    editor.putString("isSchemeEnabled", "/ARTEC/SS_SCHEME");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_SPCP_EVAL")) {
                    editor.putString("isBehaviourEnabled", "/ARTEC/SS_SPCP_EVAL");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_MYTRGTS")) {
                    editor.putString("isMyTargetsEnabled", "/ARTEC/SS_MYTRGTS");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_MYPERF")) {
                    editor.putString("isMyPerformanceEnabled", "/ARTEC/SS_MYPERF");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isFocusedProductTcode)) {
                    editor.putString(isFocusedProductKey, isFocusedProductTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isNewProductTcode)) {
                    editor.putString(isNewProductKey, isNewProductTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isMustSellTcode)) {
                    editor.putString(isMustSellKey, isMustSellTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_ACTSTS")) {
                    editor.putString("isActStatusEnabled", "/ARTEC/SS_ACTSTS");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isMerchReviewTcode)) {
                    editor.putString(isMerchReviewKey, isMerchReviewTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_INVCRT")) {
                    editor.putString("isCreateInvoiceEnabled", "/ARTEC/SS_INVCRT");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isCollCreateTcode)) {
                    editor.putString(isCollCreateEnabledKey, isCollCreateTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_FDBKCRT")) {
                    editor.putString("isFeedbackCreateEnabled", "/ARTEC/SF_FDBKCRT");

                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_FDBKLIST")) {
                    editor.putString("isFeedbackListEnabled", "/ARTEC/SF_FDBKLIST");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_COMPINFO")) {
                    editor.putString("isCompInfoEnabled", "/ARTEC/SS_COMPINFO");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isOutStandingTcode)) {
                    editor.putString(isOutstandingEnabled, isOutStandingTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isMerchReviewListTcode)) {
                    editor.putString(isMerchReviewListKey, isMerchReviewListTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_CUSTLIST")) {
                    editor.putString("isRetailerListEnabled", "/ARTEC/SF_CUSTLIST");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isSOApprovalTcode)) {
                    editor.putString(isSOApprovalKey, isSOApprovalTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isPlantStockTcode)) {
                    editor.putString(isPlantStockKey, isPlantStockTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isMatpriceTcode)) {
                    editor.putString(isMatPriceKey, isMatpriceTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isRTGSTcode)) {
                    editor.putString(isRTGSEnabled, isRTGSTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isMTPTcode)) {
                    editor.putString(isMTPEnabled, isMTPTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isMTPApprovalTcode)) {
                    editor.putString(isMTPApprovalKey, isMTPApprovalTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isMyTargetsTcode)) {
                    editor.putString(isMyTargetsEnabled, isMyTargetsTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isCustomerListTcode)) {
                    editor.putString(isCustomerListEnabled, isCustomerListTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isDealerBehaviourTcode)) {
                    editor.putString(isDealerBehaviourEnabled, isDealerBehaviourTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isMTPSubOrdinateTcode)) {
                    editor.putString(isMTPSubOrdinateEnabled, isMTPSubOrdinateTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isRTGSSubOrdinateTcode)) {
                    editor.putString(isRTGSSubOrdinateEnabled, isRTGSSubOrdinateTcode);
                }


                //new
                else if (authList.get(incVal).getFeature().equalsIgnoreCase(isSOCreateTcode)) {
                    editor.putString(isSOCreateKey, isSOCreateTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_EXPCRT")) {
                    editor.putString("isExpenseEntryEnabled", "/ARTEC/SF_EXPCRT");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_EXPLIST")) {
                    editor.putString("isExpenseListEnabled", "/ARTEC/SF_EXPLIST");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_CRDLMT")) {
                    editor.putString("isCreditLimitEnabled", "/ARTEC/SF_CRDLMT");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_SOCRT")) {
                    editor.putString("isSOCreateEnabled", "/ARTEC/SF_SOCRT");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isSOListTcode)) {
                    editor.putString(isSOListEnabled, isSOListTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isDBStockTcode)) {
                    editor.putString(isDBStockKey, isDBStockTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_SOCHN")) {
                    editor.putString(isSOChangeEnabled, "/ARTEC/SF_SOCHN");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_SOCNCL")) {
                    editor.putString(isSOCancelEnabled, "/ARTEC/SF_SOCNCL");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_SOCRT01")) {
                    editor.putString(isSOWithSingleItemEnabled, "/ARTEC/SF_SOCRT01");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isROLisTcode)) {
                    editor.putString(isROListKey, isROLisTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isROLisItemTcode)) {
                    editor.putString(isROListItemKey, isROLisItemTcode);
                }else if (authList.get(incVal).getFeature().equalsIgnoreCase(isAttndSumryTcode)) {
                    editor.putString(isAttndSumryEnabled, isAttndSumryTcode);
                }else if (authList.get(incVal).getFeature().equalsIgnoreCase(isClaimSumryTcode)) {
                    editor.putString(isClaimSumryEnabled, isClaimSumryTcode);
                }else if (authList.get(incVal).getFeature().equalsIgnoreCase(isGRReportTcode)) {
                    editor.putString(isGRReportKey, isGRReportTcode);
                }

                editor.commit();
            }
        }
    }

    public static final void setIconVisibiltyReports(SharedPreferences sharedPreferences, int[] mArrIntReportsOriginalStatus) {
        String sharedVal = sharedPreferences.getString(isSOListEnabled, "");
        if (sharedVal.equalsIgnoreCase(isSOListTcode)) {
            mArrIntReportsOriginalStatus[0] = 1;
        } else {
            mArrIntReportsOriginalStatus[0] = 0;
        }
        sharedVal = sharedPreferences.getString("isCollHistory", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SF_COLLLIST")) {
            mArrIntReportsOriginalStatus[1] = 1;
        } else {
            mArrIntReportsOriginalStatus[1] = 0;
        }

        sharedVal = sharedPreferences.getString(isOutstandingEnabled, "");
        if (sharedVal.equalsIgnoreCase(isOutStandingTcode)) {
            mArrIntReportsOriginalStatus[2] = 1;
        } else {
            mArrIntReportsOriginalStatus[2] = 0;
        }

        sharedVal = sharedPreferences.getString(isMustSellKey, "");
        if (sharedVal.equalsIgnoreCase(isMustSellTcode)) {
            mArrIntReportsOriginalStatus[3] = 1;
        } else {
            mArrIntReportsOriginalStatus[3] = 0;
        }
        sharedVal = sharedPreferences.getString(isFocusedProductKey, "");
        if (sharedVal.equalsIgnoreCase(isFocusedProductTcode)) {
            mArrIntReportsOriginalStatus[4] = 1;
        } else {
            mArrIntReportsOriginalStatus[4] = 0;
        }

        sharedVal = sharedPreferences.getString(isNewProductKey, "");
        if (sharedVal.equalsIgnoreCase(isNewProductTcode)) {
            mArrIntReportsOriginalStatus[5] = 1;
        } else {
            mArrIntReportsOriginalStatus[5] = 0;
        }

        sharedVal = sharedPreferences.getString(isMerchReviewListKey, "");
        if (sharedVal.equalsIgnoreCase(isMerchReviewListTcode)) {
            mArrIntReportsOriginalStatus[6] = 1;
        } else {
            mArrIntReportsOriginalStatus[6] = 0;
        }

        sharedVal = sharedPreferences.getString(isInvHistoryEnabled, "");
        if (sharedVal.equalsIgnoreCase(isInvoiceHistoryTcode)) {
            mArrIntReportsOriginalStatus[7] = 1;
        } else {
            mArrIntReportsOriginalStatus[7] = 0;
        }


        sharedVal = sharedPreferences.getString("isCreditLimitEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SF_CRDLMT")) {
            mArrIntReportsOriginalStatus[8] = 1;
        } else {
            mArrIntReportsOriginalStatus[8] = 0;
        }

        //TO DO

        mArrIntReportsOriginalStatus[9] = 0;


        mArrIntReportsOriginalStatus[10] = 0;
        mArrIntReportsOriginalStatus[11] = 0;
        mArrIntReportsOriginalStatus[12] = 0;
        mArrIntReportsOriginalStatus[13] = 0;
        mArrIntReportsOriginalStatus[14] = 0;
        mArrIntReportsOriginalStatus[15] = 0;
        sharedVal = sharedPreferences.getString("isTrends", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SF_TRENDS")) {
            mArrIntReportsOriginalStatus[16] = 1;
        } else {
            mArrIntReportsOriginalStatus[16] = 0;
        }

        sharedVal = sharedPreferences.getString(isROListKey, "");
        if (sharedVal.equalsIgnoreCase(isROLisTcode)) {
            mArrIntReportsOriginalStatus[17] = 1;
        } else {
            mArrIntReportsOriginalStatus[17] = 0;
        }
        sharedVal = sharedPreferences.getString(isROListItemKey, "");
        if (sharedVal.equalsIgnoreCase(isROLisItemTcode)) {
            mArrIntReportsOriginalStatus[18] = 1;
        } else {
            mArrIntReportsOriginalStatus[18] = 0;
        }

        if (sharedPreferences.getString(Constants.isGRReportKey, "").equalsIgnoreCase(Constants.isGRReportTcode)) {
            mArrIntReportsOriginalStatus[19] = 1;
        } else {
            mArrIntReportsOriginalStatus[19] = 0;
        }

    }

    public static final void setIconVisibilty(SharedPreferences sharedPreferences, int[] mArrIntMainMenuOriginalStatus, int[] mArrIntMainMenuReportsOriginalStatus) {
        String sharedVal = sharedPreferences.getString("isStartCloseEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SF_ATTND")) {
//        if (sharedVal.equalsIgnoreCase("/ARTEC/MC_ATTND")) {
            mArrIntMainMenuOriginalStatus[0] = 1;
        } else {
            mArrIntMainMenuOriginalStatus[0] = 0;
        }
        sharedVal = sharedPreferences.getString("isRouteEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SF_ROUTPLAN")) {
            mArrIntMainMenuOriginalStatus[1] = 1;
        } else {
            mArrIntMainMenuOriginalStatus[1] = 0;
        }

        sharedVal = sharedPreferences.getString("isMyTargetsEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_MYTRGTS")) {
            mArrIntMainMenuOriginalStatus[2] = 1;
        } else {
            mArrIntMainMenuOriginalStatus[2] = 0;
        }

//        mArrIntMainMenuOriginalStatus[3] = 1; //Schemes
        mArrIntMainMenuOriginalStatus[3] = 0; //Schemes
        sharedVal = sharedPreferences.getString(isDBStockKey, "");
        if (sharedVal.equalsIgnoreCase(isDBStockTcode)) {
            mArrIntMainMenuOriginalStatus[4] = 1;// Depot Stock
        } else {
            mArrIntMainMenuOriginalStatus[4] = 0;// Depot Stock
        }

        sharedVal = sharedPreferences.getString(isDaySummaryEnabled, "");
        if (sharedVal.equalsIgnoreCase(isDaySummaryTcode)) {
            mArrIntMainMenuOriginalStatus[5] = 1;
            ;
        } else {
            mArrIntMainMenuOriginalStatus[5] = 0;
        }

        sharedVal = sharedPreferences.getString("isAdhocVisitEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SF_ADHOCVST")) {
//        if (sharedVal.equalsIgnoreCase("/ARTEC/ADHOC_VST")) {
            mArrIntMainMenuOriginalStatus[8] = 1;
        } else {
            mArrIntMainMenuOriginalStatus[8] = 0;
        }
        if (isAlertRecordsAvailable) // alerts
            mArrIntMainMenuOriginalStatus[9] = 0;
        else
            mArrIntMainMenuOriginalStatus[9] = 0;

        sharedVal = sharedPreferences.getString("isExpenseEntryEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SF_EXPCRT")) {
            mArrIntMainMenuOriginalStatus[10] = 0;
        } else {
            mArrIntMainMenuOriginalStatus[10] = 0;
        }
        sharedVal = sharedPreferences.getString("isExpenseListEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SF_EXPLIST")) {
            mArrIntMainMenuOriginalStatus[11] = 0;
        } else {
            mArrIntMainMenuOriginalStatus[11] = 0;
        }
//        mArrIntMainMenuOriginalStatus[12] = 1;
//
//        mArrIntMainMenuOriginalStatus[13] = 1;
//        mArrIntMainMenuOriginalStatus[14] = 1;
//        mArrIntMainMenuOriginalStatus[15] = 1;
//        mArrIntMainMenuOriginalStatus[16] = 1;
//
//        mArrIntMainMenuOriginalStatus[17] = 1;

        sharedVal = sharedPreferences.getString(isSOApprovalKey, "");
        if (sharedVal.equalsIgnoreCase(isSOApprovalTcode)) {
            mArrIntMainMenuOriginalStatus[12] = 1;
        } else {
            mArrIntMainMenuOriginalStatus[12] = 0;
        }
        mArrIntMainMenuOriginalStatus[13] = 1;

        mArrIntMainMenuOriginalStatus[14] = 1;
        mArrIntMainMenuOriginalStatus[15] = 1;


        sharedVal = sharedPreferences.getString("isRetailerListEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_CP_GETLST")) {
            mArrIntMainMenuReportsOriginalStatus[0] = 1;
        } else {
            mArrIntMainMenuReportsOriginalStatus[0] = 1;
        }

        mArrIntMainMenuReportsOriginalStatus[1] = 0;
        mArrIntMainMenuReportsOriginalStatus[2] = 0;


    }

    public static final void createDB(SQLiteDatabase db) {
        String sql = "create table if not exists "
                + Constants.DATABASE_REGISTRATION_TABLE
                + "( username  text, password   text,repassword text,themeId text,mainView text);";
        Log.d("EventsData", "onCreate: " + sql);
        db.execSQL(sql);
    }

    public static final void insertHistoryDB(SQLiteDatabase db, String tblName, String clmname, String value) {
        String sql = "INSERT INTO " + tblName + "( " + clmname + ") VALUES('"
                + value + "') ;";
        db.execSQL(sql);
    }

    public static final void updateStatus(SQLiteDatabase db, String tblName, String clmname, String value, String inspectionLot) {
        String sql = "UPDATE " + tblName + " SET  " + clmname + "='" + value
                + "' Where Collections = '" + inspectionLot + "';";
        db.execSQL(sql);
    }

    public static final void createTable(SQLiteDatabase db, String tableName, String clumsname) {
        try {
            String sql = Constants.create_table + tableName
                    + " ( " + clumsname + ", Status text );";
            Log.d(Constants.EventsData, Constants.on_Create + sql);
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final void deleteTable(SQLiteDatabase db, String tableName) {
        try {
            String delSql = Constants.delete_from + tableName;
            db.execSQL(delSql);

        } catch (Exception e) {
            System.out.println("createTableKey(EventDataSqlHelper): " + e.getMessage());
        }
    }

    /*
       TODO Get Current Day Birthdays list
    */
    public static ArrayList<BirthdaysBean> getTodayBirthDayList() {
        ArrayList<BirthdaysBean> alRetBirthDayList = null;
        ArrayList<BirthdaysBean> alAppointmentList = null;
        String[][] oneWeekDay = UtilConstants.getOneweekValues(1);
        if (oneWeekDay != null && oneWeekDay.length > 0) {
            for (int i = 0; i < oneWeekDay[0].length; i++) {

                String[] splitDayMonth = oneWeekDay[0][i].split("-");

                String mStrBirthdayAvlQry = Constants.ChannelPartners + "?$filter=(month%28" + Constants.DOB + "%29%20eq " + splitDayMonth[0] + " " +
                        "and day%28" + Constants.DOB + "%29%20eq " + UtilConstants.removeLeadingZeros(splitDayMonth[1]) + ") or (month%28" + Constants.Anniversary + "%29%20eq " + splitDayMonth[0] + " " +
                        "and day%28" + Constants.Anniversary + "%29%20eq " + UtilConstants.removeLeadingZeros(splitDayMonth[1]) + ") ";
                try {
                    if (OfflineManager.getVisitStatusForCustomer(mStrBirthdayAvlQry)) {

                        try {
                            alRetBirthDayList = OfflineManager.getTodayBirthDayList(mStrBirthdayAvlQry);
                        } catch (OfflineODataStoreException e) {
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                        }
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                String mStrSPGUID = Constants.getSPGUID(Constants.SPGUID);

                String mStrAppointmentListQuery = Constants.Visits + "?$filter=" + Constants.StatusID + " eq '00' and (month%28" + Constants.PlannedDate + "%29%20eq " + splitDayMonth[0] + " " +
                        "and day%28" + Constants.PlannedDate + "%29%20eq " + UtilConstants.removeLeadingZeros(splitDayMonth[1]) + ") and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
                try {
                    alAppointmentList = OfflineManager.getAppointmentListForAlert(mStrAppointmentListQuery);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }


                if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                    if (alAppointmentList != null && alAppointmentList.size() > 0) {
                        alRetBirthDayList.addAll(alRetBirthDayList.size(), alAppointmentList);
                    }
                } else {
                    alRetBirthDayList = new ArrayList<>();
                    if (alAppointmentList != null && alAppointmentList.size() > 0) {
                        alRetBirthDayList.addAll(alAppointmentList);
                    }
                }

            }
        }

        return alRetBirthDayList;
    }

    public static void setCurrentDateTOSharedPerf(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME,
                0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.BirthDayAlertsDate, UtilConstants.getDate1());
        editor.commit();

    }

    // add values into data vault
    public static void assignValuesIntoDataVault(ArrayList<BirthdaysBean> alRetBirthDayList,Context context) {

        Gson gson = new Gson();
        Hashtable dbHeaderTable = new Hashtable();
        try {
            String jsonFromMap = gson.toJson(alRetBirthDayList);
            //noinspection unchecked
            dbHeaderTable.put(Constants.ITEM_TXT, jsonFromMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonHeaderObject = new JSONObject(dbHeaderTable);
        //noinspection deprecation
        try {
            //noinspection deprecation
            ConstantsUtils.storeInDataVault(Constants.BirthDayAlertsKey, jsonHeaderObject.toString(),context);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<BirthdaysBean> getBirthdayListFromDataVault(String mStrKeyVal) {

        ArrayList<BirthdaysBean> beanArrayList = null;
        //Fetch object from data vault
        try {

            JSONObject fetchJsonHeaderObject = new JSONObject(mStrKeyVal);

            String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);

            beanArrayList = convertToBirthDayArryList(itemsString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return beanArrayList;
    }

    // TODO add  empty values into data vault
    public static void assignEmptyValuesIntoDataVault(Context context) {
        try {
            //noinspection deprecation
            ConstantsUtils.storeInDataVault(Constants.BirthDayAlertsKey, "",context);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void setBirthdayListToDataValut(Context context) {
        String[][] oneWeekDay;
        oneWeekDay = UtilConstants.getOneweekValues(1);
        String splitDayMonth[] = oneWeekDay[0][0].split("-");

        ArrayList<BirthdaysBean> alRetBirthDayTempList = new ArrayList<>();
        ArrayList<BirthdaysBean> alDataVaultList = new ArrayList<>();
        ArrayList<BirthdaysBean> alRetBirthDayList = getTodayBirthDayList();

        try {
            SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME,
                    0);
            String mStrBirthdayDate = settings.getString(Constants.BirthDayAlertsDate, "");

            if (mStrBirthdayDate.equalsIgnoreCase(UtilConstants.getDate1())) {

                String store = null;
                try {
                    store = ConstantsUtils.getFromDataVault(Constants.BirthDayAlertsKey,context);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                if (store != null && !store.equalsIgnoreCase("")) {
                    alDataVaultList = Constants.getBirthdayListFromDataVault(store);


                    if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                        for (BirthdaysBean firstBeanAL : alRetBirthDayList) {
                            boolean mBoolIsRecordExists = false;

                            if (alDataVaultList != null && alDataVaultList.size() > 0) {

                                // Loop arrayList1 items
                                for (BirthdaysBean secondBeanAL : alDataVaultList) {
                                    if (firstBeanAL.getCPUID().toUpperCase().equalsIgnoreCase(secondBeanAL.getCPUID()) && (!firstBeanAL.getAppointmentAlert()
                                            && !secondBeanAL.getAppointmentAlert())) {

                                        if ((secondBeanAL.getDOB().equalsIgnoreCase(firstBeanAL.getDOB())
                                                || (secondBeanAL.getAnniversary().equalsIgnoreCase(firstBeanAL.getAnniversary())))) {

                                            BirthdaysBean birthdaysBean = new BirthdaysBean();
                                            birthdaysBean.setCPUID(firstBeanAL.getCPUID());
                                            if (firstBeanAL.getDOB().contains(splitDayMonth[1] + "/" + splitDayMonth[0]) && secondBeanAL.getDOBStatus().equalsIgnoreCase(""))
                                                birthdaysBean.setDOBStatus("");
                                            else
                                                birthdaysBean.setDOBStatus(Constants.X);

                                            if (firstBeanAL.getAnniversary().contains(splitDayMonth[1] + "/" + splitDayMonth[0]) && secondBeanAL.getAnniversaryStatus().equalsIgnoreCase(""))
                                                birthdaysBean.setAnniversaryStatus("");
                                            else
                                                birthdaysBean.setAnniversaryStatus(Constants.X);

                                            birthdaysBean.setMobileNo(firstBeanAL.getMobileNo());
                                            birthdaysBean.setDOB(firstBeanAL.getDOB());
                                            birthdaysBean.setAnniversary(firstBeanAL.getAnniversary());
                                            birthdaysBean.setOwnerName(firstBeanAL.getOwnerName());
                                            birthdaysBean.setRetailerName(firstBeanAL.getRetailerName());
                                            birthdaysBean.setMobileNo(firstBeanAL.getMobileNo());
                                            alRetBirthDayTempList.add(birthdaysBean);
                                            mBoolIsRecordExists = true;
                                            break;
                                        }

                                    } else {
                                        if (firstBeanAL.getCPUID().toUpperCase().equalsIgnoreCase(secondBeanAL.getCPUID())
                                                && (firstBeanAL.getAppointmentAlert()
                                                && secondBeanAL.getAppointmentAlert())) {
                                            BirthdaysBean birthdaysBean = new BirthdaysBean();
                                            birthdaysBean.setCPUID(firstBeanAL.getCPUID());
                                            if (secondBeanAL.getAppointmentStatus().equalsIgnoreCase(""))
                                                birthdaysBean.setAppointmentStatus("");
                                            else
                                                birthdaysBean.setAppointmentStatus(Constants.X);

                                            birthdaysBean.setMobileNo(firstBeanAL.getMobileNo());
                                            birthdaysBean.setOwnerName(firstBeanAL.getOwnerName());
                                            birthdaysBean.setRetailerName(firstBeanAL.getRetailerName());
                                            birthdaysBean.setMobileNo(firstBeanAL.getMobileNo());
                                            birthdaysBean.setAppointmentTime(firstBeanAL.getAppointmentTime());
                                            birthdaysBean.setAppointmentEndTime(firstBeanAL.getAppointmentEndTime());
                                            birthdaysBean.setAppointmentType(firstBeanAL.getAppointmentType());
                                            birthdaysBean.setAppointmentAlert(true);
                                            alRetBirthDayTempList.add(birthdaysBean);
                                            mBoolIsRecordExists = true;
                                            break;
                                        }
                                    }
                                }

                                if (!mBoolIsRecordExists) {
                                    BirthdaysBean birthdaysBean = new BirthdaysBean();
                                    if (!firstBeanAL.getAppointmentAlert()) {
                                        birthdaysBean.setCPUID(firstBeanAL.getCPUID());
                                        birthdaysBean.setDOBStatus(firstBeanAL.getDOBStatus());
                                        birthdaysBean.setAnniversaryStatus(firstBeanAL.getAnniversaryStatus());
                                        birthdaysBean.setMobileNo(firstBeanAL.getMobileNo());
                                        birthdaysBean.setDOB(firstBeanAL.getDOB());
                                        birthdaysBean.setAnniversary(firstBeanAL.getAnniversary());
                                        birthdaysBean.setOwnerName(firstBeanAL.getOwnerName());
                                        birthdaysBean.setRetailerName(firstBeanAL.getRetailerName());
                                        alRetBirthDayTempList.add(birthdaysBean);
                                    } else {
                                        birthdaysBean.setCPUID(firstBeanAL.getCPUID());
                                        birthdaysBean.setAppointmentStatus("");
                                        birthdaysBean.setMobileNo(firstBeanAL.getMobileNo());
                                        birthdaysBean.setOwnerName(firstBeanAL.getOwnerName());
                                        birthdaysBean.setRetailerName(firstBeanAL.getRetailerName());
                                        birthdaysBean.setMobileNo(firstBeanAL.getMobileNo());
                                        birthdaysBean.setAppointmentTime(firstBeanAL.getAppointmentTime());
                                        birthdaysBean.setAppointmentEndTime(firstBeanAL.getAppointmentEndTime());
                                        birthdaysBean.setAppointmentType(firstBeanAL.getAppointmentType());
                                        birthdaysBean.setAppointmentAlert(true);
                                        alRetBirthDayTempList.add(birthdaysBean);
                                    }
                                }

                            }


                        }

                    }


                    setCurrentDateTOSharedPerf(context);
                    // TODO add values into data vault
                    if (alRetBirthDayTempList != null && alRetBirthDayTempList.size() > 0) {
                        assignValuesIntoDataVault(alRetBirthDayTempList,context);
                    } else {
                        if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                            assignValuesIntoDataVault(alRetBirthDayList,context);
                        } else {
                            assignEmptyValuesIntoDataVault(context);
                        }
                    }

                } else {
                    setCurrentDateTOSharedPerf(context);
                    // TODO add values into data vault
                    if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                        assignValuesIntoDataVault(alRetBirthDayList,context);
                    } else {
                        assignEmptyValuesIntoDataVault(context);
                    }
                }


            } else {
                assignEmptyValuesIntoDataVault(context);
                setCurrentDateTOSharedPerf(context);
                // TODO add values into data vault
                if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                    assignValuesIntoDataVault(alRetBirthDayList,context);
                } else {
                    assignEmptyValuesIntoDataVault(context);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void displayAlertWithBackPressed(final Activity activity, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.MyTheme).create();
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    alertDialog.cancel();
                    activity.onBackPressed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialog.show();
    }

    public static void updateLastSyncTimeToTable(ArrayList<String> alAssignColl, Context context, String syncType,String refGuid) {
        try {
            /*String syncTime = Constants.getSyncHistoryddmmyyyyTime();
            for (int incReq = 0; incReq < alAssignColl.size(); incReq++) {
                String colName = alAssignColl.get(incReq);
                if (colName.contains("?$")) {
                    String splitCollName[] = colName.split("\\?");
                    colName = splitCollName[0];
                }

                Constants.events.updateStatus(Constants.SYNC_TABLE,
                        colName, Constants.TimeStamp, syncTime
                );
            }*/
            LogManager.writeLogInfo(" updating sync history : start");
            Constants.updateSyncTime(alAssignColl, context, syncType,refGuid);
        } catch (Exception exce) {
            LogManager.writeLogError(Constants.SyncTableHistory + exce.getMessage());
        }
    }

    // TODO make query for current month any Invoices is created or not
    public static String getCurrentMonthInvoiceQry(String cpUID) {
        return Constants.Invoices + "?$filter=" + Constants.SoldToID + " eq '" + cpUID + "' " +
                "and " + Constants.InvoiceDate + " ge datetime'" + Constants.getFirstDateOfCurrentMonth() + "'";
    }

    // TODO make query for current month any collection is created or not
    public static String getCurrentMonthCollHisQry(String CPGUID) {
        return Constants.FinancialPostings + "?$filter=" + Constants.CPGUID + " eq guid'"
                + Constants.convertStrGUID32to36(CPGUID).toUpperCase()
                + "' and " + Constants.FIPDate + " ge datetime'" + Constants.getFirstDateOfCurrentMonth() + "'";
    }

    public static void editTextDecimalFormat(final EditText editText, final int beforeDecimal, final int afterDecimal) {
        editText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                StringBuilder sbText = new StringBuilder(source);
                String text = sbText.toString();
                if (dstart == 0) {
                    if (text.contains("0")) {
                        return "";
                    } else if (text.contains(".")) {
                        return "0.";
                    } else if (text.contains("0..")) {
                        return "0.";
                    } else {
                        return source;
                    }
                }
                String etText = editText.getText().toString();
                if (etText.isEmpty()) {
                    return null;
                }
                String temp = editText.getText() + source.toString();

                if (temp.equals(".")) {
                    return "0.";
                }
                if (temp.contains("0..")) {
                    return "";
                } else if (temp.toString().indexOf(".") == -1) {
                    // no decimal point placed yet
                    if (temp.length() > beforeDecimal) {
                        return "";
                    }
                } else {
                    int dotPosition;
                    int cursorPositon = editText.getSelectionStart();
                    if (etText.indexOf(".") == -1) {
                        Log.i("First time Dot", etText.toString().indexOf(".") + " " + etText);
                        dotPosition = temp.indexOf(".");
                    } else {
                        dotPosition = etText.indexOf(".");
                    }
                    if (cursorPositon <= dotPosition) {
                        String beforeDot = etText.substring(0, dotPosition);
                        if (beforeDot.length() < beforeDecimal) {
                            return source;
                        } else {
                            if (source.toString().equalsIgnoreCase(".")) {
                                return source;
                            } else {
                                return "";
                            }

                        }
                    } else {
                        temp = temp.substring(temp.indexOf(".") + 1);
                        if (temp.length() > afterDecimal) {
                            return "";
                        } else if (etText.contains(source) && source.equals(".")) {
                            return "";
                        }
                    }
                }
                return null;


            }
        }});


    }

    //	public static void createVisit(String cpID, String cpName, String cpTypeId, ODataGuid cpGuid, Context context, String visitCatId,String statusID,UIListener listener)
    public static void createVisit(Map<String, String> parameterMap, String cpGuid, Context context, UIListener listener) {

        try {
            Thread.sleep(100);

            GUID guid = GUID.newRandom();

            Hashtable table = new Hashtable();
            //noinspection unchecked
            table.put(Constants.CPNo, UtilConstants.removeLeadingZeros(parameterMap.get(Constants.CPNo)));

            table.put(Constants.CPName, parameterMap.get(Constants.CPName));
            //noinspection unchecked
            table.put(Constants.STARTDATE, UtilConstants.getNewDateTimeFormat());

            final Calendar calCurrentTime = Calendar.getInstance();
            int hourOfDay = calCurrentTime.get(Calendar.HOUR_OF_DAY); // 24 hour clock
            int minute = calCurrentTime.get(Calendar.MINUTE);
            int second = calCurrentTime.get(Calendar.SECOND);
            ODataDuration oDataDuration = null;
            try {
                oDataDuration = new ODataDurationDefaultImpl();
                oDataDuration.setHours(hourOfDay);
                oDataDuration.setMinutes(minute);
                oDataDuration.setSeconds(BigDecimal.valueOf(second));
            } catch (Exception e) {
                e.printStackTrace();
            }

            table.put(Constants.STARTTIME, oDataDuration);
            //noinspection unchecked
            table.put(Constants.StartLat, BigDecimal.valueOf(UtilConstants.latitude));
            //noinspection unchecked
            table.put(Constants.StartLong, BigDecimal.valueOf(UtilConstants.longitude));
            //noinspection unchecked
            table.put(Constants.EndLat, "");
            //noinspection unchecked
            table.put(Constants.EndLong, "");
            //noinspection unchecked
            table.put(Constants.ENDDATE, "");
            //noinspection unchecked
            table.put(Constants.ENDTIME, "");
            //noinspection unchecked
            table.put(Constants.VISITKEY, guid.toString().toUpperCase());

            table.put(Constants.StatusID, parameterMap.get(Constants.StatusID));
            try {
                table.put(Constants.VisitCatID, parameterMap.get(Constants.VisitCatID));
            } catch (Exception e) {
                table.put(Constants.VisitCatID, "");
                e.printStackTrace();
            }

            //   table.put(Constants.VisitCatID, parameterMap.get(Constants.VisitCatID));

            table.put(Constants.CPTypeID, parameterMap.get(Constants.CPTypeID));

            if (parameterMap.get(Constants.PlannedDate) != null) {
                table.put(Constants.PlannedDate, parameterMap.get(Constants.PlannedDate));
            } else {
                table.put(Constants.PlannedDate, "");
            }

            if (parameterMap.get(Constants.PlannedStartTime) != null) {
                ODataDuration startDuration = getTimeAsODataDuration(parameterMap.get(Constants.PlannedStartTime));
                table.put(Constants.PlannedStartTime, startDuration);
            } else {
                table.put(Constants.PlannedStartTime, "");
            }

            if (parameterMap.get(Constants.PlannedEndTime) != null) {
                ODataDuration endDuration = getTimeAsODataDuration(parameterMap.get(Constants.PlannedEndTime));
                table.put(Constants.PlannedEndTime, endDuration);
            } else {
                table.put(Constants.PlannedEndTime, "");
            }

            //noinspection unchecked
            if (parameterMap.get(Constants.Remarks) != null) {
                table.put(Constants.Remarks, parameterMap.get(Constants.Remarks));
            }

            table.put(Constants.VisitTypeID, parameterMap.get(Constants.VisitTypeID));
            table.put(Constants.VisitTypeDesc, parameterMap.get(Constants.VisitTypeDesc));


            //  if (parameterMap.get(Constants.VisitDate) != null) {
            table.put(Constants.VisitDate, UtilConstants.getNewDateTimeFormat());
            /*} else {
                table.put(Constants.VisitDate, "");
            }*/

            table.put(Constants.CPGUID, cpGuid);

            table.put(Constants.SPGUID, Constants.getSPGUID(Constants.SPGUID));

            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);

            int sharedVal = sharedPreferences.getInt("VisitSeqId", 0);

            String loginIdVal = sharedPreferences.getString(Constants.username, "");
            //noinspection unchecked
            table.put(Constants.LOGINID, loginIdVal);

            table.put(Constants.VisitSeq, sharedVal + "");

            sharedVal++;

            SharedPreferences sharedPreferencesVal = context.getSharedPreferences(Constants.PREFS_NAME, 0);
            SharedPreferences.Editor editor = sharedPreferencesVal.edit();
            editor.putInt(Constants.VisitSeqId, sharedVal);
            editor.commit();


            String mStrRoutePlanKey = Constants.Route_Plan_Key;
            if (!mStrRoutePlanKey.equalsIgnoreCase("")) {
                String mStrRouteGuidFormat = CharBuffer.join9(StringFunction.substring(mStrRoutePlanKey, 0, 8), "-", StringFunction.substring(mStrRoutePlanKey, 8, 12), "-", StringFunction.substring(mStrRoutePlanKey, 12, 16), "-", StringFunction.substring(mStrRoutePlanKey, 16, 20), "-", StringFunction.substring(mStrRoutePlanKey, 20, 32));
                //noinspection unchecked
                table.put(Constants.ROUTEPLANKEY, mStrRouteGuidFormat.toUpperCase());
            } else {
//				String mStrRouteKey = getRouteNo(cpGuid);
                String mStrRouteKey = "";
                if (mStrRouteKey.equalsIgnoreCase("")) {
                    table.put(Constants.ROUTEPLANKEY, "");
                } else {
                    String mStrRouteGuidFormat = CharBuffer.join9(StringFunction.substring(mStrRouteKey, 0, 8), "-", StringFunction.substring(mStrRouteKey, 8, 12), "-", StringFunction.substring(mStrRouteKey, 12, 16), "-", StringFunction.substring(mStrRouteKey, 16, 20), "-", StringFunction.substring(mStrRouteKey, 20, 32));
                    table.put(Constants.ROUTEPLANKEY, mStrRouteGuidFormat.toUpperCase());
                }

            }


            try {
                //noinspection unchecked
                OfflineManager.createVisit(table, listener);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
        } catch (InterruptedException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }


    }

    private static String getRouteNo(ODataGuid mCpGuid) {

        String mStrRouteKey = "";
        String qryStr = Constants.RouteSchedulePlans + "?$filter=" + Constants.VisitCPGUID + " eq '" + mCpGuid.guidAsString32().toUpperCase() + "' ";
        try {
            mStrRouteKey = OfflineManager.getRoutePlanKeyNew(qryStr);

        } catch (OfflineODataStoreException e) {
            mStrRouteKey = "";
            e.printStackTrace();
        }
        return mStrRouteKey;
    }

    public static ODataDuration getTimeAsODataDuration(String timeString) {

        List<String> timeDuration = Arrays.asList(timeString.split("-"));
        int hour = Integer.parseInt(timeDuration.get(0));
        int minute = Integer.parseInt(timeDuration.get(1));
        int seconds = 00;
        ODataDuration oDataDuration = null;
        try {
            oDataDuration = new ODataDurationDefaultImpl();
            oDataDuration.setHours(hour);
            oDataDuration.setMinutes(minute);
            oDataDuration.setSeconds(BigDecimal.valueOf(seconds));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return oDataDuration;
    }

    public static File SaveImageInDevice(String filename, Bitmap bitmap) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;

        File file = new File(extStorageDirectory, filename + ".png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, filename + ".png");
        }
        try {
            // make a new bitmap from your file

            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("file", "" + file);
        return file;

    }

    public static void onVisitActivityUpdate(Context mContext, String mStrBundleCPGUID32,
                                             String visitActRefID, String vistActType, String visitActTypeDesc, ODataDuration mStartTimeDuration) {
        //========>Start VisitActivity
        try {
            Hashtable visitActivityTable = new Hashtable();
            String mStrSPGUID = Constants.getSPGUID(Constants.SPGUID);
            String getVisitGuidQry = Constants.Visits + "?$filter=EndDate eq null and CPGUID eq '" + mStrBundleCPGUID32.toUpperCase() + "' " +
                    "and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
            ODataGuid mGuidVisitId = null;
            try {
                mGuidVisitId = OfflineManager.getVisitDetails(getVisitGuidQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);

            String loginIdVal = sharedPreferences.getString(Constants.username, "");
            if (mGuidVisitId != null) {
                GUID mStrGuide = GUID.newRandom();
                visitActivityTable.put(Constants.VisitActivityGUID, mStrGuide.toString());
                visitActivityTable.put(Constants.LOGINID, loginIdVal);
                visitActivityTable.put(Constants.VisitGUID, mGuidVisitId.guidAsString36());
                visitActivityTable.put(Constants.ActivityType, vistActType);
                visitActivityTable.put(Constants.ActivityTypeDesc, visitActTypeDesc);
                visitActivityTable.put(Constants.ActivityRefID, visitActRefID);
                visitActivityTable.put(Constants.Latitude, BigDecimal.valueOf(UtilConstants.latitude));
                visitActivityTable.put(Constants.Longitude, BigDecimal.valueOf(UtilConstants.longitude));
                visitActivityTable.put(Constants.StartTime, mStartTimeDuration);
                visitActivityTable.put(Constants.EndTime, UtilConstants.getOdataDuration());

                try {
                    OfflineManager.createVisitActivity(visitActivityTable);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //========>End VisitActivity
    }

    public static String getLastThreeMonthDate() {
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -3);
        return simpleDateFormat.format(cal.getTime()) + "T00:00:00";
    }

    public static ODataDuration getOdataDuration() {
        final Calendar calCurrentTime = Calendar.getInstance();
        int hourOfDay = calCurrentTime.get(Calendar.HOUR_OF_DAY); // 24 hour clock
        int minute = calCurrentTime.get(Calendar.MINUTE);
        int second = calCurrentTime.get(Calendar.SECOND);
        ODataDuration oDataDuration = null;
        try {
            oDataDuration = new ODataDurationDefaultImpl();
            oDataDuration.setHours(hourOfDay);
            oDataDuration.setMinutes(minute);
            oDataDuration.setSeconds(BigDecimal.valueOf(second));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return oDataDuration;
    }

    public static String convertArrListToGsonString(ArrayList<HashMap<String, String>> arrtable) {
        String convertGsonString = "";
        Gson gson = new Gson();
        try {
            convertGsonString = gson.toJson(arrtable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertGsonString;
    }

    public static void saveDeviceDocNoToSharedPref(Context context, String createType, String refDocNo) {
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        set = sharedPreferences.getStringSet(createType, null);

        HashSet<String> setTemp = new HashSet<>();
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                setTemp.add(itr.next().toString());
            }
        }
        setTemp.add(refDocNo);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(createType, setTemp);
        editor.commit();
    }

    public static void removeDeviceDocNoFromSharedPref(Context context, String createType, String refDocNo) {
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        set = sharedPreferences.getStringSet(createType, null);

        HashSet<String> setTemp = new HashSet<>();
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                setTemp.add(itr.next().toString());
            }
        }
        setTemp.remove(refDocNo);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(createType, setTemp);
        editor.commit();
    }

    public static String[][] getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(String mStrCPGUID) {
        String spGuid = "";
        try {
            spGuid = OfflineManager.getGuidValueByColumnName(Constants.SalesPersons + "?$select=" + Constants.SPGUID + " ", Constants.SPGUID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        String selCPDMSDIV = "";
        try {
            selCPDMSDIV = OfflineManager.getValueByColumnName(Constants.CPDMSDivisions + "?$select=" + Constants.DMSDivision + " &$filter="
                    + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' and " + Constants.PartnerMgrGUID + " eq guid'" + spGuid.toUpperCase() + "' ", Constants.DMSDivision);


        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        String[][] mArraySPValues = null;
        String qryStr = Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' and "
                + Constants.DMSDivision + " eq '" + selCPDMSDIV + "' and " + Constants.PartnerMgrGUID + " eq guid'" + spGuid.toUpperCase() + "'";
        try {
            mArraySPValues = OfflineManager.getSPValuesByCPGUIDAndDMSDivision(qryStr);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }


        if (mArraySPValues == null) {
            mArraySPValues = new String[12][1];
            mArraySPValues[0][0] = "";
            mArraySPValues[1][0] = "";
            mArraySPValues[2][0] = "";
            mArraySPValues[3][0] = "";
            mArraySPValues[4][0] = "";
            mArraySPValues[5][0] = "";
            mArraySPValues[6][0] = "";
            mArraySPValues[7][0] = "";
            mArraySPValues[8][0] = "";
            mArraySPValues[9][0] = "";
            mArraySPValues[10][0] = "";
            mArraySPValues[11][0] = "";
        } else {
            try {
                if (mArraySPValues[4][0] != null) {

                }
            } catch (Exception e) {
                mArraySPValues = new String[12][1];
                mArraySPValues[0][0] = "";
                mArraySPValues[1][0] = "";
                mArraySPValues[2][0] = "";
                mArraySPValues[3][0] = "";
                mArraySPValues[4][0] = "";
                mArraySPValues[5][0] = "";
                mArraySPValues[6][0] = "";
                mArraySPValues[7][0] = "";
                mArraySPValues[8][0] = "";
                mArraySPValues[9][0] = "";
                mArraySPValues[10][0] = "";
                mArraySPValues[11][0] = "";
            }
        }

        return mArraySPValues;
    }

    public static String[][] getDistributors() {
        String[][] mArrayDistributors = null;
//        String qryStr = Constants.SalesPersons + "?$filter=(" + Constants.CPGUID + " ne '' and " + Constants.CPGUID + " ne null) &$apply=groupby((" + Constants.CPGUID + "))";
        String qryStr = Constants.SalesPersons;
        try {
            mArrayDistributors = OfflineManager.getDistributorList(qryStr);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if (mArrayDistributors == null) {
            mArrayDistributors = new String[10][1];
            mArrayDistributors[0][0] = "";
            mArrayDistributors[1][0] = "";
            mArrayDistributors[2][0] = "";
            mArrayDistributors[3][0] = "";
            mArrayDistributors[4][0] = "";
            mArrayDistributors[5][0] = "";
            mArrayDistributors[6][0] = "";
            mArrayDistributors[7][0] = "";
            mArrayDistributors[8][0] = "";
            mArrayDistributors[9][0] = "";
        }

        return mArrayDistributors;
    }

    public static String[][] getDistributorsByCPGUID(String mStrCPGUID) {
        String[][] mArrayDistributors = null;
        String qryStr = Constants.ChannelPartners + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' ";
        try {
            mArrayDistributors = OfflineManager.getDistributorListByCPGUID(qryStr);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if (mArrayDistributors == null) {
            mArrayDistributors = new String[11][1];
            mArrayDistributors[0][0] = "";
            mArrayDistributors[1][0] = "";
            mArrayDistributors[2][0] = "";
            mArrayDistributors[3][0] = "";
            mArrayDistributors[4][0] = "";
            mArrayDistributors[5][0] = "";
            mArrayDistributors[6][0] = "";
            mArrayDistributors[7][0] = "";
            mArrayDistributors[8][0] = "";
            mArrayDistributors[9][0] = "";
            mArrayDistributors[10][0] = "";
        }

        return mArrayDistributors;
    }

    public static String[][] getDMSDivisionByCPGUID(String mStrCPGUID) {
        String[][] mArrayCPDMSDivisions = null;
        String qryStr = Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' ";
        try {
            mArrayCPDMSDivisions = OfflineManager.getDMSDivisionByCPGUID(qryStr);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if (mArrayCPDMSDivisions == null) {
            mArrayCPDMSDivisions = new String[2][1];
            mArrayCPDMSDivisions[0][0] = "";
            mArrayCPDMSDivisions[1][0] = "";
        }

        return mArrayCPDMSDivisions;
    }

    public static String getLastMonthDate() {
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        return simpleDateFormat.format(cal.getTime()) + "T00:00:00";
    }

    public static String getConcatinatinFlushCollectios(ArrayList<String> alFlushColl) {
        String concatFlushCollStr = "";
        for (int incVal = 0; incVal < alFlushColl.size(); incVal++) {
            if (incVal == 0 && incVal == alFlushColl.size() - 1) {
                concatFlushCollStr = concatFlushCollStr + alFlushColl.get(incVal);
            } else if (incVal == 0) {
                concatFlushCollStr = concatFlushCollStr + alFlushColl.get(incVal) + ", ";
            } else if (incVal == alFlushColl.size() - 1) {
                concatFlushCollStr = concatFlushCollStr + alFlushColl.get(incVal);
            } else {
                concatFlushCollStr = concatFlushCollStr + alFlushColl.get(incVal) + ", ";
            }
        }

        return concatFlushCollStr;
    }

    public static ArrayList<String> getPendingList() {
        ArrayList<String> alFlushColl = new ArrayList<>();
        try {
            if (OfflineManager.getVisitStatusForCustomer(Constants.Attendances + Constants.isLocalFilterQry)) {
                alFlushColl.add(Constants.Attendances);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.Visits + Constants.isLocalFilterQry)) {
                alFlushColl.add(Constants.Visits);
            }

            if (OfflineManager.getVisitStatusForCustomer(Constants.VisitActivities + Constants.isLocalFilterQry)) {
                alFlushColl.add(Constants.VisitActivities);
            }

            if (OfflineManager.getVisitStatusForCustomer(Constants.MerchReviews + Constants.isLocalFilterQry)) {
                alFlushColl.add(Constants.MerchReviews);
            }

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

        return alFlushColl;
    }

    public static ArrayList<String> getRefreshList() {
        ArrayList<String> alAssignColl = new ArrayList<>();
        try {
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            if (OfflineManager.getVisitStatusForCustomer(Constants.Attendances + Constants.isLocalFilterQry)) {
                alAssignColl.add(Constants.Attendances);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.Visits + Constants.isLocalFilterQry)) {
                alAssignColl.add(Constants.Visits);
            }

            if (OfflineManager.getVisitStatusForCustomer(Constants.VisitActivities + Constants.isLocalFilterQry)) {
                alAssignColl.add(Constants.VisitActivities);
            }

            if (OfflineManager.getVisitStatusForCustomer(Constants.MerchReviews + Constants.isLocalFilterQry)) {
                alAssignColl.add(Constants.MerchReviews);
                alAssignColl.add(Constants.MerchReviewImages);
            }

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

        return alAssignColl;
    }

    public static boolean isSpecificCollTodaySyncOrNot(String sleDate) {


        boolean mBoolDBSynced = false;
        if (sleDate != null && !sleDate.equalsIgnoreCase("")) {

            Date date = null;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                date = null;
                try {
                    date = sdf.parse(sleDate);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int mYear = cal.get(Calendar.YEAR);
            int mMonth = cal.get(Calendar.MONTH);
            int mDay = cal.get(Calendar.DAY_OF_MONTH);

            Calendar calCurrent = Calendar.getInstance();

            int mYearCurrent = calCurrent.get(Calendar.YEAR);
            int mMonthCurrent = calCurrent.get(Calendar.MONTH);
            int mDayCurrent = calCurrent.get(Calendar.DAY_OF_MONTH);

            if (mYear == mYearCurrent && mMonth == mMonthCurrent && mDay == mDayCurrent) {
                mBoolDBSynced = true;
            } else {
                mBoolDBSynced = false;
            }

        } else {
            mBoolDBSynced = false;
        }
        return mBoolDBSynced;
    }

    public static String getLastSyncDate(String collName, String whereCol, String whereColVal, String retiveColName, Context context) {
        String lastSyncTime = "";
        Cursor cursorLastSync = SyncHist.getInstance()
                .getLastSyncTime(collName, whereCol, whereColVal);

        if (cursorLastSync != null
                && cursorLastSync.getCount() > 0) {
            while (cursorLastSync.moveToNext()) {
                lastSyncTime = cursorLastSync
                        .getString(cursorLastSync
                                .getColumnIndex(retiveColName)) != null ? cursorLastSync
                        .getString(cursorLastSync
                                .getColumnIndex(retiveColName)) : "";
            }
        }
        return lastSyncTime;
    }

    public static String getCurrentMonth() {
        String mStrCurrMonth = "";

        Calendar cal = Calendar.getInstance();
        int intFromMnt = cal.get(Calendar.MONTH) + 1;
        if (intFromMnt < 10)
            mStrCurrMonth = "0" + intFromMnt;
        else
            mStrCurrMonth = "" + intFromMnt;

        return mStrCurrMonth;
    }

    public static String getCurrentYear() {
        String mStrCurrentYear = "";

        Calendar cal = Calendar.getInstance();
        mStrCurrentYear = cal.get(Calendar.YEAR) + "";

        return mStrCurrentYear;
    }

    public static void storeInDataVault(String docNo, String jsonHeaderObjectAsString,Context context) {
        try {
            ConstantsUtils.storeInDataVault(docNo, jsonHeaderObjectAsString,context);
        } catch (Throwable var3) {
            var3.printStackTrace();
        }

    }

    public static ArrayList<String> getPendingMerchList(Context context, String createType) {
        ArrayList<String> devMerList = new ArrayList<>();
        try {
            Set<String> set = new HashSet<>();
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
            set = sharedPreferences.getStringSet(createType, null);
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
                while (itr.hasNext()) {
                    devMerList.add(itr.next().toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return devMerList;
    }

    public static String getValueFromDataVault(String key,Context context) {
        String store = null;
        try {
            store = ConstantsUtils.getFromDataVault(key,context);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return store;
    }

    public static void deleteDeviceMerchansisingFromDataVault(Context context) {
        ArrayList<String> alDeviceMerList = Constants.getPendingMerchList(context, Constants.MerchList);
        if (alDeviceMerList != null && alDeviceMerList.size() > 0) {
            for (int incVal = 0; incVal < alDeviceMerList.size(); incVal++) {
                try {
                    if (!OfflineManager.getVisitStatusForCustomer(Constants.MerchReviews +
                            "?$filter=sap.islocal() and " + Constants.MerchReviewGUID + " eq guid'" + alDeviceMerList.get(incVal) + "'")) {
                        Constants.removeDeviceDocNoFromSharedPref(context, Constants.MerchList, alDeviceMerList.get(incVal));
                        storeInDataVault(alDeviceMerList.get(incVal), "",context);
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /*returns total number of retailers has to visit today(Route plan)*/
    public static ArrayList<CustomerBean> getTodaysBeatRetailers() {
        String routeQry = Constants.RoutePlans + "?$filter=" + Constants.VisitDate + " eq datetime'" + UtilConstants.getNewDate() + "'";
        String count = "0";
        ArrayList<CustomerBean> alRetailerList = new ArrayList<>();
        try {
            alRetailerList = OfflineManager.getRetailerListForRoute(routeQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return alRetailerList;
    }

    /*returns total number of retailers has to visit today(Route plan)*/
    public static String getVisitTargetForToday(Context mContext) {
        alTodayBeatCustomers.clear();
        alCustomers.clear();
        SO_Cust_QRY = "";
        String count = "0";
        String spGuid = Constants.getSPGUID(Constants.SPGUID);
        ArrayList<MTPRoutePlanBean> alRetailerList = new ArrayList<>();
        try {
            String startDate = SOUtils.getStartDate(mContext, mContext.getString(R.string.so_filter_today));
            String endDate = SOUtils.getEndDate(mContext, mContext.getString(R.string.so_filter_today));
//            String qry = Constants.RoutePlans+"?$select=CustomerNo,CustomerName,VisitDate &$filter=VisitDate ge datetime'" + startDate + "' and VisitDate le datetime'" + endDate + "'";
            String qry = Constants.RouteSchedules + "?$filter=ValidTo ge datetime'" + startDate + "' and ValidFrom le datetime'" + endDate + "' and SalesPersonID eq guid'" + spGuid + "' and ApprovalStatus eq '03'";
            alRetailerList = OfflineManager.getMTPTodayPlane(qry, OfflineManager.isASMUser());
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
//        alRetailerList = getTodaysBeatCustomers();
        alTodayBeatCustomers.addAll(alRetailerList);
        count = (alRetailerList.size() > 0) ? String.valueOf(alRetailerList.size()) : "0";
        return count;
    }

    public static ArrayList<CustomerBean> getTodaysBeatCustomers() {
        ArrayList<CustomerBean> alRetailerList = new ArrayList<>();

        // Currently customers getting from RoutePlans table.
        ArrayList<CustomerBean> alRSCHList = getTodayRoutePlanCustomers();
        if (alRSCHList != null && alRSCHList.size() > 0) {
            alRetailerList.addAll(alRSCHList);
        }

        // Below logic is required in future
//        ArrayList<CustomerBean> alRSCHList = getTodayRoutePlan();
//        if(alRSCHList!=null && alRSCHList.size()>0) {
//            String mCPGuidQry = getCustomerFromRouteSchPlan(alRSCHList);
//            try {
//                if (!mCPGuidQry.equalsIgnoreCase("")) {
//                    List<CustomerBean> listRetailers = OfflineManager.getTodayBeatCustomers(mCPGuidQry);
//                    alRetailerList = (ArrayList<CustomerBean>) listRetailers;
//                }
//            } catch (OfflineODataStoreException e) {
//                e.printStackTrace();
//            }
//        }
        return alRetailerList;

    }

    /*returns total number of retailers visited(Route plan)*/
    public static String getVisitedRetailerCount() {
        String mTodayBeatVisitCount = "0";
        String mStrSPGUID = Constants.getSPGUID(Constants.SPGUID);
        String mVisitQry = Constants.Visits + "?$filter= " + Constants.StartDate + " eq datetime'" + UtilConstants.getNewDate() + "'" +
                " and " + Constants.ENDDATE + " eq datetime'" + UtilConstants.getNewDate() + "' " + "and ("
                + Constants.VisitCatID + " eq '" + Constants.str_01 + "' ) and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
        Set<String> retList = new HashSet<>();
        try {
            retList = OfflineManager.getUniqueOutVisitFromVisit(mVisitQry);
            mTodayBeatVisitCount = retList.size() + "";
        } catch (Exception e) {
            mTodayBeatVisitCount = "0";
        }

        return mTodayBeatVisitCount;
    }

    public static ArrayList<String> getTodayVisitedCustomers() {
        String mTodayBeatVisitCount = "0";
        String mStrSPGUID = Constants.getSPGUID(Constants.SPGUID);
        String mVisitQry = Constants.Visits + "?$filter= " + Constants.StartDate + " eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
        //" and " + Constants.ENDDATE + " eq datetime'" + UtilConstants.getNewDate() + "' ";
        ArrayList<String> retList = new ArrayList<>();
        try {
            retList = OfflineManager.getUniqueOutVisitFromVisitTemp(mVisitQry);
            mTodayBeatVisitCount = retList.size() + "";
        } catch (Exception e) {
            mTodayBeatVisitCount = "0";
        }

        return retList;
    }

    public static String convert24hrFormatTo12hrFormat(String time) {

        DateFormat f1 = new SimpleDateFormat("HH:mm"); //HH for hour of the day (0 - 23)
        Date d;
        try {
            d = f1.parse(time);
            DateFormat f2 = new SimpleDateFormat("h:mm a");
            return f2.format(d).toLowerCase();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getGUIDEditResourcePath(String collection, String key) {
        return new String(collection + "(guid'" + key + "')");
    }

    /**
     * SHOW PROGRESS DIALOG
     *
     * @param context
     * @param title
     * @param message
     * @return
     */
    public static ProgressDialog showProgressDialog(Context context, String title, String message) {
        ProgressDialog progressDialog = null;
        try {
            progressDialog = new ProgressDialog(context, R.style.ProgressDialogTheme);
            progressDialog.setMessage(message);
            progressDialog.setTitle(title);
            progressDialog.setCancelable(false);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return progressDialog;
    }

    /**
     * HIDE PROGRESS DIALOG
     *
     * @param progressDialog
     */
    public static void hideProgressDialog(ProgressDialog progressDialog) {
        try {
            if (progressDialog != null)
                progressDialog.hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static void dialogSingleButton(Context context, String message, final DialogCallBack dialogCallBack) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);

        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(
                        R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog,
                                    int id) {
                                dialog.cancel();
                                if (dialogCallBack != null)
                                    dialogCallBack.clickedStatus(true);
                            }
                        });

        builder.show();
    }

    public static String getNewDateTimeFormat() {
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String currentDateTimeString = (String) android.text.format.DateFormat
                .format("yyyy-MM-dd'T'HH:mm:ss", new Date());
//		String currentDateTimeString = currentDateTimeString1;
        return currentDateTimeString;
    }

    public static Calendar convertDateFormat(String dateVal) {
        Date date = null;
//		String dtStart = "2016-04-12T05:30:00";


        Calendar curCal = new GregorianCalendar();

//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = format.parse(dateVal);
            curCal.setTime(date);
//            curCal.add(Calendar.HOUR, 5);
//            curCal.add(Calendar.MINUTE, 30);
            System.out.println("Date" + curCal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return curCal;
    }

    public static String convertDateIntoYYYYMMDD(String dateString) {
        String stringDateReturns = "";
        Date date = null;
        try {
            date = (new SimpleDateFormat("dd/MM/yyyy")).parse(dateString);
            stringDateReturns = (new SimpleDateFormat("yyyy-MM-dd")).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return stringDateReturns;
    }

    public static String getEditResourcePath(String collection, String key) {
        return new String(collection + "('" + key + "')");
    }

    public static JSONObject getSOsHeaderValueFrmJsonObject1(JSONObject fetchJsonHeaderObject) {
        JSONObject dbHeadTable = new JSONObject();
        try {
            if(fetchJsonHeaderObject.has(Constants.SONo)) {
                dbHeadTable.put(Constants.SONo, fetchJsonHeaderObject.getString(Constants.SONo));
//                REPEATABLE_REQUEST_ID = fetchJsonHeaderObject.getString(Constants.SONo);
            }
            if(fetchJsonHeaderObject.has(Constants.OrderType)) {
                dbHeadTable.put(Constants.OrderType, fetchJsonHeaderObject.getString(Constants.OrderType));
            }
            if(fetchJsonHeaderObject.has(Constants.OrderDate)) {
                dbHeadTable.put(Constants.OrderDate, fetchJsonHeaderObject.getString(Constants.OrderDate));
            }
            if(fetchJsonHeaderObject.has(Constants.CustomerNo)) {
                dbHeadTable.put(Constants.CustomerNo, fetchJsonHeaderObject.getString(Constants.CustomerNo));
            }
            if(fetchJsonHeaderObject.has(Constants.CustomerPO) && !TextUtils.isEmpty(fetchJsonHeaderObject.getString(Constants.CustomerPO))) {
                dbHeadTable.put(Constants.CustomerPO, fetchJsonHeaderObject.getString(Constants.CustomerPO));
            }
            if(fetchJsonHeaderObject.has(Constants.CustomerPODate) && !TextUtils.isEmpty(fetchJsonHeaderObject.getString(Constants.CustomerPODate))) {
                dbHeadTable.put(Constants.CustomerPODate, fetchJsonHeaderObject.getString(Constants.CustomerPODate));
            }
            if(fetchJsonHeaderObject.has(Constants.ShippingTypeID)) {
                dbHeadTable.put(Constants.ShippingTypeID, fetchJsonHeaderObject.getString(Constants.ShippingTypeID));
            }
//            if(fetchJsonHeaderObject.has(Constants.MeansOfTranstyp)) {
            //dbHeadTable.put(Constants.MeansOfTranstyp, fetchJsonHeaderObject.getString(Constants.MeansOfTranstyp));
//            }
            if(fetchJsonHeaderObject.has(Constants.ShipToParty)) {
                dbHeadTable.put(Constants.ShipToParty, fetchJsonHeaderObject.getString(Constants.ShipToParty));
            }
            if(fetchJsonHeaderObject.has(Constants.SalesArea)) {
                dbHeadTable.put(Constants.SalesArea, fetchJsonHeaderObject.getString(Constants.SalesArea));
            }
            if(fetchJsonHeaderObject.has(Constants.SalesOffice)) {
                dbHeadTable.put(Constants.SalesOffice, fetchJsonHeaderObject.getString(Constants.SalesOffice));
            }
            if(fetchJsonHeaderObject.has(Constants.SalesGroup)) {
                dbHeadTable.put(Constants.SalesGroup, fetchJsonHeaderObject.getString(Constants.SalesGroup));
            }
            if(fetchJsonHeaderObject.has(Constants.Plant)) {
                dbHeadTable.put(Constants.Plant, fetchJsonHeaderObject.getString(Constants.Plant));
            }
            try {
                if(fetchJsonHeaderObject.has(Constants.PlantDesc)) {
                    dbHeadTable.put(Constants.PlantDesc, fetchJsonHeaderObject.getString(Constants.PlantDesc) != null ? fetchJsonHeaderObject.getString(Constants.PlantDesc) : "");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(fetchJsonHeaderObject.has(Constants.Incoterm1)) {
                dbHeadTable.put(Constants.Incoterm1, fetchJsonHeaderObject.optString(Constants.Incoterm1));
            }
            if(fetchJsonHeaderObject.has(Constants.Incoterm1Desc)) {
                dbHeadTable.put(Constants.Incoterm1Desc, fetchJsonHeaderObject.optString(Constants.Incoterm1Desc) != null ? fetchJsonHeaderObject.optString(Constants.Incoterm1Desc) : "");
            }
            if(fetchJsonHeaderObject.has(Constants.Incoterm2)) {
                dbHeadTable.put(Constants.Incoterm2, fetchJsonHeaderObject.optString(Constants.Incoterm2));
            }
            if(fetchJsonHeaderObject.has(Constants.Payterm)) {
                dbHeadTable.put(Constants.Payterm, fetchJsonHeaderObject.optString(Constants.Payterm));
            }
            if(fetchJsonHeaderObject.has(Constants.PaytermDesc)) {
                dbHeadTable.put(Constants.PaytermDesc, fetchJsonHeaderObject.optString(Constants.PaytermDesc) != null ? fetchJsonHeaderObject.optString(Constants.PaytermDesc) : "");
            }
            if(fetchJsonHeaderObject.has(Constants.Currency)) {
                dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.optString(Constants.Currency));
            }
            if(fetchJsonHeaderObject.has(Constants.NetPrice)) {
                dbHeadTable.put(Constants.NetPrice, fetchJsonHeaderObject.optString(Constants.NetPrice));
            }
            if(fetchJsonHeaderObject.has(Constants.TotalAmount)) {
                dbHeadTable.put(Constants.TotalAmount, fetchJsonHeaderObject.optString(Constants.TotalAmount));
            }
            if(fetchJsonHeaderObject.has(Constants.TaxAmount)) {
                dbHeadTable.put(Constants.TaxAmount, fetchJsonHeaderObject.optString(Constants.TaxAmount));
            }
            if(fetchJsonHeaderObject.has(Constants.Freight)) {
                dbHeadTable.put(Constants.Freight, fetchJsonHeaderObject.optString(Constants.Freight));
            }
            if(fetchJsonHeaderObject.has(Constants.Discount)) {
                dbHeadTable.put(Constants.Discount, fetchJsonHeaderObject.optString(Constants.Discount));
            }
            if(fetchJsonHeaderObject.has(Constants.Remarks)) {
                dbHeadTable.put(Constants.Remarks, fetchJsonHeaderObject.optString(Constants.Remarks));
            }
            if(fetchJsonHeaderObject.has(Constants.Testrun)) {
                dbHeadTable.put(Constants.Testrun, fetchJsonHeaderObject.optString(Constants.Testrun));
            }
            if(fetchJsonHeaderObject.has(Constants.ReferenceNo)) {
                dbHeadTable.put(Constants.ReferenceNo, fetchJsonHeaderObject.optString(Constants.ReferenceNo));
                try {
                    REPEATABLE_REQUEST_ID = fetchJsonHeaderObject.getString(Constants.ReferenceNo).replaceAll("-","");
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            String createdOn = "";
            try {
                if(fetchJsonHeaderObject.has(Constants.CreatedOn)) {
                    createdOn=fetchJsonHeaderObject.optString(Constants.CreatedOn);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String createdAt = "";
            try {
                if(fetchJsonHeaderObject.has(Constants.CreatedAt)) {
                    createdAt=fetchJsonHeaderObject.optString(Constants.CreatedAt);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            REPEATABLE_DATE = UtilConstants.getReArrangeDateFormat(createdOn) + Constants.T + UtilConstants.convertTimeOnly(createdAt);

            JSONArray itemsArray = new JSONArray(fetchJsonHeaderObject.getString(Constants.SalesOrderItems));
            JSONArray jsonArray = new JSONArray();
            for (int incrementVal = 0; incrementVal < itemsArray.length(); incrementVal++) {
                JSONObject singleRow = itemsArray.getJSONObject(incrementVal);

                JSONObject itemObject = new JSONObject();
                if (singleRow.has(Constants.SONo)) {
                    itemObject.put(Constants.SONo, singleRow.get(Constants.SONo));
                }
                if (singleRow.has(Constants.ItemNo)) {
                    itemObject.put(Constants.ItemNo, singleRow.get(Constants.ItemNo));
                }
//                if (singleRow.has(Constants.MaterialGroup)) {
//                    itemObject.put(Constants.MaterialGroup, singleRow.get(Constants.MaterialGroup));
//                }
                if (singleRow.has(Constants.Material)) {
                    itemObject.put(Constants.Material, singleRow.get(Constants.Material));
                }
                if (singleRow.has(Constants.HighLevellItemNo)) {
                    itemObject.put(Constants.HighLevellItemNo, singleRow.get(Constants.HighLevellItemNo));
                }
                if (singleRow.has(Constants.ItemFlag)) {
                    itemObject.put(Constants.ItemFlag, singleRow.get(Constants.ItemFlag));
                }
                if (singleRow.has(Constants.ItemCategory)) {
                    itemObject.put(Constants.ItemCategory, singleRow.get(Constants.ItemCategory));
                }
//                if (singleRow.has(Constants.MaterialDesc)) {
//                    itemObject.put(Constants.MaterialDesc, singleRow.get(Constants.MaterialDesc));
//                }
                if (singleRow.has(Constants.Plant)) {
                    itemObject.put(Constants.Plant, singleRow.get(Constants.Plant));
                }
//                if (singleRow.has(Constants.StorLoc)) {
//                    itemObject.put(Constants.StorLoc, singleRow.get(Constants.StorLoc));
//                }
                if (singleRow.has(Constants.UOM)) {
                    itemObject.put(Constants.UOM, singleRow.get(Constants.UOM));
                }
                if (singleRow.has(Constants.Quantity)) {
                    itemObject.put(Constants.Quantity, singleRow.get(Constants.Quantity));
                }
                if (singleRow.has(Constants.Currency)) {
                    itemObject.put(Constants.Currency, singleRow.get(Constants.Currency));
                }
                if (singleRow.has(Constants.UnitPrice)) {
                    itemObject.put(Constants.UnitPrice, singleRow.get(Constants.UnitPrice));
                }
                if (singleRow.has(Constants.NetAmount)) {
                    itemObject.put(Constants.NetAmount, singleRow.get(Constants.NetAmount));
                }
//                if (singleRow.has(Constants.AlternateWeight)) {
//                    itemObject.put(Constants.AlternateWeight, singleRow.get(Constants.AlternateWeight));
//                }
                if (singleRow.has(Constants.GrossAmount)) {
                    itemObject.put(Constants.GrossAmount, singleRow.get(Constants.GrossAmount));
                }
                if (singleRow.has(Constants.Freight)) {
                    itemObject.put(Constants.Freight, singleRow.get(Constants.Freight));
                }
                if (singleRow.has(Constants.Tax)) {
                    itemObject.put(Constants.Tax, singleRow.get(Constants.Tax));
                }
                if (singleRow.has(Constants.Discount)) {
                    itemObject.put(Constants.Discount, singleRow.get(Constants.Discount));
                }
                if (singleRow.has(Constants.RejReason)) {
                    itemObject.put(Constants.RejReason, singleRow.get(Constants.RejReason));
                }
                if (singleRow.has(Constants.RejReasonDesc)) {
                    itemObject.put(Constants.RejReasonDesc, singleRow.get(Constants.RejReasonDesc));
                }

                JSONArray jsonArraySub = new JSONArray();
                String itemsString = singleRow.get("item_" + singleRow.get(Constants.Material)).toString();
                if (!TextUtils.isEmpty(itemsString)) {
                    ArrayList<HashMap<String, String>> subItemList = UtilConstants.convertToArrayListMap(itemsString);
                    for (int j = 0; j < subItemList.size(); j++) {
                        JSONObject jsonObject = new JSONObject();
                        HashMap<String, String> subSingleItem = subItemList.get(j);
                        if (singleRow.get(Constants.SONo) != null) {
                            jsonObject.put(Constants.SONo, singleRow.get(Constants.SONo));
                        }
                        jsonObject.put(Constants.DelSchLineNo, subSingleItem.get(Constants.DelSchLineNo));
                        jsonObject.put(Constants.ItemNo, subSingleItem.get(Constants.ItemNo));
                        jsonObject.put(Constants.DeliveryDate, UtilConstants.convertDateFormat(subSingleItem.get(Constants.DeliveryDate)));
                        jsonObject.put(Constants.MaterialNo, subSingleItem.get(Constants.MaterialNo));
                        jsonObject.put(Constants.OrderQty, BigDecimal.valueOf(Double.parseDouble(subSingleItem.get(Constants.OrderQty))));

                        jsonObject.put(Constants.ConfirmedQty, (subSingleItem.get(Constants.ConfirmedQty)));

                        jsonObject.put(Constants.RequiredQty,subSingleItem.get(Constants.RequiredQty));

                        jsonObject.put(Constants.UOM, subSingleItem.get(Constants.UOM));
                        jsonObject.put(Constants.SONo, subSingleItem.get(Constants.SONo));
                        if (subSingleItem.get(Constants.RequirementDate) != null) {
                            jsonObject.put(Constants.RequirementDate, subSingleItem.get(Constants.RequirementDate));
                        }
                        if (subSingleItem.get(Constants.TransportationPlanDate) != null) {
                            jsonObject.put(Constants.TransportationPlanDate, subSingleItem.get(Constants.TransportationPlanDate));
                        }
                        if (subSingleItem.get(Constants.MaterialAvailDate) != null) {
                            jsonObject.put(Constants.MaterialAvailDate, subSingleItem.get(Constants.MaterialAvailDate));
                        }
                        jsonArraySub.put(jsonObject);
                    }
                    itemObject.put(Constants.SOItemSchedules,jsonArraySub);
                }

                jsonArray.put(itemObject);
            }
            dbHeadTable.put(SOItemDetails, jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static Hashtable getSOsHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        Hashtable dbHeadTable = new Hashtable();
        try {


            dbHeadTable.put(Constants.SONo, fetchJsonHeaderObject.getString(Constants.SONo));
            dbHeadTable.put(Constants.OrderType, fetchJsonHeaderObject.getString(Constants.OrderType));
            dbHeadTable.put(Constants.OrderDate, fetchJsonHeaderObject.getString(Constants.OrderDate));
            dbHeadTable.put(Constants.CustomerNo, fetchJsonHeaderObject.getString(Constants.CustomerNo));
            dbHeadTable.put(Constants.CustomerPO, fetchJsonHeaderObject.getString(Constants.CustomerPO));
            dbHeadTable.put(Constants.CustomerPODate, fetchJsonHeaderObject.getString(Constants.CustomerPODate));
            dbHeadTable.put(Constants.ShippingTypeID, fetchJsonHeaderObject.getString(Constants.ShippingTypeID));
            //dbHeadTable.put(Constants.MeansOfTranstyp, fetchJsonHeaderObject.getString(Constants.MeansOfTranstyp));
            dbHeadTable.put(Constants.ShipToParty, fetchJsonHeaderObject.getString(Constants.ShipToParty));
            dbHeadTable.put(Constants.SalesArea, fetchJsonHeaderObject.getString(Constants.SalesArea));
            dbHeadTable.put(Constants.SalesOffice, fetchJsonHeaderObject.getString(Constants.SalesOffice));
            dbHeadTable.put(Constants.SalesGroup, fetchJsonHeaderObject.getString(Constants.SalesGroup));
            dbHeadTable.put(Constants.Plant, fetchJsonHeaderObject.getString(Constants.Plant));
            try {
                dbHeadTable.put(Constants.PlantDesc, fetchJsonHeaderObject.getString(Constants.PlantDesc) != null ? fetchJsonHeaderObject.getString(Constants.PlantDesc) : "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dbHeadTable.put(Constants.Incoterm1, fetchJsonHeaderObject.optString(Constants.Incoterm1));
            dbHeadTable.put(Constants.Incoterm1Desc, fetchJsonHeaderObject.optString(Constants.Incoterm1Desc) != null ? fetchJsonHeaderObject.optString(Constants.Incoterm1Desc) : "");

            dbHeadTable.put(Constants.Incoterm2, fetchJsonHeaderObject.optString(Constants.Incoterm2));
            dbHeadTable.put(Constants.Payterm, fetchJsonHeaderObject.optString(Constants.Payterm));
            dbHeadTable.put(Constants.PaytermDesc, fetchJsonHeaderObject.optString(Constants.PaytermDesc) != null ? fetchJsonHeaderObject.optString(Constants.PaytermDesc) : "");

            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.optString(Constants.Currency));
            dbHeadTable.put(Constants.NetPrice, fetchJsonHeaderObject.optString(Constants.NetPrice));
            dbHeadTable.put(Constants.TotalAmount, fetchJsonHeaderObject.optString(Constants.TotalAmount));
            dbHeadTable.put(Constants.TaxAmount, fetchJsonHeaderObject.optString(Constants.TaxAmount));
            dbHeadTable.put(Constants.Freight, fetchJsonHeaderObject.optString(Constants.Freight));
            dbHeadTable.put(Constants.Discount, fetchJsonHeaderObject.optString(Constants.Discount));
            dbHeadTable.put(Constants.Remarks, fetchJsonHeaderObject.optString(Constants.Remarks));
            dbHeadTable.put(Constants.Testrun, fetchJsonHeaderObject.optString(Constants.Testrun));
            dbHeadTable.put(Constants.ReferenceNo, fetchJsonHeaderObject.optString(Constants.ReferenceNo));
            try {
                dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.optString(Constants.CreatedOn));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                dbHeadTable.put(Constants.CreatedAt, fetchJsonHeaderObject.optString(Constants.CreatedAt));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static String getItemNoInSixCharsWithPrefixZeros(String itemNo) {
        String updatedString = new String(itemNo);
        if (itemNo != null) {
            int prefixLength = 6 - itemNo.length();
            if (itemNo.length() < 6) {
                for (int i = 0; i < prefixLength; i++) {
                    updatedString = "0" + updatedString;
                }
            }
        } else
            updatedString = "000000";
        return updatedString;
    }

    /**
     * open camera
     */
    public static void openCameraWindow(Activity context) {
        try {
            String defaultCameraPackage = "";
            PackageManager packageManager = context.getPackageManager();
            List<ApplicationInfo> list = packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
            for (int n = 0; n < list.size(); n++) {
                if ((list.get(n).flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                    if (list.get(n).loadLabel(packageManager).toString().equalsIgnoreCase("Camera")) {
                        defaultCameraPackage = list.get(n).packageName;
                        break;
                    }
                }
            }

            Intent intentResult = new Intent("android.media.action.IMAGE_CAPTURE");
            intentResult.setPackage(defaultCameraPackage);
            context.startActivityForResult(intentResult, TAKE_PICTURE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete visual vid product
     */
    public static void deleteFolder() {

        File myDirectory = new File(Environment.getExternalStorageDirectory(), "VisualVid");
        if (myDirectory.exists()) {
            myDirectory.delete();
        }


    }

    public static void openImageInGallery(Context mContext, String file) {
//        String videoResource = file.getPath();
        Uri intentUri = Uri.fromFile(new File(file));
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(intentUri.fromFile(new File(file)), "image/jpeg");
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here, or something
            Toast.makeText(mContext, "You may not have a proper app for viewing this content ", Toast.LENGTH_LONG).show();
        }
    }

    public static void dialogBoxWithButton(Context context, String title, String message, String positiveButton, String negativeButton, final DialogCallBack dialogCallBack) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyTheme);
            if (!title.equalsIgnoreCase("")) {
                builder.setTitle(title);
            }
            builder.setMessage(message).setCancelable(false).setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    if (dialogCallBack != null)
                        dialogCallBack.clickedStatus(true);
                }
            });
            if (!negativeButton.equalsIgnoreCase("")) {
                builder.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (dialogCallBack != null)
                            dialogCallBack.clickedStatus(false);
                    }
                });
            }
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void hideCustomKeyboard(KeyboardView keyboardView) {
        try {
            keyboardView.setVisibility(View.GONE);
            keyboardView.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showCustomKeyboard(View v, KeyboardView keyboardView, Context context) {
        if (v != null) {
            ((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        keyboardView.setVisibility(View.VISIBLE);
        keyboardView.setEnabled(true);

    }

    public static void decrementEditTextVal(EditText editText, String mStrDotAval) {
        BigDecimal mDouAmountVal = new BigDecimal("0");
        String et_text = editText.getText().toString();
        String total = "0.0";
        if (!et_text.isEmpty()) {
            total = et_text;

        }

        if (total.contains(".")) {
//            double number = Double.parseDouble(total);
            BigDecimal number = new BigDecimal(total);
//            int integer = (int)number;
            BigInteger integer = new BigDecimal(number.doubleValue()).toBigInteger();
            String[] splitNumber = total.split("\\.");
            BigDecimal decimal = new BigDecimal("0.0");
            BigInteger subtactVal = new BigInteger("1");
            if (splitNumber.length > 1) {
                if (!splitNumber[1].equalsIgnoreCase("")) {
                    decimal = BigDecimal.valueOf(Double.parseDouble("." + splitNumber[1]));
                    mDouAmountVal = BigDecimal.valueOf(integer.subtract(subtactVal).doubleValue() + decimal.doubleValue());
                } else {
                    mDouAmountVal = BigDecimal.valueOf(integer.subtract(subtactVal).doubleValue());
                }
            } else {
                mDouAmountVal = BigDecimal.valueOf(integer.subtract(subtactVal).doubleValue());
            }

        } else {
            mDouAmountVal = BigDecimal.valueOf(Double.parseDouble(total) - 1);
        }
        int res = mDouAmountVal.compareTo(new BigDecimal("0"));

        if (res <= 0) {
            if (mStrDotAval.equalsIgnoreCase("Y")) {
                setCursorPos(editText);
                if (et_text.contains(".")) {
                    editText.setText("0.0");
                } else {
                    editText.setText(UtilConstants.removeLeadingZeroVal("0"));
                }
            } else {
                editText.setText("0");
            }
            setCursorPos(editText);
        } else {
            if (mStrDotAval.equalsIgnoreCase("Y")) {
                setCursorPos(editText);
                if (et_text.contains(".")) {
                    editText.setText(mDouAmountVal + "");
                } else {
                    editText.setText(UtilConstants.removeLeadingZeroVal(mDouAmountVal + ""));
                }
            } else {
                editText.setText(UtilConstants.removeLeadingZeroVal(mDouAmountVal + ""));
            }
            setCursorPos(editText);
        }

    }

    public static void incrementTextValues(EditText editText, String mStrDotAval) {
//        double sPrice = 0;
        BigDecimal sPrice = new BigDecimal("0");
        String et_text = editText.getText().toString();

        String total = "0.0";
        if (!et_text.isEmpty()) {
            total = et_text;
        }
//        sPrice = Double.parseDouble(total);
//        sPrice++;

        if (total.contains(".")) {
//            double number = Double.parseDouble(total);
            BigDecimal number = new BigDecimal(total);
//            int integer = (int)number;
            BigInteger integer = new BigDecimal(number.doubleValue()).toBigInteger();
            String[] splitNumber = total.split("\\.");
            BigDecimal decimal = new BigDecimal("0.0");
            BigInteger incrementVal = new BigInteger("1");
            if (splitNumber.length > 1) {
                if (!splitNumber[1].equalsIgnoreCase("")) {
//                    decimal = Double.parseDouble("."+splitNumber[1]);
                    decimal = BigDecimal.valueOf(Double.parseDouble("." + splitNumber[1]));
                    sPrice = BigDecimal.valueOf(integer.add(incrementVal).doubleValue() + decimal.doubleValue());
                } else {
                    sPrice = BigDecimal.valueOf(integer.add(incrementVal).doubleValue());
                }
            } else {
                sPrice = BigDecimal.valueOf(integer.add(incrementVal).doubleValue());
            }

        } else {
            sPrice = BigDecimal.valueOf(Double.parseDouble(total) + 1);
        }
        if (mStrDotAval.equalsIgnoreCase("Y")) {
            setCursorPos(editText);
            if (et_text.contains(".")) {
                editText.setText(sPrice + "");
            } else {
                editText.setText(UtilConstants.removeLeadingZeroVal(sPrice + ""));
            }
        } else {
            editText.setText(UtilConstants.removeLeadingZeroVal(sPrice + ""));
        }
        setCursorPos(editText);

    }

    private static void setCursorPos(EditText editText) {
        int position = 0;
        try {
            position = editText.getText().toString().length();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            editText.setSelection(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setCursorPostion(EditText editText, View view, MotionEvent motionEvent) {
        EditText edText = (EditText) view;
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int touchPosition = editText.getOffsetForPosition(x, y);
        if (touchPosition >= 0) {
            editText.setSelection(touchPosition);
        }
    }

    public static int getCursorPostion(EditText editText, View view, MotionEvent motionEvent) {
        int touchPosition = 0;
        try {
            EditText edText = (EditText) view;
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            touchPosition = editText.getOffsetForPosition(x, y);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return touchPosition;
    }

    public static boolean isCustomKeyboardVisible(KeyboardView keyboardView) {
        boolean visibleStatus = false;
        try {
            if (keyboardView != null)
                visibleStatus = keyboardView.getVisibility() == View.VISIBLE;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return visibleStatus;
    }

    public static Date convertStringToDate(String dates) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.parse(dates);
    }

    public static String[][] CheckForOtherInConfigValue(String[][] configValues) {
        for (int i = 0; i < configValues[0].length; i++) {
            if (configValues[1][i].equalsIgnoreCase("Others")) {
                String[] temp = new String[configValues.length];
                for (int k = 0; k < configValues.length; k++) {
                    temp[k] = configValues[k][i];
                }
                for (int j = i; j < configValues[0].length - 1; j++) {
                    for (int k = 0; k < configValues.length; k++) {
                        configValues[k][j] = configValues[k][j + 1];
                    }
                }
                for (int k = 0; k < configValues.length; k++) {
                    configValues[k][configValues[0].length - 1] = temp[k];
                }
                break;
            }
        }
        return configValues;
    }

    public static Hashtable getExpenseHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        Hashtable dbHeadTable = new Hashtable();
        try {

            dbHeadTable.put(Constants.ExpenseGUID, fetchJsonHeaderObject.getString(Constants.ExpenseGUID));
            dbHeadTable.put(Constants.LoginID, fetchJsonHeaderObject.getString(Constants.LoginID));
            dbHeadTable.put(Constants.ExpenseNo, fetchJsonHeaderObject.getString(Constants.ExpenseNo));
            dbHeadTable.put(Constants.FiscalYear, fetchJsonHeaderObject.getString(Constants.FiscalYear));
            dbHeadTable.put(Constants.CPName, fetchJsonHeaderObject.getString(Constants.CPName));
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
            dbHeadTable.put(Constants.CPNo, fetchJsonHeaderObject.getString(Constants.CPNo));
            dbHeadTable.put(Constants.CPType, fetchJsonHeaderObject.getString(Constants.CPType));
            dbHeadTable.put(Constants.CPTypeDesc, fetchJsonHeaderObject.getString(Constants.CPTypeDesc));
            dbHeadTable.put(Constants.SPGUID, fetchJsonHeaderObject.getString(Constants.SPGUID));
            dbHeadTable.put(Constants.SPNo, fetchJsonHeaderObject.getString(Constants.SPNo));
            dbHeadTable.put(Constants.SPName, fetchJsonHeaderObject.getString(Constants.SPName));
            dbHeadTable.put(Constants.ExpenseType, fetchJsonHeaderObject.getString(Constants.ExpenseType));
            dbHeadTable.put(Constants.ExpenseTypeDesc, fetchJsonHeaderObject.getString(Constants.ExpenseTypeDesc));
            dbHeadTable.put(Constants.ExpenseDate, fetchJsonHeaderObject.getString(Constants.ExpenseDate));
            dbHeadTable.put(Constants.Status, fetchJsonHeaderObject.getString(Constants.Status));
            dbHeadTable.put(Constants.StatusDesc, fetchJsonHeaderObject.getString(Constants.StatusDesc));
            dbHeadTable.put(Constants.Amount, fetchJsonHeaderObject.getString(Constants.Amount));
            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static Hashtable getSOCancelHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        Hashtable dbHeadTable = new Hashtable();
        try {
            dbHeadTable.put(Constants.SONo, fetchJsonHeaderObject.getString(Constants.SONo));
            dbHeadTable.put(Constants.OrderType, fetchJsonHeaderObject.getString(Constants.OrderType));
            dbHeadTable.put(Constants.OrderDate, fetchJsonHeaderObject.getString(Constants.OrderDate));
            dbHeadTable.put(Constants.CustomerNo, fetchJsonHeaderObject.getString(Constants.CustomerNo));
            dbHeadTable.put(Constants.CustomerPO, fetchJsonHeaderObject.getString(Constants.CustomerPO));
            dbHeadTable.put(Constants.CustomerPODate, fetchJsonHeaderObject.getString(Constants.CustomerPODate));
            dbHeadTable.put(Constants.ShippingTypeID, fetchJsonHeaderObject.getString(Constants.ShippingTypeID));
            // dbHeadTable.put(Constants.MeansOfTranstyp, fetchJsonHeaderObject.getString(Constants.MeansOfTranstyp));
            dbHeadTable.put(Constants.ShipToParty, fetchJsonHeaderObject.getString(Constants.ShipToParty));
            dbHeadTable.put(Constants.SalesArea, fetchJsonHeaderObject.getString(Constants.SalesArea));
            dbHeadTable.put(Constants.SalesOffice, fetchJsonHeaderObject.getString(Constants.SalesOffice));
            dbHeadTable.put(Constants.SalesGroup, fetchJsonHeaderObject.getString(Constants.SalesGroup));
            dbHeadTable.put(Constants.Plant, fetchJsonHeaderObject.getString(Constants.Plant));
            dbHeadTable.put(Constants.Incoterm1, fetchJsonHeaderObject.getString(Constants.Incoterm1));
            dbHeadTable.put(Constants.Incoterm2, fetchJsonHeaderObject.getString(Constants.Incoterm2));
            dbHeadTable.put(Constants.Payterm, fetchJsonHeaderObject.getString(Constants.Payterm));
            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));
            dbHeadTable.put(Constants.NetPrice, fetchJsonHeaderObject.getString(Constants.NetPrice));
            dbHeadTable.put(Constants.TotalAmount, fetchJsonHeaderObject.getString(Constants.TotalAmount));
            dbHeadTable.put(Constants.TaxAmount, fetchJsonHeaderObject.getString(Constants.TaxAmount));
            dbHeadTable.put(Constants.Freight, fetchJsonHeaderObject.getString(Constants.Freight));
            dbHeadTable.put(Constants.Discount, fetchJsonHeaderObject.getString(Constants.Discount));
            dbHeadTable.put(Constants.Remarks, fetchJsonHeaderObject.getString(Constants.Remarks));
            dbHeadTable.put(Constants.Testrun, fetchJsonHeaderObject.getString(Constants.Testrun));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static void displayMsgReqError(int errorCode, Context context) {
        if (errorCode == Constants.UnAuthorized_Error_Code || errorCode == Constants.UnAuthorized_Error_Code_Offline) {
            if (errorCode == Constants.UnAuthorized_Error_Code_Offline) {
                String errorMessage = Constants.PasswordExpiredMsg;
                UtilConstants.showAlert(errorMessage, context);
            } else {
                UtilConstants.showAlert(context.getString(R.string.auth_fail_plz_contact_admin, errorCode + ""), context);
            }
//            UtilConstants.showAlert(context.getString(R.string.auth_fail_plz_contact_admin,errorCode+""), context);
        } else if (errorCode == Constants.Unable_to_reach_server_offline || errorCode == Constants.Network_Error_Code_Offline) {
            UtilConstants.showAlert(context.getString(R.string.data_conn_lost_during_sync_error_code, errorCode + ""), context);
        } else if (errorCode == Constants.Resource_not_found) {
            UtilConstants.showAlert(context.getString(R.string.techincal_error_plz_contact, errorCode + ""), context);
        } else if (errorCode == Constants.Unable_to_reach_server_failed_offline) {
            UtilConstants.showAlert(context.getString(R.string.comm_error_server_failed_plz_contact, errorCode + ""), context);
        } else {
            UtilConstants.showAlert(context.getString(R.string.data_conn_lost_during_sync_error_code, errorCode + ""), context);
        }
    }

    public static ErrorBean getErrorCode(int operation, Exception exception, Context context) {
        ErrorBean errorBean = new ErrorBean();
        try {
            int errorCode = 0;
            boolean hasNoError = true;
            if ((operation == Operation.Create.getValue())) {

                try {
                    // below error code getting from online manger (While posting data vault data)
//                    errorCode = ((ErrnoException) ((ODataNetworkException) exception).getCause().getCause()).errno;
                    Throwable throwables = (((ODataNetworkException) exception).getCause()).getCause().getCause();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (throwables instanceof ErrnoException) {
                            errorCode = ((ErrnoException) throwables).errno;
                        } else {
                            if (exception.getMessage().contains(Constants.Unothorized_Error_Name)) {
                                errorCode = Constants.UnAuthorized_Error_Code;
                                hasNoError = false;
                            } else if (exception.getMessage().contains(Constants.Comm_error_name)) {
                                hasNoError = false;
                                errorCode = Constants.Comm_Error_Code;
                            } else if (exception.getMessage().contains(Constants.Network_Name)) {
                                hasNoError = false;
                                errorCode = Constants.Network_Error_Code;
                            } else {
                                Constants.ErrorNo = 0;
                            }
                        }
                    } else {
                        try {
                            if (exception.getMessage() != null) {
                                if (exception.getMessage().contains(Constants.Unothorized_Error_Name)) {
                                    errorCode = Constants.UnAuthorized_Error_Code;
                                    hasNoError = false;
                                } else if (exception.getMessage().contains(Constants.Comm_error_name)) {
                                    hasNoError = false;
                                    errorCode = Constants.Comm_Error_Code;
                                } else if (exception.getMessage().contains(Constants.Network_Name)) {
                                    hasNoError = false;
                                    errorCode = Constants.Network_Error_Code;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                    if (errorCode != Constants.UnAuthorized_Error_Code) {
                        if (errorCode == Constants.Network_Error_Code || errorCode == Constants.Comm_Error_Code) {
                            hasNoError = false;
                        } else {
                            hasNoError = true;
                        }
                    }
                } catch (Exception e1) {
                    if (exception.getMessage().contains(Constants.Unothorized_Error_Name)) {
                        errorCode = Constants.UnAuthorized_Error_Code;
                        hasNoError = false;
                    } else {
                        Constants.ErrorNo = 0;
                    }
                }
                LogManager.writeLogError("Error : [" + errorCode + "]" + exception.getMessage());

            } else if (operation == Operation.OfflineFlush.getValue() || operation == Operation.OfflineRefresh.getValue() || operation == Operation.GetRequest.getValue()) {
                try {
//                    if (exception instanceof ODataContractViolationException){
//                        errorCode = ((ODataOfflineException) ((ODataContractViolationException) exception).getCause()).getCode();
//                    }else {
                    // below error code getting from offline manger (While posting flush and refresh collection)
                    errorCode = ((ODataOfflineException) ((ODataNetworkException) exception).getCause()).getCode();
//                    }
                    // Display popup for Communication and Unauthorized errors
                    if (errorCode == Constants.Network_Error_Code_Offline
                            || errorCode == Constants.UnAuthorized_Error_Code_Offline
                            || errorCode == Constants.Unable_to_reach_server_offline
                            || errorCode == Constants.Resource_not_found
                            || errorCode == Constants.Unable_to_reach_server_failed_offline) {

                        hasNoError = false;
                    } else {
                        hasNoError = true;
                    }

                } catch (Exception e) {
                    try {
                        String mStrErrMsg = exception.getCause().getLocalizedMessage();
                        if (mStrErrMsg.contains(Executing_SQL_Commnd_Error)) {
                            hasNoError = false;
                            errorCode = -10001;
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
                if (errorCode != 0) {
                    LogManager.writeLogError("Error : [" + errorCode + "]" + exception.getMessage());
                }
            } else if (operation == Operation.GetStoreOpen.getValue()) {
                // below error code getting from offline manger (While posting flush and refresh collection)
                try {
//                    if (exception instanceof ODataContractViolationException){
//                        errorCode = ((ODataOfflineException) ((ODataContractViolationException) exception).getCause()).getCode();
//                    }else {
                    errorCode = ((ODataOfflineException) ((ODataNetworkException) exception).getCause()).getCode();
//                    }
                    // Display popup for Communication and Unauthorized errors
                    if (errorCode == Constants.Network_Error_Code_Offline
                            || errorCode == Constants.UnAuthorized_Error_Code_Offline
                            || errorCode == Constants.Unable_to_reach_server_offline
                            || errorCode == Constants.Resource_not_found
                            || errorCode == Constants.Unable_to_reach_server_failed_offline) {

                        hasNoError = false;
                    } else {
                        hasNoError = true;
                    }
                } catch (Exception e) {
                    try {
                        String mStrErrMsg = exception.getCause().getLocalizedMessage();
                        if (mStrErrMsg.contains(Store_Defining_Req_Not_Matched)) {
                            hasNoError = false;
                            errorCode = -10247;
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }

            errorBean.setErrorCode(errorCode);
            if (exception.getMessage() != null && !exception.getMessage().equalsIgnoreCase("")) {
                errorBean.setErrorMsg(exception.getMessage());
            } else {
                errorBean.setErrorMsg(context.getString(R.string.unknown_error));
            }

            errorBean.setHasNoError(hasNoError);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (errorBean.getErrorMsg().contains(Constants.Build_Database_Failed_Error_Code1 + "")
                || errorBean.getErrorMsg().contains(Constants.Build_Database_Failed_Error_Code2 + "")
                || errorBean.getErrorMsg().contains(Constants.Build_Database_Failed_Error_Code3 + "")
                || errorBean.getErrorCode() == Constants.Execu_SQL_Error_Code
                || errorBean.getErrorCode() == Constants.Store_Def_Not_matched_Code) {
            if (errorBean.getErrorMsg().contains("500")
                    || errorBean.getErrorMsg().contains(Constants.RFC_ERROR_CODE_100029)
                    || errorBean.getErrorMsg().contains(Constants.RFC_ERROR_CODE_100027)) {
                errorBean.setStoreFailed(false);
            } else {
                errorBean.setStoreFailed(true);
            }

        } else {
            errorBean.setStoreFailed(false);
        }


//        }
        if (errorBean.isStoreFailed()) {
            try {
                UtilConstants.closeStore(context,
                        OfflineManager.options, errorBean.getErrorMsg() + "",
                        offlineStore, Constants.PREFS_NAME, errorBean.getErrorCode() + "");

            } catch (Exception e) {
                e.printStackTrace();
            }
            Constants.Entity_Set.clear();
            Constants.AL_ERROR_MSG.clear();
            offlineStore = null;
            OfflineManager.options = null;
        }
        errorBean.setErrorMsg(makecustomHttpErrormessage(errorBean.getErrorMsg()));
        int httperrorCode = makecustomHttpErrorCode(errorBean.getErrorMsg());
        if (httperrorCode != 0)
            errorBean.setErrorCode(httperrorCode);
        return errorBean;
    }

    public static String convertALBussinessMsgToString(ArrayList<String> arrayList) {
        String mErrorMsg = "";
        if (arrayList != null && arrayList.size() > 0) {
            for (String errMsg : arrayList) {
                if (mErrorMsg.length() == 0) {
                    mErrorMsg = mErrorMsg + errMsg;
                } else {
                    mErrorMsg = mErrorMsg + "\n" + errMsg;
                }
            }
        }
        return mErrorMsg;
    }

    public static void customAlertDialogWithScroll(final Context context, final String mErrTxt) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_dialog_scroll, null);

        String mStrErrorEntity = getErrorEntityName();

        TextView textview = (TextView) view.findViewById(R.id.tv_err_msg);
        final TextView tvdetailmsg = (TextView) view.findViewById(R.id.tv_detail_msg);

        String temp_errMsg = mErrTxt;
        temp_errMsg = Constants.makecustomHttpErrormessage(temp_errMsg);
        if (!TextUtils.isEmpty(temp_errMsg) && temp_errMsg.equalsIgnoreCase(mErrTxt))
            if (mErrTxt.contains("invalid authentication")) {
                textview.setText(Constants.PasswordExpiredMsg);
                tvdetailmsg.setText(mErrTxt);
            } else if (mErrTxt.contains("HTTP Status 401 ? Unauthorized")) {
                textview.setText(Constants.PasswordExpiredMsg);
                tvdetailmsg.setText(mErrTxt);
            } else {
                textview.setText(context.getString(R.string.msg_error_occured_during_sync_except) + " " + mStrErrorEntity + " \n" + mErrTxt);
            }

        else {
            textview.setText("\n" + temp_errMsg);
        }

        if (mErrTxt.contains("invalid authentication") || mErrTxt.contains("HTTP Status 401 ? Unauthorized")) {
            final AlertDialog dialog = new AlertDialog.Builder(context)
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                    .setNeutralButton("Details", null)
                    .setNegativeButton("Settings", null)
                    .create();

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialogInterface) {

                    Button b = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // TODO Do something
                            dialog.dismiss();
                        }
                    });

                    Button mesg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                    mesg.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // TODO Do something

                            tvdetailmsg.setVisibility(View.VISIBLE);
                            // dialog.dismiss();
                        }
                    });

                    Button change = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                    change.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // TODO Do something
                            RegistrationModel<Serializable> registrationModel = new RegistrationModel<>();
                            Intent intent = new Intent(context, com.arteriatech.mutils.support.SecuritySettingActivity.class);
                            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
                            String userName = sharedPreferences.getString("username","");
                            registrationModel.setExtenndPwdReq(true);
                            registrationModel.setUpdateAsPortalPwdReq(true);
                            registrationModel.setIDPURL(Configuration.IDPURL);
                            registrationModel.setExternalTUserName(Configuration.IDPTUSRNAME);
                            registrationModel.setExternalTPWD(Configuration.IDPTUSRPWD);
                            registrationModel.setShredPrefKey(Constants.PREFS_NAME);
                            registrationModel.setUserName(userName);
                            intent.putExtra(UtilConstants.RegIntentKey, registrationModel);
                            //context.startActivityForResult(intent, 350);
                            context.startActivity(intent);
                            // dialog.dismiss();
                        }
                    });

                }
            });
            dialog.show();
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.MyTheme);
            alertDialog.setCancelable(false)
                    .setPositiveButton(context.getString(R.string.msg_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            copyMessageToClipBoard(context, mErrTxt);
                        }
                    });
            alertDialog.setView(view);
            AlertDialog alert = alertDialog.create();
            alert.show();
        }


    }

    public static String getErrorEntityName() {
        String mEntityName = "";

        try {
            if (Constants.Entity_Set != null && Constants.Entity_Set.size() > 0) {

                if (Constants.Entity_Set != null && !Constants.Entity_Set.isEmpty()) {
                    Iterator itr = Constants.Entity_Set.iterator();
                    while (itr.hasNext()) {
                        if (mEntityName.length() == 0) {
                            mEntityName = mEntityName + itr.next().toString();
                        } else {
                            mEntityName = mEntityName + "," + itr.next().toString();
                        }
                    }
                }
            }
        } catch (Exception e) {
            mEntityName = "";
        }

        return mEntityName;
    }

    public static void copyMessageToClipBoard(Context context, String message) {
        ClipboardManager clipboard = (ClipboardManager) context.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Error Message", message);
        clipboard.setPrimaryClip(clip);
        if (!message.contains("invalid authentication")) {
            UtilConstants.showAlert(context.getString(R.string.issue_copied_to_clipboard_send_to_chnnel_team), context);
        }
    }

    public static TextView setFontSizeByMaxText(TextView textView) {
        try {
            int lineCount = textView.getText().length();

            if (lineCount < 20) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            } else if (lineCount < 35) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            } else if (lineCount < 50) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            } else if (lineCount < 70) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            } else if (lineCount < 85) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
            } else {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 6);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
        return textView;
    }

    public static String getRouteSchGUID(String collName, String columnName, String whereColumnn, String whereColval, String cpTypeID) {


        String mStrRouteSchGUID = "";
        if (cpTypeID.equalsIgnoreCase(Constants.str_01)) {
            try {
                mStrRouteSchGUID = OfflineManager.getGuidValueByColumnName(collName + "?$top=1 &$select=" + columnName + " &$filter = " + whereColumnn + " eq '" + whereColval + "'", columnName);
            } catch (Exception e) {
                mStrRouteSchGUID = "";
            }
        } else {
            // future will use ful
        }

        return mStrRouteSchGUID;
    }

    public static String[][] getDistributorsByCPNO(String mStrCPGUID) {
        String[][] mArrayDistributors = null;
        String qryStr = Constants.Customers + "?$filter=" + Constants.CustomerNo + " eq '" + mStrCPGUID + "' ";
        try {
            mArrayDistributors = OfflineManager.getCustomerDetails(qryStr);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if (mArrayDistributors == null) {
            mArrayDistributors = new String[1][1];
            mArrayDistributors[0][0] = "";
        }

        return mArrayDistributors;
    }

    public static String makeMsgReqError(int errorCode, Context context, boolean isInvError) {
        String mStrErrorMsg = "";

        if (!isInvError) {
            if (errorCode == Constants.UnAuthorized_Error_Code || errorCode == Constants.UnAuthorized_Error_Code_Offline) {
                mStrErrorMsg = context.getString(R.string.auth_fail_plz_contact_admin, errorCode + "");
            } else if (errorCode == Constants.Unable_to_reach_server_offline || errorCode == Constants.Network_Error_Code_Offline) {
                mStrErrorMsg = context.getString(R.string.data_conn_lost_during_sync_error_code, errorCode + "");
            } else if (errorCode == Constants.Resource_not_found) {
                mStrErrorMsg = context.getString(R.string.techincal_error_plz_contact, errorCode + "");
            } else if (errorCode == Constants.Unable_to_reach_server_failed_offline) {
                mStrErrorMsg = context.getString(R.string.comm_error_server_failed_plz_contact, errorCode + "");
            } else {
                mStrErrorMsg = context.getString(R.string.data_conn_lost_during_sync_error_code, errorCode + "");
            }
        } else {
            if (errorCode == 4) {
                mStrErrorMsg = context.getString(R.string.auth_fail_plz_contact_admin, Constants.UnAuthorized_Error_Code + "");
            } else if (errorCode == 3) {
                mStrErrorMsg = context.getString(R.string.data_conn_lost_during_sync_error_code, Constants.Network_Error_Code + "");
            } else {
                mStrErrorMsg = context.getString(R.string.data_conn_lost_during_sync_error_code, Constants.Network_Error_Code + "");
            }
        }

        return mStrErrorMsg;
    }

    /*Creates table for Sync history in SQLite DB*/
    public static void createSyncDatabase(Context context) {
        Hashtable hashtable = new Hashtable<>();
        hashtable.put(Constants.SyncGroup, "");
        hashtable.put(Constants.Collections, "");
        hashtable.put(Constants.TimeStamp, "");
        try {
            Constants.events.crateTableConfig(Constants.SYNC_TABLE, hashtable);
            getSyncHistoryTable(context);
        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_creating_sync_db
                    + e.getMessage());
        }
    }

    /*Sync History table for Sync*/
    public static void getSyncHistoryTable(Context context) {
        String[] definingReqArray = Constants.getDefinigReq(context);
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

    public static boolean syncHistoryTableExist() {
        return Constants.events.syncHistoryTableExist(Constants.SYNC_TABLE);
    }

    private static void createprospectedCustomerDatabase() {
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

    private static void createSchmeDatabase() {
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

    private static void createoutstandingAgeDatabase() {
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

    private static void createorderinfoDatabase() {
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

    private static void createPriceinfoDatabase() {
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

    private static void createStockInfoDatabase() {
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

    private static void createPOPDatabase() {
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

    private static void createTradeInfoDatabase() {
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

    private static void createTradeInfoCustomerTechTeamDatabase() {
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

    private static void createDealerTargetVsAchivemnetDatabase() {
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

    private static void createSalesTargetVsAchivemnetDatabase() {
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

    public static String formatValue(String key, String value) {

        String formattedValue = "";
        if (!key.equalsIgnoreCase("") && !value.equalsIgnoreCase("")) {

            formattedValue = value + " - " + key;

        } else {
            if (!key.equalsIgnoreCase("") || !value.equalsIgnoreCase("")) {
                formattedValue = value + " - " + key;

            }

        }

        return formattedValue;

    }

    public static String getNameSpace(ODataOfflineStore oDataOfflineStore) {
        String mStrNameSpace = "";

        ODataMetadata oDataMetadata = null;
        try {
            oDataMetadata = oDataOfflineStore.getMetadata();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Set<String> set = oDataMetadata.getMetaNamespaces();

        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                String tempNameSpace = itr.next().toString();
                if (!tempNameSpace.equalsIgnoreCase("OfflineOData")) {
                    mStrNameSpace = tempNameSpace;
                }

            }
        }

        return mStrNameSpace;
    }

    public static void getLocation(Activity mActivity, final LocationInterface locationInterface) {
        UtilConstants.latitude = 0.0;
        UtilConstants.longitude = 0.0;
        LocationUtils.getCustomLocation(mActivity, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                if (status) {
                    android.location.Location location = locationModel.getLocation();
                    UtilConstants.latitude = location.getLatitude();
                    UtilConstants.longitude = location.getLongitude();
                    Log.d("LocationUtils", "location: " + locationModel.getLocationFrom());
                }
                if (locationInterface != null) {
                    locationInterface.location(status, locationModel, errorMsg, errorCode);
                }
            }
        });
    }

    public static boolean checkPermission(Context context) {

        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
            int result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            int resultCore = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (result == PackageManager.PERMISSION_GRANTED && resultCore == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;

            }
        } else {
            return true;
        }

    }

    public static void setPermissionStatus(Context mContext, String key, boolean value) {
        SharedPreferences permissionStatus = mContext.getSharedPreferences("permissionStatus", MODE_PRIVATE);
        SharedPreferences.Editor editor = permissionStatus.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getPermissionStatus(Context mContext, String key) {
        SharedPreferences permissionStatus = mContext.getSharedPreferences("permissionStatus", MODE_PRIVATE);
        return permissionStatus.getBoolean(key, false);
    }

    public static void navigateToAppSettingsScreen(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    public static void showAlert(String message, Context ctxt) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctxt, R.style.MyTheme);
            builder.setMessage(message).setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getDueDateStatus(Calendar todayCalenderDate, String strInvoiceDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
            Date invoiceDate = null;
            invoiceDate = formatter.parse(strInvoiceDate);
            Calendar oldDay = Calendar.getInstance();
            oldDay.setTime(invoiceDate);
            long diff = todayCalenderDate.getTimeInMillis() - oldDay.getTimeInMillis(); //result in millis
            long days = diff / (24 * 60 * 60 * 1000);
            if (days > 0) {
                return "C";//over due
            } else if (days < -6) {
                return "B";//not due
            } else {
                return "A";//near due
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void displayPieChart(String targetPer, PieChart pieChart, Context context, float textSize, String mStrValue) {
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        //spacing between graph and margin
//        mChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setExtraOffsets(-5, -5, -5, -5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);
//        pieChart.setCenterText(Constants.generateCenterSpannableText(ConstantsUtils.decimalZeroBasedOnValue(targetPer) + "%"));
        pieChart.setCenterTextSize(textSize);
        pieChart.setCenterText(Constants.generateCenterSpannableText(mStrValue));

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(75f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setDrawCenterText(true);

        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(false);
        Constants.setPieChartData(targetPer, pieChart, context);
        pieChart.animateXY(1500, 1500);
//        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // entry label styling
        pieChart.getLegend().setEnabled(false);
        pieChart.setDrawEntryLabels(false);

//        pieChart.getTransformer().prepareMatrixValuePx(chart);
//        pieChart.mat().prepareMatrixOffset(chart);

//        pieChart.getContentRect().set(0, 0, pieChart.getWidth(), pieChart.getHeight());
        pieChart.invalidate();
    }

    public static float pxFromDp(float dp, Context mContext) {
        return dp * mContext.getResources().getDisplayMetrics().density;
    }

    public static ArrayList<CustomerBean> getTodayRoutePlan() {
        String routeQry = Constants.RoutePlans + "?$filter=" + Constants.VisitDate + " eq datetime'" + UtilConstants.getNewDate() + "'";
        ArrayList<CustomerBean> alRSCHList = null;
        try {
            alRSCHList = OfflineManager.getTodayRoutes1(routeQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        return alRSCHList;
    }

    public static String getCustomerFromRouteSchPlan(ArrayList<CustomerBean> alRouteList) {
        String mCPGuidQry = "", qryForTodaysBeat = "";
        if (alRouteList != null && alRouteList.size() > 0) {
            String mRSCHQry = "";
            // Routescope ID will be same for all the routes planned for the day hence first record scope is used to decide
            String routeSchopeVal = alRouteList.get(0).getRoutSchScope();
            if (alRouteList.size() > 1) {

                if (routeSchopeVal.equalsIgnoreCase("000001")) {
                    for (CustomerBean routeList : alRouteList) {
                        if (mRSCHQry.length() == 0)
                            mRSCHQry += " guid'" + routeList.getRschGuid().toUpperCase() + "'";
                        else
                            mRSCHQry += " or " + Constants.RouteSchGUID + " eq guid'" + routeList.getRschGuid().toUpperCase() + "'";

                    }

                } else if (routeSchopeVal.equalsIgnoreCase("000002")) {
                    // Get the list of retailers from RoutePlans

                }

            } else {


                if (routeSchopeVal.equalsIgnoreCase("000001")) {

                    mRSCHQry = "guid'" + alRouteList.get(0).getRschGuid().toUpperCase() + "'";


                } else if (routeSchopeVal.equalsIgnoreCase("000002")) {
                    // Get the list of retailers from RoutePlans
                }

            }
            qryForTodaysBeat = Constants.RouteSchedulePlans + "?$filter=(" +
                    Constants.RouteSchGUID + " eq " + mRSCHQry + ") &$orderby=" + Constants.SequenceNo + "";

            try {
                // Prepare Today's beat Retailer Query
                mCPGuidQry = OfflineManager.getCustListQryByBeatCustomers(qryForTodaysBeat);

            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }

        return mCPGuidQry;
    }

    public static String getTotalOrderValue(Context context, String mStrCurrentDate,
                                            ArrayList<String> alTodaysRetailers, boolean hideVisit, String mStrCalBase) {

        double totalOrderVal = 0.0;

        String mStrRetQry = "", ssINVRetQry = "";

        if (alTodaysRetailers != null && alTodaysRetailers.size() > 0) {
            for (int i = 0; i < alTodaysRetailers.size(); i++) {
                if (i == 0 && i == alTodaysRetailers.size() - 1) {
                    mStrRetQry = mStrRetQry
                            + "(" + Constants.CustomerNo + "%20eq%20'"
                            + alTodaysRetailers.get(i) + "')";

                    ssINVRetQry = ssINVRetQry
                            + "(" + Constants.CustomerNo + "%20eq%20'"
                            + alTodaysRetailers.get(i) + "')";

                } else if (i == 0) {
                    mStrRetQry = mStrRetQry
                            + "(" + Constants.CustomerNo + "%20eq%20'"
                            + alTodaysRetailers.get(i) + "'";

                    ssINVRetQry = ssINVRetQry
                            + "(" + Constants.CustomerNo + "%20eq%20'"
                            + alTodaysRetailers.get(i) + "'";

                } else if (i == alTodaysRetailers.size() - 1) {
                    mStrRetQry = mStrRetQry
                            + "%20or%20" + Constants.CustomerNo + "%20eq%20'"
                            + alTodaysRetailers.get(i) + "')";

                    ssINVRetQry = ssINVRetQry
                            + "%20or%20" + Constants.CustomerNo + "%20eq%20'"
                            + alTodaysRetailers.get(i) + "')";
                } else {
                    mStrRetQry = mStrRetQry
                            + "%20or%20" + Constants.CustomerNo + "%20eq%20'"
                            + alTodaysRetailers.get(i) + "'";

                    ssINVRetQry = ssINVRetQry
                            + "%20or%20" + Constants.CustomerNo + "%20eq%20'"
                            + alTodaysRetailers.get(i) + "'";
                }

                if (!alCustomers.contains(alTodaysRetailers.get(i))) {
                    alCustomers.add(alTodaysRetailers.get(i));
//                    alRetailersGuid.add(alTodaysRetailers.get(i).getCpGuidStringFormat());
                }
            }

        }
        Constants.SO_Cust_QRY = mStrRetQry;
        String mStrOrderVal = "0.0";
        String columnName = "";
        if (mStrCalBase.equalsIgnoreCase(Constants.str_02)) {
            columnName = Constants.TotalAmount;
        } else {
            columnName = Constants.TotalQuantity;
        }
        if (alCustomers.size() > 0 || hideVisit) {


            try {
                if (!mStrRetQry.equalsIgnoreCase("")) {
                    mStrOrderVal = OfflineManager.getTotalSumByCondition("" + Constants.SOs +
                            "?$select=" + columnName + " &$filter=" + Constants.OrderDate + " eq datetime'" + mStrCurrentDate + "' and " + mStrRetQry + " ", columnName);
                } else {
                    mStrOrderVal = OfflineManager.getTotalSumByCondition("" + Constants.SOs +
                            "?$select=" + columnName + " &$filter=" + Constants.OrderDate + " eq datetime'" + mStrCurrentDate + "' ", columnName);
                }
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }
        double mdouDevOrderVal = 0.0;

        if (alCustomers.size() > 0 || hideVisit) {
            try {
                if (mStrCalBase.equalsIgnoreCase(Constants.str_02)) {
                    mdouDevOrderVal = OfflineManager.getDeviceTotalOrderAmt(Constants.SalesOrderDataValt, context, mStrCurrentDate, alCustomers, hideVisit);
                } else {
                    mdouDevOrderVal = OfflineManager.getDeviceTotalOrderQty(Constants.SalesOrderDataValt, context, mStrCurrentDate, alCustomers, hideVisit);
                }

            } catch (Exception e) {
                mdouDevOrderVal = 0.0;
            }
        }

        totalOrderVal = Double.parseDouble(mStrOrderVal) + mdouDevOrderVal;

        return totalOrderVal + "";
    }

    public static ArrayList<CustomerBean> getTodayRoutePlanCustomers() {
        String routeQry = Constants.RoutePlans + "?$filter=" + Constants.VisitDate + " eq datetime'" + UtilConstants.getNewDate() + "'";
        ArrayList<CustomerBean> alRSCHList = null;
        try {
            alRSCHList = OfflineManager.getTodayRoutesCustomers(routeQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        return alRSCHList;
    }

    public static String getDeviceTLSD(String mStrSoldToID) {
        String mStrQry = "", mStrOfflineTLSD = "0";
        if (!Constants.SO_Cust_QRY.equalsIgnoreCase("")) {
            try {
                mStrQry = OfflineManager.makeSOsQry(Constants.SOs + "?$filter= " + Constants.OrderDate +
                        " eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.SO_Cust_QRY + " ", Constants.SONo);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }

        if (!mStrQry.equalsIgnoreCase("")) {
            try {
                mStrOfflineTLSD = OfflineManager.getCountTLSDFromDatabase(Constants.SOItemDetails + "?$filter=" + mStrQry);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }


        Double mDouTotalTLSD = Double.parseDouble(mStrOfflineTLSD);

        return mDouTotalTLSD + "";
    }

    public static String getDeviceTLSDDataVault(ArrayList<String> alTodaysRetailers, Context mContext) {

        double mDoubleDevTLSD = 0.0;
        if (alTodaysRetailers.size() > 0) {
            try {
                mDoubleDevTLSD = OfflineManager.getTLSD(Constants.SalesOrderDataValt, mContext,
                        UtilConstants.getNewDate(), alTodaysRetailers);
            } catch (Exception e) {
                mDoubleDevTLSD = 0.0;
            }
        }
        return mDoubleDevTLSD + "";
    }

    public static SpannableString generateCenterSpannableText(String totalPercent) {

        SpannableString s = new SpannableString(totalPercent);
        s.setSpan(new RelativeSizeSpan(1.5f), 0, s.length(), 0);
        return s;
    }

    public static void setPieChartData(String totalPercent, PieChart pieChart, Context context) {

        String remainingPercent = "0";
        try {
            remainingPercent = String.valueOf(100 - Integer.parseInt(totalPercent.split("\\.")[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        float flTotalPercent = Float.parseFloat(totalPercent);
        float flRemainingPer = Float.parseFloat(remainingPercent);

        List<PieEntry> entries = new ArrayList<>();
        if (flTotalPercent != 0f)
            entries.add(new PieEntry(flTotalPercent, ""));
        if (flRemainingPer != 0f)
            entries.add(new PieEntry(Float.parseFloat(remainingPercent), ""));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(0f);

        //pie chart background color start
        ArrayList<Integer> colors = new ArrayList<Integer>();
        if (flTotalPercent != 0f)
            colors.add(ColorTemplate.rgb(String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(context, R.color.primaryColor)))));
        if (flRemainingPer != 0f)
            colors.add(Color.rgb(238, 238, 238));
        dataSet.setColors(colors);
        //pie chart background color end
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setDrawValues(false);
        pieChart.setData(data);
//        pieChart.highlightValue(0, 0, false);
        pieChart.highlightValue(null);
    }

    public static String getTotalOrderValueByCurrentMonth(String mStrFirstDateMonth, String cpQry, String mStrCPDMSDIVQry) {

        double totalOrderVal = 0.0;

        String mStrOrderVal = "0.0";
        try {
            if (cpQry.equalsIgnoreCase("")) {
                mStrOrderVal = OfflineManager.getTotalSumByCondition("" + Constants.SOs +
                        "?$select=" + Constants.NetPrice + " &$filter=" + Constants.OrderDate + " ge datetime'" + mStrFirstDateMonth + "' and " + Constants.OrderDate + " le datetime'" + UtilConstants.getNewDate() + "'  ", Constants.NetPrice);
            } else {
                mStrOrderVal = OfflineManager.getTotalSumByCondition("" + Constants.SOs +
                        "?$select=" + Constants.NetPrice + " &$filter=" + Constants.OrderDate + " ge datetime'" + mStrFirstDateMonth + "' and " + Constants.OrderDate + " le datetime'" + UtilConstants.getNewDate() + "' and (" + cpQry + ") ", Constants.NetPrice);
            }


        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        double mdouDevOrderVal = 0.0;

        totalOrderVal = Double.parseDouble(mStrOrderVal) + mdouDevOrderVal;

        return totalOrderVal + "";
    }

    public static String removePrecedingZero(String str) {
        // Count trailing zeros

        try {
            int i = 0;
            while (str.charAt(i) == '0')
                i++;

            // Convert str into StringBuffer as Strings
            // are immutable.
            StringBuffer sb = new StringBuffer(str);

            // The  StringBuffer replace function removes
            // i characters from given index (0 here)
            sb.replace(0, i, "");

            return sb.toString();  // return in String
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }


    public static JSONObject getMTPHeaderValuesFrmJsonObject(JSONObject fetchJsonHeaderObject) {
        JSONObject dbHeadTable = new JSONObject();
        try {
            if(fetchJsonHeaderObject.has(Constants.RouteSchGUID)) {
                dbHeadTable.put(Constants.RouteSchGUID, fetchJsonHeaderObject.getString(Constants.RouteSchGUID));
                REPEATABLE_REQUEST_ID = fetchJsonHeaderObject.getString(Constants.RouteSchGUID).replace("-","");
            }

            String createdOn = "";
            try {
                if(fetchJsonHeaderObject.has(Constants.CreatedOn)) {
                    createdOn=fetchJsonHeaderObject.optString(Constants.CreatedOn);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String createdAt = "";
            try {
                if(fetchJsonHeaderObject.has(Constants.CreatedAt)) {
                    createdAt=fetchJsonHeaderObject.optString(Constants.CreatedAt);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            REPEATABLE_DATE = UtilConstants.getReArrangeDateFormat(createdOn) + Constants.T + UtilConstants.convertTimeOnly(createdAt);


            if(fetchJsonHeaderObject.has(Constants.SalesPersonID)) {
                dbHeadTable.put(Constants.SalesPersonID, fetchJsonHeaderObject.getString(Constants.SalesPersonID));
            }
            if(fetchJsonHeaderObject.has(Constants.Testrun)) {
                dbHeadTable.put(Constants.Testrun, fetchJsonHeaderObject.getString(Constants.Testrun));
            }
            if(fetchJsonHeaderObject.has(Constants.ValidFrom)) {
                dbHeadTable.put(Constants.ValidFrom, fetchJsonHeaderObject.getString(Constants.ValidFrom));
            }
            if(fetchJsonHeaderObject.has(Constants.ValidTo)) {
                dbHeadTable.put(Constants.ValidTo, fetchJsonHeaderObject.getString(Constants.ValidTo));
            }
            /*if(fetchJsonHeaderObject.has(Constants.IS_UPDATE)) {
                dbHeadTable.put(Constants.IS_UPDATE, fetchJsonHeaderObject.getString(Constants.IS_UPDATE));
            }*/
            if(fetchJsonHeaderObject.has(Constants.Month)) {
                dbHeadTable.put(Constants.Month, fetchJsonHeaderObject.getString(Constants.Month));
            }
            if(fetchJsonHeaderObject.has(Constants.Year)) {
                dbHeadTable.put(Constants.Year, fetchJsonHeaderObject.getString(Constants.Year));
            }
            if(fetchJsonHeaderObject.has(Constants.RoutId)) {
                dbHeadTable.put(Constants.RoutId, fetchJsonHeaderObject.optString(Constants.RoutId));
            }
            if(fetchJsonHeaderObject.has(Constants.CreatedBy)) {
                dbHeadTable.put(Constants.CreatedBy, fetchJsonHeaderObject.optString(Constants.CreatedBy));
            }
            if(fetchJsonHeaderObject.has(Constants.CreatedOn)) {
                dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.optString(Constants.CreatedOn));
            }


            JSONArray itemsArray = new JSONArray(fetchJsonHeaderObject.getString(Constants.RouteSchedulePlans));
            JSONArray jsonArray = new JSONArray();
            for (int incrementVal = 0; incrementVal < itemsArray.length(); incrementVal++) {
                JSONObject singleRow = itemsArray.getJSONObject(incrementVal);

                JSONObject itemObject = new JSONObject();
                if (singleRow.has(Constants.RouteSchPlanGUID)) {
                    itemObject.put(Constants.RouteSchPlanGUID, singleRow.get(Constants.RouteSchPlanGUID));
                }
                if (singleRow.has(Constants.RouteSchGUID)) {
                    itemObject.put(Constants.RouteSchGUID, singleRow.get(Constants.RouteSchGUID));
                }
                if (singleRow.has(Constants.VisitDate)) {
                    itemObject.put(Constants.VisitDate, singleRow.get(Constants.VisitDate));
                }
                if (singleRow.has(Constants.VisitCPGUID)) {
                    itemObject.put(Constants.VisitCPGUID, singleRow.get(Constants.VisitCPGUID));
                }
                if (singleRow.has(Constants.VisitCPName)) {
                    itemObject.put(Constants.VisitCPName, singleRow.get(Constants.VisitCPName));
                }
                if (singleRow.has(Constants.ActivityDesc)) {
                    itemObject.put(Constants.ActivityDesc, singleRow.get(Constants.ActivityDesc));
                }
                if (singleRow.has(Constants.ActivityID)) {
                    itemObject.put(Constants.ActivityID, singleRow.get(Constants.ActivityID));
                }
                if (singleRow.has(Constants.SalesDistrict)) {
                    itemObject.put(Constants.SalesDistrict, singleRow.get(Constants.SalesDistrict));
                }
                if (singleRow.has(Constants.SalesDistrictDesc)) {
                    itemObject.put(Constants.SalesDistrictDesc, singleRow.get(Constants.SalesDistrictDesc));
                }
                if (singleRow.has(Constants.Remarks)) {
                    itemObject.put(Constants.Remarks, singleRow.get(Constants.Remarks));
                }

                jsonArray.put(itemObject);
            }
            dbHeadTable.put(RouteSchedulePlans, jsonArray);
            //RouteScheduleSPs
//            JSONArray itemsArraySPs = new JSONArray(fetchJsonHeaderObject.getString(Constants.RouteSchedulePlans));
            JSONArray jsonArraySPs = new JSONArray();
//            for (int incrementVal = 0; incrementVal < itemsArraySPs.length(); incrementVal++) {
//                JSONObject singleRow = itemsArraySPs.getJSONObject(incrementVal);

            JSONObject itemObject = new JSONObject();
//                if (singleRow.has(Constants.RouteSchSPGUID)) {
            itemObject.put(Constants.RouteSchSPGUID,GUID.newRandom().toString36());
//                }
            if (fetchJsonHeaderObject.has(Constants.RouteSchGUID)) {
                itemObject.put(Constants.RouteSchGUID, fetchJsonHeaderObject.get(Constants.RouteSchGUID));
            }
            if (fetchJsonHeaderObject.has(Constants.SalesPersonID)) {
                itemObject.put(Constants.SalesPersonID, fetchJsonHeaderObject.get(Constants.SalesPersonID));
            }
            itemObject.put(Constants.DMSDivision, "00");
            jsonArraySPs.put(itemObject);
//            }
            dbHeadTable.put(RouteScheduleSPs, jsonArraySPs);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static Hashtable getMTPHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        Hashtable dbHeadTable = new Hashtable();
        try {
            dbHeadTable.put(Constants.RouteSchGUID, fetchJsonHeaderObject.getString(Constants.RouteSchGUID));
            dbHeadTable.put(Constants.SalesPersonID, fetchJsonHeaderObject.getString(Constants.SalesPersonID));
            dbHeadTable.put(Constants.Testrun, fetchJsonHeaderObject.getString(Constants.Testrun));
            dbHeadTable.put(Constants.ValidFrom, fetchJsonHeaderObject.getString(Constants.ValidFrom));
            dbHeadTable.put(Constants.ValidTo, fetchJsonHeaderObject.getString(Constants.ValidTo));
            dbHeadTable.put(Constants.IS_UPDATE, fetchJsonHeaderObject.getString(Constants.IS_UPDATE));
            dbHeadTable.put(Constants.Month, fetchJsonHeaderObject.getString(Constants.Month));
            dbHeadTable.put(Constants.Year, fetchJsonHeaderObject.getString(Constants.Year));
            dbHeadTable.put(Constants.RoutId, fetchJsonHeaderObject.optString(Constants.RoutId));
            dbHeadTable.put(Constants.CreatedBy, fetchJsonHeaderObject.optString(Constants.CreatedBy));
            dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.optString(Constants.CreatedOn));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static int getMTPDaysAllowEdit() {
        return getTypesetValues("MTPDAYSALW");
    }

    public static int getRTGSDaysAllowEdit() {
        return getTypesetValues("CPDAYSALW");
    }

    public static int getTypesetValues(String types) {
        int maxDays = 0;
        try {
            String stMaxValue = OfflineManager.getValueByColumnName(Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +
                    Constants.SF + "' and " + Constants.Types + " eq '" + types + "' &$top=1", Constants.TypeValue);

            if (!TextUtils.isEmpty(stMaxValue))
                maxDays = Integer.parseInt(stMaxValue);
//            if (maxDays==0){
//                maxDays=32;
//            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxDays;
    }

    public static JSONObject getRTGSHeaderValuesFrmJsonObject(JSONObject fetchJsonHeaderObject) {
        JSONObject dbHeadTable = new JSONObject();
        try {
            if(fetchJsonHeaderObject.has(Constants.CollectionPlanGUID)) {
                dbHeadTable.put(Constants.CollectionPlanGUID, fetchJsonHeaderObject.getString(Constants.CollectionPlanGUID));
                REPEATABLE_REQUEST_ID = fetchJsonHeaderObject.getString(Constants.CollectionPlanGUID).replace("-","");
            }
            if(fetchJsonHeaderObject.has(Constants.SPGUID)) {
                dbHeadTable.put(Constants.SPGUID, fetchJsonHeaderObject.getString(Constants.SPGUID));
            }
            if(fetchJsonHeaderObject.has(Constants.Period)) {
                dbHeadTable.put(Constants.Period, fetchJsonHeaderObject.getString(Constants.Period));
            }
            if(fetchJsonHeaderObject.has(Constants.Fiscalyear)) {
                dbHeadTable.put(Constants.Fiscalyear, fetchJsonHeaderObject.getString(Constants.Fiscalyear));
            }
            /*if(fetchJsonHeaderObject.has(Constants.IS_UPDATE)) {
                dbHeadTable.put(Constants.IS_UPDATE, fetchJsonHeaderObject.getString(Constants.IS_UPDATE));
            }*/
            try {
                if(fetchJsonHeaderObject.has(Constants.CreatedOn)) {
                    if(!TextUtils.isEmpty(fetchJsonHeaderObject.getString(Constants.CreatedOn))) {
                        dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.getString(Constants.CreatedOn));
                    }
                }
                if(fetchJsonHeaderObject.has(Constants.CreatedBy)) {
                    if(!TextUtils.isEmpty(fetchJsonHeaderObject.getString(Constants.CreatedBy))) {
                        dbHeadTable.put(Constants.CreatedBy, fetchJsonHeaderObject.getString(Constants.CreatedBy));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String createdOn = "";
            try {
                if(fetchJsonHeaderObject.has(Constants.CreatedOn)) {
                    createdOn=fetchJsonHeaderObject.optString(Constants.CreatedOn);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String createdAt = "";
            try {
                if(fetchJsonHeaderObject.has(Constants.CreatedAt)) {
                    createdAt=fetchJsonHeaderObject.optString(Constants.CreatedAt);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            REPEATABLE_DATE = UtilConstants.getReArrangeDateFormat(createdOn) + Constants.T + UtilConstants.convertTimeOnly(createdAt);


            JSONArray itemsArraySPs = new JSONArray(fetchJsonHeaderObject.getString(Constants.CollectionPlanItem));
            JSONArray jsonArraySPs = new JSONArray();
            for (int incrementVal = 0; incrementVal < itemsArraySPs.length(); incrementVal++) {
                JSONObject singleRow = itemsArraySPs.getJSONObject(incrementVal);

                JSONObject itemObject = new JSONObject();
                if (singleRow.has(Constants.CollectionPlanItemGUID)) {
                    itemObject.put(Constants.CollectionPlanItemGUID, singleRow.get(Constants.CollectionPlanItemGUID));
                }
                if (singleRow.has(Constants.CollectionPlanGUID)) {
                    itemObject.put(Constants.CollectionPlanGUID, singleRow.get(Constants.CollectionPlanGUID));
                }
                if (singleRow.has(Constants.CollectionPlanDate)) {
                    itemObject.put(Constants.CollectionPlanDate, singleRow.get(Constants.CollectionPlanDate));
                }
                if (singleRow.has(Constants.CPNo)) {
                    itemObject.put(Constants.CPGUID, singleRow.get(Constants.CPNo));
                }
                if (singleRow.has(Constants.CPNo)) {
                    itemObject.put(Constants.CPNo, singleRow.get(Constants.CPNo));
                }
                if (singleRow.has(Constants.CPName)) {
                    itemObject.put(Constants.CPName, singleRow.get(Constants.CPName));
                }
                if (singleRow.has(Constants.Remarks)) {
                    itemObject.put(Constants.Remarks, singleRow.get(Constants.Remarks));
                }
                if (singleRow.has(Constants.PlannedValue)) {
                    itemObject.put(Constants.PlannedValue, singleRow.get(Constants.PlannedValue));
                }
                if (singleRow.has(Constants.AchievedValue)) {
                    itemObject.put(Constants.AchievedValue, singleRow.get(Constants.AchievedValue));
                }
                if (singleRow.has(Constants.Remarks)) {
                    itemObject.put(Constants.Remarks, singleRow.get(Constants.Remarks));
                }
                if (singleRow.has(Constants.Currency)) {
                    itemObject.put(Constants.Currency, singleRow.get(Constants.Currency));
                }
                if (singleRow.has(Constants.CrdtCtrlArea)) {
                    itemObject.put(Constants.CrdtCtrlArea, singleRow.get(Constants.CrdtCtrlArea));
                }
                if (singleRow.has(Constants.CrdtCtrlAreaDs)) {
                    itemObject.put(Constants.CrdtCtrlAreaDs, singleRow.get(Constants.CrdtCtrlAreaDs));
                }
                if (singleRow.has(Constants.CreatedOn)) {
                    if(!TextUtils.isEmpty(singleRow.getString(Constants.CreatedOn))) {
                        itemObject.put(Constants.CreatedOn, singleRow.get(Constants.CreatedOn));
                    }
                }
                if (singleRow.has(Constants.CreatedBy)) {
                    if(!TextUtils.isEmpty(singleRow.getString(Constants.CreatedBy))) {
                        itemObject.put(Constants.CreatedBy, singleRow.get(Constants.CreatedBy));
                    }
                }
//                if (singleRow.has(Constants.CPType)) {
                itemObject.put(Constants.CPType, "01");
//                }

                jsonArraySPs.put(itemObject);
            }
            dbHeadTable.put(CollectionPlanItemDetails, jsonArraySPs);
//            dbHeadTable.put(Constants.CPType, fetchJsonHeaderObject.getString(Constants.CPType));
//            dbHeadTable.put(Constants.CollectionPlanDate, fetchJsonHeaderObject.getString(Constants.CollectionPlanDate));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static Hashtable getRTGSHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        Hashtable dbHeadTable = new Hashtable();
        try {
            dbHeadTable.put(Constants.CollectionPlanGUID, fetchJsonHeaderObject.getString(Constants.CollectionPlanGUID));
            dbHeadTable.put(Constants.SPGUID, fetchJsonHeaderObject.getString(Constants.SPGUID));
            dbHeadTable.put(Constants.Period, fetchJsonHeaderObject.getString(Constants.Period));
            dbHeadTable.put(Constants.Fiscalyear, fetchJsonHeaderObject.getString(Constants.Fiscalyear));
            dbHeadTable.put(Constants.IS_UPDATE, fetchJsonHeaderObject.getString(Constants.IS_UPDATE));
            try {
                dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.getString(Constants.CreatedOn));
                dbHeadTable.put(Constants.CreatedBy, fetchJsonHeaderObject.getString(Constants.CreatedBy));
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            dbHeadTable.put(Constants.CPType, fetchJsonHeaderObject.getString(Constants.CPType));
//            dbHeadTable.put(Constants.CollectionPlanDate, fetchJsonHeaderObject.getString(Constants.CollectionPlanDate));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static boolean isSunday(Calendar calendar) {
        boolean isSunday = false;
        String[] strDays = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thusday",
                "Friday", "Saturday"};
        String mSunday = strDays[calendar.get(Calendar.DAY_OF_WEEK) - 1];
        if (mSunday.equalsIgnoreCase("Sunday")) {
            isSunday = true;
        }
        return isSunday;
    }

    public static void errorEditText(TextInputLayout spType, String errorMsg) {
        spType.setErrorEnabled(true);
        spType.setError(errorMsg);
    }

    public static boolean isReadWritePermissionEnabled(final Context mContext, Activity mActivity) {
        boolean isPermissionGranted = false;

        if (Build.VERSION_CODES.M <= android.os.Build.VERSION.SDK_INT) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                ) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.STORAGE_PERMISSION_CONSTANT);
                } else if (Constants.getPermissionStatus(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) || Constants.getPermissionStatus(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Constants.dialogBoxWithButton(mContext, "",
                            mContext.getString(R.string.this_app_needs_storage_permission), mContext.getString(R.string.enable),
                            mContext.getString(R.string.later), new DialogCallBack() {
                                @Override
                                public void clickedStatus(boolean clickedStatus) {
                                    if (clickedStatus) {
                                        Constants.navigateToAppSettingsScreen(mContext);
                                    }
                                }
                            });

                } else {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constants.STORAGE_PERMISSION_CONSTANT);
                }
                Constants.setPermissionStatus(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
            } else {
                //You already have the permission, just go ahead.
                isPermissionGranted = true;
            }
        } else {
            isPermissionGranted = true;
        }

        return isPermissionGranted;
    }

    public static void removePendingList(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);

        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (sharedPreferences.contains(Constants.InvList)) {
                editor.remove(Constants.InvList);
                editor.commit();
            }
            if (sharedPreferences.contains(Constants.CollList)) {
                editor.remove(Constants.CollList);
                editor.commit();
            }
            if (sharedPreferences.contains(Constants.SOList)) {
                editor.remove(Constants.SOList);
                editor.commit();
            }
            if (sharedPreferences.contains(Constants.SalesOrderDataValt)) {
                editor.remove(Constants.SalesOrderDataValt);
                editor.commit();
            }
            if (sharedPreferences.contains(Constants.FeedbackList)) {
                editor.remove(Constants.FeedbackList);
                editor.commit();
            }
            if (sharedPreferences.contains(Constants.SOUpdate)) {
                editor.remove(Constants.SOUpdate);
                editor.commit();
            }
            if (sharedPreferences.contains(Constants.Expenses)) {
                editor.remove(Constants.Expenses);
                editor.commit();
            }
            if (sharedPreferences.contains(Constants.SOCancel)) {
                editor.remove(Constants.SOCancel);
                editor.commit();
            }
            if (sharedPreferences.contains(Constants.MTPDataValt)) {
                editor.remove(Constants.MTPDataValt);
                editor.commit();
            }
            if (sharedPreferences.contains(Constants.RTGSDataValt)) {
                editor.remove(Constants.RTGSDataValt);
                editor.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static String makePendingDataToJsonString(Context context) {
        String mStrJson = "";
        ArrayList<Object> objectArrayList = getPendingDataVaultData(context);
        if (!objectArrayList.isEmpty()) {
            String[][] invKeyValues = (String[][]) objectArrayList.get(1);
            JSONArray jsonArray = new JSONArray();
            for (int k = 0; k < invKeyValues.length; k++) {
                JSONObject jsonObject = new JSONObject();
                String store = "";
                try {
                    store = ConstantsUtils.getFromDataVault(invKeyValues[k][0].toString(),context);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                try {
                    // Add the values to the jsonObject
                    jsonObject.put(Constants.KeyNo, invKeyValues[k][0]);
                    jsonObject.put(Constants.KeyType, invKeyValues[k][1]);
                    jsonObject.put(Constants.KeyValue, store);
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put(DataVaultData, jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mStrJson = jsonObj.toString();
        }
        return mStrJson;
    }

    public static ArrayList<Object> getPendingDataVaultData(Context mContext) {
        ArrayList<Object> objectsArrayList = new ArrayList<>();
        int mIntPendingCollVal = 0;
        String[][] invKeyValues = null;
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        set = sharedPreferences.getStringSet(Constants.CollList, null);
        invKeyValues = new String[SyncSelectionActivity.getPendingListSize(mContext)][2];
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.CollList;
                mIntPendingCollVal++;
            }
        }
        set = sharedPreferences.getStringSet(Constants.SalesOrderDataValt, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.SalesOrderDataValt;
                mIntPendingCollVal++;
            }
        }
        set = sharedPreferences.getStringSet(Constants.MTPDataValt, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.MTPDataValt;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.RTGSDataValt, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.RTGSDataValt;
                mIntPendingCollVal++;
            }
        }
        set = sharedPreferences.getStringSet(Constants.SOCancel, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.SOCancel;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.SOUpdate, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.SOUpdate;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.Expenses, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.Expenses;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.InvList, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.InvList;
                mIntPendingCollVal++;
            }
        }

        if (mIntPendingCollVal > 0) {
            objectsArrayList.add(mIntPendingCollVal);
            objectsArrayList.add(invKeyValues);
        }

        return objectsArrayList;

    }

    public static boolean isFileExits(String fileName) {
        boolean isFileExits = false;
        try {
            File sdCardDir = Environment.getExternalStorageDirectory();
            // Get The Text file
            File txtFile = new File(sdCardDir, fileName);
            // Read the file Contents in a StringBuilder Object
            if (txtFile.exists()) {
                isFileExits = true;
            } else {
                isFileExits = false;
            }
        } catch (Exception e) {
            isFileExits = false;
            e.printStackTrace();
            LogManager.writeLogError("isFileExits() : " + e.getMessage());
        }
        return isFileExits;
    }

    public static String getTextFileData(String fileName) {
        // Get the dir of SD Card
        File sdCardDir = Environment.getExternalStorageDirectory();
        // Get The Text file
        File txtFile = new File(sdCardDir, fileName);
        // Read the file Contents in a StringBuilder Object
        StringBuilder text = new StringBuilder();
        if (txtFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(txtFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    text.append(line);
                }
                reader.close();
            } catch (IOException e) {
                Log.e("C2c", "Error occured while reading text file!!");
                LogManager.writeLogError("getTextFileData() : (IOException)" + e.getMessage());
            }
        } else {
            text.append("");
        }
        return text.toString();
    }

    public static void setJsonStringDataToDataVault(String mJsonString, Context context) {
        try {
            JSONObject jsonObj = new JSONObject(mJsonString);
            // Getting data JSON Array nodes
            JSONArray jsonArray = jsonObj.getJSONArray(DataVaultData);
            for (int incVal = 0; incVal < jsonArray.length(); incVal++) {
                JSONObject jsonObject = jsonArray.getJSONObject(incVal);
                String mStrKeyNo = jsonObject.getString(KeyNo);
                String mStrKeyKeyType = jsonObject.getString(KeyType);
                String mStrKeyValue = jsonObject.getString(KeyValue);
                Constants.saveDeviceDocNoToSharedPref(context, mStrKeyKeyType, mStrKeyNo);
                ConstantsUtils.storeInDataVault(mStrKeyNo, mStrKeyValue,context);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void setSyncTime(Context context,String refGuid) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME,
                0);
        if (settings.getBoolean(Constants.isReIntilizeDB, false)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(Constants.isReIntilizeDB, false);
            editor.commit();
            try {
                try {
                    Constants.createSyncDatabase(context);  // create sync history table
                } catch (Exception e) {
                    e.printStackTrace();
                }
                List<String> DEFINGREQARRAY = Arrays.asList(Constants.getDefinigReq(context));
               /* String syncTime = UtilConstants.getSyncHistoryddmmyyyyTime();
                String[] DEFINGREQARRAY = Constants.getDefinigReq(context);


                for (int incReq = 0; incReq < DEFINGREQARRAY.length; incReq++) {
                    String colName = DEFINGREQARRAY[incReq];
                    if (colName.contains("?$")) {
                        String splitCollName[] = colName.split("\\?");
                        colName = splitCollName[0];
                    }

                    Constants.events.updateStatus(Constants.SYNC_TABLE,
                            colName, Constants.TimeStamp, syncTime
                    );
                }*/
                Constants.updateSyncTime(DEFINGREQARRAY, context, Constants.Sync_All,refGuid);
            } catch (Exception exce) {
                LogManager.writeLogError(Constants.sync_table_history_txt + exce.getMessage());
            }
        }
    }

    /*public static String getNameSpaceOnline(OnlineODataStore oDataOfflineStore) {
        String mStrNameSpace = "";
        ODataMetadata oDataMetadata = null;

        try {
            oDataMetadata = oDataOfflineStore.getMetadata();
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        Set set = oDataMetadata.getMetaNamespaces();
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();

            while (itr.hasNext()) {
                String tempNameSpace = itr.next().toString();
                if (!tempNameSpace.equalsIgnoreCase("OfflineOData")) {
                    mStrNameSpace = tempNameSpace;
                }
            }
        }

        return mStrNameSpace;
    }*/

    public static ArrayList<String> getDefinigReqList(Context mContext) {
        ArrayList<String> alAssignColl = new ArrayList<>();
        String[] DEFINGREQARRAY = getDefinigReq(mContext);
        for (String collectionName : DEFINGREQARRAY) {
            if (collectionName.contains("?")) {
                String splitCollName[] = collectionName.split("\\?");
                collectionName = splitCollName[0];
            }
            alAssignColl.add(collectionName);
        }
        return alAssignColl;
    }

    public static String appendPrecedingZeros(String mStrInputNo, int stringLength) {
        String mfinalString = "";
        try {
            if (mStrInputNo != null && !mStrInputNo.equalsIgnoreCase("")) {
                try {
                    int numberOfDigits = mStrInputNo.length();
                    int numberOfLeadingZeroes = stringLength - numberOfDigits;
                    StringBuilder sb = new StringBuilder();
                    if (numberOfLeadingZeroes > 0) {
                        for (int i = 0; i < numberOfLeadingZeroes; i++) {
                            sb.append("0");
                        }
                    }
                    sb.append(mStrInputNo);
                    mfinalString = sb.toString();
                } catch (Exception e) {
                    mfinalString = mStrInputNo;
                    e.printStackTrace();
                }
            } else {
                mfinalString = "";
            }
        } catch (Exception e) {
            mfinalString = "";
            e.printStackTrace();
        }
        return mfinalString;
    }

    public static String trimLeadingZeros(String source) {
        for (int i = 0; i < source.length(); ++i) {
            char c = source.charAt(i);
            if (c != '0') {
                return source.substring(i);
            }
        }
        return ""; // or return "0";
    }

    public static HashMap<String, MyTargetsBean> getUOMBasedMaterialGrp(Context mContext, String mStrCurrentDate) {
        HashMap<String, MyTargetsBean> mapQtyBasedOnUOM = new HashMap<>();

        String mStrQry = "";
        try {
            mStrQry = OfflineManager.makeSOsQry(Constants.SOs + "?$filter= " + Constants.OrderDate +
                    " eq datetime'" + UtilConstants.getNewDate() + "' ", Constants.SONo);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if (!Constants.mapMatGrpBasedOnUOM.isEmpty()) {
            Iterator iterator = Constants.mapMatGrpBasedOnUOM.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                ArrayList<String> alMatGrp = Constants.mapMatGrpBasedOnUOM.get(key);
                if (alMatGrp != null && alMatGrp.size() > 0) {
                    String makeSOItemQry = Constants.makeCPQry(alMatGrp, Constants.MaterialGroup);
                    String mStrOrderQty = "0.00";
                    Double mDouCalQty = 0.00, mDouDeviceQty = 0.00;
                    if (!mStrQry.equalsIgnoreCase("")) {


                        try {
                            if (!mStrQry.equalsIgnoreCase("") && !makeSOItemQry.equalsIgnoreCase("")) {
                                if (!OfflineManager.checkNoUOMZero(key)) {
                                    mStrOrderQty = OfflineManager.getTotalSumByCondition("" + Constants.SOItemDetails +
                                            "?$select=" + Constants.AlternateWeight + " &$filter= " + mStrQry + " and (" + makeSOItemQry + ") ", Constants.AlternateWeight);
                                } else {
                                    mStrOrderQty = OfflineManager.getTotalSumByCondition("" + Constants.SOItemDetails +
                                            "?$select=" + Constants.Quantity + " &$filter= " + mStrQry + " and (" + makeSOItemQry + ") ", Constants.Quantity);
                                }
                            }
                        } catch (OfflineODataStoreException e) {
                            mStrOrderQty = "0.00";
                            e.printStackTrace();
                        }
                    }
                    try {
                        if (!OfflineManager.checkNoUOMZero(key)) {
                            mDouDeviceQty = OfflineManager.getDeviceTotalQtyBasedOnMaterialGrp(Constants.SalesOrderDataValt, mContext, mStrCurrentDate, alMatGrp);
                        } else {
                            mDouDeviceQty = OfflineManager.getDeviceTotalQtyBasedOnMaterialGrpVenus(Constants.SalesOrderDataValt, mContext, mStrCurrentDate, alMatGrp);
                        }

                    } catch (Exception e) {
                        mDouDeviceQty = 0.00;
                        e.printStackTrace();
                    }

                    try {
                        mDouCalQty = Double.parseDouble(mStrOrderQty) + mDouDeviceQty;
                    } catch (NumberFormatException e) {
                        mDouCalQty = 0.00;
                        e.printStackTrace();
                    }

                    MyTargetsBean myTargetsBean = new MyTargetsBean();
                    myTargetsBean.setMTDA(mDouCalQty + "");
                    mapQtyBasedOnUOM.put(key, myTargetsBean);
                }

            }
        }
        return mapQtyBasedOnUOM;
    }

    public static HashMap<String, MyTargetsBean> getKPICodeBasedMaterialGrp(Context mContext, String mStrCurrentDate) {
        HashMap<String, MyTargetsBean> mapQtyBasedOnUOM = new HashMap<>();

        String mStrQry = "";
        try {
            mStrQry = OfflineManager.makeSOsQry(Constants.SOs + "?$filter= " + Constants.OrderDate +
                    " eq datetime'" + UtilConstants.getNewDate() + "' ", Constants.SONo);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }


        HashMap<String, MyTargetsBean> mapMatGrpBasedOnUOM = new HashMap<>();
        try {
            mapMatGrpBasedOnUOM = OfflineManager.getConfigTypeValuesForMonthlyKPI("ConfigTypsetTypeValues?$filter=Typeset eq 'MTLYTG'");
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        if (!mapMatGrpBasedOnUOM.isEmpty()) {
            Iterator iterator = mapMatGrpBasedOnUOM.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                MyTargetsBean bean = mapMatGrpBasedOnUOM.get(key);
                if (bean != null) {
                    String uom = bean.getUOM();
                    ArrayList<String> alMatGrp = bean.getArrayList();
                    if (alMatGrp != null && alMatGrp.size() > 0) {
                        String makeSOItemQry = Constants.makeCPQry(alMatGrp, Constants.MaterialGroup);
                        String mStrOrderQty = "0.00";
                        Double mDouCalQty = 0.00, mDouDeviceQty = 0.00;
                        if (!mStrQry.equalsIgnoreCase("")) {


                            try {
                                if (!mStrQry.equalsIgnoreCase("") && !makeSOItemQry.equalsIgnoreCase("")) {
                                    if (!OfflineManager.checkNoUOMZero(uom)) {
                                        mStrOrderQty = OfflineManager.getTotalSumByConditionHighLevel("" + Constants.SOItemDetails +
                                                "?$select=" + Constants.AlternateWeight+","+Constants.HighLevellItemNo+","+Constants.RejReason+ " &$filter= " + mStrQry + " and (" + makeSOItemQry + ") ", Constants.AlternateWeight);
                                    } else {
                                        mStrOrderQty = OfflineManager.getTotalSumByConditionHighLevel("" + Constants.SOItemDetails +
                                                "?$select=" + Constants.Quantity+","+Constants.HighLevellItemNo +","+Constants.RejReason+ " &$filter= " + mStrQry + " and (" + makeSOItemQry + ") ", Constants.Quantity);
                                    }
                                }
                            } catch (OfflineODataStoreException e) {
                                mStrOrderQty = "0.00";
                                e.printStackTrace();
                            }
                        }
                        try {
                            if (!OfflineManager.checkNoUOMZero(uom)) {
                                mDouDeviceQty = OfflineManager.getDeviceTotalQtyBasedOnMaterialGrp(Constants.SalesOrderDataValt, mContext, mStrCurrentDate, alMatGrp);
                            } else {
                                mDouDeviceQty = OfflineManager.getDeviceTotalQtyBasedOnMaterialGrpVenus(Constants.SalesOrderDataValt, mContext, mStrCurrentDate, alMatGrp);
                            }

                        } catch (Exception e) {
                            mDouDeviceQty = 0.00;
                            e.printStackTrace();
                        }

                        try {
                            mDouCalQty = Double.parseDouble(mStrOrderQty) + mDouDeviceQty;
                        } catch (NumberFormatException e) {
                            mDouCalQty = 0.00;
                            e.printStackTrace();
                        }

                        MyTargetsBean myTargetsBean = new MyTargetsBean();
                        myTargetsBean.setMTDA(mDouCalQty + "");
                        mapQtyBasedOnUOM.put(key, myTargetsBean);
                    }

                }
            }
        }
        return mapQtyBasedOnUOM;
    }

    public static String makeCPQry(ArrayList<String> alRetailers, String columnName) {
        String mCPQry = "";
        for (String cpNo : alRetailers) {
            if (mCPQry.length() == 0)
                mCPQry += " " + columnName + " eq '" + cpNo + "'";
            else
                mCPQry += " or " + columnName + " eq '" + cpNo + "'";

        }

        return mCPQry;
    }

    public static ArrayList<String> getEntityNames() {
        ArrayList<String> alEntityList = new ArrayList<>();
        alEntityList.add(Constants.CollList);
        alEntityList.add(Constants.SalesOrderDataValt);
        alEntityList.add(Constants.SOUpdate);
        alEntityList.add(Constants.SOCancel);
        alEntityList.add(Constants.Expenses);
        alEntityList.add(Constants.MTPDataValt);
        alEntityList.add(Constants.RTGSDataValt);
        return alEntityList;
    }

    public static String getCountryCode(Context mContext) {
        String mConCode = "91";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            String countryIso = telephonyManager.getSimCountryIso().toUpperCase();
            mConCode = PhoneNumberUtil.getInstance().getCountryCodeForRegion(countryIso) + "";
        } catch (Exception e) {
            mConCode = "91";
            e.printStackTrace();
        }
        return mConCode;
    }

    public static void createLatLong(Map<String, String> mapTable, Context context, UIListener uiListener, String columnID) {
        try {
            Thread.sleep(100);

            GUID guid = GUID.newRandom();
            Hashtable hashtable = new Hashtable();
            hashtable.put(Constants.GeoGUID, guid.toString().toUpperCase());
            int tempLongitude = 0;
            int tempLatitude = 0;
            if (mapTable.get(Constants.Longitude) != null && !TextUtils.isEmpty(mapTable.get(Constants.Longitude))) {
                try {
                    tempLongitude = (int) Double.parseDouble(mapTable.get(Constants.Longitude));
                    if (tempLongitude != 0) {
                        hashtable.put(Constants.Longitude, BigDecimal.valueOf(UtilConstants.round(Double.parseDouble(mapTable.get(Constants.Longitude)), 12)));
                    }
                } catch (NumberFormatException e) {
                    tempLongitude = 0;
                    LogManager.writeLogInfo("Exception Long : " + mapTable.get(Constants.Longitude));
//                    hashtable.put(Constants.Longitude, BigDecimal.valueOf(Double.parseDouble("98.00")));
                    e.printStackTrace();
                }
            }
            if (mapTable.get(Constants.Latitude) != null && !TextUtils.isEmpty(mapTable.get(Constants.Latitude))) {
//                UtilConstants.round(Double.parseDouble(mapTable.get(Constants.Latitude)), 12);
                try {
                    tempLatitude = (int) Double.parseDouble(mapTable.get(Constants.Latitude));
                    if (tempLatitude != 0) {
                        hashtable.put(Constants.Latitude, BigDecimal.valueOf(UtilConstants.round(Double.parseDouble(mapTable.get(Constants.Latitude)), 12)));
                    }
                } catch (NumberFormatException e) {
                    tempLatitude = 0;
                    LogManager.writeLogInfo("Exception Lat : " + mapTable.get(Constants.Latitude));
//                    hashtable.put(Constants.Latitude, BigDecimal.valueOf(Double.parseDouble("99.00")));
                    e.printStackTrace();
                }
            }
            hashtable.put(Constants.GeoDate, mapTable.get(Constants.GeoDate));
            if (!TextUtils.isEmpty(mapTable.get(Constants.GeoTime))) {
                ODataDuration startDuration = Constants.getTimeAsODataDurationConvertionLocation(mapTable.get(Constants.GeoTime));
                hashtable.put(Constants.GeoTime, startDuration);
            }
//            hashtable.put(Constants.GeoTime,mapTable.get(Constants.GeoTime));
            hashtable.put(Constants.SPGUID, mapTable.get(Constants.SPGUID));
            hashtable.put(Constants.LoginID, "");
            hashtable.put(Constants.SPNO, "");
            hashtable.put(Constants.SPName, "");
            hashtable.put(Constants.Reason, "");
            hashtable.put(Constants.ReasonDesc, "");
//            hashtable.put(Constants.Remarks,mapTable.get(Constants.Remarks));
            try {
                if (!TextUtils.isEmpty(mapTable.get(Constants.Distance))) {
                    hashtable.put(Constants.Distance, ConstantsUtils.decimalRoundOff(BigDecimal.valueOf(Double.parseDouble(mapTable.get(Constants.Distance))), 2));
                } else {
                    hashtable.put(Constants.Distance, ConstantsUtils.decimalRoundOff(BigDecimal.valueOf(Double.parseDouble("0.00")), 2));
                }
            } catch (Exception e) {
                e.printStackTrace();
                hashtable.put(Constants.Distance, ConstantsUtils.decimalRoundOff(BigDecimal.valueOf(Double.parseDouble("0.00")), 2));
            }
            hashtable.put(Constants.DistanceUOM, mapTable.get(Constants.DistanceUOM));
            try {
                if (!TextUtils.isEmpty(mapTable.get(Constants.BatteryPerc))) {
                    hashtable.put(Constants.BatteryPerc, ConstantsUtils.decimalRoundOff(BigDecimal.valueOf(Double.parseDouble(mapTable.get(Constants.BatteryPerc))), 2));
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }


            try {
//            if(!TextUtils.isEmpty(mapTable.get(Constants.IMEI1))) {
                hashtable.put(Constants.IMEI1, mapTable.get(Constants.IMEI1));
//            }
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                //            if(!TextUtils.isEmpty(mapTable.get(Constants.IMEI2))) {
                hashtable.put(Constants.IMEI2, mapTable.get(Constants.IMEI2));
//            }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (!TextUtils.isEmpty(mapTable.get(Constants.APKVersion))) {
                    hashtable.put(Constants.APKVersion, mapTable.get(Constants.APKVersion));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (!TextUtils.isEmpty(mapTable.get(Constants.APKVersionCode))) {
                    hashtable.put(Constants.APKVersionCode, mapTable.get(Constants.APKVersionCode));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (!TextUtils.isEmpty(mapTable.get(Constants.MobileNo11))) {
                    hashtable.put(Constants.MobileNo11, mapTable.get(Constants.MobileNo11));
                } else {
                    hashtable.put(Constants.MobileNo11, mapTable.get(Constants.MobileNo11));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

//            hashtable.put(Constants.Remarks,getDeviceName() + " (" + mapTable.get(Constants.AppVisibility) + ")");
            if (tempLatitude != 0 && tempLongitude != 0) {
                OfflineManager.CreateLatLong(hashtable, uiListener, columnID, context);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }



    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static void getDataFromSqliteDB(Context context, UIListener rListener) {
        //logStatusToStorage("Step:8 getDataFromSqliteDB ");
        LocationBean locationBean = null;
        ArrayList<LocationBean> alLocationBeans = new ArrayList<>();
        DatabaseHelperGeo databaseHelper = DatabaseHelperGeo.getInstance(context);
        Cursor cursor = databaseHelper.getDataLatLong();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                locationBean = new LocationBean();
                locationBean.setCOLUMN_ID(cursor.getString(0));
                locationBean.setColumnLat(cursor.getString(3));
                locationBean.setColumnLong(cursor.getString(4));
                locationBean.setColumnStartdate(cursor.getString(5));
                locationBean.setColumnStarttime(cursor.getString(6));

//                DateTime date = DateTime.parse("04/02/2011 20:27:05",
//                        DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss"));
//                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                SimpleDateFormat sdf1 = new SimpleDateFormat("HH-mm-ss");
                String time = "";
                try {
                    Date d = DateFormat.getDateTimeInstance().parse(cursor.getString(9));
                    time = sdf1.format(d);

                } catch (ParseException ex) {
                    ex.printStackTrace();
                    Log.v("Exception", ex.getLocalizedMessage());
                }

//                locationBean.setCOLUMN_Status(cursor.getString(6));
//                locationBean.setColumnTempno(cursor.getString(7));
                locationBean.setColumnTimestamp(time);
                locationBean.setCOLUMN_AppVisibility(cursor.getString(10));
                locationBean.setCOLUMN_BATTERYLEVEL(cursor.getString(11));
                try {
                    locationBean.setCOLUMN_DISTANCE(cursor.getString(12));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                alLocationBeans.add(locationBean);
            }
            //  logStatusToStorage("Step:9 Location list size"+String.valueOf(alLocationBeans.size()));
            updataLatLong(alLocationBeans, context, rListener);
        } else {
            if (cursor != null)
                LocationMonitoringService.locationLog(" SQL db record count " + cursor.getCount());
        }
    }

    public static void updataLatLong(ArrayList<LocationBean> alLocationBeans, final Context context, final UIListener uListener) {

        UIListener listener = new UIListener() {
            @Override
            public void onRequestError(int i, Exception e) {
                Log.i("LocationCapture", "Lat-Long(Error)");
            }

            @Override
            public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                Log.i("LocationCapture", "Lat-Long(Stored Successfully)");
                //  logStatusToStorage("Step Final Location Stored");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (reentrantLock == null) {
                            reentrantLock = new ReentrantLock();
                        }
                        try {
                            Log.e("REENTRANT:", "LOCKED");
                            reentrantLock.lock();
                            int qry = OfflineManager.getCount(Constants.SPGeos);
                            // LocationMonitoringService.locationLog("Offline DB Count"+String.valueOf(qry));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("FOUND EXCEPTION", "ANR EXCEPTION OCCURRED");
                        } finally {
                            if (reentrantLock != null && reentrantLock.isHeldByCurrentThread()) {
                                reentrantLock.unlock();
                            }
                            Log.e("REENTRANT:", "UNLOCKED FINALLY");
                        }
                    }
                }).start();
            }
        };

        // logStatusToStorage("Step :10 Adding all Location to map");
        if (alLocationBeans != null && alLocationBeans.size() > 0) {
            String mStrSPGUID = Constants.getSPGUID(Constants.SPGUID);
            String qry = Constants.SalesPersons + "?$filter=" + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
            String mobileNo = "";
            List<SalesPersonBean> salesPersonBeanList = new ArrayList<>();
            SalesPersonBean salesPersonBean = null;
            try {
                salesPersonBeanList = OfflineManager.getSalesPerson(qry);
                if (salesPersonBeanList.size() > 0 && salesPersonBeanList != null) {
                    salesPersonBean = salesPersonBeanList.get(0);
                }
                if (salesPersonBean != null) {
                    mobileNo = salesPersonBean.getMobileNo();
                }

            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            String imeiSIM1 = "";
            String imeiSIM2 = "";

            try {
                int telephone = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
                if (telephone == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));

                    try {
                        imeiSIM1 = telephonyManager.getDeviceId(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        imeiSIM2 = telephonyManager.getDeviceId(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int k = 0; k < alLocationBeans.size(); k++) {
                mapTable = new HashMap<>();
                LocationBean locationBean = alLocationBeans.get(k);
                String columnID = locationBean.getCOLUMN_ID();
                mapTable.put(Constants.Latitude, locationBean.getColumnLat());
                mapTable.put(Constants.Longitude, locationBean.getColumnLong());
                mapTable.put(Constants.GeoDate, locationBean.getColumnStartdate());
                mapTable.put(Constants.GeoTime, locationBean.getColumnTimestamp());
                mapTable.put(Constants.SPGUID, Constants.getSPGUID(Constants.SPGUID));
                mapTable.put(Constants.BatteryPerc, locationBean.getCOLUMN_BATTERYLEVEL());
//                mapTable.put(Constants.Remarks, locationBean.getCOLUMN_DISTANCE());
                try {
                    mapTable.put(Constants.Distance, locationBean.getCOLUMN_DISTANCE());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    mapTable.put(Constants.DistanceUOM, "M");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    mapTable.put(Constants.APKVersion, BuildConfig.VERSION_NAME);
                    mapTable.put(Constants.APKVersionCode, String.valueOf(BuildConfig.VERSION_CODE));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (!TextUtils.isEmpty(mobileNo)) {
                        mapTable.put(Constants.MobileNo11, mobileNo);
                    } else {
                        mapTable.put(Constants.MobileNo11, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    mapTable.put(Constants.IMEI1, imeiSIM1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    mapTable.put(Constants.IMEI2, imeiSIM2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                mapTable.put(Constants.AppVisibility, locationBean.getCOLUMN_AppVisibility());

                if (uListener != null)
                    createLatLong(mapTable, context, uListener, columnID);
                else
                    createLatLong(mapTable, context, listener, columnID);


            }
            // logStatusToStorage("Step :11 Adding all Location to map ends");
        }
    }

    public static void setScheduleAlaram(Context context, int hours,
                                         int minuts, int seconds, int date) {
        Calendar calNow = new GregorianCalendar();
        calNow.setTimeInMillis(System.currentTimeMillis());  // Set current time

        Calendar calSet = new GregorianCalendar();
//        cal.add(Calendar.DAY_OF_YEAR, cur_cal.get(Calendar.DAY_OF_YEAR));
        calSet.set(Calendar.HOUR_OF_DAY, hours);
        calSet.set(Calendar.MINUTE, minuts);
//        cal.set(Calendar.AM_PM,Calendar.PM);
//        cal.set(Calendar.SECOND, cur_cal.get(Calendar.SECOND));
//        cal.set(Calendar.MILLISECOND, cur_cal.get(Calendar.MILLISECOND));
//        cal.set(Calendar.DATE, cur_cal.get(Calendar.DATE));
//        cal.set(Calendar.MONTH, cur_cal.get(Calendar.MONTH));
        if (calSet.compareTo(calNow) <= 0) {
            //Today Set time passed, count to tomorrow
            calSet.add(Calendar.DATE, date);
        }
        Intent intent = new Intent(context, AlaramRecevier.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pendingIntent);
        }

        Log.d("LocationServiceCapture", "Alaram enabled");
    }

    public static void closeStore(Context context) {
        try {
            UtilConstants.closeStore(context,
                    OfflineManager.options, "-100036",
                    offlineStore, Constants.PREFS_NAME, "");

        } catch (Exception e) {
            e.printStackTrace();
        }
//            Constants.Entity_Set.clear();
//            Constants.AL_ERROR_MSG.clear();
        offlineStore = null;
        OfflineManager.options = null;
    }

    public static void createSyncHistory(String collectionName, String syncTime, String syncType, String StrSPGUID32, String parternTypeID, String loginId,String refGuid) {
        try {
            Thread.sleep(100);
            if (collectionName.equalsIgnoreCase("ConfigTypsetTypeValues") && syncType.equals(Constants.UpLoad)) {
                syncType = Constants.DownLoad;
            }

            GUID guid = GUID.newRandom();
            Hashtable hashtable = new Hashtable();
            if(!TextUtils.isEmpty(collectionName) && (collectionName.contains("End") || collectionName.contains("Cancel")))
                hashtable.put(Constants.SyncHisGuid, guid);
            else
                hashtable.put(Constants.SyncHisGuid, refGuid);
            if (!collectionName.equals("") && collectionName != null) {
                hashtable.put(Constants.SyncCollection, collectionName);

                if (Constants.writeDebug)
                    LogManager.writeLogDebug("Create Sync history : " + collectionName);
            }
            hashtable.put(Constants.SyncApplication, BuildConfig.APPLICATION_ID);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH-mm-ss");
            String time = "";
            String strDate = "";
            try {
                Date date = dateFormat.parse(syncTime);
                strDate = dateFormat.format(date);
                time = timeFormat.format(date.parse(syncTime));

            } catch (ParseException ex) {
                ex.printStackTrace();
                Log.v("Exception", ex.getLocalizedMessage());

                LogManager.writeLogDebug("Creating Time exception:" + ex.getLocalizedMessage());
            }
            ODataDuration startDuration = null;
            try {
                if (!time.isEmpty()) {
                    startDuration = Constants.getTimeAsODataDurationConvertion(time);
                    hashtable.put(Constants.SyncHisTime, startDuration);
                } else {
                    hashtable.put(Constants.SyncHisTime, startDuration);
                }


            } catch (Exception e) {
                e.printStackTrace();
                LogManager.writeLogDebug("Creating startDuration exception:" + e.getLocalizedMessage());
            }
            hashtable.put(Constants.SyncDate, strDate);

            hashtable.put(Constants.SyncType, syncType);

            hashtable.put(Constants.PartnerId, StrSPGUID32);
            hashtable.put(Constants.PartnerType, parternTypeID);
            hashtable.put(Constants.LoginId, loginId);
            hashtable.put(Constants.RefGUID, refGuid);
//            hashtable.put(Constants.Remarks,getDeviceName() + " (" + mapTable.get(Constants.AppVisibility) + ")");
            OfflineManager.CreateSyncHistroy(hashtable);
//            itemtable.add(hashtable);
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.writeLogDebug("Create Sync history failed: " + e.getLocalizedMessage());
        }
    }

    public static Hashtable createSyncHistoryBatch(String collectionName, String syncTime, String syncType, String StrSPGUID32, String parternTypeID, String loginId,String refguid) {
        Hashtable hshtable = new Hashtable();
        try {
            Thread.sleep(100);

            if (collectionName.equalsIgnoreCase("ConfigTypsetTypeValues") && syncType.equals(Constants.UpLoad)) {
                syncType = Constants.DownLoad;
            }else if(syncType.equals(Constants.Attnd_refresh_sync) || syncType.equals(Constants.SOPD_sync)){
                syncType = Constants.UpLoad;
            }
            else if(!syncType.equals(Constants.Sync_All) &&  !syncType.equals(Constants.DownLoad) &&
                    !syncType.equals(Constants.UpLoad)  &&  !syncType.equals(Constants.Auto_Sync)){
                syncType = Constants.DownLoad;
            }

            GUID guid = GUID.newRandom();
            hshtable.put(Constants.SyncHisGuid, guid.toString().toUpperCase());
            if (!collectionName.equals("") && collectionName != null) {
                hshtable.put(Constants.SyncCollection, collectionName);
            }
            hshtable.put(Constants.SyncApplication, BuildConfig.APPLICATION_ID);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH-mm-ss");
            String time = "";
            String strDate = "";
            try {
                Date date = dateFormat.parse(syncTime);
                strDate = dateFormat.format(date);
                time = timeFormat.format(date.parse(syncTime));

            } catch (ParseException ex) {
                ex.printStackTrace();
                Log.v("Exception", ex.getLocalizedMessage());
            }
            ODataDuration startDuration = null;
            try {
                if (!time.isEmpty()) {
                    startDuration = Constants.getTimeAsODataDurationConvertion(time);
                    hshtable.put(Constants.SyncHisTime, startDuration);
                } else {
                    hshtable.put(Constants.SyncHisTime, startDuration);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            hshtable.put(Constants.SyncDate, strDate);

            hshtable.put(Constants.SyncType, syncType);

            hshtable.put(Constants.PartnerId, StrSPGUID32);
            hshtable.put(Constants.PartnerType, parternTypeID);
            hshtable.put(Constants.LoginId, loginId);
            hshtable.put(Constants.RefGUID, refguid);
//            hshtable.put(Constants.Remarks,getDeviceName() + " (" + mapTable.get(Constants.AppVisibility) + ")");
//            OfflineManager.CreateSyncHistroy(hshtable);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return hshtable;
    }

    public static Calendar convertDateFormat1(String dateVal) {
        Date date = null;
        Calendar curCal = new GregorianCalendar();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        try {
            date = format.parse(dateVal);
            curCal.setTime(date);
            System.out.println("Date" + curCal.getTime());
        } catch (ParseException var5) {
            var5.printStackTrace();
        }

        return curCal;
    }

    public static void updateSyncTime(final List<String> alAssignColl, final Context context, final String syncType,final String refGuid) {
        String strSPGUID = Constants.getSPGUID(Constants.SPGUID);
//        String StrSPGUID32 = "";
//        String parternTypeID = "";

        if (!TextUtils.isEmpty(strSPGUID)) {
            StrSPGUID32 = strSPGUID.replaceAll("-", "");
            try {
                parternTypeID = OfflineManager.getPartnerTypeID(Constants.UserPartners + "?$filter= PartnerID eq'" + StrSPGUID32 + "'");
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
                LogManager.writeLogInfo(" updating sync history : exception" + e.getLocalizedMessage());
            }
        }
//        if (!TextUtils.isEmpty(strSPGUID)) {
//            StrSPGUID32 = strSPGUID.replaceAll("-", "");
//        }
//       parternTypeID = context.getSharedPreferences(Constants.PREFS_NAME,0).getString(Constants.USERPARNTERID,"");
        final boolean checkSyncHistoryColl = getSyncHistoryColl(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        final String loginId = sharedPreferences.getString("username", "");
        final String syncTime = Constants.getSyncHistoryddmmyyyyTime();
        final ODataRequestParamBatch requestParamBatch = new ODataRequestParamBatchDefaultImpl();
        LogManager.writeLogInfo(" updating sync history :" + syncTime);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (reentrantLock == null) {
                    reentrantLock = new ReentrantLock();
                }
                reentrantLock.lock();
                Log.e("Sync Histroy REENTRANT:", "LOCKED");
                try {
//                    itemtable = new ArrayList<>();
                    for (int incReq = 0; incReq < alAssignColl.size(); incReq++) {
                        colName = alAssignColl.get(incReq);
                        if (colName.contains("?$")) {
                            String splitCollName[] = colName.split("\\?");
                            colName = splitCollName[0];
                            LogManager.writeLogInfo(" collection names :" + colName);
                        }
                        Constants.events.updateStatus(Constants.SYNC_TABLE,
                                colName, Constants.TimeStamp, syncTime
                        );

                        if (checkSyncHistoryColl) {
                            if (!colName.equalsIgnoreCase(Constants.SPGeos)) {
                                try {
//                                Constants.createSyncHistory(colName, syncTime, syncType, StrSPGUID32, parternTypeID, loginId);
                                    Hashtable hashtable = Constants.createSyncHistoryBatch(colName, syncTime, syncType, StrSPGUID32, parternTypeID, loginId, refGuid);
                                    ODataEntity channelPartnerEntity = null;
                                    try {
                                        Log.d("Sync History", "insert RefGuid-::" + hashtable.get(Constants.RefGUID) + "--" + hashtable.get(Constants.SyncCollection));

                                        channelPartnerEntity = OfflineManager.createSyncHistroyEntity(hashtable);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    int id = incReq + 1;
                                    String contentId = String.valueOf(id);
                                    ODataRequestParamSingle batchItem = new ODataRequestParamSingleDefaultImpl();
                                    // Create change set
                                    batchItem.setPayload(channelPartnerEntity);
                                    batchItem.setMode(ODataRequestParamSingle.Mode.Create);
                                    batchItem.setResourcePath(Constants.SyncHistroy);
                                    batchItem.setContentID(contentId);
                             /*   HashMap<String, String> map = new HashMap<>();
                                map.put("OfflineOData.RemoveAfterUpload", "true");
                                batchItem.getCustomHeaders().putAll(map);*/
                                    // batchItem.setOptions(map);

                                    Map<String, String> createHeaders = new HashMap<String, String>();
                                    createHeaders.put("OfflineOData.RemoveAfterUpload", "true");
                                    batchItem.getCustomHeaders().putAll(createHeaders);

                                    ODataRequestChangeSet changeSetItem = new ODataRequestChangeSetDefaultImpl();
                                    changeSetItem.add(batchItem);
                                    try {
                                        requestParamBatch.add(changeSetItem);
                                    } catch (ODataException e) {
                                        e.printStackTrace();
                                    }
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    try {
                        offlineStore.executeRequest(requestParamBatch);
                    } catch (Exception e) {
                        try {
                            throw new OfflineODataStoreException(e);
                        } catch (OfflineODataStoreException e1) {
                            e1.printStackTrace();
                        }
                    }
                    try {
                        updateStartSyncTime(context, syncType, Constants.EndSync,refGuid);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    if (reentrantLock != null && reentrantLock.isHeldByCurrentThread()) {
                        reentrantLock.unlock();
                    }
                    Log.e("Sync Histroy EXCEPTION", "ANR EXCEPTION OCCURRED");
                } finally {
                    if (reentrantLock != null && reentrantLock.isHeldByCurrentThread())
                        reentrantLock.unlock();
                    Log.e("Sync Histroy REENTRANT:", "UNLOCKED FINALLY");
                }
            }
        }).start();

    }

    public static void updateSyncTime(final List<String> alAssignColl, final Context context, final String syncType,final String refguid, final SyncHistoryCallBack syncHistoryCallBack) {
        String strSPGUID = Constants.getSPGUID(Constants.SPGUID);
//        String StrSPGUID32 = "";
//        String parternTypeID = "";

        if (!TextUtils.isEmpty(strSPGUID)) {
            StrSPGUID32 = strSPGUID.replaceAll("-", "");
            try {
                parternTypeID = OfflineManager.getPartnerTypeID(Constants.UserPartners + "?$filter= PartnerID eq'" + StrSPGUID32 + "'");
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }

        /*if (!TextUtils.isEmpty(strSPGUID)) {
            StrSPGUID32 = strSPGUID.replaceAll("-", "");
        }
        parternTypeID = context.getSharedPreferences(Constants.PREFS_NAME,0).getString(Constants.USERPARNTERID,"");
*/
        final boolean checkSyncHistoryColl = getSyncHistoryColl(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        final String loginId = sharedPreferences.getString("username", "");
        final String syncTime = Constants.getSyncHistoryddmmyyyyTime();
        final ODataRequestParamBatch requestParamBatch = new ODataRequestParamBatchDefaultImpl();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (reentrantLock == null) {
                    reentrantLock = new ReentrantLock();
                }
                reentrantLock.lock();
                Log.e("Sync Histroy REENTRANT:", "LOCKED");
                try {
                    for (int incReq = 0; incReq < alAssignColl.size(); incReq++) {
                        colName = alAssignColl.get(incReq);
                        if (colName.contains("?$")) {
                            String splitCollName[] = colName.split("\\?");
                            colName = splitCollName[0];
                        }


                        Constants.events.updateStatus(Constants.SYNC_TABLE,
                                colName, Constants.TimeStamp, syncTime
                        );
                        Log.d("SH Offline","insert RefGuid:-"+refguid+"--"+colName);

                        if (checkSyncHistoryColl) {
                            try {
//                                Constants.createSyncHistory(colName, syncTime, syncType, StrSPGUID32, parternTypeID, loginId);

                                Hashtable hashtable = Constants.createSyncHistoryBatch(colName, syncTime, syncType, StrSPGUID32, parternTypeID, loginId,refguid);
                                ODataEntity channelPartnerEntity = null;
                                try {
                                    channelPartnerEntity = OfflineManager.createSyncHistroyEntity(hashtable);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                int id = incReq + 1;
                                String contentId = String.valueOf(id);
                                ODataRequestParamSingle batchItem = new ODataRequestParamSingleDefaultImpl();
                                // Create change set
                                batchItem.setPayload(channelPartnerEntity);

                                batchItem.setMode(ODataRequestParamSingle.Mode.Create);
                                batchItem.setResourcePath(Constants.SyncHistroy);

                                batchItem.setContentID(contentId);
                               /* HashMap<String, String> map = new HashMap<>();
                                map.put("OfflineOData.RemoveAfterUpload", "true");*/
                               // batchItem.setOptions(map);



                                Map<String, String> createHeaders = new HashMap<String, String>();
                                createHeaders.put("OfflineOData.RemoveAfterUpload", "true");
                                batchItem.getCustomHeaders().putAll(createHeaders);


                                ODataRequestChangeSet changeSetItem = new ODataRequestChangeSetDefaultImpl();
                                changeSetItem.add(batchItem);

                                try {
                                    requestParamBatch.add(changeSetItem);
                                    //requestParamBatch.getCustomHeaders().putAll(map);
                                } catch (ODataException e) {
                                    LogManager.writeLogError("LN 8631"+e.getMessage());
                                    e.printStackTrace();
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                                LogManager.writeLogError("LN 8637"+e.getMessage());
                            }
                        }
                    }
                    try {
                        offlineStore.executeRequest(requestParamBatch);
                    } catch (Exception e) {
                        LogManager.writeLogError("LN 8644"+e.getMessage());
                        try {
                            throw new OfflineODataStoreException(e);
                        } catch (OfflineODataStoreException e1) {
                            LogManager.writeLogError("LN 8648"+e.getMessage());
                            e1.printStackTrace();
                        }
                    }
                    try {
                        updateStartSyncTime(context, syncType, Constants.EndSync,refguid);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    if (reentrantLock != null && reentrantLock.isHeldByCurrentThread()) {
                        reentrantLock.unlock();
                    }
                    Log.e("Sync Histroy EXCEPTION", "ANR EXCEPTION OCCURRED");
                } finally {
                    if (reentrantLock != null && reentrantLock.isHeldByCurrentThread())
                        reentrantLock.unlock();
                    Log.e("Sync Histroy REENTRANT:", "UNLOCKED FINALLY");
                }
                if (syncHistoryCallBack != null) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            syncHistoryCallBack.displaySuccessMessage();
                        }
                    });
                }
            }
        }).start();

    }
    public static void updateStartInitialSyncTime(Context context, final String syncType, final String syncMsg,final String refGuid,final String initialTime) {
        String strSPGUID = Constants.getSPGUID(Constants.SPGUID);
//        String StrSPGUID32 = "";
//        String parternTypeID = "";

        if (!TextUtils.isEmpty(strSPGUID)) {
            StrSPGUID32 = strSPGUID.replaceAll("-", "");
            try {
                parternTypeID = OfflineManager.getPartnerTypeID(Constants.UserPartners + "?$filter= PartnerID eq'" + StrSPGUID32 + "'");
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }
        /*if (!TextUtils.isEmpty(strSPGUID)) {
            StrSPGUID32 = strSPGUID.replaceAll("-", "");
        }
        parternTypeID = context.getSharedPreferences(Constants.PREFS_NAME,0).getString(Constants.USERPARNTERID,"");
*/
        final boolean checkSyncHistoryColl = getSyncHistoryColl(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        final String loginId = sharedPreferences.getString("username", "");
        /*String syncTime = "";
        if (syncMsg.equalsIgnoreCase(Constants.StartSync)) {
            syncTime = Constants.getSyncHistoryddmmyyyyTime();
        } else {
            syncTime = Constants.getSyncHistoryddmmyyyyTimeDelay();
        }
        final String finalSyncTime = syncTime;
*/
       /* if (Constants.writeDebug)
            LogManager.writeLogDebug("Dashboard refresh Sync time : db update : " + syncTime);*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (reentrantLock == null) {
                    reentrantLock = new ReentrantLock();
                }
                reentrantLock.lock();
                Log.e("Sync Histroy REENTRANT:", "LOCKED");
                String startColl = "";
                String syncollType = "";
                try {

                    try {
                        if (syncMsg.equalsIgnoreCase(Constants.StartSync)) {
                            if (syncType.equalsIgnoreCase(Constants.Sync_All)) {
                                startColl = "All Download Start";
                                syncollType = Constants.Sync_All;
                            } else if (syncType.equalsIgnoreCase(Constants.DownLoad)) {
                                startColl = "Download Start";
                                syncollType = Constants.DownLoad;
                            } else if (syncType.equalsIgnoreCase(Constants.UpLoad)) {
                                startColl = "Upload Start";
                                syncollType = Constants.UpLoad;
                            } else if (syncType.equalsIgnoreCase(Constants.Auto_Sync)) {
                                startColl = "Auto Sync Start";
                                syncollType = Constants.Auto_Sync;
                            }else if (syncType.equalsIgnoreCase(Constants.Attnd_sync)) {
                                startColl = "Attnd Start Sync Start";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Target_sync)) {
                                startColl = "Target Pull Down Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.MatGrpTrg_sync)) {
                                startColl = "MatGrpTrg Pull Down Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.MTP_sync)) {
                                startColl = "MTP Pull Down Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.AdVst_sync)) {
                                startColl = "Ad Vst Cust Pull Down Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.SOPD_sync)) {
                                startColl = "SO PD Sync";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Invoice_sync)) {
                                startColl = "Invoice Pull Down Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.CrdStatus_sync)) {
                                startColl = "Crd Status Pull Down Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.ProdPrc_sync)) {
                                startColl = "Prod Prc Pull Down Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.BP_sync)) {
                                startColl = "BP Pull Down Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.RTGS_sync)) {
                                startColl = "RTGS Pull Down Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Behav_sync)) {
                                startColl = "Behav Pull Down Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.SOPostPD_sync)) {
                                startColl = "SO POST PD Sync Start";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.SOPOSTBG_sync)) {
                                startColl = "SO POST BG Sync Start";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Attnd_refresh_sync)) {
                                startColl = "Attnd Refresh Sync Start";
                                syncollType = Constants.DownLoad;
                            }
                        } else {
                            if (syncType.equalsIgnoreCase(Constants.Sync_All)) {
                                startColl = "All Download End";
                                syncollType = Constants.Sync_All;
                            } else if (syncType.equalsIgnoreCase(Constants.DownLoad)) {
                                startColl = "Download End";
                                syncollType = Constants.DownLoad;
                            } else if (syncType.equalsIgnoreCase(Constants.UpLoad)) {
                                startColl = "Upload End";
                                syncollType = Constants.UpLoad;
                            } else if (syncType.equalsIgnoreCase(Constants.Auto_Sync)) {
                                startColl = "Auto Sync End";
                                syncollType = Constants.Auto_Sync;
                            }else if (syncType.equalsIgnoreCase(Constants.Attnd_sync)) {
                                startColl = "Attnd Start Sync End";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Target_sync)) {
                                startColl = "Target Pull Down Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.MatGrpTrg_sync)) {
                                startColl = "MatGrpTrg Pull Down Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.MTP_sync)) {
                                startColl = "MTP Pull Down Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.AdVst_sync)) {
                                startColl = "Ad Vst Cust Pull Down Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.SOPD_sync)) {
                                startColl = "SO PD Sync";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Invoice_sync)) {
                                startColl = "Invoice Pull Down Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.CrdStatus_sync)) {
                                startColl = "Crd Status Pull Down Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.ProdPrc_sync)) {
                                startColl = "Prod Prc Pull Down Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.BP_sync)) {
                                startColl = "BP Pull Down Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.RTGS_sync)) {
                                startColl = "RTGS Pull Down Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Behav_sync)) {
                                startColl = "Behav Pull Down Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.SOPostPD_sync)) {
                                startColl = "SO POST PD Sync End";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.SOPOSTBG_sync)) {
                                startColl = "SO POST BG Sync End";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Attnd_refresh_sync)) {
                                startColl = "Attnd Refresh Sync End";
                                syncollType = Constants.UpLoad;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (checkSyncHistoryColl) {
                        try {
                            startColl = "Initial Sync Start";
                            syncollType = Constants.Sync_All;
                            Constants.createSyncHistory(startColl, initialTime, syncollType, StrSPGUID32, parternTypeID, loginId,refGuid);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    if (reentrantLock != null && reentrantLock.isHeldByCurrentThread()) {
                        reentrantLock.unlock();
                    }
                    if (Constants.writeDebug)
                        LogManager.writeLogInfo("Sync  Execption :" + e.getLocalizedMessage());
                    Log.e("Sync  Execption", "ANR EXCEPTION OCCURRED");
                } finally {
                    if (reentrantLock != null && reentrantLock.isHeldByCurrentThread())
                        reentrantLock.unlock();
                    Log.e("Sync Histroy REENTRANT:", "UNLOCKED FINALLY");
                }
            }
        }).start();

    }


    public static void updateStartSyncTime(Context context, final String syncType, final String syncMsg,final String refGuid) {
        String strSPGUID = Constants.getSPGUID(Constants.SPGUID);
//        String StrSPGUID32 = "";
//        String parternTypeID = "";

        if (!TextUtils.isEmpty(strSPGUID)) {
            StrSPGUID32 = strSPGUID.replaceAll("-", "");
            try {
                parternTypeID = OfflineManager.getPartnerTypeID(Constants.UserPartners + "?$filter= PartnerID eq'" + StrSPGUID32 + "'");
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }

        /*if (!TextUtils.isEmpty(strSPGUID)) {
            StrSPGUID32 = strSPGUID.replaceAll("-", "");
        }
        parternTypeID = context.getSharedPreferences(Constants.PREFS_NAME,0).getString(Constants.USERPARNTERID,"");
*/
        final boolean checkSyncHistoryColl = getSyncHistoryColl(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        final String loginId = sharedPreferences.getString("username", "");
        String syncTime = "";
        if (syncMsg.equalsIgnoreCase(Constants.StartSync)) {
            syncTime = Constants.getSyncHistoryddmmyyyyTime();
        } else {
            syncTime = Constants.getSyncHistoryddmmyyyyTimeDelay();
        }
        final String finalSyncTime = syncTime;

        if (Constants.writeDebug)
            LogManager.writeLogDebug("Dashboard refresh Sync time : db update : " + syncTime);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (reentrantLock == null) {
                    reentrantLock = new ReentrantLock();
                }
                reentrantLock.lock();
                Log.e("Sync Histroy REENTRANT:", "LOCKED");
                String startColl = "";
                String syncollType = "";
                try {

                    try {
                        if (syncMsg.equalsIgnoreCase(Constants.StartSync)) {
                            if (syncType.equalsIgnoreCase(Constants.Sync_All)) {
                                startColl = "All Download Start";
                                syncollType = Constants.Sync_All;
                            } else if (syncType.equalsIgnoreCase(Constants.DownLoad)) {
                                startColl = "Download Start";
                                syncollType = Constants.DownLoad;
                            } else if (syncType.equalsIgnoreCase(Constants.UpLoad)) {
                                startColl = "Upload Start";
                                syncollType = Constants.UpLoad;
                            } else if (syncType.equalsIgnoreCase(Constants.Auto_Sync)) {
                                startColl = "Auto Sync Start";
                                syncollType = Constants.Auto_Sync;
                            }else if (syncType.equalsIgnoreCase(Constants.Attnd_sync)) {
                                startColl = "Attnd Start Sync Start";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Target_sync)) {
                                startColl = "Target Pull Down Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.MatGrpTrg_sync)) {
                                startColl = "MatGrpTrg Pull Down Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.MTP_sync)) {
                                startColl = "MTP Pull Down Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.AdVst_sync)) {
                                startColl = "Ad Vst Cust Pull Down Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.SOPD_sync)) {
                                startColl = "SO PD Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Invoice_sync)) {
                                startColl = "Invoice Pull Down Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.CrdStatus_sync)) {
                                startColl = "Crd Status Pull Down Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.ProdPrc_sync)) {
                                startColl = "Prod Prc Pull Down Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.BP_sync)) {
                                startColl = "BP Pull Down Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.RTGS_sync)) {
                                startColl = "RTGS Pull Down Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Behav_sync)) {
                                startColl = "Behav Pull Down Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.SOPostPD_sync)) {
                                startColl = "SO POST PD Sync Start";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.SOPOSTBG_sync)) {
                                startColl = "SO POST BG Sync Start";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Attnd_refresh_sync)) {
                                startColl = "Attnd Refresh Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Initial_sync)) {
                                startColl = "Initial Sync Start";
                                syncollType = Constants.Sync_All;
                            }else if (syncType.equalsIgnoreCase(Constants.Geo_sync)) {
                                startColl = "Geo Sync Start";
                                syncollType = Constants.Sync_All;
                            }else if (syncType.equalsIgnoreCase(Constants.DB_pull_sync)) {
                                startColl = "DB Pulldown Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.download_all_cancel_sync)) {
                                startColl = "All Download Cancel Man";
                                syncollType = Constants.Sync_All;
                            }else if (syncType.equalsIgnoreCase(Constants.upload_cancel_sync)) {
                                startColl = "Upload Cancel Man";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.download_cancel_sync)) {
                                startColl = "Download Cancel Man";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.upload_net_sync)) {
                                startColl = "Upload Cancel Net";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.download_all_net_sync)) {
                                startColl = "All Download Cancel Net";
                                syncollType = Constants.Sync_All;
                            }
                        } else {
                            if (syncType.equalsIgnoreCase(Constants.Sync_All)) {
                                startColl = "All Download End";
                                syncollType = Constants.Sync_All;
                            } else if (syncType.equalsIgnoreCase(Constants.DownLoad)) {
                                startColl = "Download End";
                                syncollType = Constants.DownLoad;
                            } else if (syncType.equalsIgnoreCase(Constants.UpLoad)) {
                                startColl = "Upload End";
                                syncollType = Constants.UpLoad;
                            } else if (syncType.equalsIgnoreCase(Constants.Auto_Sync)) {
                                startColl = "Auto Sync End";
                                syncollType = Constants.Auto_Sync;
                            }else if (syncType.equalsIgnoreCase(Constants.Attnd_sync)) {
                                startColl = "Attnd Start Sync End";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Target_sync)) {
                                startColl = "Target Pull Down Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.MatGrpTrg_sync)) {
                                startColl = "MatGrpTrg Pull Down Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.MTP_sync)) {
                                startColl = "MTP Pull Down Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.AdVst_sync)) {
                                startColl = "Ad Vst Cust Pull Down Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.SOPD_sync)) {
                                startColl = "SO PD Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Invoice_sync)) {
                                startColl = "Invoice Pull Down Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.CrdStatus_sync)) {
                                startColl = "Crd Status Pull Down Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.ProdPrc_sync)) {
                                startColl = "Prod Prc Pull Down Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.BP_sync)) {
                                startColl = "BP Pull Down Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.RTGS_sync)) {
                                startColl = "RTGS Pull Down Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Behav_sync)) {
                                startColl = "Behav Pull Down Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.SOPostPD_sync)) {
                                startColl = "SO POST PD Sync End";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.SOPOSTBG_sync)) {
                                startColl = "SO POST BG Sync End";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Attnd_refresh_sync)) {
                                startColl = "Attnd Refresh Sync End";
                                syncollType = Constants.UpLoad;
                            }
                            else if (syncType.equalsIgnoreCase(Constants.Initial_sync)) {
                                startColl = "Initial Sync End";
                                syncollType = Constants.Sync_All;
                            }
                            else if (syncType.equalsIgnoreCase(Constants.Geo_sync)) {
                                startColl = "Geo Sync End";
                                syncollType = Constants.Sync_All;
                            }else if (syncType.equalsIgnoreCase(Constants.DB_pull_sync)) {
                                startColl = "DB Pulldown Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.download_all_cancel_sync)) {
                                startColl = "All Download Cancel Man";
                                syncollType = Constants.Sync_All;
                            }else if (syncType.equalsIgnoreCase(Constants.upload_cancel_sync)) {
                                startColl = "Upload Cancel Man";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.download_cancel_sync)) {
                                startColl = "Download Cancel Man";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.upload_net_sync)) {
                                startColl = "Upload Cancel Net";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.download_all_net_sync)) {
                                startColl = "All Download Cancel Net";
                                syncollType = Constants.Sync_All;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (checkSyncHistoryColl) {
                        try {
                            Constants.createSyncHistory(startColl, finalSyncTime, syncollType, StrSPGUID32, parternTypeID, loginId,refGuid);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    if (reentrantLock != null && reentrantLock.isHeldByCurrentThread()) {
                        reentrantLock.unlock();
                    }
                    if (Constants.writeDebug)
                        LogManager.writeLogInfo("Sync  Execption :" + e.getLocalizedMessage());
                    Log.e("Sync  Execption", "ANR EXCEPTION OCCURRED");
                } finally {
                    if (reentrantLock != null && reentrantLock.isHeldByCurrentThread())
                        reentrantLock.unlock();
                    Log.e("Sync Histroy REENTRANT:", "UNLOCKED FINALLY");
                }
            }
        }).start();

    }

    public static boolean getSyncHistoryColl(Context context) {
        boolean check = Arrays.asList(Constants.getDefinigReq(context)).contains(Constants.SyncHistorys);
        LogManager.writeLogInfo("Checking Sync history:" + String.valueOf(check));
        return check;
    }

    public static ODataDuration getTimeAsODataDurationConvertion(String timeString) {
        List<String> timeDuration = Arrays.asList(timeString.split("-"));
        int hour = Integer.parseInt((String) timeDuration.get(0));
        int minute = Integer.parseInt((String) timeDuration.get(1));
        int seconds = Integer.parseInt((String) timeDuration.get(2));
        ODataDurationDefaultImpl oDataDuration = null;

        try {
            oDataDuration = new ODataDurationDefaultImpl();
            oDataDuration.setHours(hour);
            oDataDuration.setMinutes(minute);
            oDataDuration.setSeconds(BigDecimal.valueOf((long) seconds));
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return oDataDuration;
    }

    public static ODataDuration getTimeAsODataDurationConvertionLocation(String timeString) {
        List<String> timeDuration = Arrays.asList(timeString.split("-"));
        int hour = Integer.parseInt((String) timeDuration.get(0));
        int minute = Integer.parseInt((String) timeDuration.get(1));
        int seconds = Integer.parseInt((String) timeDuration.get(2));
        ODataDurationDefaultImpl oDataDuration = null;

        try {
            oDataDuration = new ODataDurationDefaultImpl();
            oDataDuration.setHours(hour);
            oDataDuration.setMinutes(minute);
            oDataDuration.setSeconds(BigDecimal.valueOf((long) seconds));
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return oDataDuration;
    }

    public static boolean getRollID(Context context) {
        boolean rollID = false;
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        String rollType = sharedPreferences.getString(Constants.USERROLE, "");
        if (rollType.equalsIgnoreCase("Z5")) {
            rollID = true;
        }
        return rollID;
    }

    public static void logStatusToStorage(String data) {
        try {
            File path = new File(Environment.getExternalStoragePublicDirectory(""),
                    "transport-tracker-log.txt");
            if (!path.exists()) {
                try {
                    path.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileWriter logFile = new FileWriter(path.getAbsolutePath(), true);
            logFile.append(UtilConstants.getDateTime() + ":::::" + data + "\n");
            logFile.close();
        } catch (Exception e) {
            Log.e("Location", "Log file error", e);
        }
    }

    public static boolean restartApp(Activity activity) {
        /*LogonCoreContext lgCtx1 = null;
        try {
            lgCtx1 = LogonCore.getInstance().getLogonContext();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (lgCtx1 == null) {

            SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.PREFS_NAME, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isAppRestart", true);
            editor.commit();
            Log.e("Restart", "Called");
            activity.finishAffinity();
            Intent dialogIntent = new Intent(activity, RegistrationActivity.class);
            activity.startActivity(dialogIntent);
        } else {
            return false;

        }*/
        return false;
    }

    public static String convertCurrencyInWords(double currency) {
        String convertCurrency = String.valueOf(currency);
        try {
        /*if(currency<99999 && currency>10000){
            double value = currency / 1000;
            value =Double.parseDouble(new DecimalFormat("##.##").format(value));
            convertCurrency = value + "T";
        }else */
            if (currency < 99999999 && currency >= 100000) {
                double value = currency / 100000;
                value = Double.parseDouble(new DecimalFormat("##.##").format(value));
                convertCurrency = value + " L";
            } else if (currency < 999999999 && currency > 10000000) {
                double value = currency / 10000000;
                value = Double.parseDouble(new DecimalFormat("##.##").format(value));
                convertCurrency = value + " Cr";
            }else {
                convertCurrency = ""+Double.parseDouble(new DecimalFormat("##.##").format(currency));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return convertCurrency;
    }

    public static int quantityLength() {
        String maxStrLength = "";
        int maxLength = 0;
        try {
            maxStrLength = OfflineManager.getValueByColumnName("ConfigTypsetTypeValues?$filter=Typeset eq 'SF' and Types eq 'ALWLENQTY'", Constants.TypeValue);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            maxStrLength = "";
        }
        if (!TextUtils.isEmpty(maxStrLength)) {
            maxLength = Integer.parseInt(maxStrLength);
        } else {
            maxLength = 9;
        }
        return maxLength;
    }

    public static void httphashmaperrorcodes() {
        //500 series
        httpErrorCodes.put("500", "Connection Timeout");
        httpErrorCodes.put("501", "Not Implemented");
        httpErrorCodes.put("502", "Bad Gateway");
        httpErrorCodes.put("503", "Service Unavailable");
        httpErrorCodes.put("504", "Gateway Timeout");
        httpErrorCodes.put("505", "HTTP Version Not Supported");
        httpErrorCodes.put("506", "Variant Also Negotiates");
        httpErrorCodes.put("507", "Insufficient Storage");
        httpErrorCodes.put("508", "Loop Detected");
        httpErrorCodes.put("509", "Unassigned");
        httpErrorCodes.put("510", "Not Extended");
        httpErrorCodes.put("511", "Network Authentication Required");
        //400 series
        httpErrorCodes.put("400", "Bad Request");
        httpErrorCodes.put("401", "Unauthorized");
        httpErrorCodes.put("402", "Payment Required");
        httpErrorCodes.put("403", "Forbidden");
        httpErrorCodes.put("404", "Not Found");
        httpErrorCodes.put("405", "Method Not Allowed");
        httpErrorCodes.put("406", "Not Acceptable");
        httpErrorCodes.put("407", "Proxy Authentication Required");
        httpErrorCodes.put("408", "Request Timeout");
        httpErrorCodes.put("409", "Conflict");
        httpErrorCodes.put("410", "Gone");
        httpErrorCodes.put("411", "Length Required");
        httpErrorCodes.put("412", "Precondition Failed");
        httpErrorCodes.put("413", "Payload Too Large");
        httpErrorCodes.put("414", "URI Too Long");
        httpErrorCodes.put("415", "Unsupported Media Type");
        httpErrorCodes.put("416", "Range Not Satisfiable");
        httpErrorCodes.put("417", "Expectation Failed");
        httpErrorCodes.put("421", "Misdirected Request");
        httpErrorCodes.put("422", "Unprocessable Entity");
        httpErrorCodes.put("423", "Locked");
        httpErrorCodes.put("424", "Failed Dependency");
        httpErrorCodes.put("425", "Too Early");
        httpErrorCodes.put("426", "Upgrade Required");
        httpErrorCodes.put("427", "Unassigned");
        httpErrorCodes.put("428", "Precondition Required");
        httpErrorCodes.put("429", "Too Many Requests");
        httpErrorCodes.put("430", "Unassigned");
        httpErrorCodes.put("431", "Request Header Fields Too Large");
        httpErrorCodes.put("451", "Unavailable For Legal Reasons");
    }

    public static String makecustomHttpErrormessage(String error_msg) {
        String[] DEFINGREQARRAY = {"Attendances",
                "UserProfileAuthSet?$filter=Application%20eq%20%27PD%27",
                //"SPGeos",/*SyncHistorys,UserPartners,*/
                "Customers", KPISet, Targets, TargetItems, KPIItems,
                "SalesPersons", OrderMaterialGroups, Brands, PlantStocks, UserSalesPersons,
                CustomerPartnerFunctions,
                "MPerformances?$filter= PerformanceTypeID eq '000006' and AggregationLevelID eq '01'",
                "CustomerCreditLimits", UserCustomers, CustomerSalesAreas, "MaterialSaleAreas",
                "Alerts?$filter=Application eq 'PD'",
                "Invoices", "InvoiceItemDetails", InvoicePartnerFunctions,
                "VisitActivities", "Visits", "InvoiceItems", "InvoiceConditions",
                "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                "CollectionPlans", "CollectionPlanItems", CollectionPlanItemDetails,
                "SOItems", "SOs", "SOConditions", "SOItemDetails", "SOTexts?$filter=TextCategory eq 'H' or TextCategory eq 'I'",
                "MSPChannelEvaluationList?$filter=ApplicationID eq 'SF'",
                ReturnOrderItems, ReturnOrderItemDetails, ReturnOrders,
                Collections, Stocks,
                "ConfigTypesetTypes?$filter=Typeset eq 'DELVST' or Typeset eq 'INVST' " +
                        "or Typeset eq 'REJRSN' or Typeset eq 'UOMNO0' or Typeset eq 'OINVAG' or Typeset eq 'SOITST' or Typeset eq 'EVLTYP' or Typeset eq 'CRDCTL' or Typeset eq 'RODLST' or Typeset eq 'ROGRST'",
                "ConfigTypsetTypeValues?$filter=Typeset eq 'PD' or " +
                        "Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' " +
                        "or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'SP' ",
                "MaterialByCustomers",
                "ValueHelps?$filter=ModelID eq 'SFGW_ALL' and (EntityType eq 'ExpenseItemDetail' " +
                        "or EntityType eq 'SO' or EntityType eq 'ExpenseConfig' or EntityType eq 'Campaign' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Complaints' or EntityType eq 'Collection' or EntityType eq 'Attendance') and (PropName eq 'Plant' " +
                        "or PropName eq 'Location' or PropName eq 'CampaignStatus' or PropName eq 'ConvenyanceMode' or PropName eq 'OrderType' " +
                        "or PropName eq 'Incoterm1' or PropName eq 'CampaignType' or PropName eq 'BudgetType' or PropName eq 'CampaignVenue' or PropName eq 'DMSDiv' or PropName eq 'BlockID' or PropName eq 'CampaignExpenses' or PropName eq 'ConstructionType' or PropName eq 'ConstructionStageID' or PropName eq 'Payterm' or PropName eq 'CPTypeID' or PropName eq 'PotentialType' or PropName eq 'FeedbackType' or PropName eq 'FeedbackSubType' or PropName eq 'ShippingTypeID' or PropName eq 'MeansOfTranstyp' or PropName eq 'CollectionTypeID' or PropName eq 'PaymentMethodID' or PropName eq 'SalesDistrict' or PropName eq 'Route' or PropName eq 'SplProcessing' or PropName eq 'PriceList' or PropName eq 'MatFrgtGrp' or PropName eq 'StorageLoc') "

        };
        httphashmaperrorcodes();
        if (!TextUtils.isEmpty(error_msg) && error_msg.contains("HTTP code")) {
            String make_message = "";
            List<String> errorList = Arrays.asList(error_msg.split(","));
            if (errorList.size() > 0) {
                for (int i = 0; i < DEFINGREQARRAY.length; i++) {
                    if (error_msg.contains(DEFINGREQARRAY[i])) {
                        make_message = DEFINGREQARRAY[i] + ":";
                        break;
                    }
                }
                String httperrormsg = "";
                for (String data : errorList) {
                    Iterator<String> keySetIterator = httpErrorCodes.keySet().iterator();
                    while (keySetIterator.hasNext()) {
                        String key = keySetIterator.next();
                        if (data.contains(key)) {
                            httperrormsg = httpErrorCodes.get(key) + "-" + key;
                            break;
                        }

                    }
                }
                if (!TextUtils.isEmpty(httperrormsg))
                    make_message += httperrormsg + " Please contact channel team";
                else make_message = error_msg;
            } else {
                make_message = error_msg;
            }
            return make_message;

        }
        return error_msg;

    }

    public static int makecustomHttpErrorCode(String error_msg) {
        httphashmaperrorcodes();
        String httperrorcode = "";
        int code = 0;
        if (!TextUtils.isEmpty(error_msg) && error_msg.contains("HTTP code")) {
            List<String> errorList = Arrays.asList(error_msg.split(","));
            if (errorList.size() > 0) {

                for (String data : errorList) {
                    Iterator<String> keySetIterator = httpErrorCodes.keySet().iterator();
                    while (keySetIterator.hasNext()) {
                        String key = keySetIterator.next();
                        if (data.contains(key)) {
                            httperrorcode = httpErrorCodes.get(key) + "-" + key;
                            break;
                        }

                    }
                }

            }


        }
        if (!TextUtils.isEmpty(httperrorcode)) {
            try {
                code = Integer.parseInt(httperrorcode);
            } catch (Exception e) {
                e.printStackTrace();
                code = 0;
            }
        }
        return code;

    }


/*    private static void showDialog(final String type, final Context context){
        try {
           *//* WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View mView = mInflater.inflate(R.layout.aboutus_activity, null);

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                    PixelFormat.TRANSLUCENT
            );

            wm.addView(mView, params);*//*
            if (Constants.alert == null || !Constants.alert.isShowing()) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    if (Settings.canDrawOverlays(context)) {
//                        if (alert == null || !alert.isShowing()) {
                        builder = new AlertDialog.Builder(context);
                        builder.setTitle("Geo Tracking");
                        builder.setIcon(R.mipmap.ic_app_launcher);
                        builder.setMessage(type);
//                builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //Do something
                                dialog.dismiss();
                                if (type.equalsIgnoreCase(context.getString(R.string.gps_not_enable))) {
                                    Intent I = new Intent(
                                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    I.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(I);
                                } else if (type.equalsIgnoreCase(context.getString(R.string.dateTime_not_enable))) {
                                    Intent I = new Intent(
                                            android.provider.Settings.ACTION_DATE_SETTINGS);
                                    I.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(I);
                                } else if (type.equalsIgnoreCase(context.getString(R.string.internet_not_enable))) {
                                    Intent I = new Intent(
                                            android.provider.Settings.ACTION_SETTINGS);
                                    I.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(I);
                                }
                                *//*Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
                                intent.putExtra("enabled", true);
                                sendBroadcast(intent);*//*

                            }
                        });
                        Constants.alert = builder.create();
                        Constants.alert.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                        Constants.alert.setCancelable(false);
                        Constants.alert.show();
//                        }
                    }
                } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
                    if (Settings.canDrawOverlays(context)) {
//                        if (alert == null || !alert.isShowing()) {
                        builder = new AlertDialog.Builder(context);
                        builder.setTitle("Geo Tracking");
                        builder.setIcon(R.mipmap.ic_app_launcher);
                        builder.setMessage(type);
//                builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //Do something
                                dialog.dismiss();
                                if (type.equalsIgnoreCase(context.getString(R.string.gps_not_enable))) {
                                    Intent I = new Intent(
                                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    I.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(I);
                                } else if (type.equalsIgnoreCase(context.getString(R.string.dateTime_not_enable))) {
                                    Intent I = new Intent(
                                            android.provider.Settings.ACTION_DATE_SETTINGS);
                                    I.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(I);
                                } else if (type.equalsIgnoreCase(context.getString(R.string.internet_not_enable))) {
                                    Intent I = new Intent(
                                            android.provider.Settings.ACTION_SETTINGS);
                                    I.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(I);
                                }
                                *//*Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
                                intent.putExtra("enabled", true);
                                sendBroadcast(intent);*//*

                            }
                        });
                        Constants.alert = builder.create();
                        Constants.alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        Constants.alert.setCancelable(false);
                        Constants.alert.show();
//                        }
                    }
                } else {
//                    if (alert == null || !alert.isShowing()) {
                    builder = new AlertDialog.Builder(context);
                    builder.setTitle("Geo Tracking");
                    builder.setIcon(R.mipmap.ic_app_launcher);
                    builder.setMessage(type);
//                builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Do something
                            dialog.dismiss();
                            if (type.equalsIgnoreCase(context.getString(R.string.gps_not_enable))) {
                                Intent I = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                I.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(I);
                            } else if (type.equalsIgnoreCase(context.getString(R.string.dateTime_not_enable))) {
                                Intent I = new Intent(
                                        android.provider.Settings.ACTION_DATE_SETTINGS);
                                I.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(I);
                            } else if (type.equalsIgnoreCase(context.getString(R.string.internet_not_enable))) {
                                Intent I = new Intent(
                                        android.provider.Settings.ACTION_SETTINGS);
                                I.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(I);
                            }
                                *//*Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
                                intent.putExtra("enabled", true);
                                sendBroadcast(intent);*//*

                        }
                    });
                    Constants.alert = builder.create();
                    Constants.alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    Constants.alert.setCancelable(false);
                    Constants.alert.show();
//                    }
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static Timer timer = null;
    public static TimerTask timerTask = null;
    public static final Handler handler = new Handler();

    public static void startTimer(String type,Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        boolean isFlag = sharedPreferences.getBoolean(Constants.timer_flag, false);
        if(!isFlag) {
            try {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Constants.timer_flag, true);
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //set a new Timer
            if (timer == null) {
                timer = new Timer();

                //initialize the TimerTask's job
                initializeTimerTask(type,context);

                //schedule the timer, after the first 5000ms the TimerTask will run every 60000ms
                timer.schedule(timerTask, 5000, 60000); //
            }
        }
    }

    public static void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timerTask.cancel();
            timer = null;
            timerTask = null;
        }
    }

    public static void initializeTimerTask(final String type, final Context context) {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp
//                        int duration = Toast.LENGTH_SHORT;
//                        Toast toast = Toast.makeText(getApplicationContext(), "Start timer", duration);
//                        toast.show();
//                        showDialog(type);
                        final LocationManager manager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );
                        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                                .getActiveNetworkInfo();
                        boolean connected = info != null && info.isConnectedOrConnecting();
                        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                            showDialog(context.getString(R.string.gps_not_enable),context);
                        }else if (!connected) {
                            showDialog(context.getString(R.string.internet_not_enable),context);
                        }else if(!ConstantsUtils.isAutomaticTimeZone(context)){
                            showDialog(context.getString(R.string.dateTime_not_enable),context);
                        }
                    }
                });
            }
        };
    }*/

    public static class DecimalFilter implements InputFilter {
        EditText editText;
        int beforeDecimal, afterDecimal;

        public DecimalFilter(EditText editText, int beforeDecimal, int afterDecimal) {
            this.editText = editText;
            this.afterDecimal = afterDecimal;
            this.beforeDecimal = beforeDecimal;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            StringBuilder sbText = new StringBuilder(source);
            String text = sbText.toString();
            if (dstart == 0) {
                if (text.contains("0")) {
                    return "";
                } else {
                    return source;
                }
            }
            String etText = editText.getText().toString();
            if (etText.isEmpty()) {
                return null;
            }
            String temp = editText.getText() + source.toString();

            if (temp.equals(".")) {
                return "0.";
            } else if (temp.toString().indexOf(".") == -1) {
                // no decimal point placed yet
                if (temp.length() > beforeDecimal) {
                    return "";
                }
            } else {
                int dotPosition;
                int cursorPositon = editText.getSelectionStart();
                if (etText.indexOf(".") == -1) {
                    Log.i("First time Dot", etText.toString().indexOf(".") + " " + etText);
                    dotPosition = temp.indexOf(".");
                } else {
                    dotPosition = etText.indexOf(".");
                }
                if (cursorPositon <= dotPosition) {
                    String beforeDot = etText.substring(0, dotPosition);
                    if (beforeDot.length() < beforeDecimal) {
                        return source;
                    } else {
                        if (source.toString().equalsIgnoreCase(".")) {
                            return source;
                        } else {
                            return "";
                        }

                    }
                } else {
                    temp = temp.substring(temp.indexOf(".") + 1);
                    if (temp.length() > afterDecimal) {
                        return "";
                    }
                }
            }
            return null;
        }


    }
    public static void deletePostedSOData(Context context) {
       /* try {
            Set<String> soRefNos = sharedPreferences.getStringSet(Constants.soRefrenceNoToRemove, null);
            Set<String> valuestoremove = new HashSet<String>();
            valuestoremove = soRefNos;
            if(soRefNos!=null && soRefNos.size()>0) {
                for(String docNo:soRefNos) {
                    Constants.removeDeviceDocNoFromSharedPref(context, Constants.SecondarySOCreateTemp, docNo, sharedPreferences, false);
                    try {
                        valuestoremove.remove(docNo);
                    }catch (Throwable e){
                        e.printStackTrace();
                    }
                }
                SharedPreferences.Editor spEditor = sharedPreferences.edit();
                spEditor.putStringSet(Constants.soRefrenceNoToRemove, valuestoremove);
                spEditor.commit();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Constants.isBackGroundSync = false;
            Constants.isSync = false;
        }*/

        try {
            Set<String> set = new HashSet<>();
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
            set = sharedPreferences.getStringSet(Constants.SalesOrderDataValt, null);
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
                while (itr.hasNext()) {
                    String store = null, deviceNo = "";
                    try {
                        deviceNo = itr.next().toString();
                        store = ConstantsUtils.getFromDataVault(deviceNo,context);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONObject fetchJsonHeaderObject = new JSONObject(store);
                        String referenceGuid =  fetchJsonHeaderObject.getString(Constants.ReferenceNo);
                        String query = Constants.SOs + "?$filter=" + Constants.ReferenceNo + " eq '" +referenceGuid + "'";
                        try {
                            if(OfflineManager.isSoPresent(context, query)){
                                Constants.removeDeviceDocNoFromSharedPref(context, Constants.SalesOrderDataValt, deviceNo);
                                try {
                                    ConstantsUtils.storeInDataVault(deviceNo, "",context);
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    public static String checkUnknownNetworkerror(String errorMsg,Context mcontext) {
        String customErrorMsg="";
        if(!TextUtils.isEmpty(errorMsg)) {
            if (errorMsg.contains("10346"))
                customErrorMsg = mcontext.getString(R.string.error_10346);
            else if (errorMsg.contains("10349"))
                customErrorMsg = mcontext.getString(R.string.error_10349);
            else if (errorMsg.contains("10348"))
                customErrorMsg = mcontext.getString(R.string.error_10348);
            else if (errorMsg.contains("10345"))
                customErrorMsg = mcontext.getString(R.string.error_10345);
            else if (errorMsg.contains("10065"))
                customErrorMsg = mcontext.getString(R.string.error_10065);
            else if (errorMsg.contains("10058"))
                customErrorMsg = mcontext.getString(R.string.error_10058);
        }
        return customErrorMsg;
    }
 public static ErrorBean getErrorCodeGeo(int operation, Exception exception,Context context){
        ErrorBean errorBean = new ErrorBean();
        try {
            int errorCode = 0;
            boolean hasNoError =true;
            if ((operation == Operation.Create.getValue())){

                try {
                    // below error code getting from online manger (While posting data vault data)
//                    errorCode = ((ErrnoException) ((ODataNetworkException) exception).getCause().getCause()).errno;
                    Throwable throwables = (((ODataNetworkException) exception).getCause()).getCause().getCause();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (throwables instanceof ErrnoException){
                            errorCode = ((ErrnoException) throwables).errno;
                        }else{
                            if(exception.getMessage().contains(Constants.Unothorized_Error_Name)){
                                errorCode = Constants.UnAuthorized_Error_Code;
                                hasNoError =false;
                            }else if(exception.getMessage().contains(Constants.Comm_error_name)){
                                hasNoError =false;
                                errorCode = Constants.Comm_Error_Code;
                            }else if(exception.getMessage().contains(Constants.Network_Name)){
                                hasNoError =false;
                                errorCode = Constants.Network_Error_Code;
                            }else{
                                Constants.ErrorNo = 0;
                            }
                        }
                    }else{
                        try {
                            if(exception.getMessage()!=null){
                                if(exception.getMessage().contains(Constants.Unothorized_Error_Name)){
                                    errorCode = Constants.UnAuthorized_Error_Code;
                                    hasNoError =false;
                                }else if(exception.getMessage().contains(Constants.Comm_error_name)){
                                    hasNoError =false;
                                    errorCode = Constants.Comm_Error_Code;
                                }else if(exception.getMessage().contains(Constants.Network_Name)){
                                    hasNoError =false;
                                    errorCode = Constants.Network_Error_Code;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                    if(errorCode != Constants.UnAuthorized_Error_Code) {
                        if (errorCode == Constants.Network_Error_Code || errorCode == Constants.Comm_Error_Code) {
                            hasNoError = false;
                        } else {
                            hasNoError = true;
                        }
                    }
                } catch (Exception e1) {
                    if(exception.getMessage().contains(Constants.Unothorized_Error_Name)){
                        errorCode = Constants.UnAuthorized_Error_Code;
                        hasNoError =false;
                    }else{
                        Constants.ErrorNo = 0;
                    }
                }
                LogManager.writeLogError("Error : ["+errorCode+"]"+exception.getMessage());

            }else if(operation == Operation.OfflineFlush.getValue() || operation == Operation.OfflineRefresh.getValue()){
                try {
                    // below error code getting from offline manger (While posting flush and refresh collection)
                    errorCode = ((ODataOfflineException) ((ODataNetworkException) exception).getCause()).getCode();

                    // Display popup for Communication and Unauthorized errors
                    if(errorCode==Constants.Network_Error_Code_Offline
                            || errorCode==Constants.UnAuthorized_Error_Code_Offline
                            || errorCode==Constants.Unable_to_reach_server_offline
                            || errorCode==Constants.Resource_not_found
                            || errorCode==Constants.Unable_to_reach_server_failed_offline)
                    {

                        hasNoError =false;
                    }else{
                        hasNoError =true;
                    }

                } catch (Exception e) {
                    try {
                        String mStrErrMsg = exception.getCause().getLocalizedMessage();
                        if(mStrErrMsg.contains(Executing_SQL_Commnd_Error)){
                            hasNoError =false;
                            errorCode = -10001;
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
                if(errorCode!=0) {
                    LogManager.writeLogError("Error : [" + errorCode + "]" + exception.getMessage());
                }
            }else if(operation == Operation.GetStoreOpen.getValue()){
                // below error code getting from offline manger (While posting flush and refresh collection)
                try {
                    errorCode = ((ODataOfflineException) ((ODataNetworkException) exception).getCause()).getCode();

                    // Display popup for Communication and Unauthorized errors
                    if(errorCode==Constants.Network_Error_Code_Offline
                            || errorCode==Constants.UnAuthorized_Error_Code_Offline
                            || errorCode==Constants.Unable_to_reach_server_offline
                            || errorCode==Constants.Resource_not_found
                            || errorCode==Constants.Unable_to_reach_server_failed_offline)
                    {

                        hasNoError =false;
                    }else{
                        hasNoError =true;
                    }
                } catch (Exception e) {
                    try {
                        String mStrErrMsg = exception.getCause().getLocalizedMessage();
                        if(mStrErrMsg.contains(Store_Defining_Req_Not_Matched)){
                            hasNoError =false;
                            errorCode = -10247;
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }

            errorBean.setErrorCode(errorCode);
            if(exception.getMessage()!=null && !exception.getMessage().equalsIgnoreCase("")){
                errorBean.setErrorMsg(exception.getMessage());
            }else{
                errorBean.setErrorMsg(context.getString(R.string.unknown_error));
            }

            errorBean.setHasNoError(hasNoError);
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean isStoreFaied = false;
        if (errorBean.getErrorCode() == Constants.Resource_not_found
                || errorBean.getErrorCode() == Constants.Execu_SQL_Error_Code
                || errorBean.getErrorCode()==Constants.Store_Def_Not_matched_Code
            /* || errorBean.getErrorMsg().contains(Database_Transction_Failed_Error_Code+"")*/) {
//            isStoreFaied = OfflineManager.closeStore(context,
//                    OfflineManager.options,errorBean.getErrorMsg()+"",
//                    OfflineManager.offlineStore,Constants.PREFS_NAME,errorBean.getErrorCode()+"");
            OfflineManager.closeStoreGeo(context,
                    OfflineManager.optionsGeo,errorBean.getErrorMsg()+"",
                    OfflineManager.offlineGeo,Constants.PREFS_NAME,errorBean.getErrorCode()+"");
            Constants.ReIntilizeStore = isStoreFaied;

        }
        if (errorBean.getErrorMsg().contains(Constants.Build_Database_Failed_Error_Code1+"")
                || errorBean.getErrorMsg().contains( Constants.Build_Database_Failed_Error_Code2+"")
                || errorBean.getErrorMsg().contains(Constants.Build_Database_Failed_Error_Code3+"")
                ||  errorBean.getErrorCode() == Constants.Execu_SQL_Error_Code
                || errorBean.getErrorCode()==Constants.Store_Def_Not_matched_Code
            /*|| errorBean.getErrorMsg().contains(Database_Transction_Failed_Error_Code+"")*/) {
            if(errorBean.getErrorMsg().contains("500")
                    || errorBean.getErrorMsg().contains(Constants.RFC_ERROR_CODE_100029)
                    || errorBean.getErrorMsg().contains(Constants.RFC_ERROR_CODE_100027)){
                errorBean.setStoreFailed(false);
            }else{
//                OfflineManager.offlineStore = null;
//                OfflineManager.options = null;
                OfflineManager.offlineGeo = null;
                OfflineManager.optionsGeo = null;
                errorBean.setStoreFailed(true);
            }

        }else{
            errorBean.setStoreFailed(false);
        }

        return errorBean;
    }

    public static String getTimeDiff(String dateStart , String dateEnd){
        long diffSeconds = 00;
        long diffMinutes = 00;
        String strMinutes = "00";
        long diffHours = 00;
        String strHours = "00";
        long diffTime = 00;
        try {
            if (!TextUtils.isEmpty(dateStart) && !TextUtils.isEmpty(dateEnd)) {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                Date d1 = null;
                Date d2 = null;

                    d1 = format.parse(dateStart);
                    d2 = format.parse(dateEnd);

                    //in milliseconds
                    long diff = d2.getTime() - d1.getTime();

                     diffSeconds = diff / 1000 % 60;
                     diffMinutes = diff / (60 * 1000) % 60;
                     diffHours = diff / (60 * 60 * 1000) % 24;
            }else {
                return "00:00";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            if(diffHours==1 || diffHours==2 || diffHours==3 || diffHours==4 || diffHours==5 || diffHours==6 || diffHours==7 || diffHours==8 || diffHours==9){
                strHours = "0"+diffHours;
            }else {
                strHours = ""+diffHours;
            }
        } catch (Exception e) {
            strHours="00";
            e.printStackTrace();
        }

        try {
            if(diffMinutes==1 || diffMinutes==2 || diffMinutes==3 || diffMinutes==4 || diffMinutes==5 || diffMinutes==6 || diffMinutes==7 || diffMinutes==8 || diffMinutes==9){
                strMinutes = "0"+diffMinutes;
            }else {
                strMinutes = ""+diffMinutes;
            }
        } catch (Exception e) {
            strMinutes = "00";
            e.printStackTrace();
        }
        return strHours + ":"+ strMinutes;
    }

    public static String getTotalWorkingHour(String dateStart , String dateEnd){
        long diffHours = 00;
        long diffTime = 00;
        try {
            if (!TextUtils.isEmpty(dateStart) && !TextUtils.isEmpty(dateEnd)) {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                Date d1 = null;
                Date d2 = null;

                d1 = format.parse(dateStart);
                d2 = format.parse(dateEnd);

                //in milliseconds
                long diff = d2.getTime() - d1.getTime();
                diffHours = diff / (60 * 60 * 1000) % 24;
            }else {
                return "0";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ""+ diffHours;
    }

    public static String convertTimeFormat(String timeVal) {
        String[] hour=null;
        String[] hour1=null;
        String[] min=null;
        if(timeVal.contains("PT")) {
             hour = timeVal.split("PT");
        }
        if(hour!=null){
            if(hour[1].contains("H")){
                hour1 = hour[1].split("H");
            }
        }
        if(hour1!=null){
            if(hour1[1].contains("M")){
                min = hour1[1].split("M");
            }
        }

        String timeValue="00:00";

        String strHours="00";
        String strmin="00";
        try {
            if(hour1!=null) {
                if (hour1[0].equalsIgnoreCase("1") || hour1[0].equalsIgnoreCase("2") || hour1[0].equalsIgnoreCase("3") || hour1[0].equalsIgnoreCase("4") || hour1[0].equalsIgnoreCase("5") || hour1[0].equalsIgnoreCase("6") || hour1[0].equalsIgnoreCase("7") || hour1[0].equalsIgnoreCase("8") || hour1[0].equalsIgnoreCase("9")) {
                    strHours = "0" + hour1[0];
                } else {
                    strHours = "" + hour1[0];
                }
            }
        } catch (Exception e) {
            strHours="00";
            e.printStackTrace();
        }

        try {
            if(min!=null) {
                if (min[0].equalsIgnoreCase("1") || min[0].equalsIgnoreCase("2") || min[0].equalsIgnoreCase("3") || min[0].equalsIgnoreCase("4") || min[0].equalsIgnoreCase("5") || min[0].equalsIgnoreCase("6") || min[0].equalsIgnoreCase("7") || min[0].equalsIgnoreCase("8") || min[0].equalsIgnoreCase("9")) {
                    strmin = "0" + min[0];
                } else {
                    strmin = "" + min[0];
                }
            }
        } catch (Exception e) {
            strmin="00";
            e.printStackTrace();
        }
        timeValue = strHours + ":" + strmin;

        return timeValue;
    }
    public static String getErrorMessage(IReceiveEvent event, Context context) {
        String errorMsg = "";

        try {
            if (event.getReader() != null) {
                String responseBody = IReceiveEvent.Util.getResponseBody(event.getReader());
                if (event.getResponseStatusCode() != 401) {
                    try {
                        if(!responseBody.contains("html")) {
                            JSONObject jsonObject = null;

                            try {
                                jsonObject = new JSONObject(responseBody);
                                JSONObject errorObject = jsonObject.getJSONObject("error");
                                JSONObject erMesgObject = errorObject.getJSONObject("message");
                                errorMsg = erMesgObject.optString("value");
                            } catch (JSONException var7) {
                                var7.printStackTrace();
                                errorMsg = var7.getMessage();
                            }
                        }else {
                            errorMsg = responseBody;
                        }
                    } catch (Exception var8) {
                        var8.printStackTrace();
                        errorMsg = var8.getMessage();
                    }
                } else {
                    errorMsg = responseBody;
                }
            } else {
                errorMsg = context.getString(R.string.error_bad_req);
            }
        } catch (Throwable var9) {
            errorMsg = var9.getMessage();
            var9.printStackTrace();
        }

        return errorMsg;
    }

    public static void passwordStatusErrorMessage(final Context context, final JSONObject jsonObject,String loginUser) {
        try {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message = "";
                    String buttonMSG = "";
                    String errortext = "";
                    int code = 0;
                    if (jsonObject != null) {
                        if (jsonObject.has("code")) {
                            code = jsonObject.optInt("code");
                        }

                        if (jsonObject.has("message")) {
                            message = jsonObject.optString("message");
                        }
                        if (code != 200 && code != 0) {
                            if (message.equalsIgnoreCase(Constants.PASSWORD_LOCKED)) {
                                errortext = context.getString(R.string.password_lock_error_message);
                                com.arteriatech.mutils.common.UtilConstants.getUserlockMessage(context,loginUser, Configuration.IDPURL,Configuration.IDPTUSRNAME,Configuration.IDPTUSRPWD,Configuration.APP_ID);
                            } else {
                                if (message.equalsIgnoreCase(Constants.USER_INACTIVE)) {
                                    errortext = context.getString(R.string.user_inactive_error_message);
                                } else if (message.equalsIgnoreCase(Constants.PASSWORD_RESET_REQUIRED) || message.equalsIgnoreCase(Constants.PASSWORD_CHANGE_REQUIRED)) {
                                    errortext = context.getString(R.string.password_change_error_message);
                                } else if (message.equalsIgnoreCase(Constants.PASSWORD_DISABLED)) {
                                    errortext = context.getString(R.string.password_disable_error_message);
                                } else {
                                    errortext = context.getString(R.string.unauthorized_error_message);
                                }

                                if (message.equalsIgnoreCase(Constants.PASSWORD_CHANGE_REQUIRED) || message.equalsIgnoreCase(Constants.PASSWORD_RESET_REQUIRED)) {
                                    buttonMSG = context.getString(R.string.settings_extend_password);
                                } else {
                                    buttonMSG = context.getString(R.string.ok);
                                }
                                try {
                                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.UtilsDialogTheme);
                                    builder.setCancelable(false);

                                    final String finalMessage = message;
                                    builder.setMessage(errortext).setCancelable(false).setPositiveButton(buttonMSG, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            if(finalMessage.equalsIgnoreCase(Constants.PASSWORD_RESET_REQUIRED) || finalMessage.equalsIgnoreCase(Constants.PASSWORD_CHANGE_REQUIRED)){
                                                SharedPreferences sharedPerf = context.getSharedPreferences(Constants.PREFS_NAME, 0);
                                                String userName = sharedPerf.getString("username","");
                                                Intent intentSetting = new Intent(context, SecuritySettingActivity.class);
                                                RegistrationModel registrationModel = new RegistrationModel();
                                                registrationModel.setExtenndPwdReq(true);
                                                registrationModel.setUpdateAsPortalPwdReq(true);
                                                registrationModel.setIDPURL(Configuration.IDPURL);
                                                registrationModel.setUserName(userName);
                                                registrationModel.setShredPrefKey(Constants.PREFS_NAME);
                                                registrationModel.setExternalTUserName(Configuration.IDPTUSRNAME);
                                                registrationModel.setExternalTPWD(Configuration.IDPTUSRPWD);
                                                intentSetting.putExtra(UtilConstants.RegIntentKey, registrationModel);
                                                context.startActivity(intentSetting);
                                            }
                                        }
                                    });
                                    builder.show();
                                } catch (Exception var8) {
                                    var8.printStackTrace();
                                }
                            }
                        }
                    }
                }
            });

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    public static String getLastDateToTillDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
        Date lastDayOfMonth = cal.getTime();
        String currentDateTimeString1 = (String) android.text.format.DateFormat.format("yyyy-MM-dd", lastDayOfMonth);
        return getTimeformat2(currentDateTimeString1, (String) null);
    }

    public static String getTimeformat2(String date, String time) {
        String datefrt = "";
        datefrt = "00:00:00";
        String currentDateTimeString = date + "T" + datefrt;
        return currentDateTimeString;
    }
    public static HashMap<String, String> getBlockCustomerKeyValues(JSONArray jsonItem) {
        HashMap<String, String> blockCustomer = new HashMap<>();
        try {
            for(int i=0;i<jsonItem.length();i++){
                JSONObject jsonObject = jsonItem.getJSONObject(i);
                blockCustomer.put(jsonObject.optString(Constants.application)+"_"+jsonObject.optString(Constants.salesArea),jsonObject.optString(Constants.isBlocked));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blockCustomer;
    }
    public static ArrayList<SOItemBean> exchangeBean(SOItemBean oldbean, SOItemBean newbean) {
        ArrayList<SOItemBean> list=new ArrayList<>();
        SOItemBean bean = new SOItemBean();
        bean.setSoQty(oldbean.getSoQty());
        bean.setAlternateWeight(oldbean.getAlternateWeight());
        bean.setBrand(oldbean.getBrand());
        bean.setCalTonVal(oldbean.getCalTonVal());
        bean.setDelvStatusID(oldbean.getDelvStatusID());
        bean.setDiscount(oldbean.getDiscount());
        bean.setFreight(oldbean.getFreight());
        bean.setHighLevellItemNo(oldbean.getHighLevellItemNo());
        bean.setItemCategory(oldbean.getItemCategory());
        bean.setItemFlag(oldbean.getItemFlag());
        bean.setMaterialGroupDesc(oldbean.getMaterialGroupDesc());
        bean.setMaterialGroupID(oldbean.getMaterialGroupID());
        bean.setNetWeight(oldbean.getNetWeight());
        bean.setNetWeightUOM(oldbean.getNetWeightUOM());
        bean.setSONo(oldbean.getSONo());
        bean.setStatusID(oldbean.getStatusID());
        bean.setUnitPrice(oldbean.getUnitPrice());
        bean.setConditionItemDetaiBeanArrayList(oldbean.getConditionItemDetaiBeanArrayList());
        bean.setCurrency(oldbean.getCurrency());
        bean.setCustomerPo(oldbean.getCustomerPo());
        bean.setDecimalCheck(false);
        bean.setDelvQty(oldbean.getDelvQty());
        bean.setDistChannel(oldbean.getDistChannel());
        bean.setDivision(oldbean.getDivision());
        bean.setDocDate(oldbean.getDocDate());
        bean.setEditAndApproveQty(oldbean.getEditAndApproveQty());
        bean.setIncoTerm1Text(oldbean.getIncoTerm1Text());
        bean.setButtonOnClick(false);
        bean.setItemNo(oldbean.getItemNo());
        bean.setLandingPrice(oldbean.getLandingPrice());
        bean.setMatCode(oldbean.getMatCode());
        bean.setMatDesc(oldbean.getMatDesc());
        bean.setMatNoAndDesc(oldbean.getMatNoAndDesc());
        bean.setNetAmount(oldbean.getNetAmount());
        bean.setQuantity(oldbean.getQuantity());
        bean.setSoQty(oldbean.getSoQty());
        bean.setTaxAmount(oldbean.getTaxAmount());
        bean.setUom(oldbean.getUom());
        list.add(bean);
        return list;
    }

}

