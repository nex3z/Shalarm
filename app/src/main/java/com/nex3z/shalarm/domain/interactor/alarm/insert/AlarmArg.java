package com.nex3z.shalarm.domain.interactor.alarm.insert;

import com.nex3z.shalarm.domain.Alarm;

public class AlarmArg {

    private final Alarm mAlarm;

    public AlarmArg(Alarm alarm) {
        mAlarm = alarm;
    }

    public Alarm getAlarm() {
        return mAlarm;
    }
}
