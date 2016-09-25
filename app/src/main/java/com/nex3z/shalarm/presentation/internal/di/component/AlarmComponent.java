package com.nex3z.shalarm.presentation.internal.di.component;

import com.nex3z.shalarm.presentation.alert.ui.AlertActivity;
import com.nex3z.shalarm.presentation.internal.di.PerActivity;
import com.nex3z.shalarm.presentation.internal.di.module.ActivityModule;
import com.nex3z.shalarm.presentation.internal.di.module.AlarmModule;
import com.nex3z.shalarm.presentation.ui.activity.AddAlarmActivity;
import com.nex3z.shalarm.presentation.ui.activity.EditAlarmActivity;
import com.nex3z.shalarm.presentation.ui.fragment.AlarmListFragment;

import dagger.Component;

@PerActivity
@Component(dependencies = AppComponent.class, modules = {ActivityModule.class, AlarmModule.class})
public interface AlarmComponent {
    void inject(AlarmListFragment alarmListFragment);
    void inject(AddAlarmActivity addAlarmActivity);
    void inject(EditAlarmActivity editAlarmActivity);
    void inject(AlertActivity alertActivity);
}
