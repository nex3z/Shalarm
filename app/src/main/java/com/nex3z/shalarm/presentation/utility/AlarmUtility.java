package com.nex3z.shalarm.presentation.utility;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.nex3z.shalarm.R;
import com.nex3z.shalarm.app.App;
import com.nex3z.shalarm.presentation.alert.AlarmServiceBroadcastReceiver;

public class AlarmUtility {
    private static final String LOG_TAG = AlarmUtility.class.getSimpleName();

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

}
