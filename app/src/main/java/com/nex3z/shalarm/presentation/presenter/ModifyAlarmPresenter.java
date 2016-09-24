package com.nex3z.shalarm.presentation.presenter;

import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.nex3z.shalarm.domain.interactor.DefaultSubscriber;
import com.nex3z.shalarm.domain.interactor.UseCase;
import com.nex3z.shalarm.domain.interactor.alarm.delete.DeleteAlarmByIdArg;
import com.nex3z.shalarm.domain.interactor.alarm.insert.AlarmArg;
import com.nex3z.shalarm.presentation.mapper.AlarmModelDataMapper;
import com.nex3z.shalarm.presentation.model.AlarmModel;
import com.nex3z.shalarm.presentation.ui.AddAlarmView;
import com.nex3z.shalarm.presentation.utility.AlarmUtility;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

public abstract class ModifyAlarmPresenter implements Presenter {
    private static final String LOG_TAG = ModifyAlarmPresenter.class.getSimpleName();

    AddAlarmView mView;
    UseCase mModifyAlarm;
    UseCase mDeleteAlarm;
    AlarmModelDataMapper mMapper;
    AlarmModel mAlarmModel;

    public ModifyAlarmPresenter(AlarmModel alarmModel, UseCase modifyAlarm, UseCase deleteAlarm,
                                AlarmModelDataMapper mapper) {
        mAlarmModel = alarmModel;
        mModifyAlarm = modifyAlarm;
        mDeleteAlarm = deleteAlarm;
        mMapper = mapper;
    }

    public ModifyAlarmPresenter(AlarmModel alarmModel, UseCase modifyAlarm,
                                AlarmModelDataMapper mapper) {
        this(alarmModel, modifyAlarm, null, mapper);
    }

    public void setAlarmModel(AlarmModel alarmModel) {
        mAlarmModel = alarmModel;
    }

    @Override
    public void resume() {}

    @Override
    public void pause() {}

    @Override
    public void destroy() {
        mModifyAlarm.unsubscribe();
        if (mDeleteAlarm != null) {
            mDeleteAlarm.unsubscribe();
        }
        mView = null;
    }

    public void setView(AddAlarmView view) {
        mView = view;
    }

    public void initialize() {
        if (mAlarmModel == null) {
            mAlarmModel = new AlarmModel();
            Uri ringtone = RingtoneManager.getActualDefaultRingtoneUri(mView.getContext(),
                    RingtoneManager.TYPE_ALARM);
            ringtone = ringtone == null ? Uri.EMPTY : ringtone;
            mAlarmModel.setRingtone(ringtone);
        }
        mView.renderAlarm(mAlarmModel);
    }

    public void onSetStartTime() {
        mView.showTimePicker(mAlarmModel.getStart());
    }

    public void onStartTimeSet(int hourOfDay, int minute, int second) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(mAlarmModel.getStart());

        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Date start = calendar.getTime();
        mAlarmModel.setStart(start);
        mView.renderStartTime(start);
    }

    public AlarmModel getCurrentAlarmModel() {
        updateCurrentAlarm();
        return new AlarmModel(mAlarmModel);
    }

    @SuppressWarnings("unchecked")
    public void onSave() {
        updateCurrentAlarm();
        AlarmArg arg = new AlarmArg(mMapper.toAlarm(mAlarmModel));
        mModifyAlarm.init(arg).execute(getSubscriber());

        mView.finishView();
    }

    @SuppressWarnings("unchecked")
    public void onDelete() {
        if (mDeleteAlarm != null && mAlarmModel != null) {
            mDeleteAlarm.init(new DeleteAlarmByIdArg(mAlarmModel.getId()))
                    .execute(new DeleteAlarmSubscriber());
        }
        mView.finishView();
    }

    protected abstract DefaultSubscriber getSubscriber();

    protected void updateCurrentAlarm() {
        mAlarmModel.setEnabled(true);

        Set<Integer> repeatDays = mView.getRepeatDays();
        mAlarmModel.setRepeatDays(repeatDays);
        mAlarmModel.setRepeated(!repeatDays.isEmpty());

        mAlarmModel.setVibrateEnabled(mView.isVibrateEnabled());
        mAlarmModel.setShakePower(mView.getShakePower());
        mAlarmModel.setRingtone(mView.getRingtone());
        mAlarmModel.setAlarmLabel(mView.getLabel());
        Log.v(LOG_TAG, "updateCurrentAlarm(): " + mAlarmModel);
    }

    private final class DeleteAlarmSubscriber extends DefaultSubscriber<Integer> {
        @Override
        public void onError(Throwable e) {
            Log.e(LOG_TAG, "onError(): " + e.getMessage());
            mView.showMessage(e.getMessage());
        }

        @Override
        public void onNext(Integer integer) {
            Log.v(LOG_TAG, "onNext(): deleted = " + integer);
            AlarmUtility.scheduleNextAlarm(mView.getContext());
        }
    }

}
