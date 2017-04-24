package dpanic.freestock.pexels.wallpaper.data;

import java.util.List;
import dpanic.freestock.pexels.wallpaper.data.model.Category;
import dpanic.freestock.pexels.wallpaper.data.model.Image;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.operations.put.PutResults;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by dpanic on 3/7/2017.
 * Project: Pexels
 */

public class StorIODBManager {
    private StorIOSQLite mStorIOSQLite;

    public StorIODBManager(StorIOSQLite mStorIOSQLite) {
        this.mStorIOSQLite = mStorIOSQLite;
    }

    public Observable<PutResult> addImage(Image image) {
        return mStorIOSQLite
                .put()
                .object(image)
                .prepare()
                .asRxObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DeleteResult> deleteImage(final Image image) {
        return mStorIOSQLite
                .delete()
                .object(image)
                .prepare()
                .asRxObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<Image> getImage(Image image) {
        return mStorIOSQLite
                .get()
                .object(Image.class)
                .withQuery(Query.builder()
                                   .table(Image.TABLE_NAME)
                                   .where(Image.FIELD_PEXELS_ID + " = ?")
                                   .whereArgs(image.getPexelId())
                                   .build())
                .prepare()
                .asRxObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Image>> getFavoriteImageList() {
        return mStorIOSQLite
                .get()
                .listOfObjects(Image.class)
                .withQuery(
                        Query.builder()
                                .table(Image.TABLE_NAME)
                                .where(Image.FIELD_IS_FAVORITE + " = ?")
                                .whereArgs("1")
                                .orderBy(Image.FIELD_PKEY + " DESC")
                                .build())
                .prepare()
                .asRxObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Image>> getDownloadedImageList() {
        return mStorIOSQLite
                .get()
                .listOfObjects(Image.class)
                .withQuery(Query.builder()
                                   .table(Image.TABLE_NAME)
                                   .where(Image.FIELD_LOCAL_LINK + " != ?")
                                   .whereArgs(String.valueOf(""))
                                   .orderBy(Image.FIELD_PKEY + " DESC")
                                   .build())
                .prepare()
                .asRxObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /* Category functions */

    public Observable<PutResults<Category>> addCategories(List<Category> list) {
        return mStorIOSQLite
                .put()
                .objects(list)
                .prepare()
                .asRxObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Category>> getCategoryList() {
        return mStorIOSQLite
                .get()
                .listOfObjects(Category.class)
                .withQuery(Query.builder()
                                   .table(Category.TABLE_NAME)
                                   .build())
                .prepare()
                .asRxObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DeleteResult> deleteCategoryData() {
        return mStorIOSQLite
                .delete()
                .byQuery(DeleteQuery.builder()
                                 .table(Category.TABLE_NAME)
                                 .build())
                .prepare()
                .asRxObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
