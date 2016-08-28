package com.nex3z.shalarm.presentation.alert;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

public class AlertWakeLock {
    private static final String LOG_TAG = AlertWakeLock.class.getSimpleName();

	private static PowerManager.WakeLock mWakeLock = null;

	public static void lock(Context context) {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		if (mWakeLock == null) {
            mWakeLock = pm.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "ALERT");
        }
		mWakeLock.acquire();
	}

	public static void unlock(Context context) {
		try {
			if (mWakeLock != null)
				mWakeLock.release();
		} catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
		}
	}
}