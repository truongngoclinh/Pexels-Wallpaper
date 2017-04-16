package com.dpanic.dpwallz.injection.module;

import android.content.Context;
import com.dpanic.dpwallz.ui.detail.ColorAdapter;
import com.dpanic.dpwallz.ui.detail.TagAdapter;

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
