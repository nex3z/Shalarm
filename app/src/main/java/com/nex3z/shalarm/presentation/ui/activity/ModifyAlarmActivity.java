package com.nex3z.shalarm.presentation.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.nex3z.shalarm.R;
import com.nex3z.shalarm.presentation.model.AlarmModel;
import com.nex3z.shalarm.presentation.presenter.ModifyAlarmPresenter;
import com.nex3z.shalarm.presentation.ui.AddAlarmView;
import com.nex3z.shalarm.presentation.utility.AlarmUtility;
import com.nex3z.togglebuttongroup.MultiSelectToggleGroup;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public abstract class ModifyAlarmActivity extends BaseActivity implements AddAlarmView,
        TimePickerDialog.OnTimeSetListener {
    private static final String LOG_TAG = ModifyAlarmActivity.class.getSimpleName();
    private static final String TAG_TIME_PICKER = "tag_time_picker";
    private static final int PICK_RINGTONE_REQUEST = 1;
    private static final DateFormat TIME_FORMAT = DateFormat.getTimeInstance(DateFormat.SHORT);
    private static final String STATE_ALARM_MODEL = "state_alarm";

    public static final String ALARM_INFO = "alarm_info";

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.toggle_weekdays) MultiSelectToggleGroup mToggleWeekdays;
    @BindView(R.id.tv_alarm_time) TextView mTvAlarmTime;
    @BindView(R.id.sw_vibrate) Switch mSwVibrate;
    @BindView(R.id.sb_shake_power) SeekBar mSbShakePower;
    @BindView(R.id.edit_label) EditText mEditLabel;
    @BindView(R.id.btn_ringtone) Button mBtnRingtone;
    @BindView(R.id.tv_shake_power_description) TextView mTvShakePowerDescription;

    private Unbinder mUnbinder;
    private ModifyAlarmPresenter mPresenter;
    private Uri mRingtone;

    abstract protected ModifyAlarmPresenter getPresenter(AlarmModel alarmModel);

    abstract protected boolean showDeleteButton();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_alarm);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        AlarmModel alarmModel = null;
        if (savedInstanceState == null) {
            alarmModel = getIntent().getParcelableExtra(ALARM_INFO);
            Log.v(LOG_TAG, "onCreate(): savedInstanceState == null, alarmModel = " + alarmModel);
        } else {
            alarmModel = savedInstanceState.getParcelable(STATE_ALARM_MODEL);
            Log.v(LOG_TAG, "onCreate(): recovered from savedInstanceState, alarmModel = " + alarmModel);
        }
        init(alarmModel);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.v(LOG_TAG, "onSaveInstanceState(): mPresenter.getCurrentAlarmModel() = " + mPresenter.getCurrentAlarmModel());
        outState.putParcelable(STATE_ALARM_MODEL, mPresenter.getCurrentAlarmModel());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_alarm, menu);

        MenuItem item = menu.findItem(R.id.action_delete);
        if (!showDeleteButton()) {
            item.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            mPresenter.onSave();
            return true;
        } else if (id == R.id.action_delete) {
            requestConfirmDelete();
        } else if (id == android.R.id.home) {
            finishView();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.v(LOG_TAG, "onBackPressed()");
        overridePendingTransition(R.transition.alarm_detail_enter_transition, R.transition.alarm_detail_enter_transition);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        mPresenter.onStartTimeSet(hourOfDay, minute, second);
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
    public void renderAlarm(AlarmModel alarmModel) {
        renderStartTime(alarmModel.getStart());
        renderRepeatDays(alarmModel.getRepeatDays());
        renderRingtoneButton(alarmModel.getRingtone());

        mSwVibrate.setChecked(alarmModel.isVibrateEnabled());
        mEditLabel.setText(alarmModel.getAlarmLabel());
        mEditLabel.clearFocus();
        mSbShakePower.setProgress(alarmModel.getShakePower());

    }

    @Override
    public void showTimePicker(Date date) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);

        TimePickerDialog dpd = TimePickerDialog.newInstance(
                this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        dpd.show(getFragmentManager(), TAG_TIME_PICKER);
    }

    @Override
    public void renderStartTime(Date date) {
        mTvAlarmTime.setText(TIME_FORMAT.format(date));
    }

    @Override
    public Set<Integer> getRepeatDays() {
        return mToggleWeekdays.getCheckedPositions();
    }

    @Override
    public Uri getRingtone() {
        return mRingtone;
    }

    @Override
    public boolean isVibrateEnabled() {
        return mSwVibrate.isChecked();
    }

    @Override
    public int getShakePower() {
        return mSbShakePower.getProgress();
    }

    @Override
    public String getLabel() {
        return mEditLabel.getText().toString();
    }

    @Override
    public void finishView() {
        supportFinishAfterTransition();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_RINGTONE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                if (uri != null) {
                    mRingtone = uri;
                } else {
                    mRingtone = Uri.EMPTY;
                }
                Log.v(LOG_TAG, "onActivityResult(): mRingtone = " + mRingtone);
                String name = getRingtoneNameFromUri(mRingtone);
                mBtnRingtone.setText(name);
            }
        }
    }

    @OnClick(R.id.tv_alarm_time)
    public void onAlarmTimeClicked(View view) {
        mPresenter.onSetStartTime();
    }

    @OnClick(R.id.btn_ringtone)
    public void onRingtoneClicked(View view) {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, mRingtone);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        startActivityForResult(intent, PICK_RINGTONE_REQUEST);
    }

    private void init(AlarmModel alarm) {
        initView();
        setupPresenter(alarm);
    }

    private void initView() {
        setupActionBar();
        setupProgressBar();
    }

    private void setupPresenter(AlarmModel alarmModel) {
        mPresenter = getPresenter(alarmModel);
        mPresenter.setView(this);
        mPresenter.initialize();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_24dp);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void setupProgressBar() {
        mSbShakePower.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mTvShakePowerDescription.setText(AlarmUtility.getShakeDescription(i));
                mTvShakePowerDescription.setTextColor(AlarmUtility.getBackgroundColor(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void renderRingtoneButton(Uri uri) {
        mRingtone = uri;
        mBtnRingtone.setText(getRingtoneNameFromUri(mRingtone));
    }

    private void renderRepeatDays(Set<Integer> repeatDays) {
        if (repeatDays != null) {
            mToggleWeekdays.setCheckedPositions(repeatDays);
        }
    }

    public void requestConfirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.delete_alarm_confirm_message));
        builder.setPositiveButton(android.R.string.yes, (dialog, id) -> {
            mPresenter.onDelete();
            dialog.dismiss();
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, id) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    private String getRingtoneNameFromUri(Uri uri) {
        if (uri != null && !uri.equals(Uri.EMPTY)) {
            Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
            String title = ringtone.getTitle(this);
            String[] tokens = title.split("\\.(?=[^\\.]+$)");
            return tokens[0];
        } else {
            return getString(R.string.alarm_detail_ringtone_none);
        }
    }

}
