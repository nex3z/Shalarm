package com.nex3z.shalarm.presentation.ui;

import android.content.Context;
import android.net.Uri;

import com.nex3z.shalarm.presentation.model.AlarmModel;

import java.util.Date;
import java.util.Set;

public interface AddAlarmView {

    void showMessage(String message);

    Context getContext();

    void renderAlarm(AlarmModel alarmModel);

    void showTimePicker(Date date);

    void renderStartTime(Date date);

    Set<Integer> getRepeatDays();

    Uri getRingtone();

    boolean isVibrateEnabled();

    int getShakePower();

    String getLabel();

    void finishView();
}
