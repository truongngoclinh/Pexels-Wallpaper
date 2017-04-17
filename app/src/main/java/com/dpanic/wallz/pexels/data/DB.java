package com.dpanic.wallz.pexels.data;

import android.database.Cursor;

/**
 * Created by dpanic on 9/29/2016.
 * Project: DPWallz
 */

public final class DB {
    @SuppressWarnings("unused")
    static final int BOOLEAN_FALSE = 0;
    static final int BOOLEAN_TRUE = 1;

    public static String getString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
    }

    public static boolean getBoolean(Cursor cursor, String columnName) {
        return getInt(cursor, columnName) == BOOLEAN_TRUE;
    }

    public static long getLong(Cursor cursor, String columnName) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(columnName));
    }

    private static int getInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndexOrThrow(columnName));
    }

    public DB() {
        throw new AssertionError("No instances.");
    }
}
