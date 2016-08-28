package com.nex3z.shalarm.data.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class AlarmContract {

    public static final String CONTENT_AUTHORITY = "com.nex3z.shalarm.alarm";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_AlARM = "alarm";


    public static final class AlarmEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_AlARM).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_AlARM;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_AlARM;

        public static final String TABLE_NAME = "alarm";

        public static final String COLUMN_ENABLE = "alarm_enable";
        public static final String COLUMN_START = "alarm_start";
        public static final String COLUMN_REPEAT = "alarm_repeat";
        public static final String COLUMN_REPEAT_DAY = "alarm_repeat_day";
        public static final String COLUMN_RINGTONE = "alarm_ringtone";
        public static final String COLUMN_VIBRATE = "alarm_vibrate";
        public static final String COLUMN_SHAKE_POWER = "alarm_shake_power";
        public static final String COLUMN_LABEL = "alarm_label";

        public static Uri buildAlarmUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getAlarmIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }
}

