package com.nex3z.shalarm.presentation.utility;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.nex3z.shalarm.presentation.alert.AlarmServiceBroadcastReceiver;

public class AlarmScheduleUtility {
    private static final String LOG_TAG = AlarmScheduleUtility.class.getSimpleName();

    public static void triggerAlarmService(Context context) {
        Log.v(LOG_TAG, "triggerAlarmService()");
        Intent intent = new Intent(context, AlarmServiceBroadcastReceiver.class);
        context.sendBroadcast(intent, null);
    }

}
