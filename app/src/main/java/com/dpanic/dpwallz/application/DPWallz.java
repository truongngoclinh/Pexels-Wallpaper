package com.dpanic.dpwallz.application;

import android.app.Activity;
import android.app.Application;
import com.dpanic.dpwallz.BuildConfig;
import com.dpanic.dpwallz.injection.component.AppComponent;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import timber.log.Timber;
//import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

//import io.realm.Realm;
//import io.realm.RealmConfiguration;

/**
 * Created by dpanic on 10/6/2016.
 * Project: DPWallz
 */

public class DPWallz extends Application {

    private AppComponent mAppComponent;

    public static DPWallz get(Activity activity) {
        return (DPWallz) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initDependency();

        if (BuildConfig.DEBUG) {
            initStetho();

            initTimber();

            initLeakCanary();
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

    private boolean initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return true;
        }
        LeakCanary.install(this);
        return false;
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
