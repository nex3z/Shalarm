package com.nex3z.shalarm.presentation.presenter;

import android.util.Log;

import com.nex3z.shalarm.domain.interactor.DefaultSubscriber;
import com.nex3z.shalarm.domain.interactor.UseCase;
import com.nex3z.shalarm.presentation.mapper.AlarmModelDataMapper;
import com.nex3z.shalarm.presentation.model.AlarmModel;
import com.nex3z.shalarm.presentation.utility.AlarmUtility;

public class AddAlarmPresenter extends ModifyAlarmPresenter {
    private static final String LOG_TAG = AddAlarmPresenter.class.getSimpleName();

    public AddAlarmPresenter(AlarmModel alarmModel, UseCase addAlarm,
                             AlarmModelDataMapper mapper) {
        super(alarmModel, addAlarm, mapper);
    }

    @Override
    protected DefaultSubscriber getSubscriber() {
        return new AddAlarmSubscriber();
    }

    private final class AddAlarmSubscriber extends DefaultSubscriber<Long> {
        @Override
        public void onError(Throwable e) {
            Log.e(LOG_TAG, "onError(): " + e.getMessage());
            mView.showMessage(e.getMessage());
        }

        @Override
        public void onNext(Long aLong) {
            Log.v(LOG_TAG, "onNext(): Alarm saved, id = " + aLong);
            AlarmUtility.scheduleNextAlarm(mView.getContext());
        }
    }
}
