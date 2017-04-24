package dpanic.freestock.pexels.wallpaper.injection.component;

import org.greenrobot.eventbus.EventBus;
import android.app.Application;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import dagger.Component;
import dpanic.freestock.pexels.wallpaper.application.App;
import dpanic.freestock.pexels.wallpaper.data.StorIODBManager;
import dpanic.freestock.pexels.wallpaper.injection.module.AppModule;
import dpanic.freestock.pexels.wallpaper.injection.module.EventBusModule;
import dpanic.freestock.pexels.wallpaper.injection.module.FirebaseModule;
import dpanic.freestock.pexels.wallpaper.injection.module.LocalDBModule;
import dpanic.freestock.pexels.wallpaper.injection.scope.ApplicationScope;

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
