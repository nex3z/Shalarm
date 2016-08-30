package com.nex3z.shalarm.data.repository.datasource.alarm;

import android.net.Uri;
import android.util.Log;

import com.nex3z.shalarm.app.App;
import com.nex3z.shalarm.data.entity.AlarmEntity;
import com.nex3z.shalarm.data.entity.mapper.AlarmCursorDataMapper;
import com.nex3z.shalarm.data.entity.mapper.AlarmEntityDataMapper;
import com.nex3z.shalarm.data.provider.AlarmContract;
import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

public class ContentProviderDataStore implements AlarmDataStore {
    private static final String LOG_TAG = ContentProviderDataStore.class.getSimpleName();

    public static final String FILTER_ENABLED_ALARMS = "filter_enabled_alarms";
    public static final String FILTER_DISABLED_ALARMS = "filter_disabled_alarms";
    public static final String FILTER_ALL_ALARMS = "filter_all_alarms";

    private static final String sAlarmIdSelection = AlarmContract.AlarmEntry.TABLE_NAME + "." +
            AlarmContract.AlarmEntry._ID + " = ?";

    private final BriteContentResolver mBriteContentResolver;
    private final AlarmCursorDataMapper mAlarmCursorDataMapper;
    private final AlarmEntityDataMapper mAlarmEntityDataMapper;

    public ContentProviderDataStore() {
        SqlBrite sqlBrite = SqlBrite.create();
        mBriteContentResolver = sqlBrite.wrapContentProvider(
                App.getAppContext().getContentResolver(), Schedulers.io());
        mAlarmCursorDataMapper = new AlarmCursorDataMapper();
        mAlarmEntityDataMapper = new AlarmEntityDataMapper();
    }

    @Override
    public Observable<List<AlarmEntity>> getAlarmEntityList(String sortBy) {
        return mBriteContentResolver
                .createQuery(
                        AlarmContract.AlarmEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        sortBy,
                        true)
                .map(SqlBrite.Query::run)
                .map(mAlarmCursorDataMapper::transformList);
    }

    @Override
    public Observable<List<AlarmEntity>> getAlarmEntityList(String sortBy, String filter) {
        String selection = null;
        String[] selectionArgs = null;
        if (filter != null) {
            switch (filter) {
                case FILTER_ENABLED_ALARMS:
                    selectionArgs = new String[]{"1"};
                    break;
                case FILTER_DISABLED_ALARMS:
                    selectionArgs = new String[]{"0"};
                    break;
                case FILTER_ALL_ALARMS:
                default:
                    selectionArgs = new String[]{"*"};
                    break;
            }
            selection = AlarmContract.AlarmEntry.COLUMN_ENABLE + " = ?";
        }

        return mBriteContentResolver
                .createQuery(
                        AlarmContract.AlarmEntry.CONTENT_URI,
                        null,
                        selection,
                        selectionArgs,
                        sortBy,
                        true)
                .map(SqlBrite.Query::run)
                .map(mAlarmCursorDataMapper::transformList);
    }

    @Override
    public Observable<AlarmEntity> getAlarmEntityById(long alarmId) {
        String[] selectionArgs = new String[]{Long.toString(alarmId)};
        return mBriteContentResolver
                .createQuery(
                        AlarmContract.AlarmEntry.CONTENT_URI,
                        null,
                        sAlarmIdSelection,
                        selectionArgs,
                        null,
                        true
                )
                .map(SqlBrite.Query::run)
                .map(mAlarmCursorDataMapper::transform);
    }

    @Override
    public Observable<Long> insertAlarmEntity(AlarmEntity alarm) {
        return Observable.<Long>create(subscriber -> {
                    Uri uri = App.getAppContext()
                            .getContentResolver()
                            .insert(AlarmContract.AlarmEntry.CONTENT_URI,
                                    mAlarmEntityDataMapper.toContentValues(alarm));
                    Log.v(LOG_TAG, "buildUseCaseObservable(): uri = " + uri);
                    Long id = AlarmContract.AlarmEntry.getAlarmIdFromUri(uri);
                    subscriber.onNext(id);
                    subscriber.onCompleted();
                }
        );
    }

    @Override
    public Observable<Integer> deleteAlarmEntity(long alarmId) {
        String[] selectionArgs = new String[]{Long.toString(alarmId)};
        Log.v(LOG_TAG, "deleteAlarmEntity(): selectionArgs[0] = " + selectionArgs[0]);

        return Observable.<Integer>create(subscriber -> {
            int deleted = App.getAppContext().getContentResolver().delete(
                    AlarmContract.AlarmEntry.CONTENT_URI,
                    sAlarmIdSelection,
                    selectionArgs);
            Log.v(LOG_TAG, "deleteAlarmEntity(): deleted = " + deleted);
            subscriber.onNext(deleted);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Integer> updateAlarmEntity(AlarmEntity alarm) {
        String[] selectionArgs = new String[]{Long.toString(alarm.getId())};
        Log.v(LOG_TAG, "updateAlarmEntity(): selectionArgs[0] = " + selectionArgs[0]);
        return Observable.<Integer>create(subscriber -> {
            int updated = App.getAppContext().getContentResolver().update(
                    AlarmContract.AlarmEntry.CONTENT_URI,
                    mAlarmEntityDataMapper.toContentValues(alarm),
                    sAlarmIdSelection,
                    selectionArgs);
            Log.v(LOG_TAG, "updateAlarmEntity(): updated = " + updated);
            subscriber.onNext(updated);
            subscriber.onCompleted();
        });
    }
}
