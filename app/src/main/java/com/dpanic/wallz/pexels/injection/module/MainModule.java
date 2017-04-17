package com.dpanic.wallz.pexels.injection.module;

import android.content.Context;
import com.dpanic.wallz.pexels.ui.category.CategoryAdapter;
import com.dpanic.wallz.pexels.ui.common.ImageAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by dpanic on 3/6/2017.
 * Project: DPWallz
 */

@Module
public class MainModule {
    @Provides
    ImageAdapter provideImageAdapter(Context context, AdRequest adRequest, NativeExpressAdView adView) {
        return new ImageAdapter(context, adRequest, adView);
    }

    @Provides
    CategoryAdapter provideCategoryAdapter(Context context) {
        return new CategoryAdapter(context);
    }
}
