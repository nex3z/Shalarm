package com.nex3z.shalarm.presentation.alert;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.nex3z.shalarm.data.entity.mapper.AlarmEntityDataMapper;
import com.nex3z.shalarm.data.repository.AlarmDataRepository;
import com.nex3z.shalarm.data.repository.datasource.alarm.AlarmDataStoreFactory;
import com.nex3z.shalarm.domain.Alarm;
import com.nex3z.shalarm.domain.interactor.DefaultSubscriber;
import com.nex3z.shalarm.domain.interactor.UseCase;
import com.nex3z.shalarm.domain.interactor.alarm.query.GetAlarmList;
import com.nex3z.shalarm.domain.interactor.alarm.query.GetAlarmListArg;
import com.nex3z.shalarm.domain.mapper.AlarmDataMapper;
import com.nex3z.shalarm.domain.repository.AlarmRepository;
import com.nex3z.shalarm.presentation.mapper.AlarmModelDataMapper;
import com.nex3z.shalarm.presentation.model.AlarmModel;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import rx.android.schedulers.AndroidSchedulers;

public class AlarmService extends IntentService {
    private static final String LOG_TAG = AlarmService.class.getSimpleName();

    private static final String ACTION_SCHEDULE_NEXT_ALARM = "com.nex3z.shalarm.presentation.alert.action.SCHEDULE_NEXT_ALARM";

    private UseCase mGetAlarmList;
    private AlarmModelDataMapper mMapper = new AlarmModelDataMapper();

    public AlarmService() {
        super("AlarmService");
    }

    public static void startActionScheduleNextAlarm(Context context) {
        Intent intent = new Intent(context, AlarmService.class);
        intent.setAction(ACTION_SCHEDULE_NEXT_ALARM);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action.equals(ACTION_SCHEDULE_NEXT_ALARM)) {
                handleScheduleNextAlarm();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGetAlarmList != null) {
            mGetAlarmList.unsubscribe();
        }
    }

    private void handleScheduleNextAlarm() {
        Log.v(LOG_TAG, "handleScheduleAlarm()");
        AlarmRepository repository = new AlarmDataRepository(
                new AlarmDataStoreFactory(), new AlarmEntityDataMapper(), new AlarmDataMapper());
        mGetAlarmList = new GetAlarmList(
                new GetAlarmListArg(),
                repository,
                Runnable::run,
                () -> AndroidSchedulers.from(new Handler().getLooper()));

        mGetAlarmList.execute(new DefaultSubscriber<List<Alarm>>() {
            @Override
            public void onNext(List<Alarm> alarms) {
                scheduleNextAlarm(mMapper.transform(alarms));
                this.unsubscribe();
            }
        });
    }

    private void scheduleNextAlarm(List<AlarmModel> alarms) {
        AlarmModel nextAlarm = findNextAlarm(alarms);
        Log.v(LOG_TAG, "scheduleNextAlarm(): nextAlarm = " + nextAlarm);
        AlertManager.getInstance().setNextAlarm(nextAlarm);
    }

    private AlarmModel findNextAlarm(List<AlarmModel> alarms) {
        Set<AlarmModel> alarmQueue = new TreeSet<AlarmModel>(new AlarmComparator());

        for (AlarmModel alarmModel : alarms) {
            if (alarmModel.isEnabled()) {
                alarmQueue.add(alarmModel);
            }
        }

        if(alarmQueue.iterator().hasNext()){
            return alarmQueue.iterator().next();
        }else{
            return null;
        }
    }

}
