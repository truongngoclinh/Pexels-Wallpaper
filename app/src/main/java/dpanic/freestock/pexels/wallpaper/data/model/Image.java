package dpanic.freestock.pexels.wallpaper.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import dpanic.freestock.pexels.wallpaper.data.DB;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import rx.functions.Func1;
import timber.log.Timber;

/**
 * Created by dpanic on 9/29/2016.
 * Project: Pexels
 */

@StorIOSQLiteType(table = Image.TABLE_NAME)
public class Image implements Parcelable {
    public static final String TABLE_NAME = "image";

    public static final String FIELD_PKEY = "_id";
    public static final String FIELD_PEXELS_ID = "pexels_id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_ORIGINAL_LINK = "original_link";
    public static final String FIELD_LARGE_LINK = "large_link";
    public static final String FIELD_DETAIL_LINK = "detail_link";
    public static final String FIELD_LOCAL_LINK = "local_link";
    public static final String FIELD_IS_FAVORITE = "is_favorite";

    public static final String CREATION_COMMAND =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + FIELD_PKEY + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + FIELD_PEXELS_ID + " TEXT NOT NULL, "
                    + FIELD_NAME + " TEXT NOT NULL, "
                    + FIELD_ORIGINAL_LINK + " TEXT NOT NULL, "
                    + FIELD_LARGE_LINK + " TEXT NOT NULL, "
                    + FIELD_DETAIL_LINK + " TEXT NOT NULL, "
                    + FIELD_LOCAL_LINK + " TEXT, "
                    + FIELD_IS_FAVORITE + " INTEGER NOT NULL"
                    + " )";

    public static final String CREATE_UNIQUE_INDEX_COMMAND =
            "CREATE UNIQUE INDEX idx_image_pexels_id ON " + Image.TABLE_NAME + " ("
                    + Image.FIELD_PEXELS_ID + ");";

    public static final Func1<Cursor, Image> MAPPER = new Func1<Cursor, Image>() {
        @Override
        public Image call(Cursor cursor) {
            return parseCursor(cursor);
        }
    };

    @StorIOSQLiteColumn(name = FIELD_PKEY, key = true)
    long id = -1;
    @StorIOSQLiteColumn(name = FIELD_PEXELS_ID)
    String pexelId;
    @StorIOSQLiteColumn(name = FIELD_NAME)
    String name;
    @StorIOSQLiteColumn(name = FIELD_ORIGINAL_LINK)
    String originalLink;
    @StorIOSQLiteColumn(name = FIELD_LARGE_LINK)
    String largeLink;
    @StorIOSQLiteColumn(name = FIELD_DETAIL_LINK)
    String detailLink;
    @StorIOSQLiteColumn(name = FIELD_LOCAL_LINK)
    String localLink;
    @StorIOSQLiteColumn(name = FIELD_IS_FAVORITE)
    boolean isFavorite;


    public Image() {
    }

    public Image(String pexelId, String name, String originalLink, String largeLink, String detailLink, String
            localLink, boolean
                         isFavorite) {
        setPexelId(pexelId);
        setName(name);
        setOriginalLink(originalLink);
        setLargeLink(largeLink);
        setDetailLink(detailLink);
        setLocalLink(localLink);
        setFavorite(isFavorite);
    }

    public Image(long id, String pexelId, String name, String originalLink, String largeLink, String detailLink,
                 String localLink,
                 boolean isFavorite) {
        setId(id);
        setPexelId(pexelId);
        setName(name);
        setOriginalLink(originalLink);
        setLargeLink(largeLink);
        setDetailLink(detailLink);
        setLocalLink(localLink);
        setFavorite(isFavorite);
    }

    private Image(Parcel in) {
        setId(in.readLong());
        setPexelId(in.readString());
        setName(in.readString());
        setOriginalLink(in.readString());
        setLargeLink(in.readString());
        setDetailLink(in.readString());
        setLocalLink(in.readString());
        setFavorite(in.readInt() == 1);
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPexelId() {
        return pexelId;
    }

    private void setPexelId(String pexelId) {
        this.pexelId = pexelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalLink() {
        return originalLink;
    }

    private void setOriginalLink(String originalLink) {
        this.originalLink = originalLink;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(getId());
        parcel.writeString(getPexelId());
        parcel.writeString(getName());
        parcel.writeString(getOriginalLink());
        parcel.writeString(getLargeLink());
        parcel.writeString(getDetailLink());
        parcel.writeString(getLocalLink());
        parcel.writeInt(isFavorite() ? 1 : 0);
    }

    public static Image parseCursor(Cursor cursor) {
        long id = DB.getLong(cursor, FIELD_PKEY);
        String pexelId = DB.getString(cursor, FIELD_PEXELS_ID);
        String name = DB.getString(cursor, FIELD_NAME);
        String originalLink = DB.getString(cursor, FIELD_ORIGINAL_LINK);
        String largeLink = DB.getString(cursor, FIELD_LARGE_LINK);
        String detailLink = DB.getString(cursor, FIELD_DETAIL_LINK);
        String localLink = DB.getString(cursor, FIELD_LOCAL_LINK);
        boolean isFavorite = DB.getBoolean(cursor, FIELD_IS_FAVORITE);

        return new Image(id, pexelId, name, originalLink, largeLink, detailLink, localLink, isFavorite);
    }

    public static ContentValues toContentValues(Image image) {
        ContentValues values = new ContentValues();

        values.put(FIELD_PEXELS_ID, image.getPexelId());
        values.put(FIELD_NAME, image.getName());
        values.put(FIELD_ORIGINAL_LINK, image.getOriginalLink());
        values.put(FIELD_LARGE_LINK, image.getLargeLink());
        values.put(FIELD_DETAIL_LINK, image.getDetailLink());
        values.put(FIELD_LOCAL_LINK, image.getLocalLink());
        values.put(FIELD_IS_FAVORITE, image.isFavorite() ? 1 : 0);

        return values;
    }

    public String getLargeLink() {
        return largeLink;
    }

    private void setLargeLink(String largeLink) {
        this.largeLink = largeLink;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getDetailLink() {
        return detailLink;
    }

    private void setDetailLink(String detailLink) {
        this.detailLink = detailLink;
    }

    public String getLocalLink() {
        return localLink;
    }

    public void setLocalLink(String localLink) {
        this.localLink = localLink;
    }

    public static void onUpgrade(SQLiteDatabase database, int updateVersion) {
        if (database == null) {
            return;
        }

        Timber.e("onUpgrade Image: version " + updateVersion);
        // Comment for later use
        switch (updateVersion) {
            case 3:
                database.execSQL(
                        "CREATE UNIQUE INDEX idx_image_pexels_id ON " + Image.TABLE_NAME + " ("
                                + Image.FIELD_PEXELS_ID + ");");
                break;
        default:
            break;
        }
    }
}
