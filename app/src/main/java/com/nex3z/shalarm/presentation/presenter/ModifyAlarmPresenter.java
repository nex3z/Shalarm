package com.nex3z.shalarm.presentation.presenter;

import android.util.Log;

import com.nex3z.shalarm.domain.interactor.DefaultSubscriber;
import com.nex3z.shalarm.domain.interactor.UseCase;
import com.nex3z.shalarm.domain.interactor.alarm.delete.DeleteAlarmByIdArg;
import com.nex3z.shalarm.domain.interactor.alarm.insert.AlarmArg;
import com.nex3z.shalarm.presentation.mapper.AlarmModelDataMapper;
import com.nex3z.shalarm.presentation.model.AlarmModel;
import com.nex3z.shalarm.presentation.ui.AddAlarmView;
import com.nex3z.shalarm.presentation.utility.AlarmScheduleUtility;

import java.util.Date;
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
        }
        mView.renderAlarm(mAlarmModel);
    }

    public void onSetStartTime() {
        mView.showTimePicker(mAlarmModel.getStart());
    }

    public void onStartTimeSet(Date date) {
        Log.v(LOG_TAG, "onStartTimeSet(): date = " + date);
        mAlarmModel.setStart(date);
        mView.renderStartTime(date);
    }

    @SuppressWarnings("unchecked")
    public void onSave() {
        mAlarmModel.setEnabled(true);

        Set<Integer> repeatDays = mView.getRepeatDays();
        mAlarmModel.setRepeatDays(repeatDays);
        mAlarmModel.setRepeated(!repeatDays.isEmpty());

        mAlarmModel.setVibrateEnabled(mView.isVibrateEnabled());
        mAlarmModel.setShakePower(mView.getShakePower());
        mAlarmModel.setRingtone(mView.getRingtone());
        mAlarmModel.setAlarmLabel(mView.getLabel());
        Log.v(LOG_TAG, "onSave(): " + mAlarmModel);

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

    private final class DeleteAlarmSubscriber extends DefaultSubscriber<Integer> {
        @Override
        public void onError(Throwable e) {
            Log.e(LOG_TAG, "onError(): " + e.getMessage());
            mView.showMessage(e.getMessage());
        }

        @Override
        public void onNext(Integer integer) {
            Log.v(LOG_TAG, "onNext(): deleted = " + integer);
            AlarmScheduleUtility.triggerAlarmService(mView.getContext());
        }
    }

}
