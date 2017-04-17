package com.dpanic.wallz.pexels.injection.component;

import android.content.Context;
import com.dpanic.wallz.pexels.injection.module.ActivityModule;
import com.dpanic.wallz.pexels.injection.module.ImageHelperModule;
import com.dpanic.wallz.pexels.injection.module.PreviewModule;
import com.dpanic.wallz.pexels.injection.scope.PerActivityScope;
import com.dpanic.wallz.pexels.ui.preview.PreviewActivity;
import dagger.Component;

/**
 * Created by dpanic on 3/3/2017.
 * Project: DPWallz
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
