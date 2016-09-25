package com.nex3z.shalarm.presentation.ui.activity;

import android.support.v7.app.AppCompatActivity;

import com.nex3z.shalarm.app.App;
import com.nex3z.shalarm.presentation.internal.di.component.AppComponent;
import com.nex3z.shalarm.presentation.internal.di.module.ActivityModule;

public abstract class BaseActivity extends AppCompatActivity {

    protected AppComponent getAppComponent() {
        return ((App) getApplication()).getAppComponent();
    }

    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }
}
