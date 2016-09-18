package com.nex3z.shalarm.presentation.utility;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.nex3z.shalarm.R;
import com.nex3z.shalarm.app.App;
import com.nex3z.shalarm.presentation.alert.AlarmServiceBroadcastReceiver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class AlarmUtility {
    private static final String LOG_TAG = AlarmUtility.class.getSimpleName();

    private static final String INITIAL_ALERT_TIMEOUT = "5";
    private static final Set<Integer> WORK_DAYS = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));

    public static void triggerAlarmService(Context context) {
        Log.v(LOG_TAG, "triggerAlarmService()");
        Intent intent = new Intent(context, AlarmServiceBroadcastReceiver.class);
        context.sendBroadcast(intent, null);
    }

    public static int getBackgroundColor(int shakePower) {
        Context context = App.getAppContext();
        if (shakePower < 33) {
            return ContextCompat.getColor(context, R.color.color_shake_power_light);
        } else if (shakePower < 66) {
            return ContextCompat.getColor(context, R.color.color_shake_power_medium);
        } else {
            return ContextCompat.getColor(context, R.color.color_shake_power_heavy);
        }
    }

    public static long getPrefAlertTimeout() {
        Context context = App.getAppContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String timeout = prefs.getString(context.getString(R.string.pref_key_alert_timeout),
                INITIAL_ALERT_TIMEOUT);
        return Long.valueOf(timeout) * 60 * 1000;

    }

    public static String getDaysDescription(Set<Integer> days) {
        Context context = App.getAppContext();
        TreeSet<Integer> sortedDays = new TreeSet<>(days);

        StringBuilder sb = new StringBuilder();
        if (sortedDays.size() == 0) {
            sb.append(context.getString(R.string.once));
        } else if (sortedDays.size() == 7) {
            sb.append(context.getString(R.string.everyday));
        } else if (sortedDays.equals(WORK_DAYS)) {
            sb.append(context.getString(R.string.workday));
        } else {
            String[] weekdays = context.getResources().getStringArray(R.array.short_weekdays);
            for(int day : sortedDays) {
                sb.append(weekdays[day]);
                sb.append(" ");
            }
        }

        return sb.toString();
    }

}
