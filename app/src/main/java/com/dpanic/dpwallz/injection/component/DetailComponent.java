package com.dpanic.dpwallz.injection.component;

import android.content.Context;
import com.dpanic.dpwallz.ui.detail.DetailActivity;
import com.dpanic.dpwallz.injection.module.ActivityModule;
import com.dpanic.dpwallz.injection.module.AdModule;
import com.dpanic.dpwallz.injection.module.DetailModule;
import com.dpanic.dpwallz.injection.module.ImageHelperModule;
import com.dpanic.dpwallz.injection.scope.PerActivityScope;
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
