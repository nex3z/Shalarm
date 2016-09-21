package com.nex3z.shalarm.presentation.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlarmModel implements Parcelable {
    private static final String LOG_TAG = AlarmModel.class.getSimpleName();

    public static final int SUNDAY = 0;
    public static final int MONDAY = 1;
    public static final int TUESDAY = 2;
    public static final int WEDNESDAY = 3;
    public static final int THURSDAY = 4;
    public static final int FRIDAY = 5;
    public static final int SATURDAY = 6;

    @IntDef({MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface WeekDay {}

    private long mId;
    private boolean mIsEnabled;
    private Date mStart;
    private boolean mIsRepeated;
    private Set<Integer> mRepeatDays;
    private Uri mRingtone;
    private boolean mIsVibrateEnabled;
    private int mShakePower;
    private String mAlarmLabel;

    public AlarmModel() {
        mId = -1;
        mIsEnabled = false;

        Calendar current = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        start.setTime(new Date(0));
        start.set(Calendar.HOUR_OF_DAY, current.get(Calendar.HOUR_OF_DAY));
        start.set(Calendar.MINUTE, current.get(Calendar.MINUTE));
        mStart = start.getTime();

        mIsRepeated = false;
        mRepeatDays = new HashSet<>();
        mRingtone = Uri.EMPTY;
        mIsVibrateEnabled = false;
        mShakePower = 30;
        mAlarmLabel = "";
    }

    public AlarmModel(AlarmModel alarmModel) {
        mId = alarmModel.getId();
        mIsEnabled = alarmModel.isEnabled();
        mStart = new Date(alarmModel.getStart().getTime());
        mIsRepeated = alarmModel.isRepeated();
        mRepeatDays = new HashSet<>(alarmModel.getRepeatDays());
        mRingtone = alarmModel.getRingtone();
        mIsVibrateEnabled = alarmModel.isVibrateEnabled();
        mShakePower = alarmModel.getShakePower();
        mAlarmLabel = alarmModel.getAlarmLabel();
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public boolean isEnabled() {
        return mIsEnabled;
    }

    public void setEnabled(boolean enabled) {
        mIsEnabled = enabled;
    }

    public Date getStart() {
        return mStart;
    }

    public void setStart(Date start) {
        mStart = start;
    }

    public boolean isRepeated() {
        return mIsRepeated;
    }

    public void setRepeated(boolean repeated) {
        mIsRepeated = repeated;
    }

    public Set<Integer> getRepeatDays() {
        return mRepeatDays;
    }

    public void setRepeatDays(Set<Integer> repeatDays) {
        mRepeatDays = repeatDays;
    }

    public Uri getRingtone() {
        return mRingtone;
    }

    public void setRingtone(Uri ringtone) {
        mRingtone = ringtone;
    }

    public boolean isVibrateEnabled() {
        return mIsVibrateEnabled;
    }

    public void setVibrateEnabled(boolean vibrateEnabled) {
        mIsVibrateEnabled = vibrateEnabled;
    }

    public int getShakePower() {
        return mShakePower;
    }

    public void setShakePower(int shakePower) {
        mShakePower = shakePower;
    }

    public String getAlarmLabel() {
        return mAlarmLabel;
    }

    public void setAlarmLabel(String alarmLabel) {
        mAlarmLabel = alarmLabel;
    }

    @Override
    public String toString() {
        return "AlarmModel: " +
                "mId = " + mId + ", " +
                "mIsEnabled = " + mIsEnabled + ", " +
                "mStart = " + mStart + ", " +
                "mIsRepeated = " + mIsRepeated + ", " +
                "mRepeatDays = " + mRepeatDays + ", " +
                "mRingtone = " + mRingtone + ", " +
                "mIsVibrateEnabled = " + mIsVibrateEnabled + ", " +
                "mShakePower = " + mShakePower + ", " +
                "mAlarmLabel = " + mAlarmLabel + "\n";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(mId);
        parcel.writeByte((byte) (mIsEnabled ? 1 : 0));
        parcel.writeLong(mStart.getTime());
        parcel.writeByte((byte) (mIsRepeated ? 1 : 0));
        parcel.writeList(new ArrayList<>(mRepeatDays));
        parcel.writeString(mRingtone.toString());
        parcel.writeByte((byte) (mIsVibrateEnabled ? 1 : 0));
        parcel.writeInt(mShakePower);
        parcel.writeString(mAlarmLabel);
    }

    public Date getNextAlertTime() {
        Calendar alert = GregorianCalendar.getInstance();
        alert.setTime(mStart);
        Calendar current = GregorianCalendar.getInstance();
        Log.v(LOG_TAG, "getNextAlertTime(): alert = " + alert.getTime() + ", current = " + current.getTime());

        if (alert.before(current)) {
            alert.set(Calendar.YEAR, current.get(Calendar.YEAR));
            alert.set(Calendar.DAY_OF_YEAR, current.get(Calendar.DAY_OF_YEAR));

            if (alert.before(current)) {
                alert.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

        while((mRepeatDays.size() != 0)
                && (!mRepeatDays.contains(alert.get(Calendar.DAY_OF_WEEK) - 1))) {
            alert.add(Calendar.DAY_OF_MONTH, 1);
        }

        Log.v(LOG_TAG, "getNextAlertTime(): final alert = " + alert.getTime());

        return alert.getTime();
    }

    protected AlarmModel(Parcel in) {
        mId = in.readLong();
        mIsEnabled = in.readByte() != 0;
        mStart = new Date(in.readLong());
        mIsRepeated = in.readByte() != 0;
        ArrayList<Integer> days = in.readArrayList(List.class.getClassLoader());
        mRepeatDays = new HashSet<>(days);
        mRingtone = Uri.parse(in.readString());
        mIsVibrateEnabled = in.readByte() != 0;
        mShakePower = in.readInt();
        mAlarmLabel = in.readString();
    }

    public static final Creator<AlarmModel> CREATOR = new Creator<AlarmModel>() {
        public AlarmModel createFromParcel(android.os.Parcel source) {
            return new AlarmModel(source);
        }

        public AlarmModel[] newArray(int size) {
            return new AlarmModel[size];
        }
    };
}
