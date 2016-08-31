package com.nex3z.shalarm.presentation.alert.ui;

import android.content.Context;
import android.net.Uri;

import com.nex3z.shalarm.presentation.model.AlarmModel;

public interface AlertView {

    void renderAlarm(AlarmModel alarmModel);

    void setRingtone(Uri uri);

    void startRingtone();

    void pauseRingtone();

    void stopRingtone();

    void startVibrate();

    void stopVibrate();

    void renderForce(float current, float target);

    void showResult(long time);

    void showMessage(String message);

    Context getContext();

    void finishView();
}
