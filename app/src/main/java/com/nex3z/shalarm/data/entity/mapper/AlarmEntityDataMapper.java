package com.nex3z.shalarm.data.entity.mapper;

import android.content.ContentValues;

import com.nex3z.shalarm.data.entity.AlarmEntity;
import com.nex3z.shalarm.data.provider.AlarmContract;
import com.nex3z.shalarm.domain.Alarm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlarmEntityDataMapper {
    private static final String LOG_TAG = AlarmEntityDataMapper.class.getSimpleName();

    public Alarm transform(AlarmEntity alarmEntity) {
        Alarm alarm = null;
        if (alarmEntity != null) {
            alarm = new Alarm();
            alarm.setId(alarmEntity.getId());
            alarm.setEnabled(alarmEntity.isEnabled());
            alarm.setStart(alarmEntity.getStart());
            alarm.setRepeated(alarmEntity.isRepeated());
            alarm.setRepeatDays(alarmEntity.getRepeatDays());
            alarm.setRingtone(alarmEntity.getRingtone());
            alarm.setVibrateEnabled(alarmEntity.isVibrateEnabled());
            alarm.setShakePower(alarmEntity.getShakePower());
            alarm.setAlarmLabel(alarmEntity.getAlarmLabel());
        }

        // Log.v(LOG_TAG, "transform(): alarm = " + alarmEntity);

        return alarm;
    }

    public List<Alarm> transform(List<AlarmEntity> alarmEntityList) {
        List<Alarm> alarmList;

        if (alarmEntityList != null && !alarmEntityList.isEmpty()) {
            alarmList = new ArrayList<>();
            for (AlarmEntity alarmEntity : alarmEntityList) {
                alarmList.add(transform(alarmEntity));
            }
        } else {
            alarmList = Collections.emptyList();
        }

        return alarmList;
    }

    public ContentValues toContentValues(AlarmEntity alarmEntity) {
        // Log.v(LOG_TAG, "toContentValues(): alarmEntity = " + alarmEntity + ", alarmEntity.getRepeatDays().toString() = " + alarmEntity.getRepeatDays().toString());

        ContentValues alarmValues = new ContentValues();

        alarmValues.put(AlarmContract.AlarmEntry.COLUMN_ENABLE, alarmEntity.isEnabled());
        alarmValues.put(AlarmContract.AlarmEntry.COLUMN_START, alarmEntity.getStart().getTime());
        alarmValues.put(AlarmContract.AlarmEntry.COLUMN_REPEAT, alarmEntity.isRepeated());

        String repeatDays = alarmEntity.getRepeatDays().toString()
                .replaceAll("\\[", "").replaceAll("\\]","");
        alarmValues.put(AlarmContract.AlarmEntry.COLUMN_REPEAT_DAY, repeatDays);

        alarmValues.put(AlarmContract.AlarmEntry.COLUMN_RINGTONE, alarmEntity.getRingtone());
        alarmValues.put(AlarmContract.AlarmEntry.COLUMN_VIBRATE, alarmEntity.isVibrateEnabled());
        alarmValues.put(AlarmContract.AlarmEntry.COLUMN_SHAKE_POWER, alarmEntity.getShakePower());
        alarmValues.put(AlarmContract.AlarmEntry.COLUMN_LABEL, alarmEntity.getAlarmLabel());

        return alarmValues;
    }

}
