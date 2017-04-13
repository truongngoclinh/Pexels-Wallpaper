package com.dpanic.dpwallz.injection.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import com.dpanic.dpwallz.injection.scope.ApplicationScope;
import com.dpanic.dpwallz.data.DBHelper;
import com.dpanic.dpwallz.data.DataManager;
import com.dpanic.dpwallz.data.ImagePutResolver;
import com.dpanic.dpwallz.data.StorIODBManager;
import com.dpanic.dpwallz.data.model.Category;
import com.dpanic.dpwallz.data.model.CategorySQLiteTypeMapping;
import com.dpanic.dpwallz.data.model.Image;
import com.dpanic.dpwallz.data.model.ImageStorIOSQLiteDeleteResolver;
import com.dpanic.dpwallz.data.model.ImageStorIOSQLiteGetResolver;
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
 * Project: DPWallz
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
        return context.getSharedPreferences("dpwallz_pref", Context.MODE_PRIVATE);
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
