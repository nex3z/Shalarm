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

public class AlarmUtility {
    private static final String LOG_TAG = AlarmUtility.class.getSimpleName();

    private static final String INITIAL_ALERT_TIMEOUT = "5";

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

}
