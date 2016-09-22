package com.nex3z.shalarm.presentation.alert;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.nex3z.shalarm.app.App;
import com.nex3z.shalarm.presentation.model.AlarmModel;


public class AlertManager {
    private static final String LOG_TAG = AlertManager.class.getSimpleName();

    public static final String EXTRA_NEXT_ALARM = "com.nex3z.shalarm.presentation.alert.extra.NEXT_ALARM";
    public static final String EXTRA_NEXT_ALARM_BUNDLE = "com.nex3z.shalarm.presentation.alert.extra.NEXT_ALARM_BUNDLE";

    public static final String ACTION_SET_ALARM = "com.nex3z.shalarm.presentation.alert.action.ACTION_SET_ALARM";
    public static final String ACTION_NEXT_ALARM_UPDATE = "com.nex3z.shalarm.presentation.alert.action.NEXT_ALARM_UPDATE";

    private AlarmModel mNextAlarm;

    private AlertManager() {}

    private static class Holder {
        private static final AlertManager INSTANCE = new AlertManager();
    }

    public static AlertManager getInstance() {
        return Holder.INSTANCE;
    }

    public synchronized void setNextAlarm(AlarmModel nextAlarm) {
        mNextAlarm = nextAlarm;

        Context context = App.getAppContext();
        if (mNextAlarm != null) {
            Bundle alarmBundle = new Bundle();
            alarmBundle.putParcelable(EXTRA_NEXT_ALARM, mNextAlarm);
            Intent intent = new Intent(ACTION_SET_ALARM);
            intent.putExtra(EXTRA_NEXT_ALARM_BUNDLE, alarmBundle);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            manager.set(AlarmManager.RTC_WAKEUP, mNextAlarm.getNextAlertTime().getTime(),
                    pendingIntent);

            Log.v(LOG_TAG, "scheduleNextAlarm(): next alarm at " + nextAlarm.getNextAlertTime());
        } else {
            Intent intent = new Intent(ACTION_SET_ALARM);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                    intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager =
                    (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            Log.v(LOG_TAG, "scheduleNextAlarm(): no alarm");
        }

        notifyNextAlarm(mNextAlarm);
    }

    public synchronized AlarmModel getNextAlarm() {
        return mNextAlarm;
    }

    private void notifyNextAlarm(AlarmModel alarmModel) {
        Log.v(LOG_TAG, "notifyNextAlarm(): alarmModel = " + alarmModel);
        Intent intent = new Intent(ACTION_NEXT_ALARM_UPDATE);
        if (alarmModel != null) {
            intent.putExtra(EXTRA_NEXT_ALARM, alarmModel);
        }
        App.getAppContext().sendBroadcast(intent);
    }

}
