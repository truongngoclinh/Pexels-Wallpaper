package com.dpanic.wallz.pexels.injection.module;

import android.content.Context;
import com.dpanic.wallz.pexels.ui.preview.ScrollingHandler;
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
