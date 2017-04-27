package dpanic.freestock.pexels.wallpaper.application;

import android.app.Activity;
import android.app.Application;
import com.dpanic.wallz.pexels.BuildConfig;
import com.dpanic.wallz.pexels.R;
import dpanic.freestock.pexels.wallpaper.injection.component.AppComponent;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import io.branch.referral.Branch;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by dpanic on 10/6/2016.
 * Project: Pexels
 */

public class App extends Application {

    private AppComponent mAppComponent;

    public static App get(Activity activity) {
        return (App) activity.getApplication();
    }


    @Override
    public void onCreate() {
        super.onCreate();

        Branch.enablePlayStoreReferrer(7000L);
        Branch.getAutoInstance(this);

        initDependency();

        initCalligraphy();

        if (BuildConfig.DEBUG) {
            initStetho();

            initTimber();

//            initLeakCanary();
        }
    }

    private void initDependency() {
        mAppComponent = AppComponent.Initializer.init(this);
    }

    private void initTimber() {
        Timber.plant(new Timber.DebugTree() {
            @Override
            protected String createStackElementTag(StackTraceElement element) {
                return super.createStackElementTag(element) + " : " + element.getLineNumber();
            }
        });
    }

    private void initStetho() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this)).build());
    }

    @SuppressWarnings("unused")
    private boolean initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return true;
        }
        LeakCanary.install(this);
        return false;
    }

    private void initCalligraphy() {
        CalligraphyConfig.initDefault(
                new CalligraphyConfig.Builder().setDefaultFontPath("fonts/PT_Sans-Web-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath).build());
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
