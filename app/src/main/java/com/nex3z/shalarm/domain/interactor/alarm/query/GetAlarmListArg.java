package com.nex3z.shalarm.domain.interactor.alarm.query;

import android.support.annotation.StringDef;

import com.nex3z.shalarm.data.provider.AlarmContract;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class GetAlarmListArg {

    public static final String SORT_BY_START_ASC =
            AlarmContract.AlarmEntry.COLUMN_START + " ASC";
    public static final String SORT_BY_START_DESC =
            AlarmContract.AlarmEntry.COLUMN_START + " DESC";
    @StringDef({SORT_BY_START_ASC, SORT_BY_START_DESC})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SortOrder {}

    @SortOrder private String mSortOrder;

    public GetAlarmListArg() {
        this(SORT_BY_START_DESC);
    }

    public GetAlarmListArg(@SortOrder String sortOrder) {
        mSortOrder = sortOrder;
    }

    public @SortOrder String getSortOrder() {
        return mSortOrder;
    }
}
