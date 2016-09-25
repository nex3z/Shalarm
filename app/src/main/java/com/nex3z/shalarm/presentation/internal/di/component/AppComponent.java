package com.nex3z.shalarm.presentation.internal.di.component;

import android.content.Context;

import com.nex3z.shalarm.domain.executor.PostExecutionThread;
import com.nex3z.shalarm.domain.executor.ThreadExecutor;
import com.nex3z.shalarm.domain.repository.AlarmRepository;
import com.nex3z.shalarm.presentation.internal.di.module.AppModule;
import com.nex3z.shalarm.presentation.ui.activity.BaseActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(BaseActivity activity);

    Context context();
    ThreadExecutor threadExecutor();
    PostExecutionThread postExecutionThread();
    AlarmRepository alarmRepository();
}
