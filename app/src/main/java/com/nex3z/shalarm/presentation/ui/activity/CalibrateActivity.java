package com.nex3z.shalarm.presentation.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.nex3z.shalarm.R;
import com.nex3z.shalarm.presentation.internal.di.HasComponent;
import com.nex3z.shalarm.presentation.internal.di.component.CalibrateComponent;
import com.nex3z.shalarm.presentation.internal.di.component.DaggerCalibrateComponent;

public class CalibrateActivity extends BaseActivity implements HasComponent<CalibrateComponent> {

    private CalibrateComponent mCalibrateComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate);

        initInjector();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public CalibrateComponent getComponent() {
        return mCalibrateComponent;
    }

    private void initInjector() {
        mCalibrateComponent = DaggerCalibrateComponent.builder()
                .appComponent(getAppComponent())
                .build();
    }

}
