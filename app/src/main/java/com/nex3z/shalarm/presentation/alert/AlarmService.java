package com.nex3z.shalarm.presentation.alert;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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

    public static final String NEXT_ALARM_UPDATE = "com.nex3z.shalarm.presentation.alert.NEXT_ALARM_UPDATE";
    public static final String NEXT_ALARM = "next_alarm";

    private AlarmModelDataMapper mMapper;
    private UseCase mGetAlarmList;

    public AlarmService() {
        mMapper = new AlarmModelDataMapper();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getAlarms();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGetAlarmList.unsubscribe();
    }

    private void getAlarms() {
        AlarmRepository repository = new AlarmDataRepository(
                new AlarmDataStoreFactory(), new AlarmEntityDataMapper(), new AlarmDataMapper());
        mGetAlarmList = new GetAlarmList(new GetAlarmListArg(), repository,
                new JobExecutor(), () -> {
                    Handler handler = new Handler();
                    return AndroidSchedulers.from(handler.getLooper());
                });

        mGetAlarmList.execute(new GetAlarmListSubscriber());
    }

    private void scheduleNextAlarm(List<AlarmModel> alarms) {
        AlarmModel nextAlarm = getNextAlarm(alarms);
        Log.v(LOG_TAG, "scheduleNextAlarm(): nextAlarm = " + nextAlarm);

        Context context = getApplicationContext();

        if (nextAlarm != null) {
            Intent intent = new Intent(context, AlertBroadcastReceiver.class);
            intent.putExtra(AlertBroadcastReceiver.ALARM, nextAlarm);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            manager.set(AlarmManager.RTC_WAKEUP, nextAlarm.getNextAlertTime().getTime(),
                    pendingIntent);

            Log.v(LOG_TAG, "scheduleNextAlarm(): set next alarm at "
                    + nextAlarm.getNextAlertTime());
        } else {
            Intent intent = new Intent(getApplicationContext(), AlertBroadcastReceiver.class);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                    intent,PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager)getApplicationContext()
                    .getSystemService(Context.ALARM_SERVICE);

            alarmManager.cancel(pendingIntent);
        }

        notifyNextAlarm(nextAlarm);
    }

    private void notifyNextAlarm(AlarmModel alarmModel) {
        Log.v(LOG_TAG, "notifyNextAlarm(): alarmModel = " + alarmModel);
        Intent intent = new Intent(NEXT_ALARM_UPDATE);
        if (alarmModel != null) {
            intent.putExtra(NEXT_ALARM, alarmModel);
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

        Log.v(LOG_TAG, "getNext(): alarmQueue size =  " + alarmQueue.size() + ", items = "+ alarmQueue);

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
