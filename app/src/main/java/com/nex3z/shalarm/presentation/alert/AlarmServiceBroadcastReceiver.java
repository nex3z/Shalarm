package com.nex3z.shalarm.presentation.alert;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmServiceBroadcastReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = AlarmServiceBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(LOG_TAG, "onReceive()");
        AlarmService.startActionScheduleNextAlarm(context);
    }
}
