package com.dpanic.dpwallz.injection.module;

import android.content.Context;
import com.dpanic.dpwallz.ui.preview.ScrollingHandler;
import dagger.Module;
import dagger.Provides;

/**
 * Created by dpanic on 3/6/2017.
 * Project: DPWallz
 */
@Module
public class PreviewModule {

    @Provides
    ScrollingHandler provideScrollHandler(Context activity) {
        return new ScrollingHandler(activity);
    }
}
