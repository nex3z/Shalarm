package com.nex3z.shalarm.presentation.ui.activity;

import android.util.Log;

import com.nex3z.shalarm.data.entity.mapper.AlarmEntityDataMapper;
import com.nex3z.shalarm.data.executor.JobExecutor;
import com.nex3z.shalarm.data.repository.AlarmDataRepository;
import com.nex3z.shalarm.data.repository.datasource.alarm.AlarmDataStoreFactory;
import com.nex3z.shalarm.domain.interactor.UseCase;
import com.nex3z.shalarm.domain.interactor.alarm.insert.AddAlarm;
import com.nex3z.shalarm.domain.mapper.AlarmDataMapper;
import com.nex3z.shalarm.domain.repository.AlarmRepository;
import com.nex3z.shalarm.presentation.UIThread;
import com.nex3z.shalarm.presentation.mapper.AlarmModelDataMapper;
import com.nex3z.shalarm.presentation.model.AlarmModel;
import com.nex3z.shalarm.presentation.presenter.AddAlarmPresenter;
import com.nex3z.shalarm.presentation.presenter.ModifyAlarmPresenter;

public class AddAlarmActivity extends ModifyAlarmActivity {
    private static final String LOG_TAG = AddAlarmActivity.class.getSimpleName();

    @Override
    protected ModifyAlarmPresenter getPresenter(AlarmModel alarmModel) {
        AlarmRepository repository = new AlarmDataRepository(
                new AlarmDataStoreFactory(), new AlarmEntityDataMapper(), new AlarmDataMapper());
        UseCase useCase = new AddAlarm(repository, new JobExecutor(), new UIThread());
        return new AddAlarmPresenter(alarmModel, useCase, new AlarmModelDataMapper());
    }

    @Override
    protected boolean showDeleteButton() {
        Log.v(LOG_TAG, "showDeleteButton(): false");
        return false;
    }

}
