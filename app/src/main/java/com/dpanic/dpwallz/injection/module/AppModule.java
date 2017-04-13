package com.dpanic.dpwallz.injection.module;

import android.app.Application;
import com.dpanic.dpwallz.injection.scope.ApplicationScope;
import dagger.Module;
import dagger.Provides;

/**
 * Created by dpanic on 1/9/2017.
 * Project: DPWallz
 */

@Module
public class AppModule {
    private final Application mApplication;

    public AppModule(Application application) {
        this.mApplication = application;
    }

    @Provides
    @ApplicationScope
    Application provideContext() {
        return mApplication;
    }
}
