package com.dpanic.dpwallz.data;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.dpanic.dpwallz.data.model.Category;
import com.dpanic.dpwallz.data.model.Image;

/**
 * Created by dpanic on 9/29/2016.
 * Project: DPWallz
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 3;
    private static final String DB_NAME = "dpwallz.db";
//    private static DBHelper dbHelper;

//    public static DBHelper getInstance(Context context) {
//        if (dbHelper == null) {
//            dbHelper = new DBHelper(context.getApplicationContext());
//        }
//
//        return dbHelper;
//    }

    public DBHelper(Application context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Image.CREATION_COMMAND);
        db.execSQL(Image.CREATE_UNIQUE_INDEX_COMMAND);
        db.execSQL(Category.CREATION_COMMAND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // db is out of date; update db according to corresponding version

        if (oldVersion < newVersion) {
            while (oldVersion < newVersion) {
                int updateVersion = ++oldVersion;
                Category.onUpgrade(db, updateVersion);
                Image.onUpgrade(db, updateVersion);
            }
        }
    }
}
