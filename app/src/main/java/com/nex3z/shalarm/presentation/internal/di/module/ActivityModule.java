package com.nex3z.shalarm.presentation.internal.di.module;

import android.app.Activity;

import com.nex3z.shalarm.presentation.internal.di.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {
    private final Activity mActivity;

    public ActivityModule(Activity activity) {
        this.mActivity = activity;
    }

    @Provides @PerActivity
    Activity activity() {
        return mActivity;
    }
}
