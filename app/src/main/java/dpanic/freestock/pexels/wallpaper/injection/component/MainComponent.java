package dpanic.freestock.pexels.wallpaper.injection.component;

import android.content.Context;
import dpanic.freestock.pexels.wallpaper.injection.module.AdModule;
import dpanic.freestock.pexels.wallpaper.injection.scope.PerActivityScope;
import dpanic.freestock.pexels.wallpaper.ui.favorite.FavoriteFragment;
import dpanic.freestock.pexels.wallpaper.ui.search.SearchActivity;
import dpanic.freestock.pexels.wallpaper.injection.component.DaggerMainComponent;
import dpanic.freestock.pexels.wallpaper.ui.category.CategoryFragment;
import dpanic.freestock.pexels.wallpaper.ui.explore.ExploreFragment;
import dpanic.freestock.pexels.wallpaper.ui.imagelist.ImageListFragment;
import dpanic.freestock.pexels.wallpaper.ui.main.MainActivity;
import dpanic.freestock.pexels.wallpaper.injection.module.ActivityModule;
import dpanic.freestock.pexels.wallpaper.injection.module.MainModule;
import dagger.Component;

/**
 * Created by dpanic on 3/3/2017.
 * Project: Pexels
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
