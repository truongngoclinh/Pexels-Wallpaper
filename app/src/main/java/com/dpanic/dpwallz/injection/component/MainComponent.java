package com.dpanic.dpwallz.injection.component;

import android.content.Context;
import com.dpanic.dpwallz.ui.category.CategoryFragment;
import com.dpanic.dpwallz.ui.explore.ExploreFragment;
import com.dpanic.dpwallz.ui.favorite.FavoriteFragment;
import com.dpanic.dpwallz.ui.imagelist.ImageListFragment;
import com.dpanic.dpwallz.ui.main.MainActivity;
import com.dpanic.dpwallz.injection.module.ActivityModule;
import com.dpanic.dpwallz.injection.module.AdModule;
import com.dpanic.dpwallz.injection.module.MainModule;
import com.dpanic.dpwallz.injection.scope.PerActivityScope;
import dagger.Component;

/**
 * Created by dpanic on 3/3/2017.
 * Project: DPWallz
 */

@Component(dependencies = AppComponent.class,
        modules = {
                MainModule.class,
                ActivityModule.class,
                AdModule.class
        })
@PerActivityScope
public interface MainComponent {

    final class Initializer {
        public static MainComponent init(AppComponent appComponent, Context context) {
            return DaggerMainComponent.builder()
                    .activityModule(new ActivityModule(context))
                    .appComponent(appComponent)
                    .build();
        }
    }

    void inject(MainActivity activity);

    void inject(CategoryFragment baseFragment);
    void inject(ImageListFragment baseFragment);
    void inject(FavoriteFragment baseFragment);
    void inject(ExploreFragment baseFragment);
}
