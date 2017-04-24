package dpanic.freestock.pexels.wallpaper.injection.component;

import android.content.Context;
import dpanic.freestock.pexels.wallpaper.injection.module.AdModule;
import dpanic.freestock.pexels.wallpaper.injection.module.ImageHelperModule;
import dpanic.freestock.pexels.wallpaper.injection.scope.PerActivityScope;
import dpanic.freestock.pexels.wallpaper.injection.component.DaggerDetailComponent;
import dpanic.freestock.pexels.wallpaper.ui.detail.DetailActivity;
import dpanic.freestock.pexels.wallpaper.injection.module.ActivityModule;
import dpanic.freestock.pexels.wallpaper.injection.module.DetailModule;
import dagger.Component;

/**
 * Created by dpanic on 3/3/2017.
 * Project: Pexels
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
