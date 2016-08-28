package com.nex3z.shalarm.presentation.ui.activity;

import com.nex3z.shalarm.data.entity.mapper.AlarmEntityDataMapper;
import com.nex3z.shalarm.data.executor.JobExecutor;
import com.nex3z.shalarm.data.repository.AlarmDataRepository;
import com.nex3z.shalarm.data.repository.datasource.alarm.AlarmDataStoreFactory;
import com.nex3z.shalarm.domain.interactor.UseCase;
import com.nex3z.shalarm.domain.interactor.alarm.delete.DeleteAlarmById;
import com.nex3z.shalarm.domain.interactor.alarm.update.UpdateAlarm;
import com.nex3z.shalarm.domain.mapper.AlarmDataMapper;
import com.nex3z.shalarm.domain.repository.AlarmRepository;
import com.nex3z.shalarm.presentation.UIThread;
import com.nex3z.shalarm.presentation.mapper.AlarmModelDataMapper;
import com.nex3z.shalarm.presentation.model.AlarmModel;
import com.nex3z.shalarm.presentation.presenter.ModifyAlarmPresenter;
import com.nex3z.shalarm.presentation.presenter.UpdateAlarmPresenter;

public class EditAlarmActivity extends ModifyAlarmActivity {
    private static final String LOG_TAG = EditAlarmActivity.class.getSimpleName();

    @Override
    protected ModifyAlarmPresenter getPresenter(AlarmModel alarmModel) {
        AlarmRepository repository = new AlarmDataRepository(
                new AlarmDataStoreFactory(), new AlarmEntityDataMapper(), new AlarmDataMapper());
        UseCase modifyAlarm = new UpdateAlarm(repository, new JobExecutor(), new UIThread());
        UseCase deleteAlarm = new DeleteAlarmById(repository, new JobExecutor(), new UIThread());
        return new UpdateAlarmPresenter(alarmModel, modifyAlarm, deleteAlarm,
                new AlarmModelDataMapper());
    }

    @Override
    protected boolean showDeleteButton() {
        return true;
    }

}
