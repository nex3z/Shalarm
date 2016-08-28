package com.nex3z.shalarm.presentation.ui;

import android.content.Context;

import com.nex3z.shalarm.presentation.model.AlarmModel;

import java.util.Collection;

public interface AlarmListView {

    void renderAlarmList(Collection<AlarmModel> alarmModelCollection);

    void showMessage(String message);

    Context getContext();

}
