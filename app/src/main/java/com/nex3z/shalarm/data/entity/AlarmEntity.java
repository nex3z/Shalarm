package com.nex3z.shalarm.data.entity;

import java.util.Date;
import java.util.Set;

public class AlarmEntity {

    private long mId;

    private boolean mIsEnabled;

    private Date mStart;

    private boolean mIsRepeated;

    private Set<Integer> mRepeatDays;

    private String mRingtone;

    private boolean mIsVibrateEnabled;

    private int mShakePower;

    private String mAlarmLabel;

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

    public String getRingtone() {
        return mRingtone;
    }

    public void setRingtone(String ringtone) {
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
        return "AlarmEntity: " +
                "mId = " + mId + "\n" +
                "mIsEnabled = " + mIsEnabled + "\n" +
                "mStart = " + mStart + "\n" +
                "mIsRepeated = " + mIsRepeated + "\n" +
                "mRepeatDays = " + mRepeatDays + "\n" +
                "mRingtone = " + mRingtone + "\n" +
                "mIsVibrateEnabled = " + mIsVibrateEnabled + "\n" +
                "mShakePower = " + mShakePower + "\n" +
                "mAlarmLabel = " + mAlarmLabel + "\n";
    }
}
