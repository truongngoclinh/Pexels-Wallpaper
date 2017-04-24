package dpanic.freestock.pexels.wallpaper.data;

import java.util.List;
import android.database.Cursor;
import dpanic.freestock.pexels.wallpaper.data.model.Category;
import dpanic.freestock.pexels.wallpaper.data.model.Image;
import com.squareup.sqlbrite.BriteDatabase;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by dpanic on 9/29/2016.
 * Project: Pexels
 */

@SuppressWarnings("unused")
public class DataManager {
    private BriteDatabase mDatabase;

    public DataManager(BriteDatabase database) {
        this.mDatabase = database;
    }

    /* Image function */
    @SuppressWarnings("unused")
    private Observable<Integer> addImage(final Image image) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    subscriber.onNext((int) mDatabase.insert(Image.TABLE_NAME, Image.toContentValues(image)));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });

    }

    @SuppressWarnings("unused")
    public Observable<Integer> addImages(final List<Image> imageList) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    int count = 0;
                    for (Image image : imageList) {
                        if (mDatabase.insert(Image.TABLE_NAME, Image.toContentValues(image)) >= 0) {
//                            subscriber.onNext(image);
                            count++;
                        }
                    }
                    subscriber.onNext(count);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Integer> deleteImage(final Image image) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
//                    int result = mDatabase.delete(Image.TABLE_NAME, Image.FIELD_PEXELS_ID + " = ?", image.getPexelId());
//                    if (result > 0) {
                    subscriber.onNext(mDatabase.delete(Image.TABLE_NAME, Image.FIELD_PEXELS_ID + " = ?",
                                                       image.getPexelId()));
                    //                    }
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<List<Image>> getFavoriteImageList() {
        String query = "SELECT * FROM "
                + Image.TABLE_NAME
                + " WHERE "
                + Image.FIELD_IS_FAVORITE
                + " = ? ORDER BY " + Image.FIELD_PKEY + " DESC";
        return mDatabase.createQuery(Image.TABLE_NAME, query, String.valueOf(1)).mapToList(Image.MAPPER);
    }

    public Observable<List<Image>> getHistoryImageList() {
        String query = "SELECT * FROM "
                + Image.TABLE_NAME
                + " WHERE "
                + Image.FIELD_LOCAL_LINK
                + " != ? ORDER BY " + Image.FIELD_PKEY + " DESC";
        return mDatabase.createQuery(Image.TABLE_NAME, query, String.valueOf("")).mapToList(Image.MAPPER);
    }

    @SuppressWarnings("unused")
    public Observable<List<Image>> getAllImages() {
        String query = "SELECT * FROM " + Image.TABLE_NAME;
        return mDatabase.createQuery(Image.TABLE_NAME, query, String.valueOf("")).mapToList(Image.MAPPER);
    }

    public Observable<Image> getImage(final Image image) {
        final String query = "SELECT * FROM " + Image.TABLE_NAME + " WHERE " + Image.FIELD_PEXELS_ID + " = ? ";

        return mDatabase.createQuery(Image.TABLE_NAME, query, image.getPexelId()).mapToList(Image.MAPPER)
                .map(new Func1<List<Image>, Image>() {
                    @Override
                    public Image call(List<Image> images) {
                        if (images.size() > 0) {
                            return images.get(0);
                        } else {
                            return null;
                        }
                    }
                });
    }

    @SuppressWarnings("unused")
    public Observable<Boolean> isFavoriteImage(final Image image) {
        final String query = "SELECT * FROM "
            + Image.TABLE_NAME
            + " WHERE "
            + Image.FIELD_PEXELS_ID
            + " = ? AND " + Image.FIELD_IS_FAVORITE + " = " + DB.BOOLEAN_TRUE;
        return Observable.create(new Observable.OnSubscribe<Boolean>() {

            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    Cursor cursor = mDatabase.query(query, image.getPexelId());
                    Image img = null;
                    if (cursor != null && cursor.moveToFirst()) {
                        img = Image.parseCursor(cursor);
                        cursor.close();
                    }
                    subscriber.onNext(img != null);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    /* Category function */
    public Observable<List<Category>> getCategoryList() {
        String query = "SELECT * FROM "
                + Category.TABLE_NAME;
        return mDatabase.createQuery(Category.TABLE_NAME, query).mapToList(Category.MAPPER);
    }

    private Observable<Integer> deleteCategoryData() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    subscriber.onNext(mDatabase.delete(Category.TABLE_NAME, null, (String) null));
                    Timber.e("deleteCategoryData: clear categories data");
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    @SuppressWarnings("unused")
    public Observable<Integer> clearThenInsertCategories(final List<Category> categories) {
        return deleteCategoryData().map(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer integer) {
                int count = 0;
                for (Category category : categories) {
                    if (mDatabase.insert(Category.TABLE_NAME, Category.toContentValues(category)) > 0) {
                        count++;
                    }
                }
                return count;
            }
        });
    }

    public Observable<Integer> addCategories(final List<Category> list) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    int count = 0;
                    for (Category category : list) {
                        if (mDatabase.insert(Category.TABLE_NAME, Category.toContentValues(category)) > 0) {
                            count++;
                        }
                    }

                    subscriber.onNext(count);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    public void forceToAddImage(final Image image) {
        Timber.e("thanh.dao", "forceToAddImage: ");
        final String query = "REPLACE INTO "
                + Image.TABLE_NAME
                + "(" + Image.FIELD_PEXELS_ID
                + "," + Image.FIELD_NAME
                + "," + Image.FIELD_ORIGINAL_LINK
                + "," + Image.FIELD_LARGE_LINK
                + "," + Image.FIELD_DETAIL_LINK
                + "," + Image.FIELD_LOCAL_LINK
                + "," + Image.FIELD_IS_FAVORITE
                + ")"
                + " VALUES (?, ?, ?, ?, ?, ?, ?)";
        String[] stringArgs =
                {image.getPexelId(), image.getName(), image.getOriginalLink(), image.getLargeLink(),
                        image.getDetailLink(), image.getLocalLink(),
                        image.isFavorite() ? String.valueOf(DB.BOOLEAN_TRUE) : String.valueOf(
                                DB.BOOLEAN_FALSE)};

        mDatabase.execute(query, (Object) stringArgs);

    }
}
