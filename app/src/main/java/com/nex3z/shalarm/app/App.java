package com.nex3z.shalarm.app;

import android.app.Application;
import android.content.Context;

import com.nex3z.shalarm.presentation.internal.di.component.AppComponent;
import com.nex3z.shalarm.presentation.internal.di.component.DaggerAppComponent;
import com.nex3z.shalarm.presentation.internal.di.module.AppModule;

public class App extends Application {
    private static Context mContext;
    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();

        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    public static Context getAppContext() {
        return mContext;
    }

}