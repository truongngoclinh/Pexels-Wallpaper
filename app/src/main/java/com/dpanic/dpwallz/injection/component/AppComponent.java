package com.dpanic.dpwallz.injection.component;

import org.greenrobot.eventbus.EventBus;
import android.app.Application;
import com.dpanic.dpwallz.application.DPWallz;
import com.dpanic.dpwallz.injection.scope.ApplicationScope;
import com.dpanic.dpwallz.injection.module.AppModule;
import com.dpanic.dpwallz.injection.module.EventBusModule;
import com.dpanic.dpwallz.injection.module.FirebaseModule;
import com.dpanic.dpwallz.injection.module.LocalDBModule;
import com.dpanic.dpwallz.data.StorIODBManager;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import dagger.Component;

/**
 * Created by dpanic on 1/9/2017.
 * Project: DPWallz
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
        public static AppComponent init(DPWallz app) {
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
