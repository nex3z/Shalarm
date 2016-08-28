package com.nex3z.shalarm.data.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

public class AlarmProvider extends ContentProvider {
    public static final String LOG_TAG = AlarmProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private AlarmDbHelper mOpenHelper;

    public static final int ALARM = 100;
    public static final int ALARM_WITH_ID = 101;

    private static final String sAlarmIdSelection = AlarmContract.AlarmEntry.TABLE_NAME + "." +
            AlarmContract.AlarmEntry._ID + " = ? ";

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AlarmContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, AlarmContract.PATH_AlARM, ALARM);
        matcher.addURI(authority, AlarmContract.PATH_AlARM + "/#", ALARM_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new AlarmDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ALARM_WITH_ID:
                return AlarmContract.AlarmEntry.CONTENT_ITEM_TYPE;
            case ALARM:
                return AlarmContract.AlarmEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case ALARM_WITH_ID: {
                long id = AlarmContract.AlarmEntry.getAlarmIdFromUri(uri);
                selectionArgs = new String[]{ String.valueOf(id) };
                selection = sAlarmIdSelection;
                Log.v(LOG_TAG, "query(): ALARM_WITH_ID, id = " + id);

                retCursor = mOpenHelper.getReadableDatabase().query(
                        AlarmContract.AlarmEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ALARM: {
                Log.v(LOG_TAG, "query(): ALARM");
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AlarmContract.AlarmEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }


    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case ALARM: {
                long id = db.insert(AlarmContract.AlarmEntry.TABLE_NAME, null, values);
                if ( id > 0 )
                    returnUri = AlarmContract.AlarmEntry.buildAlarmUri(id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(returnUri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (null == selection) selection = "1";
        switch (match) {
            case ALARM:
                rowsDeleted = db.delete(
                        AlarmContract.AlarmEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ALARM_WITH_ID:
                long id = AlarmContract.AlarmEntry.getAlarmIdFromUri(uri);
                selectionArgs = new String[]{ String.valueOf(id) };
                selection = sAlarmIdSelection;
                Log.v(LOG_TAG, "delete(): SCHEDULE_WITH_ID, id = " + id);
                rowsDeleted = db.delete(
                        AlarmContract.AlarmEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case ALARM:
                rowsUpdated = db.update(
                        AlarmContract.AlarmEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ALARM_WITH_ID:
                long id = AlarmContract.AlarmEntry.getAlarmIdFromUri(uri);
                selectionArgs = new String[]{ String.valueOf(id) };
                selection = sAlarmIdSelection;
                Log.v(LOG_TAG, "update(): SCHEDULE_WITH_ID, id = " + id);
                rowsUpdated = db.update(
                        AlarmContract.AlarmEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ALARM:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long id = db.insert(AlarmContract.AlarmEntry.TABLE_NAME, null, value);
                        if (id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri, null);

                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

}
