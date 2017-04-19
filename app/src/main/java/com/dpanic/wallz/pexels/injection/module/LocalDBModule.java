package com.dpanic.wallz.pexels.injection.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import com.dpanic.wallz.pexels.injection.scope.ApplicationScope;
import com.dpanic.wallz.pexels.data.DBHelper;
import com.dpanic.wallz.pexels.data.DataManager;
import com.dpanic.wallz.pexels.data.ImagePutResolver;
import com.dpanic.wallz.pexels.data.StorIODBManager;
import com.dpanic.wallz.pexels.data.model.Category;
import com.dpanic.wallz.pexels.data.model.CategorySQLiteTypeMapping;
import com.dpanic.wallz.pexels.data.model.Image;
import com.dpanic.wallz.pexels.data.model.ImageStorIOSQLiteDeleteResolver;
import com.dpanic.wallz.pexels.data.model.ImageStorIOSQLiteGetResolver;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import dagger.Module;
import dagger.Provides;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by dpanic on 2/27/2017.
 * Project: Pexels
 */

@Module
public class LocalDBModule {

    @Provides
    @ApplicationScope
    SQLiteOpenHelper provideDBHelper(Application context) {
        return new DBHelper(context);
    }

    @Provides
    @ApplicationScope
    SqlBrite provideSqlBrite() {
        return new SqlBrite.Builder().logger(new SqlBrite.Logger() {
            @Override
            public void log(String message) {
                Timber.tag("Database").v(message);
            }
        }).build();
    }

    @Provides
    @ApplicationScope
    BriteDatabase provideDatabase(SqlBrite sqlBrite, SQLiteOpenHelper dbHelper) {
        BriteDatabase db = sqlBrite.wrapDatabaseHelper(dbHelper, Schedulers.io());
        db.setLoggingEnabled(true);
        return db;
    }

    @Provides
    @ApplicationScope
    DataManager provideDataManager(BriteDatabase database) {
        return new DataManager(database);
    }

    @Provides
    @ApplicationScope
    SharedPreferences provideSharedPreferences(Application context) {
        return context.getSharedPreferences("pexels_pref", Context.MODE_PRIVATE);
    }

    @Provides
    @ApplicationScope
    StorIOSQLite provideStorIOSQLite(SQLiteOpenHelper sqLiteOpenHelper) {
        return DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .addTypeMapping(Image.class, SQLiteTypeMapping.<Image>builder()
                        .putResolver(new ImagePutResolver())
                        .getResolver(new ImageStorIOSQLiteGetResolver())
                        .deleteResolver(new ImageStorIOSQLiteDeleteResolver())
                        .build())
                .addTypeMapping(Category.class, new CategorySQLiteTypeMapping())
                .build();
    }

    @Provides
    @ApplicationScope
    StorIODBManager provideStorIODBManager(StorIOSQLite storIOSQLite) {
        return new StorIODBManager(storIOSQLite);
    }
}
