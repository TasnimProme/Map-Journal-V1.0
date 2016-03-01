package com.askme.smart_map.activity.TaskToDoBuilder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.askme.smart_map.activity.DatabaseBuilder.DBadapter;

public class AlarmController {
    private static final int IS_SMS = 999;
    private static final int IS_REMINDER = 111;
    Context context;
    DBadapter dBadapter;


    public AlarmController(Context context, String number, String Msg, Long Time, int flag,int isSmsOrNote) {


        Intent alertIntent = new Intent(context, AlertReceiver.class);

        Bundle bundle = new Bundle();

        if(isSmsOrNote==IS_SMS)
            bundle.putBoolean("taskToDoSENDSMS", true);
        else
            bundle.putBoolean("taskToDoSENDSMS", false);

        bundle.putString("taskToDoNumber", number);
        bundle.putString("taskToDoContent", Msg);
        bundle.putString("taskToDoFlag", flag + "");
        alertIntent.putExtras(bundle);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, Time, PendingIntent.getBroadcast(context, flag, alertIntent, PendingIntent.FLAG_ONE_SHOT));

    }
}
