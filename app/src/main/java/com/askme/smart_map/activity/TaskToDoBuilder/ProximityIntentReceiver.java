package com.askme.smart_map.activity.TaskToDoBuilder;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

import com.askme.smart_map.activity.DatabaseBuilder.DBadapter;
import com.yalantis.guillotine.sample.R;

public class ProximityIntentReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 1000;
    DBadapter dBadapter;
    String tickerString="";
    PendingIntent notifyUserIntent;

    @Override
    public void onReceive(Context context, Intent intent) {


        int flag = Integer.parseInt(intent.getStringExtra("taskToDoLocationFlag"));
        String phoneNumber = intent.getStringExtra("taskToDoLocationNumber");
        String content = intent.getStringExtra("taskToDoLocationContent");
        String locationName=intent.getStringExtra("taskToDoLocationName");
        double taskToDoNumberLat = Double.parseDouble(intent.getStringExtra("taskToDoLocationLat"));
        double taskToDoNumberLng = Double.parseDouble(intent.getStringExtra("taskToDoLocationLng"));


        String key = LocationManager.KEY_PROXIMITY_ENTERING;

        Boolean entering = intent.getBooleanExtra(key, false);

        if (entering) {
            Log.d(getClass().getSimpleName(), "entering");
        }
        else {
            Log.d(getClass().getSimpleName(), "exiting");
        }

        dBadapter=new DBadapter(context);

        Boolean sendSMS= intent.getBooleanExtra("taskToDoSENDSMS", false);
        if(sendSMS)
        {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber,null,content,null,null);
            tickerString="Sms has been send to "+phoneNumber;
            notifyUserIntent = PendingIntent.getActivity(context, flag, new Intent(context, Saved_message.class), PendingIntent.FLAG_ONE_SHOT);
        }
        else {
            notifyUserIntent = PendingIntent.getActivity(context, flag, new Intent(context, Saved_notification.class), PendingIntent.FLAG_ONE_SHOT);
            tickerString="Reminder!!!\nTitle: "+phoneNumber+"\nNote: "+content;
        }

        dBadapter.UpdateTaskStatus(flag + "");


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(phoneNumber)
                .setContentText(content)
                .setTicker(tickerString);


        mBuilder.setContentIntent(notifyUserIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        mBuilder.setAutoCancel(false);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, mBuilder.build());


    }


}
