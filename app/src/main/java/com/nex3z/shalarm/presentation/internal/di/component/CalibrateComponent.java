package com.nex3z.shalarm.presentation.internal.di.component;

import com.nex3z.shalarm.presentation.internal.di.PerActivity;
import com.nex3z.shalarm.presentation.internal.di.module.CalibrateModule;
import com.nex3z.shalarm.presentation.ui.fragment.CalibrateFragment;

import dagger.Component;

@PerActivity
@Component(dependencies = AppComponent.class,
        modules = {CalibrateModule.class})
public interface CalibrateComponent {
    void inject(CalibrateFragment calibrateFragment);
}
