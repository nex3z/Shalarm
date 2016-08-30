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

    public static final String FILTER_ENABLED_ALARMS = "filter_enabled_alarms";
    public static final String FILTER_DISABLED_ALARMS = "filter_disabled_alarms";
    public static final String FILTER_ALL_ALARMS = "filter_all_alarms";

    @StringDef({FILTER_ENABLED_ALARMS, FILTER_DISABLED_ALARMS, FILTER_ALL_ALARMS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Filter {}

    @SortOrder private final String mSortOrder;

    @Filter private final String mFilter;

    public GetAlarmListArg() {
        this(SORT_BY_START_DESC);
    }

    public GetAlarmListArg(@SortOrder String sortOrder) {
        this(sortOrder, null);

    }

    public GetAlarmListArg(@SortOrder String sortOrder, @Filter String filter) {
        mSortOrder = sortOrder;
        mFilter = filter;
    }

    public @SortOrder String getSortOrder() {
        return mSortOrder;
    }

    public @Filter String getFilter() {
        return mFilter;
    }
}
