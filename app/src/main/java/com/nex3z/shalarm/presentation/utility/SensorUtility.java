package com.nex3z.shalarm.presentation.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.nex3z.shalarm.R;

public class SensorUtility {
    private static final String LOG_TAG = SensorUtility.class.getSimpleName();

    private static final float ONE_G = 1.0f;
    private static final float DEFAULT_MAX_FORCE = 1.5f;

    public static float calculateForce(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        return (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ) - ONE_G;
    }

    public static void setMaxForce(Context context, float maxForce) {
        String key = context.getString(R.string.pref_key_max_force);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, maxForce);
        editor.commit();

        Log.v(LOG_TAG, "setMaxForce(): maxForce = " + maxForce);
    }

    public static float getMaxForce(Context context) {
        String key = context.getString(R.string.pref_key_max_force);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Log.v(LOG_TAG, "getMaxForce(): maxForce = " + prefs.getFloat(key, DEFAULT_MAX_FORCE));
        return prefs.getFloat(key, DEFAULT_MAX_FORCE);
    }
}
