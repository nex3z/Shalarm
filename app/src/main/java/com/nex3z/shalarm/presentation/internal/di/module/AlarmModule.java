package com.nex3z.shalarm.presentation.internal.di.module;

import android.util.Log;

import com.nex3z.shalarm.domain.executor.PostExecutionThread;
import com.nex3z.shalarm.domain.executor.ThreadExecutor;
import com.nex3z.shalarm.domain.interactor.UseCase;
import com.nex3z.shalarm.domain.interactor.alarm.delete.DeleteAlarmById;
import com.nex3z.shalarm.domain.interactor.alarm.insert.AddAlarm;
import com.nex3z.shalarm.domain.interactor.alarm.query.GetAlarmList;
import com.nex3z.shalarm.domain.interactor.alarm.update.UpdateAlarm;
import com.nex3z.shalarm.domain.repository.AlarmRepository;
import com.nex3z.shalarm.presentation.internal.di.PerActivity;
import com.nex3z.shalarm.presentation.mapper.AlarmModelDataMapper;
import com.nex3z.shalarm.presentation.model.AlarmModel;
import com.nex3z.shalarm.presentation.presenter.AddAlarmPresenter;
import com.nex3z.shalarm.presentation.presenter.AlertPresenter;
import com.nex3z.shalarm.presentation.presenter.ModifyAlarmPresenter;
import com.nex3z.shalarm.presentation.presenter.UpdateAlarmPresenter;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class AlarmModule {
    private AlarmModel mAlarmModel;

    public AlarmModule() {}

    public AlarmModule(AlarmModel alarmModel) {
        mAlarmModel = alarmModel;
    }

    @Provides @PerActivity @Named("addAlarm")
    UseCase provideAddAlarmUseCase(AlarmRepository repository, ThreadExecutor threadExecutor,
                                   PostExecutionThread postExecutionThread) {
        return new AddAlarm(repository, threadExecutor, postExecutionThread);
    }

    @Provides @PerActivity @Named("getAlarmList")
    UseCase provideGetAlarmListUseCase(AlarmRepository repository, ThreadExecutor threadExecutor,
                                       PostExecutionThread postExecutionThread) {
        return new GetAlarmList(repository, threadExecutor, postExecutionThread);
    }

    @Provides @PerActivity @Named("updateAlarm")
    UseCase provideUpdateAlarmUseCase(AlarmRepository repository, ThreadExecutor threadExecutor,
                                      PostExecutionThread postExecutionThread) {
        return new UpdateAlarm(repository, threadExecutor, postExecutionThread);
    }

    @Provides @PerActivity @Named("deleteAlarm")
    UseCase provideDeleteAlarmByIdUseCase(AlarmRepository repository, ThreadExecutor threadExecutor,
                                      PostExecutionThread postExecutionThread) {
        return new DeleteAlarmById(repository, threadExecutor, postExecutionThread);
    }

    @Provides @PerActivity @Named("addAlarmPresenter")
    ModifyAlarmPresenter provideAddAlarmPresenter(@Named("addAlarm") UseCase addAlarm,
                                                  AlarmModelDataMapper mapper) {
        Log.v("DI", "provideAddAlarmPresenter()");
        return new AddAlarmPresenter(mAlarmModel, addAlarm, mapper);
    }

    @Provides @PerActivity @Named("updateAlarmPresenter")
    ModifyAlarmPresenter provideUpdateAlarmPresenter(@Named("updateAlarm") UseCase updateAlarm,
                                                     @Named("deleteAlarm") UseCase deleteAlarm,
                                                     AlarmModelDataMapper mapper) {
        Log.v("DI", "provideUpdateAlarmPresenter()");
        return new UpdateAlarmPresenter(mAlarmModel, updateAlarm, deleteAlarm, mapper);
    }

    @Provides @PerActivity
    AlertPresenter provideAlertPresenter(@Named("updateAlarm") UseCase updateAlarm,
                                         AlarmModelDataMapper mapper) {
        return new AlertPresenter(mAlarmModel, updateAlarm, mapper);
    }
}
