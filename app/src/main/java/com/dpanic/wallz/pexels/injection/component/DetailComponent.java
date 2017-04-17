package com.dpanic.wallz.pexels.injection.component;

import android.content.Context;
import com.dpanic.wallz.pexels.ui.detail.DetailActivity;
import com.dpanic.wallz.pexels.injection.module.ActivityModule;
import com.dpanic.wallz.pexels.injection.module.AdModule;
import com.dpanic.wallz.pexels.injection.module.DetailModule;
import com.dpanic.wallz.pexels.injection.module.ImageHelperModule;
import com.dpanic.wallz.pexels.injection.scope.PerActivityScope;
import dagger.Component;

/**
 * Created by dpanic on 3/3/2017.
 * Project: DPWallz
 */

@Component(dependencies = AppComponent.class, modules = {
        DetailModule.class,
        ActivityModule.class,
        ImageHelperModule.class,
        AdModule.class
})
@PerActivityScope
public interface DetailComponent {

    final class Initializer {
        public static DetailComponent init(AppComponent appComponent, Context context) {
            return DaggerDetailComponent.builder()
                    .activityModule(new ActivityModule(context))
                    .appComponent(appComponent)
                    .build();
        }
    }

    void inject(DetailActivity activity);
}
