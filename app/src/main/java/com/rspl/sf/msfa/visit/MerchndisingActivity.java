package com.rspl.sf.msfa.visit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.CustomerDetailsActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.DialogCallBack;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.impl.ODataDurationDefaultImpl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by ${e10526} on ${17-12-2016}.
 */
@SuppressLint("NewApi")
public class MerchndisingActivity extends AppCompatActivity implements View.OnClickListener, UIListener {
    private EditText editRemraks;
    private ImageView ivThumbnailPhoto;
    private static final int TAKE_PICTURE = 1;
    private boolean mBooleanPictureTaken = false;
    private String[][] arrMerchType = null;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    private String mStrEncodedFont = "";
    private String mStrSelMerchndisingType = "", mStrMerchReviewTypeDesc = "", mStrRemarksMandatoryFlag = "";
    String mStrComingFrom = "";
    private int mLongBitmapSize = 0;
    private String defaultCameraPackage = "";
    private String mStrBundleRetailerUID = "", mStrBundleCPGUID32 = "";
    Spinner spinnerSnapType;
    Button btnClickAction;
    TextView tvRetName = null, tvUID = null, tv_remarks_mandatory = null;

    private boolean mBoolHeaderPosted = false;
    Hashtable tableItm;
    Hashtable tableHdr;
    private String selectedImagePath, filename = "";
    String strMimeType = null;
    String mimeType = null;
    private ProgressDialog pdLoadDialog=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
       // ActionBarView.initActionBarView(this, true, getString(R.string.lbl_merchndising));
        setContentView(R.layout.activity_merchndising_snap);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_Sales_order_details), 0);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
        }
        initUI();
        getMerchandisingTypes();
        setViuesIntoUI();
    }

    // TODO Initialize UI
    private void initUI() {
        tv_remarks_mandatory = (TextView) findViewById(R.id.tv_merch_remarks_mandatory);
        tvRetName = (TextView) findViewById(R.id.tv_reatiler_name);
        tvUID = (TextView) findViewById(R.id.tv_reatiler_id);
        spinnerSnapType = (Spinner) findViewById(R.id.sp_merch_snap_type);
        editRemraks = (EditText) findViewById(R.id.edit__merch_remarks);
        ivThumbnailPhoto = (ImageView) findViewById(R.id.image_merch_ThumbnailPhoto);
        btnClickAction = (Button) findViewById(R.id.btn_mrach_take_pic);

    }

    // TODO set values to UI
    private void setViuesIntoUI() {
        tvRetName.setText(mStrBundleRetName);
        tvUID.setText(mStrBundleRetailerUID);

        editRemraks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editRemraks.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ArrayAdapter<String> arrayAdepterMerchandisingTypeValues = new ArrayAdapter<>(this, R.layout.custom_textview, arrMerchType[1]);
        arrayAdepterMerchandisingTypeValues.setDropDownViewResource(R.layout.spinnerinside);
        spinnerSnapType.setAdapter(arrayAdepterMerchandisingTypeValues);
        spinnerSnapType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                mStrSelMerchndisingType = arrMerchType[0][position];
                mStrMerchReviewTypeDesc = arrMerchType[1][position];
                mStrRemarksMandatoryFlag = arrMerchType[2][position];
                spinnerSnapType.setBackgroundResource(R.drawable.spinner_bg);
                if (mStrRemarksMandatoryFlag.equalsIgnoreCase(Constants.X)) {
                    tv_remarks_mandatory.setText(getString(R.string.star));
                } else {
                    editRemraks.setBackgroundResource(R.drawable.edittext);
                    tv_remarks_mandatory.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        btnClickAction.setOnClickListener(this);
    }

    // TODO get Merchandising type values from valuehelps table
    private void getMerchandisingTypes() {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq 'MerchReviewType'";
            arrMerchType = OfflineManager.getConfigListWithDefaultValAndNone(mStrConfigQry, Constants.PROP_MER_TYPE);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
       // fillDummyValues();
        if (arrMerchType == null) {
            arrMerchType = new String[4][2];
            arrMerchType[0][0] = "";
            arrMerchType[1][0] = Constants.None;

            arrMerchType[2][0] = Constants.X;
            arrMerchType[3][0] = Constants.str_false;

        }
    }

    private void fillDummyValues() {
        arrMerchType = new String[4][2];
        arrMerchType[0][1] = "1";
        arrMerchType[1][1] = "Competitor Information";

        arrMerchType[2][1] = Constants.X;
        arrMerchType[3][1] = Constants.str_false;


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_mrach_take_pic:

                PackageManager packageManager = getPackageManager();
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
                startActivityForResult(intentResult, TAKE_PICTURE);
                break;
            case R.id.tv_back:
                onBackPressed();
                break;
            case R.id.tv_submit:
                onValidationCheck();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == TAKE_PICTURE && resultCode == RESULT_OK && intent != null) {
            // get bundle
            Bundle bundleExtrasResult = intent.getExtras();
            // get bitmap

            final Bitmap bitMap = (Bitmap) bundleExtrasResult.get("data");


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            assert bitMap != null;
            bitMap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageInByte = stream.toByteArray();
            mLongBitmapSize = imageInByte.length;

            ivThumbnailPhoto.setImageBitmap(bitMap);

            ivThumbnailPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openImagePopUp(bitMap);
                }
            });
            String[] projection = {MediaStore.Images.Media.DATA};
            @SuppressWarnings("deprecation")
            Cursor cursorMediaValue = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
            cursorMediaValue.moveToLast();

            mBooleanPictureTaken = true;
            mStrEncodedFont = BitMapToString(bitMap);

            filename = (System.currentTimeMillis() + "");

            File fileName = Constants.SaveImageInDevice(filename, bitMap);
            selectedImagePath = fileName.getPath();

            //mime


            strMimeType = MimeTypeMap.getFileExtensionFromUrl(selectedImagePath);
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    strMimeType);


        }

    }


    private void openImagePopUp(Bitmap bitMap) {

        //1. Get screen size
        Display display = MerchndisingActivity.this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;
        //2. Get Target image size
        int bitmapHeight = bitMap.getHeight();
        int bitmapWidth = bitMap.getWidth();
        //3. Scale the image down to fit perfectly into the screen
        //The value (250 in this case) must be adjusted for phone/tables displays
        while (bitmapHeight > (screenHeight - 250) || bitmapWidth > (screenWidth - 250)) {
            bitmapHeight = bitmapHeight / 2;
            bitmapWidth = bitmapWidth / 2;
        }
        //4. Create resized bitmap image
        BitmapDrawable resizedBitmap = new BitmapDrawable(MerchndisingActivity.this.getResources(), Bitmap.createScaledBitmap(bitMap, bitmapWidth, bitmapHeight, false));
        // 5. Create Dialog
        Dialog dialog = new Dialog(MerchndisingActivity.this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alertdailog_imagepreview);
        ImageView image = (ImageView) dialog.findViewById(R.id.iv_previewimage);
        //6. Do here setBackground() instead of setImageDrawable()
        image.setBackground(resizedBitmap);
        //7. Without this line there is a very small border around the image (1px)
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(null);
        //8. Show the dialog
        dialog.show();
    }


    /**
     * this method for image save in local storage and storage path assigned to
     * text view variable.
     */
    private String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }


    // TODO check validation
    private void onValidationCheck() {
        if ((mStrRemarksMandatoryFlag.equalsIgnoreCase(Constants.X) &&
                editRemraks.getText().toString().trim().equalsIgnoreCase("")) || mStrSelMerchndisingType.equalsIgnoreCase("")) {
            if (mStrSelMerchndisingType.equalsIgnoreCase("")) {
                spinnerSnapType.setBackgroundResource(R.drawable.error_spinner);
            }

            if (mStrRemarksMandatoryFlag.equalsIgnoreCase(Constants.X)) {
                if (editRemraks.getText() == null || editRemraks.getText().toString().trim().equalsIgnoreCase("")) {
                    editRemraks.setBackgroundResource(R.drawable.edittext_border);
                }
            }

            UtilConstants.showAlert(getString(R.string.validation_plz_enter_mandatory_flds), MerchndisingActivity.this);
        } else {
            if (mBooleanPictureTaken) {
                if (Constants.checkPermission(MerchndisingActivity.this)) {
                    checkGPS();
                } else {
                    requestPermission(MerchndisingActivity.this);
                }

            } else {
                UtilConstants.showAlert(getString(R.string.take_pic), MerchndisingActivity.this);

            }
        }

    }
    public void requestPermission(Activity activity){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)){
            ActivityCompat.requestPermissions(
                    activity,
                    Constants.PERMISSIONS_LOCATION,Constants.PERMISSION_REQUEST_CODE
            );
        } else if (Constants.getPermissionStatus(MerchndisingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                && Constants.getPermissionStatus(MerchndisingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Constants.dialogBoxWithButton(MerchndisingActivity.this, "",
                    getString(R.string.this_app_needs_location_permission), getString(R.string.enable),
                    getString(R.string.later), new DialogCallBack() {
                        @Override
                        public void clickedStatus(boolean clickedStatus) {
                            if (clickedStatus) {
                                Constants.navigateToAppSettingsScreen(MerchndisingActivity.this);
                            }
                        }
                    });

        } else {
            ActivityCompat.requestPermissions(MerchndisingActivity.this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.PERMISSION_REQUEST_CODE);
        }

        Constants.setPermissionStatus(MerchndisingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION, true);
        Constants.setPermissionStatus(MerchndisingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION, true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_merchindising, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_save:
                onValidationCheck();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MerchndisingActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_create_merchndising).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        onNavigateToRetDetilsActivity();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();
    }
    private void closingProgressDialog(){
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void checkGPS(){
       /* if (Constants.onGpsCheck(MerchndisingActivity.this)) {
            if (UtilConstants.getLocation(MerchndisingActivity.this)) {
                mBoolHeaderPosted = false;
                onSaveDB();
            }
        }*/
        pdLoadDialog = Constants.showProgressDialog(MerchndisingActivity.this,"",getString(R.string.gps_progress));
        Constants.getLocation(MerchndisingActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                closingProgressDialog();
                if(status){
                    mBoolHeaderPosted = false;
                    onSaveDB();
                }
            }
        });
    }
    // TODO save values into offline store(DataBase)
    private void onSaveDB() {

        try {
            GUID mStrGuide = GUID.newRandom();
            tableHdr = new Hashtable();
            Hashtable visitActivityTable = new Hashtable();
            //noinspection unchecked
            tableHdr.put(Constants.MerchReviewGUID, mStrGuide.toString());
            //noinspection unchecked
            tableHdr.put(Constants.Remarks, editRemraks.getText().toString().trim());
            //noinspection unchecked
            tableHdr.put(Constants.CPNo, UtilConstants.removeLeadingZeros(mStrBundleRetID));
            //noinspection unchecked
            tableHdr.put(Constants.CPGUID, mStrBundleCPGUID32);

            tableHdr.put(Constants.SPGUID, Constants.getSPGUID(Constants.SPGUID));

            //noinspection unchecked
            tableHdr.put(Constants.MerchReviewType, mStrSelMerchndisingType);


            tableHdr.put(Constants.MerchReviewTypeDesc, mStrMerchReviewTypeDesc);
            //noinspection unchecked
            tableHdr.put(Constants.MerchReviewDate, UtilConstants.getNewDateTimeFormat());
            //noinspection unchecked
            tableHdr.put(Constants.MerchReviewLat, BigDecimal.valueOf(UtilConstants.latitude));
            //noinspection unchecked
            tableHdr.put(Constants.MerchReviewLong, BigDecimal.valueOf(UtilConstants.longitude));


            tableHdr.put(Constants.CPTypeID, Constants.getName(Constants.ChannelPartners, Constants.CPTypeID, Constants.CPNo, mStrBundleRetID));
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);

            String loginIdVal = sharedPreferences.getString(Constants.username, "");
            //noinspection unchecked
            tableHdr.put(Constants.LOGINID, loginIdVal);

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

            tableHdr.put(Constants.MerchReviewTime, oDataDuration);

            //Todo set values to data vault
            Constants.saveDeviceDocNoToSharedPref(MerchndisingActivity.this, Constants.MerchList, mStrGuide.toString().toUpperCase());
            Constants.storeInDataVault(mStrGuide.toString().toUpperCase(), filename + "." + strMimeType,this);

            tableItm = new Hashtable();

            try {
                //noinspection unchecked
                tableItm.put(Constants.MerchReviewGUID, mStrGuide.toString());
                GUID mStrImgGuide = GUID.newRandom();
                //noinspection unchecked
                tableItm.put(Constants.MerchImageGUID, mStrImgGuide.toString());
                //noinspection unchecked
                tableItm.put(Constants.ImageMimeType, mimeType);
                //noinspection unchecked
                tableItm.put(Constants.ImageSize, mLongBitmapSize);
                //noinspection unchecked
                tableItm.put(Constants.Image, mStrEncodedFont);

                tableItm.put(Constants.ImagePath, selectedImagePath);
                tableItm.put(Constants.FileName, filename + "." + strMimeType);


            } catch (Exception exception) {
                exception.printStackTrace();
            }


            //========>Start VisitActivity
            String mStrSPGUID = Constants.getSPGUID(Constants.SPGUID);
            String getVisitGuidQry = Constants.Visits + "?$filter=EndDate eq null and CPGUID eq '" + mStrBundleCPGUID32.toUpperCase() + "' " +
                    "and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and "+Constants.SPGUID+" eq guid'"+mStrSPGUID+"'";
            ODataGuid mGuidVisitId = null;
            try {
                mGuidVisitId = OfflineManager.getVisitDetails(getVisitGuidQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }

            visitActivityTable.put(Constants.ActivityRefID, mStrGuide.toString());

            mStrGuide = GUID.newRandom();
            visitActivityTable.put(Constants.VisitActivityGUID, mStrGuide.toString());
            visitActivityTable.put(Constants.LOGINID, loginIdVal);
            visitActivityTable.put(Constants.VisitGUID, mGuidVisitId.guidAsString36());
            visitActivityTable.put(Constants.ActivityType, "01");
            visitActivityTable.put(Constants.ActivityTypeDesc, Constants.Merchendising_Snap);


            try {
                OfflineManager.createVisitActivity(visitActivityTable);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            //========>End VisitActivity

            try {
                //noinspection unchecked
                OfflineManager.createMerChndisingHeader(tableHdr, MerchndisingActivity.this);
            } catch (OfflineODataStoreException e) {
                //                    e.printStackTrace();
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void saveItemEntityToTable() {
        try {
            //noinspection unchecked
            OfflineManager.createMerChndisingItem(tableItm, tableHdr, MerchndisingActivity.this);
        } catch (OfflineODataStoreException e) {
            //                    e.printStackTrace();
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }


    @Override
    public void onRequestError(int operation, Exception e) {
        UtilConstants.showAlert(getString(R.string.error_occured_during_save), MerchndisingActivity.this);
    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {

        if (operation == Operation.Create.getValue() && mBoolHeaderPosted) {
            backToVisit();
        } else if (operation == Operation.Create.getValue() && !mBoolHeaderPosted) {
            mBoolHeaderPosted = true;
            saveItemEntityToTable();
        }


    }

    private void backToVisit() {
        String popUpText = getString(R.string.msg_snap_shot_created);
        AlertDialog.Builder builder = new AlertDialog.Builder(
                MerchndisingActivity.this, R.style.MyTheme);
        builder.setMessage(popUpText)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();
                                    onNavigateToRetDetilsActivity();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
        builder.show();
    }

    private void onNavigateToRetDetilsActivity() {
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(MerchndisingActivity.this, CustomerDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        if (!Constants.OtherRouteNameVal.equalsIgnoreCase("")) {
            intentNavPrevScreen.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
            intentNavPrevScreen.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
        }
        startActivity(intentNavPrevScreen);
    }
}
