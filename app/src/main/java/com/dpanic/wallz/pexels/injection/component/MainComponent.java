package com.dpanic.wallz.pexels.injection.component;

import android.content.Context;
import com.dpanic.wallz.pexels.ui.category.CategoryFragment;
import com.dpanic.wallz.pexels.ui.explore.ExploreFragment;
import com.dpanic.wallz.pexels.ui.favorite.FavoriteFragment;
import com.dpanic.wallz.pexels.ui.imagelist.ImageListFragment;
import com.dpanic.wallz.pexels.ui.main.MainActivity;
import com.dpanic.wallz.pexels.injection.module.ActivityModule;
import com.dpanic.wallz.pexels.injection.module.AdModule;
import com.dpanic.wallz.pexels.injection.module.MainModule;
import com.dpanic.wallz.pexels.injection.scope.PerActivityScope;
import com.dpanic.wallz.pexels.ui.search.SearchActivity;
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
    void inject(SearchActivity activity);

    void inject(CategoryFragment baseFragment);
    void inject(ImageListFragment baseFragment);
    void inject(FavoriteFragment baseFragment);
    void inject(ExploreFragment baseFragment);
}
