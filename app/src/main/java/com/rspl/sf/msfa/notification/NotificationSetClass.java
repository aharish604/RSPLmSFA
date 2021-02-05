package com.rspl.sf.msfa.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.appointment.AppointmentBean;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.store.OfflineManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Created by e10762 on 11-01-2017.
 *
 */

public class NotificationSetClass
{
    private Context context = null;
    private int appointementYear  = 0;
    private int appointementMonth = 0;
    private int appointementDay = 0;
    private int appointementHour = 0;
    private int appointementMin = 0;
    private int appointementEndHour = 0;
    private int appointementEndMin = 0;
    private String[] timePeriods;
    private String mStrRetailerName = "";
    private String mStrActivityType = "";
    private String mStrStartTime = "";
    private String mStrEndTime = "";

    public NotificationSetClass(Context context) {
        this.context = context;
        getAppointmentList();
    }



    private void getAppointmentList()
    {
        String mStrSPGUID = Constants.getSPGUID(Constants.SPGUID);
        String query = Constants.Visits+"?$filter="+Constants.StatusID+" eq '00' and "+Constants.PlannedDate+" ge datetime'"+ UtilConstants.getNewDate()+"'  and "+Constants.SPGUID+" eq guid'"+mStrSPGUID+"'";
        ArrayList<AppointmentBean> appointmentList = null;
        try
        {
            appointmentList = OfflineManager.getAppointmentList(query);
            if(appointmentList.size()>0)
            {

                for(int i=0;i<appointmentList.size();i++)
                {
                    if(appointmentList.get(i).getPlannedDate()!=null && appointmentList.get(i).getPlannedStartTime()!=null)
                    {
                        setDateParametersForNotification(appointmentList.get(i).getPlannedDate(),appointmentList.get(i).getPlannedStartTime());

                         mStrRetailerName = Constants.getNameByCPGUID(Constants.ChannelPartners,Constants.Name,Constants.CPGUID,Constants.convertStrGUID32to36(appointmentList.get(i).getCPGUID()));
                        mStrActivityType = appointmentList.get(i).getVisitTypeDesc();
                        convertTimeInto24hrFormat(appointmentList.get(i).getPlannedEndTime());
                        scheduleNotification(mStrRetailerName,5000);
                    }
                }


            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

    }


    private void convertTimeInto24hrFormat(String timeInString)
    {
        String mStrConvertTime =UtilConstants.convertTimeOnly(timeInString);
        String[] splitTime = mStrConvertTime.split(":");
        try {
            appointementEndHour = Integer.parseInt(splitTime[0]);
            appointementMin = Integer.parseInt(splitTime[1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void setDateParametersForNotification(String dateInString,String timeInString)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
        String mStrConvertTime =UtilConstants.convertTimeOnly(timeInString);
        String[] splitTime = mStrConvertTime.split(":");
        try {
            appointementHour = Integer.parseInt(splitTime[0]);
            appointementMin = Integer.parseInt(splitTime[1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try
        {
            Date date = formatter.parse(dateInString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
             appointementYear = cal.get(Calendar.YEAR);
             appointementMonth = cal.get(Calendar.MONTH);
             appointementDay = cal.get(Calendar.DAY_OF_MONTH);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    private void scheduleNotification(String content, int delay)
    {
        try
        {
            timePeriods = OfflineManager.getAppointmentTimeConfigList(Constants.ConfigTypesetTypes+"?$filter= Typeset eq 'APNRMD'");
        }
        catch (OfflineODataStoreException e)
        {
            e.printStackTrace();
        }


        for(int i=0;i<timePeriods.length;i++)
        {
            mStrStartTime = Constants.convert24hrFormatTo12hrFormat(appointementHour+":"+appointementMin);
            mStrEndTime = Constants.convert24hrFormatTo12hrFormat(appointementEndHour+":"+appointementEndMin);

            Notification notification = getNotification(content);

            Intent notificationIntent = new Intent(context, NotificationPublisher.class);
            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,i, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            notificationManager.notify(0, notification);

            long futureInMillis = SystemClock.elapsedRealtime() + delay;
            long currentTimeInMIllis = SystemClock.elapsedRealtime() + delay;


            int timInterval = Integer.parseInt(timePeriods[i]);


            Calendar cal = Calendar.getInstance();

            cal.setTimeInMillis(System.currentTimeMillis());
            cal.clear();
//            cal.set(Calendar.YEAR,appointementYear);
//            cal.set(Calendar.MONTH,appointementMonth);
//            cal.set(Calendar.DAY_OF_MONTH,appointementDay);
//            cal.set(Calendar.HOUR,appointementHour);
//            cal.set(Calendar.MINUTE,appointementMin);
//            cal.set(Calendar.SECOND,00);

            cal.set(appointementYear,appointementMonth,appointementDay,appointementHour,appointementMin);
            cal.add(Calendar.MINUTE,-timInterval);


            Calendar cal2 = Calendar.getInstance();
            cal2.setTimeInMillis(System.currentTimeMillis());
            cal2.clear();
            cal2.setTimeInMillis(currentTimeInMIllis);




            /*if(cal.after(cal2)) {

                futureInMillis = cal.getTimeInMillis();
                AlarmManager alarmManager;
                alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
            }*/

      if(cal.after(cal2))
            {


                futureInMillis = cal.getTimeInMillis();
                AlarmManager alarmManager;
                    alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);

            }


        }


    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(content);
        builder.setContentText(mStrActivityType+" "+mStrStartTime+" - "+mStrEndTime);
//        builder.setSmallIcon(R.drawable.emami);
        return builder.build();
    }

}
