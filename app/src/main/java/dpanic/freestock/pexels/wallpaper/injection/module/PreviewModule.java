package dpanic.freestock.pexels.wallpaper.injection.module;

import android.content.Context;
import dpanic.freestock.pexels.wallpaper.ui.preview.ScrollingHandler;
import dagger.Module;
import dagger.Provides;

/**
 * Created by dpanic on 3/6/2017.
 * Project: Pexels
 */
@Module
public class PreviewModule {

    @Provides
    ScrollingHandler provideScrollHandler(Context activity) {
        return new ScrollingHandler(activity);
    }
}
