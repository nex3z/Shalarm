package com.nex3z.shalarm.presentation.alert.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.nex3z.shalarm.R;
import com.nex3z.shalarm.presentation.alert.AlertWakeLock;
import com.nex3z.shalarm.presentation.internal.di.HasComponent;
import com.nex3z.shalarm.presentation.internal.di.component.AlarmComponent;
import com.nex3z.shalarm.presentation.internal.di.component.DaggerAlarmComponent;
import com.nex3z.shalarm.presentation.internal.di.module.AlarmModule;
import com.nex3z.shalarm.presentation.model.AlarmModel;
import com.nex3z.shalarm.presentation.presenter.AlertPresenter;
import com.nex3z.shalarm.presentation.ui.activity.AlarmListActivity;
import com.nex3z.shalarm.presentation.ui.activity.BaseActivity;
import com.nex3z.shalarm.presentation.utility.AlarmUtility;
import com.nex3z.shalarm.presentation.utility.SensorUtility;

import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlertActivity extends BaseActivity implements AlertView, SensorEventListener,
        HasComponent<AlarmComponent> {
    private static final String LOG_TAG = AlertActivity.class.getSimpleName();

    private static final int NOTIFICATION_ID_MISSED_ALARM = 1;
    private static final int NOTIFICATION_ID_ALERTING = 2;
    private static final String ACTION_ALERTING_NOTIFICATION_DISMISSED = "com.nex3z.shalarm.presentation.alert.ui.action.ALERTING_NOTIFICATION_DISMISSED";
    private static final String ACTION_RELAUNCH = "com.nex3z.shalarm.presentation.alert.ui.action.ACTION_RELAUNCH";

    private static final long[] VIBRATE_PATTERN = { 0, 1000, 1000 };
    private static final float MAX_FORCE = 2.0f;
    private static final float ONE_G = 1.0f;

    public static final String ALARM = "alarm";

    @BindView(R.id.tv_time) TextView mTvTime;
    @BindView(R.id.tv_label) TextView mTvLabel;
    @BindView(R.id.circle_shake_power) ExpandableCircleView mCircle;

    @Inject AlertPresenter mPresenter;

    private AlarmComponent mAlarmComponent;
    private Vibrator mVibrator;
    private MediaPlayer mMediaPlayer;

    private Handler mClockUpdateHandler = new Handler();
    private Runnable mClockUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            mTvTime.setText(AlarmUtility.TIME_FORMAT.format(new Date()));
            mClockUpdateHandler.postDelayed(this, 1000);
        }
    };

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        final String action = intent.getAction();
        if (action.equals(ACTION_ALERTING_NOTIFICATION_DISMISSED)) {
            mPresenter.onAlertCanceled();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.resume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mClockUpdateHandler.post(mClockUpdateRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.pause();
        mSensorManager.unregisterListener(this);
        mClockUpdateHandler.removeCallbacks(mClockUpdateRunnable);

        AlertWakeLock.unlock(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @OnClick(R.id.btn_cancel_alert)
    public void onClick(View view) {
        mPresenter.onAlertCanceled();
    }

    @Override
    public void renderAlarm(AlarmModel alarmModel) {
        mCircle.setInnerColor(AlarmUtility.getBackgroundColor(alarmModel.getShakePower()));

        String label = alarmModel.getAlarmLabel();
        if (label != null && !label.isEmpty()) {
            mTvLabel.setText(label);
        }

        showAlertingNotification(alarmModel);
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
            Log.e(LOG_TAG, "startRingtone(): IllegalStateException");
        }
    }

    @Override
    public void pauseRingtone() {
        try {
            mMediaPlayer.pause();
        } catch (IllegalStateException e) {
            Log.e(LOG_TAG, "pauseRingtone(): IllegalStateException");
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
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID_ALERTING);
        finish();
    }

    @Override
    public void renderForce(float current, float target) {
        mCircle.setProgress((int)(current / target * 100), true);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float force = SensorUtility.calculateForce(event);
        mPresenter.onShakeForceChanged(force);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    @Override
    public void showMissedAlarmNotification(AlarmModel alarmModel) {
        String content = AlarmUtility.TIME_FORMAT.format(alarmModel.getStart());
        if (alarmModel.getAlarmLabel() != null && !alarmModel.getAlarmLabel().isEmpty()) {
            content += " " + alarmModel.getAlarmLabel();
        }

        Intent intent = new Intent(this, AlarmListActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_alarm_white_16)
                .setContentTitle(getString(R.string.alert_missed_notification_title))
                .setContentText(content)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        NotificationManager NotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationManager.notify(NOTIFICATION_ID_MISSED_ALARM, mBuilder.build());
    }

    @Override
    public AlarmComponent getComponent() {
        return mAlarmComponent;
    }

    private void init(AlarmModel alarmModel) {
        initAlert();
        initInjector(alarmModel);
        setupPresenter();
        setupPhoneStateListener();
    }

    private void initInjector(AlarmModel alarmModel) {
        mAlarmComponent = DaggerAlarmComponent.builder()
                .appComponent(getAppComponent())
                .activityModule(getActivityModule())
                .alarmModule(new AlarmModule(alarmModel))
                .build();
        mAlarmComponent.inject(this);
    }

    private void initAlert() {
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mMediaPlayer = new MediaPlayer();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void setupPresenter() {
        mPresenter.setView(this);
        mPresenter.initialize();
    }

    private void setupPhoneStateListener() {
        TelephonyManager telephonyManager = (TelephonyManager)
                this.getSystemService(Context.TELEPHONY_SERVICE);

        PhoneStateListener listener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        try {
                            mPresenter.onCallStateRinging();
                        } catch (IllegalStateException e) {
                            Log.e(LOG_TAG, "onCallStateChanged(): e = " + e.getMessage());
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
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

    private void showAlertingNotification(AlarmModel alarmModel) {
        String title = alarmModel.getAlarmLabel();
        if (title == null || title.isEmpty()) {
            title = getString(R.string.alerting_notification_title);
        }

        String content = AlarmUtility.TIME_FORMAT.format(alarmModel.getStart());

        Intent clickIntent = new Intent(this, AlertActivity.class);
        clickIntent.setAction(ACTION_RELAUNCH);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, 0, clickIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent dismissIntent = new Intent(this, AlertActivity.class);
        dismissIntent.setAction(ACTION_ALERTING_NOTIFICATION_DISMISSED);
        PendingIntent deleteIntent = PendingIntent.getActivity(
                this, 0, dismissIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_alarm_white_16)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(contentIntent)
                .setDeleteIntent(deleteIntent);

        NotificationManager NotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationManager.notify(NOTIFICATION_ID_ALERTING, mBuilder.build());
    }

}
