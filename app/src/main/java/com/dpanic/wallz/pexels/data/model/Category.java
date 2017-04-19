package com.dpanic.wallz.pexels.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import com.dpanic.wallz.pexels.data.DB;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import rx.functions.Func1;
import timber.log.Timber;

/**
 * Created by dpanic on 9/29/2016.
 * Project: Pexels
 */

@StorIOSQLiteType(table = Category.TABLE_NAME)
public class Category implements Parcelable {

    public static final String TABLE_NAME = "category";

    private static final String FIELD_PKEY = "_id";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_LINK = "link";
    private static final String FIELD_THUMB_LINK = "thumbLink";

    public static final String CREATION_COMMAND =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    FIELD_PKEY + " INTEGER NOT NULL PRIMARY KEY, " +
                    FIELD_NAME + " TEXT NOT NULL, " +
                    FIELD_LINK + " TEXT NOT NULL, " +
                    FIELD_THUMB_LINK + " TEXT NOT NULL )";
    public static final Func1<Cursor, Category> MAPPER = new Func1<Cursor, Category>() {
        @Override
        public Category call(Cursor cursor) {
            return parseCursor(cursor);
        }
    };

    @StorIOSQLiteColumn(name = FIELD_PKEY, key = true)
    Long id; // need to be Long instead of long - required by StorIO
    @StorIOSQLiteColumn(name = FIELD_NAME)
    String name;
    @StorIOSQLiteColumn(name = FIELD_LINK)
    String link;
    @StorIOSQLiteColumn(name = FIELD_THUMB_LINK)
    String thumbLink;

    public Category() {
    }

    public Category(String name, String link, String thumbLink) {
        setName(name);
        setLink(link);
        setThumbLink(thumbLink);
    }

    public Category(Long id, String name, String link, String thumbLink) {
        setId(id);
        setName(name);
        setLink(link);
        setThumbLink(thumbLink);
    }

    protected Category(Parcel in) {
        setId(in.readLong());
        setName(in.readString());
        setLink(in.readString());
        setThumbLink(in.readString());
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    private void setLink(String link) {
        this.link = link;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(getId() == null ? 0 : getId());
        parcel.writeString(getName());
        parcel.writeString(getLink());
        parcel.writeString(getThumbLink());
    }

    private static Category parseCursor(Cursor cursor) {
        Long id = DB.getLong(cursor, FIELD_PKEY);
        String name = DB.getString(cursor, FIELD_NAME);
        String link = DB.getString(cursor, FIELD_LINK);
        String thumbLink = DB.getString(cursor, FIELD_THUMB_LINK);
        return new Category(id, name, link, thumbLink);
    }

    public static ContentValues toContentValues(Category category) {
        ContentValues values = new ContentValues();
        values.put(FIELD_NAME, category.getName());
        values.put(FIELD_LINK, category.getLink());
        values.put(FIELD_THUMB_LINK, category.getThumbLink());
        return values;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Category) {
            Category compareObj = (Category) obj;
            return compareObj.getName().equals(this.name);
        } else {
            return super.equals(obj);
        }
    }

    public static void onUpgrade(SQLiteDatabase database, int updateVersion) {
        if (database == null) {
            return;
        }

        // Comment for later use
        Timber.e("onUpgrade Category: version " + updateVersion);
        switch (updateVersion) {
            case 2:
                database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
                database.execSQL(CREATION_COMMAND);
                break;
            default:
                break;
        }
    }

    public String getThumbLink() {
        return thumbLink;
    }

    private void setThumbLink(String thumbLink) {
        this.thumbLink = thumbLink;
    }
}
