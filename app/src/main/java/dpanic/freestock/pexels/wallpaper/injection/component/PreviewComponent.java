package dpanic.freestock.pexels.wallpaper.injection.component;

import android.content.Context;
import dpanic.freestock.pexels.wallpaper.injection.module.ImageHelperModule;
import dpanic.freestock.pexels.wallpaper.injection.scope.PerActivityScope;
import dpanic.freestock.pexels.wallpaper.injection.component.DaggerPreviewComponent;
import dpanic.freestock.pexels.wallpaper.injection.module.ActivityModule;
import dpanic.freestock.pexels.wallpaper.injection.module.PreviewModule;
import dpanic.freestock.pexels.wallpaper.ui.preview.PreviewActivity;
import dagger.Component;

/**
 * Created by dpanic on 3/3/2017.
 * Project: Pexels
 */

@Component(dependencies = AppComponent.class, modules = {
        ActivityModule.class,
        ImageHelperModule.class,
        PreviewModule.class
})
@PerActivityScope
public interface PreviewComponent {

    final class Initializer {
        public static PreviewComponent init(AppComponent appComponent, Context context) {
            return DaggerPreviewComponent.builder()
                    .activityModule(new ActivityModule(context))
                    .appComponent(appComponent)
                    .build();
        }
    }

    void inject(PreviewActivity activity);
}
