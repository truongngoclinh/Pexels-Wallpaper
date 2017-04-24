package dpanic.freestock.pexels.wallpaper.data;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import dpanic.freestock.pexels.wallpaper.data.model.Image;
import dpanic.freestock.pexels.wallpaper.data.model.Category;

/**
 * Created by dpanic on 9/29/2016.
 * Project: Pexels
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "pexels.db";
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
