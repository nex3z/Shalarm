package com.nex3z.shalarm.presentation.ui.activity;

import android.util.Log;

import com.nex3z.shalarm.presentation.internal.di.HasComponent;
import com.nex3z.shalarm.presentation.internal.di.component.AlarmComponent;
import com.nex3z.shalarm.presentation.internal.di.component.DaggerAlarmComponent;
import com.nex3z.shalarm.presentation.internal.di.module.AlarmModule;
import com.nex3z.shalarm.presentation.model.AlarmModel;
import com.nex3z.shalarm.presentation.presenter.ModifyAlarmPresenter;

import javax.inject.Inject;
import javax.inject.Named;

public class EditAlarmActivity extends ModifyAlarmActivity implements HasComponent<AlarmComponent> {
    private static final String LOG_TAG = EditAlarmActivity.class.getSimpleName();

    @Inject @Named("updateAlarmPresenter") ModifyAlarmPresenter mUpdateAlarmPresenter;

    private AlarmComponent mAlarmComponent;
    private boolean mIsInjected;

    @Override
    protected ModifyAlarmPresenter getPresenter(AlarmModel alarmModel) {
        if (!mIsInjected) {
            initInjector(alarmModel);
            mIsInjected = true;
        }
        return mUpdateAlarmPresenter;
    }

    @Override
    protected boolean showDeleteButton() {
        return true;
    }

    @Override
    public AlarmComponent getComponent() {
        return mAlarmComponent;
    }

    private void initInjector(AlarmModel alarmModel) {
        mAlarmComponent = DaggerAlarmComponent.builder()
                .appComponent(getAppComponent())
                .activityModule(getActivityModule())
                .alarmModule(new AlarmModule(alarmModel))
                .build();
        mAlarmComponent.inject(this);
        Log.v(LOG_TAG, "initInjector(): mAlarmComponent = " + mAlarmComponent);
        Log.v(LOG_TAG, "initInjector(): mUpdateAlarmPresenter = " + mUpdateAlarmPresenter);
    }

}
