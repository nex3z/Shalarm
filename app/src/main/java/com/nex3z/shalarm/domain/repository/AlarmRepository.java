package com.nex3z.shalarm.domain.repository;


import com.nex3z.shalarm.domain.Alarm;

import java.util.List;

import rx.Observable;

public interface AlarmRepository {

    Observable<List<Alarm>> getAlarms(String sortBy);

    Observable<List<Alarm>> getAlarms(String sortBy, String filter);

    Observable<Alarm> getAlarmById(long alarmId);

    Observable<Long> addAlarm(Alarm alarm);

    Observable<Integer> deleteAlarm(long alarmId);

    Observable<Integer> updateAlarm(Alarm alarm);

}
