package com.nex3z.shalarm.presentation.presenter;

import android.util.Log;

import com.nex3z.shalarm.domain.Alarm;
import com.nex3z.shalarm.domain.interactor.DefaultSubscriber;
import com.nex3z.shalarm.domain.interactor.UseCase;
import com.nex3z.shalarm.domain.interactor.alarm.insert.AlarmArg;
import com.nex3z.shalarm.domain.interactor.alarm.query.GetAlarmListArg;
import com.nex3z.shalarm.presentation.internal.di.PerActivity;
import com.nex3z.shalarm.presentation.mapper.AlarmModelDataMapper;
import com.nex3z.shalarm.presentation.model.AlarmModel;
import com.nex3z.shalarm.presentation.ui.AlarmListView;
import com.nex3z.shalarm.presentation.utility.AlarmUtility;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@PerActivity
public class AlarmListPresenter implements Presenter {
    private static final String LOG_TAG = AlarmListPresenter.class.getSimpleName();

    private List<AlarmModel> mAlarmModelList;

    private AlarmListView mView;
    private UseCase mGetAlarmList;
    private UseCase mUpdateAlarm;
    private AlarmModelDataMapper mMapper;

    @Inject
    public AlarmListPresenter(@Named("getAlarmList") UseCase getAlarmList,
                              @Named("updateAlarm") UseCase updateAlarm,
                              AlarmModelDataMapper mapper) {
        mGetAlarmList = getAlarmList;
        mUpdateAlarm = updateAlarm;
        mMapper = mapper;
    }

    @Override
    public void resume() {}

    @Override
    public void pause() {}

    @Override
    public void destroy() {
        mGetAlarmList.unsubscribe();
        mUpdateAlarm.unsubscribe();
        mView = null;
    }

    public void setView(AlarmListView view) {
        mView = view;
    }

    public void initialize() {
        AlarmUtility.scheduleNextAlarm(mView.getContext());
    }

    public void loadAllAlarms() {
        GetAlarmListArg arg = new GetAlarmListArg(GetAlarmListArg.SORT_BY_START_ASC);
        loadAlarmList(arg);
    }

    public void loadEnableAlarms() {
        GetAlarmListArg arg = new GetAlarmListArg(GetAlarmListArg.SORT_BY_START_ASC,
                GetAlarmListArg.FILTER_ENABLED_ALARMS);
        loadAlarmList(arg);
    }

    public void loadDisabledAlarms() {
        GetAlarmListArg arg = new GetAlarmListArg(GetAlarmListArg.SORT_BY_START_ASC,
                GetAlarmListArg.FILTER_DISABLED_ALARMS);
        loadAlarmList(arg);
    }

    @SuppressWarnings("unchecked")
    private void loadAlarmList(GetAlarmListArg arg) {
        if (arg != null) {
            mGetAlarmList.init(arg);
        }
        mGetAlarmList.execute(new GetAlarmListSubscriber());
    }

    @SuppressWarnings("unchecked")
    public void setAlarmEnabled(int position, boolean isEnabled) {
        if (mAlarmModelList != null) {
            Log.v(LOG_TAG, "setAlarmEnabled(): position = " + position + ", isEnabled = " + isEnabled);
            AlarmModel alarmModel = mAlarmModelList.get(position);
            if (alarmModel.isEnabled() != isEnabled) {
                alarmModel.setEnabled(isEnabled);
                AlarmArg arg = new AlarmArg(mMapper.toAlarm(alarmModel));
                mUpdateAlarm.init(arg).execute(new UpdateAlarmSubscriber());
            }
        }
    }

    private void showAlarmListInView(List<Alarm> alarmList) {
        mAlarmModelList = mMapper.transform(alarmList);
        mView.renderAlarmList(mAlarmModelList);
    }

    private final class GetAlarmListSubscriber extends DefaultSubscriber<List<Alarm>> {
        @Override
        public void onError(Throwable e) {
            Log.e(LOG_TAG, "onError(): " + e.getMessage());
            mView.showMessage(e.getMessage());
        }

        @Override
        public void onNext(List<Alarm> alarms) {
            Log.v(LOG_TAG, "onNext(): alarms.size() = " + alarms.size());
            showAlarmListInView(alarms);
        }
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
            AlarmUtility.scheduleNextAlarm(mView.getContext());
        }
    }
}
