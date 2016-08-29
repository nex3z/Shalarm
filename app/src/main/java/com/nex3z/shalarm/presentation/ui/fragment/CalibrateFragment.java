package com.nex3z.shalarm.presentation.ui.fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nex3z.shalarm.R;
import com.nex3z.shalarm.presentation.presenter.CalibratePresenter;
import com.nex3z.shalarm.presentation.ui.CalibrateView;
import com.nex3z.shalarm.presentation.utility.SensorUtility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CalibrateFragment extends Fragment implements CalibrateView, SensorEventListener {
    private static final String LOG_TAG = CalibrateFragment.class.getSimpleName();

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float mMaxForce = 0;

    private CalibratePresenter mPresenter;

    @BindView(R.id.tv_maximum_force) TextView mTvMaxForce;

    public CalibrateFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calibrate, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.resume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.pause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float force = SensorUtility.calculateForce(event);
        mPresenter.onForceChanged(force);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void renderForceMeter(float force) {
        mTvMaxForce.setText(String.valueOf((int)(force * 100)));
    }

    @Override
    public void finishView() {
        getActivity().finish();
    }

    @OnClick(R.id.btn_calibrate)
    public void onClick(View view) {
        mPresenter.onConfirm();
    }

    private void init() {
        mPresenter = new CalibratePresenter();
        mPresenter.setView(this);
        mPresenter.initialize();

        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
}
