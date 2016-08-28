package com.nex3z.shalarm.presentation.mapper;

import android.net.Uri;

import com.nex3z.shalarm.data.entity.AlarmEntity;
import com.nex3z.shalarm.domain.Alarm;
import com.nex3z.shalarm.presentation.model.AlarmModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AlarmModelDataMapper {

    public AlarmModel transform(Alarm alarm) {
        AlarmModel alarmModel = null;
        if (alarm != null) {
            alarmModel = new AlarmModel();
            alarmModel.setId(alarm.getId());
            alarmModel.setEnabled(alarm.isEnabled());
            alarmModel.setStart(alarm.getStart());
            alarmModel.setRepeated(alarm.isRepeated());
            alarmModel.setRepeatDays(alarm.getRepeatDays());
            alarmModel.setRingtone(Uri.parse(alarm.getRingtone()));
            alarmModel.setVibrateEnabled(alarm.isVibrateEnabled());
            alarmModel.setShakePower(alarm.getShakePower());
            alarmModel.setAlarmLabel(alarm.getAlarmLabel());
        }
        return alarmModel;
    }

    public List<AlarmModel> transform(List<Alarm> alarmList) {
        List<AlarmModel> alarmModelList;

        if (alarmList != null && !alarmList.isEmpty()) {
            alarmModelList = new ArrayList<>();
            for (Alarm alarm : alarmList) {
                alarmModelList.add(transform(alarm));
            }
        } else {
            alarmModelList = Collections.emptyList();
        }

        return alarmModelList;
    }

    public Alarm toAlarm(AlarmModel alarmModel) {
        Alarm alarm = null;
        if (alarmModel != null) {
            alarm = new Alarm();
            alarm.setId(alarmModel.getId());
            alarm.setEnabled(alarmModel.isEnabled());
            alarm.setStart(alarmModel.getStart());
            alarm.setRepeated(alarmModel.isRepeated());
            alarm.setRepeatDays(alarmModel.getRepeatDays());
            alarm.setRingtone(alarmModel.getRingtone().toString());
            alarm.setVibrateEnabled(alarmModel.isVibrateEnabled());
            alarm.setShakePower(alarmModel.getShakePower());
            alarm.setAlarmLabel(alarmModel.getAlarmLabel());
        }
        return alarm;
    }
}
