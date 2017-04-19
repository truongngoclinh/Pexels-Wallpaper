package com.dpanic.wallz.pexels.data;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import com.dpanic.wallz.pexels.data.model.Image;
import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;

/**
 * Created by dpanic on 3/10/2017.
 * Project: Pexels
 */

public class ImagePutResolver extends DefaultPutResolver<Image> {
    @Override
    @NonNull
    public InsertQuery mapToInsertQuery(@NonNull Image object) {
        return InsertQuery.builder()
                .table("image")
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public UpdateQuery mapToUpdateQuery(@NonNull Image object) {
        return UpdateQuery.builder()
                .table("image")
                .where("pexels_id = ?")
                .whereArgs(object.getPexelId())
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public ContentValues mapToContentValues(@NonNull Image object) {
        ContentValues contentValues = new ContentValues(8);

        if (object.getId() != -1){
            contentValues.put("_id", object.getId());
        }
        contentValues.put("detail_link", object.getDetailLink());
        contentValues.put("is_favorite", object.isFavorite());
        contentValues.put("pexels_id", object.getPexelId());
        contentValues.put("original_link", object.getOriginalLink());
        contentValues.put("large_link", object.getLargeLink());
        contentValues.put("name", object.getName());
        contentValues.put("local_link", object.getLocalLink());

        return contentValues;
    }
}
