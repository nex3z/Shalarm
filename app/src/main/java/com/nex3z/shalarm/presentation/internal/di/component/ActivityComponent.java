package com.nex3z.shalarm.presentation.internal.di.component;

import android.app.Activity;

import com.nex3z.shalarm.presentation.internal.di.PerActivity;
import com.nex3z.shalarm.presentation.internal.di.module.ActivityModule;

import dagger.Component;

@PerActivity
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    Activity activity();
}
