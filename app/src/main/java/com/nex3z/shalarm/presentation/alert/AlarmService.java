package com.nex3z.shalarm.presentation.alert;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.nex3z.shalarm.data.entity.mapper.AlarmEntityDataMapper;
import com.nex3z.shalarm.data.executor.JobExecutor;
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

public class AlarmService extends Service {
    private static final String LOG_TAG = AlarmService.class.getSimpleName();

    public static final String EXTRA_NEXT_ALARM = "com.nex3z.shalarm.presentation.alert.extra.EXTRA_NEXT_ALARM";
    public static final String EXTRA_NEXT_ALARM_BUNDLE = "com.nex3z.shalarm.presentation.alert.extra.EXTRA_NEXT_ALARM_BUNDLE";

    public static final String ACTION_SCHEDULE_NEXT_ALARM = "com.nex3z.shalarm.presentation.alert.action.SCHEDULE_NEXT_ALARM";
    public static final String ACTION_RETRIEVE_NEXT_ALARM = "com.nex3z.shalarm.presentation.alert.action.RETRIEVE_NEXT_ALARM";
    public static final String ACTION_NEXT_ALARM_UPDATE = "com.nex3z.shalarm.presentation.alert.action.ACTION_NEXT_ALARM_UPDATE";
    public static final String ACTION_SET_ALARM = "com.nex3z.shalarm.presentation.alert.action.ACTION_SET_ALARM";

    private AlarmModelDataMapper mMapper = new AlarmModelDataMapper();
    private UseCase mGetAlarmList;
    private AlarmModel mNext;

    public static void startActionScheduleNextAlarm(Context context) {
        Intent intent = new Intent(context, AlarmService.class);
        intent.setAction(ACTION_SCHEDULE_NEXT_ALARM);
        context.startService(intent);
    }

    public static void startActionRetrieveNextAlarm(Context context) {
        Intent intent = new Intent(context, AlarmService.class);
        intent.setAction(ACTION_RETRIEVE_NEXT_ALARM);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String action = intent.getAction();
        Log.v(LOG_TAG, "onStartCommand(): action = " + action);

        if (action.equals(ACTION_SCHEDULE_NEXT_ALARM)) {
            handleActionScheduleNextAlarm();
        } else if (action.equals(ACTION_RETRIEVE_NEXT_ALARM)) {
            handleActionRetrieveNextAlarm();
        }

        return START_STICKY;
    }

    private void handleActionScheduleNextAlarm() {
        AlarmRepository repository = new AlarmDataRepository(
                new AlarmDataStoreFactory(), new AlarmEntityDataMapper(), new AlarmDataMapper());
        mGetAlarmList = new GetAlarmList(new GetAlarmListArg(), repository,
                new JobExecutor(), () -> {
            Handler handler = new Handler();
            return AndroidSchedulers.from(handler.getLooper());
        });

        mGetAlarmList.execute(new GetAlarmListSubscriber());
    }

    private void handleActionRetrieveNextAlarm() {
        Log.v(LOG_TAG, "handleActionRetrieveNextAlarm(): mNext = " + mNext);
        notifyNextAlarm(mNext);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGetAlarmList.unsubscribe();
    }

    private void scheduleNextAlarm(List<AlarmModel> alarms) {
        AlarmModel nextAlarm = getNextAlarm(alarms);
        Log.v(LOG_TAG, "scheduleNextAlarm(): nextAlarm = " + nextAlarm);

        Context context = getApplicationContext();

        if (nextAlarm != null) {
            mNext = nextAlarm;
            Intent intent = new Intent(ACTION_SET_ALARM);
            Bundle alarmBundle = new Bundle();
            alarmBundle.putParcelable(EXTRA_NEXT_ALARM, nextAlarm);
            intent.putExtra(EXTRA_NEXT_ALARM_BUNDLE, alarmBundle);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            manager.set(AlarmManager.RTC_WAKEUP, nextAlarm.getNextAlertTime().getTime(),
                    pendingIntent);

            Log.v(LOG_TAG, "scheduleNextAlarm(): set next alarm at "
                    + nextAlarm.getNextAlertTime());
        } else {
            Intent intent = new Intent(ACTION_SET_ALARM);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                    intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager)getApplicationContext()
                    .getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }

        notifyNextAlarm(nextAlarm);
    }

    private void notifyNextAlarm(AlarmModel alarmModel) {
        Log.v(LOG_TAG, "notifyNextAlarm(): alarmModel = " + alarmModel);
        Intent intent = new Intent(ACTION_NEXT_ALARM_UPDATE);
        if (alarmModel != null) {
            intent.putExtra(EXTRA_NEXT_ALARM, alarmModel);
        }
        sendBroadcast(intent);
    }

    private AlarmModel getNextAlarm(List<AlarmModel> alarms) {
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

    private final class GetAlarmListSubscriber extends DefaultSubscriber<List<Alarm>> {
        @Override
        public void onError(Throwable e) {
            Log.e(LOG_TAG, "onError(): " + e.getMessage());
        }

        @Override
        public void onNext(List<Alarm> alarms) {
            scheduleNextAlarm(mMapper.transform(alarms));
            this.unsubscribe();
        }
    }

}
