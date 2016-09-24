package com.nex3z.shalarm.data.repository;

import com.nex3z.shalarm.data.entity.mapper.AlarmEntityDataMapper;
import com.nex3z.shalarm.data.repository.datasource.alarm.AlarmDataStore;
import com.nex3z.shalarm.data.repository.datasource.alarm.AlarmDataStoreFactory;
import com.nex3z.shalarm.domain.Alarm;
import com.nex3z.shalarm.domain.mapper.AlarmDataMapper;
import com.nex3z.shalarm.domain.repository.AlarmRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class AlarmDataRepository implements AlarmRepository {

    private final AlarmDataStoreFactory mAlarmDataStoreFactory;
    private final AlarmEntityDataMapper mAlarmEntityDataMapper;
    private final AlarmDataMapper mAlarmDataMapper;

    @Inject
    public AlarmDataRepository(AlarmDataStoreFactory factory,
                               AlarmEntityDataMapper alarmEntityDataMapper,
                               AlarmDataMapper alarmDataMapper) {
        mAlarmDataStoreFactory = factory;
        mAlarmEntityDataMapper = alarmEntityDataMapper;
        mAlarmDataMapper = alarmDataMapper;
    }

    @Override
    public Observable<List<Alarm>> getAlarms(String sortBy) {
        final AlarmDataStore alarmDataStore =
                mAlarmDataStoreFactory.createContentProviderDataStore();
        return alarmDataStore
                .getAlarmEntityList(sortBy)
                .map(mAlarmEntityDataMapper::transform);
    }

    @Override
    public Observable<List<Alarm>> getAlarms(String sortBy, String filter) {
        final AlarmDataStore alarmDataStore =
                mAlarmDataStoreFactory.createContentProviderDataStore();
        return alarmDataStore
                .getAlarmEntityList(sortBy, filter)
                .map(mAlarmEntityDataMapper::transform);
    }

    @Override
    public Observable<Alarm> getAlarmById(long alarmId) {
        final AlarmDataStore alarmDataStore =
                mAlarmDataStoreFactory.createContentProviderDataStore();
        return alarmDataStore
                .getAlarmEntityById(alarmId)
                .map(mAlarmEntityDataMapper::transform);
    }

    @Override
    public Observable<Long> addAlarm(Alarm alarm) {
        final AlarmDataStore alarmDataStore =
                mAlarmDataStoreFactory.createContentProviderDataStore();
        return alarmDataStore
                .insertAlarmEntity(mAlarmDataMapper.toAlarmEntity(alarm));
    }

    @Override
    public Observable<Integer> deleteAlarm(long alarmId) {
        final AlarmDataStore alarmDataStore =
                mAlarmDataStoreFactory.createContentProviderDataStore();
        return alarmDataStore.deleteAlarmEntity(alarmId);
    }

    @Override
    public Observable<Integer> updateAlarm(Alarm alarm) {
        final AlarmDataStore alarmDataStore =
                mAlarmDataStoreFactory.createContentProviderDataStore();
        return alarmDataStore.updateAlarmEntity(mAlarmDataMapper.toAlarmEntity(alarm));
    }
}
