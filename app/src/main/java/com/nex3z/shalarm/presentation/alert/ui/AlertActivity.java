package com.nex3z.shalarm.presentation.alert.ui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.nex3z.shalarm.R;
import com.nex3z.shalarm.data.entity.mapper.AlarmEntityDataMapper;
import com.nex3z.shalarm.data.executor.JobExecutor;
import com.nex3z.shalarm.data.repository.AlarmDataRepository;
import com.nex3z.shalarm.data.repository.datasource.alarm.AlarmDataStoreFactory;
import com.nex3z.shalarm.domain.interactor.UseCase;
import com.nex3z.shalarm.domain.interactor.alarm.update.UpdateAlarm;
import com.nex3z.shalarm.domain.mapper.AlarmDataMapper;
import com.nex3z.shalarm.domain.repository.AlarmRepository;
import com.nex3z.shalarm.presentation.UIThread;
import com.nex3z.shalarm.presentation.alert.AlertWakeLock;
import com.nex3z.shalarm.presentation.mapper.AlarmModelDataMapper;
import com.nex3z.shalarm.presentation.model.AlarmModel;
import com.nex3z.shalarm.presentation.presenter.AlertPresenter;
import com.nex3z.shalarm.presentation.utility.SensorUtility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlertActivity extends AppCompatActivity implements AlertView, SensorEventListener {
    private static final String LOG_TAG = AlertActivity.class.getSimpleName();

    private static final long[] VIBRATE_PATTERN = { 0, 1000, 1000 };
    private static final float MAX_FORCE = 2.0f;
    private static final float ONE_G = 1.0f;

    public static final String ALARM = "alarm";

    private Vibrator mVibrator;
    private MediaPlayer mMediaPlayer;
    private AlertPresenter mPresenter;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    @BindView(R.id.tv_label) TextView mTvLabel;
    @BindView(R.id.circle_shake_power) ExpandableCircleView mCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_alert);

        ButterKnife.bind(this);

        AlarmModel alarmModel = getIntent().getParcelableExtra(ALARM);

        Log.v(LOG_TAG, "onCreate(): Alarm fired, id = " + alarmModel.getId() + ", label = "
                + alarmModel.getAlarmLabel());

        init(alarmModel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.resume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.pause();
        mSensorManager.unregisterListener(this);

        AlertWakeLock.unlock(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @OnClick(R.id.btn_cancel_alert)
    public void onClick(View view) {
        Log.v(LOG_TAG, "OnClick");
        mPresenter.onAlertCanceled();
        finish();
    }

    @Override
    public void renderAlarm(AlarmModel alarmModel) {
        String label = alarmModel.getAlarmLabel();
        if (label != null && !label.isEmpty()) {
            mTvLabel.setText(label);
        }
    }

    @Override
    public void setRingtone(Uri uri) {
        Log.v(LOG_TAG, "setRingtone(): uri = " + uri);
        try {
            mMediaPlayer.setVolume(1.0f, 1.0f);
            mMediaPlayer.setDataSource(this, uri);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
        } catch (Exception e) {
            mMediaPlayer.release();
        }
    }

    @Override
    public void startRingtone() {
        Log.v(LOG_TAG, "startRingtone()");
        try {
            mMediaPlayer.start();
        } catch (IllegalStateException e) {
            Log.e(LOG_TAG, "startRingtone(): e = " + e.getMessage());
        }
    }

    @Override
    public void pauseRingtone() {
        try {
            mMediaPlayer.pause();
        } catch (IllegalStateException e) {
            Log.e(LOG_TAG, "pauseRingtone(): e = " + e.getMessage());
        }
    }

    @Override
    public void stopRingtone() {
        try {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        } catch (IllegalStateException e) {
            Log.e(LOG_TAG, "stopRingtone(): e = " + e.getMessage());
        }
    }

    @Override
    public void startVibrate() {
        mVibrator.vibrate(VIBRATE_PATTERN, 0);
    }

    @Override
    public void stopVibrate() {
        mVibrator.cancel();
    }

    @Override
    public void showResult(long time) {
        Toast.makeText(this, "Time: " + time, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void finishView() {
        finish();
    }

    @Override
    public void renderForce(float current, float target) {
        mCircle.expand(current / target);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float force = SensorUtility.calculateForce(event);
        mPresenter.onShakeForceChanged(force);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    private void init(AlarmModel alarmModel) {
        initAlert();
        initShakeDetector(alarmModel);
        initPresenter(alarmModel);
        initPhoneStateListener();
    }

    private void initShakeDetector(AlarmModel alarmModel) {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void initAlert() {
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mMediaPlayer = new MediaPlayer();
    }

    private void initPresenter(AlarmModel alarmModel) {
        AlarmRepository repository = new AlarmDataRepository(
                new AlarmDataStoreFactory(), new AlarmEntityDataMapper(), new AlarmDataMapper());
        UseCase useCase = new UpdateAlarm(repository, new JobExecutor(), new UIThread());
        mPresenter = new AlertPresenter(alarmModel, useCase, new AlarmModelDataMapper());

        mPresenter.setView(this);
        mPresenter.initialize();
    }


    private void initPhoneStateListener() {
        TelephonyManager telephonyManager = (TelephonyManager)
                this.getSystemService(Context.TELEPHONY_SERVICE);

        PhoneStateListener listener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.v(LOG_TAG, "onCallStateChanged(): CALL_STATE_RINGING, number = "
                                + incomingNumber);
                        try {
                            mPresenter.onCallStateRinging();
                        } catch (IllegalStateException e) {
                            Log.e(LOG_TAG, "onCallStateChanged(): e = " + e.getMessage());
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        Log.v(LOG_TAG, "onCallStateChanged(): CALL_STATE_IDLE");
                        try {
                            mPresenter.onCallStateIdle();
                        } catch (IllegalStateException e) {
                            Log.e(LOG_TAG, "onCallStateChanged(): e = " + e.getMessage());
                        }
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };

        telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

}
