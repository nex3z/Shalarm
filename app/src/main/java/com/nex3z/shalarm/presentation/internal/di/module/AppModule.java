package com.nex3z.shalarm.presentation.internal.di.module;

import android.content.Context;

import com.nex3z.shalarm.app.App;
import com.nex3z.shalarm.data.executor.JobExecutor;
import com.nex3z.shalarm.data.repository.AlarmDataRepository;
import com.nex3z.shalarm.domain.executor.PostExecutionThread;
import com.nex3z.shalarm.domain.executor.ThreadExecutor;
import com.nex3z.shalarm.domain.repository.AlarmRepository;
import com.nex3z.shalarm.presentation.UIThread;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private final App mApp;

    public AppModule(App app) {
        mApp = app;
    }

    @Provides @Singleton
    Context provideApplicationContext() {
        return mApp;
    }

    @Provides @Singleton
    ThreadExecutor provideThreadExecutor(JobExecutor jobExecutor) {
        return jobExecutor;
    }

    @Provides @Singleton
    PostExecutionThread providePostExecutionThread(UIThread uiThread) {
        return uiThread;
    }

    @Provides @Singleton
    AlarmRepository provideUserRepository(AlarmDataRepository alarmDataRepository) {
        return alarmDataRepository;
    }

}
