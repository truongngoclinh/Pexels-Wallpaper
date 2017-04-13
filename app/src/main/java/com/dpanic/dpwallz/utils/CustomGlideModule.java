package com.dpanic.dpwallz.utils;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.GlideModule;

import timber.log.Timber;

/**
 * Created by dpanic on 10/14/2016.
 * Project: DPWallz
 */

public class CustomGlideModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        Timber.d("applyOptions: ");
        MemorySizeCalculator calculator = new MemorySizeCalculator(context);
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();
        Timber.d("applyOptions: default Cache = " + defaultMemoryCacheSize);
        Timber.d("applyOptions: default pool = " + defaultBitmapPoolSize);

        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int customMemoryCacheSize = maxMemory/8;
        int customBitmapPoolSize = (int) (1.2 * maxMemory/8);

        Timber.d("applyOptions: custom Cache = " + customMemoryCacheSize);
        Timber.d("applyOptions: custom pool = " + customBitmapPoolSize);

        builder.setMemoryCache(new LruResourceCache(customMemoryCacheSize));
        builder.setBitmapPool(new LruBitmapPool(customBitmapPoolSize));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
