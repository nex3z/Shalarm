package com.nex3z.shalarm.presentation.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.nex3z.shalarm.presentation.ui.misc.ToggleButtonGroup;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class ModifyAlarmActivity extends AppCompatActivity implements AddAlarmView,
        TimePickerDialog.OnTimeSetListener {
    private static final String LOG_TAG = ModifyAlarmActivity.class.getSimpleName();
    private static final String TAG_TIME_PICKER = "tag_time_picker";
    private static final int PICK_RINGTONE_REQUEST = 1;
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("kk:mm");

    public static final String ALARM_INFO = "alarm_info";

    private ModifyAlarmPresenter mPresenter;
    private Uri mRingtone;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toggle_weekdays)
    ToggleButtonGroup mToggleWeekdays;
    @BindView(R.id.tv_alarm_time)
    TextView mTvAlarmTime;
    @BindView(R.id.sw_vibrate)
    Switch mSwVibrate;
    @BindView(R.id.sb_shake_power)
    SeekBar mSbShakePower;
    @BindView(R.id.edit_label)
    EditText mEditLabel;
    @BindView(R.id.btn_ringtone)
    Button mBtnRingtone;

    abstract protected ModifyAlarmPresenter getPresenter(AlarmModel alarmModel);

    abstract protected boolean showDeleteButton();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        if (savedInstanceState == null) {
            AlarmModel alarmModel = getIntent().getParcelableExtra(ALARM_INFO);
            Log.v(LOG_TAG, "onCreate(): savedInstanceState == null, alarmModel = " + alarmModel);
            init(alarmModel);
        } else {
            init(null);
        }
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        Log.v(LOG_TAG, "onTimeSet(): hourOfDay = " + hourOfDay + ", minute = " + minute);
        Calendar calendar = GregorianCalendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        mPresenter.onStartTimeSet(calendar.getTime());
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
        mSbShakePower.setProgress(alarmModel.getShakePower());
        mEditLabel.setText(alarmModel.getAlarmLabel());
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
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_RINGTONE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                if (uri != null) {
                    mRingtone = uri;
                    String name = getRingtoneNameFromUri(uri);
                    mBtnRingtone.setText(name);
                }
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
        // intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, mRingtone);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        startActivityForResult(intent, PICK_RINGTONE_REQUEST);
    }

    private void init(AlarmModel alarm) {
        initView();
        initPresenter(alarm);
    }

    private void initView() {
        initActionBar();
        initMultiSwitchToggle();
    }

    private void initPresenter(AlarmModel alarmModel) {
        Log.v(LOG_TAG, "initPresenter(): alarmModel = " + alarmModel);
        mPresenter = getPresenter(alarmModel);

        mPresenter.setView(this);
        mPresenter.initialize();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_24dp);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void initMultiSwitchToggle() {
        String[] weekdays = getResources().getStringArray(R.array.weekdays);
        ArrayList<String> weekdaysList = new ArrayList<>(Arrays.asList(weekdays));
        mToggleWeekdays.setLabels(weekdaysList);
        mToggleWeekdays.setToggleButtonStateChangedListener((position, isEnabled) -> {
            List<Boolean> state = mToggleWeekdays.getToggleState();
            Log.v(LOG_TAG, "onToggleButtonStateChanged(): state = " + state);
        });
    }

    private void renderRingtoneButton(Uri uri) {
        Log.v(LOG_TAG, "renderRingtoneButton(): uri = " + uri);
        if (uri == null) {
            mRingtone = RingtoneManager.getActualDefaultRingtoneUri(this,
                    RingtoneManager.TYPE_ALARM);
        } else {
            mRingtone = uri;
        }
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
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mPresenter.onDelete();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private String getRingtoneNameFromUri(Uri uri) {
        Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
        String title = ringtone.getTitle(this);
        Log.v(LOG_TAG, "getRingtoneNameFromUri(): uri = " + uri + ", title = " + title);
        String[] tokens = title.split("\\.(?=[^\\.]+$)");
        return tokens[0];
    }

}
