package com.nex3z.shalarm.data.repository.datasource.alarm;


import com.nex3z.shalarm.data.entity.AlarmEntity;

import java.util.List;

import rx.Observable;

public interface AlarmDataStore {

    Observable<List<AlarmEntity>> getAlarmEntityList(String sortBy);

    Observable<AlarmEntity> getAlarmEntityById(long alarmId);

    Observable<Long> insertAlarmEntity(AlarmEntity alarm);

    Observable<Integer> deleteAlarmEntity(long alarmId);

    Observable<Integer> updateAlarmEntity(AlarmEntity alarm);

}
