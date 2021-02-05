package com.rspl.sf.msfa;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.widget.Toast;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.mbo.CustomerBean;
import com.rspl.sf.msfa.store.OfflineManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MapActivity extends Activity {

    // Google Map
    GoogleMap googleMap;
    ArrayList<LatLng> points;
    ArrayList<CustomerBean> listRetailers;
    private String mStrComingFrom = "";
    public static final int REQ_PERMISSION = 1;
    private String mStrOtherRouteguid="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mStrComingFrom = extras.getString(Constants.NAVFROM);
            mStrOtherRouteguid= extras.getString(Constants.OtherRouteGUID);
        }

        getLatLongValues();

        displayMap();


    }




    /*
           TODO Initialize map
                */
    private void displayMap() {
        points = new ArrayList<>();
        try {
// Changing map type
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            // Showing / hiding your current location
            //check permission for location enabled
            if (checkLocationPermission()) {
                googleMap.setMyLocationEnabled(true);
            } else {
                askLocationPermission();
            }


            // Enable / Disable zooming controls
            googleMap.getUiSettings().setZoomControlsEnabled(false);

            // Enable / Disable my location button
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            // Enable / Disable Compass icon
            googleMap.getUiSettings().setCompassEnabled(true);

            // Enable / Disable Rotate gesture
            googleMap.getUiSettings().setRotateGesturesEnabled(true);

            // Enable / Disable zooming functionality
            googleMap.getUiSettings().setZoomGesturesEnabled(true);

            CustomerBean retailerBean;
            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
            if (listRetailers != null && listRetailers.size() > 0) {
                for (int k = 0; k < listRetailers.size(); k++) {

                    int addMarkerValue = k + 1;
                    String markerValue = String.valueOf(addMarkerValue);
                    System.out.println("Marker Value:" + markerValue);
                    retailerBean = listRetailers.get(k);
                    String mStrSPGUID = Constants.getSPGUID(Constants.SPGUID);
                    String mStrVisitStartedQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "'  " +
                            "and CPGUID eq '" + retailerBean.getCpGuidStringFormat().toUpperCase() + "' and "+Constants.SPGUID+" eq guid'"+mStrSPGUID+"'";

                    /*if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartedQry)) {
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(retailerBean.getLatVal(), retailerBean.getLongVal()))
                                .icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.ic_mark_green, markerValue))).title("" + retailerBean.getRetailerName()).snippet("" + retailerBean.getMobileNumber()));
                    } else {
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(retailerBean.getLatVal(), retailerBean.getLongVal())).
                                icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.ic_mark, markerValue))).title("" + retailerBean.getRetailerName()).snippet("" + retailerBean.getMobileNumber()));
                    }*/

                    options.add(new LatLng(retailerBean.getLatVal(), retailerBean.getLongVal()));
                }
                googleMap.addPolyline(options);
                retailerBean = listRetailers.get(listRetailers.size() / 2);

                if (retailerBean.getLatVal() == 0 && retailerBean.getLongVal() == 0) {
                    for (int k = 0; k < listRetailers.size(); k++) {
                        retailerBean = listRetailers.get(k);


                        if (retailerBean.getLatVal() != 0 && retailerBean.getLongVal() != 0) {

                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(retailerBean.getLatVal(), retailerBean.getLongVal())).zoom(12).build();

                            googleMap.animateCamera(CameraUpdateFactory
                                    .newCameraPosition(cameraPosition));
                            break;
                        }

                        if (k == listRetailers.size() - 1) {
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(28.753189,
                                            77.056377)).zoom(8).build();

                            googleMap.animateCamera(CameraUpdateFactory
                                    .newCameraPosition(cameraPosition));
                        }


                    }
                } else {

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(retailerBean.getLatVal(), retailerBean.getLongVal())).zoom(12).build();

                    googleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                }
            } else {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(28.753189,
                                77.056377)).zoom(8).build();

                googleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
           If location is not enabled this method ask permission to enabled
     */
    private void askLocationPermission() {
        ActivityCompat.requestPermissions(
                MapActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQ_PERMISSION
        );
    }

    /*
    To check location permission
     */
    private boolean checkLocationPermission() {

        return (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);


    }

    /*
    When user requests the permission to enable the Location
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    if (checkLocationPermission())
                        googleMap.setMyLocationEnabled(true);

                } else {
                    // Permission denied
                    Toast.makeText(MapActivity.this, "Permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private Bitmap writeTextOnDrawable(int drawableId, String mapMarkerValue) {

        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId)
                .copy(Bitmap.Config.ARGB_8888, true);

        Typeface tf = Typeface.create("Gill Sans Ultra Bold", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(convertToPixels(MapActivity.this, 11));

        Rect textRect = new Rect();
        paint.getTextBounds(mapMarkerValue, 0, mapMarkerValue.length(), textRect);

        Canvas canvas = new Canvas(bm);

        //If the text is bigger than the canvas , reduce the font size
        if (textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
            paint.setTextSize(convertToPixels(MapActivity.this, 10));        //Scaling needs to be used for different dpi's

        //Calculate the positions
        int xPos = (canvas.getWidth() / 2) - 3;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) ((canvas.getHeight() / 3) - ((paint.descent() + paint.ascent()) / 3));

        canvas.drawText(mapMarkerValue, xPos, yPos, paint);

        return bm;


    }

    public static int convertToPixels(MapActivity mapActivity, int i) {
        final float conversionScale = mapActivity.getResources().getDisplayMetrics().density;

        return (int) ((i * conversionScale) + 0.5f);
    }

    /*
           TODO Get Latitude and longitude values from Channel partner tables based on today route.
        */
    private void getLatLongValues() {
        if (mStrComingFrom.equalsIgnoreCase(Constants.BeatPlan)) {

            if(mStrOtherRouteguid.equalsIgnoreCase("")){
                String routeQry = Constants.RoutePlans + "?$filter=" + Constants.VisitDate + " eq datetime'" + UtilConstants.getNewDate() + "'";
                try {
                    listRetailers = OfflineManager.getRetailerListForRoute(routeQry);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }




            }else{
                String qryForTodaysBeat = Constants.RouteSchedulePlans+ "?$filter=" + Constants.RouteSchGUID+ " eq guid'" + mStrOtherRouteguid.toUpperCase()+"' &$orderby="+ Constants.SequenceNo+"";

                try {
                    listRetailers = OfflineManager.getRetailerListForOtherBeats(qryForTodaysBeat);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }

            if (listRetailers != null && listRetailers.size() > 0) {
                Collections.sort(listRetailers, new Comparator<CustomerBean>() {
                    public int compare(CustomerBean one, CustomerBean other) {
                        return one.getRetailerName().compareTo(other.getRetailerName());
                    }
                });
            }

           /* try {

                String routeQry = Constants.RoutePlans + "?$filter=" + Constants.VisitDate + " eq datetime'" + UtilConstants.getNewDate() + "'";
                listRetailers = OfflineManager.getRetailerListForRoute(routeQry);

                if (listRetailers != null && listRetailers.size() > 0) {
                    Collections.sort(listRetailers, new Comparator<CustomerBean>() {
                        public int compare(CustomerBean one, CustomerBean other) {
                            return one.getRetailerName().compareTo(other.getRetailerName());
                        }
                    });
                }

            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }*/
        } else {
            try {

                listRetailers = OfflineManager.getRetailerLatLongValues(Constants.ChannelPartners + "?$filter=(" + Constants.CPNo + " ne '' and " + Constants.CPNo + " ne null)" +
                        " &$orderby=" + Constants.RetailerName + "%20asc");

            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
        }
    }
}
