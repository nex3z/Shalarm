package com.nex3z.shalarm.data.entity.mapper;

import android.database.Cursor;
import android.util.Log;

import com.nex3z.shalarm.data.entity.AlarmEntity;
import com.nex3z.shalarm.data.provider.AlarmContract;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlarmCursorDataMapper {
    private static final String LOG_TAG = AlarmCursorDataMapper.class.getSimpleName();

    public static final String[] ALARM_COLUMNS = {
            AlarmContract.AlarmEntry._ID,
            AlarmContract.AlarmEntry.COLUMN_ENABLE,
            AlarmContract.AlarmEntry.COLUMN_START,
            AlarmContract.AlarmEntry.COLUMN_REPEAT,
            AlarmContract.AlarmEntry.COLUMN_REPEAT_DAY,
            AlarmContract.AlarmEntry.COLUMN_RINGTONE,
            AlarmContract.AlarmEntry.COLUMN_VIBRATE,
            AlarmContract.AlarmEntry.COLUMN_SHAKE_POWER,
            AlarmContract.AlarmEntry.COLUMN_LABEL
    };

    public static final int COL_ALARM_ID = 0;
    public static final int COL_ALARM_ENABLE = 1;
    public static final int COL_ALARM_START = 2;
    public static final int COL_ALARM_REPEAT = 3;
    public static final int COL_ALARM_REPEAT_DAY = 4;
    public static final int COL_ALARM_RINGTONE = 5;
    public static final int COL_ALARM_VIBRATE = 6;
    public static final int COL_ALARM_SHAKE_POWER = 7;
    public static final int COL_ALARM_LABEL = 8;

    public AlarmEntity transform(Cursor alarmCursor) {
        AlarmEntity alarmEntity = null;
        if (alarmCursor != null) {
            alarmEntity = new AlarmEntity();
            alarmEntity.setId(alarmCursor.getLong(COL_ALARM_ID));
            alarmEntity.setEnabled(alarmCursor.getInt(COL_ALARM_ENABLE) == 1);
            alarmEntity.setStart(new Date(alarmCursor.getLong(COL_ALARM_START)));
            alarmEntity.setRepeated(alarmCursor.getInt(COL_ALARM_REPEAT) == 1);

            String repeatDays = alarmCursor.getString(COL_ALARM_REPEAT_DAY);
            alarmEntity.setRepeatDays(convertStringToIntSet(repeatDays));

            alarmEntity.setRingtone(alarmCursor.getString(COL_ALARM_RINGTONE));
            alarmEntity.setVibrateEnabled(alarmCursor.getInt(COL_ALARM_VIBRATE) == 1);
            alarmEntity.setShakePower(alarmCursor.getInt(COL_ALARM_SHAKE_POWER));
            alarmEntity.setAlarmLabel(alarmCursor.getString(COL_ALARM_LABEL));
        }

        Log.v(LOG_TAG, "transform(): alarmEntity = " + alarmEntity);

        return alarmEntity;
    }


    public List<AlarmEntity> transformList(Cursor alarmCursor) {
        List<AlarmEntity> alarmList = new ArrayList<>();

        AlarmEntity alarmEntity = null;
        while(alarmCursor.moveToNext()) {
            alarmEntity = new AlarmEntity();
            alarmEntity.setId(alarmCursor.getLong(COL_ALARM_ID));
            alarmEntity.setEnabled(alarmCursor.getInt(COL_ALARM_ENABLE) == 1);
            alarmEntity.setStart(new Date(alarmCursor.getLong(COL_ALARM_START)));
            alarmEntity.setRepeated(alarmCursor.getInt(COL_ALARM_REPEAT) == 1);

            String repeatDays = alarmCursor.getString(COL_ALARM_REPEAT_DAY);
            alarmEntity.setRepeatDays(convertStringToIntSet(repeatDays));

            alarmEntity.setRingtone(alarmCursor.getString(COL_ALARM_RINGTONE));
            alarmEntity.setVibrateEnabled(alarmCursor.getInt(COL_ALARM_VIBRATE) == 1);
            alarmEntity.setShakePower(alarmCursor.getInt(COL_ALARM_SHAKE_POWER));
            alarmEntity.setAlarmLabel(alarmCursor.getString(COL_ALARM_LABEL));

            alarmList.add(alarmEntity);
        }

        return alarmList;
    }

    private Set<Integer> convertStringToIntSet(String str) {
//        Log.v(LOG_TAG, "convertStringToIntSet(): str = " + str);
        Set<Integer> set = new HashSet<>();

        if (str == null || str.equals("")) return set;

        String[] array = str.replaceAll("\\s+", "").split(",");
        for (String s : array) {
            set.add(Integer.parseInt(s));
        }

//        Log.v(LOG_TAG, "convertStringToIntSet(): set = " + set);
        return set;
    }

}
