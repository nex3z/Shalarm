package com.nex3z.shalarm.domain.mapper;

import com.nex3z.shalarm.data.entity.AlarmEntity;
import com.nex3z.shalarm.domain.Alarm;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AlarmDataMapper {

    @Inject
    public AlarmDataMapper() {}

    public AlarmEntity toAlarmEntity(Alarm alarm) {
        AlarmEntity alarmEntity = null;
        if (alarm != null) {
            alarmEntity = new AlarmEntity();
            alarmEntity.setId(alarm.getId());
            alarmEntity.setEnabled(alarm.isEnabled());
            alarmEntity.setStart(alarm.getStart());
            alarmEntity.setRepeated(alarm.isRepeated());
            alarmEntity.setRepeatDays(alarm.getRepeatDays());
            alarmEntity.setRingtone(alarm.getRingtone());
            alarmEntity.setVibrateEnabled(alarm.isVibrateEnabled());
            alarmEntity.setShakePower(alarm.getShakePower());
            alarmEntity.setAlarmLabel(alarm.getAlarmLabel());
        }

        return alarmEntity;
    }

}
