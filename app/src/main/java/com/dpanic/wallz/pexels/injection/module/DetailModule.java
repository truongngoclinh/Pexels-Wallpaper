package com.dpanic.wallz.pexels.injection.module;

import android.content.Context;
import com.dpanic.wallz.pexels.ui.detail.ColorAdapter;
import com.dpanic.wallz.pexels.ui.detail.TagAdapter;

import org.greenrobot.eventbus.EventBus;

import dagger.Module;
import dagger.Provides;

/**
 * Created by dpanic on 3/6/2017.
 * Project: DPWallz
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
