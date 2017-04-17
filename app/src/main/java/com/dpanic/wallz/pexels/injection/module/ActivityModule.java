package com.dpanic.wallz.pexels.injection.module;

import android.content.Context;
import com.dpanic.wallz.pexels.injection.scope.PerActivityScope;
import dagger.Module;
import dagger.Provides;

/**
 * Created by dpanic on 3/6/2017.
 * Project: DPWallz
 */

@Module
public class ActivityModule {

    private final Context context;

    public ActivityModule(Context context) {
        this.context = context;
    }

    @Provides
    @PerActivityScope
    Context provideContext() {
        return context;
    }
}
