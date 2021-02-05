package com.rspl.sf.msfa.attendance;

import android.content.Context;
import android.content.SharedPreferences;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.impl.ODataDurationDefaultImpl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Hashtable;

/**
 * Created by e10742 on 5/30/2017.
 */

public class AttendanceJKT {
    /*Save Day start data on offline store*/
    public static void onSaveDayStartData(Context context, UIListener uiListener) {
        try {


            Constants.MapEntityVal.clear();
            GUID guid = GUID.newRandom();
            Hashtable hashTableAttendanceValues = new Hashtable();
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
            String loginIdVal = sharedPreferences.getString(Constants.username, "");

            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.LOGINID, loginIdVal);
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.AttendanceGUID, guid.toString());
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.StartDate, UtilConstants.getNewDateTimeFormat());

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

            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.StartTime, oDataDuration);
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.StartLat, BigDecimal.valueOf(UtilConstants.latitude));
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.StartLong, BigDecimal.valueOf(UtilConstants.longitude));
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.EndLat, "");
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.EndLong, "");
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.EndDate, "");
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.EndTime, "");

            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.Remarks, "");

            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.AttendanceTypeH1, "");
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.AttendanceTypeH2, "");


            hashTableAttendanceValues.put(Constants.SPGUID, Constants.getSPGUID(Constants.SPGUID));

            hashTableAttendanceValues.put(Constants.SetResourcePath, "guid'" + guid.toString() + "'");

            SharedPreferences sharedPreferencesVal = context.getSharedPreferences(Constants.PREFS_NAME, 0);
            SharedPreferences.Editor editor = sharedPreferencesVal.edit();
            editor.putInt(Constants.VisitSeqId, 0);
            editor.commit();
            try {
                //noinspection unchecked
                OfflineManager.createAttendance(hashTableAttendanceValues, uiListener);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }

        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }
}
