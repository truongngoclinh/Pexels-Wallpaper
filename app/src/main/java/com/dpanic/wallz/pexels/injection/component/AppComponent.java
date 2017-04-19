package com.dpanic.wallz.pexels.injection.component;

import org.greenrobot.eventbus.EventBus;
import android.app.Application;
import com.dpanic.wallz.pexels.application.App;
import com.dpanic.wallz.pexels.injection.scope.ApplicationScope;
import com.dpanic.wallz.pexels.injection.module.AppModule;
import com.dpanic.wallz.pexels.injection.module.EventBusModule;
import com.dpanic.wallz.pexels.injection.module.FirebaseModule;
import com.dpanic.wallz.pexels.injection.module.LocalDBModule;
import com.dpanic.wallz.pexels.data.StorIODBManager;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import dagger.Component;

/**
 * Created by dpanic on 1/9/2017.
 * Project: Pexels
 */

@ApplicationScope
@Component(modules = {
        AppModule.class,
        EventBusModule.class,
        FirebaseModule.class,
        LocalDBModule.class,
})
public interface AppComponent {

    final class Initializer{
        public static AppComponent init(App app) {
            return DaggerAppComponent.builder()
                    .appModule(new AppModule(app))
                    .build();
        }
    }

    FirebaseAnalytics firebaseAnalytics();
    FirebaseMessaging firebaseMessaging();
    EventBus eventBus();
    Application application();
    StorIODBManager storIODBManager();
}
