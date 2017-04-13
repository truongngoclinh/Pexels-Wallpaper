package com.dpanic.dpwallz.injection.module;

import android.app.Application;
import com.dpanic.dpwallz.injection.scope.ApplicationScope;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import dagger.Module;
import dagger.Provides;

/**
 * Created by dpanic on 1/9/2017.
 * Project: DPWallz
 */

@Module
public class FirebaseModule {

    @Provides
    @ApplicationScope
    FirebaseAnalytics provideAnalytics(Application application) {
        return FirebaseAnalytics.getInstance(application);
    }

    @Provides
    @ApplicationScope
    FirebaseMessaging provideMessaging() {
        return FirebaseMessaging.getInstance();
    }
}
