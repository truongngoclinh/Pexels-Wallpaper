package dpanic.freestock.pexels.wallpaper.injection.module;

import android.content.Context;
import dpanic.freestock.pexels.wallpaper.ui.detail.ColorAdapter;
import dpanic.freestock.pexels.wallpaper.ui.detail.TagAdapter;

import org.greenrobot.eventbus.EventBus;

import dagger.Module;
import dagger.Provides;

/**
 * Created by dpanic on 3/6/2017.
 * Project: Pexels
 */

@Module
public class DetailModule {
    @Provides
    TagAdapter provideTagAdapter(Context context, EventBus eventBus) {
        return new TagAdapter(context, eventBus);
    }

    @Provides
    ColorAdapter provideColorAdapter(Context context, EventBus eventBus) {
        return new ColorAdapter(context, eventBus);
    }
}
