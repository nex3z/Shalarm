package com.nex3z.shalarm.presentation.presenter;

import android.util.Log;

import com.nex3z.shalarm.domain.interactor.DefaultSubscriber;
import com.nex3z.shalarm.domain.interactor.UseCase;
import com.nex3z.shalarm.domain.interactor.alarm.insert.AlarmArg;
import com.nex3z.shalarm.presentation.alert.ui.AlertView;
import com.nex3z.shalarm.presentation.mapper.AlarmModelDataMapper;
import com.nex3z.shalarm.presentation.model.AlarmModel;
import com.nex3z.shalarm.presentation.utility.AlarmScheduleUtility;
import com.nex3z.shalarm.presentation.utility.SensorUtility;

public class AlertPresenter implements Presenter {
    private static final String LOG_TAG = AlertPresenter.class.getSimpleName();

    private AlarmModel mAlarmModel;
    private UseCase mUpdateAlarm;
    private AlarmModelDataMapper mMapper;
    private AlertView mView;
    private float mCurrentForce;
    private float mTargetForce;

    public AlertPresenter(AlarmModel alarmModel, UseCase updateAlarm, AlarmModelDataMapper mapper) {
        mAlarmModel = alarmModel;
        mUpdateAlarm = updateAlarm;
        mMapper = mapper;
    }

    public void setView(AlertView alertView) {
        mView = alertView;
    }

    public void initialize() {
        mCurrentForce = 0;
        float maxForce = SensorUtility.getMaxForce(mView.getContext());
        mTargetForce = mAlarmModel.getShakePower() * maxForce / 100;
        Log.v(LOG_TAG, "initialize(): mMaxForce = " + maxForce + ", mTargetForce = " + mTargetForce);

        mView.setRingtone(mAlarmModel.getRingtone());

        mView.startRingtone();
        if (mAlarmModel.isVibrateEnabled()) {
            mView.startVibrate();
        }

        if (!mAlarmModel.isRepeated()) {
            disableAlarm();
        }
    }

    public void onAlertCanceled() {
        mView.stopRingtone();
        if (mAlarmModel.isVibrateEnabled()) {
            mView.stopVibrate();
        }

        mView.finishView();
    }

    public void onShakeForceChanged(float force) {
        if (force > mCurrentForce) {
            mCurrentForce = force;
            Log.v(LOG_TAG, "onShakeForceChanged(): mCurrentForce = " + mCurrentForce);
            mView.renderForce(mCurrentForce, mTargetForce);

            if (force >= mTargetForce) {
                onAlertCanceled();
            }
        }
    }

    public void onCallStateRinging() {
        Log.v(LOG_TAG, "onCallStateRinging()");
        try {
            mView.pauseRingtone();
        } catch (IllegalStateException e) {
            Log.e(LOG_TAG, "onCallStateRinging(): e = " + e.getMessage());
        }
    }

    public void onCallStateIdle() {
        Log.v(LOG_TAG, "onCallStateIdle()");
        try {
            mView.startRingtone();
        } catch (IllegalStateException e) {
            Log.e(LOG_TAG, "onCallStateIdle(): e = " + e.getMessage());
        }
    }

    @Override
    public void resume() {}

    @Override
    public void pause() {}

    @Override
    public void destroy() {
        mView = null;
        mUpdateAlarm.unsubscribe();
    }

    @SuppressWarnings("unchecked")
    private void disableAlarm() {
        mAlarmModel.setEnabled(false);
        AlarmArg arg = new AlarmArg(mMapper.toAlarm(mAlarmModel));
        mUpdateAlarm.init(arg).execute(new UpdateAlarmSubscriber());
    }

    private final class UpdateAlarmSubscriber extends DefaultSubscriber<Integer> {
        @Override
        public void onError(Throwable e) {
            Log.e(LOG_TAG, "onError(): " + e.getMessage());
            mView.showMessage(e.getMessage());
        }

        @Override
        public void onNext(Integer integer) {
            Log.v(LOG_TAG, "onNext(): updated = " + integer);
            AlarmScheduleUtility.triggerAlarmService(mView.getContext());
        }
    }
}
