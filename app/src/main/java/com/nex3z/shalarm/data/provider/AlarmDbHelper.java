package com.nex3z.shalarm.data.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nex3z.shalarm.data.provider.AlarmContract.AlarmEntry;

public class AlarmDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "alarm.db";

    public AlarmDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ALARM_TABLE = "CREATE TABLE " + AlarmEntry.TABLE_NAME + " (" +
                AlarmEntry._ID + " INTEGER PRIMARY KEY," +
                AlarmEntry.COLUMN_ENABLE + " INTEGER NOT NULL, " +
                AlarmEntry.COLUMN_START + " INTEGER NOT NULL, " +
                AlarmEntry.COLUMN_REPEAT + " INTEGER NOT NULL, " +
                AlarmEntry.COLUMN_REPEAT_DAY + " TEXT NOT NULL, " +
                AlarmEntry.COLUMN_RINGTONE + " TEXT NOT NULL, " +
                AlarmEntry.COLUMN_VIBRATE + " INTEGER NOT NULL, " +
                AlarmEntry.COLUMN_SHAKE_POWER + " INTEGER NOT NULL, " +
                AlarmEntry.COLUMN_LABEL + " TEXT NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_ALARM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AlarmEntry.TABLE_NAME);
        onCreate(db);
    }
}
