package com.nex3z.shalarm.presentation.presenter;

import android.util.Log;

import com.nex3z.shalarm.domain.interactor.DefaultSubscriber;
import com.nex3z.shalarm.domain.interactor.UseCase;
import com.nex3z.shalarm.presentation.mapper.AlarmModelDataMapper;
import com.nex3z.shalarm.presentation.model.AlarmModel;
import com.nex3z.shalarm.presentation.utility.AlarmUtility;

public class UpdateAlarmPresenter extends ModifyAlarmPresenter {
    private static final String LOG_TAG = UpdateAlarmPresenter.class.getSimpleName();

    public UpdateAlarmPresenter(AlarmModel alarmModel, UseCase updateAlarm, UseCase deleteAlarm,
                             AlarmModelDataMapper mapper) {
        super(alarmModel, updateAlarm, deleteAlarm, mapper);
    }

    @Override
    protected DefaultSubscriber getSubscriber() {
        return new UpdateAlarmSubscriber();
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
            AlarmUtility.triggerAlarmService(mView.getContext());
        }
    }
}
