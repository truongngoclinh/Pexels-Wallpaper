package com.dpanic.dpwallz.injection.module;

import android.content.Context;
import com.dpanic.dpwallz.ui.detail.ColorAdapter;
import com.dpanic.dpwallz.ui.detail.TagAdapter;
import dagger.Module;
import dagger.Provides;

/**
 * Created by dpanic on 3/6/2017.
 * Project: DPWallz
 */

@Module
public class DetailModule {
    @Provides
    TagAdapter provideTagAdapter(Context context) {
        return new TagAdapter(context);
    }

    @Provides
    ColorAdapter provideColorAdapter(Context context) {
        return new ColorAdapter(context);
    }
}
